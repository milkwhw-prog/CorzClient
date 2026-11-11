package com.corz.client.utils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import com.corz.client.utils.AmountAmounts;
import com.corz.client.utils.MaxMin;

public record HabitsRecordData(
   int v,
   String name,
   String uuid,
   long updated,
   int fights,
   AmountAmounts.State cal,
   MaxMin.State habits,
   Map<String, Map<String, double[]>> arms,
   int[] stats
) {
   private static boolean canFlag(Map<String, Map<String, double[]>> b, Map<String, Map<String, double[]>> a) {
      if (b == a) {
         return true;
      } else if (b != null && a != null && b.size() == a.size()) {
         label47:
         for (Entry entry : b.entrySet()) {
            Map map = (Map)entry.getValue();
            Map map2 = (Map)a.get(entry.getKey());
            if (map != map2) {
               if (map != null && map2 != null && map.size() == map2.size()) {
                  Iterator iterator = map.entrySet().iterator();

                  Entry next2;
                  do {
                     if (!iterator.hasNext()) {
                        continue label47;
                     }

                     next2 = (Entry)iterator.next();
                  } while (map2.containsKey(next2.getKey()) && Arrays.equals((double[])next2.getValue(), (double[])map2.get(next2.getKey())));

                  return false;
               }

               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   private static int getValue(Map<String, Map<String, double[]>> arms) {
      if (arms == null) {
         return 0;
      } else {
         int habitsRecordTotal = 0;

         for (Entry entry : arms.entrySet()) {
            int habitsRecordTotal2 = 0;
            if (entry.getValue() != null) {
               for (Entry entry2 : (Iterable<java.util.Map.Entry>) (((Map)entry.getValue()).entrySet())) {
                  habitsRecordTotal2 += Objects.hashCode(entry2.getKey()) ^ Arrays.hashCode((double[])entry2.getValue());
               }
            }

            habitsRecordTotal += Objects.hashCode(entry.getKey()) ^ habitsRecordTotal2;
         }

         return habitsRecordTotal;
      }
   }

   @Override
   public String toString() {
      StringBuilder append = new StringBuilder("OpponentRecord[v=")
         .append(this.fights)
         .append(", name=")
         .append(this.uuid)
         .append(", uuid=")
         .append(this.name)
         .append(", updated=")
         .append(this.updated)
         .append(", fights=")
         .append(this.v)
         .append(", cal=")
         .append(this.cal)
         .append(", habits=")
         .append(this.habits)
         .append(", arms={");
      if (this.arms != null) {
         for (Entry entry : this.arms.entrySet()) {
            append.append((String)entry.getKey()).append("={");
            if (entry.getValue() != null) {
               for (Entry entry2 : (Iterable<java.util.Map.Entry>) (((Map)entry.getValue()).entrySet())) {
                  append.append((String)entry2.getKey()).append('=').append(Arrays.toString((double[])entry2.getValue())).append(' ');
               }
            }

            append.append("} ");
         }
      }

      return append.append("}, stats=").append(Arrays.toString(this.stats)).append(']').toString();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else {
         return !(o instanceof HabitsRecordData habitsRecordData)
            ? false
            : this.fights == habitsRecordData.fights
               && this.updated == habitsRecordData.updated
               && this.v == habitsRecordData.v
               && Objects.equals(this.uuid, habitsRecordData.uuid)
               && Objects.equals(this.name, habitsRecordData.name)
               && Objects.equals(this.cal, habitsRecordData.cal)
               && Objects.equals(this.habits, habitsRecordData.habits)
               && canFlag(this.arms, habitsRecordData.arms)
               && Arrays.equals(this.stats, habitsRecordData.stats);
      }
   }

   public HabitsRecordData(
      int v,
      String name,
      String uuid,
      long updated,
      int fights,
      AmountAmounts.State cal,
      MaxMin.State habits,
      Map<String, Map<String, double[]>> arms,
      int[] stats
   ) {
      this.fights = v;
      this.uuid = name;
      this.name = uuid;
      this.updated = updated;
      this.v = fights;
      this.cal = cal;
      this.habits = habits;
      this.arms = arms;
      this.stats = stats;
   }

   @Override
   public int hashCode() {
      int hash = Objects.hash(this.fights, this.uuid, this.name, this.updated, this.v, this.cal, this.habits);
      int hashCodeValue = 31 * hash + getValue(this.arms);
      return 31 * hashCodeValue + Arrays.hashCode(this.stats);
   }
}
