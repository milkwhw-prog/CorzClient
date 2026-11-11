package com.corz.client.entity;

import com.corz.client.inventory.BlockEntityUtils;
import com.corz.client.world.AbsorptionPosData;
import com.corz.client.world.BlockPosHelperGapple;
import com.corz.client.world.BoxLengthUtils;
import com.corz.client.world.HimEtaData;
import com.corz.client.world.PosBlockUtilsBonus;
import com.corz.client.world.PosBlockUtilsBonus.Armor;
import com.corz.client.world.PosBlockUtilsBonus.Window;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.function.IntFunction;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import com.corz.client.utils.RayBudget;
import com.corz.client.utils.AmountConstTime;
import com.corz.client.utils.BowGappleType;
import com.corz.client.utils.VecShieldUtilsToughness;

public final class VecBlock {
   private Set<Integer> vecBlockIntegerSet;
   private List<VecBlock.Source> copyOf;
   private final RayBudget powerTimeLimiter;
   private IntFunction<AmountConstTime> intFunction;
   private int minEtaTicks;
   private List<HimEtaData> cellCenterHimEtaDataList;
   private Armor powerArmor;
   private float secondSourceDamage;
   private float posAmount;
   private Vec3d vecBlockVec3d;
   private float registryEquipped;
   private final List<VecBlock.Source> powerSourceList;
   private long lastUpdateTick;
   private final List<VecBlock.Source> posSourceList;
   private BlockPos blockPos;
   private World world;
   private long vecBlockValue;
   private VecBlock.Threat maxMinData = VecBlock.Threat.INSTANCE;
   private float registryAmount;
   private BlockPosHelperGapple blockPosHelper;
   private BlockPos add;

   public float getMaxPower(Vec3d vec, Box box, List<VecBlockingData> list) {
      MinecraftClient instance = MinecraftClient.getInstance();
      if (instance.world != null && instance.player != null && vec != null && box != null) {
         var arrayList = new ArrayList(4);

         for (VecBlock.Source source : this.powerSourceList) {
            if (arrayList.size() >= 2) {
               break;
            }

            arrayList.add(source);
         }

         this.handlePowerDistance(arrayList, vec);
         arrayList.addAll(this.getCellCenter());
         float maxPower = 0.0F;

         for (VecBlock.Source source2 : (Iterable<com.corz.client.entity.VecBlock.Source>) (arrayList)) {
            double distanceTo = vec.distanceTo(source2.center());
            if (!(distanceTo > source2.power() * 2.0)) {
               float powerBound;
               if (this.powerTimeLimiter != null && !this.powerTimeLimiter.tryReserveRays(12, 3)) {
                  powerBound = getDifficultyInstance(distanceTo, source2.power(), source2.weight(), this.powerArmor);
               } else {
                  float raycastMin = BoxVisibilityRaycaster.computeVisibleFraction(source2.center(), box, instance.player, source2.virtualSolid(), source2.virtualAir());
                  float powerValue = (float)BoxLengthUtils.getAmount3(distanceTo, raycastMin, source2.power());
                  powerBound = PosBlockUtilsBonus.getToughnessEpf(PosBlockUtilsBonus.getAmount(powerValue, this.powerArmor.difficulty()), this.powerArmor) * source2.weight();
               }

               maxPower = Math.max(maxPower, powerBound);
            }
         }

         if (list != null) {
            for (VecBlockingData vecBlockingData : list) {
               if (vecBlockingData.serverPos() != null
                  && !(vecBlockingData.serverPos().distanceTo(vec) > 3.5)
                  && (vecBlockingData.main() == BowGappleType.SWORD || vecBlockingData.main() == BowGappleType.AXE || vecBlockingData.main() == BowGappleType.MACE)) {
                  maxPower = Math.max(maxPower, VecShieldUtilsToughness.getToughnessAmp(9.0F, 0.0F, 1.0F, false, this.powerArmor, this.registryEquipped));
               }
            }
         }

         return maxPower;
      } else {
         return 0.0F;
      }
   }

   public void setIntFunction(IntFunction<AmountConstTime> intFunction) {
      this.intFunction = intFunction;
   }

   public VecBlock(RayBudget max, IntFunction<AmountConstTime> intFunction) {
      this.lastUpdateTick = Long.MIN_VALUE;
      this.vecBlockIntegerSet = new HashSet<>();
      this.posSourceList = new ArrayList<>();
      this.powerSourceList = new ArrayList<>();
      this.vecBlockValue = Long.MIN_VALUE;
      this.minEtaTicks = 99;
      this.cellCenterHimEtaDataList = List.of();
      this.copyOf = List.of();
      this.powerArmor = Armor.INSTANCE;
      this.powerTimeLimiter = max;
      this.intFunction = intFunction;
   }

   private Map<Integer, Boolean> getPosAmount(World world2, ClientPlayerEntity entity, AbsorptionPosData absorptionPosData, List<VecBlockingData> list) {
      var hashMap = new HashMap();
      BlockPos floored = BlockPos.ofFloored(absorptionPosData.pos());
      var arrayList = new ArrayList();

      for (VecBlockingData vecBlockingData : list) {
         if (vecBlockingData.serverPos() != null && vecBlockingData.distance() <= 10.0) {
            arrayList.add(vecBlockingData);
         }
      }

      if (arrayList.isEmpty()) {
         this.blockPos = null;
         this.posAmount = 0.0F;
         this.secondSourceDamage = 0.0F;
         this.posSourceList.clear();
         return hashMap;
      } else {
         var hashMap2 = new HashMap();

         for (int i = -1; i <= 0; i++) {
            for (int j = -2; j <= 2; j++) {
               for (int k = -2; k <= 2; k++) {
                  BlockPos add = floored.add(j, i, k);
                  boolean posFlag = BlockEntityUtils.canBedrockObsidian(add) && world2.isAir(add.up());
                  Vec3d vec3d = new Vec3d(add.getX() + 0.5, add.getY() + 1.0, add.getZ() + 0.5);
                  Box box = BlockEntityUtils.getBox(add);
                  Boolean boolean2 = null;

                  for (VecBlockingData vecBlockingData2 : (Iterable<com.corz.client.entity.VecBlockingData>) (arrayList)) {
                     boolean posFlag2 = vecBlockingData2.main() == BowGappleType.OBSIDIAN || vecBlockingData2.off() == BowGappleType.OBSIDIAN;
                     if (posFlag || posFlag2) {
                        Vec3d vec3d2 = vecBlockingData2.velocity() == null ? Vec3d.ZERO : vecBlockingData2.velocity();
                        Vec3d vec = vecBlockingData2.serverPos().add(vec3d2.x * 4.0, vec3d2.y * 4.0 + 1.62, vec3d2.z * 4.0);
                        if (!(vec.distanceTo(vec3d) > 4.6) && !(BlockEntityUtils.getMinMax(vec, box) > 3.1)) {
                           if (boolean2 == null) {
                              boolean2 = posFlag ? world2.getOtherEntities(null, BlockEntityUtils.getBox3(add)).isEmpty() : canDown(world2, add);
                           }

                           if (!boolean2) {
                              break;
                           }

                           hashMap.put(vecBlockingData2.id(), Boolean.TRUE);
                           VecBlock.Cell cell = new VecBlock.Cell(add, !posFlag);
                           float posValue = posFlag ? 1.0F : 0.6F;
                           hashMap2.merge(cell, posValue, (java.util.function.BiFunction<Float, Float, Float>) Math::max);
                        }
                     }
                  }
               }
            }
         }

         var arrayList2 = new ArrayList(hashMap2.entrySet());
         Vec3d pos = absorptionPosData.pos();
         arrayList2.sort(
            Comparator.<Entry>comparingDouble(
                  entry -> BlockEntityUtils.getAmount(entity, pos.distanceTo(BlockEntityUtils.getVec3d(((VecBlock.Cell)entry.getKey()).pos())), 6.0F)
                     * (Float)entry.getValue()
               )
               .reversed()
         );
         var wordListed = new ArrayList();
         int posCounter = 0;
         boolean posFlag3 = false;

         for (Entry entry2 : (Iterable<java.util.Map.Entry>) (arrayList2)) {
            if (posCounter >= 4) {
               break;
            }

            if (this.powerTimeLimiter != null && !this.powerTimeLimiter.tryReserveRays(45, 3)) {
               posFlag3 = true;
               break;
            }

            posCounter++;
            VecBlock.Cell vecBlockCell = (VecBlock.Cell)entry2.getKey();
            Vec3d vec3d3 = BlockEntityUtils.getVec3d(vecBlockCell.pos());
            BlockPos blockPos = vecBlockCell.build() ? vecBlockCell.pos() : null;
            float creativeSpectator = BlockEntityUtils.getCreativeSpectator(entity, vec3d3, pos, absorptionPosData.box(), blockPos, null, 6.0F);
            float posValue2 = PosBlockUtilsBonus.getToughnessEpf(creativeSpectator, this.powerArmor) * (Float)entry2.getValue();
            wordListed.add(new VecBlock.Source(vecBlockCell.pos(), vec3d3, blockPos, null, 6.0F, (Float)entry2.getValue(), posValue2));
         }

         if (posFlag3 && wordListed.isEmpty() && !arrayList2.isEmpty()) {
            return hashMap;
         } else {
            wordListed.sort(Comparator.<VecBlock.Source>comparingDouble(source -> source.damage()).reversed());
            this.posSourceList.clear();
            this.posSourceList.addAll(wordListed);
            this.blockPos = wordListed.isEmpty() ? null : ((VecBlock.Source)wordListed.get(0)).base();
            this.posAmount = wordListed.isEmpty() ? 0.0F : ((VecBlock.Source)wordListed.get(0)).damage();
            this.secondSourceDamage = wordListed.size() < 2 ? 0.0F : ((VecBlock.Source)wordListed.get(1)).damage();
            return hashMap;
         }
      }
   }

   private List<VecBlock.Source> getCellCenter() {
      BlockPosHelperGapple blockPosHelper3 = this.blockPosHelper;
      if (blockPosHelper3 == null) {
         return List.of();
      } else {
         var items = blockPosHelper3.getCopyOf();
         if (items != this.cellCenterHimEtaDataList) {
            var arrayList = new ArrayList(2);

            for (HimEtaData himEtaData : getDet(items)) {
               arrayList.add(new VecBlock.Source(himEtaData.cell(), Vec3d.ofCenter(himEtaData.cell()), null, himEtaData.cell(), 5.0F, himEtaData.pDet(), himEtaData.hitUs()));
            }

            this.cellCenterHimEtaDataList = items;
            this.copyOf = List.copyOf(arrayList);
         }

         return this.copyOf;
      }
   }

   private static boolean canDown(World world2, BlockPos pos) {
      return BlockEntityUtils.canFlag(pos.down()) && BlockEntityUtils.isEmpty(pos);
   }

   public VecBlock(RayBudget rayBudget) {
      this(rayBudget, null);
   }

   public AmountConstTime getApply(int vecBlockValue) {
      var intFunction = this.intFunction;
      return intFunction == null ? null : (AmountConstTime)intFunction.apply(vecBlockValue);
   }

   private void handlePowerDistance(List<VecBlock.Source> list, Vec3d vec) {
      while (list.size() < 2) {
         VecBlock.Source source = null;
         float powerDistanceValue = 0.0F;

         for (VecBlock.Source up : this.posSourceList) {
            if (!list.contains(up)) {
               double distanceTo = vec.distanceTo(up.center());
               if (!(distanceTo > up.power() * 2.0)) {
                  float difficultyInstance = getDifficultyInstance(distanceTo, up.power(), up.weight(), this.powerArmor);
                  if (source == null || difficultyInstance > powerDistanceValue) {
                     source = up;
                     powerDistanceValue = difficultyInstance;
                  }
               }
            }
         }

         if (source == null) {
            return;
         }

         list.add(source);
      }
   }

   public VecBlock.Threat getMaxMinData() {
      return this.maxMinData;
   }

   public void setBlockPosHelperForBlockPosHelperGapple(BlockPosHelperGapple blockPosHelper) {
      this.blockPosHelper = blockPosHelper;
   }

   private void handleMaxRegistry(World world2, ClientPlayerEntity entity, AbsorptionPosData absorptionPosData, List<VecBlockingData> list) {
      this.registryAmount = 0.0F;
      this.add = null;
      if (world2.getRegistryKey() != World.NETHER) {
         BlockPos floored = BlockPos.ofFloored(absorptionPosData.pos());
         Vec3d pos = absorptionPosData.pos();

         for (int i = -2; i <= 2; i++) {
            for (int j = -5; j <= 5; j++) {
               for (int k = -5; k <= 5; k++) {
                  BlockPos add = floored.add(j, i, k);
                  BlockState blockState = world2.getBlockState(add);
                  if (blockState.isOf(Blocks.RESPAWN_ANCHOR) && blockState.get(RespawnAnchorBlock.CHARGES) > 0) {
                     Vec3d center = Vec3d.ofCenter(add);
                     if (!(center.distanceTo(pos) > 10.0) && (this.powerTimeLimiter == null || this.powerTimeLimiter.tryReserveRays(45, 3))) {
                        float amount = PosBlockUtilsBonus.getToughnessEpf(
                           BlockEntityUtils.getCreativeSpectator(entity, center, pos, absorptionPosData.box(), null, add, 5.0F), this.powerArmor
                        );
                        if (amount > this.registryAmount) {
                           this.registryAmount = amount;
                           this.add = add;
                        }
                     }
                  }
               }
            }
         }

         this.handleGlowstoneMax(world2, entity, absorptionPosData, list);
      }
   }

   public void updateAmountMin() {
      this.maxMinData = VecBlock.Threat.INSTANCE;
      this.lastUpdateTick = Long.MIN_VALUE;
      this.vecBlockVec3d = null;
      this.vecBlockIntegerSet = new HashSet<>();
      this.blockPos = null;
      this.posAmount = 0.0F;
      this.secondSourceDamage = 0.0F;
      this.posSourceList.clear();
      this.powerSourceList.clear();
      this.vecBlockValue = Long.MIN_VALUE;
      this.registryAmount = 0.0F;
      this.add = null;
      this.minEtaTicks = 99;
      this.cellCenterHimEtaDataList = List.of();
      this.copyOf = List.of();
      this.powerArmor = Armor.INSTANCE;
      this.registryEquipped = 0.0F;
      this.world = null;
   }

   public VecBlock.Threat getMaxMin(long time, AbsorptionPosData absorptionPosData, List<VecBlockingData> list, int vecBlockBound) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientWorld world = instance.world;
      ClientPlayerEntity player = instance.player;
      if (world != null && player != null && absorptionPosData != null) {
         if (world != this.world) {
            this.updateAmountMin();
            this.world = world;
         }

         List<VecBlockingData> list2 = list == null ? List.of() : list;
         this.powerArmor = absorptionPosData.armor() == null ? Armor.INSTANCE : absorptionPosData.armor();
         Vec3d vec3d = absorptionPosData.pos();
         List entitiesByClass = world.getEntitiesByClass(
            EndCrystalEntity.class,
            absorptionPosData.box().expand(8.0),
            feetPos -> feetPos.isAlive() && !feetPos.isRemoved() && feetPos.getEntityPos().distanceTo(vec3d) <= 8.0
         );
         var set = new HashSet();

         for (EndCrystalEntity endCrystalEntity : (Iterable<net.minecraft.entity.decoration.EndCrystalEntity>) (entitiesByClass)) {
            set.add(endCrystalEntity.getId());
         }

         boolean vecBlockFlag = !set.equals(this.vecBlockIntegerSet);
         boolean vecBlockFlag2 = this.vecBlockVec3d == null || vec3d.distanceTo(this.vecBlockVec3d) > 0.5;
         boolean vecBlockFlag3 = this.lastUpdateTick == Long.MIN_VALUE || time - this.lastUpdateTick >= 2L;
         if (!vecBlockFlag3 && !vecBlockFlag && !vecBlockFlag2) {
            this.maxMinData = this.getLiveWorst(this.maxMinData, absorptionPosData);
            return this.maxMinData;
         } else {
            this.lastUpdateTick = time;
            this.vecBlockVec3d = vec3d;
            this.vecBlockIntegerSet = set;
            this.registryEquipped = BlockEntityUtils.getRegistryEquipped(player);
            Window window = new Window(Math.max(0, vecBlockBound), absorptionPosData.lastScaledTaken());
            entitiesByClass.sort(Comparator.comparingDouble(obj -> ((net.minecraft.entity.decoration.EndCrystalEntity) obj).getEntityPos().squaredDistanceTo(vec3d)));
            float readyInstance2 = 0.0F;
            int vecBlockValue = -1;
            Vec3d vec3d2 = null;
            boolean vecBlockFlag4 = false;
            int min3 = 99;
            this.powerSourceList.clear();
            int vecBlockCounter = 0;

            for (EndCrystalEntity endCrystalEntity2 : (Iterable<net.minecraft.entity.decoration.EndCrystalEntity>) (entitiesByClass)) {
               if (vecBlockCounter >= 4) {
                  break;
               }

               vecBlockCounter++;
               Vec3d entityPos = endCrystalEntity2.getEntityPos();
               int serverPos = getServerPos(endCrystalEntity2.getBoundingBox(), list2);
               if (serverPos >= 0) {
                  if (this.powerTimeLimiter != null) {
                     this.powerTimeLimiter.tryReserveRays(45, 1);
                  }

                  float creativeSpectator = BlockEntityUtils.getCreativeSpectator(player, entityPos, vec3d, absorptionPosData.box(), null, null, 6.0F);
                  float readyInstance = PosBlockUtilsBonus.getReadyInstance(creativeSpectator, this.powerArmor, window, serverPos);
                  this.powerSourceList.add(new VecBlock.Source(endCrystalEntity2.getBlockPos().down(), entityPos, null, null, 6.0F, 1.0F, readyInstance));
                  min3 = Math.min(min3, serverPos);
                  if (serverPos == 0) {
                     vecBlockFlag4 = true;
                  }

                  if (readyInstance > readyInstance2) {
                     readyInstance2 = readyInstance;
                     vecBlockValue = endCrystalEntity2.getId();
                     vec3d2 = entityPos;
                  }
               }
            }

            this.powerSourceList.sort(Comparator.<VecBlock.Source>comparingDouble(source -> source.damage()).reversed());
            var posAmount = this.getPosAmount(world, player, absorptionPosData, list2);
            if (this.vecBlockValue == Long.MIN_VALUE || time - this.vecBlockValue >= 4L) {
               this.vecBlockValue = time;
               if (this.blockPosHelper == null) {
                  this.handleMaxRegistry(world, player, absorptionPosData, list2);
               } else {
                  this.registryAmount = 0.0F;
                  this.add = null;
                  this.handleGlowstoneMax(world, player, absorptionPosData, list2);
               }
            }

            float pilotStatusBound = 0.0F;

            for (VecBlockingData vecBlockingData : (Iterable<com.corz.client.entity.VecBlockingData>) (list2)) {
               if (!(vecBlockingData.distance() > 3.5)
                  && (vecBlockingData.main() == BowGappleType.SWORD || vecBlockingData.main() == BowGappleType.AXE || vecBlockingData.main() == BowGappleType.MACE)) {
                  pilotStatusBound = Math.max(pilotStatusBound, VecShieldUtilsToughness.getToughnessAmp(9.0F, 0.0F, 1.0F, false, this.powerArmor, this.registryEquipped));
               }
            }

            float vecBlockLimit = Math.max(Math.max(readyInstance2, this.posAmount), Math.max(this.registryAmount, pilotStatusBound));
            int min;
            if (vecBlockFlag4) {
               min = 0;
            } else {
               int min5 = 99;

               for (VecBlockingData vecBlockingData2 : (Iterable<com.corz.client.entity.VecBlockingData>) (list2)) {
                  int intersectsCenterBound = 99;
                  AmountConstTime amountConstTime = this.intFunction == null ? null : this.intFunction.apply(vecBlockingData2.id());
                  if (amountConstTime != null) {
                     intersectsCenterBound = amountConstTime.getMinForLong(time);
                  }

                  if (Boolean.TRUE.equals(posAmount.get(vecBlockingData2.id()))) {
                     intersectsCenterBound = Math.min(intersectsCenterBound, 4);
                  }

                  min5 = Math.min(min5, Math.max(Math.max(0, vecBlockBound), intersectsCenterBound));
               }

               int min2 = Math.min(min5, Math.max(Math.max(0, vecBlockBound), min3));
               min = min2;
            }

            this.minEtaTicks = min;
            float absorptionHealth = absorptionPosData.getAbsorptionHealth();
            boolean vecBlockFlag5 = vecBlockLimit >= absorptionHealth - 1.0F;
            boolean vecBlockFlag6 = vecBlockFlag5 && !absorptionPosData.totemConfirmed();
            this.maxMinData = this.getLiveWorst(
               new VecBlock.Threat(vecBlockLimit, readyInstance2, vecBlockValue, vec3d2, this.blockPos, this.posAmount, this.secondSourceDamage, this.registryAmount, this.add, pilotStatusBound, min, vecBlockFlag6, vecBlockFlag5),
               absorptionPosData
            );
            return this.maxMinData;
         }
      } else {
         this.maxMinData = VecBlock.Threat.INSTANCE;
         return this.maxMinData;
      }
   }

   static int getDetEta(int detEtaBound, HimEtaData himEtaData) {
      return himEtaData != null && himEtaData.isCell() && !(himEtaData.pDet() < 0.6F) ? Math.min(detEtaBound, himEtaData.eta()) : detEtaBound;
   }

   static float getChargesEta(float chargesEtaBound, List<HimEtaData> list) {
      float maxHead = Math.max(0.0F, chargesEtaBound);
      if (list == null) {
         return maxHead;
      } else {
         for (HimEtaData himEtaData : list) {
            if (himEtaData != null && himEtaData.isCell() && (himEtaData.charges() >= 1 || himEtaData.eta() <= 2)) {
               maxHead = Math.max(maxHead, himEtaData.hitUs());
            }
         }

         return maxHead;
      }
   }

   static BlockPos getCell(BlockPos pos, HimEtaData himEtaData) {
      return himEtaData != null && himEtaData.isCell() ? himEtaData.cell() : pos;
   }

   private void handleGlowstoneMax(World world2, ClientPlayerEntity entity, AbsorptionPosData absorptionPosData, List<VecBlockingData> list) {
      if (world2.getRegistryKey() != World.NETHER) {
         Vec3d pos = absorptionPosData.pos();
         BlockPos add = BlockPos.ofFloored(pos).up(2);
         if (world2.getBlockState(add).isReplaceable()) {
            Vec3d center = Vec3d.ofCenter(add);

            for (VecBlockingData vecBlockingData : list) {
               boolean glowstoneFlag = vecBlockingData.main() == BowGappleType.ANCHOR
                  || vecBlockingData.main() == BowGappleType.GLOWSTONE
                  || vecBlockingData.off() == BowGappleType.ANCHOR
                  || vecBlockingData.off() == BowGappleType.GLOWSTONE;
               if (glowstoneFlag && vecBlockingData.eye() != null && !(vecBlockingData.eye().distanceTo(center) > 4.5)) {
                  if (this.powerTimeLimiter != null && !this.powerTimeLimiter.tryReserveRays(45, 3)) {
                     break;
                  }

                  float amount = PosBlockUtilsBonus.getToughnessEpf(
                     BlockEntityUtils.getCreativeSpectator(entity, center, pos, absorptionPosData.box(), null, add, 5.0F), this.powerArmor
                  );
                  if (amount > this.registryAmount) {
                     this.registryAmount = amount;
                     this.add = add;
                  }
                  break;
               }
            }
         }
      }
   }

   static List<HimEtaData> getDet(List<HimEtaData> list) {
      var arrayList = new ArrayList(2);
      if (list == null) {
         return arrayList;
      } else {
         var arrayList2 = new ArrayList();

         for (HimEtaData himEtaData : list) {
            if (himEtaData != null && himEtaData.isCell() && himEtaData.pDet() > 0.0F) {
               arrayList2.add(himEtaData);
            }
         }

         arrayList2.sort(Comparator.<HimEtaData>comparingDouble(himEtaData2 -> himEtaData2.getDetHit()).reversed());

         for (HimEtaData totalPriceData : (Iterable<com.corz.client.world.HimEtaData>) (arrayList2)) {
            if (arrayList.size() >= 2) {
               break;
            }

            arrayList.add(totalPriceData);
         }

         return arrayList;
      }
   }

   static float getDifficultyInstance(double difficultyValue, float distanceValue, float difficultyFactor, Armor armor) {
      Armor armor3 = armor == null ? Armor.INSTANCE : armor;
      float posBlockValue = (float)BoxLengthUtils.getAmount3(difficultyValue, 1.0, distanceValue);
      return PosBlockUtilsBonus.getToughnessEpf(PosBlockUtilsBonus.getAmount(posBlockValue, armor3.difficulty()), armor3) * difficultyFactor;
   }

   private static int getServerPos(Box box, List<VecBlockingData> list) {
      int serverPos = -1;

      for (VecBlockingData vecBlockingData : list) {
         Vec3d vec3d = vecBlockingData.eye() != null ? vecBlockingData.eye() : (vecBlockingData.serverPos() == null ? null : vecBlockingData.serverPos().add(0.0, 1.62, 0.0));
         if (vec3d != null) {
            if (BlockEntityUtils.getMinMax(vec3d, box) <= 3.2) {
               return 0;
            }

            Vec3d vec3d2 = vecBlockingData.velocity() == null ? Vec3d.ZERO : vecBlockingData.velocity();
            if (vecBlockingData.serverPos() != null) {
               Vec3d add = vecBlockingData.serverPos().add(vec3d2.x * 3.0, vec3d2.y * 3.0 + 1.62, vec3d2.z * 3.0);
               if (BlockEntityUtils.getMinMax(add, box) <= 3.2) {
                  serverPos = serverPos < 0 ? 3 : Math.min(serverPos, 3);
               } else {
                  double serverPosValue = BlockEntityUtils.getMinMax(vec3d, box) - 3.2;
                  int serverPosBound = (int)Math.ceil(serverPosValue / 0.28);
                  if (serverPosBound <= 20) {
                     serverPos = serverPos < 0 ? serverPosBound : Math.min(serverPos, serverPosBound);
                  }
               }
            }
         }
      }

      return serverPos;
   }

   private VecBlock.Threat getLiveWorst(VecBlock.Threat threat, AbsorptionPosData absorptionPosData) {
      if (threat == null) {
         return VecBlock.Threat.INSTANCE;
      } else {
         float pilotStatusBound = this.registryAmount;
         BlockPos add = this.add;
         int liveWorstValue = this.minEtaTicks;
         BlockPosHelperGapple blockPosHelper3 = this.blockPosHelper;
         if (blockPosHelper3 != null) {
            pilotStatusBound = getChargesEta(this.registryAmount, blockPosHelper3.getCopyOf());
            add = getCell(this.add, blockPosHelper3.getPosHimEtaData());
            liveWorstValue = getDetEta(this.minEtaTicks, blockPosHelper3.getPosHimEtaData());
         }

         float liveWorstLimit = Math.max(Math.max(threat.liveMax(), threat.worstBaseDamage()), Math.max(pilotStatusBound, threat.meleeMax()));
         float absorptionHealth = absorptionPosData.getAbsorptionHealth();
         boolean liveWorstFlag = liveWorstLimit >= absorptionHealth - 1.0F;
         boolean liveWorstFlag2 = liveWorstFlag && !absorptionPosData.totemConfirmed();
         return liveWorstLimit == threat.maxIncoming()
               && pilotStatusBound == threat.anchorMax()
               && Objects.equals(add, threat.chargedAnchor())
               && liveWorstValue == threat.etaTicks()
               && liveWorstFlag2 == threat.lethal()
               && liveWorstFlag == threat.lethalAfterTotem()
            ? threat
            : new VecBlock.Threat(
               liveWorstLimit,
               threat.liveMax(),
               threat.liveCrystalId(),
               threat.liveCenter(),
               threat.worstBase(),
               threat.worstBaseDamage(),
               threat.secondBaseDamage(),
               pilotStatusBound,
               add,
               threat.meleeMax(),
               liveWorstValue,
               liveWorstFlag2,
               liveWorstFlag
            );
      }
   }

   private record Cell(BlockPos pos, boolean build) {
   }

   private record Source(BlockPos base, Vec3d center, BlockPos virtualSolid, BlockPos virtualAir, float power, float weight, float damage) {
   }

   public record Threat(
      float maxIncoming,
      float liveMax,
      int liveCrystalId,
      Vec3d liveCenter,
      BlockPos worstBase,
      float worstBaseDamage,
      float secondBaseDamage,
      float anchorMax,
      BlockPos chargedAnchor,
      float meleeMax,
      int etaTicks,
      boolean lethal,
      boolean lethalAfterTotem
   ) {
      public static final VecBlock.Threat INSTANCE = new VecBlock.Threat(0.0F, 0.0F, -1, null, null, 0.0F, 0.0F, 0.0F, null, 0.0F, 99, false, false);
   }
}
