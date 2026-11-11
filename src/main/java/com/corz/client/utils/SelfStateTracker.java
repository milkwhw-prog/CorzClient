package com.corz.client.utils;

import com.corz.client.inventory.BlockEntityUtils;
import com.corz.client.inventory.StackEnchantmentsUtils;
import com.corz.client.world.AbsorptionPosData;
import com.corz.client.world.BlockStateUtilsDirections;
import com.corz.client.world.PosBlockUtilsBonus;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.ServerPacketEventQueue;
import com.corz.client.categories.combat.AutoInventoryTotem;
import com.corz.client.utils.EntityDamageUtils;

public final class SelfStateTracker {
   private int recentPopCount;
   private long lastPopTick = Long.MIN_VALUE;
   private float explosionRadius;
   private Vec3d lastExplosionCenter;
   private long lastFreshTick = Long.MIN_VALUE;
   public static final int UNIT_COUNT = 1;
   private long lastBoomTick = Long.MIN_VALUE;
   private float lastExplosionDamage;
   private AbsorptionPosData lastSnapshot;
   private static final EquipmentSlot[] ARMOR_SLOTS = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};

   public void onPacketEvent(ServerPacketEventQueue.Event event, long time, int centerValue) {
      if (event instanceof ServerPacketEventQueue.Pop pop) {
         if (pop.entityId() != centerValue) {
            return;
         }

         this.recentPopCount++;
         this.lastPopTick = time;
      } else if (event instanceof ServerPacketEventQueue.EntityDamage fresh) {
         if (fresh.entityId() != centerValue) {
            return;
         }

         this.lastFreshTick = time;
         this.lastExplosionDamage = 0.0F;
      } else if (event instanceof ServerPacketEventQueue.Boom boom) {
         if (boom.center() == null) {
            return;
         }

         this.lastBoomTick = time;
         this.lastExplosionCenter = boom.center();
         this.explosionRadius = boom.radius();
      }
   }

   public void reset() {
      this.recentPopCount = 0;
      this.lastPopTick = Long.MIN_VALUE;
      this.lastExplosionDamage = 0.0F;
      this.lastFreshTick = Long.MIN_VALUE;
      this.lastBoomTick = Long.MIN_VALUE;
      this.lastExplosionCenter = null;
      this.explosionRadius = 0.0F;
      this.lastSnapshot = null;
   }

   public AbsorptionPosData buildSnapshot(long absorptionEmptyValue) {
      MinecraftClient instance = MinecraftClient.getInstance();
      ClientPlayerEntity player = instance.player;
      ClientWorld world = instance.world;
      if (player != null && world != null) {
         Vec3d entityPos = player.getEntityPos();
         Box boundingBox = player.getBoundingBox();
         this.updateExplosionDamage(absorptionEmptyValue, player, entityPos, boundingBox);
         float absorptionEmptyBound = 1.0F;

         for (EquipmentSlot equipmentSlot : ARMOR_SLOTS) {
            ItemStack equippedStack = player.getEquippedStack(equipmentSlot);
            float absorptionEmptyBound2 = equippedStack.isEmpty() ? 0.0F : (float)StackEnchantmentsUtils.getDamageDamageable(equippedStack);
            absorptionEmptyBound = Math.min(absorptionEmptyBound, absorptionEmptyBound2);
         }

         PlayerInventory inventory = player.getInventory();
         int absorptionEmptyTotal = 0;
         int absorptionEmptyTotal2 = 0;
         int absorptionEmptyTotal3 = 0;
         int absorptionEmptyTotal4 = 0;
         int absorptionEmptyTotal5 = 0;
         int absorptionEmptyTotal6 = 0;
         int absorptionEmptyTotal7 = 0;
         int absorptionEmptyTotal8 = 0;
         int absorptionEmptyTotal9 = 0;
         ItemStack itemStack = ItemStack.EMPTY;

         for (int i = 0; i <= 40; i++) {
            if (i < 36 || i >= 40) {
               ItemStack stack = inventory.getStack(i);
               if (!stack.isEmpty()) {
                  int absorptionEmptyValue2 = stack.getCount();
                  if (stack.isOf(Items.TOTEM_OF_UNDYING)) {
                     absorptionEmptyTotal += absorptionEmptyValue2;
                  } else if (stack.isOf(Items.END_CRYSTAL)) {
                     absorptionEmptyTotal2 += absorptionEmptyValue2;
                  } else if (stack.isOf(Items.OBSIDIAN)) {
                     absorptionEmptyTotal3 += absorptionEmptyValue2;
                  } else if (stack.isOf(Items.RESPAWN_ANCHOR)) {
                     absorptionEmptyTotal4 += absorptionEmptyValue2;
                  } else if (stack.isOf(Items.GLOWSTONE)) {
                     absorptionEmptyTotal5 += absorptionEmptyValue2;
                  } else if (stack.isOf(Items.GOLDEN_APPLE) || stack.isOf(Items.ENCHANTED_GOLDEN_APPLE)) {
                     absorptionEmptyTotal6 += absorptionEmptyValue2;
                  } else if (stack.isOf(Items.ENDER_PEARL)) {
                     absorptionEmptyTotal8 += absorptionEmptyValue2;
                     if (itemStack.isEmpty()) {
                        itemStack = stack;
                     }
                  } else if (stack.isOf(Items.EXPERIENCE_BOTTLE)) {
                     absorptionEmptyTotal9 += absorptionEmptyValue2;
                  } else if (stack.contains(DataComponentTypes.FOOD)) {
                     absorptionEmptyTotal7 += absorptionEmptyValue2;
                  }
               }
            }
         }

         ItemStack offHandStack = player.getOffHandStack();
         boolean of = offHandStack.isOf(Items.TOTEM_OF_UNDYING);
         boolean absorptionEmptyFlag = of && !AutoInventoryTotem.isSlotInPendingSwap(40);
         boolean absorptionEmptyFlag2 = !itemStack.isEmpty() && !player.getItemCooldownManager().isCoolingDown(itemStack);
         PosBlockUtilsBonus.Armor attributeArmor = BlockEntityUtils.getAttributeArmor(player);
         BlockStateUtilsDirections.Hole bedrockState = BlockStateUtilsDirections.getBedrockState(world, BlockPos.ofFloored(entityPos));
         this.lastSnapshot = new AbsorptionPosData(
            entityPos,
            player.getEyePos(),
            boundingBox,
            player.getVelocity(),
            player.isOnGround(),
            player.getHealth(),
            player.getAbsorptionAmount(),
            attributeArmor,
            absorptionEmptyBound,
            of,
            absorptionEmptyFlag,
            absorptionEmptyTotal,
            absorptionEmptyTotal2,
            absorptionEmptyTotal3,
            absorptionEmptyTotal4,
            absorptionEmptyTotal5,
            absorptionEmptyTotal6,
            absorptionEmptyTotal7,
            absorptionEmptyTotal8,
            absorptionEmptyTotal9,
            absorptionEmptyFlag2,
            player.getAttackCooldownProgress(0.5F),
            player.getHungerManager().getFoodLevel(),
            EntityDamageUtils.getMin(player.getId()),
            this.lastExplosionDamage,
            this.recentPopCount,
            this.lastPopTick,
            player.isUsingItem(),
            AutoInventoryTotem.isHandSwapSlotActive(),
            ExtUtils.isActionFlag2(),
            bedrockState,
            EntityDamageUtils.getCachedHurtTimeLimit(),
            EntityDamageUtils.getTime()
         );
         return this.lastSnapshot;
      } else {
         return this.lastSnapshot;
      }
   }

   public AbsorptionPosData getLastSnapshot() {
      return this.lastSnapshot;
   }

   private void updateExplosionDamage(long vecValue, ClientPlayerEntity player, Vec3d vec, Box box) {
      if (this.lastFreshTick != Long.MIN_VALUE) {
         boolean vecFlag = this.lastExplosionCenter != null && this.lastBoomTick != Long.MIN_VALUE && Math.abs(this.lastBoomTick - this.lastFreshTick) <= 1L && this.lastExplosionCenter.distanceTo(vec) <= 12.0;
         if (vecFlag) {
            float creativeSpectatorValue = this.explosionRadius > 0.0F ? this.explosionRadius : 6.0F;
            this.lastExplosionDamage = BlockEntityUtils.getCreativeSpectator(player, this.lastExplosionCenter, vec, box, null, null, creativeSpectatorValue);
            this.lastFreshTick = Long.MIN_VALUE;
         } else if (vecValue - this.lastFreshTick >= 1L) {
            this.lastExplosionDamage = 0.0F;
            this.lastFreshTick = Long.MIN_VALUE;
         }
      }
   }

   public void resetPopCount() {
      this.recentPopCount = 0;
   }
}
