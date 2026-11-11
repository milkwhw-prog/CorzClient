package com.corz.client.inventory;

import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.FoodComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import com.corz.client.utils.AmountEnchantmentsUtils;

public final class StackEnchantmentsUtils {
   private static final List<RegistryKey<Enchantment>> DAMAGE_BONUS_ENCHANTS = List.of(
      Enchantments.SHARPNESS,
      Enchantments.KNOCKBACK,
      Enchantments.SWEEPING_EDGE,
      Enchantments.DENSITY,
      Enchantments.BREACH,
      Enchantments.WIND_BURST,
      Enchantments.LOYALTY,
      Enchantments.RIPTIDE,
      Enchantments.CHANNELING,
      Enchantments.IMPALING,
      Enchantments.LUNGE
   );
   private static final List<RegistryKey<Enchantment>> MINING_TOOL_ENCHANTS = List.of(Enchantments.EFFICIENCY, Enchantments.FORTUNE, Enchantments.SILK_TOUCH);
   private static final List<RegistryKey<Enchantment>> UNBREAKING_ENCHANTS = List.of(
      Enchantments.POWER, Enchantments.PUNCH, Enchantments.FLAME, Enchantments.INFINITY, Enchantments.MENDING, Enchantments.UNBREAKING
   );
   private static final List<RegistryKey<Enchantment>> MENDING_ENCHANTS = List.of(
      Enchantments.QUICK_CHARGE, Enchantments.MULTISHOT, Enchantments.PIERCING, Enchantments.MENDING, Enchantments.UNBREAKING
   );
   private static final List<RegistryKey<Enchantment>> SPECIAL_PROTECTION_ENCHANTS = List.of(
      Enchantments.PROTECTION, Enchantments.FIRE_PROTECTION, Enchantments.BLAST_PROTECTION, Enchantments.PROJECTILE_PROTECTION, Enchantments.FEATHER_FALLING
   );

   public static double getPotionEffects(ItemStack stack) {
      PotionContentsComponent potionContentsComponent = stack.get(DataComponentTypes.POTION_CONTENTS);
      if (potionContentsComponent == null) {
         return 0.0;
      } else {
         int potionEffectsBound = -1;

         for (StatusEffectInstance statusEffectInstance : potionContentsComponent.getEffects()) {
            if (canMatches(statusEffectInstance.getEffectType(), StatusEffects.INSTANT_HEALTH)) {
               potionEffectsBound = Math.max(potionEffectsBound, statusEffectInstance.getAmplifier());
            }
         }

         if (potionEffectsBound < 0) {
            return 0.0;
         } else {
            double potionEffectsValue = stack.isOf(Items.SPLASH_POTION) ? 20.0 : (stack.isOf(Items.LINGERING_POTION) ? 10.0 : 0.0);
            return 1.0 + potionEffectsValue + potionEffectsBound;
         }
      }
   }

   public static boolean canFlag(ItemStack stack) {
      return getPotionEffects(stack) > 0.0;
   }

   public static double getUnbreakingMending(ItemStack stack, StackChestplateType stackChestplateType) {
      if (stack != null && !stack.isEmpty() && stackChestplateType != null) {
         double damageDamageable = getDamageDamageable(stack);

         return switch (stackChestplateType) {
            case SWORD, AXE -> getSweepingMainhand(stack, 0.0, damageDamageable);
            case SPEAR -> getSweepingMainhand(stack, 0.3 * getEnchantmentEnchantments(stack, Enchantments.LUNGE), damageDamageable);
            case MACE -> getSweepingMainhand(
               stack,
               0.5 * getEnchantmentEnchantments(stack, Enchantments.DENSITY)
                  + 0.6 * getEnchantmentEnchantments(stack, Enchantments.BREACH)
                  + 0.3 * getEnchantmentEnchantments(stack, Enchantments.WIND_BURST),
               damageDamageable
            );
            case TRIDENT -> getSweepingMainhand(
               stack,
               0.3 * getEnchantmentEnchantments(stack, Enchantments.LOYALTY)
                  + 0.3 * getEnchantmentEnchantments(stack, Enchantments.RIPTIDE)
                  + 0.2 * getEnchantmentEnchantments(stack, Enchantments.CHANNELING)
                  + 0.05 * getEnchantmentEnchantments(stack, Enchantments.IMPALING),
               damageDamageable
            );
            case PICKAXE -> getMultiplierMining(stack, Blocks.STONE, getSuitableDefault(stack), damageDamageable);
            case SHOVEL -> getMultiplierMining(stack, Blocks.DIRT, 0, damageDamageable);
            case BOW -> AmountEnchantmentsUtils.getAmount(
               getEnchantmentEnchantments(stack, Enchantments.POWER),
               getEnchantmentEnchantments(stack, Enchantments.PUNCH),
               getEnchantmentEnchantments(stack, Enchantments.FLAME),
               getEnchantmentEnchantments(stack, Enchantments.INFINITY),
               getEnchantmentEnchantments(stack, Enchantments.MENDING),
               getEnchantmentEnchantments(stack, Enchantments.UNBREAKING),
               getEnchantmentEnchantments(stack, UNBREAKING_ENCHANTS),
               damageDamageable
            );
            case CROSSBOW -> AmountEnchantmentsUtils.getAmount(
               getEnchantmentEnchantments(stack, Enchantments.QUICK_CHARGE),
               getEnchantmentEnchantments(stack, Enchantments.MULTISHOT),
               getEnchantmentEnchantments(stack, Enchantments.PIERCING),
               getEnchantmentEnchantments(stack, Enchantments.MENDING),
               getEnchantmentEnchantments(stack, Enchantments.UNBREAKING),
               getEnchantmentEnchantments(stack, MENDING_ENCHANTS),
               damageDamageable
            );
            case HELMET, CHESTPLATE, LEGGINGS, BOOTS -> getProtectionArmor(stack, stackChestplateType.getChest(), damageDamageable);
         };
      } else {
         return 0.0;
      }
   }

   public static double getAmount(ItemStack stack) {
      return AmountEnchantmentsUtils.getAmount(getEnchantmentEnchantments(stack, List.of()), getDamageDamageable(stack));
   }

   private StackEnchantmentsUtils() {
   }

   public static double getMultiplierDamage(ItemStack stack, StackChestplateType stackChestplateType) {
      if (stack != null && !stack.isEmpty() && stackChestplateType != null) {
         return switch (stackChestplateType) {
            case SWORD, AXE, SPEAR, MACE, TRIDENT -> getModifiersAttribute(stack, EquipmentSlot.MAINHAND, EntityAttributes.ATTACK_DAMAGE);
            case PICKAXE -> AmountEnchantmentsUtils.getAmount(
               getSuitableDefault(stack), (double)stack.getMiningSpeedMultiplier(Blocks.STONE.getDefaultState()), stack.getMaxDamage()
            );
            case SHOVEL -> AmountEnchantmentsUtils.getAmount(0, (double)stack.getMiningSpeedMultiplier(Blocks.DIRT.getDefaultState()), stack.getMaxDamage());
            case BOW, CROSSBOW -> 0.0;
            case HELMET, CHESTPLATE, LEGGINGS, BOOTS -> AmountEnchantmentsUtils.getAmount(
               getModifiersAttribute(stack, stackChestplateType.getChest(), EntityAttributes.ARMOR),
               getModifiersAttribute(stack, stackChestplateType.getChest(), EntityAttributes.ARMOR_TOUGHNESS),
               getModifiersAttribute(stack, stackChestplateType.getChest(), EntityAttributes.KNOCKBACK_RESISTANCE)
            );
         };
      } else {
         return 0.0;
      }
   }

   static int getSuitableDefault(ItemStack stack) {
      if (stack.isSuitableFor(Blocks.OBSIDIAN.getDefaultState())) {
         return 3;
      } else if (stack.isSuitableFor(Blocks.DIAMOND_ORE.getDefaultState())) {
         return 2;
      } else {
         return stack.isSuitableFor(Blocks.IRON_ORE.getDefaultState()) ? 1 : 0;
      }
   }

   public static double getSaturationNutrition(ItemStack stack) {
      FoodComponent foodComponent = stack.get(DataComponentTypes.FOOD);
      return foodComponent == null ? 0.0 : AmountEnchantmentsUtils.getAmount(foodComponent.nutrition(), foodComponent.saturation());
   }

   public static Map<String, Integer> getEnchantmentEnchantments(ItemStack stack, boolean enchantmentEnchantmentsFlag) {
      HashMap<String, Integer> hashMap = null;

      for (Entry entry : stack.getEnchantments().getEnchantmentEntries()) {
         if (((RegistryEntry)entry.getKey()).isIn(EnchantmentTags.CURSE) == enchantmentEnchantmentsFlag && entry.getIntValue() > 0) {
            if (hashMap == null) {
               hashMap = new HashMap<>();
            }

            hashMap.merge(((RegistryEntry)entry.getKey()).getIdAsString(), entry.getIntValue(), Math::max);
         }
      }

      return (Map<String, Integer>)(hashMap == null ? Map.of() : hashMap);
   }

   static <T> boolean canMatches(RegistryEntry<T> registryEntry, RegistryEntry<T> registryEntry2) {
      if (registryEntry == registryEntry2) {
         return true;
      } else {
         var optional = registryEntry2.getKey();
         return optional.isPresent() && registryEntry.matchesKey((RegistryKey<T>)optional.get());
      }
   }

   static double getModifiersAttribute(ItemStack stack, EquipmentSlot equipmentSlot, RegistryEntry<EntityAttribute> registryEntry) {
      double[] doubles = new double[1];
      stack.applyAttributeModifiers(equipmentSlot, (attributex, modifier) -> {
         if (canMatches(attributex, registryEntry) && modifier.operation() == Operation.ADD_VALUE) {
            doubles[0] += modifier.value();
         }
      });
      return doubles[0];
   }

   static int getEnchantmentEnchantments(ItemStack stack, RegistryKey<Enchantment> registryKey) {
      for (Entry entry : stack.getEnchantments().getEnchantmentEntries()) {
         if (((RegistryEntry)entry.getKey()).matchesKey(registryKey)) {
            return entry.getIntValue();
         }
      }

      return 0;
   }

   public static double getDamageDamageable(ItemStack stack) {
      return stack != null && !stack.isEmpty() && stack.isDamageable() ? AmountEnchantmentsUtils.getAmount(stack.getDamage(), stack.getMaxDamage()) : 1.0;
   }

   static double getMultiplierMining(ItemStack stack, Block block, int multiplierMiningValue, double damageDamageable) {
      float miningSpeedMultiplier = stack.getMiningSpeedMultiplier(block.getDefaultState());
      double multiplierMiningValue3 = 0.3 * getEnchantmentEnchantments(stack, Enchantments.FORTUNE) + 0.6 * getEnchantmentEnchantments(stack, Enchantments.SILK_TOUCH);
      return AmountEnchantmentsUtils.getAmount(
         multiplierMiningValue, miningSpeedMultiplier, getEnchantmentEnchantments(stack, Enchantments.EFFICIENCY), stack.getMaxDamage(), multiplierMiningValue3, getEnchantmentEnchantments(stack, MINING_TOOL_ENCHANTS), damageDamageable
      );
   }

   static double getProtectionArmor(ItemStack stack, EquipmentSlot equipmentSlot, double protectionArmorValue) {
      double modifiersAttribute = getModifiersAttribute(stack, equipmentSlot, EntityAttributes.ARMOR);
      double modifiersAttribute2 = getModifiersAttribute(stack, equipmentSlot, EntityAttributes.ARMOR_TOUGHNESS);
      double modifiersAttribute3 = getModifiersAttribute(stack, equipmentSlot, EntityAttributes.KNOCKBACK_RESISTANCE);
      int protectionArmorValue2 = getEnchantmentEnchantments(stack, Enchantments.FIRE_PROTECTION)
         + getEnchantmentEnchantments(stack, Enchantments.BLAST_PROTECTION)
         + getEnchantmentEnchantments(stack, Enchantments.PROJECTILE_PROTECTION)
         + getEnchantmentEnchantments(stack, Enchantments.FEATHER_FALLING);
      return AmountEnchantmentsUtils.getAmount(
         modifiersAttribute, modifiersAttribute2, modifiersAttribute3, getEnchantmentEnchantments(stack, Enchantments.PROTECTION), protectionArmorValue2, getEnchantmentEnchantments(stack, SPECIAL_PROTECTION_ENCHANTS), protectionArmorValue
      );
   }

   static int getEnchantmentEnchantments(ItemStack stack, List<RegistryKey<Enchantment>> list) {
      int enchantmentEnchantments = 0;

      for (Entry entry : stack.getEnchantments().getEnchantmentEntries()) {
         var registryEntry = (RegistryEntry)entry.getKey();
         if (!registryEntry.isIn(EnchantmentTags.CURSE)) {
            boolean enchantmentEnchantmentsFlag = false;

            for (RegistryKey registryKey : list) {
               if (registryEntry.matchesKey(registryKey)) {
                  enchantmentEnchantmentsFlag = true;
                  break;
               }
            }

            if (!enchantmentEnchantmentsFlag) {
               enchantmentEnchantments += entry.getIntValue();
            }
         }
      }

      return enchantmentEnchantments;
   }

   static double getSweepingMainhand(ItemStack stack, double sweepingMainhandValue, double damageDamageable) {
      double modifiersAttribute = getModifiersAttribute(stack, EquipmentSlot.MAINHAND, EntityAttributes.ATTACK_DAMAGE);
      double sweepingMainhandValue3 = sweepingMainhandValue
         + 0.05 * (getEnchantmentEnchantments(stack, Enchantments.KNOCKBACK) + getEnchantmentEnchantments(stack, Enchantments.SWEEPING_EDGE));
      return AmountEnchantmentsUtils.getAmount(
         modifiersAttribute, getEnchantmentEnchantments(stack, Enchantments.SHARPNESS), sweepingMainhandValue3, getEnchantmentEnchantments(stack, DAMAGE_BONUS_ENCHANTS), damageDamageable
      );
   }
}
