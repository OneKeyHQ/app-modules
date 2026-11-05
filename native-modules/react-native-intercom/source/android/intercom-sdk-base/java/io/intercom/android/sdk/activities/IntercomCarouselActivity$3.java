package io.intercom.android.sdk.activities;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

class IntercomCarouselActivity$3 implements OnClickListener {
   // $VF: synthetic field
   final IntercomCarouselActivity this$0;

   IntercomCarouselActivity$3(IntercomCarouselActivity var1) {
      this.this$0 = var1;
   }

   @Override
   public void onClick(DialogInterface var1, int var2) {
      this.this$0.goToSettings();
   }
}
