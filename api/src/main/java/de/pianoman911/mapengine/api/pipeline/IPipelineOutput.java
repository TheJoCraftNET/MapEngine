package de.pianoman911.mapengine.api.pipeline;

import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;

public interface IPipelineOutput extends IPipelineNode {

    void output(FullSpacedColorBuffer buffer, IPipelineContext context);
}
