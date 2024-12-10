package de.pianoman911.mapengine.api.util;

import com.google.common.base.Preconditions;

/**
 * Color buffer used for wrapping minecraft color space data.
 */
public class ColorBuffer {

    private final byte[] data;
    private final int width;
    private final int height;

    /**
     * Wraps the given minecraft map color data into an object.
     *
     * @param data   initial data
     * @param width  the width of the buffer, must match the data
     * @param height the height of the buffer, must match the data
     */
    public ColorBuffer(byte[] data, int width, int height) {
        Preconditions.checkState(data.length / width == height,
                "Width %s and height %s invalid for rgb array with length %s",
                width, height, data.length);

        this.data = data;
        this.width = width;
        this.height = height;
    }

    /**
     * Creates a new color buffer with the given size.
     *
     * @param width  the width of the buffer, must be compatible with the size
     * @param height the height of the buffer, must be compatible with the size
     * @deprecated should not be used for creation, use {@link #ColorBuffer(int, int)} instead
     */
    @Deprecated
    public ColorBuffer(int size, int width, int height) {
        this(width, height);
    }

    /**
     * Creates a new color buffer from with the given width and height.
     *
     * @param width  the width of the buffer
     * @param height the height of the buffer
     */
    public ColorBuffer(int width, int height) {
        this(new byte[width * height], width, height);
    }

    /**
     * @return the raw color data wrapped by this buffer, mutable
     */
    public final byte[] data() {
        return this.data;
    }

    /**
     * @return the internal length of the data
     */
    public final int size() {
        return this.data.length;
    }

    /**
     * @return the width in pixels
     */
    public final int width() {
        return this.width;
    }

    /**
     * @return the height in pixels
     */
    public final int height() {
        return this.height;
    }
}
