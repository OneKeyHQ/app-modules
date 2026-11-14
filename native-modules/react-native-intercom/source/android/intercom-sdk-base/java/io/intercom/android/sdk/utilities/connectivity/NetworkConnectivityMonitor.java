package io.intercom.android.sdk.utilities.connectivity;

import android.content.Context;
import android.content.IntentFilter;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.core.content.ContextCompat;

public class NetworkConnectivityMonitor implements ConnectivityBroadcastReceiver$ConnectivityUpdateListener {
   private final ConnectivityBroadcastReceiver receiver;
   @Nullable
   private NetworkConnectivityMonitor$ConnectivityEventListener listener;
   private NetworkState lastState = NetworkState.UNKNOWN;
   private boolean didRegister = false;

   public NetworkConnectivityMonitor() {
      this.receiver = new ConnectivityBroadcastReceiver(this);
   }

   @Override
   public void onUpdate(NetworkState var1) {
      if (var1 != this.lastState) {
         if (this.listener != null) {
            if (var1 == NetworkState.NOT_CONNECTED) {
               this.listener.onDisconnect();
            } else if (var1 == NetworkState.CONNECTED && this.lastState == NetworkState.NOT_CONNECTED) {
               this.listener.onReconnect();
            }
         }

         this.lastState = var1;
      }
   }

   public synchronized void startListening(Context var1) {
      if (!this.didRegister) {
         ContextCompat.registerReceiver(var1, this.receiver, new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"), 4);
         this.didRegister = true;
      }
   }

   public synchronized void stopListening(Context var1) {
      if (this.didRegister) {
         var1.unregisterReceiver(this.receiver);
         this.didRegister = false;
      }
   }

   public void setListener(@Nullable NetworkConnectivityMonitor$ConnectivityEventListener var1) {
      this.listener = var1;
   }

   @VisibleForTesting
   @Nullable
   NetworkConnectivityMonitor$ConnectivityEventListener getListener() {
      return this.listener;
   }
}
