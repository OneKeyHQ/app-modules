package io.intercom.android.sdk.api;

import io.intercom.android.sdk.survey.model.FetchSurveyRequest;

class Api$7 extends BaseCallback {
   // $VF: synthetic field
   final Api this$0;

   Api$7(Api var1) {
      this.this$0 = var1;
   }

   protected void onSuccess(FetchSurveyRequest var1) {
      Api.access$200(this.this$0).updateSurveyData(var1.getSurvey());
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   protected void onSuccess(Object var1) {
      this.onSuccess((FetchSurveyRequest)var1);
   }
}
