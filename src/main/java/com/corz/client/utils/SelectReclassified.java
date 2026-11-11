package com.corz.client.utils;

import java.util.BitSet;

public final class SelectReclassified {
   public static final byte CODE_REMOVED = 2;
   private int selectIndexReclassified;
   private final byte[] bitTable;
   public static final byte CODE_NONE = 0;
   private final BitSet bitSet;
   private int selectIndexReused;
   public static final byte CODE_ADDED = 1;
   private int selectIndexDirtyMarks;
   public static final byte CODE_UNKNOWN = 4;
   public static final byte CODE_CHANGED = 3;

   public SelectReclassified(int reclassifiedSize) {
      this.bitTable = new byte[reclassifiedSize];
      this.bitSet = new BitSet(reclassifiedSize);
      this.bitSet.set(0, reclassifiedSize);
   }

   public int getCardinality() {
      return this.bitSet.cardinality();
   }

   public int getSelectIndexDirtyMarks() {
      return this.selectIndexDirtyMarks;
   }

   public String formatSelectDirty() {
      return "selectIndexReused="
         + this.selectIndexReused
         + " selectIndexReclassified="
         + this.selectIndexReclassified
         + " selectIndexDirtyMarks="
         + this.selectIndexDirtyMarks;
   }

   public boolean canBit(int index) {
      return !this.bitSet.get(index) && this.bitTable[index] != 0;
   }

   public void updateBit() {
      this.bitSet.set(0, this.bitTable.length);
   }

   public int getValue() {
      return this.bitTable.length;
   }

   public void handleDirtyMarks(int dirtyMarksLimit) {
      if (dirtyMarksLimit >= 0 && dirtyMarksLimit < this.bitTable.length && !this.bitSet.get(dirtyMarksLimit)) {
         this.bitSet.set(dirtyMarksLimit);
         this.selectIndexDirtyMarks++;
      }
   }

   public byte getReusedSelect(int index) {
      this.selectIndexReused++;
      return this.bitTable[index];
   }

   public int getSelectIndexReused() {
      return this.selectIndexReused;
   }

   public void handleReclassifiedSelect(int index, byte reclassifiedSelectValue) {
      this.bitTable[index] = reclassifiedSelectValue;
      this.bitSet.clear(index);
      this.selectIndexReclassified++;
   }

   public int getSelectIndexReclassified() {
      return this.selectIndexReclassified;
   }

   public byte getValue(int index) {
      return this.bitTable[index];
   }
}
