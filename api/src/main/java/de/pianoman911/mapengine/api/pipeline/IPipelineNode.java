package de.pianoman911.mapengine.api.pipeline;

/**
 * Implemented by every pipeline processing element.
 */
public interface IPipelineNode {

    /**
     * Destroys all related resources to this node.<br>
     * <strong>WARNING: This method should be called when the pipeline node is no longer needed.
     * It is not guaranteed that a pipeline node will work correctly after this method is called.
     * </strong>
     */
    default void destroy() {
    }
}
