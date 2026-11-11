package com.corz.client.config;

import com.corz.client.gui.GuiClickScreen;
import com.corz.client.gui.MatrixCorz;
import com.corz.client.gui.MatrixMatricesScreen;
import com.corz.client.mixins.AccessorRepeatingAudioStream;
import com.corz.client.mixins.AccessorSoundManager;
import com.corz.client.mixins.AccessorSoundSystem;
import com.corz.client.render.HalloweenMatrixUtils;
import com.corz.client.render.SnowVertexUtils;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.sound.AbstractSoundInstance;
import net.minecraft.client.sound.AudioStream;
import net.minecraft.client.sound.BufferedAudioStream;
import net.minecraft.client.sound.Channel.SourceManager;
import net.minecraft.client.sound.RepeatingAudioStream;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.client.sound.SoundInstance.AttenuationType;
import net.minecraft.client.sound.SoundManager;
import net.minecraft.client.sound.SoundSystem;
import net.minecraft.client.sound.SoundSystem.PlayResult;
import net.minecraft.client.sound.Source;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import com.corz.client.categories.theme.ThemeCustomizer;
import com.corz.client.utils.DirExtractionUtils;
import com.corz.client.utils.HalloweenChristmasType;

public final class ThemeMusicPlayer {
   private static long lastSourcePlayingCheckNanos;
   private static long soundResumeDeadlineNanos;
   private static final Consumer<Source> APPLY_VOLUME_SOURCE = source -> source.setVolume(ThemeMusicPlayer.adjustedVolume);
   private static long lastTickNanos;
   private static volatile boolean sourcePlayingDetected;
   private static long lastVolumesRefreshNanos;
   private static volatile float adjustedVolume;
   private static ThemeMusicPlayer.Voice currentVoice;
   private static final Consumer<Source> PAUSE_SOURCE = Source::pause;
   private static volatile ThemeMusicPlayer.Voice pendingVoice;
   private static long lastVolumeApplyNanos;
   private static float soundFadeProgress;
   private static boolean disabledAfterError;
   private static final Consumer<Source> RESUME_SOURCE = Source::resume;
   private static boolean musicPaused;
   private static final Consumer<Source> DETECT_PLAYING_SOURCE = source -> {
      if (source.isPlaying()) {
         sourcePlayingDetected = true;
      }
   };
   private static final long[] SAVED_POSITION_FRAMES = new long[ThemeMusicPlayer.Track.values().length];
   private static float lastAppliedVolumeFactor = -1.0F;
   private static float lastRefreshedDuckFactor = 1.0F;

   static long computeLoopPositionFrames(long soundStopValue, long soundStopBound, long soundStopValue2) {
      long maxPlayedNs = Math.max(0L, soundStopBound);
      long floorModValue = maxPlayedNs / 1000000000L * 44100L + maxPlayedNs % 1000000000L * 44100L / 1000000000L;
      return Math.floorMod(Math.floorMod(soundStopValue, soundStopValue2) + Math.floorMod(floorModValue, soundStopValue2), soundStopValue2);
   }

   private static void applyFadeVolume(long nanoTime) {
      float voiceVolume = getVoiceVolume();
      boolean adjustedVolumeFlag = soundFadeProgress <= 0.0F || soundFadeProgress >= 1.0F;
      if (!(Math.abs(voiceVolume - lastAppliedVolumeFactor) < 1.0E-4F)) {
         if (adjustedVolumeFlag || nanoTime - lastVolumeApplyNanos >= 8000000L) {
            SourceManager soundSources = getVoiceSourceManager();
            if (soundSources != null) {
               adjustedVolume = ((AccessorSoundSystem)getSoundSystem()).corzAdjustedVolume(currentVoice);
               soundSources.run(APPLY_VOLUME_SOURCE);
               lastAppliedVolumeFactor = voiceVolume;
               lastVolumeApplyNanos = nanoTime;
            }
         }
      }
   }

   static boolean isMatrixScreenShowing(MinecraftClient client) {
      if (!MatrixCorz.isScissorMatrixActive()) {
         return false;
      } else {
         Screen currentScreen = client.currentScreen;

         for (int i = 0; i < 4 && currentScreen != null; i++) {
            if (currentScreen instanceof MatrixMatricesScreen) {
               return i > 0 || !MatrixMatricesScreen.isMatrixMatricesActive();
            }

            currentScreen = currentScreen instanceof GuiClickScreen guiClickScreen ? guiClickScreen.getCurrentScreen() : null;
         }

         return false;
      }
   }

   static ThemeMusicPlayer.Track getTrackForTheme(HalloweenChristmasType halloweenChristmasType) {
      return switch (halloweenChristmasType) {
         case HALLOWEEN -> ThemeMusicPlayer.Track.HALLOWEEN;
         case CHRISTMAS -> ThemeMusicPlayer.Track.CHRISTMAS;
         case NONE -> null;
      };
   }

   static float getVoiceVolume() {
      float soundStopFactor = smoothStep(soundFadeProgress);
      return soundStopFactor * soundStopFactor * getConfiguredVolume();
   }

   public static float getMusicDuckFactor() {
      return currentVoice != null && !musicPaused ? 1.0F - smoothStep(soundFadeProgress) : 1.0F;
   }

   private static SourceManager getVoiceSourceManager() {
      SoundSystem soundSystem = getSoundSystem();
      return soundSystem != null && currentVoice != null ? ((AccessorSoundSystem)soundSystem).corzSources().get(currentVoice) : null;
   }

   private static void refreshMusicVolumes(SoundManager soundManager, long nanoTime) {
      float amount = getMusicDuckFactor();
      if (amount != lastRefreshedDuckFactor) {
         boolean volumesRefreshFlag = amount >= 1.0F || amount <= 0.0F;
         if (volumesRefreshFlag || !(Math.abs(amount - lastRefreshedDuckFactor) < 0.01F) && nanoTime - lastVolumesRefreshNanos >= 30000000L) {
            soundManager.refreshSoundVolumes(SoundCategory.MUSIC);
            lastRefreshedDuckFactor = amount;
            lastVolumesRefreshNanos = nanoTime;
         }
      }
   }

   private static ThemeMusicPlayer.Track getDesiredTrack() {
      ThemeCustomizer singleton = ThemeCustomizer.singleton;
      if (singleton != null && singleton.booleanBase.isEnabled() && !(getConfiguredVolume() <= 0.0F)) {
         HalloweenChristmasType timeInstance = HalloweenChristmasType.getTimeInstance();

         boolean soundStopFlag = switch (timeInstance) {
            case HALLOWEEN -> HalloweenMatrixUtils.isBaseInstance();
            case CHRISTMAS -> SnowVertexUtils.isBaseInstance();
            case NONE -> false;
         };
         return soundStopFlag ? getTrackForTheme(timeInstance) : null;
      } else {
         return null;
      }
   }

   private static void tickMusic() {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (instance != null) {
         SoundManager soundManager = instance.getSoundManager();
         if (soundManager != null) {
            long nanoTime = System.nanoTime();
            if (DirExtractionUtils.isFlag()) {
               if (currentVoice != null) {
                  soundManager.stop(currentVoice);
                  saveAndClearVoice();
               }

               refreshMusicVolumes(soundManager, nanoTime);
            } else {
               long soundValue = lastTickNanos == 0L ? 0L : nanoTime - lastTickNanos;
               float min2 = Math.min(0.1F, (float)soundValue / 1.0E9F);
               lastTickNanos = nanoTime;
               updateVoicePlayback(instance, soundManager, nanoTime, min2, soundValue);
               refreshMusicVolumes(soundManager, nanoTime);
            }
         }
      }
   }

   private static void runOnVoiceSources(Consumer<Source> consumer) {
      SourceManager soundSources = getVoiceSourceManager();
      if (soundSources != null) {
         soundSources.run(consumer);
      }
   }

   private static float getConfiguredVolume() {
      ThemeCustomizer singleton = ThemeCustomizer.singleton;
      if (singleton == null) {
         return 0.0F;
      } else {
         double soundStopLimit = singleton.doubleHelper.getValue();
         return (float)(soundStopLimit < 0.0 ? 0.0 : (soundStopLimit > 1.0 ? 1.0 : soundStopLimit));
      }
   }

   static float smoothStep(float soundStopFactor) {
      soundStopFactor = soundStopFactor < 0.0F ? 0.0F : (soundStopFactor > 1.0F ? 1.0F : soundStopFactor);
      return soundStopFactor * soundStopFactor * (3.0F - 2.0F * soundStopFactor);
   }

   public static void enforcePausedState() {
      if (currentVoice != null && musicPaused) {
         runOnVoiceSources(PAUSE_SOURCE);
      }
   }

   private static boolean isVoiceSourcePlaying(long soundValue) {
      if (!currentVoice.streamReady) {
         return false;
      } else if (sourcePlayingDetected) {
         return true;
      } else {
         if (soundValue - lastSourcePlayingCheckNanos >= 10000000L) {
            lastSourcePlayingCheckNanos = soundValue;
            runOnVoiceSources(DETECT_PLAYING_SOURCE);
         }

         return false;
      }
   }

   private static void updateVoicePlayback(MinecraftClient client, SoundManager soundManager, long soundValue, float soundValue2, long soundValue3) {
      ThemeMusicPlayer.Track baseInstance = getDesiredTrack();
      boolean soundFlag = baseInstance != null && isMatrixScreenShowing(client) && isUiVolumeAudible(client);
      if (currentVoice != null && currentVoice.playbackStarted && !musicPaused) {
         currentVoice.playbackPositionFrames += soundValue3;
      }

      if (currentVoice != null && !soundManager.isPlaying(currentVoice)) {
         saveAndClearVoice();
      }

      if (currentVoice != null && currentVoice.track != baseInstance) {
         soundFlag = false;
      }

      if (currentVoice == null) {
         soundFadeProgress = 0.0F;
         if (!soundFlag || soundValue < soundResumeDeadlineNanos) {
            return;
         }

         ThemeMusicPlayer.Voice voice = new ThemeMusicPlayer.Voice(baseInstance, SAVED_POSITION_FRAMES[baseInstance.ordinal()], soundValue);
         sourcePlayingDetected = false;
         pendingVoice = voice;

         PlayResult playResult;
         try {
            playResult = soundManager.play(voice);
         } finally {
            pendingVoice = null;
         }

         if (playResult == PlayResult.NOT_STARTED || !soundManager.isPlaying(voice)) {
            soundResumeDeadlineNanos = soundValue + 3000000000L;
            return;
         }

         if (!voice.streamHooked) {
            voice.streamOffsetFrames = 0L;
            voice.streamReady = true;
         }

         currentVoice = voice;
         musicPaused = false;
         lastAppliedVolumeFactor = -1.0F;
      }

      if (!currentVoice.playbackStarted) {
         if (currentVoice.track != baseInstance) {
            soundManager.stop(currentVoice);
            saveAndClearVoice();
            return;
         }

         if (!isVoiceSourcePlaying(soundValue)) {
            if (soundValue - currentVoice.createdNanos > 10000000000L) {
               soundManager.stop(currentVoice);
               saveAndClearVoice();
               soundResumeDeadlineNanos = soundValue + 3000000000L;
            }

            return;
         }

         currentVoice.playbackStarted = true;
      }

      if (soundFlag) {
         if (musicPaused) {
            runOnVoiceSources(RESUME_SOURCE);
            musicPaused = false;
         }

         soundFadeProgress = Math.min(1.0F, soundFadeProgress + soundValue2 / 1.8F);
      } else {
         soundFadeProgress = Math.max(0.0F, soundFadeProgress - soundValue2 / 1.1F);
      }

      applyFadeVolume(soundValue);
      if (!soundFlag && soundFadeProgress <= 0.0F) {
         if (currentVoice.track != baseInstance) {
            soundManager.stop(currentVoice);
            saveAndClearVoice();
         } else if (!musicPaused) {
            runOnVoiceSources(PAUSE_SOURCE);
            musicPaused = true;
         }
      }
   }

   public static void tickMusicSafely() {
      if (!disabledAfterError) {
         try {
            tickMusic();
         } catch (Throwable e) {
            disabledAfterError = true;

            try {
               MinecraftClient instance = MinecraftClient.getInstance();
               if (currentVoice != null && instance != null && instance.getSoundManager() != null) {
                  instance.getSoundManager().stop(currentVoice);
               }
            } catch (Throwable e2) {
            }

            currentVoice = null;
         }
      }
   }

   private ThemeMusicPlayer() {
   }

   private static boolean isUiVolumeAudible(MinecraftClient client) {
      return client.options != null && client.options.getSoundVolume(SoundCategory.UI) > 0.0F;
   }

   private static void saveAndClearVoice() {
      if (currentVoice != null) {
         SAVED_POSITION_FRAMES[currentVoice.track.ordinal()] = currentVoice.getLoopPositionFrames();
      }

      currentVoice = null;
      musicPaused = false;
      soundFadeProgress = 0.0F;
      lastAppliedVolumeFactor = -1.0F;
   }

   private static SoundSystem getSoundSystem() {
      MinecraftClient instance = MinecraftClient.getInstance();
      SoundManager soundManager = instance == null ? null : instance.getSoundManager();
      return soundManager == null ? null : ((AccessorSoundManager)soundManager).corzSoundSystem();
   }

   public static CompletableFuture<AudioStream> hookVoiceStreamLoad(Identifier id2, CompletableFuture<AudioStream> completableFuture) {
      ThemeMusicPlayer.Voice pendingVoice22 = pendingVoice;
      if (pendingVoice22 != null && !pendingVoice22.streamHooked && completableFuture != null && pendingVoice22.getSound() != null && pendingVoice22.getSound().getLocation().equals(id2)) {
         pendingVoice22.streamHooked = true;
         return completableFuture.thenApplyAsync(stream -> {
            try {
               pendingVoice22.streamOffsetFrames = countSkippedStreamFrames(stream, pendingVoice22.startOffsetFrames);
            } catch (Exception e) {
            }

            pendingVoice22.streamReady = true;
            return (AudioStream)stream;
         }, Util.getDownloadWorkerExecutor());
      } else {
         return null;
      }
   }

   static long countSkippedStreamFrames(AudioStream audioStream, long delegateReadFactor)  throws java.io.IOException {
      if (delegateReadFactor <= 0L) {
         return 0L;
      } else if (!(
         (audioStream instanceof RepeatingAudioStream repeatingAudioStream ? ((AccessorRepeatingAudioStream)repeatingAudioStream).corzDelegate() : audioStream) instanceof BufferedAudioStream bufferedAudioStream
      )) {
         return 0L;
      } else {
         int maxChannels = Math.max(1, bufferedAudioStream.getFormat().getChannels());
         long delegateReadLimit = delegateReadFactor * maxChannels;
         ThemeMusicPlayer.SampleCounter sampleCounter = new ThemeMusicPlayer.SampleCounter();

         while (sampleCounter.sampleCount < delegateReadLimit && bufferedAudioStream.read(sampleCounter)) {
         }

         return sampleCounter.sampleCount / maxChannels;
      }
   }

   private static final class SampleCounter implements FloatConsumer {
      long sampleCount;

      public void accept(float acceptValue) {
         this.sampleCount++;
      }
   }

   public static enum Track {
      HALLOWEEN("theme.halloween", 5997915L),
      CHRISTMAS("theme.christmas", 5584530L);

      final SoundEvent corzclient;
      final long loopLengthFrames;

      private Track(String soundStopText, long corzclientTime) {
         this.corzclient = SoundEvent.of(Identifier.of("corzclient", soundStopText));
         this.loopLengthFrames = corzclientTime;
      }
   }

   static final class Voice extends AbstractSoundInstance {
      boolean playbackStarted;
      boolean streamHooked;
      final long createdNanos;
      volatile boolean streamReady;
      long playbackPositionFrames;
      final long startOffsetFrames;
      final ThemeMusicPlayer.Track track;
      volatile long streamOffsetFrames;

      Voice(ThemeMusicPlayer.Track halloweenChristmasType, long time, long nanoTime) {
         super(halloweenChristmasType.corzclient, SoundCategory.UI, SoundInstance.createRandom());
         this.track = halloweenChristmasType;
         this.startOffsetFrames = time;
         this.streamOffsetFrames = time;
         this.createdNanos = nanoTime;
         this.repeat = true;
         this.repeatDelay = 0;
         this.volume = 0.0F;
         this.pitch = 1.0F;
         this.relative = true;
         this.attenuationType = AttenuationType.NONE;
      }

      @Override
      public boolean shouldAlwaysPlay() {
         return true;
      }

      long getLoopPositionFrames() {
         return ThemeMusicPlayer.computeLoopPositionFrames(this.streamOffsetFrames, this.playbackPositionFrames, this.track.loopLengthFrames);
      }

      @Override
      public float getVolume() {
         return ThemeMusicPlayer.getVoiceVolume();
      }
   }
}
