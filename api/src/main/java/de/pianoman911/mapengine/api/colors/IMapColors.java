package de.pianoman911.mapengine.api.colors;

import de.pianoman911.mapengine.api.util.ColorBuffer;
import de.pianoman911.mapengine.api.util.Converter;
import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;

import java.awt.Color;
import java.awt.image.BufferedImage;

/**
 * This is used to convert 24-bit RGB color space
 * to the minecraft map color space.
 */
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

    /**
     * @return the minecraft map color nearest of the given red, green and blue colors.
     */
    byte color(int r, int g, int b);

    /**
     * @return the minecraft map color nearest of the given color.
     */
    byte color(Color color);

    /**
     * @return the minecraft map color nearest of the given RGB color.
     */
    byte color(int rgb);

    /**
     * @see #colors(int[], int)
     */
    default byte[] colors(int[] rgb) {
        // 3 threads are the optimal amount (graph: https://i.imgur.com/TtVSqyq.png)
        return colors(rgb, 3);
    }

    /**
     * Converts the given RGB colors to the minecraft map color space.<br>
     * The number of threads can be specified to speed up the process.<br>
     * <p>
     * Tests show that the most efficient number of threads for
     * converting colors is 3: <a href="https://i.imgur.com/TtVSqyq.png">Graph</a><br>
     * If the number of used threads is not important, use {@link #colors(int[])} instead.
     *
     * @return the minecraft map colors
     */
    byte[] colors(int[] rgb, int threads);

    /**
     * @return a minecraft map color buffer converted from the specified RGB color buffer
     */
    default ColorBuffer convertDirect(FullSpacedColorBuffer buffer) {
        byte[] mapColors = colors(buffer.buffer());
        return new ColorBuffer(mapColors, buffer.width(), buffer.height());
    }

    /**
     * Creates a copy and adjusts the colors of the
     * full spaced color buffer to minecraft map colors.
     *
     * @param buffer    the buffer to adjust
     * @param converter the converter to use
     * @return the copied buffer with the adjusted colors
     */
    FullSpacedColorBuffer adjustColors(FullSpacedColorBuffer buffer, Converter converter);

    /**
     * @return the minecraft map colors converted from the specified image.
     */
    byte[] convertImage(BufferedImage image);

    /**
     * @return the RGB color associated with the specified minecraft map color
     */
    Color toColor(byte color);

    /**
     * @return the RGB color associated with the specified minecraft map color
     */
    int toRGB(byte color);

    /**
     * @return the RGB colors associated with the specified minecraft map colors
     */
    int[] toRGBs(byte[] colors);
}
