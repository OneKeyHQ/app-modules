package io.intercom.android.sdk.m5.home.states;

import androidx.compose.runtime.internal.StabilityInferred;
import androidx.compose.ui.graphics.Color;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0012\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B!\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\u0010\u0010\u0011\u001a\u00020\u0003HÆ\u0003¢\u0006\u0004\b\u0012\u0010\u000bJ\u000b\u0010\u0013\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0007HÆ\u0003J0\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007HÇ\u0001¢\u0006\u0004\b\u0016\u0010\u0017J\u0013\u0010\u0018\u001a\u00020\u00072\b\u0010\u0019\u001a\u0004\u0018\u00010\u001aH×\u0003J\t\u0010\u001b\u001a\u00020\u001cH×\u0001J\t\u0010\u001d\u001a\u00020\u0005H×\u0001R\u0013\u0010\u0002\u001a\u00020\u0003¢\u0006\n\n\u0002\u0010\f\u001a\u0004\b\n\u0010\u000bR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0014\u0010\u0006\u001a\u00020\u0007X\u0096\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010¨\u0006\u001e",
   d2 = {
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Image;",
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader$HeaderBackdropStyle;",
         "fallbackColor",
         "Landroidx/compose/ui/graphics/Color;",
         "imageUrl",
         "",
         "fade",
         "",
         "<init>",
         "(JLjava/lang/String;ZLkotlin/jvm/internal/DefaultConstructorMarker;)V",
         "getFallbackColor-0d7_KjU",
         "()J",
         "J",
         "getImageUrl",
         "()Ljava/lang/String;",
         "getFade",
         "()Z",
         "component1",
         "component1-0d7_KjU",
         "component2",
         "component3",
         "copy",
         "copy-ek8zF_U",
         "(JLjava/lang/String;Z)Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Image;",
         "equals",
         "other",
         "",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Image extends HomeUiState$Content$ContentHeader$HeaderBackdropStyle {
   private final long fallbackColor;
   @Nullable
   private final String imageUrl;
   private final boolean fade;
   public static int $stable;

   static {}

   private HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Image(long var1, String var3, boolean var4) {
      super(null);
      this.fallbackColor = var1;
      this.imageUrl = var3;
      this.fade = var4;
   }

   public final long recovered$b7af0fa3dc882892() {
      return this.fallbackColor;
   }

   @Nullable
   public final String getImageUrl() {
      return this.imageUrl;
   }

   @Override
   public boolean getFade() {
      return this.fade;
   }

   public final long recovered$2b43d556f0094889() {
      return this.fallbackColor;
   }

   @Nullable
   public final String component2() {
      return this.imageUrl;
   }

   public final boolean component3() {
      return this.fade;
   }

   @NotNull
   public final HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Image recovered$70d3722802474719(long var1, @Nullable String var3, boolean var4) {
      return new HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Image(var1, var3, var4, null);
   }

   // $VF: synthetic method
   public static HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Image recovered$ec36f297a8fabe96(
      HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Image var0, long var1, String var3, boolean var4, int var5, Object var6
   ) {
      if ((var5 & 1) != 0) {
         var1 = var0.fallbackColor;
      }

      if ((var5 & 2) != 0) {
         var3 = var0.imageUrl;
      }

      if ((var5 & 4) != 0) {
         var4 = var0.fade;
      }

      return var0.recovered$70d3722802474719(var1, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "Image(fallbackColor=" + Color.recovered$cfef63bb1d111f9f(this.fallbackColor) + ", imageUrl=" + this.imageUrl + ", fade=" + this.fade + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Color.recovered$87e060624084b26b(this.fallbackColor);
      var1 = var1 * 31 + (this.imageUrl == null ? 0 : this.imageUrl.hashCode());
      return var1 * 31 + Boolean.hashCode(this.fade);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Image)) {
         return false;
      } else {
         HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Image var2 = (HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Image)var1;
         if (!Color.recovered$9eab66ea8c46e11c(this.fallbackColor, var2.fallbackColor)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.imageUrl, var2.imageUrl) ? false : this.fade == var2.fade;
         }
      }
   }

   // $VF: synthetic method
   public HomeUiState$Content$ContentHeader$HeaderBackdropStyle$Image(long var1, String var3, boolean var4, DefaultConstructorMarker var5) {
      this(var1, var3, var4);
   }
}
