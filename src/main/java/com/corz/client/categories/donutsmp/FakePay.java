package com.corz.client.categories.donutsmp;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.ModuleConfigAutosave;
import com.corz.client.config.IntegerSetting;
import com.corz.client.config.StringBase;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import com.corz.client.categories.misc.FakeCoordinates;
import ai.auth.Module;
import ai.auth.ModuleRegistry;
import com.corz.client.categories.donutsmp.FakeStats;
import com.corz.client.utils.IconsPngType;

public class FakePay extends Module {
   private final IntegerSetting delayTicks;
   private final List<FakePay.Pending> pendingLines;
   private final BooleanSetting playSound;
   private static final String DEFAULT_MESSAGE_TEMPLATE = "&aYou have sent &f${amount} &ato &f{player}&a.";
   private final BooleanSetting showOverHotbar;
   private final BooleanSetting blockSelfPay;
   private final StringBase messageTemplate = new StringBase(
      "Message", "Fake line. Codes: & colours. Placeholders: {player} {amount} {full} {short} {raw}.", "&fYou paid {player} &a$ &f{amount}"
   );

   static String formatShortAmount(long fakePayValue) {
      String[] strings = new String[]{"", "K", "M", "B", "T"};
      double spotValue = fakePayValue;

      int index;
      for (index = 0; Math.abs(spotValue) >= 1000.0 && index < strings.length - 1; index++) {
         spotValue /= 1000.0;
      }

      if (index == 0) {
         return String.format("%,d", fakePayValue);
      } else {
         String fakePayText = spotValue == Math.floor(spotValue) ? String.valueOf((long)spotValue) : String.format("%.1f", spotValue);
         return fakePayText + strings[index];
      }
   }

   public FakePay() {
      super("FakePay", IconsPngType.DONUTSMP);
      this.playSound = new BooleanSetting("Play Sound", "Play the XP-pickup 'pling' with the fake line (like the real server).", true);
      this.showOverHotbar = new BooleanSetting("Show Over Hotbar", "Also show the fake line just above the hotbar, like DonutSMP.", true);
      this.delayTicks = new IntegerSetting("Delay", "Ticks to wait before the fake line appears (mimics server latency).", 2, 0, 40);
      this.blockSelfPay = new BooleanSetting("Block Self Pay", "Ignore /pay directed at your own name (as the server would).", true);
      this.pendingLines = new ArrayList<>();
      this.addSettings(new ConfigSetting[]{this.messageTemplate, this.playSound, this.showOverHotbar, this.delayTicks, this.blockSelfPay});
   }

   @Override
   public void onConfigLoaded() {
      if ("&aYou have sent &f${amount} &ato &f{player}&a.".equals(this.messageTemplate.getCurrentText())) {
         this.messageTemplate.handleText(this.messageTemplate.getText());
         ModuleConfigAutosave.updateFlag2();
      }
   }

   @Override
   public void onDisable() {
      this.pendingLines.clear();
   }

   private String resolvePlayerName(String name) {
      if (mc.getNetworkHandler() != null) {
         for (PlayerListEntry playerListEntry : mc.getNetworkHandler().getPlayerList()) {
            if (playerListEntry.getProfile() != null) {
               String name2 = playerListEntry.getProfile().name();
               if (name2 != null && name2.equalsIgnoreCase(name)) {
                  return name2;
               }
            }
         }
      }

      return name;
   }

   static Long parseAmountWithSuffix(String numberText) {
      if (numberText == null) {
         return null;
      } else {
         numberText = numberText.trim().replace(",", "");
         if (numberText.startsWith("$")) {
            numberText = numberText.substring(1);
         }

         if (numberText.isEmpty()) {
            return null;
         } else {
            double numberFactor = 1.0;
            char lowerCase = Character.toLowerCase(numberText.charAt(numberText.length() - 1));
            if (Character.isLetter(lowerCase)) {
               switch (lowerCase) {
                  case 'b':
                     numberFactor = 1.0E9;
                     break;
                  case 'k':
                     numberFactor = 1000.0;
                     break;
                  case 'm':
                     numberFactor = 1000000.0;
                     break;
                  case 't':
                     numberFactor = 1.0E12;
                     break;
                  default:
                     return null;
               }

               numberText = numberText.substring(0, numberText.length() - 1);
               if (numberText.isEmpty()) {
                  return null;
               }
            }

            try {
               double double2 = Double.parseDouble(numberText);
               return !(double2 < 0.0) && !Double.isNaN(double2) && !Double.isInfinite(double2) ? Math.round(double2 * numberFactor) : null;
            } catch (NumberFormatException e) {
               return null;
            }
         }
      }
   }

   @Override
   public void onReset() {
      if (!this.pendingLines.isEmpty()) {
         for (int i = this.pendingLines.size() - 1; i >= 0; i--) {
            FakePay.Pending paydirecteditem = this.pendingLines.get(i);
            if (--paydirecteditem.remainingTicks <= 0) {
               this.showFakeLine(paydirecteditem.lineText);
               this.pendingLines.remove(i);
            }
         }
      }
   }

   static String translateColorCodes(String replaceText) {
      return replaceText == null ? "" : replaceText.replaceAll("(?i)&([0-9a-fk-or])", "\u00a7$1");
   }

   private String buildFakeLine(String rawFullText, long rawFullValue, String rawFullText2) {
      String format = String.format("%,d", rawFullValue);
      String formatShortAmount2 = formatShortAmount(rawFullValue);
      String replace = this.messageTemplate
         .getCurrentText()
         .replace("{player}", this.resolvePlayerName(rawFullText))
         .replace("{amount}", formatShortAmount2)
         .replace("{full}", format)
         .replace("{short}", formatShortAmount2)
         .replace("{raw}", rawFullText2);
      return translateColorCodes(replace);
   }

   public boolean interceptPayCommand(String payDirectedText) {
      if (payDirectedText == null) {
         return false;
      } else {
         String[] split = payDirectedText.trim().split("\\s+");
         if (split.length == 3 && split[0].equalsIgnoreCase("pay")) {
            String rawFullText = split[1];
            Long long2 = parseAmountWithSuffix(split[2]);
            if (long2 == null) {
               return false;
            } else if (this.blockSelfPay.isEnabled()
               && mc.player != null
               && rawFullText.equalsIgnoreCase(mc.player.getName().getString())) {
               return true;
            } else {
               FakeStats fakeStats = (FakeStats)ModuleRegistry.getModule(FakeStats.class);
               if (fakeStats != null && fakeStats.isEnabled() && fakeStats.shouldDeductOnPay()) {
                  fakeStats.deductBalance(long2.longValue());
               }

               String formatRawFull = this.buildFakeLine(rawFullText, long2, split[2]);
               int payDirectedValue = this.delayTicks.getValue();
               if (payDirectedValue <= 0) {
                  this.showFakeLine(formatRawFull);
               } else {
                  this.pendingLines.add(new FakePay.Pending(formatRawFull, payDirectedValue));
               }

               return true;
            }
         } else {
            return false;
         }
      }
   }

   private void showFakeLine(String hudGameText) {
      if (mc.inGameHud != null) {
         Text literal = FakeCoordinates.getLiteral(Text.literal(hudGameText));
         mc.inGameHud.getChatHud().addMessage(literal);
         if (this.showOverHotbar.isEnabled()) {
            mc.inGameHud.setOverlayMessage(literal, false);
         }

         if (this.playSound.isEnabled() && mc.player != null) {
            mc.player.playSound(SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0F, 1.0F);
         }
      }
   }

   private static final class Pending {
      final String lineText;
      int remainingTicks;

      Pending(String text2, int value) {
         this.lineText = text2;
         this.remainingTicks = value;
      }
   }
}
