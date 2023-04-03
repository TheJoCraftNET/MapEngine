package de.pianoman911.mapengine.api.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ImageUtils {

    public static BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = result.createGraphics();
        graphics.drawImage(image, 0, 0, width, height, null);
        graphics.dispose();

        return result;
    }

    public static int[] rgb(BufferedImage image) {
        int[] rgb = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), rgb, 0, image.getWidth());
        return rgb;
    }
}
