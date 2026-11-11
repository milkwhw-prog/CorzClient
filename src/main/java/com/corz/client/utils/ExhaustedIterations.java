package com.corz.client.utils;

import java.util.Locale;

public final class ExhaustedIterations {
   private int episodeExhaustedCount;
   private int exhaustedEpisodeCount;
   private int exhaustedIterationsCount;
   private boolean untriedUnprovenFlag;
   private ExhaustedIterations.Step giveTowardType;
   private final int barrenIterationsValue;
   private int episodesExhaustedTotal;
   private boolean episodeLadderFlag;
   private int barrenIterationsCount;

   public int getBarrenIterationsCount() {
      return this.barrenIterationsCount;
   }

   public String formatIterationSummary() {
      return String.format(
         Locale.ROOT,
         "component=%d iterations=%d barren=%d mined=%d moved=%d retired=%d last=%s escalated=%s exhausted=%s",
         this.barrenIterationsValue,
         this.episodeExhaustedCount,
         this.exhaustedIterationsCount,
         this.barrenIterationsCount,
         this.exhaustedEpisodeCount,
         this.episodesExhaustedTotal,
         this.giveTowardType,
         this.untriedUnprovenFlag,
         this.episodeLadderFlag
      );
   }

   public int getEpisodeExhaustedCount() {
      return this.episodeExhaustedCount;
   }

   public boolean isUntriedUnprovenFlag() {
      return this.untriedUnprovenFlag;
   }

   public int getEpisodeExhaustedTotal() {
      return this.exhaustedIterationsCount;
   }

   public ExhaustedIterations(int barrenIterationsValue) {
      this.barrenIterationsValue = barrenIterationsValue;
   }

   public ExhaustedIterations.Progress getEpisodeExhausted(int episodeExhaustedLimit, int episodeExhaustedLimit2, int episodeExhaustedLimit3) {
      this.episodeExhaustedCount++;
      if (this.episodeLadderFlag) {
         return this.getGiveToward(ExhaustedIterations.Step.GIVE_UP, "the episode is already exhausted");
      } else if (this.exhaustedIterationsCount >= 3) {
         this.episodeLadderFlag = true;
         return this.getGiveToward(ExhaustedIterations.Step.GIVE_UP, this.exhaustedIterationsCount + " consecutive iterations changed nothing");
      } else if (this.exhaustedIterationsCount >= 2) {
         this.untriedUnprovenFlag = true;
         return this.getGiveToward(ExhaustedIterations.Step.ESCALATE, this.exhaustedIterationsCount + " consecutive iterations changed nothing \u2014 escalating");
      } else if (episodeExhaustedLimit > 0) {
         return this.getGiveToward(ExhaustedIterations.Step.MINE_OWNED, "mine one of our own blocks bounding the pocket (" + episodeExhaustedLimit + " left)");
      } else if (episodeExhaustedLimit2 > 0) {
         return this.getGiveToward(ExhaustedIterations.Step.MOVE_TOWARD_ESCAPE, "stand on an untried cell inside the pocket (" + episodeExhaustedLimit2 + " left)");
      } else if (episodeExhaustedLimit3 > 0) {
         return this.getGiveToward(
            ExhaustedIterations.Step.MARK_CANDIDATE_IMPOSSIBLE, "retire one doorway candidate permanently (" + episodeExhaustedLimit3 + " left)"
         );
      } else if (!this.untriedUnprovenFlag) {
         this.untriedUnprovenFlag = true;
         return this.getGiveToward(ExhaustedIterations.Step.ESCALATE, "no owned block, no untried cell, no unproven candidate");
      } else {
         this.episodeLadderFlag = true;
         return this.getGiveToward(ExhaustedIterations.Step.GIVE_UP, "the ladder is spent and the escalation produced nothing");
      }
   }

   public int getExhaustedIterationsTotal() {
      return this.episodesExhaustedTotal;
   }

   public ExhaustedIterations.Step getGiveTowardType() {
      return this.giveTowardType;
   }

   public boolean isEpisodeLadderFlag() {
      return this.episodeLadderFlag;
   }

   private ExhaustedIterations.Progress getGiveToward(ExhaustedIterations.Step giveTowardType, String giveTowardText) {
      this.giveTowardType = giveTowardType;
      return new ExhaustedIterations.Progress(giveTowardType, giveTowardText);
   }

   public int getExhaustedIterationsCount() {
      return this.exhaustedEpisodeCount;
   }

   public int getBarrenIterationsValue() {
      return this.barrenIterationsValue;
   }

   public void recordEpisodeOutcome(boolean episodeExhaustedFlag, boolean episodeExhaustedFlag2, boolean episodeExhaustedFlag3) {
      if (episodeExhaustedFlag) {
         this.barrenIterationsCount++;
      }

      if (episodeExhaustedFlag2) {
         this.exhaustedEpisodeCount++;
      }

      if (episodeExhaustedFlag3) {
         this.episodesExhaustedTotal++;
      }

      if (!episodeExhaustedFlag && !episodeExhaustedFlag2 && !episodeExhaustedFlag3) {
         this.exhaustedIterationsCount++;
      } else {
         this.exhaustedIterationsCount = 0;
      }
   }

   public record Progress(ExhaustedIterations.Step step, String why) {
      public boolean isStepGive() {
         return this.step != ExhaustedIterations.Step.ESCALATE && this.step != ExhaustedIterations.Step.GIVE_UP;
      }
   }

   public static enum Step {
      MINE_OWNED,
      MOVE_TOWARD_ESCAPE,
      MARK_CANDIDATE_IMPOSSIBLE,
      ESCALATE,
      GIVE_UP;
   }
}
