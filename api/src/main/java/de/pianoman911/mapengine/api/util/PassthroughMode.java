package de.pianoman911.mapengine.api.util;

/**
 * Specifies which type of packet should be passed to the server,
 * when a player interacts with a map.
 */
public enum PassthroughMode {

    /**
     * All packets will be cancelled,
     * no interaction events will be fired.
     */
    NONE,
    /**
     * Only the swing animation will be visible to other players,
     * no interaction events are fired.
     * <br>
     * <strong>WARNING: MapEngine will trigger the swing animation on its own,
     * otherwise interaction events will be fired by bukkit.
     * </strong>
     */
    ONLY_ANIMATION,
    /**
     * All packets will be passed to the server,
     * interaction events will be fired.
     */
    ALL,
}
