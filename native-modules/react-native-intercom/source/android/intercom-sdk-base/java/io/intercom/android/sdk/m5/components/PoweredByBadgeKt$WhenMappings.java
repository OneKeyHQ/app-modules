package io.intercom.android.sdk.m5.components;

import io.intercom.android.sdk.models.PoweredBy$PoweredByIconType;
import kotlin.Metadata;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class PoweredByBadgeKt$WhenMappings {
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$0;

   static {
      int[] var0 = new int[PoweredBy$PoweredByIconType.values().length];

      try {
         var0[PoweredBy$PoweredByIconType.INTERCOM.ordinal()] = 1;
      } catch (NoSuchFieldError var3) {
      }

      try {
         var0[PoweredBy$PoweredByIconType.FIN.ordinal()] = 2;
      } catch (NoSuchFieldError var2) {
      }

      $EnumSwitchMapping$0 = var0;
   }
}
