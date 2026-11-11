package com.corz.client.utils;

public interface WeaknessGodApi {
   default boolean isGodWeaknessWaitingPlayer() {
      return false;
   }

   default boolean isAgePlayer() {
      return false;
   }

   default boolean isGodWeakness() {
      return false;
   }

   default boolean isReductionCritical() {
      return false;
   }
}
