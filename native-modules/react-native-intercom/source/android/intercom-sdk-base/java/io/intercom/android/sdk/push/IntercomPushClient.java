package io.intercom.android.sdk.push;

import android.app.Application;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.os.Bundle;
import androidx.compose.runtime.internal.StabilityInferred;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.api.Api;
import io.intercom.android.sdk.identity.PushTokenStore;
import io.intercom.android.sdk.identity.UserIdentity;
import io.intercom.android.sdk.logger.LumberMill;
import io.intercom.android.sdk.m5.push.IntercomPushClientHandler;
import io.intercom.android.sdk.m5.push.IntercomPushClientHandler$Companion;
import io.intercom.android.sdk.utilities.ContextLocaliser;
import java.util.Map;
import java.util.Map.Entry;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010$\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\u0019\b\u0000\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007B\t\b\u0016¢\u0006\u0004\b\u0006\u0010\bJ\u0016\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000eJ\u0016\u0010\u000f\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\u0011J\"\u0010\u000f\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0012\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u000e0\u0012J\u000e\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0010\u001a\u00020\u0011J\u001a\u0010\u0013\u001a\u00020\u00142\u0012\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u000e0\u0012J\u001e\u0010\u0015\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u0016\u001a\u00020\u0017J*\u0010\u0015\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\f2\u0012\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u000e0\u00122\u0006\u0010\u0016\u001a\u00020\u0017J\u001c\u0010\u0018\u001a\u00020\u00112\u0012\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\u000e\u0012\u0004\u0012\u00020\u000e0\u0012H\u0002R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0019",
   d2 = {
         "Lio/intercom/android/sdk/push/IntercomPushClient;",
         "",
         "twig",
         "Lcom/intercom/twig/Twig;",
         "pushHandler",
         "Lio/intercom/android/sdk/m5/push/IntercomPushClientHandler;",
         "<init>",
         "(Lcom/intercom/twig/Twig;Lio/intercom/android/sdk/m5/push/IntercomPushClientHandler;)V",
         "()V",
         "sendTokenToIntercom",
         "",
         "application",
         "Landroid/app/Application;",
         "token",
         "",
         "handlePush",
         "message",
         "Landroid/os/Bundle;",
         "",
         "isIntercomPush",
         "",
         "handlePushWithCustomStack",
         "customStack",
         "Landroid/app/TaskStackBuilder;",
         "convertMessageMapToBundle",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class IntercomPushClient {
   @NotNull
   private final Twig twig;
   @NotNull
   private final IntercomPushClientHandler pushHandler;
   public static final int $stable;

   static { $stable = 8; }

   public IntercomPushClient(@NotNull Twig var1, @NotNull IntercomPushClientHandler var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "twig");
      Intrinsics.checkNotNullParameter(var2, "pushHandler");
      this.twig = var1;
      this.pushHandler = var2;
   }

   public IntercomPushClient() {
      Twig var10001 = LumberMill.getLogger();
      Intrinsics.checkNotNullExpressionValue(var10001, "getLogger(...)");
      this(var10001, new IntercomPushClientHandler());
   }

   public final void sendTokenToIntercom(@NotNull Application var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "application");
      Intrinsics.checkNotNullParameter(var2, "token");
      if (StringsKt.isBlank(var2)) {
         this.twig
            .e("sendTokenToIntercom() was called with a null or empty token. This user will not receive push notifications until a valid device token is sent.");
      } else {
         Injector.initIfCachedCredentials(var1);
         if (Injector.isNotInitialised()) {
            this.twig.w("Token not sent because Intercom is not initialised");
         } else {
            Injector var3 = Injector.get();
            IntercomPushClientHandler var10000 = this.pushHandler;
            PushTokenStore var10001 = var3.getPushTokenStore();
            Intrinsics.checkNotNullExpressionValue(var10001, "getPushTokenStore(...)");
            if (var10000.shouldSendDeviceToken(var10001, var2)) {
               var10000 = this.pushHandler;
               Api var10002 = var3.getApi();
               Intrinsics.checkNotNullExpressionValue(var10002, "getApi(...)");
               UserIdentity var10003 = var3.getUserIdentity();
               Intrinsics.checkNotNullExpressionValue(var10003, "getUserIdentity(...)");
               Provider var10004 = var3.getAppConfigProvider();
               Intrinsics.checkNotNullExpressionValue(var10004, "getAppConfigProvider(...)");
               PushTokenStore var10005 = var3.getPushTokenStore();
               Intrinsics.checkNotNullExpressionValue(var10005, "getPushTokenStore(...)");
               var10000.sendTokenToIntercom(var2, var10002, var10003, var10004, var10005);
            }
         }
      }
   }

   public final void handlePush(@NotNull Application var1, @NotNull Bundle var2) {
      Intrinsics.checkNotNullParameter(var1, "application");
      Intrinsics.checkNotNullParameter(var2, "message");
      if (!this.isIntercomPush(var2)) {
         this.twig.i("The message passed to handlePush() was not an Intercom push message.");
      } else {
         Injector.initIfCachedCredentials(var1);
         if (Injector.isNotInitialised()) {
            this.twig.w("Push not handled because Intercom is not initialised");
         } else {
            Injector var3 = Injector.get();
            Provider var4 = var3.getAppConfigProvider();
            Context var5 = new ContextLocaliser(var4).createLocalisedContext(var1);
            IntercomPushClientHandler$Companion var10000 = IntercomPushClientHandler.Companion;
            Intrinsics.checkNotNull(var5);
            var10000.setUpNotificationChannels(var5);
            IntercomPushClientHandler var6 = this.pushHandler;
            UserIdentity var10003 = var3.getUserIdentity();
            Intrinsics.checkNotNullExpressionValue(var10003, "getUserIdentity(...)");
            IntercomPushClientHandler.handlePush$default(var6, var2, var5, var10003, null, 8, null);
         }
      }
   }

   public final void handlePush(@NotNull Application var1, @NotNull Map var2) {
      Intrinsics.checkNotNullParameter(var1, "application");
      Intrinsics.checkNotNullParameter(var2, "message");
      this.handlePush(var1, this.convertMessageMapToBundle(var2));
   }

   public final boolean isIntercomPush(@NotNull Bundle var1) {
      Intrinsics.checkNotNullParameter(var1, "message");
      return this.pushHandler.isIntercomPush(var1);
   }

   public final boolean isIntercomPush(@NotNull Map var1) {
      Intrinsics.checkNotNullParameter(var1, "message");
      return this.isIntercomPush(this.convertMessageMapToBundle(var1));
   }

   public final void handlePushWithCustomStack(@NotNull Application var1, @NotNull Bundle var2, @NotNull TaskStackBuilder var3) {
      Intrinsics.checkNotNullParameter(var1, "application");
      Intrinsics.checkNotNullParameter(var2, "message");
      Intrinsics.checkNotNullParameter(var3, "customStack");
      if (!this.isIntercomPush(var2)) {
         this.twig.i("The message passed to handlePush() was not an Intercom push message.");
      } else {
         Injector.initIfCachedCredentials(var1);
         if (Injector.isNotInitialised()) {
            this.twig.w("Push not handled because Intercom is not initialised");
         } else {
            Injector var4 = Injector.get();
            Provider var5 = var4.getAppConfigProvider();
            Context var6 = new ContextLocaliser(var5).createLocalisedContext(var1);
            IntercomPushClientHandler$Companion var10000 = IntercomPushClientHandler.Companion;
            Intrinsics.checkNotNull(var6);
            var10000.setUpNotificationChannels(var6);
            IntercomPushClientHandler var7 = this.pushHandler;
            UserIdentity var10003 = var4.getUserIdentity();
            Intrinsics.checkNotNullExpressionValue(var10003, "getUserIdentity(...)");
            var7.handlePush(var2, var6, var10003, var3);
         }
      }
   }

   public final void handlePushWithCustomStack(@NotNull Application var1, @NotNull Map var2, @NotNull TaskStackBuilder var3) {
      Intrinsics.checkNotNullParameter(var1, "application");
      Intrinsics.checkNotNullParameter(var2, "message");
      Intrinsics.checkNotNullParameter(var3, "customStack");
      this.handlePushWithCustomStack(var1, this.convertMessageMapToBundle(var2), var3);
   }

   private final Bundle convertMessageMapToBundle(Map var1) {
      Bundle var2 = new Bundle(var1.size());

      for (java.lang.Object recoveredEntryElement_0_189 : var1.entrySet()) {
         Entry var4 = (Entry) recoveredEntryElement_0_189;
         String var5 = (String)var4.getKey();
         String var6 = (String)var4.getValue();
         var2.putString(var5, var6);
      }

      return var2;
   }
}
