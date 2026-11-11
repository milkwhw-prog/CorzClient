package com.corz.client.categories.bots;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.config.StringBase;
import com.corz.client.utils.FlipperAuctionUtils.Candidate;
import com.corz.client.utils.FlipperAuctionUtils.Params;
import com.corz.client.utils.FlipperAuctionUtils.Result;
import com.corz.client.gui.ButtonBase;
import com.corz.client.gui.DisabledEnabled;
import com.corz.client.inventory.InventoryStackUtilsHandle;
import com.corz.client.inventory.ItemList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.OptionalLong;
import java.util.Set;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.input.KeyInput;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import com.corz.client.utils.FlipperAuctionUtils;
import ai.auth.Module;
import com.corz.client.utils.TotalPriceData;
import com.corz.client.utils.HotbarSlotUtils;
import com.corz.client.utils.BuyerAppendUtils;
import com.corz.client.utils.IconsPngType;
import com.corz.client.utils.KmbtSellerUtils;

public class AutoAuctionFlipper extends Module {
   private final StringBase confirmNoWord;
   private final StringBase maximum;
   private final IntegerSetting minProfitPerItem;
   private final List<TotalPriceData> scannedListings;
   private final StringBase priceKeyword;
   private final ButtonBase resetProfitButton;
   private final EnumSetting priceIsPer;
   private static final Item[] SELL_CONFIRM_ITEMS = new Item[]{
      Items.LIME_STAINED_GLASS_PANE, Items.GREEN_STAINED_GLASS_PANE, Items.LIME_WOOL, Items.GREEN_WOOL, Items.LIME_DYE, Items.EMERALD, Items.EMERALD_BLOCK
   };
   private final StringBase confirmYesWord;
   private final Set<Integer> skippedSlots;
   private int stateEnterTick;
   private final StringBase alreadyBoughtWord;
   private int lastActionTick;
   private static final Item[] AUTO_PICK_ITEMS = new Item[]{
      Items.ENCHANTED_GOLDEN_APPLE,
      Items.MACE,
      Items.HEAVY_CORE,
      Items.ELYTRA,
      Items.NETHERITE_INGOT,
      Items.NETHERITE_BLOCK,
      Items.NETHERITE_SCRAP,
      Items.NETHERITE_UPGRADE_SMITHING_TEMPLATE,
      Items.WITHER_SKELETON_SKULL,
      Items.BEACON,
      Items.SHULKER_BOX,
      Items.TNT
   };
   private final IntegerSetting salesTax;
   private int resellHotbarSlot;
   private final ButtonBase scanPrices;
   private static volatile String alreadyBoughtTextLower = null;
   private final ItemList itemsToFlip = new ItemList(
      "Items to Flip",
      "The item(s) to buy low and sell high. Add what you want to flip \u2014 that's usually all you need to set. (Ignored while Auto-Pick Items is on.)",
      List.of(Items.ENCHANTED_GOLDEN_APPLE, Items.MACE)
   );
   private final List<Item> sweepItems;
   private int flipsThisSweep;
   private final StringBase nextPageWord;
   private final EnumSetting searchBy;
   private int listedItemTotal;
   private Item targetFlipItem;
   private int inventoryCountBeforeSell;
   private final StringBase ahTitleWord;
   private final StringBase confirm;
   private int sweepIndex;
   private final IntegerSetting sellConfirmSlot;
   private final StringBase searchCommand;
   private final BooleanSetting autoPickItems = new BooleanSetting(
      "Auto-Pick Items", "Skip the item list and hunt a built-in set of items that actually have room to flip. Turn it on, enable the bot, walk away.", false
   );
   private int pageIndex;
   private final Map<Item, Integer> ownedCountBaseline;
   private final EnumSetting mode = new EnumSetting(
      "Mode",
      "Flip = buy bargains and re-list them higher (makes money) \u00b7 Snipe Only = buy bargains and keep them \u00b7 Analyze = just price the items, buy nothing.",
      "Flip",
      "Flip",
      "Snipe Only",
      "Analyze"
   );
   private final IntegerSetting maxUnitPrice;
   private static volatile String listedTextLower = null;
   private static volatile String listFailTextLower = null;
   private final IntegerSetting minListings;
   private final IntegerSetting maxFlipsSweep;
   private long pendingListTotal;
   private int snipedItemTotal;
   private final IntegerSetting referencePercentile;
   private int hotbarSwapState;
   private long marketUnitPrice;
   private final StringBase listed;
   private final IntegerSetting maxTotalSpend;
   private final StringBase sellCommand;
   private final IntegerSetting maxAhPages;
   private long resellUnitPrice;
   private static volatile boolean alreadyBoughtSeen = false;
   private Item resellItem;
   private Candidate currentCandidate;
   private int inventoryCountBeforeBuy;
   private long listedRevenueTotal;
   private static volatile boolean listedMessageSeen = false;
   private String lastHoldNoticeKey;
   private long totalSpent;
   private final IntegerSetting refreshCooldown;
   private final IntegerSetting minReturn;
   private final IntegerSetting clickSpacing;
   private final BooleanSetting notifications;
   private int tickCount;
   private int confirmKeyStep;
   private int missCount;
   private final IntegerSetting resellPercent;
   private AutoAuctionFlipper.State flipState;
   private int pendingListCount;
   private int lastHoldNoticeTick;
   private boolean priceScanRequested;
   private int boardSyncId;
   private static volatile boolean listFailMessageSeen = false;

   private int getTicksInState() {
      return this.tickCount - this.stateEnterTick;
   }

   private boolean hasInventoryRoomFor(Item item) {
      PlayerInventory inventory = mc.player.getInventory();

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

   private void registerMissedListing(String missesManyText) {
      if (this.currentCandidate != null) {
         this.skippedSlots.add(this.currentCandidate.slot());
      }

      this.currentCandidate = null;
      this.missCount++;
      if (this.missCount >= 3) {
         this.skipToNextItem("too many misses");
      } else {
         if (this.isSameBoardScreen()) {
            this.enterState(AutoAuctionFlipper.State.SCAN);
         } else {
            this.enterState(AutoAuctionFlipper.State.SEARCH);
         }
      }
   }

   private String getSearchTerm(Item item2) {
      String path = getItemPath(item2);
      String formatText = this.searchBy.getCurrentOption();

      return switch (formatText) {
         case "Id" -> path;
         case "Display" -> new ItemStack(item2).getName().getString();
         default -> path.replace('_', ' ');
      };
   }

   private int getRefreshCooldownTicks() {
      return Math.max(20, this.refreshCooldown.getValue() * 20);
   }

   private void beginResellSend() {
      if (this.resellHotbarSlot >= 0) {
         mc.player.getInventory().setSelectedSlot(this.resellHotbarSlot);
      }

      this.hotbarSwapState = 0;
      this.confirmKeyStep = 0;
      this.lastActionTick = this.tickCount;
      this.enterState(AutoAuctionFlipper.State.RESELL_SEND);
   }

   private void tickNextPage() {
      if (!this.isSameBoardScreen()) {
         this.enterState(AutoAuctionFlipper.State.SEARCH);
      } else if (this.tickCount - this.lastActionTick >= this.getClickSpacingTicks()) {
         int rootSlot = this.findSlotByNameKeyword(this.nextPageWord.getCurrentText());
         if (rootSlot < 0) {
            if (this.isAnalyzeMode()) {
               this.reportAnalysisResult();
               mc.player.closeHandledScreen();
               this.advanceToNextItem();
            } else {
               this.skipToNextItem("no more pages");
            }
         } else {
            mc.interactionManager
               .clickSlot(mc.player.currentScreenHandler.syncId, rootSlot, 0, SlotActionType.PICKUP, mc.player);
            this.lastActionTick = this.tickCount;
            this.pageIndex++;
            this.enterState(AutoAuctionFlipper.State.SCAN);
         }
      }
   }

   private int getClickSpacingTicks() {
      return Math.max(2, this.clickSpacing.getValue());
   }

   private void armMessageMatchers() {
      alreadyBoughtTextLower = normalizeWord(this.alreadyBoughtWord.getCurrentText());
      listedTextLower = normalizeWord(this.listed.getCurrentText());
      listFailTextLower = normalizeWord(this.maximum.getCurrentText());
      alreadyBoughtSeen = false;
      listedMessageSeen = false;
      listFailMessageSeen = false;
   }

   private void showToast(String autoAuctionText, String substringText, boolean autoAuctionFlag) {
      DisabledEnabled.getInstance().handleDisabledEnabled(autoAuctionText, substringText, autoAuctionFlag);
   }

   private boolean isAnalyzeMode() {
      return this.priceScanRequested || "Analyze".equals(this.mode.getCurrentOption());
   }

   private void finishResellAttempt() {
      InventoryStackUtilsHandle.handle(this);
      this.resellItem = null;
      this.resellHotbarSlot = -1;
      this.hotbarSwapState = 0;
      this.continueSameItem();
   }

   private List<TotalPriceData> collectListings(Item item2) {
      var arrayList = new ArrayList();
      if (!this.isContainerScreenOpen()) {
         return arrayList;
      } else {
         ScreenHandler currentScreenHandler = mc.player.currentScreenHandler;
         int wordListedLimit = currentScreenHandler.slots.size() - 36;
         boolean equals = "Item".equals(this.priceIsPer.getCurrentOption());

         for (int i = 0; i < wordListedLimit; i++) {
            if (!this.skippedSlots.contains(i)) {
               ItemStack stack = currentScreenHandler.getSlot(i).getStack();
               if (!stack.isEmpty() && stack.isOf(item2)) {
                  int wordListedFactor = Math.max(1, stack.getCount());
                  OptionalLong sellerPrice = KmbtSellerUtils.getSellerPrice(getNameAndLoreLines(stack), this.priceKeyword.getCurrentText());
                  if (!sellerPrice.isEmpty()) {
                     long wordListedValue = equals ? sellerPrice.getAsLong() * wordListedFactor : sellerPrice.getAsLong();
                     arrayList.add(new TotalPriceData(i, wordListedFactor, wordListedValue));
                  }
               }
            }
         }

         return arrayList;
      }
   }

   private void tickScan() {
      if (!this.isAuctionBoardOpen()) {
         if (this.getTicksInState() > 100) {
            if (this.isAnalyzeMode()) {
               this.reportAnalysisResult();
               this.advanceToNextItem();
            } else {
               this.skipToNextItem("no board opened");
            }
         }
      } else {
         if (this.boardSyncId < 0) {
            this.boardSyncId = mc.player.currentScreenHandler.syncId;
         }

         if (this.getTicksInState() >= 5) {
            var wordListed = this.collectListings(this.targetFlipItem);
            if (this.isAnalyzeMode()) {
               this.scannedListings.addAll(wordListed);
               if (this.hasNextPageButton() && this.pageIndex + 1 < this.maxAhPages.getValue()) {
                  this.enterState(AutoAuctionFlipper.State.NEXT_PAGE);
               } else {
                  this.reportAnalysisResult();
                  mc.player.closeHandledScreen();
                  this.advanceToNextItem();
               }
            } else {
               long integerOpenedValue = this.maxTotalSpend.getValue() > 0 ? Math.max(1L, this.maxTotalSpend.getValue() - this.totalSpent) : 0L;
               Result pricePercent = FlipperAuctionUtils.findFlipCandidates(wordListed, this.buildPricingParams(integerOpenedValue));
               if (!pricePercent.hasCandidates()) {
                  if (this.hasNextPageButton() && this.pageIndex + 1 < this.maxAhPages.getValue()) {
                     this.enterState(AutoAuctionFlipper.State.NEXT_PAGE);
                  } else {
                     this.notifyNoFlipFound(pricePercent);
                     this.skipToNextItem("no flip (ref " + formatCoins(pricePercent.referenceUnit()) + ")");
                  }
               } else {
                  this.currentCandidate = pricePercent.getBestCandidate();
                  this.marketUnitPrice = pricePercent.referenceUnit();
                  this.resellUnitPrice = this.currentCandidate.resellUnit();
                  this.enterState(AutoAuctionFlipper.State.CLICK);
               }
            }
         }
      }
   }

   private int countInventoryItem(Item item) {
      if (mc.player == null) {
         return 0;
      } else {
         PlayerInventory inventory = mc.player.getInventory();
         int inventoryPlayer = 0;

         for (int i = 0; i <= 35; i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isOf(item)) {
               inventoryPlayer += stack.getCount();
            }
         }

         return inventoryPlayer;
      }
   }

   private boolean isPurchaseConfirmScreen() {
      if (mc.currentScreen != null && this.isContainerScreenOpen()) {
         String lowerCase = normalizeWord(this.confirm.getCurrentText());
         if (!lowerCase.isEmpty()) {
            String lowerCase2 = KmbtSellerUtils.formatText(mc.currentScreen.getTitle().getString()).toLowerCase(Locale.ROOT);
            if (lowerCase2.contains(lowerCase)) {
               return true;
            }
         }

         boolean redConfirmFlag = this.findSlotWithItem(SELL_CONFIRM_ITEMS) >= 0;
         boolean redConfirmFlag2 = this.findSlotWithItem(Items.RED_STAINED_GLASS_PANE, Items.RED_WOOL, Items.BARRIER) >= 0;
         return redConfirmFlag && redConfirmFlag2;
      } else {
         return false;
      }
   }

   private static String stripLeadingSlash(String autoAuctionText) {
      String autoAuctionText2 = autoAuctionText == null ? "" : autoAuctionText.trim();
      return autoAuctionText2.startsWith("/") ? autoAuctionText2.substring(1) : autoAuctionText2;
   }

   private static String getItemPath(Item item2) {
      return Registries.ITEM.getId(item2).getPath();
   }

   private void recordListedBatch() {
      listedMessageSeen = false;
      this.listedRevenueTotal = this.listedRevenueTotal + this.pendingListTotal;
      this.listedItemTotal = this.listedItemTotal + this.pendingListCount;
      if (this.notifications.isEnabled()) {
         this.sendFlipMessage(
            "\u00a7b[Flip]\u00a7r listed \u00a7f"
               + this.pendingListCount
               + "\u00d7\u00a7r "
               + getItemPath(this.resellItem)
               + " for "
               + formatCoins(this.pendingListTotal)
               + " \u00a77(net so far "
               + formatSignedCoins(this.listedRevenueTotal - this.totalSpent)
               + ")"
         );
      }

      if (this.isContainerScreenOpen()) {
         mc.player.closeHandledScreen();
      }

      this.enterState(AutoAuctionFlipper.State.RESELL_PREP);
   }

   private void pressScreenKey(int screenReleasedValue) {
      if (mc.currentScreen != null) {
         try {
            KeyInput keyInput = new KeyInput(screenReleasedValue, 0, 0);
            mc.currentScreen.keyPressed(keyInput);
            mc.currentScreen.keyReleased(keyInput);
         } catch (Throwable e) {
         }
      }
   }

   private boolean isContainerScreenOpen() {
      return mc.player != null
         && mc.currentScreen instanceof HandledScreen
         && !(mc.currentScreen instanceof InventoryScreen)
         && mc.player.currentScreenHandler != null
         && mc.player.currentScreenHandler != mc.player.playerScreenHandler;
   }

   private static String formatCoins(long autoAuctionValue) {
      return BuyerAppendUtils.formatText(autoAuctionValue);
   }

   private int findSlotByNameKeyword(String rootSlotText) {
      if (rootSlotText != null && !rootSlotText.isEmpty() && this.isContainerScreenOpen()) {
         String[] split = rootSlotText.toLowerCase(Locale.ROOT).split(",");
         ScreenHandler currentScreenHandler = mc.player.currentScreenHandler;
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

   private void notifyNoFlipFound(Result result) {
      if (this.notifications.isEnabled() && this.targetFlipItem != null && result.pricedListings() > 0) {
         long lossThinFactor = result.referenceUnit() * Math.max(0, this.resellPercent.getValue()) / 100L;
         long lossThinValue = lossThinFactor * (100L - Math.min(50, Math.max(0, this.salesTax.getValue()))) / 100L;
         long lossThinLimit = lossThinValue - result.floorUnit();
         long autoAuctionValue = this.minProfitPerItem.getValue();
         String append = getItemPath(this.targetFlipItem) + '|' + (lossThinLimit > 0L ? "thin" : "loss");
         if (!append.equals(this.lastHoldNoticeKey) || this.tickCount - this.lastHoldNoticeTick >= 200) {
            this.lastHoldNoticeKey = append;
            this.lastHoldNoticeTick = this.tickCount;
            String lossThinText = lossThinLimit <= 0L
               ? "cheapest still sells for less than it costs (buy under " + formatCoins(lossThinValue) + ") \u2014 holding"
               : "best is ~" + formatCoins(lossThinLimit) + "/item, want " + formatCoins(autoAuctionValue) + "+ \u2014 holding";
            this.sendFlipMessage(
               "\u00a77[Flip]\u00a7r "
                  + getItemPath(this.targetFlipItem)
                  + ": \u00a7f"
                  + result.pricedListings()
                  + "\u00a77 listings \u00b7 market \u00a7f"
                  + formatCoins(result.referenceUnit())
                  + "\u00a77 \u00b7 floor \u00a7f"
                  + formatCoins(result.floorUnit())
                  + "\u00a77 \u2014 "
                  + lossThinText
            );
         }
      }
   }

   @Override
   public void onEnable() {
      this.tickCount = 0;
      this.totalSpent = 0L;
      this.listedRevenueTotal = 0L;
      this.snipedItemTotal = 0;
      this.listedItemTotal = 0;
      this.resetState();
      if (mc.player != null && mc.world != null) {
         this.announceStart();
         this.startSweep();
      } else {
         this.showToast("Flipper", "Join a world first", false);
         this.setEnabled(false);
      }
   }

   private static List<String> getNameAndLoreLines(ItemStack stack) {
      var arrayList = new ArrayList();
      arrayList.add(stack.getName().getString());
      LoreComponent loreComponent = stack.get(DataComponentTypes.LORE);
      if (loreComponent != null) {
         for (Text text : loreComponent.lines()) {
            arrayList.add(text.getString());
         }
      }

      return arrayList;
   }

   private boolean isSameBoardScreen() {
      return this.isAuctionBoardOpen() && mc.player.currentScreenHandler.syncId == this.boardSyncId;
   }

   private void sendFlipMessage(String messageLiteralText) {
      if (mc.player != null) {
         mc.player.sendMessage(Text.literal(messageLiteralText), false);
      }
   }

   private void resetState() {
      this.flipState = AutoAuctionFlipper.State.IDLE;
      this.sweepItems.clear();
      this.ownedCountBaseline.clear();
      this.scannedListings.clear();
      this.skippedSlots.clear();
      this.sweepIndex = -1;
      this.targetFlipItem = null;
      this.currentCandidate = null;
      this.missCount = 0;
      this.pageIndex = 0;
      this.boardSyncId = -1;
      this.resellItem = null;
      this.resellHotbarSlot = -1;
      this.hotbarSwapState = 0;
      this.confirmKeyStep = 0;
      this.flipsThisSweep = 0;
      this.lastHoldNoticeTick = -100000;
      this.lastHoldNoticeKey = "";
   }

   private static String normalizeWord(String lowerCaseText) {
      return lowerCaseText == null ? "" : lowerCaseText.toLowerCase(Locale.ROOT).trim();
   }

   private void onScanPricesPressed() {
      if (this.isEnabled()) {
         this.showToast("Scan Prices", "Turn the flipper off first", false);
      } else if (mc.player != null && mc.world != null) {
         this.priceScanRequested = true;
         this.setEnabled(true);
      } else {
         this.showToast("Scan Prices", "Join a world first", false);
      }
   }

   private void reportAnalysisResult() {
      Result pricePercent = FlipperAuctionUtils.findFlipCandidates(this.scannedListings, this.buildPricingParams(0L));
      String path = getItemPath(this.targetFlipItem);
      if (pricePercent.pricedListings() == 0) {
         if (this.notifications.isEnabled()) {
            this.sendFlipMessage("\u00a77[Flip]\u00a7r " + path + ": no priced listings");
         }
      } else {
         String unitPricedText = pricePercent.hasCandidates()
            ? "\u00a7abest flip \u00a7f"
               + pricePercent.getBestCandidate().count()
               + "\u00d7 @ "
               + formatCoins(pricePercent.getBestCandidate().unitPrice())
               + " \u2192 ~"
               + formatCoins(pricePercent.getBestCandidate().totalProfit())
               + " net profit"
            : "\u00a78no bargain (floor " + formatCoins(pricePercent.floorUnit()) + ")";
         if (this.notifications.isEnabled()) {
            this.sendFlipMessage(
               "\u00a7b[Flip]\u00a7r "
                  + path
                  + " \u2014 market \u00a7f"
                  + formatCoins(pricePercent.referenceUnit())
                  + "\u00a7r over "
                  + pricePercent.pricedListings()
                  + " listings \u00b7 "
                  + unitPricedText
                  + " \u00a78\u00b7 analyze mode: pricing only, not buying"
            );
         }
      }
   }

   private void resetProfitCounters() {
      this.totalSpent = 0L;
      this.listedRevenueTotal = 0L;
      this.snipedItemTotal = 0;
      this.listedItemTotal = 0;
      this.showToast("Flipper", "Profit counters reset", true);
   }

   private boolean tryClickConfirmButton(boolean clickPickupFlag) {
      if (!this.isContainerScreenOpen()) {
         return false;
      } else {
         ScreenHandler currentScreenHandler = mc.player.currentScreenHandler;
         int clickPickupLimit = currentScreenHandler.slots.size() - 36;
         int rootSlot = this.findSlotByNameKeyword(this.confirmYesWord.getCurrentText());
         if (rootSlot < 0) {
            rootSlot = this.findSlotWithItem(SELL_CONFIRM_ITEMS);
         }

         if (rootSlot < 0 && clickPickupFlag) {
            int clickPickupLimit2 = this.sellConfirmSlot.getValue();
            if (clickPickupLimit2 >= 0 && clickPickupLimit2 < clickPickupLimit) {
               rootSlot = clickPickupLimit2;
            }
         }

         if (rootSlot >= 0 && rootSlot < clickPickupLimit) {
            mc.interactionManager.clickSlot(currentScreenHandler.syncId, rootSlot, 0, SlotActionType.PICKUP, mc.player);
            return true;
         } else {
            return false;
         }
      }
   }

   @Override
   public void onReset() {
      if (mc.player != null && mc.interactionManager != null) {
         this.tickCount++;
         switch (this.flipState) {
            case SEARCH:
               this.tickSearch();
               break;
            case SCAN:
               this.tickScan();
               break;
            case NEXT_PAGE:
               this.tickNextPage();
               break;
            case CLICK:
               this.tickClickListing();
               break;
            case CONFIRM:
               this.tickConfirmPurchase();
               break;
            case VERIFY:
               this.tickVerifyPurchase();
               break;
            case RESELL_PREP:
               this.tickResellPrep();
               break;
            case RESELL_SEND:
               this.tickResellSend();
               break;
            case RESELL_CONFIRM:
               this.tickResellConfirm();
               break;
            case RESELL_VERIFY:
               this.tickResellVerify();
               break;
            case COOLDOWN:
               this.tickCooldown();
         }
      }
   }

   private boolean hasNextPageButton() {
      return this.findSlotByNameKeyword(this.nextPageWord.getCurrentText()) >= 0;
   }

   public AutoAuctionFlipper() {
      super("AutoAuctionFlipper", IconsPngType.DONUTSMP);
      this.minProfitPerItem = new IntegerSetting(
         "Min Profit / Item",
         "THE money dial: buy ANY listing you can flip for at least this many coins of net (after-tax) profit \u2014 small wins count, it won't hold out for a fat margin. Lower = more buys, more often.",
         2000,
         0,
         10000000
      );
      this.notifications = new BooleanSetting(
         "Notifications",
         "Chat + toast feedback for each snipe and listing, the running profit, and a periodic 'here's what I see, still holding' line so it's never a silent black box.",
         true
      );
      this.scanPrices = new ButtonBase(
         "Scan Prices",
         "Price the items once and print each one's market value and best flip \u2014 buys nothing. (Turn the bot off first.)",
         "Scan",
         this::onScanPricesPressed
      );
      this.resetProfitButton = new ButtonBase("Reset Profit", "Zero the session spent / earned counters.", "Reset", this::resetProfitCounters);
      this.minReturn = new IntegerSetting(
         "Min Return %",
         "Optional extra brake: also require the profit to be at least this % of what you pay (0 = OFF \u2014 pure Min Profit, buys every small win). Raise it only if you want to avoid thin-margin buys.",
         0,
         0,
         500
      );
      this.salesTax = new IntegerSetting(
         "Sales Tax %",
         "The cut the auction house skims when your listing sells. Folded into every profit estimate so a 2k 'profit' is really 2k in your pocket. DonutSMP is around 5% \u2014 lower it if you're getting too few buys.",
         5,
         0,
         50
      );
      this.resellPercent = new IntegerSetting(
         "Resell %",
         "Re-list sniped stock at this percent of the market price. 100 = list at market so the most flips qualify; under 100 undercuts to sell faster but needs a bigger buy discount to still profit.",
         100,
         50,
         200
      );
      this.maxUnitPrice = new IntegerSetting(
         "Max Unit Price", "Never buy a listing whose per-item price is above this many coins (0 = no cap).", 0, 0, 1000000000
      );
      this.maxTotalSpend = new IntegerSetting(
         "Max Total Spend", "Turn the bot off once this many coins have been spent this session (0 = no cap).", 0, 0, 1000000000
      );
      this.referencePercentile = new IntegerSetting(
         "Reference %", "Which percentile of the current listings counts as the market price (50 = median). Rarely needs changing.", 50, 0, 100
      );
      this.minListings = new IntegerSetting(
         "Min Listings", "Require at least this many priced listings before trusting the market read (thin markets are skipped).", 3, 1, 30
      );
      this.priceIsPer = new EnumSetting("Price Is Per", "Whether a listing's shown price is for the whole stack or for one item.", "Stack", "Stack", "Item");
      this.maxAhPages = new IntegerSetting("Max AH Pages", "How many auction pages to read per item.", 2, 1, 10);
      this.maxFlipsSweep = new IntegerSetting("Max Flips / Sweep", "Stop buying for the current pass after this many snipes (0 = unlimited).", 0, 0, 100);
      this.clickSpacing = new IntegerSetting("Click Spacing", "Ticks between auction clicks \u2014 higher is slower but gentler on the server.", 3, 2, 20);
      this.refreshCooldown = new IntegerSetting(
         "Refresh Cooldown", "Seconds to wait before re-scanning the item list (underpriced items get sniped fast \u2014 keep this low).", 3, 0, 120
      );
      this.searchCommand = new StringBase("Search Command", "Command (no slash) that opens the board for an item. {item} = the item name.", "ah {item}");
      this.sellCommand = new StringBase(
         "Sell Command", "Command (no slash) that lists the held stack. {price} = total asking price, {count} = stack size.", "ah sell {price}"
      );
      this.searchBy = new EnumSetting(
         "Search By",
         "How {item} is formed \u2014 Spaced 'redstone block', Id 'redstone_block', or Display 'Block of Redstone'. Flip this if searches find nothing.",
         "Spaced",
         "Spaced",
         "Id",
         "Display"
      );
      this.ahTitleWord = new StringBase("AH Title Word", "Text that identifies the auction board's title.", "Auction");
      this.priceKeyword = new StringBase("Price Keyword", "Symbol/word that marks the price line in a listing tooltip.", "$");
      this.confirmYesWord = new StringBase("Confirm Yes Word", "Name of the confirm button in a buy/sell confirmation GUI.", "Confirm");
      this.confirmNoWord = new StringBase("Confirm No Word", "Name of the cancel button in a confirmation GUI.", "Cancel");
      this.confirm = new StringBase("Confirm Title Word", "A word in a confirmation GUI's TITLE (used as one more way to spot it).", "confirm");
      this.sellConfirmSlot = new IntegerSetting(
         "Sell Confirm Slot",
         "Fallback slot clicked to confirm a listing when the green button can't be found by material (DonutSMP's is slot 15 \u2014 row 2, slot 7).",
         15,
         0,
         53
      );
      this.nextPageWord = new StringBase("Next Page Word", "Name of the next-page button item in the auction board.", "Next");
      this.alreadyBoughtWord = new StringBase(
         "Already-Bought Word", "Server chat shown when a listing was sniped by someone else first \u2014 rules the buy a miss instantly.", "already bought"
      );
      this.listed = new StringBase("Listed Word", "Server chat confirming a successful listing (also confirmed by the item leaving your inventory).", "listed");
      this.maximum = new StringBase("List-Fail Word", "Server chat shown when a listing is refused (e.g. too many active listings).", "maximum");
      this.flipState = AutoAuctionFlipper.State.IDLE;
      this.sweepItems = new ArrayList<>();
      this.boardSyncId = -1;
      this.skippedSlots = new HashSet<>();
      this.resellHotbarSlot = -1;
      this.scannedListings = new ArrayList<>();
      this.ownedCountBaseline = new HashMap<>();
      this.lastHoldNoticeTick = -100000;
      this.lastHoldNoticeKey = "";

      for (ConfigSetting autoEspBase : new ConfigSetting[]{
         this.minReturn,
         this.salesTax,
         this.resellPercent,
         this.maxUnitPrice,
         this.maxTotalSpend,
         this.referencePercentile,
         this.minListings,
         this.priceIsPer,
         this.maxAhPages,
         this.maxFlipsSweep,
         this.clickSpacing,
         this.refreshCooldown
      }) {
         autoEspBase.withGroupName("Tuning");
      }

      for (ConfigSetting autoEspBase2 : new ConfigSetting[]{
         this.searchCommand,
         this.sellCommand,
         this.searchBy,
         this.ahTitleWord,
         this.priceKeyword,
         this.confirmYesWord,
         this.confirmNoWord,
         this.confirm,
         this.sellConfirmSlot,
         this.nextPageWord,
         this.alreadyBoughtWord,
         this.listed,
         this.maximum
      }) {
         autoEspBase2.withGroupName("Server Wording");
      }

      this.addSettings(
         new ConfigSetting[]{
            this.itemsToFlip,
            this.autoPickItems,
            this.mode,
            this.minProfitPerItem,
            this.notifications,
            this.scanPrices,
            this.resetProfitButton,
            this.minReturn,
            this.salesTax,
            this.resellPercent,
            this.maxUnitPrice,
            this.maxTotalSpend,
            this.referencePercentile,
            this.minListings,
            this.priceIsPer,
            this.maxAhPages,
            this.maxFlipsSweep,
            this.clickSpacing,
            this.refreshCooldown,
            this.searchCommand,
            this.sellCommand,
            this.searchBy,
            this.ahTitleWord,
            this.priceKeyword,
            this.confirmYesWord,
            this.confirmNoWord,
            this.confirm,
            this.sellConfirmSlot,
            this.nextPageWord,
            this.alreadyBoughtWord,
            this.listed,
            this.maximum
         }
      );
   }

   private int findSlotWithItem(Item... items) {
      if (!this.isContainerScreenOpen()) {
         return -1;
      } else {
         ScreenHandler currentScreenHandler = mc.player.currentScreenHandler;
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

   private static String formatSignedCoins(long autoAuctionValue) {
      return autoAuctionValue < 0L ? "-" + BuyerAppendUtils.formatText(-autoAuctionValue) : BuyerAppendUtils.formatText(autoAuctionValue);
   }

   private Params buildPricingParams(long integerValue) {
      return new Params(
         this.referencePercentile.getValue(),
         this.minListings.getValue(),
         this.minReturn.getValue(),
         this.maxUnitPrice.getValue(),
         integerValue,
         this.resellPercent.getValue(),
         this.minProfitPerItem.getValue(),
         this.salesTax.getValue()
      );
   }

   private void enterState(AutoAuctionFlipper.State state) {
      this.flipState = state;
      this.stateEnterTick = this.tickCount;
   }

   private int findFreeHotbarSlot() {
      PlayerInventory inventory = mc.player.getInventory();

      for (int i = 0; i <= 8; i++) {
         if (inventory.getStack(i).isEmpty()) {
            return i;
         }
      }

      return inventory.getSelectedSlot();
   }

   private boolean isAuctionBoardOpen() {
      if (!this.isContainerScreenOpen()) {
         return false;
      } else {
         String titleScreenText = mc.currentScreen != null
            ? KmbtSellerUtils.formatText(mc.currentScreen.getTitle().getString()).toLowerCase(Locale.ROOT)
            : "";
         String lowerCase = normalizeWord(this.ahTitleWord.getCurrentText());
         return !lowerCase.isEmpty() && titleScreenText.contains(lowerCase) ? true : this.targetFlipItem != null && this.containerHasItem(this.targetFlipItem);
      }
   }

   private void startSweep() {
      this.sweepItems.clear();

      for (Item item : this.autoPickItems.isEnabled() ? Arrays.asList(AUTO_PICK_ITEMS) : this.itemsToFlip.getContainsItemList()) {
         if (item != null && item != Items.AIR && !this.sweepItems.contains(item)) {
            this.sweepItems.add(item);
         }
      }

      if (this.sweepItems.isEmpty()) {
         if (this.notifications.isEnabled()) {
            this.sendFlipMessage("\u00a7c[Flip]\u00a7r no items chosen \u2014 add an item to flip, or turn on Auto-Pick Items.");
         }

         this.showToast("Flipper", "Pick an item or use Auto-Pick", false);
         this.setEnabled(false);
      } else {
         this.ownedCountBaseline.clear();

         for (Item botCrystalText : this.sweepItems) {
            this.ownedCountBaseline.put(botCrystalText, this.countInventoryItem(botCrystalText));
         }

         this.flipsThisSweep = 0;
         this.sweepIndex = -1;
         this.armMessageMatchers();
         this.advanceToNextItem();
      }
   }

   private void finishSweep(String flipScanText) {
      if (mc.player != null && this.isContainerScreenOpen()) {
         mc.player.closeHandledScreen();
      }

      if (this.priceScanRequested) {
         if (this.notifications.isEnabled()) {
            this.sendFlipMessage("\u00a7b[Flip]\u00a7r price scan complete.");
         }

         this.setEnabled(false);
      } else {
         this.enterState(AutoAuctionFlipper.State.COOLDOWN);
      }
   }

   @Override
   public void onDisable() {
      this.clearMessageMatchers();
      if (mc.player != null && this.isContainerScreenOpen()) {
         mc.player.closeHandledScreen();
      }

      InventoryStackUtilsHandle.handle(this);
      if (this.notifications.isEnabled() && (this.snipedItemTotal > 0 || this.listedItemTotal > 0)) {
         this.sendFlipMessage(
            "\u00a7b[Flip]\u00a7r session: sniped \u00a7f"
               + this.snipedItemTotal
               + "\u00a7r for "
               + formatCoins(this.totalSpent)
               + ", listed \u00a7f"
               + this.listedItemTotal
               + "\u00a7r for "
               + formatCoins(this.listedRevenueTotal)
               + " \u2192 net "
               + formatSignedCoins(this.listedRevenueTotal - this.totalSpent)
         );
      }

      this.resetState();
      this.priceScanRequested = false;
   }

   private void skipToNextItem(String autoAuctionText) {
      this.advanceToNextItem();
   }

   private boolean isFlipMode() {
      return !this.isAnalyzeMode() && "Flip".equals(this.mode.getCurrentOption());
   }

   public static void onChatMessageReceived(String refusedText) {
      if (refusedText != null) {
         String lowerCase = KmbtSellerUtils.formatText(refusedText).toLowerCase(Locale.ROOT);
         String alreadyBoughtTextLower32 = alreadyBoughtTextLower;
         if (alreadyBoughtTextLower32 != null && !alreadyBoughtTextLower32.isEmpty() && lowerCase.contains(alreadyBoughtTextLower32)) {
            alreadyBoughtSeen = true;
         }

         String listedTextLower2 = listedTextLower;
         if (listedTextLower2 != null && !listedTextLower2.isEmpty() && lowerCase.contains(listedTextLower2)) {
            listedMessageSeen = true;
         }

         String listFailTextLower22 = listFailTextLower;
         if (listFailTextLower22 != null && !listFailTextLower22.isEmpty() && lowerCase.contains(listFailTextLower22)) {
            listFailMessageSeen = true;
         }
      }
   }

   private void tickResellSend() {
      Item resellItem2 = this.resellItem;
      PlayerInventory inventory = mc.player.getInventory();
      if (this.resellHotbarSlot < 0) {
         this.finishResellAttempt();
      } else if (this.isContainerScreenOpen()) {
         mc.player.closeHandledScreen();
         this.lastActionTick = this.tickCount;
      } else if (inventory.getSelectedSlot() != this.resellHotbarSlot) {
         inventory.setSelectedSlot(this.resellHotbarSlot);
      } else {
         ItemStack stack = inventory.getStack(this.resellHotbarSlot);
         if (!stack.isEmpty() && stack.isOf(resellItem2)) {
            if (this.tickCount - this.lastActionTick >= this.getClickSpacingTicks()) {
               int count = stack.getCount();
               long max = Math.max(1L, this.resellUnitPrice * count);
               this.inventoryCountBeforeSell = this.countInventoryItem(resellItem2);
               this.pendingListTotal = max;
               this.pendingListCount = count;
               listedMessageSeen = false;
               listFailMessageSeen = false;
               mc.player.networkHandler.sendChatCommand(this.buildSellCommand(max, count));
               this.lastActionTick = this.tickCount;
               this.enterState(AutoAuctionFlipper.State.RESELL_CONFIRM);
            }
         } else {
            this.finishResellAttempt();
         }
      }
   }

   private void continueSameItem() {
      this.currentCandidate = null;
      this.skippedSlots.clear();
      this.pageIndex = 0;
      this.boardSyncId = -1;
      this.scannedListings.clear();
      this.resellItem = null;
      this.resellHotbarSlot = -1;
      this.hotbarSwapState = 0;
      if (this.maxTotalSpend.getValue() > 0 && this.totalSpent >= this.maxTotalSpend.getValue()) {
         if (this.notifications.isEnabled()) {
            this.sendFlipMessage("\u00a7e[Flip]\u00a7r spend cap reached (" + formatCoins(this.totalSpent) + ") \u2014 stopping.");
         }

         this.setEnabled(false);
      } else if (this.maxFlipsSweep.getValue() > 0 && this.flipsThisSweep >= this.maxFlipsSweep.getValue()) {
         this.finishSweep("flip cap");
      } else {
         this.enterState(AutoAuctionFlipper.State.SEARCH);
      }
   }

   private void tickResellPrep() {
      Item resellItem2 = this.resellItem;
      if (resellItem2 == null) {
         this.advanceToNextItem();
      } else if (this.isContainerScreenOpen()) {
         mc.player.closeHandledScreen();
      } else if (mc.currentScreen == null) {
         int maximumListingsLimit = this.getSurplusCount(resellItem2);
         if (maximumListingsLimit <= 0) {
            this.finishResellAttempt();
         } else {
            PlayerInventory inventory = mc.player.getInventory();

            for (int i = 0; i <= 8; i++) {
               ItemStack stack = inventory.getStack(i);
               if (stack.isOf(resellItem2) && stack.getCount() > 0 && stack.getCount() <= maximumListingsLimit) {
                  this.resellHotbarSlot = i;
                  this.beginResellSend();
                  return;
               }
            }

            if (this.hotbarSwapState == 1) {
               if (this.resellHotbarSlot >= 0 && inventory.getStack(this.resellHotbarSlot).isOf(resellItem2)) {
                  this.beginResellSend();
               } else {
                  if (this.getTicksInState() > 20) {
                     this.hotbarSwapState = 0;
                  }
               }
            } else {
               int maximumListingsValue = -1;

               for (int j = 9; j <= 35; j++) {
                  ItemStack stack2 = inventory.getStack(j);
                  if (stack2.isOf(resellItem2) && stack2.getCount() > 0 && stack2.getCount() <= maximumListingsLimit) {
                     maximumListingsValue = j;
                     break;
                  }
               }

               if (maximumListingsValue < 0) {
                  if (this.notifications.isEnabled()) {
                     this.sendFlipMessage(
                        "\u00a7e[Flip]\u00a7r kept " + getItemPath(resellItem2) + " \u00a77(only stacks larger than surplus \u2014 won't touch owned stock)"
                     );
                  }

                  this.finishResellAttempt();
               } else if (InventoryStackUtilsHandle.canFlagForObject(this)) {
                  int freeHotbarSlot = this.findFreeHotbarSlot();
                  mc.interactionManager
                     .clickSlot(
                        mc.player.playerScreenHandler.syncId,
                        HotbarSlotUtils.toScreenSlot(maximumListingsValue),
                        HotbarSlotUtils.requireHotbarSlot(freeHotbarSlot),
                        SlotActionType.SWAP,
                        mc.player
                     );
                  this.resellHotbarSlot = freeHotbarSlot;
                  this.hotbarSwapState = 1;
                  this.lastActionTick = this.tickCount;
               }
            }
         }
      }
   }

   private String buildSellCommand(long priceValue, int logoProfileCounter) {
      String stripLeadingSlash2 = stripLeadingSlash(this.sellCommand.getCurrentText());
      return stripLeadingSlash2.replace("{price}", Long.toString(priceValue)).replace("{count}", Integer.toString(logoProfileCounter)).replace("{amount}", Integer.toString(logoProfileCounter));
   }

   private void tickResellVerify() {
      Item resellItem2 = this.resellItem;
      if (listFailMessageSeen) {
         listFailMessageSeen = false;
         this.reportListingFailure("server refused");
      } else if (this.countInventoryItem(resellItem2) >= this.inventoryCountBeforeSell && !listedMessageSeen) {
         if (this.isContainerScreenOpen() && this.tickCount - this.lastActionTick >= this.getClickSpacingTicks()) {
            this.tryClickConfirmButton(true);
            this.lastActionTick = this.tickCount;
         } else {
            if (this.getTicksInState() > 100) {
               this.reportListingFailure("listing unconfirmed");
            }
         }
      } else {
         this.recordListedBatch();
      }
   }

   private void tickClickListing() {
      if (this.currentCandidate != null && this.isSameBoardScreen()) {
         if (this.tickCount - this.lastActionTick >= this.getClickSpacingTicks()) {
            if (!this.hasInventoryRoomFor(this.targetFlipItem)) {
               this.skipToNextItem("inventory full \u2014 make room");
            } else {
               ItemStack stack = mc.player.currentScreenHandler.getSlot(this.currentCandidate.slot()).getStack();
               if (!stack.isEmpty() && stack.isOf(this.targetFlipItem)) {
                  this.inventoryCountBeforeBuy = this.countInventoryItem(this.targetFlipItem);
                  alreadyBoughtSeen = false;
                  this.confirmKeyStep = 0;
                  mc.interactionManager.clickSlot(this.boardSyncId, this.currentCandidate.slot(), 0, SlotActionType.PICKUP, mc.player);
                  this.lastActionTick = this.tickCount;
                  this.enterState(AutoAuctionFlipper.State.CONFIRM);
               } else {
                  this.skippedSlots.add(this.currentCandidate.slot());
                  this.enterState(AutoAuctionFlipper.State.SCAN);
               }
            }
         }
      } else {
         this.enterState(AutoAuctionFlipper.State.SCAN);
      }
   }

   private void tickCooldown() {
      if (this.isContainerScreenOpen()) {
         mc.player.closeHandledScreen();
      } else {
         if (this.getTicksInState() >= this.getRefreshCooldownTicks()) {
            this.startSweep();
         }
      }
   }

   private void advanceToNextItem() {
      this.currentCandidate = null;
      this.skippedSlots.clear();
      this.pageIndex = 0;
      this.missCount = 0;
      this.boardSyncId = -1;
      this.scannedListings.clear();
      this.resellItem = null;
      this.resellHotbarSlot = -1;
      this.hotbarSwapState = 0;
      if (!this.isAnalyzeMode() && this.maxTotalSpend.getValue() > 0 && this.totalSpent >= this.maxTotalSpend.getValue()) {
         if (this.notifications.isEnabled()) {
            this.sendFlipMessage("\u00a7e[Flip]\u00a7r spend cap reached (" + formatCoins(this.totalSpent) + ") \u2014 stopping.");
         }

         this.setEnabled(false);
      } else if (!this.isAnalyzeMode() && this.maxFlipsSweep.getValue() > 0 && this.flipsThisSweep >= this.maxFlipsSweep.getValue()) {
         this.finishSweep("flip cap");
      } else {
         this.sweepIndex++;
         if (this.sweepIndex >= this.sweepItems.size()) {
            this.finishSweep("sweep complete");
         } else {
            this.targetFlipItem = this.sweepItems.get(this.sweepIndex);
            this.enterState(AutoAuctionFlipper.State.SEARCH);
         }
      }
   }

   private void tickResellConfirm() {
      Item resellItem2 = this.resellItem;
      if (listFailMessageSeen) {
         listFailMessageSeen = false;
         this.reportListingFailure("server refused");
      } else if (this.isContainerScreenOpen()) {
         if (this.tickCount - this.lastActionTick >= this.getClickSpacingTicks()) {
            if (this.tryClickConfirmButton(true)) {
               this.lastActionTick = this.tickCount;
               this.enterState(AutoAuctionFlipper.State.RESELL_VERIFY);
            }
         }
      } else if (this.countInventoryItem(resellItem2) >= this.inventoryCountBeforeSell && !listedMessageSeen) {
         if (this.getTicksInState() > 80) {
            this.reportListingFailure("no sell GUI");
         }
      } else {
         this.recordListedBatch();
      }
   }

   private void announceStart() {
      if (this.notifications.isEnabled() && !this.priceScanRequested) {
         int analyzeEpricingCounter = 0;
         if (this.autoPickItems.isEnabled()) {
            analyzeEpricingCounter = AUTO_PICK_ITEMS.length;
         } else {
            for (Item item : this.itemsToFlip.getContainsItemList()) {
               if (item != null && item != Items.AIR) {
                  analyzeEpricingCounter++;
               }
            }
         }

         String analyzeEpricingText = this.isAnalyzeMode()
            ? "\u00a7epricing (Analyze \u2014 buys nothing)"
            : ("Snipe Only".equals(this.mode.getCurrentOption()) ? "\u00a7asniping" : "\u00a7aflipping");
         String analyzeEpricingText2 = this.minReturn.getValue() > 0 ? " + " + this.minReturn.getValue() + "% return" : "";
         this.sendFlipMessage(
            "\u00a7b[Flip]\u00a7r "
               + analyzeEpricingText
               + " \u00a7f"
               + analyzeEpricingCounter
               + (this.autoPickItems.isEnabled() ? "\u00a7r auto-picked items" : "\u00a7r item(s)")
               + " \u00a77\u00b7 buy anything netting \u00a7f"
               + formatCoins(this.minProfitPerItem.getValue())
               + "\u00a77+"
               + analyzeEpricingText2
               + " \u00b7 resell "
               + this.resellPercent.getValue()
               + "% after "
               + this.salesTax.getValue()
               + "% tax"
         );
      }
   }

   private void finishPurchase() {
      int receivedSnipedValue = this.countInventoryItem(this.targetFlipItem) - this.inventoryCountBeforeBuy;
      if (receivedSnipedValue <= 0) {
         this.registerMissedListing("no items received");
      } else {
         this.snipedItemTotal += receivedSnipedValue;
         this.totalSpent = this.totalSpent + this.currentCandidate.totalPrice();
         this.flipsThisSweep++;
         this.missCount = 0;
         if (this.notifications.isEnabled()) {
            this.sendFlipMessage(
               "\u00a7a[Flip]\u00a7r sniped \u00a7f"
                  + receivedSnipedValue
                  + "\u00d7\u00a7r "
                  + getItemPath(this.targetFlipItem)
                  + " for "
                  + formatCoins(this.currentCandidate.totalPrice())
                  + " \u00a77(market "
                  + formatCoins(this.marketUnitPrice)
                  + ", ~"
                  + formatCoins(this.currentCandidate.totalProfit())
                  + " profit)"
            );
         }

         this.showToast("Sniped", receivedSnipedValue + "\u00d7 " + getItemPath(this.targetFlipItem) + " \u00b7 " + formatCoins(this.currentCandidate.totalPrice()), true);
         if (this.isFlipMode()) {
            this.resellItem = this.targetFlipItem;
            this.hotbarSwapState = 0;
            this.resellHotbarSlot = -1;
            this.enterState(AutoAuctionFlipper.State.RESELL_PREP);
         } else {
            this.continueSameItem();
         }
      }
   }

   private void tickSearch() {
      if (this.isContainerScreenOpen()) {
         mc.player.closeHandledScreen();
         this.lastActionTick = this.tickCount;
      } else if (mc.currentScreen == null) {
         if (this.tickCount - this.lastActionTick >= 4) {
            mc.player.networkHandler.sendChatCommand(this.buildSearchCommand(this.targetFlipItem));
            this.lastActionTick = this.tickCount;
            this.boardSyncId = -1;
            this.pageIndex = 0;
            this.skippedSlots.clear();
            this.enterState(AutoAuctionFlipper.State.SCAN);
         }
      }
   }

   private void tickConfirmPurchase() {
      if (alreadyBoughtSeen) {
         alreadyBoughtSeen = false;
         this.registerMissedListing("already bought");
      } else if (this.countInventoryItem(this.targetFlipItem) > this.inventoryCountBeforeBuy) {
         this.finishPurchase();
      } else {
         if (this.isContainerScreenOpen()) {
            boolean unconfirmedListingFlag = mc.player.currentScreenHandler.syncId != this.boardSyncId || this.isPurchaseConfirmScreen();
            if (unconfirmedListingFlag) {
               if (this.tickCount - this.lastActionTick < this.getClickSpacingTicks()) {
                  return;
               }

               if (this.tryClickConfirmButton(false)) {
                  this.lastActionTick = this.tickCount;
                  this.enterState(AutoAuctionFlipper.State.VERIFY);
                  return;
               }

               if (this.confirmKeyStep == 0) {
                  this.pressScreenKey(263);
                  this.confirmKeyStep = 1;
                  this.lastActionTick = this.tickCount;
                  return;
               }

               this.pressScreenKey(257);
               this.confirmKeyStep = 0;
               this.lastActionTick = this.tickCount;
               this.enterState(AutoAuctionFlipper.State.VERIFY);
               return;
            }

            if (this.currentCandidate != null) {
               ItemStack stack = mc.player.currentScreenHandler.getSlot(this.currentCandidate.slot()).getStack();
               if (stack.isEmpty() || !stack.isOf(this.targetFlipItem)) {
                  this.registerMissedListing("listing gone");
                  return;
               }
            }
         }

         if (this.getTicksInState() > 40) {
            this.registerMissedListing("no confirm dialog");
         }
      }
   }

   private int getSurplusCount(Item item) {
      return Math.max(0, this.countInventoryItem(item) - this.ownedCountBaseline.getOrDefault(item, 0));
   }

   private void tickVerifyPurchase() {
      if (alreadyBoughtSeen) {
         alreadyBoughtSeen = false;
         this.registerMissedListing("already bought");
      } else if (this.countInventoryItem(this.targetFlipItem) > this.inventoryCountBeforeBuy) {
         this.finishPurchase();
      } else {
         if (this.getTicksInState() > 60) {
            this.registerMissedListing("item never arrived");
         }
      }
   }

   private boolean containerHasItem(Item item2) {
      if (!this.isContainerScreenOpen()) {
         return false;
      } else {
         ScreenHandler currentScreenHandler = mc.player.currentScreenHandler;
         int slotStackLimit = currentScreenHandler.slots.size() - 36;

         for (int i = 0; i < slotStackLimit; i++) {
            if (currentScreenHandler.getSlot(i).getStack().isOf(item2)) {
               return true;
            }
         }

         return false;
      }
   }

   private void clearMessageMatchers() {
      alreadyBoughtTextLower = null;
      listedTextLower = null;
      listFailTextLower = null;
      alreadyBoughtSeen = false;
      listedMessageSeen = false;
      listFailMessageSeen = false;
   }

   private void reportListingFailure(String sellConfirmText) {
      if (this.notifications.isEnabled()) {
         String sellConfirmText2;
         if (!this.isContainerScreenOpen()) {
            sellConfirmText2 = " \u00a78[no sell GUI opened \u2014 check the Sell Command text / price / listing cap]";
         } else {
            boolean sellConfirmFlag = this.findSlotByNameKeyword(this.confirmYesWord.getCurrentText()) >= 0 || this.findSlotWithItem(SELL_CONFIRM_ITEMS) >= 0;
            sellConfirmText2 = sellConfirmFlag
               ? " \u00a78[sell GUI open, confirm found \u2014 server refused the price?]"
               : " \u00a78[sell GUI open but no confirm button \u2014 retune Sell Confirm Slot / Confirm Yes Word]";
         }

         this.sendFlipMessage("\u00a7c[Flip]\u00a7r couldn't list " + getItemPath(this.resellItem) + " \u00a77(" + sellConfirmText + ")" + sellConfirmText2);
      }

      this.finishResellAttempt();
   }

   private String buildSearchCommand(Item item2) {
      String stripLeadingSlash2 = stripLeadingSlash(this.searchCommand.getCurrentText());
      return stripLeadingSlash2.replace("{item}", this.getSearchTerm(item2));
   }

   public static boolean hasActiveMessageMatchers() {
      return alreadyBoughtTextLower != null || listedTextLower != null || listFailTextLower != null;
   }

   private static enum State {
      IDLE,
      SEARCH,
      SCAN,
      NEXT_PAGE,
      CLICK,
      CONFIRM,
      VERIFY,
      RESELL_PREP,
      RESELL_SEND,
      RESELL_CONFIRM,
      RESELL_VERIFY,
      COOLDOWN;
   }
}
