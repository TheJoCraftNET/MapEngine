package de.pianoman911.mapengine.api.util;

import de.pianoman911.mapengine.api.clientside.IMapDisplay;
import org.bukkit.entity.Player;

/**
 * Used to hold the result of tracing a map display using {@link de.pianoman911.mapengine.api.MapEngineApi#traceDisplayInView(Player, int)}.
 *
 * @param viewPos the position of where the player would click the display
 * @param display the display that the player is looking at
 */
public record MapTraceResult(Vec2i viewPos, IMapDisplay display) {
}
