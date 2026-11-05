package io.intercom.android.sdk.blocks;

import io.intercom.android.sdk.blocks.lib.VideoProvider;
import kotlin.Metadata;

// $VF: synthetic class
@Metadata(mv = {2, 0, 0}, k = 3, xi = 48)
public final class VideoUrlUtilKt$WhenMappings {
   // $VF: synthetic field
   public static final int[] $EnumSwitchMapping$0;

   static {
      int[] var0 = new int[VideoProvider.values().length];

      try {
         var0[VideoProvider.YOUTUBE.ordinal()] = 1;
      } catch (NoSuchFieldError var13) {
      }

      try {
         var0[VideoProvider.VIMEO.ordinal()] = 2;
      } catch (NoSuchFieldError var12) {
      }

      try {
         var0[VideoProvider.WISTIA.ordinal()] = 3;
      } catch (NoSuchFieldError var11) {
      }

      try {
         var0[VideoProvider.LOOM.ordinal()] = 4;
      } catch (NoSuchFieldError var10) {
      }

      try {
         var0[VideoProvider.VIDYARD.ordinal()] = 5;
      } catch (NoSuchFieldError var9) {
      }

      try {
         var0[VideoProvider.BRIGHTCOVE.ordinal()] = 6;
      } catch (NoSuchFieldError var8) {
      }

      try {
         var0[VideoProvider.JWPLAYER.ordinal()] = 7;
      } catch (NoSuchFieldError var7) {
      }

      try {
         var0[VideoProvider.MICROSOFTSTREAM.ordinal()] = 8;
      } catch (NoSuchFieldError var6) {
      }

      try {
         var0[VideoProvider.SYNTHESIA.ordinal()] = 9;
      } catch (NoSuchFieldError var5) {
      }

      try {
         var0[VideoProvider.GUIDDE.ordinal()] = 10;
      } catch (NoSuchFieldError var4) {
      }

      try {
         var0[VideoProvider.DESCRIPT.ordinal()] = 11;
      } catch (NoSuchFieldError var3) {
      }

      try {
         var0[VideoProvider.UNKNOWN.ordinal()] = 12;
      } catch (NoSuchFieldError var2) {
      }

      $EnumSwitchMapping$0 = var0;
   }
}
