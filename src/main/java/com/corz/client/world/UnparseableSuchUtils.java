package com.corz.client.world;

import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;

public final class UnparseableSuchUtils {
   private UnparseableSuchUtils() {
   }

   private static void handleBlock(UnparseableSuchUtils.FailSink failSink, Block block, String blockText, String orElse, String blockText3) {
      failSink.onFail(Registries.BLOCK.getId(block).toString(), blockText, orElse, blockText3);
   }

   private static <T extends Comparable<T>> BlockState getParse(BlockState blockState, Property<T> property, String unparseableSuchText) {
      var parse = property.parse(unparseableSuchText);
      return ((net.minecraft.block.BlockState) (parse.<BlockState>map(propx -> blockState.with(property, propx)).orElse(null)));
   }

   public static BlockState getPropertyUnparseable(Block block, NbtCompound nbtCompound, UnparseableSuchUtils.FailSink failSink) {
      BlockState defaultState = block.getDefaultState();
      if (nbtCompound == null) {
         return defaultState;
      } else {
         var stateManager = block.getStateManager();

         for (String propertyUnparseableText : nbtCompound.getKeys()) {
            String orElse = nbtCompound.getString(propertyUnparseableText).orElse("");
            var property = stateManager.getProperty(propertyUnparseableText);
            if (property == null) {
               if (failSink != null) {
                  handleBlock(failSink, block, propertyUnparseableText, orElse, "no_such_property");
               }
            } else {
               BlockState parse = getParse(defaultState, property, orElse);
               if (parse == null) {
                  if (failSink != null) {
                     handleBlock(failSink, block, propertyUnparseableText, orElse, "unparseable_value");
                  }
               } else {
                  defaultState = parse;
               }
            }
         }

         return defaultState;
      }
   }

   public static BlockState getPropertyUnparseable(Block block, String propertyUnparseableText, UnparseableSuchUtils.FailSink failSink) {
      BlockState defaultState = block.getDefaultState();
      int indexOf = propertyUnparseableText.indexOf(91);
      int lastIndexOf = propertyUnparseableText.lastIndexOf(93);
      if (indexOf >= 0 && lastIndexOf > indexOf) {
         var stateManager = block.getStateManager();

         for (String propertyUnparseableText2 : propertyUnparseableText.substring(indexOf + 1, lastIndexOf).split(",")) {
            int end = propertyUnparseableText2.indexOf(61);
            if (end >= 0) {
               String trim = propertyUnparseableText2.substring(0, end).trim();
               String orElse = propertyUnparseableText2.substring(end + 1).trim();
               var property = stateManager.getProperty(trim);
               if (property == null) {
                  if (failSink != null) {
                     handleBlock(failSink, block, trim, orElse, "no_such_property");
                  }
               } else {
                  BlockState parse = getParse(defaultState, property, orElse);
                  if (parse == null) {
                     if (failSink != null) {
                        handleBlock(failSink, block, trim, orElse, "unparseable_value");
                     }
                  } else {
                     defaultState = parse;
                  }
               }
            }
         }

         return defaultState;
      } else {
         return defaultState;
      }
   }

   public interface FailSink {
      void onFail(String failText, String blockText, String orElse, String failText4);
   }
}
