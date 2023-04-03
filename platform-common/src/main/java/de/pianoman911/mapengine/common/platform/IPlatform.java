package de.pianoman911.mapengine.common.platform;

import de.pianoman911.mapengine.common.data.MapUpdateData;
import it.unimi.dsi.fastutil.ints.IntList;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.util.BlockVector;

public interface IPlatform<T> {

    String getDisplayedName();

    void sendPacket(Player player, PacketContainer<T> packet);

    PacketContainer<T> createMapDataPacket(MapUpdateData data, boolean fullData, int mapId, MapCursorCollection cursors);

    PacketContainer<T> createMapEntitySpawnPacket(int entityId, BlockVector pos, BlockFace facing);

    PacketContainer<T> createMapSetIdPacket(int entityId, int mapId, boolean invisible);

    PacketContainer<T> createRemoveEntitiesPacket(IntList entityIds);
}
