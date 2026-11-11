package com.corz.client.utils;

public enum CoordinatorRunnerType {
   COORDINATOR,
   BUILDER,
   MATERIAL_RUNNER,
   REPAIR,
   SUPPORT;

   public static CoordinatorRunnerType getBuilder(String builderText) {
      if (builderText != null) {
         for (CoordinatorRunnerType builder : values()) {
            if (builder.name().equalsIgnoreCase(builderText)) {
               return builder;
            }
         }
      }

      return BUILDER;
   }
}
