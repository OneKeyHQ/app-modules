package cn.jiguang.s;

import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.SystemClock;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.d.RecoverType5a68d10;

public class RecoverType5a6c560 {
   private static Context recoverField_a;
   private static boolean recoverField_b = false;
   private static RecoverType5a6c560 recoverField_c = null;
   private static RecoverType5a6c55f recoverField_d = null;
   private static Object recoverField_e = new Object();
   private static HandlerThread recoverField_f = null;
   private static Handler recoverField_g = null;
   private static String recoverField_h = null;
   private static String recoverField_i = null;
   private static String recoverField_j = null;
   private static String recoverField_k = null;

   private RecoverType5a6c560() {
   }

   public static RecoverType5a6c560 recoverMethod_a_97bbe9d2(Context var0) {
      if (recoverField_c == null) {
         recoverField_c = new RecoverType5a6c560();
         recoverField_a = var0;
         recoverMethod_e_9b75();
         recoverField_d = new RecoverType5a6c55f(recoverField_a);
         recoverMethod_b_f0554df3(recoverField_a);
      }

      return recoverField_c;
   }

   private void recoverMethod_b_13462e(int var1) {
      Message var2 = recoverField_g.obtainMessage();
      var2.what = 11;
      Bundle var3 = new Bundle();
      if (var1 == 0) {
         String var4 = RecoverType5a68d10.recoverMethod_c_e96ea081("ggTIEepvzybHoO4C7/s5eQ==");
         var3.putString("type", var4);
      } else if (var1 == 1) {
         String var5 = RecoverType5a68d10.recoverMethod_c_e96ea081("QlAKJNz4iTmDadRoCSAs4A==");
         var3.putString("type", var5);
      } else if (var1 == 2) {
         String var6 = RecoverType5a68d10.recoverMethod_c_e96ea081("bRmfYSLDPlLJ9XQro1+/dQ==");
         var3.putString("type", var6);
      }

      var2.setData(var3);
      recoverField_g.sendMessage(var2);
   }

   public static void recoverMethod_b_f0554df3(Context var0) {
      if (var0 == null) {
         recoverField_b = false;
      } else {
         Cursor var1 = null;
         PackageManager var2 = var0.getPackageManager();
         String var3 = RecoverType5a68d10.recoverMethod_c_e96ea081("rIjLIWp9yd1+l2BByOWYjPjGCBlvF2tDounrABnhodw=");
         if (var2 != null && var2.resolveContentProvider(var3, 0) != null) {
            String var4 = RecoverType5a68d10.recoverMethod_c_e96ea081("SyfFpc71r1BITMlIo0m1Vt2cR3sdiPGnMd0WMSsF4yU8+J95KN/jHVtZShu2ONYO");
            Uri var5 = Uri.parse(var4);

            try {
               var1 = var0.getContentResolver().query(var5, null, null, new String[]{"supported"}, null);
               if (var1 != null) {
                  var1.moveToFirst();
                  int var6 = var1.getColumnIndex("value");
                  if (var6 >= 0) {
                     String var7 = var1.getString(var6);
                     recoverField_b = "0".equals(var7);
                  } else {
                     recoverField_b = false;
                  }

                  var1.close();
               }
            } catch (Throwable var8) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("MeizuIdManager", "mz not support");
               recoverField_b = false;
            }
         } else {
            recoverField_b = false;
         }
      }

      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("MeizuIdManager", "check meizu supported:" + recoverField_b);
   }

   private static void recoverMethod_e_9b75() {
      recoverField_f = new HandlerThread("jg_mzdb_thread");
      recoverField_f.start();
      recoverField_g = new Handler(recoverField_f.getLooper()) {
         @Override
         public void handleMessage(Message var1) {
            if (var1.what == 11) {
               try {
                  RecoverType5a6c560.recoverField_h = RecoverType5a6c560.recoverField_d.recoverMethod_a_e96ea081(var1.getData().getString("type"));
               } catch (Throwable var5) {
                  RecoverType5a6c560.recoverField_h = "";
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("MeizuIdManager", "getID exception, " + var5.getMessage());
               }

               synchronized (RecoverType5a6c560.recoverField_e) {
                  RecoverType5a6c560.recoverField_e.notify();
               }
            } else {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("MeizuIdManager", "message type valid");
            }
         }
      };
   }

   public void recoverMethod_a_13462e(int var1) {
      synchronized (recoverField_e) {
         this.recoverMethod_b_13462e(var1);
         long var3 = SystemClock.uptimeMillis();

         try {
            recoverField_e.wait(2000L);
         } catch (InterruptedException var7) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("MeizuIdManager", "lock wait err: " + var7.getMessage());
         }

         if (SystemClock.uptimeMillis() - var3 < 2000L) {
            switch (var1) {
               case 0:
                  recoverField_i = recoverField_h;
                  recoverField_h = null;
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("MeizuIdManager", "query aaid: " + recoverField_i);
                  break;
               case 1:
                  if (recoverField_h == null) {
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("MeizuIdManager", "get ids-va failed");
                  } else {
                     recoverField_j = recoverField_h;
                     recoverField_h = null;
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("MeizuIdManager", "query aaid: " + recoverField_j);
                  }
                  break;
               case 2:
                  if (recoverField_h == null) {
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("MeizuIdManager", "get ids-aa failed");
                  } else {
                     recoverField_k = recoverField_h;
                     recoverField_h = null;
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("MeizuIdManager", "query aaid: " + recoverField_k);
                  }
            }
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("MeizuIdManager", "query timeout");
         }
      }
   }

   public boolean recoverMethod_a_9b79() {
      return recoverField_b;
   }

   public String recoverMethod_b_73cf11cb() {
      if (!this.recoverMethod_a_9b79()) {
         return null;
      }

      if (recoverField_i != null) {
         return recoverField_i;
      }

      this.recoverMethod_a_13462e(0);
      return recoverField_i;
   }
}
