package de.pianoman911.mapengine.core.platform;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.pianoman911.mapengine.api.event.MapClickEvent;
import de.pianoman911.mapengine.api.util.MapClickType;
import de.pianoman911.mapengine.api.util.MapTraceResult;
import de.pianoman911.mapengine.api.util.PassthroughMode;
import de.pianoman911.mapengine.api.util.Vec2i;
import de.pianoman911.mapengine.common.platform.IListenerBridge;
import de.pianoman911.mapengine.core.MapEnginePlugin;
import de.pianoman911.mapengine.core.clientside.Frame;
import de.pianoman911.mapengine.core.clientside.FrameContainer;
import de.pianoman911.mapengine.core.util.MapUtil;
import it.unimi.dsi.fastutil.Pair;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public final class ImplListenerBridge implements IListenerBridge {

    // prevents spamming of clicks on the same map
    private static final Cache<Player, PassthroughMode> CLICKED = CacheBuilder.newBuilder()
            .expireAfterWrite(100, TimeUnit.MILLISECONDS) // 4 ticks click delay
            .weakKeys().build();

    private final MapEnginePlugin plugin;

    public ImplListenerBridge(MapEnginePlugin plugin) {
        this.plugin = plugin;
    }

    private FrameContainer checkMap(Player player, int entityId) {
        return this.plugin.mapManager().display(entityId);
    }

    private PassthroughMode getLastPacketMode(Player player) {
        return CLICKED.getIfPresent(player);
    }

    private PassthroughMode handleRawClick(Player player, int entityId, Function<FrameContainer, PassthroughMode> handler) {
        PassthroughMode last = getLastPacketMode(player);
        if (last == null) {
            FrameContainer map = checkMap(player, entityId);
            if (map != null) {
                last = handler.apply(map);
                if (last != null) {
                    CLICKED.put(player, last == PassthroughMode.ALL ? PassthroughMode.ALL : PassthroughMode.NONE); // only passthroughs the animation once
                }
            }
        }
        return last;
    }

    private PassthroughMode executeAtExactPosition(Player player, FrameContainer map, MapClickType type) {
        Pair<Vector, Vec2i> clickPos = MapUtil.calculateClickPosition(player, map, map.interactDistance());

        if (clickPos != null) {
            return callClickEvent(player, map, type, clickPos.right(), clickPos.left());
        }
        return null;
    }

    private PassthroughMode callClickEvent(Player player, FrameContainer map, MapClickType type, Vec2i clickPos, Vector interactionPos) {
        Location worldPos = interactionPos.toLocation(player.getWorld());
        return callClickEvent(player, map, type, clickPos, worldPos);
    }

    private PassthroughMode callClickEvent(Player player, FrameContainer map, MapClickType type, Vec2i clickPos, Location worldPos) {
        double interactDistance = worldPos.distance(player.getEyeLocation());
        MapClickEvent clickEvent = new MapClickEvent(map, type, player, clickPos, worldPos, interactDistance);
        clickEvent.callEvent();
        return clickEvent.passthroughMode();
    }

    @Override
    public PassthroughMode handleInteract(Player player, int entityId, double posX, double posY, double posZ) {
        return this.handleRawClick(player, entityId,
                map -> {
                    // Only handle clicks on the right side of the box
                    if (switch (map.direction()) {
                        case EAST -> posX >= 0.5 - Frame.INTERACTION_OFFSET;
                        case WEST -> posX <= -0.5 + Frame.INTERACTION_OFFSET;
                        case SOUTH -> posZ >= 0.5 - Frame.INTERACTION_OFFSET;
                        case NORTH -> posZ <= -0.5 + Frame.INTERACTION_OFFSET;
                        case UP -> posY >= 1 - Frame.INTERACTION_OFFSET;
                        case DOWN -> posY <= 0 + Frame.INTERACTION_OFFSET;
                        default -> throw new UnsupportedOperationException("Unsupported direction: " + map.direction());
                    }) {
                        return executeAtExactPosition(player, map, MapClickType.RIGHT_CLICK);
                    }
                    return null;
                });
    }

    @Override
    public PassthroughMode handleAttack(Player player, int entityId) {
        return this.handleRawClick(player, entityId,
                map -> this.executeAtExactPosition(player, map, MapClickType.LEFT_CLICK));
    }

    @Override
    public PassthroughMode handleSwing(Player player) {
        PassthroughMode lastMode = getLastPacketMode(player);
        if (lastMode != null) {
            return lastMode;
        }

        MapTraceResult result = this.plugin.mapManager().traceDisplayInView(player);
        if (result == null) {
            return null;
        }
        PassthroughMode passthroughMode = callClickEvent(player, (FrameContainer) result.display(),
                MapClickType.LEFT_CLICK, result.viewPos(), result.worldPos());
        CLICKED.put(player, PassthroughMode.NONE); // only passthroughs the animation once

        return passthroughMode;
    }
}
