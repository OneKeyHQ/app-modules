package io.intercom.android.sdk.api;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class CallbackHolder$4 implements Callback {
   // $VF: synthetic field
   final String val$action;
   // $VF: synthetic field
   final RatingCallback val$ratingCallback;
   // $VF: synthetic field
   final CallbackHolder this$0;

   CallbackHolder$4(CallbackHolder var1, String var2, RatingCallback var3) {
      this.this$0 = var1;
      this.val$action = var2;
      this.val$ratingCallback = var3;
   }

   @Override
   public void onResponse(Call var1, Response var2) {
      if (var2.isSuccessful()) {
         CallbackHolder.access$200().internal(this.val$action + " success");
         if (this.val$ratingCallback != null) {
            this.val$ratingCallback.onSuccess();
         }
      } else {
         CallbackHolder.access$200().internal(this.val$action + " failed with status " + var2.code());
         if (this.val$ratingCallback != null) {
            this.val$ratingCallback.onFailure(new ErrorObject(null, var2));
         }
      }
   }

   @Override
   public void onFailure(Call var1, Throwable var2) {
      CallbackHolder.access$200().internal(this.val$action + " failure");
      if (this.val$ratingCallback != null) {
         this.val$ratingCallback.onFailure(new ErrorObject(var2, null));
      }
   }
}
