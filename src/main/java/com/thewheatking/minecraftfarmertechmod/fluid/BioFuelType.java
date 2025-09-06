package com.thewheatking.minecraftfarmertechmod.fluid;

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
    // In BioFuelType.java
    public static final ResourceLocation STILL_TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraftfarmertechmod", "block/biofuel_still");
    public static final ResourceLocation FLOWING_TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraftfarmertechmod", "block/biofuel_flow");
    public static final ResourceLocation OVERLAY_TEXTURE = ResourceLocation.fromNamespaceAndPath("minecraftfarmertechmod", "block/biofuel_overlay");
    public BioFuelType() {
        super(FluidType.Properties.create()
                .density(1000)
                .viscosity(1000)
                .temperature(300)
                .canSwim(true)
                .canDrown(true)
                .fallDistanceModifier(0.0F)
                .supportsBoating(true));
    }


    public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
        consumer.accept(new IClientFluidTypeExtensions() {

            @Override
            public ResourceLocation getStillTexture() {
                return STILL_TEXTURE;
            }

            @Override
            public ResourceLocation getFlowingTexture() {
                return FLOWING_TEXTURE;
            }

            @Override
            public ResourceLocation getOverlayTexture() {
                return OVERLAY_TEXTURE;
            }

            @Override
            public int getTintColor() {
                return 0xFF40FF40; // Green tint
            }

            @Override
            public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level,
                                                    int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor) {
                // Green tint for bio fuel fog
                return new Vector3f(0.2F, 0.6F, 0.2F);
            }
        });
    }
}