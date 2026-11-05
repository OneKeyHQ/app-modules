package io.intercom.android.sdk.m5.push;

import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001c\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\bp\u0018\u0000 \b2\u00020\u0001:\u0003\u0006\u0007\bR\u0012\u0010\u0002\u001a\u00020\u0003X¦\u0004¢\u0006\u0006\u001a\u0004\b\u0004\u0010\u0005\u0082\u0001\u0002\t\n¨\u0006\u000b",
   d2 = {
         "Lio/intercom/android/sdk/m5/push/IntercomPushData;",
         "",
         "notificationId",
         "",
         "getNotificationId",
         "()I",
         "ConversationPushData",
         "DeepLinkPushData",
         "Companion",
         "Lio/intercom/android/sdk/m5/push/IntercomPushData$ConversationPushData;",
         "Lio/intercom/android/sdk/m5/push/IntercomPushData$DeepLinkPushData;",
         "intercom-sdk-base_release"
   }
)
public interface IntercomPushData {
   @NotNull
   IntercomPushData$Companion Companion = IntercomPushData$Companion.$$INSTANCE;

   int getNotificationId();
}
