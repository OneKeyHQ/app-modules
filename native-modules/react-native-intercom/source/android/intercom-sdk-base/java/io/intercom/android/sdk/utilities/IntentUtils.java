package io.intercom.android.sdk.utilities;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Build.VERSION;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.logger.LumberMill;

public class IntentUtils {
   private static final Twig twig = LumberMill.getLogger();

   public static void safelyOpenIntent(Context var0, Intent var1) {
      if (VERSION.SDK_INT >= 30) {
         var1.addCategory("android.intent.category.BROWSABLE");

         try {
            var0.startActivity(var1);
         } catch (ActivityNotFoundException var3) {
            twig.i("This device has no application that can handle the Uri passed in");
         }
      } else {
         ResolveInfo var2 = var0.getPackageManager().resolveActivity(var1, 65536);
         if (var2 == null) {
            twig.i("This device has no application that can handle the Uri passed in");
         } else {
            var0.startActivity(var1);
         }
      }
   }
}
