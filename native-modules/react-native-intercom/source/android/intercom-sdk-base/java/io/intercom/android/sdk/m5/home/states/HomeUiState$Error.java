package io.intercom.android.sdk.m5.home.states;

import androidx.compose.runtime.internal.StabilityInferred;
import io.intercom.android.sdk.m5.components.ErrorState;
import io.intercom.android.sdk.models.PoweredBy;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00008\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001:\u0001\u001cB!\u0012\b\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\u000b\u0010\u0010\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0005HÆ\u0003J\t\u0010\u0012\u001a\u00020\u0007HÆ\u0003J)\u0010\u0013\u001a\u00020\u00002\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007HÇ\u0001J\u0013\u0010\u0014\u001a\u00020\u00152\b\u0010\u0016\u001a\u0004\u0018\u00010\u0017H×\u0003J\t\u0010\u0018\u001a\u00020\u0019H×\u0001J\t\u0010\u001a\u001a\u00020\u001bH×\u0001R\u0016\u0010\u0002\u001a\u0004\u0018\u00010\u0003X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u001d",
   d2 = {
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState$Error;",
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState;",
         "poweredBy",
         "Lio/intercom/android/sdk/models/PoweredBy;",
         "errorState",
         "Lio/intercom/android/sdk/m5/components/ErrorState;",
         "header",
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState$Error$ErrorHeader;",
         "<init>",
         "(Lio/intercom/android/sdk/models/PoweredBy;Lio/intercom/android/sdk/m5/components/ErrorState;Lio/intercom/android/sdk/m5/home/states/HomeUiState$Error$ErrorHeader;)V",
         "getPoweredBy",
         "()Lio/intercom/android/sdk/models/PoweredBy;",
         "getErrorState",
         "()Lio/intercom/android/sdk/m5/components/ErrorState;",
         "getHeader",
         "()Lio/intercom/android/sdk/m5/home/states/HomeUiState$Error$ErrorHeader;",
         "component1",
         "component2",
         "component3",
         "copy",
         "equals",
         "",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "",
         "ErrorHeader",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class HomeUiState$Error extends HomeUiState {
   @Nullable
   private final PoweredBy poweredBy;
   @NotNull
   private final ErrorState errorState;
   @NotNull
   private final HomeUiState$Error$ErrorHeader header;
   public static int $stable;

   static {}

   public HomeUiState$Error(@Nullable PoweredBy var1, @NotNull ErrorState var2, @NotNull HomeUiState$Error$ErrorHeader var3) {
      Intrinsics.checkNotNullParameter(var2, "errorState");
      Intrinsics.checkNotNullParameter(var3, "header");
      super(null);
      this.poweredBy = var1;
      this.errorState = var2;
      this.header = var3;
   }

   @Nullable
   @Override
   public PoweredBy getPoweredBy() {
      return this.poweredBy;
   }

   @NotNull
   public final ErrorState getErrorState() {
      return this.errorState;
   }

   @NotNull
   public final HomeUiState$Error$ErrorHeader getHeader() {
      return this.header;
   }

   @Nullable
   public final PoweredBy component1() {
      return this.poweredBy;
   }

   @NotNull
   public final ErrorState component2() {
      return this.errorState;
   }

   @NotNull
   public final HomeUiState$Error$ErrorHeader component3() {
      return this.header;
   }

   @NotNull
   public final HomeUiState$Error copy(@Nullable PoweredBy var1, @NotNull ErrorState var2, @NotNull HomeUiState$Error$ErrorHeader var3) {
      Intrinsics.checkNotNullParameter(var2, "errorState");
      Intrinsics.checkNotNullParameter(var3, "header");
      return new HomeUiState$Error(var1, var2, var3);
   }

   // $VF: synthetic method
   public static HomeUiState$Error copy$default(
      HomeUiState$Error var0, PoweredBy var1, ErrorState var2, HomeUiState$Error$ErrorHeader var3, int var4, Object var5
   ) {
      if ((var4 & 1) != 0) {
         var1 = var0.poweredBy;
      }

      if ((var4 & 2) != 0) {
         var2 = var0.errorState;
      }

      if ((var4 & 4) != 0) {
         var3 = var0.header;
      }

      return var0.copy(var1, var2, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "Error(poweredBy=" + this.poweredBy + ", errorState=" + this.errorState + ", header=" + this.header + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.poweredBy == null ? 0 : this.poweredBy.hashCode();
      var1 = var1 * 31 + this.errorState.hashCode();
      return var1 * 31 + this.header.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof HomeUiState$Error)) {
         return false;
      } else {
         HomeUiState$Error var2 = (HomeUiState$Error)var1;
         if (!Intrinsics.areEqual(this.poweredBy, var2.poweredBy)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.errorState, var2.errorState) ? false : Intrinsics.areEqual(this.header, var2.header);
         }
      }
   }
}
