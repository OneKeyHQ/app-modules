package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0016\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001BI\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\t\u0010\nJ\t\u0010\u0012\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0013\u001a\u00020\u0003HÆ\u0003J\t\u0010\u0014\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\u0015\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010\u0016\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010\u0017\u001a\u0004\u0018\u00010\u0003HÆ\u0003JK\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0003HÇ\u0001J\u0013\u0010\u0019\u001a\u00020\u001a2\b\u0010\u001b\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u001c\u001a\u00020\u001dH×\u0001J\t\u0010\u001e\u001a\u00020\u0003H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0016\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\r\u0010\fR\u0016\u0010\u0005\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\fR\u0018\u0010\u0006\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\fR\u0018\u0010\u0007\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\fR\u0018\u0010\b\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\f¨\u0006\u001f",
   d2 = {
         "Lio/intercom/android/sdk/models/CustomizationColorsModel;",
         "",
         "backgroundColor",
         "",
         "foregroundColor",
         "foregroundLowContrastColor",
         "backgroundColorDark",
         "foregroundColorDark",
         "foregroundLowContrastColorDark",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V",
         "getBackgroundColor",
         "()Ljava/lang/String;",
         "getForegroundColor",
         "getForegroundLowContrastColor",
         "getBackgroundColorDark",
         "getForegroundColorDark",
         "getForegroundLowContrastColorDark",
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
public final class CustomizationColorsModel {
   @SerializedName("background_color")
   @NotNull
   private final String backgroundColor;
   @SerializedName("foreground_color")
   @NotNull
   private final String foregroundColor;
   @SerializedName("foreground_color_low_contrast")
   @NotNull
   private final String foregroundLowContrastColor;
   @SerializedName("background_color_dark")
   @Nullable
   private final String backgroundColorDark;
   @SerializedName("foreground_color_dark")
   @Nullable
   private final String foregroundColorDark;
   @SerializedName("foreground_color_low_contrast_dark")
   @Nullable
   private final String foregroundLowContrastColorDark;
   public static int $stable;

   static {}

   public CustomizationColorsModel(
      @NotNull String var1, @NotNull String var2, @NotNull String var3, @Nullable String var4, @Nullable String var5, @Nullable String var6
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "backgroundColor");
      Intrinsics.checkNotNullParameter(var2, "foregroundColor");
      Intrinsics.checkNotNullParameter(var3, "foregroundLowContrastColor");
      this.backgroundColor = var1;
      this.foregroundColor = var2;
      this.foregroundLowContrastColor = var3;
      this.backgroundColorDark = var4;
      this.foregroundColorDark = var5;
      this.foregroundLowContrastColorDark = var6;
   }

   // $VF: synthetic method
   public CustomizationColorsModel(String var1, String var2, String var3, String var4, String var5, String var6, int var7, DefaultConstructorMarker var8) {
      if ((var7 & 1) != 0) {
         var1 = "#FFFFFF";
      }

      if ((var7 & 2) != 0) {
         var2 = "#000000";
      }

      if ((var7 & 4) != 0) {
         var3 = var2;
      }

      if ((var7 & 8) != 0) {
         var4 = null;
      }

      if ((var7 & 16) != 0) {
         var5 = null;
      }

      if ((var7 & 32) != 0) {
         var6 = null;
      }

      this(var1, var2, var3, var4, var5, var6);
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
   public final String getForegroundLowContrastColor() {
      return this.foregroundLowContrastColor;
   }

   @Nullable
   public final String getBackgroundColorDark() {
      return this.backgroundColorDark;
   }

   @Nullable
   public final String getForegroundColorDark() {
      return this.foregroundColorDark;
   }

   @Nullable
   public final String getForegroundLowContrastColorDark() {
      return this.foregroundLowContrastColorDark;
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
   public final String component3() {
      return this.foregroundLowContrastColor;
   }

   @Nullable
   public final String component4() {
      return this.backgroundColorDark;
   }

   @Nullable
   public final String component5() {
      return this.foregroundColorDark;
   }

   @Nullable
   public final String component6() {
      return this.foregroundLowContrastColorDark;
   }

   @NotNull
   public final CustomizationColorsModel copy(
      @NotNull String var1, @NotNull String var2, @NotNull String var3, @Nullable String var4, @Nullable String var5, @Nullable String var6
   ) {
      Intrinsics.checkNotNullParameter(var1, "backgroundColor");
      Intrinsics.checkNotNullParameter(var2, "foregroundColor");
      Intrinsics.checkNotNullParameter(var3, "foregroundLowContrastColor");
      return new CustomizationColorsModel(var1, var2, var3, var4, var5, var6);
   }

   // $VF: synthetic method
   public static CustomizationColorsModel copy$default(
      CustomizationColorsModel var0, String var1, String var2, String var3, String var4, String var5, String var6, int var7, Object var8
   ) {
      if ((var7 & 1) != 0) {
         var1 = var0.backgroundColor;
      }

      if ((var7 & 2) != 0) {
         var2 = var0.foregroundColor;
      }

      if ((var7 & 4) != 0) {
         var3 = var0.foregroundLowContrastColor;
      }

      if ((var7 & 8) != 0) {
         var4 = var0.backgroundColorDark;
      }

      if ((var7 & 16) != 0) {
         var5 = var0.foregroundColorDark;
      }

      if ((var7 & 32) != 0) {
         var6 = var0.foregroundLowContrastColorDark;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6);
   }

   @NotNull
   @Override
   public String toString() {
      return "CustomizationColorsModel(backgroundColor="
         + this.backgroundColor
         + ", foregroundColor="
         + this.foregroundColor
         + ", foregroundLowContrastColor="
         + this.foregroundLowContrastColor
         + ", backgroundColorDark="
         + this.backgroundColorDark
         + ", foregroundColorDark="
         + this.foregroundColorDark
         + ", foregroundLowContrastColorDark="
         + this.foregroundLowContrastColorDark
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.backgroundColor.hashCode();
      var1 = var1 * 31 + this.foregroundColor.hashCode();
      var1 = var1 * 31 + this.foregroundLowContrastColor.hashCode();
      var1 = var1 * 31 + (this.backgroundColorDark == null ? 0 : this.backgroundColorDark.hashCode());
      var1 = var1 * 31 + (this.foregroundColorDark == null ? 0 : this.foregroundColorDark.hashCode());
      return var1 * 31 + (this.foregroundLowContrastColorDark == null ? 0 : this.foregroundLowContrastColorDark.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof CustomizationColorsModel)) {
         return false;
      } else {
         CustomizationColorsModel var2 = (CustomizationColorsModel)var1;
         if (!Intrinsics.areEqual(this.backgroundColor, var2.backgroundColor)) {
            return false;
         } else if (!Intrinsics.areEqual(this.foregroundColor, var2.foregroundColor)) {
            return false;
         } else if (!Intrinsics.areEqual(this.foregroundLowContrastColor, var2.foregroundLowContrastColor)) {
            return false;
         } else if (!Intrinsics.areEqual(this.backgroundColorDark, var2.backgroundColorDark)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.foregroundColorDark, var2.foregroundColorDark)
               ? false
               : Intrinsics.areEqual(this.foregroundLowContrastColorDark, var2.foregroundLowContrastColorDark);
         }
      }
   }

   public CustomizationColorsModel() {
      this(null, null, null, null, null, null, 63, null);
   }
}
