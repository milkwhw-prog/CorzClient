package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class PanicDestructUtils {
   public static <T extends PanicDestructUtils.Target> List<T> getItems(Collection<? extends T> collection) {
      var arrayList = new ArrayList();
      if (collection == null) {
         return arrayList;
      } else {
         for (PanicDestructUtils.Target target : collection) {
            if (target != null && target.isEnabled() && !target.isHidden()) {
               arrayList.add(target);
            }
         }

         return arrayList;
      }
   }

   private PanicDestructUtils() {
   }

   public interface Target {
      boolean isHidden();

      boolean isEnabled();
   }
}
