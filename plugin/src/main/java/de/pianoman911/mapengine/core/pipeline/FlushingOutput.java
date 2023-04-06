package de.pianoman911.mapengine.core.pipeline;

import de.pianoman911.mapengine.api.pipeline.IPipelineContext;
import de.pianoman911.mapengine.api.pipeline.IPipelineOutput;
import de.pianoman911.mapengine.api.util.ColorBuffer;
import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;
import de.pianoman911.mapengine.common.data.MapUpdateData;
import de.pianoman911.mapengine.core.MapEnginePlugin;
import de.pianoman911.mapengine.core.colors.dithering.FloydSteinbergDithering;
import org.bukkit.entity.Player;

public class FlushingOutput implements IPipelineOutput {

    private final MapEnginePlugin plugin;

    public FlushingOutput(MapEnginePlugin plugin) {
        this.plugin = plugin;
    }

    public static ColorBuffer[] splitColorBuffer(ColorBuffer colorBuffer, int width, int height) {
        ColorBuffer[] result = new ColorBuffer[width * height];
        byte[] rawData = colorBuffer.data();

        for (int i = 0; i < result.length; i++) {
            ColorBuffer buffer = new ColorBuffer(16384, 128, 128);
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
        int size = ctx.display().width() * ctx.display().height();

        ColorBuffer[] buffers = splitColorBuffer(buf, ctx.display().width(), ctx.display().height());
        MapUpdateData[] data = new MapUpdateData[size];
        MapUpdateData[] previousData = new MapUpdateData[size];

        if (!ctx.full()) { // If the update is not full, we need to regenerate the previous data for the tile update mode. In the future, this should be a buffered pipeline itself.
            FullSpacedColorBuffer previous = ctx.previousBuffer();
            if (previous != null) {
                ColorBuffer[] previousBuffers = splitColorBuffer(convert(previous, ctx), ctx.display().width(), ctx.display().height());
                for (int i = 0; i < previousBuffers.length; i++) {
                    previousData[i] = MapUpdateData.createMapUpdateData(previousBuffers[i].data(), null, 0);
                }
            } else {
                plugin.getLogger().warning("Previous buffer is null! Please set the previous buffer before calling the pipeline, if you want to use the tile update mode!");
            }
        }

        for (int i = 0; i < buffers.length; i++) {
            data[i] = MapUpdateData.createMapUpdateData(buffers[i].data(), previousData[i], 0);
        }

        for (Player receiver : ctx.receivers()) {
            ctx.display().update(receiver, data, ctx.full(), ctx.z(), ctx.cursors());
        }
    }

    private ColorBuffer convert(FullSpacedColorBuffer buffer, IPipelineContext ctx) {
        return switch (ctx.converter()) {
            case DIRECT -> plugin.colorPalette().convertDirect(buffer);
            case FLOYD_STEINBERG -> FloydSteinbergDithering.dither(buffer, plugin.colorPalette(), ctx.display().height());
        };
    }
}



