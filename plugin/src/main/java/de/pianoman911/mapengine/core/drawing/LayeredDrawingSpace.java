package de.pianoman911.mapengine.core.drawing;

import de.pianoman911.mapengine.api.drawing.IDrawingSpace;
import de.pianoman911.mapengine.api.drawing.ILayeredDrawingSpace;
import de.pianoman911.mapengine.api.pipeline.IPipelineContext;
import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;
import de.pianoman911.mapengine.core.pipeline.PipelineContext;

import java.util.ArrayList;
import java.util.List;

public class LayeredDrawingSpace implements ILayeredDrawingSpace {

    private final List<IDrawingSpace> layers = new ArrayList<>();
    private final FullSpacedColorBuffer resultBuffer;
    private final PipelineContext context;

    public LayeredDrawingSpace(FullSpacedColorBuffer resultSpace, PipelineContext context) {
        this.resultBuffer = resultSpace;
        this.context = context;
    }

    @Override
    public List<IDrawingSpace> layers() {
        return layers;
    }

    @Override
    public FullSpacedColorBuffer resultBuffer() {
        return resultBuffer;
    }

    @Override
    public IPipelineContext ctx() {
        return context;
    }

    @Override
    public IDrawingSpace layer(int index) {
        return layers.get(index);
    }

    @Override
    public IDrawingSpace newLayer() {
        DrawingSpace drawingSpace = new DrawingSpace(resultBuffer, context);
        layers.add(drawingSpace);
        return drawingSpace;
    }

    @Override
    public IDrawingSpace layerOrNew(int index) {
        if (index >= layers.size()) {
            return newLayer();
        } else {
            return layer(index);
        }
    }
}
