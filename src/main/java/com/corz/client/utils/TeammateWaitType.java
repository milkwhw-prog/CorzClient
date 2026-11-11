package com.corz.client.utils;

public enum TeammateWaitType {
   NONE,
   WAIT_TEAMMATE_DEPENDENCY,
   WAIT_TEAMMATE_SUPPORT,
   WAIT_TEAMMATE_WORKSPACE,
   WAIT_TEAMMATE_TEMP_ASSET,
   NOT_MY_LEASE,
   OWNERSHIP_REVOKED,
   ASSIGNMENT_EPOCH_CHANGED;

   public boolean isTeammateWait() {
      return this == WAIT_TEAMMATE_DEPENDENCY || this == WAIT_TEAMMATE_SUPPORT || this == WAIT_TEAMMATE_WORKSPACE || this == WAIT_TEAMMATE_TEMP_ASSET;
   }

   public boolean isFlag() {
      return false;
   }

   public boolean isNone() {
      return this != NONE;
   }
}
