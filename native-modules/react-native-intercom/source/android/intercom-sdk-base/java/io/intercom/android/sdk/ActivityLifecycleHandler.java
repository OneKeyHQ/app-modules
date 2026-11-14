package io.intercom.android.sdk;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import androidx.annotation.VisibleForTesting;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.m5.data.IntercomEvent$ActivityPaused;
import io.intercom.android.sdk.m5.data.IntercomEvent$ActivityReadyForViewAttachment;
import io.intercom.android.sdk.m5.data.IntercomEvent$ActivityStopped;

class ActivityLifecycleHandler extends Handler {
   static final int RESUMED_CODE = 0;
   static final int READY_FOR_VIEW_CODE = 1;
   static final int PAUSED_CODE = 2;
   static final int STOPPED_CODE = 3;
   private static final int ESTIMATED_ACTIVITY_TRANSITION_DURATION_MS = 500;
   private final long readyForViewDelayMs;
   private final IntercomDataLayer dataLayer;
   private final Twig twig = LumberMill.getLogger();

   ActivityLifecycleHandler(Looper var1, float var2, IntercomDataLayer var3) {
      super(var1);
      this.readyForViewDelayMs = (long)(var2 * 500.0F);
      this.dataLayer = var3;
   }

   @Override
   public void handleMessage(Message var1) {
      Activity var2 = (Activity)var1.obj;
      switch (var1.what) {
         case 0:
            this.sendMessageAfterDelay(getMessage(1, var2), this.readyForViewDelayMs);
            break;
         case 1:
            this.twig.i("Ready to show in-app messages in " + var2);
            this.dataLayer.emitEvent(new IntercomEvent$ActivityReadyForViewAttachment(var2));
            this.dataLayer.activityReadyForViewAttachment(var2);
            break;
         case 2:
            this.twig.i(var2 + " was paused");
            this.dataLayer.emitEvent(new IntercomEvent$ActivityPaused(var2));
            this.dataLayer.activityPaused(var2);
            break;
         case 3:
            this.dataLayer.emitEvent(new IntercomEvent$ActivityStopped(var2));
            this.dataLayer.activityStopped(var2);
      }
   }

   @VisibleForTesting
   void sendMessageAfterDelay(Message var1, long var2) {
      this.sendMessageDelayed(var1, var2);
   }

   static Message getMessage(int var0, Activity var1) {
      Message var2 = Message.obtain();
      var2.what = var0;
      var2.obj = var1;
      return var2;
   }
}
