package io.intercom.android.nexus;

class NexusClient$1 implements Runnable {
   // $VF: synthetic field
   final NexusClient this$0;

   NexusClient$1(NexusClient var1) {
      this.this$0 = var1;
   }

   @Override
   public void run() {
      this.this$0.fire(NexusEvent.getPingEvent());
      NexusClient.access$100(this.this$0);
   }
}
