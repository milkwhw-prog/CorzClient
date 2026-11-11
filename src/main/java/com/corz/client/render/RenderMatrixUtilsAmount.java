package com.corz.client.render;

import com.corz.client.gui.MatrixMatricesScreen;
import com.corz.client.world.StateDefault;
import java.util.concurrent.CompletableFuture;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import com.corz.client.utils.CornerCam;
import com.corz.client.utils.CornerCamUtils;

public final class RenderMatrixUtilsAmount {
   private static int cabinBoundDivisor = 1;
   private static CompletableFuture<Object[]> supplyAsync;
   static double legNeckAmount;
   private static RenderLayer[] renderLayers;
   static final float PROJECTION_SCALE;
   private static PlayerEntityModel playerEntityModel;
   static final CornerCam CORNER_CAM = new CornerCam();
   private static boolean pipelineFlag;
   private static RenderMatrix renderMatrix;
   private static final float[] defaultTable = new float[70];
   private static RenderLayer noCull;
   private static Object object2CullEntity;
   static final float vectorTransformAmount;
   private static float cabinAmount;
   private static AmbBaked ambBaked;
   static final float[] slimTexturesAmounts = new float[]{0.46F, 0.14F, 0.46F};
   private static StateDefault stateDefault;
   private static long lastNanoTime;
   private static final EntityModelLayer ENTITY_MODEL_LAYER = new EntityModelLayer(Identifier.ofVanilla("player_slim"), "main");
   private static ModelPart modelPart;
   private static RenderLayer entityCutoutNoCull;
   private static int lastCamCornerSize;
   private static final float[] matrixTable = new float[3];
   private static final float[] neckTable = new float[70];
   private static float cabinAmount4CabinMatrix;
   static final float matrixAmount;
   private static float pipelineCabinAmount;
   private static final Matrix4f MATRIX4F = new Matrix4f();
   private static ModelPart modelCullEntity;
   private static boolean cullEntityActive;
   private static float cabinFade;
   private static ModelPart modelPartModel;
   private static RenderLayer entityCutoutNoCullCullEntity;
   private static RenderLayer entityCutoutNoCullEntityRend;
   private static boolean neckActive;
   private static int maxPipelineCabinBound = 1;
   private static final PlayerEntityRenderState PLAYER_ENTITY_RENDER_STATE = new PlayerEntityRenderState();
   static float frameDeltaSeconds;
   private static PlayerEntityModel cullEntityPlayerEntityModel;
   private static final Identifier vanilla = Identifier.ofVanilla("textures/entity/player/wide/steve.png");
   private static final Vector3f VECTOR3F = new Vector3f();
   private static int camCornerValue = -1;
   private static final PlayerEntityRenderState SITTING_RENDER_STATE = new PlayerEntityRenderState();
   private static final float[] table = new float[70];
   private static RenderLayer entityCutout;
   private static Object object3CamCorner;
   private static final Identifier vanillaCullEntity = Identifier.ofVanilla("textures/entity/player/slim/alex.png");
   private static float cabinMatrixAmount;
   static final float[] matrixRenderTable = new float[]{-0.52F, 0.56F, 0.64F};
   private static RenderLayer eyes;
   private static ModelPart modelEntityRender;
   private static RenderLayer cullEntityRenderLayer;
   private static final float[] DEFAULT_DEPTH_OFFSETS = new float[70];

   public static void renderPipelineFlag(DrawContext context, int pipelineValue, int santaArmLimit, int pipelineValue3, int boopHeheBound, int pipelineValue5) {
      if (!pipelineFlag) {
         SantaArmUtils.renderBoopHehe(context, pipelineValue, santaArmLimit, pipelineValue3, boopHeheBound, pipelineValue5);
      }
   }

   private static void handleVisiblePants(PlayerEntityRenderState playerEntityRenderState) {
      playerEntityRenderState.hasVehicle = true;
      playerEntityRenderState.hatVisible = true;
      playerEntityRenderState.jacketVisible = true;
      playerEntityRenderState.leftSleeveVisible = true;
      playerEntityRenderState.rightSleeveVisible = true;
      playerEntityRenderState.leftPantsLegVisible = true;
      playerEntityRenderState.rightPantsLegVisible = true;
      playerEntityRenderState.capeVisible = false;
      playerEntityRenderState.spectator = false;
      playerEntityRenderState.invisible = false;
   }

   public static int getValue(int matrixValue, int matrixValue2, float matrixFactor) {
      matrixFactor = Math.max(0.0F, Math.min(1.0F, matrixFactor));
      int matrixValue3 = matrixValue >> 16 & 0xFF;
      int matrixValue4 = matrixValue >> 8 & 0xFF;
      int matrixValue5 = matrixValue & 0xFF;
      int matrixValue6 = (int)(matrixValue3 + ((matrixValue2 >> 16 & 0xFF) - matrixValue3) * matrixFactor);
      int matrixValue7 = (int)(matrixValue4 + ((matrixValue2 >> 8 & 0xFF) - matrixValue4) * matrixFactor);
      int matrixValue8 = (int)(matrixValue5 + ((matrixValue2 & 0xFF) - matrixValue5) * matrixFactor);
      return 0xFF000000 | matrixValue6 << 16 | matrixValue7 << 8 | matrixValue8;
   }

   public static void update() {
      PartNeckUtils.updateTime();
   }

   static int getMax() {
      return maxPipelineCabinBound;
   }

   public static boolean isPipelineFlag() {
      return SnowVertexUtils.isBaseInstance() && !pipelineFlag;
   }

   static void handle(Matrix4f matrix, VertexConsumer vertices, float matrixValue, float matrixValue2, float matrixValue3, float matrixValue4, int matrixValue5) {
      float matrixValue6 = matrixValue - matrixValue4;
      float vectorTransformValue = matrixValue + matrixValue4;
      float matrixValue8 = matrixValue2 - matrixValue4;
      float matrixValue9 = matrixValue2 + matrixValue4;
      float moonNeckValue = matrixValue3 - matrixValue4;
      float matrixValue11 = matrixValue3 + matrixValue4;
      int matrixValue12 = getValue(matrixValue5, -16777216, 0.22F);
      handleVectorTransform(matrix, vertices, matrixValue6, matrixValue8, matrixValue11, matrixValue5);
      handleVectorTransform(matrix, vertices, vectorTransformValue, matrixValue8, matrixValue11, matrixValue5);
      handleVectorTransform(matrix, vertices, vectorTransformValue, matrixValue9, matrixValue11, matrixValue5);
      handleVectorTransform(matrix, vertices, matrixValue6, matrixValue9, matrixValue11, matrixValue5);
      handleVectorTransform(matrix, vertices, matrixValue6, matrixValue9, moonNeckValue, matrixValue5);
      handleVectorTransform(matrix, vertices, matrixValue6, matrixValue9, matrixValue11, matrixValue5);
      handleVectorTransform(matrix, vertices, vectorTransformValue, matrixValue9, matrixValue11, matrixValue5);
      handleVectorTransform(matrix, vertices, vectorTransformValue, matrixValue9, moonNeckValue, matrixValue5);
      handleVectorTransform(matrix, vertices, matrixValue6, matrixValue8, moonNeckValue, matrixValue12);
      handleVectorTransform(matrix, vertices, matrixValue6, matrixValue8, matrixValue11, matrixValue12);
      handleVectorTransform(matrix, vertices, matrixValue6, matrixValue9, matrixValue11, matrixValue12);
      handleVectorTransform(matrix, vertices, matrixValue6, matrixValue9, moonNeckValue, matrixValue12);
      handleVectorTransform(matrix, vertices, vectorTransformValue, matrixValue8, matrixValue11, matrixValue12);
      handleVectorTransform(matrix, vertices, vectorTransformValue, matrixValue8, moonNeckValue, matrixValue12);
      handleVectorTransform(matrix, vertices, vectorTransformValue, matrixValue9, moonNeckValue, matrixValue12);
      handleVectorTransform(matrix, vertices, vectorTransformValue, matrixValue9, matrixValue11, matrixValue12);
   }

   private static void handleAmountsDefault(Entry entry, VertexConsumer vertices) {
      Matrix4f positionMatrix = entry.getPositionMatrix();
      float legNeckAmount2 = (float)legNeckAmount;
      float defaultValue = ambBaked.getAmounts(StateDefault.defaultTable[0], StateDefault.defaultTable[2]);
      handleVertexVector(
         positionMatrix, vertices, StateDefault.defaultTable[0], StateDefault.defaultTable[1] + 0.3F, StateDefault.defaultTable[2] + 0.6F, 2.4F, getValue(-30160, 0.42F * defaultValue)
      );
      handleVertexVector(
         positionMatrix, vertices, StateDefault.defaultTable[0], StateDefault.defaultTable[1] + 0.2F, StateDefault.defaultTable[2] + 0.7F, 1.0F, getValue(-15254, 0.5F * defaultValue)
      );

      for (float[] defaultValues : stateDefault.defaultFloatArrayList) {
         handleVertexVector(positionMatrix, vertices, defaultValues[0], defaultValues[1], defaultValues[2] + 0.1F, 0.8F, getValue(-18330, 0.36F));
      }

      for (float[] defaultValues2 : stateDefault.ambFloatArrayList) {
         handleVertexVector(
            positionMatrix, vertices, defaultValues2[0], defaultValues2[1], defaultValues2[2] + 0.1F, 0.42F, getValue(-15750, 0.45F * (0.85F + 0.15F * (float)Math.sin(legNeckAmount2 * 13.0F + defaultValues2[0])))
         );
      }

      for (int i = 0; i < stateDefault.spruceDefaultFloatArrayList.size(); i++) {
         float[] defaultValues3 = stateDefault.spruceDefaultFloatArrayList.get(i);
         handleVertexVector(positionMatrix, vertices, defaultValues3[0], defaultValues3[1], defaultValues3[2] + 0.05F, 0.34F, getValue((int)defaultValues3[3], 0.55F * getAmount(i, legNeckAmount2)));
      }

      for (int j = 0; j < stateDefault.defaultPoints.size(); j++) {
         float[] defaultValues4 = stateDefault.defaultPoints.get(j);
         handleVertexVector(positionMatrix, vertices, defaultValues4[0], defaultValues4[1], defaultValues4[2] + 0.05F, 0.24F, getValue((int)defaultValues4[3], 0.5F * getAmount(j + 40, legNeckAmount2)));
      }

      float[] defaultPosition = StateDefault.DEFAULT_POSITION;
      handleVertexVector(
         positionMatrix,
         vertices,
         defaultPosition[0],
         defaultPosition[1] + 0.05F * (float)Math.sin(legNeckAmount2 * 1.3),
         defaultPosition[2] + 0.15F,
         1.1F,
         getValue(-10121, 0.5F * (0.8F + 0.2F * (float)Math.sin(legNeckAmount2 * 2.2)))
      );
   }

   private static int getValue(float matrixValue) {
      int matrixLimit = (int)(matrixValue * 255.0F + 0.5F);
      return matrixLimit < 0 ? 0 : (matrixLimit > 255 ? 255 : matrixLimit);
   }

   static float getAmount(int matrixValue, int i) {
      double sinScaled = Math.sin(matrixValue * 12.9898 + i * 78.233) * 43758.5453;
      return (float)(sinScaled - Math.floor(sinScaled));
   }

   static RenderLayer getCutoutEntity() {
      return entityCutout;
   }

   public static boolean isFlag() {
      PartNeckUtils.updateTime();
      if (PartNeckUtils.isPeekFlag()) {
         PartNeckUtils.updateWoolFall();
         return true;
      } else if (!PartNeckUtils.isRunningFlag()) {
         return false;
      } else {
         PartNeckUtils.updateRunningAmount();
         return true;
      }
   }

   private static void handleMoonNeck(Matrix4f matrix, VertexConsumer vertices) {
      float moonNeckValue = -26.0F;
      int moonNeckValue2 = -16315620;
      int moonNeckValue3 = -15721926;
      int moonNeckValue4 = -14471078;
      handle(matrix, vertices, -70.0F, 6.0F, moonNeckValue, 70.0F, 40.0F, moonNeckValue2, moonNeckValue3);
      handle(matrix, vertices, -70.0F, -6.0F, moonNeckValue, 70.0F, 6.0F, moonNeckValue3, moonNeckValue4);

      for (int i = 0; i < 48; i++) {
         float moonNeckValue5 = -14.0F + 28.0F * getAmount(i, 3);
         float moonNeckValue6 = 3.5F + 8.0F * getAmount(i, 7);
         float moonNeckValue7 = 0.6F + 0.4F * (float)Math.sin(legNeckAmount * (0.8 + getAmount(i, 11)) + i);
         float moonNeckValue8 = 0.045F + 0.05F * getAmount(i, 13);
         handle(matrix, vertices, moonNeckValue5 - moonNeckValue8, moonNeckValue6 - moonNeckValue8, moonNeckValue + 0.3F, moonNeckValue5 + moonNeckValue8, moonNeckValue6 + moonNeckValue8, getValue(-1, -15063992, 1.0F - moonNeckValue7));
      }

      float moonNeckValue9 = CornerCamUtils.getAmount("moon.x", -5.2F);
      float moonNeckValue10 = CornerCamUtils.getAmount("moon.y", 7.4F);
      float moonNeckValue11 = CornerCamUtils.getAmount("moon.s", 1.1F);
      handle(matrix, vertices, moonNeckValue9 - moonNeckValue11 * 1.5F, moonNeckValue10 - moonNeckValue11 * 1.5F, moonNeckValue + 0.4F, moonNeckValue9 + moonNeckValue11 * 1.5F, moonNeckValue10 + moonNeckValue11 * 1.5F, -14010778);
      handle(matrix, vertices, moonNeckValue9 - moonNeckValue11, moonNeckValue10 - moonNeckValue11, moonNeckValue + 0.5F, moonNeckValue9 + moonNeckValue11, moonNeckValue10 + moonNeckValue11, -790312);
      handle(matrix, vertices, moonNeckValue9 - moonNeckValue11 * 0.55F, moonNeckValue10 + moonNeckValue11 * 0.1F, moonNeckValue + 0.55F, moonNeckValue9 - moonNeckValue11 * 0.15F, moonNeckValue10 + moonNeckValue11 * 0.5F, -2566980);
      handle(matrix, vertices, moonNeckValue9 + moonNeckValue11 * 0.2F, moonNeckValue10 - moonNeckValue11 * 0.6F, moonNeckValue + 0.55F, moonNeckValue9 + moonNeckValue11 * 0.6F, moonNeckValue10 - moonNeckValue11 * 0.2F, -2303808);
   }

   static int getBoostTint(float boostTintValue, float boostTintValue2, float defaultValue) {
      if (ambBaked == null) {
         return -1;
      } else {
         float[] ambAmounts = ambBaked.getAmbAmounts(boostTintValue, boostTintValue2, defaultValue, 0.0F, 1.0F, 0.0F);
         float boostTintFactor = ambBaked.getAmounts(boostTintValue, defaultValue);
         float boostTintFactor2 = CornerCamUtils.getAmount("tint.boost", 1.25F);
         int boostTintValue4 = getValue((ambAmounts[0] + ambAmounts[3] * boostTintFactor) * boostTintFactor2);
         int boostTintValue5 = getValue((ambAmounts[1] + ambAmounts[4] * boostTintFactor) * boostTintFactor2);
         int boostTintValue6 = getValue((ambAmounts[2] + ambAmounts[5] * boostTintFactor) * boostTintFactor2);
         return 0xFF000000 | boostTintValue4 << 16 | boostTintValue5 << 8 | boostTintValue6;
      }
   }

   static float getAmount(int matrixValue, float matrixValue2) {
      float sin = (float)Math.sin(matrixValue2 * 1.6 - matrixValue * 0.55);
      return 0.55F + 0.45F * Math.max(0.0F, sin);
   }

   private static void handleCamCorner(Object model, int cullEntityValue) {
      if (stateDefault == null || ambBaked == null || object2CullEntity != model || camCornerValue != cullEntityValue) {
         if (supplyAsync == null || object3CamCorner != model || lastCamCornerSize != cullEntityValue) {
            CORNER_CAM.handleCamCorner(620.0F, 360.0F, 0.0F, 0.0F, true);
            float camCornerAmount = CORNER_CAM.camCornerAmount;
            float camParyAmount = CORNER_CAM.camParyAmount;
            float cornerCamAmount = CORNER_CAM.cornerCamAmount;
            CORNER_CAM.handleCamCorner(620.0F, 360.0F, 0.0F, 0.0F, false);
            float colorsModelsValues = CORNER_CAM.camCornerAmount;
            float camParyAmount2 = CORNER_CAM.camParyAmount;
            float cornerCamAmount2 = CORNER_CAM.cornerCamAmount;
            object3CamCorner = model;
            lastCamCornerSize = cullEntityValue;
            supplyAsync = CompletableFuture.supplyAsync(() -> {
               long var6x = System.nanoTime();
               StateDefault stateDefault2 = StateDefault.getStateDefault();
               AmbBaked ambBaked2 = new AmbBaked();
               ambBaked2.handleAmount(PROJECTION_SCALE, vectorTransformAmount, matrixAmount);
               ambBaked2.handleColorsModels(stateDefault2, colorsModelsValues, camParyAmount2, cornerCamAmount2, camCornerAmount, camParyAmount, cornerCamAmount);
               return new Object[]{stateDefault2, ambBaked2, (System.nanoTime() - var6x) / 1000000L};
            }, Util.getMainWorkerExecutor());
         }
      }
   }

   private static float getAmount(float[] matrixValues, float[] merryAmounts) {
      float y = merryAmounts[0] - matrixValues[0];
      float x = merryAmounts[2] - matrixValues[2];
      float matrixBound = matrixValues[3] - (float)Math.toDegrees(Math.atan2(y, x));
      return Math.max(-55.0F, Math.min(55.0F, matrixBound));
   }

   private static void handleRenderMatrix(String matrixText) {
      pipelineFlag = true;
      PartNeckUtils.updatePeekFlag();
      if (renderMatrix != null) {
         renderMatrix.updateProjectionRaw();
      }

      renderMatrix = null;

      try {
         MinecraftClient.getInstance().gameRenderer.getEntityRenderDispatcher().getQueue().clear();
      } catch (Throwable e) {
      }
   }

   static void handleVectorTransform(Matrix4f matrix, VertexConsumer vertices, float vectorTransformValue, float vectorTransformValue2, float moonNeckValue, int matrixValue) {
      matrix.transformPosition(vectorTransformValue, vectorTransformValue2, moonNeckValue, VECTOR3F);
      vertices.vertex(VECTOR3F.x, VECTOR3F.y, VECTOR3F.z, matrixValue, 0.5F, 0.5F, OverlayTexture.DEFAULT_UV, 15728880, PROJECTION_SCALE, vectorTransformAmount, matrixAmount);
   }

   public static int getValue(int matrixValue) {
      PartNeckUtils.updateTime();
      if (PartNeckUtils.isPeekFlag()) {
         PartNeckUtils.updateWoolFall();
         return 1;
      } else if (!PartNeckUtils.isRunningFlag()) {
         return 0;
      } else if (matrixValue == 256) {
         return 2;
      } else {
         PartNeckUtils.updateRunningAmount();
         return 1;
      }
   }

   public static void update2() {
      PartNeckUtils.updateTimeMerry();
   }

   static int getValue(int matrixValue, float boostTintValue, float matrixValue3, float defaultValue) {
      int boostTint = getBoostTint(boostTintValue, matrixValue3, defaultValue);
      int matrixValue5 = (matrixValue >> 16 & 0xFF) * (boostTint >> 16 & 0xFF) / 255;
      int matrixValue6 = (matrixValue >> 8 & 0xFF) * (boostTint >> 8 & 0xFF) / 255;
      int matrixValue7 = (matrixValue & 0xFF) * (boostTint & 0xFF) / 255;
      return 0xFF000000 | matrixValue5 << 16 | matrixValue6 << 8 | matrixValue7;
   }

   private static boolean isCullEntity() {
      MinecraftClient instance = MinecraftClient.getInstance();
      Object model = getModel(instance);
      int cullEntityValue = CornerCamUtils.getValue();
      if (stateDefault == null || ambBaked == null || object2CullEntity != model || camCornerValue != cullEntityValue) {
         handleCamCorner(model, cullEntityValue);
         if (!supplyAsync.isDone()) {
            cullEntityActive = false;
            return false;
         }

         Object[] join = supplyAsync.join();
         supplyAsync = null;
         stateDefault = (StateDefault)join[0];
         ambBaked = (AmbBaked)join[1];
         object2CullEntity = object3CamCorner;
         camCornerValue = lastCamCornerSize;
      }

      if (playerEntityModel == null) {
         ModelPartUtils.handleIdentifierIdentifiers(instance);
         playerEntityModel = new PlayerEntityModel(instance.getLoadedEntityModels().getModelPart(EntityModelLayers.PLAYER), false);
         cullEntityPlayerEntityModel = new PlayerEntityModel(instance.getLoadedEntityModels().getModelPart(ENTITY_MODEL_LAYER), true);
         noCull = RenderLayers.entityCutoutNoCull(vanilla);
         cullEntityRenderLayer = RenderLayers.entityCutoutNoCull(vanillaCullEntity);
         modelPartModel = ModelPartUtils.getModel7();
         modelEntityRender = ModelPartUtils.getModel8();
         modelPart = ModelPartUtils.getModel3();
         renderLayers = new RenderLayer[ModelPartUtils.presentBlueIdentifiers.length];

         for (int i = 0; i < renderLayers.length; i++) {
            renderLayers[i] = RenderLayers.entityCutoutNoCull(ModelPartUtils.presentBlueIdentifiers[i]);
         }

         entityCutoutNoCullEntityRend = RenderLayers.entityCutoutNoCull(ModelPartUtils.STOCKING_TEXTURE);
         entityCutoutNoCull = RenderLayers.entityCutoutNoCull(ModelPartUtils.STAR_TEXTURE);
         modelCullEntity = ModelPartUtils.getModel5();
         entityCutoutNoCullCullEntity = RenderLayers.entityCutoutNoCull(ModelPartUtils.MUG_TEXTURE);
         entityCutout = RenderLayers.entityCutout(ModelPartUtils.BULB_TEXTURE);
         eyes = RenderLayers.eyes(ModelPartUtils.GLOW_TEXTURE);
         handleVisiblePants(PLAYER_ENTITY_RENDER_STATE);
         handleVisiblePants(SITTING_RENDER_STATE);
      }

      cullEntityActive = true;
      return true;
   }

   private static void handleVertexVector(Matrix4f matrix, VertexConsumer vertices, float vertexVectorValue, float vertexVectorValue2, float vertexVectorValue3, float vertexVectorValue4, int vertexVectorValue5) {
      matrix.transformPosition(vertexVectorValue, vertexVectorValue2, vertexVectorValue3, VECTOR3F);
      float x2 = VECTOR3F.x;
      float y2 = VECTOR3F.y;
      float z2 = VECTOR3F.z;
      vertices.vertex(x2 - vertexVectorValue4, y2 - vertexVectorValue4, z2, vertexVectorValue5, 0.0F, 1.0F, OverlayTexture.DEFAULT_UV, 15728880, 0.0F, 0.0F, 1.0F);
      vertices.vertex(x2 + vertexVectorValue4, y2 - vertexVectorValue4, z2, vertexVectorValue5, 1.0F, 1.0F, OverlayTexture.DEFAULT_UV, 15728880, 0.0F, 0.0F, 1.0F);
      vertices.vertex(x2 + vertexVectorValue4, y2 + vertexVectorValue4, z2, vertexVectorValue5, 1.0F, 0.0F, OverlayTexture.DEFAULT_UV, 15728880, 0.0F, 0.0F, 1.0F);
      vertices.vertex(x2 - vertexVectorValue4, y2 + vertexVectorValue4, z2, vertexVectorValue5, 0.0F, 0.0F, OverlayTexture.DEFAULT_UV, 15728880, 0.0F, 0.0F, 1.0F);
   }

   static boolean canCabinMatrix(float cabinMatrixValue, float cabinMatrixValue2, float[] cabinMatrixValues, float[] cabinMatrixValues2, int orderWindowLimit, float orderValue, float cabinAmount4, float cabinMatrixFactor, float rocketsElytraBound, boolean cabinMatrixFlag, float matrixRenderValue) {
      if (cullEntityActive && !pipelineFlag && (renderMatrix != null || RenderMatrix.isFlag())) {
         try {
            MinecraftClient instance = MinecraftClient.getInstance();
            float scaleFactor = instance.getWindow().getScaleFactor();
            float cabinMatrixFactor3 = RenderMatrix.getAmount(cabinMatrixFactor, rocketsElytraBound, scaleFactor);
            int textureCreateBound;
            int rocketsBuyBound;
            if (cabinMatrixFactor3 >= scaleFactor - 0.001F) {
               float floor7 = (float)Math.floor(orderValue * scaleFactor);
               float floor8 = (float)Math.floor(cabinAmount4 * scaleFactor);
               textureCreateBound = Math.max(8, (int)Math.ceil((orderValue + cabinMatrixFactor) * scaleFactor - floor7));
               rocketsBuyBound = Math.max(8, (int)Math.ceil((cabinAmount4 + rocketsElytraBound) * scaleFactor - floor8));
               orderValue = floor7 / scaleFactor;
               cabinAmount4 = floor8 / scaleFactor;
               cabinMatrixFactor = textureCreateBound / scaleFactor;
               rocketsElytraBound = rocketsBuyBound / scaleFactor;
            } else {
               textureCreateBound = Math.max(8, (int)Math.ceil(cabinMatrixFactor * cabinMatrixFactor3));
               rocketsBuyBound = Math.max(8, (int)Math.ceil(rocketsElytraBound * cabinMatrixFactor3));
            }

            cabinAmount = orderValue;
            cabinAmount4CabinMatrix = cabinAmount4;
            CORNER_CAM.handleCamCorner(cabinMatrixFactor, rocketsElytraBound, pipelineCabinAmount * 0.6F, cabinMatrixAmount * 0.6F, cabinMatrixFlag);
            ambBaked.handleAmounts(legNeckAmount);
            updateAmountsNeck();
            CorzRoundedUtils.updateSamplerView();
            if (renderMatrix == null) {
               renderMatrix = new RenderMatrix("cabin", matrixRenderTable, slimTexturesAmounts);
            }

            renderMatrix.handleTextureCreate(textureCreateBound, rocketsBuyBound);
            renderMatrix.handleShaderProjection(0, 0, textureCreateBound, rocketsBuyBound, CORNER_CAM.camCornerMatrix4f, RenderMatrixUtilsAmount::handleEntityRender);
            renderMatrix.handleShaderProjection(0, 0, textureCreateBound, rocketsBuyBound, CORNER_CAM.camCornerMatrix4f, RenderMatrixUtilsAmount::handleCustomSubmit);
            renderMatrix.handleOrderWindow(MATRIX4F, cabinMatrixValue, cabinMatrixValue2, cabinMatrixValues, cabinMatrixValues2, orderWindowLimit, orderValue, cabinAmount4, cabinMatrixFactor, rocketsElytraBound, textureCreateBound, rocketsBuyBound, matrixRenderValue);
            return true;
         } catch (Throwable e) {
            handleRenderMatrix(String.valueOf(e));
            return false;
         }
      } else {
         return false;
      }
   }

   public static float getAmountFlag() {
      return cullEntityActive ? cabinFade : 0.0F;
   }

   private static void handle(Matrix4f matrix, VertexConsumer vertices, float matrixValue, float matrixValue2, float moonNeckValue, float vectorTransformValue, float matrixValue5, int matrixValue6, int matrixValue7) {
      handleVectorTransform(matrix, vertices, matrixValue, matrixValue2, moonNeckValue, matrixValue7);
      handleVectorTransform(matrix, vertices, vectorTransformValue, matrixValue2, moonNeckValue, matrixValue7);
      handleVectorTransform(matrix, vertices, vectorTransformValue, matrixValue5, moonNeckValue, matrixValue6);
      handleVectorTransform(matrix, vertices, matrixValue, matrixValue5, moonNeckValue, matrixValue6);
   }

   static {
      float matrixFactor = matrixRenderTable[0] + slimTexturesAmounts[0];
      float matrixFactor2 = matrixRenderTable[1] + slimTexturesAmounts[1];
      float matrixFactor3 = matrixRenderTable[2] + slimTexturesAmounts[2];
      float distance = (float)Math.sqrt(matrixFactor * matrixFactor + matrixFactor2 * matrixFactor2 + matrixFactor3 * matrixFactor3);
      PROJECTION_SCALE = matrixFactor / distance;
      vectorTransformAmount = matrixFactor2 / distance;
      matrixAmount = matrixFactor3 / distance;
   }

   static float getNeckLeg() {
      long nanoTime = System.nanoTime();
      long neckLegValue = lastNanoTime == 0L ? 0L : nanoTime - lastNanoTime;
      lastNanoTime = nanoTime;
      frameDeltaSeconds = Math.min(0.05F, (float)neckLegValue / 1.0E9F);
      legNeckAmount = legNeckAmount + frameDeltaSeconds;
      return frameDeltaSeconds;
   }

   static boolean canCabinCam(float cabinCamValue, float cabinCamValue2, float cabinCamValue3, float[] cabinCamValues) {
      if (!CORNER_CAM.canVectorTransform(cabinCamValue, cabinCamValue2, cabinCamValue3, cabinCamValues)) {
         return false;
      } else {
         cabinCamValues[0] += cabinAmount;
         cabinCamValues[1] += cabinAmount4CabinMatrix;
         return true;
      }
   }

   static void updateRenderPipeline() {
      if (!pipelineFlag && isPipelineFlag()) {
         try {
            MinecraftClient instance = MinecraftClient.getInstance();
            if (instance.getBlockRenderManager() == null) {
               return;
            }

            handleCamCorner(getModel(instance), CornerCamUtils.getValue());
         } catch (Throwable e) {
            handleRenderMatrix("prewarm " + e);
         }
      }
   }

   public static void handlePipelineCabin(
      int pipelineCabinBound, int rocketsBuyBound, float pipelineCabinLimit, int pipelineCabinValue, int merryValue, boolean pipelineCabinFlag, boolean pipelineCabinFlag2, boolean pipelineCabinFlag3, boolean pipelineCabinFlag4, int scissorMatrixValue, int randomValue, int pipelineCabinValue5, int pipelineCabinValue6
   ) {
      getNeckLeg();
      cabinBoundDivisor = Math.max(1, pipelineCabinBound);
      maxPipelineCabinBound = Math.max(1, rocketsBuyBound);
      PartNeckUtils.handleMerryAmount(scissorMatrixValue, randomValue, pipelineCabinValue5, pipelineCabinValue6, 12.0F);
      if (pipelineCabinFlag && pipelineCabinFlag2) {
         boolean pipelineFlag2 = isPipelineFlag();
         float pipelineCabinValue7 = pipelineFlag2 ? 1.0F : 0.0F;
         cabinFade = cabinFade + (pipelineCabinValue7 - cabinFade) * (1.0F - (float)Math.exp(-(pipelineFlag2 ? 3.2F : 6.0F) * frameDeltaSeconds));
         if (!pipelineFlag2 && cabinFade < 0.004F) {
            cabinFade = 0.0F;
         }

         float pipelineCabinBound3 = ((float)pipelineCabinValue / cabinBoundDivisor - 0.5F) * 2.0F;
         float hashHuntBound = ((float)merryValue / maxPipelineCabinBound - 0.5F) * 2.0F;
         float pipelineCabinFactor = 1.0F - (float)Math.exp(-2.4F * frameDeltaSeconds);
         pipelineCabinAmount = pipelineCabinAmount + (Math.max(-1.0F, Math.min(1.0F, pipelineCabinBound3)) - pipelineCabinAmount) * pipelineCabinFactor;
         cabinMatrixAmount = cabinMatrixAmount + (-Math.max(-1.0F, Math.min(1.0F, hashHuntBound)) - cabinMatrixAmount) * pipelineCabinFactor;
         if (pipelineFlag2 && !pipelineFlag) {
            try {
               if (!RenderMatrix.isFlag()) {
                  handleRenderMatrix("pipeline");
               } else {
                  isCullEntity();
               }
            } catch (Throwable e) {
               handleRenderMatrix(String.valueOf(e));
            }
         }

         boolean windowFocused = MinecraftClient.getInstance().isWindowFocused();
         if (!windowFocused) {
            PartNeckUtils.updateTime();
         }

         PartNeckUtils.handlePeekAmount(
            frameDeltaSeconds, pipelineCabinValue, merryValue, pipelineCabinBound, rocketsBuyBound, windowFocused && pipelineCabinFlag3 && pipelineCabinLimit > 0.995F && getAmountFlag() > 0.98F && !MatrixMatricesScreen.isMatrixMatricesActive(), pipelineCabinFlag4
         );
      } else {
         if (!pipelineCabinFlag) {
            cabinFade = 0.0F;
         }

         PartNeckUtils.updateTimeMerry();
      }
   }

   public static void renderPipelineFlag(DrawContext context, int pipelineValue, int ctxOffset, float merryFactor, float pipelineValue4) {
      if (!pipelineFlag) {
         PartNeckUtils.renderMerryAmount(context, pipelineValue, ctxOffset, merryFactor, pipelineValue4);
      }
   }

   public static void updateAmountTime() {
      lastNanoTime = 0L;
      cabinFade = isPipelineFlag() ? 1.0F : 0.0F;
      PartNeckUtils.updateTimeMerry();
      updateRenderPipeline();
   }

   public static boolean isFlag2() {
      return PartNeckUtils.isRunning();
   }

   public static void updateRenderMatrix() {
      if (renderMatrix != null) {
         renderMatrix.updateProjectionRaw();
      }

      renderMatrix = null;
      PartNeckUtils.updateRenderMatrix();
      SantaArmUtils.updateBoopHehe();
   }

   private static int getValue(int matrixValue, float matrixBound) {
      int round = Math.round(255.0F * Math.max(0.0F, Math.min(1.0F, matrixBound)));
      return round << 24 | matrixValue & 16777215;
   }

   private static void handleDefaultAmounts(Entry entry, VertexConsumer vertices) {
      Matrix4f positionMatrix = entry.getPositionMatrix();
      handleMoonNeck(positionMatrix, vertices);

      for (int i = 0; i < 70; i++) {
         float defaultValue = defaultTable[i];
         handle(positionMatrix, vertices, neckTable[i] - defaultValue, table[i] - defaultValue, DEFAULT_DEPTH_OFFSETS[i], neckTable[i] + defaultValue, table[i] + defaultValue, -722689);
      }

      float legNeckAmount2 = (float)legNeckAmount;

      for (int j = 0; j < stateDefault.spruceDefaultFloatArrayList.size(); j++) {
         float[] defaultValues = stateDefault.spruceDefaultFloatArrayList.get(j);
         handle(positionMatrix, vertices, defaultValues[0], defaultValues[1], defaultValues[2], 0.085F, getValue((int)defaultValues[3], -14673902, 1.0F - getAmount(j, legNeckAmount2)));
      }

      for (int k = 0; k < stateDefault.floatArrayList.size(); k++) {
         float[] defaultValues2 = stateDefault.floatArrayList.get(k);
         boolean defaultFlag = defaultValues2[3] < 0.0F;
         float defaultValue2 = getAmount(k, 51);
         int matrixValue = defaultFlag ? -3663316 : (defaultValue2 < 0.33F ? -14788053 : (defaultValue2 < 0.66F ? -13997003 : -15251422));
         handle(positionMatrix, vertices, defaultValues2[0], defaultValues2[1], defaultValues2[2], Math.abs(defaultValues2[3]), getValue(matrixValue, defaultValues2[0], defaultValues2[1], defaultValues2[2]));
      }

      for (int m = 0; m < stateDefault.defaultPoints.size(); m++) {
         float[] defaultValues3 = stateDefault.defaultPoints.get(m);
         handle(positionMatrix, vertices, defaultValues3[0], defaultValues3[1], defaultValues3[2], 0.05F, getValue((int)defaultValues3[3], -14673902, 1.0F - getAmount(m + 40, legNeckAmount2)));
      }
   }

   private static void handleCustomSubmit(MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue) {
      handleMatrixMul(matrices);
      orderedRenderCommandQueue.submitCustom(matrices, eyes, RenderMatrixUtilsAmount::handleAmountsDefault);
      PartNeckUtils.handleNeckChild(matrices, orderedRenderCommandQueue);
   }

   static void handlePartModel(MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, float x2, float y2, float z2, float partModelValue, float degrees, int i) {
      if (modelPartModel != null) {
         matrices.push();
         matrices.translate(x2, y2, z2);
         matrices.multiply(new Quaternionf().rotateY((float)Math.toRadians(degrees)));
         float yawScaled = partModelValue * 1.3F;
         matrices.scale(yawScaled, -yawScaled, -yawScaled);
         orderedRenderCommandQueue.submitModelPart(
            modelPartModel,
            matrices,
            renderLayers[Math.floorMod(i, renderLayers.length)],
            15728880,
            OverlayTexture.DEFAULT_UV,
            null,
            getBoostTint(x2, y2 + 0.4F, z2),
            null
         );
         matrices.pop();
      }
   }

   static int getCabinBoundDivisor() {
      return cabinBoundDivisor;
   }

   static void handle(Matrix4f matrix, VertexConsumer vertices, float matrixValue, float matrixValue2, float moonNeckValue, float vectorTransformValue, float matrixValue5, int matrixValue6) {
      handleVectorTransform(matrix, vertices, matrixValue, matrixValue2, moonNeckValue, matrixValue6);
      handleVectorTransform(matrix, vertices, vectorTransformValue, matrixValue2, moonNeckValue, matrixValue6);
      handleVectorTransform(matrix, vertices, vectorTransformValue, matrixValue5, moonNeckValue, matrixValue6);
      handleVectorTransform(matrix, vertices, matrixValue, matrixValue5, moonNeckValue, matrixValue6);
   }

   static void handleMatrixMul(MatrixStack matrices) {
      Entry entry = matrices.peek();
      entry.getPositionMatrix().mul(CORNER_CAM.viewMatrix);
      entry.getNormalMatrix().mul(CORNER_CAM.camCornerMatrix3f);
   }

   private static Object getModel(MinecraftClient client) {
      return client.getBlockRenderManager().getModels().getModel(Blocks.WHITE_CONCRETE.getDefaultState());
   }

   public static boolean canPipelineFlag(double pipelineValue, double heightAmount, int button) {
      if (button != 0 || PartNeckUtils.isRunningFlag() || getAmountFlag() < 0.9F || pipelineFlag) {
         return false;
      } else {
         return PartNeckUtils.canMerryAmount((float)pipelineValue, (float)heightAmount) ? PartNeckUtils.isWelcomeWool() : SantaArmUtils.canWokeSanta(pipelineValue, heightAmount);
      }
   }

   private static void updateAmountsNeck() {
      if (!neckActive) {
         neckActive = true;

         for (int i = 0; i < 70; i++) {
            neckTable[i] = -9.0F + 18.0F * getAmount(i, 21);
            table[i] = 9.0F * getAmount(i, 23);
            DEFAULT_DEPTH_OFFSETS[i] = -1.6F - 9.0F * getAmount(i, 29);
            defaultTable[i] = 0.03F + 0.035F * getAmount(i, 31);
         }
      }

      for (int j = 0; j < 70; j++) {
         table[j] = table[j] - frameDeltaSeconds * (0.55F + 0.5F * getAmount(j, 37));
         neckTable[j] = neckTable[j] + frameDeltaSeconds * 0.25F * (float)Math.sin(legNeckAmount * 0.7 + j);
         if (table[j] < -0.2F) {
            table[j] = 9.0F;
            neckTable[j] = -9.0F + 18.0F * getAmount(j + (int)(legNeckAmount * 10.0), 41);
         }
      }
   }

   private static void handleEntityRender(MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue) {
      handleMatrixMul(matrices);
      orderedRenderCommandQueue.submitCustom(matrices, TexturedRenderLayers.getEntityCutout(), ambBaked::handleVectorPosition);
      orderedRenderCommandQueue.submitCustom(matrices, entityCutout, RenderMatrixUtilsAmount::handleDefaultAmounts);
      float legNeckAmount2 = (float)legNeckAmount;
      float[] merryAmounts = PartNeckUtils.getMerryAmounts();
      PLAYER_ENTITY_RENDER_STATE.age = legNeckAmount2 * 20.0F;
      SITTING_RENDER_STATE.age = legNeckAmount2 * 20.0F + 37.0F;
      if (merryAmounts != null) {
         PLAYER_ENTITY_RENDER_STATE.relativeHeadYaw = getAmount(StateDefault.entityRenderTable, merryAmounts);
         SITTING_RENDER_STATE.relativeHeadYaw = getAmount(StateDefault.table, merryAmounts);
         PLAYER_ENTITY_RENDER_STATE.pitch = SITTING_RENDER_STATE.pitch = -4.0F;
      } else {
         PLAYER_ENTITY_RENDER_STATE.relativeHeadYaw = 26.0F * (float)Math.sin(legNeckAmount2 * 0.21) - 8.0F;
         PLAYER_ENTITY_RENDER_STATE.pitch = 6.0F + 4.0F * (float)Math.sin(legNeckAmount2 * 0.33);
         SITTING_RENDER_STATE.relativeHeadYaw = -22.0F * (float)Math.sin(legNeckAmount2 * 0.17 + 1.2) + 10.0F;
         SITTING_RENDER_STATE.pitch = 4.0F + 5.0F * (float)Math.sin(legNeckAmount2 * 0.29 + 0.4);
      }

      handleSitTranslate(matrices, orderedRenderCommandQueue, playerEntityModel, PLAYER_ENTITY_RENDER_STATE, noCull, StateDefault.entityRenderTable);
      handleSitTranslate(matrices, orderedRenderCommandQueue, cullEntityPlayerEntityModel, SITTING_RENDER_STATE, cullEntityRenderLayer, StateDefault.table);

      for (float[] entityRenderValues : stateDefault.entityRenderFloatArrayList) {
         handlePartModel(matrices, orderedRenderCommandQueue, entityRenderValues[0], entityRenderValues[1], entityRenderValues[2], entityRenderValues[3], entityRenderValues[4], (int)entityRenderValues[5]);
      }

      int entityRenderBound = PartNeckUtils.getMin();
      float[][] floats = StateDefault.getAmounts();

      for (int i = 0; i < Math.min(entityRenderBound, floats.length); i++) {
         handlePartModel(matrices, orderedRenderCommandQueue, floats[i][0], 0.0F, floats[i][1], floats[i][2], floats[i][3], (i + 1) % 4);
      }

      for (int j = 0; j < stateDefault.entityRenderPoints.size(); j++) {
         float[] entityRenderValues2 = stateDefault.entityRenderPoints.get(j);
         matrices.push();
         matrices.translate(entityRenderValues2[0], entityRenderValues2[1], entityRenderValues2[2]);
         matrices.multiply(new Quaternionf().rotateZ(entityRenderValues2[3] + 0.04F * (float)Math.sin(legNeckAmount2 * 0.9 + j)));
         matrices.scale(1.5F, -1.5F, -1.5F);
         orderedRenderCommandQueue.submitModelPart(modelEntityRender, matrices, entityCutoutNoCullEntityRend, 15728880, OverlayTexture.DEFAULT_UV, null, getBoostTint(entityRenderValues2[0], entityRenderValues2[1], entityRenderValues2[2]), null);
         matrices.pop();
      }

      matrices.push();
      float[] defaultPosition = StateDefault.DEFAULT_POSITION;
      matrices.translate(defaultPosition[0], defaultPosition[1] + 0.05F * (float)Math.sin(legNeckAmount2 * 1.3), defaultPosition[2]);
      matrices.multiply(new Quaternionf().rotateY(0.35F * (float)Math.sin(legNeckAmount2 * 0.6)));
      matrices.scale(1.3F, -1.3F, -1.3F);
      orderedRenderCommandQueue.submitModelPart(modelPart, matrices, entityCutoutNoCull, 15728880, OverlayTexture.DEFAULT_UV, null, -1, null);
      matrices.pop();
      PartNeckUtils.handleLimbAmount(matrices, orderedRenderCommandQueue);
   }

   private RenderMatrixUtilsAmount() {
   }

   private static void handleSitTranslate(
      MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, PlayerEntityModel playerEntityModel, PlayerEntityRenderState angles, RenderLayer renderLayer, float[] sitTranslateValues
   ) {
      matrices.push();
      matrices.translate(sitTranslateValues[0], sitTranslateValues[1] + CornerCamUtils.getAmount("sit.y", -0.14F), sitTranslateValues[2]);
      matrices.multiply(new Quaternionf().rotateY((float)Math.toRadians(sitTranslateValues[3])));
      matrices.scale(0.9375F, -0.9375F, -0.9375F);
      matrices.translate(0.0F, -1.501F, 0.0F);
      orderedRenderCommandQueue.submitModel(playerEntityModel, angles, matrices, renderLayer, 15728880, OverlayTexture.DEFAULT_UV, getBoostTint(sitTranslateValues[0], 1.2F, sitTranslateValues[2]), null, 0, null);
      playerEntityModel.setAngles(angles);
      matrices.push();
      playerEntityModel.rightArm.applyTransform(matrices);
      matrices.translate(-0.025F, 0.6F, -0.0375F);
      matrices.multiply(new Quaternionf().rotateX(-playerEntityModel.rightArm.pitch - 0.15F));
      orderedRenderCommandQueue.submitModelPart(modelCullEntity, matrices, entityCutoutNoCullCullEntity, 15728880, OverlayTexture.DEFAULT_UV, null, getBoostTint(sitTranslateValues[0], 1.0F, sitTranslateValues[2]), null);
      matrices.pop();
      matrices.pop();
   }
}
