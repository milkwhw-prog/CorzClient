package com.corz.client.inventory;

import com.corz.client.entity.EntityCenterUtils;
import com.corz.client.entity.PosBlockUtilsSelf;
import com.corz.client.world.ChargeAmountApi;
import com.corz.client.world.MathClampUtils;
import com.corz.client.world.PosBlockUtilsBonus;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import com.corz.client.categories.combat.AutoInventoryTotem;
import com.corz.client.utils.BlockHitResolver;
import com.corz.client.utils.EntityDamageUtils;

public final class UnsafeCharge {
   private boolean chargeGoneFlag;
   private long placeTimeoutStartMs;
   private BlockPos immutable;
   private String unsafeChargeText;
   private int chargeGlowstoneValue;
   private final BlockPosHelperBlocked armedCoverBlockPosHelperBloc;
   private PosBlockUtilsSelf.Plan unsafe;
   private long timeoutChargeTime;
   private boolean unsafeCoverFlag;
   private Vec3d pos;
   private int netherCount;
   private boolean chargeUnsafeActive;
   private int placeAnchorValue;
   private int detonateChargeValue;
   private long unsafeTime;
   private boolean unsafeChargeActive;
   private int totemValue;
   private boolean placeTimeoutFlag;
   private int detonateDetonatorValue;
   private UnsafeCharge.Phase coverArmedType = UnsafeCharge.Phase.IDLE;
   private BlockPos blockPos;
   private final ChargeAmountApi timeout;
   private long unsafeChargeTime;
   private Direction sideSidePos;
   private static final String UNSAFE_COVER = "unsafe without cover";

   public void updateUnsafeCharge() {
      this.armedCoverBlockPosHelperBloc.updateTextImmutableText();
      this.coverArmedType = UnsafeCharge.Phase.IDLE;
      this.unsafe = null;
      this.immutable = null;
      this.blockPos = null;
      this.placeAnchorValue = -1;
      this.chargeGlowstoneValue = -1;
      this.detonateDetonatorValue = -1;
      this.unsafeChargeActive = false;
      this.chargeGoneFlag = false;
      this.unsafeCoverFlag = false;
      this.chargeUnsafeActive = false;
      this.placeTimeoutFlag = false;
      this.placeTimeoutStartMs = Long.MIN_VALUE;
      this.totemValue = 0;
      this.pos = null;
      this.sideSidePos = null;
      this.unsafeChargeText = null;
      this.unsafeChargeTime = Long.MIN_VALUE;
      this.unsafeTime = Long.MIN_VALUE;
   }

   public String getText() {
      return this.unsafeChargeText;
   }

   static int getValue(int unsafeChargeValue, int[] unsafeChargeValues, boolean[] unsafeChargeFlags) {
      if (unsafeChargeValues == null) {
         return 0;
      } else {
         int unsafeChargeTotal = 0;

         for (int i = 0; i < unsafeChargeValues.length && i < 9; i++) {
            if (unsafeChargeValues[i] > 0 && (i == unsafeChargeValue || !canFlag(unsafeChargeFlags, i))) {
               unsafeChargeTotal += unsafeChargeValues[i];
            }
         }

         return unsafeChargeTotal;
      }
   }

   private static boolean[] getFlags() {
      boolean[] booleans = new boolean[9];
      int base = AutoInventoryTotem.getReserveSlotIfEnabled();

      for (int i = 0; i < 9; i++) {
         booleans[i] = i == base || AutoInventoryTotem.wasTotemSlot(i) || AutoInventoryTotem.isSlotInPendingSwap(i);
      }

      return booleans;
   }

   private static boolean canRespawnAnchor(BlockState state) {
      return !state.isOf(Blocks.RESPAWN_ANCHOR);
   }

   private static boolean canFlag(boolean[] unsafeChargeFlags, int index) {
      return unsafeChargeFlags != null && index >= 0 && index < unsafeChargeFlags.length && unsafeChargeFlags[index];
   }

   private boolean isTimeoutDetonate() {
      return this.placeTimeoutFlag && this.timeout.getTime() - this.placeTimeoutStartMs <= this.detonateChargeValue;
   }

   public void setUnsafeCoverFlag(boolean unsafeCoverFlag) {
      this.unsafeCoverFlag = unsafeCoverFlag;
   }

   private boolean canDetonatorUndying(ClientPlayerEntity clientPlayerEntity) {
      return canFlag(this.detonateDetonatorValue) && clientPlayerEntity.getInventory().getStack(this.detonateDetonatorValue).isOf(Items.TOTEM_OF_UNDYING)
         || clientPlayerEntity.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING);
   }

   static int getValueForIntIntArray(int index, int[] unsafeChargeValues, boolean[] unsafeChargeFlags) {
      if (unsafeChargeValues == null) {
         return -1;
      } else if (index >= 0 && index < unsafeChargeValues.length && unsafeChargeValues[index] > 0) {
         return index;
      } else {
         for (int i = 0; i < unsafeChargeValues.length && i < 9; i++) {
            if (unsafeChargeValues[i] > 0 && !canFlag(unsafeChargeFlags, i)) {
               return i;
            }
         }

         return -1;
      }
   }

   private BlockPosHelperBlocked.Result getGlowstonePos(ClientPlayerEntity clientPlayerEntity) {
      if (this.blockPos == null || !BlockPosUtilsRespawn.canReplaceableAir(this.blockPos)) {
         this.handleTimeoutDetonate(this.getChargeDetonate(UnsafeCharge.Phase.COVER));
         return BlockPosHelperBlocked.Result.WAITING;
      } else if (BlockPosUtilsRespawn.getChargesRespawn(this.immutable) < 0) {
         return this.getUnsafeArmed("anchor gone");
      } else if (!this.isChargeTimeout() && canInventoryStack(clientPlayerEntity, this.chargeGlowstoneValue, Items.GLOWSTONE)) {
         if (!this.chargeUnsafeActive) {
            if (BlockPosUtilsRespawn.getChargesRespawn(this.immutable) <= 0
                  && getValue(this.chargeGlowstoneValue, getInventoryStack(clientPlayerEntity, Items.GLOWSTONE), getFlags()) < 2
               || !BlockPosUtilsRespawn.canAbsentPlace(this.blockPos, Blocks.GLOWSTONE.getDefaultState())
               || !PosBlockUtilsSelf.canRaycastAmounts(clientPlayerEntity.getEyePos(), this.timeout.getAmountDouble(), this.immutable, this.blockPos, null, null)
               || !this.armedCoverBlockPosHelperBloc
                  .canImmutableFlag(this.blockPos, this.chargeGlowstoneValue, Blocks.GLOWSTONE.getDefaultState(), this.immutable, this.detonateChargeValue)) {
               return this.getCoverWaiting();
            }

            this.chargeUnsafeActive = true;
         }

         BlockPosHelperBlocked.Result blockedHit = this.armedCoverBlockPosHelperBloc.getBlockedHit();
         if (blockedHit == BlockPosHelperBlocked.Result.ACTED) {
            this.updateTimeoutPlace();
         }

         if (blockedHit == BlockPosHelperBlocked.Result.DONE) {
            this.handleTimeoutDetonate(this.getChargeDetonate(UnsafeCharge.Phase.COVER));
            return BlockPosHelperBlocked.Result.WAITING;
         } else {
            return blockedHit == BlockPosHelperBlocked.Result.FAILED ? this.getCoverWaiting() : blockedHit;
         }
      } else {
         return this.getCoverWaiting();
      }
   }

   public void updateGoneCharge() {
      this.chargeGoneFlag = true;
   }

   private boolean isUnsafeCover() {
      if (this.unsafe != null && this.unsafe.cover() != null) {
         ClientWorld world = MinecraftClient.getInstance().world;
         return world == null || !world.getBlockState(this.unsafe.cover()).isOf(Blocks.GLOWSTONE);
      } else {
         return false;
      }
   }

   public UnsafeCharge(ChargeAmountApi timeout) {
      this.placeAnchorValue = -1;
      this.chargeGlowstoneValue = -1;
      this.detonateDetonatorValue = -1;
      this.placeTimeoutStartMs = Long.MIN_VALUE;
      this.unsafeChargeTime = Long.MIN_VALUE;
      this.unsafeTime = Long.MIN_VALUE;
      this.timeout = timeout;
      this.armedCoverBlockPosHelperBloc = new BlockPosHelperBlocked(timeout, 2, true);
   }

   public boolean isArmedCover() {
      return this.coverArmedType == UnsafeCharge.Phase.PLACE && this.armedCoverBlockPosHelperBloc.isFlag();
   }

   private boolean canDetonatorUndyingForClientPlayerEntity(ClientPlayerEntity clientPlayerEntity) {
      boolean detonatorUndyingFlag = clientPlayerEntity.getInventory().getStack(this.detonateDetonatorValue).isOf(Items.TOTEM_OF_UNDYING)
         && !AutoInventoryTotem.isSlotInPendingSwap(this.detonateDetonatorValue);
      boolean detonatorUndyingFlag2 = clientPlayerEntity.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING) && !AutoInventoryTotem.isSlotInPendingSwap(40);
      return detonatorUndyingFlag || detonatorUndyingFlag2;
   }

   public boolean isArmedCoverCoverArmedType() {
      return this.coverArmedType == UnsafeCharge.Phase.PLACE && this.armedCoverBlockPosHelperBloc.isHitBlock();
   }

   private void updateTimeoutPlace() {
      this.placeTimeoutFlag = true;
      this.placeTimeoutStartMs = this.timeout.getTime();
   }

   private static boolean canInventoryStack(ClientPlayerEntity clientPlayerEntity, int inventoryStackValue, Item item2) {
      return canFlag(inventoryStackValue) && clientPlayerEntity.getInventory().getStack(inventoryStackValue).isOf(item2);
   }

   private static int getValue() {
      return Math.round(EntityDamageUtils.getCachedHurtTimeLimit() / 50.0F);
   }

   private static int[] getInventoryStack(ClientPlayerEntity clientPlayerEntity, Item item2) {
      int[] ints = new int[9];

      for (int i = 0; i < 9; i++) {
         ItemStack stack = clientPlayerEntity.getInventory().getStack(i);
         ints[i] = stack.isOf(item2) ? stack.getCount() : 0;
      }

      return ints;
   }

   private BlockPosHelperBlocked.Result getCoverWaiting() {
      this.blockPos = null;
      this.handleTimeoutDetonate(this.getChargeDetonate(UnsafeCharge.Phase.COVER));
      return BlockPosHelperBlocked.Result.WAITING;
   }

   private BlockPosHelperBlocked.Result getTimeoutCharge(ClientPlayerEntity clientPlayerEntity) {
      int chargesRespawn = BlockPosUtilsRespawn.getChargesRespawn(this.immutable);
      if (chargesRespawn < 0) {
         return this.getUnsafeArmed("anchor gone");
      } else if (chargesRespawn == 0) {
         return this.getUnsafeArmed("no charge");
      } else if (this.chargeGoneFlag) {
         if (this.timeout.getTime() > this.timeoutChargeTime) {
            this.unsafeChargeTime = this.timeout.getTime();
         }

         this.handleTimeoutDetonate(UnsafeCharge.Phase.DETONATE);
         return BlockPosHelperBlocked.Result.WAITING;
      } else if (this.timeout.getTime() - this.timeoutChargeTime >= 10L) {
         return this.getUnsafeArmed("timeout");
      } else {
         this.timeout.canSelectedInventoryForInt(this.detonateDetonatorValue);
         this.handleSidePos(clientPlayerEntity, true);
         return BlockPosHelperBlocked.Result.WAITING;
      }
   }

   public void handleDetonateDetonator(int detonateDetonatorValue) {
      if (canFlag(detonateDetonatorValue) && detonateDetonatorValue != this.detonateDetonatorValue) {
         this.detonateDetonatorValue = detonateDetonatorValue;
         if (this.coverArmedType == UnsafeCharge.Phase.DETONATE && this.chargeUnsafeActive && !this.isTimeoutDetonate()) {
            this.armedCoverBlockPosHelperBloc.updateTextImmutableText();
            this.chargeUnsafeActive = false;
         }
      }
   }

   private BlockPosHelperBlocked.Result getDetonateUnsafe(ClientPlayerEntity clientPlayerEntity) {
      int chargesRespawn = BlockPosUtilsRespawn.getChargesRespawn(this.immutable);
      if (chargesRespawn < 0) {
         return this.placeTimeoutFlag ? this.getDoneUnsafe() : this.getUnsafeArmed("anchor gone");
      } else {
         if (!this.isTimeoutDetonate()) {
            if (chargesRespawn == 0) {
               return this.getUnsafeArmed("no charge");
            }

            if (clientPlayerEntity.getOffHandStack().isOf(Items.GLOWSTONE)) {
               return this.getUnsafeArmed("glowstone in offhand");
            }

            if (!BlockPosUtilsRespawn.canRespawnGlowstone(clientPlayerEntity.getInventory().getStack(this.detonateDetonatorValue))) {
               return this.getUnsafeArmed("no detonator");
            }

            if (this.isChargeTimeout()) {
               return this.getUnsafeArmed("detonate timeout");
            }

            if (this.timeout.isSprintSneak()) {
               this.timeout.canSelectedInventoryForInt(this.detonateDetonatorValue);
               this.handleSidePos(clientPlayerEntity, true);
               return BlockPosHelperBlocked.Result.WAITING;
            }

            float immutable = this.getImmutable(clientPlayerEntity);
            float detonateUnsafeValue = this.getMin(immutable);
            boolean detonateUnsafeFlag = this.unsafe.needsTotem() || immutable >= clientPlayerEntity.getHealth() + clientPlayerEntity.getAbsorptionAmount() - 1.0F;
            if (detonateUnsafeFlag && !this.canDetonatorUndyingForClientPlayerEntity(clientPlayerEntity)) {
               if (++this.totemValue > 10) {
                  return this.getUnsafeArmed("no totem");
               }

               this.timeout.canSelectedInventoryForInt(this.detonateDetonatorValue);
               this.handleSidePos(clientPlayerEntity, true);
               return BlockPosHelperBlocked.Result.WAITING;
            }

            if (!this.timeout.canAmountAbsorption(immutable, detonateUnsafeValue, this.unsafe.kills() && canFlag(this.unsafeChargeTime, this.unsafeTime)) && !this.unsafeCoverFlag) {
               return this.getUnsafeArmed("unsafe");
            }
         }

         if (!this.chargeUnsafeActive) {
            if (!this.armedCoverBlockPosHelperBloc.canImmutableFlag(this.immutable, this.detonateDetonatorValue, UnsafeCharge::canRespawnAnchor, this.detonateChargeValue)) {
               return this.getUnsafeArmed("can't detonate");
            }

            this.chargeUnsafeActive = true;
         }

         BlockPosHelperBlocked.Result blockedHit = this.armedCoverBlockPosHelperBloc.getBlockedHit();
         if (blockedHit == BlockPosHelperBlocked.Result.ACTED) {
            this.updateTimeoutPlace();
         }

         if (blockedHit == BlockPosHelperBlocked.Result.DONE) {
            return this.getDoneUnsafe();
         } else {
            return blockedHit == BlockPosHelperBlocked.Result.FAILED ? this.getUnsafeArmed(this.formatPosBlock("detonate")) : blockedHit;
         }
      }
   }

   public PosBlockUtilsSelf.Plan getUnsafe() {
      return this.unsafe;
   }

   public boolean canUnsafeArmed(PosBlockUtilsSelf.Plan unsafe) {
      if (this.coverArmedType == UnsafeCharge.Phase.ARMED && unsafe != null && this.immutable != null && this.immutable.equals(unsafe.anchor())) {
         this.unsafe = unsafe;
         this.unsafeTime = this.timeout.getTime();
         return true;
      } else {
         return false;
      }
   }

   private BlockPosHelperBlocked.Result getPlaceTimeout(ClientPlayerEntity clientPlayerEntity) {
      if (BlockPosUtilsRespawn.getChargesRespawn(this.immutable) >= 0) {
         this.handleTimeoutDetonate(this.getChargeDetonate(UnsafeCharge.Phase.PLACE));
         return BlockPosHelperBlocked.Result.WAITING;
      } else if (this.isChargeTimeout()) {
         return this.getUnsafeArmed("place timeout");
      } else if (!this.placeTimeoutFlag && this.timeout.getTime() - this.timeoutChargeTime > 8 + PosBlockUtilsSelf.getValue(this.unsafe.turn())) {
         return this.getUnsafeArmed("place stall");
      } else {
         if (!this.chargeUnsafeActive) {
            if (!canInventoryStack(clientPlayerEntity, this.placeAnchorValue, Items.RESPAWN_ANCHOR)) {
               return this.getUnsafeArmed("no anchor");
            }

            if (!this.armedCoverBlockPosHelperBloc
               .canImmutableFlag(this.immutable, this.placeAnchorValue, Blocks.RESPAWN_ANCHOR.getDefaultState(), null, this.detonateChargeValue)) {
               return this.getUnsafeArmed("can't place");
            }

            this.chargeUnsafeActive = true;
         }

         BlockPosHelperBlocked.Result blockedHit = this.armedCoverBlockPosHelperBloc.getBlockedHit();
         if (blockedHit == BlockPosHelperBlocked.Result.ACTED) {
            this.updateTimeoutPlace();
         }

         if (blockedHit == BlockPosHelperBlocked.Result.DONE) {
            this.handleTimeoutDetonate(this.getChargeDetonate(UnsafeCharge.Phase.PLACE));
            return BlockPosHelperBlocked.Result.WAITING;
         } else {
            return blockedHit == BlockPosHelperBlocked.Result.FAILED ? this.getUnsafeArmed(this.formatPosBlock("place")) : blockedHit;
         }
      }
   }

   private static boolean canChargesRespawn(BlockState state) {
      return state.isOf(Blocks.RESPAWN_ANCHOR) && state.get(RespawnAnchorBlock.CHARGES) > 0;
   }

   static boolean canFlag(long unsafeChargeValue, long unsafeChargeLimit) {
      return unsafeChargeValue == Long.MIN_VALUE || unsafeChargeLimit >= unsafeChargeValue;
   }

   private float getImmutable(ClientPlayerEntity entity) {
      float boxBounding = EntityCenterUtils.getBoxBounding(this.immutable, null, null);
      return PosBlockUtilsBonus.getToughnessEpf(boxBounding, BlockEntityUtils.getAttributeArmor(entity));
   }

   private boolean isChargeTimeout() {
      return this.timeout.getTime() - this.timeoutChargeTime > 20L + 2L * this.detonateChargeValue;
   }

   private BlockPosHelperBlocked.Result getDoneUnsafe() {
      this.armedCoverBlockPosHelperBloc.updateTextImmutableText();
      this.coverArmedType = UnsafeCharge.Phase.DONE;
      this.unsafeChargeText = null;
      this.chargeUnsafeActive = false;
      return BlockPosHelperBlocked.Result.DONE;
   }

   public BlockPos getImmutable() {
      return this.immutable;
   }

   public BlockPosHelperBlocked.Result getNetherArmed() {
      if (!this.isCoverArmedType()) {
         return BlockPosHelperBlocked.Result.IDLE;
      } else {
         MinecraftClient instance = MinecraftClient.getInstance();
         ClientPlayerEntity player = instance.player;
         if (player != null && instance.world != null) {
            if (!BlockPosUtilsRespawn.isRegistryWorld()) {
               return this.getUnsafeArmed("nether");
            } else {
               BlockPosHelperBlocked.Result netherArmed = BlockPosHelperBlocked.Result.WAITING;

               for (int i = 0; i < UnsafeCharge.Phase.values().length; i++) {
                  int netherCount = this.netherCount;

                  netherArmed = switch (this.coverArmedType) {
                     case PLACE -> this.getPlaceTimeout(player);
                     case COVER -> this.getGlowstonePos(player);
                     case CHARGE -> this.getChargeUnsafe(player);
                     case ARMED -> this.getTimeoutCharge(player);
                     case DETONATE -> this.getDetonateUnsafe(player);
                     default -> BlockPosHelperBlocked.Result.IDLE;
                  };
                  if (this.coverArmedType == UnsafeCharge.Phase.DONE) {
                     return BlockPosHelperBlocked.Result.DONE;
                  }

                  if (this.coverArmedType == UnsafeCharge.Phase.FAILED) {
                     return BlockPosHelperBlocked.Result.FAILED;
                  }

                  if (this.netherCount == netherCount || netherArmed == BlockPosHelperBlocked.Result.ACTED) {
                     return netherArmed;
                  }
               }

               return netherArmed;
            }
         } else {
            return this.getUnsafeArmed("no world");
         }
      }
   }

   private void handleSidePos(ClientPlayerEntity clientPlayerEntity, boolean sidePosFlag) {
      Vec3d eyePos = clientPlayerEntity.getEyePos();
      double sidePosValue = this.timeout.getAmountDouble();
      if (this.pos == null || !this.canSquaredDistance(this.pos, this.sideSidePos, eyePos, sidePosValue)) {
         this.pos = null;
         this.sideSidePos = null;
         float[] wrapDegrees = MathClampUtils.getWrapDegrees(eyePos, Vec3d.ofCenter(this.immutable));
         BlockHitResult posEye = BlockHitResolver.findNearestRotationHit(this.immutable, sidePosValue, wrapDegrees[0], wrapDegrees[1]);
         if (posEye != null) {
            this.sideSidePos = posEye.getSide();
            Vec3d vec3d = BlockEntityUtils.getVec3d(this.immutable, this.sideSidePos);
            this.pos = this.canSquaredDistance(vec3d, this.sideSidePos, eyePos, sidePosValue) ? vec3d : posEye.getPos();
         }
      }

      this.timeout.handleEyeAction(this.pos != null ? this.pos : Vec3d.ofCenter(this.immutable), sidePosFlag);
   }

   public UnsafeCharge.Phase getCoverArmedType() {
      return this.coverArmedType;
   }

   public boolean isCoverArmedType() {
      return this.coverArmedType == UnsafeCharge.Phase.PLACE
         || this.coverArmedType == UnsafeCharge.Phase.COVER
         || this.coverArmedType == UnsafeCharge.Phase.CHARGE
         || this.coverArmedType == UnsafeCharge.Phase.ARMED
         || this.coverArmedType == UnsafeCharge.Phase.DETONATE;
   }

   public boolean canUnsafeImmutable(PosBlockUtilsSelf.Plan unsafe, int placeAnchorValue, int chargeGlowstoneValue, int detonateDetonatorValue, boolean flag) {
      if (!this.isCoverArmedType() && unsafe != null && unsafe.anchor() != null) {
         MinecraftClient instance = MinecraftClient.getInstance();
         if (instance.player == null || instance.world == null || !BlockPosUtilsRespawn.isRegistryWorld()) {
            return false;
         } else if (!canFlag(detonateDetonatorValue)) {
            return false;
         } else {
            BlockPos immutable = unsafe.anchor().toImmutable();
            int chargesRespawn = BlockPosUtilsRespawn.getChargesRespawn(immutable);
            if (chargesRespawn >= 0 || canInventoryStack(instance.player, placeAnchorValue, Items.RESPAWN_ANCHOR) && BlockPosUtilsRespawn.canReplaceableAir(immutable)) {
               if (chargesRespawn <= 0 && !canInventoryStack(instance.player, chargeGlowstoneValue, Items.GLOWSTONE)) {
                  return false;
               } else {
                  this.armedCoverBlockPosHelperBloc.updateTextImmutableText();
                  this.unsafe = unsafe;
                  this.immutable = immutable;
                  this.blockPos = unsafe.cover() != null && canInventoryStack(instance.player, chargeGlowstoneValue, Items.GLOWSTONE)
                     ? unsafe.cover().toImmutable()
                     : null;
                  this.placeAnchorValue = placeAnchorValue;
                  this.chargeGlowstoneValue = chargeGlowstoneValue;
                  this.detonateDetonatorValue = detonateDetonatorValue;
                  this.unsafeChargeActive = flag;
                  this.chargeGoneFlag = false;
                  this.unsafeCoverFlag = false;
                  this.pos = null;
                  this.sideSidePos = null;
                  this.unsafeChargeText = null;
                  this.unsafeChargeTime = Long.MIN_VALUE;
                  this.unsafeTime = this.timeout.getTime();
                  this.handleTimeoutDetonate(this.getChargeDetonate(UnsafeCharge.Phase.IDLE));
                  return true;
               }
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private String formatPosBlock(String posBlockText) {
      String text2 = this.armedCoverBlockPosHelperBloc.getText();
      return text2 == null ? posBlockText + " failed" : posBlockText + ": " + text2;
   }

   private boolean canSquaredDistance(Vec3d vec, Direction direction, Vec3d vec2, double squaredDistanceFactor) {
      if (vec2.squaredDistanceTo(vec) > squaredDistanceFactor * squaredDistanceFactor) {
         return false;
      } else {
         float[] wrapDegrees = MathClampUtils.getWrapDegrees(vec2, vec);
         return BlockHitResolver.findBlockHitOnFace(this.immutable, direction, squaredDistanceFactor, wrapDegrees[0], wrapDegrees[1]) != null;
      }
   }

   private static boolean canFlag(int unsafeChargeLimit) {
      return unsafeChargeLimit >= 0 && unsafeChargeLimit <= 8;
   }

   private BlockPosHelperBlocked.Result getUnsafeArmed(String text) {
      this.armedCoverBlockPosHelperBloc.updateTextImmutableText();
      this.coverArmedType = UnsafeCharge.Phase.FAILED;
      this.unsafeChargeText = text;
      this.chargeUnsafeActive = false;
      return BlockPosHelperBlocked.Result.FAILED;
   }

   private boolean canUnsafeAbsorption(ClientPlayerEntity clientPlayerEntity) {
      float immutable = this.getImmutable(clientPlayerEntity);
      boolean unsafeAbsorptionFlag = this.unsafe.needsTotem() || immutable >= clientPlayerEntity.getHealth() + clientPlayerEntity.getAbsorptionAmount() - 1.0F;
      return unsafeAbsorptionFlag && !this.canDetonatorUndying(clientPlayerEntity) ? false : this.timeout.canAmountAbsorption(immutable, this.getMin(immutable), this.unsafe.kills());
   }

   private float getMin(float unsafeChargeLimit) {
      float selfFull = this.unsafe.selfFull();
      return !(selfFull <= 0.001F) && !(unsafeChargeLimit > selfFull + 0.5F) ? Math.min(unsafeChargeLimit, this.unsafe.selfCost() * unsafeChargeLimit / selfFull) : unsafeChargeLimit;
   }

   private UnsafeCharge.Phase getChargeDetonate(UnsafeCharge.Phase phase) {
      return switch (phase) {
         case IDLE -> BlockPosUtilsRespawn.getChargesRespawn(this.immutable) < 0
            ? UnsafeCharge.Phase.PLACE
            : this.getChargeDetonate(UnsafeCharge.Phase.PLACE);
         case PLACE -> this.blockPos != null && BlockPosUtilsRespawn.canReplaceableAir(this.blockPos)
            ? UnsafeCharge.Phase.COVER
            : this.getChargeDetonate(UnsafeCharge.Phase.COVER);
         case COVER -> BlockPosUtilsRespawn.getChargesRespawn(this.immutable) > 0
            ? this.getChargeDetonate(UnsafeCharge.Phase.CHARGE)
            : UnsafeCharge.Phase.CHARGE;
         case CHARGE -> this.unsafeChargeActive && !this.chargeGoneFlag ? UnsafeCharge.Phase.ARMED : UnsafeCharge.Phase.DETONATE;
         case ARMED -> UnsafeCharge.Phase.DETONATE;
         default -> UnsafeCharge.Phase.DONE;
      };
   }

   private void handleTimeoutDetonate(UnsafeCharge.Phase coverArmedType) {
      this.armedCoverBlockPosHelperBloc.updateTextImmutableText();
      this.coverArmedType = coverArmedType;
      this.timeoutChargeTime = this.timeout.getTime();
      this.detonateChargeValue = (coverArmedType == UnsafeCharge.Phase.DETONATE ? 10 : 4) + getValue();
      this.chargeUnsafeActive = false;
      this.placeTimeoutFlag = false;
      this.placeTimeoutStartMs = Long.MIN_VALUE;
      this.totemValue = 0;
      this.netherCount++;
   }

   public int getPosBlock() {
      return this.isCoverArmedType() ? this.armedCoverBlockPosHelperBloc.getBlockedHitImmutableActive() : Integer.MAX_VALUE;
   }

   public boolean isArmedCoverFlag() {
      return this.coverArmedType == UnsafeCharge.Phase.PLACE && this.armedCoverBlockPosHelperBloc.isBlockedEntity();
   }

   private BlockPosHelperBlocked.Result getChargeUnsafe(ClientPlayerEntity clientPlayerEntity) {
      int chargesRespawn = BlockPosUtilsRespawn.getChargesRespawn(this.immutable);
      if (chargesRespawn < 0) {
         return this.getUnsafeArmed("anchor gone");
      } else if (chargesRespawn > 0) {
         this.handleTimeoutDetonate(this.getChargeDetonate(UnsafeCharge.Phase.CHARGE));
         return BlockPosHelperBlocked.Result.WAITING;
      } else if (this.isChargeTimeout()) {
         return this.getUnsafeArmed("charge timeout");
      } else {
         if (!this.isTimeoutDetonate()) {
            if (!canInventoryStack(clientPlayerEntity, this.chargeGlowstoneValue, Items.GLOWSTONE)) {
               int chargeGlowstoneValue = getValueForIntIntArray(this.chargeGlowstoneValue, getInventoryStack(clientPlayerEntity, Items.GLOWSTONE), getFlags());
               if (chargeGlowstoneValue < 0) {
                  return this.getUnsafeArmed("no glowstone");
               }

               this.chargeGlowstoneValue = chargeGlowstoneValue;
               this.armedCoverBlockPosHelperBloc.updateTextImmutableText();
               this.chargeUnsafeActive = false;
            }

            if (this.timeout.isSprintSneak()) {
               this.handleSidePos(clientPlayerEntity, false);
               return BlockPosHelperBlocked.Result.WAITING;
            }
         }

         if (!this.chargeUnsafeActive) {
            if (this.isUnsafeCover() && !this.canUnsafeAbsorption(clientPlayerEntity)) {
               return this.getUnsafeArmed("unsafe without cover");
            }

            if (!this.unsafeCoverFlag && !this.canUnsafeAbsorption(clientPlayerEntity)) {
               return this.getUnsafeArmed("unsafe");
            }

            if ((this.unsafe.needsTotem() || this.getImmutable(clientPlayerEntity) >= clientPlayerEntity.getHealth() + clientPlayerEntity.getAbsorptionAmount() - 1.0F)
               && !this.canDetonatorUndyingForClientPlayerEntity(clientPlayerEntity)) {
               if (++this.totemValue > 10) {
                  return this.getUnsafeArmed("no totem");
               }

               this.handleSidePos(clientPlayerEntity, false);
               return BlockPosHelperBlocked.Result.WAITING;
            }

            if (!this.armedCoverBlockPosHelperBloc.canImmutableFlag(this.immutable, this.chargeGlowstoneValue, UnsafeCharge::canChargesRespawn, this.detonateChargeValue)) {
               return this.getUnsafeArmed("can't charge");
            }

            this.chargeUnsafeActive = true;
         }

         BlockPosHelperBlocked.Result blockedHit = this.armedCoverBlockPosHelperBloc.getBlockedHit();
         if (blockedHit == BlockPosHelperBlocked.Result.ACTED) {
            this.updateTimeoutPlace();
         }

         if (blockedHit == BlockPosHelperBlocked.Result.DONE) {
            this.handleTimeoutDetonate(this.getChargeDetonate(UnsafeCharge.Phase.CHARGE));
            return BlockPosHelperBlocked.Result.WAITING;
         } else {
            return blockedHit == BlockPosHelperBlocked.Result.FAILED ? this.getUnsafeArmed(this.formatPosBlock("charge")) : blockedHit;
         }
      }
   }

   public static enum Phase {
      IDLE,
      PLACE,
      COVER,
      CHARGE,
      ARMED,
      DETONATE,
      DONE,
      FAILED;
   }
}
