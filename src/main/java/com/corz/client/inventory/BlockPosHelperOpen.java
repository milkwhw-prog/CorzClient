package com.corz.client.inventory;

import com.corz.client.mixins.AccessorClientPlayerInteractionManager;
import com.corz.client.world.MathClampUtils;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import com.corz.client.utils.ToolBreakerUtils;
import com.corz.client.utils.WrapDegreesUtils;
import com.corz.client.utils.BlockOffsetUtils;
import com.corz.client.categories.movement.Freecam;
import com.corz.client.utils.DeltaAngle;
import com.corz.client.utils.ElytraAutoUtils;

public final class BlockPosHelperOpen {
   private Set<Long> longPositionSet;
   private final List<BlockPosHelperOpen.Watch> blockBrokeWatchList;
   private int closeRangeTicks;
   private static final Set<Item> ALLOWED_BLOCK_ITEMS = Set.of(
      Items.DIRT,
      Items.COARSE_DIRT,
      Items.ROOTED_DIRT,
      Items.GRASS_BLOCK,
      Items.PODZOL,
      Items.COBBLESTONE,
      Items.COBBLED_DEEPSLATE,
      Items.MOSSY_COBBLESTONE,
      Items.BLACKSTONE,
      Items.STONE,
      Items.DEEPSLATE,
      Items.NETHERRACK,
      Items.GRANITE,
      Items.DIORITE,
      Items.ANDESITE,
      Items.TUFF,
      Items.END_STONE,
      Items.STONE_BRICKS,
      Items.NETHER_BRICKS,
      Items.SANDSTONE,
      Items.DRIPSTONE_BLOCK
   );
   private int mutableUnpackValue;
   private final MinecraftClient minecraftClient;
   private boolean closerRemainFlag;
   private final Mutable mutable;
   private final LongOpenHashSet longOpenHashSet;
   private int minZ;
   private int maxY;
   private int apiValue;
   private int minecraftClientValue;
   private BlockPos blockPos;
   private final BlockPosHelperOpen.Host minecraftClientHost;
   private BlockPos blockPosMinecraftClient;
   private BlockPos up;
   private float wrapDegrees;
   private double y;
   private BlockState blockState;
   private int posMinecraftValue;
   private float yaw;
   private int posBlockValue;
   private double distanceTo;
   private int blockPosValue;
   private Direction sideMinecraftClient;
   private final Random random = new Random();
   private int mutableValue;
   private float targetYaw;
   private int mutableClampValue;
   private float hypot;
   private boolean posMinecraftActive;
   private int blockPosCount;
   private boolean minecraftClientActive;
   private int minY;
   private int distanceMinecraftCount;
   private BlockPos posBlockBlockPos;
   private final Long2IntOpenHashMap long2IntOpenHashMap = new Long2IntOpenHashMap();
   private boolean posBlockActive;
   private int x;
   private BlockPos down;
   private final Long2IntOpenHashMap posExpiryTicks = new Long2IntOpenHashMap();
   private BlockPos longBlockPos;
   private int scanY;
   private int lastClientHandleTick;
   private int posBlockCount;
   private boolean blockPosActive;
   private int posMinecraftCount;
   private boolean remainCloserActive;
   private final Mutable mutableMutable;
   private boolean airborneDistFlag;
   private float pitch;
   private int maxX;
   private boolean hashActive;
   private int z;
   private final Long2IntOpenHashMap brokenBlockExpiryTicks = new Long2IntOpenHashMap();
   private float clamp;
   private int longY = Integer.MIN_VALUE;
   private static final Direction[] replaceableMutableDirections = Direction.values();
   private static final int UNUSED_ONE = 1;
   private LongOpenHashSet mutableLongOpenHashSet;
   private int hashValue;
   private int lastClientCheckTick;
   private int minecraftClientCount;
   private int nextHashOpenTick;

   private boolean isMinecraftClient() {
      int foodLevel = this.minecraftClient.player.getHungerManager().getFoodLevel();
      byte minecraftClientValue = 15;
      if (!this.blockPosActive && foodLevel > 10) {
         return false;
      } else if (this.blockPosActive && foodLevel >= minecraftClientValue) {
         this.blockPosActive = false;
         return false;
      } else {
         int componentsFood = this.getComponentsFood();
         if (componentsFood < 0) {
            this.blockPosActive = false;
            return false;
         } else {
            this.blockPosActive = true;
            this.updateCancelBreaking();
            if (this.minecraftClient.player.getInventory().getSelectedSlot() != componentsFood) {
               this.minecraftClient.player.getInventory().setSelectedSlot(componentsFood);
            }

            this.getEyeMinecraft(this.minecraftClient.player.getEyePos().add(0.0, 4.0, 0.0));
            this.minecraftClient.options.useKey.setPressed(true);
            return true;
         }
      }
   }

   private BlockPos getLong() {
      BlockPos mutableMin = this.getMutableMin(20);
      if (mutableMin != null) {
         return mutableMin;
      } else {
         BlockPos mutableMin2 = this.getMutableMin();
         if (mutableMin2 != null) {
            return mutableMin2;
         } else {
            BlockPos blockPos = this.minecraftClient.player.getBlockPos();
            BlockPos fromLong2 = null;
            double longValue = Double.MAX_VALUE;

            for (long longValue2 : this.longPositionSet) {
               int unpackLongX = BlockPos.unpackLongX(longValue2);
               int unpackLongY = BlockPos.unpackLongY(longValue2);
               int unpackLongZ = BlockPos.unpackLongZ(longValue2);
               double longFactor = unpackLongX - blockPos.getX();
               double longFactor2 = unpackLongY - blockPos.getY();
               double longFactor3 = unpackLongZ - blockPos.getZ();
               double longLimit = longFactor * longFactor + longFactor2 * longFactor2 + longFactor3 * longFactor3;
               if (!(longLimit >= longValue)) {
                  this.mutableMutable.set(unpackLongX, unpackLongY, unpackLongZ);
                  if (!this.canHashOpen(this.mutableMutable)
                     && this.canHardnessMinecraft(this.mutableMutable)
                     && this.canReplaceableMutable(this.mutableMutable)
                     && !this.canHashOpen(unpackLongX, unpackLongY, unpackLongZ)) {
                     fromLong2 = BlockPos.fromLong(longValue2);
                     longValue = longLimit;
                  }
               }
            }

            return fromLong2;
         }
      }
   }

   private void handlePosMinecraft(BlockPos blockPos) {
      BlockPos blockPos2 = this.minecraftClient.player.getBlockPos();
      this.posMinecraftActive = true;
      this.blockPosMinecraftClient = blockPos;
      this.x = blockPos2.getX();
      this.z = blockPos2.getZ();
      this.posMinecraftValue = blockPos2.getY();
      this.scanY = blockPos2.getY() - 1;
      this.up = null;
      this.posMinecraftCount = 0;
      this.minecraftClientCount = 0;
      this.y = this.minecraftClient.player.getY();
      this.blockPos = null;
      this.updateCancelBreaking();
   }

   public boolean isFlag() {
      return this.longPositionSet != null && !this.longPositionSet.isEmpty();
   }

   private void updateMinecraftOptions() {
      this.minecraftClient.options.forwardKey.setPressed(false);
      ElytraAutoUtils.handleRawPressed(this.minecraftClient.options.sprintKey, false);
      ElytraAutoUtils.handleRawPressed(this.minecraftClient.options.sneakKey, false);
      this.minecraftClient.options.jumpKey.setPressed(false);
      this.minecraftClient.options.useKey.setPressed(false);
   }

   private boolean canHashOpen(BlockPos pos2) {
      long long2 = pos2.asLong();
      if (this.long2IntOpenHashMap.get(long2) > this.blockPosCount) {
         return true;
      } else {
         return this.posExpiryTicks.get(long2) > this.blockPosCount ? true : this.canHashOpenForBlockPos(pos2);
      }
   }

   public void updateBlockPos() {
      if (this.minecraftClient.player != null && this.minecraftClient.world != null && this.minecraftClient.interactionManager != null) {
         this.updateMinecraftOptions();
         this.hashActive = false;
         this.airborneDistFlag = false;
         if (this.minecraftClientHost.isFlag() && this.minecraftClient.currentScreen != null) {
            this.updateCancelBreaking();
         } else if (this.isFlag() && !this.closerRemainFlag) {
            this.blockPosCount++;
            ((AccessorClientPlayerInteractionManager)this.minecraftClient.interactionManager).setBlockBreakingCooldown(0);
            this.updateMutableUnpack();
            if (!this.minecraftClientHost.isFlag2() || !this.isMinecraftClient()) {
               this.blockPosActive = false;
               if ((this.blockPosCount & 63) == 0) {
                  this.updateHashOpen();
               }

               this.updateBlockBroke();
               if (this.posMinecraftActive) {
                  this.updateMinecraftClient();
               } else {
                  if (this.blockPos != null
                     && (!this.canFlag(this.blockPos) || !this.canHardnessMinecraft(this.blockPos) || !this.canReplaceableMutable(this.blockPos))) {
                     if (this.minecraftClient.world.getBlockState(this.blockPos).isAir()) {
                        this.posExpiryTicks.put(this.blockPos.asLong(), this.blockPosCount + 15);
                        if (this.minecraftClientActive && this.blockPos.equals(this.down)) {
                           this.handleBlockState(this.blockPos);
                        }
                     }

                     this.blockPos = null;
                     this.updateCancelBreaking();
                  }

                  if (this.blockPos == null) {
                     BlockPos blockPos = null;
                     BlockPos mutableMin = this.getMutableMin();
                     if (mutableMin != null && this.canSquaredEye(mutableMin)) {
                        blockPos = mutableMin;
                     }

                     if (blockPos == null) {
                        blockPos = this.getMutableClamp();
                     }

                     if (blockPos != null) {
                        this.handlePosBlock(blockPos);
                     }
                  }

                  boolean blockPosFlag = this.minecraftClientActive && this.blockPos != null && this.blockPos.equals(this.down);
                  boolean blockPosFlag2 = this.minecraftClient.player.isOnGround() || blockPosFlag;
                  if (this.blockPos != null && this.canSquaredEye(this.blockPos) && blockPosFlag2) {
                     this.longBlockPos = null;
                     this.distanceMinecraftCount = 0;
                     this.handleSelectedSlot(this.blockPos);
                     float[] eyeMinecraft = this.getEyeMinecraft(Vec3d.ofCenter(this.blockPos));
                     if (blockPosFlag) {
                        this.handleMinecraftClient(this.blockPos, this.sideMinecraftClient);
                        this.closeRangeTicks = 0;
                        return;
                     }

                     BlockHitResult minecraftClient = this.getMinecraftClient(this.minecraftClient.player.getEyePos(), eyeMinecraft[0], eyeMinecraft[1], this.getMinMinecraftClientHost());
                     if (minecraftClient.getType() == Type.BLOCK && minecraftClient.getBlockPos().equals(this.blockPos)) {
                        this.closeRangeTicks = 0;
                        boolean blockPosFlag3 = canAxisSide(minecraftClient, this.blockPos, 0.22F) || this.posBlockCount >= 6;
                        if (++this.posBlockCount >= 1 && this.blockPosCount >= this.nextHashOpenTick && blockPosFlag3) {
                           this.handleMinecraftClient(this.blockPos, minecraftClient.getSide());
                        }

                        return;
                     }

                     if (minecraftClient.getType() == Type.BLOCK && this.hypot < 6.0F) {
                        BlockPos blockPos2 = minecraftClient.getBlockPos();
                        if (!blockPos2.equals(this.blockPos)
                           && this.canFlag(blockPos2)
                           && this.canHardnessMinecraft(blockPos2)
                           && this.canReplaceableMutable(blockPos2)
                           && !this.canHashOpenForBlockPos(blockPos2)
                           && !this.canMinecraftClient4(blockPos2)) {
                           this.handlePosBlock(blockPos2);
                           return;
                        }
                     }

                     this.posBlockCount = 0;
                     if (!(this.hypot < 6.0F) || ++this.closeRangeTicks <= 3) {
                        return;
                     }

                     this.handlePutPos(this.blockPos);
                     this.blockPos = null;
                  }

                  BlockPos down = this.minecraftClient.player.getBlockPos().down();
                  BlockPos down2 = this.getDown();
                  if (!this.minecraftClientActive || this.down == null || !this.down.equals(down) && !this.down.equals(down2)) {
                     this.updateCancelBreaking();
                  }

                  boolean blockPosFlag4 = this.longBlockPos == null
                     || !this.canFlag(this.longBlockPos)
                     || !this.canHardnessMinecraft(this.longBlockPos)
                     || !this.canReplaceableMutable(this.longBlockPos)
                     || this.canHashOpen(this.longBlockPos);
                  if (blockPosFlag4) {
                     BlockPos pos = this.longBlockPos;
                     this.longBlockPos = this.getLong();
                     if (!Objects.equals(pos, this.longBlockPos)) {
                        this.distanceMinecraftCount = 0;
                        this.distanceTo = Double.MAX_VALUE;
                     }
                  }

                  if (this.longBlockPos == null) {
                     this.updateRemainCloser();
                  } else {
                     if (this.blockPosCount - this.lastClientHandleTick > 40) {
                        double sqrt = Math.sqrt(
                           this.minecraftClient.player.squaredDistanceTo(this.longBlockPos.getX() + 0.5, this.longBlockPos.getY() + 0.5, this.longBlockPos.getZ() + 0.5)
                        );
                        String blockPosText = this.isMultiplierMining() ? "" : " \u00a7c(no pickaxe in your hotbar!)";
                        this.handleApi(
                           "walking to "
                              + formatText(this.longBlockPos)
                              + " \u00a77dist="
                              + String.format("%.1f", sqrt)
                              + (this.minecraftClient.player.isOnGround() ? "" : " \u00a77(airborne)")
                              + blockPosText
                        );
                     }

                     this.airborneDistFlag = true;
                     this.handleMinecraftClientForBlockPos(this.longBlockPos);
                  }
               }
            }
         }
      }
   }

   private BlockPos getMutableMin(int mutableValue) {
      BlockPos blockPos = this.minecraftClient.player.getBlockPos();
      int max2 = Math.max(this.mutableClampValue, blockPos.getX() - mutableValue);
      int mutableLimit = Math.min(this.maxX, blockPos.getX() + mutableValue);
      int max3 = Math.max(this.minY, blockPos.getY() - mutableValue);
      int min4 = Math.min(this.maxY, blockPos.getY() + mutableValue);
      int max5 = Math.max(this.minZ, blockPos.getZ() - mutableValue);
      int min2 = Math.min(this.mutableValue, blockPos.getZ() + mutableValue);
      BlockPos mutableMin = null;
      double maxValue = Double.MAX_VALUE;

      for (int i = min4; i >= max3; i--) {
         for (int j = max2; j <= mutableLimit; j++) {
            for (int k = max5; k <= min2; k++) {
               if (this.mutableLongOpenHashSet.contains(BlockPos.asLong(j, i, k))) {
                  double mutableFactor = j - blockPos.getX();
                  double mutableFactor2 = i - blockPos.getY();
                  double mutableFactor3 = k - blockPos.getZ();
                  double mutableLimit3 = mutableFactor * mutableFactor + mutableFactor2 * mutableFactor2 + mutableFactor3 * mutableFactor3;
                  if (!(mutableLimit3 >= maxValue)) {
                     this.mutableMutable.set(j, i, k);
                     if (!this.canHashOpen(this.mutableMutable)
                        && this.canHardnessMinecraft(this.mutableMutable)
                        && this.canReplaceableMutable(this.mutableMutable)
                        && !this.canHashOpen(j, i, k)) {
                        mutableMin = new BlockPos(j, i, k);
                        maxValue = mutableLimit3;
                     }
                  }
               }
            }
         }
      }

      return mutableMin;
   }

   public void updateClearHash() {
      this.blockPos = null;
      this.minecraftClientActive = false;
      this.down = null;
      this.sideMinecraftClient = null;
      this.longBlockPos = null;
      this.airborneDistFlag = false;
      this.blockPosActive = false;
      this.distanceMinecraftCount = 0;
      this.remainCloserActive = false;
      this.posMinecraftActive = false;
      this.up = null;
      this.blockPosMinecraftClient = null;
      this.longOpenHashSet.clear();
      this.hashValue = 0;
      this.distanceTo = Double.MAX_VALUE;
      this.hashActive = false;
      this.nextHashOpenTick = 0;
      this.closeRangeTicks = 0;
      this.posBlockCount = 0;
      this.blockState = null;
      this.blockBrokeWatchList.clear();
      this.long2IntOpenHashMap.clear();
      this.posExpiryTicks.clear();
      this.brokenBlockExpiryTicks.clear();
      this.longY = Integer.MIN_VALUE;
      this.mutableUnpackValue = -1000;
   }

   private int getInventoryStack() {
      PlayerInventory inventory = this.minecraftClient.player.getInventory();

      for (int i = 0; i < 9; i++) {
         ItemStack stack = inventory.getStack(i);
         if (!stack.isEmpty() && stack.getItem() instanceof BlockItem && ALLOWED_BLOCK_ITEMS.contains(stack.getItem())) {
            return i;
         }
      }

      return -1;
   }

   public int getMinY() {
      return this.minY;
   }

   public int getMax() {
      return this.maxY;
   }

   private void updateRemainCloser() {
      this.updateMinecraftOptions();
      if (!this.remainCloserActive) {
         if (this.longY != Integer.MIN_VALUE) {
            this.handleApi("blocks remain but none are reachable from here \u2014 no path (try moving closer)");
         } else {
            this.remainCloserActive = true;
            this.closerRemainFlag = true;
            this.minecraftClientHost.handle(0);
         }
      }
   }

   private boolean canSquaredEye(BlockPos pos2) {
      Vec3d eyePos = this.minecraftClient.player.getEyePos();
      return eyePos.squaredDistanceTo(getClamp(eyePos, pos2)) <= this.getMinMinecraftClientHost() * this.getMinMinecraftClientHost();
   }

   public int getMutableClampValue() {
      return this.mutableClampValue;
   }

   private void handleApi(String apiText) {
      if (this.blockPosCount - this.apiValue >= 60) {
         this.apiValue = this.blockPosCount;
         this.minecraftClientHost.handleForString(apiText);
      }
   }

   private static String formatText(BlockPos pos) {
      return "(" + pos.getX() + "," + pos.getY() + "," + pos.getZ() + ")";
   }

   private void handleDistanceMinecraft(BlockPos pos) {
      double distanceTo = this.minecraftClient.player.squaredDistanceTo(pos.getX() + 0.5, this.minecraftClient.player.getY(), pos.getZ() + 0.5);
      if (distanceTo < this.distanceTo - 0.01) {
         this.distanceTo = distanceTo;
         this.distanceMinecraftCount = 0;
      } else if (++this.distanceMinecraftCount > 40) {
         this.handleMinecraftClient(pos);
      }
   }

   public BlockPos getDownPos() {
      return this.down != null ? this.down : this.blockPos;
   }

   private boolean canMinecraftClient(BlockPos pos) {
      Direction horizontalDegrees = Direction.fromHorizontalDegrees(MathClampUtils.getWrapDegrees(this.minecraftClient.player.getEyePos(), Vec3d.ofCenter(pos))[0]);
      return this.canEmptyMinecraft(this.minecraftClient.player.getBlockPos().offset(horizontalDegrees).up());
   }

   private boolean canHashOpenForBlockPos(BlockPos pos2) {
      return this.brokenBlockExpiryTicks.get(pos2.asLong()) > this.blockPosCount;
   }

   private float[] getEyeMinecraft(Vec3d vec) {
      float[] wrapDegrees = MathClampUtils.getWrapDegrees(this.minecraftClient.player.getEyePos(), vec);
      return this.getWrapPitch(wrapDegrees[0], wrapDegrees[1]);
   }

   private BlockPos getMutableClamp() {
      BlockPos blockPos = this.minecraftClient.player.getBlockPos();
      double mutableClampFactor = this.getMinMinecraftClientHost();
      int mutableClampLimit = (int)Math.ceil(mutableClampFactor) + 1;
      Vec3d eyePos = this.minecraftClient.player.getEyePos();
      BlockPos blockPos2 = null;
      BlockPos blockPos3 = null;
      double maxValue = Double.MAX_VALUE;
      double maxValue3 = Double.MAX_VALUE;

      for (int i = mutableClampLimit; i >= -mutableClampLimit; i--) {
         for (int j = -mutableClampLimit; j <= mutableClampLimit; j++) {
            for (int k = -mutableClampLimit; k <= mutableClampLimit; k++) {
               int x = blockPos.getX() + j;
               int y = blockPos.getY() + i;
               int z = blockPos.getZ() + k;
               if (x >= this.mutableClampValue
                  && x <= this.maxX
                  && y >= this.minY
                  && y <= this.maxY
                  && z >= this.minZ
                  && z <= this.mutableValue
                  && this.mutableLongOpenHashSet.contains(BlockPos.asLong(x, y, z))) {
                  double mutableClampFactor2 = x + 0.5 - this.minecraftClient.player.getX();
                  double mutableClampFactor3 = z + 0.5 - this.minecraftClient.player.getZ();
                  if (!(mutableClampFactor2 * mutableClampFactor2 + mutableClampFactor3 * mutableClampFactor3 > 6.760000000000001)) {
                     double mutableClampFactor4 = MathHelper.clamp(eyePos.x, (double)x, x + 1.0) - eyePos.x;
                     double mutableClampFactor5 = MathHelper.clamp(eyePos.y, (double)y, y + 1.0) - eyePos.y;
                     double mutableClampFactor6 = MathHelper.clamp(eyePos.z, (double)z, z + 1.0) - eyePos.z;
                     double mutableClampLimit4 = mutableClampFactor4 * mutableClampFactor4 + mutableClampFactor5 * mutableClampFactor5 + mutableClampFactor6 * mutableClampFactor6;
                     if (!(mutableClampLimit4 > mutableClampFactor * mutableClampFactor)
                        && !(mutableClampLimit4 >= (y >= blockPos.getY() ? maxValue : maxValue3))
                        && (j != 0 || k != 0 || i != 0 && (i != -1 || this.canBlockDown(new BlockPos(x, y - 1, z))))) {
                        this.mutableMutable.set(x, y, z);
                        if (!this.canHashOpen(this.mutableMutable)
                           && this.canHardnessMinecraft(this.mutableMutable)
                           && this.canReplaceableMutable(this.mutableMutable)
                           && !this.canHashOpen(x, y, z)) {
                           if (y >= blockPos.getY()) {
                              if (mutableClampLimit4 < maxValue) {
                                 blockPos2 = new BlockPos(x, y, z);
                                 maxValue = mutableClampLimit4;
                              }
                           } else if (mutableClampLimit4 < maxValue3) {
                              blockPos3 = new BlockPos(x, y, z);
                              maxValue3 = mutableClampLimit4;
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return blockPos2 != null ? blockPos2 : blockPos3;
   }

   private void handleSelectedSlot(BlockPos pos) {
      PlayerInventory inventory = this.minecraftClient.player.getInventory();
      BlockState blockState = this.minecraftClient.world.getBlockState(pos);
      float[] floats = new float[PlayerInventory.getHotbarSize()];
      boolean[] booleans = new boolean[floats.length];

      for (int i = 0; i < floats.length; i++) {
         ItemStack stack = inventory.getStack(i);
         floats[i] = stack.getMiningSpeedMultiplier(blockState);
         booleans[i] = stack.isSuitableFor(blockState);
      }

      int selectedSlot = ToolBreakerUtils.selectBestToolIndex(floats, booleans, inventory.getSelectedSlot());
      if (selectedSlot != inventory.getSelectedSlot()) {
         inventory.setSelectedSlot(selectedSlot);
      }
   }

   private void updateMinecraftClient() {
      BlockPos blockPos = this.minecraftClient.player.getBlockPos();
      boolean onGround = this.minecraftClient.player.isOnGround();
      if (onGround && this.up == null) {
         if (blockPos.getX() != this.x || blockPos.getZ() != this.z) {
            this.x = blockPos.getX();
            this.z = blockPos.getZ();
            this.posMinecraftValue = blockPos.getY();
            this.scanY = blockPos.getY() - 1;
         }

         if (this.blockPosMinecraftClient == null || !this.canFlag(this.blockPosMinecraftClient) || !this.canHardnessMinecraft(this.blockPosMinecraftClient)) {
            this.updatePosBlock();
            return;
         }

         boolean minecraftClientFlag = blockPos.getY() - this.posMinecraftValue >= 24;
         BlockPos blockPos2 = this.canSquaredEye(this.blockPosMinecraftClient) && this.canMinecraftClientForBlockPos(this.blockPosMinecraftClient) ? this.blockPosMinecraftClient : this.getMutableClamp();
         if (blockPos2 != null) {
            this.updatePosBlock();
            this.handlePosBlock(blockPos2);
            return;
         }

         if (minecraftClientFlag) {
            this.handlePutPos(this.blockPosMinecraftClient);
            this.updatePosBlock();
            return;
         }
      }

      this.minecraftClient.options.forwardKey.setPressed(false);
      ElytraAutoUtils.handleRawPressed(this.minecraftClient.options.sprintKey, false);
      ElytraAutoUtils.handleRawPressed(this.minecraftClient.options.sneakKey, false);
      this.minecraftClient.player.setSprinting(false);
      BlockPos pos2 = new BlockPos(this.x, this.scanY, this.z);
      BlockPos up = pos2.up();
      Vec3d vec3d = new Vec3d(pos2.getX() + 0.5, pos2.getY() + 1.0, pos2.getZ() + 0.5);
      if (this.up != null) {
         BlockState blockState = this.minecraftClient.world.getBlockState(this.up);
         if (!blockState.isAir() && !blockState.isReplaceable()) {
            this.longOpenHashSet.add(this.up.asLong());
            this.scanY = this.up.getY();
            this.up = null;
         } else {
            if (this.blockPosCount - this.blockPosValue <= 12) {
               this.getEyeMinecraft(vec3d);
               return;
            }

            this.up = null;
         }

         pos2 = new BlockPos(this.x, this.scanY, this.z);
         up = pos2.up();
         vec3d = new Vec3d(pos2.getX() + 0.5, pos2.getY() + 1.0, pos2.getZ() + 0.5);
      }

      double y = this.minecraftClient.player.getY();
      if (y > this.y + 0.05) {
         this.y = y;
         this.minecraftClientCount = 0;
      } else {
         this.minecraftClientCount++;
      }

      if (++this.posMinecraftCount > 400 || this.minecraftClientCount > 60) {
         this.handlePutPos(this.blockPosMinecraftClient);
         this.updatePosBlock();
      } else if (this.canEmptyMinecraft(blockPos.up(2))) {
         this.handlePutPos(this.blockPosMinecraftClient);
         this.updatePosBlock();
      } else {
         int inventoryStack = this.getInventoryStack();
         if (inventoryStack < 0) {
            this.updateMinecraftOptions();
            this.minecraftClientHost.handle("scaffold blocks");
            this.updatePosBlock();
         } else {
            if (this.minecraftClient.player.getInventory().getSelectedSlot() != inventoryStack) {
               this.minecraftClient.player.getInventory().setSelectedSlot(inventoryStack);
            }

            float[] eyeMinecraft = this.getEyeMinecraft(vec3d);
            if (onGround) {
               this.minecraftClient.options.jumpKey.setPressed(true);
            }

            BlockState blockState2 = this.minecraftClient.world.getBlockState(up);
            boolean minecraftClientFlag2 = this.minecraftClient.player.getY() >= up.getY() + 0.9;
            boolean minecraftClientFlag3 = blockState2.isAir() || blockState2.isReplaceable();
            if (minecraftClientFlag2 && minecraftClientFlag3 && this.canEmptyMinecraft(pos2)) {
               BlockHitResult minecraftClient = this.getMinecraftClient(this.minecraftClient.player.getEyePos(), eyeMinecraft[0], eyeMinecraft[1], this.getMinMinecraftClientHost());
               if (minecraftClient.getType() == Type.BLOCK && minecraftClient.getBlockPos().equals(pos2) && minecraftClient.getSide() == Direction.UP) {
                  this.minecraftClient.interactionManager.interactBlock(this.minecraftClient.player, Hand.MAIN_HAND, minecraftClient);
                  this.minecraftClient.player.swingHand(Hand.MAIN_HAND);
                  this.up = up;
                  this.blockPosValue = this.blockPosCount;
               }
            }
         }
      }
   }

   public BlockPosHelperOpen(MinecraftClient minecraftClient, BlockPosHelperOpen.Host api) {
      this.mutableUnpackValue = -1000;
      this.blockBrokeWatchList = new ArrayList<>();
      this.posBlockValue = -1000;
      this.longOpenHashSet = new LongOpenHashSet();
      this.lastClientCheckTick = -100;
      this.distanceTo = Double.MAX_VALUE;
      this.lastClientHandleTick = -1000;
      this.apiValue = -1000;
      this.mutableMutable = new Mutable();
      this.mutable = new Mutable();
      this.minecraftClient = minecraftClient;
      this.minecraftClientHost = api;
      this.long2IntOpenHashMap.defaultReturnValue(Integer.MIN_VALUE);
      this.posExpiryTicks.defaultReturnValue(Integer.MIN_VALUE);
      this.brokenBlockExpiryTicks.defaultReturnValue(Integer.MIN_VALUE);
   }

   private void handleMinecraftClient(BlockPos down, Direction side) {
      if (this.minecraftClient.world.getBlockState(down).isAir()) {
         this.updateCancelBreaking();
      } else {
         if (!down.equals(this.down)) {
            this.updateCancelBreaking();
            this.sideMinecraftClient = side;
            this.minecraftClientValue = this.blockPosCount;
            this.blockState = this.minecraftClient.world.getBlockState(down);
         }

         if (this.minecraftClient.interactionManager.updateBlockBreakingProgress(down, this.sideMinecraftClient)) {
            this.minecraftClient.player.swingHand(Hand.MAIN_HAND);
         }

         this.minecraftClientActive = true;
         this.down = down;
         this.lastClientHandleTick = this.blockPosCount;
      }
   }

   private void handleMinecraftClient(BlockPos pos) {
      if (pos != null && pos.getY() < this.minecraftClient.player.getBlockPos().getY() && !this.canMinecraftClient(pos)) {
         this.updatePosBlockMinecraftClient();
      } else {
         this.handlePutPos(pos);
      }

      this.distanceMinecraftCount = 0;
   }

   public int getMaxX() {
      return this.maxX;
   }

   private boolean isMultiplierMining() {
      PlayerInventory inventory = this.minecraftClient.player.getInventory();
      BlockState defaultState = Blocks.STONE.getDefaultState();

      for (int i = 0; i < 9; i++) {
         ItemStack stack = inventory.getStack(i);
         if (!stack.isEmpty() && stack.getMiningSpeedMultiplier(defaultState) > 1.5F) {
            return true;
         }
      }

      return false;
   }

   private void updatePosBlock() {
      this.posBlockValue = this.blockPosCount;
      this.posBlockActive = this.minecraftClient.player.getBlockPos().getY() > this.posMinecraftValue;
      this.posBlockBlockPos = this.blockPosMinecraftClient;
      this.posMinecraftActive = false;
      this.up = null;
      this.blockPosMinecraftClient = null;
      this.updateMinecraftOptions();
   }

   public int getMin() {
      return this.minZ;
   }

   private void updateCancelBreaking() {
      if (this.minecraftClientActive) {
         this.minecraftClient.interactionManager.cancelBlockBreaking();
         this.minecraftClientActive = false;
      }

      this.down = null;
   }

   private boolean canMinecraftClientForBlockPos(BlockPos pos) {
      BlockHitResult raycast = this.minecraftClient
         .world
         .raycast(
            new RaycastContext(this.minecraftClient.player.getEyePos(), Vec3d.ofCenter(pos), ShapeType.COLLIDER, FluidHandling.NONE, this.minecraftClient.player)
         );
      return raycast.getType() == Type.BLOCK && raycast.getBlockPos().equals(pos);
   }

   private boolean canReplaceableMutable(BlockPos pos2) {
      for (Direction direction : replaceableMutableDirections) {
         BlockState blockState = this.minecraftClient.world.getBlockState(this.mutable.set(pos2, direction));
         if (blockState.isAir() || blockState.isReplaceable()) {
            return true;
         }
      }

      return false;
   }

   private boolean canMinMax(int blockPosLimit, int blockPosLimit2, int blockPosLimit3) {
      return blockPosLimit >= this.mutableClampValue && blockPosLimit <= this.maxX && blockPosLimit2 >= this.minY && blockPosLimit2 <= this.maxY && blockPosLimit3 >= this.minZ && blockPosLimit3 <= this.mutableValue
         ? this.mutableLongOpenHashSet.contains(BlockPos.asLong(blockPosLimit, blockPosLimit2, blockPosLimit3))
         : false;
   }

   private boolean canFlag(BlockPos pos) {
      return this.canMinMax(pos.getX(), pos.getY(), pos.getZ());
   }

   public BlockPos getAirborneDist() {
      return this.airborneDistFlag ? this.longBlockPos : null;
   }

   private double getMinMinecraftClientHost() {
      return Math.min(this.minecraftClientHost.getAmount(), this.minecraftClient.player.getBlockInteractionRange());
   }

   private static Vec3d getClamp(Vec3d vec, BlockPos pos2) {
      return new Vec3d(
         MathHelper.clamp(vec.x, (double)pos2.getX(), pos2.getX() + 1.0),
         MathHelper.clamp(vec.y, (double)pos2.getY(), pos2.getY() + 1.0),
         MathHelper.clamp(vec.z, (double)pos2.getZ(), pos2.getZ() + 1.0)
      );
   }

   private BlockPos getMutableMin() {
      if (this.longY == Integer.MIN_VALUE) {
         return null;
      } else {
         int longY = this.longY;
         BlockPos blockPos = this.minecraftClient.player.getBlockPos();
         boolean mutableFlag = true;

         for (int i = this.mutableClampValue; i <= this.maxX; i++) {
            for (int j = 0; j <= this.mutableValue - this.minZ; j++) {
               int z = mutableFlag ? this.minZ + j : this.mutableValue - j;
               if (this.canMinMax(i, longY, z)) {
                  this.mutableMutable.set(i, longY, z);
                  if (!this.canHashOpen(this.mutableMutable)
                     && this.canHardnessMinecraft(this.mutableMutable)
                     && this.canReplaceableMutable(this.mutableMutable)
                     && (i != blockPos.getX() || z != blockPos.getZ() || longY != blockPos.getY() - 1 || this.canBlockDown(new BlockPos(i, longY - 1, z)))) {
                     return new BlockPos(i, longY, z);
                  }
               }
            }

            mutableFlag = !mutableFlag;
         }

         return null;
      }
   }

   private boolean canMinecraftClientFlag(BlockPos pos2) {
      BlockState blockState = this.minecraftClient.world.getBlockState(pos2);
      if (!blockState.isAir() && !blockState.isReplaceable()) {
         return false;
      } else {
         BlockHitResult offsetOpposite = BlockOffsetUtils.findPlacementHit(pos2);
         if (offsetOpposite == null) {
            return false;
         } else {
            int inventoryStack = this.getInventoryStack();
            if (inventoryStack < 0) {
               return false;
            } else {
               if (this.minecraftClient.player.getInventory().getSelectedSlot() != inventoryStack) {
                  this.minecraftClient.player.getInventory().setSelectedSlot(inventoryStack);
               }

               this.updateCancelBreaking();
               float[] eyeMinecraft = this.getEyeMinecraft(offsetOpposite.getPos());
               BlockHitResult minecraftClient = this.getMinecraftClient(this.minecraftClient.player.getEyePos(), eyeMinecraft[0], eyeMinecraft[1], this.getMinMinecraftClientHost());
               if (minecraftClient.getType() == Type.BLOCK
                  && minecraftClient.getBlockPos().equals(offsetOpposite.getBlockPos())
                  && minecraftClient.getBlockPos().offset(minecraftClient.getSide()).equals(pos2)
                  && this.blockPosCount - this.lastClientCheckTick >= 4) {
                  this.minecraftClient.interactionManager.interactBlock(this.minecraftClient.player, Hand.MAIN_HAND, minecraftClient);
                  this.minecraftClient.player.swingHand(Hand.MAIN_HAND);
                  this.lastClientCheckTick = this.blockPosCount;
                  this.longOpenHashSet.add(pos2.asLong());
               }

               return true;
            }
         }
      }
   }

   public int getMutableValue() {
      return this.mutableValue;
   }

   private void updatePosBlockMinecraftClient() {
      BlockPos blockPos = this.minecraftClient.player.getBlockPos();
      BlockPos down = this.getDown();
      if (down == null) {
         down = blockPos.down();
      }

      if (this.canEmptyMinecraft(down)) {
         if (!this.canHardnessMinecraft(down)) {
            this.handlePutPos(this.longBlockPos);
            return;
         }

         if (!this.canFlag(down) && !this.longOpenHashSet.contains(down.asLong())) {
            this.handlePutPos(this.longBlockPos);
            return;
         }

         if (!this.canBlockDown(down.down())) {
            this.updateCancelBreaking();
            if (!this.minecraftClientHost.isFlag4() || !this.canMinecraftClientFlag(down.down())) {
               this.handlePutPos(this.longBlockPos);
            }

            return;
         }

         this.handleSelectedSlot(down);
         float[] eyeMinecraft = this.getEyeMinecraft(Vec3d.ofCenter(down));
         if (this.minecraftClientActive && down.equals(this.down)) {
            this.handleMinecraftClient(down, this.sideMinecraftClient);
            return;
         }

         BlockHitResult minecraftClient = this.getMinecraftClient(this.minecraftClient.player.getEyePos(), eyeMinecraft[0], eyeMinecraft[1], this.getMinMinecraftClientHost());
         if (minecraftClient.getType() == Type.BLOCK && minecraftClient.getBlockPos().equals(down)) {
            boolean posBlockFlag = canAxisSide(minecraftClient, down, 0.22F) || this.posBlockCount >= 6;
            if (++this.posBlockCount >= 1 && posBlockFlag) {
               this.handleMinecraftClient(down, minecraftClient.getSide());
            }
         } else {
            this.posBlockCount = 0;
         }
      } else {
         this.updateCancelBreaking();
         if (!this.canBlockDown(down) && (!this.minecraftClientHost.isFlag4() || !this.canMinecraftClientFlag(down))) {
            this.handlePutPos(this.longBlockPos);
         }
      }
   }

   private void handleIterator(Long2IntOpenHashMap long2IntOpenHashMap) {
      IntIterator iterator = long2IntOpenHashMap.values().iterator();

      while (iterator.hasNext()) {
         if (iterator.nextInt() <= this.blockPosCount) {
            iterator.remove();
         }
      }
   }

   private void handleMinecraftClientForBlockPos(BlockPos pos) {
      BlockPos blockPos = this.minecraftClient.player.getBlockPos();
      double hypot = Math.hypot(pos.getX() + 0.5 - this.minecraftClient.player.getX(), pos.getZ() + 0.5 - this.minecraftClient.player.getZ());
      int deltaY = pos.getY() - blockPos.getY();
      if (deltaY >= 2 && hypot <= 1.7) {
         if (!this.posBlockActive && this.blockPosCount - this.posBlockValue < 40 && pos.equals(this.posBlockBlockPos)) {
            this.handlePutPos(pos);
         } else {
            if (this.minecraftClientHost.isFlag4()) {
               this.handlePosMinecraft(pos);
            } else {
               this.handlePutPos(pos);
            }
         }
      } else if (deltaY <= -1 && hypot <= 1.5) {
         this.updatePosBlockMinecraftClient();
      } else if (deltaY <= -1 && this.longOpenHashSet.contains(blockPos.down().asLong())) {
         this.updatePosBlockMinecraftClient();
      } else if (deltaY >= 2 && this.longOpenHashSet.contains(blockPos.down().asLong())) {
         this.updatePosBlockMinecraftClient();
      } else {
         Vec3d eyePos = this.minecraftClient.player.getEyePos();
         float minecraftClientValue = MathClampUtils.getWrapDegrees(eyePos, new Vec3d(pos.getX() + 0.5, eyePos.y, pos.getZ() + 0.5))[0];
         Direction horizontalDegrees = Direction.fromHorizontalDegrees(minecraftClientValue);
         BlockPos offset = blockPos.offset(horizontalDegrees);
         BlockPos down = offset.down();
         int hashValue12 = this.hashValue;
         this.hashValue = 0;
         if (this.canEmptyMinecraft(offset) && !this.canEmptyMinecraft(offset.up()) && !this.canEmptyMinecraft(offset.up(2))) {
            float eyeWrap = this.getEyeWrap(pos);
            if (eyeWrap < 72.0F) {
               if (this.minecraftClient.player.isOnGround()) {
                  this.minecraftClient.options.jumpKey.setPressed(true);
               }

               this.minecraftClient.options.forwardKey.setPressed(true);
            }

            this.handleDistanceMinecraft(pos);
         } else if (!this.canEmptyMinecraft(down)) {
            if (pos.getY() <= blockPos.getY() && this.canBlockDown(down)) {
               float eyeWrap2 = this.getEyeWrap(pos);
               if (eyeWrap2 < 72.0F) {
                  this.minecraftClient.options.forwardKey.setPressed(true);
               }

               this.handleDistanceMinecraft(pos);
            } else {
               ElytraAutoUtils.handleRawPressed(this.minecraftClient.options.sneakKey, true);
               if (this.minecraftClientHost.isFlag4() && this.canMinecraftClientFlag(down)) {
                  ElytraAutoUtils.handleRawPressed(this.minecraftClient.options.sneakKey, true);
                  this.minecraftClient.options.forwardKey.setPressed(true);
                  this.hashValue = hashValue12 + 1;
                  if (this.hashValue > 80) {
                     this.hashValue = 0;
                     if (pos.getY() < blockPos.getY()) {
                        this.updatePosBlockMinecraftClient();
                     } else {
                        this.handlePutPos(pos);
                     }
                  }
               } else if (pos.getY() < blockPos.getY()) {
                  this.updatePosBlockMinecraftClient();
               } else {
                  this.handleMinecraftClient(pos);
               }
            }
         } else {
            float eyeWrap3 = this.getEyeWrap(pos);
            if (this.canDownOffset(offset, horizontalDegrees)) {
               ElytraAutoUtils.handleRawPressed(this.minecraftClient.options.sneakKey, true);
            }

            if (eyeWrap3 < 72.0F) {
               this.minecraftClient.options.forwardKey.setPressed(true);
               if (this.minecraftClientHost.isFlag3()
                  && eyeWrap3 < 32.0F
                  && !this.canDownOffset(offset, horizontalDegrees)
                  && this.minecraftClient.player.getHungerManager().getFoodLevel() > 6) {
                  ElytraAutoUtils.handleRawPressed(this.minecraftClient.options.sprintKey, true);
               }
            }

            this.handleDistanceMinecraft(pos);
         }
      }
   }

   private int getComponentsFood() {
      PlayerInventory inventory = this.minecraftClient.player.getInventory();

      for (int i = 0; i < 9; i++) {
         ItemStack stack = inventory.getStack(i);
         if (!stack.isEmpty() && stack.getItem().getComponents().contains(DataComponentTypes.FOOD)) {
            return i;
         }
      }

      return -1;
   }

   private float getEyeWrap(BlockPos pos) {
      this.getEyeMinecraft(new Vec3d(pos.getX() + 0.5, this.minecraftClient.player.getEyePos().y, pos.getZ() + 0.5));
      return this.wrapDegrees;
   }

   private void handlePutPos(BlockPos pos2) {
      if (pos2 != null) {
         this.long2IntOpenHashMap.put(pos2.asLong(), this.blockPosCount + 80);
      }

      if (pos2 != null && pos2.equals(this.longBlockPos)) {
         this.longBlockPos = null;
      }

      this.distanceTo = Double.MAX_VALUE;
   }

   private static boolean canAxisSide(BlockHitResult blockHitResult, BlockPos pos2, double axisSideLimit) {
      Vec3d pos3 = blockHitResult.getPos();
      Axis axis = blockHitResult.getSide().getAxis();
      if (axis != Axis.X) {
         double deltaX = pos3.x - pos2.getX();
         if (deltaX < axisSideLimit || deltaX > 1.0 - axisSideLimit) {
            return false;
         }
      }

      if (axis != Axis.Y) {
         double deltaY = pos3.y - pos2.getY();
         if (deltaY < axisSideLimit || deltaY > 1.0 - axisSideLimit) {
            return false;
         }
      }

      if (axis != Axis.Z) {
         double deltaZ = pos3.z - pos2.getZ();
         if (deltaZ < axisSideLimit || deltaZ > 1.0 - axisSideLimit) {
            return false;
         }
      }

      return true;
   }

   public boolean isMinecraftClientActive() {
      return this.minecraftClientActive;
   }

   private boolean canBlockDown(BlockPos pos) {
      for (int i = 0; i <= 5; i++) {
         BlockPos down = pos.down(i);
         if (this.minecraftClient.world.getBlockState(down).getBlock() == Blocks.LAVA) {
            return false;
         }

         if (this.canEmptyMinecraft(down)) {
            return true;
         }
      }

      return false;
   }

   private boolean canEmptyMinecraft(BlockPos pos2) {
      BlockState blockState = this.minecraftClient.world.getBlockState(pos2);
      return !blockState.isAir() && blockState.getFluidState().isEmpty() ? !blockState.getCollisionShape(this.minecraftClient.world, pos2).isEmpty() : false;
   }

   private boolean canDownOffset(BlockPos pos, Direction direction) {
      BlockPos offset = pos.offset(direction);
      return this.canEmptyMinecraft(offset.down()) ? false : !this.canBlockDown(offset.down());
   }

   private BlockPos getDown() {
      BlockPos blockPos = this.minecraftClient.player.getBlockPos();
      BlockPos down = blockPos.down();
      if (this.canEmptyMinecraft(down)) {
         return down;
      } else if (!this.minecraftClient.player.isOnGround()) {
         return null;
      } else {
         Box boundingBox = this.minecraftClient.player.getBoundingBox();
         int y = blockPos.getY() - 1;

         for (int i = (int)Math.floor(boundingBox.minX); i <= (int)Math.floor(boundingBox.maxX); i++) {
            for (int j = (int)Math.floor(boundingBox.minZ); j <= (int)Math.floor(boundingBox.maxZ); j++) {
               BlockPos pos2 = new BlockPos(i, y, j);
               if (this.canEmptyMinecraft(pos2)) {
                  return pos2;
               }
            }
         }

         return null;
      }
   }

   public void handleMinMax(Collection<BlockPos> collection) {
      this.closerRemainFlag = false;
      this.remainCloserActive = false;
      this.longY = Integer.MIN_VALUE;
      this.mutableUnpackValue = -1000;
      HashSet set = new HashSet(Math.max(16, collection.size() * 4 / 3));
      LongOpenHashSet longOpenHashSet = new LongOpenHashSet(collection.size());
      boolean blockPosFlag = true;

      for (BlockPos blockPos : collection) {
         set.add(blockPos.asLong());
         longOpenHashSet.add(blockPos.asLong());
         if (blockPosFlag) {
            this.mutableClampValue = this.maxX = blockPos.getX();
            this.minY = this.maxY = blockPos.getY();
            this.minZ = this.mutableValue = blockPos.getZ();
            blockPosFlag = false;
         } else {
            this.mutableClampValue = Math.min(this.mutableClampValue, blockPos.getX());
            this.maxX = Math.max(this.maxX, blockPos.getX());
            this.minY = Math.min(this.minY, blockPos.getY());
            this.maxY = Math.max(this.maxY, blockPos.getY());
            this.minZ = Math.min(this.minZ, blockPos.getZ());
            this.mutableValue = Math.max(this.mutableValue, blockPos.getZ());
         }
      }

      this.longPositionSet = set;
      this.mutableLongOpenHashSet = longOpenHashSet;
      if (set.isEmpty()) {
         this.closerRemainFlag = true;
      }
   }

   public void updateMinecraftClient2() {
      this.updateMinecraftOptions();
      if (this.minecraftClient.interactionManager != null) {
         this.minecraftClient.interactionManager.cancelBlockBreaking();
      }

      this.blockPos = null;
      this.minecraftClientActive = false;
      this.down = null;
      this.blockPosActive = false;
      this.airborneDistFlag = false;
      this.hashActive = false;
   }

   private float[] getWrapPitch(float wrapPitchValue, float wrapPitchValue2) {
      this.yaw = this.minecraftClient.player.getYaw();
      this.pitch = this.minecraftClient.player.getPitch();
      this.hashActive = true;
      float wrapPitchValue3 = 60.0F * (0.85F + this.random.nextFloat() * 0.3F);
      float clamp = MathHelper.clamp(wrapPitchValue3 / 150.0F, 0.08F, 0.9F);
      float wobbleOffset = WrapDegreesUtils.getWobbleOffset(this.blockPosCount, 0.2F, 0);
      float wobbleOffset5 = WrapDegreesUtils.getWobbleOffset(this.blockPosCount, 0.12F, 1);
      float amount = WrapDegreesUtils.stepAngleProportional(this.yaw, wrapPitchValue + wobbleOffset, clamp, wrapPitchValue3, 0.8F);
      float clamp2 = MathHelper.clamp(WrapDegreesUtils.stepAngleProportional(this.pitch, wrapPitchValue2 + wobbleOffset5, clamp, wrapPitchValue3, 0.8F), -90.0F, 90.0F);
      this.minecraftClient.player.setYaw(amount);
      this.minecraftClient.player.setPitch(clamp2);
      this.targetYaw = amount;
      this.clamp = clamp2;
      this.wrapDegrees = Math.abs(MathHelper.wrapDegrees(wrapPitchValue - amount));
      this.hypot = (float)Math.hypot(MathHelper.wrapDegrees(wrapPitchValue - amount), wrapPitchValue2 - clamp2);
      return new float[]{amount, clamp2};
   }

   public boolean isCloserRemainFlag() {
      return this.closerRemainFlag;
   }

   private boolean canHashOpen(int hashOpenValue, int unpackLongY, int unpackLongZ) {
      if (!this.longOpenHashSet.isEmpty() && this.longOpenHashSet.contains(BlockPos.asLong(hashOpenValue, unpackLongY, unpackLongZ))) {
         BlockPos blockPos = this.minecraftClient.player.getBlockPos();
         return hashOpenValue == blockPos.getX() && unpackLongZ == blockPos.getZ() && unpackLongY < blockPos.getY();
      } else {
         return false;
      }
   }

   private boolean canHardnessMinecraft(BlockPos pos2) {
      BlockState blockState = this.minecraftClient.world.getBlockState(pos2);
      if (blockState.isAir()) {
         return false;
      } else {
         Block block = blockState.getBlock();
         return block != Blocks.WATER && block != Blocks.LAVA ? blockState.getHardness(this.minecraftClient.world, pos2) >= 0.0F : false;
      }
   }

   private void handlePosBlock(BlockPos blockPos) {
      if (!blockPos.equals(this.blockPos)) {
         this.updateCancelBreaking();
         this.blockPos = blockPos;
         this.closeRangeTicks = 0;
         this.posBlockCount = 0;
         this.nextHashOpenTick = this.blockPosCount + this.random.nextInt(2);
      }
   }

   private BlockHitResult getMinecraftClient(Vec3d vec, float degrees, float degrees2, double minecraftClientValue) {
      double angle = Math.toRadians(degrees);
      double radians = Math.toRadians(degrees2);
      Vec3d vec3d = new Vec3d(-Math.sin(angle) * Math.cos(radians), -Math.sin(radians), Math.cos(angle) * Math.cos(radians));
      return this.minecraftClient
         .world
         .raycast(new RaycastContext(vec, vec.add(vec3d.multiply(minecraftClientValue)), ShapeType.COLLIDER, FluidHandling.NONE, this.minecraftClient.player));
   }

   private void updateHashOpen() {
      this.handleIterator(this.long2IntOpenHashMap);
      this.handleIterator(this.posExpiryTicks);
      this.handleIterator(this.brokenBlockExpiryTicks);
   }

   private void handleBlockState(BlockPos pos2) {
      if (this.blockState != null && !(this.blockState.getBlock() instanceof FallingBlock)) {
         this.blockBrokeWatchList.add(new BlockPosHelperOpen.Watch(pos2, this.blockState, this.blockPosCount));
      }
   }

   public boolean canLerpAngle(DeltaAngle deltaAngle) {
      if (!Freecam.isInstanceFlag() && this.hashActive && this.minecraftClient.player != null) {
         float tickDelta = deltaAngle.getTickDelta();
         deltaAngle.setAngleDegrees(MathHelper.lerpAngleDegrees(tickDelta, this.yaw, this.targetYaw));
         deltaAngle.setLerp(MathHelper.lerp(tickDelta, this.pitch, this.clamp));
         deltaAngle.updateFlag();
         return true;
      } else {
         return false;
      }
   }

   private void updateMutableUnpack() {
      if (this.longY == Integer.MIN_VALUE || this.blockPosCount - this.mutableUnpackValue >= 8) {
         this.mutableUnpackValue = this.blockPosCount;
         int mutableUnpackLimit = this.longY == Integer.MIN_VALUE ? this.maxY : Math.min(this.longY, this.maxY);
         int longY = Integer.MIN_VALUE;
         LongIterator iterator = this.mutableLongOpenHashSet.iterator();

         while (iterator.hasNext()) {
            long randomLong = iterator.nextLong();
            int unpackLongY = BlockPos.unpackLongY(randomLong);
            if (unpackLongY <= mutableUnpackLimit && unpackLongY > longY) {
               this.mutableMutable.set(randomLong);
               if (!this.canHashOpenForBlockPos(this.mutableMutable) && this.canHardnessMinecraft(this.mutableMutable) && this.canReplaceableMutable(this.mutableMutable)) {
                  longY = unpackLongY;
               }
            }
         }

         this.longY = longY;
      }
   }

   private void updateBlockBroke() {
      if (!this.blockBrokeWatchList.isEmpty()) {
         Iterator iterator = this.blockBrokeWatchList.iterator();

         while (iterator.hasNext()) {
            BlockPosHelperOpen.Watch watch = (BlockPosHelperOpen.Watch)iterator.next();
            BlockState blockState = this.minecraftClient.world.getBlockState(watch.pos);
            if (!blockState.isAir() && blockState.getBlock() == watch.original.getBlock()) {
               this.brokenBlockExpiryTicks.put(watch.pos.asLong(), this.blockPosCount + 1200);
               iterator.remove();
            } else if (this.blockPosCount - watch.brokeTick >= 20) {
               iterator.remove();
            }
         }
      }
   }

   private boolean canMinecraftClient4(BlockPos pos) {
      BlockPos blockPos = this.minecraftClient.player.getBlockPos();
      return pos.getX() == blockPos.getX() && pos.getZ() == blockPos.getZ() && (pos.getY() == blockPos.getY() || pos.getY() == blockPos.getY() - 1);
   }

   public interface Host {
      boolean isFlag();

      boolean isFlag2();

      double getAmount();

      void handle(int hostValue);

      boolean isFlag3();

      boolean isFlag4();

      void handle(String hostText);

      void handleForString(String hostText);
   }

   private record Watch(BlockPos pos, BlockState original, int brokeTick) {
   }
}
