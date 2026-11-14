package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005¢\u0006\u0004\b\u0007\u0010\bJ\t\u0010\r\u001a\u00020\u0003HÆ\u0003J\u000f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005HÆ\u0003J#\u0010\u000f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\u000e\b\u0002\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u0005HÇ\u0001J\u0013\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0013\u001a\u00020\u0014H×\u0001J\t\u0010\u0015\u001a\u00020\u0003H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u001c\u0010\u0004\u001a\b\u0012\u0004\u0012\u00020\u00060\u00058\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f¨\u0006\u0016",
   d2 = {
         "Lio/intercom/android/sdk/models/AblyConnectResponse;",
         "",
         "channel",
         "",
         "endpoints",
         "",
         "Lio/intercom/android/sdk/models/AblyEndpoint;",
         "<init>",
         "(Ljava/lang/String;Ljava/util/List;)V",
         "getChannel",
         "()Ljava/lang/String;",
         "getEndpoints",
         "()Ljava/util/List;",
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
@StabilityInferred(parameters = 0)
public final class AblyConnectResponse {
   @SerializedName("channel")
   @NotNull
   private final String channel;
   @SerializedName("endpoints")
   @NotNull
   private final List endpoints;
   public static final int $stable;

   static { $stable = 8; }

   public AblyConnectResponse(@NotNull String var1, @NotNull List var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "channel");
      Intrinsics.checkNotNullParameter(var2, "endpoints");
      this.channel = var1;
      this.endpoints = var2;
   }

   // $VF: synthetic method
   public AblyConnectResponse(String var1, List var2, int var3, DefaultConstructorMarker var4) {
      this(var1, (((var3 & 2) != 0) ? (List) (CollectionsKt.emptyList()) : var2));
   }

   @NotNull
   public final String getChannel() {
      return this.channel;
   }

   @NotNull
   public final List getEndpoints() {
      return this.endpoints;
   }

   @NotNull
   public final String component1() {
      return this.channel;
   }

   @NotNull
   public final List component2() {
      return this.endpoints;
   }

   @NotNull
   public final AblyConnectResponse copy(@NotNull String var1, @NotNull List var2) {
      Intrinsics.checkNotNullParameter(var1, "channel");
      Intrinsics.checkNotNullParameter(var2, "endpoints");
      return new AblyConnectResponse(var1, var2);
   }

   // $VF: synthetic method
   public static AblyConnectResponse copy$default(AblyConnectResponse var0, String var1, List var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.channel;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.endpoints;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "AblyConnectResponse(channel=" + this.channel + ", endpoints=" + this.endpoints + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.channel.hashCode();
      return var1 * 31 + this.endpoints.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof AblyConnectResponse)) {
         return false;
      }

      AblyConnectResponse var2 = (AblyConnectResponse)var1;
      return !Intrinsics.areEqual(this.channel, var2.channel) ? false : Intrinsics.areEqual(this.endpoints, var2.endpoints);
   }
}
