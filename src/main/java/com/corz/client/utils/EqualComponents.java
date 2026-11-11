package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.ToIntFunction;
import net.minecraft.item.ItemStack;

public final class EqualComponents {
   private final List<EqualComponents.Thrown> emptyThrownList = new ArrayList<>();

   public void handleTimeMax(ToIntFunction<ItemStack> toIntFunction, long equalComponentsLimit, double equalComponentsValue, double equalComponentsValue2, double equalComponentsValue3) {
      Iterator iterator = this.emptyThrownList.iterator();

      while (iterator.hasNext()) {
         EqualComponents.Thrown thrown = (EqualComponents.Thrown)iterator.next();
         if (thrown.expireTime >= 0L) {
            if (equalComponentsLimit >= thrown.expireTime || thrown.getAmount(equalComponentsValue, equalComponentsValue2, equalComponentsValue3) > 16.0) {
               iterator.remove();
            }
         } else {
            long equalComponentsLimit2 = equalComponentsLimit - thrown.thrownValue;
            if (equalComponentsLimit2 > 200L) {
               iterator.remove();
            } else {
               int applyAsInt = toIntFunction.applyAsInt(thrown.withCount);
               if (equalComponentsLimit2 >= 38L && applyAsInt > thrown.maxStackCount) {
                  thrown.expireTime = equalComponentsLimit + 1200L;
               } else {
                  thrown.maxStackCount = Math.min(thrown.maxStackCount, applyAsInt);
               }
            }
         }
      }
   }

   public boolean canEqualComponents(ItemStack itemStack) {
      if (itemStack.isEmpty()) {
         return false;
      } else {
         for (EqualComponents.Thrown thrown : this.emptyThrownList) {
            if (thrown.expireTime >= 0L && ItemStack.areItemsAndComponentsEqual(thrown.withCount, itemStack)) {
               return true;
            }
         }

         return false;
      }
   }

   public void handleEmpty(ItemStack stack, int emptyBound, long emptyValue, double emptyValue2, double emptyValue3, double emptyValue4) {
      if (!stack.isEmpty()) {
         this.emptyThrownList.removeIf(thrown -> thrown.expireTime < 0L && ItemStack.areItemsAndComponentsEqual(thrown.withCount, stack));
         this.emptyThrownList.add(new EqualComponents.Thrown(stack.copyWithCount(1), Math.max(0, emptyBound), emptyValue, emptyValue2, emptyValue3, emptyValue4));
      }
   }

   public void update() {
      this.emptyThrownList.clear();
   }

   private static final class Thrown {
      int maxStackCount;
      long expireTime = -1L;
      final double z;
      final ItemStack withCount;
      final double x;
      final long thrownValue;
      final double y;

      double getAmount(double thrownValue, double thrownValue2, double thrownValue3) {
         double thrownFactor = thrownValue - this.x;
         double thrownFactor2 = thrownValue2 - this.y;
         double thrownFactor3 = thrownValue3 - this.z;
         return thrownFactor * thrownFactor + thrownFactor2 * thrownFactor2 + thrownFactor3 * thrownFactor3;
      }

      Thrown(ItemStack withCount, int max, long time, double thrownValue, double thrownValue2, double thrownValue3) {
         this.withCount = withCount;
         this.maxStackCount = max;
         this.thrownValue = time;
         this.x = thrownValue;
         this.y = thrownValue2;
         this.z = thrownValue3;
      }
   }
}
