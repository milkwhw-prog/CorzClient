package com.corz.client.utils;

import com.corz.client.entity.ClientThreadUtils;
import com.corz.client.render.CodeCategoryUtils;
import com.corz.client.world.AbsorptionPosData;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import com.corz.client.utils.BotActionType;
import com.corz.client.utils.HotbarSlotSwitcher;
import com.corz.client.utils.HandswapGate;
import com.corz.client.utils.OutboundPacketKind;
import com.corz.client.utils.TickActionLedger;
import com.corz.client.utils.TrackFlick;
import com.corz.client.utils.HotbarKitRestocker;
import com.corz.client.utils.AbsorptionFoodUtils;

public final class AutoEatController {
   private int holdTickCount;
   private boolean eatingGoldenApple;
   private long blockedUntilTick;
   private HotbarKitRestocker stackInventory;
   private final TrackFlick trackFlick;
   private long gappleCooldownUntilTick;
   private int selectStartTicks;
   private final HandswapGate ledgerHandswap;
   private final HotbarSlotSwitcher inventorySelected;
   private boolean preferRegularFood;
   private boolean gappleAllowed;
   private AutoEatController.EatState eatState = AutoEatController.EatState.IDLE;
   private final TickActionLedger sprintSneak;
   private long lastReleaseTick;
   private boolean useKeyHeld;
   private int eatSlot = -1;

   static long getRemainingCooldownTicks(long cooldownPlayerValue, long cooldownPlayerBound, long cheapHigh) {
      long maxTick = Math.max(cooldownPlayerBound, cheapHigh);
      return maxTick != Long.MIN_VALUE && maxTick > cooldownPlayerValue ? maxTick - cooldownPlayerValue : 0L;
   }

   private boolean requestGappleFetch(ClientPlayerEntity player, long inventoryStackValue) {
      if (this.stackInventory != null && this.inventorySelected.findSlotMatching(AutoEatController::isGoldenApple, false) < 0) {
         for (int i = 9; i <= 35; i++) {
            if (isGoldenApple(player.getInventory().getStack(i))) {
               this.stackInventory.setKindBlockedUntil(HotbarKitRestocker.Kind.GAPPLE, inventoryStackValue + 60L);
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   static boolean isGoldenApple(ItemStack stack) {
      return stack != null && (stack.isOf(Items.GOLDEN_APPLE) || stack.isOf(Items.ENCHANTED_GOLDEN_APPLE));
   }

   static boolean isReleasingWhileHolding(AutoEatController.EatState state, AutoEatController.EatState state2, boolean holdReleaseFlag) {
      return state == AutoEatController.EatState.HOLD && state2 == AutoEatController.EatState.RELEASE && holdReleaseFlag;
   }

   private static CarefulBalancedType getPlaystyle(SelfPopData selfPopData) {
      return selfPopData != null && selfPopData.cfg() != null && selfPopData.cfg().playstyle() != null ? selfPopData.cfg().playstyle() : CarefulBalancedType.TERMINATOR;
   }

   private void releaseUseKey(MinecraftClient client, ClientPlayerEntity player, boolean optionsReleaseFlag) {
      ElytraAutoUtils.handleRawPressed(client.options.useKey, CodeCategoryUtils.isKeyPressed(client.options.useKey));
      this.useKeyHeld = false;
      if (isReleasingWhileHolding(AutoEatController.EatState.HOLD, AutoEatController.EatState.RELEASE, optionsReleaseFlag) && player.isUsingItem()) {
         this.sprintSneak.recordForcedInteract(OutboundPacketKind.RELEASE_USE);
      }
   }

   public AutoEatController.EatState getEatState() {
      return this.eatState;
   }

   private static boolean isEdibleFood(ItemStack stack) {
      if (stack == null || stack.isEmpty()) {
         return false;
      } else if (stack.isOf(Items.GOLDEN_APPLE) || stack.isOf(Items.ENCHANTED_GOLDEN_APPLE)) {
         return false;
      } else {
         return !stack.isOf(Items.CHORUS_FRUIT)
               && !stack.isOf(Items.SUSPICIOUS_STEW)
               && !stack.isOf(Items.POISONOUS_POTATO)
               && !stack.isOf(Items.ROTTEN_FLESH)
               && !stack.isOf(Items.SPIDER_EYE)
               && !stack.isOf(Items.PUFFERFISH)
            ? stack.contains(DataComponentTypes.FOOD)
            : false;
      }
   }

   public boolean isHoldingUse() {
      return this.eatState == AutoEatController.EatState.HOLD || this.useKeyHeld;
   }

   public boolean isEatingFinished() {
      return this.eatState == AutoEatController.EatState.IDLE
         || this.eatState == AutoEatController.EatState.DONE
         || this.eatState == AutoEatController.EatState.ABORT;
   }

   private int findEatSlot(ClientPlayerEntity player, boolean selectedInventoryFlag, boolean selectedInventoryFlag2) {
      if (selectedInventoryFlag) {
         int selectedSlot = this.inventorySelected.findSlotMatching(stack -> isEdibleFood(stack) && player.canConsume(false), false);
         if (selectedSlot >= 0 || !selectedInventoryFlag2) {
            return selectedSlot;
         }
      }

      if (!selectedInventoryFlag2) {
         return -1;
      } else {
         int slotMatching = this.inventorySelected.findSlotMatching(stack2 -> stack2.isOf(Items.ENCHANTED_GOLDEN_APPLE), false);
         int slotMatching3 = this.inventorySelected.findSlotMatching(stack3 -> stack3.isOf(Items.GOLDEN_APPLE), false);
         if (selectedInventoryFlag) {
            return slotMatching3 >= 0 ? slotMatching3 : slotMatching;
         } else {
            return slotMatching >= 0 ? slotMatching : slotMatching3;
         }
      }
   }

   public void reset() {
      this.cancelEating();
      this.eatState = AutoEatController.EatState.IDLE;
      this.eatSlot = -1;
      this.blockedUntilTick = Long.MIN_VALUE;
      this.gappleCooldownUntilTick = Long.MIN_VALUE;
      this.lastReleaseTick = Long.MIN_VALUE;
      this.eatingGoldenApple = false;
      this.gappleAllowed = true;
   }

   public int getEatSlot() {
      return this.eatSlot;
   }

   private static boolean canEatSlot(ClientPlayerEntity player, int appleGoldenLimit) {
      if (appleGoldenLimit >= 0 && appleGoldenLimit < 9) {
         ItemStack stack = player.getInventory().getStack(appleGoldenLimit);
         if (stack.isEmpty()) {
            return false;
         } else {
            return !stack.isOf(Items.GOLDEN_APPLE) && !stack.isOf(Items.ENCHANTED_GOLDEN_APPLE) ? isEdibleFood(stack) && player.canConsume(false) : true;
         }
      } else {
         return false;
      }
   }

   static boolean isHoldState(AutoEatController.EatState state) {
      return state == AutoEatController.EatState.HOLD;
   }

   public boolean beginEating(boolean flag) {
      if (this.eatState != AutoEatController.EatState.SELECT
         && this.eatState != AutoEatController.EatState.START
         && this.eatState != AutoEatController.EatState.HOLD) {
         MinecraftClient instance = MinecraftClient.getInstance();
         ClientPlayerEntity player = instance.player;
         if (player == null) {
            return false;
         } else {
            long abortReleaseValue = ClientThreadUtils.getTime();
            if (abortReleaseValue < this.blockedUntilTick) {
               return false;
            } else {
               boolean gappleAllowed2 = isGappleAllowed(abortReleaseValue, this.gappleCooldownUntilTick, player.getHungerManager().getFoodLevel() <= 6);
               int value = this.findEatSlot(player, flag, gappleAllowed2);
               if (value < 0) {
                  if (gappleAllowed2) {
                     this.requestGappleFetch(player, abortReleaseValue);
                  }

                  return false;
               } else {
                  this.eatSlot = value;
                  this.preferRegularFood = flag;
                  this.gappleAllowed = gappleAllowed2;
                  this.eatingGoldenApple = false;
                  this.holdTickCount = 0;
                  this.selectStartTicks = 0;
                  this.eatState = AutoEatController.EatState.SELECT;
                  return true;
               }
            }
         }
      } else {
         return false;
      }
   }

   public long getCooldownRemaining(long cooldownPlayerValue) {
      return getRemainingCooldownTicks(cooldownPlayerValue, this.blockedUntilTick, this.gappleCooldownUntilTick);
   }

   private void recordCooldown(boolean cooldownPlayerFlag, long cooldownPlayerValue) {
      long cooldownPlayerBound = AbsorptionFoodUtils.computeNextCheckTick(this.eatingGoldenApple, cooldownPlayerFlag, this.holdTickCount, cooldownPlayerValue);
      if (cooldownPlayerBound > cooldownPlayerValue) {
         if (cooldownPlayerFlag) {
            this.gappleCooldownUntilTick = Math.max(this.gappleCooldownUntilTick, cooldownPlayerBound);
         } else {
            this.blockedUntilTick = Math.max(this.blockedUntilTick, cooldownPlayerBound);
         }
      }
   }

   static AutoEatController.EatState nextState(AutoEatController.EatState state, boolean abortIdleFlag, boolean abortIdleFlag2, boolean usingItem, int abortIdleValue, boolean abortReleaseFlag) {
      if (state == null) {
         return AutoEatController.EatState.IDLE;
      } else {
         return switch (state) {
            case IDLE -> AutoEatController.EatState.IDLE;
            case SELECT -> abortReleaseFlag ? AutoEatController.EatState.ABORT : (abortIdleFlag ? AutoEatController.EatState.START : AutoEatController.EatState.SELECT);
            case START -> abortReleaseFlag
               ? AutoEatController.EatState.ABORT
               : (
                  !abortIdleFlag
                     ? AutoEatController.EatState.SELECT
                     : (!abortIdleFlag2 ? AutoEatController.EatState.START : (usingItem ? AutoEatController.EatState.HOLD : AutoEatController.EatState.ABORT))
               );
            case HOLD -> !abortReleaseFlag && usingItem && abortIdleValue <= 45 ? AutoEatController.EatState.HOLD : AutoEatController.EatState.RELEASE;
            case RELEASE -> AutoEatController.EatState.DONE;
            case DONE, ABORT -> AutoEatController.EatState.IDLE;
         };
      }
   }

   public AutoEatController.EatState tickEating(boolean abortReleaseFlag) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      if (player != null && instance.interactionManager != null && instance.options != null) {
         long time = ClientThreadUtils.getTime();
         switch (this.eatState) {
            case IDLE:
               return AutoEatController.EatState.IDLE;
            case SELECT:
            case START:
            case HOLD:
            default:
               if (this.eatState == AutoEatController.EatState.SELECT) {
                  boolean abortIdleFlag = canEatSlot(player, this.eatSlot) && this.inventorySelected.requestSlot(this.eatSlot, false);
                  if (!canEatSlot(player, this.eatSlot)) {
                     int value = this.findEatSlot(player, this.preferRegularFood, this.gappleAllowed);
                     if (value < 0) {
                        return this.abortEating(time);
                     }

                     this.eatSlot = value;
                     abortIdleFlag = this.inventorySelected.requestSlot(this.eatSlot, false);
                  }

                  if (++this.selectStartTicks > 20) {
                     return this.abortEating(time);
                  }

                  AutoEatController.EatState abortReleaseType = nextState(AutoEatController.EatState.SELECT, abortIdleFlag, false, false, 0, abortReleaseFlag);
                  if (abortReleaseType == AutoEatController.EatState.ABORT) {
                     return this.abortEating(time);
                  }

                  this.eatState = abortReleaseType;
                  if (this.eatState == AutoEatController.EatState.SELECT) {
                     return this.eatState;
                  }
               }

               if (this.eatState == AutoEatController.EatState.START) {
                  if (++this.selectStartTicks > 20) {
                     return this.abortEating(time);
                  } else {
                     boolean abortReleaseFlag3 = this.ledgerHandswap.isSlotSelected(this.eatSlot) && canEatSlot(player, this.eatSlot);
                     boolean abortReleaseFlag4 = abortReleaseFlag3 && !abortReleaseFlag && this.ledgerHandswap.isSwitchBlockerClear() && this.ledgerHandswap.isRotationSynced();
                     boolean abortReleaseFlag5 = false;
                     if (abortReleaseFlag4) {
                        ActionResult actionResult;
                        try (HandswapGate.SwitchHandle handler = this.ledgerHandswap.acquireHandle()) {
                           actionResult = instance.interactionManager.interactItem(player, Hand.MAIN_HAND);
                        }

                        abortReleaseFlag5 = actionResult != null && actionResult.isAccepted() && player.isUsingItem();
                        if (abortReleaseFlag5) {
                           ElytraAutoUtils.handleRawPressed(instance.options.useKey, true);
                           this.useKeyHeld = true;
                           this.holdTickCount = 0;
                           this.eatingGoldenApple = isGoldenApple(player.getInventory().getStack(this.eatSlot));
                           this.trackFlick.requestHold(BotActionType.EAT, 50);
                        }
                     }

                     AutoEatController.EatState nextState2 = nextState(AutoEatController.EatState.START, abortReleaseFlag3, abortReleaseFlag4, abortReleaseFlag5, 0, abortReleaseFlag);
                     if (nextState2 == AutoEatController.EatState.ABORT) {
                        return this.abortEating(time);
                     } else {
                        if (nextState2 == AutoEatController.EatState.SELECT) {
                           this.inventorySelected.requestSlot(this.eatSlot, false);
                        }

                        this.eatState = nextState2;
                        return this.eatState;
                     }
                  }
               } else if (this.eatState == AutoEatController.EatState.HOLD) {
                  boolean usingItem = player.isUsingItem();
                  boolean abortReleaseFlag6 = player.getInventory().getSelectedSlot() != this.eatSlot;
                  this.holdTickCount++;
                  AutoEatController.EatState abortIdle = nextState(AutoEatController.EatState.HOLD, true, true, usingItem, this.holdTickCount, abortReleaseFlag || abortReleaseFlag6);
                  if (abortIdle == AutoEatController.EatState.RELEASE) {
                     this.releaseUseKey(instance, player, usingItem);
                     this.recordCooldown(!usingItem, time);
                     this.lastReleaseTick = time;
                     this.eatState = AutoEatController.EatState.DONE;
                     return AutoEatController.EatState.RELEASE;
                  } else {
                     ElytraAutoUtils.handleRawPressed(instance.options.useKey, true);
                     this.useKeyHeld = true;
                     this.trackFlick.requestHold(BotActionType.EAT, 50);
                     return AutoEatController.EatState.HOLD;
                  }
               } else {
                  return this.eatState;
               }
            case RELEASE:
               this.eatState = AutoEatController.EatState.DONE;
               return AutoEatController.EatState.DONE;
            case DONE:
            case ABORT:
               AutoEatController.EatState eatState3 = this.eatState;
               this.eatState = AutoEatController.EatState.IDLE;
               return eatState3 == AutoEatController.EatState.ABORT ? AutoEatController.EatState.IDLE : AutoEatController.EatState.DONE;
         }
      } else {
         this.cancelEating();
         return this.eatState;
      }
   }

   private AutoEatController.EatState abortEating(long abortOptionsValue) {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (this.useKeyHeld && instance.options != null) {
         ElytraAutoUtils.handleRawPressed(instance.options.useKey, CodeCategoryUtils.isKeyPressed(instance.options.useKey));
      }

      this.useKeyHeld = false;
      this.blockedUntilTick = abortOptionsValue + 40L;
      this.eatState = AutoEatController.EatState.ABORT;
      return AutoEatController.EatState.ABORT;
   }

   public void setStackInventory(HotbarKitRestocker stackInventory) {
      this.stackInventory = stackInventory;
   }

   public CaseMaxData getEatOption(SelfPopData selfPopData) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      if (player == null) {
         return CaseMaxData.getCaseMaxData(SurroundKitType.EAT, "no player");
      } else if (this.eatState != AutoEatController.EatState.SELECT
         && this.eatState != AutoEatController.EatState.START
         && this.eatState != AutoEatController.EatState.HOLD) {
         long eatCooldownValue = ClientThreadUtils.getTime();
         if (eatCooldownValue < this.blockedUntilTick) {
            return CaseMaxData.getCaseMaxData(SurroundKitType.EAT, "cooldown");
         } else {
            AbsorptionPosData absorptionPosData = selfPopData == null ? null : selfPopData.self();
            boolean eatCooldownFlag = absorptionPosData != null && !AbsorptionFoodUtils.shouldEatGappleForHealth(absorptionPosData, getPlaystyle(selfPopData)) && AbsorptionFoodUtils.isHungryWithFoodAvailable(absorptionPosData);
            boolean gappleAllowed2 = isGappleAllowed(eatCooldownValue, this.gappleCooldownUntilTick, AbsorptionFoodUtils.isFoodLevelCritical(absorptionPosData));
            int selectedInventory = this.findEatSlot(player, eatCooldownFlag, gappleAllowed2);
            if (selectedInventory >= 0) {
               int eatCooldownValue2 = player.getInventory().getSelectedSlot() == selectedInventory ? 0 : 2;
               return new CaseMaxData(SurroundKitType.EAT, true, 4.0, eatCooldownValue2 + 32, false, false, eatCooldownFlag ? "food" : "gapple");
            } else if (!gappleAllowed2 && this.findEatSlot(player, eatCooldownFlag, true) >= 0) {
               return CaseMaxData.getCaseMaxData(SurroundKitType.EAT, "cooldown");
            } else {
               return gappleAllowed2 && AbsorptionFoodUtils.needsHealingOrFood(absorptionPosData, getPlaystyle(selfPopData)) && this.requestGappleFetch(player, eatCooldownValue)
                  ? CaseMaxData.getCaseMaxData(SurroundKitType.EAT, "fetch")
                  : CaseMaxData.getCaseMaxData(SurroundKitType.EAT, gappleAllowed2 ? "no food" : "cooldown");
            }
         }
      } else {
         return new CaseMaxData(SurroundKitType.EAT, true, 4.0, 0, false, false, "eating");
      }
   }

   public boolean isReleaseTick() {
      return this.lastReleaseTick == ClientThreadUtils.getTime();
   }

   public void cancelEating() {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      boolean abortReleaseFlag = this.eatState == AutoEatController.EatState.HOLD || this.eatState == AutoEatController.EatState.START || this.useKeyHeld;
      if (abortReleaseFlag && instance.options != null) {
         ElytraAutoUtils.handleRawPressed(instance.options.useKey, CodeCategoryUtils.isKeyPressed(instance.options.useKey));
         if (player != null && player.isUsingItem() && this.eatState == AutoEatController.EatState.HOLD) {
            this.sprintSneak.recordForcedInteract(OutboundPacketKind.RELEASE_USE);
         }
      }

      this.useKeyHeld = false;
      if (this.eatState != AutoEatController.EatState.IDLE) {
         if (this.eatState == AutoEatController.EatState.HOLD) {
            long time = ClientThreadUtils.getTime();
            this.lastReleaseTick = time;
            this.recordCooldown(player != null && !player.isUsingItem(), time);
         }

         this.eatState = AutoEatController.EatState.IDLE;
      }

      this.holdTickCount = 0;
      this.selectStartTicks = 0;
   }

   static boolean isGappleAllowed(long cooldownPlayerLimit, long cooldownPlayerValue, boolean cooldownPlayerFlag) {
      return cooldownPlayerLimit >= cooldownPlayerValue || cooldownPlayerFlag;
   }

   public AutoEatController(TrackFlick trackFlick, HandswapGate ledgerHandswap, TickActionLedger sprintSneak, HotbarSlotSwitcher inventorySelected) {
      this.blockedUntilTick = Long.MIN_VALUE;
      this.gappleCooldownUntilTick = Long.MIN_VALUE;
      this.lastReleaseTick = Long.MIN_VALUE;
      this.gappleAllowed = true;
      this.trackFlick = trackFlick;
      this.ledgerHandswap = ledgerHandswap;
      this.sprintSneak = sprintSneak;
      this.inventorySelected = inventorySelected;
   }

   public static enum EatState {
      IDLE,
      SELECT,
      START,
      HOLD,
      RELEASE,
      DONE,
      ABORT;
   }
}
