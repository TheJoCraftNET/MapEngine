package de.pianoman911.mapengine.common;

import de.pianoman911.mapengine.common.platform.IListenerBridge;
import de.pianoman911.mapengine.common.platform.IPlatform;
import de.pianoman911.mapengine.common.platform.IPlatformProvider;
import it.unimi.dsi.fastutil.ints.IntSet;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

public final class Paper1212Provider implements IPlatformProvider {

    private static final IntSet SUPPORTED_PROTOCOLS = IntSet.of(
            768 // 1.21.2
    );

    @SuppressWarnings("deprecation") // bukkit unsafe
    @Override
    public Optional<IPlatform<?>> tryProvide(Plugin plugin, IListenerBridge bridge) {
        if (IPlatformProvider.existsClass("org.bukkit.craftbukkit.CraftServer")
                && SUPPORTED_PROTOCOLS.contains(Bukkit.getUnsafe().getProtocolVersion())) {
            return Optional.of(Paper1212StaticProvider.provide(plugin, bridge));
        }
        return Optional.empty();
    }
}

final class Paper1212StaticProvider {

    private Paper1212StaticProvider() {
    }

    static IPlatform<?> provide(Plugin plugin, IListenerBridge bridge) {
        return new Paper1205Platform(plugin, bridge);
    }
}
