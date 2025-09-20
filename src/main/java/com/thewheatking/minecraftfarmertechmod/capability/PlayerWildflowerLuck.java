package com.thewheatking.minecraftfarmertechmod.capability;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class PlayerWildflowerLuck implements INBTSerializable<CompoundTag> {
    private int consecutiveFailures = 0;
    private static final float BASE_CHANCE = 0.1f; // 10%
    private static final float BONUS_PER_FAILURE = 0.05f; // 5% per failure
    private static final int MAX_FAILURES = 20; // Cap at 20 failures (110% max chance)

    public void incrementFailures() {
        if (consecutiveFailures < MAX_FAILURES) {
            consecutiveFailures++;
        }
    }

    public void resetFailures() {
        consecutiveFailures = 0;
    }

    public float getCurrentChance() {
        return Math.min(1.0f, BASE_CHANCE + (consecutiveFailures * BONUS_PER_FAILURE));
    }

    public int getFailureCount() {
        return consecutiveFailures;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("wildflower_failures", consecutiveFailures);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        consecutiveFailures = tag.getInt("wildflower_failures");
    }
}