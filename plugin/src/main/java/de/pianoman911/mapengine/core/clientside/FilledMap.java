package de.pianoman911.mapengine.core.clientside;

import de.pianoman911.mapengine.api.clientside.IMap;
import de.pianoman911.mapengine.common.data.MapUpdateData;
import de.pianoman911.mapengine.common.platform.PacketContainer;
import de.pianoman911.mapengine.core.MapEnginePlugin;
import de.pianoman911.mapengine.core.util.DummyMapView;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntMaps;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCursorCollection;

public class FilledMap implements IMap {

    // start counting down at -32768 for compatibility with other map plugins
    private static volatile int CURRENT_ID = -Short.MAX_VALUE;
    protected final MapEnginePlugin plugin;
    private Int2IntMap mapIds = new Int2IntArrayMap();

    public FilledMap(MapEnginePlugin plugin) {
        this.plugin = plugin;
    }

    protected PacketContainer<?> updatePacket(MapUpdateData data, int z, MapCursorCollection cursors) {
        return plugin.platform().createMapDataPacket(data, mapId(z), cursors);
    }

    // this method is synchronized and the field is only accessed inside this method,
    // so this is actually thread-safe
    @SuppressWarnings("NonAtomicOperationOnVolatileField")
    public synchronized int mapId(int z) {
        return mapIds.computeIfAbsent(z, k -> CURRENT_ID--);
    }

    public Int2IntMap mapIds0() {
        return this.mapIds;
    }

    @Override
    public Int2IntMap mapIds() {
        return Int2IntMaps.unmodifiable(this.mapIds0());
    }

    @Override
    public ItemStack itemStack(int z) {
        ItemStack item = new ItemStack(Material.FILLED_MAP);
        item.editMeta(MapMeta.class, meta -> meta.setMapView(new DummyMapView(this.mapId(z))));
        return item;
    }

    // Used for connecting displays
    public void mapIds(Int2IntMap mapIds) {
        this.mapIds = mapIds;
    }
}
