package com.corz.client.categories.combat;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.config.KeybindSetting;
import com.corz.client.entity.ClientThreadUtils;
import com.corz.client.entity.FloorMathUtilsBlock;
import com.corz.client.gui.DisabledEnabled;
import com.corz.client.inventory.BlockPosUtilsRespawn;
import com.corz.client.inventory.InventorySelectedUtils;
import com.corz.client.world.MathClampUtils;
import java.util.Random;
import java.util.function.Predicate;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.CakeBlock;
import net.minecraft.block.CandleCakeBlock;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.DragonEggBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.FlowerPotBlock;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.client.util.InputUtil;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.lwjgl.glfw.GLFW;
import com.corz.client.utils.AutoVanish;
import com.corz.client.utils.InventoryPlayer;
import com.corz.client.categories.settings.Notifications;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.categories.combat.AnchorMacro;
import com.corz.client.utils.AnchorMacroUtils;
import com.corz.client.categories.combat.AutoInventoryTotem;
import com.corz.client.utils.GlowstoneTimeout;
import com.corz.client.utils.IconsPngType;
import com.corz.client.utils.PageRaltUtils;
import com.corz.client.utils.WeaknessGodApi;

public class SafeAnchor extends Module implements WeaknessGodApi {
   private BlockHitResult glowstone;
   private SafeAnchor.AutoState autoState;
   private BlockHitResult shieldPlaceHit;
   private boolean detonatePhaseActive;
   private int lastInteractAgeTicks;
   private boolean wasTriggerKeyPressed;
   private final BooleanSetting autoAnchorMode;
   private boolean selfInteracting;
   private boolean clickedThisTick;
   private BlockPos macroFallbackPos;
   private boolean coverPlaceSent;
   private final BooleanSetting fastCombo;
   private BlockHitResult detonateAnchorHit;
   private int autoTotemSlot;
   private int shieldGlowstoneSlot;
   private final BooleanSetting placeCover;
   private final KeybindSetting triggerKey;
   private GlowstoneTimeout.State previousShieldState;
   private final EnumSetting rotationMode = new EnumSetting(
      "Rotation",
      "How to face the block before placing/detonating (needed \u2014 interacting with a block your aim can't support is a hard anticheat flag). Visible: the camera briefly turns to the block; keeps FULL movement speed and is the cleanest profile (a real aim, so no Simulation/PacketOrder/RotationPlace). Silent: camera stays still, but your movement coasts/slows on each aim tick (yaw-independent so GrimAC's movement sim stays clean) and you may still see occasional low-VL flags. Off: never rotate \u2014 it only clicks when your own crosshair is already on the block.",
      "Visible",
      "Visible",
      "Silent",
      "Off"
   );
   private final BooleanSetting swapBackToAnchor;
   private boolean anchorPlaceSent;
   private int detonateDelayTicks;
   private boolean anchorCharged;
   private BlockPos cover;
   private final BooleanSetting lootSafety;
   private BlockPos detonate;
   private BlockPos anchor;
   private boolean rotationOffNoticeShown;
   private BlockPos shieldBlockPos;
   private final IntegerSetting confirmTimeoutSetting;
   private final GlowstoneTimeout glowstoneTimeout;
   private boolean detonateClicked;
   private boolean shieldPlaceSent;
   private static final Random RANDOM = new Random();
   private final IntegerSetting totemSlot;
   private final BooleanSetting macroFallback = new BooleanSetting(
      "Macro Fallback",
      "If a shielded detonation isn't possible (a tight 1x3, no solid block to place the shield against, or no glowstone), hand the charged anchor back to Anchor Macro so it detonates normally. Off = only ever detonate once the glowstone shield is confirmed.",
      true
   );
   private int detonateItemSlot;
   private boolean coverRetried;
   private boolean chargeRetried;
   private int lastProcessedAgeTicks;
   private BlockHitResult detonateAimHit;
   private BlockHitResult autoComboHit;
   private int aimWaitTicks;
   private boolean shieldableBlocked;
   private int lastPlayerAgeTicks;
   private int originalSlot;
   private int stepWaitTicks;

   private Box getOutlineBox(BlockPos pos) {
      VoxelShape outlineShape = mc.world.getBlockState(pos).getOutlineShape(mc.world, pos);
      return outlineShape.isEmpty() ? null : outlineShape.getBoundingBox().offset(pos);
   }

   private boolean hasLootNear(BlockPos pos2) {
      return BlockPosUtilsRespawn.canHelmetBoots(pos2, 10.0);
   }

   private BlockHitResult raycastAlongAim() {
      Vec3d eyePos = mc.player.getEyePos();
      double minBlockInteractionRange = Math.min(4.5, mc.player.getBlockInteractionRange());
      Vec3d add = eyePos.add(this.getLookDirection().multiply(minBlockInteractionRange));
      return mc.world.raycast(new RaycastContext(eyePos, add, ShapeType.OUTLINE, FluidHandling.NONE, mc.player));
   }

   private boolean trySelectAnchorTarget() {
      HitResult crosshairTarget = mc.crosshairTarget;
      if (crosshairTarget != null && crosshairTarget.getType() == Type.BLOCK) {
         BlockHitResult blockHitResult = (BlockHitResult)crosshairTarget;
         BlockPos blockPos = blockHitResult.getBlockPos();
         BlockPos anchor = this.isReplaceableAir(blockPos) ? blockPos : blockPos.offset(blockHitResult.getSide());
         BlockPos blockPos2 = mc.player.getBlockPos();
         if (anchor.equals(blockPos2) || anchor.equals(blockPos2.up()) || anchor.equals(blockPos2.down())) {
            return false;
         } else if (!this.isWithinEyeReach(Vec3d.ofCenter(anchor))) {
            return false;
         } else {
            this.anchor = anchor;
            this.cover = this.findCoverPos(anchor);
            this.autoComboHit = this.isPlacementHitValid(blockHitResult) ? blockHitResult : this.findPlacementHit(anchor, null);
            return true;
         }
      } else {
         return false;
      }
   }

   private BlockHitResult getAimedPlacementHit(BlockHitResult blockHitResult) {
      if (blockHitResult != null && this.canClickThisTick()) {
         BlockHitResult posBlock = this.getAimedHitAt(blockHitResult.getBlockPos());
         if (posBlock == null) {
            return null;
         } else {
            return !this.isReplaceableAir(blockHitResult.getBlockPos()) && posBlock.getSide() != blockHitResult.getSide() ? null : posBlock;
         }
      } else {
         return null;
      }
   }

   private boolean isInteractableBlock(BlockState state, BlockPos pos2) {
      Block block = state.getBlock();
      return state.hasBlockEntity()
         || block instanceof RespawnAnchorBlock
         || block instanceof DoorBlock
         || block instanceof TrapdoorBlock
         || block instanceof FenceGateBlock
         || block instanceof ButtonBlock
         || block instanceof LeverBlock
         || block instanceof NoteBlock
         || block instanceof AbstractRedstoneGateBlock
         || block instanceof CakeBlock
         || block instanceof CandleCakeBlock
         || block instanceof DragonEggBlock
         || block instanceof RedstoneWireBlock
         || block instanceof ComposterBlock
         || block instanceof FlowerPotBlock
         || state.createScreenHandlerFactory(mc.world, pos2) != null;
   }

   private void resyncAnchorMacroSlot() {
      if (ModuleRegistry.getModule(AnchorMacro.class) instanceof AnchorMacro anchorMacro && anchorMacro.isEnabled()) {
         anchorMacro.startPearlSequence();
      }
   }

   private boolean isDetonateClickBlocked(BlockPos pos, boolean handStackFlag) {
      boolean handStackFlag2 = !mc.player.getMainHandStack().isEmpty() || !mc.player.getOffHandStack().isEmpty();
      if (mc.player.shouldCancelInteraction() && handStackFlag2) {
         return true;
      } else if (!handStackFlag) {
         return false;
      } else {
         BlockState blockState = mc.world.getBlockState(pos);
         return (mc.player.getOffHandStack().isOf(Items.GLOWSTONE) || mc.player.getMainHandStack().isOf(Items.GLOWSTONE))
            && blockState.isOf(Blocks.RESPAWN_ANCHOR)
            && blockState.get(RespawnAnchorBlock.CHARGES) < 4;
      }
   }

   private ActionResult interactAndRecordAge(BlockHitResult blockHitResult, String ageInteractText) {
      this.rotateToward(blockHitResult.getPos());
      this.selfInteracting = true;

      ActionResult ageInteract;
      try {
         ageInteract = mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, blockHitResult);
      } finally {
         this.selfInteracting = false;
      }

      this.lastInteractAgeTicks = mc.player.age;
      return ageInteract;
   }

   private boolean canPlaceGlowstoneAt(BlockPos pos2) {
      return BlockPosUtilsRespawn.canAbsentPlace(pos2, Blocks.GLOWSTONE.getDefaultState());
   }

   private int findHotbarGlowstoneSlot() {
      PlayerInventory inventory = mc.player.getInventory();

      for (int i = 0; i < 9; i++) {
         if (inventory.getStack(i).isOf(Items.GLOWSTONE)) {
            return i;
         }
      }

      return -1;
   }

   private void resetAllState() {
      this.glowstoneTimeout.updateTimeoutAim();
      this.previousShieldState = GlowstoneTimeout.State.IDLE;
      this.macroFallbackPos = null;
      this.resetShieldState();
      this.resetAutoCombo();
      this.wasTriggerKeyPressed = true;
      this.rotationOffNoticeShown = false;
   }

   private Vec3d getInsetHitPos(BlockHitResult blockHitResult) {
      BlockPos blockPos = blockHitResult.getBlockPos();
      VoxelShape outlineShape = mc.world.getBlockState(blockPos).getOutlineShape(mc.world, blockPos);
      Box box = outlineShape.isEmpty() ? new Box(blockPos) : outlineShape.getBoundingBox().offset(blockPos);
      Vec3d pos = blockHitResult.getPos();
      Axis axis = blockHitResult.getSide().getAxis();
      double x = axis == Axis.X ? pos.x : clampInsideRange(pos.x, box.minX, box.maxX);
      double y = axis == Axis.Y ? pos.y : clampInsideRange(pos.y, box.minY, box.maxY);
      double z = axis == Axis.Z ? pos.z : clampInsideRange(pos.z, box.minZ, box.maxZ);
      return new Vec3d(x, y, z);
   }

   private BlockHitResult aimUntilReady(BlockHitResult autoAnchor, BlockHitResult coverAuto) {
      if (coverAuto != null) {
         this.aimWaitTicks = 0;
         return coverAuto;
      } else {
         this.autoComboHit = autoAnchor;
         this.rotateToward(this.getInsetHitPos(autoAnchor));
         this.aimWaitTicks++;
         return null;
      }
   }

   public boolean isAutoComboRunning() {
      return this.autoAnchorMode.isEnabled() && this.autoState != SafeAnchor.AutoState.IDLE;
   }

   @Override
   public void onDisable() {
      this.resetAllState();
   }

   private GlowstoneTimeout.Signals createInactiveSignals() {
      GlowstoneTimeout.Signals signals = new GlowstoneTimeout.Signals();
      signals.glowstoneDetonateActive = false;
      return signals;
   }

   private void executeShieldAction(GlowstoneTimeout.Action action) {
      switch (action) {
         case SELECT_GLOWSTONE_SLOT:
            this.selectHotbarSlot(this.shieldGlowstoneSlot, "glowstone");
            if (this.glowstone != null) {
               this.rotateToward(this.glowstone.getPos());
            }
            break;
         case PLACE_GLOWSTONE:
            if (this.shieldPlaceHit != null) {
               this.interactAndRecordAge(this.shieldPlaceHit, "PLACE_PROTECTION");
               this.shieldPlaceSent = FloorMathUtilsBlock.canInstanceWorld(this.shieldBlockPos);
            }
            break;
         case SELECT_DETONATE_SLOT:
            this.selectHotbarSlot(this.detonateItemSlot, "detonate");
            if (this.detonate != null) {
               if (!this.isHitReachableFromEye(this.detonateAimHit)) {
                  this.detonateAimHit = this.findAnchorClickHit(this.detonate);
               }

               if (this.detonateAimHit != null) {
                  this.rotateToward(this.detonateAimHit.getPos());
               }
            }
            break;
         case DETONATE:
            this.clickDetonateAnchor();
            break;
         case RESTORE_SLOT:
            this.selectHotbarSlot(this.getRestoreSlot(), "restore");
         case NONE:
      }
   }

   @Override
   public boolean isAgePlayer() {
      if (mc.player == null) {
         return false;
      } else if (this.autoState == SafeAnchor.AutoState.IDLE && mc.player.age != this.lastInteractAgeTicks) {
         GlowstoneTimeout.State protectionDetonateType = this.glowstoneTimeout.getProtectionDetonateType();
         return protectionDetonateType != GlowstoneTimeout.State.IDLE && protectionDetonateType != GlowstoneTimeout.State.DONE && protectionDetonateType != GlowstoneTimeout.State.FAILED;
      } else {
         return true;
      }
   }

   private GlowstoneTimeout.Signals buildShieldSignals(boolean flag) {
      GlowstoneTimeout.Signals signals = new GlowstoneTimeout.Signals();
      signals.glowstoneDetonateActive = flag;
      signals.timeoutLimit = this.detonateDelayTicks > 0 ? this.detonateDelayTicks : this.confirmTimeoutSetting.getValue();
      signals.detonateCharges = this.isDetonateTargetCharged();
      signals.invalidAnchorFlag = this.detonate != null && this.shieldBlockPos == null && this.shieldableBlocked;
      this.shieldGlowstoneSlot = this.findHotbarGlowstoneSlot();
      signals.glowstoneReplaceableFlag = this.shieldGlowstoneSlot != -1;
      this.detonateItemSlot = this.findDetonateItemSlot();
      signals.signalsActive = this.detonateItemSlot != -1;
      this.shieldPlaceHit = null;
      if (this.shieldBlockPos != null) {
         signals.replaceableGlowstoneFlag = this.isReplaceableAir(this.shieldBlockPos) && this.canPlaceGlowstoneAt(this.shieldBlockPos) && !isPlayerBlocking(this.shieldBlockPos);
         if (!this.isPlacementHitValid(this.glowstone)) {
            this.glowstone = this.findPlacementHit(this.shieldBlockPos, this.detonate);
         }

         signals.replaceableFaceFlag = this.glowstone != null;
         boolean of = mc.world.getBlockState(this.shieldBlockPos).isOf(Blocks.GLOWSTONE);
         boolean instanceWorld = FloorMathUtilsBlock.canInstanceWorld(this.shieldBlockPos);
         signals.glowstoneTimeoutActive = of && !instanceWorld;
         signals.glowstoneRefusedFlag = this.shieldPlaceSent && !instanceWorld && !of;
         this.shieldPlaceHit = this.getAimedPlacementHit(this.glowstone);
         signals.faceSupportFlag = this.shieldPlaceHit != null;
      }

      this.detonateAnchorHit = this.detonate == null ? null : this.getClickableAimHit(this.detonate, true);
      signals.timeoutConfirmationFlag = this.detonateAnchorHit != null;
      this.detonatePhaseActive = this.glowstoneTimeout.getProtectionDetonateType() == GlowstoneTimeout.State.DETONATE;
      return signals;
   }

   private boolean isUseButtonHeld() {
      return GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), 1) == 1;
   }

   private BlockHitResult findBestSupportClick(BlockPos pos, BlockPos pos2) {
      Vec3d eyePos = mc.player.getEyePos();
      Vec3d vec3d = this.getLookDirection();
      BlockHitResult productDot = null;
      double productDotValue = -2.0;

      for (Direction direction : Direction.values()) {
         BlockPos offset = pos.offset(direction);
         if (!offset.equals(pos2) && this.isClickableSupportBlock(offset)) {
            Direction opposite = direction.getOpposite();
            Vec3d minMax = this.getFaceClickPoint(offset, opposite, eyePos);
            if (minMax != null) {
               BlockHitResult blockHitResult = new BlockHitResult(minMax, opposite, offset, false);
               if (this.isFaceFacingEye(blockHitResult) && this.isHitClickable(blockHitResult)) {
                  double dotProduct = minMax.subtract(eyePos).normalize().dotProduct(vec3d);
                  if (dotProduct > productDotValue) {
                     productDotValue = dotProduct;
                     productDot = blockHitResult;
                  }
               }
            }
         }
      }

      return productDot;
   }

   private boolean isDetonateTargetCharged() {
      if (this.detonate == null) {
         return false;
      } else {
         BlockState blockState = mc.world.getBlockState(this.detonate);
         if (!blockState.isOf(Blocks.RESPAWN_ANCHOR)) {
            return false;
         } else {
            return blockState.get(RespawnAnchorBlock.CHARGES) == 0 ? false : this.isWithinEyeReach(Vec3d.ofCenter(this.detonate));
         }
      }
   }

   private GlowstoneTimeout.CoverPlan planGlowstoneCover(BlockPos pos, Predicate<BlockPos> predicate) {
      return BlockPosUtilsRespawn.getMinMax(pos, mc.player.getBoundingBox(), predicate, Blocks.GLOWSTONE.getDefaultState());
   }

   private void selectHotbarSlot(int selectedSlotLimit, String selectedSlotText) {
      if (selectedSlotLimit >= 0 && selectedSlotLimit <= 8 && mc.player != null) {
         PlayerInventory inventory = mc.player.getInventory();
         if (inventory.getSelectedSlot() != selectedSlotLimit) {
            InventorySelectedUtils.handleSelectedSlot(selectedSlotLimit);
         }
      }
   }

   private BlockHitResult getAimedHitAt(BlockPos pos) {
      if (pos != null && mc.player != null && mc.world != null) {
         BlockHitResult raycast = this.raycastAlongAim();
         if (raycast != null && raycast.getType() == Type.BLOCK && raycast.getBlockPos().equals(pos)) {
            return this.isHitInBlockRange(raycast) && this.getInsetHitPos(raycast).squaredDistanceTo(raycast.getPos()) < 1.0E-8 ? raycast : null;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private boolean isWithinEyeReach(Vec3d vec) {
      return mc.player.getEyePos().squaredDistanceTo(vec) <= 25.0;
   }

   private boolean isFaceVisibleFromEye(BlockHitResult blockHitResult) {
      if (blockHitResult != null && mc.player != null && mc.world != null) {
         Vec3d eyePos = mc.player.getEyePos();
         Vec3d subtract = blockHitResult.getPos().subtract(eyePos);
         double varLength = subtract.length();
         if (varLength < 1.0E-4) {
            return false;
         } else {
            Vec3d add = blockHitResult.getPos().add(subtract.multiply(0.05 / varLength));
            BlockHitResult raycast = mc.world.raycast(new RaycastContext(eyePos, add, ShapeType.OUTLINE, FluidHandling.NONE, mc.player));
            return raycast != null && raycast.getType() == Type.BLOCK && raycast.getBlockPos().equals(blockHitResult.getBlockPos()) && raycast.getSide() == blockHitResult.getSide();
         }
      } else {
         return false;
      }
   }

   private boolean isHitClickable(BlockHitResult blockHitResult) {
      return this.isHitInBlockRange(blockHitResult) && this.isFaceVisibleFromEye(blockHitResult);
   }

   private int findDetonateItemSlot() {
      PlayerInventory inventory = mc.player.getInventory();
      if (this.originalSlot >= 0 && this.originalSlot <= 8 && isDetonateItem(inventory.getStack(this.originalSlot))) {
         return this.originalSlot;
      } else {
         for (int i = 0; i < 9; i++) {
            if (isDetonateItem(inventory.getStack(i))) {
               return i;
            }
         }

         return -1;
      }
   }

   private Vec3d getFaceClickPoint(BlockPos pos, Direction direction, Vec3d vec) {
      Box offset = this.getOutlineBox(pos);
      if (offset == null) {
         return null;
      } else {
         double x = (offset.minX + offset.maxX) / 2.0;
         double y = (offset.minY + offset.maxY) / 2.0;
         double z = (offset.minZ + offset.maxZ) / 2.0;

         Vec3d minMax = switch (direction) {
            case DOWN -> new Vec3d(x, offset.minY, z);
            case UP -> new Vec3d(x, offset.maxY, z);
            case NORTH -> new Vec3d(x, y, offset.minZ);
            case SOUTH -> new Vec3d(x, y, offset.maxZ);
            case WEST -> new Vec3d(offset.minX, y, z);
            case EAST -> new Vec3d(offset.maxX, y, z);
         };
         double rangeInteraction = BlockPosUtilsRespawn.getRangeInteraction();
         if (vec.squaredDistanceTo(minMax) <= rangeInteraction * rangeInteraction) {
            return minMax;
         } else {
            Axis axis = direction.getAxis();
            Vec3d vec3d = new Vec3d(
               axis == Axis.X ? minMax.x : clampInsideFace(vec.x, offset.minX, offset.maxX, x),
               axis == Axis.Y ? minMax.y : clampInsideFace(vec.y, offset.minY, offset.maxY, y),
               axis == Axis.Z ? minMax.z : clampInsideFace(vec.z, offset.minZ, offset.maxZ, z)
            );
            return vec.squaredDistanceTo(vec3d) <= rangeInteraction * rangeInteraction ? vec3d : null;
         }
      }
   }

   private void abortIfAimStalled(boolean coverFlag) {
      if (this.aimWaitTicks > 12) {
         this.notifyUser(this.describeAimFailure(coverFlag));
         this.resetAutoCombo();
      }
   }

   private int getRestoreSlot() {
      if (this.swapBackToAnchor.isEnabled() && this.detonateClicked) {
         int inventoryRespawn = this.findHotbarAnchorSlot();
         if (inventoryRespawn != -1) {
            return inventoryRespawn;
         }
      }

      return this.originalSlot;
   }

   @Override
   protected void onPacketSend(InventoryPlayer inventoryPlayer) {
      if (mc.player != null && mc.isOnThread()) {
         if (inventoryPlayer.packet instanceof PlayerInteractBlockC2SPacket && !this.selfInteracting && !inventoryPlayer.isOpenedByClient()) {
            this.lastPlayerAgeTicks = mc.player.age;
         }

         if (inventoryPlayer.packet instanceof PlayerInteractBlockC2SPacket && !inventoryPlayer.isOpenedByClient()) {
            AnchorMacroUtils.recordInteractTime(ClientThreadUtils.getTime());
         }
      }
   }

   private BlockHitResult findAnchorClickHit(BlockPos pos) {
      Vec3d eyePos = mc.player.getEyePos();
      Vec3d rotationVec = mc.player.getRotationVec(1.0F);
      BlockHitResult posSide = this.clampHitToInteractionRange(BlockPosUtilsRespawn.getOffsetCenter(pos, eyePos, rotationVec, BlockPosUtilsRespawn.getRangeInteraction(), RANDOM));
      if (!this.isHitReachableFromEye(posSide)) {
         posSide = this.clampHitToInteractionRange(BlockPosUtilsRespawn.getOffsetCenter(pos, eyePos, rotationVec, 5.0, RANDOM));
      }

      if (!this.isHitReachableFromEye(posSide)) {
         posSide = this.findBestFaceClick(pos);
      }

      if (posSide == null
         && mc.crosshairTarget instanceof BlockHitResult blockHitResult
         && blockHitResult.getType() == Type.BLOCK
         && blockHitResult.getBlockPos().equals(pos)
         && this.isHitReachableFromEye(blockHitResult)) {
         posSide = blockHitResult;
      }

      return posSide;
   }

   private void tickAutoCombo() {
      switch (this.autoState) {
         case PLACE_ANCHOR:
            if (!this.isReplaceableAir(this.anchor)) {
               if (mc.world.getBlockState(this.anchor).isOf(Blocks.RESPAWN_ANCHOR)) {
                  this.autoState = this.placeCover.isEnabled() ? SafeAnchor.AutoState.SEL_COVER : SafeAnchor.AutoState.SEL_CHARGE;
                  this.stepWaitTicks = 0;
                  this.autoComboHit = null;
               } else {
                  this.resetAutoCombo();
               }

               return;
            }

            BlockHitResult blockHitResult = this.isPlacementHitValid(this.autoComboHit) ? this.autoComboHit : this.findPlacementHit(this.anchor, null);
            if (blockHitResult == null) {
               this.resetAutoCombo();
               return;
            }

            if (isPlayerBlocking(this.anchor)) {
               this.notifyUser("A player is in that spot \u2014 the anchor would ghost");
               this.resetAutoCombo();
               return;
            }

            this.selectHotbarSlot(this.findHotbarAnchorSlot(), "auto-anchor");
            BlockHitResult coverAuto = this.aimUntilReady(blockHitResult, this.getAimedPlacementHit(blockHitResult));
            if (coverAuto == null) {
               this.abortIfAimStalled(false);
               return;
            }

            this.clickBlockAndMarkAge(coverAuto, "anchor");
            this.anchorPlaceSent = FloorMathUtilsBlock.canInstanceWorld(this.anchor);
            this.autoState = this.placeCover.isEnabled() ? SafeAnchor.AutoState.SEL_COVER : SafeAnchor.AutoState.SEL_CHARGE;
            this.stepWaitTicks = 0;
            this.autoComboHit = null;
            break;
         case SEL_COVER:
            int inventoryGlowstone = this.findHotbarGlowstoneSlot();
            this.autoComboHit = this.cover == null ? null : this.findPlacementHit(this.cover, this.anchor);
            if (this.autoComboHit == null || inventoryGlowstone == -1 || !this.isCoverPlaceable()) {
               this.autoComboHit = null;
               this.autoState = this.getChargeOrTotemState();
               this.stepWaitTicks = 0;
               return;
            }

            this.selectHotbarSlot(inventoryGlowstone, "auto-cover");
            this.rotateToward(this.autoComboHit.getPos());
            this.autoState = SafeAnchor.AutoState.PLACE_COVER;
            break;
         case PLACE_COVER:
            int hotbarGlowstoneSlot = this.findHotbarGlowstoneSlot();
            BlockHitResult blockHitResult2 = this.isPlacementHitValid(this.autoComboHit)
               ? this.autoComboHit
               : (this.cover == null ? null : this.findPlacementHit(this.cover, this.anchor));
            if (blockHitResult2 != null && hotbarGlowstoneSlot != -1 && this.isCoverPlaceable()) {
               this.selectHotbarSlot(hotbarGlowstoneSlot, "auto-cover");
               BlockHitResult aimUntilReady2 = this.aimUntilReady(blockHitResult2, this.getAimedPlacementHit(blockHitResult2));
               if (aimUntilReady2 == null && this.aimWaitTicks <= 12) {
                  return;
               }

               if (aimUntilReady2 != null) {
                  this.clickBlockAndMarkAge(aimUntilReady2, "cover");
                  this.coverPlaceSent = FloorMathUtilsBlock.canInstanceWorld(this.cover);
               }
            }

            this.autoState = this.getChargeOrTotemState();
            this.stepWaitTicks = 0;
            this.aimWaitTicks = 0;
            this.autoComboHit = null;
            break;
         case SEL_CHARGE:
            int selectedSlotLimit = this.findHotbarGlowstoneSlot();
            if (selectedSlotLimit == -1) {
               this.resetAutoCombo();
               return;
            }

            boolean anchorAutoFlag = !FloorMathUtilsBlock.canInstanceWorld(this.anchor);
            boolean of = mc.world.getBlockState(this.anchor).isOf(Blocks.RESPAWN_ANCHOR);
            if (anchorAutoFlag && !of && this.anchorPlaceSent) {
               this.notifyUser("The server refused the anchor \u2014 stopped before it could ghost");
               this.resetAutoCombo();
               return;
            }

            if (!of) {
               if (++this.stepWaitTicks > this.confirmTimeoutSetting.getValue()) {
                  this.resetAutoCombo();
               }

               return;
            }

            if (!this.isHitReachableFromEye(this.autoComboHit)) {
               this.autoComboHit = this.findAnchorClickHit(this.anchor);
            }

            if (this.autoComboHit == null) {
               this.notifyUser("No face of the anchor you can legally click to charge it");
               this.resetAutoCombo();
               return;
            }

            this.selectHotbarSlot(selectedSlotLimit, "auto-charge");
            this.rotateToward(this.autoComboHit.getPos());
            if (anchorAutoFlag) {
               this.autoState = SafeAnchor.AutoState.CHARGE;
            } else if (++this.stepWaitTicks > this.confirmTimeoutSetting.getValue()) {
               this.resetAutoCombo();
            }
            break;
         case CHARGE:
            int hotbarGlowstoneSlot4 = this.findHotbarGlowstoneSlot();
            if (hotbarGlowstoneSlot4 == -1) {
               this.resetAutoCombo();
               return;
            }

            BlockHitResult autoAnchor = this.isHitReachableFromEye(this.autoComboHit) ? this.autoComboHit : this.findAnchorClickHit(this.anchor);
            if (autoAnchor == null) {
               this.resetAutoCombo();
               return;
            }

            this.selectHotbarSlot(hotbarGlowstoneSlot4, "auto-charge");
            BlockHitResult aimUntilReady3 = this.aimUntilReady(autoAnchor, this.getClickableAimHit(this.anchor, false));
            if (aimUntilReady3 == null) {
               this.abortIfAimStalled(true);
               return;
            }

            this.clickBlockAndMarkAge(aimUntilReady3, "charge");
            this.autoComboHit = autoAnchor;
            this.autoState = SafeAnchor.AutoState.SEL_TOTEM;
            this.stepWaitTicks = 0;
            break;
         case SEL_TOTEM:
            if (!this.isHitReachableFromEye(this.autoComboHit)) {
               this.autoComboHit = this.findAnchorClickHit(this.anchor);
            }

            if (this.autoComboHit == null) {
               this.resetAutoCombo();
               return;
            }

            this.rotateToward(this.autoComboHit.getPos());
            int totemAutoValue = this.findTotemSlot();
            if (totemAutoValue == -1) {
               if (++this.stepWaitTicks <= this.confirmTimeoutSetting.getValue()) {
                  return;
               }

               if (!mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING) || AutoInventoryTotem.isSlotInPendingSwap(40)) {
                  this.notifyUser("No totem in hand \u2014 detonation refused");
                  this.resetAutoCombo();
                  return;
               }

               totemAutoValue = this.totemSlot.getValue() - 1;
               if (mc.player.getInventory().getStack(totemAutoValue).isOf(Items.GLOWSTONE)) {
                  totemAutoValue = this.findDetonateItemSlot();
               }

               if (totemAutoValue == -1) {
                  this.notifyUser("Glowstone in the Totem Slot would charge the anchor instead of detonating it");
                  this.resetAutoCombo();
                  return;
               }
            }

            this.autoTotemSlot = totemAutoValue;
            this.selectHotbarSlot(totemAutoValue, "auto-totem");
            if (FloorMathUtilsBlock.canInstanceWorld(this.anchor) || this.cover != null && FloorMathUtilsBlock.canInstanceWorld(this.cover)) {
               if (++this.stepWaitTicks > this.confirmTimeoutSetting.getValue()) {
                  this.notifyUser("No answer from the server for the charge \u2014 stopped (raise Confirm Timeout on high ping)");
                  this.resetAutoCombo();
               }

               return;
            }

            if (this.getAnchorCharges() <= 0) {
               boolean of2 = mc.world.getBlockState(this.anchor).isOf(Blocks.RESPAWN_ANCHOR);
               if (!this.chargeRetried && of2) {
                  this.chargeRetried = true;
                  this.autoState = SafeAnchor.AutoState.SEL_CHARGE;
                  this.stepWaitTicks = 0;
                  this.autoComboHit = null;
               } else {
                  if (of2) {
                     this.notifyUser("The server refused the anchor charge 2 times \u2014 stopped, no more clicks on it");
                  }

                  this.resetAutoCombo();
               }

               return;
            }

            this.anchorCharged = true;
            if (this.coverPlaceSent && !this.coverRetried && !mc.world.getBlockState(this.cover).isOf(Blocks.GLOWSTONE)) {
               this.coverRetried = true;
               this.coverPlaceSent = false;
               this.cover = this.findCoverPos(this.anchor);
               if (this.cover != null) {
                  this.autoState = SafeAnchor.AutoState.SEL_COVER;
                  this.stepWaitTicks = 0;
                  this.autoComboHit = null;
                  return;
               }
            }

            this.autoState = SafeAnchor.AutoState.DETONATE;
            break;
         case DETONATE:
            if (this.getAnchorCharges() == 0) {
               this.resetAutoCombo();
               return;
            }

            if (this.lootSafety.isEnabled() && this.hasLootNear(this.anchor)) {
               this.notifyUser("Loot nearby \u2014 detonation refused");
               this.resetAutoCombo();
               return;
            }

            boolean anchorAutoFlag2 = this.autoTotemSlot >= 0 && mc.player.getInventory().getStack(this.autoTotemSlot).isOf(Items.TOTEM_OF_UNDYING);
            if (!anchorAutoFlag2 && !mc.player.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING)) {
               this.autoState = SafeAnchor.AutoState.SEL_TOTEM;
               this.stepWaitTicks = 0;
               return;
            }

            BlockHitResult blockHitResult3 = this.isHitReachableFromEye(this.autoComboHit) ? this.autoComboHit : this.findAnchorClickHit(this.anchor);
            if (blockHitResult3 == null) {
               this.resetAutoCombo();
               return;
            }

            this.selectHotbarSlot(this.autoTotemSlot, "auto-totem");
            BlockHitResult aimUntilReady4 = this.aimUntilReady(blockHitResult3, this.getClickableAimHit(this.anchor, true));
            if (aimUntilReady4 == null) {
               this.abortIfAimStalled(true);
               return;
            }

            this.clickBlockAndMarkAge(aimUntilReady4, "detonate");
            this.resetAutoCombo();
      }
   }

   public SafeAnchor() {
      super("Safe Anchor", IconsPngType.COMBAT);
      this.swapBackToAnchor = new BooleanSetting(
         "Swap Back To Anchor",
         "After a detonation, restore your held slot to the respawn anchor (if one is in your hotbar) instead of the item you had before, so you can place the next anchor without pressing the slot key yourself. Off = restore your original slot.",
         true
      );
      this.confirmTimeoutSetting = new IntegerSetting(
         "Confirm Timeout",
         "Max ticks to wait for the server to confirm a placed block before retry/abort (raise for high ping). Nothing is charged or detonated on a block the server hasn't confirmed.",
         10,
         2,
         40
      );
      this.autoAnchorMode = new BooleanSetting(
         "Auto Anchor",
         "Run the WHOLE anchor combo on a single Trigger Key press instead of shielding an already-charged anchor with right-click: place a respawn anchor on the block you're looking at, charge it with glowstone, drop a glowstone cover between you and the blast, swap to your Totem Slot and detonate \u2014 all automatically. The press latches, so it finishes even if you let go of the key; presses while a combo is still running are ignored (one press = one anchor).",
         false
      );
      this.triggerKey = new KeybindSetting(
         "Trigger Key",
         "Auto Anchor only: press this key or mouse button to fire the full place \u2192 charge \u2192 cover \u2192 totem \u2192 detonate combo on the block under your crosshair. Default V.",
         86
      );
      this.totemSlot = new IntegerSetting(
         "Totem Slot",
         "Auto Anchor only: hotbar slot (1-9) to switch to for the detonation \u2014 keep a totem here so the blast can't kill you. The combo never detonates without a totem in hand: if this slot is empty (the last blast popped it) it uses any other hotbar totem, or waits a few ticks for Auto Inventory Totem's refill. Keep it equal to that module's Reserve Slot so the refill lands here.",
         9,
         1,
         9
      );
      this.placeCover = new BooleanSetting(
         "Place Cover",
         "Auto Anchor only: after charging, drop a glowstone block between you and the anchor to soak the blast (needs a 2nd glowstone beyond the one used to charge; skipped if there's no room).",
         true
      );
      this.lootSafety = new BooleanSetting(
         "Loot Safety",
         "Auto Anchor only: refuse to detonate when dropped armour, a sword, or TNT is lying within 10 blocks of the anchor, so the blast can't destroy valuable loot.",
         true
      );
      this.fastCombo = new BooleanSetting(
         "Fast Combo",
         "Auto Anchor only: do each step's slot swap, aim and click on the SAME tick \u2014 the anchor explodes 3 ticks after the press (4 with Place Cover) plus your ping instead of 6 (8): the charge waits for the server to confirm the anchor and the detonation waits for it to confirm the charge, so a refused block stops the combo instead of leaving a ghost. Every click waits until the look the server already has really points at the block face it clicks (turns are capped per tick), so a big turn adds a tick or two. Packet order stays vanilla (the slot change is sent right before the click, like pressing a number key and right-clicking together) and the rotation on the movement packet after each click looks at the clicked block. Turn OFF if you ever see RotationPlace flags: the slower sequence also aims a full tick ahead of every click.",
         true
      );
      this.glowstoneTimeout = new GlowstoneTimeout();
      this.shieldGlowstoneSlot = -1;
      this.detonateItemSlot = -1;
      this.originalSlot = -1;
      this.previousShieldState = GlowstoneTimeout.State.IDLE;
      this.lastProcessedAgeTicks = Integer.MIN_VALUE;
      this.lastPlayerAgeTicks = Integer.MIN_VALUE;
      this.lastInteractAgeTicks = Integer.MIN_VALUE;
      this.autoState = SafeAnchor.AutoState.IDLE;
      this.autoTotemSlot = -1;
      this.wasTriggerKeyPressed = true;
      this.addSettings(
         new ConfigSetting[]{
            this.autoAnchorMode,
            this.triggerKey,
            this.totemSlot,
            this.placeCover,
            this.lootSafety,
            this.fastCombo,
            this.rotationMode,
            this.macroFallback,
            this.swapBackToAnchor,
            this.confirmTimeoutSetting
         }
      );
   }

   private BlockHitResult clampHitToInteractionRange(BlockHitResult posSide) {
      if (posSide == null) {
         return null;
      } else {
         Vec3d eyePos = mc.player.getEyePos();
         double rangeInteraction = BlockPosUtilsRespawn.getRangeInteraction();
         if (eyePos.squaredDistanceTo(posSide.getPos()) <= rangeInteraction * rangeInteraction) {
            return posSide;
         } else {
            Vec3d clampOffset = clampPointOnFace(posSide.getBlockPos(), posSide.getSide(), eyePos);
            return eyePos.squaredDistanceTo(clampOffset) > rangeInteraction * rangeInteraction ? null : new BlockHitResult(clampOffset, posSide.getSide(), posSide.getBlockPos(), false);
         }
      }
   }

   private boolean isValidShieldPos(BlockPos pos, BlockPos pos2) {
      return !isPlayerBlocking(pos2) && this.findPlacementHit(pos2, pos) != null && this.hasUnobstructedFaceTo(pos, pos2);
   }

   private int getAnchorCharges() {
      BlockState blockState = mc.world.getBlockState(this.anchor);
      return blockState.isOf(Blocks.RESPAWN_ANCHOR) ? blockState.get(RespawnAnchorBlock.CHARGES) : 0;
   }

   private void reportShieldFailure(String glowstoneRefusedText) {
      if ("NO_GLOWSTONE".equals(glowstoneRefusedText)) {
         this.notifyUser("No glowstone in hotbar");
      } else if ("AIM_TIMEOUT".equals(glowstoneRefusedText)) {
         this.notifyUser(this.describeAimFailure(this.detonatePhaseActive));
      } else if ("GLOWSTONE_REFUSED".equals(glowstoneRefusedText)) {
         this.notifyUser("Server refused the shield glowstone twice \u2014 it would have been a ghost block");
      }
   }

   private BlockHitResult findPlacementHit(BlockPos pos, BlockPos pos2) {
      if (this.isReplaceableAir(pos) && this.hasSolidOutline(pos)) {
         return this.findBestFaceClick(pos);
      } else {
         Vec3d eyePos = mc.player.getEyePos();
         Vec3d rotationVec = mc.player.getRotationVec(1.0F);
         BlockHitResult posSide = this.clampHitToInteractionRange(
            BlockPosUtilsRespawn.getOffsetManhattan(pos, pos2, eyePos, rotationVec, BlockPosUtilsRespawn.getRangeInteraction(), RANDOM)
         );
         if (!this.isPlacementHitValid(posSide)) {
            posSide = this.clampHitToInteractionRange(BlockPosUtilsRespawn.getOffsetManhattan(pos, pos2, eyePos, rotationVec, 5.0, RANDOM));
         }

         if (!this.isPlacementHitValid(posSide)) {
            posSide = this.findBestSupportClick(pos, pos2);
         }

         return posSide;
      }
   }

   private static double clampInsideFace(double value, double clampValue, double clampValue2, double clampBound) {
      return MathHelper.clamp(value, Math.min(clampValue + 0.1, clampBound), Math.max(clampValue2 - 0.1, clampBound));
   }

   private SafeAnchor.AutoState getChargeOrTotemState() {
      return this.anchorCharged ? SafeAnchor.AutoState.SEL_TOTEM : SafeAnchor.AutoState.SEL_CHARGE;
   }

   private static BlockPos getCoverPosByIndex(BlockPos pos, int blockPosValue) {
      return BlockPosUtilsRespawn.getBlockPos(pos, blockPosValue);
   }

   private String describeAimFailure(boolean rotationCrouchingFlag) {
      if (rotationCrouchingFlag && mc.player != null && mc.player.isSneaking()) {
         return "Stop crouching \u2014 a crouched click can't charge or detonate an anchor";
      } else {
         return "Off".equals(this.rotationMode.getCurrentOption()) && !this.autoAnchorMode.isEnabled()
            ? "Rotation is Off \u2014 it only clicks when your own crosshair is on the block"
            : "Couldn't turn to the block in time \u2014 another module is holding the rotation";
      }
   }

   public boolean isBusyForMacro(BlockPos pos2) {
      return this.isEnabled() && this.isShieldOrComboRunning();
   }

   private void clickBlockAndMarkAge(BlockHitResult blockHitResult, String ageText) {
      this.rotateToward(blockHitResult.getPos());
      this.selfInteracting = true;

      try {
         mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, blockHitResult);
      } finally {
         this.selfInteracting = false;
      }

      this.lastInteractAgeTicks = mc.player.age;
      this.clickedThisTick = true;
   }

   private void advanceAutoCombo() {
      SafeAnchor.AutoState autoState = this.autoState;
      this.tickAutoCombo();
      if (this.fastCombo.isEnabled() && !this.clickedThisTick && this.autoState != autoState && isClickingState(this.autoState)) {
         this.tickAutoCombo();
      }
   }

   private BlockHitResult getClickableAimHit(BlockPos pos, boolean blockHitFlag) {
      return this.canClickThisTick() && !FloorMathUtilsBlock.canInstanceWorld(pos) && !this.isDetonateClickBlocked(pos, blockHitFlag) ? this.getAimedHitAt(pos) : null;
   }

   @Override
   protected void onPacketReceive(AutoVanish autoVanish) {
   }

   public boolean isMacroFallbackPos(BlockPos pos2) {
      return this.macroFallback.isEnabled() && pos2 != null && pos2.equals(this.macroFallbackPos);
   }

   private void tickAutoAnchorMode() {
      if (this.glowstoneTimeout.getProtectionDetonateType() != GlowstoneTimeout.State.IDLE) {
         this.glowstoneTimeout.updateTimeoutAim();
         this.resetShieldState();
         this.previousShieldState = GlowstoneTimeout.State.IDLE;
      }

      if (mc.currentScreen != null) {
         this.resetAutoCombo();
      } else {
         this.clickedThisTick = false;
         boolean explodeSpawnFlag = this.isTriggerKeyPressed();
         boolean anchorExplodeFlag = explodeSpawnFlag && !this.wasTriggerKeyPressed;
         this.wasTriggerKeyPressed = explodeSpawnFlag;
         if (this.autoState != SafeAnchor.AutoState.IDLE) {
            this.advanceAutoCombo();
         } else if (anchorExplodeFlag) {
            if (!AutoInventoryTotem.isHandSwapSlotActive()) {
               if (!BlockPosUtilsRespawn.isRegistryWorld()) {
                  this.notifyUser("Respawn anchors don't explode in the Nether \u2014 the click would only set your spawn");
               } else if (this.findHotbarAnchorSlot() == -1 || this.findHotbarGlowstoneSlot() == -1) {
                  this.notifyUser("Need a respawn anchor + glowstone in the hotbar");
               } else if (this.trySelectAnchorTarget()) {
                  if (this.autoComboHit == null) {
                     this.notifyUser("No block face you can legally click to place the anchor there");
                     this.resetAutoCombo();
                  } else {
                     this.selectHotbarSlot(this.findHotbarAnchorSlot(), "auto-anchor");
                     this.rotateToward(this.autoComboHit.getPos());
                     this.autoState = SafeAnchor.AutoState.PLACE_ANCHOR;
                     this.stepWaitTicks = 0;
                     if (this.fastCombo.isEnabled()) {
                        this.tickAutoCombo();
                     }
                  }
               }
            }
         }
      }
   }

   private boolean isPlacementHitValid(BlockHitResult blockHitResult) {
      if (blockHitResult == null) {
         return false;
      } else {
         return this.isReplaceableAir(blockHitResult.getBlockPos())
            ? this.hasSolidOutline(blockHitResult.getBlockPos()) && this.isHitReachableFromEye(blockHitResult)
            : (BlockPosUtilsRespawn.canPosBlock(blockHitResult, mc.player.getEyePos(), 5.0) || this.isFaceFacingEye(blockHitResult))
               && this.isClickableSupportBlock(blockHitResult.getBlockPos())
               && this.isHitClickable(blockHitResult);
      }
   }

   private Vec3d getLookDirection() {
      return MathClampUtils.getCosSin(MathClampUtils.getYaw(), MathClampUtils.getClamp());
   }

   public boolean isShieldOrComboRunning() {
      return this.isEnabled()
         && (this.glowstoneTimeout.getProtectionDetonateType() != GlowstoneTimeout.State.IDLE || this.autoState != SafeAnchor.AutoState.IDLE);
   }

   private boolean isUsingFoodOrShield() {
      boolean handStackFlag = mc.player.getMainHandStack().getItem().getComponents().contains(DataComponentTypes.FOOD)
         || mc.player.getOffHandStack().getItem().getComponents().contains(DataComponentTypes.FOOD);
      boolean handStackFlag2 = mc.player.getMainHandStack().getItem() instanceof ShieldItem
         || mc.player.getOffHandStack().getItem() instanceof ShieldItem;
      return (handStackFlag || handStackFlag2) && this.isUseButtonHeld();
   }

   private BlockHitResult findBestFaceClick(BlockPos pos) {
      Vec3d eyePos = mc.player.getEyePos();
      Vec3d vec3d = this.getLookDirection();
      BlockHitResult productDot = null;
      double productDotValue = -2.0;

      for (Direction direction : Direction.values()) {
         Vec3d minMax = this.getFaceClickPoint(pos, direction, eyePos);
         if (minMax != null) {
            BlockHitResult blockHitResult = new BlockHitResult(minMax, direction, pos, false);
            if (this.isFaceFacingEye(blockHitResult) && this.isHitClickable(blockHitResult)) {
               double dotProduct = minMax.subtract(eyePos).normalize().dotProduct(vec3d);
               if (dotProduct > productDotValue) {
                  productDotValue = dotProduct;
                  productDot = blockHitResult;
               }
            }
         }
      }

      return productDot;
   }

   private void rotateToward(Vec3d vec) {
      if (mc.player != null) {
         Vec3d subtract = this.compensateForPlayerMotion(vec);
         String formatText = this.rotationMode.getCurrentOption();
         boolean booleanValue = this.autoAnchorMode.isEnabled();
         if ("Off".equals(formatText) && booleanValue) {
            formatText = "Silent";
            if (!this.rotationOffNoticeShown) {
               this.rotationOffNoticeShown = true;
               this.notifyUser("Auto Anchor: Rotation=Off runs Silent + move-fix (keep moving) \u2014 Visible if you flag while sprinting");
            }
         }

         switch (formatText) {
            case "Visible":
               if (booleanValue) {
                  MathClampUtils.handleInstanceEye(this, subtract, 90, false, false, true);
               } else {
                  MathClampUtils.handleInstanceEye(this, subtract, 90, false);
               }
               break;
            case "Silent":
               if (booleanValue) {
                  MathClampUtils.handleInstanceEye(this, subtract, 90, true, true, false);
               } else {
                  MathClampUtils.handleInstanceEye(this, subtract, 90, true, false, true);
               }
         }
      }
   }

   private int findHotbarAnchorSlot() {
      PlayerInventory inventory = mc.player.getInventory();

      for (int i = 0; i < 9; i++) {
         if (inventory.getStack(i).isOf(Items.RESPAWN_ANCHOR)) {
            return i;
         }
      }

      return -1;
   }

   private int findTotemSlot() {
      PlayerInventory inventory = mc.player.getInventory();
      int undyingStack = this.totemSlot.getValue() - 1;
      if (inventory.getStack(undyingStack).isOf(Items.TOTEM_OF_UNDYING) && !AutoInventoryTotem.isSlotInPendingSwap(undyingStack)) {
         return undyingStack;
      } else {
         for (int i = 0; i < 9; i++) {
            if (inventory.getStack(i).isOf(Items.TOTEM_OF_UNDYING) && !AutoInventoryTotem.isSlotInPendingSwap(i)) {
               return i;
            }
         }

         return -1;
      }
   }

   private boolean isClickableSupportBlock(BlockPos pos2) {
      if (!this.isReplaceableAir(pos2) && !FloorMathUtilsBlock.canInstanceWorld(pos2)) {
         BlockState blockState = mc.world.getBlockState(pos2);
         return this.getOutlineBox(pos2) == null ? false : mc.player.isSneaking() || !this.isInteractableBlock(blockState, pos2);
      } else {
         return false;
      }
   }

   private static double clampInsideRange(double safeAnchorBound, double safeAnchorValue, double safeAnchorValue2) {
      double min3 = Math.min(0.15, Math.max(0.0, (safeAnchorValue2 - safeAnchorValue) / 2.0 - 0.01));
      return Math.max(safeAnchorValue + min3, Math.min(safeAnchorValue2 - min3, safeAnchorBound));
   }

   private void clickDetonateAnchor() {
      if (this.detonate != null && mc.interactionManager != null && this.detonateAnchorHit != null) {
         this.interactAndRecordAge(this.detonateAnchorHit, "DETONATE");
         this.detonateClicked = true;
      }
   }

   private static boolean isClickingState(SafeAnchor.AutoState autoState) {
      return autoState == SafeAnchor.AutoState.PLACE_ANCHOR
         || autoState == SafeAnchor.AutoState.CHARGE
         || autoState == SafeAnchor.AutoState.PLACE_COVER
         || autoState == SafeAnchor.AutoState.DETONATE;
   }

   private void resetAutoCombo() {
      this.autoState = SafeAnchor.AutoState.IDLE;
      this.anchor = null;
      this.cover = null;
      this.autoComboHit = null;
      this.autoTotemSlot = -1;
      this.stepWaitTicks = 0;
      this.aimWaitTicks = 0;
      this.anchorPlaceSent = false;
      this.coverPlaceSent = false;
      this.anchorCharged = false;
      this.chargeRetried = false;
      this.coverRetried = false;
   }

   private static Vec3d clampPointOnFace(BlockPos pos, Direction direction, Vec3d vec) {
      Vec3d add = Vec3d.ofCenter(pos).add(direction.getOffsetX() * 0.5, direction.getOffsetY() * 0.5, direction.getOffsetZ() * 0.5);
      Axis axis = direction.getAxis();
      return new Vec3d(
         axis == Axis.X ? add.x : MathHelper.clamp(vec.x, add.x - 0.45, add.x + 0.45),
         axis == Axis.Y ? add.y : MathHelper.clamp(vec.y, add.y - 0.45, add.y + 0.45),
         axis == Axis.Z ? add.z : MathHelper.clamp(vec.z, add.z - 0.45, add.z + 0.45)
      );
   }

   private boolean isReplaceableAir(BlockPos pos2) {
      return BlockPosUtilsRespawn.canReplaceableAir(pos2);
   }

   private void trackShieldStateChange() {
      GlowstoneTimeout.State protectionDetonateType = this.glowstoneTimeout.getProtectionDetonateType();
      if (protectionDetonateType == GlowstoneTimeout.State.FAILED && this.previousShieldState != GlowstoneTimeout.State.FAILED) {
         this.reportShieldFailure(this.glowstoneTimeout.getText());
         if (this.detonate != null) {
            this.macroFallbackPos = this.detonate;
         }

         this.glowstoneTimeout.updateTimeoutAim();
         this.resetShieldState();
         this.previousShieldState = GlowstoneTimeout.State.IDLE;
      } else {
         if (protectionDetonateType == GlowstoneTimeout.State.DONE && this.previousShieldState != GlowstoneTimeout.State.DONE && this.detonateClicked) {
            this.resyncAnchorMacroSlot();
         }

         this.previousShieldState = protectionDetonateType;
         if (protectionDetonateType == GlowstoneTimeout.State.IDLE) {
            this.resetShieldState();
         }
      }
   }

   private boolean isHitReachableFromEye(BlockHitResult blockHitResult) {
      return blockHitResult != null && (BlockPosUtilsRespawn.canSidePos(blockHitResult, mc.player.getEyePos(), 5.0) || this.isFaceFacingEye(blockHitResult)) && this.isHitClickable(blockHitResult);
   }

   private boolean isFaceFacingEye(BlockHitResult blockHitResult) {
      if (blockHitResult == null) {
         return false;
      } else {
         Box offset = this.getOutlineBox(blockHitResult.getBlockPos());
         if (offset == null) {
            return false;
         } else {
            Vec3d eyePos = mc.player.getEyePos();

            double posLimit = switch (blockHitResult.getSide()) {
               case DOWN -> offset.minY - eyePos.y;
               case UP -> eyePos.y - offset.maxY;
               case NORTH -> offset.minZ - eyePos.z;
               case SOUTH -> eyePos.z - offset.maxZ;
               case WEST -> offset.minX - eyePos.x;
               case EAST -> eyePos.x - offset.maxX;
            };
            return posLimit >= 0.1;
         }
      }
   }

   private boolean hasSolidOutline(BlockPos pos2) {
      return !mc.world.getBlockState(pos2).isAir() && this.getOutlineBox(pos2) != null;
   }

   private boolean canClickThisTick() {
      return mc.player != null
         && mc.player.age != this.lastPlayerAgeTicks
         && mc.player.age != this.lastInteractAgeTicks
         && AnchorMacroUtils.canInteractAt(ClientThreadUtils.getTime());
   }

   private boolean isCoverPlaceable() {
      return this.cover != null && this.isReplaceableAir(this.cover) && this.canPlaceGlowstoneAt(this.cover) && !isPlayerBlocking(this.cover);
   }

   @Override
   public void onEnable() {
      this.resetAllState();
   }

   private boolean hasUnobstructedFaceTo(BlockPos pos, BlockPos pos2) {
      Vec3d eyePos = mc.player.getEyePos();
      Box box = new Box(pos2);
      double rangeInteraction = BlockPosUtilsRespawn.getRangeInteraction();

      for (Direction direction : Direction.values()) {
         if (!pos.offset(direction).equals(pos2)) {
            Vec3d add = Vec3d.ofCenter(pos).add(direction.getOffsetX() * 0.5, direction.getOffsetY() * 0.5, direction.getOffsetZ() * 0.5);
            double offsetRaycastValue = (eyePos.x - add.x) * direction.getOffsetX() + (eyePos.y - add.y) * direction.getOffsetY() + (eyePos.z - add.z) * direction.getOffsetZ();
            if (!(offsetRaycastValue < 0.1)) {
               Vec3d vec3d = eyePos.squaredDistanceTo(add) <= rangeInteraction * rangeInteraction ? add : clampPointOnFace(pos, direction, eyePos);
               if (!(eyePos.squaredDistanceTo(vec3d) > rangeInteraction * rangeInteraction) && !box.raycast(eyePos, vec3d).isPresent()) {
                  BlockHitResult raycast = mc.world
                     .raycast(new RaycastContext(eyePos, vec3d, ShapeType.OUTLINE, FluidHandling.NONE, mc.player));
                  if (raycast == null || raycast.getType() == Type.MISS || raycast.getBlockPos().equals(pos)) {
                     return true;
                  }
               }
            }
         }
      }

      return false;
   }

   private void selectDetonateTarget() {
      HitResult crosshairTarget = mc.crosshairTarget;
      if (crosshairTarget != null && crosshairTarget.getType() == Type.BLOCK) {
         BlockPos detonate = ((BlockHitResult)crosshairTarget).getBlockPos();
         BlockState blockState = mc.world.getBlockState(detonate);
         if (blockState.isOf(Blocks.RESPAWN_ANCHOR)) {
            if (blockState.get(RespawnAnchorBlock.CHARGES) != 0) {
               if (!detonate.equals(this.macroFallbackPos)) {
                  if (BlockPosUtilsRespawn.isRegistryWorld()) {
                     this.detonate = detonate;
                     GlowstoneTimeout.CoverPlan defaultBounding = this.planGlowstoneCover(this.detonate, pos -> this.isValidShieldPos(this.detonate, pos));
                     this.shieldBlockPos = defaultBounding.isFlag() ? getCoverPosByIndex(this.detonate, defaultBounding.index()) : null;
                     this.shieldableBlocked = defaultBounding.isShieldableBlocked();
                     this.glowstone = this.shieldBlockPos == null ? null : this.findPlacementHit(this.shieldBlockPos, this.detonate);
                     this.originalSlot = mc.player.getInventory().getSelectedSlot();
                     this.detonateDelayTicks = this.confirmTimeoutSetting.getValue() + RANDOM.nextInt(3);
                  }
               }
            }
         }
      }
   }

   private void resetShieldState() {
      this.detonate = null;
      this.shieldBlockPos = null;
      this.shieldableBlocked = false;
      this.glowstone = null;
      this.detonateAimHit = null;
      this.shieldPlaceHit = null;
      this.detonateAnchorHit = null;
      this.shieldGlowstoneSlot = -1;
      this.detonateItemSlot = -1;
      this.originalSlot = -1;
      this.detonateDelayTicks = 0;
      this.shieldPlaceSent = false;
      this.detonatePhaseActive = false;
      this.detonateClicked = false;
   }

   private boolean isHitInBlockRange(BlockHitResult blockHitResult) {
      double minBlockInteractionRange = Math.min(4.5, mc.player.getBlockInteractionRange());
      return blockHitResult != null && mc.player.getEyePos().squaredDistanceTo(blockHitResult.getPos()) <= minBlockInteractionRange * minBlockInteractionRange;
   }

   private BlockPos findCoverPos(BlockPos pos) {
      GlowstoneTimeout.CoverPlan defaultBounding = this.planGlowstoneCover(pos, anchorx -> this.isValidShieldPos(pos, anchorx));
      return defaultBounding.isFlag() && !defaultBounding.isShieldableBlocked() ? getCoverPosByIndex(pos, defaultBounding.index()) : null;
   }

   private boolean isTriggerKeyPressed() {
      int pressedValue = this.triggerKey.getKeyCode();
      if (pressedValue < 0) {
         return false;
      } else {
         return PageRaltUtils.canFlag(pressedValue)
            ? GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), PageRaltUtils.getValue(pressedValue)) == 1
            : InputUtil.isKeyPressed(mc.getWindow(), pressedValue);
      }
   }

   private static boolean isPlayerBlocking(BlockPos pos) {
      return FloorMathUtilsBlock.canInterpolatorLerped(pos, FloorMathUtilsBlock.getValueInt());
   }

   @Override
   public void onReset() {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         int age = mc.player.age;
         if (age != this.lastProcessedAgeTicks) {
            this.lastProcessedAgeTicks = age;
            if (this.autoAnchorMode.isEnabled()) {
               this.tickAutoAnchorMode();
            } else {
               if (this.autoState != SafeAnchor.AutoState.IDLE) {
                  this.resetAutoCombo();
               }

               if (mc.currentScreen == null && !this.isUsingFoodOrShield()) {
                  boolean handleGlfw = this.isUseButtonHeld();
                  if (!handleGlfw) {
                     this.macroFallbackPos = null;
                  }

                  if (this.glowstoneTimeout.getProtectionDetonateType() == GlowstoneTimeout.State.IDLE && handleGlfw) {
                     this.selectDetonateTarget();
                  }

                  if (this.detonate != null && handleGlfw) {
                     mc.options.useKey.setPressed(false);
                  }

                  this.executeShieldAction(this.glowstoneTimeout.getGlowstoneTimeout(this.buildShieldSignals(handleGlfw)));
                  this.trackShieldStateChange();
               } else {
                  this.executeShieldAction(this.glowstoneTimeout.getGlowstoneTimeout(this.createInactiveSignals()));
                  this.trackShieldStateChange();
               }
            }
         }
      } else {
         this.resetAllState();
      }
   }

   public boolean shouldMacroDefer(BlockPos pos2) {
      return !this.autoAnchorMode.isEnabled() ? !this.isMacroFallbackPos(pos2) : this.autoState != SafeAnchor.AutoState.IDLE || !this.macroFallback.isEnabled();
   }

   private void notifyUser(String safeAnchorText) {
      if (!Notifications.isInstanceBase()) {
         if (mc.player != null) {
            mc.player.sendMessage(Text.literal("\u00a77[Safe Anchor]\u00a7r " + safeAnchorText), false);
         }
      } else {
         DisabledEnabled.getInstance().handleDisabledEnabled("SafeAnchor", safeAnchorText, false);
      }
   }

   private Vec3d compensateForPlayerMotion(Vec3d vec) {
      Vec3d vec3d = new Vec3d(
         mc.player.getX() - mc.player.lastX,
         mc.player.getY() - mc.player.lastY,
         mc.player.getZ() - mc.player.lastZ
      );
      return vec3d.lengthSquared() > 1.0 ? vec : vec.subtract(vec3d);
   }

   private static boolean isDetonateItem(ItemStack itemStack) {
      return BlockPosUtilsRespawn.canRespawnGlowstone(itemStack);
   }

   private static enum AutoState {
      IDLE,
      PLACE_ANCHOR,
      SEL_COVER,
      PLACE_COVER,
      SEL_CHARGE,
      CHARGE,
      SEL_TOTEM,
      DETONATE;
   }
}
