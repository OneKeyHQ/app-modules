package io.intercom.android.sdk.utilities;

import android.content.Context;
import android.provider.Settings.Global;
import com.intercom.twig.Twig;
import io.intercom.android.sdk.logger.LumberMill;

public class SystemSettings {
   private static final Twig twig = LumberMill.getLogger();

   public static float getTransitionScale(Context var0) {
      try {
         return Global.getFloat(var0.getContentResolver(), "transition_animation_scale");
      } catch (Exception var2) {
         twig.internal("Couldn't get animation scale: " + var2.getMessage());
         return 1.0F;
      }
   }
}
