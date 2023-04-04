package de.pianoman911.mapengine.core.clientside;

import de.pianoman911.mapengine.api.clientside.IHoldableDisplay;
import de.pianoman911.mapengine.core.MapEnginePlugin;
import de.pianoman911.mapengine.core.pipeline.Pipeline;
import de.pianoman911.mapengine.core.util.DummyMapView;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;

public class MapItem extends FilledMap implements IHoldableDisplay {

    private final Pipeline pipeline;

    public MapItem(MapEnginePlugin plugin, Pipeline pipeline) {
        super(plugin);
        this.pipeline = pipeline;
    }

    @Override
    public ItemStack itemStack(int z) {
        ItemStack item = new ItemStack(Material.FILLED_MAP);
        item.editMeta(MapMeta.class, meta -> meta.setMapView(new DummyMapView(mapId(z))));
        return item;
    }

    @Override
    public Pipeline pipeline() {
        return pipeline;
    }
}
