package de.pianoman911.mapengine.api.event;

import de.pianoman911.mapengine.api.clientside.IMapDisplay;
import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class MapEvent extends Event implements Cancellable {

    private static final HandlerList HANDLER_LIST = new HandlerList();

    private final IMapDisplay display;
    private boolean cancelled;

    public MapEvent(IMapDisplay display) {
        super(!Bukkit.isPrimaryThread());
        this.display = display;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled = cancel;
    }

    public IMapDisplay display() {
        return display;
    }
}
