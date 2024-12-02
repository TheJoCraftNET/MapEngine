package de.pianoman911.mapengine.api.util;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.util.NumberConversions;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

public final class FontRegistry {

    public static final Font DEFAULT = new Font("Arial", Font.PLAIN, 10);
    private static final Rectangle2D ZERO_RECT = new Rectangle.Float(0f, 0f, 0f, 0f);
    public static final FullSpacedColorBuffer EMPTY_BUFFER = new FullSpacedColorBuffer(0, 0);

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
     * @param antiAliasing if antialiasing should be enabled (less rough edges)
     * @return a rendered image of the specified text
     */
    public static BufferedImage convertText(String text, Font font, Color color, boolean antiAliasing) {
        return convertTextBuffer(text, font, color, antiAliasing).snapshot();
    }

    private static FullSpacedColorBuffer convertTextBuffer(String text, Font font, Color color, boolean antiAliasing) {
        if (font == null) {
            font = DEFAULT;
        }
        BufferedImage img = renderMultiLineText(text, font, color, antiAliasing);
        if (img == null) {
            return EMPTY_BUFFER;
        }

        return new FullSpacedColorBuffer(ImageUtils.rgb(img), img.getWidth(), img.getHeight());
    }


    private static BufferedImage renderMultiLineText(String text, Font font, Color color, boolean antiAliasing) {
        String[] lines = StringUtils.splitPreserveAllTokens(text, '\n');
        if (lines.length < 1) {
            return null;
        }

        FontRenderContext ctx = new FontRenderContext(null, antiAliasing, false);

        LineData[] data = new LineData[lines.length];
        Rectangle2D totalRect = (Rectangle2D) ZERO_RECT.clone();
        for (int i = 0; i < lines.length; i++) {
            GlyphVector vec = font.createGlyphVector(ctx, lines[i]);

            // line heights are managed outside of this method
            Rectangle2D widthBounds = vec.getLogicalBounds();
            Rectangle2D heightBounds = vec.getVisualBounds();

            Rectangle2D bounds = new Rectangle2D.Double(
                    widthBounds.getX(), heightBounds.getY(),
                    widthBounds.getWidth(), heightBounds.getHeight()
            );
            totalRect.setRect(
                    0d, 0d,
                    Math.max(totalRect.getWidth(), bounds.getWidth()),
                    totalRect.getHeight() + bounds.getHeight()
            );
            // save for later use
            data[i] = new LineData(vec, bounds);
        }

        if (totalRect.getWidth() == 0 || totalRect.getHeight() == 0) {
            return null;
        }

        // add small buffer zone around image to allow
        // for antialiasing to work correctly
        BufferedImage img = new BufferedImage(
                NumberConversions.ceil(totalRect.getWidth()) + 2,
                NumberConversions.ceil(totalRect.getHeight()) + 2,
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D graphics = img.createGraphics();
        graphics.setColor(color);

        for (LineData datum : data) {
            graphics.drawGlyphVector(datum.vec(), 1f, (float) -datum.bounds().getY() + 1f);
            graphics.translate(0d, datum.bounds().getHeight());
        }

        return img;
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
        return convertTextBuffer(text, font, color, antiAliasing);
    }

    private record LineData(GlyphVector vec, Rectangle2D bounds) {}
}
