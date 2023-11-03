package de.pianoman911.mapengine.core.pipeline;

import de.pianoman911.mapengine.api.pipeline.IPipeline;
import de.pianoman911.mapengine.api.pipeline.IPipelineContext;
import de.pianoman911.mapengine.api.pipeline.IPipelineInput;
import de.pianoman911.mapengine.api.pipeline.IPipelineOutput;
import de.pianoman911.mapengine.api.pipeline.IPipelineStream;
import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;

import java.util.ArrayList;
import java.util.List;

public final class Pipeline implements IPipeline {

    private final List<IPipelineStream> streams;
    private IPipelineOutput output;

    public Pipeline(IPipelineOutput output, IPipelineStream... streams) {
        this.streams = new ArrayList<>(List.of(streams));
        this.output = output;
    }

    @Override
    public IPipelineOutput output() {
        return this.output;
    }

    @Override
    public void output(IPipelineOutput output) {
        this.output = output;
    }

    @Override
    public void addStream(IPipelineStream stream) {
        this.streams.add(stream);
    }

    @Override
    public boolean removeStream(IPipelineStream stream) {
        return this.streams.remove(stream);
    }

    @Override
    public List<IPipelineStream> streams() {
        return List.copyOf(this.streams);
    }

    @Override
    public void flush(IPipelineInput input) {
        FullSpacedColorBuffer buffer = input.buffer();
        IPipelineContext context = input.ctx();

        for (IPipelineStream stream : this.streams) {
            buffer = stream.compute(buffer, context);
        }
        this.output.output(buffer, context);
    }
}
