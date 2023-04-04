package de.pianoman911.mapengine.core.clientside;

import de.pianoman911.mapengine.common.platform.PacketContainer;
import de.pianoman911.mapengine.core.MapEnginePlugin;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BlockVector;

public class Frame extends FilledMap {

    @SuppressWarnings("deprecation") // unsafe api, don't care + didn't ask
    protected final int entityId = Bukkit.getUnsafe().nextEntityId();
    protected final BlockFace direction;
    protected final BlockVector pos;

    protected Frame(MapEnginePlugin plugin, BlockFace direction, BlockVector pos) {
        super(plugin);
        this.direction = direction;
        this.pos = pos;
    }

    protected PacketContainer<?> spawnPacket() {
        return plugin.platform().createMapEntitySpawnPacket(entityId, pos, direction);
    }

    protected PacketContainer<?> removePacket() {
        return plugin.platform().createRemoveEntitiesPacket(new IntArrayList(entityId));
    }

    protected PacketContainer<?> setIdPacket(int z, boolean invisible) {
        return plugin.platform().createMapSetIdPacket(entityId, mapId(z), invisible);
    }

    public BlockVector pos() {
        return pos;
    }
}
