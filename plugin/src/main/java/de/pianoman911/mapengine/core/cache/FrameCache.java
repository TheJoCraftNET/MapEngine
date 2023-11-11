package de.pianoman911.mapengine.core.cache;

/**
 * The common cache interface for all frame caches.<br>
 * Indexed by the item frame index.
 */
public interface FrameCache {

    /**
     * Reads the data at the item frame index.
     *
     * @param index the item frame index
     * @return the data
     */
    byte[] read(int index);

    /**
     * Writes the data at the item frame index.
     * @param data the data
     * @param index the item frame index
     */
    void write(byte[] data, int index);

    void closeAndDelete();
}
