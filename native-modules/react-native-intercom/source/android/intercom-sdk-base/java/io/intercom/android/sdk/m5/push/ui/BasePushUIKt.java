package io.intercom.android.sdk.m5.push.ui;

import android.content.Context;
import androidx.core.app.NotificationCompat.Builder;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.m5.push.NotificationChannel;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u001c\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\u001a(\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00052\u0006\u0010\u0007\u001a\u00020\bH\u0000¨\u0006\t",
   d2 = {
         "createBaseNotificationBuilder",
         "Landroidx/core/app/NotificationCompat$Builder;",
         "context",
         "Landroid/content/Context;",
         "contentTitle",
         "",
         "contentText",
         "notificationChannel",
         "Lio/intercom/android/sdk/m5/push/NotificationChannel;",
         "intercom-sdk-base_release"
   }
)
public final class BasePushUIKt {
   @NotNull
   public static final Builder createBaseNotificationBuilder(
      @NotNull Context var0, @NotNull String var1, @NotNull String var2, @NotNull NotificationChannel var3
   ) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var1, "contentTitle");
      Intrinsics.checkNotNullParameter(var2, "contentText");
      Intrinsics.checkNotNullParameter(var3, "notificationChannel");
      Builder var10000 = new Builder(var0, var3.getChannelName())
         .setContentTitle(var1)
         .setContentText(var2)
         .setSmallIcon(drawable.intercom_push_icon)
         .setAutoCancel(true);
      Intrinsics.checkNotNullExpressionValue(var10000, "setAutoCancel(...)");
      return var10000;
   }
}
