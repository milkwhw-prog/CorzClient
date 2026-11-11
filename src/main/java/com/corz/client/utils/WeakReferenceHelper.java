package com.corz.client.utils;

import java.lang.ref.WeakReference;

public final class WeakReferenceHelper {
   private WeakReference<Object> weakReference;

   public void updateWeakReference() {
      this.weakReference = null;
   }

   public void handleWeakReference(Object obj) {
      this.weakReference = obj == null ? null : new WeakReference<>(obj);
   }

   public boolean canWeakReference(Object obj) {
      WeakReference weakReference = this.weakReference;
      return obj != null && weakReference != null && weakReference.get() == obj;
   }
}
