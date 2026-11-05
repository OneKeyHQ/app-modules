package io.intercom.android.sdk.activities;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.util.Map;

class IntercomSheetActivity$8 implements OnClickListener {
   // $VF: synthetic field
   final Map val$payload;
   // $VF: synthetic field
   final IntercomSheetActivity this$0;

   IntercomSheetActivity$8(IntercomSheetActivity var1, Map var2) {
      this.this$0 = var1;
      this.val$payload = var2;
   }

   @Override
   public void onClick(DialogInterface var1, int var2) {
      this.this$0.onSubmitSheetAction(this.val$payload);
   }
}
