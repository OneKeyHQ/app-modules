package io.intercom.android.sdk.activities;

import io.intercom.android.sdk.models.Sheet;
import io.intercom.android.sdk.models.Sheet$Builder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class IntercomSheetActivity$4 implements Callback {
   // $VF: synthetic field
   final IntercomSheetActivity this$0;

   IntercomSheetActivity$4(IntercomSheetActivity var1) {
      this.this$0 = var1;
   }

   @Override
   public void onResponse(Call var1, Response var2) {
      if (var2.isSuccessful() && var2.body() != null) {
         IntercomSheetActivity.access$200(this.this$0).setVisibility(8);
         Sheet var3 = ((Sheet$Builder)var2.body()).build();
         IntercomSheetActivity.access$300(this.this$0).loadBundle(var3.getBody());
         IntercomSheetActivity.access$700(this.this$0)
            .viewedMessengerSheet(
               IntercomSheetActivity.access$400(this.this$0), IntercomSheetActivity.access$500(this.this$0), IntercomSheetActivity.access$600(this.this$0)
            );
      } else {
         IntercomSheetActivity.access$800(this.this$0);
      }
   }

   @Override
   public void onFailure(Call var1, Throwable var2) {
      IntercomSheetActivity.access$800(this.this$0);
   }
}
