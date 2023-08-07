package de.pianoman911.mapengine.core.util;

import de.pianoman911.mapengine.api.util.Vec2i;
import de.pianoman911.mapengine.core.clientside.FrameContainer;
import it.unimi.dsi.fastutil.Pair;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

public class MapUtil {

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

    public static Vec2i calculateClickPosition(Player player, FrameContainer map, double maxDistance) {
        Pair<Vector, BlockFace> clipped = RayTraceUtil.clipBox(player, map.interactionBox(), maxDistance);
        if (clipped == null || clipped.second() != map.direction()) {
            return null;
        }

        Vector clickedPos = clipped.left().subtract(map.interactionBox().getCenter().setY(map.box().getMinY()));
        double posX = clickedPos.getX();
        double posY = clickedPos.getY();
        double posZ = clickedPos.getZ();

        int x;
        int y;

        switch (map.direction()) {
            case EAST -> {
                x = (int) ((map.width() - (posZ + map.width() / 2.0)) * 128);
                y = (int) ((map.height() - posY) * 128);
            }
            case WEST -> {
                x = (int) ((posZ + map.width() / 2.0) * 128);
                y = (int) ((map.height() - posY) * 128);
            }
            case SOUTH -> {
                x = (int) ((posX + map.width() / 2.0) * 128);
                y = (int) ((map.height() - posY) * 128);
            }
            case NORTH -> {
                x = (int) ((map.width() - (posX + map.width() / 2.0)) * 128);
                y = (int) ((map.height() - posY) * 128);
            }
            case UP -> {
                x = (int) ((posX + map.width() / 2.0) * 128);
                y = (int) ((posZ + map.height() / 2.0) * 128);
            }
            case DOWN -> {
                x = (int) ((posX + map.width() / 2.0) * 128);
                y = (int) ((map.height() - (posZ + map.height() / 2.0)) * 128);
            }

            default -> throw new UnsupportedOperationException("Unsupported direction: " + map.direction());
        }
        return new Vec2i(x, y);
    }
}
