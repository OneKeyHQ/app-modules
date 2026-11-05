package io.intercom.android.sdk.metrics;

import io.intercom.android.sdk.metrics.ops.OpsMetricObject;
import java.util.List;

class MetricsStore$3 implements Runnable {
   // $VF: synthetic field
   final MetricsStore this$0;

   MetricsStore$3(MetricsStore var1) {
      this.this$0 = var1;
   }

   @Override
   public void run() {
      List var1 = this.this$0.loadMetrics();
      List var2 = this.this$0.loadOpsMetrics();
      if (!var1.isEmpty() || !var2.isEmpty()) {
         MetricsStore.access$500(this.this$0, var1, var2);
      }
   }
}
