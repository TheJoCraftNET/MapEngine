package de.pianoman911.mapengine.api.clientside;

import de.pianoman911.mapengine.api.pipeline.IPipeline;
import org.bukkit.inventory.ItemStack;

/**
 * A holdable display is a single map with the dimensions of 128x128.<br>
 * This can be used for giving players a holdable map item.
 */
public interface IHoldableDisplay {

    /**
     * Creates an {@link ItemStack} with the correct map id set for the given z level.<br>
     * This should be used for giving e.g. {@link org.bukkit.entity.Player} this item into
     * their inventory for viewing.
     *
     * @param z the z level
     * @return the configured map {@link ItemStack}
     */
    ItemStack itemStack(int z);

    /**
     * The default {@link IPipeline} for this display.<br>
     *
     * @return the default pipeline for this display
     */
    IPipeline pipeline();
}
