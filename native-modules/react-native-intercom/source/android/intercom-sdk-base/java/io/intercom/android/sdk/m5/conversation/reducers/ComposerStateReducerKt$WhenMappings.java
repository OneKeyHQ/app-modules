package io.intercom.android.sdk.m5.conversation.reducers;

import io.intercom.android.sdk.models.ConversationEndedButton$Reason;
import kotlin.Metadata;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class ComposerStateReducerKt$WhenMappings {
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$0;

   static {
      int[] var0 = new int[ConversationEndedButton$Reason.values().length];

      try {
         var0[ConversationEndedButton$Reason.MERGED_INTO_CONVERSATION.ordinal()] = 1;
      } catch (NoSuchFieldError var2) {
      }

      $EnumSwitchMapping$0 = var0;
   }
}
