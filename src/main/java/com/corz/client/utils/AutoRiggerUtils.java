package com.corz.client.utils;

import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class AutoRiggerUtils {
   private static final Map<String, String> RIGGER_CUSTOM_MAP = Map.of(
      "remove_client",
      "self_destruct",
      "old_chunkfinder",
      "chunk_finder",
      "custom_fov",
      "fov_slider",
      "gamble_rigger",
      "gambling_rigger",
      "rtp_meta",
      "rtp_base_alert",
      "shader_fog",
      "custom_sky"
   );
   private static final Set<String> REFILL_MACE_SET = Set.of(
      "milker",
      "double_anchor",
      "bow_aimbot",
      "auto_crystal_v2",
      "auto_totem_v2",
      "anchor_macro_v2",
      "safe_anchor_v2",
      "perfect_mace_hit",
      "auto_crystal",
      "bridge_build",
      "mace_swap",
      "auto_refill_hotbar"
   );

   public static String getLowerCase(String lowerCaseText) {
      if (lowerCaseText == null) {
         return "";
      } else {
         String lowerCase = lowerCaseText.trim().toLowerCase(Locale.ROOT);
         String rigger_custom_map = RIGGER_CUSTOM_MAP.get(lowerCase);
         return rigger_custom_map != null ? rigger_custom_map : lowerCase;
      }
   }

   public static boolean isContainsKey(String containsText) {
      return containsText == null ? false : RIGGER_CUSTOM_MAP.containsKey(containsText.trim().toLowerCase(Locale.ROOT));
   }

   private AutoRiggerUtils() {
   }

   public static boolean isContains(String containsText) {
      return REFILL_MACE_SET.contains(getLowerCase(containsText));
   }

   public static String formatText(String autoRiggerText) {
      long autoRiggerValue = 2024680511712617786L;
      long autoRiggerValue2 = 7338604231476299151L;
      long autoRiggerValue3 = -7920474191114440760L;
      if (autoRiggerText != null && !autoRiggerText.isEmpty()) {
         StringBuilder stringBuilder = new StringBuilder(autoRiggerText.length() + 8);
         long autoRiggerValue4 = autoRiggerValue ^ ((long)autoRiggerText.length() << 32 ^ autoRiggerValue) & -4294967296L;

         for (long i = autoRiggerValue4 ^ (0L ^ autoRiggerValue4) & 4294967295L; (int)i < (int)(i >>> 32); i ^= (i ^ i + 1L) & 4294967295L) {
            autoRiggerValue2 ^= ((long)autoRiggerText.charAt((int)i) << 32 ^ autoRiggerValue2) & -4294967296L;
            if (Character.isUpperCase((char)((int)(autoRiggerValue2 >>> 32)))) {
               autoRiggerValue2 ^= (((int)i > 0 && Character.isLowerCase(autoRiggerText.charAt((int)i - 1)) ? 1 : 0) ^ autoRiggerValue2) & 4294967295L;
               autoRiggerValue3 ^= ((long)((int)i + 1 < (int)(i >>> 32) && Character.isLowerCase(autoRiggerText.charAt((int)i + 1)) ? 1 : 0) << 32 ^ autoRiggerValue3)
                  & -4294967296L;
               if ((int)i > 0 && ((int)autoRiggerValue2 != 0 || (int)(autoRiggerValue3 >>> 32) != 0)) {
                  stringBuilder.append('_');
               }

               stringBuilder.append(Character.toLowerCase((char)((int)(autoRiggerValue2 >>> 32))));
            } else if (Character.isLetterOrDigit((char)((int)(autoRiggerValue2 >>> 32)))) {
               stringBuilder.append((char)((int)(autoRiggerValue2 >>> 32)));
            } else {
               stringBuilder.append('_');
            }
         }

         return stringBuilder.toString();
      } else {
         return "";
      }
   }
}
