package io.intercom.android.sdk.tickets;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\t\u0010\n\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000b\u001a\u00020\u0003HÆ\u0003J\u001d\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\r\u001a\u00020\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0010H×\u0003J\t\u0010\u0011\u001a\u00020\u0012H×\u0001J\t\u0010\u0013\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0014\u0010\u0004\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\b¨\u0006\u0014",
   d2 = {
         "Lio/intercom/android/sdk/tickets/TicketLaunchedFrom$Other;",
         "Lio/intercom/android/sdk/tickets/TicketLaunchedFrom;",
         "ticketId",
         "",
         "from",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;)V",
         "getTicketId",
         "()Ljava/lang/String;",
         "getFrom",
         "component1",
         "component2",
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
@StabilityInferred(parameters = 1)
public final class TicketLaunchedFrom$Other implements TicketLaunchedFrom {
   @NotNull
   private final String ticketId;
   @NotNull
   private final String from;
   public static int $stable;

   static {}

   public TicketLaunchedFrom$Other(@NotNull String var1, @NotNull String var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "ticketId");
      Intrinsics.checkNotNullParameter(var2, "from");
      this.ticketId = var1;
      this.from = var2;
   }

   @NotNull
   public final String getTicketId() {
      return this.ticketId;
   }

   @NotNull
   @Override
   public String getFrom() {
      return this.from;
   }

   @NotNull
   public final String component1() {
      return this.ticketId;
   }

   @NotNull
   public final String component2() {
      return this.from;
   }

   @NotNull
   public final TicketLaunchedFrom$Other copy(@NotNull String var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "ticketId");
      Intrinsics.checkNotNullParameter(var2, "from");
      return new TicketLaunchedFrom$Other(var1, var2);
   }

   // $VF: synthetic method
   public static TicketLaunchedFrom$Other copy$default(TicketLaunchedFrom$Other var0, String var1, String var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.ticketId;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.from;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "Other(ticketId=" + this.ticketId + ", from=" + this.from + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.ticketId.hashCode();
      return var1 * 31 + this.from.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof TicketLaunchedFrom$Other)) {
         return false;
      }

      TicketLaunchedFrom$Other var2 = (TicketLaunchedFrom$Other)var1;
      return !Intrinsics.areEqual(this.ticketId, var2.ticketId) ? false : Intrinsics.areEqual(this.from, var2.from);
   }
}
