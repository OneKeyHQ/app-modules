package io.intercom.android.sdk.metrics;

import io.intercom.android.sdk.persistence.JsonStorage$LoadHandler;
import java.util.List;

class MetricsStore$5 implements JsonStorage$LoadHandler {
   // $VF: synthetic field
   final List val$metrics;
   // $VF: synthetic field
   final MetricsStore this$0;

   MetricsStore$5(MetricsStore var1, List var2) {
      this.this$0 = var1;
      this.val$metrics = var2;
   }

   public void onLoad(List var1) {
      MetricsStore.access$700(var1, this.val$metrics);
   }

   // $VF: synthetic method
   // $VF: bridge method
   @Override
   public void onLoad(Object var1) {
      this.onLoad((List)var1);
   }
}
