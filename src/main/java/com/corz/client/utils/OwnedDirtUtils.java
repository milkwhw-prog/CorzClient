package com.corz.client.utils;

import java.util.Objects;
import java.util.Set;
import java.util.function.LongPredicate;

public final class OwnedDirtUtils {
   private OwnedDirtUtils() {
   }

   public static OwnedDirtUtils.Revalidation getOwnedConfirmed(Set<Long> set, LongPredicate longPredicate) {
      Objects.requireNonNull(set, "owned");
      Objects.requireNonNull(longPredicate, "stillConfirmedOwnedDirt");
      int ownedSize = set.size();
      set.removeIf(cell -> !longPredicate.test(cell));
      return new OwnedDirtUtils.Revalidation(ownedSize, set.size(), ownedSize - set.size());
   }

   public record Revalidation(int before, int retained, int removed) {
   }
}
