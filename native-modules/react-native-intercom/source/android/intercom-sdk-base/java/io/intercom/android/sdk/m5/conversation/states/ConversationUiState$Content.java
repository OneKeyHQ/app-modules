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
   d1 = "\u0000X\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u001c\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001BS\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\f\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\n\u0012\b\b\u0002\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\u000e\u0012\u0006\u0010\u000f\u001a\u00020\u0010\u0012\b\b\u0002\u0010\u0011\u001a\u00020\u0012¢\u0006\u0004\b\u0013\u0010\u0014J\t\u0010%\u001a\u00020\u0003HÆ\u0003J\u000f\u0010&\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005HÆ\u0003J\t\u0010'\u001a\u00020\bHÆ\u0003J\t\u0010(\u001a\u00020\nHÆ\u0003J\t\u0010)\u001a\u00020\fHÆ\u0003J\t\u0010*\u001a\u00020\u000eHÆ\u0003J\t\u0010+\u001a\u00020\u0010HÆ\u0003J\t\u0010,\u001a\u00020\u0012HÆ\u0003J_\u0010-\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u0011\u001a\u00020\u0012HÇ\u0001J\u0013\u0010.\u001a\u00020/2\b\u00100\u001a\u0004\u0018\u000101H×\u0003J\t\u00102\u001a\u000203H×\u0001J\t\u00104\u001a\u000205H×\u0001R\u0014\u0010\u0002\u001a\u00020\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0017\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\t\u001a\u00020\n¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0011\u0010\u000b\u001a\u00020\f¢\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0011\u0010\r\u001a\u00020\u000e¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010 R\u0011\u0010\u000f\u001a\u00020\u0010¢\u0006\b\n\u0000\u001a\u0004\b!\u0010\"R\u0011\u0010\u0011\u001a\u00020\u0012¢\u0006\b\n\u0000\u001a\u0004\b#\u0010$¨\u00066",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationUiState$Content;",
         "Lio/intercom/android/sdk/m5/conversation/states/ConversationUiState;",
         "topAppBarUiState",
         "Lio/intercom/android/sdk/m5/conversation/states/TopAppBarUiState;",
         "contentRows",
         "",
         "Lio/intercom/android/sdk/m5/conversation/states/ContentRow;",
         "bottomBarUiState",
         "Lio/intercom/android/sdk/m5/conversation/states/BottomBarUiState;",
         "networkState",
         "Lio/intercom/android/sdk/m5/conversation/states/NetworkState;",
         "bottomSheetState",
         "Lio/intercom/android/sdk/m5/conversation/states/BottomSheetState;",
         "floatingIndicatorState",
         "Lio/intercom/android/sdk/m5/conversation/states/FloatingIndicatorState;",
         "teamPresenceState",
         "Lio/intercom/android/sdk/m5/conversation/states/TeamPresenceState;",
         "voiceTranscriptionState",
         "Lio/intercom/android/sdk/m5/conversation/states/VoiceTranscriptionState;",
         "<init>",
         "(Lio/intercom/android/sdk/m5/conversation/states/TopAppBarUiState;Ljava/util/List;Lio/intercom/android/sdk/m5/conversation/states/BottomBarUiState;Lio/intercom/android/sdk/m5/conversation/states/NetworkState;Lio/intercom/android/sdk/m5/conversation/states/BottomSheetState;Lio/intercom/android/sdk/m5/conversation/states/FloatingIndicatorState;Lio/intercom/android/sdk/m5/conversation/states/TeamPresenceState;Lio/intercom/android/sdk/m5/conversation/states/VoiceTranscriptionState;)V",
         "getTopAppBarUiState",
         "()Lio/intercom/android/sdk/m5/conversation/states/TopAppBarUiState;",
         "getContentRows",
         "()Ljava/util/List;",
         "getBottomBarUiState",
         "()Lio/intercom/android/sdk/m5/conversation/states/BottomBarUiState;",
         "getNetworkState",
         "()Lio/intercom/android/sdk/m5/conversation/states/NetworkState;",
         "getBottomSheetState",
         "()Lio/intercom/android/sdk/m5/conversation/states/BottomSheetState;",
         "getFloatingIndicatorState",
         "()Lio/intercom/android/sdk/m5/conversation/states/FloatingIndicatorState;",
         "getTeamPresenceState",
         "()Lio/intercom/android/sdk/m5/conversation/states/TeamPresenceState;",
         "getVoiceTranscriptionState",
         "()Lio/intercom/android/sdk/m5/conversation/states/VoiceTranscriptionState;",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "component7",
         "component8",
         "copy",
         "equals",
         "",
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
public final class ConversationUiState$Content implements ConversationUiState {
   @NotNull
   private final TopAppBarUiState topAppBarUiState;
   @NotNull
   private final List contentRows;
   @NotNull
   private final BottomBarUiState bottomBarUiState;
   @NotNull
   private final NetworkState networkState;
   @NotNull
   private final BottomSheetState bottomSheetState;
   @NotNull
   private final FloatingIndicatorState floatingIndicatorState;
   @NotNull
   private final TeamPresenceState teamPresenceState;
   @NotNull
   private final VoiceTranscriptionState voiceTranscriptionState;
   public static final int $stable;

   static { $stable = 8; }

   public ConversationUiState$Content(
      @NotNull TopAppBarUiState var1,
      @NotNull List var2,
      @NotNull BottomBarUiState var3,
      @NotNull NetworkState var4,
      @NotNull BottomSheetState var5,
      @NotNull FloatingIndicatorState var6,
      @NotNull TeamPresenceState var7,
      @NotNull VoiceTranscriptionState var8
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "topAppBarUiState");
      Intrinsics.checkNotNullParameter(var2, "contentRows");
      Intrinsics.checkNotNullParameter(var3, "bottomBarUiState");
      Intrinsics.checkNotNullParameter(var4, "networkState");
      Intrinsics.checkNotNullParameter(var5, "bottomSheetState");
      Intrinsics.checkNotNullParameter(var6, "floatingIndicatorState");
      Intrinsics.checkNotNullParameter(var7, "teamPresenceState");
      Intrinsics.checkNotNullParameter(var8, "voiceTranscriptionState");
      this.topAppBarUiState = var1;
      this.contentRows = var2;
      this.bottomBarUiState = var3;
      this.networkState = var4;
      this.bottomSheetState = var5;
      this.floatingIndicatorState = var6;
      this.teamPresenceState = var7;
      this.voiceTranscriptionState = var8;
   }

   // $VF: synthetic method
   public ConversationUiState$Content(
      TopAppBarUiState var1,
      List var2,
      BottomBarUiState var3,
      NetworkState var4,
      BottomSheetState var5,
      FloatingIndicatorState var6,
      TeamPresenceState var7,
      VoiceTranscriptionState var8,
      int var9,
      DefaultConstructorMarker var10
   ) {
      this(var1, var2, var3, (((var9 & 8) != 0) ? (NetworkState) (NetworkState$Connected.INSTANCE) : var4), (((var9 & 16) != 0) ? (BottomSheetState) (BottomSheetState$Empty.INSTANCE) : var5), var6, var7, (((var9 & 128) != 0) ? (VoiceTranscriptionState) (VoiceTranscriptionState$Idle.INSTANCE) : var8));
   }

   @NotNull
   @Override
   public TopAppBarUiState getTopAppBarUiState() {
      return this.topAppBarUiState;
   }

   @NotNull
   public final List getContentRows() {
      return this.contentRows;
   }

   @NotNull
   public final BottomBarUiState getBottomBarUiState() {
      return this.bottomBarUiState;
   }

   @NotNull
   public final NetworkState getNetworkState() {
      return this.networkState;
   }

   @NotNull
   public final BottomSheetState getBottomSheetState() {
      return this.bottomSheetState;
   }

   @NotNull
   public final FloatingIndicatorState getFloatingIndicatorState() {
      return this.floatingIndicatorState;
   }

   @NotNull
   public final TeamPresenceState getTeamPresenceState() {
      return this.teamPresenceState;
   }

   @NotNull
   public final VoiceTranscriptionState getVoiceTranscriptionState() {
      return this.voiceTranscriptionState;
   }

   @NotNull
   public final TopAppBarUiState component1() {
      return this.topAppBarUiState;
   }

   @NotNull
   public final List component2() {
      return this.contentRows;
   }

   @NotNull
   public final BottomBarUiState component3() {
      return this.bottomBarUiState;
   }

   @NotNull
   public final NetworkState component4() {
      return this.networkState;
   }

   @NotNull
   public final BottomSheetState component5() {
      return this.bottomSheetState;
   }

   @NotNull
   public final FloatingIndicatorState component6() {
      return this.floatingIndicatorState;
   }

   @NotNull
   public final TeamPresenceState component7() {
      return this.teamPresenceState;
   }

   @NotNull
   public final VoiceTranscriptionState component8() {
      return this.voiceTranscriptionState;
   }

   @NotNull
   public final ConversationUiState$Content copy(
      @NotNull TopAppBarUiState var1,
      @NotNull List var2,
      @NotNull BottomBarUiState var3,
      @NotNull NetworkState var4,
      @NotNull BottomSheetState var5,
      @NotNull FloatingIndicatorState var6,
      @NotNull TeamPresenceState var7,
      @NotNull VoiceTranscriptionState var8
   ) {
      Intrinsics.checkNotNullParameter(var1, "topAppBarUiState");
      Intrinsics.checkNotNullParameter(var2, "contentRows");
      Intrinsics.checkNotNullParameter(var3, "bottomBarUiState");
      Intrinsics.checkNotNullParameter(var4, "networkState");
      Intrinsics.checkNotNullParameter(var5, "bottomSheetState");
      Intrinsics.checkNotNullParameter(var6, "floatingIndicatorState");
      Intrinsics.checkNotNullParameter(var7, "teamPresenceState");
      Intrinsics.checkNotNullParameter(var8, "voiceTranscriptionState");
      return new ConversationUiState$Content(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   // $VF: synthetic method
   public static ConversationUiState$Content copy$default(
      ConversationUiState$Content var0,
      TopAppBarUiState var1,
      List var2,
      BottomBarUiState var3,
      NetworkState var4,
      BottomSheetState var5,
      FloatingIndicatorState var6,
      TeamPresenceState var7,
      VoiceTranscriptionState var8,
      int var9,
      Object var10
   ) {
      if ((var9 & 1) != 0) {
         var1 = var0.topAppBarUiState;
      }

      if ((var9 & 2) != 0) {
         var2 = var0.contentRows;
      }

      if ((var9 & 4) != 0) {
         var3 = var0.bottomBarUiState;
      }

      if ((var9 & 8) != 0) {
         var4 = var0.networkState;
      }

      if ((var9 & 16) != 0) {
         var5 = var0.bottomSheetState;
      }

      if ((var9 & 32) != 0) {
         var6 = var0.floatingIndicatorState;
      }

      if ((var9 & 64) != 0) {
         var7 = var0.teamPresenceState;
      }

      if ((var9 & 128) != 0) {
         var8 = var0.voiceTranscriptionState;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @NotNull
   @Override
   public String toString() {
      return "Content(topAppBarUiState="
         + this.topAppBarUiState
         + ", contentRows="
         + this.contentRows
         + ", bottomBarUiState="
         + this.bottomBarUiState
         + ", networkState="
         + this.networkState
         + ", bottomSheetState="
         + this.bottomSheetState
         + ", floatingIndicatorState="
         + this.floatingIndicatorState
         + ", teamPresenceState="
         + this.teamPresenceState
         + ", voiceTranscriptionState="
         + this.voiceTranscriptionState
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.topAppBarUiState.hashCode();
      var1 = var1 * 31 + this.contentRows.hashCode();
      var1 = var1 * 31 + this.bottomBarUiState.hashCode();
      var1 = var1 * 31 + this.networkState.hashCode();
      var1 = var1 * 31 + this.bottomSheetState.hashCode();
      var1 = var1 * 31 + this.floatingIndicatorState.hashCode();
      var1 = var1 * 31 + this.teamPresenceState.hashCode();
      return var1 * 31 + this.voiceTranscriptionState.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof ConversationUiState$Content)) {
         return false;
      } else {
         ConversationUiState$Content var2 = (ConversationUiState$Content)var1;
         if (!Intrinsics.areEqual(this.topAppBarUiState, var2.topAppBarUiState)) {
            return false;
         } else if (!Intrinsics.areEqual(this.contentRows, var2.contentRows)) {
            return false;
         } else if (!Intrinsics.areEqual(this.bottomBarUiState, var2.bottomBarUiState)) {
            return false;
         } else if (!Intrinsics.areEqual(this.networkState, var2.networkState)) {
            return false;
         } else if (!Intrinsics.areEqual(this.bottomSheetState, var2.bottomSheetState)) {
            return false;
         } else if (!Intrinsics.areEqual(this.floatingIndicatorState, var2.floatingIndicatorState)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.teamPresenceState, var2.teamPresenceState)
               ? false
               : Intrinsics.areEqual(this.voiceTranscriptionState, var2.voiceTranscriptionState);
         }
      }
   }
}
