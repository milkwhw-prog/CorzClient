package com.corz.client.config;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;
import com.corz.client.utils.MinmaxEnumType;

public final class ConfigSharePolicy {
   private static volatile ConfigSharePolicy cachedPolicy;
   public static final Pattern SENSITIVE_NAME_PATTERN = Pattern.compile(
      "price|spend|money|cost|\\bpay|buy|sell|coin|emerald|auction|\\bah\\b|profit|\\btax\\b|budget|overbuy|\\border|command|\\bcmd\\b|message|\\bmsg\\b|chat|webhook|\\burl\\b|\\blink\\b|\\bhost\\b|server|\\bip\\b|\\bport\\b|http|upload|discord|\\bteam|session|\\bfiles?\\b|\\bpath\\b|folder|director|\\bkeys?\\b|bind|seed|alias|logoff|log off|disconnect|\\bhome\\b|tpa|\\bwords?\\b|\\bdrop|throw|\\btrades?\\b",
      2
   );
   private final Set<String> extraAllowedKeys;
   private final boolean policyLoaded;
   public static final Pattern PROTECTIVE_NAME_PATTERN = Pattern.compile("safety|\\bsafe\\b|anti[ -]?(?:suicide|death)|\\bavoid|protect|ignore friends|stop on|\\brequire", 2);
   private static final Pattern INVISIBLE_CHARS_PATTERN = Pattern.compile(
      "[\\u00ad\\u034f\\u061c\\u115f\\u1160\\u17b4\\u17b5\\u180e\\u200b-\\u200f\\u202a-\\u202e\\u2060-\\u206f\\u3164\\ufe00-\\ufe0f\\ufeff\\uffa0]"
   );
   private static final Pattern CONTROL_CHARS_PATTERN = Pattern.compile("[\\u0000-\\u001f\\u007f-\\u009f\\u2028\\u2029]");
   private static final Pattern LINK_PATTERN = Pattern.compile("(?:^|[\\s\"'(<])(?:www\\.|discord(?:app)?\\.(?:gg|com)/)", 2);
   private static final Pattern DRIVE_PATH_PATTERN = Pattern.compile("^[a-z]:[\\\\/]", 2);
   private static final Pattern HOME_PATH_PATTERN = Pattern.compile("^~[\\\\/]");
   private static final Pattern PARENT_TRAVERSAL_PATTERN = Pattern.compile("(?:^|[\\\\/])\\.\\.(?:[\\\\/]|$)");
   private final Map<String, String> aliases;
   private final Set<MinmaxEnumType> localByDefaultTypes;
   private final Set<String> removedKeys;
   public static final String POLICY_RESOURCE_PATH = "/assets/corzclient/config-policy.json";
   private static final Pattern COMMAND_PATTERN = Pattern.compile(
      "^(?:pay|tpahere|tpaccept|tpyes|tpa|msg|tell|whisper|gift|sethome|delhome|ah\\s+(?:sell|list))\\s+\\S", 2
   );
   private final Map<String, ConfigSharePolicy.ModulePolicy> modulePolicies;
   private static final Pattern RESOURCE_ID_PATTERN = Pattern.compile("^(?:[a-z0-9_.-]+:)?[a-z0-9_./-]+$");
   private final Map<String, Set<String>> sections;

   public ConfigSharePolicy.Role getEffectiveRole(String riskyPortableText, boolean riskyPortableFlag, boolean riskyPortableFlag2) {
      ConfigSharePolicy.Role actorPinned = this.getModuleRole(riskyPortableText, riskyPortableFlag);
      return riskyPortableFlag2 && actorPinned == ConfigSharePolicy.Role.PORTABLE ? ConfigSharePolicy.Role.RISKY : actorPinned;
   }

   public Map<String, String> getAliases() {
      return this.aliases;
   }

   public boolean canSharePortableKeybind(ConfigSharePolicy.Role role, int jsonReviewValue) {
      return this.isPortableRole(role) && ConfigValueSanitizer.isKeyboardKeyCode(jsonReviewValue);
   }

   public boolean isPolicyLoaded() {
      return this.policyLoaded;
   }

   static ConfigSharePolicy loadBundledPolicy() {
      try {
         ConfigSharePolicy policyAssets;
         try (InputStream resourceAsStream = ConfigSharePolicy.class.getResourceAsStream("/assets/corzclient/config-policy.json")) {
            if (resourceAsStream == null) {
               return createRestrictivePolicy();
            }

            policyAssets = parsePolicy(new String(resourceAsStream.readAllBytes(), StandardCharsets.UTF_8));
         }

         return policyAssets;
      } catch (Exception e) {
         return createRestrictivePolicy();
      }
   }

   public static String detectRiskyText(String compileCommandText) {
      if (compileCommandText == null) {
         return null;
      } else if (CONTROL_CHARS_PATTERN.matcher(compileCommandText).find()) {
         return "control characters";
      } else {
         String trim = INVISIBLE_CHARS_PATTERN.matcher(Normalizer.normalize(compileCommandText, Form.NFKC)).replaceAll("").trim();
         if (trim.startsWith("/")) {
            return "a command";
         } else if (trim.contains("://") || LINK_PATTERN.matcher(trim).find()) {
            return "a link";
         } else if (trim.startsWith("\\\\") || DRIVE_PATH_PATTERN.matcher(trim).find() || HOME_PATH_PATTERN.matcher(trim).find() || PARENT_TRAVERSAL_PATTERN.matcher(trim).find()) {
            return "a file path";
         } else {
            return COMMAND_PATTERN.matcher(trim).find() ? "a command" : null;
         }
      }
   }

   public static String detectRiskyJson(JsonElement json, int jsonDeepLimit) {
      if (json != null && !json.isJsonNull()) {
         if (jsonDeepLimit > 14) {
            return "too deep";
         } else if (json.isJsonPrimitive()) {
            return json.getAsJsonPrimitive().isString() ? detectRiskyText(json.getAsString()) : null;
         } else if (json.isJsonArray()) {
            for (JsonElement jsonElement : json.getAsJsonArray()) {
               String detectRiskyJson2 = detectRiskyJson(jsonElement, jsonDeepLimit + 1);
               if (detectRiskyJson2 != null) {
                  return detectRiskyJson2;
               }
            }

            return null;
         } else {
            for (Entry entry : json.getAsJsonObject().entrySet()) {
               String detectRiskyText2 = detectRiskyText((String)entry.getKey());
               if (detectRiskyText2 == null) {
                  detectRiskyText2 = detectRiskyJson((JsonElement)entry.getValue(), jsonDeepLimit + 1);
               }

               if (detectRiskyText2 != null) {
                  return detectRiskyText2;
               }
            }

            return null;
         }
      } else {
         return null;
      }
   }

   public static ConfigSharePolicy getInstance() {
      ConfigSharePolicy cachedPolicy2 = cachedPolicy;
      if (cachedPolicy2 == null) {
         cachedPolicy2 = loadBundledPolicy();
         cachedPolicy = cachedPolicy2;
      }

      return cachedPolicy2;
   }

   private static Set<String> readNormalizedStringSet(JsonObject json, String iteratorJsonText) {
      HashSet hashSet = new HashSet();
      if (!json.has(iteratorJsonText)) {
         return hashSet;
      } else {
         for (JsonElement jsonElement : json.getAsJsonArray(iteratorJsonText)) {
            hashSet.add(normalizeKey(jsonElement.getAsString()));
         }

         return hashSet;
      }
   }

   public static ConfigSharePolicy createRestrictivePolicy() {
      return new ConfigSharePolicy(false, Map.of(), EnumSet.allOf(MinmaxEnumType.class), Map.of(), Set.of(), Map.of(), Set.of());
   }

   public static boolean isResourceIdList(String compileText) {
      long compileValue = 1822293571366756034L;
      if (compileText == null) {
         return false;
      } else {
         String[] split = compileText.split(",", -1);
         long compileValue2 = compileValue ^ ((long)split.length << 32 ^ compileValue) & -4294967296L;

         for (long i = compileValue2 ^ (0L ^ compileValue2) & 4294967295L; (int)i < (int)(i >>> 32); i ^= (i ^ i + 1L) & 4294967295L) {
            String compileText2 = split[(int)i];
            String trim = compileText2.trim();
            if (!trim.isEmpty() && !RESOURCE_ID_PATTERN.matcher(trim).matches()) {
               return false;
            }
         }

         return true;
      }
   }

   public ConfigSharePolicy.ModulePolicy getModulePolicy(String unmodifiableText) {
      return this.modulePolicies.get(unmodifiableText);
   }

   private static Set<String> readStringSet(JsonElement json) {
      HashSet hashSet = new HashSet();
      if (json != null && json.isJsonArray()) {
         for (JsonElement jsonElement : json.getAsJsonArray()) {
            hashSet.add(jsonElement.getAsString());
         }
      }

      return hashSet;
   }

   public static String normalizeKey(String lowerCaseText) {
      if (lowerCaseText == null) {
         return "";
      } else {
         String normalize = Normalizer.normalize(lowerCaseText, Form.NFKC);
         String replaceAll = INVISIBLE_CHARS_PATTERN.matcher(normalize).replaceAll("").trim().replaceAll("\\s+", " ");
         return replaceAll.toLowerCase(Locale.ROOT);
      }
   }

   public boolean isInAnySection(String jsonReviewText) {
      for (Set set : this.sections.values()) {
         if (set.contains(jsonReviewText)) {
            return true;
         }
      }

      return false;
   }

   public Set<String> getRemovedKeys() {
      return this.removedKeys;
   }

   public static ConfigSharePolicy parsePolicy(String reviewPortableText) {
      try {
         JsonObject asJsonObject = JsonParser.parseString(reviewPortableText).getAsJsonObject();
         if (!asJsonObject.has("version") || asJsonObject.get("version").getAsInt() != 1) {
            return createRestrictivePolicy();
         } else if (asJsonObject.has("modules") && asJsonObject.get("modules").isJsonObject()) {
            LinkedHashMap linkedHashMap = new LinkedHashMap();

            for (Entry entry : asJsonObject.getAsJsonObject("modules").entrySet()) {
               if (!((JsonElement)entry.getValue()).isJsonObject()) {
                  return createRestrictivePolicy();
               }

               JsonObject json = ((JsonElement)entry.getValue()).getAsJsonObject();
               ConfigSharePolicy.Role valueOf = ConfigSharePolicy.Role.valueOf(json.get("role").getAsString());
               String reviewPortableText2 = json.has("settingsDefault") ? json.get("settingsDefault").getAsString() : "portable";
               if (!reviewPortableText2.equals("local") && !reviewPortableText2.equals("portable")) {
                  return createRestrictivePolicy();
               }

               String reviewPortableText3 = json.has("sinkReview") && json.get("sinkReview").isJsonPrimitive() ? json.get("sinkReview").getAsString() : null;
               String reviewPortableText4 = json.has("portableReview") && json.get("portableReview").isJsonPrimitive() ? json.get("portableReview").getAsString() : null;
               linkedHashMap.put(
                  (String)entry.getKey(),
                  new ConfigSharePolicy.ModulePolicy(
                     (String)entry.getKey(),
                     valueOf,
                     reviewPortableText2.equals("local"),
                     readNormalizedStringSet(json, "local"),
                     readNormalizedStringSet(json, "validate"),
                     readNormalizedStringSet(json, "portable"),
                     reviewPortableText3,
                     reviewPortableText4
                  )
               );
            }

            EnumSet noneOf = EnumSet.noneOf(MinmaxEnumType.class);
            JsonObject jsonObject = asJsonObject.has("typeDefaults") && asJsonObject.get("typeDefaults").isJsonObject() ? asJsonObject.getAsJsonObject("typeDefaults") : null;
            if (jsonObject != null && jsonObject.has("local") && jsonObject.get("local").isJsonArray()) {
               for (JsonElement jsonElement : jsonObject.getAsJsonArray("local")) {
                  noneOf.add(MinmaxEnumType.valueOf(jsonElement.getAsString()));
               }

               LinkedHashMap linkedHashMap2 = new LinkedHashMap();
               if (asJsonObject.has("sections") && asJsonObject.get("sections").isJsonObject()) {
                  for (Entry entryItem : asJsonObject.getAsJsonObject("sections").entrySet()) {
                     linkedHashMap2.put((String)entryItem.getKey(), readStringSet((JsonElement)entryItem.getValue()));
                  }
               }

               LinkedHashMap linkedHashMap3 = new LinkedHashMap();
               if (asJsonObject.has("aliases") && asJsonObject.get("aliases").isJsonObject()) {
                  for (Entry entry3 : asJsonObject.getAsJsonObject("aliases").entrySet()) {
                     linkedHashMap3.put(((String)entry3.getKey()).toLowerCase(Locale.ROOT), ((JsonElement)entry3.getValue()).getAsString());
                  }
               }

               return new ConfigSharePolicy(
                  true, Collections.unmodifiableMap(linkedHashMap), noneOf, linkedHashMap2, readStringSet(asJsonObject.get("extraAllowed")), linkedHashMap3, readStringSet(asJsonObject.get("removed"))
               );
            } else {
               return createRestrictivePolicy();
            }
         } else {
            return createRestrictivePolicy();
         }
      } catch (Exception e) {
         return createRestrictivePolicy();
      }
   }

   public boolean isExtraAllowed(String jsonReviewText) {
      return this.policyLoaded && this.extraAllowedKeys.contains(jsonReviewText);
   }

   private ConfigSharePolicy(
      boolean flag,
      Map<String, ConfigSharePolicy.ModulePolicy> unmodifiableMap,
      Set<MinmaxEnumType> allOf,
      Map<String, Set<String>> map,
      Set<String> set,
      Map<String, String> map2,
      Set<String> set2
   ) {
      this.policyLoaded = flag;
      this.modulePolicies = unmodifiableMap;
      this.localByDefaultTypes = allOf;
      this.sections = map;
      this.extraAllowedKeys = set;
      this.aliases = map2;
      this.removedKeys = set2;
   }

   public ConfigSharePolicy.Scope classifyScope(String localPortableText, String lowerCaseText, MinmaxEnumType minmaxEnumType) {
      long localPortableValue = 2270213005120368886L;
      if (this.policyLoaded && lowerCaseText != null) {
         ConfigSharePolicy.ModulePolicy unmodifiablemap = this.modulePolicies.get(localPortableText);
         if (unmodifiablemap == null) {
            return ConfigSharePolicy.Scope.LOCAL;
         } else {
            String lowerCase = normalizeKey(lowerCaseText);
            if (unmodifiablemap.local().contains(lowerCase)) {
               return ConfigSharePolicy.Scope.LOCAL;
            } else {
               long localPortableValue2 = localPortableValue ^ ((long)(unmodifiablemap.role() != ConfigSharePolicy.Role.PINNED && unmodifiablemap.role() != ConfigSharePolicy.Role.GUARD ? 1 : 0) << 32 ^ localPortableValue) & -4294967296L;
               if ((int)(localPortableValue2 >>> 32) != 0 && unmodifiablemap.validate().contains(lowerCase)) {
                  return ConfigSharePolicy.Scope.VALIDATE;
               } else if ((int)(localPortableValue2 >>> 32) != 0 && unmodifiablemap.portable().contains(lowerCase)) {
                  return ConfigSharePolicy.Scope.PORTABLE;
               } else if (unmodifiablemap.settingsLocal()) {
                  return ConfigSharePolicy.Scope.LOCAL;
               } else if (minmaxEnumType == null || this.localByDefaultTypes.contains(minmaxEnumType)) {
                  return ConfigSharePolicy.Scope.LOCAL;
               } else {
                  return !SENSITIVE_NAME_PATTERN.matcher(lowerCaseText).find() && !PROTECTIVE_NAME_PATTERN.matcher(lowerCaseText).find() ? ConfigSharePolicy.Scope.PORTABLE : ConfigSharePolicy.Scope.LOCAL;
               }
            }
         }
      } else {
         return ConfigSharePolicy.Scope.LOCAL;
      }
   }

   public boolean isPortableRole(ConfigSharePolicy.Role role) {
      return role == ConfigSharePolicy.Role.PORTABLE;
   }

   public boolean isRoleAllowed(ConfigSharePolicy.Role role, boolean jsonReviewFlag) {
      return switch (role) {
         case PORTABLE -> true;
         case ACTOR, RISKY -> !jsonReviewFlag;
         case GUARD -> jsonReviewFlag;
         case PINNED -> false;
      };
   }

   public boolean isValueShareable(ConfigSharePolicy.Scope scope, JsonElement json) {
      if (scope == null || scope == ConfigSharePolicy.Scope.LOCAL || json == null || json.isJsonNull()) {
         return false;
      } else if (scope != ConfigSharePolicy.Scope.VALIDATE) {
         return detectRiskyJson(json, 0) == null;
      } else if (!json.isJsonPrimitive()) {
         return false;
      } else {
         JsonPrimitive asJsonPrimitive = json.getAsJsonPrimitive();
         if (asJsonPrimitive.isNumber()) {
            double asDouble = asJsonPrimitive.getAsDouble();
            return asDouble == Math.floor(asDouble) && Math.abs(asDouble) <= 2.147483647E9 && ConfigValueSanitizer.isKeyboardKeyCode((int)asDouble);
         } else {
            return !asJsonPrimitive.isString() ? false : detectRiskyText(asJsonPrimitive.getAsString()) == null && isResourceIdList(asJsonPrimitive.getAsString());
         }
      }
   }

   public Map<String, ConfigSharePolicy.ModulePolicy> getModulePolicies() {
      return this.modulePolicies;
   }

   public Set<String> getExtraAllowedKeys() {
      return this.extraAllowedKeys;
   }

   public ConfigSharePolicy.Role getModuleRole(String actorPinnedText, boolean actorPinnedFlag) {
      if (this.policyLoaded && !actorPinnedFlag) {
         ConfigSharePolicy.ModulePolicy unmodifiablemap = this.modulePolicies.get(actorPinnedText);
         return unmodifiablemap == null ? ConfigSharePolicy.Role.ACTOR : unmodifiablemap.role();
      } else {
         return ConfigSharePolicy.Role.PINNED;
      }
   }

   public boolean isLocalByDefault(MinmaxEnumType minmaxEnumType) {
      return minmaxEnumType == null || this.localByDefaultTypes.contains(minmaxEnumType);
   }

   public boolean isInSection(String jsonReviewText, String lowerCase) {
      Set set = this.sections.get(jsonReviewText);
      return set != null && set.contains(lowerCase);
   }

   public record ModulePolicy(
      String id,
      ConfigSharePolicy.Role role,
      boolean settingsLocal,
      Set<String> local,
      Set<String> validate,
      Set<String> portable,
      String sinkReview,
      String portableReview
   ) {
   }

   public static enum Role {
      PORTABLE,
      ACTOR,
      RISKY,
      GUARD,
      PINNED;
   }

   public static enum Scope {
      LOCAL,
      VALIDATE,
      PORTABLE;
   }
}
