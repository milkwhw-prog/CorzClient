package com.corz.client.utils;

import com.corz.client.utils.KnifeSoundCues.Vanilla;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.sound.MovingSoundInstance;
import net.minecraft.client.sound.Sound;
import net.minecraft.client.sound.Sound.RegistrationType;
import net.minecraft.client.sound.SoundInstance.AttenuationType;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.WeightedSoundSet;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.LocalRandom;
import com.corz.client.utils.KnifeSoundCues;
import com.corz.client.utils.KnifeSoundVariety;
import com.corz.client.utils.C2PackAudio;

public final class KnifeSounds {
   private static final KnifeSoundVariety KNIFE_SOUND_VARIETY = new KnifeSoundVariety();
   public static final String DEPLOY = "deploy";
   public static final String DEPLOY_BUTTERFLY = "deploy_butterfly";
   public static final String SWISH = "swish";
   private static final List<KnifeSounds.Voice> ACTIVE_VOICES = new ArrayList<>();
   private static final Map<String, SoundEvent> DEPLOY_KNIFE_MAP = new HashMap<>();
   private static long voiceSequence = Long.MIN_VALUE;
   static final String CORZCLIENT_KNIFE_DEPLOY = "corzclient:knife.deploy";

   public static void update() {
      for (KnifeSounds.Voice voice : ACTIVE_VOICES) {
         voice.updateFlag();
      }

      ACTIVE_VOICES.clear();
   }

   public static void handleMinecraftPlay(String minecraftPlayText, String packText, float minecraftPlayLimit, float minecraftPlayValue, long deployKnifeValue) {
      if (!"Off".equals(minecraftPlayText) && minecraftPlayLimit > 0.0F) {
         MinecraftClient instance = MinecraftClient.getInstance();
         if (instance != null) {
            SoundManager soundManager = instance.getSoundManager();
            if (soundManager != null) {
               ThreadLocalRandom current = ThreadLocalRandom.current();
               if (C2PackAudio.canPack(packText)) {
                  handleDeployKnife(soundManager, packText, minecraftPlayLimit, deployKnifeValue, current);
               } else if (!C2PackAudio.canDeploySwish(packText)) {
                  float minecraftPlayBound = KnifeSoundVariety.getAmount(minecraftPlayLimit, current.nextDouble());
                  float hashHuntBound = KnifeSoundVariety.getAmount(minecraftPlayValue, KnifeSoundCues.getDeployButterfly(packText), current.nextDouble());
                  String longYValue;
                  if ("Minecraft".equals(minecraftPlayText)) {
                     Vanilla minecraft_block_map = KnifeSoundCues.MINECRAFT_BLOCK_MAP.get(packText);
                     if (minecraft_block_map == null) {
                        return;
                     }

                     longYValue = minecraft_block_map.id();
                     minecraftPlayBound *= minecraft_block_map.volume();
                     hashHuntBound *= minecraft_block_map.pitch();
                  } else {
                     longYValue = "corzclient:knife." + packText;
                  }

                  SoundEvent ifAbsent = DEPLOY_KNIFE_MAP.computeIfAbsent(longYValue, minecraftPlayText4 -> SoundEvent.of(Identifier.of(minecraftPlayText4)));
                  KnifeSounds.Voice voice = new KnifeSounds.Voice(
                     ifAbsent, Math.min(1.0F, minecraftPlayBound), Math.max(0.5F, Math.min(2.0F, hashHuntBound)), deployKnifeValue, longYValue, current.nextDouble(), current.nextLong()
                  );
                  handle(soundManager);
                  soundManager.play(voice);
                  if (deployKnifeValue != 0L) {
                     ACTIVE_VOICES.add(voice);
                  }
               }
            }
         }
      }
   }

   public static long getTime() {
      return ++voiceSequence;
   }

   private KnifeSounds() {
   }

   private static void handleDeployKnife(SoundManager soundManager, String deployKnifeText, float deployKnifeBound, long deployKnifeValue, ThreadLocalRandom threadLocalRandom) {
      Identifier identifier = C2PackAudio.getIdentifier(deployKnifeText);
      if (identifier != null) {
         SoundEvent ifAbsent = DEPLOY_KNIFE_MAP.computeIfAbsent("corzclient:knife.deploy", deployKnifeText2 -> SoundEvent.of(Identifier.of(deployKnifeText2)));
         KnifeSounds.Voice voice = new KnifeSounds.Voice(
            ifAbsent, Math.min(1.0F, deployKnifeBound), 1.0F, deployKnifeValue, "corzclient:knife.deploy", threadLocalRandom.nextDouble(), threadLocalRandom.nextLong()
         );
         voice.soundEntry = new Sound(identifier, random2 -> 1.0F, random3 -> 1.0F, 1, RegistrationType.FILE, false, false, 16);
         handle(soundManager);
         soundManager.play(voice);
         C2PackAudio.updatePlayed();
         if (deployKnifeValue != 0L) {
            ACTIVE_VOICES.add(voice);
         }
      }
   }

   public static void handleSoundInstance(long soundValue) {
      if (soundValue != 0L) {
         for (KnifeSounds.Voice voice : ACTIVE_VOICES) {
            if (voice.startSequence == soundValue) {
               voice.updateFlag();
            }
         }

         MinecraftClient instance = MinecraftClient.getInstance();
         if (instance != null && instance.getSoundManager() != null) {
            handle(instance.getSoundManager());
         }
      }
   }

   private static void handle(SoundManager soundManager) {
      ACTIVE_VOICES.removeIf(soundsx -> soundsx.isDone() || !soundManager.isPlaying(soundsx));

      while (ACTIVE_VOICES.size() > 24) {
         ACTIVE_VOICES.remove(0);
      }
   }

   public static void handle(String knifeSoundsText, String packText, float knifeSoundsValue, float minecraftPlayValue) {
      handleMinecraftPlay(knifeSoundsText, packText, knifeSoundsValue, minecraftPlayValue, 0L);
   }

   static final class Pick extends LocalRandom {
      private final double nextDouble;
      private final String intText;
      private boolean intActive;

      @Override
      public int nextInt(int intLimit) {
         if (!this.intActive && intLimit > 0) {
            this.intActive = true;
            return KnifeSounds.KNIFE_SOUND_VARIETY.getValue(this.intText, intLimit, this.nextDouble);
         } else {
            return super.nextInt(intLimit);
         }
      }

      Pick(long knifeSoundsValue, String text, double nextDouble) {
         super(knifeSoundsValue);
         this.intText = text;
         this.nextDouble = nextDouble;
      }
   }

   static final class Voice extends MovingSoundInstance {
      Sound soundEntry;
      private boolean voiceActive;
      final long startSequence;

      Voice(SoundEvent soundEvent, float volume, float pitch, long time, String voiceText, double voiceValue, long voiceValue2) {
         super(soundEvent, SoundCategory.PLAYERS, new KnifeSounds.Pick(voiceValue2, voiceText, voiceValue));
         this.volume = volume;
         this.pitch = pitch;
         this.startSequence = time;
         this.repeat = false;
         this.repeatDelay = 0;
         this.attenuationType = AttenuationType.NONE;
         this.relative = true;
         this.x = 0.0;
         this.y = 0.0;
         this.z = 0.0;
      }

      void updateFlag() {
         this.voiceActive = true;
      }

      @Override
      public WeightedSoundSet getSoundSet(SoundManager soundManager) {
         WeightedSoundSet soundSet = super.getSoundSet(soundManager);
         if (this.soundEntry != null && soundSet != null) {
            super.sound = this.soundEntry;
         }

         return soundSet;
      }

      @Override
      public void tick() {
         if (this.voiceActive) {
            this.volume *= 0.3F;
            if (this.volume < 0.01F) {
               this.setDone();
            }
         }
      }
   }
}
