package de.pianoman911.mapengine.core.clientside;

import de.pianoman911.mapengine.api.clientside.IMapDisplay;
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
import org.jetbrains.annotations.Range;

public class FrameContainer implements IMapDisplay {

    private final Frame[] frames;
    private final int width, height;
    private final MapEnginePlugin plugin;
    private final Pipeline pipeline;
    private final BlockFace direction;
    private final BlockFace visualDirection;
    private final BoundingBox box;
    private final BoundingBox interactionBox;

    @Deprecated
    public FrameContainer(BlockVector a, BlockVector b, BlockFace direction, MapEnginePlugin plugin, Pipeline pipeline) {
        this(a, b, direction, direction, plugin, pipeline);
    }

    public FrameContainer(BlockVector a, BlockVector b, BlockFace direction, BlockFace visualDirection, MapEnginePlugin plugin, Pipeline pipeline) {
        this.plugin = plugin;
        this.pipeline = pipeline;
        this.direction = direction;
        this.visualDirection = visualDirection;

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

                    x = direction == BlockFace.NORTH ? max.getBlockX() - x - 1 : min.getBlockX() + x;
                    frames[i] = new Frame(plugin, direction, new BlockVector(x, max.getBlockY() - y - 1, max.getBlockZ()));
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

                    z = direction == BlockFace.EAST ? max.getBlockZ() - z - 1 : min.getBlockZ() + z;
                    frames[i] = new Frame(plugin, direction, new BlockVector(max.getBlockX(), max.getBlockY() - y - 1, z));
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

                    z = direction == BlockFace.DOWN ? max.getBlockZ() - z - 1 : min.getBlockZ() + z;
                    frames[i] = new Frame(plugin, direction, new BlockVector(min.getBlockX() + x, max.getBlockY(), z));
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
    public BlockFace visualDirection() {
        return visualDirection;
    }

    @Override
    public void spawn(Player player, int z) {
        spawn0(player, visualDirection, z);
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

    @SuppressWarnings("removal")
    @Override
    public void update(Player player, de.pianoman911.mapengine.api.data.IMapUpdateData[] data, int z, MapCursorCollection cursors) {
        for (int i = 0; i < frames.length; i++) {
            if (data[i].empty()) {
                continue;
            }
            frames[i].updatePacket((MapUpdateData) data[i], z, cursors).send(player);
        }
        plugin.platform().flush(player);
    }

    @Override
    public IPipeline pipeline() {
        return pipeline;
    }

    @Override
    public void rotation(Player player, float yaw, float pitch) {
        for (Frame frame : frames) {
            frame.rotationPacket(yaw, pitch).send(player);
        }
        plugin.platform().flush(player);
    }

    @Override
    public void itemRotation(Player player, @Range(from = 0, to = 7) int rotation) {
        for (Frame frame : frames) {
            frame.itemRotationPacket(rotation).send(player);
        }
        plugin.platform().flush(player);
    }

    @Override
    public void visualDirection(Player player, BlockFace visualDirection) {
        spawn0(player, visualDirection, 0);
    }

    @Override
    public void visualDirection(Player player, BlockFace visualDirection, int z) {
        spawn0(player, visualDirection, z);
    }

    private void spawn0(Player player, BlockFace visualDirection, int z) {
        for (Frame frame : frames) {
            frame.spawnPacket(visualDirection).send(player);
            frame.interactionEntity().send(player);
            frame.interactionEntitySize().send(player);
            frame.setIdPacket(z, true).send(player);
        }
        plugin.platform().flush(player);
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
