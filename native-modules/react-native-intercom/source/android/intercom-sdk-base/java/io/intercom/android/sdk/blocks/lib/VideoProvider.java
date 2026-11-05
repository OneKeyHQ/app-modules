package io.intercom.android.sdk.blocks.lib;

import java.util.Locale;

public enum VideoProvider {
   YOUTUBE,
   VIMEO,
   WISTIA,
   LOOM,
   VIDYARD,
   BRIGHTCOVE,
   JWPLAYER,
   MICROSOFTSTREAM,
   SYNTHESIA,
   GUIDDE,
   DESCRIPT,
   UNKNOWN;

   public static VideoProvider videoValueOf(String var0) {
      VideoProvider var1 = UNKNOWN;

      try {
         var1 = valueOf(var0.toUpperCase(Locale.ENGLISH));
      } catch (IllegalArgumentException var3) {
      }

      return var1;
   }

   // $VF: synthetic method
   private static VideoProvider[] $values() {
      return new VideoProvider[]{YOUTUBE, VIMEO, WISTIA, LOOM, VIDYARD, BRIGHTCOVE, JWPLAYER, MICROSOFTSTREAM, SYNTHESIA, GUIDDE, DESCRIPT, UNKNOWN};
   }
}
