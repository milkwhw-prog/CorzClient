package com.corz.client.utils;

import com.corz.client.config.VillagerTradeRule;
import com.corz.client.inventory.IngotBallUtils;
import com.corz.client.inventory.OfferTrade;
import com.corz.client.inventory.ProtectionCurseUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.c2s.play.SelectMerchantTradeC2SPacket;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import com.corz.client.utils.TradeEnchantUtils;
import com.corz.client.utils.EnchantJsonKeep;

public final class MerchantTrader {
   private String statusMessage;
   private boolean waitForCursorClear;
   private int emeraldsNeeded;
   private int unfilledOutputFailures;
   private int cursorStashAttempts;
   private int inventoryCountBeforeTrade;
   private VillagerTradeRule tradeRule;
   private Item missingItem;
   private int recipeResendCount;
   private long phaseStartTick;
   private int phaseDelayTicks = 4;
   private int pickupTargetSlot;
   private int emptyRecipeTicks;
   private final int verifyTimeoutTicks;
   private long tickCounter;
   private OfferTrade currentTrade;
   private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
   private int syncId;
   private long lastActionTick;
   private int pendingInventoryBaseline;
   private MerchantTrader.Phase phase;
   private int totalItemsTraded;
   private final int fillTimeoutTicks = 30;
   private OfferTrade pendingVerifyTrade;
   private long delayStartTick;
   private Predicate<ItemStack> dropFilter;

   public void setDropFilter(Predicate<ItemStack> predicate) {
      this.dropFilter = predicate;
   }

   private boolean isOutputReady(MerchantScreenHandler merchantScreenHandler, OfferTrade offerTrade) {
      if (merchantScreenHandler.slots.size() <= 2) {
         return false;
      } else {
         ItemStack stack = merchantScreenHandler.getSlot(2).getStack();
         return stack.isEmpty() ? false : this.matchesOfferOutput(stack, offerTrade);
      }
   }

   private MerchantTrader.Result tickTradePhase(MerchantScreenHandler merchantScreenHandler) {
      switch (this.phase) {
         case WAIT_FILL:
            if (this.isOutputReady(merchantScreenHandler, this.currentTrade)) {
               this.enterPhase(MerchantTrader.Phase.TAKE);
               this.lastActionTick = this.tickCounter;
               return MerchantTrader.Result.RUNNING;
            } else {
               if (this.getPhaseElapsedTicks() > this.fillTimeoutTicks) {
                  if (this.recipeResendCount < 2) {
                     this.recipeResendCount++;
                     this.selectRecipe(merchantScreenHandler, this.currentTrade.recipeIndex);
                     this.lastActionTick = this.tickCounter;
                     this.phaseStartTick = this.tickCounter;
                  } else {
                     this.enterPhase(MerchantTrader.Phase.DELAY);
                     this.delayStartTick = this.tickCounter;
                     this.currentTrade = null;
                     if (++this.unfilledOutputFailures >= 3) {
                        this.statusMessage = "the trade never filled \u2014 your items don't match its inputs (renamed or enchanted inputs, or something stuck in the trade slots?)";
                        this.enterPhase(MerchantTrader.Phase.READY);
                        return MerchantTrader.Result.FAILED;
                     }

                     this.statusMessage = "output never readied";
                  }
               }

               return MerchantTrader.Result.RUNNING;
            }
         case TAKE:
            if (this.tickCounter - this.lastActionTick < this.phaseDelayTicks) {
               return MerchantTrader.Result.RUNNING;
            } else {
               if (this.isCurrentTradeNearTarget()) {
                  ItemStack itemStack = merchantScreenHandler.slots.size() > 2 ? merchantScreenHandler.getSlot(2).getStack() : ItemStack.EMPTY;
                  int fullValue = merchantScreenHandler.getCursorStack().isEmpty() && !itemStack.isEmpty() ? this.findInventorySlotFor(merchantScreenHandler, itemStack) : -1;
                  if (fullValue < 0) {
                     this.currentTrade = null;
                     this.enterPhase(MerchantTrader.Phase.READY);
                     if (!itemStack.isEmpty() && merchantScreenHandler.getCursorStack().isEmpty()) {
                        this.statusMessage = "inventory full";
                        return MerchantTrader.Result.INV_FULL;
                     }

                     return MerchantTrader.Result.RUNNING;
                  }

                  CLIENT.interactionManager.clickSlot(this.syncId, 2, 0, SlotActionType.PICKUP, CLIENT.player);
                  this.pickupTargetSlot = fullValue;
                  this.lastActionTick = this.tickCounter;
                  this.enterPhase(MerchantTrader.Phase.PLACE);
                  return MerchantTrader.Result.RUNNING;
               }

               CLIENT.interactionManager.clickSlot(this.syncId, 2, 0, SlotActionType.QUICK_MOVE, CLIENT.player);
               this.lastActionTick = this.tickCounter;
               this.enterPhase(MerchantTrader.Phase.VERIFY);
               return MerchantTrader.Result.RUNNING;
            }
         case PLACE:
            if (this.tickCounter - this.lastActionTick < 1L) {
               return MerchantTrader.Result.RUNNING;
            }

            ItemStack cursorStack = merchantScreenHandler.getCursorStack();
            if (!cursorStack.isEmpty() && this.matchesOfferOutput(cursorStack, this.currentTrade)) {
               int unconfirmedReadiedLimit = canStackIntoSlot(merchantScreenHandler, this.pickupTargetSlot, cursorStack) ? this.pickupTargetSlot : this.findInventorySlotFor(merchantScreenHandler, cursorStack);
               if (unconfirmedReadiedLimit >= 0) {
                  CLIENT.interactionManager.clickSlot(this.syncId, unconfirmedReadiedLimit, 0, SlotActionType.PICKUP, CLIENT.player);
               }
            }

            this.pickupTargetSlot = -1;
            this.lastActionTick = this.tickCounter;
            this.enterPhase(MerchantTrader.Phase.VERIFY);
            return MerchantTrader.Result.RUNNING;
         case VERIFY:
            int inventoryStack = this.countInventoryOutput(this.currentTrade);
            if (inventoryStack > this.inventoryCountBeforeTrade) {
               this.recordTradedItems(inventoryStack - this.inventoryCountBeforeTrade);
               this.enterPhase(this.isAutoDropEnabled() ? MerchantTrader.Phase.DROP : MerchantTrader.Phase.DELAY);
               return MerchantTrader.Result.TRADED;
            }

            if (this.getPhaseElapsedTicks() > this.verifyTimeoutTicks) {
               this.pendingVerifyTrade = this.currentTrade;
               this.pendingInventoryBaseline = this.inventoryCountBeforeTrade;
               this.enterPhase(MerchantTrader.Phase.DELAY);
               this.delayStartTick = this.tickCounter;
               this.currentTrade = null;
               this.statusMessage = "trade unconfirmed";
            }

            return MerchantTrader.Result.RUNNING;
         case DROP:
            if (this.tickCounter - this.lastActionTick < 1L) {
               return MerchantTrader.Result.RUNNING;
            } else {
               if (merchantScreenHandler.getCursorStack().isEmpty() && this.getPhaseElapsedTicks() <= this.verifyTimeoutTicks) {
                  int predicateSlots = this.findDropSlot(this.currentTrade);
                  if (predicateSlots >= 0) {
                     CLIENT.interactionManager.clickSlot(this.syncId, predicateSlots, 1, SlotActionType.THROW, CLIENT.player);
                     this.lastActionTick = this.tickCounter;
                     return MerchantTrader.Result.RUNNING;
                  }

                  this.enterPhase(MerchantTrader.Phase.DELAY);
                  return MerchantTrader.Result.RUNNING;
               }

               this.enterPhase(MerchantTrader.Phase.DELAY);
               return MerchantTrader.Result.RUNNING;
            }
         default:
            return MerchantTrader.Result.RUNNING;
      }
   }

   public void reset() {
      this.tradeRule = null;
      this.phase = MerchantTrader.Phase.READY;
      this.currentTrade = null;
   }

   private MerchantScreenHandler getMerchantHandler() {
      if (CLIENT.player == null) {
         return null;
      } else if (CLIENT.currentScreen instanceof HandledScreen && !(CLIENT.currentScreen instanceof InventoryScreen)) {
         return CLIENT.player.currentScreenHandler instanceof MerchantScreenHandler merchantScreenHandler ? merchantScreenHandler : null;
      } else {
         return null;
      }
   }

   public void start(VillagerTradeRule enchantJsonProfession, int stackMerchantValue) {
      this.startWithCursorPolicy(enchantJsonProfession, stackMerchantValue, false);
   }

   public OfferTrade getCurrentTrade() {
      return this.currentTrade;
   }

   private long getPhaseElapsedTicks() {
      return this.tickCounter - this.phaseStartTick;
   }

   private int findDropSlot(OfferTrade offerTrade) {
      MerchantScreenHandler instanceScreen = this.getMerchantHandler();
      if (instanceScreen == null) {
         return -1;
      } else {
         int predicateSlotsValue = instanceScreen.slots.size() - 36;
         if (predicateSlotsValue < 0) {
            return -1;
         } else {
            for (int i = predicateSlotsValue; i < instanceScreen.slots.size(); i++) {
               ItemStack stack = instanceScreen.getSlot(i).getStack();
               if (this.matchesOfferOutput(stack, offerTrade) && (this.dropFilter == null || this.dropFilter.test(stack))) {
                  return i;
               }
            }

            return -1;
         }
      }
   }

   private OfferTrade pickBestTrade(List<OfferTrade> list) {
      OfferTrade next = null;

      for (OfferTrade offerTrade : list) {
         if (TradeEnchantUtils.isAvailableMatch(this.tradeRule, offerTrade)) {
            if (next == null) {
               next = offerTrade;
            } else if (this.tradeRule.action == VillagerTradeRule.Action.BUY) {
               if (offerTrade.count3EnchantJson < next.count3EnchantJson) {
                  next = offerTrade;
               }
            } else if (offerTrade.emeraldAutomateValue > next.emeraldAutomateValue) {
               next = offerTrade;
            }
         }
      }

      return next;
   }

   private static boolean canStackIntoSlot(MerchantScreenHandler merchantScreenHandler, int slotsEqualLimit, ItemStack itemStack) {
      if (slotsEqualLimit >= merchantScreenHandler.slots.size() - 36 && slotsEqualLimit >= 0 && slotsEqualLimit < merchantScreenHandler.slots.size()) {
         ItemStack stack = merchantScreenHandler.getSlot(slotsEqualLimit).getStack();
         return stack.isEmpty() ? true : ItemStack.areItemsAndComponentsEqual(stack, itemStack) && stack.getCount() + itemStack.getCount() <= stack.getMaxCount();
      } else {
         return false;
      }
   }

   private void enterPhase(MerchantTrader.Phase verifyTakeType) {
      this.phase = verifyTakeType;
      this.phaseStartTick = this.tickCounter;
   }

   public String getStatusMessage() {
      return this.statusMessage;
   }

   public String formatCurrentTrade() {
      return this.currentTrade == null ? "?" : this.currentTrade.formatLevelMax();
   }

   public boolean hasTradeRule() {
      return this.tradeRule != null;
   }

   private String describeNoMatch(List<OfferTrade> list) {
      if (this.tradeRule.action == VillagerTradeRule.Action.BUY && this.tradeRule.kind == VillagerTradeRule.OutputKind.ITEM) {
         Item defaultedRegistry = IngotBallUtils.getDefaultedRegistry(this.tradeRule.item);

         for (OfferTrade offerTrade : list) {
            if (defaultedRegistry != null && offerTrade.emeraldStackItem == defaultedRegistry) {
               boolean enchantJsonFlag = this.tradeRule.maxEmeraldCost <= 0 || offerTrade.count3EnchantJson <= this.tradeRule.maxEmeraldCost;
               return "want "
                  + IngotBallUtils.formatBookIngot(this.tradeRule.item)
                  + " max"
                  + this.tradeRule.maxEmeraldCost
                  + " | got cost"
                  + offerTrade.count3EnchantJson
                  + " used "
                  + offerTrade.uses2EnchantJson
                  + "/"
                  + offerTrade.maxUses
                  + " | avail="
                  + offerTrade.isDisabled()
                  + " costOK="
                  + enchantJsonFlag;
            }
         }

         return "no " + IngotBallUtils.formatBookIngot(this.tradeRule.item) + " offer; had [" + this.formatOfferSummary(list) + "]";
      } else if (this.tradeRule.action == VillagerTradeRule.Action.BUY && this.tradeRule.kind == VillagerTradeRule.OutputKind.ENCHANTED_BOOK) {
         for (OfferTrade offerTrade2 : list) {
            if (offerTrade2.emeraldStackActive) {
               boolean enchantJsonFlag2 = offerTrade2.next != null && ProtectionCurseUtils.canMatchesProtection(offerTrade2.next, this.tradeRule.enchant);
               boolean enchantJsonFlag3 = offerTrade2.level >= Math.max(1, this.tradeRule.enchantLevel);
               boolean enchantJsonFlag4 = this.tradeRule.maxEmeraldCost <= 0 || offerTrade2.count3EnchantJson <= this.tradeRule.maxEmeraldCost;
               return "want '"
                  + this.tradeRule.enchant
                  + "' L"
                  + this.tradeRule.enchantLevel
                  + " max"
                  + this.tradeRule.maxEmeraldCost
                  + " | got '"
                  + offerTrade2.levelText
                  + "' L"
                  + offerTrade2.level
                  + " cost"
                  + offerTrade2.count3EnchantJson
                  + " | avail="
                  + offerTrade2.isDisabled()
                  + " enchOK="
                  + enchantJsonFlag2
                  + " lvlOK="
                  + enchantJsonFlag3
                  + " costOK="
                  + enchantJsonFlag4;
            }
         }

         return "no enchanted-book offer; had [" + this.formatOfferSummary(list) + "]";
      } else {
         return "no match among [" + this.formatOfferSummary(list) + "]";
      }
   }

   public void startWithCursorPolicy(VillagerTradeRule enchantJson, int readiedTradeBound, boolean cursorFlag) {
      this.tradeRule = enchantJson;
      this.waitForCursorClear = cursorFlag;
      this.cursorStashAttempts = 0;
      this.phaseDelayTicks = Math.max(1, readiedTradeBound);
      this.phase = MerchantTrader.Phase.READY;
      this.syncId = -1;
      this.currentTrade = null;
      this.recipeResendCount = 0;
      this.emptyRecipeTicks = 0;
      this.unfilledOutputFailures = 0;
      this.pendingVerifyTrade = null;
      this.emeraldsNeeded = 0;
      this.missingItem = null;
      this.totalItemsTraded = 0;
      this.lastActionTick = -4611686018427387904L;
      this.delayStartTick = -4611686018427387904L;
      this.statusMessage = "";
   }

   private boolean isCurrentTradeNearTarget() {
      return this.isNearTargetAmount(this.currentTrade);
   }

   private void recordTradedItems(int readiedUnconfirmedValue) {
      this.totalItemsTraded += readiedUnconfirmedValue;
      if (this.tradeRule != null) {
         this.tradeRule.tradedAmount += readiedUnconfirmedValue;
      }

      this.delayStartTick = this.tickCounter;
      this.unfilledOutputFailures = 0;
   }

   private boolean isAutoDropEnabled() {
      return this.tradeRule != null && this.tradeRule.action == VillagerTradeRule.Action.BUY && this.tradeRule.autoDrop;
   }

   private boolean isNearTargetAmount(OfferTrade offerTrade) {
      if (this.tradeRule != null && this.tradeRule.targetAmount > 0 && offerTrade != null) {
         int enchantJsonValue = this.tradeRule.targetAmount - this.tradeRule.tradedAmount;
         return enchantJsonValue < offerTrade.getMax() * Math.max(1, offerTrade.emeraldAutomateValue);
      } else {
         return false;
      }
   }

   private boolean hasRoomForOutput(OfferTrade offerTrade) {
      PlayerInventory inventory = CLIENT.player.getInventory();

      for (int i = 0; i < 36; i++) {
         ItemStack stack = inventory.getStack(i);
         if (stack.isEmpty()) {
            return true;
         }

         if (stack.isOf(offerTrade.emeraldStackItem) && !offerTrade.emeraldStackActive && stack.getCount() < stack.getMaxCount()) {
            return true;
         }
      }

      return false;
   }

   private List<OfferTrade> collectOffers(MerchantScreenHandler merchantScreenHandler) {
      ArrayList arrayList = new ArrayList();
      TradeOfferList recipes = merchantScreenHandler.getRecipes();

      for (int i = 0; i < recipes.size(); i++) {
         TradeOffer tradeOffer = recipes.get(i);
         if (tradeOffer != null) {
            arrayList.add(OfferTrade.getDisplayedUses(tradeOffer, i));
         }
      }

      return arrayList;
   }

   public MerchantTrader.Result tickTrading() {
      if (this.tradeRule == null) {
         return MerchantTrader.Result.FAILED;
      } else if (CLIENT.player != null && CLIENT.interactionManager != null) {
         MerchantScreenHandler instanceScreen = this.getMerchantHandler();
         if (instanceScreen == null) {
            return MerchantTrader.Result.CLOSED;
         } else {
            this.tickCounter++;
            if (this.syncId < 0) {
               this.syncId = instanceScreen.syncId;
            } else if (this.syncId != instanceScreen.syncId) {
               this.syncId = instanceScreen.syncId;
               this.enterPhase(MerchantTrader.Phase.READY);
               this.currentTrade = null;
            }

            if (this.phase != MerchantTrader.Phase.READY && this.phase != MerchantTrader.Phase.DELAY) {
               if (this.currentTrade != null) {
                  return this.tickTradePhase(instanceScreen);
               }

               this.enterPhase(MerchantTrader.Phase.READY);
            }

            if (this.phase == MerchantTrader.Phase.DELAY && this.tickCounter - this.delayStartTick < this.phaseDelayTicks) {
               return MerchantTrader.Result.RUNNING;
            } else {
               ItemStack cursorStack = instanceScreen.getCursorStack();
               if (cursorStack.isEmpty()) {
                  this.cursorStashAttempts = 0;
                  if (this.pendingVerifyTrade != null) {
                     int unconfirmedFullLimit = this.countInventoryOutput(this.pendingVerifyTrade) - this.pendingInventoryBaseline;
                     this.pendingVerifyTrade = null;
                     if (unconfirmedFullLimit > 0) {
                        this.recordTradedItems(unconfirmedFullLimit);
                        this.enterPhase(MerchantTrader.Phase.DELAY);
                        return MerchantTrader.Result.TRADED;
                     }
                  }

                  if (!this.tradeRule.isEnabled()) {
                     return MerchantTrader.Result.DONE_TARGET;
                  } else {
                     List recipes = this.collectOffers(instanceScreen);
                     if (recipes.isEmpty()) {
                        if (++this.emptyRecipeTicks <= 60) {
                           return MerchantTrader.Result.RUNNING;
                        } else {
                           this.statusMessage = "villager sent no trades";
                           return MerchantTrader.Result.STOCK_OUT;
                        }
                     } else {
                        this.emptyRecipeTicks = 0;
                        OfferTrade offerTrade = this.pickBestTrade(recipes);
                        if (offerTrade == null) {
                           this.statusMessage = this.describeNoMatch(recipes);
                           return MerchantTrader.Result.STOCK_OUT;
                        } else if (offerTrade.primaryCostItem == Items.EMERALD && this.countItemInInventoryAndInputs(instanceScreen, Items.EMERALD) < offerTrade.primaryCostCount) {
                           this.emeraldsNeeded = offerTrade.primaryCostCount;
                           this.statusMessage = "short " + this.emeraldsNeeded + " emeralds";
                           return MerchantTrader.Result.NEED_EMERALDS;
                        } else if (offerTrade.secondaryCostItem == Items.EMERALD && this.countItemInInventoryAndInputs(instanceScreen, Items.EMERALD) < offerTrade.count2UnconfirmedFull) {
                           this.emeraldsNeeded = offerTrade.count2UnconfirmedFull;
                           this.statusMessage = "short " + this.emeraldsNeeded + " emeralds";
                           return MerchantTrader.Result.NEED_EMERALDS;
                        } else if (offerTrade.primaryCostItem != null && offerTrade.primaryCostItem != Items.EMERALD && this.countItemInInventoryAndInputs(instanceScreen, offerTrade.primaryCostItem) < offerTrade.primaryCostCount) {
                           this.missingItem = offerTrade.primaryCostItem;
                           this.statusMessage = "short " + IngotBallUtils.getKey(this.missingItem);
                           return MerchantTrader.Result.NEED_MATERIALS;
                        } else if (offerTrade.isFlag() && offerTrade.secondaryCostItem != Items.EMERALD && this.countItemInInventoryAndInputs(instanceScreen, offerTrade.secondaryCostItem) < offerTrade.count2UnconfirmedFull) {
                           this.missingItem = offerTrade.secondaryCostItem;
                           this.statusMessage = "short " + IngotBallUtils.getKey(this.missingItem);
                           return MerchantTrader.Result.NEED_MATERIALS;
                        } else if (!this.isAutoDropEnabled() && !this.hasRoomForOutput(offerTrade)) {
                           this.statusMessage = "inventory full";
                           return MerchantTrader.Result.INV_FULL;
                        } else if (this.isNearTargetAmount(offerTrade) && this.findInventorySlotFor(instanceScreen, offerTrade.copy) < 0) {
                           this.statusMessage = "inventory full";
                           return MerchantTrader.Result.INV_FULL;
                        } else {
                           this.currentTrade = offerTrade;
                           this.selectRecipe(instanceScreen, offerTrade.recipeIndex);
                           this.inventoryCountBeforeTrade = this.countInventoryOutput(offerTrade);
                           this.lastActionTick = this.tickCounter;
                           this.recipeResendCount = 0;
                           this.enterPhase(MerchantTrader.Phase.WAIT_FILL);
                           return MerchantTrader.Result.RUNNING;
                        }
                     }
                  }
               } else if (this.waitForCursorClear) {
                  return MerchantTrader.Result.RUNNING;
               } else {
                  int slotsSlot = this.findInventorySlotFor(instanceScreen, cursorStack);
                  if (slotsSlot >= 0 && ++this.cursorStashAttempts <= 3) {
                     CLIENT.interactionManager.clickSlot(this.syncId, slotsSlot, 0, SlotActionType.PICKUP, CLIENT.player);
                     this.delayStartTick = this.tickCounter;
                     this.enterPhase(MerchantTrader.Phase.DELAY);
                     return MerchantTrader.Result.RUNNING;
                  } else {
                     this.statusMessage = "an item is stuck on the cursor" + (slotsSlot < 0 ? " and the inventory is full" : "");
                     return MerchantTrader.Result.FAILED;
                  }
               }
            }
         }
      } else {
         return MerchantTrader.Result.FAILED;
      }
   }

   private int countItemInInventoryAndInputs(MerchantScreenHandler merchantScreenHandler, Item item2) {
      int inventoryStack = this.countInventoryItem(item2);

      for (int i = 0; i < 2 && i < merchantScreenHandler.slots.size(); i++) {
         ItemStack stack = merchantScreenHandler.getSlot(i).getStack();
         if (stack.isOf(item2)) {
            inventoryStack += stack.getCount();
         }
      }

      return inventoryStack;
   }

   private String formatOfferSummary(List<OfferTrade> list) {
      StringBuilder stringBuilder = new StringBuilder();

      for (OfferTrade offerTrade : list) {
         if (stringBuilder.length() > 0) {
            stringBuilder.append(", ");
         }

         stringBuilder.append(offerTrade.formatLevelMax())
            .append(" ")
            .append(offerTrade.count3EnchantJson)
            .append("e")
            .append(offerTrade.isDisabled() ? "" : "(x" + offerTrade.uses2EnchantJson + "/" + offerTrade.maxUses + (offerTrade.disabled ? " off" : "") + ")");
      }

      return stringBuilder.toString();
   }

   private void selectRecipe(MerchantScreenHandler merchantScreenHandler, int recipeIndex) {
      merchantScreenHandler.setRecipeIndex(recipeIndex);
      CLIENT.player.networkHandler.sendPacket(new SelectMerchantTradeC2SPacket(recipeIndex));
   }

   private boolean matchesOfferOutput(ItemStack stack, OfferTrade offerTrade) {
      if (stack == null || stack.isEmpty()) {
         return false;
      } else {
         return offerTrade.emeraldStackActive
            ? EnchantJsonKeep.hasStoredEnchantmentAtLeast(stack, offerTrade.levelText.isEmpty() ? "Any" : ProtectionCurseUtils.getKey(offerTrade.next), Math.max(1, offerTrade.level))
            : stack.isOf(offerTrade.emeraldStackItem);
      }
   }

   public int getTotalItemsTraded() {
      return this.totalItemsTraded;
   }

   public MerchantTrader() {
      this.verifyTimeoutTicks = 40;
      this.phase = MerchantTrader.Phase.READY;
      this.syncId = -1;
      this.lastActionTick = -4611686018427387904L;
      this.delayStartTick = -4611686018427387904L;
      this.pickupTargetSlot = -1;
      this.statusMessage = "";
   }

   private int countInventoryItem(Item item2) {
      PlayerInventory inventory = CLIENT.player.getInventory();
      int inventoryStack = 0;

      for (int i = 0; i <= 35; i++) {
         ItemStack stack = inventory.getStack(i);
         if (stack.isOf(item2)) {
            inventoryStack += stack.getCount();
         }
      }

      return inventoryStack;
   }

   public int getEmeraldsNeeded() {
      return this.emeraldsNeeded;
   }

   public Item getMissingItem() {
      return this.missingItem;
   }

   private int countInventoryOutput(OfferTrade offerTrade) {
      PlayerInventory inventory = CLIENT.player.getInventory();
      int inventoryStack = 0;

      for (int i = 0; i <= 35; i++) {
         ItemStack stack = inventory.getStack(i);
         if (this.matchesOfferOutput(stack, offerTrade)) {
            inventoryStack += stack.getCount();
         }
      }

      return inventoryStack;
   }

   private int findInventorySlotFor(MerchantScreenHandler merchantScreenHandler, ItemStack itemStack) {
      int slotsSlotValue = merchantScreenHandler.slots.size() - 36;
      if (slotsSlotValue < 0) {
         return -1;
      } else {
         int slotsSlotValue2 = slotsSlotValue + 27;

         for (int i = 0; i < 2; i++) {
            boolean slotsSlotFlag = i == 1;

            for (int j = slotsSlotValue; j < merchantScreenHandler.slots.size(); j++) {
               if (j >= slotsSlotValue2 == slotsSlotFlag && !merchantScreenHandler.getSlot(j).getStack().isEmpty() && canStackIntoSlot(merchantScreenHandler, j, itemStack)) {
                  return j;
               }
            }

            for (int k = slotsSlotValue; k < merchantScreenHandler.slots.size(); k++) {
               if (k >= slotsSlotValue2 == slotsSlotFlag && merchantScreenHandler.getSlot(k).getStack().isEmpty()) {
                  return k;
               }
            }
         }

         return -1;
      }
   }

   private static enum Phase {
      READY,
      WAIT_FILL,
      TAKE,
      PLACE,
      VERIFY,
      DROP,
      DELAY;
   }

   public static enum Result {
      RUNNING,
      TRADED,
      STOCK_OUT,
      NEED_EMERALDS,
      NEED_MATERIALS,
      INV_FULL,
      DONE_TARGET,
      CLOSED,
      FAILED;
   }
}
