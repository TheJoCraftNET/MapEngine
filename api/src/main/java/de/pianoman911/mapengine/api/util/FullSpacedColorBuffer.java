package de.pianoman911.mapengine.api.util;

import com.google.common.base.Preconditions;
import de.pianoman911.mapengine.api.colors.IMapColors;

import java.awt.image.BufferedImage;

/**
 * ARGB color buffer with 24 bit of rgb colors and 8 bits of an alpha channel.
 */
public class FullSpacedColorBuffer {

    private final int[] data;
    private final int width;
    private final int height;

    /**
     * Wraps the given argb data into a color buffer.
     *
     * @param data   initial data
     * @param width  the width of the buffer, must match the data
     * @param height the height of the buffer, must match the data
     */
    public FullSpacedColorBuffer(int[] data, int width, int height) {
        Preconditions.checkState((data.length == 0 && width == 0 && height == 0) ||
                        (data.length / width == height),
                "Width %s and height %s invalid for rgb array with length %s",
                width, height, data.length);

        this.data = data;
        this.width = width;
        this.height = height;
    }

    /**
     * Creates a new color buffer with the given size.
     *
     * @param width  the width of the buffer
     * @param height the height of the buffer
     * @deprecated should not be used for creation, use {@link #FullSpacedColorBuffer(int, int)} instead
     */
    @Deprecated
    public FullSpacedColorBuffer(int size, int width, int height) {
        this(width, height);
    }

    /**
     * Creates a new color buffer from with the given width and height.
     *
     * @param width  the width of the buffer
     * @param height the height of the buffer
     */
    public FullSpacedColorBuffer(int width, int height) {
        this(new int[width * height], width, height);
    }

    private static int index(int x, int y, int width) {
        return x + y * width;
    }

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

    // see https://www.w3.org/Graphics/Color/sRGB.html section "Colorimetric definitions and digital encodings"
    //
    // this is adjusted to be faster, which means that it
    // is technically incorrect. in reality, no one cares

    /**
     * Sets the pixel at the given position in the buffer to the given color.
     * It respects the alpha channel of the color and blends the color with the
     * existing color in the buffer.
     *
     * @param x        the x position of the pixel
     * @param y        the y position of the pixel
     * @param newColor the new color of the pixel
     */
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

    /**
     * Sets rectangular area of the buffer to the given color.
     * It respects the alpha channel of the color and blends the color with the
     * existing color in the buffer.
     *
     * @param pixels the pixels to set
     * @param x      the x position of the area
     * @param y      the y position of the area
     * @param width  the width of the area
     * @param height the height of the area
     */
    public void pixels(int[] pixels, int x, int y, int width, int height) {
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w++) {
                if (x + w >= 0 && x + w < width() && y + h >= 0 && y + h < height()) {
                    this.pixel(x + w, y + h, pixels[index(w, h, width)]);
                }
            }
        }
    }

    /**
     * Sets another buffer on top of this buffer
     * It respects the alpha channel of the color and blends the color with the
     * existing color in the buffer.
     *
     * @param buffer the buffer to set
     * @param x      the x position of the buffer
     * @param y      the y position of the buffer
     */
    public void buffer(FullSpacedColorBuffer buffer, int x, int y) {
        this.pixels(buffer.buffer(), x, y, buffer.width(), buffer.height());
    }

    /**
     * Replaces all occurrences of the old color with the new color.
     *
     * @param oldColor the color to replace
     * @param newColor the color to replace with
     */
    public void replaceColor(int oldColor, int newColor) {
        for (int i = 0; i < this.data.length; i++) {
            if (this.data[i] == oldColor) {
                this.data[i] = newColor;
            }
        }
    }

    /**
     * Removes all occurrences of the given color, replacing them with transparent pixels.
     *
     * @param color the color to remove
     */
    public void removeColor(int color) {
        this.replaceColor(color, IMapColors.TRANSPARENT);
    }

    /**
     * Creates a scaled copy of this buffer
     *
     * @param scale  the factor to scale by
     * @param smooth whether to use a smooth scaling algorithm
     * @return the scaled copy
     */
    public FullSpacedColorBuffer scale(double scale, boolean smooth) {
        return this.scale(scale, scale, smooth);
    }

    /**
     * Creates a scaled copy of this buffer with the given x- and y-scale.
     *
     * @param scaleX the factor to scale the x-axis by
     * @param scaleY the factor to scale the y-axis by
     * @param smooth whether to use a smooth scaling algorithm
     * @return the scaled copy
     */
    @SuppressWarnings("Duplicates")
    public FullSpacedColorBuffer scale(double scaleX, double scaleY, boolean smooth) {
        int newWidth = (int) (this.width * scaleX);
        int newHeight = (int) (this.height * scaleY);
        int[] newData = new int[newWidth * newHeight];

        double ratioX = (double) this.width / newWidth;
        double ratioY = (double) this.height / newHeight;

        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                int scrX = (int) (x * ratioX);
                int scrY = (int) (y * ratioY);
                int scrIndex = scrX + scrY * this.width;
                if (scrX >= this.width || scrY >= this.height) {
                    continue;
                }

                int color = this.data[scrIndex];
                if (smooth) {
                    int alpha = (color >> 24) & 0xFF;
                    int red = (color >> 16) & 0xFF;
                    int green = (color >> 8) & 0xFF;
                    int blue = color & 0xFF;

                    int count = 1;
                    if (scrX + 1 < this.width) {
                        color = this.data[scrIndex + 1];
                        alpha += (color >> 24) & 0xFF;
                        red += (color >> 16) & 0xFF;
                        green += (color >> 8) & 0xFF;
                        blue += color & 0xFF;
                        count++;
                    }
                    if (scrY + 1 < this.height) {
                        color = this.data[scrIndex + this.width];
                        alpha += (color >> 24) & 0xFF;
                        red += (color >> 16) & 0xFF;
                        green += (color >> 8) & 0xFF;
                        blue += color & 0xFF;
                        count++;
                    }
                    if (scrX + 1 < this.width && scrY + 1 < this.height) {
                        color = this.data[scrIndex + this.width + 1];
                        alpha += (color >> 24) & 0xFF;
                        red += (color >> 16) & 0xFF;
                        green += (color >> 8) & 0xFF;
                        blue += color & 0xFF;
                        count++;
                    }

                    color = ((alpha / count) << 24)
                            | ((red / count) << 16)
                            | ((green / count) << 8)
                            | (blue / count);
                }

                newData[index(x, y, newWidth)] = color;
            }
        }

        return new FullSpacedColorBuffer(newData, newWidth, newHeight);
    }

    /**
     * Creates a rotated copy of this buffer.
     *
     * @param rotation the rotation to rotate by
     * @return the rotated copy
     */
    public FullSpacedColorBuffer rotate(Rotation rotation) {
        int newWidth = rotation == Rotation.CLOCKWISE
                || rotation == Rotation.COUNTER_CLOCKWISE
                ? this.height : this.width;
        int newHeight = rotation == Rotation.CLOCKWISE
                || rotation == Rotation.COUNTER_CLOCKWISE
                ? this.width : this.height;
        int[] newData = new int[newWidth * newHeight];

        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                int newX;
                int newY;
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
                    default -> {
                        newX = x;
                        newY = y;
                    }
                }
                newData[x + y * newWidth] = this.data[this.index(newX, newY)];
            }
        }

        return new FullSpacedColorBuffer(newData, newWidth, newHeight);
    }

    /**
     * Create a super-sampled copy of this buffer.
     *
     * @param factor the factor to super-sample by, must be above 0
     * @return the super-sampled copy
     */
    public FullSpacedColorBuffer applySuperSampling(int factor) {
        Preconditions.checkState(factor > 0, "Invalid super-sampling factor: %s", factor);

        FullSpacedColorBuffer buffer = new FullSpacedColorBuffer(this.width + 2 * factor,
                this.height + 2 * factor);
        buffer.buffer(this, factor, factor);
        return buffer.scale(factor, true).scale(1d / factor, true);
    }

    /**
     * Creates a scaled copy of this buffer with the specified new dimensions.
     *
     * @param newWidth  the new width
     * @param newHeight the new height
     * @param smooth    whether to use a smooth scaling algorithm
     * @return the scaled copy
     */
    public FullSpacedColorBuffer scale(int newWidth, int newHeight, boolean smooth) {
        return scale((double) newWidth / this.width, (double) newHeight / this.height, smooth);
    }

    /**
     * @return a snapshot of this buffer as a {@link BufferedImage}
     */
    public BufferedImage snapshot() {
        BufferedImage image = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
        image.setRGB(0, 0, this.width, this.height, this.data, 0, this.width);
        return image;
    }

    /**
     * Creates a new sub-buffer of this buffer at the
     * given position with the given dimensions.
     *
     * @param x      the x-coordinate of where to start
     * @param y      the y-coordinate of where to start
     * @param width  the width of the sub-buffer
     * @param height the height of the sub-buffer
     * @return the new sub-buffer
     */
    public FullSpacedColorBuffer subBuffer(int x, int y, int width, int height) {
        int[] newData = new int[width * height];
        for (int h = 0; h < height; h++) {
            // can't copy everything directly, as this isn't a 2d-array
            System.arraycopy(this.data, this.index(x, y + h), newData, h * width, width);
        }
        return new FullSpacedColorBuffer(newData, width, height);
    }

    /**
     * * Creates a new sub-buffer of this buffer with the alpha channel cropped.
     *
     * @return the copy of this buffer with the alpha channel cropped
     */
    public FullSpacedColorBuffer cropAlpha() {
        return this.crop(IMapColors.TRANSPARENT);
    }

    /**
     * Creates a new sub-buffer of this buffer with the given background color cropped.
     * The rest of the background color will <b>not</b> be removed.
     * If you want to remove the background color, use {@link #removeColor(int)} afterward.
     *
     * @return the copy of this buffer with the given background color cropped
     */
    public FullSpacedColorBuffer crop(int background) {
        int minX = this.width;
        int minY = this.height;
        int maxX = 0;
        int maxY = 0;
        for (int y = 0; y < this.height; y++) {
            for (int x = 0; x < this.width; x++) {
                if (this.data[this.index(x, y)] != background) {
                    minX = Math.min(minX, x);
                    minY = Math.min(minY, y);
                    maxX = Math.max(maxX, x);
                    maxY = Math.max(maxY, y);
                }
            }
        }
        return this.subBuffer(minX, minY, maxX - minX + 1, maxY - minY + 1);
    }

    /**
     * @return a copy of this buffer
     */
    public FullSpacedColorBuffer copy() {
        return new FullSpacedColorBuffer(this.data.clone(), this.width, this.height);
    }

    private int index(int x, int y) {
        return index(x, y, this.width);
    }

    /**
     * Returns the color of the pixel at the given position.
     *
     * @param x the x-coordinate of the pixel
     * @param y the y-coordinate of the pixel
     * @return the color of the pixel
     */
    public int pixel(int x, int y) {
        return this.data[index(x, y)];
    }

    /**
     * @return the raw argb data wrapped by this buffer, mutable
     */
    public int[] buffer() {
        return this.data;
    }

    /**
     * @return the internal length of the data
     */
    public int size() {
        return this.data.length;
    }

    /**
     * @return the width in pixels
     */
    public int width() {
        return this.width;
    }

    /**
     * @return the height in pixels
     */
    public int height() {
        return this.height;
    }
}
