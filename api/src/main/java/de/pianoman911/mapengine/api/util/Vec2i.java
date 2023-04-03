package de.pianoman911.mapengine.api.util;

public record Vec2i(int x, int y) {
    public static Vec2i of(int x, int y) {
        return new Vec2i(x, y);
    }

    @Override
    public String toString() {
        return "Vec2i{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
