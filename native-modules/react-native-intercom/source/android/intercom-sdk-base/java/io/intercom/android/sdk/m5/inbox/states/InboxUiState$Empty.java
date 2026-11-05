package io.intercom.android.sdk.m5.inbox.states;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.models.EmptyState;
import io.intercom.android.sdk.ui.common.IntercomTopBarState;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0007\b\u0007\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\r¨\u0006\u000e",
   d2 = {
         "Lio/intercom/android/sdk/m5/inbox/states/InboxUiState$Empty;",
         "Lio/intercom/android/sdk/m5/inbox/states/InboxUiState;",
         "intercomTopBarState",
         "Lio/intercom/android/sdk/ui/common/IntercomTopBarState;",
         "emptyState",
         "Lio/intercom/android/sdk/models/EmptyState;",
         "showActionButton",
         "",
         "<init>",
         "(Lio/intercom/android/sdk/ui/common/IntercomTopBarState;Lio/intercom/android/sdk/models/EmptyState;Z)V",
         "getEmptyState",
         "()Lio/intercom/android/sdk/models/EmptyState;",
         "getShowActionButton",
         "()Z",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class InboxUiState$Empty extends InboxUiState {
   @NotNull
   private final EmptyState emptyState;
   private final boolean showActionButton;
   public static int $stable;

   static {}

   public InboxUiState$Empty(@NotNull IntercomTopBarState var1, @NotNull EmptyState var2, boolean var3) {
      Intrinsics.checkNotNullParameter(var1, "intercomTopBarState");
      Intrinsics.checkNotNullParameter(var2, "emptyState");
      super(var1);
      this.emptyState = var2;
      this.showActionButton = var3;
   }

   @NotNull
   public final EmptyState getEmptyState() {
      return this.emptyState;
   }

   public final boolean getShowActionButton() {
      return this.showActionButton;
   }
}
