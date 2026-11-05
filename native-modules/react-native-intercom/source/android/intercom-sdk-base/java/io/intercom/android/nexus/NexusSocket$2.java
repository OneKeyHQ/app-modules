package io.intercom.android.nexus;

class NexusSocket$2 implements Runnable {
   // $VF: synthetic field
   final NexusSocket this$0;

   NexusSocket$2(NexusSocket var1) {
      this.this$0 = var1;
   }

   @Override
   public void run() {
      this.this$0.connect();
      NexusSocket.access$102(this.this$0, null);
   }
}
