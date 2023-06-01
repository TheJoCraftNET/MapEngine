package de.pianoman911.mapengine.api.pipeline;

import org.jetbrains.annotations.Unmodifiable;

import java.util.List;

/**
 * The pipeline is a list of streams that are executed in order for every invoked flush.<br>
 * Multiple pipelines can be created for different purposes, they can be used for the same display.
 * <p>
 * <strong>WARNING: This pipeline is not thread-safe and should only be used on a single thread.</strong>
 */
public interface IPipeline {

    /**
     * Currently only used internally, but is a working part of the API.
     *
     * @return the output instance used for flushing
     */
    IPipelineOutput output();

    /**
     * Currently only used internally, but is a working part of the API.
     *
     * @param output the output instance used for flushing
     */
    void output(IPipelineOutput output);

    /**
     * Adds the given stream to the streams executed on flush.<br>
     * They are executed in the order in which they are added.
     *
     * @param stream the stream to add
     */
    void addStream(IPipelineStream stream);

    /**
     * @deprecated misleading name, use {@link #addStream(IPipelineStream)} instead
     */
    @Deprecated
    default void addNode(IPipelineStream stream) {
        this.addStream(stream);
    }

    /**
     * Removes the given stream from the streams executed on flush.<br>
     * They are executed in the order in which they are added.
     *
     * @param stream the stream to remove
     */
    boolean removeStream(IPipelineStream stream);

    /**
     * @deprecated misleading name, use {@link #removeStream(IPipelineStream)} instead
     */
    @Deprecated
    default void removeNode(IPipelineStream stream) {
        this.removeStream(stream);
    }

    /**
     * The returned streams are executed in the order in which they are added on flush.
     *
     * @return the streams executed on flush
     */
    @Unmodifiable List<IPipelineStream> streams();

    /**
     * Flushes the pipeline. This will execute all streams in the order in which they are added.
     *
     * @param input the input to use for the pipeline
     * @see #streams()
     */
    void flush(IPipelineInput input);
}
