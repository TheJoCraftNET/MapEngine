package de.pianoman911.mapengine.api.pipeline;

import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;

public interface IPipelineStream {

    FullSpacedColorBuffer compute(FullSpacedColorBuffer buffer, IPipelineContext context);
}
