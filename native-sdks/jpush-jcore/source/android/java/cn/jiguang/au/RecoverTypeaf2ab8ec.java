package cn.jiguang.au;

import android.annotation.TargetApi;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.net.ConnectivityManager.NetworkCallback;
import android.net.NetworkRequest.Builder;
import android.os.Bundle;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.api.JCoreManager;
import cn.jiguang.bk.RecoverTypeaf2b07c4;

@TargetApi(21)
public class RecoverTypeaf2ab8ec extends NetworkCallback {
   private NetworkRequest recoverField_a;
   private boolean recoverField_b = false;

   public static RecoverTypeaf2ab8ec recoverMethod_a_c696ca3a() {
      return RecoverTypeaf2ab8ec.Member8f6032a9.recoverField_a;
   }

   private RecoverTypeaf2ab8ec() {
      try {
         this.recoverField_a = new Builder().build();
      } catch (Throwable var2) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ConnectionStateMonitor", "init network request error, " + var2.getMessage());
      }
   }

   public void recoverMethod_a_f0554df3(Context var1) {
      if (var1 != null && this.recoverField_a != null && !this.recoverField_b) {
         try {
            ConnectivityManager var2 = (ConnectivityManager)var1.getSystemService("connectivity");
            if (null != var2) {
               var2.registerNetworkCallback(this.recoverField_a, this);
               this.recoverField_b = true;
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ConnectionStateMonitor", "start network listen...");
            }
         } catch (Throwable var3) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ConnectionStateMonitor", "enable connectivity error, " + var3.getMessage());
         }
      }
   }

   @Override
   public void onAvailable(Network var1) {
      try {
         String var2 = "";
         if (null != var1) {
            var2 = var1.toString();
         }

         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ConnectionStateMonitor", "[onAvailable] network is connected, netId: " + var2);
         Context var3 = RecoverTypeaf2b07c4.recoverMethod_a_6263c3eb(null);
         if (var3 == null) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ConnectionStateMonitor", "[onAvailable] context is null, can not handle network change event");
            return;
         }

         ConnectivityManager var4 = (ConnectivityManager)var3.getSystemService("connectivity");
         if (null != var4) {
            NetworkCapabilities var5 = var4.getNetworkCapabilities(var1);
            if (var5 == null || !var5.hasTransport(1) && !var5.hasTransport(0)) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ConnectionStateMonitor", "[onAvailable] capabilities:" + var5);
            } else {
               Bundle var6 = new Bundle();
               var6.putBoolean("connected", true);
               JCoreManager.onEvent(var3, "", 80, null, var6);
            }
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ConnectionStateMonitor", "[onAvailable] connectivityManager null");
         }
      } catch (Throwable var7) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ConnectionStateMonitor", "[onAvailable] error, " + var7.getMessage());
      }
   }

   @Override
   public void onLost(Network var1) {
      try {
         String var2 = "";
         if (null != var1) {
            var2 = var1.toString();
         }

         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("ConnectionStateMonitor", "[onLost] network is disconnected, netId: " + var2);
         Context var3 = RecoverTypeaf2b07c4.recoverMethod_a_6263c3eb(null);
         if (var3 == null) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ConnectionStateMonitor", "[onLost] context is null, can not handle network change event");
            return;
         }

         Bundle var4 = new Bundle();
         var4.putBoolean("connected", false);
         JCoreManager.onEvent(var3, "", 80, null, var4);
      } catch (Throwable var5) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ConnectionStateMonitor", "[onLost] error, " + var5.getMessage());
      }
   }

   private static final class Member8f6032a9 {
      private static final RecoverTypeaf2ab8ec recoverField_a = new RecoverTypeaf2ab8ec();
   }
}
