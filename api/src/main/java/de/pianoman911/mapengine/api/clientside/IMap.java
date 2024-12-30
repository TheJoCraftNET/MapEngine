package de.pianoman911.mapengine.api.clientside;

import it.unimi.dsi.fastutil.ints.Int2IntMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Unmodifiable;

/**
 * Abstract representation for vanilla filled maps, used for {@link IHoldableDisplay} and
 * for multiple {@link IFrame} in {@link IMapDisplay}.
 * The main purpose of this is to provide the vanilla map ids per z level.
 * <p>
 * <b>Note: Maps only exist on the network level,
 * so the server doesn't know about this map id and content.
 *
 * @see IMapDisplay#mapId(Player, int) for more info about z levels
 */
public interface IMap {

    /**
     * Maps the z level to a map id.
     *
     * @param z the z level
     * @return the map id
     * @see IMapDisplay#mapId(Player, int) for more info about z levels
     */
    int mapId(int z);

    /**
     * @return an unmodifiable map of all map ids
     */
    @Unmodifiable
    Int2IntMap mapIds();

    /**
     * Creates an {@link ItemStack} with the correct map id set for the given z level.<br>
     * This should be used for giving e.g. {@link org.bukkit.entity.Player} this item into
     * their inventory for viewing.
     *
     * @param z the z level
     * @return the configured map {@link ItemStack}
     */
    ItemStack itemStack(int z);
}
