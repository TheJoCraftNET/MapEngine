package de.pianoman911.mapengine.core.pipeline;

import de.pianoman911.mapengine.api.pipeline.IPipelineContext;
import de.pianoman911.mapengine.api.util.ColorBuffer;
import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;
import de.pianoman911.mapengine.common.data.MapUpdateData;
import de.pianoman911.mapengine.core.MapEnginePlugin;
import de.pianoman911.mapengine.core.clientside.MapItem;
import de.pianoman911.mapengine.core.util.FrameFileCache;
import org.bukkit.entity.Player;

public class HoldableDisplayOutput extends BaseDisplayOutput {

    private static final int MAP_COUNT = 1;

    public HoldableDisplayOutput(MapEnginePlugin plugin) {
        super(plugin);
    }

    @Override
    public void output(FullSpacedColorBuffer buf, IPipelineContext ctx) {
        ColorBuffer buffer = this.convert(buf, ctx, MAP_COUNT);
        MapItem display = (MapItem) ctx.getDisplay();

        if (!ctx.buffering()) {
            ColorBuffer previous = this.convert(ctx.previousBuffer(), ctx, MAP_COUNT);
            MapUpdateData data = MapUpdateData.createMapUpdateData(buffer.data(), previous.data(), 0);

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
                FrameFileCache cache = getFrameFileCache(receiver, ctx.z(), buffer.data().length);
                MapUpdateData data = MapUpdateData.createMapUpdateData(buffer.data(), cache.read(0), 0);
                cache.write(data.buffer(), 0);

                display.update(receiver, data, ctx.z(), ctx.cursors());
            });
        }
    }
}
