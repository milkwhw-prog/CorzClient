package com.corz.client.mixins;

import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil.Key;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({KeyBinding.class})
public interface AccessorKeyBinding {
   @Accessor("boundKey")
   Key getBoundKey();

   @Accessor("pressed")
   void setPressedRaw(boolean pressedRawFlag);

   @Accessor("pressed")
   boolean getPressedRaw();

   @Accessor("timesPressed")
   int corzGetTimesPressed();

   @Accessor("timesPressed")
   void corzSetTimesPressed(int corzPressedValue);
}
