package io.intercom.android.sdk.m5.conversation.data;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b1\u0018\u00002\u00020\u0001:\u0004\u0004\u0005\u0006\u0007B\t\b\u0005¢\u0006\u0004\b\u0002\u0010\u0003\u0082\u0001\u0004\b\t\n\u000b¨\u0006\f",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedNexusEvent;",
         "",
         "<init>",
         "()V",
         "ConversationNexusEvent",
         "NexusConnected",
         "AblyReconnectedWithoutResume",
         "UnSupportedEvent",
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedNexusEvent$AblyReconnectedWithoutResume;",
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedNexusEvent$ConversationNexusEvent;",
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedNexusEvent$NexusConnected;",
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedNexusEvent$UnSupportedEvent;",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public abstract class ParsedNexusEvent {
   public static int $stable;

   static {}

   private ParsedNexusEvent() {
   }

   // $VF: synthetic method
   public ParsedNexusEvent(DefaultConstructorMarker var1) {
      this();
   }
}
