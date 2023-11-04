package de.pianoman911.mapengine.api.event;

import de.pianoman911.mapengine.api.clientside.IMapDisplay;
import de.pianoman911.mapengine.api.util.MapClickType;
import de.pianoman911.mapengine.api.util.Vec2i;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Represents a click on a map done by a player.
 */
public class MapClickEvent extends MapEvent {

    private final MapClickType clickType;

    private final Player player;
    private final Vec2i clickPos;
    private final Location worldPos;
    private final double interactDistance;

    public MapClickEvent(IMapDisplay display, MapClickType clickType, Player player, Vec2i clickPos, Location worldPos, double interactDistance) {
        super(display);
        this.clickType = clickType;
        this.player = player;
        this.clickPos = clickPos;
        this.worldPos = worldPos;
        this.interactDistance = interactDistance;
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

    /**
     * @return the clicked position in the world as a {@link Location}
     */
    public Location worldPos() {
        return worldPos;
    }

    /**
     * @return the distance at which the player clicked the map
     */
    public double interactDistance() {
        return interactDistance;
    }

    @Override
    public String toString() {
        return "MapClickEvent{" +
                "clickType=" + clickType +
                ", player=" + player +
                ", worldPos=" + clickPos +
                '}';
    }
}
