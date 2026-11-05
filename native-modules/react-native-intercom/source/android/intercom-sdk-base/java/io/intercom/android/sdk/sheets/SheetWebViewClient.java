package io.intercom.android.sdk.sheets;

import android.net.Uri;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.utilities.LinkOpener;

class SheetWebViewClient extends WebViewClient {
   private final SheetListener sheetListener;

   SheetWebViewClient(SheetListener var1) {
      this.sheetListener = var1;
   }

   @Override
   public void onPageFinished(WebView var1, String var2) {
      this.sheetListener.onWebViewFinishedLoad();
   }

   @Override
   public boolean shouldOverrideUrlLoading(WebView var1, String var2) {
      Uri var3 = Uri.parse(var2);
      String var4 = var3.getScheme();
      if (!var4.equals("http") && !var4.equals("https")) {
         LinkOpener.handleUrl(var2, var1.getContext(), Injector.get().getApi());
         return true;
      } else {
         var1.loadUrl(var2);
         return false;
      }
   }

   @Override
   public boolean shouldOverrideUrlLoading(WebView var1, WebResourceRequest var2) {
      return this.shouldOverrideUrlLoading(var1, var2.getUrl().toString());
   }
}
