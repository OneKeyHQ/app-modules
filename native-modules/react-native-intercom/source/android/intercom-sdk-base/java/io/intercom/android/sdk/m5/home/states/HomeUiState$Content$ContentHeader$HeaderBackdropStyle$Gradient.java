package io.intercom.android.sdk.m5.home.states;

import androidx.compose.runtime.internal.StabilityInferred;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u000b\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0087\b\u0018\u00002\u00020\u0001B\u001d\u0012\f\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006¢\u0006\u0004\b\u0007\u0010\bJ\u000f\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003HÆ\u0003J\t\u0010\u000e\u001a\u00020\u0006HÆ\u0003J#\u0010\u000f\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0006HÇ\u0001J\u0013\u0010\u0010\u001a\u00020\u00062\b\u0010\u0011\u001a\u0004\u0018\u00010\u0012H×\u0003J\t\u0010\u0013\u001a\u00020\u0014H×\u0001J\t\u0010\u0015\u001a\u00020\u0016H×\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0014\u0010\u0005\u001a\u00020\u0006X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\f¨\u0006\u0017",
   d2 = {
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Gradient;",
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader$HeaderBackdropStyle;",
         "colors",
         "",
         "Landroidx/compose/ui/graphics/Color;",
         "fade",
         "",
         "<init>",
         "(Ljava/util/List;Z)V",
         "getColors",
         "()Ljava/util/List;",
         "getFade",
         "()Z",
         "component1",
         "component2",
         "copy",
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
@StabilityInferred(parameters = 0)
public final class HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Gradient extends HomeUiState$Content$ContentHeader$HeaderBackdropStyle {
   @NotNull
   private final List colors;
   private final boolean fade;
   public static final int $stable;

   static { $stable = 8; }

   public HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Gradient(@NotNull List var1, boolean var2) {
      Intrinsics.checkNotNullParameter(var1, "colors");
      super(null);
      this.colors = var1;
      this.fade = var2;
   }

   @NotNull
   public final List getColors() {
      return this.colors;
   }

   @Override
   public boolean getFade() {
      return this.fade;
   }

   @NotNull
   public final List component1() {
      return this.colors;
   }

   public final boolean component2() {
      return this.fade;
   }

   @NotNull
   public final HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Gradient copy(@NotNull List var1, boolean var2) {
      Intrinsics.checkNotNullParameter(var1, "colors");
      return new HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Gradient(var1, var2);
   }

   // $VF: synthetic method
   public static HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Gradient copy$default(
      HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Gradient var0, List var1, boolean var2, int var3, Object var4
   ) {
      if ((var3 & 1) != 0) {
         var1 = var0.colors;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.fade;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "Gradient(colors=" + this.colors + ", fade=" + this.fade + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.colors.hashCode();
      return var1 * 31 + Boolean.hashCode(this.fade);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Gradient)) {
         return false;
      }

      HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Gradient var2 = (HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Gradient)var1;
      return !Intrinsics.areEqual(this.colors, var2.colors) ? false : this.fade == var2.fade;
   }
}
