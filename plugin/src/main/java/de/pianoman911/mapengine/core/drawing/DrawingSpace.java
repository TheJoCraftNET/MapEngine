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
        int xMax = x + width;
        int yMax = y + height;
        rect(x, y, xMax - x, thickness, color);
        rect(x, yMax - thickness, xMax - x, thickness, color);
        rect(x, y + thickness, thickness, height - 2 * thickness, color);
        rect(xMax - thickness, y + thickness, thickness, height - 2 * thickness, color);
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
    public void circle(int x, int y, int radius, int color) {
        this.ellipse(x, y, radius, radius, color);
    }

    @Override
    public void circle(int x, int y, int radius, int thickness, int color) {
        this.ellipse(x, y, radius, radius, thickness, color);
    }

    @Override
    public void ellipse(int x, int y, int radiusX, int radiusY, int color) {
        if (((color >> 24) & 0xFF) == 255) {
            fastEllipse(x, y, radiusX, radiusY, color);
            return;
        }
        long radiusX2 = (long) radiusX * radiusX;
        long radiusY2 = (long) radiusY * radiusY;
        long radiusX2Y2 = radiusX2 * radiusY2;
        for (int posY = -radiusY; posY < radiusY; posY++) {
            long posY2RadiusX2 = (long) posY * posY * radiusX2;
            for (int posX = -radiusX; posX < radiusX; posX++) {
                long posX2 = (long) posX * posX;
                if (posX2 * radiusY2 + posY2RadiusX2 < radiusX2Y2) {
                    pixel(x + posX, y + posY, color);
                }
            }
        }
    }

    // Only works for colors with alpha 255, because colors blending could apply more than once to the same pixel
    private void fastEllipse(int x, int y, int radiusX, int radiusY, int color) {
        long radiusX2 = (long) radiusX * radiusX;
        long radiusY2 = (long) radiusY * radiusY;
        double radiusX2Y2 = radiusX2 * radiusY2;

        for (int posY = 0; posY < radiusY; posY++) {
            int xLimit = (int) Math.ceil(Math.sqrt((radiusX2Y2 - posY * posY * radiusX2) / radiusY2));
            for (int posX = 0; posX <= xLimit; posX++) {
                pixel(x + posX, y + posY, color);
                pixel(x - posX, y + posY, color);
                pixel(x + posX, y - posY, color);
                pixel(x - posX, y - posY, color);
            }
        }
    }

    @Override
    public void ellipse(int x, int y, int radiusX, int radiusY, int thickness, int color) {
        if (((color >> 24) & 0xFF) == 255) {
            fastEllipse(x, y, radiusX, radiusY, thickness, color);
            return;
        }

        double t = thickness / 2d;
        double innerRadiusY2 = (radiusY - t) * (radiusY - t);
        double innerRadiusX2 = (radiusX - t) * (radiusX - t);
        double outerRadiusY2 = (radiusY + t) * (radiusY + t);
        double outerRadiusX2 = (radiusX + t) * (radiusX + t);

        double outerRadiusX2Y2 = outerRadiusX2 * outerRadiusY2;
        double innerRadiusX2Y2 = innerRadiusX2 * innerRadiusY2;

        int radiusXThickness = radiusX + thickness;
        int negativeRadiusXThickness = -radiusXThickness; // >12% faster than (-radiusX - thickness) in the loop
        int radiusYThickness = radiusY + thickness;
        int negativeRadiusYThickness = -radiusYThickness;

        for (int posY = negativeRadiusYThickness; posY < radiusYThickness; posY++) {
            double posY2 = posY * posY;
            for (int posX = negativeRadiusXThickness; posX < radiusXThickness; posX++) {
                double posX2 = posX * posX;
                if (posX2 * outerRadiusY2 + posY2 * outerRadiusX2 < outerRadiusX2Y2 &&
                        posX2 * innerRadiusY2 + posY2 * innerRadiusX2 > innerRadiusX2Y2) {
                    pixel(x + posX, y + posY, color);
                }
            }
        }
    }

    // Only works for colors with alpha 255, because colors blending could apply more than once to the same pixel
    private void fastEllipse(int x, int y, int radiusX, int radiusY, int thickness, int color) {
        double t = thickness / 2d;
        double innerRadiusY2 = (radiusY - t) * (radiusY - t);
        double innerRadiusX2 = (radiusX - t) * (radiusX - t);
        double outerRadiusY2 = (radiusY + t) * (radiusY + t);
        double outerRadiusX2 = (radiusX + t) * (radiusX + t);

        double innerRadiusX2Y2 = innerRadiusX2 * innerRadiusY2;
        double outerRadiusX2Y2 = outerRadiusX2 * outerRadiusY2;

        int yLimit = (int) Math.ceil(Math.sqrt(outerRadiusX2Y2 / outerRadiusY2));
        for (int posY = 0; posY <= yLimit; posY++) {
            int xLowerLimit = (int) Math.ceil(Math.sqrt((innerRadiusX2Y2 - posY * posY * innerRadiusX2) / innerRadiusY2));
            double xUpperLimit = Math.sqrt((outerRadiusX2Y2 - posY * posY * outerRadiusX2) / outerRadiusY2);
            for (int posX = xLowerLimit; posX <= xUpperLimit; posX++) {
                pixel(x + posX, y + posY, color);
                pixel(x - posX, y + posY, color);
                pixel(x + posX, y - posY, color);
                pixel(x - posX, y - posY, color);
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
