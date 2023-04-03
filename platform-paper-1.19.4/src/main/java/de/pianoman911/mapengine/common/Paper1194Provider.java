package de.pianoman911.mapengine.common;

import de.pianoman911.mapengine.common.platform.IListenerBridge;
import de.pianoman911.mapengine.common.platform.IPlatform;
import de.pianoman911.mapengine.common.platform.IPlatformProvider;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

public final class Paper1194Provider implements IPlatformProvider {

    @Override
    public Optional<IPlatform<?>> tryProvide(Plugin plugin, IListenerBridge bridge) {
        if (IPlatformProvider.existsClass("org.bukkit.craftbukkit.v1_19_R3.CraftServer")) {
            return Optional.of(Paper1194StaticProvider.provide(plugin, bridge));
        }
        return Optional.empty();
    }
}

final class Paper1194StaticProvider {

    private Paper1194StaticProvider() {
    }

    static IPlatform<?> provide(Plugin plugin, IListenerBridge bridge) {
        return new Paper1194Platform(plugin, bridge);
    }
}