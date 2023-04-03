package de.pianoman911.mapengine.api.event;

import de.pianoman911.mapengine.api.clientside.IMapDisplay;
import de.pianoman911.mapengine.api.util.MapClickType;
import de.pianoman911.mapengine.api.util.Vec2i;
import org.bukkit.entity.Player;

public class MapClickEvent extends MapEvent {

    private final MapClickType clickType;

    private final Player player;
    private final int x, y;

    public MapClickEvent(IMapDisplay display, MapClickType clickType, Player player, int x, int y) {
        super(display);
        this.clickType = clickType;
        this.player = player;
        this.x = x;
        this.y = y;
    }

    public MapClickType clickType() {
        return clickType;
    }

    public Player player() {
        return player;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public Vec2i asVec2i() {
        return Vec2i.of(x, y);
    }

    @Override
    public String toString() {
        return "MapClickEvent{" +
                "clickType=" + clickType +
                ", player=" + player.getName() +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
