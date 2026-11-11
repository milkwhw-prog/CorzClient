package com.corz.client.world;

import com.corz.client.entity.WorldCollisionHelper;
import com.corz.client.entity.ComboingIntersects;
import com.corz.client.entity.DeflectZone;
import com.corz.client.entity.VecBlock;
import com.corz.client.entity.VecMax;
import com.corz.client.inventory.LivingEntityHelperVisible;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.HeightDeltaMath;
import com.corz.client.utils.HuntEhpTables;
import com.corz.client.utils.FallDamageMath;
import com.corz.client.utils.PlayerCombatState;
import com.corz.client.utils.RoutedBreachingApi;
import com.corz.client.utils.AnchorRoutePlanner;
import com.corz.client.utils.AmountConstTime;
import com.corz.client.utils.CarefulBalancedType;
import com.corz.client.utils.CaseMaxData;
import com.corz.client.utils.DeflectAmountsUtils;
import com.corz.client.utils.IframeComboUtils;
import com.corz.client.utils.PearlsPlaystyleData;
import com.corz.client.utils.RefBareData;
import com.corz.client.utils.SurroundKitType;

public final class HuntDeflect {
   public static final String COOLDOWN = "cooldown";
   private long startTimeMs;
   private CenterVecData.PearlShot vecData;
   public static final String ZONE = "zone";
   public static final String LANDING = "landing";
   public static final String HUNT = "hunt";
   private final long[] timesVecNoshot;
   public static final String RESERVE = "reserve";
   private Vec3d centre;
   public static final String COUNTER = "counter";
   public static final String CFG = "cfg";
   public static final String SELFDMG = "selfdmg";
   public static final String NOSHOT = "noshot";
   private String whyCentreText;
   private final Map<String, HuntDeflect.Miss> noshotDeflectMap;
   public static final int SINGLE_STEP = 1;
   public static final String AGGRO = "aggro";
   public static final String LOW = "low";
   public static final String RUN = "run";
   private String vecNoshotText = "-";
   private final double[] vecNoshotTable;
   public static final String DEFLECT = "deflect";
   public static final String REACH = "reach";
   public static final String CLIMB = "climb";
   public static final String STUCK = "stuck";
   private BlockPos blockPos;
   public static final String DASH_TEXT = "-";

   public static String formatZoneDeflect(String zoneDeflectText) {
      if ("deflect".equals(zoneDeflectText)) {
         return "deflect";
      } else {
         return "zone".equals(zoneDeflectText) ? "zone" : "noshot";
      }
   }

   public static int getValue(int huntDeflectBound) {
      return Math.max(0, huntDeflectBound) + 10;
   }

   private static HuntDeflect.Trigger getLowAnchor(HuntDeflect.In in2) {
      ComboingIntersects.Ctx ctx = in2.c();
      Vec3d vec3d = in2.anchor() != null ? in2.anchor() : in2.feet();
      if (vec3d == null) {
         return null;
      } else {
         AbsorptionPosData self = ctx.self();
         double lowAnchorValue = WorldCollisionHelper.horizontalDistance(in2.us(), vec3d);
         int walkNoneBound = DeflectAmountsUtils.getAmounts(Math.max(0.0, lowAnchorValue - 1.3));
         VecBlock.Threat threat = ctx.s().threat();
         float absorptionHealth = self.getAbsorptionHealth();
         boolean lowAnchorFlag = threat != null && Float.isFinite(absorptionHealth) && threat.maxIncoming() >= 0.25 * absorptionHealth;
         return !canPearl(in2.us().y - vec3d.y, self.onGround(), lowAnchorValue, getRoutedDirect(ctx.nav()), walkNoneBound, lowAnchorFlag)
            ? null
            : new HuntDeflect.Trigger(vec3d, 16, lowAnchorValue, "low", true, -1);
      }
   }

   public static int getValue(double huntDeflectValue, int huntDeflectBound, boolean huntDeflectFlag) {
      int aggroPreemptBound = Math.max(1, huntDeflectBound) + 4;
      return !huntDeflectFlag && Double.isFinite(huntDeflectValue) ? (int)Math.max((double)aggroPreemptBound, Math.min(30.0, Math.floor(huntDeflectValue) - 1.0 - 1.0)) : Math.max(aggroPreemptBound, 30);
   }

   private long getTime(long huntDeflectLimit) {
      return this.startTimeMs != Long.MIN_VALUE && huntDeflectLimit >= this.startTimeMs ? huntDeflectLimit - this.startTimeMs : -1L;
   }

   private static HuntDeflect.Trigger getStuckAnchor(HuntDeflect.In in2) {
      if (!in2.stuck()) {
         return null;
      } else {
         Vec3d vec3d = in2.anchor() != null ? in2.anchor() : in2.feet();
         if (vec3d == null) {
            return null;
         } else {
            double stuckAnchorValue = WorldCollisionHelper.horizontalDistance(in2.us(), vec3d);
            return !canFlag(true, vec3d.y - in2.us().y, stuckAnchorValue) ? null : new HuntDeflect.Trigger(vec3d, 80, stuckAnchorValue, "stuck", true, -1);
         }
      }
   }

   public HuntDeflect() {
      this.noshotDeflectMap = new HashMap<>();
      this.startTimeMs = Long.MIN_VALUE;
      this.vecNoshotTable = new double[7];
      this.timesVecNoshot = getTimes();
   }

   public static boolean canHuntCounter(String huntCounterText, double huntCounterLimit) {
      if (huntCounterText != null && huntCounterLimit >= 8.0) {
         return switch (huntCounterText) {
            case "hunt", "counter", "reach", "climb", "stuck", "low", "landing" -> true;
            default -> false;
         };
      } else {
         return false;
      }
   }

   public static boolean canFlag(float huntDeflectValue, int hashHuntLimit, float routedPartialValue, boolean huntDeflectFlag, int huntDeflectBound) {
      return canAmounts(huntDeflectValue, hashHuntLimit, routedPartialValue, huntDeflectFlag, huntDeflectBound, 0);
   }

   public static int getValue(boolean huntDeflectFlag, double huntDeflectValue, int huntDeflectValue2) {
      int huntDeflectBound = Math.max(1, huntDeflectValue2 + 4);
      if (!huntDeflectFlag && Double.isFinite(huntDeflectValue)) {
         long unreachableBound = (long)Math.floor(huntDeflectValue) - 15L;
         return (int)Math.max((long)huntDeflectBound, Math.min(30L, unreachableBound));
      } else {
         return Math.max(huntDeflectBound, 30);
      }
   }

   private static HuntDeflect.Trigger getLandingTracker(HuntDeflect.In in2) {
      ComboingIntersects.Ctx ctx = in2.c();
      if (ctx.tracker() != null && ctx.tracker().isSampleGround()) {
         PosVecUtils.Path sampleTick = ctx.tracker().getSampleTick(40, ctx.world(), ctx.cal(), getTowardZero(in2), null);
         if (sampleTick == null) {
            return null;
         } else {
            int landingTrackerValue = Math.min(sampleTick.getValueGround(), sampleTick.getValue() - 1);
            if (landingTrackerValue < 3) {
               return null;
            } else {
               PosVecUtils.Sample sample = sampleTick.getDataForInt(landingTrackerValue);
               double vecHashValue = WorldCollisionHelper.getAmount(in2.us().x, in2.us().z, sample.x(), sample.z());
               if (HeightDeltaMath.isRiseWithinReach(in2.us().y, sample.y(), vecHashValue)) {
                  return null;
               } else {
                  return canFlag(vecHashValue, landingTrackerValue, sample.y() - in2.us().y)
                     ? null
                     : new HuntDeflect.Trigger(getVec3d(sample), Math.max(1, landingTrackerValue - 1), vecHashValue, "landing", false, landingTrackerValue);
               }
            }
         }
      } else {
         return null;
      }
   }

   private double getTimesAmounts(long huntDeflectValue) {
      int floorMod = Math.floorMod(huntDeflectValue, 7);
      int floorMod2 = Math.floorMod(huntDeflectValue - 6L, 7);
      return this.timesVecNoshot[floorMod] == huntDeflectValue && this.timesVecNoshot[floorMod2] == huntDeflectValue - 6L ? this.vecNoshotTable[floorMod] - this.vecNoshotTable[floorMod2] : Double.NaN;
   }

   private static double getRoutedPartial(RoutedBreachingApi routedBreachingApi) {
      AnchorRoutePlanner.Mode routedSearching = routedBreachingApi.getRouteMode();
      return routedSearching != AnchorRoutePlanner.Mode.ROUTED && routedSearching != AnchorRoutePlanner.Mode.PARTIAL ? Double.NaN : routedBreachingApi.getEstimatedTicksRemaining();
   }

   private static HuntDeflect.Trigger getAggroTracker(HuntDeflect.In in2) {
      ComboingIntersects.Ctx ctx = in2.c();
      double aggroTrackerLimit = in2.h();
      if (!(aggroTrackerLimit > 5.0)) {
         return null;
      } else {
         LivingEntityHelperVisible.Seed seed = ctx.tracker() == null ? null : ctx.tracker().getDataGroundMax();
         if (seed == null) {
            return null;
         } else {
            PosVecUtils.Sample sample = seed.sample();
            Vec3d towardZero = getTowardZero(in2);
            double aggroTrackerValue = IframeComboUtils.getAmount(sample.vx(), sample.vz(), towardZero.x, towardZero.z);
            PosVecUtils.Path sampleTick = ctx.tracker().getSampleTick(16, ctx.world(), ctx.cal(), towardZero, null);
            if (sampleTick == null) {
               return null;
            } else {
               PosVecUtils.Sample dataForInt = sampleTick.getDataForInt(6);
               double aggroTrackerValue2 = WorldCollisionHelper.getAmount(in2.us().x, in2.us().z, dataForInt.x(), dataForInt.z());
               if (!IframeComboUtils.canFlag(aggroTrackerLimit, aggroTrackerValue, aggroTrackerValue2)) {
                  return null;
               } else {
                  int amounts3 = DeflectAmountsUtils.getAmounts(Math.max(0.0, aggroTrackerLimit - 1.3));
                  PosVecUtils.Sample dataForInt3 = sampleTick.getDataForInt(amounts3 + 1);
                  int max4 = Math.max(1, (int)Math.ceil(IframeComboUtils.getAmount(aggroTrackerLimit)));
                  return new HuntDeflect.Trigger(getVec3d(dataForInt3), max4, aggroTrackerLimit, "aggro", false, -1);
               }
            }
         }
      }
   }

   public static boolean canDistance(Vec3d vec, Vec3d vec2, long distanceLimit, long distanceValue) {
      return vec != null && vec2 != null && distanceValue != Long.MIN_VALUE && distanceLimit >= distanceValue
         ? distanceLimit - distanceValue < 2L && vec.distanceTo(vec2) <= 1.2
         : false;
   }

   private static HuntDeflect.Trigger getReachOption(HuntDeflect.In in2) {
      ComboingIntersects.Ctx ctx = in2.c();
      Vec3d vec3d = in2.anchor() != null ? in2.anchor() : in2.feet();
      if (vec3d == null) {
         return null;
      } else {
         RoutedBreachingApi nav = ctx.nav();
         double reachOptionValue = WorldCollisionHelper.horizontalDistance(in2.us(), vec3d);
         int amounts2 = DeflectAmountsUtils.getAmounts(Math.max(0.0, reachOptionValue - 1.3));
         int huntDeflectLimit = nav == null ? 0 : nav.getTimeUnreachable();
         double huntDeflectValue = nav == null ? Double.NaN : getRoutedPartial(nav);
         double deltaY = vec3d.y - in2.us().y;
         int blockedStrikeTicks = in2.blockedStrikeTicks();
         if (!canFlag(huntDeflectLimit, blockedStrikeTicks, deltaY, in2.rangeOption(), huntDeflectValue, amounts2)) {
            return null;
         } else {
            boolean reachOptionFlag = canFlag(huntDeflectLimit, blockedStrikeTicks, deltaY, in2.rangeOption(), Double.NaN, amounts2);
            return new HuntDeflect.Trigger(vec3d, getValue(reachOptionFlag, huntDeflectValue, amounts2), reachOptionValue, "reach", true, -1);
         }
      }
   }

   private static Vec3d getVec3d(PosVecUtils.Sample sample) {
      return new Vec3d(sample.x(), sample.y(), sample.z());
   }

   public static boolean canFlag(double huntDeflectLimit, double huntDeflectValue, double groundY, int huntDeflectBound, int rocketsBuyBound, boolean huntDeflectFlag, int huntDeflectValue3) {
      if (!(huntDeflectLimit > 5.5) || huntDeflectLimit > 32.0 || Double.isNaN(huntDeflectValue)) {
         return false;
      } else if (huntDeflectFlag) {
         return true;
      } else if (!(huntDeflectValue < 0.15)) {
         return false;
      } else if (huntDeflectValue < -0.05) {
         return true;
      } else {
         return !Double.isFinite(groundY) ? true : groundY - (Math.max(1, huntDeflectBound) + Math.max(0, rocketsBuyBound) + 1) >= huntDeflectValue3;
      }
   }

   public HuntDeflect.Call getWhyCentre(HuntDeflect.In in2) {
      this.vecNoshotText = "-";
      if (in2 != null && in2.c() != null && in2.us() != null) {
         ComboingIntersects.Ctx ctx = in2.c();
         if (ctx.s() != null && ctx.self() != null && ctx.target() != null) {
            HuntDeflect.Trigger counterLanding = getCounterLanding(in2);
            if (counterLanding == null) {
               counterLanding = this.getVecLanding(in2);
            }

            if (counterLanding == null) {
               counterLanding = getLowAnchor(in2);
            }

            if (counterLanding == null) {
               counterLanding = getClimbAnchor(in2);
            }

            if (counterLanding == null) {
               counterLanding = getStuckAnchor(in2);
            }

            if (counterLanding == null) {
               counterLanding = getLandingTracker(in2);
            }

            if (counterLanding == null) {
               counterLanding = getReachOption(in2);
            }

            if (counterLanding == null) {
               counterLanding = getLowGround(in2);
            }

            PearlsPlaystyleData cfg = ctx.s().cfg();
            CarefulBalancedType carefulBalancedType = cfg == null ? null : (cfg.playstyle() == null ? CarefulBalancedType.TERMINATOR : cfg.playstyle());
            boolean whyCentreFlag = carefulBalancedType == CarefulBalancedType.TERMINATOR && canFlag(this.getTime(ctx.tick()), this.getTimesAmounts(ctx.tick()));
            int whyCentreCounter = counterLanding != null ? 0 : 1;
            if (counterLanding == null) {
               counterLanding = getHuntTracker(in2, whyCentreFlag);
            }

            if (counterLanding == null) {
               counterLanding = getAggroTracker(in2);
            }

            if (counterLanding == null) {
               return null;
            } else {
               if (whyCentreCounter == 1 && !"hunt".equals(counterLanding.why())) {
                  whyCentreCounter = 2;
               }

               AbsorptionPosData self = ctx.self();
               if (cfg != null && cfg.aggroPearls() && ctx.pearls() != null) {
                  boolean whyCentreFlag2 = ctx.s().getSelfPop().ref() && ctx.s().enemiesWithin8() < 2;
                  if (self.pearlReady() && canPearlsTotems(cfg.escapePearls(), self, whyCentreFlag2)) {
                     CaseMaxData options = ctx.s().getOptions(SurroundKitType.PEARL);
                     if (!options.available() && "cooldown".equals(options.why())) {
                        return this.getText("cooldown");
                     } else {
                        RoutedBreachingApi nav = ctx.nav();
                        PlayerCombatState immuneFireData = nav == null ? null : nav.getImmuneFire();
                        if (immuneFireData != null
                           && immuneFireData != PlayerCombatState.INSTANCE
                           && !canAmounts(
                              immuneFireData.fallEpf(),
                              immuneFireData.resistanceAmp(),
                              self.getAbsorptionHealth(),
                              self.totemConfirmed() && self.totems() >= 1,
                              immuneFireData.style(),
                              self.totemConfirmed() ? self.totems() : 0
                           )
                           && !canDeepRef(immuneFireData.fallEpf(), immuneFireData.resistanceAmp(), self.getAbsorptionHealth(), ctx.s().getSelfPop())) {
                           return this.getText("selfdmg");
                        } else {
                           long tick = ctx.tick();
                           DeflectZone pearls = ctx.pearls();
                           handleIntervalThreat(ctx, pearls, tick);

                           try {
                              String whyCentreText = null;

                              while (counterLanding != null) {
                                 CenterVecData.PearlShot vecData = null;
                                 BlockPos blockPos = null;
                                 String moneyEnoughText2 = null;
                                 if (this.vecData != null
                                    && counterLanding.why().equals(this.whyCentreText)
                                    && canDistanceForVec3dLong(this.centre, counterLanding.centre(), tick, this.vecData.validUntil())
                                    && !pearls.canInstanceWorld(this.vecData)) {
                                    vecData = this.vecData;
                                    blockPos = this.blockPos;
                                 } else {
                                    HuntDeflect.Miss noshotdeflectmap = this.noshotDeflectMap.get(counterLanding.why());
                                    String moneyEnoughText = noshotdeflectmap == null ? "noshot" : noshotdeflectmap.deny();
                                    if (noshotdeflectmap != null && canDistance(noshotdeflectmap.centre(), counterLanding.centre(), tick, noshotdeflectmap.tick())) {
                                       DeflectZone.ComboShot comboShot = "deflect".equals(moneyEnoughText) ? pearls.getShotInstance(tick, counterLanding.why()) : null;
                                       if (comboShot == null) {
                                          moneyEnoughText2 = moneyEnoughText;
                                       } else {
                                          vecData = comboShot.shot();
                                          blockPos = comboShot.base();
                                       }
                                    } else {
                                       pearls.setVec3d(in2.feet());
                                       pearls.handle(counterLanding.why());
                                       DeflectZone.Spec totemsConfirmed = getTotemsConfirmed(counterLanding.why(), self);
                                       Vec3d add = counterLanding.centre().add(0.0, 1.62, 0.0);
                                       Vec3d aggroHunt = getAggroHunt(counterLanding.why(), counterLanding.centre(), in2);
                                       if (!"counter".equals(counterLanding.why()) && in2.combo() != null) {
                                          DeflectZone.ComboShot posBlockData = pearls.getPosBlockDataForVec3dInt(
                                             counterLanding.centre(),
                                             aggroHunt,
                                             counterLanding.arriveBy(),
                                             tick,
                                             ctx.world(),
                                             ctx.threat(),
                                             ctx.enemies(),
                                             self,
                                             1.2,
                                             counterLanding.why(),
                                             totemsConfirmed,
                                             add,
                                             in2.combo()
                                          );
                                          if (posBlockData != null) {
                                             vecData = posBlockData.shot();
                                             blockPos = posBlockData.base();
                                          }
                                       }

                                       if (vecData == null) {
                                          vecData = pearls.getShot(
                                             counterLanding.centre(),
                                             aggroHunt,
                                             counterLanding.arriveBy(),
                                             tick,
                                             ctx.world(),
                                             ctx.threat(),
                                             ctx.enemies(),
                                             self,
                                             1.2,
                                             counterLanding.why(),
                                             totemsConfirmed,
                                             add
                                          );
                                       }

                                       long whyCentreValue = noshotdeflectmap == null ? Long.MIN_VALUE : noshotdeflectmap.blind();
                                       if (vecData == null && canHuntCounter(counterLanding.why(), counterLanding.h()) && canFlag(tick, whyCentreValue)) {
                                          vecData = pearls.getShot(
                                             counterLanding.centre(),
                                             aggroHunt,
                                             counterLanding.arriveBy(),
                                             tick,
                                             ctx.world(),
                                             ctx.threat(),
                                             ctx.enemies(),
                                             self,
                                             1.2,
                                             counterLanding.why(),
                                             totemsConfirmed,
                                             null
                                          );
                                          whyCentreValue = vecData == null ? tick : Long.MIN_VALUE;
                                       }

                                       if (vecData == null) {
                                          String formatZoneDeflect2 = formatZoneDeflect(pearls.formatDeflectZone());
                                          this.noshotDeflectMap.put(counterLanding.why(), new HuntDeflect.Miss(tick, counterLanding.centre(), formatZoneDeflect2, whyCentreValue));
                                          moneyEnoughText2 = formatZoneDeflect2;
                                       }
                                    }

                                    if (vecData != null) {
                                       this.noshotDeflectMap.remove(counterLanding.why());
                                       this.vecData = vecData;
                                       this.blockPos = blockPos;
                                       this.whyCentreText = counterLanding.why();
                                       this.centre = counterLanding.centre();
                                    }
                                 }

                                 if (vecData != null) {
                                    int valueForDouble = IframeComboUtils.getValueForDouble(BoxLengthUtils.getAmount(in2.yaw(), in2.pitch(), vecData.yaw(), vecData.pitch()));
                                    if (counterLanding.always() || ctx.s().getSelfPop().ref() || IframeComboUtils.canFlag(vecData.flightTicks(), valueForDouble, counterLanding.h())) {
                                       return new HuntDeflect.Call(vecData, counterLanding.why(), counterLanding.centre(), counterLanding.landIndex(), blockPos);
                                    }

                                    moneyEnoughText2 = "run";
                                 }

                                 if (whyCentreText == null) {
                                    whyCentreText = moneyEnoughText2;
                                 }

                                 HuntDeflect.Trigger trigger = null;

                                 while (trigger == null && whyCentreCounter < 2) {
                                    trigger = whyCentreCounter++ == 0 ? getHuntTracker(in2, whyCentreFlag) : getAggroTracker(in2);
                                 }

                                 counterLanding = trigger;
                                 if (trigger != null) {
                                    handleIntervalThreat(ctx, pearls, tick);
                                 }
                              }

                              return this.getText(whyCentreText);
                           } finally {
                              pearls.handleDeflectZone(null, 0, 0.0);
                           }
                        }
                     }
                  } else {
                     return this.getText("reserve");
                  }
               } else {
                  return this.getText("cfg");
               }
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   private static void handleIntervalThreat(ComboingIntersects.Ctx ctx, DeflectZone deflectZone, long intervalThreatValue) {
      VecBlock threat = ctx.threat();
      AmountConstTime amountConstTime = threat == null ? null : threat.getApply(ctx.target().id());
      AmountConstTime.Spot spot = amountConstTime == null ? null : amountConstTime.getTimeAmount(intervalThreatValue);
      if (spot == null) {
         deflectZone.handleDeflectZone(null, 0, 0.0);
      } else {
         deflectZone.handleDeflectZone(spot, amountConstTime.getMin(intervalThreatValue) - 2, spot.interval());
      }
   }

   private static HuntDeflect.Trigger getClimbAnchor(HuntDeflect.In in2) {
      ComboingIntersects.Ctx ctx = in2.c();
      RoutedBreachingApi nav = ctx.nav();
      if (nav == null) {
         return null;
      } else {
         Vec3d vec3d = in2.anchor() != null ? in2.anchor() : in2.feet();
         if (vec3d == null) {
            return null;
         } else {
            double climbAnchorValue = WorldCollisionHelper.horizontalDistance(in2.us(), vec3d);
            return !canFlag(nav.getTimeUnreachable(), vec3d.y - in2.us().y, climbAnchorValue) ? null : new HuntDeflect.Trigger(vec3d, 80, climbAnchorValue, "climb", true, -1);
         }
      }
   }

   public static boolean canDistanceForVec3dLong(Vec3d vec, Vec3d vec2, long distanceValue, long distanceLimit) {
      return vec != null && vec2 != null && distanceValue <= distanceLimit ? vec.distanceTo(vec2) <= 1.2 : false;
   }

   private static Vec3d getAggroHunt(String aggroHuntText, Vec3d vec, HuntDeflect.In in2) {
      if (!"hunt".equals(aggroHuntText) && !"aggro".equals(aggroHuntText)) {
         return in2.us();
      } else {
         ComboingIntersects.Ctx ctx = in2.c();
         LivingEntityHelperVisible.Seed seed = ctx.tracker() == null ? null : ctx.tracker().getDataGroundMax();
         if (seed == null) {
            return in2.us();
         } else {
            PosVecUtils.Sample sample = seed.sample();
            Vec3d towardZero = getTowardZero(in2);
            return getVec3d(vec, in2.us(), sample.vx(), sample.vz(), IframeComboUtils.getAmount(sample.vx(), sample.vz(), towardZero.x, towardZero.z));
         }
      }
   }

   public static boolean canFlag(boolean huntDeflectFlag, double huntDeflectLimit, double huntDeflectValue, int estimateFallTicks, int huntDeflectBound) {
      return !huntDeflectFlag && huntDeflectLimit >= 1.0 && huntDeflectValue <= 10.0 ? estimateFallTicks >= Math.max(1, huntDeflectBound) + 1 + 1 + 2 : false;
   }

   public static int getValueForInt(int huntDeflectBound) {
      return Math.max(1, huntDeflectBound) + 2;
   }

   private static HuntDeflect.Trigger getLowGround(HuntDeflect.In in2) {
      ComboingIntersects.Ctx ctx = in2.c();
      Vec3d vec3d = in2.anchor() != null ? in2.anchor() : in2.feet();
      if (vec3d == null) {
         return null;
      } else {
         AbsorptionPosData self = ctx.self();
         double lowGroundValue = WorldCollisionHelper.horizontalDistance(in2.us(), vec3d);
         int huntDeflectBound = DeflectAmountsUtils.getAmounts(Math.max(0.0, lowGroundValue - 1.3));
         double deltaY = in2.us().y - vec3d.y;
         Vec3d velocity = self.velocity();
         int estimateFallTicks = HeightDeltaMath.estimateFallTicks(self.onGround(), velocity == null ? 0.0 : velocity.y, deltaY - 0.75);
         return !canFlag(self.onGround(), deltaY, lowGroundValue, estimateFallTicks, huntDeflectBound)
            ? null
            : new HuntDeflect.Trigger(vec3d, Math.max(1, Math.min(16, estimateFallTicks - 1)), lowGroundValue, "low", true, -1);
      }
   }

   public static boolean canPearl(double pearlLimit, boolean pearlFlag, double pearlValue, double walkNoneLimit, int walkNoneBound, boolean lowAnchorFlag) {
      return pearlFlag && pearlLimit >= 0.75 && pearlValue <= 10.0
         ? HeightDeltaMath.chooseApproachMethod(walkNoneLimit, walkNoneBound, 1, true, lowAnchorFlag, true) == HeightDeltaMath.Choice.PEARL
         : false;
   }

   private static long[] getTimes() {
      long[] longs = new long[7];
      Arrays.fill(longs, Long.MIN_VALUE);
      return longs;
   }

   public static int getValue(boolean huntDeflectFlag, int huntDeflectValue, float huntDeflectLimit) {
      return huntDeflectFlag && huntDeflectValue <= 0 && !(huntDeflectLimit > 12.0F) ? 1 : 0;
   }

   public void handleTime(long huntDeflectValue) {
      this.startTimeMs = Long.MIN_VALUE;
   }

   public static boolean canFlag(int huntDeflectLimit, double huntDeflectLimit2, double huntDeflectValue) {
      return huntDeflectLimit >= 20 && huntDeflectLimit2 >= 3.0 && huntDeflectValue <= 16.0;
   }

   private static HuntDeflect.Trigger getCounterLanding(HuntDeflect.In in2) {
      ComboingIntersects.Ctx ctx = in2.c();
      if (ctx.watch() == null) {
         return null;
      } else {
         VecMax.Forecast vecMax = ctx.watch().getVecMaxForInt(ctx.target().id());
         if (vecMax == null) {
            return getCounterLandingForIn(in2);
         } else if (vecMax.landing() == null) {
            return null;
         } else {
            double counterLandingLimit = WorldCollisionHelper.horizontalDistance(in2.us(), vecMax.landing());
            if (!(counterLandingLimit > 5.0)) {
               return null;
            } else {
               long counterLandingValue = vecMax.seenTick() == Long.MIN_VALUE ? 0L : Math.max(0L, ctx.tick() - vecMax.seenTick());
               int huntDeflectBound = (int)Math.max(0L, vecMax.eta() - counterLandingValue);
               return new HuntDeflect.Trigger(vecMax.landing(), getValue(huntDeflectBound), counterLandingLimit, "counter", true, huntDeflectBound);
            }
         }
      }
   }

   public static boolean canFlag(boolean huntDeflectFlag, double huntDeflectValue, double stuckAnchorValue) {
      return huntDeflectFlag && Double.isFinite(huntDeflectValue) && Double.isFinite(stuckAnchorValue) && stuckAnchorValue <= 16.0 && !IframeComboUtils.canFlag2(huntDeflectValue, stuckAnchorValue);
   }

   public static boolean canFlag(int huntDeflectLimit, int blockedStrikeTicks, double huntDeflectValue, boolean huntDeflectFlag, double huntDeflectValue2, int huntDeflectValue3) {
      if (huntDeflectLimit >= 6) {
         return true;
      } else {
         return blockedStrikeTicks >= 6 && huntDeflectValue <= -1.5 && !huntDeflectFlag ? true : Double.isFinite(huntDeflectValue2) && huntDeflectValue2 - huntDeflectValue3 >= 15.0;
      }
   }

   private HuntDeflect.Trigger getVecLanding(HuntDeflect.In in2) {
      if (this.vecData != null && this.whyCentreText != null && this.centre != null && this.vecData.landing() != null) {
         ComboingIntersects.Ctx ctx = in2.c();
         if (ctx.s().committed() == SurroundKitType.PEARL && ctx.tick() <= this.vecData.validUntil()) {
            Vec3d vec3d = in2.anchor() != null ? in2.anchor() : in2.feet();
            if (vec3d == null || !(in2.h() > 4.0)) {
               return null;
            } else {
               return WorldCollisionHelper.horizontalDistance(vec3d, this.vecData.landing()) > 4.5
                  ? null
                  : new HuntDeflect.Trigger(this.centre, Math.max(1, this.vecData.flightTicks()), in2.h(), this.whyCentreText, true, -1);
            }
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static boolean canFlag(double huntDeflectValue, int huntDeflectBound, double vecHashValue) {
      return !Double.isNaN(huntDeflectValue) && !Double.isNaN(vecHashValue) ? !(huntDeflectValue - 0.28 * Math.max(0, huntDeflectBound) > 3.5) && !(Math.abs(vecHashValue) > 1.0) : true;
   }

   public static boolean canAmounts(float huntDeflectValue, int hashHuntLimit, float routedPartialValue, boolean huntDeflectFlag, int huntDeflectBound, int huntDeflectLimit) {
      if (!Float.isFinite(routedPartialValue)) {
         return false;
      } else {
         float[] damageMarginByLevel = HuntEhpTables.DAMAGE_MARGIN_BY_LEVEL;
         int index = Math.max(0, Math.min(damageMarginByLevel.length - 1, huntDeflectBound));
         float huntDeflectValue4 = huntDeflectFlag && huntDeflectLimit >= 2 ? 1.5F : (huntDeflectFlag ? 4.0F : 8.0F);
         float min5 = Math.min(damageMarginByLevel[index], routedPartialValue - huntDeflectValue4);
         return FallDamageMath.computePearlDamage(huntDeflectValue, hashHuntLimit) <= min5 + 0.001F;
      }
   }

   private HuntDeflect.Call getText(String text) {
      this.vecNoshotText = text;
      return null;
   }

   public void handleTimeTimes(long time, double huntDeflectLimit, double huntDeflectValue) {
      if (!(huntDeflectLimit > 5.5) || !(huntDeflectValue < 0.15)) {
         this.startTimeMs = Long.MIN_VALUE;
      } else if (this.startTimeMs == Long.MIN_VALUE) {
         this.startTimeMs = time;
      }

      int floorMod = Math.floorMod(time, 7);
      this.vecNoshotTable[floorMod] = huntDeflectLimit;
      this.timesVecNoshot[floorMod] = time;
   }

   public static boolean canPearlsTotems(boolean pearlsTotemsFlag, AbsorptionPosData absorptionPosData, boolean pearlsTotemsFlag2) {
      if (absorptionPosData == null) {
         return true;
      } else {
         int pearlsTotemsLimit = pearlsTotemsFlag2 ? 0 : getValue(pearlsTotemsFlag, absorptionPosData.totems(), absorptionPosData.getAbsorptionHealth());
         return absorptionPosData.pearls() <= pearlsTotemsLimit ? false : absorptionPosData.getAbsorptionHealth() > 6.0 || absorptionPosData.totemConfirmed();
      }
   }

   private static Vec3d getTowardZero(HuntDeflect.In in2) {
      return in2.towardUs() == null ? Vec3d.ZERO : in2.towardUs();
   }

   public static int getTerminator(CarefulBalancedType carefulBalancedType) {
      return carefulBalancedType != null && carefulBalancedType != CarefulBalancedType.TERMINATOR ? 8 : 2;
   }

   private static HuntDeflect.Trigger getHuntTracker(HuntDeflect.In in2, boolean huntTrackerFlag) {
      ComboingIntersects.Ctx ctx = in2.c();
      double huntTrackerLimit = in2.h();
      boolean ref = ctx.s().getSelfPop().ref();
      if (huntTrackerLimit > (ref ? 4.5 : 5.5) && ctx.tracker() != null) {
         LivingEntityHelperVisible.Seed seed = ctx.tracker().getDataGroundMax();
         if (seed == null) {
            return null;
         } else {
            PosVecUtils.Sample sample = seed.sample();
            Vec3d towardZero = getTowardZero(in2);
            double huntTrackerValue = IframeComboUtils.getAmount(sample.vx(), sample.vz(), towardZero.x, towardZero.z);
            RoutedBreachingApi nav = ctx.nav();
            boolean huntTrackerFlag2 = nav != null && (nav.getRouteMode() == AnchorRoutePlanner.Mode.PARTIAL || nav.getTimeUnreachable() >= 2);
            double routedDirect = getRoutedDirect(nav);
            double groundY = Double.isFinite(routedDirect) ? routedDirect : IframeComboUtils.getAmount(huntTrackerLimit);
            int amounts3 = DeflectAmountsUtils.getAmounts(Math.max(0.0, huntTrackerLimit - 1.3));
            PearlsPlaystyleData cfg = ctx.s().cfg();
            int huntTrackerValue4 = huntTrackerFlag ? Integer.MIN_VALUE : getTerminator(cfg == null ? null : cfg.playstyle());
            boolean huntTrackerFlag3 = ref
               ? !in2.rangeOption() && huntTrackerLimit <= 32.0 && !Double.isNaN(huntTrackerValue) && (huntTrackerLimit > 5.5 || huntTrackerFlag2 || huntTrackerValue < 0.15)
               : canFlag(huntTrackerLimit, huntTrackerValue, groundY, amounts3, 1, huntTrackerFlag2, huntTrackerValue4);
            if (!huntTrackerFlag3) {
               return null;
            } else {
               PosVecUtils.Path sampleTick = ctx.tracker().getSampleTick(40, ctx.world(), ctx.cal(), towardZero, null);
               if (sampleTick == null) {
                  return null;
               } else {
                  int huntTrackerBound = sampleTick.getValue() - 1;
                  PosVecUtils.Sample dataForInt = sampleTick.getDataForInt(Math.min(getValueForInt(amounts3), huntTrackerBound));
                  double huntTrackerValue5 = WorldCollisionHelper.getAmount(in2.us().x, in2.us().z, dataForInt.x(), dataForInt.z());
                  int huntDeflectBound = DeflectAmountsUtils.getAmounts(Math.max(0.0, huntTrackerValue5 - 1.3));
                  if (huntDeflectBound != amounts3) {
                     dataForInt = sampleTick.getDataForInt(Math.min(getValueForInt(huntDeflectBound), huntTrackerBound));
                  }

                  boolean huntDeflectFlag = huntTrackerValue < -0.05 || huntTrackerFlag || ref;
                  return new HuntDeflect.Trigger(getVec3d(dataForInt), getValue(huntTrackerFlag2 ? Double.NaN : groundY, huntDeflectBound, huntDeflectFlag), huntTrackerLimit, "hunt", true, -1);
               }
            }
         }
      } else {
         return null;
      }
   }

   public static boolean canFlag(long huntDeflectLimit, double huntDeflectLimit2) {
      return huntDeflectLimit >= 8L || huntDeflectLimit2 >= 0.6;
   }

   private static DeflectZone.Spec getTotemsConfirmed(String totemsConfirmedText, AbsorptionPosData absorptionPosData) {
      return switch (totemsConfirmedText) {
         case "landing" -> DeflectZone.Spec.getAmountsForDouble(IframeComboUtils.getAmount(absorptionPosData.totemConfirmed(), absorptionPosData.totems()));
         case "reach" -> DeflectZone.Spec.getAmounts(IframeComboUtils.getAmount(absorptionPosData.totemConfirmed(), absorptionPosData.totems()));
         case "low" -> DeflectZone.Spec.getAmounts4(IframeComboUtils.getAmount(absorptionPosData.totemConfirmed(), absorptionPosData.totems()));
         case "climb" -> DeflectZone.Spec.getAmounts6(IframeComboUtils.getAmount(absorptionPosData.totemConfirmed(), absorptionPosData.totems()));
         case "stuck" -> DeflectZone.Spec.getAmounts6(IframeComboUtils.getAmount(absorptionPosData.totemConfirmed(), absorptionPosData.totems()));
         case "hunt" -> DeflectZone.Spec.getAmountsSpec(IframeComboUtils.getAmount(absorptionPosData.totemConfirmed(), absorptionPosData.totems()));
         default -> DeflectZone.Spec.getAmounts5(IframeComboUtils.getAmount(absorptionPosData.totemConfirmed(), absorptionPosData.totems()));
      };
   }

   public String getVecNoshotText() {
      return this.vecNoshotText;
   }

   public void updateVecNoshot() {
      this.vecNoshotText = "-";
      this.vecData = null;
      this.blockPos = null;
      this.whyCentreText = null;
      this.centre = null;
      this.noshotDeflectMap.clear();
      this.startTimeMs = Long.MIN_VALUE;
      Arrays.fill(this.vecNoshotTable, 0.0);
      Arrays.fill(this.timesVecNoshot, Long.MIN_VALUE);
   }

   public static boolean canFlag(long huntDeflectValue, long huntDeflectLimit) {
      return huntDeflectLimit == Long.MIN_VALUE || huntDeflectValue < huntDeflectLimit || huntDeflectValue - huntDeflectLimit >= 6L;
   }

   public static boolean canDeepRef(float deepRefValue, int hashHuntLimit, float full, RefBareData refBareData) {
      if (refBareData != null && refBareData.on() && Float.isFinite(full)) {
         float pearlDamage = FallDamageMath.computePearlDamage(deepRefValue, hashHuntLimit);
         if (refBareData.ref()) {
            return refBareData.canBare(pearlDamage, full, false);
         } else {
            return RefBareData.canFlag(pearlDamage, full) ? false : pearlDamage <= full - (refBareData.deep() ? 2.0F : 4.0F) + 0.001F;
         }
      } else {
         return false;
      }
   }

   public static double getRoutedDirect(RoutedBreachingApi routedBreachingApi) {
      if (routedBreachingApi == null) {
         return Double.NaN;
      } else {
         AnchorRoutePlanner.Mode routedSearching = routedBreachingApi.getRouteMode();
         return routedSearching != AnchorRoutePlanner.Mode.DIRECT && routedSearching != AnchorRoutePlanner.Mode.ROUTED && routedSearching != AnchorRoutePlanner.Mode.PARTIAL ? Double.NaN : routedBreachingApi.getEstimatedTicksRemaining();
      }
   }

   public static Vec3d getVec3d(Vec3d vec, Vec3d vec3d, double vec3dFactor, double vec3dFactor2, double vec3dValue) {
      if (vec != null && !Double.isNaN(vec3dValue) && vec3dValue < -0.05) {
         double distance = Math.sqrt(vec3dFactor * vec3dFactor + vec3dFactor2 * vec3dFactor2);
         return !(distance > 0.1) ? vec3d : new Vec3d(vec.x + vec3dFactor / distance * 4.0, vec.y, vec.z + vec3dFactor2 / distance * 4.0);
      } else {
         return vec3d;
      }
   }

   private static HuntDeflect.Trigger getCounterLandingForIn(HuntDeflect.In in2) {
      ComboingIntersects.Ctx ctx = in2.c();
      VecMax.Forecast owner = ctx.watch().getOwner(ctx.target().id(), ctx.tick(), 20);
      Vec3d vec3d = in2.anchor() != null ? in2.anchor() : in2.feet();
      if (owner != null && owner.landing() != null && vec3d != null) {
         if (WorldCollisionHelper.horizontalDistance(vec3d, owner.landing()) > 4.0) {
            return null;
         } else {
            double counterLandingLimit = WorldCollisionHelper.horizontalDistance(in2.us(), vec3d);
            return !(counterLandingLimit > 5.0) ? null : new HuntDeflect.Trigger(vec3d, 24, counterLandingLimit, "counter", true, -1);
         }
      } else {
         return null;
      }
   }

   public record Call(CenterVecData.PearlShot shot, String why, Vec3d landing, int landIndex, BlockPos comboBase) {
   }

   public interface ComboGoal {
      BlockPos base(Vec3d vec, Vec3d vec2);
   }

   public record In(
      ComboingIntersects.Ctx c,
      Vec3d us,
      Vec3d eye,
      Vec3d feet,
      Vec3d anchor,
      double h,
      Vec3d towardUs,
      float yaw,
      float pitch,
      int blockedStrikeTicks,
      boolean rangeOption,
      HuntDeflect.ComboGoal combo,
      boolean stuck
   ) {
      public In(
         ComboingIntersects.Ctx h,
         Vec3d yaw,
         Vec3d combo,
         Vec3d rangeOption,
         Vec3d towardUs,
         double pitch,
         Vec3d c,
         float anchor,
         float blockedStrikeTicks,
         int eye,
         boolean us,
         HuntDeflect.ComboGoal feet
      ) {
         this(h, yaw, combo, rangeOption, towardUs, pitch, c, anchor, blockedStrikeTicks, eye, us, feet, false);
      }
   }

   private record Miss(long tick, Vec3d centre, String deny, long blind) {
   }

   private record Trigger(Vec3d centre, int arriveBy, double h, String why, boolean always, int landIndex) {
   }
}
