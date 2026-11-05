package io.intercom.android.sdk.m5.inbox.states;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.ui.common.IntercomTopBarState;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005¨\u0006\u0006",
   d2 = {
         "Lio/intercom/android/sdk/m5/inbox/states/InboxUiState$Loading;",
         "Lio/intercom/android/sdk/m5/inbox/states/InboxUiState;",
         "intercomTopBarState",
         "Lio/intercom/android/sdk/ui/common/IntercomTopBarState;",
         "<init>",
         "(Lio/intercom/android/sdk/ui/common/IntercomTopBarState;)V",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class InboxUiState$Loading extends InboxUiState {
   public static int $stable;

   static {}

   public InboxUiState$Loading(@NotNull IntercomTopBarState var1) {
      Intrinsics.checkNotNullParameter(var1, "intercomTopBarState");
      super(var1);
   }
}
