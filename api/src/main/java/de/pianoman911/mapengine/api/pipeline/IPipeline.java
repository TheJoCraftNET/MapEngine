package de.pianoman911.mapengine.api.pipeline;

import java.util.List;

public interface IPipeline {

    IPipelineOutput output();

    void output(IPipelineOutput output);

    void addNode(IPipelineStream stream);

    void removeNode(IPipelineStream stream);

    List<IPipelineStream> streams();

    void flush(IPipelineInput input);
}
