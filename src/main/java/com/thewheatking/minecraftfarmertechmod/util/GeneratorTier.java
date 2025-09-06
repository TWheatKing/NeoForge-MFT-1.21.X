package com.thewheatking.minecraftfarmertechmod.util;

public enum GeneratorTier {
    TIER_1("Coal", 20, 0x404040),      // Coal Generator - 20 FE/tick
    TIER_2("Solar", 40, 0xFF6600),      // Future Lava Generator - 40 FE/tick
    TIER_3("Bio Fuel", 60, 0x40FF40),  // Bio Generator - 60 FE/tick
    TIER_4("Nuclear", 100, 0x00FF00);  // Future Nuclear - 100 FE/tick

    private final String name;
    private final int energyPerTick;
    private final int color;

    GeneratorTier(String name, int energyPerTick, int color) {
        this.name = name;
        this.energyPerTick = energyPerTick;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public int getEnergyPerTick() {
        return energyPerTick;
    }

    public int getColor() {
        return color;
    }
}