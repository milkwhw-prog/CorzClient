package com.corz.client.categories.combat;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.entity.ClientThreadUtils;
import com.corz.client.world.MathClampUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import ai.auth.Module;
import com.corz.client.utils.MillisCooldownTimer;
import com.corz.client.utils.IconsPngType;

public class AutoHitCrystal extends Module {
   private int lastCrystalEntityId;
   private long lastTargetTime;
   private final BooleanSetting rotate;
   private final DoubleSetting range = new DoubleSetting("Range", "Max distance to a crystal", 5.0, 2.0, 6.0, 0.1);
   private final IntegerSetting hitDelayMs;
   private final MillisCooldownTimer hitTimer;

   public AutoHitCrystal() {
      super("Auto Hit Crystal", IconsPngType.COMBAT);
      this.rotate = new BooleanSetting("Rotate", "Face the crystal before hitting it", false);
      this.hitDelayMs = new IntegerSetting("Delay", "Minimum ms between crystal hits", 0, 0, 1000);
      this.hitTimer = new MillisCooldownTimer();
      this.lastCrystalEntityId = -1;
      this.lastTargetTime = Long.MIN_VALUE;
      this.addSettings(new ConfigSetting[]{this.range, this.rotate, this.hitDelayMs});
   }

   @Override
   public void onReset() {
      if (mc.player != null && mc.interactionManager != null && mc.world != null) {
         if (mc.currentScreen == null) {
            if (!mc.player.isUsingItem()) {
               if (!ClientThreadUtils.isFlag4() && !ClientThreadUtils.isFlag6() && !ClientThreadUtils.isEntityActive()) {
                  if (this.hitTimer.hasElapsed(this.hitDelayMs.getValue())) {
                     Vec3d eyePos = mc.player.getEyePos();
                     double iIiLimit = this.range.getValue() * this.range.getValue();
                     EndCrystalEntity endCrystalEntity = null;
                     double maxValue = Double.MAX_VALUE;

                     for (Entity entity : mc.world.getEntities()) {
                        if (entity instanceof EndCrystalEntity endCrystalEntity2 && endCrystalEntity2.isAlive()) {
                           double squaredDistanceTo = eyePos.squaredDistanceTo(endCrystalEntity2.getEntityPos());
                           if (squaredDistanceTo <= iIiLimit && squaredDistanceTo < maxValue) {
                              endCrystalEntity = endCrystalEntity2;
                              maxValue = squaredDistanceTo;
                           }
                        }
                     }

                     if (endCrystalEntity != null) {
                        if (this.rotate.isEnabled()) {
                           MathClampUtils.handleInstanceEye(this, endCrystalEntity.getEntityPos(), 60, true, true, false);
                           long crystalMinimumTime = ClientThreadUtils.getTime();
                           boolean iIiFlag = this.lastCrystalEntityId == endCrystalEntity.getId() && this.lastTargetTime == crystalMinimumTime - 1L;
                           this.lastCrystalEntityId = endCrystalEntity.getId();
                           this.lastTargetTime = crystalMinimumTime;
                           if (!iIiFlag) {
                              return;
                           }
                        }

                        mc.interactionManager.attackEntity(mc.player, endCrystalEntity);
                        mc.player.swingHand(Hand.MAIN_HAND);
                        this.hitTimer.restart();
                     }
                  }
               }
            }
         }
      }
   }
}
