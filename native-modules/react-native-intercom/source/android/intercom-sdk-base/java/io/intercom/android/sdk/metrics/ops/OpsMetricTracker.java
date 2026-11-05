package io.intercom.android.sdk.metrics.ops;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import io.intercom.android.sdk.metrics.MetricsStore;
import io.intercom.android.sdk.utilities.UuidStringProvider;
import io.intercom.android.sdk.utilities.commons.TimeProvider;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class OpsMetricTracker {
   public static final String SEND_PART_TIME_TO_PROCESS_ACTION = "time-to-process-action-send-part-ms";
   public static final String SEND_PART_TIME_TO_COMPLETE_REQUEST = "time-to-complete-request-send-part-ms";
   public static final String SEND_PART_TIME_TO_RENDER_RESULT = "time-to-render-result-send-part-ms";
   public static final String LOAD_CONVERSATION_TIME_TO_PROCESS_ACTION = "time-to-process-action-load-conversation-ms";
   public static final String LOAD_CONVERSATION_TIME_TO_COMPLETE_REQUEST = "time-to-complete-request-load-conversation-ms";
   public static final String LOAD_CONVERSATION_TIME_TO_RENDER_RESULT = "time-to-render-result-load-conversation-ms";
   public static final String TIME_TO_COMPLETE_PING = "time-to-complete-ping-ms";
   public static final String TIMING_TYPE = "timing";
   public static final String START = "start";
   public static final String FINISH = "finish";
   private final MetricsStore store;
   private final TimeProvider timeProvider;
   private final UuidStringProvider uuidStringProvider;
   @VisibleForTesting
   final List events = new CopyOnWriteArrayList();

   public OpsMetricTracker(MetricsStore var1, TimeProvider var2, UuidStringProvider var3) {
      this.store = var1;
      this.timeProvider = var2;
      this.uuidStringProvider = var3;
   }

   public void trackEvent(String var1, String var2) {
      OpsEvent var3 = new OpsEvent(var1, var2, this.timeProvider.currentTimeMillis());
      if ("finish".equals(var1)) {
         this.trackOpsEvent(var3);
      } else {
         this.events.add(var3);
      }
   }

   private void trackOpsEvent(OpsEvent var1) {
      OpsEvent var2 = this.findStartEvent(var1.getName());
      if (var2 != null) {
         this.events.remove(var2);
         this.store.track(new OpsMetricObject("timing", var2.getName(), var1.getTimestamp() - var2.getTimestamp(), this.uuidStringProvider.newUuidString()));
      }
   }

   @Nullable
   private OpsEvent findStartEvent(String var1) {
      OpsEvent var2 = null;

      for (java.lang.Object recoveredEntryElement_0_55 : this.events) {
         OpsEvent var4 = (OpsEvent) recoveredEntryElement_0_55;
         if ("start".equals(var4.getEventType()) && var1.equals(var4.getName())) {
            var2 = var4;
         }
      }

      return var2;
   }

   public void clear() {
      this.events.clear();
   }
}
