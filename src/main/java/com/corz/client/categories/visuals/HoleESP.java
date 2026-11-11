package com.corz.client.categories.visuals;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.VectorDotUtils;
import com.corz.client.render.VertexBuilderUtils;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents.Load;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents.Unload;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;
import com.corz.client.utils.ChunkScanScheduler;
import com.corz.client.utils.BuriedOpenBuried;
import com.corz.client.utils.BuriedOpenConst;
import com.corz.client.utils.ChunkSectionHelper;
import com.corz.client.utils.CopperBrickUtils;
import com.corz.client.utils.EspHoleApi;
import com.corz.client.utils.LinesQuads;
import com.corz.client.utils.MaxUtils;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class HoleESP extends Module {
   private HoleESP.Entry[] entrys;
   private final ArrayList<HoleESP.Entry> hashOpenEntryList;
   private final LongArrayList longArrayList;
   private final IntegerSetting integerHelper;
   private final Long2ObjectOpenHashMap<HoleESP.Entry> long2ObjectOpenHashMap;
   private final BuriedOpenBuried buriedScanner;
   private long maxScansCount;
   private int lastScanKey;
   private int hashValue;
   private final BooleanSetting booleanBase;
   private final Long2ObjectOpenHashMap<LongArrayList> pendingChunkLists;
   private final BooleanSetting tracers;
   private boolean chunksRenderFlag;
   private ClientWorld world;
   private final ArrayList<HoleESP.Entry> holeEspEntryList;
   private final ArrayList<HoleESP.Entry> hashEntryList;
   private final IntegerSetting range = new IntegerSetting(
      "Range", "How far around you hidden tunnels are searched for and shown, in blocks.", 64, 16, 128
   );
   private int entrysHashValue;
   private int scansLinesCount;
   private MaxUtils maxUtils;
   private static HoleESP singleton;
   private final ChunkSectionHelper chunkSectionHelper;
   private final Long2LongOpenHashMap long2LongOpenHashMap;
   private final BooleanSetting tunnels;
   private long lastScanNanos;
   private boolean hashActive;
   private final ChunkScanScheduler hashChunk;
   private final LongOpenHashSet longOpenHashSet;
   private int iIiCount;
   private final BooleanSetting shafts;
   private final Long2ObjectOpenHashMap<LongArrayList> renderChunkLists;
   private boolean renderScanFlag;
   private final ArrayList<HoleESP.Entry> entryList;
   private long scansValue;
   private int x;
   private int z;
   private int holeEspValue;

   private static float getTimeMin(HoleESP.Entry entry, long holeEspValue) {
      float min2 = Math.min(1.0F, Math.max(0.0F, (float)(holeEspValue - entry.min) / 3.2E8F));
      float holeEspFactor = 1.0F - min2;
      float timeMin = 1.0F - holeEspFactor * holeEspFactor * holeEspFactor;
      if (entry.nanoTime != 0L) {
         float holeEspFactor2 = Math.min(1.0F, Math.max(0.0F, (float)(holeEspValue - entry.nanoTime) / 2.6E8F));
         timeMin *= 1.0F - holeEspFactor2 * holeEspFactor2 * (3.0F - 2.0F * holeEspFactor2);
      }

      return timeMin;
   }

   private static float getAmount(float holeEspFactor) {
      return holeEspFactor * holeEspFactor;
   }

   @Override
   protected void onEnable() {
      this.updateClearHash();
   }

   public static void handleScanRender(BlockPos pos2, BlockState state, BlockState blockState) {
      HoleESP singleton2 = singleton;
      if (singleton2 != null && state != blockState && !singleton2.renderScanFlag && singleton2.isEnabled() && singleton2.world != null) {
         try {
            singleton2.handleEntrysSection(pos2.getX(), pos2.getY(), pos2.getZ(), state, blockState);
         } catch (Throwable e) {
            singleton2.handleScanRender(e);
         }
      }
   }

   private void handleArrayHash(int arrayHashValue, int arrayHashValue2) {
      long hashOpenValue = EspHoleApi.getTime(arrayHashValue, arrayHashValue2);
      this.chunkSectionHelper.handleSectionsCoord(arrayHashValue, arrayHashValue2);
      this.hashChunk.forgetChunk(hashOpenValue);
      LongArrayList long2objectopenhashmap = (LongArrayList)this.pendingChunkLists.get(hashOpenValue);
      if (long2objectopenhashmap != null) {
         this.longArrayList.clear();
         this.longArrayList.addAll(long2objectopenhashmap);
         long nanoTime = System.nanoTime();
         int arrayHashCounter = 0;

         for (int i = this.longArrayList.size(); arrayHashCounter < i; arrayHashCounter++) {
            HoleESP.Entry long2objectopenhashmap2 = (HoleESP.Entry)this.long2ObjectOpenHashMap.get(this.longArrayList.getLong(arrayHashCounter));
            if (long2objectopenhashmap2 != null) {
               this.handleHashOpen(long2objectopenhashmap2);
               long2objectopenhashmap2.nanoTime = nanoTime;
               this.hashEntryList.add(long2objectopenhashmap2);
               this.hashActive = true;
            }
         }
      }
   }

   private void updateLinesQuads() {
      boolean booleanValue = this.tunnels.isEnabled();
      boolean enabled = this.shafts.isEnabled();
      if (booleanValue || enabled) {
         Matrix4f matrix = VertexBuilderUtils.getMatrix();
         Vec3d cameraPos = mc.gameRenderer.getCamera().getCameraPos();
         double x = cameraPos.x;
         double y = cameraPos.y;
         double z = cameraPos.z;
         int linesQuadsValue = (int)Math.floor(x);
         int buriedOpenLimit = (int)Math.floor(y);
         int floor3 = (int)Math.floor(z);
         long nanoTime = System.nanoTime();
         float linesQuadsValue4 = this.range.getValue();
         int linesQuadsValue5 = MatrixEspUtils.releaseRightValue & 16777215;
         boolean enabled3 = this.booleanBase.isEnabled();
         boolean enabled4 = this.tracers.isEnabled();
         VertexConsumer vertexConsumer = enabled3 ? VertexBuilderUtils.getBufferIMMEDIATE() : null;
         VertexConsumer buffer = VertexBuilderUtils.getBufferVertexConsumer();

         for (int i = 0; i < this.entrysHashValue; i++) {
            HoleESP.Entry entry = this.entrys[i];
            LinesQuads class67 = entry.class67;
            if (class67.shaftTunnelValue == 1 ? enabled : booleanValue) {
               double linesQuadsFactor = Math.max(Math.max(class67.scoreValue - x, x - (class67.linesQuadsValue2LinesQuads + 1)), 0.0);
               double max2 = Math.max(Math.max(class67.linesQuadsValue - z, z - (class67.linesQuadsValue4LinesQuads + 1)), 0.0);
               double distance = Math.sqrt(linesQuadsFactor * linesQuadsFactor + max2 * max2);
               if (!(distance >= linesQuadsValue4)) {
                  float linesQuadsValue6 = getTimeMin(entry, nanoTime) * getAmount((float)distance, linesQuadsValue4);
                  if (class67.canLinesQuads(linesQuadsValue, buriedOpenLimit, floor3)) {
                     linesQuadsValue6 *= 0.35F;
                  }

                  if (!(linesQuadsValue6 <= 0.004F)) {
                     double vectorValue = (class67.scoreValue + class67.linesQuadsValue2LinesQuads + 1) * 0.5;
                     double destinationAmount = (class67.scoreLinesValue + class67.linesQuadsValue3LinesQuads + 1) * 0.5;
                     double destinationZ = (class67.linesQuadsValue + class67.linesQuadsValue4LinesQuads + 1) * 0.5;
                     float height = 0.5F
                        * (float)Math.sqrt(
                           getAmount(class67.linesQuadsValue2LinesQuads - class67.scoreValue + 1)
                              + getAmount(class67.linesQuadsValue3LinesQuads - class67.scoreLinesValue + 1)
                              + getAmount(class67.linesQuadsValue4LinesQuads - class67.linesQuadsValue + 1)
                        );
                     if (!VertexBuilderUtils.canVector(vectorValue, destinationAmount, destinationZ, height)) {
                        float amountX = VertexBuilderUtils.getAmountX(class67.scoreValue);
                        float linesQuadsValue12 = VertexBuilderUtils.getAmount(class67.scoreLinesValue);
                        float amountForDouble = VertexBuilderUtils.getAmountForDouble(class67.linesQuadsValue);
                        if (vertexConsumer != null) {
                           int linesQuadsValue14 = getValue(linesQuadsValue6 * 0.075F, linesQuadsValue5);
                           float[] linesQuadsValues = class67.linesQuadsTable;

                           for (int j = 0; j < linesQuadsValues.length; j += 3) {
                              vertexConsumer.vertex(matrix, amountX + linesQuadsValues[j], linesQuadsValue12 + linesQuadsValues[j + 1], amountForDouble + linesQuadsValues[j + 2]).color(linesQuadsValue14);
                           }
                        }

                        int linesQuadsValue15 = getValue(linesQuadsValue6 * 0.9F, linesQuadsValue5);
                        float[] table = class67.table;

                        for (int k = 0; k < table.length; k += 6) {
                           float linesQuadsValue16 = table[k + 3] != table[k] ? 1.0F : 0.0F;
                           float linesQuadsValue17 = table[k + 4] != table[k + 1] ? 1.0F : 0.0F;
                           float linesQuadsValue18 = table[k + 5] != table[k + 2] ? 1.0F : 0.0F;
                           buffer.vertex(matrix, amountX + table[k], linesQuadsValue12 + table[k + 1], amountForDouble + table[k + 2])
                              .color(linesQuadsValue15)
                              .normal(linesQuadsValue16, linesQuadsValue17, linesQuadsValue18)
                              .lineWidth(1.5F);
                           buffer.vertex(matrix, amountX + table[k + 3], linesQuadsValue12 + table[k + 4], amountForDouble + table[k + 5])
                              .color(linesQuadsValue15)
                              .normal(linesQuadsValue16, linesQuadsValue17, linesQuadsValue18)
                              .lineWidth(1.5F);
                        }

                        if (enabled4) {
                           VectorDotUtils.handle(
                              class67.blockX + 0.5, class67.blockY + 0.5, class67.blockZ + 0.5, getValue(linesQuadsValue6 * 0.35F, linesQuadsValue5), getValue(linesQuadsValue6 * 0.85F, linesQuadsValue5), 1.2F
                           );
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void updateHashFast() {
      if (!this.long2LongOpenHashMap.isEmpty()) {
         long currentMillis = System.currentTimeMillis();
         var fastIterator = this.long2LongOpenHashMap.long2LongEntrySet().fastIterator();

         while (fastIterator.hasNext()) {
            it.unimi.dsi.fastutil.longs.Long2LongMap.Entry entry = (it.unimi.dsi.fastutil.longs.Long2LongMap.Entry)fastIterator.next();
            if (entry.getLongValue() <= currentMillis) {
               long longKey = entry.getLongKey();
               fastIterator.remove();
               this.hashChunk.forgetChunk(longKey);
            }
         }
      }
   }

   @Override
   protected void onRender3D(QueueCommand queueCommand) {
      if (!this.chunksRenderFlag && this.entrysHashValue != 0 && mc.player != null) {
         try {
            this.updateLinesQuads();
         } catch (Throwable e) {
            this.chunksRenderFlag = true;
         }
      }
   }

   public static String formatScansLines() {
      HoleESP singleton2 = singleton;
      if (singleton2 == null) {
         return "module missing";
      } else {
         StringBuilder stringBuilder = new StringBuilder();
         stringBuilder.append("enabled=")
            .append(singleton2.isEnabled())
            .append(" scanOff=")
            .append(singleton2.renderScanFlag)
            .append(" renderOff=")
            .append(singleton2.chunksRenderFlag)
            .append(" chunks=")
            .append(singleton2.hashChunk.getScannedChunkCount())
            .append(" sections=")
            .append(singleton2.chunkSectionHelper.getHashOpen())
            .append(" live=")
            .append(singleton2.long2ObjectOpenHashMap.size())
            .append(
               String.format(
                  Locale.ROOT,
                  " scans=%d avgMs=%.3f maxMs=%.2f at=%d,%d maxFills=%d maxCells=%d tickMaxMs=%.2f",
                  singleton2.scansLinesCount,
                  singleton2.scansLinesCount == 0 ? 0.0 : singleton2.maxScansCount / 1000000.0 / singleton2.scansLinesCount,
                  singleton2.scansValue / 1000000.0,
                  singleton2.x,
                  singleton2.z,
                  singleton2.holeEspValue,
                  singleton2.hashValue,
                  singleton2.lastScanNanos / 1000000.0
               )
            );
         var iterator = singleton2.long2ObjectOpenHashMap.values().iterator();

         while (iterator.hasNext()) {
            HoleESP.Entry entry = (HoleESP.Entry)iterator.next();
            LinesQuads class67 = entry.class67;
            stringBuilder.append(" | ")
               .append(class67.shaftTunnelValue == 1 ? "shaft" : "tunnel")
               .append(" len=")
               .append(class67.cellsTunnelValue)
               .append(" cells=")
               .append(class67.scoreCellsValue)
               .append(String.format(Locale.ROOT, " score=%.2f", class67.scoreCellsAmount))
               .append(" box=")
               .append(class67.scoreValue)
               .append(',')
               .append(class67.scoreLinesValue)
               .append(',')
               .append(class67.linesQuadsValue)
               .append("..")
               .append(class67.linesQuadsValue2LinesQuads)
               .append(',')
               .append(class67.linesQuadsValue3LinesQuads)
               .append(',')
               .append(class67.linesQuadsValue4LinesQuads)
               .append(" lines=")
               .append(class67.getAmounts())
               .append(" quads=")
               .append(class67.getLinesQuadsTable());
         }

         stringBuilder.append(" | fills:");

         for (int i = 0; i < 16; i++) {
            int scansLinesLimit = singleton2.buriedScanner.getValue(i);
            if (scansLinesLimit > 0) {
               stringBuilder.append(' ').append(BuriedOpenConst.formatBuriedScore(i)).append('=').append(scansLinesLimit);
            }
         }

         return stringBuilder.toString();
      }
   }

   @Override
   protected void onReset() {
      if (mc.world != null && mc.player != null && !this.renderScanFlag) {
         long nanoTime = System.nanoTime();

         try {
            ClientWorld world = mc.world;
            if (world != this.world) {
               this.handleClearHash(false);
               this.hashEntryList.clear();
               this.world = world;
            }

            this.chunkSectionHelper.handleBottomSections(world);
            int value = this.integerHelper.getValue();
            if (value != this.lastScanKey) {
               if (this.lastScanKey != -1) {
                  this.handleClearHash(true);
               }

               this.lastScanKey = value;
               this.maxUtils = MaxUtils.getMaxUtils(value);
            }

            ChunkPos chunkPos = mc.player.getChunkPos();
            int iIiValue = this.getMax();
            this.updateHashFast();
            this.hashChunk.scanChunks(world, chunkPos.x, chunkPos.z, iIiValue, 30000L, ChunkScanScheduler.getFpsScaledBudgetNanos(450), this::handleScansMax);
            if (++this.iIiCount >= 40) {
               this.iIiCount = 0;
               this.handleLinesQuads(chunkPos.x, chunkPos.z, iIiValue);
            }

            this.updateFlag();
            if (this.hashActive) {
               this.updateEntrysHash();
            }
         } catch (Throwable e) {
            this.handleScanRender(e);
         }

         long time = System.nanoTime() - nanoTime;
         if (time > this.lastScanNanos) {
            this.lastScanNanos = time;
         }
      }
   }

   private void updateClearHash() {
      this.long2ObjectOpenHashMap.clear();
      this.pendingChunkLists.clear();
      this.renderChunkLists.clear();
      this.long2LongOpenHashMap.clear();
      this.hashEntryList.clear();
      this.hashChunk.clearScannedChunks();
      this.chunkSectionHelper.updateClearChunk();
      this.buriedScanner.update();
      this.entrysHashValue = 0;
      this.hashActive = false;
      this.world = null;
      this.lastScanKey = -1;
      this.iIiCount = 0;
      this.maxScansCount = this.scansValue = this.lastScanNanos = 0L;
      this.scansLinesCount = this.holeEspValue = this.hashValue = 0;
   }

   @Override
   protected void onDisable() {
      this.updateClearHash();
   }

   private void updateEntrysHash() {
      this.hashActive = false;
      int entrysHashLimit = this.long2ObjectOpenHashMap.size() + this.hashEntryList.size();
      if (this.entrys.length < entrysHashLimit) {
         this.entrys = new HoleESP.Entry[Math.max(entrysHashLimit, this.entrys.length * 2)];
      }

      int value = 0;
      var iterator = this.long2ObjectOpenHashMap.values().iterator();

      while (iterator.hasNext()) {
         HoleESP.Entry entry = (HoleESP.Entry)iterator.next();
         this.entrys[value++] = entry;
      }

      for (HoleESP.Entry entry2 : this.hashEntryList) {
         this.entrys[value++] = entry2;
      }

      for (int i = value; i < this.entrysHashValue; i++) {
         this.entrys[i] = null;
      }

      this.entrysHashValue = value;
   }

   private void handlePutHash(HoleESP.Entry entry) {
      this.long2ObjectOpenHashMap.put(entry.class67.hashOpenValue, entry);

      for (long hashValue : entry.class67.copyOf) {
         LongArrayList long2objectopenhashmap = (LongArrayList)this.pendingChunkLists.get(hashValue);
         if (long2objectopenhashmap == null) {
            long2objectopenhashmap = new LongArrayList(4);
            this.pendingChunkLists.put(hashValue, long2objectopenhashmap);
         }

         if (!long2objectopenhashmap.contains(entry.class67.hashOpenValue)) {
            long2objectopenhashmap.add(entry.class67.hashOpenValue);
         }
      }
   }

   private int getMax() {
      int holeEspBound = (this.range.getValue() + 15) / 16 + 1;
      return Math.max(1, Math.min(holeEspBound, mc.options.getClampedViewDistance() + 1));
   }

   private void handleHashPut(long hashPutValue, long currentTimeMillisOffset) {
      if (!this.long2LongOpenHashMap.containsKey(hashPutValue)) {
         this.long2LongOpenHashMap.put(hashPutValue, currentTimeMillisOffset);
      }
   }

   private void handleHashOpen(long hashOpenValue) {
      long nanoTime = System.nanoTime();
      this.longOpenHashSet.clear();
      this.holeEspEntryList.clear();
      this.entryList.clear();
      var list = this.buriedScanner.getItems();
      int index = 0;

      for (int i = list.size(); index < i; index++) {
         LinesQuads linesQuads = (LinesQuads)list.get(index);
         if (this.longOpenHashSet.add(linesQuads.hashOpenValue)) {
            HoleESP.Entry long2objectopenhashmap = (HoleESP.Entry)this.long2ObjectOpenHashMap.get(linesQuads.hashOpenValue);
            if (long2objectopenhashmap == null || long2objectopenhashmap.class67.chunkKey != linesQuads.chunkKey) {
               HoleESP.Entry entry = new HoleESP.Entry(linesQuads, long2objectopenhashmap != null ? long2objectopenhashmap.min : nanoTime);
               if (long2objectopenhashmap != null) {
                  this.handleHashOpen(long2objectopenhashmap);
               }

               this.handlePutHash(entry);
               this.holeEspEntryList.add(entry);
               this.hashActive = true;
            }
         }
      }

      LongArrayList long2objectopenhashmap2 = (LongArrayList)this.pendingChunkLists.get(hashOpenValue);
      if (long2objectopenhashmap2 != null) {
         this.longArrayList.clear();
         this.longArrayList.addAll(long2objectopenhashmap2);
         int hashOpenCounter = 0;

         for (int j = this.longArrayList.size(); hashOpenCounter < j; hashOpenCounter++) {
            long long2 = this.longArrayList.getLong(hashOpenCounter);
            if (!this.longOpenHashSet.contains(long2)) {
               HoleESP.Entry long2objectopenhashmap3 = (HoleESP.Entry)this.long2ObjectOpenHashMap.get(long2);
               if (long2objectopenhashmap3 != null) {
                  this.handleHashOpen(long2objectopenhashmap3);
                  this.entryList.add(long2objectopenhashmap3);
               }
            }
         }
      }

      int hashOpenCounter2 = 0;

      for (int k = this.holeEspEntryList.size(); hashOpenCounter2 < k; hashOpenCounter2++) {
         HoleESP.Entry entry2 = this.holeEspEntryList.get(hashOpenCounter2);
         this.hashOpenEntryList.clear();
         var iterator = this.long2ObjectOpenHashMap.values().iterator();

         while (iterator.hasNext()) {
            HoleESP.Entry holeESPEntry = (HoleESP.Entry)iterator.next();
            if (holeESPEntry != entry2 && entry2.class67.canLinesQuads(holeESPEntry.class67)) {
               this.hashOpenEntryList.add(holeESPEntry);
            }
         }

         for (HoleESP.Entry entry4 : this.hashOpenEntryList) {
            this.handleHashOpen(entry4);
            this.entryList.add(entry4);
         }

         for (HoleESP.Entry entry5 : this.entryList) {
            if (!entry5.flag && entry5.class67.canLinesQuads(entry2.class67)) {
               entry2.min = Math.min(entry2.min, entry5.min);
               entry5.flag = true;
            }
         }
      }

      for (HoleESP.Entry totalPriceData : this.entryList) {
         if (!totalPriceData.flag) {
            totalPriceData.nanoTime = nanoTime;
            this.hashEntryList.add(totalPriceData);
         }
      }

      if (!this.entryList.isEmpty()) {
         this.hashActive = true;
      }

      int hashOpenCounter3 = 0;

      for (int m = this.buriedScanner.getBuriedOpenEntryCount(); hashOpenCounter3 < m; hashOpenCounter3++) {
         this.handleHashPutForLong(this.buriedScanner.getTime(hashOpenCounter3), hashOpenValue);
      }
   }

   private void handleLinesQuads(int linesQuadsValue, int linesQuadsValue2, int iIiValue) {
      int centerZOffset = iIiValue + 2;
      this.hashOpenEntryList.clear();
      var iterator = this.long2ObjectOpenHashMap.values().iterator();

      while (iterator.hasNext()) {
         HoleESP.Entry entry = (HoleESP.Entry)iterator.next();
         int linesQuadsValue4 = entry.class67.scoreValue + entry.class67.linesQuadsValue2LinesQuads >> 1 >> 4;
         int linesQuadsValue5 = entry.class67.linesQuadsValue + entry.class67.linesQuadsValue4LinesQuads >> 1 >> 4;
         if (Math.abs(linesQuadsValue4 - linesQuadsValue) > centerZOffset || Math.abs(linesQuadsValue5 - linesQuadsValue2) > centerZOffset) {
            this.hashOpenEntryList.add(entry);
         }
      }

      long nanoTime = System.nanoTime();

      for (HoleESP.Entry entry2 : this.hashOpenEntryList) {
         this.handleHashOpen(entry2);

         for (long hashOpenValue : entry2.class67.copyOf) {
            this.hashChunk.forgetChunk(hashOpenValue);
         }

         entry2.nanoTime = nanoTime;
         this.hashEntryList.add(entry2);
         this.hashActive = true;
      }

      this.hashChunk.removeFarChunks(linesQuadsValue, linesQuadsValue2, centerZOffset + 1);
      var fastIterator = this.renderChunkLists.long2ObjectEntrySet().fastIterator();

      while (fastIterator.hasNext()) {
         long longKey = ((it.unimi.dsi.fastutil.longs.Long2ObjectMap.Entry)fastIterator.next()).getLongKey();
         if (Math.abs(EspHoleApi.getLongKey(longKey) - linesQuadsValue) > centerZOffset + 2 || Math.abs(EspHoleApi.getValue(longKey) - linesQuadsValue2) > centerZOffset + 2) {
            fastIterator.remove();
         }
      }
   }

   private void handleHashOpen(HoleESP.Entry entry) {
      if (this.long2ObjectOpenHashMap.get(entry.class67.hashOpenValue) == entry) {
         this.long2ObjectOpenHashMap.remove(entry.class67.hashOpenValue);
      }

      for (long hashOpenValue : entry.class67.copyOf) {
         LongArrayList long2objectopenhashmap = (LongArrayList)this.pendingChunkLists.get(hashOpenValue);
         if (long2objectopenhashmap != null) {
            long2objectopenhashmap.rem(entry.class67.hashOpenValue);
            if (long2objectopenhashmap.isEmpty()) {
               this.pendingChunkLists.remove(hashOpenValue);
            }
         }
      }
   }

   public HoleESP() {
      super("Hole ESP", IconsPngType.VISUAL);
      this.integerHelper = new IntegerSetting(
         "Min Length", "Shortest tunnel or shaft that counts, in blocks. Lower also shows short dig-outs, higher shows only long tunnels.", 8, 5, 32
      );
      this.tunnels = new BooleanSetting("Tunnels", "Sealed tunnels dug sideways: straight, branching or stair-stepped runs closed off at both ends.", true);
      this.shafts = new BooleanSetting("Shafts", "Sealed shafts dug straight down and capped at the top.", true);
      this.booleanBase = new BooleanSetting("Fill", "Faint fill inside each outline.", true);
      this.tracers = new BooleanSetting("Tracers", "Line from your crosshair to each hidden tunnel.", false);
      this.hashChunk = new ChunkScanScheduler();
      this.chunkSectionHelper = new ChunkSectionHelper();
      this.buriedScanner = new BuriedOpenBuried();
      this.maxUtils = MaxUtils.getMaxUtils(8);
      this.long2ObjectOpenHashMap = new Long2ObjectOpenHashMap();
      this.pendingChunkLists = new Long2ObjectOpenHashMap();
      this.renderChunkLists = new Long2ObjectOpenHashMap();
      this.long2LongOpenHashMap = new Long2LongOpenHashMap();
      this.hashEntryList = new ArrayList<>();
      this.entrys = new HoleESP.Entry[16];
      this.longOpenHashSet = new LongOpenHashSet();
      this.longArrayList = new LongArrayList();
      this.holeEspEntryList = new ArrayList<>();
      this.entryList = new ArrayList<>();
      this.hashOpenEntryList = new ArrayList<>();
      this.lastScanKey = -1;
      this.addSettings(new ConfigSetting[]{this.range, this.integerHelper, this.tunnels, this.shafts, this.booleanBase, this.tracers});
      singleton = this;
      ClientChunkEvents.CHUNK_LOAD.register((Load)(chunk, world) -> {
         if (this.isEnabled() && chunk == this.world && !this.renderScanFlag) {
            try {
               this.handleChunkHash(world.getPos().x, world.getPos().z);
            } catch (Throwable e) {
               this.handleScanRender(e);
            }
         }
      });
      ClientChunkEvents.CHUNK_UNLOAD.register((Unload)(chunk, world) -> {
         if (this.isEnabled() && chunk == this.world && !this.renderScanFlag) {
            try {
               this.handleArrayHash(world.getPos().x, world.getPos().z);
            } catch (Throwable e2) {
               this.handleScanRender(e2);
            }
         }
      });
   }

   private static int getValue(float holeEspBound, int holeEspValue) {
      int round = Math.round(Math.max(0.0F, Math.min(1.0F, holeEspBound)) * 255.0F);
      return round << 24 | holeEspValue;
   }

   private void handleChunkHash(int chunkHashValue, int chunkHashValue2) {
      long hashOpenValue = EspHoleApi.getTime(chunkHashValue, chunkHashValue2);
      this.chunkSectionHelper.handleSectionsCoord(chunkHashValue, chunkHashValue2);
      this.hashChunk.forgetChunk(hashOpenValue);
      LongArrayList long2objectopenhashmap = (LongArrayList)this.renderChunkLists.remove(hashOpenValue);
      if (long2objectopenhashmap != null) {
         int chunkHashCounter = 0;

         for (int i = long2objectopenhashmap.size(); chunkHashCounter < i; chunkHashCounter++) {
            this.hashChunk.forgetChunk(long2objectopenhashmap.getLong(chunkHashCounter));
         }
      }
   }

   public int getHashOpen() {
      return this.long2ObjectOpenHashMap.size();
   }

   private void handleScanRender(Throwable throwable) {
      if (!this.renderScanFlag) {
         this.renderScanFlag = true;
      }
   }

   private static float getAmount(float holeEspValue, float holeEspValue2) {
      float holeEspLimit = holeEspValue2 - 12.0F;
      if (holeEspValue <= holeEspLimit) {
         return 1.0F;
      } else {
         float holeEspFactor = Math.min(1.0F, (holeEspValue - holeEspLimit) / 12.0F);
         return 1.0F - holeEspFactor * holeEspFactor * (3.0F - 2.0F * holeEspFactor);
      }
   }

   private void handleClearHash(boolean hashFlag) {
      long nanoTime = System.nanoTime();
      if (hashFlag) {
         var iterator = this.long2ObjectOpenHashMap.values().iterator();

         while (iterator.hasNext()) {
            HoleESP.Entry entry = (HoleESP.Entry)iterator.next();
            entry.nanoTime = nanoTime;
            this.hashEntryList.add(entry);
         }
      }

      this.long2ObjectOpenHashMap.clear();
      this.pendingChunkLists.clear();
      this.renderChunkLists.clear();
      this.long2LongOpenHashMap.clear();
      this.hashChunk.clearScannedChunks();
      this.chunkSectionHelper.updateClearChunk();
      this.hashActive = true;
   }

   private void handleScansMax(WorldChunk worldChunk, long scansValue) {
      if (!this.renderScanFlag) {
         ChunkPos pos = worldChunk.getPos();
         long nanoTime = System.nanoTime();

         boolean scansFlag;
         try {
            scansFlag = this.buriedScanner.canBuriedOpen(this.chunkSectionHelper, pos.x, pos.z, this.maxUtils);
         } catch (Throwable e) {
            this.handleScanRender(e);
            return;
         }

         long time = System.nanoTime() - nanoTime;
         this.scansLinesCount++;
         this.maxScansCount += time;
         if (time > this.scansValue) {
            this.scansValue = time;
            this.x = pos.x;
            this.z = pos.z;
            this.holeEspValue = this.buriedScanner.getValue();
            this.hashValue = this.buriedScanner.getBuriedOpenCount();
         }

         if (scansFlag) {
            this.handleHashOpen(scansValue);
         }
      }
   }

   private void handleEntrysSection(int entrysSectionValue, int entrysSectionValue2, int entrysSectionValue3, BlockState state, BlockState blockState) {
      if (CopperBrickUtils.getByteReference(state) != CopperBrickUtils.getByteReference(blockState)) {
         this.chunkSectionHelper.handleHashOpen(entrysSectionValue, entrysSectionValue2, entrysSectionValue3);
         long currentTimeMillisOffset = System.currentTimeMillis() + 300L;
         this.handleHashPut(EspHoleApi.getTime(entrysSectionValue >> 4, entrysSectionValue3 >> 4), currentTimeMillisOffset);

         for (int i = 0; i < this.entrysHashValue; i++) {
            HoleESP.Entry entry = this.entrys[i];
            if (entry.nanoTime == 0L && entry.class67.canLinesQuads(entrysSectionValue, entrysSectionValue2, entrysSectionValue3, 1)) {
               for (long hashPutValue : entry.class67.copyOf) {
                  this.handleHashPut(hashPutValue, currentTimeMillisOffset);
               }
            }
         }
      }
   }

   private void updateFlag() {
      if (!this.hashEntryList.isEmpty()) {
         long nanoTime = System.nanoTime();
         if (this.hashEntryList.removeIf(entry -> nanoTime - entry.nanoTime >= 260000000L)) {
            this.hashActive = true;
         }
      }
   }

   private void handleHashPutForLong(long hashPutValue, long hashOpenValue) {
      LongArrayList long2objectopenhashmap = (LongArrayList)this.renderChunkLists.get(hashPutValue);
      if (long2objectopenhashmap == null) {
         long2objectopenhashmap = new LongArrayList(2);
         this.renderChunkLists.put(hashPutValue, long2objectopenhashmap);
      }

      if (!long2objectopenhashmap.contains(hashOpenValue)) {
         long2objectopenhashmap.add(hashOpenValue);
      }
   }

   private static final class Entry {
      long nanoTime;
      long min;
      final LinesQuads class67;
      boolean flag;

      Entry(LinesQuads linesQuads, long min) {
         this.class67 = linesQuads;
         this.min = min;
      }
   }
}
