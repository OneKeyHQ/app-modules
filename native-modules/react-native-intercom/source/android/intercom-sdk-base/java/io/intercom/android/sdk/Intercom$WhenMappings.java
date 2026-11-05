package io.intercom.android.sdk;

import kotlin.Metadata;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class Intercom$WhenMappings {
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$0;
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$1;

   static {
      int[] var0 = new int[IntercomPushManager$IntercomPushIntegrationType.values().length];

      try {
         var0[IntercomPushManager$IntercomPushIntegrationType.FCM.ordinal()] = 1;
      } catch (NoSuchFieldError var6) {
      }

      $EnumSwitchMapping$0 = var0;
      var0 = new int[IntercomSpace.values().length];

      try {
         var0[IntercomSpace.HelpCenter.ordinal()] = 1;
      } catch (NoSuchFieldError var5) {
      }

      try {
         var0[IntercomSpace.Home.ordinal()] = 2;
      } catch (NoSuchFieldError var4) {
      }

      try {
         var0[IntercomSpace.Messages.ordinal()] = 3;
      } catch (NoSuchFieldError var3) {
      }

      try {
         var0[IntercomSpace.Tickets.ordinal()] = 4;
      } catch (NoSuchFieldError var2) {
      }

      $EnumSwitchMapping$1 = var0;
   }
}
