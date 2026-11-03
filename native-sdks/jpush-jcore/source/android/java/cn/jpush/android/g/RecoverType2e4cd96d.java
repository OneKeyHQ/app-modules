package cn.jpush.android.g;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import cn.asus.push.DataBuffer;
import cn.asus.push.IAIDLInvoke;
import cn.asus.push.IAIDLInvoke$Member78d0cd3b;
import cn.jpush.android.f.RecoverType2e4cd5ab;
import cn.jpush.android.i.RecoverType2e4ce0ed;

public final class RecoverType2e4cd96d extends RecoverType2e4cd96a {
   private IAIDLInvoke recoverField_b = null;

   @Override
   public final boolean recoverMethod_a_9b79() {
      if (recoverField_a.get()) {
         RecoverType2e4ce0ed.recoverMethod_b_e925fa09("ServiceConnection", "already connecting.");
         return false;
      }

      recoverField_a.set(true);

      try {
         Intent var1 = new Intent();
         ComponentName var2 = new ComponentName(RecoverType2e4cd5ab.recoverField_e, "cn.jpush.android.service.PushManagerService");
         var1.setComponent(var2);
         RecoverType2e4cd96e var5 = new RecoverType2e4cd96e(this, (byte)0);
         boolean var4 = RecoverType2e4cd5ab.recoverField_a.bindService(var1, var5, 1);
         RecoverType2e4ce0ed.recoverMethod_b_e925fa09("ServiceConnection", "connect--" + var4);
         if (!var4) {
            recoverField_a.set(false);
         } else {
            IBinder var6 = (IBinder)var5.recoverField_a.take();
            this.recoverField_b = IAIDLInvoke$Member78d0cd3b.asInterface(var6);
         }

         return var4;
      } catch (Throwable var3) {
         RecoverType2e4ce0ed.recoverMethod_g_e925fa09("ServiceConnection", "init fail:" + var3);
         recoverField_a.set(false);
         return false;
      }
   }

   @Override
   public final boolean recoverMethod_b_9b79() {
      return this.recoverField_b == null ? false : this.recoverField_b.asBinder().isBinderAlive();
   }

   @Override
   public final void recoverMethod_a_e925fa09(String var1, String var2) {
      if (this.recoverField_b != null) {
         try {
            Bundle var3;
            (var3 = new Bundle()).putString("JPUSH_CONTENT", var2);
            DataBuffer var5 = new DataBuffer(var1, var3);
            this.recoverField_b.asyncCall(var5, new RecoverType2e4cd96b());
         } catch (Throwable var4) {
            RecoverType2e4ce0ed.recoverMethod_g_e925fa09("ServiceConnection", "send data fail:" + var4);
         }
      } else {
         RecoverType2e4ce0ed.recoverMethod_g_e925fa09("ServiceConnection", "send data fail, please init first.");
      }
   }

   // $VF: synthetic method
   static IAIDLInvoke recoverMethod_a_b3f2285f(RecoverType2e4cd96d var0, IAIDLInvoke var1) {
      return var0.recoverField_b = null;
   }
}
