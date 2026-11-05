package io.intercom.android.sdk.m5.home.ui.components;

import io.intercom.android.sdk.m5.home.data.SpaceItemType;
import kotlin.Metadata;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class SpacesCardKt$SpacesCard$1$WhenMappings {
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$0;

   static {
      int[] var0 = new int[SpaceItemType.values().length];

      try {
         var0[SpaceItemType.MESSAGES.ordinal()] = 1;
      } catch (NoSuchFieldError var4) {
      }

      try {
         var0[SpaceItemType.HELP.ordinal()] = 2;
      } catch (NoSuchFieldError var3) {
      }

      try {
         var0[SpaceItemType.TICKETS.ordinal()] = 3;
      } catch (NoSuchFieldError var2) {
      }

      $EnumSwitchMapping$0 = var0;
   }
}
