package de.pianoman911.mapengine.core.util;

import de.pianoman911.mapengine.api.util.Vec2i;
import de.pianoman911.mapengine.core.clientside.FrameContainer;
import it.unimi.dsi.fastutil.Pair;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;

public final class MapUtil {

    public static final int MAP_WIDTH = 128;
    public static final int MAP_HEIGHT = 128;
    public static final int MAP_PIXEL_COUNT = MAP_WIDTH * MAP_HEIGHT;

    private MapUtil() {
    }

    public static Vector itemFrameOffset(Vector vector, BlockFace direction) {
        vector = vector.clone();
        switch (direction) {
            case NORTH -> vector.setZ(vector.getZ() - 1);
            case SOUTH -> vector.setZ(vector.getZ() + 1);
            case WEST -> vector.setX(vector.getX() - 1);
            case EAST -> vector.setX(vector.getX() + 1);
            case UP -> vector.setY(vector.getY() + 1);
            case DOWN -> vector.setY(vector.getY() - 1);
        }
        return vector;
    }

    public static BlockVector toRealBlockVector(Vector vector) {
        return new BlockVector(vector.getBlockX(), vector.getBlockY(), vector.getBlockZ());
    }

    public static <T extends Vector> T absVector(T vector) {
        vector.setX(Math.abs(vector.getX()));
        vector.setY(Math.abs(vector.getY()));
        vector.setZ(Math.abs(vector.getZ()));
        return vector;
    }

    @Nullable
    public static Pair<Vector, Vec2i> calculateClickPosition(Player player, FrameContainer map, double maxDistance) {
        Pair<Vector, BlockFace> clipped = RayTraceUtil.clipBox(player, map.interactionBox(), maxDistance);
        if (clipped == null || clipped.second() != map.direction()) {
            return null;
        }

        Vector clickedPos = clipped.left().clone().subtract(map.interactionBox().getCenter().setY(map.box().getMinY()));
        double posX = clickedPos.getX();
        double posY = clickedPos.getY();
        double posZ = clickedPos.getZ();

        int x;
        int y;

        switch (map.direction()) {
            case EAST -> {
                x = (int) ((map.width() - (posZ + map.width() / 2.0)) * MAP_WIDTH);
                y = (int) ((map.height() - posY) * MAP_HEIGHT);
            }
            case WEST -> {
                x = (int) ((posZ + map.width() / 2.0) * MAP_WIDTH);
                y = (int) ((map.height() - posY) * MAP_HEIGHT);
            }
            case SOUTH -> {
                x = (int) ((posX + map.width() / 2.0) * MAP_WIDTH);
                y = (int) ((map.height() - posY) * MAP_HEIGHT);
            }
            case NORTH -> {
                x = (int) ((map.width() - (posX + map.width() / 2.0)) * MAP_WIDTH);
                y = (int) ((map.height() - posY) * MAP_HEIGHT);
            }
            case UP -> {
                x = (int) ((posX + map.width() / 2.0) * MAP_WIDTH);
                y = (int) ((posZ + map.height() / 2.0) * MAP_HEIGHT);
            }
            case DOWN -> {
                x = (int) ((posX + map.width() / 2.0) * MAP_WIDTH);
                y = (int) ((map.height() - (posZ + map.height() / 2.0)) * MAP_HEIGHT);
            }

            default -> throw new UnsupportedOperationException("Unsupported direction: " + map.direction());
        }
        return Pair.of(clipped.left(), new Vec2i(x, y));
    }
}
