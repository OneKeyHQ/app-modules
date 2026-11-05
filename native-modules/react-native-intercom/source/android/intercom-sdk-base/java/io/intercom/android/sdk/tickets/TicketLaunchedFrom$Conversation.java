package io.intercom.android.sdk.tickets;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.models.Ticket;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u0011\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\r\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H×\u0003J\t\u0010\u0012\u001a\u00020\u0013H×\u0001J\t\u0010\u0014\u001a\u00020\tH×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0014\u0010\b\u001a\u00020\tX\u0096D¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0015",
   d2 = {
         "Lio/intercom/android/sdk/tickets/TicketLaunchedFrom$Conversation;",
         "Lio/intercom/android/sdk/tickets/TicketLaunchedFrom;",
         "ticket",
         "Lio/intercom/android/sdk/models/Ticket;",
         "<init>",
         "(Lio/intercom/android/sdk/models/Ticket;)V",
         "getTicket",
         "()Lio/intercom/android/sdk/models/Ticket;",
         "from",
         "",
         "getFrom",
         "()Ljava/lang/String;",
         "component1",
         "copy",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class TicketLaunchedFrom$Conversation implements TicketLaunchedFrom {
   @NotNull
   private final Ticket ticket;
   @NotNull
   private final String from;
   public static final int $stable;

   static { $stable = 8; }

   public TicketLaunchedFrom$Conversation(@NotNull Ticket var1) {
      super();
      Intrinsics.checkNotNullParameter(var1, "ticket");
      this.ticket = var1;
      this.from = "conversation";
   }

   // $VF: synthetic method
   public TicketLaunchedFrom$Conversation(Ticket var1, int var2, DefaultConstructorMarker var3) {
      this((((var2 & 1) != 0) ? (Ticket) ((Ticket)Injector.get().getDataLayer().getTicket().getValue()) : var1));
   }

   @NotNull
   public final Ticket getTicket() {
      return this.ticket;
   }

   @NotNull
   @Override
   public String getFrom() {
      return this.from;
   }

   @NotNull
   public final Ticket component1() {
      return this.ticket;
   }

   @NotNull
   public final TicketLaunchedFrom$Conversation copy(@NotNull Ticket var1) {
      Intrinsics.checkNotNullParameter(var1, "ticket");
      return new TicketLaunchedFrom$Conversation(var1);
   }

   // $VF: synthetic method
   public static TicketLaunchedFrom$Conversation copy$default(TicketLaunchedFrom$Conversation var0, Ticket var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = var0.ticket;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "Conversation(ticket=" + this.ticket + ')';
   }

   @Override
   public int hashCode() {
      return this.ticket.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof TicketLaunchedFrom$Conversation)) {
         return false;
      }

      TicketLaunchedFrom$Conversation var2 = (TicketLaunchedFrom$Conversation)var1;
      return Intrinsics.areEqual(this.ticket, var2.ticket);
   }

   public TicketLaunchedFrom$Conversation() {
      this(null, 1, null);
   }
}
