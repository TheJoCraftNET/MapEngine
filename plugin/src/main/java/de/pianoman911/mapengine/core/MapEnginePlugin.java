package de.pianoman911.mapengine.core;

import de.pianoman911.mapengine.api.MapEngineApi;
import de.pianoman911.mapengine.common.platform.IPlatform;
import de.pianoman911.mapengine.core.api.ImplMapEngineApi;
import de.pianoman911.mapengine.core.colors.ColorPalette;
import de.pianoman911.mapengine.core.listener.MapEngineListener;
import de.pianoman911.mapengine.core.map.HoldableManager;
import de.pianoman911.mapengine.core.map.MapManager;
import de.pianoman911.mapengine.core.platform.ImplListenerBridge;
import de.pianoman911.mapengine.core.platform.PlatformUtil;
import de.pianoman911.mapengine.core.updater.MapEngineUpdater;
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
        this.saveDefaultConfig();

        this.platform = PlatformUtil.getPlatform(this, this.getClassLoader(), new ImplListenerBridge(this));

        this.getSLF4JLogger().info("Using platform: {}", this.platform.getDisplayedName());

        this.colorPalette = new ColorPalette(this);

        this.mapManager = new MapManager(this);
        this.holdableManager = new HoldableManager(this);

        this.api = new ImplMapEngineApi(this);
        Bukkit.getServicesManager().register(MapEngineApi.class, this.api, this, ServicePriority.Normal);

        Bukkit.getPluginManager().registerEvents(new MapEngineListener(), this);

        if (this.getConfig().getBoolean("updater.enabled", true)) {
            MapEngineUpdater updater = new MapEngineUpdater(this);
            Bukkit.getPluginManager().registerEvents(updater, this);
        }
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
