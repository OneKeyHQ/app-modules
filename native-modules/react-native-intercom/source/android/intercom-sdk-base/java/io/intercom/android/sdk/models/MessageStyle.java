package io.intercom.android.sdk.models;

import com.google.gson.annotations.SerializedName;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\f\b\u0080\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000bj\u0002\b\f¨\u0006\r",
   d2 = {
         "Lio/intercom/android/sdk/models/MessageStyle;",
         "",
         "<init>",
         "(Ljava/lang/String;I)V",
         "CHAT",
         "POST",
         "NOTE",
         "ADMIN_IS_TYPING_STYLE",
         "QUICK_REPLY",
         "ATTRIBUTE_COLLECTOR",
         "TICKET_STATE_UPDATED",
         "MERGED_PRIMARY_CONVERSATION",
         "FIN_ANSWER",
         "intercom-sdk-base_release"
   }
)
public enum MessageStyle {
   @SerializedName("chat")
   CHAT,
   @SerializedName("announcement")
   POST,
   @SerializedName("small-announcement")
   NOTE,
   @SerializedName("admin_is_typing_style")
   ADMIN_IS_TYPING_STYLE,
   @SerializedName("quick_reply")
   QUICK_REPLY,
   @SerializedName("attribute_collector")
   ATTRIBUTE_COLLECTOR,
   @SerializedName("ticket_state_updated")
   TICKET_STATE_UPDATED,
   @SerializedName("merged_primary_conversation")
   MERGED_PRIMARY_CONVERSATION,
   @SerializedName("fin_answer")
   FIN_ANSWER;

   // $VF: synthetic field
   private static final EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(MessageStyle.values());

   @NotNull
   public static EnumEntries getEntries() {
      return $ENTRIES;
   }

   // $VF: synthetic method

}
