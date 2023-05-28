package de.pianoman911.mapengine.core.api;

import de.pianoman911.mapengine.api.MapEngineApi;
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
import de.pianoman911.mapengine.core.clientside.FrameContainer;
import de.pianoman911.mapengine.core.drawing.DrawingSpace;
import de.pianoman911.mapengine.core.drawing.LayeredDrawingSpace;
import de.pianoman911.mapengine.core.pipeline.FlushingOutput;
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

    public ImplMapEngineApi(MapEnginePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public IMapColors colors() {
        return plugin.colorPalette();
    }

    @Override
    public IPipelineProvider pipeline() {
        return new IPipelineProvider() {

            @Override
            public IPipeline createPipeline(IPipelineStream... streams) {
                return new Pipeline(plugin, streams);
            }

            @Override
            public IPipeline createPipeline(IPipelineOutput output, IPipelineStream... streams) {
                return new Pipeline(output, streams);
            }

            @Override
            public IPipelineOutput output() {
                return new FlushingOutput(plugin);
            }

            @Override
            public IDrawingSpace drawingSpace(IMapDisplay display) {
                return drawingSpace(display.width() * 128, display.height() * 128, display);
            }

            @Override
            public IDrawingSpace drawingSpace(int width, int height, IMapDisplay display) {
                return drawingSpace(ctx(display), width, height);
            }

            @Override
            public IDrawingSpace drawingSpace(FullSpacedColorBuffer buffer, IMapDisplay display) {
                return drawingSpace(ctx(display), buffer);
            }

            @Override
            public IDrawingSpace drawingSpace(IPipelineContext ctx, int width, int height) {
                return new DrawingSpace(new FullSpacedColorBuffer(width, height), (PipelineContext) ctx);
            }

            @Override
            public IDrawingSpace drawingSpace(IPipelineContext ctx, FullSpacedColorBuffer buffer) {
                return new DrawingSpace(buffer, (PipelineContext) ctx);
            }

            @Override
            public ILayeredDrawingSpace layeredDrawingSpace(int width, int height, IMapDisplay display) {
                return layeredDrawingSpace(ctx(display), width, height);
            }

            @Override
            public ILayeredDrawingSpace layeredDrawingSpace(FullSpacedColorBuffer buffer, IMapDisplay display) {
                return layeredDrawingSpace(ctx(display), buffer);
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
            public IPipelineContext ctx(IMapDisplay display) {
                return new PipelineContext((FrameContainer) display);
            }
        };
    }

    @Override
    public IDisplayProvider displayProvider() {
        return new IDisplayProvider() {
            @Override
            public IMapDisplay createRawPipelineDisplay(BlockVector a, BlockVector b, BlockFace direction, IPipeline pipeline) {
                return plugin.mapManager().createDisplay(a, b, direction, direction, (Pipeline) pipeline);
            }

            @Override
            public IMapDisplay createRawPipelineDisplay(BlockVector a, BlockVector b, BlockFace direction, BlockFace visualDirection, IPipeline pipeline) {
                return plugin.mapManager().createDisplay(a, b, direction, visualDirection, (Pipeline) pipeline);
            }

            @Override
            public IMapDisplay createBasic(BlockVector a, BlockVector b, BlockFace direction) {
                return plugin.mapManager().createDisplay(a, b, direction, direction);
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
    public @Unmodifiable Set<IMapDisplay> mapDisplays() {
        return Set.copyOf(plugin.mapManager().displays());
    }

    @Override
    public @Unmodifiable Set<IHoldableDisplay> holdableDisplays() {
        return Set.copyOf(plugin.holdableManager().displays());
    }

    @Deprecated
    @Override
    public @Nullable IMapDisplay displayInView(Player player, int maxDistance) {
        return plugin.mapManager().displayInView(player, maxDistance);
    }

    @Override
    public @Nullable MapTraceResult traceDisplayInView(Player player, int maxDistance) {
        return plugin.mapManager().traceDisplayInView(player, maxDistance);
    }
}
