package de.pianoman911.mapengine.api.pipeline;

import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;

/**
 * Only internally implemented, but is working API.
 * <p>
 * Called last on each pipeline flush and accepts an RGB buffer and
 * the current pipeline context.
 */
public interface IPipelineOutput extends IPipelineNode {

    void output(FullSpacedColorBuffer buf, IPipelineContext ctx);
}
