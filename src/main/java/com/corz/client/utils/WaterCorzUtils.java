package com.corz.client.utils;

import com.corz.client.utils.HashOpenAlloc.Region;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import com.mojang.blaze3d.pipeline.BlendFunction;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.platform.DepthTestFunction;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.systems.RenderSystem.ShapeIndexBuffer;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTextureView;
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.nio.ByteBuffer;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents.Load;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientChunkEvents.Unload;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents.EndExtraction;
import net.minecraft.block.enums.CameraSubmersionType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.GpuSampler;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.state.SkyRenderState;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.dimension.DimensionType.Skybox;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;
import com.corz.client.categories.visuals.RealisticWater;
import com.corz.client.utils.VectorMatrixUtils;
import com.corz.client.utils.HashOpenAlloc;
import com.corz.client.utils.FluidStateHelper;
import com.corz.client.utils.ChunkHash;
import com.corz.client.utils.ReflectionSimpleUtils;
import com.corz.client.utils.AmountsWaterUtils;

public final class WaterCorzUtils {
   private static HashOpenAlloc hashChunkHashOpenAlloc;
   public static final RenderPipeline build = RenderPipeline.builder(RenderPipelines.TRANSFORMS_PROJECTION_FOG_SNIPPET)
      .withLocation(Identifier.of("corzclient", "pipeline/realistic_water"))
      .withVertexShader(Identifier.of("corzclient", "core/corz_water"))
      .withFragmentShader(Identifier.of("corzclient", "core/corz_water"))
      .withUniform("CorzWater", UniformType.UNIFORM_BUFFER)
      .withSampler("Sampler0")
      .withSampler("Sampler2")
      .withVertexFormat(FluidStateHelper.lightNormal, DrawMode.QUADS)
      .withBlend(BlendFunction.TRANSLUCENT_PREMULTIPLIED_ALPHA)
      .withCull(false)
      .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
      .withDepthWrite(false)
      .withDepthBias(-1.0F, -2.0F)
      .build();
   private static long samplerWaterTime = -1L;
   private static boolean scanFlag;
   private static ChunkHash chunkHash;
   private static final GpuBufferSlice[] transformsDynamicGpuBufferSlices = new GpuBufferSlice[1024];
   private static final GpuBuffer[] samplerTransformsGpuBuffers = new GpuBuffer[1024];
   private static boolean renderFlag;
   private static final int[] transformsDynamicValues = new int[1024];
   private static boolean chunkActive;
   private static Frustum frustum;
   private static ByteBuffer memAlloc;
   private static final Vector4f VECTOR4F = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
   private static boolean waterCorzActive;
   private static GpuBuffer buffer;
   private static final Vector3f VECTOR3F = new Vector3f();
   private static boolean shaderCompileFlag;
   private static final Matrix4f MATRIX4F = new Matrix4f();
   private static final Vector3f VECTOR3_FVECTOR3F = new Vector3f();
   private static final Vector3f VECTOR3F2 = new Vector3f();
   private static final float[] vectorBufferTable = new float[10];
   private static boolean valid;

   private static float getAmount(float waterCorzValue, float waterCorzValue2, float waterCorzFactor) {
      return waterCorzValue + (waterCorzValue2 - waterCorzValue) * waterCorzFactor;
   }

   private static boolean isShaderValid() {
      if (!shaderCompileFlag) {
         shaderCompileFlag = true;

         try {
            valid = RenderSystem.getDevice().precompilePipeline(build).isValid();
         } catch (Throwable e) {
            valid = false;
         }

         if (!valid) {
            handleScanFlag("shader", new IllegalStateException("corz_water did not compile"));
         }
      }

      return valid;
   }

   private static float getAmountForFloat(float waterCorzValue, float waterCorzValue2, float waterCorzValue3) {
      float starBrightness = getStarBrightness((waterCorzValue3 - waterCorzValue) / (waterCorzValue2 - waterCorzValue));
      return starBrightness * starBrightness * (3.0F - 2.0F * starBrightness);
   }

   private static void updateHashChunk() {
      if (chunkHash == null) {
         hashChunkHashOpenAlloc = new HashOpenAlloc();
         chunkHash = new ChunkHash(hashChunkHashOpenAlloc);
      }
   }

   private static float getStarBrightness(float starBrightnessLimit) {
      return starBrightnessLimit < 0.0F ? 0.0F : (starBrightnessLimit > 1.0F ? 1.0F : starBrightnessLimit);
   }

   private static void handleTransformsDynamic(MinecraftClient client, WorldRenderContext worldRenderContext, RealisticWater realisticWater, long transformsDynamicValue, float maxTime) {
      Camera camera = client.gameRenderer.getCamera();
      if (camera.getSubmersionType() != CameraSubmersionType.WATER) {
         if (isShaderValid()) {
            chunkHash.handleChunkWorld(client, 2);
            Vec3d cameraPos = camera.getCameraPos();
            int packedHashValue = (int)Math.floor(cameraPos.x) >> 4 >> 2;
            int transformsDynamicValue4 = (int)Math.floor(cameraPos.z) >> 4 >> 2;
            hashChunkHashOpenAlloc.getPackedHash(chunkHash.long2ObjectOpenHashMap, packedHashValue, transformsDynamicValue4, 8, 1500000L, transformsDynamicValue);
            if (!hashChunkHashOpenAlloc.isEmpty()) {
               float transformsDynamicFactor = chunkHash.getMax() * 16.0F - 6.0F;
               float max5 = Math.max(8.0F, transformsDynamicFactor - 64.0F);
               Matrix4f modelViewMatrix = RenderSystem.getModelViewMatrix();
               int index = 0;
               int transformsDynamicBound = 0;

               for (int i = 0; i < hashChunkHashOpenAlloc.hashRegionList.size() && index < 1024; i++) {
                  Region region = hashChunkHashOpenAlloc.hashRegionList.get(i);
                  if (region.buffer != null && region.vertexCount != 0 && region.box != null && (frustum == null || frustum.isVisible(region.box))) {
                     double max2 = Math.max(Math.max(region.box.minX - cameraPos.x, cameraPos.x - region.box.maxX), 0.0);
                     double max3 = Math.max(Math.max(region.box.minZ - cameraPos.z, cameraPos.z - region.box.maxZ), 0.0);
                     if (!(max2 * max2 + max3 * max3 > (double)transformsDynamicFactor * transformsDynamicFactor)) {
                        float transformsDynamicFactor4 = region.fadeStartNanos < 0L ? 1.0F : Math.min(1.0F, (float)(transformsDynamicValue - region.fadeStartNanos) / 4.5E8F);
                        VECTOR4F.set(1.0F, 1.0F, 1.0F, transformsDynamicFactor4 * transformsDynamicFactor4 * (3.0F - 2.0F * transformsDynamicFactor4));
                        VECTOR3F.set((float)(region.getPacked() - cameraPos.x), (float)(-cameraPos.y), (float)(region.getPackedZ() - cameraPos.z));
                        transformsDynamicGpuBufferSlices[index] = RenderSystem.getDynamicUniforms().write(modelViewMatrix, VECTOR4F, VECTOR3F, MATRIX4F);
                        samplerTransformsGpuBuffers[index] = region.buffer;
                        int rocketsBuyBound = region.vertexCount / 4 * 6;
                        transformsDynamicValues[index] = rocketsBuyBound;
                        transformsDynamicBound = Math.max(transformsDynamicBound, rocketsBuyBound);
                        index++;
                     }
                  }
               }

               if (index != 0) {
                  realisticWater.handleWavesAmount(transformsDynamicValue);
                  float transformsDynamicLimit = realisticWater.getWavesAmount();
                  GpuTextureView glTextureView = client.gameRenderer.getLightmapTextureManager().getGlTextureView();
                  GpuTextureView gpuTextureView = glTextureView;
                  if (transformsDynamicLimit > 0.001F) {
                     if (ReflectionSimpleUtils.canReflectionSky(transformsDynamicValue)) {
                        gpuTextureView = ReflectionSimpleUtils.getAttachmentView();
                     } else {
                        transformsDynamicLimit = 0.0F;
                     }
                  } else {
                     transformsDynamicLimit = 0.0F;
                  }

                  if (gpuTextureView != null && glTextureView != null) {
                     handleVectorBuffer(worldRenderContext.worldState().skyRenderState, realisticWater, transformsDynamicLimit, maxTime, max5, transformsDynamicFactor, transformsDynamicValue);
                     Framebuffer translucentFramebuffer = worldRenderContext.worldRenderer().getTranslucentFramebuffer();
                     if (translucentFramebuffer == null) {
                        translucentFramebuffer = client.getFramebuffer();
                     }

                     if (translucentFramebuffer.getColorAttachmentView() != null && translucentFramebuffer.getDepthAttachmentView() != null) {
                        ShapeIndexBuffer sequentialBuffer = RenderSystem.getSequentialBuffer(DrawMode.QUADS);
                        GpuBuffer indexBuffer = sequentialBuffer.getIndexBuffer(transformsDynamicBound);
                        GpuSampler samplercache = RenderSystem.getSamplerCache().get(FilterMode.LINEAR);
                        CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
                        boolean transformsDynamicFlag = false ;

                        try {
                           transformsDynamicFlag = true;

                           try (RenderPass renderPass = commandEncoder.createRenderPass(
                                 () -> "corz:realistic_water",
                                 translucentFramebuffer.getColorAttachmentView(),
                                 OptionalInt.empty(),
                                 translucentFramebuffer.getDepthAttachmentView(),
                                 OptionalDouble.empty()
                              )) {
                              renderPass.setPipeline(build);
                              RenderSystem.bindDefaultUniforms(renderPass);
                              renderPass.setUniform("CorzWater", buffer);
                              renderPass.bindTexture("Sampler0", gpuTextureView, samplercache);
                              renderPass.bindTexture("Sampler2", glTextureView, samplercache);
                              renderPass.setIndexBuffer(indexBuffer, sequentialBuffer.getIndexType());

                              for (int j = 0; j < index; j++) {
                                 renderPass.setUniform("DynamicTransforms", transformsDynamicGpuBufferSlices[j]);
                                 renderPass.setVertexBuffer(0, samplerTransformsGpuBuffers[j]);
                                 renderPass.drawIndexed(0, 0, transformsDynamicValues[j], 1);
                              }
                           }
                        } finally {
                           if (transformsDynamicFlag) {
                              for (int k = 0; k < index; k++) {
                                 transformsDynamicGpuBufferSlices[k] = null;
                                 samplerTransformsGpuBuffers[k] = null;
                              }
                           }
                        }

                        for (int m = 0; m < index; m++) {
                           transformsDynamicGpuBufferSlices[m] = null;
                           samplerTransformsGpuBuffers[m] = null;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static float getMoonPhase(SkyRenderState skyRenderState) {
      if (skyRenderState.moonPhase == null) {
         return 0.6F;
      } else {
         int ordinal = skyRenderState.moonPhase.ordinal();
         int moonPhaseValue = Math.min(ordinal, 8 - ordinal);
         return 0.08F + 0.92F * (1.0F - moonPhaseValue / 4.0F);
      }
   }

   public static void handleScanChunk(MinecraftClient client) {
      if (!waterCorzActive) {
         scanFlag = true;

         try {
            updateHashChunk();
            chunkHash.handleChunkClamped(client);
         } catch (Throwable e) {
            handleScanFlag("scan", e);
         }
      }
   }

   private WaterCorzUtils() {
   }

   public static void updateMemAlloc() {
      renderFlag = false;
      scanFlag = false;
      if (chunkHash != null) {
         chunkHash.updateHashOpen();
      }

      if (hashChunkHashOpenAlloc != null) {
         hashChunkHashOpenAlloc.updateMemAlloc();
      }

      ReflectionSimpleUtils.updateSimpleFramebuffer();
      if (buffer != null) {
         buffer.close();
         buffer = null;
      }

      if (memAlloc != null) {
         MemoryUtil.memFree(memAlloc);
         memAlloc = null;
      }

      samplerWaterTime = -1L;
   }

   private static void handleVectorBuffer(SkyRenderState skyRenderState, RealisticWater realisticWater, float vectorBufferValue, float maxTime, float vectorBufferValue3, float transformsDynamicFactor, long transformsDynamicValue) {
      if (memAlloc == null) {
         memAlloc = MemoryUtil.memAlloc(144);
      }

      if (buffer == null || buffer.isClosed()) {
         buffer = RenderSystem.getDevice().createBuffer(() -> "corz_water_ubo", 136, 144L);
      }

      if (samplerWaterTime < 0L) {
         samplerWaterTime = transformsDynamicValue;
      }

      double seconds = (transformsDynamicValue - samplerWaterTime) / 1.0E9;
      AmountsWaterUtils.handleAmounts(seconds, vectorBufferTable);
      boolean vectorBufferFlag = skyRenderState != null && skyRenderState.skybox == Skybox.OVERWORLD;
      float panelSamplerValue = vectorBufferFlag ? getStarBrightness(1.0F - skyRenderState.rainGradient) : 0.0F;
      float vectorBufferValue6 = 0.0F;
      float vectorBufferValue7 = 0.0F;
      float vectorBufferValue8 = 0.0F;
      float vectorBufferValue9 = 1.0F;
      float waterCorzValue = 0.93F;
      float vectorBufferValue11 = 0.8F;
      float vectorBufferValue12 = 0.05F;
      float vectorBufferValue13 = 0.04F;
      float vectorBufferValue14 = 0.09F;
      if (vectorBufferFlag) {
         VectorMatrixUtils.handle(skyRenderState.sunAngle, VECTOR3_FVECTOR3F);
         VectorMatrixUtils.handle(skyRenderState.moonAngle, VECTOR3F2);
         vectorBufferValue6 = (1.0F - panelSamplerValue) * getAmountForFloat(-0.04F, 0.07F, VECTOR3_FVECTOR3F.y);
         float amountForFloat = getAmountForFloat(-0.1F, 0.2F, VECTOR3_FVECTOR3F.y);
         vectorBufferValue7 = (1.0F - panelSamplerValue) * getAmountForFloat(-0.04F, 0.07F, VECTOR3F2.y) * getMoonPhase(skyRenderState) * (1.0F - 0.85F * amountForFloat);
         vectorBufferValue8 = getStarBrightness(skyRenderState.starBrightness) * (1.0F - panelSamplerValue);
         int sunriseAndSunsetColor = skyRenderState.sunriseAndSunsetColor;
         float vectorBufferLimit = (sunriseAndSunsetColor >>> 24 & 0xFF) / 255.0F;
         if (vectorBufferLimit > 0.0F) {
            float waterCorzFactor = vectorBufferLimit * (1.0F - getAmountForFloat(0.0F, 0.4F, VECTOR3_FVECTOR3F.y));
            vectorBufferValue9 = getAmount(vectorBufferValue9, (sunriseAndSunsetColor >> 16 & 0xFF) / 255.0F, waterCorzFactor);
            waterCorzValue = getAmount(waterCorzValue, (sunriseAndSunsetColor >> 8 & 0xFF) / 255.0F, waterCorzFactor);
            vectorBufferValue11 = getAmount(vectorBufferValue11, (sunriseAndSunsetColor & 0xFF) / 255.0F, waterCorzFactor);
         }

         int skyColor = skyRenderState.skyColor;
         vectorBufferValue12 = (skyColor >> 16 & 0xFF) / 255.0F;
         vectorBufferValue13 = (skyColor >> 8 & 0xFF) / 255.0F;
         vectorBufferValue14 = (skyColor & 0xFF) / 255.0F;
      } else {
         VECTOR3_FVECTOR3F.set(0.0F, -1.0F, 0.0F);
         VECTOR3F2.set(0.0F, -1.0F, 0.0F);
      }

      ByteBuffer memAlloc2 = memAlloc;
      memAlloc2.clear();
      memAlloc2.putFloat(0, realisticWater.getMax());
      memAlloc2.putFloat(4, realisticWater.getRealisticWaterAmount());
      memAlloc2.putFloat(8, realisticWater.getAmount());
      memAlloc2.putFloat(12, (float)(seconds % 3600.0));
      memAlloc2.putFloat(16, VECTOR3_FVECTOR3F.x);
      memAlloc2.putFloat(20, VECTOR3_FVECTOR3F.y);
      memAlloc2.putFloat(24, VECTOR3_FVECTOR3F.z);
      memAlloc2.putFloat(28, vectorBufferValue6);
      memAlloc2.putFloat(32, VECTOR3F2.x);
      memAlloc2.putFloat(36, VECTOR3F2.y);
      memAlloc2.putFloat(40, VECTOR3F2.z);
      memAlloc2.putFloat(44, vectorBufferValue7);
      memAlloc2.putFloat(48, vectorBufferValue12);
      memAlloc2.putFloat(52, vectorBufferValue13);
      memAlloc2.putFloat(56, vectorBufferValue14);
      memAlloc2.putFloat(60, vectorBufferValue);
      memAlloc2.putFloat(64, vectorBufferValue9);
      memAlloc2.putFloat(68, waterCorzValue);
      memAlloc2.putFloat(72, vectorBufferValue11);
      memAlloc2.putFloat(76, vectorBufferValue8);

      for (int i = 0; i < 10; i++) {
         memAlloc2.putFloat(80 + i * 4, vectorBufferTable[i]);
      }

      memAlloc2.putFloat(120, AmountsWaterUtils.getAmount(seconds));
      memAlloc2.putFloat(124, 0.0F);
      memAlloc2.putFloat(128, vectorBufferValue3);
      memAlloc2.putFloat(132, transformsDynamicFactor);
      memAlloc2.putFloat(136, maxTime);
      memAlloc2.putFloat(140, panelSamplerValue);
      memAlloc2.position(0).limit(144);
      RenderSystem.getDevice().createCommandEncoder().writeToBuffer(buffer.slice(), memAlloc2);
   }

   private static void handleRenderFlag(WorldRenderContext worldRenderContext) {
      if (!waterCorzActive) {
         RealisticWater instance = RealisticWater.getInstance();
         if (instance != null) {
            long nanoTime = System.nanoTime();
            float maxTime = instance.getMaxTime(nanoTime);
            if (maxTime <= 0.0F) {
               if (!instance.isEnabled() && renderFlag) {
                  updateMemAlloc();
               }
            } else if (!DirExtractionUtils.isFlag()) {
               MinecraftClient client = MinecraftClient.getInstance();
               if (client.world != null && client.player != null && chunkHash != null) {
                  renderFlag = true;

                  try {
                     handleTransformsDynamic(client, worldRenderContext, instance, nanoTime, maxTime);
                  } catch (Throwable e) {
                     handleScanFlag("render", e);
                  }
               }
            }
         }
      }
   }

   public static void updateRegisterChunk() {
      if (!chunkActive) {
         chunkActive = true;

         try {
            WorldRenderEvents.END_EXTRACTION.register((EndExtraction)ctx -> frustum = ctx.frustum());
            WorldRenderEvents.END_MAIN.register(WaterCorzUtils::handleRenderFlag);
            ClientChunkEvents.CHUNK_LOAD.register((Load)(world, chunk) -> {
               if (scanFlag && chunkHash != null) {
                  chunkHash.handleHashChunk(chunk.getPos().x, chunk.getPos().z);
               }
            });
            ClientChunkEvents.CHUNK_UNLOAD.register((Unload)(chunk, worldChunk) -> {
               if (scanFlag && chunkHash != null) {
                  chunkHash.handleHashOpen(worldChunk.getPos().x, worldChunk.getPos().z);
               }
            });
         } catch (Throwable e) {
            waterCorzActive = true;
         }
      }
   }

   private static void handleScanFlag(String waterCorzText, Throwable throwable) {
      waterCorzActive = true;

      try {
         updateMemAlloc();
      } catch (Throwable e) {
      }
   }

   public static void handleChunkHash(int chunkHashValue, int chunkHashValue2, int chunkHashValue3) {
      if (scanFlag && chunkHash != null) {
         chunkHash.handleCoordSection(chunkHashValue, chunkHashValue2, chunkHashValue3);
      }
   }

   public static boolean isScanFlag() {
      return waterCorzActive;
   }
}
