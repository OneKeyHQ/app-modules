package io.intercom.android.nexus;

import androidx.annotation.Nullable;
import java.util.List;

public class NexusEvent$Builder {
   final NexusEventType eventName;
   @Nullable
   EventData eventData;
   @Nullable
   List<String> topics;
   @Nullable
   String userId;

   NexusEvent$Builder(NexusEventType var1) {
      this.eventName = var1;
   }

   public NexusEvent$Builder withTopics(List var1) {
      this.topics = var1;
      return this;
   }

   public NexusEvent$Builder withEventData(EventData var1) {
      this.eventData = var1;
      return this;
   }

   public NexusEvent$Builder withUserId(String var1) {
      this.userId = var1;
      return this;
   }

   public NexusEvent build() {
      return new NexusEvent(this);
   }
}
