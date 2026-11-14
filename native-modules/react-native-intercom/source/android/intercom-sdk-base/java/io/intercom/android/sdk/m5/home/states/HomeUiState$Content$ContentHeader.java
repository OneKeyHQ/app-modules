package io.intercom.android.sdk.m5.home.states;

import androidx.compose.runtime.internal.StabilityInferred;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b \n\u0002\u0010\b\n\u0002\b\u0005\b\u0087\b\u0018\u00002\u00020\u0001:\u0003567Bg\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\u0006\u0010\t\u001a\u00020\b\u0012\u0006\u0010\n\u001a\u00020\u000b\u0012\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u000b\u0012\u0006\u0010\r\u001a\u00020\u0003\u0012\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f\u0012\u0006\u0010\u0011\u001a\u00020\u0012¢\u0006\u0004\b\u0013\u0010\u0014J\t\u0010%\u001a\u00020\u0003HÆ\u0003J\u000b\u0010&\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\u000b\u0010'\u001a\u0004\u0018\u00010\u0005HÆ\u0003J\t\u0010(\u001a\u00020\bHÆ\u0003J\t\u0010)\u001a\u00020\bHÆ\u0003J\t\u0010*\u001a\u00020\u000bHÆ\u0003J\u000b\u0010+\u001a\u0004\u0018\u00010\u000bHÆ\u0003J\t\u0010,\u001a\u00020\u0003HÆ\u0003J\u000f\u0010-\u001a\b\u0012\u0004\u0012\u00020\u00100\u000fHÆ\u0003J\t\u0010.\u001a\u00020\u0012HÆ\u0003Jy\u0010/\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\b2\b\b\u0002\u0010\n\u001a\u00020\u000b2\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\u000b2\b\b\u0002\u0010\r\u001a\u00020\u00032\u000e\b\u0002\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f2\b\b\u0002\u0010\u0011\u001a\u00020\u0012HÇ\u0001J\u0013\u00100\u001a\u00020\u00032\b\u00101\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u00102\u001a\u000203H×\u0001J\t\u00104\u001a\u00020\u0005H×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0013\u0010\u0006\u001a\u0004\u0018\u00010\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0018R\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\t\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001bR\u0011\u0010\n\u001a\u00020\u000b¢\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001eR\u0013\u0010\f\u001a\u0004\u0018\u00010\u000b¢\u0006\b\n\u0000\u001a\u0004\b\u001f\u0010\u001eR\u0011\u0010\r\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b \u0010\u0016R\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f¢\u0006\b\n\u0000\u001a\u0004\b!\u0010\"R\u0011\u0010\u0011\u001a\u00020\u0012¢\u0006\b\n\u0000\u001a\u0004\b#\u0010$¨\u00068",
   d2 = {
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader;",
         "",
         "showLogo",
         "",
         "logoUrl",
         "",
         "logoDarkUrl",
         "greeting",
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader$ColoredText;",
         "intro",
         "headerBackdropStyle",
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader$HeaderBackdropStyle;",
         "headerBackdropStyleDark",
         "showAvatars",
         "adminsAvatars",
         "",
         "Lio/intercom/android/sdk/m5/components/avatar/AvatarWrapper;",
         "closeButtonColor",
         "Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader$CloseButtonColor;",
         "<init>",
         "(ZLjava/lang/String;Ljava/lang/String;Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader$ColoredText;Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader$ColoredText;Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader$HeaderBackdropStyle;Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader$HeaderBackdropStyle;ZLjava/util/List;Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader$CloseButtonColor;)V",
         "getShowLogo",
         "()Z",
         "getLogoUrl",
         "()Ljava/lang/String;",
         "getLogoDarkUrl",
         "getGreeting",
         "()Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader$ColoredText;",
         "getIntro",
         "getHeaderBackdropStyle",
         "()Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader$HeaderBackdropStyle;",
         "getHeaderBackdropStyleDark",
         "getShowAvatars",
         "getAdminsAvatars",
         "()Ljava/util/List;",
         "getCloseButtonColor",
         "()Lio/intercom/android/sdk/m5/home/states/HomeUiState$Content$ContentHeader$CloseButtonColor;",
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
         "copy",
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "CloseButtonColor",
         "ColoredText",
         "HeaderBackdropStyle",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class HomeUiState$Content$ContentHeader {
   private final boolean showLogo;
   @Nullable
   private final String logoUrl;
   @Nullable
   private final String logoDarkUrl;
   @NotNull
   private final HomeUiState$Content$ContentHeader$ColoredText greeting;
   @NotNull
   private final HomeUiState$Content$ContentHeader$ColoredText intro;
   @NotNull
   private final HomeUiState$Content$ContentHeader$HeaderBackdropStyle headerBackdropStyle;
   @Nullable
   private final HomeUiState$Content$ContentHeader$HeaderBackdropStyle headerBackdropStyleDark;
   private final boolean showAvatars;
   @NotNull
   private final List adminsAvatars;
   @NotNull
   private final HomeUiState$Content$ContentHeader$CloseButtonColor closeButtonColor;
   public static final int $stable;

   static { $stable = 8; }

   public HomeUiState$Content$ContentHeader(
      boolean var1,
      @Nullable String var2,
      @Nullable String var3,
      @NotNull HomeUiState$Content$ContentHeader$ColoredText var4,
      @NotNull HomeUiState$Content$ContentHeader$ColoredText var5,
      @NotNull HomeUiState$Content$ContentHeader$HeaderBackdropStyle var6,
      @Nullable HomeUiState$Content$ContentHeader$HeaderBackdropStyle var7,
      boolean var8,
      @NotNull List var9,
      @NotNull HomeUiState$Content$ContentHeader$CloseButtonColor var10
   ) {
      super();
      Intrinsics.checkNotNullParameter(var4, "greeting");
      Intrinsics.checkNotNullParameter(var5, "intro");
      Intrinsics.checkNotNullParameter(var6, "headerBackdropStyle");
      Intrinsics.checkNotNullParameter(var9, "adminsAvatars");
      Intrinsics.checkNotNullParameter(var10, "closeButtonColor");
      this.showLogo = var1;
      this.logoUrl = var2;
      this.logoDarkUrl = var3;
      this.greeting = var4;
      this.intro = var5;
      this.headerBackdropStyle = var6;
      this.headerBackdropStyleDark = var7;
      this.showAvatars = var8;
      this.adminsAvatars = var9;
      this.closeButtonColor = var10;
   }

   // $VF: synthetic method
   public HomeUiState$Content$ContentHeader(
      boolean var1,
      String var2,
      String var3,
      HomeUiState$Content$ContentHeader$ColoredText var4,
      HomeUiState$Content$ContentHeader$ColoredText var5,
      HomeUiState$Content$ContentHeader$HeaderBackdropStyle var6,
      HomeUiState$Content$ContentHeader$HeaderBackdropStyle var7,
      boolean var8,
      List var9,
      HomeUiState$Content$ContentHeader$CloseButtonColor var10,
      int var11,
      DefaultConstructorMarker var12
   ) {
      this(var1, var2, (((var11 & 4) != 0) ? (String) (null) : var3), var4, var5, var6, (((var11 & 64) != 0) ? (HomeUiState$Content$ContentHeader$HeaderBackdropStyle) (null) : var7), var8, var9, var10);
   }

   public final boolean getShowLogo() {
      return this.showLogo;
   }

   @Nullable
   public final String getLogoUrl() {
      return this.logoUrl;
   }

   @Nullable
   public final String getLogoDarkUrl() {
      return this.logoDarkUrl;
   }

   @NotNull
   public final HomeUiState$Content$ContentHeader$ColoredText getGreeting() {
      return this.greeting;
   }

   @NotNull
   public final HomeUiState$Content$ContentHeader$ColoredText getIntro() {
      return this.intro;
   }

   @NotNull
   public final HomeUiState$Content$ContentHeader$HeaderBackdropStyle getHeaderBackdropStyle() {
      return this.headerBackdropStyle;
   }

   @Nullable
   public final HomeUiState$Content$ContentHeader$HeaderBackdropStyle getHeaderBackdropStyleDark() {
      return this.headerBackdropStyleDark;
   }

   public final boolean getShowAvatars() {
      return this.showAvatars;
   }

   @NotNull
   public final List getAdminsAvatars() {
      return this.adminsAvatars;
   }

   @NotNull
   public final HomeUiState$Content$ContentHeader$CloseButtonColor getCloseButtonColor() {
      return this.closeButtonColor;
   }

   public final boolean component1() {
      return this.showLogo;
   }

   @Nullable
   public final String component2() {
      return this.logoUrl;
   }

   @Nullable
   public final String component3() {
      return this.logoDarkUrl;
   }

   @NotNull
   public final HomeUiState$Content$ContentHeader$ColoredText component4() {
      return this.greeting;
   }

   @NotNull
   public final HomeUiState$Content$ContentHeader$ColoredText component5() {
      return this.intro;
   }

   @NotNull
   public final HomeUiState$Content$ContentHeader$HeaderBackdropStyle component6() {
      return this.headerBackdropStyle;
   }

   @Nullable
   public final HomeUiState$Content$ContentHeader$HeaderBackdropStyle component7() {
      return this.headerBackdropStyleDark;
   }

   public final boolean component8() {
      return this.showAvatars;
   }

   @NotNull
   public final List component9() {
      return this.adminsAvatars;
   }

   @NotNull
   public final HomeUiState$Content$ContentHeader$CloseButtonColor component10() {
      return this.closeButtonColor;
   }

   @NotNull
   public final HomeUiState$Content$ContentHeader copy(
      boolean var1,
      @Nullable String var2,
      @Nullable String var3,
      @NotNull HomeUiState$Content$ContentHeader$ColoredText var4,
      @NotNull HomeUiState$Content$ContentHeader$ColoredText var5,
      @NotNull HomeUiState$Content$ContentHeader$HeaderBackdropStyle var6,
      @Nullable HomeUiState$Content$ContentHeader$HeaderBackdropStyle var7,
      boolean var8,
      @NotNull List var9,
      @NotNull HomeUiState$Content$ContentHeader$CloseButtonColor var10
   ) {
      Intrinsics.checkNotNullParameter(var4, "greeting");
      Intrinsics.checkNotNullParameter(var5, "intro");
      Intrinsics.checkNotNullParameter(var6, "headerBackdropStyle");
      Intrinsics.checkNotNullParameter(var9, "adminsAvatars");
      Intrinsics.checkNotNullParameter(var10, "closeButtonColor");
      return new HomeUiState$Content$ContentHeader(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   // $VF: synthetic method
   public static HomeUiState$Content$ContentHeader copy$default(
      HomeUiState$Content$ContentHeader var0,
      boolean var1,
      String var2,
      String var3,
      HomeUiState$Content$ContentHeader$ColoredText var4,
      HomeUiState$Content$ContentHeader$ColoredText var5,
      HomeUiState$Content$ContentHeader$HeaderBackdropStyle var6,
      HomeUiState$Content$ContentHeader$HeaderBackdropStyle var7,
      boolean var8,
      List var9,
      HomeUiState$Content$ContentHeader$CloseButtonColor var10,
      int var11,
      Object var12
   ) {
      if ((var11 & 1) != 0) {
         var1 = var0.showLogo;
      }

      if ((var11 & 2) != 0) {
         var2 = var0.logoUrl;
      }

      if ((var11 & 4) != 0) {
         var3 = var0.logoDarkUrl;
      }

      if ((var11 & 8) != 0) {
         var4 = var0.greeting;
      }

      if ((var11 & 16) != 0) {
         var5 = var0.intro;
      }

      if ((var11 & 32) != 0) {
         var6 = var0.headerBackdropStyle;
      }

      if ((var11 & 64) != 0) {
         var7 = var0.headerBackdropStyleDark;
      }

      if ((var11 & 128) != 0) {
         var8 = var0.showAvatars;
      }

      if ((var11 & 256) != 0) {
         var9 = var0.adminsAvatars;
      }

      if ((var11 & 512) != 0) {
         var10 = var0.closeButtonColor;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10);
   }

   @NotNull
   @Override
   public String toString() {
      return "ContentHeader(showLogo="
         + this.showLogo
         + ", logoUrl="
         + this.logoUrl
         + ", logoDarkUrl="
         + this.logoDarkUrl
         + ", greeting="
         + this.greeting
         + ", intro="
         + this.intro
         + ", headerBackdropStyle="
         + this.headerBackdropStyle
         + ", headerBackdropStyleDark="
         + this.headerBackdropStyleDark
         + ", showAvatars="
         + this.showAvatars
         + ", adminsAvatars="
         + this.adminsAvatars
         + ", closeButtonColor="
         + this.closeButtonColor
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Boolean.hashCode(this.showLogo);
      var1 = var1 * 31 + (this.logoUrl == null ? 0 : this.logoUrl.hashCode());
      var1 = var1 * 31 + (this.logoDarkUrl == null ? 0 : this.logoDarkUrl.hashCode());
      var1 = var1 * 31 + this.greeting.hashCode();
      var1 = var1 * 31 + this.intro.hashCode();
      var1 = var1 * 31 + this.headerBackdropStyle.hashCode();
      var1 = var1 * 31 + (this.headerBackdropStyleDark == null ? 0 : this.headerBackdropStyleDark.hashCode());
      var1 = var1 * 31 + Boolean.hashCode(this.showAvatars);
      var1 = var1 * 31 + this.adminsAvatars.hashCode();
      return var1 * 31 + this.closeButtonColor.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof HomeUiState$Content$ContentHeader)) {
         return false;
      } else {
         HomeUiState$Content$ContentHeader var2 = (HomeUiState$Content$ContentHeader)var1;
         if (this.showLogo != var2.showLogo) {
            return false;
         } else if (!Intrinsics.areEqual(this.logoUrl, var2.logoUrl)) {
            return false;
         } else if (!Intrinsics.areEqual(this.logoDarkUrl, var2.logoDarkUrl)) {
            return false;
         } else if (!Intrinsics.areEqual(this.greeting, var2.greeting)) {
            return false;
         } else if (!Intrinsics.areEqual(this.intro, var2.intro)) {
            return false;
         } else if (!Intrinsics.areEqual(this.headerBackdropStyle, var2.headerBackdropStyle)) {
            return false;
         } else if (!Intrinsics.areEqual(this.headerBackdropStyleDark, var2.headerBackdropStyleDark)) {
            return false;
         } else if (this.showAvatars != var2.showAvatars) {
            return false;
         } else {
            return !Intrinsics.areEqual(this.adminsAvatars, var2.adminsAvatars) ? false : Intrinsics.areEqual(this.closeButtonColor, var2.closeButtonColor);
         }
      }
   }
}
