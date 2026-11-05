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
   d1 = "\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b$\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0081\b\u0018\u00002\u00020\u0001By\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\u0003\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u0007\u0012\u0006\u0010\u000e\u001a\u00020\u0007\u0012\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u0007\u0012\u0006\u0010\u0010\u001a\u00020\u000b¢\u0006\u0004\b\u0011\u0010\u0012J\t\u0010\"\u001a\u00020\u0003HÆ\u0003J\u000b\u0010#\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010$\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010%\u001a\u00020\u0007HÆ\u0003J\t\u0010&\u001a\u00020\u0007HÆ\u0003J\t\u0010'\u001a\u00020\u0003HÆ\u0003J\t\u0010(\u001a\u00020\u000bHÆ\u0003J\u000b\u0010)\u001a\u0004\u0018\u00010\u0007HÆ\u0003J\u000b\u0010*\u001a\u0004\u0018\u00010\u0007HÆ\u0003J\t\u0010+\u001a\u00020\u0007HÆ\u0003J\u000b\u0010,\u001a\u0004\u0018\u00010\u0007HÆ\u0003J\t\u0010-\u001a\u00020\u000bHÆ\u0003J\u008b\u0001\u0010.\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\u00032\b\b\u0002\u0010\n\u001a\u00020\u000b2\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u00072\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u00072\b\b\u0002\u0010\u000e\u001a\u00020\u00072\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\u00072\b\b\u0002\u0010\u0010\u001a\u00020\u000bHÇ\u0001J\u0013\u0010/\u001a\u0002002\b\u00101\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u00102\u001a\u00020\u000bH×\u0001J\t\u00103\u001a\u00020\u0007H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0018\u0010\u0004\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0014R\u0018\u0010\u0005\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0014R\u0016\u0010\u0006\u001a\u00020\u00078\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0016\u0010\b\u001a\u00020\u00078\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0018R\u0016\u0010\t\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0014R\u0016\u0010\n\u001a\u00020\u000b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001cR\u0018\u0010\f\u001a\u0004\u0018\u00010\u00078\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0018R\u0018\u0010\r\u001a\u0004\u0018\u00010\u00078\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0018R\u0016\u0010\u000e\u001a\u00020\u00078\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u0018R\u0018\u0010\u000f\u001a\u0004\u0018\u00010\u00078\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b \u0010\u0018R\u0016\u0010\u0010\u001a\u00020\u000b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b!\u0010\u001c¨\u00064",
   d2 = {
         "Lio/intercom/android/sdk/models/CustomizationModel;",
         "",
         "action",
         "Lio/intercom/android/sdk/models/CustomizationColorsModel;",
         "actionContrastWhite",
         "actionContrastDark",
         "alignment",
         "",
         "brandName",
         "header",
         "horizontalPadding",
         "",
         "launcherLogoUrl",
         "launcherLogoDarkUrl",
         "messengerWallpaper",
         "themeMode",
         "verticalPadding",
         "<init>",
         "(Lio/intercom/android/sdk/models/CustomizationColorsModel;Lio/intercom/android/sdk/models/CustomizationColorsModel;Lio/intercom/android/sdk/models/CustomizationColorsModel;Ljava/lang/String;Ljava/lang/String;Lio/intercom/android/sdk/models/CustomizationColorsModel;ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;I)V",
         "getAction",
         "()Lio/intercom/android/sdk/models/CustomizationColorsModel;",
         "getActionContrastWhite",
         "getActionContrastDark",
         "getAlignment",
         "()Ljava/lang/String;",
         "getBrandName",
         "getHeader",
         "getHorizontalPadding",
         "()I",
         "getLauncherLogoUrl",
         "getLauncherLogoDarkUrl",
         "getMessengerWallpaper",
         "getThemeMode",
         "getVerticalPadding",
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
         "component12",
         "copy",
         "equals",
         "",
         "other",
         "hashCode",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class CustomizationModel {
   @SerializedName("action")
   @NotNull
   private final CustomizationColorsModel action;
   @SerializedName("action_contrast_white")
   @Nullable
   private final CustomizationColorsModel actionContrastWhite;
   @SerializedName("action_contrast_dark")
   @Nullable
   private final CustomizationColorsModel actionContrastDark;
   @SerializedName("alignment")
   @NotNull
   private final String alignment;
   @SerializedName("brand_name")
   @NotNull
   private final String brandName;
   @SerializedName("header")
   @NotNull
   private final CustomizationColorsModel header;
   @SerializedName("horizontal_padding")
   private final int horizontalPadding;
   @SerializedName("launcher_logo_url")
   @Nullable
   private final String launcherLogoUrl;
   @SerializedName("launcher_logo_dark_url")
   @Nullable
   private final String launcherLogoDarkUrl;
   @SerializedName("messenger_wallpaper")
   @NotNull
   private final String messengerWallpaper;
   @SerializedName("theme_mode")
   @Nullable
   private final String themeMode;
   @SerializedName("vertical_padding")
   private final int verticalPadding;
   public static int $stable;

   static {}

   public CustomizationModel(
      @NotNull CustomizationColorsModel var1,
      @Nullable CustomizationColorsModel var2,
      @Nullable CustomizationColorsModel var3,
      @NotNull String var4,
      @NotNull String var5,
      @NotNull CustomizationColorsModel var6,
      int var7,
      @Nullable String var8,
      @Nullable String var9,
      @NotNull String var10,
      @Nullable String var11,
      int var12
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "action");
      Intrinsics.checkNotNullParameter(var4, "alignment");
      Intrinsics.checkNotNullParameter(var5, "brandName");
      Intrinsics.checkNotNullParameter(var6, "header");
      Intrinsics.checkNotNullParameter(var10, "messengerWallpaper");
      this.action = var1;
      this.actionContrastWhite = var2;
      this.actionContrastDark = var3;
      this.alignment = var4;
      this.brandName = var5;
      this.header = var6;
      this.horizontalPadding = var7;
      this.launcherLogoUrl = var8;
      this.launcherLogoDarkUrl = var9;
      this.messengerWallpaper = var10;
      this.themeMode = var11;
      this.verticalPadding = var12;
   }

   // $VF: synthetic method
   public CustomizationModel(
      CustomizationColorsModel var1,
      CustomizationColorsModel var2,
      CustomizationColorsModel var3,
      String var4,
      String var5,
      CustomizationColorsModel var6,
      int var7,
      String var8,
      String var9,
      String var10,
      String var11,
      int var12,
      int var13,
      DefaultConstructorMarker var14
   ) {
      this(var1, var2, (((var13 & 4) != 0) ? (CustomizationColorsModel) (null) : var3), var4, var5, var6, var7, (((var13 & 128) != 0) ? (String) (null) : var8), (((var13 & 256) != 0) ? (String) (null) : var9), var10, (((var13 & 1024) != 0) ? (String) (null) : var11), var12);
   }

   @NotNull
   public final CustomizationColorsModel getAction() {
      return this.action;
   }

   @Nullable
   public final CustomizationColorsModel getActionContrastWhite() {
      return this.actionContrastWhite;
   }

   @Nullable
   public final CustomizationColorsModel getActionContrastDark() {
      return this.actionContrastDark;
   }

   @NotNull
   public final String getAlignment() {
      return this.alignment;
   }

   @NotNull
   public final String getBrandName() {
      return this.brandName;
   }

   @NotNull
   public final CustomizationColorsModel getHeader() {
      return this.header;
   }

   public final int getHorizontalPadding() {
      return this.horizontalPadding;
   }

   @Nullable
   public final String getLauncherLogoUrl() {
      return this.launcherLogoUrl;
   }

   @Nullable
   public final String getLauncherLogoDarkUrl() {
      return this.launcherLogoDarkUrl;
   }

   @NotNull
   public final String getMessengerWallpaper() {
      return this.messengerWallpaper;
   }

   @Nullable
   public final String getThemeMode() {
      return this.themeMode;
   }

   public final int getVerticalPadding() {
      return this.verticalPadding;
   }

   @NotNull
   public final CustomizationColorsModel component1() {
      return this.action;
   }

   @Nullable
   public final CustomizationColorsModel component2() {
      return this.actionContrastWhite;
   }

   @Nullable
   public final CustomizationColorsModel component3() {
      return this.actionContrastDark;
   }

   @NotNull
   public final String component4() {
      return this.alignment;
   }

   @NotNull
   public final String component5() {
      return this.brandName;
   }

   @NotNull
   public final CustomizationColorsModel component6() {
      return this.header;
   }

   public final int component7() {
      return this.horizontalPadding;
   }

   @Nullable
   public final String component8() {
      return this.launcherLogoUrl;
   }

   @Nullable
   public final String component9() {
      return this.launcherLogoDarkUrl;
   }

   @NotNull
   public final String component10() {
      return this.messengerWallpaper;
   }

   @Nullable
   public final String component11() {
      return this.themeMode;
   }

   public final int component12() {
      return this.verticalPadding;
   }

   @NotNull
   public final CustomizationModel copy(
      @NotNull CustomizationColorsModel var1,
      @Nullable CustomizationColorsModel var2,
      @Nullable CustomizationColorsModel var3,
      @NotNull String var4,
      @NotNull String var5,
      @NotNull CustomizationColorsModel var6,
      int var7,
      @Nullable String var8,
      @Nullable String var9,
      @NotNull String var10,
      @Nullable String var11,
      int var12
   ) {
      Intrinsics.checkNotNullParameter(var1, "action");
      Intrinsics.checkNotNullParameter(var4, "alignment");
      Intrinsics.checkNotNullParameter(var5, "brandName");
      Intrinsics.checkNotNullParameter(var6, "header");
      Intrinsics.checkNotNullParameter(var10, "messengerWallpaper");
      return new CustomizationModel(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
   }

   // $VF: synthetic method
   public static CustomizationModel copy$default(
      CustomizationModel var0,
      CustomizationColorsModel var1,
      CustomizationColorsModel var2,
      CustomizationColorsModel var3,
      String var4,
      String var5,
      CustomizationColorsModel var6,
      int var7,
      String var8,
      String var9,
      String var10,
      String var11,
      int var12,
      int var13,
      Object var14
   ) {
      if ((var13 & 1) != 0) {
         var1 = var0.action;
      }

      if ((var13 & 2) != 0) {
         var2 = var0.actionContrastWhite;
      }

      if ((var13 & 4) != 0) {
         var3 = var0.actionContrastDark;
      }

      if ((var13 & 8) != 0) {
         var4 = var0.alignment;
      }

      if ((var13 & 16) != 0) {
         var5 = var0.brandName;
      }

      if ((var13 & 32) != 0) {
         var6 = var0.header;
      }

      if ((var13 & 64) != 0) {
         var7 = var0.horizontalPadding;
      }

      if ((var13 & 128) != 0) {
         var8 = var0.launcherLogoUrl;
      }

      if ((var13 & 256) != 0) {
         var9 = var0.launcherLogoDarkUrl;
      }

      if ((var13 & 512) != 0) {
         var10 = var0.messengerWallpaper;
      }

      if ((var13 & 1024) != 0) {
         var11 = var0.themeMode;
      }

      if ((var13 & 2048) != 0) {
         var12 = var0.verticalPadding;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11, var12);
   }

   @NotNull
   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("CustomizationModel(action=")
         .append(this.action)
         .append(", actionContrastWhite=")
         .append(this.actionContrastWhite)
         .append(", actionContrastDark=")
         .append(this.actionContrastDark)
         .append(", alignment=")
         .append(this.alignment)
         .append(", brandName=")
         .append(this.brandName)
         .append(", header=")
         .append(this.header)
         .append(", horizontalPadding=")
         .append(this.horizontalPadding)
         .append(", launcherLogoUrl=")
         .append(this.launcherLogoUrl)
         .append(", launcherLogoDarkUrl=")
         .append(this.launcherLogoDarkUrl)
         .append(", messengerWallpaper=")
         .append(this.messengerWallpaper)
         .append(", themeMode=")
         .append(this.themeMode)
         .append(", verticalPadding=");
      var1.append(this.verticalPadding).append(')');
      return var1.toString();
   }

   @Override
   public int hashCode() {
      int var1 = this.action.hashCode();
      var1 = var1 * 31 + (this.actionContrastWhite == null ? 0 : this.actionContrastWhite.hashCode());
      var1 = var1 * 31 + (this.actionContrastDark == null ? 0 : this.actionContrastDark.hashCode());
      var1 = var1 * 31 + this.alignment.hashCode();
      var1 = var1 * 31 + this.brandName.hashCode();
      var1 = var1 * 31 + this.header.hashCode();
      var1 = var1 * 31 + Integer.hashCode(this.horizontalPadding);
      var1 = var1 * 31 + (this.launcherLogoUrl == null ? 0 : this.launcherLogoUrl.hashCode());
      var1 = var1 * 31 + (this.launcherLogoDarkUrl == null ? 0 : this.launcherLogoDarkUrl.hashCode());
      var1 = var1 * 31 + this.messengerWallpaper.hashCode();
      var1 = var1 * 31 + (this.themeMode == null ? 0 : this.themeMode.hashCode());
      return var1 * 31 + Integer.hashCode(this.verticalPadding);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof CustomizationModel)) {
         return false;
      } else {
         CustomizationModel var2 = (CustomizationModel)var1;
         if (!Intrinsics.areEqual(this.action, var2.action)) {
            return false;
         } else if (!Intrinsics.areEqual(this.actionContrastWhite, var2.actionContrastWhite)) {
            return false;
         } else if (!Intrinsics.areEqual(this.actionContrastDark, var2.actionContrastDark)) {
            return false;
         } else if (!Intrinsics.areEqual(this.alignment, var2.alignment)) {
            return false;
         } else if (!Intrinsics.areEqual(this.brandName, var2.brandName)) {
            return false;
         } else if (!Intrinsics.areEqual(this.header, var2.header)) {
            return false;
         } else if (this.horizontalPadding != var2.horizontalPadding) {
            return false;
         } else if (!Intrinsics.areEqual(this.launcherLogoUrl, var2.launcherLogoUrl)) {
            return false;
         } else if (!Intrinsics.areEqual(this.launcherLogoDarkUrl, var2.launcherLogoDarkUrl)) {
            return false;
         } else if (!Intrinsics.areEqual(this.messengerWallpaper, var2.messengerWallpaper)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.themeMode, var2.themeMode) ? false : this.verticalPadding == var2.verticalPadding;
         }
      }
   }
}
