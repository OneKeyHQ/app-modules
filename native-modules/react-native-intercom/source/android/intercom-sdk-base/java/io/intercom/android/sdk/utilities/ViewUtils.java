package io.intercom.android.sdk.utilities;

import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

public class ViewUtils {
   public static void removeGlobalLayoutListener(View var0, OnGlobalLayoutListener var1) {
      var0.getViewTreeObserver().removeOnGlobalLayoutListener(var1);
   }

   public static void waitForViewAttachment(View var0, Runnable var1) {
      if (var0.getHeight() == 0) {
         var0.getViewTreeObserver().addOnGlobalLayoutListener(new ViewUtils$1(var0, var1));
      } else {
         var1.run();
      }
   }
}
