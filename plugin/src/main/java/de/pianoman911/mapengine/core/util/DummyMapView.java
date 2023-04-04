package de.pianoman911.mapengine.core.util;

import org.bukkit.World;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public record DummyMapView(int id) implements MapView {

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean isVirtual() {
        return true;
    }

    @Override
    public @NotNull
    Scale getScale() {
        return Scale.NORMAL;
    }

    @Override
    public void setScale(@NotNull Scale scale) {

    }

    @Override
    public int getCenterX() {
        return 0;
    }

    @Override
    public int getCenterZ() {
        return 0;
    }

    @Override
    public void setCenterX(int x) {

    }

    @Override
    public void setCenterZ(int z) {

    }

    @Override
    public @Nullable
    World getWorld() {
        return null;
    }

    @Override
    public void setWorld(@NotNull World world) {

    }

    @Override
    public @NotNull
    List<MapRenderer> getRenderers() {
        return Collections.emptyList();
    }

    @Override
    public void addRenderer(@NotNull MapRenderer renderer) {

    }

    @Override
    public boolean removeRenderer(@Nullable MapRenderer renderer) {
        return true;
    }

    @Override
    public boolean isTrackingPosition() {
        return false;
    }

    @Override
    public void setTrackingPosition(boolean trackingPosition) {

    }

    @Override
    public boolean isUnlimitedTracking() {
        return false;
    }

    @Override
    public void setUnlimitedTracking(boolean unlimited) {

    }

    @Override
    public boolean isLocked() {
        return true;
    }

    @Override
    public void setLocked(boolean locked) {

    }
}
