package com.corz.client.inventory;

import com.corz.client.entity.ClientThreadUtils;
import com.corz.client.entity.EntityCenterUtils;
import com.corz.client.entity.FloorMathUtilsBlock;
import com.corz.client.entity.PosBlockUtilsSelf;
import com.corz.client.entity.VecBlockingData;
import com.corz.client.world.AbsorptionPosData;
import com.corz.client.world.BlockStateUtilsDirections;
import com.corz.client.world.BoxLengthUtils;
import com.corz.client.world.CenterVecData;
import com.corz.client.world.ClientInstance;
import com.corz.client.world.MathClampUtils;
import com.corz.client.world.PosBlockUtilsBonus;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.HumanAimRotator;
import com.corz.client.utils.HotbarSlotSwitcher;
import com.corz.client.utils.HotbarSlotRules;
import com.corz.client.categories.combat.AutoInventoryTotem;
import com.corz.client.utils.BlockHitResolver;
import com.corz.client.utils.ConnectionTimeUtils;
import com.corz.client.utils.EntityDamageUtils;
import com.corz.client.utils.BonusLevelData;
import com.corz.client.utils.BowGappleType;
import com.corz.client.utils.CarefulBalancedType;
import com.corz.client.utils.CaseMaxData;
import com.corz.client.utils.ComboTradeData;
import com.corz.client.utils.IframeComboUtils;
import com.corz.client.utils.RefBareData;
import com.corz.client.utils.SelfPopData;
import com.corz.client.utils.SurroundKitType;
import com.corz.client.utils.UnplacedAntiUtils;

public final class StaleOwed {
   public static final String FIRED = "fired";
   private BlockPos blockPos;
   private int idleOwedValue;
   private SelfPopData hitStartSelfPopData;
   public static final String DIG = "dig";
   private float owedAmount;
   private long lastPopTick;
   private long idleTime;
   private final HotbarSlotSwitcher sneaking;
   private boolean vetoedDoneFlag;
   public static final String STALE_REACH = "stale reach";
   private float fireFullAmount;
   private BlockPos longValue;
   private final Map<Long, Long> forcedRefusedMap = new HashMap<>();
   private float costOwedPlan;
   private PosBlockUtilsSelf.Plan posBlockData;
   private final Map<Long, StaleOwed.Strike> forcedRefusedStrikeMap = new HashMap<>();
   private Predicate<Vec3d> predicate;
   public static final String STALE_HIT = "stale hit";
   private StaleOwed.Fire firedFire;
   private long idleTimeVecPos;
   public static final String HIT = "hit";
   private double owedAmountFired;
   private final Map<Long, StaleOwed.Fire> tickFireMap;
   private long staleNetherValue;
   private final UnsafeCharge nether;
   public static final String UNPLACED = "unplaced";
   public static final String ARMED = "armed";
   private Vec3d vecPosVec3d;
   private long startIdleTime;
   private boolean killGoneFlag;
   public static final String PLACED = "placed";
   public static final String STALE_FAR = "stale far";
   private String hit;
   public static final String WEAK = "weak";
   private long entityIdleTime;
   private ComboTradeData data2Idle;
   private BlockPos blockPosIdle;
   private final ClientInstance clientInstance;
   private int idleValue;
   private BlockPos blockPosForcedPos;
   private boolean owedPlanFlag;
   public static final String OWED = "owed";
   private long netherDoneValue;
   private boolean kills;
   private long tickIdle;
   public static final String STALE_AWAY = "stale away";
   public static final String INSTANT = "instant";
   private boolean netherDoneActive;
   private long idleWeakTime;
   private final Map<Long, StaleOwed.Owed> doneFiredMap = new HashMap<>();
   private static final String TIMEOUT = "timeout";
   private static final Direction[] posDirections = Direction.values();

   public boolean canFiredDone(BlockPos pos) {
      return pos != null && this.doneFiredMap.containsKey(pos.asLong());
   }

   private static float getCalibration(VecBlockingData vecBlockingData) {
      if (vecBlockingData == null) {
         return Float.POSITIVE_INFINITY;
      } else {
         float calibrationDivisor = vecBlockingData.calibration() > 0.0F && Float.isFinite(vecBlockingData.calibration()) ? vecBlockingData.calibration() : 1.0F;
         return vecBlockingData.getHealthTrusted() / calibrationDivisor;
      }
   }

   private static float getSelfInstance(SelfPopData selfPopData, BlockPos pos, int selfValue) {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (player != null && selfPopData.self() != null) {
         float boxBounding = EntityCenterUtils.getBoxBounding(pos, null, null);
         return PosBlockUtilsBonus.getScaledInstance(
            boxBounding, BlockEntityUtils.getAttributeArmor(player), new PosBlockUtilsBonus.Window(selfPopData.selfClosedFor(), selfPopData.self().lastScaledTaken()), selfValue
         );
      } else {
         return Float.NaN;
      }
   }

   static String formatStaleAway(boolean staleAwayFlag, boolean staleNetherFlag, boolean staleAwayFlag3, boolean staleAwayFlag4, boolean staleAwayFlag5) {
      if (!staleAwayFlag) {
         return "stale far";
      } else if (staleNetherFlag) {
         return "stale reach";
      } else if (staleAwayFlag3 && !staleAwayFlag4) {
         return "stale hit";
      } else {
         return staleAwayFlag5 ? "stale away" : null;
      }
   }

   private boolean canFreshTaken(SelfPopData selfPopData) {
      AbsorptionPosData absorptionPosData = selfPopData == null ? null : selfPopData.self();
      VecBlockingData vecBlockingData = selfPopData == null ? null : selfPopData.target();
      return absorptionPosData != null && vecBlockingData != null && !canTerminator(selfPopData) ? canFlag(absorptionPosData.ticksSinceFresh(), absorptionPosData.lastScaledTaken(), vecBlockingData.distance()) : false;
   }

   public boolean canFiredDone(SelfPopData selfPopData) {
      return !this.doneFiredMap.isEmpty() && this.getFiredInstance(selfPopData) != null;
   }

   public void updateNetherGone() {
      if (this.nether.isCoverArmedType()) {
         if (this.nether.getImmutable() == null
            || !this.canFiredDone(this.nether.getImmutable())
            || BlockPosUtilsRespawn.getChargesRespawn(this.nether.getImmutable()) < 0) {
            this.handleForcedRefused(this.nether.getImmutable());
         }

         this.posBlockData = null;
         this.vetoedDoneFlag = false;
      }

      this.nether.updateUnsafeCharge();
      this.killGoneFlag = false;
      this.netherDoneActive = false;
      this.startIdleTime = Long.MIN_VALUE;
   }

   static boolean canFlag(int staleOwedLimit) {
      return staleOwedLimit >= 2;
   }

   static float getMax(float staleOwedValue) {
      return Math.max(2.0F, Math.min(4.0F, 0.5F * staleOwedValue));
   }

   private boolean canNetherClient(BlockPos pos) {
      PosBlockUtilsSelf.Plan unsafe = this.nether.getUnsafe();
      if (unsafe != null && unsafe.kills()) {
         SelfPopData selfPopData = this.hitStartSelfPopData;
         if (selfPopData != null && pos != null && selfPopData.tick() == this.clientInstance.getTime() && selfPopData.target() != null) {
            PosBlockUtilsSelf.Plan posBlockData = this.getPosBlockData(selfPopData, pos, false);
            if (posBlockData == null) {
               return false;
            } else {
               if (this.nether.canUnsafeArmed(posBlockData)) {
                  this.posBlockData = posBlockData;
               }

               return true;
            }
         } else {
            return true;
         }
      } else {
         return true;
      }
   }

   static boolean canFlag(long staleOwedLimit, long staleOwedValue) {
      return staleOwedLimit == Long.MIN_VALUE || staleOwedValue < staleOwedLimit || staleOwedValue - staleOwedLimit >= 2L;
   }

   static boolean canCfgPlaystyle(SelfPopData selfPopData) {
      CarefulBalancedType carefulBalancedType = selfPopData != null && selfPopData.cfg() != null && selfPopData.cfg().playstyle() != null ? selfPopData.cfg().playstyle() : CarefulBalancedType.TERMINATOR;
      return carefulBalancedType != CarefulBalancedType.CAREFUL;
   }

   static int getValue(int staleOwedBound, int rocketsBuyBound) {
      return Math.max(1, Math.min(8, Math.max(staleOwedBound, rocketsBuyBound)));
   }

   static boolean canSightLine(String sightLineText) {
      return sightLineText == null || !sightLineText.endsWith("no line of sight") && !"unsafe".equals(sightLineText);
   }

   static int getValue(int[] staleOwedValues, int staleOwedValue, boolean[] staleOwedFlags, boolean[] selectedShieldFlags) {
      if (staleOwedValues == null) {
         return 0;
      } else {
         int staleOwedTotal = 0;

         for (int i = 0; i < staleOwedValues.length && i < 9; i++) {
            if (staleOwedValues[i] > 0 && !HotbarSlotRules.isSlotUnavailable(i, staleOwedValue, staleOwedFlags, selectedShieldFlags, false)) {
               staleOwedTotal += staleOwedValues[i];
            }
         }

         return staleOwedTotal;
      }
   }

   static boolean canFlag(boolean staleOwedFlag, int staleOwedValue, int selectedSlot, int slotMatching, int staleOwedLimit) {
      if (staleOwedFlag && selectedSlot < 0) {
         return false;
      } else {
         return (staleOwedFlag || staleOwedValue <= 0) && slotMatching < 0 ? false : staleOwedLimit >= 0;
      }
   }

   private PosBlockUtilsSelf.Plan getPosBlockData(SelfPopData selfPopData, BlockPos pos, boolean posBlockFlag) {
      return this.getSelfCalibration(selfPopData, pos, posBlockFlag, null);
   }

   static boolean canFlag(float staleOwedLimit, float staleOwedValue, float staleOwedValue2) {
      return staleOwedLimit >= staleOwedValue2 || staleOwedLimit >= getMax(staleOwedValue);
   }

   private boolean canStaleNether(SelfPopData selfPopData) {
      if (this.nether.isCoverArmedType() && selfPopData != null && selfPopData.self() != null && selfPopData.target() != null && selfPopData.target().entity() != null) {
         this.staleNetherValue = this.clientInstance.getTime();
         BlockPos immutable = this.nether.getImmutable();
         if (immutable == null) {
            return false;
         } else {
            VecBlockingData target = selfPopData.target();
            AbsorptionPosData self = selfPopData.self();
            Vec3d pos = self.pos();
            Vec3d vec3d = self.eye() != null ? self.eye() : pos;
            boolean staleNetherFlag = pos != null
               && vec3d != null
               && canFlag(BlockEntityUtils.getMinMax(vec3d, BlockEntityUtils.getBoxForBlockPos(immutable)), BlockPosUtilsRespawn.getRangeInteraction());
            if (this.canFiredDone(immutable) && BlockPosUtilsRespawn.getChargesRespawn(immutable) >= 0) {
               if (!staleNetherFlag && selfPopData.getSelfPop().on()) {
                  this.canClientTaken(
                     selfPopData, target, immutable, getValue(getValue(this.nether.getCoverArmedType(), BlockPosUtilsRespawn.getChargesRespawn(immutable)), selfPopData.targetReadyIn())
                  );
               }

               if (!staleNetherFlag) {
                  return false;
               } else {
                  this.handleGoneKill(immutable, "stale reach");
                  return true;
               }
            } else if (!selfPopData.getSelfPop().on()) {
               int chargesRespawn = BlockPosUtilsRespawn.getChargesRespawn(immutable);
               PosBlockUtilsSelf.Plan unsafe = this.nether.getUnsafe();
               float staleNetherValue = unsafe == null ? 0.0F : unsafe.targetDamage();
               float armorEntity = this.getArmorEntity(target, immutable, getValue(getValue(this.nether.getCoverArmedType(), chargesRespawn), selfPopData.targetReadyIn()));
               boolean staleNetherFlag2 = this.canFreshTaken(selfPopData) && !canFlagForLong(this.startIdleTime, this.clientInstance.getTime()) && !this.netherDoneActive;
               boolean staleNetherFlag3 = canFlagForInt(chargesRespawn, selfPopData.targetReadyIn()) || chargesRespawn >= 1 && this.canEntityArmor(selfPopData, immutable);
               Vec3d vec3d2 = self.velocity() == null ? Vec3d.ZERO : self.velocity();
               Vec3d vec3d3 = target.pos() != null ? target.pos() : target.entity().getEntityPos();
               boolean staleNetherFlag4 = pos != null
                  && vec3d3 != null
                  && canFlag(pos.x, pos.z, vec3d2.x, vec3d2.z, vec3d3.x, vec3d3.z, immutable.getX() + 0.5, immutable.getZ() + 0.5);
               String formatStaleAway2 = formatStaleAway(canFlag(armorEntity, staleNetherValue, getCalibration(target)), staleNetherFlag, staleNetherFlag2, staleNetherFlag3, staleNetherFlag4);
               if (formatStaleAway2 == null) {
                  return false;
               } else {
                  this.handleGoneKill(immutable, formatStaleAway2, canStaleAway(formatStaleAway2));
                  return true;
               }
            } else if (staleNetherFlag) {
               this.handleGoneKill(immutable, "stale reach", false);
               return true;
            } else if (this.canClientTaken(
               selfPopData, target, immutable, getValue(getValue(this.nether.getCoverArmedType(), BlockPosUtilsRespawn.getChargesRespawn(immutable)), selfPopData.targetReadyIn())
            )) {
               return false;
            } else {
               this.handleGoneKill(immutable, "stale far", true);
               return true;
            }
         }
      } else {
         return false;
      }
   }

   static boolean canPlace(UnsafeCharge.Phase phase, int staleOwedValue) {
      return phase == UnsafeCharge.Phase.PLACE && staleOwedValue < 0;
   }

   static boolean canFlag(float staleOwedLimit, float routedPartialValue) {
      return Float.isFinite(staleOwedLimit) && !(staleOwedLimit <= 0.0F) ? staleOwedLimit >= 4.0F || Float.isFinite(routedPartialValue) && routedPartialValue > 0.0F && staleOwedLimit >= routedPartialValue : false;
   }

   private void handleFiredDone(BlockPos pos, long firedDoneValue) {
      long long2 = pos.asLong();
      StaleOwed.Owed donefiredmap = this.doneFiredMap.get(long2);
      if (donefiredmap != null) {
         int failsOffset = donefiredmap.fails() + 1;
         if (failsOffset >= 3) {
            this.doneFiredMap.remove(long2);
            this.forcedRefusedMap.put(long2, ConnectionTimeUtils.getTimeTimeMillis() + 3000L);
         } else {
            this.doneFiredMap.put(long2, new StaleOwed.Owed(donefiredmap.since(), firedDoneValue + 2L, failsOffset));
         }
      }
   }

   private Predicate<Vec3d> getPredicate(Predicate<Vec3d> predicate, ClientWorld world) {
      return anchorsOnlyx -> {
         if (anchorsOnlyx == null) {
            return true;
         } else {
            BlockPos floored = BlockPos.ofFloored(anchorsOnlyx);
            if (this.canForcedPos(floored)) {
               return true;
            } else {
               return world != null && !world.getBlockState(floored).isOf(Blocks.RESPAWN_ANCHOR) ? true : predicate != null && predicate.test(anchorsOnlyx);
            }
         }
      };
   }

   private void handleFiredDoneForBlockPosLong(BlockPos pos, long firedDoneValue) {
      if (pos != null) {
         this.doneFiredMap.putIfAbsent(pos.asLong(), new StaleOwed.Owed(firedDoneValue, Long.MIN_VALUE, 0));
         if (this.doneFiredMap.size() > 4) {
            long immutablePosValue = 0L;
            long firedDoneLimit = Long.MAX_VALUE;

            for (Entry entry : this.doneFiredMap.entrySet()) {
               if (((StaleOwed.Owed)entry.getValue()).since() < firedDoneLimit) {
                  firedDoneLimit = ((StaleOwed.Owed)entry.getValue()).since();
                  immutablePosValue = (Long)entry.getKey();
               }
            }

            this.doneFiredMap.remove(immutablePosValue);
         }
      }
   }

   private static boolean canEyeEntity(Box box, Vec3d vec, List<VecBlockingData> list) {
      if (vec != null && BlockEntityUtils.getMinMax(vec, box) <= 4.5) {
         return true;
      } else if (list == null) {
         return false;
      } else {
         for (VecBlockingData vecBlockingData : list) {
            Vec3d vec3d = vecBlockingData == null ? null : (vecBlockingData.eye() != null ? vecBlockingData.eye() : (vecBlockingData.entity() != null ? vecBlockingData.entity().getEyePos() : null));
            if (vec3d != null && BlockEntityUtils.getMinMax(vec3d, box) <= 4.5) {
               return true;
            }
         }

         return false;
      }
   }

   public String getReplace() {
      return this.hit != null && !this.hit.isBlank() ? this.hit.trim().replace(' ', '_') : "-";
   }

   static boolean canFlag(RefBareData refBareData, float staleOwedLimit, float staleOwedValue, float routedPartialValue) {
      return refBareData != null && refBareData.canFlag(staleOwedLimit, staleOwedValue, Float.isFinite(routedPartialValue) && routedPartialValue > 0.0F && staleOwedLimit >= routedPartialValue);
   }

   private boolean canClicksTarget(SelfPopData selfPopData, PosBlockUtilsSelf.Plan plan) {
      VecBlockingData target = selfPopData.target();
      if (target != null && target.entity() != null && plan != null && plan.anchor() != null && plan.steady()) {
         boolean clicksTargetFlag = canFlag(target.onGround(), target.velocity() == null ? 0.0 : target.velocity().y);
         int clicksTargetValue = IframeComboUtils.getValue(plan.turn());
         if (!canFlag(plan.clicks(), selfPopData.targetReadyIn(), clicksTargetValue, clicksTargetFlag)) {
            return false;
         } else {
            int valueForInt = getValueForInt(plan.clicks(), clicksTargetValue);
            float minTargetDamage = Math.min(plan.targetDamage(), this.getArmorEntity(target, plan.anchor(), getValue(valueForInt, selfPopData.targetReadyIn())));
            float staleOwedBound = this.data2Idle == null ? 3.0F : this.data2Idle.minDamage();
            return canFlag(minTargetDamage, getCalibration(target), staleOwedBound, false);
         }
      } else {
         return false;
      }
   }

   public BlockPos getNether() {
      return this.isGoneKill() ? this.nether.getImmutable() : null;
   }

   static boolean canFlag(RefBareData refBareData, float staleOwedValue, float full, float staleOwedValue3, float staleOwedValue4, boolean staleOwedFlag, boolean staleOwedFlag2, boolean staleOwedFlag3, boolean staleOwedFlag4) {
      if (refBareData == null || !refBareData.canBare(refBareData.getBareRef(staleOwedValue, full), staleOwedValue3, refBareData.canBareBehind(staleOwedFlag2, staleOwedFlag))) {
         return false;
      } else if (staleOwedFlag) {
         return true;
      } else {
         return staleOwedFlag3 && refBareData.canBareRef(staleOwedValue, full) ? true : staleOwedFlag4 && refBareData.canRef(staleOwedValue4, false, staleOwedValue, full, false);
      }
   }

   public Vec3d getVecPos(SelfPopData selfPopData, ComboTradeData comboTradeData, CenterVecData centerVecData) {
      if (!this.nether.isCoverArmedType() && selfPopData != null && comboTradeData != null && selfPopData.target() != null) {
         PosBlockUtilsSelf.Plan posBlockData = this.posBlockData;
         if (posBlockData == null || posBlockData.anchor() == null || this.formatGlowstoneSneaking(selfPopData) != null) {
            return null;
         } else if (!canAnchorAvailable(selfPopData, comboTradeData, centerVecData)) {
            return null;
         } else {
            BlockPos blockPos = posBlockData.anchor();
            long idleTime = this.clientInstance.getTime();
            if (this.idleTimeVecPos == idleTime && blockPos.equals(this.blockPos)) {
               return this.vecPosVec3d;
            } else {
               this.idleTimeVecPos = idleTime;
               this.blockPos = blockPos;
               this.vecPosVec3d = this.getPos(posBlockData);
               return this.vecPosVec3d;
            }
         }
      } else {
         return null;
      }
   }

   static Vec3d getPosFloored(VecBlockingData vecBlockingData, AbsorptionPosData absorptionPosData, ClientWorld world) {
      if (vecBlockingData != null && absorptionPosData != null && world != null && absorptionPosData.pos() != null && vecBlockingData.onGround()) {
         BowGappleType main = vecBlockingData.main();
         if (main != BowGappleType.CRYSTAL && main != BowGappleType.OBSIDIAN && main != BowGappleType.ANCHOR && main != BowGappleType.GLOWSTONE) {
            return null;
         } else {
            Vec3d vec3d = vecBlockingData.serverPos() != null ? vecBlockingData.serverPos() : vecBlockingData.pos();
            if (vec3d == null) {
               return null;
            } else if (!IframeComboUtils.canFlag(BlockStateUtilsDirections.getOffsetDirections(world, BlockPos.ofFloored(vec3d.x, vec3d.y + 0.001, vec3d.z)), true)) {
               return null;
            } else {
               double deltaX = absorptionPosData.pos().x - vec3d.x;
               double deltaZ = absorptionPosData.pos().z - vec3d.z;
               double hypot = Math.hypot(deltaX, deltaZ);
               return hypot > 1.0E-6 && !(hypot > 6.0) ? new Vec3d(deltaX / hypot, 0.0, deltaZ / hypot) : null;
            }
         }
      } else {
         return null;
      }
   }

   static int getValue(UnsafeCharge.Phase phase, int staleOwedLimit) {
      if (phase == null) {
         return 0;
      } else {
         return switch (phase) {
            case PLACE -> staleOwedLimit < 0 ? 3 : 2;
            case COVER, CHARGE -> staleOwedLimit >= 1 ? 1 : 2;
            case ARMED, DETONATE -> 1;
            default -> 0;
         };
      }
   }

   public PosBlockUtilsSelf.Plan getPosBlockData() {
      return this.posBlockData;
   }

   public boolean canHitStart(SelfPopData data) {
      if (!this.nether.isCoverArmedType() && data != null && data.target() != null) {
         this.hitStartSelfPopData = data;
         if (this.formatGlowstoneSneaking(data) != null) {
            return false;
         } else if (this.canOwedVetoed(data)) {
            return true;
         } else {
            this.handleIdleVetoed(data);
            PosBlockUtilsSelf.Plan posBlockData = this.posBlockData;
            if (posBlockData == null) {
               return false;
            } else {
               VecBlockingData target = data.target();
               if (!canTerminator(data) && data.targetReadyIn() > getValue(posBlockData.prepTicks(), canFlag(target.main(), target.off()))) {
                  this.hit = "wait";
                  return false;
               } else {
                  PosBlockUtilsSelf.Plan vetoedWeak = this.getVetoedWeak(data, posBlockData);
                  if (vetoedWeak == null) {
                     this.hit = "no plan";
                     return false;
                  } else {
                     boolean clicksTarget = this.canClicksTarget(data, vetoedWeak);
                     if (this.canFreshTaken(data) && !clicksTarget) {
                        this.hit = "hit";
                        return false;
                     } else {
                        this.killGoneFlag = false;
                        if (this.canStartSneaking(data, vetoedWeak, clicksTarget)) {
                           return true;
                        } else {
                           if (vetoedWeak.anchor() != null) {
                              this.forcedRefusedMap.put(vetoedWeak.anchor().asLong(), ConnectionTimeUtils.getTimeTimeMillis() + 3000L);
                           }

                           this.posBlockData = null;
                           this.hit = "can't start";
                           return false;
                        }
                     }
                  }
               }
            }
         }
      } else {
         return false;
      }
   }

   private void handleGoneKill(BlockPos pos, String hit, boolean goneKillFlag) {
      this.entityIdleTime = ClientThreadUtils.getTime();
      if (goneKillFlag && pos != null) {
         this.forcedRefusedMap.put(pos.asLong(), ConnectionTimeUtils.getTimeTimeMillis() + 3000L);
         this.forcedRefusedStrikeMap.remove(pos.asLong());
      } else {
         this.handleForcedRefused(pos);
      }

      this.nether.updateUnsafeCharge();
      this.posBlockData = null;
      this.vetoedDoneFlag = false;
      this.killGoneFlag = false;
      this.netherDoneActive = false;
      this.startIdleTime = Long.MIN_VALUE;
      this.hit = hit;
   }

   private boolean canPosBlock(BlockPos pos) {
      return this.blockPosForcedPos != null && pos != null && pos.equals(this.blockPosForcedPos) && ConnectionTimeUtils.getTimeTimeMillis() <= this.netherDoneValue;
   }

   private BlockPos getFiredInstance(SelfPopData selfPopData) {
      if (selfPopData == null) {
         return null;
      } else if (this.lastPopTick == selfPopData.tick()) {
         this.handleOwedPlan(this.firedFire);
         return this.longValue;
      } else {
         this.lastPopTick = selfPopData.tick();
         this.longValue = null;
         this.firedFire = StaleOwed.Fire.INSTANCE;
         this.owedAmountFired = 0.0;
         ClientPlayerEntity player = MinecraftClient.getInstance().player;
         if (!this.doneFiredMap.isEmpty() && selfPopData.target() != null && selfPopData.target().entity() != null && player != null && MinecraftClient.getInstance().world != null) {
            long tick = selfPopData.tick();
            this.doneFiredMap
               .entrySet()
               .removeIf(entry -> BlockPosUtilsRespawn.getChargesRespawn(BlockPos.fromLong(entry.getKey())) < 0 || tick - entry.getValue().since() > 200L);
            Vec3d eyePos = player.getEyePos();
            double rangeInteraction = BlockPosUtilsRespawn.getRangeInteraction();
            Vec3d entityPos = selfPopData.target().entity().getEntityPos();
            ArrayList arrayList = new ArrayList(this.doneFiredMap.size());

            for (Entry entryItem : this.doneFiredMap.entrySet()) {
               BlockPos long2 = BlockPos.fromLong((Long)entryItem.getKey());
               StaleOwed.Owed owed = (StaleOwed.Owed)entryItem.getValue();
               if (tick >= owed.retryAt()
                  && !this.canPosBlock(long2)
                  && !(BlockEntityUtils.getMinMax(eyePos, BlockEntityUtils.getBoxForBlockPos(long2)) > rangeInteraction)
                  && (this.predicate == null || !this.predicate.test(Vec3d.ofCenter(long2)))) {
                  arrayList.add(entryItem);
               }
            }

            arrayList.sort(Comparator.comparingDouble(his -> Vec3d.ofCenter(BlockPos.fromLong((Long)((java.util.Map.Entry) his).getKey())).squaredDistanceTo(entityPos)));
            int firedCounter = 0;

            for (Entry entry3 : (Iterable<java.util.Map.Entry>) (arrayList)) {
               BlockPos longValue = BlockPos.fromLong((Long)entry3.getKey());
               StaleOwed.Owed staleOwedOwed = (StaleOwed.Owed)entry3.getValue();
               if (firedCounter >= 2) {
                  break;
               }

               firedCounter++;
               if (this.canTimeTick(selfPopData, longValue, staleOwedOwed.since())) {
                  StaleOwed.Fire data = this.tickFireMap.get(longValue.asLong());
                  float[] wrapDegrees = MathClampUtils.getWrapDegrees(eyePos, Vec3d.ofCenter(longValue));
                  double owedAmount = BoxLengthUtils.getAmount(this.clientInstance.getAmount(), this.clientInstance.getAmountFloat(), wrapDegrees[0], wrapDegrees[1]);
                  if (!canFlag(owedAmount, data.pops())
                     && BlockHitResolver.findNearestRotationHit(longValue, rangeInteraction, this.clientInstance.getAmount(), this.clientInstance.getAmountFloat()) != null
                     && (this.longValue == null || data.dealt() > this.firedFire.dealt())) {
                     this.longValue = longValue;
                     this.firedFire = data;
                     this.owedAmountFired = owedAmount;
                  }
               }
            }

            this.handleOwedPlan(this.firedFire);
            return this.longValue;
         } else {
            return null;
         }
      }
   }

   private boolean canEntityArmor(SelfPopData selfPopData, BlockPos pos) {
      if (selfPopData != null && pos != null && selfPopData.tick() == this.clientInstance.getTime() && selfPopData.targetReadyIn() > 0) {
         VecBlockingData target = selfPopData.target();
         if (target != null && target.entity() != null) {
            float lastScaledTaken = target.lastScaledTaken();
            if (Float.isFinite(lastScaledTaken) && !(lastScaledTaken >= Float.MAX_VALUE)) {
               float boxBounding = EntityCenterUtils.getBoxBounding(target.entity(), null, pos, null);
               PosBlockUtilsBonus.Armor armor = target.armor() != null ? target.armor() : BlockEntityUtils.getAttributeArmor(target.entity());
               float toughnessEpf = PosBlockUtilsBonus.getToughnessEpf(Math.max(0.0F, boxBounding - lastScaledTaken), armor);
               return canFlag(toughnessEpf, getCalibration(target));
            } else {
               return false;
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean isGoneKill() {
      return this.killGoneFlag && this.nether.isCoverArmedType();
   }

   public BlockPos getNetherBlockPos() {
      return this.nether.isCoverArmedType() ? this.nether.getImmutable() : null;
   }

   static long getTime(long staleOwedValue, int staleOwedValue2, int staleOwedBound) {
      return staleOwedValue + getValueForInt(staleOwedValue2, staleOwedBound) + 2L;
   }

   public UnsafeCharge.Phase getNetherPhase() {
      return this.nether.getCoverArmedType();
   }

   static boolean canFlag(int staleOwedValue, int staleOwedValue2) {
      return canFlag(staleOwedValue, staleOwedValue2, 0, false);
   }

   public boolean isNetherCharge() {
      return this.nether.isCoverArmedType()
         && (
            this.nether.getCoverArmedType() == UnsafeCharge.Phase.PLACE
               || this.nether.getCoverArmedType() == UnsafeCharge.Phase.COVER
               || this.nether.getCoverArmedType() == UnsafeCharge.Phase.CHARGE
         );
   }

   private boolean canFlooredCenter(ClientPlayerEntity player, ClientWorld world) {
      Vec3d eyePos = player.getEyePos();
      double rangeInteraction = BlockPosUtilsRespawn.getRangeInteraction();
      int flooredCenterLimit = (int)Math.ceil(rangeInteraction);
      BlockPos floored = BlockPos.ofFloored(eyePos);
      Mutable mutable = new Mutable();

      for (int i = -flooredCenterLimit; i <= flooredCenterLimit; i++) {
         for (int j = -flooredCenterLimit; j <= flooredCenterLimit; j++) {
            for (int k = -flooredCenterLimit; k <= flooredCenterLimit; k++) {
               mutable.set(floored.getX() + i, floored.getY() + j, floored.getZ() + k);
               if (world.getBlockState(mutable).isOf(Blocks.RESPAWN_ANCHOR)
                  && BlockPosUtilsRespawn.getChargesRespawn(mutable) > 0
                  && !(Vec3d.ofCenter(mutable).distanceTo(eyePos) > rangeInteraction + 0.5)
                  && (!this.canForcedPos(mutable) || this.canFiredDone(mutable))) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   public static boolean canInstantOwed(String instantOwedText) {
      return "instant".equals(instantOwedText) || "unplaced".equals(instantOwedText) || "placed".equals(instantOwedText) || "armed".equals(instantOwedText) || "owed".equals(instantOwedText);
   }

   public void updateIdleTime() {
      this.nether.updateUnsafeCharge();
      this.forcedRefusedMap.clear();
      this.forcedRefusedStrikeMap.clear();
      this.doneFiredMap.clear();
      this.tickFireMap.clear();
      this.tickIdle = Long.MIN_VALUE;
      this.lastPopTick = Long.MIN_VALUE;
      this.longValue = null;
      this.firedFire = StaleOwed.Fire.INSTANCE;
      this.owedAmountFired = 0.0;
      this.blockPosForcedPos = null;
      this.netherDoneValue = Long.MIN_VALUE;
      this.idleTimeVecPos = Long.MIN_VALUE;
      this.blockPos = null;
      this.vecPosVec3d = null;
      this.staleNetherValue = Long.MIN_VALUE;
      this.startIdleTime = Long.MIN_VALUE;
      this.posBlockData = null;
      this.vetoedDoneFlag = false;
      this.idleWeakTime = Long.MIN_VALUE;
      this.idleOwedValue = -1;
      this.hit = "idle";
      this.idleTime = Long.MIN_VALUE;
      this.blockPosIdle = null;
      this.entityIdleTime = Long.MIN_VALUE;
      this.killGoneFlag = false;
      this.netherDoneActive = false;
      this.idleValue = -1;
      this.predicate = null;
      this.data2Idle = null;
      this.hitStartSelfPopData = null;
   }

   public boolean isNether() {
      return this.nether.isCoverArmedType();
   }

   static boolean canFlag(boolean staleOwedFlag, int staleOwedValue, int staleOwedValue2, long staleOwedLimit, long staleOwedValue3) {
      return !staleOwedFlag || staleOwedValue != staleOwedValue2 || staleOwedValue3 < staleOwedLimit || staleOwedValue3 - staleOwedLimit >= 4L;
   }

   private static boolean canDamageTarget(VecBlockingData vecBlockingData, PosBlockUtilsSelf.Plan plan) {
      return vecBlockingData != null && plan != null && plan.targetDamage() >= vecBlockingData.getHealthTrusted();
   }

   private boolean canForcedPos(BlockPos pos) {
      if (pos == null) {
         return false;
      } else {
         if (this.blockPosForcedPos != null && ConnectionTimeUtils.getTimeTimeMillis() > this.netherDoneValue) {
            this.blockPosForcedPos = null;
         }

         if (this.blockPosForcedPos != null && pos.equals(this.blockPosForcedPos)) {
            return true;
         } else {
            Long forcedrefusedmap = this.forcedRefusedMap.get(pos.asLong());
            if (forcedrefusedmap == null) {
               return false;
            } else if (ConnectionTimeUtils.getTimeTimeMillis() >= forcedrefusedmap) {
               this.forcedRefusedMap.remove(pos.asLong());
               return false;
            } else {
               return true;
            }
         }
      }
   }

   public StaleOwed(ClientInstance clientInstance, HotbarSlotSwitcher sneaking) {
      this.tickFireMap = new HashMap<>();
      this.tickIdle = Long.MIN_VALUE;
      this.lastPopTick = Long.MIN_VALUE;
      this.firedFire = StaleOwed.Fire.INSTANCE;
      this.netherDoneValue = Long.MIN_VALUE;
      this.idleTimeVecPos = Long.MIN_VALUE;
      this.staleNetherValue = Long.MIN_VALUE;
      this.startIdleTime = Long.MIN_VALUE;
      this.idleWeakTime = Long.MIN_VALUE;
      this.idleOwedValue = -1;
      this.hit = "idle";
      this.idleTime = Long.MIN_VALUE;
      this.entityIdleTime = Long.MIN_VALUE;
      this.idleValue = -1;
      this.clientInstance = clientInstance;
      this.sneaking = sneaking;
      this.nether = new UnsafeCharge(clientInstance);
   }

   static boolean canFlag(
      float staleOwedLimit,
      float staleOwedLimit2,
      float staleOwedValue,
      boolean staleOwedFlag,
      int staleOwedValue2,
      boolean selfTakenFlag,
      boolean staleOwedFlag3,
      boolean staleOwedFlag4,
      boolean staleOwedFlag5,
      boolean staleOwedFlag6,
      boolean staleOwedFlag7,
      boolean staleOwedFlag8,
      boolean staleOwedFlag9,
      float staleOwedValue3
   ) {
      boolean staleOwedFlag10 = staleOwedLimit >= staleOwedValue - 1.0F;
      if (staleOwedFlag10) {
         if (!staleOwedFlag) {
            return false;
         } else if (staleOwedFlag3) {
            return true;
         } else {
            return staleOwedValue2 <= 1 ? false : selfTakenFlag || staleOwedFlag5 && staleOwedFlag6 && staleOwedFlag7;
         }
      } else {
         boolean staleOwedFlag11 = selfTakenFlag && staleOwedFlag3;
         if (!staleOwedFlag9 && staleOwedLimit2 > staleOwedValue3 && !staleOwedFlag11) {
            return false;
         } else if (!staleOwedFlag && staleOwedLimit > staleOwedValue - (staleOwedFlag9 ? 2.0F : 4.0F) && !staleOwedFlag11) {
            return false;
         } else {
            return !selfTakenFlag && !staleOwedFlag5 && !(staleOwedLimit2 <= 0.1F) ? staleOwedFlag7 && staleOwedFlag8 : true;
         }
      }
   }

   static int getMax(int staleOwedValue) {
      return Math.max(0, staleOwedValue - 1);
   }

   private PosBlockUtilsSelf.Plan getVetoedWeak(SelfPopData selfPopData, PosBlockUtilsSelf.Plan vetoedWeak) {
      if (!canFlag(this.idleWeakTime, selfPopData.tick())) {
         return vetoedWeak;
      } else {
         this.vetoedDoneFlag = false;
         this.handleIdleVetoed(selfPopData);
         return this.posBlockData;
      }
   }

   private boolean canTargetDamage(SelfPopData selfPopData, PosBlockUtilsSelf.Plan plan) {
      VecBlockingData target = selfPopData.target();
      if (target != null && target.entity() != null && plan.anchor() != null) {
         float minTargetDamage = Math.min(plan.targetDamage(), this.getArmorEntity(target, plan.anchor(), getValue(plan.prepTicks(), selfPopData.targetReadyIn())));
         RefBareData selfPop = selfPopData.getSelfPop();
         if (selfPop.on()) {
            return canFlag(selfPop, minTargetDamage, plan.knock(), getCalibration(target));
         } else {
            float targetDamageValue = this.data2Idle == null ? 3.0F : this.data2Idle.minDamage();
            return canFlag(minTargetDamage, getCalibration(target), targetDamageValue, false);
         }
      } else {
         return false;
      }
   }

   static int getValueForInt(int staleOwedValue, int staleOwedBound) {
      return Math.max(0, staleOwedBound) + Math.max(0, staleOwedValue - 1) * 2;
   }

   private PosBlockUtilsSelf.Plan getOwedAmount(BlockPos pos, float owedValue) {
      int chargesRespawn = BlockPosUtilsRespawn.getChargesRespawn(pos);
      int posBlockBound = chargesRespawn >= 1 ? 1 : 2;
      return new PosBlockUtilsSelf.Plan(
         pos,
         null,
         false,
         chargesRespawn,
         this.owedAmount,
         this.costOwedPlan,
         this.fireFullAmount,
         this.fireFullAmount >= owedValue - 1.0F,
         this.kills,
         posBlockBound,
         PosBlockUtilsSelf.getValue(posBlockBound),
         this.owedAmount
      );
   }

   public boolean canNether(BlockPos pos) {
      BlockPos nether = this.getNether();
      return nether != null && pos != null && !nether.equals(pos) && canPlace(this.nether.getCoverArmedType(), BlockPosUtilsRespawn.getChargesRespawn(nether));
   }

   static boolean canFlag(double staleOwedValue, boolean staleOwedFlag) {
      return !staleOwedFlag && !(staleOwedValue <= 60.0);
   }

   static boolean canFlag(boolean staleOwedFlag, double staleOwedValue) {
      return staleOwedFlag || Double.isFinite(staleOwedValue) && staleOwedValue <= 0.0;
   }

   public int getIdleValue() {
      return this.idleValue;
   }

   private Vec3d getPos(PosBlockUtilsSelf.Plan plan) {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (player != null && MinecraftClient.getInstance().world != null) {
         BlockPos anchor = plan.anchor();
         float posValue = this.clientInstance.getAmount();
         float amountFloat = this.clientInstance.getAmountFloat();
         double amountDouble = this.clientInstance.getAmountDouble();
         if (plan.placeAnchor() && BlockPosUtilsRespawn.getChargesRespawn(anchor) < 0) {
            Vec3d eyePos = player.getEyePos();
            BlockHitResult blockHitResult = null;
            double posLimit = Double.MAX_VALUE;

            for (Direction direction : posDirections) {
               BlockPos offset = anchor.offset(direction);
               if (!BlockPosUtilsRespawn.canReplaceableAir(offset)) {
                  Direction opposite = direction.getOpposite();
                  if (BlockEntityUtils.canBlockSquare(offset, opposite)) {
                     BlockHitResult eyeInstance = BlockHitResolver.findBlockHitAmongFaces(offset, new Direction[]{opposite}, opposite, amountDouble, posValue, amountFloat);
                     if (eyeInstance != null) {
                        float[] wrapDegrees = MathClampUtils.getWrapDegrees(eyePos, eyeInstance.getPos());
                        double posValue4 = BoxLengthUtils.getAmount(posValue, amountFloat, wrapDegrees[0], wrapDegrees[1]);
                        if (posValue4 < posLimit) {
                           posLimit = posValue4;
                           blockHitResult = eyeInstance;
                        }
                     }
                  }
               }
            }

            if (blockHitResult == null) {
               return null;
            } else {
               BlockHitResult posCenter = BlockHitResolver.chooseLessRotationHit(
                  blockHitResult, new Direction[]{blockHitResult.getSide()}, blockHitResult.getSide(), amountDouble, posValue, amountFloat, anchor, HumanAimRotator.getMaxStepDegrees()
               );
               return posCenter == null ? null : posCenter.getPos();
            }
         } else {
            BlockHitResult chooseLessRotationHit = BlockHitResolver.chooseLessRotationHit(
               BlockHitResolver.findNearestRotationHit(anchor, amountDouble, posValue, amountFloat), posDirections, null, amountDouble, posValue, amountFloat, anchor, HumanAimRotator.getMaxStepDegrees()
            );
            return chooseLessRotationHit == null ? null : chooseLessRotationHit.getPos();
         }
      } else {
         return null;
      }
   }

   static boolean canFlag(double staleOwedLimit, double staleOwedValue) {
      return staleOwedLimit > staleOwedValue + 0.5;
   }

   private void handleGoneKill(BlockPos pos, String goneKillText) {
      this.entityIdleTime = ClientThreadUtils.getTime();
      this.nether.updateUnsafeCharge();
      this.posBlockData = null;
      this.vetoedDoneFlag = false;
      this.killGoneFlag = false;
      this.netherDoneActive = false;
      this.startIdleTime = Long.MIN_VALUE;
      this.hit = "owed:" + goneKillText;
   }

   public boolean isNetherFlag() {
      return canDetonateCharge(this.nether.getCoverArmedType());
   }

   public CaseMaxData getNetherOwed(SelfPopData data, ComboTradeData data2, Predicate<Vec3d> predicate) {
      this.predicate = predicate;
      this.data2Idle = data2;
      this.hitStartSelfPopData = data;
      String hit = this.formatGlowstoneSneaking(data);
      if (hit != null) {
         this.hit = hit;
         return CaseMaxData.getCaseMaxData(SurroundKitType.ANCHOR, hit);
      } else if (this.nether.isCoverArmedType() && this.canStaleNether(data)) {
         return CaseMaxData.getCaseMaxData(SurroundKitType.ANCHOR, this.hit);
      } else {
         if (this.nether.isCoverArmedType()
            && !this.netherDoneActive
            && canPlace(this.nether.getCoverArmedType(), BlockPosUtilsRespawn.getChargesRespawn(this.nether.getImmutable()))) {
            BlockPos firedInstance = this.getFiredInstance(data);
            if (firedInstance != null && !firedInstance.equals(this.nether.getImmutable())) {
               this.nether.updateUnsafeCharge();
               this.posBlockData = null;
               this.vetoedDoneFlag = false;
               this.killGoneFlag = false;
            }
         }

         if (this.nether.isCoverArmedType()) {
            PosBlockUtilsSelf.Plan posBlockData = this.posBlockData;
            double netherOwedValue = posBlockData == null ? 0.0 : posBlockData.value();
            BlockPos immutable = this.nether.getImmutable();
            int staleOwedValue = immutable == null ? -1 : BlockPosUtilsRespawn.getChargesRespawn(immutable);
            int netherOwedValue3 = !this.netherDoneActive && canPlace(this.nether.getCoverArmedType(), staleOwedValue) && this.nether.isArmedCoverFlag()
               ? 10
               : Math.min(this.nether.getPosBlock(), 9);
            return new CaseMaxData(
               SurroundKitType.ANCHOR,
               true,
               netherOwedValue,
               netherOwedValue3,
               posBlockData != null && canDamageTarget(data.target(), posBlockData),
               posBlockData != null && posBlockData.kills(),
               formatArmedUnplaced(this.netherDoneActive, this.nether.getCoverArmedType(), staleOwedValue)
            );
         } else {
            BlockPos firedInstance2 = this.getFiredInstance(data);
            if (firedInstance2 != null) {
               this.hit = "owed";
               return new CaseMaxData(
                  SurroundKitType.ANCHOR,
                  true,
                  Math.max(this.owedAmount, 0.25F),
                  IframeComboUtils.getValue(this.owedAmountFired),
                  this.owedPlanFlag,
                  this.kills,
                  "owed"
               );
            } else {
               VecBlockingData target = data.target();
               this.handleIdleVetoed(data);
               PosBlockUtilsSelf.Plan plan = this.posBlockData;
               if (plan == null) {
                  this.hit = "no plan";
                  return CaseMaxData.getCaseMaxData(SurroundKitType.ANCHOR, this.hit);
               } else {
                  boolean freshTaken = this.canFreshTaken(data);
                  if (freshTaken && !this.canClicksTarget(data, plan)) {
                     this.hit = "hit";
                     return CaseMaxData.getCaseMaxData(SurroundKitType.ANCHOR, this.hit);
                  } else {
                     int powBound = canTerminator(data) ? 0 : Math.max(0, data.targetReadyIn() - plan.prepTicks());
                     double netherOwedValue5 = data.getSelfPop().getAmount(plan.value() * PosBlockUtilsBonus.getPow(powBound));
                     this.hit = freshTaken ? "instant" : "ok";
                     if (!freshTaken && plan.dig() > 0) {
                        this.hit = "dig";
                     }

                     return new CaseMaxData(SurroundKitType.ANCHOR, true, netherOwedValue5, plan.prepTicks(), canDamageTarget(target, plan), plan.kills(), this.hit);
                  }
               }
            }
         }
      }
   }

   private boolean canVelocityTarget(SelfPopData selfPopData) {
      VecBlockingData target = selfPopData.target();
      return target != null && selfPopData.targetReadyIn() <= 5 && canFlag(target.onGround(), target.velocity() == null ? 0.0 : target.velocity().y);
   }

   static CarefulBalancedType getCfgPlaystyle(SelfPopData selfPopData) {
      return selfPopData != null && selfPopData.cfg() != null && selfPopData.cfg().playstyle() != null ? selfPopData.cfg().playstyle() : CarefulBalancedType.TERMINATOR;
   }

   private static boolean canGlowstoneAnchor(BowGappleType bowGappleType) {
      return bowGappleType == BowGappleType.GLOWSTONE || bowGappleType == BowGappleType.ANCHOR;
   }

   static boolean canAnchorAvailable(SelfPopData selfPopData, ComboTradeData comboTradeData, CenterVecData centerVecData) {
      if (comboTradeData.lane() == SurroundKitType.ANCHOR) {
         return true;
      } else if (centerVecData != null) {
         return centerVecData.lane() == SurroundKitType.ANCHOR && centerVecData.anchor() == null;
      } else if (comboTradeData.lane() != SurroundKitType.NONE) {
         return false;
      } else {
         CaseMaxData options = selfPopData.getOptions(SurroundKitType.ANCHOR);
         if (options.available() && Double.isFinite(options.value())) {
            CaseMaxData options2 = selfPopData.getOptions(SurroundKitType.CRYSTAL);
            CaseMaxData options3 = selfPopData.getOptions(SurroundKitType.MELEE);
            return (!options2.available() || options.value() >= options2.value()) && (!options3.available() || options.value() >= options3.value());
         } else {
            return false;
         }
      }
   }

   private boolean canTimeTick(SelfPopData selfPopData, BlockPos pos, long tickValue) {
      if (selfPopData != null && pos != null) {
         if (this.tickIdle != selfPopData.tick()) {
            this.tickFireMap.clear();
            this.tickIdle = selfPopData.tick();
         }

         StaleOwed.Fire fire = this.tickFireMap.get(pos.asLong());
         if (fire == null) {
            fire = this.getSelfTaken(selfPopData, pos, tickValue);
            this.tickFireMap.put(pos.asLong(), fire);
         }

         this.handleOwedPlan(fire);
         return fire.ok();
      } else {
         return false;
      }
   }

   static boolean canFlagForLong(long staleOwedLimit, long staleOwedValue) {
      return staleOwedLimit != Long.MIN_VALUE && staleOwedValue <= staleOwedLimit;
   }

   public boolean isIdleEntity() {
      return this.entityIdleTime == ClientThreadUtils.getTime();
   }

   static boolean canStaleAway(String staleAwayText) {
      return "stale far".equals(staleAwayText) || "stale reach".equals(staleAwayText) || "stale away".equals(staleAwayText);
   }

   private int getSneakingTotem(SelfPopData selfPopData, PosBlockUtilsSelf.Plan plan) {
      boolean sneakingTotemFlag = selfPopData.self() != null && selfPopData.self().totemConfirmed();
      if (plan.needsTotem() && !sneakingTotemFlag) {
         int selectedSlot = this.sneaking.findSlotMatching(stack -> stack.isOf(Items.TOTEM_OF_UNDYING), true);
         return selectedSlot >= 0 && !AutoInventoryTotem.isSlotInPendingSwap(selectedSlot) ? selectedSlot : -1;
      } else {
         int slotMatching = this.sneaking.findSlotMatching(InventoryStackUtilsEmpty::canSwordsEmpty, false);
         return slotMatching >= 0 ? slotMatching : this.sneaking.findSlotMatching(BlockPosUtilsRespawn::canRespawnGlowstone, false);
      }
   }

   private void handleIdleVetoed(SelfPopData selfPopData) {
      long idleWeakTime = selfPopData.tick();
      int idleOwedValue = selfPopData.target().id();
      if (canFlag(this.vetoedDoneFlag, this.idleOwedValue, idleOwedValue, this.idleWeakTime, idleWeakTime)) {
         PosBlockUtilsSelf.Plan posBlockData = this.getPosBlockData(selfPopData, null, true);
         this.posBlockData = posBlockData != null && this.canTargetDamage(selfPopData, posBlockData) ? posBlockData : null;
         this.vetoedDoneFlag = true;
         this.idleWeakTime = idleWeakTime;
         this.idleOwedValue = idleOwedValue;
      }
   }

   private PosBlockUtilsSelf.Plan getSelfCalibration(SelfPopData selfPopData, BlockPos pos, boolean selfCalibrationFlag, BlockEntityUtils.Predicted predicted) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      ClientWorld world = instance.world;
      VecBlockingData target = selfPopData.target();
      AbsorptionPosData self = selfPopData.self();
      if (player != null && world != null && target != null && target.entity() != null && self != null) {
         int selfCalibrationValue = Math.max(2, Math.min(8, selfPopData.targetReadyIn()));
         BlockEntityUtils.Predicted predicted2 = predicted != null ? predicted : BlockEntityUtils.getInterpolatorInstance(target.entity(), selfCalibrationValue, false, true);
         BlockEntityUtils.Predicted predicted3 = self.pos() != null && self.box() != null ? new BlockEntityUtils.Predicted(self.pos(), self.box()) : null;
         Vec3d posFloored = getPosFloored(target, self, world);
         ComboTradeData data2Idle07 = this.data2Idle;
         BonusLevelData minData = BonusLevelData.getMinData();
         float maxSelfDamage2;
         boolean allowTotemTrade2;
         if (data2Idle07 != null) {
            float v = data2Idle07.weights() == null ? minData.offWeight() : data2Idle07.weights().off();
            minData = minData.getBonusLevelForFloat(data2Idle07.minDamage()).getBonusLevelPlanCone(data2Idle07.selfWeight()).getBonusLevel(v);
            maxSelfDamage2 = data2Idle07.maxSelfDamage();
            allowTotemTrade2 = data2Idle07.allowTotemTrade();
         } else {
            maxSelfDamage2 = UnplacedAntiUtils.getBareRef(
               self.health(), self.absorption(), self.totemConfirmed(), selfPopData.ticksSinceOwnPop(), 11.0F, selfPopData.getSelfPop()
            );
            allowTotemTrade2 = false;
         }

         if (canCfgPlaystyle(selfPopData)) {
            minData = minData.getBonusLevel(selfPopData.selfSpent(), false);
         }

         RefBareData selfPop = selfPopData.getSelfPop();
         if (selfPop.on()) {
            minData = minData.getBonusLevel(selfPop).getBonusLevelPlanCone(selfPop.getAmount(minData.selfWeight()));
         }

         float selfCalibrationDivisor = target.calibration() > 0.0F && Float.isFinite(target.calibration()) ? target.calibration() : 1.0F;
         int selectedSlot = this.sneaking.findSlotMatching(stack -> stack.isOf(Items.RESPAWN_ANCHOR), false);
         int slotMatching = this.sneaking.findSlotMatching(stack2 -> stack2.isOf(Items.GLOWSTONE), false);
         int selfCalibrationValue4 = slotMatching < 0 ? 0 : getInventoryGlowstone(player);
         Predicate predicate = this.getPredicate(this.predicate, selectedSlot < 0 ? world : null);
         PosBlockUtilsSelf.Request request = new PosBlockUtilsSelf.Request(
            target.entity(),
            predicted2,
            predicted3,
            this.clientInstance.getAmount(),
            this.clientInstance.getAmountFloat(),
            maxSelfDamage2,
            allowTotemTrade2,
            new PosBlockUtilsBonus.Window(selfPopData.targetReadyIn(), target.lastScaledTaken()),
            new PosBlockUtilsBonus.Window(selfPopData.selfClosedFor(), self.lastScaledTaken()),
            target.getHealthTrusted() / selfCalibrationDivisor,
            target.totemInHand(),
            selfCalibrationFlag,
            selfCalibrationValue4,
            minData,
            predicate,
            pos,
            posFloored
         );
         PosBlockUtilsSelf.Plan forcedImmutable = PosBlockUtilsSelf.getForcedImmutable(request);
         if (forcedImmutable == null || forcedImmutable.anchor() == null) {
            return null;
         } else if (pos == null && this.canForcedPos(forcedImmutable.anchor())) {
            return null;
         } else {
            return !canFlag(forcedImmutable.placeAnchor(), forcedImmutable.charges(), selectedSlot, slotMatching, this.getSneakingTotem(selfPopData, forcedImmutable)) ? null : forcedImmutable;
         }
      } else {
         return null;
      }
   }

   private void handleForcedRefused(BlockPos pos) {
      if (pos != null) {
         long forcedRefusedValue = ConnectionTimeUtils.getTimeTimeMillis();
         long long2 = pos.asLong();
         StaleOwed.Strike strike = this.forcedRefusedStrikeMap.get(long2);
         int staleOwedLimit = strike != null && forcedRefusedValue < strike.until() ? strike.count() + 1 : 1;
         if (canFlag(staleOwedLimit)) {
            this.forcedRefusedStrikeMap.remove(long2);
            this.forcedRefusedMap.put(long2, forcedRefusedValue + 3000L);
         } else {
            this.forcedRefusedStrikeMap.put(long2, new StaleOwed.Strike(staleOwedLimit, forcedRefusedValue + 3000L));
         }
      }
   }

   public static boolean canTerminator(SelfPopData selfPopData) {
      return selfPopData != null && (selfPopData.getSelfPop().on() || getCfgPlaystyle(selfPopData) == CarefulBalancedType.TERMINATOR);
   }

   public boolean isIdleTime() {
      return this.idleTime == ClientThreadUtils.getTime();
   }

   private static int getInventoryGlowstone(ClientPlayerEntity player) {
      int[] ints = new int[9];
      boolean[] booleans = new boolean[9];
      boolean[] selectedShieldFlags = new boolean[9];

      for (int i = 0; i < 9; i++) {
         ItemStack stack = player.getInventory().getStack(i);
         ints[i] = stack.isOf(Items.GLOWSTONE) ? stack.getCount() : 0;
         booleans[i] = AutoInventoryTotem.wasTotemSlot(i);
         selectedShieldFlags[i] = AutoInventoryTotem.isSlotInPendingSwap(i);
      }

      return getValue(ints, AutoInventoryTotem.getReserveSlotIfEnabled(), booleans, selectedShieldFlags);
   }

   private boolean canClientTaken(SelfPopData selfPopData, VecBlockingData vecBlockingData, BlockPos pos, int clientTakenValue) {
      LivingEntity entity = vecBlockingData.entity();
      float maxBoxBounding = Math.max(
         EntityCenterUtils.getBoxBounding(entity, null, pos, null),
         EntityCenterUtils.getBoxBounding(entity, BlockEntityUtils.getInterpolatorInstance(entity, clientTakenValue, false, true), pos, null)
      );
      PosBlockUtilsBonus.Armor armor = vecBlockingData.armor() != null ? vecBlockingData.armor() : BlockEntityUtils.getAttributeArmor(entity);
      float scaledInstance = PosBlockUtilsBonus.getScaledInstance(maxBoxBounding, armor, new PosBlockUtilsBonus.Window(selfPopData.targetReadyIn(), vecBlockingData.lastScaledTaken()), clientTakenValue);
      float difficultyInstance = PosBlockUtilsBonus.getDifficultyInstance(maxBoxBounding, armor, 5.0F);
      float calibration = getCalibration(vecBlockingData);
      if (selfPopData.getSelfPop().ref()) {
         int landTicks = Math.max(1, getValue(this.nether.getCoverArmedType(), BlockPosUtilsRespawn.getChargesRespawn(pos)));
         float scaledInstance2 = PosBlockUtilsBonus.getScaledInstance(maxBoxBounding, armor, new PosBlockUtilsBonus.Window(selfPopData.targetReadyIn(), vecBlockingData.lastScaledTaken()), landTicks);
         this.clientInstance.handleAmountTime(scaledInstance2, difficultyInstance, Float.isFinite(calibration) && calibration > 0.0F && scaledInstance2 >= calibration, calibration, getSelfInstance(selfPopData, pos, landTicks));
      } else {
         this.clientInstance.handle(scaledInstance, difficultyInstance, Float.isFinite(calibration) && calibration > 0.0F && scaledInstance >= calibration, calibration);
      }

      return canFlag(selfPopData.getSelfPop(), scaledInstance, difficultyInstance, calibration);
   }

   public BlockPos getPosBlock() {
      return this.isIdleTime() ? this.blockPosIdle : null;
   }

   public String getHit() {
      return this.hit;
   }

   private String formatGlowstoneSneaking(SelfPopData selfPopData) {
      if (selfPopData == null || selfPopData.target() == null || selfPopData.target().entity() == null) {
         return "no target";
      } else if (selfPopData.cfg() != null && !selfPopData.cfg().anchors()) {
         return "off";
      } else if (!BlockPosUtilsRespawn.isRegistryWorld()) {
         return "nether";
      } else if (this.nether.isCoverArmedType()) {
         return null;
      } else {
         MinecraftClient instance = MinecraftClient.getInstance();
         ClientPlayerEntity player = instance.player;
         ClientWorld world = instance.world;
         if (player != null && world != null) {
            if (player.getOffHandStack().isOf(Items.GLOWSTONE)) {
               return "glowstone in offhand";
            } else if (!player.isSneaking() && !this.clientInstance.isSprintSneak()) {
               boolean glowstoneSneakingFlag = this.sneaking.findSlotMatching(stack -> stack.isOf(Items.RESPAWN_ANCHOR), false) >= 0;
               boolean glowstoneSneakingFlag2 = this.sneaking.findSlotMatching(stack2 -> stack2.isOf(Items.GLOWSTONE), false) >= 0;
               if (glowstoneSneakingFlag && glowstoneSneakingFlag2) {
                  return null;
               } else if (!this.canFlooredCenter(player, world)) {
                  return glowstoneSneakingFlag ? "no glowstone" : "no anchors";
               } else {
                  return null;
               }
            } else {
               return "sneaking";
            }
         } else {
            return "no world";
         }
      }
   }

   public List<BlockPos> getFiredPos(Vec3d vec, List<VecBlockingData> list) {
      boolean posBlock = this.canPosBlock(this.blockPosForcedPos);
      if (vec != null && (!this.doneFiredMap.isEmpty() || posBlock)) {
         SelfPopData hitStartSelfPopData02 = this.hitStartSelfPopData;
         LivingEntity livingEntity = hitStartSelfPopData02 != null && hitStartSelfPopData02.target() != null ? hitStartSelfPopData02.target().entity() : null;
         Vec3d vec3d = livingEntity == null ? null : livingEntity.getEyePos();
         double rangeInteractionOffset = BlockPosUtilsRespawn.getRangeInteraction() + 0.5;
         ArrayList arrayList = new ArrayList();

         for (long firedPosValue : this.doneFiredMap.keySet()) {
            BlockPos long2 = BlockPos.fromLong(firedPosValue);
            if (BlockPosUtilsRespawn.getChargesRespawn(long2) >= 0) {
               Box box = BlockEntityUtils.getBoxForBlockPos(long2);
               if (!(BlockEntityUtils.getMinMax(vec, box) > rangeInteractionOffset) && !canEyeEntity(box, vec3d, list)) {
                  arrayList.add(long2);
               }
            }
         }

         if (posBlock && !arrayList.contains(this.blockPosForcedPos)) {
            arrayList.add(this.blockPosForcedPos);
         }

         return arrayList;
      } else {
         return List.of();
      }
   }

   private void handleOwedPlan(StaleOwed.Fire fire) {
      StaleOwed.Fire fire2 = fire == null ? StaleOwed.Fire.INSTANCE : fire;
      this.owedAmount = fire2.dealt();
      this.costOwedPlan = fire2.cost();
      this.fireFullAmount = fire2.full();
      this.owedPlanFlag = fire2.pops();
      this.kills = fire2.kills();
   }

   static boolean canFlag(double staleOwedValue, double staleOwedValue2, double staleOwedFactor, double staleOwedFactor2, double staleOwedValue3, double staleOwedValue4, double staleOwedValue5, double staleOwedValue6) {
      double staleOwedFactor3 = staleOwedValue3 - staleOwedValue;
      double staleOwedFactor4 = staleOwedValue4 - staleOwedValue2;
      double distance = Math.sqrt(staleOwedFactor3 * staleOwedFactor3 + staleOwedFactor4 * staleOwedFactor4);
      if (distance < 1.0E-6) {
         return false;
      } else {
         double staleOwedFactor5 = staleOwedFactor3 / distance;
         double staleOwedFactor6 = staleOwedFactor4 / distance;
         double staleOwedValue7 = (staleOwedValue5 - staleOwedValue) * staleOwedFactor5 + (staleOwedValue6 - staleOwedValue2) * staleOwedFactor6;
         double staleOwedValue8 = staleOwedFactor * staleOwedFactor5 + staleOwedFactor2 * staleOwedFactor6;
         return staleOwedValue7 < -0.5 && staleOwedValue8 < -0.08;
      }
   }

   static boolean canDetonateCharge(UnsafeCharge.Phase phase) {
      return phase == UnsafeCharge.Phase.CHARGE || phase == UnsafeCharge.Phase.ARMED || phase == UnsafeCharge.Phase.DETONATE;
   }

   static BlockEntityUtils.Predicted getFeetBox(CenterVecData.AnchorStep anchorStep) {
      return anchorStep != null && anchorStep.feetAt() != null && anchorStep.boxAt() != null ? new BlockEntityUtils.Predicted(anchorStep.feetAt(), anchorStep.boxAt()) : null;
   }

   private StaleOwed.Fire getSelfTaken(SelfPopData selfPopData, BlockPos pos, long selfTakenValue) {
      VecBlockingData target = selfPopData.target();
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (target != null && target.entity() != null && player != null && selfPopData.self() != null) {
         LivingEntity entity = target.entity();
         int selfTakenLimit = BlockPosUtilsRespawn.getChargesRespawn(pos) >= 1 ? 1 : 2;
         float maxBoxBounding = Math.max(
            EntityCenterUtils.getBoxBounding(entity, null, pos, null),
            EntityCenterUtils.getBoxBounding(entity, BlockEntityUtils.getInterpolatorInstance(entity, selfTakenLimit, false, true), pos, null)
         );
         PosBlockUtilsBonus.Armor armor = target.armor() != null ? target.armor() : BlockEntityUtils.getAttributeArmor(entity);
         float scaledInstance = PosBlockUtilsBonus.getScaledInstance(maxBoxBounding, armor, new PosBlockUtilsBonus.Window(selfPopData.targetReadyIn(), target.lastScaledTaken()), selfTakenLimit);
         float difficultyInstance = PosBlockUtilsBonus.getDifficultyInstance(maxBoxBounding, armor, 5.0F);
         float calibration = getCalibration(target);
         boolean selfTakenFlag = Float.isFinite(calibration) && calibration > 0.0F && scaledInstance >= calibration;
         float boxBounding = EntityCenterUtils.getBoxBounding(pos, null, null);
         PosBlockUtilsBonus.Armor attributeArmor = BlockEntityUtils.getAttributeArmor(player);
         float toughnessEpf = PosBlockUtilsBonus.getToughnessEpf(boxBounding, attributeArmor);
         float scaledInstance2 = PosBlockUtilsBonus.getScaledInstance(boxBounding, attributeArmor, new PosBlockUtilsBonus.Window(selfPopData.selfClosedFor(), selfPopData.self().lastScaledTaken()), 1);
         boolean selfTakenFlag2 = BlockEntityUtils.getMinMax(entity.getEyePos(), BlockEntityUtils.getBoxForBlockPos(pos)) <= 4.5;
         boolean selfTakenFlag3 = scaledInstance >= 1.0F || difficultyInstance >= 0.25F;
         boolean selfTakenFlag4 = canTerminator(selfPopData) || selfPopData.targetReadyIn() <= 0 || canFlag(scaledInstance, calibration) || selfPopData.tick() - selfTakenValue >= 10L;
         float staleOwedValue = player.getHealth() + player.getAbsorptionAmount();
         boolean selfTakenFlag5 = selfTakenFlag && !target.totemInHand();
         boolean selfTakenFlag6 = selfPopData.targetReadyIn() <= selfTakenLimit;
         float full = selfTakenLimit == 1
            ? scaledInstance2
            : PosBlockUtilsBonus.getScaledInstance(boxBounding, attributeArmor, new PosBlockUtilsBonus.Window(selfPopData.selfClosedFor(), selfPopData.self().lastScaledTaken()), selfTakenLimit);
         boolean selfTakenFlag7 = selfPopData.getSelfPop().ref()
            ? canFlag(selfPopData.getSelfPop(), toughnessEpf, full, staleOwedValue, scaledInstance, selfTakenFlag, selfTakenFlag5, selfTakenFlag2, selfTakenFlag3)
            : canFlag(
               toughnessEpf,
               scaledInstance2,
               staleOwedValue,
               selfPopData.self().totemConfirmed(),
               selfPopData.self().totems(),
               selfTakenFlag,
               selfTakenFlag5,
               selfPopData.isSelfTarget(),
               selfTakenFlag2,
               selfTakenFlag6,
               selfTakenFlag3,
               selfTakenFlag4,
               selfPopData.getSelfPop().on(),
               this.clientInstance.getAbsorptionAmount()
            );
         boolean selfTakenFlag8 = selfTakenFlag7 && (selfTakenLimit == 1 || selfTakenFlag || selfTakenFlag2 || selfTakenFlag3);
         return new StaleOwed.Fire(selfTakenFlag8, scaledInstance, scaledInstance2, toughnessEpf, selfTakenFlag, selfTakenFlag5);
      } else {
         return StaleOwed.Fire.INSTANCE;
      }
   }

   private boolean canStartSneaking(SelfPopData selfPopData, PosBlockUtilsSelf.Plan plan, boolean sneakingFlag) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      if (player != null && instance.world != null && plan.anchor() != null) {
         int chargesRespawn = BlockPosUtilsRespawn.getChargesRespawn(plan.anchor());
         int selectedSlot = this.sneaking.findSlotMatching(stack -> stack.isOf(Items.RESPAWN_ANCHOR), false);
         int slotMatching = this.sneaking.findSlotMatching(stack2 -> stack2.isOf(Items.GLOWSTONE), false);
         int sneakingTotem = this.getSneakingTotem(selfPopData, plan);
         if (!canFlag(chargesRespawn < 0, chargesRespawn, selectedSlot, slotMatching, sneakingTotem)) {
            return false;
         } else {
            this.idleValue = chargesRespawn < 0 ? -1 : 2;
            this.netherDoneActive = false;
            boolean unsafeImmutable = this.nether.canUnsafeImmutable(plan, selectedSlot, slotMatching, sneakingTotem, !canTerminator(selfPopData));
            this.startIdleTime = unsafeImmutable && sneakingFlag ? getTime(this.clientInstance.getTime(), plan.clicks(), IframeComboUtils.getValue(plan.turn())) : Long.MIN_VALUE;
            this.hit = unsafeImmutable ? "start" : "can't start";
            return unsafeImmutable;
         }
      } else {
         return false;
      }
   }

   public static boolean canFlag(BowGappleType bowGappleType, BowGappleType bowGappleType2) {
      return !canGlowstoneAnchor(bowGappleType) && !canGlowstoneAnchor(bowGappleType2);
   }

   static boolean canFlag(int staleOwedLimit, int staleOwedValue, int staleOwedBound, boolean staleOwedFlag) {
      if (staleOwedLimit >= 1 && staleOwedLimit <= 3) {
         int valueForInt = getValueForInt(staleOwedLimit, staleOwedBound);
         return valueForInt <= 5 && staleOwedValue <= valueForInt && (staleOwedFlag || staleOwedLimit <= 1);
      } else {
         return false;
      }
   }

   public void handleNetherDone(boolean netherDoneFlag) {
      if (this.nether.isCoverArmedType()) {
         SelfPopData selfPopData = this.hitStartSelfPopData;
         if (selfPopData == null || selfPopData.tick() != this.clientInstance.getTime() || this.staleNetherValue == this.clientInstance.getTime() || !this.canStaleNether(selfPopData)) {
            BlockPos blockPos = this.nether.getImmutable();
            if (blockPos != null && this.predicate != null && this.predicate.test(Vec3d.ofCenter(blockPos))) {
               this.nether.updateUnsafeCharge();
               this.posBlockData = null;
               this.vetoedDoneFlag = false;
               this.hit = "vetoed";
            } else {
               if (!netherDoneFlag && this.nether.getCoverArmedType() == UnsafeCharge.Phase.ARMED && this.canEntityArmor(selfPopData, blockPos)) {
                  netherDoneFlag = true;
               }

               if (!netherDoneFlag && this.nether.getCoverArmedType() == UnsafeCharge.Phase.ARMED && selfPopData != null && canTerminator(selfPopData)) {
                  netherDoneFlag = true;
               }

               if (netherDoneFlag && this.nether.getCoverArmedType() == UnsafeCharge.Phase.ARMED && !this.canNetherClient(blockPos) && !this.canFiredDone(blockPos)) {
                  this.entityIdleTime = ClientThreadUtils.getTime();
                  if (blockPos != null) {
                     this.forcedRefusedMap.put(blockPos.asLong(), ConnectionTimeUtils.getTimeTimeMillis() + 3000L);
                  }

                  this.nether.updateUnsafeCharge();
                  this.posBlockData = null;
                  this.vetoedDoneFlag = false;
                  this.killGoneFlag = false;
                  this.hit = "kill gone";
               } else {
                  if (netherDoneFlag) {
                     this.nether.updateGoneCharge();
                  }

                  if (blockPos != null && this.netherDoneActive && BlockPosUtilsRespawn.getChargesRespawn(blockPos) >= 0) {
                     this.handleFiredDoneForBlockPosLong(blockPos, ClientThreadUtils.getTime());
                  }

                  StaleOwed.Owed owed = blockPos == null ? null : this.doneFiredMap.get(blockPos.asLong());
                  this.nether
                     .setUnsafeCoverFlag(
                        owed != null && selfPopData != null && selfPopData.tick() == this.clientInstance.getTime() && this.canTimeTick(selfPopData, blockPos, owed.since())
                     );
                  UnsafeCharge.Phase coverArmedType = this.nether.getCoverArmedType();
                  BlockPosHelperBlocked.Result netherArmed = this.nether.getNetherArmed();
                  if (this.idleValue < 0 && coverArmedType == UnsafeCharge.Phase.PLACE) {
                     if (netherArmed == BlockPosHelperBlocked.Result.ACTED || this.nether.isArmedCoverCoverArmedType()) {
                        this.idleValue = 1;
                     } else if (this.nether.isArmedCover()) {
                        this.idleValue = 0;
                     } else if (netherArmed != BlockPosHelperBlocked.Result.FAILED && this.nether.getCoverArmedType() != UnsafeCharge.Phase.PLACE) {
                        this.idleValue = 2;
                     }
                  }

                  long idleTime = ClientThreadUtils.getTime();
                  UnsafeCharge.Phase coverArmedType2 = this.nether.getCoverArmedType();
                  if (blockPos != null
                     && (
                        coverArmedType2 == UnsafeCharge.Phase.ARMED
                           || coverArmedType2 == UnsafeCharge.Phase.DETONATE
                           || netherArmed == BlockPosHelperBlocked.Result.ACTED && coverArmedType2 == UnsafeCharge.Phase.CHARGE
                           || this.netherDoneActive
                              && (coverArmedType2 == UnsafeCharge.Phase.COVER || coverArmedType2 == UnsafeCharge.Phase.CHARGE)
                              && BlockPosUtilsRespawn.getChargesRespawn(blockPos) >= 0
                     )) {
                     this.handleFiredDoneForBlockPosLong(blockPos, idleTime);
                  }

                  if (netherArmed == BlockPosHelperBlocked.Result.ACTED) {
                     this.netherDoneActive = true;
                  }

                  if (netherArmed == BlockPosHelperBlocked.Result.ACTED && this.nether.getCoverArmedType() == UnsafeCharge.Phase.DETONATE) {
                     this.idleTime = idleTime;
                     this.blockPosIdle = blockPos;
                     this.blockPosForcedPos = blockPos;
                     this.netherDoneValue = ConnectionTimeUtils.getTimeTimeMillis() + EntityDamageUtils.getCachedHurtTimeLimit() + 100L;
                     FloorMathUtilsBlock.handleImmutableAmount(blockPos, 5.0F, this.netherDoneValue);
                     StaleOwed.Owed owed2 = blockPos == null ? null : this.doneFiredMap.get(blockPos.asLong());
                     if (owed2 != null) {
                        int failsOffset = owed2.fails() + 1;
                        if (failsOffset >= 3) {
                           this.doneFiredMap.remove(blockPos.asLong());
                           this.forcedRefusedMap.put(blockPos.asLong(), ConnectionTimeUtils.getTimeTimeMillis() + 3000L);
                        } else {
                           long netherDoneValue = idleTime + 1L + (int)Math.ceil((EntityDamageUtils.getCachedHurtTimeLimit() + 100L) / 50.0);
                           this.doneFiredMap.put(blockPos.asLong(), new StaleOwed.Owed(owed2.since(), netherDoneValue, failsOffset));
                        }
                     }

                     this.hit = "fired";
                     this.posBlockData = null;
                     this.vetoedDoneFlag = false;
                     this.nether.updateUnsafeCharge();
                     this.killGoneFlag = false;
                     this.netherDoneActive = false;
                     this.startIdleTime = Long.MIN_VALUE;
                  } else {
                     if (netherArmed == BlockPosHelperBlocked.Result.FAILED) {
                        this.entityIdleTime = idleTime;
                        String netherDoneText = this.nether.getText();
                        this.hit = netherDoneText == null ? "failed" : netherDoneText;
                        BlockPos immutable = this.nether.getImmutable();
                        if (immutable != null && this.canFiredDone(immutable) && BlockPosUtilsRespawn.getChargesRespawn(immutable) >= 0) {
                           this.handleFiredDone(immutable, netherDoneText, idleTime);
                           return;
                        }

                        boolean netherDoneFlag2 = "timeout".equals(netherDoneText) && coverArmedType == UnsafeCharge.Phase.ARMED
                           || coverArmedType == UnsafeCharge.Phase.CHARGE && netherDoneText != null && netherDoneText.endsWith("no line of sight")
                           || coverArmedType == UnsafeCharge.Phase.PLACE && netherDoneText != null && netherDoneText.endsWith("entity");
                        if (immutable != null && netherDoneFlag2) {
                           this.handleForcedRefused(immutable);
                        } else if (immutable != null) {
                           this.forcedRefusedMap.put(immutable.asLong(), ConnectionTimeUtils.getTimeTimeMillis() + 3000L);
                        }

                        this.posBlockData = null;
                        this.vetoedDoneFlag = false;
                        this.nether.updateUnsafeCharge();
                        this.killGoneFlag = false;
                     } else if (netherArmed == BlockPosHelperBlocked.Result.DONE) {
                        if (blockPos != null) {
                           this.doneFiredMap.remove(blockPos.asLong());
                        }

                        this.hit = "done";
                        this.posBlockData = null;
                        this.vetoedDoneFlag = false;
                        this.nether.updateUnsafeCharge();
                        this.killGoneFlag = false;
                     } else {
                        this.hit = this.nether.getCoverArmedType().name().toLowerCase(Locale.ROOT);
                     }
                  }
               }
            }
         }
      }
   }

   static boolean canFlag(float staleOwedLimit, float staleOwedValue, float staleOwedBound, boolean staleOwedFlag) {
      return staleOwedLimit >= staleOwedValue ? true : staleOwedLimit >= (staleOwedFlag ? 2.0F : Math.max(3.0F, staleOwedBound));
   }

   private void handleFiredDone(BlockPos pos, String firedDoneText, long firedDoneValue) {
      long long2 = pos.asLong();
      StaleOwed.Owed donefiredmap = this.doneFiredMap.get(long2);
      if (donefiredmap != null) {
         int firedDoneLimit = donefiredmap.fails() + (canSightLine(firedDoneText) ? 1 : 0);
         if (firedDoneLimit >= 3) {
            this.doneFiredMap.remove(long2);
            this.forcedRefusedMap.put(long2, ConnectionTimeUtils.getTimeTimeMillis() + 3000L);
         } else {
            this.doneFiredMap.put(long2, new StaleOwed.Owed(donefiredmap.since(), firedDoneValue + 2L, firedDoneLimit));
         }
      }

      this.handleGoneKill(pos, firedDoneText);
   }

   private float getArmorEntity(VecBlockingData vecBlockingData, BlockPos pos, int armorEntityValue) {
      LivingEntity entity = vecBlockingData.entity();
      float boxBounding = EntityCenterUtils.getBoxBounding(entity, null, pos, null);
      float rocketsElytraBound = EntityCenterUtils.getBoxBounding(entity, BlockEntityUtils.getInterpolatorInstance(entity, armorEntityValue, false, true), pos, null);
      PosBlockUtilsBonus.Armor armor = vecBlockingData.armor() != null ? vecBlockingData.armor() : BlockEntityUtils.getAttributeArmor(entity);
      return PosBlockUtilsBonus.getToughnessEpf(Math.max(boxBounding, rocketsElytraBound), armor);
   }

   private boolean canOwedVetoed(SelfPopData selfPopData) {
      BlockPos firedInstance = this.getFiredInstance(selfPopData);
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (firedInstance != null && player != null) {
         this.posBlockData = this.getOwedAmount(firedInstance, player.getHealth() + player.getAbsorptionAmount());
         this.vetoedDoneFlag = true;
         this.idleWeakTime = selfPopData.tick();
         this.idleOwedValue = selfPopData.target().id();
         this.killGoneFlag = false;
         if (this.canStartSneaking(selfPopData, this.posBlockData, false)) {
            this.hit = "owed";
            return true;
         } else {
            this.handleFiredDone(firedInstance, selfPopData.tick());
            this.posBlockData = null;
            this.vetoedDoneFlag = false;
            return false;
         }
      } else {
         return false;
      }
   }

   public String formatNether() {
      return this.nether.isCoverArmedType() ? this.nether.getCoverArmedType().name() : "-";
   }

   static int getValue(int staleOwedValue, boolean staleOwedFlag) {
      return Math.max(0, staleOwedValue - 1 + (staleOwedFlag ? 1 : 0));
   }

   static boolean canFlagForInt(int staleOwedLimit, int staleOwedValue) {
      return staleOwedLimit >= 1 && staleOwedValue <= 0;
   }

   static String formatArmedUnplaced(boolean armedUnplacedFlag, UnsafeCharge.Phase phase, int armedUnplacedValue) {
      if (armedUnplacedValue < 1 && phase != UnsafeCharge.Phase.ARMED && phase != UnsafeCharge.Phase.DETONATE) {
         return armedUnplacedFlag ? "placed" : "unplaced";
      } else {
         return "armed";
      }
   }

   public boolean canHitIdle(SelfPopData data, CenterVecData.AnchorStep anchorStep) {
      if (!this.nether.isCoverArmedType() && data != null && data.target() != null && anchorStep != null && anchorStep.cell() != null) {
         this.hitStartSelfPopData = data;
         if (this.formatGlowstoneSneaking(data) != null) {
            this.entityIdleTime = ClientThreadUtils.getTime();
            return false;
         } else {
            BlockPos immutable = anchorStep.cell().toImmutable();
            if (this.canForcedPos(immutable)) {
               this.entityIdleTime = ClientThreadUtils.getTime();
               return false;
            } else if (BlockPosUtilsRespawn.getChargesRespawn(immutable) < 0 && FloorMathUtilsBlock.canInterpolatorLerped(immutable, FloorMathUtilsBlock.getValueInt())) {
               this.hit = "entity";
               return false;
            } else {
               boolean freshTaken = this.canFreshTaken(data);
               if (freshTaken && !canFlagForInt(BlockPosUtilsRespawn.getChargesRespawn(immutable), data.targetReadyIn()) && !this.canVelocityTarget(data)) {
                  this.entityIdleTime = ClientThreadUtils.getTime();
                  this.hit = "hit";
                  return false;
               } else {
                  PosBlockUtilsSelf.Plan posBlockData = this.getSelfCalibration(data, immutable, true, getFeetBox(anchorStep));
                  if (posBlockData == null) {
                     this.entityIdleTime = ClientThreadUtils.getTime();
                     this.forcedRefusedMap.put(immutable.asLong(), ConnectionTimeUtils.getTimeTimeMillis() + 3000L);
                     this.hit = "forced refused";
                     return false;
                  } else {
                     boolean clicksTarget = this.canClicksTarget(data, posBlockData);
                     if (freshTaken && !canFlagForInt(BlockPosUtilsRespawn.getChargesRespawn(immutable), data.targetReadyIn()) && !clicksTarget) {
                        this.entityIdleTime = ClientThreadUtils.getTime();
                        this.hit = "hit";
                        return false;
                     } else if (!canFlag(posBlockData.targetDamage(), getCalibration(data.target()), 0.0F, true)
                        && !canFlag(data.getSelfPop(), posBlockData.targetDamage(), posBlockData.knock(), getCalibration(data.target()))) {
                        this.entityIdleTime = ClientThreadUtils.getTime();
                        this.handleForcedRefused(immutable);
                        this.hit = "weak";
                        return false;
                     } else {
                        this.posBlockData = posBlockData;
                        this.vetoedDoneFlag = true;
                        this.idleWeakTime = data.tick();
                        this.idleOwedValue = data.target().id();
                        this.killGoneFlag = true;
                        if (!this.canStartSneaking(data, posBlockData, clicksTarget)) {
                           this.entityIdleTime = ClientThreadUtils.getTime();
                           return false;
                        } else {
                           return true;
                        }
                     }
                  }
               }
            }
         }
      } else {
         return false;
      }
   }

   static boolean canFlag(int staleOwedValue, float routedPartialValue, double iframeComboValue) {
      return IframeComboUtils.canFlag(staleOwedValue, routedPartialValue, iframeComboValue);
   }

   private record Fire(boolean ok, float dealt, float cost, float full, boolean pops, boolean kills) {
      static final StaleOwed.Fire INSTANCE = new StaleOwed.Fire(false, 0.0F, 0.0F, 0.0F, false, false);
   }

   private record Owed(long since, long retryAt, int fails) {
   }

   private record Strike(int count, long until) {
   }
}
