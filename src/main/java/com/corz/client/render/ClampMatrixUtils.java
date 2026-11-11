package com.corz.client.render;

import com.corz.client.entity.CharacterReleaseUtils;
import com.corz.client.gui.DisabledEnabled;
import com.corz.client.gui.MatrixMatricesScreen;
import com.corz.client.gui.PanelGuiUtils;
import com.mojang.blaze3d.textures.GpuTextureView;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.client.gl.Framebuffer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.util.Window;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import com.corz.client.categories.movement.Freecam;
import com.corz.client.categories.settings.GUISettings;
import com.corz.client.categories.visuals.NameTags;

public final class ClampMatrixUtils {
   private static final ClampMatrixUtils.Rig CAMERA_RIG = new ClampMatrixUtils.Rig();
   private static float allowedCameraDistance;
   private static float lerpAmount;
   private static final ClampMatrixUtils.Panel MATRIX_CLAMP = new ClampMatrixUtils.Panel();
   private static float translateAmount;
   private static ClampMatrixUtils.Panel matrixClamp;
   private static float clampAmount;
   private static float frameAmount;
   private static final List<ClampMatrixUtils.Panel> PANELS = new ArrayList<>();
   private static boolean frameFlag;
   private static float matrixAmount;
   private static boolean panelsActive;
   private static float clamp;
   private static float vectorAmount;
   private static boolean matrixActive;
   private static float clampMatrixAmount;
   private static final List<ClampMatrixUtils.Overlay> OVERLAYS = new ArrayList<>();
   private static float frameTargetProgress;
   private static float lastRectX;
   private static final Matrix4f MATRIX4_FMATRIX4F = new Matrix4f();
   private static float panelsFrameAmount;
   private static double scaleYAmount;
   private static float wrapDegrees;
   private static final Matrix4f MATRIX4F2 = new Matrix4f();
   private static final Matrix4f MATRIX4F = new Matrix4f();
   private static float lerpedPosY;
   private static float framePulse;
   private static float framePulseScaled;
   private static float viewHeight;
   private static float lastRectHeight;
   private static final Vector4f VECTOR4_FVECTOR4F = new Vector4f();
   private static final Vector4f VECTOR4F = new Vector4f();
   private static float clampContext;
   private static float clampFrame;
   private static float lastInputX;
   private static float rectHeight;
   private static long lastNanoTime;
   private static float offsetZ;
   private static float lerpedPosZ;
   private static double cachedRigZ;
   private static float cachedRigYaw;
   private static float frameYawOffset;
   private static final float[] lerpTable = new float[2];
   private static double cachedRigY;
   private static float rectY;
   private static float zoomDistance = 4.5F;
   private static float panelsAmount;
   private static long lastNanoTimeFrame;
   private static float panelsFrameAmountFrame;
   private static float lastRectY;
   private static float zoomTarget = 4.5F;
   private static float degrees = 70.0F;
   private static double cachedRigX;
   private static float lastInputY;
   private static boolean degreesActive = true;
   private static float framePitchOffset;
   private static float vectorTransformAmount;
   private static boolean scaleXActive;
   private static boolean clampActive;
   private static boolean translateActive;
   private static float viewWidth;
   private static ClampMatrixUtils.Panel matrixClamp3ClampMatrix;
   private static final int UNIT_VALUE = 1;
   private static boolean frameActive;
   private static boolean clampMatrixActive;
   private static float panelZOffset;
   private static boolean overlayShown;
   private static float projectedY;
   private static float degreesAmount = 1.0F;
   private static float rectX;
   private static float clampBlend = 1.0F;
   private static boolean matrixPrepared;
   private static float frameAnimProgress;
   private static float clampMatrix;
   private static float followYawOffset;
   private static boolean lerpActive;
   private static float cachedInputAmount = -1.0F;
   private static float lastYawDegrees;
   private static float contextAmount;
   private static boolean framePanelsActive;
   private static float pulseElapsedMs = -1.0F;
   private static boolean contextActive;
   private static double scaleXAmount;
   private static float panelMoveBlend = 1.0F;
   private static float frameDimFactor = 0.3F;
   private static boolean clampDisabled;
   private static float pitchTargetDegrees;
   private static ClampMatrixUtils.Overlay handlerPanelsButton;
   private static int frameCount;
   private static int lastActiveFrameIndex = -10;
   private static KawaseGpu kawaseGpu;
   private static float hoveredPanelX = -100000.0F;
   private static float hoveredPanelY = -100000.0F;
   private static float lerpedPosX;
   private static float frameSlideOffset;
   private static int buttonClampValue = 0;
   private static float offsetX;
   private static boolean toggleHandled;
   private static int button = -1;
   private static DrawContext drawContext;

   public static void handleClampMatrix(ClampMatrixUtils.Panel panel) {
      PANELS.remove(panel);
      if (matrixClamp3ClampMatrix == panel) {
         matrixClamp3ClampMatrix = null;
         if (buttonClampValue == 2) {
            buttonClampValue = 0;
         }
      }

      if (matrixClamp == panel) {
         matrixClamp = null;
      }
   }

   public static float getAmount() {
      return lerpedPosY;
   }

   public static boolean canAmountFlag(ClampMatrixUtils.Panel panel, double clampMatrixValue, double modeValue, float[] clampMatrixValues) {
      if (panel == null || !panel.matrixActive || clampMatrixValues == null || clampMatrixValues.length < 2) {
         return false;
      } else if (!canVectorTransform(panel, clampMatrixValue, modeValue)) {
         return false;
      } else {
         clampMatrixValues[0] = panelsAmount;
         clampMatrixValues[1] = vectorTransformAmount;
         return true;
      }
   }

   public static void handleClampAmount(float value, int clampFactor) {
      clampContext = MathHelper.clamp(value, 0.0F, 1.0F);
      contextAmount = (int)(clampContext * clampFactor) / 255.0F;
   }

   private static void updateAmountFrame() {
      long nanoTime = System.nanoTime();
      if (lastNanoTimeFrame == 0L) {
         lastNanoTimeFrame = nanoTime;
      }

      clamp = lastNanoTime == 0L ? 0.0F : MathHelper.clamp((float)(nanoTime - lastNanoTime) / 1.0E9F, 0.0F, 0.05F);
      lastNanoTime = nanoTime;
      MinecraftClient instance = MinecraftClient.getInstance();
      if (instance.world != null && instance.player != null) {
         boolean canSleepingCamera3 = canSleepingCamera(instance);
         if (canSleepingCamera3 != frameFlag) {
            frameFlag = canSleepingCamera3;
            panelsFrameAmountFrame = panelsFrameAmount;
            frameTargetProgress = canSleepingCamera3 ? 1.0F : 0.0F;
            float frameBound = Math.abs(frameTargetProgress - panelsFrameAmountFrame);
            frameAmount = (canSleepingCamera3 ? 550.0F : 400.0F) * Math.max(0.4F, frameBound);
            frameAnimProgress = 0.0F;
            frameActive = frameBound > 0.0F;
            buttonClampValue = 0;
            matrixClamp3ClampMatrix = null;
            handlerPanelsButton = null;
            button = -1;
            if (canSleepingCamera3) {
               CharacterReleaseUtils.updateClampMatrixModeActive();
               if (panelsFrameAmount <= 0.02F) {
                  frameDimFactor = 0.3F;
                  degreesActive = true;
                  pulseElapsedMs = 0.0F;
               }
            } else {
               pulseElapsedMs = -1.0F;
            }
         }

         if (frameActive) {
            frameAnimProgress = frameAnimProgress + clamp * 1000.0F / frameAmount;
            if (frameAnimProgress >= 1.0F) {
               panelsFrameAmount = frameTargetProgress;
               frameActive = false;
            } else {
               panelsFrameAmount = panelsFrameAmountFrame + (frameTargetProgress - panelsFrameAmountFrame) * (frameFlag ? getClampForFloat(frameAnimProgress) : getClamp(frameAnimProgress));
            }
         }

         boolean frameFlag4 = frameFlag || panelsFrameAmount > 0.0F && MatrixMatricesScreen.isMatrixMatricesActive();
         frameDimFactor = (float)MatrixIdentifierUtils.getAmount(frameDimFactor, frameFlag4 ? 0.3F : 1.0, 10.0, clamp);
         zoomDistance = (float)MatrixIdentifierUtils.getAmount(zoomDistance, zoomTarget, 9.0, clamp);
         followYawOffset = (float)MatrixIdentifierUtils.getAmount(followYawOffset, clampFrame, 12.0, clamp);
         clampMatrixAmount = (float)MatrixIdentifierUtils.getAmount(clampMatrixAmount, pitchTargetDegrees, 12.0, clamp);
         float frameValue = 0.0F;
         float clamp2 = 0.0F;
         if (panelsFrameAmount > 0.0F && viewWidth > 1.0F && viewHeight > 1.0F) {
            frameValue = MathHelper.clamp((float)(getScaleX() / viewWidth) * 2.0F - 1.0F, -1.0F, 1.0F);
            clamp2 = MathHelper.clamp((float)(getScaleY() / viewHeight) * 2.0F - 1.0F, -1.0F, 1.0F);
         }

         frameYawOffset = (float)MatrixIdentifierUtils.getAmount(frameYawOffset, frameValue * 2.5F, 5.0, clamp);
         framePitchOffset = (float)MatrixIdentifierUtils.getAmount(framePitchOffset, -clamp2 * 2.5F, 5.0, clamp);
         framePulseScaled = 0.0F;
         framePulse = 0.0F;
         if (pulseElapsedMs >= 0.0F) {
            pulseElapsedMs = pulseElapsedMs + clamp * 1000.0F;
            float frameFactor = (pulseElapsedMs - 300.0F) / 860.0F;
            if (frameFactor >= 1.0F) {
               pulseElapsedMs = -1.0F;
            } else if (frameFactor > 0.0F) {
               float frameValue3 = frameFactor * frameFactor * (3.0F - 2.0F * frameFactor);
               frameSlideOffset = -0.35F + 2.15F * frameValue3;
               framePulse = (float)Math.sin(Math.PI * frameFactor) * panelsFrameAmount;
               framePulseScaled = 0.3F * framePulse;
            }
         }
      } else {
         frameFlag = false;
         frameActive = false;
         panelsFrameAmount = 0.0F;
         buttonClampValue = 0;
      }
   }

   private static void handleAmountPanels(double panelsValue, double scaleY) {
      boolean panelsFlag = buttonClampValue == 4 || buttonClampValue == 0 && getHandlerForDouble(panelsValue, scaleY) != null;
      ClampMatrixUtils.Panel panel3 = null;

      for (int i = PANELS.size() - 1; i >= 0; i--) {
         ClampMatrixUtils.Panel panel = PANELS.get(i);
         panel.panelsActive = false;
         if (panel.matrixActive && panel.clampMatrixActive && canVectorTransform(panel, panelsValue, scaleY)) {
            panel.panelsAmount = panelsAmount;
            panel.panelsClampAmount = vectorTransformAmount;
            if (panel3 == null
               && panelsAmount >= panel.clampMatrixAmount
               && panelsAmount <= panel.clampMatrixAmount + panel.matrixAmount
               && vectorTransformAmount >= panel.panelY
               && vectorTransformAmount <= panel.panelY + panel.panelHeight) {
               panel3 = panel;
            }
         } else {
            panel.panelsAmount = -100000.0F;
            panel.panelsClampAmount = -100000.0F;
         }
      }

      if (buttonClampValue == 2 && matrixClamp3ClampMatrix != null) {
         panel3 = matrixClamp3ClampMatrix;
      }

      if (buttonClampValue == 1 || panelsFlag) {
         panel3 = null;
      }

      if (panel3 != null) {
         panel3.panelsActive = true;
      }

      matrixClamp = panel3;
      if (canVectorTransform(MATRIX_CLAMP, panelsValue, scaleY)) {
         float panelsValue3 = viewWidth * 0.5F;
         float panelsValue4 = viewHeight * 0.5F;
         float panelsValue5 = panelsValue3 + (rectX - panelsValue3) * panelMoveBlend;
         float panelsValue6 = panelsValue4 + (rectY - panelsValue4) * panelMoveBlend;
         float panelsLimit = panelsValue3 + (rectX + clampAmount - panelsValue3) * panelMoveBlend;
         float panelsLimit2 = panelsValue4 + (rectY + rectHeight - panelsValue4) * panelMoveBlend;
         panelsActive = !clampActive || panelsAmount >= panelsValue5 && panelsAmount <= panelsLimit && vectorTransformAmount >= panelsValue6 && vectorTransformAmount <= panelsLimit2;
         hoveredPanelX = panelsAmount;
         hoveredPanelY = vectorTransformAmount;
      } else {
         panelsActive = false;
         hoveredPanelX = -100000.0F;
         hoveredPanelY = -100000.0F;
      }

      if (panel3 != null || buttonClampValue == 3 || panelsFlag) {
         hoveredPanelX = -100000.0F;
         hoveredPanelY = -100000.0F;
         if (panel3 != null || panelsFlag) {
            panelsActive = false;
         }
      }
   }

   public static void renderPanelsAmount(DrawContext context) {
      if (!PANELS.isEmpty() && FramebufferSimpleUtils.isCompositeBegin() && !(panelsFrameAmount <= 0.0F)) {
         ClampMatrixUtils.Panel panel = null;
         boolean panelsFlag = false;

         try {
            for (int i = 0; i < PANELS.size(); i++) {
               ClampMatrixUtils.Panel panel2 = PANELS.get(i);
               if (panel2.clampMatrixActive && !(panel2.matrixAmount <= 0.0F) && !(panel2.panelHeight <= 0.0F)) {
                  if (!panelsFlag) {
                     if (!FramebufferSimpleUtils.isFramebufferSimple()) {
                        return;
                     }

                     panelsFlag = true;
                  }

                  try {
                     panel2.render(context, panel2.panelsAmount, panel2.panelsClampAmount, panelsFrameAmount);
                  } catch (Throwable e) {
                     panel = panel2;
                     break;
                  }
               }
            }
         } catch (Throwable e2) {
            handleFrameClamp("panels", e2);
         } finally {
            if (panelsFlag) {
               FramebufferSimpleUtils.updateFramebufferSimpleBeginCompositeFlag();
            }
         }

         if (panel != null) {
            handleClampMatrix(panel);
         }
      }
   }

   public static void updateFlag() {
      contextActive = true;
   }

   public static float getViewWidth() {
      return viewWidth;
   }

   public static float getPanelsFrame() {
      return panelsFrameAmount;
   }

   public static void handleClamp(float value, float value2) {
      clampFrame = MathHelper.clamp(value, -45.0F, 45.0F);
      pitchTargetDegrees = MathHelper.clamp(value2, -12.0F, 28.0F) - 8.0F;
   }

   public static boolean canClampMatrix(ClampMatrixUtils.Panel panel) {
      return panel != null && buttonClampValue == 2 && matrixClamp3ClampMatrix == panel;
   }

   public static int getFrame() {
      return frameCount;
   }

   static void handleAmountMatrix(Framebuffer framebuffer, Framebuffer framebuffer2) {
      MinecraftClient instance = MinecraftClient.getInstance();
      Framebuffer framebuffer3 = instance.getFramebuffer();
      Window window = instance.getWindow();
      float scaleFactor = window.getScaleFactor();
      int framebufferWidth = window.getFramebufferWidth();
      int framebufferHeight = window.getFramebufferHeight();
      float amount = framebufferWidth / scaleFactor;
      float amount2 = framebufferHeight / scaleFactor;
      if (!(amount <= 1.0F) && !(amount2 <= 1.0F) && framebuffer != null) {
         viewWidth = amount;
         viewHeight = amount2;
         float overlayValue = panelsFrameAmount;
         MATRIX4_FMATRIX4F.setPerspective(2.0F * (float)Math.atan(amount2 * 0.5F / 700.0F), amount / amount2, 35.0F, 14000.0F).translate(0.0F, 0.0F, -700.0F);
         MATRIX4F2.set(MATRIX4_FMATRIX4F).scale(1.0F, -1.0F, 1.0F).translate(-amount * 0.5F, -amount2 * 0.5F, 0.0F);
         int releaseRightValue = MatrixEspUtils.releaseRightValue;
         float matrixValue = (releaseRightValue >> 16 & 0xFF) / 255.0F;
         float matrixValue2 = (releaseRightValue >> 8 & 0xFF) / 255.0F;
         float matrixValue3 = (releaseRightValue & 0xFF) / 255.0F;
         float matrixValue4 = 1.0F + (matrixValue - 1.0F) * 0.22F;
         float matrixValue5 = 1.0F + (matrixValue2 - 1.0F) * 0.22F;
         float matrixValue6 = 1.0F + (matrixValue3 - 1.0F) * 0.22F;
         float seconds = (float)(System.nanoTime() - lastNanoTimeFrame) / 1.0E9F;
         boolean matrixFlag = !clampActive || overlayValue <= 0.0F;
         float matrixValue7 = amount * 0.5F;
         float matrixValue8 = amount2 * 0.5F;
         float matrixValue9 = matrixValue7 + (rectX - matrixValue7) * panelMoveBlend;
         float matrixValue10 = matrixValue8 + (rectY - matrixValue8) * panelMoveBlend;
         float matrixValue11 = matrixValue7 + (rectX + clampAmount - matrixValue7) * panelMoveBlend;
         float matrixValue12 = matrixValue8 + (rectY + rectHeight - matrixValue8) * panelMoveBlend;
         float matrixValue13 = panelZOffset * panelMoveBlend;
         if (matrixFlag) {
            MATRIX_CLAMP.modelMatrix.set(MATRIX4F2);
         } else {
            handleAmountTranslate(amount, amount2);
            float matrixValue14 = (matrixValue9 + matrixValue11) * 0.5F - matrixValue7;
            float matrixValue15 = matrixValue8 - (matrixValue10 + matrixValue12) * 0.5F;
            float clamp = MathHelper.clamp(8.0F + clampMatrixAmount, -12.0F, 28.0F);
            float matrixFactor = (-18.0F + followYawOffset * 0.1F + frameYawOffset) * overlayValue;
            float matrixFactor2 = (5.0F + (clamp - 8.0F) * 0.18F + framePitchOffset) * overlayValue;
            float matrixValue16 = MathHelper.sin(seconds * (float) (Math.PI * 2) / 7.0F) * 0.6F * overlayValue;
            float matrixValue17 = MathHelper.sin(seconds * (float) (Math.PI * 2) / 11.2F + 1.3F) * 0.6F * 0.55F * overlayValue;
            MATRIX_CLAMP.modelMatrix
               .set(MATRIX4_FMATRIX4F)
               .translate(matrixValue14 + offsetX * overlayValue + matrixValue17, matrixValue15 + matrixAmount * overlayValue + matrixValue16, offsetZ * overlayValue)
               .rotateY(matrixFactor * (float) (Math.PI / 180.0))
               .rotateX(matrixFactor2 * (float) (Math.PI / 180.0))
               .translate(-matrixValue14, -matrixValue15, 0.0F)
               .scale(1.0F, -1.0F, 1.0F)
               .translate(-matrixValue7, -matrixValue8, 0.0F);
         }

         MATRIX_CLAMP.matrixMatrix4f.set(MATRIX_CLAMP.modelMatrix).invert();
         MATRIX_CLAMP.matrixActive = true;
         GpuTextureView gpuTextureView = framebuffer2 != null ? framebuffer2.getColorAttachmentView() : null;
         float clamp3 = MathHelper.clamp((overlayValue - 0.2F) / 0.8F, 0.0F, 1.0F);
         float matrixFactor3 = clamp3 * clamp3 * (3.0F - 2.0F * clamp3);
         boolean matrixFlag2 = clampActive && matrixActive && clampMatrix > 0.02F;

         for (int i = 0; i < PANELS.size(); i++) {
            ClampMatrixUtils.Panel panel = PANELS.get(i);
            panel.renderAlpha = panel.baseAlpha * matrixFactor3;
            if (!matrixFlag && gpuTextureView != null && panel.clampMatrixActive && !(panel.matrixAmount <= 0.0F) && !(panel.panelHeight <= 0.0F) && !(panel.renderAlpha <= 0.003F)) {
               float matrixValue18 = 700.0F / Math.max(0.05F, panel.clamp);
               float matrixFactor4 = matrixValue18 / 700.0F;
               float matrixValue19 = (1.0F - matrixFactor3) * -18.0F;
               float matrixValue20 = MathHelper.sin(seconds * (float) (Math.PI * 2) / 8.05F + 1.7F * (i + 1)) * 0.6F * overlayValue;
               float x = (panel.modeAmount + matrixValue19 - matrixValue7) * matrixFactor4;
               float matrixValue21 = (matrixValue8 - panel.panelCenterY) * matrixFactor4;
               panel.modelMatrix
                  .set(MATRIX4_FMATRIX4F)
                  .translate(x, matrixValue21 + matrixValue20, 700.0F - matrixValue18)
                  .rotateY((panel.panelAmount + frameYawOffset * 0.8F) * overlayValue * (float) (Math.PI / 180.0))
                  .rotateX((panel.tiltDegrees + framePitchOffset * 0.8F) * overlayValue * (float) (Math.PI / 180.0))
                  .scale(1.0F, -1.0F, 1.0F)
                  .translate(-(panel.clampMatrixAmount + panel.matrixAmount * 0.5F), -(panel.panelY + panel.panelHeight * 0.5F), 0.0F);
               panel.matrixMatrix4f.set(panel.modelMatrix).invert();
               panel.matrixActive = true;
               if (panel.modernThemeAmount > 0.02F) {
                  matrixFlag2 = true;
               }
            } else {
               panel.matrixActive = false;
            }
         }

         GpuTextureView kawaseFinal2 = null;
         if (matrixFlag2 && !matrixPrepared) {
            try {
               if (kawaseGpu == null) {
                  kawaseGpu = new KawaseGpu();
               }

               kawaseFinal2 = kawaseGpu.getKawaseFinal(framebuffer3.getColorAttachment(), framebuffer3.getColorAttachmentView(), framebufferWidth, framebufferHeight, 4, 2.5F);
            } catch (Throwable e) {
               matrixPrepared = true;
               kawaseFinal2 = null;
            }
         }

         GpuTextureView colorAttachmentView = framebuffer.getColorAttachmentView();
         PanelGuiUtils.updatePanelSampler();
         float matrixLimit = contextAmount * frameDimFactor;
         float matrixLimit2 = clampContext * 0.26F * overlayValue;
         if (matrixLimit > 5.0E-4F || matrixLimit2 > 5.0E-4F) {
            PanelGuiUtils.handlePanelSampler(
               colorAttachmentView,
               MATRIX4F2,
               0.0F,
               0.0F,
               amount,
               amount2,
               0.0F,
               1.0F,
               1.0F,
               0.0F,
               0.0F,
               0.0F,
               amount,
               amount2,
               1.0F,
               0.0F,
               0.0F,
               1.0F,
               0.0F,
               0.0F,
               0.0F,
               0.0F,
               0.0F,
               2.0F,
               0.0F,
               1.0F,
               0.0F,
               matrixLimit,
               matrixLimit2,
               0.0F,
               0.0F,
               0.0F,
               0.0F,
               0.0F
            );
         }

         if (matrixFlag) {
            if (kawaseFinal2 != null && clampActive && matrixActive) {
               PanelGuiUtils.handlePanelSampler(
                  kawaseFinal2,
                  MATRIX4F2,
                  matrixValue9,
                  matrixValue10,
                  matrixValue11,
                  matrixValue12,
                  framebufferWidth,
                  framebufferHeight,
                  0.0F,
                  0.0F,
                  matrixValue9,
                  matrixValue10,
                  matrixValue11,
                  matrixValue12,
                  clampMatrix,
                  0.0F,
                  0.0F,
                  1.0F,
                  0.0F,
                  0.0F,
                  0.0F,
                  0.0F,
                  matrixValue13,
                  3.0F,
                  1.0F,
                  1.0F,
                  0.0F,
                  0.0F,
                  0.0F,
                  0.0F,
                  0.0F,
                  0.0F,
                  0.0F,
                  0.0F
               );
            }

            PanelGuiUtils.handlePanelSampler(
               colorAttachmentView,
               MATRIX4F2,
               0.0F,
               0.0F,
               amount,
               amount2,
               0.0F,
               1.0F,
               1.0F,
               0.0F,
               0.0F,
               0.0F,
               amount,
               amount2,
               1.0F,
               0.0F,
               0.0F,
               1.0F,
               0.0F,
               0.0F,
               0.0F,
               0.0F,
               0.0F,
               0.0F,
               0.0F,
               1.0F,
               1.0F,
               0.0F,
               0.0F,
               0.0F,
               0.0F,
               0.0F,
               0.0F,
               0.0F
            );
         } else {
            float matrixValue22 = clampMatrix * overlayValue;
            float matrixValue23 = 54.4F;
            PanelGuiUtils.handlePanelSampler(
               colorAttachmentView,
               MATRIX_CLAMP.modelMatrix,
               matrixValue9 - matrixValue23,
               matrixValue10 - matrixValue23,
               matrixValue11 + matrixValue23,
               matrixValue12 + matrixValue23 + 10.0F,
               0.0F,
               0.0F,
               1.0F,
               1.0F,
               matrixValue9,
               matrixValue10,
               matrixValue11,
               matrixValue12,
               1.0F,
               0.0F,
               0.0F,
               1.0F,
               0.0F,
               0.0F,
               0.0F,
               0.0F,
               matrixValue13,
               1.0F,
               0.0F,
               1.0F,
               0.0F,
               0.46F * matrixValue22,
               34.0F,
               10.0F * overlayValue,
               matrixValue,
               matrixValue2,
               matrixValue3,
               0.085F * matrixValue22 * (1.0F + 2.2F * framePulse)
            );
            if (kawaseFinal2 != null && matrixActive) {
               PanelGuiUtils.handlePanelSampler(
                  kawaseFinal2,
                  MATRIX_CLAMP.modelMatrix,
                  matrixValue9,
                  matrixValue10,
                  matrixValue11,
                  matrixValue12,
                  framebufferWidth,
                  framebufferHeight,
                  0.0F,
                  0.0F,
                  matrixValue9,
                  matrixValue10,
                  matrixValue11,
                  matrixValue12,
                  clampMatrix,
                  0.0F,
                  0.0F,
                  1.0F,
                  0.0F,
                  0.0F,
                  0.0F,
                  0.0F,
                  matrixValue13,
                  3.0F,
                  1.0F,
                  1.0F,
                  0.0F,
                  0.0F,
                  0.0F,
                  0.0F,
                  0.0F,
                  0.0F,
                  0.0F,
                  0.0F
               );
            }

            float max24 = Math.max(0.0F, matrixValue9 - 26.0F);
            float max25 = Math.max(0.0F, matrixValue10 - 26.0F);
            float min26 = Math.min(amount, matrixValue11 + 26.0F);
            float min27 = Math.min(amount2, matrixValue12 + 26.0F);
            PanelGuiUtils.handlePanelSampler(
               colorAttachmentView,
               MATRIX_CLAMP.modelMatrix,
               max24,
               max25,
               min26,
               min27,
               max24 / amount,
               1.0F - max25 / amount2,
               min26 / amount,
               1.0F - min27 / amount2,
               matrixValue9,
               matrixValue10,
               matrixValue11,
               matrixValue12,
               1.0F,
               0.3F * matrixValue22 * (1.0F + 1.4F * framePulse),
               frameSlideOffset,
               0.075F,
               matrixValue4,
               matrixValue5,
               matrixValue6,
               framePulseScaled * clampMatrix,
               matrixValue13,
               0.0F,
               clampDisabled ? 0.0F : 1.0F,
               15.6F,
               0.0F,
               0.0F,
               0.0F,
               0.0F,
               0.0F,
               0.0F,
               0.0F,
               0.0F
            );
         }

         for (int j = 0; j < PANELS.size(); j++) {
            ClampMatrixUtils.Panel panel2 = PANELS.get(j);
            if (panel2.matrixActive) {
               float renderAlpha = panel2.renderAlpha;
               float clampMatrixAmount24 = panel2.clampMatrixAmount;
               float panelY = panel2.panelY;
               float matrixValue28 = panel2.clampMatrixAmount + panel2.matrixAmount;
               float matrixValue29 = panel2.panelY + panel2.panelHeight;
               float matrixValue30 = 34.0F;
               PanelGuiUtils.handlePanelSampler(
                  gpuTextureView,
                  panel2.modelMatrix,
                  clampMatrixAmount24 - matrixValue30,
                  panelY - matrixValue30,
                  matrixValue28 + matrixValue30,
                  matrixValue29 + matrixValue30 + 6.0F,
                  0.0F,
                  0.0F,
                  1.0F,
                  1.0F,
                  clampMatrixAmount24,
                  panelY,
                  matrixValue28,
                  matrixValue29,
                  renderAlpha,
                  0.0F,
                  0.0F,
                  1.0F,
                  0.0F,
                  0.0F,
                  0.0F,
                  0.0F,
                  panel2.cornerRadius,
                  1.0F,
                  0.0F,
                  1.0F,
                  0.0F,
                  0.368F,
                  20.400002F,
                  6.0F,
                  matrixValue,
                  matrixValue2,
                  matrixValue3,
                  0.0595F
               );
               if (kawaseFinal2 != null && panel2.modernThemeAmount > 0.02F) {
                  PanelGuiUtils.handlePanelSampler(
                     kawaseFinal2,
                     panel2.modelMatrix,
                     clampMatrixAmount24,
                     panelY,
                     matrixValue28,
                     matrixValue29,
                     framebufferWidth,
                     framebufferHeight,
                     0.0F,
                     0.0F,
                     clampMatrixAmount24,
                     panelY,
                     matrixValue28,
                     matrixValue29,
                     renderAlpha * Math.min(1.0F, panel2.modernThemeAmount),
                     0.0F,
                     0.0F,
                     1.0F,
                     0.0F,
                     0.0F,
                     0.0F,
                     0.0F,
                     panel2.cornerRadius,
                     3.0F,
                     1.0F,
                     1.0F,
                     0.0F,
                     0.0F,
                     0.0F,
                     0.0F,
                     0.0F,
                     0.0F,
                     0.0F,
                     0.0F
                  );
               }

               float matrixValue31 = 12.0F;
               float max32 = Math.max(0.0F, clampMatrixAmount24 - matrixValue31);
               float max33 = Math.max(0.0F, panelY - matrixValue31);
               float min34 = Math.min(amount, matrixValue28 + matrixValue31);
               float panelSamplerValue = Math.min(amount2, matrixValue29 + matrixValue31);
               PanelGuiUtils.handlePanelSampler(
                  gpuTextureView,
                  panel2.modelMatrix,
                  max32,
                  max33,
                  min34,
                  panelSamplerValue,
                  max32 / amount,
                  1.0F - max33 / amount2,
                  min34 / amount,
                  1.0F - panelSamplerValue / amount2,
                  clampMatrixAmount24,
                  panelY,
                  matrixValue28,
                  matrixValue29,
                  renderAlpha,
                  0.3F,
                  0.0F,
                  1.0F,
                  matrixValue4,
                  matrixValue5,
                  matrixValue6,
                  0.0F,
                  panel2.cornerRadius,
                  0.0F,
                  1.0F,
                  matrixValue31 * 0.6F,
                  0.0F,
                  0.0F,
                  0.0F,
                  0.0F,
                  0.0F,
                  0.0F,
                  0.0F,
                  0.0F
               );
            }
         }

         PanelGuiUtils.handleSamplerPanel(framebuffer3);
         lastActiveFrameIndex = frameCount;
         DrawContext context = drawContext;
         drawContext = null;
         if (context != null) {
            if (contextActive) {
               DisabledEnabled.getInstance().renderScaledWindow(context);
            }

            ClampMatrixUtils.Overlay overlay = null;

            for (int k = 0; k < OVERLAYS.size(); k++) {
               ClampMatrixUtils.Overlay overlay2 = OVERLAYS.get(k);

               try {
                  overlay2.render(context, overlayValue);
               } catch (Throwable e2) {
                  overlay = overlay2;
                  break;
               }
            }

            if (overlay != null) {
               OVERLAYS.remove(overlay);
            }
         }
      }
   }

   public static boolean isFrameFlag() {
      return !framePanelsActive && GUISettings.isInstanceBase();
   }

   public static void handleClamp(double clampValue, double clampValue2) {
      clampFrame = MathHelper.clamp(clampFrame + (float)clampValue * 0.3F, -45.0F, 45.0F);
      pitchTargetDegrees = MathHelper.clamp(pitchTargetDegrees + (float)clampValue2 * 0.22F, -20.0F, 20.0F);
   }

   public static boolean isFlag() {
      return lerpActive;
   }

   public static void handleAmountFlag(double amount, double amount2) {
      scaleXActive = true;
      scaleXAmount = amount;
      scaleYAmount = amount2;
   }

   private static float getClamp(float value) {
      value = MathHelper.clamp(value, 0.0F, 1.0F);
      return value < 0.5F ? 4.0F * value * value * value : 1.0F - (float)Math.pow(-2.0F * value + 2.0F, 3.0) / 2.0F;
   }

   public static boolean isFramePanels() {
      if (!framePanelsActive && (frameFlag || panelsFrameAmount > 0.0F)) {
         MinecraftClient instance = MinecraftClient.getInstance();
         return instance.currentScreen instanceof MatrixMatricesScreen && instance.world != null && instance.player != null;
      } else {
         return false;
      }
   }

   public static boolean isFramePanelsFramePanelsActive() {
      return !framePanelsActive && panelsFrameAmount > 0.0F;
   }

   public static void handleClamp(float value) {
      zoomTarget = MathHelper.clamp(value, 2.2F, 8.0F);
   }

   public static boolean isClampMatrix() {
      if (!isFramePanelsFlag()) {
         return true;
      } else {
         handleAmountPanels(getScaleX(), getScaleY());
         return panelsActive && matrixClamp == null;
      }
   }

   public static void handleFrameClamp(String frameClampText, Throwable throwable) {
      FramebufferSimpleUtils.updateCompositeBegin();
      boolean frameClampFlag = !framePanelsActive;
      framePanelsActive = true;
      frameFlag = false;
      frameActive = false;
      panelsFrameAmount = 0.0F;
      buttonClampValue = 0;
      matrixClamp3ClampMatrix = null;
      handlerPanelsButton = null;
      matrixClamp = null;
      drawContext = null;
      if (frameClampFlag && throwable != null) {
         throwable.printStackTrace();
      }
   }

   public static boolean isInstanceScreen() {
      return isFrameCamera() && CAMERA_RIG.degreesActive && MinecraftClient.getInstance().currentScreen instanceof MatrixMatricesScreen;
   }

   public static double getScaleX() {
      if (scaleXActive) {
         return scaleXAmount;
      } else {
         MinecraftClient instance = MinecraftClient.getInstance();
         return Mouse.scaleX(instance.getWindow(), instance.mouse.getX());
      }
   }

   public static ClampMatrixUtils.Panel getClampMatrix() {
      return MATRIX_CLAMP;
   }

   private static boolean canSleepingCamera(MinecraftClient client) {
      if (!GUISettings.isInstanceBase()) {
         return false;
      } else if (client.currentScreen instanceof MatrixMatricesScreen && !MatrixMatricesScreen.isMatrixMatricesActive()) {
         return client.getCameraEntity() == client.player && !Freecam.isInstanceFlag() && !client.player.isSleeping()
            ? PanelGuiUtils.isShaderValid() && !framePanelsActive
            : false;
      } else {
         return false;
      }
   }

   public static void handleHandler(ClampMatrixUtils.Overlay overlay) {
      OVERLAYS.remove(overlay);
      if (handlerPanelsButton == overlay) {
         handlerPanelsButton = null;
         if (buttonClampValue == 4) {
            buttonClampValue = 0;
         }
      }
   }

   public static float getClampAmount(float clampValue, float clampDivisor) {
      if (clampDivisor <= 0.001F) {
         return 0.0F;
      } else {
         float thumbAmount = MathHelper.clamp(clampValue / clampDivisor, 0.0F, 1.0F);
         degreesAmount = thumbAmount <= degreesAmount ? thumbAmount : (float)MatrixIdentifierUtils.getAmount(degreesAmount, thumbAmount, 7.0, clamp);
         return clampDivisor * degreesAmount;
      }
   }

   public static void handleClampAmount(float amount, float randomValue, float amount3, float amount4, float amount6, float amount7, float value, boolean flag) {
      rectX = amount;
      rectY = randomValue;
      clampAmount = amount3;
      rectHeight = amount4;
      panelZOffset = amount6;
      panelMoveBlend = amount7;
      clampMatrix = MathHelper.clamp(value, 0.0F, 1.0F);
      clampDisabled = flag;
      clampActive = true;
      MATRIX_CLAMP.clampMatrixAmount = amount;
      MATRIX_CLAMP.panelY = randomValue;
      MATRIX_CLAMP.matrixAmount = amount3;
      MATRIX_CLAMP.panelHeight = amount4;
      MATRIX_CLAMP.cornerRadius = amount6;
   }

   public static void handleClampMatrixForPanel(ClampMatrixUtils.Panel panel) {
      if (panel != null && panel != MATRIX_CLAMP && !PANELS.contains(panel)) {
         PANELS.add(panel);
      }
   }

   public static void handleClamp(double exponent) {
      zoomTarget = MathHelper.clamp(zoomTarget * (float)Math.pow(0.88F, exponent), 2.2F, 8.0F);
   }

   public static boolean canButtonClamp(int button2) {
      if (!isFramePanelsFlag()) {
         return false;
      } else if (buttonClampValue != 0 && button2 != ClampMatrixUtils.button) {
         return buttonClampValue != 1;
      } else {
         double scaleX = getScaleX();
         double scaleY = getScaleY();
         if (buttonClampValue == 0) {
            ClampMatrixUtils.Overlay handler = getHandlerForDouble(scaleX, scaleY);
            if (handler != null) {
               boolean buttonClampFlag = false;

               try {
                  buttonClampFlag = handler.canFlag(scaleX, scaleY, button2);
               } catch (Throwable e) {
                  handleHandler(handler);
               }

               if (buttonClampFlag) {
                  buttonClampValue = 4;
                  handlerPanelsButton = handler;
                  ClampMatrixUtils.button = button2;
                  return true;
               }
            }
         }

         handleAmountPanels(scaleX, scaleY);
         if (matrixClamp != null) {
            ClampMatrixUtils.Panel panel = matrixClamp;
            buttonClampValue = 2;
            matrixClamp3ClampMatrix = panel;
            ClampMatrixUtils.button = button2;

            try {
               panel.canFlag(panel.panelsAmount, panel.panelsClampAmount, button2);
            } catch (Throwable e2) {
               handleClampMatrix(panel);
            }

            return true;
         } else if (panelsActive) {
            buttonClampValue = 1;
            ClampMatrixUtils.button = button2;
            return false;
         } else if (button2 == 1) {
            buttonClampValue = 3;
            ClampMatrixUtils.button = button2;
            return true;
         } else {
            return false;
         }
      }
   }

   public static void renderFlag(DrawContext context, float clampMatrixValue) {
      if (!overlayShown && isInstanceScreen()) {
         try {
            CorzRoundedUtils.update();

            try {
               if (CharacterReleaseUtils.canModeToggle(context, clampMatrixValue, !toggleHandled)) {
                  clampMatrixActive = true;
               }
            } finally {
               CorzRoundedUtils.updateBufferCloseCount();
            }
         } catch (Throwable e) {
            overlayShown = true;
         }
      }
   }

   public static double getAmountFlag(double amountFlag, double clampMatrixValue, Window window) {
      if (!scaleXActive && !isFramePanelsFlag()) {
         return amountFlag;
      } else {
         double panelsValue = scaleXActive ? scaleXAmount : amountFlag;
         if (!isFramePanelsFlag()) {
            return panelsValue;
         } else {
            handleAmountPanels(panelsValue, scaleXActive ? scaleYAmount : Mouse.scaleY(window, clampMatrixValue));
            return hoveredPanelX;
         }
      }
   }

   public static double getScaleY() {
      if (scaleXActive) {
         return scaleYAmount;
      } else {
         MinecraftClient instance = MinecraftClient.getInstance();
         return Mouse.scaleY(instance.getWindow(), instance.mouse.getY());
      }
   }

   private static boolean canVectorAmount(Matrix4f matrix, float vectorValue, float vectorValue2, float vectorFactor, float vectorFactor2) {
      VECTOR4_FVECTOR4F.set(vectorValue, vectorValue2, 0.0F, 1.0F);
      matrix.transform(VECTOR4_FVECTOR4F);
      if (VECTOR4_FVECTOR4F.w <= 1.0E-5F) {
         vectorAmount = 0.0F;
         projectedY = 0.0F;
         return false;
      } else {
         vectorAmount = (VECTOR4_FVECTOR4F.x / VECTOR4_FVECTOR4F.w * 0.5F + 0.5F) * vectorFactor;
         projectedY = (0.5F - VECTOR4_FVECTOR4F.y / VECTOR4_FVECTOR4F.w * 0.5F) * vectorFactor2;
         return true;
      }
   }

   public static boolean isFlagFrame() {
      boolean clampMatrixActive72 = clampMatrixActive;
      clampMatrixActive = false;
      return clampMatrixActive72 && !framePanelsActive;
   }

   public static void updateFlagScaleXActive() {
      scaleXActive = false;
   }

   public static ClampMatrixUtils.Rig getAmountDegrees(
      Entity entity, float degreesValue, double degreesValue2, double degreesValue3, double degreesValue4, float degreesValue5, float degreesValue6
   ) {
      float panelsFrameAmount2 = panelsFrameAmount;
      ClampMatrixUtils.Rig amountDegrees = CAMERA_RIG;
      if (degreesActive) {
         degreesActive = false;
         float basePitch2 = degreesValue5;
         if (entity instanceof LivingEntity livingEntity) {
            float lerpAngleDegrees = MathHelper.lerpAngleDegrees(degreesValue, livingEntity.lastBodyYaw, livingEntity.bodyYaw);
            float yaw = entity.getYaw(degreesValue);
            basePitch2 = lerpAngleDegrees + 0.5F * MathHelper.wrapDegrees(yaw - lerpAngleDegrees);
         }

         wrapDegrees = MathHelper.wrapDegrees(basePitch2 + 202.0F - degreesValue5);
         lastYawDegrees = degreesValue5 + wrapDegrees;
         degreesAmount = 1.0F;
         clampBlend = 1.0F;
      }

      float wrapDegrees2 = lastYawDegrees - degreesValue5;

      while (wrapDegrees2 - wrapDegrees > 180.0F) {
         wrapDegrees2 -= 360.0F;
      }

      while (wrapDegrees2 - wrapDegrees < -180.0F) {
         wrapDegrees2 += 360.0F;
      }

      wrapDegrees = wrapDegrees2;
      float clamp = MathHelper.clamp(8.0F + clampMatrixAmount, -12.0F, 28.0F);
      amountDegrees.degreesAmount = degreesValue5 + (wrapDegrees2 + followYawOffset) * panelsFrameAmount2;
      amountDegrees.rigAmount = degreesValue6 + (clamp - degreesValue6) * panelsFrameAmount2;
      double lerp = MathHelper.lerp((double)degreesValue, entity.lastX, entity.getX());
      double lerp2 = MathHelper.lerp((double)degreesValue, entity.lastY, entity.getY());
      double lerp3 = MathHelper.lerp((double)degreesValue, entity.lastZ, entity.getZ());
      double degreesValue7 = lerp2 + entity.getHeight() * 0.64F;
      amountDegrees.clampAmount = degreesValue2 + (lerp - degreesValue2) * panelsFrameAmount2;
      amountDegrees.posY = degreesValue3 + (degreesValue7 - degreesValue3) * panelsFrameAmount2;
      amountDegrees.posZ = degreesValue4 + (lerp3 - degreesValue4) * panelsFrameAmount2;
      amountDegrees.rigDistance = zoomDistance * panelsFrameAmount2;
      amountDegrees.degreesActive = amountDegrees.rigDistance > 1.0F;
      MinecraftClient instance = MinecraftClient.getInstance();
      float degreesFactor = (float)instance.getWindow().getFramebufferWidth() / Math.max(1, instance.getWindow().getFramebufferHeight());
      float degreesFactor2 = (float)Math.tan(Math.toRadians(degrees) * 0.5) * degreesFactor;
      amountDegrees.corzFreecamAmount = panelsFrameAmount2 * 0.66F * degreesFactor2;
      float angle = amountDegrees.degreesAmount * (float) (Math.PI / 180.0);
      amountDegrees.dirX = -MathHelper.cos(angle);
      amountDegrees.dirZ = -MathHelper.sin(angle);
      return amountDegrees;
   }

   public static boolean canVectorMul(double vectorMulValue, double vectorMulValue2, double vectorMulValue3, float[] vectorMulValues) {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (instance.gameRenderer != null && vectorMulValues != null && vectorMulValues.length >= 2) {
         Vec3d cameraPos = instance.gameRenderer.getCamera().getCameraPos();
         Window window = instance.getWindow();
         float scaleFactor = window.getScaleFactor();
         VECTOR4F.set((float)(vectorMulValue - cameraPos.x), (float)(vectorMulValue2 - cameraPos.y), (float)(vectorMulValue3 - cameraPos.z), 1.0F);
         VECTOR4F.mul(NameTags.MATRIX4F3);
         VECTOR4F.mul(NameTags.MATRIX4F);
         if (VECTOR4F.w <= 1.0E-4F) {
            return false;
         } else {
            vectorMulValues[0] = (VECTOR4F.x / VECTOR4F.w * 0.5F + 0.5F) * window.getFramebufferWidth() / scaleFactor;
            vectorMulValues[1] = (0.5F - VECTOR4F.y / VECTOR4F.w * 0.5F) * window.getFramebufferHeight() / scaleFactor;
            return true;
         }
      } else {
         return false;
      }
   }

   public static float getViewHeight() {
      return viewHeight;
   }

   private static ClampMatrixUtils.Overlay getHandlerForDouble(double clampMatrixValue, double scaleY) {
      for (int i = OVERLAYS.size() - 1; i >= 0; i--) {
         ClampMatrixUtils.Overlay handler = OVERLAYS.get(i);

         try {
            if (handler.canFlag(clampMatrixValue, scaleY)) {
               return handler;
            }
         } catch (Throwable e) {
            handleHandler(handler);
            return null;
         }
      }

      return null;
   }

   public static float getPanelsFrame(float panelsFrameValue) {
      float amount = panelsFrameValue + (58.0F - panelsFrameValue) * panelsFrameAmount;
      degrees = amount;
      return amount;
   }

   public static boolean isFlag2() {
      return FramebufferSimpleUtils.isCompositeBegin();
   }

   public static double getAmountFlagForDoubleWindow(double clampMatrixValue, double amountFlag, Window window) {
      if (!scaleXActive && !isFramePanelsFlag()) {
         return amountFlag;
      } else {
         double scaleY = scaleXActive ? scaleYAmount : amountFlag;
         if (!isFramePanelsFlag()) {
            return scaleY;
         } else {
            handleAmountPanels(scaleXActive ? scaleXAmount : Mouse.scaleX(window, clampMatrixValue), scaleY);
            return hoveredPanelY;
         }
      }
   }

   static void updateFlagContext() {
      clampActive = false;
      clampDisabled = false;
      matrixActive = false;
      contextActive = false;
      contextAmount = 0.0F;
      clampContext = 0.0F;
      drawContext = null;
      updateAmountsLerp();
   }

   private static float getClampForFloat(float value) {
      float clampFactor = 1.0F - (float)Math.pow(MathHelper.clamp(value, 0.0F, 1.0F), 1.3);
      return 1.0F - clampFactor * clampFactor * clampFactor;
   }

   public static boolean canPanelsClamp(int panelsClampValue, double clampValue, double panelsClampValue3) {
      if (buttonClampValue == 3) {
         handleClamp(clampValue, panelsClampValue3);
         return true;
      } else if (buttonClampValue == 4 && handlerPanelsButton != null) {
         ClampMatrixUtils.Overlay overlay = handlerPanelsButton;

         try {
            overlay.handle(getScaleX(), getScaleY(), panelsClampValue);
         } catch (Throwable e) {
            handleHandler(overlay);
         }

         return true;
      } else if (buttonClampValue == 2 && matrixClamp3ClampMatrix != null) {
         ClampMatrixUtils.Panel panel = matrixClamp3ClampMatrix;
         handleAmountPanels(getScaleX(), getScaleY());

         try {
            panel.canFlag(panel.panelsAmount, panel.panelsClampAmount, panelsClampValue, clampValue, panelsClampValue3);
         } catch (Throwable e2) {
            handleClampMatrix(panel);
         }

         return true;
      } else {
         return false;
      }
   }

   public static ClampMatrixUtils.Panel getClampMatrixMatrixClamp() {
      if (!isFramePanelsFlag()) {
         return null;
      } else {
         handleAmountPanels(getScaleX(), getScaleY());
         return matrixClamp;
      }
   }

   private static void handleAmountTranslate(float amount, float amount2) {
      if (!translateActive || lastInputX != amount || lastInputY != amount2 || lastRectX != rectX || lastRectY != rectY || translateAmount != clampAmount || lastRectHeight != rectHeight) {
         translateActive = true;
         lastInputX = amount;
         lastInputY = amount2;
         lastRectX = rectX;
         lastRectY = rectY;
         translateAmount = clampAmount;
         lastRectHeight = rectHeight;
         float translateValue = amount * 0.5F;
         float translateValue2 = amount2 * 0.5F;
         float translateValue3 = rectX + clampAmount * 0.5F - translateValue;
         float translateValue4 = translateValue2 - (rectY + rectHeight * 0.5F);
         float amount3 = 0.0F;
         float amount6 = 0.0F;
         float amount9 = 0.0F;
         float translateValue5 = (float) (-Math.PI / 10);
         float translateValue6 = 0.08726646F;

         for (int i = 0; i < 8; i++) {
            MATRIX4F.set(MATRIX4_FMATRIX4F)
               .translate(translateValue3 + amount3, translateValue4 + amount6, amount9)
               .rotateY(translateValue5)
               .rotateX(translateValue6)
               .translate(-translateValue3, -translateValue4, 0.0F)
               .scale(1.0F, -1.0F, 1.0F)
               .translate(-translateValue, -translateValue2, 0.0F);
            float translateLimit = Float.MAX_VALUE;
            float maxValue = Float.MAX_VALUE;
            float maxValue7 = -Float.MAX_VALUE;
            float maxValue8 = -Float.MAX_VALUE;

            for (int j = 0; j < 4; j++) {
               float vectorValue = (j & 1) == 0 ? rectX : rectX + clampAmount;
               float translateValue10 = (j & 2) == 0 ? rectY : rectY + rectHeight;
               canVectorAmount(MATRIX4F, vectorValue, translateValue10, amount, amount2);
               if (vectorAmount < translateLimit) {
                  translateLimit = vectorAmount;
               }

               if (vectorAmount > maxValue7) {
                  maxValue7 = vectorAmount;
               }

               if (projectedY < maxValue) {
                  maxValue = projectedY;
               }

               if (projectedY > maxValue8) {
                  maxValue8 = projectedY;
               }
            }

            float translateDivisor = Math.max(1.0F, maxValue7 - translateLimit);
            float max2 = Math.max(1.0F, maxValue8 - maxValue);
            float translateFactor = Math.min(1.0F, Math.min(0.61F * amount / translateDivisor, 0.86F * amount2 / max2));
            float translateValue11 = 700.0F - amount9;
            if (translateFactor < 0.999F) {
               translateValue11 /= translateFactor;
               amount9 = 700.0F - translateValue11;
            }

            float translateFactor2 = translateValue11 / 700.0F;
            float translateValue12 = (translateLimit + maxValue7) * 0.5F;
            float translateValue13 = (maxValue + maxValue8) * 0.5F;
            float translateValue14 = amount * 0.97F;
            float translateValue15 = translateValue12 + (maxValue7 - translateValue12) * translateFactor;
            amount3 += (translateValue14 - translateValue15) * translateFactor2;
            amount6 -= (translateValue2 - translateValue13) * translateFactor2;
         }

         offsetX = amount3;
         matrixAmount = amount6;
         offsetZ = amount9;
      }
   }

   public static void updateFramePanels() {
      frameFlag = false;
      frameActive = false;
      panelsFrameAmount = 0.0F;
      buttonClampValue = 0;
      matrixClamp3ClampMatrix = null;
      handlerPanelsButton = null;
      button = -1;
   }

   public static void handleFlag(boolean flag) {
      matrixActive = flag;
   }

   public static float getLerpedPosX() {
      return lerpedPosX;
   }

   public static boolean isFramePanelsFlag() {
      return !framePanelsActive && !(panelsFrameAmount <= 0.0F) && lastActiveFrameIndex >= frameCount - 1 && MATRIX_CLAMP.matrixActive
         ? MinecraftClient.getInstance().currentScreen instanceof MatrixMatricesScreen
         : false;
   }

   public static boolean isClampMatrixPanelsActive() {
      if (!isFramePanelsFlag()) {
         return true;
      } else {
         handleAmountPanels(getScaleX(), getScaleY());
         return panelsActive || matrixClamp != null;
      }
   }

   public static boolean isFramePanels4() {
      return !framePanelsActive && !frameFlag && panelsFrameAmount > 0.08F;
   }

   public static boolean canPanelsClamp(double panelsClampValue) {
      if (!isFramePanelsFlag()) {
         return false;
      } else {
         handleAmountPanels(getScaleX(), getScaleY());
         if (matrixClamp != null) {
            ClampMatrixUtils.Panel panel = matrixClamp;

            try {
               if (panel.canFlag(panel.panelsAmount, panel.panelsClampAmount, panelsClampValue)) {
                  return true;
               }
            } catch (Throwable e) {
               handleClampMatrix(panel);
            }

            handleClamp(panelsClampValue);
            return true;
         } else if (panelsActive && !isPressedWindow()) {
            return false;
         } else {
            handleClamp(panelsClampValue);
            return true;
         }
      }
   }

   public static float getZoomDistance() {
      return zoomDistance;
   }

   public static boolean canHandler(ClampMatrixUtils.Overlay overlay) {
      return overlay != null && buttonClampValue == 4 && handlerPanelsButton == overlay;
   }

   private ClampMatrixUtils() {
   }

   public static void handle(ClampMatrixUtils.Overlay overlay) {
      if (overlay != null && !OVERLAYS.contains(overlay)) {
         OVERLAYS.add(overlay);
      }
   }

   public static boolean isFrameCamera() {
      if (!framePanelsActive && !(panelsFrameAmount <= 0.0F)) {
         MinecraftClient instance = MinecraftClient.getInstance();
         return instance.player != null && instance.world != null && instance.getCameraEntity() == instance.player && !Freecam.isInstanceFlag();
      } else {
         return false;
      }
   }

   public static void renderContextDraw(DrawContext context) {
      drawContext = context;
   }

   private static boolean isPressedWindow() {
      Window window = MinecraftClient.getInstance().getWindow();
      return InputUtil.isKeyPressed(window, 341) || InputUtil.isKeyPressed(window, 345);
   }

   public static float getLerpedPosZ() {
      return lerpedPosZ;
   }

   public static void handleFlag(Throwable throwable) {
      toggleHandled = true;
   }

   public static float getLerpAmount() {
      return lerpAmount;
   }

   public static float getAmountClamp(ClampMatrixUtils.Rig rig, float amount) {
      if (amount <= 0.02F) {
         return amount;
      } else {
         MinecraftClient instance = MinecraftClient.getInstance();
         if (instance.world != null && instance.player != null) {
            if (rig.clampAmount != cachedRigX || rig.posY != cachedRigY || rig.posZ != cachedRigZ || rig.degreesAmount != cachedRigYaw || amount != cachedInputAmount) {
               cachedRigX = rig.clampAmount;
               cachedRigY = rig.posY;
               cachedRigZ = rig.posZ;
               cachedRigYaw = rig.degreesAmount;
               cachedInputAmount = amount;
               float clampFactor = amount + 0.3F;
               Vec3d vec3d = new Vec3d(rig.clampAmount, rig.posY, rig.posZ);
               Vec3d vec3d2 = new Vec3d(rig.clampAmount + rig.dirX * clampFactor, rig.posY, rig.posZ + rig.dirZ * clampFactor);
               BlockHitResult raycast = instance.world.raycast(new RaycastContext(vec3d, vec3d2, ShapeType.VISUAL, FluidHandling.NONE, instance.player));
               allowedCameraDistance = amount;
               if (raycast != null && raycast.getType() != Type.MISS) {
                  allowedCameraDistance = (float)Math.max(0.0, Math.min((double)amount, raycast.getPos().distanceTo(vec3d) - 0.3));
               }
            }

            float thumbAmount = MathHelper.clamp(allowedCameraDistance / amount, 0.0F, 1.0F);
            clampBlend = thumbAmount <= clampBlend ? thumbAmount : (float)MatrixIdentifierUtils.getAmount(clampBlend, thumbAmount, 7.0, clamp);
            return amount * clampBlend;
         } else {
            return amount;
         }
      }
   }

   public static boolean isFramePanelsActive() {
      return framePanelsActive;
   }

   private static void updateAmountsLerp() {
      lerpActive = false;
      MinecraftClient instance = MinecraftClient.getInstance();
      if (instance.player != null && isFrameCamera() && CAMERA_RIG.degreesActive) {
         float tickProgress = instance.getRenderTickCounter().getTickProgress(false);
         double lerp = MathHelper.lerp((double)tickProgress, instance.player.lastX, instance.player.getX());
         double lerp2 = MathHelper.lerp((double)tickProgress, instance.player.lastY, instance.player.getY());
         double lerp3 = MathHelper.lerp((double)tickProgress, instance.player.lastZ, instance.player.getZ());
         float widthScaled = instance.player.getWidth() * 0.5F;
         float height = instance.player.getHeight();
         float amount = Float.MAX_VALUE;
         float maxValue = Float.MAX_VALUE;
         float maxValue4 = -Float.MAX_VALUE;
         float maxValue5 = -Float.MAX_VALUE;

         for (int i = 0; i < 8; i++) {
            double lerpValue = lerp + ((i & 1) == 0 ? -widthScaled : widthScaled);
            double lerpValue2 = lerp2 + ((i & 2) == 0 ? 0.0F : height);
            double lerpValue3 = lerp3 + ((i & 4) == 0 ? -widthScaled : widthScaled);
            if (!canVectorMul(lerpValue, lerpValue2, lerpValue3, lerpTable)) {
               return;
            }

            if (lerpTable[0] < amount) {
               amount = lerpTable[0];
            }

            if (lerpTable[0] > maxValue4) {
               maxValue4 = lerpTable[0];
            }

            if (lerpTable[1] < maxValue) {
               maxValue = lerpTable[1];
            }

            if (lerpTable[1] > maxValue5) {
               maxValue5 = lerpTable[1];
            }
         }

         lerpedPosX = amount;
         lerpedPosZ = maxValue;
         lerpAmount = maxValue4;
         lerpedPosY = maxValue5;
         lerpActive = true;
      }
   }

   public static boolean canPanelsButton(int panelsButtonValue) {
      if (buttonClampValue != 0 && panelsButtonValue == ClampMatrixUtils.button) {
         int buttonClampValue22 = buttonClampValue;
         ClampMatrixUtils.Panel panel = matrixClamp3ClampMatrix;
         ClampMatrixUtils.Overlay overlay = handlerPanelsButton;
         if (buttonClampValue22 == 2 && panel != null && isFramePanelsFlag()) {
            handleAmountPanels(getScaleX(), getScaleY());
         }

         buttonClampValue = 0;
         matrixClamp3ClampMatrix = null;
         handlerPanelsButton = null;
         ClampMatrixUtils.button = -1;
         if (buttonClampValue22 == 4) {
            if (overlay != null) {
               try {
                  overlay.handleForDoubleInt(getScaleX(), getScaleY(), panelsButtonValue);
               } catch (Throwable e) {
                  handleHandler(overlay);
               }
            }

            return true;
         } else if (buttonClampValue22 == 2 && panel != null) {
            try {
               panel.canFlagForFloatInt(panel.panelsAmount, panel.panelsClampAmount, panelsButtonValue);
            } catch (Throwable e2) {
               handleClampMatrix(panel);
            }

            return true;
         } else {
            return buttonClampValue22 == 3;
         }
      } else {
         return false;
      }
   }

   public static boolean canAmountMatrix(ClampMatrixUtils.Panel panel, float matrixValue, float matrixValue2, float[] matrixValues) {
      if (panel == null || !panel.matrixActive || matrixValues == null || matrixValues.length < 2 || viewWidth <= 1.0F || viewHeight <= 1.0F) {
         return false;
      } else if (!canVectorAmount(panel.modelMatrix, matrixValue, matrixValue2, viewWidth, viewHeight)) {
         return false;
      } else {
         matrixValues[0] = vectorAmount;
         matrixValues[1] = projectedY;
         return true;
      }
   }

   public static void updateFrameFlag() {
      FramebufferSimpleUtils.updateCompositeBegin();
      clampMatrixActive = false;
      frameCount++;
      if (framePanelsActive) {
         FramebufferSimpleUtils.updateSimpleFramebuffer();
      } else if (panelsFrameAmount <= 0.0F && !frameActive && !frameFlag && !GUISettings.isInstanceBase()) {
         lastNanoTime = 0L;
         FramebufferSimpleUtils.updateSimpleFramebuffer();
      } else {
         try {
            updateAmountFrame();
         } catch (Throwable e) {
            handleFrameClamp("frame", e);
         }
      }
   }

   private static boolean canVectorTransform(ClampMatrixUtils.Panel panel, double vectorTransformValue, double modeValue) {
      if (!(viewWidth <= 1.0F) && !(viewHeight <= 1.0F)) {
         float vectorTransformValue3 = (float)(vectorTransformValue / viewWidth * 2.0 - 1.0);
         float vectorTransformValue4 = (float)(1.0 - modeValue / viewHeight * 2.0);
         VECTOR4_FVECTOR4F.set(vectorTransformValue3, vectorTransformValue4, -1.0F, 1.0F);
         panel.matrixMatrix4f.transform(VECTOR4_FVECTOR4F);
         VECTOR4F.set(vectorTransformValue3, vectorTransformValue4, 1.0F, 1.0F);
         panel.matrixMatrix4f.transform(VECTOR4F);
         if (!(Math.abs(VECTOR4_FVECTOR4F.w) < 1.0E-9F) && !(Math.abs(VECTOR4F.w) < 1.0E-9F)) {
            float vectorTransformValue5 = VECTOR4_FVECTOR4F.x / VECTOR4_FVECTOR4F.w;
            float vectorTransformValue6 = VECTOR4_FVECTOR4F.y / VECTOR4_FVECTOR4F.w;
            float vectorTransformValue7 = VECTOR4_FVECTOR4F.z / VECTOR4_FVECTOR4F.w;
            float vectorTransformValue8 = VECTOR4F.x / VECTOR4F.w;
            float vectorTransformValue9 = VECTOR4F.y / VECTOR4F.w;
            float vectorTransformValue10 = VECTOR4F.z / VECTOR4F.w;
            float deltaVar = vectorTransformValue10 - vectorTransformValue7;
            if (Math.abs(deltaVar) < 1.0E-9F) {
               return false;
            } else {
               float vectorTransformFactor = -vectorTransformValue7 / deltaVar;
               float amount = vectorTransformValue5 + (vectorTransformValue8 - vectorTransformValue5) * vectorTransformFactor;
               float long2floatopenhashmap = vectorTransformValue6 + (vectorTransformValue9 - vectorTransformValue6) * vectorTransformFactor;
               if (!Float.isNaN(amount) && !Float.isNaN(long2floatopenhashmap) && !Float.isInfinite(amount) && !Float.isInfinite(long2floatopenhashmap)) {
                  VECTOR4_FVECTOR4F.set(amount, long2floatopenhashmap, 0.0F, 1.0F);
                  panel.modelMatrix.transform(VECTOR4_FVECTOR4F);
                  if (VECTOR4_FVECTOR4F.w <= 0.0F) {
                     return false;
                  } else {
                     panelsAmount = amount;
                     vectorTransformAmount = long2floatopenhashmap;
                     return true;
                  }
               } else {
                  return false;
               }
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public interface Overlay {
      default boolean canFlag(double overlayValue, double scaleY, int button) {
         return false;
      }

      default void handle(double overlayValue, double overlayValue2, int panelsClampValue) {
      }

      void render(DrawContext context, float overlayValue);

      default boolean canFlag(double overlayValue, double scaleY) {
         return false;
      }

      default void handleForDoubleInt(double overlayValue, double overlayValue2, int panelsButtonValue) {
      }
   }

   public static class Panel {
      public float panelsAmount;
      final Matrix4f matrixMatrix4f;
      public float panelCenterY;
      public boolean panelsActive;
      public float clampMatrixAmount;
      public float matrixAmount;
      public float panelsClampAmount;
      public float clamp;
      public boolean clampMatrixActive;
      public float panelAmount;
      float renderAlpha;
      public float modeAmount;
      public final float baseAlpha;
      public float modernThemeAmount;
      boolean matrixActive;
      final Matrix4f modelMatrix;
      public float panelY;
      public float panelHeight;
      public float cornerRadius = 8.0F;
      public float tiltDegrees;

      public Panel() {
         this.clamp = 1.0F;
         this.panelAmount = -18.0F;
         this.tiltDegrees = 5.0F;
         this.baseAlpha = 1.0F;
         this.clampMatrixActive = true;
         this.panelsAmount = -100000.0F;
         this.panelsClampAmount = -100000.0F;
         this.modelMatrix = new Matrix4f();
         this.matrixMatrix4f = new Matrix4f();
      }

      public boolean canFlag(float panelValue, float panelValue2, double panelsClampValue) {
         return false;
      }

      public boolean canFlag(float panelValue, float panelValue2, int button) {
         return false;
      }

      public void render(DrawContext context, float panelValue, float panelValue2, float panelValue3) {
      }

      public boolean canFlag(float panelValue, float panelValue2, int panelsClampValue, double clampValue, double panelValue5) {
         return false;
      }

      public boolean canFlagForFloatInt(float panelValue, float panelValue2, int panelsButtonValue) {
         return false;
      }
   }

   public static final class Rig {
      public float rigDistance;
      public float degreesAmount;
      public float corzFreecamAmount;
      public float rigAmount;
      public double clampAmount;
      public double posZ;
      public double posY;
      public float dirX;
      public float dirZ;
      public boolean degreesActive;
   }
}
