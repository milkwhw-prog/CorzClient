package com.corz.client.utils;

import com.google.gson.JsonObject;
import com.corz.client.utils.RotMirData;

public record DimJsonData(RotMirData placement, String dimension) {
   public JsonObject getDimProperty() {
      JsonObject propertyRot = this.placement.getPropertyRot();
      propertyRot.addProperty("dim", this.dimension);
      return propertyRot;
   }

   public boolean canPlacementDimension(DimJsonData o) {
      return o != null && this.dimension.equals(o.dimension) && this.placement != null && this.placement.equals(o.placement);
   }

   public boolean isDimensionPlacement() {
      return this.placement != null && !this.dimension.isEmpty();
   }

   public String formatDimensionPlacement() {
      return this.placement.formatOriginMirror() + " in " + this.dimension;
   }

   public DimJsonData(RotMirData placement, String dimension) {
      dimension = dimension == null ? "" : dimension;
      this.placement = placement;
      this.dimension = dimension;
   }

   public String getHexString() {
      if (!this.isDimensionPlacement()) {
         return "";
      } else {
         RotMirData placement = this.placement;
         return Integer.toHexString(this.dimension.hashCode() * 31 + placement.hashCode());
      }
   }

   public static DimJsonData getDim(JsonObject o) {
      if (o == null) {
         return null;
      } else {
         RotMirData rotMir = RotMirData.getRotMir(o);
         if (rotMir == null) {
            return null;
         } else {
            String dimText = o.has("dim") ? o.get("dim").getAsString() : "";
            return dimText != null && !dimText.isEmpty() ? new DimJsonData(rotMir, dimText) : null;
         }
      }
   }
}
