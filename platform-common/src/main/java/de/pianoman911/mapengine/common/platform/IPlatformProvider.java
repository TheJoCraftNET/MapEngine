package de.pianoman911.mapengine.common.platform;

import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public interface IPlatformProvider extends Comparable<IPlatformProvider> {

    @Override
    default int compareTo(@NotNull IPlatformProvider other) {
        // intentionally reversed
        return Integer.compare(other.getPriority(), this.getPriority());
    }

    default int getPriority() {
        return 0;
    }

    Optional<IPlatform<?>> tryProvide(Plugin plugin, IListenerBridge bridge);

    static boolean existsClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException exception) {
            return false;
        }
    }
}
