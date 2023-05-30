package de.pianoman911.mapengine.api.pipeline;

import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;

/**
 * A pipeline stream is a pipeline node that takes a buffer and a context and returns a computed buffer
 * So you can use it to apply filters to the buffer or
 * change the pipeline context by means of the buffer dynamically
 */
public interface IPipelineStream {

    FullSpacedColorBuffer compute(FullSpacedColorBuffer buffer, IPipelineContext context);
}
