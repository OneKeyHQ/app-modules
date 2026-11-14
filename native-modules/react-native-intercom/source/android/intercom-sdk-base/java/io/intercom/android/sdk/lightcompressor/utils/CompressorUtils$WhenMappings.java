package io.intercom.android.sdk.lightcompressor.utils;

import io.intercom.android.sdk.lightcompressor.VideoQuality;
import kotlin.Metadata;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class CompressorUtils$WhenMappings {
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$0;

   static {
      int[] var0 = new int[VideoQuality.values().length];

      try {
         var0[VideoQuality.VERY_LOW.ordinal()] = 1;
      } catch (NoSuchFieldError var6) {
      }

      try {
         var0[VideoQuality.LOW.ordinal()] = 2;
      } catch (NoSuchFieldError var5) {
      }

      try {
         var0[VideoQuality.MEDIUM.ordinal()] = 3;
      } catch (NoSuchFieldError var4) {
      }

      try {
         var0[VideoQuality.HIGH.ordinal()] = 4;
      } catch (NoSuchFieldError var3) {
      }

      try {
         var0[VideoQuality.VERY_HIGH.ordinal()] = 5;
      } catch (NoSuchFieldError var2) {
      }

      $EnumSwitchMapping$0 = var0;
   }
}
