package io.intercom.android.sdk.utilities;

import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.ColorKt;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(
   mv = {2, 0, 0},
   k = 2,
   xi = 48,
   d1 = "\u0000\"\n\u0000\n\u0002\u0010\u0007\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0011\n\u0002\u0010\u000e\n\u0002\b\u0005\u001a\u0013\u0010\u0003\u001a\u00020\u0004*\u00020\u0004H\u0000¢\u0006\u0004\b\u0005\u0010\u0006\u001a\u0013\u0010\u0007\u001a\u00020\u0004*\u00020\u0004H\u0000¢\u0006\u0004\b\b\u0010\u0006\u001a\u0013\u0010\t\u001a\u00020\n*\u00020\u0004H\u0000¢\u0006\u0004\b\u000b\u0010\f\u001a\u0013\u0010\r\u001a\u00020\n*\u00020\u0004H\u0000¢\u0006\u0004\b\u000e\u0010\f\u001a\u0013\u0010\u000f\u001a\u00020\n*\u00020\u0004H\u0000¢\u0006\u0004\b\u0010\u0010\f\u001a\u0013\u0010\u0011\u001a\u00020\n*\u00020\u0004H\u0000¢\u0006\u0004\b\u0012\u0010\f\u001a\u0013\u0010\u0013\u001a\u00020\u0004*\u00020\u0004H\u0000¢\u0006\u0004\b\u0014\u0010\u0006\u001a\u0013\u0010\u0015\u001a\u00020\u0004*\u00020\u0004H\u0000¢\u0006\u0004\b\u0016\u0010\u0006\u001a\u0013\u0010\u0017\u001a\u00020\u0004*\u00020\u0004H\u0000¢\u0006\u0004\b\u0018\u0010\u0006\u001a\u0013\u0010\u0019\u001a\u00020\n*\u00020\u0004H\u0002¢\u0006\u0004\b\u001a\u0010\f\u001a\u001b\u0010\u001b\u001a\u00020\u0004*\u00020\u001c2\b\b\u0002\u0010\u001d\u001a\u00020\u0001H\u0000¢\u0006\u0002\u0010\u001e\u001a\u0013\u0010\u001f\u001a\u00020\u0004*\u00020\u0004H\u0000¢\u0006\u0004\b \u0010\u0006\"\u000e\u0010\u0000\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000\"\u000e\u0010\u0002\u001a\u00020\u0001X\u0082T¢\u0006\u0002\n\u0000¨\u0006!",
   d2 = {
         "BRIGHTNESS_CUTOFF",
         "",
         "WHITENESS_CUTOFF",
         "darken",
         "Landroidx/compose/ui/graphics/Color;",
         "darken-8_81llA",
         "(J)J",
         "lighten",
         "lighten-8_81llA",
         "isDarkColor",
         "",
         "isDarkColor-8_81llA",
         "(J)Z",
         "isLightColor",
         "isLightColor-8_81llA",
         "isWhite",
         "isWhite-8_81llA",
         "isBlack",
         "isBlack-8_81llA",
         "generateTextColor",
         "generateTextColor-8_81llA",
         "getAccessibleBorderColor",
         "getAccessibleBorderColor-8_81llA",
         "getAccessibleColorOnWhiteBackground",
         "getAccessibleColorOnWhiteBackground-8_81llA",
         "isColorTooWhite",
         "isColorTooWhite-8_81llA",
         "toComposeColor",
         "",
         "opacity",
         "(Ljava/lang/String;F)J",
         "getAccessibleColorOnDarkBackground",
         "getAccessibleColorOnDarkBackground-8_81llA",
         "intercom-sdk-base_release"
   }
)
public final class ColorExtensionsKt {
   private static final float BRIGHTNESS_CUTOFF = 0.6F;
   private static final float WHITENESS_CUTOFF = 0.9411765F;

   public static final long recovered$da172df9fccf1b21(long var0) {
      int var2 = ColorUtils.darkenColor(ColorKt.recovered$81a7e7de546cbf77(var0));
      return ColorKt.Color(var2);
   }

   public static final long recovered$de7fe4417d7923f0(long var0) {
      int var2 = ColorUtils.lightenColor(ColorKt.recovered$81a7e7de546cbf77(var0));
      return ColorKt.Color(var2);
   }

   public static final boolean recovered$41e561cae61044c5(long var0) {
      return ColorKt.recovered$5610b982511547da(var0) < 0.6F;
   }

   public static final boolean recovered$4886b56a29c1c487(long var0) {
      return !recovered$41e561cae61044c5(var0);
   }

   public static final boolean recovered$d9b2a0cefdbf2194(long var0) {
      return Color.recovered$9eab66ea8c46e11c(var0, Color.Companion.recovered$95989221dad41c4d());
   }

   public static final boolean recovered$4c5bbe819723778c(long var0) {
      return Color.recovered$9eab66ea8c46e11c(var0, Color.Companion.recovered$235b30c090a399c5());
   }

   public static final long recovered$f5f9d7dd14b2e4c1(long var0) {
      return recovered$41e561cae61044c5(var0) ? Color.Companion.recovered$95989221dad41c4d() : Color.Companion.recovered$235b30c090a399c5();
   }

   public static final long recovered$9902137196ed1ac6(long var0) {
      return recovered$41e561cae61044c5(var0) ? recovered$de7fe4417d7923f0(var0) : recovered$da172df9fccf1b21(var0);
   }

   public static final long recovered$93702c38c1546c4a(long var0) {
      return recovered$3e6b5bec84967852(var0) ? Color.Companion.recovered$235b30c090a399c5() : var0;
   }

   private static final boolean recovered$3e6b5bec84967852(long var0) {
      return Color.recovered$bdf9bcb1558978c9(var0) >= 0.9411765F
         && Color.recovered$96aba75a6ff233d7(var0) >= 0.9411765F
         && Color.recovered$96bbdb8737e01af3(var0) >= 0.9411765F;
   }

   public static final long toComposeColor(@NotNull String var0, float var1) {
      Intrinsics.checkNotNullParameter(var0, "<this>");
      return Color.recovered$41aa0ee03c2282fb(ColorKt.Color(ColorUtils.parseColor(var0)), var1, 0.0F, 0.0F, 0.0F, 14, null);
   }

   // $VF: synthetic method
   public static long toComposeColor$default(String var0, float var1, int var2, Object var3) {
      if ((var2 & 1) != 0) {
         var1 = 1.0F;
      }

      return toComposeColor(var0, var1);
   }

   public static final long recovered$4576896f541dc33e(long var0) {
      return recovered$41e561cae61044c5(var0) ? recovered$de7fe4417d7923f0(var0) : var0;
   }
}
