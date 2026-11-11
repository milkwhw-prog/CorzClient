package com.corz.client.config;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public final class MsdfFontData {
   @SerializedName("glyphs")
   public List<MsdfFontData.Glyph> parsedCorzfontItems;
   @SerializedName("atlas")
   public MsdfFontData.Atlas class172;
   @SerializedName("kerning")
   public List<MsdfFontData.Kerning> items;
   @SerializedName("metrics")
   public MsdfFontData.Metrics class245;

   public static final class Atlas {
      @SerializedName("distanceRange")
      public float amount;
      @SerializedName("width")
      public float parsedCorzfontAmount;
      @SerializedName("height")
      public float parsedCorzfontAmount2;
   }

   public static final class Bounds {
      @SerializedName("bottom")
      public float amount;
      @SerializedName("top")
      public float amount2;
      @SerializedName("right")
      public float amount3;
      @SerializedName("left")
      public float amount4;
   }

   public static final class Glyph {
      @SerializedName("unicode")
      public int value;
      @SerializedName("advance")
      public float amount;
      @SerializedName("planeBounds")
      public MsdfFontData.Bounds class191;
      @SerializedName("atlasBounds")
      public MsdfFontData.Bounds class1912;
   }

   public static final class Kerning {
      @SerializedName("unicode1")
      public int value;
      @SerializedName("unicode2")
      public int value2;
      @SerializedName("advance")
      public float amount;
   }

   public static final class Metrics {
      @SerializedName("lineHeight")
      public float parsedCorzfontAmount;
      @SerializedName("ascender")
      public float parsedCorzfontAmount2;
   }
}
