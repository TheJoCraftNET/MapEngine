package de.pianoman911.mapengine.api.clientside;

import de.pianoman911.mapengine.api.data.IMapUpdateData;
import de.pianoman911.mapengine.api.pipeline.IPipeline;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.util.BoundingBox;

/**
 * A map display which creates packet-level item frame
 * displays with a custom width and height.
 *
 * @see IDisplayProvider
 */
public interface IMapDisplay {

    /**
     * @return the display width (in blocks)
     */
    int width();

    /**
     * @return the display height (in blocks)
     */
    int height();

    /**
     * @return the display total width (in available pixels)
     */
    int pixelWidth();

    /**
     * @return the display total height (in available pixels)
     */
    int pixelHeight();

    /**
     * @return 2d-box of where the map displays are attached
     * @see #direction()
     */
    BoundingBox box();

    /**
     * @return where the {@link #box()} is facing at
     */
    BlockFace direction();

    /**
     * @return the direction at which the maps face
     */
    BlockFace visualDirection();

    /**
     * Spawns the map display for the given player at z-index 0.
     *
     * @param player the player who should receive the map display
     */
    default void spawn(Player player) {
        this.spawn(player, 0);
    }

    /**
     * Spawns the map display for the given player at the given z-index.
     *
     * @param player the player who should receive the map display
     * @param z      the z-index of the map content to spawn
     */
    void spawn(Player player, int z);

    /**
     * Removes the map display for the given player.
     *
     * @param player the player who should not see the map display any longer
     */
    void despawn(Player player);

    /**
     * Sets the z-layer map id group for the player.
     * <p>
     * Z-Layering is a feature that allows you to send different map content for the same map display.<br>
     * When changing the z-index for a specific player, the only thing which changes is the map ids.
     * This results in no additional data being required to be sent to the player and allows for
     * animations to animate smoothly, even with poor internet connection.
     *
     * @param player the player to set the z-layer map id group for
     * @param z      the z-layer map id group
     */
    void mapId(Player player, int z);

    /**
     * @deprecated internally used method
     */
    @Deprecated(forRemoval = true)
    default void update(Player player, IMapUpdateData[] data, boolean fullUpdate, int z, MapCursorCollection cursors) {
        this.update(player, data, z, cursors);
    }

    /**
     * @deprecated internally used method
     */
    @Deprecated(forRemoval = true)
    void update(Player player, IMapUpdateData[] data, int z, MapCursorCollection cursors);

    /**
     * The default {@link IPipeline} for this display.<br>
     * You can also use multiple own pipelines for different purposes with the same display.
     *
     * @return the default pipeline for this display
     */
    IPipeline pipeline();

    /**
     * Sets the entity rotation of the item frame.<br><br>
     * Warning: This breaks the visual click detection of the item frame.
     *
     * @param player the player to set the rotation for
     */
    void rotation(Player player, float yaw, float pitch);

    /**
     * Sets the visual direction of the item frame, by respawning them<br><br>
     * Warning: This breaks the visual click detection of the item frame.
     *
     * @param player          the player to set the visual direction for
     * @param visualDirection the visual direction
     */
    void visualDirection(Player player, BlockFace visualDirection);

    /**
     * Sets the visual direction of the item frame, by respawning them<br><br>
     * Warning: This breaks the visual click detection of the item frame.
     *
     * @param player          the player to set the visual direction for
     * @param visualDirection the visual direction
     * @param z               the z-layer map id group
     */
    void visualDirection(Player player, BlockFace visualDirection, int z);
}
