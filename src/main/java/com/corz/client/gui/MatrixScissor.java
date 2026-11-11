package com.corz.client.gui;

import com.corz.client.inventory.DrawMatricesUtils;
import com.corz.client.inventory.StackRegistryUtils;
import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.MatrixClearUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.MatrixIdentifierUtils;
import com.corz.client.render.MatrixIdentifierUtils.Spring;
import com.corz.client.render.MatrixStackUtilsAmount;
import com.corz.client.render.MatrixStackUtilsAmount.Anims;
import com.corz.client.render.MatrixStackUtilsAmount.Entrance;
import com.corz.client.render.RenderMatrixUtilsRelease;
import com.corz.client.utils.ConstAmountUtils.RailGeom;
import com.corz.client.utils.ScissorTgtHandler.Tone;
import java.util.List;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import org.joml.Matrix3x2fStack;
import com.corz.client.categories.settings.ClientSounds;
import ai.auth.Module;
import com.corz.client.utils.ConstAmountUtils;
import com.corz.client.utils.CpxVillagerData;
import com.corz.client.utils.MinecraftBotUtils;
import com.corz.client.utils.ScissorTgtHandler;
import com.corz.client.utils.ScissorTgtUtils;

public final class MatrixScissor {
   private int lastLookIndex;
   private String[] matrixScissorTexts;
   private ItemStack[] itemStacks;
   private final Anims dangerAnims;
   private float[] scissorTable;
   private double targetScroll;
   private String[] lookTexts;
   private float scissorAmount;
   private float matrixScissorAmount;
   private String[] dangerTexts;
   private int scissorValue;
   private float[] dangerTable;
   private float[] matrixScissorTable;
   private float viewportHeight;
   private float shrinkScale;
   private boolean matrixScissorActive;
   private RailGeom scissorRailGeom;
   private boolean[] matrixScissorFlags;
   private final Spring matrixScissorSpring;
   private float[] lookTable;
   private int lookValue;
   private long lastNanoTime;
   private MinecraftBotUtils.Rail scissorRail = new MinecraftBotUtils.Rail(List.of(), 0, 0);
   private static final String SOON_SUFFIX = " \u00b7 Soon";
   private double animatedScroll;

   public void renderAmountScissor(DrawContext context, float amount, float hashRegistryValue, float betaListeningValue, int scissorValue, int matrixScissorValue, int value, Entrance entrance, int scissorValue3, boolean scissorFlag) {
      this.matrixScissorAmount = amount;
      this.scissorAmount = hashRegistryValue;
      this.viewportHeight = betaListeningValue;
      this.scissorValue = value;
      double matrixIdentifierFactor = MatrixClearUtils.getAmount();
      int slots = this.scissorRail.getSlots();
      this.targetScroll = ConstAmountUtils.getAmountForDouble(this.targetScroll, this.scissorRailGeom.getRailGeomValue(), betaListeningValue);
      this.animatedScroll = MatrixIdentifierUtils.getAmount(this.animatedScroll, this.targetScroll, 18.0, matrixIdentifierFactor);
      if (Math.abs(this.animatedScroll - this.targetScroll) < 0.05) {
         this.animatedScroll = this.targetScroll;
      }

      int value2 = scissorFlag ? this.getAmountMax(scissorValue, matrixScissorValue) : -1;
      if (value2 != this.lookValue) {
         this.lookValue = value2;
         this.lastNanoTime = System.nanoTime();
      }

      if (this.lookValue >= 0 && this.lookValue != this.lastLookIndex) {
         ClientSounds.updateScrollHover();
      }

      this.lastLookIndex = this.lookValue;
      if (slots != 0) {
         boolean maxAmount = this.isMaxAmount();
         if (maxAmount) {
            context.enableScissor((int)Math.floor(amount), (int)Math.floor(hashRegistryValue), (int)Math.ceil(amount + 32.0F), (int)Math.ceil(hashRegistryValue + betaListeningValue));
         }

         try {
            for (int i = 0; i < slots; i++) {
               boolean scissorFlag2 = i == value;
               this.scissorTable[i] = getAmount(this.scissorTable[i], i == this.lookValue && !scissorFlag2 ? 1.0F : 0.0F, i == this.lookValue ? 16.0 : 11.0, matrixIdentifierFactor);
               this.dangerTable[i] = getAmount(this.dangerTable[i], scissorFlag2 ? 1.0F : 0.0F, 14.0, matrixIdentifierFactor);
            }

            this.renderAmountMax(context, value, entrance, scissorValue3, matrixIdentifierFactor);

            for (int j = 0; j < slots; j++) {
               this.renderAmountsAmount(context, j, entrance.getAmountFlag(scissorValue3 + j), matrixIdentifierFactor);
            }

            this.renderAmountMax(context, entrance, scissorValue3);
         } finally {
            if (maxAmount) {
               context.disableScissor();
            }
         }
      }
   }

   public void handleTextsAmounts(MinecraftBotUtils.Rail rail) {
      this.scissorRail = rail == null ? new MinecraftBotUtils.Rail(List.of(), 0, 0) : rail;
      this.scissorRailGeom = ConstAmountUtils.getMaxForInt(this.scissorRail.pvp(), this.scissorRail.util());
      int slots = this.scissorRail.getSlots();
      this.itemStacks = new ItemStack[slots];
      this.matrixScissorTexts = new String[slots];
      this.dangerTexts = new String[slots];
      this.lookTexts = new String[slots];
      this.scissorTable = new float[slots];
      this.dangerTable = new float[slots];
      this.matrixScissorTable = new float[slots];
      this.lookTable = new float[slots];
      this.matrixScissorFlags = new boolean[slots];

      for (int i = 0; i < slots; i++) {
         MinecraftBotUtils.Slot slots2 = this.scissorRail.getSlots(i);
         this.itemStacks[i] = getEmpty(slots2.look());
         this.matrixScissorTexts[i] = "slot:" + slots2.formatLook();
         this.dangerTexts[i] = "pip:" + slots2.formatLook();
         Module module = slots2.module();
         this.matrixScissorFlags[i] = module != null && module.isEnabled();
         this.matrixScissorTable[i] = this.matrixScissorFlags[i] ? 1.0F : 0.0F;
      }

      this.lookValue = -1;
      this.lastLookIndex = -1;
      this.matrixScissorActive = false;
      this.targetScroll = 0.0;
      this.animatedScroll = this.targetScroll;
   }

   public void renderTextsLook(DrawContext context, float lookValue, boolean lookFlag) {
      int matrixScissorLimit = this.lookValue;
      if (lookFlag && this.canFlag(matrixScissorLimit)) {
         float lookValue2 = (float)(System.nanoTime() - this.lastNanoTime) / 1000000.0F - 150.0F;
         if (!(lookValue2 <= 0.0F)) {
            float amountForFloat = MatrixStackUtilsAmount.getAmountForFloat(lookValue2 / 120.0F);
            MinecraftBotUtils.Slot slots = this.scissorRail.getSlots(matrixScissorLimit);
            boolean module = slots.isModule();
            if (this.lookTexts[matrixScissorLimit] == null) {
               String lookText = module ? (slots.look().name() == null ? slots.formatLook() : slots.look().name()) : slots.module().getCategory();
               float lookValue4 = module ? MatrixStackUtilsAmount.getAmount3(" \u00b7 Soon") : 0.0F;
               this.lookTexts[matrixScissorLimit] = MatrixStackUtilsAmount.getFontRenderer().formatText(lookText, 140.0F - lookValue4);
               this.lookTable[matrixScissorLimit] = ConstAmountUtils.getMin(MatrixStackUtilsAmount.getAmount3(this.lookTexts[matrixScissorLimit]) + lookValue4);
            }

            float lookValue5 = this.lookTable[matrixScissorLimit];
            float materialFactor = 16.0F;
            float matrixPeekValue = lookValue + ConstAmountUtils.getAmount() - 4.0F * (1.0F - amountForFloat);
            float amountMax = this.getAmountMax();
            float panelOverlay = CorzRoundedUtils.getPanelOverlay(amountForFloat);

            try {
               MatrixPeekUtilsRender.renderMatrixPeekForDrawContextFloat(context, matrixPeekValue - 2.0F, amountMax, lookValue5 + 4.0F, materialFactor + 3.0F, 6.0F, -1946157056, 6.0F, 0.5F);
               MatrixPeekUtilsRender.renderForDrawContextFloat(context, matrixPeekValue, amountMax, lookValue5, materialFactor, 5.0F, -65990120, 385875967);
               float abcdefghijklmnopqrstuvwxyzBrown = MatrixStackUtilsAmount.getAmount(amountMax, materialFactor, 1.0F);
               float matrixStackValue = matrixPeekValue + 7.0F;
               MatrixStackUtilsAmount.handle(this.lookTexts[matrixScissorLimit], matrixStackValue, abcdefghijklmnopqrstuvwxyzBrown, -920587);
               if (module) {
                  MatrixStackUtilsAmount.handle(" \u00b7 Soon", matrixStackValue + MatrixStackUtilsAmount.getAmount3(this.lookTexts[matrixScissorLimit]), abcdefghijklmnopqrstuvwxyzBrown, -7630437);
               }
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            }
         }
      }
   }

   public void handleAmountsMax(int matrixScissorLimit) {
      if (matrixScissorLimit >= 0 && matrixScissorLimit < this.scissorRailGeom.getValue()) {
         this.matrixScissorSpring.handleAmount(this.scissorRailGeom.getValue(matrixScissorLimit));
         this.matrixScissorActive = true;

         for (int i = 0; i < this.dangerTable.length; i++) {
            this.dangerTable[i] = i == matrixScissorLimit ? 1.0F : 0.0F;
         }

         this.shrinkScale = 1.0F;
      }
   }

   public int getLookValue() {
      return this.lookValue;
   }

   public MatrixScissor() {
      this.scissorRailGeom = ConstAmountUtils.getMaxForInt(0, 0);
      this.itemStacks = new ItemStack[0];
      this.matrixScissorTexts = new String[0];
      this.dangerTexts = new String[0];
      this.lookTexts = new String[0];
      this.scissorTable = new float[0];
      this.dangerTable = new float[0];
      this.matrixScissorTable = new float[0];
      this.lookTable = new float[0];
      this.matrixScissorFlags = new boolean[0];
      this.dangerAnims = new Anims();
      this.matrixScissorSpring = new Spring(0.0);
      this.shrinkScale = 1.0F;
      this.lookValue = -1;
      this.lastLookIndex = -1;
      this.scissorValue = -1;
   }

   public void handleAmountMax(int matrixScissorValue) {
      if (!(this.viewportHeight <= 0.0F)) {
         this.targetScroll = ConstAmountUtils.getAmount(this.scissorRailGeom, matrixScissorValue, this.targetScroll, this.viewportHeight);
      }
   }

   private boolean canFlag(int matrixScissorLimit) {
      return matrixScissorLimit >= 0 && matrixScissorLimit < this.scissorRail.getSlots() && matrixScissorLimit != this.scissorValue;
   }

   private void renderMatrixTranslate(DrawContext context, int index, float matrixTranslateValue, float matrixTranslateValue2, boolean matrixTranslateFlag) {
      ItemStack itemStack = this.itemStacks[index];
      if (itemStack != null && !itemStack.isEmpty()) {
         Matrix3x2fStack matrices = context.getMatrices();
         matrices.pushMatrix();

         try {
            if (matrixTranslateFlag) {
               matrices.translate(matrixTranslateValue + 6.0F, matrixTranslateValue2 + 6.0F);
               matrices.scale(0.75F, 0.75F);
            } else {
               matrices.translate(matrixTranslateValue + 4.0F, matrixTranslateValue2 + 4.0F);
            }

            DrawMatricesUtils.render(context, itemStack, 0, 0);
         } finally {
            matrices.popMatrix();
         }
      }
   }

   public boolean canAmount(double matrixScissorLimit, double matrixScissorValue) {
      return matrixScissorLimit >= this.matrixScissorAmount && matrixScissorLimit < this.matrixScissorAmount + 32.0F && matrixScissorValue >= this.scissorAmount && matrixScissorValue < this.scissorAmount + this.viewportHeight;
   }

   public void handleTexts(int index) {
      if (index >= 0 && index < this.matrixScissorTexts.length) {
         this.dangerAnims.handleTimeForString(this.matrixScissorTexts[index]);
      }
   }

   public boolean isTime() {
      return this.canFlag(this.lookValue) && (float)(System.nanoTime() - this.lastNanoTime) / 1000000.0F > 150.0F;
   }

   public void update() {
      this.lookValue = -1;
      this.lastLookIndex = -1;
   }

   private void renderAmountsAmount(DrawContext context, int index, Entrance entrance, double matrixScissorValue) {
      MinecraftBotUtils.Slot slots = this.scissorRail.getSlots(index);
      Module module = slots.module();
      boolean matrixTranslateFlag = slots.isModule();
      boolean matrixScissorFlag = module != null && module.isEnabled();
      if (matrixScissorFlag && !this.matrixScissorFlags[index]) {
         this.dangerAnims.handleTime(this.dangerTexts[index]);
      }

      this.matrixScissorFlags[index] = matrixScissorFlag;
      this.matrixScissorTable[index] = getAmount(this.matrixScissorTable[index], matrixScissorFlag ? 1.0F : 0.0F, matrixScissorFlag ? 16.0 : 25.0, matrixScissorValue);
      float matrixPeekValue = this.matrixScissorAmount + 4.0F;
      float matrixScissorLimit = (float)(this.scissorAmount + this.scissorRailGeom.getValue(index) - this.animatedScroll);
      if (!(matrixScissorLimit > this.scissorAmount + this.viewportHeight) && !(matrixScissorLimit + 24.0F < this.scissorAmount)) {
         if (!(entrance.entranceProgress <= 0.002F)) {
            float matrixScissorValue3 = 12.0F;
            float cardMatrixFactor = this.scissorTable[index] * (matrixTranslateFlag ? 0.6F : 1.0F);
            boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, matrixPeekValue + matrixScissorValue3, matrixScissorLimit + matrixScissorValue3, entrance.matrixScissorAmount * this.dangerAnims.getTime(this.matrixScissorTexts[index], 0.07F));
            float panelOverlay = CorzRoundedUtils.getPanelOverlay(entrance.entranceProgress);

            try {
               if (!matrixTranslateFlag && this.dangerTable[index] < 0.999F) {
                  MatrixPeekUtilsRender.renderMatrixPeek(
                     context, matrixPeekValue, matrixScissorLimit, 24.0F, 24.0F, 5.0F, MatrixStackUtilsAmount.getValue(-1, (float)(0.035 * (1.0F - this.dangerTable[index])))
                  );
               }

               RenderMatrixUtilsRelease.renderForDrawContextFloatDouble(context, matrixPeekValue, matrixScissorLimit, 24.0F, 24.0F, 5.0F, cardMatrixFactor);
               if (this.matrixScissorTable[index] > 0.01F && module != null) {
                  this.renderAmountsDanger(context, index, module, matrixPeekValue, matrixScissorLimit, cardMatrixFactor);
               }

               float matrixScissorValue5 = matrixTranslateFlag ? 0.0F : 1.0F * this.scissorTable[index];
               this.renderMatrixTranslate(context, index, matrixPeekValue, matrixScissorLimit - matrixScissorValue5, matrixTranslateFlag);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay);
               MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
            }
         }
      }
   }

   public MinecraftBotUtils.Rail getScissorRail() {
      return this.scissorRail;
   }

   public RailGeom getScissorRailGeom() {
      return this.scissorRailGeom;
   }

   public float getTime() {
      return !this.isTime() ? 0.0F : MatrixStackUtilsAmount.getAmountForFloat(((float)(System.nanoTime() - this.lastNanoTime) / 1000000.0F - 150.0F) / 120.0F);
   }

   private void renderAmountMax(DrawContext context, int index, Entrance entrance, int matrixScissorValue, double matrixIdentifierFactor) {
      if (index >= 0 && index < this.scissorRailGeom.getValue()) {
         double springValue = this.scissorRailGeom.getValue(index);
         if (!this.matrixScissorActive) {
            this.matrixScissorSpring.handleAmount(springValue);
            this.matrixScissorActive = true;
         } else if (this.matrixScissorSpring.springPosition != springValue || this.matrixScissorSpring.anywayEnableCount != 0.0) {
            int matrixScissorDivisor = Math.max(1, (int)Math.ceil(matrixIdentifierFactor / 0.004166666666666667));
            double matrixScissorValue4 = matrixIdentifierFactor / matrixScissorDivisor;

            for (int i = 0; i < matrixScissorDivisor; i++) {
               this.matrixScissorSpring.handleAmount(springValue, 784.0, 42.0, matrixScissorValue4);
            }

            if (Math.abs(this.matrixScissorSpring.springPosition - springValue) < 0.04 && Math.abs(this.matrixScissorSpring.anywayEnableCount) < 1.5) {
               this.matrixScissorSpring.handleAmount(springValue);
            }
         }

         float max5 = MatrixStackUtilsAmount.getMax((float)Math.abs(this.matrixScissorSpring.springPosition - springValue) / 27.0F);
         this.shrinkScale = getAmount(this.shrinkScale, 1.0F - 0.6F * max5, 22.0, matrixIdentifierFactor);
         Entrance amountFlag = entrance.getAmountFlag(matrixScissorValue + index);
         if (!(amountFlag.entranceProgress <= 0.002F)) {
            float releaseRightValue = this.matrixScissorAmount + 4.0F;
            float f = MatrixStackUtilsAmount.getAmount((float)(this.scissorAmount + this.matrixScissorSpring.springPosition - this.animatedScroll));
            float matrixScissorValue8 = 12.0F;
            boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(
               context, releaseRightValue + matrixScissorValue8, f + matrixScissorValue8, amountFlag.matrixScissorAmount * this.dangerAnims.getTime(this.matrixScissorTexts[index], 0.07F)
            );
            float panelOverlay = CorzRoundedUtils.getPanelOverlay(amountFlag.entranceProgress);

            try {
               RenderMatrixUtilsRelease.renderReleaseRight(context, releaseRightValue, f, 24.0F, 24.0F, 5.0F);
               float round = Math.round(12.0F * this.shrinkScale);
               RenderMatrixUtilsRelease.render(context, this.matrixScissorAmount + 1.25F, f + (24.0F - round) / 2.0F, round, 1.0);
            } finally {
               CorzRoundedUtils.handlePanelOverlay(panelOverlay);
               MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
            }
         }
      }
   }

   public boolean canAmountMax(double matrixScissorValue) {
      if (!this.isMaxAmount()) {
         return false;
      } else {
         this.targetScroll = ConstAmountUtils.getAmountForDouble(this.targetScroll - matrixScissorValue * 27.0, this.scissorRailGeom.getRailGeomValue(), this.viewportHeight);
         return true;
      }
   }

   private static ItemStack getEmpty(CpxVillagerData cpxVillagerData) {
      try {
         return StackRegistryUtils.getIconGlint(cpxVillagerData);
      } catch (RuntimeException e) {
         return ItemStack.EMPTY;
      }
   }

   private void renderAmountsDanger(DrawContext context, int index, Module Module, float dangerValue, float matrixScissorLimit, float cardMatrixFactor) {
      int dangerValue4 = -10761846;
      if (Module instanceof ScissorTgtHandler scissorTgtHandler && ScissorTgtUtils.getInstance(scissorTgtHandler).tone() == Tone.DANGER) {
         dangerValue4 = -1750963;
      }

      float x2 = this.matrixScissorTable[index] * this.dangerAnims.getTime(this.dangerTexts[index], 0.0F);
      float x = dangerValue + 19.0F + 2.0F;
      float y = matrixScissorLimit + 19.0F + 2.0F;
      boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(context, x, y, x2);

      try {
         if (!CardMatrixUtils.isModernFlag()) {
            int dangerValue8 = CardMatrixUtils.getValue(CardMatrixUtils.getValue4(), -1, 0.035 * (1.0F - this.dangerTable[index]));
            int cardMatrixValue = CardMatrixUtils.getValue(dangerValue8, -1, 0.045 * cardMatrixFactor);
            int matrixStackValue = CardMatrixUtils.getValue(
               cardMatrixValue, CardMatrixUtils.getValue(CardMatrixUtils.getValue9(), MatrixEspUtils.releaseRightValue, 0.08), this.dangerTable[index]
            );
            float dangerValue11 = 2.5F;
            MatrixPeekUtilsRender.renderMatrixPeek(context, x - dangerValue11, y - dangerValue11, 5.0F, 5.0F, dangerValue11, matrixStackValue);
         }

         float materialFactor = 4.0F;
         float corzLicenseLimit = materialFactor / 2.0F;
         float dangerValue14 = (float)((Math.sin(System.nanoTime() / 1.0E9 * Math.PI * 2.0 / 1.6) + 1.0) / 2.0);
         MatrixPeekUtilsRender.renderMatrixPeek(
            context, x - corzLicenseLimit, y - corzLicenseLimit, materialFactor, materialFactor, corzLicenseLimit, MatrixStackUtilsAmount.getValue(dangerValue4, 0.35F * dangerValue14 * this.matrixScissorTable[index]), 2.5F, 0.85F
         );
         MatrixPeekUtilsRender.renderMatrixPeek(context, x - corzLicenseLimit, y - corzLicenseLimit, materialFactor, materialFactor, corzLicenseLimit, dangerValue4);
      } finally {
         MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
      }
   }

   public float getAmountMax() {
      return this.lookValue >= 0 && this.lookValue < this.scissorRail.getSlots()
         ? MatrixStackUtilsAmount.getAmount(this.scissorAmount + ConstAmountUtils.getAmountForRailGeomIntDouble(this.scissorRailGeom, this.lookValue, this.animatedScroll))
         : Float.NaN;
   }

   public boolean isMaxAmount() {
      return this.scissorRailGeom.getRailGeomValue() > this.viewportHeight;
   }

   private void renderAmountMax(DrawContext context, Entrance entrance, int matrixScissorValue) {
      int matrixScissorLimit = this.scissorRailGeom.getStartIndex();
      if (matrixScissorLimit >= 0) {
         Entrance amountFlag = entrance.getAmountFlag(matrixScissorValue + this.scissorRailGeom.getMaxRailGeomBound());
         if (!(amountFlag.entranceProgress <= 0.002F)) {
            float rootDivisor = MatrixPeekUtilsRender.getAmount();
            float f2 = MatrixStackUtilsAmount.getAmount((float)(this.scissorAmount + matrixScissorLimit - this.animatedScroll));
            if (!(f2 < this.scissorAmount) && !(f2 > this.scissorAmount + this.viewportHeight)) {
               MatrixPeekUtilsRender.renderMatrixPeek(
                  context, this.matrixScissorAmount + 8.0F, f2, 16.0F, rootDivisor, MatrixStackUtilsAmount.getValue(CardMatrixUtils.getValueInt(), amountFlag.entranceProgress)
               );
            }
         }
      }
   }

   public int getAmountMax(double matrixScissorLimit, double matrixScissorValue) {
      return this.canAmount(matrixScissorLimit, matrixScissorValue) && !(matrixScissorLimit < this.matrixScissorAmount + 4.0F) && !(matrixScissorLimit >= this.matrixScissorAmount + 4.0F + 24.0F)
         ? this.scissorRailGeom.getValue(matrixScissorValue - this.scissorAmount, this.animatedScroll)
         : -1;
   }

   private static float getAmount(float matrixScissorValue, float thumbAmount, double ctlValue, double matrixIdentifierFactor) {
      float matrixScissorValue5 = (float)MatrixIdentifierUtils.getAmount(matrixScissorValue, thumbAmount, ctlValue, matrixIdentifierFactor);
      return Math.abs(matrixScissorValue5 - thumbAmount) < 0.002F ? thumbAmount : matrixScissorValue5;
   }

   public float getAmounts() {
      int lookValue3 = this.lookValue;
      return lookValue3 >= 0 && lookValue3 < this.lookTable.length ? this.lookTable[lookValue3] : 0.0F;
   }
}
