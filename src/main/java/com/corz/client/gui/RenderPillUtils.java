package com.corz.client.gui;

import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.MatrixAmountUtils;
import com.corz.client.render.MatrixEspUtils;
import net.minecraft.client.gui.DrawContext;

public final class RenderPillUtils {

   public static int getValue() {
      return 22;
   }

   private RenderPillUtils() {
   }

   public static int getValue(int pillValue, double releaseRightFactor) {
      return MatrixAmountUtils.getValue(pillValue, releaseRightFactor, -7629919, -854534, -14473682);
   }

   public static void render(DrawContext context, int pillValue, int keybindValue, int pillValue3, int pillValue4, double pillLimit, double releaseRightLimit, double pillValue7) {
      renderReleaseRight(context, pillValue - pillValue4 - 22, keybindValue + (pillValue3 - 10) / 2, pillLimit, releaseRightLimit, pillValue7);
   }

   public static int getValueInt() {
      return 10;
   }

   private static double getAmount(double pillLimit) {
      return pillLimit < 0.0 ? 0.0 : (pillLimit > 1.0 ? 1.0 : pillLimit);
   }

   public static void render(DrawContext context, int pillValue, int matrixStackValue, int pillValue3, int pillValue4, double pillLimit, double releaseRightLimit) {
      render(context, pillValue, matrixStackValue, pillValue3, pillValue4, pillLimit, releaseRightLimit, 1.0);
   }

   public static void render(DrawContext context, int pillValue, int editValue, double pillLimit, double cardMatrixFactor) {
      renderReleaseRight(context, pillValue, editValue, pillLimit, cardMatrixFactor, 1.0);
   }

   public static boolean canFlag(double pillLimit, double betaListeningLimit, int pillValue, int keybindValue, int pillValue3, int pillValue4, int pillValue5) {
      int pillValue6 = pillValue - pillValue4 - 22;
      int pillValue7 = keybindValue + (pillValue3 - 10) / 2;
      return pillLimit >= pillValue6 - pillValue5 && pillLimit <= pillValue6 + 22 + pillValue5 && betaListeningLimit >= pillValue7 - pillValue5 && betaListeningLimit <= pillValue7 + 10 + pillValue5;
   }

   public static void renderReleaseRight(DrawContext context, int releaseRightValue, int editValue, double pillLimit, double cardMatrixFactor, double releaseRightValue5) {
      float matrixLimit = (float)getAmount(releaseRightValue5);
      if (!(matrixLimit <= 0.004F)) {
         double releaseRightFactor = getAmount(pillLimit);
         double releaseRightLimit = getAmount(cardMatrixFactor);
         int releaseRightValue7 = MatrixEspUtils.releaseRightValue;
         int cardMatrixValue = CardMatrixUtils.getValue(-14012614, releaseRightValue7, releaseRightFactor);
         if (releaseRightLimit > 0.004) {
            cardMatrixValue = CardMatrixUtils.getValue(cardMatrixValue, -1, 0.1 * releaseRightLimit);
         }

         MatrixPeekUtilsRender.renderMatrixStack(context, releaseRightValue, editValue, 22, 10, 5, MatrixAmountUtils.getValue(cardMatrixValue, matrixLimit));
         byte releaseRightFactor2 = 12;
         float releaseRightValue9 = releaseRightValue + 5 + (float)(releaseRightFactor2 * releaseRightFactor);
         float ctxOffset = editValue + 5.0F;
         int matrixValue = getValue(releaseRightValue7, releaseRightFactor);
         float corzLicenseLimit = 4.0F;
         float max3 = MatrixAmountUtils.getMax((MatrixAmountUtils.getAmountForInt(releaseRightValue7) - 0.62F) / 0.08F);
         float matrixFactor = (float)Math.min(1.0, Math.max(0.0, (releaseRightFactor - 0.5) * 2.0)) * max3;
         if (matrixFactor > 0.004F) {
            MatrixPeekUtilsRender.renderMatrixPeek(
               context,
               releaseRightValue9 - corzLicenseLimit,
               ctxOffset - corzLicenseLimit,
               corzLicenseLimit * 2.0F,
               corzLicenseLimit * 2.0F,
               corzLicenseLimit,
               MatrixAmountUtils.getValue(MatrixAmountUtils.getValue(matrixValue, 1509949440, matrixFactor), matrixLimit)
            );
            MatrixPeekUtilsRender.renderMatrixPeek(
               context,
               releaseRightValue9 - corzLicenseLimit + 1.0F,
               ctxOffset - corzLicenseLimit + 1.0F,
               (corzLicenseLimit - 1.0F) * 2.0F,
               (corzLicenseLimit - 1.0F) * 2.0F,
               corzLicenseLimit - 1.0F,
               MatrixAmountUtils.getValue(matrixValue, matrixLimit)
            );
         } else {
            MatrixPeekUtilsRender.renderMatrixPeek(
               context, releaseRightValue9 - corzLicenseLimit, ctxOffset - corzLicenseLimit, corzLicenseLimit * 2.0F, corzLicenseLimit * 2.0F, corzLicenseLimit, MatrixAmountUtils.getValue(matrixValue, matrixLimit)
            );
         }
      }
   }
}
