package com.corz.client.utils;

import com.corz.client.config.VillagerTradeRule;
import com.corz.client.inventory.IngotBallUtils;
import com.corz.client.inventory.OfferTrade;
import com.corz.client.inventory.ProtectionCurseUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.item.Item;
import net.minecraft.util.math.Vec3d;
import com.corz.client.utils.TrackedVillager;

public final class TradeEnchantUtils {
   private static double getSquaredDistance(TradeEnchantUtils.Candidate candidate, Vec3d vec) {
      return candidate.villager().entityPos.squaredDistanceTo(vec == null ? Vec3d.ZERO : vec);
   }

   public static boolean offerMatchesRule(VillagerTradeRule enchantJsonProfession, OfferTrade offerTrade, boolean enchantStackFlag) {
      if (enchantJsonProfession != null && offerTrade != null) {
         switch (enchantJsonProfession.action) {
            case BUY:
               if (!enchantStackFlag && !isWithinMaxCost(enchantJsonProfession, offerTrade)) {
                  return false;
               } else {
                  return switch (enchantJsonProfession.kind) {
                     case ENCHANTED_BOOK -> offerTrade.emeraldStackActive
                        && offerTrade.next != null
                        && ProtectionCurseUtils.canMatchesProtection(offerTrade.next, enchantJsonProfession.enchant)
                        && offerTrade.level >= Math.max(1, enchantJsonProfession.enchantLevel);
                     case EMERALD -> offerTrade.itemStack;
                     case ITEM -> offerTrade.emeraldStackItem == IngotBallUtils.getDefaultedRegistry(enchantJsonProfession.item);
                  };
               }
            case SELL:
               if (!offerTrade.itemStack) {
                  return false;
               }

               Item defaultedRegistry = IngotBallUtils.getDefaultedRegistry(enchantJsonProfession.item);
               return defaultedRegistry != null && (offerTrade.primaryCostItem == defaultedRegistry || offerTrade.secondaryCostItem == defaultedRegistry);
            default:
               return false;
         }
      } else {
         return false;
      }
   }

   public static boolean matchesProfession(TrackedVillager villagerExperience, VillagerTradeRule enchantJsonProfession) {
      return villagerExperience != null && NoneCartographerUtils.canMatchesMason(villagerExperience.profession, enchantJsonProfession.profession);
   }

   public static TradeEnchantUtils.Candidate selectCandidate(
      List<TradeEnchantUtils.Candidate> list, TradeEnchantUtils.SelectionMode selectionMode, Vec3d vec, int tradeEnchantValue
   ) {
      if (list != null && !list.isEmpty()) {
         if (list.size() == 1) {
            return (TradeEnchantUtils.Candidate)list.get(0);
         } else {
            return switch (selectionMode) {
               case CHEAPEST -> pickHighestScore(list, candidate -> -candidate.trade().count3EnchantJson);
               case CLOSEST -> pickHighestScore(list, candidate2 -> -getSquaredDistance(candidate2, vec));
               case MOST_STOCK -> pickHighestScore(list, candidate3 -> candidate3.trade().getMax());
               case ROUND_ROBIN -> (TradeEnchantUtils.Candidate)list.get(Math.floorMod(tradeEnchantValue, list.size()));
               case USER_PRIORITY -> pickHighestScore(list, candidate4 -> -candidate4.trade().count3EnchantJson);
               case BEST_SCORE -> pickHighestScore(list, candidate5 -> computeScore(candidate5, vec));
            };
         }
      } else {
         return null;
      }
   }

   public static boolean isWithinMaxCost(VillagerTradeRule enchantJsonProfession, OfferTrade offerTrade) {
      return enchantJsonProfession.action != VillagerTradeRule.Action.BUY || enchantJsonProfession.maxEmeraldCost <= 0 || offerTrade.count3EnchantJson <= enchantJsonProfession.maxEmeraldCost;
   }

   public static List<TradeEnchantUtils.Candidate> collectCandidates(
      Collection<TrackedVillager> collection, VillagerTradeRule enchantJsonProfession, Predicate<TrackedVillager> predicate
   ) {
      ArrayList arrayList = new ArrayList();

      for (TrackedVillager villagerExperience : collection) {
         if (predicate == null || predicate.test(villagerExperience)) {
            OfferTrade tradeBuy = findBestTrade(villagerExperience, enchantJsonProfession);
            if (tradeBuy != null) {
               arrayList.add(new TradeEnchantUtils.Candidate(villagerExperience, tradeBuy));
            }
         }
      }

      return arrayList;
   }

   private static TradeEnchantUtils.Candidate pickHighestScore(List<TradeEnchantUtils.Candidate> list, TradeEnchantUtils.Scorer scorer) {
      TradeEnchantUtils.Candidate candidate = null;
      double tradeEnchantValue = Double.NEGATIVE_INFINITY;

      for (TradeEnchantUtils.Candidate candidate2 : list) {
         double of = scorer.score(candidate2);
         if (of > tradeEnchantValue) {
            tradeEnchantValue = of;
            candidate = candidate2;
         }
      }

      return candidate;
   }

   public static double computeScore(TradeEnchantUtils.Candidate candidate, Vec3d vec) {
      OfferTrade trade = candidate.trade();
      double specialPriceValue = trade.count3EnchantJson * 3.0;
      double sqrtScaled = Math.sqrt(getSquaredDistance(candidate, vec)) * 0.8;
      double specialPriceValue2 = Math.min(trade.getMax(), 16) * 1.5;
      double specialPriceValue3 = trade.specialPrice < 0 ? -trade.specialPrice * 2.0 : 0.0;
      return specialPriceValue2 + specialPriceValue3 - specialPriceValue - sqrtScaled;
   }

   public static OfferTrade findBestTrade(TrackedVillager villagerExperience, VillagerTradeRule enchantJsonProfession) {
      if (!matchesProfession(villagerExperience, enchantJsonProfession)) {
         return null;
      } else {
         OfferTrade tradeBuy = null;

         for (OfferTrade offerTrade : villagerExperience.offers) {
            if (isAvailableMatch(enchantJsonProfession, offerTrade)) {
               if (tradeBuy == null) {
                  tradeBuy = offerTrade;
               } else if (enchantJsonProfession.action == VillagerTradeRule.Action.BUY) {
                  if (offerTrade.count3EnchantJson < tradeBuy.count3EnchantJson) {
                     tradeBuy = offerTrade;
                  }
               } else if (offerTrade.emeraldAutomateValue > tradeBuy.emeraldAutomateValue) {
                  tradeBuy = offerTrade;
               }
            }
         }

         return tradeBuy;
      }
   }

   public static boolean isAvailableMatch(VillagerTradeRule enchantJsonProfession, OfferTrade offerTrade) {
      return offerTrade != null && offerTrade.isDisabled() && offerMatchesRule(enchantJsonProfession, offerTrade, false);
   }

   private TradeEnchantUtils() {
   }

   public record Candidate(TrackedVillager villager, OfferTrade trade) {
   }

   private interface Scorer {
      double score(TradeEnchantUtils.Candidate candidate);
   }

   public static enum SelectionMode {
      CHEAPEST,
      CLOSEST,
      MOST_STOCK,
      ROUND_ROBIN,
      USER_PRIORITY,
      BEST_SCORE;
   }
}
