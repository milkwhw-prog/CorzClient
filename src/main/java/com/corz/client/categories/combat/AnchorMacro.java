package com.corz.client.categories.combat;

import com.corz.client.utils.AnchorMacroUtils.Refusals;
import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.entity.ClientThreadUtils;
import com.corz.client.entity.FloorMathUtilsBlock;
import com.corz.client.gui.DisabledEnabled;
import com.corz.client.inventory.SelectedInventoryUtils;
import com.corz.client.world.MathClampUtils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.lwjgl.glfw.GLFW;
import com.corz.client.utils.AnchorMacroUtils;
import com.corz.client.utils.AutoVanish;
import com.corz.client.utils.InventoryPlayer;
import com.corz.client.categories.settings.Notifications;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.categories.combat.SafeAnchor;
import com.corz.client.utils.IconsPngType;
import com.corz.client.utils.WeaknessGodApi;

public class AnchorMacro extends Module implements WeaknessGodApi {
   private final IntegerSetting pearlCooldownSeconds;
   private BlockPos targetAnchorPos;
   private int explodeDelayCounter;
   private int glowstoneDelayCounter;
   private final IntegerSetting totemSlot;
   private int lastInteractAge;
   private final IntegerSetting glowstoneDelay;
   private int pearlStage;
   private final IntegerSetting pearlPitch;
   private final Refusals chargeRefusals;
   private final BooleanSetting swapBackToAnchor;
   private int savedSlotBeforePearl;
   private int unchangedTicks;
   private final BooleanSetting pearlAimDown;
   private final BooleanSetting anchorPearl;
   private long lastPearlThrowMs;
   private int lastChargeLevel;
   private final IntegerSetting switchDelay = new IntegerSetting("Switch Delay", "Delay before switching items (ticks)", 0, 0, 20);
   private boolean swapBackPending;
   private final IntegerSetting explodeDelay;
   private int switchDelayCounter;

   @Override
   protected void onPacketSend(InventoryPlayer inventoryPlayer) {
      if (mc.player != null && mc.isOnThread()) {
         if (inventoryPlayer.packet instanceof PlayerInteractBlockC2SPacket && !inventoryPlayer.isOpenedByClient()) {
            AnchorMacroUtils.recordInteractTime(ClientThreadUtils.getTime());
         }
      }
   }

   private void sendStatusMessage(String macroAnchorText) {
      if (Notifications.isInstanceBase()) {
         DisabledEnabled.getInstance().handleDisabledEnabled("AnchorMacro", macroAnchorText, false);
      } else if (mc.player != null) {
         mc.player.sendMessage(Text.literal("\u00a77[Anchor Macro]\u00a7r " + macroAnchorText), false);
      }
   }

   private void trackAnchorState(BlockPos blockPos, int blockState) {
      this.targetAnchorPos = blockPos;
      this.lastChargeLevel = blockState;
      this.unchangedTicks = 0;
   }

   @Override
   protected void onPacketReceive(AutoVanish autoVanish) {
   }

   public AnchorMacro() {
      super("Anchor Macro", IconsPngType.COMBAT);
      this.glowstoneDelay = new IntegerSetting("Glowstone Delay", "Delay before placing glowstone (ticks)", 0, 0, 20);
      this.explodeDelay = new IntegerSetting("Explode Delay", "Delay before exploding anchor (ticks)", 0, 0, 20);
      this.totemSlot = new IntegerSetting("Totem Slot", "Hotbar slot for totem (1-9)", 1, 1, 9);
      this.swapBackToAnchor = new BooleanSetting(
         "Swap Back To Anchor",
         "After the anchor detonates, automatically switch your held slot back to the respawn anchor (if one is in your hotbar) so you can place the next one without pressing the slot key yourself.",
         true
      );
      this.anchorPearl = new BooleanSetting(
         "Anchor Pearl",
         "When the anchor detonates (by this macro or by Safe Anchor), switch to an ender pearl and throw it to reposition to low ground.",
         false
      );
      this.pearlAimDown = new BooleanSetting(
         "Pearl Aim Down",
         "Aim the thrown pearl downward (server-side only, no extra look packet) so you drop to low ground instead of throwing wherever you happen to be looking.",
         true
      );
      this.pearlPitch = new IntegerSetting(
         "Pearl Pitch", "How steep the downward pearl throw is when Pearl Aim Down is on (0 = straight ahead, 90 = straight down).", 80, 0, 90
      );
      this.pearlCooldownSeconds = new IntegerSetting(
         "Pearl Cooldown", "Minimum seconds between Anchor Pearl throws, so it doesn't pearl on every detonation (0 = every time).", 10, 0, 60
      );
      this.lastChargeLevel = -1;
      this.savedSlotBeforePearl = -1;
      this.lastInteractAge = Integer.MIN_VALUE;
      this.chargeRefusals = new Refusals();
      this.addSettings(
         new ConfigSetting[]{
            this.switchDelay,
            this.glowstoneDelay,
            this.explodeDelay,
            this.totemSlot,
            this.swapBackToAnchor,
            this.anchorPearl,
            this.pearlAimDown,
            this.pearlPitch,
            this.pearlCooldownSeconds
         }
      );
   }

   @Override
   public void onDisable() {
      this.resetAll();
   }

   @Override
   public void onReset() {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         this.tickPearlSequence();
         this.swapBackToAnchorSlot();
         if (!this.isRightMouseHeld()) {
            this.chargeRefusals.reset();
         }

         if (mc.currentScreen == null && !this.isUsingFoodOrShield() && this.isRightMouseHeld()) {
            this.tickAnchorInteraction();
         } else {
            this.clearTrackedAnchor();
         }
      } else {
         this.clearTrackedAnchor();
         this.resetPearlStage();
         this.swapBackPending = false;
      }
   }

   private boolean tryDetonateAnchor(BlockPos pos2) {
      int selectedSlot = this.totemSlot.getValue() - 1;
      if (mc.player.getInventory().getSelectedSlot() != selectedSlot) {
         if (this.switchDelayCounter < this.switchDelay.getValue()) {
            this.switchDelayCounter++;
            return false;
         }

         this.switchDelayCounter = 0;
         mc.player.getInventory().setSelectedSlot(selectedSlot);
      }

      if (this.explodeDelayCounter < this.explodeDelay.getValue()) {
         this.explodeDelayCounter++;
         return false;
      } else {
         boolean detonateIntegerFlag = mc.player.getMainHandStack().isOf(Items.GLOWSTONE) || mc.player.getOffHandStack().isOf(Items.GLOWSTONE);
         if (detonateIntegerFlag && mc.world.getBlockState(pos2).get(RespawnAnchorBlock.CHARGES) < 4) {
            return false;
         } else if (this.canInteractNow(pos2) && this.tryInteractAnchor(pos2, "detonate")) {
            this.explodeDelayCounter = 0;
            return true;
         } else {
            return false;
         }
      }
   }

   private void resetPearlStage() {
      this.pearlStage = 0;
      this.savedSlotBeforePearl = -1;
   }

   private void clearTrackedAnchor() {
      this.targetAnchorPos = null;
      this.lastChargeLevel = -1;
      this.unchangedTicks = 0;
   }

   private boolean isUsingFoodOrShield() {
      boolean handStackFlag = mc.player.getMainHandStack().getItem().getComponents().contains(DataComponentTypes.FOOD)
         || mc.player.getOffHandStack().getItem().getComponents().contains(DataComponentTypes.FOOD);
      boolean handStackFlag2 = mc.player.getMainHandStack().getItem() instanceof ShieldItem
         || mc.player.getOffHandStack().getItem() instanceof ShieldItem;
      return (handStackFlag || handStackFlag2) && this.isRightMouseHeld();
   }

   private void tickAnchorInteraction() {
      BlockPos blockPos = this.getLookedAtAnchor();
      if (blockPos != null) {
         BlockState blockState = mc.world.getBlockState(blockPos);
         mc.options.useKey.setPressed(false);
         if (this.isSafeAnchorOwning(blockPos)) {
            this.clearTrackedAnchor();
         } else {
            int blockValue = blockState.get(RespawnAnchorBlock.CHARGES);
            boolean instanceWorld = FloorMathUtilsBlock.canInstanceWorld(blockPos);
            if (this.targetAnchorPos != null) {
               boolean blockFlag = instanceWorld || this.lastChargeLevel > 0 && blockValue == this.lastChargeLevel;
               if (this.targetAnchorPos.equals(blockPos) && blockFlag && this.unchangedTicks < 10) {
                  this.unchangedTicks++;
                  return;
               }

               this.checkChargeRefusal(blockPos, blockValue, instanceWorld);
            }

            if (!instanceWorld) {
               if (!this.chargeRefusals.isRefusalLimitReached(blockPos)) {
                  if (blockValue == 0) {
                     if (this.tryChargeAnchor(blockPos)) {
                        this.trackAnchorState(blockPos, blockValue);
                     }
                  } else {
                     if (this.isSafeAnchorAutoHandling(blockPos)) {
                        return;
                     }

                     if (this.tryDetonateAnchor(blockPos)) {
                        this.trackAnchorState(blockPos, blockValue);
                        this.swapBackPending = true;
                        this.startPearlSequence();
                     }
                  }
               }
            }
         }
      }
   }

   private void checkChargeRefusal(BlockPos pos2, int letClickingLimit, boolean letClickingFlag) {
      if (this.targetAnchorPos.equals(pos2) && this.lastChargeLevel == 0 && !letClickingFlag) {
         if (letClickingLimit > 0) {
            this.chargeRefusals.resetIfSamePos(pos2);
         } else {
            boolean immutableFlag = this.chargeRefusals.recordRefusal(pos2);
            if (immutableFlag) {
               this.sendStatusMessage("The server refused the anchor charge 2 times \u2014 stopped clicking this anchor (let go of right-click to try again)");
            }
         }
      } else if (letClickingFlag) {
      }

      this.clearTrackedAnchor();
   }

   @Override
   public void onEnable() {
      this.resetAll();
   }

   private BlockPos getLookedAtAnchor() {
      if (!this.isEnabled() || mc.player == null || mc.world == null || mc.currentScreen != null) {
         return null;
      } else if (this.isRightMouseHeld() && !this.isUsingFoodOrShield()) {
         if (mc.crosshairTarget instanceof BlockHitResult blockHitResult && blockHitResult.getType() == Type.BLOCK) {
            return mc.world.getBlockState(blockHitResult.getBlockPos()).isOf(Blocks.RESPAWN_ANCHOR) ? blockHitResult.getBlockPos() : null;
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private boolean tryInteractAnchor(BlockPos pos, String ageInteractText) {
      BlockHitResult posBlock = this.getAnchorHitResult(pos);
      if (posBlock == null) {
         return false;
      } else {
         mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, posBlock);
         this.lastInteractAge = mc.player.age;
         AnchorMacroUtils.recordInteractTime(ClientThreadUtils.getTime());
         return true;
      }
   }

   private void resetAll() {
      this.switchDelayCounter = 0;
      this.glowstoneDelayCounter = 0;
      this.explodeDelayCounter = 0;
      this.swapBackPending = false;
      this.lastInteractAge = Integer.MIN_VALUE;
      this.chargeRefusals.reset();
      this.clearTrackedAnchor();
      this.resetPearlStage();
   }

   private boolean isRightMouseHeld() {
      return GLFW.glfwGetMouseButton(mc.getWindow().getHandle(), 1) == 1;
   }

   private boolean isSafeAnchorAutoHandling(BlockPos pos2) {
      return ModuleRegistry.getModule(SafeAnchor.class) instanceof SafeAnchor safeAnchor && safeAnchor.isEnabled() && safeAnchor.shouldMacroDefer(pos2);
   }

   private void applyPearlAim() {
      if (this.pearlAimDown.isEnabled() && mc.player != null) {
         MathClampUtils.handle(this, mc.player.getYaw(), this.pearlPitch.getValue(), 90, true);
      }
   }

   private void swapBackToAnchorSlot() {
      if (this.swapBackPending && mc.player != null) {
         if (this.pearlStage == 0) {
            if (mc.currentScreen == null) {
               this.swapBackPending = false;
               if (this.swapBackToAnchor.isEnabled()) {
                  int selectedSlot = SelectedInventoryUtils.getValue(Items.RESPAWN_ANCHOR);
                  if (selectedSlot != -1) {
                     mc.player.getInventory().setSelectedSlot(selectedSlot);
                  }
               }
            }
         }
      }
   }

   private void tickPearlSequence() {
      if (this.pearlStage != 0) {
         if (mc.currentScreen == null) {
            int selectedSlot = SelectedInventoryUtils.getValue(Items.ENDER_PEARL);
            if (selectedSlot == -1) {
               this.resetPearlStage();
            } else {
               mc.options.useKey.setPressed(false);
               switch (this.pearlStage) {
                  case 1:
                     mc.player.getInventory().setSelectedSlot(selectedSlot);
                     this.applyPearlAim();
                     this.pearlStage = 2;
                     break;
                  case 2:
                     this.applyPearlAim();
                     float pitch = mc.player.getPitch();
                     if (this.pearlAimDown.isEnabled()) {
                        mc.player.setPitch(this.pearlPitch.getValue());
                     }

                     mc.interactionManager.interactItem(mc.player, Hand.MAIN_HAND);
                     mc.player.setPitch(pitch);
                     this.lastPearlThrowMs = System.currentTimeMillis();
                     this.pearlStage = 3;
                     break;
                  default:
                     if (this.savedSlotBeforePearl >= 0 && this.savedSlotBeforePearl <= 8) {
                        mc.player.getInventory().setSelectedSlot(this.savedSlotBeforePearl);
                     }

                     this.resetPearlStage();
               }
            }
         }
      }
   }

   public void startPearlSequence() {
      if (this.anchorPearl.isEnabled() && this.pearlStage == 0 && mc.player != null) {
         if (System.currentTimeMillis() - this.lastPearlThrowMs >= this.pearlCooldownSeconds.getValue() * 1000L) {
            if (SelectedInventoryUtils.getValue(Items.ENDER_PEARL) != -1) {
               this.savedSlotBeforePearl = mc.player.getInventory().getSelectedSlot();
               this.pearlStage = 1;
            }
         }
      }
   }

   @Override
   public boolean isAgePlayer() {
      return this.getLookedAtAnchor() != null || mc.player != null && mc.player.age == this.lastInteractAge;
   }

   private boolean tryChargeAnchor(BlockPos pos2) {
      if (!mc.player.getMainHandStack().isOf(Items.GLOWSTONE)) {
         if (this.switchDelayCounter < this.switchDelay.getValue()) {
            this.switchDelayCounter++;
            return false;
         }

         this.switchDelayCounter = 0;
         int selectedSlot = SelectedInventoryUtils.getValue(Items.GLOWSTONE);
         if (selectedSlot == -1) {
            return false;
         }

         mc.player.getInventory().setSelectedSlot(selectedSlot);
      }

      if (this.glowstoneDelayCounter < this.glowstoneDelay.getValue()) {
         this.glowstoneDelayCounter++;
         return false;
      } else if (mc.player.getMainHandStack().isOf(Items.GLOWSTONE) && this.canInteractNow(pos2) && this.tryInteractAnchor(pos2, "charge")) {
         this.glowstoneDelayCounter = 0;
         return true;
      } else {
         return false;
      }
   }

   private boolean canInteractNow(BlockPos pos) {
      return !AnchorMacroUtils.canInteractAt(ClientThreadUtils.getTime())
         ? false
         : !mc.player.shouldCancelInteraction()
            || mc.player.getMainHandStack().isEmpty() && mc.player.getOffHandStack().isEmpty();
   }

   private boolean isSafeAnchorOwning(BlockPos pos2) {
      return ModuleRegistry.getModule(SafeAnchor.class) instanceof SafeAnchor safeAnchor && safeAnchor.isBusyForMacro(pos2);
   }

   private BlockHitResult getAnchorHitResult(BlockPos pos) {
      Vec3d eyePos = mc.player.getEyePos();
      double minBlockInteractionRange = Math.min(4.5, mc.player.getBlockInteractionRange());
      Vec3d add = eyePos.add(MathClampUtils.getCosSin(MathClampUtils.getYaw(), MathClampUtils.getClamp()).multiply(minBlockInteractionRange));
      BlockHitResult raycast = mc.world.raycast(new RaycastContext(eyePos, add, ShapeType.OUTLINE, FluidHandling.NONE, mc.player));
      if (raycast != null && raycast.getType() == Type.BLOCK && raycast.getBlockPos().equals(pos)) {
         return eyePos.squaredDistanceTo(raycast.getPos()) <= minBlockInteractionRange * minBlockInteractionRange ? raycast : null;
      } else {
         return null;
      }
   }

   public boolean isMacroInteracting() {
      return this.isEnabled() && mc.player != null && this.isRightMouseHeld() && !this.isUsingFoodOrShield();
   }
}
