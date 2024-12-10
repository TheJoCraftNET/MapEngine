package de.pianoman911.mapengine.core.colors.dithering;

import de.pianoman911.mapengine.api.util.ColorBuffer;
import de.pianoman911.mapengine.api.util.FullSpacedColorBuffer;
import de.pianoman911.mapengine.core.colors.ColorPalette;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;

public class FloydSteinbergDithering {

    private static final ExecutorService EXECUTOR = new ForkJoinPool();

    // Floyd-Steinberg error diffusion matrix
    private static final float FS_ERROR = 7f / 16f;
    private static final float FS_ERROR2 = 1f / 16f;
    private static final float FS_ERROR3 = 5f / 16f;
    private static final float FS_ERROR4 = 3f / 16f;

    /**
     * My own implementation of Floyd-Steinberg dithering algorithm, to convert a FullSpacedColorBuffer (24Bit Colors) to a ColorBuffer (Minecraft Colors).
     * It's not an accurate implementation, so it corrects the errors at the end.
     * On the other hand, it's extremely fast.
     *
     * @param buffer  The FullSpacedColorBuffer to dither
     * @param palette The ColorPalette to use
     * @param threads The amount of threads to use for dithering
     * @return The dithered ColorBuffer
     */
    @SuppressWarnings("Duplicates") // It's duplicated, but it's faster than using a method
    public static ColorBuffer dither(FullSpacedColorBuffer buffer, ColorPalette palette, int threads) {
        CompletableFuture<?>[] futures = new CompletableFuture[threads];

        int[] src = buffer.buffer();
        int w = buffer.width();
        int h = buffer.height();

        int size = h / threads;
        for (int i = 0; i < threads; i++) {
            int finalI = i;
            futures[i] = CompletableFuture.runAsync(() -> {
                int starty = finalI * size;
                int endy = (finalI + 1) * size;
                if (finalI == threads - 1) endy = h;
                for (int y = starty; y < endy; y++) {
                    for (int x = 0; x < w; x++) {
                        int index = x + y * w;
                        int rgb = src[index];

                        int oldR = (rgb >> 16) & 0xFF;
                        int oldG = (rgb >> 8) & 0xFF;
                        int oldB = (rgb) & 0xFF;

                        int mc = palette.closestColor(rgb);

                        int a;
                        int r = (mc >> 16) & 0xFF;
                        int g = (mc >> 8) & 0xFF;
                        int b = (mc) & 0xFF;

                        int errorR = oldR - r;
                        int errorG = oldG - g;
                        int errorB = oldB - b;

                        src[index] = mc;

                        if (!(x == w - 1)) {
                            index = x + 1 + y * w;
                            rgb = src[index];
                            a = (rgb >> 24) & 0xFF;
                            r = Math.max(0, Math.min(255, (int) (((rgb >> 16) & 0xFF) + (errorR * FS_ERROR))));
                            g = Math.max(0, Math.min(255, (int) (((rgb >> 8) & 0xFF) + (errorG * FS_ERROR))));
                            b = Math.max(0, Math.min(255, (int) (((rgb) & 0xFF) + (errorB * FS_ERROR))));
                            src[index] = (a << 24) | (r << 16) | (g << 8) | b;

                            if (!(y == h - 1)) {
                                index = x + 1 + (y + 1) * w;
                                rgb = src[index];
                                a = (rgb >> 24) & 0xFF;
                                r = Math.max(0, Math.min(255, (int) (((rgb >> 16) & 0xFF) + (errorR * FS_ERROR2))));
                                g = Math.max(0, Math.min(255, (int) (((rgb >> 8) & 0xFF) + (errorG * FS_ERROR2))));
                                b = Math.max(0, Math.min(255, (int) (((rgb) & 0xFF) + (errorB * FS_ERROR2))));
                                src[index] = (a << 24) | (r << 16) | (g << 8) | b;
                            }
                        }

                        if (!(y == h - 1)) {
                            index = x + (y + 1) * w;
                            rgb = src[index];
                            a = (rgb >> 24) & 0xFF;
                            r = Math.max(0, Math.min(255, (int) (((rgb >> 16) & 0xFF) + (errorR * FS_ERROR3))));
                            g = Math.max(0, Math.min(255, (int) (((rgb >> 8) & 0xFF) + (errorG * FS_ERROR3))));
                            b = Math.max(0, Math.min(255, (int) (((rgb) & 0xFF) + (errorB * FS_ERROR3))));
                            src[index] = (a << 24) | (r << 16) | (g << 8) | b;

                            if (!(x == 0)) {
                                index = x - 1 + (y + 1) * w;
                                rgb = src[index];
                                a = (rgb >> 24) & 0xFF;
                                r = Math.max(0, Math.min(255, (int) (((rgb >> 16) & 0xFF) + (errorR * FS_ERROR4))));
                                g = Math.max(0, Math.min(255, (int) (((rgb >> 8) & 0xFF) + (errorG * FS_ERROR4))));
                                b = Math.max(0, Math.min(255, (int) (((rgb) & 0xFF) + (errorB * FS_ERROR4))));
                                src[index] = (a << 24) | (r << 16) | (g << 8) | b;
                            }
                        }
                    }
                }
            }, EXECUTOR);
        }

        CompletableFuture.allOf(futures).join();

        // Recalculation only at the touch points of the threads to avoid color bleeding without using locks or synchronization (which would be slower)
        for (int i = 1; i < threads; i++) {
            int starty = i * size;
            for (int x = 0; x < w; x++) {
                int index = x + starty * w;
                src[index] = palette.closestColor(src[x + (starty - 1) * w]);
            }
        }

        return new ColorBuffer(palette.colors(src), buffer.width(), buffer.height());
    }
}
