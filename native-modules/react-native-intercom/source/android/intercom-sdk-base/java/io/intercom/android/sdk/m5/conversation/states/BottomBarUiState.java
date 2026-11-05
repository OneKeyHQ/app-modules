package io.intercom.android.sdk.m5.conversation.states;

import androidx.compose.runtime.internal.StabilityInferred;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\b\u0081\b\u0018\u00002\u00020\u0001:\u0002\"#B)\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0002\u0010\b\u001a\u00020\t¢\u0006\u0004\b\n\u0010\u000bJ\t\u0010\u0017\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0018\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0019\u001a\u00020\u0007HÆ\u0003J\t\u0010\u001a\u001a\u00020\tHÆ\u0003J1\u0010\u001b\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\tHÇ\u0001J\u0013\u0010\u001c\u001a\u00020\u00152\b\u0010\u001d\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u001e\u001a\u00020\u001fH×\u0001J\t\u0010 \u001a\u00020!H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\b\u001a\u00020\t¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0014\u001a\u00020\u00158F¢\u0006\u0006\u001a\u0004\b\u0014\u0010\u0016¨\u0006$",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/BottomBarUiState;",
         "",
         "composerState",
         "Lio/intercom/android/sdk/m5/conversation/states/ComposerState;",
         "currentlyTypingState",
         "Lio/intercom/android/sdk/m5/conversation/states/CurrentlyTypingState;",
         "inputTypeState",
         "Lio/intercom/android/sdk/m5/conversation/states/InputTypeState;",
         "bottomBadge",
         "Lio/intercom/android/sdk/m5/conversation/states/BottomBarUiState$BottomBadgeState;",
         "<init>",
         "(Lio/intercom/android/sdk/m5/conversation/states/ComposerState;Lio/intercom/android/sdk/m5/conversation/states/CurrentlyTypingState;Lio/intercom/android/sdk/m5/conversation/states/InputTypeState;Lio/intercom/android/sdk/m5/conversation/states/BottomBarUiState$BottomBadgeState;)V",
         "getComposerState",
         "()Lio/intercom/android/sdk/m5/conversation/states/ComposerState;",
         "getCurrentlyTypingState",
         "()Lio/intercom/android/sdk/m5/conversation/states/CurrentlyTypingState;",
         "getInputTypeState",
         "()Lio/intercom/android/sdk/m5/conversation/states/InputTypeState;",
         "getBottomBadge",
         "()Lio/intercom/android/sdk/m5/conversation/states/BottomBarUiState$BottomBadgeState;",
         "isMaxMediaItemsReached",
         "",
         "()Z",
         "component1",
         "component2",
         "component3",
         "component4",
         "copy",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "",
         "BottomBarButton",
         "BottomBadgeState",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class BottomBarUiState {
   @NotNull
   private final ComposerState composerState;
   @NotNull
   private final CurrentlyTypingState currentlyTypingState;
   @NotNull
   private final InputTypeState inputTypeState;
   @NotNull
   private final BottomBarUiState$BottomBadgeState bottomBadge;
   public static final int $stable;

   static { $stable = 8; }

   public BottomBarUiState(
      @NotNull ComposerState var1, @NotNull CurrentlyTypingState var2, @NotNull InputTypeState var3, @NotNull BottomBarUiState$BottomBadgeState var4
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "composerState");
      Intrinsics.checkNotNullParameter(var2, "currentlyTypingState");
      Intrinsics.checkNotNullParameter(var3, "inputTypeState");
      Intrinsics.checkNotNullParameter(var4, "bottomBadge");
      this.composerState = var1;
      this.currentlyTypingState = var2;
      this.inputTypeState = var3;
      this.bottomBadge = var4;
   }

   // $VF: synthetic method
   public BottomBarUiState(
      ComposerState var1, CurrentlyTypingState var2, InputTypeState var3, BottomBarUiState$BottomBadgeState var4, int var5, DefaultConstructorMarker var6
   ) {
      this(var1, var2, var3, (((var5 & 8) != 0) ? (BottomBarUiState$BottomBadgeState) (BottomBarUiState$BottomBadgeState$None.INSTANCE) : var4));
   }

   @NotNull
   public final ComposerState getComposerState() {
      return this.composerState;
   }

   @NotNull
   public final CurrentlyTypingState getCurrentlyTypingState() {
      return this.currentlyTypingState;
   }

   @NotNull
   public final InputTypeState getInputTypeState() {
      return this.inputTypeState;
   }

   @NotNull
   public final BottomBarUiState$BottomBadgeState getBottomBadge() {
      return this.bottomBadge;
   }

   public final boolean isMaxMediaItemsReached() {
      ComposerState var1 = this.composerState;
      ComposerState$TextInput var10000 = var1 instanceof ComposerState$TextInput ? (ComposerState$TextInput)var1 : null;
      if ((var1 instanceof ComposerState$TextInput ? (ComposerState$TextInput)var1 : null) != null) {
         List var2 = var10000.getMediaUploadItems();
         if (var2 != null) {
            return var2.size() >= 10;
         }
      }

      return 0 >= 10;
   }

   @NotNull
   public final ComposerState component1() {
      return this.composerState;
   }

   @NotNull
   public final CurrentlyTypingState component2() {
      return this.currentlyTypingState;
   }

   @NotNull
   public final InputTypeState component3() {
      return this.inputTypeState;
   }

   @NotNull
   public final BottomBarUiState$BottomBadgeState component4() {
      return this.bottomBadge;
   }

   @NotNull
   public final BottomBarUiState copy(
      @NotNull ComposerState var1, @NotNull CurrentlyTypingState var2, @NotNull InputTypeState var3, @NotNull BottomBarUiState$BottomBadgeState var4
   ) {
      Intrinsics.checkNotNullParameter(var1, "composerState");
      Intrinsics.checkNotNullParameter(var2, "currentlyTypingState");
      Intrinsics.checkNotNullParameter(var3, "inputTypeState");
      Intrinsics.checkNotNullParameter(var4, "bottomBadge");
      return new BottomBarUiState(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static BottomBarUiState copy$default(
      BottomBarUiState var0, ComposerState var1, CurrentlyTypingState var2, InputTypeState var3, BottomBarUiState$BottomBadgeState var4, int var5, Object var6
   ) {
      if ((var5 & 1) != 0) {
         var1 = var0.composerState;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.currentlyTypingState;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.inputTypeState;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.bottomBadge;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "BottomBarUiState(composerState="
         + this.composerState
         + ", currentlyTypingState="
         + this.currentlyTypingState
         + ", inputTypeState="
         + this.inputTypeState
         + ", bottomBadge="
         + this.bottomBadge
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.composerState.hashCode();
      var1 = var1 * 31 + this.currentlyTypingState.hashCode();
      var1 = var1 * 31 + this.inputTypeState.hashCode();
      return var1 * 31 + this.bottomBadge.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof BottomBarUiState)) {
         return false;
      } else {
         BottomBarUiState var2 = (BottomBarUiState)var1;
         if (!Intrinsics.areEqual(this.composerState, var2.composerState)) {
            return false;
         } else if (!Intrinsics.areEqual(this.currentlyTypingState, var2.currentlyTypingState)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.inputTypeState, var2.inputTypeState) ? false : Intrinsics.areEqual(this.bottomBadge, var2.bottomBadge);
         }
      }
   }
}
