package com.corz.client.utils;

import com.corz.client.config.ModuleIdentities;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.corz.client.categories.bots.AutoAreaMiner;
import ai.auth.Module;

public final class AutoYourUtils {
   private static final Map<String, String> YOUR_YOU_MAP = new HashMap<>();
   private static final Set<String> BOT_PVP_SET = Set.of("AutoBuilder", "AutoAreaMiner", "AutoElytraFinder", "AutoElytraPilot", "Bloodhound");
   private static final Map<String, String> UPDATE_AVAILABLE_MAP = Map.of("Bloodhound", "Available next update.", "CorzRadio", "Available next update.");
   public static final List<String> UPDATE_AUTO_ITEMS = List.of(
      "AutoBuilder",
      "AutoAreaMiner",
      "AutoElytraFinder",
      "AutoElytraPilot",
      "VillagerTradeBot",
      "AutoAuctionFlipper",
      "SpawnerProtect",
      "RtpBaseAlert",
      "AnchorHunter",
      "AutoTpaHere"
   );
   public static final List<String> BOT_PVP_ITEMS = List.of("CrystalPvpBot", "SwordPvpBot");
   private static final Map<String, String> BOT_PVP_MAP = Map.of(
      "AutoBuilder",
      "Gen3 BETA.",
      "CrystalPvpBot",
      "Crystal PvP Bot beta. It keeps getting smarter every update.",
      "SwordPvpBot",
      "Sword PvP Bot beta. It keeps getting smarter every update."
   );
   private static final Set<String> AUTO_PVP_SET = Set.of(
      "Reach",
      "AutoTotem",
      "HitBoxes",
      "AutoTrap",
      "AutoHitCrystal",
      "CrystalOptimizer",
      "CrystalAura",
      "Scaffold",
      "Blink",
      "FakeLag",
      "BackTrack",
      "KnockbackDelay",
      "Criticals",
      "SpeedHack",
      "Flight",
      "AutoCart",
      "Fastbreak",
      "AutoPromo",
      "AutoPot",
      "AutoAggroPearl",
      "BoatFly",
      "CrystalPvpBot",
      "SwordPvpBot"
   );
   private static final Set<String> PVP_CRYSTAL_SET = new HashSet<>();
   private static final Set<String> CRYSTAL_BLOCKED_MODULES = Set.of("CrystalPvpBot", "CrystalAura", "Scaffold");

   private AutoYourUtils() {
   }

   public static boolean isContains(Module Module) {
      return AUTO_PVP_SET.contains(Module.getName());
   }

   public static boolean canUpdatePvp(Module Module) {
      String pvpText = Module.getName();
      return BOT_PVP_ITEMS.contains(pvpText) || UPDATE_AUTO_ITEMS.contains(pvpText);
   }

   public static String getOrDefault(Module Module) {
      return YOUR_YOU_MAP.getOrDefault(Module.getName(), "");
   }

   public static String getOrDefaultForIlIllIiIll(Module Module) {
      return UPDATE_AVAILABLE_MAP.getOrDefault(Module.getName(), "");
   }

   public static boolean isContainsKey(Module Module) {
      return UPDATE_AVAILABLE_MAP.containsKey(Module.getName());
   }

   static {
      YOUR_YOU_MAP.put("AnchorMacro", "Respawn-anchor macro: charges with glowstone and detonates.");
      YOUR_YOU_MAP.put("SafeAnchor", "Anchor combo that walls you off before detonating.");
      YOUR_YOU_MAP.put("CrystalAura", "Places and breaks end crystals for you: hold right-click with a crystal (Manual) or let it pick nearby players (Auto).");
      YOUR_YOU_MAP.put("CrystalOptimizer", "Tunes crystal placement and target selection.");
      YOUR_YOU_MAP.put("AutoTotem", "Swaps a totem into your off-hand when it pops.");
      YOUR_YOU_MAP.put("AutoTrap", "Traps a target by placing blocks around them.");
      YOUR_YOU_MAP.put("AimAssist", "Gently pulls your aim toward the target nearest your crosshair, in small mouse-like steps.");
      YOUR_YOU_MAP.put("TriggerBot", "Attacks the target under your crosshair as soon as your weapon is charged.");
      YOUR_YOU_MAP.put(
         "HitSelect", "Holds back clicks that would be wasted while your target can't take damage yet, so your real hits land at the best moment."
      );
      YOUR_YOU_MAP.put("Reach", "Lets you hit from a little farther than normal. Risky: most anticheats detect extra reach.");
      YOUR_YOU_MAP.put("AutoClicker", "Auto-clicks at a configurable CPS.");
      YOUR_YOU_MAP.put("AutoMine", "Automatically mines the targeted block.");
      YOUR_YOU_MAP.put("FastPlace", "Removes the block-placement delay.");
      YOUR_YOU_MAP.put("FakePlayer", "Spawns a fake player for testing.");
      YOUR_YOU_MAP.put("KeepSprint", "Keeps you sprinting through hits.");
      YOUR_YOU_MAP.put("AutoWalk", "Walks forward automatically.");
      YOUR_YOU_MAP.put("AutoXP", "Throws XP bottles from your hotbar to repair Mending gear.");
      YOUR_YOU_MAP.put("AutoHotbar", "Puts your items into the hotbar slots you choose and refills them when they run out.");
      YOUR_YOU_MAP.put(
         "InventoryCleaner",
         "Throws junk and worse copies of your gear out of your inventory. Keeps totems, elytra, shulkers, renamed items and differently enchanted gear."
      );
      YOUR_YOU_MAP.put("AutoInventoryTotem", "Silently keeps a totem in your off-hand and an optional hotbar reserve.");
      YOUR_YOU_MAP.put(
         "AutoShieldBreaker", "When the player you hit is blocking, that hit uses an axe from your hotbar to disable the shield, then swaps back."
      );
      YOUR_YOU_MAP.put("AutoHitCrystal", "Attacks end crystals in range to detonate them.");
      YOUR_YOU_MAP.put("HitBoxes", "Enlarges entity hitboxes so attacks land more easily.");
      YOUR_YOU_MAP.put(
         "Scaffold",
         "Helps you bridge. Legit crouches for you at block edges while you walk backwards. GodBridge takes over after a few blocks while you hold S; move the mouse or let go of S to take back control."
      );
      YOUR_YOU_MAP.put("ObsidianOptimizer", "Pre-places obsidian at strong crystal spots near a target.");
      YOUR_YOU_MAP.put("AutoSpear", "Jump and lunge with your spear instantly, cooldown-bypassed, then swap back. One-shot per key tap.");
      YOUR_YOU_MAP.put("PerfectWindCharge", "Self-launches with a wind charge for the mace combo.");
      YOUR_YOU_MAP.put(
         "TridentAssist",
         "Perfect trident swings: every hit lands fully charged (and as a crit when you jump), and throws or riptides let go on the exact full-charge tick."
      );
      YOUR_YOU_MAP.put("AutoAggroPearl", "Follows an enemy's pearl by throwing yours to its landing.");
      YOUR_YOU_MAP.put("PearlPrediction", "Draws a pearl's flight path and the thrower's skin where it will land.");
      YOUR_YOU_MAP.put("PearlCatch", "Alerts when an enemy pearl is about to land near you.");
      YOUR_YOU_MAP.put("HeatColor", "Tints entities by health, green (full) to red (low).");
      YOUR_YOU_MAP.put("Freelook", "Look around freely without turning your player.");
      YOUR_YOU_MAP.put("TotemCounter", "Shows how many totems you have on the HUD.");
      YOUR_YOU_MAP.put("PlayerESP", "Highlights players through walls.");
      YOUR_YOU_MAP.put("StorageESP", "Highlights chests and storage through walls.");
      YOUR_YOU_MAP.put("MobESP", "Highlights mobs through walls: hostile mobs red, passive mobs green.");
      YOUR_YOU_MAP.put("MotionBlur", "Smooth motion blur when you move or turn. Your HUD and menus stay sharp.");
      YOUR_YOU_MAP.put(
         "FakeCoordinates",
         "Shows fake coordinates on F3, the HUD and client chat so clips and screenshares never leak where you are. Nothing sent to the server changes."
      );
      YOUR_YOU_MAP.put("HoleESP", "Highlights sealed, hidden player tunnels and shafts underground. Ignores caves, dungeons and mineshafts.");
      YOUR_YOU_MAP.put(
         "SpawnerFinder", "Finds mob spawners in every loaded chunk, underground to Y -64, and boxes them through walls (remembers unloaded ones)."
      );
      YOUR_YOU_MAP.put("LootESP", "Highlights dropped items.");
      YOUR_YOU_MAP.put("NameTags", "Improved entity name tags.");
      YOUR_YOU_MAP.put("BlockOverlay", "Premium shader-style highlight (fill, outline, glow, rainbow) on the block you look at.");
      YOUR_YOU_MAP.put("Fullbright", "Brightens the world like night vision.");
      YOUR_YOU_MAP.put("Freecam", "Detached camera (WASD = fly). Body can keep walking; mining uses real aim.");
      YOUR_YOU_MAP.put("NoExplosions", "Hides explosion particles and effects.");
      YOUR_YOU_MAP.put("NoFlame", "Hides the fire overlay on entities.");
      YOUR_YOU_MAP.put("NoFarmLag", "Reduces lag around farms.");
      YOUR_YOU_MAP.put("ChunkFinder", "Finds player bases, stashes, spawners, farms and redstone in the chunks around you.");
      YOUR_YOU_MAP.put(
         "Bloodhound",
         "Finds intact player bases hidden below deepslate level: double chests, shulker boxes, furnished rooms, spawner farms and trading halls. Skips griefed and flooded bases. Follow the pins, or let it hunt with /rtp or elytra."
      );
      YOUR_YOU_MAP.put("WanderingTraderFinder", "Locates wandering traders.");
      YOUR_YOU_MAP.put("SeedAnalyzer", "Analyzes structures from the world seed.");
      YOUR_YOU_MAP.put("TimeChanger", "Changes the client-side time of day.");
      YOUR_YOU_MAP.put("FakeStats", "Fake balance + editable leaderboard, wired to FakePay (DonutSMP).");
      YOUR_YOU_MAP.put("CustomCrosshair", "Draws a custom crosshair and hides the vanilla one.");
      YOUR_YOU_MAP.put("NameProtect", "Replaces your username with an alias everywhere the client draws it.");
      YOUR_YOU_MAP.put("Panic", "Instantly disables every active module. Bindable.");
      YOUR_YOU_MAP.put("SelfDestruct", "Deletes the client jar from your mods folder (two clicks to confirm).");
      YOUR_YOU_MAP.put("ShowHUD", "Client HUD overlay (FPS, coords, and more).");
      YOUR_YOU_MAP.put("SpotifyHUD", "Now-playing music card: album art, track, controls and a progress bar.");
      YOUR_YOU_MAP.put("ThemeCustomizer", "Customize the client's colors and theme.");
      YOUR_YOU_MAP.put("GUISettings", "ClickGUI appearance and behaviour.");
      YOUR_YOU_MAP.put("AutoSell", "Auto-sells items (DonutSMP).");
      YOUR_YOU_MAP.put(
         "AutoSellChest",
         "Look at a chest or barrel and turn it on: it empties the chest into your inventory, sells it with /sell and repeats until the chest is empty. Never sells the items you already carried (DonutSMP)."
      );
      YOUR_YOU_MAP.put(
         "SilentHome",
         "Sets a home where you stand, /rtps away, then /homes you back once you land, hiding the chat spam. Stand still until it says you are back (DonutSMP)."
      );
      YOUR_YOU_MAP.put("Relog", "Press its key to leave the server and join it again straight away. Does nothing in singleplayer.");
      YOUR_YOU_MAP.put("AutoPromo", "Runs the promo command automatically (DonutSMP).");
      YOUR_YOU_MAP.put("AutoBuilder", "Builds a loaded schematic automatically, block by block.");
      YOUR_YOU_MAP.put(ModuleIdentities.getProperty(AutoAreaMiner.class), "Automatically mines a configured area.");
      YOUR_YOU_MAP.put("BlockESP", "Highlights configured blocks through walls.");
      YOUR_YOU_MAP.put("BlockHighlight", "Shader-style highlight (fill, outline, glow) on the block you look at.");
      YOUR_YOU_MAP.put("Capes", "Wears a custom cape on your player.");
      YOUR_YOU_MAP.put("Configs", "Save, load and manage client configurations.");
      YOUR_YOU_MAP.put("CorzRadio", "Staff playlists. Tune in to what the Corz team is listening to.");
      YOUR_YOU_MAP.put("CustomFlame", "Replaces the fire overlay with a custom procedural flame.");
      YOUR_YOU_MAP.put("CustomWorld", "Recolors the world and sky for a custom atmosphere.");
      YOUR_YOU_MAP.put("FireFly", "Ambient floating firefly particles drifting around you.");
      YOUR_YOU_MAP.put("Friends", "Manage saved players that combat modules ignore.");
      YOUR_YOU_MAP.put("HandCustomer", "Customizes first-person hand position and swing style.");
      YOUR_YOU_MAP.put("HandShader", "Applies a shader effect to your held item and hand.");
      YOUR_YOU_MAP.put("License", "View your Corz Client license status.");
      YOUR_YOU_MAP.put("PearlCatchAssist", "Throws a pearl then a wind charge along your aim to launch you up.");
      YOUR_YOU_MAP.put("CustomSky", "Custom skies: animated Northern Lights (aurora), Black Hole, Saturn, Nebula and more sky presets.");
      YOUR_YOU_MAP.put("RealisticWater", "Living water: rolling waves, sky reflections, sun and moon glints. Mirrors your custom sky too.");
      YOUR_YOU_MAP.put("SilentAim", "Aims at targets server-side without turning your camera.");
      YOUR_YOU_MAP.put("Criticals", "Turns your hits on players into critical hits.");
      YOUR_YOU_MAP.put("AutoPot", "Throws an Instant Health splash potion at your feet when your health drops.");
      YOUR_YOU_MAP.put("PotionRefill", "Shift-clicks splash potions into your hotbar while the inventory is open.");
      YOUR_YOU_MAP.put("KeyPearl", "One keybind swaps to an ender pearl, throws it, then swaps back.");
      YOUR_YOU_MAP.put("MiddleClickFriend", "Middle-click a player in the world to add or remove them as a friend.");
      YOUR_YOU_MAP.put("AutoJumpReset", "Jumps on the exact tick an enemy's hit knocks you back so you take less knockback.");
      YOUR_YOU_MAP.put("WTap", "Lets go of W for a moment after a sprint hit so your next hit deals full knockback again.");
      YOUR_YOU_MAP.put("Sprint", "Keeps you sprinting automatically while moving forward.");
      YOUR_YOU_MAP.put(
         "Blink", "Holds back everything you send until you turn it off, so the server still sees you where you started. Shows a ghost of you there."
      );
      YOUR_YOU_MAP.put(
         "FakeLag",
         "Delays the packets you send so you look laggy. Dynamic only lags while you walk toward the player you aim at; hitting sends everything first."
      );
      YOUR_YOU_MAP.put(
         "BackTrack",
         "Lets you keep hitting players and mobs that just ran away by showing their position a moment late while you fight them. A box shows where they really are."
      );
      YOUR_YOU_MAP.put("KnockbackDelay", "Holds incoming knockback for a moment when an enemy in front of you hits you.");
      YOUR_YOU_MAP.put("SpeedHack", "Moves you faster than vanilla sprint (strafe or bhop).");
      YOUR_YOU_MAP.put("Flight", "Fly at a custom speed \u2014 velocity mode (survival) or creative-ability mode.");
      YOUR_YOU_MAP.put("AutoTool", "Auto-selects the fastest correct tool for the block you're breaking.");
      YOUR_YOU_MAP.put("Fastbreak", "Breaks blocks faster by shortening the block-break delay.");
      YOUR_YOU_MAP.put("AutoCart", "Predicts your arrow's landing spot and places a rail with a TNT minecart there.");
      YOUR_YOU_MAP.put("TargetESP", "Draws a glowing marker over your current combat target.");
      YOUR_YOU_MAP.put("Waypoints", "Mark spots in the world and see them as a beam, icon and distance label.");
      YOUR_YOU_MAP.put("StaffList", "Lists online DonutSMP staff, detected live from the star badges in the tab list.");
      YOUR_YOU_MAP.put("AntiVanish", "Detects vanished staff and if you're being watched.");
      YOUR_YOU_MAP.put("SusChunkFinder", "Flags likely player bases from grown crops and rotated deepslate (DonutSMP).");
      YOUR_YOU_MAP.put("SpawnerProtect", "Safe AFK: panic-mines your spawners and logs off when a player approaches.");
      YOUR_YOU_MAP.put("SkinProtect", "Wears a random vanilla skin over your own player (client-side only).");
      YOUR_YOU_MAP.put("CustomSkin", "Turns you into a 3D Italian brainrot like Tung Tung Tung Sahur (client-side only).");
      YOUR_YOU_MAP.put("ClientSounds", "Plays sound blips on module toggles and menu hovers.");
      YOUR_YOU_MAP.put(
         "CreamyKeys", "Real recorded mechanical keyboard sounds (Mechvibes packs) when you type in chat, text boxes and menus (or on every key in game)."
      );
      YOUR_YOU_MAP.put("Notifications", "Pop-up toast notifications in the corner of the screen.");
      YOUR_YOU_MAP.put("Zoom", "Optical zoom on a key; scroll to change the factor.");
      YOUR_YOU_MAP.put("FOVSlider", "Overrides your field of view, with optional speed-widening and no sprint punch.");
      YOUR_YOU_MAP.put("RegionMap", "DonutSMP server region map on the HUD, with your current region highlighted.");
      YOUR_YOU_MAP.put("JumpCircles", "Stamps a glowing ring or a ripple of real blocks around you when you jump or land.");
      YOUR_YOU_MAP.put("Trails", "A smooth glowing ribbon, light streak or bead trail that follows you (and optionally other players).");
      YOUR_YOU_MAP.put("WeatherNotifier", "Themed toast when the weather changes.");
      YOUR_YOU_MAP.put("ACAlert", "Pops a toast from the top when the anticheat goes quiet in your region, and when it is back.");
      YOUR_YOU_MAP.put("WeatherChanger", "Forces the client-side weather: clear, rain or thunder.");
      YOUR_YOU_MAP.put("GamblingRigger", "Rig a dispenser/dropper gamble: keep one slot, pull the rest, then restore (DonutSMP).");
      YOUR_YOU_MAP.put("SusChunkFinderV2", "Weighted base finder: grown amethyst/plants flag chunks, merged into zones (DonutSMP).");
      YOUR_YOU_MAP.put("HitSwap", "Swaps to the best weapon for the hit you land (mace when you smash, axe on a raised shield), then swaps back.");
      YOUR_YOU_MAP.put("MaceBomber", "Lands a mace smash for you when you fall onto your target; Auto Stun Slam breaks a raised shield with an axe first.");
      YOUR_YOU_MAP.put(
         "AutoElytraPilot",
         "Elytra autopilot: type coords and it flies you there over mountains and builds, keeps the elytra mended, buys rockets and XP from /ah and logs off when it lands (DonutSMP)."
      );
      YOUR_YOU_MAP.put("AutoElytraFinder", "End-ship bot: flies The End, finds the elytra ship and collects and stores the elytra (DonutSMP).");
      YOUR_YOU_MAP.put("RtpBaseAlert", "DonutSMP base-hunt macro: /rtp across regions and alert when the finder flags a base.");
      YOUR_YOU_MAP.put(
         "AnchorHunter",
         "RTPs around the overworld, finds respawn anchors and mines them like a normal player. Leaves the moment another player shows up (DonutSMP)."
      );
      YOUR_YOU_MAP.put(
         "AutoTpaHere",
         "Stand in your trap with your home set there. It sends /tpahere to random online players, and when you die it waits, goes /home and keeps asking."
      );
      YOUR_YOU_MAP.put(
         "AutoAuctionFlipper", "Auction-house flipper: searches /ah for your items, snipes underpriced listings and re-lists them higher (DonutSMP)."
      );
      YOUR_YOU_MAP.put(
         "SpecialItemBuyer",
         "Finds the best-value drills, sell wands, shard axes and shard shovels on /ah by price per hour of self-destruct time left, and can buy the best one (DonutSMP)."
      );
      YOUR_YOU_MAP.put("BoatFly", "Turns the boat you are riding into a flying machine, with optional NoClip down through terrain.");
      YOUR_YOU_MAP.put("BoneDropper", "Bone-farm macro: drops every page of bones from a DonutSMP spawner's storage, or auto-fulfils /order bone orders.");
      YOUR_YOU_MAP.put("ChunkFinderV2", "Highlights amethyst and player chunks; turn on farms, redstone or deepslate if you want them too (DonutSMP).");
      YOUR_YOU_MAP.put("DeepPlayerFinder", "Reveals hidden underground players from the zeroed sky-light the anti-ESP leaves around them (DonutSMP).");
      YOUR_YOU_MAP.put("FakePay", "Intercepts /pay locally and prints a fake \"you paid\" line. Nothing is sent; no money moves (DonutSMP).");
      YOUR_YOU_MAP.put(
         "NetheriteFinder", "Finds the ancient debris your client knows about and draws the real block through walls, with an optional outline box."
      );
      YOUR_YOU_MAP.put("Particles", "Cosmetic hit sparks and a totem-pop fountain drawn on the client renderer.");
      YOUR_YOU_MAP.put("AtmosphericParticles", "Soft snow or rose petals drifting down through the open sky around you.");
      YOUR_YOU_MAP.put(
         "SwordInspect",
         "CS:GO-style weapon inspect: press the key and your held sword spins, flips or twirls first-person, including M9 Bayonet, Stiletto and Butterfly knife inspects."
      );
      YOUR_YOU_MAP.put(
         "VillagerTradeBot",
         "Autonomous villager trader: finds the right villager, buys enchanted books, XP bottles and other items or sells for emeralds, and earns emeralds when short (DonutSMP)."
      );
      YOUR_YOU_MAP.put(
         "SwordPvpBot", "Plays sword PvP for you: keeps just out of reach, hits first with perfect W-taps, strafes and jump-resets using only vanilla moves."
      );
      YOUR_YOU_MAP.put(
         "CrystalPvpBot", "Plays crystal PvP for you: picks a target, moves, aims and fights with crystals, anchors and totems using only vanilla moves."
      );
   }

   public static boolean isContainsForIlIllIiIll(Module Module) {
      return BOT_PVP_SET.contains(Module.getName());
   }

   public static void handlePvpCrystal(Module Module) {
      PVP_CRYSTAL_SET.add(Module.getName());
   }

   public static boolean isContainsKeyForIlIllIiIll(Module Module) {
      return BOT_PVP_MAP.containsKey(Module.getName());
   }

   public static boolean isContainsPVP_CRYSTAL_SET(Module Module) {
      return PVP_CRYSTAL_SET.contains(Module.getName());
   }

   public static boolean isContainsUPDATE_AUTO_ITEMS(Module Module) {
      return UPDATE_AUTO_ITEMS.contains(Module.getName());
   }

   public static boolean isContainsBOT_PVP_ITEMS(Module Module) {
      return BOT_PVP_ITEMS.contains(Module.getName());
   }

   public static boolean canPvpCrystal(Module Module) {
      return isContains(Module) && !CRYSTAL_BLOCKED_MODULES.contains(Module.getName());
   }

   public static String getOrDefaultBOT_PVP_MAP(Module Module) {
      return BOT_PVP_MAP.getOrDefault(Module.getName(), "");
   }

   public static int getIndexOf(Module Module) {
      String indexOfText = Module.getName();
      int indexOf = BOT_PVP_ITEMS.indexOf(indexOfText);
      if (indexOf < 0) {
         indexOf = UPDATE_AUTO_ITEMS.indexOf(indexOfText);
      }

      return indexOf < 0 ? Integer.MAX_VALUE : indexOf;
   }
}
