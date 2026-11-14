package io.intercom.android.sdk.blocks.messengercard;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;

class MessengerCardAlertDialogBuilder$2 implements OnClickListener {
   // $VF: synthetic field
   final AutoCompleteTextView val$inputView;
   // $VF: synthetic field
   final MessengerCardAlertDialogBuilder this$0;

   MessengerCardAlertDialogBuilder$2(MessengerCardAlertDialogBuilder var1, AutoCompleteTextView var2) {
      this.this$0 = var1;
      this.val$inputView = var2;
   }

   @Override
   public void onClick(DialogInterface var1, int var2) {
      InputMethodManager var3 = (InputMethodManager)this.val$inputView.getContext().getSystemService("input_method");
      var3.hideSoftInputFromWindow(this.val$inputView.getWindowToken(), 0);
      var1.cancel();
   }
}
