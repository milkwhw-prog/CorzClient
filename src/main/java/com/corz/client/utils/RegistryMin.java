package com.corz.client.utils;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.block.BlockState;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public final class RegistryMin {
   private final Map<Long, Integer> numberIntegerMap;
   private final AtomicInteger atomicInteger;
   private final List<BlockPos> copyOf;
   private int minRegistryBound;
   private final RegistryKey<World> registryKey;
   private final AtomicInteger atomicIntegerAtomicInteger;
   private final AtomicInteger registryAtomicInteger;
   private final long timeMillis = System.currentTimeMillis();
   private volatile boolean registryActive;
   private final List<BlockState> copyOf2Block;

   public BlockPos getBlockPos(int index) {
      return this.copyOf.get(index);
   }

   public BlockState getBlockState(int index) {
      return this.copyOf2Block.get(index);
   }

   public RegistryMin(List<BlockPos> copyOf, List<BlockState> copyOf2, Map<Long, Integer> map, RegistryKey<World> registryKey) {
      this.registryAtomicInteger = new AtomicInteger();
      this.atomicInteger = new AtomicInteger();
      this.atomicIntegerAtomicInteger = new AtomicInteger();
      if (copyOf.size() != copyOf2.size()) {
         throw new IllegalArgumentException("positions/states must be index-aligned: " + copyOf.size() + " vs " + copyOf2.size());
      } else {
         this.copyOf = copyOf;
         this.copyOf2Block = copyOf2;
         this.numberIntegerMap = map == null ? Map.of() : map;
         this.registryKey = registryKey;
      }
   }

   public Integer getNumber(BlockPos pos2) {
      return this.numberIntegerMap.get(pos2.asLong());
   }

   public int getValue() {
      return this.copyOf.size();
   }

   public int getAtomicInteger() {
      return this.registryAtomicInteger.get();
   }

   public void updateAtomicInteger() {
      this.registryAtomicInteger.incrementAndGet();
   }

   public boolean isFlag() {
      return this.registryActive;
   }

   public int getAtomicIntegerAtomicInteger() {
      return this.atomicIntegerAtomicInteger.get();
   }

   public float getMin() {
      return this.getValue() == 0 ? 1.0F : Math.min(1.0F, (float)this.getAtomicIntegerRegistryAtomicInteger() / this.getValue());
   }

   public boolean isMin() {
      return this.minRegistryBound >= this.getValue();
   }

   public int getMinRegistryBound() {
      return this.minRegistryBound;
   }

   public long getTimeMillis() {
      return this.timeMillis;
   }

   public boolean isFlag2() {
      return this.isMin() && this.getAtomicIntegerRegistryAtomicInteger() >= this.getValue();
   }

   public void updateFlag() {
      this.registryActive = true;
   }

   public int getAtomicIntegerInt() {
      return this.atomicInteger.get();
   }

   public int getAtomicIntegerRegistryAtomicInteger() {
      return this.registryAtomicInteger.get() + this.atomicInteger.get() + this.atomicIntegerAtomicInteger.get();
   }

   public void updateAtomicInteger2() {
      this.atomicInteger.incrementAndGet();
   }

   public void handleAtomicInteger(int atomicIntegerLimit) {
      if (atomicIntegerLimit > 0) {
         this.atomicIntegerAtomicInteger.addAndGet(atomicIntegerLimit);
      }
   }

   public int getMin(int registryBound) {
      this.minRegistryBound = Math.min(this.getValue(), this.minRegistryBound + Math.max(0, registryBound));
      return this.minRegistryBound;
   }

   public RegistryKey<World> getRegistryKey() {
      return this.registryKey;
   }
}
