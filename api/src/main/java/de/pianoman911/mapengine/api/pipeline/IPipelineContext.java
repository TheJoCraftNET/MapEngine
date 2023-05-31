package de.pianoman911.mapengine.api.pipeline;

import de.pianoman911.mapengine.api.clientside.IMapDisplay;
import de.pianoman911.mapengine.api.util.Converter;
import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCursorCollection;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

/**
 * This interface is used to pass data between {@link IPipelineNode}s,
 * it is created for every flush and is passed to every node.
 * <p>
 * <strong>WARNING: This context is not thread-safe and should only be used on a single thread</strong>
 */
public interface IPipelineContext {

    /**
     * The receivers are used for determining which players will receive the update.<br>
     * Receivers are used for sending different images on the same display to different players.
     *
     * @return a modifiable {@link Set} of receivers
     */
    Set<Player> receivers();

    /**
     * Replaces the receivers with the specified collection of receivers.
     */
    void receivers(Collection<? extends Player> receivers);

    /**
     * Adds one or more specified players as receivers.
     */
    void addReceiver(Player... players);

    /**
     * Removes one or more specified players from receivers.
     */
    void removeReceiver(Player... players);

    /**
     * @return if the specified player is a receiver at the moment
     */
    boolean isReceiver(Player player);

    /**
     * Clears all currently set receivers.
     */
    void clearReceivers();

    /**
     * @return the {@link IMapDisplay} associated with this context
     */
    IMapDisplay display();

    /**
     * @return true if the buffer will update everything,
     * false if it is a partial update
     * @deprecated use {@link #buffering()} instead
     */
    @Deprecated
    boolean full();

    /**
     * @see #full()
     * @deprecated use {@link #buffering()} instead
     */
    @Deprecated
    void full(boolean full);

    boolean buffering();

    /**
     * This enabled per player buffering.<br>
     * If this is enabled, the updates will be buffered with a FileCache for each player.<br>
     * Then only the changes will be sent to the player, not the full image.
     * This saves a lot of bandwidth, but adds a bit of extra computation time.<br><br>
     * Recommended for large displays or displays that are updated very often.
     *
     * @param buffering true if per player buffering should be enabled
     */
    void buffering(boolean buffering);

    /**
     * @return the current z-layer index
     * @see IMapDisplay#mapId(Player, int) for more info
     */
    int z();

    /**
     * @param z the new z-layer index
     * @see IMapDisplay#mapId(Player, int) for more info
     */
    void z(int z);

    /**
     * {@link org.bukkit.map.MapCursor}'s are used for
     * displaying e.g. arrows and similar decorations on maps.
     *
     * @return a modifiable {@link MapCursorCollection}
     */
    MapCursorCollection cursors();

    /**
     * A {@link Converter} is used for modifying the buffer written in this context.<br>
     * These can be used e.g. for applying dithering to the buffer.
     *
     * @return the current {@link Converter} for this context
     */
    Converter converter();

    /**
     * @param converter the new {@link Converter} to be set in this context
     * @see #converter()
     */
    void converter(Converter converter);

    /**
     * The previous buffer is used for getting the changed section.<br>
     * If this is not set, everything will update.
     */
    @Deprecated
    @Nullable FullSpacedColorBuffer previousBuffer();

    /**
     * @see #previousBuffer()
     */
    @Deprecated
    void previousBuffer(@Nullable FullSpacedColorBuffer previousBuffer);
}
