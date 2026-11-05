package io.intercom.android.sdk.tickets.create.model;

import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\b\u0080\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0011\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007j\u0002\b\bj\u0002\b\t¨\u0006\n",
   d2 = {
         "Lio/intercom/android/sdk/tickets/create/model/CreateTicketLaunchedFrom;",
         "",
         "from",
         "",
         "<init>",
         "(Ljava/lang/String;ILjava/lang/String;)V",
         "getFrom",
         "()Ljava/lang/String;",
         "Conversation",
         "Home",
         "intercom-sdk-base_release"
   }
)
public enum CreateTicketLaunchedFrom {
   Conversation("conversation"),
   Home("home_screen");

   @NotNull
   private final String from;
   // $VF: synthetic field
   private static final EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(CreateTicketLaunchedFrom.values());

   CreateTicketLaunchedFrom(String var3) {
      this.from = var3;
   }

   @NotNull
   public final String getFrom() {
      return this.from;
   }

   @NotNull
   public static EnumEntries getEntries() {
      return $ENTRIES;
   }

   // $VF: synthetic method

}
