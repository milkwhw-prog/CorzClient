package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.MatrixStack.Entry;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import com.corz.client.utils.C2Knife;
import com.corz.client.utils.C2Cues;
import com.corz.client.utils.C2Model;
import com.corz.client.utils.KnifeFinish;
import com.corz.client.utils.KnifeFinishTextures;
import com.corz.client.utils.KnifeRig;
import com.corz.client.utils.C2PackAudio;
import com.corz.client.utils.C2Assets;
import com.corz.client.utils.C2Frame;
import com.corz.client.utils.C2Renderer;
import com.corz.client.utils.KnifeMesh;
import com.corz.client.utils.C2Clips;

public final class C2Preview {
   private static final Map<C2Knife, C2Preview.Frame> FRAME_CACHE = new EnumMap<>(C2Knife.class);
   private static final Set<C2Knife> noneOf = EnumSet.noneOf(C2Knife.class);
   private static final Matrix4f MATRIX4_FMATRIX4F = new Matrix4f();
   private static final Matrix4f MATRIX4F2 = new Matrix4f();
   private static final Matrix4f MATRIX4F4 = new Matrix4f();
   private static final Matrix4f MATRIX4F = new Matrix4f();

   public static float getAmount(float c2PreviewLimit, float c2PreviewValue) {
      float c2PreviewBound;
      if (c2PreviewLimit < 0.0F) {
         c2PreviewBound = 1.0F + c2PreviewLimit / 0.2F;
      } else {
         if (!(c2PreviewLimit > c2PreviewValue)) {
            return 1.0F;
         }

         c2PreviewBound = 1.0F - (c2PreviewLimit - c2PreviewValue) / 0.2F;
      }

      float c2PreviewFactor = Math.max(0.0F, Math.min(1.0F, c2PreviewBound));
      return c2PreviewFactor * c2PreviewFactor * (3.0F - 2.0F * c2PreviewFactor);
   }

   public static List<String> getItems(C2Knife c2Knife) {
      C2Preview.Frame frame = canFlag(c2Knife) ? FRAME_CACHE.get(c2Knife) : null;
      return frame == null ? List.of() : frame.invertMatrixStringList;
   }

   public static List<C2Cues.Contact> getButterfly(String butterflyText) {
      return getModel(C2Knife.BUTTERFLY, butterflyText);
   }

   private static boolean canBonesMesh(C2Model c2Model, int bonesMeshValue, int parentBonesValue) {
      for (int i = 0; i < c2Model.meshModelBones.length; i++) {
         if (c2Model.meshModelBones[i].vertices().length != 0 && c2Model.canParentBones(i, bonesMeshValue) && (parentBonesValue < 0 || !c2Model.canParentBones(parentBonesValue, i))) {
            return true;
         }
      }

      return false;
   }

   public static float getLength(C2Knife c2Knife, String lengthText) {
      C2Model.Clip unmodifiableModel = getUnmodifiableModel(c2Knife, lengthText);
      return unmodifiableModel == null ? 0.0F : unmodifiableModel.length();
   }

   public static boolean canButterfly(
      MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, KnifeFinish.Finish finish, String butterflyText, float butterflyValue, boolean butterflyFlag, int bladeCustomValue
   ) {
      return canMatrixPop(C2Knife.BUTTERFLY, matrixStack, orderedRenderCommandQueue, finish, butterflyText, butterflyValue, butterflyFlag, bladeCustomValue);
   }

   public static boolean canInstance(KnifeFinish.Finish finish) {
      return finish != null && !finish.isCompactFlag() ? KnifeFinishTextures.getTimeRigs(KnifeRig.INSTANCE, finish) != null : true;
   }

   public static List<C2Cues.Contact> getModel(C2Knife c2Knife, String modelText) {
      C2Model.Clip unmodifiableModel = getUnmodifiableModel(c2Knife, modelText);
      if (unmodifiableModel == null) {
         return List.of();
      } else {
         List lengthCues = C2PackAudio.getLengthCues(c2Knife, unmodifiableModel);
         return lengthCues != null ? lengthCues : C2Cues.getInspectBlade(FRAME_CACHE.get(c2Knife).c2Model, unmodifiableModel);
      }
   }

   public static boolean canFlag(C2Knife c2Knife) {
      return getTreasureNone(c2Knife) != null;
   }

   private static C2Preview.Frame getTreasureNone(C2Knife c2Knife) {
      if (c2Knife == null) {
         return null;
      } else {
         C2Assets.handleSupplyAsync(c2Knife);
         C2Assets.Loaded supplyAsync = C2Assets.getSupplyAsync(c2Knife);
         if (supplyAsync == null) {
            return null;
         } else {
            C2Preview.Frame treasureNone = FRAME_CACHE.get(c2Knife);
            if (treasureNone == null || treasureNone.loaded != supplyAsync) {
               if (noneOf.contains(c2Knife)) {
                  return null;
               }

               try {
                  treasureNone = new C2Preview.Frame(supplyAsync);
               } catch (RuntimeException e) {
                  noneOf.add(c2Knife);
                  System.err.println("[C" + c2Knife.treasureText + " cannot be previewed: " + e);
                  return null;
               }

               FRAME_CACHE.put(c2Knife, treasureNone);
            }

            return treasureNone;
         }
      }
   }

   private static void handleLightNormal(float[] lightNormalValues, Entry entry, VertexConsumer vertices, int lightNormalValue) {
      for (int i = 0; i < lightNormalValues.length; i += 8) {
         vertices.vertex(entry, lightNormalValues[i], lightNormalValues[i + 1], lightNormalValues[i + 2])
            .color(-1)
            .texture(lightNormalValues[i + 3], lightNormalValues[i + 4])
            .overlay(OverlayTexture.DEFAULT_UV)
            .light(lightNormalValue)
            .normal(entry, lightNormalValues[i + 5], lightNormalValues[i + 6], lightNormalValues[i + 7]);
      }
   }

   public static boolean isButterfly() {
      return canNone(C2Knife.BUTTERFLY);
   }

   private static C2Model.Clip getUnmodifiableModel(C2Knife c2Knife, String unmodifiableModelText) {
      return canFlag(c2Knife) && unmodifiableModelText != null ? FRAME_CACHE.get(c2Knife).c2Model.unmodifiableMap.get(unmodifiableModelText) : null;
   }

   public static float getMax(C2Knife c2Knife) {
      C2Preview.Frame frame = canFlag(c2Knife) ? FRAME_CACHE.get(c2Knife) : null;
      return frame == null ? 1.0F : frame.maxExtent;
   }

   private static boolean canBladeCustom(
      C2Model c2Model, Matrix4f[] matrix4fs, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, int bladeCustomValue, KnifeFinishTextures.Skin skin2
   ) {
      int bladeCustomLimit = C2Frame.downBladeTexts.length;
      Matrix4f[] matrixModel = C2Renderer.getMatrixModel(c2Model);

      for (int i = 0; i < bladeCustomLimit; i++) {
         KnifeMesh texturesKnife = KnifeMesh.getTexturesKnife(C2Frame.downBladeTexts[i]);
         if (texturesKnife == null || !texturesKnife.canAmounts(skin2.getSkins(i)) || C2Frame.getBladeTexts(c2Model, i) < 0 || matrixModel[i] == null) {
            return false;
         }
      }

      for (int j = 0; j < bladeCustomLimit; j++) {
         int bladeTexts = C2Frame.getBladeTexts(c2Model, j);
         KnifeMesh texturesKnife2 = KnifeMesh.getTexturesKnife(C2Frame.downBladeTexts[j]);
         KnifeFinish.Skin skins = skin2.getSkins(j);
         matrixStack.push();

         try {
            matrixStack.multiplyPositionMatrix(MATRIX4F.set(matrix4fs[bladeTexts]).mul(matrixModel[j]));
            orderedRenderCommandQueue.submitCustom(matrixStack, skin2.renderLayerCorzrenderKnife, (entry, skin) -> texturesKnife2.handleAmountsLight(entry, skin, bladeCustomValue, OverlayTexture.DEFAULT_UV, skins));
            if (skin2.canLayerRender(j)) {
               orderedRenderCommandQueue.getBatchingQueue(1).submitCustom(matrixStack, skin2.renderLayer, (skin, mesh) -> texturesKnife2.handleAmounts(skin, mesh, skins));
            }
         } finally {
            matrixStack.pop();
         }
      }

      return true;
   }

   public static boolean isButterflyFlag() {
      return canFlag(C2Knife.BUTTERFLY);
   }

   public static float getButterflyForString(String butterflyText) {
      return getLength(C2Knife.BUTTERFLY, butterflyText);
   }

   private C2Preview() {
   }

   public static boolean canMatrixPop(
      C2Knife c2Knife, MatrixStack matrixStack, OrderedRenderCommandQueue orderedRenderCommandQueue, KnifeFinish.Finish finish, String matrixPopText, float matrixPopValue, boolean matrixPopFlag, int bladeCustomValue
   ) {
      if (!canFlag(c2Knife)) {
         return false;
      } else {
         C2Preview.Frame frame = FRAME_CACHE.get(c2Knife);
         C2Model c2Model = frame.c2Model;
         frame.handleAmountsLength(matrixPopText == null ? null : c2Model.unmodifiableMap.get(matrixPopText), matrixPopValue);
         Matrix4f[] matrix4fs = frame.bonesMeshInvertMatrix;
         MATRIX4F4.set(matrix4fs[frame.mulValue]).invert();
         MATRIX4_FMATRIX4F.identity();
         if (matrixPopFlag) {
            MATRIX4_FMATRIX4F.set(frame.matrixPopMatrix4f).mul(frame.getMul()).setTranslation(0.0F, 0.0F, 0.0F);
         }

         MATRIX4_FMATRIX4F.translate(-frame.matrixPopVector3f.x, -frame.matrixPopVector3f.y, -frame.matrixPopVector3f.z).mul(MATRIX4F4);
         matrixStack.push();

         try {
            matrixStack.multiplyPositionMatrix(MATRIX4_FMATRIX4F);
            KnifeFinishTextures.Skin skin = c2Knife.isClassics() ? KnifeFinishTextures.getTimeRigs(KnifeRig.INSTANCE, finish) : null;
            if (skin == null || !canBladeCustom(c2Model, matrix4fs, matrixStack, orderedRenderCommandQueue, bladeCustomValue, skin)) {
               RenderLayer noCull = C2Renderer.getNoCull(frame.loaded);

               for (int i = 0; i < c2Model.meshModelBones.length; i++) {
                  if (frame.matrixPopFlags[i] && !(Math.abs(matrix4fs[i].determinant3x3()) < 1.0E-10F)) {
                     float[] vertices = c2Model.meshModelBones[i].vertices();
                     matrixStack.push();

                     try {
                        matrixStack.multiplyPositionMatrix(matrix4fs[i]);
                        orderedRenderCommandQueue.submitCustom(matrixStack, noCull, (lightx, vertices2) -> handleLightNormal(vertices, lightx, vertices2, bladeCustomValue));
                     } finally {
                        matrixStack.pop();
                     }
                  }
               }
            }
         } finally {
            matrixStack.pop();
         }

         return true;
      }
   }

   public static List<String> getButterfly() {
      return getItems(C2Knife.BUTTERFLY);
   }

   public static boolean canNone(C2Knife c2Knife) {
      return C2Assets.canVisualsLoad(c2Knife) || noneOf.contains(c2Knife);
   }

   public static float getButterflyFloat() {
      return getMax(C2Knife.BUTTERFLY);
   }

   static int[] getBonesMesh(C2Model c2Model, C2Knife c2Knife) {
      if (c2Knife.isClassics()) {
         int bonesMesh = c2Model.getBonesMesh("butterfly");
         int bonesMesh2 = c2Model.getBonesMesh("knife_and_right");
         if (bonesMesh >= 0 && bonesMesh2 >= 0) {
            return new int[]{bonesMesh2, bonesMesh};
         } else {
            throw new IllegalStateException("C2 butterfly bones missing");
         }
      } else {
         int parentBonesValue = c2Model.getBonesMesh("righthand_pos");
         int bonesMeshLimit = -1;

         for (int i = parentBonesValue < 0 ? -1 : c2Model.meshModelBones[parentBonesValue].parent(); i >= 0; i = c2Model.meshModelBones[i].parent()) {
            if (canBonesMesh(c2Model, i, parentBonesValue)) {
               bonesMeshLimit = i;
               break;
            }
         }

         if (bonesMeshLimit >= 0) {
            for (int j = 0; j < c2Model.meshModelBones.length; j++) {
               if (c2Model.meshModelBones[j].parent() == bonesMeshLimit && (parentBonesValue < 0 || !c2Model.canParentBones(parentBonesValue, j)) && canBonesMesh(c2Model, j, -1)) {
                  return new int[]{bonesMeshLimit, j};
               }
            }

            return new int[]{bonesMeshLimit, bonesMeshLimit};
         } else {
            for (int k = 0; k < c2Model.meshModelBones.length; k++) {
               if (c2Model.meshModelBones[k].vertices().length > 0) {
                  int index = k;

                  while (c2Model.meshModelBones[index].parent() >= 0 && c2Model.meshModelBones[c2Model.meshModelBones[index].parent()].parent() >= 0) {
                     index = c2Model.meshModelBones[index].parent();
                  }

                  return new int[]{-1, index};
               }
            }

            throw new IllegalStateException("Knife model has no mesh");
         }
      }
   }

   static final class Frame {
      final String modelBonesText;
      final float[] bonesMesh;
      final C2Assets.Loaded loaded;
      final Vector3f matrixPopVector3f = new Vector3f();
      final float[] bonesMeshLength;
      final boolean[] matrixPopFlags;
      float maxExtent;
      final List<String> invertMatrixStringList;
      final Matrix4f matrixPopMatrix4f = new Matrix4f();
      final int boneIndex;
      final Matrix4f[] bonesMeshInvertMatrix;
      final C2Model c2Model;
      final int mulValue;

      Frame(C2Assets.Loaded loaded) {
         this.maxExtent = 1.0F;
         this.loaded = loaded;
         this.c2Model = loaded.model();
         this.modelBonesText = C2Clips.formatStaticIdle(loaded.knife(), loaded.model());
         int[] bonesMesh = C2Preview.getBonesMesh(this.c2Model, loaded.knife());
         this.boneIndex = bonesMesh[0];
         this.mulValue = bonesMesh[1];
         this.matrixPopFlags = new boolean[this.c2Model.meshModelBones.length];

         for (int i = 0; i < this.matrixPopFlags.length; i++) {
            this.matrixPopFlags[i] = this.c2Model.meshModelBones[i].vertices().length > 0 && this.c2Model.canParentBones(i, this.mulValue);
         }

         this.invertMatrixStringList = C2Clips.getUnmodifiable(loaded.knife(), this.c2Model);
         this.bonesMeshLength = this.c2Model.getBonesMeshMeshModelBones();
         this.bonesMesh = this.c2Model.getBonesMeshMeshModelBones();
         this.bonesMeshInvertMatrix = this.c2Model.getBonesMesh();
         this.updateInvertMatrix();
      }

      void handleAmountsModel(C2Model.Clip clip, float modelBound) {
         this.handleModelBones(this.bonesMeshLength);
         if (clip != null) {
            clip.handleTracks(Math.max(0.0F, Math.min(clip.length(), modelBound)), this.bonesMeshLength);
         }

         this.c2Model.handlePivotParent(this.bonesMeshLength, this.bonesMeshInvertMatrix);
      }

      private void handleModelBones(float[] modelBonesValues) {
         Arrays.fill(modelBonesValues, 0.0F);

         for (int i = 0; i < this.c2Model.meshModelBones.length; i++) {
            Arrays.fill(modelBonesValues, i * 9 + 6, i * 9 + 9, 1.0F);
         }

         C2Model.Clip unmodifiablemap = this.c2Model.unmodifiableMap.get(this.modelBonesText);
         if (unmodifiablemap != null) {
            unmodifiablemap.handleTracks(0.0F, modelBonesValues);
         }
      }

      private void updateInvertMatrix() {
         this.handleAmountsModel(null, 0.0F);
         this.matrixPopMatrix4f.set(this.getMul()).invert();
         int invertMatrixSize = this.c2Model.meshModelBones.length;
         float[][] floats = new float[invertMatrixSize][];
         Vector3f vector3f = new Vector3f(Float.MAX_VALUE);
         Vector3f vector3f2 = new Vector3f(-Float.MAX_VALUE);
         Vector3f vector3f3 = new Vector3f();
         Matrix4f invert = new Matrix4f(this.bonesMeshInvertMatrix[this.mulValue]).invert();
         Matrix4f matrix4f = new Matrix4f();

         for (int i = 0; i < invertMatrixSize; i++) {
            if (this.matrixPopFlags[i]) {
               float[] vertices = this.c2Model.meshModelBones[i].vertices();
               float invertMatrixBound = Float.MAX_VALUE;
               float maxValue = Float.MAX_VALUE;
               float wrapDegreesBound = Float.MAX_VALUE;
               float pilotStatusBound = -Float.MAX_VALUE;
               float maxValue5 = -Float.MAX_VALUE;
               float maxValue6 = -Float.MAX_VALUE;
               boolean invertMatrixFlag = Math.abs(this.bonesMeshInvertMatrix[i].determinant3x3()) >= 1.0E-10F;
               matrix4f.set(invert).mul(this.bonesMeshInvertMatrix[i]);

               for (int j = 0; j < vertices.length; j += 8) {
                  invertMatrixBound = Math.min(invertMatrixBound, vertices[j]);
                  pilotStatusBound = Math.max(pilotStatusBound, vertices[j]);
                  maxValue = Math.min(maxValue, vertices[j + 1]);
                  maxValue5 = Math.max(maxValue5, vertices[j + 1]);
                  wrapDegreesBound = Math.min(wrapDegreesBound, vertices[j + 2]);
                  maxValue6 = Math.max(maxValue6, vertices[j + 2]);
                  if (invertMatrixFlag) {
                     matrix4f.transformPosition(vertices[j], vertices[j + 1], vertices[j + 2], vector3f3);
                     vector3f.min(vector3f3);
                     vector3f2.max(vector3f3);
                  }
               }

               float invertMatrixValue = (invertMatrixBound + pilotStatusBound) / 2.0F;
               float invertMatrixValue2 = (maxValue + maxValue5) / 2.0F;
               float invertMatrixValue3 = (wrapDegreesBound + maxValue6) / 2.0F;
               float max7 = 0.0F;

               for (int k = 0; k < vertices.length; k += 8) {
                  float invertMatrixFactor = vertices[k] - invertMatrixValue;
                  float invertMatrixFactor2 = vertices[k + 1] - invertMatrixValue2;
                  float invertMatrixFactor3 = vertices[k + 2] - invertMatrixValue3;
                  max7 = Math.max(max7, (float)Math.sqrt(invertMatrixFactor * invertMatrixFactor + invertMatrixFactor2 * invertMatrixFactor2 + invertMatrixFactor3 * invertMatrixFactor3));
               }

               floats[i] = new float[]{invertMatrixValue, invertMatrixValue2, invertMatrixValue3, max7};
            }
         }

         if (vector3f.x > vector3f2.x) {
            vector3f.zero();
            vector3f2.zero();
         }

         this.matrixPopVector3f.set(vector3f).add(vector3f2).mul(0.5F);
         float max8 = 0.0F;
         ArrayList arrayList = new ArrayList();
         arrayList.add(null);

         for (String invertMatrixText : this.invertMatrixStringList) {
            arrayList.add(this.c2Model.unmodifiableMap.get(invertMatrixText));
         }

         for (C2Model.Clip clip : (Iterable<com.corz.client.utils.C2Model.Clip>) (arrayList)) {
            float invertMatrixValue4 = clip == null ? 0.0F : clip.length();

            for (float invertMatrixTotal = 0.0F; invertMatrixTotal <= invertMatrixValue4 + 1.0E-4F; invertMatrixTotal += 0.033333335F) {
               this.handleAmountsModel(clip, invertMatrixTotal);
               invert.set(this.bonesMeshInvertMatrix[this.mulValue]).invert();

               for (int m = 0; m < invertMatrixSize; m++) {
                  if (floats[m] != null && !(Math.abs(this.bonesMeshInvertMatrix[m].determinant3x3()) < 1.0E-10F)) {
                     matrix4f.set(invert).mul(this.bonesMeshInvertMatrix[m]).transformPosition(floats[m][0], floats[m][1], floats[m][2], vector3f3);
                     max8 = Math.max(max8, vector3f3.distance(this.matrixPopVector3f) + floats[m][3]);
                  }
               }
            }
         }

         this.maxExtent = Math.max(0.001F, max8);
      }

      void handleAmountsLength(C2Model.Clip clip, float lengthBound) {
         if (clip == null) {
            this.handleAmountsModel(null, 0.0F);
         } else {
            float lengthLimit = C2Preview.getAmount(lengthBound, clip.length());
            this.handleModelBones(this.bonesMeshLength);
            if (lengthLimit > 0.0F) {
               this.handleModelBones(this.bonesMesh);
               clip.handleTracks(Math.max(0.0F, Math.min(clip.length(), lengthBound)), this.bonesMesh);

               for (int i = 0; i < this.bonesMeshLength.length; i++) {
                  float lengthTotal = this.bonesMesh[i] - this.bonesMeshLength[i];
                  int lengthLimit2 = i % 9;
                  if (lengthLimit2 >= 3 && lengthLimit2 < 6) {
                     lengthTotal -= (float)Math.floor((lengthTotal + 180.0F) / 360.0F) * 360.0F;
                  }

                  this.bonesMeshLength[i] = this.bonesMeshLength[i] + lengthTotal * lengthLimit;
               }
            }

            this.c2Model.handlePivotParent(this.bonesMeshLength, this.bonesMeshInvertMatrix);
         }
      }

      private Matrix4f getMul() {
         return this.boneIndex >= 0 && this.boneIndex != this.mulValue
            ? C2Preview.MATRIX4F2.set(this.bonesMeshInvertMatrix[this.boneIndex]).invert().mul(this.bonesMeshInvertMatrix[this.mulValue])
            : C2Preview.MATRIX4F2.set(this.bonesMeshInvertMatrix[this.mulValue]);
      }
   }
}
