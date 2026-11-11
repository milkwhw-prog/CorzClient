package com.corz.client.utils;

import com.corz.client.inventory.InventoryStackUtilsEmpty;
import java.util.Optional;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public final class SwordAttributeAndHazardUtils {
   public static final double[] STEP_PROBE_DISTANCES = new double[]{0.45, 1.0};

   private SwordAttributeAndHazardUtils() {
   }

   static boolean hasCollision(World world, BlockPos pos2) {
      BlockState blockState = world.getBlockState(pos2);
      return !blockState.getCollisionShape(world, pos2).isEmpty();
   }

   public static boolean isStepDirectionSafe(World world, Vec3d vec, double downValue, double deltaZ) {
      if (world != null && vec != null) {
         double hypot = Math.hypot(downValue, deltaZ);
         if (!(hypot > 1.0E-6)) {
            return true;
         } else {
            double downFactor = downValue / hypot;
            double downFactor2 = deltaZ / hypot;
            int y = (int)Math.floor(vec.y + 0.01);

            for (double stepProbeDistancesItem : STEP_PROBE_DISTANCES) {
               int x = (int)Math.floor(vec.x + downFactor * stepProbeDistancesItem);
               int z = (int)Math.floor(vec.z + downFactor2 * stepProbeDistancesItem);
               if (isHazardBlock(world, new BlockPos(x, y, z)) || isHazardBlock(world, new BlockPos(x, y + 1, z))) {
                  return false;
               }

               BlockPos blockPos = new BlockPos(x, y, z);
               if (!hasCollision(world, blockPos)) {
                  boolean downFlag = false;
                  int downCounter = 1;

                  while (true) {
                     if (downCounter <= 4) {
                        BlockPos down = blockPos.down(downCounter);
                        if (isHazardBlock(world, down) || isLava(world.getBlockState(down))) {
                           return false;
                        }

                        if (!hasCollision(world, down)) {
                           downCounter++;
                           continue;
                        }

                        downFlag = true;
                     }

                     if (!downFlag) {
                        return false;
                     }
                     break;
                  }
               }
            }

            return true;
         }
      } else {
         return true;
      }
   }

   public static boolean isSwordWithDamageAtLeast(ItemStack stack, double blockAttributeValue) {
      return isSword(stack) && getSwordDamage(stack) >= blockAttributeValue - 1.0E-6;
   }

   public static double getSwordDamage(ItemStack stack) {
      if (!isSword(stack)) {
         return Double.NEGATIVE_INFINITY;
      } else {
         double[] doubles = new double[1];
         stack.applyAttributeModifiers(EquipmentSlot.MAINHAND, (attr, sum) -> {
            if (isSameAttribute(attr, EntityAttributes.ATTACK_DAMAGE) && sum.operation() == Operation.ADD_VALUE) {
               doubles[0] += sum.value();
            }
         });
         return 1.0 + doubles[0] + getSharpnessBonus(InventoryStackUtilsEmpty.getEnchantmentsDefault(stack, Enchantments.SHARPNESS));
      }
   }

   static boolean isLava(BlockState state) {
      return state != null && state.getFluidState().isIn(FluidTags.LAVA);
   }

   public static double getSharpnessBonus(int blockAttributeLimit) {
      return blockAttributeLimit > 0 ? 0.5 * blockAttributeLimit + 0.5 : 0.0;
   }

   public static double getMaxValue(double[] blockAttributeValues) {
      double blockAttributeValue = Double.NEGATIVE_INFINITY;
      if (blockAttributeValues != null) {
         for (double blockAttributeLimit : blockAttributeValues) {
            if (blockAttributeLimit > blockAttributeValue) {
               blockAttributeValue = blockAttributeLimit;
            }
         }
      }

      return blockAttributeValue;
   }

   public static boolean isSword(ItemStack stack) {
      return InventoryStackUtilsEmpty.canSwordsEmpty(stack);
   }

   static boolean isHazardBlock(World world, BlockPos pos2) {
      BlockState blockState = world.getBlockState(pos2);
      return isLava(blockState)
         ? true
         : blockState.isOf(Blocks.FIRE)
            || blockState.isOf(Blocks.SOUL_FIRE)
            || blockState.isOf(Blocks.MAGMA_BLOCK)
            || blockState.isOf(Blocks.CACTUS)
            || blockState.isOf(Blocks.SWEET_BERRY_BUSH)
            || blockState.isOf(Blocks.POWDER_SNOW)
            || blockState.isOf(Blocks.COBWEB)
            || blockState.isOf(Blocks.WITHER_ROSE)
            || blockState.isOf(Blocks.CAMPFIRE)
            || blockState.isOf(Blocks.SOUL_CAMPFIRE);
   }

   public static double getAttackSpeed(ItemStack stack) {
      if (stack != null && !stack.isEmpty()) {
         double[] doubles = new double[1];
         stack.applyAttributeModifiers(EquipmentSlot.MAINHAND, (attr, mod) -> {
            if (isSameAttribute(attr, EntityAttributes.ATTACK_SPEED) && mod.operation() == Operation.ADD_VALUE) {
               doubles[0] += mod.value();
            }
         });
         double modifiersAttributeLimit = 4.0 + doubles[0];
         return modifiersAttributeLimit > 0.0 ? modifiersAttributeLimit : 4.0;
      } else {
         return 4.0;
      }
   }

   private static boolean isSameAttribute(RegistryEntry<EntityAttribute> registryEntry, RegistryEntry<EntityAttribute> registryEntry2) {
      if (registryEntry == registryEntry2) {
         return true;
      } else {
         Optional optional = registryEntry2.getKey();
         return optional.isPresent() && registryEntry.matchesKey((RegistryKey)optional.get());
      }
   }
}
