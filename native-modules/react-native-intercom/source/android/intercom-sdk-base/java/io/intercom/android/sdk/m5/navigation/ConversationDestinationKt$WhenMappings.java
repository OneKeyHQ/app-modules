package io.intercom.android.sdk.m5.navigation;

import androidx.lifecycle.Lifecycle.Event;
import kotlin.Metadata;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class ConversationDestinationKt$WhenMappings {
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$0;

   static {
      int[] var0 = new int[Event.values().length];

      try {
         var0[Event.ON_RESUME.ordinal()] = 1;
      } catch (NoSuchFieldError var3) {
      }

      try {
         var0[Event.ON_PAUSE.ordinal()] = 2;
      } catch (NoSuchFieldError var2) {
      }

      $EnumSwitchMapping$0 = var0;
   }
}
