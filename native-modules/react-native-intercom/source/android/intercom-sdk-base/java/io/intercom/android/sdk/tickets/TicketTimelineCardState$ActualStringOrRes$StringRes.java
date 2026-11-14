package io.intercom.android.sdk.tickets;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u0011\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003HÆ\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0003\u0010\u0002\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH×\u0003J\t\u0010\u000e\u001a\u00020\u0003H×\u0001J\t\u0010\u000f\u001a\u00020\u0010H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0011",
   d2 = {
         "Lio/intercom/android/sdk/tickets/TicketTimelineCardState$ActualStringOrRes$StringRes;",
         "Lio/intercom/android/sdk/tickets/TicketTimelineCardState$ActualStringOrRes;",
         "stringRes",
         "",
         "<init>",
         "(I)V",
         "getStringRes",
         "()I",
         "component1",
         "copy",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class TicketTimelineCardState$ActualStringOrRes$StringRes extends TicketTimelineCardState$ActualStringOrRes {
   private final int stringRes;
   public static int $stable;

   static {}

   public TicketTimelineCardState$ActualStringOrRes$StringRes(@androidx.annotation.StringRes int var1) {
      super(null);
      this.stringRes = var1;
   }

   public final int getStringRes() {
      return this.stringRes;
   }

   public final int component1() {
      return this.stringRes;
   }

   @NotNull
   public final TicketTimelineCardState$ActualStringOrRes$StringRes copy(@androidx.annotation.StringRes int var1) {
      return new TicketTimelineCardState$ActualStringOrRes$StringRes(var1);
   }

   // $VF: synthetic method
   public static TicketTimelineCardState$ActualStringOrRes$StringRes copy$default(
      TicketTimelineCardState$ActualStringOrRes$StringRes var0, int var1, int var2, Object var3
   ) {
      if ((var2 & 1) != 0) {
         var1 = var0.stringRes;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "StringRes(stringRes=" + this.stringRes + ')';
   }

   @Override
   public int hashCode() {
      return Integer.hashCode(this.stringRes);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof TicketTimelineCardState$ActualStringOrRes$StringRes)) {
         return false;
      }

      TicketTimelineCardState$ActualStringOrRes$StringRes var2 = (TicketTimelineCardState$ActualStringOrRes$StringRes)var1;
      return this.stringRes == var2.stringRes;
   }
}
