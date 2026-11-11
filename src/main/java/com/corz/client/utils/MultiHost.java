package com.corz.client.utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class MultiHost {
   private boolean presentActive;
   private final Map<String, String> presentStringMap;
   private final Set<String> presentStringSet = new LinkedHashSet<>();

   public boolean isFlag() {
      return this.presentActive;
   }

   public void updateFlag() {
      this.presentStringSet.clear();
      this.presentStringMap.clear();
      this.presentActive = false;
   }

   private String formatText(String multiHostText) {
      String multiHostText2 = this.presentStringMap.get(multiHostText);
      return multiHostText2 != null && !multiHostText2.isBlank() ? multiHostText2 : multiHostText.substring(0, Math.min(6, multiHostText.length()));
   }

   public MultiHost.Result getInstancePresent(List<MultiHost.Member> list, String presentText) {
      LinkedHashSet linkedHashSet = new LinkedHashSet();
      if (list != null) {
         for (MultiHost.Member member : list) {
            if (member != null && member.id() != null && !member.id().isEmpty() && (presentText == null || !presentText.equals(member.id()))) {
               if (member.name() != null && !member.name().isBlank()) {
                  this.presentStringMap.put(member.id(), member.name());
               }

               if (member.present()) {
                  linkedHashSet.add(member.id());
               }
            }
         }
      }

      if (!this.presentActive) {
         this.presentActive = true;
         this.presentStringSet.clear();
         this.presentStringSet.addAll(linkedHashSet);
         return MultiHost.Result.INSTANCE;
      } else {
         ArrayList arrayList = new ArrayList();

         for (String multiHostText : (Iterable<java.lang.String>) (linkedHashSet)) {
            if (!this.presentStringSet.contains(multiHostText)) {
               arrayList.add(this.formatText(multiHostText));
            }
         }

         ArrayList arrayList2 = new ArrayList();

         for (String tickx : this.presentStringSet) {
            if (!linkedHashSet.contains(tickx)) {
               arrayList2.add(this.formatText(tickx));
            }
         }

         this.presentStringSet.clear();
         this.presentStringSet.addAll(linkedHashSet);
         return arrayList.isEmpty() && arrayList2.isEmpty() ? MultiHost.Result.INSTANCE : new MultiHost.Result(arrayList, arrayList2);
      }
   }

   public MultiHost() {
      this.presentStringMap = new LinkedHashMap<>();
   }

   public record Member(String id, String name, boolean present) {
   }

   public record Result(List<String> arrived, List<String> departed) {
      public static final MultiHost.Result INSTANCE = new MultiHost.Result(List.of(), List.of());

      public boolean isDepartedArrived() {
         return !this.arrived.isEmpty() || !this.departed.isEmpty();
      }
   }
}
