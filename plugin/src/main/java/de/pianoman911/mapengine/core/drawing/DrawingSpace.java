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
    public void line(int x1, int y1, int x2, int y2, int thickness, int color) {
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;
        while (true) {
            rect(x1, y1, thickness, thickness, color);
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
        for (int posY = 0; posY < height; posY++) {
            for (int posX = 0; posX < width; posX++) {
                if (posX < thickness || posX >= width - thickness ||
                        posY < thickness || posY >= height - thickness) {
                    pixel(x + posX, y + posY, color);
                }
            }
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
        for (int posY = -radius; posY < radius; posY++) {
            for (int posX = -radius; posX < radius; posX++) {
                if (posX * posX + posY * posY < radius * radius &&
                        posX * posX + posY * posY > (radius - thickness) * (radius - thickness)) {
                    pixel(x + posX, y + posY, color);
                }
            }
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
    public void triangle(int x1, int y1, int x2, int y2, int x3, int y3, int color) {
        for (int posY = 0; posY < buffer.height(); posY++) {
            for (int posX = 0; posX < buffer.width(); posX++) {
                int as_x = posX - x1;
                int as_y = posY - y1;
                boolean side = (x2 - x1) * as_y - (y2 - y1) * as_x > 0;
                if ((x3 - x1) * as_y - (y3 - y1) * as_x > 0 == side) {
                    continue;
                }
                if ((x3 - x2) * (posY - y2) - (y3 - y2) * (posX - x2) > 0 != side) {
                    continue;
                }
                pixel(posX, posY, color);
            }
        }
    }

    @Override
    public void triangle(int x1, int y1, int x2, int y2, int x3, int y3, int thickness, int color) {
        line(x1, y1, x2, y2, thickness, color);
        line(x2, y2, x3, y3, thickness, color);
        line(x3, y3, x1, y1, thickness, color);
    }

    @Override
    public void polygon(int[] x, int[] y, int thickness, int color) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("x and y must have the same length");
        }
        if (x.length < 3) {
            throw new IllegalArgumentException("x and y must have at least 3 points");
        }
        if (x.length == 3) { // faster triangle
            triangle(x[0], y[0], x[1], y[1], x[2], y[2], thickness, color);
            return;
        }
        if (x.length == 4) { // faster rectangle
            rect(x[0], y[0], x[2] - x[0], y[2] - y[0], thickness, color);
            return;
        }
        for (int i = 0; i < x.length; i++) {
            line(x[i], y[i], x[(i + 1) % x.length], y[(i + 1) % x.length], thickness, color);
        }
    }

    @Override
    public void polygon(int[] x, int[] y, int color) {
        if (x.length != y.length) {
            throw new IllegalArgumentException("x and y must have the same length");
        }
        if (x.length < 3) {
            throw new IllegalArgumentException("x and y must have at least 3 points");
        }
        if (x.length == 3) { // faster triangle
            triangle(x[0], y[0], x[1], y[1], x[2], y[2], color);
            return;
        }
        if (x.length == 4) { // faster rectangle
            rect(x[0], y[0], x[2] - x[0], y[2] - y[0], color);
            return;
        }
        for (int posY = 0; posY < buffer.height(); posY++) {
            for (int posX = 0; posX < buffer.width(); posX++) {
                boolean inside = false;
                for (int i = 0, j = x.length - 1; i < x.length; j = i++) {
                    if (((y[i] > posY) != (y[j] > posY)) &&
                            (posX < (x[j] - x[i]) * (posY - y[i]) / (y[j] - y[i]) + x[i])) {
                        inside = !inside;
                    }
                }
                if (inside) {
                    pixel(posX, posY, color);
                }
            }
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
