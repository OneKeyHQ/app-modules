package io.intercom.android.sdk.m5.conversation.data;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b1\u0018\u00002\u00020\u0001:\u0006\u0004\u0005\u0006\u0007\b\tB\t\b\u0005¢\u0006\u0004\b\u0002\u0010\u0003\u0082\u0001\u0006\n\u000b\f\r\u000e\u000f¨\u0006\u0010",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedAblyEvent;",
         "",
         "<init>",
         "()V",
         "AblyConnected",
         "AblyClosed",
         "AblyFailed",
         "UnSupportedEvent",
         "AblyReconnectedWithoutResume",
         "ConversationAblyEvent",
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedAblyEvent$AblyClosed;",
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedAblyEvent$AblyConnected;",
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedAblyEvent$AblyFailed;",
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedAblyEvent$AblyReconnectedWithoutResume;",
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedAblyEvent$ConversationAblyEvent;",
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedAblyEvent$UnSupportedEvent;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public abstract class ParsedAblyEvent {
   public static int $stable;

   static {}

   private ParsedAblyEvent() {
   }

   // $VF: synthetic method
   public ParsedAblyEvent(DefaultConstructorMarker var1) {
      this();
   }
}
