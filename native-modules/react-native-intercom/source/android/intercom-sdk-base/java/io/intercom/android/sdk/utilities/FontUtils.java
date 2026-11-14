package io.intercom.android.sdk.utilities;

import android.graphics.Typeface;
import android.widget.TextView;

public class FontUtils {
   public static void setRobotoMediumTypeface(TextView var0) {
      var0.setTypeface(Typeface.create("sans-serif-medium", 0));
   }

   public static void setRobotoLightTypeface(TextView var0) {
      var0.setTypeface(Typeface.create("sans-serif-light", 0));
   }
}
