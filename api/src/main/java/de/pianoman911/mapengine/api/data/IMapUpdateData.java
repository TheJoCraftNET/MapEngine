package de.pianoman911.mapengine.api.data;

public interface IMapUpdateData {

    /**
     * @return the x-offset of the tile section
     */
    short offsetX();

    /**
     * @return the y-offset of the tile section
     */
    short offsetY();

    int width();

    int height();

    /**
     * The full buffer contains the full tile section
     * It has always the size of 128*128 (16384)
     *
     * @return the full buffer
     */
    byte[] fullBuffer();

    /**
     * The buffer contains only the rectangle of the changed pixels
     * It has the size of width*height
     * The buffer is always a rectangle
     *
     * @return the buffer
     */
    byte[] buffer();

    /**
     * If the buffer is empty, no update packet will be sent
     *
     * @return true if the buffer is empty, false otherwise
     */
    boolean empty();
}
