package io.intercom.android.sdk.metrics;

import io.intercom.android.sdk.metrics.ops.OpsMetricObject;

class MetricsStore$2 implements Runnable {
   // $VF: synthetic field
   final OpsMetricObject val$opsMetric;
   // $VF: synthetic field
   final MetricsStore this$0;

   MetricsStore$2(MetricsStore var1, OpsMetricObject var2) {
      this.this$0 = var1;
      this.val$opsMetric = var2;
   }

   @Override
   public void run() {
      if (!MetricsStore.access$300(this.this$0)) {
         MetricsStore.access$200(this.this$0).saveToFileAsJson(this.val$opsMetric, MetricsStore.access$400(this.this$0, this.val$opsMetric));
      }
   }
}
