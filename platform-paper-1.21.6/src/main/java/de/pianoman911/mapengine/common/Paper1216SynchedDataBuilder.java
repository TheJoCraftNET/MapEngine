package de.pianoman911.mapengine.common;

import de.pianoman911.mapengine.common.util.ReflectionUtil;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SyncedDataHolder;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ThrownEgg;
import net.minecraft.world.item.Items;

// Use own builder to create SynchedEntityData instances
// We don't want to specify every single data item, so we can use dummies for unused fields instead
public final class Paper1216SynchedDataBuilder {

    private static final Entity FAKED_ENTITY = new ThrownEgg(MinecraftServer.getServer().overworld(), 0, 0, 0, Items.EGG.getDefaultInstance());
    private static final WrappedDataItem<?> DUMMY_ITEM = new WrappedDataItem<>(EntityDataSerializers.BYTE.createAccessor(0), (byte) 0);

    private WrappedDataItem<?>[] dataItems = new WrappedDataItem[0];

    private Paper1216SynchedDataBuilder() {
    }

    public static Paper1216SynchedDataBuilder builder() {
        return new Paper1216SynchedDataBuilder();
    }

    public <T> Paper1216SynchedDataBuilder setDataItem(EntityDataAccessor<T> accessor, T defaultValue) {
        int index = accessor.id();
        if (index >= this.dataItems.length) {
            // resize array
            WrappedDataItem<?>[] newArray = new WrappedDataItem[index + 1];
            System.arraycopy(this.dataItems, 0, newArray, 0, this.dataItems.length);

            // fill with dummy items, they will be replaced or ignored
            for (int i = this.dataItems.length; i < newArray.length; i++) {
                newArray[i] = DUMMY_ITEM;
            }
            this.dataItems = newArray;
        }
        this.dataItems[index] = new WrappedDataItem<>(accessor, defaultValue);

        return this;
    }

    public SynchedEntityData build() {
        SynchedEntityData data = ReflectionUtil.newInstance(SynchedEntityData.class);
        ReflectionUtil.setFinalField(SynchedEntityData.class, SyncedDataHolder.class, 0, data, FAKED_ENTITY);

        SynchedEntityData.DataItem<?>[] items = new SynchedEntityData.DataItem<?>[this.dataItems.length];
        for (int i = 0; i < items.length; i++) {
            items[i] = this.dataItems[i].toDataItem();
        }

        ReflectionUtil.setFinalField(SynchedEntityData.class, SynchedEntityData.DataItem[].class, 0, data, items);

        return data;
    }

    private record WrappedDataItem<T>(EntityDataAccessor<T> accessor, T value) {

        public SynchedEntityData.DataItem<T> toDataItem() {
            return new SynchedEntityData.DataItem<>(this.accessor, this.value);
        }
    }
}
