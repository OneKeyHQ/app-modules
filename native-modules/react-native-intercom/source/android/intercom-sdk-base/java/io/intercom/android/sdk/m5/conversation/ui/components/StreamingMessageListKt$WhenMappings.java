package io.intercom.android.sdk.m5.conversation.ui.components;

import io.intercom.android.sdk.m5.conversation.states.GroupingPosition;
import kotlin.Metadata;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class StreamingMessageListKt$WhenMappings {
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$0;

   static {
      int[] var0 = new int[GroupingPosition.values().length];

      try {
         var0[GroupingPosition.STANDALONE.ordinal()] = 1;
      } catch (NoSuchFieldError var5) {
      }

      try {
         var0[GroupingPosition.TOP.ordinal()] = 2;
      } catch (NoSuchFieldError var4) {
      }

      try {
         var0[GroupingPosition.MIDDLE.ordinal()] = 3;
      } catch (NoSuchFieldError var3) {
      }

      try {
         var0[GroupingPosition.BOTTOM.ordinal()] = 4;
      } catch (NoSuchFieldError var2) {
      }

      $EnumSwitchMapping$0 = var0;
   }
}
