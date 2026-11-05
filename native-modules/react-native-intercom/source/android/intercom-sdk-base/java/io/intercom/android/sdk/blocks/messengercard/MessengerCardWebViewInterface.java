package io.intercom.android.sdk.blocks.messengercard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import com.google.gson.Gson;
import io.intercom.android.sdk.activities.IntercomSheetActivity;
import io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity;
import io.intercom.android.sdk.helpcenter.articles.IntercomArticleActivity$ArticleActivityArguments;
import io.intercom.android.sdk.metrics.MetricTracker;
import java.util.HashMap;
import java.util.Map;

class MessengerCardWebViewInterface {
   private static final String OPEN_URL = "OPEN_URL";
   private static final String OPEN_SHEET = "OPEN_SHEET";
   private static final String INPUT_TEXT = "INPUT_TEXT";
   private static final String METRIC_EVENT = "METRIC_EVENT";
   private static final String HEIGHT_CHANGE_EVENT = "HEIGHT_CHANGE_EVENT";
   private static final Double MESSENGER_APP_ID_ARTICLE_SEARCH = 27.0;
   private final WebView webView;
   private final View loadingView;
   private final Gson gson;
   private final int primaryColor;
   private final MetricTracker metricTracker;
   private final String conversationId;
   private final Boolean isHomescreen;
   private final Context context;

   MessengerCardWebViewInterface(WebView var1, View var2, Gson var3, @ColorInt int var4, MetricTracker var5, String var6, boolean var7, Context var8) {
      this.webView = var1;
      this.loadingView = var2;
      this.gson = var3;
      this.primaryColor = var4;
      this.metricTracker = var5;
      this.conversationId = var6;
      this.isHomescreen = var7;
      this.context = var8;
   }

   @JavascriptInterface
   public void handleAction(String var1) {
      MessengerCardWebViewAction var2 = (MessengerCardWebViewAction)this.gson.fromJson(var1, MessengerCardWebViewAction.class);
      Map var3 = var2.getPayload();
      String var4 = var2.getType();
      if (var4 != null && var3 != null) {
         switch (var4) {
            case "OPEN_URL":
               this.webView.post(new MessengerCardWebViewInterface$1(this, var3));
               break;
            case "OPEN_SHEET":
               this.webView.post(new MessengerCardWebViewInterface$2(this, var3));
               break;
            case "INPUT_TEXT":
               this.webView.post(new MessengerCardWebViewInterface$3(this, var3));
               break;
            case "HEIGHT_CHANGE_EVENT":
               if (this.loadingView.getVisibility() == 0) {
                  this.webView.post(new MessengerCardWebViewInterface$4(this));
               }
               break;
            case "METRIC_EVENT":
               this.webView.post(new MessengerCardWebViewInterface$5(this, var2));
         }
      }
   }

   private void openUrl(Map var1, Context var2) {
      String var3 = (String)var1.get("url");
      var2.startActivity(new Intent("android.intent.action.VIEW", Uri.parse(var3)));
   }

   private void openSheet(Map var1, Context var2) {
      String var3 = (String)var1.get("url");
      Map var4 = var1.containsKey("params") ? (Map)var1.get("params") : new HashMap();
      String var5 = this.getStringFromMap(var4, "uri");
      if (!TextUtils.isEmpty(var3) && !TextUtils.isEmpty(var5)) {
         String var6 = this.getStringFromMap(var4, "action_id");
         if ("article-link".equals(var6) && var4.containsKey("values")) {
            Map var7 = (Map<String, Object>)var4.get("values");
            String var8 = this.getStringFromMap(var7, "article_id");
            if (!TextUtils.isEmpty(var8)) {
               this.displayArticle(var8, var2);
               return;
            }
         }

         Intent var9 = IntercomSheetActivity.buildIntent(var2, var3, var4, var5, this.conversationId);
         var2.startActivity(var9);
      }
   }

   private String getStringFromMap(Map var1, String var2) {
      return var1.containsKey(var2) ? String.valueOf(var1.get(var2)) : "";
   }

   private void displayArticle(String var1, Context var2) {
      String var3;
      if (this.isHomescreen) {
         var3 = "messenger_home_app";
      } else {
         var3 = "conversation";
      }

      Intent var4 = IntercomArticleActivity.buildIntent(var2, new IntercomArticleActivity$ArticleActivityArguments(var1, var3, false, !this.isHomescreen));
      var2.startActivity(var4);
   }

   private void showTextInputDialog(Map var1, Context var2) {
      if (var2 instanceof Activity) {
         Activity var3 = (Activity)var2;
         if (var3.isFinishing() || var3.isDestroyed()) {
            return;
         }
      }

      MessengerCardAlertDialogBuilder var5 = new MessengerCardAlertDialogBuilder(var2, var1, this.primaryColor, this.webView);
      AlertDialog var4 = var5.create();
      var4.show();
      var4.getWindow().setSoftInputMode(5);
      var4.getButton(-2).setTextColor(this.primaryColor);
      var4.getButton(-1).setTextColor(this.primaryColor);
   }

   // $VF: synthetic method
   static Context access$000(MessengerCardWebViewInterface var0) {
      return var0.context;
   }

   // $VF: synthetic method
   static void access$100(MessengerCardWebViewInterface var0, Map var1, Context var2) {
      var0.openUrl(var1, var2);
   }

   // $VF: synthetic method
   static void access$200(MessengerCardWebViewInterface var0, Map var1, Context var2) {
      var0.openSheet(var1, var2);
   }

   // $VF: synthetic method
   static Double access$300() {
      return MESSENGER_APP_ID_ARTICLE_SEARCH;
   }

   // $VF: synthetic method
   static WebView access$400(MessengerCardWebViewInterface var0) {
      return var0.webView;
   }

   // $VF: synthetic method
   static void access$500(MessengerCardWebViewInterface var0, Map var1, Context var2) {
      var0.showTextInputDialog(var1, var2);
   }

   // $VF: synthetic method
   static View access$600(MessengerCardWebViewInterface var0) {
      return var0.loadingView;
   }

   // $VF: synthetic method
   static Boolean access$700(MessengerCardWebViewInterface var0) {
      return var0.isHomescreen;
   }

   // $VF: synthetic method
   static MetricTracker access$800(MessengerCardWebViewInterface var0) {
      return var0.metricTracker;
   }
}
