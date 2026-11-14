package io.intercom.android.sdk.identity;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u001b\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\t\u0010\n\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000b\u001a\u00020\u0003HÆ\u0003J\u001d\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\r\u001a\u00020\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0010\u001a\u00020\u0011H×\u0001J\t\u0010\u0012\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\b¨\u0006\u0013",
   d2 = {
         "Lio/intercom/android/sdk/identity/AppIdentityStore$AppCredentials;",
         "",
         "apiKey",
         "",
         "appId",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;)V",
         "getApiKey",
         "()Ljava/lang/String;",
         "getAppId",
         "component1",
         "component2",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class AppIdentityStore$AppCredentials {
   @NotNull
   private final String apiKey;
   @NotNull
   private final String appId;
   public static int $stable;

   static {}

   public AppIdentityStore$AppCredentials(@NotNull String var1, @NotNull String var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "apiKey");
      Intrinsics.checkNotNullParameter(var2, "appId");
      this.apiKey = var1;
      this.appId = var2;
   }

   // $VF: synthetic method
   public AppIdentityStore$AppCredentials(String var1, String var2, int var3, DefaultConstructorMarker var4) {
      this((((var3 & 1) != 0) ? (String) ("") : var1), (((var3 & 2) != 0) ? (String) ("") : var2));
   }

   @NotNull
   public final String getApiKey() {
      return this.apiKey;
   }

   @NotNull
   public final String getAppId() {
      return this.appId;
   }

   @NotNull
   public final String component1() {
      return this.apiKey;
   }

   @NotNull
   public final String component2() {
      return this.appId;
   }

   @NotNull
   public final AppIdentityStore$AppCredentials copy(@NotNull String var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "apiKey");
      Intrinsics.checkNotNullParameter(var2, "appId");
      return new AppIdentityStore$AppCredentials(var1, var2);
   }

   // $VF: synthetic method
   public static AppIdentityStore$AppCredentials copy$default(AppIdentityStore$AppCredentials var0, String var1, String var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.apiKey;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.appId;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "AppCredentials(apiKey=" + this.apiKey + ", appId=" + this.appId + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.apiKey.hashCode();
      return var1 * 31 + this.appId.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof AppIdentityStore$AppCredentials)) {
         return false;
      }

      AppIdentityStore$AppCredentials var2 = (AppIdentityStore$AppCredentials)var1;
      return !Intrinsics.areEqual(this.apiKey, var2.apiKey) ? false : Intrinsics.areEqual(this.appId, var2.appId);
   }

   public AppIdentityStore$AppCredentials() {
      this(null, null, 3, null);
   }
}
