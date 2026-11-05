package io.intercom.android.sdk.m5.inbox.ui;

import io.intercom.android.sdk.models.ActionType;
import kotlin.Metadata;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class InboxScreenKt$InboxContent$5$1$1$3$WhenMappings {
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$0;

   static {
      int[] var0 = new int[ActionType.values().length];

      try {
         var0[ActionType.MESSAGE.ordinal()] = 1;
      } catch (NoSuchFieldError var3) {
      }

      try {
         var0[ActionType.HELP.ordinal()] = 2;
      } catch (NoSuchFieldError var2) {
      }

      $EnumSwitchMapping$0 = var0;
   }
}
