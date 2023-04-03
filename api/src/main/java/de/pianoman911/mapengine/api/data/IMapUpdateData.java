package de.pianoman911.mapengine.api.data;

public interface IMapUpdateData {

    short offsetX();

    short offsetY();

    int width();

    int height();

    byte[] fullBuffer();

    byte[] buffer();

    boolean empty();
}
