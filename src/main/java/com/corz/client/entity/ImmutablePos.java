package com.corz.client.entity;

import com.corz.client.inventory.BlockEntityUtils;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public final class ImmutablePos {
   private ImmutablePos.Kind breakObsidianType;
   private final int sequenceIndex;
   private final BlockPos immutable;
   private long actionTimeMs;
   private EndCrystalEntity endCrystalEntity;
   private final BlockPos blockPos;
   private long breakValue = Long.MIN_VALUE;
   private final Direction direction;
   private double plannedScore;

   public static ImmutablePos getImmutableBreak(EndCrystalEntity endCrystalEntity, double immutableBreakValue, long posDownValue, int immutableBreakValue3) {
      return new ImmutablePos(ImmutablePos.Kind.BREAK, BlockEntityUtils.getDown(endCrystalEntity).toImmutable(), endCrystalEntity, immutableBreakValue, posDownValue, immutableBreakValue3);
   }

   public static ImmutablePos getImmutableObsidian(BlockPos pos, double immutableObsidianValue, long posDownValue, int immutableObsidianValue3) {
      return new ImmutablePos(ImmutablePos.Kind.OBSIDIAN, pos.toImmutable(), null, immutableObsidianValue, posDownValue, immutableObsidianValue3);
   }

   public ImmutablePos.Kind getBreakObsidianTypeKind() {
      return this.breakObsidianType;
   }

   public static ImmutablePos getImmutablePlace(BlockPos pos, double immutablePlaceValue, long posDownValue, int immutablePlaceValue3) {
      return new ImmutablePos(ImmutablePos.Kind.PLACE, pos.toImmutable(), null, immutablePlaceValue, posDownValue, immutablePlaceValue3);
   }

   public static ImmutablePos getImmutableObsidian(BlockPos pos, double immutableObsidianValue, long posDownValue, int immutableObsidianValue3, BlockPos pos2, Direction direction) {
      return new ImmutablePos(ImmutablePos.Kind.OBSIDIAN, pos.toImmutable(), null, immutableObsidianValue, posDownValue, immutableObsidianValue3, pos2, direction);
   }

   private ImmutablePos(ImmutablePos.Kind kind2, BlockPos pos, EndCrystalEntity endCrystalEntity, double immutablePosValue, long immutablePosValue2, int immutablePosValue3) {
      this(kind2, pos, endCrystalEntity, immutablePosValue, immutablePosValue2, immutablePosValue3, null, null);
   }

   public void handleBreakTime(EndCrystalEntity endCrystalEntity, long time) {
      this.breakObsidianType = ImmutablePos.Kind.BREAK;
      this.endCrystalEntity = endCrystalEntity;
      this.breakValue = Long.MIN_VALUE;
      this.actionTimeMs = time;
   }

   public BlockPos getDown() {
      return this.blockPos != null ? this.blockPos : this.immutable.down();
   }

   public BlockPos getImmutable() {
      return this.immutable;
   }

   private ImmutablePos(
      ImmutablePos.Kind breakObsidianType, BlockPos immutable, EndCrystalEntity endCrystalEntity, double amount, long time, int value, BlockPos pos, Direction direction
   ) {
      this.breakObsidianType = breakObsidianType;
      this.immutable = immutable;
      this.endCrystalEntity = endCrystalEntity;
      this.plannedScore = amount;
      this.actionTimeMs = time;
      this.sequenceIndex = value;
      boolean immutablePosFlag = pos != null && direction != null;
      this.blockPos = immutablePosFlag ? pos.toImmutable() : null;
      this.direction = immutablePosFlag ? direction : null;
   }

   public boolean isTime() {
      return this.breakValue != Long.MIN_VALUE;
   }

   public int getValue() {
      return this.sequenceIndex;
   }

   public double getAmount() {
      return this.plannedScore;
   }

   public void handleTimeBreak(long time) {
      this.breakObsidianType = ImmutablePos.Kind.PLACE;
      this.endCrystalEntity = null;
      this.breakValue = Long.MIN_VALUE;
      this.actionTimeMs = time;
   }

   public void setAmount(double amount) {
      this.plannedScore = amount;
   }

   public void handleTime(long time) {
      this.breakValue = time;
      this.actionTimeMs = time;
   }

   public long getTime() {
      return this.actionTimeMs;
   }

   public void setTime(long time) {
      this.actionTimeMs = time;
   }

   public long getBreakValue() {
      return this.breakValue;
   }

   public EndCrystalEntity getEndCrystalEntity() {
      return this.endCrystalEntity;
   }

   public Direction getUp() {
      return this.blockPos != null ? this.direction : Direction.UP;
   }

   public static enum Kind {
      PLACE,
      BREAK,
      OBSIDIAN;
   }
}
