package io.intercom.android.nexus;

import java.util.List;

public class NexusConfig$Builder {
   List endpoints;
   int presence_heartbeat_interval;
   int connection_timeout;

   public NexusConfig build() {
      return new NexusConfig(this);
   }

   public NexusConfig$Builder withEndpoints(List var1) {
      this.endpoints = var1;
      return this;
   }

   public NexusConfig$Builder withPresenceHeartbeatInterval(int var1) {
      this.presence_heartbeat_interval = var1;
      return this;
   }

   public NexusConfig$Builder withConnectionTimeout(int var1) {
      this.connection_timeout = var1;
      return this;
   }
}
