package de.pianoman911.mapengine.core.clientside;

import de.pianoman911.mapengine.api.clientside.IMapDisplay;
import de.pianoman911.mapengine.api.data.IMapUpdateData;
import de.pianoman911.mapengine.api.pipeline.IPipeline;
import de.pianoman911.mapengine.common.data.MapUpdateData;
import de.pianoman911.mapengine.core.MapEnginePlugin;
import de.pianoman911.mapengine.core.pipeline.Pipeline;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.util.BlockVector;
import org.bukkit.util.BoundingBox;

public class FrameContainer implements IMapDisplay {

    private final Frame[] frames;
    private final int width, height;
    private final MapEnginePlugin plugin;
    private final Pipeline pipeline;
    private final BlockFace direction;
    private final BoundingBox box;
    private final BoundingBox interactionBox;

    public FrameContainer(BlockVector a, BlockVector b, BlockFace direction, MapEnginePlugin plugin, Pipeline pipeline) {
        this.plugin = plugin;
        this.pipeline = pipeline;
        this.direction = direction;
        BlockVector min = new BlockVector(Math.min(a.getBlockX(), b.getBlockX()), Math.min(a.getBlockY(), b.getBlockY()), Math.min(a.getBlockZ(), b.getBlockZ()));
        BlockVector max = new BlockVector(Math.max(a.getBlockX(), b.getBlockX()), Math.max(a.getBlockY(), b.getBlockY()), Math.max(a.getBlockZ(), b.getBlockZ()));

        switch (direction) {
            case NORTH, SOUTH -> {
                max.setX(max.getBlockX() + 1);
                max.setY(max.getBlockY() + 1);

                this.width = max.getBlockX() - min.getBlockX();
                this.height = max.getBlockY() - min.getBlockY();

                frames = new Frame[width * height];

                for (int i = 0; i < frames.length; i++) {
                    int x = i % width;
                    int y = i / width;
                    frames[i] = new Frame(plugin, direction, new BlockVector(min.getBlockX() + x, min.getBlockY() + y, max.getBlockZ()));
                }

                if (direction == BlockFace.NORTH) {
                    min.setZ(min.getBlockZ() + 1);
                    max.setZ(max.getBlockZ() + 1);
                }
            }
            case EAST, WEST -> {
                max.setY(max.getBlockY() + 1);
                max.setZ(max.getBlockZ() + 1);

                this.width = max.getBlockZ() - min.getBlockZ();
                this.height = max.getBlockY() - min.getBlockY();
                frames = new Frame[width * height];

                for (int i = 0; i < frames.length; i++) {
                    int z = i % width;
                    int y = i / width;
                    frames[i] = new Frame(plugin, direction, new BlockVector(max.getBlockX(), min.getBlockY() + y, min.getBlockZ() + z));
                }

                if (direction == BlockFace.WEST) {
                    min.setX(min.getBlockX() + 1);
                    max.setX(max.getBlockX() + 1);
                }
            }

            case UP, DOWN -> {
                max.setX(max.getBlockX() + 1);
                max.setZ(max.getBlockZ() + 1);

                this.width = max.getBlockX() - min.getBlockX();
                this.height = max.getBlockZ() - min.getBlockZ();
                frames = new Frame[width * height];

                for (int i = 0; i < frames.length; i++) {
                    int x = i % width;
                    int z = i / width;
                    frames[i] = new Frame(plugin, direction, new BlockVector(min.getBlockX() + x, max.getBlockY(), min.getBlockZ() + z));
                }
            }
            default -> throw new IllegalArgumentException("Unknown direction: " + direction);
        }
        this.box = BoundingBox.of(min, max);

        switch (direction) {
            case NORTH -> interactionBox = box.clone().shift(0, 0, -Frame.INVISIBLE_MAP_DEPTH);
            case SOUTH -> interactionBox = box.clone().shift(0, 0, Frame.INVISIBLE_MAP_DEPTH);
            case EAST -> interactionBox = box.clone().shift(Frame.INVISIBLE_MAP_DEPTH, 0, 0);
            case WEST -> interactionBox = box.clone().shift(-Frame.INVISIBLE_MAP_DEPTH, 0, 0);
            case UP -> interactionBox = box.clone().shift(0, Frame.INVISIBLE_MAP_DEPTH, 0);
            case DOWN -> interactionBox = box.clone().shift(0, -Frame.INVISIBLE_MAP_DEPTH, 0);
            default -> throw new IllegalArgumentException("Unknown direction: " + direction);
        }
    }

    @Override
    public int width() {
        return width;
    }

    @Override
    public int height() {
        return height;
    }

    @Override
    public int pixelWidth() {
        return width * 128;
    }

    @Override
    public int pixelHeight() {
        return height * 128;
    }

    @Override
    public BoundingBox box() {
        return box;
    }

    @Override
    public BlockFace direction() {
        return direction;
    }

    @Override
    public void spawn(Player player) {
        for (Frame frame : frames) {
            frame.spawnPacket().send(player);
            frame.interactionEntity().send(player);
            frame.interactionEntitySize().send(player);
            frame.setIdPacket(0, true).send(player);
        }
        plugin.platform().flush(player);
    }

    @Override
    public void despawn(Player player) {
        IntList ids = new IntArrayList();
        for (Frame frame : frames) {
            ids.add(frame.entityId);
            ids.add(frame.interactionId);
        }

        plugin.platform().createRemoveEntitiesPacket(ids).send(player);
        plugin.platform().flush(player);
    }

    @Override
    public void mapId(Player player, int z) {
        for (Frame frame : frames) {
            frame.setIdPacket(z, true).send(player);
        }
        plugin.platform().flush(player);
    }

    @Override
    public void update(Player player, IMapUpdateData[] data, boolean fullData, int z, MapCursorCollection cursors) {
        for (int i = 0; i < frames.length; i++) {
            if (data[i].empty()) {
                continue;
            }
            frames[i].updatePacket((MapUpdateData) data[i], fullData, z, cursors).send(player);
        }
        plugin.platform().flush(player);
    }

    @Override
    public IPipeline pipeline() {
        return pipeline;
    }

    public boolean hasEntity(int entityId) {
        for (Frame frame : frames) {
            if (frame.entityId == entityId) {
                return true;
            }
        }
        return false;
    }

    public boolean isInteraction(int entityId) {
        for (Frame frame : frames) {
            if (frame.interactionId == entityId) {
                return true;
            }
        }
        return false;
    }

    public boolean hasBlock(BlockVector blockVector) {
        for (Frame frame : frames) {
            if (frame.pos.equals(blockVector)) {
                return true;
            }
        }
        return false;
    }

    public BoundingBox interactionBox() {
        return interactionBox;
    }
}
