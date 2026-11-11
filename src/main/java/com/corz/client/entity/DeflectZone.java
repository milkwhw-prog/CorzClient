package com.corz.client.entity;

import com.corz.client.entity.FloorMathUtilsDirection.Result;
import com.corz.client.utils.AmountConstTime.Spot;
import com.corz.client.utils.ZoneEscapeUtils2.Miss;
import com.corz.client.utils.ZoneEscapeUtilsFlag.Clear;
import com.corz.client.world.AbsorptionPosData;
import com.corz.client.world.CenterVecData.PearlShot;
import com.corz.client.world.HuntDeflect.ComboGoal;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;
import net.minecraft.world.World;
import com.corz.client.utils.HeightDeltaMath;
import com.corz.client.utils.BlockHazardFlags;
import com.corz.client.utils.AmountConstTime;
import com.corz.client.utils.DeflectAmountsUtils;
import com.corz.client.utils.VecShieldUtilsAmounts;
import com.corz.client.utils.ZoneEscapeUtils2;
import com.corz.client.utils.ZoneEscapeUtilsFlag;

public final class DeflectZone {
   public static final double[] deflectZoneTable = new double[]{2.4, 2.8};
   private double zoneIntervalTicks;
   public static final int[] table = new int[]{0, -1, 1, -2};
   private final List<DeflectZone.Held> deflectFlightHeldList = new ArrayList<>();
   public static final String DEFLECT = "deflect";
   private Spot deflectZoneSpot;
   private int remainingAttempts;
   public static final double[] RADIUS_STEPS_SHORT = new double[]{1.0, 1.3};
   public static final double[] RADIUS_STEPS_LONG = new double[]{1.0, 1.3, 1.8, 2.4};
   public static final int[] DROP_OFFSETS_DEFAULT = new int[]{0, -1, -2};
   public static final int[] deflectFlightTable = new int[]{1};
   private int deflectZoneValue;
   public static final int[] DROP_OFFSETS_WIDE = new int[]{-1, -2, 0, -3};
   public static final int[] DROP_OFFSETS_LOW_FIRST = new int[]{-1, 0, -2};
   private int deflectZoneCount;
   public static final double[] RADIUS_STEPS_ALT = new double[]{1.0, 1.3};
   public static final String ZONE = "zone";
   private Vec3d vec3dDeflectFlight;
   private int deflect;
   public static final int[] DROP_OFFSETS_ALT = new int[]{-1, 0, -2};

   static double[] getAmounts(double deflectZoneValue, double deflectZoneValue2, double deflectZoneValue3, double deflectZoneValue4, double deflectZoneLimit) {
      return !canFlag(deflectZoneValue4, deflectZoneLimit) ? new double[]{deflectZoneValue} : new double[]{deflectZoneValue, getAmount(deflectZoneValue2, deflectZoneValue3, deflectZoneValue4, deflectZoneLimit)};
   }

   static boolean canStuckClimb(String stuckClimbText) {
      return !"low".equals(stuckClimbText) && !"climb".equals(stuckClimbText) && !"stuck".equals(stuckClimbText);
   }

   public PearlShot getInstance(
      Vec3d vec,
      Vec3d vec2,
      int deflectZoneValue,
      long deflectFlightValue,
      WorldCollisionHelper blockPosHelperSolid,
      VecBlock vecBlock,
      List<VecBlockingData> list,
      AbsorptionPosData absorptionPosData,
      double vecDistanceValue,
      String deflectZoneText
   ) {
      return this.getShot(vec, vec2, deflectZoneValue, deflectFlightValue, blockPosHelperSolid, vecBlock, list, absorptionPosData, vecDistanceValue, deflectZoneText, DeflectZone.Spec.INSTANCE, null);
   }

   public void setVec3d(Vec3d vec3d) {
      this.vec3dDeflectFlight = vec3d;
   }

   static Box getBox(Vec3d vec) {
      return new Box(vec.x - 0.3, vec.y, vec.z - 0.3, vec.x + 0.3, vec.y + 1.8, vec.z + 0.3);
   }

   static boolean canFlag(double deflectZoneValue, double vec3d2, double vec) {
      return canFlag(deflectZoneValue, vec3d2, vec, vec);
   }

   private static BlockPos getFloored(World world, Vec3d vec, int[] flooredValues) {
      BlockPos floored = BlockPos.ofFloored(vec.x, vec.y + 0.01, vec.z);

      for (int flooredValue : flooredValues) {
         BlockPos flooredAbove = floored.up(flooredValue);
         if (BlockHazardFlags.isSafeToStand(world, flooredAbove)) {
            return flooredAbove;
         }
      }

      return null;
   }

   public DeflectZone.ComboShot getShotInstance(long shotValue, String shotText) {
      if (this.deflectFlightHeldList.isEmpty()) {
         return null;
      } else {
         double[] shotValues = this.deflectZoneSpot == null ? null : getInstancePlayer();
         ClientWorld world = MinecraftClient.getInstance().world;
         this.remainingAttempts = 16;

         for (DeflectZone.Held held : this.deflectFlightHeldList) {
            PearlShot shot = held.shot();
            if (shotValue <= shot.validUntil()
               && (shotText == null || shotText.equals(shot.why()))
               && (this.deflectZoneSpot == null || shotValues != null && !ZoneEscapeUtilsFlag.canFlag(this.getLandingFlight(shot, shotValues, world)))) {
               return new DeflectZone.ComboShot(shot, held.base());
            }
         }

         return null;
      }
   }

   public PearlShot getInstanceWorld(Vec3d vec, int worldValue, double vecDistanceValue, long hunterLandingValue, String worldText, double worldValue4, double groundY) {
      double worldLimit = Double.isFinite(worldValue4) ? worldValue4 : -38.0;
      double worldValue6 = Double.isFinite(groundY) ? groundY : 30.0;
      if (worldLimit > worldValue6) {
         double worldLimit7 = worldLimit;
         worldLimit = worldValue6;
         worldValue6 = worldLimit7;
      }

      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (player != null && vec != null && MinecraftClient.getInstance().world != null) {
         Vec3d eye = FloorMathUtilsDirection.getEye(player);
         float long2floatopenhashmap = FloorMathUtilsDirection.getAmount(eye, vec, 1.5, 0.03);
         return getVecDistance(eye, getGround(player), vec, worldValue, vecDistanceValue, hunterLandingValue, worldText, worldLimit, worldValue6, long2floatopenhashmap);
      } else {
         return null;
      }
   }

   static boolean canIntersects(Box box, Box box2, Box box3) {
      return box == null ? true : box2 != null && box.intersects(box2) || box3 != null && box.intersects(box3);
   }

   private static double[] getGround(ClientPlayerEntity clientPlayerEntity) {
      double groundValue = clientPlayerEntity.getX() - clientPlayerEntity.lastX;
      double groundValue2 = clientPlayerEntity.getZ() - clientPlayerEntity.lastZ;
      double groundValue3 = clientPlayerEntity.isOnGround() ? 0.0 : clientPlayerEntity.getY() - clientPlayerEntity.lastY;
      if (clientPlayerEntity.isOnGround()) {
         double groundValue4 = groundValue * 0.29811600000000005;
         double groundValue5 = groundValue2 * 0.29811600000000005;
         return new double[]{groundValue4, groundValue3, groundValue5};
      } else {
         return DeflectAmountsUtils.getAmounts(groundValue, groundValue3, groundValue2, false, 2);
      }
   }

   static boolean canFlag(double deflectZoneValue, double vecHashValue, double maxAbove, double vec) {
      if (!Double.isNaN(deflectZoneValue) && !Double.isNaN(vecHashValue)) {
         double deflectZoneLimit = deflectZoneValue - vecHashValue;
         return !(deflectZoneLimit > maxAbove) && !(-deflectZoneLimit > vec);
      } else {
         return false;
      }
   }

   private static boolean canAbsentRaycast(World world, Vec3d vec, Vec3d vec2) {
      return vec.squaredDistanceTo(vec2) < 1.0E-8
         ? true
         : world.raycast(new RaycastContext(vec, vec2, ShapeType.COLLIDER, FluidHandling.NONE, ShapeContext.absent())).getType() == Type.MISS;
   }

   public boolean canInstanceWorld(PearlShot pearlShot) {
      if (this.deflectZoneSpot != null && pearlShot != null) {
         double[] instancePlayer = getInstancePlayer();
         if (instancePlayer == null) {
            return false;
         } else {
            this.remainingAttempts = 16;
            return ZoneEscapeUtilsFlag.canFlag(this.getLandingFlight(pearlShot, instancePlayer, MinecraftClient.getInstance().world));
         }
      } else {
         return false;
      }
   }

   private boolean canFlag(Vec3d vec) {
      Spot deflectZoneSpot35 = this.deflectZoneSpot;
      if (deflectZoneSpot35 != null && vec != null) {
         double deflectZoneFactor = vec.x - deflectZoneSpot35.getX();
         double deflectZoneFactor2 = vec.y - deflectZoneSpot35.getY();
         double deflectZoneFactor3 = vec.z - deflectZoneSpot35.getZ();
         return canFlagForDouble(vec.y, Math.sqrt(deflectZoneFactor * deflectZoneFactor + deflectZoneFactor2 * deflectZoneFactor2 + deflectZoneFactor3 * deflectZoneFactor3), deflectZoneSpot35.getAmount());
      } else {
         return false;
      }
   }

   private static int getRelAngle(DeflectZone.Pick pick, DeflectZone.Pick pick2) {
      int compare = Double.compare(HeightDeltaMath.getHeightDeltaScore(pick2.rel(), true), HeightDeltaMath.getHeightDeltaScore(pick.rel(), true));
      return compare != 0 ? compare : Double.compare(pick.angle(), pick2.angle());
   }

   private double getLandingFlight(PearlShot pearlShot, double[] landingFlightValues, World world) {
      Spot deflectZoneSpot35 = this.deflectZoneSpot;
      if (deflectZoneSpot35 != null && pearlShot != null && landingFlightValues != null && pearlShot.landing() != null) {
         int[] zoneEscapeValues = ZoneEscapeUtilsFlag.getValues(this.deflectZoneValue, this.zoneIntervalTicks, pearlShot.flightTicks());
         if (zoneEscapeValues.length == 0) {
            return 0.0;
         } else {
            double[] amounts3 = DeflectAmountsUtils.getAmounts(pearlShot.yaw(), pearlShot.pitch(), landingFlightValues[3], landingFlightValues[4], landingFlightValues[5]);
            Clear clear = world == null ? Clear.INSTANCE : (x, y, z, x2, y2, worldx) -> {
               if (this.remainingAttempts <= 0) {
                  return true;
               } else {
                  this.remainingAttempts--;
                  return canAbsentRaycast(world, new Vec3d(x, y, z), new Vec3d(x2, y2, worldx));
               }
            };
            return ZoneEscapeUtilsFlag.getInstance(
               landingFlightValues[0], landingFlightValues[1], landingFlightValues[2], amounts3[0], amounts3[1], amounts3[2], pearlShot.landing().y, zoneEscapeValues, deflectZoneSpot35.getX(), deflectZoneSpot35.getY(), deflectZoneSpot35.getZ(), clear
            );
         }
      } else {
         return 0.0;
      }
   }

   static boolean canStuckClimb(String stuckClimbText, double stuckClimbLimit, boolean stuckClimbFlag) {
      return !stuckClimbFlag && !Double.isNaN(stuckClimbLimit) && !"climb".equals(stuckClimbText) && !"stuck".equals(stuckClimbText) ? stuckClimbLimit < 1.75 || stuckClimbLimit > 5.5 : true;
   }

   static boolean canFlag(double deflectZoneValue, double deflectZoneLimit) {
      return deflectZoneValue < -38.0 || deflectZoneLimit > 30.0;
   }

   private DeflectZone.ComboShot getDeflectFlight(
      Vec3d vec,
      Vec3d vec2,
      int deflectFlightLimit,
      long deflectFlightValue,
      WorldCollisionHelper blockPosHelperSolid,
      VecBlock vecBlock,
      List<VecBlockingData> list,
      AbsorptionPosData absorptionPosData,
      double vecDistanceValue,
      String deflectFlightText,
      DeflectZone.Spec spec,
      Vec3d vec3,
      ComboGoal comboGoal
   ) {
      if (vec != null && blockPosHelperSolid != null && blockPosHelperSolid.getWorld() != null && absorptionPosData != null && deflectFlightLimit > 0) {
         DeflectZone.Spec spec3 = spec == null ? DeflectZone.Spec.INSTANCE : spec;
         World world = blockPosHelperSolid.getWorld();
         Vec3d vec3d = vec2 == null ? absorptionPosData.pos() : vec2;
         double angle = Math.atan2(vec3d.z - vec.z, vec3d.x - vec.x);
         double[] clone = spec3.getClone();
         int angles = spec3.angles();
         var arrayList = new ArrayList<DeflectZone.Goal>(clone.length * angles);

         for (double deflectFlightFactor : clone) {
            for (int i = 0; i < angles; i++) {
               double angle2 = (Math.PI * 2) * i / angles;
               double x2 = vec.x + Math.cos(angle2) * deflectFlightFactor;
               double z = vec.z + Math.sin(angle2) * deflectFlightFactor;
               double abs3 = Math.abs(getAmount(angle2 - angle));
               arrayList.add(new DeflectZone.Goal(new Vec3d(x2, vec.y, z), abs3 + deflectFlightFactor * 0.001));
            }
         }

         arrayList.sort((goal2, goal3) -> Double.compare(goal2.angle(), goal3.angle()));
         Box box = getBox(vec);
         Box box2 = this.vec3dDeflectFlight == null ? null : getBox(this.vec3dDeflectFlight);
         int solveGoals = spec3.solveGoals();
         boolean lowFirst = spec3.lowFirst();
         int deflectFlightValue4 = lowFirst ? solveGoals * 2 : solveGoals;
         int[] levels = spec3.getLevels();
         double maxAbove = spec3.maxAbove();
         if (lowFirst && canStuckClimb(deflectFlightText) && !canFeet(world, arrayList, levels)) {
            levels = deflectFlightTable;
            maxAbove = 0.5;
         }

         var arrayList2 = new ArrayList<DeflectZone.Pick>(deflectFlightValue4);
         var arrayList3 = new ArrayList(deflectFlightValue4);
         float absorptionHealth = absorptionPosData.getAbsorptionHealth();
         boolean deflectFlightFlag = spec3.los() && vec3 != null;
         Vec3d pos = absorptionPosData.pos();
         boolean deflectFlightFlag2 = pos != null && vec2 != null && Math.hypot(vec2.x - pos.x, vec2.z - pos.z) > 2.0;

         for (DeflectZone.Goal goal4 : (Iterable<com.corz.client.entity.DeflectZone.Goal>) (arrayList)) {
            if (arrayList2.size() >= deflectFlightValue4) {
               break;
            }

            BlockPos floored = getFloored(world, goal4.feet(), levels);
            if (floored != null && !arrayList3.contains(floored)) {
               Vec3d vec3d2 = new Vec3d(floored.getX() + 0.5, floored.getY(), floored.getZ() + 0.5);
               if (canFlag(vec3d2.y, vec.y, maxAbove, spec3.maxBelow()) && (!deflectFlightFlag2 || !canFlag(pos, vec3d2, vec))) {
                  Box box3 = getBox(vec3d2);
                  if (!canIntersects(box3, box, box2) && BlockHazardFlags.countOpenBlocksBelow(world, floored, 4) <= 3) {
                     int stateBlock = BlockHazardFlags.getExtendedHazardFlags(world, floored);
                     if ((stateBlock & 351) == 0 && (!lowFirst || (stateBlock & 1024) == 0) && !canEntityPos(blockPosHelperSolid, vec3d2)) {
                        if (this.canFlag(vec3d2)) {
                           this.deflect++;
                        } else if ((vecBlock == null || vecBlock.getMaxPower(vec3d2, box3, list) < spec3.threatShare() * absorptionHealth)
                           && (!deflectFlightFlag || canAbsentRaycast(world, vec3d2.add(0.0, 1.62, 0.0), vec3))) {
                           BlockPos blockPos = null;
                           if (comboGoal != null) {
                              blockPos = comboGoal.base(vec3d2, vec);
                              if (blockPos == null) {
                                 continue;
                              }
                           }

                           if (spec3 == DeflectZone.Spec.INSTANCE
                              || canStuckClimb(deflectFlightText, Math.hypot(vec3d2.x - vec.x, vec3d2.z - vec.z), blockPos != null && blockPosHelperSolid.canPlaceOnBedrockObsidian(blockPos))) {
                              arrayList3.add(floored);
                              arrayList2.add(new DeflectZone.Pick(vec3d2, blockPos, vec3d2.y - vec.y, goal4.angle()));
                           }
                        }
                     }
                  }
               }
            }
         }

         if (lowFirst) {
            arrayList2.sort(DeflectZone::getRelAngle);
         }

         int min2 = Math.min(solveGoals, arrayList2.size());
         double[] deflectFlightValues = this.deflectZoneSpot == null ? null : getInstancePlayer();
         this.remainingAttempts = 16;
         PearlShot pearlShot = null;
         BlockPos base2 = null;
         double negativeInfinity = Double.NEGATIVE_INFINITY;

         for (int j = 0; j < min2; j++) {
            DeflectZone.Pick pick = (DeflectZone.Pick)arrayList2.get(j);
            PearlShot instanceWorld = this.getInstanceWorld(pick.feet(), deflectFlightLimit, vecDistanceValue, deflectFlightValue, deflectFlightText, spec3.pitchLo(), spec3.pitchHi());
            if (instanceWorld != null && (!lowFirst || !canFlag2(instanceWorld.landing().y, pick.feet().y))) {
               double deflectFlightLimit3 = lowFirst ? getAmount(pick.rel(), instanceWorld.flightTicks()) : -instanceWorld.flightTicks();
               if (deflectFlightValues != null && ZoneEscapeUtilsFlag.canFlag(this.getLandingFlight(instanceWorld, deflectFlightValues, world))) {
                  this.deflectZoneCount++;
                  this.deflectFlightHeldList.add(new DeflectZone.Held(instanceWorld, pick.base(), deflectFlightLimit3));
               } else if (pearlShot == null || (lowFirst ? deflectFlightLimit3 > negativeInfinity : instanceWorld.flightTicks() < pearlShot.flightTicks())) {
                  pearlShot = instanceWorld;
                  base2 = pick.base();
                  negativeInfinity = deflectFlightLimit3;
               }
            }
         }

         if (this.deflectFlightHeldList.size() > 1) {
            this.deflectFlightHeldList.sort((held, held2) -> Double.compare(held2.score(), held.score()));
         }

         return pearlShot == null ? null : new DeflectZone.ComboShot(pearlShot, base2);
      } else {
         return null;
      }
   }

   static double getAmount(double deflectZoneValue, int deflectZoneBound) {
      return HeightDeltaMath.getHeightDeltaScore(deflectZoneValue, true) - 0.05 * Math.max(0, deflectZoneBound);
   }

   static boolean canFlag(Vec3d vec, Vec3d vec2, Vec3d vec3) {
      if (vec != null && vec2 != null && vec3 != null) {
         double deltaX = vec2.x - vec.x;
         double deltaZ = vec2.z - vec.z;
         double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
         if (!(distance > 1.0E-6)) {
            return false;
         } else {
            double deltaX2 = vec3.x - vec.x;
            double deltaZ2 = vec3.z - vec.z;
            double deflectZoneLimit = (deltaX2 * deltaX + deltaZ2 * deltaZ) / distance;
            double deflectZoneValue = Math.abs(deltaX2 * deltaZ - deltaZ2 * deltaX) / distance;
            return deflectZoneLimit > 0.0 && deflectZoneLimit < distance && deflectZoneValue < 0.9;
         }
      } else {
         return false;
      }
   }

   public void handle(String deflectZoneText) {
      if (deflectZoneText == null) {
         this.deflectFlightHeldList.clear();
      } else {
         this.deflectFlightHeldList.removeIf(whyx -> deflectZoneText.equals(whyx.shot().why()));
      }
   }

   private static boolean canEntityPos(WorldCollisionHelper blockPosHelperSolid, Vec3d vec) {
      for (EndCrystalEntity endCrystalEntity : blockPosHelperSolid.getCrystalsInRadius(vec, 2.5)) {
         Vec3d entityPos = endCrystalEntity.getEntityPos();
         double deltaX = entityPos.x - vec.x;
         double deltaZ = entityPos.z - vec.z;
         double deltaY = entityPos.y - vec.y;
         if (Math.sqrt(deltaX * deltaX + deltaZ * deltaZ) <= 1.5 && Math.abs(deltaY) <= 2.0) {
            return true;
         }
      }

      return false;
   }

   static double getAmount(double deflectZoneValue, double deflectZoneValue2, double deflectZoneValue3, double deflectZoneLimit) {
      return deflectZoneValue < deflectZoneValue2 - 2.0 ? deflectZoneLimit - 2.0 : deflectZoneValue3 + 2.0;
   }

   static double getAmount(double deflectZoneBound, double deflectZoneLimit, double goalBound) {
      return deflectZoneBound < deflectZoneLimit ? deflectZoneLimit : Math.min(deflectZoneBound, goalBound);
   }

   static boolean canFlagForDouble(double deflectZoneValue, double deflectZoneValue2, double vecHashValue) {
      return HeightDeltaMath.getDistanceWeightedReachFraction(deflectZoneValue, deflectZoneValue2, vecHashValue) >= 0.25;
   }

   public DeflectZone.ComboShot getPosBlockDataForVec3dInt(
      Vec3d vec,
      Vec3d vec2,
      int posBlockValue,
      long deflectFlightValue,
      WorldCollisionHelper blockPosHelperSolid,
      VecBlock vecBlock,
      List<VecBlockingData> list,
      AbsorptionPosData absorptionPosData,
      double vecDistanceValue,
      String posBlockText,
      DeflectZone.Spec spec,
      Vec3d vec3,
      ComboGoal comboGoal
   ) {
      return comboGoal == null ? null : this.getDeflectFlight(vec, vec2, posBlockValue, deflectFlightValue, blockPosHelperSolid, vecBlock, list, absorptionPosData, vecDistanceValue, posBlockText, spec, vec3, comboGoal);
   }

   public static PearlShot getVecDistance(
      Vec3d vec, double[] vecDistanceValues, Vec3d vec2, int vecDistanceLimit, double vecDistanceValue, long hunterLandingValue, String vecDistanceText, double worldLimit, double groundY, float long2floatopenhashmap
   ) {
      if (vec != null && vecDistanceValues != null && vecDistanceValues.length >= 3 && vec2 != null) {
         double deflectZoneLimit = Double.isFinite(worldLimit) ? worldLimit : -38.0;
         double goalBound = Double.isFinite(groundY) ? groundY : 30.0;
         if (deflectZoneLimit > goalBound) {
            double deflectZoneLimit7 = deflectZoneLimit;
            deflectZoneLimit = goalBound;
            goalBound = deflectZoneLimit7;
         }

         double deltaX = vec2.x - vec.x;
         double deltaZ = vec2.z - vec.z;
         double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
         if (distance < 1.0) {
            return null;
         } else {
            double vecDistanceFactor = deltaX / distance;
            double vecDistanceFactor2 = deltaZ / distance;
            float vecDistanceValue8 = VecShieldUtilsAmounts.getAmount(deltaX, deltaZ);
            double vecDistanceValue9 = getAmount(Float.isNaN(long2floatopenhashmap) ? 0.0 : long2floatopenhashmap, deflectZoneLimit, goalBound);

            for (int i = 0; i <= 2; i++) {
               float directionVecValue = vecDistanceValue8;
               DeflectZone.Sim sim = new DeflectZone.Sim();
               Miss miss = spawnx -> {
                  Result directionVec = sim.getDirectionVec(vec, directionVecValue, spawnx, vecDistanceValues);
                  if (directionVec != null && directionVec.directionVecActive && directionVec.vec3d3VecMoved != null) {
                     Vec3d vec3d = directionVec.vec3d3VecMoved;
                     return (vec3d.x - vec.x) * vecDistanceFactor + (vec3d.z - vec.z) * vecDistanceFactor2 - distance;
                  } else {
                     return Double.POSITIVE_INFINITY;
                  }
               };
               boolean vecDistanceFlag = false;

               for (double zoneEscapeValue : getAmounts(vecDistanceValue9, vec2.y, vec.y, deflectZoneLimit, goalBound)) {
                  double vecHashValue = ZoneEscapeUtils2.getAmount(miss, zoneEscapeValue, deflectZoneLimit, goalBound, 0.4, 8);
                  if (!Double.isNaN(vecHashValue)) {
                     Result directionVec2 = sim.getDirectionVec(vec, directionVecValue, vecHashValue, vecDistanceValues);
                     if (directionVec2 != null && directionVec2.directionVecActive && directionVec2.vec3d3VecMoved != null) {
                        Vec3d vec3d2 = directionVec2.vec3d3VecMoved;
                        double deflectValue = vec3d2.x - vec.x;
                        double x = vec3d2.z - vec.z;
                        double degrees = DeflectAmountsUtils.getAmount(deltaX, deltaZ, deflectValue, x);
                        if (i < 2 && Math.abs(DeflectAmountsUtils.getAmount(deflectValue, x, degrees)) > 0.25) {
                           vecDistanceValue8 = (float)(directionVecValue + degrees);
                           vecDistanceValue9 = vecHashValue;
                           vecDistanceFlag = true;
                           break;
                        }

                        if (!(vec3d2.distanceTo(vec2) > vecDistanceValue) && directionVec2.flightTicks <= vecDistanceLimit) {
                           return new PearlShot(directionVecValue, (float)vecHashValue, vec3d2, directionVec2.flightTicks, vecDistanceValue, hunterLandingValue + 6L, vecDistanceText);
                        }
                     }
                  }
               }

               if (!vecDistanceFlag) {
                  return null;
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   public void handleDeflectZone(Spot data, int value, double deflectZoneLimit) {
      this.deflectZoneSpot = data;
      this.deflectZoneValue = value;
      this.zoneIntervalTicks = Double.isFinite(deflectZoneLimit) && deflectZoneLimit > 0.0 ? deflectZoneLimit : (data == null ? 0.0 : data.interval());
      this.deflect = 0;
      this.deflectZoneCount = 0;
   }

   private static boolean canFeet(World world, List<DeflectZone.Goal> list, int[] feetValues) {
      for (DeflectZone.Goal goal : list) {
         if (getFloored(world, goal.feet(), feetValues) != null) {
            return true;
         }
      }

      return false;
   }

   private static double getAmount(double deflectZoneValue) {
      double deflectZoneTotal = deflectZoneValue % (Math.PI * 2);
      if (deflectZoneTotal > Math.PI) {
         deflectZoneTotal -= Math.PI * 2;
      }

      if (deflectZoneTotal < -Math.PI) {
         deflectZoneTotal += Math.PI * 2;
      }

      return deflectZoneTotal;
   }

   public PearlShot getShot(
      Vec3d vec,
      Vec3d vec2,
      int shotValue,
      long deflectFlightValue,
      WorldCollisionHelper blockPosHelperSolid,
      VecBlock vecBlock,
      List<VecBlockingData> list,
      AbsorptionPosData absorptionPosData,
      double vecDistanceValue,
      String shotText,
      DeflectZone.Spec spec,
      Vec3d vec3
   ) {
      DeflectZone.ComboShot deflectFlight = this.getDeflectFlight(vec, vec2, shotValue, deflectFlightValue, blockPosHelperSolid, vecBlock, list, absorptionPosData, vecDistanceValue, shotText, spec, vec3, null);
      return deflectFlight == null ? null : deflectFlight.shot();
   }

   static boolean canFlag2(double deflectZoneValue, double deflectZoneValue2) {
      return !(deflectZoneValue <= deflectZoneValue2 + 0.3);
   }

   public String formatDeflectZone() {
      if (this.deflectZoneCount > 0) {
         return "deflect";
      } else {
         return this.deflect > 0 ? "zone" : null;
      }
   }

   public PearlShot getVecDataForVec3dInt(Vec3d vec, int vecValue, double vecDistanceValue, long hunterLandingValue, String vecText) {
      return this.getInstanceWorld(vec, vecValue, vecDistanceValue, hunterLandingValue, vecText, -38.0, 30.0);
   }

   private static double[] getInstancePlayer() {
      ClientPlayerEntity player = MinecraftClient.getInstance().player;
      if (player == null) {
         return null;
      } else {
         Vec3d eye = FloorMathUtilsDirection.getEye(player);
         double[] ground = getGround(player);
         return new double[]{eye.x, eye.y, eye.z, ground[0], ground[1], ground[2]};
      }
   }

   public record ComboShot(PearlShot shot, BlockPos base) {
   }

   private record Goal(Vec3d feet, double angle) {
   }

   private record Held(PearlShot shot, BlockPos base, double score) {
   }

   private record Pick(Vec3d feet, BlockPos base, double rel, double angle) {
   }

   private static final class Sim {
      Result vecDirection;
      double cachedInputAmount = Double.NaN;

      Result getDirectionVec(Vec3d vec, float directionVecValue, double amount, double[] directionVecValues) {
         if (amount == this.cachedInputAmount && this.vecDirection != null) {
            return this.vecDirection;
         } else {
            double[] amounts2 = DeflectAmountsUtils.getAmounts(directionVecValue, (float)amount, directionVecValues[0], directionVecValues[1], directionVecValues[2]);
            this.vecDirection = FloorMathUtilsDirection.getVecDirection(vec, new Vec3d(amounts2[0], amounts2[1], amounts2[2]), 200);
            this.cachedInputAmount = amount;
            return this.vecDirection;
         }
      }
   }

   public record Spec(
      double[] rings,
      int angles,
      int solveGoals,
      double threatShare,
      double maxDy,
      boolean los,
      double pitchLo,
      double pitchHi,
      double maxAbove,
      double maxBelow,
      int[] levels,
      boolean lowFirst
   ) {
      public static final DeflectZone.Spec INSTANCE = new DeflectZone.Spec(DeflectZone.deflectZoneTable, 12, 3, 0.5, Double.POSITIVE_INFINITY, false);

      public static DeflectZone.Spec getAmounts(double threatShare) {
         return new DeflectZone.Spec(DeflectZone.RADIUS_STEPS_SHORT, 16, 6, threatShare, 2.5, true, -80.0, 88.0, 2.5, 2.5, DeflectZone.DROP_OFFSETS_DEFAULT, true);
      }

      public static DeflectZone.Spec getAmountsForDouble(double threatShare) {
         return new DeflectZone.Spec(DeflectZone.RADIUS_STEPS_LONG, 16, 6, threatShare, 1.0, true, -80.0, 60.0, 0.3, 2.5, DeflectZone.DROP_OFFSETS_DEFAULT, true);
      }

      public double[] getClone() {
         return (double[])this.rings.clone();
      }

      public static DeflectZone.Spec getAmountsSpec(double threatShare) {
         return new DeflectZone.Spec(DeflectZone.RADIUS_STEPS_ALT, 16, 6, threatShare, 2.5, true, -60.0, 60.0, 0.3, 2.5, DeflectZone.DROP_OFFSETS_ALT, true);
      }

      public static DeflectZone.Spec getAmounts4(double threatShare) {
         return new DeflectZone.Spec(DeflectZone.RADIUS_STEPS_LONG, 16, 6, threatShare, 3.0, true, -80.0, 88.0, 0.3, 3.0, DeflectZone.DROP_OFFSETS_WIDE, true);
      }

      public Spec(double[] pitchHi, int los, int maxDy, double threatShare, double rings, boolean solveGoals, double angles, double pitchLo) {
         this(pitchHi, los, maxDy, threatShare, rings, solveGoals, angles, pitchLo, rings, rings, DeflectZone.table, false);
      }

      public Spec(
         double[] rings,
         int angles,
         int solveGoals,
         double threatShare,
         double maxDy,
         boolean los,
         double pitchLo,
         double pitchHi,
         double maxAbove,
         double maxBelow,
         int[] levels,
         boolean lowFirst
      ) {
         rings = rings == null ? new double[0] : (double[])rings.clone();
         angles = Math.max(1, angles);
         solveGoals = Math.max(1, solveGoals);
         if (!Double.isFinite(pitchLo)) {
            pitchLo = -38.0;
         }

         if (!Double.isFinite(pitchHi)) {
            pitchHi = 30.0;
         }

         if (pitchLo > pitchHi) {
            double pitchLo2 = pitchLo;
            pitchLo = pitchHi;
            pitchHi = pitchLo2;
         }

         if (Double.isNaN(maxAbove)) {
            maxAbove = maxDy;
         }

         if (Double.isNaN(maxBelow)) {
            maxBelow = maxDy;
         }

         levels = levels != null && levels.length != 0 ? (int[])levels.clone() : (int[])DeflectZone.table.clone();
         this.rings = rings;
         this.angles = angles;
         this.solveGoals = solveGoals;
         this.threatShare = threatShare;
         this.maxDy = maxDy;
         this.los = los;
         this.pitchLo = pitchLo;
         this.pitchHi = pitchHi;
         this.maxAbove = maxAbove;
         this.maxBelow = maxBelow;
         this.levels = levels;
         this.lowFirst = lowFirst;
      }

      public static DeflectZone.Spec getAmounts5(double threatShare) {
         return new DeflectZone.Spec(DeflectZone.RADIUS_STEPS_SHORT, 16, 5, threatShare, 1.0, true, -38.0, 30.0, 0.3, 2.5, DeflectZone.DROP_OFFSETS_DEFAULT, true);
      }

      public Spec(double[] maxDy, int angles, int los, double threatShare, double solveGoals, boolean rings) {
         this(maxDy, angles, los, threatShare, solveGoals, rings, -38.0, 30.0);
      }

      public static DeflectZone.Spec getAmounts6(double threatShare) {
         return new DeflectZone.Spec(DeflectZone.RADIUS_STEPS_LONG, 16, 6, threatShare, 2.5, true, -89.0, 88.0, 0.3, 2.5, DeflectZone.DROP_OFFSETS_LOW_FIRST, true);
      }

      public int[] getLevels() {
         return (int[])this.levels.clone();
      }
   }
}
