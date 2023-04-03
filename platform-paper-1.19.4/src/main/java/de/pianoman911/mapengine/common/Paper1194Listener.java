package de.pianoman911.mapengine.common;

import de.pianoman911.mapengine.common.platform.IListenerBridge;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.Vec3;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public final class Paper1194Listener extends MessageToMessageDecoder<Packet<?>> implements ServerboundInteractPacket.Handler {

    private final Player player;
    private final IListenerBridge bridge;

    private int entityId;

    public Paper1194Listener(Player player, IListenerBridge bridge) {
        this.player = player;
        this.bridge = bridge;
    }

    @Override
    public boolean acceptInboundMessage(Object msg) {
        return msg instanceof ServerboundInteractPacket;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, Packet<?> msg, List<Object> out) {
        ServerboundInteractPacket packet = (ServerboundInteractPacket) msg;
        this.entityId = packet.getEntityId();
        packet.dispatch(this);
        out.add(msg);
    }

    @Override
    public void onInteraction(@NotNull InteractionHand hand) {
        this.bridge.handleInteract(this.player, this.entityId);
    }

    @Override
    public void onInteraction(@NotNull InteractionHand hand, Vec3 pos) {
        this.bridge.handleInteract(this.player, this.entityId, pos.x, pos.y, pos.z);
    }

    @Override
    public void onAttack() {
        this.bridge.handleAttack(this.player, this.entityId);
    }
}
