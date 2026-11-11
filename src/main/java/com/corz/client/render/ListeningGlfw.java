package com.corz.client.render;

import com.corz.client.CorzClient;
import com.corz.client.config.ModuleConfigAutosave;
import com.corz.client.config.KeybindSetting;
import com.corz.client.gui.MatrixPeekUtilsRender;
import java.awt.Color;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;
import com.corz.client.utils.GlfwListeningApi;
import com.corz.client.utils.PageRaltUtils;
import com.corz.client.utils.QuintBounceType;
import com.corz.client.utils.QuintScissor;

public class ListeningGlfw {
   private final QuintScissor releaseAnimation = new QuintScissor(QuintBounceType.CUBIC_OUT, 180, 0.0);
   private int listeningValue;
   private int boxHeight;
   private int listeningValue3Listening;
   private final GlfwListeningApi apiGlfwListeningApi;
   private final KeybindSetting next;
   private int listeningValue2Listening;

   private int getValue(int listeningGlfwValue, int listeningGlfwValue2, double listeningGlfwFactor) {
      listeningGlfwFactor = Math.max(0.0, Math.min(1.0, listeningGlfwFactor));
      int listeningGlfwValue3 = listeningGlfwValue >> 24 & 0xFF;
      int listeningGlfwValue4 = listeningGlfwValue >> 16 & 0xFF;
      int listeningGlfwValue5 = listeningGlfwValue >> 8 & 0xFF;
      int listeningGlfwValue6 = listeningGlfwValue & 0xFF;
      int listeningGlfwValue7 = listeningGlfwValue2 >> 24 & 0xFF;
      int listeningGlfwValue8 = listeningGlfwValue2 >> 16 & 0xFF;
      int listeningGlfwValue9 = listeningGlfwValue2 >> 8 & 0xFF;
      int listeningGlfwValue10 = listeningGlfwValue2 & 0xFF;
      return (int)(listeningGlfwValue3 + (listeningGlfwValue7 - listeningGlfwValue3) * listeningGlfwFactor) << 24
         | (int)(listeningGlfwValue4 + (listeningGlfwValue8 - listeningGlfwValue4) * listeningGlfwFactor) << 16
         | (int)(listeningGlfwValue5 + (listeningGlfwValue9 - listeningGlfwValue5) * listeningGlfwFactor) << 8
         | (int)(listeningGlfwValue6 + (listeningGlfwValue10 - listeningGlfwValue6) * listeningGlfwFactor);
   }

   private int[] getListeningRenderer() {
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      int listeningRendererValue = Math.max((int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(this.formatGlfw()), (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont("Listening..."));
      int min2 = Math.min(this.listeningValue3Listening - 8, Math.max(56, listeningRendererValue + 18));
      int listeningRendererValue3 = this.listeningValue + this.listeningValue3Listening - 4 - min2;
      int listeningRendererValue4 = this.listeningValue2Listening + this.boxHeight / 2 - 9;
      return new int[]{listeningRendererValue3, listeningRendererValue4, min2, 18};
   }

   public void handle(int listeningGlfwValue) {
      this.next.setValue2(PageRaltUtils.getValueForInt(listeningGlfwValue));
      ModuleConfigAutosave.updateFlag2();
   }

   public void handleListening(int listeningValue, int y, int width, int value) {
      this.listeningValue = listeningValue;
      this.listeningValue2Listening = y;
      this.listeningValue3Listening = width;
      this.boxHeight = value;
   }

   private static String formatText() {
      int listeningGlfwValue = (int)(System.currentTimeMillis() / 400L % 4L);
      return "Listening" + ".".repeat(listeningGlfwValue);
   }

   private String formatText(boolean listeningGlfwFlag) {
      return listeningGlfwFlag ? formatText() : this.formatGlfw();
   }

   public ListeningGlfw(KeybindSetting next, int listeningValue, int listeningValue2, int listeningValue3, int value, GlfwListeningApi api) {
      this.next = next;
      this.apiGlfwListeningApi = api;
      this.listeningValue = listeningValue;
      this.listeningValue2Listening = listeningValue2;
      this.listeningValue3Listening = listeningValue3;
      this.boxHeight = value;
   }

   public boolean canApi(double apiLimit, double mouseY, int apiValue) {
      if (apiValue != 0) {
         return false;
      } else {
         int[] listeningRenderer = this.getListeningRenderer();
         if (apiLimit >= listeningRenderer[0] && apiLimit <= listeningRenderer[0] + listeningRenderer[2] && mouseY >= listeningRenderer[1] && mouseY <= listeningRenderer[1] + listeningRenderer[3]) {
            this.apiGlfwListeningApi.handleListeningGlfw(this);
            return true;
         } else {
            return false;
         }
      }
   }

   public void handleForInt(int listeningGlfwValue) {
      this.next.setValue2(PageRaltUtils.canFlagForInt(listeningGlfwValue) ? -1 : listeningGlfwValue);
      ModuleConfigAutosave.updateFlag2();
   }

   public void renderListeningRelease(DrawContext context, int listeningReleaseLimit, int mouseY) {
      VertexTextureFont fontRenderer = CorzClient.fontRenderer();
      int listeningReleaseValue = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown();
      fontRenderer.handleForStringFloat(
         this.next.getSettingName(), this.listeningValue + 4, fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown(this.listeningValue2Listening, this.boxHeight), new Color(-3947581)
      );
      boolean glfwListening = this.apiGlfwListeningApi.canGlfwListening(this);
      int[] listeningRenderer = this.getListeningRenderer();
      int matrixPeekValue = listeningRenderer[0];
      int brownValue = listeningRenderer[1];
      int listeningReleaseValue4 = listeningRenderer[2];
      int materialFactor = listeningRenderer[3];
      boolean listeningReleaseFlag = listeningReleaseLimit >= matrixPeekValue && listeningReleaseLimit <= matrixPeekValue + listeningReleaseValue4 && mouseY >= brownValue && mouseY <= brownValue + materialFactor;
      if (Math.abs(this.releaseAnimation.getTargetAmount() - (listeningReleaseFlag ? 1 : 0)) > 0.01) {
         this.releaseAnimation.handleAmountTime(listeningReleaseFlag ? 1.0 : 0.0);
      }

      double listeningGlfwFactor = this.releaseAnimation.getAmount();
      double releaseRightFactor = glfwListening ? 1.0 : listeningGlfwFactor;
      int matrixStackValue = this.getValue(-15461350, -14737624, listeningGlfwFactor);
      int releaseRight = MatrixPeekUtilsRender.getReleaseRight(releaseRightFactor);
      MatrixPeekUtilsRender.render(context, matrixPeekValue, brownValue, listeningReleaseValue4, materialFactor, 5, matrixStackValue, releaseRight);
      String formatText = this.formatText(glfwListening);
      int listeningReleaseValue9 = glfwListening ? MatrixEspUtils.releaseRightValue : this.getValue(-1, MatrixEspUtils.releaseRightValue, listeningGlfwFactor * 0.8);
      int listeningReleaseValue10 = (int)fontRenderer.getAbcdefghijklmnopqrstuvwxyzCorzfont(formatText);
      fontRenderer.handleForStringFloat(formatText, matrixPeekValue + (listeningReleaseValue4 - listeningReleaseValue10) / 2, fontRenderer.getAbcdefghijklmnopqrstuvwxyzBrown(brownValue, materialFactor), new Color(listeningReleaseValue9, true));
   }

   public int getValue() {
      return this.boxHeight;
   }

   private String formatGlfw() {
      int glfwValue = this.next.getKeyCode();
      String formatNone = PageRaltUtils.formatNone(glfwValue);
      if (formatNone != null) {
         return formatNone;
      } else {
         String glfwGetKeyName = GLFW.glfwGetKeyName(glfwValue, 0);
         return glfwGetKeyName != null ? glfwGetKeyName.toUpperCase() : PageRaltUtils.formatText(glfwValue);
      }
   }
}
