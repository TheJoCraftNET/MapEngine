package de.pianoman911.mapengine.api;

import de.pianoman911.mapengine.api.clientside.IDisplayProvider;
import de.pianoman911.mapengine.api.clientside.IHoldableDisplay;
import de.pianoman911.mapengine.api.clientside.IMapDisplay;
import de.pianoman911.mapengine.api.colors.IMapColors;
import de.pianoman911.mapengine.api.pipeline.IPipelineProvider;
import de.pianoman911.mapengine.api.util.MapTraceResult;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

/**
 * The main API class for the MapEngine plugin.
 */
public interface MapEngineApi {

    /**
     * @return the color palette used for converting
     * RGB colors to map colors and back.
     */
    IMapColors colors();

    /**
     * @return the pipeline provider used for creating stuff
     * related to pipelining and updating {@link IMapDisplay}'s
     */
    IPipelineProvider pipeline();

    /**
     * @return the display provider used for creating {@link IMapDisplay}'s
     * and {@link IHoldableDisplay}'s
     */
    IDisplayProvider displayProvider();

    /**
     * @return all currently active {@link IMapDisplay}'s
     */
    @Unmodifiable
    Set<IMapDisplay> mapDisplays();

    /**
     * @return all currently active {@link IHoldableDisplay}'s
     */
    @Unmodifiable
    Set<IHoldableDisplay> holdableDisplays();

    /**
     * Used to get a {@link IMapDisplay} in the view of the specified player.
     *
     * @param player      the player to check for
     * @param maxDistance the maximum ray distance to check for a display
     * @return the display in view or null if nothing was found
     * @deprecated use {@link #traceDisplayInView(Player, int)}
     */
    @Deprecated
    @Nullable
    IMapDisplay displayInView(Player player, int maxDistance);

    /**
     * Used to get a map display and the ray hit point in the view of a player.
     *
     * @param player      the player to check for
     * @param maxDistance the maximum ray distance to check for a display
     * @return the trace result or null if nothing was found
     */
    @Nullable
    MapTraceResult traceDisplayInView(Player player, int maxDistance);
}
