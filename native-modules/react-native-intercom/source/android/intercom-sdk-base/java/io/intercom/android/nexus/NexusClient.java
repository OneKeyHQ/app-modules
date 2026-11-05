package io.intercom.android.nexus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.intercom.twig.Twig;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;

public class NexusClient implements NexusTopicProvider {
   private final List<NexusSocket> sockets = new ArrayList();
   private final List<String> topics = new ArrayList();
   private final Twig twig;
   private final OkHttpClient client;
   private final NexusEventPropagator eventPropagator;
   @Nullable
   private ScheduledExecutorService backgroundTaskExecutor;
   private ScheduledFuture future;
   private long heartbeatInterval;

   public static Builder defaultOkHttpClientBuilder() {
      return new Builder().readTimeout(130L, TimeUnit.SECONDS).writeTimeout(130L, TimeUnit.SECONDS).connectTimeout(20L, TimeUnit.SECONDS);
   }

   public NexusClient(Twig var1) {
      this(var1, defaultOkHttpClientBuilder().build());
   }

   public NexusClient(Twig var1, OkHttpClient var2) {
      this(var1, var2, new NexusEventPropagator(var1));
   }

   NexusClient(Twig var1, OkHttpClient var2, NexusEventPropagator var3) {
      this.twig = var1;
      this.eventPropagator = var3;
      this.client = var2;
   }

   public void connect(NexusConfig var1, boolean var2) {
      if (var1.getEndpoints().isEmpty()) {
         this.twig.e("No endpoints present");
      } else {
         if (this.backgroundTaskExecutor == null) {
            ThreadFactory var3 = new NexusClient$NexusThreadFactory(null);
            this.backgroundTaskExecutor = Executors.newScheduledThreadPool(var1.getEndpoints().size() + 1, var3);
         }

         for (String var4 : var1.getEndpoints()) {
            this.twig.i("Adding socket");
            NexusSocket var5 = new NexusSocket(
               var4, var1.getConnectionTimeout(), var2, this.twig, this.backgroundTaskExecutor, this.client, this.eventPropagator, this
            );
            var5.connect();
            this.sockets.add(var5);
         }

         this.heartbeatInterval = var1.getPresenceHeartbeatInterval();
         if (var2) {
            this.schedulePing();
         }
      }
   }

   public synchronized void disconnect() {
      if (!this.sockets.isEmpty()) {
         for (NexusSocket var2 : this.sockets) {
            this.twig.i("disconnecting socket");
            var2.disconnect();
         }

         this.sockets.clear();
         this.twig.i("client disconnected");
      }

      if (this.future != null) {
         this.future.cancel(true);
      }
   }

   public synchronized void fire(NexusEvent var1) {
      this.eventPropagator.cacheEvent(var1);
      String var2 = var1.toStringEncodedJsonObject();
      if (!var2.isEmpty()) {
         for (NexusSocket var4 : this.sockets) {
            var4.fire(var2);
         }
      }
   }

   public synchronized void localUpdate(@NonNull NexusEvent var1) {
      this.eventPropagator.notifyEvent(var1);
   }

   public synchronized boolean isConnected() {
      for (NexusSocket var2 : this.sockets) {
         if (var2.isConnected()) {
            return true;
         }
      }

      return false;
   }

   public void addEventListener(@NonNull NexusListener var1) {
      this.eventPropagator.addListener(var1);
   }

   public void removeEventListener(@NonNull NexusListener var1) {
      this.eventPropagator.removeListener(var1);
   }

   @NonNull
   @Override
   public synchronized List getTopics() {
      return this.topics;
   }

   public synchronized void setTopics(@NonNull List var1) {
      List var2 = new ArrayList(var1);
      var2.removeAll(this.topics);
      List var3 = new ArrayList(this.topics);
      var3.removeAll(var1);
      this.subscribeToTopics(var2);
      this.unSubscribeFromTopics(var3);
      this.topics.clear();
      this.topics.addAll(var1);
   }

   public synchronized void addTopics(@NonNull List var1) {
      List var2 = new ArrayList(var1);
      var2.removeAll(this.topics);
      this.subscribeToTopics(var2);
      this.topics.addAll(var2);
   }

   public synchronized void removeTopics(@NonNull List<String> var1) {
      List var2 = new ArrayList();

      for (String var4 : var1) {
         if (this.topics.contains(var4)) {
            var2.add(var4);
         }
      }

      this.unSubscribeFromTopics(var2);
      this.topics.removeAll(var2);
   }

   public synchronized void clearTopics() {
      this.unSubscribeFromTopics(this.topics);
      this.topics.clear();
   }

   private void subscribeToTopics(@NonNull List var1) {
      if (!var1.isEmpty()) {
         this.fire(NexusEvent.getSubscribeEvent(var1));
      }
   }

   private void unSubscribeFromTopics(@NonNull List var1) {
      if (!var1.isEmpty()) {
         this.fire(NexusEvent.getUnsubscribeEvent(var1));
      }
   }

   private void schedulePing() {
      if (this.heartbeatInterval > 0L) {
         this.future = this.backgroundTaskExecutor.schedule(new NexusClient$1(this), this.heartbeatInterval, TimeUnit.SECONDS);
      }
   }

   // $VF: synthetic method
   static void access$100(NexusClient var0) {
      var0.schedulePing();
   }
}
