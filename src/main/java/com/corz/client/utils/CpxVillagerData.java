package com.corz.client.utils;

import net.minecraft.util.Identifier;

public record CpxVillagerData(
   String id,
   CpxVillagerData.Kind kind,
   String name,
   String code,
   String tagline,
   Identifier icon,
   Identifier padItem,
   Identifier potion,
   Identifier skin,
   Identifier eyes,
   Identifier core,
   boolean slim,
   Identifier mainHand,
   Identifier offHand,
   boolean planned,
   boolean glint,
   boolean villager,
   CpxVillagerData.Act act
) {
   public static final String EYES = "_eyes";
   public static final Identifier vanilla = Identifier.ofVanilla("textures/entity/villager/villager.png");
   public static final String CORZCLIENT = "corzclient";
   public static final String TEXTURES_BOTS = "textures/bots/";
   public static final String CPX = "cpx01";
   public static final String CORE = "_core";

   public boolean isPullAct() {
      return this.act == CpxVillagerData.Act.PULL;
   }

   public CpxVillagerData getHandTagline(String icon, String potion) {
      return new CpxVillagerData(
         this.id,
         this.kind,
         this.name,
         this.code,
         this.tagline,
         getIdentifier(icon),
         this.padItem,
         getIdentifier(potion),
         this.skin,
         this.eyes,
         this.core,
         this.slim,
         this.mainHand,
         this.offHand,
         this.planned,
         this.glint,
         this.villager,
         this.act
      );
   }

   public static CpxVillagerData getPullUtility(String tagline, String id, String code, String skinKey, String item) {
      Identifier identifier = getIdentifier(item);
      return new CpxVillagerData(
         tagline,
         CpxVillagerData.Kind.UTILITY,
         null,
         id,
         code,
         identifier,
         null,
         null,
         getIdentifierForString(skinKey),
         getIdentifierForString(skinKey + "_eyes"),
         getIdentifierForString(skinKey + "_core"),
         false,
         identifier,
         null,
         false,
         false,
         false,
         CpxVillagerData.Act.PULL
      );
   }

   public static Identifier getIdentifier(String id) {
      return id == null ? null : Identifier.of(id);
   }

   public static CpxVillagerData getPvpNone(String mainHand, String tagline, String id, String code, String skinKey) {
      Identifier identifier = getIdentifier(skinKey);
      return new CpxVillagerData(
         mainHand,
         CpxVillagerData.Kind.PVP,
         null,
         tagline,
         id,
         identifier,
         null,
         null,
         getIdentifierForString(code),
         null,
         null,
         false,
         identifier,
         null,
         false,
         false,
         false,
         CpxVillagerData.Act.NONE
      );
   }

   public static CpxVillagerData getPvpNone(String code, String tagline, String offHand, String skinKey, String id, String mainHand) {
      Identifier identifier = getIdentifier(id);
      return new CpxVillagerData(
         code,
         CpxVillagerData.Kind.PVP,
         null,
         tagline,
         offHand,
         identifier,
         null,
         null,
         getIdentifierForString(skinKey),
         getIdentifierForString(skinKey + "_eyes"),
         getIdentifierForString(skinKey + "_core"),
         false,
         identifier,
         getIdentifier(mainHand),
         false,
         false,
         false,
         CpxVillagerData.Act.NONE
      );
   }

   public static CpxVillagerData getUtilityNone(String mainHand, String tagline, String skinKey, String icon, String id, String code) {
      return new CpxVillagerData(
         mainHand,
         CpxVillagerData.Kind.UTILITY,
         null,
         tagline,
         skinKey,
         getIdentifier(id),
         null,
         null,
         getIdentifierForString(icon),
         null,
         null,
         false,
         getIdentifier(code),
         null,
         false,
         false,
         false,
         CpxVillagerData.Act.NONE
      );
   }

   public boolean isSkin() {
      return this.skin != null;
   }

   public boolean isPvpKind() {
      return this.kind == CpxVillagerData.Kind.PVP;
   }

   public static CpxVillagerData getUtilityNone(String code, String tagline, String padItem, String id) {
      Identifier identifier = getIdentifier(id);
      return new CpxVillagerData(
         code,
         CpxVillagerData.Kind.UTILITY,
         null,
         tagline,
         padItem,
         identifier,
         identifier,
         null,
         null,
         null,
         null,
         false,
         null,
         null,
         false,
         false,
         false,
         CpxVillagerData.Act.NONE
      );
   }

   public static CpxVillagerData getVanillaUtility(String code, String icon, String tagline, String id) {
      return new CpxVillagerData(
         code,
         CpxVillagerData.Kind.UTILITY,
         null,
         icon,
         tagline,
         getIdentifier(id),
         null,
         null,
         vanilla,
         null,
         null,
         false,
         null,
         null,
         false,
         false,
         true,
         CpxVillagerData.Act.NONE
      );
   }

   public static CpxVillagerData getCpxPvp(String name, String tagline, String id, String code, String offHand, String mainHand) {
      Identifier identifier = getIdentifier(offHand);
      return new CpxVillagerData(
         name,
         CpxVillagerData.Kind.PVP,
         tagline,
         id,
         code,
         identifier,
         null,
         null,
         getIdentifierForString("cpx01"),
         null,
         null,
         false,
         identifier,
         getIdentifier(mainHand),
         true,
         false,
         false,
         CpxVillagerData.Act.NONE
      );
   }

   public CpxVillagerData getHandTagline() {
      return new CpxVillagerData(
         this.id,
         this.kind,
         this.name,
         this.code,
         this.tagline,
         this.icon,
         this.padItem,
         this.potion,
         this.skin,
         this.eyes,
         this.core,
         this.slim,
         this.mainHand,
         this.offHand,
         this.planned,
         true,
         this.villager,
         this.act
      );
   }

   public static Identifier getIdentifierForString(String key) {
      return Identifier.of("corzclient", "textures/bots/" + key + ".png");
   }

   public static enum Act {
      NONE,
      PULL;
   }

   public static enum Kind {
      PVP,
      UTILITY;
   }
}
