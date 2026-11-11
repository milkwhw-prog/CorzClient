package com.corz.client.categories.misc;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.EnumSetting;
import java.util.Arrays;
import com.corz.client.utils.KeyBank;
import com.corz.client.utils.KeyClickPlayer;
import com.corz.client.utils.MechKeycodes;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.IconsPngType;
import com.corz.client.utils.SwordBot;

public class CreamyKeys extends Module {
   private final BooleanSetting booleanBase;
   private double pressesTypingAmount;
   private final KeyClickPlayer keyClickPlayer;
   private final EnumSetting enumHelper;
   private final boolean[] pressesTypingFlags;
   private int pressesTypingValue;
   private int pressesTypingValueIIi;
   private final DoubleSetting doubleHelper = new DoubleSetting("Volume", "How loud the key sounds are", 0.6, 0.05, 1.0, 0.05);
   private int keyTickCounter;

   @Override
   protected void onEnable() {
      this.pressesTypingValueIIi = this.getEnum();
      this.pressesTypingAmount = this.doubleHelper.getValue();
      this.pressesTypingValue = -1;
      Arrays.fill(this.pressesTypingFlags, false);
      this.keyClickPlayer.handleCreamyCorz(this.pressesTypingValueIIi);
   }

   public CreamyKeys() {
      super("Creamy Keys", IconsPngType.MISC);
      this.enumHelper = new EnumSetting(
         "Sound",
         "Which keyboard you hear. NK Cream: smooth and creamy. Cherry MX Blue: loud and clicky. Holy Pandas: deep and bumpy. Topre: soft and muted.",
         "NK Cream",
         KeyBank.creamTopreTexts
      );
      this.booleanBase = new BooleanSetting("In Game", "Also plays for key presses while you play, not only when typing in chat, text boxes and menus", false);
      this.keyClickPlayer = new KeyClickPlayer();
      this.pressesTypingFlags = new boolean[349];
      this.pressesTypingValueIIi = -1;
      this.pressesTypingAmount = -1.0;
      this.pressesTypingValue = -1;
      this.addSettings(new ConfigSetting[]{this.doubleHelper, this.enumHelper, this.booleanBase});
   }

   public static void updateClickPlayer() {
      try {
         if (ModuleRegistry.getModule(CreamyKeys.class) instanceof CreamyKeys creamyKeys) {
            creamyKeys.keyClickPlayer.updateTicksSinceClick();
         }
      } catch (Throwable e) {
      }
   }

   @Override
   protected void onReset() {
      int pressesTypingValue = this.getEnum();
      if (pressesTypingValue != this.pressesTypingValueIIi) {
         this.pressesTypingValueIIi = pressesTypingValue;
         this.keyClickPlayer.handleCreamyCorz(pressesTypingValue);
         this.pressesTypingValue = 4;
         this.keyTickCounter = 0;
      }

      if (this.doubleHelper.getValue() != this.pressesTypingAmount) {
         this.pressesTypingAmount = this.doubleHelper.getValue();
         this.pressesTypingValue = 4;
         this.keyTickCounter = 0;
      }

      this.keyClickPlayer.update();
      if (this.pressesTypingValue >= 0) {
         if (this.pressesTypingValue > 0) {
            this.pressesTypingValue--;
         } else {
            if (this.keyClickPlayer.canContextAlc(pressesTypingValue)) {
               this.keyClickPlayer.canFlag(30, false, (float)this.doubleHelper.getValue());
               this.pressesTypingValue = -1;
            } else if (++this.keyTickCounter > 60) {
               this.pressesTypingValue = -1;
            }
         }
      }
   }

   private int getEnum() {
      return KeyBank.getCreamTopre(this.enumHelper.getCurrentOption());
   }

   @Override
   protected void onDisable() {
      this.keyClickPlayer.updateTicksSinceClick();
      this.pressesTypingValue = -1;
      Arrays.fill(this.pressesTypingFlags, false);
   }

   @Override
   protected void onSwordBot(SwordBot swordBot) {
      int index = swordBot.getKey();
      int creamyValue = swordBot.getValue();
      boolean creamyFlag = index >= 0 && index < this.pressesTypingFlags.length;
      if (creamyValue == 0) {
         if (creamyFlag && this.pressesTypingFlags[index]) {
            this.pressesTypingFlags[index] = false;
            this.keyClickPlayer.canFlag(MechKeycodes.getStarts(index), true, (float)this.doubleHelper.getValue());
         }
      } else if (creamyValue == 1) {
         if (!creamyFlag || !this.pressesTypingFlags[index]) {
            if (mc.currentScreen != null || this.booleanBase.isEnabled()) {
               int pressesTypingValue = this.getEnum();
               if (pressesTypingValue != this.pressesTypingValueIIi) {
                  this.pressesTypingValueIIi = pressesTypingValue;
                  this.keyClickPlayer.handleCreamyCorz(pressesTypingValue);
               }

               if (creamyFlag) {
                  this.pressesTypingFlags[index] = true;
               }

               this.keyClickPlayer.canFlag(MechKeycodes.getStarts(index), false, (float)this.doubleHelper.getValue());
            }
         }
      }
   }
}
