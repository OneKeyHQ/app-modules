package io.intercom.android.sdk.m5.home.reducers;

import io.intercom.android.sdk.models.HeaderBackdropType;
import kotlin.Metadata;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class HomeHeaderStateReducerKt$WhenMappings {
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$0;

   static {
      int[] var0 = new int[HeaderBackdropType.values().length];

      try {
         var0[HeaderBackdropType.SOLID.ordinal()] = 1;
      } catch (NoSuchFieldError var4) {
      }

      try {
         var0[HeaderBackdropType.COLORS.ordinal()] = 2;
      } catch (NoSuchFieldError var3) {
      }

      try {
         var0[HeaderBackdropType.IMAGE.ordinal()] = 3;
      } catch (NoSuchFieldError var2) {
      }

      $EnumSwitchMapping$0 = var0;
   }
}
