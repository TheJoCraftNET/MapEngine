package de.pianoman911.mapengine.core.map;

import de.pianoman911.mapengine.api.clientside.IHoldableDisplay;
import de.pianoman911.mapengine.core.MapEnginePlugin;
import de.pianoman911.mapengine.core.clientside.MapItem;
import de.pianoman911.mapengine.core.pipeline.HoldableDisplayOutput;
import de.pianoman911.mapengine.core.pipeline.Pipeline;

import java.util.HashSet;
import java.util.Set;

public class HoldableManager {

    private final Set<IHoldableDisplay> displays = new HashSet<>();
    private final MapEnginePlugin plugin;

    public HoldableManager(MapEnginePlugin plugin) {
        this.plugin = plugin;
    }

    public IHoldableDisplay createDisplay(Pipeline pipeline) {
        MapItem item = new MapItem(this.plugin, pipeline);
        this.displays.add(item);
        return item;
    }

    public IHoldableDisplay createDisplay() {
        Pipeline pipeline = new Pipeline(new HoldableDisplayOutput(this.plugin));
        MapItem item = new MapItem(this.plugin, pipeline);
        this.displays.add(item);
        return item;
    }

    public Set<IHoldableDisplay> displays() {
        return this.displays;
    }
}
