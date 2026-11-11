package com.corz.client.inventory;

import com.corz.client.entity.WorldCollisionHelper;
import com.corz.client.entity.VecBlockingData;
import com.corz.client.world.AbsorptionPosData;
import com.corz.client.world.BoxLengthUtils;
import com.corz.client.world.PosBlockUtilsBonus;
import com.corz.client.world.PosVecUtils;
import com.corz.client.world.VecPosUtilsAmount;
import com.corz.client.world.VecPosUtilsBreak;
import com.corz.client.world.VecPosUtilsDamage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.RayBudget;
import com.corz.client.utils.PendingEntityHitTracker;
import com.corz.client.utils.EntityHeightSamples;
import com.corz.client.utils.AmountAmounts;
import com.corz.client.utils.ConstVec;
import com.corz.client.utils.MinVec;
import com.corz.client.utils.PearlsPlaystyleData;
import com.corz.client.utils.SelfHazData;
import com.corz.client.utils.VecShieldUtilsAmounts;
import com.corz.client.utils.WeakFullUtils;

public final class VecPos {
   public static final double[] vecPosTable = new double[]{1.0, 1.4, 1.8, 2.2};
   private final RayBudget offerRateLimiter;
   private static final AmountAmounts EMPTY_AMOUNTS = new AmountAmounts();

   private VecPos.Eval getIntersectsExisting(
      long intersectsExistingValue,
      WorldCollisionHelper blockPosHelperSolid,
      LivingEntityHelperVisible livingEntityHelperVisible,
      AmountAmounts amountAmounts,
      AbsorptionPosData absorptionPosData,
      VecBlockingData vecBlockingData,
      VecPos.Base base,
      VecPos.Hit hit,
      PosVecUtils.Path path,
      int intersectsExistingLimit,
      PosVecUtils.Kick kick,
      Vec3d vec,
      Vec3d vec2,
      float degrees,
      Vec3d vec3,
      Vec3d vec4,
      Vec3d vec5,
      Vec3d vec6,
      Box box,
      Box box2,
      Vec3d vec7,
      ClientPlayerEntity clientPlayerEntity,
      VecPos.Loadout loadout,
      int intersectsExistingValue3,
      boolean intersectsExistingFlag,
      int intersectsExistingValue4,
      float intersectsExistingValue5,
      double slotSwordFactor,
      int crystalPitchValue,
      int ping,
      int intersectsExistingValue9,
      double y,
      VecPosUtilsDamage.Weights weights,
      double intersectsExistingValue11,
      PosBlockUtilsBonus.Window window,
      float offerExpoLimit,
      LivingEntityHelperVisible.Seed seed
   ) {
      BlockPos pos = base.pos();
      Box box3 = BlockEntityUtils.getBox3(pos);
      if (!base.armed()) {
         if (box.intersects(box3) || box2 != null && box2.intersects(box3)) {
            return null;
         }

         if (!base.existing()) {
            Box boxForBlockPos = BlockEntityUtils.getBoxForBlockPos(pos);
            if (box.intersects(boxForBlockPos) || box2 != null && box2.intersects(boxForBlockPos)) {
               return null;
            }

            if (vec3.distanceTo(new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5)) > 4.25) {
               return null;
            }
         }

         if (vec3.distanceTo(WorldCollisionHelper.getTopCenter(pos)) > 4.25) {
            return null;
         }
      }

      Vec3d vec3d = BlockEntityUtils.getVec3d(pos);
      Box box5 = BlockEntityUtils.getBox(pos);
      VecPos.Eval intersectsExisting = null;

      for (int i = 2; i <= 9 && i < intersectsExistingLimit; i++) {
         PosVecUtils.Sample sample = path.getDataForInt(i);
         if (canTopRaised(sample.y(), base) && canFlag(getMultiply(vec3, vec, i), box5)) {
            int countSamplesAbove = EntityHeightSamples.countSamplesAbove(sample.y(), base.top());
            double existingArmorFactor = (double)countSamplesAbove / EntityHeightSamples.HEIGHT_SAMPLE_OFFSETS.length;
            int intersectsExistingValue15 = -1;
            if (!base.armed()) {
               ArrayList arrayList = new ArrayList(2);
               Vec3d eyePos = clientPlayerEntity.getEyePos();
               if (!base.existing()) {
                  arrayList.add(
                     new VecPosUtilsBreak.Step(
                        VecPosUtilsBreak.Op.PLACE_OBSIDIAN,
                        2,
                        WorldCollisionHelper.computeAngleFromRotation(
                           clientPlayerEntity.getYaw(), clientPlayerEntity.getPitch(), eyePos, new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5)
                        ),
                        false
                     )
                  );
               }

               arrayList.add(
                  new VecPosUtilsBreak.Step(
                     VecPosUtilsBreak.Op.PLACE_CRYSTAL,
                     1,
                     base.existing()
                        ? WorldCollisionHelper.computeAngleFromRotation(clientPlayerEntity.getYaw(), clientPlayerEntity.getPitch(), eyePos, WorldCollisionHelper.getTopCenter(pos))
                        : 10.0,
                     false
                  )
               );
               int[] intersectsExistingValues = VecPosUtilsBreak.getValues(new VecPosUtilsBreak.Ctx(loadout.selectedItem(), 100, 100, 2, ping, intersectsExistingValue9, true), arrayList);
               intersectsExistingValue15 = intersectsExistingValues[intersectsExistingValues.length - 1];
            }

            int sigmaBound = intersectsExistingValue15 + 1 + intersectsExistingValue4 + (intersectsExistingFlag ? 3 : 0);
            VecPos.Eval existingArmor = this.getExistingArmor(
               VecPos.Arm.KB_HIT_CRYSTAL,
               hit,
               base,
               sample,
               vec3d,
               existingArmorFactor,
               i,
               crystalPitchValue,
               path,
               intersectsExistingLimit,
               kick,
               livingEntityHelperVisible,
               blockPosHelperSolid,
               amountAmounts,
               absorptionPosData,
               vecBlockingData,
               vec2,
               vec4,
               intersectsExistingValue5,
               y,
               weights,
               intersectsExistingValue11,
               window,
               offerExpoLimit,
               seed,
               sigmaBound,
               base.existing() ? 0 : 1,
               getSigma(box, box3, sample, sigmaBound),
               degrees,
               vec6,
               slotSwordFactor,
               intersectsExistingValue3,
               intersectsExistingFlag,
               null
            );
            if (existingArmor != null && (intersectsExisting == null || existingArmor.offer().value() > intersectsExisting.offer().value())) {
               intersectsExisting = existingArmor;
            }
         }
      }

      return intersectsExisting;
   }

   static ItemStack getInventoryStack(ClientPlayerEntity clientPlayerEntity, int inventoryStackLimit) {
      return clientPlayerEntity != null && inventoryStackLimit >= 0 && inventoryStackLimit <= 8 ? clientPlayerEntity.getInventory().getStack(inventoryStackLimit) : ItemStack.EMPTY;
   }

   static double getAmount(double vecPosLimit, double goalBound) {
      return vecPosLimit > 0.0 && goalBound > 0.0 ? Math.min(vecPosLimit, vecPosLimit * Math.min(1.0, goalBound)) : 0.0;
   }

   private static boolean canTopRaised(double topRaisedLimit, VecPos.Base base) {
      int countSamplesAbove = EntityHeightSamples.countSamplesAbove(topRaisedLimit, base.top());
      return base.raised() ? countSamplesAbove >= 4 : topRaisedLimit >= base.top() + 0.2 && countSamplesAbove >= 3;
   }

   private VecPos.Eval getPunchStrike(
      WorldCollisionHelper blockPosHelperSolid,
      LivingEntityHelperVisible livingEntityHelperVisible,
      AmountAmounts amountAmounts,
      AbsorptionPosData absorptionPosData,
      VecBlockingData vecBlockingData,
      VecPos.Base base,
      PosVecUtils.Path path,
      int punchStrikeValue,
      PosVecUtils.Kick kick,
      Vec3d vec,
      Vec3d vec2,
      float degrees,
      Vec3d vec3,
      Vec3d vec4,
      Vec3d vec5,
      Vec3d vec6,
      VecPos.Loadout loadout,
      int punchStrikeValue3,
      int punchStrikeValue4,
      float punchStrikeValue5,
      double slotSwordFactor,
      int crystalPitchValue,
      int ping,
      int punchStrikeValue9,
      double y,
      VecPosUtilsDamage.Weights weights,
      double punchStrikeValue11,
      PosBlockUtilsBonus.Window window,
      float offerExpoLimit,
      LivingEntityHelperVisible.Seed seed
   ) {
      BlockPos pos = base.pos();
      Vec3d vec3d = WorldCollisionHelper.getTopCenter(pos);
      Vec3d vec3d2 = BlockEntityUtils.getVec3d(pos);
      ArrayList arrayList = new ArrayList(3);
      arrayList.add(new VecPosUtilsBreak.Step(VecPosUtilsBreak.Op.STRIKE, 1, 0.0, false));
      arrayList.add(new VecPosUtilsBreak.Step(VecPosUtilsBreak.Op.PLACE_CRYSTAL, 1, WorldCollisionHelper.computeAngleDifference(vec3, vec6, vec3d), false));
      arrayList.add(new VecPosUtilsBreak.Step(VecPosUtilsBreak.Op.BREAK, 3, WorldCollisionHelper.computeAngleDifference(vec3, vec3d, vec3d2.add(0.0, 1.0, 0.0)), false));
      int[] punchStrikeValues = VecPosUtilsBreak.getValues(new VecPosUtilsBreak.Ctx(1, 100, 100, 2, ping, punchStrikeValue9, true), arrayList);
      int punchStrikeLimit = punchStrikeValues[2];
      if (punchStrikeLimit > punchStrikeValue - 1) {
         return null;
      } else {
         PosVecUtils.Sample sample = path.getDataForInt(punchStrikeLimit);
         if (!canTopRaised(sample.y(), base)) {
            return null;
         } else {
            int pathBound = punchStrikeValues[1];
            PosVecUtils.Sample dataForInt = path.getDataForInt(pathBound);
            double minMax = WorldCollisionHelper.computeBoxGap(WorldCollisionHelper.getBoxFromSample(dataForInt, 0.0), BlockEntityUtils.getBox3(pos));
            if (minMax <= 0.0) {
               return null;
            } else if (getMultiply(vec3, vec, pathBound).distanceTo(vec3d) > 4.25) {
               return null;
            } else if (!canFlag(getMultiply(vec3, vec, punchStrikeLimit), BlockEntityUtils.getBox(pos))) {
               return null;
            } else {
               double existingArmorFactor = (double)EntityHeightSamples.countSamplesAbove(sample.y(), base.top()) / EntityHeightSamples.HEIGHT_SAMPLE_OFFSETS.length;
               int reattackOffset = punchStrikeValue4 + 3;
               double normalCdf = WorldCollisionHelper.normalCdf(-minMax / Math.max(0.05, dataForInt.sigma()));
               return this.getExistingArmor(
                  VecPos.Arm.PUNCH_POP,
                  VecPos.Hit.PUNCH_SPRINT,
                  base,
                  sample,
                  vec3d2,
                  existingArmorFactor,
                  punchStrikeLimit,
                  crystalPitchValue,
                  path,
                  punchStrikeValue,
                  kick,
                  livingEntityHelperVisible,
                  blockPosHelperSolid,
                  amountAmounts,
                  absorptionPosData,
                  vecBlockingData,
                  vec2,
                  vec4,
                  punchStrikeValue5,
                  y,
                  weights,
                  punchStrikeValue11,
                  window,
                  offerExpoLimit,
                  seed,
                  reattackOffset,
                  0,
                  normalCdf,
                  degrees,
                  vec5,
                  slotSwordFactor,
                  punchStrikeValue3,
                  true,
                  null
               );
            }
         }
      }
   }

   private static double getKickIdx(
      VecPos.Parts parts, double kickIdxFactor, float kickIdxValue, float offerExpoLimit, VecBlockingData vecBlockingData, AmountAmounts amountAmounts, VecPosUtilsDamage.Weights weights, double pilotRenderLimit
   ) {
      double meleeScale = amountAmounts.getMeleeScale();
      double x = parts.unitKb()[0] * kickIdxFactor;
      double kickIdxFactor3 = parts.unitKb()[1] * kickIdxFactor;
      double z = parts.unitKb()[2] * kickIdxFactor;
      double posVecValue = parts.kickVy() + kickIdxFactor3 * meleeScale;
      int stepCount = WorldCollisionHelper.computeStepCount(parts.kickY(), posVecValue, pilotRenderLimit, 60);
      int kickIdxValue6 = parts.kickIdx() + stepCount - Math.max(parts.kickIdx(), parts.baseLand());
      double adjustedValue = WorldCollisionHelper.computeAdjustedValue(parts.kickY(), posVecValue, pilotRenderLimit);
      float readyValue = BlockEntityUtils.getAmount(vecBlockingData.entity(), parts.feet().distanceTo(parts.centre()), 6.0F) * (float)kickIdxFactor;
      float posBlockLimit = parts.window() != null
         ? PosBlockUtilsBonus.getReadyInstance(readyValue, vecBlockingData.armor(), parts.window(), parts.blastIdx())
         : PosBlockUtilsBonus.getToughnessEpf(Math.max(0.0F, readyValue - parts.meleeRaw()), vecBlockingData.armor());
      boolean kickIdxFlag = PosBlockUtilsBonus.canFlag(posBlockLimit, vecBlockingData.getHealthTrusted());
      boolean kickIdxFlag2 = kickIdxFlag && (vecBlockingData.awaitingTotem() || !vecBlockingData.totemInHand());
      Vec3d vec3d = LivingEntityHelperVisible.getVec3d(parts.ourFeet().subtract(parts.feet()));
      Vec3d vec3d2 = LivingEntityHelperVisible.getVec3d(new Vec3d(x, 0.0, z));
      double kickIdxValue10 = Math.hypot(x, z) > 0.1 ? vec3d2.x * vec3d.x + vec3d2.z * vec3d.z : 0.0;
      return VecPosUtilsDamage.getSelfDamage(
         new VecPosUtilsDamage.Input(posBlockLimit, kickIdxFlag, kickIdxFlag2, kickIdxValue6, adjustedValue, kickIdxValue10, kickIdxValue, offerExpoLimit, parts.prep(), parts.obsidianPlaces(), parts.failRisk(), parts.reusable()),
         weights
      );
   }

   private static boolean canFlooredDown(WorldCollisionHelper blockPosHelperSolid, Vec3d vec) {
      BlockPos floored = BlockPos.ofFloored(vec.x, vec.y + 0.01, vec.z);
      return !blockPosHelperSolid.isSolidAt(floored) && !blockPosHelperSolid.isSolidAt(floored.up()) && blockPosHelperSolid.isSolidAt(floored.down());
   }

   private static boolean canFlag(Vec3d vec, Box box) {
      return BlockEntityUtils.getMinMax(vec, box) < 2.9;
   }

   static int getEmptyEnd(ItemStack stack) {
      if (stack == null || stack.isEmpty()) {
         return 3;
      } else if (InventoryStackUtilsEmpty.canSwordsEmpty(stack)) {
         return 0;
      } else if (stack.isOf(Items.END_CRYSTAL)) {
         return 1;
      } else {
         return stack.isOf(Items.OBSIDIAN) ? 2 : 3;
      }
   }

   private List<VecPos.Eval> getCrystalPitch(
      WorldCollisionHelper blockPosHelperSolid,
      LivingEntityHelperVisible livingEntityHelperVisible,
      AmountAmounts amountAmounts,
      AbsorptionPosData absorptionPosData,
      VecBlockingData vecBlockingData,
      ClientPlayerEntity clientPlayerEntity,
      VecPos.Loadout loadout,
      Vec3d vec,
      int crystalPitchLimit,
      Vec3d vec2,
      int crystalPitchValue,
      int ping,
      int crystalPitchValue3,
      double y,
      VecPosUtilsDamage.Weights weights,
      double crystalPitchValue5,
      PosBlockUtilsBonus.Window window,
      PosBlockUtilsBonus.Window window2,
      float offerExpoLimit,
      LivingEntityHelperVisible.Seed seed,
      Predicate<Vec3d> predicate,
      Box box
   ) {
      ArrayList arrayList = new ArrayList();
      PosVecUtils.Path sampleTick = livingEntityHelperVisible.getSampleTick(30, blockPosHelperSolid, amountAmounts, vec2, null);
      if (sampleTick == null) {
         return arrayList;
      } else {
         Vec3d vec3d = absorptionPosData.eye() != null ? absorptionPosData.eye() : clientPlayerEntity.getEyePos();
         Vec3d pos = absorptionPosData.pos();
         int ceil7 = (int)Math.ceil(1.5);
         int floor8 = (int)Math.floor(vec.x);
         int floor9 = (int)Math.floor(vec.z);

         for (int i = -ceil7 - 1; i <= ceil7 + 1; i++) {
            for (int j = -ceil7 - 1; j <= ceil7 + 1; j++) {
               for (int k = crystalPitchLimit - 1; k <= crystalPitchLimit; k++) {
                  BlockPos blockPos = new BlockPos(floor8 + i, k, floor9 + j);
                  double livingEntityValue = blockPos.getY() + 1.0;
                  if (!(livingEntityValue < vec.y + -0.2)
                     && !(livingEntityValue > vec.y + 0.05)
                     && !(WorldCollisionHelper.getAmount(blockPos.getX() + 0.5, blockPos.getZ() + 0.5, vec.x, vec.z) > 1.5)
                     && blockPosHelperSolid.canPlaceOnBedrockObsidian(blockPos)) {
                     boolean hasWorld = blockPosHelperSolid.findCrystalAt(blockPos) != null;
                     if ((hasWorld || blockPosHelperSolid.isAirAt(blockPos.up())) && (hasWorld || !blockPosHelperSolid.hasBlockingEntities(blockPos, vecBlockingData.entity(), clientPlayerEntity))) {
                        Vec3d vec3d2 = BlockEntityUtils.getVec3d(blockPos);
                        if (!predicate.test(vec3d2)) {
                           Box box3 = BlockEntityUtils.getBox3(blockPos);
                           if ((hasWorld || box == null || !box.intersects(box3))
                              && (hasWorld || !(vec3d.distanceTo(WorldCollisionHelper.getTopCenter(blockPos)) > 4.25))
                              && BlockEntityUtils.getMinMax(vec3d, BlockEntityUtils.getBox(blockPos)) < 3.0) {
                              ArrayList arrayList2 = new ArrayList(2);
                              if (!hasWorld) {
                                 arrayList2.add(
                                    new VecPosUtilsBreak.Step(
                                       VecPosUtilsBreak.Op.PLACE_CRYSTAL,
                                       1,
                                       WorldCollisionHelper.computeAngleFromRotation(clientPlayerEntity.getYaw(), clientPlayerEntity.getPitch(), vec3d, WorldCollisionHelper.getTopCenter(blockPos)),
                                       false
                                    )
                                 );
                              }

                              arrayList2.add(
                                 new VecPosUtilsBreak.Step(
                                    VecPosUtilsBreak.Op.BREAK,
                                    3,
                                    hasWorld ? WorldCollisionHelper.computeAngleFromRotation(clientPlayerEntity.getYaw(), clientPlayerEntity.getPitch(), vec3d, vec3d2.add(0.0, 1.0, 0.0)) : 4.0,
                                    false
                                 )
                              );
                              int[] crystalPitchValues = VecPosUtilsBreak.getValues(
                                 new VecPosUtilsBreak.Ctx(loadout.selectedItem(), 100, 100, 2, ping, crystalPitchValue3, true), arrayList2
                              );
                              int pathBound = crystalPitchValues[crystalPitchValues.length - 1];
                              double positiveInfinity = Double.POSITIVE_INFINITY;
                              if (!hasWorld) {
                                 PosVecUtils.Sample sample = sampleTick.getDataForInt(crystalPitchValues[0]);
                                 double minMax = WorldCollisionHelper.computeBoxGap(WorldCollisionHelper.getBoxFromSample(sample, 0.0), box3);
                                 if (minMax <= 0.0) {
                                    continue;
                                 }

                                 positiveInfinity = minMax;
                              }

                              PosVecUtils.Sample dataForInt = sampleTick.getDataForInt(pathBound);
                              int countSamplesAbove = EntityHeightSamples.countSamplesAbove(dataForInt.y(), livingEntityValue);
                              if (countSamplesAbove >= 3) {
                                 double existingArmorFactor = (double)countSamplesAbove / EntityHeightSamples.HEIGHT_SAMPLE_OFFSETS.length;
                                 VecPos.Base base = new VecPos.Base(blockPos, (int)livingEntityValue, true, false, hasWorld);
                                 double normalCdf = Double.isInfinite(positiveInfinity) ? 0.0 : WorldCollisionHelper.normalCdf(-positiveInfinity / Math.max(0.05, dataForInt.sigma()));
                                 VecPos.Eval existingArmor = this.getExistingArmor(
                                    VecPos.Arm.CRYSTAL_LAUNCH,
                                    VecPos.Hit.NONE,
                                    base,
                                    dataForInt,
                                    vec3d2,
                                    existingArmorFactor,
                                    pathBound,
                                    crystalPitchValue,
                                    sampleTick,
                                    sampleTick.getValueGround(),
                                    null,
                                    livingEntityHelperVisible,
                                    blockPosHelperSolid,
                                    amountAmounts,
                                    absorptionPosData,
                                    vecBlockingData,
                                    vec2,
                                    pos,
                                    0.0F,
                                    y,
                                    weights,
                                    crystalPitchValue5,
                                    window,
                                    offerExpoLimit,
                                    seed,
                                    pathBound,
                                    0,
                                    normalCdf,
                                    VecShieldUtilsAmounts.getAmount(vec.x - pos.x, vec.z - pos.z),
                                    pos,
                                    WorldCollisionHelper.horizontalDistance(pos, vec),
                                    loadout.crystalSlot(),
                                    false,
                                    window2
                                 );
                                 if (existingArmor != null) {
                                    arrayList.add(existingArmor);
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         return arrayList;
      }
   }

   private static Vec3d getMultiply(Vec3d vec, Vec3d vec2, int multiplyValue) {
      return vec.add(vec2.multiply(getAmount(multiplyValue)));
   }

   private VecPos.Eval getRaisedDistance(
      WorldCollisionHelper blockPosHelperSolid,
      LivingEntityHelperVisible livingEntityHelperVisible,
      AmountAmounts amountAmounts,
      AbsorptionPosData absorptionPosData,
      VecBlockingData vecBlockingData,
      VecPos.Base base,
      VecPos.Hit hit,
      PosVecUtils.Path path,
      int raisedDistanceValue,
      PosVecUtils.Kick kick,
      Vec3d vec,
      Vec3d vec2,
      float degrees,
      Vec3d vec3,
      Vec3d vec4,
      Vec3d vec5,
      Vec3d vec6,
      VecPos.Loadout loadout,
      int raisedDistanceValue3,
      boolean raisedDistanceFlag,
      int raisedDistanceValue4,
      float raisedDistanceValue5,
      double slotSwordFactor,
      int crystalPitchValue,
      int ping,
      int raisedDistanceValue9,
      double y,
      VecPosUtilsDamage.Weights weights,
      double raisedDistanceValue11,
      PosBlockUtilsBonus.Window window,
      float offerExpoLimit,
      LivingEntityHelperVisible.Seed seed
   ) {
      BlockPos pos = base.pos();
      boolean existing2 = !base.existing();
      if (existing2 && loadout.obsidianSlot() < 0) {
         return null;
      } else {
         Vec3d vec3d = new Vec3d(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
         Vec3d vec3d2 = WorldCollisionHelper.getTopCenter(pos);
         Vec3d vec3d3 = BlockEntityUtils.getVec3d(pos);
         ArrayList arrayList = new ArrayList(4);
         arrayList.add(new VecPosUtilsBreak.Step(VecPosUtilsBreak.Op.STRIKE, 0, 0.0, false));
         if (existing2) {
            arrayList.add(new VecPosUtilsBreak.Step(VecPosUtilsBreak.Op.PLACE_OBSIDIAN, 2, WorldCollisionHelper.computeAngleDifference(vec3, vec6, vec3d), false));
         }

         arrayList.add(
            new VecPosUtilsBreak.Step(
               VecPosUtilsBreak.Op.PLACE_CRYSTAL,
               1,
               existing2 ? WorldCollisionHelper.computeAngleDifference(vec3, vec3d, vec3d2) : WorldCollisionHelper.computeAngleDifference(vec3, vec6, vec3d2),
               false
            )
         );
         arrayList.add(new VecPosUtilsBreak.Step(VecPosUtilsBreak.Op.BREAK, 3, WorldCollisionHelper.computeAngleDifference(vec3, vec3d2, vec3d3.add(0.0, 1.0, 0.0)), false));
         int[] raisedDistanceValues = VecPosUtilsBreak.getValues(new VecPosUtilsBreak.Ctx(0, 100, 100, 2, ping, raisedDistanceValue9, true), arrayList);
         int raisedDistanceLimit = raisedDistanceValues[raisedDistanceValues.length - 1];
         if (raisedDistanceLimit > raisedDistanceValue - 1) {
            return null;
         } else {
            PosVecUtils.Sample sample = path.getDataForInt(raisedDistanceLimit);
            double raisedDistanceValue13 = base.raised() ? -0.1 : 0.9;
            if (sample.y() < base.top() + raisedDistanceValue13) {
               return null;
            } else {
               double raisedDistanceBound = Double.POSITIVE_INFINITY;
               int index = 1;
               if (existing2) {
                  int raisedDistanceValue14 = raisedDistanceValues[index++];
                  PosVecUtils.Sample dataForInt = path.getDataForInt(raisedDistanceValue14);
                  Box sigma = WorldCollisionHelper.getBoxFromSample(dataForInt, 0.0);
                  Box box = BlockEntityUtils.getBoxForBlockPos(pos);
                  double minMax = WorldCollisionHelper.computeBoxGap(sigma, box);
                  if (minMax <= 0.0) {
                     return null;
                  }

                  raisedDistanceBound = Math.min(raisedDistanceBound, minMax);
                  if (getMultiply(vec3, vec, raisedDistanceValue14).distanceTo(vec3d) > 4.25) {
                     return null;
                  }
               }

               int pathBound = raisedDistanceValues[index];
               PosVecUtils.Sample dataForInt3 = path.getDataForInt(pathBound);
               double boxGap = WorldCollisionHelper.computeBoxGap(WorldCollisionHelper.getBoxFromSample(dataForInt3, 0.0), BlockEntityUtils.getBox3(pos));
               if (boxGap <= 0.0) {
                  return null;
               } else {
                  double min16 = Math.min(raisedDistanceBound, boxGap);
                  if (getMultiply(vec3, vec, pathBound).distanceTo(vec3d2) > 4.25) {
                     return null;
                  } else if (!canFlag(getMultiply(vec3, vec, raisedDistanceLimit), BlockEntityUtils.getBox(pos))) {
                     return null;
                  } else {
                     int countSamplesAbove = EntityHeightSamples.countSamplesAbove(sample.y(), base.top());
                     if (countSamplesAbove < (base.raised() ? 4 : 3)) {
                        return null;
                     } else {
                        double existingArmorFactor = (double)countSamplesAbove / EntityHeightSamples.HEIGHT_SAMPLE_OFFSETS.length;
                        int sigmaBound = raisedDistanceValue4 + (raisedDistanceFlag ? 3 : 0);
                        double normalCdf = WorldCollisionHelper.normalCdf(-min16 / Math.max(0.05, dataForInt3.sigma()));
                        return this.getExistingArmor(
                           VecPos.Arm.KB_POP_BUILD,
                           hit,
                           base,
                           sample,
                           vec3d3,
                           existingArmorFactor,
                           raisedDistanceLimit,
                           crystalPitchValue,
                           path,
                           raisedDistanceValue,
                           kick,
                           livingEntityHelperVisible,
                           blockPosHelperSolid,
                           amountAmounts,
                           absorptionPosData,
                           vecBlockingData,
                           vec2,
                           vec4,
                           raisedDistanceValue5,
                           y,
                           weights,
                           raisedDistanceValue11,
                           window,
                           offerExpoLimit,
                           seed,
                           sigmaBound,
                           existing2 ? 1 : 0,
                           normalCdf,
                           degrees,
                           vec5,
                           slotSwordFactor,
                           raisedDistanceValue3,
                           raisedDistanceFlag,
                           null
                        );
                     }
                  }
               }
            }
         }
      }
   }

   private List<VecPos.Eval> getSlotSword(
      long slotSwordValue,
      WorldCollisionHelper blockPosHelperSolid,
      LivingEntityHelperVisible livingEntityHelperVisible,
      AbsorptionPosData absorptionPosData,
      VecBlockingData vecBlockingData,
      MinVec minVec,
      PearlsPlaystyleData pearlsPlaystyleData,
      SelfHazData selfHazData,
      double slotSwordFactor,
      int slotSwordBound,
      float offerExpoLimit,
      Predicate<Vec3d> predicate
   ) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      if (player != null && blockPosHelperSolid != null && blockPosHelperSolid.getWorld() != null && livingEntityHelperVisible != null && absorptionPosData != null && vecBlockingData != null && pearlsPlaystyleData != null) {
         LivingEntityHelperVisible.Seed seed = livingEntityHelperVisible.getDataGroundMax();
         if (seed == null) {
            return List.of();
         } else {
            int index = pearlsPlaystyleData.playstyle() == null ? 0 : pearlsPlaystyleData.playstyle().ordinal();
            if (vecBlockingData.distance() > ConstVec.frozenAmounts[index] + 1.5) {
               return List.of();
            } else if (seed.lift() == WeakFullUtils.Lift.NONE) {
               return List.of();
            } else {
               VecPos.Loadout stackKnockback = getStackKnockback(player, pearlsPlaystyleData.buildObsidian());
               if (stackKnockback.crystalSlot() < 0) {
                  return List.of();
               } else {
                  AmountAmounts amountAmounts = minVec != null ? minVec.getArmsAmountAmounts() : EMPTY_AMOUNTS;
                  Predicate predicate2 = predicate == null ? obj -> false : predicate;
                  PosVecUtils.Sample sample = seed.sample();
                  Vec3d vec3d = new Vec3d(sample.x(), sample.y(), sample.z());
                  Vec3d vec3d2 = LivingEntityHelperVisible.getVec3d(vec3d.subtract(absorptionPosData.pos()));
                  if (vec3d2.lengthSquared() < 0.5) {
                     return List.of();
                  } else {
                     Vec3d vec3d3 = new Vec3d(-vec3d2.x, 0.0, -vec3d2.z);
                     float degrees = VecShieldUtilsAmounts.getAmount(vec3d2.x, vec3d2.z);
                     int crystalPitchValue = seed.time().getMax();
                     int ping = seed.time().getPing();
                     int max5 = BoxLengthUtils.getMax((int)Math.round(seed.time().pingMs()));
                     double y = sample.y();
                     int vecPosLimit = (int)Math.floor(sample.y() + 1.0E-6);
                     VecPosUtilsDamage.Weights dataForDouble = VecPosUtilsDamage.Weights.getDataForDouble(
                        selfHazData == null ? 1.0 : selfHazData.off(), selfHazData == null ? 1.0 : selfHazData.self(), ConstVec.idxFeetTable[index]
                     );
                     double slotSwordValue7 = 15.0 * WeakFullUtils.getAmount(seed.lift());
                     PosBlockUtilsBonus.Window window = new PosBlockUtilsBonus.Window(
                        PendingEntityHitTracker.getRemainingTicksFloor(absorptionPosData.ticksSinceFresh(), absorptionPosData.loopMs()), absorptionPosData.lastScaledTaken()
                     );
                     PosBlockUtilsBonus.Window window2 = new PosBlockUtilsBonus.Window(Math.max(0, slotSwordBound), vecBlockingData.lastScaledTaken());
                     Vec3d vec3d4 = new Vec3d(vec3d.x - vec3d2.x * slotSwordFactor, vec3d.y, vec3d.z - vec3d2.z * slotSwordFactor);
                     double[] slotSwordValues = VecShieldUtilsAmounts.getAmounts(vec3d.x, vec3d.z, vec3d2.x, vec3d2.z, slotSwordFactor + 0.6);
                     Vec3d vec3d5 = new Vec3d(slotSwordValues[0], vec3d.y, slotSwordValues[1]);
                     Vec3d add = vec3d5.add(0.0, 1.62, 0.0);
                     boolean slotSwordFlag = blockPosHelperSolid.getWorld() != null
                        && canFlooredDown(blockPosHelperSolid, vec3d4)
                        && canFlooredDown(blockPosHelperSolid, vec3d5)
                        && blockPosHelperSolid.canWalkBetween(absorptionPosData.pos(), vec3d5, 1);
                     Box sigma = WorldCollisionHelper.getBoxFromSample(sample, 0.1);
                     Box box = absorptionPosData.box();
                     Vec3d vec7 = vec3d.add(0.0, 1.2, 0.0);
                     ArrayList arrayList = new ArrayList();
                     boolean slotSwordFlag2 = seed.grounded() && seed.groundedUpdates() >= 1;
                     List list = slotSwordFlag && slotSwordFlag2 ? getAmounts(blockPosHelperSolid, vec3d, vec3d2, vecPosLimit, stackKnockback.obsidianSlot() >= 0, predicate2, vecBlockingData.entity(), player) : List.of();
                     ArrayList arrayList2 = new ArrayList(3);
                     if (stackKnockback.swordSlot() >= 0) {
                        arrayList2.add(VecPos.Hit.KB_SPRINT);
                        if (stackKnockback.knockback() >= 1) {
                           arrayList2.add(VecPos.Hit.KB_WALK);
                        }
                     }

                     for (VecPos.Hit hit : (Iterable<com.corz.client.inventory.VecPos.Hit>) (arrayList2)) {
                        if (list.isEmpty()) {
                           break;
                        }

                        boolean slotSwordFlag3 = hit == VecPos.Hit.PUNCH_SPRINT;
                        boolean vecPosFlag = hit != VecPos.Hit.KB_WALK;
                        double slotSwordFactor2 = getAmount(slotSwordFactor, vecPosFlag);
                        Vec3d vec3d6 = new Vec3d(vec3d.x - vec3d2.x * slotSwordFactor2, vec3d.y, vec3d.z - vec3d2.z * slotSwordFactor2);
                        Vec3d vec3 = vec3d6.add(0.0, 1.62, 0.0);
                        double freshBlockedLimit = slotSwordFlag3 ? 0.5 : VecPosUtilsAmount.getAmount(0.0, stackKnockback.knockback(), vecPosFlag);
                        double[] freshBlocked = VecPosUtilsAmount.getFreshBlocked(
                           new double[]{0.0, -0.0784, 0.0}, true, seed.rMelee(), -vec3d2.x, -vec3d2.z, degrees, freshBlockedLimit, VecPosUtilsAmount.HitKind.FRESH
                        );
                        double explosionScale = amountAmounts.getExplosionScale();
                        PosVecUtils.Kick kick = new PosVecUtils.Kick(crystalPitchValue, freshBlocked[0] * explosionScale, freshBlocked[1], freshBlocked[2] * explosionScale, true);
                        PosVecUtils.Path sampleTick = livingEntityHelperVisible.getSampleTick(30, blockPosHelperSolid, amountAmounts, vec3d3, List.of(kick));
                        if (sampleTick != null) {
                           int valueGround = sampleTick.getValueGround();
                           float slotSwordValue10 = slotSwordFlag3 ? 1.0F : getAxeSword(stackKnockback.sword());
                           int slotSwordValue11 = slotSwordFlag3 ? stackKnockback.crystalSlot() : stackKnockback.swordSlot();
                           double slotSwordValue12 = slotSwordFlag3 ? 4.0 : 1.6;
                           int slotSwordValue13 = VecPosUtilsAmount.getValue(slotSwordValue12) + 1;

                           for (VecPos.Base base : (Iterable<com.corz.client.inventory.VecPos.Base>) (list)) {
                              VecPos.Eval intersectsExisting = this.getIntersectsExisting(
                                 slotSwordValue,
                                 blockPosHelperSolid,
                                 livingEntityHelperVisible,
                                 amountAmounts,
                                 absorptionPosData,
                                 vecBlockingData,
                                 base,
                                 hit,
                                 sampleTick,
                                 valueGround,
                                 kick,
                                 vec3d2,
                                 vec3d3,
                                 degrees,
                                 vec3,
                                 vec3d6,
                                 add,
                                 vec3d5,
                                 sigma,
                                 box,
                                 vec7,
                                 player,
                                 stackKnockback,
                                 slotSwordValue11,
                                 vecPosFlag,
                                 slotSwordValue13,
                                 slotSwordValue10,
                                 slotSwordFactor,
                                 crystalPitchValue,
                                 ping,
                                 max5,
                                 y,
                                 dataForDouble,
                                 slotSwordValue7,
                                 window,
                                 offerExpoLimit,
                                 seed
                              );
                              if (intersectsExisting != null) {
                                 arrayList.add(intersectsExisting);
                              }

                              if (!slotSwordFlag3 && !base.armed()) {
                                 VecPos.Eval raisedDistance = this.getRaisedDistance(
                                    blockPosHelperSolid,
                                    livingEntityHelperVisible,
                                    amountAmounts,
                                    absorptionPosData,
                                    vecBlockingData,
                                    base,
                                    hit,
                                    sampleTick,
                                    valueGround,
                                    kick,
                                    vec3d2,
                                    vec3d3,
                                    degrees,
                                    vec3,
                                    vec3d6,
                                    vec3d5,
                                    vec7,
                                    stackKnockback,
                                    slotSwordValue11,
                                    vecPosFlag,
                                    slotSwordValue13,
                                    slotSwordValue10,
                                    slotSwordFactor,
                                    crystalPitchValue,
                                    ping,
                                    max5,
                                    y,
                                    dataForDouble,
                                    slotSwordValue7,
                                    window,
                                    offerExpoLimit,
                                    seed
                                 );
                                 if (raisedDistance != null) {
                                    arrayList.add(raisedDistance);
                                 }
                              }

                              if (slotSwordFlag3 && base.existing() && !base.armed()) {
                                 VecPos.Eval punchStrike = this.getPunchStrike(
                                    blockPosHelperSolid,
                                    livingEntityHelperVisible,
                                    amountAmounts,
                                    absorptionPosData,
                                    vecBlockingData,
                                    base,
                                    sampleTick,
                                    valueGround,
                                    kick,
                                    vec3d2,
                                    vec3d3,
                                    degrees,
                                    vec3,
                                    vec3d6,
                                    vec3d5,
                                    vec7,
                                    stackKnockback,
                                    slotSwordValue11,
                                    slotSwordValue13,
                                    slotSwordValue10,
                                    slotSwordFactor,
                                    crystalPitchValue,
                                    ping,
                                    max5,
                                    y,
                                    dataForDouble,
                                    slotSwordValue7,
                                    window,
                                    offerExpoLimit,
                                    seed
                                 );
                                 if (punchStrike != null) {
                                    arrayList.add(punchStrike);
                                 }
                              }
                           }
                        }
                     }

                     arrayList.addAll(
                        this.getCrystalPitch(
                           blockPosHelperSolid,
                           livingEntityHelperVisible,
                           amountAmounts,
                           absorptionPosData,
                           vecBlockingData,
                           player,
                           stackKnockback,
                           vec3d,
                           vecPosLimit,
                           vec3d3,
                           crystalPitchValue,
                           ping,
                           max5,
                           y,
                           dataForDouble,
                           slotSwordValue7,
                           window,
                           window2,
                           offerExpoLimit,
                           seed,
                           predicate2,
                           box
                        )
                     );
                     arrayList.sort(Comparator.<VecPos.Eval>comparingDouble(eval -> eval.offer().value()).reversed());
                     return this.getOfferExpo(arrayList, blockPosHelperSolid, livingEntityHelperVisible, amountAmounts, absorptionPosData, vecBlockingData, player, vec3d3, crystalPitchValue, y, dataForDouble, slotSwordValue7, window, window2, offerExpoLimit, seed);
                  }
               }
            }
         }
      } else {
         return List.of();
      }
   }

   private List<VecPos.Eval> getOfferExpo(
      List<VecPos.Eval> list,
      WorldCollisionHelper blockPosHelperSolid,
      LivingEntityHelperVisible livingEntityHelperVisible,
      AmountAmounts amountAmounts,
      AbsorptionPosData absorptionPosData,
      VecBlockingData vecBlockingData,
      ClientPlayerEntity entity,
      Vec3d vec,
      int offerExpoValue,
      double y,
      VecPosUtilsDamage.Weights weights,
      double offerExpoValue3,
      PosBlockUtilsBonus.Window window,
      PosBlockUtilsBonus.Window window2,
      float offerExpoLimit,
      LivingEntityHelperVisible.Seed seed
   ) {
      ArrayList arrayList = new ArrayList(list.size());
      ArrayList arrayList2 = new ArrayList();
      HashMap hashMap = new HashMap();
      HashSet hashSet = new HashSet();
      HashMap hashMap2 = new HashMap();
      int offerExpoCounter = 0;

      for (VecPos.Eval eval : list) {
         VecPos.Offer offer = eval.offer();
         VecPos.Parts parts = eval.parts();
         VecPos.Probe probe = new VecPos.Probe(offer.base(), offer.arm() == VecPos.Arm.CRYSTAL_LAUNCH);
         if (!hashSet.contains(probe)) {
            BlockPos blockPos = eval.build() ? offer.base() : null;
            VecPos.SelfKey selfKey = new VecPos.SelfKey(offer.base(), parts.ourFeet());
            Double double2 = (Double)hashMap.get(probe);
            float vecPosLimit;
            if (double2 == null) {
               if (offerExpoCounter >= 4 || vecBlockingData.entity() == null || this.offerRateLimiter == null || !this.offerRateLimiter.tryReserveRays(90, 4)) {
                  arrayList2.add(eval);
                  continue;
               }

               offerExpoCounter++;
               Box box = WorldCollisionHelper.getPlayerBoxAt(parts.feet().x, parts.feet().y, parts.feet().z, 0.0);
               vecPosLimit = BlockEntityUtils.getAmount(parts.centre(), box, vecBlockingData.entity(), blockPos);
               if (!(vecPosLimit > 0.0F)) {
                  hashSet.add(probe);
                  continue;
               }

               hashMap.put(probe, getAmountDouble(vecPosLimit, eval.expo()));
               Vec3d ourFeet = parts.ourFeet();
               Box playerBoxAt = WorldCollisionHelper.getPlayerBoxAt(ourFeet.x, ourFeet.y, ourFeet.z, 0.0);
               hashMap2.put(selfKey, BlockEntityUtils.getCreativeSpectator(entity, parts.centre(), ourFeet, playerBoxAt, blockPos, null, 6.0F));
            } else {
               vecPosLimit = (float)getAmount(eval.expo(), double2);
               if (!(vecPosLimit > 0.0F)) {
                  continue;
               }
            }

            Float float2 = (Float)hashMap2.get(selfKey);
            float selfDamage = offer.selfDamage();
            if (float2 != null) {
               float toughnessEpf = PosBlockUtilsBonus.getToughnessEpf(float2, absorptionPosData.armor());
               selfDamage = PosBlockUtilsBonus.getScaledInstance(float2, absorptionPosData.armor(), window, offer.breakDelta());
               if (toughnessEpf > 0.0F && toughnessEpf >= absorptionPosData.health() + absorptionPosData.absorption() - 1.0F) {
                  continue;
               }
            }

            if (selfDamage <= offerExpoLimit) {
               double kickIdx = getKickIdx(parts, vecPosLimit, selfDamage, offerExpoLimit, vecBlockingData, amountAmounts, weights, y);
               if (Double.isFinite(kickIdx)) {
                  arrayList.add(new VecPos.Eval(getHitSpacing(offer, kickIdx + offerExpoValue3, selfDamage), parts, (double)vecPosLimit, eval.build()));
               }
            }
         }
      }

      arrayList.sort(Comparator.<VecPos.Eval>comparingDouble(eval2 -> eval2.offer().value()).reversed());
      double offerExpoValue4 = arrayList.isEmpty() ? Double.POSITIVE_INFINITY : ((VecPos.Eval)arrayList.get(arrayList.size() - 1)).offer().value();
      ArrayList arrayList3 = new ArrayList(arrayList.size() + arrayList2.size());
      arrayList3.addAll(arrayList);
      ArrayList supplier = new ArrayList(arrayList2.size());

      for (VecPos.Eval eval3 : (Iterable<com.corz.client.inventory.VecPos.Eval>) (arrayList2)) {
         double amountForDouble = getAmountForDouble(eval3.offer().value(), offerExpoValue4);
         supplier.add(
            amountForDouble == eval3.offer().value()
               ? eval3
               : new VecPos.Eval(getHitSpacing(eval3.offer(), amountForDouble, eval3.offer().selfDamage()), eval3.parts(), eval3.expo(), eval3.build())
         );
      }

      supplier.sort(Comparator.<VecPos.Eval>comparingDouble(eval4 -> eval4.offer().value()).reversed());
      arrayList3.addAll(supplier);
      return arrayList3;
   }

   public VecPos(RayBudget max) {
      this.offerRateLimiter = max;
   }

   static double getAmount(double vecPosValue, boolean vecPosFlag) {
      return vecPosValue + (vecPosFlag ? 0.3 : 0.35);
   }

   private static double getSigma(Box box, Box box2, PosVecUtils.Sample sample, int sigmaBound) {
      double minMax = WorldCollisionHelper.computeBoxGap(box, box2);
      double sigmaDivisor = Math.min(1.2, Math.max(0.05, sample.sigma() + 0.05 * Math.min(sigmaBound, 20)));
      return WorldCollisionHelper.normalCdf(-Math.max(0.0, minMax) / sigmaDivisor);
   }

   static double getAmountForDouble(double vecPosValue, double vecPosLimit) {
      return Double.isFinite(vecPosLimit) && !(vecPosValue < vecPosLimit) ? vecPosLimit - 0.001 : vecPosValue;
   }

   public static VecPos.Loadout getStackKnockback(ClientPlayerEntity clientPlayerEntity, boolean stackKnockbackFlag) {
      PlayerInventory inventory = clientPlayerEntity.getInventory();
      int stackKnockbackValue = -1;
      int stackKnockbackBound = -1;
      ItemStack itemStack = ItemStack.EMPTY;

      for (int i = 0; i < 9; i++) {
         ItemStack stack = inventory.getStack(i);
         if (InventoryStackUtilsEmpty.canSwordsEmpty(stack)) {
            int enchantmentsDefault = InventoryStackUtilsEmpty.getEnchantmentsDefault(stack, Enchantments.KNOCKBACK);
            if (enchantmentsDefault > stackKnockbackBound || enchantmentsDefault == stackKnockbackBound && getAxeSword(stack) > getAxeSword(itemStack)) {
               stackKnockbackBound = enchantmentsDefault;
               stackKnockbackValue = i;
               itemStack = stack;
            }
         }
      }

      int stackKnockbackValue2 = SelectedInventoryUtils.getValue(Items.END_CRYSTAL);
      int stackKnockbackValue3 = stackKnockbackFlag ? SelectedInventoryUtils.getValue(Items.OBSIDIAN) : -1;
      return new VecPos.Loadout(stackKnockbackValue, Math.max(0, stackKnockbackBound), itemStack, stackKnockbackValue2, stackKnockbackValue3, getEmptyEnd(inventory.getStack(inventory.getSelectedSlot())));
   }

   public List<VecPos.Offer> getOffer(
      long offerValue,
      WorldCollisionHelper blockPosHelperSolid,
      LivingEntityHelperVisible livingEntityHelperVisible,
      AbsorptionPosData absorptionPosData,
      VecBlockingData vecBlockingData,
      MinVec minVec,
      PearlsPlaystyleData pearlsPlaystyleData,
      SelfHazData selfHazData,
      double slotSwordFactor,
      int slotSwordBound,
      float offerExpoLimit,
      Predicate<Vec3d> predicate
   ) {
      List slotSword = this.getSlotSword(offerValue, blockPosHelperSolid, livingEntityHelperVisible, absorptionPosData, vecBlockingData, minVec, pearlsPlaystyleData, selfHazData, slotSwordFactor, slotSwordBound, offerExpoLimit, predicate);
      ArrayList arrayList = new ArrayList(slotSword.size());

      for (VecPos.Eval eval : (Iterable<com.corz.client.inventory.VecPos.Eval>) (slotSword)) {
         arrayList.add(eval.offer());
      }

      return arrayList;
   }

   private VecPos.Eval getExistingArmor(
      VecPos.Arm arm,
      VecPos.Hit hit,
      VecPos.Base base,
      PosVecUtils.Sample sample,
      Vec3d vec,
      double existingArmorFactor,
      int existingArmorValue,
      int crystalPitchValue,
      PosVecUtils.Path path,
      int intersectsExistingLimit,
      PosVecUtils.Kick kick,
      LivingEntityHelperVisible livingEntityHelperVisible,
      WorldCollisionHelper blockPosHelperSolid,
      AmountAmounts amountAmounts,
      AbsorptionPosData absorptionPosData,
      VecBlockingData vecBlockingData,
      Vec3d vec2,
      Vec3d vec3,
      float existingArmorValue4,
      double pilotRenderLimit,
      VecPosUtilsDamage.Weights weights,
      double existingArmorValue6,
      PosBlockUtilsBonus.Window window,
      float offerExpoLimit,
      LivingEntityHelperVisible.Seed seed,
      int sigmaBound,
      int existingArmorValue9,
      double normalCdf,
      float degrees,
      Vec3d vec4,
      double slotSwordFactor,
      int existingArmorValue13,
      boolean existingArmorFlag,
      PosBlockUtilsBonus.Window window3
   ) {
      Vec3d vec3d = new Vec3d(sample.x(), sample.y(), sample.z());
      double[] existingArmorValues = VecPosUtilsAmount.getAmounts(sample.x(), sample.y(), sample.z(), 1.62, vec.x, vec.y, vec.z, 6.0, 1.0, seed.rExpl());
      int pathBound = existingArmorValue + crystalPitchValue;
      PosVecUtils.Sample dataForInt = path.getDataForInt(pathBound);
      int existingArmorValue15 = intersectsExistingLimit < path.getValue() ? intersectsExistingLimit : pathBound;
      double distanceTo = vec3.distanceTo(vec);
      float scaledValue = BlockEntityUtils.getAmount(MinecraftClient.getInstance().player, distanceTo, 6.0F)
         * (float)EntityHeightSamples.getReachingFraction(vec3.y, base.top());
      float scaledInstance = PosBlockUtilsBonus.getScaledInstance(scaledValue, absorptionPosData.armor(), window, existingArmorValue);
      float toughnessEpf = PosBlockUtilsBonus.getToughnessEpf(scaledValue, absorptionPosData.armor());
      if (toughnessEpf > 0.0F && toughnessEpf >= absorptionPosData.health() + absorptionPosData.absorption() - 1.0F) {
         return null;
      } else {
         double meleeScale = amountAmounts.getMeleeScale();
         boolean posTop = canPosTop(base, dataForInt, dataForInt.vy() + existingArmorValues[1] * existingArmorFactor * meleeScale, existingArmorValues, existingArmorFactor * meleeScale);
         VecPos.Parts parts = new VecPos.Parts(vec3d, vec, existingArmorValues, dataForInt.y(), dataForInt.vy(), pathBound, existingArmorValue15, existingArmorValue4, window3, normalCdf, posTop, existingArmorValue9, sigmaBound, existingArmorValue, vec3);
         double kickIdx = getKickIdx(parts, existingArmorFactor, scaledInstance, offerExpoLimit, vecBlockingData, amountAmounts, weights, pilotRenderLimit);
         if (!Double.isFinite(kickIdx)) {
            return null;
         } else {
            double existingArmorValue17 = kickIdx + existingArmorValue6;
            BlockPos blockPos = base.existing() ? null : base.pos();
            VecPos.Offer offer = new VecPos.Offer(
               arm, hit, existingArmorValue17, sigmaBound, base.pos(), blockPos, existingArmorValue, degrees, vec4, slotSwordFactor, existingArmorValue13, existingArmorFlag, scaledInstance
            );
            return new VecPos.Eval(offer, parts, existingArmorFactor, !base.existing());
         }
      }
   }

   private static boolean canPosTop(VecPos.Base base, PosVecUtils.Sample sample, double posTopValue, double[] posTopValues, double posTopFactor) {
      int posTopBound = PosVecUtils.getValueInt(posTopValue);
      double blockPosValue = sample.x() + (sample.vx() + posTopValues[0] * posTopFactor) * Math.min(posTopBound, 10) * 0.7;
      double z = sample.z() + (sample.vz() + posTopValues[2] * posTopFactor) * Math.min(posTopBound, 10) * 0.7;
      double posTopLimit = sample.y() + PosVecUtils.getAmountDouble(posTopValue, Math.max(0, posTopBound));
      double posTopValue4 = WorldCollisionHelper.getAmount(blockPosValue, z, base.pos().getX() + 0.5, base.pos().getZ() + 0.5);
      return posTopValue4 <= 2.0 && posTopLimit >= base.top() + 1.5;
   }

   private static VecPos.Offer getHitSpacing(VecPos.Offer offer, double hitSpacingValue, float selfDamage) {
      return new VecPos.Offer(
         offer.arm(),
         offer.hit(),
         hitSpacingValue,
         offer.prepTicks(),
         offer.base(),
         offer.obsidianCell(),
         offer.breakDelta(),
         offer.launchYaw(),
         offer.lineUpFeet(),
         offer.spacing(),
         offer.weaponSlot(),
         offer.sprintHit(),
         selfDamage
      );
   }

   public static float getAxeSword(ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         Item item = stack.getItem();
         float axeSwordValue;
         if (item == Items.NETHERITE_SWORD) {
            axeSwordValue = 8.0F;
         } else if (item == Items.DIAMOND_SWORD) {
            axeSwordValue = 7.0F;
         } else if (item == Items.IRON_SWORD) {
            axeSwordValue = 6.0F;
         } else if (item == Items.STONE_SWORD) {
            axeSwordValue = 5.0F;
         } else if (item == Items.GOLDEN_SWORD || item == Items.WOODEN_SWORD) {
            axeSwordValue = 4.0F;
         } else if (item == Items.NETHERITE_AXE) {
            axeSwordValue = 10.0F;
         } else if (item == Items.DIAMOND_AXE || item == Items.IRON_AXE || item == Items.STONE_AXE) {
            axeSwordValue = 9.0F;
         } else if (item == Items.GOLDEN_AXE || item == Items.WOODEN_AXE) {
            axeSwordValue = 7.0F;
         } else if (InventoryStackUtilsEmpty.canEmpty(stack)) {
            axeSwordValue = 6.0F;
         } else if (stack.isIn(ItemTags.SWORDS)) {
            axeSwordValue = 6.0F;
         } else {
            axeSwordValue = 1.0F;
         }

         int enchantmentsDefault = InventoryStackUtilsEmpty.getEnchantmentsDefault(stack, Enchantments.SHARPNESS);
         return axeSwordValue + (enchantmentsDefault > 0 ? 0.5F * enchantmentsDefault + 0.5F : 0.0F);
      } else {
         return 1.0F;
      }
   }

   static double getAmountDouble(double vecPosLimit, double vecPosLimit2) {
      return vecPosLimit > 0.0 && vecPosLimit2 > 0.0 ? Math.min(1.0, vecPosLimit / vecPosLimit2) : 0.0;
   }

   static double getAmount(int vecPosBound) {
      return 0.12 * Math.min(Math.max(0, vecPosBound), 3);
   }

   private static List<VecPos.Base> getAmounts(
      WorldCollisionHelper blockPosHelperSolid, Vec3d vec, Vec3d vec2, int vecPosLimit, boolean vecPosFlag, Predicate<Vec3d> predicate, Entity entity, Entity player
   ) {
      ArrayList arrayList = new ArrayList();
      ArrayList arrayList2 = new ArrayList();

      for (double vecPosValue : vecPosTable) {
         double[] vecPosValues = VecShieldUtilsAmounts.getAmountsForDouble(vec.x, vec.z, vec2.x, vec2.z, vecPosValue);
         int x = (int)Math.floor(vecPosValues[0]);
         int z = (int)Math.floor(vecPosValues[1]);

         for (int i = vecPosLimit - 2; i <= vecPosLimit; i++) {
            BlockPos blockPos = new BlockPos(x, i, z);
            if (!arrayList2.contains(blockPos)) {
               arrayList2.add(blockPos);
               boolean vecPosFlag2 = i == vecPosLimit;
               boolean world = blockPosHelperSolid.canPlaceOnBedrockObsidian(blockPos);
               boolean vecPosFlag3 = world && blockPosHelperSolid.findCrystalAt(blockPos) != null;
               boolean vecPosFlag4 = world && (vecPosFlag3 || blockPosHelperSolid.isAirAt(blockPos.up()));
               boolean vecPosFlag5 = !world && vecPosFlag && blockPosHelperSolid.isReplaceableWithSupportBelow(blockPos);
               if ((vecPosFlag4 || vecPosFlag5) && (!vecPosFlag4 || vecPosFlag3 || !blockPosHelperSolid.hasBlockingEntities(blockPos, entity, player)) && !predicate.test(BlockEntityUtils.getVec3d(blockPos))) {
                  arrayList.add(new VecPos.Base(blockPos, blockPos.getY() + 1, vecPosFlag4, vecPosFlag2, vecPosFlag3));
               }
            }
         }
      }

      return arrayList;
   }

   public static enum Arm {
      KB_HIT_CRYSTAL,
      KB_POP_BUILD,
      PUNCH_POP,
      CRYSTAL_LAUNCH;
   }

   private record Base(BlockPos pos, int top, boolean existing, boolean raised, boolean armed) {
   }

   private record Eval(VecPos.Offer offer, VecPos.Parts parts, double expo, boolean build) {
   }

   public static enum Hit {
      KB_SPRINT,
      KB_WALK,
      PUNCH_SPRINT,
      NONE;
   }

   public record Loadout(int swordSlot, int knockback, ItemStack sword, int crystalSlot, int obsidianSlot, int selectedItem) {
   }

   public record Offer(
      VecPos.Arm arm,
      VecPos.Hit hit,
      double value,
      int prepTicks,
      BlockPos base,
      BlockPos obsidianCell,
      int breakDelta,
      float launchYaw,
      Vec3d lineUpFeet,
      double spacing,
      int weaponSlot,
      boolean sprintHit,
      float selfDamage
   ) {
   }

   private record Parts(
      Vec3d feet,
      Vec3d centre,
      double[] unitKb,
      double kickY,
      double kickVy,
      int kickIdx,
      int baseLand,
      float meleeRaw,
      PosBlockUtilsBonus.Window window,
      double failRisk,
      boolean reusable,
      int obsidianPlaces,
      int prep,
      int blastIdx,
      Vec3d ourFeet
   ) {
   }

   private record Probe(BlockPos base, boolean launch) {
   }

   private record SelfKey(BlockPos base, Vec3d ourFeet) {
   }
}
