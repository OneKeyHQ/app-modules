package io.intercom.android.sdk.m5.conversation.ui.components.composer;

import kotlin.Metadata;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class CameraInputButtonKt$WhenMappings {
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$0;

   static {
      int[] var0 = new int[CameraInputType.values().length];

      try {
         var0[CameraInputType.VIDEO.ordinal()] = 1;
      } catch (NoSuchFieldError var3) {
      }

      try {
         var0[CameraInputType.PHOTO.ordinal()] = 2;
      } catch (NoSuchFieldError var2) {
      }

      $EnumSwitchMapping$0 = var0;
   }
}
