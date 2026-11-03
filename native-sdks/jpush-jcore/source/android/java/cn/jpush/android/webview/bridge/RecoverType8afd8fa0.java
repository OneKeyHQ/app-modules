package cn.jpush.android.webview.bridge;

import android.text.TextUtils.TruncateAt;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class RecoverType8afd8fa0 extends RecoverType8afd8fa1 {
   private ProgressBar recoverField_a;
   private TextView recoverField_b;

   public RecoverType8afd8fa0(String var1, Class var2, ProgressBar var3, TextView var4) {
      super(var1, var2);
      this.recoverField_a = var3;
      this.recoverField_b = var4;
      if (this.recoverField_a != null) {
         this.recoverField_a.setMax(100);
      }

      if (this.recoverField_b != null) {
         this.recoverField_b.setSingleLine(true);
         this.recoverField_b.setEllipsize(TruncateAt.END);
      }
   }

   @Override
   public boolean onJsAlert(WebView var1, String var2, String var3, JsResult var4) {
      return super.onJsAlert(var1, var2, var3, var4);
   }

   @Override
   public void onProgressChanged(WebView var1, int var2) {
      super.onProgressChanged(var1, var2);
      if (this.recoverField_a != null) {
         if (100 == var2) {
            this.recoverField_a.setVisibility(8);
         } else {
            this.recoverField_a.setVisibility(0);
            this.recoverField_a.setProgress(var2);
         }
      }

      if (this.recoverField_b != null && var1.getTitle() != null && !var1.getTitle().equals(this.recoverField_b.getText())) {
         String var3 = var1.getTitle();
         boolean var4 = var3.startsWith("http") || var3.startsWith("android-app://") || var3.startsWith("file://");
         if (!var4) {
            this.recoverField_b.setText(var1.getTitle());
         }
      }
   }

   @Override
   public boolean onJsPrompt(WebView var1, String var2, String var3, String var4, JsPromptResult var5) {
      return super.onJsPrompt(var1, var2, var3, var4, var5);
   }
}
