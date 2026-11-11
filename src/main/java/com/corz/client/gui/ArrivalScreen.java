package com.corz.client.gui;

import com.corz.client.CorzClient;
import com.corz.client.render.CardMatrixUtils;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.IcoGenUtils;
import com.corz.client.render.MatrixClearUtils;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.MatrixPeekUtilsIdentifier;
import com.corz.client.render.MatrixStackUtilsAmount;
import com.corz.client.render.VertexTextureFont;
import java.awt.Color;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.DisconnectedScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import com.corz.client.utils.BuyerAppendUtils;
import com.corz.client.utils.CardStatusUtilsFlag;
import com.corz.client.utils.TripStats;

public final class ArrivalScreen extends Screen implements GuiClickApi, ScreenGuiApi {
   private final int landedValue;
   private final Identifier[] identifiers;
   private final float[] translatePushTable;
   private float spaceWidth;
   private final int landedStoppedValue;
   private long lastNanoTime;
   private final Runnable serversServerAction;
   private VertexTextureFont vertexTexture;
   private final String[] arrivalScreenTexts;
   private final String flownLoggedText;
   private long lastNanoTimeWidthHeight;
   private int screenPhase;
   private static final String[] cubeFlaskTexts = new String[]{"wing", "clock", "timer", "signal", "broadcast", "flask", "pin", "cube"};
   private final Runnable titleServersAction;
   private float landedDetailWidth;
   private final String[] identifiersTexts;
   private final String loggedStartText;
   private static final float[] cubeFlaskAmounts = new float[49];
   private static final float[] cubeTable = new float[12];
   private final String drawableCenteredText;
   private final String landedStoppedText;
   private Color color;
   private static final float[] arrivalScreenTable = new float[12];
   private ButtonWidget drawableChild;
   private static final float[] flaskCubeTable = new float[49];
   private final int arrivalScreenValue;
   private final Screen currentScreen;
   private static final float[] table = new float[12];
   private static final float[] cubeAmounts = new float[12];
   private boolean landedActive;
   private float arrivalScreenAmount;
   private final String loggedSafelyText;
   private final boolean landedStoppedFlag;
   private static final SoundEvent soundEvent = SoundEvent.of(Identifier.of("corzclient", "buildfinished"));
   private boolean serverTitleFlag;
   private String flaskCubeText;
   private final String arrivalScreenText;
   private final TripStats tripStats;
   private int translatePushCount;
   private boolean pressedActive;
   private float flaskCubeAmount;
   private final MatrixStackUtilsAmount.Anims titleServersAnims;
   private static final Color WHITE = new Color(-1, true);
   private final String[] detailRawTexts = new String[8];
   private String landedStoppedFormatted;
   private boolean screenActive;
   private float landedAmount;
   private static final float[] CUBE_PERSPECTIVE_TABLE = new float[49];
   private boolean arrivalScreenActive;
   private float flaskCubeTextWidth;
   private final String startLoggedText;
   private float landedLabelWidth;
   private String landedText;
   private static final int UNUSED_ONE = 1;
   private final String centeredTitleText;
   private final MatrixStackUtilsAmount.Hits titleServersHits;
   private float landedTotalWidth;
   private final String flownDistanceText;
   private final MatrixStack matrixStack;
   private float layoutScale;
   private static final float[] CUBE_SCREEN_X_TABLE = new float[49];
   private int colorValue;
   private float loggedSafelyWidth;
   private final String landedPrefixText;
   private final String landedDetailText;
   private final String[] titleFormattedTexts;
   private float widthHeightAmount;
   private ButtonWidget addDrawableChildDrawableCent;

   @Override
   public void render(DrawContext context, int arrivalScreenValue, int arrivalScreenValue2, float arrivalScreenValue3) {
      if (!this.pressedActive) {
         try {
            if (!this.canWidthHeight(context, arrivalScreenValue, arrivalScreenValue2)) {
               return;
            }
         } catch (Throwable e) {
            this.pressedActive = true;
            this.translatePushCount = 0;
            CorzRoundedUtils.panelOverlayAmount = 1.0F;
         }
      }

      if (this.pressedActive) {
         if (this.lastNanoTimeWidthHeight != 0L) {
            this.handleScreenInstance(this.screenPhase);
            return;
         }

         this.renderDrawableCentered(context);
      }

      super.render(context, arrivalScreenValue, arrivalScreenValue2, arrivalScreenValue3);
   }

   private void renderTextsIdentifiers(DrawContext context, int index, float identifiersValue, float identifiersValue2, float amountFloat) {
      MatrixPeekUtilsRender.render(context, identifiersValue, identifiersValue2, amountFloat, 26.0F, 6.0F, 150994943, 352321535);
      float configsLikeValue = identifiersValue2 + 5.0F;
      MatrixPeekUtilsRender.renderMatrixPeek(context, identifiersValue + 6.0F, configsLikeValue, 16.0F, 16.0F, 5.0F, MatrixStackUtilsAmount.getValue(getReleaseRight(), 0.13F));
      Identifier identifier = this.identifiers[index];
      if (identifier != null) {
         float identifiersValue5 = 3.0F;
         MatrixPeekUtilsIdentifier.handle(identifiersValue + 6.0F + identifiersValue5, configsLikeValue + identifiersValue5, 10.0F, 10.0F, identifier, this.getColor(), MatrixPeekUtilsRender.getMatrixStack());
      }

      float scaleValue = identifiersValue + 29.0F;
      MatrixStackUtilsAmount.handlePushScale(this.identifiersTexts[index], scaleValue, MatrixStackUtilsAmount.getAmount(identifiersValue2 + 3.0F, 10.0F, 0.82F), 0.82F, -7630437);
      MatrixStackUtilsAmount.handlePushScale(this.titleFormattedTexts[index], scaleValue, MatrixStackUtilsAmount.getAmount(identifiersValue2 + 12.0F, 12.0F, 0.95F), 0.95F, -920587);
   }

   private void renderAmountsCube(DrawContext context, float cubeValue, float y, float cubeLimit) {
      if (!(cubeLimit <= 0.0F) && !(cubeLimit >= 820.0F)) {
         float cubeFactor = cubeLimit / 820.0F;
         float amountForFloat = MatrixStackUtilsAmount.getAmountForFloat(cubeFactor);
         float matrixStackFactor = (1.0F - cubeFactor) * (1.0F - cubeFactor);
         int releaseRight = getReleaseRight();

         for (int i = 0; i < 12; i++) {
            float cubeFactor3 = 25.0F + arrivalScreenTable[i] * amountForFloat;
            float materialFactor = table[i] * (1.0F - 0.5F * cubeFactor);
            float matrixPeekValue = cubeValue + cubeTable[i] * cubeFactor3 - materialFactor / 2.0F;
            float configsLikeValue = y + cubeAmounts[i] * cubeFactor3 + 6.0F * cubeFactor * cubeFactor - materialFactor / 2.0F;
            int matrixStackValue = i % 3 == 0 ? -1 : (i % 3 == 1 ? this.arrivalScreenValue : releaseRight);
            MatrixPeekUtilsRender.renderMatrixPeek(context, matrixPeekValue, configsLikeValue, materialFactor, materialFactor, materialFactor / 2.0F, MatrixStackUtilsAmount.getValue(matrixStackValue, matrixStackFactor));
         }
      }
   }

   static {
      float arrivalScreenFactor = 24.0F;
      float arrivalScreenFactor2 = 296.0F;
      float deltaVar = arrivalScreenFactor2 - arrivalScreenFactor;
      float arrivalScreenFactor3 = 135.0F;
      float arrivalScreenFactor4 = arrivalScreenFactor + 0.32F * deltaVar;
      float arrivalScreenFactor5 = arrivalScreenFactor3 - 30.0F;
      float arrivalScreenFactor6 = arrivalScreenFactor + 0.66F * deltaVar;
      float arrivalScreenFactor7 = arrivalScreenFactor3 + 14.0F;
      float arrivalScreenFactor8 = arrivalScreenFactor3 - 6.0F;

      for (int i = 0; i <= 48; i++) {
         float arrivalScreenFactor9 = i / 48.0F;
         float arrivalScreenFactor10 = 1.0F - arrivalScreenFactor9;
         float arrivalScreenFactor11 = arrivalScreenFactor10 * arrivalScreenFactor10 * arrivalScreenFactor10;
         float arrivalScreenFactor12 = 3.0F * arrivalScreenFactor10 * arrivalScreenFactor10 * arrivalScreenFactor9;
         float arrivalScreenFactor13 = 3.0F * arrivalScreenFactor10 * arrivalScreenFactor9 * arrivalScreenFactor9;
         float arrivalScreenFactor14 = arrivalScreenFactor9 * arrivalScreenFactor9 * arrivalScreenFactor9;
         CUBE_SCREEN_X_TABLE[i] = arrivalScreenFactor11 * arrivalScreenFactor + arrivalScreenFactor12 * arrivalScreenFactor4 + arrivalScreenFactor13 * arrivalScreenFactor6 + arrivalScreenFactor14 * arrivalScreenFactor2;
         cubeFlaskAmounts[i] = arrivalScreenFactor11 * arrivalScreenFactor3 + arrivalScreenFactor12 * arrivalScreenFactor5 + arrivalScreenFactor13 * arrivalScreenFactor7 + arrivalScreenFactor14 * arrivalScreenFactor8;
         float arrivalScreenFactor15 = 3.0F * arrivalScreenFactor10 * arrivalScreenFactor10 * (arrivalScreenFactor4 - arrivalScreenFactor) + 6.0F * arrivalScreenFactor10 * arrivalScreenFactor9 * (arrivalScreenFactor6 - arrivalScreenFactor4) + 3.0F * arrivalScreenFactor9 * arrivalScreenFactor9 * (arrivalScreenFactor2 - arrivalScreenFactor6);
         float arrivalScreenFactor16 = 3.0F * arrivalScreenFactor10 * arrivalScreenFactor10 * (arrivalScreenFactor5 - arrivalScreenFactor3) + 6.0F * arrivalScreenFactor10 * arrivalScreenFactor9 * (arrivalScreenFactor7 - arrivalScreenFactor5) + 3.0F * arrivalScreenFactor9 * arrivalScreenFactor9 * (arrivalScreenFactor8 - arrivalScreenFactor7);
         float distance = (float)Math.sqrt(arrivalScreenFactor15 * arrivalScreenFactor15 + arrivalScreenFactor16 * arrivalScreenFactor16);
         CUBE_PERSPECTIVE_TABLE[i] = distance < 1.0E-4F ? 0.0F : -arrivalScreenFactor16 / distance;
         flaskCubeTable[i] = distance < 1.0E-4F ? 1.0F : arrivalScreenFactor15 / distance;
      }

      for (int j = 0; j < 12; j++) {
         double angle = j * (Math.PI / 6) + j * 37 % 11 / 11.0 * 0.35;
         cubeTable[j] = (float)Math.cos(angle);
         cubeAmounts[j] = (float)Math.sin(angle);
         arrivalScreenTable[j] = 8.0F + j * 53 % 7 * 1.6F;
         table[j] = 1.8F + j * 29 % 5 * 0.25F;
      }
   }

   private static float getAmount(double arrivalScreenDivisor) {
      return (float)((Math.sin(System.nanoTime() / 1.0E9 * Math.PI * 2.0 / arrivalScreenDivisor) + 1.0) / 2.0);
   }

   @Override
   public boolean keyPressed(KeyInput keyInput) {
      int pressedValue = keyInput.key();
      if (pressedValue == 256) {
         this.handleTimeFlag(0);
         return true;
      } else if (pressedValue != 257 && pressedValue != 335) {
         return this.pressedActive ? super.keyPressed(keyInput) : true;
      } else {
         this.handleTimeFlag(1);
         return true;
      }
   }

   private void renderTextAmount(DrawContext context, int arrivalScreenValue, int matrixStackValue, int matrixStackLimit, int scaledY) {
      float arrivalScreenValue5 = arrivalScreenValue + 160.0F;
      float matrixPeekValue = arrivalScreenValue + 18;
      float releaseRightBound = 284.0F;
      this.renderAmountLanded(context, arrivalScreenValue5, matrixStackValue + 40.0F);
      if (this.canTranslatePush(this.getAmount(1))) {
         try {
            MatrixStackUtilsAmount.getAmountForDrawContextString(context, this.landedStoppedFormatted, MatrixStackUtilsAmount.getAmount(arrivalScreenValue5 - this.arrivalScreenAmount / 2.0F), matrixStackValue + 72, 14.0F);
         } finally {
            this.updatePopAmounts();
         }
      }

      if (this.canTranslatePush(this.getAmount(2))) {
         try {
            this.handleLandedText(arrivalScreenValue5, matrixStackValue);
         } finally {
            this.updatePopAmounts();
         }
      }

      if (this.canTranslatePush(this.getAmount(3))) {
         try {
            this.renderFlaskCube(context, arrivalScreenValue, matrixStackValue);
         } finally {
            this.updatePopAmounts();
         }
      }

      if (this.canTranslatePush(this.getAmount(4))) {
         try {
            MatrixPeekUtilsRender.renderMatrixPeek(context, matrixPeekValue, matrixStackValue + 157, releaseRightBound, MatrixPeekUtilsRender.getAmount(), CardMatrixUtils.getValueInt());
         } finally {
            this.updatePopAmounts();
         }
      }

      float amountFloat = getAmountFloat();

      for (int i = 0; i < 8; i++) {
         int arrivalScreenValue9 = i / 2;
         int arrivalScreenFactor = i % 2;
         if (this.canTranslatePush(this.getAmount(4 + arrivalScreenValue9))) {
            try {
               this.renderTextsIdentifiers(context, i, matrixPeekValue + arrivalScreenFactor * (amountFloat + 5.0F), matrixStackValue + 165 + arrivalScreenValue9 * 31, amountFloat);
            } finally {
               this.updatePopAmounts();
            }
         }
      }

      if (this.canTranslatePush(this.getAmount(8))) {
         try {
            this.renderTitleServers(context, matrixPeekValue, releaseRightBound, matrixStackValue + 296, matrixStackLimit, scaledY);
         } finally {
            this.updatePopAmounts();
         }
      }
   }

   private void renderAmountLanded(DrawContext context, float landedValue, float y) {
      float corzLicenseLimit = 22.0F * MatrixStackUtilsAmount.getAmount((this.widthHeightAmount - 110.0F) / 380.0F, 1.7F);
      if (!(corzLicenseLimit <= 0.25F)) {
         this.render(context, landedValue, y, this.widthHeightAmount - 280.0F);
         this.render(context, landedValue, y, this.widthHeightAmount - 280.0F - 220.0F);
         if (this.landedStoppedFlag) {
            this.renderAmountsCube(context, landedValue, y, this.widthHeightAmount - 260.0F);
         }

         float landedValue4 = getAmount(2.8);
         MatrixPeekUtilsRender.renderMatrixPeekForDrawContextFloat(
            context, landedValue - corzLicenseLimit, y - corzLicenseLimit, corzLicenseLimit * 2.0F, corzLicenseLimit * 2.0F, corzLicenseLimit, MatrixStackUtilsAmount.getValue(this.arrivalScreenValue, 0.3F + 0.08F * landedValue4), 16.0F, 0.9F
         );
         MatrixPeekUtilsRender.renderMatrixPeek(context, landedValue - corzLicenseLimit, y - corzLicenseLimit, corzLicenseLimit * 2.0F, corzLicenseLimit * 2.0F, corzLicenseLimit, this.arrivalScreenValue);
         float landedLimit = MatrixStackUtilsAmount.getAmountForFloat((this.widthHeightAmount - 300.0F) / 200.0F);
         if (!(landedLimit <= 0.002F)) {
            boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(
               context, landedValue, y, 0.6F + 0.4F * MatrixStackUtilsAmount.getAmount((this.widthHeightAmount - 300.0F) / 300.0F, 2.0F)
            );

            try {
               if (this.landedStoppedFlag) {
                  Color color = landedLimit >= 1.0F ? WHITE : new Color(MatrixStackUtilsAmount.getValue(-1, landedLimit), true);
                  MatrixPeekUtilsIdentifier.handle(
                     landedValue - 12.0F, y - 12.0F, 24.0F, 24.0F, IcoGenUtils.getIcoCheck(), color, MatrixPeekUtilsRender.getMatrixStack()
                  );
               } else {
                  int matrixStackValue = MatrixStackUtilsAmount.getValue(-1, landedLimit);
                  MatrixPeekUtilsRender.renderMatrixPeek(context, landedValue - 1.7F, y - 9.0F, 3.4F, 11.0F, 1.7F, matrixStackValue);
                  MatrixPeekUtilsRender.renderMatrixPeek(context, landedValue - 1.9F, y + 4.2F, 3.8F, 3.8F, 1.9F, matrixStackValue);
               }
            } finally {
               MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
            }
         }
      }
   }

   private void updateSoundPlay() {
      try {
         MinecraftClient.getInstance()
            .getSoundManager()
            .play(this.landedStoppedFlag ? PositionedSoundInstance.ui(soundEvent, 1.0F, 0.8F) : PositionedSoundInstance.ui(SoundEvents.UI_TOAST_IN, 1.0F, 1.0F));
      } catch (Throwable e) {
      }
   }

   public static void handleSendInstance(TripStats tripStats) {
      TripStats tripStats2 = getTripStats(tripStats);
      MinecraftClient instance = MinecraftClient.getInstance();
      if (instance != null) {
         instance.send(() -> {
            if (instance.world != null) {
               try {
                  instance.disconnect(ClientWorld.QUITTING_MULTIPLAYER_TEXT);
               } catch (Throwable e) {
               }

               if (instance.world != null) {
                  handleDisconnectLiteral(instance, tripStats2);
               } else {
                  handleLiteralScreen(instance, tripStats2);
               }
            }
         });
      }
   }

   private static void handleVertexColor(
      VertexConsumer vertices,
      Matrix4f matrix,
      float vertexColorValue,
      float vertexColorValue2,
      float vertexColorFactor,
      float vertexColorFactor2,
      int vertexColorValue3,
      float vertexColorValue4,
      float vertexColorValue5,
      float vertexColorFactor3,
      float vertexColorFactor4,
      int vertexColorValue6,
      float vertexColorFactor5,
      float vertexColorValue7
   ) {
      int vertexColorValue8 = vertexColorValue3 & 16777215;
      int vertexColorValue9 = vertexColorValue6 & 16777215;
      float vertexColorFactor6 = vertexColorFactor5 + vertexColorValue7;
      if (vertexColorFactor5 > 0.0F) {
         vertices.vertex(matrix, vertexColorValue + vertexColorFactor * vertexColorFactor5, vertexColorValue2 + vertexColorFactor2 * vertexColorFactor5, 0.0F).color(vertexColorValue3);
         vertices.vertex(matrix, vertexColorValue - vertexColorFactor * vertexColorFactor5, vertexColorValue2 - vertexColorFactor2 * vertexColorFactor5, 0.0F).color(vertexColorValue3);
         vertices.vertex(matrix, vertexColorValue4 - vertexColorFactor3 * vertexColorFactor5, vertexColorValue5 - vertexColorFactor4 * vertexColorFactor5, 0.0F).color(vertexColorValue6);
         vertices.vertex(matrix, vertexColorValue4 + vertexColorFactor3 * vertexColorFactor5, vertexColorValue5 + vertexColorFactor4 * vertexColorFactor5, 0.0F).color(vertexColorValue6);
      }

      vertices.vertex(matrix, vertexColorValue + vertexColorFactor * vertexColorFactor5, vertexColorValue2 + vertexColorFactor2 * vertexColorFactor5, 0.0F).color(vertexColorValue3);
      vertices.vertex(matrix, vertexColorValue + vertexColorFactor * vertexColorFactor6, vertexColorValue2 + vertexColorFactor2 * vertexColorFactor6, 0.0F).color(vertexColorValue8);
      vertices.vertex(matrix, vertexColorValue4 + vertexColorFactor3 * vertexColorFactor6, vertexColorValue5 + vertexColorFactor4 * vertexColorFactor6, 0.0F).color(vertexColorValue9);
      vertices.vertex(matrix, vertexColorValue4 + vertexColorFactor3 * vertexColorFactor5, vertexColorValue5 + vertexColorFactor4 * vertexColorFactor5, 0.0F).color(vertexColorValue6);
      vertices.vertex(matrix, vertexColorValue - vertexColorFactor * vertexColorFactor5, vertexColorValue2 - vertexColorFactor2 * vertexColorFactor5, 0.0F).color(vertexColorValue3);
      vertices.vertex(matrix, vertexColorValue - vertexColorFactor * vertexColorFactor6, vertexColorValue2 - vertexColorFactor2 * vertexColorFactor6, 0.0F).color(vertexColorValue8);
      vertices.vertex(matrix, vertexColorValue4 - vertexColorFactor3 * vertexColorFactor6, vertexColorValue5 - vertexColorFactor4 * vertexColorFactor6, 0.0F).color(vertexColorValue9);
      vertices.vertex(matrix, vertexColorValue4 - vertexColorFactor3 * vertexColorFactor5, vertexColorValue5 - vertexColorFactor4 * vertexColorFactor5, 0.0F).color(vertexColorValue6);
   }

   private boolean canWidthHeight(DrawContext context, int widthHeightValue, int widthHeightValue2) {
      MatrixClearUtils.updateTimeAmount();
      MatrixEspUtils.updateFlagTime();
      CorzClient.fontManager.setMatrixStack(this.matrixStack);
      MatrixPeekUtilsRender.handleMatrixStack(this.matrixStack);
      CorzRoundedUtils.panelOverlayAmount = 1.0F;
      long nanoTime = System.nanoTime();
      if (this.lastNanoTimeWidthHeight != 0L && (float)(nanoTime - this.lastNanoTimeWidthHeight) / 1000000.0F >= 120.0F) {
         this.handleScreenInstance(this.screenPhase);
         return false;
      } else {
         if (this.lastNanoTime == 0L) {
            this.lastNanoTime = nanoTime;
            this.updateSoundPlay();
         }

         boolean widthHeightFlag = this.lastNanoTimeWidthHeight != 0L;
         this.widthHeightAmount = (float)(nanoTime - this.lastNanoTime) / 1000000.0F;
         float matrixStackFactor = widthHeightFlag ? (float)(nanoTime - this.lastNanoTimeWidthHeight) / 1.2E8F : this.widthHeightAmount / 320.0F;
         this.layoutScale = Math.min(1.0F, Math.min((this.width - 16) / 320.0F, (this.height - 16) / 332.0F));
         int peekValue = (this.width - 320) / 2;
         int matrixStackValue = (this.height - 332) / 2;
         int matrixStackLimit = widthHeightFlag ? -4000 : this.getMin(widthHeightValue, this.width);
         int scaledY = this.getMin(widthHeightValue2, this.height);
         this.updateAmountText();
         this.renderWidthHeight(context);
         this.titleServersHits.update();
         float amountForFloat = MatrixStackUtilsAmount.getAmountForFloat((this.widthHeightAmount - 110.0F) / 400.0F);
         boolean translatePush = MatrixStackUtilsAmount.canTranslatePush(
            context, this.width / 2.0F, this.height / 2.0F, MatrixStackUtilsAmount.getAmount(matrixStackFactor, widthHeightFlag) * this.layoutScale
         );
         float panelOverlay = CorzRoundedUtils.getPanelOverlay(MatrixStackUtilsAmount.getAmountForFloatFlag(matrixStackFactor, widthHeightFlag));

         try {
            MatrixPeekUtilsRender.renderMatrixPeek(
               context, peekValue + 24.0F, matrixStackValue + 24.0F, 272.0F, 284.0F, 40.0F, MatrixStackUtilsAmount.getValue(this.arrivalScreenValue, 0.07F * amountForFloat), 60.0F, 1.0F
            );
            MatrixStackUtilsAmount.render(
               context,
               (float)peekValue,
               (float)matrixStackValue,
               320.0F,
               332.0F,
               CardMatrixUtils.getValue(MatrixStackUtilsAmount.getValue(), this.landedValue, 0.55 * amountForFloat),
               CardMatrixUtils.getValue(352321535, MatrixStackUtilsAmount.getValue(this.arrivalScreenValue, 0.34F), amountForFloat)
            );
            TwoDidUtils.renderMinPeek(context, 2, peekValue, matrixStackValue, 320.0F, 117.0F, 10.0F, 0.6F);
            this.renderTextAmount(context, peekValue, matrixStackValue, matrixStackLimit, scaledY);
         } finally {
            CorzRoundedUtils.handlePanelOverlay(panelOverlay);
            MatrixStackUtilsAmount.renderPopMatrices(context, translatePush);
         }

         return true;
      }
   }

   private float getAmount(int arrivalScreenValue) {
      return MatrixStackUtilsAmount.getAmountForFloat((this.widthHeightAmount - 70.0F - arrivalScreenValue * 38.0F) / 230.0F);
   }

   @Override
   public void renderBackground(DrawContext context, int backgroundValue, int backgroundValue2, float backgroundValue3) {
   }

   public TripStats getTripStats() {
      return this.tripStats;
   }

   private void handleLandedText(float landedValue, int matrixStackValue) {
      float landedValue3 = MatrixStackUtilsAmount.getAmount(matrixStackValue + 88, 10.0F, 0.82F);
      float scaleValue = MatrixStackUtilsAmount.getAmount(landedValue - this.landedTotalWidth / 2.0F);
      MatrixStackUtilsAmount.handlePushScale(this.landedPrefixText, scaleValue, landedValue3, 0.82F, -7630437);
      MatrixStackUtilsAmount.handlePushScale(this.landedDetailText, MatrixStackUtilsAmount.getAmount(scaleValue + this.landedLabelWidth + this.spaceWidth), landedValue3, 0.82F, -920587);
      if (this.landedActive) {
         MatrixStackUtilsAmount.handlePushScale(
            this.loggedSafelyText, MatrixStackUtilsAmount.getAmount(scaleValue + this.landedLabelWidth + this.landedDetailWidth + this.spaceWidth * 2.0F), landedValue3, 0.82F, -7630437
         );
      }

      if (!this.landedText.isEmpty()) {
         float priceValue = MatrixStackUtilsAmount.getAmount(matrixStackValue + 99, 10.0F, 0.82F);
         MatrixStackUtilsAmount.handlePushScale(this.landedText, MatrixStackUtilsAmount.getAmount(landedValue - this.landedAmount / 2.0F), priceValue, 0.82F, this.landedStoppedValue);
      }
   }

   private static void handleLiteralScreen(MinecraftClient client, TripStats tripStats) {
      Screen currentScreen = client.currentScreen;

      try {
         client.setScreen(new ArrivalScreen(currentScreen, tripStats));
      } catch (Throwable e) {
         try {
            client.setScreen(
               new DisconnectedScreen(
                  (Screen)(currentScreen != null ? currentScreen : new TitleScreen()), Text.literal(tripStats.formatTripDestination()), Text.literal(tripStats.formatTripDestinationSuccess())
               )
            );
         } catch (Throwable e2) {
         }
      }
   }

   @Override
   public void close() {
      this.handleScreenInstance(0);
   }

   @Override
   public void removed() {
      super.removed();
      if (!this.screenActive) {
         MinecraftClient instance = MinecraftClient.getInstance();
         instance.send(() -> handleScaleFactor(instance));
      }
   }

   private void renderDrawableCentered(DrawContext context) {
      try {
         int widthScaled = this.width / 2;
         int drawableCenteredValue = Math.max(8, this.height / 2 - 52);
         if (!this.serverTitleFlag) {
            this.serverTitleFlag = true;
            this.drawableChild = this.addDrawableChild(
               ButtonWidget.builder(Text.literal("Server List"), buttonWidget -> this.handleScreenInstance(1)).dimensions(widthScaled - 102, drawableCenteredValue + 66, 100, 20).build()
            );
            this.addDrawableChildDrawableCent = this.addDrawableChild(
               ButtonWidget.builder(Text.literal("Title Screen"), buttonWidget2 -> this.handleScreenInstance(2)).dimensions(widthScaled + 2, drawableCenteredValue + 66, 100, 20).build()
            );
         }

         this.drawableChild.setPosition(widthScaled - 102, drawableCenteredValue + 66);
         this.addDrawableChildDrawableCent.setPosition(widthScaled + 2, drawableCenteredValue + 66);
         context.fill(0, 0, this.width, this.height, -16052976);
         TextRenderer textRenderer = this.textRenderer != null ? this.textRenderer : MinecraftClient.getInstance().textRenderer;
         context.drawCenteredTextWithShadow(textRenderer, this.landedStoppedText, widthScaled, drawableCenteredValue, this.landedStoppedFlag ? -8393375 : -1535700);
         context.drawCenteredTextWithShadow(textRenderer, this.arrivalScreenText, widthScaled, drawableCenteredValue + 18, -920587);
         context.drawCenteredTextWithShadow(textRenderer, this.drawableCenteredText, widthScaled, drawableCenteredValue + 30, -4932924);
         context.drawCenteredTextWithShadow(textRenderer, this.centeredTitleText, widthScaled, drawableCenteredValue + 42, -7630437);
      } catch (Throwable e) {
      }
   }

   private void render(DrawContext context, float arrivalScreenValue, float y, float arrivalScreenLimit) {
      if (!(arrivalScreenLimit <= 0.0F) && !(arrivalScreenLimit >= 900.0F)) {
         float ctxScaled = arrivalScreenLimit / 900.0F;
         float arrivalScreenFactor = 1.0F - ctxScaled;
         float corzLicenseLimit = 22.0F + 18.0F * MatrixStackUtilsAmount.getAmountForFloat(ctxScaled);
         MatrixPeekUtilsRender.renderMatrixPeek(
            context, arrivalScreenValue - corzLicenseLimit, y - corzLicenseLimit, corzLicenseLimit * 2.0F, corzLicenseLimit * 2.0F, corzLicenseLimit, 1.3F, MatrixStackUtilsAmount.getValue(this.arrivalScreenValue, 0.5F * arrivalScreenFactor * arrivalScreenFactor)
         );
      }
   }

   private static void handleScaleFactor(MinecraftClient client) {
      if (!(client.currentScreen instanceof ScreenGuiApi)) {
         Window window = client.getWindow();
         int calculateScaleFactor = window.calculateScaleFactor(client.options.getGuiScale().getValue(), client.forcesUnicodeFont());
         if (calculateScaleFactor != window.getScaleFactor()) {
            client.onResolutionChanged();
         }
      }
   }

   private void updateAmountText() {
      VertexTextureFont vertexTexture = MatrixStackUtilsAmount.getFontRenderer();
      if (vertexTexture != this.vertexTexture) {
         this.vertexTexture = vertexTexture;
         float arrivalScreenLimit = 284.0F;
         this.landedStoppedFormatted = vertexTexture.formatText(this.landedStoppedText, arrivalScreenLimit / 1.22F);
         this.arrivalScreenAmount = MatrixStackUtilsAmount.getAmount(this.landedStoppedFormatted, 1.22F);
         this.spaceWidth = Math.max(1.5F, MatrixStackUtilsAmount.getAmount("a a", 0.82F) - MatrixStackUtilsAmount.getAmount("aa", 0.82F));
         this.landedLabelWidth = MatrixStackUtilsAmount.getAmount(this.landedPrefixText, 0.82F);
         this.landedDetailWidth = MatrixStackUtilsAmount.getAmount(this.landedDetailText, 0.82F);
         this.loggedSafelyWidth = this.loggedSafelyText.isEmpty() ? 0.0F : MatrixStackUtilsAmount.getAmount(this.loggedSafelyText, 0.82F);
         this.landedTotalWidth = this.landedLabelWidth + this.spaceWidth + this.landedDetailWidth;
         this.landedActive = this.loggedSafelyWidth > 0.0F && this.landedTotalWidth + this.spaceWidth + this.loggedSafelyWidth <= arrivalScreenLimit;
         if (this.landedActive) {
            this.landedTotalWidth = this.landedTotalWidth + (this.spaceWidth + this.loggedSafelyWidth);
         }

         this.landedText = vertexTexture.formatText(this.loggedStartText, arrivalScreenLimit / 0.82F);
         this.landedAmount = MatrixStackUtilsAmount.getAmount(this.landedText, 0.82F);
         this.flaskCubeAmount = MatrixStackUtilsAmount.getAmount(this.flownLoggedText, 0.76F);
         this.flaskCubeText = vertexTexture.formatText(this.flownDistanceText, Math.max(20.0F, arrivalScreenLimit / 2.0F - this.flaskCubeAmount / 2.0F - 10.0F) / 0.76F);
         this.flaskCubeTextWidth = MatrixStackUtilsAmount.getAmount(this.flaskCubeText, 0.76F);
         float arrivalScreenValue = getAmountFloat() - 29.0F - 6.0F;

         for (int i = 0; i < 8; i++) {
            this.identifiersTexts[i] = vertexTexture.formatText(this.detailRawTexts[i], arrivalScreenValue / 0.82F);
            this.titleFormattedTexts[i] = vertexTexture.formatText(this.arrivalScreenTexts[i], arrivalScreenValue / 0.95F);
            if (this.identifiers[i] == null) {
               try {
                  this.identifiers[i] = IcoGenUtils.getIdentifierForString(cubeFlaskTexts[i]);
               } catch (Throwable e) {
               }
            }
         }
      }
   }

   private void renderLandedStopped(DrawContext context, float landedStoppedValue, float landedStoppedValue2, float matrixStackValue) {
      float landedStoppedValue4 = 4.6F * MatrixStackUtilsAmount.getAmount(matrixStackValue, 2.0F);
      if (!(landedStoppedValue4 <= 0.2F)) {
         if (!this.landedStoppedFlag) {
            MatrixPeekUtilsRender.renderMatrixPeek(
               context, landedStoppedValue - landedStoppedValue4, landedStoppedValue2 - landedStoppedValue4, landedStoppedValue4 * 2.0F, landedStoppedValue4 * 2.0F, landedStoppedValue4, MatrixStackUtilsAmount.getValue(this.arrivalScreenValue, 0.22F)
            );
            MatrixPeekUtilsRender.renderMatrixPeek(context, landedStoppedValue - landedStoppedValue4, landedStoppedValue2 - landedStoppedValue4, landedStoppedValue4 * 2.0F, landedStoppedValue4 * 2.0F, landedStoppedValue4, 1.3F, this.arrivalScreenValue);
         } else {
            float landedStoppedValue5 = (this.widthHeightAmount - 1040.0F) % 2200.0F / 1300.0F;
            if (landedStoppedValue5 < 1.0F) {
               float corzLicenseLimit = landedStoppedValue4 + 8.0F * MatrixStackUtilsAmount.getAmountForFloat(landedStoppedValue5);
               float landedStoppedFactor = 1.0F - landedStoppedValue5;
               MatrixPeekUtilsRender.renderMatrixPeek(
                  context, landedStoppedValue - corzLicenseLimit, landedStoppedValue2 - corzLicenseLimit, corzLicenseLimit * 2.0F, corzLicenseLimit * 2.0F, corzLicenseLimit, 1.1F, MatrixStackUtilsAmount.getValue(this.arrivalScreenValue, 0.55F * landedStoppedFactor * landedStoppedFactor)
               );
            }

            MatrixPeekUtilsRender.renderMatrixPeek(
               context, landedStoppedValue - landedStoppedValue4, landedStoppedValue2 - landedStoppedValue4, landedStoppedValue4 * 2.0F, landedStoppedValue4 * 2.0F, landedStoppedValue4, MatrixStackUtilsAmount.getValue(this.arrivalScreenValue, 0.45F), 6.0F, 0.9F
            );
            MatrixPeekUtilsRender.renderMatrixPeek(context, landedStoppedValue - landedStoppedValue4, landedStoppedValue2 - landedStoppedValue4, landedStoppedValue4 * 2.0F, landedStoppedValue4 * 2.0F, landedStoppedValue4, this.arrivalScreenValue);
            float min7 = Math.min(landedStoppedValue4 * 0.4F, 1.7F);
            MatrixPeekUtilsRender.renderMatrixPeek(context, landedStoppedValue - min7, landedStoppedValue2 - min7, min7 * 2.0F, min7 * 2.0F, min7, -1);
         }
      }
   }

   private void renderFlaskCube(DrawContext context, int flaskCubeValue, int flaskCubeValue2) {
      float flaskCubeLimit = MatrixStackUtilsAmount.getAmount3((this.widthHeightAmount - 360.0F) / 760.0F);
      int cardMatrixValue = this.landedStoppedFlag ? getReleaseRight() : -7630437;
      if (flaskCubeLimit > 0.0F) {
         if (this.landedStoppedFlag) {
            this.handleFlaskCube(flaskCubeValue, flaskCubeValue2, flaskCubeLimit, 0.0F, 5.0F, cardMatrixValue, this.arrivalScreenValue, 0.3F, false, -9.0F);
         }

         this.handleFlaskCube(flaskCubeValue, flaskCubeValue2, flaskCubeLimit, 0.75F, 0.8F, cardMatrixValue, this.arrivalScreenValue, 1.0F, !this.landedStoppedFlag, this.landedStoppedFlag ? this.getAmount() : -9.0F);
      }

      float flaskCubeValue4 = flaskCubeValue + CUBE_SCREEN_X_TABLE[0];
      float flaskCubeValue5 = flaskCubeValue2 + cubeFlaskAmounts[0];
      float corzLicenseLimit = 2.6F;
      MatrixPeekUtilsRender.renderMatrixPeek(
         context, flaskCubeValue4 - corzLicenseLimit, flaskCubeValue5 - corzLicenseLimit, corzLicenseLimit * 2.0F, corzLicenseLimit * 2.0F, corzLicenseLimit, MatrixStackUtilsAmount.getValue(cardMatrixValue, 0.35F), 4.0F, 0.8F
      );
      MatrixPeekUtilsRender.renderMatrixPeek(context, flaskCubeValue4 - corzLicenseLimit, flaskCubeValue5 - corzLicenseLimit, corzLicenseLimit * 2.0F, corzLicenseLimit * 2.0F, corzLicenseLimit, CardMatrixUtils.getValue(cardMatrixValue, -1, 0.2));
      if (flaskCubeLimit > 0.0F && flaskCubeLimit < 1.0F) {
         float flaskCubeValue7 = flaskCubeLimit * 48.0F;
         int index = Math.min(47, (int)flaskCubeValue7);
         float flaskCubeFactor = Math.min(1.0F, flaskCubeValue7 - index);
         float flaskCubeValue8 = flaskCubeValue + CUBE_SCREEN_X_TABLE[index] + (CUBE_SCREEN_X_TABLE[index + 1] - CUBE_SCREEN_X_TABLE[index]) * flaskCubeFactor;
         float flaskCubeValue9 = flaskCubeValue2 + cubeFlaskAmounts[index] + (cubeFlaskAmounts[index + 1] - cubeFlaskAmounts[index]) * flaskCubeFactor;
         MatrixPeekUtilsRender.renderMatrixPeekForDrawContextFloat(context, flaskCubeValue8 - 1.8F, flaskCubeValue9 - 1.8F, 3.6F, 3.6F, 1.8F, MatrixStackUtilsAmount.getValue(-1, 0.9F), 6.0F, 0.9F);
      }

      float matrixStackValue = (this.widthHeightAmount - 1040.0F) / 320.0F;
      if (matrixStackValue > 0.0F) {
         this.renderLandedStopped(context, flaskCubeValue + CUBE_SCREEN_X_TABLE[48], flaskCubeValue2 + cubeFlaskAmounts[48], matrixStackValue);
      }

      float priceValue = MatrixStackUtilsAmount.getAmount(flaskCubeValue2 + 117 + 24.0F, 10.0F, 0.76F);
      MatrixStackUtilsAmount.handlePushScale(this.startLoggedText, MatrixStackUtilsAmount.getAmount(flaskCubeValue4 - 3.0F), priceValue, 0.76F, -7630437);
      MatrixStackUtilsAmount.handlePushScale(this.flownLoggedText, MatrixStackUtilsAmount.getAmount(flaskCubeValue + 160.0F - this.flaskCubeAmount / 2.0F), priceValue, 0.76F, -7630437);
      MatrixStackUtilsAmount.handlePushScale(
         this.flaskCubeText, MatrixStackUtilsAmount.getAmount(flaskCubeValue + CUBE_SCREEN_X_TABLE[48] + 3.0F - this.flaskCubeTextWidth), priceValue, 0.76F, -920587
      );
   }

   private void handleScreenInstance(int screenValue) {
      if (!this.screenActive) {
         this.screenActive = true;
         MinecraftClient instance = MinecraftClient.getInstance();

         Object obj;
         try {
            obj = this.getScreen(screenValue);
         } catch (Throwable e) {
            obj = new TitleScreen();
         }

         instance.setScreen((Screen)obj);
         handleScaleFactor(instance);
      }
   }

   private void renderWidthHeight(DrawContext context) {
      MatrixPeekUtilsRender.renderMatrixPeek(context, 0.0F, 0.0F, this.width, this.height, -16184562);
      TwoDidUtils.renderMinPeek(context, 0, 0.0F, 0.0F, this.width, this.height, 0.0F, 1.0F);
   }

   private static int getValue(float arrivalScreenValue, int cardMatrixValue, int releaseRightValue, float matrixStackFactor, float arrivalScreenLimit) {
      int arrivalScreenValue5 = CardMatrixUtils.getValue(cardMatrixValue, releaseRightValue, arrivalScreenValue);
      if (arrivalScreenLimit > -8.0F) {
         float arrivalScreenFactor = (arrivalScreenValue - arrivalScreenLimit) / 0.08F;
         arrivalScreenValue5 = CardMatrixUtils.getValue(arrivalScreenValue5, -1, 0.55 * Math.exp(-arrivalScreenFactor * arrivalScreenFactor));
      }

      return CorzRoundedUtils.getPanelOverlay(MatrixStackUtilsAmount.getValue(arrivalScreenValue5, matrixStackFactor));
   }

   private void renderTitleServers(DrawContext context, float titleServersValue, float titleServersValue2, float editCluster, int matrixStackLimit, int scaledY) {
      float releaseRightBound = (titleServersValue2 - 6.0F) / 2.0F;
      MatrixStackUtilsAmount.getAmount(
         context, this.titleServersHits, this.titleServersAnims, "title", "Title Screen", titleServersValue + releaseRightBound, editCluster, 22.0F, releaseRightBound, matrixStackLimit, scaledY, false, 0.0F, this.titleServersAction
      );
      MatrixStackUtilsAmount.getAmount(
         context, this.titleServersHits, this.titleServersAnims, "servers", "Server List", null, titleServersValue + titleServersValue2, editCluster, 22.0F, releaseRightBound, matrixStackLimit, scaledY, true, this.serversServerAction
      );
   }

   private Screen getScreen(int screenValue) {
      if (screenValue == 2) {
         return new TitleScreen();
      } else if (screenValue == 1) {
         return (Screen)(this.currentScreen instanceof MultiplayerScreen
            ? this.currentScreen
            : new MultiplayerScreen((Screen)(this.currentScreen instanceof TitleScreen ? this.currentScreen : new TitleScreen())));
      } else {
         return (Screen)(this.currentScreen != null ? this.currentScreen : new TitleScreen());
      }
   }

   private void handleTimeFlag(int value) {
      if (this.pressedActive) {
         this.handleScreenInstance(value);
      } else if (this.lastNanoTimeWidthHeight == 0L) {
         this.lastNanoTimeWidthHeight = System.nanoTime();
         this.screenPhase = value;
      }
   }

   private void updatePopAmounts() {
      CorzRoundedUtils.handlePanelOverlay(this.translatePushTable[--this.translatePushCount]);
      MatrixPeekUtilsRender.getMatrixStack().pop();
   }

   private int getMin(double arrivalScreenValue, int arrivalScreenValue2) {
      return (int)Math.round(arrivalScreenValue2 / 2.0 + (arrivalScreenValue - arrivalScreenValue2 / 2.0) / this.layoutScale);
   }

   private static TripStats getTripStats(TripStats tripStats) {
      return tripStats != null ? tripStats : new TripStats(false, "", "", "", "", 0, 0, 0, 0.0, 0L, 0, 0, 0, 0, 0L, 0.0, 0.0);
   }

   private static int getReleaseRight() {
      return MatrixEspUtils.releaseRightValue | 0xFF000000;
   }

   public ArrivalScreen(Screen currentScreen, TripStats tripStats) {
      super(Text.literal(CardStatusUtilsFlag.getOrDefault(getTripStats(tripStats).formatTripDestination())));
      this.arrivalScreenTexts = new String[8];
      this.identifiersTexts = new String[8];
      this.titleFormattedTexts = new String[8];
      this.identifiers = new Identifier[8];
      this.matrixStack = new MatrixStack();
      this.titleServersHits = new MatrixStackUtilsAmount.Hits();
      this.titleServersAnims = new MatrixStackUtilsAmount.Anims();
      this.translatePushTable = new float[4];
      this.serversServerAction = () -> this.handleTimeFlag(1);
      this.titleServersAction = () -> this.handleTimeFlag(2);
      this.landedStoppedFormatted = "";
      this.landedText = "";
      this.flaskCubeText = "";
      this.color = WHITE;
      this.screenPhase = 0;
      this.layoutScale = 1.0F;
      this.currentScreen = currentScreen;
      this.tripStats = getTripStats(tripStats);
      TripStats tripStats2 = this.tripStats;
      this.landedStoppedFlag = tripStats2.success();
      this.arrivalScreenValue = this.landedStoppedFlag ? -12922502 : -1535700;
      this.landedValue = this.landedStoppedFlag ? -16050415 : -15265270;
      this.landedStoppedValue = this.landedStoppedFlag ? -7630437 : CardMatrixUtils.getValue(-1535700, -1, 0.25);
      this.landedStoppedText = CardStatusUtilsFlag.getOrDefault(tripStats2.formatTripDestination());
      this.landedPrefixText = this.landedStoppedFlag ? "Landed at" : "Stopped at";
      this.landedDetailText = CardStatusUtilsFlag.getOrDefault(tripStats2.formatLand());
      this.loggedSafelyText = tripStats2.dimension().isEmpty() ? "" : CardStatusUtilsFlag.getOrDefault("\u00b7 " + tripStats2.dimension());
      String arrivalScreenText;
      if (this.landedStoppedFlag) {
         arrivalScreenText = tripStats2.isFlag() ? tripStats2.formatText() + " \u00b7 logged off safely" : "Logged off safely";
      } else {
         arrivalScreenText = tripStats2.reason().isEmpty() ? "Logged off safely" : tripStats2.reason();
      }

      this.loggedStartText = CardStatusUtilsFlag.getOrDefault(arrivalScreenText);
      this.startLoggedText = "Start";
      this.flownLoggedText = CardStatusUtilsFlag.getOrDefault(tripStats2.formatFlownBlocks());
      this.flownDistanceText = CardStatusUtilsFlag.getOrDefault(tripStats2.formatDestinationLand());
      this.handleTexts(0, "Distance flown", tripStats2.formatFlownBlocks());
      this.handleTexts(1, "Flight time", tripStats2.formatSeconds());
      this.handleTexts(2, "Average speed", tripStats2.formatSecondsFlown());
      this.handleTexts(3, "Top speed", tripStats2.formatBpsSpeed());
      this.handleTexts(4, "Rockets used", BuyerAppendUtils.getAppendForLong(tripStats2.rocketsUsed()));
      this.handleTexts(5, "XP bottles used", BuyerAppendUtils.getAppendForLong(tripStats2.xpUsed()));
      this.handleTexts(6, "Pit stops", BuyerAppendUtils.getAppendForLong(tripStats2.pitStops()));
      this.handleTexts(7, tripStats2.purchases() > 0 ? "Spent \u00b7 " + tripStats2.purchases() + (tripStats2.purchases() == 1 ? " buy" : " buys") : "Spent", tripStats2.formatSpent());
      this.arrivalScreenText = CardStatusUtilsFlag.getOrDefault(
         this.landedPrefixText + " " + this.landedDetailText + (this.loggedSafelyText.isEmpty() ? "" : " " + this.loggedSafelyText)
      );
      this.drawableCenteredText = CardStatusUtilsFlag.getOrDefault(
         tripStats2.formatFlownBlocks() + " in " + tripStats2.formatSeconds() + " \u00b7 " + tripStats2.rocketsUsed() + " rockets \u00b7 " + tripStats2.xpUsed() + " XP bottles"
      );
      this.centeredTitleText = this.loggedStartText;
   }

   private void handleFlaskCube(float flaskCubeValue, float matrixStackValue, float flaskCubeBound, float flaskCubeValue3, float flaskCubeValue4, int cardMatrixValue, int releaseRightValue, float matrixStackFactor, boolean flaskCubeFlag, float arrivalScreenLimit) {
      float flaskCubeValue9 = Math.min(1.0F, flaskCubeBound) * 48.0F;
      int flaskCubeLimit = Math.min(48, (int)flaskCubeValue9);
      float deltaVar = flaskCubeValue9 - flaskCubeLimit;
      Matrix4f positionMatrix = MatrixPeekUtilsRender.getMatrixStack().peek().getPositionMatrix();
      VertexConsumer quads = VertexGuiUtils.getQuads();

      for (int i = 0; i < 48 && i <= flaskCubeLimit; i++) {
         float flaskCubeFactor = i < flaskCubeLimit ? 1.0F : deltaVar;
         if (flaskCubeFactor <= 0.0F) {
            break;
         }

         if (!flaskCubeFlag || (i & 1) != 1) {
            int index = i + 1;
            float vertexColorValue = flaskCubeValue + CUBE_SCREEN_X_TABLE[i];
            float flaskCubeValue11 = matrixStackValue + cubeFlaskAmounts[i];
            float flaskCubeValue12 = flaskCubeValue + CUBE_SCREEN_X_TABLE[i] + (CUBE_SCREEN_X_TABLE[index] - CUBE_SCREEN_X_TABLE[i]) * flaskCubeFactor;
            float flaskCubeValue13 = matrixStackValue + cubeFlaskAmounts[i] + (cubeFlaskAmounts[index] - cubeFlaskAmounts[i]) * flaskCubeFactor;
            float flaskCubeValue14 = CUBE_PERSPECTIVE_TABLE[i] + (CUBE_PERSPECTIVE_TABLE[index] - CUBE_PERSPECTIVE_TABLE[i]) * flaskCubeFactor;
            float flaskCubeValue15 = flaskCubeTable[i] + (flaskCubeTable[index] - flaskCubeTable[i]) * flaskCubeFactor;
            int flaskCubeValue16 = getValue(i / 48.0F, cardMatrixValue, releaseRightValue, matrixStackFactor, arrivalScreenLimit);
            int flaskCubeValue17 = getValue((i + flaskCubeFactor) / 48.0F, cardMatrixValue, releaseRightValue, matrixStackFactor, arrivalScreenLimit);
            handleVertexColor(
               quads, positionMatrix, vertexColorValue, flaskCubeValue11, CUBE_PERSPECTIVE_TABLE[i], flaskCubeTable[i], flaskCubeValue16, flaskCubeValue12, flaskCubeValue13, flaskCubeValue14, flaskCubeValue15, flaskCubeValue17, flaskCubeValue3, flaskCubeValue4
            );
         }
      }

      VertexGuiUtils.updateBufferClose();
   }

   @Override
   public boolean mouseClicked(Click click, boolean mouseClickedFlag) {
      if (this.pressedActive) {
         return super.mouseClicked(click, mouseClickedFlag);
      } else if (this.lastNanoTimeWidthHeight != 0L) {
         return true;
      } else {
         this.titleServersHits.canAnimsPress(this.getMin(click.x(), this.width), this.getMin(click.y(), this.height));
         return true;
      }
   }

   private void handleTexts(int index, String arrivalScreenText, String orDefault) {
      this.detailRawTexts[index] = CardStatusUtilsFlag.getOrDefault(arrivalScreenText);
      this.arrivalScreenTexts[index] = CardStatusUtilsFlag.getOrDefault(orDefault);
      this.identifiersTexts[index] = this.detailRawTexts[index];
      this.titleFormattedTexts[index] = this.arrivalScreenTexts[index];
   }

   private float getAmount() {
      float arrivalScreenValue = this.widthHeightAmount - 1300.0F;
      if (arrivalScreenValue < 0.0F) {
         return -9.0F;
      } else {
         float arrivalScreenLimit = arrivalScreenValue % 2600.0F / 1100.0F;
         return arrivalScreenLimit > 1.0F ? -9.0F : -0.2F + 1.4F * arrivalScreenLimit;
      }
   }

   @Override
   public boolean shouldPause() {
      return false;
   }

   private static void handleDisconnectLiteral(MinecraftClient client, TripStats tripStats) {
      try {
         ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
         if (networkHandler != null) {
            networkHandler.getConnection().disconnect(Text.literal(tripStats.formatTripDestinationSuccess()));
         }
      } catch (Throwable e) {
      }
   }

   private static float getAmountFloat() {
      return 139.5F;
   }

   private boolean canTranslatePush(float translatePushValue) {
      if (!(translatePushValue <= 0.002F) && this.translatePushCount < this.translatePushTable.length) {
         MatrixStack matrixStack = MatrixPeekUtilsRender.getMatrixStack();
         matrixStack.push();
         matrixStack.translate(0.0F, (1.0F - translatePushValue) * 4.0F, 0.0F);
         this.translatePushTable[this.translatePushCount++] = CorzRoundedUtils.getPanelOverlay(translatePushValue);
         return true;
      } else {
         return false;
      }
   }

   private Color getColor() {
      int value = CardMatrixUtils.getValue(getReleaseRight(), -1, 0.35);
      if (value != this.colorValue || this.color == WHITE) {
         this.colorValue = value;
         this.color = new Color(value, true);
      }

      return this.color;
   }

   @Override
   protected void init() {
      if (!this.arrivalScreenActive) {
         this.arrivalScreenActive = true;
         MinecraftClient.getInstance().onResolutionChanged();
      } else {
         this.serverTitleFlag = false;
         super.init();
      }
   }
}
