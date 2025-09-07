package com.thewheatking.minecraftfarmertechmod.mixin;

import com.thewheatking.minecraftfarmertechmod.fluid.ModFluids;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.FluidState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class BioFuelDrowningMixin {

    @Inject(method = "baseTick", at = @At("HEAD"))
    private void checkBioFuelDrowning(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity)(Object)this;

        if (entity instanceof Player player) {
            BlockPos pos = entity.blockPosition();
            FluidState fluidState = entity.level().getFluidState(pos);

            // Check if player is in bio fuel
            if (fluidState.is(ModFluids.BIOFUEL.get()) || fluidState.is(ModFluids.FLOWING_BIOFUEL.get())) {
                // Check if player's head is submerged - cast double to int
                BlockPos headPos = new BlockPos(pos.getX(), (int)entity.getEyeY(), pos.getZ());
                FluidState headFluid = entity.level().getFluidState(headPos);

                if ((headFluid.is(ModFluids.BIOFUEL.get()) || headFluid.is(ModFluids.FLOWING_BIOFUEL.get()))
                        && entity.getAirSupply() <= 0) {
                    // Custom drowning damage
                    entity.hurt(entity.damageSources().drown(), 2.0F);

                    // Grant advancement if player takes damage
                    if (player.getServer() != null) {
                        // Trigger advancement
                    }
                }
            }
        }
    }
}