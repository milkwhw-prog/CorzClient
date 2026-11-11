package com.corz.client.inventory;

import com.corz.client.entity.EntityPlayerUtilsLiving;
import com.corz.client.entity.VecBlockingData;
import com.corz.client.world.AbsorptionPosData;
import com.corz.client.world.BlockStateUtilsDirections;
import com.corz.client.world.PosBlockUtilsBonus;
import com.corz.client.world.VecOffset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.DoubleSupplier;
import java.util.function.Predicate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import com.corz.client.utils.ServerPacketEventQueue;
import com.corz.client.utils.PendingEntityHitTracker;
import com.corz.client.utils.EntityDamageUtils;
import com.corz.client.utils.AmountAmounts;
import com.corz.client.utils.AmountConstTime;
import com.corz.client.utils.BowGappleType;
import com.corz.client.utils.FlagsAmounts;
import com.corz.client.utils.MinVec;

public final class LivingEntityHelperDeque {
   private List<VecBlockingData> unmodifiableList;
   private final ArrayDeque<LivingEntityHelperDeque.CrystalInfo> arrayDeque;
   private final ArrayDeque<LivingEntityHelperDeque.OwnBlast> dequeArrayOwnBlastQueue;
   private World world;
   public static final int[] vecTable = new int[]{2, 4, 6};
   private final Map<Integer, VecBlockingData> entityVecBlockingDataMap;
   private FlagsAmounts worldFlagsAmounts;
   private double minDistance;
   private static final EquipmentSlot[] equipmentSlots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
   private final Map<Integer, LivingEntityHelperDeque.Track> dequeArrayTrackMap = new HashMap<>();
   private int minVecEntityId;
   private Predicate<BlockPos> predicate;
   private final Map<Integer, LivingEntityHelperDeque.CrystalInfo> predicateBlockingCrystalInfo;
   private int livingEntityCount;
   private final ArrayDeque<LivingEntityHelperDeque.Boom> dequeArrayBoomQueue;
   private MinVec worldMinVec;

   private static double getLivingEntity(LivingEntityHelperDeque.Track track) {
      if (track.livingEntity != null && track.livingEntityActive && !Float.isNaN(track.livingEntityAmount)) {
         float livingEntityValue = track.livingEntity.getHealth() + track.livingEntity.getAbsorptionAmount();
         return getAmount(track.livingEntityAmount, livingEntityValue, 20.0F, getBlockingVec(track));
      } else {
         return Double.NaN;
      }
   }

   static BowGappleType getBowApple(ItemStack stack) {
      if (stack == null || stack.isEmpty()) {
         return BowGappleType.EMPTY;
      } else if (stack.isOf(Items.END_CRYSTAL)) {
         return BowGappleType.CRYSTAL;
      } else if (stack.isOf(Items.OBSIDIAN)) {
         return BowGappleType.OBSIDIAN;
      } else if (stack.isOf(Items.RESPAWN_ANCHOR)) {
         return BowGappleType.ANCHOR;
      } else if (stack.isOf(Items.GLOWSTONE)) {
         return BowGappleType.GLOWSTONE;
      } else if (stack.isOf(Items.TOTEM_OF_UNDYING)) {
         return BowGappleType.TOTEM;
      } else if (stack.isOf(Items.ENDER_PEARL)) {
         return BowGappleType.PEARL;
      } else if (stack.isOf(Items.SHIELD)) {
         return BowGappleType.SHIELD;
      } else if (stack.isOf(Items.GOLDEN_APPLE) || stack.isOf(Items.ENCHANTED_GOLDEN_APPLE)) {
         return BowGappleType.GAPPLE;
      } else if (InventoryStackUtilsEmpty.canEmpty(stack)) {
         return BowGappleType.MACE;
      } else if (InventoryStackUtilsEmpty.canSwordsEmpty(stack)) {
         return BowGappleType.SWORD;
      } else if (stack.isIn(ItemTags.AXES)) {
         return BowGappleType.AXE;
      } else if (stack.isOf(Items.BOW) || stack.getItem() instanceof CrossbowItem) {
         return BowGappleType.BOW;
      } else {
         return stack.contains(DataComponentTypes.FOOD) ? BowGappleType.FOOD : BowGappleType.OTHER;
      }
   }

   private void handlePredicateBlocking(ServerPacketEventQueue.Spawn spawn, long predicateBlockingValue) {
      Vec3d vec3d = new Vec3d(spawn.x(), spawn.y(), spawn.z());
      BlockPos floored = BlockPos.ofFloored(spawn.x(), spawn.y() - 0.5, spawn.z());
      int predicateBlockingLimit = Integer.MIN_VALUE;
      if (this.predicate != null && this.predicate.test(floored)) {
         predicateBlockingLimit = -2;
      } else {
         LivingEntityHelperDeque.Track track = null;
         double distanceTo2 = 5.0;

         for (LivingEntityHelperDeque.Track track2 : this.dequeArrayTrackMap.values()) {
            if (track2.livingEntity != null
               && track2.entityActive
               && track2.vecBlockingData != null
               && (this.predicate != null || track2.vecBlockingData.main() == BowGappleType.CRYSTAL || track2.vecBlockingData.off() == BowGappleType.CRYSTAL)) {
               double distanceTo = track2.livingEntity.getEyePos().distanceTo(vec3d);
               if (distanceTo <= distanceTo2) {
                  distanceTo2 = distanceTo;
                  track = track2;
               }
            }
         }

         if (track != null) {
            predicateBlockingLimit = track.predicateBlockingValue;
         }
      }

      if (predicateBlockingLimit != Integer.MIN_VALUE) {
         this.predicateBlockingCrystalInfo.put(spawn.entityId(), new LivingEntityHelperDeque.CrystalInfo(spawn.entityId(), vec3d, predicateBlockingLimit, predicateBlockingValue));

         while (this.predicateBlockingCrystalInfo.size() > 256) {
            Iterator iterator = this.predicateBlockingCrystalInfo.keySet().iterator();
            iterator.next();
            iterator.remove();
         }

         if (predicateBlockingLimit >= 0) {
            LivingEntityHelperDeque.Track track3 = this.dequeArrayTrackMap.get(predicateBlockingLimit);
            if (track3 != null) {
               track3.dequeArrayAmountConstTime.handle(spawn.entityId(), predicateBlockingValue);
            }
         }
      }
   }

   public void setPredicate(Predicate<BlockPos> predicate) {
      this.predicate = predicate;
   }

   public int getLivingEntityCount() {
      return this.livingEntityCount;
   }

   public void handleAmount(int livingEntityValue, long entityBudgetValue, float livingEntityLimit) {
      LivingEntityHelperDeque.Track track = this.dequeArrayTrackMap.get(livingEntityValue);
      if (track != null) {
         track.amountTime = getAmount(track.amountTime, EntityDamageUtils.getMin(livingEntityValue), livingEntityLimit);
      }
   }

   static int getLoopFresh(AbsorptionPosData absorptionPosData) {
      if (absorptionPosData == null) {
         return -99;
      } else {
         int ticksSinceFresh = absorptionPosData.ticksSinceFresh();
         if (ticksSinceFresh == Integer.MAX_VALUE) {
            return -99;
         } else {
            double loopFreshValue = Double.isFinite(absorptionPosData.loopMs()) ? Math.max(0.0, absorptionPosData.loopMs()) : 0.0;
            int loopFresh = PendingEntityHitTracker.getRemainingTicksFloor(ticksSinceFresh, loopFreshValue);
            if (loopFresh > 0) {
               return loopFresh;
            } else {
               int loopFreshValue2 = (int)Math.floor(10.0 - loopFreshValue / 50.0) - 1;
               return -Math.max(0, ticksSinceFresh - loopFreshValue2);
            }
         }
      }
   }

   public void handleLivingDeque(int livingDequeValue, long shieldEntityValue, float shieldEntityLimit, float livingDequeValue4) {
      LivingEntityHelperDeque.Track track = this.dequeArrayTrackMap.get(livingDequeValue);
      if (track != null) {
         int ceil5 = (int)Math.ceil(Math.max(0.0, (double)EntityDamageUtils.getTime()) / 50.0);
         float livingDequeValue6 = track.livingEntity == null ? Float.NaN : track.livingEntity.getHealth() + track.livingEntity.getAbsorptionAmount();
         track.arrayDeque.addLast(new LivingEntityHelperDeque.Fire(shieldEntityValue, shieldEntityLimit, livingDequeValue4, livingDequeValue6, shieldEntityValue + ceil5 + 3L));

         while (track.arrayDeque.size() > 8) {
            track.arrayDeque.removeFirst();
         }
      }
   }

   private void updateDequeArray() {
      this.dequeArrayTrackMap.clear();
      this.entityVecBlockingDataMap.clear();
      this.unmodifiableList = List.of();
      this.predicateBlockingCrystalInfo.clear();
      this.arrayDeque.clear();
      this.dequeArrayBoomQueue.clear();
      this.dequeArrayOwnBlastQueue.clear();
      this.minDistance = Double.POSITIVE_INFINITY;
      if (this.worldFlagsAmounts != null) {
         this.worldFlagsAmounts.updateFlags();
      }
   }

   private void handleLivingEntity(long livingEntityValue) {
      for (LivingEntityHelperDeque.Track track : this.dequeArrayTrackMap.values()) {
         if (track.livingEntity != null && track.entityActive && (track.livingEntity.isDead() || track.livingEntity.getHealth() <= 0.0F)) {
            track.entityActive = false;
            if (track.livingEntityValue != Long.MIN_VALUE && livingEntityValue - track.livingEntityValue <= 60L) {
               this.livingEntityCount++;
            }
         }
      }
   }

   private float getAmountTime(ServerPacketEventQueue.EntityDamage fresh, LivingEntityHelperDeque.Track track, long time) {
      float explosionWall = getExplosionWall(fresh.type(), () -> getLivingEntity(track), () -> this.getEntityLiving(fresh, track));
      track.lastHitTick = Long.MIN_VALUE;
      if (explosionWall == Float.MAX_VALUE && canFlyFall(fresh.type()) && track.livingEntityActive && !Float.isNaN(track.livingEntityAmount)) {
         track.lastHitTick = time;
         track.lastHealth = track.livingEntityAmount;
      }

      return explosionWall;
   }

   public void updateMinWorld() {
      this.updateDequeArray();
      this.livingEntityCount = 0;
      this.minVecEntityId = -1;
      this.worldMinVec = null;
      this.worldFlagsAmounts = null;
      this.world = null;
   }

   static Vec3d getEntityPos(LivingEntity entity) {
      return entity.getInterpolator() != null && entity.getInterpolator().isInterpolating() ? entity.getInterpolator().getLerpedPos() : entity.getEntityPos();
   }

   private void handleAmountApple(LivingEntityHelperDeque.Track track, long appleValue) {
      if (track.usedItem == Items.ENCHANTED_GOLDEN_APPLE) {
         track.appleValue = appleValue + 6000L;
         track.appleAmount = getAmount(Math.max(track.appleAmount, Math.min(20.0F, track.appleAmount) + 16.0F), 1.0F, 40.0F);
      } else if (track.usedItem == Items.GOLDEN_APPLE) {
         track.appleAmount = getAmount(Math.max(track.appleAmount, Math.min(20.0F, track.appleAmount) + 4.0F), 1.0F, 40.0F);
      }
   }

   private double getEntityLiving(ServerPacketEventQueue.EntityDamage fresh, LivingEntityHelperDeque.Track track) {
      if (track.livingEntity == null) {
         return Double.NaN;
      } else {
         Vec3d source = fresh.source();
         if (source == null) {
            LivingEntityHelperDeque.CrystalInfo crystalInfo = this.predicateBlockingCrystalInfo.get(fresh.directId());
            if (crystalInfo == null) {
               for (LivingEntityHelperDeque.CrystalInfo crystalInfo2 : this.arrayDeque) {
                  if (crystalInfo2.id() == fresh.directId()) {
                     crystalInfo = crystalInfo2;
                  }
               }
            }

            source = crystalInfo == null ? null : crystalInfo.pos();
         }

         return source == null
            ? Double.NaN
            : BlockEntityUtils.getCreativeSpectator(track.livingEntity, source, track.livingEntity.getEntityPos(), track.livingEntity.getBoundingBox(), null, null, 6.0F);
      }
   }

   public AmountAmounts getMin(int livingEntityValue) {
      return this.worldMinVec != null && livingEntityValue == this.minVecEntityId ? this.worldMinVec.getArmsAmountAmounts() : null;
   }

   public void handleDequeArray(Vec3d vec, long dequeArrayValue) {
      if (vec != null) {
         this.dequeArrayOwnBlastQueue.addLast(new LivingEntityHelperDeque.OwnBlast(vec, dequeArrayValue));

         while (this.dequeArrayOwnBlastQueue.size() > 64) {
            this.dequeArrayOwnBlastQueue.removeFirst();
         }
      }
   }

   public void handleMin(int livingEntityValue, MinVec min) {
      if (livingEntityValue != this.minVecEntityId || min != this.worldMinVec) {
         this.worldFlagsAmounts = min == null ? null : new FlagsAmounts(32);
      }

      this.minVecEntityId = min == null ? -1 : livingEntityValue;
      this.worldMinVec = min;
   }

   private static int getBlockingVec(LivingEntityHelperDeque.Track track) {
      return track.vecBlockingData != null && track.vecBlockingData.armor() != null ? track.vecBlockingData.armor().resistanceAmp() : -1;
   }

   static boolean canFlyFall(String flyFallText) {
      return "fall".equals(flyFallText) || "fly_into_wall".equals(flyFallText);
   }

   private void handle(long livingEntityValue) {
      this.dequeArrayTrackMap.values().removeIf(track -> track.lastSeenTick != Long.MIN_VALUE && livingEntityValue - track.lastSeenTick > 200L && track.predicateBlockingValue != this.minVecEntityId);
   }

   private static float getAmount(float livingEntityBound, float livingEntityLimit, float hashHuntBound) {
      return livingEntityBound < livingEntityLimit ? livingEntityLimit : Math.min(livingEntityBound, hashHuntBound);
   }

   private void handleVecFlags(LivingEntityHelperDeque.Track track, long time, Vec3d vec, boolean vecFlag, BlockStateUtilsDirections.Hole hole) {
      AmountAmounts amountAmounts = this.worldMinVec.getArmsAmountAmounts();
      if (vecFlag) {
         this.worldMinVec.getMaxMin().handleAmount(hole != null && hole.type() != BlockStateUtilsDirections.Type.NONE && hole.openSides() == 0);
      }

      Vec3d vec3d = track.vecOffset.getAmountSmoothedOffsetX();
      if (this.worldFlagsAmounts != null) {
         this.worldFlagsAmounts.handleAmountsFlags(time, vec.x, vec.z, amountAmounts);
         if (vecFlag) {
            double vecFactor = amountAmounts.getAmount();

            for (int vecTableItem : vecTable) {
               this.worldFlagsAmounts.handleFlagsAmounts(time, vecTableItem, vec.x + vec3d.x * vecTableItem * vecFactor, vec.z + vec3d.z * vecTableItem * vecFactor, vec3d.x, vec3d.z);
            }
         }
      }

      int vecValue4 = track.vecValue;
      track.vecVec3ds[vecValue4] = vec;
      track.vecTable[vecValue4] = Math.hypot(vec3d.x, vec3d.z);
      track.vecFlags[vecValue4] = vecFlag;
      track.vecValue = (vecValue4 + 1) % track.vecVec3ds.length;
      if (track.vecCount < track.vecVec3ds.length) {
         track.vecCount++;
      }

      if (track.vecCount == track.vecVec3ds.length && (track.lastVecSampleTick == Long.MIN_VALUE || time - track.lastVecSampleTick >= 4L)) {
         int vecValue42 = track.vecValue;
         boolean vecFlag2 = true;

         for (boolean vecFlag3 : track.vecFlags) {
            vecFlag2 &= vecFlag3;
         }

         if (vecFlag2) {
            Vec3d vec3d2 = track.vecVec3ds[vecValue42];
            double hypot = Math.hypot(vec.x - vec3d2.x, vec.z - vec3d2.z);
            double vecValue = track.vecTable[vecValue42] * 4.0;
            amountAmounts.handleAmountForDouble(vecValue, hypot);
            track.lastVecSampleTick = time;
         }
      }
   }

   private VecBlockingData getTimeAmount(
      LivingEntityHelperDeque.Track track, LivingEntity entity, long time, Vec3d vec, Vec3d vec2, Vec3d vec3, double livingEntityValue, int livingEntityValue2, World world2
   ) {
      track.entityName = entity.getName().getString();
      Vec3d entityPos = entity.getEntityPos();
      Vec3d eyePos = entity.getEyePos();
      boolean onGround = entity.isOnGround();
      Vec3d vec3d = new Vec3d(vec.x - vec3.x, 0.0, vec.z - vec3.z);
      track.vecOffset.handleVecFlag(vec3, onGround, vec3d);
      float health = entity.getHealth();
      float absorptionAmount = entity.getAbsorptionAmount();
      if (!Float.isFinite(health) || !Float.isFinite(absorptionAmount) || health < 0.0F || health > 1024.0F || absorptionAmount < 0.0F || absorptionAmount > 1024.0F) {
         track.livingEntityActive = false;
      }

      float amount = health + absorptionAmount;
      if (!Float.isNaN(track.livingEntityAmount) && Math.abs(amount - track.livingEntityAmount) > 0.001F) {
         track.consecutiveHitCount = 0;
      }

      this.handleTimeAmount(track, time, amount);
      track.livingEntityAmount = amount;
      if (track.expireTick != Long.MIN_VALUE && time >= track.expireTick) {
         float livingEntityLimit = track.ehpAtFire - amount;
         if (Math.abs(livingEntityLimit) <= 0.001F) {
            track.consecutiveHitCount++;
            if (track.consecutiveHitCount >= 3) {
               track.livingEntityActive = false;
            }
         } else if (track.livingEntityActive && track.damage >= 0.5F && livingEntityLimit > 0.0F) {
            float livingEntityValue3 = livingEntityLimit / track.damage;
            track.dequeArrayAmount = getAmount(track.dequeArrayAmount + 0.3F * (livingEntityValue3 - track.dequeArrayAmount), 0.6F, 1.4F);
         }

         track.expireTick = Long.MIN_VALUE;
      }

      if (track.dequeArrayValue != Long.MIN_VALUE && time - track.dequeArrayValue <= 900L) {
         if (track.lastStepTime == Long.MIN_VALUE) {
            track.lastStepTime = time;
         }

         while (time - track.lastStepTime >= 25L) {
            track.lastStepTime += 25L;
            track.appleAmount = getAmount(track.appleAmount + 1.0F, 1.0F, 40.0F);
         }
      }

      boolean usingItem = entity.isUsingItem();
      ItemStack itemStack = usingItem ? entity.getActiveItem() : ItemStack.EMPTY;
      boolean livingEntityFlag = usingItem && itemStack.contains(DataComponentTypes.FOOD);
      if (usingItem) {
         Item item = itemStack.getItem();
         if (track.usingItem && item == track.usedItem) {
            track.trackedTickCount++;
         } else {
            track.usedItem = item;
            track.trackedTickCount = 1;
         }
      } else if (track.usingItem) {
         if (track.trackedTickCount >= 28) {
            this.handleAmountApple(track, time);
         }

         track.trackedTickCount = 0;
         track.usedItem = null;
      }

      track.usingItem = usingItem;
      PosBlockUtilsBonus.Armor attributeArmor = BlockEntityUtils.getAttributeArmor(entity);
      if (track.appleValue != Long.MIN_VALUE && time < track.appleValue) {
         attributeArmor = new PosBlockUtilsBonus.Armor(attributeArmor.armor(), attributeArmor.toughness(), Math.max(attributeArmor.resistanceAmp(), 0), attributeArmor.epf(), attributeArmor.difficulty());
      }

      float registryEquipped = BlockEntityUtils.getRegistryEquipped(entity);
      float livingEntityBound = 1.0F;

      for (EquipmentSlot equipmentSlot : equipmentSlots) {
         ItemStack equippedStack = entity.getEquippedStack(equipmentSlot);
         livingEntityBound = Math.min(livingEntityBound, equippedStack.isEmpty() ? 0.0F : (float)StackEnchantmentsUtils.getDamageDamageable(equippedStack));
      }

      boolean handUndying = BlockEntityUtils.canHandUndying(entity);
      if (track.dequeUpdated) {
         if (handUndying && time - track.lastDequeUpdateTick >= 2L) {
            track.dequeUpdated = false;
            if (this.canMin(track)) {
               this.worldMinVec.getMaxMin().handleMaxAmount(time);
            }
         } else if (time - track.lastDequeUpdateTick >= 40L) {
            track.dequeUpdated = false;
         }
      }

      BowGappleType bowApple = getBowApple(entity.getMainHandStack());
      BowGappleType bowApple2 = getBowApple(entity.getOffHandStack());
      if (track.lastSampleTick == Long.MIN_VALUE || time - track.lastSampleTick >= 4L) {
         track.lastSampleTick = time;
         track.subtractRaycast = BlockEntityUtils.canSubtractRaycast(vec2, eyePos);
      }

      BlockStateUtilsDirections.Hole bedrockState = BlockStateUtilsDirections.getBedrockState(world2, BlockPos.ofFloored(vec3));
      this.handleTimeDeadline(track, time, livingEntityValue);
      boolean livingEntityFlag2 = track.deadlineValue != Long.MIN_VALUE && time < track.deadlineValue;
      int vecOffsetBound = Math.min(8, 2 + livingEntityValue2);
      double livingEntityValue5 = track.vecOffset.getAmount(vecOffsetBound);
      Vec3d vec3d2 = track.vecOffset.getVec3d(vecOffsetBound);
      if (this.canMin(track)) {
         this.handleVecFlags(track, time, vec3, onGround, bedrockState);
      }

      float livingEntityValue6 = track.livingEntityActive ? amount : track.appleAmount;
      if (this.canMin(track) && livingEntityValue6 <= 10.0F) {
         this.worldMinVec.getMaxMin().handleMaxTime(time);
      }

      return new VecBlockingData(
         track.predicateBlockingValue,
         track.entityName,
         entity,
         entityPos,
         vec3,
         track.vecOffset.getAmountSmoothedOffsetX(),
         eyePos,
         onGround,
         livingEntityValue,
         track.subtractRaycast,
         health,
         absorptionAmount,
         track.livingEntityActive,
         track.appleAmount,
         attributeArmor,
         registryEquipped,
         livingEntityBound,
         track.dequeArrayAmount,
         handUndying,
         bowApple,
         bowApple2,
         livingEntityFlag,
         entity.isBlocking(),
         EntityDamageUtils.getMin(track.predicateBlockingValue),
         track.amountTime,
         track.dequeArrayActive,
         track.useTickCount,
         track.dequeArrayValue,
         track.dequeUpdated,
         bedrockState,
         track.vecOffset.getAmount(),
         livingEntityValue5,
         vec3d2,
         livingEntityFlag2
      );
   }

   static boolean canFlag(float livingEntityLimit, long livingEntityValue, long livingEntityLimit2) {
      return !(livingEntityLimit >= 1.0F) ? false : livingEntityLimit2 == Long.MIN_VALUE || livingEntityLimit2 < livingEntityValue - 10L || livingEntityLimit2 > livingEntityValue;
   }

   static float getAmount(float livingEntityValue, float livingEntityValue2, float routedPartialValue, int livingEntityValue4) {
      float livingEntityLimit = livingEntityValue - livingEntityValue2;
      if (Float.isFinite(livingEntityLimit) && livingEntityLimit > 0.01F) {
         float livingEntityLimit2 = livingEntityValue4 < 0 ? 1.0F : 1.0F - 0.2F * (livingEntityValue4 + 1);
         float livingEntityLimit3 = 1.0F - Math.min(20.0F, Math.max(0.0F, Float.isFinite(routedPartialValue) ? routedPartialValue : 0.0F)) / 25.0F;
         return livingEntityLimit2 > 0.0F && livingEntityLimit3 > 0.0F ? livingEntityLimit / (livingEntityLimit2 * livingEntityLimit3) : Float.MAX_VALUE;
      } else {
         return Float.MAX_VALUE;
      }
   }

   static float getExplosionWall(String explosionWallText, DoubleSupplier doubleSupplier, DoubleSupplier doubleSupplier2) {
      if (explosionWallText == null) {
         return Float.MAX_VALUE;
      } else {
         return switch (explosionWallText) {
            case "in_fire", "on_fire", "campfire", "in_wall", "cactus", "hot_floor", "sweet_berry_bush", "freeze", "starve" -> 1.0F;
            case "drown" -> 2.0F;
            case "lava" -> 4.0F;
            case "ender_pearl" -> 5.0F;
            case "cramming" -> 6.0F;
            case "fall", "fly_into_wall" -> getAmount(doubleSupplier);
            case "explosion", "player_explosion", "bad_respawn_point" -> getAmount(doubleSupplier2);
            default -> Float.MAX_VALUE;
         };
      }
   }

   private void handleDequeArray(long dequeArrayValue, AbsorptionPosData absorptionPosData, Vec3d vec, int dequeArrayValue2) {
      while (!this.dequeArrayBoomQueue.isEmpty()) {
         LivingEntityHelperDeque.Boom removeFirst = this.dequeArrayBoomQueue.removeFirst();
         Vec3d center = removeFirst.center();
         boolean dequeArrayFlag = false;

         for (LivingEntityHelperDeque.OwnBlast ownBlast : this.dequeArrayOwnBlastQueue) {
            if (dequeArrayValue - ownBlast.tick() <= dequeArrayValue2 + 2 && ownBlast.center().distanceTo(center) <= 0.5) {
               dequeArrayFlag = true;
               break;
            }
         }

         LivingEntityHelperDeque.CrystalInfo crystalInfo = dequeArrayFlag ? null : this.getNext(center);
         if (!dequeArrayFlag && crystalInfo != null && crystalInfo.owner() == -2 && !this.canBlockingVec(center, dequeArrayValue)) {
            dequeArrayFlag = true;
         }

         if (!dequeArrayFlag) {
            LivingEntityHelperDeque.Track track = null;
            if (crystalInfo != null && crystalInfo.owner() >= 0) {
               track = this.dequeArrayTrackMap.get(crystalInfo.owner());
            }

            if (track == null) {
               double dequeArrayLimit = 6.0;

               for (LivingEntityHelperDeque.Track track2 : this.dequeArrayTrackMap.values()) {
                  if (track2.vecBlockingData != null && track2.lastSeenTick == dequeArrayValue && track2.vecBlockingData.serverPos() != null) {
                     double distanceTo = track2.vecBlockingData.serverPos().distanceTo(center);
                     if (distanceTo <= dequeArrayLimit) {
                        dequeArrayLimit = distanceTo;
                        track = track2;
                     }
                  }
               }
            }

            if (track != null) {
               long lastEventTimeMs = track.dequeArrayAmountConstTime.getLastEventTimeMs();
               boolean dequeArrayFlag2 = vec != null && center.distanceTo(vec) <= 6.0;
               track.dequeArrayAmountConstTime.handleDequeArray(dequeArrayValue, dequeArrayFlag2, getLoopFresh(absorptionPosData), center.x, center.y, center.z, removeFirst.radius());
               if (this.canMin(track) && lastEventTimeMs != Long.MIN_VALUE) {
                  long limit = dequeArrayValue - lastEventTimeMs;
                  if (limit > 0L && limit <= 80L) {
                     this.worldMinVec.getMaxMin().handleAmount(limit);
                  }
               }
            }
         }
      }

      while (!this.dequeArrayOwnBlastQueue.isEmpty() && dequeArrayValue - this.dequeArrayOwnBlastQueue.peekFirst().tick() > dequeArrayValue2 + 2) {
         this.dequeArrayOwnBlastQueue.removeFirst();
      }
   }

   static float getAmount(float livingEntityBound, int livingEntityValue, float livingEntityLimit) {
      return livingEntityValue < 10 && Float.isFinite(livingEntityLimit) && livingEntityLimit > 0.0F ? Math.max(livingEntityBound, livingEntityLimit) : livingEntityBound;
   }

   private void handleDequeArray(long dequeArrayValue) {
      while (!this.arrayDeque.isEmpty() && dequeArrayValue - this.arrayDeque.peekFirst().tick() > 4L) {
         this.arrayDeque.removeFirst();
      }
   }

   public double getMin() {
      return this.minDistance;
   }

   public void handleEntityTime(long time, AbsorptionPosData absorptionPosData, EntityPlayerUtilsLiving.Options options, double entityValue, int entityLimit, double groundY) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientWorld world = instance.world;
      ClientPlayerEntity player = instance.player;
      if (world != null && player != null) {
         if (world != this.world) {
            this.updateDequeArray();
            this.world = world;
         }

         EntityPlayerUtilsLiving.Options options3 = options == null ? EntityPlayerUtilsLiving.Options.INSTANCE2 : options;
         Vec3d vec3d = absorptionPosData != null ? absorptionPosData.pos() : player.getEntityPos();
         Vec3d vec3d2 = absorptionPosData != null ? absorptionPosData.eye() : player.getEyePos();
         double entityBound = absorptionPosData != null ? absorptionPosData.loopMs() : EntityDamageUtils.getTime();
         int ceil3 = (int)Math.ceil(Math.max(0.0, entityBound) / 50.0);
         this.handleLivingEntity(time);
         double min = Double.POSITIVE_INFINITY;
         ArrayList arrayList = new ArrayList();
         this.entityVecBlockingDataMap.clear();

         for (Entity entity : !options3.mobs() && !options3.animals() ? world.getPlayers() : world.getEntities()) {
            if (entity instanceof LivingEntity livingEntity
               && entity != player
               && EntityPlayerUtilsLiving.canSpectatorDead(entity, options3)
               && !(entity instanceof PlayerEntity playerEntity && (playerEntity.isCreative() || playerEntity.isSpectator()))) {
               Vec3d entityPos = getEntityPos(livingEntity);
               double distanceTo = vec3d.distanceTo(entityPos);
               min = Math.min(min, distanceTo);
               int id = livingEntity.getId();
               LivingEntityHelperDeque.Track track = this.dequeArrayTrackMap.get(id);
               boolean entityFlag = id == entityLimit && entityLimit >= 0;
               if (entityFlag && Double.isFinite(groundY) && distanceTo > groundY) {
                  if (track == null) {
                     continue;
                  }

                  if (track.entityValue == Long.MIN_VALUE) {
                     track.entityValue = time;
                  }

                  if (time - track.entityValue >= 40L) {
                     continue;
                  }
               } else {
                  if (!entityFlag && distanceTo > entityValue) {
                     continue;
                  }

                  if (track != null) {
                     track.entityValue = Long.MIN_VALUE;
                  }
               }

               arrayList.add(new LivingEntityHelperDeque.Candidate(livingEntity, entityPos, distanceTo, entityFlag));
            }
         }

         arrayList.sort(
            Comparator.<LivingEntityHelperDeque.Candidate, Boolean>comparing(candidate -> !candidate.keep()).thenComparingDouble(LivingEntityHelperDeque.Candidate::dist)
         );
         ArrayList arrayList2 = new ArrayList();

         for (LivingEntityHelperDeque.Candidate candidate2 : (Iterable<com.corz.client.inventory.LivingEntityHelperDeque.Candidate>) (arrayList)) {
            if (arrayList2.size() >= 64) {
               break;
            }

            int id2 = candidate2.entity().getId();
            LivingEntityHelperDeque.Track ifAbsent = this.dequeArrayTrackMap.computeIfAbsent(id2, LivingEntityHelperDeque.Track::new);
            ifAbsent.livingEntity = candidate2.entity();
            ifAbsent.lastSeenTick = time;
            ifAbsent.entityActive = true;
            VecBlockingData vecBlockingData = this.getTimeAmount(ifAbsent, candidate2.entity(), time, vec3d, vec3d2, candidate2.serverPos(), candidate2.dist(), ceil3, world);
            ifAbsent.vecBlockingData = vecBlockingData;
            this.entityVecBlockingDataMap.put(id2, vecBlockingData);
            arrayList2.add(vecBlockingData);
         }

         this.minDistance = min;
         arrayList2.sort(Comparator.comparingDouble(VecBlockingData::distance));
         this.unmodifiableList = Collections.unmodifiableList(arrayList2);
         this.handleDequeArray(time, absorptionPosData, vec3d, ceil3);
         this.handle(time);
         this.handleDequeArray(time);
      } else {
         this.unmodifiableList = List.of();
         this.entityVecBlockingDataMap.clear();
         this.minDistance = Double.POSITIVE_INFINITY;
      }
   }

   public LivingEntityHelperDeque() {
      this.entityVecBlockingDataMap = new HashMap<>();
      this.unmodifiableList = List.of();
      this.predicateBlockingCrystalInfo = new HashMap<>();
      this.arrayDeque = new ArrayDeque<>();
      this.dequeArrayBoomQueue = new ArrayDeque<>();
      this.dequeArrayOwnBlastQueue = new ArrayDeque<>();
      this.minDistance = Double.POSITIVE_INFINITY;
      this.minVecEntityId = -1;
   }

   public List<VecBlockingData> getUnmodifiableList() {
      return this.unmodifiableList;
   }

   private boolean canMin(LivingEntityHelperDeque.Track track) {
      return this.worldMinVec != null && track.predicateBlockingValue == this.minVecEntityId;
   }

   private static float getAmount(DoubleSupplier doubleSupplier) {
      double livingEntityLimit = doubleSupplier == null ? Double.NaN : doubleSupplier.getAsDouble();
      return Double.isFinite(livingEntityLimit) && livingEntityLimit > 0.0 && livingEntityLimit < Float.MAX_VALUE ? (float)livingEntityLimit : Float.MAX_VALUE;
   }

   public void handle(int livingEntityValue) {
      LivingEntityHelperDeque.Track track = this.dequeArrayTrackMap.get(livingEntityValue);
      if (track != null) {
         track.useTickCount = 0;
      }
   }

   private LivingEntityHelperDeque.CrystalInfo getNext(Vec3d vec) {
      LivingEntityHelperDeque.CrystalInfo next = null;
      double livingEntityLimit = 0.5;

      for (LivingEntityHelperDeque.CrystalInfo crystalInfo : this.predicateBlockingCrystalInfo.values()) {
         double distanceTo = crystalInfo.pos().distanceTo(vec);
         if (distanceTo <= livingEntityLimit) {
            livingEntityLimit = distanceTo;
            next = crystalInfo;
         }
      }

      for (LivingEntityHelperDeque.CrystalInfo crystalInfo2 : this.arrayDeque) {
         double distanceTo2 = crystalInfo2.pos().distanceTo(vec);
         if (distanceTo2 <= livingEntityLimit) {
            livingEntityLimit = distanceTo2;
            next = crystalInfo2;
         }
      }

      return next;
   }

   private void handleTimeDeadline(LivingEntityHelperDeque.Track track, long deadlineLimit, double deadlineValue) {
      Iterator iterator = track.arrayDeque.iterator();

      while (iterator.hasNext()) {
         LivingEntityHelperDeque.Fire fire = (LivingEntityHelperDeque.Fire)iterator.next();
         if (deadlineLimit > fire.deadline()) {
            iterator.remove();
            if (deadlineValue <= 6.0 && track.entityActive && canFlag(fire.damage(), fire.tick(), track.lastFireTick)) {
               track.deadlineCount++;
               if (track.deadlineCount >= 6) {
                  track.deadlineValue = deadlineLimit + 100L;
                  track.deadlineCount = 0;
               }
            }
         }
      }
   }

   private void handleTimeAmount(LivingEntityHelperDeque.Track track, long livingEntityValue, float livingEntityValue2) {
      if (track.lastHitTick != Long.MIN_VALUE) {
         if (track.lastFireTick == track.lastHitTick && livingEntityValue - track.lastHitTick <= 2L && track.livingEntityActive) {
            float amount = getAmount(track.lastHealth, livingEntityValue2, 20.0F, getBlockingVec(track));
            if (amount != Float.MAX_VALUE) {
               track.amountTime = amount;
               track.lastHitTick = Long.MIN_VALUE;
            }
         } else {
            track.lastHitTick = Long.MIN_VALUE;
         }
      }
   }

   private boolean canBlockingVec(Vec3d vec, long blockingVecValue) {
      for (LivingEntityHelperDeque.Track track : this.dequeArrayTrackMap.values()) {
         if (track.blockingVecValue != Long.MIN_VALUE
            && blockingVecValue - track.blockingVecValue <= 3L
            && track.vecBlockingData != null
            && track.vecBlockingData.serverPos() != null
            && track.vecBlockingData.serverPos().distanceTo(vec) <= 6.0) {
            return true;
         }
      }

      return false;
   }

   public void handleDequeArray(ServerPacketEventQueue.Event event, long time, int dequeArrayLimit) {
      if (event instanceof ServerPacketEventQueue.Pop pop) {
         LivingEntityHelperDeque.Track track = this.dequeArrayTrackMap.get(pop.entityId());
         if (track == null) {
            return;
         }

         track.useTickCount++;
         track.dequeArrayValue = time;
         track.dequeUpdated = true;
         track.lastDequeUpdateTick = time;
         track.appleAmount = 9.0F;
         track.lastStepTime = time;
         track.dequeArrayAmount = 1.0F;
         track.expireTick = Long.MIN_VALUE;
         track.appleValue = Long.MIN_VALUE;
         if (this.canMin(track)) {
            this.worldMinVec.getMaxMin().handleMax(time);
         }
      } else if (event instanceof ServerPacketEventQueue.EntityDamage fresh) {
         LivingEntityHelperDeque.Track track2 = this.dequeArrayTrackMap.get(fresh.entityId());
         if (track2 == null) {
            return;
         }

         boolean flag = fresh.causeId() == dequeArrayLimit && dequeArrayLimit >= 0;
         track2.dequeArrayActive = flag;
         track2.lastFireTick = time;
         if (!flag) {
            track2.amountTime = this.getAmountTime(fresh, track2, time);
            return;
         }

         track2.livingEntityValue = time;
         track2.deadlineCount = 0;
         track2.deadlineValue = Long.MIN_VALUE;
         LivingEntityHelperDeque.Fire fire = null;
         Iterator iterator = track2.arrayDeque.iterator();

         while (iterator.hasNext()) {
            LivingEntityHelperDeque.Fire livingEntityHelperDequeFire = (LivingEntityHelperDeque.Fire)iterator.next();
            if (time >= livingEntityHelperDequeFire.tick() && time <= livingEntityHelperDequeFire.deadline()) {
               fire = livingEntityHelperDequeFire;
               iterator.remove();
               break;
            }
         }

         if (fire == null) {
            track2.amountTime = Float.MAX_VALUE;
            return;
         }

         track2.amountTime = fire.scaled();
         track2.appleAmount = getAmount(track2.appleAmount - fire.damage(), 1.0F, 40.0F);
         if (fire.damage() >= 0.5F && Float.isFinite(fire.ehpAtFire())) {
            track2.expireTick = time + 2L;
            track2.ehpAtFire = fire.ehpAtFire();
            track2.damage = fire.damage();
         }
      } else if (event instanceof ServerPacketEventQueue.Spawn spawn) {
         if (spawn.type() != EntityType.END_CRYSTAL) {
            return;
         }

         this.handlePredicateBlocking(spawn, time);
      } else if (event instanceof ServerPacketEventQueue.Destroy destroy) {
         if (destroy.ids() == null) {
            return;
         }

         for (int dequeArrayValue : destroy.ids()) {
            LivingEntityHelperDeque.CrystalInfo crystalInfo = this.predicateBlockingCrystalInfo.remove(dequeArrayValue);
            if (crystalInfo != null) {
               if (crystalInfo.owner() >= 0) {
                  LivingEntityHelperDeque.Track track3 = this.dequeArrayTrackMap.get(crystalInfo.owner());
                  if (track3 != null) {
                     track3.dequeArrayAmountConstTime.handleAmountFlag(dequeArrayValue, time);
                  }
               }

               this.arrayDeque.addLast(new LivingEntityHelperDeque.CrystalInfo(dequeArrayValue, crystalInfo.pos(), crystalInfo.owner(), time));

               while (this.arrayDeque.size() > 64) {
                  this.arrayDeque.removeFirst();
               }
            }
         }
      } else if (event instanceof ServerPacketEventQueue.Boom boom) {
         if (boom.center() != null) {
            this.dequeArrayBoomQueue.addLast(new LivingEntityHelperDeque.Boom(boom.center(), boom.radius()));
         }

         while (this.dequeArrayBoomQueue.size() > 64) {
            this.dequeArrayBoomQueue.removeFirst();
         }
      } else if (event instanceof ServerPacketEventQueue.Swing swing) {
         LivingEntityHelperDeque.Track track4 = this.dequeArrayTrackMap.get(swing.entityId());
         if (track4 == null) {
            return;
         }

         track4.blockingVecValue = time;
         track4.dequeArrayAmountConstTime.setTime(time);
      }
   }

   public VecBlockingData getVecBlockingData(int vecBlockingValue) {
      return this.entityVecBlockingDataMap.get(vecBlockingValue);
   }

   public AmountConstTime getClassForInt(int classValue) {
      LivingEntityHelperDeque.Track track = this.dequeArrayTrackMap.get(classValue);
      return track != null ? track.dequeArrayAmountConstTime : new AmountConstTime();
   }

   public VecOffset getVecOffset(int vecOffsetValue) {
      LivingEntityHelperDeque.Track track = this.dequeArrayTrackMap.get(vecOffsetValue);
      return track != null ? track.vecOffset : new VecOffset();
   }

   private record Boom(Vec3d center, float radius) {
   }

   private record Candidate(LivingEntity entity, Vec3d serverPos, double dist, boolean keep) {
   }

   private record CrystalInfo(int id, Vec3d pos, int owner, long tick) {
   }

   private record Fire(long tick, float scaled, float damage, float ehpAtFire, long deadline) {
   }

   private record OwnBlast(Vec3d center, long tick) {
   }

   private static final class Track {
      int useTickCount;
      long lastStepTime;
      String entityName = "";
      float lastHealth;
      boolean subtractRaycast;
      float damage;
      final boolean[] vecFlags;
      int consecutiveHitCount;
      int deadlineCount;
      int vecValue;
      long dequeArrayValue;
      boolean livingEntityActive;
      int trackedTickCount;
      boolean usingItem;
      int vecCount;
      long livingEntityValue;
      LivingEntity livingEntity;
      long blockingVecValue;
      boolean dequeArrayActive;
      long lastHitTick;
      final ArrayDeque<LivingEntityHelperDeque.Fire> arrayDeque;
      long lastVecSampleTick;
      float livingEntityAmount;
      long lastDequeUpdateTick;
      long deadlineValue;
      long entityValue;
      float amountTime;
      Item usedItem;
      VecBlockingData vecBlockingData;
      final double[] vecTable;
      final Vec3d[] vecVec3ds;
      float appleAmount;
      boolean dequeUpdated;
      float dequeArrayAmount;
      final AmountConstTime dequeArrayAmountConstTime;
      long lastFireTick;
      long expireTick;
      final int predicateBlockingValue;
      long lastSampleTick;
      boolean entityActive;
      float ehpAtFire;
      final VecOffset vecOffset = new VecOffset();
      long lastSeenTick;
      long appleValue;

      Track(int value) {
         this.dequeArrayAmountConstTime = new AmountConstTime();
         this.subtractRaycast = true;
         this.lastSampleTick = Long.MIN_VALUE;
         this.dequeArrayValue = Long.MIN_VALUE;
         this.lastDequeUpdateTick = Long.MIN_VALUE;
         this.appleAmount = 20.0F;
         this.lastStepTime = Long.MIN_VALUE;
         this.livingEntityActive = true;
         this.livingEntityAmount = Float.NaN;
         this.dequeArrayAmount = 1.0F;
         this.expireTick = Long.MIN_VALUE;
         this.amountTime = Float.MAX_VALUE;
         this.livingEntityValue = Long.MIN_VALUE;
         this.lastFireTick = Long.MIN_VALUE;
         this.lastHitTick = Long.MIN_VALUE;
         this.appleValue = Long.MIN_VALUE;
         this.arrayDeque = new ArrayDeque<>();
         this.deadlineValue = Long.MIN_VALUE;
         this.lastSeenTick = Long.MIN_VALUE;
         this.entityValue = Long.MIN_VALUE;
         this.blockingVecValue = Long.MIN_VALUE;
         this.entityActive = true;
         this.vecVec3ds = new Vec3d[5];
         this.vecTable = new double[5];
         this.vecFlags = new boolean[5];
         this.lastVecSampleTick = Long.MIN_VALUE;
         this.predicateBlockingValue = value;
      }
   }
}
