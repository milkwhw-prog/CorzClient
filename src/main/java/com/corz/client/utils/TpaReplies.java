package com.corz.client.utils;

import java.util.Locale;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TpaReplies {
   private static final Pattern COOLDOWN_DURATION_PATTERN = Pattern.compile("(\\d{1,5})\\s*(seconds|second|secs|sec|s|minutes|minute|mins|min|m)(?![a-z])");
   private static final String ᴀʙᴄᴅᴇꜰɢʜꞮᴊᴋʟᴍɴᴏᴘǪƦꜱᴛᴜᴠᴡʏᴢ = "\u1d00\u0299\u1d04\u1d05\u1d07\ua730\u0262\u029c\u026a\u1d0a\u1d0b\u029f\u1d0d\u0274\u1d0f\u1d18\u01eb\u0280\ua731\u1d1b\u1d1c\u1d20\u1d21\u028f\u1d22";
   private static final Pattern ANGLE_BRACKET_CHAT_PATTERN = Pattern.compile("^<[^<>]{1,40}>\\s");
   private static final Pattern CHAT_SEPARATOR_PATTERN = Pattern.compile(":\\s|\\s\u00bb\\s|\\s>>\\s|\\s\\|\\s");
   private static final Pattern USERNAME_AT_END_PATTERN = Pattern.compile("\\.?([a-z0-9_]{3,16})$");
   private static final String[] UNKNOWN_COMMAND_TEXTS = new String[]{
      "unknown command", "unknown or incomplete", "no permission", "don't have permission", "do not have permission"
   };
   private static final String[] HOME_MISSING_TEXTS = new String[]{"not found", "does not exist", "doesn't exist", "no home", "invalid", "unknown home"};
   private static final String[] COOLDOWN_TEXTS = new String[]{
      "cooldown", "please wait", "you must wait", "wait before", "try again in", "too fast", "slow down"
   };
   private static final String[] PENDING_REQUEST_TEXTS = new String[]{"request", "pending", "sent"};
   private static final String[] DISABLED_FEATURE_TEXTS = new String[]{"teleport", "tpa", "request"};
   private static final String[] NOT_ACCEPTING_TEXTS = new String[]{"not accepting", "toggled off"};
   private static final String[] PLAYER_NOT_FOUND_TEXTS = new String[]{
      "not found", "not online", "isn't online", "offline", "could not find", "couldn't find", "no player"
   };
   private static final String[] CANNOT_TELEPORT_TEXTS = new String[]{"you cannot", "you can't", "cannot teleport", "not allowed", "in combat", "combat tag"};
   private static final String[] EXPIRED_TEXTS = new String[]{"expired", "timed out"};
   private static final String[] DENIED_TEXTS = new String[]{"denied", "declined", "rejected"};
   private static final String[] REQUEST_ACCEPTED_TEXTS = new String[]{"accepted"};
   private static final String PLAIN_LATIN_CHARS = "abcdefghijklmnopqrstuvwyz";
   private static final String[] REQUEST_SENT_TEXTS = new String[]{
      "request sent", "sent a request", "sent a teleport request", "teleport request sent", "you sent"
   };
   private static final String[] WARMUP_TEXTS = new String[]{"teleporting in", "teleportation will", "don't move", "do not move"};
   private static final String[] CANCELLED_TEXTS = new String[]{"cancelled", "canceled", "you moved"};
   private static final String[] TELEPORTED_TEXTS = new String[]{"teleported", "teleporting"};
   private static final String[] WARMUP_MESSAGE_TEXTS = new String[]{
      "teleporting in",
      "teleportation will",
      "don't move",
      "do not move",
      "will be teleported",
      "while you are teleported",
      "while you teleport",
      "while teleporting",
      "teleporting you",
      "until you are teleported"
   };
   private static final String[] WARMUP_EXCLUDE_TEXTS = new String[]{"cooldown", "again", "cancel"};
   private static final String[] TPA_COMMAND_TEXTS = new String[]{"tpa", "teleport", "request", "command"};
   private static final String[] HOME_COMMAND_TEXTS = new String[]{"home", "teleport", "command"};
   private static final String[] TELEPORT_KEYWORD_TEXTS = new String[]{"teleport", "home", "warmup"};

   public static boolean canTeleportCommand(String teleportCommandText) {
      return containsAny(normalizeSmallCaps(teleportCommandText), HOME_COMMAND_TEXTS);
   }

   private static boolean isNameChar(char tpaRepliesLimit) {
      return tpaRepliesLimit >= 'a' && tpaRepliesLimit <= 'z' || tpaRepliesLimit >= '0' && tpaRepliesLimit <= '9' || tpaRepliesLimit == '_';
   }

   public static long parseCooldownMs(String compileText) {
      Matcher matcher = COOLDOWN_DURATION_PATTERN.matcher(normalizeSmallCaps(compileText));
      if (!matcher.find()) {
         return -1L;
      } else {
         long compileValue;
         try {
            compileValue = Long.parseLong(matcher.group(1));
         } catch (NumberFormatException e) {
            return -1L;
         }

         return matcher.group(2).startsWith("m") ? compileValue * 60000L : compileValue * 1000L;
      }
   }

   public static boolean isTeleportToYouMessage(String youTeleportText, String youTeleportText2) {
      String normalizeSmallCaps3 = normalizeSmallCaps(youTeleportText);
      return normalizeSmallCaps3.contains("teleport") && normalizeSmallCaps3.contains("to you") && !normalizeSmallCaps3.contains(" you to ") && containsWholeWord(youTeleportText, youTeleportText2);
   }

   public static boolean isTeleportCommandForPlayer(String playerTeleportText, String playerTeleportText2, boolean playerTeleportFlag) {
      String tpaRepliesText = normalizeSmallCaps(playerTeleportText);
      return containsAny(tpaRepliesText, TPA_COMMAND_TEXTS) || playerTeleportFlag && tpaRepliesText.contains("player") || containsWholeWord(playerTeleportText, playerTeleportText2);
   }

   public static boolean isCancelledWarmupMessage(String requestText) {
      String tpaRepliesText = normalizeSmallCaps(requestText);
      return containsAny(tpaRepliesText, CANCELLED_TEXTS) && containsAny(tpaRepliesText, TELEPORT_KEYWORD_TEXTS) && !tpaRepliesText.contains("request");
   }

   public static TpaReplies.Reply classifyReply(String requestText) {
      if (requestText != null && !requestText.isBlank() && requestText.length() <= 200) {
         String compileCommandText = normalizeSmallCaps(requestText);
         if (compileCommandText.isEmpty() || ANGLE_BRACKET_CHAT_PATTERN.matcher(compileCommandText).find()) {
            return TpaReplies.Reply.NONE;
         } else if (containsAny(compileCommandText, UNKNOWN_COMMAND_TEXTS)) {
            return TpaReplies.Reply.UNKNOWN_COMMAND;
         } else if (compileCommandText.contains("home") && containsAny(compileCommandText, HOME_MISSING_TEXTS)) {
            return TpaReplies.Reply.HOME_MISSING;
         } else if (containsAny(compileCommandText, COOLDOWN_TEXTS)) {
            return TpaReplies.Reply.COOLDOWN;
         } else if (compileCommandText.contains("already") && containsAny(compileCommandText, PENDING_REQUEST_TEXTS)) {
            return TpaReplies.Reply.PENDING;
         } else if ((!compileCommandText.contains("disabled") || !containsAny(compileCommandText, DISABLED_FEATURE_TEXTS)) && !containsAny(compileCommandText, NOT_ACCEPTING_TEXTS)) {
            if (containsAny(compileCommandText, PLAYER_NOT_FOUND_TEXTS)) {
               return TpaReplies.Reply.NOT_FOUND;
            } else if (containsAny(compileCommandText, CANNOT_TELEPORT_TEXTS)) {
               return TpaReplies.Reply.CANNOT;
            } else if (containsAny(compileCommandText, EXPIRED_TEXTS)) {
               return TpaReplies.Reply.EXPIRED;
            } else if (containsAny(compileCommandText, DENIED_TEXTS)) {
               return TpaReplies.Reply.DENIED;
            } else if (containsAny(compileCommandText, REQUEST_ACCEPTED_TEXTS)) {
               return TpaReplies.Reply.ACCEPTED;
            } else if (containsAny(compileCommandText, REQUEST_SENT_TEXTS)) {
               return TpaReplies.Reply.SENT;
            } else if (containsAny(compileCommandText, WARMUP_TEXTS)) {
               return TpaReplies.Reply.TELEPORTING;
            } else if (containsAny(compileCommandText, CANCELLED_TEXTS)) {
               return TpaReplies.Reply.CANCELLED;
            } else {
               return containsAny(compileCommandText, TELEPORTED_TEXTS) ? TpaReplies.Reply.TELEPORTED : TpaReplies.Reply.NONE;
            }
         } else {
            return TpaReplies.Reply.NOT_ACCEPTING;
         }
      } else {
         return TpaReplies.Reply.NONE;
      }
   }

   private TpaReplies() {
   }

   private static boolean containsAny(String tpaRepliesText, String[] texts) {
      for (String textsItem : texts) {
         if (tpaRepliesText.contains(textsItem)) {
            return true;
         }
      }

      return false;
   }

   public static String normalizeSmallCaps(String tpaRepliesText) {
      if (tpaRepliesText == null) {
         return "";
      } else {
         String lowerCase = KmbtSellerUtils.formatText(tpaRepliesText).replace('\u2019', '\'').trim().toLowerCase(Locale.ROOT);
         StringBuilder stringBuilder = new StringBuilder(lowerCase.length());

         for (int i = 0; i < lowerCase.length(); i++) {
            char charAt = lowerCase.charAt(i);
            int indexOf = "\u1d00\u0299\u1d04\u1d05\u1d07\ua730\u0262\u029c\u026a\u1d0a\u1d0b\u029f\u1d0d\u0274\u1d0f\u1d18\u01eb\u0280\ua731\u1d1b\u1d1c\u1d20\u1d21\u028f\u1d22"
               .indexOf(charAt);
            stringBuilder.append(indexOf >= 0 ? "abcdefghijklmnopqrstuvwyz".charAt(indexOf) : charAt);
         }

         return stringBuilder.toString();
      }
   }

   public static boolean isTeleportWarmupMessage(String teleportedText) {
      String tpaRepliesText = normalizeSmallCaps(teleportedText);
      return containsAny(tpaRepliesText, WARMUP_MESSAGE_TEXTS) && !containsAny(tpaRepliesText, WARMUP_EXCLUDE_TEXTS);
   }

   public static boolean isPlayerChatMessage(String compileText, Predicate<String> predicate) {
      String compileCommandText = normalizeSmallCaps(compileText);
      if (ANGLE_BRACKET_CHAT_PATTERN.matcher(compileCommandText).find()) {
         return true;
      } else if (predicate == null) {
         return false;
      } else {
         Matcher matcher = CHAT_SEPARATOR_PATTERN.matcher(compileCommandText);
         if (matcher.find() && matcher.start() <= 48) {
            Matcher matcher2 = USERNAME_AT_END_PATTERN.matcher(compileCommandText.substring(0, matcher.start()).trim());
            return matcher2.find() && predicate.test(matcher2.group(1));
         } else {
            return false;
         }
      }
   }

   public static boolean containsWholeWord(String rootText, String rootText2) {
      if (rootText2 != null && !rootText2.isEmpty()) {
         String normalizeSmallCaps3 = normalizeSmallCaps(rootText);
         String lowerCase = rootText2.toLowerCase(Locale.ROOT);
         int rootValue = 0;

         while (true) {
            int indexOf = normalizeSmallCaps3.indexOf(lowerCase, rootValue);
            if (indexOf < 0) {
               return false;
            }

            int index = indexOf + lowerCase.length();
            if ((indexOf == 0 || !isNameChar(normalizeSmallCaps3.charAt(indexOf - 1))) && (index >= normalizeSmallCaps3.length() || !isNameChar(normalizeSmallCaps3.charAt(index)))) {
               return true;
            }

            rootValue = indexOf + 1;
         }
      } else {
         return false;
      }
   }

   public static enum Reply {
      NONE,
      SENT,
      NOT_FOUND,
      PENDING,
      NOT_ACCEPTING,
      COOLDOWN,
      CANNOT,
      UNKNOWN_COMMAND,
      ACCEPTED,
      DENIED,
      EXPIRED,
      HOME_MISSING,
      TELEPORTING,
      TELEPORTED,
      CANCELLED;
   }
}
