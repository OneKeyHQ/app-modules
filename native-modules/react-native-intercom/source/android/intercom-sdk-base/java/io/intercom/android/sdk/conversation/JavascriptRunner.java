package io.intercom.android.sdk.conversation;

import android.webkit.WebView;
import androidx.annotation.VisibleForTesting;
import java.util.ArrayList;
import java.util.Collection;

public class JavascriptRunner {
   private final WebView webView;
   private final Collection actionsAfterLoad;
   private boolean hasLoaded = false;

   public JavascriptRunner(WebView var1) {
      this(var1, new ArrayList());
   }

   @VisibleForTesting
   JavascriptRunner(WebView var1, Collection var2) {
      this.webView = var1;
      this.actionsAfterLoad = var2;
   }

   public synchronized void runPendingScripts() {
      this.hasLoaded = true;

      for (java.lang.Object recoveredEntryElement_0_26 : this.actionsAfterLoad) {
         Runnable var2 = (Runnable) recoveredEntryElement_0_26;
         var2.run();
      }

      this.clearPendingScripts();
   }

   public synchronized void clearPendingScripts() {
      this.actionsAfterLoad.clear();
   }

   public synchronized void run(String var1) {
      Runnable var2 = new JavascriptRunner$1(this, var1);
      if (this.hasLoaded) {
         this.webView.post(var2);
      } else {
         this.actionsAfterLoad.add(var2);
      }
   }

   public synchronized void reset() {
      this.hasLoaded = false;
      this.clearPendingScripts();
   }

   // $VF: synthetic method
   static WebView access$000(JavascriptRunner var0) {
      return var0.webView;
   }
}
