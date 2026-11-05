package io.intercom.android.sdk.utilities;

import android.content.Context;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import io.intercom.android.sdk.R.color;
import io.intercom.android.sdk.identity.AppConfig;

public class ColorUtils {
   private static final float BRIGHTNESS_CUTOFF = 0.7F;
   private static final double ACCESSIBILITY_CONTRAST_RATIO = 4.5;
   private static final char COLOR_PREFIX = '#';
   private static final String SHORT_COLOR_REGEX = String.format("%s([0-9a-fA-F])([0-9a-fA-F])([0-9a-fA-F])", '#');
   private static final int SHORT_COLOR_WITH_PREFIX_LENGTH = 4;

   public static boolean isColorLight(String var0) {
      double var1 = androidx.core.graphics.ColorUtils.calculateLuminance(Color.parseColor(var0));
      return var1 > 0.7F;
   }

   public static boolean isColorLight(@ColorInt int var0) {
      double var1 = androidx.core.graphics.ColorUtils.calculateLuminance(var0);
      return 1.05 / (var1 + 0.05) < 4.5;
   }

   public static boolean isComparedColorsLowerThanAccessibilityContrastRatio(@ColorInt int var0, @ColorInt int var1) {
      return androidx.core.graphics.ColorUtils.calculateContrast(var0, var1) < 4.5;
   }

   public static int lightenColor(int var0) {
      int var1 = (Color.red(var0) + 255) / 2;
      int var2 = (Color.green(var0) + 255) / 2;
      int var3 = (Color.blue(var0) + 255) / 2;
      return Color.argb(Color.alpha(var0), var1, var2, var3);
   }

   public static int lightenColor(int var0, float var1) {
      float[] var2 = new float[3];
      androidx.core.graphics.ColorUtils.colorToHSL(var0, var2);
      var2[2] += var1;
      var2[2] = Math.max(0.0F, Math.min(var2[2], 1.0F));
      return androidx.core.graphics.ColorUtils.HSLToColor(var2);
   }

   public static int darkenColor(int var0) {
      float[] var1 = new float[3];
      Color.colorToHSV(var0, var1);
      var1[2] *= 0.79F;
      return Color.HSVToColor(var1);
   }

   public static int darkenColor(int var0, float var1) {
      float[] var2 = new float[3];
      androidx.core.graphics.ColorUtils.colorToHSL(var0, var2);
      var2[2] -= var1;
      var2[2] = Math.max(0.0F, Math.min(var2[2], 1.0F));
      return androidx.core.graphics.ColorUtils.HSLToColor(var2);
   }

   public static int desaturateColor(int var0, float var1) {
      float[] var2 = new float[3];
      androidx.core.graphics.ColorUtils.colorToHSL(var0, var2);
      var2[1] -= var1;
      var2[1] = Math.max(0.0F, Math.min(var2[1], 1.0F));
      return androidx.core.graphics.ColorUtils.HSLToColor(var2);
   }

   public static int buttonTextColorVariant(int var0) {
      float[] var1 = new float[3];
      androidx.core.graphics.ColorUtils.colorToHSL(var0, var1);
      float var2 = var1[2];
      float var3 = var2 - 0.3F;
      if (var2 > 0.9) {
         return darkenColor(var0, var3);
      } else {
         return var2 < 0.35 ? var0 : darkenColor(var0, var3);
      }
   }

   public static int buttonBackgroundColorVariant(int var0) {
      float[] var1 = new float[3];
      androidx.core.graphics.ColorUtils.colorToHSL(var0, var1);
      float var2 = var1[2];
      float var3 = 0.95F - var2;
      float var4 = var2 - 0.85F;
      if (var2 > 0.9) {
         return darkenColor(var0, var4);
      } else {
         return var2 < 0.35 ? lightenColor(var0, var3) : lightenColor(var0, var3);
      }
   }

   public static ColorFilter newGreyscaleFilter() {
      ColorMatrix var0 = new ColorMatrix();
      var0.setSaturation(0.0F);
      return new ColorMatrixColorFilter(var0);
   }

   public static void setTextColorWhiteOrBlack(TextView var0, boolean var1) {
      int var2 = whiteOrBlackColor(var0.getContext(), var1);
      var0.setTextColor(var2);
      var0.setLinkTextColor(var2);
   }

   public static void setTextColorWhiteOrDark(TextView var0, boolean var1) {
      int var2 = whiteOrDarkColor(var0.getContext(), var1);
      var0.setTextColor(var2);
      var0.setLinkTextColor(var2);
   }

   public static void setTextColorPrimaryOrDark(TextView var0, AppConfig var1) {
      int var2 = primaryOrDarkColor(var0.getContext(), var1);
      var0.setTextColor(var2);
      var0.setLinkTextColor(var2);
   }

   public static void setTextColorPrimaryOrBlack(TextView var0, AppConfig var1) {
      int var2 = primaryOrBlackColor(var0.getContext(), var1);
      var0.setTextColor(var2);
      var0.setLinkTextColor(var2);
   }

   @ColorInt
   public static int primaryOrBlackAccessibility(Context var0, AppConfig var1) {
      return isColorLight(var1.getPrimaryColor()) ? ContextCompat.getColor(var0, color.intercom_accessibility_black) : var1.getPrimaryColor();
   }

   @ColorInt
   public static int whiteOrBlackAccessibility(Context var0, AppConfig var1) {
      return isColorLight(var1.getPrimaryColor())
         ? ContextCompat.getColor(var0, color.intercom_accessibility_black)
         : ContextCompat.getColor(var0, color.intercom_white);
   }

   @ColorInt
   public static int primaryOrDarkColor(Context var0, AppConfig var1) {
      return var1.isPrimaryColorRenderDarkText() ? ContextCompat.getColor(var0, darkColorRes()) : var1.getPrimaryColor();
   }

   @ColorInt
   private static int primaryOrBlackColor(Context var0, AppConfig var1) {
      return var1.isPrimaryColorRenderDarkText() ? ContextCompat.getColor(var0, color.intercom_black) : var1.getPrimaryColor();
   }

   @ColorInt
   public static int whiteOrDarkColor(Context var0, boolean var1) {
      return var1 ? ContextCompat.getColor(var0, darkColorRes()) : ContextCompat.getColor(var0, color.intercom_white);
   }

   @ColorInt
   public static int whiteOrBlackColor(Context var0, boolean var1) {
      return ContextCompat.getColor(var0, var1 ? color.intercom_accessibility_black : color.intercom_white);
   }

   public static void setImageColorWhiteOrBlack(ImageView var0, boolean var1) {
      int var2 = whiteOrBlackColor(var0.getContext(), var1);
      var0.setColorFilter(var2);
   }

   @ColorInt
   public static int parseColor(@NonNull String var0) {
      if (var0.length() == 4 && var0.charAt(0) == '#') {
         var0 = var0.replaceAll(SHORT_COLOR_REGEX, String.format("%s$1$1$2$2$3$3", '#'));
      }

      return Color.parseColor(var0);
   }

   @ColorRes
   private static int darkColorRes() {
      return color.intercom_accessibility_black;
   }
}
