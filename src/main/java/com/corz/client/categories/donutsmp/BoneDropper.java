package com.corz.client.categories.donutsmp;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.utils.SpawnerStorageLayout.Cell;
import com.corz.client.utils.SpawnerStorageLayout.Kind;
import com.corz.client.inventory.InventorySelectedUtils;
import java.util.ArrayList;
import java.util.Locale;
import net.minecraft.block.Blocks;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ArmorStandItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BoatItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.DecorationItem;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.item.FireChargeItem;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MinecartItem;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import com.corz.client.utils.SpawnerStorageLayout;
import com.corz.client.utils.InventoryPlayer;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class BoneDropper extends Module {
   private int currentPageBoneCount;
   private BlockPos spawnerPos;
   private final EnumSetting modeSetting = new EnumSetting(
      "Mode",
      "Spawner = drop every page of bones from the spawner you're looking at (or have open), then turn off. Orders = run /order and auto-fulfil bone orders.",
      "Spawner",
      "Spawner",
      "Orders"
   );
   private int reopenCount;
   private int lastLoreBoneCount;
   private BoneDropper.St order;
   private boolean closingScreenSelf;
   private long nextActionTimeMs;
   private long stateEnteredMillis;
   private int lastBoneInventoryCount;
   private int menuOpenRetryCount;
   private int unresponsiveRetryCount;
   private ScreenHandler screenHandler;
   private long lastClickedSlotSignature;
   private boolean warnedLookAtSpawner;
   private ClientWorld lastWorld;
   private int baselineDroppedBoneCount;
   private String activeModeName;
   private boolean laterPagesIdentical;
   private int peakEntityCount;
   private boolean strictPageCheck;
   private boolean staleDropDetected;
   private ScreenHandler lastClickedScreenHandler;
   private int pageAdvanceCount;
   private boolean pageLimitReached;
   private boolean menuClosedByUser;
   private int dropsSinceReopen;
   private int totalBonesDropped;
   private int lastClickedSlot;
   private int syncId;
   private final BooleanSetting notificationsSetting;
   private boolean warnedSneaking;
   private boolean warnedNotSpawnerMenu;
   private int droppedPageCount;
   private final IntegerSetting delaySetting = new IntegerSetting(
      "Delay", "Milliseconds between menu actions \u2014 higher is slower but gentler on the server.", 300, 100, 2000
   );
   private int unchangedPollCount;

   @Override
   public boolean requiresContainerBindPolling() {
      return this.isSpawnerMode() || this.isEnabled();
   }

   private void sendCommand(String networkText) {
      if (mc.player != null && mc.player.networkHandler != null) {
         String networkText2 = networkText.startsWith("/") ? networkText.substring(1) : networkText;
         mc.player.networkHandler.sendChatCommand(networkText2);
      }
   }

   private String buildStopReason(String appendText) {
      return this.droppedPageCount == 0
         ? appendText + "."
         : "dropped " + this.totalBonesDropped + " bones from " + this.droppedPageCount + (this.droppedPageCount == 1 ? " page" : " pages") + ", then " + appendText + ".";
   }

   private boolean isSpawnerMode() {
      return "Spawner".equalsIgnoreCase(this.modeSetting.getCurrentOption());
   }

   private int countNearbyDroppedBones() {
      if (mc.world != null && mc.player != null) {
         int expandEntities = 0;

         for (ItemEntity itemEntity : mc.world
            .getEntitiesByClass(ItemEntity.class, mc.player.getBoundingBox().expand(12.0), itemEntity2 -> itemEntity2.getStack().isOf(Items.BONE))) {
            expandEntities += itemEntity.getStack().getCount();
         }

         return expandEntities;
      } else {
         return 0;
      }
   }

   private boolean matchesCategory(ItemStack stack, BoneDropper.Cat cat) {
      if (stack != null && !stack.isEmpty()) {
         Item item = stack.getItem();
         String lowerCase = stack.getName().getString().toLowerCase(Locale.ROOT);

         return switch (cat) {
            case BONE -> item == Items.BONE || lowerCase.contains("bone");
            case CHEST -> item == Items.CHEST || item == Items.TRAPPED_CHEST || item == Items.ENDER_CHEST || lowerCase.contains("chest") || lowerCase.contains("truhe");
            case DROPPER -> item == Items.DROPPER || lowerCase.contains("dropper");
            case ARROW -> item == Items.ARROW || lowerCase.contains("arrow") || lowerCase.contains("pfeil");
         };
      } else {
         return false;
      }
   }

   private int countInventoryBones() {
      if (mc.player == null) {
         return 0;
      } else {
         PlayerInventory inventory = mc.player.getInventory();
         int boneInventory = 0;

         for (int i = 0; i < inventory.size(); i++) {
            ItemStack stack = inventory.getStack(i);
            if (stack.isOf(Items.BONE)) {
               boneInventory += stack.getCount();
            }
         }

         return boneInventory;
      }
   }

   private void sendChatMessage(String messageLiteralText) {
      if (mc.player != null) {
         mc.player.sendMessage(Text.literal(messageLiteralText), false);
      }
   }

   @Override
   public void onDisable() {
      this.resetRunState();
      this.lastWorld = null;
   }

   private void processScanState(ScreenHandler screenHandler) {
      if (screenHandler == null) {
         this.retryOrReopen();
      } else if (screenHandler == this.screenHandler || this.adoptOrRejectOpenedMenu(screenHandler)) {
         SpawnerStorageLayout codeBone = this.readSpawnerLayout(screenHandler);
         if (codeBone.dropperSlotId < 0) {
            if (this.getStateElapsedMs() > this.getTimeoutMs()) {
               this.finishRun(this.buildStopReason("the Drop Loot button disappeared"), true);
            } else {
               this.scheduleNextAction();
            }
         } else {
            int disappearedLootValue = this.readLoreBoneCount(screenHandler);
            if (codeBone.boneCount > 0) {
               this.currentPageBoneCount = codeBone.boneCount;
               this.lastLoreBoneCount = disappearedLootValue;
               this.baselineDroppedBoneCount = this.peakEntityCount = this.countNearbyDroppedBones();
               this.lastBoneInventoryCount = this.countInventoryBones();
               this.clickSlotAndTrack(screenHandler, codeBone, codeBone.dropperSlotId, BoneDropper.St.SP_DROPPED);
            } else {
               boolean disappearedLootFlag = codeBone.nextSlotId >= 0 && this.pageAdvanceCount < 200 && !this.staleDropDetected && (disappearedLootValue > 0 ? this.unchangedPollCount < 50 : disappearedLootValue < 0 && this.unchangedPollCount < 3);
               if (disappearedLootFlag) {
                  this.pageAdvanceCount++;
                  this.clickSlotAndTrack(screenHandler, codeBone, codeBone.nextSlotId, BoneDropper.St.SP_PAGED);
               } else {
                  this.pageLimitReached = codeBone.nextSlotId >= 0 && this.pageAdvanceCount >= 200;
                  this.laterPagesIdentical = codeBone.nextSlotId >= 0 && this.unchangedPollCount > 0 && !this.pageLimitReached;
                  this.finishOrContinuePages(disappearedLootValue);
               }
            }
         }
      }
   }

   private void scheduleNextAction() {
      this.nextActionTimeMs = System.currentTimeMillis() + this.delaySetting.getValue();
   }

   private void reopenSpawner() {
      this.setTrackedScreenHandler(null);
      if (++this.reopenCount >= 300) {
         this.finishRun(this.buildStopReason("stopped after 300 re-opens"), true);
      } else {
         this.warnedLookAtSpawner = false;
         this.setState(BoneDropper.St.SP_OPEN);
         this.scheduleNextAction();
      }
   }

   private ScreenHandler getOpenScreenHandler() {
      return this.hasContainerScreenOpen() ? mc.player.currentScreenHandler : null;
   }

   private boolean ensureSafeHeldItem() {
      if (!isUnsafeToRightClickWith(mc.player.getMainHandStack())) {
         return true;
      } else {
         PlayerInventory inventory = mc.player.getInventory();

         for (int i = 0; i < 9; i++) {
            if (inventory.getStack(i).isEmpty()) {
               InventorySelectedUtils.handleSelectedSlot(i);
               return true;
            }
         }

         for (int j = 0; j < 9; j++) {
            if (!isUnsafeToRightClickWith(inventory.getStack(j))) {
               InventorySelectedUtils.handleSelectedSlot(j);
               return true;
            }
         }

         return false;
      }
   }

   private void finishOrContinuePages(int pagesBonesLimit) {
      if (this.dropsSinceReopen > 0 || !this.strictPageCheck) {
         this.closeAndReopenSpawner();
      } else if (pagesBonesLimit > 0) {
         this.finishRun(this.buildStopReason("the menu still lists " + pagesBonesLimit + " bones I couldn't reach"), true);
      } else if (this.pageLimitReached && pagesBonesLimit < 0) {
         this.finishRun(this.buildStopReason("stopped after 200 pages \u2014 more pages exist"), true);
      } else if (this.laterPagesIdentical && pagesBonesLimit < 0) {
         this.finishRun(this.buildStopReason("the later pages look identical, so I couldn't check them"), true);
      } else {
         this.finishRun(
            this.droppedPageCount > 0
               ? "done \u2014 dropped " + this.totalBonesDropped + " bones from " + this.droppedPageCount + (this.droppedPageCount == 1 ? " page." : " pages.")
               : "no bones in this spawner.",
            false
         );
      }
   }

   private long getTimeoutMs() {
      return Math.max(1500L, 4L * this.delaySetting.getValue());
   }

   private void clickSlotAndTrack(ScreenHandler screenHandler, SpawnerStorageLayout spawnerStorageLayout, int emptiedBeenValue, BoneDropper.St st) {
      this.lastClickedScreenHandler = screenHandler;
      this.syncId = screenHandler.syncId;
      this.lastClickedSlot = emptiedBeenValue;
      this.lastClickedSlotSignature = spawnerStorageLayout.computeSignature(emptiedBeenValue);
      this.clickSlot(screenHandler, emptiedBeenValue);
      this.setState(st);
      this.scheduleNextAction();
   }

   private void processOpenState(ScreenHandler screenHandler) {
      this.scheduleNextAction();
      BlockHitResult crosshairTarget = this.getTargetedSpawner();
      if (screenHandler != null) {
         if (this.readSpawnerLayout(screenHandler).dropperSlotId < 0 || crosshairTarget == null && !this.isSpawnerScreenTitle()) {
            if (crosshairTarget == null || this.spawnerPos != null && !crosshairTarget.getBlockPos().equals(this.spawnerPos)) {
               if (!this.warnedNotSpawnerMenu) {
                  this.warnedNotSpawnerMenu = true;
                  this.sendPrefixedMessage("this menu isn't a spawner's \u2014 open a spawner to drop its bones.");
               }
            } else {
               this.closeScreenSilently();
            }
         } else {
            this.setTrackedScreenHandler(screenHandler);
            if (this.spawnerPos == null && crosshairTarget != null) {
               this.spawnerPos = crosshairTarget.getBlockPos();
            }

            this.startScanning(false);
         }
      } else if (mc.currentScreen == null) {
         if (crosshairTarget == null || this.spawnerPos != null && !crosshairTarget.getBlockPos().equals(this.spawnerPos)) {
            if (this.spawnerPos != null && this.reopenCount > 0 && this.getStateElapsedMs() > 3000L) {
               this.finishRun(this.buildStopReason("look back at the spawner and turn it on again to continue"), true);
            } else {
               if (!this.warnedLookAtSpawner) {
                  this.warnedLookAtSpawner = true;
                  this.sendPrefixedMessage(this.spawnerPos == null ? "look at a spawner (or open it) to start." : "look back at the spawner to continue.");
               }
            }
         } else if (mc.player.isSneaking()) {
            if (!this.warnedSneaking) {
               this.warnedSneaking = true;
               this.sendPrefixedMessage("stop sneaking so the spawner menu can open.");
            }
         } else if (!this.ensureSafeHeldItem()) {
            this.finishRun("hold an empty hand or a tool \u2014 right-clicking the spawner with blocks or spawn eggs isn't safe.", true);
         } else {
            this.spawnerPos = crosshairTarget.getBlockPos();
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, crosshairTarget);
            mc.player.swingHand(Hand.MAIN_HAND);
            this.setState(BoneDropper.St.SP_WAIT);
         }
      }
   }

   private void processDroppedState(ScreenHandler screenHandler) {
      boolean disappearedLootFlag = this.peakEntityCount > this.baselineDroppedBoneCount || this.countInventoryBones() > this.lastBoneInventoryCount;
      if (screenHandler == null) {
         if (disappearedLootFlag) {
            this.recordDroppedPage();
            this.reopenSpawner();
         } else if (this.getStateElapsedMs() > this.getTimeoutMs()) {
            this.retryUnresponsiveDrop();
         } else {
            this.scheduleNextAction();
         }
      } else if (screenHandler == this.screenHandler || this.adoptOrRejectOpenedMenu(screenHandler)) {
         SpawnerStorageLayout codeBone = this.readSpawnerLayout(screenHandler);
         int linesLore = this.readLoreBoneCount(screenHandler);
         boolean disappearedLootFlag2 = screenHandler == this.lastClickedScreenHandler && screenHandler.syncId == this.syncId;
         if (disappearedLootFlag
            || disappearedLootFlag2 && codeBone.boneCount < this.currentPageBoneCount
            || this.lastLoreBoneCount >= 0 && linesLore >= 0 && linesLore < this.lastLoreBoneCount) {
            this.recordDroppedPage();
            if (disappearedLootFlag2 && codeBone.boneCount > 0) {
               this.staleDropDetected = true;
            }

            if (codeBone.boneCount > 0 && codeBone.nextSlotId >= 0 && this.pageAdvanceCount < 200) {
               this.pageAdvanceCount++;
               this.clickSlotAndTrack(screenHandler, codeBone, codeBone.nextSlotId, BoneDropper.St.SP_PAGED);
            } else if (codeBone.boneCount > 0) {
               this.closeAndReopenSpawner();
            } else {
               this.setState(BoneDropper.St.SP_SCAN);
               this.scheduleNextAction();
            }
         } else if (this.getStateElapsedMs() > this.getTimeoutMs()) {
            this.retryUnresponsiveDrop();
         } else {
            this.scheduleNextAction();
         }
      }
   }

   private void runOrderStateMachine() {
      switch (this.order) {
         case OR_START:
            if (!this.hasContainerScreenOpen()) {
               this.sendCommand("order");
               this.order = BoneDropper.St.OR_WAIT;
            } else {
               this.order = BoneDropper.St.OR_S1;
            }

            this.scheduleNextAction();
            break;
         case OR_WAIT:
            if (!this.hasContainerScreenOpen()) {
               this.sendCommand("order");
            } else {
               this.order = BoneDropper.St.OR_S1;
            }

            this.scheduleNextAction();
            break;
         case OR_S1:
            this.clickOrderCategorySlot(BoneDropper.Cat.CHEST, BoneDropper.St.OR_S2);
            break;
         case OR_S2:
            this.clickOrderCategorySlot(BoneDropper.Cat.BONE, BoneDropper.St.OR_S3);
            break;
         case OR_S3:
            this.clickOrderCategorySlot(BoneDropper.Cat.CHEST, BoneDropper.St.OR_S4);
            break;
         case OR_S4:
            this.clickOrderCategorySlot(BoneDropper.Cat.DROPPER, BoneDropper.St.OR_S5);
            break;
         case OR_S5:
            this.clickOrderCategorySlot(BoneDropper.Cat.ARROW, BoneDropper.St.OR_S6);
            break;
         case OR_S6:
            this.clickOrderCategorySlot(BoneDropper.Cat.DROPPER, BoneDropper.St.OR_S1);
            break;
         default:
            this.order = BoneDropper.St.OR_START;
            this.scheduleNextAction();
      }
   }

   private void setState(BoneDropper.St order) {
      this.order = order;
      this.stateEnteredMillis = System.currentTimeMillis();
   }

   private int readLoreBoneCount(ScreenHandler screenHandler) {
      int linesLoreLimit = this.getContainerSlotCount(screenHandler);

      for (int i = 0; i < linesLoreLimit; i++) {
         LoreComponent stack = screenHandler.getSlot(i).getStack().get(DataComponentTypes.LORE);
         if (stack != null) {
            var arrayList = new ArrayList(stack.lines().size());

            for (Text text : stack.lines()) {
               arrayList.add(text.getString());
            }

            int linesLore = SpawnerStorageLayout.parseBoneCount(arrayList);
            if (linesLore >= 0) {
               return linesLore;
            }
         }
      }

      return -1;
   }

   private void setTrackedScreenHandler(ScreenHandler screenHandler) {
      this.screenHandler = screenHandler;
   }

   private void processPagedState(ScreenHandler screenHandler) {
      if (screenHandler == null) {
         this.retryOrReopen();
      } else if (screenHandler == this.screenHandler || this.adoptOrRejectOpenedMenu(screenHandler)) {
         if (screenHandler != this.lastClickedScreenHandler || screenHandler.syncId != this.syncId || this.readSpawnerLayout(screenHandler).computeSignature(this.lastClickedSlot) != this.lastClickedSlotSignature) {
            this.unchangedPollCount = 0;
            this.setState(BoneDropper.St.SP_SCAN);
         } else if (this.getStateElapsedMs() > this.getTimeoutMs()) {
            this.unchangedPollCount++;
            this.setState(BoneDropper.St.SP_SCAN);
         }

         this.scheduleNextAction();
      }
   }

   private boolean hasContainerScreenOpen() {
      return mc.currentScreen instanceof HandledScreen
         && mc.player != null
         && mc.player.currentScreenHandler != null
         && mc.player.currentScreenHandler != mc.player.playerScreenHandler;
   }

   @Override
   public void onEnable() {
      this.resetRunState();
      this.lastWorld = mc.world;
   }

   private void closeAndReopenSpawner() {
      if (this.spawnerPos == null) {
         this.finishRun(
            this.buildStopReason(
               this.droppedPageCount > 0
                  ? "open the spawner again to keep going"
                  : "nothing from this page on \u2014 open the spawner fresh to check from page 1"
            ),
            true
         );
      } else {
         if (this.hasContainerScreenOpen()) {
            this.closeScreenSilently();
         }

         this.reopenSpawner();
      }
   }

   private void closeScreenSilently() {
      this.closingScreenSelf = true;

      try {
         if (mc.player != null) {
            mc.player.closeHandledScreen();
         }

         mc.setScreen(null);
      } finally {
         this.closingScreenSelf = false;
      }
   }

   private void recordDroppedPage() {
      this.droppedPageCount++;
      this.dropsSinceReopen++;
      this.totalBonesDropped = this.totalBonesDropped + this.currentPageBoneCount;
      this.unresponsiveRetryCount = 0;
      this.unchangedPollCount = 0;
   }

   private boolean isSpawnerScreenTitle() {
      return mc.currentScreen != null && SpawnerStorageLayout.normalizeText(mc.currentScreen.getTitle().getString()).contains("spawner");
   }

   private SpawnerStorageLayout readSpawnerLayout(ScreenHandler screenHandler) {
      int codeBoneLimit = this.getContainerSlotCount(screenHandler);
      var arrayList = new ArrayList(codeBoneLimit);

      for (int i = 0; i < codeBoneLimit; i++) {
         Slot slot = screenHandler.getSlot(i);
         ItemStack stack = slot.getStack();
         Kind prevDropper = SpawnerStorageLayout.classifyCell(stack.isEmpty(), stack.isOf(Items.BONE), stack.getName().getString());
         arrayList.add(new Cell(i, slot.x, slot.y, prevDropper, stack.getCount(), ItemStack.hashCode(stack)));
      }

      return SpawnerStorageLayout.fromCells(arrayList);
   }

   private void finishRun(String screenText, boolean screenFlag) {
      if (this.screenHandler != null && this.getOpenScreenHandler() == this.screenHandler) {
         this.closeScreenSilently();
      }

      if (screenFlag || this.notificationsSetting.isEnabled()) {
         this.sendChatMessage("\u00a7b[BoneDropper]\u00a7r " + screenText);
      }

      this.setTrackedScreenHandler(null);
      this.order = BoneDropper.St.SP_DONE;
      this.nextActionTimeMs = Long.MAX_VALUE;
      this.setEnabled(false);
   }

   private void processWaitState(ScreenHandler screenHandler) {
      this.scheduleNextAction();
      if (screenHandler != null) {
         if (this.readSpawnerLayout(screenHandler).dropperSlotId >= 0) {
            this.setTrackedScreenHandler(screenHandler);
            this.menuOpenRetryCount = 0;
            this.startScanning(true);
         } else if (this.getStateElapsedMs() > 3000L) {
            this.finishRun(this.buildStopReason("the menu that opened has no Drop Loot button"), true);
         }
      } else if (this.getStateElapsedMs() > 3000L) {
         if (++this.menuOpenRetryCount >= 3) {
            this.finishRun(this.buildStopReason("the spawner menu didn't open"), true);
         } else {
            this.setState(BoneDropper.St.SP_OPEN);
         }
      }
   }

   private BlockHitResult getTargetedSpawner() {
      if (mc.world == null) {
         return null;
      } else {
         return mc.crosshairTarget instanceof BlockHitResult blockHitResult
               && blockHitResult.getType() == Type.BLOCK
               && mc.world.getBlockState(blockHitResult.getBlockPos()).isOf(Blocks.SPAWNER)
            ? blockHitResult
            : null;
      }
   }

   public BoneDropper() {
      super("BoneDropper", IconsPngType.DONUTSMP);
      this.notificationsSetting = new BooleanSetting("Notifications", "Chat feedback when the spawner has been emptied.", true);
      this.order = BoneDropper.St.SP_OPEN;
      this.lastClickedSlot = -1;
      this.addSettings(new ConfigSetting[]{this.modeSetting, this.delaySetting, this.notificationsSetting});
   }

   private void runSpawnerStateMachine() {
      if (this.menuClosedByUser) {
         this.menuClosedByUser = false;
         this.finishRun(this.buildStopReason("stopped because the menu was closed"), false);
      } else {
         ScreenHandler handlerScreen = this.getOpenScreenHandler();
         switch (this.order) {
            case SP_OPEN:
               this.processOpenState(handlerScreen);
               break;
            case SP_WAIT:
               this.processWaitState(handlerScreen);
               break;
            case SP_SCAN:
               this.processScanState(handlerScreen);
               break;
            case SP_DROPPED:
               this.processDroppedState(handlerScreen);
               break;
            case SP_PAGED:
               this.processPagedState(handlerScreen);
            case SP_DONE:
               break;
            default:
               this.setState(BoneDropper.St.SP_OPEN);
               this.scheduleNextAction();
         }
      }
   }

   private void retryOrReopen() {
      if (this.getStateElapsedMs() > this.getTimeoutMs()) {
         this.reopenSpawner();
      } else {
         this.scheduleNextAction();
      }
   }

   private int getContainerSlotCount(ScreenHandler screenHandler) {
      int slotsSize = screenHandler.slots.size();
      int boneDropperValue = Math.max(0, slotsSize - 36);
      return boneDropperValue == 0 ? slotsSize : boneDropperValue;
   }

   private boolean adoptOrRejectOpenedMenu(ScreenHandler screenHandler) {
      if (this.readSpawnerLayout(screenHandler).dropperSlotId >= 0) {
         this.setTrackedScreenHandler(screenHandler);
         return true;
      } else {
         if (this.getStateElapsedMs() > this.getTimeoutMs()) {
            this.finishRun(this.buildStopReason("stopped because a different menu opened"), true);
         } else {
            this.scheduleNextAction();
         }

         return false;
      }
   }

   private void sendPrefixedMessage(String boneDropperText) {
      this.sendChatMessage("\u00a7b[BoneDropper]\u00a7r " + boneDropperText);
   }

   private int findSlotForCategory(ScreenHandler screenHandler, BoneDropper.Cat cat, boolean slotStackFlag) {
      int slotStackLimit = this.getContainerSlotCount(screenHandler);
      int slotStack = -1;

      for (int i = 0; i < slotStackLimit; i++) {
         ItemStack stack = screenHandler.getSlot(i).getStack();
         if (!stack.isEmpty() && this.matchesCategory(stack, cat)) {
            slotStack = i;
            if (!slotStackFlag) {
               break;
            }
         }
      }

      return slotStack;
   }

   private void onWorldChanged() {
      if (this.isSpawnerMode()) {
         this.finishRun(
            this.droppedPageCount > 0 ? this.buildStopReason("stopped because the world changed") : "turned off \u2014 switch it on at the spawner to run it.", true
         );
      } else {
         this.sendChatMessage("\u00a7b[BoneDropper]\u00a7r turned off \u2014 switch it on again to restart /order runs.");
         this.setEnabled(false);
      }
   }

   private void startScanning(boolean pagesExistFlag) {
      this.strictPageCheck = pagesExistFlag;
      this.pageAdvanceCount = 0;
      this.unchangedPollCount = 0;
      this.dropsSinceReopen = 0;
      this.staleDropDetected = false;
      this.pageLimitReached = false;
      this.laterPagesIdentical = false;
      this.setState(BoneDropper.St.SP_SCAN);
   }

   private long getStateElapsedMs() {
      return System.currentTimeMillis() - this.stateEnteredMillis;
   }

   private void retryUnresponsiveDrop() {
      if (++this.unresponsiveRetryCount >= 3) {
         this.finishRun(this.buildStopReason("the server isn't reacting to the Drop Loot button"), true);
      } else {
         this.closeAndReopenSpawner();
      }
   }

   private void resetRunState() {
      this.activeModeName = this.modeSetting.getCurrentOption();
      this.setState(this.isSpawnerMode() ? BoneDropper.St.SP_OPEN : BoneDropper.St.OR_START);
      this.nextActionTimeMs = 0L;
      this.spawnerPos = null;
      this.setTrackedScreenHandler(null);
      this.strictPageCheck = false;
      this.closingScreenSelf = false;
      this.menuClosedByUser = false;
      this.laterPagesIdentical = false;
      this.pageLimitReached = false;
      this.staleDropDetected = false;
      this.lastClickedScreenHandler = null;
      this.syncId = 0;
      this.lastClickedSlot = -1;
      this.lastClickedSlotSignature = 0L;
      this.baselineDroppedBoneCount = 0;
      this.peakEntityCount = 0;
      this.lastBoneInventoryCount = 0;
      this.currentPageBoneCount = 0;
      this.lastLoreBoneCount = -1;
      this.pageAdvanceCount = 0;
      this.unchangedPollCount = 0;
      this.dropsSinceReopen = 0;
      this.reopenCount = 0;
      this.unresponsiveRetryCount = 0;
      this.menuOpenRetryCount = 0;
      this.droppedPageCount = 0;
      this.totalBonesDropped = 0;
      this.warnedLookAtSpawner = false;
      this.warnedSneaking = false;
      this.warnedNotSpawnerMenu = false;
   }

   @Override
   public void onReset() {
      if (mc.player != null && mc.world != null && mc.interactionManager != null) {
         if (!this.modeSetting.getCurrentOption().equalsIgnoreCase(this.activeModeName)) {
            this.resetRunState();
         }

         if (mc.world != this.lastWorld) {
            this.onWorldChanged();
         } else {
            if (this.isSpawnerMode()) {
               this.trackPeakDroppedBones();
            }

            if (mc.currentScreen == null || mc.currentScreen instanceof HandledScreen) {
               if (System.currentTimeMillis() >= this.nextActionTimeMs) {
                  if (this.isSpawnerMode()) {
                     this.runSpawnerStateMachine();
                  } else {
                     this.runOrderStateMachine();
                  }
               }
            }
         }
      }
   }

   private void clickOrderCategorySlot(BoneDropper.Cat cat, BoneDropper.St order) {
      ScreenHandler handlerScreen = this.getOpenScreenHandler();
      if (handlerScreen == null) {
         this.sendCommand("order");
         this.scheduleNextAction();
      } else {
         int slotStack = this.findSlotForCategory(handlerScreen, cat, cat == BoneDropper.Cat.DROPPER || cat == BoneDropper.Cat.ARROW);
         if (slotStack < 0) {
            this.scheduleNextAction();
         } else {
            this.clickSlot(handlerScreen, slotStack);
            this.order = order;
            this.scheduleNextAction();
         }
      }
   }

   private void clickSlot(ScreenHandler screenHandler, int interactionClickValue) {
      if (mc.interactionManager != null && mc.player != null) {
         mc.interactionManager.clickSlot(screenHandler.syncId, interactionClickValue, 0, SlotActionType.PICKUP, mc.player);
      }
   }

   private void trackPeakDroppedBones() {
      if (this.order == BoneDropper.St.SP_DROPPED) {
         this.peakEntityCount = Math.max(this.peakEntityCount, this.countNearbyDroppedBones());
      }
   }

   @Override
   protected void onPacketSend(InventoryPlayer inventoryPlayer) {
      if (!this.closingScreenSelf && this.isSpawnerMode() && this.order != BoneDropper.St.SP_DONE) {
         if (inventoryPlayer.packet instanceof CloseHandledScreenC2SPacket closeHandledScreenC2SPacket && closeHandledScreenC2SPacket.getSyncId() != 0) {
            if (this.screenHandler != null || this.spawnerPos != null || this.order == BoneDropper.St.SP_WAIT) {
               this.menuClosedByUser = true;
            }
         }
      }
   }

   private static boolean isUnsafeToRightClickWith(ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      } else {
         Item item = stack.getItem();
         return item instanceof BlockItem
            || item instanceof SpawnEggItem
            || item instanceof FlintAndSteelItem
            || item instanceof FireChargeItem
            || item instanceof BucketItem
            || item instanceof ArmorStandItem
            || item instanceof BoatItem
            || item instanceof MinecartItem
            || item instanceof EndCrystalItem
            || item instanceof DecorationItem;
      }
   }

   private static enum Cat {
      BONE,
      CHEST,
      DROPPER,
      ARROW;
   }

   private static enum St {
      SP_OPEN,
      SP_WAIT,
      SP_SCAN,
      SP_DROPPED,
      SP_PAGED,
      SP_DONE,
      OR_START,
      OR_WAIT,
      OR_S1,
      OR_S2,
      OR_S3,
      OR_S4,
      OR_S5,
      OR_S6;
   }
}
