package io.intercom.android.sdk.m5.conversation.usecase;

import kotlin.Metadata;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class SoundEffectsUseCase$WhenMappings {
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$0;

   static {
      int[] var0 = new int[Action.values().length];

      try {
         var0[Action.MESSAGE_SENT.ordinal()] = 1;
      } catch (NoSuchFieldError var5) {
      }

      try {
         var0[Action.ADMIN_MESSAGE_RECEIVED.ordinal()] = 2;
      } catch (NoSuchFieldError var4) {
      }

      try {
         var0[Action.OPERATOR_MESSAGE_RECEIVED.ordinal()] = 3;
      } catch (NoSuchFieldError var3) {
      }

      try {
         var0[Action.MESSAGE_FAILED.ordinal()] = 4;
      } catch (NoSuchFieldError var2) {
      }

      $EnumSwitchMapping$0 = var0;
   }
}
