package com.corz.client.inventory;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import net.minecraft.item.ItemStack;
import com.corz.client.utils.StackEmptyUtilsAbilities;

public final class DequeEmpty {
   private final Map<Integer, DequeEmpty.Click> dequeArrayClickMap = new HashMap<>();
   private final ArrayDeque<Long> arrayDeque = new ArrayDeque<>();

   public DequeEmpty.Verdict getDequeArray(int dequeArrayValue, ItemStack stack, long dequeArrayValue2) {
      if (this.canEmpty(dequeArrayValue, stack)) {
         return DequeEmpty.Verdict.SKIP;
      } else {
         DequeEmpty.Click click = this.dequeArrayClickMap.get(dequeArrayValue);
         if (click != null && !click.dequeArrayActive && dequeArrayValue2 - click.clickTime <= 40L && StackEmptyUtilsAbilities.canEmptyEqual(stack, click.empty)) {
            click.clickCount++;
            click.dequeArrayActive = true;
            this.arrayDeque.addLast(dequeArrayValue2);

            while (!this.arrayDeque.isEmpty() && dequeArrayValue2 - this.arrayDeque.peekFirst() > 100L) {
               this.arrayDeque.removeFirst();
            }

            if (this.arrayDeque.size() >= 6) {
               this.arrayDeque.clear();

               for (DequeEmpty.Click click2 : this.dequeArrayClickMap.values()) {
                  if (click2.clickCount > 0) {
                     click2.clickCount = 3;
                  }
               }

               return DequeEmpty.Verdict.STOP;
            } else {
               return click.clickCount >= 3 ? DequeEmpty.Verdict.SKIP : DequeEmpty.Verdict.SEND;
            }
         } else {
            return DequeEmpty.Verdict.SEND;
         }
      }
   }

   public void updateDequeArray() {
      this.dequeArrayClickMap.clear();
      this.arrayDeque.clear();
   }

   public void handleTime(long dequeEmptyValue) {
      Iterator iterator = this.dequeArrayClickMap.values().iterator();

      while (iterator.hasNext()) {
         DequeEmpty.Click click = (DequeEmpty.Click)iterator.next();
         if (click.clickCount < 3 && dequeEmptyValue - click.clickTime > 40L) {
            iterator.remove();
         }
      }
   }

   public boolean canEmpty(int emptyValue, ItemStack stack) {
      DequeEmpty.Click click = this.dequeArrayClickMap.get(emptyValue);
      if (click == null || click.clickCount < 3) {
         return false;
      } else if (StackEmptyUtilsAbilities.canEmptyEqual(stack, click.empty)) {
         return true;
      } else {
         this.dequeArrayClickMap.remove(emptyValue);
         return false;
      }
   }

   public void handleEmptyTime(int emptyValue, ItemStack empty, long time) {
      DequeEmpty.Click ifAbsent = this.dequeArrayClickMap.computeIfAbsent(emptyValue, integer -> new DequeEmpty.Click());
      ifAbsent.empty = empty;
      ifAbsent.clickTime = time;
      ifAbsent.dequeArrayActive = false;
   }

   private static final class Click {
      boolean dequeArrayActive;
      int clickCount;
      long clickTime;
      ItemStack empty = ItemStack.EMPTY;
   }

   public static enum Verdict {
      SEND,
      SKIP,
      STOP;
   }
}
