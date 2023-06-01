package de.pianoman911.mapengine.api.pipeline;

import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;

/**
 * A pipeline stream is a pipeline node which takes a buffer,
 * a context and returns a computed buffer.<br>
 * This is used for applying filters in the pipeline or
 * changing the pipeline context based on the buffer contents.
 */
@FunctionalInterface
public interface IPipelineStream extends IPipelineNode {

    FullSpacedColorBuffer compute(FullSpacedColorBuffer buffer, IPipelineContext context);
}
