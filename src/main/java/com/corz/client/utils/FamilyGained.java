package com.corz.client.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.function.LongPredicate;
import java.util.function.LongUnaryOperator;
import com.corz.client.utils.EscalationsEscalation;

public final class FamilyGained {
   private int boundaryWalkableCount;
   private int reclimbIgnoredCount;
   private long restartedSectionTime;
   private long admittedDispatcherCount;
   private int familyQueuedEventCount;
   private int terminalExecutingCount;
   private final Map<Long, Long> familyCausalPositionMap;
   private long appendTickCounter;
   private final LinkedHashMap<Long, FamilyGained.Entry> familyQueuedEntryMap = new LinkedHashMap<>();
   private int walkableBoundaryCount;
   private int banMissionCount;
   private int connectionSupersededCount;
   private int familyQueuedCount;
   private long familyGainedCount;
   private final ArrayList<Long> familyCausalPositionList;
   private String append;
   private int familyQueuedTotalCount;

   public int getAppendTime(long appendValue, EscalationsEscalation.Strategy... strategies) {
      FamilyGained.Entry entry = this.familyQueuedEntryMap.get(appendValue);
      if (entry != null && !entry.isTerminalExecuting()) {
         int appendTime = 0;

         for (EscalationsEscalation.Strategy strategy : strategies) {
            if (entry.noneOf.add(strategy)) {
               appendTime++;
            }
         }

         if (appendTime > 0) {
            this.appendTickCounter++;
         }

         if (appendTime > 0) {
            this.append = "rungs " + Arrays.toString((Object[])strategies) + " share one call for family " + formatPositionText(appendValue) + " - recorded as tried";
         }

         return appendTime;
      } else {
         return 0;
      }
   }

   public FamilyGained.Transition getBuiltConnection(long builtConnectionValue, boolean builtConnectionFlag) {
      this.appendTickCounter++;

      for (FamilyGained.Entry entry : this.familyQueuedEntryMap.values()) {
         if (entry.terminalExecutingType == FamilyGained.State.EXECUTING && entry.orDefault == builtConnectionValue) {
            FamilyGained.State terminalExecutingType = entry.terminalExecutingType;
            if (builtConnectionFlag) {
               entry.terminalExecutingType = FamilyGained.State.TERMINAL_BUILT;
               entry.missionBanType = FamilyGained.Dependency.NONE;
               this.connectionSupersededCount++;
               entry.causalFamilyCount = 0;
               entry.connectionBuiltValue = 0;
               entry.connectionBuiltValue2 = 0;
               entry.reconsideredFamilyValue = 0;
               entry.boundaryWalkableTime = this.familyGainedCount;
               entry.reason = "connection built";
               this.append = "terminal family=" + formatPositionText(entry.connectionBuiltTime) + " result=BUILT";
            } else {
               entry.terminalExecutingType = FamilyGained.State.QUEUED;
               entry.missionBanType = FamilyGained.Dependency.NONE;
               entry.reason = "service mission " + builtConnectionValue + " failed; next strategy";
               this.append = "strategy-result family="
                  + formatPositionText(entry.connectionBuiltTime)
                  + " strategy="
                  + entry.strategy
                  + " result=MISSION_FAILED next="
                  + getProveImpossible(entry);
            }

            return new FamilyGained.Transition(terminalExecutingType, entry.terminalExecutingType, builtConnectionFlag ? null : getProveImpossible(entry), entry.reason);
         }
      }

      this.append = "mission " + builtConnectionValue + " ended (" + (builtConnectionFlag ? "built" : "failed") + "): no family owns it any more (superseded, retired or evicted)";
      return null;
   }

   public int getQueuedFamilyCount() {
      return this.familyQueuedEventCount;
   }

   private static boolean containsPackedValue(long[] familyGainedValues, long familyGainedValue) {
      for (long familyGainedValuesItem : familyGainedValues) {
         if (familyGainedValuesItem == familyGainedValue) {
            return true;
         }
      }

      return false;
   }

   private void recordCausalEntry(FamilyGained.Entry entry) {
      ArrayList familyCausalItems = null;

      for (long familyCausalValue : entry.familyCausalItems) {
         Long long2 = this.familyCausalPositionMap.get(familyCausalValue);
         if (long2 == null || long2 != entry.connectionBuiltTime) {
            FamilyGained.Entry entry2 = long2 == null ? null : this.familyQueuedEntryMap.get(long2);
            if (entry2 == null || entry2.isTerminalExecuting()) {
               if (familyCausalItems == null) {
                  familyCausalItems = new ArrayList<>(entry.familyVerdictItems);
               }

               familyCausalItems.add(familyCausalValue);
            }
         }
      }

      if (familyCausalItems != null) {
         entry.familyCausalItems = familyCausalItems;
      } else {
         entry.familyCausalItems = null;
         entry.familyCellsTime = Long.MIN_VALUE;
      }
   }

   public int getCausalPositionCount() {
      return this.walkableBoundaryCount;
   }

   private void recordCausalPosition(long familyCausalValue) {
      FamilyGained.Entry entry = this.familyQueuedEntryMap.remove(familyCausalValue);
      if (entry != null) {
         for (long familyCausalValue2 : entry.familyVerdictItems) {
            this.familyCausalPositionMap.remove(familyCausalValue2, familyCausalValue);
         }

         this.familyCausalPositionList.addAll(entry.familyVerdictItems);
         if (entry.familyCausalItems != null) {
            this.familyCausalPositionList.addAll(entry.familyCausalItems);
         }
      }
   }

   public FamilyGained.Transition getTerminalConnection(long terminalConnectionValue, String terminalConnectionText) {
      this.appendTickCounter++;
      FamilyGained.Entry entry = this.familyQueuedEntryMap.get(terminalConnectionValue);
      if (entry != null && !entry.isTerminalExecuting()) {
         FamilyGained.State terminalExecutingType = entry.terminalExecutingType;
         entry.terminalExecutingType = FamilyGained.State.TERMINAL_BUILT;
         entry.missionBanType = FamilyGained.Dependency.NONE;
         this.connectionSupersededCount++;
         entry.causalFamilyCount = 0;
         entry.connectionBuiltValue = 0;
         entry.connectionBuiltValue2 = 0;
         entry.reconsideredFamilyValue = 0;
         entry.boundaryWalkableTime = this.familyGainedCount;
         entry.reason = terminalConnectionText == null ? "" : terminalConnectionText;
         this.append = "terminal family=" + formatPositionText(terminalConnectionValue) + " result=BUILT (" + entry.reason + ")";
         return new FamilyGained.Transition(terminalExecutingType, entry.terminalExecutingType, null, entry.reason);
      } else {
         return null;
      }
   }

   private FamilyGained.Transition getStrategyFamily(long strategyFamilyValue, EscalationsEscalation.Strategy strategy, FamilyGained.StrategyResult strategyResult, FamilyGained.Transition strategyFamily) {
      this.append = "strategy-result family="
         + formatPositionText(strategyFamilyValue)
         + " strategy="
         + strategy
         + " result="
         + strategyResult
         + " next="
         + (strategyFamily == null ? "-" : (strategyFamily.next() == null ? strategyFamily.to() : strategyFamily.next()));
      return strategyFamily;
   }

   public int getFamilyGainedCountValue() {
      return this.familyQueuedEntryMap.size();
   }

   public Long getSectionTime() {
      FamilyGained.Entry entry = null;

      for (FamilyGained.Entry entry2 : this.familyQueuedEntryMap.values()) {
         if (entry2.terminalExecutingType == FamilyGained.State.QUEUED && (entry == null || entry2.sectionTime < entry.sectionTime)) {
            entry = entry2;
         }
      }

      return entry == null ? null : entry.connectionBuiltTime;
   }

   public int getConnectionSupersededCount() {
      return this.connectionSupersededCount;
   }

   public int getValueWithPredicate(long familyGainedValue, LongPredicate longPredicate) {
      return this.getValueWithOperatorAndPredicate(familyGainedValue, familyGainedValue2 -> Long.MIN_VALUE, longPredicate);
   }

   public int getQueuedTotalCountValue() {
      return this.banMissionCount;
   }

   private static int[] getValuesFromList(List<Long> list) {
      if (list.isEmpty()) {
         return new int[0];
      } else {
         int familyGainedLimit = Integer.MAX_VALUE;
         int maxValue = Integer.MAX_VALUE;
         int maxValue3 = Integer.MAX_VALUE;
         int familyGainedValue = Integer.MIN_VALUE;
         int minValue = Integer.MIN_VALUE;
         int minValue3 = Integer.MIN_VALUE;

         for (long proofSupportValue : list) {
            int valueInt = ProofSupportUtils.getValueInt(proofSupportValue);
            int familyGainedLimit5 = ProofSupportUtils.getValue(proofSupportValue);
            int valueForLong = ProofSupportUtils.getValueForLong(proofSupportValue);
            if (valueInt < familyGainedLimit) {
               familyGainedLimit = valueInt;
            }

            if (valueInt > familyGainedValue) {
               familyGainedValue = valueInt;
            }

            if (familyGainedLimit5 < maxValue) {
               maxValue = familyGainedLimit5;
            }

            if (familyGainedLimit5 > minValue) {
               minValue = familyGainedLimit5;
            }

            if (valueForLong < maxValue3) {
               maxValue3 = valueForLong;
            }

            if (valueForLong > minValue3) {
               minValue3 = valueForLong;
            }
         }

         return new int[]{familyGainedLimit, maxValue, maxValue3, familyGainedValue, minValue, minValue3};
      }
   }

   public FamilyGained.Family getFamilyForPosition(long familyGainedValue) {
      FamilyGained.Entry entry = this.familyQueuedEntryMap.get(familyGainedValue);
      return entry == null ? null : entry.getTimeGained();
   }

   public int getBanExecuting(FamilyGained.Dependency dependency) {
      int banExecuting = 0;

      for (FamilyGained.Entry entry : this.familyQueuedEntryMap.values()) {
         if (entry.terminalExecutingType == FamilyGained.State.WAITING && entry.missionBanType == dependency) {
            banExecuting++;
         }
      }

      return banExecuting;
   }

   public long getTimeForPosition(long familyGainedValue) {
      FamilyGained.Entry entry = this.familyQueuedEntryMap.get(familyGainedValue);
      return entry == null ? Long.MIN_VALUE : entry.time;
   }

   public boolean canFamilyQueued(long familyQueuedValue, Collection<Long> collection, long gainedTime, long asLong, long time, String familyQueuedText) {
      FamilyGained.Entry entry = this.familyQueuedEntryMap.get(familyQueuedValue);
      if (entry == null) {
         if (this.familyQueuedEntryMap.size() >= 1024 && !this.isTerminalCausal()) {
            this.familyQueuedEventCount++;
            this.append = "enqueue family="
               + formatPositionText(familyQueuedValue)
               + " refused: "
               + 1024
               + " live families (capacity) - the members stay held until a family ends";
            return false;
         } else {
            FamilyGained.Entry finish = new FamilyGained.Entry(familyQueuedValue, new ArrayList<>(), gainedTime);
            this.familyQueuedEntryMap.put(familyQueuedValue, finish);
            this.appendTickCounter++;
            this.canFamilyVerdict(finish, collection);
            recordRestartedCausal(finish, asLong);
            finish.boundaryWalkableTime = this.familyGainedCount;
            finish.min = finish.familyVerdictItems.size();
            if (time != Long.MIN_VALUE) {
               finish.time = time;
               finish.familyQueuedTimes = new long[]{time};
            }

            finish.reason = familyQueuedText == null ? "" : familyQueuedText;
            this.familyQueuedCount++;
            this.append = "enqueue family=" + formatPositionText(familyQueuedValue) + " size=" + this.familyQueuedEntryMap.size() + " reason=" + finish.reason;
            return true;
         }
      } else if (!entry.isTerminalExecuting()) {
         if (collection != null && !collection.isEmpty() && !this.canFamilyVerdictAlternate(entry, collection)) {
            boolean connectionBuilt = this.canConnectionBuilt(entry, collection);
            boolean familyVerdict = this.canFamilyVerdict(entry, collection);
            if (connectionBuilt && entry.connectionBuiltValue2 < 2) {
               entry.connectionBuiltValue2++;
               entry.min = entry.familyVerdictItems.size();
            }

            this.appendTickCounter++;
            if (familyVerdict && entry.terminalExecutingType == FamilyGained.State.QUEUED && !entry.noneOf.isEmpty() && entry.restartedSectionValue < 2) {
               entry.restartedSectionValue++;
               entry.noneOf.clear();
               recordRestartedCausal(entry, Long.MIN_VALUE);
            }
         }

         if (entry.terminalExecutingType == FamilyGained.State.WAITING
            && entry.missionBanType == FamilyGained.Dependency.EPOCH
            && gainedTime != entry.gainedTime) {
            entry.terminalExecutingType = FamilyGained.State.QUEUED;
            entry.missionBanType = FamilyGained.Dependency.NONE;
            entry.gainedTime = gainedTime;
            this.append = "enqueue family=" + formatPositionText(familyQueuedValue) + " woke from EPOCH wait (" + familyQueuedText + ")";
            this.appendTickCounter++;
            return true;
         } else {
            return entry.terminalExecutingType == FamilyGained.State.QUEUED;
         }
      } else {
         boolean familyQueuedFlag = asLong != Long.MIN_VALUE && entry.asLong2 != Long.MIN_VALUE && asLong != entry.asLong2;
         boolean familyQueuedFlag2 = collection != null && !collection.isEmpty() && this.canConnectionBuilt(entry, collection);
         boolean familyQueuedFlag3 = time != Long.MIN_VALUE
            && entry.time != Long.MIN_VALUE
            && time != entry.time
            && !containsPackedValue(entry.familyQueuedTimes, time)
            && entry.familyQueuedTimes.length < 4;
         boolean familyQueuedFlag4 = familyQueuedFlag || familyQueuedFlag2 || familyQueuedFlag3;
         String familyQueuedText2 = familyQueuedFlag
            ? "the geometry around the family or its approach changed"
            : (familyQueuedFlag2 ? "the family gained cells" : (familyQueuedFlag3 ? "a new walk origin (approach " + (entry.familyQueuedTimes.length + 1) + "/" + 4 + ")" : ""));
         FamilyGained.State terminalExecutingType = entry.terminalExecutingType;
         if (entry.terminalExecutingType == FamilyGained.State.TERMINAL_IMPOSSIBLE) {
            if (!familyQueuedFlag4 && gainedTime == entry.gainedTime) {
               this.append = "enqueue family="
                  + formatPositionText(familyQueuedValue)
                  + " ignored: terminal "
                  + entry.terminalExecutingType
                  + " under the same epoch, geometry and origin";
               return false;
            } else {
               String familyQueuedText3;
               if (familyQueuedFlag4) {
                  if ((familyQueuedFlag || familyQueuedFlag2) && !this.canCausalWalkable(entry)) {
                     if (familyQueuedFlag2) {
                        entry.familyCausalItems = new ArrayList<>(collection);
                        entry.familyCellsTime = time;
                     }

                     if (familyQueuedFlag) {
                        entry.asLong = asLong;
                        entry.causalFamilyTime = -1L;
                     }

                     this.append = "deferred-causal family="
                        + formatPositionText(familyQueuedValue)
                        + ": the "
                        + (familyQueuedFlag ? "geometry changed" : "family gained cells")
                        + " again after "
                        + entry.causalFamilyCount
                        + " causal reopens that ended IMPOSSIBLE - reopened after progress elsewhere or when the dispatcher is idle";
                     return false;
                  }

                  entry.familyCausalItems = null;
                  entry.familyCellsTime = Long.MIN_VALUE;
                  if (familyQueuedFlag || familyQueuedFlag2) {
                     entry.causalFamilyCount++;
                     entry.reopensReopenedValue = 0;
                     entry.familyQueuedTimes = time != Long.MIN_VALUE ? new long[]{time} : new long[0];
                  } else if (familyQueuedFlag3) {
                     entry.familyQueuedTimes = getTimesWithValue(entry.familyQueuedTimes, time);
                  }

                  entry.impossibleQueuedValue = 0;
                  entry.reconsideredFamilyValue = 0;
                  familyQueuedText3 = "re-queued: " + familyQueuedText2 + " since the IMPOSSIBLE verdict" + (familyQueuedFlag ? " (causal reopen " + entry.causalFamilyCount + ")" : "");
               } else {
                  if (++entry.impossibleQueuedValue > 3) {
                     boolean familyQueuedFlag5 = entry.impossibleQueuedValue == 4;
                     if (familyQueuedFlag5) {
                        this.reclimbIgnoredCount++;
                     }

                     this.append = (familyQueuedFlag5 ? "refused-reclimb" : "ignored")
                        + " family="
                        + formatPositionText(familyQueuedValue)
                        + ": IMPOSSIBLE re-climbed "
                        + 3
                        + " times with unchanged geometry and origin - reopened only by a change around the family or its approach, a new origin, or a walk-component expansion that serves it";
                     return false;
                  }

                  familyQueuedText3 = "re-queued after the epoch advanced, geometry unchanged (re-climb " + entry.impossibleQueuedValue + "/" + 3 + ")";
               }

               this.canFamilyVerdict(entry, (Collection<Long>)(collection != null && !collection.isEmpty() ? collection : entry.familyVerdictItems));
               entry.terminalExecutingType = FamilyGained.State.QUEUED;
               entry.noneOf.clear();
               entry.strategy = null;
               entry.missionBanType = FamilyGained.Dependency.NONE;
               entry.orDefault = 0L;
               entry.verdictFamilyValue = 0;
               entry.restartedSectionValue = 0;
               entry.connectionBuiltValue = 0;
               entry.connectionBuiltValue2 = 0;
               entry.min = entry.familyVerdictItems.size();
               if (!familyQueuedFlag4) {
                  entry.familyCausalItems = null;
                  entry.familyCellsTime = Long.MIN_VALUE;
               }

               entry.gainedTime = gainedTime;
               recordRestartedCausal(entry, asLong);
               if (time != Long.MIN_VALUE) {
                  entry.time = time;
               }

               entry.reason = familyQueuedText3 + " (" + familyQueuedText + ")";
               this.append = "enqueue family=" + formatPositionText(familyQueuedValue) + " size=" + this.familyQueuedEntryMap.size() + " reason=" + entry.reason;
               this.familyGainedCount++;
               this.appendTickCounter++;
               return true;
            }
         } else {
            boolean familyQueuedFlag6 = !familyQueuedFlag2 && collection != null && !collection.isEmpty() && collection.size() < Math.min(entry.familyVerdictItems.size(), entry.min);
            if (!familyQueuedFlag4 && !familyQueuedFlag6) {
               if (++entry.verdictFamilyValue > 2) {
                  entry.terminalExecutingType = FamilyGained.State.TERMINAL_IMPOSSIBLE;
                  entry.strategy = EscalationsEscalation.Strategy.PROVE_IMPOSSIBLE;
                  entry.missionBanType = FamilyGained.Dependency.NONE;
                  this.familyQueuedTotalCount++;
                  entry.reason = "verdict " + terminalExecutingType + " contradicted " + entry.verdictFamilyValue + " times with unchanged geometry (" + familyQueuedText + ")";
                  entry.gainedTime = gainedTime;
                  entry.boundaryWalkableTime = this.familyGainedCount;
                  this.append = "terminal family=" + formatPositionText(familyQueuedValue) + " result=IMPOSSIBLE (" + entry.reason + ")";
                  this.appendTickCounter++;
                  return false;
               } else {
                  if (this.canFamilyVerdict(entry, (Collection<Long>)(collection != null && !collection.isEmpty() ? collection : entry.familyVerdictItems))) {
                     recordRestartedCausal(entry, Long.MIN_VALUE);
                  }

                  entry.terminalExecutingType = FamilyGained.State.QUEUED;
                  entry.strategy = null;
                  entry.missionBanType = FamilyGained.Dependency.NONE;
                  entry.orDefault = 0L;
                  entry.gainedTime = gainedTime;
                  entry.familyCausalItems = null;
                  entry.familyCellsTime = Long.MIN_VALUE;
                  entry.reason = "verdict "
                     + terminalExecutingType
                     + " contradicted by a re-park with unchanged geometry ("
                     + entry.verdictFamilyValue
                     + "/"
                     + 2
                     + "): "
                     + familyQueuedText;
                  this.append = "enqueue family=" + formatPositionText(familyQueuedValue) + " size=" + this.familyQueuedEntryMap.size() + " reason=" + entry.reason;
                  this.familyGainedCount++;
                  this.appendTickCounter++;
                  return true;
               }
            } else {
               boolean familyQueuedFlag7 = entry.connectionBuiltValue < 2
                  || this.familyGainedCount - entry.boundaryWalkableTime >= 1L << Math.min(entry.connectionBuiltValue, 12);
               boolean familyQueuedFlag8 = false;
               if (familyQueuedFlag6) {
                  familyQueuedText2 = (familyQueuedText2.isEmpty() ? "" : familyQueuedText2 + " - ")
                     + (entry.familyVerdictItems.size() - collection.size())
                     + " cell(s) left the family since the verdict (progress)";
               } else if ((familyQueuedFlag || familyQueuedFlag2) && !familyQueuedFlag7) {
                  if (++entry.verdictFamilyValue > 2) {
                     entry.terminalExecutingType = FamilyGained.State.TERMINAL_IMPOSSIBLE;
                     entry.strategy = EscalationsEscalation.Strategy.PROVE_IMPOSSIBLE;
                     entry.missionBanType = FamilyGained.Dependency.NONE;
                     this.familyQueuedTotalCount++;
                     entry.reason = "verdict "
                        + terminalExecutingType
                        + " contradicted "
                        + entry.verdictFamilyValue
                        + " times, the last by geometry churn beyond the causal budget ("
                        + familyQueuedText
                        + ")";
                     entry.gainedTime = gainedTime;
                     entry.boundaryWalkableTime = this.familyGainedCount;
                     entry.asLong2 = asLong;
                     entry.causalFamilyTime = -1L;
                     entry.asLong = Long.MIN_VALUE;
                     if (time != Long.MIN_VALUE && !containsPackedValue(entry.familyQueuedTimes, time) && entry.familyQueuedTimes.length < 4) {
                        entry.familyQueuedTimes = getTimesWithValue(entry.familyQueuedTimes, time);
                     }

                     if (familyQueuedFlag2) {
                        entry.familyCausalItems = new ArrayList<>(collection);
                        entry.familyCellsTime = time;
                     }

                     this.append = "terminal family=" + formatPositionText(familyQueuedValue) + " result=IMPOSSIBLE (" + entry.reason + ")";
                     this.appendTickCounter++;
                     return false;
                  }

                  familyQueuedText2 = familyQueuedText2 + " - the churn budget is spent, counted as contradiction " + entry.verdictFamilyValue + "/" + 2;
               } else if (familyQueuedFlag || familyQueuedFlag2) {
                  entry.verdictFamilyValue = 0;
                  familyQueuedFlag8 = true;
               }

               if ((familyQueuedFlag || familyQueuedFlag2) && !familyQueuedFlag6) {
                  entry.connectionBuiltValue++;
               }

               this.canFamilyVerdict(entry, (Collection<Long>)(collection != null && !collection.isEmpty() ? collection : entry.familyVerdictItems));
               entry.min = familyQueuedFlag8 ? entry.familyVerdictItems.size() : Math.min(entry.min, entry.familyVerdictItems.size());
               entry.terminalExecutingType = FamilyGained.State.QUEUED;
               entry.noneOf.clear();
               entry.strategy = null;
               entry.missionBanType = FamilyGained.Dependency.NONE;
               entry.orDefault = 0L;
               entry.impossibleQueuedValue = 0;
               entry.restartedSectionValue = 0;
               entry.reconsideredFamilyValue = 0;
               entry.familyCausalItems = null;
               entry.familyCellsTime = Long.MIN_VALUE;
               if (familyQueuedFlag8 || familyQueuedFlag6 && familyQueuedFlag) {
                  entry.reopensReopenedValue = 0;
                  entry.familyQueuedTimes = time != Long.MIN_VALUE ? new long[]{time} : new long[0];
               } else if (time != Long.MIN_VALUE && !containsPackedValue(entry.familyQueuedTimes, time) && entry.familyQueuedTimes.length < 4) {
                  entry.familyQueuedTimes = getTimesWithValue(entry.familyQueuedTimes, time);
               }

               entry.gainedTime = gainedTime;
               recordRestartedCausal(entry, asLong);
               if (time != Long.MIN_VALUE) {
                  entry.time = time;
               }

               entry.reason = "re-queued: " + familyQueuedText2 + " since the " + terminalExecutingType + " verdict (" + familyQueuedText + ")";
               this.append = "enqueue family=" + formatPositionText(familyQueuedValue) + " size=" + this.familyQueuedEntryMap.size() + " reason=" + entry.reason;
               this.familyGainedCount++;
               this.appendTickCounter++;
               return true;
            }
         }
      }
   }

   public FamilyGained.Transition getReopensTerminal(long reopensTerminalValue, long orDefault, String reopensTerminalText) {
      this.appendTickCounter++;
      FamilyGained.Entry entry = this.familyQueuedEntryMap.get(reopensTerminalValue);
      if (entry == null || entry.terminalExecutingType != FamilyGained.State.TERMINAL_IMPOSSIBLE) {
         return null;
      } else if (entry.reopensReopenedValue >= 2) {
         this.append = "terminal family=" + formatPositionText(reopensTerminalValue) + " result=IMPOSSIBLE (stands: parked on a column ban " + 2 + " times under this geometry)";
         return null;
      } else {
         entry.reopensReopenedValue++;
         FamilyGained.State terminalExecutingType = entry.terminalExecutingType;
         entry.terminalExecutingType = FamilyGained.State.WAITING;
         entry.missionBanType = FamilyGained.Dependency.COLUMN_BAN;
         entry.orDefault = orDefault;
         entry.noneOf.clear();
         entry.restartedSectionValue = 0;
         entry.strategy = null;
         entry.reason = reopensTerminalText == null ? "" : reopensTerminalText;
         this.append = "strategy-result family="
            + formatPositionText(reopensTerminalValue)
            + " result=PARKED_ON_COLUMN_BAN until="
            + orDefault
            + " ("
            + entry.reopensReopenedValue
            + "/"
            + 2
            + ": "
            + entry.reason
            + ")";
         return new FamilyGained.Transition(terminalExecutingType, entry.terminalExecutingType, null, entry.reason);
      }
   }

   public boolean canFlagFamilyWithText(long familyGainedValue, Collection<Long> collection, long gainedTime, long asLong, String familyGainedText) {
      return this.canFamilyQueued(familyGainedValue, collection, gainedTime, asLong, Long.MIN_VALUE, familyGainedText);
   }

   private boolean canFamilyVerdict(FamilyGained.Entry entry, Collection<Long> collection) {
      int[] restartedClimbValues = entry.restartedClimbValues;

      for (long familyVerdictValue : entry.familyVerdictItems) {
         this.familyCausalPositionMap.remove(familyVerdictValue, entry.connectionBuiltTime);
      }

      entry.familyVerdictItems = new ArrayList<>(collection);

      for (long botCrystalText : entry.familyVerdictItems) {
         Long put = this.familyCausalPositionMap.put(botCrystalText, entry.connectionBuiltTime);
         if (put != null && put != entry.connectionBuiltTime) {
            this.recordFamilySuperseded(put, botCrystalText);
         }
      }

      entry.restartedClimbValues = getValuesFromList(entry.familyVerdictItems);
      return !Arrays.equals(restartedClimbValues, entry.restartedClimbValues);
   }

   public FamilyGained.State getExecutingTerminalState(long executingTerminalValue) {
      FamilyGained.Entry entry = this.familyQueuedEntryMap.get(executingTerminalValue);
      return entry == null ? null : entry.terminalExecutingType;
   }

   private static EscalationsEscalation.Strategy getProveImpossible(FamilyGained.Entry entry) {
      for (EscalationsEscalation.Strategy proveImpossible : EscalationsEscalation.getStrategyOrder()) {
         if (proveImpossible == EscalationsEscalation.Strategy.PROVE_IMPOSSIBLE) {
            break;
         }

         if (!entry.noneOf.contains(proveImpossible)) {
            return proveImpossible;
         }
      }

      return null;
   }

   public List<FamilyGained.Family> getFamilyList() {
      ArrayList arrayList = new ArrayList();

      for (FamilyGained.Entry entry : this.familyQueuedEntryMap.values()) {
         arrayList.add(entry.getTimeGained());
      }

      return arrayList;
   }

   public int getExecutingProducer(
      boolean executingProducerFlag, boolean executingProducerFlag2, boolean executingProducerFlag3, boolean executingProducerFlag4, boolean executingProducerFlag5
   ) {
      this.appendTickCounter++;
      int executingProducer = 0;

      for (FamilyGained.Entry entry : this.familyQueuedEntryMap.values()) {
         boolean executingProducerFlag6;
         label63: {
            label52:
            if ((entry.terminalExecutingType != FamilyGained.State.DISPATCHED || executingProducerFlag4)
               && (entry.terminalExecutingType != FamilyGained.State.EXECUTING || executingProducerFlag)) {
               if (entry.terminalExecutingType == FamilyGained.State.WAITING) {
                  switch (entry.missionBanType) {
                     case SERVICE_MISSION:
                        if (!executingProducerFlag) {
                           break label52;
                        }
                        break;
                     case COMPLETION_MISSION:
                        if (!executingProducerFlag2) {
                           break label52;
                        }
                        break;
                     case MATERIAL:
                        if (executingProducerFlag3) {
                           break label52;
                        }
                        break;
                     case EPOCH:
                        if (!executingProducerFlag && !executingProducerFlag2) {
                           break label52;
                        }
                        break;
                     case COLUMN_BAN:
                        if (!executingProducerFlag5) {
                           break label52;
                        }
                        break;
                     default:
                        break label52;
                  }
               }

               executingProducerFlag6 = false;
               break label63;
            }

            executingProducerFlag6 = true;
         }

         boolean executingProducerFlag7 = executingProducerFlag6;
         if (executingProducerFlag7) {
            entry.terminalExecutingType = FamilyGained.State.QUEUED;
            entry.missionBanType = FamilyGained.Dependency.NONE;
            entry.reason = "unstuck: producer absent";
            executingProducer++;
         }
      }

      if (executingProducer > 0) {
         this.append = "unstick families=" + executingProducer;
      }

      return executingProducer;
   }

   public int[] getClonedValues(long cloneValue) {
      FamilyGained.Entry entry = this.familyQueuedEntryMap.get(cloneValue);
      return entry == null ? new int[0] : (entry.clone.length == 6 ? (int[])entry.clone.clone() : (int[])entry.restartedClimbValues.clone());
   }

   public int getBanExecutingDependencyCount(FamilyGained.Dependency dependency) {
      this.appendTickCounter++;
      int banExecuting = 0;

      for (FamilyGained.Entry entry : this.familyQueuedEntryMap.values()) {
         if (entry.terminalExecutingType == FamilyGained.State.WAITING && entry.missionBanType == dependency) {
            entry.terminalExecutingType = FamilyGained.State.QUEUED;
            entry.missionBanType = FamilyGained.Dependency.NONE;
            entry.reason = "woken: " + dependency + " producer gone";
            banExecuting++;
         }
      }

      if (banExecuting > 0) {
         this.append = "wake dependency=" + dependency + " families=" + banExecuting;
      }

      return banExecuting;
   }

   public List<Long> getCausalPositionList() {
      if (this.familyCausalPositionList.isEmpty()) {
         return List.of();
      } else {
         ArrayList arrayList = new ArrayList<>(this.familyCausalPositionList);
         this.familyCausalPositionList.clear();
         return arrayList;
      }
   }

   public FamilyGained.Transition getTerminalExecutingTransitionDetailed(
      long terminalExecutingValue, EscalationsEscalation.Strategy strategy, FamilyGained.StrategyResult strategyResult, FamilyGained.Dependency missionBanType, long orDefault, long gainedTime
   ) {
      FamilyGained.Entry entry = this.familyQueuedEntryMap.get(terminalExecutingValue);
      if (entry == null) {
         return null;
      } else {
         this.familyGainedCount++;
         if (entry.terminalExecutingType == FamilyGained.State.DISPATCHED && gainedTime != Long.MIN_VALUE) {
            entry.gainedTime = gainedTime;
         }

         if (entry.terminalExecutingType != FamilyGained.State.DISPATCHED) {
            this.terminalExecutingCount++;
            this.append = "strategy-result family="
               + formatPositionText(terminalExecutingValue)
               + " strategy="
               + strategy
               + " result="
               + strategyResult
               + " ignored: stale (family is "
               + entry.terminalExecutingType
               + ")";
            return null;
         } else {
            this.appendTickCounter++;
            FamilyGained.State terminalExecutingType = entry.terminalExecutingType;
            if (strategy != null) {
               entry.noneOf.add(strategy);
            }

            switch (strategyResult) {
               case BUILT_PENDING:
                  entry.terminalExecutingType = FamilyGained.State.EXECUTING;
                  entry.missionBanType = FamilyGained.Dependency.SERVICE_MISSION;
                  entry.orDefault = orDefault;
                  entry.reason = "service mission " + orDefault + " builds the connection";
                  return this.getStrategyFamily(terminalExecutingValue, strategy, strategyResult, new FamilyGained.Transition(terminalExecutingType, entry.terminalExecutingType, null, entry.reason));
               case BECAME_REACHABLE:
                  entry.terminalExecutingType = FamilyGained.State.TERMINAL_REACHABLE;
                  entry.missionBanType = FamilyGained.Dependency.NONE;
                  this.boundaryWalkableCount++;
                  entry.boundaryWalkableTime = this.familyGainedCount;
                  entry.reason = "a boundary pose is walkable";
                  this.append = "terminal family=" + formatPositionText(terminalExecutingValue) + " result=REACHABLE";
                  return new FamilyGained.Transition(terminalExecutingType, entry.terminalExecutingType, null, entry.reason);
               case TEMPORARILY_BLOCKED:
                  if (missionBanType != null && missionBanType != FamilyGained.Dependency.NONE) {
                     entry.noneOf.remove(strategy);
                     entry.terminalExecutingType = FamilyGained.State.WAITING;
                     entry.missionBanType = missionBanType;
                     entry.orDefault = orDefault;
                     entry.reason = "waiting for " + missionBanType;
                     return this.getStrategyFamily(
                        terminalExecutingValue, strategy, strategyResult, new FamilyGained.Transition(terminalExecutingType, entry.terminalExecutingType, strategy, entry.reason)
                     );
                  }

                  return this.getTransitionForStrategyResult(terminalExecutingValue, strategy, FamilyGained.StrategyResult.NO_CANDIDATE, FamilyGained.Dependency.NONE, 0L);
               default:
                  EscalationsEscalation.Strategy proveImpossible = getProveImpossible(entry);
                  if (proveImpossible == null) {
                     entry.terminalExecutingType = FamilyGained.State.TERMINAL_IMPOSSIBLE;
                     entry.strategy = EscalationsEscalation.Strategy.PROVE_IMPOSSIBLE;
                     entry.missionBanType = FamilyGained.Dependency.NONE;
                     this.familyQueuedTotalCount++;
                     entry.boundaryWalkableTime = this.familyGainedCount;
                     entry.reason = "every constructive strategy tried under epoch " + entry.gainedTime + " (last " + strategy + " -> " + strategyResult + ")";
                     this.getStrategyFamily(terminalExecutingValue, strategy, strategyResult, null);
                     this.append = "terminal family=" + formatPositionText(terminalExecutingValue) + " result=IMPOSSIBLE (" + entry.reason + ")";
                     return new FamilyGained.Transition(terminalExecutingType, entry.terminalExecutingType, null, entry.reason);
                  } else {
                     entry.terminalExecutingType = FamilyGained.State.QUEUED;
                     entry.strategy = null;
                     entry.missionBanType = FamilyGained.Dependency.NONE;
                     entry.reason = strategy + " -> " + strategyResult + "; next " + proveImpossible;
                     return this.getStrategyFamily(
                        terminalExecutingValue, strategy, strategyResult, new FamilyGained.Transition(terminalExecutingType, entry.terminalExecutingType, proveImpossible, entry.reason)
                     );
                  }
            }
         }
      }
   }

   public int getFamilyReconsidered(
      long gainedTime, long causalFamilyTime, LongUnaryOperator longUnaryOperator, LongPredicate longPredicate, boolean familyReconsideredFlag, boolean familyReconsideredFlag2
   ) {
      this.appendTickCounter++;
      this.familyGainedCount++;
      this.admittedDispatcherCount++;
      int familyReconsidered = 0;
      int familyReconsideredCounter = 0;
      int familyReconsideredCounter2 = 0;
      long familyReconsideredValue = 0L;

      for (FamilyGained.Entry entry : this.familyQueuedEntryMap.values()) {
         if (!entry.familyVerdictItems.isEmpty()) {
            boolean familyReconsideredFlag3 = entry.terminalExecutingType == FamilyGained.State.WAITING && entry.missionBanType == FamilyGained.Dependency.EPOCH;
            boolean familyReconsideredFlag4 = false;
            boolean familyReconsideredFlag5 = false;
            boolean familyReconsideredFlag6 = false;
            boolean familyReconsideredFlag7 = false;
            boolean familyReconsideredFlag8 = false;
            long asLong = Long.MIN_VALUE;
            if (entry.terminalExecutingType == FamilyGained.State.TERMINAL_IMPOSSIBLE) {
               if (causalFamilyTime >= 0L && entry.causalFamilyTime == causalFamilyTime) {
                  asLong = entry.asLong;
               } else {
                  asLong = longUnaryOperator.applyAsLong(entry.connectionBuiltTime);
                  entry.asLong = asLong;
                  entry.causalFamilyTime = causalFamilyTime;
               }

               familyReconsideredFlag8 = asLong != Long.MIN_VALUE && entry.asLong2 != Long.MIN_VALUE && asLong != entry.asLong2;
               if (entry.familyCausalItems != null) {
                  this.recordCausalEntry(entry);
               }

               familyReconsideredFlag6 = entry.familyCausalItems != null;
               boolean familyReconsideredFlag9 = familyReconsideredFlag8 || familyReconsideredFlag6;
               boolean familyReconsideredFlag10 = false;
               boolean familyReconsideredFlag11 = false;
               boolean familyReconsideredFlag12 = false;
               if (familyReconsideredFlag9 && !this.canCausalWalkable(entry)) {
                  boolean familyReconsideredFlag13 = familyReconsideredFlag && this.admittedDispatcherCount - entry.admittedDispatcherTime >= 1L << Math.min(entry.causalFamilyCount, 12);
                  familyReconsideredFlag7 = !familyReconsideredFlag13 && familyReconsideredFlag2 && entry.familyReconsideredCount < 2;
                  familyReconsideredFlag12 = familyReconsideredFlag13 || familyReconsideredFlag7;
                  if (!familyReconsideredFlag12) {
                     familyReconsideredFlag10 = entry.reconsideredFamilyValue < 3 && longPredicate.test(entry.connectionBuiltTime);
                     familyReconsideredFlag11 = true;
                     if (!familyReconsideredFlag10) {
                        familyReconsideredCounter++;
                        familyReconsideredValue = entry.connectionBuiltTime;
                        if (familyReconsideredFlag6) {
                           familyReconsideredCounter2++;
                        }
                        continue;
                     }

                     familyReconsideredFlag9 = false;
                     familyReconsideredFlag6 = false;
                  }
               }

               if (!familyReconsideredFlag11 && !familyReconsideredFlag9) {
                  familyReconsideredFlag10 = entry.reconsideredFamilyValue < 3 && longPredicate.test(entry.connectionBuiltTime);
               }

               familyReconsideredFlag5 = familyReconsideredFlag10 && !familyReconsideredFlag9;
               familyReconsideredFlag4 = familyReconsideredFlag9 || familyReconsideredFlag5;
               if (familyReconsideredFlag12) {
                  entry.admittedDispatcherTime = this.admittedDispatcherCount;
               }

               if (familyReconsideredFlag7) {
                  entry.familyReconsideredCount++;
               }
            }

            if (familyReconsideredFlag3 || familyReconsideredFlag4) {
               if (familyReconsideredFlag4) {
                  if (familyReconsideredFlag5) {
                     entry.reconsideredFamilyValue++;
                     if (familyReconsideredFlag8) {
                        entry.verdictFamilyValue = 0;
                        entry.reopensReopenedValue = 0;
                        entry.familyQueuedTimes = entry.time != Long.MIN_VALUE ? new long[]{entry.time} : new long[0];
                     }
                  } else {
                     entry.causalFamilyCount++;
                     entry.reconsideredFamilyValue = 0;
                     entry.verdictFamilyValue = 0;
                     entry.reopensReopenedValue = 0;
                     entry.familyQueuedTimes = entry.time != Long.MIN_VALUE ? new long[]{entry.time} : new long[0];
                  }
               }

               if (familyReconsideredFlag6) {
                  this.canFamilyVerdict(entry, entry.familyCausalItems);
                  if (entry.familyCellsTime != Long.MIN_VALUE) {
                     entry.time = entry.familyCellsTime;
                     entry.familyQueuedTimes = new long[]{entry.familyCellsTime};
                  }

                  entry.familyCausalItems = null;
                  entry.familyCellsTime = Long.MIN_VALUE;
               }

               entry.terminalExecutingType = FamilyGained.State.QUEUED;
               entry.noneOf.clear();
               entry.strategy = null;
               entry.missionBanType = FamilyGained.Dependency.NONE;
               entry.gainedTime = gainedTime;
               entry.impossibleQueuedValue = 0;
               entry.restartedSectionValue = 0;
               entry.connectionBuiltValue = 0;
               entry.connectionBuiltValue2 = 0;
               entry.min = entry.familyVerdictItems.size();
               if (familyReconsideredFlag6) {
                  recordRestartedCausal(entry, Long.MIN_VALUE);
               } else if (asLong != Long.MIN_VALUE) {
                  recordRestartedCausal(entry, asLong);
               }

               entry.reason = familyReconsideredFlag5
                  ? "reconsidered: the walk component serves the family now (label reopen "
                     + entry.reconsideredFamilyValue
                     + "/"
                     + 3
                     + (familyReconsideredFlag8 ? ", climbed under the changed geometry" : "")
                     + ")"
                  : (
                     familyReconsideredFlag4
                        ? "reconsidered: the "
                           + (familyReconsideredFlag6 ? "family gained cells" : "geometry around the family or its approach changed")
                           + " (causal reopen "
                           + entry.causalFamilyCount
                           + (
                              familyReconsideredFlag7
                                 ? ", admitted before a pause " + entry.familyReconsideredCount + "/" + 2
                                 : (familyReconsideredFlag && this.admittedDispatcherCount == entry.admittedDispatcherTime ? ", admitted: the dispatcher is idle" : "")
                           )
                           + ")"
                        : "reconsidered after the epoch advanced"
                  );
               familyReconsidered++;
            }
         }
      }

      if (familyReconsidered > 0) {
         this.append = "wake dependency=EPOCH families=" + familyReconsidered;
      } else if (familyReconsideredCounter > 0) {
         this.append = "deferred-causal family="
            + formatPositionText(familyReconsideredValue)
            + (familyReconsideredCounter > 1 ? " (+" + (familyReconsideredCounter - 1) + ")" : "")
            + ": the "
            + (familyReconsideredCounter2 == familyReconsideredCounter ? "family gained cells" : (familyReconsideredCounter2 > 0 ? "geometry changed or the family gained cells" : "geometry changed"))
            + " again after repeated causal reopens that ended IMPOSSIBLE - reopened after progress elsewhere or when the dispatcher is idle";
      }

      return familyReconsidered;
   }

   public Long getConnectionBuilt(LongPredicate longPredicate) {
      FamilyGained.Entry entry = null;

      for (FamilyGained.Entry entry2 : this.familyQueuedEntryMap.values()) {
         if (entry2.terminalExecutingType == FamilyGained.State.QUEUED
            && longPredicate.test(entry2.connectionBuiltTime)
            && (entry == null || entry2.sectionTime < entry.sectionTime)) {
            entry = entry2;
         }
      }

      return entry == null ? null : entry.connectionBuiltTime;
   }

   public boolean canFlagThreeFamilyFlags(boolean familyGainedFlag, boolean familyGainedFlag2, boolean familyGainedFlag3) {
      return this.canFlagFourFamilyFlags(familyGainedFlag, familyGainedFlag2, familyGainedFlag3, true);
   }

   public int getFamilyGainedTotalValue() {
      return this.terminalExecutingCount;
   }

   public EscalationsEscalation.Strategy getStandProveStrategy(long standProveValue, long gainedTime) {
      return this.getRestartedSection(standProveValue, gainedTime, standProveValue3 -> Long.MIN_VALUE, Long.MIN_VALUE);
   }

   public boolean isExecutingTerminal() {
      for (FamilyGained.Entry entry : this.familyQueuedEntryMap.values()) {
         if (entry.terminalExecutingType == FamilyGained.State.QUEUED
            || entry.terminalExecutingType == FamilyGained.State.DISPATCHED
            || entry.terminalExecutingType == FamilyGained.State.EXECUTING) {
            return false;
         }
      }

      return true;
   }

   public int getValueWithOperatorAndPredicate(long familyGainedValue, LongUnaryOperator longUnaryOperator, LongPredicate longPredicate) {
      return this.getValueForPositionPairWithOperator(familyGainedValue, -1L, longUnaryOperator, longPredicate, false);
   }

   public String formatFamilyGained() {
      return String.format(
         Locale.ROOT,
         "AccessQueue families=%d owed=%d queued=%d dispatched=%d executing=%d waiting=%d built=%d reachable=%d impossible=%d dispatches=%d deadlocks=%d capacityRefused=%d stale=%d reclimbRefused=%d",
         this.familyQueuedEntryMap.size(),
         this.getRestartedSectionCountValue(),
         this.getExecutingTerminalCountForState(FamilyGained.State.QUEUED),
         this.getExecutingTerminalCountForState(FamilyGained.State.DISPATCHED),
         this.getExecutingTerminalCountForState(FamilyGained.State.EXECUTING),
         this.getExecutingTerminalCountForState(FamilyGained.State.WAITING),
         this.connectionSupersededCount,
         this.boundaryWalkableCount,
         this.familyQueuedTotalCount,
         this.walkableBoundaryCount,
         this.banMissionCount,
         this.familyQueuedEventCount,
         this.terminalExecutingCount,
         this.reclimbIgnoredCount
      );
   }

   private boolean isTerminalCausal() {
      for (Map.Entry entry : this.familyQueuedEntryMap.entrySet()) {
         FamilyGained.State terminalExecutingType = ((FamilyGained.Entry)entry.getValue()).terminalExecutingType;
         if (terminalExecutingType == FamilyGained.State.TERMINAL_BUILT || terminalExecutingType == FamilyGained.State.TERMINAL_REACHABLE) {
            this.recordCausalPosition((Long)entry.getKey());
            return true;
         }
      }

      for (Map.Entry entryItem : this.familyQueuedEntryMap.entrySet()) {
         if (((FamilyGained.Entry)entryItem.getValue()).isTerminalExecuting() && ((FamilyGained.Entry)entryItem.getValue()).familyCausalItems == null) {
            this.recordCausalPosition((Long)entryItem.getKey());
            return true;
         }
      }

      for (Map.Entry entry3 : this.familyQueuedEntryMap.entrySet()) {
         if (((FamilyGained.Entry)entry3.getValue()).isTerminalExecuting()) {
            this.recordCausalPosition((Long)entry3.getKey());
            return true;
         }
      }

      return false;
   }

   public int getValueForPosition(long familyGainedValue) {
      return this.getValueWithPredicate(familyGainedValue, familyGainedValue2 -> true);
   }

   public int getTerminalExecutingCountValue() {
      return this.familyQueuedCount;
   }

   private boolean canCausalWalkable(FamilyGained.Entry entry) {
      return entry.causalFamilyCount < 2 || this.familyGainedCount - entry.boundaryWalkableTime >= 1L << Math.min(entry.causalFamilyCount, 12);
   }

   public FamilyGained.Transition getTerminalExecutingTransitionWithText(long terminalExecutingValue, String terminalExecutingText) {
      this.appendTickCounter++;
      FamilyGained.Entry entry = this.familyQueuedEntryMap.get(terminalExecutingValue);
      if (entry != null && !entry.isTerminalExecuting()) {
         FamilyGained.State terminalExecutingType = entry.terminalExecutingType;
         entry.terminalExecutingType = FamilyGained.State.TERMINAL_IMPOSSIBLE;
         entry.strategy = EscalationsEscalation.Strategy.PROVE_IMPOSSIBLE;
         entry.missionBanType = FamilyGained.Dependency.NONE;
         this.familyQueuedTotalCount++;
         entry.boundaryWalkableTime = this.familyGainedCount;
         entry.reason = terminalExecutingText == null ? "" : terminalExecutingText;
         this.append = "terminal family=" + formatPositionText(terminalExecutingValue) + " result=IMPOSSIBLE (" + entry.reason + ")";
         return new FamilyGained.Transition(terminalExecutingType, entry.terminalExecutingType, null, entry.reason);
      } else {
         return null;
      }
   }

   public FamilyGained.Transition getTransitionForStrategyResult(
      long familyGainedValue, EscalationsEscalation.Strategy strategy, FamilyGained.StrategyResult strategyResult, FamilyGained.Dependency dependency, long orDefault
   ) {
      return this.getTerminalExecutingTransitionDetailed(familyGainedValue, strategy, strategyResult, dependency, orDefault, Long.MIN_VALUE);
   }

   private void recordFamilySuperseded(long familySupersededValue, long botCrystalText) {
      FamilyGained.Entry entry = this.familyQueuedEntryMap.get(familySupersededValue);
      if (entry != null) {
         entry.familyVerdictItems.remove(botCrystalText);
         entry.restartedClimbValues = getValuesFromList(entry.familyVerdictItems);
         if (entry.familyVerdictItems.isEmpty()) {
            if (!entry.isTerminalExecuting()) {
               this.connectionSupersededCount++;
            }

            entry.terminalExecutingType = FamilyGained.State.TERMINAL_BUILT;
            entry.missionBanType = FamilyGained.Dependency.NONE;
            entry.clone = new int[0];
            entry.familyCausalItems = null;
            entry.familyCellsTime = Long.MIN_VALUE;
            entry.reason = "superseded: its cells moved to another family";
         }
      }
   }

   public EscalationsEscalation.Strategy getStandProveStrategyForTriple(long standProveValue, long gainedTime, long standProveValue3) {
      return this.getRestartedSection(standProveValue, gainedTime, stampKeyx -> standProveValue3, Long.MIN_VALUE);
   }

   private boolean canConnectionBuilt(FamilyGained.Entry entry, Collection<Long> collection) {
      for (long connectionBuiltValue : collection) {
         Long long2 = this.familyCausalPositionMap.get(connectionBuiltValue);
         if (long2 == null || long2 != entry.connectionBuiltTime) {
            return true;
         }
      }

      return false;
   }

   public boolean canFlagFamilyShort(long familyGainedValue, Collection<Long> collection, long gainedTime, String familyGainedText) {
      return this.canFlagFamilyWithText(familyGainedValue, collection, gainedTime, Long.MIN_VALUE, familyGainedText);
   }

   private static void recordRestartedCausal(FamilyGained.Entry entry, long asLong) {
      entry.asLong2 = asLong;
      entry.causalFamilyTime = -1L;
      entry.asLong = Long.MIN_VALUE;
      entry.clone = (int[])entry.restartedClimbValues.clone();
   }

   public boolean canBanMission(boolean banMissionFlag, boolean banMissionFlag2, boolean banMissionFlag3, boolean banMissionFlag4, boolean banMissionFlag5) {
      boolean banMission = false;

      for (FamilyGained.Entry entry : this.familyQueuedEntryMap.values()) {
         if (!entry.isTerminalExecuting()) {
            banMission = true;
            switch (entry.terminalExecutingType) {
               case QUEUED:
                  return false;
               case DISPATCHED:
                  if (banMissionFlag4) {
                     return false;
                  }
                  break;
               case EXECUTING:
                  if (banMissionFlag) {
                     return false;
                  }
                  break;
               case WAITING:
                  if (entry.missionBanType == FamilyGained.Dependency.SERVICE_MISSION && banMissionFlag) {
                     return false;
                  }

                  if (entry.missionBanType == FamilyGained.Dependency.COMPLETION_MISSION && banMissionFlag2) {
                     return false;
                  }

                  if (entry.missionBanType == FamilyGained.Dependency.MATERIAL && !banMissionFlag3) {
                     return false;
                  }

                  if (entry.missionBanType == FamilyGained.Dependency.EPOCH && (banMissionFlag || banMissionFlag2)) {
                     return false;
                  }

                  if (entry.missionBanType == FamilyGained.Dependency.COLUMN_BAN && banMissionFlag5) {
                     return false;
                  }
            }
         }
      }

      if (banMission) {
         this.banMissionCount++;
      }

      return banMission;
   }

   public int getBanMissionCountValue() {
      return this.familyQueuedTotalCount;
   }

   public Long getNumberForPosition(long numberValue) {
      return this.familyCausalPositionMap.get(numberValue);
   }

   public String getAppendText() {
      return this.append;
   }

   public boolean isFamilyFlagSet() {
      return this.getSectionTime() != null;
   }

   public int getValueFromFourFlags(boolean familyGainedFlag, boolean familyGainedFlag2, boolean familyGainedFlag3, boolean familyGainedFlag4) {
      return this.getExecutingProducer(familyGainedFlag, familyGainedFlag2, familyGainedFlag3, familyGainedFlag4, false);
   }

   public FamilyGained() {
      this.familyCausalPositionMap = new HashMap<>();
      this.familyCausalPositionList = new ArrayList<>();
      this.append = "";
   }

   public static String formatPositionText(long familyGainedValue) {
      return ProofSupportUtils.getValueInt(familyGainedValue) + "," + ProofSupportUtils.getValue(familyGainedValue) + "," + ProofSupportUtils.getValueForLong(familyGainedValue);
   }

   private static long[] getTimesWithValue(long[] familyGainedValues, long familyGainedValue) {
      long[] copyOf = Arrays.copyOf(familyGainedValues, familyGainedValues.length + 1);
      copyOf[familyGainedValues.length] = familyGainedValue;
      return copyOf;
   }

   public long getFamilyGainedTime() {
      return this.appendTickCounter;
   }

   public int getTerminalCausal() {
      int terminalCausal = 0;

      for (FamilyGained.Entry entry : this.familyQueuedEntryMap.values()) {
         if (entry.terminalExecutingType == FamilyGained.State.TERMINAL_IMPOSSIBLE
            && (entry.familyCausalItems != null || entry.asLong != Long.MIN_VALUE && entry.asLong2 != Long.MIN_VALUE && entry.asLong != entry.asLong2)) {
            terminalCausal++;
         }
      }

      return terminalCausal;
   }

   public FamilyGained.Transition getExecutingTerminalTransition(long executingTerminalValue) {
      this.appendTickCounter++;

      for (FamilyGained.Entry entry : this.familyQueuedEntryMap.values()) {
         if (entry.terminalExecutingType == FamilyGained.State.EXECUTING && entry.orDefault == executingTerminalValue) {
            FamilyGained.State terminalExecutingType = entry.terminalExecutingType;
            if (entry.strategy != null) {
               entry.noneOf.remove(entry.strategy);
            }

            entry.terminalExecutingType = FamilyGained.State.QUEUED;
            entry.missionBanType = FamilyGained.Dependency.NONE;
            entry.reason = "service mission " + executingTerminalValue + " abandoned (geometry changed under it); the rung is retried";
            this.append = "strategy-result family="
               + formatPositionText(entry.connectionBuiltTime)
               + " strategy="
               + entry.strategy
               + " result=MISSION_ABANDONED next="
               + getProveImpossible(entry);
            return new FamilyGained.Transition(terminalExecutingType, entry.terminalExecutingType, getProveImpossible(entry), entry.reason);
         }
      }

      this.append = "mission " + executingTerminalValue + " abandoned: no family owns it any more (superseded, retired or evicted)";
      return null;
   }

   public void updateWalkableBoundary() {
      this.appendTickCounter++;
      this.familyQueuedEntryMap.clear();
      this.familyCausalPositionMap.clear();
      this.restartedSectionTime = 0L;
      this.familyGainedCount = 0L;
      this.admittedDispatcherCount = 0L;
      this.familyQueuedCount = this.walkableBoundaryCount = this.connectionSupersededCount = this.boundaryWalkableCount = this.familyQueuedTotalCount = this.banMissionCount = this.familyQueuedEventCount = this.terminalExecutingCount = this.reclimbIgnoredCount = 0;
      this.append = "";
   }

   private boolean canFamilyVerdictAlternate(FamilyGained.Entry entry, Collection<Long> collection) {
      return collection.size() == entry.familyVerdictItems.size() && !this.canConnectionBuilt(entry, collection);
   }

   public int getRestartedSectionCountValue() {
      int familyGainedCounter = 0;

      for (FamilyGained.Entry entry : this.familyQueuedEntryMap.values()) {
         if (!entry.isTerminalExecuting()) {
            familyGainedCounter++;
         }
      }

      return familyGainedCounter;
   }

   public int getValueForPositionPair(long familyGainedValue, long familyGainedValue2, LongPredicate longPredicate) {
      return this.getValueWithOperatorAndPredicate(familyGainedValue, familyGainedValue3 -> familyGainedValue2, longPredicate);
   }

   public int getReclimbIgnoredCount() {
      return this.reclimbIgnoredCount;
   }

   public int getValueFromThreeFlags(boolean familyGainedFlag, boolean familyGainedFlag2, boolean familyGainedFlag3) {
      return this.getValueFromFourFlags(familyGainedFlag, familyGainedFlag2, familyGainedFlag3, true);
   }

   public int getBoundaryWalkableCount() {
      return this.boundaryWalkableCount;
   }

   public int getExecutingTerminalCountForState(FamilyGained.State state) {
      int executingTerminal = 0;

      for (FamilyGained.Entry entry : this.familyQueuedEntryMap.values()) {
         if (entry.terminalExecutingType == state) {
            executingTerminal++;
         }
      }

      return executingTerminal;
   }

   public int getValueForPositionPairWithOperator(long familyGainedValue, long causalFamilyTime, LongUnaryOperator longUnaryOperator, LongPredicate longPredicate, boolean familyGainedFlag) {
      return this.getFamilyReconsidered(familyGainedValue, causalFamilyTime, longUnaryOperator, longPredicate, familyGainedFlag, false);
   }

   public EscalationsEscalation.Strategy getRestartedSection(long restartedSectionValue, long gainedTime, LongUnaryOperator longUnaryOperator, long time) {
      this.appendTickCounter++;
      FamilyGained.Entry entry = this.familyQueuedEntryMap.get(restartedSectionValue);
      if (entry != null && entry.terminalExecutingType == FamilyGained.State.QUEUED) {
         boolean restartedSectionFlag = time != Long.MIN_VALUE && entry.time != Long.MIN_VALUE && time != entry.time && !entry.noneOf.isEmpty();
         if (time != Long.MIN_VALUE && (entry.noneOf.isEmpty() || restartedSectionFlag && entry.restartedSectionValue < 2)) {
            entry.time = time;
            if (!containsPackedValue(entry.familyQueuedTimes, time) && entry.familyQueuedTimes.length < 4) {
               entry.familyQueuedTimes = getTimesWithValue(entry.familyQueuedTimes, time);
            }
         }

         if (entry.noneOf.isEmpty()) {
            entry.clone = (int[])entry.restartedClimbValues.clone();
         }

         long applyAsLong = longUnaryOperator.applyAsLong(restartedSectionValue);
         String restartedSectionText = "";
         if (applyAsLong != Long.MIN_VALUE) {
            if (entry.noneOf.isEmpty() || entry.asLong2 == Long.MIN_VALUE) {
               recordRestartedCausal(entry, applyAsLong);
            } else if ((applyAsLong != entry.asLong2 || restartedSectionFlag) && entry.restartedSectionValue < 2) {
               entry.restartedSectionValue++;
               entry.noneOf.clear();
               entry.clone = (int[])entry.restartedClimbValues.clone();
               recordRestartedCausal(entry, longUnaryOperator.applyAsLong(restartedSectionValue));
               restartedSectionText = " (climb restarted: the "
                  + (restartedSectionFlag ? "body moved to another section" : "geometry changed")
                  + " under it, "
                  + entry.restartedSectionValue
                  + "/"
                  + 2
                  + ")";
            }
         }

         entry.sectionTime = ++this.restartedSectionTime;
         this.familyGainedCount++;
         entry.attempts++;
         this.walkableBoundaryCount++;

         for (EscalationsEscalation.Strategy strategy : EscalationsEscalation.getStrategyOrder()) {
            if (strategy == EscalationsEscalation.Strategy.PROVE_IMPOSSIBLE) {
               break;
            }

            if (!entry.noneOf.contains(strategy)) {
               entry.terminalExecutingType = FamilyGained.State.DISPATCHED;
               entry.strategy = strategy;
               entry.gainedTime = gainedTime;
               this.append = "dispatch family=" + formatPositionText(restartedSectionValue) + " strategy=" + strategy + " attempts=" + entry.attempts + restartedSectionText;
               return strategy;
            }
         }

         entry.terminalExecutingType = FamilyGained.State.TERMINAL_IMPOSSIBLE;
         entry.strategy = EscalationsEscalation.Strategy.PROVE_IMPOSSIBLE;
         entry.gainedTime = gainedTime;
         entry.boundaryWalkableTime = this.familyGainedCount;
         entry.reason = "every constructive strategy tried under epoch " + gainedTime;
         this.familyQueuedTotalCount++;
         this.append = "terminal family=" + formatPositionText(restartedSectionValue) + " result=IMPOSSIBLE (" + entry.reason + ")";
         return null;
      } else {
         return null;
      }
   }

   public boolean canFlagFourFamilyFlags(boolean familyGainedFlag, boolean familyGainedFlag2, boolean familyGainedFlag3, boolean familyGainedFlag4) {
      return this.canBanMission(familyGainedFlag, familyGainedFlag2, familyGainedFlag3, familyGainedFlag4, false);
   }

   public static enum Dependency {
      NONE,
      SERVICE_MISSION,
      COMPLETION_MISSION,
      MATERIAL,
      EPOCH,
      COLUMN_BAN;
   }

   private static final class Entry {
      final long connectionBuiltTime;
      long asLong;
      List<Long> familyVerdictItems;
      int impossibleQueuedValue;
      long sectionTime;
      long[] familyQueuedTimes;
      int[] restartedClimbValues;
      List<Long> familyCausalItems;
      long gainedTime;
      int connectionBuiltValue;
      long familyCellsTime;
      long boundaryWalkableTime;
      String reason;
      int connectionBuiltValue2;
      long time;
      int reopensReopenedValue;
      int[] clone;
      EscalationsEscalation.Strategy strategy;
      int reconsideredFamilyValue;
      FamilyGained.Dependency missionBanType;
      FamilyGained.State terminalExecutingType = FamilyGained.State.QUEUED;
      int familyReconsideredCount;
      long causalFamilyTime;
      long admittedDispatcherTime;
      int min;
      long orDefault;
      int causalFamilyCount;
      int attempts;
      final EnumSet<EscalationsEscalation.Strategy> noneOf = EnumSet.noneOf(EscalationsEscalation.Strategy.class);
      long asLong2;
      int restartedSectionValue;
      int verdictFamilyValue;

      FamilyGained.Family getTimeGained() {
         return new FamilyGained.Family(
            this.connectionBuiltTime,
            Collections.unmodifiableList(new ArrayList<>(this.familyVerdictItems)),
            this.terminalExecutingType,
            this.strategy,
            Collections.unmodifiableSet(EnumSet.copyOf(this.noneOf.isEmpty() ? EnumSet.noneOf(EscalationsEscalation.Strategy.class) : this.noneOf)),
            this.missionBanType,
            this.orDefault,
            this.gainedTime,
            this.attempts,
            this.sectionTime,
            this.reason
         );
      }

      Entry(long connectionBuiltTime, List<Long> familyVerdictItems, long gainedTime) {
         this.missionBanType = FamilyGained.Dependency.NONE;
         this.min = Integer.MAX_VALUE;
         this.familyCellsTime = Long.MIN_VALUE;
         this.asLong2 = Long.MIN_VALUE;
         this.causalFamilyTime = -1L;
         this.asLong = Long.MIN_VALUE;
         this.admittedDispatcherTime = -4611686018427387904L;
         this.time = Long.MIN_VALUE;
         this.familyQueuedTimes = new long[0];
         this.restartedClimbValues = new int[0];
         this.clone = new int[0];
         this.reason = "";
         this.connectionBuiltTime = connectionBuiltTime;
         this.familyVerdictItems = familyVerdictItems;
         this.gainedTime = gainedTime;
      }

      boolean isTerminalExecuting() {
         return this.terminalExecutingType == FamilyGained.State.TERMINAL_BUILT
            || this.terminalExecutingType == FamilyGained.State.TERMINAL_REACHABLE
            || this.terminalExecutingType == FamilyGained.State.TERMINAL_IMPOSSIBLE;
      }
   }

   public record Family(
      long root,
      List<Long> members,
      FamilyGained.State state,
      EscalationsEscalation.Strategy current,
      Set<EscalationsEscalation.Strategy> tried,
      FamilyGained.Dependency dependency,
      long dependencyKey,
      long epoch,
      int attempts,
      long lastDispatchSeq,
      String why
   ) {
      public boolean isQueuedState() {
         return this.state == FamilyGained.State.QUEUED;
      }

      public boolean isTerminalState() {
         return this.state == FamilyGained.State.TERMINAL_BUILT
            || this.state == FamilyGained.State.TERMINAL_REACHABLE
            || this.state == FamilyGained.State.TERMINAL_IMPOSSIBLE;
      }
   }

   public static enum State {
      QUEUED,
      DISPATCHED,
      EXECUTING,
      WAITING,
      TERMINAL_BUILT,
      TERMINAL_REACHABLE,
      TERMINAL_IMPOSSIBLE;
   }

   public static enum StrategyResult {
      BUILT_PENDING,
      BECAME_REACHABLE,
      NO_CANDIDATE,
      NOT_APPLICABLE,
      IMPOSSIBLE_FOR_STRATEGY,
      TEMPORARILY_BLOCKED;
   }

   public record Transition(FamilyGained.State from, FamilyGained.State to, EscalationsEscalation.Strategy next, String why) {
   }
}
