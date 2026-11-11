package com.corz.client.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import com.corz.client.utils.StaffBadge;

public final class StaffRoster {
   private int seenSlotCount;
   private int memberCount;
   private int staffRosterCount;
   private List<StaffRoster.Member> copyOf;
   private List<String> memberStringList;
   private final HashMap<String, StaffRoster.Slot> memberSlotMap = new HashMap<>();
   private boolean memberActive;

   public int getMemberCount() {
      return this.memberCount;
   }

   public List<String> getItems() {
      return this.memberStringList;
   }

   public List<StaffRoster.Member> getCopyOf() {
      return this.copyOf;
   }

   public void handleMemberFlag(String memberText, UUID uuid, StaffBadge.Tier tier2, int memberValue) {
      if (memberText != null && !memberText.isEmpty() && tier2 != null) {
         StaffRoster.Slot slot = this.memberSlotMap.get(memberText);
         if (slot == null) {
            slot = new StaffRoster.Slot();
            slot.member = new StaffRoster.Member(memberText, uuid, tier2, memberValue);
            this.memberSlotMap.put(memberText, slot);
            this.memberActive = true;
         } else {
            if (slot.seenGeneration == this.staffRosterCount) {
               return;
            }

            if (!slot.member.canTierUid(uuid, tier2, memberValue)) {
               slot.member = new StaffRoster.Member(memberText, uuid, tier2, memberValue);
               this.memberActive = true;
            }
         }

         slot.seenGeneration = this.staffRosterCount;
         this.seenSlotCount++;
      }
   }

   private void updateMemberText() {
      ArrayList arrayList = new ArrayList(this.memberSlotMap.size());

      for (StaffRoster.Slot slot : this.memberSlotMap.values()) {
         arrayList.add(slot.member);
      }

      arrayList.sort((obj, obj2) -> {
         int compare = String.CASE_INSENSITIVE_ORDER.compare(((com.corz.client.utils.StaffRoster.Member) obj).memberText, ((com.corz.client.utils.StaffRoster.Member) obj2).memberText);
         return compare != 0 ? compare : ((com.corz.client.utils.StaffRoster.Member) obj).memberText.compareTo(((com.corz.client.utils.StaffRoster.Member) obj2).memberText);
      });
      String[] strings = new String[arrayList.size()];

      for (int i = 0; i < strings.length; i++) {
         strings[i] = ((StaffRoster.Member)arrayList.get(i)).memberText;
      }

      this.copyOf = List.copyOf(arrayList);
      this.memberStringList = List.of(strings);
      this.memberCount++;
   }

   public StaffRoster.Member getMember(String memberText) {
      StaffRoster.Slot slot = memberText == null ? null : this.memberSlotMap.get(memberText);
      return slot == null ? null : slot.member;
   }

   public boolean isFlag() {
      if (this.seenSlotCount != this.memberSlotMap.size()) {
         Iterator iterator = this.memberSlotMap.values().iterator();

         while (iterator.hasNext()) {
            if (((StaffRoster.Slot)iterator.next()).seenGeneration != this.staffRosterCount) {
               iterator.remove();
            }
         }

         this.memberActive = true;
      }

      if (!this.memberActive) {
         return false;
      } else {
         this.memberActive = false;
         this.updateMemberText();
         return true;
      }
   }

   public StaffRoster() {
      this.copyOf = List.of();
      this.memberStringList = List.of();
   }

   public int getValue() {
      return this.copyOf.size();
   }

   public void update() {
      this.staffRosterCount++;
      this.seenSlotCount = 0;
   }

   public void updateFlag() {
      this.memberActive = false;
      if (!this.memberSlotMap.isEmpty() || !this.copyOf.isEmpty()) {
         this.memberSlotMap.clear();
         this.updateMemberText();
      }
   }

   public static final class Member {
      public final int displayColorRgb;
      public final String memberText;
      public final StaffBadge.Tier tier2TierUid;
      public final int tierUidValue;
      public final UUID uUID;

      boolean canTierUid(UUID uuid, StaffBadge.Tier tier2, int tierUidValue) {
         return this.tier2TierUid == tier2 && this.tierUidValue == tierUidValue && Objects.equals(this.uUID, uuid);
      }

      Member(String text, UUID uUID, StaffBadge.Tier tier2, int value) {
         this.memberText = text;
         this.uUID = uUID;
         this.tier2TierUid = tier2;
         this.tierUidValue = value;
         this.displayColorRgb = StaffBadge.getValue(value, tier2);
      }
   }

   private static final class Slot {
      int seenGeneration;
      StaffRoster.Member member;
   }
}
