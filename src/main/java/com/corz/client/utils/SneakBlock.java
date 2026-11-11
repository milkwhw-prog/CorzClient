package com.corz.client.utils;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public final class SneakBlock {
   public Boolean sneak;
   public BlockPos longValue;
   public final BlockPos target;
   public Integer rotation;
   public SneakBlock.Failure needsSupportType;
   public final List<Direction> faces;
   public Direction click;
   public final BlockState blockState;
   public BlockPos blockPos;
   public int interact;
   public final BlockState block;
   public boolean sneakPoseFlag;
   public boolean poseSneakActive;
   public SneakBlock.Kind kind = SneakBlock.Kind.PLAIN;
   public Float yaw;
   public final List<BlockPos> poseSneakPositions = new ArrayList<>();
   public final List<BlockPos> sneakBlockBlockPosList = new ArrayList<>();
   public boolean sneakBlockActive;
   public Float floatValue;
   public int sneakBlockLimit;
   public final Item placementItem;

   public SneakBlock getPosBlock(BlockPos blockPos) {
      this.blockPos = blockPos;
      return this;
   }

   public SneakBlock getClick(BlockPos longValue, Direction click) {
      this.longValue = longValue;
      this.click = click;
      return this;
   }

   public SneakBlock getSneakBlock(Float floatValue) {
      this.floatValue = floatValue;
      return this;
   }

   public SneakBlock getKind(SneakBlock.Kind kind) {
      this.kind = kind;
      return this;
   }

   public SneakBlock getPoseSneak(boolean sneakPoseFlag) {
      this.sneakPoseFlag = sneakPoseFlag;
      return this;
   }

   public SneakBlock getSneak(Boolean sneak) {
      this.sneak = sneak;
      return this;
   }

   public SneakBlock(BlockPos target2, BlockState block, Item item) {
      this.faces = new ArrayList<>();
      this.needsSupportType = SneakBlock.Failure.NONE;
      this.target = target2;
      this.block = block;
      this.blockState = block;
      this.placementItem = item;
   }

   public SneakBlock getMax(int sneakBlockBound) {
      this.sneakBlockLimit = Math.max(0, sneakBlockBound);
      return this;
   }

   public SneakBlock getNeedsSupport(SneakBlock.Failure needsSupportType) {
      this.needsSupportType = needsSupportType;
      return this;
   }

   public SneakBlock getRotation(int rotationValue) {
      this.rotation = Math.floorMod(rotationValue, 16);
      return this;
   }

   public SneakBlock getFaces(Direction direction) {
      this.faces.add(direction);
      return this;
   }

   @Override
   public String toString() {
      return "Recipe{target="
         + (this.target == null ? "?" : this.target.toShortString())
         + " block="
         + (this.block == null ? "?" : this.block.getBlock())
         + " kind="
         + this.kind
         + (this.click != null ? " click=" + this.click : "")
         + (this.yaw != null ? " yaw=" + this.yaw : "")
         + (this.rotation != null ? " rotation=" + this.rotation : "")
         + (!this.faces.isEmpty() ? " faces=" + this.faces : "")
         + (this.sneak != null ? " sneak=" + this.sneak : "")
         + (this.sneakPoseFlag ? " poseSneak=true" : "")
         + (this.poseSneakActive ? " temp=" + this.poseSneakPositions : "")
         + (this.interact > 0 ? " interact=" + this.interact : "")
         + (this.needsSupportType != SneakBlock.Failure.NONE ? " FAIL=" + this.needsSupportType : "")
         + "}";
   }

   public SneakBlock getYaw(Float yaw) {
      this.yaw = yaw;
      return this;
   }

   public SneakBlock getPoseSneak(BlockPos pos) {
      this.poseSneakActive = true;
      this.poseSneakPositions.add(pos);
      return this;
   }

   public SneakBlock getFlag(boolean flag) {
      this.sneakBlockActive = flag;
      return this;
   }

   public boolean isNeedsSupport() {
      return this.needsSupportType == SneakBlock.Failure.NONE;
   }

   public static enum Failure {
      NONE,
      NO_ITEM,
      NO_SUPPORT,
      NO_STAND,
      NEEDS_TEMP_SUPPORT,
      NEEDS_ACTIVATION,
      STATE_UNREACHABLE,
      INTERACTION_FACE_BLOCKED,
      UNSUPPORTED;
   }

   public static enum Kind {
      PLAIN,
      CLICK_FACE,
      YAW_FACING,
      POST_INTERACT,
      MULTIFACE,
      SIGN_ROTATION,
      PAIRED,
      SOURCE_FLUID;
   }
}
