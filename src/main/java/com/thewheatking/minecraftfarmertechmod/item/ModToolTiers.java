package com.thewheatking.minecraftfarmertechmod.item;

import com.thewheatking.minecraftfarmertechmod.util.ModTags;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.common.SimpleTier;

public class ModToolTiers {
    public static final Tier ZINC = new SimpleTier(ModTags.Blocks.INCORRECT_FOR_ZINC_TOOL,
            1450, 7f, 4f, 28, () -> Ingredient.of(ModItems.ZINC_INGOT));

}
