package de.pianoman911.mapengine.core.util;

import com.google.common.base.Preconditions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;

public class FrameFileCache {

    private static final int FRAME_SIZE = 16384;

    private final File file;
    private final RandomAccessFile cache;

    private final WeakReference<byte[]>[] memoryCache;
    private volatile boolean closed = false;

    @SuppressWarnings({"unchecked", "ResultOfMethodCallIgnored"})
    public FrameFileCache(File file, int size) {
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
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        this.memoryCache = new WeakReference[size];
        Arrays.fill(this.memoryCache, new WeakReference<byte[]>(null));
    }

    public byte[] read(int index) {
        synchronized (this) {
            Preconditions.checkState(!this.closed, "Cache is already closed");

            byte[] buffer = this.memoryCache[index].get();
            if (buffer != null) {
                // gc hasn't collected the buffer yet, can be reused
                return buffer;
            }

            byte[] data = new byte[FRAME_SIZE];
            try {
                FileChannel channel = this.cache.getChannel(); // File Channel is considered to be reused, so no need to close it
                channel.map(FileChannel.MapMode.READ_ONLY,
                        (long) FRAME_SIZE * index, FRAME_SIZE).get(data);
            } catch (IOException exception) {
                throw new RuntimeException("Failed to read cache file: " + this.file.getName(), exception);
            }

            this.memoryCache[index] = new WeakReference<>(data);
            return data;
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public void write(byte[] data, int index) {
        synchronized (this) {
            Preconditions.checkState(!this.closed, "Cache is already closed");

            this.memoryCache[index] = new WeakReference<>(data);
            try {
                FileChannel channel = this.cache.getChannel();
                channel.write(ByteBuffer.wrap(data), (long) FRAME_SIZE * index);
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }
    }

    public void closeAndDelete() {
        synchronized (this) {
            Preconditions.checkState(!this.closed, "Cache is already closed");
            this.closed = true;

            try {
                this.cache.close();
                if (!this.file.delete()) {
                    throw new RuntimeException("Failed to delete cache file: " + this.file.getName());
                }
            } catch (IOException exception) {
                throw new RuntimeException(exception);
            }
        }
    }
}
