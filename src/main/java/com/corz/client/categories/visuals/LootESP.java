package com.corz.client.categories.visuals;

import com.corz.client.CorzClient;
import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.gui.MatrixPeekUtilsRender;
import com.corz.client.gui.TickProgress;
import com.corz.client.inventory.DrawMatricesUtils;
import com.corz.client.inventory.ItemList;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.VectorDotUtils;
import com.corz.client.render.VertexBuilderUtils;
import com.corz.client.render.VertexConsumerUtilsVector;
import com.corz.client.render.VertexTexturePeek;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.joml.Matrix4f;
import ai.auth.Module;
import com.corz.client.categories.visuals.ShowHUD;
import com.corz.client.utils.IconsPngType;

public class LootESP extends Module {
   private final BooleanSetting booleanBase;
   private final BooleanSetting tracers;
   private final BooleanSetting groupStacks;
   private final ItemList itemList;
   private final List<LootESP.Mark> labelDropItems;
   private long lastNanoTime;
   private final IntegerSetting integerHelper;
   private final BooleanSetting showDistance;
   private final IntegerSetting boxAlpha;
   private final BooleanSetting onlyValuable;
   private final BooleanSetting boxes = new BooleanSetting("Boxes", "Draw an outline box around each drop (off = labels only)", false);
   private final IntegerSetting lineWidth;
   private final EnumSetting enumHelper = new EnumSetting("Mode", "Box style", "Corner ESP", "Corner ESP", "Normal ESP");
   private final Map<Integer, Float> mergeTracersMap;

   private static float getAmount(float lootEspLimit) {
      return lootEspLimit < 0.0F ? 0.0F : (lootEspLimit > 1.0F ? 1.0F : lootEspLimit);
   }

   private static void handle(Matrix4f matrix, float lootEspValue, float deltaY, float lootEspValue3, float lootEspValue4, float f5, float amountForDouble, int n2, float blockOverlayValue) {
      float lootEspValue9 = (lootEspValue4 - lootEspValue) * 0.28F;
      float lootEspValue10 = (f5 - deltaY) * 0.28F;
      float lootEspValue11 = (amountForDouble - lootEspValue3) * 0.28F;
      handleForMatrix4fFloat(matrix, lootEspValue, deltaY, lootEspValue3, lootEspValue9, lootEspValue10, lootEspValue11, n2, blockOverlayValue);
      handleForMatrix4fFloat(matrix, lootEspValue4, deltaY, lootEspValue3, -lootEspValue9, lootEspValue10, lootEspValue11, n2, blockOverlayValue);
      handleForMatrix4fFloat(matrix, lootEspValue4, deltaY, amountForDouble, -lootEspValue9, lootEspValue10, -lootEspValue11, n2, blockOverlayValue);
      handleForMatrix4fFloat(matrix, lootEspValue, deltaY, amountForDouble, lootEspValue9, lootEspValue10, -lootEspValue11, n2, blockOverlayValue);
      handleForMatrix4fFloat(matrix, lootEspValue, f5, lootEspValue3, lootEspValue9, -lootEspValue10, lootEspValue11, n2, blockOverlayValue);
      handleForMatrix4fFloat(matrix, lootEspValue4, f5, lootEspValue3, -lootEspValue9, -lootEspValue10, lootEspValue11, n2, blockOverlayValue);
      handleForMatrix4fFloat(matrix, lootEspValue4, f5, amountForDouble, -lootEspValue9, -lootEspValue10, -lootEspValue11, n2, blockOverlayValue);
      handleForMatrix4fFloat(matrix, lootEspValue, f5, amountForDouble, lootEspValue9, -lootEspValue10, -lootEspValue11, n2, blockOverlayValue);
   }

   private boolean canFlag(ItemStack stack) {
      return this.itemList.isContains(stack.getItem());
   }

   @Override
   public void onRender3D(QueueCommand queueCommand) {
      this.labelDropItems.clear();
      if (mc.world != null && mc.player != null) {
         Camera camera = mc.gameRenderer.getCamera();
         Vec3d cameraPos = camera.getCameraPos();
         float tickProgress = queueCommand.tickProgress;
         float lootEspValue = this.integerHelper.getValue();
         boolean equals = this.enumHelper.getCurrentOption().equals("Normal ESP");
         float blockOverlayValue = this.lineWidth.getValue();
         float lootEspLimit = this.boxAlpha.getValue() / 100.0F;

         for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof ItemEntity itemEntity) {
               ItemStack stack = itemEntity.getStack();
               if (stack != null && !stack.isEmpty() && (!this.onlyValuable.isEnabled() || this.canFlag(stack))) {
                  double lerp = MathHelper.lerp((double)tickProgress, itemEntity.lastX, itemEntity.getX());
                  double lerp2 = MathHelper.lerp((double)tickProgress, itemEntity.lastY, itemEntity.getY());
                  double lerp3 = MathHelper.lerp((double)tickProgress, itemEntity.lastZ, itemEntity.getZ());
                  double lootEspFactor = lerp - cameraPos.x;
                  double lootEspFactor2 = lerp2 - cameraPos.y;
                  double lootEspFactor3 = lerp3 - cameraPos.z;
                  double distance = Math.sqrt(lootEspFactor * lootEspFactor + lootEspFactor2 * lootEspFactor2 + lootEspFactor3 * lootEspFactor3);
                  if (!(distance > lootEspValue)) {
                     float lootEspValue3 = getAmount((float)((lootEspValue - distance) / 8.0));
                     this.labelDropItems.add(new LootESP.Mark(itemEntity, stack, lerp, lerp2, lerp3, distance, this.getRarityUncommon(stack), lootEspValue3));
                  }
               }
            }
         }

         if (!this.labelDropItems.isEmpty()) {
            boolean booleanValue = this.boxes.isEnabled();
            boolean enabled = this.tracers.isEnabled();
            if (booleanValue || enabled) {
               Matrix4f positionMatrix = queueCommand.matrices.peek().getPositionMatrix();

               for (LootESP.Mark mark : this.labelDropItems) {
                  float height = mark.entity.getHeight();
                  if (booleanValue) {
                     float widthScaled = mark.entity.getWidth() / 2.0F;
                     float vertexBuilderValue = (float)(mark.x - widthScaled - cameraPos.x);
                     float deltaY = (float)(mark.y - cameraPos.y);
                     float lootEspValue5 = (float)(mark.z - widthScaled - cameraPos.z);
                     float vertexColorValue = (float)(mark.x + widthScaled - cameraPos.x);
                     float f5 = (float)(mark.y + height - cameraPos.y);
                     float amountForDouble = (float)(mark.z + widthScaled - cameraPos.z);
                     int n2 = getValue(mark.color, 0.78F * mark.fade);
                     if (lootEspLimit > 0.001F) {
                        int color = mark.color;
                        VertexBuilderUtils.handle(
                           positionMatrix,
                           vertexBuilderValue,
                           deltaY,
                           lootEspValue5,
                           vertexColorValue,
                           f5,
                           amountForDouble,
                           (color >> 16 & 0xFF) / 255.0F,
                           (color >> 8 & 0xFF) / 255.0F,
                           (color & 0xFF) / 255.0F,
                           lootEspLimit * mark.fade
                        );
                     }

                     if (equals) {
                        VertexBuilderUtils.handle(positionMatrix, vertexBuilderValue, deltaY, lootEspValue5, vertexColorValue, f5, amountForDouble, n2, blockOverlayValue);
                     } else {
                        handle(positionMatrix, vertexBuilderValue, deltaY, lootEspValue5, vertexColorValue, f5, amountForDouble, n2, blockOverlayValue);
                     }
                  }

                  if (enabled) {
                     VectorDotUtils.handle(
                        mark.x, mark.y + height * 0.5, mark.z, getValue(mark.color, 0.22F * mark.fade), getValue(mark.color, 0.7F * mark.fade), 1.0F
                     );
                  }
               }

               VertexBuilderUtils.updateDrawImmediate();
            }
         }
      }
   }

   private static int getValue(int lootEspValue, float lootEspBound) {
      int round = Math.round(Math.max(0.0F, Math.min(1.0F, lootEspBound)) * 255.0F);
      return round << 24 | lootEspValue & 16777215;
   }

   private void renderMatricesAmount(DrawContext context, VertexTexturePeek vertexTexturePeek, LootESP.Label label, float matricesDivisor, float matricesValue) {
      Vec3d windowFramebuffer = VertexConsumerUtilsVector.getWindowFramebuffer(new Vec3d(label.labelAmount, label.matricesAmount + 0.34, label.posZ));
      if (windowFramebuffer != null && !(windowFramebuffer.z <= 0.0) && !(windowFramebuffer.z >= 1.0)) {
         float matricesValue2 = (float)(windowFramebuffer.x / matricesDivisor);
         float matricesValue3 = (float)(windowFramebuffer.y / matricesDivisor);
         float clamp = MathHelper.clamp(1.12F - (float)label.distance / 56.0F, 0.72F, 1.0F);
         float materialFactor = 18.0F * clamp;
         float matricesValue5 = 7.0F * clamp;
         float matricesValue6 = 10.0F * clamp;
         float matricesValue7 = 4.0F * clamp;
         float corzclientValue = 11.0F * clamp;
         float corzLicenseLimit = 5.0F * clamp;
         String matricesText = label.itemStack.getName().getString();
         String matricesText2 = label.matricesValue > 1 ? " x" + label.matricesValue : "";
         String corzclientText = this.showDistance.isEnabled() ? "  " + Math.round(label.distance) + "m" : "";
         float corzclientAmounts = vertexTexturePeek.getCorzclientAmounts(matricesText, corzclientValue);
         float corzclientAmounts2 = vertexTexturePeek.getCorzclientAmounts(matricesText2, corzclientValue);
         float corzclientAmounts3 = vertexTexturePeek.getCorzclientAmounts(corzclientText, corzclientValue);
         float matricesValue10 = matricesValue5 + matricesValue6 + matricesValue7 + corzclientAmounts + corzclientAmounts2 + corzclientAmounts3 + matricesValue5;
         float matrixPeekValue = matricesValue2 - matricesValue10 / 2.0F;
         float deltaVar = matricesValue3 - materialFactor;
         float matricesFactor = 1.0F - (1.0F - matricesValue) * (1.0F - matricesValue) * (1.0F - matricesValue);
         float matricesLimit = label.fadeAlpha * matricesFactor;
         if (!(matricesLimit < 0.02F)) {
            float panelOverlay = CorzRoundedUtils.getPanelOverlay(matricesLimit);

            try {
               MatrixPeekUtilsRender.renderForDrawContextFloat(context, matrixPeekValue, deltaVar, matricesValue10, materialFactor, corzLicenseLimit, -233499369, 620756991);
               if (matricesLimit > 0.5F) {
                  context.getMatrices().pushMatrix();
                  context.getMatrices().scale(matricesDivisor, matricesDivisor);
                  context.getMatrices().translate(matrixPeekValue + matricesValue5, deltaVar + (materialFactor - matricesValue6) / 2.0F);
                  context.getMatrices().scale(matricesValue6 / 16.0F, matricesValue6 / 16.0F);
                  DrawMatricesUtils.renderMatricesMatrix(context, label.itemStack, 0, 0, 16);
                  context.getMatrices().popMatrix();
               }

               float peekEndValue = matrixPeekValue + matricesValue5 + matricesValue6 + matricesValue7;
               float matricesValue13 = deltaVar + (materialFactor - vertexTexturePeek.getAmountForFloat(corzclientValue)) / 2.0F;
               vertexTexturePeek.handlePeekEnd(matricesText, peekEndValue, matricesValue13, label.labelColor, corzclientValue);
               float matricesTotal = peekEndValue + corzclientAmounts;
               if (!matricesText2.isEmpty()) {
                  vertexTexturePeek.handlePeekEnd(matricesText2, matricesTotal, matricesValue13, -6645087, corzclientValue);
                  matricesTotal += corzclientAmounts2;
               }

               if (!corzclientText.isEmpty()) {
                  vertexTexturePeek.handlePeekEnd(corzclientText, matricesTotal, matricesValue13, -6645087, corzclientValue);
               }
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            }
         }
      }
   }

   private static void handleForMatrix4fFloat(Matrix4f matrix, float lootEspValue, float y, float amountForDouble, float lootEspValue4, float lootEspValue5, float lootEspValue6, int playerValue, float z22) {
      VertexBuilderUtils.handleForMatrix4fFloat(matrix, lootEspValue, y, amountForDouble, lootEspValue + lootEspValue4, y, amountForDouble, playerValue, z22);
      VertexBuilderUtils.handleForMatrix4fFloat(matrix, lootEspValue, y, amountForDouble, lootEspValue, y + lootEspValue5, amountForDouble, playerValue, z22);
      VertexBuilderUtils.handleForMatrix4fFloat(matrix, lootEspValue, y, amountForDouble, lootEspValue, y, amountForDouble + lootEspValue6, playerValue, z22);
   }

   @Override
   public void onRender2D(TickProgress tickProgress) {
      if (this.booleanBase.isEnabled() && !this.labelDropItems.isEmpty() && mc.world != null && mc.player != null) {
         if (mc.currentScreen == null) {
            DrawContext drawContext = tickProgress.drawContext;
            long nanoTime = System.nanoTime();
            float lootEspValue = this.lastNanoTime == 0L ? 0.016F : Math.min(0.1F, (float)(nanoTime - this.lastNanoTime) / 1.0E9F);
            this.lastNanoTime = nanoTime;
            var arrayList = new ArrayList();

            for (LootESP.Mark mark : this.labelDropItems) {
               LootESP.Label label = null;
               if (this.groupStacks.isEnabled()) {
                  for (LootESP.Label label2 : (Iterable<com.corz.client.categories.visuals.LootESP.Label>) (arrayList)) {
                     if (label2.itemStack.getItem() == mark.stack.getItem() && label2.labelColor == mark.color) {
                        double lootEspFactor = label2.labelAmount - mark.x;
                        double lootEspFactor2 = label2.posZ - mark.z;
                        double spotValue = label2.matricesAmount - mark.y;
                        if (lootEspFactor * lootEspFactor + lootEspFactor2 * lootEspFactor2 <= 2.25 && Math.abs(spotValue) <= 1.25) {
                           label = label2;
                           break;
                        }
                     }
                  }
               }

               if (label == null) {
                  label = new LootESP.Label();
                  label.itemStack = mark.stack;
                  label.labelColor = mark.color;
                  label.labelAmount = mark.x;
                  label.matricesAmount = mark.y;
                  label.posZ = mark.z;
                  label.distance = mark.dist;
                  label.matricesValue = 0;
                  label.entityId = mark.entity.getId();
                  label.fadeAlpha = mark.fade;
                  arrayList.add(label);
               }

               label.matricesValue = label.matricesValue + mark.stack.getCount();
               if (mark.y > label.matricesAmount) {
                  label.matricesAmount = mark.y;
               }

               if (mark.dist < label.distance) {
                  label.distance = mark.dist;
               }

               if (mark.entity.getId() < label.entityId) {
                  label.entityId = mark.entity.getId();
               }

               if (mark.fade > label.fadeAlpha) {
                  label.fadeAlpha = mark.fade;
               }
            }

            arrayList.sort((obj, obj2) -> Double.compare(((com.corz.client.categories.visuals.LootESP.Label) obj2).distance, ((com.corz.client.categories.visuals.LootESP.Label) obj).distance));
            var hashMap = new HashMap();

            for (LootESP.Label label3 : (Iterable<com.corz.client.categories.visuals.LootESP.Label>) (arrayList)) {
               float orDefault = this.mergeTracersMap.getOrDefault(label3.entityId, 0.0F);
               hashMap.put(label3.entityId, Math.min(1.0F, orDefault + lootEspValue / 0.16F));
            }

            this.mergeTracersMap.clear();
            this.mergeTracersMap.putAll(hashMap);
            MatrixStack matrixStack = new MatrixStack();
            CorzClient.fontManager.setMatrixStack(matrixStack);
            MatrixPeekUtilsRender.handleMatrixStack(matrixStack);
            double scaleFactor = mc.getWindow().getScaleFactor();
            int calculateScaleFactor = mc.getWindow().calculateScaleFactor(ShowHUD.getMax(mc), mc.forcesUnicodeFont());
            float lootEspDivisor = (float)(calculateScaleFactor / scaleFactor);
            boolean lootEspFlag = Math.abs(lootEspDivisor - 1.0F) > 1.0E-4F;
            if (lootEspFlag) {
               CorzRoundedUtils.scaledWidth = Math.round(mc.getWindow().getScaledWidth() / lootEspDivisor);
               CorzRoundedUtils.scaledHeight = Math.round(mc.getWindow().getScaledHeight() / lootEspDivisor);
            }

            VertexTexturePeek regular = VertexTexturePeek.getRegular();
            VertexTexturePeek vertexTexturePeek = regular.isCorzclientFlag() ? regular : VertexTexturePeek.getSuisse();

            try {
               if (!vertexTexturePeek.isCorzclientFlag()) {
                  return;
               }

               for (LootESP.Label label4 : (Iterable<com.corz.client.categories.visuals.LootESP.Label>) (arrayList)) {
                  this.renderMatricesAmount(drawContext, vertexTexturePeek, label4, lootEspDivisor, this.mergeTracersMap.getOrDefault(label4.entityId, 1.0F));
               }
            } finally {
               if (lootEspFlag) {
                  CorzRoundedUtils.scaledWidth = 0.0F;
                  CorzRoundedUtils.scaledHeight = 0.0F;
               }
            }
         }
      }
   }

   public LootESP() {
      super("LootESP", IconsPngType.VISUAL);
      this.lineWidth = new IntegerSetting("Line Width", "Box line width (px)", 1, 1, 4);
      this.boxAlpha = new IntegerSetting("Box Alpha", "Box fill opacity (%)", 10, 0, 60, 5);
      this.integerHelper = new IntegerSetting("Max Distance", "Only mark drops within this many blocks (they fade out near the limit)", 64, 16, 128, 8);
      this.onlyValuable = new BooleanSetting("Only Valuable", "Only mark items in the Valuable Items list", false);
      this.itemList = new ItemList(
         "Valuable Items",
         "Items that count as valuable: drawn in gold, and the only ones shown while Only Valuable is on",
         List.of(
            Items.DIAMOND_SWORD,
            Items.DIAMOND_PICKAXE,
            Items.DIAMOND_AXE,
            Items.DIAMOND_HELMET,
            Items.DIAMOND_CHESTPLATE,
            Items.DIAMOND_LEGGINGS,
            Items.DIAMOND_BOOTS,
            Items.NETHERITE_SWORD,
            Items.NETHERITE_PICKAXE,
            Items.NETHERITE_AXE,
            Items.NETHERITE_HELMET,
            Items.NETHERITE_CHESTPLATE,
            Items.NETHERITE_LEGGINGS,
            Items.NETHERITE_BOOTS,
            Items.MACE,
            Items.TRIDENT,
            Items.TOTEM_OF_UNDYING,
            Items.ELYTRA,
            Items.ENCHANTED_GOLDEN_APPLE
         )
      );
      this.booleanBase = new BooleanSetting("Show Name", "Label above the drop: icon, name and count", true);
      this.showDistance = new BooleanSetting("Show Distance", "Distance in blocks on the label", false);
      this.groupStacks = new BooleanSetting("Group Stacks", "Merge nearby drops of the same item into one label", true);
      this.tracers = new BooleanSetting("Tracers", "Line from the crosshair to each drop", false);
      this.labelDropItems = new ArrayList<>();
      this.mergeTracersMap = new HashMap<>();
      this.lastNanoTime = 0L;
      this.addSettings(
         new ConfigSetting[]{
            this.booleanBase,
            this.showDistance,
            this.groupStacks,
            this.boxes,
            this.enumHelper,
            this.lineWidth,
            this.boxAlpha,
            this.integerHelper,
            this.onlyValuable,
            this.itemList,
            this.tracers
         }
      );
   }

   private int getRarityUncommon(ItemStack stack) {
      if (this.canFlag(stack)) {
         return -667076;
      } else {
         Rarity rarity = stack.getRarity();
         if (rarity == Rarity.UNCOMMON) {
            return -8090;
         } else if (rarity == Rarity.RARE) {
            return -10690049;
         } else {
            return rarity == Rarity.EPIC ? -2519809 : -1;
         }
      }
   }

   private static final class Label {
      double distance;
      int labelColor;
      int matricesValue;
      double matricesAmount;
      double labelAmount;
      ItemStack itemStack;
      int entityId;
      double posZ;
      float fadeAlpha;
   }

   private record Mark(ItemEntity entity, ItemStack stack, double x, double y, double z, double dist, int color, float fade) {
   }
}
