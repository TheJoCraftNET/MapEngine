package de.pianoman911.mapengine.core.pipeline;

import de.pianoman911.mapengine.api.clientside.IDisplay;
import de.pianoman911.mapengine.api.pipeline.IPipelineContext;
import de.pianoman911.mapengine.api.util.Converter;
import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCursorCollection;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class PipelineContext implements IPipelineContext {

    private final Set<Player> receivers = new HashSet<>();
    private final IDisplay display;
    private final MapCursorCollection cursors = new MapCursorCollection();
    private boolean buffering = false;
    private int z = 0;
    private Converter converter = Converter.DIRECT;
    private FullSpacedColorBuffer previousBuffer;

    public PipelineContext(IDisplay display) {
        this.display = display;
    }

    @Override
    public Set<Player> receivers() {
        return receivers;
    }

    @Override
    public void receivers(Collection<? extends Player> receivers) {
        this.receivers.clear();
        this.receivers.addAll(receivers);
    }

    @Override
    public void addReceiver(Player... players) {
        Collections.addAll(receivers, players);
    }

    @Override
    public void removeReceiver(Player... players) {
        for (Player player : players) {
            receivers.remove(player);
        }
    }

    @Override
    public boolean isReceiver(Player player) {
        return receivers.contains(player);
    }

    @Override
    public void clearReceivers() {
        receivers.clear();
    }

    @Override
    public IDisplay getDisplay() {
        return this.display;
    }

    @Override
    public boolean full() {
        return buffering;
    }

    @Override
    public void full(boolean full) {
        this.buffering = full;
    }

    @Override
    public boolean buffering() {
        return buffering;
    }

    @Override
    public void buffering(boolean buffering) {
        this.buffering = buffering;
    }

    @Override
    public int z() {
        return z;
    }

    @Override
    public void z(int z) {
        this.z = z;
    }

    @Override
    public MapCursorCollection cursors() {
        return cursors;
    }

    @Override
    public Converter converter() {
        return converter;
    }

    @Override
    public FullSpacedColorBuffer previousBuffer() {
        return previousBuffer;
    }

    @Override
    public void previousBuffer(FullSpacedColorBuffer previousBuffer) {
        this.previousBuffer = previousBuffer;
    }

    @Override
    public void converter(Converter converter) {
        if (converter == null) {
            throw new IllegalArgumentException("Converter cannot be null");
        }
        this.converter = converter;
    }
}
