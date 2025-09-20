package com.thewheatking.minecraftfarmertechmod.item.custom;

import com.thewheatking.minecraftfarmertechmod.item.ModItems;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CornItem extends Item {

    public CornItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        // Call the parent method to handle the eating
        ItemStack result = super.finishUsingItem(stack, level, livingEntity);

        // Give seeds to player after eating
        if (livingEntity instanceof Player player && !level.isClientSide()) {
            ItemStack seeds = new ItemStack(ModItems.CORN_SEEDS.get(), 1);

            // Try to add to inventory, or drop if full
            if (!player.getInventory().add(seeds)) {
                player.drop(seeds, false);
            }
        }

        return result;
    }
}