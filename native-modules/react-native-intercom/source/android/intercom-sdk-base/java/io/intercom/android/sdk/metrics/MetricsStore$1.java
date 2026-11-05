package io.intercom.android.sdk.metrics;

class MetricsStore$1 implements Runnable {
   // $VF: synthetic field
   final MetricObject val$metric;
   // $VF: synthetic field
   final MetricsStore this$0;

   MetricsStore$1(MetricsStore var1, MetricObject var2) {
      this.this$0 = var1;
      this.val$metric = var2;
   }

   @Override
   public void run() {
      if (!MetricsStore.access$000(this.this$0)) {
         MetricsStore.access$200(this.this$0).saveToFileAsJson(this.val$metric, MetricsStore.access$100(this.this$0, this.val$metric));
      }
   }
}
