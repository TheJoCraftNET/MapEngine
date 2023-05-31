package de.pianoman911.mapengine.core.pipeline;

import de.pianoman911.mapengine.api.pipeline.IPipeline;
import de.pianoman911.mapengine.api.pipeline.IPipelineContext;
import de.pianoman911.mapengine.api.pipeline.IPipelineInput;
import de.pianoman911.mapengine.api.pipeline.IPipelineOutput;
import de.pianoman911.mapengine.api.pipeline.IPipelineStream;
import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;
import de.pianoman911.mapengine.core.MapEnginePlugin;
import it.unimi.dsi.fastutil.Pair;

import java.util.ArrayList;
import java.util.List;

public class Pipeline implements IPipeline {

    private final List<IPipelineStream> streams;
    private IPipelineOutput output;

    public Pipeline(IPipelineOutput output, IPipelineStream... streams) {
        this.streams = new ArrayList<>(List.of(streams));
        this.output = output;
    }

    public Pipeline(MapEnginePlugin plugin, IPipelineStream... streams) {
        this.streams = new ArrayList<>(List.of(streams));
        this.output = new FlushingOutput(plugin);
    }

    @Override
    public List<IPipelineStream> streams() {
        return List.copyOf(streams);
    }

    @Override
    public void addNode(IPipelineStream stream) {
        streams.add(stream);
    }

    @Override
    public void removeNode(IPipelineStream stream) {
        streams.remove(stream);
    }

    @Override
    public IPipelineOutput output() {
        return output;
    }

    @Override
    public void output(IPipelineOutput output) {
        this.output = output;
    }

    @Override
    public void flush(IPipelineInput input) {
        Pair<FullSpacedColorBuffer, IPipelineContext> i = input.combined();
        FullSpacedColorBuffer buffer = i.first();
        IPipelineContext context = i.second();
        for (IPipelineStream stream : streams) {
            buffer = stream.compute(buffer, context);
        }
        output.output(buffer, context);
    }
}
