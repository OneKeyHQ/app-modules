package io.intercom.android.sdk.metrics;

class MetricsStore$4$1 implements Runnable {
   // $VF: synthetic field
   final MetricsStore$4 this$1;

   MetricsStore$4$1(MetricsStore$4 var1) {
      this.this$1 = var1;
   }

   @Override
   public void run() {
      this.this$1.this$0.deleteFilesFor(this.this$1.val$metrics, this.this$1.val$opsMetrics);
   }
}
