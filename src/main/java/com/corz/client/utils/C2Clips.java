package com.corz.client.utils;

import java.util.List;
import com.corz.client.utils.C2Knife;
import com.corz.client.utils.C2Model;

public final class C2Clips {
   public static String formatInspectDraw(C2Knife c2Knife, C2Model c2Model, String formatInspectDraw, C2Knife c2Knife2, C2Model c2Model2) {
      if (formatInspectDraw == null || c2Knife2 == null || c2Model2 == null) {
         return null;
      } else if (c2Model2.unmodifiableMap.containsKey(formatInspectDraw) && c2Knife2.isEndsWith(formatInspectDraw)) {
         return formatInspectDraw;
      } else if (formatInspectDraw.startsWith("draw")) {
         return formatDrawUnmodifiable(c2Knife2, c2Model2);
      } else if (formatInspectDraw.startsWith("inspect")) {
         List unmodifiable = getUnmodifiable(c2Knife2, c2Model2);
         if (unmodifiable.isEmpty()) {
            return null;
         } else {
            int inspectDrawValue = c2Knife != null && c2Model != null ? Math.max(0, getUnmodifiable(c2Knife, c2Model).indexOf(formatInspectDraw)) : 0;
            return (String)unmodifiable.get(inspectDrawValue % unmodifiable.size());
         }
      } else if (!formatInspectDraw.startsWith("melee_")) {
         return null;
      } else {
         int inspectDrawValue2 = formatInspectDraw.length() > 6 && Character.isDigit(formatInspectDraw.charAt(6)) ? formatInspectDraw.charAt(6) - '0' : 1;
         return formatMeleeUnmodifiable(c2Knife2, c2Model2, inspectDrawValue2 == 3, inspectDrawValue2 - 1);
      }
   }

   public static String formatDrawUnmodifiable(C2Knife c2Knife, C2Model c2Model) {
      String formatDrawAngel = c2Knife.formatDrawAngel();
      return c2Model.unmodifiableMap.containsKey(formatDrawAngel) ? formatDrawAngel : "draw";
   }

   public static String formatStaticIdle(C2Knife c2Knife, C2Model c2Model) {
      String formatStaticIdle = c2Knife.formatStaticIdle();
      return c2Model.unmodifiableMap.containsKey(formatStaticIdle) ? formatStaticIdle : "static_idle";
   }

   private C2Clips() {
   }

   public static String formatMeleeUnmodifiable(C2Knife c2Knife, C2Model c2Model, boolean meleeUnmodifiableFlag, int meleeUnmodifiableValue) {
      String formatMeleeUnmodifiable = meleeUnmodifiableFlag ? "melee_3" : "melee_" + (1 + Math.floorMod(meleeUnmodifiableValue, 2));
      if (!c2Model.unmodifiableMap.containsKey(formatMeleeUnmodifiable)) {
         formatMeleeUnmodifiable = "melee_1";
      }

      if (c2Knife.isAnnihilatorYellow() && c2Model.unmodifiableMap.containsKey(formatMeleeUnmodifiable + "_angel")) {
         formatMeleeUnmodifiable = formatMeleeUnmodifiable + "_angel";
      }

      return formatMeleeUnmodifiable;
   }

   public static List<String> getUnmodifiable(C2Knife c2Knife, C2Model c2Model) {
      return c2Model.unmodifiableMap.keySet().stream().filter(knifex -> knifex.startsWith("inspect") && c2Knife.isEndsWith(knifex)).toList();
   }
}
