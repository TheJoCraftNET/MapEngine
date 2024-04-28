package de.pianoman911.mapengine.core.clientside;

import de.pianoman911.mapengine.api.clientside.IHoldableDisplay;
import de.pianoman911.mapengine.common.data.MapUpdateData;
import de.pianoman911.mapengine.common.platform.PacketContainer;
import de.pianoman911.mapengine.core.MapEnginePlugin;
import de.pianoman911.mapengine.core.pipeline.Pipeline;
import de.pianoman911.mapengine.core.util.DummyMapView;
import de.pianoman911.mapengine.core.util.MapUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCursorCollection;

public class MapItem extends FilledMap implements IHoldableDisplay {

    private final Pipeline pipeline;

    public MapItem(MapEnginePlugin plugin, Pipeline pipeline) {
        super(plugin);
        this.pipeline = pipeline;
    }

    @Override
    public ItemStack itemStack(int z) {
        ItemStack item = new ItemStack(Material.FILLED_MAP);
        item.editMeta(MapMeta.class, meta -> meta.setMapView(new DummyMapView(this.mapId(z))));
        return item;
    }

    @Override
    public int pixelWidth() {
        return MapUtil.MAP_WIDTH;
    }

    @Override
    public int pixelHeight() {
        return MapUtil.MAP_HEIGHT;
    }

    @Override
    public Pipeline pipeline() {
        return this.pipeline;
    }

    @Override
    public void destroy() {
        this.plugin.holdableManager().removeDisplay(this);
        this.pipeline.destroy();
    }

    public void update(Player receiver, MapUpdateData data, int z, MapCursorCollection cursors) {
        PacketContainer<?> packet = this.updatePacket(data, z, cursors);
        packet.send(receiver);
        this.plugin.platform().flush(receiver);
    }
}
