package io.intercom.android.sdk.activities;

import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class IntercomSheetActivity$7 implements Callback {
   // $VF: synthetic field
   final Map val$payload;
   // $VF: synthetic field
   final IntercomSheetActivity this$0;

   IntercomSheetActivity$7(IntercomSheetActivity var1, Map var2) {
      this.this$0 = var1;
      this.val$payload = var2;
   }

   @Override
   public void onResponse(Call var1, Response var2) {
      if (var2.isSuccessful()) {
         IntercomSheetActivity.access$1000(this.this$0);
         this.this$0.closeSheet();
      } else {
         IntercomSheetActivity.access$1100(this.this$0, this.val$payload);
      }
   }

   @Override
   public void onFailure(Call var1, Throwable var2) {
      IntercomSheetActivity.access$1100(this.this$0, this.val$payload);
   }
}
