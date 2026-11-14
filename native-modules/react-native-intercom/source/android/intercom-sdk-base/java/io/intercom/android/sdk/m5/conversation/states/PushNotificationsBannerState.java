package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\bÁ\n\u0018\u00002\u00020\u0001B\t\b\u0003¢\u0006\u0004\b\u0002\u0010\u0003J\u0013\u0010\u0004\u001a\u00020\u00052\b\u0010\u0006\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0007\u001a\u00020\bH×\u0001J\t\u0010\t\u001a\u00020\nH×\u0001¨\u0006\u000b",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/PushNotificationsBannerState;",
         "",
         "<init>",
         "()V",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class PushNotificationsBannerState {
   @NotNull
   public static final PushNotificationsBannerState INSTANCE = new PushNotificationsBannerState();
   public static int $stable;

   static {}

   private PushNotificationsBannerState() {
   }

   @NotNull
   @Override
   public String toString() {
      return "PushNotificationsBannerState";
   }

   @Override
   public int hashCode() {
      return -517876950;
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      return this == var1 ? true : var1 instanceof PushNotificationsBannerState;
   }
}
