package com.corz.client.categories.combat;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.entity.EntityPlayerUtilsLiving;
import com.corz.client.entity.EntityPlayerUtilsLiving.Options;
import com.corz.client.entity.ThreadClientHandlerConst;
import com.corz.client.entity.ThreadClientHandlerConst.Result;
import com.corz.client.inventory.EntityPlayerUtilsGliding;
import com.corz.client.inventory.InventoryStackUtilsEmpty;
import com.corz.client.inventory.PlayerInventoryApi;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import com.corz.client.utils.InventoryPlayer;
import com.corz.client.utils.InventoryPlayerUtils;
import ai.auth.Module;
import com.corz.client.utils.DensityBreachType;
import com.corz.client.utils.IconsPngType;
import com.corz.client.utils.SwapPreferenceData;

public class MaceBomber extends Module implements PlayerInventoryApi, ThreadClientHandlerConst {
   private final DoubleSetting minFallHeightSetting = new DoubleSetting("Height", "Minimum fall distance before it smashes", 2.0, 2.0, 30.0, 1.0);
   private final BooleanSetting autoStunSlam;
   private final BooleanSetting switchBack;
   private static final Options INVISIBLES_TEAMMATES = Options.INSTANCE.getInvisiblesTeammates(true);
   private final EnumSetting preferredMaceSetting = new EnumSetting(
      "Preferred Mace",
      "Which enchanted mace to pick if you have one; falls back to any mace",
      DensityBreachType.DENSITY.getText(),
      DensityBreachType.getTextDensityText()
   );

   @Override
   public Result resolveAttackIntercept(Object obj, Entity entity) {
      return InventoryPlayerUtils.getPassPlan(obj, entity);
   }

   @Override
   public int getHandlerPriority() {
      return 100;
   }

   @Override
   public boolean isBreachOwnerActive() {
      throw new AbstractMethodError();
   }

   private LivingEntity getTargetedLivingEntity() {
      if (mc.crosshairTarget instanceof EntityHitResult entityHitResult) {
         Entity entity = entityHitResult.getEntity();
         return entity instanceof LivingEntity livingEntity && EntityPlayerUtilsLiving.canSpectatorDead(entity, INVISIBLES_TEAMMATES) ? livingEntity : null;
      } else {
         return null;
      }
   }

   public MaceBomber() {
      super("Auto Mace", IconsPngType.COMBAT);
      this.autoStunSlam = new BooleanSetting("Auto Stun Slam", "Break a blocking target's shield with a hotbar axe first so the mace hit lands", false);
      this.switchBack = new BooleanSetting("Switch Back", "Return to the previous slot after the smash", false);
      this.addSettings(new ConfigSetting[]{this.minFallHeightSetting, this.preferredMaceSetting, this.autoStunSlam, this.switchBack});
   }

   @Override
   public boolean isMaceSmashReady() {
      return this.isEnabled()
         && mc.player != null
         && EntityPlayerUtilsGliding.canGlidingGround(this.minFallHeightSetting.getValue())
         && InventoryStackUtilsEmpty.getDensityInstance(this.getPreferredMaceType()) >= 0;
   }

   @Override
   public SwapPreferenceData getSwapPreference() {
      return new SwapPreferenceData(
         true, true, this.minFallHeightSetting.getValue(), this.getPreferredMaceType(), this.autoStunSlam.isEnabled(), false, false, this.switchBack.isEnabled()
      );
   }

   @Override
   protected void onDisable() {
      InventoryPlayerUtils.handleApiSelected(this);
   }

   @Override
   protected void onReset() {
      InventoryPlayerUtils.updateApiDead();
      if (mc.player != null && mc.world != null && mc.interactionManager != null && mc.currentScreen == null) {
         double iIiValue = this.minFallHeightSetting.getValue();
         if (EntityPlayerUtilsGliding.canGlidingGround(iIiValue)) {
            LivingEntity entity = this.getTargetedLivingEntity();
            if (entity != null && !InventoryPlayerUtils.canTime(entity) && InventoryPlayerUtils.isLivingEntity()) {
               DensityBreachType preferredMaceType = this.getPreferredMaceType();
               int densityInstance = InventoryStackUtilsEmpty.getDensityInstance(preferredMaceType);
               if (densityInstance >= 0) {
                  if (EntityPlayerUtilsGliding.canPlayerInstance(entity)) {
                     if (this.autoStunSlam.isEnabled()) {
                        int instanceSelected = InventoryStackUtilsEmpty.getInstanceSelected();
                        if (instanceSelected >= 0
                           && EntityPlayerUtilsGliding.canInstancePlayer(entity, InventoryStackUtilsEmpty.getEmpty(instanceSelected))
                           && EntityPlayerUtilsGliding.canFlag(entity, 2)) {
                           InventoryPlayerUtils.canFlag(this, entity, instanceSelected, preferredMaceType, iIiValue, this.switchBack.isEnabled());
                        }
                     }
                  } else if (EntityPlayerUtilsGliding.canInstancePlayer(entity, InventoryStackUtilsEmpty.getEmpty(densityInstance))
                     && EntityPlayerUtilsGliding.canFlag(entity, 1)) {
                     InventoryPlayerUtils.canValidHotbar(this, entity, densityInstance, this.switchBack.isEnabled());
                  }
               }
            }
         }
      }
   }

   private DensityBreachType getPreferredMaceType() {
      return DensityBreachType.getDensityText(this.preferredMaceSetting.getCurrentOption());
   }

   @Override
   protected void onPacketSend(InventoryPlayer inventoryPlayer) {
      InventoryPlayerUtils.handleTimeLiving(inventoryPlayer);
   }

   @Override
   public int getSwapPriority() {
      return 3;
   }
}
