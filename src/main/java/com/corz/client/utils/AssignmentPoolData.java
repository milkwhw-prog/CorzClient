package com.corz.client.utils;

import java.util.Locale;

public record AssignmentPoolData(
   String jobId,
   String ownerId,
   long leaseGeneration,
   long assignmentEpoch,
   AssignmentPoolData.State state,
   long lastHeartbeatMs,
   long lastProgressMs,
   int inFlightActions
) {
   public boolean isLeasedState() {
      return this.state == AssignmentPoolData.State.LEASED || this.state == AssignmentPoolData.State.ACTIVE;
   }

   public boolean canLeasedAssignment(String epoch, long member, long gen) {
      return this.canOwner(epoch)
         && this.leaseGeneration == member
         && this.assignmentEpoch == gen
         && (this.state == AssignmentPoolData.State.LEASED || this.state == AssignmentPoolData.State.ACTIVE);
   }

   public boolean canOwner(String member) {
      return this.ownerId != null && this.ownerId.equals(member);
   }

   public boolean canStateRevoking(long nowMs, long heartbeatTimeoutMs) {
      if (this.state == AssignmentPoolData.State.COMPLETE || this.state == AssignmentPoolData.State.REVOKING) {
         return false;
      } else if (this.state == AssignmentPoolData.State.BLOCKED_DEPENDENCY) {
         return false;
      } else {
         return this.inFlightActions > 0 ? false : nowMs - this.lastHeartbeatMs > heartbeatTimeoutMs;
      }
   }

   public AssignmentPoolData getAssignmentHeartbeat(AssignmentPoolData.State s) {
      return new AssignmentPoolData(
         this.jobId, this.ownerId, this.leaseGeneration, this.assignmentEpoch, s, this.lastHeartbeatMs, this.lastProgressMs, this.inFlightActions
      );
   }

   public AssignmentPoolData getLeasedGeneration(String newOwner, long nowMs, long assignmentEpoch) {
      return new AssignmentPoolData(
         this.jobId,
         newOwner,
         this.leaseGeneration + 1L,
         nowMs,
         newOwner == null ? AssignmentPoolData.State.UNASSIGNED : AssignmentPoolData.State.LEASED,
         assignmentEpoch,
         assignmentEpoch,
         0
      );
   }

   public AssignmentPoolData getRevoking() {
      return this.getAssignmentHeartbeat(AssignmentPoolData.State.REVOKING);
   }

   public String getFormat() {
      return String.format(
         Locale.ROOT,
         "job=%s owner=%s generation=%d assignmentEpoch=%d state=%s inFlight=%d",
         this.jobId,
         this.ownerId == null ? "pool" : this.ownerId,
         this.leaseGeneration,
         this.assignmentEpoch,
         this.state,
         this.inFlightActions
      );
   }

   public static AssignmentPoolData getUnassigned(String jobId) {
      return new AssignmentPoolData(jobId, null, 0L, 0L, AssignmentPoolData.State.UNASSIGNED, 0L, 0L, 0);
   }

   public static enum State {
      UNASSIGNED,
      LEASED,
      ACTIVE,
      BLOCKED_DEPENDENCY,
      COMPLETING,
      COMPLETE,
      REVOKING;
   }
}
