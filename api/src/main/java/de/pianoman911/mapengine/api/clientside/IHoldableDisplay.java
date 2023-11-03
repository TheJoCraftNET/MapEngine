package de.pianoman911.mapengine.api.clientside;

import org.bukkit.inventory.ItemStack;

/**
 * A holdable display is a single map with the dimensions of 128x128.<br>
 * This can be used for giving players a holdable map item.
 */
public interface IHoldableDisplay extends IDisplay {

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
