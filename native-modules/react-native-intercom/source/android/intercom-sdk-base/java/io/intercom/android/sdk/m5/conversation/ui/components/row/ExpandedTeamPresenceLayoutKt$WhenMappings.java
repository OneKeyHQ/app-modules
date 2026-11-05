package io.intercom.android.sdk.m5.conversation.ui.components.row;

import io.intercom.android.sdk.models.AvatarType;
import io.intercom.android.sdk.models.Header$Expanded$Style;
import kotlin.Metadata;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class ExpandedTeamPresenceLayoutKt$WhenMappings {
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$0;
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$1;

   static {
      int[] var0 = new int[AvatarType.values().length];

      try {
         var0[AvatarType.FACEPILE.ordinal()] = 1;
      } catch (NoSuchFieldError var9) {
      }

      try {
         var0[AvatarType.LAYERED_BUBBLES.ordinal()] = 2;
      } catch (NoSuchFieldError var8) {
      }

      try {
         var0[AvatarType.LOGO.ordinal()] = 3;
      } catch (NoSuchFieldError var7) {
      }

      try {
         var0[AvatarType.UNKNOWN.ordinal()] = 4;
      } catch (NoSuchFieldError var6) {
      }

      $EnumSwitchMapping$0 = var0;
      var0 = new int[Header$Expanded$Style.values().length];

      try {
         var0[Header$Expanded$Style.H1.ordinal()] = 1;
      } catch (NoSuchFieldError var5) {
      }

      try {
         var0[Header$Expanded$Style.PARAGRAPH.ordinal()] = 2;
      } catch (NoSuchFieldError var4) {
      }

      try {
         var0[Header$Expanded$Style.INTRO.ordinal()] = 3;
      } catch (NoSuchFieldError var3) {
      }

      try {
         var0[Header$Expanded$Style.GREETING.ordinal()] = 4;
      } catch (NoSuchFieldError var2) {
      }

      $EnumSwitchMapping$1 = var0;
   }
}
