package io.intercom.android.sdk.tickets.list.data;

import androidx.compose.runtime.internal.StabilityInferred;
import androidx.paging.compose.LazyPagingItems;
import io.intercom.android.sdk.m5.components.ErrorState;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00006\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0010\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B3\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b\u0012\u0006\u0010\t\u001a\u00020\n¢\u0006\u0004\b\u000b\u0010\fJ\u000f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\t\u0010\u0015\u001a\u00020\u0006HÆ\u0003J\u000b\u0010\u0016\u001a\u0004\u0018\u00010\bHÆ\u0003J\t\u0010\u0017\u001a\u00020\nHÆ\u0003J9\u0010\u0018\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b2\b\b\u0002\u0010\t\u001a\u00020\nHÇ\u0001J\u0013\u0010\u0019\u001a\u00020\u00062\b\u0010\u001a\u001a\u0004\u0018\u00010\u001bH×\u0003J\t\u0010\u001c\u001a\u00020\u001dH×\u0001J\t\u0010\u001e\u001a\u00020\nH×\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\u0005\u0010\u000fR\u0013\u0010\u0007\u001a\u0004\u0018\u00010\b¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0014\u0010\t\u001a\u00020\nX\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013¨\u0006\u001f",
   d2 = {
         "Lio/intercom/android/sdk/tickets/list/data/TicketsScreenUiState$Content;",
         "Lio/intercom/android/sdk/tickets/list/data/TicketsScreenUiState;",
         "lazyPagingTickets",
         "Landroidx/paging/compose/LazyPagingItems;",
         "Lio/intercom/android/sdk/tickets/list/ui/TicketRowData;",
         "isLoadingMore",
         "",
         "errorState",
         "Lio/intercom/android/sdk/m5/components/ErrorState;",
         "screenLabel",
         "",
         "<init>",
         "(Landroidx/paging/compose/LazyPagingItems;ZLio/intercom/android/sdk/m5/components/ErrorState;Ljava/lang/String;)V",
         "getLazyPagingTickets",
         "()Landroidx/paging/compose/LazyPagingItems;",
         "()Z",
         "getErrorState",
         "()Lio/intercom/android/sdk/m5/components/ErrorState;",
         "getScreenLabel",
         "()Ljava/lang/String;",
         "component1",
         "component2",
         "component3",
         "component4",
         "copy",
         "equals",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class TicketsScreenUiState$Content extends TicketsScreenUiState {
   @NotNull
   private final LazyPagingItems lazyPagingTickets;
   private final boolean isLoadingMore;
   @Nullable
   private final ErrorState errorState;
   @NotNull
   private final String screenLabel;
   public static final int $stable = LazyPagingItems.$stable;

   public TicketsScreenUiState$Content(@NotNull LazyPagingItems var1, boolean var2, @Nullable ErrorState var3, @NotNull String var4) {
      Intrinsics.checkNotNullParameter(var1, "lazyPagingTickets");
      Intrinsics.checkNotNullParameter(var4, "screenLabel");
      super(null);
      this.lazyPagingTickets = var1;
      this.isLoadingMore = var2;
      this.errorState = var3;
      this.screenLabel = var4;
   }

   // $VF: synthetic method
   public TicketsScreenUiState$Content(LazyPagingItems var1, boolean var2, ErrorState var3, String var4, int var5, DefaultConstructorMarker var6) {
      this(var1, (((var5 & 2) != 0) ? (boolean) (false) : var2), (((var5 & 4) != 0) ? (ErrorState) (null) : var3), var4);
   }

   @NotNull
   public final LazyPagingItems getLazyPagingTickets() {
      return this.lazyPagingTickets;
   }

   public final boolean isLoadingMore() {
      return this.isLoadingMore;
   }

   @Nullable
   public final ErrorState getErrorState() {
      return this.errorState;
   }

   @NotNull
   @Override
   public String getScreenLabel() {
      return this.screenLabel;
   }

   @NotNull
   public final LazyPagingItems component1() {
      return this.lazyPagingTickets;
   }

   public final boolean component2() {
      return this.isLoadingMore;
   }

   @Nullable
   public final ErrorState component3() {
      return this.errorState;
   }

   @NotNull
   public final String component4() {
      return this.screenLabel;
   }

   @NotNull
   public final TicketsScreenUiState$Content copy(@NotNull LazyPagingItems var1, boolean var2, @Nullable ErrorState var3, @NotNull String var4) {
      Intrinsics.checkNotNullParameter(var1, "lazyPagingTickets");
      Intrinsics.checkNotNullParameter(var4, "screenLabel");
      return new TicketsScreenUiState$Content(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static TicketsScreenUiState$Content copy$default(
      TicketsScreenUiState$Content var0, LazyPagingItems var1, boolean var2, ErrorState var3, String var4, int var5, Object var6
   ) {
      if ((var5 & 1) != 0) {
         var1 = var0.lazyPagingTickets;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.isLoadingMore;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.errorState;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.screenLabel;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "Content(lazyPagingTickets="
         + this.lazyPagingTickets
         + ", isLoadingMore="
         + this.isLoadingMore
         + ", errorState="
         + this.errorState
         + ", screenLabel="
         + this.screenLabel
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.lazyPagingTickets.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.isLoadingMore);
      var1 = var1 * 31 + (this.errorState == null ? 0 : this.errorState.hashCode());
      return var1 * 31 + this.screenLabel.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof TicketsScreenUiState$Content)) {
         return false;
      } else {
         TicketsScreenUiState$Content var2 = (TicketsScreenUiState$Content)var1;
         if (!Intrinsics.areEqual(this.lazyPagingTickets, var2.lazyPagingTickets)) {
            return false;
         } else if (this.isLoadingMore != var2.isLoadingMore) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.errorState, var2.errorState) ? false : Intrinsics.areEqual(this.screenLabel, var2.screenLabel);
         }
      }
   }
}
