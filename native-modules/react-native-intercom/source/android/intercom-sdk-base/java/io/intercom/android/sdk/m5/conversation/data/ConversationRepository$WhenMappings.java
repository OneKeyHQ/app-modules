package io.intercom.android.sdk.m5.conversation.data;

import kotlin.Metadata;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class ConversationRepository$WhenMappings {
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$0;

   static {
      int[] var0 = new int[GetConversationReason.values().length];

      try {
         var0[GetConversationReason.NEW_COMMENT.ordinal()] = 1;
      } catch (NoSuchFieldError var6) {
      }

      try {
         var0[GetConversationReason.NEXUS_CONNECTED.ordinal()] = 2;
      } catch (NoSuchFieldError var5) {
      }

      try {
         var0[GetConversationReason.NETWORK_CONNECTED.ordinal()] = 3;
      } catch (NoSuchFieldError var4) {
      }

      try {
         var0[GetConversationReason.OPEN_CONVERSATION.ordinal()] = 4;
      } catch (NoSuchFieldError var3) {
      }

      try {
         var0[GetConversationReason.POLLING.ordinal()] = 5;
      } catch (NoSuchFieldError var2) {
      }

      $EnumSwitchMapping$0 = var0;
   }
}
