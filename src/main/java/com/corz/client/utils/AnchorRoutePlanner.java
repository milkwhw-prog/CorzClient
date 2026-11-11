package com.corz.client.utils;

import com.corz.client.inventory.BlockEntityUtils;
import it.unimi.dsi.fastutil.longs.Long2LongOpenHashMap;
import java.util.List;
import java.util.Locale;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.RoutedBreachingApi;
import com.corz.client.utils.PreferAnchorData;
import com.corz.client.utils.AStarPathfinder;
import com.corz.client.utils.PathGoals;
import com.corz.client.utils.PlayerCombatState;
import com.corz.client.utils.PathFollower;
import com.corz.client.utils.RayBudget;
import com.corz.client.utils.PathfindingWorldAdapter;
import com.corz.client.utils.HuntEhpTables;
import com.corz.client.utils.FallDamageMath;

public final class AnchorRoutePlanner implements RoutedBreachingApi {
   private PreferAnchorData searchTarget;
   private List<AStarPathfinder.Node> routeNodes;
   private long routeSearchTick;
   private boolean partialRouteDone;
   private double routeEstimateTicks;
   private static final double[] WALK_ANGLE_OFFSETS_DEGREES = new double[]{0.0, 45.0, -45.0};
   private final boolean directOnly;
   private boolean gapsAllowed;
   private boolean cachedReachable;
   private AStarPathfinder.Search activeSearch;
   private int searchStartZ;
   private int meleeRetryCount;
   private AnchorRoutePlanner.Mode currentMode;
   private boolean routeInvalidated;
   private AnchorRoutePlanner.Mode routeResultMode;
   private final PathGoals.Los lineOfSight;
   private long cachedCellKey;
   final int maxExpandedNodes;
   private Vec3d cachedAnchor;
   private final AStarPathfinder.Danger dangerModel;
   private double searchStartY;
   private long lowAnchorUntilTick;
   private final AStarPathfinder.World world;
   private PlayerCombatState immuneFireData;
   private long lastPruneTick;
   private long unreachableSinceTick;
   private double ticksPerHp;
   private long cachedAtTick;
   private boolean offPath;
   private final PathFollower routeFollower = new PathFollower();
   private int searchStartX;
   private PathFollower.Cmd lastWalkCommand;
   private int maxDrop;
   private boolean anchorRetryUsed;
   private PreferAnchorData.Kind routeKind;
   private Vec3d lowAnchorPos;
   private int searchCount;
   private long crystalAnchorUntilTick;
   private boolean sprintGapsAllowed;
   private long searchStartTick;
   private long latestTick;
   private Vec3d routeAnchorPos;
   final int searchStepBudget;
   private final Long2LongOpenHashMap blockedCellExpiryTicks = new Long2LongOpenHashMap();
   private double directDistanceTicks;
   private Vec3d crystalAnchorPos;
   private PreferAnchorData.Kind cachedKind;
   private boolean cachedCrystalNear;
   private float hpBudget;
   private long unreachableUntilTick;

   private AStarPathfinder.Limits buildSearchLimits() {
      return new AStarPathfinder.Limits(
         this.maxDrop,
         this.hpBudget,
         this.immuneFireData.fallEpf(),
         this.immuneFireData.resistanceAmp(),
         this.immuneFireData.fireResistant(),
         this.gapsAllowed,
         this.sprintGapsAllowed,
         this.ticksPerHp,
         32,
         24,
         this.maxExpandedNodes
      );
   }

   private PathFollower.Cmd getFallbackWalkCommand(Vec3d vec, PreferAnchorData preferAnchorData) {
      Vec3d vec3d = resolveTargetPoint(preferAnchorData, vec);
      if (vec3d == null) {
         return new PathFollower.Cmd(false, 0.0, 0.0, false, false, false, false, 0, 0.0, null);
      } else {
         double deltaX = vec3d.x - vec.x;
         double deltaZ = vec3d.z - vec.z;
         double hypot = Math.hypot(deltaX, deltaZ);
         if (hypot < 0.001) {
            return new PathFollower.Cmd(false, 0.0, 0.0, false, false, false, false, 0, 0.0, null);
         } else {
            double apiWalkFactor = deltaX / hypot;
            double apiWalkFactor2 = deltaZ / hypot;
            double min3 = Math.min(hypot, 1.5);

            for (double degrees : WALK_ANGLE_OFFSETS_DEGREES) {
               double angle = Math.toRadians(degrees);
               double cos = Math.cos(angle);
               double sin = Math.sin(angle);
               double apiWalkFactor4 = apiWalkFactor * cos - apiWalkFactor2 * sin;
               double apiWalkFactor5 = apiWalkFactor * sin + apiWalkFactor2 * cos;
               Vec3d vec3d2 = new Vec3d(vec.x + apiWalkFactor4 * min3, vec.y, vec.z + apiWalkFactor5 * min3);
               if (PathFollower.isTraceable(this.world, vec, vec3d2, true, 1.5, Math.min(this.maxDrop, 1))) {
                  return new PathFollower.Cmd(true, apiWalkFactor4, apiWalkFactor5, false, false, false, false, 0, hypot, AStarPathfinder.Move.WALK);
               }
            }

            return new PathFollower.Cmd(false, 0.0, 0.0, false, false, false, false, 0, hypot, null);
         }
      }
   }

   public void markUnreachable(long time) {
      if (!this.directOnly) {
         this.latestTick = Math.max(this.latestTick, time);
         this.unreachableUntilTick = time + 60L;
         if (this.unreachableSinceTick == Long.MIN_VALUE) {
            this.unreachableSinceTick = time;
         }

         this.routeInvalidated = true;
      }
   }

   private static Vec3d resolveTargetPoint(PreferAnchorData preferAnchorData, Vec3d vec) {
      Vec3d preferAnchor = preferAnchorData.getPreferAnchor();
      if (preferAnchor == null || !Double.isFinite(preferAnchor.x) || !Double.isFinite(preferAnchor.z)) {
         return null;
      } else {
         return !Double.isFinite(preferAnchor.y) ? new Vec3d(preferAnchor.x, vec.y, preferAnchor.z) : preferAnchor;
      }
   }

   public boolean canReachDirectly(long max, Vec3d vec, PreferAnchorData preferAnchorData) {
      this.latestTick = max;
      if (this.directOnly) {
         this.currentMode = AnchorRoutePlanner.Mode.DIRECT;
         return true;
      } else if (vec != null && isFiniteVec(vec) && preferAnchorData != null && preferAnchorData.anchor() != null) {
         PreferAnchorData anchorLow = this.resolveLowAnchor(max, preferAnchorData);
         Vec3d vec3d = resolveTargetPoint(anchorLow, vec);
         if (vec3d == null) {
            return false;
         } else if (!this.isTargetReachable(max, vec, anchorLow, vec3d)) {
            return false;
         } else {
            this.currentMode = AnchorRoutePlanner.Mode.DIRECT;
            this.directDistanceTicks = Math.hypot(vec3d.x - vec.x, vec3d.z - vec.z) * 3.6;
            this.activeSearch = null;
            this.searchTarget = null;
            this.routeNodes = null;
            this.routeResultMode = AnchorRoutePlanner.Mode.IDLE;
            this.routeAnchorPos = null;
            this.routeKind = null;
            this.routeEstimateTicks = Double.NaN;
            this.routeFollower.reset();
            this.lastWalkCommand = null;
            this.offPath = false;
            this.routeInvalidated = false;
            this.partialRouteDone = false;
            if (max >= this.unreachableUntilTick) {
               this.unreachableSinceTick = Long.MIN_VALUE;
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public int[] getNextNodeCoords() {
      List list = this.routeNodes;
      if (!this.directOnly && list != null) {
         int index = this.routeFollower.getCurrentNodeIndex();
         if (index < 1) {
            index = Math.min(1, list.size() - 1);
         }

         if (index >= 0 && index < list.size()) {
            AStarPathfinder.Node node = (AStarPathfinder.Node)list.get(index);
            return new int[]{node.x(), node.y(), node.z()};
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   PreferAnchorData resolveLowAnchor(long anchorLowLimit, PreferAnchorData anchorLow) {
      if (anchorLow != null && anchorLow.kind() == PreferAnchorData.Kind.MELEE_LOW && anchorLow.anchor() != null) {
         Vec3d vec3d2 = this.lowAnchorPos;
         if (vec3d2 == null || anchorLowLimit >= this.lowAnchorUntilTick || anchorLowLimit < this.lowAnchorUntilTick - 60L) {
            return anchorLow;
         } else {
            return !(vec3d2.distanceTo(anchorLow.anchor()) <= 2.0) ? anchorLow : PreferAnchorData.getMelee(anchorLow.anchor(), anchorLow.prefer());
         }
      } else {
         return anchorLow;
      }
   }

   boolean hasActiveSearch() {
      return this.activeSearch != null;
   }

   private static PathFollower.Cmd getDirectWalkCommand(Vec3d vec, PreferAnchorData preferAnchorData) {
      Vec3d vec3d = preferAnchorData == null ? null : preferAnchorData.getPreferAnchor();
      if (vec != null && vec3d != null) {
         double deltaX = vec3d.x - vec.x;
         double deltaZ = vec3d.z - vec.z;
         double hypot = Math.hypot(deltaX, deltaZ);
         return !(hypot > 0.001)
            ? new PathFollower.Cmd(false, 0.0, 0.0, false, false, true, false, 0, 0.0, AStarPathfinder.Move.WALK)
            : new PathFollower.Cmd(true, deltaX / hypot, deltaZ / hypot, false, hypot >= 2.0, false, false, 0, hypot, AStarPathfinder.Move.WALK);
      } else {
         return new PathFollower.Cmd(false, 0.0, 0.0, false, false, true, false, 0, 0.0, null);
      }
   }

   public void markCellBlocked(int vecHashValue, int blockY, int z2, long vecHashValue4) {
      if (!this.directOnly) {
         this.blockedCellExpiryTicks.put(AStarPathfinder.packPosKey(vecHashValue, blockY, z2), vecHashValue4 + 100L);
         this.routeInvalidated = true;
      }
   }

   static boolean isFarFrom(Vec3d vec, Vec3d vec2) {
      return vec != null && vec2 != null ? vec.distanceTo(vec2) > 2.0 || Math.abs(vec.y - vec2.y) >= 0.9 : vec != vec2;
   }

   @Override
   public AnchorRoutePlanner.Mode getRouteMode() {
      if (this.directOnly) {
         return AnchorRoutePlanner.Mode.DIRECT;
      } else {
         return this.unreachableUntilTick != Long.MIN_VALUE && this.latestTick < this.unreachableUntilTick ? AnchorRoutePlanner.Mode.UNREACHABLE : this.currentMode;
      }
   }

   static boolean isHeightReachable(double vecHashValue, double vecHashValue2, double vecHashLimit) {
      if (Double.isNaN(vecHashValue) || Double.isNaN(vecHashValue2)) {
         return false;
      } else {
         return vecHashLimit > 16.0 ? true : Math.abs(vecHashValue - vecHashValue2) <= 0.600000001;
      }
   }

   private double[] findStartCell(Vec3d vec, Vec3d vec2, boolean apiFlag) {
      int apiValue = (int)Math.floor(vec.x);
      int z2 = (int)Math.floor(vec.z);
      if (!apiFlag && vec2 != null && Double.isFinite(vec2.x) && Double.isFinite(vec2.z)) {
         double apiValue3 = vec.x + vec2.x * 3.0;
         double apiValue4 = vec.z + vec2.z * 3.0;
         int floor5 = (int)Math.floor(apiValue3);
         int floor6 = (int)Math.floor(apiValue4);
         double groundY = AStarPathfinder.findGroundY(this.world, floor5, floor6, vec.y, 0.6, 26);
         if (Double.isFinite(groundY)) {
            return new double[]{floor5, floor6, groundY};
         } else {
            double groundY8 = AStarPathfinder.findGroundY(this.world, apiValue, z2, vec.y, 0.6, 26);
            return Double.isFinite(groundY8) ? new double[]{apiValue, z2, groundY8} : null;
         }
      } else {
         double groundY9 = AStarPathfinder.findGroundY(this.world, apiValue, z2, vec.y, 0.6, 1);
         if (Double.isFinite(groundY9) && Math.abs(groundY9 - vec.y) <= 0.6) {
            return new double[]{apiValue, z2, groundY9};
         } else {
            double apiValue10 = 0.299;
            double apiLimit = Double.MAX_VALUE;
            double[] api = null;

            for (int i = 0; i < 4; i++) {
               int hashRegistryValue = (int)Math.floor(vec.x + ((i & 1) == 0 ? -apiValue10 : apiValue10));
               int floor12 = (int)Math.floor(vec.z + ((i & 2) == 0 ? -apiValue10 : apiValue10));
               if (hashRegistryValue != apiValue || floor12 != z2) {
                  double groundY13 = AStarPathfinder.findGroundY(this.world, hashRegistryValue, floor12, vec.y, 0.6, 1);
                  if (Double.isFinite(groundY13) && !(Math.abs(groundY13 - vec.y) > 0.6)) {
                     double apiValue14 = Math.hypot(hashRegistryValue + 0.5 - vec.x, floor12 + 0.5 - vec.z) + Math.abs(groundY13 - vec.y);
                     if (apiValue14 < apiLimit) {
                        apiLimit = apiValue14;
                        api = new double[]{hashRegistryValue, floor12, groundY13};
                     }
                  }
               }
            }

            if (api != null) {
               return api;
            } else {
               double groundY15 = AStarPathfinder.findGroundY(this.world, apiValue, z2, vec.y, 0.6, 26);
               return Double.isFinite(groundY15) ? new double[]{apiValue, z2, groundY15} : null;
            }
         }
      }
   }

   public boolean isGapOrDropCell(int gapDropValue, int gapDropValue2) {
      List list = this.routeNodes;
      if (!this.directOnly && list != null) {
         int max3 = Math.max(1, this.routeFollower.getCurrentNodeIndex());
         int max4 = Math.max(1, max3 - 2);
         int gapDropLimit = Math.min(list.size(), max3 + 4 + 2);

         for (int i = max4; i < gapDropLimit; i++) {
            AStarPathfinder.Node node = (AStarPathfinder.Node)list.get(i);
            AStarPathfinder.Move move = node.move();
            if (move == AStarPathfinder.Move.DROP && node.x() == gapDropValue && node.z() == gapDropValue2) {
               return true;
            }

            if (move == AStarPathfinder.Move.GAP1 || move == AStarPathfinder.Move.GAP2) {
               AStarPathfinder.Node aStarPathfinderNode = (AStarPathfinder.Node)list.get(i - 1);
               int signum = Integer.signum(node.x() - aStarPathfinderNode.x());
               int signum2 = Integer.signum(node.z() - aStarPathfinderNode.z());
               int gapDropLimit2 = move == AStarPathfinder.Move.GAP1 ? 1 : 2;

               for (int j = 1; j <= gapDropLimit2; j++) {
                  if (aStarPathfinderNode.x() + signum * j == gapDropValue && aStarPathfinderNode.z() + signum2 * j == gapDropValue2) {
                     return true;
                  }
               }
            }
         }

         return false;
      } else {
         return false;
      }
   }

   private AStarPathfinder.Goal buildGoalForTarget(long anchorValue, PreferAnchorData preferAnchorData, int byteHashValue) {
      return (AStarPathfinder.Goal)(preferAnchorData != null
            && preferAnchorData.kind() == PreferAnchorData.Kind.CRYSTAL
            && preferAnchorData.anchor() != null
            && this.isCrystalAnchorKnown(anchorValue, preferAnchorData)
         ? PathGoals.createCubeAreaGoal(preferAnchorData.anchor(), preferAnchorData.prefer(), this.lineOfSight, byteHashValue)
         : PathGoals.createGoalForPreference(preferAnchorData, this.lineOfSight, byteHashValue));
   }

   private static boolean isFiniteVec(Vec3d vec) {
      return Double.isFinite(vec.x) && Double.isFinite(vec.y) && Double.isFinite(vec.z);
   }

   private void startSearch(AStarPathfinder.Goal goal) {
      if (goal == null) {
         this.activeSearch = null;
      } else {
         long searchStartTick8 = this.searchStartTick;
         this.searchCount++;
         this.activeSearch = AStarPathfinder.createSearch(
            this.world, this.searchStartX, this.searchStartZ, this.searchStartY, goal, this.buildSearchLimits(), this.dangerModel, hashOpenValue -> this.blockedCellExpiryTicks.get(hashOpenValue) > searchStartTick8
         );
      }
   }

   boolean isLowAnchorWindowActive(long vecLimit) {
      return this.lowAnchorPos != null && vecLimit < this.lowAnchorUntilTick && vecLimit >= this.lowAnchorUntilTick - 60L;
   }

   public void reset() {
      this.activeSearch = null;
      this.searchTarget = null;
      this.anchorRetryUsed = false;
      this.meleeRetryCount = 0;
      this.clearRoute();
      this.routeAnchorPos = null;
      this.routeKind = null;
      this.routeSearchTick = Long.MIN_VALUE;
      this.blockedCellExpiryTicks.clear();
      this.routeInvalidated = false;
      this.partialRouteDone = false;
      this.offPath = false;
      this.unreachableSinceTick = Long.MIN_VALUE;
      this.unreachableUntilTick = Long.MIN_VALUE;
      this.lastWalkCommand = null;
      this.directDistanceTicks = Double.NaN;
      this.cachedCellKey = Long.MIN_VALUE;
      this.cachedAtTick = Long.MIN_VALUE;
      this.cachedAnchor = null;
      this.cachedKind = null;
      this.cachedCrystalNear = false;
      this.lowAnchorUntilTick = Long.MIN_VALUE;
      this.lowAnchorPos = null;
      this.crystalAnchorUntilTick = Long.MIN_VALUE;
      this.crystalAnchorPos = null;
      this.currentMode = this.directOnly ? AnchorRoutePlanner.Mode.DIRECT : AnchorRoutePlanner.Mode.IDLE;
   }

   public void setImmuneFireData(PlayerCombatState immuneFireData) {
      if (!this.directOnly) {
         this.immuneFireData = immuneFireData == null ? PlayerCombatState.INSTANCE : immuneFireData;
         this.recomputeLimits(this.immuneFireData);
      }
   }

   boolean isCrystalAnchorKnown(long anchorDistanceLimit, PreferAnchorData preferAnchorData) {
      Vec3d vec3d4 = this.crystalAnchorPos;
      if (preferAnchorData == null || preferAnchorData.kind() != PreferAnchorData.Kind.CRYSTAL || preferAnchorData.anchor() == null || vec3d4 == null) {
         return false;
      } else {
         return anchorDistanceLimit < this.crystalAnchorUntilTick && anchorDistanceLimit >= this.crystalAnchorUntilTick - 60L ? vec3d4.distanceTo(preferAnchorData.anchor()) <= 2.0 : false;
      }
   }

   public PathFollower.Cmd computeWalkCommand(long max, Vec3d vec, Vec3d vec2, boolean searchingRoutedFlag, boolean searchingRoutedFlag2, boolean searchingRoutedFlag3, PreferAnchorData preferAnchorData) {
      this.latestTick = max;
      if (this.directOnly) {
         this.currentMode = AnchorRoutePlanner.Mode.DIRECT;
         this.lastWalkCommand = getDirectWalkCommand(vec, preferAnchorData);
         return this.lastWalkCommand;
      } else if (vec != null && isFiniteVec(vec) && preferAnchorData != null && preferAnchorData.anchor() != null) {
         PreferAnchorData anchorLow = this.resolveLowAnchor(max, preferAnchorData);
         this.pruneExpiredBlockedCells(max);
         Vec3d anchor = anchorLow.anchor();
         boolean searchingRoutedFlag4 = this.routeKind != null && this.routeKind != anchorLow.kind();
         boolean searchingRoutedFlag5 = this.routeAnchorPos != null && hasAnchorShifted(this.routeAnchorPos, anchor);
         boolean searchingRoutedFlag6 = this.routeNodes != null && max - this.routeSearchTick >= 8L;
         boolean searchingRoutedFlag7 = this.routeNodes != null && !this.isRouteStillValid();
         if (searchingRoutedFlag7 || this.offPath) {
            this.clearRoute();
         }

         if (this.activeSearch != null
            && (this.searchTarget == null || this.searchTarget.kind() != anchorLow.kind() || isVeryFarFrom(this.searchTarget.anchor(), anchor) || this.routeInvalidated || this.offPath)) {
            this.activeSearch = null;
         }

         boolean searchingRoutedFlag8 = this.routeNodes == null || searchingRoutedFlag5 || searchingRoutedFlag6 || this.routeInvalidated || this.partialRouteDone || this.offPath || searchingRoutedFlag4;
         if (searchingRoutedFlag8 && this.activeSearch == null) {
            this.startNewSearch(max, vec, vec2, searchingRoutedFlag, anchorLow);
         }

         this.routeInvalidated = false;
         this.partialRouteDone = false;
         this.offPath = false;
         this.advanceSearch(max);
         PathFollower.Cmd data;
         if (this.routeNodes != null) {
            data = this.routeFollower.computeCommand(this.routeNodes, vec, vec2, searchingRoutedFlag, searchingRoutedFlag2, searchingRoutedFlag3, this.world, this.maxDrop);
            if (data.offPath()) {
               this.offPath = true;
            }

            if (data.done() && this.routeResultMode == AnchorRoutePlanner.Mode.PARTIAL && this.activeSearch == null) {
               this.partialRouteDone = true;
            }

            this.currentMode = this.routeResultMode;
         } else {
            data = this.getFallbackWalkCommand(vec, anchorLow);
            this.currentMode = this.unreachableSinceTick != Long.MIN_VALUE
               ? AnchorRoutePlanner.Mode.UNREACHABLE
               : (this.activeSearch != null ? AnchorRoutePlanner.Mode.SEARCHING : AnchorRoutePlanner.Mode.IDLE);
         }

         if (this.currentMode != AnchorRoutePlanner.Mode.UNREACHABLE && max >= this.unreachableUntilTick) {
            this.unreachableSinceTick = Long.MIN_VALUE;
         }

         this.lastWalkCommand = data;
         return data;
      } else {
         this.currentMode = AnchorRoutePlanner.Mode.IDLE;
         this.lastWalkCommand = new PathFollower.Cmd(false, 0.0, 0.0, false, false, false, false, 0, 0.0, null);
         return this.lastWalkCommand;
      }
   }

   public static AnchorRoutePlanner create(RayBudget rayBudget, AStarPathfinder.Danger danger) {
      PathGoals.Los los = (x, y, z, x2, y2, z2) -> PathfindingWorldAdapter.isWorldAndPlayerPresent()
         && (rayBudget == null || rayBudget.tryReserveRays(1, 3))
         && BlockEntityUtils.canSubtractRaycast(new Vec3d(x, y, z), new Vec3d(x2, y2, z2));
      return new AnchorRoutePlanner(new PathfindingWorldAdapter(), los, danger == null ? AStarPathfinder.Danger.INSTANCE : danger, false);
   }

   private void onSearchResult(long time, AStarPathfinder.Result result) {
      PreferAnchorData preferAnchorData = this.searchTarget;
      if (result != null && preferAnchorData != null) {
         switch (result.status()) {
            case REACHED:
               this.applyRoute(time, result, AnchorRoutePlanner.Mode.ROUTED);
               break;
            case PARTIAL:
               this.applyRoute(time, result, AnchorRoutePlanner.Mode.PARTIAL);
               break;
            default:
               if (preferAnchorData.kind() == PreferAnchorData.Kind.MELEE_LOW && preferAnchorData.anchor() != null) {
                  this.lowAnchorUntilTick = time + 60L;
                  this.lowAnchorPos = preferAnchorData.anchor();
                  this.searchTarget = PreferAnchorData.getMelee(preferAnchorData.anchor(), preferAnchorData.prefer());
                  this.startSearch(PathGoals.createGoalForPreference(this.searchTarget, this.lineOfSight, 16));
                  return;
               }

               if (!this.anchorRetryUsed && preferAnchorData.kind() == PreferAnchorData.Kind.MELEE && this.meleeRetryCount < 1) {
                  this.meleeRetryCount++;
                  this.anchorRetryUsed = true;
                  Vec3d anchor = preferAnchorData.anchor();
                  this.startSearch(PathGoals.createCubeAreaGoal(new Vec3d(anchor.x, anchor.y + 1.0, anchor.z), preferAnchorData.prefer(), this.lineOfSight, 16));
                  return;
               }

               if (!this.anchorRetryUsed && preferAnchorData.kind() == PreferAnchorData.Kind.CRYSTAL && preferAnchorData.anchor() != null && !this.isCrystalAnchorKnown(time, preferAnchorData)) {
                  this.anchorRetryUsed = true;
                  this.crystalAnchorUntilTick = time + 60L;
                  this.crystalAnchorPos = preferAnchorData.anchor();
                  this.startSearch(PathGoals.createCubeAreaGoal(preferAnchorData.anchor(), preferAnchorData.prefer(), this.lineOfSight, 16));
                  return;
               }

               this.applyRoute(time, result, AnchorRoutePlanner.Mode.UNREACHABLE);
               if (this.unreachableSinceTick == Long.MIN_VALUE) {
                  this.unreachableSinceTick = time;
               }
         }
      } else {
         this.activeSearch = null;
      }
   }

   public static AnchorRoutePlanner createDirectOnly() {
      return new AnchorRoutePlanner(null, null, null, true);
   }

   List<AStarPathfinder.Node> getRouteNodes() {
      return this.routeNodes;
   }

   @Override
   public double getEstimatedTicksRemaining() {
      if (this.directOnly) {
         return Double.NaN;
      } else {
         AnchorRoutePlanner.Mode routedSearching = this.getRouteMode();
         if (routedSearching == AnchorRoutePlanner.Mode.UNREACHABLE) {
            return Double.POSITIVE_INFINITY;
         } else if (routedSearching == AnchorRoutePlanner.Mode.DIRECT) {
            return this.directDistanceTicks;
         } else if ((routedSearching == AnchorRoutePlanner.Mode.ROUTED || routedSearching == AnchorRoutePlanner.Mode.PARTIAL) && this.routeNodes != null && Double.isFinite(this.routeEstimateTicks)) {
            int routedValue = Math.max(1, Math.min(this.routeNodes.size(), this.routeFollower.getCurrentNodeIndex()));
            double routedValue2 = this.routeNodes.get(routedValue - 1).g();
            return Math.max(0.0, this.routeEstimateTicks - routedValue2);
         } else {
            return Double.NaN;
         }
      }
   }

   public AnchorRoutePlanner(AStarPathfinder.World api, PathGoals.Los handler, AStarPathfinder.Danger danger) {
      this(api, handler, danger, api == null);
   }

   private void applyRoute(long time, AStarPathfinder.Result result, AnchorRoutePlanner.Mode routedSearchingType) {
      this.activeSearch = null;
      this.routeNodes = result.nodes().isEmpty() ? null : result.nodes();
      this.routeResultMode = routedSearchingType;
      this.routeEstimateTicks = result.ticks();
      this.routeAnchorPos = this.searchTarget.anchor();
      this.routeKind = this.searchTarget.kind();
      this.routeSearchTick = time;
      this.routeFollower.reset();
      if (routedSearchingType != AnchorRoutePlanner.Mode.UNREACHABLE && time >= this.unreachableUntilTick) {
         this.unreachableSinceTick = Long.MIN_VALUE;
      }
   }

   private boolean isRouteStillValid() {
      List list = this.routeNodes;
      if (list == null) {
         return false;
      } else {
         int apiFireValue = Math.max(1, this.routeFollower.getCurrentNodeIndex());
         boolean fireResistant = this.immuneFireData.fireResistant();

         for (int i = apiFireValue; i < list.size() && i < apiFireValue + 3; i++) {
            AStarPathfinder.Node node = (AStarPathfinder.Node)list.get(i);
            double groundY = AStarPathfinder.findGroundY(this.world, node.x(), node.z(), node.feetY(), 0.6, 1);
            if (!Double.isFinite(groundY) || Math.abs(groundY - node.feetY()) > 0.3) {
               return false;
            }

            if (AStarPathfinder.isHazardousCell(this.world.getBlockFlags(node.x(), node.y(), node.z()), fireResistant)) {
               return false;
            }
         }

         return true;
      }
   }

   public String getDebugString() {
      double amountRouted = this.getEstimatedTicksRemaining();
      List list = this.routeNodes;
      String appendText = Double.isFinite(amountRouted) ? String.format(Locale.ROOT, "%.1f", amountRouted) : "-";
      String appendText2 = list == null ? "-" : String.format(Locale.ROOT, "%.1f", this.getRemainingRouteHp());
      return this.getRouteMode().name()
         + ":"
         + appendText
         + ":"
         + (list == null ? 0 : list.size())
         + ":"
         + this.getMaxDrop()
         + ":"
         + appendText2
         + ":"
         + this.getTimeUnreachable();
   }

   public float[] getRouteLookTarget(float routedPartialValue) {
      AnchorRoutePlanner.Mode routedSearching = this.getRouteMode();
      if (routedSearching != AnchorRoutePlanner.Mode.ROUTED && routedSearching != AnchorRoutePlanner.Mode.PARTIAL) {
         return null;
      } else {
         PathFollower.Cmd lastWalkCommand37 = this.lastWalkCommand;
         if (lastWalkCommand37 == null || !lastWalkCommand37.move() || !(lastWalkCommand37.remaining() >= 6.0)) {
            return null;
         } else if (!Float.isFinite(routedPartialValue)) {
            return null;
         } else {
            float degrees = (float)Math.toDegrees(Math.atan2(-lastWalkCommand37.dx(), lastWalkCommand37.dz()));
            float routedPartialValue2 = ((degrees - routedPartialValue) % 360.0F + 540.0F) % 360.0F - 180.0F;
            return new float[]{routedPartialValue + routedPartialValue2, 12.0F};
         }
      }
   }

   int getSearchCount() {
      return this.searchCount;
   }

   @Override
   public PlayerCombatState getImmuneFire() {
      return this.directOnly ? PlayerCombatState.INSTANCE : this.immuneFireData;
   }

   private void pruneExpiredBlockedCells(long time) {
      if (!this.blockedCellExpiryTicks.isEmpty()) {
         if (this.lastPruneTick == Long.MIN_VALUE || time - this.lastPruneTick >= 20L || time < this.lastPruneTick) {
            this.lastPruneTick = time;
            this.blockedCellExpiryTicks.long2LongEntrySet().removeIf(tickx -> tickx.getLongValue() <= time);
         }
      }
   }

   @Override
   public int getTimeUnreachable() {
      if (!this.directOnly && this.getRouteMode() == AnchorRoutePlanner.Mode.UNREACHABLE && this.unreachableSinceTick != Long.MIN_VALUE) {
         long unreachableBound = this.latestTick - this.unreachableSinceTick + 1L;
         return unreachableBound <= 0L ? 0 : (int)Math.min(2147483647L, unreachableBound);
      } else {
         return 0;
      }
   }

   private void startNewSearch(long time, Vec3d vec, Vec3d vec2, boolean vecFlag, PreferAnchorData vecData) {
      double[] api = this.findStartCell(vec, vec2, vecFlag);
      if (api != null) {
         this.searchStartX = (int)api[0];
         this.searchStartZ = (int)api[1];
         this.searchStartY = api[2];
         this.searchStartTick = time;
         this.searchTarget = vecData;
         this.anchorRetryUsed = false;
         this.meleeRetryCount = 0;
         this.startSearch(this.buildGoalForTarget(time, vecData, 16));
      }
   }

   private boolean isTargetReachable(long time, Vec3d vec, PreferAnchorData preferAnchorData, Vec3d vec2) {
      double meleeKindValue = PathFollower.traceGroundHeight(this.world, vec, vec2, true, 16.0, this.maxDrop);
      if (Double.isNaN(meleeKindValue)) {
         return false;
      } else {
         int hashRegistryValue = (int)Math.floor(vec2.x);
         int z2 = (int)Math.floor(vec2.z);
         double groundY = AStarPathfinder.findGroundY(this.world, hashRegistryValue, z2, vec2.y, 0.6, 1);
         if (!Double.isFinite(groundY) || Math.abs(groundY - vec2.y) > 0.600000001) {
            return false;
         } else if (!isHeightReachable(meleeKindValue, groundY, Math.hypot(vec2.x - vec.x, vec2.z - vec.z))) {
            return false;
         } else if (preferAnchorData.kind() == PreferAnchorData.Kind.CELL) {
            return true;
         } else {
            Vec3d vec3d = preferAnchorData.anchor();
            if (preferAnchorData.kind() == PreferAnchorData.Kind.MELEE_LOW && !isWithinStepHeight(groundY, vec3d.y)) {
               return false;
            } else {
               boolean flag = this.isCrystalAnchorKnown(time, preferAnchorData);
               if (preferAnchorData.kind() == PreferAnchorData.Kind.CRYSTAL && !flag && !isWithinStepHeight(groundY, vec3d.y - 1.0)) {
                  return false;
               } else {
                  long packPosKey = AStarPathfinder.packPosKey(hashRegistryValue, AStarPathfinder.toBlockY(groundY), z2);
                  if (packPosKey == this.cachedCellKey
                     && preferAnchorData.kind() == this.cachedKind
                     && this.cachedAnchor != null
                     && time >= this.cachedAtTick
                     && time - this.cachedAtTick < 5L
                     && this.cachedAnchor.squaredDistanceTo(vec3d) <= 0.0625
                     && flag == this.cachedCrystalNear) {
                     return this.cachedReachable;
                  } else {
                     AStarPathfinder.Goal handlerAnchor = this.buildGoalForTarget(time, preferAnchorData, 1);
                     boolean flag2 = handlerAnchor != null && handlerAnchor.isGoalReached(hashRegistryValue, groundY, z2);
                     this.cachedCellKey = packPosKey;
                     this.cachedKind = preferAnchorData.kind();
                     this.cachedAnchor = vec3d;
                     this.cachedAtTick = time;
                     this.cachedReachable = flag2;
                     this.cachedCrystalNear = flag;
                     return flag2;
                  }
               }
            }
         }
      }
   }

   private void advanceSearch(long hashOpenValue) {
      int reachedApiLimit = this.searchStepBudget;
      int hashOpenCounter = 0;

      while (this.activeSearch != null && reachedApiLimit > 0 && hashOpenCounter++ < 4) {
         int heapPopCount = this.activeSearch.getHeapPopCount();
         boolean reachedApi = this.activeSearch.runIterations(reachedApiLimit);
         reachedApiLimit -= Math.max(1, this.activeSearch.getHeapPopCount() - heapPopCount);
         if (!reachedApi) {
            return;
         }

         this.onSearchResult(hashOpenValue, this.activeSearch.getResult());
      }
   }

   static boolean isWithinStepHeight(double vecHashValue, double groundY) {
      return !Double.isFinite(groundY) || vecHashValue <= groundY + 0.6 + 1.0E-9;
   }

   private void recomputeLimits(PlayerCombatState immuneFireData) {
      int index = HuntEhpTables.clampStyleIndex(immuneFireData.style());
      float ehpValue = immuneFireData.totem() ? 4.0F : 8.0F;
      float ehpValue2 = Float.isFinite(immuneFireData.ehp()) ? immuneFireData.ehp() : 0.0F;
      this.hpBudget = Math.max(0.0F, Math.min(HuntEhpTables.FIRE_IMMUNE_CAP_BY_STYLE[index], ehpValue2 - ehpValue));
      this.maxDrop = FallDamageMath.getMaxSafeFallDistance(immuneFireData.fallEpf(), immuneFireData.resistanceAmp(), this.hpBudget, 26);
      this.ticksPerHp = HuntEhpTables.EHP_BY_STYLE[index];
      this.gapsAllowed = !immuneFireData.usingItem();
      this.sprintGapsAllowed = immuneFireData.food() > 6 && !immuneFireData.usingItem() && immuneFireData.pingMs() < 150;
   }

   private AnchorRoutePlanner(AStarPathfinder.World api, PathGoals.Los handler, AStarPathfinder.Danger danger, boolean flag) {
      this.searchStepBudget = 300;
      this.maxExpandedNodes = 2400;
      this.immuneFireData = PlayerCombatState.INSTANCE;
      this.maxDrop = 3;
      this.ticksPerHp = HuntEhpTables.EHP_BY_STYLE[0];
      this.gapsAllowed = true;
      this.sprintGapsAllowed = true;
      this.currentMode = AnchorRoutePlanner.Mode.IDLE;
      this.latestTick = Long.MIN_VALUE;
      this.lastPruneTick = Long.MIN_VALUE;
      this.routeResultMode = AnchorRoutePlanner.Mode.IDLE;
      this.routeSearchTick = Long.MIN_VALUE;
      this.routeEstimateTicks = Double.NaN;
      this.unreachableSinceTick = Long.MIN_VALUE;
      this.unreachableUntilTick = Long.MIN_VALUE;
      this.directDistanceTicks = Double.NaN;
      this.cachedCellKey = Long.MIN_VALUE;
      this.cachedAtTick = Long.MIN_VALUE;
      this.lowAnchorUntilTick = Long.MIN_VALUE;
      this.crystalAnchorUntilTick = Long.MIN_VALUE;
      this.world = api;
      this.lineOfSight = handler;
      this.dangerModel = danger == null ? AStarPathfinder.Danger.INSTANCE : danger;
      this.directOnly = flag;
      this.blockedCellExpiryTicks.defaultReturnValue(Long.MIN_VALUE);
      this.recomputeLimits(PlayerCombatState.INSTANCE);
      if (flag) {
         this.maxDrop = 3;
      }
   }

   @Override
   public float getRemainingRouteHp() {
      List list = this.routeNodes;
      if (!this.directOnly && list != null) {
         int vecValue = Math.max(1, this.routeFollower.getCurrentNodeIndex());
         float vecFlag = 0.0F;

         for (int i = vecValue; i < list.size(); i++) {
            vecFlag += ((AStarPathfinder.Node)list.get(i)).hp();
         }

         return vecFlag;
      } else {
         return 0.0F;
      }
   }

   static boolean isVeryFarFrom(Vec3d vec, Vec3d vec2) {
      return vec != null && vec2 != null ? vec.distanceTo(vec2) > 4.0 || Math.abs(vec.y - vec2.y) >= 0.9 : vec != vec2;
   }

   @Override
   public int getMaxDrop() {
      return this.directOnly ? 3 : this.maxDrop;
   }

   static boolean hasAnchorShifted(Vec3d vec, Vec3d vec2) {
      return vec != null && vec2 != null ? Math.hypot(vec.x - vec2.x, vec.z - vec2.z) > 1.25 || isFarFrom(vec, vec2) : vec != vec2;
   }

   private void clearRoute() {
      this.routeNodes = null;
      this.routeResultMode = AnchorRoutePlanner.Mode.IDLE;
      this.routeEstimateTicks = Double.NaN;
      this.routeFollower.reset();
   }

   public static enum Mode {
      IDLE,
      DIRECT,
      SEARCHING,
      ROUTED,
      PARTIAL,
      UNREACHABLE;
   }
}
