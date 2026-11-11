package com.corz.client.render;

import com.corz.client.world.BlockStateType;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.RenderSystem.ShapeIndexBuffer;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.longs.Long2ByteOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongArrayList;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.render.BlockRenderLayer;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.SpriteAtlasTexture;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.ChunkStatus;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;
import com.corz.client.utils.RebuildFms;

public final class HashOpenSchem implements AutoCloseable {
   private final LongOpenHashSet longOpenHashSet;
   private final List<SchemCorz> samplerGhostItems;
   private final Long2ObjectOpenHashMap<SchemCorz> long2ObjectOpenHashMap = new Long2ObjectOpenHashMap();
   private ByteBuffer memAlloc;
   private static final BlockRenderLayer[] ghostSamplerBlockRenderLayer = BlockRenderLayer.values();
   private final LongOpenHashSet arrayHashLongOpenHashSet;
   private final List<GpuBufferSlice> transformsDynamicItems;
   private final List<GpuBufferSlice> transformsDynamicGpuBufferSl;
   private final BlockBuffer blockBuffer;
   private final RebuildFms rebuildFms;
   private final LongOpenHashSet rebuildFmsLongOpenHashSet;
   private GpuBuffer buffer;
   private GpuBuffer bufferBufferAlloc;
   private static final Direction[] offsetArrayDirections = Direction.values();
   private static final Vector4f VECTOR4F = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
   private final Long2ByteOpenHashMap long2ByteOpenHashMap = new Long2ByteOpenHashMap();
   private final LongArrayList longArrayList;
   private static final Vector3f VECTOR3F = new Vector3f();
   private final List<GpuBufferSlice> dynamicTextureGpuBufferSlice;
   private long lastRebuildTime;
   private static final Matrix4f MATRIX4F = new Matrix4f();
   private boolean fmsRebuildActive;
   private final Long2ObjectOpenHashMap<BlockState> blockStatesByPos = new Long2ObjectOpenHashMap();
   private long fmsRebuildValue;
   private final List<int[]> arrayHashIntArrayList;

   private void handleArrayHash(SchemCorz schemCorz, MinecraftClient client) {
      boolean hasChunk = client.world.getChunkManager().getChunk(schemCorz.schemCorzValue >> 4, schemCorz.arrayHashValue >> 4, ChunkStatus.FULL, false) != null;
      if (!hasChunk) {
         schemCorz.chunkArrayCount++;
      } else {
         this.longArrayList.clear();
         this.arrayHashLongOpenHashSet.clear();
         boolean arrayHashFlag = this.arrayHashIntArrayList.size() != 1;
         Mutable mutable = new Mutable();

         for (int i = 0; i < this.arrayHashIntArrayList.size(); i++) {
            int[] arrayHashValues = this.arrayHashIntArrayList.get(i);
            int arrayHashValue = Math.max(arrayHashValues[0], schemCorz.schemCorzValue);
            int arrayHashLimit = Math.min(arrayHashValues[3], schemCorz.schemCorzValue + 15);
            int max2 = Math.max(arrayHashValues[1], schemCorz.matrixStackValue);
            int min2 = Math.min(arrayHashValues[4], schemCorz.matrixStackValue + 15);
            int max3 = Math.max(arrayHashValues[2], schemCorz.arrayHashValue);
            int min3 = Math.min(arrayHashValues[5], schemCorz.arrayHashValue + 15);
            if (arrayHashValue <= arrayHashLimit && max2 <= min2 && max3 <= min3) {
               for (int j = max2; j <= min2; j++) {
                  for (int k = max3; k <= min3; k++) {
                     for (int m = arrayHashValue; m <= arrayHashLimit; m++) {
                        long long2 = BlockPos.asLong(m, j, k);
                        if (!this.blockStatesByPos.containsKey(long2) && (!arrayHashFlag || !this.arrayHashLongOpenHashSet.contains(long2))) {
                           mutable.set(m, j, k);
                           if (!client.world.getBlockState(mutable).isAir()) {
                              this.longArrayList.add(long2);
                              if (arrayHashFlag) {
                                 this.arrayHashLongOpenHashSet.add(long2);
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         if (!this.longArrayList.isEmpty()) {
            schemCorz.longArray = this.longArrayList.toLongArray();
            schemCorz.arrayHashCount = schemCorz.arrayHashCount + schemCorz.longArray.length;
         }
      }
   }

   public boolean canFmsRebuild(
      OpacityColorData opacityColorData, Vec3d vec, List<BlockPos> list, List<BlockState> list2, int fmsRebuildValue, OrderedRenderCommandQueue orderedRenderCommandQueue, float tickProgress
   ) {
      if (this.fmsRebuildActive && !this.long2ObjectOpenHashMap.isEmpty()) {
         MinecraftClient instance = MinecraftClient.getInstance();
         if (instance.world == null) {
            return false;
         } else {
            long time = opacityColorData.getOpacityClarity();
            if (time != this.lastRebuildTime) {
               this.lastRebuildTime = time;
               this.updateHashOpen();
            }

            this.rebuildFms.handleFmsRebuild(this.long2ObjectOpenHashMap.size());
            this.fmsRebuildValue = SchemCorz.getTime((int)Math.floor(vec.x), (int)Math.floor(vec.y), (int)Math.floor(vec.z));
            Frustum frustum = this.getFrustum(vec);
            double renderDistance = opacityColorData.renderDistance();
            this.samplerGhostItems.clear();
            ObjectIterator iterator = this.long2ObjectOpenHashMap.values().iterator();

            while (iterator.hasNext()) {
               SchemCorz schemCorz = (SchemCorz)iterator.next();
               schemCorz.fmsRebuildActive = false;
               schemCorz.distanceSq = schemCorz.getAmount(vec.x, vec.y, vec.z);
               double fmsRebuildFactor = renderDistance + 13.86;
               if (schemCorz.distanceSq > fmsRebuildFactor * fmsRebuildFactor) {
                  this.rebuildFms.rebuildFmsCount++;
               } else if (!opacityColorData.canPlayer(schemCorz.matrixStackValue)) {
                  this.rebuildFms.rebuildFmsCount++;
               } else if (frustum != null && !frustum.isVisible(schemCorz.box)) {
                  this.rebuildFms.schemCount++;
               } else {
                  schemCorz.fmsRebuildActive = true;
                  this.samplerGhostItems.add(schemCorz);
               }
            }

            this.samplerGhostItems.sort((schemCorz2, schemCorz3) -> Double.compare(schemCorz3.distanceSq, schemCorz2.distanceSq));
            this.rebuildFms.samplerghostitemsSize = this.samplerGhostItems.size();
            this.rebuildFms.fmsRebuildValue = this.longOpenHashSet.size();
            this.handleRebuildFms(opacityColorData, list, list2, fmsRebuildValue, vec);
            this.rebuildFms.totalIntArrayLength = 0;
            this.rebuildFms.totalBufferClosed = 0;

            for (SchemCorz schemCorz4 : this.samplerGhostItems) {
               this.rebuildFms.totalIntArrayLength = this.rebuildFms.totalIntArrayLength + schemCorz4.intArray.length;
               this.rebuildFms.totalBufferClosed = this.rebuildFms.totalBufferClosed + schemCorz4.bufferCloseValue;
            }

            this.rebuildFms.update();
            ObjectIterator iterator2 = this.long2ObjectOpenHashMap.values().iterator();

            while (iterator2.hasNext()) {
               SchemCorz next5 = (SchemCorz)iterator2.next();
               this.rebuildFms.totalChunkArrayRebuilt = this.rebuildFms.totalChunkArrayRebuilt + next5.chunkArrayRebuiltCount;
               this.rebuildFms.rebuildFmsValue = this.rebuildFms.rebuildFmsValue + next5.schemCorzCount + next5.visitedChunkCount;
               this.rebuildFms.accumulatedRebuildCount = this.rebuildFms.accumulatedRebuildCount + next5.fmsRebuildCount;
            }

            try {
               this.handleDynamicTexture(opacityColorData, vec);
               return true;
            } catch (Throwable e) {
               this.fmsRebuildActive = false;
               return false;
            }
         }
      } else {
         return false;
      }
   }

   private void handleRebuildFms(OpacityColorData opacityColorData, List<BlockPos> list, List<BlockState> list2, int rebuildFmsValue, Vec3d vec) {
      if (!this.longOpenHashSet.isEmpty() || !this.rebuildFmsLongOpenHashSet.isEmpty()) {
         MinecraftClient instance = MinecraftClient.getInstance();
         ArrayList arrayList = new ArrayList();

         for (SchemCorz schemCorz : this.samplerGhostItems) {
            boolean contains = this.longOpenHashSet.contains(schemCorz.longValue);
            if ((contains || this.rebuildFmsLongOpenHashSet.contains(schemCorz.longValue))
               && (contains || !schemCorz.chunkArrayActive || instance.world.getChunkManager().getChunk(schemCorz.schemCorzValue >> 4, schemCorz.arrayHashValue >> 4, ChunkStatus.FULL, false) != null)) {
               arrayList.add(schemCorz);
            }
         }

         if (arrayList.size() < opacityColorData.rebuildBudget()) {
            LongIterator iterator = this.longOpenHashSet.iterator();

            while (iterator.hasNext()) {
               long next2 = (Long)iterator.next();
               SchemCorz long2objectopenhashmap = (SchemCorz)this.long2ObjectOpenHashMap.get(next2);
               if (long2objectopenhashmap != null && !long2objectopenhashmap.fmsRebuildActive) {
                  long2objectopenhashmap.distanceSq = long2objectopenhashmap.getAmount(vec.x, vec.y, vec.z);
                  arrayList.add(long2objectopenhashmap);
               }
            }
         }

         if (!arrayList.isEmpty()) {
            arrayList.sort((obj, obj2) -> Double.compare(((com.corz.client.render.SchemCorz) obj).distanceSq, ((com.corz.client.render.SchemCorz) obj2).distanceSq));
            BlockRenderViewHelper blockRenderViewHelper = new BlockRenderViewHelper(instance.world, this.blockStatesByPos, opacityColorData);
            BlockBuffer.Context context = new BlockBuffer.Context(
               list,
               list2,
               this.long2ByteOpenHashMap,
               blockRenderViewHelper,
               opacityColorData,
               rebuildFmsValue,
               opacityColorData.correctColor(),
               opacityColorData.missingColor(),
               opacityColorData.wrongBlockColor(),
               opacityColorData.wrongStateColor(),
               opacityColorData.extraColor()
            );
            int rebuildBudget = opacityColorData.rebuildBudget();
            long nanoTime = System.nanoTime();
            long rebuildFmsValue3 = nanoTime + 8333333L;

            for (SchemCorz schemCorz2 : (Iterable<com.corz.client.render.SchemCorz>) (arrayList)) {
               if (rebuildBudget-- <= 0 || this.rebuildFms.fmsRebuildCount > 0 && System.nanoTime() > rebuildFmsValue3) {
                  break;
               }

               if (this.rebuildFmsLongOpenHashSet.contains(schemCorz2.longValue)) {
                  this.handleChunkArray(schemCorz2, list, list2, instance, opacityColorData);
                  this.rebuildFms.rebuildCallCount++;
               }

               this.blockBuffer.handleBufferClose(schemCorz2, context);
               schemCorz2.hashOpenActive = false;
               this.longOpenHashSet.remove(schemCorz2.longValue);
               if (schemCorz2.chunkArrayCount == 0) {
                  this.rebuildFmsLongOpenHashSet.remove(schemCorz2.longValue);
               }

               this.rebuildFms.fmsRebuildCount++;
            }

            this.rebuildFms.fmsRebuildDeadline = System.nanoTime() - nanoTime;
         }
      }
   }

   private Frustum getFrustum(Vec3d vec) {
      return FrustumUtils.getFrustum();
   }

   private void handleBufferAlloc(CommandEncoder commandEncoder, OpacityColorData opacityColorData) {
      if (this.memAlloc == null) {
         this.memAlloc = MemoryUtil.memAlloc(32);
         this.buffer = RenderSystem.getDevice().createBuffer(() -> "corz_schem_fill_ubo", 136, 32L);
         this.bufferBufferAlloc = RenderSystem.getDevice().createBuffer(() -> "corz_schem_fill_ubo_occ", 136, 32L);
      }

      if (opacityColorData.isLitematicaStyle()) {
         this.handleAllocMem(commandEncoder, this.buffer, 1.0F, 0.0F, 1.0F, 1.0F);
         this.handleAllocMem(commandEncoder, this.bufferBufferAlloc, 1.0F, 0.0F, 1.0F, 1.0F);
      } else {
         float offsetArray = this.getOffsetArray(opacityColorData);
         float hideInterior = opacityColorData.getHideInterior(offsetArray);
         float interiorFade = opacityColorData.getInteriorFade(offsetArray);
         float hideCamera = opacityColorData.getHideCamera();
         this.handleAllocMem(commandEncoder, this.buffer, opacityColorData.ghostOpacity(), hideInterior, interiorFade, hideCamera);
         this.handleAllocMem(commandEncoder, this.bufferBufferAlloc, opacityColorData.occludedOpacity(), 0.0F, 0.0F, 1.0F);
      }
   }

   private void handleGhostSampler(
      CommandEncoder commandEncoder,
      Framebuffer framebuffer,
      OpacityColorData opacityColorData,
      Vec3d vec,
      RenderPipeline renderPipeline,
      GpuTextureView gpuTextureView,
      GpuSampler gpuSampler,
      GpuTextureView gpuTextureView2,
      GpuSampler gpuSampler2,
      float ghostSamplerValue,
      boolean ghostSamplerFlag
   ) {
      if (!(ghostSamplerValue <= 0.003F)) {
         GpuBuffer gpuBuffer = ghostSamplerFlag ? this.bufferBufferAlloc : this.buffer;
         if (gpuBuffer != null) {
            try (RenderPass renderPass = commandEncoder.createRenderPass(
                  () -> "corz:schem_ghost", framebuffer.getColorAttachmentView(), OptionalInt.empty(), framebuffer.getDepthAttachmentView(), OptionalDouble.empty()
               )) {
               renderPass.setPipeline(renderPipeline);
               RenderSystem.bindDefaultUniforms(renderPass);
               renderPass.bindTexture("Sampler0", gpuTextureView, gpuSampler);
               renderPass.setUniform("CorzGhostData", gpuBuffer);

               for (BlockRenderLayer blockRenderLayer : ghostSamplerBlockRenderLayer) {
                  if (!blockRenderLayer.isTranslucent()) {
                     this.handleGhostSampler(renderPass, blockRenderLayer);
                  }
               }

               for (BlockRenderLayer ghostSamplerBlockRenderLayerItem : ghostSamplerBlockRenderLayer) {
                  if (ghostSamplerBlockRenderLayerItem.isTranslucent()) {
                     this.handleGhostSampler(renderPass, ghostSamplerBlockRenderLayerItem);
                  }
               }

               if (gpuTextureView2 != null && gpuSampler2 != null) {
                  boolean ghostSamplerFlag2 = false;

                  for (SchemCorz schemCorz : this.samplerGhostItems) {
                     if (schemCorz.corzSchemGhostChest != null && !schemCorz.corzSchemGhostChest.isBuffer()) {
                        ghostSamplerFlag2 = true;
                        break;
                     }
                  }

                  if (ghostSamplerFlag2) {
                     renderPass.bindTexture("Sampler0", gpuTextureView2, gpuSampler2);

                     for (int i = 0; i < this.samplerGhostItems.size(); i++) {
                        BufferGpu corzSchemGhostChest = this.samplerGhostItems.get(i).corzSchemGhostChest;
                        if (corzSchemGhostChest != null && !corzSchemGhostChest.isBuffer()) {
                           this.handleTransformsDynamic(renderPass, i, false);
                           this.handleBufferFms(renderPass, corzSchemGhostChest);
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public void updateHashOpen() {
      ObjectIterator iterator = this.long2ObjectOpenHashMap.values().iterator();

      while (iterator.hasNext()) {
         SchemCorz schemCorz = (SchemCorz)iterator.next();
         schemCorz.hashOpenActive = true;
         this.rebuildFmsLongOpenHashSet.add(schemCorz.longValue);
         this.longOpenHashSet.add(schemCorz.longValue);
      }
   }

   public void handle(int hashOpenValue, int hashOpenValue2, int hashOpenValue3) {
      if (this.canHashOpen(hashOpenValue, hashOpenValue2, hashOpenValue3)) {
         this.handleHashOpen(SchemCorz.getTime(hashOpenValue, hashOpenValue2, hashOpenValue3));
         if ((hashOpenValue & 15) == 0) {
            this.handleHashOpen(SchemCorz.getTime(hashOpenValue - 1, hashOpenValue2, hashOpenValue3));
         }

         if ((hashOpenValue & 15) == 15) {
            this.handleHashOpen(SchemCorz.getTime(hashOpenValue + 1, hashOpenValue2, hashOpenValue3));
         }

         if ((hashOpenValue2 & 15) == 0) {
            this.handleHashOpen(SchemCorz.getTime(hashOpenValue, hashOpenValue2 - 1, hashOpenValue3));
         }

         if ((hashOpenValue2 & 15) == 15) {
            this.handleHashOpen(SchemCorz.getTime(hashOpenValue, hashOpenValue2 + 1, hashOpenValue3));
         }

         if ((hashOpenValue3 & 15) == 0) {
            this.handleHashOpen(SchemCorz.getTime(hashOpenValue, hashOpenValue2, hashOpenValue3 - 1));
         }

         if ((hashOpenValue3 & 15) == 15) {
            this.handleHashOpen(SchemCorz.getTime(hashOpenValue, hashOpenValue2, hashOpenValue3 + 1));
         }
      }
   }

   private void handleTransformsDynamic(RenderPass renderPass, int index, boolean transformsDynamicFlag) {
      List list = transformsDynamicFlag && index < this.transformsDynamicGpuBufferSl.size() ? this.transformsDynamicGpuBufferSl : this.dynamicTextureGpuBufferSlice;
      renderPass.setUniform("DynamicTransforms", (GpuBufferSlice)list.get(index));
   }

   public void handleHashOpen(List<BlockPos> list, List<BlockState> list2, List<int[]> wordListed) {
      this.updateHashOpenLong2ObjectOpenHashMap();
      this.long2ByteOpenHashMap.clear();
      this.blockStatesByPos.clear();
      this.rebuildFmsLongOpenHashSet.clear();
      this.longOpenHashSet.clear();
      this.arrayHashIntArrayList.clear();
      this.arrayHashIntArrayList.addAll(wordListed);
      this.blockStatesByPos.ensureCapacity(list.size());
      Long2ObjectOpenHashMap long2ObjectOpenHashMap = new Long2ObjectOpenHashMap();

      for (int i = 0; i < list.size(); i++) {
         BlockPos position = (BlockPos)list.get(i);
         BlockState blockState = (BlockState)list2.get(i);
         if (blockState != null) {
            this.blockStatesByPos.put(position.asLong(), blockState);
            long hashOpenValue = SchemCorz.getTime(position.getX(), position.getY(), position.getZ());
            SchemCorz long2objectopenhashmap = (SchemCorz)this.long2ObjectOpenHashMap.get(hashOpenValue);
            if (long2objectopenhashmap == null) {
               SchemCorz schemCorz = new SchemCorz(hashOpenValue, position.getX() >> 4 << 4, position.getY() >> 4 << 4, position.getZ() >> 4 << 4);
               this.long2ObjectOpenHashMap.put(hashOpenValue, schemCorz);
            }

            ((IntArrayList)long2ObjectOpenHashMap.computeIfAbsent(hashOpenValue, hashOpenValue2 -> new IntArrayList())).add(i);
         }
      }

      int hashOpenValue3 = 262144;

      for (int[] hashOpenValues : this.arrayHashIntArrayList) {
         long hashOpenFactor = (long)(hashOpenValues[3] >> 4) - (hashOpenValues[0] >> 4) + 1L;
         long hashOpenFactor2 = (long)(hashOpenValues[4] >> 4) - (hashOpenValues[1] >> 4) + 1L;
         long hashOpenFactor3 = (long)(hashOpenValues[5] >> 4) - (hashOpenValues[2] >> 4) + 1L;
         if (hashOpenFactor * hashOpenFactor2 * hashOpenFactor3 <= 262144L) {
            for (int j = hashOpenValues[0] >> 4; j <= hashOpenValues[3] >> 4; j++) {
               for (int m = hashOpenValues[1] >> 4; m <= hashOpenValues[4] >> 4; m++) {
                  for (int p = hashOpenValues[2] >> 4; p <= hashOpenValues[5] >> 4; p++) {
                     long long2 = BlockPos.asLong(j, m, p);
                     if (!this.long2ObjectOpenHashMap.containsKey(long2)) {
                        this.long2ObjectOpenHashMap.put(long2, new SchemCorz(long2, j << 4, m << 4, p << 4));
                     }
                  }
               }
            }
         }
      }

      ObjectIterator iterator = this.long2ObjectOpenHashMap.values().iterator();

      while (iterator.hasNext()) {
         SchemCorz next2 = (SchemCorz)iterator.next();
         IntArrayList intArrayList = (IntArrayList)long2ObjectOpenHashMap.get(next2.longValue);
         next2.hashOpenTable = intArrayList == null ? new int[0] : intArrayList.toIntArray();
         next2.unpackExtraTable = new byte[next2.hashOpenTable.length];
         Arrays.fill(next2.unpackExtraTable, (byte)BlockStateType.UNKNOWN.ordinal());
         next2.hashOpenActive = true;
         this.rebuildFmsLongOpenHashSet.add(next2.longValue);
         this.longOpenHashSet.add(next2.longValue);
      }
   }

   public HashOpenSchem() {
      this.blockBuffer = new BlockBuffer();
      this.rebuildFms = new RebuildFms();
      this.rebuildFmsLongOpenHashSet = new LongOpenHashSet();
      this.longOpenHashSet = new LongOpenHashSet();
      this.samplerGhostItems = new ArrayList<>();
      this.dynamicTextureGpuBufferSlice = new ArrayList<>();
      this.transformsDynamicGpuBufferSl = new ArrayList<>();
      this.transformsDynamicItems = new ArrayList<>();
      this.arrayHashIntArrayList = new ArrayList<>();
      this.longArrayList = new LongArrayList();
      this.arrayHashLongOpenHashSet = new LongOpenHashSet();
      this.lastRebuildTime = Long.MIN_VALUE;
      this.fmsRebuildActive = true;
   }

   public void updateClearTransforms() {
      this.updateHashOpenLong2ObjectOpenHashMap();
      this.long2ByteOpenHashMap.clear();
      this.blockStatesByPos.clear();
      this.rebuildFmsLongOpenHashSet.clear();
      this.longOpenHashSet.clear();
      this.samplerGhostItems.clear();
      this.dynamicTextureGpuBufferSlice.clear();
      this.transformsDynamicGpuBufferSl.clear();
      this.transformsDynamicItems.clear();
      this.arrayHashIntArrayList.clear();
      this.lastRebuildTime = Long.MIN_VALUE;
      this.fmsRebuildActive = true;
   }

   private float getOffsetArray(OpacityColorData opacityColorData) {
      SchemCorz long2objectopenhashmap = (SchemCorz)this.long2ObjectOpenHashMap.get(this.fmsRebuildValue);
      if (long2objectopenhashmap == null) {
         return 1.0F;
      } else {
         int offsetArrayTotal = long2objectopenhashmap.hashOpenTable.length;
         int offsetArrayCounter = 1;

         for (Direction direction : offsetArrayDirections) {
            SchemCorz long2objectopenhashmap2 = (SchemCorz)this.long2ObjectOpenHashMap
               .get(
                  SchemCorz.getTime(
                     long2objectopenhashmap.schemCorzValue + direction.getOffsetX() * 16 + 8, long2objectopenhashmap.matrixStackValue + direction.getOffsetY() * 16 + 8, long2objectopenhashmap.arrayHashValue + direction.getOffsetZ() * 16 + 8
                  )
               );
            if (long2objectopenhashmap2 != null) {
               offsetArrayTotal += long2objectopenhashmap2.hashOpenTable.length;
               offsetArrayCounter++;
            }
         }

         float offsetArrayValue = (float)offsetArrayTotal / (offsetArrayCounter * 4096);
         return 1.0F + Math.min(1.0F, offsetArrayValue * 2.2F);
      }
   }

   private void handleGhostSampler(RenderPass renderPass, BlockRenderLayer blockRenderLayer) {
      for (int i = 0; i < this.samplerGhostItems.size(); i++) {
         BufferGpu bufferGpu = this.samplerGhostItems.get(i).transformsDynamicBufferGpus[blockRenderLayer.ordinal()];
         if (bufferGpu != null && !bufferGpu.isBuffer()) {
            this.handleTransformsDynamic(renderPass, i, false);
            this.handleBufferFms(renderPass, bufferGpu);
         }
      }
   }

   private void handleBufferFms(RenderPass renderPass, BufferGpu bufferGpu) {
      renderPass.setVertexBuffer(0, bufferGpu.getBuffer());
      ShapeIndexBuffer sequentialBuffer = RenderSystem.getSequentialBuffer(bufferGpu.mode);
      renderPass.setIndexBuffer(sequentialBuffer.getIndexBuffer(bufferGpu.indexCount), sequentialBuffer.getIndexType());
      renderPass.drawIndexed(0, 0, bufferGpu.indexCount, 1);
      this.rebuildFms.bufferFmsCount++;
   }

   private void handleAllocMem(CommandEncoder commandEncoder, GpuBuffer gpuBuffer, float allocMemValue, float hideInterior, float interiorFade, float hideCamera) {
      this.memAlloc.clear();
      this.memAlloc.putFloat(1.0F).putFloat(1.0F).putFloat(1.0F).putFloat(allocMemValue);
      this.memAlloc.putFloat(hideInterior).putFloat(interiorFade).putFloat(hideCamera).putFloat(0.06F);
      this.memAlloc.flip();
      commandEncoder.writeToBuffer(gpuBuffer.slice(0L, 32L), this.memAlloc);
   }

   private static RenderPipeline getRegister(BlockRenderLayer blockRenderLayer) {
      return switch (blockRenderLayer) {
         case SOLID -> BuilderSchemUtils.registerSamplerTransforms;
         case CUTOUT -> BuilderSchemUtils.builderSchemRenderPipeline;
         case TRANSLUCENT, TRIPWIRE -> BuilderSchemUtils.samplerTransformsRenderPipel;
      };
   }

   private void handleSamplerTransforms(
      CommandEncoder commandEncoder,
      Framebuffer framebuffer,
      OpacityColorData opacityColorData,
      MinecraftClient client,
      GpuTextureView gpuTextureView,
      GpuSampler gpuSampler,
      GpuTextureView gpuTextureView2,
      GpuSampler gpuSampler2
   ) {
      boolean samplerTransformsFlag = false;

      for (SchemCorz schemCorz : this.samplerGhostItems) {
         for (BufferGpu bufferGpu : schemCorz.transformsDynamicBufferGpus) {
            if (bufferGpu != null && !bufferGpu.isBuffer()) {
               samplerTransformsFlag = true;
               break;
            }
         }

         if (samplerTransformsFlag || schemCorz.corzSchemGhostChest != null && !schemCorz.corzSchemGhostChest.isBuffer()) {
            samplerTransformsFlag = true;
            break;
         }
      }

      boolean litematicaTranslucent = opacityColorData.litematicaTranslucent();
      if (samplerTransformsFlag && opacityColorData.ghostOpacity() > 0.003F) {
         GpuTextureView glTextureView = client.gameRenderer.getLightmapTextureManager().getGlTextureView();
         GpuSampler samplercache = RenderSystem.getSamplerCache().get(FilterMode.LINEAR);

         try (RenderPass renderPass = commandEncoder.createRenderPass(
               () -> "corz:schem_lit", framebuffer.getColorAttachmentView(), OptionalInt.empty(), framebuffer.getDepthAttachmentView(), OptionalDouble.empty()
            )) {
            RenderSystem.bindDefaultUniforms(renderPass);
            renderPass.bindTexture("Sampler2", glTextureView, samplercache);
            renderPass.bindTexture("Sampler0", gpuTextureView, gpuSampler);

            for (BlockRenderLayer blockRenderLayer : ghostSamplerBlockRenderLayer) {
               renderPass.setPipeline(litematicaTranslucent ? BuilderSchemUtils.samplerTransformsRenderPipel : getRegister(blockRenderLayer));

               for (int i = 0; i < this.samplerGhostItems.size(); i++) {
                  BufferGpu bufferGpu2 = this.samplerGhostItems.get(i).transformsDynamicBufferGpus[blockRenderLayer.ordinal()];
                  if (bufferGpu2 != null && !bufferGpu2.isBuffer()) {
                     renderPass.setUniform("DynamicTransforms", this.transformsDynamicItems.get(i));
                     this.handleBufferFms(renderPass, bufferGpu2);
                  }
               }
            }

            if (gpuTextureView2 != null && gpuSampler2 != null) {
               boolean samplerTransformsFlag2 = false;

               for (int j = 0; j < this.samplerGhostItems.size(); j++) {
                  BufferGpu corzSchemGhostChest = this.samplerGhostItems.get(j).corzSchemGhostChest;
                  if (corzSchemGhostChest != null && !corzSchemGhostChest.isBuffer()) {
                     if (!samplerTransformsFlag2) {
                        renderPass.setPipeline(litematicaTranslucent ? BuilderSchemUtils.samplerTransformsRenderPipel : BuilderSchemUtils.registerSamplerTransforms);
                        renderPass.bindTexture("Sampler0", gpuTextureView2, gpuSampler2);
                        samplerTransformsFlag2 = true;
                     }

                     renderPass.setUniform("DynamicTransforms", this.transformsDynamicItems.get(j));
                     this.handleBufferFms(renderPass, corzSchemGhostChest);
                  }
               }
            }
         }
      }

      boolean missingThroughWalls = opacityColorData.missingThroughWalls();
      if (opacityColorData.outlines()) {
         this.handleGhostCorz(commandEncoder, framebuffer, opacityColorData, null, missingThroughWalls ? BuilderSchemUtils.renderPipeline : GhostBuilderUtils.RENDER_PIPELINE, false, true);
      }

      this.handleGhostCorz(commandEncoder, framebuffer, opacityColorData, null, missingThroughWalls ? BuilderSchemUtils.register : GhostBuilderUtils.CORZ_SCHEM, false, false);
   }

   private boolean canHashOpen(int hashOpenValue, int hashOpenValue2, int hashOpenValue3) {
      return this.canFlag(hashOpenValue, hashOpenValue2, hashOpenValue3)
         || this.blockStatesByPos.containsKey(BlockPos.asLong(hashOpenValue, hashOpenValue2, hashOpenValue3))
         || this.blockStatesByPos.containsKey(BlockPos.asLong(hashOpenValue + 1, hashOpenValue2, hashOpenValue3))
         || this.blockStatesByPos.containsKey(BlockPos.asLong(hashOpenValue - 1, hashOpenValue2, hashOpenValue3))
         || this.blockStatesByPos.containsKey(BlockPos.asLong(hashOpenValue, hashOpenValue2 + 1, hashOpenValue3))
         || this.blockStatesByPos.containsKey(BlockPos.asLong(hashOpenValue, hashOpenValue2 - 1, hashOpenValue3))
         || this.blockStatesByPos.containsKey(BlockPos.asLong(hashOpenValue, hashOpenValue2, hashOpenValue3 + 1))
         || this.blockStatesByPos.containsKey(BlockPos.asLong(hashOpenValue, hashOpenValue2, hashOpenValue3 - 1));
   }

   private boolean canFlag(int hashOpenLimit, int hashOpenLimit2, int hashOpenLimit3) {
      for (int i = 0; i < this.arrayHashIntArrayList.size(); i++) {
         int[] hashOpenValues = this.arrayHashIntArrayList.get(i);
         if (hashOpenLimit >= hashOpenValues[0] && hashOpenLimit <= hashOpenValues[3] && hashOpenLimit2 >= hashOpenValues[1] && hashOpenLimit2 <= hashOpenValues[4] && hashOpenLimit3 >= hashOpenValues[2] && hashOpenLimit3 <= hashOpenValues[5]) {
            return true;
         }
      }

      return false;
   }

   public RebuildFms getRebuildFms() {
      return this.rebuildFms;
   }

   private void handleHashOpen(long hashOpenValue) {
      if (this.long2ObjectOpenHashMap.containsKey(hashOpenValue)) {
         this.rebuildFmsLongOpenHashSet.add(hashOpenValue);
         this.longOpenHashSet.add(hashOpenValue);
      }
   }

   public void handle(List<BlockPos> list, List<BlockState> list2) {
      this.handleHashOpen(list, list2, List.of());
   }

   private void handleDynamicTexture(OpacityColorData opacityColorData, Vec3d vec) {
      MinecraftClient instance = MinecraftClient.getInstance();
      CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
      Framebuffer framebuffer = instance.getFramebuffer();
      this.samplerGhostItems.removeIf(SchemCorz::isSchemCorz);
      this.rebuildFms.samplerghostitemsSize = this.samplerGhostItems.size();
      this.dynamicTextureGpuBufferSlice.clear();
      this.transformsDynamicGpuBufferSl.clear();
      this.transformsDynamicItems.clear();
      boolean litematicaStyle = opacityColorData.isLitematicaStyle();
      Vector4f vector4f = new Vector4f(1.0F, 1.0F, 1.0F, opacityColorData.litematicaTranslucent() ? Math.max(0.0F, Math.min(1.0F, opacityColorData.ghostOpacity())) : 1.0F);
      Vector3f vector3f = new Vector3f();
      boolean missingThroughWalls = opacityColorData.missingThroughWalls();
      Vector4f vector4f2 = new Vector4f(1.0F, 1.0F, 1.0F, Math.max(0.0F, Math.min(1.0F, opacityColorData.occludedOpacity())));

      for (SchemCorz schemCorz : this.samplerGhostItems) {
         Matrix4f matrix4f = new Matrix4f(RenderSystem.getModelViewMatrix());
         matrix4f.translate((float)(schemCorz.schemCorzValue - vec.x), (float)(schemCorz.matrixStackValue - vec.y), (float)(schemCorz.arrayHashValue - vec.z));
         this.dynamicTextureGpuBufferSlice.add(RenderSystem.getDynamicUniforms().write(matrix4f, VECTOR4F, VECTOR3F, MATRIX4F));
         if (missingThroughWalls) {
            this.transformsDynamicGpuBufferSl.add(RenderSystem.getDynamicUniforms().write(matrix4f, vector4f2, VECTOR3F, MATRIX4F));
         }

         if (litematicaStyle) {
            vector3f.set((float)(schemCorz.schemCorzValue - vec.x), (float)(schemCorz.matrixStackValue - vec.y), (float)(schemCorz.arrayHashValue - vec.z));
            this.transformsDynamicItems.add(RenderSystem.getDynamicUniforms().write(RenderSystem.getModelViewMatrix(), vector4f, vector3f, MATRIX4F));
         }
      }

      this.handleBufferAlloc(commandEncoder, opacityColorData);
      AbstractTexture texture = instance.getTextureManager().getTexture(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE);
      GpuTextureView glTextureView = texture.getGlTextureView();
      GpuSampler sampler = texture.getSampler();
      GpuTextureView gpuTextureView = null;
      GpuSampler gpuSampler = null;

      try {
         AbstractTexture texture2 = instance.getTextureManager().getTexture(TexturedRenderLayers.CHEST_ATLAS_TEXTURE);
         if (texture2 != null) {
            gpuTextureView = texture2.getGlTextureView();
            gpuSampler = texture2.getSampler();
         }
      } catch (Throwable e) {
      }

      if (litematicaStyle) {
         this.handleSamplerTransforms(commandEncoder, framebuffer, opacityColorData, instance, glTextureView, sampler, gpuTextureView, gpuSampler);
      } else {
         if (opacityColorData.missingThroughWalls()) {
            this.handleGhostSampler(commandEncoder, framebuffer, opacityColorData, vec, GhostBuilderUtils.GHOST_OCCLUDED_PIPELINE, glTextureView, sampler, gpuTextureView, gpuSampler, opacityColorData.occludedOpacity(), true);
            this.handleGhostCorz(commandEncoder, framebuffer, opacityColorData, vec, GhostBuilderUtils.OVERLAY_QUADS_OCCLUDED_PIPELINE, true, false);
            if (opacityColorData.outlines()) {
               this.handleGhostCorz(commandEncoder, framebuffer, opacityColorData, vec, GhostBuilderUtils.DEPTH_BIAS, true, true);
            }
         }

         RenderPipeline renderPipeline = GhostBuilderUtils.GHOST_CORZ;
         this.handleGhostSampler(commandEncoder, framebuffer, opacityColorData, vec, renderPipeline, glTextureView, sampler, gpuTextureView, gpuSampler, opacityColorData.ghostOpacity(), false);
         this.handleGhostCorz(commandEncoder, framebuffer, opacityColorData, vec, GhostBuilderUtils.CORZ_SCHEM, false, false);
         if (opacityColorData.outlines()) {
            this.handleGhostCorz(commandEncoder, framebuffer, opacityColorData, vec, GhostBuilderUtils.RENDER_PIPELINE, false, true);
         }
      }
   }

   private void handleChunkArray(SchemCorz schemCorz, List<BlockPos> list, List<BlockState> list2, MinecraftClient client, OpacityColorData opacityColorData) {
      schemCorz.fmsRebuildCount = schemCorz.chunkArrayRebuiltCount = schemCorz.schemCorzCount = schemCorz.visitedChunkCount = schemCorz.arrayHashCount = schemCorz.chunkArrayCount = 0;
      boolean litematicaStyle = opacityColorData.isLitematicaStyle();
      boolean hasChunk = client.world.getChunkManager().getChunk(schemCorz.schemCorzValue >> 4, schemCorz.arrayHashValue >> 4, ChunkStatus.FULL, false) != null;
      schemCorz.chunkArrayActive = !hasChunk;

      for (int i = 0; i < schemCorz.hashOpenTable.length; i++) {
         int index = schemCorz.hashOpenTable[i];
         BlockPos blockPos = (BlockPos)list.get(index);
         BlockState cfg2 = (BlockState)list2.get(index);
         BlockState blockState = hasChunk ? client.world.getBlockState(blockPos) : null;
         BlockStateType blockStateType = litematicaStyle ? BlockStateType.getCorrectAir(cfg2, blockState, hasChunk) : BlockStateType.getCorrectReplaceable(cfg2, blockState, hasChunk);
         schemCorz.unpackExtraTable[i] = (byte)blockStateType.ordinal();
         this.long2ByteOpenHashMap.put(blockPos.asLong(), (byte)blockStateType.ordinal());
         switch (blockStateType) {
            case CORRECT:
               schemCorz.fmsRebuildCount++;
               break;
            case MISSING:
               schemCorz.chunkArrayRebuiltCount++;
               break;
            case WRONG_BLOCK:
               schemCorz.schemCorzCount++;
               break;
            case WRONG_STATE:
               schemCorz.visitedChunkCount++;
               break;
            case EXTRA:
               schemCorz.arrayHashCount++;
               break;
            case UNKNOWN:
               schemCorz.chunkArrayCount++;
         }
      }

      schemCorz.longArray = SchemCorz.chunkArrayTable;
      if (litematicaStyle && !this.arrayHashIntArrayList.isEmpty()) {
         this.handleArrayHash(schemCorz, client);
      }
   }

   private void handleGhostCorz(
      CommandEncoder commandEncoder, Framebuffer framebuffer, OpacityColorData opacityColorData, Vec3d vec, RenderPipeline renderPipeline, boolean ghostCorzFlag, boolean ghostCorzFlag2
   ) {
      boolean ghostCorzFlag3 = false;

      for (SchemCorz schemCorz : this.samplerGhostItems) {
         BufferGpu bufferGpu = ghostCorzFlag2 ? schemCorz.corzSchemOutline : schemCorz.corzSchemOverlay;
         if (bufferGpu != null && !bufferGpu.isBuffer()) {
            ghostCorzFlag3 = true;
            break;
         }
      }

      if (ghostCorzFlag3) {
         try (RenderPass renderPass = commandEncoder.createRenderPass(
               () -> "corz:schem_overlay", framebuffer.getColorAttachmentView(), OptionalInt.empty(), framebuffer.getDepthAttachmentView(), OptionalDouble.empty()
            )) {
            renderPass.setPipeline(renderPipeline);
            RenderSystem.bindDefaultUniforms(renderPass);
            if (!ghostCorzFlag2) {
               GpuBuffer gpuBuffer = ghostCorzFlag ? this.bufferBufferAlloc : this.buffer;
               if (gpuBuffer != null) {
                  renderPass.setUniform("CorzGhostData", gpuBuffer);
               }
            }

            for (int i = 0; i < this.samplerGhostItems.size(); i++) {
               SchemCorz samplerghostitem = this.samplerGhostItems.get(i);
               BufferGpu bufferGpu2 = ghostCorzFlag2 ? samplerghostitem.corzSchemOutline : samplerghostitem.corzSchemOverlay;
               if (bufferGpu2 != null && !bufferGpu2.isBuffer()) {
                  this.handleTransformsDynamic(renderPass, i, ghostCorzFlag);
                  this.handleBufferFms(renderPass, bufferGpu2);
               }
            }
         }
      }
   }

   @Override
   public void close() {
      this.updateClearTransforms();
      this.blockBuffer.close();
      if (this.buffer != null) {
         this.buffer.close();
         this.buffer = null;
      }

      if (this.memAlloc != null) {
         MemoryUtil.memFree(this.memAlloc);
         this.memAlloc = null;
      }
   }

   private void updateHashOpenLong2ObjectOpenHashMap() {
      ObjectIterator iterator = this.long2ObjectOpenHashMap.values().iterator();

      while (iterator.hasNext()) {
         SchemCorz schemCorz = (SchemCorz)iterator.next();
         schemCorz.close();
      }

      this.long2ObjectOpenHashMap.clear();
   }
}
