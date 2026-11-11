package com.corz.client.entity;

import com.corz.client.entity.ThreadClientHandlerConst.Result;
import com.corz.client.mixins.AccessorKeyBinding;
import com.corz.client.world.ThreadClientHandlerHandle;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Vec3d;
import com.corz.client.categories.combat.SilentAim;
import com.corz.client.utils.EntityDamageUtils;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.DirExtractionUtils;
import com.corz.client.utils.WeaknessGodApi;

public final class ClientThreadUtils {
   private static long tickCounter;
   private static Object object2Deque;
   private static boolean optionsActive;
   private static Entity entity;
   private static final MinecraftClient clientThreadMinecraftClient = MinecraftClient.getInstance();
   private static Object dueOwner;
   private static final ArrayDeque<ClientThreadUtils.Press> ARRAY_DEQUE = new ArrayDeque<>();
   private static int recentPressCount;
   private static boolean dequeActive;
   private static boolean entityActive;
   private static boolean clientThreadActive;
   private static final ArrayDeque<ClientThreadUtils.Press> USE_KEY_PRESSES = new ArrayDeque<>();

   private static void handle(Vec3d vec, boolean clientThreadFlag) {
      for (Module ilIllIiIll2 : getItemsList()) {
         if (ilIllIiIll2 instanceof ThreadClientHandlerHandle threadClientHandlerHandle) {
            threadClientHandlerHandle.handlePlayerReduction(vec, clientThreadFlag);
         }
      }
   }

   private static void handlePressedTimes(KeyBinding keyBinding, ArrayDeque<ClientThreadUtils.Press> arrayDeque, long pressedLimit) {
      if (!arrayDeque.isEmpty()) {
         int pressedCounter = 0;
         var iterator = arrayDeque.iterator();

         while (iterator.hasNext()) {
            if (((ClientThreadUtils.Press)iterator.next()).dueTick() <= pressedLimit) {
               iterator.remove();
               pressedCounter++;
            }
         }

         if (pressedCounter != 0 && keyBinding != null) {
            AccessorKeyBinding accessorKeyBinding = (AccessorKeyBinding)keyBinding;
            accessorKeyBinding.corzSetTimesPressed(Math.max(0, accessorKeyBinding.corzGetTimesPressed() - pressedCounter));
         }
      }
   }

   public static void handleThreadInstance(Vec3d vec) {
      if (clientThreadMinecraftClient.player != null && vec != null && clientThreadMinecraftClient.isOnThread()) {
         handle(vec, true);
      }
   }

   private static List<ThreadClientHandlerConst> getItems() {
      var arrayList = new ArrayList();

      for (Module ilIllIiIll2 : getItemsList()) {
         if (ilIllIiIll2 instanceof ThreadClientHandlerConst threadClientHandlerConst) {
            arrayList.add(threadClientHandlerConst);
         }
      }

      handle(arrayList);
      return arrayList;
   }

   public static boolean isFlag() {
      return canFlag(WeaknessGodApi::isAgePlayer);
   }

   public static boolean canFlag(Object obj) {
      if (DirExtractionUtils.isFlag()) {
         return false;
      } else {
         for (Module iilliliilIIiItem : ModuleRegistry.getModules()) {
            if (iilliliilIIiItem != obj && iilliliilIIiItem instanceof WeaknessGodApi weaknessGodApi && iilliliilIIiItem.isEnabled() && weaknessGodApi.isGodWeakness()) {
               return true;
            }
         }

         return false;
      }
   }

   public static boolean canFlag(Object obj, Entity entity) {
      return !DirExtractionUtils.isFlag() && obj != null && entity != null && !entityActive ? !canFlagForObject(obj) : false;
   }

   public static void updateFlag() {
      optionsActive = false;
   }

   static void handle(List<ThreadClientHandlerConst> list) {
      list.sort(Comparator.comparingInt(ThreadClientHandlerConst::getHandlerPriority).reversed());
   }

   public static boolean canPassFlag(Object obj, Entity entity) {
      if (DirExtractionUtils.isFlag()) {
         return true;
      } else if (obj == null || entity == null || entityActive) {
         return false;
      } else {
         return canFlagForObject(obj) ? false : getPass(getItems(), obj, entity) == Result.PASS;
      }
   }

   private static Entity getCrosshairEntity() {
      return clientThreadMinecraftClient.crosshairTarget instanceof EntityHitResult entityHitResult ? entityHitResult.getEntity() : null;
   }

   private static boolean canFlagForObject(Object obj) {
      for (Module iilliliilIIiItem : ModuleRegistry.getModules()) {
         if (iilliliilIIiItem != obj && iilliliilIIiItem instanceof WeaknessGodApi weaknessGodApi && iilliliilIIiItem.isEnabled() && weaknessGodApi.isGodWeaknessWaitingPlayer()) {
            return true;
         }
      }

      return false;
   }

   public static boolean canOptionsInstance(Object obj) {
      return clientThreadMinecraftClient.options != null && canPressedTimes(clientThreadMinecraftClient.options.attackKey, ARRAY_DEQUE, obj);
   }

   private static Object getDueOwner(ArrayDeque<ClientThreadUtils.Press> arrayDeque) {
      ClientThreadUtils.Press peekFirst = (ClientThreadUtils.Press)arrayDeque.peekFirst();
      if (peekFirst != null && peekFirst.dueTick() <= tickCounter) {
         arrayDeque.pollFirst();
         return peekFirst.owner();
      } else {
         return null;
      }
   }

   public static long getTime() {
      return tickCounter;
   }

   public static void updateMax() {
      recentPressCount = Math.max(0, recentPressCount - 1);
      object2Deque = null;
   }

   private static boolean canFlag(Predicate<WeaknessGodApi> predicate) {
      if (DirExtractionUtils.isFlag()) {
         return false;
      } else {
         for (Module iilliliilIIiItem : ModuleRegistry.getModules()) {
            if (iilliliilIIiItem instanceof WeaknessGodApi weaknessGodApi && iilliliilIIiItem.isEnabled() && predicate.test(weaknessGodApi)) {
               return true;
            }
         }

         return false;
      }
   }

   public static Entity getEntity() {
      return entity;
   }

   public static boolean isFlag2() {
      return canFlag(WeaknessGodApi::isReductionCritical);
   }

   public static boolean isOptionsPressed() {
      return clientThreadMinecraftClient.options != null && ((AccessorKeyBinding)clientThreadMinecraftClient.options.attackKey).corzGetTimesPressed() > 0;
   }

   public static boolean isDequeActive() {
      return dequeActive;
   }

   public static Boolean getMaxDeque() {
      Object obj = getDueOwner(ARRAY_DEQUE);
      object2Deque = obj;
      recentPressCount++;
      boolean dequeFlag = false;

      Boolean maxDeque;
      try {
         Boolean false2 = getFalse(obj);
         dequeFlag = false2 != null;
         maxDeque = false2;
      } finally {
         if (dequeFlag) {
            recentPressCount = Math.max(0, recentPressCount - 1);
            object2Deque = null;
         }
      }

      return maxDeque;
   }

   static long getTime(long clientThreadValue, boolean clientThreadFlag) {
      return clientThreadFlag ? clientThreadValue : clientThreadValue + 1L;
   }

   public static boolean isFlag4() {
      return canFlag(WeaknessGodApi::isGodWeaknessWaitingPlayer);
   }

   public static void updateDequeFlag() {
      optionsActive = false;
      ARRAY_DEQUE.clear();
      USE_KEY_PRESSES.clear();
      entityActive = false;
      dequeActive = false;
      clientThreadActive = false;
      entity = null;
      recentPressCount = 0;
      object2Deque = null;
      dueOwner = null;
   }

   public static boolean isDequeArray() {
      dueOwner = getDueOwner(USE_KEY_PRESSES);
      return isFlag();
   }

   public static void handleInstanceThread(int threadValue) {
      if (clientThreadMinecraftClient.player != null && threadValue == clientThreadMinecraftClient.player.getId() && clientThreadMinecraftClient.isOnThread()) {
         handle(clientThreadMinecraftClient.player.getVelocity(), false);
      }
   }

   public static void updateOptionsInstance() {
      optionsActive = false;
      if (clientThreadMinecraftClient.options != null) {
         handlePressedTimes(clientThreadMinecraftClient.options.attackKey, ARRAY_DEQUE, Long.MAX_VALUE);
         handlePressedTimes(clientThreadMinecraftClient.options.useKey, USE_KEY_PRESSES, Long.MAX_VALUE);
      }
   }

   public static void update() {
      dueOwner = null;
   }

   public static boolean isUSE_KEY_PRESSES() {
      return !USE_KEY_PRESSES.isEmpty();
   }

   public static Object getObject() {
      return dueOwner;
   }

   private static boolean canPressedTimes(KeyBinding keyBinding, ArrayDeque<ClientThreadUtils.Press> arrayDeque, Object obj) {
      if (obj != null && keyBinding != null && clientThreadMinecraftClient.player != null && clientThreadMinecraftClient.currentScreen == null && clientThreadMinecraftClient.getOverlay() == null) {
         for (ClientThreadUtils.Press press : arrayDeque) {
            if (press.owner() == obj) {
               return false;
            }
         }

         AccessorKeyBinding accessorKeyBinding = (AccessorKeyBinding)keyBinding;
         accessorKeyBinding.corzSetTimesPressed(accessorKeyBinding.corzGetTimesPressed() + 1);
         arrayDeque.addLast(new ClientThreadUtils.Press(obj, getTime(tickCounter, optionsActive)));
         return true;
      } else {
         return false;
      }
   }

   private static List<Module> getItemsList() {
      if (DirExtractionUtils.isFlag()) {
         return List.of();
      } else {
         var list = ModuleRegistry.getModules();
         var arrayList = new ArrayList();

         for (Module totalPriceData : list) {
            if (totalPriceData.isEnabled()) {
               arrayList.add(totalPriceData);
            }
         }

         return arrayList;
      }
   }

   public static boolean isEntityActive() {
      return entityActive;
   }

   public static boolean isARRAY_DEQUE() {
      return !ARRAY_DEQUE.isEmpty();
   }

   private ClientThreadUtils() {
   }

   public static boolean canOptionsInstanceForObject(Object obj) {
      return clientThreadMinecraftClient.options != null && canPressedTimes(clientThreadMinecraftClient.options.useKey, USE_KEY_PRESSES, obj);
   }

   public static Object getObject2Deque() {
      return object2Deque;
   }

   public static void handleFlagEntity(Entity entity2) {
      entityActive = true;
      entity = entity2;
      if (recentPressCount == 0) {
         dequeActive = true;
      } else if (object2Deque != null) {
         clientThreadActive = true;
      }

      EntityDamageUtils.handleTimeHurt(entity2);
   }

   public static boolean isFlag6() {
      return canFlag(WeaknessGodApi::isGodWeakness);
   }

   public static void updateOptionsTime() {
      tickCounter++;
      optionsActive = true;
      if (clientThreadMinecraftClient.options != null) {
         handlePressedTimes(clientThreadMinecraftClient.options.attackKey, ARRAY_DEQUE, tickCounter - 1L);
         handlePressedTimes(clientThreadMinecraftClient.options.useKey, USE_KEY_PRESSES, tickCounter - 1L);
      }

      entityActive = false;
      dequeActive = false;
      clientThreadActive = false;
      entity = null;
      recentPressCount = 0;
      object2Deque = null;
      dueOwner = null;
   }

   private static Boolean getFalse(Object obj) {
      if (DirExtractionUtils.isFlag()) {
         return null;
      } else if (isFlag4()) {
         return Boolean.FALSE;
      } else if (!dequeActive && (!entityActive || obj == null && !clientThreadActive)) {
         PlayerEntity playerEntity = obj == null ? SilentAim.getLockedHitTarget() : null;
         Object obj2 = playerEntity != null ? playerEntity : getCrosshairEntity();
         Result pass = getPass(getItems(), obj, (Entity)obj2);
         if (pass == Result.BLOCK) {
            return Boolean.FALSE;
         } else if (pass == Result.HANDLED) {
            return Boolean.TRUE;
         } else {
            return playerEntity != null && SilentAim.tryAttackLockedTarget(true) ? Boolean.TRUE : null;
         }
      } else {
         return Boolean.FALSE;
      }
   }

   static Result getPass(List<ThreadClientHandlerConst> list, Object obj, Entity entity) {
      for (ThreadClientHandlerConst threadClientHandlerConst : list) {
         Result passCharging = threadClientHandlerConst.resolveAttackIntercept(obj, entity);
         if (passCharging != null && passCharging != Result.PASS) {
            return passCharging;
         }
      }

      return Result.PASS;
   }

   private record Press(Object owner, long dueTick) {
   }
}
