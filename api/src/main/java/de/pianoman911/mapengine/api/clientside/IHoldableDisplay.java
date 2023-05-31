package de.pianoman911.mapengine.api.clientside;

import de.pianoman911.mapengine.api.pipeline.IPipeline;
import org.bukkit.inventory.ItemStack;

public interface IHoldableDisplay {

    /**
     * A ItemStack with the right map id for the given z level.
     * So you can use this to give the player a display in his inventory.
     *
     * @param z The z level
     * @return The {@link ItemStack}
     */
    ItemStack itemStack(int z);

    /**
     * The default {@link IPipeline} for this display.<br>
     * You can also use multiple own pipelines for different purposes with the same display.
     *
     * @return The default pipeline for this display.
     */
    IPipeline pipeline();
}
