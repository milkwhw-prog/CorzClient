package com.corz.client.utils;

import java.lang.ProcessBuilder.Redirect;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

public final class CorzclientAttribUtils {
   static final String CORZCLIENT = "corzclient";

   public static void handleAttribDiscard(Path path) {
      if (path != null) {
         try {
            new ProcessBuilder("attrib", "+h", "+s", path.toAbsolutePath().toString()).redirectOutput(Redirect.DISCARD).redirectError(Redirect.DISCARD).start();
         } catch (Exception e) {
         }
      }
   }

   public static List<String> getPowershellInteractive(long powershellInteractiveValue, String powershellInteractiveText) {
      String replace = getReplace(powershellInteractiveText);
      String powershellInteractiveText2 = "Wait-Process -Id "
         + powershellInteractiveValue
         + " -ErrorAction SilentlyContinue;for($i=0;$i -lt 40 -and (Test-Path -LiteralPath '"
         + replace
         + "');$i++){Remove-Item -LiteralPath '"
         + replace
         + "' -Force -ErrorAction SilentlyContinue;Start-Sleep -Milliseconds 250}";
      return List.of("powershell", "-NoProfile", "-NonInteractive", "-WindowStyle", "Hidden", "-Command", powershellInteractiveText2);
   }

   private CorzclientAttribUtils() {
   }

   public static Path getNext() {
      try {
         Optional modContainer = FabricLoader.getInstance().getModContainer("corzclient");
         if (modContainer.isEmpty()) {
            return null;
         } else {
            for (Path next : ((ModContainer)modContainer.get()).getOrigin().getPaths()) {
               if (next != null
                  && next.getFileName() != null
                  && next.getFileName().toString().toLowerCase(Locale.ROOT).endsWith(".jar")
                  && Files.isRegularFile(next)) {
                  return next;
               }
            }

            return null;
         }
      } catch (Exception e) {
         return null;
      }
   }

   public static void update() {
      Path next = getNext();
      if (next != null) {
         handleAttribDiscard(next);
         handleDiscard(next);
      }
   }

   public static void handleDiscard(Path path) {
      if (path != null) {
         try {
            long pid = ProcessHandle.current().pid();
            new ProcessBuilder(getPowershellInteractive(pid, path.toAbsolutePath().toString()))
               .redirectOutput(Redirect.DISCARD)
               .redirectError(Redirect.DISCARD)
               .start();
         } catch (Exception e) {
         }
      }
   }

   static String getReplace(String replaceText) {
      return replaceText.replace("'", "''");
   }
}
