package de.pianoman911.mapengine.core.drawing;

import de.pianoman911.mapengine.api.drawing.IDrawingSpace;
import de.pianoman911.mapengine.api.pipeline.IPipelineContext;
import de.pianoman911.mapengine.api.util.Alignment;
import de.pianoman911.mapengine.api.util.FontRegistry;
import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;
import de.pianoman911.mapengine.api.util.ImageUtils;
import de.pianoman911.mapengine.core.pipeline.PipelineContext;
import de.pianoman911.mapengine.core.util.ComponentUtil;
import it.unimi.dsi.fastutil.Pair;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TranslatableComponent;
import net.kyori.adventure.text.format.TextColor;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public record DrawingSpace(FullSpacedColorBuffer buffer, PipelineContext context) implements IDrawingSpace {

    public static final int ALPHA = 0x00000000;

    @Override
    public IPipelineContext ctx() {
        return context;
    }

    @Override
    public void clear() {
        Arrays.fill(buffer.buffer(), ALPHA);
    }

    @Override
    public void clear(int x, int y, int width, int height) {
        for (int i = 0; i < height; i++) {
            Arrays.fill(buffer.buffer(), x + (y + i) * buffer.width(), x + (y + i) * buffer.width() + width, ALPHA);
        }
    }

    @Override
    public void pixel(int x, int y, int color) {
        buffer.pixel(x, y, color);
    }

    @Override
    public void pixels(int[] pixels, int x, int y, int width, int height) {
        buffer.pixels(pixels, x, y, width, height);
    }

    @Override
    public void buffer(FullSpacedColorBuffer buffer, int x, int y) {
        this.buffer.buffer(buffer, x, y);
    }

    @Override
    public void line(int x1, int y1, int x2, int y2, int color) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;
        while (true) {
            pixel(x1, y1, color);
            if (x1 == x2 && y1 == y2) {
                break;
            }

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }

    @Override
    public void rect(int x, int y, int width, int height, int thickness, int color) {
        for (int t = 0; t < thickness; t++) {
            rect(x + t, y + t, width, height, color);
        }
    }

    @Override
    public void rect(int x, int y, int width, int height, int color) {
        for (int posY = 0; posY < height; posY++) {
            for (int posX = 0; posX < width; posX++) {
                pixel(x + posX, y + posY, color);
            }
        }
    }

    @Override
    public void circle(int x, int y, int radius, int thickness, int color) {
        for (int t = 0; t < thickness; t++) {
            circle(x + t, y + t, radius, color);
        }
    }

    @Override
    public void circle(int x, int y, int radius, int color) {
        for (int posY = -radius; posY < radius; posY++) {
            for (int posX = -radius; posX < radius; posX++) {
                if (posX * posX + posY * posY < radius * radius) {
                    pixel(x + posX, y + posY, color);
                }
            }
        }
    }

    @Override
    public void triangle(int x1, int y1, int x2, int y2, int x3, int y3, int thickness, int color) {
        for (int t = 0; t < thickness; t++) {
            triangle(x1 + t, y1 + t, x2 + t, y2 + t, x3 + t, y3 + t, color);
        }
    }

    @Override
    public void triangle(int x1, int y1, int x2, int y2, int x3, int y3, int color) {
        line(x1, y1, x2, y2, color);
        line(x2, y2, x3, y3, color);
        line(x3, y3, x1, y1, color);
    }

    @Override
    public void polygon(int[] x, int[] y, int thickness, int color) {
        for (int t = 0; t < thickness; t++) {
            polygon(x, y, color);
        }
    }

    @Override
    public void polygon(int[] x, int[] y, int color) {
        // Draw polygon
        for (int i = 0; i < x.length; i++) {
            line(x[i], y[i], x[(i + 1) % x.length], y[(i + 1) % x.length], color);
        }
    }

    @Override
    public void text(String text, Font font, int x, int y, int color) {
        buffer(FontRegistry.convertText2Bytes(text, font, new Color(color)), x, y);
    }

    @Override
    public void component(Component component, Font font, int x, int y) {
        this.component(component, font, x, y, Alignment.START, Alignment.CENTER);
    }

    @Override
    public void component(Component component, Font font, int x, int y, Alignment alignmentX, Alignment alignmentY) {
        this.component(component, font, x, y, alignmentX, alignmentY, true);
    }

    @Override
    public void component(Component component, Font font, int x, int y, Alignment alignmentX, Alignment alignmentY, boolean antiAliasing) {
        this.component(component, font, x, y, alignmentX, alignmentY, antiAliasing, 1.2f);
    }

    @Override
    public void component(Component component, Font font, int x, int y, Alignment alignmentX, Alignment alignmentY, boolean antiAliasing, float lineHeight) {
        int width = 0;

        List<FullSpacedColorBuffer> buffers = new ArrayList<>();
        for (Component child : ComponentUtil.inlineComponent(component)) {
            String content;
            if (child instanceof TextComponent) {
                content = ((TextComponent) child).content();
            } else if (child instanceof TranslatableComponent) {
                content = ((TranslatableComponent) child).key();
            } else {
                content = child.getClass().getSimpleName();
            }

            TextColor componentColor = child.color();
            Color color = componentColor == null ? Color.WHITE :
                    new Color(componentColor.value());

            FullSpacedColorBuffer childBuf = null;

            String[] parts = content.split("\n");
            for (String part : parts) {
                if (childBuf != null) {
                    width = Math.max(width, childBuf.width());
                }
                childBuf = FontRegistry.convertText2Bytes(part,
                        font, color, antiAliasing);

                buffers.add(childBuf);
            }
            if (childBuf != null) {
                width = Math.max(width, childBuf.width());
            }
        }

        int offsetX = alignmentX.getOffset(width);
        int offsetY = alignmentY.getOffset((int) (font.getSize() * lineHeight * buffers.size()));
        for (FullSpacedColorBuffer renderedLine : buffers) {
            int lineOffsetX = (width - renderedLine.width()) / 2;
            buffer.buffer(renderedLine, x + offsetX + lineOffsetX, y + offsetY);
            y += (int) (font.getSize() * lineHeight);
        }
    }

    @Override
    public void image(BufferedImage image, int x, int y) {
        this.buffer.pixels(ImageUtils.rgb(image), x, y, image.getWidth(), image.getHeight());
    }

    @Override
    public Pair<FullSpacedColorBuffer, IPipelineContext> combined() {
        return Pair.of(buffer, context);
    }
}
