package io.intercom.android.sdk.metrics;

import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class MetricsStore$4 implements Callback {
   // $VF: synthetic field
   final List val$metrics;
   // $VF: synthetic field
   final List val$opsMetrics;
   // $VF: synthetic field
   final MetricsStore this$0;

   MetricsStore$4(MetricsStore var1, List var2, List var3) {
      this.this$0 = var1;
      this.val$metrics = var2;
      this.val$opsMetrics = var3;
   }

   @Override
   public void onResponse(Call var1, Response var2) {
      if (var2.isSuccessful() || var2.code() == 400) {
         MetricsStore.access$600(this.this$0).execute(new MetricsStore$4$1(this));
      }
   }

   @Override
   public void onFailure(Call var1, Throwable var2) {
   }
}
