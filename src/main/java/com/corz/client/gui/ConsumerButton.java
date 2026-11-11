package com.corz.client.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

public final class ConsumerButton {
   private int hitCount;
   private final List<ConsumerButton.Hit> consumerHitList = new ArrayList<>();
   static final int UNUSED_ONE = 1;

   boolean canConsumer(double consumerValue, double amountMaxForDouble, double consumerLimit) {
      ConsumerButton.Hit hit = this.getMaxForDouble(consumerValue, amountMaxForDouble);
      if (hit == null || hit.hitKind == 2) {
         return false;
      } else if (hit.hitKind == 1 && hit.intConsumer != null && consumerLimit != 0.0) {
         hit.intConsumer.accept(consumerLimit > 0.0 ? 1 : -1);
         return true;
      } else {
         return true;
      }
   }

   boolean canConsumerText(double consumerValue, double amountMaxForDouble, int consumerValue3, MinButton minButton) {
      ConsumerButton.Hit hit = this.getMaxForDouble(consumerValue, amountMaxForDouble);
      if (hit == null) {
         return false;
      } else {
         switch (hit.hitKind) {
            case 1:
               if (hit.intConsumer == null || consumerValue3 != 0 && consumerValue3 != 1) {
                  return true;
               }

               if (minButton != null && hit.consumerText != null) {
                  minButton.handle(hit.consumerText);
               }

               hit.intConsumer.accept(consumerValue3 == 1 ? -1 : 1);
               break;
            case 2:
               if (consumerValue3 != 0 || hit.intConsumer == null) {
                  return true;
               }

               if (minButton != null && hit.consumerText != null) {
                  minButton.handle(hit.consumerText);
               }

               hit.intConsumer.accept(hit.consumerValue);
               break;
            default:
               if (consumerValue3 != 0 || hit.action == null) {
                  return true;
               }

               if (minButton != null && hit.consumerText != null) {
                  minButton.handle(hit.consumerText);
               }

               hit.action.run();
         }

         return true;
      }
   }

   int getValue() {
      return this.hitCount;
   }

   void updateConsumerAction() {
      for (int i = 0; i < this.hitCount; i++) {
         ConsumerButton.Hit hit = this.consumerHitList.get(i);
         hit.action = null;
         hit.intConsumer = null;
         hit.consumerText = null;
      }

      this.hitCount = 0;
   }

   ConsumerButton.Hit getMaxForDouble(double consumerButtonValue, double amountMaxForDouble) {
      for (int i = this.hitCount - 1; i >= 0; i--) {
         ConsumerButton.Hit hit = this.consumerHitList.get(i);
         if (hit.canMaxAmount(consumerButtonValue, amountMaxForDouble)) {
            return hit;
         }
      }

      return null;
   }

   public void handleMaxConsumer(float max, float maxCubeSchematicAmount, float amount, float amount2, int value, String text, Runnable action, IntConsumer intConsumer, int i) {
      if (!(amount <= 0.0F) && !(amount2 <= 0.0F) && (action != null || intConsumer != null)) {
         ConsumerButton.Hit hit;
         if (this.hitCount < this.consumerHitList.size()) {
            hit = this.consumerHitList.get(this.hitCount);
         } else {
            hit = new ConsumerButton.Hit();
            this.consumerHitList.add(hit);
         }

         this.hitCount++;
         hit.hitX = max;
         hit.hitAmount = maxCubeSchematicAmount;
         hit.consumerAmount = amount;
         hit.hitHeight = amount2;
         hit.hitKind = value;
         hit.consumerText = text;
         hit.action = action;
         hit.intConsumer = intConsumer;
         hit.consumerValue = i;
      }
   }

   static final class Hit {
      IntConsumer intConsumer;
      int hitKind;
      float hitHeight;
      float consumerAmount;
      String consumerText;
      float hitX;
      float hitAmount;
      int consumerValue;
      Runnable action;

      boolean canMaxAmount(double hitLimit, double amountMaxForDouble) {
         return hitLimit >= this.hitX && hitLimit <= this.hitX + this.consumerAmount && amountMaxForDouble >= this.hitAmount && amountMaxForDouble <= this.hitAmount + this.hitHeight;
      }
   }
}
