package de.pianoman911.mapengine.core.clientside;

import de.pianoman911.mapengine.common.data.MapUpdateData;
import de.pianoman911.mapengine.common.platform.PacketContainer;
import de.pianoman911.mapengine.core.MapEnginePlugin;
import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.util.BlockVector;

public class Frame {

    // start counting down at -32768 for compatibility with other map plugins
    private static int CURRENT_ID = -Short.MAX_VALUE;

    @SuppressWarnings("deprecation") // unsafe api, don't care + didn't ask
    protected final int entityId = Bukkit.getUnsafe().nextEntityId();

    protected final Int2IntMap mapIds = new Int2IntArrayMap();
    protected final MapEnginePlugin plugin;
    protected final BlockFace direction;
    protected final BlockVector pos;

    protected Frame(MapEnginePlugin plugin, BlockFace direction, BlockVector pos) {
        this.direction = direction;
        this.pos = pos;
        this.plugin = plugin;
    }

    protected PacketContainer<?> spawnPacket() {
        return plugin.platform().createMapEntitySpawnPacket(entityId, pos, direction);
    }

    protected PacketContainer<?> setIdPacket(int z, boolean invisible) {
        return plugin.platform().createMapSetIdPacket(entityId, mapIds.computeIfAbsent(z, $ -> CURRENT_ID--), invisible);
    }

    protected PacketContainer<?> removePacket() {
        return plugin.platform().createRemoveEntitiesPacket(new IntArrayList(entityId));
    }

    protected PacketContainer<?> updatePacket(MapUpdateData data, boolean fullData, int z, MapCursorCollection cursors) {
        return plugin.platform().createMapDataPacket(data, fullData, mapIds.computeIfAbsent(z, $ -> CURRENT_ID--), cursors);
    }

    public BlockVector pos() {
        return pos;
    }
}
