package io.intercom.android.sdk.blocks.messengercard;

import android.content.Context;
import android.util.AttributeSet;
import android.webkit.WebView;
import io.intercom.android.sdk.Injector;
import io.intercom.android.sdk.m5.data.IntercomEvent$CardUpdated;
import java.util.concurrent.CancellationException;
import kotlin.Unit;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.Job;

public class CardWebView extends WebView {
   private CoroutineScope eventScope;

   public CardWebView(Context var1) {
      super(var1);
   }

   public CardWebView(Context var1, AttributeSet var2) {
      super(var1, var2);
   }

   public CardWebView(Context var1, AttributeSet var2, int var3) {
      super(var1, var2, var3);
   }

   public void setUp() {
      this.getSettings().setAllowFileAccess(false);
      this.eventScope = CoroutineScopeKt.MainScope();
      Injector.get().getDataLayer().listenToEvents(this.eventScope, (kotlin.jvm.functions.Function1<io.intercom.android.sdk.m5.data.IntercomEvent, kotlin.Unit>) var1 -> {
         if (var1 == IntercomEvent$CardUpdated.INSTANCE) {
            this.reload();
         }

         return Unit.INSTANCE;
      });
   }

   @Override
   public void destroy() {
      if (this.eventScope != null) {
         Job var1 = (Job)this.eventScope.getCoroutineContext().get(Job.Key);
         if (var1 != null) {
            var1.cancel((CancellationException)null);
         }

         this.eventScope = null;
      }

      super.destroy();
   }
}
