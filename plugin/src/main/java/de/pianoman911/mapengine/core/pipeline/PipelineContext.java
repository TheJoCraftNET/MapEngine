package de.pianoman911.mapengine.core.pipeline;

import de.pianoman911.mapengine.api.clientside.IMapDisplay;
import de.pianoman911.mapengine.api.pipeline.IPipelineContext;
import de.pianoman911.mapengine.api.util.Converter;
import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;
import de.pianoman911.mapengine.core.clientside.FrameContainer;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCursorCollection;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class PipelineContext implements IPipelineContext {
    private final Set<Player> receivers = new HashSet<>();
    private final FrameContainer container;
    private final MapCursorCollection cursors = new MapCursorCollection();
    private boolean full = true;
    private int z = 0;
    private Converter converter = Converter.DIRECT;

    private FullSpacedColorBuffer previousBuffer = null;

    public PipelineContext(FrameContainer container) {
        this.container = container;
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
    public IMapDisplay display() {
        return container;
    }

    @Override
    public boolean full() {
        return full;
    }

    @Override
    public void full(boolean full) {
        this.full = full;
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
