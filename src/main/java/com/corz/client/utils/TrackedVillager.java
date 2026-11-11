package com.corz.client.utils;

import com.corz.client.config.VillagerTradeRule;
import com.corz.client.inventory.OfferTrade;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;
import com.corz.client.utils.TradeEnchantUtils;

public final class TrackedVillager {
   public long soldOutTick;
   public long levelResetTick;
   public Vec3d entityPos = Vec3d.ZERO;
   public long lastTradeScreenTick;
   public long lastSeenTick;
   public boolean hasProfession;
   public int entityId;
   public final long unusedTick;
   public long cooldownUntilTick;
   public final UUID uuid;
   public BlockPos blockPos = BlockPos.ORIGIN;
   public int level;
   public String professionName = "None";
   public List<OfferTrade> offers;
   public RegistryEntry<VillagerProfession> profession;
   public int experience;
   public int failStreak;
   private String offersSignature;

   public void updateLevel(int level) {
      if (this.level > 0 && level != this.level) {
         this.offers = new ArrayList<>();
         this.offersSignature = "";
         this.levelResetTick = -4611686018427387904L;
      }

      this.level = level;
   }

   public void updateFailStreak(boolean villagerExperienceFlag, boolean restockFlag) {
      if (!restockFlag) {
         this.failStreak = 0;
      } else if (villagerExperienceFlag) {
         this.failStreak = Math.min(this.failStreak + 1, 4);
      }
   }

   public boolean setOffersAndCheckChanged(List<OfferTrade> list) {
      StringBuilder stringBuilder = new StringBuilder();

      for (OfferTrade offerTrade : list) {
         stringBuilder.append(offerTrade.getAppend()).append('|');
      }

      String noneText = stringBuilder.toString();
      boolean noneText2 = !noneText.equals(this.offersSignature);
      this.offers = new ArrayList<>(list);
      this.offersSignature = noneText;
      return noneText2;
   }

   public boolean needsLevelUp(VillagerTradeRule enchantJsonProfession) {
      int levelLimit = enchantJsonProfession == null ? 0 : enchantJsonProfession.getBuyItemValue();
      return levelLimit > 0 && this.level > 0 && this.level < levelLimit;
   }

   public TrackedVillager(UUID price) {
      this.hasProfession = true;
      this.offers = new ArrayList<>();
      this.offersSignature = "";
      this.lastTradeScreenTick = -4611686018427387904L;
      this.unusedTick = -4611686018427387904L;
      this.levelResetTick = -4611686018427387904L;
      this.cooldownUntilTick = -4611686018427387904L;
      this.soldOutTick = -4611686018427387904L;
      this.uuid = price;
   }

   public boolean isBlockedByStockOrPrice(VillagerTradeRule enchantJsonProfession) {
      return this.getBlockReason(enchantJsonProfession) != TrackedVillager.Drain.NONE;
   }

   public OfferTrade findTooExpensiveOffer(VillagerTradeRule enchantJsonProfession) {
      if (enchantJsonProfession != null && enchantJsonProfession.action == VillagerTradeRule.Action.BUY && enchantJsonProfession.maxEmeraldCost > 0) {
         for (OfferTrade next : this.offers) {
            if (next.isDisabled() && TradeEnchantUtils.offerMatchesRule(enchantJsonProfession, next, true) && !TradeEnchantUtils.isWithinMaxCost(enchantJsonProfession, next)) {
               return next;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public boolean isOnCooldown(long villagerExperienceValue) {
      return villagerExperienceValue < this.cooldownUntilTick;
   }

   public void updateFromEntity(VillagerEntity villagerEntity, long time) {
      this.entityId = villagerEntity.getId();
      this.entityPos = villagerEntity.getEntityPos();
      this.blockPos = villagerEntity.getBlockPos();
      VillagerData villagerData = villagerEntity.getVillagerData();
      this.profession = villagerData.profession();
      this.professionName = NoneCartographerUtils.getKey(this.profession);
      this.updateLevel(villagerData.level());
      this.experience = villagerEntity.getExperience();
      this.hasProfession = !villagerEntity.isBaby()
         && this.profession != null
         && !this.profession.matchesKey(VillagerProfession.NONE)
         && !this.profession.matchesKey(VillagerProfession.NITWIT);
      this.lastSeenTick = time;
   }

   public TrackedVillager.Drain getBlockReason(VillagerTradeRule enchantJsonProfession) {
      if (enchantJsonProfession != null && TradeEnchantUtils.matchesProfession(this, enchantJsonProfession)) {
         TrackedVillager.Drain noneExpensive = TrackedVillager.Drain.NONE;

         for (OfferTrade offerTrade : this.offers) {
            if (TradeEnchantUtils.offerMatchesRule(enchantJsonProfession, offerTrade, true)) {
               boolean emeraldCost = TradeEnchantUtils.isWithinMaxCost(enchantJsonProfession, offerTrade);
               if (offerTrade.isDisabled() && emeraldCost) {
                  return TrackedVillager.Drain.NONE;
               }

               if (!offerTrade.isDisabled()) {
                  noneExpensive = TrackedVillager.Drain.SOLD_OUT;
               } else if (noneExpensive == TrackedVillager.Drain.NONE) {
                  noneExpensive = TrackedVillager.Drain.TOO_EXPENSIVE;
               }
            }
         }

         return noneExpensive;
      } else {
         return TrackedVillager.Drain.NONE;
      }
   }

   public static enum Drain {
      NONE,
      SOLD_OUT,
      TOO_EXPENSIVE;
   }
}
