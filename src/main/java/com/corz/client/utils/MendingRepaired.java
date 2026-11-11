package com.corz.client.utils;

import com.corz.client.entity.ClientThreadUtils;
import com.corz.client.inventory.InventoryStackUtilsEmpty;
import com.corz.client.inventory.StackEnchantmentsUtils;
import com.corz.client.world.BlockStateUtilsDirections;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BooleanSupplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResult.Success;
import net.minecraft.util.ActionResult.SwingSource;
import net.minecraft.util.Hand;
import com.corz.client.utils.BotActionType;
import com.corz.client.utils.HotbarSlotSwitcher;
import com.corz.client.utils.HandswapGate;
import com.corz.client.utils.TrackFlick;
import com.corz.client.utils.HotbarKitRestocker;

public final class MendingRepaired {
   private HotbarKitRestocker stackInventory;
   private float xpLookPitch;
   private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
   private long lastXpThrowTick = Long.MIN_VALUE;
   private long lastBottleUseTick = Long.MIN_VALUE;
   private BooleanSupplier safetyOverrideSupplier;
   private long restUntilTick = Long.MIN_VALUE;
   private final TrackFlick trackFlick;
   private final HandswapGate ledgerHandswap;
   private int xpThrowCount;
   private boolean mendingActive;
   private final HotbarSlotSwitcher repaired;

   private void finishMendingSession(long restWornValue, boolean restWornFlag) {
      this.mendingActive = false;
      this.xpThrowCount = 0;
      this.lastXpThrowTick = Long.MIN_VALUE;
      if (restWornFlag) {
         this.restUntilTick = restWornValue + 20L;
      }
   }

   public void cancelMendingSession() {
      if (this.mendingActive) {
         this.finishMendingSession(ClientThreadUtils.getTime(), false);
      }
   }

   boolean isSafeToMend(SelfPopData selfPopData) {
      if (selfPopData.pearlIncoming()) {
         return false;
      } else if (this.safetyOverrideSupplier != null && this.safetyOverrideSupplier.getAsBoolean()) {
         return true;
      } else if (selfPopData.nearestEnemy() >= 12.0) {
         return true;
      } else {
         BlockStateUtilsDirections.Hole hole = selfPopData.self().hole();
         return hole != null && hole.type() != BlockStateUtilsDirections.Type.NONE && hole.openSides() == 0 && selfPopData.nearestEnemy() >= 8.0;
      }
   }

   public void setStackInventory(HotbarKitRestocker stackInventory) {
      this.stackInventory = stackInventory;
   }

   public void resetMendingState() {
      this.mendingActive = false;
      this.lastXpThrowTick = Long.MIN_VALUE;
      this.xpThrowCount = 0;
      this.lastBottleUseTick = Long.MIN_VALUE;
      this.restUntilTick = Long.MIN_VALUE;
   }

   public boolean tryThrowXpBottle() {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      if (player != null && instance.interactionManager != null) {
         long time = ClientThreadUtils.getTime();
         if (time < this.restUntilTick) {
            return false;
         } else {
            if (!this.mendingActive) {
               this.mendingActive = true;
               this.lastXpThrowTick = time;
               this.xpThrowCount = 0;
               this.xpLookPitch = 82.0F + ThreadLocalRandom.current().nextFloat() * 6.0F;
            }

            if (this.xpThrowCount < 32 && time - this.lastXpThrowTick < 40L) {
               int selectedSlot = this.repaired.findSlotMatching(stack -> stack.isOf(Items.EXPERIENCE_BOTTLE), false);
               if (selectedSlot < 0) {
                  this.finishMendingSession(time, true);
                  return false;
               } else {
                  this.trackFlick.requestSmoothedRotation(BotActionType.XP, this.trackFlick.getCurrentYaw(), this.xpLookPitch, TrackFlick.Speed.TRACK, 50);
                  if (!this.repaired.requestSlot(selectedSlot, false)) {
                     return false;
                  } else if (this.lastBottleUseTick != Long.MIN_VALUE && time - this.lastBottleUseTick < 4L) {
                     return false;
                  } else if (this.trackFlick.getCurrentPitch() < 75.0F) {
                     return false;
                  } else if (this.ledgerHandswap.isSwitchAllowed() && this.ledgerHandswap.isRotationSynced()) {
                     ItemStack stack2 = player.getInventory().getStack(selectedSlot);
                     if (stack2.isOf(Items.EXPERIENCE_BOTTLE) && !player.getItemCooldownManager().isCoolingDown(stack2)) {
                        ActionResult actionResult;
                        try (HandswapGate.SwitchHandle handler = this.ledgerHandswap.acquireHandle()) {
                           actionResult = instance.interactionManager.interactItem(player, Hand.MAIN_HAND);
                        }

                        if (actionResult instanceof Success success && success.swingSource() == SwingSource.CLIENT) {
                           player.swingHand(Hand.MAIN_HAND);
                        }

                        this.lastBottleUseTick = time;
                        this.xpThrowCount++;
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     return false;
                  }
               }
            } else {
               this.finishMendingSession(time, true);
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private static double getLowestMendingDurabilityFraction(ClientPlayerEntity player) {
      double naN = Double.NaN;

      for (EquipmentSlot equipmentSlot : ARMOR_SLOTS) {
         ItemStack equippedStack = player.getEquippedStack(equipmentSlot);
         if (!equippedStack.isEmpty() && equippedStack.isDamageable() && InventoryStackUtilsEmpty.getEnchantmentsDefault(equippedStack, Enchantments.MENDING) > 0) {
            double damageDamageable = StackEnchantmentsUtils.getDamageDamageable(equippedStack);
            naN = Double.isNaN(naN) ? damageDamageable : Math.min(naN, damageDamageable);
         }
      }

      return naN;
   }

   public CaseMaxData evaluateMendingOption(SelfPopData selfPopData) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      if (player != null && selfPopData != null && selfPopData.self() != null) {
         long mendingWornValue = ClientThreadUtils.getTime();
         if (!this.isSafeToMend(selfPopData)) {
            if (this.mendingActive) {
               this.finishMendingSession(mendingWornValue, false);
            }

            return CaseMaxData.getCaseMaxData(SurroundKitType.XP, "enemy near");
         } else if (selfPopData.self().usingItem()) {
            return CaseMaxData.getCaseMaxData(SurroundKitType.XP, "eating");
         } else if (mendingWornValue < this.restUntilTick) {
            return CaseMaxData.getCaseMaxData(SurroundKitType.XP, "rest");
         } else {
            double lowestMendingDurabilityFraction = getLowestMendingDurabilityFraction(player);
            if (Double.isNaN(lowestMendingDurabilityFraction)) {
               return CaseMaxData.getCaseMaxData(SurroundKitType.XP, "no mending");
            } else {
               boolean mendingWornFlag = this.mendingActive ? lowestMendingDurabilityFraction < 0.9 : lowestMendingDurabilityFraction < 0.35;
               if (!mendingWornFlag) {
                  if (this.mendingActive) {
                     this.finishMendingSession(mendingWornValue, false);
                  }

                  return CaseMaxData.getCaseMaxData(SurroundKitType.XP, "repaired");
               } else if (this.repaired.findSlotMatching(stack -> stack.isOf(Items.EXPERIENCE_BOTTLE), false) >= 0) {
                  return new CaseMaxData(SurroundKitType.XP, true, 2.0, this.mendingActive ? 0 : 2, false, false, this.mendingActive ? "mending" : "worn");
               } else if (this.stackInventory != null && selfPopData.self().xpBottles() > 0) {
                  this.stackInventory.setKindBlockedUntil(HotbarKitRestocker.Kind.XP, mendingWornValue + 60L);
                  return CaseMaxData.getCaseMaxData(SurroundKitType.XP, "fetch");
               } else {
                  return CaseMaxData.getCaseMaxData(SurroundKitType.XP, "no bottles");
               }
            }
         }
      } else {
         return CaseMaxData.getCaseMaxData(SurroundKitType.XP, "no player");
      }
   }

   public boolean isMendingActive() {
      return this.mendingActive;
   }

   public void setSafetyOverrideSupplier(BooleanSupplier booleanSupplier) {
      this.safetyOverrideSupplier = booleanSupplier;
   }

   public MendingRepaired(TrackFlick trackFlick, HandswapGate ledgerHandswap, HotbarSlotSwitcher repaired) {
      this.xpLookPitch = 85.0F;
      this.trackFlick = trackFlick;
      this.ledgerHandswap = ledgerHandswap;
      this.repaired = repaired;
   }
}
