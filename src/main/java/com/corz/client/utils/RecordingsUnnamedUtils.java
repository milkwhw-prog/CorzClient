package com.corz.client.utils;

import com.corz.client.config.SchematicLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import com.corz.client.utils.JsonMirror;

public final class RecordingsUnnamedUtils {
   public static Path getResolve(String requestedName) {
      String baseName = requestedName != null && !requestedName.isBlank() ? requestedName : "unnamed";
      int lastIndexOf = baseName.lastIndexOf(46);
      if (lastIndexOf > 0) {
         baseName = baseName.substring(0, lastIndexOf);
      }

      return SchematicLoader.getSchematicsDir().resolve("recordings").resolve(baseName + ".build.json");
   }

   private RecordingsUnnamedUtils() {
   }

   public static JsonMirror getJsonMirror(String jsonMirrorText) {
      try {
         Path resolve = getResolve(jsonMirrorText);
         return !Files.exists(resolve) ? null : JsonMirror.getMirrorFormat(Files.readString(resolve));
      } catch (Exception e) {
         return null;
      }
   }
}
