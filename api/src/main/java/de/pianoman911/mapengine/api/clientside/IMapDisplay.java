package de.pianoman911.mapengine.api.clientside;

import de.pianoman911.mapengine.api.data.IMapUpdateData;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.util.BoundingBox;
import org.jetbrains.annotations.Range;

/**
 * A map display which creates packet-level item frame
 * displays with a custom width and height.
 *
 * @see IDisplayProvider
 */
public interface IMapDisplay extends IDisplay {

    /**
     * @return the display width (in blocks)
     */
    int width();

    /**
     * @return the display height (in blocks)
     */
    int height();

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
     * @return the distance at which the player can interact with the map display
     */
    double interactDistance();

    /**
     * Sets the distance at which the player can interact with the map display.<br>
     * The default distance is at 6 blocks.
     * <p>
     * <strong>WARNING: Distances above 6 (creative) or 3 (other gamemodes) are only detected
     * by an attacking (left-click) interaction.</strong>
     * @param interactDistance the distance at which the player can interact with the map display
     */
    void interactDistance(double interactDistance);

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
     * Sets the entity rotation of the item frame.<br><br>
     * Warning: This breaks the visual click detection of the item frame.
     *
     * @param player the player to set the rotation for
     */
    void rotation(Player player, float yaw, float pitch);

    /**
     * Sets the item rotation of the item frame.
     * It's like right-clicking the item frame.<br><br>
     * Warning: This breaks the visual click detection of the item frame.
     *
     * @param player   the player to set the rotation for
     * @param rotation the rotation (0-7)
     */
    void itemRotation(Player player, @Range(from = 0, to = 7) int rotation);

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

    /**
     * Clones the map ids from the given display.<br>
     * This can be used to "clone" the content of a display to another display.
     * This process will save traffic by only sending the content once.<br>
     * The cloning is active until {@link #cutOffCloneGroupIds()} is called.<br><br>
     *
     * <strong>This display must have the same dimensions as the target display.
     * After cloning the ids, it's necessary to call {@link #mapId(Player, int)} for all players
     * to update the map ids.
     * </strong>
     *
     * @param source the display to clone the group ids from
     */
    void cloneGroupIds(IMapDisplay source);

    /**
     * Cuts off the clone group ids.<br><br>
     *
     * <strong>After cutting off the ids, it's necessary to call {@link #mapId(Player, int)} for all players
     * to update the map ids.</strong>
     */
    void cutOffCloneGroupIds();
}
