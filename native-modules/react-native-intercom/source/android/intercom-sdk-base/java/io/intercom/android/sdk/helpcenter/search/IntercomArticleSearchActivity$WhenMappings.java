package io.intercom.android.sdk.helpcenter.search;

import io.intercom.android.sdk.ui.theme.ThemeMode;
import kotlin.Metadata;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class IntercomArticleSearchActivity$WhenMappings {
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$0;

   static {
      int[] var0 = new int[ThemeMode.values().length];

      try {
         var0[ThemeMode.DARK.ordinal()] = 1;
      } catch (NoSuchFieldError var4) {
      }

      try {
         var0[ThemeMode.LIGHT.ordinal()] = 2;
      } catch (NoSuchFieldError var3) {
      }

      try {
         var0[ThemeMode.SYSTEM.ordinal()] = 3;
      } catch (NoSuchFieldError var2) {
      }

      $EnumSwitchMapping$0 = var0;
   }
}
