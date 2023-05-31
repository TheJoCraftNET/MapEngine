package de.pianoman911.mapengine.api.pipeline;

import java.util.List;

/**
 * This is the pipeline interface.
 * <p>
 * The pipeline is a list of streams that are executed in order.
 * The pipeline is executed for every flush.
 * You can create multiple pipelines for different purposes and use it with the same displays.
 */
public interface IPipeline {

    IPipelineOutput output();

    void output(IPipelineOutput output);

    void addNode(IPipelineStream stream);

    void removeNode(IPipelineStream stream);

    List<IPipelineStream> streams();

    /**
     * Flushes the pipeline. This will execute all streams in order.
     *
     * @param input the input to use for the pipeline
     */
    void flush(IPipelineInput input);
}
