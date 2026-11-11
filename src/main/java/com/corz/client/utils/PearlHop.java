package com.corz.client.utils;

import com.corz.client.entity.DeflectZone;
import com.corz.client.entity.FloorMathUtilsDirection;
import com.corz.client.inventory.CooldownEscape;
import com.corz.client.inventory.InventoryStackUtilsHandle;
import com.corz.client.world.CenterVecData;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.HotbarSlotUtils;
import com.corz.client.utils.McCells;
import com.corz.client.utils.HunterBody;
import com.corz.client.utils.AnchorBreaker;
import com.corz.client.utils.McViews;
import com.corz.client.utils.Cells;
import com.corz.client.utils.StandFinder;

public final class PearlHop {
   private PearlHop.Shot currentShot;
   private long cooldownUntilTick;
   private final McCells mcCells;
   private String aiming;
   private final HunterBody hunterBody;
   private long lastNoShotTick;
   private long pearlThrownTick;
   private Vec3d throwPos;
   private Vec3d failedAimPos;
   public static final String ANCHOR_HUNTER = "anchor-hunter";
   private boolean swapPending;
   private int throwCount;
   private long aimStartTick;
   private final MinecraftClient client = MinecraftClient.getInstance();
   private PearlHop.State hopState = PearlHop.State.IDLE;
   private final Object inventoryContext;

   public PearlHop.State getState() {
      return this.hopState;
   }

   static int findNonPickaxeHotbarSlot(PlayerInventory playerInventory) {
      for (int i = 8; i >= 0; i--) {
         if (!AnchorBreaker.isUsableMiningPickaxe(playerInventory.getStack(i)) && i != playerInventory.getSelectedSlot()) {
            return i;
         }
      }

      return -1;
   }

   public void onPearlThrown(long flyingTime) {
      ClientPlayerEntity player = this.client.player;
      this.hopState = PearlHop.State.FLYING;
      this.pearlThrownTick = flyingTime;
      this.throwPos = player == null ? Vec3d.ZERO : player.getEntityPos();
      this.throwCount++;
      this.aiming = "flying";
   }

   public PearlHop(HunterBody hunterBody, McCells mcCells, Object object) {
      this.aiming = "";
      this.hunterBody = hunterBody;
      this.mcCells = mcCells;
      this.inventoryContext = object;
   }

   public boolean canThrowPearl(ClientPlayerEntity player) {
      if (player == null || !hasEnoughArmor(player) || !hasEnoughHealth(player)) {
         return false;
      } else if (player.isOnGround() && !player.isTouchingWater() && !player.isUsingItem() && !player.isSneaking()) {
         return player.getItemCooldownManager().isCoolingDown(new ItemStack(Items.ENDER_PEARL)) ? false : findInventoryPearlSlot(player.getInventory()) >= 0;
      } else {
         return false;
      }
   }

   private PearlHop.Act tickAiming(long pearlReachTime, ClientPlayerEntity player) {
      if (pearlReachTime - this.aimStartTick > 40L) {
         this.abortAiming(pearlReachTime, "aim timeout");
         return PearlHop.Act.IDLE;
      } else if (!this.canThrowPearl(player)) {
         this.abortAiming(pearlReachTime, "not ready");
         return PearlHop.Act.IDLE;
      } else {
         this.hunterBody.rotateTowardYaw(this.currentShot.yaw(), this.currentShot.pitch(), 2);
         PlayerInventory inventory = player.getInventory();
         int enderStack = findHotbarPearlSlot(inventory);
         if (enderStack < 0) {
            if (!this.tryMovePearlToHotbar(player, inventory)) {
               this.failedAimPos = player.getEntityPos();
               this.lastNoShotTick = pearlReachTime;
               this.abortAiming(pearlReachTime, "no pearl in reach");
            }

            return PearlHop.Act.BUSY;
         } else {
            this.hunterBody.selectHotbarSlot(enderStack);
            if (!this.hunterBody.isSlotSelectedAndSettled(enderStack)) {
               return PearlHop.Act.BUSY;
            } else if (Math.abs(player.getX() - player.lastX) > 0.003 || Math.abs(player.getZ() - player.lastZ) > 0.003) {
               return PearlHop.Act.BUSY;
            } else if (!(Math.abs(MathHelper.wrapDegrees(this.currentShot.yaw() - player.getYaw())) > 0.75F) && !(Math.abs(this.currentShot.pitch() - player.getPitch()) > 0.75F)) {
               FloorMathUtilsDirection.Result ground = this.simulateThrow(player, player.getYaw(), player.getPitch(), true);
               if (ground != null
                  && ground.entityValue < 0
                  && ground.movedFlag
                  && ground.vec3d3VecMoved != null
                  && CooldownEscape.canDistance(ground.vec3d3VecMoved, this.currentShot.landing(), 2.0)
                  && this.findStandPosNear(ground.vec3d3VecMoved) != null) {
                  return PearlHop.Act.THROW;
               } else {
                  this.abortAiming(pearlReachTime, "landing moved");
                  return PearlHop.Act.IDLE;
               }
            } else {
               return PearlHop.Act.BUSY;
            }
         }
      }
   }

   public int getThrowCount() {
      return this.throwCount;
   }

   public PearlHop.Shot getShot() {
      return this.currentShot;
   }

   private List<BlockPos> findFallbackLandingSpots(Vec3d vec, BlockPos pos, double floorFactor) {
      ArrayList arrayList = new ArrayList();
      if (!(floorFactor < 10.0) && this.client.world != null) {
         double floorValue = pos.getX() + 0.5 - vec.x;
         double deltaZ = pos.getZ() + 0.5 - vec.z;
         double hypot = Math.hypot(floorValue, deltaZ);
         if (hypot < 0.001) {
            return arrayList;
         } else {
            int floor = MathHelper.floor(vec.x + floorValue / hypot * floorFactor);
            int floor4 = MathHelper.floor(vec.z + deltaZ / hypot * floorFactor);
            ArrayList arrayList2 = new ArrayList();

            for (int i = -3; i <= 3; i++) {
               for (int j = -3; j <= 3; j++) {
                  if (i * i + j * j <= 9) {
                     arrayList2.add(new int[]{i, j});
                  }
               }
            }

            arrayList2.sort((obj, obj2) -> Integer.compare(((int[]) obj)[0] * ((int[]) obj)[0] + ((int[]) obj)[1] * ((int[]) obj)[1], ((int[]) obj2)[0] * ((int[]) obj2)[0] + ((int[]) obj2)[1] * ((int[]) obj2)[1]));

            for (int[] floorValues : (Iterable<int[]>) (arrayList2)) {
               if (arrayList.size() >= 3) {
                  break;
               }

               BlockPos chunkTop = McViews.getSurfaceStandPos(this.mcCells, this.client.world, floor + floorValues[0], floor4 + floorValues[1]);
               if (chunkTop != null) {
                  arrayList.add(chunkTop);
               }
            }

            return arrayList;
         }
      } else {
         return arrayList;
      }
   }

   private PearlHop.Shot planShot(long hunterLandingValue, ClientPlayerEntity player, BlockPos pos, List<BlockPos> list) {
      Vec3d entityPos = player.getEntityPos();
      double horizontalDistanceTo2 = horizontalDistanceTo(entityPos, pos);
      if (horizontalDistanceTo2 < 18.0) {
         return null;
      } else {
         ArrayList arrayList = new ArrayList();
         if (horizontalDistanceTo2 <= 42.0) {
            for (BlockPos blockPos : list) {
               if (arrayList.size() >= 3) {
                  break;
               }

               if (blockPos != null && horizontalDistanceTo(entityPos, blockPos) <= 42.0) {
                  arrayList.add(blockPos);
               }
            }
         }

         if (arrayList.isEmpty()) {
            arrayList.addAll(this.findFallbackLandingSpots(entityPos, pos, Math.min(34.0, horizontalDistanceTo2 - 10.0)));
         }

         if (arrayList.isEmpty()) {
            return null;
         } else {
            Vec3d eye = FloorMathUtilsDirection.getEye(player);
            double[] doubles = new double[]{0.0, 0.0, 0.0};
            PearlHop.Shot hunterLanding = null;
            double negativeInfinity = Double.NEGATIVE_INFINITY;

            for (BlockPos blockPos2 : (Iterable<net.minecraft.util.math.BlockPos>) (arrayList)) {
               Vec3d bottomCenter = Vec3d.ofBottomCenter(blockPos2);
               float long2floatopenhashmap = FloorMathUtilsDirection.getAmount(eye, bottomCenter, 1.5, 0.03);
               CenterVecData.PearlShot vecDistance = DeflectZone.getVecDistance(eye, doubles, bottomCenter, 90, 2.0, hunterLandingValue, "anchor-hunter", -55.0, 45.0, long2floatopenhashmap);
               if (vecDistance != null && vecDistance.landing() != null) {
                  BlockPos cellsFloored = this.findStandPosNear(vecDistance.landing());
                  if (cellsFloored != null) {
                     double hunterLandingValue5 = horizontalDistanceTo2 - horizontalDistanceTo(Vec3d.ofBottomCenter(cellsFloored), pos);
                     if (!(hunterLandingValue5 < 10.0)) {
                        FloorMathUtilsDirection.Result ground = this.simulateThrow(player, vecDistance.yaw(), vecDistance.pitch(), true);
                        if (ground != null && ground.entityValue < 0 && ground.vec3d3VecMoved != null && CooldownEscape.canDistance(ground.vec3d3VecMoved, vecDistance.landing(), 2.0)) {
                           double hunterLandingLimit = hunterLandingValue5 - 0.05 * vecDistance.flightTicks();
                           if (hunterLandingLimit > negativeInfinity) {
                              negativeInfinity = hunterLandingLimit;
                              hunterLanding = new PearlHop.Shot(vecDistance.yaw(), vecDistance.pitch(), vecDistance.landing(), cellsFloored, vecDistance.flightTicks(), List.copyOf(ground.vecVec3dList));
                           }
                        }
                     }
                  }
               }
            }

            return hunterLanding;
         }
      }
   }

   private FloorMathUtilsDirection.Result simulateThrow(ClientPlayerEntity player, float groundValue, float groundValue2, boolean groundFlag) {
      double deflectValue = player.getX() - player.lastX;
      double groundValue4 = player.getZ() - player.lastZ;
      double groundValue5 = player.isOnGround() ? 0.0 : player.getY() - player.lastY;
      double[] groundValues = DeflectAmountsUtils.getAmounts(groundValue, groundValue2, deflectValue, groundValue5, groundValue4);
      return FloorMathUtilsDirection.getBoundingBox(
         FloorMathUtilsDirection.getEye(player), new Vec3d(groundValues[0], groundValues[1], groundValues[2]), false, 0, null, player, groundFlag, 200
      );
   }

   BlockPos findStandPosNear(Vec3d vec) {
      BlockPos floored = BlockPos.ofFloored(vec.x, vec.y + 0.01, vec.z);
      if (Cells.hasAnyFlag(this.mcCells.getCellFlags(floored.getX(), floored.getY(), floored.getZ()), 1)) {
         floored = floored.up();
      }

      int cellsFlooredLimit = StandFinder.measureFallDepth(this.mcCells, floored.getX(), floored.getY(), floored.getZ(), 4);
      if (cellsFlooredLimit > 3) {
         return null;
      } else {
         BlockPos down = floored.down(cellsFlooredLimit);
         return McViews.isSafeStandSpot(this.mcCells, down.getX(), down.getY(), down.getZ()) ? down : null;
      }
   }

   static int findHotbarPearlSlot(PlayerInventory playerInventory) {
      for (int i = 0; i <= 8; i++) {
         if (playerInventory.getStack(i).isOf(Items.ENDER_PEARL)) {
            return i;
         }
      }

      return -1;
   }

   private boolean tryMovePearlToHotbar(ClientPlayerEntity player, PlayerInventory playerInventory) {
      if (!this.swapPending && !ExtUtils.isAction()) {
         if (player.currentScreenHandler != player.playerScreenHandler) {
            return false;
         } else {
            for (int i = 9; i <= 35; i++) {
               if (playerInventory.getStack(i).isOf(Items.ENDER_PEARL)) {
                  int stackPickaxes = AnchorBreaker.findFreeHotbarSlot(playerInventory);
                  if (AnchorBreaker.isUsableMiningPickaxe(playerInventory.getStack(stackPickaxes))) {
                     stackPickaxes = findNonPickaxeHotbarSlot(playerInventory);
                  }

                  if (stackPickaxes < 0) {
                     return false;
                  }

                  if (!InventoryStackUtilsHandle.canFlagForObject(this.inventoryContext)) {
                     return true;
                  }

                  int stackValue = i;
                  int elytraAnchorValue = stackPickaxes;
                  ExtUtils.handleExt(
                     () -> {
                        ClientPlayerEntity var3x = this.client.player;
                        if (var3x != null && this.client.interactionManager != null && var3x.currentScreenHandler == var3x.playerScreenHandler) {
                           if (var3x.getInventory().getStack(stackValue).isOf(Items.ENDER_PEARL)) {
                              if (!AnchorBreaker.isUsableMiningPickaxe(var3x.getInventory().getStack(elytraAnchorValue))) {
                                 this.client
                                    .interactionManager
                                    .clickSlot(
                                       var3x.playerScreenHandler.syncId,
                                       HotbarSlotUtils.toScreenSlot(stackValue),
                                       HotbarSlotUtils.requireHotbarSlot(elytraAnchorValue),
                                       SlotActionType.SWAP,
                                       var3x
                                    );
                              }
                           }
                        }
                     },
                     true
                  );
                  this.swapPending = true;
                  return true;
               }
            }

            return false;
         }
      } else {
         return true;
      }
   }

   static int findInventoryPearlSlot(PlayerInventory playerInventory) {
      for (int i = 0; i <= 35; i++) {
         if (playerInventory.getStack(i).isOf(Items.ENDER_PEARL)) {
            return i;
         }
      }

      return -1;
   }

   public static boolean hasEnoughHealth(ClientPlayerEntity player) {
      return player != null && player.getHealth() + player.getAbsorptionAmount() >= 12.0F;
   }

   private static double horizontalDistanceTo(Vec3d vec, BlockPos pos) {
      return Math.hypot(pos.getX() + 0.5 - vec.x, pos.getZ() + 0.5 - vec.z);
   }

   public static boolean hasEnoughArmor(ClientPlayerEntity player) {
      return player != null && player.getArmor() >= 12;
   }

   public String getAimStatus() {
      return this.aiming;
   }

   public void reset() {
      this.hopState = PearlHop.State.IDLE;
      this.currentShot = null;
      this.throwCount = 0;
      this.cooldownUntilTick = -2305843009213693952L;
      this.failedAimPos = null;
      this.swapPending = false;
      this.aiming = "";
   }

   public void abortAiming(long aimingValue, String aiming) {
      if (this.hopState != PearlHop.State.FLYING) {
         this.hopState = PearlHop.State.IDLE;
         this.cooldownUntilTick = aimingValue + 60L;
         this.aiming = aiming;
      }
   }

   public boolean isBusy() {
      return this.hopState != PearlHop.State.IDLE;
   }

   public PearlHop.Act updateAndGetAct(long pearlReachTime, BlockPos pos, Supplier<List<BlockPos>> supplier) {
      ClientPlayerEntity player = this.client.player;
      if (player != null && this.client.world != null) {
         if (this.swapPending && !ExtUtils.isAction()) {
            this.swapPending = false;
         }

         if (pos == null && this.hopState != PearlHop.State.FLYING) {
            if (this.hopState == PearlHop.State.AIM) {
               this.abortAiming(pearlReachTime, "no target");
            }

            return PearlHop.Act.IDLE;
         } else {
            switch (this.hopState) {
               case AIM:
                  return this.tickAiming(pearlReachTime, player);
               case FLYING:
                  if (!(player.getEntityPos().distanceTo(this.throwPos) > 3.0) && pearlReachTime - this.pearlThrownTick <= this.currentShot.flight() + 30) {
                     return PearlHop.Act.BUSY;
                  }

                  this.hopState = PearlHop.State.IDLE;
                  this.cooldownUntilTick = pearlReachTime + 20L;
                  return PearlHop.Act.IDLE;
               default:
                  if (pearlReachTime >= this.cooldownUntilTick && this.throwCount < 6 && this.canThrowPearl(player)) {
                     if (this.failedAimPos != null && player.getEntityPos().distanceTo(this.failedAimPos) < 6.0 && pearlReachTime - this.lastNoShotTick < 100L) {
                        return PearlHop.Act.IDLE;
                     } else {
                        this.cooldownUntilTick = pearlReachTime + 20L;
                        PearlHop.Shot shot = this.planShot(pearlReachTime, player, pos, supplier == null ? List.of() : (List)supplier.get());
                        if (shot == null) {
                           this.failedAimPos = player.getEntityPos();
                           this.lastNoShotTick = pearlReachTime;
                           return PearlHop.Act.IDLE;
                        } else {
                           this.failedAimPos = null;
                           this.currentShot = shot;
                           this.hopState = PearlHop.State.AIM;
                           this.aimStartTick = pearlReachTime;
                           this.aiming = "aiming";
                           return this.tickAiming(pearlReachTime, player);
                        }
                     }
                  } else {
                     return PearlHop.Act.IDLE;
                  }
            }
         }
      } else {
         return PearlHop.Act.IDLE;
      }
   }

   public static enum Act {
      IDLE,
      BUSY,
      THROW;
   }

   public record Shot(float yaw, float pitch, Vec3d landing, BlockPos stand, int flight, List<Vec3d> arc) {
   }

   public static enum State {
      IDLE,
      AIM,
      FLYING;
   }
}
