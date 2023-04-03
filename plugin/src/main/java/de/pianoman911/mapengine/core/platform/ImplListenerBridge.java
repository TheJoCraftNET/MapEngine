package de.pianoman911.mapengine.core.platform;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.pianoman911.mapengine.api.event.MapClickEvent;
import de.pianoman911.mapengine.api.util.MapClickType;
import de.pianoman911.mapengine.api.util.Vec2i;
import de.pianoman911.mapengine.common.platform.IListenerBridge;
import de.pianoman911.mapengine.core.MapEnginePlugin;
import de.pianoman911.mapengine.core.clientside.FrameContainer;
import de.pianoman911.mapengine.core.util.RayTraceUtil;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Objects;
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

    private void handleUnspecificPosition(Player player, int entityId, MapClickType type) {
        FrameContainer map = this.checkMap(player, entityId);
        if (map == null) {
            return;
        }

        Vector framePos = Objects.requireNonNull(map.locOfFrame(entityId));
        Location clipped = RayTraceUtil.clipBox(player, RayTraceUtil.createFrameBBox(framePos, map.direction()),
                player.getGameMode() == GameMode.CREATIVE ? 6f : 3f);
        if (clipped == null) {
            return;
        }

        Vec2i absolutePos = map.absolute(clipped.toVector());
        new MapClickEvent(map, type, player, absolutePos.x(), absolutePos.y()).callEvent();
    }

    @Override
    public void handleInteract(Player player, int entityId) {
        this.handleUnspecificPosition(player, entityId, MapClickType.RIGHT_CLICK);
    }

    @Override
    public void handleInteract(Player player, int entityId, double posX, double posY, double posZ) {
        FrameContainer map = this.checkMap(player, entityId);
        if (map == null) {
            return;
        }

        Vector framePos = Objects.requireNonNull(map.locOfFrame(entityId));
        Vector relativeInteractPoint = new Vector((posX + 0.375d) / 0.75d, (posY + 0.375d) / 0.75d, (posZ + 0.375d) / 0.75d);

        int relativeX = 0, relativeY = 0;
        switch (map.direction()) {
            case NORTH -> {
                relativeX = 96 - (int) (relativeInteractPoint.getX() * 96);
                relativeY = 96 - (int) (relativeInteractPoint.getY() * 96);
            }
            case SOUTH -> {
                relativeX = (int) (relativeInteractPoint.getX() * 96);
                relativeY = 96 - (int) (relativeInteractPoint.getY() * 96);
            }
            case WEST -> {
                relativeX = (int) (relativeInteractPoint.getZ() * 96);
                relativeY = 96 - (int) (relativeInteractPoint.getY() * 96);
            }
            case EAST -> {
                relativeX = 96 - (int) (relativeInteractPoint.getZ() * 96);
                relativeY = 96 - (int) (relativeInteractPoint.getY() * 96);
            }
            case UP, DOWN -> {
            }
        }

        relativeX += 16;
        relativeY += 16;

        int[] absolute = map.addTileOffset(framePos, relativeX, relativeY);
        new MapClickEvent(map, MapClickType.RIGHT_CLICK, player, absolute[0], absolute[1]).callEvent();
    }

    @Override
    public void handleAttack(Player player, int entityId) {
        this.handleUnspecificPosition(player, entityId, MapClickType.LEFT_CLICK);
    }
}
