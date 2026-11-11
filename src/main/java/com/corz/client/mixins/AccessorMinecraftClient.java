package com.corz.client.mixins;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin({MinecraftClient.class})
public interface AccessorMinecraftClient {
   @Invoker("doAttack")
   boolean corzDoAttack();

   @Accessor("attackCooldown")
   void corzSetAttackCooldown(int corzAttackValue);

   @Accessor("attackCooldown")
   int corzGetAttackCooldown();
}
