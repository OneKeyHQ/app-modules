package io.intercom.android.sdk.tickets;

import androidx.annotation.DrawableRes;
import androidx.compose.ui.graphics.ColorKt;
import io.intercom.android.sdk.R.drawable;
import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\f\b\u0080\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u001b\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007R\u0013\u0010\u0002\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\n\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fj\u0002\b\rj\u0002\b\u000ej\u0002\b\u000fj\u0002\b\u0010¨\u0006\u0011",
   d2 = {
         "Lio/intercom/android/sdk/tickets/TicketStatus;",
         "",
         "color",
         "Landroidx/compose/ui/graphics/Color;",
         "iconRes",
         "",
         "<init>",
         "(Ljava/lang/String;IJI)V",
         "getColor-0d7_KjU",
         "()J",
         "J",
         "getIconRes",
         "()I",
         "Submitted",
         "InProgress",
         "WaitingOnCustomer",
         "Resolved",
         "intercom-sdk-base_release"
   }
)
public enum TicketStatus {
   Submitted(ColorKt.Color(4278212607L), drawable.intercom_ticket_submitted_icon),
   InProgress(ColorKt.Color(4278212607L), drawable.intercom_ticket_submitted_icon),
   WaitingOnCustomer(ColorKt.Color(4291644690L), drawable.intercom_ticket_waiting_icon),
   Resolved(ColorKt.Color(4279072050L), drawable.intercom_ticket_resolved_icon);

   private final long color;
   private final int iconRes;
   // $VF: synthetic field
   private static final EnumEntries $ENTRIES = EnumEntriesKt.enumEntries(TicketStatus.values());

   TicketStatus(long var3, @DrawableRes int var5) {
      this.color = var3;
      this.iconRes = var5;
   }

   public final long recovered$6f15fcf6c761ad06() {
      return this.color;
   }

   public final int getIconRes() {
      return this.iconRes;
   }

   @NotNull
   public static EnumEntries getEntries() {
      return $ENTRIES;
   }

   // $VF: synthetic method

}
