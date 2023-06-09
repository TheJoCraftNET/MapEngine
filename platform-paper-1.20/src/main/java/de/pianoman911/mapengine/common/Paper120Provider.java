package de.pianoman911.mapengine.common;

import de.pianoman911.mapengine.common.platform.IListenerBridge;
import de.pianoman911.mapengine.common.platform.IPlatform;
import de.pianoman911.mapengine.common.platform.IPlatformProvider;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

public final class Paper120Provider implements IPlatformProvider {

    @Override
    public Optional<IPlatform<?>> tryProvide(Plugin plugin, IListenerBridge bridge) {
        if (IPlatformProvider.existsClass("org.bukkit.craftbukkit.v1_20_R1.CraftServer")) {
            System.out.println("Paper120Provider.tryProvide: Paper120StaticProvider.provide");
            return Optional.of(Paper120StaticProvider.provide(plugin, bridge));
        }
        return Optional.empty();
    }
}

final class Paper120StaticProvider {

    private Paper120StaticProvider() {
    }

    static IPlatform<?> provide(Plugin plugin, IListenerBridge bridge) {
        return new Paper120Platform(plugin, bridge);
    }
}