package de.pianoman911.mapengine.api.drawing;

import de.pianoman911.mapengine.api.pipeline.IPipelineInput;
import de.pianoman911.mapengine.api.util.Alignment;
import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;
import net.kyori.adventure.text.Component;

import java.awt.Font;
import java.awt.image.BufferedImage;

/**
 * Drawing utility for drawing single pixels or other
 * shapes and forms.
 */
public interface IDrawingSpace extends IPipelineInput {

    /**
     * Clears the drawing space.
     */
    void clear();

    /**
     * Clears the drawing space in the given area.
     *
     * @param x      the x coordinate of the area
     * @param y      the y coordinate of the area
     * @param width  the width of the area
     * @param height the height of the area
     */
    void clear(int x, int y, int width, int height);

    /**
     * Draws a pixel in the drawing space
     *
     * @param x     the x coordinate of the pixel
     * @param y     the y coordinate of the pixel
     * @param color the argb color of the pixel
     */
    void pixel(int x, int y, int color);

    /**
     * Draws multiple pixels in the drawing space.
     *
     * @param pixels the pixels to draw in argb format
     * @param x      the x coordinate of the pixels
     * @param y      the y coordinate of the pixels
     * @param width  the width of the pixels
     * @param height the height of the pixels
     */
    void pixels(int[] pixels, int x, int y, int width, int height);

    /**
     * Draws a buffer in the drawing space.
     *
     * @param buffer the buffer to draw
     * @param x      the x coordinate of the buffer
     * @param y      the y coordinate of the buffer
     */
    void buffer(FullSpacedColorBuffer buffer, int x, int y);

    /**
     * Draws a line in the drawing space.
     *
     * @param x1    the x coordinate of the first point
     * @param y1    the y coordinate of the first point
     * @param x2    the x coordinate of the second point
     * @param y2    the y coordinate of the second point
     * @param color the rgb color of the line
     */
    void line(int x1, int y1, int x2, int y2, int color);

    /**
     * Draws a line in the drawing space.
     *
     * @param x1        the x coordinate of the first point
     * @param y1        the y coordinate of the first point
     * @param x2        the x coordinate of the second point
     * @param y2        the y coordinate of the second point
     * @param thickness the thickness of the line
     * @param color     the rgb color of the line
     */
    void line(int x1, int y1, int x2, int y2, int thickness, int color);

    /**
     * Draws a hollow rectangle in the drawing space with the border
     * using the specified thickness.
     *
     * @param x         the x coordinate of the rectangle
     * @param y         the y coordinate of the rectangle
     * @param width     the width of the rectangle
     * @param height    the height of the rectangle
     * @param thickness the thickness of the rectangle
     * @param color     the rgb color of the rectangle
     */
    void rect(int x, int y, int width, int height, int thickness, int color);

    /**
     * Draws a filled rectangle in the drawing space.
     *
     * @param x      the x coordinate of the rectangle
     * @param y      the y coordinate of the rectangle
     * @param width  the width of the rectangle
     * @param height the height of the rectangle
     * @param color  the rgb color of the rectangle
     */
    void rect(int x, int y, int width, int height, int color);

    /**
     * Draws a hollow circle in the drawing space with the border
     * using the specified thickness.
     *
     * @param x         the x coordinate of the circle
     * @param y         the y coordinate of the circle
     * @param radius    the radius of the circle
     * @param thickness the thickness of the circle
     * @param color     the rgb color of the circle
     */
    void circle(int x, int y, int radius, int thickness, int color);

    /**
     * Draws a filled circle in the drawing space.
     *
     * @param x      the x coordinate of the circle
     * @param y      the y coordinate of the circle
     * @param radius the radius of the circle
     * @param color  the rgb color of the circle
     */
    void circle(int x, int y, int radius, int color);

    /**
     * Draws a hollow triangle in the drawing space with the border
     * using the specified thickness.
     *
     * @param x1        the x coordinate of the first point
     * @param y1        the y coordinate of the first point
     * @param x2        the x coordinate of the second point
     * @param y2        the y coordinate of the second point
     * @param x3        the x coordinate of the third point
     * @param y3        the y coordinate of the third point
     * @param thickness the thickness of the triangle
     * @param color     the rgb color of the triangle
     */
    void triangle(int x1, int y1, int x2, int y2, int x3, int y3, int thickness, int color);

    /**
     * Draws a filled triangle in the drawing space.
     *
     * @param x1    the x coordinate of the first point
     * @param y1    the y coordinate of the first point
     * @param x2    the x coordinate of the second point
     * @param y2    the y coordinate of the second point
     * @param x3    the x coordinate of the third point
     * @param y3    the y coordinate of the third point
     * @param color the rgb color of the triangle
     */
    void triangle(int x1, int y1, int x2, int y2, int x3, int y3, int color);

    /**
     * Draws a hollow polygon in the drawing space with the border
     * using the specified thickness.
     *
     * @param x         the x coordinates of the polygon
     * @param y         the y coordinates of the polygon
     * @param thickness the thickness of the polygon
     * @param color     the rgb color of the polygon
     */
    void polygon(int[] x, int[] y, int thickness, int color);

    /**
     * Draws a filled polygon in the drawing space.
     *
     * @param x     the x coordinates of the polygon
     * @param y     the y coordinates of the polygon
     * @param color the rgb color of the polygon
     */
    void polygon(int[] x, int[] y, int color);

    /**
     * Draws a text in the drawing space.
     *
     * @param text  the text to draw
     * @param font  the font of the text
     * @param x     the x coordinate of the text
     * @param y     the y coordinate of the text
     * @param color the rgb color of the text
     */
    void text(String text, Font font, int x, int y, int color);

    /**
     * Draw a component to the drawing space.<br>
     * The component will be left-aligned (x-axis) and centered (y-axis),<br>
     * Antialiasing will be enabled and the line height will be 1.2
     * <p>
     * <strong>WARNING:</strong> This will only write text components with
     * their color. Decorations will be completely ignored.
     *
     * @param component the component to draw
     * @param font      the font of what is drawn
     * @param x         the x coordinate of the component
     * @param y         the y coordinate of the component
     */
    void component(Component component, Font font, int x, int y);

    /**
     * Draw a component to the drawing space.<br>
     * Antialiasing will be enabled and the line height will be 1.2
     * <p>
     * <strong>WARNING:</strong> This will only write text components with
     * their color, decorations will be completely ignored.
     *
     * @param component  the component to draw
     * @param font       the font of what is drawn
     * @param x          the x coordinate of the component
     * @param y          the y coordinate of the component
     * @param alignmentX the horizontal alignment of the component
     * @param alignmentY the vertical alignment of the component
     */
    void component(Component component, Font font, int x, int y, Alignment alignmentX, Alignment alignmentY);

    /**
     * Draw a component to the drawing space.<br>
     * The line height will be 1.2
     * <p>
     * <strong>WARNING:</strong> This will only write text components with
     * their color, decorations will be completely ignored.
     * @param component the component to draw
     * @param font the font of what is drawn
     * @param x the x coordinate of the component
     * @param y the y coordinate of the component
     * @param alignmentX the horizontal alignment of the component
     * @param alignmentY the vertical alignment of the component
     * @param antiAliasing if antialiasing should be enabled (less rough edges)
     */
    void component(Component component, Font font, int x, int y, Alignment alignmentX, Alignment alignmentY, boolean antiAliasing);

    /**
     * Draw a component to the drawing space.
     * <p>
     * <strong>WARNING:</strong> This will only write text components with
     * their color, decorations will be completely ignored.
     * @param component the component to draw
     * @param font the font of what is drawn
     * @param x the x coordinate of the component
     * @param y the y coordinate of the component
     * @param alignmentX the horizontal alignment of the component
     * @param alignmentY the vertical alignment of the component
     * @param antiAliasing if antialiasing should be enabled (less rough edges)
     * @param lineHeight the line height in percent (1.0 = 100%), 1.2 is recommended
     */
    void component(Component component, Font font, int x, int y, Alignment alignmentX, Alignment alignmentY, boolean antiAliasing, float lineHeight);

    /**
     * Draws an image in the drawing space.
     *
     * @param image the image to draw
     * @param x     the x coordinate of the image
     * @param y     the y coordinate of the image
     */
    void image(BufferedImage image, int x, int y);
}
