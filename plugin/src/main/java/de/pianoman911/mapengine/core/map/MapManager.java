package de.pianoman911.mapengine.core.map;

import de.pianoman911.mapengine.api.clientside.IMapDisplay;
import de.pianoman911.mapengine.api.util.MapTraceResult;
import de.pianoman911.mapengine.api.util.Vec2i;
import de.pianoman911.mapengine.core.MapEnginePlugin;
import de.pianoman911.mapengine.core.clientside.FrameContainer;
import de.pianoman911.mapengine.core.pipeline.Pipeline;
import de.pianoman911.mapengine.core.util.MapUtil;
import org.bukkit.FluidCollisionMode;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class MapManager {

    private final Set<FrameContainer> displays = new HashSet<>();
    private final MapEnginePlugin plugin;

    public MapManager(MapEnginePlugin plugin) {
        this.plugin = plugin;
    }

    public @Nullable FrameContainer display(int entityId) {
        for (FrameContainer display : this.displays()) {
            if (display.isInteraction(entityId)
                    || display.hasEntity(entityId)) {
                return display;
            }
        }
        return null;
    }

    public @Nullable FrameContainer display(BlockVector blockVector) {
        for (FrameContainer display : this.displays()) {
            if (display.hasBlock(blockVector)) {
                return display;
            }
        }
        return null;
    }

    public Set<FrameContainer> displays() {
        synchronized (this.displays) {
            return this.displays;
        }
    }

    @Deprecated
    public @Nullable IMapDisplay displayInView(Player player, int maxDistance) {
        MapTraceResult result = traceDisplayInView(player, maxDistance);
        if (result == null) {
            return null;
        }
        return result.display();
    }

    public @Nullable MapTraceResult traceDisplayInView(Player player, int maxDistance) {
        RayTraceResult result = player.rayTraceBlocks(maxDistance, FluidCollisionMode.NEVER);
        if (result == null) {
            return null;
        }

        Block block = result.getHitBlock();
        BlockFace face = result.getHitBlockFace();
        if (block == null || face == null) {
            return null;
        }

        Vector blockVector = block.getLocation().toVector().toBlockVector();
        blockVector = MapUtil.itemFrameOffset(blockVector, face);
        FrameContainer display = display(blockVector.toBlockVector());
        if (display == null) {
            return null;
        }

        Vec2i clickPos = MapUtil.calculateClickPosition(player, display, maxDistance);
        if (clickPos == null) {
            return null;
        }

        return new MapTraceResult(clickPos, display);
    }

    public IMapDisplay createDisplay(BlockVector a, BlockVector b, BlockFace direction, BlockFace visualDirection) {
        FrameContainer display = new FrameContainer(a, b, direction, visualDirection, plugin, new Pipeline(plugin));
        synchronized (this.displays) {
            this.displays.add(display);
        }
        return display;
    }

    public IMapDisplay createDisplay(BlockVector a, BlockVector b, BlockFace direction, BlockFace visualDirection, Pipeline pipeline) {
        FrameContainer display = new FrameContainer(a, b, direction, visualDirection, plugin, pipeline);
        synchronized (this.displays) {
            this.displays.add(display);
        }
        return display;
    }
}
