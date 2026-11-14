package io.intercom.android.sdk.api;

import android.os.Handler;
import android.os.Looper;
import io.intercom.android.sdk.IntercomError;
import io.intercom.android.sdk.IntercomStatusCallback;
import io.intercom.android.sdk.models.UpdateUserResponse;
import io.intercom.android.sdk.models.UpdateUserResponse$Builder;
import java.util.Map;

class Api$3 extends BaseCallback {
   // $VF: synthetic field
   final IntercomStatusCallback val$intercomStatusCallback;
   // $VF: synthetic field
   final String val$fingerprint;
   // $VF: synthetic field
   final Map val$params;
   // $VF: synthetic field
   final Api this$0;

   Api$3(Api var1, IntercomStatusCallback var2, String var3, Map var4) {
      this.this$0 = var1;
      this.val$intercomStatusCallback = var2;
      this.val$fingerprint = var3;
      this.val$params = var4;
   }

   public void onSuccess(UpdateUserResponse$Builder var1) {
      new Handler(Looper.getMainLooper()).post(this.val$intercomStatusCallback::onSuccess);
      if (this.val$fingerprint.equals(this.this$0.userIdentity.getFingerprint())) {
         Api.access$100().i("Successfully registered or updated user");
         this.this$0.callbacks.unreadCallback().onSuccess(var1);
         UpdateUserResponse var2 = var1.build();
         Api.access$200(this.this$0).updateCarousel(var2.getCarousel());
         Api.access$200(this.this$0).updateSurveyData(var2.getSurveyData());
         Api.access$300(this.this$0).trackEvent("finish", "time-to-complete-ping-ms");
         if (!Api.access$000(this.this$0).getPushToken().isEmpty()) {
            Api.access$000(this.this$0).setDeviceTokenSubmitted(true);
         }
      }
   }

   @Override
   public void onError(ErrorObject var1) {
      Map var2 = (Map<String, Object>)this.val$params.get("user");
      if (Api.isUserNotFound(var1, var2)) {
         var2.remove("intercom_id");
         this.val$params.put("user", var2);
         Api.access$400(this.this$0, this.val$params, this.val$fingerprint, this.val$intercomStatusCallback);
      } else {
         String var3 = ErrorStringExtractorKt.extractErrorString(var1);
         Handler failureHandler = new Handler(Looper.getMainLooper());
         final io.intercom.android.sdk.IntercomStatusCallback failureCallback = this.val$intercomStatusCallback;
         failureHandler.post(() -> failureCallback.onFailure(new IntercomError(1001, "ERROR - [HTTP " + var1.getStatusCode() + "] - " + var3)));
      }
   }

   @Override
   void logFailure(String var1, ErrorObject var2) {
      super.logFailure("Failed to register or update user", var2);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void onSuccess(Object var1) {
      this.onSuccess((UpdateUserResponse$Builder)var1);
   }
}
