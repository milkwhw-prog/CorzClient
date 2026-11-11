package com.corz.client.world;

import com.google.gson.JsonObject;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public final class RoleDimension {
   public transient long overworldMinecraftTime;
   public int intValue;
   public transient long overworldMinecraftTimeOverwo;
   public int asInt;
   public String dimension = "minecraft:overworld";
   public int asIntRoleDimension;
   public transient boolean outputWaitingFlag;
   public RoleDimension.Role role = RoleDimension.Role.ANY;

   public boolean isRoleEmeralds() {
      return this.role == RoleDimension.Role.EMERALDS || this.role == RoleDimension.Role.ANY;
   }

   public RoleDimension() {
      this.overworldMinecraftTimeOverwo = -4611686018427387904L;
      this.overworldMinecraftTime = -4611686018427387904L;
   }

   public String formatRole() {
      return this.role + " @ " + this.asIntRoleDimension + " " + this.intValue + " " + this.asInt;
   }

   public void handleOverworldOutput(boolean outputWaitingFlag, long overworldMinecraftTime) {
      this.outputWaitingFlag = outputWaitingFlag;
      if (outputWaitingFlag) {
         this.overworldMinecraftTimeOverwo = overworldMinecraftTime;
      }
   }

   public static RoleDimension getRoleDimension(JsonObject json) {
      RoleDimension roleDimension = new RoleDimension();

      try {
         roleDimension.asIntRoleDimension = json.get("x").getAsInt();
         roleDimension.intValue = json.get("y").getAsInt();
         roleDimension.asInt = json.get("z").getAsInt();
         if (json.has("dimension")) {
            roleDimension.dimension = json.get("dimension").getAsString();
         }

         if (json.has("role")) {
            roleDimension.role = RoleDimension.Role.valueOf(json.get("role").getAsString());
         }
      } catch (Exception e) {
      }

      return roleDimension;
   }

   public JsonObject getPropertyRole() {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("x", this.asIntRoleDimension);
      jsonObject.addProperty("y", this.intValue);
      jsonObject.addProperty("z", this.asInt);
      jsonObject.addProperty("dimension", this.dimension);
      jsonObject.addProperty("role", this.role.name());
      return jsonObject;
   }

   public boolean isRoleMaterials() {
      return this.role == RoleDimension.Role.MATERIALS || this.role == RoleDimension.Role.ANY;
   }

   public Vec3d getVec3d() {
      return new Vec3d(this.asIntRoleDimension + 0.5, this.intValue + 0.5, this.asInt + 0.5);
   }

   public boolean isRoleOutputs() {
      return this.role == RoleDimension.Role.OUTPUTS || this.role == RoleDimension.Role.ANY;
   }

   public BlockPos getBlockPos() {
      return new BlockPos(this.asIntRoleDimension, this.intValue, this.asInt);
   }

   public RoleDimension(BlockPos pos2, String dimension2, RoleDimension.Role role2) {
      this.overworldMinecraftTimeOverwo = -4611686018427387904L;
      this.overworldMinecraftTime = -4611686018427387904L;
      this.asIntRoleDimension = pos2.getX();
      this.intValue = pos2.getY();
      this.asInt = pos2.getZ();
      this.dimension = dimension2;
      this.role = role2;
   }

   public boolean canDimension(String dimensionText) {
      return this.dimension != null && this.dimension.equals(dimensionText);
   }

   public static enum Role {
      MATERIALS,
      OUTPUTS,
      EMERALDS,
      ANY,
      DROP;
   }
}
