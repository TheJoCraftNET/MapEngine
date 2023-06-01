package de.pianoman911.mapengine.api.clientside;

import de.pianoman911.mapengine.api.pipeline.IPipeline;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BlockVector;

/**
 * Manager interface for creating all sorts of map displays.
 * <p>
 * All "raw" creation methods require creating a pipeline using the
 * {@link de.pianoman911.mapengine.api.pipeline.IPipelineProvider}.
 */
public interface IDisplayProvider {

    /**
     * The a and b points should build a 2d box.
     *
     * @param a         the first position for the item frame box
     * @param b         the second position for the item frame bos
     * @param direction the direction used for interactions and in which the map faces
     * @param pipeline  the pipeline used for creating the display
     * @return a new map display instance with the specified pipeline
     */
    default IMapDisplay createRawPipelineDisplay(BlockVector a, BlockVector b,
                                                 BlockFace direction, IPipeline pipeline) {
        return this.createRawPipelineDisplay(a, b, direction, direction, pipeline);
    }

    /**
     * The a and b points should build a 2d box.
     *
     * @param a               the first position for the item frame box
     * @param b               the second position for the item frame bos
     * @param direction       the interaction direction
     * @param visualDirection the direction in which the map faces
     * @param pipeline        the pipeline used for creating the display
     * @return a new map display instance with the specified pipeline
     */
    IMapDisplay createRawPipelineDisplay(BlockVector a, BlockVector b, BlockFace direction,
                                         BlockFace visualDirection, IPipeline pipeline);

    /**
     * The a and b points should build a 2d box.
     *
     * @param a         the first position for the item frame box
     * @param b         the second position for the item frame bos
     * @param direction the direction used for interactions and in which the map faces
     * @return a new map display instance with a default pipeline
     */
    default IMapDisplay createBasic(BlockVector a, BlockVector b, BlockFace direction) {
        return this.createBasic(a, b, direction, direction);
    }

    /**
     * The a and b points should build a 2d box.
     *
     * @param a               the first position for the item frame box
     * @param b               the second position for the item frame bos
     * @param direction       the interaction direction
     * @param visualDirection the direction in which the map faces
     * @return a new map display instance with a default pipeline
     */
    IMapDisplay createBasic(BlockVector a, BlockVector b, BlockFace direction, BlockFace visualDirection);

    /**
     * @param pipeline the pipeline used for creating the display
     * @return a new holdable display using the specified pipeline
     */
    IHoldableDisplay createRawPipelineHoldableDisplay(IPipeline pipeline);

    /**
     * @return a new holdable display with a default pipeline
     */
    IHoldableDisplay createHoldableDisplay();
}
