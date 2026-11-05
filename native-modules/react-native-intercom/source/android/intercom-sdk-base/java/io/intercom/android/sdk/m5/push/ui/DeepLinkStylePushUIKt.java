package io.intercom.android.sdk.m5.push.ui;

import android.app.Notification;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.graphics.Bitmap;
import androidx.core.app.NotificationCompat.BigPictureStyle;
import androidx.core.app.NotificationCompat.BigTextStyle;
import androidx.core.app.NotificationCompat.Builder;
import io.intercom.android.sdk.m5.push.ConversationDeepLinkRouterKt;
import io.intercom.android.sdk.m5.push.IntercomPushData$DeepLinkPushData;
import io.intercom.android.sdk.m5.push.NotificationChannel;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000&\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\u001a8\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t2\b\b\u0002\u0010\n\u001a\u00020\u000bH\u0000¨\u0006\f",
   d2 = {
         "buildDeepLinkNotification",
         "Landroid/app/Notification;",
         "context",
         "Landroid/content/Context;",
         "deepLinkPushData",
         "Lio/intercom/android/sdk/m5/push/IntercomPushData$DeepLinkPushData;",
         "contentBitmap",
         "Landroid/graphics/Bitmap;",
         "customStack",
         "Landroid/app/TaskStackBuilder;",
         "isSilent",
         "",
         "intercom-sdk-base_release"
   }
)
public final class DeepLinkStylePushUIKt {
   @NotNull
   public static final Notification buildDeepLinkNotification(
      @NotNull Context var0, @NotNull IntercomPushData$DeepLinkPushData var1, @Nullable Bitmap var2, @Nullable TaskStackBuilder var3, boolean var4
   ) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var1, "deepLinkPushData");
      IntercomPushData$DeepLinkPushData var5 = var1;
      int var6 = 0;
      Builder var7 = BasePushUIKt.createBaseNotificationBuilder(var0, var5.getContentTitle(), var5.getContentText(), NotificationChannel.ACTIONS_CHANNEL);
      Builder var8 = var7;
      int var9 = 0;
      if (var2 != null) {
         var8.setStyle(
            new BigPictureStyle().bigPicture(var2).bigLargeIcon((Bitmap)null).setBigContentTitle(var5.getContentTitle()).setSummaryText(var5.getContentText())
         );
         var8.setLargeIcon(var2);
      } else {
         var8.setStyle(new BigTextStyle().setBigContentTitle(var5.getContentTitle()).bigText(var5.getContentText()));
      }

      Notification var10000 = var7.setContentIntent(ConversationDeepLinkRouterKt.buildIntentForDeepLinkScreen(var0, var5.getUri(), var5.getInstanceId(), var3))
         .setSilent(var4)
         .build();
      Intrinsics.checkNotNullExpressionValue(var10000, "build(...)");
      return var10000;
   }

   // $VF: synthetic method
   public static Notification buildDeepLinkNotification$default(
      Context var0, IntercomPushData$DeepLinkPushData var1, Bitmap var2, TaskStackBuilder var3, boolean var4, int var5, Object var6
   ) {
      if ((var5 & 8) != 0) {
         var3 = null;
      }

      if ((var5 & 16) != 0) {
         var4 = false;
      }

      return buildDeepLinkNotification(var0, var1, var2, var3, var4);
   }
}
