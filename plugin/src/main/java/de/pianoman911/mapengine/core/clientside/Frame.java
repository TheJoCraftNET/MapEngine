package de.pianoman911.mapengine.core.clientside;

import de.pianoman911.mapengine.common.platform.PacketContainer;
import de.pianoman911.mapengine.core.MapEnginePlugin;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import org.bukkit.Bukkit;
import org.bukkit.block.BlockFace;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

public class Frame extends FilledMap {

    public static final double INVISIBLE_MAP_DEPTH = 0.0078125;
    public static final double INTERACTION_OFFSET = 0.0626;

    @SuppressWarnings("deprecation") // unsafe api, don't care + don't ask
    protected final int entityId = Bukkit.getUnsafe().nextEntityId();
    @SuppressWarnings("deprecation")
    protected final int interactionId = Bukkit.getUnsafe().nextEntityId();
    protected final BlockFace direction;
    protected final BlockVector pos;

    protected Frame(MapEnginePlugin plugin, BlockFace direction, BlockVector pos) {
        super(plugin);
        this.direction = direction;
        this.pos = pos;
    }

    protected PacketContainer<?> spawnPacket() {
        return plugin.platform().createMapEntitySpawnPacket(entityId, pos, direction);
    }

    protected PacketContainer<?> interactionEntity() {
        Vector interactionPos;
        switch (direction) {
            case WEST -> {
                interactionPos = new Vector(pos.getX() + 1.5, pos.getY(), pos.getZ() + 0.5);
                interactionPos.setX(interactionPos.getX() - INTERACTION_OFFSET);
            }
            case EAST -> {
                interactionPos = new Vector(pos.getX() - 0.5, pos.getY(), pos.getZ() + 0.5);
                interactionPos.setX(interactionPos.getX() + INTERACTION_OFFSET);
            }
            case NORTH -> {
                interactionPos = new Vector(pos.getX() + 0.5, pos.getY(), pos.getZ() + 1.5);
                interactionPos.setZ(interactionPos.getZ() - INTERACTION_OFFSET);
            }
            case SOUTH -> {
                interactionPos = new Vector(pos.getX() + 0.5, pos.getY(), pos.getZ() - 0.5);
                interactionPos.setZ(interactionPos.getZ() + INTERACTION_OFFSET);
            }
            case UP -> {
                interactionPos = new Vector(pos.getX() + 0.5, pos.getY() - 1, pos.getZ() + 0.5);
                interactionPos.setY(interactionPos.getY() + INTERACTION_OFFSET);
            }
            case DOWN -> {
                interactionPos = new Vector(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
                interactionPos.setY(interactionPos.getY() - INTERACTION_OFFSET);
            }
            default -> throw new IllegalStateException("Unexpected value: " + direction);
        }
        return plugin.platform().createInteractionEntitySpawnPacket(interactionId, interactionPos, direction);
    }

    protected PacketContainer<?> interactionEntitySize() {
        return plugin.platform().createInteractionEntityBlockSizePacket(interactionId);
    }

    protected PacketContainer<?> removePacket() {
        return plugin.platform().createRemoveEntitiesPacket(new IntArrayList(entityId));
    }

    protected PacketContainer<?> setIdPacket(int z, boolean invisible) {
        return plugin.platform().createMapSetIdPacket(entityId, mapId(z), invisible);
    }

    public BlockVector pos() {
        return pos;
    }
}
