package de.pianoman911.mapengine.api.clientside;

import de.pianoman911.mapengine.api.pipeline.IPipeline;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BlockVector;

public interface IDisplayProvider {

    IMapDisplay createRawPipelineDisplay(BlockVector a, BlockVector b, BlockFace direction, IPipeline pipeline);

    IMapDisplay createBasic(BlockVector a, BlockVector b, BlockFace direction);
}
