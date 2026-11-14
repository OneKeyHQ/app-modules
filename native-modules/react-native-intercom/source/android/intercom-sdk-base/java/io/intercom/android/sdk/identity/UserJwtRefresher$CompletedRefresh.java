package io.intercom.android.sdk.identity;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\b\u0002\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003¢\u0006\u0004\b\u0006\u0010\u0007R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\t¨\u0006\f",
   d2 = {
         "Lio/intercom/android/sdk/identity/UserJwtRefresher$CompletedRefresh;",
         "",
         "staleJwt",
         "",
         "fingerprint",
         "jwt",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
         "getStaleJwt",
         "()Ljava/lang/String;",
         "getFingerprint",
         "getJwt",
         "intercom-sdk-base_release"
   }
)
final class UserJwtRefresher$CompletedRefresh {
   @NotNull
   private final String staleJwt;
   @NotNull
   private final String fingerprint;
   @NotNull
   private final String jwt;

   public UserJwtRefresher$CompletedRefresh(@NotNull String var1, @NotNull String var2, @NotNull String var3) {
      super();
      Intrinsics.checkNotNullParameter(var1, "staleJwt");
      Intrinsics.checkNotNullParameter(var2, "fingerprint");
      Intrinsics.checkNotNullParameter(var3, "jwt");
      this.staleJwt = var1;
      this.fingerprint = var2;
      this.jwt = var3;
   }

   @NotNull
   public final String getStaleJwt() {
      return this.staleJwt;
   }

   @NotNull
   public final String getFingerprint() {
      return this.fingerprint;
   }

   @NotNull
   public final String getJwt() {
      return this.jwt;
   }
}
