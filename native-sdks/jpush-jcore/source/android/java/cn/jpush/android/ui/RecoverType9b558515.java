package cn.jpush.android.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build.VERSION;
import android.webkit.SslErrorHandler;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import cn.jpush.android.ad.RecoverType9b4c5ae5;
import cn.jpush.android.d.RecoverType2e4cce2a;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.helper.RecoverType8b455831;
import java.util.PriorityQueue;
import java.util.Queue;
import org.json.JSONObject;

public class RecoverType9b558515 extends WebViewClient {
   private final RecoverType2e4cce2a recoverField_a;
   private final Context recoverField_b;
   private boolean recoverField_c = false;
   private String recoverField_d = "";
   private Queue recoverField_e;
   private boolean recoverField_f = false;

   public RecoverType9b558515(RecoverType2e4cce2a var1, Context var2) {
      this.recoverField_a = var1;
      this.recoverField_b = var2;
      this.recoverField_e = new PriorityQueue();
   }

   public void recoverMethod_a_1385ff(boolean var1) {
      this.recoverField_f = var1;
   }

   @Override
   public boolean shouldOverrideUrlLoading(WebView var1, String var2) {
      if (var2 != null && !var2.trim().isEmpty()) {
         try {
            Uri.parse(var2);
         } catch (Exception var18) {
            Logger.recoverMethod_w_e925fa09("JPushWebViewClient", "Invalid URL format: " + var2);
            return true;
         }

         Context var3 = var1.getContext();
         Logger.recoverMethod_i_e925fa09("JPushWebViewClient", "Url vaule is :" + var2);

         try {
            var1.getSettings().setSavePassword(false);
            RecoverType9b4c5ae5.recoverMethod_a_60006d18(var1);

            String var4;
            try {
               JSONObject var5 = new JSONObject();
               var5.put("url", var2);
               var4 = var5.toString();
            } catch (Exception var16) {
               Logger.recoverMethod_e_1765c98c("JPushWebViewClient", "JSON error", var16);
               var4 = "{\"url\":\"\"}";
            }

            if (var2.endsWith(".mp3")) {
               Intent var23 = new Intent("android.intent.action.VIEW");
               var23.setDataAndType(Uri.parse(var2), "audio/*");
               var1.getContext().startActivity(var23);
               return true;
            }

            if (var2.endsWith(".mp4") || var2.endsWith(".3gp")) {
               Intent var22 = new Intent("android.intent.action.VIEW");
               var22.setDataAndType(Uri.parse(var2), "video/*");
               var1.getContext().startActivity(var22);
               return true;
            }

            if (var2.endsWith(".apk")) {
               Logger.recoverMethod_dd_e925fa09("JPushWebViewClient", "google play not allowed");
               return true;
            }

            if (var2.startsWith("http")) {
               if (this.recoverField_a.recoverField_ai == 0) {
                  RecoverType8b455831.recoverMethod_a_d7d77f79(this.recoverField_a.recoverField_d, 1016L, var4, this.recoverField_b);
               } else {
                  RecoverType8b455831.recoverMethod_a_3e8d9d2b(
                     this.recoverField_a.recoverField_d, "", this.recoverField_a.recoverField_ai, 1016L, this.recoverField_b, null, var4
                  );
               }

               return false;
            } else if (var2.startsWith("mailto")) {
               int var19 = var2.lastIndexOf("direct=");
               if (var19 < 0 && !var2.startsWith("mailto")) {
                  if (var2.indexOf("?") > 0) {
                     var2 = var2 + "&direct=false";
                  } else {
                     var2 = var2 + "?direct=false";
                  }

                  var19 = var2.lastIndexOf("direct=");
               }

               var19 = var2.indexOf("?");
               String var6 = var2.substring(0, var19);
               String var7 = var2.substring(var19);
               Logger.recoverMethod_v_e925fa09("JPushWebViewClient", "Uri: " + var6);
               Logger.recoverMethod_v_e925fa09("JPushWebViewClient", "QueryString: " + var7);
               Intent var8 = null;
               if (var6.startsWith("mailto")) {
                  String[] var9 = var6.split(":");
                  if (var9 != null && var9.length == 2 && var7 != null) {
                     String var10 = "&content=";
                     int var11 = var7.indexOf("title=") + "title=".length();
                     int var12 = var7.indexOf("&content=");
                     String var13 = var7.substring(var11, var12);
                     String var14 = var7.substring(var12 + "&content=".length());
                     String[] var15 = new String[]{var9[1]};
                     var8 = new Intent("android.intent.action.SEND");
                     var8.setType("plain/text");
                     var8.putExtra("android.intent.extra.EMAIL", var15);
                     var8.putExtra("android.intent.extra.SUBJECT", var13);
                     var8.putExtra("android.intent.extra.TEXT", var14);
                  }
               }

               if (var8 != null) {
                  var3.startActivity(var8);
               }

               if (this.recoverField_a.recoverField_ai == 0) {
                  RecoverType8b455831.recoverMethod_a_d7d77f79(this.recoverField_a.recoverField_d, 1016L, var4, this.recoverField_b);
               } else {
                  RecoverType8b455831.recoverMethod_a_3e8d9d2b(
                     this.recoverField_a.recoverField_d, "", this.recoverField_a.recoverField_ai, 1016L, this.recoverField_b, null, var4
                  );
               }

               return true;
            } else {
               return this.recoverMethod_a_aad3b203(var2);
            }
         } catch (Throwable var17) {
            Logger.recoverMethod_e_e925fa09("JPushWebViewClient", "Invalid url");
            return true;
         }
      } else {
         Logger.recoverMethod_w_e925fa09("JPushWebViewClient", "Invalid URL: null or empty");
         return true;
      }
   }

   @Override
   public void onPageStarted(WebView var1, String var2, Bitmap var3) {
      super.onPageStarted(var1, var2, var3);
   }

   @Override
   public void onPageFinished(WebView var1, String var2) {
      super.onPageFinished(var1, var2);
      this.recoverField_d = var2;
   }

   @Override
   public void onLoadResource(WebView var1, String var2) {
      super.onLoadResource(var1, var2);
   }

   @Override
   public void onReceivedError(WebView var1, int var2, String var3, String var4) {
      super.onReceivedError(var1, var2, var3, var4);
   }

   @Override
   public void onReceivedError(WebView var1, WebResourceRequest var2, WebResourceError var3) {
      super.onReceivedError(var1, var2, var3);
   }

   @Override
   public void onReceivedHttpError(WebView var1, WebResourceRequest var2, WebResourceResponse var3) {
      super.onReceivedHttpError(var1, var2, var3);
   }

   @Override
   public void onReceivedSslError(WebView var1, SslErrorHandler var2, SslError var3) {
      Logger.recoverMethod_ww_e925fa09("JPushWebViewClient", "onReceivedSslError stop show:" + var1.getUrl());
      var2.cancel();
   }

   private boolean recoverMethod_a_aad3b203(String var1) {
      Intent var2 = null;
      boolean var3 = true;

      try {
         if (var1.startsWith("android-app://")) {
            if (VERSION.SDK_INT >= 22) {
               var2 = RecoverType9b4c5ae5.recoverMethod_a_2e1cfbaf(var1, 2);
            }
         } else {
            var2 = RecoverType9b4c5ae5.recoverMethod_a_2e1cfbaf(var1, 1);
         }

         if (this.recoverField_b != null && null != var2) {
            PackageManager var4 = this.recoverField_b.getPackageManager();
            if (var4 != null && var4.resolveActivity(var2, 0) == null) {
               Logger.recoverMethod_w_e925fa09("JPushWebViewClient", "open deeplink intent can be not resolved, url: " + var1);
               return true;
            }

            var2.addCategory("android.intent.category.BROWSABLE");
            if (!var1.startsWith("android-app://")) {
               var2.setComponent(null);
            }

            var2 = RecoverType9b4c5ae5.recoverMethod_a_2e1cfbaf(var1, 0);
            var2.setFlags(268435456);
            this.recoverField_b.startActivity(var2);
            Logger.recoverMethod_d_e925fa09("JPushWebViewClient", "h5 open deeplink success, url: " + var1);
         }
      } catch (Throwable var6) {
         Logger.recoverMethod_w_e925fa09("JPushWebViewClient", "[openDeeplink] Bad URI " + var1 + ": " + var6.getMessage());
         var3 = false;
      }

      long var8 = var3 ? 976L : 977L;
      if (this.recoverField_a.recoverField_ai == 0) {
         RecoverType8b455831.recoverMethod_a_36ca9ac3(this.recoverField_a.recoverField_d, var8, this.recoverField_b);
      } else {
         RecoverType8b455831.recoverMethod_a_2ef42797(this.recoverField_a.recoverField_d, "", this.recoverField_a.recoverField_ai, var8, this.recoverField_b);
      }

      return var3;
   }
}
