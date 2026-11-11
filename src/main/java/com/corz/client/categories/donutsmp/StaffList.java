package com.corz.client.categories.donutsmp;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.utils.StaffBadge.Hit;
import com.corz.client.utils.StaffBadge.Run;
import com.corz.client.utils.StaffRoster.Member;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;
import com.mojang.serialization.JsonOps;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.CharacterVisitor;
import net.minecraft.text.Style;
import net.minecraft.text.StyleSpriteSource;
import net.minecraft.text.StyleSpriteSource.Font;
import net.minecraft.text.StyleSpriteSource.Sprite;
import net.minecraft.text.Text;
import net.minecraft.text.TextCodecs;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.utils.IconsPngType;
import com.corz.client.utils.StaffBadge;
import com.corz.client.utils.StaffRoster;

public class StaffList extends Module {
   private long nanoTime;
   private final StaffList.Collector collector;
   private int value;
   private final HashMap<UUID, StaffList.Slot> showDonutMap;
   private final StaffRoster staffRoster;
   private final BooleanSetting booleanBase = new BooleanSetting("DonutSMP Only", "Only show while connected to a DonutSMP server.", true);
   private ClientPlayNetworkHandler networkHandler;
   private static String[] texts;
   private static final String DONUTSMP = "donutsmp";
   public static final String[] cryptoDaveTexts = new String[]{
      "archivePedro",
      "DrDonutt",
      "Munkerlich",
      "FluffyMaster07",
      "showered",
      "Fallerfly",
      "W1zoX_",
      "bloodspulse",
      "Pastagamer08",
      "0Gsummer",
      "LzouZMp5",
      "Frenk_Btw",
      "Napooo_",
      "Splaterd",
      "CaptainMoose35",
      "u_vv",
      "CryptoDaveYT"
   };
   private boolean flag;
   private static final Map<String, UUID> CLEAR_CRYPTO_MAP = new LinkedHashMap<>();
   private static boolean donutsmpFlag;
   private static String address;
   private static StaffList singleton;
   private final BooleanSetting booleanBase2 = new BooleanSetting("Show Count", "Show the online-staff count in the panel header.", true);

   public int getOnlineStaffCount() {
      return this.staffRoster.getMemberCount();
   }

   public static boolean isStaffPlayer(UUID uuid, String daveCryptoText) {
      StaffList singleton2 = singleton;
      if (singleton2 != null) {
         if (!singleton2.isEnabled()) {
            long nanoTime = System.nanoTime();
            if (singleton2.nanoTime == 0L || nanoTime - singleton2.nanoTime >= 250000000L) {
               singleton2.nanoTime = nanoTime;
               singleton2.refreshRoster();
            }
         }

         var copyOf = singleton2.staffRoster.getCopyOf();
         if (!copyOf.isEmpty()) {
            for (int i = 0; i < copyOf.size(); i++) {
               Member member = (Member)copyOf.get(i);
               if (uuid != null && uuid.equals(member.uUID) || daveCryptoText != null && daveCryptoText.equalsIgnoreCase(member.memberText)) {
                  return true;
               }
            }

            return false;
         }
      }

      if (daveCryptoText != null && !daveCryptoText.isEmpty()) {
         String[] texts2 = texts;
         if (texts2 == null) {
            texts2 = new String[cryptoDaveTexts.length];

            for (int j = 0; j < cryptoDaveTexts.length; j++) {
               texts2[j] = cryptoDaveTexts[j].trim();
            }

            texts = texts2;
         }

         for (String daveCryptoText2 : texts2) {
            if (!daveCryptoText2.isEmpty() && daveCryptoText2.equalsIgnoreCase(daveCryptoText)) {
               return true;
            }
         }

         return false;
      } else {
         return false;
      }
   }

   @Override
   protected void onEnable() {
      this.nanoTime = 0L;
      this.refreshRoster();
   }

   public static boolean isDonutsmpAddress() {
      try {
         ServerInfo currentServerEntry = MinecraftClient.getInstance().getCurrentServerEntry();
         String staffText = currentServerEntry == null ? null : currentServerEntry.address;
         if (staffText == null) {
            return false;
         } else {
            if (staffText != address) {
               address = staffText;
               donutsmpFlag = containsIgnoreCase(staffText, "donutsmp");
            }

            return donutsmpFlag;
         }
      } catch (Throwable e) {
         return false;
      }
   }

   public boolean isDonutsmpOnly() {
      return this.booleanBase.isEnabled();
   }

   private void refreshRoster() {
      if (!this.flag) {
         try {
            this.scanPlayerList();
         } catch (Throwable e) {
            this.flag = true;
            this.clearRoster();
         }
      }
   }

   @Override
   protected void onDisable() {
      this.clearRoster();
   }

   private void clearRoster() {
      this.showDonutMap.clear();
      this.networkHandler = null;
      this.staffRoster.updateFlag();
   }

   public List<Member> getStaffMembers() {
      return this.staffRoster.getCopyOf();
   }

   public StaffList() {
      super("Staff List", IconsPngType.DONUTSMP);
      this.staffRoster = new StaffRoster();
      this.showDonutMap = new HashMap<>();
      this.collector = new StaffList.Collector();
      this.addSettings(new ConfigSetting[]{this.booleanBase, this.booleanBase2});
      singleton = this;
   }

   public static void applyDevTabCommand(String cryptoText) {
      ClientPlayNetworkHandler networkHandler = MinecraftClient.getInstance().getNetworkHandler();
      if (networkHandler != null) {
         String cryptoText2 = cryptoText == null ? "" : cryptoText.trim();

         try {
            Field declaredField = ClientPlayNetworkHandler.class.getDeclaredField("playerListEntries");
            Field declaredField2 = ClientPlayNetworkHandler.class.getDeclaredField("listedPlayerListEntries");
            declaredField.setAccessible(true);
            declaredField2.setAccessible(true);
            var map = (Map)declaredField.get(networkHandler);
            var set = (Set)declaredField2.get(networkHandler);
            if (cryptoText2.isEmpty() || cryptoText2.equalsIgnoreCase("clear")) {
               for (UUID uuid : CLEAR_CRYPTO_MAP.values()) {
                  set.remove(map.remove(uuid));
               }

               CLEAR_CRYPTO_MAP.clear();
            } else if (cryptoText2.startsWith("-")) {
               UUID clear_crypto_map = CLEAR_CRYPTO_MAP.remove(cryptoText2.substring(1).trim());
               if (clear_crypto_map != null) {
                  set.remove(map.remove(clear_crypto_map));
               }
            } else {
               int indexOf = cryptoText2.indexOf(32);
               String botCrystalText = indexOf < 0 ? cryptoText2 : cryptoText2.substring(0, indexOf);
               String cryptoText4 = indexOf < 0 ? botCrystalText : cryptoText2.substring(indexOf + 1).trim();
               UUID nameUUIDFromBytes = UUID.nameUUIDFromBytes(("corz-devtab:" + botCrystalText).getBytes(StandardCharsets.UTF_8));
               PlayerListEntry playerListEntry = (PlayerListEntry)map.get(nameUUIDFromBytes);
               if (playerListEntry == null) {
                  playerListEntry = new PlayerListEntry(new GameProfile(nameUUIDFromBytes, botCrystalText), false);
                  map.put(nameUUIDFromBytes, playerListEntry);
                  set.add(playerListEntry);
               }

               playerListEntry.setDisplayName(
                  (Text)(!cryptoText4.startsWith("{") && !cryptoText4.startsWith("[")
                     ? Text.literal(cryptoText4.replace('&', '\u00a7'))
                     : (Text)TextCodecs.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(cryptoText4)).getOrThrow())
               );
               CLEAR_CRYPTO_MAP.put(botCrystalText, nameUUIDFromBytes);
            }
         } catch (Throwable e) {
         }

         StaffList staffList = (StaffList)ModuleRegistry.getModule(StaffList.class);
         if (staffList != null && staffList.isEnabled()) {
            staffList.nanoTime = 0L;
            staffList.refreshRoster();
         }
      }
   }

   @Override
   protected void onReset() {
      long nanoTime = System.nanoTime();
      if (this.nanoTime == 0L || nanoTime - this.nanoTime >= 250000000L) {
         this.nanoTime = nanoTime;
         this.refreshRoster();
      }
   }

   public boolean isShowCount() {
      return this.booleanBase2.isEnabled();
   }

   private void scanPlayerList() {
      ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
      if (networkHandler != null && mc.world != null && (!this.booleanBase.isEnabled() || isDonutsmpAddress())) {
         if (networkHandler != this.networkHandler) {
            this.showDonutMap.clear();
            this.networkHandler = networkHandler;
         }

         int value = ++this.value;
         int donutShowCounter = 128;
         int donutShowCounter2 = 0;
         this.staffRoster.update();

         for (PlayerListEntry playerListEntry : networkHandler.getListedPlayerListEntries()) {
            GameProfile profile = playerListEntry.getProfile();
            if (profile != null && profile.id() != null) {
               UUID id = profile.id();
               StaffList.Slot showdonutmap = this.showDonutMap.get(id);
               if (showdonutmap == null) {
                  showdonutmap = new StaffList.Slot();
                  this.showDonutMap.put(id, showdonutmap);
               }

               if (showdonutmap.scanGeneration != value) {
                  donutShowCounter2++;
               }

               showdonutmap.scanGeneration = value;
               if (!showdonutmap.isCacheCurrent(playerListEntry, profile) && donutShowCounter > 0) {
                  donutShowCounter--;
                  showdonutmap.cacheEntry(playerListEntry, profile);
                  showdonutmap.hit = this.detectStaffHit(playerListEntry, profile);
               }

               Hit hit = showdonutmap.hit;
               if (hit != null) {
                  this.staffRoster.handleMemberFlag(hit.name(), id, hit.tier(), hit.rgb());
               }
            }
         }

         if (donutShowCounter2 != this.showDonutMap.size()) {
            this.showDonutMap.values().removeIf(slot -> slot.scanGeneration != value);
         }

         this.staffRoster.isFlag();
      } else {
         this.clearRoster();
      }
   }

   private static boolean containsIgnoreCase(String staffText, String staffText2) {
      int staffValue = staffText2.length();

      for (int i = 0; i + staffValue <= staffText.length(); i++) {
         if (staffText.regionMatches(true, i, staffText2, 0, staffValue)) {
            return true;
         }
      }

      return false;
   }

   public List<String> getStaffNames() {
      return this.staffRoster.getItems();
   }

   private Hit matchBadgeFromText(Text text, String collectorOrderedText) {
      this.collector.reset();
      text.asOrderedText().accept(this.collector);
      this.collector.flushRun();
      return StaffBadge.getText(this.collector.runs, collectorOrderedText);
   }

   private Hit detectStaffHit(PlayerListEntry playerListEntry, GameProfile gameProfile) {
      String name = gameProfile.name();
      Team scoreboardTeam = playerListEntry.getScoreboardTeam();
      Text displayName = playerListEntry.getDisplayName();
      Object obj = displayName != null ? displayName : Team.decorateName(scoreboardTeam, Text.literal(name == null ? "" : name));
      return StaffBadge.getPurpleGreen(this.matchBadgeFromText((Text)obj, name), StaffBadge.getValue(scoreboardTeam == null ? null : scoreboardTeam.getName(), name), name);
   }

   private static final class Collector implements CharacterVisitor {
      private String fontId;
      final ArrayList<Run> runs = new ArrayList<>();
      private StyleSpriteSource currentFont;
      private int currentColor;
      private final StringBuilder pendingText = new StringBuilder(48);

      void flushRun() {
         if (this.pendingText.length() != 0) {
            this.runs.add(new Run(this.pendingText.toString(), this.currentColor, this.fontId));
            this.pendingText.setLength(0);
         }
      }

      @Override
      public boolean accept(int acceptValue, Style style, int acceptValue2) {
         TextColor color = style.getColor();
         int value = color == null ? -1 : color.getRgb() & 16777215;
         StyleSpriteSource font = style.getFont();
         if (this.pendingText.length() > 0 && (value != this.currentColor || !font.equals(this.currentFont))) {
            this.flushRun();
         }

         if (this.pendingText.length() == 0) {
            this.currentColor = value;
            if (!font.equals(this.currentFont)) {
               this.currentFont = font;
               this.fontId = getFontId(font);
            }
         }

         this.pendingText.appendCodePoint(acceptValue2);
         return true;
      }

      private Collector() {
         this.currentColor = -1;
      }

      private static String getFontId(StyleSpriteSource styleSpriteSource) {
         if (styleSpriteSource instanceof Font font) {
            return font.id().toString();
         } else {
            return styleSpriteSource instanceof Sprite sprite ? "sprite:" + sprite.atlasId() + "/" + sprite.spriteId() : "player:";
         }
      }

      void reset() {
         this.runs.clear();
         this.pendingText.setLength(0);
         this.currentColor = -1;
         this.currentFont = null;
         this.fontId = null;
      }
   }

   private static final class Slot {
      Text displayName;
      Text cachedSuffix;
      boolean cacheValid;
      Text cachedPrefix;
      int scanGeneration;
      Hit hit;
      String name;
      Team scoreboardTeam;
      Formatting formatting;

      void cacheEntry(PlayerListEntry playerListEntry, GameProfile gameProfile) {
         this.cacheValid = true;
         this.displayName = playerListEntry.getDisplayName();
         this.name = gameProfile.name();
         Team scoreboardTeam = playerListEntry.getScoreboardTeam();
         this.scoreboardTeam = scoreboardTeam;
         boolean displayScoreboardFlag = scoreboardTeam != null && this.displayName == null;
         this.cachedPrefix = displayScoreboardFlag ? scoreboardTeam.getPrefix() : null;
         this.cachedSuffix = displayScoreboardFlag ? scoreboardTeam.getSuffix() : null;
         this.formatting = displayScoreboardFlag ? scoreboardTeam.getColor() : null;
      }

      boolean isCacheCurrent(PlayerListEntry playerListEntry, GameProfile gameProfile) {
         if (this.cacheValid && playerListEntry.getDisplayName() == this.displayName && gameProfile.name() == this.name) {
            Team scoreboardTeam = playerListEntry.getScoreboardTeam();
            return scoreboardTeam != this.scoreboardTeam
               ? false
               : scoreboardTeam == null
                  || this.displayName != null
                  || scoreboardTeam.getPrefix() == this.cachedPrefix && scoreboardTeam.getSuffix() == this.cachedSuffix && scoreboardTeam.getColor() == this.formatting;
         } else {
            return false;
         }
      }
   }
}
