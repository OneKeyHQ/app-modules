package cn.jpush.android.webview.bridge;

import android.os.Build.VERSION;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import cn.jpush.android.helper.Logger;

public class RecoverType8afd8fa1 extends WebChromeClient {
   private final String recoverField_a = "InjectedChromeClient";
   private RecoverType8afd8fa2 recoverField_b;
   private boolean recoverField_c;

   public RecoverType8afd8fa1(String var1, Class var2) {
      this.recoverField_b = new RecoverType8afd8fa2(var1, var2);
   }

   @Override
   public boolean onJsAlert(WebView var1, String var2, String var3, JsResult var4) {
      var4.confirm();
      return true;
   }

   @Override
   public void onProgressChanged(WebView var1, int var2) {
      var1.getSettings().setSavePassword(false);
      if (VERSION.SDK_INT >= 21) {
         var1.getSettings().setMixedContentMode(1);
      }

      if (VERSION.SDK_INT < 17) {
         if (var2 <= 25) {
            this.recoverField_c = false;
         } else if (!this.recoverField_c) {
            Logger.recoverMethod_d_e925fa09("InjectedChromeClient", "Android sdk version lesser than 17, Java—Js interact by injection!");
            var1.loadUrl(this.recoverField_b.recoverMethod_a_73cf11cb());
            this.recoverField_c = true;
            Logger.recoverMethod_d_e925fa09("InjectedChromeClient", " inject js interface completely on progress " + var2);
         }
      }

      super.onProgressChanged(var1, var2);
   }

   @Override
   public boolean onJsPrompt(WebView var1, String var2, String var3, String var4, JsPromptResult var5) {
      if (VERSION.SDK_INT < 17) {
         var5.confirm(this.recoverField_b.recoverMethod_a_f25613fe(var1, var3));
      }

      return true;
   }
}
