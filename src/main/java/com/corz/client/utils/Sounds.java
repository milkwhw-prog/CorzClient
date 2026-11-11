package com.corz.client.utils;
import com.corz.client.utils.Clue;

public final class Sounds {
   public static Clue getWorkstationMining(int workstationMiningValue) {
      return switch (workstationMiningValue) {
         case 1000, 1001, 1002, 1049, 1050 -> Clue.MACHINE;
         case 1030, 1035, 1042, 1044, 1500 -> Clue.WORKSTATION;
         case 2001 -> Clue.MINING;
         case 2004 -> Clue.SPAWNER;
         default -> null;
      };
   }

   private Sounds() {
   }

   public static Clue getFenceTrapdoor(String fenceTrapdoorText, boolean fenceTrapdoorFlag) {
      if (fenceTrapdoorText == null || fenceTrapdoorText.isEmpty()) {
         return null;
      } else if (!fenceTrapdoorText.startsWith("block.")) {
         return null;
      } else {
         String substring = fenceTrapdoorText.substring(6);
         if (substring.startsWith("chest.")
            || substring.startsWith("barrel.")
            || substring.startsWith("shulker_box.")
            || substring.startsWith("ender_chest.")
            || substring.startsWith("copper_chest.")) {
            return !substring.endsWith(".open") && !substring.endsWith(".close") ? null : Clue.CHEST_USE;
         } else if (substring.startsWith("anvil.use")
            || substring.startsWith("grindstone.use")
            || substring.startsWith("smithing_table.use")
            || substring.startsWith("enchantment_table.use")
            || substring.startsWith("brewing_stand.brew")
            || substring.startsWith("respawn_anchor.charge")
            || substring.startsWith("respawn_anchor.set_spawn")
            || substring.startsWith("beacon.activate")
            || substring.startsWith("lodestone.lock")
            || substring.startsWith("end_portal_frame.fill")
            || substring.startsWith("composter.fill")
            || substring.startsWith("crafter.craft")) {
            return Clue.WORKSTATION;
         } else if (substring.startsWith("piston.") || substring.startsWith("dispenser.") || substring.startsWith("note_block.") || substring.startsWith("crafter.fail")) {
            return Clue.MACHINE;
         } else if (substring.endsWith("_trapdoor.open")
            || substring.endsWith("_trapdoor.close")
            || substring.startsWith("fence_gate.")
            || substring.endsWith("fence_gate.open")
            || substring.endsWith("fence_gate.close")
            || substring.startsWith("iron_door.")
            || substring.startsWith("iron_trapdoor.")
            || substring.startsWith("lever.click")
            || substring.endsWith("_button.click_on")
            || substring.endsWith("_pressure_plate.click_on")) {
            return Clue.DOORS;
         } else {
            return fenceTrapdoorFlag && substring.endsWith(".place") ? Clue.BUILDING : null;
         }
      }
   }
}
