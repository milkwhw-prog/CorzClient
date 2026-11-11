package com.corz.client.utils;

import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

public final class EscalationsEscalation {
   private int escalationsImpossibleCount;
   private int impossibleEscalationsCount;
   private final LinkedHashMap<Long, EscalationsEscalation.Family> escalationsFamiliesMap = new LinkedHashMap<Long, EscalationsEscalation.Family>(
      16, 0.75F, false
   ) {
      @Override
      protected boolean removeEldestEntry(Entry<Long, EscalationsEscalation.Family> entry) {
         return this.size() > 1024;
      }
   };
   private EscalationsEscalation.Decision appendDecision;
   private static final EscalationsEscalation.Strategy[] standProveTypes = new EscalationsEscalation.Strategy[]{
      EscalationsEscalation.Strategy.SIDE_STAND,
      EscalationsEscalation.Strategy.LATERAL_LEDGE,
      EscalationsEscalation.Strategy.VERTICAL_PILLAR,
      EscalationsEscalation.Strategy.BRIDGE,
      EscalationsEscalation.Strategy.STAIR_SCAFFOLD,
      EscalationsEscalation.Strategy.STAND_MIGRATION,
      EscalationsEscalation.Strategy.SERVICE_SPINE,
      EscalationsEscalation.Strategy.PROVE_IMPOSSIBLE
   };
   public static final String COMPLETION_ACCESS_ESCALATION = "COMPLETION_ACCESS_ESCALATION_REQUIRED";

   public int getEscalationsFamilies() {
      return this.escalationsFamiliesMap.size();
   }

   public static EscalationsEscalation.Decision getProveImpossible(
      int proveImpossibleValue, int proveImpossibleLimit, boolean proveImpossibleFlag, Set<EscalationsEscalation.Strategy> set
   ) {
      if (proveImpossibleValue <= 0) {
         return new EscalationsEscalation.Decision(EscalationsEscalation.Verdict.RESUME_LEGACY, null, "nothing remaining");
      } else if (proveImpossibleLimit > 0) {
         return new EscalationsEscalation.Decision(
            EscalationsEscalation.Verdict.EXECUTE_PLAN, null, "plan has " + proveImpossibleLimit + " steps for remaining=" + proveImpossibleValue
         );
      } else if (proveImpossibleFlag) {
         return new EscalationsEscalation.Decision(
            EscalationsEscalation.Verdict.RESUME_LEGACY,
            null,
            "topology changed since the last plan; ordinary admission reconsidered once for remaining=" + proveImpossibleValue
         );
      } else {
         Set set2 = set == null ? Set.of() : set;

         for (EscalationsEscalation.Strategy strategy : standProveTypes) {
            if (strategy == EscalationsEscalation.Strategy.PROVE_IMPOSSIBLE) {
               break;
            }

            if (!set2.contains(strategy)) {
               return new EscalationsEscalation.Decision(
                  EscalationsEscalation.Verdict.ESCALATE,
                  strategy,
                  "COMPLETION_ACCESS_ESCALATION_REQUIRED remaining="
                     + proveImpossibleValue
                     + " steps=0 epochUnchanged next="
                     + strategy
                     + " tried="
                     + set2.size()
               );
            }
         }

         return new EscalationsEscalation.Decision(
            EscalationsEscalation.Verdict.IMPOSSIBLE_PROVEN,
            EscalationsEscalation.Strategy.PROVE_IMPOSSIBLE,
            "COMPLETION_ACCESS_ESCALATION_REQUIRED remaining="
               + proveImpossibleValue
               + " steps=0 epochUnchanged every constructive strategy tried next="
               + EscalationsEscalation.Strategy.PROVE_IMPOSSIBLE
         );
      }
   }

   public int getEscalationsImpossibleCount() {
      return this.escalationsImpossibleCount;
   }

   public EscalationsEscalation.Decision getAppendDecision() {
      return this.appendDecision;
   }

   public static EscalationsEscalation.Strategy[] getStrategyOrder() {
      return (EscalationsEscalation.Strategy[])standProveTypes.clone();
   }

   public void updateEscalationsImpossible() {
      this.escalationsFamiliesMap.clear();
      this.escalationsImpossibleCount = 0;
      this.impossibleEscalationsCount = 0;
      this.appendDecision = null;
   }

   public EscalationsEscalation.Decision getImpossibleEscalations(long impossibleEscalationsValue, long impossibleEscalationsValue2, int proveImpossibleValue, int proveImpossibleLimit, boolean impossibleEscalationsFlag) {
      EscalationsEscalation.Decision data = getProveImpossible(proveImpossibleValue, proveImpossibleLimit, impossibleEscalationsFlag, this.getEscalationsFamilies(impossibleEscalationsValue, impossibleEscalationsValue2));
      if (data.verdict() == EscalationsEscalation.Verdict.ESCALATE) {
         this.handleEscalationsFamilies(impossibleEscalationsValue, impossibleEscalationsValue2, data.next());
         this.escalationsImpossibleCount++;
      } else if (data.verdict() == EscalationsEscalation.Verdict.IMPOSSIBLE_PROVEN) {
         this.handleEscalationsFamilies(impossibleEscalationsValue, impossibleEscalationsValue2, data.next());
         this.impossibleEscalationsCount++;
      }

      this.appendDecision = data;
      return data;
   }

   public Set<EscalationsEscalation.Strategy> getEscalationsFamilies(long escalationsFamiliesValue, long escalationsFamiliesValue2) {
      EscalationsEscalation.Family escalationsfamiliesmap = this.escalationsFamiliesMap.get(escalationsFamiliesValue);
      return escalationsfamiliesmap != null && escalationsfamiliesmap.familyTimestamp == escalationsFamiliesValue2 ? Collections.unmodifiableSet(escalationsfamiliesmap.noneOf) : Set.of();
   }

   public String getAppend() {
      return "families="
         + this.escalationsFamiliesMap.size()
         + " escalations="
         + this.escalationsImpossibleCount
         + " impossible="
         + this.impossibleEscalationsCount
         + " last="
         + (this.appendDecision == null ? "-" : this.appendDecision.verdict() + (this.appendDecision.next() == null ? "" : "/" + this.appendDecision.next()));
   }

   public void handleEscalationsFamilies(long escalationsFamiliesValue, long time, EscalationsEscalation.Strategy strategy) {
      if (strategy != null) {
         EscalationsEscalation.Family escalationsfamiliesmap = this.escalationsFamiliesMap.get(escalationsFamiliesValue);
         if (escalationsfamiliesmap == null) {
            escalationsfamiliesmap = new EscalationsEscalation.Family(time);
            this.escalationsFamiliesMap.put(escalationsFamiliesValue, escalationsfamiliesmap);
         } else if (escalationsfamiliesmap.familyTimestamp != time) {
            escalationsfamiliesmap.familyTimestamp = time;
            escalationsfamiliesmap.noneOf.clear();
         }

         escalationsfamiliesmap.noneOf.add(strategy);
      }
   }

   public int getImpossibleEscalationsCount() {
      return this.impossibleEscalationsCount;
   }

   public record Decision(EscalationsEscalation.Verdict verdict, EscalationsEscalation.Strategy next, String why) {
      public boolean isVerdictEscalate() {
         return this.verdict == EscalationsEscalation.Verdict.ESCALATE || this.verdict == EscalationsEscalation.Verdict.IMPOSSIBLE_PROVEN;
      }
   }

   private static final class Family {
      final EnumSet<EscalationsEscalation.Strategy> noneOf = EnumSet.noneOf(EscalationsEscalation.Strategy.class);
      long familyTimestamp;

      Family(long time) {
         this.familyTimestamp = time;
      }
   }

   public static enum Strategy {
      SIDE_STAND,
      LATERAL_LEDGE,
      VERTICAL_PILLAR,
      BRIDGE,
      STAIR_SCAFFOLD,
      STAND_MIGRATION,
      SERVICE_SPINE,
      PROVE_IMPOSSIBLE;
   }

   public static enum Verdict {
      EXECUTE_PLAN,
      RESUME_LEGACY,
      ESCALATE,
      IMPOSSIBLE_PROVEN;
   }
}
