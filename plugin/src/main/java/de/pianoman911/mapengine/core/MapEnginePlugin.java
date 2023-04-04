package de.pianoman911.mapengine.core;

import de.pianoman911.mapengine.api.MapEngineApi;
import de.pianoman911.mapengine.common.platform.IPlatform;
import de.pianoman911.mapengine.core.api.ImplMapEngineApi;
import de.pianoman911.mapengine.core.colors.ColorPalette;
import de.pianoman911.mapengine.core.map.HoldableManager;
import de.pianoman911.mapengine.core.map.MapManager;
import de.pianoman911.mapengine.core.platform.ImplListenerBridge;
import de.pianoman911.mapengine.core.platform.PlatformUtil;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

public class MapEnginePlugin extends JavaPlugin {

    private IPlatform<?> platform;
    private ColorPalette colorPalette;
    private MapManager mapManager;
    private HoldableManager holdableManager;
    private ImplMapEngineApi api;

    @Override
    public void onLoad() {
        new Metrics(this, 18122);
    }

    @Override
    public void onEnable() {
        this.platform = PlatformUtil.getPlatform(this, this.getClassLoader(), new ImplListenerBridge(this));
        this.colorPalette = new ColorPalette(this);

        this.mapManager = new MapManager(this);
        this.holdableManager = new HoldableManager(this);

        this.api = new ImplMapEngineApi(this);
        Bukkit.getServicesManager().register(MapEngineApi.class, this.api, this, ServicePriority.Normal);
    }

    public IPlatform<?> platform() {
        return platform;
    }

    public ColorPalette colorPalette() {
        return colorPalette;
    }

    public MapManager mapManager() {
        return mapManager;
    }

    public ImplMapEngineApi api() {
        return api;
    }

    public HoldableManager holdableManager() {
        return holdableManager;
    }
}
