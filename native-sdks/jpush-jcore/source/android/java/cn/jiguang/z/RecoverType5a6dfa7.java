package cn.jiguang.z;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import java.util.concurrent.LinkedBlockingQueue;

public class RecoverType5a6dfa7 implements ServiceConnection {
   public final LinkedBlockingQueue recoverField_a = new LinkedBlockingQueue(1);

   @Override
   public void onServiceConnected(ComponentName var1, IBinder var2) {
      try {
         this.recoverField_a.put(var2);
      } catch (Throwable var4) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ZuiSericeConnection", "service connect error: " + var4.getMessage());
      }
   }

   @Override
   public void onServiceDisconnected(ComponentName var1) {
   }
}
