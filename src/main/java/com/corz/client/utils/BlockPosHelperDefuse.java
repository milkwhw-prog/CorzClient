package com.corz.client.utils;

import com.corz.client.entity.EntityCenterUtils;
import com.corz.client.entity.FloorMathUtilsBlock;
import com.corz.client.entity.VecBlockingData;
import com.corz.client.inventory.BlockEntityUtils;
import com.corz.client.inventory.BlockPosHelperBlocked;
import com.corz.client.inventory.BlockPosUtilsRespawn;
import com.corz.client.inventory.InventoryStackUtilsEmpty;
import com.corz.client.world.AbsorptionPosData;
import com.corz.client.world.BlockPosHelperGapple;
import com.corz.client.world.BlockStateUtilsDirections;
import com.corz.client.world.BoxLengthUtils;
import com.corz.client.world.CenterVecData;
import com.corz.client.world.ClientInstance;
import com.corz.client.world.HimEtaData;
import com.corz.client.world.MathClampUtils;
import com.corz.client.world.PosBlockUtilsBonus;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.IntFunction;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.item.Items;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.HumanAimRotator;
import com.corz.client.utils.HotbarSlotSwitcher;
import com.corz.client.utils.RayBudget;
import com.corz.client.utils.BlockHitResolver;
import com.corz.client.utils.EntityDamageUtils;
import com.corz.client.utils.DefenseChooser;

public final class BlockPosHelperDefuse {
   private final StringBuilder traceBuilder;
   private final HotbarSlotSwitcher inventorySelected;
   private BlockPos sentSupportPos;
   public static final String SENT = "sent";
   private boolean actionPerformed;
   private final BlockPosHelperGapple blockPosHelper;
   private BlockPos lastPlanCell;
   private boolean facePlan;
   public static final String NETHER = "nether";
   private final RayBudget searchBudget;
   public static final String BLOCK = "block";
   private DefenseChooser.DefenseKind lastPlanKind;
   private long cachedPlanTick;
   private DefenseChooser.DefenseKind sentKind;
   private long sentDeadlineTick;
   private static final Direction[] ALL_DIRECTIONS = Direction.values();
   private BlockPosHelperDefuse.Plan cachedPlan;
   private static final Direction[] HORIZONTAL_DIRECTIONS = new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
   public static final String FACE = "face";
   private int traceEntryCount;
   private boolean nether;
   public static final String QUIET = "quiet";
   private boolean block;
   private final Map<Long, Long> shieldedAnchorTicks;
   private final Map<Long, Long> avoidCellUntilTick;
   private BlockPos sentCell;
   private boolean sentSpotPlan;
   private final ClientInstance clientInstance;
   public static final String BUSY = "busy";
   public static final String NONE = "none";
   private int defuseSentCount;
   private IntFunction<AmountConstTime> spotPredictionProvider;
   private final ArrayDeque<Long> recentShieldTicks = new ArrayDeque<>();
   private final BlockPosHelperBlocked blockPlacer;
   private double sentScore;
   private final Map<Long, Long> recentlyDefusedUntilTick;
   public static final String DEFUSE = "defuse";
   private int shieldSentCount;
   private double lastPlanScore;
   public static final String SPOT = "spot";
   private CenterVecData fallbackCombo;
   private final Map<Long, Long> shieldSpotPlacedTick;
   private final Map<Long, Long> defuseFailedUntilTick;
   public static final String SHIELD = "shield";

   private static void markCooldown(Map<Long, Long> map, BlockPos pos, long blockPosValue) {
      handle(map, pos, blockPosValue, 40);
   }

   static String kindToLabel(DefenseChooser.DefenseKind kind) {
      return kind == DefenseChooser.DefenseKind.DEFUSE ? "defuse" : (kind == DefenseChooser.DefenseKind.SHIELD ? "shield" : "none");
   }

   private float estimateDamageToTarget(SelfPopData selfPopData, HimEtaData himEtaData, int entityArmorBound) {
      VecBlockingData target = selfPopData.target();
      if (target != null && target.entity() != null) {
         float amountFlag = this.blockPosHelper.getAmountFlag(himEtaData.cell());
         if (Float.isNaN(amountFlag)) {
            if (this.searchBudget != null && !this.searchBudget.tryReserveRays(45, 3)) {
               return 0.0F;
            }

            BlockEntityUtils.Predicted interpolatorInstance = BlockEntityUtils.getInterpolatorInstance(target.entity(), Math.max(1, entityArmorBound), false, true);
            amountFlag = EntityCenterUtils.getBoxBounding(target.entity(), interpolatorInstance, himEtaData.cell(), null);
         }

         PosBlockUtilsBonus.Armor armor = target.armor() == null ? PosBlockUtilsBonus.Armor.INSTANCE : target.armor();
         return PosBlockUtilsBonus.getReadyInstance(amountFlag, armor, new PosBlockUtilsBonus.Window(Math.max(0, selfPopData.targetReadyIn()), target.lastScaledTaken()), entityArmorBound);
      } else {
         return 0.0F;
      }
   }

   private static int getPingTicks() {
      return Math.round(EntityDamageUtils.getCachedHurtTimeLimit() / 50.0F);
   }

   private BlockPosHelperDefuse.Plan planShieldAtPredictedSpot(SelfPopData selfPopData, ClientPlayerEntity clientPlayerEntity, ClientWorld clientWorld, int shieldSpotLimit) {
      if (shieldSpotLimit >= 2) {
         return null;
      } else {
         VecBlockingData target = selfPopData.target();
         AmountConstTime apply = this.getSpotPrediction(target);
         AmountConstTime.Spot spot = apply == null ? null : apply.getTimeAmount(selfPopData.tick());
         if (spot == null) {
            return null;
         } else {
            BlockPos blockPos = new BlockPos(spot.x(), spot.y(), spot.z());
            if (!isSpotWindowFree(this.shieldSpotPlacedTick, blockPos, selfPopData.tick())) {
               return null;
            } else {
               BlockPosHelperDefuse.ShieldPick instanceDirections = this.findShieldAroundSelf(selfPopData, spot, blockPos, apply.getMin(selfPopData.tick()), clientPlayerEntity, clientWorld, target);
               if (!instanceDirections.shield().feasible()) {
                  return null;
               } else {
                  double shieldValue = computeShieldValue(instanceDirections.shield().gain(), spot.interval());
                  if (!(shieldValue > 0.0)) {
                     return null;
                  } else {
                     DefenseChooser.DefenseChoice response = new DefenseChooser.DefenseChoice(
                        DefenseChooser.DefenseKind.SHIELD, instanceDirections.shield().cell(), instanceDirections.shield().glowstone(), shieldValue
                     );
                     return new BlockPosHelperDefuse.Plan(response, instanceDirections.shield().prepTicks(), instanceDirections.slot(), blockPos, "spot", 0);
                  }
               }
            }
         }
      }
   }

   private void clearSentAction() {
      this.blockPlacer.updateTextImmutableText();
      this.nether = false;
      this.actionPerformed = false;
      this.sentKind = DefenseChooser.DefenseKind.NONE;
      this.sentCell = null;
      this.sentSupportPos = null;
      this.sentSpotPlan = false;
      this.block = false;
      this.facePlan = false;
      this.sentDeadlineTick = 0L;
      this.sentScore = 0.0;
   }

   private AmountConstTime.Spot getPredictedSpot(VecBlockingData vecBlockingData, long blockPosValue) {
      AmountConstTime apply = this.getSpotPrediction(vecBlockingData);
      return apply == null ? null : apply.getTimeAmount(blockPosValue);
   }

   private static boolean isCooldownActive(Map<Long, Long> map, BlockPos pos, long blockPosValue) {
      Long long2 = pos == null ? null : (Long)map.get(pos.asLong());
      return long2 != null && long2 >= blockPosValue;
   }

   private static double getAimTurnAngle(Vec3d vec, Vec3d vec2, float blockPosValue, float amountFloat) {
      float[] wrapDegrees = MathClampUtils.getWrapDegrees(vec, vec2);
      return BoxLengthUtils.getAmount(blockPosValue, amountFloat, wrapDegrees[0], wrapDegrees[1]);
   }

   static int countRecentTicks(ArrayDeque<Long> arrayDeque, long blockPosLimit) {
      int blockPosCounter = 0;

      for (long blockPosValue : arrayDeque) {
         if (blockPosLimit >= blockPosValue && blockPosLimit - blockPosValue < 40L) {
            blockPosCounter++;
         }
      }

      return blockPosCounter;
   }

   private static void handle(Map<Long, Long> map, BlockPos pos, long blockPosValue, int blockPosValue2) {
      if (pos != null) {
         map.put(pos.asLong(), blockPosValue + blockPosValue2);
      }
   }

   private static List<BlockPos> getComboOccupiedCells(CenterVecData centerVecData) {
      ArrayList arrayList = new ArrayList(3);
      if (centerVecData == null) {
         return arrayList;
      } else {
         if (centerVecData.crystal() != null && centerVecData.crystal().base() != null) {
            arrayList.add(centerVecData.crystal().base());
            arrayList.add(centerVecData.crystal().base().up());
         }

         if (centerVecData.anchor() != null && centerVecData.anchor().cell() != null) {
            arrayList.add(centerVecData.anchor().cell());
         }

         return arrayList;
      }
   }

   private BlockPosHelperDefuse.DefusePick chooseDefuse(SelfPopData selfPopData, HimEtaData himEtaData, ClientPlayerEntity clientPlayerEntity) {
      if (himEtaData.charges() >= 1 && himEtaData.cell() != null) {
         if (!this.clientInstance.isSprintSneak() && !clientPlayerEntity.getOffHandStack().isOf(Items.GLOWSTONE)) {
            Vec3d eyePos = clientPlayerEntity.getEyePos();
            double clientValue = this.clientInstance.getAmountDouble();
            if (BlockEntityUtils.getMinMax(eyePos, BlockEntityUtils.getBoxForBlockPos(himEtaData.cell())) > clientValue) {
               return BlockPosHelperDefuse.DefusePick.INSTANCE;
            } else {
               int selectedSlot = this.inventorySelected.findSlotMatching(InventoryStackUtilsEmpty::canSwordsEmpty, false);
               if (selectedSlot < 0) {
                  selectedSlot = this.inventorySelected.findSlotMatching(BlockPosUtilsRespawn::canRespawnGlowstone, false);
               }

               if (selectedSlot < 0) {
                  return BlockPosHelperDefuse.DefusePick.INSTANCE;
               } else {
                  float squaredValue = this.clientInstance.getAmount();
                  float amountFloat = this.clientInstance.getAmountFloat();
                  BlockHitResult eyeInstance = BlockHitResolver.findBlockHitAmongFaces(himEtaData.cell(), ALL_DIRECTIONS, null, clientValue, squaredValue, amountFloat);
                  if (eyeInstance == null) {
                     return BlockPosHelperDefuse.DefusePick.INSTANCE;
                  } else {
                     int weightTier = DefenseChooser.getWeightTier(getAimTurnAngle(eyePos, eyeInstance.getPos(), squaredValue, amountFloat));
                     AbsorptionPosData self = selfPopData.self();
                     PosBlockUtilsBonus.Armor armor = self.armor() == null ? PosBlockUtilsBonus.Armor.INSTANCE : self.armor();
                     PosBlockUtilsBonus.Window window = new PosBlockUtilsBonus.Window(Math.max(0, selfPopData.selfClosedFor()), self.lastScaledTaken());
                     float scaledInstance = PosBlockUtilsBonus.getScaledInstance(himEtaData.rawUs(), armor, window, weightTier);
                     float entityArmor = this.estimateDamageToTarget(selfPopData, himEtaData, weightTier);
                     return new BlockPosHelperDefuse.DefusePick(new DefenseChooser.Defuse(true, scaledInstance, entityArmor, weightTier), selectedSlot);
                  }
               }
            }
         } else {
            return BlockPosHelperDefuse.DefusePick.INSTANCE;
         }
      } else {
         return BlockPosHelperDefuse.DefusePick.INSTANCE;
      }
   }

   private static BlockPos getPlanSupportCell(BlockPosHelperDefuse.Plan plan) {
      return "face".equals(plan.why()) && plan.r().cell() != null ? plan.r().cell().down() : plan.anchor();
   }

   private BlockPosHelperDefuse.Plan getCachedPlan(SelfPopData selfPopData) {
      if (this.cachedPlanTick == selfPopData.tick()) {
         return this.cachedPlan;
      } else {
         BlockPosHelperDefuse.Plan quietNoneData = this.computePlan(selfPopData);
         this.cachedPlan = quietNoneData;
         this.cachedPlanTick = selfPopData.tick();
         if (!this.nether) {
            this.lastPlanKind = quietNoneData.r().kind();
            this.lastPlanCell = quietNoneData.r().cell();
            this.lastPlanScore = quietNoneData.r().ev();
         }

         return quietNoneData;
      }
   }

   public CaseMaxData getGuardAction(SelfPopData selfPopData) {
      if (selfPopData == null) {
         return CaseMaxData.getCaseMaxData(SurroundKitType.GUARD, "none");
      } else if (selfPopData.nether()) {
         return CaseMaxData.getCaseMaxData(SurroundKitType.GUARD, "nether");
      } else if (!this.nether) {
         AbsorptionPosData self = selfPopData.self();
         if (self == null || this.blockPosHelper == null) {
            return CaseMaxData.getCaseMaxData(SurroundKitType.GUARD, "none");
         } else if (self.handSwapBusy()) {
            return CaseMaxData.getCaseMaxData(SurroundKitType.GUARD, "busy");
         } else {
            BlockPosHelperDefuse.Plan quietNone = this.getCachedPlan(selfPopData);
            return quietNone.r().kind() == DefenseChooser.DefenseKind.NONE
               ? CaseMaxData.getCaseMaxData(SurroundKitType.GUARD, quietNone.why())
               : new CaseMaxData(SurroundKitType.GUARD, true, quietNone.r().ev(), quietNone.prep(), false, false, quietNone.why());
         }
      } else {
         String sentGuardText = this.actionPerformed && this.sentKind == DefenseChooser.DefenseKind.DEFUSE
            ? "sent"
            : (this.sentSpotPlan ? "spot" : kindToLabel(this.sentKind));
         if (this.block) {
            sentGuardText = this.actionPerformed ? "sent" : "block";
         }

         return new CaseMaxData(SurroundKitType.GUARD, true, Math.max(0.0, this.sentScore), 0, false, false, sentGuardText);
      }
   }

   static boolean isReductionWorthwhile(float blockPosValue, float blockPosValue2, float blockPosLimit) {
      return isDamageSignificant(blockPosValue, blockPosValue2) && Float.isFinite(blockPosLimit) && blockPosLimit >= 1.5F && blockPosLimit >= 0.4F * blockPosValue;
   }

   private BlockPosHelperDefuse.Plan planFaceShield(SelfPopData selfPopData, ClientPlayerEntity entity, ClientWorld world) {
      VecBlockingData target = selfPopData.target();
      AbsorptionPosData self = selfPopData.self();
      if (target != null && target.onGround()) {
         Vec3d vec3d = target.serverPos() != null ? target.serverPos() : target.pos();
         if (vec3d == null) {
            return null;
         } else {
            BlockPos floored = BlockPos.ofFloored(vec3d.x, vec3d.y + 0.001, vec3d.z);
            if (!IframeComboUtils.canFlag(BlockStateUtilsDirections.getOffsetDirections(world, floored), true)) {
               return null;
            } else {
               Vec3d pos = self.pos();
               if (pos.y - vec3d.y < 0.75) {
                  return null;
               } else {
                  long tick = selfPopData.tick();
                  AmountConstTime.Spot spot = this.getPredictedSpot(target, tick);
                  if (spot == null
                     || spot.y() != floored.getY()
                     || !IframeComboUtils.canFlag(spot.getX(), spot.getZ(), spot.y(), vec3d.x, vec3d.z, floored.getY())) {
                     return null;
                  } else if ((spot.getX() - vec3d.x) * (pos.x - vec3d.x) + (spot.getZ() - vec3d.z) * (pos.z - vec3d.z) <= 0.0) {
                     return null;
                  } else {
                     BlockPos blockPos = new BlockPos(spot.x(), spot.y(), spot.z());
                     if (!isSpotWindowFree(this.shieldSpotPlacedTick, blockPos, tick)) {
                        return null;
                     } else {
                        BlockPos up = blockPos.up();
                        if (!getComboOccupiedCells(this.getActiveCombo(selfPopData)).contains(up) && !isCooldownActive(this.avoidCellUntilTick, up, tick)) {
                           BlockState defaultState = Blocks.OBSIDIAN.getDefaultState();
                           if (BlockPosUtilsRespawn.canReplaceableAir(up)
                              && world.canPlace(defaultState, up, ShapeContext.absent())
                              && !FloorMathUtilsBlock.canInterpolatorLerped(up, FloorMathUtilsBlock.getValueInt())) {
                              BlockPos ofFloored = BlockPos.ofFloored(pos);
                              boolean[] booleans = new boolean[HORIZONTAL_DIRECTIONS.length];
                              int directionsFaceLimit = -1;

                              for (int i = 0; i < HORIZONTAL_DIRECTIONS.length; i++) {
                                 booleans[i] = !BlockPosUtilsRespawn.canReplaceableAir(ofFloored.offset(HORIZONTAL_DIRECTIONS[i]));
                                 if (ofFloored.offset(HORIZONTAL_DIRECTIONS[i]).equals(up)) {
                                    directionsFaceLimit = i;
                                 }
                              }

                              if (directionsFaceLimit >= 0 && !DefenseChooser.isCountLow(DefenseChooser.countSetFlags(booleans, directionsFaceLimit))) {
                                 return null;
                              } else {
                                 boolean directionsFaceFlag = selfPopData.cfg() == null || selfPopData.cfg().buildObsidian();
                                 if (directionsFaceFlag && self.obsidian() > 0) {
                                    int selectedSlot = this.inventorySelected.findSlotMatching(stack -> stack.isOf(Items.OBSIDIAN), false);
                                    if (selectedSlot < 0) {
                                       return null;
                                    } else if (this.searchBudget != null && !this.searchBudget.tryReserveRays(45, 3)) {
                                       return null;
                                    } else {
                                       PosBlockUtilsBonus.Armor armor = self.armor() == null ? PosBlockUtilsBonus.Armor.INSTANCE : self.armor();
                                       Vec3d vec3d2 = new Vec3d(spot.getX(), spot.getY(), spot.getZ());
                                       float toughnessEpf = PosBlockUtilsBonus.getToughnessEpf(
                                          BlockEntityUtils.getCreativeSpectator(entity, vec3d2, pos, self.box(), null, null, 6.0F), armor
                                       );
                                       if (!isDamageSignificant(toughnessEpf, self.getAbsorptionHealth())) {
                                          return null;
                                       } else {
                                          Vec3d vec3d3 = target.eye() != null ? target.eye() : vec3d.add(0.0, 1.62, 0.0);
                                          boolean directionsFaceFlag2 = world.isAir(up.up()) && vec3d3.distanceTo(Vec3d.ofCenter(up.up())) <= 4.6;
                                          float directionsFaceValue = directionsFaceFlag2
                                             ? PosBlockUtilsBonus.getToughnessEpf(
                                                BlockEntityUtils.getCreativeSpectator(
                                                   entity, BlockEntityUtils.getVec3d(up), pos, self.box(), up, null, 6.0F
                                                ),
                                                armor
                                             )
                                             : 0.0F;
                                          float deltaVar = toughnessEpf - directionsFaceValue;
                                          if (!isReductionWorthwhile(toughnessEpf, self.getAbsorptionHealth(), deltaVar)) {
                                             return null;
                                          } else {
                                             float squaredValue = this.clientInstance.getAmount();
                                             float amountFloat = this.clientInstance.getAmountFloat();
                                             BlockHitResult eyeInstance = BlockHitResolver.findBlockHitAmongFaces(
                                                blockPos, new Direction[]{Direction.UP}, Direction.UP, this.clientInstance.getAmountDouble(), squaredValue, amountFloat
                                             );
                                             if (eyeInstance == null) {
                                                return null;
                                             } else {
                                                DefenseChooser.DefenseChoice response = new DefenseChooser.DefenseChoice(
                                                   DefenseChooser.DefenseKind.SHIELD, up, false, computeShieldValue(deltaVar, spot.interval())
                                                );
                                                return new BlockPosHelperDefuse.Plan(
                                                   response,
                                                   DefenseChooser.getWeightTier(getAimTurnAngle(entity.getEyePos(), eyeInstance.getPos(), squaredValue, amountFloat)),
                                                   selectedSlot,
                                                   null,
                                                   "face",
                                                   0
                                                );
                                             }
                                          }
                                       }
                                    }
                                 } else {
                                    return null;
                                 }
                              }
                           } else {
                              return null;
                           }
                        } else {
                           return null;
                        }
                     }
                  }
               }
            }
         }
      } else {
         return null;
      }
   }

   private BlockPosHelperDefuse.ShieldPick findShieldForAnchor(SelfPopData selfPopData, HimEtaData himEtaData, ClientPlayerEntity entity, ClientWorld clientWorld, int intersectsValue) {
      BlockPos cell = himEtaData.cell();
      if (cell != null && !this.shieldedAnchorTicks.containsKey(cell.asLong())) {
         int intersectsLimit = DefenseChooser.getRemainingSlots(himEtaData.eta(), getSelfPingTicks(selfPopData));
         if (intersectsLimit < 0) {
            return BlockPosHelperDefuse.ShieldPick.INSTANCE;
         } else {
            AbsorptionPosData self = selfPopData.self();
            float absorptionHealth = self.getAbsorptionHealth();
            boolean intersectsFlag = selfPopData.cfg() == null || selfPopData.cfg().buildObsidian();
            int intersectsLimit2 = self.glowstone() > 20 ? this.inventorySelected.findSlotMatching(stack -> stack.isOf(Items.GLOWSTONE), false) : -1;
            int intersectsValue2 = intersectsFlag && self.obsidian() > 0 ? this.inventorySelected.findSlotMatching(stack2 -> stack2.isOf(Items.OBSIDIAN), false) : -1;
            if (intersectsLimit2 < 0 && intersectsValue2 < 0) {
               return BlockPosHelperDefuse.ShieldPick.INSTANCE;
            } else {
               boolean intersectsFlag2 = intersectsLimit2 >= 0;
               int intersectsValue3 = intersectsFlag2 ? intersectsLimit2 : intersectsValue2;
               BlockState blockState = intersectsFlag2 ? Blocks.GLOWSTONE.getDefaultState() : Blocks.OBSIDIAN.getDefaultState();
               boolean intersectsFlag3 = himEtaData.hitUs() >= absorptionHealth - 0.5F;
               Box box = self.box();
               Vec3d pos = self.pos();
               Vec3d vec3d = self.velocity() == null ? Vec3d.ZERO : self.velocity();
               Box expand = box.offset(vec3d.x * (intersectsLimit + 1), 0.0, vec3d.z * (intersectsLimit + 1)).expand(0.05);
               VecBlockingData target = selfPopData.target();
               Box box2 = null;
               Box boundingBox3 = null;
               if (target != null && target.entity() != null) {
                  boundingBox3 = target.entity().getBoundingBox();
                  box2 = BlockEntityUtils.getInterpolatorInstance(target.entity(), Math.max(1, Math.min(8, himEtaData.eta())), false, true).box();
               }

               List crystalAnchor = getComboOccupiedCells(this.getActiveCombo(selfPopData));
               Vec3d center = Vec3d.ofCenter(cell);
               double[][] doubles = DefenseChooser.getBoxSamplePoints(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
               Vec3d center2 = box.getCenter();
               int valueInt = FloorMathUtilsBlock.getValueInt();
               ArrayList arrayList = new ArrayList();

               for (int i = -1; i <= 1; i++) {
                  for (int j = -1; j <= 1; j++) {
                     for (int k = -1; k <= 1; k++) {
                        if (i != 0 || j != 0 || k != 0) {
                           BlockPos add = cell.add(i, j, k);
                           int countRayHitsOnBlock = DefenseChooser.countRayHitsOnBlock(center.x, center.y, center.z, doubles, add.getX(), add.getY(), add.getZ());
                           if (countRayHitsOnBlock > 0 && BlockPosUtilsRespawn.canReplaceableAir(add) && !crystalAnchor.contains(add) && !isCooldownActive(this.avoidCellUntilTick, add, selfPopData.tick())) {
                              Box boxForBlockPos = BlockEntityUtils.getBoxForBlockPos(add);
                              if (!boxForBlockPos.intersects(box.expand(0.05))
                                 && !boxForBlockPos.intersects(expand)
                                 && (box2 == null || !boxForBlockPos.intersects(box2))
                                 && (boundingBox3 == null || !boxForBlockPos.intersects(boundingBox3))
                                 && !FloorMathUtilsBlock.canInterpolatorLerped(add, valueInt)) {
                                 arrayList.add(new BlockPosHelperDefuse.Candidate(add, countRayHitsOnBlock, Vec3d.ofCenter(add).squaredDistanceTo(center2)));
                              }
                           }
                        }
                     }
                  }
               }

               if (arrayList.isEmpty()) {
                  return BlockPosHelperDefuse.ShieldPick.INSTANCE;
               } else {
                  arrayList.sort(Comparator.<BlockPosHelperDefuse.Candidate>comparingInt(candidate -> -candidate.hits()).thenComparingDouble(BlockPosHelperDefuse.Candidate::dist));
                  BlockPos floored = BlockPos.ofFloored(pos);
                  boolean[] booleans = new boolean[HORIZONTAL_DIRECTIONS.length];

                  for (int m = 0; m < HORIZONTAL_DIRECTIONS.length; m++) {
                     booleans[m] = !BlockPosUtilsRespawn.canReplaceableAir(floored.offset(HORIZONTAL_DIRECTIONS[m]));
                  }

                  PosBlockUtilsBonus.Armor armor = self.armor() == null ? PosBlockUtilsBonus.Armor.INSTANCE : self.armor();
                  PosBlockUtilsBonus.Window window = new PosBlockUtilsBonus.Window(Math.max(0, selfPopData.selfClosedFor()), self.lastScaledTaken());
                  Vec3d eyePos = entity.getEyePos();
                  double amountDouble = this.clientInstance.getAmountDouble();
                  float blockPosValue = this.clientInstance.getAmount();
                  float amountFloat = this.clientInstance.getAmountFloat();
                  DefenseChooser.Shield shield = DefenseChooser.Shield.INSTANCE;
                  float negativeInfinity = Float.NEGATIVE_INFINITY;
                  int hits9 = 0;
                  int intersectsCounter = 0;

                  for (BlockPosHelperDefuse.Candidate candidate2 : (Iterable<com.corz.client.utils.BlockPosHelperDefuse.Candidate>) (arrayList)) {
                     if (intersectsCounter++ >= 3) {
                        break;
                     }

                     BlockPos cell2 = candidate2.cell();
                     int constLimit = -1;

                     for (int p = 0; p < HORIZONTAL_DIRECTIONS.length; p++) {
                        if (floored.offset(HORIZONTAL_DIRECTIONS[p]).equals(cell2)) {
                           constLimit = p;
                        }
                     }

                     if (DefenseChooser.isCountLow(DefenseChooser.countSetFlags(booleans, constLimit))
                        && (intersectsFlag2 || intersectsFlag3 || !this.canTargetPunishCell(cell2, clientWorld, pos, target, armor, entity))
                        && clientWorld.canPlace(blockState, cell2, ShapeContext.absent())) {
                        BlockPosHelperDefuse.SupportAim oppositeOffset = this.findSupportAim(cell2, cell, eyePos, amountDouble, blockPosValue, amountFloat);
                        if (!Double.isNaN(oppositeOffset.turn()) || oppositeOffset.now()) {
                           int distanceTier = DefenseChooser.getDistanceTier(oppositeOffset.turn(), oppositeOffset.now(), HumanAimRotator.getMaxStepDegrees());
                           if (distanceTier <= intersectsLimit) {
                              if (this.searchBudget != null && !this.searchBudget.tryReserveRays(45, 2)) {
                                 break;
                              }

                              float creativeSpectator = BlockEntityUtils.getCreativeSpectator(entity, center, pos, box, cell2, cell, 5.0F);
                              float intersectsValue12 = himEtaData.hitUs() - PosBlockUtilsBonus.getReadyInstance(creativeSpectator, armor, window, himEtaData.eta());
                              if (DefenseChooser.isWithinActionWindow(intersectsValue12, himEtaData.hitUs(), distanceTier, intersectsLimit, intersectsValue)) {
                                 float reducedValue = DefenseChooser.getReducedValue(intersectsValue12, distanceTier);
                                 if (reducedValue > negativeInfinity || reducedValue == negativeInfinity && candidate2.hits() > hits9) {
                                    shield = new DefenseChooser.Shield(true, cell2, intersectsFlag2, intersectsValue12, distanceTier);
                                    negativeInfinity = reducedValue;
                                    hits9 = candidate2.hits();
                                 }
                              }
                           }
                        }
                     }
                  }

                  return shield.feasible() ? new BlockPosHelperDefuse.ShieldPick(shield, intersectsValue3) : BlockPosHelperDefuse.ShieldPick.INSTANCE;
               }
            }
         }
      } else {
         return BlockPosHelperDefuse.ShieldPick.INSTANCE;
      }
   }

   private void appendTrace(String builderText, BlockPos pos) {
      if (pos != null && this.traceEntryCount < 16) {
         if (!this.traceBuilder.isEmpty()) {
            this.traceBuilder.append(' ');
         }

         this.traceBuilder.append(builderText).append(pos.getX()).append(',').append(pos.getY()).append(',').append(pos.getZ());
         this.traceEntryCount++;
      }
   }

   private boolean isPredictedSpot(VecBlockingData vecBlockingData, BlockPos pos, long blockPosValue) {
      AmountConstTime.Spot spot = this.getPredictedSpot(vecBlockingData, blockPosValue);
      return spot != null && pos != null && spot.x() == pos.getX() && spot.y() == pos.getY() && spot.z() == pos.getZ();
   }

   private BlockPosHelperDefuse.SupportAim findSupportAim(BlockPos pos, BlockPos pos2, Vec3d vec, double oppositeOffsetValue, float blockPosValue, float amountFloat) {
      double naN = Double.NaN;
      boolean oppositeOffsetFlag = false;

      for (Direction direction : ALL_DIRECTIONS) {
         BlockPos offset = pos.offset(direction);
         if (!offset.equals(pos2)
            && !BlockPosUtilsRespawn.canReplaceableAir(offset)
            && !FloorMathUtilsBlock.canInstanceWorld(offset)
            && !FloorMathUtilsBlock.canCenterBlast(offset)) {
            Direction opposite = direction.getOpposite();
            if (BlockEntityUtils.canBlockSquare(offset, opposite)) {
               if (BlockHitResolver.findBlockHitOnFace(offset, opposite, oppositeOffsetValue, blockPosValue, amountFloat) != null) {
                  oppositeOffsetFlag = true;
               }

               BlockHitResult eyeInstance = BlockHitResolver.findBlockHitAmongFaces(offset, new Direction[]{opposite}, opposite, oppositeOffsetValue, blockPosValue, amountFloat);
               if (eyeInstance != null) {
                  double aimTurnAngle = getAimTurnAngle(vec, eyeInstance.getPos(), blockPosValue, amountFloat);
                  if (Double.isNaN(naN) || aimTurnAngle < naN) {
                     naN = aimTurnAngle;
                  }
               }
            }
         }
      }

      return Double.isNaN(naN) && !oppositeOffsetFlag ? BlockPosHelperDefuse.SupportAim.INSTANCE : new BlockPosHelperDefuse.SupportAim(naN, oppositeOffsetFlag);
   }

   private CenterVecData getActiveCombo(SelfPopData selfPopData) {
      return selfPopData.combo() != null && selfPopData.combo() != CenterVecData.INSTANCE ? selfPopData.combo() : this.fallbackCombo;
   }

   public void setSpotPredictionProvider(IntFunction<AmountConstTime> intFunction) {
      this.spotPredictionProvider = intFunction;
   }

   public String formatActivePlan() {
      DefenseChooser.DefenseKind kind = this.nether ? this.sentKind : this.lastPlanKind;
      BlockPos blockPos = this.nether ? this.sentCell : this.lastPlanCell;
      double netherDefuseValue = this.nether ? this.sentScore : this.lastPlanScore;
      return kind != null && kind != DefenseChooser.DefenseKind.NONE && blockPos != null
         ? kind.name() + "@" + blockPos.getX() + "," + blockPos.getY() + "," + blockPos.getZ() + ":" + String.format(Locale.ROOT, "%.1f", netherDefuseValue)
         : "-";
   }

   private double findMinSupportTurn(BlockPos pos, BlockPos pos2, Vec3d vec, double oppositeOffsetValue, float blockPosValue, float amountFloat) {
      double naN = Double.NaN;

      for (Direction direction : ALL_DIRECTIONS) {
         BlockPos offset = pos.offset(direction);
         if (!offset.equals(pos2) && !BlockPosUtilsRespawn.canReplaceableAir(offset)) {
            Direction opposite = direction.getOpposite();
            if (BlockEntityUtils.canBlockSquare(offset, opposite)) {
               BlockHitResult eyeInstance = BlockHitResolver.findBlockHitAmongFaces(offset, new Direction[]{opposite}, opposite, oppositeOffsetValue, blockPosValue, amountFloat);
               if (eyeInstance != null) {
                  double aimTurnAngle = getAimTurnAngle(vec, eyeInstance.getPos(), blockPosValue, amountFloat);
                  if (Double.isNaN(naN) || aimTurnAngle < naN) {
                     naN = aimTurnAngle;
                  }
               }
            }
         }
      }

      return naN;
   }

   public int getShieldSentCount() {
      return this.shieldSentCount;
   }

   private void recordSentAction(long dfzShdValue) {
      if (this.sentKind == DefenseChooser.DefenseKind.SHIELD) {
         this.shieldSentCount++;
         this.recentShieldTicks.addLast(dfzShdValue);
         if (this.sentSupportPos != null) {
            (this.sentSpotPlan ? this.shieldSpotPlacedTick : this.shieldedAnchorTicks).put(this.sentSupportPos.asLong(), dfzShdValue);
         }

         this.appendTrace("shd@", this.sentCell);
      } else if (this.sentKind == DefenseChooser.DefenseKind.DEFUSE) {
         this.defuseSentCount++;
         this.appendTrace("dfz@", this.sentCell);
      }
   }

   public BlockPosHelperDefuse(ClientInstance clientInstance, HotbarSlotSwitcher inventorySelected, BlockPosHelperGapple blockPosHelper, RayBudget max) {
      this.shieldedAnchorTicks = new HashMap<>();
      this.defuseFailedUntilTick = new HashMap<>();
      this.recentlyDefusedUntilTick = new HashMap<>();
      this.avoidCellUntilTick = new HashMap<>();
      this.shieldSpotPlacedTick = new HashMap<>();
      this.traceBuilder = new StringBuilder();
      this.fallbackCombo = CenterVecData.INSTANCE;
      this.cachedPlan = BlockPosHelperDefuse.Plan.INSTANCE2;
      this.cachedPlanTick = Long.MIN_VALUE;
      this.sentKind = DefenseChooser.DefenseKind.NONE;
      this.lastPlanKind = DefenseChooser.DefenseKind.NONE;
      this.clientInstance = clientInstance;
      this.inventorySelected = inventorySelected;
      this.blockPosHelper = blockPosHelper;
      this.searchBudget = max;
      this.blockPlacer = new BlockPosHelperBlocked(clientInstance);
   }

   static boolean isSamePlanAsSent(
      DefenseChooser.DefenseKind kind, BlockPos pos, BlockPos pos2, DefenseChooser.DefenseKind defenseKind, BlockPos pos3, BlockPos pos4
   ) {
      if (kind == null || kind == DefenseChooser.DefenseKind.NONE || kind != defenseKind) {
         return false;
      } else {
         return !Objects.equals(pos, pos3) ? false : kind == DefenseChooser.DefenseKind.SHIELD || Objects.equals(pos2, pos4);
      }
   }

   private BlockPosHelperDefuse.ShieldPick findShieldAroundSelf(
      SelfPopData selfPopData, AmountConstTime.Spot spot, BlockPos pos, int directionsBound, ClientPlayerEntity entity, ClientWorld clientWorld, VecBlockingData vecBlockingData
   ) {
      AbsorptionPosData self = selfPopData.self();
      Box box = self.box();
      Vec3d pos2 = self.pos();
      float absorptionHealth = self.getAbsorptionHealth();
      PosBlockUtilsBonus.Armor armor = self.armor() == null ? PosBlockUtilsBonus.Armor.INSTANCE : self.armor();
      Vec3d vec3d = new Vec3d(spot.getX(), spot.getY(), spot.getZ());
      if (pos2.distanceTo(vec3d) > 12.0) {
         return BlockPosHelperDefuse.ShieldPick.INSTANCE;
      } else if (this.searchBudget != null && !this.searchBudget.tryReserveRays(45, 3)) {
         return BlockPosHelperDefuse.ShieldPick.INSTANCE;
      } else {
         float toughnessEpf = PosBlockUtilsBonus.getToughnessEpf(BlockEntityUtils.getCreativeSpectator(entity, vec3d, pos2, box, null, null, 6.0F), armor);
         if (!isDamageSignificant(toughnessEpf, absorptionHealth)) {
            return BlockPosHelperDefuse.ShieldPick.INSTANCE;
         } else {
            Box box2 = null;
            Box boundingBox3 = null;
            if (vecBlockingData != null && vecBlockingData.entity() != null) {
               boundingBox3 = vecBlockingData.entity().getBoundingBox();
               box2 = BlockEntityUtils.getInterpolatorInstance(vecBlockingData.entity(), Math.max(1, Math.min(8, directionsBound)), false, true).box();
            }

            List crystalAnchor = getComboOccupiedCells(this.getActiveCombo(selfPopData));
            double[][] doubles = DefenseChooser.getBoxSamplePoints(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
            Vec3d center = box.getCenter();
            BlockPos floored = BlockPos.ofFloored(pos2);
            BlockPos up = pos.up();
            int directionsValue = FloorMathUtilsBlock.getValueInt();
            ArrayList arrayList = new ArrayList();

            for (int i = -1; i <= 1; i++) {
               for (int j = 0; j <= 1; j++) {
                  for (int k = -1; k <= 1; k++) {
                     if (i != 0 || k != 0) {
                        BlockPos add = floored.add(i, j, k);
                        if (!add.equals(pos) && !add.equals(up)) {
                           int directionsLimit = DefenseChooser.countRayHitsOnBlock(vec3d.x, vec3d.y, vec3d.z, doubles, add.getX(), add.getY(), add.getZ());
                           if (directionsLimit > 0
                              && BlockPosUtilsRespawn.canReplaceableAir(add)
                              && !crystalAnchor.contains(add)
                              && !isCooldownActive(this.avoidCellUntilTick, add, selfPopData.tick())) {
                              Box boxForBlockPos = BlockEntityUtils.getBoxForBlockPos(add);
                              if (!boxForBlockPos.intersects(box.expand(0.05))
                                 && (box2 == null || !boxForBlockPos.intersects(box2))
                                 && (boundingBox3 == null || !boxForBlockPos.intersects(boundingBox3))
                                 && !FloorMathUtilsBlock.canInterpolatorLerped(add, directionsValue)) {
                                 arrayList.add(new BlockPosHelperDefuse.Candidate(add, directionsLimit, Vec3d.ofCenter(add).squaredDistanceTo(center)));
                              }
                           }
                        }
                     }
                  }
               }
            }

            if (arrayList.isEmpty()) {
               return BlockPosHelperDefuse.ShieldPick.INSTANCE;
            } else {
               arrayList.sort(Comparator.<BlockPosHelperDefuse.Candidate>comparingInt(candidate -> -candidate.hits()).thenComparingDouble(BlockPosHelperDefuse.Candidate::dist));
               boolean[] booleans = new boolean[HORIZONTAL_DIRECTIONS.length];

               for (int m = 0; m < HORIZONTAL_DIRECTIONS.length; m++) {
                  booleans[m] = !BlockPosUtilsRespawn.canReplaceableAir(floored.offset(HORIZONTAL_DIRECTIONS[m]));
               }

               boolean directionsFlag = selfPopData.cfg() == null || selfPopData.cfg().buildObsidian();
               Vec3d eyePos = entity.getEyePos();
               double amountDouble = this.clientInstance.getAmountDouble();
               float blockPosValue = this.clientInstance.getAmount();
               float amountFloat = this.clientInstance.getAmountFloat();
               DefenseChooser.Shield shield = DefenseChooser.Shield.INSTANCE;
               int directionsValue5 = -1;
               int directionsCounter = 0;

               for (BlockPosHelperDefuse.Candidate candidate2 : (Iterable<com.corz.client.utils.BlockPosHelperDefuse.Candidate>) (arrayList)) {
                  if (directionsCounter++ >= 3) {
                     break;
                  }

                  BlockPos cell = candidate2.cell();
                  int constLimit = -1;

                  for (int p = 0; p < HORIZONTAL_DIRECTIONS.length; p++) {
                     if (floored.offset(HORIZONTAL_DIRECTIONS[p]).equals(cell)) {
                        constLimit = p;
                     }
                  }

                  if (DefenseChooser.isCountLow(DefenseChooser.countSetFlags(booleans, constLimit))) {
                     boolean distanceServer = this.canTargetPunishCell(cell, clientWorld, pos2, vecBlockingData, armor, entity);
                     boolean hasTimeBudget = DefenseChooser.hasTimeBudget(distanceServer, directionsFlag ? self.obsidian() : 0, self.glowstone());
                     if (DefenseChooser.isChargeWindowOpen(hasTimeBudget, distanceServer, directionsFlag ? self.obsidian() : 0, self.glowstone())) {
                        BlockState blockState = hasTimeBudget ? Blocks.GLOWSTONE.getDefaultState() : Blocks.OBSIDIAN.getDefaultState();
                        if (clientWorld.canPlace(blockState, cell, ShapeContext.absent())) {
                           int directionsLimit2 = hasTimeBudget
                              ? this.inventorySelected.findSlotMatching(stack -> stack.isOf(Items.GLOWSTONE), false)
                              : this.inventorySelected.findSlotMatching(stack2 -> stack2.isOf(Items.OBSIDIAN), false);
                           if (directionsLimit2 >= 0) {
                              double oppositeOffset = this.findMinSupportTurn(cell, pos, eyePos, amountDouble, blockPosValue, amountFloat);
                              if (!Double.isNaN(oppositeOffset)) {
                                 if (this.searchBudget != null && !this.searchBudget.tryReserveRays(45, 3)) {
                                    break;
                                 }

                                 float creativeSpectator = BlockEntityUtils.getCreativeSpectator(entity, vec3d, pos2, box, cell, null, 6.0F);
                                 float blockPosLimit = toughnessEpf - PosBlockUtilsBonus.getToughnessEpf(creativeSpectator, armor);
                                 if (isReductionWorthwhile(toughnessEpf, absorptionHealth, blockPosLimit) && blockPosLimit > shield.gain()) {
                                    shield = new DefenseChooser.Shield(true, cell, hasTimeBudget, blockPosLimit, DefenseChooser.getWeightTier(oppositeOffset));
                                    directionsValue5 = directionsLimit2;
                                 }
                              }
                           }
                        }
                     }
                  }
               }

               return shield.feasible() ? new BlockPosHelperDefuse.ShieldPick(shield, directionsValue5) : BlockPosHelperDefuse.ShieldPick.INSTANCE;
            }
         }
      }
   }

   public DefenseChooser.DefenseKind getActiveKind() {
      return this.nether ? this.sentKind : this.lastPlanKind;
   }

   static double computeShieldValue(float blockPosFactor, double blockPosValue) {
      double blockPosBound = Double.isFinite(blockPosValue) ? blockPosValue : 6.0;
      return blockPosFactor * Math.min(3.0, 20.0 / Math.max(blockPosBound, 10.0)) - 0.3;
   }

   private static int getSelfPingTicks(SelfPopData selfPopData) {
      return Math.round(Math.max(0, selfPopData.self().pingMs()) / 50.0F);
   }

   public BlockPosHelperBlocked.Result tickBlockAction(SelfPopData selfPopData) {
      if (selfPopData != null && this.blockPosHelper != null) {
         long blockSentLimit = this.clientInstance.getTime();
         if (this.nether && !this.actionPerformed && !this.block) {
            BlockPosHelperDefuse.Plan quietNone = this.getCachedPlan(selfPopData);
            if (!isSamePlanAsSent(quietNone.r().kind(), getPlanSupportCell(quietNone), quietNone.r().cell(), this.sentKind, this.sentSupportPos, this.sentCell)) {
               this.clearSentAction();
            }
         }

         if (this.nether && this.block && !this.actionPerformed && blockSentLimit > this.sentDeadlineTick) {
            handle(this.avoidCellUntilTick, this.sentCell, blockSentLimit, 4);
         }

         if (this.nether && this.block && (this.actionPerformed ? !this.blockPosHelper.canFlag(this.sentSupportPos) : blockSentLimit > this.sentDeadlineTick)) {
            this.clearSentAction();
         }

         if (this.nether
            && this.sentKind == DefenseChooser.DefenseKind.SHIELD
            && !this.actionPerformed
            && (this.sentSpotPlan ? !this.isPredictedSpot(selfPopData.target(), this.sentSupportPos, blockSentLimit) : !this.blockPosHelper.canFlag(this.sentSupportPos))) {
            this.clearSentAction();
         }

         if (!this.nether) {
            BlockPosHelperDefuse.Plan plan = this.getCachedPlan(selfPopData);
            if (plan.r().kind() == DefenseChooser.DefenseKind.NONE) {
               return BlockPosHelperBlocked.Result.IDLE;
            }

            if (!this.tryCommitPlan(plan, blockSentLimit)) {
               if ("face".equals(plan.why())) {
                  handle(this.avoidCellUntilTick, plan.r().cell(), blockSentLimit, 4);
               } else {
                  markCooldown(plan.r().kind() == DefenseChooser.DefenseKind.DEFUSE ? this.defuseFailedUntilTick : this.avoidCellUntilTick, plan.r().cell(), blockSentLimit);
               }

               return BlockPosHelperBlocked.Result.FAILED;
            }
         }

         BlockPosHelperBlocked.Result blockedHit = this.blockPlacer.getBlockedHit();
         if (blockedHit == BlockPosHelperBlocked.Result.ACTED) {
            if (!this.actionPerformed) {
               this.recordSentAction(blockSentLimit);
            }

            this.actionPerformed = true;
            if (this.sentKind == DefenseChooser.DefenseKind.SHIELD) {
               this.blockPosHelper.handleImmutable(this.sentCell, blockSentLimit);
            }
         } else if (blockedHit == BlockPosHelperBlocked.Result.DONE) {
            this.clearSentAction();
         } else if (blockedHit == BlockPosHelperBlocked.Result.FAILED) {
            if (this.facePlan) {
               handle(this.avoidCellUntilTick, this.sentCell, blockSentLimit, 4);
            } else {
               markCooldown(this.sentKind == DefenseChooser.DefenseKind.DEFUSE ? this.defuseFailedUntilTick : this.avoidCellUntilTick, this.sentCell, blockSentLimit);
            }

            this.clearSentAction();
         }

         return blockedHit;
      } else {
         return BlockPosHelperBlocked.Result.IDLE;
      }
   }

   private boolean canTargetPunishCell(BlockPos pos, ClientWorld clientWorld, Vec3d vec, VecBlockingData vecBlockingData, PosBlockUtilsBonus.Armor armor, ClientPlayerEntity entity) {
      BlockPos up = pos.up();
      if (!clientWorld.isAir(up)) {
         return false;
      } else {
         Vec3d vec3d = BlockEntityUtils.getVec3d(pos);
         if (vecBlockingData != null) {
            Vec3d vec3d2 = vecBlockingData.eye() != null ? vecBlockingData.eye() : (vecBlockingData.serverPos() != null ? vecBlockingData.serverPos().add(0.0, 1.62, 0.0) : null);
            if (vec3d2 == null || vec3d2.distanceTo(Vec3d.ofCenter(up)) > 4.6) {
               return false;
            }
         }

         float toughnessEpf = PosBlockUtilsBonus.getToughnessEpf(BlockEntityUtils.getAmount(entity, vec.distanceTo(vec3d), 6.0F), armor);
         return toughnessEpf >= 4.0F;
      }
   }

   public boolean hasCommittedAction() {
      return this.nether;
   }

   public void setFallbackCombo(CenterVecData centerVecData) {
      this.fallbackCombo = centerVecData == null ? CenterVecData.INSTANCE : centerVecData;
   }

   private BlockPosHelperDefuse.Plan computePlan(SelfPopData selfPopData) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      ClientWorld world = instance.world;
      AbsorptionPosData self = selfPopData.self();
      if (player != null && world != null && self != null && self.pos() != null && self.box() != null) {
         long tick = selfPopData.tick();
         this.pruneExpiredEntries(tick);
         float absorptionHealth = self.getAbsorptionHealth();
         CarefulBalancedType carefulBalancedType = selfPopData.cfg() != null && selfPopData.cfg().playstyle() != null ? selfPopData.cfg().playstyle() : CarefulBalancedType.TERMINATOR;
         SelfHazData selfHaz = SelfHazData.getSelfHaz(selfPopData.lastPosture(), carefulBalancedType);
         VecBlockingData target = selfPopData.target();
         float defuseShieldValue = target == null ? Float.POSITIVE_INFINITY : target.getHealthTrusted();
         int intersectsValue = countRecentTicks(this.recentShieldTicks, tick);
         boolean defuseShieldFlag = false;
         int defuseShieldCounter = 0;

         for (HimEtaData himEtaData : this.blockPosHelper.getCopyOf()) {
            boolean gainSufficient = DefenseChooser.isGainSufficient(himEtaData.hitUs(), absorptionHealth, himEtaData.pDet());
            if (gainSufficient || DefenseChooser.isChargeThreatSerious(himEtaData, absorptionHealth)) {
               defuseShieldFlag = true;
               if (!isCooldownActive(this.recentlyDefusedUntilTick, himEtaData.cell(), tick)) {
                  if (defuseShieldCounter++ >= 2) {
                     break;
                  }

                  BlockPosHelperDefuse.DefusePick defusePick = isCooldownActive(this.defuseFailedUntilTick, himEtaData.cell(), tick)
                     ? BlockPosHelperDefuse.DefusePick.INSTANCE
                     : this.chooseDefuse(selfPopData, himEtaData, player);
                  boolean defuseShieldFlag3 = himEtaData.fullUs() >= absorptionHealth - 0.5F && selfPopData.selfClosedFor() <= defusePick.defuse().prepTicks() + getSelfPingTicks(selfPopData);
                  boolean defuseShieldFlag4 = target != null && defusePick.defuse().dealt() >= defuseShieldValue && !target.totemInHand();
                  if (defuseShieldFlag3 && !defuseShieldFlag4) {
                     defusePick = BlockPosHelperDefuse.DefusePick.INSTANCE;
                  }

                  if (defusePick.defuse().feasible() && defusePick.defuse().cost() <= 0.1F) {
                     double defuseShieldBound = DefenseChooser.getDefuseValue(
                        selfHaz.off(), selfHaz.self(), defusePick.defuse().dealt(), defusePick.defuse().cost(), himEtaData.pDet(), himEtaData.hitUs()
                     );
                     DefenseChooser.DefenseChoice response = new DefenseChooser.DefenseChoice(DefenseChooser.DefenseKind.DEFUSE, himEtaData.cell(), false, Math.max(0.0, defuseShieldBound));
                     return new BlockPosHelperDefuse.Plan(response, defusePick.defuse().prepTicks(), defusePick.slot(), himEtaData.cell(), "defuse", 0);
                  }

                  if (gainSufficient && intersectsValue < 4) {
                     BlockPosHelperDefuse.ShieldPick instanceIntersects = this.findShieldForAnchor(selfPopData, himEtaData, player, world, intersectsValue);
                     if (instanceIntersects.shield().feasible()) {
                        DefenseChooser.DefenseChoice defenseChoice = new DefenseChooser.DefenseChoice(
                           DefenseChooser.DefenseKind.SHIELD, instanceIntersects.shield().cell(), instanceIntersects.shield().glowstone(), himEtaData.pDet() * instanceIntersects.shield().gain()
                        );
                        return new BlockPosHelperDefuse.Plan(
                           defenseChoice, instanceIntersects.shield().prepTicks(), instanceIntersects.slot(), himEtaData.cell(), "block", DefenseChooser.getRemainingSlots(himEtaData.eta(), getSelfPingTicks(selfPopData))
                        );
                     }
                  }

                  DefenseChooser.DefenseInput in = new DefenseChooser.DefenseInput(
                     himEtaData, absorptionHealth, self.totemConfirmed(), selfHaz.off(), selfHaz.self(), defusePick.defuse(), DefenseChooser.Shield.INSTANCE, intersectsValue
                  );
                  DefenseChooser.DefenseChoice defuseCell = DefenseChooser.chooseDefenseWithThreshold(in, defuseShieldValue);
                  if (defuseCell.kind() == DefenseChooser.DefenseKind.DEFUSE) {
                     return new BlockPosHelperDefuse.Plan(defuseCell, defusePick.defuse().prepTicks(), defusePick.slot(), himEtaData.cell(), "defuse", 0);
                  }
               }
            }
         }

         BlockPosHelperDefuse.Plan directionsFace = this.planFaceShield(selfPopData, player, world);
         if (directionsFace != null) {
            return directionsFace;
         } else {
            BlockPosHelperDefuse.Plan shieldSpot = this.planShieldAtPredictedSpot(selfPopData, player, world, intersectsValue);
            if (shieldSpot != null) {
               return shieldSpot;
            } else {
               return defuseShieldFlag ? BlockPosHelperDefuse.Plan.INSTANCE2 : BlockPosHelperDefuse.Plan.INSTANCE;
            }
         }
      } else {
         return BlockPosHelperDefuse.Plan.INSTANCE2;
      }
   }

   public void finishDefuse() {
      if (this.nether && this.actionPerformed && this.sentKind == DefenseChooser.DefenseKind.DEFUSE && this.sentCell != null) {
         this.recentlyDefusedUntilTick.merge(this.sentCell.asLong(), this.clientInstance.getTime() + 6L + getPingTicks(), Math::max);
      }

      this.clearSentAction();
   }

   private void pruneExpiredEntries(long dequeArrayLimit) {
      while (!this.recentShieldTicks.isEmpty() && (dequeArrayLimit < this.recentShieldTicks.peekFirst() || dequeArrayLimit - this.recentShieldTicks.peekFirst() >= 40L)) {
         this.recentShieldTicks.removeFirst();
      }

      this.shieldedAnchorTicks.values().removeIf(tickx -> dequeArrayLimit < tickx || dequeArrayLimit - tickx > 200L);
      this.shieldedAnchorTicks.keySet().removeIf(long2 -> !this.blockPosHelper.canFlag(BlockPos.fromLong(long2)));
      this.shieldSpotPlacedTick.values().removeIf(long3 -> dequeArrayLimit < long3 || dequeArrayLimit - long3 > 60L);
      this.defuseFailedUntilTick.values().removeIf(until -> until < dequeArrayLimit);
      this.recentlyDefusedUntilTick.values().removeIf(until -> until < dequeArrayLimit);
      this.avoidCellUntilTick.values().removeIf(until -> until < dequeArrayLimit);
   }

   private AmountConstTime getSpotPrediction(VecBlockingData vecBlockingData) {
      IntFunction intFunction = this.spotPredictionProvider;
      return intFunction != null && vecBlockingData != null ? (AmountConstTime)intFunction.apply(vecBlockingData.id()) : null;
   }

   static boolean isDamageSignificant(float blockPosLimit, float blockPosValue) {
      return Float.isFinite(blockPosLimit) && blockPosLimit >= Math.max(2.0F, 0.15F * blockPosValue);
   }

   static boolean isSpotWindowFree(Map<Long, Long> map, BlockPos pos, long blockPosValue) {
      Long long2 = pos == null ? null : (Long)map.get(pos.asLong());
      return long2 == null || blockPosValue < long2 || blockPosValue - long2 > 60L;
   }

   public String drainTrace() {
      String formatBuilder = this.traceBuilder.toString();
      this.traceBuilder.setLength(0);
      this.traceEntryCount = 0;
      return formatBuilder;
   }

   public int getDefuseSentCount() {
      return this.defuseSentCount;
   }

   public void reset() {
      this.clearSentAction();
      this.recentShieldTicks.clear();
      this.shieldedAnchorTicks.clear();
      this.defuseFailedUntilTick.clear();
      this.recentlyDefusedUntilTick.clear();
      this.avoidCellUntilTick.clear();
      this.shieldSpotPlacedTick.clear();
      this.fallbackCombo = CenterVecData.INSTANCE;
      this.traceBuilder.setLength(0);
      this.traceEntryCount = 0;
      this.cachedPlan = BlockPosHelperDefuse.Plan.INSTANCE2;
      this.cachedPlanTick = Long.MIN_VALUE;
      this.lastPlanKind = DefenseChooser.DefenseKind.NONE;
      this.lastPlanCell = null;
      this.lastPlanScore = 0.0;
      this.defuseSentCount = 0;
      this.shieldSentCount = 0;
   }

   private boolean tryCommitPlan(BlockPosHelperDefuse.Plan plan, long blockDefuseValue) {
      DefenseChooser.DefenseChoice response = plan.r();
      boolean blockDefuseFlag;
      if (response.kind() == DefenseChooser.DefenseKind.DEFUSE) {
         int blockDefuseValue2 = 6 + getPingTicks();
         blockDefuseFlag = this.blockPlacer.canImmutableFlag(response.cell(), plan.slot(), state -> !state.isOf(Blocks.RESPAWN_ANCHOR), blockDefuseValue2);
      } else {
         if (response.kind() != DefenseChooser.DefenseKind.SHIELD) {
            return false;
         }

         int immutableBound = 4 + getPingTicks();
         BlockState blockState = response.glowstone() ? Blocks.GLOWSTONE.getDefaultState() : Blocks.OBSIDIAN.getDefaultState();
         blockDefuseFlag = this.blockPlacer.canImmutableFlag(response.cell(), plan.slot(), blockState, plan.anchor(), immutableBound);
         if (blockDefuseFlag) {
            this.blockPosHelper.handleImmutable(response.cell(), blockDefuseValue);
         }
      }

      if (!blockDefuseFlag) {
         return false;
      } else {
         boolean equals = "face".equals(plan.why());
         this.nether = true;
         this.actionPerformed = false;
         this.sentKind = response.kind();
         this.sentCell = response.cell();
         this.sentSupportPos = equals ? response.cell().down() : plan.anchor();
         this.sentSpotPlan = "spot".equals(plan.why()) || equals;
         this.facePlan = equals;
         this.block = "block".equals(plan.why());
         this.sentDeadlineTick = blockDefuseValue + Math.max(0, plan.deadline());
         this.sentScore = response.ev();
         this.lastPlanKind = this.sentKind;
         this.lastPlanCell = this.sentCell;
         this.lastPlanScore = this.sentScore;
         return true;
      }
   }

   private record Candidate(BlockPos cell, int hits, double dist) {
   }

   private record DefusePick(DefenseChooser.Defuse defuse, int slot) {
      static final BlockPosHelperDefuse.DefusePick INSTANCE = new BlockPosHelperDefuse.DefusePick(DefenseChooser.Defuse.INSTANCE, -1);
   }

   private record Plan(DefenseChooser.DefenseChoice r, int prep, int slot, BlockPos anchor, String why, int deadline) {
      static final BlockPosHelperDefuse.Plan INSTANCE = new BlockPosHelperDefuse.Plan(DefenseChooser.DefenseChoice.INSTANCE, 0, -1, null, "quiet", 0);
      static final BlockPosHelperDefuse.Plan INSTANCE2 = new BlockPosHelperDefuse.Plan(DefenseChooser.DefenseChoice.INSTANCE, 0, -1, null, "none", 0);
   }

   private record ShieldPick(DefenseChooser.Shield shield, int slot) {
      static final BlockPosHelperDefuse.ShieldPick INSTANCE = new BlockPosHelperDefuse.ShieldPick(DefenseChooser.Shield.INSTANCE, -1);
   }

   private record SupportAim(double turn, boolean now) {
      static final BlockPosHelperDefuse.SupportAim INSTANCE = new BlockPosHelperDefuse.SupportAim(Double.NaN, false);
   }
}
