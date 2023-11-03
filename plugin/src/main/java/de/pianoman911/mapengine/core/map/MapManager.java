package de.pianoman911.mapengine.core.map;

import de.pianoman911.mapengine.api.clientside.IMapDisplay;
import de.pianoman911.mapengine.api.util.MapTraceResult;
import de.pianoman911.mapengine.api.util.Vec2i;
import de.pianoman911.mapengine.core.MapEnginePlugin;
import de.pianoman911.mapengine.core.clientside.FrameContainer;
import de.pianoman911.mapengine.core.pipeline.MapDisplayOutput;
import de.pianoman911.mapengine.core.pipeline.Pipeline;
import de.pianoman911.mapengine.core.util.MapUtil;
import de.pianoman911.mapengine.core.util.RayTraceUtil;
import it.unimi.dsi.fastutil.Pair;
import org.bukkit.FluidCollisionMode;
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
        synchronized (this.displays) {
            for (FrameContainer display : this.displays) {
                double distance = Math.max(maxDistance, display.interactDistance());
                if (distance <= 0) {
                    continue;
                }

                Pair<Vector, BlockFace> clip = RayTraceUtil.clipBox(player, display.interactionBox(), distance);
                if (clip != null && clip.second() == display.direction()) {
                    RayTraceResult ray = player.rayTraceBlocks(clip.left().subtract(player.getEyeLocation().toVector()).length(), FluidCollisionMode.NEVER);
                    if (ray != null) {
                        continue; // block in the sight
                    }

                    Vec2i clickPos = MapUtil.calculateClickPosition(player, display, distance);
                    return new MapTraceResult(clickPos, display);
                }
            }
        }
        return null;
    }

    public @Nullable MapTraceResult traceDisplayInView(Player player) {
        return traceDisplayInView(player, 0);
    }

    public IMapDisplay createDisplay(BlockVector a, BlockVector b, BlockFace direction, BlockFace visualDirection) {
        FrameContainer display = new FrameContainer(a, b, direction, visualDirection, this.plugin,
                new Pipeline(new MapDisplayOutput(this.plugin)));
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
