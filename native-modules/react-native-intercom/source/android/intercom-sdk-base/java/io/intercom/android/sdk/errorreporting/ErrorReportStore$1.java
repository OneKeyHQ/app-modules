package io.intercom.android.sdk.errorreporting;

import io.intercom.android.sdk.Provider;
import io.intercom.android.sdk.api.Api;

class ErrorReportStore$1 implements Runnable {
   // $VF: synthetic field
   final Provider val$apiProvider;
   // $VF: synthetic field
   final ErrorReportStore this$0;

   ErrorReportStore$1(ErrorReportStore var1, Provider var2) {
      this.this$0 = var1;
      this.val$apiProvider = var2;
   }

   @Override
   public void run() {
      if (ErrorReportStore.access$000(this.this$0).exists()) {
         ErrorReportStore.access$100(this.this$0, (Api)this.val$apiProvider.get());
      }
   }
}
