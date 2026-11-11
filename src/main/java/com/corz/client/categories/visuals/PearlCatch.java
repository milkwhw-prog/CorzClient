package com.corz.client.categories.visuals;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.entity.FloorMathUtilsDirection;
import com.corz.client.entity.FloorMathUtilsDirection.Result;
import com.corz.client.gui.DisabledEnabled;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EnderPearlEntity;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class PearlCatch extends Module {
   private final Set<Integer> pearlWarnSet;
   private final DoubleSetting doubleHelper = new DoubleSetting("Radius", "Warn when a pearl lands within this many blocks", 5.0, 1.0, 30.0, 0.5);

   @Override
   public void onEnable() {
      this.pearlWarnSet.clear();
   }

   public PearlCatch() {
      super("Pearl Catch", IconsPngType.VISUAL);
      this.pearlWarnSet = new HashSet<>();
      this.addSettings(new ConfigSetting[]{this.doubleHelper});
   }

   @Override
   public void onReset() {
      if (mc.player != null && mc.world != null) {
         this.pearlWarnSet.removeIf(integer -> mc.world.getEntityById(integer) == null);

         for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof EnderPearlEntity enderPearlEntity && !this.pearlWarnSet.contains(enderPearlEntity.getId())) {
               Entity owner = enderPearlEntity.getOwner();
               if (owner instanceof PlayerEntity && owner != mc.player) {
                  Result vecDirection = FloorMathUtilsDirection.getVecDirectionForVec3dInt(enderPearlEntity.getEntityPos(), enderPearlEntity.getVelocity(), 300);
                  double distanceTo = mc.player.getEntityPos().distanceTo(vecDirection.targetPos);
                  if (distanceTo <= this.doubleHelper.getValue()) {
                     DisabledEnabled.getInstance().handleDisabledEnabled("Pearl Incoming!", String.format("Landing ~%.0fm away", distanceTo), true);
                     this.pearlWarnSet.add(enderPearlEntity.getId());
                  }
               }
            }
         }
      }
   }
}
