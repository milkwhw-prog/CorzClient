package com.corz.client.world;

import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.HandleTime;

public interface ChargeAmountApi {
   default boolean canSelectedInventory(int selectedInventoryValue) {
      return this.canSelectedInventoryForInt(selectedInventoryValue);
   }

   float getAmount();

   default void update() {
   }

   default float[] getEyeAction(Vec3d vec) {
      return null;
   }

   boolean isLedgerHandswap();

   boolean isFlag();

   boolean canAmountAbsorption(float absorptionValue, float detonateUnsafeValue, boolean absorptionFlag);

   HandleTime getSupplier();

   double getAmountDouble();

   default boolean isFlag2() {
      return false;
   }

   float getAmountFloat();

   void handleBlock(float blockValue, float amountFloat);

   long getTime();

   boolean canSelectedInventoryForInt(int selectedInventoryValue);

   boolean isSprintSneak();

   void handleEyeAction(Vec3d vec, boolean eyeActionFlag);

   long getTimeLong();

   double getAmount4();
}
