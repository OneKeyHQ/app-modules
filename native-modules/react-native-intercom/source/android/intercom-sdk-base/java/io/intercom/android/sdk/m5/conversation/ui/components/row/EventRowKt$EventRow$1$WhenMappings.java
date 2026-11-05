package io.intercom.android.sdk.m5.conversation.ui.components.row;

import io.intercom.android.sdk.models.Weight;
import kotlin.Metadata;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class EventRowKt$EventRow$1$WhenMappings {
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$0;

   static {
      int[] var0 = new int[Weight.values().length];

      try {
         var0[Weight.REGULAR.ordinal()] = 1;
      } catch (NoSuchFieldError var6) {
      }

      try {
         var0[Weight.LIGHT.ordinal()] = 2;
      } catch (NoSuchFieldError var5) {
      }

      try {
         var0[Weight.MEDIUM.ordinal()] = 3;
      } catch (NoSuchFieldError var4) {
      }

      try {
         var0[Weight.SEMI_BOLD.ordinal()] = 4;
      } catch (NoSuchFieldError var3) {
      }

      try {
         var0[Weight.BOLD.ordinal()] = 5;
      } catch (NoSuchFieldError var2) {
      }

      $EnumSwitchMapping$0 = var0;
   }
}
