package de.pianoman911.mapengine.api.event;

import de.pianoman911.mapengine.api.clientside.IMapDisplay;
import de.pianoman911.mapengine.api.util.MapClickType;
import de.pianoman911.mapengine.api.util.Vec2i;
import org.bukkit.entity.Player;

/**
 * Represents a click on a map done by a player.
 */
public class MapClickEvent extends MapEvent {

    private final MapClickType clickType;

    private final Player player;
    private final Vec2i clickPos;

    @Deprecated
    public MapClickEvent(IMapDisplay display, MapClickType clickType, Player player, int x, int y) {
        this(display, clickType, player, Vec2i.of(x, y));
    }

    public MapClickEvent(IMapDisplay display, MapClickType clickType, Player player, Vec2i clickPos) {
        super(display);
        this.clickType = clickType;
        this.player = player;
        this.clickPos = clickPos;
    }

    /**
     * @return the interaction type used by the player
     */
    public MapClickType clickType() {
        return clickType;
    }

    /**
     * @return the player doing the click
     */
    public Player player() {
        return player;
    }

    /**
     * @return the clicked x-position in pixels
     */
    public int x() {
        return clickPos.x();
    }

    /**
     * @return the clicked y-position in pixels
     */
    public int y() {
        return clickPos.y();
    }

    /**
     * @return the clicked position as an {@link Vec2i}
     */
    public Vec2i asVec2i() {
        return clickPos;
    }

    @Override
    public String toString() {
        return "MapClickEvent{" +
                "clickType=" + clickType +
                ", player=" + player +
                ", clickPos=" + clickPos +
                '}';
    }
}
