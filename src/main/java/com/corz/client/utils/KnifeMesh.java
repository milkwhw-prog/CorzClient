package com.corz.client.utils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack.Entry;
import net.minecraft.util.Identifier;
import com.corz.client.utils.KnifeFinishMesh;
import com.corz.client.utils.KnifeFinish;
import com.corz.client.utils.KnifeRig;

public final class KnifeMesh {
   final float[] lightTable;
   final int[] knifeMeshTable;
   public static final Identifier identifier = Identifier.of("corzclient", "textures/knife/knife.png");
   private static RenderLayer noCull;
   final int[] table;
   private static final Map<String, KnifeMesh> KNIFE_TEXTURES_MAP = new ConcurrentHashMap<>();
   final int[] quadIndices;
   private static final Set<String> keySet = ConcurrentHashMap.newKeySet();
   final float[] modelTable;
   final KnifeFinishMesh knifeFinishMesh;
   final float[] vertexNormals;

   public static RenderLayer getNoCull(Identifier id) {
      return RenderLayers.entityCutoutNoCull(id);
   }

   public void handleAmountsLight(Entry entry, VertexConsumer vertices, int lightValue, int lightValue2) {
      for (int i = 0; i < this.quadIndices.length; i++) {
         int index = this.quadIndices[i];
         vertices.vertex(entry, this.modelTable[index * 3], this.modelTable[index * 3 + 1], this.modelTable[index * 3 + 2])
            .color(this.knifeMeshTable[index])
            .texture(this.lightTable[index * 2], this.lightTable[index * 2 + 1])
            .overlay(lightValue2)
            .light(lightValue)
            .normal(entry, this.vertexNormals[index * 3], this.vertexNormals[index * 3 + 1], this.vertexNormals[index * 3 + 2]);
      }
   }

   public boolean canAmounts(KnifeFinish.Skin skin) {
      return skin != null
         && skin.lightTable.length == this.quadIndices.length
         && skin.table.length == this.quadIndices.length * 2
         && skin.skinVertexData.length == skin.skinTable.length * 8;
   }

   public static KnifeMesh getTexturesKnife(String texturesKnifeText) {
      KnifeMesh knife_textures_map = KNIFE_TEXTURES_MAP.get(texturesKnifeText);
      if (knife_textures_map == null) {
         handle(texturesKnifeText);
      }

      return knife_textures_map;
   }

   private KnifeMesh(KnifeFinishMesh knifeFinishMesh) {
      this.knifeFinishMesh = knifeFinishMesh;
      this.modelTable = knifeFinishMesh.vertexPositions;
      this.vertexNormals = knifeFinishMesh.knifeFinishTable;
      this.lightTable = knifeFinishMesh.uvCoords;
      this.knifeMeshTable = knifeFinishMesh.table;
      this.table = knifeFinishMesh.triangleIndices;
      this.quadIndices = knifeFinishMesh.copyOf;
   }

   public static RenderLayer getEyes(Identifier id) {
      return RenderLayers.eyes(id);
   }

   public static RenderLayer getCullCutout() {
      if (noCull == null) {
         noCull = RenderLayers.entityCutoutNoCull(identifier);
      }

      return noCull;
   }

   public void handleAmounts(Entry entry, VertexConsumer vertices, KnifeFinish.Skin skin) {
      if (skin != null) {
         int[] knifeMeshValues = skin.skinTable;
         float[] skinVertexData = skin.skinVertexData;

         for (int i = 0; i < knifeMeshValues.length; i++) {
            int knifeMeshValue = knifeMeshValues[i] * 4;
            if (knifeMeshValue + 3 >= this.quadIndices.length) {
               return;
            }

            for (int j = 0; j < 4; j++) {
               this.handleAmountsLight(entry, vertices, this.quadIndices[knifeMeshValue + j], skinVertexData, (i * 4 + j) * 2);
            }

            for (int k = 3; k >= 0; k--) {
               this.handleAmountsLight(entry, vertices, this.quadIndices[knifeMeshValue + k], skinVertexData, (i * 4 + k) * 2);
            }
         }
      }
   }

   public int getValue() {
      return this.knifeMeshTable.length;
   }

   public int getQuadIndices() {
      return this.quadIndices.length / 4;
   }

   public static void handle(String knifeMeshText) {
      if (keySet.add(knifeMeshText)) {
         CompletableFuture.runAsync(() -> {
            KnifeMesh knifeMesh = getKnifeMesh(knifeMeshText);
            if (knifeMesh != null) {
               KNIFE_TEXTURES_MAP.put(knifeMeshText, knifeMesh);
            }
         });
      }
   }

   static KnifeMesh getKnifeMesh(String knifeMeshText) {
      KnifeFinishMesh knifeFinishMesh = KnifeFinishMesh.getKnifeFinishMesh(knifeMeshText);
      return knifeFinishMesh == null ? null : new KnifeMesh(knifeFinishMesh);
   }

   public static void updateInstanceModel() {
      for (KnifeRig knifeRig : new KnifeRig[]{KnifeRig.M9_BAYONET_TP, KnifeRig.STILETTO_TP, KnifeRig.BUTTERFLY_TP, KnifeRig.INSTANCE}) {
         for (int i = 0; i < knifeRig.getParentParts(); i++) {
            handle(knifeRig.getParentParts(i).model());
         }
      }
   }

   public static boolean canModel(KnifeRig knifeRig) {
      boolean model = true;

      for (int i = 0; i < knifeRig.getParentParts(); i++) {
         if (getTexturesKnife(knifeRig.getParentParts(i).model()) == null) {
            model = false;
         }
      }

      return model;
   }

   public int getTable() {
      return this.table.length / 3;
   }

   public KnifeFinishMesh getKnifeFinishMesh() {
      return this.knifeFinishMesh;
   }

   private void handleAmountsLight(Entry entry, VertexConsumer vertices, int lightValue, float[] lightValues, int index) {
      vertices.vertex(entry, this.modelTable[lightValue * 3], this.modelTable[lightValue * 3 + 1], this.modelTable[lightValue * 3 + 2])
         .color(-1)
         .texture(lightValues[index], lightValues[index + 1])
         .overlay(OverlayTexture.DEFAULT_UV)
         .light(15728880)
         .normal(entry, this.vertexNormals[lightValue * 3], this.vertexNormals[lightValue * 3 + 1], this.vertexNormals[lightValue * 3 + 2]);
   }

   static KnifeMesh getKnifeMesh(byte[] knifeMeshBytes) {
      return new KnifeMesh(KnifeFinishMesh.getMeshKnife(knifeMeshBytes));
   }

   public void handleAmountsLight(Entry entry, VertexConsumer vertices, int lightValue, int lightValue2, KnifeFinish.Skin skin) {
      if (!this.canAmounts(skin)) {
         this.handleAmountsLight(entry, vertices, lightValue, lightValue2);
      } else {
         float[] lightValues = skin.table;
         int[] lightTable2 = skin.lightTable;

         for (int i = 0; i < this.quadIndices.length; i++) {
            int lightValue3 = this.quadIndices[i];
            vertices.vertex(entry, this.modelTable[lightValue3 * 3], this.modelTable[lightValue3 * 3 + 1], this.modelTable[lightValue3 * 3 + 2])
               .color(lightTable2[i])
               .texture(lightValues[i * 2], lightValues[i * 2 + 1])
               .overlay(lightValue2)
               .light(lightValue)
               .normal(entry, this.vertexNormals[lightValue3 * 3], this.vertexNormals[lightValue3 * 3 + 1], this.vertexNormals[lightValue3 * 3 + 2]);
         }
      }
   }
}
