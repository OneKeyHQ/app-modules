package io.intercom.android.sdk.errorreporting;

import io.intercom.android.sdk.api.Api;
import io.intercom.android.sdk.persistence.JsonStorage$LoadHandler;

class ErrorReportStore$2 implements JsonStorage$LoadHandler {
   // $VF: synthetic field
   final Api val$api;
   // $VF: synthetic field
   final ErrorReportStore this$0;

   ErrorReportStore$2(ErrorReportStore var1, Api var2) {
      this.this$0 = var1;
      this.val$api = var2;
   }

   public void onLoad(ErrorReport var1) {
      this.val$api.sendErrorReport(var1);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void onLoad(Object var1) {
      this.onLoad((ErrorReport)var1);
   }
}
