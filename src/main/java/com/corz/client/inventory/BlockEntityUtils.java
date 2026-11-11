package com.corz.client.inventory;

import com.corz.client.world.BoxLengthUtils;
import com.corz.client.world.MathClampUtils;
import com.corz.client.world.PosBlockUtilsBonus;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Predicate;
import net.minecraft.block.AbstractRedstoneGateBlock;
import net.minecraft.block.AnvilBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ButtonBlock;
import net.minecraft.block.CakeBlock;
import net.minecraft.block.CartographyTableBlock;
import net.minecraft.block.ComposterBlock;
import net.minecraft.block.CraftingTableBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.DragonEggBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.GrindstoneBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.LeverBlock;
import net.minecraft.block.LoomBlock;
import net.minecraft.block.NoteBlock;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.StonecutterBlock;
import net.minecraft.block.TrapdoorBlock;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.hit.HitResult.Type;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.RaycastContext.FluidHandling;
import net.minecraft.world.RaycastContext.ShapeType;

public final class BlockEntityUtils {
   public static final Direction[] idleDirections = new Direction[]{Direction.UP, Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST};
   public static final Direction[] directions = new Direction[]{Direction.UP};
   private static RegistryEntry<Enchantment> registryEntry;
   private static final double[] squaredTable = new double[]{0.2, 0.35, 0.5, 0.65, 0.8};
   private static final double[] pointVectorTable = new double[]{0.25, 0.5, 0.75};
   private static final int[][] blockEntityInts = new int[][]{{0, -1}, {0, 1}, {-1, 0}, {1, 0}, {-1, -1}, {1, -1}, {-1, 1}, {1, 1}};
   private static final EquipmentSlot[] equipmentSlots = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
   private static final MinecraftClient blockEntityMinecraftClient = MinecraftClient.getInstance();
   private static final int UNUSED_ONE = 1;
   private static DynamicRegistryManager registryManager;
   private static RegistryEntry<Enchantment> optionalEnchantment;

   public static boolean canEntitiesInstance(BlockPos pos) {
      return canBedrockObsidian(pos) && blockEntityMinecraftClient.world.isAir(pos.up()) && blockEntityMinecraftClient.world.getOtherEntities(null, getBox3(pos)).isEmpty();
   }

   public static boolean canBedrockObsidian(BlockPos pos2) {
      BlockState blockState = blockEntityMinecraftClient.world.getBlockState(pos2);
      return blockState.isOf(Blocks.OBSIDIAN) || blockState.isOf(Blocks.BEDROCK);
   }

   public static boolean isEmpty(BlockPos pos) {
      if (blockEntityMinecraftClient.world.getBlockState(pos).isReplaceable() && blockEntityMinecraftClient.world.isAir(pos.up())) {
         return !blockEntityMinecraftClient.world.canPlace(Blocks.OBSIDIAN.getDefaultState(), pos, ShapeContext.absent())
            ? false
            : blockEntityMinecraftClient.world.getOtherEntities(null, getBox3(pos)).isEmpty();
      } else {
         return false;
      }
   }

   public static boolean isModifyBlocks() {
      return blockEntityMinecraftClient.player.canModifyBlocks();
   }

   public static boolean isAmplifierEffect() {
      StatusEffectInstance statusEffect = blockEntityMinecraftClient.player.getStatusEffect(StatusEffects.WEAKNESS);
      if (statusEffect == null) {
         return true;
      } else {
         StatusEffectInstance statusEffect2 = blockEntityMinecraftClient.player.getStatusEffect(StatusEffects.STRENGTH);
         double amplifierEffectLimit = 1.0 - 4.0 * (statusEffect.getAmplifier() + 1) + (statusEffect2 == null ? 0.0 : 3.0 * (statusEffect2.getAmplifier() + 1));
         return amplifierEffectLimit > 0.0;
      }
   }

   private static Vec3d getVectorMultiply(Vec3d vec, float vectorMultiplyValue, float degrees, double raycastVectorFactor) {
      return vec.add(blockEntityMinecraftClient.player.getRotationVector(degrees, vectorMultiplyValue).multiply(raycastVectorFactor + 1.0));
   }

   public static boolean canModifyInstance(BlockPos pos) {
      if (blockEntityMinecraftClient.player.canModifyBlocks()) {
         return true;
      } else {
         int modifyLimit = SelectedInventoryUtils.getValue(Items.END_CRYSTAL);
         return modifyLimit >= 0 && blockEntityMinecraftClient.player.getInventory().getStack(modifyLimit).canPlaceOn(new CachedBlockPosition(blockEntityMinecraftClient.world, pos, false));
      }
   }

   public static boolean canAbsorptionHealth(Vec3d vec, BlockPos pos, boolean absorptionHealthFlag, float absorptionHealthValue) {
      ClientPlayerEntity player = blockEntityMinecraftClient.player;
      float attributeArmor = getAttributeArmor(vec, pos);
      return BoxLengthUtils.canFlag(attributeArmor, player.getHealth(), player.getAbsorptionAmount(), absorptionHealthFlag, absorptionHealthValue);
   }

   public static float getAmount(Vec3d vec, Box box, Entity entity, BlockPos pos) {
      return getLerpMin(vec, box, entity, pos, null);
   }

   public static boolean canBoundingBox(Vec3d vec, float boundingBoxValue, float degrees, Entity entity, double raycastVectorFactor) {
      if (!canRaycastVector(vec, boundingBoxValue, degrees, entity, raycastVectorFactor)) {
         return false;
      } else {
         Box boundingBox = entity.getBoundingBox();
         return boundingBox.contains(vec) ? true : BoxLengthUtils.canLengthContract(boundingBox, vec, getVectorMultiply(vec, boundingBoxValue, degrees, raycastVectorFactor), 0.1);
      }
   }

   public static boolean canFlag(float blockEntityValue, float clamp, float[] blockEntityValues, float blockEntityLimit) {
      return blockEntityValues != null && blockEntityValues.length >= 2 && blockEntityLimit > 0.0F && Float.isFinite(blockEntityLimit)
         ? Math.abs(BoxLengthUtils.getAmount3((double)blockEntityValues[0] - blockEntityValue)) <= blockEntityLimit && Math.abs((double)blockEntityValues[1] - clamp) <= blockEntityLimit
         : false;
   }

   public static boolean canSquaredDirections(BlockPos pos, Vec3d vec, double squaredDirectionsFactor) {
      for (Direction direction : idleDirections) {
         if (canFlag(vec, pos, direction)) {
            Vec3d vec3d = getVec3d(pos, direction, 0.5, 0.5);
            if (!(vec.squaredDistanceTo(vec3d) > squaredDirectionsFactor * squaredDirectionsFactor) && canSubtractRaycast(vec, vec3d)) {
               return true;
            }
         }
      }

      return false;
   }

   public static double getMinMax(Vec3d vec, Box box) {
      return BoxLengthUtils.getAmount(vec.x, vec.y, vec.z, box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
   }

   public static Vec3d getVec3d(BlockPos pos2, Direction direction) {
      return getVec3d(pos2, direction, 0.5, 0.5);
   }

   public static Vec3d getVec3d(Box box, Entity entity, Vec3d vec, float vec3dValue, float vec3dValue2, double vec3dValue3) {
      return getPointVector(box, entity, vec, vec3dValue, vec3dValue2, vec3dValue3, 0.0);
   }

   public static Vec3d getVec3d(BlockPos pos) {
      return new Vec3d(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5);
   }

   private static void handle(double[] blockEntityValues, double blockEntityFactor, double blockEntityValue, double blockEntityFactor2, double blockEntityFactor3, boolean blockEntityFlag) {
      double blockEntityFactor4 = blockEntityFactor * (1.0 - blockEntityValue);
      if (blockEntityFactor4 > 0.0) {
         blockEntityValues[0] = blockEntityValues[0] / 2.0 + blockEntityFactor2 * blockEntityFactor4;
         blockEntityValues[1] = blockEntityFlag ? Math.min(0.4, blockEntityValues[1] / 2.0 + blockEntityFactor4) : blockEntityValues[1];
         blockEntityValues[2] = blockEntityValues[2] / 2.0 + blockEntityFactor3 * blockEntityFactor4;
      }
   }

   public static float getAttributeArmor(Vec3d vec, BlockPos pos) {
      ClientPlayerEntity player = blockEntityMinecraftClient.player;
      if (player != null && blockEntityMinecraftClient.world != null && !player.isCreative() && !player.isSpectator()) {
         double sqrt = Math.sqrt(player.squaredDistanceTo(vec));
         if (sqrt > 12.0) {
            return 0.0F;
         } else {
            float attributeArmorValue = getAmount(vec, player.getBoundingBox(), player, pos);
            float boxLengthValue = (float)BoxLengthUtils.getAmount3(sqrt, attributeArmorValue, 6.0);
            StatusEffectInstance statusEffect = player.getStatusEffect(StatusEffects.RESISTANCE);
            return BoxLengthUtils.getAmount(
               boxLengthValue,
               blockEntityMinecraftClient.world.getDifficulty().getId(),
               (float)player.getAttributeValue(EntityAttributes.ARMOR),
               (float)player.getAttributeValue(EntityAttributes.ARMOR_TOUGHNESS),
               statusEffect == null ? -1 : statusEffect.getAmplifier(),
               getRegistryLevel(player)
            );
         }
      } else {
         return 0.0F;
      }
   }

   public static boolean canFlag(Vec3d vec, Vec3d vec2, float blockEntityValue, double boxLengthLimit) {
      return BoxLengthUtils.canFlag(vec2.x - vec.x, vec2.z - vec.z, blockEntityValue, boxLengthLimit);
   }

   public static float[] getAmounts(Vec3d vec, float blockEntityValue, float clamp, float blockEntityLimit, Predicate<HitResult> predicate) {
      if (vec != null && predicate != null && blockEntityLimit > 0.0F && Float.isFinite(blockEntityLimit)) {
         int max2 = Math.max(1, Math.min(10, (int)Math.ceil(3.0 / blockEntityLimit)));

         for (int i = 1; i <= max2; i++) {
            float blockEntityFactor = i * blockEntityLimit;

            for (int[] blockEntityValues : blockEntityInts) {
               float multiplySquaredValue = blockEntityValue + blockEntityValues[0] * blockEntityFactor;
               float blockEntityLimit3 = clamp + blockEntityValues[1] * blockEntityFactor;
               if (!(blockEntityLimit3 < -90.0F) && !(blockEntityLimit3 > 90.0F) && predicate.test(getMultiplySquared(vec, multiplySquaredValue, blockEntityLimit3))) {
                  return new float[]{multiplySquaredValue, blockEntityLimit3};
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   private static void updateRegistryProtection() {
      DynamicRegistryManager dynamicRegistryManager = blockEntityMinecraftClient.world.getRegistryManager();
      if (dynamicRegistryManager != registryManager) {
         registryManager = dynamicRegistryManager;
         optionalEnchantment = getOptionalEnchantment(dynamicRegistryManager, Enchantments.PROTECTION);
         registryEntry = getOptionalEnchantment(dynamicRegistryManager, Enchantments.BLAST_PROTECTION);
      }
   }

   public static EndCrystalEntity getNext(BlockPos pos) {
      BlockPos up = pos.up();

      for (EndCrystalEntity next : blockEntityMinecraftClient.world.getEntitiesByClass(EndCrystalEntity.class, getBox3(pos), endCrystalEntity -> endCrystalEntity.isAlive() && !endCrystalEntity.isRemoved())) {
         if (next.getBlockPos().equals(up)) {
            return next;
         }
      }

      return null;
   }

   public static Box getBox(BlockPos pos) {
      return new Box(pos.getX() - 0.5, pos.getY() + 1.0, pos.getZ() - 0.5, pos.getX() + 1.5, pos.getY() + 3.0, pos.getZ() + 1.5);
   }

   static boolean canFlag(Vec3d vec, BlockPos pos2, Direction direction) {
      return switch (direction) {
         case UP -> vec.y > pos2.getY() + 1.0;
         case DOWN -> vec.y < pos2.getY();
         case NORTH -> vec.z < pos2.getZ();
         case SOUTH -> vec.z > pos2.getZ() + 1.0;
         case WEST -> vec.x < pos2.getX();
         case EAST -> vec.x > pos2.getX() + 1.0;
      };
   }

   private static BlockEntityUtils.Sample getVecDataForVec3dFloat(Vec3d vec, Vec3d vec2, float vecValue, float clamp) {
      float[] wrapDegrees = MathClampUtils.getWrapDegrees(vec, vec2);
      return new BlockEntityUtils.Sample(vec2, wrapDegrees[0], wrapDegrees[1], BoxLengthUtils.getAmount(vecValue, clamp, wrapDegrees[0], wrapDegrees[1]));
   }

   public static float getAmount(LivingEntity entity, double blockEntityValue, float distanceValue) {
      return PosBlockUtilsBonus.getAmount((float)BoxLengthUtils.getAmount3(blockEntityValue, 1.0, distanceValue), getDifficultyInstance(entity));
   }

   public static boolean canBlockSquare(BlockPos pos2, Direction direction) {
      BlockState blockState = blockEntityMinecraftClient.world.getBlockState(pos2);
      return !blockState.hasBlockEntity() && !canFlag(blockState.getBlock()) ? blockState.isSideSolidFullSquare(blockEntityMinecraftClient.world, pos2, direction) : false;
   }

   public static float getCreativeSpectator(LivingEntity entity, Vec3d vec, Vec3d vec2, Box box, BlockPos pos, BlockPos pos2, float creativeSpectatorValue) {
      return !(entity instanceof PlayerEntity playerEntity && (playerEntity.isCreative() || playerEntity.isSpectator()))
         ? PosBlockUtilsBonus.getAmount(getDistance(vec, vec2, box, entity, pos, pos2, creativeSpectatorValue), getDifficultyInstance(entity))
         : 0.0F;
   }

   public static HitResult getMultiplySquared(Vec3d vec, float multiplySquaredValue, float clamp) {
      ClientPlayerEntity player = blockEntityMinecraftClient.player;
      double blockInteractionRange = player.getBlockInteractionRange();
      double multiplySquaredFactor = Math.max(blockInteractionRange, 3.0);
      Vec3d rotationVector = player.getRotationVector(clamp, multiplySquaredValue);
      Vec3d add = vec.add(rotationVector.multiply(multiplySquaredFactor));
      BlockHitResult raycast = blockEntityMinecraftClient.world.raycast(new RaycastContext(vec, add, ShapeType.OUTLINE, FluidHandling.NONE, player));
      double squaredDistanceTo = vec.squaredDistanceTo(raycast.getPos());
      double squaredDistanceTo3 = multiplySquaredFactor * multiplySquaredFactor;
      double multiplySquaredFactor4 = multiplySquaredFactor;
      if (raycast.getType() != Type.MISS) {
         squaredDistanceTo3 = squaredDistanceTo;
         multiplySquaredFactor4 = Math.sqrt(squaredDistanceTo);
      }

      Vec3d add2 = vec.add(rotationVector.multiply(multiplySquaredFactor4));
      Box expand = player.getBoundingBox().stretch(rotationVector.multiply(multiplySquaredFactor4)).expand(1.0);
      EntityHitResult raycast2 = ProjectileUtil.raycast(player, vec, add2, expand, entity -> !entity.isSpectator() && entity.canHit(), squaredDistanceTo3);
      if (raycast2 != null && vec.squaredDistanceTo(raycast2.getPos()) < squaredDistanceTo) {
         return vec.squaredDistanceTo(raycast2.getPos()) < 9.0 ? raycast2 : null;
      } else {
         return raycast.getType() == Type.BLOCK && !(squaredDistanceTo > blockInteractionRange * blockInteractionRange) ? raycast : null;
      }
   }

   public static boolean canHandUndying(LivingEntity entity) {
      return entity.getMainHandStack().isOf(Items.TOTEM_OF_UNDYING) || entity.getOffHandStack().isOf(Items.TOTEM_OF_UNDYING);
   }

   private static boolean canMinMax(Vec3d vec, Box box, Direction direction) {
      return switch (direction) {
         case UP -> vec.y > box.maxY;
         case DOWN -> vec.y < box.minY;
         case NORTH -> vec.z < box.minZ;
         case SOUTH -> vec.z > box.maxZ;
         case WEST -> vec.x < box.minX;
         case EAST -> vec.x > box.maxX;
      };
   }

   public static BlockEntityUtils.Predicted getBoxBoundingDataForLivingEntityInt(LivingEntity entity, int boxBoundingValue, boolean boxBoundingFlag) {
      return getInterpolatorInstance(entity, boxBoundingValue, boxBoundingFlag, false);
   }

   public static Box getBoxForBlockPos(BlockPos pos2) {
      return new Box(pos2.getX(), pos2.getY(), pos2.getZ(), pos2.getX() + 1.0, pos2.getY() + 1.0, pos2.getZ() + 1.0);
   }

   public static boolean canVectorMultiply(Vec3d vec, float vectorMultiplyValue, float currentPitch, Entity entity, double raycastVectorFactor, double vectorMultiplyBound) {
      if (!canRaycastVector(vec, vectorMultiplyValue, currentPitch, entity, raycastVectorFactor)) {
         return false;
      } else {
         Box boundingBox = entity.getBoundingBox();
         if (boundingBox.contains(vec)) {
            return true;
         } else {
            Vec3d add = vec.add(blockEntityMinecraftClient.player.getRotationVector(currentPitch, vectorMultiplyValue).multiply(Math.max(0.0, vectorMultiplyBound)));
            return BoxLengthUtils.canLengthContract(boundingBox, vec, add, 0.1);
         }
      }
   }

   public static PosBlockUtilsBonus.Armor getAttributeArmor(LivingEntity entity) {
      StatusEffectInstance statusEffect = entity.getStatusEffect(StatusEffects.RESISTANCE);
      return new PosBlockUtilsBonus.Armor(
         (float)entity.getAttributeValue(EntityAttributes.ARMOR),
         (float)entity.getAttributeValue(EntityAttributes.ARMOR_TOUGHNESS),
         statusEffect == null ? -1 : statusEffect.getAmplifier(),
         getRegistryLevel(entity),
         getDifficultyInstance(entity)
      );
   }

   public static BlockEntityUtils.Predicted getOffsetFeet(BlockEntityUtils.Predicted predicted, Vec3d vec) {
      return new BlockEntityUtils.Predicted(predicted.feet().add(vec), predicted.box().offset(vec));
   }

   public static Box getBox3(BlockPos pos) {
      return new Box(pos.getX(), pos.getY() + 1.0, pos.getZ(), pos.getX() + 1.0, pos.getY() + 3.0, pos.getZ() + 1.0);
   }

   public static boolean canSubtractRaycast(Vec3d vec, Vec3d vec2) {
      Vec3d subtract = vec2.subtract(vec);
      double varLength = subtract.length();
      if (varLength < 1.0E-4) {
         return true;
      } else {
         Vec3d add = vec.add(subtract.multiply(Math.max(0.0, varLength - 0.05) / varLength));
         return blockEntityMinecraftClient.world.raycast(new RaycastContext(vec, add, ShapeType.OUTLINE, FluidHandling.NONE, blockEntityMinecraftClient.player)).getType() == Type.MISS;
      }
   }

   public static float[] getAmounts(Vec3d vec, float blockEntityValue, float amountFloat, BlockPos pos2, Direction direction, float blockEntityFactor) {
      int max3 = Math.max(1, Math.min(3, (int)Math.floor(1.5 / Math.max((double)blockEntityFactor, 0.001))));
      float blockEntityValue4 = (1 + ThreadLocalRandom.current().nextInt(max3)) * blockEntityFactor;
      float[][] floats = new float[][]{{blockEntityValue + blockEntityValue4, amountFloat}, {blockEntityValue - blockEntityValue4, amountFloat}, {blockEntityValue, amountFloat + blockEntityValue4}, {blockEntityValue, amountFloat - blockEntityValue4}};
      int randomInt = ThreadLocalRandom.current().nextInt(floats.length);

      for (int i = 0; i < floats.length; i++) {
         float[] blockEntityValues = floats[(randomInt + i) % floats.length];
         if (!(blockEntityValues[1] < -90.0F) && !(blockEntityValues[1] > 90.0F) && getPosBlock(getMultiplySquared(vec, blockEntityValues[0], blockEntityValues[1]), pos2, direction, vec, 4.4) != null) {
            return blockEntityValues;
         }
      }

      return null;
   }

   public static double getDistanceFeet(BlockEntityUtils.Predicted predicted, Entity entity, Vec3d vec, BlockPos pos) {
      double distanceTo = predicted.feet().distanceTo(vec);
      if (distanceTo > 12.0) {
         return 0.0;
      } else {
         float distanceFeetValue = getAmount(vec, predicted.box(), entity, pos);
         return BoxLengthUtils.getAmountForDouble(BoxLengthUtils.getAmount3(distanceTo, distanceFeetValue, 6.0));
      }
   }

   public static boolean canOppositeOffset(BlockPos pos) {
      for (Direction direction : Direction.values()) {
         if (canBlockState(pos.offset(direction), direction.getOpposite())) {
            return true;
         }
      }

      return false;
   }

   public static boolean canBlockState(BlockPos pos2, Direction direction) {
      return canBlockSquare(pos2, direction) || blockEntityMinecraftClient.world.getBlockState(pos2).getBlock() instanceof LeavesBlock;
   }

   public static boolean canFlag(BlockPos pos2) {
      return canBlockSquare(pos2, Direction.UP);
   }

   private BlockEntityUtils() {
   }

   public static BlockHitResult getAmountsSquared(
      BlockPos pos2, Direction[] directions, Direction direction, Vec3d vec, float squaredValue, float clamp, double squaredFactor
   ) {
      ArrayList arrayList = new ArrayList();

      for (Direction directionsItem : directions) {
         if (canFlag(vec, pos2, directionsItem)) {
            for (double squaredTableItem : squaredTable) {
               for (double vec3dValue2 : squaredTable) {
                  Vec3d vec3d = getVec3d(pos2, directionsItem, squaredTableItem, vec3dValue2);
                  if (!(vec.squaredDistanceTo(vec3d) > squaredFactor * squaredFactor)) {
                     arrayList.add(getVecDataForVec3dFloat(vec, vec3d, squaredValue, clamp));
                  }
               }
            }
         }
      }

      arrayList.sort(Comparator.comparingDouble(BlockEntityUtils.Sample::angle));
      int squaredCounter = 0;

      for (BlockEntityUtils.Sample sample : (Iterable<com.corz.client.inventory.BlockEntityUtils.Sample>) (arrayList)) {
         if (squaredCounter++ >= 16) {
            break;
         }

         BlockHitResult posBlock = getPosBlock(getMultiplySquared(vec, sample.yaw(), sample.pitch()), pos2, direction, vec, squaredFactor);
         if (posBlock != null) {
            return posBlock;
         }
      }

      return null;
   }

   public static double getMax(LivingEntity entity) {
      if (entity == null) {
         return 0.0;
      } else {
         Item[] items = new Item[]{Items.NETHERITE_HELMET, Items.NETHERITE_CHESTPLATE, Items.NETHERITE_LEGGINGS, Items.NETHERITE_BOOTS};
         int blockEntityCounter = 0;

         for (int i = 0; i < equipmentSlots.length; i++) {
            ItemStack equippedStack = entity.getEquippedStack(equipmentSlots[i]);
            if (equippedStack != null && equippedStack.isOf(items[i])) {
               blockEntityCounter++;
            }
         }

         double blockEntityValue = 0.0;
         if (entity.getAttributes() != null && entity.getAttributes().hasAttribute(EntityAttributes.KNOCKBACK_RESISTANCE)) {
            blockEntityValue = entity.getAttributeValue(EntityAttributes.KNOCKBACK_RESISTANCE);
         }

         double blockEntityBound = Math.max(Double.isFinite(blockEntityValue) ? blockEntityValue : 0.0, 0.1 * blockEntityCounter);
         return Math.max(0.0, Math.min(1.0, blockEntityBound));
      }
   }

   public static BlockEntityUtils.Support getOppositeImmutable(BlockPos pos, Vec3d vec, float oppositeImmutableValue, float clamp, double squaredFactor) {
      BlockEntityUtils.Support oppositeImmutable = null;
      double oppositeImmutableLimit = Double.POSITIVE_INFINITY;

      for (Direction direction : Direction.values()) {
         BlockPos offset = pos.offset(direction);
         Direction opposite = direction.getOpposite();
         if (canBlockState(offset, opposite)) {
            BlockHitResult amountsSquared = getAmountsSquared(offset, new Direction[]{opposite}, opposite, vec, oppositeImmutableValue, clamp, squaredFactor);
            if (amountsSquared != null) {
               float[] wrapDegrees = MathClampUtils.getWrapDegrees(vec, amountsSquared.getPos());
               double oppositeImmutableValue4 = BoxLengthUtils.getAmount(oppositeImmutableValue, clamp, wrapDegrees[0], wrapDegrees[1]);
               if (oppositeImmutableValue4 < oppositeImmutableLimit) {
                  oppositeImmutableLimit = oppositeImmutableValue4;
                  oppositeImmutable = new BlockEntityUtils.Support(offset.toImmutable(), opposite);
               }
            }
         }
      }

      return oppositeImmutable;
   }

   public static BlockHitResult getPosBlock(HitResult hitResult, BlockPos pos2, Direction direction, Vec3d vec, double posBlockFactor) {
      if (!(hitResult instanceof BlockHitResult blockHitResult && blockHitResult.getType() == Type.BLOCK)) {
         return null;
      } else if (!blockHitResult.getBlockPos().equals(pos2)) {
         return null;
      } else if (direction != null && blockHitResult.getSide() != direction) {
         return null;
      } else {
         return vec.squaredDistanceTo(blockHitResult.getPos()) > posBlockFactor * posBlockFactor ? null : blockHitResult;
      }
   }

   private static RegistryEntry<Enchantment> getOptionalEnchantment(DynamicRegistryManager dynamicRegistryManager, RegistryKey<Enchantment> registryKey) {
      Optional optional = dynamicRegistryManager.getOptional(RegistryKeys.ENCHANTMENT);
      if (optional.isEmpty()) {
         return null;
      } else {
         Optional optional2 = ((Registry)optional.get()).getOptional(registryKey);
         return optional2.isPresent() ? (RegistryEntry)optional2.get() : null;
      }
   }

   public static double[] getAmounts(double blockEntityValue, double y, double z, double blockEntityFactor, double blockEntityFactor2, boolean blockEntityFlag, double blockEntityLimit, int blockEntityLimit2) {
      double[] doubles = new double[]{blockEntityValue, y, z};
      double distance = Math.sqrt(blockEntityFactor * blockEntityFactor + blockEntityFactor2 * blockEntityFactor2);
      double blockEntityValue4 = distance > 1.0E-4 ? blockEntityFactor / distance : 0.0;
      double blockEntityValue5 = distance > 1.0E-4 ? blockEntityFactor2 / distance : 0.0;
      double blockEntityValue6 = blockEntityLimit > 0.0 ? Math.min(1.0, blockEntityLimit) : 0.0;
      handle(doubles, 0.4F, blockEntityValue6, blockEntityValue4, blockEntityValue5, blockEntityFlag);
      if (blockEntityLimit2 > 0) {
         handle(doubles, 0.5 * blockEntityLimit2, blockEntityValue6, blockEntityValue4, blockEntityValue5, blockEntityFlag);
      }

      return doubles;
   }

   public static float getLerpMin(Vec3d vec, Box box, Entity entity, BlockPos pos, BlockPos pos2) {
      double lerpDivisor = 1.0 / ((box.maxX - box.minX) * 2.0 + 1.0);
      double lerpValue = 1.0 / ((box.maxY - box.minY) * 2.0 + 1.0);
      double lerpDivisor2 = 1.0 / ((box.maxZ - box.minZ) * 2.0 + 1.0);
      if (!(lerpDivisor < 0.0) && !(lerpValue < 0.0) && !(lerpDivisor2 < 0.0)) {
         double lerpValue2 = (1.0 - Math.floor(1.0 / lerpDivisor) * lerpDivisor) / 2.0;
         double lerpValue3 = (1.0 - Math.floor(1.0 / lerpDivisor2) * lerpDivisor2) / 2.0;
         Box box3 = pos == null
            ? null
            : new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1.0, pos.getY() + 1.0 - 0.001, pos.getZ() + 1.0);
         int lerpCounter = 0;
         int lerpCounter2 = 0;

         for (double delta = 0.0; delta <= 1.0; delta += lerpDivisor) {
            for (double delta2 = 0.0; delta2 <= 1.0; delta2 += lerpValue) {
               for (double delta3 = 0.0; delta3 <= 1.0; delta3 += lerpDivisor2) {
                  Vec3d vec3d = new Vec3d(
                     MathHelper.lerp(delta, box.minX, box.maxX) + lerpValue2,
                     MathHelper.lerp(delta2, box.minY, box.maxY),
                     MathHelper.lerp(delta3, box.minZ, box.maxZ) + lerpValue3
                  );
                  BlockHitResult raycast = blockEntityMinecraftClient.world.raycast(new RaycastContext(vec3d, vec, ShapeType.COLLIDER, FluidHandling.NONE, entity));
                  boolean lerpFlag = raycast.getType() == Type.MISS || pos2 != null && raycast.getBlockPos().equals(pos2);
                  if (lerpFlag && box3 != null && box3.raycast(vec3d, vec).isPresent()) {
                     lerpFlag = false;
                  }

                  if (lerpFlag) {
                     lerpCounter++;
                  }

                  lerpCounter2++;
               }
            }
         }

         return lerpCounter2 == 0 ? 0.0F : (float)lerpCounter / lerpCounter2;
      } else {
         return 0.0F;
      }
   }

   public static boolean canRaycastVector(Vec3d vec, float raycastVectorValue, float yaw, Entity entity, double raycastVectorFactor) {
      if (entity != null && blockEntityMinecraftClient.player != null) {
         if (getMultiplySquared(vec, raycastVectorValue, yaw) instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() == entity) {
            Box boundingBox = entity.getBoundingBox();
            if (boundingBox.contains(vec)) {
               return true;
            } else {
               Vec3d add = vec.add(blockEntityMinecraftClient.player.getRotationVector(yaw, raycastVectorValue).multiply(raycastVectorFactor));
               Optional raycast = boundingBox.raycast(vec, add);
               return raycast.isPresent() && vec.squaredDistanceTo((Vec3d)raycast.get()) < raycastVectorFactor * raycastVectorFactor && canSubtractRaycast(vec, (Vec3d)raycast.get());
            }
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public static BlockEntityUtils.Predicted getInterpolatorInstance(LivingEntity entity, int interpolatorLimit, boolean interpolatorFlag, boolean interpolatorFlag2) {
      Vec3d entityPos = entity.getEntityPos();
      double x = entityPos.x - entity.lastX;
      double y = entityPos.y - entity.lastY;
      double z = entityPos.z - entity.lastZ;
      if (x * x + y * y + z * z > 4.0) {
         x = 0.0;
         y = 0.0;
         z = 0.0;
      }

      if (interpolatorFlag && blockEntityMinecraftClient.player != null) {
         double deltaX = entityPos.x - blockEntityMinecraftClient.player.getX();
         double deltaZ = entityPos.z - blockEntityMinecraftClient.player.getZ();
         double distance = Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
         double interpolatorValue;
         double interpolatorValue2;
         if (distance > 1.0E-4) {
            interpolatorValue = deltaX / distance;
            interpolatorValue2 = deltaZ / distance;
         } else {
            interpolatorValue = 0.0;
            interpolatorValue2 = 0.0;
         }

         double[] interpolatorValues = getAmounts(x, y, z, interpolatorValue, interpolatorValue2, entity.isOnGround(), getMax(entity), 1);
         x = interpolatorValues[0];
         y = interpolatorValues[1];
         z = interpolatorValues[2];
      }

      Box boundingBox = entity.getBoundingBox();
      if (interpolatorFlag2) {
         Vec3d vec3d = entity.getInterpolator() != null ? entity.getInterpolator().getLerpedPos() : entityPos;
         boundingBox = boundingBox.offset(vec3d.subtract(entityPos));
      }

      for (int i = 0; i < interpolatorLimit; i++) {
         Vec3d vec3d2 = new Vec3d(x, y, z);
         Vec3d vec3d3 = Entity.adjustMovementForCollisions(entity, vec3d2, boundingBox, blockEntityMinecraftClient.world, List.of());
         boundingBox = boundingBox.offset(vec3d3);
         boolean interpolatorFlag3 = Math.abs(vec3d3.y - vec3d2.y) > 1.0E-6;
         boolean interpolatorFlag4 = interpolatorFlag3 && vec3d2.y < 0.0;
         if (Math.abs(vec3d3.x - vec3d2.x) > 1.0E-6) {
            x = 0.0;
         }

         if (Math.abs(vec3d3.z - vec3d2.z) > 1.0E-6) {
            z = 0.0;
         }

         if (interpolatorFlag3) {
            y = 0.0;
         }

         y = (y - 0.08) * 0.98;
         double interpolatorValue3 = interpolatorFlag4 ? 0.546 : 0.91;
         x *= interpolatorValue3;
         z *= interpolatorValue3;
      }

      Vec3d vec3d4 = new Vec3d((boundingBox.minX + boundingBox.maxX) / 2.0, boundingBox.minY, (boundingBox.minZ + boundingBox.maxZ) / 2.0);
      return new BlockEntityUtils.Predicted(vec3d4, boundingBox);
   }

   public static float getDistance(Vec3d vec, Vec3d vec2, Box box, Entity entity, BlockPos pos, BlockPos pos2, float distanceValue) {
      double distanceTo = vec2.distanceTo(vec);
      return distanceTo > distanceValue * 2.0 ? 0.0F : (float)BoxLengthUtils.getAmount3(distanceTo, getLerpMin(vec, box, entity, pos, pos2), distanceValue);
   }

   public static Vec3d getRaycastVector(Box box, Vec3d vec, float raycastVectorValue, float raycastVectorValue2, double raycastVectorValue3) {
      if (box != null && vec != null && blockEntityMinecraftClient.player != null) {
         Vec3d add = vec.add(blockEntityMinecraftClient.player.getRotationVector(raycastVectorValue2, raycastVectorValue).multiply(raycastVectorValue3));
         if (box.contains(vec)) {
            return add;
         } else {
            Optional raycast = box.raycast(vec, add);
            return raycast.isPresent() && canSubtractRaycast(vec, (Vec3d)raycast.get()) ? (Vec3d)raycast.get() : null;
         }
      } else {
         return null;
      }
   }

   public static float getRegistryEquipped(LivingEntity livingEntity) {
      updateRegistryProtection();
      if (optionalEnchantment == null) {
         return 0.0F;
      } else {
         int registryEquipped = 0;

         for (EquipmentSlot equipmentSlot : equipmentSlots) {
            ItemStack equippedStack = livingEntity.getEquippedStack(equipmentSlot);
            if (!equippedStack.isEmpty()) {
               registryEquipped += EnchantmentHelper.getLevel(optionalEnchantment, equippedStack);
            }
         }

         return registryEquipped;
      }
   }

   static boolean canFlag(Block block) {
      return block instanceof CraftingTableBlock
         || block instanceof NoteBlock
         || block instanceof RespawnAnchorBlock
         || block instanceof CartographyTableBlock
         || block instanceof LoomBlock
         || block instanceof TrapdoorBlock
         || block instanceof DoorBlock
         || block instanceof FenceGateBlock
         || block instanceof ButtonBlock
         || block instanceof LeverBlock
         || block instanceof AnvilBlock
         || block instanceof GrindstoneBlock
         || block instanceof StonecutterBlock
         || block instanceof AbstractRedstoneGateBlock
         || block instanceof CakeBlock
         || block instanceof ComposterBlock
         || block instanceof DragonEggBlock;
   }

   public static int getDifficultyInstance(LivingEntity entity) {
      return entity instanceof PlayerEntity && blockEntityMinecraftClient.world != null ? blockEntityMinecraftClient.world.getDifficulty().getId() : -1;
   }

   public static boolean canRaycastVector(Box box, Vec3d vec, float raycastVectorValue, float raycastVectorValue2, double raycastVectorValue3) {
      if (box.contains(vec)) {
         return true;
      } else {
         Vec3d add = vec.add(blockEntityMinecraftClient.player.getRotationVector(raycastVectorValue2, raycastVectorValue).multiply(raycastVectorValue3));
         Optional raycast = box.raycast(vec, add);
         return raycast.isPresent() && canSubtractRaycast(vec, (Vec3d)raycast.get());
      }
   }

   static Vec3d getVec3d(BlockPos pos2, Direction direction, double vec3dValue, double vec3dValue2) {
      double x = pos2.getX();
      double y = pos2.getY();
      double z = pos2.getZ();

      return switch (direction) {
         case UP -> new Vec3d(x + vec3dValue, y + 1.0, z + vec3dValue2);
         case DOWN -> new Vec3d(x + vec3dValue, y, z + vec3dValue2);
         case NORTH -> new Vec3d(x + vec3dValue, y + vec3dValue2, z);
         case SOUTH -> new Vec3d(x + vec3dValue, y + vec3dValue2, z + 1.0);
         case WEST -> new Vec3d(x, y + vec3dValue2, z + vec3dValue);
         case EAST -> new Vec3d(x + 1.0, y + vec3dValue2, z + vec3dValue);
      };
   }

   public static float getRegistryLevel(LivingEntity livingEntity) {
      updateRegistryProtection();
      int registryLevel = 0;

      for (EquipmentSlot equipmentSlot : equipmentSlots) {
         ItemStack equippedStack = livingEntity.getEquippedStack(equipmentSlot);
         if (!equippedStack.isEmpty()) {
            if (optionalEnchantment != null) {
               registryLevel += EnchantmentHelper.getLevel(optionalEnchantment, equippedStack);
            }

            if (registryEntry != null) {
               registryLevel += 2 * EnchantmentHelper.getLevel(registryEntry, equippedStack);
            }
         }
      }

      return registryLevel;
   }

   private static Vec3d getLerpMin(Box box, Direction direction, double delta, double delta2) {
      double lerp = MathHelper.lerp(delta, box.minX, box.maxX);
      double lerp2 = MathHelper.lerp(delta2, box.minZ, box.maxZ);
      double lerp3 = MathHelper.lerp(delta2, box.minY, box.maxY);
      double lerp4 = MathHelper.lerp(delta, box.minZ, box.maxZ);

      return switch (direction) {
         case UP -> new Vec3d(lerp, box.maxY, lerp2);
         case DOWN -> new Vec3d(lerp, box.minY, lerp2);
         case NORTH -> new Vec3d(lerp, lerp3, box.minZ);
         case SOUTH -> new Vec3d(lerp, lerp3, box.maxZ);
         case WEST -> new Vec3d(box.minX, lerp3, lerp4);
         case EAST -> new Vec3d(box.maxX, lerp3, lerp4);
      };
   }

   public static Vec3d getPointVector(Box box, Entity entity, Vec3d vec, float pointVectorValue, float vec3dValue2, double pointVectorFactor, double pointVectorLimit) {
      if (box.contains(vec)) {
         return vec.add(blockEntityMinecraftClient.player.getRotationVector(vec3dValue2, pointVectorValue));
      } else {
         ArrayList arrayList = new ArrayList();

         for (Direction direction : Direction.values()) {
            if (canMinMax(vec, box, direction)) {
               for (double pointVectorTableItem : pointVectorTable) {
                  for (double pointVectorValue4 : pointVectorTable) {
                     Vec3d lerpMin = getLerpMin(box, direction, pointVectorTableItem, pointVectorValue4);
                     if (!(vec.squaredDistanceTo(lerpMin) > pointVectorFactor * pointVectorFactor)) {
                        arrayList.add(getVecDataForVec3dFloat(vec, lerpMin, pointVectorValue, vec3dValue2));
                     }
                  }
               }
            }
         }

         arrayList.sort(Comparator.comparingDouble(BlockEntityUtils.Sample::angle));
         int pointVectorCounter = 0;

         for (BlockEntityUtils.Sample sample : (Iterable<com.corz.client.inventory.BlockEntityUtils.Sample>) (arrayList)) {
            if (entity == null
               || !(pointVectorLimit > 0.0)
               || BoxLengthUtils.canLengthContract(box, vec, getVectorMultiply(vec, sample.yaw(), sample.pitch(), pointVectorFactor), pointVectorLimit)) {
               if (pointVectorCounter++ >= 16) {
                  break;
               }

               if (entity != null) {
                  if (getMultiplySquared(vec, sample.yaw(), sample.pitch()) instanceof EntityHitResult entityHitResult && entityHitResult.getEntity() == entity) {
                     return sample.point();
                  }
               } else if (canSubtractRaycast(vec, sample.point())) {
                  return sample.point();
               }
            }
         }

         return null;
      }
   }

   public static BlockPos getDown(EndCrystalEntity endCrystalEntity) {
      return endCrystalEntity.getBlockPos().down();
   }

   public record Predicted(Vec3d feet, Box box) {
   }

   private record Sample(Vec3d point, float yaw, float pitch, double angle) {
   }

   public record Support(BlockPos pos, Direction face) {
   }
}
