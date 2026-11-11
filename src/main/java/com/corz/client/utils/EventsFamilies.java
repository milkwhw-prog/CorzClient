package com.corz.client.utils;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public final class EventsFamilies {
   private static final EventsFamilies.Event[] appendEvents = EventsFamilies.Event.values();
   private final int[] eventsFamiliesValues;
   private final LinkedHashMap<Long, long[]> familiesEventsLongArrayMap;
   private long familiesEpochTime = 1L;
   private int eventsFamiliesValue;
   private final int[] eventsNonValues;
   private static final EventsFamilies.NonEvent[] appendNonEvents = EventsFamilies.NonEvent.values();

   public void recordNonEvent(EventsFamilies.NonEvent nonEvent) {
      this.eventsNonValues[nonEvent.ordinal()]++;
   }

   public int getNonEvents(EventsFamilies.NonEvent nonEvent) {
      return this.eventsNonValues[nonEvent.ordinal()];
   }

   public int getTotalFamiliesValue() {
      return this.familiesEventsLongArrayMap.size();
   }

   public int getEventsFamilies(EventsFamilies.Event event) {
      return this.eventsFamiliesValues[event.ordinal()];
   }

   public long getTime(long eventsFamiliesValue) {
      long[] eventsFamiliesValues = this.familiesEventsLongArrayMap.get(eventsFamiliesValue);
      return eventsFamiliesValues == null ? 0L : eventsFamiliesValues[0];
   }

   public int getFamiliesEvents(long familiesEventsValue) {
      long[] familiesEventsValues = this.familiesEventsLongArrayMap.remove(familiesEventsValue);
      if (familiesEventsValues == null || familiesEventsValues[0] != this.familiesEpochTime) {
         familiesEventsValues = new long[]{this.familiesEpochTime, 0L};
         this.eventsFamiliesValue++;
      }

      familiesEventsValues[1]++;
      this.familiesEventsLongArrayMap.put(familiesEventsValue, familiesEventsValues);
      return (int)familiesEventsValues[1];
   }

   public long getFamiliesEpochTime() {
      return this.familiesEpochTime;
   }

   public String getAppend() {
      StringBuilder stringBuilder = new StringBuilder(160);
      stringBuilder.append("epoch=").append(this.familiesEpochTime).append(" families=").append(this.eventsFamiliesValue).append(" events{");
      boolean appendFlag = true;

      for (EventsFamilies.Event event : appendEvents) {
         int appendValue = this.eventsFamiliesValues[event.ordinal()];
         if (appendValue != 0) {
            if (!appendFlag) {
               stringBuilder.append(',');
            }

            stringBuilder.append(event.name()).append('=').append(appendValue);
            appendFlag = false;
         }
      }

      stringBuilder.append("} nonEvents{");
      boolean appendFlag2 = true;

      for (EventsFamilies.NonEvent nonEvent : appendNonEvents) {
         int appendValue2 = this.eventsNonValues[nonEvent.ordinal()];
         if (appendValue2 != 0) {
            if (!appendFlag2) {
               stringBuilder.append(',');
            }

            stringBuilder.append(nonEvent.name()).append('=').append(appendValue2);
            appendFlag2 = false;
         }
      }

      return stringBuilder.append('}').toString();
   }

   public EventsFamilies() {
      this.eventsFamiliesValues = new int[appendEvents.length];
      this.eventsNonValues = new int[appendNonEvents.length];
      this.familiesEventsLongArrayMap = new LinkedHashMap<Long, long[]>(64, 0.75F, false) {
         @Override
         protected boolean removeEldestEntry(Entry<Long, long[]> entry) {
            if (this.size() <= 4096) {
               return false;
            } else {
               if (((long[])entry.getValue())[0] == EventsFamilies.this.familiesEpochTime) {
                  EventsFamilies.this.eventsFamiliesValue--;
               }

               return true;
            }
         }
      };
   }

   public boolean canFlagFamiliesValue(long eventsFamiliesValue, int eventsFamiliesValue2) {
      return this.getFamiliesEpochIndex(eventsFamiliesValue) >= eventsFamiliesValue2;
   }

   public long getFamiliesEpoch(EventsFamilies.Event event) {
      this.eventsFamiliesValues[event.ordinal()]++;
      this.familiesEpochTime++;
      this.eventsFamiliesValue = 0;
      return this.familiesEpochTime;
   }

   public void updateEventsFamilies() {
      this.familiesEpochTime = 1L;
      this.eventsFamiliesValue = 0;
      this.familiesEventsLongArrayMap.clear();
      Arrays.fill(this.eventsFamiliesValues, 0);
      Arrays.fill(this.eventsNonValues, 0);
   }

   public int getEventsFamiliesValue() {
      return this.eventsFamiliesValue;
   }

   public int getFamiliesEpochIndex(long familiesEpochValue) {
      long[] familiesEpochValues = this.familiesEventsLongArrayMap.get(familiesEpochValue);
      return familiesEpochValues != null && familiesEpochValues[0] == this.familiesEpochTime ? (int)familiesEpochValues[1] : 0;
   }

   public static enum Event {
      CELL_RETIRED,
      TOPOLOGY_TEMP_CONFIRMED,
      REACHABLE_COMPONENT_EXPANDED,
      STAND_MIGRATED,
      ROUTE_FAIL_TO_PASS,
      UNRESOLVED_COMPONENT_SHRANK;
   }

   public static enum NonEvent {
      TARGET_SWITCH_SAME_COMPONENT,
      FAILURE_MEMORY_WRITE,
      COOLDOWN_EXPIRY,
      RELAXED_FUTURE_STAND,
      EMPTY_PATH_SEARCH,
      PLANNER_PASS,
      TICK,
      TARGET_SWITCH;
   }
}
