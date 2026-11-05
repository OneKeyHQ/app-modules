package io.intercom.android.sdk.m5.home.states;

import androidx.compose.runtime.internal.StabilityInferred;
import androidx.compose.ui.graphics.Color;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u000f\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005¢\u0006\u0004\b\u0006\u0010\u0007J\u0010\u0010\r\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b\u000e\u0010\tJ\t\u0010\u000f\u001a\u00020\u0005HÆ\u0003J$\u0010\u0010\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0005HÇ\u0001¢\u0006\u0004\b\u0011\u0010\u0012J\u0013\u0010\u0013\u001a\u00020\u00052\b\u0010\u0014\u001a\u0004\u0018\u00010\u0015H×\u0003J\t\u0010\u0016\u001a\u00020\u0017H×\u0001J\t\u0010\u0018\u001a\u00020\u0019H×\u0001R\u0013\u0010\u0002\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\n\u001a\u0004\b\b\u0010\tR\u0014\u0010\u0004\u001a\u00020\u0005X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f¨\u0006\u001a",
   d2 = {
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Solid;",
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader$HeaderBackdropStyle;",
         "color",
         "Landroidx/compose/ui/graphics/Color;",
         "fade",
         "",
         "<init>",
         "(JZLkotlin/jvm/internal/DefaultConstructorMarker;)V",
         "getColor-0d7_KjU",
         "()J",
         "J",
         "getFade",
         "()Z",
         "component1",
         "component1-0d7_KjU",
         "component2",
         "copy",
         "copy-DxMtmZc",
         "(JZ)Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Solid;",
         "equals",
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
public final class HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Solid extends HomeUiState$Content$ContentHeader$HeaderBackdropStyle {
   private final long color;
   private final boolean fade;
   public static int $stable;

   static {}

   private HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Solid(long var1, boolean var3) {
      super(null);
      this.color = var1;
      this.fade = var3;
   }

   public final long recovered$6f15fcf6c761ad06() {
      return this.color;
   }

   @Override
   public boolean getFade() {
      return this.fade;
   }

   public final long recovered$2b43d556f0094889() {
      return this.color;
   }

   public final boolean component2() {
      return this.fade;
   }

   @NotNull
   public final HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Solid recovered$9ca5558ab62d978e(long var1, boolean var3) {
      return new HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Solid(var1, var3, null);
   }

   // $VF: synthetic method
   public static HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Solid recovered$f9078a798db7279a(
      HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Solid var0, long var1, boolean var3, int var4, Object var5
   ) {
      if ((var4 & 1) != 0) {
         var1 = var0.color;
      }

      if ((var4 & 2) != 0) {
         var3 = var0.fade;
      }

      return var0.recovered$9ca5558ab62d978e(var1, var3);
   }

   @NotNull
   @Override
   public String toString() {
      return "Solid(color=" + Color.recovered$cfef63bb1d111f9f(this.color) + ", fade=" + this.fade + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Color.recovered$87e060624084b26b(this.color);
      return var1 * 31 + Boolean.hashCode(this.fade);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Solid)) {
         return false;
      }

      HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Solid var2 = (HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Solid)var1;
      return !Color.recovered$9eab66ea8c46e11c(this.color, var2.color) ? false : this.fade == var2.fade;
   }

   // $VF: synthetic method
   public HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Solid(long var1, boolean var3, DefaultConstructorMarker var4) {
      this(var1, var3);
   }
}
