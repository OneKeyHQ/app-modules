package io.intercom.android.sdk.m5.home.states;

import androidx.compose.runtime.internal.StabilityInferred;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\n\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u0017\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003¢\u0006\u0004\b\u0005\u0010\u0006J\t\u0010\n\u001a\u00020\u0003HÆ\u0003J\t\u0010\u000b\u001a\u00020\u0003HÆ\u0003J\u001d\u0010\f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\r\u001a\u00020\u000e2\b\u0010\u000f\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u0010\u001a\u00020\u0011H×\u0001J\t\u0010\u0012\u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0004\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\t\u0010\b¨\u0006\u0013",
   d2 = {
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState$Error$ErrorHeader;",
         "",
         "backgroundColor",
         "",
         "foregroundColor",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;)V",
         "getBackgroundColor",
         "()Ljava/lang/String;",
         "getForegroundColor",
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
public final class HomeUiState$Error$ErrorHeader {
   @NotNull
   private final String backgroundColor;
   @NotNull
   private final String foregroundColor;
   public static int $stable;

   static {}

   public HomeUiState$Error$ErrorHeader(@NotNull String var1, @NotNull String var2) {
      super();
      Intrinsics.checkNotNullParameter(var1, "backgroundColor");
      Intrinsics.checkNotNullParameter(var2, "foregroundColor");
      this.backgroundColor = var1;
      this.foregroundColor = var2;
   }

   @NotNull
   public final String getBackgroundColor() {
      return this.backgroundColor;
   }

   @NotNull
   public final String getForegroundColor() {
      return this.foregroundColor;
   }

   @NotNull
   public final String component1() {
      return this.backgroundColor;
   }

   @NotNull
   public final String component2() {
      return this.foregroundColor;
   }

   @NotNull
   public final HomeUiState$Error$ErrorHeader copy(@NotNull String var1, @NotNull String var2) {
      Intrinsics.checkNotNullParameter(var1, "backgroundColor");
      Intrinsics.checkNotNullParameter(var2, "foregroundColor");
      return new HomeUiState$Error$ErrorHeader(var1, var2);
   }

   // $VF: synthetic method
   public static HomeUiState$Error$ErrorHeader copy$default(HomeUiState$Error$ErrorHeader var0, String var1, String var2, int var3, Object var4) {
      if ((var3 & 1) != 0) {
         var1 = var0.backgroundColor;
      }

      if ((var3 & 2) != 0) {
         var2 = var0.foregroundColor;
      }

      return var0.copy(var1, var2);
   }

   @NotNull
   @Override
   public String toString() {
      return "ErrorHeader(backgroundColor=" + this.backgroundColor + ", foregroundColor=" + this.foregroundColor + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.backgroundColor.hashCode();
      return var1 * 31 + this.foregroundColor.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      }

      if (!(var1 instanceof HomeUiState$Error$ErrorHeader)) {
         return false;
      }

      HomeUiState$Error$ErrorHeader var2 = (HomeUiState$Error$ErrorHeader)var1;
      return !Intrinsics.areEqual(this.backgroundColor, var2.backgroundColor) ? false : Intrinsics.areEqual(this.foregroundColor, var2.foregroundColor);
   }
}
