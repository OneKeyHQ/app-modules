package io.intercom.android.sdk.blocks.messengercard;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.net.Uri;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.widget.FrameLayout.LayoutParams;
import androidx.annotation.ColorInt;
import com.google.gson.Gson;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.metrics.MetricTracker;
import java.io.File;

public class MessengerCardWebViewPresenter {
   private final CardWebView webView;
   private final View loadingView;
   private final String fallbackUrl;
   @ColorInt
   private final int primaryColor;
   private final Gson gson;
   private final MetricTracker metricTracker;
   private final String conversationId;
   private final File cache;
   private final boolean isHomeScreen;
   private final Context context;

   public MessengerCardWebViewPresenter(
      CardWebView var1, View var2, String var3, @ColorInt int var4, Gson var5, MetricTracker var6, File var7, String var8, boolean var9, Context var10
   ) {
      this.webView = var1;
      this.loadingView = var2;
      this.fallbackUrl = var3;
      this.primaryColor = var4;
      this.gson = var5;
      this.metricTracker = var6;
      this.conversationId = var8;
      this.cache = var7;
      this.isHomeScreen = var9;
      this.context = var10;
   }

   @SuppressLint("SetJavaScriptEnabled")
   public void setUpWebView() {
      this.webView.setUp();
      this.webView.setBackgroundColor(0);
      this.webView.setWebViewClient(new MessengerCardWebViewClient(Uri.parse(this.fallbackUrl).getHost()));
      WebSettings var1 = this.webView.getSettings();
      var1.setJavaScriptEnabled(true);
      var1.setUseWideViewPort(true);
      var1.setCacheMode(-1);
      var1.setMixedContentMode(0);
      this.webView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
      this.webView.setVerticalScrollBarEnabled(false);
      this.webView.setHorizontalScrollBarEnabled(false);
      MessengerCardWebViewInterface var2 = new MessengerCardWebViewInterface(
         this.webView, this.loadingView, this.gson, this.primaryColor, this.metricTracker, this.conversationId, this.isHomeScreen, this.context
      );
      this.webView.addJavascriptInterface(var2, "AndroidHost");
   }

   public static CardWebView createCardWebView(Context var0) {
      CardWebView var1;
      try {
         var1 = new CardWebView(var0);
      } catch (NotFoundException var3) {
         var1 = new CardWebView(Injector.get().getApplication());
      }

      var1.setAlpha(0.0F);
      var1.setId(id.intercom_messenger_card_webview);
      var1.setLayoutParams(new LayoutParams(-2, -2));
      return var1;
   }
}
