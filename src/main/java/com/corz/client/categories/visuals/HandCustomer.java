package com.corz.client.categories.visuals;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.entity.EntityEyeUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Arm;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;
import com.corz.client.utils.QuintBounceType;

public class HandCustomer extends Module {
   private final EnumSetting enumHelper;
   private final DoubleSetting doubleHelper;
   private final EnumSetting multiplyDegreesEnumHelper;
   private final DoubleSetting multiplyDegreesDoubleHelper;
   private final DoubleSetting handCustomerDoubleHelper;
   private final DoubleSetting translateRightDoubleHelper;
   private final DoubleSetting spinSpeedHelper;
   private final DoubleSetting rightXHelper;
   private final BooleanSetting booleanBase;
   private final DoubleSetting strengthHelper;
   private final DoubleSetting leftYHelper;
   private final DoubleSetting leftZHelper;
   private final BooleanSetting handCustomerBooleanBase = (BooleanSetting)new BooleanSetting("Only when target", "Only apply while you have a nearby player target.", false)
      .withGroupName("Check");
   private final EnumSetting handCustomerEnumHelper;
   private final EnumSetting swingModeHelper;
   private final DoubleSetting customSwingXHelper;
   private final BooleanSetting multiplyDegreesBooleanBase;
   private final DoubleSetting customSwingYHelper;
   private final BooleanSetting spinToggle = (BooleanSetting)new BooleanSetting(
         "Constant Spin", "Continuously spin the item around its X axis (needs Animation on).", false
      )
      .withGroupName("Check");
   private final DoubleSetting rightZHelper;
   private final DoubleSetting leftXHelper;

   public boolean isBase() {
      if (!this.isEnabled()) {
         return false;
      } else {
         return this.handCustomerBooleanBase.isEnabled() ? EntityEyeUtils.getNext(6.0) != null : true;
      }
   }

   private void handleMultiplyDegrees(MatrixStack matrixStack, Arm arm, float multiplyDegreesFactor, float multiplyDegreesFactor2) {
      int multiplyDegreesFactor3 = arm == Arm.RIGHT ? 1 : -1;
      float sin = MathHelper.sin(multiplyDegreesFactor * multiplyDegreesFactor * (float) Math.PI);
      matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(multiplyDegreesFactor3 * (45.0F + sin * -20.0F * multiplyDegreesFactor2)));
      float sin2 = MathHelper.sin(MathHelper.sqrt(multiplyDegreesFactor) * (float) Math.PI);
      matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(multiplyDegreesFactor3 * sin2 * -20.0F * multiplyDegreesFactor2));
      matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(sin2 * -80.0F * multiplyDegreesFactor2));
      matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(multiplyDegreesFactor3 * -45.0F));
   }

   public HandCustomer() {
      super("Swing Animation", IconsPngType.VISUAL);
      this.handCustomerEnumHelper = (EnumSetting)new EnumSetting("Hand Side", "Which hand(s) the constant spin applies to.", "All", "All", "Right", "Left").withGroupName("Check");
      this.multiplyDegreesEnumHelper = (EnumSetting)new EnumSetting("Spin Direction", "Direction of the constant spin.", "Backward", "Forward", "Backward").withGroupName("Check");
      this.spinSpeedHelper = (DoubleSetting)new DoubleSetting("Spin Speed", "Constant spin speed.", 5.0, 1.0, 20.0).withGroupName("Check");
      this.booleanBase = (BooleanSetting)new BooleanSetting("Animation", "Enable the custom swing animation (replaces the vanilla swing).", true)
         .withGroupName("Animation");
      this.doubleHelper = (DoubleSetting)new DoubleSetting("Speed", "Animation speed.", 10.0, 1.0, 20.0).withGroupName("Animation");
      this.strengthHelper = (DoubleSetting)new DoubleSetting("Strength", "Animation strength / amplitude.", 10.0, 1.0, 20.0).withGroupName("Animation");
      this.swingModeHelper = (EnumSetting)new EnumSetting(
            "Swing mode",
            "Custom swing style.",
            "Smooth",
            "None",
            "Custom",
            "Smooth",
            "Smooth2",
            "Stab",
            "Punch",
            "Self2",
            "Self",
            "Block",
            "Akrien",
            "Spin",
            "Touch",
            "Forward",
            "Down",
            "Down2",
            "Curt",
            "Pander",
            "BlockHit"
         )
         .withGroupName("Animation");
      this.enumHelper = (EnumSetting)new EnumSetting(
            "Progress animation",
            "Easing applied to the swing progress.",
            "Linear",
            "Linear",
            "Sine In",
            "Sine Out",
            "Sine In Out",
            "Cubic In",
            "Cubic Out",
            "Cubic In Out",
            "Quad In",
            "Quad Out",
            "Quad In Out"
         )
         .withGroupName("Animation");
      this.customSwingXHelper = (DoubleSetting)new DoubleSetting("Custom X", "Custom swing X (Swing mode = Custom).", 1.0, -3.0, 3.0, 0.1).withGroupName("Animation");
      this.customSwingYHelper = (DoubleSetting)new DoubleSetting("Custom Y", "Custom swing Y (Swing mode = Custom).", 1.0, -3.0, 3.0, 0.1).withGroupName("Animation");
      this.handCustomerDoubleHelper = (DoubleSetting)new DoubleSetting("Custom Z", "Custom swing Z (Swing mode = Custom).", 1.0, -3.0, 3.0, 0.1).withGroupName("Animation");
      this.multiplyDegreesDoubleHelper = (DoubleSetting)new DoubleSetting("Custom Rotation", "Custom swing base rotation (Swing mode = Custom).", 0.0, -180.0, 180.0, 5.0)
         .withGroupName("Animation");
      this.multiplyDegreesBooleanBase = (BooleanSetting)new BooleanSetting("Hands Position", "Enable the static per-hand position offsets below.", true).withGroupName("Hands Position");
      this.rightXHelper = (DoubleSetting)new DoubleSetting("Right X", "", 0.0, -2.0, 2.0, 0.1).withGroupName("Hands Position");
      this.translateRightDoubleHelper = (DoubleSetting)new DoubleSetting("Right Y", "", 0.0, -2.0, 2.0, 0.1).withGroupName("Hands Position");
      this.rightZHelper = (DoubleSetting)new DoubleSetting("Right Z", "", 0.0, -2.0, 2.0, 0.1).withGroupName("Hands Position");
      this.leftXHelper = (DoubleSetting)new DoubleSetting("Left X", "", 0.0, -2.0, 2.0, 0.1).withGroupName("Hands Position");
      this.leftYHelper = (DoubleSetting)new DoubleSetting("Left Y", "", 0.0, -2.0, 2.0, 0.1).withGroupName("Hands Position");
      this.leftZHelper = (DoubleSetting)new DoubleSetting("Left Z", "", 0.0, -2.0, 2.0, 0.1).withGroupName("Hands Position");
      this.addSettings(
         new ConfigSetting[]{
            this.handCustomerBooleanBase,
            this.spinToggle,
            this.handCustomerEnumHelper,
            this.multiplyDegreesEnumHelper,
            this.spinSpeedHelper,
            this.booleanBase,
            this.doubleHelper,
            this.strengthHelper,
            this.swingModeHelper,
            this.enumHelper,
            this.customSwingXHelper,
            this.customSwingYHelper,
            this.handCustomerDoubleHelper,
            this.multiplyDegreesDoubleHelper,
            this.multiplyDegreesBooleanBase,
            this.rightXHelper,
            this.translateRightDoubleHelper,
            this.rightZHelper,
            this.leftXHelper,
            this.leftYHelper,
            this.leftZHelper
         }
      );
   }

   public void handleTranslateRight(MatrixStack matrixStack, Arm arm) {
      if (this.isBase() && this.multiplyDegreesBooleanBase.isEnabled()) {
         if (arm == Arm.RIGHT) {
            matrixStack.translate((float)this.rightXHelper.getValue(), (float)this.translateRightDoubleHelper.getValue(), (float)this.rightZHelper.getValue());
         } else {
            matrixStack.translate(-((float)this.leftXHelper.getValue()), (float)this.leftYHelper.getValue(), (float)this.leftZHelper.getValue());
         }
      }
   }

   public boolean canMultiplyDegrees(float multiplyDegreesValue, float multiplyDegreesFactor, MatrixStack matrixStack, float side, Arm arm) {
      if (!this.isBase()) {
         return false;
      } else {
         boolean multiplyDegreesFlag = false;
         if (this.multiplyDegreesBooleanBase.isEnabled()) {
            this.handleTranslateRight(matrixStack, arm);
            boolean multiplyDegreesFlag2 = true;
         }

         if (!this.booleanBase.isEnabled()) {
            if (this.multiplyDegreesBooleanBase.isEnabled()) {
               this.handleMultiplyDegrees(matrixStack, arm, multiplyDegreesValue, 1.0F);
               return true;
            } else {
               return false;
            }
         } else {
            boolean multiplyDegrees = true;
            boolean multiplyDegreesFlag3 = arm == Arm.RIGHT;
            int multiplyDegreesFactor2 = multiplyDegreesFlag3 ? 1 : -1;
            Arm arm3 = MinecraftClient.getInstance().options.getMainArm().getValue();
            boolean multiplyDegreesFlag4 = arm == arm3;
            matrixStack.translate(multiplyDegreesFactor2 * 0.56F, -0.52F, -0.72F);
            if (this.spinToggle.isEnabled()) {
               boolean multiplyDegreesFlag5 = false;
               if (this.handCustomerEnumHelper.getCurrentOption().equals("All")) {
                  multiplyDegreesFlag5 = true;
               } else if (this.handCustomerEnumHelper.getCurrentOption().equals("Right") && multiplyDegreesFlag3) {
                  multiplyDegreesFlag5 = true;
               } else if (this.handCustomerEnumHelper.getCurrentOption().equals("Left") && !multiplyDegreesFlag3) {
                  multiplyDegreesFlag5 = true;
               }

               if (multiplyDegreesFlag5) {
                  float seconds = (float)(System.currentTimeMillis() % 360000L) / 1000.0F;
                  float multiplyDegreesFactor3 = this.multiplyDegreesEnumHelper.getCurrentOption().equals("Backward") ? 1.0F : -1.0F;
                  float multiplyDegreesValue3 = seconds * ((float)this.spinSpeedHelper.getValue() / 5.0F) * multiplyDegreesFactor3;
                  matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(360.0F * multiplyDegreesValue3));
               }
            }

            if (multiplyDegreesFlag4) {
               float unaryOperator = (float)getCubicSine(this.enumHelper.getCurrentOption()).getUnaryOperator(multiplyDegreesValue);
               float multiplyDegreesFactor4 = (float)(this.strengthHelper.getValue() / this.strengthHelper.getMaxValue());
               String formatText = this.swingModeHelper.getCurrentOption();
               switch (formatText) {
                  case "None":
                  default:
                     break;
                  case "Block":
                     matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(multiplyDegreesFactor2 * -45));
                     matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees((float)(multiplyDegreesFactor2 * 45 + 90.0F * multiplyDegreesFactor4 * Math.sin(unaryOperator * Math.PI))));
                     matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(multiplyDegreesFactor2 * -90.0F));
                     break;
                  case "Smooth":
                     this.handleMultiplyDegrees(matrixStack, arm, unaryOperator, multiplyDegreesFactor4);
                     break;
                  case "Akrien":
                     if (unaryOperator > 0.0F) {
                        float sin = MathHelper.sin(MathHelper.sqrt(unaryOperator) * (float) Math.PI);
                        matrixStack.translate(0.0F, multiplyDegreesFactor * -0.2F + 0.02F, 0.02F);
                        matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45 * multiplyDegreesFactor2));
                        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(sin * -85.0F * multiplyDegreesFactor4));
                        matrixStack.translate(-0.1F * multiplyDegreesFactor2, 0.28F, 0.2F);
                        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-85.0F));
                     } else {
                        this.handleMultiplyDegrees(matrixStack, arm, unaryOperator, multiplyDegreesFactor4);
                     }
                     break;
                  case "Stab":
                     float sin2 = (float)Math.sin(unaryOperator * (Math.PI / 2) * 2.0);
                     matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90 * multiplyDegreesFactor2));
                     matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-70 * multiplyDegreesFactor2));
                     matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-100.0F - 60.0F * multiplyDegreesFactor4 * sin2));
                     break;
                  case "Spin":
                     matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-unaryOperator * 360.0F * multiplyDegreesFactor4 * 3.3F));
                     break;
                  case "Punch":
                     float sin3 = (float)Math.sin(unaryOperator * (Math.PI / 2) * 2.0);
                     matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45.0F));
                     matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(sin3 * -40.0F * multiplyDegreesFactor4));
                     matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(30.0F));
                     matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-80.0F));
                     matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(60.0F));
                     break;
                  case "Down":
                     float sin4 = MathHelper.sin(unaryOperator * (float) Math.PI);
                     matrixStack.translate(0.0F, -sin4 * 0.8F * multiplyDegreesFactor4, 0.0F);
                     break;
                  case "Custom":
                     float sin5 = MathHelper.sin(MathHelper.sqrt(unaryOperator) * (float) Math.PI);
                     float multiplyDegreesFactor5 = (float)this.customSwingXHelper.getValue();
                     float multiplyDegreesFactor6 = (float)this.customSwingYHelper.getValue();
                     float multiplyDegreesFactor7 = (float)this.handCustomerDoubleHelper.getValue();
                     float multiplyDegreesValue4 = (float)this.multiplyDegreesDoubleHelper.getValue();
                     if (multiplyDegreesValue4 != 0.0F) {
                        matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(multiplyDegreesValue4));
                     }

                     matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(multiplyDegreesFactor2 * (45.0F + sin5 * -20.0F * multiplyDegreesFactor5)));
                     matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(multiplyDegreesFactor2 * sin5 * -20.0F * multiplyDegreesFactor6));
                     matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(sin5 * -80.0F * multiplyDegreesFactor7));
                     matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(multiplyDegreesFactor2 * -45.0F));
                     break;
                  case "Smooth2":
                     float multiplyDegreesValue5 = (float)this.strengthHelper.getValue() * 10.0F;
                     float sin6 = MathHelper.sin(unaryOperator * unaryOperator * (float) Math.PI);
                     float sin7 = MathHelper.sin(MathHelper.sqrt(unaryOperator) * (float) Math.PI);
                     matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45.0F + sin6 * (-multiplyDegreesValue5 / 4.0F)));
                     matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(sin7 * -(multiplyDegreesValue5 / 4.0F)));
                     matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(sin7 * -multiplyDegreesValue5));
                     matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-45.0F));
                     break;
                  case "Self":
                     float sin8 = MathHelper.sin(unaryOperator * (float) Math.PI);
                     matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
                     matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-30.0F));
                     matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-45.0F - (float)this.strengthHelper.getValue() * 10.0F * sin8));
                     break;
                  case "Forward":
                     float sin9 = MathHelper.sin(MathHelper.sqrt(unaryOperator) * (float) Math.PI);
                     matrixStack.translate(0.0, 0.0, -0.3 * sin9);
                     matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(sin9 * -35.0F));
                     matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(sin9 * 35.0F));
                     break;
                  case "Self2":
                     float sin10 = MathHelper.sin(unaryOperator * (float) Math.PI);
                     matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
                     matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-60.0F));
                     matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-45.0F - (float)this.strengthHelper.getValue() * 10.0F * sin10));
                     break;
                  case "Down2":
                     float sin11 = MathHelper.sin(unaryOperator * (float) Math.PI);
                     matrixStack.translate(0.0F, -(sin11 * (float)this.strengthHelper.getValue() / 24.0F), 0.0F);
                     matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(90.0F));
                     matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-30.0F));
                     matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-90.0F));
                     break;
                  case "Touch":
                     float sin12 = MathHelper.sin(unaryOperator * (float) Math.PI);
                     matrixStack.scale(1.0F, 1.0F, (float)(1.0 + sin12 * this.strengthHelper.getValue() / 4.0));
                     matrixStack.translate(0.0F, 0.0F, -0.265F);
                     matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-100.0F));
                     break;
                  case "Curt":
                     float sqrt = MathHelper.sqrt(unaryOperator);
                     float sin13 = MathHelper.sin(sqrt * (float) Math.PI);
                     float sin14 = MathHelper.sin(unaryOperator * (float) Math.PI);
                     matrixStack.translate(0.4F - sin13 * 0.2F, -0.2F + sin13 * 0.3F, -0.5F - sin14 * 0.2F);
                     matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(91.0F));
                     matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-40.0F + sin13 * -100.0F));
                     matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-60.0F));
                     break;
                  case "Pander":
                     matrixStack.scale(0.8F, 0.8F, 0.8F);
                     float multiplyDegreesValue6 = 1.0F - multiplyDegreesFactor;
                     float sin15 = MathHelper.sin(unaryOperator * (float) Math.PI);
                     matrixStack.translate(0.3 - sin15 * 0.15F, (double)(0.2F - multiplyDegreesValue6 * 0.12F), (double)(-0.15F - sin15 * 0.13F));
                     matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(76.0F - 10.0F * sin15));
                     matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(-16.0F - 8.0F * sin15));
                     matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-83.0F - 26.0F * sin15));
                     break;
                  case "BlockHit":
                     float sin16 = MathHelper.sin((float)(unaryOperator * unaryOperator * Math.PI));
                     matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(45.0F));
                     float sin17 = MathHelper.sin((float)(MathHelper.sqrt(unaryOperator) * Math.PI));
                     matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(sin16 * -20.0F));
                     matrixStack.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(sin17 * -20.0F));
                     matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(sin17 * -80.0F));
                     matrixStack.translate(0.4F, 0.2F, 0.2F);
                     matrixStack.translate(-0.5F, 0.08F, 0.0F);
                     matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(20.0F));
                     matrixStack.multiply(RotationAxis.POSITIVE_X.rotationDegrees(-80.0F));
                     matrixStack.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(20.0F));
               }
            } else {
               this.handleMultiplyDegrees(matrixStack, arm, multiplyDegreesValue, 1.0F);
            }

            return multiplyDegrees;
         }
      }
   }

   private static QuintBounceType getCubicSine(String cubicSineText) {
      return switch (cubicSineText) {
         case "Sine In" -> QuintBounceType.SINE_IN;
         case "Sine Out" -> QuintBounceType.SINE_OUT;
         case "Sine In Out" -> QuintBounceType.SINE_IN_OUT;
         case "Cubic In" -> QuintBounceType.CUBIC_IN;
         case "Cubic Out" -> QuintBounceType.CUBIC_OUT;
         case "Cubic In Out" -> QuintBounceType.CUBIC_IN_OUT;
         case "Quad In" -> QuintBounceType.QUAD_IN;
         case "Quad Out" -> QuintBounceType.QUAD_OUT;
         case "Quad In Out" -> QuintBounceType.QUAD_IN_OUT;
         default -> QuintBounceType.LINEAR;
      };
   }
}
