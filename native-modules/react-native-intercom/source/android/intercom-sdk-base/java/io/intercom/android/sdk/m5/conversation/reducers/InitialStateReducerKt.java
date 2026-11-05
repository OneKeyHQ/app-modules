package io.intercom.android.sdk.m5.conversation.reducers;

import io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Loading;
import io.intercom.android.sdk.m5.conversation.states.LaunchMode;
import io.intercom.android.sdk.m5.conversation.states.TopAppBarUiState;
import io.intercom.android.sdk.ui.R.drawable;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u0010\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0000¨\u0006\u0004",
   d2 = {
         "reduceInitialState",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationUiState$Loading;",
         "launchMode",
         "Lio/intercom/android/sdk/m5/conversation/states/LaunchMode;",
         "intercom-sdk-base_release"
   }
)
public final class InitialStateReducerKt {
   @NotNull
   public static final ConversationUiState$Loading reduceInitialState(@NotNull LaunchMode var0) {
      Intrinsics.checkNotNullParameter(var0, "launchMode");
      return new ConversationUiState$Loading(
         TopAppBarUiState.recovered$c1f0d5c4f39cbc8e(
            TopAppBarUiState.Companion.getDefault(),
            null,
            var0 == LaunchMode.PROGRAMMATIC ? drawable.intercom_ic_close : drawable.intercom_ic_back,
            null,
            null,
            null,
            false,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            32765,
            null
         )
      );
   }
}
