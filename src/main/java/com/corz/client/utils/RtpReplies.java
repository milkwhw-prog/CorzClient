package com.corz.client.utils;

import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.corz.client.utils.TpaReplies;

public final class RtpReplies {
   private static final Pattern CLOCK_PATTERN = Pattern.compile("(?<![0-9])(\\d{1,2}):(\\d{2})(?![0-9])");
   private static final Pattern WAIT_WORD_PATTERN = Pattern.compile("\\bwait\\b|\\bcooldown\\b|\\bcool down\\b|\\bagain in\\b");
   private static final String[] DUEL_TEXTS = new String[]{"rtp queue", "rtpqueue", "opponent", "1v1", "duel"};
   private static final String[] CHAT_LIMIT_TEXTS = new String[]{
      "before your next message", "do not repeat the same", "don't repeat the same", "sending messages too fast", "please slow down your chat"
   };
   private static final String[] UNKNOWN_COMMAND_TEXTS = new String[]{
      "this command does not exist", "unknown command", "unknown or incomplete", "no permission", "don't have permission", "do not have permission"
   };
   private static final String[] COMBAT_TEXTS = new String[]{"in combat", "combat tag", "combat-tag", "while fighting"};
   private static final String[] PROTECTED_AREA_TEXTS = new String[]{
      "cannot do this on this area",
      "can't do this on this area",
      "cannot do this in this area",
      "can't do this in this area",
      "this area is protected",
      "you cannot break",
      "you can't break",
      "not allowed to break"
   };
   private static final String[] SEARCHING_TEXTS = new String[]{
      "finding random location",
      "finding a random location",
      "finding a safe location",
      "searching for a safe",
      "searching for a location",
      "looking for a safe"
   };
   private static final String[] SUCCESS_TEXTS = new String[]{
      "teleported to a random location", "you have been teleported", "teleported you to", "you were teleported"
   };
   private static final String[] CANCEL_WORD_TEXTS = new String[]{"cancelled", "canceled", "you moved"};
   private static final String[] CANCEL_CONTEXT_TEXTS = new String[]{"teleport", "rtp", "moved"};
   private static final String[] COOLDOWN_TEXTS = new String[]{
      "cooldown",
      "please wait",
      "you must wait",
      "wait before",
      "try again in",
      "too fast",
      "slow down",
      "you need to wait",
      "can use this again",
      "can rtp again"
   };
   private static final Pattern DURATION_PATTERN = Pattern.compile("(\\d{1,5})\\s*(hours|hour|hrs|hr|h|minutes|minute|mins|min|m|seconds|second|secs|sec|s)(?![a-z])");

   private static boolean containsAny(String rtpRepliesText, String[] texts) {
      for (String textsItem : texts) {
         if (rtpRepliesText.contains(textsItem)) {
            return true;
         }
      }

      return false;
   }

   public static RtpReplies.Reply classifyReply(String combatText, long combatLimit, Predicate<String> predicate) {
      if (combatText != null && !combatText.isBlank() && combatText.length() <= 240) {
         String normalizeSmallCaps = TpaReplies.normalizeSmallCaps(combatText);
         if (normalizeSmallCaps.isEmpty()) {
            return RtpReplies.Reply.INSTANCE;
         } else if (TpaReplies.isPlayerChatMessage(combatText, predicate)) {
            return RtpReplies.Reply.INSTANCE;
         } else if (containsAny(normalizeSmallCaps, DUEL_TEXTS)) {
            return RtpReplies.Reply.INSTANCE;
         } else if (containsAny(normalizeSmallCaps, CHAT_LIMIT_TEXTS)) {
            return RtpReplies.Reply.withKind(RtpReplies.Kind.CHAT_LIMIT);
         } else if (containsAny(normalizeSmallCaps, UNKNOWN_COMMAND_TEXTS)) {
            return combatLimit >= 0L && combatLimit <= 2000L ? RtpReplies.Reply.withKind(RtpReplies.Kind.UNKNOWN_COMMAND) : RtpReplies.Reply.INSTANCE;
         } else if (containsAny(normalizeSmallCaps, COMBAT_TEXTS)) {
            return RtpReplies.Reply.withKind(RtpReplies.Kind.COMBAT);
         } else if (containsAny(normalizeSmallCaps, PROTECTED_AREA_TEXTS)) {
            return RtpReplies.Reply.withKind(RtpReplies.Kind.PROTECTED);
         } else if (containsAny(normalizeSmallCaps, SEARCHING_TEXTS)) {
            return RtpReplies.Reply.withKind(RtpReplies.Kind.SEARCHING);
         } else if (containsAny(normalizeSmallCaps, SUCCESS_TEXTS)) {
            return RtpReplies.Reply.withKind(RtpReplies.Kind.SUCCESS);
         } else if (containsAny(normalizeSmallCaps, CANCEL_WORD_TEXTS) && containsAny(normalizeSmallCaps, CANCEL_CONTEXT_TEXTS)) {
            return RtpReplies.Reply.withKind(RtpReplies.Kind.CANCELLED);
         } else if (TpaReplies.isTeleportWarmupMessage(combatText)) {
            return RtpReplies.Reply.withKind(RtpReplies.Kind.WARMUP);
         } else {
            return !containsAny(normalizeSmallCaps, COOLDOWN_TEXTS) && (!WAIT_WORD_PATTERN.matcher(normalizeSmallCaps).find() || parseDurationMs(normalizeSmallCaps) < 0L)
               ? RtpReplies.Reply.INSTANCE
               : new RtpReplies.Reply(RtpReplies.Kind.COOLDOWN, parseDurationMs(normalizeSmallCaps));
         }
      } else {
         return RtpReplies.Reply.INSTANCE;
      }
   }

   private RtpReplies() {
   }

   public static long parseWaitMs(String rtpRepliesText) {
      return parseDurationMs(TpaReplies.normalizeSmallCaps(rtpRepliesText));
   }

   static long parseDurationMs(String compileText) {
      if (compileText == null) {
         return -1L;
      } else {
         Matcher matcher = CLOCK_PATTERN.matcher(compileText);
         if (matcher.find()) {
            try {
               long long2 = Long.parseLong(matcher.group(1));
               long long3 = Long.parseLong(matcher.group(2));
               if (long3 < 60L) {
                  return (long2 * 60L + long3) * 1000L;
               }
            } catch (NumberFormatException e) {
               return -1L;
            }
         }

         long compileTotal = 0L;
         boolean compileFlag = false;
         Matcher matcher2 = DURATION_PATTERN.matcher(compileText);

         while (matcher2.find()) {
            long compileValue;
            try {
               compileValue = Long.parseLong(matcher2.group(1));
            } catch (NumberFormatException e2) {
               continue;
            }

            String group = matcher2.group(2);
            if (group.startsWith("h")) {
               compileTotal += compileValue * 3600000L;
            } else if (group.startsWith("m")) {
               compileTotal += compileValue * 60000L;
            } else {
               compileTotal += compileValue * 1000L;
            }

            compileFlag = true;
         }

         return compileFlag ? compileTotal : -1L;
      }
   }

   public static enum Kind {
      NONE,
      SEARCHING,
      SUCCESS,
      COOLDOWN,
      CHAT_LIMIT,
      COMBAT,
      PROTECTED,
      UNKNOWN_COMMAND,
      WARMUP,
      CANCELLED;
   }

   public record Reply(RtpReplies.Kind kind, long waitMs) {
      public static final RtpReplies.Reply INSTANCE = new RtpReplies.Reply(RtpReplies.Kind.NONE, -1L);

      static RtpReplies.Reply withKind(RtpReplies.Kind kind) {
         return new RtpReplies.Reply(kind, -1L);
      }
   }
}
