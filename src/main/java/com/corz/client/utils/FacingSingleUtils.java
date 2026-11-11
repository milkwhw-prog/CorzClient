package com.corz.client.utils;

import com.corz.client.utils.SingleRightUtils.Type;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import com.corz.client.utils.SingleRightUtils;

public final class FacingSingleUtils {
   public static List<FacingSingleUtils.Bank> getFacing(List<FacingSingleUtils.Pair> list) {
      var arrayList = new ArrayList();
      var hashMap = new HashMap<Long, List<FacingSingleUtils.Pair>>();

      for (FacingSingleUtils.Pair pair : list) {
         hashMap.computeIfAbsent((long)pair.facing << 32 ^ pair.nextFacing.y() & 4294967295L, long2 -> new ArrayList<>()).add(pair);
      }

      for (Entry<Long, List<FacingSingleUtils.Pair>> entry : hashMap.entrySet()) {
         var list2 = (List)entry.getValue();
         int facing = ((FacingSingleUtils.Pair)list2.get(0)).facing;
         char ch = getFacingChar(facing);
         int facingValue = ch == 'X' ? 90 : 88;
         int facingLimit = getPairListScore(list2, true);
         int pairListScore = getPairListScore(list2, false);
         char ch2 = facingLimit >= pairListScore ? (char)88 : (char)90;
         var arrayList2 = new ArrayList<FacingSingleUtils.Pair>(list2);
         boolean facingFlag = ch2 == 88;
         arrayList2.sort(Comparator.comparingInt(pair2 -> getPairScore(pair2, facingFlag)));
         arrayList.add(new FacingSingleUtils.Bank(facing, ch2, ch, arrayList2));
      }

      arrayList.sort(
         Comparator.<FacingSingleUtils.Bank>comparingInt(bank -> -bank.getCellCount())
            .thenComparingInt(bank2 -> bank2.facingPairList.get(0).nextFacing.x())
            .thenComparingInt(bank3 -> bank3.facingPairList.get(0).nextFacing.z())
      );
      return arrayList;
   }

   public static List<FacingSingleUtils.Pair> getSingleFacing(List<FacingSingleUtils.Cell> list) {
      var hashMap = new HashMap();

      for (FacingSingleUtils.Cell cell : list) {
         hashMap.put(cell.getPackedPosition(), cell);
      }

      var arrayList = new ArrayList();
      var hashSet = new HashSet();
      var arrayList2 = new ArrayList(list);
      arrayList2.sort(Comparator.comparingInt(FacingSingleUtils.Cell::y).thenComparingInt(FacingSingleUtils.Cell::x).thenComparingInt(FacingSingleUtils.Cell::z));

      for (FacingSingleUtils.Cell cell2 : (Iterable<com.corz.client.utils.FacingSingleUtils.Cell>) (arrayList2)) {
         if (cell2.type() != Type.SINGLE && !hashSet.contains(cell2.getPackedPosition())) {
            int[] singleFacingValues = SingleRightUtils.getValues(cell2.facing(), cell2.type());
            if (singleFacingValues != null) {
               FacingSingleUtils.Cell facingSingleUtilsCell = (FacingSingleUtils.Cell)hashMap.get(packCellPosition(cell2.x() + singleFacingValues[0], cell2.y(), cell2.z() + singleFacingValues[1]));
               if (facingSingleUtilsCell != null && !hashSet.contains(facingSingleUtilsCell.getPackedPosition())) {
                  FacingSingleUtils.Pair singleTrapped = getSingleTrapped(cell2, facingSingleUtilsCell);
                  if (singleTrapped != null) {
                     arrayList.add(singleTrapped);
                     hashSet.add(cell2.getPackedPosition());
                     hashSet.add(facingSingleUtilsCell.getPackedPosition());
                  }
               }
            }
         }
      }

      return arrayList;
   }

   public static FacingSingleUtils.Pair getSingleTrapped(FacingSingleUtils.Cell cell, FacingSingleUtils.Cell facingSingleUtilsCell) {
      if (cell != null && facingSingleUtilsCell != null) {
         if (cell.trapped() != facingSingleUtilsCell.trapped()) {
            return null;
         } else if (cell.facing() != facingSingleUtilsCell.facing()) {
            return null;
         } else if (cell.type() == Type.SINGLE || facingSingleUtilsCell.type() == Type.SINGLE) {
            return null;
         } else if (cell.type() == facingSingleUtilsCell.type()) {
            return null;
         } else if (canFacing(cell, facingSingleUtilsCell) && canFacing(facingSingleUtilsCell, cell)) {
            return cell.type() == Type.LEFT ? new FacingSingleUtils.Pair(cell, facingSingleUtilsCell) : new FacingSingleUtils.Pair(facingSingleUtilsCell, cell);
         } else {
            return null;
         }
      } else {
         return null;
      }
   }

   public static List<FacingSingleUtils.Cell> getBankCellsForward(FacingSingleUtils.Bank bank) {
      var arrayList = new ArrayList();

      for (FacingSingleUtils.Pair pair : bank.facingPairList) {
         arrayList.add(pair.nextFacing);
      }

      boolean facingSingleFlag = bank.bankAmount == bank.bankTargetChar;
      if (!facingSingleFlag) {
         return arrayList;
      } else {
         var arrayList2 = new ArrayList();

         for (int i = 0; i < arrayList.size(); i += 2) {
            arrayList2.add((FacingSingleUtils.Cell)arrayList.get(i));
         }

         for (int j = 1; j < arrayList.size(); j += 2) {
            arrayList2.add((FacingSingleUtils.Cell)arrayList.get(j));
         }

         return arrayList2;
      }
   }

   public static List<FacingSingleUtils.Cell> getBankCellsReversed(FacingSingleUtils.Bank bank) {
      var hashMap = new HashMap();

      for (FacingSingleUtils.Pair pair : bank.facingPairList) {
         hashMap.put(pair.nextFacing.getPackedPosition(), pair);
      }

      var arrayList = new ArrayList();

      for (FacingSingleUtils.Cell cell : getBankCellsForward(bank)) {
         FacingSingleUtils.Pair facingSingleUtilsPair = (FacingSingleUtils.Pair)hashMap.get(cell.getPackedPosition());
         if (facingSingleUtilsPair != null) {
            arrayList.add(facingSingleUtilsPair.next);
         }
      }

      return arrayList;
   }

   public static char getFacingChar(int facingSingleValue) {
      return (char)(facingSingleValue != 0 && facingSingleValue != 2 ? 'Z' : 'X');
   }

   private FacingSingleUtils() {
   }

   public static List<long[]> getBankCellCoordinates(FacingSingleUtils.Bank bank) {
      FacingSingleUtils.LinkedHashSetLong linkedHashSetLong = new FacingSingleUtils.LinkedHashSetLong();
      var arrayList = new ArrayList();

      for (FacingSingleUtils.Pair pair : bank.facingPairList) {
         for (FacingSingleUtils.Cell cell : pair.getPairCells()) {
            long facingSingleValue = packCellPosition(cell.x(), cell.y() - 1, cell.z());
            if (linkedHashSetLong.addIfAbsent(facingSingleValue)) {
               arrayList.add(new long[]{cell.x(), cell.y() - 1, cell.z()});
            }
         }
      }

      return arrayList;
   }

   private static int getPairScore(FacingSingleUtils.Pair pair, boolean facingSingleFlag) {
      return facingSingleFlag ? pair.nextFacing.x() : pair.nextFacing.z();
   }

   private static boolean canFacing(FacingSingleUtils.Cell cell, FacingSingleUtils.Cell facingSingleUtilsCell) {
      int[] facingValues = SingleRightUtils.getValues(cell.facing(), cell.type());
      return facingValues == null ? false : facingSingleUtilsCell.x() == cell.x() + facingValues[0] && facingSingleUtilsCell.z() == cell.z() + facingValues[1] && facingSingleUtilsCell.y() == cell.y();
   }

   private static int getPairListScore(List<FacingSingleUtils.Pair> list, boolean facingSingleFlag) {
      int facingSingleBound = Integer.MAX_VALUE;
      int minValue = Integer.MIN_VALUE;

      for (FacingSingleUtils.Pair pair : list) {
         int spotBound = facingSingleFlag ? pair.nextFacing.x() : pair.nextFacing.z();
         facingSingleBound = Math.min(facingSingleBound, spotBound);
         minValue = Math.max(minValue, spotBound);
      }

      return minValue - facingSingleBound;
   }

   public static long packCellPosition(int facingSingleValue, int facingSingleValue2, int facingSingleValue3) {
      return (long)(facingSingleValue & 67108863) << 38 | (long)(facingSingleValue3 & 67108863) << 12 | facingSingleValue2 & 4095;
   }

   public static final class Bank {
      public final char bankAmount;
      public final List<FacingSingleUtils.Pair> facingPairList;
      public final char bankTargetChar;
      public final int bankSize;

      public Bank(int value, char ch, char ch2, List<FacingSingleUtils.Pair> items) {
         this.bankSize = value;
         this.bankAmount = ch;
         this.bankTargetChar = ch2;
         this.facingPairList = items;
      }

      public int getCellCount() {
         return this.facingPairList.size();
      }
   }

   public record Cell(int x, int y, int z, int facing, Type type, boolean trapped) {
      public long getPackedPosition() {
         return FacingSingleUtils.packCellPosition(this.x, this.y, this.z);
      }
   }

   private static final class LinkedHashSetLong {
      private final HashSet<Long> linkedHashPositionSet = new HashSet<>();

      boolean addIfAbsent(long linkedHashValue) {
         return this.linkedHashPositionSet.add(linkedHashValue);
      }
   }

   public static final class Pair {
      public final FacingSingleUtils.Cell next;
      public final FacingSingleUtils.Cell nextFacing;
      public final int facing;

      public List<FacingSingleUtils.Cell> getPairCells() {
         return List.of(this.nextFacing, this.next);
      }

      public Pair(FacingSingleUtils.Cell next, FacingSingleUtils.Cell next2) {
         this.nextFacing = next;
         this.next = next2;
         this.facing = next.facing();
      }

      @Override
      public String toString() {
         return "Pair{L=("
            + this.nextFacing.x()
            + ","
            + this.nextFacing.y()
            + ","
            + this.nextFacing.z()
            + ") R=("
            + this.next.x()
            + ","
            + this.next.y()
            + ","
            + this.next.z()
            + ") facing="
            + this.facing
            + "}";
      }
   }
}
