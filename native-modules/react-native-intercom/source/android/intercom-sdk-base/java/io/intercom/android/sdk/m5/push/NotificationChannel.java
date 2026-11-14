package io.intercom.android.sdk.m5.push;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\b\b\u0080\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0011\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\n¨\u0006\u000b",
   d2 = {
         "Lio/intercom/android/sdk/m5/push/NotificationChannel;",
         "",
         "channelName",
         "",
         "<init>",
         "(Ljava/lang/String;ILjava/lang/String;)V",
         "getChannelName",
         "()Ljava/lang/String;",
         "CHAT_REPLIES_CHANNEL",
         "NEW_CHATS_CHANNEL",
         "ACTIONS_CHANNEL",
         "intercom-sdk-base_release"
   }
)
public enum NotificationChannel {
   CHAT_REPLIES_CHANNEL("intercom_chat_replies_channel"),
   NEW_CHATS_CHANNEL("intercom_new_chats_channel"),
   ACTIONS_CHANNEL("intercom_actions_channel");

   @NotNull
   private final String channelName;
   // $VF: synthetic field
   private static final EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(NotificationChannel.values());

   NotificationChannel(String var3) {
      this.channelName = var3;
   }

   @NotNull
   public final String getChannelName() {
      return this.channelName;
   }

   @NotNull
   public static EnumEntries getEntries() {
      return $ENTRIES;
   }

   // $VF: synthetic method

}
