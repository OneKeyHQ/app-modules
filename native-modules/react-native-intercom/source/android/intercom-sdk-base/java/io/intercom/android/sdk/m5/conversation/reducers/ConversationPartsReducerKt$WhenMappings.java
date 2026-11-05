package io.intercom.android.sdk.m5.conversation.reducers;

import io.intercom.android.sdk.models.MessageStyle;
import kotlin.Metadata;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class ConversationPartsReducerKt$WhenMappings {
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$0;

   static {
      int[] var0 = new int[MessageStyle.values().length];

      try {
         var0[MessageStyle.TICKET_STATE_UPDATED.ordinal()] = 1;
      } catch (NoSuchFieldError var3) {
      }

      try {
         var0[MessageStyle.MERGED_PRIMARY_CONVERSATION.ordinal()] = 2;
      } catch (NoSuchFieldError var2) {
      }

      $EnumSwitchMapping$0 = var0;
   }
}
