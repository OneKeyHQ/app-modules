package io.intercom.android.sdk.views;

import android.view.View;
import androidx.core.content.ContextCompat;
import io.intercom.android.sdk.R.color;
import io.intercom.android.sdk.identity.AppConfig;
import io.intercom.android.sdk.utilities.BackgroundUtils;

public class ActiveStatePresenter {
   private static final String ENGLISH_LOCALE = "en";

   public void presentStateDot(boolean var1, View var2, AppConfig var3) {
      if (!"en".equals(var3.getLocale())) {
         var2.setBackgroundResource(0);
      } else {
         int var4 = ContextCompat.getColor(var2.getContext(), var1 ? color.intercom_active_state : color.intercom_away_state);
         int var5 = ContextCompat.getColor(var2.getContext(), color.intercom_white);
         float var6 = 1.0F * var2.getResources().getDisplayMetrics().density;
         BackgroundUtils.setBackground(var2, new ActiveStateDrawable(var4, var5, var6));
      }
   }
}
