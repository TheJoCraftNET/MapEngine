package de.pianoman911.mapengine.common;

import de.pianoman911.mapengine.common.data.MapUpdateData;
import de.pianoman911.mapengine.common.platform.IListenerBridge;
import de.pianoman911.mapengine.common.platform.IPlatform;
import de.pianoman911.mapengine.common.platform.PacketContainer;
import io.netty.buffer.Unpooled;
import io.papermc.paper.adventure.PaperAdventure;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.SharedConstants;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.network.protocol.game.ClientboundMapItemDataPacket;
import net.minecraft.network.protocol.game.ClientboundRemoveEntitiesPacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.protocol.game.ClientboundTeleportEntityPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SyncedDataHolder;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.saveddata.maps.MapDecoration;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData.MapPatch;
import net.minecraft.world.phys.Vec3;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.map.MapCursor;
import org.bukkit.map.MapCursorCollection;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Paper1205Platform implements IPlatform<Packet<ClientGamePacketListener>>, Listener {

    private static final Entity FAKED_ENTITY = new ThrownEgg(MinecraftServer.getServer().overworld(), 0, 0, 0);
    private static final EntityDataAccessor<Byte> DATA_SHARED_FLAGS_ID = EntityDataSerializers.BYTE.createAccessor(0);
    private static final EntityDataAccessor<Float> DATA_INTERACTION_BOX_WIDTH_ID = EntityDataSerializers.FLOAT.createAccessor(8);
    private static final EntityDataAccessor<Float> DATA_INTERACTION_BOX_HEIGHT_ID = EntityDataSerializers.FLOAT.createAccessor(9);
    private static final EntityDataAccessor<Boolean> DATA_INTERACTION_BOX_RESPONSIVE_ID = EntityDataSerializers.BOOLEAN.createAccessor(10);

    private static final SynchedEntityData.DataItem<?>[] MAP_DATA_FIELDS = new SynchedEntityData.DataItem[]{
            new SynchedEntityData.DataItem<>(ItemFrame.DATA_ITEM, ItemStack.EMPTY),
            new SynchedEntityData.DataItem<>(DATA_SHARED_FLAGS_ID, (byte) 0x00)
    };

    private static final SynchedEntityData.DataItem<?>[] MAP_ROTATION_FIELDS = new SynchedEntityData.DataItem[]{
            new SynchedEntityData.DataItem<>(ItemFrame.DATA_ROTATION, 0)
    };

    private static final SynchedEntityData.DataItem<?>[] INTERACTION_FIELDS = new SynchedEntityData.DataItem[]{
            new SynchedEntityData.DataItem<>(DATA_INTERACTION_BOX_WIDTH_ID, 0f),
            new SynchedEntityData.DataItem<>(DATA_INTERACTION_BOX_HEIGHT_ID, 0f),
            new SynchedEntityData.DataItem<>(DATA_INTERACTION_BOX_RESPONSIVE_ID, false),
            new SynchedEntityData.DataItem<>(DATA_SHARED_FLAGS_ID, (byte) 0x00)
    };

    private static final MethodHandle SYNCHED_ENTITY_DATA_CONSTRUCTOR;
    private static final MethodHandle CLIENTBOUND_TELEPORT_ENTITY_PACKET_CONSTRUCTOR;

    static {
        try {
            SYNCHED_ENTITY_DATA_CONSTRUCTOR = MethodHandles.privateLookupIn(SynchedEntityData.class, MethodHandles.lookup())
                    .findConstructor(SynchedEntityData.class, MethodType.methodType(void.class, SyncedDataHolder.class, SynchedEntityData.DataItem[].class));

            CLIENTBOUND_TELEPORT_ENTITY_PACKET_CONSTRUCTOR = MethodHandles.privateLookupIn(ClientboundTeleportEntityPacket.class, MethodHandles.lookup())
                    .findConstructor(ClientboundTeleportEntityPacket.class, MethodType.methodType(void.class, FriendlyByteBuf.class));
        } catch (NoSuchMethodException | IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }

    private final IListenerBridge bridge;

    public Paper1205Platform(Plugin plugin, IListenerBridge bridge) {
        this.bridge = bridge;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Paper1205Listener listener = new Paper1205Listener(event.getPlayer(), this.bridge);
        ((CraftPlayer) event.getPlayer()).getHandle().connection.connection.channel
                .pipeline().addAfter("decoder", "mapengine", listener);
    }

    @Override
    public String getDisplayedName() {
        return MinecraftServer.getServer().getServerModName()
                + " " + SharedConstants.getCurrentVersion().getName();
    }

    @Override
    public void sendPacket(Player player, PacketContainer<Packet<ClientGamePacketListener>> packet) {
        ((CraftPlayer) player).getHandle().connection.connection.channel.write(packet.getPacket());
    }

    @Override
    public void flush(Player player) {
        ((CraftPlayer) player).getHandle().connection.connection.channel.flush();
    }

    @Override
    public PacketContainer<Packet<ClientGamePacketListener>> createMapDataPacket(MapUpdateData data, int mapId, MapCursorCollection cursors) {
        MapId id = new MapId(mapId);
        MapPatch updateData = new MapPatch(data.offsetX(), data.offsetY(), data.width(), data.height(), data.buffer());

        List<MapDecoration> decorations;
        if (cursors != null && cursors.size() > 0) {
            decorations = new ArrayList<>(cursors.size());
            for (int i = 0; i < cursors.size(); i++) {
                MapCursor cursor = cursors.getCursor(i);
                if (!cursor.isVisible()) {
                    continue;
                }

                decorations.add(new MapDecoration(BuiltInRegistries.MAP_DECORATION_TYPE.getHolder(cursor.getRawType()).orElseThrow(),
                        cursor.getX(), cursor.getY(), cursor.getDirection(),
                        cursor.caption() == null ? Optional.empty() : Optional.of(PaperAdventure.asVanilla(cursor.caption()))));
            }
        } else {
            decorations = null;
        }

        return PacketContainer.wrap(this, new ClientboundMapItemDataPacket(id,
                (byte) 0, decorations != null, decorations, updateData));
    }

    @Override
    public PacketContainer<Packet<ClientGamePacketListener>> createMapEntitySpawnPacket(int entityId, BlockVector pos, BlockFace facing) {
        int facingIndex = switch (facing) {
            case UP -> 1;
            case NORTH -> 2;
            case SOUTH -> 3;
            case WEST -> 4;
            case EAST -> 5;
            default -> 0;
        };

        return PacketContainer.wrap(this, new ClientboundAddEntityPacket(entityId, UUID.randomUUID(),
                pos.getX(), pos.getY(), pos.getZ(), 0, 0, EntityType.GLOW_ITEM_FRAME, facingIndex, Vec3.ZERO, 0));
    }

    @Override
    public PacketContainer<Packet<ClientGamePacketListener>> createMapSetIdPacket(int entityId, int mapId, boolean invisible) {
        SynchedEntityData entityData;
        try {
            entityData = (SynchedEntityData) SYNCHED_ENTITY_DATA_CONSTRUCTOR.invoke(FAKED_ENTITY, MAP_DATA_FIELDS);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }

        ItemStack mapItem = Items.FILLED_MAP.getDefaultInstance();

        mapItem.set(DataComponents.MAP_ID, new MapId(mapId));

        entityData.set(ItemFrame.DATA_ITEM, mapItem); // map item

        if (invisible) {
            entityData.set(DATA_SHARED_FLAGS_ID, (byte) 0x20); // invisible
        }

        return PacketContainer.wrap(this, new ClientboundSetEntityDataPacket(entityId, Objects.requireNonNull(entityData.packDirty())));
    }

    @Override
    public PacketContainer<Packet<ClientGamePacketListener>> createRemoveEntitiesPacket(IntList entityIds) {
        return PacketContainer.wrap(this, new ClientboundRemoveEntitiesPacket(entityIds));
    }

    @Override
    public PacketContainer<?> createInteractionEntitySpawnPacket(int interactionId, Vector pos, BlockFace direction) {
        return PacketContainer.wrap(this, new ClientboundAddEntityPacket(interactionId, UUID.randomUUID(),
                pos.getX(), pos.getY(), pos.getZ(), 0, 0, EntityType.INTERACTION, 0, Vec3.ZERO, 0));
    }

    @Override
    public PacketContainer<?> createInteractionEntityBlockSizePacket(int interactionId) {
        SynchedEntityData entityData;
        try {
            entityData = (SynchedEntityData) SYNCHED_ENTITY_DATA_CONSTRUCTOR.invoke(FAKED_ENTITY, INTERACTION_FIELDS);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }

        entityData.set(DATA_INTERACTION_BOX_WIDTH_ID, 1f);
        entityData.set(DATA_INTERACTION_BOX_HEIGHT_ID, 1f);
        entityData.set(DATA_INTERACTION_BOX_RESPONSIVE_ID, true);
        entityData.set(DATA_SHARED_FLAGS_ID, (byte) 0x20); // invisible

        return PacketContainer.wrap(this, new ClientboundSetEntityDataPacket(interactionId, Objects.requireNonNull(entityData.packDirty())));
    }

    @Override
    public PacketContainer<?> createTeleportPacket(int entityId, Vector pos, float yaw, float pitch, boolean onGround) {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer(5 + Double.BYTES * 3 + 2 + 1));
        ClientboundTeleportEntityPacket packet;
        try {
            buf.writeVarInt(entityId);
            buf.writeDouble(pos.getX());
            buf.writeDouble(pos.getY());
            buf.writeDouble(pos.getZ());
            buf.writeByte((int) (yaw * 256f / 360f));
            buf.writeByte((int) (pitch * 256f / 360f));
            buf.writeBoolean(onGround);

            packet = (ClientboundTeleportEntityPacket) CLIENTBOUND_TELEPORT_ENTITY_PACKET_CONSTRUCTOR.invoke(buf);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        } finally {
            buf.release();
        }
        return PacketContainer.wrap(this, packet);
    }

    @Override
    public PacketContainer<?> createItemRotationPacket(int entityId, int rotation) {
        SynchedEntityData entityData;
        try {
            entityData = (SynchedEntityData) SYNCHED_ENTITY_DATA_CONSTRUCTOR.invoke(FAKED_ENTITY, MAP_ROTATION_FIELDS);
        } catch (Throwable throwable) {
            throw new RuntimeException(throwable);
        }

        entityData.set(ItemFrame.DATA_ROTATION, rotation); // item rotation (0-7)

        return PacketContainer.wrap(this, new ClientboundSetEntityDataPacket(entityId, Objects.requireNonNull(entityData.packDirty())));
    }

    @SuppressWarnings("unchecked")
    @Override
    public void sendBundled(Player player, PacketContainer<?>... packets) {
        List<Packet<? super ClientGamePacketListener>> mcPackets = new ArrayList<>(packets.length);
        for (PacketContainer<?> packetContainer : packets) {
            mcPackets.add((Packet<ClientGamePacketListener>) packetContainer.getPacket());
        }
        ClientboundBundlePacket packet = new ClientboundBundlePacket(mcPackets);
        ((CraftPlayer) player).getHandle().connection.send(packet);
    }
}