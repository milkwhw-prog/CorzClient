package com.corz.client.utils;

import com.corz.client.CorzClient;
import com.corz.client.render.VertexTextureFont;

public final class HudFontUtils {
   private HudFontUtils() {
   }

   public static VertexTextureFont getLargeFont() {
      return CorzClient.fontManager.getLargeFont();
   }

   public static VertexTextureFont getLargeAltFont() {
      return CorzClient.fontManager.getLargeAltFont();
   }

   public static VertexTextureFont getSmallFont() {
      return CorzClient.fontManager.getSmallFont();
   }
}
