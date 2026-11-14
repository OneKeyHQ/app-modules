package io.intercom.android.sdk.m5.conversation.reducers;

import io.intercom.android.sdk.models.Header$Collapsed$HeaderIconType;
import kotlin.Metadata;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class HeaderReducerKt$WhenMappings {
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$0;

   static {
      int[] var0 = new int[Header$Collapsed$HeaderIconType.values().length];

      try {
         var0[Header$Collapsed$HeaderIconType.CLOCK.ordinal()] = 1;
      } catch (NoSuchFieldError var3) {
      }

      try {
         var0[Header$Collapsed$HeaderIconType.AI.ordinal()] = 2;
      } catch (NoSuchFieldError var2) {
      }

      $EnumSwitchMapping$0 = var0;
   }
}
