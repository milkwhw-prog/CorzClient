package com.corz.client.world;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;

public final class ElseBuilder {
   public final List<ElseBuilder.Region> elseRegionList;
   public final String orElse;
   public final LinkedHashMap<Block, Integer> builderSelectMap;
   public final List<String> builderAutoItems;
   public final int orElse2BuilderElse;
   public final int orElse4BuilderElse;
   public final int orElse3BuilderElse;
   public final int builderAutoValue;

   public ElseBuilder(
      String orElse,
      int orElse2,
      int orElse3,
      int orElse4,
      List<ElseBuilder.Region> items,
      LinkedHashMap<Block, Integer> builderSelectMap,
      int builderAutoValue,
      List<String> builderAutoItems
   ) {
      this.orElse = orElse;
      this.orElse2BuilderElse = orElse2;
      this.orElse3BuilderElse = orElse3;
      this.orElse4BuilderElse = orElse4;
      this.elseRegionList = items;
      this.builderSelectMap = builderSelectMap;
      this.builderAutoValue = builderAutoValue;
      this.builderAutoItems = builderAutoItems;
   }

   public static final class Region {
      public final BlockState[] blockStates;
      public final int orElse;
      public final int orElse6Else;
      public final int orElse2Else;
      private final int[] elseTable;
      public final int elseArrayValue;
      public final int orElse5Else;
      public final int orElse4Else;
      public final int elseValue;
      public final int regionValue;
      public final Map<Integer, Integer> elseIntegerMap;
      public final int regionEnd;

      public Region(int orElse, int orElse2, int orElse3, int j, int orElse5, int orElse6, BlockState[] blockStates, int[] values, Map<Integer, Integer> map) {
         this.regionEnd = orElse;
         this.orElse2Else = orElse2;
         this.orElse = orElse3;
         this.orElse4Else = j;
         this.orElse5Else = orElse5;
         this.orElse6Else = orElse6;
         this.elseValue = Math.abs(j);
         this.elseArrayValue = Math.abs(orElse5);
         this.regionValue = Math.abs(orElse6);
         this.blockStates = blockStates;
         this.elseTable = values;
         this.elseIntegerMap = map == null ? Map.of() : map;
      }

      public int getElse(int elseValue, int elseFactor, int k) {
         int index = (elseFactor * this.regionValue + k) * this.elseValue + elseValue;
         return index >= 0 && index < this.elseTable.length ? this.elseTable[index] : -1;
      }

      public int getElse() {
         return this.orElse + (this.orElse6Else < 0 ? this.orElse6Else + 1 : 0);
      }

      public int getElseRegionEnd() {
         return this.regionEnd + (this.orElse4Else < 0 ? this.orElse4Else + 1 : 0);
      }

      public Region(int regionValue, int regionValue2, int regionValue3, int regionValue4, int j, int regionValue6, BlockState[] blockStates, int[] regionValues) {
         this(regionValue, regionValue2, regionValue3, regionValue4, j, regionValue6, blockStates, regionValues, Map.of());
      }

      public BlockState getStatesBlock(int statesBlockValue, int elseFactor, int j) {
         int else2 = this.getElse(statesBlockValue, elseFactor, j);
         return else2 >= 0 && else2 < this.blockStates.length ? this.blockStates[else2] : null;
      }

      public int getElseForInt(int elseValue, int elseFactor, int j) {
         return (elseFactor * this.regionValue + j) * this.elseValue + elseValue;
      }

      public Integer getNumber(int numberValue, int elseFactor, int j) {
         return this.elseIntegerMap.get(this.getElseForInt(numberValue, elseFactor, j));
      }

      public int getElseOrElse2Else() {
         return this.orElse2Else + (this.orElse5Else < 0 ? this.orElse5Else + 1 : 0);
      }
   }
}
