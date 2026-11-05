package io.intercom.android.nexus;

import android.util.LruCache;
import androidx.annotation.NonNull;
import com.intercom.twig.Twig;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

class NexusEventPropagator implements NexusListener {
   private final LruCache cache = new LruCache(100);
   private final List<NexusListener> listeners = new CopyOnWriteArrayList();
   private final Twig twig;

   public NexusEventPropagator(Twig var1) {
      this.twig = var1;
   }

   void addListener(@NonNull NexusListener var1) {
      this.listeners.add(var1);
   }

   void removeListener(@NonNull NexusListener var1) {
      this.listeners.remove(var1);
   }

   void cacheEvent(NexusEvent var1) {
      this.cache.put(var1.getGuid(), true);
   }

   @Override
   public void notifyEvent(@NonNull NexusEvent var1) {
      if (var1.getEventType() != NexusEventType.UNKNOWN) {
         synchronized (this.cache) {
            if (this.cache.get(var1.getGuid()) == null) {
               this.cache.put(var1.getGuid(), true);
               this.twig.internal("notifying listeners of event: " + var1.getEventType());

               for (NexusListener var4 : this.listeners) {
                  var4.notifyEvent(var1);
               }
            } else {
               this.twig.internal("dropping event, already in cache: " + var1.toStringEncodedJsonObject());
            }
         }
      }
   }

   @Override
   public void onConnect() {
      this.twig.d("notifying listeners that a connection opened");

      for (NexusListener var2 : this.listeners) {
         var2.onConnect();
      }
   }

   @Override
   public void onConnectFailed() {
      this.twig.d("notifying listeners that a connection failed to open");

      for (NexusListener var2 : this.listeners) {
         var2.onConnectFailed();
      }
   }

   @Override
   public void onShutdown() {
      this.twig.d("notifying listeners that a connection has shutdown");

      for (NexusListener var2 : this.listeners) {
         var2.onShutdown();
      }
   }
}
