package io.intercom.android.sdk.helpcenter.webview;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import com.google.gson.Gson;
import io.intercom.android.sdk.api.Api;
import io.intercom.android.sdk.metrics.MetricTracker;
import java.util.HashMap;
import java.util.Map;

public class HelpCenterWebViewInterface {
   private final WebView webView;
   private final Gson gson;
   private final MetricTracker metricTracker;
   private final Api api;
   private final boolean isFromSearchBrowse;
   public static final String METRIC_EVENT = "METRIC_EVENT";
   public static final String TRACK_REACTION = "TRACK_REACTION";

   public HelpCenterWebViewInterface(WebView var1, Gson var2, MetricTracker var3, Api var4, boolean var5) {
      this.webView = var1;
      this.gson = var2;
      this.metricTracker = var3;
      this.api = var4;
      this.isFromSearchBrowse = var5;
   }

   @JavascriptInterface
   public void handleAction(String var1) {
      HelpCenterWebViewAction var2 = (HelpCenterWebViewAction)this.gson.fromJson(var1, HelpCenterWebViewAction.class);
      String var3 = var2.getType();
      Map var4 = var2.getValue();
      if (var3 != null && var4 != null) {
         switch (var3) {
            case "METRIC_EVENT":
               this.webView.post(() -> {
                  String eventAction = (String)var4.get("action");
                  String eventObject = (String)var4.get("object");
                  String eventPlace = (String)var4.get("place");
                  Map eventMetaData = new HashMap();
                  if (var4.get("metadata") instanceof Map) {
                     eventMetaData.putAll((Map)var4.get("metadata"));
                  }

                  if (this.isFromSearchBrowse) {
                     eventMetaData.put("article_source", "search_browse");
                  }

                  this.metricTracker.educateWebviewMetric(eventAction, eventObject, eventPlace, eventMetaData, "from_help_center_webview");
               });
               break;
            case "TRACK_REACTION":
               this.webView.post(() -> {
                  Double articleId = (Double)var4.get("article_id");
                  Double articleContentId = (Double)var4.get("article_content_id");
                  Double reactionIndex = (Double)var4.get("reaction_index");
                  if (articleId != null && articleContentId != null && reactionIndex != null) {
                     this.api.reactToLink(String.valueOf(articleId.intValue()), String.valueOf(articleContentId.intValue()), reactionIndex.intValue(), true);
                  }
               });
         }
      }
   }
}
