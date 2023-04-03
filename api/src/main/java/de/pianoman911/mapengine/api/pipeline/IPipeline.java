package de.pianoman911.mapengine.api.pipeline;

import java.util.List;

public interface IPipeline {

    IPipelineOutput output();

    void output(IPipelineOutput output);

    default void addNode(IPipelineStream stream) {
        streams().add(stream);
    }

    default void removeNode(IPipelineStream stream) {
        streams().remove(stream);
    }

    List<IPipelineStream> streams();

    void flush(IPipelineInput input);
}
