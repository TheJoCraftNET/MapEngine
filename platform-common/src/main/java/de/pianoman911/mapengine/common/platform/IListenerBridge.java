package de.pianoman911.mapengine.common.platform;

import org.bukkit.entity.Player;

public interface IListenerBridge {

    void handleInteract(Player player, int entityId, double posX, double posY, double posZ);

    void handleAttack(Player player, int entityId);

    void handleSwing(Player player);
}
