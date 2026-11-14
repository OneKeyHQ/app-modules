package io.intercom.android.sdk.m5.push;

import android.app.TaskStackBuilder;
import android.content.Context;
import android.os.Bundle;
import androidx.compose.runtime.internal.StabilityInferred;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.api.Api;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.identity.PushTokenStore;
import io.intercom.android.sdk.identity.UserIdentity;
import io.intercom.android.sdk.logger.LumberMill;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000X\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0001\u0018\u0000 !2\u00020\u0001:\u0001!B\t\b\u0007¢\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\b\u001a\u00020\t2\u0006\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rJ4\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\f\u001a\u00020\r2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0012\u001a\u00020\u00132\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00160\u00152\u0006\u0010\n\u001a\u00020\u000bJ*\u0010\u0017\u001a\u00020\u000f2\u0006\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u0012\u001a\u00020\u00132\n\b\u0002\u0010\u001c\u001a\u0004\u0018\u00010\u001dJ\u000e\u0010\u001e\u001a\u00020\t2\u0006\u0010\u0018\u001a\u00020\u0019J\u0018\u0010\u001f\u001a\u00020\t2\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010 \u001a\u00020\u0016H\u0002R\u0018\u0010\u0004\u001a\n \u0006*\u0004\u0018\u00010\u00050\u0005X\u0082\u0004¢\u0006\u0004\n\u0002\u0010\u0007¨\u0006\"",
   d2 = {
         "Lio/intercom/android/sdk/m5/push/IntercomPushClientHandler;",
         "",
         "<init>",
         "()V",
         "twig",
         "Lcom/intercom/twig/Twig;",
         "kotlin.jvm.PlatformType",
         "Lcom/intercom/twig/Twig;",
         "shouldSendDeviceToken",
         "",
         "pushTokenStore",
         "Lio/intercom/android/sdk/identity/PushTokenStore;",
         "token",
         "",
         "sendTokenToIntercom",
         "",
         "api",
         "Lio/intercom/android/sdk/api/Api;",
         "userIdentity",
         "Lio/intercom/android/sdk/identity/UserIdentity;",
         "appConfigProvider",
         "Lio/intercom/android/sdk/Provider;",
         "Lio/intercom/android/sdk/identity/AppConfig;",
         "handlePush",
         "message",
         "Landroid/os/Bundle;",
         "localisedContext",
         "Landroid/content/Context;",
         "customStack",
         "Landroid/app/TaskStackBuilder;",
         "isIntercomPush",
         "hasUserHashIfNeeded",
         "appConfig",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class IntercomPushClientHandler {
   @NotNull
   public static final IntercomPushClientHandler$Companion Companion = new IntercomPushClientHandler$Companion(null);
   private final Twig twig = LumberMill.getLogger();
   public static final int $stable;

   static { $stable = 8; }

   public final boolean shouldSendDeviceToken(@NotNull PushTokenStore var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "pushTokenStore");
      Intrinsics.checkNotNullParameter(var2, "token");
      return !var1.hasCachedToken(var2) || !var1.isDeviceTokenSubmitted();
   }

   public final void sendTokenToIntercom(
      @NotNull String var1, @NotNull Api var2, @NotNull UserIdentity var3, @NotNull Provider var4, @NotNull PushTokenStore var5
   ) {
      Intrinsics.checkNotNullParameter(var1, "token");
      Intrinsics.checkNotNullParameter(var2, "api");
      Intrinsics.checkNotNullParameter(var3, "userIdentity");
      Intrinsics.checkNotNullParameter(var4, "appConfigProvider");
      Intrinsics.checkNotNullParameter(var5, "pushTokenStore");
      var5.savePushToken(var1);
      if (var3.identityExists()) {
         Object var10002 = var4.get();
         Intrinsics.checkNotNullExpressionValue(var10002, "get(...)");
         if (this.hasUserHashIfNeeded(var3, (AppConfig)var10002)) {
            var2.setDeviceToken(var1);
         }
      }
   }

   public final void handlePush(@NotNull Bundle var1, @NotNull Context var2, @NotNull UserIdentity var3, @Nullable TaskStackBuilder var4) {
      Intrinsics.checkNotNullParameter(var1, "message");
      Intrinsics.checkNotNullParameter(var2, "localisedContext");
      Intrinsics.checkNotNullParameter(var3, "userIdentity");
      IntercomPushData var5 = IntercomPushData.Companion.createFrom(var2, var1);
      if (var5 == null) {
         this.twig.i("This is not an Intercom push message");
      } else if (!var3.identityExists()) {
         this.twig.i("Can't create push message as we have no user identity. This can be caused by messages being sent to a logged out user.");
         IntercomNotificationHandler.INSTANCE.clear(var2);
      } else {
         IntercomNotificationHandler.processIntercomPushNotification$intercom_sdk_base_release$default(
            IntercomNotificationHandler.INSTANCE, var2, var5, var4, null, 8, null
         );
      }
   }

   // $VF: synthetic method
   public static void handlePush$default(
      IntercomPushClientHandler var0, Bundle var1, Context var2, UserIdentity var3, TaskStackBuilder var4, int var5, Object var6
   ) {
      if ((var5 & 8) != 0) {
         var4 = null;
      }

      var0.handlePush(var1, var2, var3, var4);
   }

   public final boolean isIntercomPush(@NotNull Bundle var1) {
      Intrinsics.checkNotNullParameter(var1, "message");
      return IntercomPushData.Companion.isIntercomPush(var1);
   }

   private final boolean hasUserHashIfNeeded(UserIdentity var1, AppConfig var2) {
      if (var2.isReceivedFromServer()) {
         if (!var2.isIdentityVerificationEnabled()) {
            return true;
         }

         if (var2.isIdentityVerificationEnabled()) {
            String var10000 = var1.getHmac();
            Intrinsics.checkNotNullExpressionValue(var10000, "getHmac(...)");
            if (!StringsKt.isBlank(var10000)) {
               return true;
            }
         }
      }

      return false;
   }
}
