package cn.jpush.android.g;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import cn.jpush.android.i.RecoverType2e4ce0ed;
import java.util.concurrent.LinkedBlockingQueue;

final class RecoverType2e4cd96e implements ServiceConnection {
   public final LinkedBlockingQueue recoverField_a;
   // $VF: synthetic field
   final RecoverType2e4cd96d recoverField_b;

   private RecoverType2e4cd96e(RecoverType2e4cd96d var1) {
      this.recoverField_b = var1;
      this.recoverField_a = new LinkedBlockingQueue(1);
   }

   @Override
   public final void onServiceConnected(ComponentName var1, IBinder var2) {
      try {
         RecoverType2e4cd96a.recoverField_a.set(false);
         RecoverType2e4ce0ed.recoverMethod_b_e925fa09("ServiceConnection", "onServiceConnected:" + var1);
         if (var2 == null) {
            RecoverType2e4ce0ed.recoverMethod_g_e925fa09("ServiceConnection", "onServiceConnected, but IBinder is null.");
            return;
         }

         this.recoverField_a.put(var2);
      } catch (Throwable var3) {
      }
   }

   @Override
   public final void onServiceDisconnected(ComponentName var1) {
      RecoverType2e4cd96a.recoverField_a.set(false);
      RecoverType2e4cd96d.recoverMethod_a_b3f2285f(this.recoverField_b, null);
      RecoverType2e4ce0ed.recoverMethod_g_e925fa09("ServiceConnection", "onServiceDisconnected:" + var1);
   }

   // $VF: synthetic method
   RecoverType2e4cd96e(RecoverType2e4cd96d var1, byte var2) {
      this(var1);
   }
}
