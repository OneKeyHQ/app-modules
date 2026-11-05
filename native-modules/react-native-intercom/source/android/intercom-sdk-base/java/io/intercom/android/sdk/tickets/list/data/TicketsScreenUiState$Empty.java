package io.intercom.android.sdk.tickets.list.data;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.models.EmptyState;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\t\u0010\r\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÇ\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H×\u0003J\t\u0010\u0013\u001a\u00020\u0014H×\u0001J\t\u0010\u0015\u001a\u00020\u0005H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0014\u0010\u0004\u001a\u00020\u0005X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0016",
   d2 = {
         "Lio/intercom/android/sdk/tickets/list/data/TicketsScreenUiState$Empty;",
         "Lio/intercom/android/sdk/tickets/list/data/TicketsScreenUiState;",
         "emptyState",
         "Lio/intercom/android/sdk/models/EmptyState;",
         "screenLabel",
         "",
         "<init>",
         "(Lio/intercom/android/sdk/models/EmptyState;Ljava/lang/String;)V",
         "getEmptyState",
         "()Lio/intercom/android/sdk/models/EmptyState;",
         "getScreenLabel",
         "()Ljava/lang/String;",
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
public final class TicketsScreenUiState$Empty extends TicketsScreenUiState {
   @NotNull
   private final EmptyState emptyState;
   @NotNull
   private final String screenLabel;
   public static int $stable;

   static {}

   public TicketsScreenUiState$Empty(@NotNull EmptyState var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "emptyState");
      Intrinsics.checkNotNullParameter(var2, "screenLabel");
      super(null);
      this.emptyState = var1;
      this.screenLabel = var2;
   }

   @NotNull
   public final EmptyState getEmptyState() {
      return this.emptyState;
   }

   @NotNull
   @Override
   public String getScreenLabel() {
      return this.screenLabel;
   }

   @NotNull
   public final EmptyState component1() {
      return this.emptyState;
   }

   @NotNull
   public final String component2() {
      return this.screenLabel;
   }

   @NotNull
   public final TicketsScreenUiState$Empty copy(@NotNull EmptyState var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "emptyState");
      Intrinsics.checkNotNullParameter(var2, "screenLabel");
      return new TicketsScreenUiState$Empty(var1, var2);
   }

   // $VF: synthetic method
   public static TicketsScreenUiState$Empty copy$default(TicketsScreenUiState$Empty var0, EmptyState var1, String var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.emptyState;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.screenLabel;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "Empty(emptyState=" + this.emptyState + ", screenLabel=" + this.screenLabel + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.emptyState.hashCode();
      return var1 * 31 + this.screenLabel.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof TicketsScreenUiState$Empty)) {
         return false;
      }

      TicketsScreenUiState$Empty var2 = (TicketsScreenUiState$Empty)var1;
      return !Intrinsics.areEqual(this.emptyState, var2.emptyState) ? false : Intrinsics.areEqual(this.screenLabel, var2.screenLabel);
   }
}
