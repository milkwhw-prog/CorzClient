package com.corz.client.categories.donutsmp;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.utils.SellChestFlow.Action;
import com.corz.client.utils.SellChestFlow.Phase;
import com.corz.client.utils.SellChestFlow.Screen;
import com.corz.client.utils.SellChestFlow.View;
import com.corz.client.utils.SellMenu.Cell;
import com.corz.client.gui.DisabledEnabled;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BarrelBlock;
import net.minecraft.block.Block;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.ingame.GenericContainerScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.ClientConnection;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.text.Text;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import com.corz.client.utils.SellChestFlow;
import com.corz.client.utils.SellMenu;
import com.corz.client.categories.settings.Notifications;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.categories.donutsmp.AutoSell;
import com.corz.client.utils.BlockHitResolver;
import com.corz.client.utils.ExtUtils;
import com.corz.client.utils.IconsPngType;

public class AutoSellChest extends Module {
   private int actionStartTick;
   private World targetWorld;
   private Runnable pendingAction;
   private Vec3d startPlayerPos;
   private ClientConnection connection;
   private int tickCount;
   private boolean actionExecuted;
   private SellChestFlow sellChestFlow;
   private BlockPos chestPos;
   private final IntegerSetting clickDelaySetting = new IntegerSetting(
      "Click Delay", "Ticks between item moves. Higher is slower but gentler on the server.", 2, 1, 10
   );
   private final boolean[] preexistingSlotFlags = new boolean[36];
   private int depositSlotCursor;

   private boolean hasPlayerMoved() {
      ClientPlayerEntity player = mc.player;
      if (this.startPlayerPos == null) {
         return false;
      } else if (mc.world != this.targetWorld) {
         return true;
      } else {
         Vec3d entityPos = player.getEntityPos();
         double deltaX = entityPos.x - this.startPlayerPos.x;
         double deltaZ = entityPos.z - this.startPlayerPos.z;
         return Math.sqrt(deltaX * deltaX + deltaZ * deltaZ) > 0.75 || Math.abs(entityPos.y - this.startPlayerPos.y) > 1.0;
      }
   }

   @Override
   protected void onDisable() {
      this.cancelPendingAction();
      this.sellChestFlow = null;
      this.connection = null;
   }

   private void closeOpenContainer() {
      ClientPlayerEntity player = mc.player;
      if (player != null && mc.currentScreen instanceof HandledScreen && player.currentScreenHandler != player.playerScreenHandler) {
         player.closeHandledScreen();
      }
   }

   private void queueDepositClick() {
      ClientPlayerEntity player = mc.player;
      if (player.currentScreenHandler instanceof GenericContainerScreenHandler genericContainerScreenHandle) {
         int rowsSlotsValue = Math.min(genericContainerScreenHandle.getRows() * 9, genericContainerScreenHandle.slots.size());
         int rowsSlotsLimit = genericContainerScreenHandle.slots.size() - rowsSlotsValue;

         for (int i = 0; i < rowsSlotsLimit; i++) {
            int rowsSlotsValue2 = rowsSlotsValue + Math.floorMod(this.depositSlotCursor + i, rowsSlotsLimit);
            if (this.isNewInventoryItemSlot(genericContainerScreenHandle.getSlot(rowsSlotsValue2), player)) {
               this.depositSlotCursor = rowsSlotsValue2 - rowsSlotsValue + 1;
               int syncId = genericContainerScreenHandle.syncId;
               this.scheduleAction(() -> {
                  ClientPlayerEntity var4x = mc.player;
                  if (var4x != null && mc.interactionManager != null && var4x.currentScreenHandler == genericContainerScreenHandle && genericContainerScreenHandle.syncId == syncId) {
                     if (rowsSlotsValue2 < genericContainerScreenHandle.slots.size() && this.isNewInventoryItemSlot(genericContainerScreenHandle.getSlot(rowsSlotsValue2), var4x)) {
                        mc.interactionManager.clickSlot(syncId, rowsSlotsValue2, 0, SlotActionType.QUICK_MOVE, var4x);
                     }
                  }
               });
               return;
            }
         }
      }
   }

   private void cancelPendingAction() {
      if (this.pendingAction != null) {
         ExtUtils.canActionFlag(this.pendingAction);
      }

      this.pendingAction = null;
      this.actionExecuted = false;
   }

   private BlockHitResult getTargetedChestHit() {
      ClientPlayerEntity player = mc.player;
      if (player != null && mc.world != null && !player.isSneaking()) {
         if (mc.crosshairTarget instanceof BlockHitResult blockHitResult && blockHitResult.getType() == Type.BLOCK) {
            Block block = mc.world.getBlockState(blockHitResult.getBlockPos()).getBlock();
            if (!(block instanceof ChestBlock) && !(block instanceof BarrelBlock)) {
               return null;
            } else if (player.getEyePos().distanceTo(blockHitResult.getPos()) > 4.5) {
               return null;
            } else {
               return this.chestPos != null && !this.chestPos.equals(blockHitResult.getBlockPos()) ? null : blockHitResult;
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   @Override
   protected void onEnable() {
      this.sellChestFlow = null;
      this.chestPos = null;
      this.startPlayerPos = null;
      this.targetWorld = null;
      this.pendingAction = null;
      this.actionExecuted = false;
      this.depositSlotCursor = 0;
      this.tickCount = 0;
      if (mc != null) {
         if (mc.player != null
            && mc.world != null
            && mc.interactionManager != null
            && mc.getNetworkHandler() != null) {
            Module iliIiiliiiii = ModuleRegistry.getModule(AutoSell.class);
            if (iliIiiliiiii != null && iliIiiliiiii.isEnabled()) {
               mc.send(() -> this.finishFlow("Turn off AutoSell first - both would use /sell.", false));
            } else {
               PlayerInventory inventory = mc.player.getInventory();
               int autoSellCounter = 0;

               for (int i = 0; i < 36; i++) {
                  this.preexistingSlotFlags[i] = !inventory.getStack(i).isEmpty();
                  if (this.preexistingSlotFlags[i]) {
                     autoSellCounter++;
                  }
               }

               this.connection = mc.getNetworkHandler().getConnection();
               this.sellChestFlow = new SellChestFlow(this.clickDelaySetting.getValue());
            }
         } else {
            mc.send(() -> this.finishFlow(null, false));
         }
      }
   }

   public AutoSellChest() {
      super("Auto Sell Chest", IconsPngType.DONUTSMP);
      this.addSettings(new ConfigSetting[]{this.clickDelaySetting});
   }

   private void finishFlow(String flowSellText, boolean flowSellFlag) {
      this.cancelPendingAction();
      this.sellChestFlow = null;
      if (this.isEnabled()) {
         try (AutoCloseable disabledEnabled = DisabledEnabled.getInstance().getDisabledEnabled()) {
            this.setEnabled(false);
         } catch (Exception e) {
            this.sellChestFlow = null;
         }
      }

      if (flowSellText != null) {
         this.sendStatusMessage(flowSellText, flowSellFlag);
      }
   }

   private void sendStatusMessage(String sellChestText, boolean sellChestFlag) {
      if (!Notifications.isInstanceBase()) {
         if (mc.player != null) {
            mc.player.sendMessage(Text.literal("\u00a77[Auto Sell Chest]\u00a7r " + sellChestText), false);
         }
      } else {
         DisabledEnabled.getInstance().handleDisabledEnabled("Auto Sell Chest", sellChestText, sellChestFlag);
      }
   }

   private void scheduleAction(Runnable runnable) {
      this.cancelPendingAction();
      Runnable sellChestAction = new Runnable() {
         @Override
         public void run() {
            if (AutoSellChest.this.pendingAction == this) {
               AutoSellChest.this.actionExecuted = true;
            }

            runnable.run();
         }
      };
      this.pendingAction = sellChestAction;
      this.actionStartTick = this.tickCount;
      ExtUtils.handleExt(sellChestAction, false);
   }

   private void performFlowAction(Action action) {
      switch (action.kind()) {
         case OPEN_CHEST:
            this.rememberTargetChest();
            break;
         case TAKE:
            this.queueTakeClick();
            break;
         case CLOSE:
            this.closeOpenContainer();
            break;
         case SELL_COMMAND:
            mc.getNetworkHandler().sendChatCommand("sell");
            break;
         case DEPOSIT:
            this.queueDepositClick();
            break;
         case CLICK_SELL:
            this.queueSellButtonClick();
            break;
         case DONE:
            if (action.close()) {
               this.closeOpenContainer();
            }

            this.finishFlow(action.text(), true);
            break;
         case FAIL:
            if (action.close()) {
               this.closeOpenContainer();
            }

            this.finishFlow(action.text(), false);
      }
   }

   @Override
   protected void onReset() {
      if (this.sellChestFlow != null
         && mc.player != null
         && mc.world != null
         && mc.interactionManager != null
         && mc.getNetworkHandler() != null) {
         if (mc.getNetworkHandler().getConnection() != this.connection) {
            this.finishFlow("You left the server - Auto Sell Chest stopped.", false);
         } else {
            this.tickCount++;
            if (this.actionExecuted) {
               this.pendingAction = null;
               this.actionExecuted = false;
            }

            if (this.pendingAction != null && this.tickCount - this.actionStartTick > 20) {
               this.cancelPendingAction();
            }

            Phase phase = this.sellChestFlow.getPhase();
            Action sellChest = this.sellChestFlow.nextAction(this.buildFlowView());
            this.performFlowAction(sellChest);
         }
      }
   }

   private void queueSellButtonClick() {
      ClientPlayerEntity player = mc.player;
      if (player.currentScreenHandler instanceof GenericContainerScreenHandler genericContainerScreenHandle) {
         int sellPane = SellMenu.findSellButtonIndex(getContainerCells(genericContainerScreenHandle));
         if (sellPane >= 0) {
            int syncId = genericContainerScreenHandle.syncId;
            this.scheduleAction(() -> {
               ClientPlayerEntity var3x = mc.player;
               if (var3x != null && mc.interactionManager != null && var3x.currentScreenHandler == genericContainerScreenHandle && genericContainerScreenHandle.syncId == syncId) {
                  mc.interactionManager.clickSlot(syncId, sellPane, 0, SlotActionType.PICKUP, var3x);
               }
            });
         }
      }
   }

   private void rememberTargetChest() {
      ClientPlayerEntity player = mc.player;
      BlockHitResult crosshairTarget = this.getTargetedChestHit();
      if (crosshairTarget != null) {
         if (this.chestPos == null) {
            this.chestPos = crosshairTarget.getBlockPos();
            this.startPlayerPos = player.getEntityPos();
            this.targetWorld = mc.world;
         }

         BlockHitResolver.interactWithBlock(crosshairTarget);
      }
   }

   private View buildFlowView() {
      ClientPlayerEntity player = mc.player;
      Screen screen = Screen.NONE;
      int screenSellCounter = 0;
      int screenSellTotal = 0;
      boolean screenSellFlag = false;
      boolean emptyCellBesides2 = false;
      boolean screenSellFlag3 = false;
      if (mc.currentScreen != null) {
         if (mc.currentScreen instanceof GenericContainerScreen && player.currentScreenHandler instanceof GenericContainerScreenHandler genericContainerScreenHandle) {
            screen = Screen.CONTAINER;
            int screenSellLimit = Math.min(genericContainerScreenHandle.getRows() * 9, genericContainerScreenHandle.slots.size());

            for (int i = 0; i < screenSellLimit; i++) {
               ItemStack stack = genericContainerScreenHandle.getSlot(i).getStack();
               if (!stack.isEmpty()) {
                  screenSellCounter++;
                  screenSellTotal += stack.getCount();
               }
            }

            var stainedPane = getContainerCells(genericContainerScreenHandle);
            int sellPane = SellMenu.findSellButtonIndex(stainedPane);
            screenSellFlag = SellMenu.isSellMenu(mc.currentScreen.getTitle().getString(), stainedPane);
            screenSellFlag3 = sellPane >= 0;
            emptyCellBesides2 = SellMenu.hasEmptyCellBesides(stainedPane, sellPane);
         } else {
            screen = Screen.OTHER;
         }
      }

      PlayerInventory inventory = player.getInventory();
      boolean screenSellFlag4 = false;
      int screenSellCounter2 = 0;
      int screenSellTotal2 = 0;

      for (int j = 0; j < 36; j++) {
         ItemStack stack2 = inventory.getStack(j);
         if (stack2.isEmpty()) {
            screenSellFlag4 = true;
         } else if (!this.preexistingSlotFlags[j]) {
            screenSellCounter2++;
            screenSellTotal2 += stack2.getCount();
         }
      }

      boolean screenSellFlag5 = !player.isAlive() || mc.currentScreen instanceof DeathScreen;
      return new View(
         this.tickCount,
         screen,
         screenSellFlag,
         this.hasPlayerMoved(),
         screenSellFlag5,
         this.getTargetedChestHit() != null,
         screenSellCounter,
         screenSellTotal,
         screenSellFlag4,
         screenSellCounter2,
         screenSellTotal2,
         emptyCellBesides2,
         screenSellFlag3,
         this.pendingAction != null
      );
   }

   private boolean isNewInventoryItemSlot(Slot slot2, ClientPlayerEntity player) {
      if (slot2 != null && slot2.inventory == player.getInventory()) {
         int index = slot2.getIndex();
         return index >= 0 && index < 36 && !this.preexistingSlotFlags[index] && !slot2.getStack().isEmpty();
      } else {
         return false;
      }
   }

   private void queueTakeClick() {
      ClientPlayerEntity player = mc.player;
      if (player.currentScreenHandler instanceof GenericContainerScreenHandler genericContainerScreenHandle) {
         int rowsSlotLimit = Math.min(genericContainerScreenHandle.getRows() * 9, genericContainerScreenHandle.slots.size());

         for (int i = 0; i < rowsSlotLimit; i++) {
            if (!genericContainerScreenHandle.getSlot(i).getStack().isEmpty()) {
               int syncId = genericContainerScreenHandle.syncId;
               int rowsSlotValue = i;
               this.scheduleAction(() -> {
                  ClientPlayerEntity var3x = mc.player;
                  if (var3x != null && mc.interactionManager != null && var3x.currentScreenHandler == genericContainerScreenHandle && genericContainerScreenHandle.syncId == syncId) {
                     if (rowsSlotValue < genericContainerScreenHandle.slots.size() && !genericContainerScreenHandle.getSlot(rowsSlotValue).getStack().isEmpty()) {
                        mc.interactionManager.clickSlot(syncId, rowsSlotValue, 0, SlotActionType.QUICK_MOVE, var3x);
                     }
                  }
               });
               return;
            }
         }
      }
   }

   private static List<Cell> getContainerCells(GenericContainerScreenHandler genericContainerScreenHandle) {
      int stainedPaneLimit = Math.min(genericContainerScreenHandle.getRows() * 9, genericContainerScreenHandle.slots.size());
      var arrayList = new ArrayList(stainedPaneLimit);

      for (int i = 0; i < stainedPaneLimit; i++) {
         ItemStack stack = genericContainerScreenHandle.getSlot(i).getStack();
         if (stack.isEmpty()) {
            arrayList.add(Cell.INSTANCE);
         } else {
            boolean stainedPaneFlag = stack.isOf(Items.LIME_STAINED_GLASS_PANE) || stack.isOf(Items.GREEN_STAINED_GLASS_PANE);
            arrayList.add(new Cell(false, stack.getName().getString(), stainedPaneFlag));
         }
      }

      return arrayList;
   }
}
