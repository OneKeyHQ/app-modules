package io.intercom.android.nexus;

import java.util.ArrayList;
import java.util.List;

public class NexusConfig {
   private final List<String> endpoints;
   private final int presenceHeartbeatInterval;
   private final int connectionTimeout;

   public NexusConfig() {
      this(new NexusConfig$Builder());
   }

   public NexusConfig(NexusConfig$Builder var1) {
      this.endpoints = var1.endpoints == null ? new ArrayList() : var1.endpoints;
      this.presenceHeartbeatInterval = var1.presence_heartbeat_interval;
      this.connectionTimeout = var1.connection_timeout;
   }

   public List<String> getEndpoints() {
      return this.endpoints;
   }

   public int getPresenceHeartbeatInterval() {
      return this.presenceHeartbeatInterval;
   }

   public int getConnectionTimeout() {
      return this.connectionTimeout;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         NexusConfig var2 = (NexusConfig)var1;
         return this.connectionTimeout == var2.connectionTimeout
            && this.presenceHeartbeatInterval == var2.presenceHeartbeatInterval
            && this.endpoints.equals(var2.endpoints);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      int var1 = this.endpoints.hashCode();
      var1 = 31 * var1 + this.presenceHeartbeatInterval;
      return 31 * var1 + this.connectionTimeout;
   }
}
