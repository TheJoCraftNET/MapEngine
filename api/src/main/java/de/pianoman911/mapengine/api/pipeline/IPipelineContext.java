package de.pianoman911.mapengine.api.pipeline;

import de.pianoman911.mapengine.api.clientside.IMapDisplay;
import de.pianoman911.mapengine.api.util.Converter;
import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCursorCollection;

import java.util.Collection;
import java.util.Set;

public interface IPipelineContext {

    Set<Player> receivers();

    void receivers(Collection<? extends Player> receivers);

    void addReceiver(Player... players);

    void removeReceiver(Player... players);

    boolean isReceiver(Player player);

    void clearReceivers();

    IMapDisplay display();

    boolean full();

    void full(boolean full);

    int z();

    void z(int z);

    MapCursorCollection cursors();

    Converter converter();

    void converter(Converter converter);

    FullSpacedColorBuffer previousBuffer();

    void previousBuffer(FullSpacedColorBuffer previousBuffer);
}
