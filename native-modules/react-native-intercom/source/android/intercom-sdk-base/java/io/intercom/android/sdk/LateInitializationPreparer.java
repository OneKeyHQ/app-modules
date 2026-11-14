package io.intercom.android.sdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.utilities.SimpleActivityLifecycleCallbacks;
import java.util.HashSet;
import java.util.Set;

public class LateInitializationPreparer extends SimpleActivityLifecycleCallbacks {
   @SuppressLint("StaticFieldLeak")
   @Nullable
   private static LateInitializationPreparer instance;
   private final Twig twig = LumberMill.getLogger();
   private final Set startedActivities = new HashSet();
   @Nullable
   private Activity lastResumedActivity;
   private boolean hasPaused;
   private boolean isRegistered;

   public static LateInitializationPreparer getInstance() {
      if (instance == null) {
         instance = new LateInitializationPreparer();
      }

      return instance;
   }

   public void handlePastLifecycleEvents(Application var1, Injector var2) {
      Activity var3 = this.lastResumedActivity;
      boolean var4 = this.hasPaused;
      Set var5 = this.startedActivities;
      this.unregister(var1);
      if (var3 != null) {
         this.twig.i("Handling lifecycle events for " + var3 + " during late initialisation");
         LifecycleTracker var6 = var2.getLifecycleTracker();
         var6.onActivityStarted(var3);
         var6.onActivityResumed(var3);
         if (var4) {
            var6.onActivityPaused(var3);
         }
      }

      if (!var5.isEmpty()) {
         var2.getLifecycleTracker().registerActivities(var5);
         this.twig.i("Observed Activities with hashcodes " + var5 + " during late initialization");
      }
   }

   public void register(Application var1) {
      this.twig.i("Registering for later initialization");
      synchronized (this) {
         if (!this.isRegistered) {
            var1.registerActivityLifecycleCallbacks(this);
            this.isRegistered = true;
         }
      }
   }

   public void unregister(Application var1) {
      this.twig.i("Unregistering for later initialization");
      var1.unregisterActivityLifecycleCallbacks(this);
      this.isRegistered = false;
      this.lastResumedActivity = null;
      this.hasPaused = false;
   }

   @Override
   public void onActivityStarted(Activity var1) {
      this.startedActivities.add(System.identityHashCode(var1));
   }

   @Override
   public void onActivityResumed(Activity var1) {
      this.startedActivities.add(System.identityHashCode(var1));
      this.lastResumedActivity = var1;
      this.hasPaused = false;
   }

   @Override
   public void onActivityPaused(Activity var1) {
      this.startedActivities.add(System.identityHashCode(var1));
      this.hasPaused = true;
   }

   @Override
   public void onActivityStopped(Activity var1) {
      this.startedActivities.remove(System.identityHashCode(var1));
      if (var1 == this.lastResumedActivity) {
         this.lastResumedActivity = null;
      }
   }

   @VisibleForTesting
   boolean hasPaused() {
      return this.hasPaused;
   }

   @VisibleForTesting
   @Nullable
   Activity lastResumedActivity() {
      return this.lastResumedActivity;
   }

   @VisibleForTesting
   Set startedActivities() {
      return this.startedActivities;
   }
}
