package io.intercom.android.sdk.m5.inbox.states;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.m5.components.ErrorState;
import io.intercom.android.sdk.ui.common.IntercomTopBarState;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\b\u0007\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007R\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t¨\u0006\n",
   d2 = {
         "Lio/intercom/android/sdk/m5/inbox/states/InboxUiState$Error;",
         "Lio/intercom/android/sdk/m5/inbox/states/InboxUiState;",
         "intercomTopBarState",
         "Lio/intercom/android/sdk/ui/common/IntercomTopBarState;",
         "errorState",
         "Lio/intercom/android/sdk/m5/components/ErrorState;",
         "<init>",
         "(Lio/intercom/android/sdk/ui/common/IntercomTopBarState;Lio/intercom/android/sdk/m5/components/ErrorState;)V",
         "getErrorState",
         "()Lio/intercom/android/sdk/m5/components/ErrorState;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class InboxUiState$Error extends InboxUiState {
   @NotNull
   private final ErrorState errorState;
   public static int $stable;

   static {}

   public InboxUiState$Error(@NotNull IntercomTopBarState var1, @NotNull ErrorState var2) {
      Intrinsics.checkNotNullParameter(var1, "intercomTopBarState");
      Intrinsics.checkNotNullParameter(var2, "errorState");
      super(var1);
      this.errorState = var2;
   }

   @NotNull
   public final ErrorState getErrorState() {
      return this.errorState;
   }
}
