package com.corz.client.utils;

public enum JobSessionType {
   SESSION_CREATE,
   SESSION_JOIN,
   SESSION_LEAVE,
   PLAYER_STATE,
   BUILD_INFO,
   JOB_ASSIGN,
   JOB_CLAIM,
   JOB_RELEASE,
   JOB_COMPLETE,
   JOB_FAILED,
   BLOCK_RESERVED,
   BLOCK_COMPLETE,
   BLOCK_INVALID,
   PROGRESS_UPDATE,
   MATERIAL_STATE,
   HEARTBEAT,
   HOST_CHANGED;

   public static JobSessionType parseByName(String jobSessionText) {
      if (jobSessionText != null) {
         for (JobSessionType jobSessionType : values()) {
            if (jobSessionType.name().equalsIgnoreCase(jobSessionText)) {
               return jobSessionType;
            }
         }
      }

      return null;
   }
}
