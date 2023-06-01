package de.pianoman911.mapengine.api.drawing;

import de.pianoman911.mapengine.api.pipeline.IPipelineContext;
import de.pianoman911.mapengine.api.pipeline.IPipelineInput;
import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;

import java.util.List;

/**
 * A combination of multiple {@link IDrawingSpace}'s, stacked on top to produce
 * layering. This produces a slight performance overhead, but allows for
 * easier development and combination of stuff.
 */
public interface ILayeredDrawingSpace extends IPipelineInput {

    /**
     * @return all layers of this drawing space
     */
    List<IDrawingSpace> layers();

    /**
     * @return the result buffer of this drawing space
     */
    FullSpacedColorBuffer resultBuffer();

    /**
     * @return the context in which everything is drawn
     */
    IPipelineContext ctx();

    /**
     * @param index the index of the wanted layer
     * @return the layer with the given index
     * @throws IndexOutOfBoundsException if the index is invalid
     */
    IDrawingSpace layer(int index) throws IndexOutOfBoundsException;

    /**
     * Creates a new layer on top of the other layers
     *
     * @return the new layer
     */
    IDrawingSpace newLayer();

    /**
     * Creates a new layer on the given index of the other layers.
     * <p>
     * If the index is greater than the number of layers, the new layer will be on top of the other layers.<br>
     * If layers below the given index are missing, they will be created as well
     *
     * @param index the index of the new layer
     * @return the new layer
     */
    IDrawingSpace layerOrNew(int index);
}
