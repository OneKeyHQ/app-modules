package io.intercom.android.sdk.m5.home.states;

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
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B+\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007¢\u0006\u0004\b\b\u0010\tJ\t\u0010\u0010\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0011\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\u0012\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0007HÆ\u0003J3\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u0007HÇ\u0001J\u0013\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0018\u001a\u00020\u0019H×\u0001J\t\u0010\u001a\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000bR\u0011\u0010\u0006\u001a\u00020\u0007¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f¨\u0006\u001b",
   d2 = {
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader$CloseButtonColor;",
         "",
         "backgroundColor",
         "",
         "foregroundColor",
         "foregroundColorDark",
         "backgroundOpacity",
         "",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;F)V",
         "getBackgroundColor",
         "()Ljava/lang/String;",
         "getForegroundColor",
         "getForegroundColorDark",
         "getBackgroundOpacity",
         "()F",
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
@StabilityInferred(parameters = 1)
public final class HomeUiState$Content$ContentHeader$CloseButtonColor {
   @NotNull
   private final String backgroundColor;
   @NotNull
   private final String foregroundColor;
   @Nullable
   private final String foregroundColorDark;
   private final float backgroundOpacity;
   public static int $stable;

   static {}

   public HomeUiState$Content$ContentHeader$CloseButtonColor(@NotNull String var1, @NotNull String var2, @Nullable String var3, float var4) {
      super();
      Intrinsics.checkNotNullParameter(var1, "backgroundColor");
      Intrinsics.checkNotNullParameter(var2, "foregroundColor");
      this.backgroundColor = var1;
      this.foregroundColor = var2;
      this.foregroundColorDark = var3;
      this.backgroundOpacity = var4;
   }

   // $VF: synthetic method
   public HomeUiState$Content$ContentHeader$CloseButtonColor(String var1, String var2, String var3, float var4, int var5, DefaultConstructorMarker var6) {
      this(var1, var2, (((var5 & 4) != 0) ? (String) (null) : var3), var4);
   }

   @NotNull
   public final String getBackgroundColor() {
      return this.backgroundColor;
   }

   @NotNull
   public final String getForegroundColor() {
      return this.foregroundColor;
   }

   @Nullable
   public final String getForegroundColorDark() {
      return this.foregroundColorDark;
   }

   public final float getBackgroundOpacity() {
      return this.backgroundOpacity;
   }

   @NotNull
   public final String component1() {
      return this.backgroundColor;
   }

   @NotNull
   public final String component2() {
      return this.foregroundColor;
   }

   @Nullable
   public final String component3() {
      return this.foregroundColorDark;
   }

   public final float component4() {
      return this.backgroundOpacity;
   }

   @NotNull
   public final HomeUiState$Content$ContentHeader$CloseButtonColor copy(@NotNull String var1, @NotNull String var2, @Nullable String var3, float var4) {
      Intrinsics.checkNotNullParameter(var1, "backgroundColor");
      Intrinsics.checkNotNullParameter(var2, "foregroundColor");
      return new HomeUiState$Content$ContentHeader$CloseButtonColor(var1, var2, var3, var4);
   }

   // $VF: synthetic method
   public static HomeUiState$Content$ContentHeader$CloseButtonColor copy$default(
      HomeUiState$Content$ContentHeader$CloseButtonColor var0, String var1, String var2, String var3, float var4, int var5, Object var6
   ) {
      if ((var5 & 1) != 0) {
         var1 = var0.backgroundColor;
      }

      if ((var5 & 2) != 0) {
         var2 = var0.foregroundColor;
      }

      if ((var5 & 4) != 0) {
         var3 = var0.foregroundColorDark;
      }

      if ((var5 & 8) != 0) {
         var4 = var0.backgroundOpacity;
      }

      return var0.copy(var1, var2, var3, var4);
   }

   @NotNull
   @Override
   public String toString() {
      return "CloseButtonColor(backgroundColor="
         + this.backgroundColor
         + ", foregroundColor="
         + this.foregroundColor
         + ", foregroundColorDark="
         + this.foregroundColorDark
         + ", backgroundOpacity="
         + this.backgroundOpacity
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.backgroundColor.hashCode();
      var1 = var1 * 31 + this.foregroundColor.hashCode();
      var1 = var1 * 31 + (this.foregroundColorDark == null ? 0 : this.foregroundColorDark.hashCode());
      return var1 * 31 + Float.hashCode(this.backgroundOpacity);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof HomeUiState$Content$ContentHeader$CloseButtonColor)) {
         return false;
      } else {
         HomeUiState$Content$ContentHeader$CloseButtonColor var2 = (HomeUiState$Content$ContentHeader$CloseButtonColor)var1;
         if (!Intrinsics.areEqual(this.backgroundColor, var2.backgroundColor)) {
            return false;
         } else if (!Intrinsics.areEqual(this.foregroundColor, var2.foregroundColor)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.foregroundColorDark, var2.foregroundColorDark)
               ? false
               : Float.compare(this.backgroundOpacity, var2.backgroundOpacity) == 0;
         }
      }
   }
}
