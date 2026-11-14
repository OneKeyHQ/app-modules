package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003HÆ\u0003J\t\u0010\r\u001a\u00020\u0005HÆ\u0003J\u001d\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÇ\u0001J\u0013\u0010\u000f\u001a\u00020\u00032\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H×\u0003J\t\u0010\u0012\u001a\u00020\u0013H×\u0001J\t\u0010\u0014\u001a\u00020\u0015H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0014\u0010\u0004\u001a\u00020\u0005X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000b¨\u0006\u0016",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationUiState$Error;",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationUiState;",
         "showCta",
         "",
         "topAppBarUiState",
         "Lio/intercom/android/sdk/m5/conversation/states/TopAppBarUiState;",
         "<init>",
         "(ZLio/intercom/android/sdk/m5/conversation/states/TopAppBarUiState;)V",
         "getShowCta",
         "()Z",
         "getTopAppBarUiState",
         "()Lio/intercom/android/sdk/m5/conversation/states/TopAppBarUiState;",
         "component1",
         "component2",
         "copy",
         "equals",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class ConversationUiState$Error implements ConversationUiState {
   private final boolean showCta;
   @NotNull
   private final TopAppBarUiState topAppBarUiState;
   public static final int $stable;

   static { $stable = 8; }

   public ConversationUiState$Error(boolean var1, @NotNull TopAppBarUiState var2) {
      super();
      Intrinsics.checkNotNullParameter(var2, "topAppBarUiState");
      this.showCta = var1;
      this.topAppBarUiState = var2;
   }

   public final boolean getShowCta() {
      return this.showCta;
   }

   @NotNull
   @Override
   public TopAppBarUiState getTopAppBarUiState() {
      return this.topAppBarUiState;
   }

   public final boolean component1() {
      return this.showCta;
   }

   @NotNull
   public final TopAppBarUiState component2() {
      return this.topAppBarUiState;
   }

   @NotNull
   public final ConversationUiState$Error copy(boolean var1, @NotNull TopAppBarUiState var2) {
      Intrinsics.checkNotNullParameter(var2, "topAppBarUiState");
      return new ConversationUiState$Error(var1, var2);
   }

   // $VF: synthetic method
   public static ConversationUiState$Error copy$default(ConversationUiState$Error var0, boolean var1, TopAppBarUiState var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.showCta;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.topAppBarUiState;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "Error(showCta=" + this.showCta + ", topAppBarUiState=" + this.topAppBarUiState + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Boolean.hashCode(this.showCta);
      return var1 * 31 + this.topAppBarUiState.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof ConversationUiState$Error)) {
         return false;
      }

      ConversationUiState$Error var2 = (ConversationUiState$Error)var1;
      return this.showCta != var2.showCta ? false : Intrinsics.areEqual(this.topAppBarUiState, var2.topAppBarUiState);
   }
}
