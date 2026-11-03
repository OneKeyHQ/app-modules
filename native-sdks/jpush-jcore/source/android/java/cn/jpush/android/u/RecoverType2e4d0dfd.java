package cn.jpush.android.u;

import android.app.Activity;
import android.content.Context;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import cn.jpush.android.ad.RecoverType9b4c5ae5;
import cn.jpush.android.ad.RecoverType9b4c5aef;
import cn.jpush.android.d.RecoverType2e4cce2a;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.s.RecoverType2e4d0676;
import cn.jpush.android.t.RecoverType2e4d0a37;
import cn.jpush.android.webview.bridge.HostJsScope;
import cn.jpush.android.webview.bridge.RecoverType8afd8fa0;
import cn.jpush.android.webview.bridge.RecoverType8afd8fa3;

public class RecoverType2e4d0dfd extends RecoverType2e4d0df9 {
   private WebView recoverField_f;
   private RecoverType8afd8fa3 recoverField_g;

   public RecoverType2e4d0dfd(Context var1, RecoverType2e4d0a37 var2, RecoverType2e4cce2a var3, int var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   public View recoverMethod_e_d08d2077() {
      return this.recoverField_f;
   }

   @Override
   public boolean recoverMethod_f_9b79() {
      if (this.recoverField_d != null && this.recoverField_b != null) {
         try {
            String var1 = this.recoverField_b.recoverField_be;
            if (TextUtils.isEmpty(var1)) {
               Logger.recoverMethod_w_e925fa09("BaseInAppWrapper", "url is empty, wb inflate failed.");
               return false;
            }

            if (null == this.recoverField_f) {
               this.recoverField_f = new WebView(this.recoverField_d);
               this.recoverField_f.setBackgroundColor(this.recoverField_d.getResources().getColor(17170445));
            }

            this.recoverField_f.setHorizontalScrollBarEnabled(false);
            this.recoverField_f.setVerticalScrollBarEnabled(false);
            this.recoverField_f.setScrollbarFadingEnabled(true);
            this.recoverField_f.setScrollBarStyle(33554432);
            WebSettings var2 = this.recoverField_f.getSettings();
            RecoverType9b4c5ae5.recoverMethod_c_4221005a(var2);
            RecoverType9b4c5ae5.recoverMethod_a_60006d18(this.recoverField_f);
            RecoverType9b4c5ae5.recoverMethod_a_4221005a(var2);
            Activity var3 = RecoverType2e4d0676.recoverMethod_a_2b1cd295(this.recoverField_d);
            this.recoverField_g = new RecoverType8afd8fa3(var3, this.recoverField_b);
            HostJsScope.setWebViewHelper(this.recoverField_g);
            if (VERSION.SDK_INT >= 17) {
               Logger.recoverMethod_d_e925fa09("BaseInAppWrapper", "Android sdk version greater than or equal to 17, Java—Js interact by annotation!");
               this.recoverMethod_a_9b75();
            }

            this.recoverField_f.setWebViewClient(new WebViewClient());
            this.recoverField_f.setWebChromeClient(new RecoverType8afd8fa0("JPushWeb", HostJsScope.class, null, null));
            this.recoverField_f.loadUrl(var1);
            Logger.recoverMethod_d_e925fa09("BaseInAppWrapper", "wb inflate completed, url: " + var1);
            return true;
         } catch (Throwable var4) {
            Logger.recoverMethod_w_e925fa09("BaseInAppWrapper", "wb inflate failed, " + var4.getMessage());
            return false;
         }
      } else {
         Logger.recoverMethod_w_e925fa09("BaseInAppWrapper", "unexpected error param is null");
         return false;
      }
   }

   private void recoverMethod_a_9b75() {
      Object[] var1 = new Object[2];
      Class[] var2 = new Class[2];
      var1[0] = this.recoverField_g;
      var1[1] = "JPushWeb";
      var2[0] = Object.class;
      var2[1] = String.class;

      try {
         RecoverType9b4c5aef.recoverMethod_a_222a2ac4(this.recoverField_f, "addJavascriptInterface", var2, var1);
      } catch (Throwable var4) {
         Logger.recoverMethod_e_e925fa09("BaseInAppWrapper", "addJavascriptInterface failed:" + var4.toString());
      }
   }

   @Override
   public void recoverMethod_c_9b75() {
      try {
         if (null != this.recoverField_f) {
            ViewParent var1 = this.recoverField_f.getParent();
            if (var1 != null) {
               try {
                  Logger.recoverMethod_d_e925fa09("BaseInAppWrapper", "wb parent view " + var1);
                  ((ViewGroup)var1).removeView(this.recoverField_f);
               } catch (Throwable var3) {
                  Logger.recoverMethod_w_e925fa09("BaseInAppWrapper", "release - parent not viewGroup");
               }
            }

            this.recoverField_f.getSettings().setJavaScriptEnabled(false);
            this.recoverField_f.clearCache(true);
            this.recoverField_f.clearHistory();
            this.recoverField_f.clearView();
            this.recoverField_f.removeAllViews();
            this.recoverField_f.clearSslPreferences();
            this.recoverField_f.destroy();
            this.recoverField_f = null;
         }

         this.recoverMethod_i_9b75();
         Logger.recoverMethod_d_e925fa09("BaseInAppWrapper", "wb release completed.");
      } catch (Throwable var4) {
         Logger.recoverMethod_w_e925fa09("BaseInAppWrapper", "wb destroy failed. error: " + var4.getMessage());
      }
   }

   @Override
   public void recoverMethod_d_9b75() {
   }

   @Override
   public void recoverMethod_g_9b75() {
   }
}
