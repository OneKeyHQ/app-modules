package cn.jiguang.o;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.j.RecoverType5a6a399;
import cn.jiguang.p.RecoverType5a6ba1c;
import cn.jiguang.p.RecoverType5a6ba1d;
import cn.jiguang.q.RecoverType5a6bddd;
import cn.jiguang.q.RecoverType5a6bdde;
import cn.jiguang.r.RecoverType5a6c19e;
import cn.jiguang.r.RecoverType5a6c19f;
import cn.jiguang.s.RecoverType5a6c561;
import cn.jiguang.t.RecoverType5a6c921;
import cn.jiguang.u.RecoverType5a6cce1;
import cn.jiguang.u.RecoverType5a6cce2;
import cn.jiguang.v.RecoverType5a6d0a2;
import cn.jiguang.v.RecoverType5a6d0a3;
import cn.jiguang.w.RecoverType5a6d465;
import cn.jiguang.x.RecoverType5a6d824;
import cn.jiguang.y.RecoverType5a6dbe5;
import cn.jiguang.y.RecoverType5a6dbe6;
import cn.jiguang.z.RecoverType5a6dfa6;
import cn.jiguang.z.RecoverType5a6dfa7;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import org.json.JSONException;
import org.json.JSONObject;

public class RecoverType5a6b65b {
   private static volatile JSONObject recoverField_a = null;
   private static String recoverField_b;
   private static String recoverField_c = null;
   private static String recoverField_d = null;
   private static String recoverField_e = null;
   private static String recoverField_f = null;
   private static String recoverField_g = null;
   private static String recoverField_h = null;
   private static String recoverField_i = null;
   private static String recoverField_j = null;
   private static String recoverField_k = null;

   public static JSONObject recoverMethod_a_16ce4761(Context var0) {
      return recoverMethod_a_662c69af(var0, true);
   }

   public static JSONObject recoverMethod_a_662c69af(Context var0, boolean var1) {
      try {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("IdHelper", "[getAllIds] useMethod：" + var1);
         if (recoverField_a != null) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("IdHelper", "[getAllIds] local idsJSON：" + recoverField_a);
            return recoverField_a;
         }

         FutureTask var2 = new FutureTask(new RecoverType5a6b65b.Member36d29058(var0, var1));
         RecoverTypeaf2aa628.recoverMethod_e_a4fc3973(var2);
         JSONObject var3 = (JSONObject)var2.get(2L, TimeUnit.SECONDS);
         recoverField_a = var3.toString().equals("{}") ? null : var3;
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("IdHelper", "[getAllIds] allIds：" + var3);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("IdHelper", "[getAllIds] idsJSON：" + recoverField_a);
      } catch (Throwable var4) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("IdHelper", "[getAllIds] failed：" + var4.getMessage());
      }

      return recoverField_a;
   }

   private static String recoverMethod_b_9ba16009(final Context var0, final String var1, final boolean var2) {
      try {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("IdHelper", "[getIds] useMethod：" + var2);
         Object var3 = RecoverType5a6a399.recoverMethod_a_8f0f26ca(var0, 1900, 1800000L, 259200000L, new RecoverType5a6a399.Member368c2616() {
            @Override
            public Object recoverMethod_c_810c345d() {
               return RecoverType5a6b65b.recoverField_b;
            }

            @Override
            public void recoverMethod_b_5d527811(Object var1x) {
               if (var1x instanceof String) {
                  RecoverType5a6b65b.recoverField_b = (String)var1x;
               }
            }

            @Override
            public Object recoverMethod_b_810c345d() {
               return var2 ? RecoverType5a6b65b.recoverMethod_b_a4ea6d43(var0, var1) : "";
            }
         });
         if (var3 instanceof String) {
            recoverField_b = (String)var3;
         }
      } catch (Throwable var4) {
      }

      return recoverField_b;
   }

   private static String recoverMethod_b_a4ea6d43(Context var0, String var1) {
      try {
         if (recoverMethod_c_9b79()) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("IdHelper", "not get id in main thread");
            return "";
         }

         String var2 = "";
         String var3 = "";
         JSONObject var4 = new JSONObject();
         var2 = RecoverType5a68d10.recoverMethod_c_e96ea081("KACzk43fXSu6fNDY3NHiRg==");
         String var5 = RecoverType5a68d10.recoverMethod_c_e96ea081("1uhzqUdzasJ2ZR3KEL+MzQ==");
         if (!TextUtils.isEmpty(var2) && var1.toLowerCase().equals(var2) || !TextUtils.isEmpty(var5) && var1.toLowerCase().equals(var5)) {
            var3 = recoverMethod_d_e501eb0d(var0);
            if (TextUtils.isEmpty(var3)) {
               return "";
            }

            var4.put("joad", var3);
            return var4.toString();
         }

         var2 = RecoverType5a68d10.recoverMethod_c_e96ea081("abElWL6JFOA3DmtpsVXe5g==");
         String var6 = RecoverType5a68d10.recoverMethod_c_e96ea081("qqDQS9ZdTx8AkhCp5nUUmQ==");
         if (!TextUtils.isEmpty(var2) && var1.toLowerCase().equals(var2) || !TextUtils.isEmpty(var6) && var1.toLowerCase().equals(var6)) {
            return recoverMethod_l_e501eb0d(var0);
         }

         var2 = RecoverType5a68d10.recoverMethod_c_e96ea081("ZCUqO8ru60AnJTvdxNftwg==");
         if (!TextUtils.isEmpty(var2) && var1.toLowerCase().equals(var2)) {
            return recoverMethod_j_e501eb0d(var0);
         }

         var2 = RecoverType5a68d10.recoverMethod_c_e96ea081("Sqo/G40afBQEk/ThxiHCDA==");
         if (!TextUtils.isEmpty(var2) && var1.toLowerCase().equals(var2)) {
            var3 = recoverMethod_i_e501eb0d(var0);
            if (TextUtils.isEmpty(var3)) {
               return "";
            }

            var4.put("joad", var3);
            return var4.toString();
         }

         var2 = RecoverType5a68d10.recoverMethod_c_e96ea081("Fl44OKc45ZSCqG4pxdgAdA==");
         if (!TextUtils.isEmpty(var2) && var1.toLowerCase().equals(var2)) {
            var3 = recoverMethod_g_e501eb0d(var0);
            if (TextUtils.isEmpty(var3)) {
               return "";
            }

            var4.put("joad", var3);
            return var4.toString();
         }

         var2 = RecoverType5a68d10.recoverMethod_c_e96ea081("v4ibuvMAw0xxYGyCW947bw==");
         var6 = RecoverType5a68d10.recoverMethod_c_e96ea081("lJTl9z+ZycJlu+D6qavE9g==");
         if (!TextUtils.isEmpty(var2) && var1.toLowerCase().equals(var2) || !TextUtils.isEmpty(var6) && var1.toLowerCase().equals(var6)) {
            return recoverMethod_h_e501eb0d(var0);
         }

         var2 = RecoverType5a68d10.recoverMethod_c_e96ea081("7UUoz3VX0wN8BuYNQ77o2g==");
         if (!TextUtils.isEmpty(var2) && var1.toLowerCase().equals(var2)) {
            return recoverMethod_f_e501eb0d(var0);
         }

         var2 = RecoverType5a68d10.recoverMethod_c_e96ea081("NftKgs8fjwuVYJ3VslncvA==");
         var6 = RecoverType5a68d10.recoverMethod_c_e96ea081("Wq1559o9+HHChTmry59Bkg==");
         if (!TextUtils.isEmpty(var2) && var1.toLowerCase().equals(var2) || !TextUtils.isEmpty(var6) && var1.toLowerCase().equals(var6)) {
            var3 = recoverMethod_e_e501eb0d(var0);
            if (TextUtils.isEmpty(var3)) {
               return "";
            }

            var4.put("joad", var3);
            return var4.toString();
         }

         var2 = RecoverType5a68d10.recoverMethod_c_e96ea081("fmTCWm9ViPlyzM8H0bOrgw==");
         if (!TextUtils.isEmpty(var2) && var1.toLowerCase().equals(var2)) {
            var3 = recoverMethod_k_e501eb0d(var0);
            if (TextUtils.isEmpty(var3)) {
               return "";
            }

            var4.put("joad", var3);
            return var4.toString();
         }

         var2 = RecoverType5a68d10.recoverMethod_c_e96ea081("bbyVCf9F4jMXWPUAeT3frw==");
         if (!TextUtils.isEmpty(var2) && var1.toLowerCase().equals(var2)) {
            var3 = recoverMethod_m_e501eb0d(var0);
            if (TextUtils.isEmpty(var3)) {
               return "";
            }

            var4.put("joad", var3);
            return var4.toString();
         }

         var2 = RecoverType5a68d10.recoverMethod_c_e96ea081("0kbg5dLQpyr1z+M/7Qkp7A==");
         if (!TextUtils.isEmpty(var2) && var1.toLowerCase().equals(var2) || RecoverType5a6dbe5.recoverMethod_a_9b79()) {
            var3 = recoverMethod_n_e501eb0d(var0);
            if (TextUtils.isEmpty(var3)) {
               return "";
            }

            var4.put("joad", var3);
            return var4.toString();
         }

         var2 = RecoverType5a68d10.recoverMethod_c_e96ea081("7kOXkVyi2CnACIIN3msOQg==");
         if (!TextUtils.isEmpty(var2) && var1.toLowerCase().equals(var2) || RecoverType5a6dbe5.recoverMethod_b_9b79()) {
            var3 = recoverMethod_o_e501eb0d(var0);
            if (TextUtils.isEmpty(var3)) {
               return "";
            }

            var4.put("joad", var3);
            return var4.toString();
         }

         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("IdHelper", "not supported this device: " + var1);
      } catch (Throwable var7) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("IdHelper", "getIdsByLocal failed:" + var7.getMessage());
      }

      return "";
   }

   public static String recoverMethod_b_5e799b7f(final Context var0, final boolean var1) {
      try {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("IdHelper", "[getGoogleAdid] useMethod：" + var1);
         Object var2 = RecoverType5a6a399.recoverMethod_a_8f0f26ca(var0, 1901, 1800000L, 259200000L, new RecoverType5a6a399.Member368c2616() {
            @Override
            public Object recoverMethod_c_810c345d() {
               return RecoverType5a6b65b.recoverField_c;
            }

            @Override
            public void recoverMethod_b_5d527811(Object var1x) {
               if (var1x instanceof String) {
                  RecoverType5a6b65b.recoverField_c = (String)var1x;
               }
            }

            @Override
            public Object recoverMethod_b_810c345d() {
               return var1 ? RecoverType5a6b65b.recoverMethod_c_e501eb0d(var0) : "";
            }
         });
         if (var2 instanceof String) {
            recoverField_c = (String)var2;
         }
      } catch (Throwable var3) {
         recoverField_c = "";
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("IdHelper", "getGoogleAdid failed:" + var3.getMessage());
      }

      return recoverField_c;
   }

   private static String recoverMethod_c_e501eb0d(Context var0) {
      if (recoverMethod_c_9b79()) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("IdHelper", "not get id in main thread");
         return "";
      }

      try {
         String var1 = RecoverType5a68d10.recoverMethod_c_e96ea081("+Fc+/S0DV5xukan0E/9N4RvXQpEI8h8+6y3k9NAvwjLhqIYeN+juscczCl6Sq6PxwLAq9CdDlLtmxsbD83akRg==");
         if (TextUtils.isEmpty(var1)) {
            return "";
         }

         Intent var2 = new Intent(var1);
         String var3 = RecoverType5a68d10.recoverMethod_c_e96ea081("+Fc+/S0DV5xukan0E/9N4VArQdi8pEVBp8UquAC8VbE=");
         if (TextUtils.isEmpty(var3)) {
            return "";
         }

         var2.setPackage(var3);
         RecoverType5a6bdde var4 = new RecoverType5a6bdde();
         boolean var5 = var0.bindService(var2, var4, 1);
         if (var5) {
            try {
               if (var4.recoverField_a) {
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("IdHelper", "google service repeat bind");
                  return "";
               }

               RecoverType5a6bddd var6 = new RecoverType5a6bddd((IBinder)var4.recoverField_b.take());
               String var7 = var6.recoverMethod_a_73cf11cb();
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("IdHelper", "google ad id:" + var7);
               return var7;
            } catch (Throwable var13) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("IdHelper", "get google Ids by service error: " + var13.getMessage());
            } finally {
               var0.unbindService(var4);
            }
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("IdHelper", "google service bind failed");
         }
      } catch (Throwable var15) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("IdHelper", "google getAdvertisingIdInfo Exception: " + var15.toString());
      }

      return "";
   }

   private static boolean recoverMethod_c_9b79() {
      try {
         return Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId();
      } catch (Throwable var1) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("IdHelper", "[isMainThread] failed:" + var1.getMessage());
         return true;
      }
   }

   private static String recoverMethod_d_e501eb0d(Context var0) {
      if (var0 == null) {
         return "";
      }

      boolean var1 = false;

      try {
         if (recoverField_d != null) {
            return recoverField_d;
         }

         String var2 = RecoverType5a68d10.recoverMethod_c_e96ea081("CV+BUnOM9r9hBWkUu5oSnA==");
         if (TextUtils.isEmpty(var2)) {
            return "";
         }

         recoverField_d = "";
         String var3 = RecoverType5a68d10.recoverMethod_c_e96ea081("axDZqud6H+CDQBXA/yBXOiEPy9gW6px8eLENPXdBlBHSZeanEgWxhi72s58AM6wZ");
         if (TextUtils.isEmpty(var3)) {
            return "";
         }

         Intent var4 = new Intent(var3);
         var4.setPackage(var2);
         RecoverType5a6c19f var5 = new RecoverType5a6c19f();
         var1 = var0.bindService(var4, var5, 1);
         if (var1) {
            try {
               RecoverType5a6c19e var6 = new RecoverType5a6c19e((IBinder)var5.recoverField_a.take());
               String var7 = var6.recoverMethod_a_73cf11cb();
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("IdHelper", "hw ids-o:" + var7);
               recoverField_d = var7;
               return var7;
            } catch (Throwable var13) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("IdHelper", "hw get Ids-oa error: " + var13.getMessage());
            } finally {
               if (var1) {
                  var0.unbindService(var5);
               }
            }
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("IdHelper", "hw service bind failed");
         }
      } catch (Throwable var15) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("IdHelper", "hw getAdvertisingIdInfo Exception: " + var15.toString());
      }

      return "";
   }

   private static String recoverMethod_e_e501eb0d(Context var0) {
      String var1 = "";

      try {
         if (null != recoverField_e) {
            return recoverField_e;
         }

         var1 = RecoverType5a68d10.recoverMethod_c_e96ea081("Mpy0fkBSw1N+kug2cBPj2YJ2JkldBoT0Hj8EbMwXMGU=");
         if (TextUtils.isEmpty(var1)) {
            return "";
         }

         recoverField_e = "";
         String var2 = RecoverType5a68d10.recoverMethod_c_e96ea081("Mpy0fkBSw1N+kug2cBPj2dTFEwqSue3aUHyxqBhF0BWQuC1TiTYwRmpXbBFYowSK");
         if (TextUtils.isEmpty(var2)) {
            return "";
         }

         Intent var3 = new Intent(var2);
         var3.setPackage(var1);
         RecoverType5a6dfa7 var4 = new RecoverType5a6dfa7();
         boolean var5 = var0.bindService(var3, var4, 1);
         if (var5) {
            try {
               RecoverType5a6dfa6 var6 = new RecoverType5a6dfa6((IBinder)var4.recoverField_a.take());
               String var7 = var6.recoverMethod_a_73cf11cb();
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("IdHelper", "zui ids-oa:" + var7);
               recoverField_e = var7;
               return var7;
            } catch (Throwable var13) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("IdHelper", "get zui ids-oa error: " + var13.getMessage());
            } finally {
               if (var5) {
                  var0.unbindService(var4);
               }
            }
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("IdHelper", "zui service bind failed");
         }
      } catch (Exception var15) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("IdHelper", "zui getAdvertisingIdInfo Exception: " + var15.toString());
      }

      return "";
   }

   private static String recoverMethod_f_e501eb0d(Context var0) {
      JSONObject var1 = new JSONObject();

      try {
         RecoverType5a6c921 var2 = new RecoverType5a6c921(var0);
         String var3 = var2.recoverMethod_a_73cf11cb();
         if (!TextUtils.isEmpty(var3)) {
            var1.put("joad", var3);
         }
      } catch (JSONException var4) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("IdHelper", "getNubiaIds err: " + var4.getMessage());
      }

      return var1.toString();
   }

   private static String recoverMethod_g_e501eb0d(Context var0) {
      try {
         if (null != recoverField_f) {
            return recoverField_f;
         }

         String var1 = RecoverType5a68d10.recoverMethod_c_e96ea081("NZZdxIY39aBpJdeuRJ0VLp5xPlLZJqfiSSPak26ACVtSyGK74B1pfv+DoniV/u8H");
         if (TextUtils.isEmpty(var1)) {
            return "";
         }

         recoverField_f = "";
         String var2 = RecoverType5a68d10.recoverMethod_c_e96ea081("NZZdxIY39aBpJdeuRJ0VLp5xPlLZJqfiSSPak26ACVsaSg3goTgfCOA0dOFWjVLPc7dVv4XHGL0Dk7MQTYMVQw==");
         if (TextUtils.isEmpty(var2)) {
            return "";
         }

         Intent var3 = new Intent();
         var3.setClassName(var1, var2);
         RecoverType5a6d0a3 var4 = new RecoverType5a6d0a3();
         boolean var5 = var0.bindService(var3, var4, 1);
         if (var5) {
            try {
               RecoverType5a6d0a2 var6 = new RecoverType5a6d0a2((IBinder)var4.recoverField_a.take());
               String var7 = var6.recoverMethod_a_e501eb0d(var0);
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("IdHelper", "sumsuang ids-oa:" + var7);
               recoverField_f = var7;
               return var7;
            } catch (Throwable var13) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("IdHelper", "get sumsung Ids-oa error: " + var13.getMessage());
            } finally {
               if (var5) {
                  var0.unbindService(var4);
               }
            }
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("IdHelper", "sumsung service bind failed");
         }
      } catch (Exception var15) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("IdHelper", "sumsung getAdvertisingIdInfo Exception: " + var15.toString());
      }

      return "";
   }

   private static String recoverMethod_h_e501eb0d(Context var0) {
      JSONObject var1 = new JSONObject();

      try {
         RecoverType5a6c561 var2 = new RecoverType5a6c561(var0);
         String var3 = var2.recoverMethod_a_73cf11cb();
         if (!TextUtils.isEmpty(var3)) {
            var1.put("joad", var3);
         }
      } catch (JSONException var4) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("IdHelper", "getVivoIds err: " + var4.getMessage());
      }

      return var1.toString();
   }

   private static String recoverMethod_i_e501eb0d(Context var0) {
      try {
         if (null != recoverField_g) {
            return recoverField_g;
         }

         String var1 = RecoverType5a68d10.recoverMethod_c_e96ea081("qFFOesfckPwVmbfqzGl5oG9IMWwJa4PjDfKEUokUsrU=");
         if (TextUtils.isEmpty(var1)) {
            return "";
         }

         recoverField_g = "";
         String var2 = RecoverType5a68d10.recoverMethod_c_e96ea081("qFFOesfckPwVmbfqzGl5oOMkBF5tSSPHMYAiT/owwSwoCrUg5HXoJRYDJrCupV3K");
         if (TextUtils.isEmpty(var2)) {
            return "";
         }

         Intent var3 = new Intent(var2);
         var3.setPackage(var1);
         var2 = RecoverType5a68d10.recoverMethod_c_e96ea081("/3gurUKcGzqF8/YMsL45D9RvCsSd5y5lQDTuvU2S5vtG2aMIkkluUNr+NKF2loi0");
         if (TextUtils.isEmpty(var2)) {
            return "";
         }

         var3.setAction(var2);
         RecoverType5a6cce2 var4 = new RecoverType5a6cce2();
         boolean var5 = var0.bindService(var3, var4, 1);
         if (var5) {
            try {
               RecoverType5a6cce1 var6 = new RecoverType5a6cce1((IBinder)var4.recoverField_a.take());
               String var7 = var6.recoverMethod_a_e501eb0d(var0);
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("IdHelper", "oppo ids-oa: " + var7);
               recoverField_g = var7;
               return var7;
            } catch (Throwable var13) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("IdHelper", "get oppo ids-oa error: " + var13.getMessage());
            } finally {
               if (var5) {
                  var0.unbindService(var4);
               }
            }
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("IdHelper", "oppo service bind failed");
         }
      } catch (Exception var15) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("IdHelper", "oppo getAdvertisingIdInfo Exception: " + var15.toString());
      }

      return "";
   }

   private static String recoverMethod_j_e501eb0d(Context var0) {
      JSONObject var1 = new JSONObject();

      try {
         RecoverType5a6d465 var2 = new RecoverType5a6d465(var0);
         var2.recoverMethod_a_aad3b1ff("");
         String var3 = var2.recoverMethod_a_73cf11cb();
         if (!TextUtils.isEmpty(var3)) {
            var1.put("joad", var3);
         }
      } catch (JSONException var4) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("IdHelper", "getVivoIds err: " + var4.getMessage());
      }

      return var1.toString();
   }

   private static String recoverMethod_k_e501eb0d(Context var0) {
      if (var0 == null) {
         return "";
      }

      boolean var1 = false;
      ServiceConnection var2 = null;

      try {
         if (recoverField_h != null) {
            return recoverField_h;
         }

         recoverField_h = "";
         String var3 = RecoverType5a68d10.recoverMethod_c_e96ea081("AVy8x+cBVze9OKJHZKioHyNDBGqtEllVM4dvG69zMJc=");
         if (TextUtils.isEmpty(var3)) {
            return "";
         }

         Intent var4 = new Intent(var3);
         String var5 = RecoverType5a68d10.recoverMethod_c_e96ea081("8lAhWp7NB89J3VIJU4lIGQtLf0YkfPcfFvWDnnGUuiQ=");
         if (TextUtils.isEmpty(var5)) {
            return "";
         }

         String var6 = RecoverType5a68d10.recoverMethod_c_e96ea081("8lAhWp7NB89J3VIJU4lIGQnPwmB8zgbENyN+gUA8dpRI7pO4GGAXufB+HCPmLRg7bL9N6o9V3Rxxk98J3lyy6g==");
         if (TextUtils.isEmpty(var6)) {
            return "";
         }

         ComponentName var7 = new ComponentName(var5, var6);
         Intent var8 = new Intent(var4);
         var8.setComponent(var7);
         RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("IdHelper", "start to bind did service.");
         var2 = new RecoverType5a6ba1d();
         var1 = var0.bindService(var8, var2, 1);
         if (var1) {
            try {
               RecoverType5a6ba1c var9 = new RecoverType5a6ba1c((IBinder)((RecoverType5a6ba1d)var2).recoverField_a.take());
               String var10 = var9.recoverMethod_a_73cf11cb();
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("IdHelper", "asus ids-oa; " + var10);
               recoverField_h = var10;
               return var10;
            } catch (Throwable var16) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("IdHelper", "get asus ids-oa error: " + var16.getMessage());
            } finally {
               if (var1) {
                  RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("IdHelper", "start to unbind did service");
                  var0.unbindService(var2);
               }
            }
         }
      } catch (Exception var18) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("IdHelper", "asus getAdvertisingIdInfo Exception: " + var18.toString());
      }

      return "";
   }

   private static String recoverMethod_l_e501eb0d(Context var0) {
      return RecoverType5a6d824.recoverMethod_a_e501eb0d(var0);
   }

   private static String recoverMethod_m_e501eb0d(Context var0) {
      if (null == recoverField_i) {
         RecoverType5a6dbe6 var1 = new RecoverType5a6dbe6(var0);
         recoverField_i = var1.recoverMethod_a_73cf11cb();
         recoverField_i = TextUtils.isEmpty(recoverField_i) ? "" : recoverField_i;
      }

      return recoverField_i;
   }

   private static String recoverMethod_n_e501eb0d(Context var0) {
      if (null == recoverField_j) {
         RecoverType5a6dbe6 var1 = new RecoverType5a6dbe6(var0);
         recoverField_j = var1.recoverMethod_a_73cf11cb();
         recoverField_j = TextUtils.isEmpty(recoverField_j) ? "" : recoverField_j;
      }

      return recoverField_j;
   }

   private static String recoverMethod_o_e501eb0d(Context var0) {
      if (null == recoverField_k) {
         RecoverType5a6dbe6 var1 = new RecoverType5a6dbe6(var0);
         recoverField_k = var1.recoverMethod_a_73cf11cb();
         recoverField_k = TextUtils.isEmpty(recoverField_k) ? "" : recoverField_k;
      }

      return recoverField_k;
   }

   static class Member36d29058 implements Callable {
      private Context recoverField_a;
      private boolean recoverField_b;

      public Member36d29058(Context var1, boolean var2) {
         this.recoverField_a = var1;
         this.recoverField_b = var2;
      }

      public JSONObject recoverMethod_a_a7dbfe3() {
         JSONObject var1 = new JSONObject();

         try {
            String var2 = RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_x_e501eb0d(this.recoverField_a);
            if (!TextUtils.isEmpty(var2)) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("IdHelper", "start getAllIds by local");
               String var3 = RecoverType5a6b65b.recoverMethod_b_9ba16009(this.recoverField_a, var2, this.recoverField_b);
               if (!TextUtils.isEmpty(var3)) {
                  var1 = new JSONObject(var3);
               }
            }

            String var5 = RecoverType5a6b65b.recoverMethod_b_5e799b7f(this.recoverField_a, this.recoverField_b);
            if (!TextUtils.isEmpty(var5)) {
               var1.put("jgad", var5);
            }
         } catch (Throwable var4) {
         }

         return var1;
      }

      // $VF: synthetic method
      @Override
      public Object call() {
         return this.recoverMethod_a_a7dbfe3();
      }
   }
}
