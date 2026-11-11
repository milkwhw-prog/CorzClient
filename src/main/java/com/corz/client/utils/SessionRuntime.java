package com.corz.client.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public final class SessionRuntime {
   private int sessionRuntimeValue;
   private int sessionRuntimeCount;
   private final Map<String, Long> runtimeSessionPositionMap = new LinkedHashMap<>();

   public long getTime(String sessionRuntimeText, long sessionRuntimeValue) {
      return sessionRuntimeValue - this.runtimeSessionPositionMap.getOrDefault(sessionRuntimeText, 0L);
   }

   public void handleRuntimeSession(int sessionRuntimeValue, Map<String, Long> map) {
      this.sessionRuntimeCount++;
      this.sessionRuntimeValue = sessionRuntimeValue;
      this.runtimeSessionPositionMap.clear();
      this.runtimeSessionPositionMap.putAll(map);
   }

   public String formatSessionRuntime(int sessionRuntimeValue, Map<String, Long> map) {
      StringBuilder append = new StringBuilder("sessionId=")
         .append(this.sessionRuntimeCount)
         .append(" sessionRuntime=")
         .append(this.sessionRuntimeValue < 0 ? 0 : (sessionRuntimeValue - this.sessionRuntimeValue) / 20)
         .append('s');

      for (Entry entry : map.entrySet()) {
         String sessionRuntimeText = (String)entry.getKey();
         append.append(" session")
            .append(Character.toUpperCase(sessionRuntimeText.charAt(0)))
            .append(sessionRuntimeText.substring(1))
            .append('=')
            .append(this.getTime(sessionRuntimeText, (Long)entry.getValue()));
      }

      return append.toString();
   }

   public boolean isRuntimeSession() {
      return this.sessionRuntimeValue >= 0;
   }

   public SessionRuntime() {
      this.sessionRuntimeValue = -1;
   }

   public int getSessionRuntimeValue() {
      return this.sessionRuntimeValue;
   }

   public int getSessionRuntimeCount() {
      return this.sessionRuntimeCount;
   }
}
