package com.thewheatking.minecraftfarmertechmod.config;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

import java.util.EnumMap;
import java.util.Map;

/**
 * Manages side configuration for machines - what each face does
 */
public class SideConfig {

    public enum SideMode {
        NONE("None", 0xFF808080),           // Gray - disabled
        INPUT("Input", 0xFF00FF00),         // Green - accepts items/fluids
        OUTPUT("Output", 0xFFFF0000),       // Red - outputs items/fluids
        INPUT_OUTPUT("Both", 0xFF0000FF),   // Blue - both input and output
        ENERGY_INPUT("Energy In", 0xFFFFFF00),  // Yellow - energy input only
        ENERGY_OUTPUT("Energy Out", 0xFFFF8000); // Orange - energy output only

        private final String displayName;
        private final int color;

        SideMode(String displayName, int color) {
            this.displayName = displayName;
            this.color = color;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getColor() {
            return color;
        }

        public SideMode next() {
            return values()[(ordinal() + 1) % values().length];
        }
    }

    private final Map<Direction, SideMode> sideConfigs = new EnumMap<>(Direction.class);

    public SideConfig() {
        // Default configuration
        for (Direction direction : Direction.values()) {
            sideConfigs.put(direction, SideMode.NONE);
        }
    }

    public SideMode getSideMode(Direction side) {
        return sideConfigs.getOrDefault(side, SideMode.NONE);
    }

    public void setSideMode(Direction side, SideMode mode) {
        sideConfigs.put(side, mode);
    }

    public void cycleSideMode(Direction side) {
        SideMode current = getSideMode(side);
        setSideMode(side, current.next());
    }

    public boolean canInput(Direction side) {
        SideMode mode = getSideMode(side);
        return mode == SideMode.INPUT || mode == SideMode.INPUT_OUTPUT;
    }

    public boolean canOutput(Direction side) {
        SideMode mode = getSideMode(side);
        return mode == SideMode.OUTPUT || mode == SideMode.INPUT_OUTPUT;
    }

    public boolean canInputEnergy(Direction side) {
        SideMode mode = getSideMode(side);
        return mode == SideMode.ENERGY_INPUT || mode == SideMode.INPUT_OUTPUT;
    }

    public boolean canOutputEnergy(Direction side) {
        SideMode mode = getSideMode(side);
        return mode == SideMode.ENERGY_OUTPUT || mode == SideMode.INPUT_OUTPUT;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        ListTag sidesTag = new ListTag();

        for (Direction direction : Direction.values()) {
            CompoundTag sideTag = new CompoundTag();
            sideTag.putString("direction", direction.name());
            sideTag.putString("mode", getSideMode(direction).name());
            sidesTag.add(sideTag);
        }

        tag.put("sides", sidesTag);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        if (tag.contains("sides", Tag.TAG_LIST)) {
            ListTag sidesTag = tag.getList("sides", Tag.TAG_COMPOUND);

            for (int i = 0; i < sidesTag.size(); i++) {
                CompoundTag sideTag = sidesTag.getCompound(i);
                String directionName = sideTag.getString("direction");
                String modeName = sideTag.getString("mode");

                try {
                    Direction direction = Direction.valueOf(directionName);
                    SideMode mode = SideMode.valueOf(modeName);
                    setSideMode(direction, mode);
                } catch (IllegalArgumentException e) {
                    // Skip invalid entries
                }
            }
        }
    }

    public SideConfig copy() {
        SideConfig copy = new SideConfig();
        for (Map.Entry<Direction, SideMode> entry : sideConfigs.entrySet()) {
            copy.setSideMode(entry.getKey(), entry.getValue());
        }
        return copy;
    }
}