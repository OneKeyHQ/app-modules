package io.intercom.android.sdk.m5.data;

import kotlin.Metadata;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class IntercomDataLayer$WhenMappings {
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$0;

   static {
      int[] var0 = new int[SessionStartClaim.values().length];

      try {
         var0[SessionStartClaim.IF_IDLE.ordinal()] = 1;
      } catch (NoSuchFieldError var4) {
      }

      try {
         var0[SessionStartClaim.NEW_VISIT.ordinal()] = 2;
      } catch (NoSuchFieldError var3) {
      }

      try {
         var0[SessionStartClaim.IF_OWED.ordinal()] = 3;
      } catch (NoSuchFieldError var2) {
      }

      $EnumSwitchMapping$0 = var0;
   }
}
