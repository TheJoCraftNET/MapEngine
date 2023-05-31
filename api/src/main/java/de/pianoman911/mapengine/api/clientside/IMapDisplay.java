package de.pianoman911.mapengine.api.clientside;

import de.pianoman911.mapengine.api.data.IMapUpdateData;
import de.pianoman911.mapengine.api.pipeline.IPipeline;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.util.BoundingBox;

public interface IMapDisplay {

    int width();

    int height();

    int pixelWidth();

    int pixelHeight();

    BoundingBox box();

    BlockFace direction();

    void spawn(Player player);

    void despawn(Player player);

    /**
     * Sets the z-layer map id group for the player.
     * <p>
     * Z-Layering is a feature that allows you to send different map ids to the same item frame.
     * Trough this you can switch between different map ids via the z coordinates.<br><br>
     * The Content of the map switches when the z coordinate changes, but the client caches the other content with the given map ids.
     * Therefore, you can use this for switching content without sending the full map data every time.<br><br>
     * This is especially useful for animations. You can send the full map data once and then switch between the
     * different frames via the z coordinate. This way you save a lot of bandwidth.
     *
     * @param player The player to set the z-layer map id group for.
     * @param z      The z-layer map id group.
     */
    void mapId(Player player, int z);

    @Deprecated
    default void update(Player player, IMapUpdateData[] data, boolean fullUpdate, int z, MapCursorCollection cursors){
        update(player, data, z, cursors);
    }

    void update(Player player, IMapUpdateData[] data, int z, MapCursorCollection cursors);

    IPipeline pipeline();

    void rotation(Player player, float yaw, float pitch);

    void visualDirection(Player player, BlockFace visualDirection);

    void visualDirection(Player player, BlockFace visualDirection, int z);
}
