package com.corz.client.render;

import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.gui.MatrixStackUtilsRelease;
import com.corz.client.inventory.DrawMatricesUtils;
import com.corz.client.inventory.StackRegistryUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.EndCrystalEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel.ArmPose;
import net.minecraft.client.render.entity.model.EndCrystalEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.model.VillagerResemblingModel;
import net.minecraft.client.render.entity.model.WitchEntityModel;
import net.minecraft.client.render.entity.state.EndCrystalEntityRenderState;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.client.render.entity.state.VillagerEntityRenderState;
import net.minecraft.client.render.item.ItemRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.BlockItem;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Box;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import com.corz.client.utils.AmountConstStrain;
import com.corz.client.utils.CpxVillagerData;
import com.corz.client.utils.RotateQuaternionfUtils;
import com.corz.client.utils.ScissorTgtHandler;
import com.corz.client.utils.StandbyDanger;

public final class MatrixRender {
   private double lastPosX;
   private ItemStack empty;
   private RenderLayer eyes;
   private final Vector3f vectorQuaternionfVector3f;
   private RenderLayer renderLayer;
   private float leftMultiplyAmount;
   private RenderLayer entityRenderLayer;
   private static final EntityModelLayer ENTITY_MODEL_LAYER = new EntityModelLayer(Identifier.ofVanilla("player_slim"), "main");
   private boolean entityActive;
   private VillagerResemblingModel villagerResemblingModel;
   private final EndCrystalEntityRenderState endCrystalEntityRenderSt;
   private boolean vectorQuaternionfActive;
   private float multiplyRotationAmount;
   private static final Identifier vanillaEntity = Identifier.ofVanilla("textures/entity/end_crystal/end_crystal.png");
   private float botsMatrixAmount;
   private final AmountConstStrain vectorQuaternionfAmountConst;
   private int modelTintColor;
   private RenderLayer noCull;
   private static final Identifier IDENTIFIER = CpxVillagerData.getIdentifierForString("cpx01");
   private float springFrameDelta;
   private float totemsCrystalsAmount;
   private final Quaternionf quaternionf;
   private float matrixRenderAmount;
   private float vectorAmount;
   private final StageKnife.Scene botsMatrixScene;
   private float crystalsAlphaScale;
   private EndCrystalEntityModel endCrystalEntityModel;
   private double lastPosY;
   private final Vector3f controlPointB;
   private float totemsAmount;
   private float cpxCount;
   private float lengthAmount;
   private long lastNanoTime;
   private final Vector3f leftEyePos;
   private boolean totemsActive;
   private final Vector3f springPos;
   private final StandbyDanger matricesCpxStandbyDanger;
   private RenderLayer entityCutoutNoCull;
   private float y;
   private float cpxAmount;
   private boolean matrixRenderActive;
   private boolean crystalsActive;
   private float vectorProjectAmount;
   private static final int UNUSED_ONE = 1;
   private final MatrixStack matrixStack;
   private RenderLayer entityCutoutNoCullHatCull;
   private static final Identifier CPX01_EYES_TEXTURE = CpxVillagerData.getIdentifierForString("cpx01_eyes");
   private float crystalsTotemsAmount;
   private CpxVillagerData cpxVillagerData;
   private final Vector3f rightEyePos;
   private float scaledVectorAmount;
   private boolean cpxActive;
   private RenderLayer entityCutoutNoCullModelCull;
   private float faceDepthScale;
   private int hatCullValue;
   private float eyesCenterX;
   private float modelCenterY;
   private float projectionDepthScale;
   private float multiplyCenterZ;
   private boolean crystalsForced;
   private final Vector3f noseTipPos;
   private final Quaternionf multiplyRotationQuaternionf;
   private float cpxPitch;
   private final Vector3f neckPos;
   private float totemsOffsetX;
   private final Quaternionf vectorQuaternionf;
   private static final Quaternionf rotationX = new Quaternionf().rotationX((float) Math.PI);
   private final float[] vectorTransformTable;
   private RenderLayer hatCullRenderLayer;
   private float modelCenterX;
   private float modelScale;
   private final Vector3f orbitRotationVec;
   private ModelPart hat;
   private final Quaternionf vectorTranslateQuaternionf;
   private final PositionMatrixCustom positionMatrixCustom;
   private final Vector3f transformedAnchorPos;
   private static final Quaternionf rotationY = new Quaternionf().rotationY((float) Math.PI);
   private float multiplyCenterY;
   private float trackedAmountA;
   private final Matrix4f botsMatrixMatrix4f;
   private final Vector3f springVelocity;
   private static final Quaternionf rotationXMultiplyRender = new Quaternionf().rotationX((float) (-Math.PI / 2));
   private boolean slimEntity;
   private final Vector3f springTargetPos;
   private static final Identifier vanillaHatCull = Identifier.ofVanilla("textures/entity/villager/type/plains.png");
   private float x;
   private final VillagerEntityRenderState villagerEntityRenderStat;
   private boolean totemsFlag;
   private ModelPart modelPart;
   private static final Identifier vanilla = Identifier.ofVanilla("textures/entity/villager/profession/librarian.png");
   private float vectorTranslateAmount;
   private float matrixVillagerAmount;
   private final StageKnife bots = new StageKnife("bots");
   private float trackedAmountB;
   private float rightEyeX;
   private StandbyDanger.Mode standbyDangerType;
   public static final String CRYSTALS = "No crystals";
   private float crystalsCount;
   public static final String TOTEMS = "No totems";
   private RadiansVectorModel radiansVectorModel;
   private int amountStandby;
   private final RotateQuaternionfUtils.Orbit crystalsOrbit;
   private final Vector3f noseBasePos;
   private ModelPart child;
   private float rightEyeY;
   private static final Identifier WITCH_TEXTURE = Identifier.ofVanilla("textures/entity/witch.png");
   private boolean matrixVillagerActive;
   private float crystalsAmount;
   private float crystalsFade;
   private final Quaternionf leftMultiplyQuaternionf;
   private final Vector3f controlPointC;
   private final PlayerEntityRenderState playerEntityRenderState;
   private final ItemRenderState itemRenderState;
   private boolean crystalsFlag;
   private float vectorTransformAmount;
   private boolean modelCullActive;
   private float eyesCenterY;
   private final Vector3f projectedPoint;
   private final Vector3f controlPointD;
   private float crystalsOrbitRadians;
   private float totemsOffsetY;

   private void renderBotsMatrix(DrawContext context, float botsMatrixValue, float peekPositionFactor, float matrixRotateFactor, float amount, float botsMatrixFactor, float panelOverlay) {
      double scaleFactor = MinecraftClient.getInstance().getWindow().getScaleFactor();
      int textureCreateBound = MatrixRotateUtilsHandle.getValue(matrixRotateFactor, scaleFactor, 2);
      int botsMatrixDivisor = MatrixRotateUtilsHandle.getValue(amount, scaleFactor, 2);
      float matrixRotateValue = (float)textureCreateBound / botsMatrixDivisor;
      this.projectionDepthScale = amount;
      this.botsMatrixAmount = MatrixRotateUtilsHandle.getAmount(matrixRotateValue);
      MatrixRotateUtilsHandle.getPerspective(this.botsMatrixMatrix4f, matrixRotateValue, this.botsMatrixAmount);
      this.updateAmountCrystals();
      this.updateRenderEntity();
      CorzRoundedUtils.updateSamplerView();
      this.bots.handleTextureCreate(textureCreateBound, botsMatrixDivisor);
      this.bots.handleShaderProjection(0, 0, textureCreateBound, botsMatrixDivisor, this.botsMatrixMatrix4f, this.botsMatrixScene);
      Matrix4f positionMatrix = MatrixPeekUtilsRender.getMatrixStack().peek().getPositionMatrix();
      this.bots
         .handleBufferSampler(
            positionMatrix,
            botsMatrixValue,
            peekPositionFactor,
            matrixRotateFactor,
            amount,
            this.bots.getMin(0),
            this.bots.getMinForInt(0),
            this.bots.getMin(textureCreateBound),
            this.bots.getMinForInt(botsMatrixDivisor),
            MatrixStackUtilsRelease.getPeekPositionForFloat(botsMatrixValue),
            MatrixStackUtilsRelease.getPeekPosition(peekPositionFactor),
            MatrixStackUtilsRelease.getPeekPositionForFloat(botsMatrixValue + matrixRotateFactor),
            MatrixStackUtilsRelease.getPeekPosition(peekPositionFactor + amount),
            botsMatrixFactor * MatrixStackUtilsRelease.getMFloat(),
            panelOverlay
         );
      this.renderCpxAmount(context, botsMatrixValue, peekPositionFactor, matrixRotateFactor, amount);
   }

   private void handleOriginTransform(Vector3f vector3f, float originTransformValue, float originTransformValue2, Matrix4f matrix, ModelPart modelPart) {
      vector3f.set(originTransformValue, -0.28125F, originTransformValue2);
      this.quaternionf.transform(vector3f);
      vector3f.add(modelPart.originX / 16.0F, modelPart.originY / 16.0F, modelPart.originZ / 16.0F);
      matrix.transformPosition(vector3f);
   }

   public CpxVillagerData getCpxVillagerData() {
      return this.cpxVillagerData;
   }

   public void setFlagForFlag(boolean flag) {
      this.crystalsForced = flag;
   }

   private void updateVectorAmount() {
      this.scaledVectorAmount = (0.72F + 0.27999997F * this.crystalsAmount) * this.vectorAmount;
      this.amountStandby = getValue(this.vectorQuaternionfAmountConst.glowStrength * this.scaledVectorAmount);
      this.modelTintColor = PositionMatrixCustom.getValueForIntFloat(getValue(this.vectorQuaternionfAmountConst.strainAmount * this.scaledVectorAmount), 0.9F + -0.45F * this.vectorQuaternionfAmountConst.strainAmount);
      VectorRadiansUtils.getRadiansXyz(this.vectorQuaternionf, this.vectorQuaternionfAmountConst.vectorAmount);
      VectorRadiansUtils.getRotate(this.orbitRotationVec, this.crystalsOrbitRadians + VectorRadiansUtils.getAmount(this.crystalsOrbit.getSmoothedYaw() - -16.0F) * (float) (Math.PI / 180.0));
      if (this.radiansVectorModel != null) {
         VectorRadiansUtils.getInverseTransform(this.radiansVectorModel.armRadiansVector3f, this.orbitRotationVec, this.vectorQuaternionf);
      }
   }

   private void handleHatCull(MinecraftClient client, CpxVillagerData cpxVillagerData) {
      if (this.villagerResemblingModel == null) {
         this.villagerResemblingModel = new VillagerResemblingModel(client.getLoadedEntityModels().getModelPart(EntityModelLayers.VILLAGER));
         this.child = this.villagerResemblingModel.getHead().getChild("hat");
      }

      if (this.hat == null) {
         try {
            this.hat = new WitchEntityModel(client.getLoadedEntityModels().getModelPart(EntityModelLayers.WITCH)).getHead().getChild("hat");
         } catch (Throwable e) {
         }
      }

      this.itemRenderState.clear();
      this.modelScale = 0.0F;
      this.noCull = RenderLayers.entityCutoutNoCull(cpxVillagerData.skin());
      this.entityCutoutNoCullHatCull = RenderLayers.entityCutoutNoCull(vanillaHatCull);
      this.hatCullRenderLayer = RenderLayers.entityCutoutNoCull(vanilla);
      this.entityCutoutNoCull = RenderLayers.entityCutoutNoCull(WITCH_TEXTURE);
      this.eyes = this.entityRenderLayer = this.renderLayer = null;
      this.entityActive = false;
      this.hatCullValue = -1;
      this.playerEntityRenderState.rightHandItemState.clear();
      this.playerEntityRenderState.leftHandItemState.clear();
      this.villagerEntityRenderStat.headRolling = false;
      this.villagerEntityRenderStat.baby = false;
      this.villagerEntityRenderStat.invisible = false;
   }

   private void updateDispatcherQueue() {
      try {
         this.bots.updateTextureClose();
      } catch (Throwable e) {
      }

      try {
         MinecraftClient.getInstance().gameRenderer.getEntityRenderDispatcher().getQueue().clear();
      } catch (Throwable e2) {
      }
   }

   private boolean canAmount(float matrixRenderValue, float matrixRenderLimit) {
      return !Float.isNaN(this.trackedAmountA) && this.trackedAmountA < matrixRenderLimit && this.trackedAmountB > matrixRenderValue;
   }

   private void updateLengthAmount() {
      this.modelScale = 0.0F;
      if (!this.itemRenderState.isEmpty()) {
         Box modelBoundingBox = this.itemRenderState.getModelBoundingBox();
         double lengthX = modelBoundingBox.getLengthX();
         double lengthY = modelBoundingBox.getLengthY();
         double lengthZ = modelBoundingBox.getLengthZ();
         float maxSqrt = (float)Math.max(lengthY, Math.sqrt(lengthX * lengthX + lengthZ * lengthZ));
         this.modelScale = 0.9F / Math.max(0.05F, maxSqrt);
         this.modelCenterX = (float)((modelBoundingBox.minX + modelBoundingBox.maxX) * 0.5);
         this.modelCenterY = (float)((modelBoundingBox.minY + modelBoundingBox.maxY) * 0.5);
         this.lengthAmount = (float)((modelBoundingBox.minZ + modelBoundingBox.maxZ) * 0.5);
      }
   }

   private void handleVectorProject(Matrix4f matrix, ModelPart modelPart) {
      this.quaternionf.rotationZYX(modelPart.roll, modelPart.yaw, modelPart.pitch);
      this.handleOriginTransform(this.leftEyePos, -0.125F, -0.26875F, matrix, modelPart);
      this.handleOriginTransform(this.rightEyePos, 0.125F, -0.26875F, matrix, modelPart);
      this.handleOriginTransform(this.noseTipPos, 0.0F, -0.26875F, matrix, modelPart);
      this.handleOriginTransform(this.noseBasePos, 0.0F, -0.33125F, matrix, modelPart);
      this.vectorProjectAmount = MatrixRotateUtilsHandle.getLength(this.noseTipPos, this.noseBasePos);
      this.faceDepthScale = MatrixRotateUtilsHandle.getAmount(this.noseTipPos.z, this.projectionDepthScale);
      this.botsMatrixMatrix4f.transformProject(this.leftEyePos);
      this.botsMatrixMatrix4f.transformProject(this.rightEyePos);
      this.x = this.leftEyePos.x;
      this.y = this.leftEyePos.y;
      this.rightEyeX = this.rightEyePos.x;
      this.rightEyeY = this.rightEyePos.y;
      this.eyesCenterX = (this.x + this.rightEyeX) * 0.5F;
      this.eyesCenterY = (this.y + this.rightEyeY) * 0.5F;
      this.matrixVillagerActive = this.noseTipPos.z < 0.0F && Math.abs(this.leftEyePos.z) < 1.0F && Math.abs(this.rightEyePos.z) < 1.0F;
   }

   public void handleAmount(float amount, float amount2) {
      this.trackedAmountA = amount;
      this.trackedAmountB = amount2;
   }

   private static boolean canDangerTone(boolean dangerToneFlag, ScissorTgtHandler.Snapshot snapshot, String dangerToneText) {
      return dangerToneFlag && snapshot != null && snapshot.tone() == ScissorTgtHandler.Tone.DANGER && dangerToneText.equalsIgnoreCase(snapshot.state());
   }

   private static float getAmount(float matrixRenderValue) {
      float matrixRenderValue2 = matrixRenderValue % 1.2F;
      return matrixRenderValue2 < 0.3F ? matrixRenderValue2 / 0.3F : 0.0F;
   }

   private void handleVectorTranslate(MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue) {
      ItemRenderState rightHandItemState = this.playerEntityRenderState.rightHandItemState;
      matrices.push();
      matrices.translate(0.0F, 1.5F, 0.0F);
      matrices.multiply(this.vectorTranslateQuaternionf.set(this.vectorQuaternionf).conjugate());
      matrices.translate(0.0F, -1.5F, 0.0F);
      Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
      this.handleVectorTransform(positionMatrix);
      this.handleVectorTransform(positionMatrix, this.springTargetPos, 1);
      this.handleVectorTransform(positionMatrix, this.controlPointB, 2);
      this.handleVectorTransform(positionMatrix, this.controlPointC, 3);
      this.handleVectorTransform(positionMatrix, this.controlPointD, 4);
      float vectorTranslateValue = -this.vectorTranslateAmount * (float) (Math.PI / 180.0) + this.vectorQuaternionfAmountConst.swayRoll;
      if (!rightHandItemState.isEmpty()) {
         this.handleMultiplyRotation(
            matrices, orderedRenderCommandQueue, rightHandItemState, this.springTargetPos, VectorRadiansUtils.getAmount(this.vectorQuaternionfAmountConst.itemProgress, this.vectorQuaternionfAmountConst.itemScale), vectorTranslateValue, this.vectorQuaternionfAmountConst.vectorTranslateAmount
         );
      }

      matrices.pop();
   }

   private void updateRenderEntity() {
      if (this.radiansVectorModel != null) {
         this.radiansVectorModel.armPitchScale = RadiansVectorModel.getAmount(this.crystalsAmount);
      }

      this.playerEntityRenderState.age = this.crystalsCount * 20.0F;
      this.playerEntityRenderState.handSwingProgress = this.standbyDangerType == StandbyDanger.Mode.FIGHT ? getAmount(this.crystalsCount) : 0.0F;
      this.playerEntityRenderState.relativeHeadYaw = this.cpxAmount;
      this.playerEntityRenderState.pitch = this.cpxPitch;
      this.endCrystalEntityRenderSt.age = this.crystalsCount * 20.0F * 0.5F;
      this.villagerEntityRenderStat.age = this.crystalsCount * 20.0F;
      this.villagerEntityRenderStat.relativeHeadYaw = this.cpxAmount;
      this.villagerEntityRenderStat.pitch = this.cpxPitch;
      float entityFactor = this.matricesCpxStandbyDanger.getBlendedDanger();
      this.modelTintColor = PositionMatrixCustom.getValueForIntFloat(StandbyDanger.getValue(-12959668, -54742, entityFactor), 0.9F + -0.45F * entityFactor);
      if (this.cpxActive) {
         this.updateVectorAmount();
      }
   }

   public void handleCpxEmpty(CpxVillagerData cpxVillagerData) {
      if (cpxVillagerData != null && cpxVillagerData != this.cpxVillagerData) {
         this.cpxVillagerData = cpxVillagerData;
         this.empty = ItemStack.EMPTY;

         try {
            this.empty = StackRegistryUtils.getIconGlint(cpxVillagerData);
         } catch (Throwable e) {
         }

         try {
            this.handleRenderEntity(cpxVillagerData);
         } catch (Throwable e2) {
            this.crystalsActive = true;
         }
      }
   }

   private void renderMatricesCpx(DrawContext context, float matricesCpxValue, float peekPositionFactor, float matricesCpxBound, float hashHuntBound) {
      try {
         float cubeBound = Math.min(matricesCpxBound, hashHuntBound) * 0.42F;
         float matricesCpxValue4 = matricesCpxValue + matricesCpxBound / 2.0F;
         float matricesCpxValue5 = peekPositionFactor + hashHuntBound * 0.46F;
         MatrixPeekUtilsRender.renderMatrixPeekForDrawContextFloat(
            context,
            matricesCpxValue4 - cubeBound / 2.0F,
            matricesCpxValue5 - cubeBound / 2.0F,
            cubeBound,
            cubeBound,
            cubeBound / 2.0F,
            CardMatrixUtils.getReleaseRight(0.05 + 0.07 * this.crystalsAmount),
            cubeBound * 0.35F,
            1.0F
         );
         if (!this.empty.isEmpty() && !this.canAmount(matricesCpxValue5 - cubeBound / 2.0F, matricesCpxValue5 + cubeBound / 2.0F)) {
            float matricesCpxValue6 = cubeBound / 16.0F;
            context.getMatrices().pushMatrix();

            try {
               context.getMatrices().translate(matricesCpxValue4, matricesCpxValue5);
               context.getMatrices().scale(matricesCpxValue6, matricesCpxValue6);
               DrawMatricesUtils.render(context, this.empty, -8, -8);
            } finally {
               context.getMatrices().popMatrix();
            }
         }

         if (this.cpxVillagerData != null && this.cpxVillagerData.isSkin() && !this.cpxVillagerData.planned()) {
            float amountMax = matricesCpxValue5 + cubeBound * 0.62F;
            if (this.matricesCpxStandbyDanger.getBlendedDanger() > 0.01F) {
               MatrixPeekUtilsRender.renderMatrixPeekForDrawContextFloat(context, matricesCpxValue4 - 2.0F, amountMax, 4.0F, 4.0F, 2.0F, this.matricesCpxStandbyDanger.getAmountBlendedDanger(), 4.0F, 0.8F);
            }

            MatrixPeekUtilsRender.renderMatrixPeek(context, matricesCpxValue4 - 2.0F, amountMax, 4.0F, 4.0F, 2.0F, this.matricesCpxStandbyDanger.getValue());
         }
      } catch (Throwable e) {
      }
   }

   private void handleVectorTransform(Matrix4f matrix, Vector3f vector3f, int vectorTransformValue) {
      matrix.transformPosition(this.projectedPoint.set(vector3f));
      this.vectorTransformTable[vectorTransformValue * 3 + 2] = MatrixRotateUtilsHandle.getAmount(this.projectedPoint.z, this.projectionDepthScale);
      this.botsMatrixMatrix4f.transformProject(this.projectedPoint);
      this.vectorTransformTable[vectorTransformValue * 3] = this.projectedPoint.x;
      this.vectorTransformTable[vectorTransformValue * 3 + 1] = this.projectedPoint.y;
   }

   private void handleCpxAmount(CpxVillagerData cpxVillagerData, float cpxValue, float cpxValue2) {
      if (cpxVillagerData == this.cpxVillagerData) {
         this.cpxCount = 0.0F;
      } else {
         this.cpxCount += cpxValue2;
         if (this.cpxVillagerData == null || cpxValue <= 0.02F || this.cpxCount >= 0.25F) {
            this.cpxCount = 0.0F;
            this.handleCpxEmpty(cpxVillagerData);
         }
      }
   }

   private void handleMultiplyRender(MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, RadiansVectorModel radiansVectorModel, ItemRenderState itemRenderState, Arm arm) {
      if (!itemRenderState.isEmpty()) {
         matrices.push();
         radiansVectorModel.setArmAngle(this.playerEntityRenderState, arm, matrices);
         matrices.multiply(rotationXMultiplyRender);
         matrices.multiply(rotationY);
         matrices.translate((arm == Arm.LEFT ? -1.0F : 1.0F) / 16.0F, 0.125F, -0.625F);
         itemRenderState.render(matrices, orderedRenderCommandQueue, 15728880, OverlayTexture.DEFAULT_UV, 0);
         matrices.pop();
      }
   }

   public StandbyDanger getMatricesCpxStandbyDanger() {
      return this.matricesCpxStandbyDanger;
   }

   public void handleAmountMin(double amount, double heightAmount) {
      if (this.totemsActive) {
         this.crystalsOrbit.handleAmount(amount - this.lastPosX, heightAmount - this.lastPosY);
         this.lastPosX = amount;
         this.lastPosY = heightAmount;
      }
   }

   public void updateTotemsAmount() {
      this.totemsFlag = false;
      this.totemsOffsetX = 0.0F;
      this.totemsOffsetY = 0.0F;
   }

   private static float getAmountForFloat(float matrixRenderValue) {
      float matrixRenderTotal = matrixRenderValue % 360.0F;
      if (matrixRenderTotal > 180.0F) {
         matrixRenderTotal -= 360.0F;
      } else if (matrixRenderTotal < -180.0F) {
         matrixRenderTotal += 360.0F;
      }

      return matrixRenderTotal;
   }

   public float getCrystalsAmount() {
      return this.crystalsAmount;
   }

   private void renderAmount(DrawContext context, float matrixRenderValue, float matrixRenderValue2, float matrixRenderValue3, float matrixRenderValue4, float matrixRenderFactor) {
      if (!(matrixRenderValue3 <= 0.0F)) {
         float matrixRenderFactor2 = 0.05859375F * matrixRenderValue3;
         float matrixRenderFactor3 = 1.0F - (1.0F - matrixRenderValue4) * (1.0F - matrixRenderValue4);
         float matrixRenderFactor4 = 2.5F * matrixRenderFactor2 * matrixRenderFactor3;
         float cubeBound = matrixRenderFactor2 * (1.0F - 0.5F * matrixRenderValue4);
         int valueForIntFloat = PositionMatrixCustom.getValueForIntFloat(-13183762, matrixRenderFactor * this.scaledVectorAmount);

         for (int i = 0; i < 8; i++) {
            float angle = i * (float) (Math.PI * 2) / 8.0F + 0.39F;
            render(context, matrixRenderValue + (float)Math.cos(angle) * matrixRenderFactor4, matrixRenderValue2 + (float)Math.sin(angle) * matrixRenderFactor4, cubeBound, valueForIntFloat);
         }
      }
   }

   public void update() {
      this.matricesCpxStandbyDanger.updateAmountFlag();
   }

   public void updateFlagMin() {
      if (this.totemsActive) {
         this.crystalsOrbit.updateMaxFlag();
      }

      this.totemsActive = false;
   }

   private void renderAmountAmounts(DrawContext context, float matrixRenderValue, float peekPositionFactor, float matrixRotateFactor, float matrixRenderValue4) {
      float matrixRotateLimit = this.vectorQuaternionfAmountConst.strainAmount * this.scaledVectorAmount * this.vectorTransformAmount * 0.3F;
      float matrixRenderValue6 = this.vectorTransformTable[2];
      if (!(matrixRotateLimit <= 0.01F) && !(matrixRenderValue6 <= 0.0F)) {
         float cubeBound = 0.08203125F * matrixRenderValue6;
         render(context, this.getAmounts(0, matrixRenderValue, matrixRotateFactor), this.getAmountsForIntFloat(0, peekPositionFactor, matrixRenderValue4), cubeBound, cubeBound, PositionMatrixCustom.getValueForIntFloat(-13183762, matrixRotateLimit));
      }
   }

   private void handleLeftMultiply(MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, RadiansVectorModel radiansVectorModel, ItemRenderState itemRenderState, Arm arm, float leftMultiplyValue) {
      boolean leftMultiplyFlag = arm == Arm.RIGHT && this.entityActive && this.endCrystalEntityModel != null;
      if ((leftMultiplyFlag || !itemRenderState.isEmpty()) && !(leftMultiplyValue <= 0.02F)) {
         ModelPart modelPart = arm == Arm.LEFT ? radiansVectorModel.leftArm : radiansVectorModel.rightArm;
         float x = 0.95F * leftMultiplyValue;
         matrices.push();
         radiansVectorModel.setArmAngle(this.playerEntityRenderState, arm, matrices);
         matrices.translate(arm == Arm.LEFT ? 0.0625F : -0.0625F, 0.5625F, 0.0F);
         matrices.multiply(this.leftMultiplyQuaternionf.rotationZYX(modelPart.roll, modelPart.yaw, modelPart.pitch).conjugate());
         matrices.translate(arm == Arm.LEFT ? 0.125F : -0.125F, arm == Arm.LEFT ? -0.03125F : -0.09375F, -0.15625F);
         matrices.multiply(this.multiplyRotationQuaternionf.rotationY(this.leftMultiplyAmount));
         matrices.multiply(rotationX);
         matrices.scale(x, x, x);
         if (leftMultiplyFlag) {
            this.handleEndTranslate(matrices, orderedRenderCommandQueue);
         } else {
            itemRenderState.render(matrices, orderedRenderCommandQueue, 15728880, OverlayTexture.DEFAULT_UV, 0);
         }

         matrices.pop();
      }
   }

   public boolean isTotemsFlag() {
      return this.totemsFlag;
   }

   private void renderAmountAmountsForDrawContextFloat(DrawContext context, float matrixRenderValue, float peekPositionFactor, float matrixRotateFactor, float matrixRenderValue4) {
      float matrixRenderFactor = this.vectorTransformTable[5];
      if (!(this.scaledVectorAmount <= 0.01F) && !(matrixRenderFactor <= 0.0F)) {
         float matrixRenderValue5 = 0.05859375F * matrixRenderFactor;
         float matrixRenderValue6 = 0.16F * VectorRadiansUtils.getAmount(this.vectorQuaternionfAmountConst.itemProgress, this.vectorQuaternionfAmountConst.itemScale) * 0.9375F * matrixRenderFactor;
         float amounts7 = this.getAmounts(1, matrixRenderValue, matrixRotateFactor);
         float amountsForIntFloat = this.getAmountsForIntFloat(1, peekPositionFactor, matrixRenderValue4);
         if (this.vectorQuaternionfAmountConst.sparkleWindow > 0.004F) {
            float amounts9 = this.getAmounts(0, matrixRenderValue, matrixRotateFactor);
            float amountsForIntFloat10 = this.getAmountsForIntFloat(0, peekPositionFactor, matrixRenderValue4);
            float max2 = Math.max(1.5F * matrixRenderValue5, matrixRenderValue6 * 1.25F);

            for (int i = 0; i < 6; i++) {
               float matrixRotateLimit = this.vectorQuaternionfAmountConst.sparkleAlphas[i] * this.scaledVectorAmount;
               if (!(matrixRotateLimit <= 0.01F)) {
                  float angle = i * (float) (Math.PI * 2) / 6.0F + this.vectorQuaternionfAmountConst.getElapsedTime() * 1.4F;
                  float matrixRenderFactor3 = this.vectorQuaternionfAmountConst.strainTable[i];
                  float matrixRenderFactor4 = matrixRenderFactor3 * matrixRenderFactor3;
                  float matrixRenderValue12 = amounts7 + (float)Math.cos(angle) * max2;
                  float matrixRenderValue13 = amountsForIntFloat + (float)Math.sin(angle) * max2;
                  render(context, matrixRenderValue12 + (amounts9 - matrixRenderValue12) * matrixRenderFactor4, matrixRenderValue13 + (amountsForIntFloat10 - matrixRenderValue13) * matrixRenderFactor4, 2.0F, PositionMatrixCustom.getValueForIntFloat(-13183762, matrixRotateLimit));
               }
            }
         }

         if (this.vectorQuaternionfAmountConst.afterglowAmount > 0.004F) {
            this.renderAmountForDrawContextFloat(context, amounts7, amountsForIntFloat, this.getAmounts(2, matrixRenderValue, matrixRotateFactor), this.getAmountsForIntFloat(2, peekPositionFactor, matrixRenderValue4), matrixRenderValue6 * 2.0F);
         }

         if (this.vectorQuaternionfAmountConst.burstAlpha * this.scaledVectorAmount > 0.01F) {
            float corzLicenseLimit = matrixRenderValue6 + 2.5F * matrixRenderValue5 * this.vectorQuaternionfAmountConst.burstExpand;
            MatrixPeekUtilsRender.renderMatrixPeek(
               context,
               amounts7 - corzLicenseLimit,
               amountsForIntFloat - corzLicenseLimit,
               corzLicenseLimit * 2.0F,
               corzLicenseLimit * 2.0F,
               corzLicenseLimit,
               1.5F,
               PositionMatrixCustom.getValueForIntFloat(-13183762, this.vectorQuaternionfAmountConst.burstAlpha * this.scaledVectorAmount)
            );
         }

         if (this.vectorQuaternionfAmountConst.flashAlpha * this.scaledVectorAmount > 0.01F) {
            this.renderAmount(context, this.getAmounts(3, matrixRenderValue, matrixRotateFactor), this.getAmountsForIntFloat(3, peekPositionFactor, matrixRenderValue4), this.vectorTransformTable[11], this.vectorQuaternionfAmountConst.flashPulse, this.vectorQuaternionfAmountConst.flashAlpha);
         }

         if (this.vectorQuaternionfAmountConst.displayBlend * this.scaledVectorAmount > 0.01F) {
            this.renderAmount(context, this.getAmounts(4, matrixRenderValue, matrixRotateFactor), this.getAmountsForIntFloat(4, peekPositionFactor, matrixRenderValue4), this.vectorTransformTable[14], this.vectorQuaternionfAmountConst.flashPulseLate, this.vectorQuaternionfAmountConst.displayBlend);
         }
      }
   }

   private void handleMatrixVillager(MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue) {
      Entry entry = matrices.peek();
      MatrixRotateUtilsHandle.handleRotateTranslate(entry.getPositionMatrix(), entry.getNormalMatrix(), this.botsMatrixAmount, this.matrixVillagerAmount);
      orderedRenderCommandQueue.getBatchingQueue(1).submitCustom(matrices, RenderLayers.debugQuads(), this.positionMatrixCustom);
      MatrixRotateUtilsHandle.handleRotate(entry.getPositionMatrix(), entry.getNormalMatrix(), this.vectorTranslateAmount);
      if (!this.crystalsFlag && this.cpxVillagerData != null) {
         if (this.cpxVillagerData.villager()) {
            this.matrixVillagerActive = false;
            if (this.villagerResemblingModel != null) {
               this.handleModelCull(matrices, orderedRenderCommandQueue);
            }
         } else if (this.cpxVillagerData.isSkin()) {
            if (this.radiansVectorModel != null) {
               this.handleRenderState(matrices, orderedRenderCommandQueue, this.radiansVectorModel);
            }
         } else {
            this.handleAmountRender(matrices, orderedRenderCommandQueue);
         }
      } else {
         this.matrixVillagerActive = false;
      }
   }

   private float getCrystalsCount() {
      return this.crystalsCount;
   }

   private static boolean canWeaponTool(ItemStack stack) {
      return !stack.isEmpty() && !(stack.getItem() instanceof BlockItem) && !stack.contains(DataComponentTypes.TOOL) && !stack.contains(DataComponentTypes.WEAPON);
   }

   private void handleRenderState(MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, RadiansVectorModel radiansVectorModel) {
      matrices.push();
      Entry entry = matrices.peek();
      MatrixRotateUtilsHandle.handleScaleTranslate(entry.getPositionMatrix(), entry.getNormalMatrix());
      if (this.cpxActive) {
         VectorRadiansUtils.handleTranslateMultiply(matrices, this.vectorQuaternionf);
      }

      radiansVectorModel.setAngles(this.playerEntityRenderState);
      if (this.cpxActive) {
         this.handleVectorQuaternionf(radiansVectorModel);
      }

      orderedRenderCommandQueue.submitModel(radiansVectorModel, this.playerEntityRenderState, matrices, this.noCull, 15728880, OverlayTexture.DEFAULT_UV, this.hatCullValue, null, 0, null);
      if (this.eyes != null) {
         orderedRenderCommandQueue.getBatchingQueue(1)
            .submitModel(
               radiansVectorModel, this.playerEntityRenderState, matrices, this.eyes, 15728880, OverlayTexture.DEFAULT_UV, this.hatCullValue == -1 ? this.amountStandby : -16250613, null, 0, null
            );
      }

      if (this.entityRenderLayer != null) {
         orderedRenderCommandQueue.getBatchingQueue(1).submitModel(radiansVectorModel, this.playerEntityRenderState, matrices, this.entityRenderLayer, 15728880, OverlayTexture.DEFAULT_UV, this.modelTintColor, null, 0, null);
      }

      if (this.matrixRenderActive) {
         this.handleMultiplyRender(matrices, orderedRenderCommandQueue, radiansVectorModel, this.playerEntityRenderState.rightHandItemState, Arm.RIGHT);
         this.handleMultiplyRender(matrices, orderedRenderCommandQueue, radiansVectorModel, this.playerEntityRenderState.leftHandItemState, Arm.LEFT);
      } else {
         if (this.cpxActive) {
            this.handleVectorTranslate(matrices, orderedRenderCommandQueue);
         } else {
            this.handleLeftMultiply(matrices, orderedRenderCommandQueue, radiansVectorModel, this.playerEntityRenderState.rightHandItemState, Arm.RIGHT, this.crystalsTotemsAmount);
         }

         this.handleLeftMultiply(matrices, orderedRenderCommandQueue, radiansVectorModel, this.playerEntityRenderState.leftHandItemState, Arm.LEFT, this.totemsCrystalsAmount);
      }

      this.handleVectorProject(entry.getPositionMatrix(), radiansVectorModel.head);
      matrices.pop();
   }

   public MatrixRender() {
      this.botsMatrixScene = this::handleMatrixVillager;
      this.crystalsOrbit = new RotateQuaternionfUtils.Orbit(-16.0F, 10.0F, 1.0F, 1.0F, 1.0F, 14.0F);
      this.matricesCpxStandbyDanger = new StandbyDanger();
      this.playerEntityRenderState = new PlayerEntityRenderState();
      this.villagerEntityRenderStat = new VillagerEntityRenderState();
      this.endCrystalEntityRenderSt = new EndCrystalEntityRenderState();
      this.itemRenderState = new ItemRenderState();
      this.positionMatrixCustom = new PositionMatrixCustom();
      this.botsMatrixMatrix4f = new Matrix4f();
      this.quaternionf = new Quaternionf();
      this.leftMultiplyQuaternionf = new Quaternionf();
      this.multiplyRotationQuaternionf = new Quaternionf();
      this.leftEyePos = new Vector3f();
      this.rightEyePos = new Vector3f();
      this.noseTipPos = new Vector3f();
      this.noseBasePos = new Vector3f();
      this.vectorQuaternionfAmountConst = new AmountConstStrain();
      this.matrixStack = new MatrixStack();
      this.vectorQuaternionf = new Quaternionf();
      this.vectorTranslateQuaternionf = new Quaternionf();
      this.orbitRotationVec = new Vector3f();
      this.vectorQuaternionfVector3f = new Vector3f();
      this.controlPointD = new Vector3f();
      this.springTargetPos = new Vector3f();
      this.controlPointB = new Vector3f();
      this.controlPointC = new Vector3f();
      this.transformedAnchorPos = new Vector3f();
      this.neckPos = new Vector3f();
      this.springPos = new Vector3f();
      this.springVelocity = new Vector3f();
      this.projectedPoint = new Vector3f();
      this.vectorTransformTable = new float[15];
      this.empty = ItemStack.EMPTY;
      this.standbyDangerType = StandbyDanger.Mode.OFF;
      this.hatCullValue = -1;
      this.amountStandby = -12959668;
      this.crystalsAlphaScale = 1.0F;
      this.crystalsTotemsAmount = 1.0F;
      this.totemsCrystalsAmount = 1.0F;
      this.vectorTranslateAmount = -16.0F;
      this.matrixVillagerAmount = 10.0F;
      this.matrixRenderAmount = 1.0F;
      this.trackedAmountA = Float.NaN;
   }

   private void updateAmountCrystals() {
      int releaseRightValue = MatrixEspUtils.releaseRightValue;
      int crystalsValue = PositionMatrixCustom.getValue(releaseRightValue, 0.4F, 0.75F, this.crystalsAmount, this.totemsAmount);
      int crystalsValue2 = PositionMatrixCustom.getValue(releaseRightValue, 0.25F, 0.55F, this.crystalsAmount, this.totemsAmount);
      int valueForIntFloat3 = 0;
      float crystalsValue4 = 0.0F;
      if (this.crystalsFade > 0.004F) {
         float crystalsFactor = this.crystalsCount % 4.0F / 2.6F;
         if (crystalsFactor < 1.0F) {
            crystalsValue4 = crystalsFactor * 1.8F;
            valueForIntFloat3 = PositionMatrixCustom.getValueForIntFloat(releaseRightValue, 0.35F * this.crystalsFade * (float)Math.sin(crystalsFactor * Math.PI));
         }
      }

      this.positionMatrixCustom.handleAmount(PositionMatrixCustom.getValue(releaseRightValue, this.crystalsAmount), crystalsValue, crystalsValue2, 1711276032, this.crystalsAlphaScale, valueForIntFloat3, crystalsValue4);
   }

   private static float getAmount(float matrixRenderLimit, float matrixRenderLimit2, float matrixRenderValue) {
      return matrixRenderLimit < matrixRenderLimit2 ? matrixRenderLimit2 : (matrixRenderLimit > matrixRenderValue ? matrixRenderValue : matrixRenderLimit);
   }

   static int getValue(float matrixRenderValue) {
      float amountFloat = MatrixRotateUtilsHandle.getAmountFloat(matrixRenderValue);
      return amountFloat <= 0.8F
         ? StandbyDanger.getValue(-16038830, -13183762, amountFloat / 0.8F)
         : StandbyDanger.getValue(-13183762, -2425089, (amountFloat - 0.8F) / 0.19999999F);
   }

   private static void handleUpdateModel(MinecraftClient client, ItemRenderState itemRenderState, ItemStack itemStack, ItemDisplayContext itemDisplayContext) {
      client.getItemModelManager().clearAndUpdate(itemRenderState, itemStack, itemDisplayContext, client.world, null, 0);
   }

   private static void render(DrawContext context, float matrixRenderValue, float matrixRenderValue2, float cubeBound, int valueForIntFloat) {
      MatrixPeekUtilsRender.renderMatrixPeekForDrawContextFloat(context, matrixRenderValue - cubeBound / 2.0F, matrixRenderValue2 - cubeBound / 2.0F, cubeBound, cubeBound, 0.0F, valueForIntFloat, cubeBound, 0.9F);
   }

   public void updateMatricesCpxStandbyDanger() {
      this.matricesCpxStandbyDanger.updateFlag();
   }

   public void updateTotemsFlag() {
      if (this.totemsActive) {
         this.crystalsOrbit.updateMaxFlag();
      }

      this.totemsActive = false;

      try {
         this.bots.updateTextureClose();
      } catch (Throwable e) {
      }

      this.cpxVillagerData = null;
      this.cpxActive = false;
      this.empty = ItemStack.EMPTY;
      this.lastNanoTime = 0L;
      this.matricesCpxStandbyDanger.updateAmountStandby();
      this.matrixVillagerActive = false;
      this.totemsFlag = false;
      this.totemsAmount = 0.0F;
      this.cpxAmount = 0.0F;
      this.cpxPitch = 0.0F;
   }

   private void renderAmountForDrawContextFloat(DrawContext context, float matrixRenderValue, float amountsForIntFloat, float matrixRenderValue3, float matrixRenderValue4, float matrixRenderFactor) {
      float matrixRenderFactor2 = matrixRenderValue3 - matrixRenderValue;
      float matrixRenderFactor3 = matrixRenderValue4 - amountsForIntFloat;
      float distance = (float)Math.sqrt(matrixRenderFactor2 * matrixRenderFactor2 + matrixRenderFactor3 * matrixRenderFactor3);
      float min5 = Math.min(distance, 2.2F * matrixRenderFactor);
      float acrossScaled = matrixRenderFactor * 0.5F;
      if (!(min5 <= acrossScaled) && !(distance <= 0.001F)) {
         float matrixRenderFactor4 = matrixRenderFactor2 / distance;
         float matrixRenderFactor5 = matrixRenderFactor3 / distance;

         for (int i = 0; i < 6; i++) {
            float matrixRenderFactor6 = i / 5.0F;
            float matrixRenderFactor7 = acrossScaled + (min5 - acrossScaled) * matrixRenderFactor6;
            float cubeBound = matrixRenderFactor * (0.55F + -0.4F * matrixRenderFactor6);
            float matrixRenderLimit = 0.5F * (1.0F - matrixRenderFactor6) * this.vectorQuaternionfAmountConst.afterglowAmount * this.scaledVectorAmount;
            if (matrixRenderLimit > 0.01F) {
               render(context, matrixRenderValue + matrixRenderFactor4 * matrixRenderFactor7, amountsForIntFloat + matrixRenderFactor5 * matrixRenderFactor7, cubeBound, cubeBound, PositionMatrixCustom.getValueForIntFloat(-13183762, matrixRenderLimit));
            }
         }
      }
   }

   private void handleVectorTransform(Matrix4f matrix) {
      matrix.transformPosition(this.transformedAnchorPos.set(this.vectorQuaternionfVector3f));
      matrix.transformPosition(this.neckPos.set(0.0F, 0.375F, 0.0F));
      this.vectorTransformAmount = MatrixRotateUtilsHandle.getAmountFloat((this.transformedAnchorPos.z - this.neckPos.z) / 0.4F + 0.5F);
      this.handleVectorTransform(matrix, this.vectorQuaternionfVector3f, 0);
   }

   public boolean isCrystalsActive() {
      return this.crystalsActive;
   }

   private void handleModelCull(MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue) {
      boolean modelCullFlag = this.hat != null && HalloweenMatrixUtils.isHalloween();
      if (!modelCullFlag && this.modelPart == null && !this.modelCullActive && SnowVertexUtils.isChristmas()) {
         try {
            this.modelPart = HatModelUtils.getChild();
            this.entityCutoutNoCullModelCull = RenderLayers.entityCutoutNoCull(HatModelUtils.getTextureIdentifier(MinecraftClient.getInstance()));
         } catch (Throwable e) {
            this.modelPart = null;
            this.entityCutoutNoCullModelCull = null;
            this.modelCullActive = true;
         }
      }

      boolean modelCullFlag2 = !modelCullFlag && this.modelPart != null && this.entityCutoutNoCullModelCull != null && SnowVertexUtils.isChristmas();
      boolean modelCullFlag3 = modelCullFlag || modelCullFlag2;
      float x = modelCullFlag ? 0.76F : 0.88F;
      matrices.push();
      if (modelCullFlag3) {
         matrices.scale(x, x, x);
      }

      Entry entry = matrices.peek();
      MatrixRotateUtilsHandle.handleScaleTranslate(entry.getPositionMatrix(), entry.getNormalMatrix());
      this.villagerResemblingModel.setAngles(this.villagerEntityRenderStat);
      if (this.child != null) {
         this.child.visible = !modelCullFlag3;
      }

      orderedRenderCommandQueue.submitModel(this.villagerResemblingModel, this.villagerEntityRenderStat, matrices, this.noCull, 15728880, OverlayTexture.DEFAULT_UV, -1, null, 0, null);
      orderedRenderCommandQueue.submitModel(this.villagerResemblingModel, this.villagerEntityRenderStat, matrices, this.entityCutoutNoCullHatCull, 15728880, OverlayTexture.DEFAULT_UV, -1, null, 0, null);
      orderedRenderCommandQueue.submitModel(this.villagerResemblingModel, this.villagerEntityRenderStat, matrices, this.hatCullRenderLayer, 15728880, OverlayTexture.DEFAULT_UV, -1, null, 0, null);
      if (modelCullFlag3) {
         matrices.push();
         this.villagerResemblingModel.getHead().applyTransform(matrices);
         orderedRenderCommandQueue.submitModelPart(modelCullFlag ? this.hat : this.modelPart, matrices, modelCullFlag ? this.entityCutoutNoCull : this.entityCutoutNoCullModelCull, 15728880, OverlayTexture.DEFAULT_UV, null);
         matrices.pop();
      }

      matrices.pop();
   }

   private float getAmounts(int matrixRenderValue, float matrixRenderValue2, float matrixRotateFactor) {
      return MatrixRotateUtilsHandle.getAmountForFloat(this.vectorTransformTable[matrixRenderValue * 3], matrixRenderValue2, matrixRotateFactor);
   }

   private void handleEndTranslate(MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue) {
      float yOffsetScaled = EndCrystalEntityRenderer.getYOffset(this.endCrystalEntityRenderSt.age) * 0.5F;
      matrices.translate(0.0F, 0.28125F, 0.0F);
      matrices.scale(0.52F, 0.52F, 0.52F);
      matrices.translate(0.0F, -1.5F - yOffsetScaled + (yOffsetScaled - -0.525F) * 0.35F, 0.0F);
      orderedRenderCommandQueue.submitModel(
         this.endCrystalEntityModel, this.endCrystalEntityRenderSt, matrices, this.renderLayer, 15728880, OverlayTexture.DEFAULT_UV, this.hatCullValue, null, 0, null
      );
   }

   private void handleVectorQuaternionf(RadiansVectorModel radiansVectorModel) {
      VectorRadiansUtils.getPositionIdentity(radiansVectorModel, this.playerEntityRenderState, this.vectorQuaternionf, this.matrixStack, this.vectorQuaternionfVector3f);
      VectorRadiansUtils.getAmount(this.controlPointD, this.orbitRotationVec, this.vectorQuaternionfVector3f, this.vectorQuaternionfAmountConst);
      VectorRadiansUtils.getLerp(this.springTargetPos, this.controlPointD, this.vectorQuaternionfVector3f, this.vectorQuaternionfAmountConst.itemProgress, this.vectorQuaternionfAmountConst.liftAmount);
      VectorRadiansUtils.getLerp(this.controlPointB, this.controlPointD, this.vectorQuaternionfVector3f, this.vectorQuaternionfAmountConst.vectorQuaternionfAmount, this.vectorQuaternionfAmountConst.liftAmount);
      VectorRadiansUtils.getLerp(this.controlPointC, this.controlPointD, this.vectorQuaternionfVector3f, 1.0F, this.vectorQuaternionfAmountConst.liftAmount);
      if (this.vectorQuaternionfActive) {
         this.springPos.set(this.springTargetPos);
         this.springVelocity.zero();
         this.vectorQuaternionfActive = false;
      } else {
         VectorRadiansUtils.handle(this.springPos, this.springVelocity, this.springTargetPos, 14.0F, this.springFrameDelta);
      }

      VectorRadiansUtils.getInverseTransform(radiansVectorModel.handTargetPos, this.springPos, this.vectorQuaternionf);
      radiansVectorModel.setAngles(this.playerEntityRenderState);
   }

   private void handleAmountRender(MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue) {
      this.matrixVillagerActive = false;
      if (!this.itemRenderState.isEmpty() && !(this.modelScale <= 0.0F)) {
         matrices.push();
         matrices.translate(0.0F, 0.85F + 0.035F * (float)Math.sin(this.crystalsCount * 0.27F * (float) (Math.PI * 2)), 0.0F);
         matrices.scale(this.modelScale, this.modelScale, this.modelScale);
         matrices.multiply(rotationY);
         matrices.translate(-this.modelCenterX, -this.modelCenterY, -this.lengthAmount);
         this.itemRenderState.render(matrices, orderedRenderCommandQueue, 15728880, OverlayTexture.DEFAULT_UV, 0);
         matrices.pop();
      }
   }

   private void updateAmountState() {
      this.multiplyRotationAmount = this.multiplyCenterY = this.multiplyCenterZ = 0.0F;
      if (!this.playerEntityRenderState.rightHandItemState.isEmpty()) {
         Box modelBoundingBox = this.playerEntityRenderState.rightHandItemState.getModelBoundingBox();
         this.multiplyRotationAmount = (float)((modelBoundingBox.minX + modelBoundingBox.maxX) * 0.5);
         this.multiplyCenterY = (float)((modelBoundingBox.minY + modelBoundingBox.maxY) * 0.5);
         this.multiplyCenterZ = (float)((modelBoundingBox.minZ + modelBoundingBox.maxZ) * 0.5);
      }
   }

   private void handleRenderEntity(CpxVillagerData cpxVillagerData) {
      MinecraftClient instance = MinecraftClient.getInstance();
      this.matrixVillagerActive = false;
      this.cpxActive = false;
      if (!cpxVillagerData.isSkin()) {
         this.noCull = this.eyes = this.entityRenderLayer = this.renderLayer = null;
         this.entityActive = false;
         this.playerEntityRenderState.rightHandItemState.clear();
         this.playerEntityRenderState.leftHandItemState.clear();
         handleUpdateModel(instance, this.itemRenderState, StackRegistryUtils.getPadGlint(cpxVillagerData), ItemDisplayContext.FIXED);
         this.updateLengthAmount();
      } else if (cpxVillagerData.villager()) {
         this.handleHatCull(instance, cpxVillagerData);
      } else {
         if (this.radiansVectorModel == null || this.slimEntity != cpxVillagerData.slim()) {
            this.radiansVectorModel = new RadiansVectorModel(
               instance.getLoadedEntityModels().getModelPart(cpxVillagerData.slim() ? ENTITY_MODEL_LAYER : EntityModelLayers.PLAYER), cpxVillagerData.slim()
            );
            this.slimEntity = cpxVillagerData.slim();
         }

         this.itemRenderState.clear();
         this.modelScale = 0.0F;
         this.noCull = RenderLayers.entityCutoutNoCull(cpxVillagerData.skin());
         Identifier identifier = cpxVillagerData.eyes() != null ? cpxVillagerData.eyes() : (IDENTIFIER.equals(cpxVillagerData.skin()) ? CPX01_EYES_TEXTURE : null);
         this.eyes = identifier == null ? null : RenderLayers.eyes(identifier);
         this.entityRenderLayer = !cpxVillagerData.planned() && cpxVillagerData.core() != null ? RenderLayers.eyes(cpxVillagerData.core()) : null;
         this.hatCullValue = cpxVillagerData.planned() ? -15065822 : -1;
         ItemStack rightHandItem = StackRegistryUtils.getGlintPotion(cpxVillagerData);
         ItemStack leftHandItem = StackRegistryUtils.getPotionEmpty(cpxVillagerData);
         this.matrixRenderActive = !canWeaponTool(rightHandItem);
         this.radiansVectorModel.entityActive = this.matrixRenderActive;
         if (this.matrixRenderActive) {
            handleUpdateModel(instance, this.playerEntityRenderState.rightHandItemState, rightHandItem, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND);
            handleUpdateModel(instance, this.playerEntityRenderState.leftHandItemState, leftHandItem, ItemDisplayContext.THIRD_PERSON_LEFT_HAND);
         } else {
            handleUpdateModel(instance, this.playerEntityRenderState.rightHandItemState, rightHandItem, ItemDisplayContext.GROUND);
            handleUpdateModel(instance, this.playerEntityRenderState.leftHandItemState, leftHandItem, ItemDisplayContext.GROUND);
         }

         this.cpxActive = cpxVillagerData.isPullAct() && !cpxVillagerData.planned() && !this.matrixRenderActive;
         this.vectorQuaternionfActive = true;
         this.radiansVectorModel.entityAmountConstStrain = this.cpxActive ? this.vectorQuaternionfAmountConst : null;
         if (this.cpxActive) {
            this.updateAmountState();
         }

         this.entityActive = rightHandItem.getItem() instanceof EndCrystalItem;
         if (this.entityActive && this.endCrystalEntityModel == null) {
            this.endCrystalEntityModel = new EndCrystalEntityModel(instance.getLoadedEntityModels().getModelPart(EntityModelLayers.END_CRYSTAL));
         }

         this.renderLayer = this.entityActive ? RenderLayers.entityCutoutNoCull(vanillaEntity) : null;
         this.endCrystalEntityRenderSt.baseVisible = false;
         this.playerEntityRenderState.rightHandItem = rightHandItem;
         this.playerEntityRenderState.leftHandItem = leftHandItem;
         this.playerEntityRenderState.mainArm = Arm.RIGHT;
         this.playerEntityRenderState.rightArmPose = this.playerEntityRenderState.rightHandItemState.isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;
         this.playerEntityRenderState.leftArmPose = this.playerEntityRenderState.leftHandItemState.isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;
         this.playerEntityRenderState.hatVisible = true;
         this.playerEntityRenderState.jacketVisible = true;
         this.playerEntityRenderState.leftSleeveVisible = true;
         this.playerEntityRenderState.rightSleeveVisible = true;
         this.playerEntityRenderState.leftPantsLegVisible = true;
         this.playerEntityRenderState.rightPantsLegVisible = true;
         this.playerEntityRenderState.capeVisible = false;
         this.playerEntityRenderState.spectator = false;
         this.playerEntityRenderState.invisible = false;
      }
   }

   private static void render(DrawContext context, float matrixRenderValue, float matrixRenderValue2, float cubeBound, float fontOffset, int valueForIntFloat) {
      MatrixPeekUtilsRender.renderMatrixPeekForDrawContextFloat(context, matrixRenderValue - cubeBound / 2.0F, matrixRenderValue2 - fontOffset / 2.0F, cubeBound, fontOffset, fontOffset / 2.0F, valueForIntFloat, cubeBound * 1.1F, 0.9F);
   }

   public void handleMin(float matrixRenderBound) {
      this.crystalsAlphaScale = matrixRenderBound < 0.0F ? 0.0F : Math.min(1.0F, matrixRenderBound);
   }

   private void handleAmountCpx(float cpxValue) {
      float thumbAmount = 0.0F;
      float cpxValue3 = 0.0F;
      if (this.totemsFlag && !this.totemsActive && !this.crystalsFlag && !this.cpxActive && this.cpxVillagerData != null && this.cpxVillagerData.isSkin()) {
         float cpxValue4 = getAmount((this.totemsOffsetX - this.eyesCenterX) * 40.0F, -70.0F, 70.0F);
         float amountForFloat = getAmountForFloat(-(cpxValue4 + this.vectorTranslateAmount));
         float cpxFactor = MatrixRotateUtilsHandle.getAmountFloat((70.0F - Math.abs(amountForFloat)) / 30.0F);
         thumbAmount = getAmount(amountForFloat, -20.0F, 20.0F) * cpxFactor;
         cpxValue3 = getAmount((this.totemsOffsetY + this.eyesCenterY) * 30.0F, -12.0F, 12.0F) * cpxFactor;
      }

      this.cpxAmount = (float)MatrixIdentifierUtils.getAmount(this.cpxAmount, thumbAmount, 6.0, cpxValue);
      this.cpxPitch = (float)MatrixIdentifierUtils.getAmount(this.cpxPitch, cpxValue3, 6.0, cpxValue);
   }

   public boolean canMinMax(double amount, double matrixScissorValue, float matrixRenderLimit, float matrixRenderLimit2, float matrixRenderValue, float matrixRenderValue2, int button) {
      if (amount < matrixRenderLimit || amount > matrixRenderLimit + matrixRenderValue || matrixScissorValue < matrixRenderLimit2 || matrixScissorValue > matrixRenderLimit2 + matrixRenderValue2) {
         return false;
      } else if (button == 1) {
         this.crystalsOrbit.updateAmountMax();
         return true;
      } else if (button != 0) {
         return false;
      } else {
         this.totemsActive = true;
         this.lastPosX = amount;
         this.lastPosY = matrixScissorValue;
         this.crystalsOrbit.updateMaxAmount();
         return true;
      }
   }

   public void renderCrystalsAmount(
      DrawContext context, float crystalsValue, float peekPositionFactor, float matricesCpxBound, float hashHuntBound, float botsMatrixFactor, CpxVillagerData cpxVillagerData, boolean crystalsFlag, ScissorTgtHandler.Snapshot snapshot
   ) {
      long nanoTime = System.nanoTime();
      float min = this.lastNanoTime == 0L ? 0.0F : Math.min(0.1F, (float)(nanoTime - this.lastNanoTime) / 1.0E9F);
      this.lastNanoTime = nanoTime;
      this.crystalsCount += min;
      this.springFrameDelta = min;
      this.crystalsOrbit.handleAmountMax(min);
      this.matrixRenderAmount = (float)MatrixIdentifierUtils.getAmount(
         this.matrixRenderAmount, !this.crystalsOrbit.isFlag() && !(Math.abs(this.crystalsOrbit.getMax()) > 1.0F) ? 1.0 : 0.0, this.crystalsOrbit.isFlag() ? 10.0 : 0.6F, min
      );
      this.vectorTranslateAmount = this.crystalsOrbit.getSmoothedYaw() + 10.0F * (float)Math.sin(this.crystalsCount * 0.42F) * this.matrixRenderAmount;
      this.leftMultiplyAmount = 0.6F * (this.crystalsOrbit.getSmoothedYaw() - this.vectorTranslateAmount) * (float) (Math.PI / 180.0);
      this.crystalsOrbitRadians = (this.crystalsOrbit.getSmoothedYaw() - this.vectorTranslateAmount) * (float) (Math.PI / 180.0);
      this.matrixVillagerAmount = this.crystalsOrbit.getSmoothedPitch();
      boolean dangerToneFlag = crystalsFlag && cpxVillagerData != null && !cpxVillagerData.planned();
      this.crystalsFlag = cpxVillagerData == null || this.crystalsForced;
      this.crystalsAmount = (float)MatrixIdentifierUtils.getAmount(this.crystalsAmount, dangerToneFlag ? 1.0 : 0.0, 8.0, min);
      this.crystalsTotemsAmount = (float)MatrixIdentifierUtils.getAmount(
         this.crystalsTotemsAmount, canDangerTone(dangerToneFlag, snapshot, "No crystals") ? 0.0 : 1.0, 9.0, min
      );
      this.totemsCrystalsAmount = (float)MatrixIdentifierUtils.getAmount(
         this.totemsCrystalsAmount, canDangerTone(dangerToneFlag, snapshot, "No totems") ? 0.0 : 1.0, 9.0, min
      );
      this.totemsAmount = (float)MatrixIdentifierUtils.getAmount(this.totemsAmount, this.totemsFlag ? 1.0 : 0.0, 12.0, min);
      this.standbyDangerType = StandbyDanger.getStandbyFight(dangerToneFlag, snapshot);
      this.amountStandby = this.matricesCpxStandbyDanger.getAmountStandby(this.standbyDangerType, this.crystalsCount, min);
      if (this.cpxActive) {
         this.vectorQuaternionfAmountConst.getAmountStrain(this.getCrystalsCount());
      }

      this.vectorAmount = (float)MatrixIdentifierUtils.getAmount(this.vectorAmount, this.matricesCpxStandbyDanger.isFlag() ? 0.0 : 1.0, 12.0, min);
      boolean crystalsFlag3 = dangerToneFlag && cpxVillagerData.isSkin() && (this.standbyDangerType == StandbyDanger.Mode.HUNT || this.standbyDangerType.isFightDanger());
      this.crystalsFade = (float)MatrixIdentifierUtils.getAmount(this.crystalsFade, crystalsFlag3 ? 1.0 : 0.0, 6.0, min);
      this.handleAmountCpx(min);
      if (!(matricesCpxBound < 8.0F) && !(hashHuntBound < 8.0F)) {
         float panelOverlay = CorzRoundedUtils.getPanelOverlay();
         if (!this.crystalsFlag) {
            this.handleCpxAmount(cpxVillagerData, panelOverlay, min);
         }

         if (!(panelOverlay <= 0.004F)) {
            if (!this.crystalsActive && !StageKnife.isValidPrecompile()) {
               this.crystalsActive = true;
            }

            if (this.crystalsActive || !this.crystalsFlag && this.cpxVillagerData == null) {
               if (!this.crystalsFlag) {
                  this.renderMatricesCpx(context, crystalsValue, peekPositionFactor, matricesCpxBound, hashHuntBound);
               }
            } else {
               try {
                  this.renderBotsMatrix(context, crystalsValue, peekPositionFactor, matricesCpxBound, hashHuntBound, botsMatrixFactor, panelOverlay);
               } catch (Throwable e) {
                  this.crystalsActive = true;
                  this.updateDispatcherQueue();
               }
            }
         }
      }
   }

   public boolean isTotemsActive() {
      return this.totemsActive;
   }

   private void handleMultiplyRotation(MatrixStack matrices, OrderedRenderCommandQueue orderedRenderCommandQueue, ItemRenderState itemRenderState, Vector3f vector3f, float x, float multiplyRotationValue, float multiplyRotationValue2) {
      if (!(x <= 0.02F)) {
         matrices.push();
         matrices.translate(vector3f.x, vector3f.y, vector3f.z);
         matrices.multiply(this.multiplyRotationQuaternionf.rotationY(multiplyRotationValue));
         matrices.multiply(rotationX);
         matrices.multiply(this.multiplyRotationQuaternionf.rotationZ(multiplyRotationValue2));
         matrices.scale(x, x, x);
         matrices.translate(-this.multiplyRotationAmount, -this.multiplyCenterY, -this.multiplyCenterZ);
         itemRenderState.render(matrices, orderedRenderCommandQueue, 15728880, OverlayTexture.DEFAULT_UV, 0);
         matrices.pop();
      }
   }

   private float getAmountsForIntFloat(int matrixRenderValue, float peekPositionFactor, float matrixRotateFactor) {
      return MatrixRotateUtilsHandle.getAmount(this.vectorTransformTable[matrixRenderValue * 3 + 1], peekPositionFactor, matrixRotateFactor);
   }

   private void renderCpxAmount(DrawContext context, float cpxValue, float peekPositionFactor, float matrixRotateFactor, float cpxValue4) {
      if (!this.crystalsFlag && this.cpxVillagerData != null && this.cpxVillagerData.isSkin() && !this.cpxVillagerData.planned()) {
         if (this.cpxActive) {
            this.renderAmountAmounts(context, cpxValue, peekPositionFactor, matrixRotateFactor, cpxValue4);
            this.renderAmountAmountsForDrawContextFloat(context, cpxValue, peekPositionFactor, matrixRotateFactor, cpxValue4);
         }

         if (this.matrixVillagerActive && this.eyes != null) {
            float matrixRotateLimit = (this.cpxActive ? this.vectorQuaternionfAmountConst.glowStrength * this.scaledVectorAmount : this.matricesCpxStandbyDanger.getBlendedDanger()) * this.vectorProjectAmount * 0.32F;
            if (!(matrixRotateLimit <= 0.01F) && !(this.faceDepthScale <= 0.0F)) {
               int valueForIntFloat = PositionMatrixCustom.getValueForIntFloat(this.cpxActive ? -13183762 : -54742, matrixRotateLimit);
               float cubeBound = 0.1171875F * this.faceDepthScale;
               float fontOffset = 0.05859375F * this.faceDepthScale;
               render(context, MatrixRotateUtilsHandle.getAmountForFloat(this.x, cpxValue, matrixRotateFactor), MatrixRotateUtilsHandle.getAmount(this.y, peekPositionFactor, cpxValue4), cubeBound, fontOffset, valueForIntFloat);
               render(context, MatrixRotateUtilsHandle.getAmountForFloat(this.rightEyeX, cpxValue, matrixRotateFactor), MatrixRotateUtilsHandle.getAmount(this.rightEyeY, peekPositionFactor, cpxValue4), cubeBound, fontOffset, valueForIntFloat);
            }
         }
      }
   }

   public void handleTotemsFlag(double totemsLimit, double heightValue, float totemsValue, float peekPositionFactor, float w, float n4) {
      this.totemsFlag = w > 0.0F && n4 > 0.0F && totemsLimit >= totemsValue && totemsLimit <= totemsValue + w && heightValue >= peekPositionFactor && heightValue <= peekPositionFactor + n4;
      this.totemsOffsetX = this.totemsFlag ? (float)((totemsLimit - totemsValue) / w * 2.0 - 1.0) : 0.0F;
      this.totemsOffsetY = this.totemsFlag ? (float)((heightValue - peekPositionFactor) / n4 * 2.0 - 1.0) : 0.0F;
   }
}
