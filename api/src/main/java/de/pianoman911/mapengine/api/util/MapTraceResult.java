package de.pianoman911.mapengine.api.util;

import de.pianoman911.mapengine.api.clientside.IMapDisplay;

public record MapTraceResult(Vec2i viewPos, IMapDisplay display) {
}
