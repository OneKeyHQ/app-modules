package io.intercom.android.sdk;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.api.Api;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.identity.PushTokenStore;
import io.intercom.android.sdk.identity.UserIdentity;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.m5.data.IntercomDataLayer;
import io.intercom.android.sdk.m5.data.IntercomEvent$SoftReset;
import io.intercom.android.sdk.metrics.UnreadConversationsTracker;
import io.intercom.android.sdk.overlay.OverlayPresenter;
import io.intercom.android.sdk.utilities.ActivityFinisher;

public class ResetManager {
   private final Provider apiProvider;
   private final UserIdentity userIdentity;
   private final OverlayPresenter overlayPresenter;
   private final Provider appConfigProvider;
   private final Context context;
   private final ActivityFinisher activityFinisher;
   private final IntercomDataLayer dataLayer;
   private final UnreadConversationsTracker unreadConversationsTracker;
   private final PushTokenStore pushTokenStore;
   private final Handler handler = new Handler(Looper.getMainLooper());
   private final Twig twig = LumberMill.getLogger();
   private final Runnable hardResetTask = new ResetManager$1(this);

   public ResetManager(
      Provider var1,
      UserIdentity var2,
      OverlayPresenter var3,
      Provider var4,
      Context var5,
      ActivityFinisher var6,
      IntercomDataLayer var7,
      UnreadConversationsTracker var8,
      PushTokenStore var9
   ) {
      this.apiProvider = var1;
      this.userIdentity = var2;
      this.overlayPresenter = var3;
      this.appConfigProvider = var4;
      this.context = var5;
      this.activityFinisher = var6;
      this.dataLayer = var7;
      this.unreadConversationsTracker = var8;
      this.pushTokenStore = var9;
   }

   public void softReset() {
      this.activityFinisher.finishActivities();
      this.overlayPresenter.softReset();
      this.handler.postDelayed(this.hardResetTask, ((AppConfig)this.appConfigProvider.get()).getSoftResetTimeoutMs());
      this.dataLayer.emitEvent(IntercomEvent$SoftReset.INSTANCE);
      this.userIdentity.softReset();
      this.twig.i("Successfully reset the user. To resume communicating with Intercom, you can register a user");
   }

   public void hardReset() {
      this.handler.removeCallbacks(this.hardResetTask);
      this.dataLayer.clearUserData();
      this.unreadConversationsTracker.clear();
      if (this.isSoftReset()) {
         this.pushTokenStore.setDeviceTokenSubmitted(false);
         String var1 = this.pushTokenStore.getPushToken();
         if (TextUtils.isEmpty(var1)) {
            this.twig.internal("There is no device token to remove.");
         } else {
            ((Api)this.apiProvider.get()).removeDeviceToken(var1, this.userIdentity);
         }

         this.dataLayer.hardReset();
         this.userIdentity.hardReset();
      }
   }

   public void clear() {
      this.handler.removeCallbacks(this.hardResetTask);
   }

   boolean isSoftReset() {
      return this.userIdentity.isSoftReset();
   }
}
