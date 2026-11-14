package io.intercom.android.sdk.m5.conversation.data;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.nexus.NexusEventType;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\b\u0017\u0018\u00002\u00020\u0001:\u0004\f\r\u000e\u000fB\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007R\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0010",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedNexusEvent$ConversationNexusEvent;",
         "Lio/intercom/android/sdk/m5/conversation/data/ParsedNexusEvent;",
         "conversationId",
         "",
         "eventType",
         "Lio/intercom/android/nexus/NexusEventType;",
         "<init>",
         "(Ljava/lang/String;Lio/intercom/android/nexus/NexusEventType;)V",
         "getConversationId",
         "()Ljava/lang/String;",
         "getEventType",
         "()Lio/intercom/android/nexus/NexusEventType;",
         "NewComment",
         "AdminIsTyping",
         "UserContentSeenByAdmin",
         "FinStreaming",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public class ParsedNexusEvent$ConversationNexusEvent extends ParsedNexusEvent {
   @NotNull
   private final String conversationId;
   @NotNull
   private final NexusEventType eventType;
   public static int $stable;

   static {}

   public ParsedNexusEvent$ConversationNexusEvent(@NotNull String var1, @NotNull NexusEventType var2) {
      Intrinsics.checkNotNullParameter(var1, "conversationId");
      Intrinsics.checkNotNullParameter(var2, "eventType");
      super(null);
      this.conversationId = var1;
      this.eventType = var2;
   }

   @NotNull
   public String getConversationId() {
      return this.conversationId;
   }

   @NotNull
   public final NexusEventType getEventType() {
      return this.eventType;
   }
}
