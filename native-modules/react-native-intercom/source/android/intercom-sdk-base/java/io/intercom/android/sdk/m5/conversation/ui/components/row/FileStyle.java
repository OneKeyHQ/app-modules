package io.intercom.android.sdk.m5.conversation.ui.components.row;

import androidx.annotation.DrawableRes;
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
   d1 = "\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0014\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0081\b\u0018\u00002\u00020\u0001B)\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b¢\u0006\u0004\b\t\u0010\nJ\t\u0010\u0013\u001a\u00020\u0003HÆ\u0003J\u0010\u0010\u0014\u001a\u00020\u0005HÆ\u0003¢\u0006\u0004\b\u0015\u0010\u000eJ\u0010\u0010\u0016\u001a\u00020\u0005HÆ\u0003¢\u0006\u0004\b\u0017\u0010\u000eJ\t\u0010\u0018\u001a\u00020\bHÆ\u0003J8\u0010\u0019\u001a\u00020\u00002\b\b\u0003\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\bHÇ\u0001¢\u0006\u0004\b\u001a\u0010\u001bJ\u0013\u0010\u001c\u001a\u00020\u001d2\b\u0010\u001e\u001a\u0004\u0018\u00010\u0001H×\u0003J\t\u0010\u001f\u001a\u00020\u0003H×\u0001J\t\u0010 \u001a\u00020\bH×\u0001R\u0011\u0010\u0002\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0013\u0010\u0004\u001a\u00020\u0005¢\u0006\n\n\u0002\u0010\u000f\u001a\u0004\b\r\u0010\u000eR\u0013\u0010\u0006\u001a\u00020\u0005¢\u0006\n\n\u0002\u0010\u000f\u001a\u0004\b\u0010\u0010\u000eR\u0011\u0010\u0007\u001a\u00020\b¢\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012¨\u0006!",
   d2 = {
         "Lio/intercom/android/sdk/m5/conversation/ui/components/row/FileStyle;",
         "",
         "icon",
         "",
         "backgroundColor",
         "Landroidx/compose/ui/graphics/Color;",
         "iconColor",
         "type",
         "",
         "<init>",
         "(IJJLjava/lang/String;Lkotlin/jvm/internal/DefaultConstructorMarker;)V",
         "getIcon",
         "()I",
         "getBackgroundColor-0d7_KjU",
         "()J",
         "J",
         "getIconColor-0d7_KjU",
         "getType",
         "()Ljava/lang/String;",
         "component1",
         "component2",
         "component2-0d7_KjU",
         "component3",
         "component3-0d7_KjU",
         "component4",
         "copy",
         "copy-IbeAmgk",
         "(IJJLjava/lang/String;)Lio/intercom/android/sdk/m5/conversation/ui/components/row/FileStyle;",
         "equals",
         "",
         "other",
         "hashCode",
         "toString",
         "intercom-sdk-base_release"
   }
)
@StabilityInferred(parameters = 1)
public final class FileStyle {
   private final int icon;
   private final long backgroundColor;
   private final long iconColor;
   @NotNull
   private final String type;
   public static int $stable;

   static {}

   private FileStyle(int var1, long var2, long var4, String var6) {
      super();
      Intrinsics.checkNotNullParameter(var6, "type");
      this.icon = var1;
      this.backgroundColor = var2;
      this.iconColor = var4;
      this.type = var6;
   }

   public final int getIcon() {
      return this.icon;
   }

   public final long recovered$934f6183e8132565() {
      return this.backgroundColor;
   }

   public final long recovered$01833f34228693e4() {
      return this.iconColor;
   }

   @NotNull
   public final String getType() {
      return this.type;
   }

   public final int component1() {
      return this.icon;
   }

   public final long recovered$306ddc2ccc425e26() {
      return this.backgroundColor;
   }

   public final long recovered$46bfddf50387e286() {
      return this.iconColor;
   }

   @NotNull
   public final String component4() {
      return this.type;
   }

   @NotNull
   public final FileStyle recovered$c13b2ec7ea2e7309(@DrawableRes int var1, long var2, long var4, @NotNull String var6) {
      Intrinsics.checkNotNullParameter(var6, "type");
      return new FileStyle(var1, var2, var4, var6, null);
   }

   // $VF: synthetic method
   public static FileStyle recovered$2a7918bcf64e57f0(FileStyle var0, int var1, long var2, long var4, String var6, int var7, Object var8) {
      if ((var7 & 1) != 0) {
         var1 = var0.icon;
      }

      if ((var7 & 2) != 0) {
         var2 = var0.backgroundColor;
      }

      if ((var7 & 4) != 0) {
         var4 = var0.iconColor;
      }

      if ((var7 & 8) != 0) {
         var6 = var0.type;
      }

      return var0.recovered$c13b2ec7ea2e7309(var1, var2, var4, var6);
   }

   @NotNull
   @Override
   public String toString() {
      return "FileStyle(icon="
         + this.icon
         + ", backgroundColor="
         + Color.recovered$cfef63bb1d111f9f(this.backgroundColor)
         + ", iconColor="
         + Color.recovered$cfef63bb1d111f9f(this.iconColor)
         + ", type="
         + this.type
         + ')';
   }

   @Override
   public int hashCode() {
      int var1 = Integer.hashCode(this.icon);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.backgroundColor);
      var1 = var1 * 31 + Color.recovered$87e060624084b26b(this.iconColor);
      return var1 * 31 + this.type.hashCode();
   }

   @Override
   public boolean equals(@Nullable Object var1) {
      if (this == var1) {
         return true;
      } else if (!(var1 instanceof FileStyle)) {
         return false;
      } else {
         FileStyle var2 = (FileStyle)var1;
         if (this.icon != var2.icon) {
            return false;
         } else if (!Color.recovered$9eab66ea8c46e11c(this.backgroundColor, var2.backgroundColor)) {
            return false;
         } else {
            return !Color.recovered$9eab66ea8c46e11c(this.iconColor, var2.iconColor) ? false : Intrinsics.areEqual(this.type, var2.type);
         }
      }
   }

   // $VF: synthetic method
   public FileStyle(@DrawableRes int var1, long var2, long var4, String var6, DefaultConstructorMarker var7) {
      this(var1, var2, var4, var6);
   }
}
