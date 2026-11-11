package com.corz.client.categories.donutsmp;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.config.StringBase;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import ai.auth.Module;
import com.corz.client.utils.TextMutableUtils;
import com.corz.client.utils.IconsPngType;

public class FakeStats extends Module {
   private final BooleanSetting balanceCommandEnabled;
   private final IntegerSetting shardsLine;
   private String lastMoneyInput;
   private String cachedBalanceText;
   private final StringBase deaths;
   private final IntegerSetting playtimeLine;
   private static final Pattern COLOR_CODE_PATTERN = Pattern.compile("(?i)\u00a7[0-9A-FK-OR]");
   private final IntegerSetting deathsLine;
   private final IntegerSetting killsLine;
   private static final String[] MONEY_KEYWORDS = new String[]{"money", "balance", "coins", "cash"};
   private int secondaryIndexCounter;
   private final BooleanSetting deductOnPay;
   private int primaryLineCounter;
   private double cachedBalance;
   private String lowercaseSource;
   private static final String[] SHARD_KEYWORDS = new String[]{"shard"};
   private static final String[] KILL_KEYWORDS = new String[]{"kill"};
   private static final String[] DEATH_KEYWORDS = new String[]{"death"};
   private final StringBase playtime;
   private final StringBase kills;
   private final StringBase shards;
   private final StringBase moneyInput = new StringBase("Money", "Your fake balance (1m, 67k, 250000). Wired to FakePay. Blank = off.", "1m");
   private final BooleanSetting sidebar;
   private final IntegerSetting moneyLine = new IntegerSetting("Money Line", "Which sidebar line is money (count from the top). 0 = auto by name.", 1, 0, 15);
   private String lowercaseResult;
   private double fakeBalance;
   private static final String[] PLAYTIME_KEYWORDS = new String[]{"playtime", "play time", "time played", "played"};

   private static String stripColorCodes(String replaceText) {
      return replaceText == null ? "" : (replaceText.indexOf(167) < 0 ? replaceText : COLOR_CODE_PATTERN.matcher(replaceText).replaceAll(""));
   }

   private String toLowerCaseCached(String fakeStatsText) {
      if (fakeStatsText != this.lowercaseSource) {
         this.lowercaseResult = fakeStatsText.toLowerCase(Locale.ROOT);
         this.lowercaseSource = fakeStatsText;
      }

      return this.lowercaseResult;
   }

   private void refreshBalanceFromInput() {
      String text = this.moneyInput.getCurrentText();
      if (!Objects.equals(text, this.lastMoneyInput)) {
         this.lastMoneyInput = text;
         double parseDouble = TextMutableUtils.parseAbbreviatedNumber(text);
         this.fakeBalance = Double.isNaN(parseDouble) ? 0.0 : Math.max(0.0, parseDouble);
      }
   }

   public double getFakeBalance() {
      this.refreshBalanceFromInput();
      return this.fakeBalance;
   }

   private String getFormattedBalance() {
      double amount = this.getFakeBalance();
      if (this.cachedBalanceText == null || Double.compare(amount, this.cachedBalance) != 0) {
         this.cachedBalanceText = TextMutableUtils.formatAbbreviatedNumber(amount);
         this.cachedBalance = amount;
      }

      return this.cachedBalanceText;
   }

   public boolean interceptBalanceCommand(String balBalanceText) {
      if (this.isEnabled() && this.balanceCommandEnabled.isEnabled() && mc.player != null) {
         String[] split = balBalanceText.trim().split("\\s+");
         if (split.length == 0) {
            return false;
         } else {
            String substring2 = split[0];
            if (substring2.startsWith("/")) {
               substring2 = substring2.substring(1);
            }

            if (!substring2.equalsIgnoreCase("bal") && !substring2.equalsIgnoreCase("balance")) {
               return false;
            } else if (split.length >= 2 && !split[1].equalsIgnoreCase(mc.player.getName().getString())) {
               return false;
            } else {
               String formatTexts = TextMutableUtils.formatAbbreviatedNumber(this.getFakeBalance());
               MutableText append = Text.empty()
                  .append(Text.literal("You have ").formatted(Formatting.GRAY))
                  .append(Text.literal(formatTexts).formatted(Formatting.GREEN));
               mc.player.sendMessage(append, false);
               mc.player.sendMessage(append, true);
               return true;
            }
         }
      } else {
         return false;
      }
   }

   private Text rewriteSidebarLine(Text base, boolean fakeStatsFlag) {
      if (this.isEnabled() && this.sidebar.isEnabled()) {
         String replaceAll = stripColorCodes(base.getString());
         if (replaceAll.isBlank()) {
            return base;
         } else {
            int fakeStatsValue = fakeStatsFlag ? this.secondaryIndexCounter++ : this.primaryLineCounter++;
            return this.replaceLineValue(fakeStatsValue, replaceAll, base);
         }
      } else {
         return base;
      }
   }

   public Text rewriteLineFirstPass(Text text) {
      return this.rewriteSidebarLine(text, true);
   }

   private boolean matchesSidebarLine(int fakeStatsLimit, int fakeStatsValue, String fakeStatsText, String[] texts) {
      return fakeStatsLimit > 0 ? fakeStatsValue + 1 == fakeStatsLimit : containsAnyKeyword(this.toLowerCaseCached(fakeStatsText), texts);
   }

   private static boolean containsAnyKeyword(String fakeStatsText, String[] texts) {
      for (String textsItem : texts) {
         if (fakeStatsText.contains(textsItem)) {
            return true;
         }
      }

      return false;
   }

   public FakeStats() {
      super("FakeStats", IconsPngType.DONUTSMP);
      this.shards = new StringBase("Shards", "Fake shard count. Blank = off.", "");
      this.shardsLine = new IntegerSetting("Shards Line", "Which sidebar line is shards. 0 = auto by name.", 2, 0, 15);
      this.kills = new StringBase("Kills", "Fake kill count. Blank = off.", "");
      this.killsLine = new IntegerSetting("Kills Line", "Which sidebar line is kills. 0 = auto by name.", 3, 0, 15);
      this.deaths = new StringBase("Deaths", "Fake death count. Blank = off.", "");
      this.deathsLine = new IntegerSetting("Deaths Line", "Which sidebar line is deaths. 0 = auto by name.", 4, 0, 15);
      this.playtime = new StringBase("Playtime", "Fake playtime, any text. Blank = off.", "");
      this.playtimeLine = new IntegerSetting("Playtime Line", "Which sidebar line is playtime. 0 = auto by name.", 5, 0, 15);
      this.sidebar = new BooleanSetting("Sidebar", "Rewrite the server scoreboard (leaderboard) on the right.", true);
      this.balanceCommandEnabled = new BooleanSetting("Balance Command", "Intercept /bal & /balance to show your fake balance.", true);
      this.deductOnPay = new BooleanSetting("Deduct On Pay", "FakePay subtracts what you pay from Money.", true);
      this.addSettings(
         new ConfigSetting[]{
            this.moneyInput,
            this.moneyLine,
            this.shards,
            this.shardsLine,
            this.kills,
            this.killsLine,
            this.deaths,
            this.deathsLine,
            this.playtime,
            this.playtimeLine,
            this.sidebar,
            this.balanceCommandEnabled,
            this.deductOnPay
         }
      );
   }

   public void resetLineCounters() {
      this.secondaryIndexCounter = 0;
      this.primaryLineCounter = 0;
   }

   public void deductBalance(double fakeStatsBound) {
      this.refreshBalanceFromInput();
      this.fakeBalance = Math.max(0.0, this.fakeBalance - Math.max(0.0, fakeStatsBound));
   }

   private Text replaceLineValue(int fakeStatsValue, String fakeStatsText, Text base) {
      if (!this.moneyInput.getCurrentText().isBlank() && this.matchesSidebarLine(this.moneyLine.getValue(), fakeStatsValue, fakeStatsText, MONEY_KEYWORDS)) {
         return TextMutableUtils.replaceLastNumber(base, this.getFormattedBalance());
      } else if (!this.shards.getCurrentText().isBlank() && this.matchesSidebarLine(this.shardsLine.getValue(), fakeStatsValue, fakeStatsText, SHARD_KEYWORDS)) {
         return TextMutableUtils.replaceLastNumber(base, this.shards.getCurrentText().trim());
      } else if (!this.kills.getCurrentText().isBlank() && this.matchesSidebarLine(this.killsLine.getValue(), fakeStatsValue, fakeStatsText, KILL_KEYWORDS)) {
         return TextMutableUtils.replaceLastNumber(base, this.kills.getCurrentText().trim());
      } else if (!this.deaths.getCurrentText().isBlank() && this.matchesSidebarLine(this.deathsLine.getValue(), fakeStatsValue, fakeStatsText, DEATH_KEYWORDS)) {
         return TextMutableUtils.replaceLastNumber(base, this.deaths.getCurrentText().trim());
      } else {
         return !this.playtime.getCurrentText().isBlank() && this.matchesSidebarLine(this.playtimeLine.getValue(), fakeStatsValue, fakeStatsText, PLAYTIME_KEYWORDS)
            ? TextMutableUtils.replaceFromFirstDigit(base, this.playtime.getCurrentText().trim())
            : base;
      }
   }

   public boolean shouldDeductOnPay() {
      return this.deductOnPay.isEnabled();
   }

   @Override
   protected void onEnable() {
      this.lastMoneyInput = null;
      this.refreshBalanceFromInput();
   }

   public Text rewriteLineSecondPass(Text text) {
      return this.rewriteSidebarLine(text, false);
   }
}
