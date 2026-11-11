package com.corz.client.utils;
import com.corz.client.utils.DefensiveAggressiveType;
import com.corz.client.utils.CarefulBalancedType;

public record SelfHazData(float off, float self, float def, float move, float space, float elev, float haz, float minUseful, float maxSelfOpen) {
   public static SelfHazData getSelfHaz(DefensiveAggressiveType p, CarefulBalancedType s) {
      DefensiveAggressiveType defensiveAggressiveType = p == null ? DefensiveAggressiveType.BALANCED : p;
      CarefulBalancedType carefulBalancedType = s == null ? CarefulBalancedType.TERMINATOR : s;

      SelfHazData selfHazData = switch (defensiveAggressiveType) {
         case AGGRESSIVE -> new SelfHazData(1.2F, 0.6F, 0.7F, 0.25F, 0.6F, 0.3F, 2.0F, 3.0F, 14.0F);
         case BALANCED -> new SelfHazData(1.0F, 0.9F, 1.0F, 0.3F, 0.8F, 0.3F, 2.0F, 4.0F, 11.0F);
         case DEFENSIVE -> new SelfHazData(0.7F, 1.3F, 1.6F, 0.35F, 0.6F, 0.4F, 2.5F, 5.0F, 8.0F);
         case ESCAPE -> new SelfHazData(0.0F, 1.5F, 2.0F, 0.2F, 0.0F, 0.2F, 3.0F, 99.0F, 4.0F);
      };

      float selfHazFactor = switch (carefulBalancedType) {
         case TERMINATOR -> 1.15F;
         case BALANCED -> 1.0F;
         case CAREFUL -> 0.85F;
      };

      float selfHazValue = switch (carefulBalancedType) {
         case TERMINATOR -> 2.0F;
         case BALANCED -> 0.0F;
         case CAREFUL -> -2.0F;
      };
      return new SelfHazData(selfHazData.off * selfHazFactor, selfHazData.self, selfHazData.def, selfHazData.move, selfHazData.space, selfHazData.elev, selfHazData.haz, selfHazData.minUseful, selfHazData.maxSelfOpen + selfHazValue);
   }
}
