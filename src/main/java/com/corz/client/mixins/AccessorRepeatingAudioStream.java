package com.corz.client.mixins;

import net.minecraft.client.sound.AudioStream;
import net.minecraft.client.sound.RepeatingAudioStream;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({RepeatingAudioStream.class})
public interface AccessorRepeatingAudioStream {
   @Accessor("delegate")
   AudioStream corzDelegate();
}
