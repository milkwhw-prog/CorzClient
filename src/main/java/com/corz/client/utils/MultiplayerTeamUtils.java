package com.corz.client.utils;

public final class MultiplayerTeamUtils {
   public static boolean canFlag(boolean multiplayerTeamFlag, boolean multiplayerTeamFlag2, boolean multiplayerTeamFlag3, boolean multiplayerTeamFlag4) {
      return multiplayerTeamFlag && multiplayerTeamFlag2 ? !multiplayerTeamFlag3 || multiplayerTeamFlag4 : false;
   }

   public static String formatMultiplayerTeam(boolean multiplayerTeamFlag) {
      return multiplayerTeamFlag ? "multiplayer: your build is not at the team's placement" : "multiplayer: waiting for the team's placement";
   }

   private MultiplayerTeamUtils() {
   }
}
