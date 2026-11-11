package com.corz.client.utils;

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
import com.mojang.blaze3d.vertex.VertexFormat.DrawMode;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.Supplier;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.world.WorldRenderEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gl.RenderPipelines;
import net.minecraft.client.gl.UniformType;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.system.MemoryUtil;
import com.corz.client.utils.WorldMutable;
import com.corz.client.utils.PetalsAmounts;
import com.corz.client.utils.AmountAtmoUtils;
import com.corz.client.utils.PetalsSnowType;

public final class RenderAtmoUtils {
   private static final Matrix4f MATRIX4F = new Matrix4f();
   private static final WorldMutable WORLD_MUTABLE = new WorldMutable();
   private static final GpuBuffer[] gpuBuffers = new GpuBuffer[3];
   private static final int BASE_PARTICLE_COUNT;
   private static boolean renderFlag;
   private static float particleSizeScale = Float.POSITIVE_INFINITY;
   private static float atmoAmount;
   private static final Vector3f VECTOR3F = new Vector3f();
   private static int petalsVertexCount;
   private static final Supplier<String> SUPPLIER = () -> "corz:atmo_particles";
   private static final int atmoUnavailableValue;
   private static boolean installFlag;
   private static boolean lateRenderFlag;
   private static int atmoValue;
   private static boolean mutableActive;
   private static RenderAtmoUtils.Settings installRegisterSettings;
   private static long lastNanoTime;
   private static final float[] vectorTable;
   private static final int[] atmoTable;
   private static final Supplier<String> VBO_LABEL_SUPPLIER = () -> "corz:atmo_vbo";
   private static final int[] table;
   private static final float[] PARTICLE_DISTANCES;
   private static boolean lateRenderActive;
   private static ByteBuffer memAlloc;
   private static float snowFadeAmount;
   private static int snowVertexCount;
   private static final float[] PARTICLE_ROTATIONS;
   private static double lastCameraX;
   private static final Vector3f VECTOR3_FVECTOR3F = new Vector3f();
   private static final Matrix4f MATRIX4_FMATRIX4F = new Matrix4f();
   private static Boolean booleanValue;
   private static final Vector3f VECTOR3F3 = new Vector3f();
   private static final int MAX_PARTICLE_COUNT;
   private static float petalsFadeAmount;
   private static float vectorAmount = Float.POSITIVE_INFINITY;
   private static double lastCameraZ;
   private static float cameraVelocityY;
   private static double lastCameraY;
   private static PetalsAmounts minTime;
   private static long memAddress;
   private static final int[] DEPTH_BUCKET_COUNTS = new int[129];
   private static final float[] WIND_OFFSETS = new float[3];
   private static float cameraVelocityZ;
   private static final Vector4f VECTOR4F = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
   private static double atmoCount;
   private static boolean atmoActive;
   private static final int[] SORTED_PARTICLE_INDICES;
   private static final Vector3f VECTOR3F4 = new Vector3f();
   private static PetalsAmounts atmoPetalsAmounts;
   private static long updateDurationNanos;

   private static int getAmountsMutable(PetalsAmounts petalsAmounts, int index) {
      int timesAmounts = WORLD_MUTABLE.getTimesAmounts(PetalsAmounts.getValue(petalsAmounts.positionX[index]), PetalsAmounts.getValue(petalsAmounts.positionZ[index]), petalsAmounts.petalsSnowTable[index]);
      return timesAmounts << 4 | 15728640;
   }

   private static long getVectorAmounts(long vectorAmounts, PetalsAmounts petalsAmounts, int index, float vectorValue, float vectorFactor, double vectorValue2, double y, double z, float atmoFactor) {
      float vectorBound = petalsAmounts.vectorTable[index];
      float vectorValue6 = 1.0F - AmountAtmoUtils.getAmount(2.0F, 9.0F, vectorFactor);
      vectorValue *= 0.78F + 0.22F * vectorValue6;
      float vectorFactor2 = 0.88F + 0.12F * vectorValue6;
      float vectorLimit = 1.15F * atmoFactor * vectorFactor;
      if (vectorBound < vectorLimit) {
         float vectorFactor3 = vectorBound / vectorLimit;
         vectorValue *= vectorFactor3 * vectorFactor3;
         vectorBound = vectorLimit;
      }

      float vectorFactor4 = (float)(petalsAmounts.positionX[index] - vectorValue2);
      float vectorFactor5 = (float)(petalsAmounts.petalsSnowTable[index] - y);
      float vectorFactor6 = (float)(petalsAmounts.positionZ[index] - z);
      float vectorFactor7 = -(VECTOR3F4.x * vectorFactor4 + VECTOR3F4.y * vectorFactor5 + VECTOR3F4.z * vectorFactor6);
      if (vectorFactor7 <= 0.02F) {
         return vectorAmounts;
      } else {
         float min8 = Math.min(vectorBound, vectorAmount * vectorFactor7);
         int min7 = getMin(vectorValue);
         if (min7 <= 0) {
            return vectorAmounts;
         } else {
            float vectorValue8 = petalsAmounts.flutterPhase[index];
            int vectorValue9 = getValue(vectorFactor2 * (1.0F - 0.1F * vectorValue8), vectorFactor2 * (1.0F - 0.06F * vectorValue8), vectorFactor2, min7);
            int amountsMutable = getAmountsMutable(petalsAmounts, index);
            float vectorValue10 = VECTOR3F.x * min8;
            float vectorValue11 = VECTOR3F.y * min8;
            float vectorValue12 = VECTOR3F.z * min8;
            float vectorValue13 = VECTOR3F3.x * min8;
            float vectorValue14 = VECTOR3F3.y * min8;
            float vectorValue15 = VECTOR3F3.z * min8;
            vectorAmounts = getMemPut(vectorAmounts, vectorFactor4 - vectorValue10 - vectorValue13, vectorFactor5 - vectorValue11 - vectorValue14, vectorFactor6 - vectorValue12 - vectorValue15, -1.0F, -1.0F, vectorValue9, amountsMutable);
            vectorAmounts = getMemPut(vectorAmounts, vectorFactor4 + vectorValue10 - vectorValue13, vectorFactor5 + vectorValue11 - vectorValue14, vectorFactor6 + vectorValue12 - vectorValue15, 1.0F, -1.0F, vectorValue9, amountsMutable);
            vectorAmounts = getMemPut(vectorAmounts, vectorFactor4 + vectorValue10 + vectorValue13, vectorFactor5 + vectorValue11 + vectorValue14, vectorFactor6 + vectorValue12 + vectorValue15, 1.0F, 1.0F, vectorValue9, amountsMutable);
            return getMemPut(vectorAmounts, vectorFactor4 - vectorValue10 + vectorValue13, vectorFactor5 - vectorValue11 + vectorValue14, vectorFactor6 - vectorValue12 + vectorValue15, -1.0F, 1.0F, vectorValue9, amountsMutable);
         }
      }
   }

   private static int getMin(float atmoValue) {
      int atmoBound = (int)(atmoValue * 255.0F + 0.5F);
      return atmoBound < 0 ? 0 : Math.min(atmoBound, 255);
   }

   private static void handleAmounts(int atmoLimit) {
      float atmoDivisor = 1.0F;

      for (int i = 0; i < atmoLimit; i++) {
         if (PARTICLE_DISTANCES[i] > atmoDivisor) {
            atmoDivisor = PARTICLE_DISTANCES[i];
         }
      }

      float atmoFactor = 127.0F / atmoDivisor;

      for (int j = 0; j <= 128; j++) {
         DEPTH_BUCKET_COUNTS[j] = 0;
      }

      for (int k = 0; k < atmoLimit; k++) {
         int atmoLimit2 = 127 - (int)(PARTICLE_DISTANCES[k] * atmoFactor);
         if (atmoLimit2 < 0) {
            atmoLimit2 = 0;
         } else if (atmoLimit2 >= 128) {
            atmoLimit2 = 127;
         }

         atmoTable[k] = atmoLimit2;
         DEPTH_BUCKET_COUNTS[atmoLimit2 + 1]++;
      }

      for (int m = 0; m < 128; m++) {
         DEPTH_BUCKET_COUNTS[m + 1] = DEPTH_BUCKET_COUNTS[m + 1] + DEPTH_BUCKET_COUNTS[m];
      }

      int index = 0;

      while (index < atmoLimit) {
         SORTED_PARTICLE_INDICES[DEPTH_BUCKET_COUNTS[atmoTable[index]]++] = index++;
      }
   }

   private static float getAmount(float atmoLimit) {
      return atmoLimit < 0.0F ? 0.0F : (atmoLimit > 1.0F ? 1.0F : atmoLimit);
   }

   private static void handleRenderMatrix(WorldRenderContext worldRenderContext) {
      if (!lateRenderActive && installRegisterSettings != null && !lateRenderFlag) {
         try {
            MATRIX4_FMATRIX4F.set(RenderSystem.getModelViewMatrix()).mul(worldRenderContext.matrices().peek().getPositionMatrix());
            handleMinTime(null);
            renderFlag = true;
         } catch (Throwable e) {
            handleLateRender("render", e);
         }
      }
   }

   static float getAmountForFloat(float atmoFactor) {
      return atmoFactor * atmoFactor * (3.0F - 2.0F * atmoFactor);
   }

   private static int getValue(int atmoValue, float atmoFactor, int atmoValue2) {
      return getValue((atmoValue >> 16 & 0xFF) / 255.0F * atmoFactor, (atmoValue >> 8 & 0xFF) / 255.0F * atmoFactor, (atmoValue & 0xFF) / 255.0F * atmoFactor, atmoValue2);
   }

   public static void handleRenderLate(GpuBufferSlice gpuBufferSlice) {
      if (!lateRenderActive && installRegisterSettings != null) {
         lateRenderFlag = true;
         if (renderFlag) {
            renderFlag = false;
         } else {
            try {
               MATRIX4_FMATRIX4F.set(RenderSystem.getModelViewMatrix());
               handleMinTime(gpuBufferSlice);
            } catch (Throwable e) {
               handleLateRender("render", e);
            }
         }
      }
   }

   private static int getAmountsMin(int atmoLimit, double atmoValue, double y, double z, float atmoFactor) {
      long memAddress2 = memAddress;
      int amountsMin = 0;

      for (int i = 0; i < atmoLimit; i++) {
         int index = SORTED_PARTICLE_INDICES[i];
         int atmoValue5 = table[index];
         int atmoValue6 = atmoValue5 & 65535;
         if (atmoValue5 >>> 16 == 0) {
            if (amountsMin + 1 > MAX_PARTICLE_COUNT) {
               break;
            }

            long vectorAmounts = getVectorAmounts(memAddress2, atmoPetalsAmounts, atmoValue6, vectorTable[index], PARTICLE_DISTANCES[index], atmoValue, y, z, atmoFactor);
            if (vectorAmounts != memAddress2) {
               memAddress2 = vectorAmounts;
               amountsMin++;
            }
         } else {
            if (amountsMin + 2 > MAX_PARTICLE_COUNT) {
               break;
            }

            long amountsVector = getAmountsVector(memAddress2, minTime, atmoValue6, vectorTable[index], PARTICLE_ROTATIONS[index], PARTICLE_DISTANCES[index], atmoValue, y, z, atmoFactor);
            if (amountsVector != memAddress2) {
               memAddress2 = amountsVector;
               amountsMin += 2;
            }
         }
      }

      return amountsMin;
   }

   public static void handleInstallRegister(RenderAtmoUtils.Settings api) {
      installRegisterSettings = api;
      if (!installFlag) {
         installFlag = true;

         try {
            WorldRenderEvents.END_MAIN.register(RenderAtmoUtils::handleRenderMatrix);
         } catch (Throwable e) {
            handleLateRender("install", e);
         }
      }
   }

   private static int getValue(float atmoValue, float atmoLimit, float atmoValue3, int atmoValue4) {
      int atmoValue5 = (int)(getAmount(atmoValue) * 255.0F + 0.5F);
      int atmoValue6 = (int)(getAmount(atmoLimit) * 255.0F + 0.5F);
      int atmoValue7 = (int)(getAmount(atmoValue3) * 255.0F + 0.5F);
      return atmoValue4 << 24 | atmoValue7 << 16 | atmoValue6 << 8 | atmoValue5;
   }

   private RenderAtmoUtils() {
   }

   private static void handleAmountFlag(double amount, double amount3, double amount5, float atmoDivisor) {
      if (!atmoActive) {
         atmoActive = true;
         lastCameraX = amount;
         lastCameraY = amount3;
         lastCameraZ = amount5;
         atmoAmount = 0.0F;
         cameraVelocityY = 0.0F;
         cameraVelocityZ = 0.0F;
      } else {
         if (atmoDivisor > 0.0F) {
            float atmoFactor = (float)((amount - lastCameraX) / atmoDivisor);
            float atmoFactor2 = (float)((amount3 - lastCameraY) / atmoDivisor);
            float atmoFactor3 = (float)((amount5 - lastCameraZ) / atmoDivisor);
            if (atmoFactor * atmoFactor + atmoFactor2 * atmoFactor2 + atmoFactor3 * atmoFactor3 > 6400.0F) {
               atmoAmount = 0.0F;
               cameraVelocityY = 0.0F;
               cameraVelocityZ = 0.0F;
            } else {
               float atmoFactor4 = 1.0F - (float)Math.exp(-atmoDivisor / 0.08F);
               atmoAmount = atmoAmount + (atmoFactor - atmoAmount) * atmoFactor4;
               cameraVelocityY = cameraVelocityY + (atmoFactor2 - cameraVelocityY) * atmoFactor4;
               cameraVelocityZ = cameraVelocityZ + (atmoFactor3 - cameraVelocityZ) * atmoFactor4;
            }
         }

         lastCameraX = amount;
         lastCameraY = amount3;
         lastCameraZ = amount5;
      }
   }

   static float getAmount(float atmoValue, boolean atmoFlag, float atmoValue2) {
      return atmoFlag ? Math.min(1.0F, atmoValue + atmoValue2 / 1.1F) : Math.max(0.0F, atmoValue - atmoValue2 / 0.8F);
   }

   private static int getAmountsVector(PetalsAmounts petalsAmounts, int vectorValue, float vectorFactor, double vectorValue2, double vectorValue3, double vectorValue4, int index) {
      PetalsSnowType petalsSnowType = petalsAmounts.petalsSnowType;
      float vectorAmount5 = petalsSnowType.vectorAmount;
      float vectorValue5 = vectorAmount5 * 0.62F;
      float vectorValue6 = vectorAmount5 * 0.97F;
      float vectorValue7 = petalsSnowType.spawnHeight - 4.5F;
      float vectorValue8 = petalsSnowType.spawnHeight - 0.2F;
      float vectorValue9 = -petalsSnowType.petalsSnowAmount + 0.2F;
      float vectorValue10 = -petalsSnowType.petalsSnowAmount + 3.5F;
      boolean vectorFlag = petalsSnowType == PetalsSnowType.PETALS;
      float vectorValue11 = vectorFlag ? 1.3F : 0.5F;
      float vectorValue12 = vectorFlag ? 3.0F : 2.0F;
      float x = -VECTOR3F4.x;
      float y = -VECTOR3F4.y;
      float z = -VECTOR3F4.z;
      float vectorFactor2 = petalsSnowType.sizeMultiplier * vectorFactor;

      for (int i = 0; i < petalsAmounts.activeParticleCount && index < BASE_PARTICLE_COUNT; i++) {
         float vectorFactor3 = petalsAmounts.table[i];
         if (!(vectorFactor3 <= 0.002F)) {
            float vectorFactor4 = (float)(petalsAmounts.positionX[i] - vectorValue2);
            float vectorFactor5 = (float)(petalsAmounts.petalsSnowTable[i] - vectorValue3);
            float vectorFactor6 = (float)(petalsAmounts.positionZ[i] - vectorValue4);
            float vectorValue13 = petalsAmounts.vectorTable[i] * 2.5F;
            if (!(vectorFactor4 * x + vectorFactor5 * y + vectorFactor6 * z < -vectorValue13)) {
               float distance = (float)Math.sqrt(vectorFactor4 * vectorFactor4 + vectorFactor6 * vectorFactor6);
               float vectorFactor7 = 1.0F - AmountAtmoUtils.getAmount(vectorValue5, vectorValue6, distance);
               float vectorFactor8 = vectorFactor7 * (1.0F - AmountAtmoUtils.getAmount(vectorValue7, vectorValue8, vectorFactor5));
               float squaredDistanceTo = vectorFactor8 * AmountAtmoUtils.getAmount(vectorValue9, vectorValue10, vectorFactor5);
               float vectorFactor9 = vectorFactor2 * vectorFactor3 * petalsAmounts.getPetalsSnow(i) * (vectorFlag ? (float)Math.sqrt(squaredDistanceTo) : squaredDistanceTo);
               float petalsSnowValue = (float)Math.sqrt(distance * distance + vectorFactor5 * vectorFactor5);
               float vectorValue15 = vectorFactor9 * AmountAtmoUtils.getAmount(vectorValue11, vectorValue12, petalsSnowValue);
               if (!(vectorValue15 < 0.004F)) {
                  table[index] = vectorValue << 16 | i;
                  vectorTable[index] = vectorValue15;
                  PARTICLE_ROTATIONS[index] = squaredDistanceTo;
                  PARTICLE_DISTANCES[index] = petalsSnowValue;
                  index++;
               }
            }
         }
      }

      return index;
   }

   public static boolean isLateRender() {
      return lateRenderActive;
   }

   private static void updateMinTime() {
      if (atmoPetalsAmounts != null) {
         atmoPetalsAmounts.updateMaxFlag();
      }

      if (minTime != null) {
         minTime.updateMaxFlag();
      }

      petalsFadeAmount = 0.0F;
      snowFadeAmount = 0.0F;
      updateMutableFlag();
      atmoActive = false;
      lastNanoTime = 0L;
   }

   private static boolean isMemAtmo() {
      if (booleanValue == null) {
         boolean booleanValue2;
         try {
            booleanValue2 = VertexFormats.POSITION_TEXTURE_COLOR_LIGHT.getVertexSize() == 28
               && ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN
               && RenderSystem.getDevice().precompilePipeline(RenderAtmoUtils.Gpu.build).isValid();
         } catch (Throwable e) {
            booleanValue2 = false;
         }

         booleanValue = booleanValue2;
         if (!booleanValue2) {
            handleLateRender("pipeline", new IllegalStateException("corz_atmo shader or vertex layout unavailable"));
            return false;
         }
      }

      if (!booleanValue) {
         return false;
      } else {
         if (memAlloc == null) {
            memAlloc = MemoryUtil.memAlloc(atmoUnavailableValue);
            memAddress = MemoryUtil.memAddress(memAlloc);
         }

         return true;
      }
   }

   static {
      MAX_PARTICLE_COUNT = PetalsSnowType.SNOW.baseParticleCount + PetalsSnowType.PETALS.baseParticleCount * 2;
      atmoUnavailableValue = MAX_PARTICLE_COUNT * 4 * 28;
      BASE_PARTICLE_COUNT = PetalsSnowType.SNOW.baseParticleCount + PetalsSnowType.PETALS.baseParticleCount;
      table = new int[BASE_PARTICLE_COUNT];
      vectorTable = new float[BASE_PARTICLE_COUNT];
      PARTICLE_DISTANCES = new float[BASE_PARTICLE_COUNT];
      PARTICLE_ROTATIONS = new float[BASE_PARTICLE_COUNT];
      atmoTable = new int[BASE_PARTICLE_COUNT];
      SORTED_PARTICLE_INDICES = new int[BASE_PARTICLE_COUNT];
   }

   private static void handleAmountAmounts(PetalsAmounts petalsAmounts, float atmoValue, float atmoValue2, double x, double y, double z, float petalsLimit) {
      petalsAmounts.handleMin(petalsAmounts.petalsSnowType.getValue(atmoValue));
      petalsAmounts.handleAmount(WIND_OFFSETS[0], WIND_OFFSETS[1], atmoValue2, WIND_OFFSETS[2]);
      if (atmoActive) {
         petalsAmounts.handleAmountFlag(lastCameraX, lastCameraY, lastCameraZ, atmoAmount, cameraVelocityY, cameraVelocityZ);
      } else {
         petalsAmounts.updateFlag();
      }

      petalsAmounts.handleAmountMax(x, y, z, petalsLimit, WORLD_MUTABLE);
   }

   static int getValue(int atmoValue, int atmoValue2, float atmoFactor) {
      int round = Math.round((atmoValue >> 16 & 0xFF) + ((atmoValue2 >> 16 & 0xFF) - (atmoValue >> 16 & 0xFF)) * atmoFactor);
      int round4 = Math.round((atmoValue >> 8 & 0xFF) + ((atmoValue2 >> 8 & 0xFF) - (atmoValue >> 8 & 0xFF)) * atmoFactor);
      int round5 = Math.round((atmoValue & 0xFF) + ((atmoValue2 & 0xFF) - (atmoValue & 0xFF)) * atmoFactor);
      return round << 16 | round4 << 8 | round5;
   }

   private static long getMemPut(long memPutValue, float deltaVar, float memPutValue3, float memPutValue4, float memPutValue5, float memPutValue6, int memPutValue7, int amountsMutable) {
      MemoryUtil.memPutFloat(memPutValue, deltaVar);
      MemoryUtil.memPutFloat(memPutValue + 4L, memPutValue3);
      MemoryUtil.memPutFloat(memPutValue + 8L, memPutValue4);
      MemoryUtil.memPutFloat(memPutValue + 12L, memPutValue5);
      MemoryUtil.memPutFloat(memPutValue + 16L, memPutValue6);
      MemoryUtil.memPutInt(memPutValue + 20L, memPutValue7);
      MemoryUtil.memPutInt(memPutValue + 24L, amountsMutable);
      return memPutValue + 28L;
   }

   private static void handleMinTime(GpuBufferSlice gpuBufferSlice) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientWorld world = instance.world;
      ClientPlayerEntity player = instance.player;
      if (world != null && player != null && !DirExtractionUtils.isFlag()) {
         long nanoTime = System.nanoTime();
         float atmoValue = lastNanoTime == 0L ? 0.0F : (float)Math.min(0.25, (nanoTime - lastNanoTime) / 1.0E9);
         lastNanoTime = nanoTime;
         boolean atmoFlag = installRegisterSettings.isFlag() && world.getDimension().hasSkyLight() && !world.getDimension().hasCeiling();
         int rosePetals = installRegisterSettings.getRosePetals();
         boolean atmoFlag2 = atmoFlag && rosePetals == 0;
         boolean atmoFlag3 = atmoFlag && rosePetals == 1;
         petalsFadeAmount = getAmount(petalsFadeAmount, atmoFlag2, atmoValue);
         snowFadeAmount = getAmount(snowFadeAmount, atmoFlag3, atmoValue);
         if (petalsFadeAmount <= 0.0F && !atmoFlag2 && atmoPetalsAmounts != null && atmoPetalsAmounts.getMax() > 0) {
            atmoPetalsAmounts.updateMaxFlag();
         }

         if (snowFadeAmount <= 0.0F && !atmoFlag3 && minTime != null && minTime.getMax() > 0) {
            minTime.updateMaxFlag();
         }

         if (petalsFadeAmount <= 0.0F && snowFadeAmount <= 0.0F && !atmoFlag2 && !atmoFlag3) {
            updateMutableFlag();
            atmoActive = false;
            snowVertexCount = 0;
            petalsVertexCount = 0;
         } else {
            long nanoTime3 = System.nanoTime();
            atmoCount += atmoValue;
            float atmoValue2 = getAmount(installRegisterSettings.getInteger());
            float atmoValue3 = getAmount(installRegisterSettings.getIntegerHelper());
            PetalsAmounts.handle(atmoCount, atmoValue2, WIND_OFFSETS);
            Camera camera = instance.gameRenderer.getCamera();
            Vec3d cameraPos = camera.getCameraPos();
            double x = cameraPos.x;
            double y = cameraPos.y;
            double z = cameraPos.z;
            float tickProgress = instance.getRenderTickCounter().getTickProgress(false);
            double atmoValue4 = player.lastX + (player.getX() - player.lastX) * tickProgress;
            double atmoValue5 = player.lastY + (player.getY() - player.lastY) * tickProgress;
            double atmoValue6 = player.lastZ + (player.getZ() - player.lastZ) * tickProgress;
            handleAmountFlag(atmoValue4, atmoValue5, atmoValue6, atmoValue);
            mutableActive = true;
            WORLD_MUTABLE.handleTimesWorld(world);
            if (atmoFlag2 || petalsFadeAmount > 0.0F) {
               if (atmoPetalsAmounts == null) {
                  atmoPetalsAmounts = new PetalsAmounts(PetalsSnowType.SNOW, (int)nanoTime);
               }

               handleAmountAmounts(atmoPetalsAmounts, atmoValue3, atmoValue2, x, y, z, atmoValue);
            }

            if (atmoFlag3 || snowFadeAmount > 0.0F) {
               if (minTime == null) {
                  minTime = new PetalsAmounts(PetalsSnowType.PETALS, (int)(nanoTime >>> 7));
               }

               handleAmountAmounts(minTime, atmoValue3, atmoValue2, x, y, z, atmoValue);
            }

            MATRIX4_FMATRIX4F.normalizedPositiveX(VECTOR3F);
            MATRIX4_FMATRIX4F.normalizedPositiveY(VECTOR3F3);
            MATRIX4_FMATRIX4F.normalizedPositiveZ(VECTOR3F4);
            float maxFramebufferHeight = Math.max(1, instance.getWindow().getFramebufferHeight());
            float degrees = FovOptionsUtils.getValue();
            if (!(degrees > 1.0F) || !(degrees < 179.0F)) {
               degrees = 70.0F;
            }

            float atmoFactor = (float)(2.0 * Math.tan(Math.toRadians(degrees) * 0.5)) / maxFramebufferHeight;
            particleSizeScale = 0.016F * maxFramebufferHeight * atmoFactor;
            vectorAmount = 0.011F * maxFramebufferHeight * atmoFactor;
            int value = 0;
            if (atmoPetalsAmounts != null && petalsFadeAmount > 0.0F) {
               value = getAmountsVector(atmoPetalsAmounts, 0, getAmountForFloat(petalsFadeAmount), x, y, z, value);
            }

            if (minTime != null && snowFadeAmount > 0.0F) {
               value = getAmountsVector(minTime, 1, getAmountForFloat(snowFadeAmount), x, y, z, value);
            }

            petalsVertexCount = value;
            if (value == 0) {
               snowVertexCount = 0;
               updateDurationNanos = System.nanoTime() - nanoTime3;
            } else if (isMemAtmo()) {
               handleAmounts(value);
               int amountsMin = getAmountsMin(value, x, y, z, atmoFactor);
               snowVertexCount = amountsMin;
               updateDurationNanos = System.nanoTime() - nanoTime3;
               if (amountsMin > 0) {
                  handleBufferDynamic(instance, amountsMin, gpuBufferSlice);
               }
            }
         }
      } else {
         updateMinTime();
      }
   }

   private static void handleBufferDynamic(MinecraftClient client, int bufferDynamicValue, GpuBufferSlice gpuBufferSlice) {
      int orderLimit = bufferDynamicValue * 4 * 28;
      int atmoValue32 = atmoValue;
      atmoValue = (atmoValue + 1) % 3;
      GpuBuffer gpuBuffer = gpuBuffers[atmoValue32];
      if (gpuBuffer == null || gpuBuffer.isClosed()) {
         gpuBuffer = RenderSystem.getDevice().createBuffer(VBO_LABEL_SUPPLIER, 40, atmoUnavailableValue);
         gpuBuffers[atmoValue32] = gpuBuffer;
      }

      CommandEncoder commandEncoder = RenderSystem.getDevice().createCommandEncoder();
      memAlloc.clear();
      memAlloc.limit(orderLimit);
      commandEncoder.writeToBuffer(gpuBuffer.slice(0L, orderLimit), memAlloc);
      memAlloc.clear();
      int quadsScaled = bufferDynamicValue * 6;
      ShapeIndexBuffer sequentialBuffer = RenderSystem.getSequentialBuffer(DrawMode.QUADS);
      GpuBuffer indexBuffer = sequentialBuffer.getIndexBuffer(quadsScaled);
      GpuBufferSlice write = RenderSystem.getDynamicUniforms().write(MATRIX4_FMATRIX4F, VECTOR4F, VECTOR3_FVECTOR3F, MATRIX4F);
      Framebuffer framebuffer = client.getFramebuffer();

      try (RenderPass renderPass = commandEncoder.createRenderPass(
            SUPPLIER, framebuffer.getColorAttachmentView(), OptionalInt.empty(), framebuffer.getDepthAttachmentView(), OptionalDouble.empty()
         )) {
         renderPass.setPipeline(RenderAtmoUtils.Gpu.build);
         RenderSystem.bindDefaultUniforms(renderPass);
         if (gpuBufferSlice != null) {
            renderPass.setUniform("Fog", gpuBufferSlice);
         }

         renderPass.setUniform("DynamicTransforms", write);
         renderPass.bindTexture("Sampler2", client.gameRenderer.getLightmapTextureManager().getGlTextureView(), RenderSystem.getSamplerCache().get(FilterMode.LINEAR));
         renderPass.setVertexBuffer(0, gpuBuffer);
         renderPass.setIndexBuffer(indexBuffer, sequentialBuffer.getIndexType());
         renderPass.drawIndexed(0, 0, quadsScaled, 1);
      }
   }

   public static String getFormat() {
      return String.format(
         Locale.ROOT,
         "atmo failed=%s hook=%s snow(active=%d visible=%d presence=%.2f) petals(active=%d visible=%d presence=%.2f) candidates=%d quads=%d cpu=%.3fms",
         lateRenderActive,
         lateRenderFlag ? "late" : "main",
         atmoPetalsAmounts == null ? 0 : atmoPetalsAmounts.getMax(),
         atmoPetalsAmounts == null ? 0 : atmoPetalsAmounts.getAmountsMax(),
         petalsFadeAmount,
         minTime == null ? 0 : minTime.getMax(),
         minTime == null ? 0 : minTime.getAmountsMax(),
         snowFadeAmount,
         petalsVertexCount,
         snowVertexCount,
         updateDurationNanos / 1000000.0
      );
   }

   private static float getAmountFloat(float atmoValue) {
      return atmoValue - (float)Math.floor(atmoValue);
   }

   private static long getAmountsVector(long amountsVector, PetalsAmounts petalsAmounts, int index, float vectorValue, float vectorValue2, float vectorFactor, double vectorValue3, double y, double z, float vectorValue6) {
      float vectorFactor2 = 0.35F + 0.65F * vectorValue2;
      float vectorFactor3 = petalsAmounts.vectorTable[index] * vectorFactor2;
      float vectorFactor4 = vectorFactor3 * petalsAmounts.sizeScale[index];
      float vectorLimit = 1.6F * vectorValue6 * vectorFactor;
      if (vectorFactor3 < vectorLimit) {
         float vectorFactor5 = vectorFactor3 / vectorLimit;
         vectorValue *= vectorFactor5 * vectorFactor5;
         float vectorValue7 = vectorLimit / vectorFactor3;
         vectorFactor3 *= vectorValue7;
         vectorFactor4 *= vectorValue7;
      }

      float vectorFactor6 = (float)(petalsAmounts.positionX[index] - vectorValue3);
      float vectorFactor7 = (float)(petalsAmounts.petalsSnowTable[index] - y);
      float vectorFactor8 = (float)(petalsAmounts.positionZ[index] - z);
      float vectorBound = -(VECTOR3F4.x * vectorFactor6 + VECTOR3F4.y * vectorFactor7 + VECTOR3F4.z * vectorFactor8);
      if (vectorBound <= 0.05F) {
         return amountsVector;
      } else {
         float vectorFactor9 = vectorBound / Math.max(vectorFactor, vectorBound);
         float vectorValue8 = particleSizeScale * vectorBound * vectorFactor9 * vectorFactor9;
         if (vectorFactor3 > vectorValue8) {
            vectorFactor4 *= vectorValue8 / vectorFactor3;
            vectorFactor3 = vectorValue8;
         }

         int min9 = getMin(vectorValue);
         if (min9 <= 0) {
            return amountsVector;
         } else {
            float vectorValue10 = petalsAmounts.flutterPhase[index];
            float vectorValue11 = 4.0F * (1 + (int)(getAmountFloat(vectorValue10 * 13.7F) * 4.0F));
            float amountFloat = getAmountFloat(vectorValue10 * 7.31F);
            float angle = petalsAmounts.currentYaw[index];
            float angle2 = petalsAmounts.spinAngle[index];
            float angle3 = petalsAmounts.swayAngleOffset[index] + (getAmountFloat(vectorValue10 * 11.3F) - 0.5F) * 1.1F;
            float vectorLimit2 = petalsAmounts.fadeIn[index];
            if (vectorLimit2 > 0.0F) {
               float vectorValue13 = Math.round(angle2 / (float) Math.PI) * (float) Math.PI;
               angle2 += (vectorValue13 - angle2) * vectorLimit2;
               angle3 *= 1.0F - vectorLimit2;
            }

            float cos = (float)Math.cos(angle);
            float sin = (float)Math.sin(angle);
            float cos2 = (float)Math.cos(angle2);
            float sin2 = (float)Math.sin(angle2);
            float cos3 = (float)Math.cos(angle3);
            float sin3 = (float)Math.sin(angle3);
            float vectorFactor10 = -sin * cos2;
            float vectorFactor11 = sin2;
            float vectorFactor12 = cos * cos2;
            float vectorFactor13 = sin * sin2;
            float vectorFactor14 = -cos * sin2;
            float vectorFactor15 = cos * cos3 + vectorFactor13 * sin3;
            float vectorFactor16 = cos2 * sin3;
            float vectorFactor17 = sin * cos3 + vectorFactor14 * sin3;
            float vectorFactor18 = -cos * sin3 + vectorFactor13 * cos3;
            float vectorFactor19 = cos2 * cos3;
            float vectorFactor20 = -sin * sin3 + vectorFactor14 * cos3;
            if (vectorLimit2 > 0.0F && vectorFactor19 < 0.0F) {
               vectorFactor18 = -vectorFactor18;
               vectorFactor19 = -vectorFactor19;
               vectorFactor20 = -vectorFactor20;
               vectorFactor10 = -vectorFactor10;
               vectorFactor11 = -sin2;
               vectorFactor12 = -vectorFactor12;
            }

            float vectorFactor21 = 0.22F + 0.16F * getAmountFloat(vectorValue10 * 3.7F);
            float vectorFactor22 = 0.12F + 0.18F * getAmountFloat(vectorValue10 * 5.3F);
            boolean vectorFlag = -(vectorFactor18 * vectorFactor6 + vectorFactor19 * vectorFactor7 + vectorFactor20 * vectorFactor8) >= 0.0F;
            int vectorValue14;
            int vectorValue15;
            if (vectorValue10 < 0.1F) {
               vectorValue14 = getValue(15637162, 16036542, amountFloat);
               vectorValue15 = getValue(12858966, 13782120, amountFloat);
            } else {
               float vectorValue16 = (vectorValue10 - 0.1F) / 0.9F;
               int vectorValue17 = vectorValue16 < 0.5F ? getValue(11413056, 13645903, vectorValue16 * 2.0F) : getValue(13645903, 14964332, vectorValue16 * 2.0F - 1.0F);
               vectorValue15 = vectorValue16 < 0.5F ? getValue(7604766, 10226728, vectorValue16 * 2.0F) : getValue(10226728, 12457526, vectorValue16 * 2.0F - 1.0F);
               vectorValue14 = getValue(vectorValue17, vectorValue15, 0.18F * amountFloat);
            }

            if (!vectorFlag) {
               vectorValue14 = getValue(vectorValue14, 15773884, 0.24F);
               vectorValue15 = getValue(vectorValue15, 14711438, 0.24F);
            }

            float vectorFactor23 = 1.0F / (float)Math.sqrt(1.0F + vectorFactor21 * vectorFactor21);
            float vectorValue18 = 0.7F + 0.3F * Math.abs((vectorFactor19 + vectorFactor11 * vectorFactor21) * vectorFactor23);
            float atmoFactor = 0.7F + 0.3F * Math.abs((vectorFactor19 - vectorFactor11 * vectorFactor21) * vectorFactor23);
            int amountsMutable = getAmountsMutable(petalsAmounts, index);
            float vectorValue20 = vectorFactor15 * vectorFactor3;
            float vectorValue21 = vectorFactor16 * vectorFactor3;
            float vectorValue22 = vectorFactor17 * vectorFactor3;
            float vectorValue23 = vectorFactor10 * vectorFactor4;
            float vectorValue24 = vectorFactor11 * vectorFactor4;
            float vectorValue25 = vectorFactor12 * vectorFactor4;
            float vectorFactor24 = vectorFactor21 * vectorFactor4;
            float vectorFactor25 = vectorFactor22 * vectorFactor3;
            float deltaVar = vectorFactor6 - vectorValue20;
            float deltaVar2 = vectorFactor7 - vectorValue21;
            float deltaVar3 = vectorFactor8 - vectorValue22;
            float vectorValue26 = vectorFactor6 + vectorValue20 + vectorFactor18 * vectorFactor25;
            float vectorValue27 = vectorFactor7 + vectorValue21 + vectorFactor19 * vectorFactor25;
            float vectorValue28 = vectorFactor8 + vectorValue22 + vectorFactor20 * vectorFactor25;
            float vectorValue29 = vectorFactor18 * vectorFactor24;
            float vectorValue30 = vectorFactor19 * vectorFactor24;
            float vectorValue31 = vectorFactor20 * vectorFactor24;
            int vectorValue32 = getValue(vectorValue14, vectorValue18, min9);
            int vectorValue33 = getValue(vectorValue15, vectorValue18, min9);
            int vectorValue34 = getValue(vectorValue14, atmoFactor, min9);
            int vectorValue35 = getValue(vectorValue15, atmoFactor, min9);
            float vectorValue36 = vectorValue11 - 1.0F;
            float vectorValue37 = vectorValue11 + 1.0F;
            amountsVector = getMemPut(amountsVector, deltaVar - vectorValue23 + vectorValue29, deltaVar2 - vectorValue24 + vectorValue30, deltaVar3 - vectorValue25 + vectorValue31, vectorValue36, -1.0F, vectorValue32, amountsMutable);
            amountsVector = getMemPut(amountsVector, deltaVar, deltaVar2, deltaVar3, vectorValue11, -1.0F, vectorValue32, amountsMutable);
            amountsVector = getMemPut(amountsVector, vectorValue26, vectorValue27, vectorValue28, vectorValue11, 1.0F, vectorValue33, amountsMutable);
            amountsVector = getMemPut(amountsVector, vectorValue26 - vectorValue23 + vectorValue29, vectorValue27 - vectorValue24 + vectorValue30, vectorValue28 - vectorValue25 + vectorValue31, vectorValue36, 1.0F, vectorValue33, amountsMutable);
            amountsVector = getMemPut(amountsVector, deltaVar, deltaVar2, deltaVar3, vectorValue11, -1.0F, vectorValue34, amountsMutable);
            amountsVector = getMemPut(amountsVector, deltaVar + vectorValue23 + vectorValue29, deltaVar2 + vectorValue24 + vectorValue30, deltaVar3 + vectorValue25 + vectorValue31, vectorValue37, -1.0F, vectorValue34, amountsMutable);
            amountsVector = getMemPut(amountsVector, vectorValue26 + vectorValue23 + vectorValue29, vectorValue27 + vectorValue24 + vectorValue30, vectorValue28 + vectorValue25 + vectorValue31, vectorValue37, 1.0F, vectorValue35, amountsMutable);
            return getMemPut(amountsVector, vectorValue26, vectorValue27, vectorValue28, vectorValue11, 1.0F, vectorValue35, amountsMutable);
         }
      }
   }

   private static void updateMutableFlag() {
      if (mutableActive) {
         mutableActive = false;
         WORLD_MUTABLE.updateTimesWorld();
      }
   }

   private static void handleLateRender(String lateRenderText, Throwable throwable) {
      lateRenderActive = true;
   }

   private static final class Gpu {
      static final RenderPipeline build = RenderPipeline.builder(RenderPipelines.TRANSFORMS_AND_PROJECTION_SNIPPET)
         .withLocation(Identifier.of("corzclient", "pipeline/atmo_particles"))
         .withVertexShader(Identifier.of("corzclient", "core/corz_atmo"))
         .withFragmentShader(Identifier.of("corzclient", "core/corz_atmo"))
         .withUniform("Fog", UniformType.UNIFORM_BUFFER)
         .withSampler("Sampler2")
         .withVertexFormat(VertexFormats.POSITION_TEXTURE_COLOR_LIGHT, DrawMode.QUADS)
         .withBlend(BlendFunction.TRANSLUCENT)
         .withCull(false)
         .withDepthTestFunction(DepthTestFunction.LEQUAL_DEPTH_TEST)
         .withDepthWrite(false)
         .build();
   }

   public interface Settings {
      float getInteger();

      boolean isFlag();

      float getIntegerHelper();

      int getRosePetals();
   }
}
