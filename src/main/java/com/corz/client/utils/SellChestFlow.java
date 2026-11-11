package com.corz.client.utils;

public final class SellChestFlow {
   public static final String MOVE_CHEST_STOPPED = "Items would not move out of the chest - stopped.";
   private final int sellDelayTicks;
   private int sellableBeforeSell;
   public static final String CLOSE_MENU_LOOK = "Close the menu first, then look at the chest.";
   public static final String SELL_OPENED_MENU = "/sell opened a menu that is not the sell menu.";
   public static final String CHEST_DID_NOT_OPEN = "The chest did not open.";
   private int chestOpenAttempts;
   public static final String SELL_MENU_CLOSED = "The sell menu was closed - Auto Sell Chest stopped.";
   public static final String ANOTHER_SCREEN_OPENED = "Another screen opened - Auto Sell Chest stopped.";
   public static final String LOOK_CHEST_BARREL = "Look at a chest or barrel within 4.5 blocks.";
   private boolean chestInteractionStarted;
   private int nextTransferTick;
   private boolean transferPending;
   private int sellableItemsAtStart;
   public static final String CHEST_EMPTY_NOTHING = "The chest is empty - nothing to sell.";
   public static final String SELL_MENU_REJECTED_ITEMS = "The sell menu did not take these items - stopped.";
   private SellChestFlow.Phase phase = SellChestFlow.Phase.OPEN_CHEST;
   private int sellMenuDeadlineTick;
   private int actionDelayUntilTick;
   public static final String INVENTORY_FULL_MESSAGE = "Inventory full of your own items - make room.";
   public static final String MOVED_STOPPED_MESSAGE = "You moved - Auto Sell Chest stopped.";
   private int startTick = Integer.MIN_VALUE;
   public static final String CHEST_CLOSED_STOPPED = "The chest was closed - Auto Sell Chest stopped.";
   private int sellRoundCount;
   public static final String DIED_STOPPED_MESSAGE = "You died - Auto Sell Chest stopped.";
   private int totalStacksSold;
   private int lastItemCount;
   private boolean chestEmptied;
   public static final String SELL_DID_NOT_OPEN = "/sell did not open the sell menu.";
   private int stalledTransferCount;
   private int screenOpenedTick = -1;

   private SellChestFlow.Action stepDeposit(SellChestFlow.View view) {
      if (view.screen() != SellChestFlow.Screen.CONTAINER) {
         return this.failAction("The sell menu was closed - Auto Sell Chest stopped.", false);
      } else if (!view.busy() && view.tick() >= this.nextTransferTick) {
         this.trackTransferProgress(view.sellableItems());
         if (view.sellable() > 0 && view.gridRoom() && this.stalledTransferCount < 4) {
            this.transferPending = true;
            this.nextTransferTick = view.tick() + this.sellDelayTicks;
            return SellChestFlow.Action.ofKind(SellChestFlow.Action.Kind.DEPOSIT);
         } else if (view.sellableItems() >= this.sellableItemsAtStart) {
            return this.failAction("The sell menu did not take these items - stopped.", true);
         } else if (view.button()) {
            this.enterPhase(SellChestFlow.Phase.CONFIRM);
            this.actionDelayUntilTick = view.tick() + 12;
            return SellChestFlow.Action.ofKind(SellChestFlow.Action.Kind.CLICK_SELL);
         } else {
            this.enterPhase(SellChestFlow.Phase.AFTER_SELL);
            this.actionDelayUntilTick = view.tick() + 8;
            return SellChestFlow.Action.ofKind(SellChestFlow.Action.Kind.CLOSE);
         }
      } else {
         return SellChestFlow.Action.INSTANCE;
      }
   }

   private SellChestFlow.Action stepOpenChest(SellChestFlow.View view) {
      if (view.tick() < this.actionDelayUntilTick) {
         return SellChestFlow.Action.INSTANCE;
      } else if (view.screen() != SellChestFlow.Screen.NONE) {
         return !this.chestInteractionStarted && view.tick() - this.startTick < 600
            ? SellChestFlow.Action.INSTANCE
            : this.failAction(
               this.chestInteractionStarted ? "Another screen opened - Auto Sell Chest stopped." : "Close the menu first, then look at the chest.", false
            );
      } else if (!view.aimOk()) {
         return this.failAction("Look at a chest or barrel within 4.5 blocks.", false);
      } else {
         this.chestOpenAttempts++;
         this.chestInteractionStarted = true;
         this.enterPhase(SellChestFlow.Phase.WAIT_CHEST);
         this.sellMenuDeadlineTick = view.tick() + 60;
         return SellChestFlow.Action.ofKind(SellChestFlow.Action.Kind.OPEN_CHEST);
      }
   }

   private SellChestFlow.Action failAction(String phaseDoneText, boolean phaseDoneFlag) {
      this.phase = SellChestFlow.Phase.DONE;
      return new SellChestFlow.Action(SellChestFlow.Action.Kind.FAIL, phaseDoneText, phaseDoneFlag);
   }

   private SellChestFlow.Action finishTakePhase(SellChestFlow.View view) {
      if (view.sellable() == 0) {
         return this.chestEmptied
            ? this.doneAction(this.totalStacksSold == 0 ? "The chest is empty - nothing to sell." : formatSoldMessage(this.totalStacksSold), true)
            : this.failAction("Inventory full of your own items - make room.", true);
      } else {
         this.enterPhase(SellChestFlow.Phase.OPEN_SELL);
         this.actionDelayUntilTick = view.tick() + 8;
         return SellChestFlow.Action.ofKind(SellChestFlow.Action.Kind.CLOSE);
      }
   }

   public SellChestFlow.Phase getPhase() {
      return this.phase;
   }

   private SellChestFlow.Action stepAfterSell(SellChestFlow.View view) {
      if (view.tick() < this.actionDelayUntilTick) {
         return SellChestFlow.Action.INSTANCE;
      } else if (view.screen() != SellChestFlow.Screen.NONE) {
         return this.failAction("Another screen opened - Auto Sell Chest stopped.", false);
      } else {
         int sellChestValue = this.sellableBeforeSell - view.sellable();
         if (sellChestValue <= 0) {
            return this.failAction("The sell menu did not take these items - stopped.", false);
         } else {
            this.totalStacksSold += sellChestValue;
            this.sellRoundCount++;
            if (!this.chestEmptied && this.sellRoundCount < 500) {
               this.enterPhase(SellChestFlow.Phase.OPEN_CHEST);
               this.chestOpenAttempts = 0;
               this.actionDelayUntilTick = view.tick() + 8;
               return SellChestFlow.Action.INSTANCE;
            } else {
               return this.doneAction(formatSoldMessage(this.totalStacksSold), false);
            }
         }
      }
   }

   private void enterPhase(SellChestFlow.Phase phase) {
      this.phase = phase;
      this.screenOpenedTick = -1;
      this.stalledTransferCount = 0;
      this.transferPending = false;
   }

   public boolean hasStartedChestInteraction() {
      return this.chestInteractionStarted;
   }

   public int getSellRoundCount() {
      return this.sellRoundCount;
   }

   private SellChestFlow.Action stepTake(SellChestFlow.View view) {
      if (view.screen() != SellChestFlow.Screen.CONTAINER) {
         return this.failAction(
            view.screen() == SellChestFlow.Screen.NONE ? "The chest was closed - Auto Sell Chest stopped." : "Another screen opened - Auto Sell Chest stopped.",
            false
         );
      } else if (!view.busy() && view.tick() >= this.nextTransferTick) {
         this.trackTransferProgress(view.chestItems());
         if (view.chestStacks() == 0) {
            this.chestEmptied = true;
            return this.finishTakePhase(view);
         } else if (this.stalledTransferCount >= 4 && view.invRoom() && view.sellable() == 0) {
            return this.failAction("Items would not move out of the chest - stopped.", true);
         } else if (view.invRoom() && this.stalledTransferCount < 4) {
            this.transferPending = true;
            this.nextTransferTick = view.tick() + this.sellDelayTicks;
            return SellChestFlow.Action.ofKind(SellChestFlow.Action.Kind.TAKE);
         } else {
            return this.finishTakePhase(view);
         }
      } else {
         return SellChestFlow.Action.INSTANCE;
      }
   }

   public int getTotalStacksSold() {
      return this.totalStacksSold;
   }

   public SellChestFlow(int sellChestBound) {
      this.sellDelayTicks = Math.max(1, sellChestBound);
   }

   private void trackTransferProgress(int chestOpenedValue) {
      if (this.transferPending) {
         if (chestOpenedValue < this.lastItemCount) {
            this.stalledTransferCount = 0;
         } else {
            this.stalledTransferCount++;
         }

         this.transferPending = false;
      }

      this.lastItemCount = chestOpenedValue;
   }

   private SellChestFlow.Action stepOpenSell(SellChestFlow.View view) {
      if (view.tick() < this.actionDelayUntilTick) {
         return SellChestFlow.Action.INSTANCE;
      } else if (view.screen() != SellChestFlow.Screen.NONE) {
         return this.failAction("Another screen opened - Auto Sell Chest stopped.", false);
      } else {
         this.sellableBeforeSell = view.sellable();
         this.enterPhase(SellChestFlow.Phase.WAIT_SELL);
         this.sellMenuDeadlineTick = view.tick() + 100;
         return SellChestFlow.Action.ofKind(SellChestFlow.Action.Kind.SELL_COMMAND);
      }
   }

   public static String formatSoldMessage(int appendValue) {
      return "Chest empty - sold " + appendValue + (appendValue == 1 ? " stack." : " stacks.");
   }

   private SellChestFlow.Action stepConfirm(SellChestFlow.View view) {
      if (view.screen() != SellChestFlow.Screen.CONTAINER) {
         this.enterPhase(SellChestFlow.Phase.AFTER_SELL);
         this.actionDelayUntilTick = view.tick() + 8;
         return SellChestFlow.Action.INSTANCE;
      } else if (view.busy() || view.tick() < this.actionDelayUntilTick) {
         return SellChestFlow.Action.INSTANCE;
      } else if (view.sellable() > 0 && view.gridRoom()) {
         this.enterDepositPhase(view);
         return SellChestFlow.Action.INSTANCE;
      } else {
         this.enterPhase(SellChestFlow.Phase.AFTER_SELL);
         this.actionDelayUntilTick = view.tick() + 8;
         return SellChestFlow.Action.ofKind(SellChestFlow.Action.Kind.CLOSE);
      }
   }

   private SellChestFlow.Action doneAction(String donePhaseText, boolean donePhaseFlag) {
      this.phase = SellChestFlow.Phase.DONE;
      return new SellChestFlow.Action(SellChestFlow.Action.Kind.DONE, donePhaseText, donePhaseFlag);
   }

   private SellChestFlow.Action stepWaitSell(SellChestFlow.View view) {
      if (view.screen() == SellChestFlow.Screen.CONTAINER) {
         if (this.screenOpenedTick < 0) {
            this.screenOpenedTick = view.tick();
         }

         if (view.tick() - this.screenOpenedTick < 4) {
            return SellChestFlow.Action.INSTANCE;
         } else if (!view.sellMenu()) {
            return this.failAction("/sell opened a menu that is not the sell menu.", true);
         } else {
            this.enterDepositPhase(view);
            return this.stepDeposit(view);
         }
      } else if (view.screen() == SellChestFlow.Screen.OTHER) {
         return this.failAction("Another screen opened - Auto Sell Chest stopped.", false);
      } else {
         return view.tick() >= this.sellMenuDeadlineTick ? this.failAction("/sell did not open the sell menu.", false) : SellChestFlow.Action.INSTANCE;
      }
   }

   private void enterDepositPhase(SellChestFlow.View view) {
      this.enterPhase(SellChestFlow.Phase.DEPOSIT);
      this.lastItemCount = view.sellableItems();
      this.sellableItemsAtStart = view.sellableItems();
      this.nextTransferTick = view.tick();
   }

   private SellChestFlow.Action stepWaitChest(SellChestFlow.View view) {
      if (view.screen() == SellChestFlow.Screen.CONTAINER) {
         if (this.screenOpenedTick < 0) {
            this.screenOpenedTick = view.tick();
         }

         if (view.tick() - this.screenOpenedTick < 4) {
            return SellChestFlow.Action.INSTANCE;
         } else {
            this.enterPhase(SellChestFlow.Phase.TAKE);
            this.lastItemCount = view.chestItems();
            this.nextTransferTick = view.tick();
            return this.stepTake(view);
         }
      } else if (view.screen() == SellChestFlow.Screen.OTHER) {
         return this.failAction("Another screen opened - Auto Sell Chest stopped.", false);
      } else {
         if (view.tick() >= this.sellMenuDeadlineTick) {
            if (this.chestOpenAttempts >= 2) {
               return this.failAction("The chest did not open.", false);
            }

            this.enterPhase(SellChestFlow.Phase.OPEN_CHEST);
            this.actionDelayUntilTick = view.tick() + 8;
         }

         return SellChestFlow.Action.INSTANCE;
      }
   }

   public SellChestFlow.Action nextAction(SellChestFlow.View view) {
      if (this.phase != SellChestFlow.Phase.DONE && view != null) {
         if (this.startTick == Integer.MIN_VALUE) {
            this.startTick = view.tick();
         }

         if (view.dead()) {
            return this.failAction("You died - Auto Sell Chest stopped.", false);
         } else if (this.chestInteractionStarted && view.moved()) {
            return this.failAction("You moved - Auto Sell Chest stopped.", view.screen() == SellChestFlow.Screen.CONTAINER);
         } else {
            return switch (this.phase) {
               case OPEN_CHEST -> this.stepOpenChest(view);
               case WAIT_CHEST -> this.stepWaitChest(view);
               case TAKE -> this.stepTake(view);
               case OPEN_SELL -> this.stepOpenSell(view);
               case WAIT_SELL -> this.stepWaitSell(view);
               case DEPOSIT -> this.stepDeposit(view);
               case CONFIRM -> this.stepConfirm(view);
               case AFTER_SELL -> this.stepAfterSell(view);
               case DONE -> SellChestFlow.Action.INSTANCE;
            };
         }
      } else {
         return SellChestFlow.Action.INSTANCE;
      }
   }

   public record Action(SellChestFlow.Action.Kind kind, String text, boolean close) {
      public static final SellChestFlow.Action INSTANCE = new SellChestFlow.Action(SellChestFlow.Action.Kind.NONE, null, false);

      static SellChestFlow.Action ofKind(SellChestFlow.Action.Kind kind) {
         return new SellChestFlow.Action(kind, null, false);
      }

      public static enum Kind {
         NONE,
         OPEN_CHEST,
         TAKE,
         CLOSE,
         SELL_COMMAND,
         DEPOSIT,
         CLICK_SELL,
         DONE,
         FAIL;
      }
   }

   public static enum Phase {
      OPEN_CHEST,
      WAIT_CHEST,
      TAKE,
      OPEN_SELL,
      WAIT_SELL,
      DEPOSIT,
      CONFIRM,
      AFTER_SELL,
      DONE;
   }

   public static enum Screen {
      NONE,
      CONTAINER,
      OTHER;
   }

   public record View(
      int tick,
      SellChestFlow.Screen screen,
      boolean sellMenu,
      boolean moved,
      boolean dead,
      boolean aimOk,
      int chestStacks,
      int chestItems,
      boolean invRoom,
      int sellable,
      int sellableItems,
      boolean gridRoom,
      boolean button,
      boolean busy
   ) {
   }
}
