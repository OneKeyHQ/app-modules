package io.intercom.android.sdk.m5.conversation.reducers;

import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponseKt;
import io.intercom.android.sdk.m5.conversation.states.ConversationUiState;
import io.intercom.android.sdk.m5.conversation.states.ConversationUiState$Error;
import io.intercom.android.sdk.m5.conversation.states.TopAppBarUiState;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\u0018\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\u001a\u001e\u0010\u0000\u001a\u00020\u00012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\u0006\u0010\u0005\u001a\u00020\u0006H\u0000¨\u0006\u0007",
   d2 = {
         "reduceError",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationUiState;",
         "networkResponse",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "",
         "topAppBarUiState",
         "Lio/intercom/android/sdk/m5/conversation/states/TopAppBarUiState;",
         "intercom-sdk-base_release"
   }
)
public final class ErrorReducerKt {
   @NotNull
   public static final ConversationUiState reduceError(@NotNull NetworkResponse var0, @NotNull TopAppBarUiState var1) {
      Intrinsics.checkNotNullParameter(var0, "networkResponse");
      Intrinsics.checkNotNullParameter(var1, "topAppBarUiState");
      return new ConversationUiState$Error(NetworkResponseKt.isRetryable(var0), var1);
   }
}
