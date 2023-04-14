package de.pianoman911.mapengine.core.platform;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import de.pianoman911.mapengine.api.event.MapClickEvent;
import de.pianoman911.mapengine.api.util.MapClickType;
import de.pianoman911.mapengine.common.platform.IListenerBridge;
import de.pianoman911.mapengine.core.MapEnginePlugin;
import de.pianoman911.mapengine.core.clientside.Frame;
import de.pianoman911.mapengine.core.clientside.FrameContainer;
import de.pianoman911.mapengine.core.util.RayTraceUtil;
import it.unimi.dsi.fastutil.Pair;
import org.bukkit.GameMode;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

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
        Pair<Vector, BlockFace> clipped = RayTraceUtil.clipBox(player, map.interactionBox(),
                player.getGameMode() == GameMode.CREATIVE ? 6f : 3f);
        if (clipped == null || clipped.second() != map.direction()) {
            return;
        }

        Vector clickedPos = clipped.left().subtract(map.interactionBox().getCenter().setY(map.box().getMinY()));
        handleData(player, map, clickedPos.getX(), clickedPos.getY(), clickedPos.getZ(), type);
    }

    private void handleData(Player player, FrameContainer map, double posX, double posY, double posZ, MapClickType type) {
        int x;
        int y;

        switch (map.direction()) {
            case EAST -> {
                x = (int) ((map.width() - (posZ + map.width() / 2.0)) * 128);
                y = (int) ((map.height() - posY) * 128);
            }
            case WEST -> {
                x = (int) ((posZ + map.width() / 2.0) * 128);
                y = (int) ((map.height() - posY) * 128);
            }
            case SOUTH -> {
                x = (int) ((posX + map.width() / 2.0) * 128);
                y = (int) ((map.height() - posY) * 128);
            }
            case NORTH -> {
                x = (int) ((map.width() - (posX + map.width() / 2.0)) * 128);
                y = (int) ((map.height() - posY) * 128);
            }

            default -> throw new UnsupportedOperationException("Unsupported direction: " + map.direction());
        }
        new MapClickEvent(map, type, player, x, y).callEvent();
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
}
