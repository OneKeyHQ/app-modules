package io.intercom.android.sdk.m5.conversation.data;

import io.intercom.android.nexus.NexusEventType;
import kotlin.Metadata;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class NexusEventAsFlowKt$WhenMappings {
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$0;

   static {
      int[] var0 = new int[NexusEventType.values().length];

      try {
         var0[NexusEventType.AdminIsTyping.ordinal()] = 1;
      } catch (NoSuchFieldError var5) {
      }

      try {
         var0[NexusEventType.UserContentSeenByAdmin.ordinal()] = 2;
      } catch (NoSuchFieldError var4) {
      }

      try {
         var0[NexusEventType.NewComment.ordinal()] = 3;
      } catch (NoSuchFieldError var3) {
      }

      try {
         var0[NexusEventType.ConversationPartToken.ordinal()] = 4;
      } catch (NoSuchFieldError var2) {
      }

      $EnumSwitchMapping$0 = var0;
   }
}
