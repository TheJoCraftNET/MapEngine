package de.pianoman911.mapengine.api.colors;

import de.pianoman911.mapengine.api.util.ColorBuffer;
import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;

import java.awt.Color;
import java.awt.image.BufferedImage;

public interface IMapColors {

    byte TRANSPARENT = 0;
    byte BLACK = 119;
    byte WHITE = 34;
    byte RED = 18;
    byte GREEN = 30;
    byte BLUE = 50;
    byte CYAN = 126;
    byte YELLOW = 122;
    byte ORANGE = 62;
    byte BROWN = 42;
    byte PURPLE = 66;
    byte PINK = 82;

    byte color(int r, int g, int b);

    byte color(Color color);

    byte color(int rgb);

    byte[] colors(int[] rgb, int threads);

    default byte[] colors(int[] rgb) {
        // 3 Thread is the optimal value (graph: https://i.imgur.com/TtVSqyq.png)
        return colors(rgb, 3);
    }

    default ColorBuffer convertDirect(FullSpacedColorBuffer buffer) {
        int[] rgb = buffer.buffer();
        return new ColorBuffer(colors(rgb), buffer.x(), buffer.y());
    }

    byte[] convertImage(BufferedImage image);

    Color toColor(byte color);

}
