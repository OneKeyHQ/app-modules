package io.intercom.android.sdk.tickets;

import io.intercom.android.sdk.models.Ticket$ConversationButton$IconType;
import kotlin.Metadata;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class TicketDetailReducerKt$WhenMappings {
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$0;

   static {
      int[] var0 = new int[Ticket$ConversationButton$IconType.values().length];

      try {
         var0[Ticket$ConversationButton$IconType.SEND.ordinal()] = 1;
      } catch (NoSuchFieldError var3) {
      }

      try {
         var0[Ticket$ConversationButton$IconType.CONVERSATION.ordinal()] = 2;
      } catch (NoSuchFieldError var2) {
      }

      $EnumSwitchMapping$0 = var0;
   }
}
