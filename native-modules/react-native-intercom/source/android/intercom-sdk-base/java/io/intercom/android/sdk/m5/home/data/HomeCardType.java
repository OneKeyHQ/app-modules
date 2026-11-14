package io.intercom.android.sdk.m5.home.data;

import com.google.gson.annotations.SerializedName;
import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u000b\b\u0080\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002¢\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006j\u0002\b\u0007j\u0002\b\bj\u0002\b\tj\u0002\b\nj\u0002\b\u000b¨\u0006\f",
   d2 = {
         "Lio/intercom/android/sdk/m5/home/data/HomeCardType;",
         "",
         "<init>",
         "(Ljava/lang/String;I)V",
         "SPACES",
         "RECENT_CONVERSATION",
         "NEW_CONVERSATION",
         "HELP_CENTER",
         "EXTERNAL_LINKS",
         "MESSENGER_APP",
         "RECENT_TICKETS",
         "TICKET_LINKS",
         "intercom-sdk-base_release"
   }
)
public enum HomeCardType {
   @SerializedName("spaces")
   SPACES,
   @SerializedName("recent_conversation")
   RECENT_CONVERSATION,
   @SerializedName("new_conversation")
   NEW_CONVERSATION,
   @SerializedName("help_center")
   HELP_CENTER,
   @SerializedName("external_links")
   EXTERNAL_LINKS,
   @SerializedName("messenger_app")
   MESSENGER_APP,
   @SerializedName("recent_tickets")
   RECENT_TICKETS,
   @SerializedName("ticket_links")
   TICKET_LINKS;

   // $VF: synthetic field
   private static final EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(HomeCardType.values());

   @NotNull
   public static EnumEntries getEntries() {
      return $ENTRIES;
   }

   // $VF: synthetic method

}
