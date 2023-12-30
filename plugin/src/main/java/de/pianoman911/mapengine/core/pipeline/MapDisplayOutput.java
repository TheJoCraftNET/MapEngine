package de.pianoman911.mapengine.core.pipeline;

import de.pianoman911.mapengine.api.pipeline.IPipelineContext;
import de.pianoman911.mapengine.api.pipeline.IPipelineOutput;
import de.pianoman911.mapengine.api.util.ColorBuffer;
import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;
import de.pianoman911.mapengine.common.data.MapUpdateData;
import de.pianoman911.mapengine.core.MapEnginePlugin;
import de.pianoman911.mapengine.core.cache.FrameCache;
import de.pianoman911.mapengine.core.clientside.FrameContainer;
import de.pianoman911.mapengine.core.util.MapUtil;
import org.bukkit.entity.Player;

public class MapDisplayOutput extends BaseDisplayOutput implements IPipelineOutput {

    public MapDisplayOutput(MapEnginePlugin plugin) {
        super(plugin);
    }

    public static ColorBuffer[] splitColorBuffer(ColorBuffer colorBuffer, int width, int height) {
        ColorBuffer[] result = new ColorBuffer[width * height];
        byte[] rawData = colorBuffer.data();

        for (int i = 0; i < result.length; i++) {
            ColorBuffer buffer = new ColorBuffer(MapUtil.MAP_WIDTH, MapUtil.MAP_HEIGHT);
            int x = i % width;
            int y = i / width;
            for (int y1 = 0; y1 < MapUtil.MAP_HEIGHT; y1++) {
                for (int x1 = 0; x1 < MapUtil.MAP_WIDTH; x1++) {
                    buffer.data()[y1 * MapUtil.MAP_WIDTH + x1] = rawData[(y * MapUtil.MAP_WIDTH + y1) * width * MapUtil.MAP_WIDTH + x * MapUtil.MAP_WIDTH + x1];
                }
            }
            result[i] = buffer;
        }
        return result;
    }

    @Override
    public void output(FullSpacedColorBuffer buffer, IPipelineContext ctx) {
        removeOfflinePlayers(ctx);

        FrameContainer display = (FrameContainer) ctx.getDisplay();
        ColorBuffer buf = convert(buffer, ctx, display.height());
        int size = display.width() * display.height();

        ColorBuffer[] buffers = splitColorBuffer(buf, display.width(), display.height());
        byte[][] previousBuffers = new byte[size][];
        if (ctx.previousBuffer() != null) {
            ColorBuffer[] previous = splitColorBuffer(convert(ctx.previousBuffer(), ctx, display.height()), display.width(), display.height());
            for (int i = 0; i < previous.length; i++) {
                previousBuffers[i] = MapUpdateData.createMapUpdateData(previous[i].data(), null, 0).buffer();
            }
        }

        if (!ctx.buffering()) {
            MapUpdateData[] data = new MapUpdateData[size];
            for (int i = 0; i < buffers.length; i++) {
                data[i] = MapUpdateData.createMapUpdateData(buffers[i].data(), previousBuffers[i], 0);
            }

            for (Player receiver : ctx.receivers()) {
                display.update(receiver, data, ctx.z(), ctx.cursors(), ctx.bundling());
            }
            return;
        }

        for (Player receiver : ctx.receivers()) {
            EXECUTOR.submit(() -> {
                if (!receiver.isOnline()) {
                    return;
                }

                FrameCache cache = this.getFrameFileCache(receiver, ctx.z(), size);

                MapUpdateData[] data = new MapUpdateData[size];
                for (int i = 0; i < buffers.length; i++) {
                    ColorBuffer currentBuffer = buffers[i];

                    data[i] = MapUpdateData.createMapUpdateData(currentBuffer.data(), cache.read(i), 0);
                    cache.write(currentBuffer.data(), i);
                }

                display.update(receiver, data, ctx.z(), ctx.cursors(), ctx.bundling());
            });
        }
    }
}
