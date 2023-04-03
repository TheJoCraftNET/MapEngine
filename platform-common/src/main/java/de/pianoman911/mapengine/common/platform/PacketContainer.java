package de.pianoman911.mapengine.common.platform;
// Created by booky10 in MapStream (17:16 11.09.22)

import org.bukkit.entity.Player;

public final class PacketContainer<T> {

    private final IPlatform<T> platform;
    private final T packet;

    public PacketContainer(IPlatform<T> platform, T packet) {
        this.platform = platform;
        this.packet = packet;
    }

    public static <T> PacketContainer<T> wrap(IPlatform<T> platform, T packet) {
        return new PacketContainer<>(platform, packet);
    }

    public void send(Player player) {
        platform.sendPacket(player, this);
    }

    public T getPacket() {
        return packet;
    }
}
