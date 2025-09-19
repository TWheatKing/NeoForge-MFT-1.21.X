package com.thewheatking.minecraftfarmertechmod.sound;

import com.thewheatking.minecraftfarmertechmod.MinecraftFarmerTechMod;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.JukeboxSong;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, MinecraftFarmerTechMod.MOD_ID);

    public static final Supplier<SoundEvent> CHISEL_USE = registerSoundEvent("chisel_use");

    public static final Supplier<SoundEvent> ZINC_BLOCK_BREAK = registerSoundEvent("zinc_block_break");
    public static final Supplier<SoundEvent> ZINC_BLOCK_STEP = registerSoundEvent("zinc_block_step");
    public static final Supplier<SoundEvent> ZINC_BLOCK_PLACE = registerSoundEvent("zinc_block_place");
    public static final Supplier<SoundEvent> ZINC_BLOCK_HIT = registerSoundEvent("zinc_block_hit");
    public static final Supplier<SoundEvent> ZINC_BLOCK_FALL = registerSoundEvent("zinc_block_fall");

    public static final DeferredSoundType ZINC_BLOCK_SOUNDS = new DeferredSoundType(1f, 1f,
            ModSounds.ZINC_BLOCK_BREAK, ModSounds.ZINC_BLOCK_STEP, ModSounds.ZINC_BLOCK_PLACE,
            ModSounds.ZINC_BLOCK_HIT, ModSounds.ZINC_BLOCK_FALL);

    public static final Supplier<SoundEvent> BAR_BRAWL = registerSoundEvent("bar_brawl");
    public static final ResourceKey<JukeboxSong> BAR_BRAWL_KEY = createSong("bar_brawl");

    private static ResourceKey<JukeboxSong> createSong(String name) {
        return ResourceKey.create(Registries.JUKEBOX_SONG, ResourceLocation.fromNamespaceAndPath(MinecraftFarmerTechMod.MOD_ID, name));
    }


    private static Supplier<SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(MinecraftFarmerTechMod.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}