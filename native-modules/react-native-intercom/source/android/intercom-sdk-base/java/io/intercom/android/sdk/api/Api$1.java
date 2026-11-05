package io.intercom.android.sdk.api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class Api$1 implements Callback {
   // $VF: synthetic field
   final Api this$0;

   Api$1(Api var1) {
      this.this$0 = var1;
   }

   @Override
   public void onResponse(Call var1, Response var2) {
      if (var2.isSuccessful()) {
         Api.access$000(this.this$0).setDeviceTokenSubmitted(true);
      }
   }

   @Override
   public void onFailure(Call var1, Throwable var2) {
   }
}
