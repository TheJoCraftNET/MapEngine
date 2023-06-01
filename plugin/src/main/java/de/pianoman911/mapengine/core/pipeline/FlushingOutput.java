package de.pianoman911.mapengine.core.pipeline;

import de.pianoman911.mapengine.api.pipeline.IPipelineContext;
import de.pianoman911.mapengine.api.pipeline.IPipelineOutput;
import de.pianoman911.mapengine.api.util.ColorBuffer;
import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;
import de.pianoman911.mapengine.common.data.MapUpdateData;
import de.pianoman911.mapengine.core.MapEnginePlugin;
import de.pianoman911.mapengine.core.clientside.FrameContainer;
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

public class FlushingOutput implements IPipelineOutput {

    private static final Set<FlushingOutput> INSTANCES = new HashSet<>();
    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();

    private final MapEnginePlugin plugin;
    private final Map<UUID, FrameFileCache> cache = new HashMap<>();

    public FlushingOutput(MapEnginePlugin plugin) {
        this.plugin = plugin;

        synchronized (INSTANCES) {
            INSTANCES.add(this);
        }
    }

    public static void ejectPlayer(Player player) {
        Set<FlushingOutput> instances;
        synchronized (INSTANCES) {
            instances = Set.copyOf(INSTANCES);
        }

        for (FlushingOutput instance : instances) {
            FrameFileCache playerCache;
            synchronized (instance.cache) {
                playerCache = instance.cache.remove(player.getUniqueId());
            }

            if (playerCache != null) {
                playerCache.closeAndDelete();
            }
        }
    }

    public static ColorBuffer[] splitColorBuffer(ColorBuffer colorBuffer, int width, int height) {
        ColorBuffer[] result = new ColorBuffer[width * height];
        byte[] rawData = colorBuffer.data();

        for (int i = 0; i < result.length; i++) {
            ColorBuffer buffer = new ColorBuffer(128, 128);
            int x = i % width;
            int y = i / width;
            for (int y1 = 0; y1 < 128; y1++) {
                for (int x1 = 0; x1 < 128; x1++) {
                    buffer.data()[y1 * 128 + x1] = rawData[(y * 128 + y1) * width * 128 + x * 128 + x1];
                }
            }
            result[i] = buffer;
        }
        return result;
    }

    @Override
    public void output(FullSpacedColorBuffer buffer, IPipelineContext ctx) {
        ColorBuffer buf = convert(buffer, ctx);
        FrameContainer display = (FrameContainer) ctx.display();
        int size = ctx.display().width() * ctx.display().height();


        ColorBuffer[] buffers = splitColorBuffer(buf, ctx.display().width(), ctx.display().height());

        if (!ctx.buffering()) {
            MapUpdateData[] data = new MapUpdateData[size];
            for (int i = 0; i < buffers.length; i++) {
                data[i] = MapUpdateData.createMapUpdateData(buffers[i].data(), null, 0);
            }

            for (Player receiver : ctx.receivers()) {
                display.update(receiver, data, ctx.z(), ctx.cursors());
            }
            return;
        }

        for (Player receiver : ctx.receivers()) {
            EXECUTOR.submit(() -> {
                if (!receiver.isOnline()) {
                    return;
                }

                FrameFileCache cache;
                synchronized (this.cache) {
                    cache = this.cache.computeIfAbsent(receiver.getUniqueId(),
                            $ -> new FrameFileCache(new File(plugin.getDataFolder() + "/caches", UUID.randomUUID() + ".cache"), size));
                }

                MapUpdateData[] data = new MapUpdateData[size];
                for (int i = 0; i < buffers.length; i++) {
                    ColorBuffer currentBuffer = buffers[i];

                    data[i] = MapUpdateData.createMapUpdateData(currentBuffer.data(), cache.read(i), 0);
                    cache.write(currentBuffer.data(), i);
                }

                display.update(receiver, data, ctx.z(), ctx.cursors());
            });
        }
    }

    private ColorBuffer convert(FullSpacedColorBuffer buffer, IPipelineContext ctx) {
        return switch (ctx.converter()) {
            case DIRECT -> plugin.colorPalette().convertDirect(buffer);
            case FLOYD_STEINBERG -> FloydSteinbergDithering.dither(buffer, plugin.colorPalette(), ctx.display().height());
        };
    }
}
