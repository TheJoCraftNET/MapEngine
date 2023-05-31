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
     * Z-Layering is a feature that allows you to send different map content for the same map display.<br>
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
    default void update(Player player, IMapUpdateData[] data, boolean fullUpdate, int z, MapCursorCollection cursors) {
        update(player, data, z, cursors);
    }

    void update(Player player, IMapUpdateData[] data, int z, MapCursorCollection cursors);

    /**
     * The default {@link IPipeline} for this display.<br>
     * You can also use multiple own pipelines for different purposes with the same display.
     *
     * @return The default pipeline for this display.
     */
    IPipeline pipeline();

    /**
     * Sets the entity rotation of the item frame.<br><br>
     * Warning: This breaks the visual click detection of the item frame.
     *
     * @param player The player to set the rotation for.
     * @param yaw    The yaw.
     * @param pitch  The pitch.
     */
    void rotation(Player player, float yaw, float pitch);

    /**
     * Sets the visual direction of the item frame, by respawning them<br><br>
     * Warning: This breaks the visual click detection of the item frame.
     *
     * @param player          The player to set the visual direction for.
     * @param visualDirection The visual direction.
     */
    void visualDirection(Player player, BlockFace visualDirection);

    /**
     * Sets the visual direction of the item frame, by respawning them<br><br>
     * Warning: This breaks the visual click detection of the item frame.
     *
     * @param player          The player to set the visual direction for.
     * @param visualDirection The visual direction.
     * @param z               The z-layer map id group.
     */
    void visualDirection(Player player, BlockFace visualDirection, int z);
}
