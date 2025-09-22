package com.thewheatking.minecraftfarmertechmod.common.blockentity.transmission;

import com.thewheatking.minecraftfarmertechmod.hybrid.HybridBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

/**
 * Copper Cable Block Entity - Tier 1 Energy Transmission (UPDATED)
 * Transfer Rate: 512 FE/tick, Explodes at 2048+ FE/tick
 */
public class CopperCableBlockEntity extends EnergyTransmissionBlockEntity {

    // Copper Cable Specifications (Tier 1)
    private static final int TRANSFER_RATE = 512;
    private static final int EXPLOSION_THRESHOLD = 2048; // Tier 2 threshold
    private static final float ELECTRICAL_DAMAGE = 2.0f;

    public CopperCableBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.COPPER_CABLE.get(), pos, state, 0, TRANSFER_RATE, TRANSFER_RATE, 0);
    }

    // Electrical State
    private boolean isCarryingEnergy = false;
    private int energyFlowAmount = 0;
    private long lastDamageTime = 0L;
    private int overloadTicks = 0;
    private static final int OVERLOAD_EXPLOSION_DELAY = 60;  // 3 seconds before explosion

    public CopperCableBlockEntity(BlockPos pos, BlockState state) {
        super(HybridBlockEntities.COPPER_CABLE.get(), pos, state,
                0, TRANSFER_RATE, TRANSFER_RATE, 0); // No energy storage, just transfer
    }

    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        super.serverTick(level, pos, state);

        // Monitor energy flow and handle electrical effects
        monitorEnergyFlow();

        // Handle electrical damage to nearby entities
        if (isCarryingEnergy && energyFlowAmount > 0) {
            handleElectricalDamage();
        }

        // Check for overload and potential explosion
        checkForOverload();

        // Update visual/audio effects
        updateElectricalEffects();
    }

    /**
     * Monitor energy flow through the cable
     */
    private void monitorEnergyFlow() {
        boolean wasCarryingEnergy = isCarryingEnergy;
        int totalFlow = 0;

        // Check energy flow from all connected sides
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = worldPosition.relative(direction);
            var energyCap = level.getCapability(Capabilities.EnergyStorage.BLOCK,
                    neighborPos, direction.getOpposite());

            if (energyCap != null) {
                // Estimate energy flow based on extractable energy
                int potentialFlow = energyCap.extractEnergy(TRANSFER_RATE, true);
                totalFlow += potentialFlow;
            }
        }

        energyFlowAmount = Math.min(totalFlow, TRANSFER_RATE);
        isCarryingEnergy = energyFlowAmount > 10; // Minimum threshold for "carrying energy"

        // Update working state
        isWorking = isCarryingEnergy;

        // Sync to client if energy state changed
        if (wasCarryingEnergy != isCarryingEnergy && tickCounter % 10 == 0) {
            setChanged();
            syncToClient();
        }
    }

    /**
     * Handle electrical damage to nearby entities (standard cables only)
     */
    private void handleElectricalDamage() {
        if (!canCauseElectricalDamage() || level.getGameTime() - lastDamageTime < DAMAGE_COOLDOWN) {
            return;
        }

        // Get entities in cable area (using cable's voxel shape)
        AABB damageArea = getCableDamageArea();
        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, damageArea);

        for (LivingEntity entity : entities) {
            // Only damage if entity is actually touching the cable
            if (isEntityTouchingCable(entity)) {
                causeLectricalDamage(entity);
            }
        }

        lastDamageTime = level.getGameTime();
    }

    /**
     * Check if this cable type can cause electrical damage
     */
    protected boolean canCauseElectricalDamage() {
        return true; // Standard copper cables cause damage
    }

    /**
     * Get damage area around cable (based on voxel shape)
     */
    private AABB getCableDamageArea() {
        // Cable voxel shapes are typically smaller than full block
        // Standard cable might be 6x6x6 pixels (0.375 blocks) centered
        double offset = 0.3125; // 5/16 blocks
        return new AABB(
                worldPosition.getX() + offset, worldPosition.getY() + offset, worldPosition.getZ() + offset,
                worldPosition.getX() + 1 - offset, worldPosition.getY() + 1 - offset, worldPosition.getZ() + 1 - offset
        );
    }

    /**
     * Check if entity is actually touching the cable voxel shape
     */
    private boolean isEntityTouchingCable(LivingEntity entity) {
        AABB cableArea = getCableDamageArea();
        AABB entityArea = entity.getBoundingBox();
        return cableArea.intersects(entityArea);
    }

    /**
     * Cause electrical damage to entity
     */
    private void causeLectricalDamage(LivingEntity entity) {
        // Create electrical damage source
        DamageSource electricalDamage = level.damageSources().lightningBolt();

        // Apply damage with electrical effects
        entity.hurt(electricalDamage, ELECTRICAL_DAMAGE);

        // Visual/audio effects for electrical damage
        spawnElectricalDamageEffects(entity);

        // Add brief slowness effect to simulate electrical shock
        // entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 1));
    }

    /**
     * Check for energy overload and handle explosion
     */
    private void checkForOverload() {
        // Check if we're receiving energy above our tier limit
        boolean isOverloaded = energyFlowAmount >= EXPLOSION_THRESHOLD;

        if (isOverloaded) {
            overloadTicks++;

            // Spawn warning particles and sounds
            if (overloadTicks % 10 == 0) {
                spawnOverloadWarningEffects();
            }

            // Explode after delay
            if (overloadTicks >= OVERLOAD_EXPLOSION_DELAY) {
                explodeCable();
            }
        } else {
            overloadTicks = 0;
        }
    }

    /**
     * Handle cable explosion from overload
     */
    private void explodeCable() {
        if (level != null && !level.isClientSide()) {
            // Create explosion
            level.explode(null, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5,
                    2.0f, Level.ExplosionInteraction.BLOCK);

            // Remove the cable block
            level.removeBlock(worldPosition, false);

            // Spawn electrical explosion effects
            spawnElectricalExplosionEffects();
        }
    }

    /**
     * Update electrical visual and audio effects
     */
    private void updateElectricalEffects() {
        if (level != null && level.isClientSide()) {
            if (isCarryingEnergy) {
                spawnEnergyFlowParticles();

                // Electrical sparks occasionally
                if (level.random.nextFloat() < 0.05f) {
                    spawnElectricalSparks();
                }
            }

            // Overload warning effects
            if (overloadTicks > 0) {
                spawnOverloadParticles();
            }
        }
    }

    // ========== VISUAL EFFECTS (CLIENT-SIDE) ==========

    private void spawnEnergyFlowParticles() {
        // TODO: Implement energy flow particles along cable
        // Orange particles for copper cables moving along the cable direction
    }

    private void spawnElectricalSparks() {
        // TODO: Implement electrical spark particles
        // Small electrical effects around the cable
    }

    private void spawnElectricalDamageEffects(LivingEntity entity) {
        // TODO: Implement electrical damage effects
        // Lightning-like particles and sound effects
    }

    private void spawnOverloadWarningEffects() {
        // TODO: Implement overload warning effects
        // Red particles and warning sounds
    }

    private void spawnOverloadParticles() {
        // TODO: Implement overload particles
        // Dangerous red/orange particles indicating imminent explosion
    }

    private void spawnElectricalExplosionEffects() {
        // TODO: Implement electrical explosion effects
        // Special electrical-themed explosion particles
    }

    // ========== ENERGY TRANSMISSION OVERRIDES ==========

    @Override
    protected boolean canOperate() {
        return true; // Cables are always ready to transmit
    }

    @Override
    protected void performOperation() {
        // Energy transmission is handled by the base class
        // This cable just monitors flow and handles electrical effects
    }

    @Override
    public boolean canConnectEnergy(Direction side) {
        return true; // Cables connect on all sides
    }

    @Override
    public boolean canReceiveEnergy(Direction side) {
        return true; // Can receive energy from any side
    }

    @Override
    public boolean canExtractEnergy(Direction side) {
        return true; // Can provide energy to any side
    }

    // ========== GUI DATA METHODS ==========

    public boolean isCarryingEnergy() { return isCarryingEnergy; }
    public int getEnergyFlowAmount() { return energyFlowAmount; }
    public int getTransferRate() { return TRANSFER_RATE; }
    public int getExplosionThreshold() { return EXPLOSION_THRESHOLD; }
    public boolean isOverloaded() { return overloadTicks > 0; }
    public int getOverloadTicks() { return overloadTicks; }
    public float getOverloadProgress() {
        return (float) overloadTicks / OVERLOAD_EXPLOSION_DELAY;
    }

    // ========== NBT SERIALIZATION ==========

    @Override
    protected void saveAdditionalData(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putBoolean("IsCarryingEnergy", isCarryingEnergy);
        tag.putInt("EnergyFlowAmount", energyFlowAmount);
        tag.putInt("OverloadTicks", overloadTicks);
        tag.putLong("LastDamageTime", lastDamageTime);
    }

    @Override
    protected void loadAdditionalData(CompoundTag tag, HolderLookup.Provider registries) {
        isCarryingEnergy = tag.getBoolean("IsCarryingEnergy");
        energyFlowAmount = tag.getInt("EnergyFlowAmount");
        overloadTicks = tag.getInt("OverloadTicks");
        lastDamageTime = tag.getLong("LastDamageTime");
    }

    // ========== NETWORKING ==========

    @Override
    protected void writeClientData(CompoundTag tag) {
        super.writeClientData(tag);
        tag.putBoolean("CarryingEnergy", isCarryingEnergy);
        tag.putInt("EnergyFlow", energyFlowAmount);
        tag.putInt("Overload", overloadTicks);
    }

    @Override
    protected void readClientData(CompoundTag tag) {
        super.readClientData(tag);
        isCarryingEnergy = tag.getBoolean("CarryingEnergy");
        energyFlowAmount = tag.getInt("EnergyFlow");
        overloadTicks = tag.getInt("Overload");
    }
}
