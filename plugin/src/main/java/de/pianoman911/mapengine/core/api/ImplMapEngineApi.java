package de.pianoman911.mapengine.core.api;

import de.pianoman911.mapengine.api.MapEngineApi;
import de.pianoman911.mapengine.api.clientside.IDisplay;
import de.pianoman911.mapengine.api.clientside.IDisplayProvider;
import de.pianoman911.mapengine.api.clientside.IHoldableDisplay;
import de.pianoman911.mapengine.api.clientside.IMapDisplay;
import de.pianoman911.mapengine.api.colors.IMapColors;
import de.pianoman911.mapengine.api.drawing.IDrawingSpace;
import de.pianoman911.mapengine.api.drawing.ILayeredDrawingSpace;
import de.pianoman911.mapengine.api.pipeline.IPipeline;
import de.pianoman911.mapengine.api.pipeline.IPipelineContext;
import de.pianoman911.mapengine.api.pipeline.IPipelineOutput;
import de.pianoman911.mapengine.api.pipeline.IPipelineProvider;
import de.pianoman911.mapengine.api.pipeline.IPipelineStream;
import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;
import de.pianoman911.mapengine.api.util.MapTraceResult;
import de.pianoman911.mapengine.core.MapEnginePlugin;
import de.pianoman911.mapengine.core.drawing.DrawingSpace;
import de.pianoman911.mapengine.core.drawing.LayeredDrawingSpace;
import de.pianoman911.mapengine.core.pipeline.HoldableDisplayOutput;
import de.pianoman911.mapengine.core.pipeline.MapDisplayOutput;
import de.pianoman911.mapengine.core.pipeline.Pipeline;
import de.pianoman911.mapengine.core.pipeline.PipelineContext;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

public class ImplMapEngineApi implements MapEngineApi {

    private final MapEnginePlugin plugin;
    private final IPipelineProvider pipelineProvider;
    private final IDisplayProvider displayProvider;

    public ImplMapEngineApi(MapEnginePlugin plugin) {
        this.plugin = plugin;

        this.pipelineProvider = new IPipelineProvider() {
            @Override
            public IPipeline createPipeline(IPipelineOutput output, IPipelineStream... streams) {
                return new Pipeline(output, streams);
            }

            @Override
            public IPipelineOutput createMapOutput() {
                return new MapDisplayOutput(plugin);
            }

            @Override
            public IPipelineOutput createHoldableOutput() {
                return new HoldableDisplayOutput(plugin);
            }

            @Override
            public IDrawingSpace drawingSpace(IPipelineContext ctx, FullSpacedColorBuffer buffer) {
                return new DrawingSpace(buffer, (PipelineContext) ctx);
            }

            @Override
            public ILayeredDrawingSpace createLayeredDrawingSpace(int width, int height, IDisplay display) {
                return layeredDrawingSpace(this.createCtx(display), width, height);
            }

            @Override
            public ILayeredDrawingSpace createLayeredDrawingSpace(FullSpacedColorBuffer buffer, IDisplay display) {
                return layeredDrawingSpace(this.createCtx(display), buffer);
            }

            @Override
            public ILayeredDrawingSpace layeredDrawingSpace(IPipelineContext ctx, int width, int height) {
                return new LayeredDrawingSpace(new FullSpacedColorBuffer(width, height), (PipelineContext) ctx);
            }

            @Override
            public ILayeredDrawingSpace layeredDrawingSpace(IPipelineContext ctx, FullSpacedColorBuffer buffer) {
                return new LayeredDrawingSpace(buffer, (PipelineContext) ctx);
            }

            @Override
            public IPipelineContext createCtx(IDisplay display) {
                return new PipelineContext(display);
            }
        };

        this.displayProvider = new IDisplayProvider() {
            @Override
            public IMapDisplay createRawPipelineDisplay(BlockVector a, BlockVector b, BlockFace direction, BlockFace visualDirection, IPipeline pipeline) {
                return plugin.mapManager().createDisplay(a, b, direction, visualDirection, (Pipeline) pipeline);
            }

            @Override
            public IMapDisplay createBasic(BlockVector a, BlockVector b, BlockFace direction, BlockFace visualDirection) {
                return plugin.mapManager().createDisplay(a, b, direction, visualDirection);
            }

            @Override
            public IHoldableDisplay createRawPipelineHoldableDisplay(IPipeline pipeline) {
                return plugin.holdableManager().createDisplay((Pipeline) pipeline);
            }

            @Override
            public IHoldableDisplay createHoldableDisplay() {
                return plugin.holdableManager().createDisplay();
            }
        };
    }

    @Override
    public IMapColors colors() {
        return this.plugin.colorPalette();
    }

    @Override
    public IPipelineProvider pipeline() {
        return this.pipelineProvider;
    }

    @Override
    public IDisplayProvider displayProvider() {
        return this.displayProvider;
    }

    @Override
    public @Unmodifiable Set<IMapDisplay> mapDisplays() {
        return Set.copyOf(this.plugin.mapManager().displays());
    }

    @Override
    public @Unmodifiable Set<IHoldableDisplay> holdableDisplays() {
        return Set.copyOf(this.plugin.holdableManager().displays());
    }

    @Deprecated
    @Override
    public @Nullable IMapDisplay displayInView(Player player, int maxDistance) {
        return this.plugin.mapManager().displayInView(player, maxDistance);
    }

    @Override
    public @Nullable MapTraceResult traceDisplayInView(Player player, int maxDistance) {
        return this.plugin.mapManager().traceDisplayInView(player, maxDistance);
    }
}
