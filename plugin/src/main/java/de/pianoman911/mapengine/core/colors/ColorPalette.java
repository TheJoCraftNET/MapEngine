package de.pianoman911.mapengine.core.colors;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.pianoman911.mapengine.api.colors.IMapColors;
import de.pianoman911.mapengine.api.util.ColorBuffer;
import de.pianoman911.mapengine.api.util.Converter;
import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;
import de.pianoman911.mapengine.core.MapEnginePlugin;
import de.pianoman911.mapengine.core.colors.dithering.FloydSteinbergDithering;
import de.pianoman911.mapengine.core.util.MapUtil;
import org.bukkit.Bukkit;
import org.bukkit.map.MapPalette;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class ColorPalette implements IMapColors {

    protected final CompletableFuture<ColorPalette> loadFuture = new CompletableFuture<>(); // Used for run code after the palette is loaded
    private final MapEnginePlugin plugin;
    private final File file;
    protected byte[] colors;
    protected byte[] available;
    protected int[] rgb;
    protected int[] reverseColors;
    protected int retries = 0;

    public ColorPalette(MapEnginePlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "colors.bin");
        load();
    }

    @Override
    public byte color(Color color) {
        return color(color.getRGB());
    }

    @Override
    public byte color(int rgb) {
        return colors[rgb & 0xFFFFFF];
    }

    public byte[] colors(int[] rgb, int threads) {
        byte[] result = new byte[rgb.length];
        CompletableFuture<?>[] futures = new CompletableFuture[threads];
        int size = rgb.length / threads;
        for (int i = 0; i < threads; i++) {
            int finalI = i;
            futures[i] = CompletableFuture.runAsync(() -> {
                int start = finalI * size;
                int end = (finalI + 1) * size;
                if (finalI == threads - 1) end = rgb.length;
                for (int j = start; j < end; j++) {
                    int color = rgb[j];
                    if (((color >> 24) & 0xFF) != 255) continue;
                    result[j] = color(rgb[j]);
                }
            });
        }
        CompletableFuture.allOf(futures).join();
        return result;
    }

    @Override
    public byte[] convertImage(BufferedImage image) {
        int[] rgb = new int[image.getWidth() * image.getHeight()];
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), rgb, 0, image.getWidth());
        return colors(rgb);
    }

    @Override
    public FullSpacedColorBuffer adjustColors(FullSpacedColorBuffer buffer, Converter converter) {
        return switch (converter) {
            case DIRECT -> {
                ColorBuffer mc = convertDirect(buffer);
                yield new FullSpacedColorBuffer(plugin.colorPalette().toRGBs(mc.data()), mc.width(), mc.height());
            }
            case FLOYD_STEINBERG -> {
                ColorBuffer mc = FloydSteinbergDithering.dither(buffer, plugin.colorPalette(), buffer.height() / MapUtil.MAP_HEIGHT + 1);
                yield new FullSpacedColorBuffer(plugin.colorPalette().toRGBs(mc.data()), mc.width(), mc.height());
            }
        };
    }

    @Override
    public Color toColor(byte color) {
        return new Color(toRGB(color));
    }

    @Override
    public byte color(int r, int g, int b) {
        return colors[dataIndex(r, g, b)];
    }

    private int dataIndex(int r, int g, int b) {
        return ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF));
    }

    @SuppressWarnings("deprecation")
    private void generateColors() {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            plugin.getLogger().info("Generating palette...");

            colors = new byte[256 * 256 * 256];
            reverseColors = new int[256 * 256 * 256];
            long start = System.currentTimeMillis();
            long last = start;

            Set<Byte> usedColors = new HashSet<>();
            for (int red = 0; red < 256; red++) {
                CompletableFuture<?>[] futures = new CompletableFuture[256];
                for (int green = 0; green < 256; green++) {
                    int finalRed = red, finalGreen = green;
                    futures[green] = CompletableFuture.supplyAsync(() -> {
                        for (int blue = 0; blue < 256; blue++) {
                            byte color = MapPalette.matchColor(finalRed, finalGreen, blue);
                            colors[dataIndex(finalRed, finalGreen, blue)] = color;
                            reverseColors[dataIndex(finalRed, finalGreen, blue)] = dataIndex(finalRed, finalGreen, blue);
                            usedColors.add(color);
                        }
                        return null;
                    });
                }

                if (last + 250 < System.currentTimeMillis() || red == 255) {
                    plugin.getLogger().info("Generating palette... " + String.format("%.2f", (red * 100 / 255.0))
                            + "% - Working threads: " + futures.length);
                    last = System.currentTimeMillis();
                }

                CompletableFuture.allOf(futures).join();
            }

            available = new byte[usedColors.size()];
            rgb = new int[255];

            int i = 0;
            for (Byte color : usedColors) {
                available[i++] = color;
                rgb[color >= 0 ? color : color + 256] = MapPalette.getColor(color).getRGB();
            }

            plugin.getLogger().info("Palette generated! Took " + (System.currentTimeMillis() - start) + "ms");
            save();
            loadFuture.complete(this);

            checkValidity();
        });
    }

    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    private void save() {
        plugin.getLogger().info("Saving palette...");
        ByteArrayDataOutput output = ByteStreams.newDataOutput();

        output.writeInt(Bukkit.getUnsafe().getDataVersion());
        output.writeInt(colors.length);

        for (byte color : colors) {
            output.writeByte(color);
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(file); GZIPOutputStream gzip = new GZIPOutputStream(fileOutputStream)) {
            gzip.write(output.toByteArray());
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        plugin.getLogger().info("Palette saved!");
    }

    @SuppressWarnings({"UnstableApiUsage", "deprecation"})
    private void load() {
        if (!file.exists()) {
            file.getParentFile().mkdir();
            generateColors();
            return;
        }

        try (FileInputStream fileInput = new FileInputStream(file);
             GZIPInputStream gzipInput = new GZIPInputStream(fileInput)) {
            ByteArrayDataInput input = ByteStreams.newDataInput(gzipInput.readAllBytes());

            if (input.readInt() == Bukkit.getUnsafe().getDataVersion()) {
                Set<Byte> usedColors = new HashSet<>();
                colors = new byte[input.readInt()];
                reverseColors = new int[colors.length];

                for (int i = 0; i < colors.length; i++) {
                    colors[i] = input.readByte();
                    reverseColors[i] = MapPalette.getColor(colors[i]).getRGB();
                    usedColors.add(colors[i]);
                }

                available = new byte[usedColors.size()];
                rgb = new int[255];

                int i = 0;
                for (Byte color : usedColors) {
                    available[i++] = color;
                    rgb[color >= 0 ? color : color + 256] = MapPalette.getColor(color).getRGB();
                }

                plugin.getLogger().info("Loaded color palette");
                loadFuture.complete(this);
                return;
            }
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        plugin.getLogger().info("Incompatible color palette saved");
        generateColors();
    }

    @Override
    public int toRGB(byte color) {
        return rgb[color >= 0 ? color : color + 256];
    }

    @Override
    public int[] toRGBs(byte[] colors) {
        int[] rgb = new int[colors.length];
        for (int i = 0; i < colors.length; i++) {
            rgb[i] = toRGB(colors[i]);
        }
        return rgb;
    }

    public int closestColor(int rgb) {
        return reverseColors[rgb & 0xFFFFFF];
    }

    @SuppressWarnings("deprecation") // magic value
    public void checkValidity() {
        loadFuture.thenAccept($ -> {
            boolean valid = true;
            try {
                for (byte i = -128; i < 127; i++) {
                    int engine = toRGB(i);
                    int bukkit = MapPalette.getColor(i).getRGB();

                    if (engine != bukkit) {
                        plugin.getLogger().warning("Color " + i + " is invalid! MapEngine: " + engine + " Bukkit: " + bukkit);
                        valid = false;
                    }
                }
            } catch (Throwable ignored) {
            }

            if (valid) {
                plugin.getLogger().info("Color palette is valid!" + (retries > 0 ? " (Retried " + retries + " times)" : ""));
                return;
            }

            plugin.getLogger().warning("Color palette is invalid!" + (retries > 0 ? " (Retried " + retries + " times)" : ""));
            if (retries < 10) {
                retries++;
                plugin.getLogger().warning("Retrying... (" + retries + "/10)");
                generateColors();
            } else {
                plugin.getLogger().warning("Failed to load color palette!");
            }
        });
    }
}
