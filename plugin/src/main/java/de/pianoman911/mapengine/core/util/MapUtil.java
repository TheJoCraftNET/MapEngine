package de.pianoman911.mapengine.core.util;

import org.bukkit.block.BlockFace;
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
}
