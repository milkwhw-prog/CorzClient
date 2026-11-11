package com.corz.client.mixins;

import com.corz.client.world.MathClampUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.corz.client.utils.PlayerInputOverride;
import com.corz.client.categories.movement.Freecam;
import com.corz.client.utils.AmountsUtils;
import com.corz.client.utils.ExtUtils;

@Mixin({KeyboardInput.class})
public class MixinKeyboardInput {
   @Inject(
      method = {"method_3129()V"},
      at = {@At("RETURN")}
   )
   private void corzFreezeMove(CallbackInfo callbackInfo) {
      MinecraftClient mc = MinecraftClient.getInstance();
      if (mc.player != null) {
         AccessorInput in = (AccessorInput)this;
         if (mc.player.input == ((Object) this)) {
            Freecam.updateFlagInput();
            PlayerInputOverride.applyInputOverride(in, mc.player);
         }

         boolean rotFreeze = MathClampUtils.isFlag();
         boolean invMask = ExtUtils.isActionFlag2();
         if (!rotFreeze && !invMask) {
            if (MathClampUtils.isClampSettled() && !PlayerInputOverride.isRotationOverrideActive()) {
               PlayerInput p = in.getPlayerInput();
               int strafe = (p.left() ? 1 : 0) - (p.right() ? 1 : 0);
               int forward = (p.forward() ? 1 : 0) - (p.backward() ? 1 : 0);
               if (strafe == 0 && forward == 0) {
                  return;
               }

               int[] r = AmountsUtils.getValues(strafe, forward, mc.player.getYaw(), MathClampUtils.getYaw());
               if (r[0] == strafe && r[1] == forward) {
                  return;
               }

               in.setPlayerInput(new PlayerInput(r[1] > 0, r[1] < 0, r[0] > 0, r[0] < 0, p.jump(), p.sneak(), p.sprint()));
               in.setMovementVector(new Vec2f(r[0], r[1]).normalize());
            }
         } else {
            in.setMovementVector(Vec2f.ZERO);
            PlayerInput px = in.getPlayerInput();
            boolean jump = px.jump() && !invMask;
            boolean sneak = px.sneak() && !invMask;
            in.setPlayerInput(new PlayerInput(false, false, false, false, jump, sneak, false));
         }
      }
   }
}
