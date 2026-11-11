package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class OpsCalls {
   private final long[] callsDmsTimes;
   private final int[] callsTable;
   private final long[] callsDmsTable;
   private final long[][] callsLongs;
   private static final int opsCallsValue = OpsCalls.Phase.values().length;
   private final long[] opsCallsTable;
   private final long[] table;
   private static final int COUNTER_COUNT = OpsCalls.Counter.values().length;

   public void updateTimesCalls() {
      Arrays.fill(this.callsDmsTable, 0L);
      Arrays.fill(this.callsDmsTimes, 0L);
      Arrays.fill(this.table, 0L);
      Arrays.fill(this.callsTable, 0);
      Arrays.fill(this.opsCallsTable, 0L);

      for (long[] callsValues : this.callsLongs) {
         Arrays.fill(callsValues, 0L);
      }
   }

   public void handleTimes(OpsCalls.Counter counter) {
      this.opsCallsTable[counter.ordinal()]++;
   }

   public List<String> getCallsDms() {
      ArrayList arrayList = new ArrayList();
      Integer[] integers = new Integer[opsCallsValue];

      for (int i = 0; i < opsCallsValue; i++) {
         integers[i] = i;
      }

      Arrays.sort(integers, (index, index2) -> Long.compare(this.callsDmsTimes[index2], this.callsDmsTimes[index]));
      Integer[] integers2 = integers;
      int callsDmsLimit = integers.length;

      for (int j = 0; j < callsDmsLimit; j++) {
         int index3 = integers2[j];
         if (this.callsDmsTable[index3] != 0L) {
            long callsDmsValue = this.callsDmsTimes[index3] / this.callsDmsTable[index3];
            arrayList.add(
               String.format(
                  Locale.ROOT,
                  "%s calls=%d total=%dms avg=%s p50=%s p95=%s p99=%s max=%s",
                  OpsCalls.Phase.values()[index3],
                  this.callsDmsTable[index3],
                  this.callsDmsTimes[index3] / 1000000L,
                  getFormat(callsDmsValue),
                  getFormat(this.getTimes(index3, 0.5)),
                  getFormat(this.getTimes(index3, 0.95)),
                  getFormat(this.getTimes(index3, 0.99)),
                  getFormat(this.table[index3])
               )
            );
         }
      }

      StringBuilder stringBuilder = new StringBuilder("ops:");

      for (OpsCalls.Counter counter : OpsCalls.Counter.values()) {
         if (this.opsCallsTable[counter.ordinal()] != 0L) {
            stringBuilder.append(' ').append(counter).append('=').append(this.opsCallsTable[counter.ordinal()]);
         }
      }

      if (stringBuilder.length() > 4) {
         arrayList.add(stringBuilder.toString());
      }

      return arrayList;
   }

   public OpsCalls() {
      this.callsDmsTable = new long[opsCallsValue];
      this.callsDmsTimes = new long[opsCallsValue];
      this.table = new long[opsCallsValue];
      this.callsLongs = new long[opsCallsValue][256];
      this.callsTable = new int[opsCallsValue];
      this.opsCallsTable = new long[COUNTER_COUNT];
   }

   public void handleTimes(OpsCalls.Counter counter, long opsCallsValue) {
      this.opsCallsTable[counter.ordinal()] += opsCallsValue;
   }

   public long getNanoTime() {
      return System.nanoTime();
   }

   public long getCallsDms(OpsCalls.Phase phase) {
      return this.callsDmsTable[phase.ordinal()];
   }

   private static String getFormat(long opsCallsLimit) {
      return opsCallsLimit >= 1000000L ? opsCallsLimit / 1000000L + "ms" : String.format(Locale.ROOT, "%.2fms", opsCallsLimit / 1000000.0);
   }

   public long getTimes(OpsCalls.Phase phase) {
      return this.table[phase.ordinal()] / 1000000L;
   }

   private long getTimes(int index, double opsCallsFactor) {
      int hashRegistryValue = Math.min(this.callsTable[index], 256);
      if (hashRegistryValue == 0) {
         return 0L;
      } else {
         long[] copyOf = Arrays.copyOf(this.callsLongs[index], hashRegistryValue);
         Arrays.sort(copyOf);
         return copyOf[Math.min(hashRegistryValue - 1, (int)Math.floor(opsCallsFactor * hashRegistryValue))];
      }
   }

   public void handleTimesCalls(OpsCalls.Phase phase, long callsValue) {
      long callsLimit = System.nanoTime() - callsValue;
      int ordinal = phase.ordinal();
      this.callsDmsTable[ordinal]++;
      this.callsDmsTimes[ordinal] = this.callsDmsTimes[ordinal] + callsLimit;
      if (callsLimit > this.table[ordinal]) {
         this.table[ordinal] = callsLimit;
      }

      this.callsLongs[ordinal][this.callsTable[ordinal]++ & 0xFF] = callsLimit;
   }

   public long getCallsDmsForPhase(OpsCalls.Phase phase) {
      return this.callsDmsTimes[phase.ordinal()] / 1000000L;
   }

   public long getTime(OpsCalls.Phase phase, double opsCallsValue) {
      return this.getTimes(phase.ordinal(), opsCallsValue) / 1000000L;
   }

   public long getTimes(OpsCalls.Counter counter) {
      return this.opsCallsTable[counter.ordinal()];
   }

   public static enum Counter {
      RAYCASTS,
      STAND_SCANS,
      ASTAR_RUNS,
      ASTAR_NODES,
      COMPONENT_BUILDS,
      PROOF_CACHE_HITS,
      PROOF_CACHE_MISSES,
      WAIT_SKIPS,
      WAIT_FULL_PASSES,
      COLUMN_RANKED_REUSED,
      PATH_PRECHECK_SKIPS,
      PATH_NEG_HITS,
      COMPONENT_CACHE_HITS,
      FAST_CONTINUES,
      LOCAL_SWEEP_PICKS,
      MULTIGOAL_SEARCHES,
      BAND_GATE_FALLBACKS,
      ROI_REFUSALS,
      TEMP_KEPT_FOR_WORK,
      COMPLETION_REPLANS,
      COMPLETION_PLAN_MS,
      COMPLETION_CURSOR_CONTINUES,
      COMPLETION_POSE_REJECTS,
      ROUTE_FOOTPRINT_HITS,
      ACCESS_FAMILY_BANS,
      SIGNATURE_REFUSALS,
      EGRESS_DEFERRALS,
      POCKET_ROUTE_REJECTS,
      LEASE_TRANSFERS,
      ACCESS_RETAINED,
      ELEVATION_PRECHECK_SKIPS,
      ROI_EVALUATIONS,
      ROI_ACCEPTED,
      LOCAL_SWEEP_EVALS,
      LOCAL_SWEEP_CANDIDATES;
   }

   public static enum Phase {
      SELECT_TOTAL,
      SELECT_PASS1,
      SELECT_GUARDS,
      SELECT_COMPONENT,
      SELECT_COVERAGE,
      SELECT_SCORING,
      SELECT_WORKPLAN,
      SELECT_WAIT,
      DRAIN_SCAN,
      SPENT_SCAN,
      STALE_SCAN,
      FLOATING_SUPPORT,
      STUCK_REPORT,
      GO_TOTAL,
      GO_OPPORTUNISTIC,
      GO_STAND_SOLVE,
      GO_PATH,
      GO_COLUMN,
      COLUMN_SOLVE,
      UNLOCKS_PROBE,
      ALIGN_TOTAL,
      CLEANUP_SELECT,
      INVENTORY_SWAP,
      OTHER_TICK,
      COMPLETION_TOTAL,
      COMPLETION_INDEX,
      COMPLETION_SNAPSHOT,
      COMPLETION_ADOPT,
      COMPLETION_STEP,
      COMPLETION_PARITY,
      TICK_TOTAL;
   }
}
