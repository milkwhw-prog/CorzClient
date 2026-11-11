package com.corz.client.utils;

import com.corz.client.inventory.InventoryStackUtilsHandle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FireworksComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import com.corz.client.utils.TotalPriceData;

public final class PilotAhBuyer {
   private int peakItemsGained;
   private boolean sawListing;
   private final List<TotalPriceData> pageListings;
   private boolean triedAlternateSearch;
   private int syncId;
   private long totalBudget;
   private volatile PilotAhBuyer.Status status;
   private int menuWaitTicks;
   private int boardClosedCount;
   private int unconfirmedItemTotal;
   private int phaseStartTick;
   private long lastUnconfirmedPrice;
   private static final String NOT_ENOUGH_MONEY = "not enough money";
   private boolean ahMenuRequested;
   private MinecraftClient client;
   private int lastUnconfirmedBaseline;
   private final Set<TotalPriceData> triedListings;
   private int retryAtTick;
   private int targetItemCount;
   private final ConcurrentLinkedQueue<String> chatQueue = new ConcurrentLinkedQueue<>();
   static final String NEXT_PAGE_LABEL = "next page";
   private int lastUnconfirmedTick;
   private static final long START_NANOS = System.nanoTime();
   private int inventoryCountBeforeClick;
   private volatile String stopReason;
   private volatile String statusMessage;
   private String searchCommand;
   private static final MaxBuyer COMMAND_LIMITER = new MaxBuyer();
   private int confirmKeyStep;
   private final List<KmbtSellerUtils.ChatPurchase> recentChatPurchases = new ArrayList<>();
   private int scanReadyTick;
   private int confirmAttemptCount;
   static final String ENCHANTING = "ah enchanting";
   private static long lastSearchNanos = System.nanoTime() - 1500000000L;
   private PilotAhBuyer.Phase phase;
   static final String CURRENCY_SYMBOL = "$";
   private volatile boolean outOfMoney;
   private volatile long totalSpent;
   private volatile int purchaseCount;
   private boolean overcharged;
   private TotalPriceData selectedListing;
   private int retryCount;
   private int snipedStreak;
   private static final String INVENTORY_FULL = "inventory full";
   private int lastUnconfirmedCount;
   private String chatFailureReason;
   private boolean wasSniped;
   private int pageContentHash;
   private volatile int boughtCount;
   static final String FIREWORK_ROCKET = "ah firework rocket";
   private boolean smallLotsAvailable;
   private boolean allowSmallLots;
   private int clickDelayTicks;
   private boolean reachedLastPage;
   private int tickCount;
   static final String BOTTLE_ENCHANTING = "ah bottle o' enchanting";
   private volatile PilotAhBuyer.Goods goods;
   private int purchaseCheckTick;
   private int purchaseLimit;
   private int pagesTurned;
   private long maxUnitPrice;
   private int searchSentTick;
   private int lastActionTick;

   public void onTick() {
      if (this.status != PilotAhBuyer.Status.RUNNING) {
         if (this.menuWaitTicks > 0) {
            this.tickMenuCloseWait();
         }
      } else {
         this.client = MinecraftClient.getInstance();

         try {
            this.tickStateMachine();
         } catch (Throwable e) {
            this.finishRun(PilotAhBuyer.Status.FAILED, "error: " + e.getClass().getSimpleName());
         }
      }
   }

   static PilotAhBuyer.ChatKind classifyChatMessage(String boughtMoneyText) {
      if (boughtMoneyText == null) {
         return PilotAhBuyer.ChatKind.NONE;
      } else {
         String trim = KmbtSellerUtils.formatText(boughtMoneyText).toLowerCase(Locale.ROOT).trim();
         if (trim.isEmpty()) {
            return PilotAhBuyer.ChatKind.NONE;
         } else if (trim.startsWith("this item was already bought")
            || trim.startsWith("this item has already been bought")
            || trim.equals("already bought")
            || trim.equals("already bought.")
            || trim.equals("already bought!")) {
            return PilotAhBuyer.ChatKind.SNIPED;
         } else if (trim.startsWith("you do not have enough money")
            || trim.startsWith("you don't have enough money")
            || trim.startsWith("you don\u2019t have enough money")) {
            return PilotAhBuyer.ChatKind.NO_MONEY;
         } else if (trim.startsWith("your inventory is full")) {
            return PilotAhBuyer.ChatKind.INVENTORY_FULL;
         } else {
            return KmbtSellerUtils.getEmptyForString(boughtMoneyText).isPresent() ? PilotAhBuyer.ChatKind.RECEIPT : PilotAhBuyer.ChatKind.NONE;
         }
      }
   }

   private void tickMenuCloseWait() {
      this.menuWaitTicks--;

      try {
         this.client = MinecraftClient.getInstance();
         if (this.client == null || this.client.player == null) {
            this.menuWaitTicks = 0;
            return;
         }

         if (this.isConfirmDialogOpen()) {
            this.closeCurrentScreen();
            return;
         }

         if (this.isAuctionBoardOpen()) {
            this.client.player.closeHandledScreen();
            this.menuWaitTicks = 0;
         }
      } catch (Throwable e) {
         this.menuWaitTicks = 0;
      }
   }

   private void tickStateMachine() {
      if (this.client != null && this.client.player != null && this.client.interactionManager != null && this.client.player.networkHandler != null) {
         this.tickCount++;
         this.drainChatQueue();
         if (this.chatFailureReason != null) {
            String moneyEnoughText = this.chatFailureReason;
            this.chatFailureReason = null;
            if (this.settleUnconfirmedPurchase()) {
               if ("not enough money".equals(moneyEnoughText)) {
                  this.outOfMoney = true;
                  this.finishRun(PilotAhBuyer.Status.FAILED, "not enough money");
               } else {
                  this.finishRun(this.boughtCount > 0 ? PilotAhBuyer.Status.DONE : PilotAhBuyer.Status.FAILED, "inventory full");
               }
            }
         } else if (this.tickCount > 3600) {
            if (this.settleUnconfirmedPurchase()) {
               this.finishRun(this.boughtCount > 0 ? PilotAhBuyer.Status.DONE : PilotAhBuyer.Status.FAILED, "timed out");
            }
         } else {
            if (this.phase != PilotAhBuyer.Phase.CONFIRM && this.phase != PilotAhBuyer.Phase.VERIFY && this.phase != PilotAhBuyer.Phase.CREDIT) {
               this.reconcileUnconfirmedPurchase();
               if (this.status != PilotAhBuyer.Status.RUNNING) {
                  return;
               }
            }

            switch (this.phase) {
               case SEARCH:
                  this.runSearchPhase();
                  break;
               case WAIT_BOARD:
                  this.runWaitBoardPhase();
                  break;
               case SCAN:
                  this.runScanPhase();
                  break;
               case PAGE_WAIT:
                  this.runPageWaitPhase();
                  break;
               case CLICK:
                  this.runClickPhase();
                  break;
               case CONFIRM:
                  this.runConfirmPhase();
                  break;
               case VERIFY:
                  this.runVerifyPhase();
                  break;
               case CREDIT:
                  this.runCreditPhase();
                  break;
               case RETRY_WAIT:
                  this.runRetryWaitPhase();
            }
         }
      } else {
         this.finishRun(PilotAhBuyer.Status.FAILED, "no player");
      }
   }

   public int getTargetItemCount() {
      return this.targetItemCount;
   }

   private void reconcileUnconfirmedPurchase() {
      if (this.lastUnconfirmedBaseline >= 0) {
         if (this.wasSniped) {
            this.wasSniped = false;
            this.unconfirmedItemTotal = Math.max(0, this.unconfirmedItemTotal - this.lastUnconfirmedCount);
            this.lastUnconfirmedBaseline = -1;
         } else {
            int fireworkRocketLimit = this.getHeldGoodsCount() - this.lastUnconfirmedBaseline;
            if (fireworkRocketLimit > 0) {
               this.lastUnconfirmedBaseline = -1;
               this.unconfirmedItemTotal = Math.max(0, this.unconfirmedItemTotal - fireworkRocketLimit);
               if (this.recordPurchase(fireworkRocketLimit, this.lastUnconfirmedPrice)) {
                  if (this.getHeldGoodsCount() >= this.targetItemCount) {
                     this.finishRun(PilotAhBuyer.Status.DONE, "");
                  }
               }
            }
         }
      }
   }

   private void runClickPhase() {
      if (this.handleBoardClosedOrChanged()) {
         this.selectedListing = null;
      } else if (this.selectedListing == null) {
         this.enterPhase(PilotAhBuyer.Phase.SCAN);
      } else if (this.tickCount - this.lastActionTick >= this.clickDelayTicks) {
         if (this.countFreeInventorySlots() == 0) {
            this.finishRun(this.boughtCount > 0 ? PilotAhBuyer.Status.DONE : PilotAhBuyer.Status.FAILED, "inventory full");
         } else {
            TotalPriceData goodsSlot = this.readListingAtSlot(this.client.player.currentScreenHandler, this.selectedListing.slot());
            int purchaseCheckingValue = this.getHeldGoodsCount();
            boolean fullFlag = this.totalBudget > 0L && this.selectedListing.totalPrice() > this.totalBudget - this.totalSpent;
            if (goodsSlot != null && goodsSlot.equals(this.selectedListing) && purchaseCheckingValue < this.targetItemCount && purchaseCheckingValue + this.unconfirmedItemTotal + this.selectedListing.count() <= this.purchaseLimit && !fullFlag) {
               if (this.lastUnconfirmedBaseline >= 0) {
                  if (this.tickCount - this.lastUnconfirmedTick < 100) {
                     return;
                  }

                  this.unconfirmedItemTotal = Math.max(0, this.unconfirmedItemTotal - this.lastUnconfirmedCount);
                  this.lastUnconfirmedBaseline = -1;
               }

               this.inventoryCountBeforeClick = purchaseCheckingValue;
               this.wasSniped = false;
               this.recentChatPurchases.clear();
               this.confirmKeyStep = 0;
               this.confirmAttemptCount = 0;
               this.client.interactionManager.clickSlot(this.syncId, this.selectedListing.slot(), 0, SlotActionType.PICKUP, this.client.player);
               this.markActionTick();
               this.enterPhase(PilotAhBuyer.Phase.CONFIRM);
            } else {
               this.selectedListing = null;
               this.syncScreenAndScheduleScan();
               this.enterPhase(PilotAhBuyer.Phase.SCAN);
            }
         }
      }
   }

   private void onPurchaseUnconfirmed(String tryingText) {
      if (this.selectedListing != null) {
         this.lastUnconfirmedBaseline = this.inventoryCountBeforeClick;
         this.lastUnconfirmedTick = this.tickCount;
         this.lastUnconfirmedCount = this.selectedListing.count();
         this.lastUnconfirmedPrice = this.selectedListing.totalPrice();
         this.unconfirmedItemTotal = this.unconfirmedItemTotal + this.selectedListing.count();
         this.triedListings.add(this.selectedListing);
      }

      this.closeCurrentScreen();
      this.retryOrFail(tryingText, true);
   }

   public void onChatMessage(String concurrentQueueText) {
      try {
         if (concurrentQueueText == null || this.status != PilotAhBuyer.Status.RUNNING || classifyChatMessage(concurrentQueueText) == PilotAhBuyer.ChatKind.NONE) {
            return;
         }

         while (this.chatQueue.size() >= 64) {
            this.chatQueue.poll();
         }

         this.chatQueue.offer(concurrentQueueText);
      } catch (Throwable e) {
      }
   }

   private void closeCurrentScreen() {
      if (this.isConfirmDialogOpen()) {
         try {
            this.client.currentScreen.close();
         } catch (Throwable e) {
         }
      }
   }

   public boolean isRunning() {
      return this.status == PilotAhBuyer.Status.RUNNING;
   }

   private boolean isActionCooldownOver() {
      return this.tickCount - this.lastActionTick >= 4;
   }

   public String getStatusMessage() {
      return this.statusMessage;
   }

   public static int countGoodsInInventory(PlayerInventory playerInventory, PilotAhBuyer.Goods goods) {
      if (playerInventory != null && goods != null) {
         int stackRockets = 0;

         for (int i = 0; i < 36; i++) {
            ItemStack stack = playerInventory.getStack(i);
            if (isGoodsItem(stack, goods)) {
               stackRockets += stack.getCount();
            }
         }

         if (goods == PilotAhBuyer.Goods.ROCKETS) {
            ItemStack stack2 = playerInventory.getStack(40);
            if (isDuration3Rocket(stack2)) {
               stackRockets += stack2.getCount();
            }
         }

         return stackRockets;
      } else {
         return 0;
      }
   }

   public static boolean isPlainFireworkRocket(ItemStack stack) {
      if (stack != null && !stack.isEmpty() && stack.isOf(Items.FIREWORK_ROCKET)) {
         FireworksComponent fireworksComponent = stack.get(DataComponentTypes.FIREWORKS);
         return fireworksComponent == null || fireworksComponent.explosions().isEmpty();
      } else {
         return false;
      }
   }

   public int getPurchaseLimit() {
      return this.purchaseLimit;
   }

   private String getCurrentScreenTitle() {
      return this.client == null ? "" : getLowerCaseTitle(this.client.currentScreen);
   }

   static Optional<TotalPriceData> chooseBestListing(
      List<TotalPriceData> list, int totalPriceValue, int totalPriceValue2, int deltaMax, long totalPriceLimit, long listingAffordableValue, int totalPriceBound, int totalPriceBound2
   ) {
      if (list != null && !list.isEmpty()) {
         int totalPriceLimit3 = totalPriceValue2 - totalPriceValue;
         int minCap = Math.min(deltaMax - totalPriceValue, totalPriceBound);
         if (totalPriceLimit3 > 0 && minCap > 0) {
            int max4 = Math.max(1, Math.min(totalPriceBound2, Math.min(totalPriceLimit3, minCap)));
            ArrayList arrayList = new ArrayList(list.size());

            for (TotalPriceData totalPriceData : list) {
               if (totalPriceData != null
                  && totalPriceData.count() > 0
                  && totalPriceData.totalPrice() >= 0L
                  && totalPriceData.count() <= minCap
                  && totalPriceData.count() >= max4
                  && (totalPriceLimit <= 0L || totalPriceData.getTotalPrice() <= totalPriceLimit)
                  && (listingAffordableValue <= 0L || totalPriceData.totalPrice() <= listingAffordableValue)) {
                  arrayList.add(totalPriceData);
               }
            }

            return arrayList.isEmpty() ? Optional.empty() : TotalPriceUtilsOptional.getTotalPrice(arrayList, totalPriceLimit3, totalPriceLimit, listingAffordableValue, 0.15, minCap);
         } else {
            return Optional.empty();
         }
      } else {
         return Optional.empty();
      }
   }

   private void syncScreenAndScheduleScan() {
      this.syncId = this.client.player.currentScreenHandler.syncId;
      this.scanReadyTick = this.tickCount + 4 + randomBelow(4);
   }

   private boolean hasGreenOrRedPane(boolean slotStackFlag) {
      ScreenHandler currentScreenHandler = this.client.player.currentScreenHandler;
      int slotStackCounter = 0;

      for (int i = currentScreenHandler.slots.size() - 36; slotStackCounter < i; slotStackCounter++) {
         ItemStack stack = currentScreenHandler.getSlot(slotStackCounter).getStack();
         if (slotStackFlag ? isGreenConfirmItem(stack) : isRedCancelItem(stack)) {
            return true;
         }
      }

      return false;
   }

   private void runRetryWaitPhase() {
      if (this.isConfirmDialogOpen()) {
         if (this.isActionCooldownOver()) {
            this.closeCurrentScreen();
            this.markActionTick();
         }
      } else if (this.isContainerScreenOpen()) {
         if (this.isActionCooldownOver()) {
            this.client.player.closeHandledScreen();
            this.markActionTick();
         }
      } else {
         if (this.tickCount >= this.retryAtTick) {
            this.enterPhase(PilotAhBuyer.Phase.SEARCH);
         }
      }
   }

   public int getBoughtCount() {
      return this.boughtCount;
   }

   static OptionalLong parseListingPrice(List<String> list) {
      if (list != null && list.size() >= 2) {
         List subList = list.subList(1, list.size());
         return !KmbtSellerUtils.canPriceRoot(subList, "$") ? OptionalLong.empty() : KmbtSellerUtils.getRoot(subList, "$");
      } else {
         return OptionalLong.empty();
      }
   }

   private boolean isAuctionBoardOpen() {
      if (!this.isContainerScreenOpen()) {
         return false;
      } else {
         String formatInstanceScreen = this.getCurrentScreenTitle();
         if (formatInstanceScreen.contains("sure") || formatInstanceScreen.contains("confirm")) {
            return false;
         } else {
            return formatInstanceScreen.contains("auction") ? true : this.hasGoodsListing() && (!this.hasGreenOrRedPane(true) || !this.hasGreenOrRedPane(false));
         }
      }
   }

   private boolean handleBoardClosedOrChanged() {
      if (this.isAuctionBoardOpen()) {
         if (this.client.player.currentScreenHandler.syncId == this.syncId) {
            return false;
         } else {
            this.triedListings.clear();
            this.syncScreenAndScheduleScan();
            this.enterPhase(PilotAhBuyer.Phase.SCAN);
            return true;
         }
      } else if (++this.boardClosedCount >= 4) {
         this.boardClosedCount = 0;
         this.retryOrFail("the auction board kept closing", false);
         return true;
      } else {
         this.enterPhase(PilotAhBuyer.Phase.SEARCH);
         return true;
      }
   }

   private static int randomBelow(int intBound) {
      return ThreadLocalRandom.current().nextInt(Math.max(1, intBound));
   }

   private boolean isContainerScreenOpen() {
      return this.client != null
         && this.client.player != null
         && this.client.currentScreen instanceof HandledScreen
         && !(this.client.currentScreen instanceof InventoryScreen)
         && this.client.player.currentScreenHandler != null
         && this.client.player.currentScreenHandler != this.client.player.playerScreenHandler;
   }

   private void closeAhScreens() {
      if (this.ahMenuRequested) {
         try {
            if (this.client == null || this.client.player == null) {
               return;
            }

            if (this.isConfirmDialogOpen()) {
               this.closeCurrentScreen();
            }

            if (this.isContainerScreenOpen()) {
               this.client.player.closeHandledScreen();
            }
         } catch (Throwable e) {
         }
      }
   }

   private static boolean isGreenConfirmItem(ItemStack stack) {
      return stack != null
         && !stack.isEmpty()
         && (
            stack.isOf(Items.LIME_STAINED_GLASS_PANE)
               || stack.isOf(Items.GREEN_STAINED_GLASS_PANE)
               || stack.isOf(Items.LIME_WOOL)
               || stack.isOf(Items.GREEN_WOOL)
               || stack.isOf(Items.LIME_DYE)
               || stack.isOf(Items.EMERALD)
               || stack.isOf(Items.EMERALD_BLOCK)
         );
   }

   static boolean isConfirmLabel(String declineYesText) {
      if (declineYesText == null) {
         return false;
      } else {
         String trim = KmbtSellerUtils.formatText(declineYesText).toLowerCase(Locale.ROOT).trim();
         return !trim.contains("cancel") && !trim.contains("deny") && !trim.contains("decline") && !trim.startsWith("no")
            ? trim.contains("yes") || trim.contains("confirm") || trim.contains("accept")
            : false;
      }
   }

   private int findNextPageSlot() {
      ScreenHandler currentScreenHandler = this.client.player.currentScreenHandler;
      int pageSlotValue = currentScreenHandler.slots.size() - 36;

      for (int i = pageSlotValue - 1; i >= 0; i--) {
         ItemStack stack = currentScreenHandler.getSlot(i).getStack();
         if (!stack.isEmpty() && KmbtSellerUtils.formatText(stack.getName().getString()).toLowerCase(Locale.ROOT).contains("next page")) {
            List linesLore = getNameAndLoreLines(stack);
            if (i >= pageSlotValue - 9 || !KmbtSellerUtils.canPriceRoot(linesLore.subList(1, linesLore.size()), "$")) {
               return i;
            }
         }
      }

      return -1;
   }

   private boolean tryAlternateSearch() {
      if (this.goods == PilotAhBuyer.Goods.XP && !this.triedAlternateSearch && !this.sawListing) {
         this.triedAlternateSearch = true;
         this.searchCommand = getSearchCommand(this.goods, true);
         this.enterPhase(PilotAhBuyer.Phase.SEARCH);
         return true;
      } else {
         return false;
      }
   }

   public static boolean isDuration3Rocket(ItemStack stack) {
      if (stack != null && !stack.isEmpty() && stack.isOf(Items.FIREWORK_ROCKET)) {
         FireworksComponent fireworksComponent = stack.get(DataComponentTypes.FIREWORKS);
         return fireworksComponent != null && fireworksComponent.flightDuration() == 3 && fireworksComponent.explosions().isEmpty();
      } else {
         return false;
      }
   }

   private void pressKeyOnScreen(int screenReleasedValue) {
      if (this.client.currentScreen != null) {
         try {
            KeyInput keyInput = new KeyInput(screenReleasedValue, 0, 0);
            this.client.currentScreen.keyPressed(keyInput);
            this.client.currentScreen.keyReleased(keyInput);
         } catch (Throwable e) {
         }
      }
   }

   public static boolean isExperienceBottle(ItemStack stack) {
      return stack != null && !stack.isEmpty() && stack.isOf(Items.EXPERIENCE_BOTTLE);
   }

   private static List<String> getNameAndLoreLines(ItemStack stack) {
      ArrayList arrayList = new ArrayList();
      arrayList.add(stack.getName().getString());
      LoreComponent loreComponent = stack.get(DataComponentTypes.LORE);
      if (loreComponent != null) {
         for (Text text : loreComponent.lines()) {
            arrayList.add(text.getString());
         }
      }

      return arrayList;
   }

   private void finishRun(PilotAhBuyer.Status status, String doneStoppedText) {
      String fireworkRocketText = doneStoppedText == null ? "" : doneStoppedText;
      boolean doneStoppedFlag = this.phase == PilotAhBuyer.Phase.WAIT_BOARD || this.tickCount - this.searchSentTick < 60;
      this.stopReason = fireworkRocketText;
      this.status = status;
      if (status == PilotAhBuyer.Status.DONE) {
         this.statusMessage = this.boughtCount > 0 ? "Bought " + this.boughtCount + " " + this.formatItemNoun(this.boughtCount) : "Done";
      } else {
         this.statusMessage = "Stopped - " + (fireworkRocketText.isEmpty() ? "failed" : fireworkRocketText);
      }

      this.selectedListing = null;
      this.chatQueue.clear();
      this.closeAhScreens();
      InventoryStackUtilsHandle.handle(this);
      this.menuWaitTicks = this.ahMenuRequested && doneStoppedFlag ? 60 : 0;
   }

   private int computePageContentHash() {
      ScreenHandler currentScreenHandler = this.client.player.currentScreenHandler;
      int slotStack = 1;
      int slotStackCounter = 0;

      for (int i = currentScreenHandler.slots.size() - 36; slotStackCounter < i; slotStackCounter++) {
         ItemStack stack = currentScreenHandler.getSlot(slotStackCounter).getStack();
         slotStack = 31 * slotStack + (stack.isEmpty() ? 0 : Objects.hash(stack.getItem(), stack.getCount(), getNameAndLoreLines(stack)));
      }

      return slotStack;
   }

   public PilotAhBuyer() {
      this.triedListings = new HashSet<>();
      this.pageListings = new ArrayList<>();
      this.status = PilotAhBuyer.Status.IDLE;
      this.statusMessage = "";
      this.stopReason = "";
      this.goods = PilotAhBuyer.Goods.ROCKETS;
      this.phase = PilotAhBuyer.Phase.SEARCH;
      this.searchCommand = "ah firework rocket";
      this.syncId = -1;
      this.lastUnconfirmedBaseline = -1;
   }

   public String getStopReason() {
      return this.stopReason;
   }

   private void retryOrFail(String fireworkRocketText, boolean chargedShowedFlag) {
      this.stopReason = fireworkRocketText;
      this.selectedListing = null;
      if (++this.retryCount >= 3) {
         this.finishRun(PilotAhBuyer.Status.FAILED, fireworkRocketText);
      } else {
         this.statusMessage = "Retrying - " + fireworkRocketText;
         if (chargedShowedFlag && this.isAuctionBoardOpen()) {
            this.syncScreenAndScheduleScan();
            this.enterPhase(PilotAhBuyer.Phase.SCAN);
         } else {
            this.retryAtTick = this.tickCount + 40 + randomBelow(20);
            this.enterPhase(PilotAhBuyer.Phase.RETRY_WAIT);
         }
      }
   }

   private int getTicksInPhase() {
      return this.tickCount - this.phaseStartTick;
   }

   private boolean failWithReason(String fireworkRocketText) {
      this.stopReason = fireworkRocketText;
      this.statusMessage = "Stopped - " + fireworkRocketText;
      this.status = PilotAhBuyer.Status.FAILED;
      return false;
   }

   private boolean settleUnconfirmedPurchase() {
      if (this.selectedListing != null
         && (this.phase == PilotAhBuyer.Phase.CONFIRM || this.phase == PilotAhBuyer.Phase.VERIFY || this.phase == PilotAhBuyer.Phase.CREDIT)) {
         int phaseCreditValue = Math.max(this.phase == PilotAhBuyer.Phase.CREDIT ? this.peakItemsGained : 0, this.getHeldGoodsCount() - this.inventoryCountBeforeClick);
         if (phaseCreditValue <= 0) {
            return true;
         } else {
            TotalPriceData selectedListing18 = this.selectedListing;
            this.selectedListing = null;
            return this.recordPurchase(phaseCreditValue, selectedListing18.totalPrice());
         }
      } else {
         return true;
      }
   }

   private void runSearchPhase() {
      if (this.isConfirmDialogOpen()) {
         if (this.isActionCooldownOver()) {
            this.closeCurrentScreen();
            this.markActionTick();
         }
      } else if (this.isContainerScreenOpen()) {
         if (this.isActionCooldownOver()) {
            this.client.player.closeHandledScreen();
            this.markActionTick();
         }
      } else {
         Screen currentScreen = this.client.currentScreen;
         if (currentScreen != null && !(currentScreen instanceof GameMenuScreen)) {
            this.statusMessage = "Waiting for the open menu to close";
            if (this.getTicksInPhase() > 100) {
               this.retryOrFail("a menu stayed open", false);
            }
         } else if (this.isActionCooldownOver()) {
            if (!InventoryStackUtilsHandle.canFlagForObject(this)) {
               this.statusMessage = "Waiting for the inventory to be free";
               if (this.getTicksInPhase() > 100) {
                  this.retryOrFail("the inventory is busy", false);
               }
            } else {
               int waitingMenuValue = getElapsedTicks();
               if (System.nanoTime() - lastSearchNanos >= 1500000000L && COMMAND_LIMITER.canMaxForInt(waitingMenuValue)) {
                  lastSearchNanos = System.nanoTime();
                  COMMAND_LIMITER.handleMax(waitingMenuValue, 20 + randomBelow(20));
                  this.client.player.networkHandler.sendChatCommand(this.searchCommand);
                  this.ahMenuRequested = true;
                  this.searchSentTick = this.tickCount;
                  this.markActionTick();
                  this.syncId = -1;
                  this.pagesTurned = 0;
                  this.reachedLastPage = false;
                  this.sawListing = false;
                  this.smallLotsAvailable = false;
                  this.snipedStreak = 0;
                  this.selectedListing = null;
                  this.triedListings.clear();
                  this.statusMessage = "Searching /ah for " + this.formatItemNoun(2);
                  this.enterPhase(PilotAhBuyer.Phase.WAIT_BOARD);
               } else {
                  this.statusMessage = "Waiting to search /ah";
               }
            }
         }
      }
   }

   private void runScanPhase() {
      if (!this.handleBoardClosedOrChanged()) {
         if (this.tickCount >= this.scanReadyTick && this.isActionCooldownOver()) {
            int goodsInventory = this.getHeldGoodsCount();
            if (goodsInventory >= this.targetItemCount) {
               this.finishRun(PilotAhBuyer.Status.DONE, "");
            } else {
               int deltaMax = this.purchaseLimit - this.unconfirmedItemTotal;
               if (goodsInventory >= deltaMax) {
                  this.finishRun(PilotAhBuyer.Status.DONE, "at the stack cap");
               } else {
                  long listingAffordableValue = 0L;
                  if (this.totalBudget > 0L) {
                     listingAffordableValue = this.totalBudget - this.totalSpent;
                     if (listingAffordableValue <= 0L) {
                        this.finishRun(PilotAhBuyer.Status.DONE, "budget used up");
                        return;
                     }
                  }

                  int inventoryStack = this.countFreeInventorySlots();
                  if (inventoryStack == 0) {
                     this.finishRun(this.boughtCount > 0 ? PilotAhBuyer.Status.DONE : PilotAhBuyer.Status.FAILED, "inventory full");
                  } else {
                     this.collectPageListings();
                     int totalPriceBound = inventoryStack * 64;
                     Optional totalPrice = chooseBestListing(this.pageListings, goodsInventory, this.targetItemCount, deltaMax, this.maxUnitPrice, listingAffordableValue, totalPriceBound, this.allowSmallLots ? 1 : 16);
                     if (totalPrice.isPresent()) {
                        this.selectedListing = (TotalPriceData)totalPrice.get();
                        this.clickDelayTicks = 4 + randomBelow(3);
                        this.statusMessage = "Buying "
                           + this.selectedListing.count()
                           + " "
                           + this.formatItemNoun(this.selectedListing.count())
                           + " for "
                           + BuyerAppendUtils.formatText(this.selectedListing.totalPrice());
                        this.enterPhase(PilotAhBuyer.Phase.CLICK);
                     } else {
                        boolean listingAffordableFlag = !this.allowSmallLots
                           && chooseBestListing(this.pageListings, goodsInventory, this.targetItemCount, deltaMax, this.maxUnitPrice, listingAffordableValue, totalPriceBound, 1).isPresent();
                        if (listingAffordableFlag) {
                           this.smallLotsAvailable = true;
                        }

                        if (!this.reachedLastPage && this.pagesTurned < 7) {
                           int pageSlot = this.findNextPageSlot();
                           if (pageSlot >= 0) {
                              this.pageContentHash = this.computePageContentHash();
                              this.client.interactionManager.clickSlot(this.syncId, pageSlot, 0, SlotActionType.PICKUP, this.client.player);
                              this.markActionTick();
                              this.pagesTurned++;
                              this.statusMessage = "Next page (" + (this.pagesTurned + 1) + ")";
                              this.enterPhase(PilotAhBuyer.Phase.PAGE_WAIT);
                              return;
                           }
                        }

                        if (this.smallLotsAvailable && !this.allowSmallLots) {
                           this.allowSmallLots = true;
                           if (!listingAffordableFlag) {
                              this.enterPhase(PilotAhBuyer.Phase.SEARCH);
                           }
                        } else if (!this.tryAlternateSearch()) {
                           this.retryOrFail(this.sawListing ? "no affordable listing" : "no listing", false);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private int getHeldGoodsCount() {
      return countGoodsInInventory(this.client.player.getInventory(), this.goods);
   }

   private void runCreditPhase() {
      int max = Math.max(this.peakItemsGained, this.getHeldGoodsCount() - this.inventoryCountBeforeClick);
      this.peakItemsGained = max;
      if (this.findReceiptByCount(max) != null || this.tickCount - this.purchaseCheckTick >= 4) {
         int purchaseValue = this.selectedListing == null ? max : this.selectedListing.count();
         long l2 = this.selectedListing == null ? 0L : this.selectedListing.totalPrice();
         if (max > purchaseValue) {
            this.unconfirmedItemTotal = Math.max(0, this.unconfirmedItemTotal - (max - purchaseValue));
         }

         if (this.recordPurchase(max, l2)) {
            this.selectedListing = null;
            this.closeCurrentScreen();
            if (this.getHeldGoodsCount() >= this.targetItemCount) {
               this.finishRun(PilotAhBuyer.Status.DONE, "");
            } else if (this.isAuctionBoardOpen()) {
               this.syncScreenAndScheduleScan();
               this.enterPhase(PilotAhBuyer.Phase.SCAN);
            } else {
               this.enterPhase(PilotAhBuyer.Phase.SEARCH);
            }
         }
      }
   }

   public PilotAhBuyer.Goods getGoods() {
      return this.goods;
   }

   private int countFreeInventorySlots() {
      PlayerInventory inventory = this.client.player.getInventory();
      int inventoryStack = 0;

      for (int i = 0; i < 36; i++) {
         if (inventory.getStack(i).isEmpty()) {
            inventoryStack++;
         }
      }

      return inventoryStack;
   }

   private boolean isConfirmDialogOpen() {
      if (this.client == null) {
         return false;
      } else {
         Screen currentScreen = this.client.currentScreen;
         return currentScreen != null && !(currentScreen instanceof HandledScreen) && !(currentScreen instanceof ChatScreen) && !(currentScreen instanceof GameMenuScreen)
            ? getLowerCaseTitle(currentScreen).contains("sure")
            : false;
      }
   }

   private static int getElapsedTicks() {
      return (int)((System.nanoTime() - START_NANOS) / 50000000L);
   }

   private void onListingSniped() {
      this.wasSniped = false;
      if (this.selectedListing != null) {
         this.triedListings.add(this.selectedListing);
      }

      this.selectedListing = null;
      this.snipedStreak++;
      this.statusMessage = "Sniped - trying the next listing";
      if (this.isConfirmDialogOpen()) {
         this.closeCurrentScreen();
         this.markActionTick();
      }

      if (this.snipedStreak >= 8) {
         this.snipedStreak = 0;
         this.enterPhase(PilotAhBuyer.Phase.SEARCH);
      } else if (this.isAuctionBoardOpen()) {
         this.syncScreenAndScheduleScan();
         this.enterPhase(PilotAhBuyer.Phase.SCAN);
      } else {
         this.enterPhase(PilotAhBuyer.Phase.SEARCH);
      }
   }

   private boolean recordPurchase(int chargedShowedValue, long l2) {
      KmbtSellerUtils.ChatPurchase fullInventory = this.findReceiptByCount(chargedShowedValue);
      if (fullInventory != null) {
         this.recentChatPurchases.remove(fullInventory);
      }

      long buyerAppendValue = fullInventory != null ? fullInventory.coins() : l2;
      this.totalSpent += buyerAppendValue;
      this.purchaseCount++;
      this.boughtCount += chargedShowedValue;
      this.retryCount = 0;
      this.boardClosedCount = 0;
      this.statusMessage = "Bought " + chargedShowedValue + " " + this.formatItemNoun(chargedShowedValue) + " for " + BuyerAppendUtils.formatText(buyerAppendValue);
      if (LimitBuyerUtils.canFlag(buyerAppendValue, l2)) {
         this.overcharged = true;
         this.finishRun(PilotAhBuyer.Status.FAILED, "charged more than the listing showed");
         return false;
      } else {
         return true;
      }
   }

   public boolean isOvercharged() {
      return this.overcharged;
   }

   public void stopRun() {
      boolean phaseFlag = this.status == PilotAhBuyer.Status.RUNNING;
      this.client = MinecraftClient.getInstance();
      if (phaseFlag) {
         try {
            if (this.selectedListing != null
               && this.phase == PilotAhBuyer.Phase.VERIFY
               && this.client != null
               && this.client.player != null
               && this.getHeldGoodsCount() <= this.inventoryCountBeforeClick) {
               this.totalSpent = this.totalSpent + this.selectedListing.totalPrice();
               this.purchaseCount++;
            } else {
               this.settleUnconfirmedPurchase();
            }
         } catch (Throwable e) {
         }
      }

      boolean phaseFlag2 = phaseFlag && (this.phase == PilotAhBuyer.Phase.WAIT_BOARD || this.tickCount - this.searchSentTick < 60);
      this.status = PilotAhBuyer.Status.IDLE;
      this.statusMessage = "";
      this.selectedListing = null;
      this.chatQueue.clear();
      if (phaseFlag) {
         this.closeAhScreens();
         this.menuWaitTicks = this.ahMenuRequested && phaseFlag2 ? 60 : 0;
      }

      InventoryStackUtilsHandle.handle(this);
   }

   private void runPageWaitPhase() {
      if (!this.isAuctionBoardOpen()) {
         if (this.getTicksInPhase() > 30) {
            this.handleBoardClosedOrChanged();
         }
      } else if (this.client.player.currentScreenHandler.syncId == this.syncId && this.computePageContentHash() == this.pageContentHash) {
         if (this.getTicksInPhase() > 30) {
            this.reachedLastPage = true;
            this.scanReadyTick = this.tickCount;
            this.enterPhase(PilotAhBuyer.Phase.SCAN);
         }
      } else {
         this.triedListings.clear();
         this.syncScreenAndScheduleScan();
         this.enterPhase(PilotAhBuyer.Phase.SCAN);
      }
   }

   public boolean startBuying(PilotAhBuyer.Goods goods2, int inventoryEnoughBound, int inventoryEnoughLimit, long inventoryEnoughBound2, long cheapHigh) {
      if (this.status == PilotAhBuyer.Status.RUNNING) {
         this.stopRun();
      }

      this.client = MinecraftClient.getInstance();
      this.goods = goods2 == null ? PilotAhBuyer.Goods.ROCKETS : goods2;
      this.purchaseLimit = inventoryEnoughLimit > 0 ? inventoryEnoughLimit : Math.max(0, inventoryEnoughBound);
      this.targetItemCount = Math.min(Math.max(0, inventoryEnoughBound), this.purchaseLimit);
      this.maxUnitPrice = Math.max(0L, inventoryEnoughBound2);
      this.totalBudget = Math.max(0L, cheapHigh);
      this.searchCommand = getSearchCommand(this.goods, false);
      this.chatQueue.clear();
      this.recentChatPurchases.clear();
      this.triedListings.clear();
      this.pageListings.clear();
      this.tickCount = 0;
      this.phaseStartTick = 0;
      this.lastActionTick = -1000;
      this.clickDelayTicks = 4;
      this.scanReadyTick = 0;
      this.retryAtTick = 0;
      this.searchSentTick = -1000;
      this.menuWaitTicks = 0;
      this.syncId = -1;
      this.pagesTurned = 0;
      this.pageContentHash = 0;
      this.retryCount = 0;
      this.snipedStreak = 0;
      this.boardClosedCount = 0;
      this.confirmKeyStep = 0;
      this.confirmAttemptCount = 0;
      this.purchaseCheckTick = 0;
      this.peakItemsGained = 0;
      this.triedAlternateSearch = false;
      this.sawListing = false;
      this.smallLotsAvailable = false;
      this.allowSmallLots = false;
      this.reachedLastPage = false;
      this.ahMenuRequested = false;
      this.wasSniped = false;
      this.chatFailureReason = null;
      this.selectedListing = null;
      this.inventoryCountBeforeClick = 0;
      this.unconfirmedItemTotal = 0;
      this.lastUnconfirmedBaseline = -1;
      this.lastUnconfirmedCount = 0;
      this.lastUnconfirmedPrice = 0L;
      this.totalSpent = 0L;
      this.boughtCount = 0;
      this.purchaseCount = 0;
      this.outOfMoney = false;
      this.stopReason = "";
      this.overcharged = false;
      this.lastUnconfirmedTick = 0;
      this.phase = PilotAhBuyer.Phase.SEARCH;

      try {
         if (this.client == null || this.client.player == null || this.client.interactionManager == null || this.client.player.networkHandler == null) {
            return this.failWithReason("no player");
         }

         int stackRockets = countGoodsInInventory(this.client.player.getInventory(), this.goods);
         if (this.targetItemCount <= 0 || stackRockets >= this.targetItemCount) {
            return this.failWithReason("already have enough " + this.formatItemNoun(2));
         }

         if (this.countFreeInventorySlots() == 0) {
            return this.failWithReason("no free inventory slot");
         }
      } catch (Throwable e) {
         return this.failWithReason("error: " + e.getClass().getSimpleName());
      }

      this.statusMessage = "Searching /ah for " + this.formatItemNoun(2);
      this.status = PilotAhBuyer.Status.RUNNING;
      return true;
   }

   private KmbtSellerUtils.ChatPurchase findReceiptByCount(int fullInventoryValue) {
      for (int i = this.recentChatPurchases.size() - 1; i >= 0; i--) {
         if (this.recentChatPurchases.get(i).count() == fullInventoryValue) {
            return this.recentChatPurchases.get(i);
         }
      }

      return null;
   }

   private static String getLowerCaseTitle(Screen screen) {
      if (screen == null) {
         return "";
      } else {
         Text title = screen.getTitle();
         return title == null ? "" : KmbtSellerUtils.formatText(title.getString()).toLowerCase(Locale.ROOT);
      }
   }

   private String formatItemNoun(int rocketBottlesValue) {
      if (this.goods == PilotAhBuyer.Goods.XP) {
         return rocketBottlesValue == 1 ? "XP bottle" : "XP bottles";
      } else {
         return rocketBottlesValue == 1 ? "rocket" : "rockets";
      }
   }

   private void markActionTick() {
      this.lastActionTick = this.tickCount;
   }

   public int getPurchaseCount() {
      return this.purchaseCount;
   }

   private void runVerifyPhase() {
      if (this.wasSniped) {
         this.onListingSniped();
      } else if (this.getHeldGoodsCount() > this.inventoryCountBeforeClick) {
         this.beginPurchaseCheck();
      } else {
         this.statusMessage = "Waiting for the item";
         if (this.confirmAttemptCount >= 3 || this.tickCount - this.lastActionTick < 12 || !this.isConfirmDialogOpen() && !this.isConfirmScreenOpen()) {
            if (this.getTicksInPhase() > 60) {
               this.onPurchaseUnconfirmed("the item never arrived");
            }
         } else {
            this.confirmKeyStep = 0;
            this.enterPhase(PilotAhBuyer.Phase.CONFIRM);
         }
      }
   }

   private void enterPhase(PilotAhBuyer.Phase phase) {
      this.phase = phase;
      this.phaseStartTick = this.tickCount;
   }

   private void drainChatQueue() {
      String enoughMoneyText;
      while ((enoughMoneyText = this.chatQueue.poll()) != null) {
         switch (classifyChatMessage(enoughMoneyText)) {
            case SNIPED:
               this.wasSniped = true;
               break;
            case NO_MONEY:
               this.chatFailureReason = "not enough money";
               break;
            case INVENTORY_FULL:
               if (this.chatFailureReason == null) {
                  this.chatFailureReason = "inventory full";
               }
               break;
            case RECEIPT:
               KmbtSellerUtils.ChatPurchase groupParseData = parseGoodsReceipt(enoughMoneyText, this.goods);
               if (groupParseData != null) {
                  if (this.recentChatPurchases.size() >= 8) {
                     this.recentChatPurchases.remove(0);
                  }

                  this.recentChatPurchases.add(groupParseData);
               }
         }
      }
   }

   private boolean tryClickConfirmButton() {
      ScreenHandler currentScreenHandler = this.client.player.currentScreenHandler;
      int slotLimit = currentScreenHandler.slots.size() - 36;
      int slotValue = -1;

      for (int i = 0; i < 2 && slotValue < 0; i++) {
         for (int j = 0; j < slotLimit && slotValue < 0; j++) {
            ItemStack stack = currentScreenHandler.getSlot(j).getStack();
            if (!stack.isEmpty() && !isGoodsItem(stack, this.goods)) {
               List linesLore = getNameAndLoreLines(stack);
               if (!KmbtSellerUtils.canPriceRoot(linesLore.subList(1, linesLore.size()), "$") && (i == 0 ? isConfirmLabel((String)linesLore.get(0)) : isGreenConfirmItem(stack))) {
                  slotValue = j;
               }
            }
         }
      }

      if (slotValue < 0) {
         return false;
      } else {
         this.client.interactionManager.clickSlot(currentScreenHandler.syncId, slotValue, 0, SlotActionType.PICKUP, this.client.player);
         return true;
      }
   }

   static String getSearchCommand(PilotAhBuyer.Goods goods, boolean enchantingFireworkFlag) {
      if (goods == PilotAhBuyer.Goods.XP) {
         return enchantingFireworkFlag ? "ah enchanting" : "ah bottle o' enchanting";
      } else {
         return "ah firework rocket";
      }
   }

   public PilotAhBuyer.Status getStatus() {
      return this.status;
   }

   static KmbtSellerUtils.ChatPurchase parseGoodsReceipt(String groupParseText, PilotAhBuyer.Goods goods) {
      Optional empty = KmbtSellerUtils.getEmptyForString(groupParseText);
      return !empty.isEmpty() && isGoodsItemName(((KmbtSellerUtils.ChatPurchase)empty.get()).item(), goods) ? (KmbtSellerUtils.ChatPurchase)empty.get() : null;
   }

   static boolean isGoodsItem(ItemStack stack, PilotAhBuyer.Goods goods) {
      return goods == PilotAhBuyer.Goods.XP ? isExperienceBottle(stack) : isDuration3Rocket(stack);
   }

   public long getTotalSpent() {
      return this.totalSpent;
   }

   private boolean isConfirmScreenOpen() {
      if (!this.isContainerScreenOpen()) {
         return false;
      } else {
         String formatInstanceScreen = this.getCurrentScreenTitle();
         if (formatInstanceScreen.contains("sure") || formatInstanceScreen.contains("confirm")) {
            return true;
         } else {
            return formatInstanceScreen.contains("auction")
               ? false
               : this.client.player.currentScreenHandler.syncId != this.syncId || this.hasGreenOrRedPane(true) && this.hasGreenOrRedPane(false);
         }
      }
   }

   static boolean isGoodsItemName(String fireworkrocketStarText, PilotAhBuyer.Goods goods) {
      if (fireworkrocketStarText != null && goods != null) {
         String trim = KmbtSellerUtils.formatText(fireworkrocketStarText).toLowerCase(Locale.ROOT).trim();
         if (goods == PilotAhBuyer.Goods.XP) {
            return trim.startsWith("bottle") && trim.contains("enchanting");
         } else {
            StringBuilder stringBuilder = new StringBuilder(trim.length());

            for (int i = 0; i < trim.length(); i++) {
               if (Character.isLetterOrDigit(trim.charAt(i))) {
                  stringBuilder.append(trim.charAt(i));
               }
            }

            String fireworkrocketStarText2 = stringBuilder.toString();
            return fireworkrocketStarText2.startsWith("fireworkrocket") && !fireworkrocketStarText2.contains("star");
         }
      } else {
         return false;
      }
   }

   private TotalPriceData readListingAtSlot(ScreenHandler screenHandler, int goodsSlotLimit) {
      if (screenHandler != null && goodsSlotLimit >= 0 && goodsSlotLimit < screenHandler.slots.size() - 36) {
         ItemStack stack = screenHandler.getSlot(goodsSlotLimit).getStack();
         if (!isGoodsItem(stack, this.goods)) {
            return null;
         } else {
            OptionalLong empty = parseListingPrice(getNameAndLoreLines(stack));
            return empty.isEmpty() ? null : new TotalPriceData(goodsSlotLimit, Math.max(1, stack.getCount()), empty.getAsLong());
         }
      } else {
         return null;
      }
   }

   private void runWaitBoardPhase() {
      if (!this.isAuctionBoardOpen()) {
         this.statusMessage = "Waiting for the auction board";
         if (this.getTicksInPhase() > 100 && !this.tryAlternateSearch()) {
            this.retryOrFail("no auction board opened", false);
         }
      } else {
         this.syncScreenAndScheduleScan();
         this.statusMessage = "Reading the auction board";
         this.enterPhase(PilotAhBuyer.Phase.SCAN);
      }
   }

   private void beginPurchaseCheck() {
      this.purchaseCheckTick = this.tickCount;
      this.peakItemsGained = this.getHeldGoodsCount() - this.inventoryCountBeforeClick;
      this.statusMessage = "Checking the purchase";
      this.enterPhase(PilotAhBuyer.Phase.CREDIT);
   }

   private static boolean isRedCancelItem(ItemStack stack) {
      return stack != null && !stack.isEmpty() && (stack.isOf(Items.RED_STAINED_GLASS_PANE) || stack.isOf(Items.RED_WOOL) || stack.isOf(Items.BARRIER));
   }

   private boolean hasGoodsListing() {
      ScreenHandler currentScreenHandler = this.client.player.currentScreenHandler;
      int goodsSlotCounter = 0;

      for (int i = currentScreenHandler.slots.size() - 36; goodsSlotCounter < i; goodsSlotCounter++) {
         if (isGoodsItem(currentScreenHandler.getSlot(goodsSlotCounter).getStack(), this.goods)) {
            return true;
         }
      }

      return false;
   }

   private void collectPageListings() {
      this.pageListings.clear();
      ScreenHandler currentScreenHandler = this.client.player.currentScreenHandler;
      int fullInventoryCounter = 0;

      for (int i = currentScreenHandler.slots.size() - 36; fullInventoryCounter < i; fullInventoryCounter++) {
         if (isGoodsItem(currentScreenHandler.getSlot(fullInventoryCounter).getStack(), this.goods)) {
            this.sawListing = true;
            TotalPriceData goodsSlot = this.readListingAtSlot(currentScreenHandler, fullInventoryCounter);
            if (goodsSlot != null && !this.triedListings.contains(goodsSlot)) {
               this.pageListings.add(goodsSlot);
            }
         }
      }
   }

   private void runConfirmPhase() {
      if (this.wasSniped) {
         this.onListingSniped();
      } else if (this.getHeldGoodsCount() > this.inventoryCountBeforeClick) {
         this.beginPurchaseCheck();
      } else if (this.isConfirmDialogOpen()) {
         this.statusMessage = "Confirming";
         if (this.isActionCooldownOver()) {
            if (this.confirmKeyStep == 0) {
               this.pressKeyOnScreen(263);
               this.confirmKeyStep = 1;
               this.markActionTick();
            } else {
               this.pressKeyOnScreen(257);
               this.confirmKeyStep = 0;
               this.confirmAttemptCount++;
               this.markActionTick();
               this.enterPhase(PilotAhBuyer.Phase.VERIFY);
            }
         }
      } else {
         if (this.isConfirmScreenOpen()) {
            this.statusMessage = "Confirming";
            if (!this.isActionCooldownOver()) {
               return;
            }

            if (this.tryClickConfirmButton()) {
               this.confirmAttemptCount++;
               this.markActionTick();
               this.enterPhase(PilotAhBuyer.Phase.VERIFY);
               return;
            }
         }

         if (this.getTicksInPhase() > 60) {
            this.onPurchaseUnconfirmed("no confirm showed up");
         }
      }
   }

   public boolean isOutOfMoney() {
      return this.outOfMoney;
   }

   static enum ChatKind {
      NONE,
      SNIPED,
      NO_MONEY,
      INVENTORY_FULL,
      RECEIPT;
   }

   public static enum Goods {
      ROCKETS,
      XP;
   }

   private static enum Phase {
      SEARCH,
      WAIT_BOARD,
      SCAN,
      PAGE_WAIT,
      CLICK,
      CONFIRM,
      VERIFY,
      CREDIT,
      RETRY_WAIT;
   }

   public static enum Status {
      IDLE,
      RUNNING,
      DONE,
      FAILED;
   }
}
