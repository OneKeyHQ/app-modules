package io.intercom.android.sdk.articles;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.util.TypedValue;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.utilities.LinkOpener;
import org.json.JSONObject;

public class ArticleWebViewClient extends WebViewClient {
   private final String articleUrl;
   private final ArticleWebViewListener articleWebViewListener;
   private boolean isLoadError;
   private boolean isArticleNotFound = false;

   public ArticleWebViewClient(String var1, ArticleWebViewListener var2) {
      this.articleUrl = var1;
      this.articleWebViewListener = var2;
   }

   @Override
   public void onReceivedHttpError(WebView var1, WebResourceRequest var2, WebResourceResponse var3) {
      super.onReceivedHttpError(var1, var2, var3);
      if (var2.isForMainFrame()) {
         if (var3.getStatusCode() == 404 || var3.getStatusCode() == 401) {
            this.isLoadError = true;
            this.isArticleNotFound = true;
            this.articleWebViewListener.articleNotFound();
         }
      }
   }

   @Override
   public void onPageFinished(WebView var1, String var2) {
      super.onPageFinished(var1, var2);
      if (!this.isLoadError && !this.isArticleNotFound) {
         this.scrollToAnchorIfNeeded(var1, var2);
         this.articleWebViewListener.onArticleFinishedLoading();
      }
   }

   @Override
   public void onPageStarted(WebView var1, String var2, Bitmap var3) {
      super.onPageStarted(var1, var2, var3);
      if (!this.isArticleNotFound) {
         this.isLoadError = false;
         this.articleWebViewListener.onArticleStartedLoading();
      }
   }

   @Override
   public void onReceivedError(WebView var1, WebResourceRequest var2, WebResourceError var3) {
      super.onReceivedError(var1, var2, var3);
      if (var2.isForMainFrame()) {
         this.isLoadError = true;
         this.articleWebViewListener.onArticleLoadingError();
      }
   }

   @Override
   public boolean shouldOverrideUrlLoading(WebView var1, String var2) {
      Uri var3 = Uri.parse(var2);
      Uri var4 = Uri.parse(this.articleUrl);
      String var5 = var3.getLastPathSegment();
      String var6 = var4.getLastPathSegment();
      String var7 = this.extractArticleId(var5);
      String var8 = this.extractArticleId(var6);
      if (!TextUtils.isEmpty(var7) && !TextUtils.isEmpty(var8) && var7.equals(var8)) {
         return false;
      }

      LinkOpener.handleUrl(var2, var1.getContext(), Injector.get().getApi());
      return true;
   }

   private String extractArticleId(String var1) {
      if (TextUtils.isEmpty(var1)) {
         return null;
      }

      int var2 = var1.indexOf(45);
      return var2 > 0 ? var1.substring(0, var2) : var1;
   }

   @Override
   public boolean shouldOverrideUrlLoading(WebView var1, WebResourceRequest var2) {
      return this.shouldOverrideUrlLoading(var1, var2.getUrl().toString());
   }

   private void scrollToAnchorIfNeeded(WebView var1, String var2) {
      String var3 = Uri.parse(var2).getFragment();
      if (var3 != null) {
         var1.evaluateJavascript("document.getElementById(" + JSONObject.quote(var3) + ").offsetTop", (android.webkit.ValueCallback<String>)(var2x -> {
            try {
               int scrollPixels = (int)this.getDensityIndependentPixels(Integer.parseInt(var2x), var1.getContext());
               this.articleWebViewListener.scrollArticleViewTo(scrollPixels);
            } catch (NumberFormatException var4) {
            }
         }));
      }
   }

   private float getDensityIndependentPixels(int var1, Context var2) {
      return TypedValue.applyDimension(1, var1, var2.getResources().getDisplayMetrics());
   }
}
