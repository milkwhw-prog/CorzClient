package com.corz.client.gui;

import com.corz.client.CorzClient;
import com.corz.client.render.CorzRoundedUtils;
import com.corz.client.render.MatrixEspUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.render.state.GuiRenderState;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.client.util.Window;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import com.corz.client.categories.settings.ClientSounds;

public abstract class GuiClickScreen extends Screen implements ScreenGuiApi, GuiClickApi, ButtonInspectsApi {
   private boolean guiClickActive;
   private boolean guiActive;
   private boolean screenClientActive;
   private DrawContext drawContext;
   private boolean hasInitialized;
   protected final PreviewRail previewRail;
   private final GuiRenderState guiRenderState;
   private final String guiClickText;
   private static boolean contextDrawActive;
   protected final Screen currentScreen;
   private final MatrixStack matrixStack = new MatrixStack();

   protected Screen getJoinFlag() {
      return this.currentScreen;
   }

   @Override
   public void render(DrawContext context, int guiClickValue, int guiClickValue2, float guiValue) {
      if (!this.screenClientActive && !this.guiActive) {
         this.handleRenderGui(guiValue);
         CorzClient.fontManager.setMatrixStack(this.matrixStack);
         MatrixPeekUtilsRender.handleMatrixStack(this.matrixStack);
         CorzRoundedUtils.panelOverlayAmount = 1.0F;
         MatrixEspUtils.updateFlagTime();

         label43: {
            try {
               this.previewRail.renderAmountNon(context, this.width, this.height, guiClickValue, guiClickValue2);
               break label43;
            } catch (Throwable e) {
               this.updateScreenClient();
            } finally {
               CorzRoundedUtils.panelOverlayAmount = 1.0F;
            }

            return;
         }

         if (this.previewRail.isButtonActive()) {
            this.updateScreenClient();
         }
      }
   }

   @Override
   public boolean keyPressed(KeyInput keyInput) {
      return this.previewRail.canNonFlag(keyInput.key(), keyInput.modifiers()) ? true : super.keyPressed(keyInput);
   }

   protected final void updateRailPreview() {
      this.previewRail.updateFlagAmount();
   }

   @Override
   public boolean mouseClicked(Click click, boolean mouseClickedFlag) {
      this.previewRail.canButtonMin(click.x(), click.y(), click.button());
      return true;
   }

   @Override
   public void close() {
      this.previewRail.updateFlagAmount();
   }

   @Override
   public boolean charTyped(CharInput charInput) {
      return this.previewRail.isFlag() && this.canMatrixStack(charInput.codepoint(), charInput.modifiers()) ? true : super.charTyped(charInput);
   }

   @Override
   public String formatText() {
      return this.guiClickText;
   }

   public Screen getCurrentScreen() {
      return this.currentScreen;
   }

   protected void updateTimeMax() {
   }

   private int getScaleFactor() {
      Window window = this.client.getWindow();
      return window.calculateScaleFactor(this.client.options.getGuiScale().getValue(), this.client.forcesUnicodeFont());
   }

   private void handleRenderGui(float guiValue) {
      if (this.isScreenFlag()) {
         this.guiActive = true;

         try {
            if (this.drawContext == null) {
               this.drawContext = new DrawContext(this.client, this.guiRenderState, -10000, -10000);
            }

            this.guiRenderState.clear();
            this.currentScreen.render(this.drawContext, -10000, -10000, guiValue);
         } catch (Throwable e) {
            this.handleContextDraw(e);
         } finally {
            this.guiActive = false;
            this.guiRenderState.clear();
            CorzRoundedUtils.panelOverlayAmount = 1.0F;
         }
      }
   }

   @Override
   public void renderBackground(DrawContext context, int backgroundValue, int backgroundValue2, float backgroundValue3) {
   }

   private void updateScreenClient() {
      if (!this.screenClientActive) {
         this.screenClientActive = true;

         try {
            this.updateMatrixStack3();
         } finally {
            Screen joinFlag = this.getJoinFlag();
            if (joinFlag == this.currentScreen && this.currentScreen instanceof MatrixMatricesScreen matrixMatricesScreen) {
               matrixMatricesScreen.updateFlag();
            }

            this.client.setScreen(joinFlag);
            handleScaleFactor(this.client);
         }
      }
   }

   @Override
   public boolean mouseDragged(Click click, double mouseDraggedValue, double mouseDraggedValue2) {
      return this.previewRail.canCloseFlag(click.x(), click.y(), click.button(), mouseDraggedValue, mouseDraggedValue2);
   }

   @Override
   public void removed() {
      super.removed();
      if (!this.screenClientActive) {
         MinecraftClient client = this.client;
         client.send(() -> handleScaleFactor(client));
      }
   }

   private boolean isScreenFlag() {
      return !contextDrawActive && this.currentScreen instanceof GuiClickApi && this.currentScreen != this;
   }

   @Override
   public boolean mouseReleased(Click click) {
      this.previewRail.canPreviewClose(click.x(), click.y(), click.button());
      return true;
   }

   protected GuiClickScreen(Screen currentScreen, String guiClickText) {
      super(Text.literal(guiClickText == null ? "" : guiClickText));
      this.guiRenderState = new GuiRenderState();
      this.currentScreen = currentScreen;
      this.guiClickText = guiClickText == null ? "" : guiClickText;
      this.previewRail = new PreviewRail(this);
   }

   @Override
   public boolean mouseScrolled(double mouseScrolledValue, double amountMaxForDouble, double mouseScrolledValue3, double consumerLimit) {
      return this.previewRail.canMaxAmount(mouseScrolledValue, amountMaxForDouble, consumerLimit);
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

   @Override
   public boolean shouldPause() {
      return false;
   }

   protected void updateMatrixStack3() {
   }

   @Override
   protected void init() {
      if (!this.hasInitialized) {
         this.hasInitialized = true;
         if (this.getScaleFactor() != this.client.getWindow().getScaleFactor()) {
            this.client.onResolutionChanged();
            return;
         }
      }

      this.previewRail.updateMax();
      if (this.isScreenFlag() && (this.currentScreen.width != this.width || this.currentScreen.height != this.height)) {
         try {
            this.currentScreen.resize(this.width, this.height);
         } catch (Throwable e) {
            this.handleContextDraw(e);
         }
      }

      if (!this.guiClickActive) {
         this.guiClickActive = true;
         this.previewRail.handleFlagTime(this.isScreenFlag());
         ClientSounds.updateCardOpen();
         this.updateTimeMax();
      }
   }

   private void handleContextDraw(Throwable throwable) {
      contextDrawActive = true;
      this.drawContext = null;
   }
}
