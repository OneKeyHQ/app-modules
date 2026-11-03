package cn.jiguang.e;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Build.VERSION;
import android.text.TextUtils;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bj.RecoverTypeaf2b0403;
import cn.jiguang.bk.RecoverTypeaf2b07d4;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class RecoverType5a690d2 {
   private static final Map recoverField_a = new ConcurrentHashMap();

   private static void recoverMethod_c_5ffc00fd(Context var0, String var1) {
      String var2 = (String)recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_aa_48794abf());
      String var3 = "5.2.0";
      if ((TextUtils.isEmpty(var2) || var2.startsWith("1.")) && var3.startsWith("2.")) {
         if (var1.equals("cn.jiguang.sdk.user.set.profile")) {
            recoverMethod_a_1cd9da6f(
               var0,
               RecoverType5a690d1.recoverMethod_O_48794abf(),
               RecoverType5a690d1.recoverMethod_O_48794abf().recoverMethod_i_5ef90ef5("cn.jpush.preferences.v2")
            );
            recoverMethod_a_1cd9da6f(
               var0,
               RecoverType5a690d1.recoverMethod_P_48794abf(),
               RecoverType5a690d1.recoverMethod_P_48794abf().recoverMethod_a_4e9722a3("cn.jpush.android.user.profile")
            );
         } else if (var1.equals("cn.jiguang.sdk.user.profile")) {
            recoverMethod_a_1cd9da6f(
               var0,
               RecoverType5a690d1.recoverMethod_B_48794abf(),
               RecoverType5a690d1.recoverMethod_B_48794abf().recoverMethod_i_5ef90ef5("cn.jpush.android.user.profile").recoverMethod_j_5ef90ef5("device_uid")
            );
            recoverMethod_a_1cd9da6f(
               var0,
               RecoverType5a690d1.recoverMethod_C_48794abf(),
               RecoverType5a690d1.recoverMethod_C_48794abf()
                  .recoverMethod_i_5ef90ef5("cn.jpush.android.user.profile")
                  .recoverMethod_j_5ef90ef5("device_registration_id")
            );
            recoverMethod_a_1cd9da6f(
               var0,
               RecoverType5a690d1.recoverMethod_E_48794abf(),
               RecoverType5a690d1.recoverMethod_E_48794abf()
                  .recoverMethod_i_5ef90ef5("cn.jpush.android.user.profile")
                  .recoverMethod_j_5ef90ef5("device_password")
            );
         } else if (var1.equals("cn.jiguang.sdk.address")) {
            recoverMethod_a_1cd9da6f(
               var0,
               RecoverType5a690d1.recoverMethod_aw_48794abf(),
               RecoverType5a690d1.recoverMethod_aw_48794abf().recoverMethod_i_5ef90ef5("cn.jpush.android.user.profile").recoverMethod_j_5ef90ef5("conn")
            );
            recoverMethod_a_1cd9da6f(
               var0,
               RecoverType5a690d1.recoverMethod_ax_48794abf(),
               RecoverType5a690d1.recoverMethod_ax_48794abf().recoverMethod_i_5ef90ef5("cn.jpush.android.user.profile").recoverMethod_j_5ef90ef5("srv")
            );
         } else if (var1.equals(RecoverType5a690d1.recoverField_a)) {
            recoverMethod_a_1cd9da6f(
               var0,
               RecoverType5a690d1.recoverMethod_ah_48794abf(),
               RecoverType5a690d1.recoverMethod_ah_48794abf().recoverMethod_j_5ef90ef5("device_registered_appkey_n")
            );
            recoverMethod_a_1cd9da6f(
               var0, RecoverType5a690d1.recoverMethod_aj_48794abf(), RecoverType5a690d1.recoverMethod_aj_48794abf().recoverMethod_j_5ef90ef5("imei")
            );
         }
      }

      int var4 = (Integer)recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_Z_48794abf());
      if (0 == var4) {
         recoverMethod_a_1cd9da6f(
            var0,
            RecoverType5a690d1.Member3645a54e.Memberbb718e8b.recoverMethod_a_48794abf(),
            RecoverType5a690d1.Member3645a54e.Memberbb718e8b.recoverMethod_b_48794abf()
         );
         recoverMethod_a_1cd9da6f(
            var0,
            RecoverType5a690d1.Member3645a54e.Memberbb718e8b.recoverMethod_c_48794abf(),
            RecoverType5a690d1.Member3645a54e.Memberbb718e8b.recoverMethod_d_48794abf()
         );
         boolean var5 = true;
      }

      recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_Z_48794abf().recoverMethod_a_4e9722a3(1));
   }

   public static void recoverMethod_a_abcb8d40(Context var0, RecoverType5a690d1... var1) {
      if (var1 != null && var1.length > 0) {
         if (recoverMethod_a_9b79()) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("Sp", "main thread, async sp set");
            recoverMethod_a_25433db2(var0, true, var1);
         } else {
            recoverMethod_b_abcb8d40(var0, var1);
         }
      }
   }

   public static void recoverMethod_b_abcb8d40(Context var0, RecoverType5a690d1... var1) {
      if (var1 != null && var1.length > 0) {
         recoverMethod_a_25433db2(var0, false, var1);
      }
   }

   private static boolean recoverMethod_a_9b79() {
      try {
         return Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId();
      } catch (Throwable var1) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("Sp", "[isMainThread] failed:" + var1.getMessage());
         return true;
      }
   }

   private static void recoverMethod_a_25433db2(final Context var0, boolean var1, final RecoverType5a690d1... var2) {
      try {
         RecoverTypeaf2b0401 var3 = new RecoverTypeaf2b0401() {
            @Override
            public void recoverMethod_a_9b75() {
               try {
                  HashMap var1x = new HashMap();

                  for (RecoverType5a690d1 var5 : var2) {
                     Editor var6 = (Editor)var1x.get(var5.recoverField_b);
                     if (var6 == null) {
                        SharedPreferences var7 = RecoverType5a690d2.recoverMethod_e_60e33291(var0, var5.recoverField_b);
                        if (var7 != null) {
                           var6 = var7.edit();
                           var1x.put(var5.recoverField_b, var6);
                        }
                     }

                     RecoverType5a690d2.recoverMethod_b_54ebfa5e(var6, var5.recoverField_c, var5.recoverField_d, var5.recoverField_f);
                  }

                  for (Object recoveryElement_148 : var1x.entrySet()) {
                     Entry var10 = (Entry)recoveryElement_148;
                     String var11 = (String)var10.getKey();
                     Editor var12 = (Editor)var10.getValue();
                     if (var12 != null) {
                        var12.apply();
                     }
                  }
               } catch (Throwable var8) {
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("Sp", "sp commit failed, " + var8.getMessage());
               }
            }
         };
         if (var1) {
            RecoverType5a690d2.Member3645a90f.recoverMethod_a_fa8ba7c1(var3, false);
         } else {
            var3.run();
         }
      } catch (Throwable var4) {
      }
   }

   public static Object recoverMethod_a_413ed21(Context var0, RecoverType5a690d1 var1) {
      Object var2 = recoverMethod_b_413ed21(var0, var1);
      return var2 != null ? var2 : var1.recoverField_d;
   }

   public static Object recoverMethod_b_413ed21(Context var0, RecoverType5a690d1 var1) {
      Object var2 = recoverMethod_a_8e150a93(recoverMethod_e_60e33291(var0, var1.recoverField_b), var1.recoverField_c, var1.recoverField_d, var1.recoverField_f);
      if (var2 == null && var1.recoverField_e) {
         var2 = recoverMethod_a_8e150a93(recoverMethod_d_60e33291(var0, var1.recoverField_b), var1.recoverField_c, var1.recoverField_d, var1.recoverField_f);
      }

      if (var2 != null) {
         var1.recoverMethod_a_4e9722a3(var2);
         return var2;
      } else {
         return null;
      }
   }

   public static void recoverMethod_a_5ffc00fd(Context var0, String var1) {
      SharedPreferences var2 = recoverMethod_e_60e33291(var0, var1);
      if (var2 != null) {
         var2.edit().clear().apply();
      }
   }

   private static SharedPreferences recoverMethod_d_60e33291(Context var0, String var1) {
      if (var0 != null) {
         if (VERSION.SDK_INT >= 11) {
            var0.getSharedPreferences(var1, 4);
         }

         return var0.getSharedPreferences(var1, 0);
      } else {
         return null;
      }
   }

   private static void recoverMethod_b_54ebfa5e(Editor var0, String var1, Object var2, boolean var3) {
      if (var0 != null) {
         if (var2 == null) {
            var0.remove(var1);
            return;
         }

         if (var3) {
            String var4 = String.valueOf(var2);
            if (var4.length() > 0) {
               var4 = RecoverTypeaf2b07d4.recoverMethod_a_8ea82db7(var4, RecoverType5a681cd.recoverField_i);
            }

            var0.putString(var1, var4);
            return;
         }

         if (var2 instanceof Boolean) {
            var0.putBoolean(var1, (Boolean)var2);
         } else if (var2 instanceof String) {
            var0.putString(var1, (String)var2);
         } else if (var2 instanceof Integer) {
            var0.putInt(var1, (Integer)var2);
         } else if (var2 instanceof Long) {
            var0.putLong(var1, (Long)var2);
         } else if (var2 instanceof Float) {
            var0.putFloat(var1, (Float)var2);
         }
      }
   }

   private static Object recoverMethod_a_8e150a93(SharedPreferences var0, String var1, Object var2, boolean var3) {
      if (var0 != null && var0.contains(var1)) {
         try {
            if (var3) {
               return recoverMethod_a_988428dd(var0, var1, var2);
            }

            if (var2 instanceof Boolean) {
               return var0.getBoolean(var1, (Boolean)var2);
            }

            if (var2 instanceof String) {
               return var0.getString(var1, (String)var2);
            }

            if (var2 instanceof Integer) {
               return var0.getInt(var1, (Integer)var2);
            }

            if (var2 instanceof Long) {
               return var0.getLong(var1, (Long)var2);
            }

            if (var2 instanceof Float) {
               return var0.getFloat(var1, (Float)var2);
            }
         } catch (Throwable var5) {
         }

         return null;
      } else {
         return null;
      }
   }

   private static Object recoverMethod_a_988428dd(SharedPreferences var0, String var1, Object var2) {
      if (var0 != null && var0.contains(var1)) {
         try {
            String var3 = var0.getString(var1, null);
            if (var3 != null && var3.length() > 0) {
               var3 = RecoverTypeaf2b07d4.recoverMethod_b_8ea82db7(var3, RecoverType5a681cd.recoverField_i);
            }

            if (null == var3) {
               return var2;
            }

            if (var2 instanceof Boolean) {
               return Boolean.valueOf(var3);
            }

            if (var2 instanceof String) {
               return var3;
            }

            if (var2 instanceof Integer) {
               return Integer.valueOf(var3);
            }

            if (var2 instanceof Long) {
               return Long.valueOf(var3);
            }

            if (var2 instanceof Float) {
               return Float.valueOf(var3);
            }
         } catch (Throwable var4) {
         }

         return null;
      } else {
         return null;
      }
   }

   private static SharedPreferences recoverMethod_e_60e33291(Context var0, String var1) {
      if (TextUtils.isEmpty(var1)) {
         return null;
      }

      SharedPreferences var2 = (SharedPreferences)recoverField_a.get(var1);
      if (var2 == null && var0 != null) {
         var2 = var0.getSharedPreferences(var1, 0);
         recoverField_a.put(var1, var2);
         recoverMethod_c_5ffc00fd(var0, var1);
      }

      return var2;
   }

   private static void recoverMethod_a_1cd9da6f(Context var0, RecoverType5a690d1 var1, RecoverType5a690d1 var2) {
      Object var3 = recoverMethod_b_413ed21(var0, var1);
      if (var3 == null) {
         var3 = recoverMethod_b_413ed21(var0, var2);
         if (var3 != null) {
            recoverMethod_a_abcb8d40(var0, var1.recoverMethod_a_4e9722a3(var3));
            recoverMethod_a_abcb8d40(var0, var2.recoverMethod_a_4e9722a3(null));
         }
      }
   }

   private static class Member3645a90f {
      private static volatile Handler recoverField_a = null;
      private static final Object recoverField_b = new Object();
      private static final LinkedList recoverField_c = new LinkedList();
      private static boolean recoverField_d = true;
      private static final Object recoverField_e = new Object();

      private static Handler recoverMethod_b_de962d31() {
         if (recoverField_a == null) {
            synchronized (recoverField_b) {
               if (recoverField_a == null) {
                  recoverField_a = new RecoverType5a690d2.Member3645a90f.Memberbb7fa60c(Looper.getMainLooper());
               }
            }
         }

         return recoverField_a;
      }

      public static void recoverMethod_a_fa8ba7c1(Runnable var0, boolean var1) {
         try {
            Handler var2 = recoverMethod_b_de962d31();
            if (null != var2) {
               synchronized (recoverField_b) {
                  recoverField_c.add(var0);
                  if (var1 && recoverField_d) {
                     var2.sendEmptyMessageDelayed(1, 100L);
                  } else {
                     var2.sendEmptyMessage(1);
                  }
               }
            }
         } catch (Throwable var6) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("Sp", "sp task add queue failed, " + var6.getMessage());
         }
      }

      private static void recoverMethod_c_9b75() {
         try {
            long var0 = System.currentTimeMillis();
            synchronized (recoverField_e) {
               LinkedList var3;
               synchronized (recoverField_b) {
                  var3 = (LinkedList)recoverField_c.clone();
                  recoverField_c.clear();
                  Handler var5 = recoverMethod_b_de962d31();
                  if (null != var5) {
                     var5.removeMessages(1);
                  }
               }

               if (var3.size() > 0) {
                  for (Object recoveryElement_391 : var3) {
                     Runnable var12 = (Runnable)recoveryElement_391;
                     var12.run();
                  }
               }
            }
         } catch (Throwable var10) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("Sp", "sp commit task error, " + var10.getMessage());
         }
      }

      private static class Memberbb7fa60c extends Handler {
         Memberbb7fa60c(Looper var1) {
            super(var1);
         }

         @Override
         public void handleMessage(Message var1) {
            if (var1.what == 1) {
               RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("FUTURE_TASK", new RecoverTypeaf2b0401() {
                  @Override
                  public void recoverMethod_a_9b75() {
                     RecoverType5a690d2.Member3645a90f.recoverMethod_c_9b75();
                  }
               });
            }
         }
      }
   }
}
