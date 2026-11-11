package com.corz.client.world;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.PreferAnchorData;
import com.corz.client.utils.SurroundKitType;

public record CenterVecData(
   CenterVecData.State state,
   CenterVecData.Proposal proposal,
   double proposalValue,
   CenterVecData.Abort abort,
   SurroundKitType lane,
   CenterVecData.CrystalStep crystal,
   CenterVecData.Strike strike,
   CenterVecData.PearlShot pearl,
   CenterVecData.AnchorStep anchor,
   CenterVecData.Stand stand,
   Vec3d aimPoint,
   int killInTicks,
   int yields,
   String status,
   String arm
) {
   public static final CenterVecData INSTANCE = new CenterVecData(
      CenterVecData.State.IDLE,
      CenterVecData.Proposal.NONE,
      0.0,
      CenterVecData.Abort.NONE,
      SurroundKitType.NONE,
      null,
      null,
      null,
      null,
      null,
      null,
      Integer.MAX_VALUE,
      0,
      null,
      null
   );

   public boolean isIdleState() {
      return this.state != CenterVecData.State.IDLE;
   }

   public boolean isLoopHit() {
      return this.state == CenterVecData.State.HIT_LOOP;
   }

   public static enum Abort {
      NONE,
      POSTURE,
      OWN_POP,
      LOW_HP,
      LETHAL_THREAT,
      HEAL,
      TARGET,
      RESOURCES,
      EPISODE_CAP;
   }

   public record AnchorStep(BlockPos cell, boolean release, Vec3d feetAt, Box boxAt) {
      public AnchorStep(BlockPos cell, boolean release) {
         this(cell, release, null, null);
      }
   }

   public record CrystalStep(
      BlockPos base,
      BlockPos obsidianCell,
      Direction face,
      boolean hold,
      boolean breakNow,
      boolean trustObsidian,
      Vec3d targetFeetAt,
      Box targetBoxAt,
      BlockPos support,
      Direction supportFace
   ) {
      public CrystalStep(
         BlockPos targetFeetAt, BlockPos base, Direction obsidianCell, boolean trustObsidian, boolean face, boolean breakNow, Vec3d hold, Box targetBoxAt
      ) {
         this(targetFeetAt, base, obsidianCell, trustObsidian, face, breakNow, hold, targetBoxAt, null, null);
      }
   }

   public record PearlShot(float yaw, float pitch, Vec3d landing, int flightTicks, double acceptRadius, long validUntil, String why) {
   }

   public static enum Proposal {
      NONE,
      OPEN,
      ANCHOR,
      PURSUE;
   }

   public record Stand(
      CenterVecData.StandKind kind, Vec3d feet, double tolerance, boolean sprint, boolean noJump, long wtapTick, Vec3d attackAt, PreferAnchorData.Kind goal
   ) {
      public Stand(CenterVecData.StandKind kind, Vec3d sprint, double feet, boolean noJump, boolean tolerance, long wtapTick) {
         this(kind, sprint, feet, noJump, tolerance, wtapTick, null, null);
      }
   }

   public static enum StandKind {
      UNDER_TARGET,
      BELOW_LAYER,
      FOLLOW,
      LINE_UP,
      HOLD;
   }

   public static enum State {
      IDLE,
      OPENER,
      LAUNCH,
      JUGGLE,
      FOLLOW,
      PEARL_FOLLOW,
      ANCHOR_BELOW,
      RECOMBO,
      FINISHER,
      HIT_LOOP;
   }

   public record Strike(int weaponSlot, boolean sprintHit, float launchYaw, float yawTolerance, boolean requireGrounded, boolean tempo, Vec3d aimPoint) {
      public Strike(int sprintHit, boolean yawTolerance, float weaponSlot, float launchYaw, boolean requireGrounded) {
         this(sprintHit, yawTolerance, weaponSlot, launchYaw, requireGrounded, false, null);
      }
   }
}
