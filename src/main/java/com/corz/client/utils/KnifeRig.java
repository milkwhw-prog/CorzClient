package com.corz.client.utils;

import java.util.function.IntToDoubleFunction;

public final class KnifeRig {
   public static final KnifeRig M9_BAYONET_TP = new KnifeRig("m9_bayonet_tp", new KnifeRig.Part("m9_bayonet", -1, 0.0F, 0.0F, -1));
   public static final KnifeRig STILETTO_TP = new KnifeRig(
      "stiletto_tp", new KnifeRig.Part("stiletto_handle", -1, 0.0F, 0.0F, -1), new KnifeRig.Part("stiletto_blade", 0, 8.0F, 6.3F, 14)
   );
   public static final String ASSETS_CORZCLIENT_TEXTURES = "/assets/corzclient/textures/knife/knife.png";
   public static final String ASSETS_CORZCLIENT_VISUALS = "/assets/corzclient/c2visuals/butterfly/texture.png";
   public static final KnifeRig BUTTERFLY_TP = new KnifeRig(
      "butterfly_tp",
      new KnifeRig.Part("balisong_safe", -1, 0.0F, 0.0F, -1),
      new KnifeRig.Part("balisong_blade", 0, 7.4F, 6.2F, 15),
      new KnifeRig.Part("balisong_bite", 1, 8.6F, 6.2F, 16)
   );
   private final KnifeRig.Part[] childParentParts;
   private final String childParentText;
   public static final String BUTTERFLY = "C2 Butterfly";
   public static final KnifeRig INSTANCE = new KnifeRig(
      "butterfly_tp",
      "/assets/corzclient/c2visuals/butterfly/texture.png",
      true,
      new KnifeRig.Part("c2_blade", -1, 0.0F, 0.0F, -1),
      new KnifeRig.Part("c2_up", -1, 0.0F, 0.0F, -1),
      new KnifeRig.Part("c2_down", -1, 0.0F, 0.0F, -1)
   );
   private final String childParentTextChildParent;
   private final boolean childParentFlag;

   public String getChildParentText() {
      return this.childParentText;
   }

   public static KnifeRig getButterflyInstance(String butterflyText) {
      return "C2 Butterfly".equals(butterflyText) ? INSTANCE : getBayonetStiletto(butterflyText);
   }

   public static double getAmount(double knifeRigValue) {
      double knifeRigFactor = Math.max(0.0, (6.2F - knifeRigValue) / 10.46);
      return Math.toRadians(30.0) * knifeRigFactor * (0.3 + 0.7 * knifeRigFactor);
   }

   public boolean isChildParentFlag() {
      return this.childParentFlag;
   }

   public int getParentParts() {
      return this.childParentParts.length;
   }

   public static double[] getAmounts(double knifeRigValue, double knifeRigValue2) {
      double knifeRigValue3 = 6.2F - knifeRigValue2;
      if (knifeRigValue3 <= 0.0) {
         return new double[]{knifeRigValue, knifeRigValue2, 0.0};
      } else {
         double spotValue = knifeRigValue - 8.0;
         double abs5 = Math.abs(spotValue);
         double knifeRigFactor = knifeRigValue3 / 10.46;
         double knifeRigFactor2 = abs5 <= 0.02 ? spotValue : Math.copySign(0.02 + (abs5 - 0.02) * (1.0 - 0.11 * knifeRigFactor * knifeRigFactor), spotValue);
         int knifeRigDivisor = 2 * (int)Math.ceil(knifeRigValue3 * 8.0);
         double knifeRigFactor3 = knifeRigValue3 / knifeRigDivisor;
         double knifeRigTotal = 0.0;
         double knifeRigTotal2 = 0.0;

         for (int i = 0; i <= knifeRigDivisor; i++) {
            double knifeRigFactor4 = i != 0 && i != knifeRigDivisor ? ((i & 1) == 1 ? 4.0 : 2.0) : 1.0;
            double angle = getAmount(6.2F - i * knifeRigFactor3);
            knifeRigTotal += knifeRigFactor4 * Math.sin(angle);
            knifeRigTotal2 += knifeRigFactor4 * Math.cos(angle);
         }

         double angle2 = getAmount(knifeRigValue2);
         return new double[]{8.0 + knifeRigTotal * knifeRigFactor3 / 3.0 + knifeRigFactor2 * Math.cos(angle2), 6.2F - knifeRigTotal2 * knifeRigFactor3 / 3.0 + knifeRigFactor2 * Math.sin(angle2), angle2};
      }
   }

   public KnifeRig.Part getParentParts(int index) {
      return this.childParentParts[index];
   }

   private KnifeRig(String knifeRigText, KnifeRig.Part... parts2) {
      this(knifeRigText, "/assets/corzclient/textures/knife/knife.png", false, parts2);
   }

   public static KnifeRig getBayonetStiletto(String bayonetStilettoText) {
      if (bayonetStilettoText == null) {
         return null;
      } else {
         return switch (bayonetStilettoText) {
            case "M9 Bayonet" -> M9_BAYONET_TP;
            case "Stiletto" -> STILETTO_TP;
            case "Butterfly" -> BUTTERFLY_TP;
            default -> null;
         };
      }
   }

   public String getChildParentTextChildParent() {
      return this.childParentTextChildParent;
   }

   private KnifeRig(String childParentText, String childParentText2, boolean childParentFlag, KnifeRig.Part... childParentParts) {
      for (int i = 0; i < childParentParts.length; i++) {
         if (childParentParts[i].parent() >= i) {
            throw new IllegalArgumentException("parent must come before its child");
         }
      }

      this.childParentTextChildParent = childParentText;
      this.childParentText = childParentText2;
      this.childParentFlag = childParentFlag;
      this.childParentParts = childParentParts;
   }

   public void handlePivotParent(int pivotParentValue, IntToDoubleFunction intToDoubleFunction, float[] pivotParentValues) {
      double angle = 0.0;
      double pivotParentTotal = 0.0;
      double pivotParentTotal2 = 0.0;
      int[] ints = new int[this.childParentParts.length];
      int pivotParentCounter = 0;

      for (int j = pivotParentValue; j >= 0; j = this.childParentParts[j].parent()) {
         ints[pivotParentCounter++] = j;
      }

      for (int k = pivotParentCounter - 1; k >= 0; k--) {
         KnifeRig.Part part = this.childParentParts[ints[k]];
         if (part.channel() >= 0) {
            double radians = Math.toRadians(intToDoubleFunction.applyAsDouble(part.channel()));
            double cos = Math.cos(radians);
            double sin = Math.sin(radians);
            double pivotParentFactor = part.pivotX() - (cos * part.pivotX() - sin * part.pivotY());
            double pivotParentFactor2 = part.pivotY() - (sin * part.pivotX() + cos * part.pivotY());
            double cos2 = Math.cos(angle);
            double sin2 = Math.sin(angle);
            pivotParentTotal += cos2 * pivotParentFactor - sin2 * pivotParentFactor2;
            pivotParentTotal2 += sin2 * pivotParentFactor + cos2 * pivotParentFactor2;
            angle += radians;
         }
      }

      pivotParentValues[0] = (float)Math.toDegrees(angle);
      pivotParentValues[1] = (float)pivotParentTotal;
      pivotParentValues[2] = (float)pivotParentTotal2;
   }

   public record Part(String model, int parent, float pivotX, float pivotY, int channel) {
   }
}
