package io.intercom.android.sdk.sheets;

import android.annotation.SuppressLint;
import android.webkit.WebSettings;
import android.webkit.WebView;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.conversation.JavascriptRunner;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class SheetWebViewPresenter {
   private final WebView webView;
   private final JavascriptRunner jsRunner;
   private final String sheetUrl;
   private final SheetListener listener;
   private final Injector injector;

   public SheetWebViewPresenter(WebView var1, JavascriptRunner var2, String var3, SheetListener var4, Injector var5) {
      this.webView = var1;
      this.jsRunner = var2;
      this.sheetUrl = var3;
      this.listener = var4;
      this.injector = var5;
   }

   @SuppressLint("SetJavaScriptEnabled")
   public void setUpWebView() {
      this.webView.setVerticalScrollBarEnabled(false);
      WebSettings var1 = this.webView.getSettings();
      var1.setJavaScriptEnabled(true);
      var1.setUserAgentString("IntercomMobileWebView");
      var1.setLoadWithOverviewMode(true);
      var1.setUseWideViewPort(true);
      var1.setDomStorageEnabled(true);
      var1.setCacheMode(1);
      SheetWebViewInterface var2 = new SheetWebViewInterface(
         this.webView, this.injector.getGson(), this.injector.getMetricTracker(), this.injector.getApi(), this.listener
      );
      this.webView.addJavascriptInterface(var2, "AndroidHost");
      this.webView.setWebViewClient(new SheetWebViewClient(this.listener));
   }

   public void loadBundle(String var1) {
      this.jsRunner.reset();

      String var2;
      try {
         var2 = "intercom_data=" + URLEncoder.encode(var1, "UTF-8");
      } catch (UnsupportedEncodingException var4) {
         return;
      }

      this.webView.postUrl(this.sheetUrl, var2.getBytes());
   }
}
