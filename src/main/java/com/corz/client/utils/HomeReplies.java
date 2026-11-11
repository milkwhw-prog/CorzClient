package com.corz.client.utils;

import java.util.function.Predicate;
import java.util.regex.Pattern;
import com.corz.client.utils.TpaReplies;
import com.corz.client.utils.RtpReplies;

public final class HomeReplies {
   private static final String[] NO_COMMAND_KEYWORDS = new String[]{
      "unknown command", "unknown or incomplete", "this command does not exist", "no permission", "don't have permission", "do not have permission"
   };
   private static final String[] CHAT_LIMIT_KEYWORDS = new String[]{
      "before your next message", "do not repeat the same", "don't repeat the same", "sending messages too fast", "please slow down your chat"
   };
   private static final String[] EXISTS_KEYWORDS = new String[]{
      "already exists", "already have a home", "already has a home", "already set", "name is taken", "already in use", "already used", "already exist"
   };
   private static final String[] LIMIT_KEYWORDS = new String[]{
      "maximum", "max homes", "home limit", "limit of", "too many homes", "cannot set more", "can't set more", "no more homes", "reached"
   };
   private static final String[] DENIED_KEYWORDS = new String[]{
      "you cannot",
      "you can't",
      "not allowed",
      "cannot set",
      "can't set",
      "invalid",
      "can not",
      "cannot",
      "can't",
      "unable",
      "not able",
      "failed",
      "could not",
      "couldn't",
      "denied",
      "not permitted",
      "disabled",
      "error",
      "usage",
      "must ",
      "only "
   };
   private static final String[] DELETED_KEYWORDS = new String[]{"deleted", "removed"};
   private static final String[] TPA_REQUEST_KEYWORDS = new String[]{"request", "tpa"};
   private static final String[] NOT_FOUND_KEYWORDS = new String[]{"no home", "not set", "not found", "does not exist", "doesn't exist", "unknown home"};
   private static final Pattern SET_SUCCESS_PATTERN = Pattern.compile("\\b(set|created|saved|updated|moved|relocated)\\b");

   public static HomeReplies.SetReply classifySetHomeReply(String homeText) {
      if (homeText != null && homeText.length() <= 240) {
         String normalizeSmallCaps = TpaReplies.normalizeSmallCaps(homeText);
         if (normalizeSmallCaps.isEmpty()) {
            return HomeReplies.SetReply.NONE;
         } else if (containsAnyKeyword(normalizeSmallCaps, CHAT_LIMIT_KEYWORDS)) {
            return HomeReplies.SetReply.CHAT_LIMIT;
         } else if (containsAnyKeyword(normalizeSmallCaps, NO_COMMAND_KEYWORDS)) {
            return HomeReplies.SetReply.NO_COMMAND;
         } else if (!normalizeSmallCaps.contains("home") || containsAnyKeyword(normalizeSmallCaps, TPA_REQUEST_KEYWORDS)) {
            return HomeReplies.SetReply.NONE;
         } else if (containsAnyKeyword(normalizeSmallCaps, EXISTS_KEYWORDS)) {
            return HomeReplies.SetReply.EXISTS;
         } else if (containsAnyKeyword(normalizeSmallCaps, LIMIT_KEYWORDS)) {
            return HomeReplies.SetReply.LIMIT;
         } else if (containsAnyKeyword(normalizeSmallCaps, DENIED_KEYWORDS)) {
            return HomeReplies.SetReply.DENIED;
         } else if (containsAnyKeyword(normalizeSmallCaps, NOT_FOUND_KEYWORDS)) {
            return HomeReplies.SetReply.NONE;
         } else {
            return SET_SUCCESS_PATTERN.matcher(normalizeSmallCaps).find() ? HomeReplies.SetReply.OK : HomeReplies.SetReply.NONE;
         }
      } else {
         return HomeReplies.SetReply.NONE;
      }
   }

   private HomeReplies() {
   }

   private static boolean containsAnyKeyword(String homeRepliesText, String[] texts) {
      for (String textsItem : texts) {
         if (homeRepliesText.contains(textsItem)) {
            return true;
         }
      }

      return false;
   }

   public static boolean isHomeRelatedMessage(String homeNoneText, Predicate<String> predicate) {
      if (homeNoneText == null || homeNoneText.isBlank() || homeNoneText.length() > 240) {
         return false;
      } else if (TpaReplies.isPlayerChatMessage(homeNoneText, predicate)) {
         return false;
      } else {
         String normalizeSmallCaps = TpaReplies.normalizeSmallCaps(homeNoneText);
         if (normalizeSmallCaps.isEmpty() || containsAnyKeyword(normalizeSmallCaps, TPA_REQUEST_KEYWORDS)) {
            return false;
         } else if (classifySetHomeReply(homeNoneText) == HomeReplies.SetReply.NONE && !isHomeDeletedMessage(homeNoneText)) {
            RtpReplies.Kind kind = RtpReplies.classifyReply(homeNoneText, 0L, predicate).kind();
            if (kind != RtpReplies.Kind.NONE && kind != RtpReplies.Kind.PROTECTED) {
               return true;
            } else if (normalizeSmallCaps.contains("home")) {
               return true;
            } else {
               TpaReplies.Reply textsRequest = TpaReplies.classifyReply(homeNoneText);
               return textsRequest == TpaReplies.Reply.TELEPORTING || textsRequest == TpaReplies.Reply.TELEPORTED || TpaReplies.isTeleportWarmupMessage(homeNoneText);
            }
         } else {
            return true;
         }
      }
   }

   public static boolean isHomeDeletedMessage(String homeText) {
      if (homeText != null && homeText.length() <= 240) {
         String normalizeSmallCaps = TpaReplies.normalizeSmallCaps(homeText);
         return normalizeSmallCaps.contains("home") && containsAnyKeyword(normalizeSmallCaps, DELETED_KEYWORDS) && !containsAnyKeyword(normalizeSmallCaps, TPA_REQUEST_KEYWORDS);
      } else {
         return false;
      }
   }

   public static enum SetReply {
      NONE,
      OK,
      EXISTS,
      LIMIT,
      DENIED,
      NO_COMMAND,
      CHAT_LIMIT;
   }
}
