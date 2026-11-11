package com.corz.client.gui;

import java.util.function.IntConsumer;

public final class ButtonMin {
   public float cubeSchematicAmount;
   public float cubeSchematicAmount2Cube;
   ConsumerButton buttonConsumerButton;
   public float frameDelta;
   public float cubeKnifeAmount;
   public boolean buttonActive;
   public boolean thumbsActive;
   public boolean buttonCloseActive;
   public float materialAmount;
   public float cursorY;
   MinButton buttonMinButton;
   public float buttonAmount;
   public boolean guidePackageFlag;
   public float buttonAlpha;
   public int selectedIndex;
   public int button = -1;
   public float cubeSchematicAmount3Cube;
   public float renderAmount;

   public void handle(String buttonText, float buttonValue, float matrixStackValue, float cubeSchematicValue, float materialFactor, Runnable runnable) {
      this.handleCubeSchematic(buttonValue, matrixStackValue, cubeSchematicValue, materialFactor, 0, buttonText, runnable, null);
   }

   public boolean canAmountMaterial(float materialValue, float matrixStackValue, float cubeSchematicValue, float materialFactor) {
      return this.buttonActive
         && !this.buttonCloseActive
         && this.canCubeSchematic(this.materialAmount, this.cursorY)
         && this.materialAmount >= materialValue
         && this.materialAmount <= materialValue + cubeSchematicValue
         && this.cursorY >= matrixStackValue
         && this.cursorY <= matrixStackValue + materialFactor;
   }

   public ButtonMin() {
      this.selectedIndex = -1;
      this.buttonAlpha = 1.0F;
      this.buttonAmount = 1.0F;
      this.guidePackageFlag = true;
   }

   public boolean canCubeSchematic(double cubeSchematicLimit, double cubeSchematicLimit2) {
      return cubeSchematicLimit >= this.cubeKnifeAmount
         && cubeSchematicLimit <= this.cubeKnifeAmount + this.cubeSchematicAmount2Cube
         && cubeSchematicLimit2 >= this.cubeSchematicAmount
         && cubeSchematicLimit2 <= this.cubeSchematicAmount + this.cubeSchematicAmount3Cube;
   }

   public float getCubeAmount() {
      return this.cubeKnifeAmount + this.cubeSchematicAmount2Cube / 2.0F;
   }

   public float getMin(String buttonText, float buttonValue) {
      return this.buttonMinButton == null ? 1.0F : this.buttonMinButton.getAmount(buttonText, buttonValue);
   }

   public void handle(String buttonText, float buttonValue, float matrixStackValue, float cubeSchematicValue, float materialFactor, IntConsumer intConsumer) {
      this.handleCubeSchematic(buttonValue, matrixStackValue, cubeSchematicValue, materialFactor, 1, buttonText, null, intConsumer);
   }

   private void handleCubeSchematic(float cubeSchematicBound, float pilotStatusBound, float cubeSchematicValue, float materialFactor, int cubeSchematicValue3, String cubeSchematicText, Runnable runnable, IntConsumer intConsumer) {
      if (this.buttonActive && this.buttonConsumerButton != null) {
         float maxCubeKnifeAmount = Math.max(cubeSchematicBound, this.cubeKnifeAmount);
         float maxCubeSchematicAmount = Math.max(pilotStatusBound, this.cubeSchematicAmount);
         float min4 = Math.min(cubeSchematicBound + cubeSchematicValue, this.cubeKnifeAmount + this.cubeSchematicAmount2Cube);
         float min5 = Math.min(pilotStatusBound + materialFactor, this.cubeSchematicAmount + this.cubeSchematicAmount3Cube);
         if (min4 - maxCubeKnifeAmount > 0.5F && min5 - maxCubeSchematicAmount > 0.5F) {
            this.buttonConsumerButton.handleMaxConsumer(maxCubeKnifeAmount, maxCubeSchematicAmount, min4 - maxCubeKnifeAmount, min5 - maxCubeSchematicAmount, cubeSchematicValue3, cubeSchematicText, runnable, intConsumer, 0);
         }
      }
   }

   public float getCubeSchematic() {
      return this.cubeSchematicAmount + this.cubeSchematicAmount3Cube / 2.0F;
   }

   public float getMin(String buttonText, float buttonValue, float matrixStackValue, float cubeSchematicValue, float materialFactor) {
      return this.buttonMinButton == null ? 0.0F : this.buttonMinButton.getAmount(buttonText, this.canAmountMaterial(buttonValue, matrixStackValue, cubeSchematicValue, materialFactor));
   }
}
