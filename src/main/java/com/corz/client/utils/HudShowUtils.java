package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class HudShowUtils {
   public static <T extends HudShowUtils.View> List<T> getItems(Collection<? extends T> collection) {
      var arrayList = new ArrayList();
      if (collection == null) {
         return arrayList;
      } else {
         for (HudShowUtils.View view : collection) {
            if (view != null && view.isEnabled() && view.isVisible()) {
               arrayList.add(view);
            }
         }

         return arrayList;
      }
   }

   private HudShowUtils() {
   }

   public interface View {
      boolean isVisible();

      boolean isEnabled();
   }
}
