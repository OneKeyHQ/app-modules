package io.intercom.android.sdk.metrics;

import io.intercom.android.sdk.persistence.JsonStorage$LoadFailureHandler;
import io.intercom.android.sdk.utilities.IoUtils;
import java.io.File;

class MetricsStore$7 implements JsonStorage$LoadFailureHandler {
   // $VF: synthetic field
   final MetricsStore this$0;

   MetricsStore$7(MetricsStore var1) {
      this.this$0 = var1;
   }

   @Override
   public void onLoadFailed(File var1, Exception var2) {
      IoUtils.safelyDelete(var1);
      MetricsStore.access$800(this.this$0).v(var2, "Couldn't load file " + var1.getAbsolutePath());
   }
}
