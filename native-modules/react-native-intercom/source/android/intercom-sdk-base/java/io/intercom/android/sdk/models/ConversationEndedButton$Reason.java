package io.intercom.android.sdk.models;

import com.google.gson.annotations.SerializedName;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0005\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005¨\u0006\u0006",
   d2 = {
         "Lio/intercom/android/sdk/models/ConversationEndedButton$Reason;",
         "",
         "<init>",
         "(Ljava/lang/String;I)V",
         "UNKNOWN",
         "MERGED_INTO_CONVERSATION",
         "intercom-sdk-base_release"
   }
)
public enum ConversationEndedButton$Reason {
   UNKNOWN,
   @SerializedName("merged_into_conversation")
   MERGED_INTO_CONVERSATION;

   // $VF: synthetic field
   private static final EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(ConversationEndedButton$Reason.values());

   @NotNull
   public static EnumEntries getEntries() {
      return $ENTRIES;
   }

   // $VF: synthetic method

}
