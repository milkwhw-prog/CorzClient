package com.corz.client.categories.donutsmp;

import com.corz.client.config.ConfigSetting;
import com.corz.client.config.BooleanSetting;
import com.corz.client.config.EnumSetting;
import com.corz.client.config.IntegerSetting;
import com.corz.client.render.QueueCommand;
import com.corz.client.render.VectorDotUtils;
import com.corz.client.render.VertexBuilderUtils;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.chunk.ChunkSection;
import net.minecraft.world.chunk.WorldChunk;
import org.joml.Matrix4f;
import ai.auth.Module;
import com.corz.client.utils.IconsPngType;

public class NetheriteFinder extends Module {
   static final String VIEW_MODE_BOX = "Box";
   private final BooleanSetting exposedOnly;
   static final String VIEW_MODE_BLOCK = "Block";
   private final IntegerSetting sectionLimit;
   private ExecutorService threadExecutor;
   private final BooleanSetting announceFinds;
   private final AtomicBoolean scanRunning;
   private volatile int worldIdentityHash;
   private int scanTickCounter;
   private final Set<Long> announcedDebrisKeys;
   private final EnumSetting viewMode = new EnumSetting(
      "View", "Block draws the real debris block through walls, Box draws an outline, or both.", "Block + Box", "Block + Box", "Block", "Box"
   );
   static final String VIEW_MODE_BLOCK_AND_BOX = "Block + Box";
   private volatile List<NetheriteFinder.DebrisBox> debrisBoxes;
   private final BooleanSetting tracers;
   private volatile List<BlockPos> debrisBlocks;

   private void announceNewDebris() {
      var debrisAncientItems = this.debrisBoxes;
      if (!debrisAncientItems.isEmpty()) {
         int debrisAncientCounter = 0;

         for (NetheriteFinder.DebrisBox debrisBox : debrisAncientItems) {
            long long2 = BlockPos.asLong((int)debrisBox.minX(), (int)debrisBox.minY(), (int)debrisBox.minZ());
            if (this.announcedDebrisKeys.add(long2) && debrisAncientCounter < 3 && mc.player != null) {
               String debrisAncientText = debrisBox.exact()
                  ? (int)debrisBox.minX() + " " + (int)debrisBox.minY() + " " + (int)debrisBox.minZ()
                  : "~"
                     + (int)((debrisBox.minX() + debrisBox.maxX()) / 2.0)
                     + " "
                     + (int)((debrisBox.minY() + debrisBox.maxY()) / 2.0)
                     + " "
                     + (int)((debrisBox.minZ() + debrisBox.maxZ()) / 2.0);
               mc.player.sendMessage(Text.literal("\u00a7aAncient Debris found! \u00a7f" + debrisAncientText), false);
               debrisAncientCounter++;
            }
         }

         if (this.announcedDebrisKeys.size() > 8192) {
            this.announcedDebrisKeys.clear();
         }
      }
   }

   @Override
   protected void onRender3D(QueueCommand queueCommand) {
      if (mc.world != null && mc.player != null) {
         String formatText = this.viewMode.getCurrentOption();
         boolean block = shouldDrawBoxes(formatText);
         Vec3d cameraPos = mc.gameRenderer.getCamera().getCameraPos();
         double x = mc.player.getX();
         double y = mc.player.getY();
         double z = mc.player.getZ();
         MatrixStack matrices = queueCommand.matrices;
         matrices.push();
         matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
         Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
         int netheriteFinderValue = packColor(255, 60, 255, 90);
         int packColor2 = packColor(235, 80, 220, 255);
         int packColor3 = packColor(150, 60, 150, 200);
         int packColor4 = packColor(200, 255, 60, 220);
         boolean booleanValue = this.tracers.isEnabled();

         for (NetheriteFinder.DebrisBox debrisBox : this.debrisBoxes) {
            double vectorDotValue = (debrisBox.minX() + debrisBox.maxX()) / 2.0;
            double vertexBuilderValue = (debrisBox.minY() + debrisBox.maxY()) / 2.0;
            double netheriteFinderValue7 = (debrisBox.minZ() + debrisBox.maxZ()) / 2.0;
            double deltaVar = vectorDotValue - x;
            double deltaVar2 = vertexBuilderValue - y;
            double deltaVar3 = netheriteFinderValue7 - z;
            boolean netheriteFinderFlag = deltaVar * deltaVar + deltaVar2 * deltaVar2 + deltaVar3 * deltaVar3 <= 4096.0;
            if (!debrisBox.exact() || block) {
               int netheriteFinderValue2;
               float blockOverlayValue;
               float netheriteFinderValue10;
               if (debrisBox.exact()) {
                  netheriteFinderValue2 = netheriteFinderValue;
                  blockOverlayValue = 3.0F;
                  netheriteFinderValue10 = 0.01F;
               } else {
                  netheriteFinderValue2 = netheriteFinderFlag ? packColor2 : packColor3;
                  blockOverlayValue = netheriteFinderFlag ? 2.0F : 1.2F;
                  netheriteFinderValue10 = -0.4F;
               }

               VertexBuilderUtils.handle(
                  positionMatrix,
                  (float)debrisBox.minX() - netheriteFinderValue10,
                  (float)debrisBox.minY() - netheriteFinderValue10,
                  (float)debrisBox.minZ() - netheriteFinderValue10,
                  (float)debrisBox.maxX() + netheriteFinderValue10,
                  (float)debrisBox.maxY() + netheriteFinderValue10,
                  (float)debrisBox.maxZ() + netheriteFinderValue10,
                  netheriteFinderValue2,
                  blockOverlayValue
               );
            }

            if (booleanValue) {
               VectorDotUtils.handle(vectorDotValue, vertexBuilderValue, netheriteFinderValue7, packColor4, 1.2F);
            }
         }

         for (Entity entity : mc.world.getEntities()) {
            if (entity instanceof ItemEntity itemEntity && itemEntity.getStack().isOf(Items.ANCIENT_DEBRIS)) {
               Box boundingBox = itemEntity.getBoundingBox();
               VertexBuilderUtils.handle(
                  positionMatrix, (float)boundingBox.minX, (float)boundingBox.minY, (float)boundingBox.minZ, (float)boundingBox.maxX, (float)boundingBox.maxY, (float)boundingBox.maxZ, netheriteFinderValue, 3.0F
               );
               if (booleanValue) {
                  VectorDotUtils.handle(itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), packColor4, 1.2F);
               }
            }
         }

         matrices.pop();
         VertexBuilderUtils.updateDrawImmediate();
      }
   }

   @Override
   protected void onDisable() {
      if (this.threadExecutor != null) {
         this.threadExecutor.shutdownNow();
      }

      this.threadExecutor = null;
      this.debrisBoxes = List.of();
      this.debrisBlocks = List.of();
      this.announcedDebrisKeys.clear();
   }

   private static List<NetheriteFinder.DebrisBox> clusterIntoBoxes(List<BlockPos> list) {
      boolean[] booleans = new boolean[list.size()];
      var arrayList = new ArrayList();

      for (int i = 0; i < list.size(); i++) {
         if (!booleans[i]) {
            int netheriteFinderBound = Integer.MAX_VALUE;
            int maxValue = Integer.MAX_VALUE;
            int intersectsCenterBound = Integer.MAX_VALUE;
            int minValue = Integer.MIN_VALUE;
            int minValue5 = Integer.MIN_VALUE;
            int aggroPreemptBound = Integer.MIN_VALUE;
            var arrayDeque = new ArrayDeque();
            arrayDeque.add(i);
            booleans[i] = true;

            while (!arrayDeque.isEmpty()) {
               int index = (Integer)arrayDeque.poll();
               BlockPos position = (BlockPos)list.get(index);
               netheriteFinderBound = Math.min(netheriteFinderBound, position.getX());
               maxValue = Math.min(maxValue, position.getY());
               intersectsCenterBound = Math.min(intersectsCenterBound, position.getZ());
               minValue = Math.max(minValue, position.getX() + 1);
               minValue5 = Math.max(minValue5, position.getY() + 1);
               aggroPreemptBound = Math.max(aggroPreemptBound, position.getZ() + 1);

               for (int j = 0; j < list.size(); j++) {
                  if (!booleans[j]) {
                     BlockPos blockPos = (BlockPos)list.get(j);
                     int maxAbs = Math.max(
                        Math.abs(position.getX() - blockPos.getX()), Math.max(Math.abs(position.getY() - blockPos.getY()), Math.abs(position.getZ() - blockPos.getZ()))
                     );
                     if (maxAbs <= 2) {
                        booleans[j] = true;
                        arrayDeque.add(j);
                     }
                  }
               }
            }

            arrayList.add(new NetheriteFinder.DebrisBox((double)netheriteFinderBound, (double)maxValue, (double)intersectsCenterBound, (double)minValue, (double)minValue5, (double)aggroPreemptBound, true));
         }
      }

      return arrayList;
   }

   static boolean shouldDrawBlocks(String boxText) {
      return !"Box".equals(boxText);
   }

   @Override
   protected void onReset() {
      if (mc.world != null && mc.player != null) {
         int hashCode = System.identityHashCode(mc.world);
         if (hashCode != this.worldIdentityHash) {
            this.worldIdentityHash = hashCode;
            this.debrisBoxes = List.of();
            this.debrisBlocks = List.of();
         }

         if (this.announceFinds.isEnabled()) {
            this.announceNewDebris();
         } else if (!this.announcedDebrisKeys.isEmpty()) {
            this.announcedDebrisKeys.clear();
         }

         if (++this.scanTickCounter >= 20) {
            this.scanTickCounter = 0;
            ExecutorService threadExecutor = this.threadExecutor;
            if (threadExecutor != null && !threadExecutor.isShutdown()) {
               if (this.scanRunning.compareAndSet(false, true)) {
                  double x = mc.player.getX();
                  double y = mc.player.getY();
                  double z = mc.player.getZ();
                  var arrayList = new ArrayList();
                  int iIiValue = mc.options.getViewDistance().getValue();
                  ChunkPos chunkPos = mc.player.getChunkPos();

                  for (int i = chunkPos.x - iIiValue; i <= chunkPos.x + iIiValue; i++) {
                     for (int j = chunkPos.z - iIiValue; j <= chunkPos.z + iIiValue; j++) {
                        if (mc.world.isChunkLoaded(i, j)) {
                           arrayList.add(mc.world.getChunk(i, j));
                        }
                     }
                  }

                  boolean booleanValue = this.exposedOnly.isEnabled();
                  int sectionBound = this.sectionLimit.getValue();
                  threadExecutor.submit(() -> {
                     try {
                        NetheriteFinder.ScanResult var11x = scanChunks(arrayList, booleanValue, sectionBound, x, y, z);
                        if (hashCode == this.worldIdentityHash) {
                           this.debrisBoxes = var11x.boxes();
                           this.debrisBlocks = var11x.blocks();
                        }
                     } catch (Throwable e) {
                     } finally {
                        this.scanRunning.set(false);
                     }
                  });
               }
            }
         }
      }
   }

   @Override
   protected void onEnable() {
      this.debrisBoxes = List.of();
      this.debrisBlocks = List.of();
      this.scanTickCounter = 0;
      this.scanRunning.set(false);
      this.announcedDebrisKeys.clear();
      this.threadExecutor = Executors.newSingleThreadExecutor(task -> {
         Thread thread = new Thread(task, "netherite-finder-scan");
         thread.setDaemon(true);
         return thread;
      });
   }

   private static NetheriteFinder.ScanResult scanChunks(
      List<WorldChunk> list, boolean sectionFlag, int sectionBound, double sectionValue, double y, double z
   ) {
      var hashMap = new HashMap();

      for (WorldChunk worldChunk : list) {
         hashMap.put(worldChunk.getPos().toLong(), worldChunk);
      }

      var arrayList = new ArrayList();
      var arrayList2 = new ArrayList();
      var arrayList3 = new ArrayList();
      int sectionTotal = 0;
      int sectionTotal2 = 0;

      for (WorldChunk worldChunk2 : list) {
         ChunkSection[] sectionArray = worldChunk2.getSectionArray();
         int bottomSectionCoord = worldChunk2.getBottomSectionCoord();
         ChunkPos pos2 = worldChunk2.getPos();

         for (int i = 0; i < sectionArray.length; i++) {
            ChunkSection chunkSection = sectionArray[i];
            if (chunkSection != null && !chunkSection.isEmpty() && chunkSection.hasAny(state -> state.isOf(Blocks.ANCIENT_DEBRIS))) {
               int sectionValue4 = bottomSectionCoord + i << 4;
               var debrisAncient = collectDebrisInSection(chunkSection, pos2.getStartX(), sectionValue4, pos2.getStartZ());
               sectionTotal += debrisAncient.size();
               if (debrisAncient.isEmpty()) {
                  arrayList2.add(
                     new NetheriteFinder.DebrisBox(
                        (double)pos2.getStartX(),
                        (double)sectionValue4,
                        (double)pos2.getStartZ(),
                        (double)(pos2.getStartX() + 16),
                        (double)(sectionValue4 + 16),
                        (double)(pos2.getStartZ() + 16),
                        false
                     )
                  );
               } else {
                  if (sectionFlag) {
                     debrisAncient.removeIf(pos -> !isExposedToAirOrLava(hashMap, pos.getX(), pos.getY(), pos.getZ()));
                  }

                  sectionTotal2 += debrisAncient.size();
                  if (!debrisAncient.isEmpty()) {
                     arrayList3.addAll(debrisAncient);
                     arrayList.addAll(clusterIntoBoxes(debrisAncient));
                  }
               }
            }
         }
      }

      Comparator comparingDouble = Comparator.comparingDouble(
         playerXx -> distanceSquared(
            (((com.corz.client.categories.donutsmp.NetheriteFinder.DebrisBox) playerXx).minX() + ((com.corz.client.categories.donutsmp.NetheriteFinder.DebrisBox) playerXx).maxX()) / 2.0,
            (((com.corz.client.categories.donutsmp.NetheriteFinder.DebrisBox) playerXx).minY() + ((com.corz.client.categories.donutsmp.NetheriteFinder.DebrisBox) playerXx).maxY()) / 2.0,
            (((com.corz.client.categories.donutsmp.NetheriteFinder.DebrisBox) playerXx).minZ() + ((com.corz.client.categories.donutsmp.NetheriteFinder.DebrisBox) playerXx).maxZ()) / 2.0,
            sectionValue,
            y,
            z
         )
      );
      arrayList.sort(comparingDouble);
      arrayList2.sort(comparingDouble);
      arrayList3.sort(
         Comparator.comparingDouble((java.util.function.ToDoubleFunction<BlockPos>) playerYx -> distanceSquared(playerYx.getX() + 0.5, playerYx.getY() + 0.5, playerYx.getZ() + 0.5, sectionValue, y, z))
      );
      var arrayList4 = new ArrayList();
      arrayList4.addAll(arrayList.subList(0, Math.min(256, arrayList.size())));
      arrayList4.addAll(arrayList2.subList(0, Math.min(Math.max(0, sectionBound), arrayList2.size())));
      var copyOf = List.copyOf(arrayList3.subList(0, Math.min(512, arrayList3.size())));
      return new NetheriteFinder.ScanResult(List.copyOf(arrayList4), copyOf, sectionTotal, sectionTotal2, arrayList2.size());
   }

   private static List<BlockPos> collectDebrisInSection(ChunkSection chunkSection, int debrisAncientValue, int debrisAncientValue2, int debrisAncientValue3) {
      var arrayList = new ArrayList();

      for (int i = 0; i < 16; i++) {
         for (int j = 0; j < 16; j++) {
            for (int k = 0; k < 16; k++) {
               if (chunkSection.getBlockState(k, i, j).isOf(Blocks.ANCIENT_DEBRIS)) {
                  arrayList.add(new BlockPos(debrisAncientValue + k, debrisAncientValue2 + i, debrisAncientValue3 + j));
               }
            }
         }
      }

      return arrayList;
   }

   static boolean shouldDrawBoxes(String blockText) {
      return !"Block".equals(blockText);
   }

   private static boolean isExposedToAirOrLava(Map<Long, WorldChunk> map, int netheriteFinderValue, int y2, int z2) {
      return isAirOrLava(map, netheriteFinderValue + 1, y2, z2)
         || isAirOrLava(map, netheriteFinderValue - 1, y2, z2)
         || isAirOrLava(map, netheriteFinderValue, y2 + 1, z2)
         || isAirOrLava(map, netheriteFinderValue, y2 - 1, z2)
         || isAirOrLava(map, netheriteFinderValue, y2, z2 + 1)
         || isAirOrLava(map, netheriteFinderValue, y2, z2 - 1);
   }

   public int getDebrisBoxCount() {
      return this.debrisBoxes.size();
   }

   private static boolean isAirOrLava(Map<Long, WorldChunk> map, int x2, int y2, int z2) {
      WorldChunk worldChunk = (WorldChunk)map.get(ChunkPos.toLong(x2 >> 4, z2 >> 4));
      if (worldChunk == null) {
         return false;
      } else {
         BlockState blockState = worldChunk.getBlockState(new BlockPos(x2, y2, z2));
         return blockState.isAir() || blockState.isOf(Blocks.LAVA);
      }
   }

   private static int packColor(int netheriteFinderValue, int netheriteFinderValue2, int netheriteFinderValue3, int netheriteFinderValue4) {
      return (netheriteFinderValue & 0xFF) << 24 | (netheriteFinderValue2 & 0xFF) << 16 | (netheriteFinderValue3 & 0xFF) << 8 | netheriteFinderValue4 & 0xFF;
   }

   public NetheriteFinder() {
      super("Netherite Finder", IconsPngType.DONUTSMP);
      this.exposedOnly = new BooleanSetting("Exposed Only", "Only show debris that touches air or lava, like debris you could see in a cave.", true);
      this.sectionLimit = new IntegerSetting("Section Limit", "How many of the nearest debris sections (16x16x16 boxes) to show at once.", 8, 1, 64);
      this.tracers = new BooleanSetting("Tracers", "Draw a line from your view to each hit.", true);
      this.announceFinds = new BooleanSetting("Announce Finds", "Print \"Ancient Debris found! X Y Z\" to your own chat (local only) on new hits.", true);
      this.debrisBoxes = List.of();
      this.debrisBlocks = List.of();
      this.scanRunning = new AtomicBoolean();
      this.announcedDebrisKeys = new HashSet<>();
      this.addSettings(new ConfigSetting[]{this.viewMode, this.exposedOnly, this.sectionLimit, this.tracers, this.announceFinds});
   }

   private static double distanceSquared(double netheriteFinderValue, double netheriteFinderValue2, double netheriteFinderValue3, double sectionValue, double y, double z) {
      double netheriteFinderFactor = netheriteFinderValue - sectionValue;
      double netheriteFinderFactor2 = netheriteFinderValue2 - y;
      double netheriteFinderFactor3 = netheriteFinderValue3 - z;
      return netheriteFinderFactor * netheriteFinderFactor + netheriteFinderFactor2 * netheriteFinderFactor2 + netheriteFinderFactor3 * netheriteFinderFactor3;
   }

   private record DebrisBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ, boolean exact) {
   }

   private record ScanResult(List<NetheriteFinder.DebrisBox> boxes, List<BlockPos> blocks, int raw, int exposed, int ghost) {
   }
}
