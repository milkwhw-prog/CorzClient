package com.corz.client.categories.combat;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.DoubleSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.config.FriendNameStore;
import com.corz.client.render.MatrixEspUtils;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.VertexBuilderUtils;
import com.corz.client.render.VertexConsumerUtilsVector;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.render.Camera;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket.LookAndOnGround;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import com.corz.client.categories.visuals.BlockOverlay;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class AutoTrap extends Module {
   private final BooleanSetting placeTopBlock;
   private final DoubleSetting rangeSetting = new DoubleSetting("Range", "Max distance to target player", 4.0, 1.0, 6.0, 0.1);
   private final BooleanSetting hitMarker;
   private PlayerEntity targetPlayer;
   private final BooleanSetting rotateToPlacement;
   private final DoubleSetting hitboxExpand;
   private final IntegerSetting blocksPerTick = new IntegerSetting("Blocks/Tick", "How many blocks to place per tick", 4, 1, 8);

   @Override
   protected void onDisable() {
      this.targetPlayer = null;
   }

   @Override
   protected void onRender3D(QueueCommand queueCommand) {
      if (this.hitMarker.isEnabled()) {
         if (mc.player != null && mc.world != null) {
            if (this.targetPlayer != null && !this.targetPlayer.isDead() && !this.targetPlayer.isRemoved()) {
               var lerpBlock = this.getOpenMarkerPositions(this.targetPlayer);
               if (!lerpBlock.isEmpty()) {
                  Camera camera = mc.getEntityRenderDispatcher().camera;
                  if (camera != null) {
                     Vec3d cameraPos = camera.getCameraPos();
                     int releaseRightValue = MatrixEspUtils.releaseRightValue;
                     int red = releaseRightValue >> 16 & 0xFF;
                     int green = releaseRightValue >> 8 & 0xFF;
                     int blue = releaseRightValue & 0xFF;
                     Color color = new Color(red, green, blue, 220);

                     for (Vec3d vec3d : lerpBlock) {
                        float autoTrapValue = 0.12F;
                        VertexConsumerUtilsVector.handlePeekPosition(
                           queueCommand.matrices,
                           (float)(vec3d.x - autoTrapValue - cameraPos.x),
                           (float)(vec3d.y - autoTrapValue - cameraPos.y),
                           (float)(vec3d.z - autoTrapValue - cameraPos.z),
                           (float)(vec3d.x + autoTrapValue - cameraPos.x),
                           (float)(vec3d.y + autoTrapValue - cameraPos.y),
                           (float)(vec3d.z + autoTrapValue - cameraPos.z),
                           color
                        );
                        float autoTrapValue2 = autoTrapValue + 0.01F;
                        Box box = new Box(vec3d.x - autoTrapValue2, vec3d.y - autoTrapValue2, vec3d.z - autoTrapValue2, vec3d.x + autoTrapValue2, vec3d.y + autoTrapValue2, vec3d.z + autoTrapValue2);
                        BlockOverlay.handlePeekPosition(queueCommand.matrices, box, -1, 2.0);
                     }

                     VertexBuilderUtils.updateDrawImmediate();
                  }
               }
            }
         }
      }
   }

   @Override
   protected void onEnable() {
      this.targetPlayer = null;
   }

   private List<Vec3d> getOpenMarkerPositions(PlayerEntity player) {
      var arrayList = new ArrayList();
      float tickProgress = mc.getRenderTickCounter().getTickProgress(true);
      double lerp = MathHelper.lerp((double)tickProgress, player.lastX, player.getX());
      double lerp2 = MathHelper.lerp((double)tickProgress, player.lastY, player.getY());
      double lerp3 = MathHelper.lerp((double)tickProgress, player.lastZ, player.getZ());
      BlockPos blockPos = player.getBlockPos();
      double widthScaled = player.getWidth() / 2.0;
      int[][] ints = new int[][]{{0, -1}, {0, 1}, {1, 0}, {-1, 0}, {1, -1}, {-1, -1}, {1, 1}, {-1, 1}};

      for (int i = 0; i <= 1; i++) {
         for (int[] lerpBlockValues : ints) {
            BlockPos add = blockPos.add(lerpBlockValues[0], i, lerpBlockValues[1]);
            BlockState blockState = mc.world.getBlockState(add);
            if (blockState.isReplaceable() || blockState.isAir()) {
               double x = lerp + (lerpBlockValues[0] != 0 ? Math.signum((float)lerpBlockValues[0]) * widthScaled : 0.0);
               double y = lerp2 + i + 0.5;
               double z = lerp3 + (lerpBlockValues[1] != 0 ? Math.signum((float)lerpBlockValues[1]) * widthScaled : 0.0);
               arrayList.add(new Vec3d(x, y, z));
            }
         }
      }

      BlockPos up = blockPos.up(2);
      if (mc.world.getBlockState(up).isReplaceable() || mc.world.getBlockState(up).isAir()) {
         arrayList.add(new Vec3d(lerp, lerp2 + player.getHeight(), lerp3));
      }

      return arrayList;
   }

   public double getHitboxExpand() {
      return this.hitboxExpand.getValue();
   }

   public AutoTrap() {
      super("AutoTrap", IconsPngType.COMBAT);
      this.placeTopBlock = new BooleanSetting("Top Block", "Place obsidian above the target's head", true);
      this.rotateToPlacement = new BooleanSetting("Rotate", "Rotate towards placement position", true);
      this.hitMarker = new BooleanSetting("Hit Marker", "Show marker where the target's hitbox is exposed", true);
      this.hitboxExpand = new DoubleSetting("Hitbox Expand", "Slightly expand trapped player hitbox for easier hits", 0.08, 0.0, 0.2, 0.01);
      this.addSettings(new ConfigSetting[]{this.rangeSetting, this.blocksPerTick, this.placeTopBlock, this.rotateToPlacement, this.hitMarker, this.hitboxExpand});
   }

   private int findObsidianSlot() {
      for (int i = 0; i < 9; i++) {
         ItemStack stack = mc.player.getInventory().getStack(i);
         if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() == Blocks.OBSIDIAN) {
            return i;
         }
      }

      return -1;
   }

   private boolean tryPlaceAgainstNeighbor(BlockPos pos2) {
      for (Direction direction : Direction.values()) {
         BlockPos offset = pos2.offset(direction);
         if (!mc.world.getBlockState(offset).isAir() && !mc.world.getBlockState(offset).isReplaceable()) {
            Direction opposite = direction.getOpposite();
            Vec3d add = Vec3d.ofCenter(offset).add(opposite.getOffsetX() * 0.5, opposite.getOffsetY() * 0.5, opposite.getOffsetZ() * 0.5);
            if (this.rotateToPlacement.isEnabled()) {
               float[] eyePos = this.computeRotationToward(add);
               mc.player
                  .networkHandler
                  .sendPacket(new LookAndOnGround(eyePos[0], eyePos[1], mc.player.isOnGround(), mc.player.horizontalCollision));
            }

            BlockHitResult blockHitResult = new BlockHitResult(add, opposite, offset, false);
            mc.interactionManager.interactBlock(mc.player, Hand.MAIN_HAND, blockHitResult);
            return true;
         }
      }

      return false;
   }

   @Override
   protected void onReset() {
      if (mc.player != null && mc.world != null) {
         this.targetPlayer = this.getNearestTarget();
         if (this.targetPlayer != null) {
            int inventoryStack = this.findObsidianSlot();
            if (inventoryStack != -1) {
               var northSouth = this.getTrapPositions(this.targetPlayer);
               northSouth.removeIf(pos -> !mc.world.getBlockState(pos).isReplaceable());
               int selectedSlot = mc.player.getInventory().getSelectedSlot();
               mc.player.getInventory().setSelectedSlot(inventoryStack);
               int iIiCounter = 0;

               for (BlockPos blockPos : northSouth) {
                  if (iIiCounter >= this.blocksPerTick.getValue()) {
                     break;
                  }

                  if (this.tryPlaceAgainstNeighbor(blockPos)) {
                     iIiCounter++;
                  }
               }

               mc.player.getInventory().setSelectedSlot(selectedSlot);
            }
         }
      }
   }

   private float[] computeRotationToward(Vec3d vec) {
      Vec3d eyePos = mc.player.getEyePos();
      double deltaX = vec.x - eyePos.x;
      double deltaY = vec.y - eyePos.y;
      double deltaZ = vec.z - eyePos.z;
      double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
      float eyePosValue = (float)Math.toDegrees(Math.atan2(deltaZ, deltaX)) - 90.0F;
      float degrees = (float)(-Math.toDegrees(Math.atan2(deltaY, distance)));
      return new float[]{eyePosValue, degrees};
   }

   private PlayerEntity getNearestTarget() {
      double autoTrapLimit = this.rangeSetting.getValue();
      PlayerEntity next = null;
      double maxValue = Double.MAX_VALUE;

      for (PlayerEntity playerEntity : mc.world.getPlayers()) {
         if (playerEntity != mc.player && !playerEntity.isSpectator() && !playerEntity.isDead() && !FriendNameStore.canFlag(playerEntity)) {
            double distanceTo = mc.player.distanceTo(playerEntity);
            if (distanceTo <= autoTrapLimit && distanceTo < maxValue) {
               maxValue = distanceTo;
               next = playerEntity;
            }
         }
      }

      return next;
   }

   private List<BlockPos> getTrapPositions(PlayerEntity player) {
      BlockPos blockPos = player.getBlockPos();
      var arrayList = new ArrayList();
      arrayList.add(blockPos.north());
      arrayList.add(blockPos.south());
      arrayList.add(blockPos.east());
      arrayList.add(blockPos.west());
      BlockPos up = blockPos.up();
      arrayList.add(up.north());
      arrayList.add(up.south());
      arrayList.add(up.east());
      arrayList.add(up.west());
      if (this.placeTopBlock.isEnabled()) {
         arrayList.add(blockPos.up(2).north());
         arrayList.add(blockPos.up(2));
      }

      return arrayList;
   }
}
