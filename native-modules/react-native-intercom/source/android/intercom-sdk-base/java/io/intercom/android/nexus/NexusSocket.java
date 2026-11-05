package io.intercom.android.nexus;

import androidx.annotation.Nullable;
import com.intercom.twig.Twig;
import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.Request.Builder;

class NexusSocket {
   private static final int OK_CLIENT_DISCONNECT = 4000;
   private static final int N_TIMEOUT_DISCONNECT = 4001;
   private static final int MAX_RECONNECT_TIME_SECONDS = 900;
   private static final String HEADER = "?X-Nexus-Version=android.1.3.0";
   private final NexusListener listener;
   private final NexusTopicProvider topicProvider;
   private final ScheduledExecutorService timeoutExecutor;
   private final OkHttpClient client;
   private final Twig twig;
   private final String url;
   private final long connectionTimeoutSeconds;
   private final boolean shouldSendPing;
   private WebSocket socket;
   @Nullable
   private ScheduledFuture reconnectFuture;
   @Nullable
   private ScheduledFuture timeoutFuture;
   private final Runnable timeoutRunnable;
   private long lastReconnectAt;
   private int reconnectAttempts;
   private final WebSocketListener webSocketListener;
   private static final WebSocket CLOSED_SOCKET = new NexusSocket$4();

   NexusSocket(String var1, int var2, boolean var3, Twig var4, ScheduledExecutorService var5, OkHttpClient var6, NexusListener var7, NexusTopicProvider var8) {
      this.socket = CLOSED_SOCKET;
      this.timeoutRunnable = new NexusSocket$1(this);
      this.lastReconnectAt = 0L;
      this.reconnectAttempts = 0;
      this.webSocketListener = new NexusSocket$3(this);
      this.url = var1;
      this.connectionTimeoutSeconds = var2;
      this.shouldSendPing = var3;
      this.twig = var4;
      this.listener = var7;
      this.topicProvider = var8;
      this.client = var6;
      this.timeoutExecutor = var5;
   }

   void connect() {
      this.twig.d("connecting to a socket...");
      Request var1 = new Builder().url(this.url + "?X-Nexus-Version=android.1.3.0").build();
      this.client.newWebSocket(var1, this.webSocketListener);
      this.timeoutFuture = this.timeoutExecutor.schedule(this.timeoutRunnable, this.connectionTimeoutSeconds, TimeUnit.SECONDS);
   }

   void fire(String var1) {
      if (!var1.isEmpty()) {
         try {
            this.twig.internal("firing: " + var1);
            this.socket.send(var1);
         } catch (IllegalStateException var3) {
            this.twig.internal("Error when firing '" + var1 + "': " + var3);
         }
      }
   }

   void disconnect() {
      this.disconnect(4000, "Disconnect called by client");
   }

   boolean isConnected() {
      return this.socket != CLOSED_SOCKET;
   }

   private void disconnect(int var1, String var2) {
      boolean var3 = this.socket.close(var1, var2);
      if (!var3) {
         this.twig.internal("Could not close socket while disconnecting, it may be already closed");
      }
   }

   private void timedOut() {
      if (this.socket == CLOSED_SOCKET) {
         this.scheduleReconnect();
      } else {
         this.disconnect(4001, "Socket timed out");
      }

      this.listener.onConnectFailed();
   }

   private void resetTimeout() {
      if (this.timeoutFuture != null) {
         this.timeoutFuture.cancel(true);
      }

      this.timeoutFuture = this.timeoutExecutor.schedule(this.timeoutRunnable, this.connectionTimeoutSeconds, TimeUnit.SECONDS);
   }

   private void scheduleReconnect() {
      if (this.reconnectFuture == null) {
         this.modifyReconnectAttempts();
         long var1 = calculateReconnectTimerInSeconds(this.reconnectAttempts);
         this.twig.internal("Scheduling reconnect in: " + var1 + " for attempt: " + this.reconnectAttempts);
         this.reconnectFuture = this.timeoutExecutor.schedule(new NexusSocket$2(this), var1, TimeUnit.SECONDS);
      }
   }

   private void modifyReconnectAttempts() {
      long var1 = System.currentTimeMillis() - this.lastReconnectAt;
      long var3 = TimeUnit.SECONDS.toMillis(900L) * 2L;
      if (var1 > var3) {
         this.twig.d("resetting reconnection attempts");
         this.reconnectAttempts = 1;
      } else {
         this.twig.d("incrementing reconnection attempts");
         this.reconnectAttempts++;
      }

      this.lastReconnectAt = System.currentTimeMillis();
   }

   private void shutdown() {
      this.socket = CLOSED_SOCKET;
      if (this.timeoutFuture != null) {
         this.timeoutFuture.cancel(true);
      }

      this.listener.onShutdown();
   }

   static long calculateReconnectTimerInSeconds(int var0) {
      int var1 = (int)Math.min(Math.pow(2.0, var0), 900.0);
      int var2 = new Random().nextInt(var1 + 1);
      return var1 + var2;
   }

   static boolean shouldReconnectFromFailure(@Nullable Throwable var0, @Nullable Response var1) {
      if (var1 == null && var0 != null && isAndroidLifecycleException(var0)) {
         return false;
      }

      if (var1 == null) {
         return true;
      }

      int var2 = var1.code();
      return var2 >= 500 && var2 <= 599;
   }

   private static boolean isAndroidLifecycleException(@Nullable Throwable var0) {
      if (var0 == null) {
         return false;
      }

      String var1 = var0.getMessage();
      if (var1 == null) {
         var1 = "";
      }

      if (var0 instanceof SocketTimeoutException) {
         return var1.contains("Read timed out") && var1.length() < 50;
      } else if (var0 instanceof ConnectException) {
         return var1.contains("Connection refused") || var1.contains("Network unreachable");
      } else if (var0 instanceof SocketException) {
         return var1.contains("Software caused connection abort")
            || var1.contains("Connection reset by peer")
            || var1.contains("Connection reset")
            || var1.contains("Broken pipe")
            || var1.contains("Socket closed");
      } else if (var0 instanceof IllegalStateException) {
         return var1.contains("WebSocket is closed") || var1.contains("closed");
      } else if (var0 instanceof UnknownHostException) {
         return var1.contains("Unable to resolve host") || var1.contains("No address associated with hostname");
      } else {
         return !(var0 instanceof IOException)
            ? false
            : var1.contains("Unexpected end of stream") || var1.contains("Stream was reset") || var1.contains("Connection closed");
      }
   }

   // $VF: synthetic method
   static void access$000(NexusSocket var0) {
      var0.timedOut();
   }

   // $VF: synthetic method
   static ScheduledFuture access$102(NexusSocket var0, ScheduledFuture var1) {
      return var0.reconnectFuture = var1;
   }

   // $VF: synthetic method
   static Twig access$200(NexusSocket var0) {
      return var0.twig;
   }

   // $VF: synthetic method
   static WebSocket access$302(NexusSocket var0, WebSocket var1) {
      return var0.socket = var1;
   }

   // $VF: synthetic method
   static void access$400(NexusSocket var0) {
      var0.resetTimeout();
   }

   // $VF: synthetic method
   static NexusTopicProvider access$500(NexusSocket var0) {
      return var0.topicProvider;
   }

   // $VF: synthetic method
   static boolean access$600(NexusSocket var0) {
      return var0.shouldSendPing;
   }

   // $VF: synthetic method
   static NexusListener access$700(NexusSocket var0) {
      return var0.listener;
   }

   // $VF: synthetic method
   static void access$800(NexusSocket var0) {
      var0.shutdown();
   }

   // $VF: synthetic method
   static void access$900(NexusSocket var0) {
      var0.scheduleReconnect();
   }
}
