package io.intercom.android.sdk.tickets;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\bp\u0018\u00002\u00020\u0001:\u0002\b\tR\u0018\u0010\u0002\u001a\u00020\u0003X¦\u0004¢\u0006\f\u0012\u0004\b\u0004\u0010\u0005\u001a\u0004\b\u0006\u0010\u0007\u0082\u0001\u0002\n\u000b¨\u0006\f",
   d2 = {
         "Lio/intercom/android/sdk/tickets/TicketLaunchedFrom;",
         "",
         "from",
         "",
         "getFrom$annotations",
         "()V",
         "getFrom",
         "()Ljava/lang/String;",
         "Conversation",
         "Other",
         "Lio/intercom/android/sdk/tickets/TicketLaunchedFrom$Conversation;",
         "Lio/intercom/android/sdk/tickets/TicketLaunchedFrom$Other;",
         "intercom-sdk-base_release"
   }
)
public interface TicketLaunchedFrom {
   @NotNull
   String getFrom();
}
