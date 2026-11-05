package io.intercom.android.sdk.m5.push;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import androidx.core.app.NotificationCompat.Action;
import androidx.core.app.NotificationCompat.Action.Builder;
import androidx.core.graphics.drawable.IconCompat;
import io.intercom.android.sdk.R.drawable;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u001a\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u0018\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0000\u001a\u0018\u0010\u0006\u001a\u00020\u00072\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0002¨\u0006\b",
   d2 = {
         "buildContextualAction",
         "Landroidx/core/app/NotificationCompat$Action;",
         "context",
         "Landroid/content/Context;",
         "url",
         "",
         "getAttachmentIntent",
         "Landroid/app/PendingIntent;",
         "intercom-sdk-base_release"
   }
)
@SourceDebugExtension(
   "SMAP\nConversationActionHandler.kt\nKotlin\n*S Kotlin\n*F\n+ 1 ConversationActionHandler.kt\nio/intercom/android/sdk/m5/push/ConversationActionHandlerKt\n+ 2 Uri.kt\nandroidx/core/net/UriKt\n*L\n1#1,39:1\n29#2:40\n*S KotlinDebug\n*F\n+ 1 ConversationActionHandler.kt\nio/intercom/android/sdk/m5/push/ConversationActionHandlerKt\n*L\n34#1:40\n*E\n"
)
public final class ConversationActionHandlerKt {
   @NotNull
   public static final Action buildContextualAction(@NotNull Context var0, @NotNull String var1) {
      Intrinsics.checkNotNullParameter(var0, "context");
      Intrinsics.checkNotNullParameter(var1, "url");
      IconCompat var2 = IconCompat.createWithResource(var0, drawable.intercom_ic_attachment);
      Action var10000 = new Builder(var2, "Open Attachment", getAttachmentIntent(var0, var1)).setContextual(true).build();
      Intrinsics.checkNotNullExpressionValue(var10000, "build(...)");
      return var10000;
   }

   private static final PendingIntent getAttachmentIntent(Context var0, String var1) {
      int var2 = var1.hashCode();
      int var3 = 201326592;
      Intent var5 = new Intent("android.intent.action.VIEW");
      Intent var6 = var5;
      int var7 = 0;
      String var8 = var1;
      int var9 = 0;
      var6.setData(Uri.parse(var8));
      Intent var4 = var5;
      PendingIntent var10000 = PendingIntent.getActivity(var0, var2, var4, var3);
      Intrinsics.checkNotNullExpressionValue(var10000, "getActivity(...)");
      return var10000;
   }
}
