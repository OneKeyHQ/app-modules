package io.intercom.android.sdk.api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class CallbackHolder$3 implements Callback {
   // $VF: synthetic field
   final String val$action;
   // $VF: synthetic field
   final CallbackHolder this$0;

   CallbackHolder$3(CallbackHolder var1, String var2) {
      this.this$0 = var1;
      this.val$action = var2;
   }

   @Override
   public void onResponse(Call var1, Response var2) {
      CallbackHolder.access$200().internal(this.val$action + " success");
   }

   @Override
   public void onFailure(Call var1, Throwable var2) {
      CallbackHolder.access$200().internal(this.val$action + " failure");
   }
}
