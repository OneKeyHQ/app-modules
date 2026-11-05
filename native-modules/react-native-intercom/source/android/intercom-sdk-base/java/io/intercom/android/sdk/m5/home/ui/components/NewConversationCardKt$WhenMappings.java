package io.intercom.android.sdk.m5.home.ui.components;

import io.intercom.android.sdk.m5.home.data.IconType;
import io.intercom.android.sdk.models.OpenMessengerResponse$IconType;
import kotlin.Metadata;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class NewConversationCardKt$WhenMappings {
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$0;
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$1;

   static {
      int[] var0 = new int[IconType.values().length];

      try {
         var0[IconType.TEAMMATE.ordinal()] = 1;
      } catch (NoSuchFieldError var5) {
      }

      try {
         var0[IconType.BOT.ordinal()] = 2;
      } catch (NoSuchFieldError var4) {
      }

      try {
         var0[IconType.FIN.ordinal()] = 3;
      } catch (NoSuchFieldError var3) {
      }

      $EnumSwitchMapping$0 = var0;
      var0 = new int[OpenMessengerResponse$IconType.values().length];

      try {
         var0[OpenMessengerResponse$IconType.PAPER_PLANE.ordinal()] = 1;
      } catch (NoSuchFieldError var2) {
      }

      $EnumSwitchMapping$1 = var0;
   }
}
