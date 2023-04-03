package de.pianoman911.mapengine.core.platform;
// Created by booky10 in MapStream (17:54 11.09.22)

import com.google.common.base.Preconditions;
import de.pianoman911.mapengine.common.platform.IListenerBridge;
import de.pianoman911.mapengine.common.platform.IPlatform;
import de.pianoman911.mapengine.common.platform.IPlatformProvider;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public final class PlatformUtil {

    private PlatformUtil() {
    }

    private static List<IPlatformProvider> getProviders(ClassLoader loader) {
        Preconditions.checkArgument(loader instanceof URLClassLoader, "Loader is not an instance of URLClassLoader");

        List<IPlatformProvider> providers = new ArrayList<>();
        for (URL url : ((URLClassLoader) loader).getURLs()) {
            Path sourcePath;
            try {
                sourcePath = new File(url.toURI()).toPath();
            } catch (URISyntaxException exception) {
                throw new RuntimeException(exception);
            }

            try (FileSystem fs = FileSystems.newFileSystem(sourcePath)) {
                for (Path rootDir : fs.getRootDirectories()) {
                    extractProviders(rootDir, loader, providers);
                }
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }
        return Collections.unmodifiableList(providers);
    }

    private static void extractProviders(Path rootDir, ClassLoader loader, List<IPlatformProvider> providers) {
        try (Stream<Path> files = Files.list(rootDir)) {
            files.filter(path -> path.getFileName().toString().startsWith("provider_"))
                    .map(path -> {
                        try {
                            return Files.readString(path).trim();
                        } catch (IOException exception) {
                            throw new RuntimeException(exception);
                        }
                    })
                    .map(className -> {
                        try {
                            return loader.loadClass(className).getConstructor().newInstance();
                        } catch (ReflectiveOperationException exception) {
                            throw new RuntimeException(exception);
                        }
                    })
                    .map(obj -> (IPlatformProvider) obj)
                    .forEach(providers::add);
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public static IPlatform<?> getPlatform(Plugin plugin, ClassLoader loader, IListenerBridge bridge) {
        return getProviders(loader).stream().sorted()
                .flatMap(provider -> provider.tryProvide(plugin, bridge).stream()).findFirst()
                .orElseThrow(() -> new UnsupportedOperationException("Unsupported server version/software"));
    }
}
