package io.intercom.android.sdk.m5.conversation.states;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000X\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0013\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\bp\u0018\u00002\u00020\u0001:\u0011\u0006\u0007\b\t\n\u000b\f\r\u000e\u000f\u0010\u0011\u0012\u0013\u0014\u0015\u0016R\u0012\u0010\u0002\u001a\u00020\u0003X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0004\u0010\u0005\u0082\u0001\u0011\u0017\u0018\u0019\u001a\u001b\u001c\u001d\u001e\u001f !\"#$%&'¨\u0006(",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow;",
         "",
         "key",
         "",
         "getKey",
         "()Ljava/lang/String;",
         "SpecialNoticeRow",
         "TeamIntroRow",
         "BigTicketRow",
         "EventRow",
         "TicketStatusRow",
         "MergedConversationRow",
         "AskedAboutRow",
         "BubbleMessageRow",
         "PostCardRow",
         "NoteCardRow",
         "ComposerSuggestionRow",
         "QuickRepliesRow",
         "DayDividerRow",
         "FinAnswerRow",
         "TypingIndicatorRow",
         "NewMessagesRow",
         "FooterNoticeRow",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow$AskedAboutRow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow$BigTicketRow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow$BubbleMessageRow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow$ComposerSuggestionRow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow$DayDividerRow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow$EventRow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow$FinAnswerRow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow$FooterNoticeRow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow$MergedConversationRow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow$NewMessagesRow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow$NoteCardRow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow$PostCardRow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow$QuickRepliesRow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow$SpecialNoticeRow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow$TeamIntroRow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow$TicketStatusRow;",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow$TypingIndicatorRow;",
         "intercom-sdk-base_release"
   }
)
public interface ContentRow {
   @NotNull
   String getKey();
}
