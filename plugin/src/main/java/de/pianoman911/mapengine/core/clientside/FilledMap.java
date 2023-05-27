package de.pianoman911.mapengine.core.clientside;

import de.pianoman911.mapengine.common.data.MapUpdateData;
import de.pianoman911.mapengine.common.platform.PacketContainer;
import de.pianoman911.mapengine.core.MapEnginePlugin;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import org.bukkit.map.MapCursorCollection;

import java.util.concurrent.atomic.AtomicInteger;

public class FilledMap {

    // start counting down at -32768 for compatibility with other map plugins
    protected static final AtomicInteger CURRENT_ID = new AtomicInteger(-Short.MAX_VALUE);
    protected final Int2IntMap mapIds = new Int2IntArrayMap();
    protected final MapEnginePlugin plugin;

    public FilledMap(MapEnginePlugin plugin) {
        this.plugin = plugin;
    }

    protected PacketContainer<?> updatePacket(MapUpdateData data, boolean fullData, int z, MapCursorCollection cursors) {
        return plugin.platform().createMapDataPacket(data, fullData, mapId(z), cursors);
    }

    protected synchronized int mapId(int z) {
        return mapIds.computeIfAbsent(z, k -> {
            synchronized (CURRENT_ID) {
                return CURRENT_ID.getAndDecrement();
            }
        });
    }
}
