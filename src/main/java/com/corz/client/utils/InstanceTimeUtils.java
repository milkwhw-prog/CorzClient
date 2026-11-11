package com.corz.client.utils;

import com.corz.client.inventory.InventoryStackUtilsHandle;
import java.util.ArrayDeque;
import net.minecraft.client.MinecraftClient;
import com.corz.client.utils.ExtUtils;

public final class InstanceTimeUtils {
   private static long timeMillis;
   private static final MinecraftClient minecraftClient = MinecraftClient.getInstance();
   private static final ArrayDeque<Runnable> ARRAY_DEQUE = new ArrayDeque<>();
   private static long minIntervalMs = 50L;

   public static void updateDequeArray() {
      if (minecraftClient.player == null || minecraftClient.interactionManager == null) {
         ARRAY_DEQUE.clear();
         InventoryStackUtilsHandle.update();
         ExtUtils.updateFlagText();
      } else if (!ARRAY_DEQUE.isEmpty()) {
         if (System.currentTimeMillis() - timeMillis >= minIntervalMs) {
            Runnable array_deque = ARRAY_DEQUE.poll();
            if (array_deque != null) {
               try {
                  array_deque.run();
               } catch (Exception e) {
               }

               timeMillis = System.currentTimeMillis();
            }
         }
      }
   }

   private InstanceTimeUtils() {
   }

   public static void handleMax(long bound) {
      minIntervalMs = Math.max(0L, bound);
   }

   public static void handleDequeArray(Runnable runnable) {
      if (runnable != null) {
         ARRAY_DEQUE.add(runnable);
      }
   }

   public static void updateDequeArrayARRAY_DEQUE() {
      ARRAY_DEQUE.clear();
   }

   public static int getDequeArray() {
      return ARRAY_DEQUE.size();
   }
}
