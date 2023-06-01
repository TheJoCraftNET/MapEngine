package de.pianoman911.mapengine.api.pipeline;

import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;
import it.unimi.dsi.fastutil.Pair;

public interface IPipelineInput extends IPipelineNode {

    /**
     * @deprecated no longer used, use this object as a holder instead
     */
    @Deprecated
    Pair<FullSpacedColorBuffer, IPipelineContext> combined();

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
        ctx().display().pipeline().flush(this);
    }
}
