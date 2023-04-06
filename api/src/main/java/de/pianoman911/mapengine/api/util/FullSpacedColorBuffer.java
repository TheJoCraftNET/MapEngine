package de.pianoman911.mapengine.api.util;

public class FullSpacedColorBuffer {

    private final int[] data;
    private final int width;
    private final int height;

    public FullSpacedColorBuffer(int[] data, int width, int height) {
        this.data = data;
        this.width = width;
        this.height = height;
    }

    public FullSpacedColorBuffer(int size, int width, int height) {
        this.data = new int[size];
        this.width = width;
        this.height = height;
    }

    public FullSpacedColorBuffer(int width, int height) {
        this.data = new int[width * height];
        this.width = width;
        this.height = height;
    }

    public int[] buffer() {
        return data;
    }

    public int size() {
        return data.length;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public void pixel(int x, int y, int newColor) {
        int newAlpha = (newColor >> 24) & 0xFF;
        if (newAlpha == 255) {
            // completely opaque pixel, overwrite
            this.data[x + y * width()] = newColor;
            return;
        }

        if (newAlpha == 0) {
            // if pixel is completely transparent, do nothing
            return;
        }

        // 0 < newAlpha < 255 -> alpha blending

        int colorIndex = x + y * width();
        int oldColor = this.data[colorIndex];
        int oldAlpha = (oldColor >> 24) & 0xFF;

        // actual alpha blending
        int alpha = 255 - ((255 - oldAlpha) * (255 - newAlpha)) / 255;
        int red = (((oldColor >> 16) & 0xFF) * oldAlpha * (255 - newAlpha)) / (255 * 255)
                + (((newColor >> 16) & 0xFF) * newAlpha * alpha) / (255 * 255);
        int green = (((oldColor >> 8) & 0xFF) * oldAlpha * (255 - newAlpha)) / (255 * 255)
                + (((newColor >> 8) & 0xFF) * newAlpha * alpha) / (255 * 255);
        int blue = ((oldColor & 0xFF) * oldAlpha * (255 - newAlpha)) / (255 * 255)
                + ((newColor & 0xFF) * newAlpha * alpha) / (255 * 255);

        this.data[colorIndex] = (alpha << 24) | (red << 16) | (green << 8) | blue;
    }

    public void pixels(int[] pixels, int x, int y, int width, int height) {
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                if (x + w >= 0 && x + w < width() && y + h >= 0 && y + h < height()) {
                    pixel(x + w, y + h, pixels[w + h * width]);
                }
            }
        }
    }

    public void buffer(FullSpacedColorBuffer buffer, int x, int y) {
        pixels(buffer.buffer(), x, y, buffer.width(), buffer.height());
    }

    public FullSpacedColorBuffer copy() {
        return new FullSpacedColorBuffer(data.clone(), width, height);
    }
}
