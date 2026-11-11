package com.corz.client.gui;

import com.corz.client.config.ConfigSetting;
import java.util.function.Supplier;
import com.corz.client.utils.MinmaxEnumType;

public class ButtonBase extends ConfigSetting {
   private Supplier<Integer> supplier;
   private final Runnable action;
   private final String buttonText;
   private boolean buttonActive;
   private Supplier<String> supplier2Supplier = null;

   public ButtonBase getSupplier(Supplier<String> supplier2) {
      this.supplier2Supplier = supplier2;
      return this;
   }

   public int getSupplier() {
      if (this.supplier != null) {
         try {
            Integer supplier = this.supplier.get();
            if (supplier != null) {
               return supplier;
            }
         } catch (Exception e) {
         }
      }

      return 0;
   }

   public void updateAction() {
      if (this.action != null) {
         try {
            this.action.run();
         } catch (Exception e) {
         }
      }
   }

   public ButtonBase(String buttonText, String description, Runnable runnable) {
      this(buttonText, description, buttonText, runnable);
   }

   public ButtonBase(String buttonText, String description, String text, Runnable runnable) {
      super(buttonText, description);
      this.supplier = null;
      this.buttonActive = false;
      this.buttonText = text;
      this.action = runnable;
   }

   public String formatSupplier() {
      if (this.supplier2Supplier != null) {
         try {
            String supplier = this.supplier2Supplier.get();
            if (supplier != null) {
               return supplier;
            }
         } catch (Exception e) {
         }
      }

      return this.getSettingName();
   }

   public String getText() {
      return this.buttonText;
   }

   public ButtonBase getFlagButtonActive() {
      this.buttonActive = true;
      return this;
   }

   public ButtonBase getSupplierForSupplier(Supplier<Integer> supplier2) {
      this.supplier = supplier2;
      return this;
   }

   public boolean isButtonActive() {
      return this.buttonActive;
   }

   @Override
   public MinmaxEnumType getSettingType() {
      return MinmaxEnumType.BUTTON;
   }
}
