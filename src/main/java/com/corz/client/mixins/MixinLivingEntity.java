package com.corz.client.mixins;

import com.corz.client.world.MathClampUtils;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.corz.client.categories.visuals.SwordInspect;
import ai.auth.Module;

@Mixin({LivingEntity.class})
public abstract class MixinLivingEntity {
   @Inject(
      method = {"method_6043()V"},
      at = {@At("TAIL")}
   )
   private void corzJumpMoveFix(CallbackInfo callbackInfo) {
      if (((Object) this) == Module.mc.player) {
         if (MathClampUtils.isClampSettled()) {
            LivingEntity self = (LivingEntity)((net.minecraft.entity.LivingEntity) (Object) (this));
            if (self.isSprinting()) {
               float real = self.getYaw() * (float) (Math.PI / 180.0);
               float srv = MathClampUtils.getYaw() * (float) (Math.PI / 180.0);
               Vec3d v = self.getVelocity();
               double nx = v.x + MathHelper.sin(real) * 0.2 - MathHelper.sin(srv) * 0.2;
               double nz = v.z - MathHelper.cos(real) * 0.2 + MathHelper.cos(srv) * 0.2;
               self.setVelocity(nx, v.y, nz);
            }
         }
      }
   }

   @Inject(
      method = {"method_23667(Lnet/minecraft/class_1268;Z)V"},
      at = {@At("TAIL")}
   )
   private void corz$knifeSwing(Hand hand, boolean corzknifeSwingFlag, CallbackInfo callbackInfo) {
      LivingEntity self = (LivingEntity)((net.minecraft.entity.LivingEntity) (Object) (this));
      if (self.handSwinging && self.handSwingTicks == -1 && self.preferredHand == hand) {
         SwordInspect.handle(self, hand);
      }
   }
}
