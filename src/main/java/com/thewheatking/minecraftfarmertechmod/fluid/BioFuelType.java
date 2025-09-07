package com.thewheatking.minecraftfarmertechmod.fluid;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.BlockAndTintGetter;
import net.neoforged.neoforge.common.SoundActions;
import net.neoforged.neoforge.fluids.FluidType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.material.FluidState;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import java.util.function.Consumer;

public class BioFuelType extends FluidType {

    public BioFuelType() {
        super(FluidType.Properties.create()
                .density(1000)  // Heavier than water (1000)
                .viscosity(2000)  // More viscous than water
                .temperature(300)
                .canSwim(true)  // Can swim in it
                .canDrown(true)  // Can drown in it
                .canConvertToSource(false)  // Cannot create infinite sources
                .fallDistanceModifier(0.0F)
                .supportsBoating(true)  // Boats sink in bio fuel
                .sound(SoundActions.BUCKET_FILL, SoundEvents.BUCKET_FILL_LAVA)  // Gloopy sound
                .sound(SoundActions.BUCKET_EMPTY, SoundEvents.BUCKET_EMPTY_LAVA)
                .sound(SoundActions.FLUID_VAPORIZE, SoundEvents.FIRE_EXTINGUISH));
    }

    @Override
    @SuppressWarnings("removal")
    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {
            private static final ResourceLocation STILL_TEXTURE = ResourceLocation.fromNamespaceAndPath(
                    "minecraftfarmertechmod", "block/bio_fuel_still");
            private static final ResourceLocation FLOWING_TEXTURE = ResourceLocation.fromNamespaceAndPath(
                    "minecraftfarmertechmod", "block/bio_fuel_flow");
            private static final ResourceLocation UNDERWATER_TEXTURE = ResourceLocation.fromNamespaceAndPath(
                    "minecraftfarmertechmod", "block/biofuel_overlay");

            @Override
            public ResourceLocation getStillTexture() {
                return STILL_TEXTURE;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return FLOWING_TEXTURE;
            }

            @Override
            public ResourceLocation getRenderOverlayTexture(net.minecraft.client.Minecraft mc) {
                return UNDERWATER_TEXTURE;
            }

            @Override
            public int getTintColor() {
                // Changed from 0xFF40FF40 to 0x9940FF40
                // First two digits control opacity:
                // FF = 100% opaque, CC = 80%, 99 = 60%, 66 = 40%, 33 = 20%
                return 0x9940FF40; // 60% opacity green
            }

            @Override
            public int getTintColor(FluidState state, BlockAndTintGetter getter, BlockPos pos) {
                return 0x9940FF40; // Same 60% opacity green
            }

            @Override
            public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level,
                                                    int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
                return new Vector3f(0.2F, 0.6F, 0.2F);
            }
        });
    }
}