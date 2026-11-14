package io.intercom.android.sdk.m5.push;

import android.app.Notification;
import android.content.Context;
import android.os.Build.VERSION;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import com.intercom.twig.Twig;
import java.util.Map;
import java.util.Map.Entry;
import kotlin.Metadata;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000(\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010$\n\u0000\u001a(\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\tH\u0000\u001a,\u0010\n\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0012\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u00070\f2\u0006\u0010\b\u001a\u00020\tH\u0000¨\u0006\r",
   d2 = {
         "showNotification",
         "",
         "context",
         "Landroid/content/Context;",
         "notificationId",
         "",
         "notification",
         "Landroid/app/Notification;",
         "twig",
         "Lcom/intercom/twig/Twig;",
         "showNotifications",
         "notifications",
         "",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nNotificationPermissionChecker.kt\nKotlin\n*S Kotlin\n*F\n+ 1 NotificationPermissionChecker.kt\nio/intercom/android/sdk/m5/push/NotificationPermissionCheckerKt\n+ 2 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n*L\n1#1,52:1\n216#2,2:53\n*S KotlinDebug\n*F\n+ 1 NotificationPermissionChecker.kt\nio/intercom/android/sdk/m5/push/NotificationPermissionCheckerKt\n*L\n38#1:53,2\n*E\n"
)
public final class NotificationPermissionCheckerKt {
   public static final void showNotification(@NotNull Context var0, int var1, @NotNull Notification var2, @NotNull Twig var3) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var2, "notification");
      Intrinsics.checkNotNullParameter(var3, "twig");
      showNotifications(var0, MapsKt.mapOf(TuplesKt.to(var1, var2)), var3);
   }

   public static final void showNotifications(@NotNull Context var0, @NotNull Map var1, @NotNull Twig var2) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var1, "notifications");
      Intrinsics.checkNotNullParameter(var2, "twig");

      try {
         NotificationManagerCompat var18 = NotificationManagerCompat.from(var0);
         int var5 = 0;
         boolean var6 = VERSION.SDK_INT >= 33 ? ActivityCompat.checkSelfPermission(var0, "android.permission.POST_NOTIFICATIONS") == 0 : true;
         if (var6) {
            Map var7 = var1;
            int var8 = 0;

            for (java.lang.Object recoveredEntryElement_0_64 : var7.entrySet()) {
               Entry var10 = (Entry) recoveredEntryElement_0_64;
               Entry var11 = var10;
               int var12 = 0;
               int var13 = ((Number)var11.getKey()).intValue();
               Notification var14 = (Notification)var11.getValue();
               var18.notify(var13, var14);
            }
         }
      } catch (NullPointerException var15) {
         Object[] var17 = new Object[]{var15};
         var2.w("Failed to show notifications due to unavailable NotificationManager service.", var17);
      } catch (Exception var16) {
         Object[] var4 = new Object[]{var16};
         var2.w("Failed to show notifications due to unexpected error.", var4);
      }
   }
}
