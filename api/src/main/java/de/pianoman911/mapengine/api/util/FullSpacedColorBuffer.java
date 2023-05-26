package de.pianoman911.mapengine.api.util;

import java.awt.image.BufferedImage;

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
        int newAlpha = ((newColor >> 24) & 0xFF);
        if (newAlpha == 255) {
            // completely opaque pixel, overwrite
            this.data[x + y * width()] = newColor;
            return;
        }

        if (newAlpha == 0) {
            // if pixel is completely transparent, do nothing
            return;
        }

        // neither full pixel nor completely empty pixel, needs blending
        float newRed = ((newColor >> 16) & 0xFF) / 255f;
        float newGreen = ((newColor >> 8) & 0xFF) / 255f;
        float newBlue = (newColor & 0xFF) / 255f;

        int colorIndex = x + y * width();
        int oldColor = this.data[colorIndex];
        float oldAlpha = ((oldColor >> 24) & 0xFF) / 255f;
        float oldRed = ((oldColor >> 16) & 0xFF) / 255f;
        float oldGreen = ((oldColor >> 8) & 0xFF) / 255f;
        float oldBlue = (oldColor & 0xFF) / 255f;

        // mix alpha channels
        float newAlphaF = newAlpha / 255f;
        float alpha = newAlphaF + (oldAlpha * (1f - newAlphaF));

        // mix colors
        int red = srgb2linearByte((linearFloat2srgb(newRed) * newAlphaF + linearFloat2srgb(oldRed) * oldAlpha * (1f - newAlphaF)) / alpha);
        int green = srgb2linearByte((linearFloat2srgb(newGreen) * newAlphaF + linearFloat2srgb(oldGreen) * oldAlpha * (1f - newAlphaF)) / alpha);
        int blue = srgb2linearByte((linearFloat2srgb(newBlue) * newAlphaF + linearFloat2srgb(oldBlue) * oldAlpha * (1f - newAlphaF)) / alpha);

        this.data[colorIndex] = ((int) (alpha * 255f) << 24) | (red << 16) | (green << 8) | blue;
    }

    // see https://www.w3.org/Graphics/Color/sRGB.html section "Colorimetric definitions and digital encodings"
    //
    // this is adjusted to be faster, which means that it
    // is technically incorrect. in reality, no one cares

    private static float linearFloat2srgb(float val) {
        // this linear function is based on the original graph and
        // fixes really bright colors as a "side effect"
        if (val >= 0.938f) {
            return val * 2.2f - 1.2f;
        }

        float adjustedVal = val - 0.015f;
        return adjustedVal * adjustedVal;
    }

    private static int srgb2linearByte(float srgb) {
        // see above method for reason
        if (srgb >= 0.8636f) {
            return (int) ((srgb + 1.2f) / (2.2f / 255f));
        }
        return (int) ((Math.sqrt(srgb) + 0.015f) * 255f);
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
                    int red = (color >> 16) & 0xFF;
                    int green = (color >> 8) & 0xFF;
                    int blue = color & 0xFF;

                    int count = 1;
                    if (scrX + 1 < width) {
                        color = data[scrIndex + 1];

                        alpha += (color >> 24) & 0xFF;
                        red += (color >> 16) & 0xFF;
                        green += (color >> 8) & 0xFF;
                        blue += color & 0xFF;
                        count++;

                    }
                    if (scrY + 1 < height) {
                        color = data[scrIndex + width];

                        alpha += (color >> 24) & 0xFF;
                        red += (color >> 16) & 0xFF;
                        green += (color >> 8) & 0xFF;
                        blue += color & 0xFF;
                        count++;
                    }
                    if (scrX + 1 < width && scrY + 1 < height) {
                        color = data[scrIndex + width + 1];

                        alpha += (color >> 24) & 0xFF;
                        red += (color >> 16) & 0xFF;
                        green += (color >> 8) & 0xFF;
                        blue += color & 0xFF;
                        count++;
                    }

                    newData[x + y * newWidth] = ((alpha / count) << 24) | ((red / count) << 16) | ((green / count) << 8) | (blue / count);
                } else {
                    newData[x + y * newWidth] = data[scrIndex];
                }
            }
        }

        return new FullSpacedColorBuffer(newData, newWidth, newHeight);
    }

    public FullSpacedColorBuffer rotate(Rotation rotation) {
        int newWidth = rotation == Rotation.CLOCKWISE || rotation == Rotation.COUNTER_CLOCKWISE ? height : width;
        int newHeight = rotation == Rotation.CLOCKWISE || rotation == Rotation.COUNTER_CLOCKWISE ? width : height;
        int[] newData = new int[newWidth * newHeight];

        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                int newX = x;
                int newY = y;
                switch (rotation) {
                    case CLOCKWISE -> {
                        newX = y; // x is used as a y-coordinate here because of the rotation
                        newY = newHeight - x - 1;
                    }
                    case COUNTER_CLOCKWISE -> {
                        newX = newWidth - y - 1;
                        newY = x; // y is used as an x-coordinate here because of the rotation
                    }
                    case UPSIDE_DOWN -> {
                        newX = newWidth - x - 1;
                        newY = newHeight - y - 1;
                    }
                }
                newData[x + y * newWidth] = data[newX + newY * width];
            }
        }

        return new FullSpacedColorBuffer(newData, newWidth, newHeight);
    }

    public FullSpacedColorBuffer applySuperSampling(int factor) {
        FullSpacedColorBuffer buffer = new FullSpacedColorBuffer(width + 2 * factor, height + 2 * factor);
        buffer.buffer(this, factor, factor);
        return buffer.scale(factor, true).scale(1.0 / factor, true);
    }

    public FullSpacedColorBuffer scale(int newWidth, int newHeight, boolean smooth) {
        return scale((double) newWidth / width, (double) newHeight / height, smooth);
    }

    public BufferedImage snapshot() {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, width, height, data, 0, width);
        return image;
    }

    public FullSpacedColorBuffer subBuffer(int x, int y, int width, int height) {
        int[] newData = new int[width * height];
        for (int h = 0; h < height; h++) {
            System.arraycopy(data, (x + (y + h) * this.width), newData, h * width, width);
        }
        return new FullSpacedColorBuffer(newData, width, height);
    }

    public FullSpacedColorBuffer copy() {
        return new FullSpacedColorBuffer(data.clone(), width, height);
    }
}
