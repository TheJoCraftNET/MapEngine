package de.pianoman911.mapengine.api.pipeline;

import de.pianoman911.mapengine.api.MapEngineApi;
import de.pianoman911.mapengine.api.clientside.IDisplay;
import de.pianoman911.mapengine.api.clientside.IMapDisplay;
import de.pianoman911.mapengine.api.util.Converter;
import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCursorCollection;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;

/**
 * This interface is used to pass data between {@link IPipelineNode}'s,
 * it is created for every flush and is passed to every node.
 * <p>
 * <strong>WARNING: This context is not thread-safe and should only be used on a single thread.</strong>
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
     * @deprecated use {@link #getDisplay()} instead
     */
    @Deprecated(forRemoval = true)
    default IMapDisplay display() {
        return (IMapDisplay) this.getDisplay();
    }

    /**
     * @return the {@link IDisplay} associated with this context
     */
    IDisplay getDisplay();

    boolean buffering();

    /**
     * This enables per player buffering.<br>
     * If this is enabled, updates in this context will be
     * buffered with an on-disk temporary file cache for each player.
     * <p>
     * If buffering is turned on, only changes flushed through the
     * pipeline will actually be sent to the player. This improves
     * performance for map display flushing on poor internet connections,
     * but increases IO- and CPU-time.
     * <p>
     * This is recommended for large displays which update often,
     * with very small difference in content.
     * <p>
     * If you want manual control over the buffering, you can use
     * {@link #previousBuffer()} to set the previous buffer. This
     * will only update the changed section, but it takes no account
     * if the player has already seen the previous buffer.
     *
     * @param buffering true if per player buffering should be enabled
     */
    void buffering(boolean buffering);

    boolean bundling();

    /**
     * This enables packet bundling.
     * <p>
     * If bundling is turned on, all packets sent to the player
     * will be bundled into one packet.
     * This can improve tearing issues on slow internet connections.
     * <p>
     * It's not recommended to use this if you update the display
     * very often.
     * This can block the connection for a long time and can cause
     * the player to time out or connection lag spikes.
     *
     * @param bundling true if per player bundling should be enabled
     */
    void bundling(boolean bundling);

    /**
     * @return the z-layer index updated with this pipeline
     * @see IMapDisplay#mapId(Player, int) for more info
     */
    int z();

    /**
     * @param z the new z-layer index updated with this pipeline
     * @see IMapDisplay#mapId(Player, int) for more info
     */
    void z(int z);

    /**
     * {@link org.bukkit.map.MapCursor}'s are used for
     * displaying e.g. arrows and other vanilla decorations on maps.
     *
     * @return a modifiable {@link MapCursorCollection}
     */
    MapCursorCollection cursors();

    /**
     * A {@link Converter} is used for converting the RGB buffer to minecraft map colors.<br>
     * These can be used e.g. for applying dithering to the buffer ({@link Converter#FLOYD_STEINBERG}).
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
     * The previous buffer is used for getting the changed section.
     * If this is not set, everything will update.<br>
     * Also, it takes no account if the player has already seen the previous buffer.<br><br>
     * Buffering must be disabled for this to work.<br>
     * {@link #buffering(boolean)} for more info.
     */
    @Nullable FullSpacedColorBuffer previousBuffer();

    /**
     * @see #previousBuffer()
     */
    void previousBuffer(@Nullable FullSpacedColorBuffer previousBuffer);

    /**
     * @return the {@link MapEngineApi} instance
     */
    MapEngineApi mapEngineApi();
}
