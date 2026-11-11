package com.corz.client.utils;
import com.corz.client.utils.ExcavationCraftedType;

public final class CraftedExcavation {
   public final int posX;
   public static final int CRAFTED_VERSION = 1;
   public final ExcavationCraftedType excavationCraftedType;
   public final int craftedExcavationValue;
   public final int penDivValue;
   public final int posY;
   public final int craftedTypeId;
   public final int craftedGroupId;
   public final int flagMask;
   public final int craftedCategory;
   public final int craftedWeight;
   public final int posZ;
   public final int faceIndex;

   public CraftedExcavation(int craftedExcavationValue, int craftedExcavationValue2, int craftedExcavationValue3, ExcavationCraftedType excavationCraftedType, int craftedExcavationValue4, int craftedExcavationValue5, int craftedExcavationValue6, int craftedExcavationValue7) {
      this(craftedExcavationValue, craftedExcavationValue2, craftedExcavationValue3, excavationCraftedType, craftedExcavationValue4, craftedExcavationValue5, craftedExcavationValue6, craftedExcavationValue7, 0);
   }

   public boolean hasFlag(int craftedExcavationValue) {
      return (this.flagMask & craftedExcavationValue) != 0;
   }

   public CraftedExcavation(int craftedExcavationValue, int craftedExcavationValue2, int craftedExcavationValue3, ExcavationCraftedType excavationCraftedType, int craftedExcavationValue4, int craftedExcavationValue5, int craftedExcavationValue6, int craftedExcavationValue7, int craftedExcavationValue8) {
      this(craftedExcavationValue, craftedExcavationValue2, craftedExcavationValue3, excavationCraftedType, craftedExcavationValue4, craftedExcavationValue5, craftedExcavationValue6, craftedExcavationValue7, craftedExcavationValue8, 0, 0);
   }

   public static CraftedExcavation createTyped(int classValue, int y, int classValue3, ExcavationCraftedType excavationCraftedType, int classValue4, int classValue5, int classValue6, int biomeCategory) {
      return new CraftedExcavation(classValue, y, classValue3, excavationCraftedType, classValue4, -1, -1, 0, 0, classValue5, classValue6, biomeCategory);
   }

   public static CraftedExcavation createCategorized(int classValue, int classValue2, int classValue3, ExcavationCraftedType excavationCraftedType, int classValue4, int classValue5, int classValue6) {
      return new CraftedExcavation(classValue, classValue2, classValue3, excavationCraftedType, classValue4, -1, -1, classValue6, classValue5);
   }

   public CraftedExcavation(int craftedExcavationValue, int craftedExcavationValue2, int craftedExcavationValue3, ExcavationCraftedType excavationCraftedType, int craftedExcavationValue4, int craftedExcavationValue5, int craftedExcavationValue6, int craftedExcavationValue7, int craftedExcavationValue8, int craftedExcavationValue9, int craftedExcavationValue10) {
      this(craftedExcavationValue, craftedExcavationValue2, craftedExcavationValue3, excavationCraftedType, craftedExcavationValue4, craftedExcavationValue5, craftedExcavationValue6, craftedExcavationValue7, craftedExcavationValue8, craftedExcavationValue9, craftedExcavationValue10, 0);
   }

   public int getChebyshevDistance(CraftedExcavation craftedExcavation) {
      int craftedExcavationBound = Math.abs(this.posX - craftedExcavation.posX);
      int aggroPreemptBound = Math.abs(this.posY - craftedExcavation.posY);
      int rocketsBuyBound = Math.abs(this.posZ - craftedExcavation.posZ);
      return Math.max(craftedExcavationBound, Math.max(aggroPreemptBound, rocketsBuyBound));
   }

   public CraftedExcavation(
      int value, int value2, int value3, ExcavationCraftedType excavationCraftedType, int value4, int value5, int value6, int value7, int value8, int value9, int value10, int value11
   ) {
      this.posX = value;
      this.posY = value2;
      this.posZ = value3;
      this.excavationCraftedType = excavationCraftedType;
      this.craftedWeight = value4;
      this.faceIndex = value5;
      this.craftedExcavationValue = value6;
      this.craftedCategory = value7;
      this.penDivValue = value8;
      this.craftedTypeId = value9;
      this.flagMask = value10;
      this.craftedGroupId = value11;
   }

   public static CraftedExcavation createTypedNoGroup(int classValue, int classValue2, int classValue3, ExcavationCraftedType excavationCraftedType, int classValue4, int classValue5, int classValue6) {
      return new CraftedExcavation(classValue, classValue2, classValue3, excavationCraftedType, classValue4, -1, -1, 0, 0, classValue5, classValue6);
   }

   public static CraftedExcavation createFull(
      int classValue, int y, int z, ExcavationCraftedType excavationCraftedType, int classValue4, int classValue5, int classValue6, int classValue7, int classValue8, int classValue9
   ) {
      return new CraftedExcavation(classValue, y, z, excavationCraftedType, classValue4, -1, -1, classValue6, classValue5, classValue7, classValue8, classValue9);
   }

   public static CraftedExcavation createCategorizedTyped(int classValue, int classValue2, int classValue3, ExcavationCraftedType excavationCraftedType, int classValue4, int classValue5, int classValue6, int classValue7, int classValue8) {
      return new CraftedExcavation(classValue, classValue2, classValue3, excavationCraftedType, classValue4, -1, -1, classValue6, classValue5, classValue7, classValue8);
   }

   public CraftedExcavation(int craftedExcavationValue, int craftedExcavationValue2, int craftedExcavationValue3, ExcavationCraftedType excavationCraftedType, int craftedExcavationValue4) {
      this(craftedExcavationValue, craftedExcavationValue2, craftedExcavationValue3, excavationCraftedType, craftedExcavationValue4, -1, -1, 0, 0);
   }
}
