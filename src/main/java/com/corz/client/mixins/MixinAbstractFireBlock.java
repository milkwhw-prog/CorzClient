package com.corz.client.mixins;

import net.minecraft.block.AbstractFireBlock;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import com.corz.client.categories.visuals.CustomFlame;

@Mixin({AbstractFireBlock.class})
public class MixinAbstractFireBlock {
   @Redirect(
      method = {"method_9496(Lnet/minecraft/class_2680;Lnet/minecraft/class_1937;Lnet/minecraft/class_2338;Lnet/minecraft/class_5819;)V"},
      at = @At(
         value = "INVOKE",
         target = "Lnet/minecraft/class_1937;method_8406(Lnet/minecraft/class_2394;DDDDDD)V"
      )
   )
   private void corz_suppressFireSmoke(World world, ParticleEffect particleEffect, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
      if (!CustomFlame.isBaseRestyleWorldFire()) {
         world.addParticleClient(particleEffect, x, y, z, velocityX, velocityY, velocityZ);
      }
   }
}
