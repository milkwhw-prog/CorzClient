package com.corz.client.world;

import com.corz.client.inventory.BlockPosUtilsRespawn;
import java.util.function.Supplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.BotActionType;
import com.corz.client.utils.HotbarSlotSwitcher;
import com.corz.client.utils.HandswapGate;
import com.corz.client.utils.TickActionLedger;
import com.corz.client.utils.TrackFlick;
import com.corz.client.utils.HandleTime;
import com.corz.client.utils.RefBareData;

public final class ClientInstance implements ChargeAmountApi {
   private final Supplier<HandleTime> supplier;
   private float refAmount;
   private float clientAmount;
   private RefBareData absorptionRefBareData = RefBareData.INSTANCE;
   private boolean absorptionActive;
   private long instanceTimeMs;
   private final HotbarSlotSwitcher inventorySelected;
   private final TrackFlick trackFlick;
   private final HandswapGate ledgerHandswap;
   private long selectedInventoryValue;
   private float absorptionAmount;
   private boolean clientActive;
   private final TickActionLedger sprintSneak;
   private float absorptionPitch;
   private long clientValue = Long.MIN_VALUE;
   private float absorptionYaw;

   @Override
   public float getAmount() {
      return this.trackFlick.getCurrentYaw();
   }

   public boolean isTime() {
      return this.clientValue == this.selectedInventoryValue;
   }

   @Override
   public double getAmount4() {
      return this.trackFlick.getLastYawChangeDegrees();
   }

   @Override
   public boolean isFlag2() {
      return true;
   }

   @Override
   public boolean isFlag() {
      return this.trackFlick.didRotationMatchCommit();
   }

   public void handleInstance(long clientValue, long playerValue, float clientBound, boolean clientFlag) {
      this.handleTimeInstance(clientValue, playerValue, clientBound, clientFlag, RefBareData.INSTANCE);
   }

   @Override
   public double getAmountDouble() {
      return BlockPosUtilsRespawn.getRangeInteraction();
   }

   @Override
   public boolean isLedgerHandswap() {
      return this.ledgerHandswap.isSwitchAllowedWithTeleport();
   }

   @Override
   public boolean canAmountAbsorption(float absorptionLimit, float absorptionValue, boolean absorptionFlag) {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (player == null) {
         return false;
      } else {
         float full = player.getHealth() + player.getAbsorptionAmount();
         boolean time2 = this.isTime();
         if (this.absorptionRefBareData.ref()) {
            return this.canAmountFlag(absorptionLimit, absorptionValue, full, absorptionFlag, time2);
         } else if (time2 && this.absorptionRefBareData.canRef(this.absorptionYaw, this.absorptionPitch, this.clientActive, this.refAmount, absorptionLimit, absorptionValue, full)) {
            return true;
         } else {
            boolean absorptionFlag3 = absorptionLimit > 0.0F && absorptionLimit >= full - 1.0F;
            return !absorptionFlag3 || this.absorptionActive && absorptionFlag
               ? absorptionValue <= this.absorptionAmount && (!this.absorptionRefBareData.on() || time2 && RefBareData.canFlag(this.absorptionYaw, this.absorptionPitch, this.clientActive, absorptionValue))
               : false;
         }
      }
   }

   @Override
   public boolean canSelectedInventory(int selectedInventoryValue) {
      return this.inventorySelected.trySelectSlot(selectedInventoryValue, this.selectedInventoryValue);
   }

   @Override
   public long getTimeLong() {
      return this.instanceTimeMs;
   }

   public boolean isAbsorptionActive() {
      return this.absorptionActive;
   }

   @Override
   public long getTime() {
      return this.selectedInventoryValue;
   }

   @Override
   public void handleEyeAction(Vec3d vec, boolean eyeActionFlag) {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (player != null && vec != null) {
         float[] wrapDegrees = MathClampUtils.getWrapDegrees(player.getEyePos(), vec);
         this.trackFlick.requestSmoothedRotation(BotActionType.BLOCK, wrapDegrees[0], wrapDegrees[1], TrackFlick.Speed.ACTION, 60);
      }
   }

   @Override
   public boolean canSelectedInventoryForInt(int selectedInventoryValue) {
      return this.inventorySelected.requestSlot(selectedInventoryValue, false);
   }

   @Override
   public void handleBlock(float blockValue, float routedPartialValue) {
      this.trackFlick.requestDirectRotation(BotActionType.BLOCK, blockValue, routedPartialValue, 60);
   }

   public float getAbsorptionAmount() {
      return this.absorptionAmount;
   }

   @Override
   public HandleTime getSupplier() {
      return this.supplier == null ? null : this.supplier.get();
   }

   public RefBareData getAbsorptionRefBareData() {
      return this.absorptionRefBareData;
   }

   @Override
   public float[] getEyeAction(Vec3d vec) {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (player != null && vec != null) {
         float[] wrapDegrees = MathClampUtils.getWrapDegrees(player.getEyePos(), vec);
         return this.trackFlick.lockTrackRotation(BotActionType.BLOCK, wrapDegrees[0], wrapDegrees[1], TrackFlick.Speed.ACTION, 60, 8.0F);
      } else {
         return null;
      }
   }

   @Override
   public boolean isSprintSneak() {
      return this.sprintSneak.isSneak();
   }

   public ClientInstance(TrackFlick trackFlick, HandswapGate ledgerHandswap, TickActionLedger sprintSneak, HotbarSlotSwitcher inventorySelected, Supplier<HandleTime> supplier) {
      this.refAmount = Float.NaN;
      this.clientAmount = Float.NaN;
      this.trackFlick = trackFlick;
      this.ledgerHandswap = ledgerHandswap;
      this.sprintSneak = sprintSneak;
      this.inventorySelected = inventorySelected;
      this.supplier = supplier;
   }

   @Override
   public void update() {
      this.trackFlick.confirmLockedTrack();
   }

   public void handleTimeInstance(long time, long playerValue, float clientBound, boolean flag, RefBareData refBareData) {
      this.selectedInventoryValue = time;
      this.instanceTimeMs = playerValue;
      this.absorptionAmount = Float.isFinite(clientBound) ? Math.max(0.0F, clientBound) : 0.0F;
      this.absorptionActive = flag;
      this.absorptionRefBareData = refBareData == null ? RefBareData.INSTANCE : refBareData;
   }

   public void handleAmountTime(float amount, float difficultyInstance, boolean flag, float calibration, float amount4) {
      this.clientValue = this.selectedInventoryValue;
      this.absorptionYaw = amount;
      this.absorptionPitch = difficultyInstance;
      this.clientActive = flag;
      this.refAmount = calibration;
      this.clientAmount = amount4;
   }

   public void handle(float clientValue, float difficultyInstance, boolean clientFlag, float calibration) {
      this.handleAmountTime(clientValue, difficultyInstance, clientFlag, calibration, Float.NaN);
   }

   private boolean canAmountFlag(float clientValue, float clientBound, float full, boolean clientFlag, boolean clientFlag2) {
      if (!clientFlag2) {
         return this.absorptionRefBareData.canBare(clientValue, full, this.absorptionRefBareData.canFlag(clientFlag));
      } else {
         float clientValue3 = Float.isFinite(this.clientAmount) ? Math.max(clientBound, this.clientAmount) : clientValue;
         return this.absorptionRefBareData.canBare(this.absorptionRefBareData.getBareRef(clientValue, clientValue3), full, this.absorptionRefBareData.canBareBehind(clientFlag, this.clientActive))
            && this.absorptionRefBareData.canRef(this.absorptionYaw, this.clientActive, clientValue, clientValue3, false);
      }
   }

   @Override
   public float getAmountFloat() {
      return this.trackFlick.getCurrentPitch();
   }
}
