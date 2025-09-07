package com.thewheatking.minecraftfarmertechmod.config;

import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;

/**
 * Interface for block entities that support side configuration
 */
public interface SideConfigurable {

    /**
     * Get the side configuration for this machine
     */
    SideConfig getSideConfig();

    /**
     * Set the side configuration for this machine
     */
    void setSideConfig(SideConfig config);

    /**
     * Called when a side configuration changes
     */
    default void onSideConfigChanged(Direction side, SideConfig.SideMode newMode) {
        // Override in implementations if needed
    }

    /**
     * Check if the given side can accept the specified type of interaction
     */
    default boolean canInteractOnSide(Direction side, InteractionType type) {
        SideConfig.SideMode mode = getSideConfig().getSideMode(side);
        return switch (type) {
            case ITEM_INPUT -> getSideConfig().canInput(side);
            case ITEM_OUTPUT -> getSideConfig().canOutput(side);
            case ENERGY_INPUT -> getSideConfig().canInputEnergy(side);
            case ENERGY_OUTPUT -> getSideConfig().canOutputEnergy(side);
            case FLUID_INPUT -> getSideConfig().canInput(side);
            case FLUID_OUTPUT -> getSideConfig().canOutput(side);
        };
    }

    /**
     * Open the configuration GUI for this machine
     */
    void openConfigGui(Player player);

    enum InteractionType {
        ITEM_INPUT,
        ITEM_OUTPUT,
        ENERGY_INPUT,
        ENERGY_OUTPUT,
        FLUID_INPUT,
        FLUID_OUTPUT
    }
}