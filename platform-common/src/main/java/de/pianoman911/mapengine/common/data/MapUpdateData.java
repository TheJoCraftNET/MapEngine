package de.pianoman911.mapengine.common.data;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

@SuppressWarnings("removal")
public final class MapUpdateData implements de.pianoman911.mapengine.api.data.IMapUpdateData {

    private static final byte[] EMPTY_ARR = new byte[0];

    private final byte[] fullBuffer, buffer;
    private final short beginX, beginY;
    private final short endX, endY;

    public MapUpdateData(byte[] fullBuffer, byte[] colors, long beginX, long beginY, long endX, long endY) {
        this.fullBuffer = fullBuffer;
        this.buffer = colors;
        this.beginX = (short) beginX;
        this.beginY = (short) beginY;
        this.endX = (short) endX;
        this.endY = (short) endY;
    }

    public static MapUpdateData createMapUpdateData(byte[] fullBuffer, byte @Nullable [] lastUpdateData, int minChanges) {
        if (lastUpdateData == null) {
            return createFull(fullBuffer);
        }

        short beginX = 128, beginY = 128;
        short endX = 0, endY = 0;
        int changes = 0;

        for (short i = 0; i < 16384; i++) {
            short x = (short) (i % 128);
            short y = (short) (i / 128);

            if (fullBuffer[i] != lastUpdateData[i]) {
                changes++;
                if (x < beginX) {
                    beginX = x;
                }
                if (y < beginY) {
                    beginY = y;
                }
                if (x > endX) {
                    endX = x;
                }
                if (y > endY) {
                    endY = y;
                }
            }
        }

        if (changes < minChanges || beginX == 128 && beginY == 128 && endX == 0 && endY == 0) {
            return createEmpty(fullBuffer);
        }

        endX++;
        endY++;

        byte[] colors = new byte[(endX - beginX) * (endY - beginY)];
        for (short i = 0; i < colors.length; i++) {
            short x = (short) (i % (endX - beginX));
            short y = (short) (i / (endX - beginX));
            colors[i] = fullBuffer[(y + beginY) * 128 + x + beginX];
        }

        return new MapUpdateData(fullBuffer, colors, beginX, beginY, endX, endY);
    }

    private static MapUpdateData createFull(byte[] colors) {
        return new MapUpdateData(colors.clone(), colors, 0, 0, 128, 128);
    }

    private static MapUpdateData createEmpty(byte[] fullBuffer) {
        return new MapUpdateData(fullBuffer, EMPTY_ARR, 0, 0, 0, 0);
    }

    public int size() {
        return buffer.length;
    }

    @Override
    public boolean empty() {
        return buffer.length == 0;
    }

    @Override
    public short offsetX() {
        return beginX;
    }

    @Override
    public short offsetY() {
        return beginY;
    }

    @Override
    public int width() {
        return endX - beginX;
    }

    @Override
    public int height() {
        return endY - beginY;
    }

    @Override
    public byte[] fullBuffer() {
        return fullBuffer;
    }

    @Override
    public byte[] buffer() {
        return buffer;
    }

    @Override
    public String toString() {
        return "MapUpdateData{" + "fullBuffer=" + Arrays.toString(fullBuffer) + ", colors=" + Arrays.toString(buffer) + ", beginX=" + beginX + ", beginY=" + beginY + ", endX=" + endX + ", endY=" + endY + '}';
    }
}
