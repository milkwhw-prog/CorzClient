package com.corz.client.utils;
import com.corz.client.utils.DimJsonData;

public final class OverworldNetherUtils {
   public static String formatMinecraftOverworld(String minecraftOverworldText) {
      if (minecraftOverworldText != null && !minecraftOverworldText.isEmpty()) {
         return switch (minecraftOverworldText) {
            case "minecraft:overworld" -> "the Overworld";
            case "minecraft:the_nether" -> "the Nether";
            case "minecraft:the_end" -> "the End";
            default -> {
               int indexOf = minecraftOverworldText.indexOf(58);
               yield indexOf >= 0 && indexOf + 1 < minecraftOverworldText.length() ? minecraftOverworldText.substring(indexOf + 1) : minecraftOverworldText;
            }
         };
      } else {
         return "no world";
      }
   }

   private OverworldNetherUtils() {
   }

   public static OverworldNetherUtils.Decision getSeatAdopt(
      DimJsonData dimJsonData, DimJsonData dimJsonData2, DimJsonData dimJsonData3, String seatAdoptText, boolean seatAdoptFlag, boolean seatAdoptFlag2
   ) {
      if (dimJsonData3 != null && dimJsonData3.isDimensionPlacement()) {
         if (seatAdoptText == null || seatAdoptText.isEmpty() || !dimJsonData3.dimension().equals(seatAdoptText)) {
            return new OverworldNetherUtils.Decision(
               OverworldNetherUtils.Action.REFUSE_WRONG_DIMENSION,
               null,
               "The team is building in "
                  + formatMinecraftOverworld(dimJsonData3.dimension())
                  + " and you are in "
                  + formatMinecraftOverworld(seatAdoptText)
                  + ". Go there and it will line up."
            );
         } else if (dimJsonData != null && dimJsonData.canPlacementDimension(dimJsonData3)) {
            if (dimJsonData2 != null && !dimJsonData2.canPlacementDimension(dimJsonData3) && seatAdoptFlag && !seatAdoptFlag2) {
               return new OverworldNetherUtils.Decision(
                  OverworldNetherUtils.Action.SUPPRESSED_BY_MANUAL_OVERRIDE,
                  null,
                  "You moved your own placement - the team is still building at " + dimJsonData3.placement().formatOriginMirror()
               );
            } else {
               return dimJsonData2 != null && !dimJsonData2.canPlacementDimension(dimJsonData3)
                  ? new OverworldNetherUtils.Decision(OverworldNetherUtils.Action.ADOPT, dimJsonData3, "")
                  : new OverworldNetherUtils.Decision(OverworldNetherUtils.Action.ALREADY_MATCHES, null, "");
            }
         } else if (dimJsonData != null) {
            return new OverworldNetherUtils.Decision(
               OverworldNetherUtils.Action.ADOPT,
               dimJsonData3,
               "The team moved the build to " + dimJsonData3.placement().formatOriginMirror() + " - following them"
            );
         } else {
            return dimJsonData2 != null && dimJsonData2.canPlacementDimension(dimJsonData3)
               ? new OverworldNetherUtils.Decision(OverworldNetherUtils.Action.SEAT_AS_IS, dimJsonData3, "")
               : new OverworldNetherUtils.Decision(
                  OverworldNetherUtils.Action.SEAT_AS_IS,
                  dimJsonData3,
                  "Moved your schematic to the team's placement: " + dimJsonData3.placement().formatOriginMirror()
               );
         }
      } else {
         return new OverworldNetherUtils.Decision(OverworldNetherUtils.Action.ALREADY_MATCHES, null, "");
      }
   }

   public static enum Action {
      SEAT_AS_IS,
      ADOPT,
      ALREADY_MATCHES,
      REFUSE_WRONG_DIMENSION,
      SUPPRESSED_BY_MANUAL_OVERRIDE;
   }

   public record Decision(OverworldNetherUtils.Action action, DimJsonData target, String userMessage) {
      public boolean isActionSeat() {
         return this.action == OverworldNetherUtils.Action.SEAT_AS_IS || this.action == OverworldNetherUtils.Action.ADOPT;
      }

      public boolean isAdoptAction() {
         return this.action == OverworldNetherUtils.Action.ADOPT;
      }
   }
}
