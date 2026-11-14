package io.intercom.android.sdk.tickets.list.data;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0081\b\u0018\u00002\u00020\u0001B\u001f\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006¢\u0006\u0004\b\u0007\u0010\bJ\u000f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0006HÆ\u0003¢\u0006\u0002\u0010\fJ*\u0010\u0010\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0006HÇ\u0001¢\u0006\u0002\u0010\u0011J\u0013\u0010\u0012\u001a\u00020\u00132\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0015\u001a\u00020\u0016H×\u0001J\t\u0010\u0017\u001a\u00020\u0018H×\u0001R\u001c\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u001a\u0010\u0005\u001a\u0004\u0018\u00010\u00068\u0006X\u0087\u0004¢\u0006\n\n\u0002\u0010\r\u001a\u0004\b\u000b\u0010\f¨\u0006\u0019",
   d2 = {
         "Lio/intercom/android/sdk/tickets/list/data/TicketsResponse;",
         "",
         "tickets",
         "",
         "Lio/intercom/android/sdk/models/Ticket;",
         "nextPage",
         "",
         "<init>",
         "(Ljava/util/List;Ljava/lang/Long;)V",
         "getTickets",
         "()Ljava/util/List;",
         "getNextPage",
         "()Ljava/lang/Long;",
         "Ljava/lang/Long;",
         "component1",
         "component2",
         "copy",
         "(Ljava/util/List;Ljava/lang/Long;)Lio/intercom/android/sdk/tickets/list/data/TicketsResponse;",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class TicketsResponse {
   @SerializedName("tickets")
   @NotNull
   private final List tickets;
   @SerializedName("next_page")
   @Nullable
   private final Long nextPage;
   public static final int $stable;

   static { $stable = 8; }

   public TicketsResponse(@NotNull List var1, @Nullable Long var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "tickets");
      this.tickets = var1;
      this.nextPage = var2;
   }

   @NotNull
   public final List getTickets() {
      return this.tickets;
   }

   @Nullable
   public final Long getNextPage() {
      return this.nextPage;
   }

   @NotNull
   public final List component1() {
      return this.tickets;
   }

   @Nullable
   public final Long component2() {
      return this.nextPage;
   }

   @NotNull
   public final TicketsResponse copy(@NotNull List var1, @Nullable Long var2) {
      Intrinsics.checkNotNullParameter(var1, "tickets");
      return new TicketsResponse(var1, var2);
   }

   // $VF: synthetic method
   public static TicketsResponse copy$default(TicketsResponse var0, List var1, Long var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.tickets;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.nextPage;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "TicketsResponse(tickets=" + this.tickets + ", nextPage=" + this.nextPage + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.tickets.hashCode();
      return var1 * 31 + (this.nextPage == null ? 0 : this.nextPage.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof TicketsResponse)) {
         return false;
      }

      TicketsResponse var2 = (TicketsResponse)var1;
      return !Intrinsics.areEqual(this.tickets, var2.tickets) ? false : Intrinsics.areEqual(this.nextPage, var2.nextPage);
   }
}
