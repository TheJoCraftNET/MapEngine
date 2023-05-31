package de.pianoman911.mapengine.core.listener;

import de.pianoman911.mapengine.core.pipeline.FlushingOutput;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public final class MapEngineListener implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        FlushingOutput.ejectPlayer(event.getPlayer());
    }
}
