package io.intercom.android.sdk.utilities.connectivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;

class ConnectivityBroadcastReceiver extends BroadcastReceiver {
   private final ConnectivityBroadcastReceiver$ConnectivityUpdateListener connectivityUpdateListener;

   ConnectivityBroadcastReceiver(ConnectivityBroadcastReceiver$ConnectivityUpdateListener var1) {
      this.connectivityUpdateListener = var1;
   }

   @Override
   public void onReceive(@Nullable Context var1, @Nullable Intent var2) {
      if (var2 != null && "android.net.conn.CONNECTIVITY_CHANGE".equals(var2.getAction())) {
         NetworkState var3 = var2.getBooleanExtra("noConnectivity", false) ? NetworkState.NOT_CONNECTED : NetworkState.CONNECTED;
         this.connectivityUpdateListener.onUpdate(var3);
      }
   }
}
