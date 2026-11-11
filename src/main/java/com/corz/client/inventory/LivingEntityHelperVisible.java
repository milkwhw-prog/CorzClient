package com.corz.client.inventory;

import com.corz.client.world.PosVecUtils;
import com.corz.client.world.VecPosUtilsAmount;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.VelocityCpacket;
import com.corz.client.utils.AmountAmounts;
import com.corz.client.utils.PingTargetData;
import com.corz.client.utils.WeakFullUtils;

public final class LivingEntityHelperVisible {
   private static final EquipmentSlot[] equipmentSlots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
   private long lastUpdateTick;
   private double equipmentAttributeBonus;
   private boolean visibleActive;
   private long groundValue;
   private double maxVisibleAt;
   private int streakCount;
   private static final Item[] attributesEquipmentItems = new Item[]{Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS};
   private Vec3d entityInstance;
   private final ArrayList<LivingEntityHelperVisible.BlastExpect> sentBlastExpectList;
   private long attributesEquipmentValue;
   private final ArrayList<LivingEntityHelperVisible.Hist> groundHistList = new ArrayList<>(64);
   private int trackedEntityId;
   private long fullValue;
   private double visibleAmount;
   private LivingEntityHelperVisible.Seed groundMax;
   private long visibleValue;
   private boolean groundActive;
   private WeakFullUtils.Lift weakFullType;
   private long interpolatorValue;
   private PingTargetData interpolatorPingTargetData;
   private double y;
   private PosVecUtils.Sample visibleSample;
   private PosVecUtils.Terrain handler;
   private final ArrayList<LivingEntityHelperVisible.Kick> visibleKickList = new ArrayList<>(8);
   private AmountAmounts groundAmountAmounts;
   private final ArrayList<LivingEntityHelperVisible.VelocitySeen> velocityVelocitySeenList = new ArrayList<>(16);
   private double attributesEquipmentAmount;

   private int getValue(long livingEntityValue, long livingEntityValue2) {
      if (livingEntityValue != Long.MIN_VALUE && livingEntityValue2 != Long.MIN_VALUE) {
         long livingEntityBound = livingEntityValue2 - livingEntityValue + Math.round(this.interpolatorPingTargetData.getAmount());
         return (int)Math.max(0L, Math.min(24L, livingEntityBound));
      } else {
         return (int)Math.round(this.interpolatorPingTargetData.getAmount());
      }
   }

   private void handleTimeGround(long time, PosVecUtils.Sample data, boolean flag, boolean groundFlag) {
      if (flag || groundFlag) {
         this.groundValue = time;
      }

      this.visibleSample = data;
      this.lastUpdateTick = time;
      this.groundActive = flag;
      if (this.groundHistList.size() >= 64) {
         this.groundHistList.remove(0);
      }

      this.groundHistList.add(new LivingEntityHelperVisible.Hist(time, data.x(), data.y(), data.z(), !data.onGround()));
   }

   public boolean canTime(long livingEntityValue) {
      return this.visibleValue != Long.MIN_VALUE && this.visibleValue > livingEntityValue;
   }

   private static Vec3d getEntityInstance(Vec3d vec) {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (player != null && vec != null) {
         Vec3d entityPos = player.getEntityPos();
         return getVec3d(new Vec3d(entityPos.x - vec.x, 0.0, entityPos.z - vec.z));
      } else {
         return new Vec3d(0.0, 0.0, 0.0);
      }
   }

   public double getTick(long livingEntityLimit) {
      double naN = Double.NaN;
      double livingEntityBound = Double.NEGATIVE_INFINITY;

      for (LivingEntityHelperVisible.Hist hist : this.groundHistList) {
         if (hist.tick() <= livingEntityLimit) {
            naN = hist.y();
         } else {
            livingEntityBound = Math.max(livingEntityBound, hist.y());
         }
      }

      return !Double.isNaN(naN) && livingEntityBound != Double.NEGATIVE_INFINITY ? livingEntityBound - naN : 0.0;
   }

   public void handleHandler(int livingEntityValue) {
      PingTargetData data = this.interpolatorPingTargetData;
      PosVecUtils.Terrain handler = this.handler;
      this.updateTimeFull();
      this.trackedEntityId = livingEntityValue;
      this.interpolatorPingTargetData = data;
      this.handler = handler;
   }

   private LivingEntityHelperVisible.Seed getGroundMax(long groundValue) {
      if (this.visibleSample == null) {
         return null;
      } else {
         int groundBound = this.getValue(this.lastUpdateTick, groundValue);
         double groundFactor = this.groundAmountAmounts == null ? 1.0 : this.groundAmountAmounts.getAmount();
         PosVecUtils.Sample visibleSample59 = this.visibleSample;
         if (visibleSample59.onGround() && groundFactor != 1.0 && Double.isFinite(groundFactor)) {
            visibleSample59 = new PosVecUtils.Sample(visibleSample59.x(), visibleSample59.y(), visibleSample59.z(), visibleSample59.vx() * groundFactor, visibleSample59.vy(), visibleSample59.vz() * groundFactor, true, visibleSample59.sigma());
         }

         ArrayList arrayList = new ArrayList(this.visibleKickList.size());

         for (LivingEntityHelperVisible.Kick kick : this.visibleKickList) {
            int rocketsBuyBound = (int)Math.round(kick.visibleAt() - this.lastUpdateTick);
            if (rocketsBuyBound <= groundBound) {
               arrayList.add(new PosVecUtils.Kick(Math.max(0, rocketsBuyBound), kick.vx(), kick.vy(), kick.vz(), kick.set()));
            }
         }

         double groundFactor2 = this.groundAmountAmounts == null ? 0.0 : this.groundAmountAmounts.getSpeedOffset();
         Vec3d vec3d = getVec3d(this.entityInstance);
         PosVecUtils.Sample sample;
         if (groundBound <= 0 && arrayList.isEmpty()) {
            sample = visibleSample59;
         } else {
            PosVecUtils.Path ground = PosVecUtils.getGround(
               visibleSample59, Math.max(0, groundBound), this.handler, groundFactor2 * vec3d.x, groundFactor2 * vec3d.z, true, visibleSample59.onGround() ? 0.05 : 0.026, arrayList
            );
            sample = ground.getDataForInt(Math.max(0, groundBound));
         }

         if (sample.sigma() > 1.2) {
            sample = new PosVecUtils.Sample(sample.x(), sample.y(), sample.z(), sample.vx(), sample.vy(), sample.vz(), sample.onGround(), 1.2);
         }

         boolean groundFlag = this.visibleActive && sample.onGround();
         return new LivingEntityHelperVisible.Seed(sample, groundFlag, this.streakCount, this.equipmentAttributeBonus, this.attributesEquipmentAmount, this.weakFullType, this.interpolatorPingTargetData, this.lastUpdateTick);
      }
   }

   private void handleSentMax(long sentLimit, double sentValue) {
      if (this.groundAmountAmounts != null && !this.sentBlastExpectList.isEmpty()) {
         long sentLimit2 = (long)Math.ceil(this.interpolatorPingTargetData.getPingMs()) + this.interpolatorPingTargetData.getMax() + 4L;
         LivingEntityHelperVisible.BlastExpect blastExpect = null;

         for (LivingEntityHelperVisible.BlastExpect blastExpect2 : this.sentBlastExpectList) {
            if (sentLimit >= blastExpect2.sentTick() && sentLimit - blastExpect2.sentTick() <= sentLimit2) {
               blastExpect = blastExpect2;
            }
         }

         if (blastExpect != null) {
            this.sentBlastExpectList.remove(blastExpect);
            double groundY = sentValue - blastExpect.vyBefore();
            if (blastExpect.kbY() > 0.0 && Double.isFinite(groundY)) {
               this.groundAmountAmounts.handleAmountMeleeScale(blastExpect.kbY(), groundY);
            }
         }
      }
   }

   private void handleAttributesEquipment(LivingEntity entity, long time) {
      this.attributesEquipmentValue = time;
      int attributesEquipmentCounter = 0;
      int[] ints = new int[equipmentSlots.length];

      for (int i = 0; i < equipmentSlots.length; i++) {
         ItemStack equippedStack = entity.getEquippedStack(equipmentSlots[i]);
         if (equippedStack != null && !equippedStack.isEmpty()) {
            if (equippedStack.isOf(attributesEquipmentItems[i])) {
               attributesEquipmentCounter++;
            }

            ints[i] = InventoryStackUtilsEmpty.getEnchantmentsDefault(equippedStack, Enchantments.BLAST_PROTECTION);
         }
      }

      this.equipmentAttributeBonus = VecPosUtilsAmount.getAmount(attributesEquipmentCounter);
      double attributesEquipmentBound = 0.0;
      if (entity.getAttributes() != null && entity.getAttributes().hasAttribute(EntityAttributes.EXPLOSION_KNOCKBACK_RESISTANCE)) {
         attributesEquipmentBound = entity.getAttributeValue(EntityAttributes.EXPLOSION_KNOCKBACK_RESISTANCE);
      }

      double amount = Double.isFinite(attributesEquipmentBound) ? Math.max(0.0, Math.min(1.0, attributesEquipmentBound)) : 0.0;
      if (amount <= 0.0) {
         amount = VecPosUtilsAmount.getAmount(ints);
      }

      this.attributesEquipmentAmount = amount;
      this.weakFullType = WeakFullUtils.getWeakFull(this.attributesEquipmentAmount);
   }

   public void updateTimeFull() {
      this.groundHistList.clear();
      this.visibleKickList.clear();
      this.velocityVelocitySeenList.clear();
      this.sentBlastExpectList.clear();
      this.trackedEntityId = -1;
      this.visibleSample = null;
      this.lastUpdateTick = Long.MIN_VALUE;
      this.groundValue = Long.MIN_VALUE;
      this.groundActive = false;
      this.visibleActive = false;
      this.streakCount = 0;
      this.equipmentAttributeBonus = 0.0;
      this.attributesEquipmentAmount = 0.0;
      this.weakFullType = WeakFullUtils.Lift.FULL;
      this.attributesEquipmentValue = Long.MIN_VALUE;
      this.visibleValue = Long.MIN_VALUE;
      this.fullValue = Long.MIN_VALUE;
      this.y = 0.0;
      this.groundMax = null;
      this.interpolatorValue = Long.MIN_VALUE;
      this.visibleAmount = 0.0;
      this.maxVisibleAt = Double.NaN;
   }

   public Vec3d getVec3d() {
      return this.entityInstance;
   }

   public Vec3d getTickForLong(long livingEntityLimit) {
      LivingEntityHelperVisible.Hist hist = null;

      for (LivingEntityHelperVisible.Hist hist2 : this.groundHistList) {
         if (hist2.tick() <= livingEntityLimit) {
            hist = hist2;
         }
      }

      return hist == null ? null : new Vec3d(hist.x(), hist.y(), hist.z());
   }

   public boolean canTick(long livingEntityValue, double livingEntityValue2) {
      for (LivingEntityHelperVisible.VelocitySeen velocitySeen : this.velocityVelocitySeenList) {
         if (velocitySeen.tick() > livingEntityValue && velocitySeen.vy() >= livingEntityValue2) {
            return true;
         }
      }

      return false;
   }

   public static Vec3d getVec3d(Vec3d vec) {
      if (vec == null) {
         return new Vec3d(0.0, 0.0, 0.0);
      } else {
         double distance = Math.sqrt(vec.x * vec.x + vec.z * vec.z);
         return !(distance > 1.0E-6) ? new Vec3d(0.0, 0.0, 0.0) : new Vec3d(vec.x / distance, 0.0, vec.z / distance);
      }
   }

   private void handleVisibleTime(long time, Vec3d vec, boolean visibleFlag) {
      if (this.visibleSample == null) {
         boolean visibleFlag2 = !visibleFlag;
         this.handleTimeGround(time, new PosVecUtils.Sample(vec.x, vec.y, vec.z, 0.0, visibleFlag2 ? 0.0 : -0.0784, 0.0, !visibleFlag2, 0.15), false, true);
         this.streakCount = visibleFlag2 ? 0 : 1;
         this.visibleActive = !visibleFlag2;
      } else {
         double deltaX = vec.x - this.visibleSample.x();
         double deltaY = vec.y - this.visibleSample.y();
         double deltaZ = vec.z - this.visibleSample.z();
         double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
         if (distance <= 1.0E-4) {
            if (time - this.lastUpdateTick >= 3L
               && (Math.abs(this.visibleSample.vx()) > 0.0 || Math.abs(this.visibleSample.vz()) > 0.0 || !this.visibleSample.onGround() || this.visibleSample.vy() != -0.0784)) {
               this.handleTimeGround(time, new PosVecUtils.Sample(vec.x, vec.y, vec.z, 0.0, -0.0784, 0.0, true, 0.15), false, false);
               this.visibleActive = true;
               this.streakCount++;
            }
         } else {
            long visibleLimit = this.groundValue == Long.MIN_VALUE ? Long.MAX_VALUE : time - this.groundValue;
            int visibleValue = (int)Math.max(1L, Math.min(4L, time - this.lastUpdateTick));
            if (LivingEntityHelperVisible.Jump.canFlag(distance, visibleValue, this.getMax(time))) {
               this.visibleValue = time;
               this.visibleKickList.clear();
               boolean visibleFlag3 = !visibleFlag;
               this.handleTimeGround(time, new PosVecUtils.Sample(vec.x, vec.y, vec.z, 0.0, visibleFlag3 ? 0.0 : -0.0784, 0.0, !visibleFlag3, 0.3), false, true);
               this.visibleActive = !visibleFlag3;
               this.streakCount = visibleFlag3 ? 0 : 1;
            } else if (visibleLimit >= 1L && visibleLimit <= 4L) {
               int exponent = (int)visibleLimit;
               boolean visibleFlag4 = !visibleFlag || Math.abs(deltaY) > 0.001;
               double visibleValue2;
               double visibleValue3;
               double groundY;
               if (visibleFlag4) {
                  double pow = Math.pow(0.91, exponent);
                  double visibleFactor = 0.08999999999999997 / (1.0 - pow) * pow;
                  visibleValue2 = deltaX * visibleFactor;
                  visibleValue3 = deltaZ * visibleFactor;
                  groundY = PosVecUtils.getAmount(PosVecUtils.getAmountForDoubleInt(deltaY, exponent), exponent);
                  if (!Double.isFinite(groundY)) {
                     groundY = 0.0;
                  }
               } else {
                  visibleValue2 = deltaX / exponent;
                  visibleValue3 = deltaZ / exponent;
                  groundY = -0.0784;
               }

               boolean onGround5 = !this.visibleSample.onGround();
               if (visibleFlag4 && onGround5 && this.groundActive) {
                  this.handleVisibleMax(this.visibleSample, deltaX, deltaZ, exponent, time);
               }

               double visibleValue5 = 0.15;
               LivingEntityHelperVisible.Kick kick = null;

               for (LivingEntityHelperVisible.Kick kick2 : this.visibleKickList) {
                  if (kick2.visibleAt() <= time && kick2.visibleAt() > this.lastUpdateTick && kick2.set() && (kick == null || kick2.visibleAt() > kick.visibleAt())
                     )
                   {
                     kick = kick2;
                  }
               }

               if (kick != null && visibleFlag4) {
                  int i = Math.max(0, (int)Math.round(time - kick.visibleAt()));
                  double pow2 = Math.pow(0.91, i);
                  visibleValue2 = kick.vx() * pow2;
                  visibleValue3 = kick.vz() * pow2;
                  groundY = PosVecUtils.getAmount(kick.vy(), i);
                  visibleValue5 = kick.packet() ? 0.05 : 0.15;
               }

               this.visibleKickList.removeIf(tickx -> tickx.visibleAt() <= time);
               this.handleTimeGround(time, new PosVecUtils.Sample(vec.x, vec.y, vec.z, visibleValue2, groundY, visibleValue3, !visibleFlag4, visibleValue5), true, true);
               this.visibleActive = !visibleFlag4;
               this.streakCount = visibleFlag4 ? 0 : this.streakCount + 1;
            } else {
               boolean visibleFlag6 = !visibleFlag;
               this.handleTimeGround(time, new PosVecUtils.Sample(vec.x, vec.y, vec.z, 0.0, visibleFlag6 ? 0.0 : -0.0784, 0.0, !visibleFlag6, 0.15), false, true);
               this.visibleActive = !visibleFlag6;
               this.streakCount = visibleFlag6 ? 0 : this.streakCount + 1;
            }
         }
      }
   }

   public boolean isSampleGround() {
      return this.groundMax != null && !this.groundMax.sample().onGround();
   }

   private double getMax(long livingEntityLimit) {
      double livingEntityBound = this.visibleSample == null
         ? 0.0
         : Math.sqrt(this.visibleSample.vx() * this.visibleSample.vx() + this.visibleSample.vy() * this.visibleSample.vy() + this.visibleSample.vz() * this.visibleSample.vz());
      boolean livingEntityFlag = !Double.isNaN(this.maxVisibleAt) && livingEntityLimit >= this.maxVisibleAt - 8.0 && livingEntityLimit <= this.maxVisibleAt + 8.0;
      return livingEntityFlag ? Math.max(livingEntityBound, this.visibleAmount) : livingEntityBound;
   }

   public LivingEntityHelperVisible() {
      this.sentBlastExpectList = new ArrayList<>(4);
      this.handler = PosVecUtils.NO_TERRAIN;
      this.trackedEntityId = -1;
      this.lastUpdateTick = Long.MIN_VALUE;
      this.groundValue = Long.MIN_VALUE;
      this.weakFullType = WeakFullUtils.Lift.FULL;
      this.attributesEquipmentValue = Long.MIN_VALUE;
      this.interpolatorPingTargetData = new PingTargetData(100.0, 100.0);
      this.interpolatorValue = Long.MIN_VALUE;
      this.visibleValue = Long.MIN_VALUE;
      this.fullValue = Long.MIN_VALUE;
      this.entityInstance = new Vec3d(0.0, 0.0, 0.0);
      this.maxVisibleAt = Double.NaN;
   }

   public int getId() {
      return this.trackedEntityId;
   }

   public PingTargetData getInterpolatorPingTargetData() {
      return this.interpolatorPingTargetData;
   }

   public PosVecUtils.Path getSampleTick(int sampleTickBound, PosVecUtils.Terrain terrain, AmountAmounts amountAmounts, Vec3d vec, List<PosVecUtils.Kick> list) {
      LivingEntityHelperVisible.Seed groundMax57 = this.groundMax;
      if (groundMax57 == null) {
         return null;
      } else {
         PosVecUtils.Terrain terrain3 = terrain == null ? this.handler : terrain;
         double sampleTickFactor = amountAmounts == null ? 0.0 : amountAmounts.getSpeedOffset();
         Vec3d vec3d = getVec3d(vec);
         boolean sampleTickFlag = !groundMax57.sample().onGround();
         ArrayList arrayList = new ArrayList(this.visibleKickList.size() + (list == null ? 0 : list.size()));
         double sampleTickValue = groundMax57.sampleTick() + this.getValue(groundMax57.sampleTick(), this.interpolatorValue);

         for (LivingEntityHelperVisible.Kick kick : this.visibleKickList) {
            int sampleTickLimit = (int)Math.round(kick.visibleAt() - sampleTickValue);
            if (sampleTickLimit >= 1) {
               arrayList.add(new PosVecUtils.Kick(sampleTickLimit, kick.vx(), kick.vy(), kick.vz(), kick.set()));
            }
         }

         if (list != null) {
            for (PosVecUtils.Kick totalPriceData : list) {
               if (totalPriceData != null) {
                  arrayList.add(totalPriceData);
               }
            }
         }

         return PosVecUtils.getGround(groundMax57.sample(), Math.max(1, sampleTickBound), terrain3, sampleTickFactor * vec3d.x, sampleTickFactor * vec3d.z, true, sampleTickFlag ? 0.026 : 0.05, arrayList);
      }
   }

   public void handleTimeVelocity(VelocityCpacket.Velocity velocity, long time) {
      if (velocity != null && velocity.entityId() == this.trackedEntityId && velocity.velocity() != null) {
         Vec3d velocity2 = velocity.velocity();
         this.fullValue = time;
         this.y = velocity2.y;
         if (this.velocityVelocitySeenList.size() >= 16) {
            this.velocityVelocitySeenList.remove(0);
         }

         this.velocityVelocitySeenList.add(new LivingEntityHelperVisible.VelocitySeen(time, velocity2.y));
         double velocityValue = time + this.interpolatorPingTargetData.getMax();
         this.visibleKickList.removeIf(visible -> visible.visibleAt() <= velocityValue + 0.75);
         this.handleMaxVisible(new LivingEntityHelperVisible.Kick(velocityValue, velocity2.x, velocity2.y, velocity2.z, true, true));
         this.handleSentMax(time, velocity2.y);
         if (this.interpolatorValue != Long.MIN_VALUE) {
            this.groundMax = this.getGroundMax(this.interpolatorValue);
         }
      }
   }

   public void handle(long livingEntityValue, double groundY, double livingEntityValue3) {
      if (Double.isFinite(groundY) && Double.isFinite(livingEntityValue3)) {
         if (this.sentBlastExpectList.size() >= 4) {
            this.sentBlastExpectList.remove(0);
         }

         this.sentBlastExpectList.add(new LivingEntityHelperVisible.BlastExpect(livingEntityValue, groundY, livingEntityValue3));
      }
   }

   public double getY() {
      return this.y;
   }

   public void handleHandler(PosVecUtils.Terrain terrain) {
      this.handler = terrain == null ? PosVecUtils.NO_TERRAIN : terrain;
   }

   public LivingEntityHelperVisible.Seed getDataGroundMax() {
      return this.groundMax;
   }

   public boolean isFlag() {
      return this.visibleActive;
   }

   public int getValue(PosVecUtils.Terrain terrain, AmountAmounts amountAmounts, Vec3d vec) {
      if (!this.isSampleGround()) {
         return 0;
      } else {
         PosVecUtils.Path sampleTick = this.getSampleTick(40, terrain, amountAmounts, vec, null);
         return sampleTick == null ? 0 : sampleTick.getValueGround();
      }
   }

   public long getVisibleValue() {
      return this.visibleValue;
   }

   public Vec3d getVec3dGroundHistList() {
      if (this.groundHistList.isEmpty()) {
         return null;
      } else {
         LivingEntityHelperVisible.Hist hist = this.groundHistList.get(this.groundHistList.size() - 1);
         return new Vec3d(hist.x(), hist.y(), hist.z());
      }
   }

   public void handleInterpolatorTime(long time, LivingEntity entity, int interpolatorLimit, int interpolatorLimit2, AmountAmounts max) {
      if (entity != null) {
         if (entity.getId() != this.trackedEntityId) {
            this.handleHandler(entity.getId());
         }

         this.groundAmountAmounts = max;
         this.interpolatorValue = time;
         this.interpolatorPingTargetData = new PingTargetData(interpolatorLimit2 > 0 ? interpolatorLimit2 : 100.0, interpolatorLimit > 0 ? interpolatorLimit : 100.0);
         if (this.attributesEquipmentValue == Long.MIN_VALUE || time - this.attributesEquipmentValue >= 10L || time < this.attributesEquipmentValue) {
            this.handleAttributesEquipment(entity, time);
         }

         Vec3d vec3d = entity.getInterpolator() != null ? entity.getInterpolator().getLerpedPos() : entity.getEntityPos();
         this.entityInstance = getEntityInstance(vec3d);
         if (vec3d != null && Double.isFinite(vec3d.x) && Double.isFinite(vec3d.y) && Double.isFinite(vec3d.z)) {
            this.handleVisibleTime(time, vec3d, entity.isOnGround());
            this.visibleKickList.removeIf(tickx -> tickx.visibleAt() < time - 3L);
         }

         this.groundMax = this.getGroundMax(time);
      }
   }

   public void handleTime(long livingEntityValue, double livingEntityValue2, double groundY, double livingEntityValue4, boolean livingEntityFlag) {
      if (Double.isFinite(livingEntityValue2) && Double.isFinite(groundY) && Double.isFinite(livingEntityValue4)) {
         double livingEntityValue5 = livingEntityValue + this.interpolatorPingTargetData.getMax() + this.interpolatorPingTargetData.getPingMs() + 0.5;
         this.handleMaxVisible(new LivingEntityHelperVisible.Kick(livingEntityValue5, livingEntityValue2, groundY, livingEntityValue4, livingEntityFlag, false));
         if (this.interpolatorValue != Long.MIN_VALUE) {
            this.groundMax = this.getGroundMax(this.interpolatorValue);
         }
      }
   }

   private void handleMaxVisible(LivingEntityHelperVisible.Kick kick) {
      if (this.visibleKickList.size() >= 8) {
         this.visibleKickList.remove(0);
      }

      this.visibleKickList.add(kick);
      double max = Math.sqrt(kick.vx() * kick.vx() + kick.vy() * kick.vy() + kick.vz() * kick.vz());
      if (!Double.isNaN(this.maxVisibleAt) && Math.abs(kick.visibleAt() - this.maxVisibleAt) <= 8.0) {
         this.visibleAmount = Math.max(this.visibleAmount, max);
         this.maxVisibleAt = Math.max(this.maxVisibleAt, kick.visibleAt());
      } else {
         this.visibleAmount = max;
         this.maxVisibleAt = kick.visibleAt();
      }
   }

   private void handleVisibleMax(PosVecUtils.Sample sample, double visibleValue, double deltaZ, int exponent, long visibleLimit) {
      if (this.groundAmountAmounts != null) {
         for (LivingEntityHelperVisible.Kick kick : this.visibleKickList) {
            if (kick.visibleAt() <= visibleLimit && kick.visibleAt() > this.lastUpdateTick) {
               return;
            }
         }

         for (LivingEntityHelperVisible.VelocitySeen velocitySeen : this.velocityVelocitySeenList) {
            if (velocitySeen.tick() > this.lastUpdateTick - 1L && velocitySeen.tick() <= visibleLimit) {
               return;
            }
         }

         double pow = Math.pow(0.91, exponent);
         double visibleFactor = (1.0 - pow) / 0.08999999999999997;
         double visibleValue3 = sample.vx() * visibleFactor;
         double visibleValue4 = sample.vz() * visibleFactor;
         double visibleFactor2 = visibleValue - visibleValue3;
         double visibleFactor3 = deltaZ - visibleValue4;
         double visibleLimit2 = (exponent - 0.91 * (1.0 - pow) / 0.08999999999999997) / 0.08999999999999997;
         if (visibleLimit2 > 1.0E-6) {
            Vec3d vec3d = this.entityInstance;
            double distance = Math.sqrt(vec3d.x * vec3d.x + vec3d.z * vec3d.z);
            if (!(distance < 1.0E-6)) {
               double groundY = (visibleFactor2 * vec3d.x + visibleFactor3 * vec3d.z) / distance / visibleLimit2;
               if (Double.isFinite(groundY)) {
                  this.groundAmountAmounts.handleAmount(groundY);
               }
            }
         }
      }
   }

   public long getFullValue() {
      return this.fullValue;
   }

   public boolean canFlag(long livingEntityValue, double livingEntityValue2) {
      return this.canTick(livingEntityValue, livingEntityValue2) || this.getTick(livingEntityValue) >= 0.3;
   }

   private record BlastExpect(long sentTick, double kbY, double vyBefore) {
   }

   private record Hist(long tick, double x, double y, double z, boolean airborne) {
   }

   static final class Jump {
      static boolean canFlag(double jumpLimit, int jumpBound, double groundY) {
         int maxMoved = Math.max(1, jumpBound);
         double jumpFactor = Double.isFinite(groundY) ? Math.max(0.0, groundY) : 0.0;
         return jumpLimit > 2.0 + jumpFactor * maxMoved + 0.04 * maxMoved * maxMoved;
      }

      private Jump() {
      }
   }

   private record Kick(double visibleAt, double vx, double vy, double vz, boolean set, boolean packet) {
   }

   public record Seed(
      PosVecUtils.Sample sample,
      boolean grounded,
      int groundedUpdates,
      double rMelee,
      double rExpl,
      WeakFullUtils.Lift lift,
      PingTargetData time,
      long sampleTick
   ) {
   }

   private record VelocitySeen(long tick, double vy) {
   }
}
