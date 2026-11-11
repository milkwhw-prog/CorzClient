package com.corz.client.utils;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public final class DequeArray<T> {
   private final long windowTimeMs;
   private final ArrayDeque<DequeArray.Entry<T>> arrayDeque = new ArrayDeque<>();

   public void handleDequeArray(Object obj) {
      for (DequeArray.Entry entry : this.arrayDeque) {
         entry.handle(obj);
      }
   }

   public boolean isEmpty() {
      return this.arrayDeque.isEmpty();
   }

   public List<T> getDequeArray(long dequeArrayValue) {
      var arrayList = new ArrayList();

      while (!this.arrayDeque.isEmpty() && this.arrayDeque.peekFirst().canTime(dequeArrayValue, this.windowTimeMs)) {
         arrayList.add(this.arrayDeque.pollFirst().object);
      }

      return arrayList;
   }

   public void handleDequeArray(Object obj, long dequeArrayValue) {
      for (DequeArray.Entry entry : this.arrayDeque) {
         entry.handle(obj, dequeArrayValue);
      }
   }

   public void updateDequeArray() {
      this.arrayDeque.clear();
   }

   public DequeArray(long time) {
      this.windowTimeMs = time;
   }

   public boolean canDequeArray(Object obj) {
      var iterator = this.arrayDeque.iterator();

      while (iterator.hasNext()) {
         if (((DequeArray.Entry)iterator.next()).canFlag(obj)) {
            return true;
         }
      }

      return false;
   }

   public DequeArray.Entry<T> getNext(Predicate<T> predicate) {
      var descendingIterator = this.arrayDeque.descendingIterator();

      while (descendingIterator.hasNext()) {
         var next = (DequeArray.Entry<T>)descendingIterator.next();
         if (predicate.test(next.object)) {
            return next;
         }
      }

      return null;
   }

   public boolean isLastOccurrence(DequeArray.Entry<T> entry) {
      return this.arrayDeque.removeLastOccurrence(entry);
   }

   public int getDequeArray() {
      return this.arrayDeque.size();
   }

   boolean canDequeArrayForObject(T t) {
      var peekLast = this.arrayDeque.peekLast();
      if (peekLast != null && peekLast.object == t) {
         this.arrayDeque.pollLast();
         return true;
      } else {
         return false;
      }
   }

   public long getMax(long dequeArrayValue) {
      var peekFirst = this.arrayDeque.peekFirst();
      return peekFirst == null ? 0L : Math.max(0L, dequeArrayValue - peekFirst.time);
   }

   public DequeArray.Entry<T> getDequeArray(T t, long dequeArrayValue) {
      var dequeArray = new DequeArray.Entry<>(t, dequeArrayValue);
      this.arrayDeque.addLast(dequeArray);
      return dequeArray;
   }

   DequeArray.Entry<T> getPeekLast() {
      return this.arrayDeque.peekLast();
   }

   public List<T> getDequeArrayArrayDeque() {
      var arrayList = new ArrayList(this.arrayDeque.size());

      while (!this.arrayDeque.isEmpty()) {
         arrayList.add(this.arrayDeque.pollFirst().object);
      }

      return arrayList;
   }

   public static final class Entry<T> {
      private final long time;
      private int value;
      private final T object;
      private long[] copyOf;
      private Object[] copyOf2 = new Object[1];

      void handle(Object obj, long entryBound) {
         for (int i = 0; i < this.value; i++) {
            if (this.copyOf2[i] == obj) {
               this.copyOf[i] = Math.min(this.copyOf[i], entryBound);
            }
         }
      }

      void handle(Object obj) {
         for (int i = 0; i < this.value; i++) {
            if (this.copyOf2[i] == obj) {
               this.value--;
               this.copyOf2[i] = this.copyOf2[this.value];
               this.copyOf[i] = this.copyOf[this.value];
               this.copyOf2[this.value] = null;
               return;
            }
         }
      }

      int getValue() {
         return this.value;
      }

      boolean canFlag(Object obj) {
         for (int i = 0; i < this.value; i++) {
            if (this.copyOf2[i] == obj) {
               return true;
            }
         }

         return false;
      }

      boolean canTime(long entryValue, long entryLimit) {
         if (entryLimit > 0L && entryValue - this.time >= entryLimit) {
            return true;
         } else {
            for (int i = 0; i < this.value; i++) {
               if (this.copyOf[i] > entryValue) {
                  return false;
               }
            }

            return true;
         }
      }

      public void handleForObjectLong(Object obj, long entryBound) {
         for (int i = 0; i < this.value; i++) {
            if (this.copyOf2[i] == obj) {
               this.copyOf[i] = Math.max(this.copyOf[i], entryBound);
               return;
            }
         }

         if (this.value == this.copyOf2.length) {
            this.copyOf2 = Arrays.copyOf(this.copyOf2, this.value * 2);
            this.copyOf = Arrays.copyOf(this.copyOf, this.value * 2);
         }

         this.copyOf2[this.value] = obj;
         this.copyOf[this.value] = entryBound;
         this.value++;
      }

      Entry(T t, long time) {
         this.copyOf = new long[1];
         this.object = (T)t;
         this.time = time;
      }

      T getObject() {
         return this.object;
      }

      long getTime() {
         return this.time;
      }
   }
}
