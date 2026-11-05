package io.intercom.android.sdk;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.text.TextUtils;
import androidx.annotation.VisibleForTesting;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.api.Api;
import io.intercom.android.sdk.api.DeDuper;
import io.intercom.android.sdk.errorreporting.ErrorReporter;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.m5.IntercomRootActivity;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.m5.data.IntercomEvent$AppEnteredBackground;
import io.intercom.android.sdk.m5.data.IntercomEvent$AppEnteredForeground;
import io.intercom.android.sdk.m5.push.IntercomPushClientHandler;
import io.intercom.android.sdk.metrics.MetricsStore;
import io.intercom.android.sdk.models.HostAppState;
import io.intercom.android.sdk.user.UserUpdaterHelper;
import io.intercom.android.sdk.utilities.SimpleActivityLifecycleCallbacks;
import io.intercom.android.sdk.utilities.commons.TimeProvider;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

class LifecycleTracker extends SimpleActivityLifecycleCallbacks implements ActivityLifecycleCallbacks {
   private final Set startedActivities = new HashSet();
   private final MetricsStore metricsStore;
   private final ErrorReporter errorReporter;
   private final DeDuper deDuper;
   private final TimeProvider timeProvider;
   private final ResetManager resetManager;
   private final ActivityLifecycleHandler handler;
   private final Twig twig = LumberMill.getLogger();
   private final Provider apiProvider;
   private final IntercomDataLayer dataLayer;
   private final UserUpdaterHelper userUpdaterHelper;

   static LifecycleTracker create(
      MetricsStore var0,
      ErrorReporter var1,
      DeDuper var2,
      TimeProvider var3,
      ResetManager var4,
      float var5,
      Provider var6,
      IntercomDataLayer var7,
      UserUpdaterHelper var8
   ) {
      return new LifecycleTracker(var0, var1, var2, var3, var4, new ActivityLifecycleHandler(Looper.getMainLooper(), var5, var7), var6, var7, var8);
   }

   @VisibleForTesting
   LifecycleTracker(
      MetricsStore var1,
      ErrorReporter var2,
      DeDuper var3,
      TimeProvider var4,
      ResetManager var5,
      ActivityLifecycleHandler var6,
      Provider var7,
      IntercomDataLayer var8,
      UserUpdaterHelper var9
   ) {
      this.metricsStore = var1;
      this.errorReporter = var2;
      this.deDuper = var3;
      this.timeProvider = var4;
      this.resetManager = var5;
      this.handler = var6;
      this.apiProvider = var7;
      this.dataLayer = var8;
      this.userUpdaterHelper = var9;
   }

   @Override
   public void onActivityStarted(Activity var1) {
      this.registerActivity(var1);
      this.handleExtras(var1);
      if (!((HostAppState)this.dataLayer.getHostAppState().getValue()).isForeground()) {
         long var2 = this.timeProvider.currentTimeMillis();
         this.userUpdaterHelper.update(var2);
         this.dataLayer.emitEvent(new IntercomEvent$AppEnteredForeground(var2));
         this.dataLayer.appEnteredForeground();
         this.metricsStore.loadAndSendMetrics();
         this.errorReporter.sendSavedReport();
         if (var1 instanceof IntercomRootActivity) {
            IntercomPushClientHandler.Companion.clear(var1);
         }
      }
   }

   @Override
   public void onActivityResumed(Activity var1) {
      this.handler.removeMessages(1, var1);
      this.handler.sendMessage(ActivityLifecycleHandler.getMessage(0, var1));
      this.handleExtras(var1);
   }

   @Override
   public void onActivityPaused(Activity var1) {
      this.handler.removeMessages(1, var1);
      this.handler.sendMessage(ActivityLifecycleHandler.getMessage(2, var1));
   }

   @Override
   public void onActivityStopped(Activity var1) {
      this.unregisterActivity(var1);
      this.handler.removeMessages(1, var1);
      this.handler.sendMessage(ActivityLifecycleHandler.getMessage(3, var1));
      if (this.hasApplicationBecomeBackgrounded(var1)) {
         if (this.resetManager.isSoftReset()) {
            this.resetManager.hardReset();
         }

         long var2 = this.timeProvider.currentTimeMillis();
         this.dataLayer.emitEvent(new IntercomEvent$AppEnteredBackground(var2));
         this.dataLayer.appEnteredBackground(var2);
         this.deDuper.reset();
         this.metricsStore.loadAndSendMetrics();
      }
   }

   void registerActivities(Collection var1) {
      this.startedActivities.addAll(var1);
   }

   private void registerActivity(Activity var1) {
      this.startedActivities.add(System.identityHashCode(var1));
      this.twig.i("Started observing " + var1);
   }

   private void unregisterActivity(Activity var1) {
      this.startedActivities.remove(System.identityHashCode(var1));
      this.twig.i("Stopped observing " + var1);
   }

   private boolean hasApplicationBecomeBackgrounded(Activity var1) {
      return this.isApplicationInBackground(var1) && ((HostAppState)this.dataLayer.getHostAppState().getValue()).isForeground();
   }

   @VisibleForTesting
   boolean isApplicationInBackground(Activity var1) {
      return isScreenLocked(var1) || this.startedActivities.isEmpty() && !var1.isChangingConfigurations();
   }

   private static boolean isScreenLocked(Context var0) {
      KeyguardManager var1 = (KeyguardManager)var0.getSystemService("keyguard");
      return var1.inKeyguardRestrictedInputMode();
   }

   private void handleExtras(Activity var1) {
      Intent var2 = var1.getIntent();
      if (var2 != null) {
         if (var2.hasExtra("io.intercom.android.sdk.INTERCOM_PUSH_INSTANCE_ID")) {
            String var3 = var2.getStringExtra("io.intercom.android.sdk.INTERCOM_PUSH_INSTANCE_ID");
            if (!TextUtils.isEmpty(var3)) {
               this.twig.i("Activity started from Mobile Push: " + var1);
               ((Api)this.apiProvider.get()).markPushAsOpened(var3);
               Injector.get().getMetricTracker().openedPushOnlyNotification(var3);
            }

            var2.removeExtra("io.intercom.android.sdk.INTERCOM_PUSH_INSTANCE_ID");
         }
      }
   }
}
