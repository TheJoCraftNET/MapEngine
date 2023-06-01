package de.pianoman911.mapengine.api.pipeline;

import de.pianoman911.mapengine.api.clientside.IMapDisplay;
import de.pianoman911.mapengine.api.drawing.IDrawingSpace;
import de.pianoman911.mapengine.api.drawing.ILayeredDrawingSpace;
import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;

/**
 * Manager interface for creating objects related to pipelines.
 */
public interface IPipelineProvider {

    /**
     * Creates a new pipeline with the given output and the specified streams.<br>
     * The initial pipeline output is created using {@link #output()}.
     *
     * @param streams the initial streams to be set on the pipeline
     * @return a new pipeline
     */
    default IPipeline createPipeline(IPipelineStream... streams) {
        return this.createPipeline(this.output(), streams);
    }

    /**
     * Creates a new pipeline with the given output and the specified streams.
     *
     * @param output  the initial output called to process every pipeline flush
     * @param streams the initial streams to be set on the pipeline
     * @return a new pipeline
     */
    IPipeline createPipeline(IPipelineOutput output, IPipelineStream... streams);

    /**
     * Creates a new pipeline output instance, which flushes the buffer
     * to the map display of the context.
     *
     * @return a new default pipeline output instance
     */
    IPipelineOutput output();

    /**
     * Creates a drawing space for the given map display. The width and height
     * is the total width and height of the specified map display. The context
     * is the default for this map and is created using {@link #ctx(IMapDisplay)}.
     *
     * @param display the map display used for creating the drawing space
     * @return a new drawing space used for drawing shapes
     */
    default IDrawingSpace drawingSpace(IMapDisplay display) {
        return this.drawingSpace(display.pixelWidth(), display.pixelHeight(), display);
    }

    /**
     * Creates a drawing space for the given context and creates a new rgb buffer
     * with the given width and height.
     *
     * @param width   the width for the new drawing space
     * @param height  the height for the new drawing space
     * @param display the map display used for creating the drawing space
     * @return a new drawing space used for drawing shapes
     */
    default IDrawingSpace drawingSpace(int width, int height, IMapDisplay display) {
        return this.drawingSpace(new FullSpacedColorBuffer(width, height), display);
    }

    /**
     * Creates a drawing space for the given context and buffer.
     *
     * @param buffer  the buffer used for drawing
     * @param display the map display used for creating the drawing space
     * @return a new drawing space used for drawing shapes
     */
    default IDrawingSpace drawingSpace(FullSpacedColorBuffer buffer, IMapDisplay display) {
        return this.drawingSpace(this.ctx(display), buffer);
    }

    /**
     * Creates a drawing space for the given context and creates a new rgb buffer
     * with the given width and height.
     *
     * @param ctx    the context for this drawing space
     * @param width  the width for the new drawing space
     * @param height the height for the new drawing space
     * @return a new drawing space used for drawing shapes
     */
    default IDrawingSpace drawingSpace(IPipelineContext ctx, int width, int height) {
        return this.drawingSpace(ctx, new FullSpacedColorBuffer(width, height));
    }

    /**
     * Creates a drawing space for the given context and buffer.
     *
     * @param ctx    the context for this drawing space
     * @param buffer the buffer used for drawing
     * @return a new drawing space used for drawing shapes
     */
    IDrawingSpace drawingSpace(IPipelineContext ctx, FullSpacedColorBuffer buffer);

    /**
     * Creates a layered drawing space for the given map display. The width and height
     * is the total width and height of the specified map display. The context
     * is the default for this map and is created using {@link #ctx(IMapDisplay)}.
     *
     * @param display the map display used for creating the layered drawing space
     * @return a new layered drawing space used for drawing shapes
     */
    default ILayeredDrawingSpace layeredDrawingSpace(IMapDisplay display) {
        return this.layeredDrawingSpace(display.pixelWidth(), display.pixelHeight(), display);
    }

    /**
     * Creates a layered drawing space for the given context and creates a new rgb buffer
     * with the given width and height.
     *
     * @param width   the width for the new layered drawing space
     * @param height  the height for the new layered drawing space
     * @param display the map display used for creating the layered drawing space
     * @return a new layered drawing space used for drawing shapes
     */
    default ILayeredDrawingSpace layeredDrawingSpace(int width, int height, IMapDisplay display) {
        return this.layeredDrawingSpace(new FullSpacedColorBuffer(width, height), display);
    }

    /**
     * Creates a layered drawing space for the given context and buffer.
     *
     * @param buffer  the buffer used for layered drawing
     * @param display the map display used for creating the layered drawing space
     * @return a new layered drawing space used for drawing shapes
     */
    default ILayeredDrawingSpace layeredDrawingSpace(FullSpacedColorBuffer buffer, IMapDisplay display) {
        return this.layeredDrawingSpace(this.ctx(display), buffer);
    }

    /**
     * Creates a layered drawing space for the given context and creates a new rgb buffer
     * with the given width and height.
     *
     * @param ctx    the context for this layered drawing space
     * @param width  the width for the new layered drawing space
     * @param height the height for the new layered drawing space
     * @return a new layered drawing space used for drawing shapes
     */
    default ILayeredDrawingSpace layeredDrawingSpace(IPipelineContext ctx, int width, int height) {
        return this.layeredDrawingSpace(ctx, new FullSpacedColorBuffer(width, height));
    }

    /**
     * Creates a layered drawing space for the given context and buffer.
     *
     * @param ctx    the context for this layered drawing space
     * @param buffer the buffer used for layered drawing
     * @return a new layered drawing space used for drawing shapes
     */
    ILayeredDrawingSpace layeredDrawingSpace(IPipelineContext ctx, FullSpacedColorBuffer buffer);

    /**
     * Creates a new context instance for the provided {@link IMapDisplay}.<br>
     * The display can be retrieved again by using {@link IPipelineContext#display()}.
     *
     * @param display the display to create the context for
     * @return a new pipelining context
     */
    IPipelineContext ctx(IMapDisplay display);
}
