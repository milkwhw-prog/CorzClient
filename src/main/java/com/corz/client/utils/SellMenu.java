package com.corz.client.utils;

import java.util.List;
import com.corz.client.utils.TpaReplies;

public final class SellMenu {
   public static boolean hasEmptyCellBesides(List<SellMenu.Cell> list, int emptyValue) {
      if (list == null) {
         return false;
      } else {
         for (int i = 0; i < list.size(); i++) {
            if (i != emptyValue) {
               SellMenu.Cell sellMenuCell = (SellMenu.Cell)list.get(i);
               if (sellMenuCell == null || sellMenuCell.empty()) {
                  return true;
               }
            }
         }

         return false;
      }
   }

   public static int findSellButtonIndex(List<SellMenu.Cell> list) {
      if (list == null) {
         return -1;
      } else {
         int sellPane = -1;

         for (int i = 0; i < list.size(); i++) {
            SellMenu.Cell sellMenuCell = (SellMenu.Cell)list.get(i);
            if (sellMenuCell != null && !sellMenuCell.empty()) {
               if (TpaReplies.normalizeSmallCaps(sellMenuCell.name()).contains("sell")) {
                  return i;
               }

               if (sellMenuCell.greenPane()) {
                  sellPane = i;
               }
            }
         }

         return sellPane;
      }
   }

   private SellMenu() {
   }

   public static boolean isSellMenu(String sellText, List<SellMenu.Cell> list) {
      return TpaReplies.normalizeSmallCaps(sellText).contains("sell") ? true : findSellButtonIndex(list) >= 0;
   }

   public record Cell(boolean empty, String name, boolean greenPane) {
      public static final SellMenu.Cell INSTANCE = new SellMenu.Cell(true, "", false);
   }
}
