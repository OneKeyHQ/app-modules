package io.intercom.android.sdk.sheets;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import com.google.gson.Gson;
import io.intercom.android.sdk.api.Api;
import io.intercom.android.sdk.helpcenter.webview.HelpCenterWebViewInterface;
import io.intercom.android.sdk.metrics.MetricTracker;
import java.util.Map;

public class SheetWebViewInterface {
   private static final String SHEET_TITLE = "set-title";
   private static final String CLOSE_SHEET = "close";
   private static final String SUBMIT_SHEET = "submit-sheet";
   private static final String PAYLOAD_SHEET_TITLE = "title";
   private final WebView webView;
   private final MetricTracker metricTracker;
   private final Api api;
   private final Gson gson;
   private final SheetListener listener;

   SheetWebViewInterface(WebView var1, Gson var2, MetricTracker var3, Api var4, SheetListener var5) {
      this.webView = var1;
      this.gson = var2;
      this.listener = var5;
      this.metricTracker = var3;
      this.api = var4;
   }

   @JavascriptInterface
   public void handleAction(String var1) {
      SheetWebViewAction var2 = (SheetWebViewAction)this.gson.fromJson(var1, SheetWebViewAction.class);
      String var3 = var2.getType();
      Map var4 = var2.getPayload();
      if (this.isHelpCenterMetric(var3)) {
         HelpCenterWebViewInterface var7 = new HelpCenterWebViewInterface(this.webView, this.gson, this.metricTracker, this.api, false);
         var7.handleAction(var1);
      } else if (var3 != null && var4 != null) {
         switch (var3) {
            case "set-title":
               this.webView.post(new SheetWebViewInterface$1(this, var4));
               break;
            case "close":
               this.webView.post(new SheetWebViewInterface$2(this));
               break;
            case "submit-sheet":
               this.webView.post(new SheetWebViewInterface$3(this, var4));
         }
      }
   }

   private boolean isHelpCenterMetric(String var1) {
      return "METRIC_EVENT".equals(var1) || "TRACK_REACTION".equals(var1);
   }

   // $VF: synthetic method
   static SheetListener access$000(SheetWebViewInterface var0) {
      return var0.listener;
   }
}
