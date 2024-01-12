package de.pianoman911.mapengine.common.platform;

import de.pianoman911.mapengine.api.util.PassthroughMode;
import org.bukkit.entity.Player;

public interface IListenerBridge {

    PassthroughMode handleInteract(Player player, int entityId, double posX, double posY, double posZ);

    PassthroughMode handleAttack(Player player, int entityId);

    PassthroughMode handleSwing(Player player);
}
