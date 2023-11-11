package de.pianoman911.mapengine.core.cache;

import com.google.common.base.Preconditions;
import de.pianoman911.mapengine.core.util.MapUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class FileFrameCache implements FrameCache {

    private static final Logger LOGGER = LoggerFactory.getLogger("MapEngine[FrameFileCache]");

    private final File file;
    private final RandomAccessFile cache;

    private final WeakReference<byte[]>[] memoryCache;
    private volatile boolean closed = false;

    @SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored"})
    public FileFrameCache(File file, int size) {
        this.file = file;
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            try {
                file.createNewFile();
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }

        file.deleteOnExit();

        try {
            this.cache = new RandomAccessFile(file, "rw");
        } catch (FileNotFoundException exception) {
            throw new RuntimeException(exception);
        }

        this.memoryCache = new WeakReference[size];
        Arrays.fill(this.memoryCache, new WeakReference<byte[]>(null));
    }

    @Override
    public byte[] read(int index) {
        synchronized (this) {
            Preconditions.checkState(!this.closed, "Cache is already closed");

            byte[] buffer = this.memoryCache[index].get();
            if (buffer != null) {
                // gc hasn't collected the buffer yet, can be reused
                return buffer;
            }

            byte[] data = new byte[MapUtil.MAP_PIXEL_COUNT];
            try {
                FileChannel channel = this.cache.getChannel(); // File Channel is considered to be reused, so no need to close it
                channel.map(FileChannel.MapMode.READ_ONLY,
                        (long) MapUtil.MAP_PIXEL_COUNT * index, MapUtil.MAP_PIXEL_COUNT).get(data);
            } catch (IOException exception) {
                throw new RuntimeException("Failed to read cache file: " + this.file.getName(), exception);
            }

            this.memoryCache[index] = new WeakReference<>(data);
            return data;
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public void write(byte[] data, int index) {
        synchronized (this) {
            Preconditions.checkState(!this.closed, "Cache is already closed");

            this.memoryCache[index] = new WeakReference<>(data);
            try {
                FileChannel channel = this.cache.getChannel();
                channel.write(ByteBuffer.wrap(data), (long) MapUtil.MAP_PIXEL_COUNT * index);
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    @Override
    public void closeAndDelete() {
        synchronized (this) {
            Preconditions.checkState(!this.closed, "Cache is already closed");
            this.closed = true;

            try {
                this.cache.close();
                if (!this.file.delete()) {
                    if (!this.file.exists()) {
                        LOGGER.warn("Failed to delete cache file: {} (File does not exist)", this.file.getName());
                    } else {
                        LOGGER.warn("Failed to delete cache file: {}", this.file.getName());
                    }
                }
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }
    }
}
