package io.intercom.android.sdk.blocks.messengercard;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.AutoCompleteTextView;
import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AlertDialog.Builder;
import androidx.core.view.ViewCompat;
import io.intercom.android.sdk.R.id;
import io.intercom.android.sdk.R.layout;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;

class MessengerCardAlertDialogBuilder extends Builder {
   MessengerCardAlertDialogBuilder(@NonNull Context var1, @NonNull Map var2, int var3, @NonNull WebView var4) {
      super(var1);
      this.setUp(var1, var2, var3, var4);
   }

   private void setUp(Context var1, Map var2, int var3, WebView var4) {
      View var5 = LayoutInflater.from(var1).inflate(layout.intercom_webview_card_input, null, false);
      this.setTitle((CharSequence)var2.get("label"));
      AutoCompleteTextView var6 = (AutoCompleteTextView)var5.findViewById(id.input);
      var6.setText((CharSequence)var2.get("currentValue"));
      var6.setHint((CharSequence)var2.get("placeholder"));
      ColorStateList var7 = ColorStateList.valueOf(var3);
      ViewCompat.setBackgroundTintList(var6, var7);
      this.setView(var5);
      this.setPositiveButton(17039370, new MessengerCardAlertDialogBuilder$1(this, var4, var6, var2));
      this.setNegativeButton(17039360, new MessengerCardAlertDialogBuilder$2(this, var6));
   }

   @VisibleForTesting
   void sendResultToWebview(WebView var1, String var2, Map var3) {
      String var4 = "";

      try {
         var4 = URLEncoder.encode(var2, "UTF-8").replaceAll("\\+", "%20");
      } catch (UnsupportedEncodingException var6) {
         var6.printStackTrace();
      }

      String var5 = "window.fallbackApp.bridgeEvent('UPDATE_TEXT', { value:'" + var4 + "', id:'" + var3.get("id") + "'})";
      var1.evaluateJavascript(var5, null);
   }
}
