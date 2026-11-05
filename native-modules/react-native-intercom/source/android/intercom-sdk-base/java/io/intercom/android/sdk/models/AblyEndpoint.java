package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B9\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0010\b\u0002\u0010\u0004\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\b\u0010\tJ\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\u0011\u0010\u0011\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u0005HÆ\u0003J\u000b\u0010\u0012\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010\u0013\u001a\u0004\u0018\u00010\u0003HÆ\u0003J=\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u0010\b\u0002\u0010\u0004\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0003HÇ\u0001J\u0013\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0018\u001a\u00020\u0019H×\u0001J\t\u0010\u001a\u001a\u00020\u0003H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u001e\u0010\u0004\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0018\u0010\u0006\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000bR\u0018\u0010\u0007\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u000b¨\u0006\u001b",
   d2 = {
         "Lio/intercom/android/sdk/models/AblyEndpoint;",
         "",
         "endpoint",
         "",
         "fallback",
         "",
         "connectivityCheck",
         "wsConnectivityCheck",
         "<init>",
         "(Ljava/lang/String;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;)V",
         "getEndpoint",
         "()Ljava/lang/String;",
         "getFallback",
         "()Ljava/util/List;",
         "getConnectivityCheck",
         "getWsConnectivityCheck",
         "component1",
         "component2",
         "component3",
         "component4",
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
@StabilityInferred(parameters = 0)
public final class AblyEndpoint {
   @SerializedName("endpoint")
   @NotNull
   private final String endpoint;
   @SerializedName("fallback")
   @Nullable
   private final List fallback;
   @SerializedName("connectivity_check")
   @Nullable
   private final String connectivityCheck;
   @SerializedName("ws_connectivity_check")
   @Nullable
   private final String wsConnectivityCheck;
   public static final int $stable;

   static { $stable = 8; }

   public AblyEndpoint(@NotNull String var1, @Nullable List var2, @Nullable String var3, @Nullable String var4) {
      super();
      Intrinsics.checkNotNullParameter(var1, "endpoint");
      this.endpoint = var1;
      this.fallback = var2;
      this.connectivityCheck = var3;
      this.wsConnectivityCheck = var4;
   }

   // $VF: synthetic method
   public AblyEndpoint(String var1, List var2, String var3, String var4, int var5, DefaultConstructorMarker var6) {
      this(var1, (((var5 & 2) != 0) ? (List) (null) : var2), (((var5 & 4) != 0) ? (String) (null) : var3), (((var5 & 8) != 0) ? (String) (null) : var4));
   }

   @NotNull
   public final String getEndpoint() {
      return this.endpoint;
   }

   @Nullable
   public final List getFallback() {
      return this.fallback;
   }

   @Nullable
   public final String getConnectivityCheck() {
      return this.connectivityCheck;
   }

   @Nullable
   public final String getWsConnectivityCheck() {
      return this.wsConnectivityCheck;
   }

   @NotNull
   public final String component1() {
      return this.endpoint;
   }

   @Nullable
   public final List component2() {
      return this.fallback;
   }

   @Nullable
   public final String component3() {
      return this.connectivityCheck;
   }

   @Nullable
   public final String component4() {
      return this.wsConnectivityCheck;
   }

   @NotNull
   public final AblyEndpoint copy(@NotNull String var1, @Nullable List var2, @Nullable String var3, @Nullable String var4) {
      Intrinsics.checkNotNullParameter(var1, "endpoint");
      return new AblyEndpoint(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static AblyEndpoint copy$default(AblyEndpoint var0, String var1, List var2, String var3, String var4, int var5, Object var6) {
      if ((var5 & 1) != 0) {
         var1 = var0.endpoint;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.fallback;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.connectivityCheck;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.wsConnectivityCheck;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "AblyEndpoint(endpoint="
         + this.endpoint
         + ", fallback="
         + this.fallback
         + ", connectivityCheck="
         + this.connectivityCheck
         + ", wsConnectivityCheck="
         + this.wsConnectivityCheck
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.endpoint.hashCode();
      var1 = var1 * 31 + (this.fallback == null ? 0 : this.fallback.hashCode());
      var1 = var1 * 31 + (this.connectivityCheck == null ? 0 : this.connectivityCheck.hashCode());
      return var1 * 31 + (this.wsConnectivityCheck == null ? 0 : this.wsConnectivityCheck.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof AblyEndpoint)) {
         return false;
      } else {
         AblyEndpoint var2 = (AblyEndpoint)var1;
         if (!Intrinsics.areEqual(this.endpoint, var2.endpoint)) {
            return false;
         } else if (!Intrinsics.areEqual(this.fallback, var2.fallback)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.connectivityCheck, var2.connectivityCheck)
               ? false
               : Intrinsics.areEqual(this.wsConnectivityCheck, var2.wsConnectivityCheck);
         }
      }
   }
}
