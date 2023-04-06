package de.pianoman911.mapengine.api.util;

public class ColorBuffer {

    private final byte[] data;
    private final int width;
    private final int height;

    public ColorBuffer(byte[] data, int width, int height) {
        this.data = data;
        this.width = width;
        this.height = height;
    }

    public ColorBuffer(int size, int width, int height) {
        this.data = new byte[size];
        this.width = width;
        this.height = height;
    }

    public byte[] data() {
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
}
