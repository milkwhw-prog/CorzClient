package com.corz.client.utils;
import com.corz.client.utils.SurroundKitType;

public record CaseMaxData(SurroundKitType lane, boolean available, double value, int prepTicks, boolean pops, boolean kills, String why) {
   public static CaseMaxData getCaseMaxData(SurroundKitType why, String lane) {
      return new CaseMaxData(why, false, Double.NEGATIVE_INFINITY, 0, false, false, lane);
   }
}
