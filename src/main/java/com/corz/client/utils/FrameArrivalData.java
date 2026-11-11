package com.corz.client.utils;

import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.BackTrackUtils;

public record FrameArrivalData(int id, Vec3d base, Vec3d pos, long arrivalFrame) {
   public FrameArrivalData getPositionPosData(Vec3d frame, long position) {
      return new FrameArrivalData(this.id, frame, frame, position);
   }

   public static FrameArrivalData getPositionPosData(int frame, long id) {
      return new FrameArrivalData(frame, null, null, id);
   }

   public FrameArrivalData getBase(Vec3d frame, long position) {
      return new FrameArrivalData(this.id, this.base, frame, position);
   }

   public FrameArrivalData getArrivalFrame(Vec3d newPos, Vec3d newBase) {
      return new FrameArrivalData(this.id, newPos, newBase, this.arrivalFrame);
   }

   public boolean isBasePos() {
      return this.base != null && this.pos != null;
   }

   public FrameArrivalData getBase(long dx, long dy, long frame, long dz) {
      if (this.base == null) {
         return new FrameArrivalData(this.id, null, null, dz);
      } else {
         Vec3d vec3d = BackTrackUtils.getVec3d(this.base, dx, dy, frame);
         return new FrameArrivalData(this.id, vec3d, vec3d, dz);
      }
   }
}
