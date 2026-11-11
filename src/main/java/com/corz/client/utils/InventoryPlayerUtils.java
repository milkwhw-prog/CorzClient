package com.corz.client.utils;

import com.corz.client.entity.ClientThreadUtils;
import com.corz.client.entity.ThreadClientHandlerConst.Result;
import com.corz.client.entity.TimeInstanceUtils;
import com.corz.client.inventory.EntityPlayerUtilsGliding;
import com.corz.client.inventory.FlagInventoryUtils;
import com.corz.client.inventory.InventoryStackUtilsEmpty;
import com.corz.client.inventory.PlayerInventoryApi;
import com.corz.client.inventory.SelectedInventoryUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerInteractEntityC2SPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.EntityHitResult;
import com.corz.client.categories.combat.AutoInventoryTotem;
import com.corz.client.categories.combat.SilentAim;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.InventoryPlayer;
import com.corz.client.utils.PlayerCrystalUtils;

public final class InventoryPlayerUtils {
   private static final Object FALLBACK_OBJECT = new Object();
   private static ClientPlayerEntity player;
   private static boolean attackSelectedActive;
   private static final MinecraftClient inventoryPlayerMinecraftClie = MinecraftClient.getInstance();
   private static int refreshCooldownTicks;
   private static PlayerInventoryApi apiSelectedPlayerInventoryAp;
   private static long lastCheckedTimeMs = Long.MIN_VALUE;
   private static Entity entity;
   private static LivingEntity livingEntity;
   private static long passPlanValue = Long.MIN_VALUE;
   private static boolean swapBack;
   private static PlayerInventoryApi ownerDensityBreach;
   private static long lastSwapTimeMs = Long.MIN_VALUE;
   private static int selectedSlot = -1;
   private static int selectedSlotApiSelected = -1;
   private static long densityBreachValue;
   private static boolean densityBreachActive;
   private static int selectedSlot4SelectedSlot = -1;
   private static DensityBreachType densityBreachType = DensityBreachType.ANY;
   private static long apiSelectedValue;
   private static long inventoryPlayerValue;
   private static double minFallDistance = 1.5;
   private static long livingValue = Long.MIN_VALUE;
   private static int selectedValue = -1;
   private static long lastTargetTimeMs = Long.MIN_VALUE;
   private static int lastTargetId = -1;
   private static Object object2PassPlan;
   private static int selectedCount;

   private static boolean isFlag() {
      return AutoInventoryTotem.isHandSwapSlotActive() || SelectedInventoryUtils.isSelectedSlot();
   }

   private static PlayerInventoryApi getApiPlayerInventoryApi() {
      var list = getItems();
      return list.isEmpty() ? null : (PlayerInventoryApi)list.get(0);
   }

   private static List<PlayerInventoryApi> getItems() {
      if (DirExtractionUtils.isFlag()) {
         return List.of();
      } else {
         var arrayList = new ArrayList(3);

         for (Module iilliliilIIiItem : ModuleRegistry.getModules()) {
            if (iilliliilIIiItem instanceof PlayerInventoryApi playerInventoryApi && iilliliilIIiItem.isEnabled()) {
               arrayList.add(playerInventoryApi);
            }
         }

         if (arrayList.size() > 1) {
            arrayList.sort(Comparator.comparingInt(PlayerInventoryApi::getSwapPriority).reversed());
         }

         return arrayList;
      }
   }

   private InventoryPlayerUtils() {
   }

   private static boolean canMinimumBelow(LivingEntity entity, int minimumBelowValue, int minimumBelowValue2, long inventoryLimit, boolean minimumBelowFlag) {
      if (!PlayerInventory.isValidHotbarIndex(minimumBelowValue)) {
         return false;
      } else {
         ItemStack empty = InventoryStackUtilsEmpty.getEmpty(minimumBelowValue);
         if (!EntityPlayerUtilsGliding.canVectorRotation(entity, empty)) {
            return false;
         } else {
            return minimumBelowFlag && inventoryPlayerMinecraftClie.player.isBelowMinimumAttackCharge(empty, 0) ? false : minimumBelowValue == minimumBelowValue2 || FlagInventoryUtils.canFlag(inventoryLimit, lastSwapTimeMs);
         }
      }
   }

   public static boolean canFlag(PlayerInventoryApi playerInventoryApi, LivingEntity entity, int inventoryPlayerValue, DensityBreachType densityBreachType, double iIiValue, boolean inventoryPlayerFlag) {
      if (!canValidHotbar(playerInventoryApi, entity, inventoryPlayerValue, inventoryPlayerFlag)) {
         return false;
      } else {
         handleDensityBreach(new InventoryPlayerUtils.Plan(playerInventoryApi, inventoryPlayerValue, true, densityBreachType, iIiValue, inventoryPlayerFlag), entity, ClientThreadUtils.getTime(), false);
         return true;
      }
   }

   private static void updateLivingSelected() {
      if (selectedSlotApiSelected < 0 && livingEntity == null && getApiPlayerInventoryApi() == null) {
         updateTimeSelected();
      }
   }

   private static boolean isSelectedSlot() {
      ClientPlayerEntity player2 = inventoryPlayerMinecraftClie.player;
      if (player2 != player) {
         updateTimeSelected();
         player = player2;
         selectedSlot = player2 == null ? -1 : player2.getInventory().getSelectedSlot();
      }

      if (player2 == null) {
         return false;
      } else {
         int inventoryValue = player2.getInventory().getSelectedSlot();
         if (inventoryValue != selectedSlot) {
            selectedSlot = inventoryValue;
            lastSwapTimeMs = Math.max(lastSwapTimeMs, ClientThreadUtils.getTime());
         }

         if (selectedSlotApiSelected >= 0 && !FlagInventoryUtils.canFlag(inventoryValue, selectedSlot4SelectedSlot, isFlag())) {
            updateApiSelected();
         }

         return true;
      }
   }

   private static void updateDensityBreach() {
      ownerDensityBreach = null;
      livingEntity = null;
      densityBreachType = DensityBreachType.ANY;
      minFallDistance = 1.5;
      swapBack = false;
      densityBreachActive = false;
      densityBreachValue = 0L;
      inventoryPlayerValue = 0L;
   }

   public static void updateApiDead() {
      long livingEntityLimit = ClientThreadUtils.getTime();
      if (lastCheckedTimeMs != livingEntityLimit) {
         lastCheckedTimeMs = livingEntityLimit;
         if (passPlanValue != livingEntityLimit) {
            object2PassPlan = null;
            entity = null;
         }

         EntityPlayerUtilsGliding.updateGlidingVehicle();
         if (isSelectedSlot()) {
            if (selectedSlotApiSelected >= 0 && apiSelectedPlayerInventoryAp == null) {
               apiSelectedPlayerInventoryAp = getApiPlayerInventoryApi();
            }

            if (!inventoryPlayerMinecraftClie.player.isDead() && inventoryPlayerMinecraftClie.interactionManager != null) {
               handleLivingEntity(livingEntityLimit);
               handleSelectedSlot(livingEntityLimit);
            } else {
               updateDensityBreach();
               updateApiSelected();
            }
         }
      }
   }

   public static void handleApiSelected(PlayerInventoryApi playerInventoryApi) {
      if (playerInventoryApi != null) {
         if (ownerDensityBreach == playerInventoryApi) {
            updateDensityBreach();
         }

         if (apiSelectedPlayerInventoryAp == playerInventoryApi && selectedSlotApiSelected >= 0) {
            apiSelectedPlayerInventoryAp = getApiPlayerInventoryApi();
            if (apiSelectedPlayerInventoryAp == null && !isSelectedSlotSelectedSlotApiSelected()) {
               update();
            }
         }

         updateLivingSelected();
      }
   }

   public static void updateTimeSelected() {
      updateDensityBreach();
      updateApiSelected();
      player = null;
      selectedSlot = -1;
      passPlanValue = Long.MIN_VALUE;
      object2PassPlan = null;
      entity = null;
      livingValue = Long.MIN_VALUE;
      selectedValue = -1;
      lastTargetTimeMs = Long.MIN_VALUE;
      lastTargetId = -1;
      attackSelectedActive = false;
      selectedCount++;
      refreshCooldownTicks = 0;
      EntityPlayerUtilsGliding.updatePlayer();
   }

   public static boolean canSlot(Entity entity) {
      InventoryPlayerUtils.Choice instanceFlag = getInstanceFlag(entity);
      return instanceFlag != null && !instanceFlag.isPlan() && InventoryStackUtilsEmpty.canSecondsDisable(InventoryStackUtilsEmpty.getEmpty(instanceFlag.slot()));
   }

   private static boolean isFlag2() {
      for (PlayerInventoryApi playerInventoryApi : getItems()) {
         if (playerInventoryApi.isMaceSmashReady()) {
            return true;
         }
      }

      return false;
   }

   private static boolean canAttackSelected(PlayerInventoryApi playerInventoryApi, LivingEntity entity, int attackSelectedValue, boolean attackSelectedFlag) {
      ClientPlayerEntity player = inventoryPlayerMinecraftClie.player;
      long inventoryLimit = ClientThreadUtils.getTime();
      if (!EntityPlayerUtilsGliding.canInstancePlayer(entity, InventoryStackUtilsEmpty.getEmpty(attackSelectedValue))) {
         return false;
      } else if (attackSelectedValue != player.getInventory().getSelectedSlot() && !FlagInventoryUtils.canFlag(inventoryLimit, lastSwapTimeMs)) {
         return false;
      } else {
         attackSelectedActive = true;

         boolean canPassFlag2;
         try {
            canPassFlag2 = ClientThreadUtils.canPassFlag(playerInventoryApi, entity);
         } finally {
            attackSelectedActive = false;
         }

         if (canPassFlag2 && isSelectedSlot()) {
            int selectedSlot = player.getInventory().getSelectedSlot();
            if (attackSelectedValue != selectedSlot) {
               if (!FlagInventoryUtils.canFlag(inventoryLimit, lastSwapTimeMs)) {
                  return false;
               }

               handleSelectedSlot(playerInventoryApi, selectedSlot, attackSelectedValue, attackSelectedFlag, inventoryLimit);
            } else {
               handleSelectedSlotForLong(inventoryLimit);
            }

            inventoryPlayerMinecraftClie.interactionManager.attackEntity(player, entity);
            player.swingHand(Hand.MAIN_HAND);
            player.resetTicksSinceLastAttack();
            return true;
         } else {
            return false;
         }
      }
   }

   public static Result getPassPlan(Object obj, Entity entity2) {
      if (!attackSelectedActive && !(obj instanceof PlayerInventoryApi)) {
         long time = ClientThreadUtils.getTime();
         if (passPlanValue == time && object2PassPlan == obj && entity == entity2) {
            return Result.PASS;
         } else {
            passPlanValue = time;
            object2PassPlan = obj;
            entity = entity2;
            boolean passPlanFlag = obj == null || ClientThreadUtils.getObject2Deque() == obj;
            InventoryPlayerUtils.Choice slotLiving = getSlotLiving(obj, entity2, passPlanFlag, obj == null);
            if (slotLiving == null) {
               return Result.PASS;
            } else {
               LivingEntity target2 = (LivingEntity)entity2;
               int selectedSlot = inventoryPlayerMinecraftClie.player.getInventory().getSelectedSlot();
               PlayerInventoryApi playerInventoryApi = slotLiving.isPlan() ? ownerDensityBreach : slotLiving.plan().owner();
               boolean selectedSlotFlag = slotLiving.isPlan() ? swapBack : slotLiving.plan().swapBack();
               if (slotLiving.slot() == selectedSlot) {
                  handleSelectedSlotForLong(time);
               } else {
                  handleSelectedSlot(playerInventoryApi, selectedSlot, slotLiving.slot(), selectedSlotFlag, time);
               }

               if (slotLiving.isPlan()) {
                  updateDensityBreach();
               } else if (slotLiving.plan().stun()) {
                  handleDensityBreach(slotLiving.plan(), target2, time, true);
               }

               return Result.PASS;
            }
         }
      } else {
         return Result.PASS;
      }
   }

   private static boolean canRidingInstance(LivingEntity entity, boolean ridingFlag) {
      if (ridingFlag && SilentAim.getLockedHitTarget() == entity) {
         return true;
      } else {
         return inventoryPlayerMinecraftClie.attackCooldown <= 0 && !inventoryPlayerMinecraftClie.player.isRiding()
            ? inventoryPlayerMinecraftClie.crosshairTarget instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() == entity
            : false;
      }
   }

   public static boolean canValidHotbar(PlayerInventoryApi playerInventoryApi, LivingEntity entity, int validHotbarValue, boolean validHotbarFlag) {
      if (playerInventoryApi == null || entity == null || !PlayerInventory.isValidHotbarIndex(validHotbarValue)) {
         return false;
      } else {
         return !isLivingEntity() ? false : canAttackSelected(playerInventoryApi, entity, validHotbarValue, validHotbarFlag);
      }
   }

   private static void handleForInt(int inventoryPlayerValue) {
      if (inventoryPlayerValue == selectedCount) {
         if (!isSelectedSlotSelectedSlotApiSelected()) {
            if (--refreshCooldownTicks > 0) {
               InstanceTimeUtils.handleDequeArray(() -> handleForInt(inventoryPlayerValue));
               return;
            }

            updateApiSelected();
         }

         updateLivingSelected();
      }
   }

   private static InventoryPlayerUtils.Plan getPreferenceFall(Object obj, LivingEntity entity) {
      var list = getItems();
      if (list.isEmpty()) {
         return null;
      } else {
         int selectedSlot = inventoryPlayerMinecraftClie.player.getInventory().getSelectedSlot();
         int preferenceFallValue = selectedSlotApiSelected >= 0 && selectedSlot == selectedSlot4SelectedSlot ? selectedSlotApiSelected : selectedSlot;
         ItemStack empty = InventoryStackUtilsEmpty.getEmpty(preferenceFallValue);
         if (EntityPlayerUtilsGliding.canPlayerInstance(entity)) {
            int instanceSelected = InventoryStackUtilsEmpty.getInstanceSelected();
            if (instanceSelected < 0) {
               return null;
            } else {
               for (PlayerInventoryApi playerInventoryApi : list) {
                  SwapPreferenceData baseEnum = playerInventoryApi.getSwapPreference();
                  if (baseEnum.stunSlam()
                     && baseEnum.maces()
                     && playerInventoryApi.isSwapAllowed(obj, empty)
                     && EntityPlayerUtilsGliding.canGlidingGround(baseEnum.minFall())
                     && InventoryStackUtilsEmpty.getDensityInstance(baseEnum.preference()) >= 0) {
                     return new InventoryPlayerUtils.Plan(playerInventoryApi, instanceSelected, true, baseEnum.preference(), baseEnum.minFall(), baseEnum.swapBack());
                  }
               }

               for (PlayerInventoryApi playerInventoryApi2 : list) {
                  SwapPreferenceData swapPreference = playerInventoryApi2.getSwapPreference();
                  if (swapPreference.axes() && playerInventoryApi2.isSwapAllowed(obj, empty)) {
                     return new InventoryPlayerUtils.Plan(playerInventoryApi2, instanceSelected, false, swapPreference.preference(), swapPreference.minFall(), swapPreference.swapBack());
                  }
               }

               return null;
            }
         } else {
            for (PlayerInventoryApi playerInventoryApi3 : list) {
               SwapPreferenceData swapPreference3 = playerInventoryApi3.getSwapPreference();
               if (swapPreference3.maces()
                  && playerInventoryApi3.isSwapAllowed(obj, empty)
                  && (!swapPreference3.smashOnly() || EntityPlayerUtilsGliding.canGlidingGround(swapPreference3.minFall()))) {
                  int densityInstance = InventoryStackUtilsEmpty.getDensityInstance(swapPreference3.preference());
                  if (densityInstance >= 0) {
                     return new InventoryPlayerUtils.Plan(playerInventoryApi3, densityInstance, false, swapPreference3.preference(), swapPreference3.minFall(), swapPreference3.swapBack());
                  }
               }
            }

            for (PlayerInventoryApi playerInventoryApi4 : list) {
               SwapPreferenceData swapPreference4 = playerInventoryApi4.getSwapPreference();
               if (swapPreference4.swords() && playerInventoryApi4.isSwapAllowed(obj, empty)) {
                  int selectedSlot3 = InventoryStackUtilsEmpty.getSelectedSlot(preferenceFallValue);
                  if (selectedSlot3 >= 0) {
                     return new InventoryPlayerUtils.Plan(playerInventoryApi4, selectedSlot3, false, swapPreference4.preference(), swapPreference4.minFall(), swapPreference4.swapBack());
                  }
               }
            }

            return preferenceFallValue != selectedSlot && apiSelectedPlayerInventoryAp != null ? new InventoryPlayerUtils.Plan(apiSelectedPlayerInventoryAp, preferenceFallValue, false, DensityBreachType.ANY, 1.5, true) : null;
         }
      }
   }

   private static boolean isInstanceSpectator() {
      ClientPlayerEntity player = inventoryPlayerMinecraftClie.player;
      return player != null
         && inventoryPlayerMinecraftClie.interactionManager != null
         && inventoryPlayerMinecraftClie.currentScreen == null
         && !player.isUsingItem()
         && !player.isSpectator()
         && !isFlag()
         && !ClientThreadUtils.isEntityActive()
         && !TimeInstanceUtils.isInteractionBreaking();
   }

   private static void handleSelectedSlot(long inventoryLimit) {
      if (selectedSlotApiSelected >= 0) {
         if (FlagInventoryUtils.canFlag(inventoryLimit, apiSelectedValue, lastSwapTimeMs)) {
            if (livingEntity == null && inventoryPlayerMinecraftClie.currentScreen == null) {
               if (!ClientThreadUtils.isOptionsPressed() && !ClientThreadUtils.isEntityActive() && !isFlag()) {
                  if (!ClientThreadUtils.isFlag6() && !isFlag2()) {
                     ClientPlayerEntity player = inventoryPlayerMinecraftClie.player;
                     if (player.getInventory().getSelectedSlot() != selectedSlot4SelectedSlot) {
                        updateApiSelected();
                     } else {
                        player.getInventory().setSelectedSlot(selectedSlotApiSelected);
                        selectedSlot = selectedSlotApiSelected;
                        lastSwapTimeMs = inventoryLimit;
                        updateApiSelected();
                     }
                  }
               }
            }
         }
      }
   }

   private static InventoryPlayerUtils.Choice getInstanceFlag(Entity entity) {
      if (inventoryPlayerMinecraftClie != null && !attackSelectedActive) {
         Object obj = ClientThreadUtils.getObject2Deque();
         return getSlotLiving(obj != null ? obj : FALLBACK_OBJECT, entity, true, false);
      } else {
         return null;
      }
   }

   public static boolean canTime(Entity entity) {
      return entity != null && FlagInventoryUtils.canFlag(lastTargetId, lastTargetTimeMs, entity.getId(), ClientThreadUtils.getTime(), 10);
   }

   private static boolean canEntityLiving(long entityLivingValue) {
      if (livingEntity == null || densityBreachActive || !FlagInventoryUtils.canFlagForLong(entityLivingValue, densityBreachValue, inventoryPlayerValue)) {
         return false;
      } else if (ownerDensityBreach != null && ownerDensityBreach.isBreachOwnerActive()) {
         return livingEntity.isAlive() && !livingEntity.isRemoved() && livingEntity.getEntityWorld() == inventoryPlayerMinecraftClie.world
            ? EntityPlayerUtilsGliding.canGlidingGround(minFallDistance)
            : false;
      } else {
         return false;
      }
   }

   private static void updateApiSelected() {
      apiSelectedPlayerInventoryAp = null;
      selectedSlotApiSelected = -1;
      selectedSlot4SelectedSlot = -1;
      apiSelectedValue = 0L;
   }

   private static void handleSelectedSlot(PlayerInventoryApi api, int selectedSlot3, int attackSelectedValue, boolean selectedSlotFlag, long inventoryLimit) {
      inventoryPlayerMinecraftClie.player.getInventory().setSelectedSlot(attackSelectedValue);
      selectedSlot = attackSelectedValue;
      lastSwapTimeMs = inventoryLimit;
      if (selectedSlotApiSelected >= 0) {
         if (attackSelectedValue == selectedSlotApiSelected) {
            updateApiSelected();
         } else {
            selectedSlot4SelectedSlot = attackSelectedValue;
            apiSelectedValue = inventoryLimit + 2L;
         }
      } else if (selectedSlotFlag && api != null) {
         apiSelectedPlayerInventoryAp = api;
         selectedSlotApiSelected = selectedSlot3;
         selectedSlot4SelectedSlot = attackSelectedValue;
         apiSelectedValue = inventoryLimit + 2L;
      }
   }

   private static InventoryPlayerUtils.Choice getSlotLiving(Object obj, Entity entity, boolean slotLivingFlag, boolean ridingFlag) {
      if (!isSelectedSlot()) {
         return null;
      } else {
         ClientPlayerEntity player = inventoryPlayerMinecraftClie.player;
         if (!(entity instanceof LivingEntity livingEntity2 && livingEntity2 != player && livingEntity2.isAlive() && !(livingEntity2 instanceof ArmorStandEntity))) {
            return null;
         } else if (inventoryPlayerMinecraftClie.interactionManager == null || player.isUsingItem() || player.isSpectator() || isFlag()) {
            return null;
         } else if (slotLivingFlag && !canRidingInstance(livingEntity2, ridingFlag)) {
            return null;
         } else {
            long slotLivingValue = ClientThreadUtils.getTime();
            int selectedSlot = player.getInventory().getSelectedSlot();
            if (livingEntity == null) {
               InventoryPlayerUtils.Plan preferenceFall = getPreferenceFall(obj, livingEntity2);
               return preferenceFall != null && canMinimumBelow(livingEntity2, preferenceFall.slot(), selectedSlot, slotLivingValue, slotLivingFlag) ? new InventoryPlayerUtils.Choice(preferenceFall, preferenceFall.slot()) : null;
            } else if (livingEntity2 == livingEntity && canEntityLiving(slotLivingValue)) {
               int densityInstance = InventoryStackUtilsEmpty.getDensityInstance(densityBreachType);
               return canMinimumBelow(livingEntity2, densityInstance, selectedSlot, slotLivingValue, slotLivingFlag) ? new InventoryPlayerUtils.Choice(null, densityInstance) : null;
            } else {
               return null;
            }
         }
      }
   }

   private static void handleDensityBreach(InventoryPlayerUtils.Plan plan, LivingEntity livingEntity2, long time, boolean flag) {
      ownerDensityBreach = plan.owner();
      livingEntity = livingEntity2;
      densityBreachType = plan.preference();
      minFallDistance = plan.minFall();
      swapBack = plan.swapBack();
      densityBreachActive = flag;
      densityBreachValue = time;
      inventoryPlayerValue = time + 3L;
   }

   public static boolean isLivingEntity() {
      return isSelectedSlot() && livingEntity == null && isInstanceSpectator();
   }

   private static void update() {
      int inventoryPlayerValue = ++selectedCount;
      refreshCooldownTicks = 100;
      InstanceTimeUtils.handleDequeArray(() -> handleForInt(inventoryPlayerValue));
   }

   private static boolean isSelectedSlotSelectedSlotApiSelected() {
      if (selectedSlotApiSelected >= 0 && apiSelectedPlayerInventoryAp == null) {
         ClientPlayerEntity player2 = inventoryPlayerMinecraftClie.player;
         if (player2 == null || player2 != player || inventoryPlayerMinecraftClie.interactionManager == null) {
            updateApiSelected();
            return true;
         } else if (isFlag()) {
            return false;
         } else if (player2.getInventory().getSelectedSlot() != selectedSlot4SelectedSlot) {
            updateApiSelected();
            return true;
         } else {
            long selectedSlotValue = ClientThreadUtils.getTime();
            if (FlagInventoryUtils.canFlag(selectedSlotValue, lastSwapTimeMs) && !ClientThreadUtils.isEntityActive()) {
               player2.getInventory().setSelectedSlot(selectedSlotApiSelected);
               selectedSlot = selectedSlotApiSelected;
               lastSwapTimeMs = selectedSlotValue + 1L;
               updateApiSelected();
               return true;
            } else {
               return false;
            }
         }
      } else {
         return true;
      }
   }

   public static boolean isLivingSelected() {
      return livingEntity != null || selectedSlotApiSelected >= 0;
   }

   private static void handleSelectedSlotForLong(long selectedSlotValue) {
      if (selectedSlotApiSelected >= 0 && inventoryPlayerMinecraftClie.player.getInventory().getSelectedSlot() == selectedSlot4SelectedSlot) {
         apiSelectedValue = Math.max(apiSelectedValue, selectedSlotValue + 2L);
      }
   }

   public static boolean canSlotForEntity(Entity entity) {
      InventoryPlayerUtils.Choice instanceFlag = getInstanceFlag(entity);
      return instanceFlag != null && InventoryStackUtilsEmpty.canEmpty(InventoryStackUtilsEmpty.getEmpty(instanceFlag.slot()));
   }

   public static void handleTimeLiving(InventoryPlayer inventoryPlayer) {
      if (inventoryPlayer != null && !inventoryPlayer.isOpenedByClient() && inventoryPlayerMinecraftClie.player != null && inventoryPlayerMinecraftClie.isOnThread()) {
         if (inventoryPlayer.packet instanceof PlayerInteractEntityC2SPacket playerInteractEntityC2SPacke && PlayerCrystalUtils.canHandleFlag(playerInteractEntityC2SPacke)) {
            long time = ClientThreadUtils.getTime();
            int value = PlayerCrystalUtils.getEntityId(playerInteractEntityC2SPacke);
            if (livingValue != time || selectedValue != value) {
               livingValue = time;
               selectedValue = value;
               if (livingEntity != null && densityBreachActive && time == densityBreachValue && value == livingEntity.getId()) {
                  densityBreachActive = false;
               }

               if (InventoryStackUtilsEmpty.canEmpty(inventoryPlayerMinecraftClie.player.getMainHandStack()) && EntityPlayerUtilsGliding.isGlidingGround()) {
                  lastTargetTimeMs = time;
                  lastTargetId = value;
               }
            }
         }
      }
   }

   private static void handleLivingEntity(long livingEntityLimit) {
      if (livingEntity != null && livingEntityLimit > densityBreachValue) {
         if (inventoryPlayerMinecraftClie.currentScreen == null && canEntityLiving(livingEntityLimit)) {
            int densityInstance = InventoryStackUtilsEmpty.getDensityInstance(densityBreachType);
            if (densityInstance < 0) {
               updateDensityBreach();
            } else if (isInstanceSpectator()) {
               if (canAttackSelected(ownerDensityBreach, livingEntity, densityInstance, swapBack)) {
                  updateDensityBreach();
               }
            }
         } else {
            updateDensityBreach();
         }
      }
   }

   private record Choice(InventoryPlayerUtils.Plan plan, int slot) {
      boolean isPlan() {
         return this.plan == null;
      }
   }

   private record Plan(PlayerInventoryApi owner, int slot, boolean stun, DensityBreachType preference, double minFall, boolean swapBack) {
   }
}
