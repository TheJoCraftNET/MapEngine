package de.pianoman911.mapengine.api.util;

public class FullSpacedColorBuffer {

    private final int[] buffer;
    private final int x;
    private final int y;

    public FullSpacedColorBuffer(int[] buffer, int x, int y) {
        this.buffer = buffer;
        this.x = x;
        this.y = y;
    }

    public FullSpacedColorBuffer(int size, int x, int y) {
        this.buffer = new int[size];
        this.x = x;
        this.y = y;
    }

    public FullSpacedColorBuffer(int x, int y) {
        this.buffer = new int[x * y];
        this.x = x;
        this.y = y;
    }

    public int[] buffer() {
        return buffer;
    }

    public int size() {
        return buffer.length;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public void pixel(int x, int y, int newColor) {
        int newAlpha = (newColor >> 24) & 0xFF;
        if (newAlpha == 255) {
            // completely opaque pixel, overwrite
            this.buffer[x + y * x()] = newColor;
            return;
        }

        if (newAlpha == 0) {
            // if pixel is completely transparent, do nothing
            return;
        }

        // 0 < newAlpha < 255 -> alpha blending

        int colorIndex = x + y * x();
        int oldColor = this.buffer[colorIndex];
        int oldAlpha = (oldColor >> 24) & 0xFF;

        // actual alpha blending
        int alpha = 255 - ((255 - oldAlpha) * (255 - newAlpha)) / 255;
        int red = (((oldColor >> 16) & 0xFF) * oldAlpha * (255 - newAlpha)) / (255 * 255)
                + (((newColor >> 16) & 0xFF) * newAlpha * alpha) / (255 * 255);
        int green = (((oldColor >> 8) & 0xFF) * oldAlpha * (255 - newAlpha)) / (255 * 255)
                + (((newColor >> 8) & 0xFF) * newAlpha * alpha) / (255 * 255);
        int blue = ((oldColor & 0xFF) * oldAlpha * (255 - newAlpha)) / (255 * 255)
                + ((newColor & 0xFF) * newAlpha * alpha) / (255 * 255);

        this.buffer[colorIndex] = (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public void pixels(int[] pixels, int x, int y, int width, int height) {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (x + j >= 0 && x + j < x() && y + i >= 0 && y + i < y()) {
                    pixel(x + j, y + i, pixels[j + i * width]);
                }
            }
        }
    }

    public void buffer(FullSpacedColorBuffer buffer, int x, int y) {
        pixels(buffer.buffer(), x, y, buffer.x(), buffer.y());
    }

    public FullSpacedColorBuffer copy() {
        return new FullSpacedColorBuffer(buffer.clone(), x, y);
    }
}
