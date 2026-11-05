package io.intercom.android.sdk.activities;

import android.view.View;
import android.view.View.OnClickListener;
import io.intercom.android.sdk.Injector;

class IntercomSheetActivity$3 implements OnClickListener {
   // $VF: synthetic field
   final IntercomSheetActivity this$0;

   IntercomSheetActivity$3(IntercomSheetActivity var1) {
      this.this$0 = var1;
   }

   @Override
   public void onClick(View var1) {
      IntercomSheetActivity.access$100(this.this$0, Injector.get().getApi());
   }
}
