package com.corz.client.entity;

import net.minecraft.entity.Entity;

public interface ThreadClientHandlerConst {

   ThreadClientHandlerConst.Result resolveAttackIntercept(Object obj, Entity entity);

   default int getHandlerPriority() {
      return 500;
   }

   public static enum Result {
      PASS,
      BLOCK,
      HANDLED;
   }
}
