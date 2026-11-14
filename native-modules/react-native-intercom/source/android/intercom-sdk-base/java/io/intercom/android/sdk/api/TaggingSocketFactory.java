package io.intercom.android.sdk.api;

import android.net.TrafficStats;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.net.SocketFactory;

public class TaggingSocketFactory extends SocketFactory {
   private final SocketFactory defaultFactory;
   private final int threadStatsTag;

   public TaggingSocketFactory(SocketFactory var1, int var2) {
      this.defaultFactory = var1;
      this.threadStatsTag = var2;
   }

   @Override
   public Socket createSocket() throws IOException {
      return this.withTag(this.defaultFactory.createSocket());
   }

   @Override
   public Socket createSocket(String var1, int var2) throws IOException, UnknownHostException {
      return this.withTag(this.defaultFactory.createSocket(var1, var2));
   }

   @Override
   public Socket createSocket(String var1, int var2, InetAddress var3, int var4) throws IOException, UnknownHostException {
      return this.withTag(this.defaultFactory.createSocket(var1, var2, var3, var4));
   }

   @Override
   public Socket createSocket(InetAddress var1, int var2) throws IOException {
      return this.withTag(this.defaultFactory.createSocket(var1, var2));
   }

   @Override
   public Socket createSocket(InetAddress var1, int var2, InetAddress var3, int var4) throws IOException {
      return this.withTag(this.defaultFactory.createSocket(var1, var2, var3, var4));
   }

   private Socket withTag(Socket var1) throws IOException {
      TrafficStats.setThreadStatsTag(this.threadStatsTag);
      return var1;
   }
}
