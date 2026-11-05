package io.intercom.android.sdk.m5.push.ui;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.Person;
import androidx.core.app.NotificationCompat.BubbleMetadata;
import androidx.core.app.NotificationCompat.BubbleMetadata.Builder;
import androidx.core.graphics.drawable.IconCompat;
import io.intercom.android.sdk.R.drawable;
import io.intercom.android.sdk.m5.ConversationScreenOpenerKt;
import io.intercom.android.sdk.m5.bubble.IntercomBubbleActivity;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0014\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u0018\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H\u0000¨\u0006\u0006",
   d2 = {
         "getBubbleMetaData",
         "Landroidx/core/app/NotificationCompat$BubbleMetadata;",
         "context",
         "Landroid/content/Context;",
         "conversation",
         "Lio/intercom/android/sdk/m5/push/ui/IntercomPushConversation;",
         "intercom-sdk-base_release"
   }
)
public final class BubbleMetaDataKt {
   @NotNull
   public static final BubbleMetadata getBubbleMetaData(@NotNull Context var0, @NotNull IntercomPushConversation var1) {
      PendingIntent var6;
      IconCompat var9;
      label16: {
         Intrinsics.checkNotNullParameter(var0, "context");
         Intrinsics.checkNotNullParameter(var1, "conversation");
         String var3 = var1.getConversationId();
         Class<IntercomBubbleActivity> var4 = IntercomBubbleActivity.class;
         Intent var2 = ConversationScreenOpenerKt.getComposerIntent$default(var0, null, false, null, var3, var4, null, 66, null);
         var6 = PendingIntent.getActivity(var0, 2, var2, 33554432);
         IntercomPushConversation$Message var10000 = (IntercomPushConversation$Message)CollectionsKt.firstOrNull(var1.getMessages());
         if (var10000 != null) {
            Person var8 = var10000.getPerson();
            if (var8 != null) {
               var9 = var8.getIcon();
               break label16;
            }
         }

         var9 = null;
      }

      IconCompat var7 = var9;
      Builder var10;
      IconCompat var10003 = var7;
      if (var7 == null) {
         var10003 = IconCompat.createWithResource(var0, drawable.intercom_ic_avatar_person);
      }

      var10 = new Builder(var6, var10003);
      Builder var5 = var10.setDesiredHeight(600).setSuppressNotification(true);
      BubbleMetadata var11 = var5.build();
      Intrinsics.checkNotNullExpressionValue(var11, "build(...)");
      return var11;
   }
}
