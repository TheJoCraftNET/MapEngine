package de.pianoman911.mapengine.api.pipeline;

import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;

public interface IPipelineInput extends IPipelineNode {

    /**
     * @return the internal buffer of the drawing space
     */
    FullSpacedColorBuffer buffer();

    /**
     * @return the pipeline context of this drawing space
     */
    IPipelineContext ctx();

    /**
     * Flushes the drawing space to the display using the display's pipeline
     */
    default void flush() {
        ctx().getDisplay().pipeline().flush(this);
    }
}
