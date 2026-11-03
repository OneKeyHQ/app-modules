package cn.jiguang.q;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import java.util.concurrent.LinkedBlockingQueue;

public class RecoverType5a6bdde implements ServiceConnection {
   public boolean recoverField_a;
   public final LinkedBlockingQueue recoverField_b = new LinkedBlockingQueue(1);

   @Override
   public void onServiceConnected(ComponentName var1, IBinder var2) {
      try {
         this.recoverField_b.put(var2);
      } catch (Throwable var4) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("GoogleSericeConnection", "service is connect e: " + var4.getMessage());
      }
   }

   @Override
   public void onServiceDisconnected(ComponentName var1) {
   }
}
