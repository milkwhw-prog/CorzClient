package com.corz.client.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class ModuleIdentities {
   private static final Properties PROPERTIES = loadIdentitiesProperties();

   private static Properties loadIdentitiesProperties() {
      Properties properties = new Properties();

      try {
         try (InputStream resourceAsStream = ModuleIdentities.class.getResourceAsStream("/META-INF/corz-module-ids.properties")) {
            if (resourceAsStream != null) {
               properties.load(resourceAsStream);
            }
         }

         return properties;
      } catch (IOException e) {
         throw new IllegalStateException("Could not read module identities", e);
      }
   }

   private ModuleIdentities() {
   }

   public static String getProperty(Class<?> class2) {
      return PROPERTIES.getProperty(class2.getName(), class2.getSimpleName());
   }
}
