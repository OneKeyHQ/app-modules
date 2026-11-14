package io.intercom.android.sdk.activities;

import android.net.Uri;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebChromeClient.FileChooserParams;

class IntercomSheetActivity$5 extends WebChromeClient {
   // $VF: synthetic field
   final IntercomSheetActivity this$0;

   IntercomSheetActivity$5(IntercomSheetActivity var1) {
      this.this$0 = var1;
   }

   @Override
   public void onProgressChanged(WebView var1, int var2) {
      this.this$0.loadingBar.setProgress(var2);
   }

   @Override
   public boolean onShowFileChooser(WebView var1, ValueCallback var2, FileChooserParams var3) {
      this.this$0.valueCallback = var2;
      IntercomSheetActivity.access$900(this.this$0).launch(var3.createIntent());
      return true;
   }
}
