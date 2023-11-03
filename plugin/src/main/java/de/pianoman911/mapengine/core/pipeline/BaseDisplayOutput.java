package de.pianoman911.mapengine.core.pipeline;

import de.pianoman911.mapengine.api.pipeline.IPipelineContext;
import de.pianoman911.mapengine.api.pipeline.IPipelineOutput;
import de.pianoman911.mapengine.api.util.ColorBuffer;
import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;
import de.pianoman911.mapengine.core.MapEnginePlugin;
import de.pianoman911.mapengine.core.colors.dithering.FloydSteinbergDithering;
import de.pianoman911.mapengine.core.util.FrameFileCache;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BaseDisplayOutput implements IPipelineOutput {

    protected static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    private static final Set<BaseDisplayOutput> INSTANCES = new HashSet<>();

    protected final MapEnginePlugin plugin;
    private final Map<UUID, Map<Integer, FrameFileCache>> cache = new HashMap<>();

    protected BaseDisplayOutput(MapEnginePlugin plugin) {
        this.plugin = plugin;

        synchronized (INSTANCES) {
            INSTANCES.add(this);
        }
    }

    public static void ejectPlayer(Player player) {
        Set<BaseDisplayOutput> instances;
        synchronized (INSTANCES) {
            instances = Set.copyOf(INSTANCES);
        }

        for (BaseDisplayOutput instance : instances) {
            synchronized (instance.cache) {
                for (FrameFileCache cache : instance.cache.remove(player.getUniqueId()).values()) {
                    cache.closeAndDelete();
                }
            }
        }
    }

    protected FrameFileCache getFrameFileCache(Player receiver, int z, int size) {
        synchronized (this.cache) {
            return this.cache.computeIfAbsent(receiver.getUniqueId(), uuid -> new HashMap<>())
                    .computeIfAbsent(z, $ -> new FrameFileCache(new File(this.plugin.getDataFolder() + "/caches", UUID.randomUUID() + ".cache"), size));
        }
    }


    protected ColorBuffer convert(FullSpacedColorBuffer buffer, IPipelineContext ctx, int frameHeight) {
        return switch (ctx.converter()) {
            case DIRECT -> this.plugin.colorPalette().convertDirect(buffer);
            case FLOYD_STEINBERG -> FloydSteinbergDithering.dither(buffer, this.plugin.colorPalette(), frameHeight); // frameHeight 128 is one frame height
        };
    }
}
