package io.intercom.android.sdk.api;

import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

class Api$5 implements Callback {
   // $VF: synthetic field
   final Api this$0;

   Api$5(Api var1) {
      this.this$0 = var1;
   }

   @Override
   public void onFailure(Call var1, IOException var2) {
      Api.access$100().internal("Tracking Url", "Failed tracking url request");
   }

   @Override
   public void onResponse(Call var1, Response var2) {
      Api.access$100().internal("Tracking Url", "success");
      var2.body().close();
   }
}
