package com.thewheatking.minecraftfarmertechmod.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.*;

/**
 * Handles energy distribution between connected machines and cables.
 * This system finds connected networks and distributes power automatically.
 */
public class MftEnergyNetwork {

    private final Level level;
    private final Set<BlockPos> networkNodes = new HashSet<>();
    private final Set<BlockPos> energyProducers = new HashSet<>();
    private final Set<BlockPos> energyConsumers = new HashSet<>();
    private final Set<BlockPos> cables = new HashSet<>();

    public MftEnergyNetwork(Level level) {
        this.level = level;
    }

    /**
     * Discovers and maps the energy network starting from a given position
     */
    public void discoverNetwork(BlockPos startPos) {
        networkNodes.clear();
        energyProducers.clear();
        energyConsumers.clear();
        cables.clear();

        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> toVisit = new LinkedList<>();
        toVisit.add(startPos);

        while (!toVisit.isEmpty()) {
            BlockPos pos = toVisit.poll();
            if (visited.contains(pos)) continue;
            visited.add(pos);

            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity == null) continue;

            // Check if this block has energy capability
            var energyStorage = level.getCapability(ModEnergyCapabilities.ENERGY, pos, null);
            if (energyStorage != null) {
                networkNodes.add(pos);

                // Categorize the block
                if (isEnergyProducer(blockEntity)) {
                    energyProducers.add(pos);
                } else if (isEnergyConsumer(blockEntity)) {
                    energyConsumers.add(pos);
                } else if (isEnergyCable(blockEntity)) {
                    cables.add(pos);
                }

                // Add adjacent blocks to search queue
                for (Direction direction : Direction.values()) {
                    BlockPos adjacent = pos.relative(direction);
                    if (!visited.contains(adjacent)) {
                        toVisit.add(adjacent);
                    }
                }
            }
        }
    }

    /**
     * Distributes energy throughout the network
     */
    public void distributeEnergy() {
        if (energyProducers.isEmpty() || energyConsumers.isEmpty()) return;

        // Calculate total energy available from producers
        int totalEnergyAvailable = 0;
        List<IEnergyStorage> producers = new ArrayList<>();

        for (BlockPos pos : energyProducers) {
            var energyStorage = level.getCapability(ModEnergyCapabilities.ENERGY, pos, null);
            if (energyStorage != null && energyStorage.canExtract()) {
                producers.add(energyStorage);
                totalEnergyAvailable += energyStorage.getEnergyStored();
            }
        }

        if (totalEnergyAvailable <= 0) return;

        // Calculate total energy demand from consumers
        int totalEnergyDemand = 0;
        List<ConsumerData> consumers = new ArrayList<>();

        for (BlockPos pos : energyConsumers) {
            var energyStorage = level.getCapability(ModEnergyCapabilities.ENERGY, pos, null);
            if (energyStorage != null && energyStorage.canReceive()) {
                int demand = energyStorage.getMaxEnergyStored() - energyStorage.getEnergyStored();
                if (demand > 0) {
                    consumers.add(new ConsumerData(energyStorage, demand));
                    totalEnergyDemand += demand;
                }
            }
        }

        if (consumers.isEmpty()) return;

        // Distribute energy proportionally to demand
        int energyToDistribute = Math.min(totalEnergyAvailable, totalEnergyDemand);
        int distributedEnergy = 0;

        for (ConsumerData consumer : consumers) {
            if (distributedEnergy >= energyToDistribute) break;

            // Calculate how much energy this consumer should get
            float demandRatio = (float) consumer.demand / (float) totalEnergyDemand;
            int energyForConsumer = Math.round(energyToDistribute * demandRatio);
            energyForConsumer = Math.min(energyForConsumer, energyToDistribute - distributedEnergy);

            if (energyForConsumer > 0) {
                // Extract energy from producers and give to consumer
                int energyTransferred = transferEnergy(producers, consumer.storage, energyForConsumer);
                distributedEnergy += energyTransferred;
            }
        }
    }

    /**
     * Transfers energy from producers to a consumer
     */
    private int transferEnergy(List<IEnergyStorage> producers, IEnergyStorage consumer, int maxTransfer) {
        int totalTransferred = 0;

        for (IEnergyStorage producer : producers) {
            if (totalTransferred >= maxTransfer) break;

            int toTransfer = maxTransfer - totalTransferred;
            int extracted = producer.extractEnergy(toTransfer, false);

            if (extracted > 0) {
                int received = consumer.receiveEnergy(extracted, false);
                totalTransferred += received;

                // Return any energy that couldn't be received
                if (received < extracted) {
                    producer.receiveEnergy(extracted - received, false);
                }
            }
        }

        return totalTransferred;
    }

    private boolean isEnergyProducer(BlockEntity blockEntity) {
        // Check if this is a generator or other energy producer
        return blockEntity.getClass().getSimpleName().contains("Generator");
    }

    private boolean isEnergyConsumer(BlockEntity blockEntity) {
        String className = blockEntity.getClass().getSimpleName();
        return className.contains("Machine") || className.contains("Battery") ||
                className.contains("Storage") || className.contains("Liquifier");
    }

    private boolean isEnergyCable(BlockEntity blockEntity) {
        // Check if this is a cable
        return blockEntity.getClass().getSimpleName().contains("Cable");
    }

    private static class ConsumerData {
        final IEnergyStorage storage;
        final int demand;

        ConsumerData(IEnergyStorage storage, int demand) {
            this.storage = storage;
            this.demand = demand;
        }
    }
}