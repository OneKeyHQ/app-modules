package io.intercom.android.nexus;

import okhttp3.Request;
import okhttp3.WebSocket;
import okio.ByteString;

class NexusSocket$4 implements WebSocket {
   @Override
   public Request request() {
      throw new NullPointerException("ClosedSocket has no request");
   }

   @Override
   public long queueSize() {
      return 0L;
   }

   @Override
   public boolean send(String var1) {
      return false;
   }

   @Override
   public boolean send(ByteString var1) {
      return false;
   }

   @Override
   public boolean close(int var1, String var2) {
      return false;
   }

   @Override
   public void cancel() {
   }
}
