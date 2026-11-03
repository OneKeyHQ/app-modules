package cn.jiguang.ax;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.at.RecoverTypeaf2ab531;
import java.util.concurrent.ConcurrentHashMap;

public class RecoverTypeaf2ac430 {
   private boolean recoverField_a = false;
   private ConcurrentHashMap recoverField_b = new ConcurrentHashMap();
   private Handler recoverField_c;
   private HandlerThread recoverField_d;

   private RecoverTypeaf2ac430() {
   }

   public static RecoverTypeaf2ac430 recoverMethod_a_c6982776() {
      return RecoverTypeaf2ac430.Member8f8a7ced.recoverField_a;
   }

   public synchronized void recoverMethod_a_f0554df3(Context var1) {
      if (!this.recoverField_a) {
         if (var1 == null) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TaskHandlerManager_xxx", "init context is null");
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TaskHandlerManager_xxx", "init task manager...");

            try {
               if (this.recoverField_d == null || !this.recoverField_d.isAlive()) {
                  this.recoverField_d = new HandlerThread("jg_tsk_thread") {
                     @Override
                     public void run() {
                        try {
                           super.run();
                        } catch (RuntimeException var2) {
                           RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09(
                              "TaskHandlerManager_xxx",
                              "handler thread run e:" + var2 + "  t=" + Thread.currentThread().getName() + "_" + Thread.currentThread().getId()
                           );
                        }
                     }
                  };
                  this.recoverField_d.start();
               }

               this.recoverField_c = new RecoverTypeaf2ac430.Member8f8a7cee(
                  this.recoverField_d.getLooper() == null ? Looper.getMainLooper() : this.recoverField_d.getLooper()
               );
            } catch (Exception var3) {
               this.recoverField_c = new RecoverTypeaf2ac430.Member8f8a7cee(Looper.getMainLooper());
            }

            this.recoverField_a = true;
         }
      }
   }

   public void recoverMethod_a_1a5239c(int var1, long var2, RecoverTypeaf2ac42f var4) {
      this.recoverMethod_a_f0a22443(1, var1, var2, var4);
   }

   private void recoverMethod_a_f0a22443(int var1, int var2, long var3, RecoverTypeaf2ac42f var5) {
      if (this.recoverField_c != null) {
         var5.recoverField_b = var3;
         var5.recoverField_c = var1;
         this.recoverField_b.put(var2, var5);
         if (this.recoverField_c.hasMessages(var2)) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("TaskHandlerManager_xxx", "register,same action in handler,will replace " + var2);
            this.recoverField_c.removeMessages(var2);
         }

         this.recoverField_c.sendEmptyMessageDelayed(var2, var3);
      }
   }

   public void recoverMethod_b_1a5239c(int var1, long var2, RecoverTypeaf2ac42f var4) {
      if (this.recoverField_c != null) {
         var4.recoverField_c = 2;
         this.recoverField_b.put(var1, var4);
         if (this.recoverField_c.hasMessages(var1)) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TaskHandlerManager_xxx", "sendMsg,replace:" + var1);
            this.recoverField_c.removeMessages(var1);
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("TaskHandlerManager_xxx", "sendMsg,action=" + var1);
         }

         this.recoverField_c.sendEmptyMessageDelayed(var1, var2);
      }
   }

   public boolean recoverMethod_a_134632(int var1) {
      return this.recoverField_c == null ? false : this.recoverField_c.hasMessages(var1);
   }

   public void recoverMethod_b_13462e(int var1) {
      if (this.recoverField_c != null) {
         this.recoverField_b.remove(var1);
         this.recoverField_c.removeMessages(var1);
      }
   }

   private static final class Member8f8a7ced {
      private static final RecoverTypeaf2ac430 recoverField_a = new RecoverTypeaf2ac430();
   }

   class Member8f8a7cee extends Handler {
      public Member8f8a7cee(Looper var2) {
         super(var2);
      }

      @Override
      public void handleMessage(Message var1) {
         try {
            RecoverTypeaf2ac42f var2 = (RecoverTypeaf2ac42f)RecoverTypeaf2ac430.this.recoverField_b.get(var1.what);
            if (var2 != null) {
               var2.recoverMethod_a_c050048(var1);
               if (var2.recoverField_c == 1) {
                  this.sendEmptyMessageDelayed(var1.what, RecoverTypeaf2ab531.recoverMethod_a_c6965695().recoverMethod_f_9b68() * 1000);
               } else if (var2.recoverField_c != 3) {
                  RecoverTypeaf2ac430.this.recoverField_b.remove(var1.what);
               }
            } else {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("TaskHandlerManager_xxx", "miss task:" + var1.what);
            }
         } catch (Throwable var3) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("TaskHandlerManager_xxx", "handleMessage,e:" + var3);
         }
      }
   }
}
