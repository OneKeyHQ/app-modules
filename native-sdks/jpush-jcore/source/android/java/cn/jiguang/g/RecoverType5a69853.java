package cn.jiguang.g;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Bundle;
import android.util.ArrayMap;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.bh.RecoverTypeaf2afc7e;
import cn.jiguang.bk.RecoverTypeaf2b07c2;
import cn.jiguang.bk.RecoverTypeaf2b07c4;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.d.RecoverType5a68d11;
import cn.jiguang.i.RecoverType5a69fd6;
import cn.jiguang.j.RecoverType5a6a396;
import cn.jiguang.j.RecoverType5a6a398;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

@TargetApi(19)
public class RecoverType5a69853 {
   private Map recoverField_c;
   public static Map recoverField_a = new ArrayMap();
   private static Map recoverField_d = new ArrayMap();
   private static ArrayList recoverField_e = new ArrayList();
   private static Map recoverField_f = new ArrayMap();
   private static ArrayList recoverField_g = new ArrayList() {
      {
         this.add(2000);
         this.add(2002);
         this.add(2008);
         this.add(2009);
         this.add(2010);
         this.add(2011);
         this.add(2012);
         this.add(1014);
         this.add(1009);
         this.add(1015);
      }
   };
   private static ArrayList recoverField_h = new ArrayList();
   private static ArrayList recoverField_i = new ArrayList();
   public ArrayList recoverField_b;
   private boolean recoverField_j = false;
   private Context recoverField_k = RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d();

   private RecoverType5a69853() {
      this.recoverField_b = new ArrayList();
      this.recoverField_c = new ArrayMap();
      this.recoverMethod_b_9b75();
   }

   public static RecoverType5a69853 recoverMethod_a_487a337d() {
      return RecoverType5a69853.Member3661d450.recoverField_a;
   }

   public void recoverMethod_b_9b75() {
      if (!this.recoverField_j) {
         RecoverTypeaf2aa628.recoverMethod_d_a4fc3973(new Runnable() {
            @Override
            public void run() {
               RecoverType5a6a398.recoverMethod_b_487b90d8().recoverMethod_c_f0554df3(RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d());
            }
         });
         this.recoverField_j = true;
      }
   }

   public boolean recoverMethod_a_134632(int var1) {
      return this.recoverMethod_a_256326a(var1, true);
   }

   private boolean recoverMethod_g_134632(int var1) {
      return var1 >= 2000 && var1 < 2300 && var1 != 2004 && var1 != 2005 && var1 != 2007;
   }

   public boolean recoverMethod_a_256326a(int var1, boolean var2) {
      boolean var3 = false;
      boolean var4 = false;

      try {
         boolean var5 = RecoverType5a69855.recoverMethod_a_487a33bb().recoverMethod_a_134632(var1);
         boolean var6 = RecoverType5a69855.recoverMethod_a_487a33bb().recoverMethod_b_134632(var1);
         if (this.recoverField_c == null || this.recoverField_c.size() == 0) {
            if (var2) {
               if (var5) {
                  RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_255f295(var1, 2003);
                  return true;
               }

               if (var6) {
                  RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_255f295(var1, 2010);
                  return false;
               }
            }

            if (recoverField_h.contains(var1)) {
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_255f295(var1, 2011);
               RecoverTypeaf2a9ae9.recoverMethod_a_6ef37c42(var1, "JDataConfigManager", "config map is null, noMapWhiteIDs contains " + var1 + "enable");
               return true;
            }

            RecoverTypeaf2a9ae9.recoverMethod_a_6ef37c42(
               var1, "JDataConfigManager", "config map is null, config " + var1 + (var1 >= 2000 ? "enable" : " not enable")
            );
            boolean var19 = this.recoverMethod_g_134632(var1);
            if (var19) {
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_255f295(var1, 2008);
            } else {
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_255f295(var1, 2009);
            }

            return var19;
         }

         if (recoverField_e.contains(var1)) {
            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_255f295(var1, 2005);
            return true;
         }

         Integer[] var7 = (Integer[])recoverField_f.get(var1);
         if (var7 != null && var7.length > 0) {
            Integer[] var8 = var7;
            int var9 = var8.length;

            for (int var10 = 0; var10 < var9; var10++) {
               int var11 = var8[var10];
               if (this.recoverMethod_a_134632(var11)) {
                  RecoverType5a6a396 var12 = (RecoverType5a6a396)this.recoverField_c.get(var11);
                  if (var12 != null) {
                     var12.recoverMethod_a_13462e(var1);
                     if (!this.recoverField_c.containsKey(var1)) {
                        this.recoverField_c.put(var1, var12);
                     }
                  }

                  RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_255f295(var1, 2012);
                  return true;
               }
            }
         }

         RecoverType5a6a396 var20 = (RecoverType5a6a396)this.recoverField_c.get(var1);
         if (var20 != null) {
            var3 = var20.recoverMethod_b_9b79();
         }

         long var21 = System.currentTimeMillis();
         SimpleDateFormat var22 = new SimpleDateFormat("HH:mm:ss");
         int var23 = 0;

         while (true) {
            if (var23 < this.recoverField_b.size()) {
               String[] var13 = (String[])this.recoverField_b.get(var23);
               if (this.recoverMethod_a_ad2a454a(var22.parse(var13[1])) <= var21 || var21 <= this.recoverMethod_a_ad2a454a(var22.parse(var13[0]))) {
                  var23++;
                  continue;
               }

               var4 = true;
            }

            try {
               if (var20 != null) {
                  var23 = var20.recoverMethod_d_9b68() * 1000;
                  long var25 = RecoverType5a68d11.recoverMethod_b_f0554de7(this.recoverField_k) * 1000L;
                  long var15 = RecoverType5a68d10.recoverMethod_g_f0554de7(this.recoverField_k);
                  if (0L == var15) {
                     if (this.recoverMethod_g_134632(var1)) {
                        var4 = true;
                     } else {
                        var4 = false;
                     }
                  } else if (Math.abs(var25 - var15) >= var23) {
                     var4 = true;
                  } else {
                     var4 = false;
                  }

                  RecoverTypeaf2a9ae9.recoverMethod_a_6ef37c42(
                     var1,
                     "JDataConfigManager",
                     "config "
                        + var1
                        + " registerProtectionTime="
                        + var23
                        + " serverTime="
                        + var15
                        + " currentTime="
                        + var25
                        + " ,protect time check enable=:"
                        + var4
                  );
               }
            } catch (Throwable var17) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDataConfigManager", " throwable=" + var17);
            }
            break;
         }

         if (!var2) {
            RecoverTypeaf2a9ae9.recoverMethod_a_6ef37c42(
               var1, "JDataConfigManager", "config " + var1 + " serviceEnable=" + var3 + " ,protect time check enable=:" + var4
            );
            if (var3) {
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_255f295(var1, 2013);
               if (var4) {
                  RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_255f295(var1, 2015);
               } else {
                  RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_255f295(var1, 2016);
               }

               return var4;
            }

            RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_255f295(var1, 2014);
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_a_6ef37c42(
               var1,
               "JDataConfigManager",
               "config "
                  + var1
                  + " serviceEnable="
                  + var3
                  + " ,use set enable list contain="
                  + var5
                  + " ,use set disable list contain="
                  + var6
                  + " ,protect time check enable=:"
                  + var4
            );
            if (var3) {
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_255f295(var1, 2013);
               if (var5) {
                  RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_255f295(var1, 2017);
               }

               if (var6) {
                  RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_255f295(var1, 2004);
               }

               if (var5 || !var6) {
                  if (var4) {
                     RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_255f295(var1, 2015);
                  } else {
                     RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_255f295(var1, 2016);
                  }

                  return var4;
               }
            } else {
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_255f295(var1, 2014);
            }
         }
      } catch (Throwable var18) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDataConfigManager", "check config enable error:" + var18);
      }

      RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_255f295(var1, 2018);
      return false;
   }

   private long recoverMethod_a_ad2a454a(Date var1) {
      try {
         if (var1 == null) {
            return 0L;
         }

         Date var2 = new Date();
         var2.setHours(var1.getHours());
         var2.setMinutes(var1.getMinutes());
         var2.setSeconds(var1.getSeconds());
         return var2.getTime() / 1000L * 1000L;
      } catch (Exception var3) {
         return 0L;
      }
   }

   public void recoverMethod_a_18e5f6ce(RecoverType5a6a396 var1) {
      try {
         if (var1 == null) {
            return;
         }

         if (this.recoverField_c == null) {
            this.recoverField_c = new ArrayMap();
         }

         int var2 = var1.recoverMethod_a_9b68();
         if (!this.recoverField_c.containsKey(var2)) {
            this.recoverField_c.put(var2, var1);
         }

         if (var2 < 2000) {
            int var3 = var2 / 100 * 100;
            if (recoverField_i.contains(var3)) {
               return;
            }

            if (!this.recoverField_c.containsKey(var3)) {
               RecoverType5a6a396 var4 = new RecoverType5a6a396();
               var4.recoverMethod_a_13462e(var3);
               var4.recoverMethod_a_1385ff(true);
               var4.recoverMethod_b_13462e(var1.recoverMethod_c_9b68());
               var4.recoverMethod_e_13462e(var1.recoverMethod_f_9b68());
               var4.recoverMethod_d_13462e(var1.recoverMethod_e_9b68());
               var4.recoverMethod_f_13462e(var1.recoverMethod_g_9b68());
               var4.recoverMethod_g_13462e(var1.recoverMethod_h_9b68());
               var4.recoverMethod_h_13462e(var1.recoverMethod_i_9b68());
               var4.recoverMethod_c_13462e(var1.recoverMethod_d_9b68());
               this.recoverField_c.put(var3, var4);
            }
         }
      } catch (Throwable var5) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDataConfigManager", "addConfig failed :" + var5);
      }
   }

   public void recoverMethod_c_9b75() {
      this.recoverField_b = new ArrayList();
      this.recoverField_c = new ArrayMap();
   }

   public synchronized int recoverMethod_b_134621(int var1) {
      try {
         if (recoverField_d != null && recoverField_d.containsKey(var1)) {
            return 0;
         }

         if (this.recoverField_c != null && this.recoverField_c.containsKey(var1)) {
            RecoverType5a6a396 var2 = (RecoverType5a6a396)this.recoverField_c.get(var1);
            if (var2 != null) {
               return var2.recoverMethod_c_9b68() * 1000;
            }
         }
      } catch (Throwable var3) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDataConfigManager", "get clt delay  failed :" + var3);
      }

      return 0;
   }

   public synchronized void recoverMethod_a_aad3b1ff(String var1) {
      try {
         if (recoverField_a != null) {
            Integer var2 = (Integer)recoverField_a.get(var1);
            if (recoverField_d != null && var2 != null && !recoverField_d.containsKey(var2)) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDataConfigManager", "mark first collect delay :" + var1 + ":" + var2);
               recoverField_d.put(var2, true);
            }
         }
      } catch (Throwable var3) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDataConfigManager", "mark first collect delay failed :" + var3);
      }
   }

   public static void recoverMethod_a_79e5e33f(Bundle var0) {
      Bundle var1 = var0.getBundle("action_tag_time");
      if (null != var1) {
         for (String var4 : var1.keySet()) {
            recoverMethod_a_afa304a5(var4, var1.getLong(var4));
         }
      }
   }

   private static synchronized void recoverMethod_a_afa304a5(String var0, long var1) {
   }

   public long recoverMethod_b_aad3b1f3(String var1) {
      try {
         if (recoverField_a != null) {
            Integer var2 = (Integer)recoverField_a.get(var1);
            if (this.recoverField_c != null && var2 != null && this.recoverField_c.containsKey(var2)) {
               RecoverType5a6a396 var3 = (RecoverType5a6a396)this.recoverField_c.get(var2);
               if (var3 != null) {
                  return var3.recoverMethod_f_9b68() * 1000L;
               }
            }
         }
      } catch (Throwable var4) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDataConfigManager", "get clt interval  failed :" + var4);
      }

      return 86400000L;
   }

   public long recoverMethod_c_aad3b1f3(String var1) {
      try {
         if (recoverField_a != null) {
            Integer var2 = (Integer)recoverField_a.get(var1);
            if (this.recoverField_c != null && var2 != null && this.recoverField_c.containsKey(var2)) {
               RecoverType5a6a396 var3 = (RecoverType5a6a396)this.recoverField_c.get(var2);
               if (var3 != null) {
                  return var3.recoverMethod_d_9b68() * 1000;
               }
            }
         }
      } catch (Throwable var4) {
      }

      return 0L;
   }

   public long recoverMethod_d_aad3b1f3(String var1) {
      try {
         if (recoverField_a != null) {
            Integer var2 = (Integer)recoverField_a.get(var1);
            if (this.recoverField_c != null && var2 != null && this.recoverField_c.containsKey(var2)) {
               RecoverType5a6a396 var3 = (RecoverType5a6a396)this.recoverField_c.get(var2);
               if (var3 != null) {
                  return var3.recoverMethod_g_9b68() * 1000L;
               }
            }
         }
      } catch (Throwable var4) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDataConfigManager", "get rpt interval  failed :" + var4);
      }

      return 86400000L;
   }

   public int recoverMethod_c_134621(int var1) {
      try {
         if (this.recoverField_c != null && this.recoverField_c.containsKey(var1)) {
            RecoverType5a6a396 var2 = (RecoverType5a6a396)this.recoverField_c.get(var1);
            if (var2 != null) {
               return var2.recoverMethod_h_9b68();
            }
         }
      } catch (Throwable var3) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDataConfigManager", "get bg interval  failed :" + var3);
      }

      return recoverField_g.contains(var1) ? -1 : 0;
   }

   public boolean recoverMethod_d_134632(int var1) {
      if (!RecoverTypeaf2b07c2.recoverMethod_a_9b79()) {
         RecoverTypeaf2a9ae9.recoverMethod_a_6ef37c42(var1, "JDataConfigManager", "isCanCollect isAuth " + RecoverTypeaf2b07c2.recoverMethod_a_9b79());
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_255f295(var1, 2030);
         return false;
      }

      boolean var2 = RecoverType5a69fd6.recoverMethod_a_134632(var1);
      boolean var3 = recoverMethod_a_487a337d().recoverMethod_a_134632(var1) && !var2;
      if (var2) {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_255f295(var1, 2029);
      } else {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_255f295(var1, 2065);
      }

      if (var3) {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_255f295(var1, 2031);
      } else {
         RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_255f295(var1, 2032);
      }

      RecoverTypeaf2a9ae9.recoverMethod_a_6ef37c42(var1, "JDataConfigManager", "configId is " + var1 + ", isCanCollect is " + var3);
      return var3;
   }

   public boolean recoverMethod_b_256326a(int var1, boolean var2) {
      if (!RecoverTypeaf2b07c2.recoverMethod_a_9b79()) {
         RecoverTypeaf2a9ae9.recoverMethod_a_6ef37c42(var1, "JDataConfigManager", "isCanCollect isAuth " + RecoverTypeaf2b07c2.recoverMethod_a_9b79());
         return false;
      } else {
         boolean var3 = recoverMethod_a_487a337d().recoverMethod_a_256326a(var1, var2) && !RecoverType5a69fd6.recoverMethod_a_134632(var1);
         RecoverTypeaf2a9ae9.recoverMethod_a_6ef37c42(var1, "JDataConfigManager", "configId is " + var1 + ", isCanCollect is " + var3);
         return var3;
      }
   }

   public boolean recoverMethod_e_134632(int var1) {
      if (!RecoverTypeaf2b07c2.recoverMethod_a_9b79()) {
         RecoverTypeaf2a9ae9.recoverMethod_a_6ef37c42(var1, "JDataConfigManager", "isNotCollect isAuth " + RecoverTypeaf2b07c2.recoverMethod_a_9b79());
         return true;
      } else {
         boolean var2 = !recoverMethod_a_487a337d().recoverMethod_a_134632(var1) || RecoverType5a69fd6.recoverMethod_a_134632(var1);
         RecoverTypeaf2a9ae9.recoverMethod_a_6ef37c42(var1, "JDataConfigManager", "configId is " + var1 + ", isNotCollect is " + var2);
         return var2;
      }
   }

   public boolean recoverMethod_f_134632(int var1) {
      boolean var2 = RecoverType5a69855.recoverMethod_a_487a33bb().recoverMethod_a_134632(var1);
      boolean var3 = RecoverType5a69855.recoverMethod_a_487a33bb().recoverMethod_b_134632(var1);
      RecoverTypeaf2a9ae9.recoverMethod_a_6ef37c42(
         var1, "JDataConfigManager", "isUserEnable with configId: " + var1 + " { containEnableDatas: " + var2 + ", containDisableDatas: " + var3
      );
      return var2 ? true : !var3;
   }

   static {
      recoverField_a.put("JDevice", 1000);
      recoverField_a.put("JDeviceScreen", 2900);
      recoverField_i.add(1100);
      recoverField_i.add(1900);
      recoverField_a.put("JLocation", 1500);
      recoverField_a.put("JAppAll", 1100);
      recoverField_a.put("JAppMovement", 1101);
      recoverField_a.put("JAppPermission", 1300);
      recoverField_a.put("JAppRunning", 1102);
      recoverField_a.put("JAppSdk", 1103);
      recoverField_a.put("JDeviceBattery", 1200);
      recoverField_a.put("JLocationv2", 1500);
      recoverField_a.put("JDeviceIds", 1900);
      recoverField_a.put("uid", 2300);
      recoverField_a.put("JAppActive", 1104);
      recoverField_a.put("JAppActiveWithFile", 1112);
      recoverField_a.put("JDataCollectStatistic", 2600);
      recoverField_a.put("JAppProbe", 1106);
      recoverField_a.put("JDeviceMac", 2700);
      recoverField_a.put("JDeviceIp", 2800);
      recoverField_a.put("JDeviceIdMap", 1030);
      recoverField_e.add(1800);
      recoverField_f.put(1022, new Integer[]{1023, 1024, 1025});
      recoverField_h.add(1015);
      recoverField_h.add(1014);
      recoverField_h.add(1009);
   }

   private static final class Member3661d450 {
      private static final RecoverType5a69853 recoverField_a = new RecoverType5a69853();
   }
}
