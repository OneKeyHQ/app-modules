package io.intercom.android.nexus;

import androidx.annotation.NonNull;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

class NexusClient$NexusThreadFactory implements ThreadFactory {
   private final ThreadFactory defaultFactory = Executors.defaultThreadFactory();
   private int threadCount = 0;

   private NexusClient$NexusThreadFactory() {
   }

   @Override
   public Thread newThread(@NonNull Runnable var1) {
      Thread var2 = this.defaultFactory.newThread(var1);
      this.threadCount++;
      var2.setName("IntercomNexus-" + this.threadCount);
      return var2;
   }

   // $VF: synthetic method
   NexusClient$NexusThreadFactory(NexusClient$1 var1) {
      this();
   }
}
