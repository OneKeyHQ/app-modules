package io.intercom.android.sdk.api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class Api$2 implements Callback {
   // $VF: synthetic field
   final String val$previousFingerprint;
   // $VF: synthetic field
   final Api this$0;

   Api$2(Api var1, String var2) {
      this.this$0 = var1;
      this.val$previousFingerprint = var2;
   }

   @Override
   public void onResponse(Call var1, Response var2) {
      if (var2.isSuccessful() && this.val$previousFingerprint.equals(this.this$0.userIdentity.getFingerprint())) {
         Api.access$000(this.this$0).setDeviceTokenSubmitted(false);
      }
   }

   @Override
   public void onFailure(Call var1, Throwable var2) {
   }
}
