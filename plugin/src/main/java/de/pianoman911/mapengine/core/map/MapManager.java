package de.pianoman911.mapengine.core.map;

import de.pianoman911.mapengine.api.clientside.IMapDisplay;
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

import java.util.HashSet;
import java.util.Set;

public class MapManager {

    private final MapEnginePlugin plugin;
    private final Set<FrameContainer> displays = new HashSet<>();

    public MapManager(MapEnginePlugin plugin) {
        this.plugin = plugin;
    }

    public FrameContainer display(int entityId) {
        for (FrameContainer display : displays) {
            if (display.hasEntity(entityId)) {
                return display;
            }
        }
        return null;
    }

    public FrameContainer display(BlockVector blockVector) {
        for (FrameContainer display : displays) {
            if (display.hasBlock(blockVector)) {
                return display;
            }
        }
        return null;
    }

    public Set<FrameContainer> displays() {
        return displays;
    }

    public IMapDisplay displayInView(Player player, int maxDistance) {
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

        result.getHitPosition();
        return display;
    }

    public IMapDisplay createDisplay(BlockVector a, BlockVector b, BlockFace direction) {
        FrameContainer display = new FrameContainer(a, b, direction, plugin, new Pipeline(plugin));
        displays.add(display);
        return display;
    }

    public IMapDisplay createDisplay(BlockVector a, BlockVector b, BlockFace direction, Pipeline pipeline) {
        FrameContainer display = new FrameContainer(a, b, direction, plugin, pipeline);
        displays.add(display);
        return display;
    }
}
