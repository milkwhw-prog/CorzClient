package com.corz.client.categories.visuals;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.sound.SoundInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.Identifier;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.IconsPngType;

public class NoFarmLag extends Module {
   private static volatile int redstoneTorchLimit = 0;
   private final BooleanSetting booleanBase;
   private final BooleanSetting pistons;
   private final BooleanSetting explosions;
   private final ConcurrentHashMap<Identifier, long[]> concurrentHashMap;
   private final BooleanSetting sounds;
   private final BooleanSetting noFarmBooleanBase = new BooleanSetting("Items", "Hides dropped items and their splash bubbles in water.", true);
   private final EnumSetting enumHelper;
   private final IntegerSetting integerHelper;
   private final BooleanSetting xpOrbs = new BooleanSetting("XP Orbs", "Hides experience orbs.", true);
   private final BooleanSetting tntSetting;
   private final BooleanSetting noteBlocks;

   public static NoFarmLag getNoFarmLag() {
      NoFarmLag noFarmLag = (NoFarmLag)ModuleRegistry.getModule(NoFarmLag.class);
      return noFarmLag != null && noFarmLag.isEnabled() ? noFarmLag : null;
   }

   private void updateEnum() {
      int currentIndex = this.enumHelper.getCurrentIndex();
      if (currentIndex != redstoneTorchLimit) {
         redstoneTorchLimit = currentIndex;
         updateExecuteWorld();
      }
   }

   @Override
   protected void onEnable() {
      this.concurrentHashMap.clear();
      this.updateEnum();
   }

   public static boolean canRedstoneTorch(BlockState blockState) {
      int redstoneTorchValue = redstoneTorchLimit;
      if (redstoneTorchValue != 0 && blockState != null) {
         Block block = blockState.getBlock();
         if (block == Blocks.REDSTONE_WIRE) {
            return true;
         } else {
            return redstoneTorchValue < 2 ? false : block instanceof AbstractRedstoneGateBlock || block == Blocks.REDSTONE_TORCH || block == Blocks.REDSTONE_WALL_TORCH;
         }
      } else {
         return false;
      }
   }

   private static void updateExecuteWorld() {
      if (mc != null && mc.worldRenderer != null && mc.world != null) {
         mc.execute(() -> mc.worldRenderer.reload());
      }
   }

   @Override
   protected void onReset() {
      this.updateEnum();
      if (this.concurrentHashMap.size() > 256) {
         long currentMillis = System.currentTimeMillis();
         this.concurrentHashMap.values().removeIf(iIiValues -> currentMillis - iIiValues[1] > 1000L);
      }
   }

   public NoFarmLag() {
      super("No Farm Lag", IconsPngType.VISUAL);
      this.booleanBase = new BooleanSetting("Falling Blocks", "Hides falling sand, gravel and concrete powder.", true);
      this.tntSetting = new BooleanSetting("TNT", "Hides activated TNT.", true);
      this.explosions = new BooleanSetting("Explosions", "Hides explosion particles.", true);
      this.noteBlocks = new BooleanSetting("Note Blocks", "Hides the notes that pop up from note blocks.", true);
      this.sounds = new BooleanSetting("Sounds", "Mutes sounds that repeat too often. Sounds play again after they stop repeating.", true);
      this.integerHelper = new IntegerSetting("Sound Spam", "Sets how many repeated sounds trigger muting. Lower values mute sounds sooner.", 5, 1, 20);
      this.pistons = new BooleanSetting("Pistons", "Skips piston animations and moves pushed blocks straight to their new position.", true);
      this.enumHelper = new EnumSetting(
         "Redstone", "Hides redstone dust or dust and components. Levers, buttons and lamps stay visible.", "All", "Off", "Dust", "All"
      );
      this.concurrentHashMap = new ConcurrentHashMap<>();
      this.addSettings(
         new ConfigSetting[]{
            this.noFarmBooleanBase,
            this.xpOrbs,
            this.booleanBase,
            this.tntSetting,
            this.explosions,
            this.noteBlocks,
            this.sounds,
            this.integerHelper,
            this.pistons,
            this.enumHelper
         }
      );
   }

   @Override
   protected void onDisable() {
      this.concurrentHashMap.clear();
      if (redstoneTorchLimit != 0) {
         redstoneTorchLimit = 0;
         updateExecuteWorld();
      }
   }

   public boolean isBase() {
      return this.noteBlocks.isEnabled();
   }

   public boolean canBase(Entity entity) {
      if (entity instanceof ItemEntity) {
         return this.noFarmBooleanBase.isEnabled();
      } else if (entity instanceof ExperienceOrbEntity) {
         return this.xpOrbs.isEnabled();
      } else if (entity instanceof FallingBlockEntity) {
         return this.booleanBase.isEnabled();
      } else {
         return entity instanceof TntEntity ? this.tntSetting.isEnabled() : false;
      }
   }

   public boolean isPistons() {
      return this.pistons.isEnabled();
   }

   public boolean canNoteBlock(SoundInstance soundInstance) {
      if (this.sounds.isEnabled() && soundInstance != null) {
         SoundCategory category = soundInstance.getCategory();
         Identifier id = soundInstance.getId();
         if (id == null) {
            return false;
         } else {
            boolean noteBlockFlag = category == SoundCategory.RECORDS && id.getPath().startsWith("block.note_block");
            if (noteBlockFlag
               || category != SoundCategory.MASTER
                  && category != SoundCategory.MUSIC
                  && category != SoundCategory.RECORDS
                  && category != SoundCategory.UI
                  && category != SoundCategory.VOICE) {
               long currentMillis = System.currentTimeMillis();
               long[] ifAbsent = this.concurrentHashMap.computeIfAbsent(id, now -> new long[]{currentMillis, 0L, 0L, 0L});
               synchronized (ifAbsent) {
                  if (currentMillis - ifAbsent[1] > 1000L) {
                     ifAbsent[0] = currentMillis;
                     ifAbsent[2] = 0L;
                     ifAbsent[3] = 0L;
                  } else if (currentMillis - ifAbsent[0] > 1000L) {
                     ifAbsent[0] = currentMillis;
                     ifAbsent[2] = 0L;
                  }

                  ifAbsent[1] = currentMillis;
                  ifAbsent[2]++;
                  if (ifAbsent[2] > this.integerHelper.getValue()) {
                     ifAbsent[3] = 1L;
                  }

                  return ifAbsent[3] != 0L;
               }
            } else {
               return false;
            }
         }
      } else {
         return false;
      }
   }

   public boolean isExplosions() {
      return this.explosions.isEnabled();
   }
}
