package de.pianoman911.mapengine.api.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public final class FontRegistry {

    public static final Font DEFAULT = new Font("Arial", Font.PLAIN, 10);

    private FontRegistry() {
    }

    /**
     * Convert the specified string text to an image with the given font and color,
     * which is anti-aliased (less rough edges).
     *
     * @param text  the text to render
     * @param font  the font to use for the text
     * @param color the color to use for the text
     * @return a rendered image of the specified text
     */
    public static BufferedImage convertText(String text, Font font, Color color) {
        return convertText(text, font, color, true);
    }

    /**
     * Convert the specified string text to an image with the given font and color.
     *
     * @param text         the text to render
     * @param font         the font to use for the text
     * @param color        the color to use for the text
     * @param antiAliasing if anti-aliasing should be enabled (less rough edges)
     * @return a rendered image of the specified text
     */
    public static BufferedImage convertText(String text, Font font, Color color, boolean antiAliasing) {
        if (font == null) {
            font = DEFAULT;
        }

        BufferedImage image = new BufferedImage(text.length() * font.getSize(),
                (int) (font.getSize() * (5 / 4.0)), BufferedImage.TYPE_INT_ARGB);
        {
            Graphics2D graphics = (Graphics2D) image.getGraphics();
            graphics.setFont(font);
            graphics.setColor(color);
            if (antiAliasing) {
                graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            }
            graphics.drawString(text, 0, font.getSize());
            graphics.dispose();
        }

        int startX = image.getWidth();
        int startY = image.getHeight();
        int endX = 0;
        int endY = 0;
        for (int x = 0; x < image.getWidth(); x++) {
            for (int y = 0; y < image.getHeight(); y++) {
                if (((image.getRGB(x, y) >> 24) & 0xFF) != 0) {
                    startX = Math.min(startX, x);
                    startY = Math.min(startY, y);
                    endX = Math.max(endX, x);
                    endY = Math.max(endY, y);
                }
            }
        }
        endX++;
        endY++;

        BufferedImage cutImage = new BufferedImage(endX - startX, endY - startY, BufferedImage.TYPE_INT_ARGB);
        {
            Graphics2D gc = cutImage.createGraphics();
            gc.drawImage(image, 0, 0, endX - startX, endY - startY, startX, startY, endX, endY, null);
            gc.dispose();
        }

        return cutImage;
    }

    /**
     * Rendered the specified text to an argb color buffer
     *
     * @see #convertText(String, Font, Color)
     */
    public static FullSpacedColorBuffer convertText2Bytes(String text, Font font, Color color) {
        return convertText2Bytes(text, font, color, true);
    }

    /**
     * Rendered the specified text to an argb color buffer
     *
     * @see #convertText(String, Font, Color, boolean)
     */
    public static FullSpacedColorBuffer convertText2Bytes(String text, Font font, Color color, boolean antiAliasing) {
        BufferedImage image = convertText(text, font, color, antiAliasing);
        int[] bytes = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), bytes, 0, image.getWidth());
        return new FullSpacedColorBuffer(bytes, image.getWidth(), image.getHeight());
    }
}
