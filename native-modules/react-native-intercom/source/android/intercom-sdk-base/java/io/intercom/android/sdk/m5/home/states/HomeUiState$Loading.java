package io.intercom.android.sdk.m5.home.states;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.models.PoweredBy;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u0011\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\u0004\u0010\u0005J\u000b\u0010\b\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u0015\u0010\t\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003HÇ\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH×\u0003J\t\u0010\u000e\u001a\u00020\u000fH×\u0001J\t\u0010\u0010\u001a\u00020\u0011H×\u0001R\u0016\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007¨\u0006\u0012",
   d2 = {
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState$Loading;",
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState;",
         "poweredBy",
         "Lio/intercom/android/sdk/models/PoweredBy;",
         "<init>",
         "(Lio/intercom/android/sdk/models/PoweredBy;)V",
         "getPoweredBy",
         "()Lio/intercom/android/sdk/models/PoweredBy;",
         "component1",
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
@StabilityInferred(parameters = 1)
public final class HomeUiState$Loading extends HomeUiState {
   @Nullable
   private final PoweredBy poweredBy;
   public static int $stable;

   static {}

   public HomeUiState$Loading(@Nullable PoweredBy var1) {
      super(null);
      this.poweredBy = var1;
   }

   @Nullable
   @Override
   public PoweredBy getPoweredBy() {
      return this.poweredBy;
   }

   @Nullable
   public final PoweredBy component1() {
      return this.poweredBy;
   }

   @NotNull
   public final HomeUiState$Loading copy(@Nullable PoweredBy var1) {
      return new HomeUiState$Loading(var1);
   }

   // $VF: synthetic method
   public static HomeUiState$Loading copy$default(HomeUiState$Loading var0, PoweredBy var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = var0.poweredBy;
      }

      return var0.copy(var1);
   }

   @NotNull
   @Override
   public String toString() {
      return "Loading(poweredBy=" + this.poweredBy + ')';
   }

   @Override
   public int hashCode() {
      return this.poweredBy == null ? 0 : this.poweredBy.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof HomeUiState$Loading)) {
         return false;
      }

      HomeUiState$Loading var2 = (HomeUiState$Loading)var1;
      return Intrinsics.areEqual(this.poweredBy, var2.poweredBy);
   }
}
