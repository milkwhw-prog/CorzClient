package com.corz.client.categories.visuals;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.VectorDotUtils;
import com.corz.client.render.VertexBuilderUtils;
import com.corz.client.render.VertexConsumerUtilsVector;
import it.unimi.dsi.fastutil.bytes.ByteArrayList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.EnchantingTableBlockEntity;
import net.minecraft.block.entity.EnderChestBlockEntity;
import net.minecraft.block.entity.FurnaceBlockEntity;
import net.minecraft.block.entity.MobSpawnerBlockEntity;
import net.minecraft.block.entity.PistonBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.block.entity.TrappedChestBlockEntity;
import net.minecraft.client.render.Camera;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;
import ai.auth.Module;
import com.corz.client.utils.ChunkDidnUtils;
import com.corz.client.utils.IconsPngType;

public class StorageESP extends Module {
   private final ByteArrayList byteArrayList;
   private final BooleanSetting booleanBase;
   private final BooleanSetting tracers;
   private static final int[] storageEspTable = new int[]{
      getValue(200, 91, 0),
      getValue(156, 91, 0),
      getValue(117, 0, 255),
      getValue(138, 126, 166),
      getValue(134, 0, 158),
      getValue(125, 125, 125),
      getValue(255, 140, 140),
      getValue(80, 80, 255),
      getValue(35, 226, 0)
   };
   private final BooleanSetting spawners;
   private final BooleanSetting enderChests;
   private final List<BlockEntity> pistonsTablesItems;
   private boolean storageEspActive;
   private final BooleanSetting shulkerBoxes;
   private final BooleanSetting barrels;
   private final BooleanSetting chests;
   private int totalStorageCount;
   private int iIiCount;
   private final IntegerSetting integerHelper = new IntegerSetting("Alpha", "Transparency of the ESP boxes", 125, 1, 255);
   private final BooleanSetting pistons;
   private final BooleanSetting enchantingTables;

   private static int getValue(int storageEspValue, int storageEspValue2, int storageEspValue3) {
      return storageEspValue << 16 | storageEspValue2 << 8 | storageEspValue3;
   }

   @Override
   protected void onRender3D(QueueCommand queueCommand) {
      if (mc.world != null && this.totalStorageCount != 0) {
         Camera camera = VertexConsumerUtilsVector.getCamera();
         if (camera != null) {
            Vec3d cameraPos = VertexConsumerUtilsVector.getCameraPos();
            MatrixStack matrices = queueCommand.matrices;
            matrices.push();
            matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
            Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
            float storageEspValue = this.integerHelper.getValue() / 255.0F;
            boolean booleanValue = this.tracers.isEnabled();
            boolean storageEspFlag = this.storageEspActive;
            double maxAbs = Math.max(Math.abs(cameraPos.x), Math.max(Math.abs(cameraPos.y), Math.abs(cameraPos.z)));
            int storageEspCounter = 0;
            int index = 0;

            for (int i = this.pistonsTablesItems.size(); index < i; index++) {
               BlockEntity pistonstablesitem = this.pistonsTablesItems.get(index);
               byte byte2 = this.byteArrayList.getByte(index);
               if (!pistonstablesitem.isRemoved() && this.canBase(byte2)) {
                  storageEspCounter++;
                  BlockPos pos = pistonstablesitem.getPos();
                  int x = pos.getX();
                  int y = pos.getY();
                  int z = pos.getZ();
                  int storageEspValue2 = storageEspTable[byte2];
                  if (!storageEspFlag || !canFlag(x, y, z, cameraPos, maxAbs)) {
                     VertexBuilderUtils.handle(
                        positionMatrix,
                        x + 0.1F,
                        y + 0.05F,
                        z + 0.1F,
                        x + 0.9F,
                        y + 0.85F,
                        z + 0.9F,
                        (storageEspValue2 >> 16 & 0xFF) / 255.0F,
                        (storageEspValue2 >> 8 & 0xFF) / 255.0F,
                        (storageEspValue2 & 0xFF) / 255.0F,
                        storageEspValue
                     );
                  }

                  if (booleanValue) {
                     VectorDotUtils.handle(x + 0.5, y + 0.5, z + 0.5, 0xFF000000 | storageEspValue2, 1.0F);
                  }
               }
            }

            matrices.pop();
            VertexBuilderUtils.updateDrawImmediate();
            if (!this.storageEspActive) {
               this.storageEspActive = true;
            }
         }
      }
   }

   public StorageESP() {
      super("StorageESP", IconsPngType.VISUAL);
      this.tracers = new BooleanSetting("Tracers", "Draws a line from your crosshair to the storage block", false);
      this.chests = new BooleanSetting("Chests", "Show chests and trapped chests", true);
      this.enderChests = new BooleanSetting("Ender Chests", "Show ender chests", true);
      this.spawners = new BooleanSetting("Spawners", "Show mob spawners", true);
      this.shulkerBoxes = new BooleanSetting("Shulker Boxes", "Show shulker boxes", true);
      this.booleanBase = new BooleanSetting("Furnaces", "Show furnaces", true);
      this.barrels = new BooleanSetting("Barrels", "Show barrels", true);
      this.enchantingTables = new BooleanSetting("Enchanting Tables", "Show enchanting tables", true);
      this.pistons = new BooleanSetting("Pistons", "Show pistons", true);
      this.pistonsTablesItems = new ArrayList<>();
      this.byteArrayList = new ByteArrayList();
      this.addSettings(
         new ConfigSetting[]{
            this.integerHelper,
            this.tracers,
            this.chests,
            this.enderChests,
            this.spawners,
            this.shulkerBoxes,
            this.booleanBase,
            this.barrels,
            this.enchantingTables,
            this.pistons
         }
      );
   }

   @Override
   protected void onReset() {
      if (mc.world != null && mc.player != null) {
         if (this.iIiCount > 0) {
            this.iIiCount--;
         } else {
            this.iIiCount = 4;
            this.pistonsTablesItems.clear();
            this.byteArrayList.clear();
            this.totalStorageCount = 0;

            for (WorldChunk worldChunk : ChunkDidnUtils.getFilter().toList()) {
               var collection = worldChunk.getBlockEntities().values();
               this.totalStorageCount = this.totalStorageCount + collection.size();

               for (BlockEntity blockEntity : collection) {
                  int iIiLimit = getValue(blockEntity);
                  if (iIiLimit >= 0) {
                     this.pistonsTablesItems.add(blockEntity);
                     this.byteArrayList.add((byte)iIiLimit);
                  }
               }
            }
         }
      }
   }

   private static boolean canFlag(int storageEspValue, int y, int z, Vec3d vec, double storageEspBound) {
      double vectorValue = storageEspValue + 0.5;
      double cameraMaxOffset = y + 0.45;
      double spotValue = z + 0.5;
      double storageEspFactor = vectorValue - vec.x;
      double storageEspFactor2 = cameraMaxOffset - vec.y;
      double storageEspFactor3 = spotValue - vec.z;
      float distance = (float)Math.sqrt(storageEspFactor * storageEspFactor + storageEspFactor2 * storageEspFactor2 + storageEspFactor3 * storageEspFactor3);
      double max6 = Math.max(storageEspBound, Math.max(Math.abs(vectorValue), Math.max(Math.abs(cameraMaxOffset), Math.abs(spotValue))) + 1.0);
      float height = 0.7F + 0.52F * (distance + 0.7F) + 0.05F + 4.0F * Math.ulp((float)max6);
      return VertexBuilderUtils.canVector(vectorValue, cameraMaxOffset, spotValue, height);
   }

   @Override
   protected void onDisable() {
      this.pistonsTablesItems.clear();
      this.byteArrayList.clear();
      this.totalStorageCount = 0;
      this.iIiCount = 0;
   }

   private boolean canBase(int storageEspValue) {
      return switch (storageEspValue) {
         case 0, 1 -> this.chests.isEnabled();
         case 2 -> this.enderChests.isEnabled();
         case 3 -> this.spawners.isEnabled();
         case 4 -> this.shulkerBoxes.isEnabled();
         case 5 -> this.booleanBase.isEnabled();
         case 6 -> this.barrels.isEnabled();
         case 7 -> this.enchantingTables.isEnabled();
         case 8 -> this.pistons.isEnabled();
         default -> false;
      };
   }

   private static int getValue(BlockEntity blockEntity) {
      if (blockEntity instanceof TrappedChestBlockEntity) {
         return 0;
      } else if (blockEntity instanceof ChestBlockEntity) {
         return 1;
      } else if (blockEntity instanceof EnderChestBlockEntity) {
         return 2;
      } else if (blockEntity instanceof MobSpawnerBlockEntity) {
         return 3;
      } else if (blockEntity instanceof ShulkerBoxBlockEntity) {
         return 4;
      } else if (blockEntity instanceof FurnaceBlockEntity) {
         return 5;
      } else if (blockEntity instanceof BarrelBlockEntity) {
         return 6;
      } else if (blockEntity instanceof EnchantingTableBlockEntity) {
         return 7;
      } else {
         return blockEntity instanceof PistonBlockEntity ? 8 : -1;
      }
   }
}
