package de.pianoman911.mapengine.api.pipeline;

import de.pianoman911.mapengine.api.clientside.IMapDisplay;
import de.pianoman911.mapengine.api.drawing.IDrawingSpace;
import de.pianoman911.mapengine.api.drawing.ILayeredDrawingSpace;
import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;

public interface IPipelineProvider {

    IPipeline createPipeline(IPipelineStream... streams);

    IPipeline createPipeline(IPipelineOutput output, IPipelineStream... streams);

    IPipelineOutput output();

    IDrawingSpace drawingSpace(IMapDisplay display);

    IDrawingSpace drawingSpace(int width, int height, IMapDisplay display);

    IDrawingSpace drawingSpace(FullSpacedColorBuffer buffer, IMapDisplay display);

    IDrawingSpace drawingSpace(IPipelineContext ctx, int width, int height);

    IDrawingSpace drawingSpace(IPipelineContext ctx, FullSpacedColorBuffer buffer);

    ILayeredDrawingSpace layeredDrawingSpace(int width, int height, IMapDisplay display);

    ILayeredDrawingSpace layeredDrawingSpace(FullSpacedColorBuffer buffer, IMapDisplay display);

    ILayeredDrawingSpace layeredDrawingSpace(IPipelineContext ctx, int width, int height);

    ILayeredDrawingSpace layeredDrawingSpace(IPipelineContext ctx, FullSpacedColorBuffer buffer);

    IPipelineContext ctx(IMapDisplay display);
}
