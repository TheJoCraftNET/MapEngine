package de.pianoman911.mapengine.core.clientside;

import de.pianoman911.mapengine.api.clientside.IMapDisplay;
import de.pianoman911.mapengine.api.data.IMapUpdateData;
import de.pianoman911.mapengine.api.pipeline.IPipeline;
import de.pianoman911.mapengine.api.util.Vec2i;
import de.pianoman911.mapengine.common.data.MapUpdateData;
import de.pianoman911.mapengine.core.MapEnginePlugin;
import de.pianoman911.mapengine.core.pipeline.Pipeline;
import de.pianoman911.mapengine.core.util.MapUtil;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.util.BlockVector;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class FrameContainer implements IMapDisplay {

    private final Frame[] frames;
    private final int width, height;
    private final MapEnginePlugin plugin;
    private final Pipeline pipeline;
    private final BlockFace direction;
    private final BlockVector min;
    private final BlockVector max;
    private final BoundingBox box;

    public FrameContainer(BlockVector a, BlockVector b, BlockFace direction, MapEnginePlugin plugin, Pipeline pipeline) {
        this.plugin = plugin;
        this.pipeline = pipeline;
        this.direction = direction;
        min = new BlockVector(Math.min(a.getBlockX(), b.getBlockX()), Math.min(a.getBlockY(), b.getBlockY()), Math.min(a.getBlockZ(), b.getBlockZ()));
        max = new BlockVector(Math.max(a.getBlockX(), b.getBlockX()), Math.max(a.getBlockY(), b.getBlockY()), Math.max(a.getBlockZ(), b.getBlockZ()));

        switch (direction) {
            case NORTH, SOUTH -> {
                this.width = max.getBlockX() - min.getBlockX() + 1;
                this.height = max.getBlockY() - min.getBlockY() + 1;

                frames = new Frame[width * height];

                if (direction == BlockFace.SOUTH) {
                    max.setX(min.getBlockX());
                }

                byte factor = (byte) (direction.equals(BlockFace.SOUTH) ? 1 : -1);
                for (int i = 0; i < frames.length; i++) {
                    int x = i % width;
                    int y = i / width;
                    frames[i] = new Frame(plugin, direction, new BlockVector(max.getBlockX() + factor * x, max.getBlockY() - y, max.getBlockZ()));
                }
            }
            case EAST, WEST -> {
                this.width = max.getBlockZ() - min.getBlockZ() + 1;
                this.height = max.getBlockY() - min.getBlockY() + 1;
                frames = new Frame[width * height];

                if (direction.equals(BlockFace.WEST)) {
                    max.setZ(min.getBlockZ());
                }

                byte factor = (byte) (direction.equals(BlockFace.WEST) ? 1 : -1);
                for (int i = 0; i < frames.length; i++) {
                    int z = i % width;
                    int y = i / width;
                    frames[i] = new Frame(plugin, direction, new BlockVector(max.getBlockX(), max.getBlockY() - y, max.getBlockZ() + factor * z));
                }
            }

            case UP, DOWN -> {
                this.width = max.getBlockX() - min.getBlockX() + 1;
                this.height = max.getBlockZ() - min.getBlockZ() + 1;
                frames = new Frame[width * height];

                if (direction.equals(BlockFace.UP)) {
                    max.setZ(min.getBlockZ());
                }
                byte factor = (byte) (direction.equals(BlockFace.UP) ? 1 : -1);
                for (int i = 0; i < frames.length; i++) {
                    int x = i % width;
                    int z = i / width;
                    frames[i] = new Frame(plugin, direction, new BlockVector(max.getBlockX() + x, max.getBlockY(), max.getBlockZ() + factor * z));
                }
            }
            default -> throw new IllegalArgumentException("Unknown direction: " + direction);
        }
        this.box = BoundingBox.of(min, max);
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
            frame.setIdPacket(0, false).send(player);
        }
    }

    @Override
    public void despawn(Player player) {
        IntList ids = new IntArrayList();
        for (Frame frame : frames) {
            ids.add(frame.entityId);
        }
        plugin.platform().createRemoveEntitiesPacket(ids).send(player);
    }

    @Override
    public void mapId(Player player, int z) {
        for (Frame frame : frames) {
            frame.setIdPacket(z, true).send(player);
        }

    }

    @Override
    public void update(Player player, IMapUpdateData[] data, boolean fullData, int z, MapCursorCollection cursors) {
        for (int i = 0; i < frames.length; i++) {
            if (data[i].empty()) {
                continue;
            }
            frames[i].updatePacket((MapUpdateData) data[i], fullData, z, cursors).send(player);
        }
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

    public boolean hasBlock(BlockVector blockVector) {
        for (Frame frame : frames) {
            if (frame.pos.equals(blockVector)) {
                return true;
            }
        }
        return false;
    }

    public Vector locOfFrame(int entityId) {
        for (Frame frame : frames) {
            if (frame.entityId == entityId) {
                return frame.pos;
            }
        }
        return null;
    }

    public Vec2i absolute(Vector vector) {
        Vector interactPoint = MapUtil.itemFrameOffset(vector, direction);
        BlockVector realBlockVector = MapUtil.toRealBlockVector(interactPoint);

        Vector relativeInteractPoint = realBlockVector.subtract(interactPoint);

        relativeInteractPoint.setX(Math.abs(relativeInteractPoint.getX()));
        relativeInteractPoint.setY(Math.abs(relativeInteractPoint.getY()));
        relativeInteractPoint.setZ(Math.abs(relativeInteractPoint.getZ()));

        int relativeX = 0;
        int relativeY = 0;
        switch (direction) {
            case NORTH -> {
                relativeX = 128 - (int) (relativeInteractPoint.getX() * 128);
                relativeY = 128 - (int) (relativeInteractPoint.getY() * 128);
            }
            case SOUTH -> {
                relativeX = (int) (relativeInteractPoint.getX() * 128);
                relativeY = 128 - (int) (relativeInteractPoint.getY() * 128);
            }
            case WEST -> {
                relativeX = (int) (relativeInteractPoint.getZ() * 128);
                relativeY = 128 - (int) (relativeInteractPoint.getY() * 128);
            }
            case EAST -> {
                relativeX = 128 - (int) (relativeInteractPoint.getZ() * 128);
                relativeY = 128 - (int) (relativeInteractPoint.getY() * 128);
            }
        }
        int[] absolute = addTileOffset(interactPoint, relativeX, relativeY);
        return new Vec2i(absolute[0], absolute[1]);
    }

    public int[] addTileOffset(Vector pos, int rx, int ry) {
        Vector offset = MapUtil.toRealBlockVector(switch (direction) {
            case EAST, NORTH, UP, DOWN -> max;
            case WEST, SOUTH -> min.clone().setY(max.getBlockY());
            default -> throw new IllegalArgumentException("Unknown direction - How did you get here?");
        }).subtract(MapUtil.toRealBlockVector(pos));

        MapUtil.absVector(offset);
        switch (direction) {
            case NORTH, SOUTH -> {
                return new int[]{offset.getBlockX() * 128 + rx, offset.getBlockY() * 128 + ry};
            }
            case EAST, WEST -> {
                return new int[]{offset.getBlockZ() * 128 + rx, offset.getBlockY() * 128 + ry};
            }
            case UP, DOWN -> {
                return new int[]{offset.getBlockX() * 128 + rx, offset.getBlockZ() * 128 + ry};
            }
            default -> throw new IllegalArgumentException("Unknown direction - How did you get here?");
        }
    }
}
