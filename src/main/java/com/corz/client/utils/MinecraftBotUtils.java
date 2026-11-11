package com.corz.client.utils;

import com.corz.client.utils.CpxVillagerData.Act;
import com.corz.client.utils.CpxVillagerData.Kind;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.util.Identifier;
import ai.auth.Module;
import com.corz.client.utils.CpxVillagerData;
import com.corz.client.utils.AutoYourUtils;

public final class MinecraftBotUtils {
   private static final Map<String, CpxVillagerData> MINECRAFT_RESPAWN_MAP = new HashMap<>();
   private static final Map<String, CpxVillagerData> MINECRAFT_ENDER_MAP = new LinkedHashMap<>();
   private static final List<CpxVillagerData> MINECRAFT_RESPAWN_ITEMS = new ArrayList<>();
   public static final Identifier identifier = Identifier.of("minecraft", "command_block");
   public static final List<CpxVillagerData> MINECRAFT_SENDS_ITEMS = List.of(
      CpxVillagerData.getPvpNone(
         "CrystalPvpBot", "CPX-01", "Fights crystal PvP for you, start to finish.", "cpx01", "minecraft:end_crystal", "minecraft:totem_of_undying"
      ),
      CpxVillagerData.getPvpNone("SwordPvpBot", "SBX-02", "Sword duels with perfect W-taps and strafes.", "sbx02", "minecraft:diamond_sword").getHandTagline(),
      CpxVillagerData.getCpxPvp("SmpBot", "SMP Bot", "MCX-03", "Mace fights for SMP servers.", "minecraft:mace", "minecraft:totem_of_undying"),
      CpxVillagerData.getCpxPvp(
            "NethPotBot", "NethPot Bot", "NPX-04", "Netherite pot PvP with splash heals.", "minecraft:netherite_sword", "minecraft:splash_potion"
         )
         .getHandTagline("minecraft:splash_potion", "minecraft:strong_healing"),
      CpxVillagerData.getUtilityNone(
         "AutoBuilder", "BLD-01", "Builds a schematic for you, block by block.", "bld01", "minecraft:scaffolding", "minecraft:scaffolding"
      ),
      CpxVillagerData.getUtilityNone(
            "AutoAreaMiner", "MIN-02", "Mines out an area you pick.", "min02", "minecraft:netherite_pickaxe", "minecraft:netherite_pickaxe"
         )
         .getHandTagline(),
      CpxVillagerData.getUtilityNone("AutoElytraFinder", "ELY-03", "Flies the End and brings home elytra.", "minecraft:elytra").getHandTagline(),
      CpxVillagerData.getUtilityNone(
         "AutoElytraPilot", "PLT-09", "Flies you to your coords on an elytra.", "plt09", "minecraft:firework_rocket", "minecraft:firework_rocket"
      ),
      CpxVillagerData.getVanillaUtility("VillagerTradeBot", "VIL-04", "Trades with villagers to hit your targets.", "minecraft:enchanted_book")
         .getHandTagline(),
      CpxVillagerData.getUtilityNone("AutoAuctionFlipper", "AHF-05", "Buys low on the auction house, sells high.", "minecraft:bundle"),
      CpxVillagerData.getUtilityNone("SpawnerProtect", "SPN-06", "Saves your spawners when a player shows up.", "minecraft:spawner"),
      CpxVillagerData.getUtilityNone(
         "RtpBaseAlert", "RTP-07", "Teleports around and alerts you to bases.", "rtp07", "minecraft:recovery_compass", "minecraft:recovery_compass"
      ),
      CpxVillagerData.getUtilityNone(
         "AnchorHunter", "ANC-10", "RTPs around and mines respawn anchors for you.", "anc10", "minecraft:respawn_anchor", "minecraft:diamond_pickaxe"
      ),
      CpxVillagerData.getPullUtility("AutoTpaHere", "TPA-08", "Sends /tpahere to random players from your trap.", "tpa08", "minecraft:ender_pearl")
   );
   public static final String BOT = "BOT-00";

   public static List<Module> getItems(List<Module> list) {
      var arrayList = new ArrayList(list);
      arrayList.sort(Comparator.<Module>comparingInt(Module -> AutoYourUtils.isContainsBOT_PVP_ITEMS(Module) ? 0 : 1).thenComparingInt(AutoYourUtils::getIndexOf));
      return arrayList;
   }

   private MinecraftBotUtils() {
   }

   public static CpxVillagerData getBotIdentifier(Module Module) {
      Kind kind = AutoYourUtils.isContainsBOT_PVP_ITEMS(Module) ? Kind.PVP : Kind.UTILITY;
      return new CpxVillagerData(
         formatText(Module), kind, null, "BOT-00", "", identifier, identifier, null, null, null, null, false, null, null, false, false, false, Act.NONE
      );
   }

   static {
      for (CpxVillagerData cpxVillagerData : MINECRAFT_SENDS_ITEMS) {
         MINECRAFT_ENDER_MAP.put(cpxVillagerData.id(), cpxVillagerData);
         if (cpxVillagerData.planned()) {
            MINECRAFT_RESPAWN_ITEMS.add(cpxVillagerData);
         }
      }
   }

   public static List<CpxVillagerData> getCopyOf() {
      return List.copyOf(MINECRAFT_RESPAWN_ITEMS);
   }

   public static MinecraftBotUtils.Rail getDataForList(List<Module> list) {
      return getPvpKind(list, isFlag());
   }

   public static boolean canEnderMinecraft(Module Module) {
      CpxVillagerData minecraft_ender_map = MINECRAFT_ENDER_MAP.get(formatText(Module));
      return minecraft_ender_map != null && !minecraft_ender_map.planned();
   }

   public static CpxVillagerData getEnderMinecraft(String enderMinecraftText) {
      return enderMinecraftText == null ? null : MINECRAFT_ENDER_MAP.get(enderMinecraftText);
   }

   public static boolean isFlag() {
      return false;
   }

   public static String formatText(Module Module) {
      return Module.getName();
   }

   public static MinecraftBotUtils.Rail getPvpKind(List<Module> list, boolean pvpKindFlag) {
      var items2 = getItems(list);
      var arrayList = new ArrayList();
      var hashSet = new HashSet();
      int pvpKindCounter = 0;
      int pvpKindCounter2 = 0;

      for (Module Module : items2) {
         hashSet.add(formatText(Module));
         if (getMinecraftRespawn(Module).kind() == Kind.PVP) {
            arrayList.add(new MinecraftBotUtils.Slot(getMinecraftRespawn(Module), Module));
            pvpKindCounter++;
         }
      }

      if (pvpKindFlag) {
         for (CpxVillagerData cpxVillagerData : MINECRAFT_RESPAWN_ITEMS) {
            if (!hashSet.contains(cpxVillagerData.id())) {
               arrayList.add(new MinecraftBotUtils.Slot(cpxVillagerData, null));
               pvpKindCounter++;
            }
         }
      }

      for (Module ilIllIiIll3 : items2) {
         if (getMinecraftRespawn(ilIllIiIll3).kind() != Kind.PVP) {
            arrayList.add(new MinecraftBotUtils.Slot(getMinecraftRespawn(ilIllIiIll3), ilIllIiIll3));
            pvpKindCounter2++;
         }
      }

      return new MinecraftBotUtils.Rail(List.copyOf(arrayList), pvpKindCounter, pvpKindCounter2);
   }

   public static CpxVillagerData getMinecraftRespawn(Module Module) {
      String botCrystalText = formatText(Module);
      CpxVillagerData minecraft_ender_map = MINECRAFT_ENDER_MAP.get(botCrystalText);
      if (minecraft_ender_map != null && !minecraft_ender_map.planned()) {
         return minecraft_ender_map;
      } else {
         CpxVillagerData minecraft_respawn_map = MINECRAFT_RESPAWN_MAP.get(botCrystalText);
         if (minecraft_respawn_map == null) {
            minecraft_respawn_map = getBotIdentifier(Module);
            MINECRAFT_RESPAWN_MAP.put(botCrystalText, minecraft_respawn_map);
         }

         return minecraft_respawn_map;
      }
   }

   public static List<CpxVillagerData> getSendsMinecraft() {
      return MINECRAFT_SENDS_ITEMS;
   }

   public record Rail(List<MinecraftBotUtils.Slot> slots, int pvp, int util) {
      public MinecraftBotUtils.Slot getSlots(int i) {
         return i >= 0 && i < this.slots.size() ? this.slots.get(i) : null;
      }

      public int getSlots(String id) {
         if (id == null) {
            return -1;
         } else {
            for (int i = 0; i < this.slots.size(); i++) {
               if (this.slots.get(i).formatLook().equals(id)) {
                  return i;
               }
            }

            return -1;
         }
      }

      public int getSlots() {
         return this.slots.size();
      }

      public int getSlotsModule(Module m) {
         if (m == null) {
            return -1;
         } else {
            for (int i = 0; i < this.slots.size(); i++) {
               if (this.slots.get(i).module() == m) {
                  return i;
               }
            }

            return -1;
         }
      }
   }

   public record Slot(CpxVillagerData look, Module module) {
      public String formatLook() {
         return this.look.id();
      }

      public boolean isModule() {
         return this.module == null;
      }
   }
}
