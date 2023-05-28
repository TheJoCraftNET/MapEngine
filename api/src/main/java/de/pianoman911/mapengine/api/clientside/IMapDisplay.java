package de.pianoman911.mapengine.api.clientside;

import de.pianoman911.mapengine.api.data.IMapUpdateData;
import de.pianoman911.mapengine.api.pipeline.IPipeline;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.util.BoundingBox;

public interface IMapDisplay {

    int width();

    int height();

    int pixelWidth();

    int pixelHeight();

    BoundingBox box();

    BlockFace direction();

    void spawn(Player player);

    void despawn(Player player);

    void mapId(Player player, int z);

    void update(Player player, IMapUpdateData[] data, boolean fullUpdate, int z, MapCursorCollection cursors);

    IPipeline pipeline();

    void rotation(Player player, float yaw, float pitch);

    void visualDirection(Player player, BlockFace visualDirection);

    void visualDirection(Player player, BlockFace visualDirection, int z);
}
