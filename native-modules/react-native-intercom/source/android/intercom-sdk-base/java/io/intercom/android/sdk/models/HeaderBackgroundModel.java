package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import java.util.List;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b%\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B\u008f\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\u0010\b\u0002\u0010\u0005\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u0006\u0012\u0010\b\u0002\u0010\u0007\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u0006\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u000b\u0012\b\b\u0002\u0010\r\u001a\u00020\u000e\u0012\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u000e\u0012\b\b\u0002\u0010\u0010\u001a\u00020\u000b¢\u0006\u0004\b\u0011\u0010\u0012J\t\u0010$\u001a\u00020\u0003HÆ\u0003J\u000b\u0010%\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u0011\u0010&\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u0006HÆ\u0003J\u0011\u0010'\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u0006HÆ\u0003J\u000b\u0010(\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010)\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010*\u001a\u00020\u000bHÆ\u0003J\u0010\u0010+\u001a\u0004\u0018\u00010\u000bHÆ\u0003¢\u0006\u0002\u0010\u001eJ\t\u0010,\u001a\u00020\u000eHÆ\u0003J\u000b\u0010-\u001a\u0004\u0018\u00010\u000eHÆ\u0003J\t\u0010.\u001a\u00020\u000bHÆ\u0003J\u0096\u0001\u0010/\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\u0010\b\u0002\u0010\u0005\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u00062\u0010\b\u0002\u0010\u0007\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u00062\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\n\u001a\u00020\u000b2\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u000b2\b\b\u0002\u0010\r\u001a\u00020\u000e2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u000e2\b\b\u0002\u0010\u0010\u001a\u00020\u000bHÇ\u0001¢\u0006\u0002\u00100J\u0013\u00101\u001a\u00020\u000b2\b\u00102\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u00103\u001a\u000204H×\u0001J\t\u00105\u001a\u00020\u0003H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0018\u0010\u0004\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0014R\u001e\u0010\u0005\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u00068\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u001e\u0010\u0007\u001a\n\u0012\u0004\u0012\u00020\u0003\u0018\u00010\u00068\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0017R\u0018\u0010\b\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0014R\u0018\u0010\t\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0014R\u0016\u0010\n\u001a\u00020\u000b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u001a\u0010\f\u001a\u0004\u0018\u00010\u000b8\u0006X\u0087\u0004¢\u0006\n\n\u0002\u0010\u001f\u001a\u0004\b\u001d\u0010\u001eR\u0016\u0010\r\u001a\u00020\u000e8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u0018\u0010\u000f\u001a\u0004\u0018\u00010\u000e8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\"\u0010!R\u0016\u0010\u0010\u001a\u00020\u000b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b#\u0010\u001c¨\u00066",
   d2 = {
         "Lio/intercom/android/sdk/models/HeaderBackgroundModel;",
         "",
         "color",
         "",
         "colorDark",
         "gradient",
         "",
         "gradientDark",
         "imageUrl",
         "imageDarkUrl",
         "fade",
         "",
         "fadeToDark",
         "type",
         "Lio/intercom/android/sdk/models/HeaderBackdropType;",
         "typeDark",
         "enabled",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;ZLjava/lang/Boolean;Lio/intercom/android/sdk/models/HeaderBackdropType;Lio/intercom/android/sdk/models/HeaderBackdropType;Z)V",
         "getColor",
         "()Ljava/lang/String;",
         "getColorDark",
         "getGradient",
         "()Ljava/util/List;",
         "getGradientDark",
         "getImageUrl",
         "getImageDarkUrl",
         "getFade",
         "()Z",
         "getFadeToDark",
         "()Ljava/lang/Boolean;",
         "Ljava/lang/Boolean;",
         "getType",
         "()Lio/intercom/android/sdk/models/HeaderBackdropType;",
         "getTypeDark",
         "getEnabled",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "component7",
         "component8",
         "component9",
         "component10",
         "component11",
         "copy",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/util/List;Ljava/util/List;Ljava/lang/String;Ljava/lang/String;ZLjava/lang/Boolean;Lio/intercom/android/sdk/models/HeaderBackdropType;Lio/intercom/android/sdk/models/HeaderBackdropType;Z)Lio/intercom/android/sdk/models/HeaderBackgroundModel;",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class HeaderBackgroundModel {
   @SerializedName("color")
   @NotNull
   private final String color;
   @SerializedName("color_dark")
   @Nullable
   private final String colorDark;
   @SerializedName("gradient")
   @Nullable
   private final List gradient;
   @SerializedName("gradient_dark")
   @Nullable
   private final List gradientDark;
   @SerializedName("image_url")
   @Nullable
   private final String imageUrl;
   @SerializedName("image_dark_url")
   @Nullable
   private final String imageDarkUrl;
   @SerializedName("fade_to_white")
   private final boolean fade;
   @SerializedName("fade_to_dark")
   @Nullable
   private final Boolean fadeToDark;
   @SerializedName("type")
   @NotNull
   private final HeaderBackdropType type;
   @SerializedName("type_dark")
   @Nullable
   private final HeaderBackdropType typeDark;
   @SerializedName("enabled")
   private final boolean enabled;
   public static final int $stable;

   static { $stable = 8; }

   public HeaderBackgroundModel(
      @NotNull String var1,
      @Nullable String var2,
      @Nullable List var3,
      @Nullable List var4,
      @Nullable String var5,
      @Nullable String var6,
      boolean var7,
      @Nullable Boolean var8,
      @NotNull HeaderBackdropType var9,
      @Nullable HeaderBackdropType var10,
      boolean var11
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "color");
      Intrinsics.checkNotNullParameter(var9, "type");
      this.color = var1;
      this.colorDark = var2;
      this.gradient = var3;
      this.gradientDark = var4;
      this.imageUrl = var5;
      this.imageDarkUrl = var6;
      this.fade = var7;
      this.fadeToDark = var8;
      this.type = var9;
      this.typeDark = var10;
      this.enabled = var11;
   }

   // $VF: synthetic method
   public HeaderBackgroundModel(
      String var1,
      String var2,
      List var3,
      List var4,
      String var5,
      String var6,
      boolean var7,
      Boolean var8,
      HeaderBackdropType var9,
      HeaderBackdropType var10,
      boolean var11,
      int var12,
      DefaultConstructorMarker var13
   ) {
      this((((var12 & 1) != 0) ? (String) ("#FFFFFF") : var1), (((var12 & 2) != 0) ? (String) (null) : var2), (((var12 & 4) != 0) ? (List) (null) : var3), (((var12 & 8) != 0) ? (List) (null) : var4), (((var12 & 16) != 0) ? (String) (null) : var5), (((var12 & 32) != 0) ? (String) (null) : var6), (((var12 & 64) != 0) ? (boolean) (true) : var7), (((var12 & 128) != 0) ? (Boolean) (null) : var8), (((var12 & 256) != 0) ? (HeaderBackdropType) (HeaderBackdropType.SOLID) : var9), (((var12 & 512) != 0) ? (HeaderBackdropType) (null) : var10), (((var12 & 1024) != 0) ? (boolean) (false) : var11));
   }

   @NotNull
   public final String getColor() {
      return this.color;
   }

   @Nullable
   public final String getColorDark() {
      return this.colorDark;
   }

   @Nullable
   public final List getGradient() {
      return this.gradient;
   }

   @Nullable
   public final List getGradientDark() {
      return this.gradientDark;
   }

   @Nullable
   public final String getImageUrl() {
      return this.imageUrl;
   }

   @Nullable
   public final String getImageDarkUrl() {
      return this.imageDarkUrl;
   }

   public final boolean getFade() {
      return this.fade;
   }

   @Nullable
   public final Boolean getFadeToDark() {
      return this.fadeToDark;
   }

   @NotNull
   public final HeaderBackdropType getType() {
      return this.type;
   }

   @Nullable
   public final HeaderBackdropType getTypeDark() {
      return this.typeDark;
   }

   public final boolean getEnabled() {
      return this.enabled;
   }

   @NotNull
   public final String component1() {
      return this.color;
   }

   @Nullable
   public final String component2() {
      return this.colorDark;
   }

   @Nullable
   public final List component3() {
      return this.gradient;
   }

   @Nullable
   public final List component4() {
      return this.gradientDark;
   }

   @Nullable
   public final String component5() {
      return this.imageUrl;
   }

   @Nullable
   public final String component6() {
      return this.imageDarkUrl;
   }

   public final boolean component7() {
      return this.fade;
   }

   @Nullable
   public final Boolean component8() {
      return this.fadeToDark;
   }

   @NotNull
   public final HeaderBackdropType component9() {
      return this.type;
   }

   @Nullable
   public final HeaderBackdropType component10() {
      return this.typeDark;
   }

   public final boolean component11() {
      return this.enabled;
   }

   @NotNull
   public final HeaderBackgroundModel copy(
      @NotNull String var1,
      @Nullable String var2,
      @Nullable List var3,
      @Nullable List var4,
      @Nullable String var5,
      @Nullable String var6,
      boolean var7,
      @Nullable Boolean var8,
      @NotNull HeaderBackdropType var9,
      @Nullable HeaderBackdropType var10,
      boolean var11
   ) {
      Intrinsics.checkNotNullParameter(var1, "color");
      Intrinsics.checkNotNullParameter(var9, "type");
      return new HeaderBackgroundModel(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   // $VF: synthetic method
   public static HeaderBackgroundModel copy$default(
      HeaderBackgroundModel var0,
      String var1,
      String var2,
      List var3,
      List var4,
      String var5,
      String var6,
      boolean var7,
      Boolean var8,
      HeaderBackdropType var9,
      HeaderBackdropType var10,
      boolean var11,
      int var12,
      Object var13
   ) {
      if ((var12 & 1) != 0) {
         var1 = var0.color;
      }

      if ((var12 & 2) != 0) {
         var2 = var0.colorDark;
      }

      if ((var12 & 4) != 0) {
         var3 = var0.gradient;
      }

      if ((var12 & 8) != 0) {
         var4 = var0.gradientDark;
      }

      if ((var12 & 16) != 0) {
         var5 = var0.imageUrl;
      }

      if ((var12 & 32) != 0) {
         var6 = var0.imageDarkUrl;
      }

      if ((var12 & 64) != 0) {
         var7 = var0.fade;
      }

      if ((var12 & 128) != 0) {
         var8 = var0.fadeToDark;
      }

      if ((var12 & 256) != 0) {
         var9 = var0.type;
      }

      if ((var12 & 512) != 0) {
         var10 = var0.typeDark;
      }

      if ((var12 & 1024) != 0) {
         var11 = var0.enabled;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   @NotNull
   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("HeaderBackgroundModel(color=")
         .append(this.color)
         .append(", colorDark=")
         .append(this.colorDark)
         .append(", gradient=")
         .append(this.gradient)
         .append(", gradientDark=")
         .append(this.gradientDark)
         .append(", imageUrl=")
         .append(this.imageUrl)
         .append(", imageDarkUrl=")
         .append(this.imageDarkUrl)
         .append(", fade=")
         .append(this.fade)
         .append(", fadeToDark=")
         .append(this.fadeToDark)
         .append(", type=")
         .append(this.type)
         .append(", typeDark=")
         .append(this.typeDark)
         .append(", enabled=")
         .append(this.enabled)
         .append(')');
      return var1.toString();
   }

   @Override
   public int hashCode() {
      int var1 = this.color.hashCode();
      var1 = var1 * 31 + (this.colorDark == null ? 0 : this.colorDark.hashCode());
      var1 = var1 * 31 + (this.gradient == null ? 0 : this.gradient.hashCode());
      var1 = var1 * 31 + (this.gradientDark == null ? 0 : this.gradientDark.hashCode());
      var1 = var1 * 31 + (this.imageUrl == null ? 0 : this.imageUrl.hashCode());
      var1 = var1 * 31 + (this.imageDarkUrl == null ? 0 : this.imageDarkUrl.hashCode());
      var1 = var1 * 31 + Boolean.hashCode(this.fade);
      var1 = var1 * 31 + (this.fadeToDark == null ? 0 : this.fadeToDark.hashCode());
      var1 = var1 * 31 + this.type.hashCode();
      var1 = var1 * 31 + (this.typeDark == null ? 0 : this.typeDark.hashCode());
      return var1 * 31 + Boolean.hashCode(this.enabled);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof HeaderBackgroundModel)) {
         return false;
      } else {
         HeaderBackgroundModel var2 = (HeaderBackgroundModel)var1;
         if (!Intrinsics.areEqual(this.color, var2.color)) {
            return false;
         } else if (!Intrinsics.areEqual(this.colorDark, var2.colorDark)) {
            return false;
         } else if (!Intrinsics.areEqual(this.gradient, var2.gradient)) {
            return false;
         } else if (!Intrinsics.areEqual(this.gradientDark, var2.gradientDark)) {
            return false;
         } else if (!Intrinsics.areEqual(this.imageUrl, var2.imageUrl)) {
            return false;
         } else if (!Intrinsics.areEqual(this.imageDarkUrl, var2.imageDarkUrl)) {
            return false;
         } else if (this.fade != var2.fade) {
            return false;
         } else if (!Intrinsics.areEqual(this.fadeToDark, var2.fadeToDark)) {
            return false;
         } else if (this.type != var2.type) {
            return false;
         } else {
            return this.typeDark != var2.typeDark ? false : this.enabled == var2.enabled;
         }
      }
   }

   public HeaderBackgroundModel() {
      this(null, null, null, null, null, null, false, null, null, null, false, 2047, null);
   }
}
