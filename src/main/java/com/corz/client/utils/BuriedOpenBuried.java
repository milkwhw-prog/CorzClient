package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import com.corz.client.utils.BuriedOpenConst;
import com.corz.client.utils.LinesQuads;
import com.corz.client.utils.TimesUpdate;
import com.corz.client.utils.ConstCopperUtils;
import com.corz.client.utils.EspHoleApi;
import com.corz.client.utils.MaxUtils;

public final class BuriedOpenBuried {
   private final BuriedOpenConst.Claims buriedOpenClaims;
   private final ArrayList<LinesQuads> buriedOpenLinesQuadsList;
   private int processedCount;
   private long[] copyOf;
   private int buriedOpenCount;
   private int buriedOpenEntryCount;
   private final int[] buriedOpenTable;
   private final BuriedOpenConst buriedOpen = new BuriedOpenConst();
   private final TimesUpdate buriedOpenTimesUpdate = new TimesUpdate(4096);
   private final int[] table;

   public int getBuriedOpenEntryCount() {
      return this.buriedOpenEntryCount;
   }

   public int getValue() {
      return this.processedCount;
   }

   public List<LinesQuads> getItems() {
      return this.buriedOpenLinesQuadsList;
   }

   private boolean canBuriedOpen(byte[] buriedOpenBytes, int buriedOpenValue, int apiLimit, int buriedOpenValue3, int buriedOpenLimit, int buriedOpenLimit2, int buriedOpenLimit3, int bottom) {
      if (buriedOpenLimit > 0 && buriedOpenLimit < 15 && buriedOpenLimit2 > 0 && buriedOpenLimit2 < 15 && buriedOpenLimit3 > 0 && buriedOpenLimit3 < 15) {
         int buriedOpenValue4 = buriedOpenLimit2 << 8 | buriedOpenLimit3 << 4 | buriedOpenLimit;
         return (buriedOpenBytes[buriedOpenValue4 + 1] & 3) != 1
            || (buriedOpenBytes[buriedOpenValue4 - 1] & 3) != 1
            || (buriedOpenBytes[buriedOpenValue4 + 16] & 3) != 1
            || (buriedOpenBytes[buriedOpenValue4 - 16] & 3) != 1
            || (buriedOpenBytes[buriedOpenValue4 + 256] & 3) != 1
            || (buriedOpenBytes[buriedOpenValue4 - 256] & 3) != 1;
      } else {
         return apiLimit - 1 < bottom
            ? true
            : !ConstCopperUtils.canFlag(this.buriedOpen.getFlagsApi(buriedOpenValue + 1, apiLimit, buriedOpenValue3))
               || !ConstCopperUtils.canFlag(this.buriedOpen.getFlagsApi(buriedOpenValue - 1, apiLimit, buriedOpenValue3))
               || !ConstCopperUtils.canFlag(this.buriedOpen.getFlagsApi(buriedOpenValue, apiLimit + 1, buriedOpenValue3))
               || !ConstCopperUtils.canFlag(this.buriedOpen.getFlagsApi(buriedOpenValue, apiLimit - 1, buriedOpenValue3))
               || !ConstCopperUtils.canFlag(this.buriedOpen.getFlagsApi(buriedOpenValue, apiLimit, buriedOpenValue3 + 1))
               || !ConstCopperUtils.canFlag(this.buriedOpen.getFlagsApi(buriedOpenValue, apiLimit, buriedOpenValue3 - 1));
      }
   }

   private void handle(long buriedOpenValue) {
      if (buriedOpenValue != Long.MIN_VALUE) {
         for (int i = 0; i < this.buriedOpenEntryCount; i++) {
            if (this.copyOf[i] == buriedOpenValue) {
               return;
            }
         }

         if (this.buriedOpenEntryCount == this.copyOf.length) {
            this.copyOf = Arrays.copyOf(this.copyOf, this.buriedOpenEntryCount * 2);
         }

         this.copyOf[this.buriedOpenEntryCount++] = buriedOpenValue;
      }
   }

   public void update() {
      Arrays.fill(this.buriedOpenTable, 0);
   }

   public BuriedOpenBuried() {
      this.buriedOpenClaims = (buriedOpenValue, buriedOpenLimit, buriedOpenValue3) -> this.buriedOpenTimesUpdate.canTimesForLong(EspHoleApi.getTime(buriedOpenValue, buriedOpenLimit, buriedOpenValue3));
      this.buriedOpenLinesQuadsList = new ArrayList<>();
      this.table = new int[256];
      this.buriedOpenTable = new int[16];
      this.copyOf = new long[8];
   }

   public int getBuriedOpenCount() {
      return this.buriedOpenCount;
   }

   public BuriedOpenConst getBuriedOpen() {
      return this.buriedOpen;
   }

   public boolean canBuriedOpen(EspHoleApi espHoleApi, int buriedOpenValue, int buriedOpenValue2, MaxUtils maxUtils) {
      this.buriedOpenLinesQuadsList.clear();
      this.buriedOpenTimesUpdate.updateTimesLoadCounter();
      this.buriedOpenEntryCount = 0;
      this.processedCount = 0;
      this.buriedOpenCount = 0;
      this.buriedOpen.handleFlagsApi(espHoleApi);
      int bottom = espHoleApi.getBottom();
      int buriedOpenValue3 = buriedOpenValue << 4;
      int buriedOpenValue4 = buriedOpenValue2 << 4;
      int buriedOpenBound = Integer.MIN_VALUE;

      for (int i = 0; i < 16; i++) {
         for (int j = 0; j < 16; j++) {
            int heightmapSample = espHoleApi.getHeightmapSample(buriedOpenValue3 + j, buriedOpenValue4 + i);
            if (heightmapSample == Integer.MIN_VALUE) {
               return false;
            }

            this.table[i << 4 | j] = heightmapSample;
            if (heightmapSample > buriedOpenBound) {
               buriedOpenBound = heightmapSample;
            }
         }
      }

      int buriedOpenValue5 = Math.min(buriedOpenBound, espHoleApi.getValue()) - 1;
      if (buriedOpenValue5 < bottom) {
         return true;
      } else {
         for (int k = bottom >> 4; k <= buriedOpenValue5 >> 4; k++) {
            byte[] sectionHash = espHoleApi.getSectionHash(buriedOpenValue, k, buriedOpenValue2);
            if (sectionHash == null) {
               return false;
            }

            if (sectionHash != ConstCopperUtils.buriedOpenTable && sectionHash != ConstCopperUtils.sectionHashTable) {
               int buriedOpenValue6 = k << 4;

               for (int m = 0; m < 4096; m++) {
                  if ((sectionHash[m] & 3) == 1) {
                     int buriedOpenValue7 = m >> 8;
                     int buriedOpenValue8 = m >> 4 & 15;
                     int buriedOpenValue9 = m & 15;
                     int buriedOpenLimit = buriedOpenValue6 + buriedOpenValue7;
                     if (buriedOpenLimit >= bottom && buriedOpenLimit < this.table[buriedOpenValue8 << 4 | buriedOpenValue9]) {
                        int buriedOpenValue10 = buriedOpenValue3 + buriedOpenValue9;
                        int buriedOpenValue11 = buriedOpenValue4 + buriedOpenValue8;
                        if (this.canBuriedOpen(sectionHash, buriedOpenValue10, buriedOpenLimit, buriedOpenValue11, buriedOpenValue9, buriedOpenValue7, buriedOpenValue8, bottom)
                           && !this.buriedOpenTimesUpdate.canTimesForLong(EspHoleApi.getTime(buriedOpenValue10, buriedOpenLimit, buriedOpenValue11))) {
                           int timeClaimed = this.buriedOpen.getTimeClaimed(buriedOpenValue10, buriedOpenLimit, buriedOpenValue11, maxUtils, this.buriedOpenClaims);
                           this.processedCount++;
                           this.buriedOpenTable[timeClaimed]++;
                           int facesEdgesCount = this.buriedOpen.getFacesEdgesCount();
                           this.buriedOpenCount += facesEdgesCount;

                           for (int p = 0; p < facesEdgesCount; p++) {
                              this.buriedOpenTimesUpdate
                                 .canTimes(
                                    EspHoleApi.getTime(this.buriedOpen.getValue(p), this.buriedOpen.getValueFacesEdgesTable(p), this.buriedOpen.getValueInt(p))
                                 );
                           }

                           if (timeClaimed == 0) {
                              this.buriedOpenLinesQuadsList.add(this.buriedOpen.getClassFacesEdges());
                           } else if (timeClaimed == 4) {
                              this.handle(this.buriedOpen.getTime());
                           }
                        }
                     }
                  }
               }
            }
         }

         return true;
      }
   }

   public int getValue(int index) {
      return this.buriedOpenTable[index];
   }

   public long getTime(int index) {
      return this.copyOf[index];
   }
}
