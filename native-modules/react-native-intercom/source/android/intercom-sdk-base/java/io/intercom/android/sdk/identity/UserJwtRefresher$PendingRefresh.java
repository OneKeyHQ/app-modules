package io.intercom.android.sdk.identity;

import java.util.concurrent.CountDownLatch;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0002\b\f\b\u0002\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\nR\u0011\u0010\u0005\u001a\u00020\u0006¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u000e\u001a\u00020\u000f¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u001a\u0010\u0012\u001a\u00020\u0013X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0015\"\u0004\b\u0016\u0010\u0017R\u001a\u0010\u0018\u001a\u00020\u0013X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0019\u0010\u0015\"\u0004\b\u001a\u0010\u0017R\u001c\u0010\u001b\u001a\u0004\u0018\u00010\u0003X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u001c\u0010\n\"\u0004\b\u001d\u0010\u001e¨\u0006\u001f",
   d2 = {
         "Lio/intercom/android/sdk/identity/UserJwtRefresher$PendingRefresh;",
         "",
         "staleJwt",
         "",
         "fingerprint",
         "deadlineMillis",
         "",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;J)V",
         "getStaleJwt",
         "()Ljava/lang/String;",
         "getFingerprint",
         "getDeadlineMillis",
         "()J",
         "latch",
         "Ljava/util/concurrent/CountDownLatch;",
         "getLatch",
         "()Ljava/util/concurrent/CountDownLatch;",
         "timedOut",
         "",
         "getTimedOut",
         "()Z",
         "setTimedOut",
         "(Z)V",
         "answered",
         "getAnswered",
         "setAnswered",
         "result",
         "getResult",
         "setResult",
         "(Ljava/lang/String;)V",
         "intercom-sdk-base_release"
   }
)
final class UserJwtRefresher$PendingRefresh {
   @NotNull
   private final String staleJwt;
   @NotNull
   private final String fingerprint;
   private final long deadlineMillis;
   @NotNull
   private final CountDownLatch latch;
   private boolean timedOut;
   private boolean answered;
   @Nullable
   private volatile String result;

   public UserJwtRefresher$PendingRefresh(@NotNull String var1, @NotNull String var2, long var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "staleJwt");
      Intrinsics.checkNotNullParameter(var2, "fingerprint");
      this.staleJwt = var1;
      this.fingerprint = var2;
      this.deadlineMillis = var3;
      this.latch = new CountDownLatch(1);
   }

   @NotNull
   public final String getStaleJwt() {
      return this.staleJwt;
   }

   @NotNull
   public final String getFingerprint() {
      return this.fingerprint;
   }

   public final long getDeadlineMillis() {
      return this.deadlineMillis;
   }

   @NotNull
   public final CountDownLatch getLatch() {
      return this.latch;
   }

   public final boolean getTimedOut() {
      return this.timedOut;
   }

   public final void setTimedOut(boolean var1) {
      this.timedOut = var1;
   }

   public final boolean getAnswered() {
      return this.answered;
   }

   public final void setAnswered(boolean var1) {
      this.answered = var1;
   }

   @Nullable
   public final String getResult() {
      return this.result;
   }

   public final void setResult(@Nullable String var1) {
      this.result = var1;
   }
}
