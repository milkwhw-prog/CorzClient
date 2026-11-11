package com.corz.client.utils;

import java.util.Locale;
import java.util.Set;

public final class TokenVersionUtils {
   public static final int UNUSED_ONE = 1;
   public static final Set<String> CONTENT_VERSION_SET = Set.of(
      "formatVersion", "clientVersion", "minecraftVersion", "name", "description", "author", "createdAt", "updatedAt", "tags", "content"
   );
   public static final Set<String> CONTENT_MODULES_SET = Set.of("modules", "hud", "theme");
   private static final Set<String> CARDNUMBER_CREDITCARD_SET = Set.of(
      "licensekey",
      "license_key",
      "licensetoken",
      "license_token",
      "token",
      "refreshcredential",
      "refresh_token",
      "refreshtoken",
      "deviceid",
      "device_id",
      "devicedigest",
      "device_digest",
      "adminapikey",
      "admin_api_key",
      "apikey",
      "api_key",
      "password",
      "passwd",
      "secret",
      "privatekey",
      "private_key",
      "sessioncookie",
      "session_cookie",
      "sessiontoken",
      "payment",
      "creditcard",
      "card_number",
      "cardnumber",
      "ssn"
   );

   private TokenVersionUtils() {
   }

   public static boolean canFlag(String tokenVersionText) {
      return tokenVersionText == null ? false : tokenVersionText.contains("/") || tokenVersionText.contains("\\") || tokenVersionText.contains("..") || tokenVersionText.contains(":") || tokenVersionText.startsWith("~");
   }

   public static boolean canCardnumberCreditcard(String cardnumberCreditcardText) {
      if (cardnumberCreditcardText == null) {
         return false;
      } else {
         String replace = cardnumberCreditcardText.toLowerCase(Locale.ROOT).replace("-", "").replace(" ", "");

         for (String cardnumberCreditcardItem : CARDNUMBER_CREDITCARD_SET) {
            if (replace.contains(cardnumberCreditcardItem.replace("_", ""))) {
               return true;
            }
         }

         return false;
      }
   }
}
