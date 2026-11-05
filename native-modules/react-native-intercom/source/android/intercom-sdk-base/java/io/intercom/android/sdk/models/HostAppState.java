package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.jvm.JvmField;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\t\n\u0002\b\u001e\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0081\b\u0018\u0000 )2\u00020\u0001:\u0001)BE\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0007\u0012\b\b\u0002\u0010\t\u001a\u00020\u0005\u0012\b\b\u0002\u0010\n\u001a\u00020\u0005¢\u0006\u0004\b\u000b\u0010\fJ\t\u0010\u001b\u001a\u00020\u0003HÆ\u0003J\t\u0010\u001c\u001a\u00020\u0005HÆ\u0003J\t\u0010\u001d\u001a\u00020\u0007HÆ\u0003J\u0010\u0010\u001e\u001a\u0004\u0018\u00010\u0007HÆ\u0003¢\u0006\u0002\u0010\u0014J\t\u0010\u001f\u001a\u00020\u0005HÆ\u0003J\t\u0010 \u001a\u00020\u0005HÆ\u0003JL\u0010!\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00072\b\b\u0002\u0010\t\u001a\u00020\u00052\b\b\u0002\u0010\n\u001a\u00020\u0005HÇ\u0001¢\u0006\u0002\u0010\"J\u0013\u0010#\u001a\u00020\u00052\b\u0010$\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010%\u001a\u00020&H×\u0001J\t\u0010'\u001a\u00020(H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0015\u0010\b\u001a\u0004\u0018\u00010\u0007¢\u0006\n\n\u0002\u0010\u0015\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\t\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0010R\u0011\u0010\n\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0010R\u0011\u0010\u0018\u001a\u00020\u00058F¢\u0006\u0006\u001a\u0004\b\u0018\u0010\u0010R\u0011\u0010\u0019\u001a\u00020\u00058F¢\u0006\u0006\u001a\u0004\b\u0019\u0010\u0010R\u0011\u0010\u001a\u001a\u00020\u00058F¢\u0006\u0006\u001a\u0004\b\u001a\u0010\u0010¨\u0006*",
   d2 = {
         "Lio/intercom/android/sdk/models/HostAppState;",
         "",
         "lifecycleState",
         "Lio/intercom/android/sdk/models/AppLifecycleState;",
         "sessionStartedSinceLastBackgrounded",
         "",
         "backgroundedTimestamp",
         "",
         "sessionStartAttemptId",
         "sessionStartOwed",
         "sessionStartRetryRequested",
         "<init>",
         "(Lio/intercom/android/sdk/models/AppLifecycleState;ZJLjava/lang/Long;ZZ)V",
         "getLifecycleState",
         "()Lio/intercom/android/sdk/models/AppLifecycleState;",
         "getSessionStartedSinceLastBackgrounded",
         "()Z",
         "getBackgroundedTimestamp",
         "()J",
         "getSessionStartAttemptId",
         "()Ljava/lang/Long;",
         "Ljava/lang/Long;",
         "getSessionStartOwed",
         "getSessionStartRetryRequested",
         "isBackgrounded",
         "isForeground",
         "isUninitialized",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "copy",
         "(Lio/intercom/android/sdk/models/AppLifecycleState;ZJLjava/lang/Long;ZZ)Lio/intercom/android/sdk/models/HostAppState;",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "",
         "Companion",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class HostAppState {
   @NotNull
   public static final HostAppState$Companion Companion = new HostAppState$Companion(null);
   @NotNull
   private final AppLifecycleState lifecycleState;
   private final boolean sessionStartedSinceLastBackgrounded;
   private final long backgroundedTimestamp;
   @Nullable
   private final Long sessionStartAttemptId;
   private final boolean sessionStartOwed;
   private final boolean sessionStartRetryRequested;
   public static int $stable;

   static {}
   @JvmField
   @NotNull
   public static final HostAppState NULL = new HostAppState(null, false, 0L, null, false, false, 63, null);

   public HostAppState(@NotNull AppLifecycleState var1, boolean var2, long var3, @Nullable Long var5, boolean var6, boolean var7) {
      super();
      Intrinsics.checkNotNullParameter(var1, "lifecycleState");
      this.lifecycleState = var1;
      this.sessionStartedSinceLastBackgrounded = var2;
      this.backgroundedTimestamp = var3;
      this.sessionStartAttemptId = var5;
      this.sessionStartOwed = var6;
      this.sessionStartRetryRequested = var7;
   }

   // $VF: synthetic method
   public HostAppState(AppLifecycleState var1, boolean var2, long var3, Long var5, boolean var6, boolean var7, int var8, DefaultConstructorMarker var9) {
      this((((var8 & 1) != 0) ? (AppLifecycleState) (AppLifecycleState.UNINITIALIZED) : var1), (((var8 & 2) != 0) ? (boolean) (false) : var2), (((var8 & 4) != 0) ? (long) (0L) : var3), (((var8 & 8) != 0) ? (Long) (null) : var5), (((var8 & 16) != 0) ? (boolean) (false) : var6), (((var8 & 32) != 0) ? (boolean) (false) : var7));
   }

   @NotNull
   public final AppLifecycleState getLifecycleState() {
      return this.lifecycleState;
   }

   public final boolean getSessionStartedSinceLastBackgrounded() {
      return this.sessionStartedSinceLastBackgrounded;
   }

   public final long getBackgroundedTimestamp() {
      return this.backgroundedTimestamp;
   }

   @Nullable
   public final Long getSessionStartAttemptId() {
      return this.sessionStartAttemptId;
   }

   public final boolean getSessionStartOwed() {
      return this.sessionStartOwed;
   }

   public final boolean getSessionStartRetryRequested() {
      return this.sessionStartRetryRequested;
   }

   public final boolean isBackgrounded() {
      return this.lifecycleState == AppLifecycleState.BACKGROUND;
   }

   public final boolean isForeground() {
      return this.lifecycleState == AppLifecycleState.FOREGROUND;
   }

   public final boolean isUninitialized() {
      return this.lifecycleState == AppLifecycleState.UNINITIALIZED;
   }

   @NotNull
   public final AppLifecycleState component1() {
      return this.lifecycleState;
   }

   public final boolean component2() {
      return this.sessionStartedSinceLastBackgrounded;
   }

   public final long component3() {
      return this.backgroundedTimestamp;
   }

   @Nullable
   public final Long component4() {
      return this.sessionStartAttemptId;
   }

   public final boolean component5() {
      return this.sessionStartOwed;
   }

   public final boolean component6() {
      return this.sessionStartRetryRequested;
   }

   @NotNull
   public final HostAppState copy(@NotNull AppLifecycleState var1, boolean var2, long var3, @Nullable Long var5, boolean var6, boolean var7) {
      Intrinsics.checkNotNullParameter(var1, "lifecycleState");
      return new HostAppState(var1, var2, var3, var5, var6, var7);
   }

   // $VF: synthetic method
   public static HostAppState copy$default(
      HostAppState var0, AppLifecycleState var1, boolean var2, long var3, Long var5, boolean var6, boolean var7, int var8, Object var9
   ) {
      if ((var8 & 1) != 0) {
         var1 = var0.lifecycleState;
      }

      if ((var8 & 2) != 0) {
         var2 = var0.sessionStartedSinceLastBackgrounded;
      }

      if ((var8 & 4) != 0) {
         var3 = var0.backgroundedTimestamp;
      }

      if ((var8 & 8) != 0) {
         var5 = var0.sessionStartAttemptId;
      }

      if ((var8 & 16) != 0) {
         var6 = var0.sessionStartOwed;
      }

      if ((var8 & 32) != 0) {
         var7 = var0.sessionStartRetryRequested;
      }

      return var0.copy(var1, var2, var3, var5, var6, var7);
   }

   @NotNull
   @Override
   public String toString() {
      return "HostAppState(lifecycleState="
         + this.lifecycleState
         + ", sessionStartedSinceLastBackgrounded="
         + this.sessionStartedSinceLastBackgrounded
         + ", backgroundedTimestamp="
         + this.backgroundedTimestamp
         + ", sessionStartAttemptId="
         + this.sessionStartAttemptId
         + ", sessionStartOwed="
         + this.sessionStartOwed
         + ", sessionStartRetryRequested="
         + this.sessionStartRetryRequested
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.lifecycleState.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.sessionStartedSinceLastBackgrounded);
      var1 = var1 * 31 + Long.hashCode(this.backgroundedTimestamp);
      var1 = var1 * 31 + (this.sessionStartAttemptId == null ? 0 : this.sessionStartAttemptId.hashCode());
      var1 = var1 * 31 + Boolean.hashCode(this.sessionStartOwed);
      return var1 * 31 + Boolean.hashCode(this.sessionStartRetryRequested);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof HostAppState)) {
         return false;
      } else {
         HostAppState var2 = (HostAppState)var1;
         if (this.lifecycleState != var2.lifecycleState) {
            return false;
         } else if (this.sessionStartedSinceLastBackgrounded != var2.sessionStartedSinceLastBackgrounded) {
            return false;
         } else if (this.backgroundedTimestamp != var2.backgroundedTimestamp) {
            return false;
         } else if (!Intrinsics.areEqual(this.sessionStartAttemptId, var2.sessionStartAttemptId)) {
            return false;
         } else {
            return this.sessionStartOwed != var2.sessionStartOwed ? false : this.sessionStartRetryRequested == var2.sessionStartRetryRequested;
         }
      }
   }

   public HostAppState() {
      this(null, false, 0L, null, false, false, 63, null);
   }
}
