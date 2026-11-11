package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import com.corz.client.utils.SupportStandUtils;
import com.corz.client.utils.Failures;

public final class PosesBoundaryUtils {
   public static final String CONSTRUCTIBLE_POSES_DIRT = "constructible poses: a dirt floor makes them standable";
   public static final String FUTURE_STAND_BOUNDARY = "future-stand boundary";
   public static final String FOOT_LAYER_POSES = "foot-layer poses: the head-level boundary was refuted";
   public static final String HEAD_LEVEL_POSES = "head-level poses: no stand at the family's layer or one above can exist";
   public static final String EDGE_BRIDGE_DESCENT = "edge-bridge descent";
   public static final String SERVICE_ACCESS = "service-access";

   static Set<SupportStandUtils.Cell> getSet(Collection<SupportStandUtils.Cell> collection, Collection<SupportStandUtils.Cell> collection2, int posesBoundaryValue) {
      var linkedHashSet = new LinkedHashSet<>(getSet(collection, posesBoundaryValue));

      for (SupportStandUtils.Cell cell : collection) {
         SupportStandUtils.Cell blockPos = null;
         int posesBoundaryLimit = Integer.MAX_VALUE;

         for (SupportStandUtils.Cell cell3 : collection2) {
            int posesBoundaryValue2 = Math.abs(cell3.x() - cell.x()) + Math.abs(cell3.y() - cell.y()) + Math.abs(cell3.z() - cell.z());
            if (blockPos == null || posesBoundaryValue2 < posesBoundaryLimit || posesBoundaryValue2 == posesBoundaryLimit && cell3.getTime() < blockPos.getTime()) {
               blockPos = cell3;
               posesBoundaryLimit = posesBoundaryValue2;
            }
         }

         if (blockPos != null) {
            int x = blockPos.x();
            int y = blockPos.y();
            int z = blockPos.z();
            linkedHashSet.add(blockPos);

            while (x != cell.x()) {
               x += Integer.signum(cell.x() - x);
               linkedHashSet.add(new SupportStandUtils.Cell(x, y, z));
            }

            while (z != cell.z()) {
               z += Integer.signum(cell.z() - z);
               linkedHashSet.add(new SupportStandUtils.Cell(x, y, z));
            }

            while (y != cell.y()) {
               y += Integer.signum(cell.y() - y);
               linkedHashSet.add(new SupportStandUtils.Cell(x, y, z));
            }
         }
      }

      return linkedHashSet;
   }

   public static PosesBoundaryUtils.Plan getAppendData(
      SupportStandUtils.World world,
      SupportStandUtils.Cell cell,
      Collection<SupportStandUtils.Cell> collection,
      Set<SupportStandUtils.Cell> set,
      SupportStandUtils.Options options,
      boolean appendFlag
   ) {
      return getRemainingCells(world, cell, collection, set, options, appendFlag, false);
   }

   static int getMembersReach(
      SupportStandUtils.World world,
      PosesBoundaryUtils.Family family,
      Set<SupportStandUtils.Cell> set,
      SupportStandUtils.Options options,
      SupportStandUtils.Cell cell
   ) {
      return getValue(getSchematicPlain(world, family, set == null ? Set.of() : set), cell, family.members(), options.reach());
   }

   static int getReach(SupportStandUtils.Options options) {
      return (int)Math.ceil(options.reach()) + 2;
   }

   private static SupportStandUtils.Cell getDataForNavList(SupportStandUtils.Nav nav, List<SupportStandUtils.Cell> list, Map<SupportStandUtils.Cell, Integer> map) {
      SupportStandUtils.Cell cell = null;
      double posesBoundaryValue = Double.POSITIVE_INFINITY;
      int integer2 = -1;

      for (SupportStandUtils.Cell cell2 : list) {
         double orDefault = nav.getOrDefaultForCell(cell2);
         if (orDefault != Double.POSITIVE_INFINITY) {
            int integer = (Integer)map.get(cell2);
            if (cell == null || orDefault < posesBoundaryValue - 1.0E-6 || Math.abs(orDefault - posesBoundaryValue) <= 1.0E-6 && integer > integer2) {
               cell = cell2;
               posesBoundaryValue = orDefault;
               integer2 = integer;
            }
         }
      }

      return cell;
   }

   private static SupportStandUtils.Nav getFlagBudget(
      SupportStandUtils.Overlay overlay,
      SupportStandUtils.Cell cell,
      SupportStandUtils.Options options,
      Set<SupportStandUtils.Cell> set,
      Set<SupportStandUtils.Cell> linkedHashSet,
      boolean budgetFlag
   ) {
      boolean flag = overlay.budgetActive;
      if (budgetFlag) {
         overlay.budgetActive = true;
      }

      SupportStandUtils.Nav flagBudget;
      try {
         flagBudget = SupportStandUtils.Nav.getTrailWalk(overlay, cell, options, options.dirtBudget(), set, linkedHashSet);
      } finally {
         overlay.budgetActive = flag;
      }

      return flagBudget;
   }

   static int getValue(SupportStandUtils.Overlay overlay, SupportStandUtils.Cell cell, List<SupportStandUtils.Cell> list, double posesBoundaryValue) {
      return getValue(overlay, cell, list, posesBoundaryValue, false);
   }

   private static List<SupportStandUtils.Cell> getMembersReach(
      SupportStandUtils.Overlay overlay,
      PosesBoundaryUtils.Family family,
      SupportStandUtils.Options options,
      Predicate<SupportStandUtils.Cell> predicate,
      Map<SupportStandUtils.Cell, Integer> map,
      boolean membersReachFlag
   ) {
      int ceilReach = (int)Math.ceil(options.reach());
      var hashSet = new HashSet();
      var arrayList = new ArrayList();

      for (SupportStandUtils.Cell cell : family.members()) {
         for (int i = -3; i <= 2; i++) {
            for (int j = -ceilReach; j <= ceilReach; j++) {
               for (int k = -ceilReach; k <= ceilReach; k++) {
                  SupportStandUtils.Cell dataX = cell.getDataX(j, i, k);
                  if (hashSet.add(dataX) && predicate.test(dataX) && !overlay.canLavaDirt(dataX)) {
                     SupportStandUtils.Cell data222 = dataX.getData222();
                     if (overlay.canFlagForCell(dataX)
                        && overlay.canFlagForCell(dataX.getDataCell())
                        && overlay.canDirtLava(data222, options)
                        && !data222.equals(cell)
                        && !family.members().contains(data222)) {
                        overlay.handleInstance(new SupportStandUtils.Diff(List.of(data222), List.of()));

                        try {
                           if (overlay.canLavaDirt(dataX)) {
                              int membersReachLimit = getValue(overlay, dataX, family.members(), options.reach(), membersReachFlag);
                              if (membersReachLimit > 0) {
                                 map.put(dataX, membersReachLimit);
                                 arrayList.add(dataX);
                              }
                           }
                        } finally {
                           overlay.updateInstance();
                        }
                     }
                  }
               }
            }
         }
      }

      return arrayList;
   }

   public static List<SupportStandUtils.Cell> getOptBase(
      SupportStandUtils.World world, PosesBoundaryUtils.Family family, Set<SupportStandUtils.Cell> set, SupportStandUtils.Options options, int optValue
   ) {
      Objects.requireNonNull(world, "base");
      Objects.requireNonNull(options, "opt");
      return getMembersReach(world, family, set, options, optValue).poses();
   }

   private static List<SupportStandUtils.Cell> getItems(
      SupportStandUtils.Overlay overlay,
      PosesBoundaryUtils.Family family,
      SupportStandUtils.Options options,
      Set<SupportStandUtils.Cell> set,
      Set<SupportStandUtils.Cell> set2,
      Map<SupportStandUtils.Cell, Integer> map,
      boolean posesBoundaryFlag,
      boolean[] posesBoundaryFlags
   ) {
      posesBoundaryFlags[0] = posesBoundaryFlags[1] = false;
      List membersReach = getMembersReachForOverlayFamily(overlay, family, options, cell -> !set.contains(cell) && !set2.contains(cell), map, posesBoundaryFlag);
      if (membersReach.isEmpty()) {
         membersReach = getMembersReachForOverlayFamily(overlay, family, options, cell2 -> true, map, posesBoundaryFlag);
         posesBoundaryFlags[0] = !membersReach.isEmpty();
      }

      if (membersReach.isEmpty()) {
         membersReach = getMembersReach(overlay, family, options, membersx -> !set.contains(membersx) && !set2.contains(membersx), map, posesBoundaryFlag);
         posesBoundaryFlags[1] = !membersReach.isEmpty();
      }

      return membersReach;
   }

   private static PosesBoundaryUtils.Ranking getMembersReach(
      SupportStandUtils.World world, PosesBoundaryUtils.Family family, Set<SupportStandUtils.Cell> set, SupportStandUtils.Options options, int membersReachLimit
   ) {
      Set<SupportStandUtils.Cell> set2 = set == null ? Set.of() : set;
      SupportStandUtils.Overlay schematicPlain = getSchematicPlain(world, family, set2);
      if (family != null && !family.members().isEmpty() && membersReachLimit > 0) {
         var hashSet = new HashSet<>(family.members());
         var hashMap = new HashMap<SupportStandUtils.Cell, Integer>();
         boolean[] booleans = new boolean[2];
         var list = getItems(schematicPlain, family, options, hashSet, set2, hashMap, false, booleans);
         boolean membersReachFlag = booleans[0];
         boolean membersReachFlag2 = booleans[1];
         var hashMap2 = new HashMap();

         for (SupportStandUtils.Cell cell : list) {
            hashMap2.put(cell, getValue(schematicPlain, cell, family.members(), options.reach(), true));
         }

         double[] membersReachValues = getAmounts(family.members());
         list.sort((compliantCov, cen) -> {
            int var5x = Integer.compare((Integer)hashMap2.get(cen), (Integer)hashMap2.get(compliantCov));
            if (var5x != 0) {
               return var5x;
            } else {
               int var6x = Integer.compare((Integer)hashMap.get(cen), (Integer)hashMap.get(compliantCov));
               if (var6x != 0) {
                  return var6x;
               } else {
                  int var7x = Double.compare(getAmount(compliantCov, membersReachValues), getAmount(cen, membersReachValues));
                  return var7x != 0 ? var7x : Long.compare(compliantCov.getTime(), cen.getTime());
               }
            }
         });
         boolean membersReachFlag3 = !list.isEmpty() && (Integer)hashMap2.get(list.get(0)) == 0;
         var copyOf = List.copyOf(list);
         return new PosesBoundaryUtils.Ranking(
            copyOf.size() > membersReachLimit ? copyOf.subList(0, membersReachLimit) : copyOf, copyOf, hashMap, membersReachFlag, schematicPlain, membersReachFlag2, membersReachFlag3
         );
      } else {
         return new PosesBoundaryUtils.Ranking(List.of(), List.of(), Map.of(), false, schematicPlain, false, false);
      }
   }

   private static SupportStandUtils.Overlay getSchematicPlain(SupportStandUtils.World world, PosesBoundaryUtils.Family family, Set<SupportStandUtils.Cell> set) {
      var linkedHashSet = new LinkedHashSet();
      if (family != null) {
         linkedHashSet.addAll(family.members());
      }

      var arrayList = new ArrayList(set.size());

      for (SupportStandUtils.Cell cell : set) {
         if (cell != null && !linkedHashSet.contains(cell)) {
            arrayList.add(cell);
         }
      }

      arrayList.sort((obj, obj2) -> Long.compare(((com.corz.client.utils.SupportStandUtils.Cell) obj).getTime(), ((com.corz.client.utils.SupportStandUtils.Cell) obj2).getTime()));
      linkedHashSet.addAll(arrayList);
      var arrayList2 = new ArrayList(linkedHashSet.size());

      for (SupportStandUtils.Cell cell2 : (Iterable<com.corz.client.utils.SupportStandUtils.Cell>) (linkedHashSet)) {
         arrayList2.add(new SupportStandUtils.Target(cell2, "schematic", SupportStandUtils.Kind.PLAIN, -1, SupportStandUtils.Shape.FULL, false, null));
      }

      return new SupportStandUtils.Overlay(world, arrayList2);
   }

   private static double getAmount(SupportStandUtils.Cell cell, double[] posesBoundaryValues) {
      return Math.max(Math.abs(cell.x() - posesBoundaryValues[0]), Math.max(Math.abs(cell.y() - posesBoundaryValues[1]), Math.abs(cell.z() - posesBoundaryValues[2])));
   }

   private static PosesBoundaryUtils.Plan getRemainingCells(
      SupportStandUtils.World world,
      SupportStandUtils.Cell cell,
      Collection<SupportStandUtils.Cell> collection,
      Set<SupportStandUtils.Cell> set,
      SupportStandUtils.Options options,
      boolean remainingCellsFlag,
      boolean budgetFlag
   ) {
      PosesBoundaryUtils.Plan remainingCells = null;
      PosesBoundaryUtils.Plan posesAccess2 = null;
      PosesBoundaryUtils.Plan plan = null;

      for (PosesBoundaryUtils.Family family : getCopyOf(collection)) {
         PosesBoundaryUtils.Plan posesAccess = getPosesAccess(world, cell, family, set, options, remainingCellsFlag, budgetFlag);
         switch (posesAccess.kind()) {
            case ALREADY_CONNECTED:
               if (remainingCells == null) {
                  remainingCells = posesAccess;
               }
               break;
            case CONNECT:
               return posesAccess;
            case NOT_ACCESS_PROBLEM:
               if (plan == null) {
                  plan = posesAccess;
               }
               break;
            case IMPOSSIBLE:
               if (posesAccess2 == null) {
                  posesAccess2 = posesAccess;
               }
         }
      }

      if (remainingCells != null) {
         return remainingCells;
      } else if (posesAccess2 != null) {
         return posesAccess2;
      } else {
         return plan != null
            ? plan
            : new PosesBoundaryUtils.Plan(
               PosesBoundaryUtils.Kind.NOT_ACCESS_PROBLEM, Long.MIN_VALUE, 0, List.of(), null, null, 0, List.of(), "no remaining cells"
            );
      }
   }

   public static PosesBoundaryUtils.Plan getAppendData(
      SupportStandUtils.World world,
      SupportStandUtils.Cell cell,
      PosesBoundaryUtils.Family family,
      Set<SupportStandUtils.Cell> set,
      SupportStandUtils.Options options
   ) {
      return getPosesAccess(world, cell, family, set, options, false, false);
   }

   public static PosesBoundaryUtils.Plan getAppendData(
      SupportStandUtils.World world,
      SupportStandUtils.Cell cell,
      Collection<SupportStandUtils.Cell> collection,
      Set<SupportStandUtils.Cell> set,
      SupportStandUtils.Options options
   ) {
      return getAppendData(world, cell, collection, set, options, false);
   }

   private PosesBoundaryUtils() {
   }

   private static Set<SupportStandUtils.Cell> getUnmodifiableSet(Collection<SupportStandUtils.Cell> collection) {
      var arrayList = new ArrayList(collection);
      arrayList.sort((obj, obj2) -> Long.compare(((com.corz.client.utils.SupportStandUtils.Cell) obj).getTime(), ((com.corz.client.utils.SupportStandUtils.Cell) obj2).getTime()));
      return Collections.unmodifiableSet(new LinkedHashSet<>(arrayList));
   }

   private static PosesBoundaryUtils.Ranking getMembers(
      SupportStandUtils.World world, PosesBoundaryUtils.Family family, Set<SupportStandUtils.Cell> set, SupportStandUtils.Options options, int membersValue
   ) {
      Set<SupportStandUtils.Cell> set2 = set == null ? Set.of() : set;
      SupportStandUtils.Overlay schematicPlain = getSchematicPlain(world, family, set2);
      var hashSet = new HashSet<>(family.members());
      var hashMap = new HashMap();
      List membersReach = getMembersReachForOverlayFamily(schematicPlain, family, options, cell -> true, hashMap, true);
      List supplier = getMembersReach(schematicPlain, family, options, tickx -> !hashSet.contains(tickx) && !set2.contains(tickx), hashMap, true);
      boolean membersFlag = false;

      for (SupportStandUtils.Cell cell3 : (Iterable<com.corz.client.utils.SupportStandUtils.Cell>) (membersReach)) {
         if (hashSet.contains(cell3) || set2.contains(cell3)) {
            membersFlag = true;
            break;
         }
      }

      var arrayList = new ArrayList(membersReach);
      arrayList.addAll(supplier);
      double[] membersValues = getAmounts(family.members());
      arrayList.sort((coverage, cen) -> {
         int compare = Integer.compare((Integer)hashMap.get(cen), (Integer)hashMap.get(coverage));
         if (compare != 0) {
            return compare;
         } else {
            int var5x = Double.compare(getAmount(((com.corz.client.utils.SupportStandUtils.Cell) (coverage)), membersValues), getAmount(((com.corz.client.utils.SupportStandUtils.Cell) (cen)), membersValues));
            return var5x != 0 ? var5x : Long.compare(((com.corz.client.utils.SupportStandUtils.Cell) coverage).getTime(), ((com.corz.client.utils.SupportStandUtils.Cell) cen).getTime());
         }
      });
      var copyOf = List.copyOf(arrayList);
      return new PosesBoundaryUtils.Ranking(copyOf.size() > membersValue ? copyOf.subList(0, membersValue) : copyOf, copyOf, hashMap, membersFlag, schematicPlain, !supplier.isEmpty(), false);
   }

   public static int[] getValues(double posesBoundaryValue) {
      int ceilReach = (int)Math.ceil(posesBoundaryValue);
      int posesBoundaryValue2 = ceilReach + 2;
      return new int[]{Math.max(2 * ceilReach + 2, 10 + ceilReach + 1), 2 + posesBoundaryValue2, 27 + posesBoundaryValue2};
   }

   private static double[] getAmounts(List<SupportStandUtils.Cell> list) {
      double posesBoundaryTotal = 0.0;
      double posesBoundaryTotal2 = 0.0;
      double posesBoundaryTotal3 = 0.0;

      for (SupportStandUtils.Cell cell : list) {
         posesBoundaryTotal += cell.x();
         posesBoundaryTotal2 += cell.y();
         posesBoundaryTotal3 += cell.z();
      }

      int posesBoundaryDivisor = Math.max(1, list.size());
      return new double[]{posesBoundaryTotal / posesBoundaryDivisor, posesBoundaryTotal2 / posesBoundaryDivisor, posesBoundaryTotal3 / posesBoundaryDivisor};
   }

   public static PosesBoundaryUtils.Plan getAppendDataForWorldCell(
      SupportStandUtils.World world,
      SupportStandUtils.Cell cell,
      Collection<SupportStandUtils.Cell> collection,
      Set<SupportStandUtils.Cell> set,
      SupportStandUtils.Options options,
      boolean appendFlag
   ) {
      return getRemainingCells(world, cell, collection, set, options, appendFlag, true);
   }

   private static SupportStandUtils.Step getWalk(SupportStandUtils.Cell cell) {
      return new SupportStandUtils.Step(SupportStandUtils.Op.WALK, cell, cell, 0, null, "");
   }

   public static PosesBoundaryUtils.Plan getAppendData(
      SupportStandUtils.World world,
      SupportStandUtils.Cell cell,
      PosesBoundaryUtils.Family family,
      Set<SupportStandUtils.Cell> set,
      SupportStandUtils.Options options,
      boolean appendFlag
   ) {
      return getPosesAccess(world, cell, family, set, options, appendFlag, false);
   }

   static SupportStandUtils.Options getBannedBudget(SupportStandUtils.Options options) {
      SupportStandUtils.Options options2 = new SupportStandUtils.Options(
         options.reach(), options.dirtBudget() == 0 ? 0 : Math.max(options.dirtBudget(), 16), options.maxPillar(), false, false, false, Math.max(options.windowMargin(), 6)
      );
      return options2.getAllowStands(options.bannedIntents())
         .getAllowStandsForSet(options.bannedPairs())
         .getAllowBanned(options.unreachableStands())
         .getAllowStandsForFlag(options.edgeSneak())
         .getAllowStands(options.egressGuard());
   }

   static String formatText(SupportStandUtils.Cell cell) {
      return cell.x() + "," + cell.y() + "," + cell.z();
   }

   static boolean canFlag(SupportStandUtils.World world, Collection<SupportStandUtils.Cell> collection, Set<SupportStandUtils.Cell> set, int posesBoundaryValue) {
      for (SupportStandUtils.Cell cell : collection) {
         if (world.canOwnDirt(cell.getData222())) {
            return false;
         }
      }

      int[] posesBoundaryValues = SupportStandUtils.Nav.getValues(set);

      for (int i = posesBoundaryValues[0] - posesBoundaryValue; i <= posesBoundaryValues[3] + posesBoundaryValue; i++) {
         for (int j = posesBoundaryValues[2] - posesBoundaryValue; j <= posesBoundaryValues[5] + posesBoundaryValue; j++) {
            for (int k = posesBoundaryValues[1] - 4; k <= posesBoundaryValues[4] + 4; k++) {
               if (world.canOwnDirt(new SupportStandUtils.Cell(i, k, j))) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   static boolean canMembers(SupportStandUtils.World world, PosesBoundaryUtils.Family family, SupportStandUtils.Cell cell) {
      boolean members = false;

      for (SupportStandUtils.Cell membersItem : family.members()) {
         if (world.getCellBoxes(membersItem).isEmpty()) {
            if (membersItem.y() >= cell.y() - 1) {
               return false;
            }

            members = true;
         }
      }

      return members;
   }

   private static PosesBoundaryUtils.Plan getPosesAccess(
      SupportStandUtils.World world,
      SupportStandUtils.Cell cell,
      PosesBoundaryUtils.Family family,
      Set<SupportStandUtils.Cell> set,
      SupportStandUtils.Options options,
      boolean posesAccessFlag,
      boolean budgetFlag
   ) {
      Objects.requireNonNull(world, "base");
      Objects.requireNonNull(cell, "feet");
      Objects.requireNonNull(options, "opt");
      if (family != null && !family.members().isEmpty()) {
         PosesBoundaryUtils.Ranking membersReach = getMembersReach(world, family, set, options, 24);
         boolean posesBoundaryFlag = false;
         if (posesAccessFlag && membersReach.headLevel() && options.dirtBudget() > 0) {
            PosesBoundaryUtils.Ranking members = getMembers(world, family, set, options, 24);
            if (!members.poses().isEmpty()) {
               membersReach = members;
               posesBoundaryFlag = true;
            }
         }

         var poses = membersReach.poses();
         if (poses.isEmpty()) {
            return new PosesBoundaryUtils.Plan(
               PosesBoundaryUtils.Kind.NOT_ACCESS_PROBLEM,
               family.root(),
               0,
               List.of(),
               null,
               null,
               0,
               List.of(),
               "no pose within reach of any member: geometry/support, not access"
            );
         } else {
            String posesAccessText = (
                  membersReach.future() ? " (future-stand boundary)" : (membersReach.constructible() ? " (constructible poses: a dirt floor makes them standable)" : "")
               )
               + (membersReach.headLevel() ? " (head-level poses: no stand at the family's layer or one above can exist)" : "")
               + (posesBoundaryFlag ? " (foot-layer poses: the head-level boundary was refuted)" : "");
            var linkedHashSet = new LinkedHashSet<>(family.members());
            linkedHashSet.addAll(poses);
            var optFeet = getOptFeet(world, cell, linkedHashSet, options);
            Set optFeet2 = world.canOwnDirt(cell.getData222()) ? optFeet : null;

            for (SupportStandUtils.Cell posesItem : poses) {
               if (optFeet.contains(posesItem)) {
                  if (optFeet2 == null) {
                     optFeet2 = getOptFeet(getApiForWorld(world), cell, linkedHashSet, options);
                  }

                  if (optFeet2.contains(posesItem)) {
                     int coverage = membersReach.coverage().get(posesItem);
                     return new PosesBoundaryUtils.Plan(
                        PosesBoundaryUtils.Kind.ALREADY_CONNECTED,
                        family.root(),
                        coverage,
                        getItems(membersReach.overlay(), posesItem, family.members(), options.reach(), posesBoundaryFlag),
                        posesItem,
                        null,
                        0,
                        poses,
                        "boundary "
                           + posesItem
                           + " is in the walk component from "
                           + cell
                           + "; serves "
                           + coverage
                           + " of "
                           + family.getMembers()
                           + " members; no construction"
                           + posesAccessText
                     );
                  }
               }
            }

            SupportStandUtils.Options bannedBudget = getBannedBudget(options);
            Object obj = optFeet;
            if (budgetFlag) {
               var stands = new LinkedHashSet<>(bannedBudget.unreachableStands());
               var arrayList = new ArrayList();

               for (SupportStandUtils.Cell optFeetItem : optFeet) {
                  if (optFeetItem.y() < cell.y()) {
                     stands.add(optFeetItem.getTime());
                  } else {
                     arrayList.add(optFeetItem);
                  }
               }

               bannedBudget = bannedBudget.getAllowBanned(stands);
               obj = arrayList;
            }

            var linkedHashSet3 = new LinkedHashSet(linkedHashSet);
            linkedHashSet3.add(cell);
            SupportStandUtils.Overlay overlay = membersReach.overlay();
            int y = cell.y();

            for (SupportStandUtils.Cell cell4 : optFeet) {
               y = Math.min(y, cell4.y());
            }

            SupportStandUtils.Nav flagBudget = getFlagBudget(
               overlay, cell, bannedBudget, budgetFlag ? getSet(poses, (Collection<SupportStandUtils.Cell>)obj, y) : getSet(poses, y), linkedHashSet3, budgetFlag
            );
            SupportStandUtils.Cell dataForNavList = getDataForNavList(flagBudget, poses, membersReach.coverage());
            List list = poses;
            String posesAccessText2 = "";
            if (dataForNavList == null && membersReach.all().size() > poses.size()) {
               list = membersReach.all();
               linkedHashSet3.addAll(list);
               flagBudget = getFlagBudget(
                  overlay, cell, bannedBudget, budgetFlag ? getSet(list, (Collection<SupportStandUtils.Cell>)obj, y) : getSet(list, y), linkedHashSet3, budgetFlag
               );
               dataForNavList = getDataForNavList(flagBudget, list, membersReach.coverage());
               posesAccessText2 = " (beyond the top-" + poses.size() + " coverage ranking, " + list.size() + " poses searched)";
            }

            if (dataForNavList == null && bannedBudget.dirtBudget() > 0 && canMembers(world, family, cell)) {
               var collection = membersReach.all().size() > poses.size() ? membersReach.all() : poses;
               linkedHashSet3.addAll(collection);
               if (canFlag(world, optFeet, linkedHashSet3, bannedBudget.windowMargin())) {
                  overlay.budgetActive = true;

                  try {
                     flagBudget = getFlagBudget(
                        overlay, cell, bannedBudget, budgetFlag ? getSet(collection, (Collection<SupportStandUtils.Cell>)obj, y) : getSet(collection, y), linkedHashSet3, budgetFlag
                     );
                  } finally {
                     overlay.budgetActive = false;
                  }

                  dataForNavList = getDataForNavList(flagBudget, collection, membersReach.coverage());
                  if (dataForNavList != null) {
                     list = collection;
                     posesAccessText2 = posesAccessText2 + " (edge-bridge descent)";
                  }
               }
            }

            if (dataForNavList == null && posesBoundaryFlag) {
               PosesBoundaryUtils.Plan posesAccess = getPosesAccess(world, cell, family, set, options, false, budgetFlag);
               return new PosesBoundaryUtils.Plan(
                  posesAccess.kind(),
                  posesAccess.familyRoot(),
                  posesAccess.membersServed(),
                  posesAccess.servedMembers(),
                  posesAccess.boundary(),
                  posesAccess.mission(),
                  posesAccess.dirt(),
                  posesAccess.boundaryCandidates(),
                  posesAccess.why() + " (foot-layer poses out of reach: legacy ranking)"
               );
            } else if (dataForNavList == null) {
               return new PosesBoundaryUtils.Plan(
                  PosesBoundaryUtils.Kind.IMPOSSIBLE,
                  family.root(),
                  0,
                  List.of(),
                  null,
                  null,
                  0,
                  poses,
                  "no walk or construction within budget reaches a boundary pose (budget="
                     + bannedBudget.dirtBudget()
                     + " dirt, "
                     + list.size()
                     + " candidates; construction can only start within "
                     + getReach(bannedBudget)
                     + " cells of one)"
                     + posesAccessText
               );
            } else {
               int coverage2 = membersReach.coverage().get(dataForNavList);
               List poses3 = poses;
               if (!poses.contains(dataForNavList)) {
                  poses3 = new ArrayList(poses);
                  poses3.add(dataForNavList);
               }

               var arrayList2 = new ArrayList();
               SupportStandUtils.Cell cell6 = cell;
               int posesAccessTotal = 0;

               for (SupportStandUtils.Nav.Edge edge : flagBudget.getWalkTrail(dataForNavList)) {
                  switch (edge.op()) {
                     case WALK:
                        arrayList2.add(getWalk(edge.to()));
                        cell6 = edge.to();
                        break;
                     case PILLAR:
                        if (!cell6.equals(edge.from())) {
                           arrayList2.add(getWalk(edge.from()));
                        }

                        for (int i = 0; i < edge.count(); i++) {
                           overlay.handle(edge.from().getData223(i));
                        }

                        posesAccessTotal += edge.count();
                        arrayList2.add(
                           new SupportStandUtils.Step(SupportStandUtils.Op.PILLAR, edge.from(), edge.to(), edge.count(), null, "service access to " + dataForNavList)
                        );
                        cell6 = edge.to();
                        break;
                     case DIRT:
                        SupportStandUtils.Cell o = edge.placeFrom() == null ? edge.from() : edge.placeFrom();
                        if (!cell6.equals(o)) {
                           arrayList2.add(getWalk(o));
                        }

                        overlay.handle(edge.cell());
                        posesAccessTotal++;
                        arrayList2.add(new SupportStandUtils.Step(SupportStandUtils.Op.DIRT, edge.cell(), o, 1, edge.intent(), "service floor for " + dataForNavList));
                        arrayList2.add(getWalk(edge.to()));
                        cell6 = edge.to();
                        break;
                     case DIG_DOWN:
                        if (!cell6.equals(edge.from())) {
                           arrayList2.add(getWalk(edge.from()));
                        }

                        overlay.handleForCell(edge.cell());
                        arrayList2.add(new SupportStandUtils.Step(SupportStandUtils.Op.DIG_DOWN, edge.cell(), edge.to(), 1, null, "descend own column"));
                        cell6 = edge.to();
                  }
               }

               if (!cell6.equals(dataForNavList)) {
                  arrayList2.add(getWalk(dataForNavList));
               }

               SupportStandUtils.Mission mission = new SupportStandUtils.Mission(
                  SupportStandUtils.Status.SOLVED, List.copyOf(arrayList2), List.of(), List.of(), List.of(), List.of(), posesAccessTotal, "service-access"
               );
               String posesAccessText3 = "service access to "
                  + dataForNavList
                  + ": dirt="
                  + posesAccessTotal
                  + " serves "
                  + coverage2
                  + " of "
                  + family.getMembers()
                  + " members, steps="
                  + arrayList2.size()
                  + (posesAccessTotal == 0 ? " (walk only, wider window)" : "")
                  + posesAccessText2
                  + posesAccessText;
               return new PosesBoundaryUtils.Plan(
                  PosesBoundaryUtils.Kind.CONNECT,
                  family.root(),
                  coverage2,
                  getItems(membersReach.overlay(), dataForNavList, family.members(), options.reach(), posesBoundaryFlag),
                  dataForNavList,
                  mission,
                  posesAccessTotal,
                  (List<SupportStandUtils.Cell>)poses3,
                  posesAccessText3
               );
            }
         }
      } else {
         return new PosesBoundaryUtils.Plan(
            PosesBoundaryUtils.Kind.NOT_ACCESS_PROBLEM,
            family == null ? Long.MIN_VALUE : family.root(),
            0,
            List.of(),
            null,
            null,
            0,
            List.of(),
            "empty family: nothing to connect"
         );
      }
   }

   private static List<SupportStandUtils.Cell> getMembersReachForOverlayFamily(
      SupportStandUtils.Overlay overlay,
      PosesBoundaryUtils.Family family,
      SupportStandUtils.Options options,
      Predicate<SupportStandUtils.Cell> predicate,
      Map<SupportStandUtils.Cell, Integer> map,
      boolean membersReachFlag
   ) {
      int ceilReach = (int)Math.ceil(options.reach());
      var hashSet = new HashSet();
      var arrayList = new ArrayList();

      for (SupportStandUtils.Cell cell : family.members()) {
         for (int i = -3; i <= 2; i++) {
            for (int j = -ceilReach; j <= ceilReach; j++) {
               for (int k = -ceilReach; k <= ceilReach; k++) {
                  SupportStandUtils.Cell dataX = cell.getDataX(j, i, k);
                  if (hashSet.add(dataX) && predicate.test(dataX) && overlay.canLavaDirt(dataX)) {
                     int membersReachLimit = getValue(overlay, dataX, family.members(), options.reach(), membersReachFlag);
                     if (membersReachLimit > 0) {
                        map.put(dataX, membersReachLimit);
                        arrayList.add(dataX);
                     }
                  }
               }
            }
         }
      }

      return arrayList;
   }

   static int getValue(SupportStandUtils.Overlay overlay, SupportStandUtils.Cell cell, List<SupportStandUtils.Cell> list, double posesBoundaryValue, boolean posesBoundaryFlag) {
      return getItems(overlay, cell, list, posesBoundaryValue, posesBoundaryFlag).size();
   }

   public static String getAppend(PosesBoundaryUtils.Plan plan) {
      return plan == null
         ? "access -"
         : "access "
            + plan.kind()
            + " root="
            + (plan.familyRoot() == Long.MIN_VALUE ? "-" : formatText(SupportStandUtils.Cell.getDataForLong(plan.familyRoot())))
            + " served="
            + plan.membersServed()
            + " boundary="
            + (plan.boundary() == null ? "-" : formatText(plan.boundary()))
            + " dirt="
            + plan.dirt()
            + " steps="
            + (plan.mission() == null ? 0 : plan.mission().steps().size())
            + " candidates="
            + plan.boundaryCandidates().size()
            + " why="
            + plan.why().replace('\n', ' ').replace('\r', ' ');
   }

   public static Set<SupportStandUtils.Cell> getOptFeet(
      SupportStandUtils.World world, SupportStandUtils.Cell cell, Collection<SupportStandUtils.Cell> collection, SupportStandUtils.Options options
   ) {
      Objects.requireNonNull(world, "base");
      Objects.requireNonNull(cell, "feet");
      Objects.requireNonNull(options, "opt");
      var linkedHashSet = new LinkedHashSet();
      if (collection != null) {
         for (SupportStandUtils.Cell blockPos : collection) {
            if (blockPos != null) {
               linkedHashSet.add(blockPos);
            }
         }
      }

      linkedHashSet.add(cell);
      SupportStandUtils.Nav trailWalk = SupportStandUtils.Nav.getTrailWalk(new SupportStandUtils.Overlay(world, List.of()), cell, options, 0, Set.of(), linkedHashSet);
      return getUnmodifiableSet(trailWalk.getKeySet());
   }

   static SupportStandUtils.World getApiForWorld(SupportStandUtils.World world) {
      return new SupportStandUtils.World() {
         @Override
         public List<SupportStandUtils.Box> getLavaOverrideBoxes(SupportStandUtils.Cell cell) {
            return world.getLavaOverrideBoxes(cell);
         }

         @Override
         public List<SupportStandUtils.Box> getTailOverrideBoxes(SupportStandUtils.Cell cell) {
            return world.getTailOverrideBoxes(cell);
         }

         @Override
         public boolean isLavaOverrideCell(SupportStandUtils.Cell cell) {
            return world.isLavaOverrideCell(cell);
         }

         @Override
         public boolean isApiCell(SupportStandUtils.Cell cell) {
            return world.isApiCell(cell);
         }

         @Override
         public boolean canOwnDirt(SupportStandUtils.Cell cell) {
            return false;
         }

         @Override
         public List<SupportStandUtils.Box> getCellBoxes(SupportStandUtils.Cell cell) {
            return world.getCellBoxes(cell);
         }

         @Override
         public boolean isOutsideBoxesSet(SupportStandUtils.Cell cell) {
            return world.isOutsideBoxesSet(cell);
         }

         @Override
         public boolean isInteractiveCell(SupportStandUtils.Cell cell) {
            return world.isInteractiveCell(cell);
         }

         @Override
         public boolean isTailCell(SupportStandUtils.Cell cell) {
            return world.isTailCell(cell);
         }

         @Override
         public boolean isHazardCell(SupportStandUtils.Cell cell) {
            return world.isHazardCell(cell);
         }

         @Override
         public boolean isTrapCell(SupportStandUtils.Cell cell) {
            return world.isTrapCell(cell);
         }

         @Override
         public boolean isReservedCell(SupportStandUtils.Cell cell) {
            return world.isReservedCell(cell);
         }

         @Override
         public boolean isForeignCell(SupportStandUtils.Cell cell) {
            return world.isForeignCell(cell);
         }

         @Override
         public List<SupportStandUtils.Box> getExtraBoxes(SupportStandUtils.Cell cell) {
            return world.getExtraBoxes(cell);
         }
      };
   }

   static Set<SupportStandUtils.Cell> getSet(Collection<SupportStandUtils.Cell> collection, int posesBoundaryValue) {
      var linkedHashSet = new LinkedHashSet(collection);

      for (SupportStandUtils.Cell cell : collection) {
         int posesBoundaryCounter = cell.y() - 1;

         for (int i = 0; posesBoundaryCounter >= posesBoundaryValue - 1 && i < 24; i++) {
            linkedHashSet.add(new SupportStandUtils.Cell(cell.x(), posesBoundaryCounter, cell.z()));
            posesBoundaryCounter--;
         }
      }

      return linkedHashSet;
   }

   static List<SupportStandUtils.Cell> getItems(
      SupportStandUtils.Overlay overlay, SupportStandUtils.Cell cell, List<SupportStandUtils.Cell> list, double posesBoundaryValue, boolean posesBoundaryFlag
   ) {
      int ceilReach = (int)Math.ceil(posesBoundaryValue);
      double[] posesBoundaryValues = overlay.getAmounts(cell, false);
      double posesBoundaryFactor = Math.max(1.0, posesBoundaryValue - 0.6);
      double posesBoundaryValue2 = posesBoundaryFactor * posesBoundaryFactor;
      var arrayList = new ArrayList();

      for (SupportStandUtils.Cell o : list) {
         if (Math.abs(cell.x() - o.x()) <= ceilReach
            && Math.abs(cell.z() - o.z()) <= ceilReach
            && (posesBoundaryFlag ? ProofSupportUtils.canFlag(cell.y(), o.y()) : cell.y() >= o.y() - 3 && cell.y() <= o.y() + 2)
            && !cell.equals(o)
            && !cell.getDataCell().equals(o)) {
            double posesBoundaryFactor2 = posesBoundaryValues[0] - (o.x() + 0.5);
            double posesBoundaryFactor3 = posesBoundaryValues[1] - (o.y() + 0.5);
            double posesBoundaryFactor4 = posesBoundaryValues[2] - (o.z() + 0.5);
            if (!(posesBoundaryFactor2 * posesBoundaryFactor2 + posesBoundaryFactor3 * posesBoundaryFactor3 + posesBoundaryFactor4 * posesBoundaryFactor4 > posesBoundaryValue2)) {
               arrayList.add(o);
            }
         }
      }

      return List.copyOf(arrayList);
   }

   public static List<PosesBoundaryUtils.Family> getCopyOf(Collection<SupportStandUtils.Cell> collection) {
      if (collection != null && !collection.isEmpty()) {
         var linkedHashSet = new LinkedHashSet();

         for (SupportStandUtils.Cell cell : collection) {
            if (cell != null) {
               linkedHashSet.add(cell.getTime());
            }
         }

         if (linkedHashSet.isEmpty()) {
            return List.of();
         } else {
            var map = Failures.getPositionMapBounded(linkedHashSet, 1);
            var hashMap = new HashMap<Long, List<Long>>();

            for (Long long2 : (Iterable<java.lang.Long>) (linkedHashSet)) {
               hashMap.computeIfAbsent((Long)map.get(long2), long3 -> new ArrayList<>()).add(long2);
            }

            var arrayList = new ArrayList(hashMap.size());

            for (Entry<Long, List<Long>> entry : hashMap.entrySet()) {
               var list = (List)entry.getValue();
               Collections.sort(list);
               var arrayList2 = new ArrayList(list.size());

               for (Long long4 : (Iterable<java.lang.Long>) (list)) {
                  arrayList2.add(SupportStandUtils.Cell.getDataForLong(long4));
               }

               arrayList.add(new PosesBoundaryUtils.Family((Long)entry.getKey(), arrayList2));
            }

            arrayList.sort((obj, obj2) -> ((com.corz.client.utils.PosesBoundaryUtils.Family) obj).getMembers() != ((com.corz.client.utils.PosesBoundaryUtils.Family) obj2).getMembers() ? Integer.compare(((com.corz.client.utils.PosesBoundaryUtils.Family) obj2).getMembers(), ((com.corz.client.utils.PosesBoundaryUtils.Family) obj).getMembers()) : Long.compare(((com.corz.client.utils.PosesBoundaryUtils.Family) obj).root(), ((com.corz.client.utils.PosesBoundaryUtils.Family) obj2).root()));
            return List.copyOf(arrayList);
         }
      } else {
         return List.of();
      }
   }

   public record Family(long root, List<SupportStandUtils.Cell> members) {
      public int getMembers() {
         return this.members.size();
      }

      public Family(long root, List<SupportStandUtils.Cell> members) {
         members = members == null ? List.of() : List.copyOf(members);
         this.root = root;
         this.members = members;
      }
   }

   public static enum Kind {
      ALREADY_CONNECTED,
      CONNECT,
      NOT_ACCESS_PROBLEM,
      IMPOSSIBLE;
   }

   public record Plan(
      PosesBoundaryUtils.Kind kind,
      long familyRoot,
      int membersServed,
      List<SupportStandUtils.Cell> servedMembers,
      SupportStandUtils.Cell boundary,
      SupportStandUtils.Mission mission,
      int dirt,
      List<SupportStandUtils.Cell> boundaryCandidates,
      String why
   ) {
      public Plan(
         PosesBoundaryUtils.Kind kind,
         long familyRoot,
         int membersServed,
         List<SupportStandUtils.Cell> servedMembers,
         SupportStandUtils.Cell boundary,
         SupportStandUtils.Mission mission,
         int dirt,
         List<SupportStandUtils.Cell> boundaryCandidates,
         String why
      ) {
         kind = kind == null ? PosesBoundaryUtils.Kind.NOT_ACCESS_PROBLEM : kind;
         servedMembers = servedMembers == null ? List.of() : List.copyOf(servedMembers);
         boundaryCandidates = boundaryCandidates == null ? List.of() : List.copyOf(boundaryCandidates);
         why = why == null ? "" : why;
         this.kind = kind;
         this.familyRoot = familyRoot;
         this.membersServed = membersServed;
         this.servedMembers = servedMembers;
         this.boundary = boundary;
         this.mission = mission;
         this.dirt = dirt;
         this.boundaryCandidates = boundaryCandidates;
         this.why = why;
      }

      public boolean isMissionConnect() {
         return this.kind == PosesBoundaryUtils.Kind.CONNECT && this.mission != null && !this.mission.steps().isEmpty();
      }
   }

   private record Ranking(
      List<SupportStandUtils.Cell> poses,
      List<SupportStandUtils.Cell> all,
      Map<SupportStandUtils.Cell, Integer> coverage,
      boolean future,
      SupportStandUtils.Overlay overlay,
      boolean constructible,
      boolean headLevel
   ) {
   }
}
