package de.pianoman911.mapengine.core.drawing;

import de.pianoman911.mapengine.api.drawing.IDrawingSpace;
import de.pianoman911.mapengine.api.pipeline.IPipelineContext;
import de.pianoman911.mapengine.api.util.FontRegistry;
import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;
import de.pianoman911.mapengine.core.pipeline.PipelineContext;
import it.unimi.dsi.fastutil.Pair;

import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;

public record DrawingSpace(FullSpacedColorBuffer buffer, PipelineContext context) implements IDrawingSpace {

    public static final int ALPHA = 0xFF000000;

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
        buffer.buffer(buffer, x, y);
    }

    @Override
    public void line(int x1, int y1, int x2, int y2, int color) {
        for (int y = y1; y < y2; y++) {
            for (int x = x1; x < x2; x++) {
                pixel(x, y, color);
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
        // Draw triangle
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
    public Pair<FullSpacedColorBuffer, IPipelineContext> combined() {
        return Pair.of(buffer, context);
    }
}
