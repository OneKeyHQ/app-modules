package io.intercom.android.sdk.utilities;

import android.app.Activity;
import androidx.collection.ArraySet;
import java.util.Set;

public class ActivityFinisher {
   private final Set activities = new ArraySet();

   public void register(Activity var1) {
      this.activities.add(var1);
   }

   public void unregister(Activity var1) {
      this.activities.remove(var1);
   }

   public void finishActivities() {
      for (java.lang.Object recoveredEntryElement_0_19 : this.activities) {
         Activity var2 = (Activity) recoveredEntryElement_0_19;
         var2.finish();
      }

      this.activities.clear();
   }
}
