package com.corz.client.utils;

import com.corz.client.utils.ScissorTgtHandler.Snapshot;
import ai.auth.Module;
import com.corz.client.utils.ScissorTgtHandler;
public final class ScissorTgtUtils {
   public static Snapshot getInstance(ScissorTgtHandler scissorTgtHandler) {
      if (scissorTgtHandler == null) {
         return Snapshot.INSTANCE;
      } else {
         try {
            Snapshot instanceMenu = scissorTgtHandler.getStatusSnapshot();
            return instanceMenu != null ? instanceMenu : Snapshot.INSTANCE;
         } catch (RuntimeException e) {
            return Snapshot.INSTANCE;
         }
      }
   }

   private ScissorTgtUtils() {
   }

   public static Snapshot getMenuTurnData(Module Module) {
      return Module instanceof ScissorTgtHandler scissorTgtHandler ? getInstance(scissorTgtHandler) : null;
   }
}
