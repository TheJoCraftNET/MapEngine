package de.pianoman911.mapengine.api.util;

public class ColorBuffer {

    private final byte[] buffer;
    private final int x;
    private final int y;

    public ColorBuffer(byte[] buffer, int x, int y) {
        this.buffer = buffer;
        this.x = x;
        this.y = y;
    }

    public ColorBuffer(int size, int x, int y) {
        this.buffer = new byte[size];
        this.x = x;
        this.y = y;
    }

    public byte[] buffer() {
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
}
