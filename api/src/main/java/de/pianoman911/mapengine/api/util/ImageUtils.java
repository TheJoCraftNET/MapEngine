package de.pianoman911.mapengine.api.util;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Utilities for working with {@link BufferedImage}'s.
 */
public final class ImageUtils {

    private ImageUtils() {
    }

    /**
     * Creates a new image with the given width and height
     * using the content of the given image.
     *
     * @param image  the image to resize
     * @param width  the new width
     * @param height the new height
     * @return the resized image
     */
    public static BufferedImage resize(BufferedImage image, int width, int height) {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        {
            Graphics2D graphics = result.createGraphics();
            graphics.drawImage(image, 0, 0, width, height, null);
            graphics.dispose();
        }
        return result;
    }

    /**
     * A helper method to get the rgb array from a buffered image.
     *
     * @param image the image to get the rgb array from
     * @return the rgb array of the image
     */
    public static int[] rgb(BufferedImage image) {
        int[] rgb = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), rgb, 0, image.getWidth());
        return rgb;
    }

    public static BufferedImage cutImage(BufferedImage image) {
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
}
