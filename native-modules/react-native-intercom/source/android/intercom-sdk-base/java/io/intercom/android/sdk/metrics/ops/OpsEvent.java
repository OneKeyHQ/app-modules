package io.intercom.android.sdk.metrics.ops;

public class OpsEvent {
   private final String eventType;
   private final String name;
   private final long timestamp;

   public OpsEvent(String var1, String var2, long var3) {
      this.eventType = var1;
      this.name = var2;
      this.timestamp = var3;
   }

   public String getEventType() {
      return this.eventType;
   }

   public String getName() {
      return this.name;
   }

   public long getTimestamp() {
      return this.timestamp;
   }
}
