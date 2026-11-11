package com.corz.client.categories.combat;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.inventory.InventorySelectedUtils;
import com.corz.client.inventory.InventoryStackUtilsHandle;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.function.BooleanSupplier;
import net.minecraft.client.gui.screen.DeathScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.network.packet.c2s.play.CloseHandledScreenC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action;
import net.minecraft.network.packet.c2s.play.PlayerInteractBlockC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerInteractItemC2SPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SetPlayerInventoryS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.sync.ComponentChangesHash.ComponentHasher;
import net.minecraft.screen.sync.ItemStackHash;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import com.corz.client.categories.movement.Scaffold;
import com.corz.client.utils.AutoVanish;
import com.corz.client.utils.InventoryPlayer;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.RecentDelayRandomizer;
import com.corz.client.utils.MouseInventoryScreen;
import com.corz.client.categories.combat.SafeAnchor;
import com.corz.client.utils.AutoYourUtils;
import com.corz.client.utils.ExtUtils;
import com.corz.client.utils.IconsPngType;

public class AutoInventoryTotem extends Module {
   private final boolean[] totemSlotMemory;
   private static volatile int pendingSwapSourceSlot = -1;
   private boolean reequipPending;
   private boolean poppedWithoutMainHandTotem;
   private int handSwapTotemSlot;
   private final Random random;
   private int screenOpenedTick;
   private int pendingTargetSlot;
   private static volatile BooleanSupplier externalSecondarySupplier;
   private static volatile Object externalOwner;
   private int screenCooldownUntilTick;
   private long lastSwapSentMs;
   private int reequipAtTick;
   private final RecentDelayRandomizer reactionHistory;
   private final BooleanSetting reserve;
   private int popStartTick;
   private long nextActionTimeMs;
   private float lastTotalHealth;
   private boolean offhandSwapInFlight;
   private boolean offhandHasTotem;
   private long lastPopMs;
   private final IntegerSetting swapSpacingTicks;
   private int handSwapStage;
   private int lastInteractAge;
   private Runnable pendingClickAction;
   static final int SINGLE_TOTEM_COUNT = 1;
   private static volatile int pendingSwapTargetSlot = -1;
   private int handSwapUntilTick;
   private volatile long lastFullSyncMs;
   private static volatile long fullSyncCount;
   private static volatile boolean handSwapSlotActive;
   private volatile long lastRespawnMs;
   private int originalSelectedSlot;
   private final BooleanSetting keepReserveTotem;
   private static final AtomicIntegerArray SLOT_UPDATE_COUNTS = new AtomicIntegerArray(46);
   private long handSwapSentMs;
   private AutoInventoryTotem.PendingSwap pendingSwap;
   private final BooleanSetting clicksOnlyWhenIdle;
   private MouseInventoryScreen mouseInventoryScreen;
   private final IntegerSetting maxReaction;
   private boolean respawnPending;
   private long lastClickSentMs;
   private static volatile String lastSlotUpdateText = "";
   private final BooleanSetting moveBypass;
   private final BooleanSetting humanize;
   private int tickCount;
   private final BooleanSetting panicReEquip;
   private final IntegerSetting minReaction;
   private int screenReadyTick;
   private final IntegerSetting totemSlot;
   private boolean reactionNotRecorded;
   private final BooleanSetting offHandViaHandSwap;
   private final BooleanSetting legitMode = new BooleanSetting(
      "Legit Mode",
      "Move totems the way a player does it by hand: your inventory really opens on screen, the totem is swapped into place (into the off-hand like pressing F over it, into the reserve like pressing its number key), then the inventory closes. You stop moving while it's open, exactly like vanilla, and your own keys and clicks can't move items in it. A bit slower than the silent mode and you can't fight while it's open; Off-hand Via Hand Swap isn't used. Off = silent swaps, no screen.",
      false
   );
   private int screenCloseTick;
   private static volatile BooleanSupplier externalPrimarySupplier;

   public static boolean wasTotemSlot(int index) {
      AutoInventoryTotem autoInventoryTotem = getActiveInstance();
      return autoInventoryTotem != null && index >= 0 && index < 9 && autoInventoryTotem.totemSlotMemory[index];
   }

   private void checkPendingSwapResult(long invTgtValue) {
      AutoInventoryTotem.PendingSwap pendingSwap = this.pendingSwap;
      if (pendingSwap != null) {
         PlayerInventory inventory = mc.player.getInventory();
         long invTgtLimit = invTgtValue - pendingSwap.sentMs();
         boolean invTgtFlag = SLOT_UPDATE_COUNTS.get(pendingSwap.srcHandlerSlot()) > pendingSwap.srcSeqAtSend();
         boolean invTgtFlag2 = SLOT_UPDATE_COUNTS.get(pendingSwap.tgtHandlerSlot()) > pendingSwap.tgtSeqAtSend();
         boolean invTgtFlag3 = fullSyncCount > pendingSwap.fullSyncSeqAtSend();
         boolean invTgtFlag4 = invTgtFlag || invTgtFlag2 || invTgtFlag3;
         boolean of = inventory.getStack(pendingSwap.tgtInv()).isOf(Items.TOTEM_OF_UNDYING);
         if (invTgtFlag4 && of) {
            this.clearPendingSwap();
         } else if (invTgtFlag4 && invTgtLimit >= 60L) {
            this.onSwapFailed(
               invTgtValue,
               pendingSwap,
               "NOT APPLIED \u2014 server "
                  + (invTgtFlag3 ? "resynced the inventory" : "updated the slots")
                  + " after "
                  + invTgtLimit
                  + "ms; "
                  + describeSlot(pendingSwap.tgtInv())
                  + " holds "
                  + describeStack(inventory.getStack(pendingSwap.tgtInv()))
                  + ", inv "
                  + pendingSwap.srcInv()
                  + " holds "
                  + describeStack(inventory.getStack(pendingSwap.srcInv()))
                  + "; last slot update: "
                  + lastSlotUpdateText
            );
            this.clearPendingSwap();
         } else {
            if (invTgtLimit > 500L) {
               if (!invTgtFlag4) {
                  inventory.setStack(pendingSwap.srcInv(), pendingSwap.srcBefore());
                  inventory.setStack(pendingSwap.tgtInv(), pendingSwap.tgtBefore());
                  this.onSwapFailed(invTgtValue, pendingSwap, "DROPPED \u2014 no server answer in " + invTgtLimit + "ms, prediction rolled back; last slot update: " + lastSlotUpdateText);
               } else {
                  this.onSwapFailed(
                     invTgtValue,
                     pendingSwap,
                     "NOT APPLIED \u2014 only an early server update ("
                        + invTgtLimit
                        + "ms); "
                        + describeSlot(pendingSwap.tgtInv())
                        + " holds "
                        + describeStack(inventory.getStack(pendingSwap.tgtInv()))
                        + "; last slot update: "
                        + lastSlotUpdateText
                  );
               }

               this.clearPendingSwap();
            }
         }
      }
   }

   private int findHotbarTotemSlot() {
      PlayerInventory inventory = mc.player.getInventory();
      int integer = this.getReserveHotbarSlot();
      if (inventory.getStack(integer).isOf(Items.TOTEM_OF_UNDYING) && !isSlotInPendingSwap(integer)) {
         return integer;
      } else {
         for (int i = 0; i < 9; i++) {
            if (inventory.getStack(i).isOf(Items.TOTEM_OF_UNDYING) && !isSlotInPendingSwap(i)) {
               return i;
            }
         }

         return -1;
      }
   }

   private void resetState() {
      this.reequipPending = false;
      this.reequipAtTick = 0;
      this.popStartTick = 0;
      this.poppedWithoutMainHandTotem = false;
      this.reactionNotRecorded = false;
      this.offhandHasTotem = false;
      this.respawnPending = false;
      this.nextActionTimeMs = 0L;
      this.reactionHistory.reset();
      this.offhandSwapInFlight = false;
      this.lastSwapSentMs = 0L;
      this.lastPopMs = 0L;
      this.lastTotalHealth = 0.0F;
      this.handSwapUntilTick = Integer.MIN_VALUE;
      this.clearPendingSwap();
      this.restoreSelectedSlot();
      this.lastClickSentMs = -1L;
      this.pendingTargetSlot = -1;
      this.pendingClickAction = null;
      this.lastInteractAge = Integer.MIN_VALUE;
      Arrays.fill(this.totemSlotMemory, false);
   }

   public static boolean isHandSwapSlotActive() {
      return handSwapSlotActive;
   }

   private void sendSwapAndClose(int reserveNetworkValue, int autoInventoryLimit, int integer) {
      this.sendPredictedSwapClick(reserveNetworkValue, autoInventoryLimit, integer, integer == 40 ? "off-hand" : "reserve");
      if (this.reserve.isEnabled() && mc.getNetworkHandler() != null && !(mc.currentScreen instanceof HandledScreen)) {
         mc.getNetworkHandler().sendPacket(new CloseHandledScreenC2SPacket(reserveNetworkValue));
      }
   }

   public static boolean isSlotInPendingSwap(int autoInventoryValue) {
      return autoInventoryValue == pendingSwapSourceSlot || autoInventoryValue == pendingSwapTargetSlot;
   }

   private void openLegitInventoryScreen(boolean screenMouseFlag) {
      if (this.mouseInventoryScreen == null && mc.currentScreen == null && mc.player != null) {
         if (this.tickCount >= this.screenCooldownUntilTick && !this.isSwapBlocked(System.currentTimeMillis())) {
            if (!(ModuleRegistry.getModule(SafeAnchor.class) instanceof SafeAnchor safeAnchor && safeAnchor.isShieldOrComboRunning())) {
               if (InventoryStackUtilsHandle.canFlagForObject(this)) {
                  MouseInventoryScreen mouseInventoryScreen = new MouseInventoryScreen(mc.player, this);
                  mc.setScreen(mouseInventoryScreen);
                  if (mc.currentScreen != mouseInventoryScreen) {
                     InventoryStackUtilsHandle.handle(this);
                  } else {
                     this.mouseInventoryScreen = mouseInventoryScreen;
                     this.screenOpenedTick = this.tickCount;
                     this.screenReadyTick = this.tickCount + (screenMouseFlag ? 1 : 2) + this.random.nextInt(2);
                     this.screenCloseTick = -1;
                  }
               }
            }
         }
      }
   }

   private boolean tryHandSwapToOffhand(long timeMillis) {
      if (mc.currentScreen == null && mc.getNetworkHandler() != null) {
         int value = this.findHotbarTotemSlot();
         if (value == -1) {
            return false;
         } else if (this.lastInteractAge == mc.player.age) {
            return false;
         } else if (ModuleRegistry.getModule(SafeAnchor.class) instanceof SafeAnchor safeAnchor && safeAnchor.isEnabled() && safeAnchor.isAutoComboRunning()) {
            return false;
         } else {
            this.originalSelectedSlot = mc.player.getInventory().getSelectedSlot();
            this.handSwapTotemSlot = value;
            this.handSwapStage = 1;
            this.handSwapSentMs = timeMillis;
            handSwapSlotActive = true;
            InventorySelectedUtils.handleSelectedSlot(value);
            mc.getNetworkHandler().sendPacket(new PlayerActionC2SPacket(Action.SWAP_ITEM_WITH_OFFHAND, BlockPos.ORIGIN, Direction.DOWN));
            return true;
         }
      } else {
         return false;
      }
   }

   private static String describeStack(ItemStack itemStack) {
      return itemStack != null && !itemStack.isEmpty() ? itemStack.getItem().toString() + "x" + itemStack.getCount() : "empty";
   }

   private static String describeSlot(int handValue) {
      return handValue == 40 ? "off-hand" : (handValue < 9 ? "hotbar " + (handValue + 1) : "inv " + handValue);
   }

   public void forwardIncomingPacket(AutoVanish autoVanish) {
      if (!this.isEnabled() && externalOwner != null) {
         this.onPacketReceive(autoVanish);
      }
   }

   private boolean tryScheduleSwap(int pendingValue, int integer, long spectatorPlayerValue) {
      if (this.pendingSwap != null) {
         return false;
      } else if (ExtUtils.isAction() && integer != 40) {
         return false;
      } else if (this.isSwapBlocked(spectatorPlayerValue)) {
         return false;
      } else {
         boolean creativeMode = this.isLegitModeActive();
         if (creativeMode && !this.isLegitScreenReady()) {
            return false;
         } else {
            boolean pendingFlag = this.moveBypass.isEnabled() && !this.clicksOnlyWhenIdle.isEnabled() && !Scaffold.isGodBridge();
            int syncId = mc.player.playerScreenHandler.syncId;
            int autoInventoryLimit = hotbarToHandlerSlot(pendingValue);
            int value = integer == 40 ? 40 : integer;
            this.nextActionTimeMs = spectatorPlayerValue + this.getSwapSpacingMs();
            this.pendingTargetSlot = value;
            Runnable action = () -> {
               if (this.isEnabled() || externalOwner != null) {
                  if (mc.player != null && mc.interactionManager != null && mc.player.isAlive()) {
                     if (mc.player.currentScreenHandler == mc.player.playerScreenHandler) {
                        if (mc.player.currentScreenHandler.getCursorStack().isEmpty()) {
                           if (this.pendingSwap == null) {
                              if (!this.isSwapBlocked(System.currentTimeMillis())) {
                                 if (!mc.player.getInventory().getStack(value).isOf(Items.TOTEM_OF_UNDYING)) {
                                    if (mc.player.getInventory().getStack(pendingValue).isOf(Items.TOTEM_OF_UNDYING)) {
                                       if (!creativeMode || mc.currentScreen instanceof InventoryScreen) {
                                          boolean var7x = InventoryStackUtilsHandle.canFlagInventoryStackObject(this);
                                          if (var7x || InventoryStackUtilsHandle.canFlagForObject(this)) {
                                             try {
                                                this.sendSwapAndClose(syncId, autoInventoryLimit, integer);
                                             } finally {
                                                if (!var7x) {
                                                   InventoryStackUtilsHandle.handle(this);
                                                }
                                             }
                                          }
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            };
            this.pendingClickAction = action;
            ExtUtils.handleExt(action, pendingFlag);
            return true;
         }
      }
   }

   static boolean hasHealthDropped(float autoInventoryValue, float autoInventoryValue2) {
      return autoInventoryValue <= autoInventoryValue2 - 1.0F;
   }

   private static int containerToInventorySlot(int autoInventoryLimit) {
      return autoInventoryLimit >= 36 && autoInventoryLimit <= 44 ? autoInventoryLimit - 36 : (autoInventoryLimit == 45 ? 40 : autoInventoryLimit);
   }

   private void sendPredictedSwapClick(int stackSwapValue, int autoInventoryLimit, int integer, String stackSwapText) {
      ScreenHandler currentScreenHandler = mc.player.currentScreenHandler;
      if (stackSwapValue == currentScreenHandler.syncId && mc.getNetworkHandler() != null) {
         int containerToInventorySlot2 = containerToInventorySlot(autoInventoryLimit);
         int slotLimit = integer == 40 ? 40 : integer;
         int slot = inventoryToHandlerSlot(slotLimit);
         PlayerInventory inventory = mc.player.getInventory();
         ItemStack copy = inventory.getStack(containerToInventorySlot2).copy();
         ItemStack copy2 = inventory.getStack(slotLimit).copy();
         int atomic_integer_array = SLOT_UPDATE_COUNTS.get(autoInventoryLimit);
         int atomic_integer_array2 = SLOT_UPDATE_COUNTS.get(slot);
         var arrayList = new ArrayList(currentScreenHandler.slots.size());

         for (Slot slot2 : currentScreenHandler.slots) {
            arrayList.add(slot2.getStack().copy());
         }

         currentScreenHandler.onSlotClick(autoInventoryLimit, integer, SlotActionType.SWAP, mc.player);
         ComponentHasher componentHasher = mc.getNetworkHandler().getComponentHasher();
         var int2ObjectOpenHashMap = new Int2ObjectOpenHashMap();

         for (int i = 0; i < arrayList.size(); i++) {
            ItemStack stack = currentScreenHandler.slots.get(i).getStack();
            if (!ItemStack.areEqual((ItemStack)arrayList.get(i), stack)) {
               int2ObjectOpenHashMap.put(i, ItemStackHash.fromItemStack(stack, componentHasher));
            }
         }

         int2ObjectOpenHashMap.put(autoInventoryLimit, ItemStackHash.fromItemStack((ItemStack)arrayList.get(autoInventoryLimit), componentHasher));
         mc.getNetworkHandler()
            .sendPacket(
               new ClickSlotC2SPacket(
                  stackSwapValue,
                  currentScreenHandler.getRevision(),
                  (short)autoInventoryLimit,
                  (byte)integer,
                  SlotActionType.SWAP,
                  int2ObjectOpenHashMap,
                  ItemStackHash.fromItemStack(currentScreenHandler.getCursorStack(), componentHasher)
               )
            );
         this.pendingSwap = new AutoInventoryTotem.PendingSwap(System.currentTimeMillis(), stackSwapText, containerToInventorySlot2, slotLimit, autoInventoryLimit, slot, copy, copy2, atomic_integer_array, atomic_integer_array2, fullSyncCount);
         this.lastClickSentMs = this.pendingSwap.sentMs();
         pendingSwapSourceSlot = containerToInventorySlot2;
         pendingSwapTargetSlot = slotLimit;
         if (slotLimit == 40) {
            this.offhandSwapInFlight = true;
            this.lastSwapSentMs = this.pendingSwap.sentMs();
         }
      }
   }

   static int getExternalControlMode(boolean autoInventoryFlag, BooleanSupplier booleanSupplier, BooleanSupplier booleanSupplier2) {
      if (autoInventoryFlag && booleanSupplier != null && !booleanSupplier.getAsBoolean()) {
         return booleanSupplier2 != null && booleanSupplier2.getAsBoolean() ? 1 : 0;
      } else {
         return 2;
      }
   }

   private void sendChatMessage(String messageLiteralText) {
      if (mc.player != null) {
         mc.player.sendMessage(Text.literal(messageLiteralText), false);
      }
   }

   static int getReactionMode(boolean autoInventoryFlag, boolean autoInventoryFlag2, boolean autoInventoryFlag3) {
      if (!autoInventoryFlag && !autoInventoryFlag2) {
         return autoInventoryFlag3 ? 1 : 2;
      } else {
         return 0;
      }
   }

   public static synchronized void releaseExternalControl(Object obj) {
      if (obj != null && externalOwner == obj) {
         externalOwner = null;
         externalPrimarySupplier = null;
         externalSecondarySupplier = null;
         if (ModuleRegistry.getModule(AutoInventoryTotem.class) instanceof AutoInventoryTotem autoInventoryTotem && !autoInventoryTotem.isEnabled()) {
            ExtUtils.canActionFlag(autoInventoryTotem.pendingClickAction);
            autoInventoryTotem.resetState();
            InventoryStackUtilsHandle.handle(autoInventoryTotem);
         }
      }
   }

   @Override
   public void onEnable() {
      this.resetState();
   }

   private boolean recordReactionTicks() {
      if (!this.reactionNotRecorded) {
         return false;
      } else {
         this.reactionNotRecorded = false;
         this.reactionHistory.recordDelay(this.tickCount - this.popStartTick);
         return true;
      }
   }

   public static void requestHandSwapWindow(Object obj, int swapHandBound) {
      if (isExternalOwner(obj)) {
         if (ModuleRegistry.getModule(AutoInventoryTotem.class) instanceof AutoInventoryTotem autoInventoryTotem) {
            autoInventoryTotem.handSwapUntilTick = autoInventoryTotem.tickCount + Math.max(0, swapHandBound);
         }
      }
   }

   private int getMinReactionTicks() {
      return !this.humanize.isEnabled() ? 1 : Math.max(1, (this.minReaction.getValue() + 49) / 50);
   }

   private static AutoInventoryTotem getActiveInstance() {
      return !(ModuleRegistry.getModule(AutoInventoryTotem.class) instanceof AutoInventoryTotem autoInventoryTotem && (autoInventoryTotem.isEnabled() || externalOwner != null))
         ? null
         : autoInventoryTotem;
   }

   private void closeLegitScreen() {
      MouseInventoryScreen mouseInventoryScreen = this.mouseInventoryScreen;
      this.clearLegitScreen();
      if (mouseInventoryScreen != null
         && mc.player != null
         && mc.currentScreen == mouseInventoryScreen
         && mc.player.currentScreenHandler.getCursorStack().isEmpty()) {
         mc.player.closeHandledScreen();
      }
   }

   private long getSwapSpacingMs() {
      return this.swapSpacingTicks.getValue() * 50L;
   }

   private int getReserveHotbarSlot() {
      return this.totemSlot.getValue() - 1;
   }

   @Override
   public void onDisable() {
      if (externalOwner == null) {
         ExtUtils.canActionFlag(this.pendingClickAction);
      }

      this.resetState();
      this.closeLegitScreen();
      InventoryStackUtilsHandle.handle(this);
   }

   private void restoreSelectedSlot() {
      if (this.handSwapStage == 1 && this.originalSelectedSlot >= 0 && this.originalSelectedSlot != this.handSwapTotemSlot && mc.player != null) {
         InventorySelectedUtils.handleSelectedSlot(this.originalSelectedSlot);
      }

      this.handSwapStage = 0;
      handSwapSlotActive = false;
      this.originalSelectedSlot = -1;
      this.handSwapTotemSlot = -1;
   }

   private void onHandSwapFinished(long undyingInventoryValue, boolean undyingInventoryFlag) {
      this.restoreSelectedSlot();
      if (undyingInventoryFlag) {
         this.nextActionTimeMs = undyingInventoryValue + this.getSwapSpacingMs();
      } else {
         int undyingStack = this.findTotemForOffhand();
         if (undyingStack != -1 && !mc.player.getInventory().getStack(40).isOf(Items.TOTEM_OF_UNDYING)) {
            this.tryScheduleSwap(undyingStack, 40, undyingInventoryValue);
         }
      }
   }

   private int getMaxReactionTicks() {
      return !this.humanize.isEnabled() ? 1 : Math.max(this.getMinReactionTicks() + 4 - 1, (this.maxReaction.getValue() + 25) / 50);
   }

   private boolean isLegitModeActive() {
      return this.legitMode.isEnabled() && externalOwner == null && mc.player != null && !mc.player.isInCreativeMode();
   }

   public static int getReserveSlotIfEnabled() {
      AutoInventoryTotem autoInventoryTotem = getActiveInstance();
      if (autoInventoryTotem != null && autoInventoryTotem.keepReserveTotem.isEnabled()) {
         int integer = autoInventoryTotem.getReserveHotbarSlot();
         return integer >= 0 && integer < 9 ? integer : -1;
      } else {
         return -1;
      }
   }

   private void tickLegitScreenClose() {
      if (this.mouseInventoryScreen != null) {
         if (this.pendingSwap == null && !ExtUtils.isAction() && !this.needsTotemMove()) {
            if (this.screenCloseTick < 0) {
               this.screenCloseTick = this.tickCount + 1 + this.random.nextInt(3);
            } else if (this.tickCount >= this.screenCloseTick) {
               this.closeLegitScreen();
            }
         } else {
            this.screenCloseTick = -1;
         }
      }
   }

   private void advanceHandSwap(long selectedSlotValue) {
      if (this.handSwapStage == 1) {
         if (this.originalSelectedSlot >= 0 && this.originalSelectedSlot != this.handSwapTotemSlot) {
            InventorySelectedUtils.handleSelectedSlot(this.originalSelectedSlot);
         }

         handSwapSlotActive = false;
         this.handSwapStage = 2;
      }

      if (mc.player.getInventory().getStack(40).isOf(Items.TOTEM_OF_UNDYING)) {
         this.onHandSwapFinished(selectedSlotValue, true);
      } else if (selectedSlotValue - this.handSwapSentMs > 600L) {
         this.onHandSwapFinished(selectedSlotValue, false);
      }
   }

   public static boolean isExternalOwner(Object obj) {
      return obj != null && externalOwner == obj;
   }

   public void forwardTick() {
      if (!this.isEnabled() && externalOwner != null) {
         this.onReset();
      }
   }

   private boolean isLegitScreenReady() {
      if (this.mouseInventoryScreen == null) {
         if (mc.currentScreen instanceof InventoryScreen) {
            return true;
         } else {
            this.openLegitInventoryScreen(false);
            return false;
         }
      } else {
         return mc.currentScreen == this.mouseInventoryScreen && this.tickCount >= this.screenReadyTick;
      }
   }

   private boolean isSwapBlocked(long spectatorPlayerValue) {
      if (mc.player != null && !mc.player.isSpectator()) {
         long lastRespawnMs3 = this.lastRespawnMs;
         long lastFullSyncMs2 = this.lastFullSyncMs;
         return lastRespawnMs3 > 0L && spectatorPlayerValue - lastRespawnMs3 < 1500L || lastFullSyncMs2 > 0L && spectatorPlayerValue - lastFullSyncMs2 < 250L;
      } else {
         return true;
      }
   }

   boolean isHandSwapWindowOpen() {
      return externalOwner != null && this.handSwapUntilTick != Integer.MIN_VALUE && this.tickCount <= this.handSwapUntilTick;
   }

   public static synchronized boolean acquireExternalControl(Object object2, BooleanSupplier booleanSupplier, BooleanSupplier booleanSupplier4) {
      if (object2 == null) {
         return false;
      } else if (ModuleRegistry.getModule(AutoInventoryTotem.class) instanceof AutoInventoryTotem autoInventoryTotem) {
         if (AutoYourUtils.isContainsKey(autoInventoryTotem)) {
            return false;
         } else {
            Object obj = externalOwner;
            if (obj != null && obj != object2) {
               return false;
            } else {
               externalPrimarySupplier = booleanSupplier;
               externalSecondarySupplier = booleanSupplier4;
               if (obj == null) {
                  if (!autoInventoryTotem.isEnabled()) {
                     autoInventoryTotem.resetState();
                  }

                  externalOwner = object2;
               }

               return true;
            }
         }
      } else {
         return false;
      }
   }

   private void clearPendingSwap() {
      this.pendingSwap = null;
      pendingSwapSourceSlot = -1;
      pendingSwapTargetSlot = -1;
   }

   @Override
   public void onReset() {
      this.tickCount++;
      if (mc.player != null && mc.interactionManager != null) {
         if (this.mouseInventoryScreen != null && mc.currentScreen != this.mouseInventoryScreen) {
            this.clearLegitScreen();
         }

         if (this.mouseInventoryScreen != null && this.tickCount - this.screenOpenedTick > 40) {
            this.screenCooldownUntilTick = this.tickCount + 10;
            this.closeLegitScreen();
         }

         if (mc.player.currentScreenHandler == mc.player.playerScreenHandler
            && mc.player.currentScreenHandler.getCursorStack().isEmpty()) {
            long timeMillis = System.currentTimeMillis();
            if (mc.player.isAlive() && !(mc.currentScreen instanceof DeathScreen)) {
               if (this.respawnPending) {
                  this.respawnPending = false;
                  this.lastRespawnMs = timeMillis;
               }

               for (int i = 0; i < 9; i++) {
                  ItemStack stack = mc.player.getInventory().getStack(i);
                  if (stack.isOf(Items.TOTEM_OF_UNDYING)) {
                     this.totemSlotMemory[i] = true;
                  } else if (!stack.isEmpty()) {
                     this.totemSlotMemory[i] = false;
                  }
               }

               this.checkPendingSwapResult(timeMillis);
               if (!ExtUtils.isAction()) {
                  this.pendingTargetSlot = -1;
               }

               this.tickLegitScreenClose();
               if (this.handSwapStage != 0) {
                  this.advanceHandSwap(timeMillis);
               } else {
                  boolean itemStack = mc.player.getInventory().getStack(40).isOf(Items.TOTEM_OF_UNDYING);
                  boolean offhandHasTotem2 = this.offhandHasTotem;
                  this.offhandHasTotem = itemStack;
                  if (itemStack) {
                     this.reequipPending = false;
                     this.offhandSwapInFlight = false;
                  } else if ((!this.offhandSwapInFlight || timeMillis - this.lastSwapSentMs >= 400L) && this.pendingTargetSlot != 40) {
                     this.offhandSwapInFlight = false;
                     if (this.reequipPending) {
                        if (this.panicReEquip.isEnabled() && this.isHandSwapWindowOpen() && this.reequipAtTick > this.tickCount) {
                           this.reequipAtTick = clampReactionTick(this.reequipAtTick, this.popStartTick, this.tickCount, this.reactionHistory.getMinimumExtraTicks());
                        }
                     } else {
                        this.reequipPending = true;
                        boolean iIiFlag = this.panicReEquip.isEnabled() && this.lastPopMs > 0L && timeMillis - this.lastPopMs < 600L;
                        boolean iIiFlag2 = this.panicReEquip.isEnabled() && this.isHandSwapWindowOpen();
                        boolean iIiFlag3 = this.offHandViaHandSwap.isEnabled() && !this.isLegitModeActive() && this.findHotbarTotemSlot() == -1;
                        this.lastPopMs = timeMillis;
                        this.popStartTick = this.tickCount;
                        this.poppedWithoutMainHandTotem = offhandHasTotem2 && !mc.player.getMainHandStack().isOf(Items.TOTEM_OF_UNDYING);
                        this.reactionNotRecorded = this.poppedWithoutMainHandTotem;
                        this.lastTotalHealth = mc.player.getHealth() + mc.player.getAbsorptionAmount();
                        int iIiValue = getReactionMode(iIiFlag, iIiFlag2, iIiFlag3);
                        if (iIiValue == 0) {
                           this.reequipAtTick = this.tickCount + this.reactionHistory.getMinimumExtraTicks();
                        } else if (iIiValue == 1) {
                           this.reequipAtTick = this.tickCount;
                        } else {
                           this.reequipAtTick = this.tickCount + this.reactionHistory.pickVariedDelay(this.getMinReactionTicks(), this.getMaxReactionTicks());
                        }

                        if (this.isLegitModeActive() && this.findTotemForOffhand() != -1) {
                           this.openLegitInventoryScreen(iIiValue == 0);
                        }
                     }

                     if (this.tickCount >= this.reequipAtTick || this.isTakingFollowUpDamage()) {
                        boolean iIiFlag4 = this.offHandViaHandSwap.isEnabled() && !this.isLegitModeActive();
                        if (iIiFlag4 && this.findHotbarTotemSlot() != -1 && this.lastClickSentMs > 0L && timeMillis - this.lastClickSentMs < 150L) {
                           return;
                        }

                        if (iIiFlag4 && this.tryHandSwapToOffhand(timeMillis)) {
                           this.recordReactionTicks();
                           this.reequipPending = false;
                           return;
                        }

                        int undyingStack = this.findTotemForOffhand();
                        if (undyingStack != -1 && this.tryScheduleSwap(undyingStack, 40, timeMillis)) {
                           this.reequipPending = false;
                           return;
                        }
                     }
                  }

                  int externalControlMode = getExternalControlMode(externalOwner != null, externalPrimarySupplier, externalSecondarySupplier);
                  if (externalControlMode != 0) {
                     if (timeMillis >= this.nextActionTimeMs) {
                        if (this.keepReserveTotem.isEnabled()) {
                           int integer = this.getReserveHotbarSlot();
                           if (!mc.player.getInventory().getStack(integer).isOf(Items.TOTEM_OF_UNDYING)) {
                              int undyingInventory = this.findStorageTotemSlot();
                              if (undyingInventory != -1) {
                                 this.tryScheduleSwap(undyingInventory, integer, timeMillis);
                                 return;
                              }
                           }

                           if (externalControlMode != 2) {
                              return;
                           }

                           for (int j = 0; j < 9; j++) {
                              if (j != integer && this.totemSlotMemory[j] && mc.player.getInventory().getStack(j).isEmpty()) {
                                 int storageTotemSlot = this.findStorageTotemSlot();
                                 if (storageTotemSlot != -1) {
                                    this.tryScheduleSwap(storageTotemSlot, j, timeMillis);
                                    return;
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            } else {
               this.clearPendingSwap();
               this.closeLegitScreen();
               if (this.handSwapStage != 0) {
                  this.restoreSelectedSlot();
               }

               this.reequipPending = false;
               this.offhandSwapInFlight = false;
               this.reactionNotRecorded = false;
               this.offhandHasTotem = false;
               this.respawnPending = true;
            }
         }
      } else {
         this.reequipPending = false;
         this.mouseInventoryScreen = null;
         this.screenCloseTick = -1;
         InventoryStackUtilsHandle.handle(this);
      }
   }

   private void onSwapFailed(long autoInventoryValue, AutoInventoryTotem.PendingSwap pendingSwap, String autoInventoryText) {
      this.offhandSwapInFlight = false;
      this.nextActionTimeMs = Math.max(this.nextActionTimeMs, autoInventoryValue + 150L);
   }

   public void forwardOutgoingPacket(InventoryPlayer inventoryPlayer) {
      if (!this.isEnabled() && externalOwner != null) {
         this.onPacketSend(inventoryPlayer);
      }
   }

   private boolean isTakingFollowUpDamage() {
      if (this.panicReEquip.isEnabled() && mc.player != null) {
         float amount = mc.player.getHealth() + mc.player.getAbsorptionAmount();
         if (amount > this.lastTotalHealth) {
            this.lastTotalHealth = amount;
            return false;
         } else {
            return hasHealthDropped(amount, this.lastTotalHealth);
         }
      } else {
         return false;
      }
   }

   @Override
   protected void onPacketReceive(AutoVanish autoVanish) {
      if (autoVanish.packet instanceof InventoryS2CPacket inventoryS2CPacket) {
         if (inventoryS2CPacket.syncId() == 0) {
            fullSyncCount++;
            if (pendingSwapTargetSlot == -1) {
               this.lastFullSyncMs = System.currentTimeMillis();
            }
         }
      } else if (autoVanish.packet instanceof PlayerRespawnS2CPacket) {
         this.lastRespawnMs = System.currentTimeMillis();
      } else if (autoVanish.packet instanceof ScreenHandlerSlotUpdateS2CPacket screenHandlerSlotUpdateS2CPa) {
         int slot = screenHandlerSlotUpdateS2CPa.getSlot();
         if (screenHandlerSlotUpdateS2CPa.getSyncId() == 0) {
            if (slot >= 0 && slot < 46) {
               SLOT_UPDATE_COUNTS.incrementAndGet(slot);
            }

            String iiIText = slot == 45 ? "off-hand" : (slot >= 36 && slot <= 44 ? "hotbar " + (slot - 35) : "inv " + slot);
            lastSlotUpdateText = "SetSlot(" + iiIText + "=" + describeStack(screenHandlerSlotUpdateS2CPa.getStack()) + ")";
         } else if (screenHandlerSlotUpdateS2CPa.getSyncId() == -2) {
            int slot2 = inventoryToHandlerSlot(slot);
            if (slot2 >= 0 && slot2 < 46) {
               SLOT_UPDATE_COUNTS.incrementAndGet(slot2);
            }

            String iiIText2 = slot == 40 ? "off-hand" : (slot < 9 ? "hotbar " + (slot + 1) : "inv " + slot);
            lastSlotUpdateText = "SetSlot[-2](" + iiIText2 + "=" + describeStack(screenHandlerSlotUpdateS2CPa.getStack()) + ")";
         }
      } else if (autoVanish.packet instanceof SetPlayerInventoryS2CPacket setPlayerInventoryS2CPacket) {
         int slot3 = setPlayerInventoryS2CPacket.slot();
         int slot4 = inventoryToHandlerSlot(slot3);
         if (slot4 >= 0 && slot4 < 46) {
            SLOT_UPDATE_COUNTS.incrementAndGet(slot4);
         }

         if (slot3 == 40 || slot3 < 9) {
            lastSlotUpdateText = "SetPlayerInventory(" + (slot3 == 40 ? "off-hand" : "hotbar " + (slot3 + 1)) + "=" + describeStack(setPlayerInventoryS2CPacket.contents()) + ")";
         }
      }
   }

   private static int inventoryToHandlerSlot(int slotLimit) {
      if (slotLimit >= 0 && slotLimit < 9) {
         return 36 + slotLimit;
      } else {
         return slotLimit == 40 ? 45 : slotLimit;
      }
   }

   private int findTotemForOffhand() {
      int undyingInventory = this.findStorageTotemSlot();
      if (undyingInventory != -1) {
         return undyingInventory;
      } else {
         PlayerInventory inventory = mc.player.getInventory();
         int integer = this.getReserveHotbarSlot();

         for (int i = 0; i < 9; i++) {
            if (i != integer && inventory.getStack(i).isOf(Items.TOTEM_OF_UNDYING) && !isSlotInPendingSwap(i)) {
               return i;
            }
         }

         return inventory.getStack(integer).isOf(Items.TOTEM_OF_UNDYING) && !isSlotInPendingSwap(integer) ? integer : -1;
      }
   }

   private static int hotbarToHandlerSlot(int autoInventoryValue) {
      return autoInventoryValue < 9 ? 36 + autoInventoryValue : autoInventoryValue;
   }

   private void clearLegitScreen() {
      this.mouseInventoryScreen = null;
      this.screenCloseTick = -1;
      InventoryStackUtilsHandle.handle(this);
   }

   @Override
   protected void onPacketSend(InventoryPlayer inventoryPlayer) {
      if (mc.player != null) {
         var packet = inventoryPlayer.packet;
         boolean llIFlag = packet instanceof PlayerInteractEntityC2SPacket
            || packet instanceof PlayerInteractBlockC2SPacket
            || packet instanceof PlayerInteractItemC2SPacket
            || packet instanceof PlayerActionC2SPacket playerActionC2SPacket && playerActionC2SPacket.getAction() != Action.SWAP_ITEM_WITH_OFFHAND;
         if (llIFlag) {
            this.lastInteractAge = mc.player.age;
         }
      }
   }

   private int findStorageTotemSlot() {
      PlayerInventory inventory = mc.player.getInventory();

      for (int i = 9; i < 36; i++) {
         if (inventory.getStack(i).isOf(Items.TOTEM_OF_UNDYING)) {
            return i;
         }
      }

      return -1;
   }

   public AutoInventoryTotem() {
      super("Auto Inventory Totem", IconsPngType.COMBAT);
      this.keepReserveTotem = new BooleanSetting(
         "Reserve Slot",
         "Also keep a spare totem in a hotbar slot. It is refilled at once (even while the off-hand is waiting out its humanized reaction) and is what Safe Anchor holds in the main hand while detonating.",
         true
      );
      this.totemSlot = new IntegerSetting(
         "Totem Slot",
         "Reserve hotbar slot (1-9). Keep it equal to Safe Anchor's Totem Slot so the refill lands in the hand the anchor combo detonates with.",
         9,
         1,
         9
      );
      this.humanize = new BooleanSetting(
         "Humanize",
         "Wait a randomized human-like delay after a pop before re-equipping the off-hand, so TotemGuard's AutoTotem checks (impossible speed / low standard deviation / low outliers) don't flag. Off = re-equip as fast as possible (flags TotemGuard).",
         true
      );
      this.minReaction = new IntegerSetting(
         "Min Reaction",
         "Fastest re-equip delay in ms. The module acts on whole ticks (50 ms) and the server only ever sees the tick, so this rounds UP to the first tick it allows: 45 = the very next tick.",
         45,
         20,
         500
      );
      this.maxReaction = new IntegerSetting(
         "Max Reaction",
         "Slowest re-equip delay in ms \u2014 your worst-case unprotected window after an ISOLATED pop (combos and blast spam are covered by Panic Re-equip). Rounded to ticks. Min..Max is the set of 50 ms buckets a reaction can land in; consecutive reactions are kept two buckets apart so TotemGuard's AutoTotemB (the punishable one) never counts a pair, and the buckets are rotated so its AutoTotemE/C alerts stay quiet too. That needs at least 4 buckets (Max \u2265 Min + 150) \u2014 the module widens a tighter band to 4 by itself. 200 = 4 buckets, 300 = 6: both clean in the model, 6 is more random. Lower only if you'd rather survive isolated pops than stay quiet.",
         300,
         60,
         900
      );
      this.swapSpacingTicks = new IntegerSetting(
         "Swap Spacing",
         "Whole ticks between consecutive inventory clicks. The module acts on tick boundaries, so this is the only spacing that means anything; clicks are also serialized (one unanswered at a time).",
         1,
         0,
         10
      );
      this.clicksOnlyWhenIdle = new BooleanSetting(
         "Clicks Only When Idle",
         "Send inventory CLICKS (hotbar restocks from storage, and the off-hand click when no hotbar totem is left) only once YOU have been still for a few ticks \u2014 no movement, jump, sneak or sprint, the state a real inventory click happens in \u2014 and never take your keys away to get there. Hand swaps are never held back. Off = Move Bypass may freeze your input for the few ticks a click needs.",
         false
      );
      this.moveBypass = new BooleanSetting(
         "Move Bypass",
         "When a click is needed while you move, freeze your movement keys (and sneak/sprint) so you coast to a stop, and send the click only after the server has seen ~4 still ticks \u2014 the exact packets and motion of a player opening their inventory mid-run. Every click sent sooner than that was silently cancelled by the server (anticheat-test.com, DonutSMP): the totem never moved and the hotbar ran dry. Costs up to ~250 ms of movement per click; off = wait until you stand still.",
         true
      );
      this.reserve = new BooleanSetting(
         "Close After Swap",
         "Silence TotemGuard's AutoTotemF alert (\"invalid interaction\") by sending a bare inventory-close packet right after each swap, mimicking a human closing their inventory. This does NOT cause the ghost death it was once blamed for (that was the reaction window). On by default. The close packet is the historical desync suspect.",
         true
      );
      this.panicReEquip = new BooleanSetting(
         "Panic Re-equip",
         "SURVIVAL override: if you take another hit while the off-hand is still empty (a combo is underway), or a totem pops less than 0.6 s after the previous pop (blast spam), re-equip NEXT TICK instead of finishing the humanized wait. Isolated pops keep the full delay so AutoTotemB stays clean; only genuine follow-up damage skips it \u2014 exactly what a human does when getting comboed.",
         true
      );
      this.offHandViaHandSwap = new BooleanSetting(
         "Off-hand Via Hand Swap",
         "GHOST FIX: refill the off-hand the way a player pressing F does \u2014 select the hotbar totem (the reserve), send the vanilla hand-swap, switch back next tick \u2014 instead of an inventory click. The client predicts nothing on that path (the server sends the result), so it can never show a totem the server doesn't have, and the server has never rejected it where it did reject off-hand clicks. Falls back to the click path when no hotbar totem is available. The reserve is then restocked from storage by a server-confirmed click.",
         true
      );
      this.random = new Random();
      this.handSwapUntilTick = Integer.MIN_VALUE;
      this.reactionHistory = new RecentDelayRandomizer(this.random);
      this.lastRespawnMs = -1L;
      this.lastFullSyncMs = -1L;
      this.lastClickSentMs = -1L;
      this.originalSelectedSlot = -1;
      this.handSwapTotemSlot = -1;
      this.screenCloseTick = -1;
      this.screenCooldownUntilTick = Integer.MIN_VALUE;
      this.lastInteractAge = Integer.MIN_VALUE;
      this.pendingTargetSlot = -1;
      this.totemSlotMemory = new boolean[9];
      this.addSettings(
         new ConfigSetting[]{
            this.legitMode,
            this.keepReserveTotem,
            this.totemSlot,
            this.humanize,
            this.minReaction,
            this.maxReaction,
            this.swapSpacingTicks,
            this.clicksOnlyWhenIdle,
            this.moveBypass,
            this.reserve,
            this.panicReEquip,
            this.offHandViaHandSwap
         }
      );
   }

   public static synchronized boolean canFlag(Object obj, BooleanSupplier booleanSupplier) {
      return acquireExternalControl(obj, booleanSupplier, null);
   }

   private boolean needsTotemMove() {
      PlayerInventory inventory = mc.player.getInventory();
      if (!inventory.getStack(40).isOf(Items.TOTEM_OF_UNDYING) && this.findTotemForOffhand() != -1) {
         return true;
      } else if (this.keepReserveTotem.isEnabled() && this.findStorageTotemSlot() != -1) {
         if (!inventory.getStack(this.getReserveHotbarSlot()).isOf(Items.TOTEM_OF_UNDYING)) {
            return true;
         } else {
            for (int i = 0; i < 9; i++) {
               if (this.totemSlotMemory[i] && inventory.getStack(i).isEmpty()) {
                  return true;
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   static int clampReactionTick(int autoInventoryBound, int autoInventoryValue, int aggroPreemptBound, int autoInventoryValue2) {
      return Math.min(autoInventoryBound, Math.max(aggroPreemptBound, autoInventoryValue + autoInventoryValue2));
   }

   private record PendingSwap(
      long sentMs,
      String kind,
      int srcInv,
      int tgtInv,
      int srcHandlerSlot,
      int tgtHandlerSlot,
      ItemStack srcBefore,
      ItemStack tgtBefore,
      int srcSeqAtSend,
      int tgtSeqAtSend,
      long fullSyncSeqAtSend
   ) {
   }
}
