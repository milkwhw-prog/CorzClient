package com.corz.client.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.OptionalLong;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;

public final class ConfirmScreenHelper {
   private int maxTimedBoughtCount;
   private int boughtValue;
   private int maxBoughtFullBound;
   private String confirm;
   private ConfirmScreenHelper.Status status;
   private static final MinecraftClient CLIENT = MinecraftClient.getInstance();
   private int boughtScreenValue;
   private static final Item[] redConfirmItems = new Item[]{
      Items.LIME_STAINED_GLASS_PANE, Items.GREEN_STAINED_GLASS_PANE, Items.LIME_WOOL, Items.GREEN_WOOL, Items.LIME_DYE, Items.EMERALD, Items.EMERALD_BLOCK
   };
   private String fullInventoryCommand;
   private String confirmBoughtText;
   private final Set<Integer> confirmBoughtSet;
   private int boughtListingValue;
   private int maxInteger;
   private int boughtTimedCount;
   private ConfirmScreenHelper.Phase phase;
   private String auction = "auction";
   private int syncId;
   private int confirmAttemptCount;
   private static volatile String lowerCase = null;
   private static volatile boolean boughtMoneyFlag = false;
   private Item targetItem;
   private int confirmButtonSlot;
   private Predicate<ItemStack> itemPredicate;
   private boolean boughtFullActive;
   private int timedValue;
   private static long lastNanoTime;
   private int maxBoughtFullBoundBoughtFull;
   private static volatile String moneyEnoughText = null;
   private String inventoryFullMatchText;
   private String confirmAuctionText = "Confirm";
   private String trimConfirmAuction;
   private int timedPlayerValue;

   private void updateSuccessHandled() {
      this.updateMoneyLower();
      this.updateScreenClose();
      if (this.isScreenInstance()) {
         CLIENT.player.closeHandledScreen();
      }

      this.phase = ConfirmScreenHelper.Phase.DONE;
      this.status = ConfirmScreenHelper.Status.SUCCESS;
   }

   private int getInstanceInventory() {
      if (CLIENT.player == null) {
         return 0;
      } else {
         PlayerInventory inventory = CLIENT.player.getInventory();
         int instanceInventory = 0;

         for (int i = 0; i <= 35; i++) {
            ItemStack stack = inventory.getStack(i);
            if (this.isItemStack(stack)) {
               instanceInventory += stack.getCount();
            }
         }

         return instanceInventory;
      }
   }

   public ConfirmScreenHelper.Status getStatus() {
      return this.status;
   }

   private void onScreenReleased(int screenReleasedValue) {
      if (CLIENT.currentScreen != null) {
         try {
            KeyInput keyInput = new KeyInput(screenReleasedValue, 0, 0);
            CLIENT.currentScreen.keyPressed(keyInput);
            CLIENT.currentScreen.keyReleased(keyInput);
         } catch (Throwable e) {
         }
      }
   }

   public void updateTimedBought() {
      if (this.status == ConfirmScreenHelper.Status.RUNNING) {
         if (CLIENT.player != null && CLIENT.interactionManager != null) {
            this.boughtTimedCount++;
            if (this.getInstanceInventory() > this.timedPlayerValue) {
               this.updateSuccessHandled();
            } else {
               String fullInventoryText = moneyEnoughText;
               if (fullInventoryText != null) {
                  moneyEnoughText = null;
                  this.onHandledSuccess(fullInventoryText);
               } else if (this.boughtTimedCount > 2400) {
                  this.onHandledSuccess("timed out");
               } else {
                  switch (this.phase) {
                     case SEARCH:
                        this.updateBoughtScreen();
                        break;
                     case WAIT_BOARD:
                        this.updateOpenedBoard();
                        break;
                     case CLICK:
                        this.updateBoughtListing();
                        break;
                     case CONFIRM:
                        this.updateBoughtConfirm();
                        break;
                     case VERIFY:
                        this.updateBoughtTimed();
                        break;
                     case RETRY_WAIT:
                        this.updateHandledClose();
                  }
               }
            }
         } else {
            this.onHandledSuccess("no player");
         }
      }
   }

   private boolean isRedConfirm() {
      if (CLIENT.currentScreen != null && this.isScreenInstance()) {
         if (!this.confirm.isEmpty()) {
            String lowerCase = KmbtSellerUtils.formatText(CLIENT.currentScreen.getTitle().getString()).toLowerCase(Locale.ROOT);
            if (lowerCase.contains(this.confirm)) {
               return true;
            }
         }

         boolean redConfirmFlag = this.getSlotStack(redConfirmItems) >= 0;
         boolean redConfirmFlag2 = this.getSlotStack(Items.RED_STAINED_GLASS_PANE, Items.RED_WOOL, Items.BARRIER) >= 0;
         return redConfirmFlag && redConfirmFlag2;
      } else {
         return false;
      }
   }

   public static boolean isLowerCase() {
      return lowerCase != null;
   }

   private int getSlotStack(Item... items) {
      if (!this.isScreenInstance()) {
         return -1;
      } else {
         ScreenHandler currentScreenHandler = CLIENT.player.currentScreenHandler;
         int slotStackLimit = currentScreenHandler.slots.size() - 36;

         for (int i = 0; i < slotStackLimit; i++) {
            ItemStack stack = currentScreenHandler.getSlot(i).getStack();
            if (!stack.isEmpty()) {
               for (Item item : items) {
                  if (stack.isOf(item)) {
                     return i;
                  }
               }
            }
         }

         return -1;
      }
   }

   private boolean isInstanceClick() {
      if (!this.isScreenInstance()) {
         return false;
      } else {
         ScreenHandler currentScreenHandler = CLIENT.player.currentScreenHandler;
         int clickLimit = currentScreenHandler.slots.size() - 36;
         int rootSlot = this.getRootSlot(this.confirmAuctionText);
         if (rootSlot < 0) {
            rootSlot = this.getSlotStack(redConfirmItems);
         }

         if (rootSlot >= 0 && rootSlot < clickLimit) {
            CLIENT.interactionManager.clickSlot(currentScreenHandler.syncId, rootSlot, 0, SlotActionType.PICKUP, CLIENT.player);
            return true;
         } else {
            return false;
         }
      }
   }

   public boolean canBoughtFull(String boughtFullText, Item enderChest, Predicate<ItemStack> predicate, int boughtFullBound, int rocketsBuyBound, int boughtFullBound3) {
      if (CLIENT.player == null || enderChest == null) {
         return false;
      } else if (!this.canInstanceInventory(enderChest)) {
         this.status = ConfirmScreenHelper.Status.FAILED;
         this.phase = ConfirmScreenHelper.Phase.FAIL;
         this.inventoryFullMatchText = "inventory full";
         return false;
      } else {
         this.fullInventoryCommand = normalizeText(boughtFullText);
         this.targetItem = enderChest;
         this.itemPredicate = predicate;
         this.maxBoughtFullBound = Math.max(1, boughtFullBound);
         this.maxTimedBoughtCount = Math.max(0, rocketsBuyBound);
         this.maxBoughtFullBoundBoughtFull = Math.max(0, boughtFullBound3);
         this.confirmAttemptCount = 0;
         this.boughtTimedCount = 0;
         this.confirmBoughtSet.clear();
         this.confirmButtonSlot = -1;
         this.syncId = -1;
         this.inventoryFullMatchText = "";
         this.boughtFullActive = false;
         this.boughtValue = 0;
         this.boughtScreenValue = -1000;
         this.boughtListingValue = this.getInstanceInventory();
         this.timedPlayerValue = this.boughtListingValue;
         this.updateLowerCase();
         this.setPhase(ConfirmScreenHelper.Phase.SEARCH);
         this.status = ConfirmScreenHelper.Status.RUNNING;
         return true;
      }
   }

   public boolean canBoughtFullDefault(String confirmScreenText, Item item, int confirmScreenValue, int confirmScreenValue2, int rocketsBuyBound) {
      return this.canBoughtFull(confirmScreenText, item, null, confirmScreenValue, confirmScreenValue2, rocketsBuyBound);
   }

   private boolean isContains() {
      if (CLIENT.currentScreen == null || this.isScreenInstance() || CLIENT.currentScreen instanceof InventoryScreen) {
         return false;
      } else if (CLIENT.currentScreen instanceof ChatScreen) {
         return false;
      } else if (this.confirm.isEmpty()) {
         return false;
      } else {
         String lowerCase = KmbtSellerUtils.formatText(CLIENT.currentScreen.getTitle().getString()).toLowerCase(Locale.ROOT);
         return lowerCase.contains(this.confirm);
      }
   }

   private void updateHandledClose() {
      if (this.isScreenInstance()) {
         CLIENT.player.closeHandledScreen();
      } else if (this.isContains()) {
         this.updateScreenClose();
      } else {
         if (this.getTimedBought() >= this.maxTimedBoughtCount) {
            this.setPhase(ConfirmScreenHelper.Phase.SEARCH);
         }
      }
   }

   public boolean isRunningSuccess() {
      return this.status == ConfirmScreenHelper.Status.RUNNING;
   }

   private void clickConfirmButton(String fullInventoryText) {
      this.inventoryFullMatchText = fullInventoryText;
      if (this.confirmButtonSlot >= 0) {
         this.confirmBoughtSet.add(this.confirmButtonSlot);
      }

      this.confirmButtonSlot = -1;
      if (this.confirmAttemptCount >= this.maxBoughtFullBoundBoughtFull) {
         this.onHandledSuccess(fullInventoryText);
      } else {
         this.confirmAttemptCount++;
         this.updateScreenClose();
         if (this.isScreenInstance()) {
            CLIENT.player.closeHandledScreen();
         }

         this.setPhase(ConfirmScreenHelper.Phase.RETRY_WAIT);
      }
   }

   public ConfirmScreenHelper() {
      this.confirm = "confirm";
      this.confirmBoughtText = "$";
      this.trimConfirmAuction = "already bought";
      this.maxInteger = 4;
      this.phase = ConfirmScreenHelper.Phase.DONE;
      this.status = ConfirmScreenHelper.Status.IDLE;
      this.syncId = -1;
      this.confirmBoughtSet = new HashSet<>();
      this.confirmButtonSlot = -1;
      this.inventoryFullMatchText = "";
   }

   private boolean isAuctionTitle() {
      if (!this.isScreenInstance()) {
         return false;
      } else {
         String auctionTitleText = CLIENT.currentScreen != null ? KmbtSellerUtils.formatText(CLIENT.currentScreen.getTitle().getString()).toLowerCase(Locale.ROOT) : "";
         return !this.auction.isEmpty() && auctionTitleText.contains(this.auction) ? true : this.isSlotStack();
      }
   }

   private boolean canInstanceInventory(Item item) {
      if (CLIENT.player == null) {
         return false;
      } else {
         PlayerInventory inventory = CLIENT.player.getInventory();

         for (int i = 0; i < 36; i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isEmpty()) {
               return true;
            }

            if (stack.isOf(item) && stack.getCount() < stack.getMaxCount()) {
               return true;
            }
         }

         return false;
      }
   }

   private boolean isScreenInstance() {
      return CLIENT.player != null
         && CLIENT.currentScreen instanceof HandledScreen
         && !(CLIENT.currentScreen instanceof InventoryScreen)
         && CLIENT.player.currentScreenHandler != null
         && CLIENT.player.currentScreenHandler != CLIENT.player.playerScreenHandler;
   }

   private void updateBoughtListing() {
      if (!this.isSyncHandler()) {
         this.setPhase(ConfirmScreenHelper.Phase.SEARCH);
      } else if (this.boughtTimedCount - this.boughtScreenValue >= this.maxInteger) {
         if (!this.canInstanceInventory(this.targetItem)) {
            this.onHandledSuccess("inventory full");
         } else {
            int confirmButtonValue = this.getBoughtConfirm();
            if (confirmButtonValue < 0) {
               this.clickConfirmButton("no listing for item");
            } else {
               this.confirmButtonSlot = confirmButtonValue;
               this.boughtListingValue = this.getInstanceInventory();
               boughtMoneyFlag = false;
               CLIENT.interactionManager.clickSlot(this.syncId, confirmButtonValue, 0, SlotActionType.PICKUP, CLIENT.player);
               this.boughtScreenValue = this.boughtTimedCount;
               this.setPhase(ConfirmScreenHelper.Phase.CONFIRM);
            }
         }
      }
   }

   private int getTimedBought() {
      return this.boughtTimedCount - this.timedValue;
   }

   private int getRootSlot(String rootSlotText) {
      if (rootSlotText != null && !rootSlotText.isEmpty() && this.isScreenInstance()) {
         String[] split = rootSlotText.toLowerCase(Locale.ROOT).split(",");
         ScreenHandler currentScreenHandler = CLIENT.player.currentScreenHandler;
         int rootSlotLimit = currentScreenHandler.slots.size() - 36;

         for (int i = 0; i < rootSlotLimit; i++) {
            ItemStack stack = currentScreenHandler.getSlot(i).getStack();
            if (!stack.isEmpty()) {
               String lowerCase = KmbtSellerUtils.formatText(stack.getName().getString()).toLowerCase(Locale.ROOT);

               for (String rootSlotText2 : split) {
                  String trim = rootSlotText2.trim();
                  if (!trim.isEmpty() && lowerCase.contains(trim)) {
                     return i;
                  }
               }
            }
         }

         return -1;
      } else {
         return -1;
      }
   }

   private boolean isSlotStack() {
      if (!this.isScreenInstance()) {
         return false;
      } else {
         ScreenHandler currentScreenHandler = CLIENT.player.currentScreenHandler;
         int slotStackLimit = currentScreenHandler.slots.size() - 36;

         for (int i = 0; i < slotStackLimit; i++) {
            if (this.isItemStack(currentScreenHandler.getSlot(i).getStack())) {
               return true;
            }
         }

         return false;
      }
   }

   private void updateBoughtScreen() {
      if (this.isScreenInstance()) {
         CLIENT.player.closeHandledScreen();
         this.boughtScreenValue = this.boughtTimedCount;
      } else if (this.isContains()) {
         this.updateScreenClose();
         this.boughtScreenValue = this.boughtTimedCount;
      } else if (CLIENT.currentScreen != null && !(CLIENT.currentScreen instanceof GameMenuScreen)) {
         if (this.getTimedBought() > 100) {
            this.clickConfirmButton("screen open");
         }
      } else if (this.boughtTimedCount - this.boughtScreenValue >= this.maxInteger) {
         if (System.nanoTime() - lastNanoTime >= 1500000000L) {
            lastNanoTime = System.nanoTime();
            boughtMoneyFlag = false;
            CLIENT.player.networkHandler.sendChatCommand(this.fullInventoryCommand);
            this.boughtScreenValue = this.boughtTimedCount;
            this.syncId = -1;
            this.confirmBoughtSet.clear();
            this.confirmButtonSlot = -1;
            this.setPhase(ConfirmScreenHelper.Phase.WAIT_BOARD);
         }
      }
   }

   private boolean isSyncHandler() {
      return this.isAuctionTitle() && CLIENT.player.currentScreenHandler.syncId == this.syncId;
   }

   public static void setMoneyEnoughText(String kmbtSellerText) {
      if (kmbtSellerText != null) {
         String lowerCase2 = lowerCase;
         if (lowerCase2 != null && !lowerCase2.isEmpty()) {
            String trim = KmbtSellerUtils.formatText(kmbtSellerText).toLowerCase(Locale.ROOT).trim();
            if (trim.startsWith("this item was " + lowerCase2) || trim.equals(lowerCase2) || trim.equals(lowerCase2 + ".") || trim.equals(lowerCase2 + "!")) {
               boughtMoneyFlag = true;
            } else if (trim.startsWith("you do not have enough money")) {
               moneyEnoughText = "not enough money";
            } else if (trim.startsWith("your inventory is full")) {
               moneyEnoughText = "inventory full";
            }
         }
      }
   }

   private boolean isItemStack(ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         return this.itemPredicate != null ? this.itemPredicate.test(stack) : stack.isOf(this.targetItem);
      } else {
         return false;
      }
   }

   public String getFullInventoryMatchText() {
      return this.inventoryFullMatchText;
   }

   private void updateMoneyLower() {
      lowerCase = null;
      boughtMoneyFlag = false;
      moneyEnoughText = null;
   }

   private void updateScreenClose() {
      if (CLIENT.currentScreen != null && this.isContains()) {
         try {
            CLIENT.currentScreen.close();
         } catch (Throwable e) {
         }
      }
   }

   public int getMaxBoughtCount() {
      return this.targetItem == null ? 0 : Math.max(0, this.getInstanceInventory() - this.timedPlayerValue);
   }

   private int getBoughtConfirm() {
      if (!this.isScreenInstance()) {
         return -1;
      } else {
         ScreenHandler currentScreenHandler = CLIENT.player.currentScreenHandler;
         int boughtConfirmLimit = currentScreenHandler.slots.size() - 36;
         int boughtConfirm = -1;
         long maxValue = Long.MAX_VALUE;

         for (int i = 0; i < boughtConfirmLimit; i++) {
            if (!this.confirmBoughtSet.contains(i)) {
               ItemStack stack = currentScreenHandler.getSlot(i).getStack();
               if (!stack.isEmpty() && this.isItemStack(stack)) {
                  List linesLore = getLinesLore(stack);
                  OptionalLong root = KmbtSellerUtils.getRoot(linesLore.subList(1, linesLore.size()), this.confirmBoughtText);
                  if (!root.isEmpty()) {
                     long boughtConfirmValue = this.boughtFullActive ? root.getAsLong() * 64L / Math.max(1, stack.getCount()) : root.getAsLong();
                     if (boughtConfirmValue < maxValue) {
                        maxValue = boughtConfirmValue;
                        boughtConfirm = i;
                     }
                  }
               }
            }
         }

         return boughtConfirm;
      }
   }

   private void updateOpenedBoard() {
      if (!this.isAuctionTitle()) {
         if (this.getTimedBought() > 100) {
            this.clickConfirmButton("no board opened");
         }
      } else {
         if (this.syncId < 0) {
            this.syncId = CLIENT.player.currentScreenHandler.syncId;
         }

         if (this.getTimedBought() >= 5) {
            this.setPhase(ConfirmScreenHelper.Phase.CLICK);
         }
      }
   }

   public void setBoughtFullActive(boolean flag) {
      this.boughtFullActive = flag;
   }

   private void updateBoughtTimed() {
      if (this.getInstanceInventory() > this.boughtListingValue) {
         this.updateSuccessHandled();
      } else if (boughtMoneyFlag) {
         boughtMoneyFlag = false;
         this.clickConfirmButton("already bought");
      } else {
         if (this.isScreenInstance() && this.isRedConfirm() && this.boughtTimedCount - this.boughtScreenValue >= this.maxInteger) {
            this.isInstanceClick();
            this.boughtScreenValue = this.boughtTimedCount;
         }

         if (this.isContains() && this.boughtTimedCount - this.boughtScreenValue >= this.maxInteger * 3) {
            this.setPhase(ConfirmScreenHelper.Phase.CONFIRM);
         } else {
            if (this.getTimedBought() > 60) {
               this.clickConfirmButton("item never arrived");
            }
         }
      }
   }

   private static String normalizeText(String confirmScreenText) {
      String confirmScreenText2 = confirmScreenText == null ? "" : confirmScreenText.trim();
      return confirmScreenText2.startsWith("/") ? confirmScreenText2.substring(1) : confirmScreenText2;
   }

   private void updateLowerCase() {
      lowerCase = this.trimConfirmAuction == null ? "" : this.trimConfirmAuction.toLowerCase(Locale.ROOT).trim();
      boughtMoneyFlag = false;
      moneyEnoughText = null;
   }

   private void onHandledSuccess(String fullInventoryText) {
      this.inventoryFullMatchText = fullInventoryText;
      this.updateMoneyLower();
      this.updateScreenClose();
      if (CLIENT.player != null && this.isScreenInstance()) {
         CLIENT.player.closeHandledScreen();
      }

      this.phase = ConfirmScreenHelper.Phase.FAIL;
      this.status = ConfirmScreenHelper.Status.FAILED;
   }

   public void configureConfirmAuction(
      String confirmAuctionText, String confirmAuctionText2, String confirmAuctionText3, String confirmBoughtText, String confirmAuctionText4, Integer integer
   ) {
      if (confirmAuctionText != null) {
         this.auction = confirmAuctionText.toLowerCase(Locale.ROOT).trim();
      }

      if (confirmAuctionText2 != null) {
         this.confirmAuctionText = confirmAuctionText2;
      }

      if (confirmAuctionText3 != null) {
         this.confirm = confirmAuctionText3.toLowerCase(Locale.ROOT).trim();
      }

      if (confirmBoughtText != null) {
         this.confirmBoughtText = confirmBoughtText;
      }

      if (confirmAuctionText4 != null) {
         this.trimConfirmAuction = confirmAuctionText4.toLowerCase(Locale.ROOT).trim();
      }

      if (integer != null) {
         this.maxInteger = Math.max(2, integer);
      }
   }

   private void updateBoughtConfirm() {
      if (this.getInstanceInventory() > this.boughtListingValue) {
         this.updateSuccessHandled();
      } else if (boughtMoneyFlag) {
         boughtMoneyFlag = false;
         this.clickConfirmButton("already bought");
      } else if (this.isContains()) {
         if (this.boughtTimedCount - this.boughtScreenValue >= this.maxInteger) {
            if (this.boughtValue == 0) {
               this.onScreenReleased(263);
               this.boughtValue = 1;
               this.boughtScreenValue = this.boughtTimedCount;
            } else {
               this.onScreenReleased(257);
               this.boughtValue = 0;
               this.boughtScreenValue = this.boughtTimedCount;
               this.setPhase(ConfirmScreenHelper.Phase.VERIFY);
            }
         }
      } else {
         if (this.isScreenInstance()) {
            boolean boughtConfirmFlag = CLIENT.player.currentScreenHandler.syncId != this.syncId || this.isRedConfirm();
            if (boughtConfirmFlag) {
               if (this.boughtTimedCount - this.boughtScreenValue < this.maxInteger) {
                  return;
               }

               if (this.isInstanceClick()) {
                  this.boughtScreenValue = this.boughtTimedCount;
                  this.setPhase(ConfirmScreenHelper.Phase.VERIFY);
                  return;
               }

               if (this.getTimedBought() > 40) {
                  this.clickConfirmButton("no confirm button");
               }

               return;
            }

            if (this.confirmButtonSlot >= 0) {
               ItemStack stack = CLIENT.player.currentScreenHandler.getSlot(this.confirmButtonSlot).getStack();
               if (stack.isEmpty() || !this.isItemStack(stack)) {
                  this.clickConfirmButton("listing gone");
                  return;
               }
            }
         }

         if (this.getTimedBought() > 40) {
            this.clickConfirmButton("no confirm dialog");
         }
      }
   }

   private static List<String> getLinesLore(ItemStack stack) {
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

   private void setPhase(ConfirmScreenHelper.Phase waitVerifyType) {
      this.phase = waitVerifyType;
      this.timedValue = this.boughtTimedCount;
   }

   public void updateHandledSuccess() {
      this.updateMoneyLower();
      this.updateScreenClose();
      if (CLIENT.player != null && this.isScreenInstance()) {
         CLIENT.player.closeHandledScreen();
      }

      this.phase = ConfirmScreenHelper.Phase.DONE;
      this.status = ConfirmScreenHelper.Status.IDLE;
   }

   private static enum Phase {
      SEARCH,
      WAIT_BOARD,
      CLICK,
      CONFIRM,
      VERIFY,
      RETRY_WAIT,
      DONE,
      FAIL;
   }

   public static enum Status {
      IDLE,
      RUNNING,
      SUCCESS,
      FAILED;
   }
}
