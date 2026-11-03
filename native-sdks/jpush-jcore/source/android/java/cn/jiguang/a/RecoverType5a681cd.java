package cn.jiguang.a;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Build.VERSION;
import android.text.TextUtils;
import cn.jiguang.al.RecoverTypeaf2a9723;
import cn.jiguang.al.RecoverTypeaf2a9725;
import cn.jiguang.al.RecoverTypeaf2a9726;
import cn.jiguang.al.RecoverTypeaf2a9728;
import cn.jiguang.al.RecoverTypeaf2a972a;
import cn.jiguang.am.RecoverTypeaf2a9ae4;
import cn.jiguang.am.RecoverTypeaf2a9ae5;
import cn.jiguang.am.RecoverTypeaf2a9ae8;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.android.IDataShare;
import cn.jiguang.ap.RecoverTypeaf2aa627;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.ar.RecoverTypeaf2aadab;
import cn.jiguang.at.RecoverTypeaf2ab52c;
import cn.jiguang.at.RecoverTypeaf2ab52d;
import cn.jiguang.at.RecoverTypeaf2ab531;
import cn.jiguang.at.RecoverTypeaf2ab533;
import cn.jiguang.au.RecoverTypeaf2ab8ec;
import cn.jiguang.ay.RecoverTypeaf2ac7f1;
import cn.jiguang.ay.RecoverTypeaf2ac7f3;
import cn.jiguang.az.RecoverTypeaf2acbb8;
import cn.jiguang.az.RecoverTypeaf2acbb9;
import cn.jiguang.b.RecoverType5a6858e;
import cn.jiguang.bb.RecoverTypeaf2ae5f8;
import cn.jiguang.bb.RecoverTypeaf2ae5f9;
import cn.jiguang.bb.RecoverTypeaf2ae5fa;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bj.RecoverTypeaf2b0403;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07c2;
import cn.jiguang.bk.RecoverTypeaf2b07c4;
import cn.jiguang.bk.RecoverTypeaf2b07c6;
import cn.jiguang.bk.RecoverTypeaf2b07cb;
import cn.jiguang.bk.RecoverTypeaf2b07d1;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.dy.JDynamicLoadBase;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import cn.jiguang.e.RecoverType5a690d3;
import cn.jiguang.g.RecoverType5a69855;
import cn.jiguang.internal.JConstants;
import cn.jiguang.m.RecoverType5a6aed9;
import cn.jpush.android.service.DataShare;
import cn.jpush.android.service.JCommonService;
import cn.jpush.android.service.PushReceiver;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecoverType5a681cd {
   public static String recoverField_a = ".permission.JPUSH_MESSAGE";
   public static String recoverField_b = "5.2.0";
   public static int recoverField_c = 520;
   public static String recoverField_d = "";
   public static Boolean recoverField_e = null;
   public static Integer recoverField_f = null;
   public static boolean recoverField_g = false;
   public static final Date recoverField_h = RecoverTypeaf2b07c6.recoverMethod_c_a911e122();
   public static final String recoverField_i = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{85, 118, 97, 33, 60, 84, 32, 0, 98, 46, 73, 85, 85, 116, 18, 44}
   );
   public static List recoverField_j = new ArrayList();
   private static boolean recoverField_l;
   public static boolean recoverField_k;
   private static ServiceConnection recoverField_m = new ServiceConnection() {
      @Override
      public void onServiceConnected(ComponentName var1, final IBinder var2) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreGobal", "action - onServiceConnected, ComponentName:" + var1);

         try {
            RecoverTypeaf2aa628.recoverMethod_e_a4fc3973(new RecoverTypeaf2b0401("onServiceConnected-" + var1) {
               @Override
               public void recoverMethod_a_9b75() {
                  try {
                     RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("JCoreGobal", "Remote Service bind success.");
                     IDataShare var1x = IDataShare.Membereeaed960.asInterface(var2);
                     String var2x = RecoverTypeaf2a9726.recoverMethod_a_e501eb0d(RecoverTypeaf2b07c4.recoverMethod_a_6263c3eb(null));
                     DataShare.init(var1x, var2x);
                     RecoverType5a681cd.recoverMethod_d_9b75();
                  } catch (Throwable var3) {
                     RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JCoreGobal", "onServiceConnected run e:" + var3);
                  }
               }
            });
         } catch (Throwable var4) {
            RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JCoreGobal", "onServiceConnected e:" + var4);
         }
      }

      @Override
      public void onServiceDisconnected(ComponentName var1) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreGobal", "action - onServiceDisconnected, ComponentName:" + var1);
         RecoverType5a681cd.recoverField_k = false;
      }
   };

   public static synchronized boolean recoverMethod_a_69c77405(Context var0, Bundle var1) {
      if (recoverField_e != null) {
         return recoverField_e;
      }

      if (var0 == null) {
         RecoverTypeaf2a9ae9.recoverMethod_m_e925fa09("JCoreGobal", "init failed,context is null");
         return false;
      }

      recoverMethod_d_f0554df3(var0);
      RecoverTypeaf2b07c4.recoverMethod_a_6263c3eb(var0);
      RecoverTypeaf2a9ae9.recoverMethod_h_e925fa09(
         "JCoreGobal", "main process init - jcore, SdkVersion:" + recoverField_b + ", build id:" + 134 + " build type:" + "google_play"
      );
      var0 = var0.getApplicationContext();
      RecoverTypeaf2a9725.recoverMethod_a_c692b321();
      String var2 = RecoverTypeaf2a9726.recoverMethod_b_e501eb0d(var0);
      if ((RecoverTypeaf2a9725.recoverMethod_a_c692b321().recoverMethod_g_9b79() || RecoverTypeaf2a9725.recoverMethod_a_c692b321().recoverMethod_f_9b79())
         && TextUtils.isEmpty(var2)) {
         recoverField_e = false;
         RecoverTypeaf2a9ae9.recoverMethod_n_e925fa09(
            "JCoreGobal",
            "AndroidManifest.xml missing required service:"
               + JCommonService.class.getCanonicalName()
               + ",please custom one service and extends JCommonService. Otherwise no service"
         );
         return false;
      }

      try {
         RecoverType5a6858e.recoverMethod_a_4877eda2().recoverMethod_b_9b75();
      } catch (Throwable var4) {
      }

      RecoverTypeaf2acbb9.recoverMethod_a_f0554df3(var0);
      recoverMethod_c_69c77401(var0, var1);
      recoverMethod_e_69c77401(var0, var1);
      recoverField_e = true;
      return recoverField_e;
   }

   private static void recoverMethod_c_69c77401(Context var0, Bundle var1) {
      String var2 = RecoverTypeaf2a9726.recoverMethod_b_e501eb0d(var0);
      if (TextUtils.isEmpty(var2)) {
         RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("JCoreGobal", "not found commonServiceClass（JCommonService）");
      } else {
         Intent var3 = null;

         try {
            var3 = new Intent(var0.getApplicationContext(), Class.forName(var2));
            if (var1 != null) {
               var3.putExtras(var1);
            }

            var0.startService(var3);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreGobal", "start common service");
         } catch (Throwable var5) {
         }
      }
   }

   public static boolean recoverMethod_a_9b79() {
      return recoverField_e != null && recoverField_e || recoverField_f != null && recoverField_f == 0;
   }

   public static synchronized int recoverMethod_a_f0554de6(Context var0) {
      if (recoverField_f != null) {
         return recoverField_f;
      }

      if (var0 == null) {
         RecoverTypeaf2a9ae9.recoverMethod_m_e925fa09("JCoreGobal", "init failed, context is null");
         return 1000;
      }

      recoverMethod_d_f0554df3(var0);
      if (RecoverTypeaf2b07c1.recoverMethod_b_f0554df7(var0)) {
         recoverMethod_d_9b75();
      }

      RecoverTypeaf2a9ae9.recoverMethod_h_e925fa09(
         "JCoreGobal", "service process init - jcore, SdkVersion:" + recoverField_b + ", build id:" + 134 + " build type:" + "google_play"
      );
      RecoverTypeaf2b07c4.recoverMethod_a_6263c3eb(var0);
      var0 = var0.getApplicationContext();
      int var1 = RecoverTypeaf2a9723.recoverMethod_e_f0554de6(var0);
      if (var1 != 0) {
         return var1;
      }

      var1 = RecoverTypeaf2a9723.recoverMethod_f_f0554de6(var0);
      if (var1 != 0) {
         recoverField_f = -1;
         return var1;
      }

      RecoverTypeaf2a9725.recoverMethod_a_c692b321();
      recoverMethod_e_f0554df3(var0);
      recoverField_f = 0;
      if (VERSION.SDK_INT >= 21) {
         RecoverTypeaf2ab8ec.recoverMethod_a_c696ca3a().recoverMethod_a_f0554df3(var0);
      }

      return recoverField_f;
   }

   public static void recoverMethod_b_f0554df3(Context var0) {
      if (!recoverField_l) {
         recoverField_l = true;
         RecoverTypeaf2aa627.recoverMethod_a_f0554df3(var0);
         if (recoverMethod_b_9b79()) {
            try {
               JDynamicLoadBase.getInstance().updatePluin(RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d(), 1, "");
            } catch (Throwable var2) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCoreGobal", "u p call failed");
            }
         }

         RecoverTypeaf2acbb9.recoverMethod_a_f0554df3(var0);
         RecoverTypeaf2acbb8.recoverMethod_a_c69910ee().recoverMethod_b_f0554df3(var0);
         RecoverType5a6858e.recoverMethod_a_4877eda2().recoverMethod_e_f0554df3(var0);
         if (!(Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_an_48794abf())) {
            RecoverTypeaf2a9ae5.recoverMethod_b_f0554df3(var0);
         }

         RecoverTypeaf2b07c2.recoverMethod_a_f0554df3(var0);
         RecoverTypeaf2ae5f9.recoverMethod_a_c69c3ecd().recoverMethod_b_9b75();
         RecoverTypeaf2ae5fa.recoverMethod_a_c69c3eec().recoverMethod_b_9b75();
         RecoverTypeaf2ae5f8.recoverMethod_a_c69c3eae().recoverMethod_b_9b75();
      }
   }

   public static boolean recoverMethod_b_9b79() {
      return false;
   }

   private static void recoverMethod_e_f0554df3(Context var0) {
      try {
         IntentFilter var1 = new IntentFilter();
         PushReceiver var2 = new PushReceiver();
         if (VERSION.SDK_INT < 28) {
            var1.addAction("android.net.conn.CONNECTIVITY_CHANGE");
         }

         String var3 = var0.getPackageName() + recoverField_a;
         RecoverTypeaf2b07d1.recoverMethod_a_6169dcd2(var0, var2, var1, var3, null, false);
      } catch (Throwable var4) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JCoreGobal", "registerPushReceiver fail:" + var4);
      }
   }

   public static void recoverMethod_a_d19840b7(Context var0, String var1, Bundle var2) {
      try {
         var0 = RecoverTypeaf2b07c4.recoverMethod_a_6263c3eb(var0);
         String var3 = RecoverTypeaf2a9726.recoverMethod_b_e501eb0d(var0);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreGobal", "serviceName is " + var3);
         if ((RecoverTypeaf2a9725.recoverMethod_a_c692b321().recoverMethod_g_9b79() || RecoverTypeaf2a9725.recoverMethod_a_c692b321().recoverMethod_f_9b79())
            && TextUtils.isEmpty(var3)) {
            RecoverTypeaf2a9ae9.recoverMethod_n_e925fa09(
               "JCoreGobal",
               "AndroidManifest.xml missing required service:"
                  + JCommonService.class.getCanonicalName()
                  + ",please custom one service and extends JCommonService. Otherwise no service"
            );
            return;
         }

         String var4 = RecoverTypeaf2b07c1.recoverMethod_a_e501eb0d(var0);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreGobal", "curProcessName is " + var4);
         if (TextUtils.isEmpty(var3)) {
            if (var0.getPackageName().equals(var4)) {
               recoverMethod_b_d19840b7(var0, var1, var2);
            }
         } else {
            String var5 = RecoverTypeaf2a9726.recoverMethod_a_e501eb0d(var0);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreGobal", "curProcessName is " + var4 + ", remoteProcessName is " + var5);
            if (!TextUtils.isEmpty(var3) && var0.getPackageName().equals(var4)) {
               RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("MAIN_IPC_ACTION_TASK", new RecoverType5a681cd.Member360d474a(var0, false, var1, var2));
            } else if (!TextUtils.isEmpty(var4) && var4.equals(var5)) {
               recoverMethod_b_d19840b7(var0, var1, var2);
            }
         }
      } catch (Throwable var6) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCoreGobal", "sendToServiceAction failed, " + var6.getMessage());
      }
   }

   public static void recoverMethod_b_d19840b7(Context var0, String var1, Bundle var2) {
      RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("IPC_ACTION_TASK", new RecoverType5a681cd.Member360d474a(var0, true, var1, var2));
   }

   private static void recoverMethod_d_69c77401(Context var0, Bundle var1) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreGobal", "setCountry...bundle=" + var1);
      Long var2 = RecoverTypeaf2aadab.recoverMethod_c_f0554de7(var0);
      String var3 = var1.getString("sdk_type");
      if (TextUtils.isEmpty(var3)) {
         var3 = "JCore";
      }

      String var4 = var1.getString("set_country");
      if (TextUtils.isEmpty(var4)) {
         RecoverTypeaf2a9ae9.recoverMethod_m_e925fa09("JCoreGobal", "setCountry is empty");
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("JCoreGobal", "setCountry - action:" + var4);
         RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_K_48794abf().recoverMethod_a_4e9722a3(var4));
         RecoverTypeaf2ac7f1 var5 = new RecoverTypeaf2ac7f1(8192);
         var5.recoverMethod_a_aad3b1ff(var4);
         byte[] var6 = var5.recoverMethod_b_12d408();
         RecoverTypeaf2ab533.recoverMethod_a_c69656d3().recoverMethod_a_9e326362(var0, var2, 41, 1, var6, var3, 10000L);
      }
   }

   public static void recoverMethod_a_304fbba3(Context var0, boolean var1, long var2) {
      recoverMethod_a_1068f372(var0, var1, var2, 0L, 0, false);
   }

   public static void recoverMethod_a_1068f372(Context var0, boolean var1, long var2, long var4, int var6, boolean var7) {
      try {
         Bundle var8 = new Bundle();
         var8.putBoolean("force", var1);
         var8.putLong("delay_time", var2);
         if (var7) {
            if (var4 > RecoverTypeaf2ab531.recoverField_d || var4 < 3000L) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
                  "JCoreGobal", "set  heartbeatTimeOut=" + var4 + " need force change to default value=" + RecoverTypeaf2ab531.recoverField_d
               );
               var4 = RecoverTypeaf2ab531.recoverField_d;
            }

            if (var6 > RecoverTypeaf2ab531.recoverField_c || var6 < 1) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
                  "JCoreGobal", "set  heartbeatTimeOutCount=" + var6 + " need force change to default value=" + RecoverTypeaf2ab531.recoverField_c
               );
               var6 = RecoverTypeaf2ab531.recoverField_c;
            }

            var8.putInt("heartbeat_time_out_count", var6);
            var8.putLong("heartbeat_time_out", var4);
         }

         RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("JCoreGobal", " sendRtcToTcp  bundle=" + var8);
         recoverMethod_a_d19840b7(var0, "tcp_a2", var8);
      } catch (Throwable var9) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JCoreGobal", "sendHeartBeat error:" + var9);
      }
   }

   private static void recoverMethod_e_69c77401(Context var0, Bundle var1) {
      String var2 = RecoverTypeaf2a9726.recoverMethod_b_e501eb0d(var0);
      if (TextUtils.isEmpty(var2)) {
         RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("JCoreGobal", "not found commonServiceClass（JCommonService）");
      } else if (DataShare.isBinding()) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreGobal", "is binding service");
      } else {
         try {
            Intent var3 = new Intent();
            if (var1 != null) {
               var3.putExtras(var1);
            }

            var3.setClass(var0, Class.forName(var2));
            if (var0.bindService(var3, recoverField_m, 1)) {
               RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("JCoreGobal", "Remote Service on binding...");
               DataShare.setBinding();
            } else {
               RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("JCoreGobal", "Remote Service bind failed");
            }
         } catch (SecurityException var4) {
            RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JCoreGobal", "Remote Service bind failed caused by SecurityException!");
         } catch (Throwable var5) {
            RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JCoreGobal", "Remote Service bind failed :" + var5);
         }
      }
   }

   public static void recoverMethod_c_f0554df3(Context var0) {
      try {
         RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("JCoreGobal", "unRegisterCommonService start");
         var0.unbindService(recoverField_m);
         String var1 = RecoverTypeaf2a9726.recoverMethod_b_e501eb0d(var0);
         if (TextUtils.isEmpty(var1)) {
            RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("JCoreGobal", "not found commonServiceClass（JCommonService）");
            return;
         }

         Intent var2 = new Intent("cn.jiguang.user.service.action");
         var2.setClassName(var0, var1);
         var2.setPackage(var0.getPackageName());
         var0.stopService(var2);
         RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("JCoreGobal", "unRegisterCommonService end");
      } catch (Throwable var3) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JCoreGobal", "unregister service failed, " + var3.getMessage());
      }
   }

   private static void recoverMethod_d_9b75() {
      recoverField_k = true;
      RecoverType5a68d10.recoverMethod_a_58872b9c().sendCurrentState();
   }

   public static void recoverMethod_d_f0554df3(Context var0) {
      if (var0 != null) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreGobal", "setInternalDebug");

         try {
            if ((Integer)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_aA_48794abf()) == 1) {
               if (System.currentTimeMillis() > (Long)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_aC_48794abf())) {
                  RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_aA_48794abf().recoverMethod_a_4e9722a3(0));
                  RecoverType5a690d3.recoverMethod_b_1a54e370(var0, 2);
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreGobal", "setInternalDebug is expire!");
                  RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("FUTURE_TASK", new RecoverTypeaf2b0401() {
                     @Override
                     public void recoverMethod_a_9b75() {
                        RecoverTypeaf2a9ae8.recoverMethod_a_9b75();
                     }
                  });
               } else {
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreGobal", "setInternalDebug open");
                  recoverField_g = true;
               }
            } else {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreGobal", "setInternalDebug close and delete log file");
               RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("FUTURE_TASK", new RecoverTypeaf2b0401() {
                  @Override
                  public void recoverMethod_a_9b75() {
                     RecoverTypeaf2a9ae8.recoverMethod_a_9b75();
                  }
               });
            }

            if ((Integer)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_aB_48794abf()) == 1) {
               if (System.currentTimeMillis() > (Long)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_aC_48794abf())) {
                  RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_aB_48794abf().recoverMethod_a_4e9722a3(0));
                  RecoverType5a690d3.recoverMethod_b_1a54e370(var0, 2);
                  JConstants.CMD_TO_PRINT_ALL_LOG = false;
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreGobal", "setInternalDebug is expire!");
               } else {
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreGobal", "setInternalDebug open");
                  JConstants.CMD_TO_PRINT_ALL_LOG = true;
               }
            }
         } catch (Throwable var2) {
         }
      }
   }

   static {
      recoverField_j.add("a7");
      recoverField_j.add("a8");
      recoverField_j.add("set_auth");
      recoverField_j.add("set_wake_enable");
      recoverField_j.add("a4");
   }

   static class Member360d474a extends RecoverTypeaf2b0401 {
      Context recoverField_a;
      boolean recoverField_b;
      String recoverField_c;
      Bundle recoverField_d;

      Member360d474a(Context var1, boolean var2, String var3, Bundle var4) {
         this.recoverField_a = var1;
         this.recoverField_b = var2;
         this.recoverField_c = var3;
         this.recoverField_d = var4;
         this.recoverField_h = "JCoreGlobal";
      }

      @Override
      public void recoverMethod_a_9b75() {
         try {
            this.recoverMethod_a_79e5e33f(this.recoverField_d);
            if (!RecoverTypeaf2b07c2.recoverMethod_a_9b79() && !RecoverType5a681cd.recoverField_j.contains(this.recoverField_c)) {
               RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JCoreGobal", "user don't auth, so return init");
               return;
            }

            if (this.recoverField_b) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
                  "JCoreGobal",
                  "inService do action: "
                     + this.recoverField_c
                     + ", "
                     + RecoverTypeaf2a9728.recoverMethod_a_79e5e333(this.recoverField_d)
                     + ", bundle="
                     + this.recoverField_d
               );
               if (this.recoverField_c.equals("a5")) {
                  RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_a_4abe089d(this.recoverField_a, 2001, 1, "");
                  RecoverType5a690d2.recoverMethod_a_abcb8d40(this.recoverField_a, RecoverType5a690d1.recoverMethod_Q_48794abf().recoverMethod_a_4e9722a3(true));
                  String var1 = this.recoverField_d.getString("appkey");
                  if (TextUtils.isEmpty(var1)) {
                     RecoverTypeaf2ab52c.recoverMethod_a_c69655fa()
                        .recoverMethod_a_4abe089d(this.recoverField_a, 0, 10003, RecoverTypeaf2ac7f3.recoverMethod_a_47921032(10003));
                     return;
                  }

                  String var2 = RecoverTypeaf2a9723.recoverMethod_b_e501eb0d(this.recoverField_a);
                  if (!TextUtils.isEmpty(var2) && !var2.equals(var1)) {
                     RecoverTypeaf2ab52c.recoverMethod_a_c69655fa()
                        .recoverMethod_a_4abe089d(this.recoverField_a, 0, 10002, RecoverTypeaf2ac7f3.recoverMethod_a_47921032(10002));
                     return;
                  }

                  RecoverTypeaf2b07c1.recoverMethod_f_f0554df3(this.recoverField_a);
                  JConstants.APP_KEY = var1;
                  boolean var3 = RecoverTypeaf2ab52d.recoverMethod_a_f0554df7(this.recoverField_a);
                  if (var3) {
                     this.recoverField_c = "tcp_a24";
                  } else {
                     String var4 = RecoverType5a68d10.recoverMethod_b_e501eb0d(this.recoverField_a);
                     if (!TextUtils.isEmpty(var4)) {
                        RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_a_4abe089d(this.recoverField_a, 2001, 0, var4);
                     }

                     this.recoverField_c = "tcp_a1";
                  }
               } else if (this.recoverField_c.equals("tcp_a23")) {
                  RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_a_4abe089d(this.recoverField_a, 2000, 0, "success");
               } else if (this.recoverField_c.equals("a6")) {
                  RecoverTypeaf2a972a.recoverMethod_a_f0554df7(this.recoverField_a);
               } else if (TextUtils.equals(this.recoverField_c, "a8")) {
                  if (null != this.recoverField_d) {
                     boolean var6 = this.recoverField_d.getBoolean("enable_auto_wakeup");
                     RecoverTypeaf2b07c2.recoverMethod_d_1a552341(this.recoverField_a, var6);
                  }
               } else if (TextUtils.equals(this.recoverField_c, "a11")) {
                  if (null != this.recoverField_d) {
                     boolean var7 = this.recoverField_d.getBoolean("enable_ci_priority");
                     RecoverTypeaf2b07c2.recoverMethod_f_1a552341(this.recoverField_a, var7);
                  }
               } else if (TextUtils.equals(this.recoverField_c, "a9")) {
                  if (null != this.recoverField_d) {
                     boolean var8 = this.recoverField_d.getBoolean("enable_dy");
                     RecoverTypeaf2b07c2.recoverMethod_e_1a552341(this.recoverField_a, var8);
                  }
               } else if (this.recoverField_c.equals("a7")) {
                  RecoverType5a69855.recoverMethod_a_487a33bb().recoverMethod_a_79e5e33f(this.recoverField_d);
               } else if (this.recoverField_c.equals("a10")) {
                  RecoverType5a6aed9.recoverMethod_a_b00c10b9(this.recoverField_a, "GET_ANDROID_ID", this.recoverField_d);
               } else if (this.recoverField_c.equals("tcp_set_country")) {
                  RecoverType5a681cd.recoverMethod_d_69c77401(this.recoverField_a, this.recoverField_d);
               } else if (TextUtils.equals(this.recoverField_c, "a13") && null != this.recoverField_d) {
                  RecoverTypeaf2a9ae4.recoverMethod_a_69c77401(this.recoverField_a, this.recoverField_d);
               }

               int var9 = RecoverType5a681cd.recoverMethod_a_f0554de6(this.recoverField_a);
               if (var9 == 0) {
                  RecoverTypeaf2a9726.recoverMethod_a_c692b340().recoverMethod_b_d19840b7(this.recoverField_a, this.recoverField_c, this.recoverField_d);
               } else if (var9 > 0) {
                  RecoverTypeaf2ab52c.recoverMethod_a_c69655fa()
                     .recoverMethod_a_4abe089d(this.recoverField_a, 2002, var9, RecoverTypeaf2ac7f3.recoverMethod_a_47921032(var9));
               }
            } else if (RecoverType5a681cd.recoverMethod_a_69c77405(this.recoverField_a, this.recoverField_d)) {
               this.recoverField_d = this.recoverMethod_b_bd038e43(this.recoverField_d);
               this.recoverField_d = RecoverTypeaf2a9728.Member8ee171e7.recoverMethod_a_21dbd0dd(this.recoverField_a)
                  .recoverMethod_a_bd038e43(this.recoverField_d);
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
                  "JCoreGobal",
                  "do action: "
                     + this.recoverField_c
                     + ", "
                     + RecoverTypeaf2a9728.recoverMethod_a_79e5e333(this.recoverField_d)
                     + ", bundle="
                     + this.recoverField_d
               );
               RecoverTypeaf2a9726.recoverMethod_a_c692b340().recoverMethod_a_d19840b7(this.recoverField_a, this.recoverField_c, this.recoverField_d);
            }
         } catch (Throwable var5) {
            RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JCoreGobal", "do action error: action: " + this.recoverField_c + " bundle=" + this.recoverField_d);
            RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JCoreGobal", "do action error:" + var5.getMessage());
         }
      }

      private void recoverMethod_a_79e5e33f(Bundle var1) {
         if (null != var1) {
            boolean var2 = var1.containsKey("is_auth");
            if (var2) {
               boolean var3 = var1.getBoolean("is_auth", true);
               boolean var4 = var1.getBoolean("had_auth", true);
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreGobal", "setAuthFormBundle：auth=" + var3 + " hadAuth=" + var4);
               RecoverTypeaf2b07c2.recoverMethod_a_25decb5(var3, var4);
            }

            if (var1.containsKey("had_init")) {
               boolean var5 = var1.getBoolean("had_init");
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreGobal", "setAuthFormBundle hadInit =" + var5);
               JConstants.isCallInit.set(var5);
            }

            if (var1.containsKey("foreground")) {
               boolean var6 = var1.getBoolean("foreground");
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JCoreGobal", "isForeGround  =" + var6);
               RecoverTypeaf2aa628.recoverField_b.set(var6);
            }
         }
      }

      private Bundle recoverMethod_b_bd038e43(Bundle var1) {
         if (null == var1) {
            var1 = new Bundle();
         }

         var1.putBoolean("is_auth", RecoverTypeaf2b07c2.recoverMethod_a_9b79());
         var1.putBoolean("had_auth", RecoverTypeaf2b07c2.recoverMethod_b_9b79());
         var1.putBoolean("had_init", JConstants.isCallInit.get());
         var1.putBoolean("foreground", RecoverTypeaf2aa628.recoverField_b.get());
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
            "JCoreGobal",
            "addAuthToBundle hadAuth="
               + RecoverTypeaf2b07c2.recoverMethod_b_9b79()
               + " auth="
               + RecoverTypeaf2b07c2.recoverMethod_a_9b79()
               + "  isCallInit="
               + JConstants.isCallInit.get()
               + " isForeGround="
               + RecoverTypeaf2aa628.recoverField_b.get()
         );
         return var1;
      }
   }
}
