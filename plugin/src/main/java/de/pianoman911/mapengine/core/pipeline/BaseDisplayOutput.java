package de.pianoman911.mapengine.core.pipeline;

import de.pianoman911.mapengine.api.pipeline.IPipelineContext;
import de.pianoman911.mapengine.api.pipeline.IPipelineOutput;
import de.pianoman911.mapengine.api.util.ColorBuffer;
import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;
import de.pianoman911.mapengine.core.MapEnginePlugin;
import de.pianoman911.mapengine.core.cache.FileFrameCache;
import de.pianoman911.mapengine.core.cache.FrameCache;
import de.pianoman911.mapengine.core.cache.NullFrameCache;
import de.pianoman911.mapengine.core.colors.dithering.FloydSteinbergDithering;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BaseDisplayOutput implements IPipelineOutput {

    protected static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    private static final Set<BaseDisplayOutput> INSTANCES = new HashSet<>();

    protected final MapEnginePlugin plugin;
    private final Map<UUID, Map<Integer, FrameCache>> cache = new HashMap<>();
    private final Set<Player> preventBuffering = Collections.newSetFromMap(new WeakHashMap<>());
    private boolean destroyed = false;

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
                synchronized (instance.preventBuffering) {
                    instance.preventBuffering.add(player);
                }

                Map<Integer, FrameCache> zs = instance.cache.remove(player.getUniqueId());
                if (zs != null) {
                    for (FrameCache cache : zs.values()) {
                        cache.closeAndDelete();
                    }
                }
            }
        }
    }

    protected FrameCache getFrameFileCache(Player receiver, int z, int size) {
        if (this.destroyed) {
            return NullFrameCache.INSTANCE;
        }

        synchronized (this.cache) {
            synchronized (this.preventBuffering) {
                if (this.preventBuffering.contains(receiver)) {
                    return NullFrameCache.INSTANCE;
                }
            }
            return this.cache.computeIfAbsent(receiver.getUniqueId(), uuid -> new HashMap<>())
                    .computeIfAbsent(z, $ -> new FileFrameCache(new File(this.plugin.getDataFolder() + "/caches", UUID.randomUUID() + ".cache"), size));
        }
    }


    protected ColorBuffer convert(FullSpacedColorBuffer buffer, IPipelineContext ctx, int frameHeight) {
        return switch (ctx.converter()) {
            case DIRECT -> this.plugin.colorPalette().convertDirect(buffer);
            case FLOYD_STEINBERG -> FloydSteinbergDithering.dither(buffer, this.plugin.colorPalette(), frameHeight); // frameHeight 128 is one frame height
        };
    }

    // ensures that only online players are used
    protected void removeOfflinePlayers(IPipelineContext ctx) {
        ctx.receivers().removeIf(player -> !player.isOnline());
    }

    @Override
    public void destroy() {
        this.destroyed = true;
        synchronized (INSTANCES) {
            INSTANCES.remove(this);
        }

        synchronized (this.cache) {
            for (Map<Integer, FrameCache> caches : this.cache.values()) {
                for (FrameCache cache : caches.values()) {
                    cache.closeAndDelete();
                }
            }
        }
    }
}
