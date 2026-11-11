package com.corz.client.utils;

public final class TeamJoinUtils {

   public static int getStage(TeamJoinUtils.Snapshot snapshot) {
      return switch (snapshot.stage()) {
         case OFF, NO_SCHEMATIC -> -8750470;
         case CONTACTING, WAITING_FOR_ANCHOR, PLACEMENT_OVERRIDDEN, ALONE_IN_ROOM, READY_BUILDER_OFF -> -2051263;
         case RELAY_UNREACHABLE, REFUSED_WRONG_SCHEMATIC, REFUSED_OTHER, WRONG_DIMENSION, ENDED -> -2076084;
         case BUILDING -> -11740828;
      };
   }

   private TeamJoinUtils() {
   }

   public static boolean canContactingWaiting(TeamJoinUtils.Stage stage) {
      return stage == TeamJoinUtils.Stage.CONTACTING || stage == TeamJoinUtils.Stage.WAITING_FOR_ANCHOR;
   }

   public static String formatTeamJoin(TeamJoinUtils.Snapshot snapshot) {
      String teamJoinText = snapshot.teamCode().isEmpty() ? "" : " " + snapshot.teamCode();

      return switch (snapshot.stage()) {
         case OFF -> "Not in a team. Press Host or Join";
         case NO_SCHEMATIC -> "Load a schematic first, then press Host or Join";
         case CONTACTING -> "Contacting the team server...";
         case RELAY_UNREACHABLE -> "Team server unreachable - retrying" + (snapshot.detail().isEmpty() ? "" : " (" + snapshot.detail() + ")");
         case REFUSED_WRONG_SCHEMATIC -> "That team is building a different schematic" + (snapshot.detail().isEmpty() ? "" : ": " + snapshot.detail());
         case REFUSED_OTHER -> "Could not join" + (snapshot.detail().isEmpty() ? "" : ": " + snapshot.detail());
         case WAITING_FOR_ANCHOR -> "Matching the team's placement...";
         case WRONG_DIMENSION -> "The team is building in " + snapshot.detail() + ", you are not";
         case PLACEMENT_OVERRIDDEN -> "Your build is not where the team's is"
            + (snapshot.detail().isEmpty() ? "" : " (team: " + snapshot.detail() + ")")
            + " - move it back or press Join again";
         case ALONE_IN_ROOM -> "Nobody else is on" + (teamJoinText.isEmpty() ? " this code" : teamJoinText) + " yet";
         case READY_BUILDER_OFF -> snapshot.builders() + " in the team - turn Auto Builder on to build";
         case BUILDING -> (snapshot.hostName().isEmpty() ? "" : "Host " + snapshot.hostName() + ". ") + snapshot.builders() + " builder" + (snapshot.builders() == 1 ? "" : "s");
         case ENDED -> snapshot.detail().isEmpty() ? "Session ended. Press Host or Join" : "Session ended: " + snapshot.detail();
      };
   }

   public record Snapshot(TeamJoinUtils.Stage stage, String teamCode, String hostName, int builders, String detail) {
      public Snapshot(TeamJoinUtils.Stage stage, String teamCode, String hostName, int builders, String detail) {
         teamCode = teamCode == null ? "" : teamCode;
         hostName = hostName == null ? "" : hostName;
         detail = detail == null ? "" : detail;
         this.stage = stage;
         this.teamCode = teamCode;
         this.hostName = hostName;
         this.builders = builders;
         this.detail = detail;
      }
   }

   public static enum Stage {
      OFF,
      NO_SCHEMATIC,
      CONTACTING,
      RELAY_UNREACHABLE,
      REFUSED_WRONG_SCHEMATIC,
      REFUSED_OTHER,
      WAITING_FOR_ANCHOR,
      WRONG_DIMENSION,
      PLACEMENT_OVERRIDDEN,
      ALONE_IN_ROOM,
      READY_BUILDER_OFF,
      BUILDING,
      ENDED;
   }
}
