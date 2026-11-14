package io.intercom.android.sdk.m5.home.states;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.helpcenter.utils.networking.NetworkResponse;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\r\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0081\b\u0018\u00002\u00020\u0001B5\u0012\u0010\b\u0002\u0010\u0002\u001a\n\u0012\u0004\u0012\u00020\u0004\u0018\u00010\u0003\u0012\u0010\b\u0002\u0010\u0005\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nJ\u0011\u0010\u000f\u001a\n\u0012\u0004\u0012\u00020\u0004\u0018\u00010\u0003HÆ\u0003J\u0011\u0010\u0010\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\bHÆ\u0003J7\u0010\u0012\u001a\u00020\u00002\u0010\b\u0002\u0010\u0002\u001a\n\u0012\u0004\u0012\u00020\u0004\u0018\u00010\u00032\u0010\b\u0002\u0010\u0005\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010\u00032\b\b\u0002\u0010\u0007\u001a\u00020\bHÇ\u0001J\u0013\u0010\u0013\u001a\u00020\b2\b\u0010\u0014\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0015\u001a\u00020\u0016H×\u0001J\t\u0010\u0017\u001a\u00020\u0018H×\u0001R\u0019\u0010\u0002\u001a\n\u0012\u0004\u0012\u00020\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0019\u0010\u0005\u001a\n\u0012\u0004\u0012\u00020\u0006\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\fR\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\u000e¨\u0006\u0019",
   d2 = {
         "Lio/intercom/android/sdk/m5/home/states/HomeClientState;",
         "",
         "homeResponse",
         "Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "Lio/intercom/android/sdk/m5/home/data/HomeV2Response;",
         "openMessengerResponse",
         "Lio/intercom/android/sdk/models/OpenMessengerResponse;",
         "isHeaderImageLoaded",
         "",
         "<init>",
         "(Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;Z)V",
         "getHomeResponse",
         "()Lio/intercom/android/sdk/helpcenter/utils/networking/NetworkResponse;",
         "getOpenMessengerResponse",
         "()Z",
         "component1",
         "component2",
         "component3",
         "copy",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class HomeClientState {
   @Nullable
   private final NetworkResponse homeResponse;
   @Nullable
   private final NetworkResponse openMessengerResponse;
   private final boolean isHeaderImageLoaded;
   public static int $stable;

   static {}

   public HomeClientState(@Nullable NetworkResponse var1, @Nullable NetworkResponse var2, boolean var3) {
      this.homeResponse = var1;
      this.openMessengerResponse = var2;
      this.isHeaderImageLoaded = var3;
   }

   // $VF: synthetic method
   public HomeClientState(NetworkResponse var1, NetworkResponse var2, boolean var3, int var4, DefaultConstructorMarker var5) {
      this((((var4 & 1) != 0) ? (NetworkResponse) (null) : var1), (((var4 & 2) != 0) ? (NetworkResponse) (null) : var2), (((var4 & 4) != 0) ? (boolean) (false) : var3));
   }

   @Nullable
   public final NetworkResponse getHomeResponse() {
      return this.homeResponse;
   }

   @Nullable
   public final NetworkResponse getOpenMessengerResponse() {
      return this.openMessengerResponse;
   }

   public final boolean isHeaderImageLoaded() {
      return this.isHeaderImageLoaded;
   }

   @Nullable
   public final NetworkResponse component1() {
      return this.homeResponse;
   }

   @Nullable
   public final NetworkResponse component2() {
      return this.openMessengerResponse;
   }

   public final boolean component3() {
      return this.isHeaderImageLoaded;
   }

   @NotNull
   public final HomeClientState copy(@Nullable NetworkResponse var1, @Nullable NetworkResponse var2, boolean var3) {
      return new HomeClientState(var1, var2, var3);
   }

   // $VF: synthetic method
   public static HomeClientState copy$default(HomeClientState var0, NetworkResponse var1, NetworkResponse var2, boolean var3, int var4, Object var5) {
      if ((var4 & 1) != 0) {
         var1 = var0.homeResponse;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.openMessengerResponse;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.isHeaderImageLoaded;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "HomeClientState(homeResponse="
         + this.homeResponse
         + ", openMessengerResponse="
         + this.openMessengerResponse
         + ", isHeaderImageLoaded="
         + this.isHeaderImageLoaded
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.homeResponse == null ? 0 : this.homeResponse.hashCode();
      var1 = var1 * 31 + (this.openMessengerResponse == null ? 0 : this.openMessengerResponse.hashCode());
      return var1 * 31 + Boolean.hashCode(this.isHeaderImageLoaded);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof HomeClientState)) {
         return false;
      } else {
         HomeClientState var2 = (HomeClientState)var1;
         if (!Intrinsics.areEqual(this.homeResponse, var2.homeResponse)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.openMessengerResponse, var2.openMessengerResponse) ? false : this.isHeaderImageLoaded == var2.isHeaderImageLoaded;
         }
      }
   }

   public HomeClientState() {
      this(null, null, false, 7, null);
   }
}
