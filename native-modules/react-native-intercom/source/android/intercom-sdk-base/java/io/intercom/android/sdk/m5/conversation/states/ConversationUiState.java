package io.intercom.android.sdk.m5.conversation.states;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000 \n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\bp\u0018\u00002\u00020\u0001:\u0003\u0006\u0007\bR\u0012\u0010\u0002\u001a\u00020\u0003X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0004\u0010\u0005\u0082\u0001\u0003\t\n\u000b¨\u0006\f",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationUiState;",
         "",
         "topAppBarUiState",
         "Lio/intercom/android/sdk/m5/conversation/states/TopAppBarUiState;",
         "getTopAppBarUiState",
         "()Lio/intercom/android/sdk/m5/conversation/states/TopAppBarUiState;",
         "Loading",
         "Content",
         "Error",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationUiState$Content;",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationUiState$Error;",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationUiState$Loading;",
         "intercom-sdk-base_release"
   }
)
public interface ConversationUiState {
   @NotNull
   TopAppBarUiState getTopAppBarUiState();
}
