package com.corz.client.entity;

import com.corz.client.inventory.BlockEntityUtils;
import com.corz.client.inventory.EntityPlayerUtilsGliding;
import com.corz.client.world.BoxLengthUtils;
import com.corz.client.world.MathClampUtils;
import com.corz.client.world.PosBlockUtilsBonus;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.BonusLevelData;
import com.corz.client.utils.HandleTime;
import com.corz.client.utils.RefBareData;

public final class UnsafeEntityUtils {
   private static final int UNUSED_ONE = 1;
   private static final MinecraftClient unsafeEntityMinecraftClient = MinecraftClient.getInstance();

   private static UnsafeEntityUtils.Forced getDataForString(String unsafeEntityText) {
      return new UnsafeEntityUtils.Forced(false, unsafeEntityText, 0.0F, 0.0F, 0.0F, Double.NEGATIVE_INFINITY);
   }

   public static UnsafeEntityUtils.Plan getTuningElite(UnsafeEntityUtils.Request request) {
      return request.tuning() != null && request.tuning().eliteMode() ? getAmountPos(request) : getPosObsidian(request);
   }

   private static void handleBoundingBox(
      UnsafeEntityUtils.Ctx ctx, Set<Long> set, List<UnsafeEntityUtils.EliteSpot> list, boolean boundingBoxFlag, int x2, int y2, int z2
   ) {
      long long2 = BlockPos.asLong(x2, y2, z2);
      if (set.add(long2)) {
         UnsafeEntityUtils.Request zeroLivingData = ctx.zeroLivingData;
         BlockPos blockPos = new BlockPos(x2, y2, z2);
         if (!(BlockEntityUtils.getMinMax(ctx.ignored, BlockEntityUtils.getBox(blockPos)) >= 3.0)) {
            Vec3d vec3d = BlockEntityUtils.getVec3d(blockPos);
            if (BlockEntityUtils.canFlag(ctx.ignored, vec3d, zeroLivingData.viewYaw(), ctx.minData.planCone())) {
               boolean bedrockObsidian = BlockEntityUtils.canBedrockObsidian(blockPos);
               HandleTime guards = zeroLivingData.guards();
               if (bedrockObsidian) {
                  if (guards != null && (guards.canTime(long2, zeroLivingData.nowMs()) || !guards.canFlag(long2, zeroLivingData.tick(), 1))) {
                     return;
                  }

                  if (!BlockEntityUtils.canEntitiesInstance(blockPos) || !BlockEntityUtils.canModifyInstance(blockPos)) {
                     return;
                  }
               } else if (!boundingBoxFlag
                  || guards != null && guards.canFlagForLong(long2, zeroLivingData.nowMs())
                  || !BlockEntityUtils.canOppositeOffset(blockPos)
                  || !BlockEntityUtils.isEmpty(blockPos)) {
                  return;
               }

               Box box = BlockEntityUtils.getBox3(blockPos);
               Box box2 = bedrockObsidian ? null : BlockEntityUtils.getBoxForBlockPos(blockPos);
               if (!canIntersectsBox(ctx.livingZeroPredicted, box, box2)) {
                  if (ctx.boundingBoxPredicted == null
                     || !canIntersectsBox(ctx.boundingBoxPredicted, box, box2) && !canIntersectsBox(ctx.ctxPredicted, box, box2)) {
                     if (!canIntersectsBox(ctx.boxBoundingPredicted, box, box2)) {
                        if (ctx.canZeroLiving(vec3d)) {
                           ctx.lootBuildFlag = true;
                        } else {
                           float distanceLiving = getDistanceLiving(ctx, vec3d);
                           if (distanceLiving < ctx.minData.minDamage() && distanceLiving < ctx.targetEffectiveHealth) {
                              ctx.weakUnsafeFlag = true;
                           } else if (ctx.minData.levelGate() && !ctx.tradeMode.ref() && !canBoundingBox(ctx, vec3d) && !(distanceLiving >= ctx.targetEffectiveHealth)) {
                              ctx.loot = true;
                           } else {
                              list.add(new UnsafeEntityUtils.EliteSpot(blockPos, distanceLiving, bedrockObsidian));
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   static BlockEntityUtils.Support getFaceDown(BlockEntityUtils.Support support, BlockPos pos) {
      return support != null && support.pos() != null && support.face() != null && support.pos().offset(support.face()).equals(pos)
         ? support
         : new BlockEntityUtils.Support(pos.down(), Direction.UP);
   }

   private static UnsafeEntityUtils.Forced getLootIntersects(UnsafeEntityUtils.Ctx ctx, BlockPos pos, BlockPos pos2) {
      UnsafeEntityUtils.Request zeroLivingData = ctx.zeroLivingData;
      if (pos == null) {
         return getDataForString("idle");
      } else {
         BlockEntityUtils.Support faceDown = getFaceDown(zeroLivingData.support(), pos);
         if (zeroLivingData.autoObsidian()
            && zeroLivingData.hasObsidian()
            && BlockEntityUtils.isModifyBlocks()
            && (zeroLivingData.guards() == null || !zeroLivingData.guards().canFlagForLong(pos.asLong(), zeroLivingData.nowMs()))
            && BlockEntityUtils.canBlockState(faceDown.pos(), faceDown.face())
            && BlockEntityUtils.isEmpty(pos)) {
            Box box = BlockEntityUtils.getBoxForBlockPos(pos);
            if (!ctx.livingZeroPredicted.box().intersects(box)
               && !ctx.player.getBoundingBox().intersects(box)
               && !ctx.boxBoundingPredicted.box().intersects(box)) {
               BlockHitResult amountsSquared = BlockEntityUtils.getAmountsSquared(
                  faceDown.pos(),
                  new Direction[]{faceDown.face()},
                  faceDown.face(),
                  ctx.ignored,
                  zeroLivingData.fromYaw(),
                  zeroLivingData.fromPitch(),
                  4.4 - ctx.minData.actReachMargin()
               );
               if (amountsSquared == null) {
                  return getDataForString("far");
               } else {
                  Vec3d vec3d = BlockEntityUtils.getVec3d(pos2);
                  if (ctx.canZeroLiving(vec3d)) {
                     return getDataForString("loot");
                  } else {
                     UnsafeEntityUtils.Hit zeroLiving = ctx.getZeroLiving(vec3d, pos2, true, ctx.lootValue, false);
                     double lootIntersectsValue = PosBlockUtilsBonus.getBonusWeight(
                           zeroLiving.targetDamage(), zeroLiving.pops(), zeroLiving.kills(), zeroLiving.selfCost(), ctx.getZeroLiving(amountsSquared.getPos()), true, ctx.minData
                        )
                        + ctx.tradeMode.getAmountForFloat(zeroLiving.knock());
                     return !ctx.canFlag(zeroLiving.selfFull(), zeroLiving.selfCost(), zeroLiving.targetDamage(), zeroLiving.knock(), zeroLiving.pops(), zeroLiving.kills())
                        ? new UnsafeEntityUtils.Forced(false, "unsafe", zeroLiving.targetDamage(), zeroLiving.selfFull(), zeroLiving.selfCost(), lootIntersectsValue)
                        : new UnsafeEntityUtils.Forced(true, "ok", zeroLiving.targetDamage(), zeroLiving.selfFull(), zeroLiving.selfCost(), lootIntersectsValue);
                  }
               }
            } else {
               return getDataForString("blocked");
            }
         } else {
            return getDataForString("can't build");
         }
      }
   }

   private static float getDistanceLiving(UnsafeEntityUtils.Ctx ctx, Vec3d vec) {
      return PosBlockUtilsBonus.getToughnessEpf(BlockEntityUtils.getAmount(ctx.livingEntity, ctx.livingZeroPredicted.feet().distanceTo(vec), 6.0F), ctx.attributeArmorLivingZero);
   }

   private static boolean canBoundingBox(UnsafeEntityUtils.Ctx ctx, Vec3d vec) {
      Vec3d feet = ctx.livingZeroPredicted.feet();
      double deltaY = vec.y - feet.y;
      double hypot = Math.hypot(vec.x - feet.x, vec.z - feet.z);
      return PosBlockUtilsBonus.canLevelAdj(deltaY, hypot, ctx.minData);
   }

   private static UnsafeEntityUtils.Forced getIgnoredFar(UnsafeEntityUtils.Ctx ctx, EndCrystalEntity endCrystalEntity) {
      UnsafeEntityUtils.Request zeroLivingData = ctx.zeroLivingData;
      if (endCrystalEntity != null && endCrystalEntity.isAlive() && !endCrystalEntity.isRemoved()) {
         if (zeroLivingData.guards() != null && zeroLivingData.guards().canFlag(endCrystalEntity.getId(), zeroLivingData.nowMs())) {
            return getDataForString("ignored");
         } else if (BlockEntityUtils.getMinMax(ctx.ignored, endCrystalEntity.getBoundingBox()) >= 3.0) {
            return getDataForString("far");
         } else if (ctx.canZeroLiving(endCrystalEntity.getEntityPos())) {
            return getDataForString("loot");
         } else {
            UnsafeEntityUtils.BreakEval livingZero = ctx.getLivingZero(endCrystalEntity);
            return !livingZero.safe()
               ? new UnsafeEntityUtils.Forced(false, "unsafe", livingZero.targetDamage(), livingZero.selfFull(), livingZero.selfCost(), livingZero.value())
               : new UnsafeEntityUtils.Forced(true, "ok", livingZero.targetDamage(), livingZero.selfFull(), livingZero.selfCost(), livingZero.value());
         }
      } else {
         return getDataForString("gone");
      }
   }

   private static UnsafeEntityUtils.Plan getPosObsidian(UnsafeEntityUtils.Request request) {
      ClientPlayerEntity player = unsafeEntityMinecraftClient.player;
      Vec3d eyePos = player.getEyePos();
      int id = request.target().getId();
      boolean posObsidianFlag = false;
      boolean posObsidianFlag2 = false;
      boolean posObsidianFlag3 = false;
      EndCrystalEntity endCrystalEntity = null;
      double posObsidianValue = -1.0;
      Box expand = player.getBoundingBox().expand(5.0);

      for (EndCrystalEntity getEntitiesByClassItem : unsafeEntityMinecraftClient.world.getEntitiesByClass(EndCrystalEntity.class, expand, endCrystalEntity3 -> endCrystalEntity3.isAlive() && !endCrystalEntity3.isRemoved())) {
         if (!request.guards().canFlag(getEntitiesByClassItem.getId(), request.nowMs()) && !request.guards().canTick(getEntitiesByClassItem.getId(), request.tick(), request.reattackTicks())) {
            Box boundingBox = getEntitiesByClassItem.getBoundingBox();
            if (!(BlockEntityUtils.getMinMax(eyePos, boundingBox) >= 3.0) && BlockEntityUtils.canFlag(eyePos, boundingBox.getCenter(), request.viewYaw(), 90.0)) {
               if (BlockEntityUtils.getVec3d(boundingBox, getEntitiesByClassItem, eyePos, request.fromYaw(), request.fromPitch(), 3.0) == null) {
                  posObsidianFlag3 = true;
               } else {
                  Vec3d entityPos = getEntitiesByClassItem.getEntityPos();
                  if (!BlockEntityUtils.canAbsorptionHealth(entityPos, null, request.antiSuicide(), request.maxSelfDamage())) {
                     posObsidianFlag2 = true;
                  } else {
                     double distanceFeet = BlockEntityUtils.getDistanceFeet(request.predicted(), request.target(), entityPos, null);
                     if (distanceFeet > posObsidianValue) {
                        posObsidianValue = distanceFeet;
                        endCrystalEntity = getEntitiesByClassItem;
                     }
                  }
               }
            }
         }
      }

      if (endCrystalEntity != null) {
         return new UnsafeEntityUtils.Plan(ImmutablePos.getImmutableBreak(endCrystalEntity, posObsidianValue, request.tick(), id), null);
      } else {
         int floor = MathHelper.floor(player.getX());
         int floor3 = MathHelper.floor(player.getY());
         int floor4 = MathHelper.floor(player.getZ());
         boolean posObsidianFlag4 = request.autoObsidian() && request.hasObsidian() && BlockEntityUtils.isModifyBlocks();
         ArrayList arrayList = new ArrayList();
         ArrayList arrayList2 = new ArrayList();

         for (int i = -1; i <= 1; i++) {
            for (int j = -4; j <= 4; j++) {
               for (int k = -4; k <= 4; k++) {
                  BlockPos blockPos = new BlockPos(floor + j, floor3 + i, floor4 + k);
                  if (!(BlockEntityUtils.getMinMax(eyePos, BlockEntityUtils.getBox(blockPos)) >= 3.0)) {
                     Vec3d vec3d = BlockEntityUtils.getVec3d(blockPos);
                     if (BlockEntityUtils.canFlag(eyePos, vec3d, request.viewYaw(), 90.0)) {
                        boolean bedrockObsidian = BlockEntityUtils.canBedrockObsidian(blockPos);
                        long long2 = blockPos.asLong();
                        if (bedrockObsidian
                           ? !request.guards().canTime(long2, request.nowMs())
                              && request.guards().canFlag(long2, request.tick(), request.reattackTicks())
                              && BlockEntityUtils.canEntitiesInstance(blockPos)
                              && BlockEntityUtils.canModifyInstance(blockPos)
                           : posObsidianFlag4 && !request.guards().canFlagForLong(long2, request.nowMs()) && BlockEntityUtils.canFlag(blockPos.down()) && BlockEntityUtils.isEmpty(blockPos)) {
                           Box box = request.predicted().box();
                           if (!box.intersects(BlockEntityUtils.getBox3(blockPos)) && (bedrockObsidian || !box.intersects(BlockEntityUtils.getBoxForBlockPos(blockPos)))) {
                              double posObsidianValue5 = BoxLengthUtils.getAmount(request.predicted().feet().distanceTo(vec3d));
                              if (posObsidianValue5 < request.minEfficiency()) {
                                 posObsidianFlag = true;
                              } else {
                                 (bedrockObsidian ? arrayList : arrayList2).add(new UnsafeEntityUtils.Spot(blockPos, posObsidianValue5));
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         arrayList.sort(Comparator.comparingDouble(UnsafeEntityUtils.Spot::bound).reversed());
         ImmutablePos immutablePos = null;
         double posObsidianLimit = -1.0;
         int posObsidianCounter = 0;

         for (UnsafeEntityUtils.Spot spot : (Iterable<com.corz.client.entity.UnsafeEntityUtils.Spot>) (arrayList)) {
            if (spot.bound() <= posObsidianLimit || posObsidianCounter++ >= 12) {
               break;
            }

            Vec3d vec3d2 = BlockEntityUtils.getVec3d(spot.pos());
            double immutablePlaceValue = BlockEntityUtils.getDistanceFeet(request.predicted(), request.target(), vec3d2, null);
            if (immutablePlaceValue < request.minEfficiency()) {
               posObsidianFlag = true;
            } else if (!(immutablePlaceValue <= posObsidianLimit)) {
               if (!BlockEntityUtils.canAbsorptionHealth(vec3d2, null, request.antiSuicide(), request.maxSelfDamage())) {
                  posObsidianFlag2 = true;
               } else if (BlockEntityUtils.getAmountsSquared(spot.pos(), BlockEntityUtils.idleDirections, null, eyePos, request.fromYaw(), request.fromPitch(), 4.4) == null
                  )
                {
                  posObsidianFlag3 = true;
               } else {
                  immutablePos = ImmutablePos.getImmutablePlace(spot.pos(), immutablePlaceValue, request.tick(), id);
                  posObsidianLimit = immutablePlaceValue;
               }
            }
         }

         if (immutablePos != null) {
            return new UnsafeEntityUtils.Plan(immutablePos, null);
         } else {
            arrayList2.sort(Comparator.comparingDouble(UnsafeEntityUtils.Spot::bound).reversed());
            int posObsidianCounter2 = 0;

            for (UnsafeEntityUtils.Spot spot2 : (Iterable<com.corz.client.entity.UnsafeEntityUtils.Spot>) (arrayList2)) {
               if (spot2.bound() <= posObsidianLimit || posObsidianCounter2++ >= 12) {
                  break;
               }

               Vec3d vec3d3 = BlockEntityUtils.getVec3d(spot2.pos());
               double immutableObsidianValue = BlockEntityUtils.getDistanceFeet(request.predicted(), request.target(), vec3d3, spot2.pos());
               if (immutableObsidianValue < request.minEfficiency()) {
                  posObsidianFlag = true;
               } else if (!(immutableObsidianValue <= posObsidianLimit)) {
                  if (!BlockEntityUtils.canAbsorptionHealth(vec3d3, spot2.pos(), request.antiSuicide(), request.maxSelfDamage())) {
                     posObsidianFlag2 = true;
                  } else if (BlockEntityUtils.getAmountsSquared(
                           spot2.pos().down(), BlockEntityUtils.directions, Direction.UP, eyePos, request.fromYaw(), request.fromPitch(), 4.4
                        )
                        != null
                     && BlockEntityUtils.canSquaredDirections(spot2.pos(), eyePos, 4.4)) {
                     immutablePos = ImmutablePos.getImmutableObsidian(spot2.pos(), immutableObsidianValue, request.tick(), id);
                     posObsidianLimit = immutableObsidianValue;
                  } else {
                     posObsidianFlag3 = true;
                  }
               }
            }

            if (immutablePos != null) {
               return new UnsafeEntityUtils.Plan(immutablePos, null);
            } else {
               String posObsidianText;
               if (posObsidianFlag) {
                  posObsidianText = "too weak";
               } else if (posObsidianFlag2) {
                  posObsidianText = "unsafe";
               } else if (posObsidianFlag3) {
                  posObsidianText = "out of reach";
               } else if (request.autoObsidian() && !request.hasObsidian()) {
                  posObsidianText = "no obsidian";
               } else if (!BlockEntityUtils.isModifyBlocks()) {
                  posObsidianText = "can't build";
               } else {
                  posObsidianText = "no spot";
               }

               return new UnsafeEntityUtils.Plan(null, posObsidianText);
            }
         }
      }
   }

   private UnsafeEntityUtils() {
   }

   private static UnsafeEntityUtils.Forced getBlockedIntersects(UnsafeEntityUtils.Ctx ctx, BlockPos pos, Direction[] directions) {
      UnsafeEntityUtils.Request zeroLivingData = ctx.zeroLivingData;
      if (pos == null) {
         return getDataForString("idle");
      } else if (!BlockEntityUtils.canBedrockObsidian(pos)) {
         return getDataForString("no base");
      } else {
         boolean blockedIntersectsFlag = zeroLivingData.guards() != null && zeroLivingData.guards().canTime(pos.asLong(), zeroLivingData.nowMs());
         BlockHitResult blockHitResult = null;
         if (!blockedIntersectsFlag) {
            if (!BlockEntityUtils.canEntitiesInstance(pos)) {
               return getDataForString("blocked");
            }

            if (!BlockEntityUtils.canModifyInstance(pos)) {
               return getDataForString("can't build");
            }

            Box box = BlockEntityUtils.getBox3(pos);
            if (ctx.livingZeroPredicted.box().intersects(box) || ctx.player.getBoundingBox().intersects(box) || ctx.boxBoundingPredicted.box().intersects(box)) {
               return getDataForString("blocked");
            }
         }

         Vec3d vec3d = BlockEntityUtils.getVec3d(pos);
         if (ctx.canZeroLiving(vec3d)) {
            return getDataForString("loot");
         } else {
            if (!blockedIntersectsFlag) {
               blockHitResult = BlockEntityUtils.getAmountsSquared(pos, directions, null, ctx.ignored, zeroLivingData.fromYaw(), zeroLivingData.fromPitch(), 4.4 - ctx.minData.actReachMargin());
               if (blockHitResult == null && !BlockEntityUtils.canSquaredDirections(pos, ctx.ignored, 4.4)) {
                  return getDataForString("far");
               }
            }

            UnsafeEntityUtils.Hit zeroLiving = ctx.getZeroLiving(vec3d, null, true, ctx.reattackTicks, false);
            double posBlockValue = ctx.getZeroLiving(blockHitResult != null ? blockHitResult.getPos() : BlockEntityUtils.getVec3d(pos, Direction.UP));
            double blockedIntersectsValue = PosBlockUtilsBonus.getBonusWeight(zeroLiving.targetDamage(), zeroLiving.pops(), zeroLiving.kills(), zeroLiving.selfCost(), posBlockValue, false, ctx.minData)
               + ctx.tradeMode.getAmountForFloat(zeroLiving.knock());
            return !ctx.canFlag(zeroLiving.selfFull(), zeroLiving.selfCost(), zeroLiving.targetDamage(), zeroLiving.knock(), zeroLiving.pops(), zeroLiving.kills())
               ? new UnsafeEntityUtils.Forced(false, "unsafe", zeroLiving.targetDamage(), zeroLiving.selfFull(), zeroLiving.selfCost(), blockedIntersectsValue)
               : new UnsafeEntityUtils.Forced(true, blockedIntersectsFlag ? "pending" : "ok", zeroLiving.targetDamage(), zeroLiving.selfFull(), zeroLiving.selfCost(), blockedIntersectsValue);
         }
      }
   }

   public static UnsafeEntityUtils.Forced getDataForRequestKind(
      UnsafeEntityUtils.Request request, ImmutablePos.Kind kind, BlockPos pos, EndCrystalEntity endCrystalEntity, Direction[] directions
   ) {
      return getIdleDirections(request, kind, pos, pos, endCrystalEntity, directions);
   }

   public static double getGuardsAlive(UnsafeEntityUtils.Request request, EndCrystalEntity endCrystalEntity) {
      if (canInstanceWorld(request) && endCrystalEntity != null && endCrystalEntity.isAlive() && !endCrystalEntity.isRemoved()) {
         if (request.guards() != null && request.guards().canFlag(endCrystalEntity.getId(), request.nowMs())) {
            return Double.NaN;
         } else {
            UnsafeEntityUtils.Ctx ctx = new UnsafeEntityUtils.Ctx(request);
            if (ctx.canZeroLiving(endCrystalEntity.getEntityPos())) {
               return Double.NaN;
            } else {
               UnsafeEntityUtils.BreakEval livingZero = ctx.getLivingZero(endCrystalEntity);
               return livingZero.safe() ? livingZero.value() : Double.NaN;
            }
         }
      } else {
         return Double.NaN;
      }
   }

   private static UnsafeEntityUtils.Scan getPosUnsafe(UnsafeEntityUtils.Ctx ctx, boolean posUnsafeFlag) {
      UnsafeEntityUtils.Scan scan = new UnsafeEntityUtils.Scan();
      UnsafeEntityUtils.Request zeroLivingData = ctx.zeroLivingData;
      BonusLevelData minData = ctx.minData;
      Box expand = ctx.player.getBoundingBox().expand(5.0);

      for (EndCrystalEntity next : unsafeEntityMinecraftClient.world.getEntitiesByClass(EndCrystalEntity.class, expand, endCrystalEntity -> endCrystalEntity.isAlive() && !endCrystalEntity.isRemoved())) {
         int id = next.getId();
         if (zeroLivingData.guards() == null || !zeroLivingData.guards().canFlag(id, zeroLivingData.nowMs()) && !zeroLivingData.guards().canTick(id, zeroLivingData.tick(), zeroLivingData.reattackTicks())) {
            Box boundingBox = next.getBoundingBox();
            if (!(BlockEntityUtils.getMinMax(ctx.ignored, boundingBox) >= 3.0) && BlockEntityUtils.canFlag(ctx.ignored, boundingBox.getCenter(), zeroLivingData.viewYaw(), minData.planCone())
               )
             {
               if (BlockEntityUtils.getVec3d(boundingBox, next, ctx.ignored, zeroLivingData.fromYaw(), zeroLivingData.fromPitch(), 3.0) == null) {
                  ctx.unsafe = true;
               } else if (ctx.canZeroLiving(next.getEntityPos())) {
                  ctx.lootBuildFlag = true;
               } else {
                  UnsafeEntityUtils.BreakEval livingZero = ctx.getLivingZero(next);
                  if (!livingZero.safe()) {
                     ctx.unsafeWeakFlag = true;
                  } else if (!PosBlockUtilsBonus.canBreakDamage(livingZero.targetDamage(), livingZero.pops(), livingZero.denial(), minData)
                     && !ctx.tradeMode.canFlag(livingZero.targetDamage(), livingZero.knock(), livingZero.pops())) {
                     ctx.weakUnsafeFlag = true;
                  } else {
                     scan.posUnsafeCandidateList
                        .add(
                           new UnsafeEntityUtils.Candidate(
                              ImmutablePos.Kind.BREAK,
                              BlockEntityUtils.getDown(next).toImmutable(),
                              id,
                              livingZero.targetDamage(),
                              livingZero.selfCost(),
                              livingZero.pops(),
                              livingZero.value()
                           )
                        );
                     if (ctx.tradeMode.on() && ctx.canZeroLivingForEndCrystalEntity(next)) {
                        scan.posUnsafeActive = true;
                     }

                     if (livingZero.value() > scan.bestScore) {
                        scan.bestScore = livingZero.value();
                        scan.next = next;
                     }
                  }
               }
            }
         }
      }

      ArrayList arrayList = new ArrayList();
      ArrayList arrayList2 = new ArrayList();

      for (UnsafeEntityUtils.EliteSpot eliteSpot : getFloorZero(ctx)) {
         (eliteSpot.base() ? arrayList : arrayList2).add(eliteSpot);
      }

      arrayList.sort(Comparator.comparingDouble(UnsafeEntityUtils.EliteSpot::bound).reversed());
      double posUnsafeValue = minData.levelGate() ? Math.max(0.0, (double)minData.adjacentBonus()) : 0.0;
      int posUnsafeCounter = 0;

      for (UnsafeEntityUtils.EliteSpot eliteSpot2 : (Iterable<com.corz.client.entity.UnsafeEntityUtils.EliteSpot>) (arrayList)) {
         if (posUnsafeCounter >= minData.maxEvaluations() || scan.posUnsafeActive) {
            break;
         }

         double posUnsafeValue2 = minData.offWeight() * eliteSpot2.bound() + minData.popBonus() + minData.killBonus() + posUnsafeValue + ctx.tradeMode.getAmount();
         if (posUnsafeFlag && posUnsafeValue2 <= Math.max(scan.posAmount, scan.bestScore)) {
            break;
         }

         posUnsafeCounter++;
         Vec3d vec3d = BlockEntityUtils.getVec3d(eliteSpot2.pos());
         UnsafeEntityUtils.Hit zeroLiving = ctx.getZeroLiving(vec3d, null, posUnsafeCounter <= minData.robustTop(), ctx.reattackTicks, true);
         if (zeroLiving != null) {
            if (!ctx.canFlag(zeroLiving.selfFull(), zeroLiving.selfCost(), zeroLiving.targetDamage(), zeroLiving.knock(), zeroLiving.pops(), zeroLiving.kills())) {
               ctx.unsafeWeakFlag = true;
            } else {
               BlockHitResult amountsSquared = BlockEntityUtils.getAmountsSquared(
                  eliteSpot2.pos(), BlockEntityUtils.idleDirections, null, ctx.ignored, zeroLivingData.fromYaw(), zeroLivingData.fromPitch(), 4.4 - minData.actReachMargin()
               );
               if (amountsSquared == null) {
                  ctx.unsafe = true;
               } else {
                  double amount = PosBlockUtilsBonus.getBonusWeight(
                        zeroLiving.targetDamage(), zeroLiving.pops(), zeroLiving.kills(), zeroLiving.selfCost(), ctx.getZeroLiving(amountsSquared.getPos()), false, minData
                     )
                     + getMinBounding(ctx, vec3d)
                     + ctx.tradeMode.getAmountForFloat(zeroLiving.knock());
                  scan.posUnsafeCandidateList
                     .add(
                        new UnsafeEntityUtils.Candidate(ImmutablePos.Kind.PLACE, eliteSpot2.pos(), -1, zeroLiving.targetDamage(), zeroLiving.selfCost(), zeroLiving.pops(), amount)
                     );
                  if (amount > scan.posAmount) {
                     scan.posAmount = amount;
                     scan.blockPos = eliteSpot2.pos();
                  }
               }
            }
         }
      }

      arrayList2.sort(Comparator.comparingDouble(UnsafeEntityUtils.EliteSpot::bound).reversed());
      int posUnsafeValue3 = scan.blockPos == null && scan.next == null ? minData.maxEvaluations() : Math.min(8, minData.maxEvaluations());
      int posUnsafeCounter2 = 0;

      for (UnsafeEntityUtils.EliteSpot eliteSpot3 : (Iterable<com.corz.client.entity.UnsafeEntityUtils.EliteSpot>) (arrayList2)) {
         if (posUnsafeCounter2 >= posUnsafeValue3 || scan.posUnsafeActive) {
            break;
         }

         double posUnsafeValue4 = minData.offWeight() * eliteSpot3.bound() + minData.popBonus() + minData.killBonus() - minData.obsidianPenalty() + posUnsafeValue + ctx.tradeMode.getAmount();
         if (posUnsafeFlag && posUnsafeValue4 <= Math.max(scan.posUnsafeAmount, Math.max(scan.posAmount, scan.bestScore))) {
            break;
         }

         posUnsafeCounter2++;
         Vec3d vec3d2 = BlockEntityUtils.getVec3d(eliteSpot3.pos());
         UnsafeEntityUtils.Hit zeroLiving2 = ctx.getZeroLiving(vec3d2, eliteSpot3.pos(), posUnsafeCounter2 <= minData.robustTop(), ctx.lootValue, true);
         if (zeroLiving2 != null) {
            if (!ctx.canFlag(zeroLiving2.selfFull(), zeroLiving2.selfCost(), zeroLiving2.targetDamage(), zeroLiving2.knock(), zeroLiving2.pops(), zeroLiving2.kills())) {
               ctx.unsafeWeakFlag = true;
            } else {
               BlockEntityUtils.Support oppositeDownData = BlockEntityUtils.getOppositeImmutable(
                  eliteSpot3.pos(), ctx.ignored, zeroLivingData.fromYaw(), zeroLivingData.fromPitch(), 4.4 - minData.actReachMargin()
               );
               if (oppositeDownData != null && BlockEntityUtils.canSquaredDirections(eliteSpot3.pos(), ctx.ignored, 4.4)) {
                  double posBlockValue = ctx.getZeroLiving(BlockEntityUtils.getVec3d(oppositeDownData.pos(), oppositeDownData.face()));
                  double amount2 = PosBlockUtilsBonus.getBonusWeight(zeroLiving2.targetDamage(), zeroLiving2.pops(), zeroLiving2.kills(), zeroLiving2.selfCost(), posBlockValue, true, minData)
                     + getMinBounding(ctx, vec3d2)
                     + ctx.tradeMode.getAmountForFloat(zeroLiving2.knock());
                  scan.posUnsafeCandidateList
                     .add(
                        new UnsafeEntityUtils.Candidate(
                           ImmutablePos.Kind.OBSIDIAN, eliteSpot3.pos(), -1, zeroLiving2.targetDamage(), zeroLiving2.selfCost(), zeroLiving2.pops(), amount2
                        )
                     );
                  if (amount2 > scan.posUnsafeAmount) {
                     scan.posUnsafeAmount = amount2;
                     scan.posUnsafeBlockPos = eliteSpot3.pos();
                     scan.oppositeDownData = oppositeDownData;
                  }
               } else {
                  ctx.unsafe = true;
               }
            }
         }
      }

      return scan;
   }

   public static EndCrystalEntity getTuningExpand(UnsafeEntityUtils.Request request, BlockPos pos) {
      if (canInstanceWorld(request) && request.tuning() != null && request.tuning().isTradeMode() && request.guards() != null) {
         UnsafeEntityUtils.Ctx ctx = new UnsafeEntityUtils.Ctx(request);
         Box expand = ctx.player.getBoundingBox().expand(5.0);

         for (EndCrystalEntity tuningExpand : unsafeEntityMinecraftClient.world.getEntitiesByClass(EndCrystalEntity.class, expand, endCrystalEntity -> endCrystalEntity.isAlive() && !endCrystalEntity.isRemoved())) {
            if ((pos == null || !BlockEntityUtils.getDown(tuningExpand).equals(pos)) && ctx.canZeroLiving(tuningExpand)) {
               return tuningExpand;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static UnsafeEntityUtils.Forced getDirectionsIdle(UnsafeEntityUtils.Request request, ImmutablePos.Kind kind, BlockPos pos, EndCrystalEntity endCrystalEntity) {
      return getDataForRequestKind(request, kind, pos, endCrystalEntity, BlockEntityUtils.idleDirections);
   }

   private static double getMinBounding(UnsafeEntityUtils.Ctx ctx, Vec3d vec) {
      if (!ctx.minData.levelGate()) {
         return 0.0;
      } else {
         Vec3d feet = ctx.livingZeroPredicted.feet();
         double deltaY = vec.y - feet.y;
         double hypot = Math.hypot(vec.x - feet.x, vec.z - feet.z);
         return PosBlockUtilsBonus.getLevelPref(deltaY, hypot, ctx.minData);
      }
   }

   public static UnsafeEntityUtils.Forced getIdleDirections(
      UnsafeEntityUtils.Request request, ImmutablePos.Kind kind, BlockPos pos, BlockPos pos2, EndCrystalEntity endCrystalEntity, Direction[] directions
   ) {
      if (canInstanceWorld(request) && kind != null) {
         UnsafeEntityUtils.Ctx ctx = new UnsafeEntityUtils.Ctx(request);
         Direction[] directions2 = directions != null && directions.length != 0 ? directions : BlockEntityUtils.idleDirections;

         return switch (kind) {
            case BREAK -> getIgnoredFar(ctx, endCrystalEntity);
            case PLACE -> getBlockedIntersects(ctx, pos, directions2);
            case OBSIDIAN -> getLootIntersects(ctx, pos, pos2 == null ? pos : pos2);
         };
      } else {
         return getDataForString("idle");
      }
   }

   private static UnsafeEntityUtils.Plan getAmountPos(UnsafeEntityUtils.Request request) {
      UnsafeEntityUtils.Ctx ctx = new UnsafeEntityUtils.Ctx(request);
      UnsafeEntityUtils.Scan posUnsafe = getPosUnsafe(ctx, true);
      UnsafeEntityUtils.Request request2 = posUnsafe.isPosBlock() && ctx.weakUnsafeFlag ? request.getTuning() : null;
      if (request2 != null) {
         request = request2;
         ctx = new UnsafeEntityUtils.Ctx(request2);
         posUnsafe = getPosUnsafe(ctx, true);
      }

      int id = ctx.livingEntity.getId();
      if (posUnsafe.next != null && posUnsafe.bestScore >= posUnsafe.posAmount && posUnsafe.bestScore >= posUnsafe.posUnsafeAmount) {
         return new UnsafeEntityUtils.Plan(ImmutablePos.getImmutableBreak(posUnsafe.next, posUnsafe.bestScore, request.tick(), id), null);
      } else if (posUnsafe.blockPos != null && posUnsafe.posAmount >= posUnsafe.posUnsafeAmount) {
         return new UnsafeEntityUtils.Plan(ImmutablePos.getImmutablePlace(posUnsafe.blockPos, posUnsafe.posAmount, request.tick(), id), null);
      } else if (posUnsafe.posUnsafeBlockPos != null) {
         BlockEntityUtils.Support oppositeDownData = posUnsafe.oppositeDownData;
         return new UnsafeEntityUtils.Plan(
            oppositeDownData == null
               ? ImmutablePos.getImmutableObsidian(posUnsafe.posUnsafeBlockPos, posUnsafe.posUnsafeAmount, request.tick(), id)
               : ImmutablePos.getImmutableObsidian(posUnsafe.posUnsafeBlockPos, posUnsafe.posUnsafeAmount, request.tick(), id, oppositeDownData.pos(), oppositeDownData.face()),
            null
         );
      } else {
         return new UnsafeEntityUtils.Plan(null, ctx.formatUnsafeWeak());
      }
   }

   public static List<UnsafeEntityUtils.Candidate> getWeakUnsafe(UnsafeEntityUtils.Request request, int weakUnsafeLimit) {
      if (canInstanceWorld(request) && weakUnsafeLimit > 0) {
         UnsafeEntityUtils.Ctx ctx = new UnsafeEntityUtils.Ctx(request);
         UnsafeEntityUtils.Scan posUnsafe = getPosUnsafe(ctx, weakUnsafeLimit <= 1);
         UnsafeEntityUtils.Request request2 = posUnsafe.isPosBlock() && ctx.weakUnsafeFlag ? request.getTuning() : null;
         if (request2 != null) {
            posUnsafe = getPosUnsafe(new UnsafeEntityUtils.Ctx(request2), weakUnsafeLimit <= 1);
         }

         ArrayList arrayList = new ArrayList<>(posUnsafe.posUnsafeCandidateList);
         arrayList.sort(Comparator.comparingDouble(UnsafeEntityUtils.Candidate::value).reversed());
         return arrayList.size() <= weakUnsafeLimit ? arrayList : new ArrayList<>(arrayList.subList(0, weakUnsafeLimit));
      } else {
         return List.of();
      }
   }

   private static List<UnsafeEntityUtils.EliteSpot> getFloorZero(UnsafeEntityUtils.Ctx ctx) {
      BonusLevelData minData = ctx.minData;
      HashSet hashSet = new HashSet();
      ArrayList arrayList = new ArrayList();
      boolean floorZeroFlag = ctx.zeroLivingData.autoObsidian() && ctx.zeroLivingData.hasObsidian() && BlockEntityUtils.isModifyBlocks();
      if (!minData.levelGate()) {
         int floorZeroValue = MathHelper.floor(ctx.player.getX());
         int floor = MathHelper.floor(ctx.player.getY());
         int floor3 = MathHelper.floor(ctx.player.getZ());
         int maxRadius = Math.max(0, minData.radius());

         for (int i = minData.dyMin(); i <= minData.dyMax(); i++) {
            for (int j = -maxRadius; j <= maxRadius; j++) {
               for (int k = -maxRadius; k <= maxRadius; k++) {
                  handleBoundingBox(ctx, hashSet, arrayList, floorZeroFlag, floorZeroValue + j, floor + i, floor3 + k);
               }
            }
         }
      }

      Vec3d feet = ctx.livingZeroPredicted.feet();
      int floor4 = MathHelper.floor(feet.x);
      int floor5 = MathHelper.floor(feet.y);
      int floor6 = MathHelper.floor(feet.z);
      int maxTargetRadius = Math.max(0, minData.targetRadius());

      for (int m = minData.targetDyMin(); m <= minData.targetDyMax(); m++) {
         for (int p = -maxTargetRadius; p <= maxTargetRadius; p++) {
            for (int q = -maxTargetRadius; q <= maxTargetRadius; q++) {
               handleBoundingBox(ctx, hashSet, arrayList, floorZeroFlag, floor4 + p, floor5 + m, floor6 + q);
            }
         }
      }

      return arrayList;
   }

   private static boolean canIntersectsBox(BlockEntityUtils.Predicted predicted, Box box, Box box2) {
      return predicted.box().intersects(box) || box2 != null && predicted.box().intersects(box2);
   }

   private static boolean canBlockingExplosion(LivingEntity entity, Vec3d vec) {
      return entity.isBlocking() && EntityPlayerUtilsGliding.canDelayHead(entity, vec, DamageTypes.PLAYER_EXPLOSION);
   }

   private static boolean canInstanceWorld(UnsafeEntityUtils.Request request) {
      return request != null && request.target() != null && unsafeEntityMinecraftClient.player != null && unsafeEntityMinecraftClient.world != null;
   }

   public static double getMinSelf(UnsafeEntityUtils.Request request, BlockPos pos, BlockPos pos2) {
      if (canInstanceWorld(request) && pos != null) {
         UnsafeEntityUtils.Ctx ctx = new UnsafeEntityUtils.Ctx(request);
         if (ctx.minData.eliteMode() && ctx.minData.maxEvaluations() <= 0) {
            return Double.NaN;
         } else {
            Box box = BlockEntityUtils.getBox3(pos);
            Box box2 = pos2 == null ? null : BlockEntityUtils.getBoxForBlockPos(pos2);
            if (!canIntersectsBox(ctx.livingZeroPredicted, box, box2) && !canIntersectsBox(ctx.boxBoundingPredicted, box, box2)) {
               Vec3d vec3d = BlockEntityUtils.getVec3d(pos);
               if (ctx.canZeroLiving(vec3d)) {
                  return Double.NaN;
               } else {
                  boolean selfFlag = pos2 != null;
                  UnsafeEntityUtils.Hit zeroLiving = ctx.getZeroLiving(vec3d, pos2, true, selfFlag ? ctx.lootValue : ctx.reattackTicks, true);
                  UnsafeEntityUtils.Request request2 = zeroLiving == null ? request.getTuning() : null;
                  if (request2 != null) {
                     ctx = new UnsafeEntityUtils.Ctx(request2);
                     zeroLiving = ctx.getZeroLiving(vec3d, pos2, true, selfFlag ? ctx.lootValue : ctx.reattackTicks, true);
                  }

                  if (zeroLiving == null) {
                     return Double.NaN;
                  } else {
                     boolean selfFlag2 = ctx.minData.tradeRule()
                        ? ctx.canFlag(zeroLiving.selfFull(), zeroLiving.selfCost(), zeroLiving.targetDamage(), zeroLiving.knock(), zeroLiving.pops(), zeroLiving.kills())
                        : ctx.canZeroLiving(zeroLiving.selfFull(), zeroLiving.selfCost(), zeroLiving.kills());
                     if (!selfFlag2) {
                        return Double.NaN;
                     } else if (ctx.minData.levelGate()
                        && !ctx.tradeMode.ref()
                        && !canBoundingBox(ctx, vec3d)
                        && !zeroLiving.pops()
                        && !(getDistanceLiving(ctx, vec3d) >= ctx.targetEffectiveHealth)) {
                        return Double.NaN;
                     } else {
                        BlockHitResult amountsSquared = BlockEntityUtils.getAmountsSquared(
                           pos,
                           BlockEntityUtils.idleDirections,
                           null,
                           ctx.ignored,
                           request.fromYaw(),
                           request.fromPitch(),
                           4.4 - ctx.minData.actReachMargin()
                        );
                        double posBlockValue = ctx.getZeroLiving(amountsSquared != null ? amountsSquared.getPos() : BlockEntityUtils.getVec3d(pos, Direction.UP));
                        return PosBlockUtilsBonus.getBonusWeight(zeroLiving.targetDamage(), zeroLiving.pops(), zeroLiving.kills(), zeroLiving.selfCost(), posBlockValue, selfFlag, ctx.minData)
                           + ctx.tradeMode.getAmountForFloat(zeroLiving.knock())
                           + getMinBounding(ctx, vec3d);
                     }
                  }
               }
            } else {
               return Double.NaN;
            }
         }
      } else {
         return Double.NaN;
      }
   }

   private record BreakEval(
      float targetDamage, float selfFull, float selfCost, boolean pops, boolean kills, double denial, boolean safe, double value, float knock
   ) {
   }

   public record Candidate(ImmutablePos.Kind kind, BlockPos base, int crystalId, float targetDamage, float selfCost, boolean pops, double value) {
   }

   private static final class Ctx {
      boolean lootBuildFlag;
      final BlockEntityUtils.Predicted boxBoundingPredicted;
      final RefBareData tradeMode;
      boolean weakUnsafeFlag;
      final PosBlockUtilsBonus.Armor attributeArmor;
      final UnsafeEntityUtils.Request zeroLivingData;
      final BlockEntityUtils.Predicted boundingBoxPredicted;
      boolean loot;
      boolean unsafe;
      final float targetEffectiveHealth;
      final int reattackTicks;
      final BlockEntityUtils.Predicted livingZeroPredicted;
      final float zeroLivingAmount;
      final int lootValue;
      final ClientPlayerEntity player;
      final BonusLevelData minData;
      final LivingEntity livingEntity;
      final BlockEntityUtils.Predicted ctxPredicted;
      final Vec3d ignored;
      boolean unsafeWeakFlag;
      final PosBlockUtilsBonus.Armor attributeArmorLivingZero;

      boolean canZeroLiving(float zeroLivingLimit, float zeroLivingValue, boolean zeroLivingFlag) {
         if (this.tradeMode.ref()) {
            return this.tradeMode.canBare(zeroLivingLimit, this.zeroLivingAmount, this.tradeMode.canFlag(zeroLivingFlag));
         } else {
            boolean zeroLivingFlag2 = zeroLivingLimit > 0.0F && zeroLivingLimit >= this.zeroLivingAmount - 1.0F;
            return (!zeroLivingFlag2 || this.zeroLivingData.allowTotemTrade() && zeroLivingFlag) && zeroLivingValue <= this.zeroLivingData.maxSelfDamage();
         }
      }

      double getZeroLiving(Vec3d vec) {
         float[] wrapDegrees = MathClampUtils.getWrapDegrees(this.ignored, vec);
         return BoxLengthUtils.getAmount(this.zeroLivingData.fromYaw(), this.zeroLivingData.fromPitch(), wrapDegrees[0], wrapDegrees[1]);
      }

      boolean canZeroLiving(Vec3d vec) {
         return this.zeroLivingData.veto() != null && this.zeroLivingData.veto().test(vec);
      }

      float getBoxBounding(Vec3d vec, BlockPos pos) {
         float creativeSpectator = BlockEntityUtils.getCreativeSpectator(
            this.player, vec, this.player.getEntityPos(), this.player.getBoundingBox(), pos, null, 6.0F
         );
         return this.zeroLivingData.self() == null
            ? creativeSpectator
            : Math.max(
               creativeSpectator,
               BlockEntityUtils.getCreativeSpectator(this.player, vec, this.boxBoundingPredicted.feet(), this.boxBoundingPredicted.box(), pos, null, 6.0F)
            );
      }

      boolean canZeroLiving(EndCrystalEntity endCrystalEntity) {
         if (endCrystalEntity != null && endCrystalEntity.isAlive() && !endCrystalEntity.isRemoved() && this.canZeroLivingForEndCrystalEntity(endCrystalEntity)) {
            int id = endCrystalEntity.getId();
            if (!this.zeroLivingData.guards().canFlag(id, this.zeroLivingData.nowMs())
               && !this.zeroLivingData.guards().canTick(id, this.zeroLivingData.tick(), this.zeroLivingData.reattackTicks())) {
               Box boundingBox = endCrystalEntity.getBoundingBox();
               if (BlockEntityUtils.getMinMax(this.ignored, boundingBox) >= 3.0) {
                  return false;
               } else if (BlockEntityUtils.getVec3d(boundingBox, endCrystalEntity, this.ignored, this.zeroLivingData.fromYaw(), this.zeroLivingData.fromPitch(), 3.0) == null) {
                  return false;
               } else if (this.canZeroLiving(endCrystalEntity.getEntityPos())) {
                  return false;
               } else {
                  UnsafeEntityUtils.BreakEval livingZero = this.getLivingZero(endCrystalEntity);
                  return livingZero.safe()
                     && (
                        PosBlockUtilsBonus.canBreakDamage(livingZero.targetDamage(), livingZero.pops(), livingZero.denial(), this.minData)
                           || this.tradeMode.canFlag(livingZero.targetDamage(), livingZero.knock(), livingZero.pops())
                     );
               }
            } else {
               return false;
            }
         } else {
            return false;
         }
      }

      float getIgnoredDistance(float ignoredDistanceValue, float difficultyValue, Vec3d vec) {
         return !this.tradeMode.on()
            ? ignoredDistanceValue
            : this.tradeMode
               .getAmount(
                  ignoredDistanceValue,
                  PosBlockUtilsBonus.getDifficultyInstance(difficultyValue, this.attributeArmor, 6.0F),
                  RefBareData.getAmount(this.ignored.y - vec.y, this.ignored.distanceTo(vec))
               );
      }

      boolean canAmountRef(float refValue, float cost, float full, float refValue4, boolean refFlag, boolean kills, boolean refFlag3) {
         return this.tradeMode.ref()
            ? this.tradeMode.canBare(refValue, this.zeroLivingAmount, this.tradeMode.canBareBehind(kills, refFlag3 && refFlag)) && this.tradeMode.canRef(full, refFlag, refValue, cost, false)
            : this.tradeMode.canRef(full, refValue4, refFlag, this.targetEffectiveHealth, refValue, cost, this.zeroLivingAmount) || this.canZeroMin(refValue, cost, full, refFlag, kills);
      }

      Ctx(UnsafeEntityUtils.Request zeroLivingData) {
         this.zeroLivingData = zeroLivingData;
         this.player = UnsafeEntityUtils.unsafeEntityMinecraftClient.player;
         this.ignored = this.player.getEyePos();
         this.livingEntity = zeroLivingData.target();
         this.minData = zeroLivingData.tuning() != null ? zeroLivingData.tuning() : BonusLevelData.getMinData();
         this.tradeMode = this.minData.tradeMode();
         this.targetEffectiveHealth = Float.isNaN(zeroLivingData.targetEhp()) ? this.livingEntity.getHealth() + this.livingEntity.getAbsorptionAmount() : zeroLivingData.targetEhp();
         this.zeroLivingAmount = this.player.getHealth() + this.player.getAbsorptionAmount();
         this.attributeArmorLivingZero = BlockEntityUtils.getAttributeArmor(this.livingEntity);
         this.attributeArmor = BlockEntityUtils.getAttributeArmor(this.player);
         this.livingZeroPredicted = zeroLivingData.predicted() != null
            ? zeroLivingData.predicted()
            : new BlockEntityUtils.Predicted(this.livingEntity.getEntityPos(), this.livingEntity.getBoundingBox());
         this.boxBoundingPredicted = zeroLivingData.self() != null ? zeroLivingData.self() : new BlockEntityUtils.Predicted(this.player.getEntityPos(), this.player.getBoundingBox());
         Vec3d spread = zeroLivingData.spread();
         boolean ctxFlag = spread != null && spread.length() > 0.001;
         this.boundingBoxPredicted = ctxFlag ? BlockEntityUtils.getOffsetFeet(this.livingZeroPredicted, spread) : null;
         this.ctxPredicted = ctxFlag ? BlockEntityUtils.getOffsetFeet(this.livingZeroPredicted, spread.multiply(-1.0)) : null;
         this.reattackTicks = Math.max(0, zeroLivingData.reattackTicks());
         this.lootValue = this.reattackTicks + 1;
      }

      boolean canFlag(float ctxValue, float cost, float full, float ctxValue4, boolean ctxFlag, boolean kills) {
         return this.canAmountRef(ctxValue, cost, full, ctxValue4, ctxFlag, kills, false);
      }

      UnsafeEntityUtils.BreakEval getLivingZero(EndCrystalEntity endCrystalEntity) {
         Vec3d entityPos = endCrystalEntity.getEntityPos();
         float creativeSpectator = BlockEntityUtils.getCreativeSpectator(this.player, entityPos, this.player.getEntityPos(), this.player.getBoundingBox(), null, null, 6.0F);
         float toughnessEpf = PosBlockUtilsBonus.getToughnessEpf(creativeSpectator, this.attributeArmor);
         float scaledInstance = PosBlockUtilsBonus.getScaledInstance(creativeSpectator, this.attributeArmor, this.zeroLivingData.selfWindow(), 0);
         float livingZeroValue = UnsafeEntityUtils.canBlockingExplosion(this.livingEntity, entityPos)
            ? 0.0F
            : BlockEntityUtils.getCreativeSpectator(this.livingEntity, entityPos, this.livingZeroPredicted.feet(), this.livingZeroPredicted.box(), null, null, 6.0F);
         float posBlockLimit = PosBlockUtilsBonus.getAmount(livingZeroValue, this.attributeArmorLivingZero, this.zeroLivingData.targetWindow(), 0, this.tradeMode.on());
         float knock = this.tradeMode.on() ? PosBlockUtilsBonus.getDifficultyInstance(livingZeroValue, this.attributeArmorLivingZero, 6.0F) : 0.0F;
         boolean livingZeroFlag = PosBlockUtilsBonus.canFlag(posBlockLimit, this.targetEffectiveHealth);
         boolean kills = livingZeroFlag && !this.zeroLivingData.targetTotem();
         boolean denyEnemyFlag = this.zeroLivingData.guards() != null
            && this.zeroLivingData.guards().canFlag(BlockEntityUtils.getDown(endCrystalEntity).asLong(), this.zeroLivingData.nowMs());
         double denyEnemy = PosBlockUtilsBonus.getDenyEnemy(denyEnemyFlag, toughnessEpf, scaledInstance, this.minData);
         boolean amountRef = this.canAmountRef(this.tradeMode.getBareRef(toughnessEpf, scaledInstance), scaledInstance, posBlockLimit, knock, livingZeroFlag, kills, true);
         if (!amountRef && !denyEnemyFlag) {
            amountRef = this.canAmount(toughnessEpf, scaledInstance, kills);
         }

         double livingZeroValue4 = PosBlockUtilsBonus.getBonusWeight(posBlockLimit, livingZeroFlag, kills, scaledInstance, denyEnemy, this.minData)
            + Math.max(0.0, UnsafeEntityUtils.getMinBounding(this, entityPos))
            + this.tradeMode.getAmountForFloat(knock);
         return new UnsafeEntityUtils.BreakEval(posBlockLimit, toughnessEpf, scaledInstance, livingZeroFlag, kills, denyEnemy, amountRef, livingZeroValue4, knock);
      }

      boolean canZeroLivingForEndCrystalEntity(EndCrystalEntity endCrystalEntity) {
         return this.zeroLivingData.guards() != null
            && this.zeroLivingData.guards().canFlag(BlockEntityUtils.getDown(endCrystalEntity).asLong(), this.zeroLivingData.nowMs());
      }

      String formatUnsafeWeak() {
         if (this.weakUnsafeFlag) {
            return "too weak";
         } else if (this.unsafeWeakFlag) {
            return "unsafe";
         } else if (this.unsafe) {
            return "out of reach";
         } else if (this.zeroLivingData.autoObsidian() && !this.zeroLivingData.hasObsidian()) {
            return "no obsidian";
         } else if (!BlockEntityUtils.isModifyBlocks()) {
            return "can't build";
         } else if (this.lootBuildFlag) {
            return "loot";
         } else {
            return this.loot ? "off level" : "no spot";
         }
      }

      float getBoxBounding(Vec3d vec, BlockPos pos, boolean boxBoundingFlag) {
         if (UnsafeEntityUtils.canBlockingExplosion(this.livingEntity, vec)) {
            return 0.0F;
         } else {
            float creativeSpectator = BlockEntityUtils.getCreativeSpectator(
               this.livingEntity, vec, this.livingZeroPredicted.feet(), this.livingZeroPredicted.box(), pos, null, 6.0F
            );
            if (boxBoundingFlag && this.boundingBoxPredicted != null) {
               float creativeSpectator2 = BlockEntityUtils.getCreativeSpectator(
                  this.livingEntity, vec, this.boundingBoxPredicted.feet(), this.boundingBoxPredicted.box(), pos, null, 6.0F
               );
               float creativeSpectator3 = BlockEntityUtils.getCreativeSpectator(
                  this.livingEntity, vec, this.ctxPredicted.feet(), this.ctxPredicted.box(), pos, null, 6.0F
               );
               return 0.5F * creativeSpectator + 0.25F * creativeSpectator2 + 0.25F * creativeSpectator3;
            } else {
               return creativeSpectator;
            }
         }
      }

      boolean canAmount(float ctxValue, float cost, boolean ctxFlag) {
         return this.tradeMode.canBareRef(ctxValue, cost) && this.tradeMode.canBare(this.tradeMode.getBareRef(ctxValue, cost), this.zeroLivingAmount, this.tradeMode.canFlag(ctxFlag));
      }

      UnsafeEntityUtils.Hit getZeroLiving(Vec3d vec, BlockPos pos, boolean zeroLivingFlag, int zeroLivingValue, boolean zeroLivingFlag2) {
         float boxBounding = this.getBoxBounding(vec, pos, zeroLivingFlag);
         float posBlockLimit = PosBlockUtilsBonus.getAmount(boxBounding, this.attributeArmorLivingZero, this.zeroLivingData.targetWindow(), zeroLivingValue, this.tradeMode.on());
         float knock = this.tradeMode.on() ? PosBlockUtilsBonus.getDifficultyInstance(boxBounding, this.attributeArmorLivingZero, 6.0F) : 0.0F;
         boolean canFlag3 = PosBlockUtilsBonus.canFlag(posBlockLimit, this.targetEffectiveHealth);
         boolean zeroLivingFlag4 = canFlag3 && !this.zeroLivingData.targetTotem();
         if (zeroLivingFlag2 && posBlockLimit < this.minData.minDamage() && !canFlag3 && !this.tradeMode.canFlag(posBlockLimit, knock, false)) {
            this.weakUnsafeFlag = true;
            return null;
         } else {
            float toughnessEpfLimit = this.getBoxBounding(vec, pos);
            float toughnessEpf = PosBlockUtilsBonus.getToughnessEpf(toughnessEpfLimit, this.attributeArmor);
            float scaledInstance = PosBlockUtilsBonus.getScaledInstance(toughnessEpfLimit, this.attributeArmor, this.zeroLivingData.selfWindow(), zeroLivingValue);
            return new UnsafeEntityUtils.Hit(posBlockLimit, toughnessEpf, scaledInstance, canFlag3, zeroLivingFlag4, this.getIgnoredDistance(knock, toughnessEpfLimit, vec));
         }
      }

      boolean canZeroMin(float zeroValue, float zeroLivingValue, float posBlockBound, boolean zeroFlag, boolean kills) {
         if (this.tradeMode.ref()) {
            return this.tradeMode.canBare(zeroValue, this.zeroLivingAmount, this.tradeMode.canFlag(kills));
         } else {
            return !this.minData.tradeRule()
               ? this.canZeroLiving(zeroValue, zeroLivingValue, kills)
               : PosBlockUtilsBonus.canFlag(
                     posBlockBound, zeroLivingValue, zeroValue, zeroFlag, kills, this.zeroLivingData.allowTotemTrade(), this.minData.popTrade(), this.zeroLivingAmount, this.minData.selfSpent()
                  )
                  && zeroLivingValue <= this.zeroLivingData.maxSelfDamage();
         }
      }
   }

   private record EliteSpot(BlockPos pos, float bound, boolean base) {
   }

   public record Forced(boolean ok, String why, float targetDamage, float selfFull, float selfCost, double value) {
   }

   private record Hit(float targetDamage, float selfFull, float selfCost, boolean pops, boolean kills, float knock) {
   }

   public record Plan(ImmutablePos action, String status) {
   }

   public record Request(
      LivingEntity target,
      BlockEntityUtils.Predicted predicted,
      double minEfficiency,
      boolean antiSuicide,
      float maxSelfDamage,
      boolean autoObsidian,
      boolean hasObsidian,
      HandleTime guards,
      long nowMs,
      long tick,
      int reattackTicks,
      float fromYaw,
      float fromPitch,
      float viewYaw,
      BonusLevelData tuning,
      BlockEntityUtils.Predicted self,
      Vec3d spread,
      PosBlockUtilsBonus.Window targetWindow,
      PosBlockUtilsBonus.Window selfWindow,
      float targetEhp,
      boolean targetTotem,
      boolean allowTotemTrade,
      Predicate<Vec3d> veto,
      BlockEntityUtils.Support support
   ) {
      public UnsafeEntityUtils.Request getSelfTarget(BlockEntityUtils.Support s) {
         return new UnsafeEntityUtils.Request(
            this.target,
            this.predicted,
            this.minEfficiency,
            this.antiSuicide,
            this.maxSelfDamage,
            this.autoObsidian,
            this.hasObsidian,
            this.guards,
            this.nowMs,
            this.tick,
            this.reattackTicks,
            this.fromYaw,
            this.fromPitch,
            this.viewYaw,
            this.tuning,
            this.self,
            this.spread,
            this.targetWindow,
            this.selfWindow,
            this.targetEhp,
            this.targetTotem,
            this.allowTotemTrade,
            this.veto,
            s
         );
      }

      public Request(
         LivingEntity viewYaw,
         BlockEntityUtils.Predicted antiSuicide,
         double maxSelfDamage,
         boolean nowMs,
         float target,
         boolean fromPitch,
         boolean predicted,
         HandleTime guards,
         long fromYaw,
         long tick,
         int minEfficiency,
         float reattackTicks,
         float hasObsidian,
         float autoObsidian
      ) {
         this(
            viewYaw,
            antiSuicide,
            maxSelfDamage,
            nowMs,
            target,
            fromPitch,
            predicted,
            guards,
            fromYaw,
            tick,
            minEfficiency,
            reattackTicks,
            hasObsidian,
            autoObsidian,
            BonusLevelData.INSTANCE,
            null,
            Vec3d.ZERO,
            PosBlockUtilsBonus.Window.INSTANCE,
            PosBlockUtilsBonus.Window.INSTANCE,
            Float.NaN,
            true,
            false,
            null
         );
      }

      public Request(
         LivingEntity fromPitch,
         BlockEntityUtils.Predicted guards,
         double predicted,
         boolean selfWindow,
         float targetEhp,
         boolean target,
         boolean tick,
         HandleTime antiSuicide,
         long viewYaw,
         long self,
         int veto,
         float autoObsidian,
         float minEfficiency,
         float reattackTicks,
         BonusLevelData nowMs,
         BlockEntityUtils.Predicted targetWindow,
         Vec3d hasObsidian,
         PosBlockUtilsBonus.Window fromYaw,
         PosBlockUtilsBonus.Window allowTotemTrade,
         float spread,
         boolean targetTotem,
         boolean tuning,
         Predicate<Vec3d> maxSelfDamage
      ) {
         this(
            fromPitch,
            guards,
            predicted,
            selfWindow,
            targetEhp,
            target,
            tick,
            antiSuicide,
            viewYaw,
            self,
            veto,
            autoObsidian,
            minEfficiency,
            reattackTicks,
            nowMs,
            targetWindow,
            hasObsidian,
            fromYaw,
            allowTotemTrade,
            spread,
            targetTotem,
            tuning,
            maxSelfDamage,
            null
         );
      }

      public UnsafeEntityUtils.Request getTuning() {
         return this.tuning != null && this.tuning.isDamageFloor() ? this.getSelfTarget(this.tuning.getBonusDamage()) : null;
      }

      public UnsafeEntityUtils.Request getSelfTarget(BonusLevelData t) {
         return new UnsafeEntityUtils.Request(
            this.target,
            this.predicted,
            this.minEfficiency,
            this.antiSuicide,
            this.maxSelfDamage,
            this.autoObsidian,
            this.hasObsidian,
            this.guards,
            this.nowMs,
            this.tick,
            this.reattackTicks,
            this.fromYaw,
            this.fromPitch,
            this.viewYaw,
            t,
            this.self,
            this.spread,
            this.targetWindow,
            this.selfWindow,
            this.targetEhp,
            this.targetTotem,
            this.allowTotemTrade,
            this.veto,
            this.support
         );
      }
   }

   private static final class Scan {
      EndCrystalEntity next;
      BlockPos blockPos;
      final List<UnsafeEntityUtils.Candidate> posUnsafeCandidateList = new ArrayList<>();
      double bestScore = Double.NEGATIVE_INFINITY;
      boolean posUnsafeActive;
      double posUnsafeAmount;
      double posAmount = Double.NEGATIVE_INFINITY;
      BlockPos posUnsafeBlockPos;
      BlockEntityUtils.Support oppositeDownData;

      private Scan() {
         this.posUnsafeAmount = Double.NEGATIVE_INFINITY;
      }

      boolean isPosBlock() {
         return this.next == null && this.blockPos == null && this.posUnsafeBlockPos == null;
      }
   }

   private record Spot(BlockPos pos, double bound) {
   }
}
