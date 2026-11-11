package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.PriorityQueue;
import java.util.Set;

public final class RequestSouthUtils {
   private static final int[][] dirtFeetInts = new int[][]{{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
   private static final Comparator<RequestSouthUtils.Cell> comparingInt = Comparator.comparingInt(RequestSouthUtils.Cell::x)
      .thenComparingInt(RequestSouthUtils.Cell::y)
      .thenComparingInt(RequestSouthUtils.Cell::z);

   public static RequestSouthUtils.Plan getDirtFeet(RequestSouthUtils.Request request, RequestSouthUtils.View view, Set<Long> set) {
      Objects.requireNonNull(request, "request");
      Objects.requireNonNull(view, "view");
      Set set2 = set == null ? Set.of() : set;
      Set radiusMax = getRadiusMax(request, view);
      PriorityQueue priorityQueue = new PriorityQueue<>(
         Comparator.<RequestSouthUtils.Node>comparingInt(node -> node.cost + getValue(node.feet, request.dirtGoal()))
            .thenComparingInt(node2 -> node2.dirt.size())
            .thenComparingInt(node3 -> node3.steps.size())
            .thenComparingInt(node4 -> node4.feet.x())
            .thenComparingInt(node5 -> node5.feet.y())
            .thenComparingInt(node6 -> node6.feet.z())
            .thenComparingLong(node7 -> getTime(node7.dirt))
      );
      HashMap hashMap = new HashMap();
      RequestSouthUtils.Node node8 = new RequestSouthUtils.Node(request.start(), new LinkedHashSet<>(), List.of(), 0);
      priorityQueue.add(node8);
      hashMap.put(getComparingDirt(node8), 0);
      int dirtFeetCounter = 0;
      int dirtfeetSize = getDirtFeet(node8, request, view).size();
      boolean dirtFeetFlag = false;
      int dirtFeetLimit = Math.abs(request.dirtGoal().x() - request.start().x());
      int abs2 = Math.abs(request.dirtGoal().z() - request.start().z());
      int deltaY = request.dirtGoal().y() - request.start().y();
      if (dirtFeetLimit > request.maxRadius() + 3 || abs2 > request.maxRadius() + 3 || deltaY > request.maxVertical() + 2 || deltaY < -request.maxVertical() - 1) {
         priorityQueue.clear();
      }

      while (!priorityQueue.isEmpty()) {
         if (++dirtFeetCounter > request.maxStates()) {
            dirtFeetFlag = true;
            break;
         }

         RequestSouthUtils.Node requestSouthUtilsNode = (RequestSouthUtils.Node)priorityQueue.poll();
         if (requestSouthUtilsNode.steps.size() <= request.maxSteps() && requestSouthUtilsNode.dirt.size() <= request.maxDirt()) {
            if (canFeetDirt(requestSouthUtilsNode, request, view) && requestSouthUtilsNode.dirt.size() < request.maxDirt()) {
               RequestSouthUtils.Support southNorth = getSouthNorth(request.dirtGoal(), requestSouthUtilsNode.dirt, view);
               LinkedHashSet linkedHashSet = new LinkedHashSet<>(requestSouthUtilsNode.dirt);
               linkedHashSet.add(request.dirtGoal());
               RequestSouthUtils.Step step = new RequestSouthUtils.Step(
                  RequestSouthUtils.Action.PLACE_DIRT, requestSouthUtilsNode.feet, requestSouthUtilsNode.feet, request.dirtGoal(), southNorth.cell, southNorth.face, false
               );
               List list = getItems(requestSouthUtilsNode.steps, step);
               RequestSouthUtils.Node node10 = new RequestSouthUtils.Node(requestSouthUtilsNode.feet, linkedHashSet, list, requestSouthUtilsNode.cost + 5);
               if (!canDirtAction(node10, request, view)) {
                  continue;
               }

               RequestSouthUtils.Result result = requestSouthUtilsNode.dirt.isEmpty()
                  ? RequestSouthUtils.Result.NATURAL_PATH_AVAILABLE
                  : RequestSouthUtils.Result.CONSTRUCTIVE_PATH_AVAILABLE;
               RequestSouthUtils.Cell dirtPlace = getDirtPlace(list, request.start());
               long codeHash = getCodeHash(request, requestSouthUtilsNode.feet, dirtPlace, list, linkedHashSet, radiusMax, result.name());
               if (!set2.contains(codeHash)) {
                  return new RequestSouthUtils.Plan(
                     request.planId(),
                     result,
                     request.schematicTarget(),
                     request.dirtGoal(),
                     request.start(),
                     dirtPlace,
                     requestSouthUtilsNode.feet,
                     request.finalSchematicPlacementStand(),
                     list,
                     linkedHashSet,
                     radiusMax,
                     true,
                     dirtfeetSize,
                     dirtFeetCounter,
                     codeHash
                  );
               }
            }

            for (RequestSouthUtils.Cell cell : getDirtFeetForNodeRequest(requestSouthUtilsNode, request, view)) {
               RequestSouthUtils.Action action = canFlag5(cell, requestSouthUtilsNode.dirt, view)
                  ? RequestSouthUtils.Action.STEP_ONTO_DIRT
                  : RequestSouthUtils.Action.MOVE_TO_EXISTING_STAND;
               RequestSouthUtils.Step step2 = new RequestSouthUtils.Step(action, requestSouthUtilsNode.feet, cell, null, null, null, false);
               handleCostSteps(priorityQueue, hashMap, new RequestSouthUtils.Node(cell, requestSouthUtilsNode.dirt, getItems(requestSouthUtilsNode.steps, step2), requestSouthUtilsNode.cost + 1), request);
            }

            if (requestSouthUtilsNode.dirt.size() < request.maxDirt()) {
               for (RequestSouthUtils.Placement placement : getDirtFeet(requestSouthUtilsNode, request, view)) {
                  LinkedHashSet linkedHashSet2 = new LinkedHashSet<>(requestSouthUtilsNode.dirt);
                  linkedHashSet2.add(placement.target);
                  RequestSouthUtils.Step steps = new RequestSouthUtils.Step(
                     RequestSouthUtils.Action.PLACE_DIRT, requestSouthUtilsNode.feet, requestSouthUtilsNode.feet, placement.target, placement.support, placement.face, placement.jump
                  );
                  RequestSouthUtils.Action action2 = placement.jump ? RequestSouthUtils.Action.JUMP_MOUNT : RequestSouthUtils.Action.STEP_ONTO_DIRT;
                  RequestSouthUtils.Step step4 = new RequestSouthUtils.Step(action2, requestSouthUtilsNode.feet, placement.nextFeet, placement.target, placement.support, placement.face, false);
                  int dirtFeetValue = requestSouthUtilsNode.cost + 5 + (placement.jump ? 2 : 1);
                  handleCostSteps(priorityQueue, hashMap, new RequestSouthUtils.Node(placement.nextFeet, linkedHashSet2, getItems(requestSouthUtilsNode.steps, steps, step4), dirtFeetValue), request);
               }
            }
         }
      }

      RequestSouthUtils.Result result2 = dirtFeetFlag
         ? RequestSouthUtils.Result.SEARCH_LIMIT
         : (dirtfeetSize == 0 ? RequestSouthUtils.Result.NO_LEGAL_FIRST_ACTION : RequestSouthUtils.Result.NO_LEGAL_COMPLETE_SEQUENCE);
      long time2 = getTime(request, request.start(), radiusMax, result2.name());
      return new RequestSouthUtils.Plan(
         request.planId(),
         result2,
         request.schematicTarget(),
         request.dirtGoal(),
         request.start(),
         request.start(),
         null,
         request.finalSchematicPlacementStand(),
         List.of(),
         Set.of(),
         radiusMax,
         false,
         dirtfeetSize,
         dirtFeetCounter,
         time2
      );
   }

   public static long getCodeHash(
      RequestSouthUtils.Request request,
      RequestSouthUtils.Cell cell,
      RequestSouthUtils.Cell dirtPlace,
      List<RequestSouthUtils.Step> list,
      Set<RequestSouthUtils.Cell> set,
      Set<RequestSouthUtils.Cell> radiusMax,
      String codeHashText
   ) {
      long codeHashValue = -3750763034362895579L;
      long time2 = getTime(codeHashValue, request.schematicTarget().hashCode());
      long time3 = getTime(time2, request.expectedState().hashCode());
      long time4 = getTime(time3, request.dirtGoal().hashCode());
      long time5 = getTime(time4, request.start().hashCode());
      long time6 = getTime(time5, Objects.hashCode(dirtPlace));
      long time7 = getTime(time6, Objects.hashCode(cell));
      long time8 = getTime(time7, Objects.hashCode(request.finalSchematicPlacementStand()));
      if (list != null) {
         for (RequestSouthUtils.Step step : list) {
            long time9 = getTime(time8, step.action().ordinal());
            long time10 = getTime(time9, Objects.hashCode(step.dirtTarget()));
            long time11 = getTime(time10, Objects.hashCode(step.support()));
            time8 = getTime(time11, Objects.hashCode(step.supportFace()));
         }
      }

      long codeComparing = getCodeComparing(time8, set);
      long requestSouthValue = getCodeComparing(codeComparing, radiusMax);
      return getTime(requestSouthValue, Objects.hashCode(codeHashText));
   }

   private static RequestSouthUtils.Support getSouthNorth(RequestSouthUtils.Cell cell, Set<RequestSouthUtils.Cell> set, RequestSouthUtils.View view) {
      int[][] ints = new int[][]{{0, -1, 0}, {1, 0, 0}, {-1, 0, 0}, {0, 0, 1}, {0, 0, -1}, {0, 1, 0}};
      String[] strings = new String[]{"UP", "WEST", "EAST", "NORTH", "SOUTH", "DOWN"};

      for (int i = 0; i < ints.length; i++) {
         RequestSouthUtils.Cell dataForInt = cell.getDataForInt(ints[i][0], ints[i][1], ints[i][2]);
         if (canFlag(dataForInt, set, view)) {
            return new RequestSouthUtils.Support(dataForInt, strings[i]);
         }
      }

      return null;
   }

   private static long getCodeComparing(long codeComparingValue, Set<RequestSouthUtils.Cell> set) {
      if (set != null && !set.isEmpty()) {
         ArrayList arrayList = new ArrayList(set);
         arrayList.sort(comparingInt);
         long requestSouthValue = codeComparingValue;

         for (RequestSouthUtils.Cell cell : (Iterable<com.corz.client.utils.RequestSouthUtils.Cell>) (arrayList)) {
            requestSouthValue = getTime(requestSouthValue, cell.hashCode());
         }

         return requestSouthValue;
      } else {
         return getTime(codeComparingValue, 0L);
      }
   }

   private static boolean canFlag(RequestSouthUtils.Cell cell, Set<RequestSouthUtils.Cell> set, RequestSouthUtils.View view) {
      return set.contains(cell) || view.isCollidableNonReplaceableCell(cell);
   }

   private static int getValue(RequestSouthUtils.Cell cell, RequestSouthUtils.Cell cell2) {
      return Math.abs(cell.x() - cell2.x()) + Math.abs(cell.z() - cell2.z()) + Math.max(0, cell2.y() - 1 - cell.y()) * 2;
   }

   private static boolean canStartRadius(RequestSouthUtils.Cell cell, RequestSouthUtils.Request request) {
      int radiusValue = Math.abs(cell.x() - request.start().x());
      int abs2 = Math.abs(cell.z() - request.start().z());
      int abs3 = Math.abs(cell.y() - request.start().y());
      return radiusValue <= request.maxRadius() && abs2 <= request.maxRadius() && abs3 <= request.maxVertical();
   }

   private static boolean canFlagForCellSet(RequestSouthUtils.Cell cell, Set<RequestSouthUtils.Cell> set, RequestSouthUtils.View view) {
      return !set.contains(cell) && view.isPassableCell(cell);
   }

   private static boolean canFlagForCellSetView(RequestSouthUtils.Cell cell, Set<RequestSouthUtils.Cell> set, RequestSouthUtils.View view) {
      return canFlag4(cell.getData822(), set, view) && canFlagForCellSet(cell, set, view) && canFlagForCellSet(cell.getDataCell(), set, view);
   }

   private static boolean canFlag4(RequestSouthUtils.Cell cell, Set<RequestSouthUtils.Cell> set, RequestSouthUtils.View view) {
      return set.contains(cell) || view.isFullHeightSolidCell(cell);
   }

   private static long getTime(long requestSouthValue, long requestSouthValue2) {
      return (requestSouthValue ^ requestSouthValue2) * 1099511628211L;
   }

   private static List<RequestSouthUtils.Step> getItems(List<RequestSouthUtils.Step> list, RequestSouthUtils.Step... steps) {
      ArrayList arrayList = new ArrayList(list);

      for (RequestSouthUtils.Step step : steps) {
         arrayList.add(step);
      }

      return List.copyOf(arrayList);
   }

   private static void handleCostSteps(
      PriorityQueue<RequestSouthUtils.Node> priorityQueue, Map<RequestSouthUtils.StateKey, Integer> map, RequestSouthUtils.Node node, RequestSouthUtils.Request request
   ) {
      if (node.steps.size() <= request.maxSteps() && node.dirt.size() <= request.maxDirt() && canStartRadius(node.feet, request)) {
         RequestSouthUtils.StateKey comparingDirt = getComparingDirt(node);
         int orDefault = map.getOrDefault(comparingDirt, Integer.MAX_VALUE);
         if (node.cost < orDefault) {
            map.put(comparingDirt, node.cost);
            priorityQueue.add(node);
         }
      }
   }

   private static boolean canDirtAction(RequestSouthUtils.Node node, RequestSouthUtils.Request request, RequestSouthUtils.View view) {
      RequestSouthUtils.Cell feet = node.feet;
      HashSet hashSet = new HashSet<>(node.dirt);

      for (int i = node.steps.size() - 1; i >= 0; i--) {
         RequestSouthUtils.Step step = node.steps.get(i);
         if (step.action != RequestSouthUtils.Action.JUMP_MOUNT && step.action != RequestSouthUtils.Action.STEP_ONTO_DIRT) {
            if (step.action == RequestSouthUtils.Action.MOVE_TO_EXISTING_STAND) {
               if (!feet.equals(step.to)) {
                  return false;
               }

               feet = step.from;
            } else if (step.action == RequestSouthUtils.Action.PLACE_DIRT) {
               hashSet.remove(step.dirtTarget);
            }
         } else {
            if (!feet.equals(step.to)) {
               return false;
            }

            hashSet.remove(step.dirtTarget);
            if (!view.isPassableCell(step.from) || Math.abs(step.to.y - step.from.y) > 1) {
               return false;
            }

            feet = step.from;
         }
      }

      return feet.equals(request.start()) && hashSet.isEmpty();
   }

   private static boolean canFlag5(RequestSouthUtils.Cell cell, Set<RequestSouthUtils.Cell> set, RequestSouthUtils.View view) {
      return set.contains(cell.getData822()) || view.isTemporaryBlockCell(cell.getData822());
   }

   private static long getTime(Set<RequestSouthUtils.Cell> set) {
      return getCodeComparing(-3750763034362895579L, set);
   }

   private static RequestSouthUtils.StateKey getComparingDirt(RequestSouthUtils.Node node) {
      ArrayList arrayList = new ArrayList<>(node.dirt);
      arrayList.sort(comparingInt);
      return new RequestSouthUtils.StateKey(node.feet, List.copyOf(arrayList));
   }

   private static RequestSouthUtils.Cell getDirtPlace(List<RequestSouthUtils.Step> list, RequestSouthUtils.Cell dirtPlace) {
      for (RequestSouthUtils.Step step : list) {
         if (step.action() == RequestSouthUtils.Action.PLACE_DIRT) {
            return step.from();
         }
      }

      return dirtPlace;
   }

   private static boolean canFeetDirt(RequestSouthUtils.Node node, RequestSouthUtils.Request request, RequestSouthUtils.View view) {
      if (!canFlagForCellSetView(node.feet, node.dirt, view)) {
         return false;
      } else {
         RequestSouthUtils.Cell dirtGoal = request.dirtGoal();
         if (canFlag6(dirtGoal, node.dirt, view) && getSouthNorth(dirtGoal, node.dirt, view) != null) {
            if (!node.feet.equals(dirtGoal) && !node.feet.equals(dirtGoal.getData822())) {
               int deltaY = dirtGoal.y() - node.feet.y();
               int deltaX = dirtGoal.x() - node.feet.x();
               int deltaZ = dirtGoal.z() - node.feet.z();
               return deltaY >= -1 && deltaY <= 2 && deltaX * deltaX + deltaZ * deltaZ <= 10 && deltaX * deltaX + deltaZ * deltaZ + deltaY * deltaY <= 16;
            } else {
               return false;
            }
         } else {
            return false;
         }
      }
   }

   private static boolean canFlag6(RequestSouthUtils.Cell cell, Set<RequestSouthUtils.Cell> set, RequestSouthUtils.View view) {
      return !set.contains(cell) && view.isAirOrReplaceableCell(cell) && !view.isSchematicOrReservedCell(cell);
   }

   private static List<RequestSouthUtils.Placement> getDirtFeet(RequestSouthUtils.Node node, RequestSouthUtils.Request request, RequestSouthUtils.View view) {
      ArrayList arrayList = new ArrayList();
      RequestSouthUtils.Cell feet = node.feet;
      RequestSouthUtils.Cell cell = feet.getData822();
      if (canStartRadius(feet.getDataCell(), request)
         && canFlag6(feet, node.dirt, view)
         && canFlag4(cell, node.dirt, view)
         && canFlagForCellSet(feet.getDataCell(), node.dirt, view)
         && canFlagForCellSet(feet.getDataCell().getDataCell(), node.dirt, view)) {
         arrayList.add(new RequestSouthUtils.Placement(feet, cell, "UP", feet.getDataCell(), true, false));
      }

      for (int[] dirtFeetValues : dirtFeetInts) {
         RequestSouthUtils.Cell dataForInt = node.feet.getDataForInt(dirtFeetValues[0], 0, dirtFeetValues[1]);
         RequestSouthUtils.Cell data822 = dataForInt.getData822();
         RequestSouthUtils.Support southNorth = getSouthNorth(data822, node.dirt, view);
         if (southNorth != null
            && canFlag6(data822, node.dirt, view)
            && canFlagForCellSet(dataForInt, node.dirt, view)
            && canFlagForCellSet(dataForInt.getDataCell(), node.dirt, view)
            && canStartRadius(dataForInt, request)) {
            LinkedHashSet linkedHashSet = new LinkedHashSet<>(node.dirt);
            linkedHashSet.add(data822);
            if (canFlagForCellSetView(dataForInt, linkedHashSet, view)) {
               arrayList.add(new RequestSouthUtils.Placement(data822, southNorth.cell, southNorth.face, dataForInt, false, false));
            }
         }

         RequestSouthUtils.Support southNorth2 = getSouthNorth(dataForInt, node.dirt, view);
         RequestSouthUtils.Cell dataCell = dataForInt.getDataCell();
         if (southNorth2 != null
            && canFlag6(dataForInt, node.dirt, view)
            && canFlagForCellSet(dataCell, node.dirt, view)
            && canFlagForCellSet(dataCell.getDataCell(), node.dirt, view)
            && canFlagForCellSet(node.feet.getDataCell().getDataCell(), node.dirt, view)
            && canStartRadius(dataCell, request)) {
            LinkedHashSet linkedHashSet2 = new LinkedHashSet<>(node.dirt);
            linkedHashSet2.add(dataForInt);
            if (canFlagForCellSetView(dataCell, linkedHashSet2, view)) {
               arrayList.add(new RequestSouthUtils.Placement(dataForInt, southNorth2.cell, southNorth2.face, dataCell, false, true));
            }
         }
      }

      return arrayList;
   }

   public static long getTime(RequestSouthUtils.Request request, RequestSouthUtils.Cell cell, Set<RequestSouthUtils.Cell> set, String requestSouthText) {
      return getCodeHash(request, null, cell, List.of(), Set.of(), set, requestSouthText);
   }

   private RequestSouthUtils() {
   }

   private static Set<RequestSouthUtils.Cell> getRadiusMax(RequestSouthUtils.Request request, RequestSouthUtils.View view) {
      HashSet hashSet = new HashSet();

      for (int i = -request.maxRadius(); i <= request.maxRadius(); i++) {
         for (int j = -request.maxRadius(); j <= request.maxRadius(); j++) {
            for (int k = -request.maxVertical(); k <= request.maxVertical(); k++) {
               RequestSouthUtils.Cell cell = request.start().getDataForInt(i, k, j);
               if (view.isTemporaryBlockCell(cell)) {
                  hashSet.add(cell);
               }
            }
         }
      }

      return Set.copyOf(hashSet);
   }

   private static List<RequestSouthUtils.Cell> getDirtFeetForNodeRequest(RequestSouthUtils.Node node, RequestSouthUtils.Request request, RequestSouthUtils.View view) {
      ArrayList arrayList = new ArrayList();

      for (int[] dirtFeetValues : dirtFeetInts) {
         RequestSouthUtils.Cell cell = node.feet.getDataForInt(dirtFeetValues[0], 0, dirtFeetValues[1]);
         if (canStartRadius(cell, request) && canFlagForCellSetView(cell, node.dirt, view)) {
            arrayList.add(cell);
         } else {
            RequestSouthUtils.Cell dataCell = cell.getDataCell();
            if (canStartRadius(dataCell, request)
               && canFlagForCellSet(node.feet.getDataCell(), node.dirt, view)
               && canFlagForCellSet(node.feet.getDataCell().getDataCell(), node.dirt, view)
               && canFlagForCellSetView(dataCell, node.dirt, view)) {
               arrayList.add(dataCell);
            } else {
               for (int i = 1; i <= 2; i++) {
                  RequestSouthUtils.Cell dataForInt = cell.getDataForInt(0, -i, 0);
                  if (canFlagForCellSetView(dataForInt, node.dirt, view)) {
                     arrayList.add(dataForInt);
                     break;
                  }

                  if (!canFlagForCellSet(dataForInt.getDataCell(), node.dirt, view)) {
                     break;
                  }
               }
            }
         }
      }

      return arrayList;
   }

   public static enum Action {
      MOVE_TO_EXISTING_STAND,
      PLACE_DIRT,
      JUMP_MOUNT,
      STEP_ONTO_DIRT;
   }

   public record Cell(int x, int y, int z) {
      public RequestSouthUtils.Cell getDataCell() {
         return this.getDataForInt(0, 1, 0);
      }

      public RequestSouthUtils.Cell getDataForInt(int dz, int dy, int dx) {
         return new RequestSouthUtils.Cell(this.x + dz, this.y + dy, this.z + dx);
      }

      public RequestSouthUtils.Cell getData822() {
         return this.getDataForInt(0, -1, 0);
      }
   }

   private record Node(RequestSouthUtils.Cell feet, LinkedHashSet<RequestSouthUtils.Cell> dirt, List<RequestSouthUtils.Step> steps, int cost) {
   }

   private record Placement(
      RequestSouthUtils.Cell target, RequestSouthUtils.Cell support, String face, RequestSouthUtils.Cell nextFeet, boolean jump, boolean stair
   ) {
   }

   public record Plan(
      long id,
      RequestSouthUtils.Result result,
      RequestSouthUtils.Cell schematicTarget,
      RequestSouthUtils.Cell dirtGoal,
      RequestSouthUtils.Cell start,
      RequestSouthUtils.Cell firstConstructionStand,
      RequestSouthUtils.Cell finalSupportStand,
      RequestSouthUtils.Cell finalSchematicPlacementStand,
      List<RequestSouthUtils.Step> steps,
      Set<RequestSouthUtils.Cell> plannedDirt,
      Set<RequestSouthUtils.Cell> reusedOwnedDirt,
      boolean egressSafe,
      int firstActionCandidates,
      int exploredStates,
      long fingerprint
   ) {
      public boolean isConstructiveAvailable() {
         return this.result == RequestSouthUtils.Result.CONSTRUCTIVE_PATH_AVAILABLE;
      }

      public Plan(
         long id,
         RequestSouthUtils.Result result,
         RequestSouthUtils.Cell schematicTarget,
         RequestSouthUtils.Cell dirtGoal,
         RequestSouthUtils.Cell start,
         RequestSouthUtils.Cell firstConstructionStand,
         RequestSouthUtils.Cell finalSupportStand,
         RequestSouthUtils.Cell finalSchematicPlacementStand,
         List<RequestSouthUtils.Step> steps,
         Set<RequestSouthUtils.Cell> plannedDirt,
         Set<RequestSouthUtils.Cell> reusedOwnedDirt,
         boolean egressSafe,
         int firstActionCandidates,
         int exploredStates,
         long fingerprint
      ) {
         steps = List.copyOf(steps == null ? List.of() : steps);
         plannedDirt = Set.copyOf(plannedDirt == null ? Set.of() : plannedDirt);
         reusedOwnedDirt = Set.copyOf(reusedOwnedDirt == null ? Set.of() : reusedOwnedDirt);
         this.id = id;
         this.result = result;
         this.schematicTarget = schematicTarget;
         this.dirtGoal = dirtGoal;
         this.start = start;
         this.firstConstructionStand = firstConstructionStand;
         this.finalSupportStand = finalSupportStand;
         this.finalSchematicPlacementStand = finalSchematicPlacementStand;
         this.steps = steps;
         this.plannedDirt = plannedDirt;
         this.reusedOwnedDirt = reusedOwnedDirt;
         this.egressSafe = egressSafe;
         this.firstActionCandidates = firstActionCandidates;
         this.exploredStates = exploredStates;
         this.fingerprint = fingerprint;
      }
   }

   public record Request(
      long planId,
      RequestSouthUtils.Cell schematicTarget,
      String expectedState,
      RequestSouthUtils.Cell dirtGoal,
      RequestSouthUtils.Cell start,
      RequestSouthUtils.Cell finalSchematicPlacementStand,
      int maxRadius,
      int maxVertical,
      int maxDirt,
      int maxSteps,
      int maxStates
   ) {
      public Request(
         long planId,
         RequestSouthUtils.Cell schematicTarget,
         String expectedState,
         RequestSouthUtils.Cell dirtGoal,
         RequestSouthUtils.Cell start,
         RequestSouthUtils.Cell finalSchematicPlacementStand,
         int maxRadius,
         int maxVertical,
         int maxDirt,
         int maxSteps,
         int maxStates
      ) {
         Objects.requireNonNull(schematicTarget, "schematicTarget");
         Objects.requireNonNull(expectedState, "expectedState");
         Objects.requireNonNull(dirtGoal, "dirtGoal");
         Objects.requireNonNull(start, "start");
         maxRadius = Math.max(2, maxRadius);
         maxVertical = Math.max(1, maxVertical);
         maxDirt = Math.max(1, maxDirt);
         maxSteps = Math.max(2, maxSteps);
         maxStates = Math.max(32, maxStates);
         this.planId = planId;
         this.schematicTarget = schematicTarget;
         this.expectedState = expectedState;
         this.dirtGoal = dirtGoal;
         this.start = start;
         this.finalSchematicPlacementStand = finalSchematicPlacementStand;
         this.maxRadius = maxRadius;
         this.maxVertical = maxVertical;
         this.maxDirt = maxDirt;
         this.maxSteps = maxSteps;
         this.maxStates = maxStates;
      }
   }

   public static enum Result {
      NATURAL_PATH_AVAILABLE,
      CONSTRUCTIVE_PATH_AVAILABLE,
      NO_LEGAL_FIRST_ACTION,
      NO_LEGAL_COMPLETE_SEQUENCE,
      SEARCH_LIMIT;
   }

   private record StateKey(RequestSouthUtils.Cell feet, List<RequestSouthUtils.Cell> dirt) {
   }

   public record Step(
      RequestSouthUtils.Action action,
      RequestSouthUtils.Cell from,
      RequestSouthUtils.Cell to,
      RequestSouthUtils.Cell dirtTarget,
      RequestSouthUtils.Cell support,
      String supportFace,
      boolean jumpPlacement
   ) {
   }

   private record Support(RequestSouthUtils.Cell cell, String face) {
   }

   public interface View {
      boolean isPassableCell(RequestSouthUtils.Cell cell);

      boolean isTemporaryBlockCell(RequestSouthUtils.Cell cell);

      default boolean isAirOrReplaceableCell(RequestSouthUtils.Cell cell) {
         return this.isPassableCell(cell) && !this.isFullHeightSolidCell(cell) && !this.isCollidableNonReplaceableCell(cell);
      }

      default boolean isCollidableNonReplaceableCell(RequestSouthUtils.Cell cell) {
         return this.isFullHeightSolidCell(cell);
      }

      boolean isSchematicOrReservedCell(RequestSouthUtils.Cell cell);

      boolean isFullHeightSolidCell(RequestSouthUtils.Cell cell);
   }
}
