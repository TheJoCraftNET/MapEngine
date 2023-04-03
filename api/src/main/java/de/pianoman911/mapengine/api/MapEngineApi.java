package de.pianoman911.mapengine.api;

import de.pianoman911.mapengine.api.clientside.IDisplayProvider;
import de.pianoman911.mapengine.api.clientside.IMapDisplay;
import de.pianoman911.mapengine.api.colors.IMapColors;
import de.pianoman911.mapengine.api.pipeline.IPipelineProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.Set;

public interface MapEngineApi {

    IMapColors colors();

    IPipelineProvider pipeline();

    IDisplayProvider displayProvider();

    @Unmodifiable
    Set<IMapDisplay> displays();

    @Nullable
    IMapDisplay displayInView(Player player, int maxDistance);
}
