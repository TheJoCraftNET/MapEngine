package de.pianoman911.mapengine.core.platform;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.pianoman911.mapengine.api.event.MapClickEvent;
import de.pianoman911.mapengine.api.util.MapClickType;
import de.pianoman911.mapengine.api.util.MapTraceResult;
import de.pianoman911.mapengine.api.util.Vec2i;
import de.pianoman911.mapengine.common.platform.IListenerBridge;
import de.pianoman911.mapengine.core.MapEnginePlugin;
import de.pianoman911.mapengine.core.clientside.Frame;
import de.pianoman911.mapengine.core.clientside.FrameContainer;
import de.pianoman911.mapengine.core.util.MapUtil;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

public final class ImplListenerBridge implements IListenerBridge {

    // prevents spamming of clicks on the same map
    private static final Cache<Player, Long> CLICKED = CacheBuilder.newBuilder()
            .expireAfterWrite(100, TimeUnit.MILLISECONDS)
            .weakKeys().build();

    private final MapEnginePlugin plugin;

    public ImplListenerBridge(MapEnginePlugin plugin) {
        this.plugin = plugin;
    }

    private FrameContainer checkMap(Player player, int entityId) {
        FrameContainer map = this.plugin.mapManager().display(entityId);
        if (map == null || CLICKED.getIfPresent(player) != null) {
            return null;
        }

        // insert dummy value, doesn't matter
        CLICKED.put(player, 0L);
        return map;
    }

    private void executeAtExactPosition(Player player, FrameContainer map, MapClickType type) {
        Vec2i clickPos = MapUtil.calculateClickPosition(player, map, map.interactDistance());

        if (clickPos != null) {
            new MapClickEvent(map, type, player, clickPos).callEvent();
        }
    }

    @Override
    public void handleInteract(Player player, int entityId, double posX, double posY, double posZ) {
        FrameContainer map = this.checkMap(player, entityId);
        if (map == null) {
            return;
        }

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
            executeAtExactPosition(player, map, MapClickType.RIGHT_CLICK);
        }
    }

    @Override
    public void handleAttack(Player player, int entityId) {
        FrameContainer map = this.checkMap(player, entityId);
        if (map == null) {
            return;
        }
        this.executeAtExactPosition(player, map, MapClickType.LEFT_CLICK);
    }

    @Override
    public void handleSwing(Player player) {
        MapTraceResult result = this.plugin.mapManager().traceDisplayInView(player);
        if (result == null || CLICKED.getIfPresent(player) != null) {
            return;
        }

        // insert dummy value, doesn't matter
        CLICKED.put(player, 0L);
        new MapClickEvent(result.display(), MapClickType.LEFT_CLICK, player, result.viewPos()).callEvent();
    }
}
