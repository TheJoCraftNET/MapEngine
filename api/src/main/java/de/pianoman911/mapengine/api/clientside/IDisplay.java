package de.pianoman911.mapengine.api.clientside;

import de.pianoman911.mapengine.api.pipeline.IPipeline;

/**
 * Marker interface for all map displays.
 */
public interface IDisplay {

    /**
     * @return the display total width (in available pixels)
     */
    int pixelWidth();

    /**
     * @return the display total height (in available pixels)
     */
    int pixelHeight();

    /**
     * The default {@link IPipeline} for this display.<br>
     * You can also use multiple own pipelines for different purposes with the same display.
     *
     * @return the default pipeline for this display
     */
    IPipeline pipeline();
}
