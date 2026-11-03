package cn.jiguang.k;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.text.TextUtils;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07c2;
import cn.jiguang.bk.RecoverTypeaf2b07c4;
import cn.jiguang.bk.RecoverTypeaf2b07d1;
import cn.jiguang.g.RecoverType5a69853;
import cn.jiguang.i.RecoverType5a69fd5;
import cn.jiguang.i.RecoverType5a69fd6;
import cn.jiguang.o.RecoverType5a6b65b;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import org.json.JSONObject;

public class RecoverType5a6a75d extends RecoverType5a69fd5 {
   private static boolean recoverField_c = false;
   public static String recoverField_a = ".permission.JPUSH_MESSAGE";
   private Context recoverField_d;
   private BroadcastReceiver recoverField_e;
   final ReentrantLock recoverField_b = new ReentrantLock();

   private RecoverType5a6a75d() {
   }

   public static RecoverType5a6a75d recoverMethod_a_487c05b3() {
      return RecoverType5a6a75d.Member369a48db.recoverField_a;
   }

   @Override
   protected String recoverMethod_a_e501eb0d(Context var1) {
      this.recoverField_d = var1;
      return "JDeviceScreen";
   }

   public void recoverMethod_b_f0554df3(Context var1) {
      boolean var2 = false;

      try {
         var2 = this.recoverField_b.tryLock(10L, TimeUnit.SECONDS);
         if (!recoverField_c) {
            recoverField_c = true;
            this.recoverField_e = new RecoverType5a6a75d.Member369a48da();
            String var3 = var1.getPackageName() + recoverField_a;
            IntentFilter var4 = new IntentFilter();
            var4.addAction("android.intent.action.SCREEN_ON");
            var4.addAction("android.intent.action.SCREEN_OFF");
            RecoverTypeaf2b07d1.recoverMethod_a_6169dcd2(var1, this.recoverField_e, var4, var3, null, false);
            return;
         }
      } catch (Throwable var8) {
         return;
      } finally {
         if (var2) {
            this.recoverField_b.unlock();
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JDeviceScreen", "registerReceiver hasLock false");
         }
      }
   }

   @Override
   protected void recoverMethod_c_5ffc00fd(Context var1, String var2) {
      if (!RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_e_134632(2900)) {
         try {
            this.recoverMethod_b_f0554df3(var1);
            super.recoverMethod_c_5ffc00fd(var1, var2);
         } catch (Throwable var4) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDeviceScreen", "packageJson exception: " + var4.getMessage());
         }
      }
   }

   @Override
   protected void recoverMethod_d_5ffc00fd(Context var1, String var2) {
   }

   private void recoverMethod_g_f0554df3(Context var1) {
      try {
         boolean var2 = RecoverType5a69fd6.recoverMethod_b_5ffc0101(var1, "JDeviceScreen");
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDeviceScreen", "onReceive isReport:" + var2);
         if (!var2) {
            return;
         }

         PowerManager var3 = (PowerManager)var1.getSystemService("power");
         boolean var4 = var3.isScreenOn();
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDeviceScreen", "onReceive isScreenOn:" + var4);
         KeyguardManager var5 = (KeyguardManager)var1.getSystemService("keyguard");
         boolean var6 = var5.inKeyguardRestrictedInputMode();
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDeviceScreen", "onReceive flag:" + var6);
         boolean var7 = false;
         if (var4 && !var6) {
            var7 = true;
         }

         byte var8;
         if (var4 && !var6) {
            var8 = 1;
         } else if (var4 && var6) {
            var8 = 2;
         } else {
            var8 = 3;
         }

         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDeviceScreen", "onReceive status:" + (var7 ? "开" : "锁"));
         JSONObject var9 = new JSONObject();
         var9.put("status", var7);
         var9.put("state", var8);
         String var10 = RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_a_9ba16009(var1, "", false);
         String var11 = "";
         String var12 = "";
         JSONObject var13 = RecoverType5a6b65b.recoverMethod_a_662c69af(var1, false);

         try {
            if (var13 != null) {
               var11 = var13.optString("joad");
               var12 = var13.optString("jgad");
            }
         } catch (Throwable var15) {
         }

         if (TextUtils.isEmpty(var10) && TextUtils.isEmpty(var11) && TextUtils.isEmpty(var12)) {
            var9.put("dev_id", "");
         } else {
            var9.put("dev_id", var10 + "|" + var11 + "|" + var12);
         }

         RecoverTypeaf2aa628.recoverMethod_a_d97c5eb5(var1, var9, "sc_status");
         RecoverType5a69fd6.recoverMethod_f_5ffc00fd(var1, "JDeviceScreen");
      } catch (Throwable var16) {
      }
   }

   private class Member369a48da extends BroadcastReceiver {
      private Member369a48da() {
      }

      @Override
      public void onReceive(final Context var1, Intent var2) {
         try {
            if (null == var2) {
               return;
            }

            final String var3 = var2.getAction();
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDeviceScreen", "onReceive Action:" + var3);
            RecoverTypeaf2aa628.recoverMethod_e_a4fc3973(new Runnable() {
               @Override
               public void run() {
                  try {
                     if (!RecoverTypeaf2b07c2.recoverMethod_a_9b79()) {
                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDeviceScreen", "no auth");
                        return;
                     }

                     boolean var1x = RecoverType5a69fd6.recoverMethod_a_134632(2900);
                     boolean var2x = RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_a_134632(2900);
                     if (!var2x) {
                        boolean var3x = false;

                        try {
                           var3x = RecoverType5a6a75d.this.recoverField_b.tryLock(10L, TimeUnit.SECONDS);
                           if (RecoverType5a6a75d.recoverField_c) {
                              RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDeviceScreen", "server had close");
                              BroadcastReceiver var4 = RecoverType5a6a75d.this.recoverField_e;
                              RecoverType5a6a75d.recoverField_c = false;
                              if (var4 != null) {
                                 RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d().unregisterReceiver(var4);
                              }

                              return;
                           }
                        } finally {
                           if (var3x) {
                              RecoverType5a6a75d.this.recoverField_b.unlock();
                           } else {
                              RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JDeviceScreen", "onReceive hasLock false");
                           }
                        }

                        return;
                     }

                     if ("android.intent.action.SCREEN_ON".equals(var3)) {
                        RecoverType5a681cd.recoverMethod_a_304fbba3(var1, false, 0L);
                     }

                     if (var1x) {
                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDeviceScreen", "in background,no can report");
                        return;
                     }

                     RecoverType5a6a75d.this.recoverMethod_g_f0554df3(var1);
                  } catch (Throwable var9) {
                  }
               }
            });
         } catch (Throwable var4) {
         }
      }
   }

   private static final class Member369a48db {
      private static final RecoverType5a6a75d recoverField_a = new RecoverType5a6a75d();
   }
}
