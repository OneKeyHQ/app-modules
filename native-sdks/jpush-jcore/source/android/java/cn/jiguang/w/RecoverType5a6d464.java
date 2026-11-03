package cn.jiguang.w;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bk.RecoverTypeaf2b07d5;

public class RecoverType5a6d464 {
   private static Context recoverField_a;
   private static boolean recoverField_b = false;
   private static RecoverType5a6d464 recoverField_c = null;
   private static RecoverType5a6d463 recoverField_d = null;
   private static Object recoverField_e = new Object();
   private static HandlerThread recoverField_f = null;
   private static Handler recoverField_g = null;
   private static String recoverField_h = null;
   private static String recoverField_i = null;
   private static String recoverField_j = null;
   private static String recoverField_k = null;

   private RecoverType5a6d464() {
   }

   public static RecoverType5a6d464 recoverMethod_a_97bdbb4e(Context var0) {
      if (recoverField_c == null) {
         recoverField_c = new RecoverType5a6d464();
         recoverField_a = var0;
         recoverMethod_f_9b75();
         recoverField_d = new RecoverType5a6d463(recoverField_a);
         recoverMethod_a_9b75();
      }

      return recoverField_c;
   }

   private void recoverMethod_b_4f708078(int var1, String var2) {
      Message var3 = recoverField_g.obtainMessage();
      var3.what = 11;
      Bundle var4 = new Bundle();
      var4.putInt("type", var1);
      if (var1 == 1 || var1 == 2) {
         var4.putString("appid", var2);
      }

      var3.setData(var4);
      recoverField_g.sendMessage(var3);
   }

   public static void recoverMethod_a_9b75() {
      recoverField_b = "1".equals(RecoverTypeaf2b07d5.recoverMethod_a_8ea82db7("persist.sys.identifierid.supported", "0"));
   }

   private static void recoverMethod_f_9b75() {
      recoverField_f = new HandlerThread("jg_vvdb_thread");
      recoverField_f.start();
      recoverField_g = new Handler(recoverField_f.getLooper()) {
         @Override
         public void handleMessage(Message var1) {
            if (var1.what == 11) {
               try {
                  RecoverType5a6d464.recoverField_h = RecoverType5a6d464.recoverField_d
                     .recoverMethod_a_dd3b7328(var1.getData().getInt("type"), var1.getData().getString("appid"));
               } catch (Throwable var5) {
                  RecoverType5a6d464.recoverField_h = "";
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("VivoIdManager", "vv getIds, exception" + var5.getMessage());
               }

               synchronized (RecoverType5a6d464.recoverField_e) {
                  RecoverType5a6d464.recoverField_e.notify();
               }
            } else {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("VivoIdManager", "message type valid");
            }
         }
      };
   }

   public void recoverMethod_a_4f708078(int var1, String var2) {
      synchronized (recoverField_e) {
         this.recoverMethod_b_4f708078(var1, var2);
         long var4 = SystemClock.uptimeMillis();

         try {
            recoverField_e.wait(2000L);
         } catch (InterruptedException var8) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("VivoIdManager", "lock wait error: " + var8.getMessage());
         }

         if (SystemClock.uptimeMillis() - var4 < 2000L) {
            switch (var1) {
               case 0:
                  recoverField_i = recoverField_h;
                  recoverField_h = null;
                  break;
               case 1:
                  if (recoverField_h == null) {
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("VivoIdManager", "get vaid failed");
                  } else {
                     recoverField_j = recoverField_h;
                     recoverField_h = null;
                  }
                  break;
               case 2:
                  if (recoverField_h == null) {
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("VivoIdManager", "get aaid failed");
                  } else {
                     recoverField_k = recoverField_h;
                     recoverField_h = null;
                  }
            }
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("VivoIdManager", "query timeout");
         }
      }
   }

   public boolean recoverMethod_b_9b79() {
      return recoverField_b;
   }

   public String recoverMethod_c_73cf11cb() {
      if (!this.recoverMethod_b_9b79()) {
         return null;
      }

      if (recoverField_i != null) {
         return recoverField_i;
      }

      this.recoverMethod_a_4f708078(0, null);
      return recoverField_i;
   }
}
