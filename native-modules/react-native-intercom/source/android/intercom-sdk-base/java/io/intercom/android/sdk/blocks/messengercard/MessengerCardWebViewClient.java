package io.intercom.android.sdk.blocks.messengercard;

import android.net.Uri;
import android.text.TextUtils;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.utilities.LinkOpener;

public class MessengerCardWebViewClient extends WebViewClient {
   private static final int WEBVIEW_FADE_IN_TIME_MS = 150;
   private final String cardHost;

   public MessengerCardWebViewClient(String var1) {
      this.cardHost = var1;
   }

   @Override
   public void onPageFinished(WebView var1, String var2) {
      var1.animate().alpha(1.0F).setStartDelay(150L).setDuration(150L).start();
   }

   @Override
   public boolean shouldOverrideUrlLoading(WebView var1, String var2) {
      Uri var3 = Uri.parse(var2);
      String var4 = var3.getHost();
      if (!TextUtils.isEmpty(this.cardHost) && this.cardHost.equals(var4)) {
         var1.loadUrl(var2);
         return false;
      } else {
         LinkOpener.handleUrl(var2, var1.getContext(), Injector.get().getApi());
         return true;
      }
   }

   @Override
   public boolean shouldOverrideUrlLoading(WebView var1, WebResourceRequest var2) {
      return this.shouldOverrideUrlLoading(var1, var2.getUrl().toString());
   }
}
