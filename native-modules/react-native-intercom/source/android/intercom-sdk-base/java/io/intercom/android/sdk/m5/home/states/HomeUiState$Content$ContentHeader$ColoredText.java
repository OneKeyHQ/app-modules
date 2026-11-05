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
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0016\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001BC\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\t\u001a\u00020\u0003¢\u0006\u0004\b\n\u0010\u000bJ\t\u0010\u0014\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0015\u001a\u00020\u0005HÆ\u0003J\u000b\u0010\u0016\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010\u0017\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010\u0018\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010\u0019\u001a\u00020\u0003HÆ\u0003JK\u0010\u001a\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\t\u001a\u00020\u0003HÇ\u0001J\u0013\u0010\u001b\u001a\u00020\u001c2\b\u0010\u001d\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u001e\u001a\u00020\u001fH×\u0001J\t\u0010 \u001a\u00020\u0003H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\rR\u0013\u0010\u0007\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\rR\u0013\u0010\b\u001a\u0004\u0018\u00010\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\rR\u0011\u0010\t\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\r¨\u0006!",
   d2 = {
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader$ColoredText;",
         "",
         "text",
         "",
         "opacity",
         "",
         "textColorType",
         "textColorTypeDark",
         "textColorDark",
         "textColorLight",
         "<init>",
         "(Ljava/lang/String;FLjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
         "getText",
         "()Ljava/lang/String;",
         "getOpacity",
         "()F",
         "getTextColorType",
         "getTextColorTypeDark",
         "getTextColorDark",
         "getTextColorLight",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
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
public final class HomeUiState$Content$ContentHeader$ColoredText {
   @NotNull
   private final String text;
   private final float opacity;
   @Nullable
   private final String textColorType;
   @Nullable
   private final String textColorTypeDark;
   @Nullable
   private final String textColorDark;
   @NotNull
   private final String textColorLight;
   public static int $stable;

   static {}

   public HomeUiState$Content$ContentHeader$ColoredText(
      @NotNull String var1, float var2, @Nullable String var3, @Nullable String var4, @Nullable String var5, @NotNull String var6
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "text");
      Intrinsics.checkNotNullParameter(var6, "textColorLight");
      this.text = var1;
      this.opacity = var2;
      this.textColorType = var3;
      this.textColorTypeDark = var4;
      this.textColorDark = var5;
      this.textColorLight = var6;
   }

   // $VF: synthetic method
   public HomeUiState$Content$ContentHeader$ColoredText(
      String var1, float var2, String var3, String var4, String var5, String var6, int var7, DefaultConstructorMarker var8
   ) {
      this(var1, var2, (((var7 & 4) != 0) ? (String) (null) : var3), (((var7 & 8) != 0) ? (String) (null) : var4), (((var7 & 16) != 0) ? (String) (null) : var5), var6);
   }

   @NotNull
   public final String getText() {
      return this.text;
   }

   public final float getOpacity() {
      return this.opacity;
   }

   @Nullable
   public final String getTextColorType() {
      return this.textColorType;
   }

   @Nullable
   public final String getTextColorTypeDark() {
      return this.textColorTypeDark;
   }

   @Nullable
   public final String getTextColorDark() {
      return this.textColorDark;
   }

   @NotNull
   public final String getTextColorLight() {
      return this.textColorLight;
   }

   @NotNull
   public final String component1() {
      return this.text;
   }

   public final float component2() {
      return this.opacity;
   }

   @Nullable
   public final String component3() {
      return this.textColorType;
   }

   @Nullable
   public final String component4() {
      return this.textColorTypeDark;
   }

   @Nullable
   public final String component5() {
      return this.textColorDark;
   }

   @NotNull
   public final String component6() {
      return this.textColorLight;
   }

   @NotNull
   public final HomeUiState$Content$ContentHeader$ColoredText copy(
      @NotNull String var1, float var2, @Nullable String var3, @Nullable String var4, @Nullable String var5, @NotNull String var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "text");
      Intrinsics.checkNotNullParameter(var6, "textColorLight");
      return new HomeUiState$Content$ContentHeader$ColoredText(var1, var2, var3, var4, var5, var6);
   }

   // $VF: synthetic method
   public static HomeUiState$Content$ContentHeader$ColoredText copy$default(
      HomeUiState$Content$ContentHeader$ColoredText var0, String var1, float var2, String var3, String var4, String var5, String var6, int var7, Object var8
   ) {
      if ((var7 & 1) != 0) {
         var1 = var0.text;
      }

      if ((var7 & 2) != 0) {
         var2 = var0.opacity;
      }

      if ((var7 & 4) != 0) {
         var3 = var0.textColorType;
      }

      if ((var7 & 8) != 0) {
         var4 = var0.textColorTypeDark;
      }

      if ((var7 & 16) != 0) {
         var5 = var0.textColorDark;
      }

      if ((var7 & 32) != 0) {
         var6 = var0.textColorLight;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6);
   }

   @NotNull
   @Override
   public String toString() {
      return "ColoredText(text="
         + this.text
         + ", opacity="
         + this.opacity
         + ", textColorType="
         + this.textColorType
         + ", textColorTypeDark="
         + this.textColorTypeDark
         + ", textColorDark="
         + this.textColorDark
         + ", textColorLight="
         + this.textColorLight
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.text.hashCode();
      var1 = var1 * 31 + Float.hashCode(this.opacity);
      var1 = var1 * 31 + (this.textColorType == null ? 0 : this.textColorType.hashCode());
      var1 = var1 * 31 + (this.textColorTypeDark == null ? 0 : this.textColorTypeDark.hashCode());
      var1 = var1 * 31 + (this.textColorDark == null ? 0 : this.textColorDark.hashCode());
      return var1 * 31 + this.textColorLight.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof HomeUiState$Content$ContentHeader$ColoredText)) {
         return false;
      } else {
         HomeUiState$Content$ContentHeader$ColoredText var2 = (HomeUiState$Content$ContentHeader$ColoredText)var1;
         if (!Intrinsics.areEqual(this.text, var2.text)) {
            return false;
         } else if (Float.compare(this.opacity, var2.opacity) != 0) {
            return false;
         } else if (!Intrinsics.areEqual(this.textColorType, var2.textColorType)) {
            return false;
         } else if (!Intrinsics.areEqual(this.textColorTypeDark, var2.textColorTypeDark)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.textColorDark, var2.textColorDark) ? false : Intrinsics.areEqual(this.textColorLight, var2.textColorLight);
         }
      }
   }
}
