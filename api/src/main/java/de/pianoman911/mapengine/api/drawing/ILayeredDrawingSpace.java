package de.pianoman911.mapengine.api.drawing;

import de.pianoman911.mapengine.api.pipeline.IPipelineContext;
import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;

import java.util.List;

public interface ILayeredDrawingSpace {

    /**
     * Returns all layers of this drawing space
     *
     * @return all layers of this drawing space
     */
    List<IDrawingSpace> layers();

    /**
     * Returns the result buffer of this drawing space
     *
     * @return the result buffer of this drawing space
     */
    FullSpacedColorBuffer resultBuffer();

    IPipelineContext ctx();

    /**
     * Returns the layer with the given index
     *
     * @param index the index of the layer
     * @return the layer with the given index
     */
    IDrawingSpace layer(int index);

    /**
     * Creates a new layer on top of the other layers
     *
     * @return the new layer
     */
    IDrawingSpace newLayer();

    /**
     * Creates a new layer on the given index of the other layers
     * If the index is greater than the number of layers, the new layer will be on top of the other layers
     * If layers below the given index are missing, they will be created as well
     *
     * @param index the index of the new layer
     * @return the new layer
     */
    IDrawingSpace layerOrNew(int index);
}
