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

    public FullSpacedColorBuffer scale(double scale, boolean smooth) {
        return scale(scale, scale, smooth);
    }

    @SuppressWarnings("Duplicates")
    public FullSpacedColorBuffer scale(double scaleX, double scaleY, boolean smooth) {
        int newWidth = (int) (width * scaleX);
        int newHeight = (int) (height * scaleY);
        int[] newData = new int[newWidth * newHeight];

        double xRatio = (double) width / newWidth;
        double yRatio = (double) height / newHeight;

        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                int scrX = (int) (x * xRatio);
                int scrY = (int) (y * yRatio);
                int scrIndex = scrX + scrY * width;
                if (scrX >= width || scrY >= height) {
                    continue;
                }
                if (smooth) {
                    int color = data[scrIndex];
                    int alpha = (color >> 24) & 0xFF;
                    if (alpha == 0) {
                        continue;
                    }

                    int red = (color >> 16) & 0xFF;
                    int green = (color >> 8) & 0xFF;
                    int blue = color & 0xFF;

                    int count = 1;
                    if (scrX + 1 < width) {
                        color = data[scrIndex + 1];
                        alpha = (color >> 24) & 0xFF;
                        if (alpha != 0) {
                            red += (color >> 16) & 0xFF;
                            green += (color >> 8) & 0xFF;
                            blue += color & 0xFF;
                            count++;
                        }
                    }
                    if (scrY + 1 < height) {
                        color = data[scrIndex + width];
                        alpha = (color >> 24) & 0xFF;
                        if (alpha != 0) {
                            red += (color >> 16) & 0xFF;
                            green += (color >> 8) & 0xFF;
                            blue += color & 0xFF;
                            count++;
                        }
                    }
                    if (scrX + 1 < width && scrY + 1 < height) {
                        color = data[scrIndex + width + 1];
                        alpha = (color >> 24) & 0xFF;
                        if (alpha != 0) {
                            red += (color >> 16) & 0xFF;
                            green += (color >> 8) & 0xFF;
                            blue += color & 0xFF;
                            count++;
                        }
                    }

                    newData[x + y * newWidth] = (alpha << 24) | ((red / count) << 16) | ((green / count) << 8) | (blue / count);
                } else {
                    newData[x + y * newWidth] = data[scrIndex];
                }
            }
        }

        return new FullSpacedColorBuffer(newData, newWidth, newHeight);
    }

    public FullSpacedColorBuffer copy() {
        return new FullSpacedColorBuffer(data.clone(), width, height);
    }
}
