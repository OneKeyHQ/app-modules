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
   d1 = "\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u001c\n\u0002\u0010\b\n\u0002\b\u0002\b\u0081\b\u0018\u00002\u00020\u0001B]\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\b\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u0003¢\u0006\u0004\b\u000e\u0010\u000fJ\t\u0010\u001c\u001a\u00020\u0003HÆ\u0003J\u000b\u0010\u001d\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\t\u0010\u001e\u001a\u00020\u0006HÆ\u0003J\t\u0010\u001f\u001a\u00020\bHÆ\u0003J\t\u0010 \u001a\u00020\bHÆ\u0003J\t\u0010!\u001a\u00020\u000bHÆ\u0003J\u000b\u0010\"\u001a\u0004\u0018\u00010\u0003HÆ\u0003J\u000b\u0010#\u001a\u0004\u0018\u00010\u0003HÆ\u0003J_\u0010$\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\b2\b\b\u0002\u0010\n\u001a\u00020\u000b2\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u0003HÇ\u0001J\u0013\u0010%\u001a\u00020\u00062\b\u0010&\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010'\u001a\u00020(H×\u0001J\t\u0010)\u001a\u00020\u0003H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0018\u0010\u0004\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0011R\u0016\u0010\u0005\u001a\u00020\u00068\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0016\u0010\u0007\u001a\u00020\b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0016\u0010\t\u001a\u00020\b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0016R\u0016\u0010\n\u001a\u00020\u000b8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0018\u0010\f\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0011R\u0018\u0010\r\u001a\u0004\u0018\u00010\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0011¨\u0006*",
   d2 = {
         "Lio/intercom/android/sdk/models/HeaderContentModel;",
         "",
         "logoUrl",
         "",
         "logoDarkUrl",
         "showAvatars",
         "",
         "greeting",
         "Lio/intercom/android/sdk/models/HeaderTextModel;",
         "intro",
         "closeButton",
         "Lio/intercom/android/sdk/models/CloseButtonModel;",
         "textColorType",
         "textColorTypeDark",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;ZLio/intercom/android/sdk/models/HeaderTextModel;Lio/intercom/android/sdk/models/HeaderTextModel;Lio/intercom/android/sdk/models/CloseButtonModel;Ljava/lang/String;Ljava/lang/String;)V",
         "getLogoUrl",
         "()Ljava/lang/String;",
         "getLogoDarkUrl",
         "getShowAvatars",
         "()Z",
         "getGreeting",
         "()Lio/intercom/android/sdk/models/HeaderTextModel;",
         "getIntro",
         "getCloseButton",
         "()Lio/intercom/android/sdk/models/CloseButtonModel;",
         "getTextColorType",
         "getTextColorTypeDark",
         "component1",
         "component2",
         "component3",
         "component4",
         "component5",
         "component6",
         "component7",
         "component8",
         "copy",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class HeaderContentModel {
   @SerializedName("logo_url")
   @NotNull
   private final String logoUrl;
   @SerializedName("logo_dark_url")
   @Nullable
   private final String logoDarkUrl;
   @SerializedName("show_avatars")
   private final boolean showAvatars;
   @SerializedName("greeting")
   @NotNull
   private final HeaderTextModel greeting;
   @SerializedName("introduction")
   @NotNull
   private final HeaderTextModel intro;
   @SerializedName("close_button")
   @NotNull
   private final CloseButtonModel closeButton;
   @SerializedName("text_color_type")
   @Nullable
   private final String textColorType;
   @SerializedName("text_color_type_dark")
   @Nullable
   private final String textColorTypeDark;
   public static int $stable;

   static {}

   public HeaderContentModel(
      @NotNull String var1,
      @Nullable String var2,
      boolean var3,
      @NotNull HeaderTextModel var4,
      @NotNull HeaderTextModel var5,
      @NotNull CloseButtonModel var6,
      @Nullable String var7,
      @Nullable String var8
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "logoUrl");
      Intrinsics.checkNotNullParameter(var4, "greeting");
      Intrinsics.checkNotNullParameter(var5, "intro");
      Intrinsics.checkNotNullParameter(var6, "closeButton");
      this.logoUrl = var1;
      this.logoDarkUrl = var2;
      this.showAvatars = var3;
      this.greeting = var4;
      this.intro = var5;
      this.closeButton = var6;
      this.textColorType = var7;
      this.textColorTypeDark = var8;
   }

   // $VF: synthetic method
   public HeaderContentModel(
      String var1,
      String var2,
      boolean var3,
      HeaderTextModel var4,
      HeaderTextModel var5,
      CloseButtonModel var6,
      String var7,
      String var8,
      int var9,
      DefaultConstructorMarker var10
   ) {
      this((((var9 & 1) != 0) ? (String) ("") : var1), (((var9 & 2) != 0) ? (String) (null) : var2), (((var9 & 4) != 0) ? (boolean) (true) : var3), (((var9 & 8) != 0) ? (HeaderTextModel) (new HeaderTextModel(null, 0.0F, null, null, 15, null)) : var4), (((var9 & 16) != 0) ? (HeaderTextModel) (new HeaderTextModel(null, 0.0F, null, null, 15, null)) : var5), (((var9 & 32) != 0) ? (CloseButtonModel) (new CloseButtonModel(null, 0.0F, null, 7, null)) : var6), (((var9 & 64) != 0) ? (String) (null) : var7), (((var9 & 128) != 0) ? (String) (null) : var8));
   }

   @NotNull
   public final String getLogoUrl() {
      return this.logoUrl;
   }

   @Nullable
   public final String getLogoDarkUrl() {
      return this.logoDarkUrl;
   }

   public final boolean getShowAvatars() {
      return this.showAvatars;
   }

   @NotNull
   public final HeaderTextModel getGreeting() {
      return this.greeting;
   }

   @NotNull
   public final HeaderTextModel getIntro() {
      return this.intro;
   }

   @NotNull
   public final CloseButtonModel getCloseButton() {
      return this.closeButton;
   }

   @Nullable
   public final String getTextColorType() {
      return this.textColorType;
   }

   @Nullable
   public final String getTextColorTypeDark() {
      return this.textColorTypeDark;
   }

   @NotNull
   public final String component1() {
      return this.logoUrl;
   }

   @Nullable
   public final String component2() {
      return this.logoDarkUrl;
   }

   public final boolean component3() {
      return this.showAvatars;
   }

   @NotNull
   public final HeaderTextModel component4() {
      return this.greeting;
   }

   @NotNull
   public final HeaderTextModel component5() {
      return this.intro;
   }

   @NotNull
   public final CloseButtonModel component6() {
      return this.closeButton;
   }

   @Nullable
   public final String component7() {
      return this.textColorType;
   }

   @Nullable
   public final String component8() {
      return this.textColorTypeDark;
   }

   @NotNull
   public final HeaderContentModel copy(
      @NotNull String var1,
      @Nullable String var2,
      boolean var3,
      @NotNull HeaderTextModel var4,
      @NotNull HeaderTextModel var5,
      @NotNull CloseButtonModel var6,
      @Nullable String var7,
      @Nullable String var8
   ) {
      Intrinsics.checkNotNullParameter(var1, "logoUrl");
      Intrinsics.checkNotNullParameter(var4, "greeting");
      Intrinsics.checkNotNullParameter(var5, "intro");
      Intrinsics.checkNotNullParameter(var6, "closeButton");
      return new HeaderContentModel(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   // $VF: synthetic method
   public static HeaderContentModel copy$default(
      HeaderContentModel var0,
      String var1,
      String var2,
      boolean var3,
      HeaderTextModel var4,
      HeaderTextModel var5,
      CloseButtonModel var6,
      String var7,
      String var8,
      int var9,
      Object var10
   ) {
      if ((var9 & 1) != 0) {
         var1 = var0.logoUrl;
      }

      if ((var9 & 2) != 0) {
         var2 = var0.logoDarkUrl;
      }

      if ((var9 & 4) != 0) {
         var3 = var0.showAvatars;
      }

      if ((var9 & 8) != 0) {
         var4 = var0.greeting;
      }

      if ((var9 & 16) != 0) {
         var5 = var0.intro;
      }

      if ((var9 & 32) != 0) {
         var6 = var0.closeButton;
      }

      if ((var9 & 64) != 0) {
         var7 = var0.textColorType;
      }

      if ((var9 & 128) != 0) {
         var8 = var0.textColorTypeDark;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6, var7, var8);
   }

   @NotNull
   @Override
   public String toString() {
      return "HeaderContentModel(logoUrl="
         + this.logoUrl
         + ", logoDarkUrl="
         + this.logoDarkUrl
         + ", showAvatars="
         + this.showAvatars
         + ", greeting="
         + this.greeting
         + ", intro="
         + this.intro
         + ", closeButton="
         + this.closeButton
         + ", textColorType="
         + this.textColorType
         + ", textColorTypeDark="
         + this.textColorTypeDark
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = this.logoUrl.hashCode();
      var1 = var1 * 31 + (this.logoDarkUrl == null ? 0 : this.logoDarkUrl.hashCode());
      var1 = var1 * 31 + Boolean.hashCode(this.showAvatars);
      var1 = var1 * 31 + this.greeting.hashCode();
      var1 = var1 * 31 + this.intro.hashCode();
      var1 = var1 * 31 + this.closeButton.hashCode();
      var1 = var1 * 31 + (this.textColorType == null ? 0 : this.textColorType.hashCode());
      return var1 * 31 + (this.textColorTypeDark == null ? 0 : this.textColorTypeDark.hashCode());
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof HeaderContentModel)) {
         return false;
      } else {
         HeaderContentModel var2 = (HeaderContentModel)var1;
         if (!Intrinsics.areEqual(this.logoUrl, var2.logoUrl)) {
            return false;
         } else if (!Intrinsics.areEqual(this.logoDarkUrl, var2.logoDarkUrl)) {
            return false;
         } else if (this.showAvatars != var2.showAvatars) {
            return false;
         } else if (!Intrinsics.areEqual(this.greeting, var2.greeting)) {
            return false;
         } else if (!Intrinsics.areEqual(this.intro, var2.intro)) {
            return false;
         } else if (!Intrinsics.areEqual(this.closeButton, var2.closeButton)) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.textColorType, var2.textColorType) ? false : Intrinsics.areEqual(this.textColorTypeDark, var2.textColorTypeDark);
         }
      }
   }

   public HeaderContentModel() {
      this(null, null, false, null, null, null, null, null, 255, null);
   }
}
