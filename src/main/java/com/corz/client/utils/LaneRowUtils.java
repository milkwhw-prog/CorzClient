package com.corz.client.utils;

import com.corz.client.utils.HeavyBatchUtils.Kind;
import java.util.ArrayList;
import java.util.List;
import java.util.function.LongPredicate;
import com.corz.client.utils.HeavyBatchUtils;
import com.corz.client.utils.PistonStickyUtils;

public final class LaneRowUtils {
   private static final int[][] laneRowInts = new int[][]{{1, 0, 5}, {-1, 0, 4}, {0, 1, 3}, {0, -1, 2}};
   static final int LANE_ROW_STEP = 1;

   private static int getRowLane(LaneRowUtils.Run run) {
      return run.laneY() == run.rowY() ? 0 : 1;
   }

   public static List<Long> getMembersLane(LaneRowUtils.Run run) {
      var arrayList = new ArrayList();

      for (LaneRowUtils.Member member : run.members()) {
         arrayList.add(member.laneCell());
      }

      return arrayList;
   }

   public static List<LaneRowUtils.Run> getItems(long laneRowValue, LaneRowUtils.World world, int laneRowValue2, int rowLimit, boolean laneRowFlag) {
      ArrayList<LaneRowUtils.Run> arrayList = new ArrayList<>();
      if (!world.canAutoBuilder(laneRowValue)) {
         return arrayList;
      } else {
         int valueInt = ProofSupportUtils.getValueInt(laneRowValue);
         int builderAutoValue = ProofSupportUtils.getValue(laneRowValue);
         int valueForLong = ProofSupportUtils.getValueForLong(laneRowValue);

         for (int laneRowValue7 : new int[]{0, 2}) {
            for (int[] laneRowValues : laneRowInts) {
               if ((laneRowValue7 != 0 || laneRowValues[0] == 0) && (laneRowValue7 != 2 || laneRowValues[1] == 0)) {
                  for (int laneRowValue8 : new int[]{0}) {
                     LaneRowUtils.Run runRow = getRunRow(valueInt, builderAutoValue, valueForLong, laneRowValue7, laneRowValues, laneRowValue8, world, rowLimit, laneRowFlag, 1);
                     LaneRowUtils.Run runRow2 = getRunRow(valueInt, builderAutoValue, valueForLong, laneRowValue7, laneRowValues, laneRowValue8, world, rowLimit, laneRowFlag, -1);
                     LaneRowUtils.Run run = runRow == null ? runRow2 : (runRow2 == null ? runRow : (runRow2.getMembers() > runRow.getMembers() ? runRow2 : runRow));
                     if (run != null && run.getMembers() >= laneRowValue2) {
                        arrayList.add(run);
                     }
                  }
               }
            }
         }

         arrayList.sort((run2, run3) -> {
            int compare = Integer.compare(((com.corz.client.utils.LaneRowUtils.Run) run3).getMembers(), ((com.corz.client.utils.LaneRowUtils.Run) run2).getMembers());
            if (compare != 0) {
               return compare;
            } else {
               int compare2 = Integer.compare(getRowLane(run2), getRowLane(run3));
               return compare2 != 0 ? compare2 : Long.compare(((com.corz.client.utils.LaneRowUtils.Run) run2).laneStart(), ((com.corz.client.utils.LaneRowUtils.Run) run3).laneStart());
            }
         });
         return arrayList;
      }
   }

   public static List<LaneRowUtils.Run> getItems(long laneRowValue, LaneRowUtils.World world, int laneRowValue2, int rowLimit) {
      return getItems(laneRowValue, world, laneRowValue2, rowLimit, true);
   }

   private static long getTime(long laneRowValue, int autoBuilder, long laneRowValue3, boolean laneRowFlag, long laneRowValue4, LaneRowUtils.World world) {
      int[] laneRowValues = PistonStickyUtils.getValues(autoBuilder);
      long replaceableCollisionValue = ProofSupportUtils.getTime(
         ProofSupportUtils.getValueInt(laneRowValue) + laneRowValues[0], ProofSupportUtils.getValue(laneRowValue) + laneRowValues[1], ProofSupportUtils.getValueForLong(laneRowValue) + laneRowValues[2]
      );
      if (replaceableCollisionValue == laneRowValue4) {
         return 0L;
      } else if (world.canReplaceableCollision(replaceableCollisionValue)) {
         return replaceableCollisionValue;
      } else {
         return laneRowFlag && replaceableCollisionValue == laneRowValue3 ? replaceableCollisionValue : 0L;
      }
   }

   private static List<LaneRowUtils.Member> getTarget(List<LaneRowUtils.Member> list, long targetValue, int targetValue2, int targetValue3) {
      int targetValue4 = -1;

      for (int i = 0; i < list.size(); i++) {
         if (((LaneRowUtils.Member)list.get(i)).target() == targetValue) {
            targetValue4 = i;
            break;
         }
      }

      if (targetValue4 < 0) {
         return List.of();
      } else {
         int index = targetValue4;
         int index2 = targetValue4;

         while (index > 0 && canFlag(((LaneRowUtils.Member)list.get(index - 1)).target(), ((LaneRowUtils.Member)list.get(index)).target(), targetValue2, targetValue3)) {
            index--;
         }

         while (index2 + 1 < list.size() && canFlag(((LaneRowUtils.Member)list.get(index2)).target(), ((LaneRowUtils.Member)list.get(index2 + 1)).target(), targetValue2, targetValue3)) {
            index2++;
         }

         return new ArrayList<>(list.subList(index, index2 + 1));
      }
   }

   private static LaneRowUtils.Run getRunRow(
      int rowValue, int builderAutoValue, int valueForLong, int rowValue4, int[] rowValues, int rowValue5, LaneRowUtils.World world, int rowLimit, boolean rowFlag, int rowFactor
   ) {
      int rowValue6 = builderAutoValue + rowValue5;
      int rowFactor2 = rowValue4 == 0 ? 1 : 0;
      int rowFactor3 = rowValue4 == 2 ? 1 : 0;
      if (!canFlag(rowValue, builderAutoValue, valueForLong, rowValues, rowValue6, world, false)) {
         return null;
      } else {
         var arrayList = new ArrayList();
         int rowCounter = 0;

         for (int i = 1; i <= rowLimit; i++) {
            int rowValue7 = rowValue - rowFactor2 * i;
            int rowValue8 = valueForLong - rowFactor3 * i;
            if (!canFlag(rowValue7, builderAutoValue, rowValue8, rowValues, rowValue6, world, true)) {
               break;
            }

            rowCounter++;
         }

         int rowCounter2 = 0;

         for (int j = 1; j <= rowLimit; j++) {
            int laneRowValue = rowValue + rowFactor2 * j;
            int rowValue10 = valueForLong + rowFactor3 * j;
            if (!canFlag(laneRowValue, builderAutoValue, rowValue10, rowValues, rowValue6, world, true)) {
               break;
            }

            rowCounter2++;
         }

         int rowTotal = -rowCounter;
         int rowLimit2 = rowCounter2;
         if (rowCounter2 - rowTotal + 1 > rowLimit) {
            int rowBound = rowCounter2 - rowTotal + 1 - rowLimit;
            int min11 = Math.min(rowBound, rowCounter);
            rowTotal += min11;
            int deltaVar = rowBound - min11;
            rowLimit2 = rowCounter2 - deltaVar;
         }

         if (rowFactor > 0) {
            for (int k = rowTotal; k <= rowLimit2; k++) {
               arrayList.add(new int[]{rowValue + rowFactor2 * k, builderAutoValue, valueForLong + rowFactor3 * k});
            }
         } else {
            for (int m = rowLimit2; m >= rowTotal; m--) {
               arrayList.add(new int[]{rowValue + rowFactor2 * m, builderAutoValue, valueForLong + rowFactor3 * m});
            }
         }

         var arrayList2 = new ArrayList();
         long autoBuilderValue12 = 0L;
         boolean laneRowFlag = false;

         for (int[] rowValues2 : (Iterable<int[]>) (arrayList)) {
            long autoBuilderValue = ProofSupportUtils.getTime(rowValues2[0], rowValues2[1], rowValues2[2]);
            long time14 = ProofSupportUtils.getTime(rowValues2[0] + rowValues[0], rowValue6, rowValues2[2] + rowValues[1]);
            int autoBuilder = world.getAutoBuilder(autoBuilderValue);
            int autoBuilderForLong = world.getAutoBuilderForLong(autoBuilderValue);
            if (rowFlag || autoBuilder < 0 && (autoBuilderForLong < 0 || autoBuilderForLong == 0)) {
               boolean rowFlag3 = false;
               int autoBuilder15 = -1;
               long time16;
               if (autoBuilder >= 0) {
                  if (autoBuilder == 1 || autoBuilder == 0) {
                     laneRowFlag = false;
                     continue;
                  }

                  time16 = getTime(autoBuilderValue, autoBuilder, autoBuilderValue12, laneRowFlag, time14, world);
                  rowFlag3 = true;
                  autoBuilder15 = autoBuilder;
               } else if (autoBuilderForLong >= 0) {
                  time16 = getTime(autoBuilderValue, autoBuilderForLong, autoBuilderValue12, laneRowFlag, time14, world);
               } else {
                  long replaceableCollisionValue = ProofSupportUtils.getTime(rowValues2[0], rowValues2[1] - 1, rowValues2[2]);
                  time16 = world.canReplaceableCollision(replaceableCollisionValue) ? replaceableCollisionValue : (laneRowFlag ? autoBuilderValue12 : 0L);
               }

               if (time16 == 0L) {
                  laneRowFlag = false;
               } else {
                  arrayList2.add(new LaneRowUtils.Member(autoBuilderValue, time14, time16, getValue(time16, autoBuilderValue), autoBuilder15, rowFlag3, world.canReplaceableAir(time16)));
                  autoBuilderValue12 = autoBuilderValue;
                  laneRowFlag = true;
               }
            } else {
               laneRowFlag = false;
            }
         }

         if (arrayList2.isEmpty()) {
            return null;
         } else {
            var target = getTarget(arrayList2, ProofSupportUtils.getTime(rowValue, builderAutoValue, valueForLong), rowFactor2 * rowFactor, rowFactor3 * rowFactor);
            if (target.isEmpty()) {
               return null;
            } else {
               var targetPulse = getTargetPulse(target, rowValues, world);
               Kind kind = Kind.WALKING_SIDE_RUN;
               boolean rowFlag4 = false;
               boolean rowFlag5 = false;

               for (LaneRowUtils.Member member : (Iterable<com.corz.client.utils.LaneRowUtils.Member>) (targetPulse)) {
                  if (member.directional()) {
                     rowFlag4 = true;
                  }

                  if (member.sneakPulse()) {
                     rowFlag5 = true;
                  }
               }

               if (rowFlag5) {
                  kind = Kind.CONTAINER_ROW_RUN;
               } else if (rowFlag4) {
                  kind = Kind.DIRECTIONAL_ROW_RUN;
               }

               return new LaneRowUtils.Run(
                  kind,
                  targetPulse,
                  rowValue4,
                  rowFactor,
                  rowValue6,
                  builderAutoValue,
                  ((LaneRowUtils.Member)targetPulse.get(0)).laneCell(),
                  ((LaneRowUtils.Member)targetPulse.get(targetPulse.size() - 1)).laneCell()
               );
            }
         }
      }
   }

   private static boolean canFlag(long laneRowValue, long proofSupportValue, int laneRowValue3, int laneRowValue4) {
      return ProofSupportUtils.getValueInt(proofSupportValue) - ProofSupportUtils.getValueInt(laneRowValue) == laneRowValue3
         && ProofSupportUtils.getValueForLong(proofSupportValue) - ProofSupportUtils.getValueForLong(laneRowValue) == laneRowValue4
         && ProofSupportUtils.getValue(laneRowValue) == ProofSupportUtils.getValue(proofSupportValue);
   }

   public static int getValue(long laneRowValue, long proofSupportValue) {
      int deltaValue = ProofSupportUtils.getValueInt(proofSupportValue) - ProofSupportUtils.getValueInt(laneRowValue);
      int deltaValue2 = ProofSupportUtils.getValue(proofSupportValue) - ProofSupportUtils.getValue(laneRowValue);
      int deltaValue3 = ProofSupportUtils.getValueForLong(proofSupportValue) - ProofSupportUtils.getValueForLong(laneRowValue);
      if (deltaValue2 > 0) {
         return 1;
      } else if (deltaValue2 < 0) {
         return 0;
      } else if (deltaValue3 < 0) {
         return 2;
      } else if (deltaValue3 > 0) {
         return 3;
      } else {
         return deltaValue < 0 ? 4 : 5;
      }
   }

   private LaneRowUtils() {
   }

   private static boolean canFlag(int laneRowValue, int laneRowValue2, int valueForLong, int[] laneRowValues, int builderAutoValue, LaneRowUtils.World world, boolean laneRowFlag) {
      long time5 = ProofSupportUtils.getTime(laneRowValue, laneRowValue2, valueForLong);
      if (laneRowFlag && !world.canAutoBuilder(time5)) {
         return false;
      } else {
         long time6 = ProofSupportUtils.getTime(laneRowValue + laneRowValues[0], builderAutoValue, valueForLong + laneRowValues[1]);
         if (!world.canAutoBuilderForLong(time6)) {
            return false;
         } else {
            long autoBuilderValue = ProofSupportUtils.getTime(laneRowValue + 2 * laneRowValues[0], builderAutoValue, valueForLong + 2 * laneRowValues[1]);
            return !world.canAutoBuilderForLong(autoBuilderValue) && !world.canReplaceableCollision(autoBuilderValue) ? false : builderAutoValue + 1 != laneRowValue2 || laneRowValues[0] != 0 || laneRowValues[1] != 0;
         }
      }
   }

   private static List<LaneRowUtils.Member> getTargetPulse(List<LaneRowUtils.Member> list, int[] targetPulseValues, LaneRowUtils.World world) {
      var arrayList = new ArrayList(list.size() * 2);
      boolean targetPulseFlag = false;

      for (LaneRowUtils.Member member : list) {
         arrayList.add(member);
         if (!member.directional() && !member.sneakPulse() && world.canAutoBuilderFlag(member.target())) {
            long target = member.target();
            long targetPulseValue = ProofSupportUtils.getTime(
               ProofSupportUtils.getValueInt(target) - targetPulseValues[0], ProofSupportUtils.getValue(target), ProofSupportUtils.getValueForLong(target) - targetPulseValues[1]
            );
            long replaceableCollisionValue = ProofSupportUtils.getTime(ProofSupportUtils.getValueInt(targetPulseValue), ProofSupportUtils.getValue(targetPulseValue) - 1, ProofSupportUtils.getValueForLong(targetPulseValue));
            if (world.canAutoBuilderFlag(targetPulseValue) && world.canReplaceableCollision(replaceableCollisionValue) && !world.canReplaceableAir(replaceableCollisionValue)) {
               arrayList.add(new LaneRowUtils.Member(targetPulseValue, member.laneCell(), replaceableCollisionValue, getValue(replaceableCollisionValue, targetPulseValue)));
               targetPulseFlag = true;
            }
         }
      }

      return (List<LaneRowUtils.Member>)(targetPulseFlag ? arrayList : list);
   }

   public static boolean canTargetMembers(LaneRowUtils.Run run, LaneRowUtils.World world, LongPredicate longPredicate) {
      for (LaneRowUtils.Member member : run.members()) {
         if (!world.canAutoBuilderForLong(member.laneCell())) {
            return false;
         }

         if (!longPredicate.test(member.target()) && !world.canAutoBuilder(member.target())) {
            return false;
         }
      }

      return true;
   }

   public record Member(long target, long laneCell, long support, int supportFace, int lookDir, boolean directional, boolean sneakPulse) {
      public Member(long supportFace, long target, long support, int laneCell) {
         this(supportFace, target, support, laneCell, -1, false, false);
      }
   }

   public record Run(Kind kind, List<LaneRowUtils.Member> members, int axis, int direction, int laneY, int rowY, long laneStart, long laneEnd) {
      public int getMembers() {
         return this.members.size();
      }

      public long getMembersTarget() {
         return this.members.isEmpty() ? 0L : this.members.get(0).target() ^ Long.rotateLeft(this.members.get(this.members.size() - 1).target(), 31);
      }

      public int getMembersDirectional() {
         int membersDirectional = 0;

         for (LaneRowUtils.Member member : this.members) {
            if (member.directional()) {
               membersDirectional++;
            }
         }

         return membersDirectional;
      }

      public int getMembersPulse() {
         int membersPulse = 0;

         for (LaneRowUtils.Member member : this.members) {
            if (member.sneakPulse()) {
               membersPulse++;
            }
         }

         return membersPulse;
      }
   }

   public interface World {
      boolean canAutoBuilder(long autoBuilderValue);

      boolean canCollisionInstance(long collisionValue);

      boolean canAutoBuilderForLong(long autoBuilderValue);

      default boolean canReplaceableAir(long replaceableAirValue) {
         return false;
      }

      default int getAutoBuilder(long autoBuilderValue) {
         return -1;
      }

      default boolean canAutoBuilderFlag(long autoBuilderValue) {
         return false;
      }

      boolean canReplaceableCollision(long replaceableCollisionValue);

      default int getAutoBuilderForLong(long autoBuilderValue) {
         return -1;
      }
   }
}
