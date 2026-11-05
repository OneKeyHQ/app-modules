package io.intercom.android.sdk.lightcompressor;

import io.intercom.android.sdk.lightcompressor.config.SaveLocation;
import kotlin.Metadata;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class VideoCompressor$WhenMappings {
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$0;

   static {
      int[] var0 = new int[SaveLocation.values().length];

      try {
         var0[SaveLocation.downloads.ordinal()] = 1;
      } catch (NoSuchFieldError var3) {
      }

      try {
         var0[SaveLocation.pictures.ordinal()] = 2;
      } catch (NoSuchFieldError var2) {
      }

      $EnumSwitchMapping$0 = var0;
   }
}
