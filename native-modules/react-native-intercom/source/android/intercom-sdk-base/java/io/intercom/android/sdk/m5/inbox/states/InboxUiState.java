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
   d1 = "\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\b\u0011\u0018\u00002\u00020\u0001:\u0005\b\t\n\u000b\fB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\r",
   d2 = {
         "Lio/intercom/android/sdk/m5/inbox/states/InboxUiState;",
         "",
         "intercomTopBarState",
         "Lio/intercom/android/sdk/ui/common/IntercomTopBarState;",
         "<init>",
         "(Lio/intercom/android/sdk/ui/common/IntercomTopBarState;)V",
         "getIntercomTopBarState",
         "()Lio/intercom/android/sdk/ui/common/IntercomTopBarState;",
         "Initial",
         "Loading",
         "Content",
         "Empty",
         "Error",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public class InboxUiState {
   @NotNull
   private final IntercomTopBarState intercomTopBarState;
   public static final int $stable = IntercomTopBarState.$stable;

   public InboxUiState(@NotNull IntercomTopBarState var1) {
      super();
      Intrinsics.checkNotNullParameter(var1, "intercomTopBarState");
      this.intercomTopBarState = var1;
   }

   @NotNull
   public final IntercomTopBarState getIntercomTopBarState() {
      return this.intercomTopBarState;
   }
}
