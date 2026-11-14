package io.intercom.android.sdk.m5.push;

import android.content.Context;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007J\u000e\u0010\b\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u0007¨\u0006\t",
   d2 = {
         "Lio/intercom/android/sdk/m5/push/IntercomPushClientHandler$Companion;",
         "",
         "<init>",
         "()V",
         "clear",
         "",
         "context",
         "Landroid/content/Context;",
         "setUpNotificationChannels",
         "intercom-sdk-base_release"
   }
)
public final class IntercomPushClientHandler$Companion {
   private IntercomPushClientHandler$Companion() {
   }

   public final void clear(@NotNull Context var1) {
      Intrinsics.checkNotNullParameter(var1, "context");
      IntercomNotificationHandler.INSTANCE.clear(var1);
   }

   public final void setUpNotificationChannels(@NotNull Context var1) {
      Intrinsics.checkNotNullParameter(var1, "context");
      IntercomNotificationHandler.INSTANCE.setUpNotificationChannels$intercom_sdk_base_release(var1);
   }

   // $VF: synthetic method
   public IntercomPushClientHandler$Companion(DefaultConstructorMarker var1) {
      this();
   }
}
