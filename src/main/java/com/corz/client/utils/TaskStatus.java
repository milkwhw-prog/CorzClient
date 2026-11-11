package com.corz.client.utils;

public enum TaskStatus {
   UNASSIGNED,
   ASSIGNED,
   ACTIVE,
   BLOCKED,
   FAILED,
   COMPLETE;

   public static TaskStatus parseOrUnassigned(String unassignedText) {
      if (unassignedText != null) {
         for (TaskStatus unassigned : values()) {
            if (unassigned.name().equalsIgnoreCase(unassignedText)) {
               return unassigned;
            }
         }
      }

      return UNASSIGNED;
   }

   public boolean isUnavailable() {
      return this == UNASSIGNED || this == BLOCKED || this == FAILED;
   }
}
