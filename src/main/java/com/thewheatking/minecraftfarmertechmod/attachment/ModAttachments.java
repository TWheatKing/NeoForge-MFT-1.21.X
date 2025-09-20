package com.thewheatking.minecraftfarmertechmod.attachment;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import com.thewheatking.minecraftfarmertechmod.capability.PlayerWildflowerLuck;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, MinecraftFarmerTechMod.MOD_ID);

    public static final Supplier<AttachmentType<PlayerWildflowerLuck>> WILDFLOWER_LUCK =
            ATTACHMENT_TYPES.register("wildflower_luck", () ->
                    AttachmentType.serializable(PlayerWildflowerLuck::new).build());
}