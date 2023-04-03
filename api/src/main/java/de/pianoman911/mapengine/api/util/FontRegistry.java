package de.pianoman911.mapengine.api.util;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class FontRegistry {

    public static final Font DEFAULT = new Font("Arial", Font.PLAIN, 10);

    public static BufferedImage convertText(String text, Font font, Color color) {
        if (font == null) {
            font = DEFAULT;
        }

        BufferedImage b = new BufferedImage(text.length() * font.getSize(), (int) (font.getSize() * (5 / 4.0)), BufferedImage.TYPE_INT_ARGB);
        Graphics g = b.getGraphics();
        g.setFont(font);
        g.setColor(color);
        g.drawString(text, 0, font.getSize());
        g.dispose();

        int startX = b.getWidth();
        int startY = b.getHeight();
        int endX = 0;
        int endY = 0;
        for (int x = 0; x < b.getWidth(); x++) {
            for (int y = 0; y < b.getHeight(); y++) {
                if (((b.getRGB(x, y) >> 24) & 0xFF) == 255) {
                    startX = Math.min(startX, x);
                    startY = Math.min(startY, y);
                    endX = Math.max(endX, x);
                    endY = Math.max(endY, y);
                }
            }
        }
        endX++;
        endY++;

        BufferedImage bc = new BufferedImage(endX - startX, endY - startY, BufferedImage.TYPE_INT_ARGB);
        Graphics2D gc = bc.createGraphics();
        gc.drawImage(b, 0, 0, endX - startX, endY - startY, startX, startY, endX, endY, null);
        gc.dispose();
        return bc;
    }

    public static FullSpacedColorBuffer convertText2Bytes(String text, Font font, Color color) {
        BufferedImage image = convertText(text, font, color);
        int[] bytes = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), bytes, 0, image.getWidth());
        return new FullSpacedColorBuffer(bytes, image.getWidth(), image.getHeight());
    }
}
