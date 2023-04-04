package de.pianoman911.mapengine.api.clientside;

import de.pianoman911.mapengine.api.pipeline.IPipeline;
import org.bukkit.inventory.ItemStack;

public interface IHoldableDisplay {

    ItemStack itemStack(int z);

    IPipeline pipeline();
}
