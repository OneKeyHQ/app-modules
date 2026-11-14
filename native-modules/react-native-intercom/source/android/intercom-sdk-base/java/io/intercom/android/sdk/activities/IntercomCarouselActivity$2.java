package io.intercom.android.sdk.activities;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

class IntercomCarouselActivity$2 implements OnClickListener {
   // $VF: synthetic field
   final IntercomCarouselActivity this$0;

   IntercomCarouselActivity$2(IntercomCarouselActivity var1) {
      this.this$0 = var1;
   }

   @Override
   public void onClick(DialogInterface var1, int var2) {
      this.this$0.permissionManager.requestPermissions(new String[]{"android.permission.ACCESS_BACKGROUND_LOCATION"}, 12306);
   }
}
