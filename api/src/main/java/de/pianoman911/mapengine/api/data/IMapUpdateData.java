package de.pianoman911.mapengine.api.data;

/**
 * Raw internal map color data representation, which is
 * broadcasted to a player.
 *
 * @deprecated only used internally
 */
@Deprecated(forRemoval = true)
public interface IMapUpdateData {

    /**
     * @return the x-offset of the tile section
     */
    short offsetX();

    /**
     * @return the y-offset of the tile section
     */
    short offsetY();

    /**
     * @return the width of the tile section
     */
    int width();

    /**
     * @return the height of the tile section
     */
    int height();

    /**
     * The full buffer contains the full tile section.<br>
     * It has always the size of 128*128 (16384).
     *
     * @return the full buffer
     */
    byte[] fullBuffer();

    /**
     * The buffer contains only the rectangle of the changed pixels.<br>
     * It has the size of width*height, so it is always a rectangle.
     *
     * @return the buffer
     */
    byte[] buffer();

    /**
     * If the buffer is empty, no update packet will be sent.
     *
     * @return if the buffer is empty
     */
    boolean empty();
}
