package io.intercom.android.sdk.models;

import androidx.compose.runtime.internal.StabilityInferred;
import com.google.gson.annotations.SerializedName;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@kotlin.Metadata(
   mv = {2, 0, 0},
   k = 1,
   xi = 48,
   d1 = "\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\"\n\u0002\u0010\b\n\u0002\b\u0004\b\u0081\b\u0018\u00002\u00020\u0001:\u000234Ba\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\u0006\u0010\b\u001a\u00020\u0003\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\b\u0010\r\u001a\u0004\u0018\u00010\u000e\u0012\u0006\u0010\u000f\u001a\u00020\f\u0012\u0006\u0010\u0010\u001a\u00020\f¢\u0006\u0004\b\u0011\u0010\u0012J\t\u0010\"\u001a\u00020\u0003HÆ\u0003J\t\u0010#\u001a\u00020\u0003HÆ\u0003J\t\u0010$\u001a\u00020\u0003HÆ\u0003J\t\u0010%\u001a\u00020\u0003HÆ\u0003J\t\u0010&\u001a\u00020\u0003HÆ\u0003J\t\u0010'\u001a\u00020\u0003HÆ\u0003J\t\u0010(\u001a\u00020\nHÆ\u0003J\t\u0010)\u001a\u00020\fHÆ\u0003J\u000b\u0010*\u001a\u0004\u0018\u00010\u000eHÆ\u0003J\t\u0010+\u001a\u00020\fHÆ\u0003J\t\u0010,\u001a\u00020\fHÆ\u0003Jy\u0010-\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\f2\n\b\u0002\u0010\r\u001a\u0004\u0018\u00010\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\f2\b\b\u0002\u0010\u0010\u001a\u00020\fHÇ\u0001J\u0013\u0010.\u001a\u00020\f2\b\u0010/\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u00100\u001a\u000201H×\u0001J\t\u00102\u001a\u00020\u0003H×\u0001R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0016\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0014R\u0016\u0010\u0005\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0014R\u0016\u0010\u0006\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0014R\u0016\u0010\u0007\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0014R\u0016\u0010\b\u001a\u00020\u00038\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0014R\u0016\u0010\t\u001a\u00020\n8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0016\u0010\u000b\u001a\u00020\f8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0018\u0010\r\u001a\u0004\u0018\u00010\u000e8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u0016\u0010\u000f\u001a\u00020\f8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b \u0010\u001dR\u0016\u0010\u0010\u001a\u00020\f8\u0006X\u0087\u0004¢\u0006\b\n\u0000\u001a\u0004\b!\u0010\u001d¨\u00065",
   d2 = {
         "Lio/intercom/android/sdk/models/Header;",
         "",
         "backgroundColor",
         "",
         "backgroundColorDark",
         "foregroundPrimaryColor",
         "foregroundPrimaryColorDark",
         "foregroundSecondaryColor",
         "foregroundSecondaryColorDark",
         "collapsed",
         "Lio/intercom/android/sdk/models/Header$Collapsed;",
         "displayActiveIndicator",
         "",
         "expanded",
         "Lio/intercom/android/sdk/models/Header$Expanded;",
         "useBotHeader",
         "hideGreeting",
         "<init>",
         "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Lio/intercom/android/sdk/models/Header$Collapsed;ZLio/intercom/android/sdk/models/Header$Expanded;ZZ)V",
         "getBackgroundColor",
         "()Ljava/lang/String;",
         "getBackgroundColorDark",
         "getForegroundPrimaryColor",
         "getForegroundPrimaryColorDark",
         "getForegroundSecondaryColor",
         "getForegroundSecondaryColorDark",
         "getCollapsed",
         "()Lio/intercom/android/sdk/models/Header$Collapsed;",
         "getDisplayActiveIndicator",
         "()Z",
         "getExpanded",
         "()Lio/intercom/android/sdk/models/Header$Expanded;",
         "getUseBotHeader",
         "getHideGreeting",
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
         "equals",
         "other",
         "hashCode",
         "",
         "toString",
         "Collapsed",
         "Expanded",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 0)
public final class Header {
   @SerializedName("background_color")
   @NotNull
   private final String backgroundColor;
   @SerializedName("background_color_dark")
   @NotNull
   private final String backgroundColorDark;
   @SerializedName("foreground_primary_color")
   @NotNull
   private final String foregroundPrimaryColor;
   @SerializedName("foreground_primary_color_dark")
   @NotNull
   private final String foregroundPrimaryColorDark;
   @SerializedName("foreground_secondary_color")
   @NotNull
   private final String foregroundSecondaryColor;
   @SerializedName("foreground_secondary_color_dark")
   @NotNull
   private final String foregroundSecondaryColorDark;
   @SerializedName("collapsed")
   @NotNull
   private final Header$Collapsed collapsed;
   @SerializedName("display_active_indicator")
   private final boolean displayActiveIndicator;
   @SerializedName("expanded")
   @Nullable
   private final Header$Expanded expanded;
   @SerializedName("use_bot_header")
   private final boolean useBotHeader;
   @SerializedName("hide_greeting")
   private final boolean hideGreeting;
   public static final int $stable;

   static { $stable = 8; }

   public Header(
      @NotNull String var1,
      @NotNull String var2,
      @NotNull String var3,
      @NotNull String var4,
      @NotNull String var5,
      @NotNull String var6,
      @NotNull Header$Collapsed var7,
      boolean var8,
      @Nullable Header$Expanded var9,
      boolean var10,
      boolean var11
   ) {
      super();
      Intrinsics.checkNotNullParameter(var1, "backgroundColor");
      Intrinsics.checkNotNullParameter(var2, "backgroundColorDark");
      Intrinsics.checkNotNullParameter(var3, "foregroundPrimaryColor");
      Intrinsics.checkNotNullParameter(var4, "foregroundPrimaryColorDark");
      Intrinsics.checkNotNullParameter(var5, "foregroundSecondaryColor");
      Intrinsics.checkNotNullParameter(var6, "foregroundSecondaryColorDark");
      Intrinsics.checkNotNullParameter(var7, "collapsed");
      this.backgroundColor = var1;
      this.backgroundColorDark = var2;
      this.foregroundPrimaryColor = var3;
      this.foregroundPrimaryColorDark = var4;
      this.foregroundSecondaryColor = var5;
      this.foregroundSecondaryColorDark = var6;
      this.collapsed = var7;
      this.displayActiveIndicator = var8;
      this.expanded = var9;
      this.useBotHeader = var10;
      this.hideGreeting = var11;
   }

   @NotNull
   public final String getBackgroundColor() {
      return this.backgroundColor;
   }

   @NotNull
   public final String getBackgroundColorDark() {
      return this.backgroundColorDark;
   }

   @NotNull
   public final String getForegroundPrimaryColor() {
      return this.foregroundPrimaryColor;
   }

   @NotNull
   public final String getForegroundPrimaryColorDark() {
      return this.foregroundPrimaryColorDark;
   }

   @NotNull
   public final String getForegroundSecondaryColor() {
      return this.foregroundSecondaryColor;
   }

   @NotNull
   public final String getForegroundSecondaryColorDark() {
      return this.foregroundSecondaryColorDark;
   }

   @NotNull
   public final Header$Collapsed getCollapsed() {
      return this.collapsed;
   }

   public final boolean getDisplayActiveIndicator() {
      return this.displayActiveIndicator;
   }

   @Nullable
   public final Header$Expanded getExpanded() {
      return this.expanded;
   }

   public final boolean getUseBotHeader() {
      return this.useBotHeader;
   }

   public final boolean getHideGreeting() {
      return this.hideGreeting;
   }

   @NotNull
   public final String component1() {
      return this.backgroundColor;
   }

   @NotNull
   public final String component2() {
      return this.backgroundColorDark;
   }

   @NotNull
   public final String component3() {
      return this.foregroundPrimaryColor;
   }

   @NotNull
   public final String component4() {
      return this.foregroundPrimaryColorDark;
   }

   @NotNull
   public final String component5() {
      return this.foregroundSecondaryColor;
   }

   @NotNull
   public final String component6() {
      return this.foregroundSecondaryColorDark;
   }

   @NotNull
   public final Header$Collapsed component7() {
      return this.collapsed;
   }

   public final boolean component8() {
      return this.displayActiveIndicator;
   }

   @Nullable
   public final Header$Expanded component9() {
      return this.expanded;
   }

   public final boolean component10() {
      return this.useBotHeader;
   }

   public final boolean component11() {
      return this.hideGreeting;
   }

   @NotNull
   public final Header copy(
      @NotNull String var1,
      @NotNull String var2,
      @NotNull String var3,
      @NotNull String var4,
      @NotNull String var5,
      @NotNull String var6,
      @NotNull Header$Collapsed var7,
      boolean var8,
      @Nullable Header$Expanded var9,
      boolean var10,
      boolean var11
   ) {
      Intrinsics.checkNotNullParameter(var1, "backgroundColor");
      Intrinsics.checkNotNullParameter(var2, "backgroundColorDark");
      Intrinsics.checkNotNullParameter(var3, "foregroundPrimaryColor");
      Intrinsics.checkNotNullParameter(var4, "foregroundPrimaryColorDark");
      Intrinsics.checkNotNullParameter(var5, "foregroundSecondaryColor");
      Intrinsics.checkNotNullParameter(var6, "foregroundSecondaryColorDark");
      Intrinsics.checkNotNullParameter(var7, "collapsed");
      return new Header(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   // $VF: synthetic method
   public static Header copy$default(
      Header var0,
      String var1,
      String var2,
      String var3,
      String var4,
      String var5,
      String var6,
      Header$Collapsed var7,
      boolean var8,
      Header$Expanded var9,
      boolean var10,
      boolean var11,
      int var12,
      Object var13
   ) {
      if ((var12 & 1) != 0) {
         var1 = var0.backgroundColor;
      }

      if ((var12 & 2) != 0) {
         var2 = var0.backgroundColorDark;
      }

      if ((var12 & 4) != 0) {
         var3 = var0.foregroundPrimaryColor;
      }

      if ((var12 & 8) != 0) {
         var4 = var0.foregroundPrimaryColorDark;
      }

      if ((var12 & 16) != 0) {
         var5 = var0.foregroundSecondaryColor;
      }

      if ((var12 & 32) != 0) {
         var6 = var0.foregroundSecondaryColorDark;
      }

      if ((var12 & 64) != 0) {
         var7 = var0.collapsed;
      }

      if ((var12 & 128) != 0) {
         var8 = var0.displayActiveIndicator;
      }

      if ((var12 & 256) != 0) {
         var9 = var0.expanded;
      }

      if ((var12 & 512) != 0) {
         var10 = var0.useBotHeader;
      }

      if ((var12 & 1024) != 0) {
         var11 = var0.hideGreeting;
      }

      return var0.copy(var1, var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
   }

   @NotNull
   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("Header(backgroundColor=")
         .append(this.backgroundColor)
         .append(", backgroundColorDark=")
         .append(this.backgroundColorDark)
         .append(", foregroundPrimaryColor=")
         .append(this.foregroundPrimaryColor)
         .append(", foregroundPrimaryColorDark=")
         .append(this.foregroundPrimaryColorDark)
         .append(", foregroundSecondaryColor=")
         .append(this.foregroundSecondaryColor)
         .append(", foregroundSecondaryColorDark=")
         .append(this.foregroundSecondaryColorDark)
         .append(", collapsed=")
         .append(this.collapsed)
         .append(", displayActiveIndicator=")
         .append(this.displayActiveIndicator)
         .append(", expanded=")
         .append(this.expanded)
         .append(", useBotHeader=")
         .append(this.useBotHeader)
         .append(", hideGreeting=")
         .append(this.hideGreeting)
         .append(')');
      return var1.toString();
   }

   @Override
   public int hashCode() {
      int var1 = this.backgroundColor.hashCode();
      var1 = var1 * 31 + this.backgroundColorDark.hashCode();
      var1 = var1 * 31 + this.foregroundPrimaryColor.hashCode();
      var1 = var1 * 31 + this.foregroundPrimaryColorDark.hashCode();
      var1 = var1 * 31 + this.foregroundSecondaryColor.hashCode();
      var1 = var1 * 31 + this.foregroundSecondaryColorDark.hashCode();
      var1 = var1 * 31 + this.collapsed.hashCode();
      var1 = var1 * 31 + Boolean.hashCode(this.displayActiveIndicator);
      var1 = var1 * 31 + (this.expanded == null ? 0 : this.expanded.hashCode());
      var1 = var1 * 31 + Boolean.hashCode(this.useBotHeader);
      return var1 * 31 + Boolean.hashCode(this.hideGreeting);
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof Header)) {
         return false;
      } else {
         Header var2 = (Header)var1;
         if (!Intrinsics.areEqual(this.backgroundColor, var2.backgroundColor)) {
            return false;
         } else if (!Intrinsics.areEqual(this.backgroundColorDark, var2.backgroundColorDark)) {
            return false;
         } else if (!Intrinsics.areEqual(this.foregroundPrimaryColor, var2.foregroundPrimaryColor)) {
            return false;
         } else if (!Intrinsics.areEqual(this.foregroundPrimaryColorDark, var2.foregroundPrimaryColorDark)) {
            return false;
         } else if (!Intrinsics.areEqual(this.foregroundSecondaryColor, var2.foregroundSecondaryColor)) {
            return false;
         } else if (!Intrinsics.areEqual(this.foregroundSecondaryColorDark, var2.foregroundSecondaryColorDark)) {
            return false;
         } else if (!Intrinsics.areEqual(this.collapsed, var2.collapsed)) {
            return false;
         } else if (this.displayActiveIndicator != var2.displayActiveIndicator) {
            return false;
         } else if (!Intrinsics.areEqual(this.expanded, var2.expanded)) {
            return false;
         } else {
            return this.useBotHeader != var2.useBotHeader ? false : this.hideGreeting == var2.hideGreeting;
         }
      }
   }
}
