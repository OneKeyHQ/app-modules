package io.intercom.android.sdk.tickets;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u001e\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0002\b\n\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\t\u0010\b\u001a\u00020\u0003HÆ\u0003J\t\u0010\t\u001a\u00020\u0003HÆ\u0003J\u001d\u0010\n\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\u000b\u001a\u00020\u00032\b\u0010\f\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\r\u001a\u00020\u000eH×\u0001J\t\u0010\u000f\u001a\u00020\u0010H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0002\u0010\u0007R\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0004\u0010\u0007¨\u0006\u0011",
   d2 = {
         "Lio/intercom/android/sdk/tickets/TicketTimelineCardState$ProgressSection;",
         "",
         "isDone",
         "",
         "isLoading",
         "<init>",
         "(ZZ)V",
         "()Z",
         "component1",
         "component2",
         "copy",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class TicketTimelineCardState$ProgressSection {
   private final boolean isDone;
   private final boolean isLoading;
   public static int $stable;

   static {}

   public TicketTimelineCardState$ProgressSection(boolean var1, boolean var2) {
      this.isDone = var1;
      this.isLoading = var2;
   }

   public final boolean isDone() {
      return this.isDone;
   }

   public final boolean isLoading() {
      return this.isLoading;
   }

   public final boolean component1() {
      return this.isDone;
   }

   public final boolean component2() {
      return this.isLoading;
   }

   @NotNull
   public final TicketTimelineCardState$ProgressSection copy(boolean var1, boolean var2) {
      return new TicketTimelineCardState$ProgressSection(var1, var2);
   }

   // $VF: synthetic method
   public static TicketTimelineCardState$ProgressSection copy$default(
      TicketTimelineCardState$ProgressSection var0, boolean var1, boolean var2, int var3, Object var4
   ) {
      if ((var3 & 1) != 0) {
         var1 = var0.isDone;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.isLoading;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "ProgressSection(isDone=" + this.isDone + ", isLoading=" + this.isLoading + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Boolean.hashCode(this.isDone);
      return var1 * 31 + Boolean.hashCode(this.isLoading);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof TicketTimelineCardState$ProgressSection)) {
         return false;
      }

      TicketTimelineCardState$ProgressSection var2 = (TicketTimelineCardState$ProgressSection)var1;
      return this.isDone != var2.isDone ? false : this.isLoading == var2.isLoading;
   }
}
