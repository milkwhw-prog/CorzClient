package com.corz.client.utils;

import com.corz.client.utils.WalkableRegionAnalyzer.Result;
import com.corz.client.utils.WalkableRegionAnalyzer.Terrain;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import com.corz.client.utils.WalkableRegionAnalyzer;
import com.corz.client.utils.CrouchedBodyUtils;
import com.corz.client.utils.PistonStickyUtils;
import com.corz.client.utils.DepsReasonData;

public final class SupportStandUtils {
   public static final List<SupportStandUtils.Box> CHEST_BOXES = List.of(new SupportStandUtils.Box(0.0625, 0.0, 0.0625, 0.9375, 0.875, 0.9375));
   public static final List<SupportStandUtils.Box> NO_BOXES = List.of();
   public static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger();
   private static final int[] sightFacingValues = new int[]{0, 1, 2, 3, 4, 5};
   public static final SupportStandUtils.Box MIN_MAX_DATA = new SupportStandUtils.Box(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
   public static final List<SupportStandUtils.Box> FULL_BOXES = List.of(SupportStandUtils.MIN_MAX_DATA);
   private static final List<double[]> unmodifiableList;
   static final int STAND_CODE_SECONDARY = 2;
   public static final List<SupportStandUtils.Box> THIN_BOXES = List.of(new SupportStandUtils.Box(0.0, 0.0, 0.0, 1.0, 0.125, 1.0));
   private static final List<double[]> unmodifiableDoubleArrayList;
   public static int everySupportCallCount;
   public static final String SUPPORT_TEAMMATE = "SUPPORT_TEAMMATE";

   static int getValue(double supportStandLimit, double spotValue) {
      if (Math.abs(supportStandLimit) > Math.abs(spotValue)) {
         return supportStandLimit >= 0.0 ? 5 : 4;
      } else {
         return spotValue >= 0.0 ? 3 : 2;
      }
   }

   public static SupportStandUtils.Mission getCellColumn(
      SupportStandUtils.World world, Set<SupportStandUtils.Cell> set, SupportStandUtils.Cell cell, SupportStandUtils.Options options2, boolean flag
   ) {
      if (set.isEmpty()) {
         return new SupportStandUtils.Mission(SupportStandUtils.Status.EMPTY, List.of(), List.of(), List.of(), List.of(), List.of(), 0, "");
      } else {
         long cellColumnBound = options2.budgetNanos() > 0L ? System.nanoTime() + options2.budgetNanos() : Long.MAX_VALUE;
         SupportStandUtils.Options options = options2;
         SupportStandUtils.Overlay overlay = new SupportStandUtils.Overlay(world, List.of());
         overlay.costCellActive = flag;

         for (SupportStandUtils.Cell cell2 : set) {
            overlay.handle(cell2);
         }

         var linkedHashSet = new LinkedHashSet(set);
         var arrayList = new ArrayList();
         var arrayList2 = new ArrayList();
         SupportStandUtils.Cell feet2 = cell;
         int cellColumnLimit = 0;
         boolean cellColumnFlag = false;
         boolean cellColumnFlag2 = false;
         int cellColumnCounter = 0;
         int size2 = -1;
         int cellColumnValue = 0;
         int cellColumnLimit2 = 0;
         Object of = Set.of();

         while (!linkedHashSet.isEmpty() && cellColumnCounter++ < 128) {
            if (System.nanoTime() > cellColumnBound) {
               cellColumnFlag = true;
               break;
            }

            overlay.cellColumnCellSet.clear();
            SupportStandUtils.Nav trailWalk = SupportStandUtils.Nav.getTrailWalk(overlay, feet2, options, 0, linkedHashSet, linkedHashSet);
            SupportStandUtils.Nav nav = trailWalk;
            if (overlay.costCellActive) {
               overlay.costCellCellSet = getSet(overlay, trailWalk, linkedHashSet);
            }

            SupportStandUtils.Removal cellStand = getCellStand(overlay, trailWalk, options, linkedHashSet, feet2);
            if (cellStand == null && options.dirtBudget() - cellColumnLimit > 0) {
               trailWalk = SupportStandUtils.Nav.getTrailWalk(overlay, feet2, options, options.dirtBudget() - cellColumnLimit, linkedHashSet, linkedHashSet);
               cellStand = getCellStand(overlay, trailWalk, options, linkedHashSet, feet2);
            }

            if (cellStand != null && options.dirtBudget() - cellColumnLimit > 0) {
               int y = cellStand.cell.y();
               boolean cellColumnFlag3 = false;

               for (SupportStandUtils.Cell cell3 : (Iterable<com.corz.client.utils.SupportStandUtils.Cell>) (linkedHashSet)) {
                  if (cell3.y() > y) {
                     cellColumnFlag3 = true;
                     break;
                  }
               }

               if (cellColumnFlag3) {
                  SupportStandUtils.Nav trailWalk2 = SupportStandUtils.Nav.getTrailWalk(overlay, feet2, options, options.dirtBudget() - cellColumnLimit, linkedHashSet, linkedHashSet);
                  SupportStandUtils.Removal cellStand2 = getCellStand(overlay, trailWalk2, options, linkedHashSet, feet2);
                  if (cellStand2 != null && cellStand2.cell.y() > y) {
                     trailWalk = trailWalk2;
                     cellStand = cellStand2;
                  }
               }
            }

            if (cellStand == null && overlay.costCellActive) {
               SupportStandUtils.Cell cell4 = null;
               if (linkedHashSet.contains(feet2.getData222()) && linkedHashSet.contains(feet2.getDataForInt(2))) {
                  cell4 = feet2.getData222();
               } else {
                  for (SupportStandUtils.Cell cell5 : overlay.costCellCellSet) {
                     if (!linkedHashSet.contains(cell5.getDataCell()) && (cell4 == null || cell5.y() > cell4.y())) {
                        cell4 = cell5;
                     }
                  }
               }

               if (cell4 != null && cellColumnLimit > 0 && arrayList2.size() > cellColumnLimit) {
                  boolean cellColumnFlag4 = false;

                  for (SupportStandUtils.Cell cell6 : (Iterable<com.corz.client.utils.SupportStandUtils.Cell>) (linkedHashSet)) {
                     if (!cell6.equals(cell4) && !overlay.costCellCellSet.contains(cell6) && cell6.y() >= cell4.y()) {
                        cellColumnFlag4 = true;
                        break;
                     }
                  }

                  if (cellColumnFlag4) {
                     cellColumnFlag2 = true;
                     break;
                  }
               }

               SupportStandUtils.Cell cell7 = cell4 == null ? null : getDataCell(overlay, cell4);
               if (cell7 != null) {
                  if (!cell4.equals(feet2.getData222())) {
                     for (SupportStandUtils.Nav.Edge edge : nav.getWalkTrail(cell4.getDataCell())) {
                        if (edge.op == SupportStandUtils.Op.WALK) {
                           arrayList.add(new SupportStandUtils.Step(SupportStandUtils.Op.WALK, edge.to, edge.to, 0, null, ""));
                           feet2 = edge.to;
                        } else if (edge.op == SupportStandUtils.Op.DIG_DOWN) {
                           if (!feet2.equals(edge.from)) {
                              arrayList.add(new SupportStandUtils.Step(SupportStandUtils.Op.WALK, edge.from, edge.from, 0, null, ""));
                           }

                           overlay.handleForCell(edge.cell);
                           linkedHashSet.remove(edge.cell);
                           arrayList2.add(edge.cell);
                           arrayList.add(new SupportStandUtils.Step(SupportStandUtils.Op.DIG_DOWN, edge.cell, edge.to, 1, null, "descend own column"));
                           feet2 = edge.to;
                        }
                     }

                     if (!feet2.equals(cell4.getDataCell())) {
                        arrayList.add(new SupportStandUtils.Step(SupportStandUtils.Op.WALK, cell4.getDataCell(), cell4.getDataCell(), 0, null, ""));
                        SupportStandUtils.Cell dataCell = cell4.getDataCell();
                     }
                  }

                  overlay.handleForCell(cell4);
                  linkedHashSet.remove(cell4);
                  arrayList2.add(cell4);
                  arrayList.add(new SupportStandUtils.Step(SupportStandUtils.Op.DIG_DOWN, cell4, cell4, 1, null, "descend own column"));
                  feet2 = cell7;
                  continue;
               }
            }

            if (cellStand == null && flag && size2 < 0 && options.dirtBudget() - cellColumnLimit > 0 && !linkedHashSet.contains(feet2.getData222())) {
               boolean cellColumnFlag5 = true;

               for (SupportStandUtils.Cell cell9 : (Iterable<com.corz.client.utils.SupportStandUtils.Cell>) (linkedHashSet)) {
                  if (cell9.y() > feet2.y() - 2 && (cell9.y() >= feet2.y() || !linkedHashSet.contains(cell9.getData222()))) {
                     cellColumnFlag5 = false;
                     break;
                  }
               }

               if (!cellColumnFlag5 && !overlay.cellColumnCellSet.isEmpty()) {
                  cellColumnFlag5 = true;

                  for (SupportStandUtils.Cell goodsSlot : (Iterable<com.corz.client.utils.SupportStandUtils.Cell>) (linkedHashSet)) {
                     if (!overlay.cellColumnCellSet.contains(goodsSlot) && goodsSlot.y() > feet2.y() - 2 && (goodsSlot.y() >= feet2.y() || !linkedHashSet.contains(goodsSlot.getData222()))) {
                        cellColumnFlag5 = false;
                        break;
                     }
                  }
               }

               if (cellColumnFlag5) {
                  overlay.cellColumnActive = true;

                  try {
                     trailWalk = SupportStandUtils.Nav.getTrailWalk(
                        overlay, feet2, options.windowMargin() > 4 ? options.getAllowStands(4) : options, options.dirtBudget() - cellColumnLimit, Set.of(), linkedHashSet
                     );
                     cellStand = getCellStand(overlay, trailWalk, options, linkedHashSet, feet2);
                  } finally {
                     overlay.cellColumnActive = false;
                  }

                  if (cellStand != null) {
                     size2 = arrayList.size();
                     cellColumnValue = arrayList2.size();
                     cellColumnLimit2 = cellColumnLimit;
                     of = new LinkedHashSet(linkedHashSet);
                     if (options2.budgetNanos() > 0L) {
                        cellColumnBound = Math.max(cellColumnBound, System.nanoTime() + options2.budgetNanos());
                     }
                  }
               }
            }

            if (cellStand != null && cellColumnLimit > 0 && arrayList2.size() > cellColumnLimit) {
               var hashSet = new HashSet();

               for (SupportStandUtils.Nav.Edge getWalkTrailItem : trailWalk.getWalkTrail(cellStand.stand)) {
                  if (getWalkTrailItem.op == SupportStandUtils.Op.DIG_DOWN) {
                     hashSet.add(getWalkTrailItem.cell);
                  }
               }

               boolean cellColumnFlag6 = false;
               if (!hashSet.isEmpty()) {
                  for (SupportStandUtils.Cell tickx : (Iterable<com.corz.client.utils.SupportStandUtils.Cell>) (linkedHashSet)) {
                     if (!hashSet.contains(tickx) && !tickx.equals(cellStand.cell) && tickx.y() > cellStand.cell.y()) {
                        cellColumnFlag6 = true;
                        break;
                     }
                  }
               }

               if (cellColumnFlag6) {
                  cellColumnFlag2 = true;
                  break;
               }
            }

            if (cellStand == null) {
               break;
            }

            for (SupportStandUtils.Nav.Edge edge3 : trailWalk.getWalkTrail(cellStand.stand)) {
               switch (edge3.op) {
                  case WALK:
                     arrayList.add(new SupportStandUtils.Step(SupportStandUtils.Op.WALK, edge3.to, edge3.to, 0, null, ""));
                     feet2 = edge3.to;
                     if (edge3.trail != null) {
                        overlay.handleForCell(edge3.trail);
                        linkedHashSet.remove(edge3.trail);
                        arrayList2.add(edge3.trail);
                        arrayList.add(new SupportStandUtils.Step(SupportStandUtils.Op.REMOVE_DIRT, edge3.trail, edge3.to, 1, null, "trailing step"));
                     }
                     break;
                  case PILLAR:
                     if (!feet2.equals(edge3.from)) {
                        arrayList.add(new SupportStandUtils.Step(SupportStandUtils.Op.WALK, edge3.from, edge3.from, 0, null, ""));
                     }

                     for (int i = 0; i < edge3.count; i++) {
                        SupportStandUtils.Cell data223 = edge3.from.getData223(i);
                        overlay.handle(data223);
                        linkedHashSet.add(data223);
                     }

                     cellColumnLimit += edge3.count;
                     arrayList.add(new SupportStandUtils.Step(SupportStandUtils.Op.PILLAR, edge3.from, edge3.to, edge3.count, null, "reach " + cellStand.cell));
                     feet2 = edge3.to;
                     break;
                  case DIRT:
                     SupportStandUtils.Cell o = edge3.placeFrom == null ? edge3.from : edge3.placeFrom;
                     if (!feet2.equals(o)) {
                        arrayList.add(new SupportStandUtils.Step(SupportStandUtils.Op.WALK, o, o, 0, null, ""));
                     }

                     overlay.handle(edge3.cell);
                     linkedHashSet.add(edge3.cell);
                     cellColumnLimit++;
                     arrayList.add(new SupportStandUtils.Step(SupportStandUtils.Op.DIRT, edge3.cell, o, 1, edge3.intent, "step to reach " + cellStand.cell));
                     arrayList.add(new SupportStandUtils.Step(SupportStandUtils.Op.WALK, edge3.to, edge3.to, 0, null, ""));
                     feet2 = edge3.to;
                     if (edge3.trail != null) {
                        overlay.handleForCell(edge3.trail);
                        linkedHashSet.remove(edge3.trail);
                        arrayList2.add(edge3.trail);
                        arrayList.add(new SupportStandUtils.Step(SupportStandUtils.Op.REMOVE_DIRT, edge3.trail, edge3.to, 1, null, "trailing step"));
                     }
                     break;
                  case DIG_DOWN:
                     if (!feet2.equals(edge3.from)) {
                        arrayList.add(new SupportStandUtils.Step(SupportStandUtils.Op.WALK, edge3.from, edge3.from, 0, null, ""));
                     }

                     overlay.handleForCell(edge3.cell);
                     linkedHashSet.remove(edge3.cell);
                     arrayList2.add(edge3.cell);
                     arrayList.add(new SupportStandUtils.Step(SupportStandUtils.Op.DIG_DOWN, edge3.cell, edge3.to, 1, null, "descend own column"));
                     feet2 = edge3.to;
               }
            }

            if (!feet2.equals(cellStand.stand)) {
               arrayList.add(new SupportStandUtils.Step(SupportStandUtils.Op.WALK, cellStand.stand, cellStand.stand, 0, null, ""));
            }

            arrayList.add(new SupportStandUtils.Step(SupportStandUtils.Op.REMOVE_DIRT, cellStand.cell, cellStand.stand, 1, null, "final cleanup"));
            overlay.handleForCell(cellStand.cell);
            linkedHashSet.remove(cellStand.cell);
            arrayList2.add(cellStand.cell);
         }

         int cellColumnCounter2 = 0;

         while (!cellColumnFlag2 && !cellColumnFlag && overlay.canApiAdded(feet2.getData222()) && linkedHashSet.contains(feet2.getData222()) && cellColumnCounter2++ < 16) {
            SupportStandUtils.Cell data222 = feet2.getData222();
            overlay.handleForCell(data222);
            SupportStandUtils.Cell dataForInt15 = null;

            for (int j = 0; j < 2 && dataForInt15 == null; j++) {
               if (overlay.canLavaDirt(data222.getDataForInt(j))) {
                  dataForInt15 = data222.getDataForInt(j);
               }
            }

            if (dataForInt15 == null) {
               overlay.handle(data222);
               break;
            }

            arrayList.add(new SupportStandUtils.Step(SupportStandUtils.Op.DIG_DOWN, data222, data222, 1, null, "descend own column"));
            linkedHashSet.remove(data222);
            arrayList2.add(data222);
            feet2 = dataForInt15;
         }

         if (size2 >= 0 && (!((java.util.Set) of).containsAll(linkedHashSet) || linkedHashSet.size() >= ((java.util.Set) of).size() || !cellColumnFlag2 && !cellColumnFlag && linkedHashSet.contains(feet2.getData222()))) {
            arrayList.subList(size2, arrayList.size()).clear();
            arrayList2.subList(cellColumnValue, arrayList2.size()).clear();
            linkedHashSet.clear();
            linkedHashSet.addAll((Collection)of);
            cellColumnLimit = cellColumnLimit2;
            cellColumnFlag2 = false;
            cellColumnFlag = false;
         }

         var arrayList3 = new ArrayList();

         for (SupportStandUtils.Cell cell16 : (Iterable<com.corz.client.utils.SupportStandUtils.Cell>) (linkedHashSet)) {
            arrayList3.add(
               new SupportStandUtils.Blocker(
                  cell16, "UNREACHABLE_FOR_REMOVAL (no stand within reach sees it, and no dirt step/column reaches such a stand)", List.of()
               )
            );
         }

         String cellColumnText = cellColumnFlag ? "time-budget" : (cellColumnFlag2 ? "budget-hold" : "");
         SupportStandUtils.Status status = linkedHashSet.isEmpty() ? SupportStandUtils.Status.SOLVED : SupportStandUtils.Status.PARTIAL;
         var list = linkedHashSet.isEmpty()
            ? List.of()
            : List.of(new SupportStandUtils.Cycle(List.copyOf(linkedHashSet), List.copyOf(arrayList3), "temporary blocks left in place"));
         return new SupportStandUtils.Mission(status, List.copyOf(arrayList), List.copyOf(arrayList2), List.copyOf(linkedHashSet), List.copyOf(arrayList3), ((java.util.List) (Object) (list)), cellColumnLimit, cellColumnText);
      }
   }

   public static SupportStandUtils.Mission getDataForWorldList(
      SupportStandUtils.World world, List<SupportStandUtils.Target> list, SupportStandUtils.Cell cell, SupportStandUtils.Options options
   ) {
      return getBudgetStands(world, list, cell, options, null);
   }

   private static List<SupportStandUtils.Cycle> getNoneDepends(
      List<SupportStandUtils.Cell> list, List<SupportStandUtils.Blocker> list2, String noneDependsText
   ) {
      var hashMap = new HashMap();

      for (SupportStandUtils.Blocker blocker : list2) {
         hashMap.put(blocker.target(), blocker);
      }

      var linkedHashSet = new LinkedHashSet(list);
      var hashMap2 = new HashMap();
      var hashMap3 = new HashMap();
      var hashSet = new HashSet();
      var arrayDeque = new ArrayDeque();
      var arrayList = new ArrayList();
      int[] ints = new int[]{0};

      for (SupportStandUtils.Cell cell : (Iterable<com.corz.client.utils.SupportStandUtils.Cell>) (linkedHashSet)) {
         if (!hashMap2.containsKey(cell)) {
            handleDepends(cell, linkedHashSet, hashMap, hashMap2, hashMap3, hashSet, arrayDeque, arrayList, ints);
         }
      }

      var arrayList2 = new ArrayList();
      String noneDependsText2 = noneDependsText.isBlank() ? "none" : noneDependsText.trim();

      for (List list3 : (Iterable<java.util.List>) (arrayList)) {
         boolean noneDependsFlag = list3.size() == 1
            && hashMap.containsKey(list3.get(0))
            && ((SupportStandUtils.Blocker)hashMap.get(list3.get(0))).dependsOn().contains(list3.get(0));
         if (list3.size() >= 2 || noneDependsFlag) {
            var arrayList3 = new ArrayList();

            for (SupportStandUtils.Cell formatGroundAir : (Iterable<com.corz.client.utils.SupportStandUtils.Cell>) (list3)) {
               if (hashMap.containsKey(formatGroundAir)) {
                  arrayList3.add((SupportStandUtils.Blocker)hashMap.get(formatGroundAir));
               }
            }

            arrayList2.add(
               new SupportStandUtils.Cycle(
                  List.copyOf(list3),
                  List.copyOf(arrayList3),
                  "mutual dependency \u2014 escalations tried: "
                     + noneDependsText2
                     + "; needs an action outside the schematic graph (dirt support / other side / different order)"
               )
            );
         }
      }

      if (arrayList2.isEmpty()) {
         var arrayList4 = new ArrayList();

         for (SupportStandUtils.Cell cell3 : list) {
            if (hashMap.containsKey(cell3)) {
               arrayList4.add((SupportStandUtils.Blocker)hashMap.get(cell3));
            }
         }

         arrayList2.add(
            new SupportStandUtils.Cycle(
               List.copyOf(list), List.copyOf(arrayList4), "no dependency cycle \u2014 each cell's blocker is geometric; escalations tried: " + noneDependsText2
            )
         );
      }

      return arrayList2;
   }

   private static SupportStandUtils.Removal getCostCell(
      SupportStandUtils.Overlay overlay,
      SupportStandUtils.Nav nav,
      SupportStandUtils.Options options,
      Set<SupportStandUtils.Cell> set,
      SupportStandUtils.Cell cell,
      Set<Long> hashSet
   ) {
      double reach = options.reach();
      int costCellLimit = (int)Math.ceil(reach);
      SupportStandUtils.Removal costCell = null;

      for (SupportStandUtils.Cell stand : set) {
         if ((costCell == null || stand.y() >= costCell.cell.y()) && (!overlay.costCellActive || !overlay.costCellCellSet.contains(stand))) {
            SupportStandUtils.Removal removal = null;

            for (SupportStandUtils.Cell getKeyItem : nav.getKeySet()) {
               if (Math.abs(getKeyItem.x() - stand.x()) <= costCellLimit
                  && Math.abs(getKeyItem.z() - stand.z()) <= costCellLimit
                  && getKeyItem.y() >= stand.y() - 4
                  && getKeyItem.y() <= stand.y() + 3
                  && (getKeyItem.y() != stand.y() - 4 || Math.abs(getKeyItem.x() - stand.x()) <= 1 && Math.abs(getKeyItem.z() - stand.z()) <= 1)
                  && !getKeyItem.equals(stand)
                  && !getKeyItem.getDataCell().equals(stand)
                  && !getKeyItem.getData222().equals(stand)
                  && (
                     !set.contains(stand.getData222())
                        || getKeyItem.y() <= stand.y() + 2 && (getKeyItem.y() != stand.y() + 2 || Math.abs(getKeyItem.x() - stand.x()) + Math.abs(getKeyItem.z() - stand.z()) == 1)
                  )
                  && !hashSet.contains(SupportStandUtils.Options.getTime(stand, getKeyItem))
                  && !options.canPairsBanned(stand, getKeyItem)) {
                  double costCellTotal = nav.getOrDefaultForCell(getKeyItem) - (overlay.cellColumnActive && set.contains(getKeyItem.getData222()) ? 1000.0 : 0.0);
                  if (overlay.cellColumnActive && nav.getOrDefault(getKeyItem).added().contains(getKeyItem.getData222())) {
                     costCellTotal += 500.0;
                  }

                  if (removal == null || !(costCellTotal >= removal.cost)) {
                     overlay.handleInstance(nav.getOrDefault(getKeyItem));

                     try {
                        if (overlay.canLavaDirt(getKeyItem) && overlay.canApiAdded(stand)) {
                           double[] costCellValues = overlay.getAmounts(getKeyItem, getKeyItem.y() == stand.y() - 4);
                           double costCellValue = stand.x() + 0.5;
                           double costCellValue2 = stand.y() + 0.5;
                           double costCellValue3 = stand.z() + 0.5;
                           if (!(getAmount(costCellValues, costCellValue, costCellValue2, costCellValue3) > (reach + 0.87) * (reach + 0.87))) {
                              boolean costCellFlag = false;
                              int costCellCounter = 0;

                              label348:
                              while (true) {
                                 if (costCellCounter < 6 && !costCellFlag) {
                                    int[] values2 = PistonStickyUtils.getValues(costCellCounter);
                                    var iterator = getUnmodifiable(false).iterator();

                                    while (true) {
                                       if (iterator.hasNext()) {
                                          double[] next3 = (double[])iterator.next();
                                          double[] doubles = getAmounts(stand, MIN_MAX_DATA, costCellCounter, next3[0], next3[1]);
                                          double costCellValue4 = (costCellValues[0] - doubles[0]) * values2[0] + (costCellValues[1] - doubles[1]) * values2[1] + (costCellValues[2] - doubles[2]) * values2[2];
                                          if (costCellValue4 <= 1.0E-4
                                             || getAmount(costCellValues, doubles[0], doubles[1], doubles[2]) > reach * reach
                                             || !overlay.canFlag(costCellValues, doubles, stand, true)) {
                                             continue;
                                          }

                                          costCellFlag = true;
                                       }

                                       costCellCounter++;
                                       continue label348;
                                    }
                                 }

                                 if (costCellFlag) {
                                    removal = new SupportStandUtils.Removal(stand, getKeyItem, costCellTotal);
                                 }
                                 break;
                              }
                           }
                        }
                     } finally {
                        overlay.updateInstance();
                     }
                  }
               }
            }

            if (removal != null && (costCell == null || stand.y() > costCell.cell.y() || stand.y() == costCell.cell.y() && removal.cost < costCell.cost)) {
               costCell = removal;
            }
         }
      }

      return costCell;
   }

   static boolean canFlag(SupportStandUtils.Overlay overlay, SupportStandUtils.Cell cell, SupportStandUtils.Cell pos) {
      if (overlay.overlayFloorY != Integer.MIN_VALUE && cell.y() > overlay.overlayFloorY + 2) {
         boolean supportStandFlag = pos != null && overlay.apiLavaCellSet.add(pos);

         try {
            var arrayDeque = new ArrayDeque();
            var hashSet = new HashSet();
            arrayDeque.add(cell);
            hashSet.add(cell);
            var arrayList = new ArrayList();

            while (!arrayDeque.isEmpty() && hashSet.size() < 256) {
               if (Thread.currentThread().isInterrupted()) {
                  return false;
               }

               SupportStandUtils.Cell supportStandUtilsCell = (SupportStandUtils.Cell)arrayDeque.poll();
               if (supportStandUtilsCell.y() <= overlay.overlayFloorY) {
                  return true;
               }

               if (!supportStandUtilsCell.equals(cell) && overlay.lavaDirtCellSet.contains(supportStandUtilsCell)) {
                  return true;
               }

               SupportStandUtils.Cell blockPos = supportStandUtilsCell;

               while (overlay.canApiAdded(blockPos.getData222()) && blockPos.y() > overlay.overlayFloorY) {
                  blockPos = blockPos.getData222();
               }

               if (blockPos.y() <= overlay.overlayFloorY) {
                  return true;
               }

               if (!blockPos.equals(supportStandUtilsCell) && hashSet.add(blockPos)) {
                  arrayDeque.add(blockPos);
               }

               arrayList.clear();
               SupportStandUtils.Nav.handleWalk(overlay, supportStandUtilsCell, arrayList);

               for (SupportStandUtils.Nav.Edge edge : (Iterable<com.corz.client.utils.SupportStandUtils.Nav.Edge>) (arrayList)) {
                  if (Math.abs(edge.to().x() - cell.x()) <= 8 && Math.abs(edge.to().z() - cell.z()) <= 8 && hashSet.add(edge.to())) {
                     arrayDeque.add(edge.to());
                  }
               }
            }

            return false;
         } finally {
            if (supportStandFlag) {
               overlay.apiLavaCellSet.remove(pos);
            }
         }
      } else {
         return true;
      }
   }

   private static boolean canMinReach(
      SupportStandUtils.Overlay overlay, SupportStandUtils.Options options, Set<SupportStandUtils.Cell> set, SupportStandUtils.Cell cell, SupportStandUtils.Cell cell2
   ) {
      double reach = options.reach();
      int reachLimit = (int)Math.ceil(reach);
      if (Math.abs(cell2.x() - cell.x()) <= reachLimit && Math.abs(cell2.z() - cell.z()) <= reachLimit) {
         if (cell2.y() >= cell.y() - 4
            && cell2.y() <= cell.y() + 3
            && (cell2.y() != cell.y() - 4 || Math.abs(cell2.x() - cell.x()) <= 1 && Math.abs(cell2.z() - cell.z()) <= 1)) {
            if (!cell2.equals(cell) && !cell2.getDataCell().equals(cell) && !cell2.getData222().equals(cell)) {
               if (!set.contains(cell.getData222())
                  || cell2.y() <= cell.y() + 2 && (cell2.y() != cell.y() + 2 || Math.abs(cell2.x() - cell.x()) + Math.abs(cell2.z() - cell.z()) == 1)) {
                  if (overlay.canLavaDirt(cell2) && overlay.canApiAdded(cell)) {
                     double[] reachValues = overlay.getAmounts(cell2, cell2.y() == cell.y() - 4);
                     double reachValue = cell.x() + 0.5;
                     double reachValue2 = cell.y() + 0.5;
                     double reachValue3 = cell.z() + 0.5;
                     if (getAmount(reachValues, reachValue, reachValue2, reachValue3) > (reach + 0.87) * (reach + 0.87)) {
                        return false;
                     } else {
                        for (int i = 0; i < 6; i++) {
                           int[] values2 = PistonStickyUtils.getValues(i);

                           for (double[] reachValues3 : getUnmodifiable(false)) {
                              double[] doubles = getAmounts(cell, MIN_MAX_DATA, i, reachValues3[0], reachValues3[1]);
                              double reachValue4 = (reachValues[0] - doubles[0]) * values2[0] + (reachValues[1] - doubles[1]) * values2[1] + (reachValues[2] - doubles[2]) * values2[2];
                              if (!(reachValue4 <= 1.0E-4)
                                 && !(getAmount(reachValues, doubles[0], doubles[1], doubles[2]) > reach * reach)
                                 && overlay.canFlag(reachValues, doubles, cell, true)) {
                                 return true;
                              }
                           }
                        }

                        return false;
                     }
                  } else {
                     return false;
                  }
               } else {
                  return false;
               }
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

   static Set<SupportStandUtils.Cell> getMutualDependency(Set<SupportStandUtils.Cell> set, List<SupportStandUtils.Blocker> list) {
      var hashSet = new HashSet();

      for (SupportStandUtils.Cycle cycle : getNoneDepends(new ArrayList<>(set), list, "")) {
         if (cycle.resolution().startsWith("mutual dependency")) {
            hashSet.addAll(cycle.members());
         }
      }

      return hashSet;
   }

   static {
      double[] doubles = new double[]{0.5, 0.3, 0.7};
      var arrayList = new ArrayList();

      for (double supportStandBound : doubles) {
         for (double supportStandBound2 : doubles) {
            arrayList.add(new double[]{supportStandBound, supportStandBound2, Math.min(Math.min(supportStandBound, 1.0 - supportStandBound), Math.min(supportStandBound2, 1.0 - supportStandBound2))});
         }
      }

      arrayList.sort((obj, obj2) -> Double.compare(((double[]) obj2)[2], ((double[]) obj)[2]));
      double[] doubles2 = new double[]{0.5, 0.3, 0.7, 0.15, 0.85, 0.08, 0.92};
      var supplier = new ArrayList();

      for (double supportStandBound3 : doubles2) {
         for (double hypot : doubles2) {
            boolean supportStandFlag = false;

            for (double supportStandValue : doubles) {
               for (double supportStandValue2 : doubles) {
                  if (supportStandValue == supportStandBound3 && supportStandValue2 == hypot) {
                     supportStandFlag = true;
                  }
               }
            }

            if (!supportStandFlag) {
               supplier.add(new double[]{supportStandBound3, hypot, Math.min(Math.min(supportStandBound3, 1.0 - supportStandBound3), Math.min(hypot, 1.0 - hypot))});
            }
         }
      }

      supplier.sort((obj3, obj4) -> Double.compare(((double[]) obj4)[2], ((double[]) obj3)[2]));
      var arrayList3 = new ArrayList(arrayList);
      arrayList3.addAll(supplier);
      unmodifiableList = Collections.unmodifiableList(arrayList);
      unmodifiableDoubleArrayList = Collections.unmodifiableList(arrayList3);
   }

   private static boolean canStandSeals(List<SupportStandUtils.Blocker> list) {
      for (SupportStandUtils.Blocker blocker : list) {
         if (blocker.reason().startsWith("NO_REACHABLE_STAND")
            || blocker.reason().startsWith("NO_LINE_OF_SIGHT")
            || blocker.reason().startsWith("NO_FACING_POSE")
            || blocker.reason().startsWith("EGRESS_SEALS_STAND")
            || blocker.reason().startsWith("STAND_UNREACHABLE_FROM_HERE")) {
            return true;
         }
      }

      return false;
   }

   static boolean canFlag(int supportStandValue, int supportStandValue2) {
      return supportStandValue == supportStandValue2 || supportStandValue == PistonStickyUtils.getValue(supportStandValue2);
   }

   private static boolean canFacingPose(
      SupportStandUtils.Overlay overlay, Set<SupportStandUtils.Cell> set, List<SupportStandUtils.Blocker> list, SupportStandUtils.Options options, int facingPoseBound
   ) {
      boolean facingPose = false;

      for (SupportStandUtils.Blocker blocker : list) {
         SupportStandUtils.Cell target = blocker.target();
         if (!overlay.isContainsKey(target) && set.contains(target)) {
            SupportStandUtils.Target lavaDirt = overlay.getLavaDirt(target);
            boolean facingPoseFlag = blocker.reason().startsWith("NO_FACING_POSE")
               && lavaDirt.kind() == SupportStandUtils.Kind.YAW_OPPOSITE
               && lavaDirt.shape() == SupportStandUtils.Shape.CHEST;
            boolean facingPoseFlag2 = (blocker.reason().startsWith("NO_FACING_POSE") || blocker.reason().startsWith("NO_LINE_OF_SIGHT"))
               && (lavaDirt.kind() == SupportStandUtils.Kind.TRAPDOOR_BOTTOM || lavaDirt.kind() == SupportStandUtils.Kind.TRAPDOOR_TOP);
            if ((facingPoseFlag || facingPoseFlag2) && lavaDirt.facing() >= 2) {
               SupportStandUtils.Cell cell = target.getData222(PistonStickyUtils.getValue(lavaDirt.facing()));
               if (!set.contains(cell) && overlay.canDirtLava(cell, options)) {
                  var items2 = getItems(overlay, target, cell, set, options, Math.min(3, facingPoseBound));
                  if (items2 != null) {
                     for (SupportStandUtils.Cell cell2 : items2) {
                        overlay.handleDirtPlain(cell2, target);
                        set.add(cell2);
                     }

                     facingPose = true;
                  }
               }
            }
         }
      }

      return facingPose;
   }

   private static String formatFloorsOccupied(SupportStandUtils.Overlay overlay, SupportStandUtils.Target target, Set<SupportStandUtils.Cell> set) {
      for (int i = -2; i <= 2; i++) {
         for (int j = -2; j <= 2; j++) {
            for (int floorsOccupiedValue : ProofSupportUtils.coverageTable) {
               SupportStandUtils.Cell cell = target.cell().getDataX(i, floorsOccupiedValue, j);
               if (set.contains(cell.getData222())) {
                  return "a candidate stand " + cell + " floats over unbuilt " + cell.getData222();
               }
            }
         }
      }

      return "floors are air or occupied";
   }

   private static double getKindTrapdoor(double kindTrapdoorValue, double kindTrapdoorValue2, double spotValue, int pistonStickyValue, SupportStandUtils.Target target) {
      if (target.kind() == SupportStandUtils.Kind.PLAIN || target.kind() == SupportStandUtils.Kind.FLUID || target.kind() == SupportStandUtils.Kind.HOPPER) {
         return 0.0;
      } else if (target.kind() != SupportStandUtils.Kind.TRAPDOOR_BOTTOM && target.kind() != SupportStandUtils.Kind.TRAPDOOR_TOP) {
         double kindTrapdoorBound = Math.abs(kindTrapdoorValue);
         double absLook = Math.abs(kindTrapdoorValue2);
         double abs2 = Math.abs(spotValue);
         double kindTrapdoorValue5;
         if (target.kind() != SupportStandUtils.Kind.YAW_OPPOSITE && target.kind() != SupportStandUtils.Kind.YAW_AXIS) {
            double kindTrapdoorBound3 = PistonStickyUtils.canFlag(pistonStickyValue) ? absLook : (pistonStickyValue != 5 && pistonStickyValue != 4 ? abs2 : kindTrapdoorBound);
            double amountForDouble = getAmountForDouble(kindTrapdoorBound, absLook, abs2);
            if (kindTrapdoorBound3 < Math.max(Math.max(kindTrapdoorBound, absLook), abs2) - 1.0E-12) {
               amountForDouble = Math.max(Math.max(kindTrapdoorBound, absLook), abs2);
            }

            kindTrapdoorValue5 = (kindTrapdoorBound3 - amountForDouble) / Math.max(1.0E-6, kindTrapdoorBound3);
         } else {
            kindTrapdoorValue5 = Math.abs(kindTrapdoorBound - abs2) / Math.max(1.0E-6, Math.max(kindTrapdoorBound, abs2));
         }

         return kindTrapdoorValue5 < 0.1 ? 4.0 : (kindTrapdoorValue5 < 0.25 ? 1.5 : 0.0);
      } else {
         double hashRegistryBound = Math.abs(kindTrapdoorValue);
         double apiPutBound = Math.abs(spotValue);
         double kindTrapdoorValue7 = Math.abs(hashRegistryBound - apiPutBound) / Math.max(1.0E-6, Math.max(hashRegistryBound, apiPutBound));
         return kindTrapdoorValue7 < 0.1 ? 4.0 : (kindTrapdoorValue7 < 0.25 ? 1.5 : 0.0);
      }
   }

   private static int[] getFacingSight(SupportStandUtils.Target target) {
      if (target.supportDir() >= 0) {
         return new int[]{target.supportDir()};
      } else if (target.facing() < 0 && target.kind() != SupportStandUtils.Kind.HOPPER) {
         return sightFacingValues;
      } else {
         switch (target.kind()) {
            case FACE_PLAYER:
               return PistonStickyUtils.getValues(target.facing(), PistonStickyUtils.Kind.DISPENSER);
            case FACE_AWAY:
               return PistonStickyUtils.getValues(target.facing(), PistonStickyUtils.Kind.OBSERVER);
            case YAW_OPPOSITE:
            case YAW_AXIS:
            case FLUID:
            default:
               return sightFacingValues;
            case HOPPER:
               if (target.facing() != 0 && target.facing() >= 0) {
                  return new int[]{target.facing()};
               }

               return new int[]{0, 1};
            case TRAPDOOR_BOTTOM:
               return new int[]{0, PistonStickyUtils.getValue(target.facing())};
            case TRAPDOOR_TOP:
               return new int[]{1, PistonStickyUtils.getValue(target.facing())};
         }
      }
   }

   private static boolean canFlag(SupportStandUtils.Box box, int supportStandValue) {
      return switch (supportStandValue) {
         case 0, 1 -> box.y1() > box.y0();
         default -> box.x1() > box.x0() && box.z1() > box.z0() && box.y1() > box.y0();
      };
   }

   private static double getAmount(SupportStandUtils.Box box, int supportStandValue) {
      return switch (supportStandValue) {
         case 0, 1 -> Math.min(box.x1() - box.x0(), box.z1() - box.z0());
         case 2, 3 -> Math.min(box.x1() - box.x0(), box.y1() - box.y0());
         default -> Math.min(box.y1() - box.y0(), box.z1() - box.z0());
      };
   }

   private static SupportStandUtils.Target getDataForOverlayCell(SupportStandUtils.Overlay overlay, SupportStandUtils.Cell cell, Set<SupportStandUtils.Cell> set) {
      for (int i = 2; i <= 5; i++) {
         SupportStandUtils.Cell goodsSlot = cell.getData222(i);
         if (set.contains(goodsSlot) && !overlay.isContainsKey(goodsSlot) && canCampfireFacing(overlay.getLavaDirt(goodsSlot), cell)) {
            return overlay.getLavaDirt(goodsSlot);
         }
      }

      return null;
   }

   private static double getAmount(double supportStandValue) {
      return supportStandValue < 0.12 ? 6.0 : (supportStandValue < 0.2 ? 3.0 : (supportStandValue < 0.3 ? 1.0 : 0.0));
   }

   private static SupportStandUtils.Candidate getEverySupport(
      SupportStandUtils.Overlay overlay,
      SupportStandUtils.Target target,
      SupportStandUtils.Nav nav,
      SupportStandUtils.Options options,
      Set<SupportStandUtils.Cell> set,
      List<SupportStandUtils.Blocker> list,
      boolean everySupportFlag,
      boolean everySupportFlag2
   ) {
      double reach = options.reach();
      int everySupportLimit = (int)Math.ceil(reach);
      var arrayList = new ArrayList();
      var supplier = new ArrayList();
      boolean everySupportFlag3 = false;
      boolean everySupportFlag4 = false;
      boolean everySupportFlag5 = false;
      boolean everySupportFlag6 = false;
      boolean everySupportFlag7 = false;
      boolean everySupportFlag8 = false;
      SupportStandUtils.Candidate everySupport = null;
      var hashMap = new HashMap<SupportStandUtils.Cell, Boolean>();
      boolean everySupportFlag9 = target.partner() != null && overlay.canLavaDirtApiLavaCellSet(target.partner());

      for (int everySupportValue : getFacingSight(target)) {
         SupportStandUtils.Cell cell = target.cell().getData222(everySupportValue);
         int supportStandValue = PistonStickyUtils.getValue(everySupportValue);
         if (set.contains(cell)) {
            arrayList.add(cell);
         } else if (overlay.isForeignCell(cell)) {
            supplier.add(cell);
         } else {
            boolean everySupportFlag10 = !overlay.getCellBoxes(cell).isEmpty() || !overlay.getExtraBoxes(cell).isEmpty();
            if (!everySupportFlag10) {
               if (overlay.canDirtLava(cell, options)) {
                  everySupportFlag4 = true;
               }
            } else {
               boolean lavaApi = overlay.isInteractiveCell(cell);
               boolean facingKindFlag = everySupportFlag9 && cell.equals(target.partner());
               if (target.kind() != SupportStandUtils.Kind.YAW_OPPOSITE || !everySupportFlag9 || !lavaApi || facingKindFlag) {
                  if (!overlay.getCellBoxes(cell).isEmpty()) {
                     everySupportFlag3 = true;
                  }

                  List apiLava = overlay.getTailOverrideBoxes(cell);
                  if (apiLava.isEmpty()) {
                     apiLava = overlay.getExtraBoxes(cell);
                  }

                  for (SupportStandUtils.Box box : (Iterable<com.corz.client.utils.SupportStandUtils.Box>) (apiLava)) {
                     if (canFlag(box, supportStandValue)) {
                        for (SupportStandUtils.Cell getKeyItem : nav.getKeySet()) {
                           if (Math.abs(getKeyItem.x() - target.cell().x()) <= everySupportLimit
                              && Math.abs(getKeyItem.z() - target.cell().z()) <= everySupportLimit
                              && (everySupportFlag ? ProofSupportUtils.canFlag(getKeyItem.y(), target.cell().y()) : getKeyItem.y() >= target.cell().y() - 3 && getKeyItem.y() <= target.cell().y() + 2)
                              )
                            {
                              boolean everySupportFlag12 = getKeyItem.equals(target.cell())
                                 && everySupportValue == 0
                                 && target.shape() == SupportStandUtils.Shape.NONE
                                 && target.kind() == SupportStandUtils.Kind.YAW_OPPOSITE;
                              if ((!getKeyItem.equals(target.cell()) || everySupportFlag12)
                                 && !getKeyItem.getDataCell().equals(target.cell())
                                 && !getKeyItem.equals(cell)
                                 && !getKeyItem.getDataCell().equals(cell)
                                 && (target.partner() == null || !getKeyItem.equals(target.partner()) && !getKeyItem.getDataCell().equals(target.partner()))
                                 && (options.allowFutureStands() || !set.contains(getKeyItem) || everySupportFlag12)
                                 && (!set.contains(getKeyItem.getDataCell()) || canCampfireFacing(target, getKeyItem.getDataCell()))) {
                                 if (options.canPairsBanned(target.cell(), getKeyItem)) {
                                    everySupportCallCount++;
                                 } else if (options.canStandsUnreachable(getKeyItem) && nav.getOrDefault(getKeyItem).getAdded() == 0) {
                                    everySupportFlag8 = true;
                                 } else {
                                    overlay.handleInstance(nav.getOrDefault(getKeyItem));

                                    try {
                                       if (overlay.canLavaDirt(getKeyItem)) {
                                          everySupportFlag5 = true;
                                          if (options.egressGuard() && !everySupportFlag12 && overlay.canComponentLava(getKeyItem, target.cell())) {
                                             everySupportFlag7 = true;
                                          } else if (options.egressGuard() && !hashMap.computeIfAbsent(getKeyItem, cell3 -> canFlag(overlay, ((com.corz.client.utils.SupportStandUtils.Cell) (cell3)), target.cell()))) {
                                             everySupportFlag7 = true;
                                          } else {
                                             double orDefault = nav.getOrDefaultForCell(getKeyItem);
                                             boolean everySupportFlag13 = options.edgeSneak()
                                                && (overlay.canFlag(getKeyItem) || target.cell().y() < getKeyItem.y())
                                                && (!everySupportFlag9 || facingKindFlag);

                                             for (int i = 0; i < 2; i++) {
                                                boolean overlayFlag = i == 1;
                                                if ((!lavaApi || overlayFlag) && (!everySupportFlag9 || facingKindFlag || !overlayFlag) && (!everySupportFlag13 || overlayFlag)) {
                                                   double[] everySupportValues = overlay.getAmounts(getKeyItem, overlayFlag);
                                                   double everySupportValue3 = target.cell().x() + 0.5;
                                                   double everySupportValue4 = target.cell().y() + 0.5;
                                                   double everySupportValue5 = target.cell().z() + 0.5;
                                                   double everySupportFactor = everySupportFlag2 ? reach : Math.max(1.0, reach - 0.6);
                                                   if (!(getAmount(everySupportValues, everySupportValue3, everySupportValue4, everySupportValue5) > everySupportFactor * everySupportFactor)) {
                                                      for (double[] everySupportValues2 : getUnmodifiable(options.allowEdgeAim())) {
                                                         double[] doubles = getAmounts(cell, box, supportStandValue, everySupportValues2[0], everySupportValues2[1]);
                                                         if (!(getAmount(everySupportValues, doubles[0], doubles[1], doubles[2]) > reach * reach)) {
                                                            int[] values4 = PistonStickyUtils.getValues(supportStandValue);
                                                            double everySupportValue6 = (everySupportValues[0] - doubles[0]) * values4[0]
                                                               + (everySupportValues[1] - doubles[1]) * values4[1]
                                                               + (everySupportValues[2] - doubles[2]) * values4[2];
                                                            if (!(everySupportValue6 <= 1.0E-4)) {
                                                               double pistonStickyLimit = doubles[0] - everySupportValues[0];
                                                               double spotValue = doubles[1] - everySupportValues[1];
                                                               double everySupportValue9 = doubles[2] - everySupportValues[2];
                                                               int facingKindValue = PistonStickyUtils.getValue(pistonStickyLimit, spotValue, everySupportValue9);
                                                               if (canFacingKind(target, facingKindValue, pistonStickyLimit, everySupportValue9, facingKindFlag)
                                                                  && canFacingTrapdoor(target, supportStandValue, doubles[1], pistonStickyLimit, everySupportValue9)) {
                                                                  everySupportFlag6 = true;
                                                                  if (overlay.canFlag(everySupportValues, doubles, cell, false)
                                                                     && (!target.isKindFluid() || !"water".equals(target.name()) || !overlay.isLavaOverrideCell(cell))
                                                                     && (!target.isKindFluid() || overlay.canFlag(everySupportValues, doubles, cell, supportStandValue))) {
                                                                     double min11 = Math.min(everySupportValues2[2], getAmount(box, supportStandValue) * everySupportValues2[2]);
                                                                     double everySupportValue12 = orDefault
                                                                        + 2.0 * ProofSupportUtils.getValue(getKeyItem.y(), target.cell().y())
                                                                        + getAmount(min11)
                                                                        + (overlayFlag ? 0.5 : 0.0)
                                                                        + (lavaApi ? 1.0 : 0.0)
                                                                        + (
                                                                           !PistonStickyUtils.canFlag(supportStandValue)
                                                                                 && (
                                                                                    target.kind() == SupportStandUtils.Kind.TRAPDOOR_BOTTOM
                                                                                       || target.kind() == SupportStandUtils.Kind.TRAPDOOR_TOP
                                                                                 )
                                                                              ? 0.0
                                                                              : getKindTrapdoor(pistonStickyLimit, spotValue, everySupportValue9, facingKindValue, target)
                                                                        );
                                                                     SupportStandUtils.Intent intent = new SupportStandUtils.Intent(
                                                                        target.cell(),
                                                                        getKeyItem,
                                                                        overlayFlag,
                                                                        cell,
                                                                        supportStandValue,
                                                                        doubles[0],
                                                                        doubles[1],
                                                                        doubles[2],
                                                                        facingKindValue,
                                                                        min11,
                                                                        false
                                                                     );
                                                                     if (!options.bannedIntents().contains(intent.getSneakFace())) {
                                                                        if (everySupport == null || everySupportValue12 < everySupport.cost - 1.0E-6) {
                                                                           everySupport = new SupportStandUtils.Candidate(intent, everySupportValue12);
                                                                        }
                                                                        break;
                                                                     }
                                                                  }
                                                               }
                                                            }
                                                         }
                                                      }
                                                   }
                                                }
                                             }
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
               }
            }
         }
      }

      if (everySupport != null) {
         return everySupport;
      } else {
         String everySupportText;
         if (!everySupportFlag3) {
            everySupportText = !arrayList.isEmpty()
               ? "SUPPORT_UNBUILT (its only supports are remaining schematic cells)"
               : (
                  !supplier.isEmpty()
                     ? "SUPPORT_TEAMMATE (its only supports are blocks assigned to a teammate or still in the job pool)"
                     : (
                        everySupportFlag4
                           ? "NO_SUPPORT_FACE (every neighbour is air \u2014 needs a temporary support)"
                           : "NO_SUPPORT_FACE (every neighbour is air and no dirt may be placed beside it)"
                     )
               );
         } else if (!everySupportFlag5) {
            SupportStandUtils.Cell totalPriceData = getCell(overlay, target);
            if (totalPriceData != null) {
               supplier.add(totalPriceData);
               everySupportText = "SUPPORT_TEAMMATE (every candidate stand floats over a teammate's unbuilt cell " + totalPriceData + ")";
            } else if (everySupportFlag8) {
               everySupportText = "STAND_UNREACHABLE_FROM_HERE (every candidate stand was proven unreachable from this walkable component; needs constructive access)";
            } else {
               everySupportText = "NO_REACHABLE_STAND (no standable cell within reach; " + formatFloorsOccupied(overlay, target, set) + ")";
            }
         } else if (everySupportFlag7 && !everySupportFlag6) {
            everySupportText = "EGRESS_SEALS_STAND (placing it from every reachable stand would seal the builder in \u2014 placed last, from outside)";
         } else if (!everySupportFlag6) {
            everySupportText = "NO_FACING_POSE (no reachable stand gives a look that produces facing=" + formatSouthNorth(target.facing()) + ")";
         } else {
            everySupportText = "NO_LINE_OF_SIGHT (every facing-correct aim from every reachable stand is occluded)";
         }

         ArrayList arrayList3 = arrayList;
         if (!supplier.isEmpty()) {
            arrayList3 = new ArrayList(arrayList);
            arrayList3.addAll(supplier);
         }

         list.add(new SupportStandUtils.Blocker(target.cell(), everySupportText, List.copyOf(arrayList3)));
         return null;
      }
   }

   private static void handleDepends(
      SupportStandUtils.Cell cell,
      Set<SupportStandUtils.Cell> set,
      Map<SupportStandUtils.Cell, SupportStandUtils.Blocker> map,
      Map<SupportStandUtils.Cell, Integer> map2,
      Map<SupportStandUtils.Cell, Integer> map3,
      Set<SupportStandUtils.Cell> hashSet,
      ArrayDeque<SupportStandUtils.Cell> arrayDeque,
      List<List<SupportStandUtils.Cell>> list,
      int[] dependsValues
   ) {
      map2.put(cell, dependsValues[0]);
      map3.put(cell, dependsValues[0]);
      dependsValues[0]++;
      arrayDeque.push(cell);
      hashSet.add(cell);
      SupportStandUtils.Blocker scc = (SupportStandUtils.Blocker)map.get(cell);
      if (scc != null) {
         for (SupportStandUtils.Cell dependsOnItem : scc.dependsOn()) {
            if (set.contains(dependsOnItem)) {
               if (!map2.containsKey(dependsOnItem)) {
                  handleDepends(dependsOnItem, set, map, map2, map3, hashSet, arrayDeque, list, dependsValues);
                  map3.put(cell, Math.min((Integer)map3.get(cell), (Integer)map3.get(dependsOnItem)));
               } else if (hashSet.contains(dependsOnItem)) {
                  map3.put(cell, Math.min((Integer)map3.get(cell), (Integer)map2.get(dependsOnItem)));
               }
            }
         }
      }

      if (((Integer)map3.get(cell)).equals(map2.get(cell))) {
         var arrayList = new ArrayList();

         SupportStandUtils.Cell lowerCase;
         do {
            lowerCase = (SupportStandUtils.Cell)arrayDeque.pop();
            hashSet.remove(lowerCase);
            arrayList.add(lowerCase);
         } while (!lowerCase.equals(cell));

         list.add(arrayList);
      }
   }

   static boolean canFaceFacing(SupportStandUtils.Target target) {
      return target.kind() == SupportStandUtils.Kind.FACE_PLAYER && target.facing() == 0 || target.kind() == SupportStandUtils.Kind.FACE_AWAY && target.facing() == 1;
   }

   private SupportStandUtils() {
   }

   static boolean canFacingTrapdoor(SupportStandUtils.Target target, int facingTrapdoorValue, double facingTrapdoorValue2, double supportStandLimit, double spotValue) {
      boolean facingTrapdoorFlag = target.kind() == SupportStandUtils.Kind.TRAPDOOR_TOP;
      if (!facingTrapdoorFlag && target.kind() != SupportStandUtils.Kind.TRAPDOOR_BOTTOM) {
         return true;
      } else if (PistonStickyUtils.canFlag(facingTrapdoorValue)) {
         return facingTrapdoorValue == 0 == facingTrapdoorFlag && (target.facing() < 0 || getValue(supportStandLimit, spotValue) == PistonStickyUtils.getValue(target.facing()));
      } else {
         double facingTrapdoorLimit = facingTrapdoorValue2 - target.cell().y();
         return (target.facing() < 0 || facingTrapdoorValue == target.facing()) && (facingTrapdoorFlag ? facingTrapdoorLimit >= 0.5625 : facingTrapdoorLimit <= 0.4375);
      }
   }

   public static List<SupportStandUtils.Box> getItems(SupportStandUtils.Shape shape) {
      return switch (shape) {
         case FULL, HOPPER -> FULL_BOXES;
         case CHEST -> CHEST_BOXES;
         case THIN -> THIN_BOXES;
         case NONE -> NO_BOXES;
      };
   }

   static List<double[]> getUnmodifiable(boolean unmodifiableFlag) {
      return unmodifiableFlag ? unmodifiableDoubleArrayList : unmodifiableList;
   }

   static Set<SupportStandUtils.Cell> getSet(SupportStandUtils.Overlay overlay, SupportStandUtils.Nav nav, Set<SupportStandUtils.Cell> set) {
      var hashSet = new HashSet();

      for (SupportStandUtils.Cell cell : set) {
         if (!set.contains(cell.getDataCell())
            && set.contains(cell.getData222())
            && nav.getKeySet().contains(cell.getDataCell())
            && overlay.canLavaDirt(cell.getDataCell())) {
            int supportStandCounter = 0;

            for (SupportStandUtils.Cell cell2 = cell; set.contains(cell2); cell2 = cell2.getData222()) {
               supportStandCounter++;
            }

            if (supportStandCounter >= 3) {
               for (SupportStandUtils.Cell goodsSlot = cell; set.contains(goodsSlot); goodsSlot = goodsSlot.getData222()) {
                  hashSet.add(goodsSlot);
               }
            }
         }
      }

      return hashSet;
   }

   static List<SupportStandUtils.Cell> getItems(
      SupportStandUtils.Overlay overlay,
      SupportStandUtils.Cell cell,
      SupportStandUtils.Cell cell2,
      Set<SupportStandUtils.Cell> set,
      SupportStandUtils.Options options,
      int supportStandLimit
   ) {
      if (supportStandLimit <= 0) {
         return null;
      } else if (overlay.canFlag(cell2, cell)) {
         return List.of(cell2);
      } else {
         var hashMap = new HashMap();
         var hashMap2 = new HashMap();
         var arrayDeque = new ArrayDeque();
         hashMap.put(cell2, null);
         hashMap2.put(cell2, 1);
         arrayDeque.add(cell2);

         while (!arrayDeque.isEmpty()) {
            SupportStandUtils.Cell supportStandUtilsCell = (SupportStandUtils.Cell)arrayDeque.poll();
            int supportStandValue = (Integer)hashMap2.get(supportStandUtilsCell);
            if (supportStandValue < supportStandLimit) {
               int[] ints = new int[]{0, 2, 3, 4, 5, 1};

               for (int intsItem : ints) {
                  SupportStandUtils.Cell formatGroundAir = supportStandUtilsCell.getData222(intsItem);
                  if (!formatGroundAir.equals(cell)
                     && !hashMap.containsKey(formatGroundAir)
                     && !set.contains(formatGroundAir)
                     && overlay.canDirtLava(formatGroundAir, options)
                     && (!formatGroundAir.equals(cell.getDataCell()) || !supportStandUtilsCell.equals(cell))) {
                     hashMap.put(formatGroundAir, supportStandUtilsCell);
                     hashMap2.put(formatGroundAir, supportStandValue + 1);
                     if (canFlag(overlay, formatGroundAir, hashMap.keySet(), cell)) {
                        var arrayList = new ArrayList();

                        for (SupportStandUtils.Cell formatMinPop = formatGroundAir; formatMinPop != null; formatMinPop = (SupportStandUtils.Cell)hashMap.get(formatMinPop)) {
                           arrayList.add(formatMinPop);
                        }

                        return arrayList;
                     }

                     arrayDeque.add(formatGroundAir);
                  }
               }
            }
         }

         return null;
      }
   }

   private static boolean canFacingKind(SupportStandUtils.Target target, int facingKindValue, double supportStandLimit, double spotValue, boolean facingKindFlag) {
      return switch (target.kind()) {
         case FACE_PLAYER -> PistonStickyUtils.getValue(facingKindValue) == target.facing();
         case FACE_AWAY -> facingKindValue == target.facing();
         case YAW_OPPOSITE -> facingKindFlag || target.facing() < 0 || getValue(supportStandLimit, spotValue) == PistonStickyUtils.getValue(target.facing());
         case YAW_AXIS -> target.facing() < 0 || canFlag(getValue(supportStandLimit, spotValue), target.facing());
         default -> true;
      };
   }

   static double[] getAmounts(SupportStandUtils.Cell cell, SupportStandUtils.Box box, int supportStandValue, double supportStandFactor, double supportStandValue3) {
      double supportStandValue4 = cell.x() + box.x0();
      double supportStandValue5 = cell.x() + box.x1();
      double supportStandValue6 = cell.y() + box.y0();
      double supportStandValue7 = cell.y() + box.y1();
      double supportStandValue8 = cell.z() + box.z0();
      double supportStandValue9 = cell.z() + box.z1();

      return switch (supportStandValue) {
         case 0 -> new double[]{getAmount(supportStandValue4, supportStandValue5, supportStandFactor), supportStandValue6, getAmount(supportStandValue8, supportStandValue9, supportStandValue3)};
         case 1 -> new double[]{getAmount(supportStandValue4, supportStandValue5, supportStandFactor), supportStandValue7, getAmount(supportStandValue8, supportStandValue9, supportStandValue3)};
         case 2 -> new double[]{getAmount(supportStandValue4, supportStandValue5, supportStandFactor), getAmount(supportStandValue6, supportStandValue7, supportStandValue3), supportStandValue8};
         case 3 -> new double[]{getAmount(supportStandValue4, supportStandValue5, supportStandFactor), getAmount(supportStandValue6, supportStandValue7, supportStandValue3), supportStandValue9};
         case 4 -> new double[]{supportStandValue4, getAmount(supportStandValue6, supportStandValue7, supportStandFactor), getAmount(supportStandValue8, supportStandValue9, supportStandValue3)};
         default -> new double[]{supportStandValue5, getAmount(supportStandValue6, supportStandValue7, supportStandFactor), getAmount(supportStandValue8, supportStandValue9, supportStandValue3)};
      };
   }

   private static boolean canSupportUnbuilt(
      SupportStandUtils.Overlay overlay, Set<SupportStandUtils.Cell> set, List<SupportStandUtils.Blocker> list, SupportStandUtils.Options options, int supportUnbuiltBound
   ) {
      boolean supportUnbuiltFlag = false;
      var mutualDependency = getMutualDependency(set, list);

      for (SupportStandUtils.Blocker blocker : list) {
         SupportStandUtils.Cell target = blocker.target();
         if (!overlay.isContainsKey(target) && set.contains(target)) {
            boolean startsWith = blocker.reason().startsWith("NO_SUPPORT_FACE");
            boolean supportUnbuiltFlag2 = blocker.reason().startsWith("SUPPORT_UNBUILT") && mutualDependency.contains(target);
            if (startsWith || supportUnbuiltFlag2) {
               SupportStandUtils.Target lavaDirt = overlay.getLavaDirt(target);
               List list2 = null;

               for (int supportUnbuiltValue : getFacingSight(lavaDirt)) {
                  SupportStandUtils.Cell cell = target.getData222(supportUnbuiltValue);
                  if (!set.contains(cell) && overlay.canDirtLava(cell, options)) {
                     var items3 = getItems(overlay, target, cell, set, options, Math.min(3, supportUnbuiltBound));
                     if (items3 != null && (list2 == null || items3.size() < list2.size())) {
                        list2 = items3;
                     }
                  }
               }

               if (list2 != null) {
                  for (SupportStandUtils.Cell cell2 : (Iterable<com.corz.client.utils.SupportStandUtils.Cell>) (list2)) {
                     overlay.handleDirtPlain(cell2, target);
                     set.add(cell2);
                  }

                  supportUnbuiltFlag = true;
               }
            }
         }
      }

      return supportUnbuiltFlag || canFacingPose(overlay, set, list, options, supportUnbuiltBound);
   }

   private static boolean canCellStand(
      SupportStandUtils.Overlay overlay,
      SupportStandUtils.Nav nav,
      SupportStandUtils.Options options,
      Set<SupportStandUtils.Cell> set,
      SupportStandUtils.Removal removal
   ) {
      var linkedHashSet = new LinkedHashSet(set);
      linkedHashSet.remove(removal.cell());
      if (linkedHashSet.isEmpty()) {
         return true;
      } else {
         var hashSet = new HashSet<>(overlay.lavaApiCellSet);
         var hashSet2 = new HashSet<>(overlay.cellStandCellSet);
         SupportStandUtils.Diff addedRemoved = nav.getOrDefault(removal.stand()).getAddedRemoved(removal.cell());

         for (SupportStandUtils.Cell cell : addedRemoved.added()) {
            overlay.handle(cell);
         }

         for (SupportStandUtils.Cell removedItem : addedRemoved.removed()) {
            overlay.handleForCell(removedItem);
         }

         try {
            SupportStandUtils.Nav trailWalk = SupportStandUtils.Nav.getTrailWalk(overlay, removal.stand(), options, 0, linkedHashSet, linkedHashSet);

            for (SupportStandUtils.Cell cell3 : (Iterable<com.corz.client.utils.SupportStandUtils.Cell>) (linkedHashSet)) {
               for (SupportStandUtils.Cell getKeyItem : trailWalk.getKeySet()) {
                  if (canMinReach(overlay, options, linkedHashSet, cell3, getKeyItem)) {
                     return true;
                  }
               }
            }

            return false;
         } finally {
            overlay.lavaApiCellSet.clear();
            overlay.lavaApiCellSet.addAll(hashSet);
            overlay.cellStandCellSet.clear();
            overlay.cellStandCellSet.addAll(hashSet2);
         }
      }
   }

   private static boolean canSightFacing(SupportStandUtils.Overlay overlay, List<SupportStandUtils.Blocker> list) {
      for (SupportStandUtils.Blocker blocker : list) {
         SupportStandUtils.Target lavaDirt = overlay.getLavaDirt(blocker.target());
         int[] sightFacingValues2 = lavaDirt == null ? sightFacingValues : getFacingSight(lavaDirt);
         if (sightFacingValues2.length == 1
            && !PistonStickyUtils.canFlag(sightFacingValues2[0])
            && (blocker.reason().startsWith("NO_LINE_OF_SIGHT") || blocker.reason().startsWith("NO_FACING_POSE") || blocker.reason().startsWith("NO_REACHABLE_STAND"))) {
            return true;
         }
      }

      return false;
   }

   static boolean canCampfireFacing(SupportStandUtils.Target target, SupportStandUtils.Cell cell) {
      return target != null
         && target.kind() == SupportStandUtils.Kind.YAW_OPPOSITE
         && target.facing() >= 2
         && "campfire".equals(target.name())
         && target.cell().getData222(target.facing()).equals(cell);
   }

   public static String formatSupportStand(
      SupportStandUtils.World world,
      List<SupportStandUtils.Target> list,
      Set<SupportStandUtils.Cell> set,
      Set<SupportStandUtils.Cell> set2,
      SupportStandUtils.Intent intent
   ) {
      SupportStandUtils.Overlay overlay = new SupportStandUtils.Overlay(world, list);

      for (SupportStandUtils.Cell cell : set) {
         overlay.apiLavaCellSet.add(cell);
      }

      for (SupportStandUtils.Cell cell2 : set2) {
         overlay.handle(cell2);
      }

      SupportStandUtils.Target lavaDirt = overlay.getLavaDirt(intent.target());
      if (lavaDirt == null) {
         return "unknown target";
      } else if (!overlay.canLavaDirt(intent.stand())) {
         return "stand not standable";
      } else {
         boolean supportStandFlag = intent.stand().equals(lavaDirt.cell())
            && lavaDirt.shape() == SupportStandUtils.Shape.NONE
            && lavaDirt.kind() == SupportStandUtils.Kind.YAW_OPPOSITE
            && intent.support().equals(lavaDirt.cell().getData222());
         if ((!intent.stand().equals(lavaDirt.cell()) || supportStandFlag) && !intent.stand().getDataCell().equals(lavaDirt.cell())) {
            double[] supportStandValues = overlay.getAmounts(intent.stand(), intent.sneak());
            double[] doubles = new double[]{intent.hx(), intent.hy(), intent.hz()};
            if (overlay.getCellBoxes(intent.support()).isEmpty() && overlay.getExtraBoxes(intent.support()).isEmpty()) {
               return "support is not solid";
            } else if (!overlay.canFlag(supportStandValues, doubles, intent.support(), false)) {
               return "line of sight blocked";
            } else {
               double supportStandValue = doubles[0] - supportStandValues[0];
               double supportStandValue2 = doubles[1] - supportStandValues[1];
               double spotValue = doubles[2] - supportStandValues[2];
               int facingKindValue = PistonStickyUtils.getValue(supportStandValue, supportStandValue2, spotValue);
               boolean facingKindFlag = lavaDirt.partner() != null && overlay.canLavaDirtApiLavaCellSet(lavaDirt.partner()) && intent.support().equals(lavaDirt.partner());
               if (!canFacingKind(lavaDirt, facingKindValue, supportStandValue, spotValue, facingKindFlag)) {
                  return "look " + formatSouthNorth(facingKindValue) + " does not produce facing " + formatSouthNorth(lavaDirt.facing());
               } else if (!canFacingTrapdoor(lavaDirt, intent.face(), intent.hy(), supportStandValue, spotValue)) {
                  return "clicked face "
                     + formatSouthNorth(intent.face())
                     + " does not give the trapdoor facing "
                     + formatSouthNorth(lavaDirt.facing())
                     + " at its half";
               } else {
                  if (lavaDirt.kind() == SupportStandUtils.Kind.HOPPER) {
                     int southNorthValue = PistonStickyUtils.canFlag(intent.face()) ? 0 : PistonStickyUtils.getValue(intent.face());
                     if (lavaDirt.facing() >= 0 && southNorthValue != lavaDirt.facing()) {
                        return "clicked face would give hopper facing " + formatSouthNorth(southNorthValue);
                     }
                  }

                  return null;
               }
            }
         } else {
            return "body overlaps target";
         }
      }
   }

   static SupportStandUtils.Cell getDataCell(SupportStandUtils.Overlay overlay, SupportStandUtils.Cell cell) {
      overlay.handleForCell(cell);

      try {
         for (int i = 0; i < 2; i++) {
            if (overlay.canLavaDirt(cell.getDataForInt(i))) {
               return cell.getDataForInt(i);
            }
         }

         return null;
      } finally {
         overlay.handle(cell);
      }
   }

   public static SupportStandUtils.Mission getDataForWorldSet(
      SupportStandUtils.World world, Set<SupportStandUtils.Cell> set, SupportStandUtils.Cell cell, SupportStandUtils.Options options
   ) {
      return getCellColumn(world, set, cell, options, false);
   }

   private static double getAmount(double supportStandValue, double supportStandValue2, double supportStandFactor) {
      return supportStandValue + (supportStandValue2 - supportStandValue) * supportStandFactor;
   }

   static int getLavaDirt(SupportStandUtils.Overlay overlay, SupportStandUtils.Cell cell, SupportStandUtils.Options options) {
      SupportStandUtils.Nav trailWalk = SupportStandUtils.Nav.getTrailWalk(overlay, cell, options, 0, Set.of(), Set.of(cell));
      int lavaDirt = cell.y();

      for (SupportStandUtils.Cell getKeyItem : trailWalk.getKeySet()) {
         lavaDirt = Math.min(lavaDirt, getKeyItem.y());
      }

      if (lavaDirt >= cell.y() && !overlay.lavaDirtMap.isEmpty()) {
         SupportStandUtils.Nav lavaDirt2 = SupportStandUtils.Nav.getLavaDirt(overlay, cell, options, 0, Set.of());
         var hashMap = new HashMap();

         for (SupportStandUtils.Cell cell3 : lavaDirt2.getKeySet()) {
            hashMap.merge(cell3.y(), 1, (obj, obj2) -> Integer.sum((Integer) obj, (Integer) obj2));
         }

         int lavaDirt3 = cell.y();
         int lavaDirtValue = 0;

         for (Entry entry : (Iterable<java.util.Map.Entry>) (hashMap.entrySet())) {
            if ((Integer)entry.getValue() > lavaDirtValue || (Integer)entry.getValue() == lavaDirtValue && (Integer)entry.getKey() > lavaDirt3) {
               lavaDirt3 = (Integer)entry.getKey();
               lavaDirtValue = (Integer)entry.getValue();
            }
         }

         if (lavaDirt3 >= cell.y() - 2) {
            return lavaDirt;
         } else {
            overlay.lavaDirtCellSet = new HashSet<>(lavaDirt2.getKeySet());
            return lavaDirt3;
         }
      } else {
         return lavaDirt;
      }
   }

   private static SupportStandUtils.Mission getIntentCell(
      SupportStandUtils.World world,
      List<SupportStandUtils.Target> list,
      SupportStandUtils.Cell cell,
      SupportStandUtils.Options options,
      String intentCellText,
      long intentCellValue,
      SupportStandUtils.PlanStream planStream
   ) {
      SupportStandUtils.Overlay overlay = new SupportStandUtils.Overlay(world, list);
      overlay.overlayFloorY = getLavaDirt(overlay, cell, options);
      var arrayList = new ArrayList();
      var arrayList2 = new ArrayList();
      var linkedHashSet = new LinkedHashSet();

      for (SupportStandUtils.Target target : list) {
         linkedHashSet.add(target.cell());
      }

      for (SupportStandUtils.Target target2 : list) {
         if (!world.getCellBoxes(target2.cell()).isEmpty() && !target2.isKindFluid()) {
            arrayList.add(new SupportStandUtils.Step(SupportStandUtils.Op.BREAK, target2.cell(), null, 1, null, target2.name() + " cell holds a wrong block"));
         }
      }

      SupportStandUtils.Cell cell2 = cell;
      int intentCellCounter = 0;
      Object of = List.of();
      boolean intentCellFlag = false;
      int intentCellCounter2 = 0;

      while (!linkedHashSet.isEmpty() && intentCellCounter2++ < 96) {
         if (System.nanoTime() > intentCellValue) {
            intentCellFlag = true;
            break;
         }

         if (Thread.currentThread().isInterrupted()) {
            intentCellFlag = true;
            break;
         }

         boolean anyMatch = linkedHashSet.stream().anyMatch(obj -> !overlay.getLavaDirt(((com.corz.client.utils.SupportStandUtils.Cell) (obj))).isKindFluid());
         SupportStandUtils.Nav lavaDirt = SupportStandUtils.Nav.getLavaDirt(overlay, cell2, options, 0, linkedHashSet);
         var arrayList3 = new ArrayList();
         SupportStandUtils.Candidate costIntent = getCostIntent(overlay, lavaDirt, options, linkedHashSet, arrayList3, anyMatch, options.dirtBudget() - intentCellCounter);
         if (costIntent == null && options.dirtBudget() - intentCellCounter > 0 && canStandSeals(arrayList3)) {
            arrayList3.clear();
            lavaDirt = SupportStandUtils.Nav.getLavaDirt(overlay, cell2, options, options.dirtBudget() - intentCellCounter, linkedHashSet);
            costIntent = getCostIntent(overlay, lavaDirt, options, linkedHashSet, arrayList3, anyMatch, options.dirtBudget() - intentCellCounter);
         }

         if (costIntent == null) {
            if (intentCellCounter < options.dirtBudget() && canSupportUnbuilt(overlay, linkedHashSet, arrayList3, options, options.dirtBudget() - intentCellCounter)) {
               continue;
            }

            if (options.allowEdgeAim() && intentCellCounter < options.dirtBudget() && canSightFacing(overlay, arrayList3)) {
               overlay.budgetActive = true;

               try {
                  lavaDirt = SupportStandUtils.Nav.getLavaDirt(overlay, cell2, options, options.dirtBudget() - intentCellCounter, linkedHashSet);
               } finally {
                  overlay.budgetActive = false;
               }

               costIntent = getCostIntent(overlay, lavaDirt, options, linkedHashSet, new ArrayList<>(), anyMatch, options.dirtBudget() - intentCellCounter);
            }

            if (costIntent == null) {
               of = arrayList3;
               break;
            }
         }

         SupportStandUtils.Target target3 = options.dirtBudget() - intentCellCounter > 0 ? getDataForOverlayCell(overlay, costIntent.intent.target(), linkedHashSet) : null;
         if (target3 != null) {
            SupportStandUtils.Nav nav = SupportStandUtils.Nav.getLavaDirt(overlay, cell2, options, options.dirtBudget() - intentCellCounter, linkedHashSet);
            SupportStandUtils.Candidate cellIntent = getCellIntent(overlay, target3, nav, options, linkedHashSet, new ArrayList<>());
            if (cellIntent != null) {
               lavaDirt = nav;
               costIntent = cellIntent;
            }
         }

         SupportStandUtils.Cell escalations = cell2;

         for (SupportStandUtils.Nav.Edge edge : lavaDirt.getWalkTrail(costIntent.intent.stand())) {
            switch (edge.op) {
               case WALK:
                  arrayList.add(new SupportStandUtils.Step(SupportStandUtils.Op.WALK, edge.to, edge.to, 0, null, ""));
                  escalations = edge.to;
                  break;
               case PILLAR:
                  if (!escalations.equals(edge.from)) {
                     arrayList.add(new SupportStandUtils.Step(SupportStandUtils.Op.WALK, edge.from, edge.from, 0, null, ""));
                  }

                  for (int i = 0; i < edge.count; i++) {
                     overlay.handle(edge.from.getData223(i));
                  }

                  intentCellCounter += edge.count;
                  arrayList.add(
                     new SupportStandUtils.Step(SupportStandUtils.Op.PILLAR, edge.from, edge.to, edge.count, null, "reach stand " + costIntent.intent.stand())
                  );
                  escalations = edge.to;
                  break;
               case DIRT:
                  SupportStandUtils.Cell o = edge.placeFrom == null ? edge.from : edge.placeFrom;
                  if (!escalations.equals(o)) {
                     arrayList.add(new SupportStandUtils.Step(SupportStandUtils.Op.WALK, o, o, 0, null, ""));
                  }

                  overlay.handle(edge.cell);
                  intentCellCounter++;
                  arrayList.add(new SupportStandUtils.Step(SupportStandUtils.Op.DIRT, edge.cell, o, 1, edge.intent, "floor for stand " + edge.to));
                  arrayList.add(new SupportStandUtils.Step(SupportStandUtils.Op.WALK, edge.to, edge.to, 0, null, ""));
                  escalations = edge.to;
                  break;
               case DIG_DOWN:
                  if (!escalations.equals(edge.from)) {
                     arrayList.add(new SupportStandUtils.Step(SupportStandUtils.Op.WALK, edge.from, edge.from, 0, null, ""));
                  }

                  overlay.handleForCell(edge.cell);
                  arrayList.add(new SupportStandUtils.Step(SupportStandUtils.Op.DIG_DOWN, edge.cell, edge.to, 1, null, "descend own column"));
                  escalations = edge.to;
            }
         }

         if (!escalations.equals(costIntent.intent.stand())) {
            arrayList.add(new SupportStandUtils.Step(SupportStandUtils.Op.WALK, costIntent.intent.stand(), costIntent.intent.stand(), 0, null, ""));
         }

         SupportStandUtils.Target lavaDirt3 = overlay.getLavaDirt(costIntent.intent.target());
         if (overlay.isContainsKey(lavaDirt3.cell())) {
            overlay.handle(lavaDirt3.cell());
            intentCellCounter++;
            arrayList.add(
               new SupportStandUtils.Step(
                  SupportStandUtils.Op.DIRT, lavaDirt3.cell(), costIntent.intent.stand(), 1, costIntent.intent, "temporary support for " + overlay.getDirt(lavaDirt3.cell())
               )
            );
         } else {
            if (lavaDirt3.isKindFluid() && overlay.canApiAdded(lavaDirt3.cell())) {
               overlay.handleForCell(lavaDirt3.cell());
               arrayList.add(
                  new SupportStandUtils.Step(
                     SupportStandUtils.Op.REMOVE_DIRT, lavaDirt3.cell(), costIntent.intent.stand(), 1, null, "fluid cell must be empty before the pour"
                  )
               );
            }

            arrayList.add(new SupportStandUtils.Step(SupportStandUtils.Op.PLACE, lavaDirt3.cell(), costIntent.intent.stand(), 1, costIntent.intent, lavaDirt3.name()));
            overlay.handleCell(lavaDirt3);
            arrayList2.add(lavaDirt3.cell());
         }

         linkedHashSet.remove(lavaDirt3.cell());
         cell2 = costIntent.intent.stand();
         if (planStream != null) {
            planStream.handleAtomicInteger(arrayList);
         }
      }

      var arrayList4 = new ArrayList();

      for (SupportStandUtils.Cell cell4 : (Iterable<com.corz.client.utils.SupportStandUtils.Cell>) (linkedHashSet)) {
         if (!overlay.isContainsKey(cell4)) {
            arrayList4.add(cell4);
         }
      }

      String noneDependsText = intentCellFlag ? (intentCellText + " time-budget").trim() : intentCellText;
      var list2 = arrayList4.isEmpty() ? List.of() : getNoneDepends(arrayList4, (List<SupportStandUtils.Blocker>)of, noneDependsText);
      SupportStandUtils.Status status = arrayList4.isEmpty() ? SupportStandUtils.Status.SOLVED : SupportStandUtils.Status.PARTIAL;
      return new SupportStandUtils.Mission(
         status,
         List.copyOf(arrayList),
         List.copyOf(arrayList2),
         List.copyOf(arrayList4),
         List.copyOf((Collection<? extends SupportStandUtils.Blocker>)of),
         ((java.util.List) (Object) (list2)),
         intentCellCounter,
         noneDependsText.trim()
      );
   }

   private static SupportStandUtils.Removal getCellStand(
      SupportStandUtils.Overlay overlay, SupportStandUtils.Nav nav, SupportStandUtils.Options options, Set<SupportStandUtils.Cell> set, SupportStandUtils.Cell cell
   ) {
      if (overlay.costCellActive && set.size() > 1) {
         var hashSet = new HashSet();

         for (int i = 0; i < 8; i++) {
            SupportStandUtils.Removal costCell = getCostCell(overlay, nav, options, set, cell, hashSet);
            if (costCell == null || canCellStand(overlay, nav, options, set, costCell)) {
               return costCell;
            }

            hashSet.add(SupportStandUtils.Options.getTime(costCell.cell(), costCell.stand()));
            overlay.cellColumnCellSet.add(costCell.cell());
         }

         return null;
      } else {
         return getCostCell(overlay, nav, options, set, cell, Set.of());
      }
   }

   public static String formatSouthNorth(int southNorthValue) {
      return switch (southNorthValue) {
         case 0 -> "down";
         case 1 -> "up";
         case 2 -> "north";
         case 3 -> "south";
         case 4 -> "west";
         case 5 -> "east";
         default -> "?";
      };
   }

   public static void update() {
      everySupportCallCount = 0;
   }

   private static SupportStandUtils.Cell getCell(SupportStandUtils.Overlay overlay, SupportStandUtils.Target target) {
      for (int i = -2; i <= 2; i++) {
         for (int j = -2; j <= 2; j++) {
            for (int cellValue : ProofSupportUtils.coverageTable) {
               SupportStandUtils.Cell cell = target.cell().getDataX(i, cellValue, j);
               if (!cell.equals(target.cell()) && overlay.isForeignCell(cell.getData222()) && overlay.getCellBoxes(cell).isEmpty()) {
                  return cell.getData222();
               }
            }
         }
      }

      return null;
   }

   private static SupportStandUtils.Candidate getCellIntent(
      SupportStandUtils.Overlay overlay,
      SupportStandUtils.Target target,
      SupportStandUtils.Nav nav,
      SupportStandUtils.Options options,
      Set<SupportStandUtils.Cell> set,
      List<SupportStandUtils.Blocker> list
   ) {
      SupportStandUtils.Candidate everySupport = getEverySupport(overlay, target, nav, options, set, list, false, false);
      if (everySupport == null && target.isKindFluid() && !list.isEmpty() && target.cell().equals(((SupportStandUtils.Blocker)list.get(list.size() - 1)).target())) {
         SupportStandUtils.Blocker blocker = (SupportStandUtils.Blocker)list.remove(list.size() - 1);
         everySupport = getEverySupport(overlay, target, nav, options, set, list, false, true);
         if (everySupport == null) {
            list.set(list.size() - 1, blocker);
         }
      }

      if (everySupport != null && !ProofSupportUtils.canFlag(everySupport.intent.stand().y(), target.cell().y())) {
         ATOMIC_INTEGER.incrementAndGet();
      }

      return everySupport;
   }

   private static SupportStandUtils.Candidate getCostIntent(
      SupportStandUtils.Overlay overlay,
      SupportStandUtils.Nav nav,
      SupportStandUtils.Options options,
      Set<SupportStandUtils.Cell> set,
      List<SupportStandUtils.Blocker> list,
      boolean costIntentFlag,
      int costIntentLimit
   ) {
      SupportStandUtils.Candidate costIntent = null;

      for (SupportStandUtils.Cell cell : set) {
         SupportStandUtils.Target lavaDirt = overlay.getLavaDirt(cell);
         if (!lavaDirt.isKindFluid() || !costIntentFlag) {
            SupportStandUtils.Candidate cellIntent = getCellIntent(overlay, lavaDirt, nav, options, set, list);
            if (cellIntent != null) {
               int costIntentValue = nav.getOrDefault(cellIntent.intent().stand()).getAdded() + (overlay.isContainsKey(cell) ? 1 : 0);
               if (costIntentValue <= costIntentLimit
                  && (
                     costIntent == null
                        || cellIntent.cost < costIntent.cost - 1.0E-6
                        || Math.abs(cellIntent.cost - costIntent.cost) <= 1.0E-6 && cellIntent.intent.margin() > costIntent.intent.margin()
                  )) {
                  costIntent = cellIntent;
               }
            }
         }
      }

      return costIntent;
   }

   private static double getAmount(double[] supportStandValues, double supportStandValue, double supportStandValue2, double supportStandValue3) {
      double supportStandFactor = supportStandValues[0] - supportStandValue;
      double supportStandFactor2 = supportStandValues[1] - supportStandValue2;
      double supportStandFactor3 = supportStandValues[2] - supportStandValue3;
      return supportStandFactor * supportStandFactor + supportStandFactor2 * supportStandFactor2 + supportStandFactor3 * supportStandFactor3;
   }

   private static double getAmountForDouble(double supportStandBound, double absLook, double apiPutBound) {
      double supportStandValue = Math.max(supportStandBound, Math.max(absLook, apiPutBound));
      if (supportStandBound == supportStandValue) {
         return Math.max(absLook, apiPutBound);
      } else {
         return absLook == supportStandValue ? Math.max(supportStandBound, apiPutBound) : Math.max(supportStandBound, absLook);
      }
   }

   private static boolean canFlag(SupportStandUtils.Overlay overlay, SupportStandUtils.Cell cell, Set<SupportStandUtils.Cell> set, SupportStandUtils.Cell cell2) {
      for (int i = 0; i < 6; i++) {
         SupportStandUtils.Cell goodsSlot = cell.getData222(i);
         if (!goodsSlot.equals(cell2) && !set.contains(goodsSlot) && !overlay.getCellBoxes(goodsSlot).isEmpty()) {
            return true;
         }
      }

      return false;
   }

   public static SupportStandUtils.Mission getBudgetStands(
      SupportStandUtils.World world,
      List<SupportStandUtils.Target> list,
      SupportStandUtils.Cell cell,
      SupportStandUtils.Options options2,
      SupportStandUtils.PlanStream planStream
   ) {
      if (list.isEmpty()) {
         return new SupportStandUtils.Mission(SupportStandUtils.Status.EMPTY, List.of(), List.of(), List.of(), List.of(), List.of(), 0, "");
      } else {
         SupportStandUtils.Options options = options2;
         StringBuilder stringBuilder = new StringBuilder();
         SupportStandUtils.Mission budgetStands = null;
         StringBuilder stringBuilder2 = new StringBuilder("strict ");
         long budgetStandsValue = options2.budgetNanos() > 0L ? System.nanoTime() + options2.budgetNanos() : Long.MAX_VALUE;
         if (planStream != null) {
            planStream.updateTime();
         }

         for (int i = 0; i < 4; i++) {
            SupportStandUtils.Mission intentCell = getIntentCell(world, list, cell, options, stringBuilder.toString(), budgetStandsValue, i == 0 ? planStream : null);
            if (budgetStands == null || intentCell.placedOrder().size() > budgetStands.placedOrder().size()) {
               budgetStands = intentCell;
            }

            if (intentCell.isSolvedStatus()) {
               return intentCell;
            }

            if (System.nanoTime() > budgetStandsValue) {
               stringBuilder2.append("time-budget ");
               break;
            }

            if (i == 0 && planStream != null && !planStream.isAndSetAtomicInteger()) {
               stringBuilder2.append("streamed ");
               break;
            }

            if (i == 0 && !options.allowEdgeAim) {
               options = options.getStandsAllow();
               stringBuilder.append("edge-aim ");
               stringBuilder2.append("edge-aim ");
            } else if (i <= 1 && !options.allowFluidCellDirt) {
               options = options.getStandsAllowReach();
               stringBuilder.append("fluid-cell-dirt ");
               stringBuilder2.append("fluid-cell-dirt ");
            } else {
               if (i > 2 || options.allowFutureStands) {
                  break;
               }

               options = options.getAllowBanned();
               stringBuilder.append("future-stands ");
               stringBuilder2.append("future-stands ");
            }
         }

         if (budgetStands != null && !budgetStands.isSolvedStatus()) {
            String trim = stringBuilder2.toString().trim();
            if (budgetStands.escalations().contains("time-budget") && !trim.contains("time-budget")) {
               trim = (trim + " time-budget").trim();
            }

            if (!trim.equals(budgetStands.escalations().trim())) {
               budgetStands = new SupportStandUtils.Mission(
                  budgetStands.status(),
                  budgetStands.steps(),
                  budgetStands.placedOrder(),
                  budgetStands.unsolved(),
                  budgetStands.blockers(),
                  getNoneDepends(budgetStands.unsolved(), budgetStands.blockers(), trim),
                  budgetStands.dirtPlaced(),
                  trim
               );
            }
         }

         return budgetStands;
      }
   }

   public record Blocker(SupportStandUtils.Cell target, String reason, List<SupportStandUtils.Cell> dependsOn) {
      @Override
      public String toString() {
         return this.target + " " + this.reason + (this.dependsOn.isEmpty() ? "" : " dependsOn=" + this.dependsOn);
      }
   }

   public record Box(double x0, double y0, double z0, double x1, double y1, double z1) {
   }

   private record Candidate(SupportStandUtils.Intent intent, double cost) {
   }

   public record Cell(int x, int y, int z) {
      @Override
      public String toString() {
         return this.x + ", " + this.y + ", " + this.z;
      }

      public static SupportStandUtils.Cell getDataForLong(long packed) {
         return new SupportStandUtils.Cell(ProofSupportUtils.getValueInt(packed), ProofSupportUtils.getValue(packed), ProofSupportUtils.getValueForLong(packed));
      }

      public long getTime() {
         return ProofSupportUtils.getTime(this.x, this.y, this.z);
      }

      public int getMax(SupportStandUtils.Cell o) {
         return Math.max(Math.abs(this.x - o.x), Math.max(Math.abs(this.y - o.y), Math.abs(this.z - o.z)));
      }

      public SupportStandUtils.Cell getDataForInt(int n) {
         return this.getDataX(0, -n, 0);
      }

      public SupportStandUtils.Cell getDataCell() {
         return this.getDataX(0, 1, 0);
      }

      public SupportStandUtils.Cell getDataX(int dy, int dx, int dz) {
         return new SupportStandUtils.Cell(this.x + dy, this.y + dx, this.z + dz);
      }

      public SupportStandUtils.Cell getData222(int dir) {
         int[] cellValues = PistonStickyUtils.getValues(dir);
         return this.getDataX(cellValues[0], cellValues[1], cellValues[2]);
      }

      public SupportStandUtils.Cell getData222() {
         return this.getDataX(0, -1, 0);
      }

      public SupportStandUtils.Cell getData223(int n) {
         return this.getDataX(0, n, 0);
      }
   }

   public record Cycle(List<SupportStandUtils.Cell> members, List<SupportStandUtils.Blocker> blockers, String resolution) {
   }

   record Diff(List<SupportStandUtils.Cell> added, List<SupportStandUtils.Cell> removed) {
      static final SupportStandUtils.Diff INSTANCE = new SupportStandUtils.Diff(List.of(), List.of());

      SupportStandUtils.Diff getAddedRemoved(List<SupportStandUtils.Cell> cells) {
         var arrayList = new ArrayList<>(this.added);
         arrayList.addAll(cells);
         return new SupportStandUtils.Diff(List.copyOf(arrayList), this.removed);
      }

      SupportStandUtils.Diff getAddedRemoved(SupportStandUtils.Cell c) {
         var arrayList = new ArrayList<>(this.removed);
         arrayList.add(c);
         var arrayList2 = new ArrayList<>(this.added);
         arrayList2.remove(c);
         return new SupportStandUtils.Diff(List.copyOf(arrayList2), List.copyOf(arrayList));
      }

      int getAdded() {
         return this.added.size();
      }
   }

   public record Intent(
      SupportStandUtils.Cell target,
      SupportStandUtils.Cell stand,
      boolean sneak,
      SupportStandUtils.Cell support,
      int face,
      double hx,
      double hy,
      double hz,
      int look,
      double margin,
      boolean supportIsDirt
   ) {
      public long getSneakFace() {
         long sneakFaceValue = 3015156374637277761L;
         long sneakFace = -3750763034362895579L;
         long[] longs = new long[]{this.target.getTime(), this.stand.getTime(), this.support.getTime(), this.face, this.sneak ? 1L : 0L};
         long sneakFaceValue2 = sneakFaceValue ^ ((long)longs.length << 32 ^ sneakFaceValue) & -4294967296L;

         for (long i = sneakFaceValue2 ^ (0L ^ sneakFaceValue2) & 4294967295L; (int)i < (int)(i >>> 32); i ^= (i ^ i + 1L) & 4294967295L) {
            long sneakFaceValue3 = longs[(int)i];
            sneakFace = (sneakFace ^ sneakFaceValue3) * 1099511628211L;
         }

         return sneakFace;
      }
   }

   public static enum Kind {
      PLAIN,
      FACE_PLAYER,
      FACE_AWAY,
      YAW_OPPOSITE,
      YAW_AXIS,
      HOPPER,
      FLUID,
      TRAPDOOR_BOTTOM,
      TRAPDOOR_TOP;
   }

   public record Mission(
      SupportStandUtils.Status status,
      List<SupportStandUtils.Step> steps,
      List<SupportStandUtils.Cell> placedOrder,
      List<SupportStandUtils.Cell> unsolved,
      List<SupportStandUtils.Blocker> blockers,
      List<SupportStandUtils.Cycle> cycles,
      int dirtPlaced,
      String escalations
   ) {
      public boolean isSolvedStatus() {
         return this.status == SupportStandUtils.Status.SOLVED;
      }

      public int getCount() {
         return (int)this.steps.stream().filter(step -> step.op() == SupportStandUtils.Op.PLACE).count();
      }
   }

   static final class Nav {
      private static final int[][] walkInts = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
      private final Map<SupportStandUtils.Cell, Double> trailWalkDoubleMap = new HashMap<>();
      private static final int[][] navInts = new int[][]{{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};
      private final SupportStandUtils.Cell walkTrailCell;
      static final SupportStandUtils.Intent EMPTY_INTENT = new SupportStandUtils.Intent(
         new SupportStandUtils.Cell(0, 0, 0), new SupportStandUtils.Cell(0, 0, 0), false, new SupportStandUtils.Cell(0, 0, 0), 0, 0.0, 0.0, 0.0, 0, 0.0, false
      );
      private final Map<SupportStandUtils.Cell, SupportStandUtils.Nav.Edge> walkTrailEdgeMap = new HashMap<>();
      static final double[] reachTable = new double[]{0.0, -0.1, 0.1};
      private final Map<SupportStandUtils.Cell, SupportStandUtils.Diff> trailWalkDiffMap = new HashMap<>();

      static void handleDigDown(SupportStandUtils.Overlay overlay, SupportStandUtils.Cell cell, List<SupportStandUtils.Nav.Edge> list) {
         SupportStandUtils.Cell data222 = cell.getData222();
         if (overlay.canApiAdded(data222)) {
            SupportStandUtils.Cell cell3 = data222.getData222();
            if (!(overlay.getMax(cell3) < 0.8) || overlay.canApiAdded(cell3)) {
               if (!overlay.isHazardCell(cell3)) {
                  list.add(new SupportStandUtils.Nav.Edge(SupportStandUtils.Op.DIG_DOWN, cell, data222, data222, 1, null));
               }
            }
         }
      }

      SupportStandUtils.Nav.Edge getDataForCell(SupportStandUtils.Cell cell) {
         return this.walkTrailEdgeMap.get(cell);
      }

      static int[] getValues(Set<SupportStandUtils.Cell> set) {
         int[] ints = new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MAX_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE, Integer.MIN_VALUE};

         for (SupportStandUtils.Cell cell : set) {
            ints[0] = Math.min(ints[0], cell.x());
            ints[1] = Math.min(ints[1], cell.y());
            ints[2] = Math.min(ints[2], cell.z());
            ints[3] = Math.max(ints[3], cell.x());
            ints[4] = Math.max(ints[4], cell.y());
            ints[5] = Math.max(ints[5], cell.z());
         }

         return ints;
      }

      SupportStandUtils.Diff getOrDefault(SupportStandUtils.Cell cell) {
         return this.trailWalkDiffMap.getOrDefault(cell, SupportStandUtils.Diff.INSTANCE);
      }

      static SupportStandUtils.Intent getMin(
         SupportStandUtils.Overlay overlay,
         SupportStandUtils.Cell cell,
         SupportStandUtils.Cell cell2,
         SupportStandUtils.Options options,
         SupportStandUtils.Nav nav,
         Set<SupportStandUtils.Cell> set,
         Map<Long, SupportStandUtils.Intent> map
      ) {
         long navValue = cell.getTime() * -7046029254386353131L ^ cell2.getTime();
         SupportStandUtils.Intent intent = (SupportStandUtils.Intent)map.get(navValue);
         if (intent == null) {
            intent = getReachIntents(overlay, cell, cell2, options);
            map.put(navValue, intent == null ? EMPTY_INTENT : intent);
         }

         if (intent != null && intent != EMPTY_INTENT) {
            return intent;
         } else {
            SupportStandUtils.Intent supportStandUtilsIntent2 = null;
            double positiveInfinity = Double.POSITIVE_INFINITY;

            for (int i = -1; i <= 1; i++) {
               for (int j = -1; j <= 1; j++) {
                  for (int k = -1; k <= 1; k++) {
                     if (i != 0 || j != 0 || k != 0) {
                        SupportStandUtils.Cell dataX = cell.getDataX(i, k, j);
                        if (set.contains(dataX) && overlay.canLavaDirt(dataX)) {
                           double orDefault = nav.getOrDefaultForCell(dataX);
                           if (!(orDefault >= positiveInfinity)) {
                              long botCrystalText = dataX.getTime() * -7046029254386353131L ^ cell2.getTime();
                              SupportStandUtils.Intent supportStandUtilsIntent = (SupportStandUtils.Intent)map.get(botCrystalText);
                              if (supportStandUtilsIntent == null) {
                                 supportStandUtilsIntent = getReachIntents(overlay, dataX, cell2, options);
                                 map.put(botCrystalText, supportStandUtilsIntent == null ? EMPTY_INTENT : supportStandUtilsIntent);
                              }

                              if (supportStandUtilsIntent != null && supportStandUtilsIntent != EMPTY_INTENT) {
                                 supportStandUtilsIntent2 = supportStandUtilsIntent;
                                 positiveInfinity = orDefault;
                              }
                           }
                        }
                     }
                  }
               }
            }

            return supportStandUtilsIntent2;
         }
      }

      static void handleWalk(SupportStandUtils.Overlay overlay, SupportStandUtils.Cell cell, List<SupportStandUtils.Nav.Edge> list) {
         for (int[] walkValues : navInts) {
            SupportStandUtils.Cell dataX = cell.getDataX(walkValues[0], 0, 0);
            SupportStandUtils.Cell dataX3 = cell.getDataX(0, 0, walkValues[1]);
            if (overlay.canFlagForCell(dataX) && overlay.canFlagForCell(dataX.getDataCell()) && overlay.canFlagForCell(dataX3) && overlay.canFlagForCell(dataX3.getDataCell()) && canFlag(overlay, dataX) && canFlag(overlay, dataX3)) {
               SupportStandUtils.Cell dataX4 = cell.getDataX(walkValues[0], 0, walkValues[1]);
               if (overlay.canLavaDirt(dataX4)) {
                  list.add(new SupportStandUtils.Nav.Edge(SupportStandUtils.Op.WALK, cell, dataX4, null, 0, null));
               }
            }
         }

         for (int[] walkIntsItem : walkInts) {
            SupportStandUtils.Cell dataX5 = cell.getDataX(walkIntsItem[0], 0, walkIntsItem[1]);
            if (overlay.canLavaDirt(dataX5)) {
               list.add(new SupportStandUtils.Nav.Edge(SupportStandUtils.Op.WALK, cell, dataX5, null, 0, null));
            } else {
               SupportStandUtils.Cell dataCell = dataX5.getDataCell();
               if (overlay.canFlagForCell(cell.getDataCell()) && overlay.canFlagForCell(cell.getData223(2)) && overlay.canLavaDirt(dataCell)) {
                  list.add(new SupportStandUtils.Nav.Edge(SupportStandUtils.Op.WALK, cell, dataCell, null, 0, null));
               } else if (overlay.canFlagForCell(dataX5) && overlay.canFlagForCell(dataX5.getDataCell())) {
                  for (int i = 1; i <= 2; i++) {
                     SupportStandUtils.Cell dataForInt = dataX5.getDataForInt(i);
                     if (overlay.canLavaDirt(dataForInt)) {
                        list.add(new SupportStandUtils.Nav.Edge(SupportStandUtils.Op.WALK, cell, dataForInt, null, 0, null));
                        break;
                     }

                     if (!overlay.canFlagForCell(dataForInt.getDataCell()) || !overlay.canFlagForCell(dataForInt)) {
                        break;
                     }
                  }
               }
            }
         }
      }

      static SupportStandUtils.Nav getLavaDirt(
         SupportStandUtils.Overlay overlay, SupportStandUtils.Cell cell, SupportStandUtils.Options options, int lavaDirtValue, Set<SupportStandUtils.Cell> set
      ) {
         return getTrailWalk(overlay, cell, options, lavaDirtValue, set, overlay.lavaDirtMap.keySet());
      }

      static void handleDirtFlag(
         SupportStandUtils.Overlay overlay,
         SupportStandUtils.Cell cell,
         SupportStandUtils.Options options,
         int dirtLimit,
         SupportStandUtils.Nav nav,
         Set<SupportStandUtils.Cell> set,
         Map<Long, SupportStandUtils.Intent> map,
         List<SupportStandUtils.Nav.Edge> list
      ) {
         for (int i = 1; i <= dirtLimit && !overlay.isApiCell(cell); i++) {
            boolean dirtFlag = true;

            for (int j = 0; j < i && dirtFlag; j++) {
               if (!overlay.canDirtLava(cell.getData223(j), options)) {
                  dirtFlag = false;
               }
            }

            if (!dirtFlag || !overlay.canFlagForCell(cell.getData223(i)) || !overlay.canFlagForCell(cell.getData223(i + 1))) {
               break;
            }

            list.add(new SupportStandUtils.Nav.Edge(SupportStandUtils.Op.PILLAR, cell, cell.getData223(i), cell, i, null));
         }

         for (int[] dirtValues : walkInts) {
            SupportStandUtils.Cell dataX = cell.getDataX(dirtValues[0], 0, dirtValues[1]);
            if (!overlay.canLavaDirt(dataX)) {
               SupportStandUtils.Cell data222 = dataX.getData222();
               if (overlay.canDirtLava(data222, options) && !overlay.isHazardCell(dataX) && !overlay.isTrapCell(dataX) && overlay.getMax(dataX) <= 0.26 && overlay.canFlagForCell(dataX.getDataCell())) {
                  SupportStandUtils.Intent intent = getMin(overlay, cell, data222, options, nav, set, map);
                  if (intent == null && overlay.cellColumnActive) {
                     intent = getAmountsReach(overlay, cell, data222, options);
                  }

                  if (intent == null && overlay.budgetActive) {
                     intent = getAmountsReach(overlay, cell, data222, options);
                  }

                  SupportStandUtils.Cell cell4 = overlay.cellColumnActive && intent != null && nav.getOrDefault(cell).added().contains(cell.getData222())
                     ? cell.getData222()
                     : null;
                  if (intent != null) {
                     list.add(new SupportStandUtils.Nav.Edge(SupportStandUtils.Op.DIRT, cell, dataX, data222, 1, intent.stand(), intent, cell4));
                  }
               }
            }

            SupportStandUtils.Cell dataCell = dataX.getDataCell();
            if (!overlay.canLavaDirt(dataCell)
               && overlay.canFlagForCell(cell.getDataCell())
               && overlay.canFlagForCell(cell.getData223(2))
               && overlay.canDirtLava(dataX, options)
               && overlay.canFlagForCell(dataCell)
               && overlay.canFlagForCell(dataCell.getDataCell())) {
               SupportStandUtils.Intent min2 = getMin(overlay, cell, dataX, options, nav, set, map);
               if (min2 != null) {
                  list.add(new SupportStandUtils.Nav.Edge(SupportStandUtils.Op.DIRT, cell, dataCell, dataX, 1, min2.stand(), min2));
               }
            }

            if (!overlay.canLavaDirt(dataX) && overlay.canFlagForCell(dataX) && overlay.canFlagForCell(dataCell)) {
               for (int k = 1; k <= 2; k++) {
                  SupportStandUtils.Cell dataForInt = dataX.getDataForInt(k);
                  SupportStandUtils.Cell cell7 = dataForInt.getData222();
                  if (!overlay.canFlagForCell(dataForInt) || !overlay.canFlagForCell(dataForInt.getDataCell()) || overlay.canLavaDirt(dataForInt)) {
                     break;
                  }

                  if (overlay.canDirtLava(cell7, options)) {
                     SupportStandUtils.Intent min3 = getMin(overlay, cell, cell7, options, nav, set, map);
                     SupportStandUtils.Cell cell8 = overlay.cellColumnActive && nav.getOrDefault(cell).added().contains(cell.getData222()) ? cell.getData222() : null;
                     if (min3 != null) {
                        list.add(new SupportStandUtils.Nav.Edge(SupportStandUtils.Op.DIRT, cell, dataForInt, cell7, 1, min3.stand(), min3, cell8));
                     }
                  }

                  if (!overlay.getCellBoxes(cell7).isEmpty()) {
                     break;
                  }
               }
            }
         }
      }

      Set<SupportStandUtils.Cell> getKeySet() {
         return this.trailWalkDoubleMap.keySet();
      }

      static boolean canFlag(SupportStandUtils.Overlay overlay, SupportStandUtils.Cell cell, SupportStandUtils.Cell s) {
         int signum = Integer.signum(s.x() - cell.x());
         int dz = Integer.signum(s.z() - cell.z());
         if (s.y() < cell.y() && (signum != 0 || dz != 0)) {
            SupportStandUtils.Cell dataX = s.getDataX(signum, 0, dz);
            return overlay.canFlagForCell(dataX) && overlay.canFlagForCell(dataX.getDataCell()) && !canFlag(overlay, dataX);
         } else {
            return false;
         }
      }

      static boolean canFlag(SupportStandUtils.Cell cell, Set<SupportStandUtils.Cell> set, int navLimit) {
         if (set != null && !set.isEmpty()) {
            for (SupportStandUtils.Cell o : set) {
               if (cell.getMax(o) <= navLimit) {
                  return true;
               }
            }

            return false;
         } else {
            return true;
         }
      }

      List<SupportStandUtils.Nav.Edge> getWalkTrail(SupportStandUtils.Cell cell) {
         var arrayList = new ArrayList();
         SupportStandUtils.Cell botCrystalText = cell;
         int walkTrailCounter = 0;

         while (!botCrystalText.equals(this.walkTrailCell) && walkTrailCounter++ < 4096) {
            SupportStandUtils.Nav.Edge edge = this.walkTrailEdgeMap.get(botCrystalText);
            if (edge == null) {
               break;
            }

            arrayList.add(edge);
            botCrystalText = edge.from;
         }

         Collections.reverse(arrayList);
         var arrayList2 = new ArrayList();
         SupportStandUtils.Nav.Edge totalPriceData = null;

         for (SupportStandUtils.Nav.Edge edge3 : (Iterable<com.corz.client.utils.SupportStandUtils.Nav.Edge>) (arrayList)) {
            if (edge3.op == SupportStandUtils.Op.WALK && edge3.trail == null) {
               totalPriceData = new SupportStandUtils.Nav.Edge(SupportStandUtils.Op.WALK, totalPriceData == null ? edge3.from : totalPriceData.from, edge3.to, null, 0, null);
            } else {
               if (totalPriceData != null) {
                  arrayList2.add(totalPriceData);
                  totalPriceData = null;
               }

               arrayList2.add(edge3);
            }
         }

         if (totalPriceData != null) {
            arrayList2.add(totalPriceData);
         }

         return arrayList2;
      }

      static boolean canFlagForCellSet(SupportStandUtils.Cell cell, Set<SupportStandUtils.Cell> set, int navLimit) {
         if (set == null) {
            return false;
         } else {
            for (SupportStandUtils.Cell cell2 : set) {
               int deltaY = cell2.y() - cell.y();
               if (deltaY > navLimit && deltaY <= navLimit + 3 && Math.abs(cell2.x() - cell.x()) <= navLimit && Math.abs(cell2.z() - cell.z()) <= navLimit) {
                  return true;
               }
            }

            return false;
         }
      }

      static boolean canFlag(SupportStandUtils.Overlay overlay, SupportStandUtils.Cell cell) {
         for (int i = 0; i <= 2; i++) {
            if (overlay.canLavaDirt(cell.getDataForInt(i))) {
               return true;
            }
         }

         return false;
      }

      static SupportStandUtils.Nav getTrailWalk(
         SupportStandUtils.Overlay overlay,
         SupportStandUtils.Cell cell,
         SupportStandUtils.Options options,
         int trailWalkValue,
         Set<SupportStandUtils.Cell> set,
         Set<SupportStandUtils.Cell> linkedHashSet
      ) { double var40_u = (double) 0; 
         SupportStandUtils.Nav nav = new SupportStandUtils.Nav(cell);
         int[] trailWalkValues = linkedHashSet.isEmpty() ? new int[]{cell.x(), cell.y(), cell.z(), cell.x(), cell.y(), cell.z()} : getValues(linkedHashSet);
         int windowMargin = options.windowMargin();
         int trailWalkValue2 = Math.min(trailWalkValues[0], cell.x()) - windowMargin;
         int trailWalkLimit = Math.max(trailWalkValues[3], cell.x()) + windowMargin;
         int trailWalkValue3 = Math.min(trailWalkValues[2], cell.z()) - windowMargin;
         int trailWalkLimit2 = Math.max(trailWalkValues[5], cell.z()) + windowMargin;
         int trailWalkValue4 = Math.min(trailWalkValues[1], cell.y()) - 4;
         int trailWalkLimit3 = Math.max(trailWalkValues[4], cell.y()) + 4;
         int navLimit = (int)Math.ceil(options.reach()) + 2;
         PriorityQueue priorityQueue = new PriorityQueue<>((obj, obj2) -> Double.compare(((double[]) obj)[0], ((double[]) obj2)[0]));
         nav.trailWalkDoubleMap.put(cell, 0.0);
         nav.trailWalkDiffMap.put(cell, SupportStandUtils.Diff.INSTANCE);
         priorityQueue.add(new double[]{0.0, cell.x(), cell.y(), cell.z()});
         var hashSet = new HashSet();
         var hashMap = new HashMap();
         int trailWalkCounter = 0;

         label305:
         while (!priorityQueue.isEmpty() && trailWalkCounter++ < 20000 && !Thread.currentThread().isInterrupted()) {
            double[] poll2 = (double[])priorityQueue.poll();
            SupportStandUtils.Cell Module = new SupportStandUtils.Cell((int)poll2[1], (int)poll2[2], (int)poll2[3]);
            if (!(poll2[0] > nav.trailWalkDoubleMap.getOrDefault(Module, Double.POSITIVE_INFINITY) + 1.0E-6) && hashSet.add(Module)) {
               double trailWalkValue6 = poll2[0];
               SupportStandUtils.Diff orDefault = nav.getOrDefault(Module);
               overlay.handleInstance(orDefault);

               try {
                  if (overlay.canLavaDirt(Module) || Module.equals(cell)) {
                     var arrayList = new ArrayList();
                     handleWalk(overlay, Module, arrayList);
                     int added = orDefault.getAdded();
                     if (trailWalkValue - added > 0 && canFlag(Module, set, navLimit)) {
                        handleDirtFlag(overlay, Module, options, Math.min(options.maxPillar(), trailWalkValue - added), nav, hashSet, hashMap, arrayList);
                     } else if (trailWalkValue - added > 0 && overlay.costCellActive && canFlagForCellSet(Module, set, navLimit)) {
                        handleDirtFlag(overlay, Module, options, Math.min(3, trailWalkValue - added), nav, hashSet, hashMap, arrayList);
                     }

                     handleDigDown(overlay, Module, arrayList);
                     var iterator = arrayList.iterator();

                     while (true) {
                        SupportStandUtils.Nav.Edge edge;
                        SupportStandUtils.Cell s;
                        double botCrystalValue;
                        SupportStandUtils.Diff diff;
                        label284:
                        while (true) {
                           label262:
                           while (true) {
                              if (!iterator.hasNext()) {
                                 continue label305;
                              }

                              edge = (SupportStandUtils.Nav.Edge)iterator.next();
                              s = edge.to;
                              if ((edge.op != SupportStandUtils.Op.WALK || !options.canStandsUnreachable(s))
                                 && s.x() >= trailWalkValue2
                                 && s.x() <= trailWalkLimit
                                 && s.z() >= trailWalkValue3
                                 && s.z() <= trailWalkLimit2
                                 && s.y() >= trailWalkValue4
                                 && s.y() <= trailWalkLimit3) {
                                 if (overlay.cellColumnActive
                                    && edge.op == SupportStandUtils.Op.WALK
                                    && edge.trail == null
                                    && orDefault.added().contains(Module.getData222())
                                    && !s.getData222().equals(Module.getData222())
                                    && SupportStandUtils.canMinReach(overlay, options, Set.of(), Module.getData222(), s)) {
                                    edge = new SupportStandUtils.Nav.Edge(SupportStandUtils.Op.WALK, Module, s, null, 0, null, null, Module.getData222());
                                 }

                                 switch (edge.op) {
                                    case WALK:
                                       var40_u = 1.0 + 0.4 * Math.abs(s.y() - Module.y());
                                       break label262;
                                    case PILLAR:
                                       var40_u = 10.0 * edge.count + 2.0;
                                       break label262;
                                    case DIRT:
                                       var40_u = 11.4 + (edge.placeFrom != null && !edge.placeFrom.equals(Module) ? 3.0 : 0.0);
                                       break label262;
                                    case DIG_DOWN:
                                       var40_u = 3.0;
                                       break label262;
                                    default:
                                       var40_u = 1.0;
                                       break label262;
                                 }
                              }
                           }

                           double var40_u2 = var40_u;
                           if (overlay.cellColumnActive && (edge.op == SupportStandUtils.Op.WALK || edge.op == SupportStandUtils.Op.DIRT) && canFlag(overlay, Module, s)) {
                              var40_u2 += 15.0;
                           }

                           botCrystalValue = trailWalkValue6 + var40_u2;
                           if (botCrystalValue < nav.trailWalkDoubleMap.getOrDefault(s, Double.POSITIVE_INFINITY) - 1.0E-6) {
                              switch (edge.op) {
                                 case WALK:
                                    diff = edge.trail != null ? orDefault.getAddedRemoved(edge.trail) : orDefault;
                                    break label284;
                                 case PILLAR:
                                    var cells = new ArrayList();

                                    for (int i = 0; i < edge.count; i++) {
                                       cells.add(edge.from.getData223(i));
                                    }

                                    diff = orDefault.getAddedRemoved(cells);
                                    break label284;
                                 case DIRT:
                                    diff = edge.trail != null
                                       ? orDefault.getAddedRemoved(edge.trail).getAddedRemoved(List.of(edge.cell))
                                       : orDefault.getAddedRemoved(List.of(edge.cell));
                                    break label284;
                                 case DIG_DOWN:
                                    diff = orDefault.getAddedRemoved(edge.cell);
                                    break label284;
                                 default:
                                    diff = orDefault;
                                    break label284;
                              }
                           }
                        }

                        SupportStandUtils.Diff diff2 = diff;
                        nav.trailWalkDoubleMap.put(s, botCrystalValue);
                        nav.walkTrailEdgeMap.put(s, edge);
                        nav.trailWalkDiffMap.put(s, diff2);
                        priorityQueue.add(new double[]{botCrystalValue, s.x(), s.y(), s.z()});
                     }
                  }
               } finally {
                  overlay.updateInstance();
               }
            }
         }

         return nav;
      }

      static SupportStandUtils.Intent getReachIntents(
         SupportStandUtils.Overlay overlay, SupportStandUtils.Cell cell, SupportStandUtils.Cell cell2, SupportStandUtils.Options options
      ) {
         if (!cell2.equals(cell) && !cell2.equals(cell.getDataCell())) {
            for (int i = 0; i < 6; i++) {
               SupportStandUtils.Cell data222 = cell2.getData222(i);
               if (!data222.equals(cell) && !data222.equals(cell.getDataCell()) && !overlay.getCellBoxes(data222).isEmpty()) {
                  int reachIntentsValue = PistonStickyUtils.getValue(i);
                  boolean lavaApi = overlay.isInteractiveCell(data222);

                  for (SupportStandUtils.Box box : overlay.getTailOverrideBoxes(data222)) {
                     if (SupportStandUtils.canFlag(box, reachIntentsValue)) {
                        for (int j = lavaApi ? 1 : 0; j < 2; j++) {
                           double[] reachIntentsValues = overlay.getAmounts(cell, j == 1);

                           for (double[] getUnmodifiableItem : SupportStandUtils.getUnmodifiable(options.allowEdgeAim())) {
                              double[] doubles = SupportStandUtils.getAmounts(data222, box, reachIntentsValue, getUnmodifiableItem[0], getUnmodifiableItem[1]);
                              if (!(SupportStandUtils.getAmount(reachIntentsValues, doubles[0], doubles[1], doubles[2]) > options.reach() * options.reach())) {
                                 int[] values4 = PistonStickyUtils.getValues(reachIntentsValue);
                                 double reachIntentsValue2 = (reachIntentsValues[0] - doubles[0]) * values4[0] + (reachIntentsValues[1] - doubles[1]) * values4[1] + (reachIntentsValues[2] - doubles[2]) * values4[2];
                                 if (!(reachIntentsValue2 <= 1.0E-4) && overlay.canFlag(reachIntentsValues, doubles, data222, false)) {
                                    double pistonStickyLimit = doubles[0] - reachIntentsValues[0];
                                    double spotValue = doubles[1] - reachIntentsValues[1];
                                    double reachIntentsValue5 = doubles[2] - reachIntentsValues[2];
                                    double min6 = Math.min(getUnmodifiableItem[2], SupportStandUtils.getAmount(box, reachIntentsValue) * getUnmodifiableItem[2]);
                                    SupportStandUtils.Intent intent = new SupportStandUtils.Intent(
                                       cell2,
                                       cell,
                                       j == 1,
                                       data222,
                                       reachIntentsValue,
                                       doubles[0],
                                       doubles[1],
                                       doubles[2],
                                       PistonStickyUtils.getValue(pistonStickyLimit, spotValue, reachIntentsValue5),
                                       min6,
                                       overlay.canApiAdded(data222)
                                    );
                                    if (!options.bannedIntents().contains(intent.getSneakFace())) {
                                       return intent;
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }

            return null;
         } else {
            return null;
         }
      }

      static SupportStandUtils.Intent getAmountsReach(
         SupportStandUtils.Overlay overlay, SupportStandUtils.Cell cell, SupportStandUtils.Cell cell2, SupportStandUtils.Options options
      ) {
         SupportStandUtils.Cell data222 = cell.getData222();
         int deltaX = cell2.x() - data222.x();
         int deltaZ = cell2.z() - data222.z();
         if (cell2.y() == data222.y()
            && Math.abs(deltaX) + Math.abs(deltaZ) == 1
            && SupportStandUtils.FULL_BOXES.equals(overlay.getCellBoxes(data222))
            && !overlay.isTrapCell(data222)
            && !overlay.isHazardCell(data222)) {
            SupportStandUtils.Cell dataX = cell.getDataX(deltaX, 0, deltaZ);
            if (overlay.canFlagForCell(dataX) && overlay.canFlagForCell(dataX.getDataCell())) {
               int reachValue = deltaX > 0 ? 5 : (deltaX < 0 ? 4 : (deltaZ > 0 ? 3 : 2));
               double[] reachValues = overlay.getAmounts(cell, true);
               double reachLimit = options.reach() * options.reach();

               for (double[] getUnmodifiableItem : SupportStandUtils.getUnmodifiable(false)) {
                  double[] amounts3 = SupportStandUtils.getAmounts(data222, SupportStandUtils.MIN_MAX_DATA, reachValue, getUnmodifiableItem[0], getUnmodifiableItem[1]);
                  boolean reachFlag = true;

                  for (int i = 0; i < CrouchedBodyUtils.FACE_REACH_OFFSETS.length && reachFlag; i++) {
                     for (int j = 0; j < reachTable.length && reachFlag; j++) {
                        double reachFactor = CrouchedBodyUtils.FACE_REACH_OFFSETS[i];
                        double reachFactor2 = reachTable[j];
                        double[] doubles = new double[]{reachValues[0] + deltaX * reachFactor + deltaZ * reachFactor2, reachValues[1], reachValues[2] + deltaZ * reachFactor + deltaX * reachFactor2};
                        reachFlag = (doubles[0] - amounts3[0]) * deltaX + (doubles[2] - amounts3[2]) * deltaZ > 1.0E-4
                           && SupportStandUtils.getAmount(doubles, amounts3[0], amounts3[1], amounts3[2]) <= reachLimit
                           && overlay.canFlag(doubles, amounts3, data222, false);
                     }
                  }

                  if (reachFlag) {
                     double reachValue2 = reachValues[0] + deltaX * 0.79;
                     double reachValue3 = reachValues[2] + deltaZ * 0.79;
                     SupportStandUtils.Intent intent = new SupportStandUtils.Intent(
                        cell2,
                        cell,
                        true,
                        data222,
                        reachValue,
                        amounts3[0],
                        amounts3[1],
                        amounts3[2],
                        PistonStickyUtils.getValue(amounts3[0] - reachValue2, amounts3[1] - reachValues[1], amounts3[2] - reachValue3),
                        getUnmodifiableItem[2],
                        overlay.canApiAdded(data222)
                     );
                     if (!options.bannedIntents().contains(intent.getSneakFace())) {
                        return intent;
                     }
                  }
               }

               return null;
            } else {
               return null;
            }
         } else {
            return null;
         }
      }

      double getOrDefaultForCell(SupportStandUtils.Cell cell) {
         return this.trailWalkDoubleMap.getOrDefault(cell, Double.POSITIVE_INFINITY);
      }

      private Nav(SupportStandUtils.Cell data) {
         this.walkTrailCell = data;
      }

      record Edge(
         SupportStandUtils.Op op,
         SupportStandUtils.Cell from,
         SupportStandUtils.Cell to,
         SupportStandUtils.Cell cell,
         int count,
         SupportStandUtils.Cell placeFrom,
         SupportStandUtils.Intent intent,
         SupportStandUtils.Cell trail
      ) {
         Edge(
            SupportStandUtils.Op op,
            SupportStandUtils.Cell placeFrom,
            SupportStandUtils.Cell count,
            SupportStandUtils.Cell from,
            int to,
            SupportStandUtils.Cell cell
         ) {
            this(op, placeFrom, count, from, to, cell, null, null);
         }

         Edge(
            SupportStandUtils.Op placeFrom,
            SupportStandUtils.Cell cell,
            SupportStandUtils.Cell op,
            SupportStandUtils.Cell from,
            int to,
            SupportStandUtils.Cell intent,
            SupportStandUtils.Intent count
         ) {
            this(placeFrom, cell, op, from, to, intent, count, null);
         }
      }
   }

   public static enum Op {
      WALK,
      PILLAR,
      DIRT,
      DIG_DOWN,
      REMOVE_DIRT,
      BREAK,
      PLACE;
   }

   public record Options(
      double reach,
      int dirtBudget,
      int maxPillar,
      boolean allowEdgeAim,
      boolean allowFluidCellDirt,
      boolean allowFutureStands,
      int windowMargin,
      Set<Long> bannedIntents,
      long budgetNanos,
      boolean edgeSneak,
      boolean egressGuard,
      Set<Long> bannedPairs,
      Set<Long> unreachableStands
   ) {
      public Options(double allowFluidCellDirt, int windowMargin, int reach, boolean dirtBudget, boolean allowFutureStands, boolean allowEdgeAim, int maxPillar) {
         this(allowFluidCellDirt, windowMargin, reach, dirtBudget, allowFutureStands, allowEdgeAim, maxPillar, Set.of(), 0L, true, true);
      }

      public SupportStandUtils.Options getAllowStands(boolean on) {
         return new SupportStandUtils.Options(
            this.reach,
            this.dirtBudget,
            this.maxPillar,
            this.allowEdgeAim,
            this.allowFluidCellDirt,
            this.allowFutureStands,
            this.windowMargin,
            this.bannedIntents,
            this.budgetNanos,
            this.edgeSneak,
            on,
            this.bannedPairs,
            this.unreachableStands
         );
      }

      public SupportStandUtils.Options getAllowStands(long millis) {
         return new SupportStandUtils.Options(
            this.reach,
            this.dirtBudget,
            this.maxPillar,
            this.allowEdgeAim,
            this.allowFluidCellDirt,
            this.allowFutureStands,
            this.windowMargin,
            this.bannedIntents,
            millis <= 0L ? 0L : millis * 1000000L,
            this.edgeSneak,
            this.egressGuard,
            this.bannedPairs,
            this.unreachableStands
         );
      }

      public static SupportStandUtils.Options getMaxDataForDouble(double reach) {
         return new SupportStandUtils.Options(reach, 8, 3, false, false, true, 4);
      }

      public SupportStandUtils.Options getAllowStands(Set<Long> keys) {
         return new SupportStandUtils.Options(
            this.reach,
            this.dirtBudget,
            this.maxPillar,
            this.allowEdgeAim,
            this.allowFluidCellDirt,
            this.allowFutureStands,
            this.windowMargin,
            keys == null ? Set.of() : keys,
            this.budgetNanos,
            this.edgeSneak,
            this.egressGuard,
            this.bannedPairs,
            this.unreachableStands
         );
      }

      boolean canStandsUnreachable(SupportStandUtils.Cell s) {
         return !this.unreachableStands.isEmpty() && this.unreachableStands.contains(s.getTime());
      }

      public Options(
         double maxPillar,
         int bannedIntents,
         int edgeSneak,
         boolean reach,
         boolean egressGuard,
         boolean dirtBudget,
         int allowEdgeAim,
         Set<Long> budgetNanos,
         long windowMargin,
         boolean allowFluidCellDirt,
         boolean allowFutureStands
      ) {
         this(
            maxPillar,
            bannedIntents,
            edgeSneak,
            reach,
            egressGuard,
            dirtBudget,
            allowEdgeAim,
            budgetNanos,
            windowMargin,
            allowFluidCellDirt,
            allowFutureStands,
            Set.of(),
            Set.of()
         );
      }

      SupportStandUtils.Options getStandsAllow() {
         return new SupportStandUtils.Options(
            this.reach,
            this.dirtBudget,
            this.maxPillar,
            true,
            this.allowFluidCellDirt,
            this.allowFutureStands,
            this.windowMargin,
            this.bannedIntents,
            this.budgetNanos,
            this.edgeSneak,
            this.egressGuard,
            this.bannedPairs,
            this.unreachableStands
         );
      }

      public SupportStandUtils.Options getAllowStandsForFlag(boolean on) {
         return new SupportStandUtils.Options(
            this.reach,
            this.dirtBudget,
            this.maxPillar,
            this.allowEdgeAim,
            this.allowFluidCellDirt,
            this.allowFutureStands,
            this.windowMargin,
            this.bannedIntents,
            this.budgetNanos,
            on,
            this.egressGuard,
            this.bannedPairs,
            this.unreachableStands
         );
      }

      public SupportStandUtils.Options getAllowStands(int margin) {
         return new SupportStandUtils.Options(
            this.reach,
            this.dirtBudget,
            this.maxPillar,
            this.allowEdgeAim,
            this.allowFluidCellDirt,
            this.allowFutureStands,
            margin,
            this.bannedIntents,
            this.budgetNanos,
            this.edgeSneak,
            this.egressGuard,
            this.bannedPairs,
            this.unreachableStands
         );
      }

      public SupportStandUtils.Options getAllowStandsForSet(Set<Long> pairs) {
         return new SupportStandUtils.Options(
            this.reach,
            this.dirtBudget,
            this.maxPillar,
            this.allowEdgeAim,
            this.allowFluidCellDirt,
            this.allowFutureStands,
            this.windowMargin,
            this.bannedIntents,
            this.budgetNanos,
            this.edgeSneak,
            this.egressGuard,
            pairs == null ? Set.of() : pairs,
            this.unreachableStands
         );
      }

      public SupportStandUtils.Options getAllowBanned(Set<Long> stands) {
         return new SupportStandUtils.Options(
            this.reach,
            this.dirtBudget,
            this.maxPillar,
            this.allowEdgeAim,
            this.allowFluidCellDirt,
            this.allowFutureStands,
            this.windowMargin,
            this.bannedIntents,
            this.budgetNanos,
            this.edgeSneak,
            this.egressGuard,
            this.bannedPairs,
            stands == null ? Set.of() : stands
         );
      }

      boolean canPairsBanned(SupportStandUtils.Cell stand, SupportStandUtils.Cell target) {
         return !this.bannedPairs.isEmpty() && this.bannedPairs.contains(getTime(stand, target));
      }

      SupportStandUtils.Options getStandsAllowReach() {
         return new SupportStandUtils.Options(
            this.reach,
            this.dirtBudget,
            this.maxPillar,
            this.allowEdgeAim,
            true,
            this.allowFutureStands,
            this.windowMargin,
            this.bannedIntents,
            this.budgetNanos,
            this.edgeSneak,
            this.egressGuard,
            this.bannedPairs,
            this.unreachableStands
         );
      }

      public static long getTime(SupportStandUtils.Cell stand, SupportStandUtils.Cell target) {
         return DepsReasonData.packTargetStandKey(stand.getTime(), target.getTime());
      }

      SupportStandUtils.Options getAllowBanned() {
         return new SupportStandUtils.Options(
            this.reach,
            this.dirtBudget,
            this.maxPillar,
            this.allowEdgeAim,
            this.allowFluidCellDirt,
            true,
            this.windowMargin,
            this.bannedIntents,
            this.budgetNanos,
            this.edgeSneak,
            this.egressGuard,
            this.bannedPairs,
            this.unreachableStands
         );
      }
   }

   static final class Overlay implements SupportStandUtils.World {
      final SupportStandUtils.World apiLavaWorld;
      private SupportStandUtils.Diff apiLavaDiff;
      final Map<SupportStandUtils.Cell, SupportStandUtils.Cell> dirtMap;
      final Set<SupportStandUtils.Cell> cellColumnCellSet;
      final Map<SupportStandUtils.Cell, SupportStandUtils.Target> lavaDirtMap = new LinkedHashMap<>();
      final Set<SupportStandUtils.Cell> apiLavaCellSet = new HashSet<>();
      Set<SupportStandUtils.Cell> lavaDirtCellSet;
      boolean budgetActive;
      private int lavaValue;
      final Set<SupportStandUtils.Cell> lavaApiCellSet = new HashSet<>();
      final Set<SupportStandUtils.Cell> cellStandCellSet = new HashSet<>();
      int overlayFloorY;
      private int lavaDirtCount;
      boolean cellColumnActive;
      boolean costCellActive;
      Set<SupportStandUtils.Cell> costCellCellSet;

      Overlay(SupportStandUtils.World api, List<SupportStandUtils.Target> list) {
         this.dirtMap = new HashMap<>();
         this.overlayFloorY = Integer.MIN_VALUE;
         this.lavaDirtCellSet = Set.of();
         this.cellColumnCellSet = new HashSet<>();
         this.costCellCellSet = Set.of();
         this.apiLavaDiff = SupportStandUtils.Diff.INSTANCE;
         this.lavaValue = -1;
         this.apiLavaWorld = api;

         for (SupportStandUtils.Target target : list) {
            this.lavaDirtMap.put(target.cell(), target);
         }
      }

      boolean canLavaDirt(SupportStandUtils.Cell cell) {
         SupportStandUtils.Cell goodsSlot = cell.getData222();
         if (this.apiLavaCellSet.contains(goodsSlot) && canFenceGate(this.lavaDirtMap.get(goodsSlot))) {
            return false;
         } else if (!this.isHazardCell(goodsSlot) && !this.isTrapCell(goodsSlot) && !this.isTailCell(goodsSlot)) {
            if (this.getMax(goodsSlot) < 0.8) {
               return false;
            } else if (!this.isHazardCell(cell) && !this.isTrapCell(cell) && !this.canLavaApiForCell(cell)) {
               if (this.getMax(cell) > 0.26) {
                  return false;
               } else {
                  SupportStandUtils.Cell dataCell = cell.getDataCell();
                  return !this.isHazardCell(dataCell) && !this.canLavaApiForCell(dataCell) && this.getCellBoxes(dataCell).isEmpty();
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }

      SupportStandUtils.Target getLavaDirt(SupportStandUtils.Cell cell) {
         return this.lavaDirtMap.get(cell);
      }

      static double getAmount(double[] overlayValues, double overlayValue, double overlayValue2, double overlayValue3, SupportStandUtils.Cell cell, SupportStandUtils.Box box) {
         double overlayBound = 0.0;
         double hypot = 1.000001;
         double[] doubles = new double[]{cell.x() + box.x0(), cell.y() + box.y0(), cell.z() + box.z0()};
         double[] doubles2 = new double[]{cell.x() + box.x1(), cell.y() + box.y1(), cell.z() + box.z1()};
         double[] doubles3 = new double[]{overlayValue, overlayValue2, overlayValue3};

         for (int i = 0; i < 3; i++) {
            if (Math.abs(doubles3[i]) < 1.0E-12) {
               if (overlayValues[i] < doubles[i] - 1.0E-9 || overlayValues[i] > doubles2[i] + 1.0E-9) {
                  return -1.0;
               }
            } else {
               double overlayLimit = (doubles[i] - overlayValues[i]) / doubles3[i];
               double goalBound = (doubles2[i] - overlayValues[i]) / doubles3[i];
               if (overlayLimit > goalBound) {
                  double overlayLimit4 = overlayLimit;
                  overlayLimit = goalBound;
                  goalBound = overlayLimit4;
               }

               overlayBound = Math.max(overlayBound, overlayLimit);
               hypot = Math.min(hypot, goalBound);
               if (overlayBound > hypot) {
                  return -1.0;
               }
            }
         }

         return overlayBound;
      }

      boolean canDirtLava(SupportStandUtils.Cell cell, SupportStandUtils.Options options) {
         if (this.getCellBoxes(cell).isEmpty() && !this.isHazardCell(cell) && !this.isReservedCell(cell) && this.getExtraBoxes(cell).isEmpty()) {
            SupportStandUtils.Target lavadirtmap = this.lavaDirtMap.get(cell);
            return lavadirtmap != null && !this.apiLavaCellSet.contains(cell) ? options.allowFluidCellDirt() && lavadirtmap.isKindFluid() : !this.canApiLavaForCell(cell);
         } else {
            return false;
         }
      }

      void handleDirtPlain(SupportStandUtils.Cell cell, SupportStandUtils.Cell target) {
         this.dirtMap.put(cell, target);
         this.lavaDirtMap
            .put(cell, new SupportStandUtils.Target(cell, "dirt", SupportStandUtils.Kind.PLAIN, -1, SupportStandUtils.Shape.FULL, false, null));
      }

      static boolean canFenceGate(SupportStandUtils.Target target) {
         if (target != null && target.shape() == SupportStandUtils.Shape.FULL) {
            String name = target.name();
            return name.endsWith("_fence") || name.endsWith("_fence_gate") || name.endsWith("_wall");
         } else {
            return false;
         }
      }

      @Override
      public boolean isLavaOverrideCell(SupportStandUtils.Cell cell) {
         return !this.apiLavaWorld.isLavaOverrideCell(cell)
            ? false
            : !this.apiLavaDiff.removed().contains(cell)
               && !this.apiLavaDiff.added().contains(cell)
               && !this.cellStandCellSet.contains(cell)
               && !this.lavaApiCellSet.contains(cell)
               && !this.lavaDirtMap.containsKey(cell);
      }

      @Override
      public List<SupportStandUtils.Box> getCellBoxes(SupportStandUtils.Cell cell) {
         if (this.apiLavaDiff.removed().contains(cell)) {
            return SupportStandUtils.NO_BOXES;
         } else if (this.apiLavaDiff.added().contains(cell)) {
            return SupportStandUtils.FULL_BOXES;
         } else if (this.cellStandCellSet.contains(cell)) {
            return SupportStandUtils.NO_BOXES;
         } else if (this.lavaApiCellSet.contains(cell)) {
            return SupportStandUtils.FULL_BOXES;
         } else if (this.apiLavaCellSet.contains(cell)) {
            return SupportStandUtils.getItems(this.lavaDirtMap.get(cell).shape());
         } else {
            return this.lavaDirtMap.containsKey(cell) ? SupportStandUtils.NO_BOXES : this.apiLavaWorld.getCellBoxes(cell);
         }
      }

      @Override
      public List<SupportStandUtils.Box> getLavaOverrideBoxes(SupportStandUtils.Cell cell) {
         return !this.apiLavaDiff.removed().contains(cell)
               && !this.apiLavaDiff.added().contains(cell)
               && !this.cellStandCellSet.contains(cell)
               && !this.lavaApiCellSet.contains(cell)
               && !this.apiLavaCellSet.contains(cell)
               && !this.lavaDirtMap.containsKey(cell)
            ? this.apiLavaWorld.getLavaOverrideBoxes(cell)
            : this.getCellBoxes(cell);
      }

      @Override
      public boolean canOwnDirt(SupportStandUtils.Cell cell) {
         return this.canApiAdded(cell);
      }

      boolean canFlag(SupportStandUtils.Cell cell, SupportStandUtils.Cell cell2) {
         for (int i = 0; i < 6; i++) {
            SupportStandUtils.Cell data222 = cell.getData222(i);
            if (!data222.equals(cell2) && !this.getCellBoxes(data222).isEmpty()) {
               return true;
            }
         }

         return false;
      }

      boolean canApiAdded(SupportStandUtils.Cell cell) {
         if (this.apiLavaDiff.removed().contains(cell)) {
            return false;
         } else if (this.apiLavaDiff.added().contains(cell)) {
            return true;
         } else {
            return this.cellStandCellSet.contains(cell) ? false : this.lavaApiCellSet.contains(cell) || this.apiLavaWorld.canOwnDirt(cell);
         }
      }

      private boolean canFlag(double[] overlayValues, double[] doubles, SupportStandUtils.Cell cell, boolean overlayFlag, boolean overlayFlag2) {
         double overlayFactor = doubles[0] - overlayValues[0];
         double overlayFactor2 = doubles[1] - overlayValues[1];
         double overlayFactor3 = doubles[2] - overlayValues[2];
         double distance = Math.sqrt(overlayFactor * overlayFactor + overlayFactor2 * overlayFactor2 + overlayFactor3 * overlayFactor3);
         if (distance < 1.0E-9) {
            return false;
         } else {
            int overlayTotal = (int)Math.floor(overlayValues[0]);
            int floor2 = (int)Math.floor(overlayValues[1]);
            int floor3 = (int)Math.floor(overlayValues[2]);
            int overlayValue = (int)Math.floor(doubles[0] - overlayFactor * 1.0E-7);
            int overlayValue2 = (int)Math.floor(doubles[1] - overlayFactor2 * 1.0E-7);
            int overlayValue3 = (int)Math.floor(doubles[2] - overlayFactor3 * 1.0E-7);
            int overlayLimit = overlayFactor > 0.0 ? 1 : (overlayFactor < 0.0 ? -1 : 0);
            int overlayLimit2 = overlayFactor2 > 0.0 ? 1 : (overlayFactor2 < 0.0 ? -1 : 0);
            int overlayLimit3 = overlayFactor3 > 0.0 ? 1 : (overlayFactor3 < 0.0 ? -1 : 0);
            double overlayTotal4 = overlayLimit == 0 ? Double.POSITIVE_INFINITY : ((overlayLimit > 0 ? overlayTotal + 1 : overlayTotal) - overlayValues[0]) / overlayFactor;
            double overlayLimit4 = overlayLimit2 == 0 ? Double.POSITIVE_INFINITY : ((overlayLimit2 > 0 ? floor2 + 1 : floor2) - overlayValues[1]) / overlayFactor2;
            double overlayLimit5 = overlayLimit3 == 0 ? Double.POSITIVE_INFINITY : ((overlayLimit3 > 0 ? floor3 + 1 : floor3) - overlayValues[2]) / overlayFactor3;
            double overlayValue4 = overlayLimit == 0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / overlayFactor);
            double overlayValue5 = overlayLimit2 == 0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / overlayFactor2);
            double overlayValue6 = overlayLimit3 == 0 ? Double.POSITIVE_INFINITY : Math.abs(1.0 / overlayFactor3);
            int overlayCounter = 0;

            while (overlayCounter++ < 64) {
               SupportStandUtils.Cell cell2 = new SupportStandUtils.Cell(overlayTotal, floor2, floor3);
               List list = cell2.equals(cell) && overlayFlag ? SupportStandUtils.FULL_BOXES : this.getTailOverrideBoxes(cell2);
               if (overlayFlag2) {
                  list = cell2.equals(cell) ? SupportStandUtils.NO_BOXES : this.getLavaOverrideBoxes(cell2);
               }

               for (SupportStandUtils.Box box : (Iterable<com.corz.client.utils.SupportStandUtils.Box>) (list)) {
                  double overlayValue7 = getAmount(overlayValues, overlayFactor, overlayFactor2, overlayFactor3, cell2, box);
                  if (!(overlayValue7 < 0.0)) {
                     if (cell2.equals(cell)) {
                        if (overlayValue7 < 0.9999) {
                           return false;
                        }
                     } else if (overlayValue7 < 0.999999) {
                        return false;
                     }
                  }
               }

               if (overlayTotal == overlayValue && floor2 == overlayValue2 && floor3 == overlayValue3) {
                  break;
               }

               if (overlayTotal4 < overlayLimit4 && overlayTotal4 < overlayLimit5) {
                  overlayTotal += overlayLimit;
                  overlayTotal4 += overlayValue4;
               } else if (overlayLimit4 < overlayLimit5) {
                  floor2 += overlayLimit2;
                  overlayLimit4 += overlayValue5;
               } else {
                  floor3 += overlayLimit3;
                  overlayLimit5 += overlayValue6;
               }

               if (overlayTotal4 > 1.000001 && overlayLimit4 > 1.000001 && overlayLimit5 > 1.000001 && (overlayTotal != overlayValue || floor2 != overlayValue2 || floor3 != overlayValue3)) {
                  SupportStandUtils.Cell cell3 = new SupportStandUtils.Cell(overlayValue, overlayValue2, overlayValue3);
                  List list2 = cell3.equals(cell) && overlayFlag ? SupportStandUtils.FULL_BOXES : this.getTailOverrideBoxes(cell3);
                  if (overlayFlag2) {
                     list2 = cell3.equals(cell) ? SupportStandUtils.NO_BOXES : this.getLavaOverrideBoxes(cell3);
                  }

                  for (SupportStandUtils.Box box2 : (Iterable<com.corz.client.utils.SupportStandUtils.Box>) (list2)) {
                     double overlayLimit6 = getAmount(overlayValues, overlayFactor, overlayFactor2, overlayFactor3, cell3, box2);
                     if (overlayLimit6 >= 0.0 && !cell3.equals(cell) && overlayLimit6 < 0.999999) {
                        return false;
                     }
                  }
                  break;
               }
            }

            return true;
         }
      }

      SupportStandUtils.Cell getDirt(SupportStandUtils.Cell cell) {
         return this.dirtMap.get(cell);
      }

      boolean canLavaDirt(SupportStandUtils.Cell cell, SupportStandUtils.Cell botCrystalText) {
         SupportStandUtils.Target lavadirtmap = this.lavaDirtMap.get(botCrystalText);
         if (lavadirtmap != null && lavadirtmap.isKindFluid() && "lava".equals(lavadirtmap.name())) {
            if (this.lavaValue < 0) {
               int lavaValue = 0;

               for (SupportStandUtils.Target target2 : this.lavaDirtMap.values()) {
                  if (target2.isKindFluid() && "lava".equals(target2.name())) {
                     lavaValue++;
                  }
               }

               this.lavaValue = lavaValue;
            }

            if (this.lavaValue <= 8 && ++this.lavaDirtCount <= 1024) {
               final var hashMap = new HashMap<SupportStandUtils.Cell, Integer>();
               var arrayDeque = new ArrayDeque();
               hashMap.put(botCrystalText, 3);
               arrayDeque.add(botCrystalText);

               for (int i = 0; !arrayDeque.isEmpty() && i < 400 && hashMap.size() < 256; i++) {
                  SupportStandUtils.Cell supportStandUtilsCell = (SupportStandUtils.Cell)arrayDeque.poll();
                  SupportStandUtils.Cell data222 = supportStandUtilsCell.getData222();
                  if (this.getCellBoxes(data222).isEmpty()) {
                     if (hashMap.getOrDefault(data222, -1) < 3) {
                        hashMap.put(data222, 3);
                        arrayDeque.add(data222);
                     }
                  } else {
                     int lavaDirtLimit = (Integer)hashMap.get(supportStandUtilsCell);
                     if (lavaDirtLimit > 0) {
                        for (int[] lavaDirtValues : SupportStandUtils.Nav.walkInts) {
                           SupportStandUtils.Cell dataX = supportStandUtilsCell.getDataX(lavaDirtValues[0], 0, lavaDirtValues[1]);
                           if (this.getCellBoxes(dataX).isEmpty() && hashMap.getOrDefault(dataX, -1) < lavaDirtLimit - 1) {
                              hashMap.put(dataX, lavaDirtLimit - 1);
                              arrayDeque.add(dataX);
                           }
                        }
                     }
                  }
               }

               if (!hashMap.containsKey(cell) && !hashMap.containsKey(cell.getDataCell())) {
                  final Terrain handler = this.getHandlerTerrain();
                  Terrain terrain = new Terrain() {
                     @Override
                     public boolean canStandOn(int mutableValue, int y, int mutableValue3) {
                        return !hashMap.containsKey(new SupportStandUtils.Cell(mutableValue, y, mutableValue3)) && handler.canStandOn(mutableValue, y, mutableValue3);
                     }

                     @Override
                     public boolean solid(int solidValue, int candidateY, int candidateZ) {
                        return hashMap.containsKey(new SupportStandUtils.Cell(solidValue, candidateY, candidateZ)) || handler.solid(solidValue, candidateY, candidateZ);
                     }
                  };
                  return WalkableRegionAnalyzer.countReachableCells(terrain, cell.x(), cell.y(), cell.z(), 40) <= 12;
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

      void handleCell(SupportStandUtils.Target target) {
         this.apiLavaCellSet.add(target.cell());
      }

      boolean canFlag(SupportStandUtils.Cell cell) {
         for (int[] overlayValues : SupportStandUtils.Nav.walkInts) {
            SupportStandUtils.Cell dataX = cell.getDataX(overlayValues[0], 0, overlayValues[1]);
            if (this.isTrapCell(dataX) || this.isTrapCell(dataX.getData222())) {
               return true;
            }

            if (this.getCellBoxes(dataX).isEmpty() && this.getCellBoxes(dataX.getData222()).isEmpty()) {
               return true;
            }
         }

         return false;
      }

      void handle(SupportStandUtils.Cell cell) {
         this.lavaApiCellSet.add(cell);
         this.cellStandCellSet.remove(cell);
      }

      boolean canLavaDirtForCell(SupportStandUtils.Cell cell) {
         return this.lavaDirtMap.containsKey(cell) && !this.apiLavaCellSet.contains(cell);
      }

      void handleInstance(SupportStandUtils.Diff diff) {
         this.apiLavaDiff = diff == null ? SupportStandUtils.Diff.INSTANCE : diff;
      }

      boolean canFlag(double[] overlayValues, double[] doubles, SupportStandUtils.Cell cell, int overlayValue) {
         double overlayFactor = doubles[0] - overlayValues[0];
         double overlayFactor2 = doubles[1] - overlayValues[1];
         double overlayFactor3 = doubles[2] - overlayValues[2];
         double positiveInfinity = Double.POSITIVE_INFINITY;
         SupportStandUtils.Box box = null;

         for (SupportStandUtils.Box getLavaOverrideBoxesItem : this.getLavaOverrideBoxes(cell)) {
            double overlayLimit = getAmount(overlayValues, overlayFactor, overlayFactor2, overlayFactor3, cell, getLavaOverrideBoxesItem);
            if (overlayLimit > 1.0E-9 && overlayLimit < positiveInfinity) {
               positiveInfinity = overlayLimit;
               box = getLavaOverrideBoxesItem;
            }
         }

         if (box == null) {
            return false;
         } else {
            int[] values3 = PistonStickyUtils.getValues(overlayValue);
            double overlayValue2 = values3[0] > 0
               ? cell.x() + box.x1()
               : (
                  values3[0] < 0
                     ? cell.x() + box.x0()
                     : (
                        values3[1] > 0
                           ? cell.y() + box.y1()
                           : (values3[1] < 0 ? cell.y() + box.y0() : (values3[2] > 0 ? cell.z() + box.z1() : cell.z() + box.z0()))
                     )
               );
            double overlayValue3 = values3[0] != 0 ? overlayValues[0] + positiveInfinity * overlayFactor : (values3[1] != 0 ? overlayValues[1] + positiveInfinity * overlayFactor2 : overlayValues[2] + positiveInfinity * overlayFactor3);
            return Math.abs(overlayValue3 - overlayValue2) < 1.0E-6 && this.canFlag(overlayValues, doubles, cell, false, true);
         }
      }

      @Override
      public List<SupportStandUtils.Box> getTailOverrideBoxes(SupportStandUtils.Cell cell) {
         return !this.apiLavaDiff.removed().contains(cell)
               && !this.apiLavaDiff.added().contains(cell)
               && !this.cellStandCellSet.contains(cell)
               && !this.lavaApiCellSet.contains(cell)
               && !this.apiLavaCellSet.contains(cell)
               && !this.lavaDirtMap.containsKey(cell)
            ? this.apiLavaWorld.getTailOverrideBoxes(cell)
            : this.getCellBoxes(cell);
      }

      @Override
      public boolean isInteractiveCell(SupportStandUtils.Cell cell) {
         if (this.apiLavaDiff.added().contains(cell) || this.lavaApiCellSet.contains(cell)) {
            return false;
         } else if (this.apiLavaCellSet.contains(cell)) {
            return this.lavaDirtMap.get(cell).interactive();
         } else {
            return this.lavaDirtMap.containsKey(cell) ? false : this.apiLavaWorld.isInteractiveCell(cell);
         }
      }

      double getMax(SupportStandUtils.Cell cell) {
         double overlayBound = 0.0;

         for (SupportStandUtils.Box box : this.getCellBoxes(cell)) {
            overlayBound = Math.max(overlayBound, box.y1());
         }

         return overlayBound;
      }

      boolean canLavaDirtApiLavaCellSet(SupportStandUtils.Cell cell) {
         return this.apiLavaCellSet.contains(cell) || !this.lavaDirtMap.containsKey(cell) && !this.getCellBoxes(cell).isEmpty();
      }

      double[] getAmounts(SupportStandUtils.Cell cell, boolean overlayFlag) {
         double overlayBound = cell.y() - 1 + this.getMax(cell.getData222());
         double overlayLimit = this.getMax(cell);
         if (overlayLimit > 0.0 && overlayLimit <= 0.26) {
            overlayBound = Math.max(overlayBound, cell.y() + overlayLimit);
         }

         return new double[]{cell.x() + 0.5, overlayBound + (overlayFlag ? 1.27 : 1.62), cell.z() + 0.5};
      }

      boolean canComponentLava(SupportStandUtils.Cell cell, SupportStandUtils.Cell obsidianIdleValue) {
         Result sealsPocket = WalkableRegionAnalyzer.evaluatePocketSealing(
            this.getHandlerTerrain(),
            cell.x(),
            cell.y(),
            cell.z(),
            obsidianIdleValue.x(),
            obsidianIdleValue.y(),
            obsidianIdleValue.z(),
            !this.lavaDirtMap.containsKey(obsidianIdleValue) || !this.apiLavaWorld.isTailCell(obsidianIdleValue)
         );
         return sealsPocket.isSealsVerdict() && sealsPocket.componentBefore() == sealsPocket.componentAfter() && obsidianIdleValue.y() > cell.y() && this.canApiAdded(cell.getData222())
            ? false
            : sealsPocket.isSealsVerdict() || sealsPocket.componentBefore() >= 40 && this.canLavaDirt(cell, obsidianIdleValue);
      }

      void handleForCell(SupportStandUtils.Cell cell) {
         this.lavaApiCellSet.remove(cell);
         this.cellStandCellSet.add(cell);
      }

      boolean canApiLavaForCell(SupportStandUtils.Cell cell) {
         for (int i = 0; i < 6; i++) {
            SupportStandUtils.Cell botCrystalText = cell.getData222(i);
            SupportStandUtils.Target lavadirtmap = this.lavaDirtMap.get(botCrystalText);
            if (lavadirtmap != null && !this.isContainsKey(botCrystalText)) {
               if (lavadirtmap.shape() != SupportStandUtils.Shape.FULL) {
                  return false;
               }
            } else {
               if (this.canApiAdded(botCrystalText) || this.cellStandCellSet.contains(botCrystalText)) {
                  return false;
               }

               if (!this.apiLavaWorld.isReservedCell(botCrystalText) && !this.apiLavaWorld.isForeignCell(botCrystalText)) {
                  var lavaApi = this.apiLavaWorld.getCellBoxes(botCrystalText);
                  if (lavaApi.size() != 1 || !SupportStandUtils.MIN_MAX_DATA.equals(lavaApi.get(0))) {
                     return false;
                  }
               } else if (!this.apiLavaWorld.isOutsideBoxesSet(botCrystalText)) {
                  return false;
               }
            }
         }

         return true;
      }

      @Override
      public List<SupportStandUtils.Box> getExtraBoxes(SupportStandUtils.Cell cell) {
         return !this.apiLavaDiff.removed().contains(cell)
               && !this.apiLavaDiff.added().contains(cell)
               && !this.cellStandCellSet.contains(cell)
               && !this.lavaApiCellSet.contains(cell)
               && !this.apiLavaCellSet.contains(cell)
               && !this.lavaDirtMap.containsKey(cell)
            ? this.apiLavaWorld.getExtraBoxes(cell)
            : List.of();
      }

      Terrain getHandlerTerrain() {
         return new Terrain() {
            @Override
            public boolean solid(int solidValue, int solidValue2, int solidValue3) {
               return !Overlay.this.getCellBoxes(new SupportStandUtils.Cell(solidValue, solidValue2, solidValue3)).isEmpty();
            }

            @Override
            public boolean canStandOn(int mutableValue, int mutableValue2, int mutableValue3) {
               SupportStandUtils.Cell cell = new SupportStandUtils.Cell(mutableValue, mutableValue2, mutableValue3);
               return !Overlay.this.getCellBoxes(cell).isEmpty() && Overlay.this.getMax(cell) <= 0.26 && !this.canStandOn(mutableValue, mutableValue2 - 1, mutableValue3)
                  ? false
                  : !Overlay.this.isTrapCell(cell) && !Overlay.this.isTailCell(cell) && !Overlay.this.getCellBoxes(cell).isEmpty();
            }
         };
      }

      boolean isContains(SupportStandUtils.Cell cell) {
         return this.apiLavaCellSet.contains(cell);
      }

      void updateInstance() {
         this.apiLavaDiff = SupportStandUtils.Diff.INSTANCE;
      }

      boolean isContainsKey(SupportStandUtils.Cell cell) {
         return this.dirtMap.containsKey(cell);
      }

      @Override
      public boolean isHazardCell(SupportStandUtils.Cell cell) {
         if (this.apiLavaDiff.removed().contains(cell) || this.apiLavaDiff.added().contains(cell) || this.cellStandCellSet.contains(cell) || this.lavaApiCellSet.contains(cell)) {
            return false;
         } else if (this.apiLavaCellSet.contains(cell)) {
            return this.lavaDirtMap.get(cell).isKindFluid();
         } else {
            return this.lavaDirtMap.containsKey(cell) ? false : this.apiLavaWorld.isHazardCell(cell);
         }
      }

      boolean canFlag(double[] overlayValues, double[] doubles, SupportStandUtils.Cell cell, boolean overlayFlag) {
         return this.canFlag(overlayValues, doubles, cell, overlayFlag, false);
      }

      @Override
      public boolean isTailCell(SupportStandUtils.Cell cell) {
         if (!this.apiLavaWorld.isTailCell(cell)) {
            return false;
         } else {
            return this.apiLavaDiff.removed().contains(cell) || this.apiLavaDiff.added().contains(cell) || this.cellStandCellSet.contains(cell) || this.lavaApiCellSet.contains(cell)
               ? false
               : !this.lavaDirtMap.containsKey(cell) || this.apiLavaCellSet.contains(cell);
         }
      }

      boolean canFlagForCell(SupportStandUtils.Cell cell) {
         return !this.isHazardCell(cell) && !this.canLavaApiForCell(cell) && !this.isTrapCell(cell) && this.getCellBoxes(cell).isEmpty();
      }

      @Override
      public boolean isTrapCell(SupportStandUtils.Cell cell) {
         return !this.apiLavaDiff.removed().contains(cell)
               && !this.apiLavaDiff.added().contains(cell)
               && !this.cellStandCellSet.contains(cell)
               && !this.lavaApiCellSet.contains(cell)
               && !this.lavaDirtMap.containsKey(cell)
            ? this.apiLavaWorld.isTrapCell(cell)
            : false;
      }

      @Override
      public boolean isApiCell(SupportStandUtils.Cell cell) {
         return this.apiLavaWorld.isApiCell(cell);
      }

      @Override
      public boolean isReservedCell(SupportStandUtils.Cell cell) {
         return !this.lavaDirtMap.containsKey(cell) && !this.apiLavaCellSet.contains(cell) && !this.lavaApiCellSet.contains(cell) ? this.apiLavaWorld.isReservedCell(cell) : false;
      }

      boolean canLavaApiForCell(SupportStandUtils.Cell cell) {
         return this.lavaDirtMap.containsKey(cell)
            && !this.apiLavaCellSet.contains(cell)
            && !this.lavaApiCellSet.contains(cell)
            && !this.cellStandCellSet.contains(cell)
            && !this.apiLavaDiff.removed().contains(cell)
            && !this.apiLavaDiff.added().contains(cell)
            && this.apiLavaWorld.isHazardCell(cell);
      }

      @Override
      public boolean isForeignCell(SupportStandUtils.Cell cell) {
         return !this.lavaDirtMap.containsKey(cell) && !this.apiLavaCellSet.contains(cell) && !this.lavaApiCellSet.contains(cell) ? this.apiLavaWorld.isForeignCell(cell) : false;
      }
   }

   public static final class PlanStream {
      private volatile long planStreamValue;
      private static final int STREAM_CODE_SECOND = 2;
      private volatile List<SupportStandUtils.Step> copyOf;
      private final AtomicInteger atomicInteger = new AtomicInteger(0);
      private static final int STREAM_CODE_FIRST = 1;

      public PlanStream() {
         this.copyOf = List.of();
         this.planStreamValue = Long.MIN_VALUE;
      }

      public boolean isAtomicInteger() {
         return this.atomicInteger.get() == 1;
      }

      public boolean isAndSet() {
         return this.atomicInteger.compareAndSet(0, 1);
      }

      public static int getPlaceStand(List<SupportStandUtils.Step> list) {
         int placeStand = 0;

         for (int i = 0; i + 1 < list.size(); i++) {
            SupportStandUtils.Step step = (SupportStandUtils.Step)list.get(i);
            if (step.op() == SupportStandUtils.Op.BREAK || step.op() == SupportStandUtils.Op.DIG_DOWN || step.op() == SupportStandUtils.Op.REMOVE_DIRT) {
               break;
            }

            SupportStandUtils.Step supportStandUtilsStep = (SupportStandUtils.Step)list.get(i + 1);
            if (step.op() == SupportStandUtils.Op.PLACE && (supportStandUtilsStep.op() != SupportStandUtils.Op.PLACE || !Objects.equals(supportStandUtilsStep.stand(), step.stand()))) {
               placeStand = i + 1;
            }
         }

         return placeStand;
      }

      boolean isAndSetAtomicInteger() {
         return this.atomicInteger.compareAndSet(0, 2);
      }

      public List<SupportStandUtils.Step> getCopyOf() {
         return this.copyOf;
      }

      public long getTime() {
         long nanoTime = this.planStreamValue;
         return nanoTime == Long.MIN_VALUE ? 0L : System.nanoTime() - nanoTime;
      }

      void handleAtomicInteger(List<SupportStandUtils.Step> list) {
         if (this.atomicInteger.get() != 2) {
            this.copyOf = List.copyOf(list);
         }
      }

      void updateTime() {
         this.planStreamValue = System.nanoTime();
      }
   }

   private record Removal(SupportStandUtils.Cell cell, SupportStandUtils.Cell stand, double cost) {
   }

   public static enum Shape {
      FULL,
      CHEST,
      HOPPER,
      THIN,
      NONE;
   }

   public static enum Status {
      SOLVED,
      PARTIAL,
      EMPTY;
   }

   public record Step(
      SupportStandUtils.Op op, SupportStandUtils.Cell cell, SupportStandUtils.Cell stand, int count, SupportStandUtils.Intent intent, String why
   ) {
      @Override
      public String toString() {
         return switch (this.op) {
            case WALK -> "WALK to " + this.stand;
            case PILLAR -> "PILLAR x" + this.count + " at " + this.cell + " -> stand " + this.stand;
            case DIRT -> "DIRT "
               + this.cell
               + " from "
               + this.stand
               + " ("
               + this.why
               + ")"
               + (
                  this.intent != null
                     ? (this.intent.sneak ? " sneaking" : "")
                        + " click "
                        + this.intent.support
                        + "/"
                        + SupportStandUtils.formatSouthNorth(this.intent.face)
                        + String.format(Locale.ROOT, " @(%.2f,%.2f,%.2f)", this.intent.hx, this.intent.hy, this.intent.hz)
                     : ""
               );
            case DIG_DOWN -> "DIG_DOWN " + this.cell + " -> stand " + this.stand;
            case REMOVE_DIRT -> "REMOVE_DIRT " + this.cell + " from " + this.stand;
            case BREAK -> "BREAK " + this.cell + " (" + this.why + ")";
            case PLACE -> "PLACE "
               + this.why
               + " "
               + this.cell
               + " from "
               + this.stand
               + (
                  this.intent != null
                     ? (this.intent.sneak ? " sneaking" : "")
                        + " click "
                        + this.intent.support
                        + "/"
                        + SupportStandUtils.formatSouthNorth(this.intent.face)
                        + String.format(
                           Locale.ROOT,
                           " @(%.2f,%.2f,%.2f) look=%s margin=%.2f",
                           this.intent.hx,
                           this.intent.hy,
                           this.intent.hz,
                           SupportStandUtils.formatSouthNorth(this.intent.look),
                           this.intent.margin
                        )
                     : ""
               );
         };
      }
   }

   public record Target(
      SupportStandUtils.Cell cell,
      String name,
      SupportStandUtils.Kind kind,
      int facing,
      SupportStandUtils.Shape shape,
      boolean interactive,
      SupportStandUtils.Cell partner,
      int supportDir
   ) {
      public Target(
         SupportStandUtils.Cell cell,
         String kind,
         SupportStandUtils.Kind shape,
         int name,
         SupportStandUtils.Shape interactive,
         boolean facing,
         SupportStandUtils.Cell partner
      ) {
         this(cell, kind, shape, name, interactive, facing, partner, -1);
      }

      public boolean isKindFluid() {
         return this.kind == SupportStandUtils.Kind.FLUID;
      }
   }

   public interface World {
      boolean isInteractiveCell(SupportStandUtils.Cell cell);

      boolean canOwnDirt(SupportStandUtils.Cell cell);

      List<SupportStandUtils.Box> getCellBoxes(SupportStandUtils.Cell cell);

      default List<SupportStandUtils.Box> getExtraBoxes(SupportStandUtils.Cell cell) {
         return List.of();
      }

      default boolean isApiCell(SupportStandUtils.Cell cell) {
         return false;
      }

      default boolean isReservedCell(SupportStandUtils.Cell cell) {
         return false;
      }

      default List<SupportStandUtils.Box> getLavaOverrideBoxes(SupportStandUtils.Cell cell) {
         return this.getTailOverrideBoxes(cell);
      }

      default boolean isForeignCell(SupportStandUtils.Cell cell) {
         return false;
      }

      default List<SupportStandUtils.Box> getTailOverrideBoxes(SupportStandUtils.Cell cell) {
         return this.getCellBoxes(cell);
      }

      default boolean isLavaOverrideCell(SupportStandUtils.Cell cell) {
         return false;
      }

      default boolean isTailCell(SupportStandUtils.Cell cell) {
         return false;
      }

      boolean isTrapCell(SupportStandUtils.Cell cell);

      default boolean isOutsideBoxesSet(SupportStandUtils.Cell cell) {
         return true;
      }

      boolean isHazardCell(SupportStandUtils.Cell cell);
   }
}
