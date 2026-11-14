package io.intercom.android.sdk.blocks.messengercard;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import java.util.Map;

class MessengerCardAlertDialogBuilder$1 implements OnClickListener {
   // $VF: synthetic field
   final WebView val$webView;
   // $VF: synthetic field
   final AutoCompleteTextView val$inputView;
   // $VF: synthetic field
   final Map val$payload;
   // $VF: synthetic field
   final MessengerCardAlertDialogBuilder this$0;

   MessengerCardAlertDialogBuilder$1(MessengerCardAlertDialogBuilder var1, WebView var2, AutoCompleteTextView var3, Map var4) {
      this.this$0 = var1;
      this.val$webView = var2;
      this.val$inputView = var3;
      this.val$payload = var4;
   }

   @Override
   public void onClick(DialogInterface var1, int var2) {
      this.this$0.sendResultToWebview(this.val$webView, this.val$inputView.getText().toString(), this.val$payload);
      InputMethodManager var3 = (InputMethodManager)this.val$inputView.getContext().getSystemService("input_method");
      var3.hideSoftInputFromWindow(this.val$inputView.getWindowToken(), 0);
      var1.dismiss();
   }
}
