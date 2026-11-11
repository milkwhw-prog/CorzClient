package com.corz.client.categories.donutsmp;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.config.StringBase;
import com.corz.client.utils.ToolListingParser.Lines;
import com.corz.client.gui.DisabledEnabled;
import com.corz.client.inventory.DealBoard;
import com.corz.client.utils.ComparingUtils.Rules;
import com.corz.client.utils.ShardAxeType.Tool;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.ParentElement;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.GameMenuScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.dialog.DialogScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import com.corz.client.categories.bots.AutoElytraPilot;
import com.corz.client.utils.ToolListingParser;
import com.corz.client.utils.AutoVanish;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.categories.bots.AutoAuctionFlipper;
import com.corz.client.categories.bots.AutoBuilder;
import com.corz.client.categories.bots.AutoElytraFinder;
import com.corz.client.utils.AuctionChatMatchUtils;
import com.corz.client.utils.BuyerAppendUtils;
import com.corz.client.utils.ComparingUtils;
import com.corz.client.utils.DaysHoursUtils;
import com.corz.client.utils.ElseMaxData;
import com.corz.client.utils.ElseMaxUtils;
import com.corz.client.utils.FormatFakesUtils;
import com.corz.client.utils.IconsPngType;
import com.corz.client.utils.KmbtSellerUtils;
import com.corz.client.utils.MaxDataOver;
import com.corz.client.utils.ShardAxeType;

public class SpecialItemBuyer extends Module {
   private SpecialItemBuyer.Goal currentGoal;
   private static final String BUY_BEST = "Buy Best";
   private final StringBase confirmButtonWord;
   private static final String DIDNT_BUY_NO_LONGER_FITS = "Didn't buy: it no longer fits your limits.";
   private final BooleanSetting shardShovel;
   private final StringBase nextPageWord;
   private boolean boardScreenCloseRequested;
   private long lastStatusSecond;
   private int queuedItemIndex;
   private final List<ShardAxeType> queuedItemTypes;
   private final BooleanSetting drill = new BooleanSetting(
      "Drill", "Look for drills (Shard Pickaxe) when you turn this on with its key or switch. The Deal Board always checks all four.", true
   );
   private String lastResultMessage;
   private int tickCounter;
   private final Map<ShardAxeType, ElseMaxData> boughtAwaitingReceipt;
   private String scanSummaryText;
   private final IntegerSetting maxTotalSpend;
   private int baselineInventoryCount;
   private ElseMaxData targetListing;
   private SpecialItemBuyer.Run runKind;
   private final StringBase alreadyBoughtWord;
   private ShardAxeType receivedItemType;
   private volatile boolean boughtBySomeoneElse;
   private final StringBase shardAxeSearch;
   private int confirmKeyPhase;
   private int unconfirmedBuyCount;
   private Object networkHandler;
   private int retryCount;
   private final StringBase noMoneyWord;
   private final IntegerSetting minTimeLeft;
   private final StringBase confirm;
   private SpecialItemBuyer.Run pendingRunKind;
   private volatile boolean notEnoughMoneyFlag;
   private final List<ElseMaxData> foundListings;
   private static final String LISTING_GONE_CHANGED = "That listing is gone or changed, so I didn't buy anything. Press Scan again.";
   private boolean blockedByOpenMenu;
   private long item;
   private volatile String noMoneyWordLower;
   private final IntegerSetting clickSpacing;
   private int popupMissingSinceTick;
   private final Map<ShardAxeType, ElseMaxData> unconfirmedListings;
   private final StringBase selfDestructWord;
   private static final Item[] ACCEPT_BUTTON_ITEMS = new Item[]{
      Items.LIME_STAINED_GLASS_PANE, Items.GREEN_STAINED_GLASS_PANE, Items.LIME_WOOL, Items.GREEN_WOOL, Items.LIME_DYE, Items.EMERALD, Items.EMERALD_BLOCK
   };
   private ElseMaxData pendingPurchase;
   private int confirmClickCount;
   private long receivedCoins;
   private final StringBase shardShovelSearch;
   private boolean runCallbackOnDisable;
   private static final Item[] CANCEL_BUTTON_ITEMS = new Item[]{Items.RED_STAINED_GLASS_PANE, Items.RED_WOOL, Items.BARRIER};
   private long lastResultTimeMs;
   private final BooleanSetting notifications;
   private volatile String alreadyBoughtWordLower;
   private Runnable onFinishCallback;
   private final IntegerSetting maxAhPages;
   private Runnable pendingRunCallback;
   private int pagesScanned;
   private static final String DIDNT_BUY_INVENTORY_FULL = "Didn't buy: your inventory is full.";
   private final BooleanSetting shardAxe;
   private final IntegerSetting maxPrice;
   private int boughtItemCount;
   private final StringBase ahTitleWord;
   private final Set<String> wrongToolTypeSlots;
   private boolean confirmDialogSeen;
   private boolean buyBestMode;
   private ShardAxeType currentItemType;
   private ElseMaxData clickTargetListing;
   private int lastActionTick;
   private final StringBase sellWandSearch;
   private static final String TURN_OFF_OTHER_BOTS_MESSAGE = "Turn off Auto Builder, Auto Auction Flipper and the elytra bots first. They use /ah too.";
   private long lastAhCommandNanos;
   private boolean hasRetriedSearch;
   private ElseMaxData boardBuyListing;
   private volatile boolean inventoryFullFlag;
   private boolean purchaseClickPending;
   private int syncId;
   private Set<String> slotSignatureSnapshot;
   private SpecialItemBuyer.State buyState;
   private static final String SHOW_DEALS = "Show Deals";
   private final Map<ShardAxeType, MaxDataOver> scanDealMap;
   private static final String SWITCH_ONLY_CATEGORY = "On/Off Switch Only";
   private int stateEnteredTick;
   private final StringBase drillSearch;
   private static final String DIDNT_BUY_LISTING_CHANGED = "Didn't buy: that listing was sold or changed. Press Scan again.";
   private static final String LISTING_NO_LONGER_FITS = "That listing doesn't fit your limits anymore (Max Price, Min Time Left or Max Total Spend), so I didn't buy anything.";
   private final Set<String> unparsedListingSlots;
   private long potentialExtraSpend;
   private static final String SCAN_TOO_OLD_MESSAGE = "This scan is over " + DaysHoursUtils.formatText(600L) + " old. Press Scan again, then buy.";
   private final BooleanSetting sellWand = new BooleanSetting(
      "Sell Wand", "Look for sell wands (Shard Sell Axe) when you turn this on with its key or switch. The Deal Board always checks all four.", true
   );
   private String lastPageSignature;
   private final StringBase priceKeyword;
   private ElseMaxData pendingRunListing;
   private int pageWaitStartTick;
   private long unconfirmedSpend;
   private final EnumSetting modeSetting;
   private final StringBase sure;
   private final ConcurrentLinkedQueue<SpecialItemBuyer.Receipt> receiptQueue;
   private int currentPage;

   public MaxDataOver getDealScan(ShardAxeType shardAxeType) {
      return shardAxeType == null ? null : this.scanDealMap.get(shardAxeType);
   }

   private static Lines buildLines(ItemStack stack) {
      return new Lines(stack.getName().getString(), getLoreLines(stack), getToolKind(stack), getRemainingDurabilityFraction(stack));
   }

   private Set<String> getSlotSignatures() {
      var hashSet = new HashSet();
      if (!this.isContainerScreenOpen()) {
         return hashSet;
      } else {
         ScreenHandler currentScreenHandler = mc.player.currentScreenHandler;

         for (int i = 0; i < this.getListingSlotCount(); i++) {
            ItemStack stack = currentScreenHandler.getSlot(i).getStack();
            if (!stack.isEmpty()) {
               hashSet.add(this.getSlotSignature(stack));
            }
         }

         return hashSet;
      }
   }

   private void restartSearch() {
      this.foundListings.clear();
      this.wrongToolTypeSlots.clear();
      this.unparsedListingSlots.clear();
      this.syncId = -1;
      this.currentPage = 1;
      this.pagesScanned = 0;
      this.lastPageSignature = null;
      this.slotSignatureSnapshot = null;
      this.hasRetriedSearch = false;
      this.targetListing = null;
      this.clickTargetListing = null;
      this.confirmKeyPhase = 0;
      this.confirmClickCount = 0;
      this.confirmDialogSeen = false;
      this.popupMissingSinceTick = -1;
      this.currentGoal = SpecialItemBuyer.Goal.SCAN;
      this.enterState(SpecialItemBuyer.State.SEARCH);
   }

   private boolean nameMatchesAnyWord(ItemStack stack, String rootText) {
      if (rootText != null && !rootText.isBlank()) {
         String lowerCase = KmbtSellerUtils.formatText(stack.getName().getString()).toLowerCase(Locale.ROOT);

         for (String rootText2 : rootText.toLowerCase(Locale.ROOT).split(",")) {
            String trim = rootText2.trim();
            if (!trim.isEmpty() && lowerCase.contains(trim)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private String getScreenWidgetText() {
      StringBuilder stringBuilder = new StringBuilder();
      if (mc.currentScreen != null) {
         appendWidgetMessages(mc.currentScreen, stringBuilder, 0);
      }

      return stringBuilder.toString();
   }

   private boolean isOtherAhBotActive() {
      return this.isOtherModuleRunning(AutoAuctionFlipper.class)
         || this.isOtherModuleRunning(AutoBuilder.class)
         || this.isOtherModuleRunning(AutoElytraFinder.class)
         || this.isOtherModuleRunning(AutoElytraPilot.class);
   }

   private String getRestartHint() {
      return this.runKind == SpecialItemBuyer.Run.BOARD_BUY
         ? "press Buy again on the Deal Board"
         : (this.runKind == SpecialItemBuyer.Run.BOARD_SCAN ? "press Scan again on the Deal Board" : "turn me on again");
   }

   @Override
   public boolean requiresContainerBindPolling() {
      return this.isEnabled();
   }

   private String describeWhyNoListingFits() {
      if (this.foundListings.isEmpty()) {
         return this.unparsedListingSlots.isEmpty()
            ? "no listings found"
            : "couldn't read " + formatPlural(this.unparsedListingSlots.size(), "listing") + ", check 'Self Destruct Word' and 'Price Keyword'";
      } else {
         long listingsListingLimit = this.maxPrice.getValue();
         long listingsListingLimit2 = this.minTimeLeft.getValue() * 60L;
         long budgetLeft = this.getRemainingLimitRules().budgetLeft();
         int listingsListingCounter = 0;
         int listingsListingCounter2 = 0;
         int listingsListingCounter3 = 0;
         int listingslistingitemsSize = this.foundListings.size();

         for (ElseMaxData elseMaxData : this.foundListings) {
            if (listingsListingLimit > 0L && elseMaxData.maxCharge() > listingsListingLimit) {
               listingsListingCounter++;
            }

            if (elseMaxData.secondsLeft() < listingsListingLimit2) {
               listingsListingCounter2++;
            }

            if (budgetLeft > 0L && elseMaxData.maxCharge() > budgetLeft) {
               listingsListingCounter3++;
            }
         }

         if (listingsListingCounter2 == listingslistingitemsSize) {
            return "none with " + this.minTimeLeft.getValue() + "m+ left";
         } else if (listingsListingCounter == listingslistingitemsSize) {
            return "all could cost more than Max Price (" + formatCoins(listingsListingLimit) + ", /ah prices are rounded)";
         } else {
            return listingsListingCounter3 == listingslistingitemsSize ? "all cost more than your Max Total Spend has left (" + formatCoins(budgetLeft) + ")" : "none fit your limits";
         }
      }
   }

   private boolean recordSuccessfulBuy(ElseMaxData elseMaxData, String shardAxeText) {
      boolean shardAxeFlag = this.receivedItemType == this.currentItemType && this.receivedCoins > 0L;
      long shardAxeBound = shardAxeFlag ? this.receivedCoins : elseMaxData.price();
      long maxPrice = Math.max(shardAxeBound, elseMaxData.price());
      this.item += maxPrice;
      this.potentialExtraSpend = this.potentialExtraSpend + Math.max(0L, elseMaxData.maxCharge() - maxPrice);
      this.boughtItemCount++;
      this.pendingPurchase = null;
      this.purchaseClickPending = false;
      if (!shardAxeFlag) {
         this.boughtAwaitingReceipt.put(this.currentItemType, elseMaxData);
      }

      MaxDataOver scandealmap = this.scanDealMap.get(this.currentItemType);
      if (scandealmap != null) {
         this.scanDealMap.put(this.currentItemType, ElseMaxUtils.getRankedListings(scandealmap, this.targetListing != null ? this.targetListing : elseMaxData));
      }

      this.sendChatMessage(
         "\u00a7a[Deals]\u00a7r Bought "
            + this.currentItemType.getCommandName()
            + " for "
            + formatCoins(shardAxeBound)
            + " ("
            + formatListingValue(elseMaxData)
            + ", "
            + DaysHoursUtils.formatText(elseMaxData.secondsLeft())
            + " left)"
            + shardAxeText
      );
      this.showToast("Bought " + this.currentItemType.getCommandName(), formatCoins(shardAxeBound) + ", " + formatListingValue(elseMaxData), true);
      if (shardAxeFlag && AuctionChatMatchUtils.canChargePrice(shardAxeBound, elseMaxData)) {
         this.abortRun(this.formatOverchargeMessage(shardAxeBound, elseMaxData));
         return false;
      } else {
         return true;
      }
   }

   private static List<String> getLoreLines(ItemStack stack) {
      var arrayList = new ArrayList();
      LoreComponent loreComponent = stack.get(DataComponentTypes.LORE);
      if (loreComponent != null) {
         for (Text text : loreComponent.lines()) {
            arrayList.add(text.getString());
         }
      }

      return arrayList;
   }

   public String getStatusText() {
      ShardAxeType currentItemType2 = this.currentItemType;
      if (this.buyState == SpecialItemBuyer.State.IDLE) {
         return "";
      } else if (currentItemType2 == null) {
         return "Starting";
      } else if (this.buyState == SpecialItemBuyer.State.SEARCH && this.blockedByOpenMenu) {
         return "Waiting: close this menu";
      } else if (this.currentGoal == SpecialItemBuyer.Goal.SCAN) {
         return "Scanning " + currentItemType2.getCommandName() + ", page " + Math.max(1, this.currentPage);
      } else {
         ElseMaxData elseMaxData = this.targetListing;
         return "Buying " + currentItemType2.getCommandName() + (elseMaxData != null ? " for " + formatCoins(elseMaxData.price()) : "");
      }
   }

   private Rules buildLimitRules(long integerValue) {
      long max2 = 0L;
      if (this.maxTotalSpend.getValue() > 0) {
         max2 = Math.max(1L, this.maxTotalSpend.getValue() - integerValue);
      }

      return new Rules(this.maxPrice.getValue(), this.minTimeLeft.getValue() * 60L, max2);
   }

   private boolean isSpendLimitReached() {
      return this.maxTotalSpend.getValue() > 0 && this.item + this.unconfirmedSpend + this.potentialExtraSpend >= this.maxTotalSpend.getValue();
   }

   private void resetState() {
      this.buyState = SpecialItemBuyer.State.IDLE;
      this.currentGoal = SpecialItemBuyer.Goal.SCAN;
      this.tickCounter = 0;
      this.stateEnteredTick = 0;
      this.lastActionTick = -1000;
      this.pageWaitStartTick = -1;
      this.queuedItemTypes.clear();
      this.queuedItemIndex = -1;
      this.currentItemType = null;
      this.networkHandler = null;
      this.runKind = SpecialItemBuyer.Run.TOGGLE;
      this.boardBuyListing = null;
      this.onFinishCallback = null;
      this.runCallbackOnDisable = false;
      this.boardScreenCloseRequested = false;
      this.blockedByOpenMenu = false;
      this.receiptQueue.clear();
      this.unconfirmedListings.clear();
      this.boughtAwaitingReceipt.clear();
      this.clearListingState();
   }

   private boolean isContainerScreenOpen() {
      return mc.player != null
         && mc.currentScreen instanceof HandledScreen
         && !(mc.currentScreen instanceof InventoryScreen)
         && mc.player.currentScreenHandler != null
         && mc.player.currentScreenHandler != mc.player.playerScreenHandler;
   }

   private void updateScanForBoardListing() {
      if (this.runKind == SpecialItemBuyer.Run.BOARD_BUY && this.boardBuyListing != null) {
         MaxDataOver scandealmap = this.scanDealMap.get(this.boardBuyListing.kind());
         if (scandealmap != null) {
            this.scanDealMap.put(this.boardBuyListing.kind(), ElseMaxUtils.getRankedListings(scandealmap, this.boardBuyListing));
         }
      }
   }

   private void showToast(String specialBuyerText, String substringText, boolean specialBuyerFlag) {
      DisabledEnabled.getInstance().handleDisabledEnabled(specialBuyerText, substringText, specialBuyerFlag);
   }

   private void abortRun(String specialBuyerText) {
      this.endRunWithMessages(specialBuyerText, specialBuyerText);
   }

   private void finishRun() {
      this.runCallbackOnDisable = true;
      this.setEnabled(false);
   }

   private String formatOverchargeMessage(long priceKindValue, ElseMaxData elseMaxData) {
      return "The server charged " + formatCoins(priceKindValue) + " for a " + elseMaxData.kind().getCommandName() + " listed at " + formatCoins(elseMaxData.price()) + ", so I stopped buying.";
   }

   private boolean tryClickAcceptButton() {
      if (!this.isContainerScreenOpen()) {
         return false;
      } else {
         int slotsLimit = mc.player.currentScreenHandler.slots.size() - 36;
         int slotStack = this.findSlotByName(this.confirmButtonWord.getCurrentText());
         if (slotStack < 0) {
            slotStack = this.findSlotByItems(ACCEPT_BUTTON_ITEMS);
         }

         if (slotStack >= 0 && slotStack < slotsLimit) {
            this.clickSlot(slotStack);
            return true;
         } else {
            return false;
         }
      }
   }

   private boolean hasFreeInventorySlot() {
      PlayerInventory inventory = mc.player.getInventory();

      for (int i = 0; i < 36; i++) {
         if (inventory.getStack(i).isEmpty()) {
            return true;
         }
      }

      return false;
   }

   @Override
   protected void onPacketReceive(AutoVanish autoVanish) {
      if (this.buyState != SpecialItemBuyer.State.IDLE && autoVanish.packet instanceof GameMessageS2CPacket gameMessageS2CPacket && !gameMessageS2CPacket.overlay() && gameMessageS2CPacket.content() != null) {
         this.onChatMessage(gameMessageS2CPacket.content().getString());
      }
   }

   private boolean isScanOnly() {
      return this.runKind == SpecialItemBuyer.Run.BOARD_BUY
         ? false
         : this.runKind == SpecialItemBuyer.Run.BOARD_SCAN || !this.buyBestMode || !"Buy Best".equals(this.modeSetting.getCurrentOption());
   }

   private void pressKey(int pressedReleasedValue) {
      Screen currentScreen = mc.currentScreen;
      this.lastActionTick = this.tickCounter;
      if (currentScreen != null) {
         try {
            KeyInput keyInput = new KeyInput(pressedReleasedValue, 0, 0);
            currentScreen.keyPressed(keyInput);
            currentScreen.keyReleased(keyInput);
         } catch (Throwable e) {
         }
      }
   }

   private static double getRemainingDurabilityFraction(ItemStack stack) {
      return stack.isDamageable() && stack.getMaxDamage() > 0 ? 1.0 - (double)stack.getDamage() / stack.getMaxDamage() : 1.0;
   }

   private void endRunWithMessages(String dealsBoughtText, String specialBuyerText) {
      this.closeOpenScreen();
      String dealsBoughtText3 = this.pendingPurchase != null && this.purchaseClickPending
         ? " The " + formatCoins(this.pendingPurchase.price()) + " buy I clicked may have gone through, check your inventory."
         : "";
      this.sendChatMessage(
         "\u00a7c[Deals]\u00a7r "
            + dealsBoughtText
            + dealsBoughtText3
            + (this.boughtItemCount > 0 ? " Bought " + formatPlural(this.boughtItemCount, "item") + " for " + formatCoins(this.item) + " before that." : "")
      );
      this.showToast(this.getCategory(), dealsBoughtText, false);
      this.setBoardResultMessage(specialBuyerText + dealsBoughtText3);
      this.finishRun();
   }

   private void onConfirmMismatch(String popupDidnText) {
      this.closeOpenScreen();
      if (this.confirmClickCount > 0) {
         this.giveUpUnconfirmedBuy();
      } else {
         this.purchaseClickPending = false;
         this.onListingChanged(popupDidnText);
      }
   }

   private void tickBoardState() {
      if (!this.isAuctionScreenOpen()) {
         this.pageWaitStartTick = -1;
         if (this.getTicksInState() > 100) {
            this.onAuctionNotOpened();
         }
      } else {
         this.syncId = mc.player.currentScreenHandler.syncId;
         if (this.lastPageSignature != null) {
            String text = this.buildPageSignature();
            if (text.equals(this.lastPageSignature) || AuctionChatMatchUtils.hasMajorityOverlap(this.slotSignatureSnapshot, this.getSlotSignatures())) {
               this.lastPageSignature = text;
               if (this.getTicksInState() > 40) {
                  this.lastPageSignature = null;
                  this.slotSignatureSnapshot = null;
                  this.onNoMorePages();
               }

               return;
            }

            this.lastPageSignature = null;
            this.slotSignatureSnapshot = null;
            this.pageWaitStartTick = this.tickCounter;
         }

         if (this.pageWaitStartTick < 0) {
            this.pageWaitStartTick = this.tickCounter;
         }

         if (this.tickCounter - this.pageWaitStartTick >= 6) {
            if (this.currentGoal == SpecialItemBuyer.Goal.SCAN) {
               this.readScanPage();
            } else {
               this.findTargetOnPage();
            }
         }
      }
   }

   private void closeOpenScreen() {
      if (mc.player != null) {
         if (this.isContainerScreenOpen()) {
            mc.player.closeHandledScreen();
         } else if (mc.currentScreen instanceof DialogScreen || this.isConfirmDialogOpen()) {
            this.closeScreen();
         }
      }
   }

   private void onListingChanged(String didnChangedText) {
      this.retryCount++;
      this.closeOpenScreen();
      if (this.runKind == SpecialItemBuyer.Run.BOARD_BUY) {
         if (isSoldReason(didnChangedText)) {
            this.updateScanForBoardListing();
            this.endRunWithMessages(
               "That listing is gone or changed, so I didn't buy anything. Press Scan again.",
               "Didn't buy: that listing was sold or changed. Press Scan again."
            );
         } else {
            this.endRunWithMessages("I couldn't buy it (" + didnChangedText + "). Press Scan again.", "Didn't buy: " + didnChangedText + ".");
         }
      } else if (this.retryCount >= 3) {
         this.sendChatMessage(
            "\u00a7e[Deals]\u00a7r " + this.currentItemType.getCommandName() + ": gave up after " + this.retryCount + " tries (" + didnChangedText + ")."
         );
         this.advanceToNextItemType();
      } else {
         this.sendVerboseMessage("\u00a77[Deals]\u00a7r " + this.currentItemType.getCommandName() + ": missed it (" + didnChangedText + "), looking again.");
         this.restartSearch();
      }
   }

   private void tickVerifyState() {
      if (!this.abortOnPurchaseError()) {
         if (this.hasReceivedCurrentItem()) {
            this.onPurchaseDetected(this.pendingPurchase);
         } else if (this.boughtBySomeoneElse) {
            this.boughtBySomeoneElse = false;
            this.purchaseClickPending = false;
            this.onListingChanged("someone else bought it first");
         } else {
            if (this.isClickCooldownOver() && this.confirmClickCount < 3) {
               if (this.isContainerScreenOpen() && this.isConfirmWindowOpen()) {
                  String formatDifferentShowed = this.findConfirmMismatch();
                  if (formatDifferentShowed != null) {
                     this.onConfirmMismatch(formatDifferentShowed);
                     return;
                  }

                  if (this.tryClickAcceptButton()) {
                     this.confirmClickCount++;
                  }
               } else if (this.isConfirmDialogOpen() && this.tickCounter - this.lastActionTick >= this.getClickIntervalTicks() * 3) {
                  this.confirmKeyPhase = 0;
                  this.enterState(SpecialItemBuyer.State.CONFIRM);
                  return;
               }
            }

            if (this.getTicksInState() > 100) {
               this.giveUpUnconfirmedBuy();
            }
         }
      }
   }

   private void resetChatFlags() {
      this.alreadyBoughtWordLower = normalizeWord(this.alreadyBoughtWord.getCurrentText());
      this.noMoneyWordLower = normalizeWord(this.noMoneyWord.getCurrentText());
      this.boughtBySomeoneElse = false;
      this.notEnoughMoneyFlag = false;
      this.inventoryFullFlag = false;
      this.receivedItemType = null;
      this.receivedCoins = 0L;
   }

   private void advanceToNextItemType() {
      if (this.settlePendingPurchase()) {
         this.clearListingState();
         this.queuedItemIndex++;
         if (this.queuedItemIndex >= this.queuedItemTypes.size()) {
            this.finishRunWithSummary(null);
         } else {
            this.currentItemType = this.queuedItemTypes.get(this.queuedItemIndex);
            if (!this.isScanOnly() && this.isSpendLimitReached()) {
               this.finishRunWithSummary("Max Total Spend reached (" + formatCoins(this.item + this.unconfirmedSpend) + ")");
            } else {
               this.baselineInventoryCount = this.countInventoryItems(this.currentItemType);
               if (this.boardBuyListing != null) {
                  this.targetListing = this.boardBuyListing;
                  this.currentGoal = SpecialItemBuyer.Goal.BUY;
                  this.pagesScanned = 0;
                  this.hasRetriedSearch = true;
                  this.enterState(SpecialItemBuyer.State.SEARCH);
               } else {
                  this.currentGoal = SpecialItemBuyer.Goal.SCAN;
                  this.enterState(SpecialItemBuyer.State.SEARCH);
               }
            }
         }
      }
   }

   private void onPurchaseDetected(ElseMaxData elseMaxData) {
      if (this.recordSuccessfulBuy(elseMaxData, "")) {
         this.closeOpenScreen();
         this.advanceToNextItemType();
      }
   }

   private int getMaxPages() {
      return Math.max(1, this.maxAhPages.getValue());
   }

   @Override
   protected void onEnable() {
      SpecialItemBuyer.Run run = this.pendingRunKind == null ? SpecialItemBuyer.Run.TOGGLE : this.pendingRunKind;
      ElseMaxData elseMaxData = this.pendingRunListing;
      Runnable action = this.pendingRunCallback;
      this.pendingRunKind = null;
      this.pendingRunListing = null;
      this.pendingRunCallback = null;
      this.resetState();
      this.runKind = run;
      this.onFinishCallback = action;
      this.boardScreenCloseRequested = run != SpecialItemBuyer.Run.TOGGLE;
      this.boardBuyListing = run == SpecialItemBuyer.Run.BOARD_BUY ? elseMaxData : null;
      this.item = 0L;
      this.unconfirmedSpend = 0L;
      this.potentialExtraSpend = 0L;
      this.boughtItemCount = 0;
      this.unconfirmedBuyCount = 0;
      if (mc.player == null || mc.world == null) {
         this.showToast(this.getCategory(), "Join a world first", false);
         this.finishRun();
      } else if (this.isOtherAhBotActive()) {
         this.sendChatMessage("\u00a7c[Deals]\u00a7r Turn off Auto Builder, Auto Auction Flipper and the elytra bots first. They use /ah too.");
         this.showToast(this.getCategory(), "Another /ah bot is on", false);
         this.finishRun();
      } else if (run != SpecialItemBuyer.Run.BOARD_BUY || elseMaxData != null && elseMaxData.kind() != null) {
         if (run == SpecialItemBuyer.Run.BOARD_SCAN) {
            this.queuedItemTypes.addAll(List.of(ShardAxeType.values()));
         } else if (run == SpecialItemBuyer.Run.BOARD_BUY) {
            this.queuedItemTypes.add(elseMaxData.kind());
         } else {
            if (this.drill.isEnabled()) {
               this.queuedItemTypes.add(ShardAxeType.DRILL);
            }

            if (this.sellWand.isEnabled()) {
               this.queuedItemTypes.add(ShardAxeType.SELL_WAND);
            }

            if (this.shardAxe.isEnabled()) {
               this.queuedItemTypes.add(ShardAxeType.SHARD_AXE);
            }

            if (this.shardShovel.isEnabled()) {
               this.queuedItemTypes.add(ShardAxeType.SHARD_SHOVEL);
            }
         }

         if (this.queuedItemTypes.isEmpty()) {
            this.sendChatMessage("\u00a7c[Deals]\u00a7r Pick at least one item first: Drill, Sell Wand, Shard Axe or Shard Shovel.");
            this.showToast(this.getCategory(), "Pick at least one item", false);
            this.finishRun();
         } else {
            this.buyBestMode = run == SpecialItemBuyer.Run.BOARD_BUY
               || run == SpecialItemBuyer.Run.TOGGLE && "Buy Best".equals(this.modeSetting.getCurrentOption());
            this.networkHandler = mc.getNetworkHandler();
            this.resetChatFlags();
            if (run == SpecialItemBuyer.Run.TOGGLE) {
               this.announceRunStart();
            } else if (run == SpecialItemBuyer.Run.BOARD_BUY) {
               this.sendVerboseMessage(
                  "\u00a77[Deals]\u00a7r Buying that " + elseMaxData.kind().getCommandName() + " for " + formatCoins(elseMaxData.price()) + " (" + formatListingValue(elseMaxData) + ")."
               );
            }

            this.advanceToNextItemType();
         }
      } else {
         this.finishRun();
      }
   }

   private StringBase getSearchCommandSetting(ShardAxeType shardAxeType) {
      return switch (shardAxeType) {
         case DRILL -> this.drillSearch;
         case SELL_WAND -> this.sellWandSearch;
         case SHARD_AXE -> this.shardAxeSearch;
         case SHARD_SHOVEL -> this.shardShovelSearch;
      };
   }

   private String buildPageSignature() {
      if (!this.isContainerScreenOpen()) {
         return "";
      } else {
         ScreenHandler currentScreenHandler = mc.player.currentScreenHandler;
         StringBuilder stringBuilder = new StringBuilder();

         for (int i = 0; i < this.getListingSlotCount(); i++) {
            ItemStack stack = currentScreenHandler.getSlot(i).getStack();
            if (!stack.isEmpty()) {
               stringBuilder.append(i).append(':').append(this.getSlotSignature(stack)).append(';');
            }
         }

         return stringBuilder.toString();
      }
   }

   private static boolean isSoldReason(String someoneSoldText) {
      return someoneSoldText.startsWith("it was sold") || someoneSoldText.startsWith("someone else") || someoneSoldText.contains("different");
   }

   @Override
   protected void onReset() {
      if (this.buyState != SpecialItemBuyer.State.IDLE) {
         if (mc.player != null && mc.interactionManager != null && mc.player.networkHandler != null) {
            if (mc.getNetworkHandler() != this.networkHandler) {
               this.abortRun("You left the server, so I stopped. Rejoin, then " + this.getRestartHint() + ".");
            } else if (this.isOtherAhBotActive()) {
               this.abortRun("Another /ah bot was turned on, so I stopped.");
            } else {
               this.tickCounter++;
               if (this.applyPurchaseReceipts()) {
                  if (this.pendingPurchase != null && this.buyState != SpecialItemBuyer.State.CONFIRM && this.buyState != SpecialItemBuyer.State.VERIFY && this.hasReceivedCurrentItem()) {
                     this.onPurchaseDetected(this.pendingPurchase);
                  } else {
                     switch (this.buyState) {
                        case SEARCH:
                           this.tickSearchState();
                           break;
                        case BOARD:
                           this.tickBoardState();
                           break;
                        case NEXT_PAGE:
                           this.tickNextPageState();
                           break;
                        case CLICK:
                           this.tickClickState();
                           break;
                        case CONFIRM:
                           this.tickConfirmState();
                           break;
                        case VERIFY:
                           this.tickVerifyState();
                     }
                  }
               }
            }
         } else {
            this.finishRun();
         }
      }
   }

   private boolean isAuctionScreenOpen() {
      if (!this.isContainerScreenOpen()) {
         return false;
      } else {
         String lowerCase = normalizeWord(this.ahTitleWord.getCurrentText());
         return !lowerCase.isEmpty() && this.getScreenTitleLower().contains(lowerCase) ? true : this.containerHasCurrentItemType();
      }
   }

   private boolean isOtherModuleRunning(Class<? extends Module> class2) {
      Module iliIiiliiiii = ModuleRegistry.getModule(class2);
      return iliIiiliiiii != null && iliIiiliiiii != this && iliIiiliiiii.isEnabled();
   }

   private void giveUpUnconfirmedBuy() {
      if (this.pendingPurchase != null) {
         this.unconfirmedSpend = this.unconfirmedSpend + this.pendingPurchase.price();
         this.potentialExtraSpend = this.potentialExtraSpend + (this.pendingPurchase.maxCharge() - this.pendingPurchase.price());
         this.unconfirmedListings.put(this.currentItemType, this.pendingPurchase);
      }

      this.pendingPurchase = null;
      this.purchaseClickPending = false;
      this.unconfirmedBuyCount++;
      this.sendChatMessage(
         "\u00a7e[Deals]\u00a7r "
            + this.currentItemType.getCommandName()
            + ": I confirmed the buy but the item never showed up. Check your inventory. Skipping "
            + this.currentItemType.getCommandName()
            + " for this run."
      );
      this.updateScanForBoardListing();
      this.closeOpenScreen();
      this.advanceToNextItemType();
   }

   private void retryOrReportSold() {
      if (!this.hasRetriedSearch) {
         this.hasRetriedSearch = true;
         this.pagesScanned = 0;
         this.enterState(SpecialItemBuyer.State.SEARCH);
      } else {
         this.onListingChanged("it was sold or taken down");
      }
   }

   private int findSlotByItems(Item... items) {
      if (!this.isContainerScreenOpen()) {
         return -1;
      } else {
         ScreenHandler currentScreenHandler = mc.player.currentScreenHandler;
         int slotStackLimit = currentScreenHandler.slots.size() - 36;

         for (int i = 0; i < slotStackLimit; i++) {
            ItemStack stack = currentScreenHandler.getSlot(i).getStack();
            if (!stack.isEmpty() && !this.isListingOrToolItem(stack)) {
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

   private void startRun(SpecialItemBuyer.Run run, ElseMaxData elseMaxData, Runnable action) {
      this.lastResultMessage = "";
      this.lastStatusSecond = -1L;
      this.pendingRunKind = run;
      this.pendingRunListing = elseMaxData;
      this.pendingRunCallback = action;
      this.setEnabled(true);
      this.pendingRunKind = null;
      this.pendingRunListing = null;
      this.pendingRunCallback = null;
   }

   private boolean containerHasCurrentItemType() {
      if (this.currentItemType != null && this.isContainerScreenOpen()) {
         ScreenHandler currentScreenHandler = mc.player.currentScreenHandler;
         int shardAxeLimit = currentScreenHandler.slots.size() - 36;

         for (int i = 0; i < shardAxeLimit; i++) {
            ItemStack stack = currentScreenHandler.getSlot(i).getStack();
            if (!stack.isEmpty() && ShardAxeType.getEmpty(stack.getName().getString()).orElse(null) == this.currentItemType) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   public String getBoardStatusText() {
      if (this.isRunning()) {
         return this.getStatusText();
      } else {
         long currentMillis = System.currentTimeMillis();
         long moneyAffordTime = currentMillis / 1000L;
         if (moneyAffordTime == this.lastStatusSecond) {
            return this.scanSummaryText;
         } else {
            this.lastStatusSecond = moneyAffordTime;
            Rules integer = this.buildLimitRules(0L);
            long scannedMoneyBound = 0L;
            String scannedMoneyText = null;

            for (ShardAxeType shardAxeType : ShardAxeType.values()) {
               MaxDataOver leftSeconds = ElseMaxUtils.getLeftSeconds(this.scanDealMap.get(shardAxeType), integer, currentMillis);
               if (leftSeconds != null) {
                  ElseMaxData ranked = leftSeconds.getRanked();
                  if (ranked != null) {
                     scannedMoneyText = "Best "
                        + shardAxeType.getCommandName().toLowerCase(Locale.ROOT)
                        + " "
                        + FormatFakesUtils.formatPrice(ranked, leftSeconds.scannedAtMs(), currentMillis)
                        + " \u00b7 "
                        + FormatFakesUtils.formatTextForLong(leftSeconds.scannedAtMs(), currentMillis);
                     break;
                  }

                  scannedMoneyBound = Math.max(scannedMoneyBound, leftSeconds.scannedAtMs());
               }
            }

            this.scanSummaryText = scannedMoneyText != null ? scannedMoneyText : (scannedMoneyBound > 0L ? "No deals \u00b7 " + FormatFakesUtils.formatTextForLong(scannedMoneyBound, currentMillis) : "Not scanned yet");
            return this.scanSummaryText;
         }
      }
   }

   private void clickSlot(int clickPopupValue) {
      mc.interactionManager.clickSlot(mc.player.currentScreenHandler.syncId, clickPopupValue, 0, SlotActionType.PICKUP, mc.player);
      this.lastActionTick = this.tickCounter;
   }

   private int getListingSlotCount() {
      int specialBuyerLimit = mc.player.currentScreenHandler.slots.size() - 36;
      return specialBuyerLimit >= 18 ? specialBuyerLimit - 9 : Math.max(0, specialBuyerLimit);
   }

   private void clearChatFlags() {
      this.alreadyBoughtWordLower = "";
      this.noMoneyWordLower = "";
      this.boughtBySomeoneElse = false;
      this.notEnoughMoneyFlag = false;
      this.inventoryFullFlag = false;
      this.receivedItemType = null;
      this.receivedCoins = 0L;
      this.receiptQueue.clear();
   }

   private boolean isClickCooldownOver() {
      return this.tickCounter - this.lastActionTick >= this.getClickIntervalTicks();
   }

   private void onAuctionClosed() {
      if (this.currentGoal == SpecialItemBuyer.Goal.SCAN) {
         this.finishScan();
      } else {
         this.onListingChanged("the auction window closed");
      }
   }

   private int findSlotByName(String slotStackText) {
      if (!this.isContainerScreenOpen()) {
         return -1;
      } else {
         ScreenHandler currentScreenHandler = mc.player.currentScreenHandler;
         int slotStackLimit = currentScreenHandler.slots.size() - 36;

         for (int i = 0; i < slotStackLimit; i++) {
            ItemStack stack = currentScreenHandler.getSlot(i).getStack();
            if (!stack.isEmpty() && !this.isListingOrToolItem(stack) && this.nameMatchesAnyWord(stack, slotStackText)) {
               return i;
            }
         }

         return -1;
      }
   }

   private static String normalizeWord(String lowerCaseText) {
      return lowerCaseText == null ? "" : lowerCaseText.toLowerCase(Locale.ROOT).trim();
   }

   private int getTicksInState() {
      return this.tickCounter - this.stateEnteredTick;
   }

   private static String formatListingValue(ElseMaxData elseMaxData) {
      return FormatFakesUtils.formatSecondsPrice(elseMaxData);
   }

   private void closeScreen() {
      Screen currentScreen = mc.currentScreen;
      if (currentScreen != null) {
         try {
            currentScreen.close();
         } catch (Throwable e) {
            mc.setScreen(null);
         }
      }
   }

   private void sendChatMessage(String messageLiteralText) {
      if (mc.player != null) {
         mc.player.sendMessage(Text.literal(messageLiteralText), false);
      }
   }

   public Integer getStatusColor() {
      return this.isRunning() ? (this.buyState == SpecialItemBuyer.State.SEARCH && this.blockedByOpenMenu ? -1535700 : -8393375) : 0;
   }

   private boolean isSameAuctionScreen() {
      return this.isAuctionScreenOpen() && mc.player.currentScreenHandler.syncId == this.syncId;
   }

   private void reportBoardMessage(String boughtMoneyText, String substringText) {
      this.sendChatMessage("\u00a7c[Deals]\u00a7r " + boughtMoneyText);
      this.showToast(this.getCategory(), substringText, false);
      this.lastResultMessage = boughtMoneyText;
      this.lastResultTimeMs = System.currentTimeMillis();
   }

   private boolean applyPurchaseReceipts() {
      for (SpecialItemBuyer.Receipt concurrentlinkedqueue = this.receiptQueue.poll(); concurrentlinkedqueue != null; concurrentlinkedqueue = this.receiptQueue.poll()) {
         if (concurrentlinkedqueue.kind() == this.currentItemType && this.pendingPurchase != null && this.receivedItemType == null) {
            this.receivedItemType = concurrentlinkedqueue.kind();
            this.receivedCoins = concurrentlinkedqueue.coins();
         } else {
            ElseMaxData elseMaxData = this.boughtAwaitingReceipt.remove(concurrentlinkedqueue.kind());
            if (elseMaxData != null) {
               long maxPrice = Math.max(elseMaxData.price(), elseMaxData.maxCharge());
               if (concurrentlinkedqueue.coins() > maxPrice) {
                  this.item = this.item + (concurrentlinkedqueue.coins() - maxPrice);
               }

               if (AuctionChatMatchUtils.canChargePrice(concurrentlinkedqueue.coins(), elseMaxData)) {
                  this.abortRun(this.formatOverchargeMessage(concurrentlinkedqueue.coins(), elseMaxData));
                  return false;
               }
            } else {
               ElseMaxData elseMaxData2 = this.unconfirmedListings.remove(concurrentlinkedqueue.kind());
               if (elseMaxData2 != null) {
                  this.unconfirmedSpend = this.unconfirmedSpend - elseMaxData2.price();
                  this.unconfirmedBuyCount--;
                  this.item = this.item + elseMaxData2.price() + Math.max(0L, concurrentlinkedqueue.coins() - Math.max(elseMaxData2.price(), elseMaxData2.maxCharge()));
                  this.boughtItemCount++;
                  this.sendChatMessage(
                     "\u00a7a[Deals]\u00a7r The " + concurrentlinkedqueue.kind().getCommandName() + " buy went through after all (" + formatCoins(concurrentlinkedqueue.coins()) + ")."
                  );
                  if (AuctionChatMatchUtils.canChargePrice(concurrentlinkedqueue.coins(), elseMaxData2)) {
                     this.abortRun(this.formatOverchargeMessage(concurrentlinkedqueue.coins(), elseMaxData2));
                     return false;
                  }
               } else {
                  this.item = this.item + concurrentlinkedqueue.coins();
                  this.boughtItemCount++;
                  this.sendChatMessage(
                     "\u00a7e[Deals]\u00a7r A "
                        + concurrentlinkedqueue.kind().getCommandName()
                        + " buy for "
                        + formatCoins(concurrentlinkedqueue.coins())
                        + " came through that I didn't expect. It counts toward Max Total Spend."
                  );
               }
            }
         }
      }

      return true;
   }

   private static Tool getToolKind(ItemStack stack) {
      if (stack.isIn(ItemTags.PICKAXES)) {
         return Tool.PICKAXE;
      } else if (stack.isIn(ItemTags.AXES)) {
         return Tool.AXE;
      } else {
         return stack.isIn(ItemTags.SHOVELS) ? Tool.SHOVEL : Tool.OTHER;
      }
   }

   private static String formatPlural(int specialBuyerValue, String specialBuyerText) {
      return specialBuyerValue + " " + specialBuyerText + (specialBuyerValue == 1 ? "" : "s");
   }

   private static String formatCoins(long specialBuyerValue) {
      return BuyerAppendUtils.formatText(specialBuyerValue);
   }

   private int findNextPageSlot() {
      if (!this.isContainerScreenOpen()) {
         return -1;
      } else {
         ScreenHandler currentScreenHandler = mc.player.currentScreenHandler;
         int slotStackLimit = currentScreenHandler.slots.size() - 36;
         int slotStackValue = slotStackLimit >= 18 ? slotStackLimit - 9 : 0;

         for (int i = slotStackLimit - 1; i >= slotStackValue; i--) {
            ItemStack stack = currentScreenHandler.getSlot(i).getStack();
            if (!stack.isEmpty() && !this.isListingOrToolItem(stack) && this.nameMatchesAnyWord(stack, this.nextPageWord.getCurrentText())) {
               return i;
            }
         }

         return -1;
      }
   }

   private void onChatMessage(String moneySomeoneText) {
      if (moneySomeoneText != null) {
         if (AuctionChatMatchUtils.matchesItemMessagePrefix(moneySomeoneText, this.alreadyBoughtWordLower)) {
            this.boughtBySomeoneElse = true;
         }

         if (AuctionChatMatchUtils.matchesYouMessagePrefix(moneySomeoneText, this.noMoneyWordLower)) {
            this.notEnoughMoneyFlag = true;
         }

         if (AuctionChatMatchUtils.isInventoryFullMessage(moneySomeoneText)) {
            this.inventoryFullFlag = true;
         }

         KmbtSellerUtils.getEmptyForString(moneySomeoneText)
            .ifPresent(chatPurchase -> ShardAxeType.getEmpty(chatPurchase.item()).ifPresent(shardAxeType -> this.receiptQueue.add(new SpecialItemBuyer.Receipt(shardAxeType, chatPurchase.coins()))));
      }
   }

   private boolean isConfirmDialogOpen() {
      Screen currentScreen = mc.currentScreen;
      if (currentScreen != null && !(currentScreen instanceof HandledScreen) && !(currentScreen instanceof ChatScreen) && !(currentScreen instanceof ConfirmScreen)) {
         String formatTitleScreen = this.getScreenTitleLower();
         String lowerCase = normalizeWord(this.sure.getCurrentText());
         String normalizeWord2 = normalizeWord(this.confirm.getCurrentText());
         return !lowerCase.isEmpty() && formatTitleScreen.contains(lowerCase) ? true : currentScreen instanceof DialogScreen && !normalizeWord2.isEmpty() && formatTitleScreen.contains(normalizeWord2);
      } else {
         return false;
      }
   }

   private String getScreenTitleLower() {
      Screen currentScreen = mc.currentScreen;
      return currentScreen == null ? "" : normalizeWord(KmbtSellerUtils.formatText(currentScreen.getTitle().getString()));
   }

   private boolean settlePendingPurchase() {
      if (this.currentItemType != null && this.pendingPurchase != null) {
         ElseMaxData orElse = this.pendingPurchase;
         if (this.hasReceivedCurrentItem()) {
            return this.recordSuccessfulBuy(orElse, ", it went through after all");
         } else {
            if (this.purchaseClickPending) {
               this.unconfirmedSpend = this.unconfirmedSpend + orElse.price();
               this.potentialExtraSpend = this.potentialExtraSpend + (orElse.maxCharge() - orElse.price());
               this.unconfirmedBuyCount++;
               this.unconfirmedListings.put(this.currentItemType, orElse);
               this.sendChatMessage(
                  "\u00a7e[Deals]\u00a7r "
                     + this.currentItemType.getCommandName()
                     + ": the "
                     + formatCoins(orElse.price())
                     + " buy I clicked may have gone through. Check your inventory."
               );
            }

            this.pendingPurchase = null;
            this.purchaseClickPending = false;
            return true;
         }
      } else {
         return true;
      }
   }

   private boolean hasReceivedCurrentItem() {
      return this.receivedItemType == this.currentItemType || this.countInventoryItems(this.currentItemType) > this.baselineInventoryCount;
   }

   private boolean isConfirmWindowOpen() {
      if (this.isContainerScreenOpen() && mc.player.currentScreenHandler.syncId != this.syncId && this.countListingSlots() < 2) {
         String lowerCase = normalizeWord(this.confirm.getCurrentText());
         return !lowerCase.isEmpty() && this.getScreenTitleLower().contains(lowerCase) ? true : this.findSlotByItems(ACCEPT_BUTTON_ITEMS) >= 0 && this.findSlotByItems(CANCEL_BUTTON_ITEMS) >= 0;
      } else {
         return false;
      }
   }

   private void setBoardResultMessage(String boughtText) {
      if (this.runKind != SpecialItemBuyer.Run.TOGGLE) {
         this.lastResultMessage = boughtText == null ? "" : boughtText;
         this.lastResultTimeMs = System.currentTimeMillis();
      }
   }

   private String formatBestDeals(List<ElseMaxData> list) {
      if (list.isEmpty()) {
         return "\u00a77[Deals]\u00a7r " + this.currentItemType.getCommandName() + ": " + this.describeWhyNoListingFits() + ".";
      } else {
         ElseMaxData elseMaxData = (ElseMaxData)list.get(0);
         StringBuilder append = new StringBuilder("\u00a7b[Deals]\u00a7r ")
            .append(this.currentItemType.getCommandName())
            .append(": best \u00a7a")
            .append(formatListingValue(elseMaxData))
            .append("\u00a7r -> ")
            .append(formatCoins(elseMaxData.price()))
            .append(", ")
            .append(DaysHoursUtils.formatText(elseMaxData.secondsLeft()))
            .append(" left (page ")
            .append(elseMaxData.page())
            .append(')');

         for (int i = 1; i < Math.min(3, list.size()); i++) {
            ElseMaxData ranked3 = (ElseMaxData)list.get(i);
            append.append(i == 1 ? " \u00a77| then\u00a7r " : " \u00a77|\u00a7r ")
               .append(formatListingValue(ranked3))
               .append(" (")
               .append(formatCoins(ranked3.price()))
               .append(", ")
               .append(DaysHoursUtils.formatText(ranked3.secondsLeft()))
               .append(')');
         }

         return append.toString();
      }
   }

   private boolean isStartBlocked() {
      if (this.isRunning() || this.isEnabled()) {
         String append = this.getStatusText();
         this.sendChatMessage("\u00a7c[Deals]\u00a7r I'm busy" + (append.isEmpty() ? "" : " (" + append + ")") + ". Wait for it to finish.");
         this.showToast(this.getCategory(), "Busy, wait a moment", false);
         return true;
      } else if (mc.player == null || mc.world == null) {
         this.reportBoardMessage("Join a world first.", "Join a world first");
         return true;
      } else if (this.isOtherAhBotActive()) {
         this.reportBoardMessage("Turn off Auto Builder, Auto Auction Flipper and the elytra bots first. They use /ah too.", "Another /ah bot is on");
         return true;
      } else {
         return false;
      }
   }

   private int getCurrentPage() {
      return this.currentPage;
   }

   private String findConfirmMismatch() {
      if (this.pendingPurchase != null && this.currentItemType != null) {
         String differentShowedText = this.priceKeyword.getCurrentText();
         var arrayList = new ArrayList();
         if (this.isContainerScreenOpen()) {
            ScreenHandler currentScreenHandler = mc.player.currentScreenHandler;
            int differentShowedLimit = currentScreenHandler.slots.size() - 36;

            for (int i = 0; i < differentShowedLimit; i++) {
               ItemStack stack = currentScreenHandler.getSlot(i).getStack();
               if (!stack.isEmpty()) {
                  ShardAxeType orElse = ShardAxeType.getEmpty(stack.getName().getString()).orElse(null);
                  if (orElse != null && (orElse != this.currentItemType || getToolKind(stack) != this.currentItemType.getShovelPickaxeType())) {
                     return "the confirm window showed a different item";
                  }

                  arrayList.addAll(AuctionChatMatchUtils.extractPricesAfterLabel(String.join("\n", getLoreLines(stack)), differentShowedText));
               }
            }
         } else {
            String formatScreen = this.getScreenWidgetText();
            arrayList.addAll(AuctionChatMatchUtils.extractPricesAfterLabel(formatScreen, differentShowedText));
            var noneOf = AuctionChatMatchUtils.parseShardAxeTypes(formatScreen);
            if (!noneOf.isEmpty() && !noneOf.contains(this.currentItemType)) {
               return "the popup named a different item";
            }
         }

         return !arrayList.isEmpty() && !AuctionChatMatchUtils.anyPriceMatches(arrayList, this.pendingPurchase.price()) ? "the confirm showed a different price" : null;
      } else {
         return null;
      }
   }

   private int countInventoryItems(ShardAxeType shardAxeType) {
      if (mc.player != null && shardAxeType != null) {
         PlayerInventory inventory = mc.player.getInventory();
         String inventoryPlayerText = this.selfDestructWord.getCurrentText();
         int inventoryPlayer = 0;

         for (int i = 0; i < 36; i++) {
            ItemStack stack = inventory.getStack(i);
            if (!stack.isEmpty() && ToolListingParser.hasMatchingToolLore(buildLines(stack), shardAxeType, inventoryPlayerText)) {
               inventoryPlayer++;
            }
         }

         return inventoryPlayer;
      } else {
         return 0;
      }
   }

   private void tickClickState() {
      if (this.clickTargetListing == null || !this.isSameAuctionScreen()) {
         this.onListingChanged("the auction window closed");
      } else if (this.isClickCooldownOver()) {
         int windowClosedValue = this.countInventoryItems(this.currentItemType);
         if (this.pendingPurchase == null || windowClosedValue <= this.baselineInventoryCount && this.receivedItemType != this.currentItemType) {
            this.baselineInventoryCount = windowClosedValue;
            ItemStack stack = mc.player.currentScreenHandler.getSlot(this.clickTargetListing.slot()).getStack();
            ElseMaxData orElse = stack.isEmpty()
               ? null
               : ToolListingParser.parseToolListing(buildLines(stack), this.clickTargetListing.page(), this.clickTargetListing.slot(), this.priceKeyword.getCurrentText(), this.selfDestructWord.getCurrentText())
                  .orElse(null);
            if (orElse == null || !ToolListingParser.areListingsWithinSeconds(this.targetListing, orElse, 180L)) {
               this.onListingChanged("it was sold or moved");
            } else if (this.isSpendLimitReached()) {
               this.finishRunWithSummary("Max Total Spend reached (" + formatCoins(this.item + this.unconfirmedSpend) + ")");
            } else if (ComparingUtils.getComparingMax(List.of(orElse), this.getRemainingLimitRules()).isEmpty()) {
               if (this.runKind == SpecialItemBuyer.Run.BOARD_BUY) {
                  this.updateScanForBoardListing();
                  this.endRunWithMessages(
                     "That listing doesn't fit your limits anymore (Max Price, Min Time Left or Max Total Spend), so I didn't buy anything.",
                     "Didn't buy: it no longer fits your limits."
                  );
               } else {
                  this.sendChatMessage("\u00a7e[Deals]\u00a7r " + this.currentItemType.getCommandName() + ": that deal no longer fits your limits, skipped.");
                  this.closeOpenScreen();
                  this.advanceToNextItemType();
               }
            } else if (this.isScanOnly()) {
               this.finishRunWithSummary("Mode was switched to Show Deals, so I stopped before buying");
            } else if (!this.hasFreeInventorySlot()) {
               this.endRunWithMessages("Your inventory is full. Free one slot, then " + this.getRestartHint() + ".", "Didn't buy: your inventory is full.");
            } else {
               this.resetChatFlags();
               this.pendingPurchase = orElse;
               this.purchaseClickPending = true;
               this.confirmKeyPhase = 0;
               this.confirmDialogSeen = false;
               this.popupMissingSinceTick = -1;
               this.clickSlot(orElse.slot());
               this.enterState(SpecialItemBuyer.State.CONFIRM);
            }
         } else {
            this.onPurchaseDetected(this.pendingPurchase);
         }
      }
   }

   private void storeScanResult() {
      if (this.currentItemType != null) {
         this.scanDealMap
            .put(
               this.currentItemType,
               ElseMaxUtils.getKind(
                  this.currentItemType,
                  this.foundListings,
                  this.wrongToolTypeSlots.size(),
                  this.unparsedListingSlots.size(),
                  this.getRemainingLimitRules(),
                  this.pagesScanned,
                  this.pagesScanned > 0,
                  System.currentTimeMillis()
               )
            );
      }
   }

   private boolean abortOnPurchaseError() {
      if (this.notEnoughMoneyFlag) {
         this.purchaseClickPending = false;
         this.endRunWithMessages(
            "You don't have enough money for that "
               + this.currentItemType.getCommandName()
               + (this.pendingPurchase != null ? " (" + formatCoins(this.pendingPurchase.price()) + ")" : "")
               + ". Stopped.",
            "Didn't buy: you don't have enough money."
         );
         return true;
      } else if (this.inventoryFullFlag) {
         this.purchaseClickPending = false;
         this.endRunWithMessages("Your inventory is full. Free one slot, then " + this.getRestartHint() + ".", "Didn't buy: your inventory is full.");
         return true;
      } else {
         return false;
      }
   }

   private void onAuctionNotOpened() {
      if (this.currentGoal == SpecialItemBuyer.Goal.BUY) {
         this.onListingChanged("the auction house didn't open");
      } else if (this.pagesScanned > 0) {
         this.finishScan();
      } else {
         this.storeScanResult();
         this.sendChatMessage(
            "\u00a7e[Deals]\u00a7r "
               + this.currentItemType.getCommandName()
               + ": no listings found (the auction house didn't open). Check '"
               + this.getSearchCommandSetting(this.currentItemType).getSettingName()
               + "' and 'AH Title Word'."
         );
         this.advanceToNextItemType();
      }
   }

   public Rules getCurrentRules() {
      return this.buildLimitRules(0L);
   }

   private void sendVerboseMessage(String specialBuyerText) {
      if (this.notifications.isEnabled()) {
         this.sendChatMessage(specialBuyerText);
      }
   }

   private void announceRunStart() {
      StringBuilder stringBuilder = new StringBuilder();

      for (ShardAxeType shardAxeType : this.queuedItemTypes) {
         stringBuilder.append(stringBuilder.length() == 0 ? "" : ", ").append(shardAxeType.getCommandName());
      }

      String integerLeastText = "Max Price "
         + (this.maxPrice.getValue() > 0 ? formatCoins(this.maxPrice.getValue()) : "none")
         + ", at least "
         + this.minTimeLeft.getValue()
         + "m left"
         + (this.maxTotalSpend.getValue() > 0 ? ", spend up to " + formatCoins(this.maxTotalSpend.getValue()) : "");
      if (this.isScanOnly()) {
         this.sendChatMessage(
            "\u00a7b[Deals]\u00a7r Show Deals: checking " + stringBuilder + " on /ah (up to " + this.getMaxPages() + " pages each). Buys nothing. " + integerLeastText + "."
         );
      } else {
         this.sendChatMessage("\u00a7b[Deals]\u00a7r Buy Best: buying the best deal for " + stringBuilder + " (one of each). " + integerLeastText + ".");
      }
   }

   private void finishRunWithSummary(String boughtDealsText) {
      if (this.settlePendingPurchase()) {
         this.closeOpenScreen();
         if (this.runKind == SpecialItemBuyer.Run.BOARD_SCAN) {
            this.sendChatMessage(
               boughtDealsText == null ? "\u00a7b[Deals]\u00a7r Scanned " + formatPlural(this.queuedItemTypes.size(), "item") + "." : "\u00a7e[Deals]\u00a7r " + boughtDealsText + "."
            );
            if (boughtDealsText != null) {
               this.setBoardResultMessage(boughtDealsText + ".");
            }

            this.finishRun();
         } else {
            StringBuilder stringBuilder = new StringBuilder(boughtDealsText == null ? "\u00a7b[Deals]\u00a7r Done." : "\u00a7e[Deals]\u00a7r " + boughtDealsText + ".");
            if (this.boughtItemCount > 0) {
               stringBuilder.append(" Bought ").append(formatPlural(this.boughtItemCount, "item")).append(" for ").append(formatCoins(this.item)).append('.');
            } else if (!this.buyBestMode) {
               stringBuilder.append(" Checked ").append(formatPlural(this.queuedItemTypes.size(), "item")).append('.');
            } else {
               stringBuilder.append(" Bought nothing.");
            }

            if (this.unconfirmedBuyCount > 0) {
               stringBuilder.append(' ').append(this.unconfirmedBuyCount).append(" not confirmed, check your inventory.");
            }

            this.sendChatMessage(stringBuilder.toString());
            if (this.runKind == SpecialItemBuyer.Run.BOARD_BUY) {
               String boughtDealsText2 = this.currentItemType == null ? "it" : this.currentItemType.getCommandName();
               this.setBoardResultMessage(
                  this.boughtItemCount > 0
                     ? "Bought " + boughtDealsText2 + " for " + formatCoins(this.item) + "."
                     : (
                        this.unconfirmedBuyCount > 0
                           ? "Not confirmed. Check your inventory before you buy again."
                           : (boughtDealsText != null ? "Didn't buy: " + boughtDealsText + "." : "Bought nothing.")
                     )
               );
            }

            this.finishRun();
         }
      }
   }

   public void onBoardBuyPressed(ElseMaxData elseMaxData, Runnable runnable) {
      if (elseMaxData == null || elseMaxData.kind() == null) {
         this.reportBoardMessage("Pick a deal first.", "Nothing to buy");
      } else if (!this.isStartBlocked()) {
         MaxDataOver scandealmap = this.scanDealMap.get(elseMaxData.kind());
         long currentMillis = System.currentTimeMillis();
         if (scandealmap == null) {
            this.reportBoardMessage("Press Scan first.", "Scan first");
         } else if (ElseMaxUtils.canScanned(scandealmap, currentMillis)) {
            this.reportBoardMessage(SCAN_TOO_OLD_MESSAGE, "Scan again first");
         } else {
            ElseMaxData scannedAll = ElseMaxUtils.getScannedAll(scandealmap, elseMaxData, currentMillis);
            if (scannedAll.secondsLeft() <= 0L) {
               this.reportBoardMessage("That listing has self-destructed by now. Press Scan again.", "Listing ran out of time");
            } else {
               Rules integer = this.buildLimitRules(0L);
               if (ComparingUtils.getComparingMax(List.of(scannedAll), integer).isEmpty()) {
                  this.reportBoardMessage(
                     "That listing doesn't fit your limits anymore (Max Price, Min Time Left or Max Total Spend), so I didn't buy anything.",
                     "Doesn't fit your limits"
                  );
               } else if (!ElseMaxUtils.canFlag(scandealmap, elseMaxData, integer, currentMillis)) {
                  this.reportBoardMessage("The best deal changed, so I didn't buy anything. Check the board, then press Buy again.", "Best deal changed");
               } else if (!this.hasFreeInventorySlot()) {
                  this.reportBoardMessage("Your inventory is full. Free one slot, then press Buy again.", "Inventory full");
               } else {
                  this.startRun(SpecialItemBuyer.Run.BOARD_BUY, scannedAll, runnable);
               }
            }
         }
      }
   }

   private boolean isListingOrToolItem(ItemStack stack) {
      return KmbtSellerUtils.canPriceRoot(getLoreLines(stack), this.priceKeyword.getCurrentText()) || ShardAxeType.getEmpty(stack.getName().getString()).isPresent();
   }

   private void onNoMorePages() {
      if (this.currentGoal == SpecialItemBuyer.Goal.SCAN) {
         this.finishScan();
      } else {
         this.retryOrReportSold();
      }
   }

   private String getSlotSignature(ItemStack stack) {
      String lowerCase = normalizeWord(this.selfDestructWord.getCurrentText());
      String normalizeWord2 = normalizeWord(this.priceKeyword.getCurrentText());
      StringBuilder append = new StringBuilder()
         .append(Registries.ITEM.getId(stack.getItem()))
         .append('x')
         .append(stack.getCount())
         .append(':')
         .append(stack.getName().getString());

      for (String appendText : getLoreLines(stack)) {
         String emptyText = normalizeWord(KmbtSellerUtils.formatText(appendText));
         if ((lowerCase.isEmpty() || !emptyText.contains(lowerCase)) && (!normalizeWord2.isEmpty() && emptyText.contains(normalizeWord2) || !DaysHoursUtils.getEmpty(emptyText).isPresent())) {
            append.append('|').append(appendText);
         }
      }

      return append.toString();
   }

   public boolean isRunning() {
      return this.buyState != SpecialItemBuyer.State.IDLE;
   }

   private int countListingSlots() {
      ScreenHandler currentScreenHandler = mc.player.currentScreenHandler;
      int slotStackLimit = currentScreenHandler.slots.size() - 36;
      int slotStack = 0;

      for (int i = 0; i < slotStackLimit; i++) {
         ItemStack stack = currentScreenHandler.getSlot(i).getStack();
         if (!stack.isEmpty() && KmbtSellerUtils.canPriceRoot(getLoreLines(stack), this.priceKeyword.getCurrentText())) {
            slotStack++;
         }
      }

      return slotStack;
   }

   private void tickSearchState() {
      this.blockedByOpenMenu = false;
      if (this.boardScreenCloseRequested || this.runKind != SpecialItemBuyer.Run.TOGGLE && mc.currentScreen instanceof DealBoard) {
         this.boardScreenCloseRequested = false;
         Screen currentScreen = mc.currentScreen;
         if (currentScreen != null && !(currentScreen instanceof HandledScreen) && !(currentScreen instanceof DialogScreen)) {
            mc.setScreen(null);
            this.lastActionTick = this.tickCounter;
            return;
         }
      }

      if (this.isContainerScreenOpen()) {
         if (this.isClickCooldownOver()) {
            mc.player.closeHandledScreen();
            this.lastActionTick = this.tickCounter;
         }
      } else if (!(mc.currentScreen instanceof DialogScreen) && !this.isConfirmDialogOpen()) {
         if (mc.currentScreen != null && !(mc.currentScreen instanceof GameMenuScreen)) {
            this.blockedByOpenMenu = true;
            if (this.getTicksInState() > 1200) {
               this.abortRun("A menu stayed open, so I couldn't open /ah. Close it, then " + this.getRestartHint() + ".");
            }
         } else if (this.isClickCooldownOver() && System.nanoTime() - this.lastAhCommandNanos >= 1500000000L) {
            this.lastAhCommandNanos = System.nanoTime();
            mc.player.networkHandler.sendChatCommand(this.getSearchCommand(this.currentItemType));
            this.lastActionTick = this.tickCounter;
            this.syncId = -1;
            this.currentPage = 1;
            this.pagesScanned = 0;
            this.lastPageSignature = null;
            this.enterBoardState();
         }
      } else {
         if (this.isClickCooldownOver()) {
            this.closeScreen();
            this.lastActionTick = this.tickCounter;
         }
      }
   }

   private void enterState(SpecialItemBuyer.State state) {
      this.buyState = state;
      this.stateEnteredTick = this.tickCounter;
      this.blockedByOpenMenu = false;
   }

   private void tickConfirmState() {
      if (!this.abortOnPurchaseError()) {
         if (this.hasReceivedCurrentItem()) {
            this.onPurchaseDetected(this.pendingPurchase);
         } else if (this.boughtBySomeoneElse) {
            this.boughtBySomeoneElse = false;
            this.purchaseClickPending = false;
            this.onListingChanged("someone else bought it first");
         } else if (this.confirmClickCount == 0 && this.isScanOnly()) {
            this.finishRunWithSummary("Mode was switched to Show Deals, so I stopped before buying");
         } else if (this.isConfirmDialogOpen()) {
            this.confirmDialogSeen = true;
            this.popupMissingSinceTick = -1;
            if (this.confirmClickCount >= 3) {
               if (this.getTicksInState() > 80) {
                  this.giveUpUnconfirmedBuy();
               }
            } else if (this.isClickCooldownOver()) {
               if (this.confirmKeyPhase == 0) {
                  String formatDifferentShowed = this.findConfirmMismatch();
                  if (formatDifferentShowed != null) {
                     this.onConfirmMismatch(formatDifferentShowed);
                  } else {
                     this.pressKey(263);
                     this.confirmKeyPhase = 1;
                  }
               } else {
                  this.pressKey(257);
                  this.confirmKeyPhase = 0;
                  this.confirmClickCount++;
                  this.enterState(SpecialItemBuyer.State.VERIFY);
               }
            }
         } else if (this.confirmDialogSeen && this.confirmClickCount == 0) {
            if (this.popupMissingSinceTick < 0) {
               this.popupMissingSinceTick = this.tickCounter;
            }

            if (this.tickCounter - this.popupMissingSinceTick >= 12) {
               this.purchaseClickPending = false;
               this.endRunWithMessages(
                  "The buy popup closed before I pressed Yes, so I stopped without buying it.", "Didn't buy: the buy popup closed before I pressed Yes."
               );
            }
         } else if (!this.isContainerScreenOpen() || !this.isConfirmWindowOpen() && (this.isAuctionScreenOpen() || mc.player.currentScreenHandler.syncId == this.syncId)) {
            if (this.getTicksInState() > 80) {
               if (this.confirmClickCount > 0) {
                  this.giveUpUnconfirmedBuy();
               } else {
                  this.onListingChanged(
                     mc.currentScreen instanceof DialogScreen
                        ? "a popup opened that I didn't recognise, check 'Dialog Title Word'"
                        : "no confirm window showed up"
                  );
               }
            }
         } else if (this.confirmClickCount >= 3) {
            if (this.getTicksInState() > 80) {
               this.giveUpUnconfirmedBuy();
            }
         } else if (this.isClickCooldownOver()) {
            String confirmMismatch = this.findConfirmMismatch();
            if (confirmMismatch != null) {
               this.onConfirmMismatch(confirmMismatch);
            } else if (this.tryClickAcceptButton()) {
               this.confirmClickCount++;
               this.enterState(SpecialItemBuyer.State.VERIFY);
            } else if (this.getTicksInState() > 80) {
               if (this.confirmClickCount > 0) {
                  this.giveUpUnconfirmedBuy();
               } else {
                  this.onListingChanged("couldn't find the Confirm button");
               }
            }
         }
      }
   }

   private void clearListingState() {
      this.foundListings.clear();
      this.wrongToolTypeSlots.clear();
      this.unparsedListingSlots.clear();
      this.retryCount = 0;
      this.baselineInventoryCount = 0;
      this.syncId = -1;
      this.currentPage = 1;
      this.pagesScanned = 0;
      this.lastPageSignature = null;
      this.slotSignatureSnapshot = null;
      this.hasRetriedSearch = false;
      this.targetListing = null;
      this.clickTargetListing = null;
      this.pendingPurchase = null;
      this.purchaseClickPending = false;
      this.confirmKeyPhase = 0;
      this.confirmClickCount = 0;
      this.confirmDialogSeen = false;
      this.popupMissingSinceTick = -1;
   }

   private void readScanPage() {
      int buyingMenuValue = this.getCurrentPage();

      for (ElseMaxData elseMaxData : this.readPageListings(buyingMenuValue, this.wrongToolTypeSlots, this.unparsedListingSlots)) {
         this.addListingIfNew(elseMaxData);
      }

      this.pagesScanned++;
      if (this.pagesScanned < this.getMaxPages() && this.hasNextPageButton()) {
         this.enterState(SpecialItemBuyer.State.NEXT_PAGE);
      } else {
         this.finishScan();
      }
   }

   private static void appendWidgetMessages(ParentElement parentElement, StringBuilder stringBuilder, int messageChildrenValue) {
      if (messageChildrenValue <= 8) {
         for (Element element : parentElement.children()) {
            if (element instanceof ClickableWidget clickableWidget && clickableWidget.getMessage() != null) {
               stringBuilder.append(clickableWidget.getMessage().getString()).append('\n');
            }

            if (element instanceof ParentElement parentElement2) {
               appendWidgetMessages(parentElement2, stringBuilder, messageChildrenValue + 1);
            }
         }
      }
   }

   private void enterBoardState() {
      this.enterState(SpecialItemBuyer.State.BOARD);
      this.pageWaitStartTick = -1;
   }

   public void onBoardScanPressed(Runnable runnable) {
      if (!this.isStartBlocked()) {
         this.startRun(SpecialItemBuyer.Run.BOARD_SCAN, null, runnable);
      }
   }

   private void finishScan() {
      this.storeScanResult();
      if (this.runKind == SpecialItemBuyer.Run.BOARD_SCAN) {
         this.advanceToNextItemType();
      } else {
         var comparingMax = ComparingUtils.getComparingMax(this.foundListings, this.getRemainingLimitRules());
         if (this.isScanOnly()) {
            this.sendChatMessage(this.formatBestDeals(comparingMax));
            this.advanceToNextItemType();
         } else if (comparingMax.isEmpty()) {
            this.sendChatMessage("\u00a7e[Deals]\u00a7r " + this.currentItemType.getCommandName() + ": nothing to buy, " + this.describeWhyNoListingFits() + ".");
            this.advanceToNextItemType();
         } else {
            this.targetListing = (ElseMaxData)comparingMax.get(0);
            this.sendVerboseMessage(
               "\u00a77[Deals]\u00a7r "
                  + this.currentItemType.getCommandName()
                  + ": going for "
                  + formatCoins(this.targetListing.price())
                  + " ("
                  + formatListingValue(this.targetListing)
                  + ", "
                  + DaysHoursUtils.formatText(this.targetListing.secondsLeft())
                  + " left, page "
                  + this.targetListing.page()
                  + ")."
            );
            this.currentGoal = SpecialItemBuyer.Goal.BUY;
            this.pagesScanned = 0;
            if (this.isSameAuctionScreen() && this.getCurrentPage() == this.targetListing.page()) {
               this.hasRetriedSearch = false;
               this.lastPageSignature = null;
               this.enterState(SpecialItemBuyer.State.BOARD);
               this.pageWaitStartTick = this.tickCounter - 6;
            } else {
               this.hasRetriedSearch = true;
               this.enterState(SpecialItemBuyer.State.SEARCH);
            }
         }
      }
   }

   private void addListingIfNew(ElseMaxData elseMaxData) {
      for (ElseMaxData elseMaxData2 : this.foundListings) {
         if (ToolListingParser.areListingsWithinSeconds(elseMaxData2, elseMaxData, 60L)) {
            return;
         }
      }

      this.foundListings.add(elseMaxData);
   }

   private String getSearchCommand(ShardAxeType shardAxeType) {
      String substringText = this.getSearchCommandSetting(shardAxeType).getCurrentText();
      String trim2 = substringText == null ? "" : substringText.trim();
      if (trim2.startsWith("/")) {
         trim2 = trim2.substring(1).trim();
      }

      return trim2.isEmpty() ? shardAxeType.getDisplayLabel() : trim2;
   }

   private void findTargetOnPage() {
      this.pagesScanned++;

      for (ElseMaxData next : this.readPageListings(this.getCurrentPage(), null, null)) {
         if (ToolListingParser.areListingsWithinSeconds(this.targetListing, next, 180L)) {
            this.clickTargetListing = next;
            this.enterState(SpecialItemBuyer.State.CLICK);
            return;
         }
      }

      if (this.hasRetriedSearch
         && this.getCurrentPage() <= this.targetListing.page()
         && this.pagesScanned <= Math.max(this.getMaxPages(), this.targetListing.page())
         && this.hasNextPageButton()) {
         this.enterState(SpecialItemBuyer.State.NEXT_PAGE);
      } else {
         this.retryOrReportSold();
      }
   }

   @Override
   protected void onDisable() {
      boolean specialBuyerFlag = this.buyState != SpecialItemBuyer.State.IDLE;
      Runnable runnable = this.runCallbackOnDisable ? this.onFinishCallback : null;
      this.clearChatFlags();
      if (specialBuyerFlag) {
         this.closeOpenScreen();
      }

      this.resetState();
      if (runnable != null && mc.player != null && mc.currentScreen == null) {
         runnable.run();
      }
   }

   public SpecialItemBuyer() {
      super("SpecialItemBuyer", IconsPngType.DONUTSMP);
      this.shardAxe = new BooleanSetting(
         "Shard Axe", "Look for shard axes when you turn this on with its key or switch. The Deal Board always checks all four.", true
      );
      this.shardShovel = new BooleanSetting(
         "Shard Shovel", "Look for shard shovels when you turn this on with its key or switch. The Deal Board always checks all four.", true
      );
      this.modeSetting = new EnumSetting(
         "Mode",
         "Only for turning this on with its key or switch. Show Deals = only tells you the best deals and buys nothing. Buy Best = buys the best deal of each item you picked, then turns off. The Deal Board ignores this: its Scan never buys, and its Buy button buys only the deal you confirm.",
         "Show Deals",
         "Show Deals",
         "Buy Best"
      );
      this.maxPrice = new IntegerSetting(
         "Max Price",
         "Skip anything that could cost more than this many coins (0 = no limit). /ah rounds prices, so a $ 30M listing needs 30.1M here.",
         0,
         0,
         2000000000,
         100000
      );
      this.minTimeLeft = new IntegerSetting("Min Time Left", "Skip items that self-destruct sooner than this (minutes).", 60, 0, 1440);
      this.notifications = new BooleanSetting("Notifications", "Extra chat lines while it works. Deals, buys and problems always show.", true);
      this.maxAhPages = new IntegerSetting("Max AH Pages", "How many /ah pages to read for each item.", 5, 1, 20);
      this.clickSpacing = new IntegerSetting("Click Spacing", "Ticks between clicks. Higher is slower but gentler on the server.", 3, 2, 20);
      this.maxTotalSpend = new IntegerSetting(
         "Max Total Spend", "Stop buying once this many coins are spent in one run (0 = no limit).", 0, 0, 2000000000, 100000
      );
      this.drillSearch = new StringBase("Drill Search", "Command (no slash) that searches /ah for drills.", "ah drill");
      this.sellWandSearch = new StringBase("Sell Wand Search", "Command (no slash) that searches /ah for sell wands.", "ah sellwand");
      this.shardAxeSearch = new StringBase("Shard Axe Search", "Command (no slash) that searches /ah for shard axes.", "ah shard axe");
      this.shardShovelSearch = new StringBase("Shard Shovel Search", "Command (no slash) that searches /ah for shard shovels.", "ah shard shovel");
      this.ahTitleWord = new StringBase("AH Title Word", "A word in the auction window's title.", "Auction");
      this.priceKeyword = new StringBase("Price Keyword", "The symbol in front of the price on a listing.", "$");
      this.selfDestructWord = new StringBase("Self Destruct Word", "The words in front of the time left on a shard tool.", "Self Destruct");
      this.nextPageWord = new StringBase("Next Page Word", "Name of the next-page button.", "Next page");
      this.confirmButtonWord = new StringBase("Confirm Yes Word", "Name of the confirm button in a buy window.", "Confirm");
      this.confirm = new StringBase("Confirm Title Word", "A word in a buy window's title.", "confirm");
      this.sure = new StringBase("Dialog Title Word", "A word in the title of the server's \"Are you sure?\" popup.", "sure");
      this.alreadyBoughtWord = new StringBase("Already-Bought Word", "Chat text when someone else bought it first.", "already bought");
      this.noMoneyWord = new StringBase("No Money Word", "Chat text when you can't afford it.", "not have enough money");
      this.runKind = SpecialItemBuyer.Run.TOGGLE;
      this.wrongToolTypeSlots = new HashSet<>();
      this.unparsedListingSlots = new HashSet<>();
      this.lastResultMessage = "";
      this.scanSummaryText = "";
      this.lastStatusSecond = -1L;
      this.scanDealMap = new EnumMap<>(ShardAxeType.class);
      this.buyState = SpecialItemBuyer.State.IDLE;
      this.currentGoal = SpecialItemBuyer.Goal.SCAN;
      this.queuedItemTypes = new ArrayList<>();
      this.foundListings = new ArrayList<>();
      this.syncId = -1;
      this.popupMissingSinceTick = -1;
      this.alreadyBoughtWordLower = "";
      this.noMoneyWordLower = "";
      this.receiptQueue = new ConcurrentLinkedQueue<>();
      this.unconfirmedListings = new EnumMap<>(ShardAxeType.class);
      this.boughtAwaitingReceipt = new EnumMap<>(ShardAxeType.class);

      for (ConfigSetting autoEspBase : new ConfigSetting[]{this.drill, this.sellWand, this.shardAxe, this.shardShovel, this.modeSetting}) {
         autoEspBase.withGroupName("On/Off Switch Only");
      }

      for (ConfigSetting autoEspBase2 : new ConfigSetting[]{this.maxAhPages, this.clickSpacing, this.maxTotalSpend}) {
         autoEspBase2.withGroupName("Tuning");
      }

      for (ConfigSetting autoEspBase3 : new ConfigSetting[]{
         this.drillSearch,
         this.sellWandSearch,
         this.shardAxeSearch,
         this.shardShovelSearch,
         this.ahTitleWord,
         this.priceKeyword,
         this.selfDestructWord,
         this.nextPageWord,
         this.confirmButtonWord,
         this.confirm,
         this.sure,
         this.alreadyBoughtWord,
         this.noMoneyWord
      }) {
         autoEspBase3.withGroupName("Server Wording");
      }

      this.addSettings(
         new ConfigSetting[]{
            this.drill,
            this.sellWand,
            this.shardAxe,
            this.shardShovel,
            this.modeSetting,
            this.maxPrice,
            this.minTimeLeft,
            this.notifications,
            this.maxAhPages,
            this.clickSpacing,
            this.maxTotalSpend,
            this.drillSearch,
            this.sellWandSearch,
            this.shardAxeSearch,
            this.shardShovelSearch,
            this.ahTitleWord,
            this.priceKeyword,
            this.selfDestructWord,
            this.nextPageWord,
            this.confirmButtonWord,
            this.confirm,
            this.sure,
            this.alreadyBoughtWord,
            this.noMoneyWord
         }
      );
   }

   private boolean hasNextPageButton() {
      return this.findNextPageSlot() >= 0;
   }

   private int getClickIntervalTicks() {
      return Math.max(2, this.clickSpacing.getValue());
   }

   private Rules getRemainingLimitRules() {
      return this.buildLimitRules(this.item + this.unconfirmedSpend + this.potentialExtraSpend);
   }

   public String getRecentResultMessage(long boughtMoneyValue) {
      return !this.isRunning() && !this.lastResultMessage.isEmpty() && boughtMoneyValue - this.lastResultTimeMs <= 60000L ? this.lastResultMessage : "";
   }

   private void tickNextPageState() {
      if (!this.isSameAuctionScreen()) {
         this.onAuctionClosed();
      } else if (this.isClickCooldownOver()) {
         int slotStack = this.findNextPageSlot();
         if (slotStack < 0) {
            this.onNoMorePages();
         } else {
            this.lastPageSignature = this.buildPageSignature();
            this.slotSignatureSnapshot = this.getSlotSignatures();
            this.clickSlot(slotStack);
            this.currentPage++;
            this.enterBoardState();
         }
      }
   }

   private List<ElseMaxData> readPageListings(int shardAxeValue, Set<String> set, Set<String> set2) {
      var arrayList = new ArrayList();
      if (!this.isContainerScreenOpen()) {
         return arrayList;
      } else {
         ScreenHandler currentScreenHandler = mc.player.currentScreenHandler;
         int shardAxeLimit = currentScreenHandler.slots.size() - 36;
         int listingSlotCount = this.getListingSlotCount();
         String shardAxeText = this.priceKeyword.getCurrentText();
         String currentText = this.selfDestructWord.getCurrentText();

         for (int i = 0; i < shardAxeLimit; i++) {
            ItemStack stack = currentScreenHandler.getSlot(i).getStack();
            if (!stack.isEmpty()) {
               Lines lines = buildLines(stack);
               ElseMaxData orElse = ToolListingParser.parseToolListing(lines, shardAxeValue, i, shardAxeText, currentText).orElse(null);
               if (orElse != null) {
                  if (orElse.kind() == this.currentItemType) {
                     arrayList.add(orElse);
                  }
               } else if (i < listingSlotCount && ShardAxeType.getEmpty(lines.name()).orElse(null) == this.currentItemType) {
                  var set3 = ToolListingParser.isToolWrongForType(lines, this.currentItemType) ? set : set2;
                  if (set3 != null) {
                     set3.add(shardAxeValue + ":" + i);
                  }
               }
            }
         }

         return arrayList;
      }
   }

   private static enum Goal {
      SCAN,
      BUY;
   }

   private record Receipt(ShardAxeType kind, long coins) {
   }

   private static enum Run {
      TOGGLE,
      BOARD_SCAN,
      BOARD_BUY;
   }

   private static enum State {
      IDLE,
      SEARCH,
      BOARD,
      NEXT_PAGE,
      CLICK,
      CONFIRM,
      VERIFY;
   }
}
