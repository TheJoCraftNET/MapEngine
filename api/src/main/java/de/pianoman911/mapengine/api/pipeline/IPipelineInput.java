package de.pianoman911.mapengine.api.pipeline;

import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;
import it.unimi.dsi.fastutil.Pair;

public interface IPipelineInput extends IPipelineNode {

    Pair<FullSpacedColorBuffer, IPipelineContext> combined();

    /**
     * Internal buffer of the drawing space
     *
     * @return the buffer of the drawing space
     */
    FullSpacedColorBuffer buffer();

    /**
     * Returns the pipeline context of this drawing space
     *
     * @return the pipeline context of this drawing space
     */
    IPipelineContext ctx();
}
