package io.intercom.android.sdk.logger;

import android.annotation.SuppressLint;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.Intercom$LogLevel;

public class LumberMill {
   private static final Twig MESSENGER_TWIG = new Twig(5, "Intercom", false);
   private static final Twig NEXUS_TWIG = new Twig(5, "Intercom realtime", false);
   private static final Twig ABLY_TWIG = new Twig(5, "Intercom Ably", false);
   private static final Twig BLOCK_TWIG = new Twig(5, "Intercom", false);

   public static Twig getLogger() {
      return MESSENGER_TWIG;
   }

   public static Twig getNexusTwig() {
      return NEXUS_TWIG;
   }

   public static Twig getAblyTwig() {
      return ABLY_TWIG;
   }

   public static Twig getBlocksTwig() {
      return BLOCK_TWIG;
   }

   @SuppressLint("WrongConstant")
   public static void setLogLevel(@Intercom$LogLevel int var0) {
      MESSENGER_TWIG.setLogLevel(var0);
      NEXUS_TWIG.setLogLevel(var0);
      ABLY_TWIG.setLogLevel(var0);
      BLOCK_TWIG.setLogLevel(var0);
   }
}
