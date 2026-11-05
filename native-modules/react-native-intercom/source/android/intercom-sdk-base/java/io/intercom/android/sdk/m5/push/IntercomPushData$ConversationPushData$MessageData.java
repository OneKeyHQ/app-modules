package io.intercom.android.sdk.m5.push;

import kotlin.Metadata;
import kotlinx.serialization.Serializable;
import org.jetbrains.annotations.NotNull;

@Serializable
@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\bw\u0018\u0000 \u00052\u00020\u0001:\u0004\u0002\u0003\u0004\u0005\u0082\u0001\u0003\u0006\u0007\b¨\u0006\t",
   d2 = {
         "Lio/intercom/android/sdk/m5/push/IntercomPushData$ConversationPushData$MessageData;",
         "",
         "Text",
         "Image",
         "Attachment",
         "Companion",
         "Lio/intercom/android/sdk/m5/push/IntercomPushData$ConversationPushData$MessageData$Attachment;",
         "Lio/intercom/android/sdk/m5/push/IntercomPushData$ConversationPushData$MessageData$Image;",
         "Lio/intercom/android/sdk/m5/push/IntercomPushData$ConversationPushData$MessageData$Text;",
         "intercom-sdk-base_release"
   }
)
public interface IntercomPushData$ConversationPushData$MessageData {
   @NotNull
   IntercomPushData$ConversationPushData$MessageData$Companion Companion = IntercomPushData$ConversationPushData$MessageData$Companion.$$INSTANCE;
}
