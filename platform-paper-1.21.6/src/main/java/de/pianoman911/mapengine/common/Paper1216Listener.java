package de.pianoman911.mapengine.common;

import de.pianoman911.mapengine.api.util.PassthroughMode;
import de.pianoman911.mapengine.common.platform.IListenerBridge;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAnimatePacket;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class Paper1216Listener extends MessageToMessageDecoder<Packet<?>> implements ServerboundInteractPacket.Handler {

    private final Player player;
    private final IListenerBridge bridge;

    private int entityId;
    private PassthroughMode passthroughMode;

    public Paper1216Listener(Player player, IListenerBridge bridge) {
        this.player = player;
        this.bridge = bridge;
    }

    @Override
    public boolean acceptInboundMessage(Object msg) {
        return msg instanceof ServerboundInteractPacket || msg instanceof ServerboundSwingPacket;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, Packet<?> msg, List<Object> out) {
        if (msg instanceof ServerboundInteractPacket interact) {
            this.entityId = interact.getEntityId();
            interact.dispatch(this);
        } else if (msg instanceof ServerboundSwingPacket) {
            this.passthroughMode = this.bridge.handleSwing(this.player);
            if (this.passthroughMode == PassthroughMode.ONLY_ANIMATION) {
                ClientboundAnimatePacket animatePacket = new ClientboundAnimatePacket(((CraftPlayer) this.player).getHandle(),
                        ClientboundAnimatePacket.SWING_MAIN_HAND);
                this.player.getTrackedBy().forEach(player -> ((CraftPlayer) player).getHandle().connection.send(animatePacket));
            }
        }
        if (this.passthroughMode != null && this.passthroughMode != PassthroughMode.ALL) {
            return;
        }

        out.add(msg);
    }

    @Override
    public void onInteraction(@NotNull InteractionHand hand) {
        // onInteraction(InteractionHand, Vec3) is called instead
    }

    @Override
    public void onInteraction(@NotNull InteractionHand hand, Vec3 pos) {
        this.passthroughMode = this.bridge.handleInteract(this.player, this.entityId, pos.x, pos.y, pos.z);
    }

    @Override
    public void onAttack() {
        this.passthroughMode = this.bridge.handleAttack(this.player, this.entityId);
    }
}
