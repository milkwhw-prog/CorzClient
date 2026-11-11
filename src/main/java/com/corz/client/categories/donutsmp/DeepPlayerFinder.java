package com.corz.client.categories.donutsmp;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.ColorSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.gui.DisabledEnabled;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.VectorDotUtils;
import com.corz.client.render.VertexBuilderUtils;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import java.util.Arrays;
import net.minecraft.util.math.ChunkPos;
import org.joml.Matrix4f;
import ai.auth.Module;
import com.corz.client.utils.ChunkLightSnapshot;
import com.corz.client.utils.LightPacketCache;
import com.corz.client.utils.PingSoundPlayer;
import com.corz.client.utils.SectionStatus;
import com.corz.client.utils.IconsPngType;

public class DeepPlayerFinder extends Module {
   private int lastCenterChunkX;
   private final BooleanSetting toastSetting;
   private int lastRenderDistance;
   private final IntegerSetting renderDistanceSetting;
   private int worldIdentityHash;
   private final BooleanSetting waypointLine;
   private long[] sortedChunkOffsets;
   private boolean needsRebuild;
   private final IntegerSetting scanRadius = new IntegerSetting("Scan Radius", "Chunk scan radius around the player.", 16, 1, 32);
   private final IntegerSetting opacitySetting;
   private final BooleanSetting soundPing;
   private int[] visibleChunkXs;
   private int lastCenterChunkZ;
   private int tickCounter;
   private int visibleChunkCount;
   private final LongOpenHashSet flaggedChunkKeys;
   private int[] visibleChunkZs;
   private final ColorSetting colorSetting;

   @Override
   protected void onDisable() {
      this.resetState();
   }

   @Override
   protected void onEnable() {
      this.resetState();
   }

   @Override
   protected void onReset() {
      if (mc.world != null && mc.player != null) {
         int hashCode = System.identityHashCode(mc.world);
         if (hashCode != this.worldIdentityHash) {
            this.worldIdentityHash = hashCode;
            this.resetState();
         }

         int iIiValue = mc.player.getBlockX() >> 4;
         int iIiValue2 = mc.player.getBlockZ() >> 4;
         if (this.tickCounter++ % 4 == 0) {
            int iIiLimit = this.scanRadius.getValue();

            for (ChunkLightSnapshot chunkLightSnapshot : LightPacketCache.INSTANCE.sectionsCoordChunkLightSnaps.values()) {
               if (Math.abs(chunkLightSnapshot.chunkX - iIiValue) <= iIiLimit && Math.abs(chunkLightSnapshot.chunkZ - iIiValue2) <= iIiLimit) {
                  long long2 = ChunkPos.toLong(chunkLightSnapshot.chunkX, chunkLightSnapshot.chunkZ);
                  if (!this.flaggedChunkKeys.contains(long2) && this.hasZeroedLightBelowSurface(chunkLightSnapshot)) {
                     this.flaggedChunkKeys.add(long2);
                     this.needsRebuild = true;
                     this.notifyPlayerUnderground(chunkLightSnapshot.chunkX, chunkLightSnapshot.chunkZ);
                  }
               }
            }

            if (this.flaggedChunkKeys.size() > 4096) {
               this.pruneFarChunks(iIiValue, iIiValue2);
            }
         }

         int iIiValue3 = this.renderDistanceSetting.getValue();
         if (this.needsRebuild || iIiValue != this.lastCenterChunkX || iIiValue2 != this.lastCenterChunkZ || iIiValue3 != this.lastRenderDistance) {
            this.rebuildVisibleChunks(iIiValue, iIiValue2, iIiValue3);
         }
      }
   }

   private void notifyPlayerUnderground(int undergroundDigValue, int undergroundDigValue2) {
      int undergroundDigValue3 = (undergroundDigValue << 4) + 8;
      int undergroundDigValue4 = (undergroundDigValue2 << 4) + 8;
      if (this.toastSetting.isEnabled()) {
         DisabledEnabled.getInstance()
            .handleDisabledEnabled("Player Underground!", String.format("Chunk %d, %d \u2192 dig at (%d, <16, %d)", undergroundDigValue, undergroundDigValue2, undergroundDigValue3, undergroundDigValue4), true);
      }

      if (this.soundPing.isEnabled()) {
         PingSoundPlayer.playPingSound();
      }
   }

   private void pruneFarChunks(int packedIteratorValue, int packedIteratorValue2) {
      LongIterator iterator = this.flaggedChunkKeys.iterator();

      while (iterator.hasNext()) {
         long randomLong = iterator.nextLong();
         if (Math.abs(ChunkPos.getPackedX(randomLong) - packedIteratorValue) > 64 || Math.abs(ChunkPos.getPackedZ(randomLong) - packedIteratorValue2) > 64) {
            iterator.remove();
         }
      }

      this.needsRebuild = true;
   }

   private void resetState() {
      this.flaggedChunkKeys.clear();
      this.visibleChunkCount = 0;
      this.needsRebuild = true;
      this.tickCounter = 0;
   }

   private void rebuildVisibleChunks(int lineValue, int value, int value2) {
      this.needsRebuild = false;
      this.lastCenterChunkX = lineValue;
      this.lastCenterChunkZ = value;
      this.lastRenderDistance = value2;
      if (this.sortedChunkOffsets.length < this.flaggedChunkKeys.size()) {
         this.sortedChunkOffsets = new long[Math.max(16, this.flaggedChunkKeys.size() * 2)];
      }

      int lineGuideCounter = 0;
      LongIterator iterator = this.flaggedChunkKeys.iterator();

      while (iterator.hasNext()) {
         long randomLong = iterator.nextLong();
         int lineGuideFactor = ChunkPos.getPackedX(randomLong) - lineValue;
         int j = ChunkPos.getPackedZ(randomLong) - value;
         if (Math.abs(lineGuideFactor) <= value2 && Math.abs(j) <= value2) {
            long lineGuideValue = (long)lineGuideFactor * lineGuideFactor + (long)j * j;
            this.sortedChunkOffsets[lineGuideCounter++] = lineGuideValue << 16 | (long)(lineGuideFactor + value2) << 8 | j + value2;
         }
      }

      Arrays.sort(this.sortedChunkOffsets, 0, lineGuideCounter);
      int min = Math.min(lineGuideCounter, 256);
      if (this.visibleChunkXs.length < min) {
         this.visibleChunkXs = new int[min];
         this.visibleChunkZs = new int[min];
      }

      for (int i = 0; i < min; i++) {
         this.visibleChunkXs[i] = lineValue + (int)(this.sortedChunkOffsets[i] >> 8 & 255L) - value2;
         this.visibleChunkZs[i] = value + (int)(this.sortedChunkOffsets[i] & 255L) - value2;
      }

      this.visibleChunkCount = min;
   }

   @Override
   protected void onRender3D(QueueCommand queueCommand) {
      if (this.visibleChunkCount != 0 && mc.world != null && mc.player != null) {
         int deepPlayerValue = Math.max(20, Math.min(255, this.opacitySetting.getValue()));
         int max2 = Math.max(5, deepPlayerValue / 6);
         int intValue = this.colorSetting.getColor();
         int deepPlayerValue3 = intValue >> 16 & 0xFF;
         int deepPlayerValue4 = intValue >> 8 & 0xFF;
         int deepPlayerValue5 = intValue & 0xFF;
         int n2 = deepPlayerValue << 24 | deepPlayerValue3 << 16 | deepPlayerValue4 << 8 | deepPlayerValue5;
         float deepPlayerValue7 = deepPlayerValue3 / 255.0F;
         float deepPlayerValue8 = deepPlayerValue4 / 255.0F;
         float deepPlayerValue9 = deepPlayerValue5 / 255.0F;
         float susChunkValue = max2 / 255.0F;
         boolean booleanValue = this.waypointLine.isEnabled();
         Matrix4f matrix = VertexBuilderUtils.getMatrix();
         float f2 = VertexBuilderUtils.getAmount(59.0);
         float f5 = VertexBuilderUtils.getAmount(61.0);
         int deepPlayerLimit = this.visibleChunkCount;

         for (int i = 0; i < deepPlayerLimit; i++) {
            double deepPlayerValue13 = this.visibleChunkXs[i] * 16.0;
            double vertexBuilderValue = this.visibleChunkZs[i] * 16.0;
            if (!VertexBuilderUtils.canVector(deepPlayerValue13 + 8.0, 60.0, vertexBuilderValue + 8.0, 12.0F)) {
               float amountX = VertexBuilderUtils.getAmountX(deepPlayerValue13);
               float amountForDouble = VertexBuilderUtils.getAmountForDouble(vertexBuilderValue);
               VertexBuilderUtils.handle(matrix, amountX, f2, amountForDouble, amountX + 16.0F, f5, amountForDouble + 16.0F, deepPlayerValue7, deepPlayerValue8, deepPlayerValue9, susChunkValue);
               VertexBuilderUtils.handle(matrix, amountX, f2, amountForDouble, amountX + 16.0F, f5, amountForDouble + 16.0F, n2, 2.0F);
            }

            if (booleanValue && i < 8) {
               VectorDotUtils.handle(deepPlayerValue13 + 8.0, 60.0, vertexBuilderValue + 8.0, intValue, false);
            }
         }

         VertexBuilderUtils.updateDrawImmediate();
      }
   }

   public DeepPlayerFinder() {
      super("Deep Player Finder", IconsPngType.DONUTSMP);
      this.toastSetting = new BooleanSetting("Toast", "Show a toast notification on detection.", true);
      this.soundPing = new BooleanSetting("Sound Ping", "Play the base-found ping (same sound as ChunkFinder) on detection.", true);
      this.opacitySetting = new IntegerSetting("Opacity", "Opacity of the chunk marker box.", 200, 0, 255);
      this.renderDistanceSetting = new IntegerSetting(
         "Render Distance", "Max distance in chunks from the player to render flagged chunk boxes (max 256 boxes per frame).", 8, 1, 32
      );
      this.colorSetting = new ColorSetting("Flag Color", "Color of the chunk marker box.", -52686);
      this.waypointLine = new BooleanSetting("Waypoint Line", "Draw a guide line from your crosshair to the nearest flagged chunks.", true);
      this.flaggedChunkKeys = new LongOpenHashSet();
      this.sortedChunkOffsets = new long[0];
      this.visibleChunkXs = new int[0];
      this.visibleChunkZs = new int[0];
      this.needsRebuild = true;
      this.lastCenterChunkX = Integer.MIN_VALUE;
      this.lastCenterChunkZ = Integer.MIN_VALUE;
      this.lastRenderDistance = -1;
      this.addSettings(
         new ConfigSetting[]{
            this.scanRadius, this.toastSetting, this.soundPing, this.opacitySetting, this.renderDistanceSetting, this.colorSetting, this.waypointLine
         }
      );
   }

   private boolean hasZeroedLightBelowSurface(ChunkLightSnapshot chunkLightSnapshot) {
      for (int i = 1; i <= chunkLightSnapshot.verticalSections; i++) {
         int sectionZeroedLimit = (chunkLightSnapshot.sectionCoord + (i - 1)) * 16;
         if (sectionZeroedLimit >= 16) {
            break;
         }

         if (chunkLightSnapshot.blockLightStatuses[i] == SectionStatus.ZEROED_PRESENT) {
            return true;
         }
      }

      return false;
   }

   public int getFlaggedChunkCount() {
      return this.flaggedChunkKeys.size();
   }
}
