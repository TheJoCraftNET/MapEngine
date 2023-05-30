package de.pianoman911.mapengine.api.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class ImageUtils {

    /**
     * Creates a new image with the given width and height
     *
     * @param image  the image to resize
     * @param width  the new width
     * @param height the new height
     * @return the resized image
     */
    public static BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = result.createGraphics();
        graphics.drawImage(image, 0, 0, width, height, null);
        graphics.dispose();

        return result;
    }

    /**
     * A helper method to get the rgb array from a buffered image
     *
     * @param image the image to get the rgb array from
     * @return the rgb array of the image
     */
    public static int[] rgb(BufferedImage image) {
        int[] rgb = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), rgb, 0, image.getWidth());
        return rgb;
    }
}
