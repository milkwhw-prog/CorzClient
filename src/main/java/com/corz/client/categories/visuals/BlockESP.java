package com.corz.client.categories.visuals;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.inventory.ItemList;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.VectorDotUtils;
import com.corz.client.render.VertexBuilderUtils;
import com.corz.client.render.VertexConsumerUtilsVector;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class BlockESP extends Module {
   private int scanChunkZ;
   private int blueTracersValue;
   private int iIiCount;
   private final List<BlockPos> blueTracersItems;
   private int chunkLoadedValue;
   private int tracersBlueValue;
   private final IntegerSetting integerHelper;
   private int blockEspValue;
   private int scanMaxY;
   private int chunkLoadedCount;
   private final IntegerSetting blockEspIntegerHelper;
   private int scanMaxX;
   private int startChunkX;
   private final IntegerSetting alpha;
   private final IntegerSetting greenHelper;
   private int endChunkX;
   private boolean blueTracersActive;
   private final ItemList itemList = new ItemList("Blocks", "Blocks to highlight.", List.of(Items.DIAMOND_ORE));
   private final List<BlockPos> blueChannelItems;
   private int startChunkZ;
   private final IntegerSetting blueHelper;
   private final BooleanSetting booleanBase;
   private int scanMinZ;
   private Set<Block> blueTracersSet;

   @Override
   protected void onReset() {
      if (mc.player != null && mc.world != null) {
         if (this.iIiCount > 0) {
            this.iIiCount--;
         }

         if (this.blueTracersActive) {
            this.updateChunkLoaded();
         } else if (this.iIiCount <= 0) {
            this.iIiCount = 8;
            this.updateBlueTracers();
            if (this.blueTracersActive) {
               this.updateChunkLoaded();
            }
         }
      } else {
         this.blueTracersActive = false;
      }
   }

   @Override
   protected void onRender3D(QueueCommand queueCommand) {
      if (mc.world != null && !this.blueChannelItems.isEmpty()) {
         Vec3d cameraPos = VertexConsumerUtilsVector.getCameraPos();
         MatrixStack matrices = queueCommand.matrices;
         matrices.push();
         matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
         Color color = new Color(
            getMax(this.blockEspIntegerHelper.getValue()),
            getMax(this.greenHelper.getValue()),
            getMax(this.blueHelper.getValue()),
            getMax(this.alpha.getValue())
         );
         Color color2 = new Color(color.getRed(), color.getGreen(), color.getBlue(), 255);
         boolean booleanValue = this.booleanBase.isEnabled();
         int rgb = color2.getRGB();

         for (BlockPos blockPos : this.blueChannelItems) {
            VertexConsumerUtilsVector.handlePeekPosition(
               matrices, blockPos.getX() + 0.001F, blockPos.getY() + 0.001F, blockPos.getZ() + 0.001F, blockPos.getX() + 0.999F, blockPos.getY() + 0.999F, blockPos.getZ() + 0.999F, color
            );
            if (booleanValue) {
               VectorDotUtils.handle(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5, rgb, 1.0F);
            }
         }

         matrices.pop();
         VertexBuilderUtils.updateDrawImmediate();
      }
   }

   private void updateBlueTracers() {
      var blueTracersSet = this.getAirBlock();
      if (blueTracersSet.isEmpty()) {
         this.blueChannelItems.clear();
      } else {
         int blueTracersValue = this.integerHelper.getValue();
         BlockPos blockPos = mc.player.getBlockPos();
         this.blueTracersSet = blueTracersSet;
         this.tracersBlueValue = blockPos.getX() - blueTracersValue;
         this.scanMaxX = blockPos.getX() + blueTracersValue;
         this.blockEspValue = blockPos.getY() - blueTracersValue;
         this.scanMaxY = blockPos.getY() + blueTracersValue;
         this.scanMinZ = blockPos.getZ() - blueTracersValue;
         this.blueTracersValue = blockPos.getZ() + blueTracersValue;
         this.startChunkX = this.tracersBlueValue >> 4;
         this.chunkLoadedValue = this.scanMaxX >> 4;
         this.startChunkZ = this.scanMinZ >> 4;
         this.endChunkX = this.blueTracersValue >> 4;
         this.chunkLoadedCount = this.startChunkX;
         this.scanChunkZ = this.startChunkZ;
         this.blueTracersItems.clear();
         this.blueTracersActive = true;
      }
   }

   private void updateBlueTracersBlueTracersActive() {
      this.blueTracersActive = false;
      this.blueChannelItems.clear();
      this.blueChannelItems.addAll(this.blueTracersItems);
      this.blueTracersItems.clear();
   }

   private int getTracersBlue(WorldChunk worldChunk) {
      int tracersBlue = 0;
      int startX = worldChunk.getPos().getStartX();
      int startZ = worldChunk.getPos().getStartZ();
      int tracersBlueValue = Math.max(this.tracersBlueValue, startX) & 15;
      int tracersBlueLimit = Math.min(this.scanMaxX, startX + 15) & 15;
      int tracersBlueValue2 = Math.max(this.scanMinZ, startZ) & 15;
      int tracersBlueLimit2 = Math.min(this.blueTracersValue, startZ + 15) & 15;
      ChunkSection[] sectionArray = worldChunk.getSectionArray();
      int bottomY = worldChunk.getBottomY();

      for (int i = 0; i < sectionArray.length; i++) {
         int tracersBlueBound = bottomY + (i << 4);
         if (tracersBlueBound <= this.scanMaxY && tracersBlueBound + 15 >= this.blockEspValue) {
            ChunkSection chunkSection = sectionArray[i];
            if (chunkSection != null && !chunkSection.isEmpty() && chunkSection.hasAny(state -> this.blueTracersSet.contains(state.getBlock()))) {
               tracersBlue++;
               int tracersBlueValue3 = Math.max(this.blockEspValue, tracersBlueBound) & 15;
               int tracersBlueLimit3 = Math.min(this.scanMaxY, tracersBlueBound + 15) & 15;

               for (int j = tracersBlueValue; j <= tracersBlueLimit; j++) {
                  for (int k = tracersBlueValue3; k <= tracersBlueLimit3; k++) {
                     for (int m = tracersBlueValue2; m <= tracersBlueLimit2; m++) {
                        if (this.blueTracersSet.contains(chunkSection.getBlockState(j, k, m).getBlock())) {
                           this.blueTracersItems.add(new BlockPos(startX + j, tracersBlueBound + k, startZ + m));
                           if (this.blueTracersItems.size() >= 6000) {
                              return tracersBlue;
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return tracersBlue;
   }

   public BlockESP() {
      super("BlockESP", IconsPngType.VISUAL);
      this.integerHelper = new IntegerSetting("Range", "Scan radius around you, in blocks.", 16, 1, 48);
      this.alpha = new IntegerSetting("Alpha", "Transparency of the ESP boxes.", 90, 1, 255);
      this.blockEspIntegerHelper = new IntegerSetting("Red", "Box red channel.", 0, 0, 255);
      this.greenHelper = new IntegerSetting("Green", "Box green channel.", 200, 0, 255);
      this.blueHelper = new IntegerSetting("Blue", "Box blue channel.", 255, 0, 255);
      this.booleanBase = new BooleanSetting("Tracers", "Draw a line from your crosshair to each block.", false);
      this.blueChannelItems = new ArrayList<>();
      this.blueTracersItems = new ArrayList<>();
      this.blueTracersSet = Set.of();
      this.addSettings(
         new ConfigSetting[]{
            this.itemList, this.integerHelper, this.alpha, this.blockEspIntegerHelper, this.greenHelper, this.blueHelper, this.booleanBase
         }
      );
   }

   @Override
   protected void onDisable() {
      this.blueChannelItems.clear();
      this.blueTracersItems.clear();
      this.blueTracersActive = false;
      this.iIiCount = 0;
   }

   private static int getMax(int blockEspBound) {
      return Math.max(0, Math.min(255, blockEspBound));
   }

   private void updateChunkLoaded() {
      ClientWorld world = mc.world;
      int chunkLoadedTotal = 12;

      while (this.chunkLoadedCount <= this.chunkLoadedValue) {
         if (world.isChunkLoaded(this.chunkLoadedCount, this.scanChunkZ)) {
            WorldChunk chunk = world.getChunk(this.chunkLoadedCount, this.scanChunkZ);
            if (chunk != null) {
               chunkLoadedTotal -= this.getTracersBlue(chunk);
               if (this.blueTracersItems.size() >= 6000) {
                  this.updateBlueTracersBlueTracersActive();
                  return;
               }
            }
         }

         if (++this.scanChunkZ > this.endChunkX) {
            this.scanChunkZ = this.startChunkZ;
            this.chunkLoadedCount++;
         }

         if (chunkLoadedTotal <= 0 && this.chunkLoadedCount <= this.chunkLoadedValue) {
            return;
         }
      }

      this.updateBlueTracersBlueTracersActive();
   }

   private Set<Block> getAirBlock() {
      var hashSet = new HashSet();

      for (Item item : this.itemList.getContainsItemList()) {
         Block blockFromItem = Block.getBlockFromItem(item);
         if (blockFromItem != Blocks.AIR) {
            hashSet.add(blockFromItem);
         }
      }

      return hashSet;
   }
}
