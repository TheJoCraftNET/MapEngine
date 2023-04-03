package de.pianoman911.mapengine.core.util;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;

public class RayTraceUtil {

    private static final double CLIP_THRESHOLD = 1e-7;
    private static final double FRAME_SHIT_OFFSET = 0.03125d; // <:mojank:897231139259973673>

    private static final double FRAME_HWIDTH = 0.375d;
    private static final double FRAME_HHEIGHT = 0.03125d;

    public static BoundingBox createFrameBBox(Vector frameLocation, BlockFace facing) {
        double centerX = frameLocation.getX() + (facing.getModX() != 0d ? facing.getModX() * FRAME_SHIT_OFFSET : 0.5d);
        double centerY = frameLocation.getY() + (facing.getModY() != 0d ? facing.getModY() * FRAME_SHIT_OFFSET : 0.5d);
        double centerZ = frameLocation.getZ() + (facing.getModZ() != 0d ? facing.getModZ() * FRAME_SHIT_OFFSET : 0.5d);

        double sizeX = FRAME_HWIDTH, sizeY = FRAME_HHEIGHT, sizeZ = FRAME_HWIDTH;
        if (facing.getModY() == 0d) { // neither top nor down, special handling
            sizeY = FRAME_HWIDTH;
            if (facing.getModX() == 0d) { // check if north/south
                sizeZ = FRAME_HHEIGHT;
            } else { // else assume east/west
                sizeX = FRAME_HHEIGHT;
            }
        }

        return new BoundingBox(centerX - sizeX, centerY - sizeY, centerZ - sizeZ, centerX + sizeX, centerY + sizeY, centerZ + sizeZ);
    }

    public static Location clipBox(Player player, BoundingBox targetBox, double maxDistance) {
        Location startLoc = player.getEyeLocation();
        Vector viewVec = startLoc.toVector();
        Vector endLoc = viewVec.clone();
        endLoc.add(startLoc.getDirection().multiply(maxDistance));

        // copied from AABB#clip(Vec3, Vec3) (mojang mappings)
        double[] distArr = new double[]{1d};
        double diffX = endLoc.getX() - startLoc.getX();
        double diffY = endLoc.getY() - startLoc.getY();
        double diffZ = endLoc.getZ() - startLoc.getZ();

        BlockFace clippedBs = null;
        if (diffX > CLIP_THRESHOLD) {
            clippedBs = clipPoint(distArr, null, diffX, diffY, diffZ, targetBox.getMinX(), targetBox.getMinY(),
                    targetBox.getMaxY(), targetBox.getMinZ(), targetBox.getMaxZ(), BlockFace.WEST,
                    startLoc.getX(), startLoc.getY(), startLoc.getZ());
        } else if (diffX < -CLIP_THRESHOLD) {
            clippedBs = clipPoint(distArr, null, diffX, diffY, diffZ, targetBox.getMaxX(), targetBox.getMinY(),
                    targetBox.getMaxY(), targetBox.getMinZ(), targetBox.getMaxZ(), BlockFace.EAST,
                    startLoc.getX(), startLoc.getY(), startLoc.getZ());
        }

        if (diffY > CLIP_THRESHOLD) {
            clippedBs = clipPoint(distArr, clippedBs, diffY, diffZ, diffX, targetBox.getMinY(), targetBox.getMinZ(),
                    targetBox.getMaxZ(), targetBox.getMinX(), targetBox.getMaxX(), BlockFace.DOWN,
                    startLoc.getY(), startLoc.getZ(), startLoc.getX());
        } else if (diffY < -CLIP_THRESHOLD) {
            clippedBs = clipPoint(distArr, clippedBs, diffY, diffZ, diffX, targetBox.getMaxY(), targetBox.getMinZ(),
                    targetBox.getMaxZ(), targetBox.getMinX(), targetBox.getMaxX(), BlockFace.UP,
                    startLoc.getY(), startLoc.getZ(), startLoc.getX());
        }

        if (diffZ > CLIP_THRESHOLD) {
            clippedBs = clipPoint(distArr, clippedBs, diffZ, diffX, diffY, targetBox.getMinZ(), targetBox.getMinX(),
                    targetBox.getMaxX(), targetBox.getMinY(), targetBox.getMaxY(), BlockFace.NORTH,
                    startLoc.getZ(), startLoc.getX(), startLoc.getY());
        } else if (diffZ < -CLIP_THRESHOLD) {
            clippedBs = clipPoint(distArr, clippedBs, diffZ, diffX, diffY, targetBox.getMaxZ(), targetBox.getMinX(),
                    targetBox.getMaxX(), targetBox.getMinY(), targetBox.getMaxY(), BlockFace.SOUTH,
                    startLoc.getZ(), startLoc.getX(), startLoc.getY());
        }

        if (clippedBs == null) {
            return null;
        }

        double dist = distArr[0];
        return startLoc.clone().add(dist * diffX, dist * diffY, dist * diffZ);
    }

    // idk what the fuck this does, i just pressed CTRL+C and CTRL+V (AABB#clipPoint)
    private static BlockFace clipPoint(double[] distArr, BlockFace face, double d, double e, double f, double g, double h, double i, double j, double k, BlockFace direction2, double l, double m, double n) {
        double dist = (g - l) / d;
        double p = m + dist * e;
        double q = n + dist * f;

        if (0d < dist && dist < distArr[0] && h - CLIP_THRESHOLD < p && p < i + CLIP_THRESHOLD && j - CLIP_THRESHOLD < q && q < k + CLIP_THRESHOLD) {
            distArr[0] = dist;
            return direction2;
        }
        return face;
    }
}
