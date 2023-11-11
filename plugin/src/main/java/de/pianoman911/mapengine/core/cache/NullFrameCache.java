package de.pianoman911.mapengine.core.cache;

/**
 * A cache that does nothing.
 * Used when a player is leaving the server, but the map is still being updated.
 * So it prevents cached data from being written to the disk or using memory.
 */
public class NullFrameCache implements FrameCache {

    public static final NullFrameCache INSTANCE = new NullFrameCache();

    private NullFrameCache() {
    }

    @Override
    public byte[] read(int index) {
        return null;
    }

    @Override
    public void write(byte[] data, int index) {
    }

    @Override
    public void closeAndDelete() {
    }
}
