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

public interface MapEngineApi {

    IMapColors colors();

    IPipelineProvider pipeline();

    IDisplayProvider displayProvider();

    @Unmodifiable
    Set<IMapDisplay> mapDisplays();

    @Unmodifiable
    Set<IHoldableDisplay> holdableDisplays();

    @Deprecated
    @Nullable
    IMapDisplay displayInView(Player player, int maxDistance);

    @Nullable
    MapTraceResult traceDisplayInView(Player player, int maxDistance);
}
