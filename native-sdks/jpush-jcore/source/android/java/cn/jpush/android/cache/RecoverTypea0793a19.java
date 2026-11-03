package cn.jpush.android.cache;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Build.VERSION;
import android.text.TextUtils;
import cn.jiguang.internal.JConstants;
import cn.jpush.android.ad.RecoverType9b4c5aea;
import cn.jpush.android.ad.RecoverType9b4c5af0;
import cn.jpush.android.ad.RecoverType9b4c5af2;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.local.JPushConstants;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RecoverTypea0793a19 {
   private static final Map recoverField_a = new ConcurrentHashMap();

   public static void set(Context var0, Key... var1) {
      if (var1 != null && var1.length > 0) {
         if (recoverMethod_a_9b79()) {
            Logger.recoverMethod_d_e925fa09("Sp", "main thread, async sp set");
            recoverMethod_a_d8745aa0(var0, true, var1);
         } else {
            sync_set(var0, var1);
         }
      }
   }

   public static void sync_set(Context var0, Key... var1) {
      if (var1 != null && var1.length > 0) {
         recoverMethod_a_d8745aa0(var0, false, var1);
      }
   }

   private static boolean recoverMethod_a_9b79() {
      return Looper.getMainLooper().getThread().getId() == Thread.currentThread().getId();
   }

   private static void recoverMethod_a_d8745aa0(final Context var0, boolean var1, final Key... var2) {
      RecoverType9b4c5aea var3 = new RecoverType9b4c5aea() {
         @Override
         public void recoverMethod_a_9b75() {
            try {
               HashMap var1x = new HashMap();

               for (Key var5 : var2) {
                  Editor var6 = (Editor)var1x.get(var5.recoverField_a);
                  if (var6 == null) {
                     SharedPreferences var7 = RecoverTypea0793a19.recoverMethod_b_60e33291(var0, var5.recoverField_a);
                     if (var7 != null) {
                        var6 = var7.edit();
                        var1x.put(var5.recoverField_a, var6);
                     }
                  }

                  RecoverTypea0793a19.recoverMethod_b_54ebfa5e(var6, var5.recoverField_b, var5.recoverField_c, var5.recoverField_e);
               }

               for (Object recoveryElement_67 : var1x.keySet()) {
                  String var10 = (String)recoveryElement_67;
                  Editor var11 = (Editor)var1x.get(var10);
                  if (null != var11) {
                     boolean var12 = var11.commit();
                     if (!var12) {
                        Logger.recoverMethod_w_e925fa09("Sp", "sp commit failed, sp_file: " + var10);
                     }
                  }
               }
            } catch (Throwable var8) {
               Logger.recoverMethod_w_e925fa09("Sp", "sp commit failed, " + var8.getMessage());
            }
         }
      };

      try {
         if (var1) {
            RecoverTypea0793a19.Member67131c96.recoverMethod_a_784c1003(var0, var3, false);
         } else {
            var3.run();
         }
      } catch (Throwable var5) {
      }
   }

   public static Object get(Context var0, Key var1) {
      Object var2 = getWithNull(var0, var1);
      return var2 != null ? var2 : var1.recoverField_c;
   }

   public static Object getWithNull(Context var0, Key var1) {
      Object var2 = recoverMethod_a_988428dd(recoverMethod_b_60e33291(var0, var1.recoverField_a), var1.recoverField_b, var1.recoverField_c);
      if (var2 == null && var1.recoverField_d) {
         var2 = recoverMethod_a_988428dd(reload(var0, var1.recoverField_a), var1.recoverField_b, var1.recoverField_c);
      }

      if (var2 != null) {
         if (var1.recoverField_e && var2 instanceof String && ((String)var2).length() > 0) {
            var2 = RecoverType9b4c5af0.recoverMethod_b_8ea82db7((String)var2, RecoverType9b4c5af2.recoverField_a);
         }

         var1.set(var2);
         return var2;
      } else {
         return null;
      }
   }

   public static void read(Context var0, Key... var1) {
      if (var1 != null && var1.length > 0) {
         for (Key var5 : var1) {
            get(var0, var5);
         }
      }
   }

   public static void clear(Context var0, String var1) {
      SharedPreferences var2 = recoverMethod_b_60e33291(var0, var1);
      if (var2 != null) {
         var2.edit().clear().apply();
      }
   }

   public static SharedPreferences reload(Context var0, String var1) {
      var0 = JPushConstants.getAppContext(var0);
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
         } else if (var2 instanceof Boolean) {
            var0.putBoolean(var1, (Boolean)var2);
         } else if (var2 instanceof String) {
            String var4 = (String)var2;
            if (var3 && ((String)var2).length() > 0) {
               var2 = RecoverType9b4c5af0.recoverMethod_a_8ea82db7((String)var2, RecoverType9b4c5af2.recoverField_a);
            }

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

   private static Object recoverMethod_a_988428dd(SharedPreferences var0, String var1, Object var2) {
      if (var0 != null && var0.contains(var1)) {
         try {
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
         } catch (Throwable var4) {
         }

         return null;
      } else {
         return null;
      }
   }

   private static SharedPreferences recoverMethod_b_60e33291(Context var0, String var1) {
      if (TextUtils.isEmpty(var1)) {
         return null;
      }

      SharedPreferences var2 = (SharedPreferences)recoverField_a.get(var1);
      if (var2 == null) {
         var0 = JPushConstants.getAppContext(var0);
         if (var0 != null) {
            var2 = var0.getSharedPreferences(var1, 0);
            recoverField_a.put(var1, var2);
            recoverMethod_c_5ffc00fd(var0, var1);
         }
      }

      return var2;
   }

   private static void recoverMethod_c_5ffc00fd(Context var0, String var1) {
   }

   private static class Member67131c96 {
      private static Handler recoverField_a = null;
      private static final Object recoverField_b = new Object();
      private static final LinkedList recoverField_c = new LinkedList();
      private static boolean recoverField_d = true;
      private static Context recoverField_e;
      private static final Object recoverField_f = new Object();

      private static Handler recoverMethod_b_ce77bdf3(Context var0) {
         recoverField_e = var0 != null ? var0.getApplicationContext() : JPushConstants.getAppContext(null);
         if (recoverField_a == null) {
            synchronized (recoverField_b) {
               if (recoverField_a == null) {
                  recoverField_a = new RecoverTypea0793a19.Member67131c96.Membereebe53d3(Looper.getMainLooper());
               }
            }
         }

         return recoverField_a;
      }

      public static void recoverMethod_a_784c1003(Context var0, Runnable var1, boolean var2) {
         try {
            Handler var3 = recoverMethod_b_ce77bdf3(var0);
            if (null != var3) {
               synchronized (recoverField_b) {
                  recoverField_c.add(var1);
                  if (var2 && recoverField_d) {
                     var3.sendEmptyMessageDelayed(1, 100L);
                  } else {
                     var3.sendEmptyMessage(1);
                  }
               }
            }
         } catch (Throwable var7) {
            Logger.recoverMethod_w_e925fa09("Sp", "sp task add queue failed, " + var7.getMessage());
         }
      }

      private static void recoverMethod_c_f0554df3(Context var0) {
         try {
            long var1 = System.currentTimeMillis();
            synchronized (recoverField_f) {
               LinkedList var4;
               synchronized (recoverField_b) {
                  var4 = (LinkedList)recoverField_c.clone();
                  recoverField_c.clear();
                  Handler var6 = recoverMethod_b_ce77bdf3(var0);
                  if (null != var6) {
                     var6.removeMessages(1);
                  }
               }

               if (var4.size() > 0) {
                  for (Object recoveryElement_272 : var4) {
                     Runnable var13 = (Runnable)recoveryElement_272;
                     var13.run();
                  }

                  if (JConstants.DEBUG_MODE) {
                     Logger.recoverMethod_d_e925fa09("Sp", "sp processing " + var4.size() + " items took " + (System.currentTimeMillis() - var1) + " ms");
                  }
               }
            }
         } catch (Throwable var11) {
            Logger.recoverMethod_w_e925fa09("Sp", "sp commit task error, " + var11.getMessage());
         }
      }

      private static class Membereebe53d3 extends Handler {
         Membereebe53d3(Looper var1) {
            super(var1);
         }

         @Override
         public void handleMessage(Message var1) {
            if (var1.what == 1) {
               JCoreHelper.futureExecutor(RecoverTypea0793a19.Member67131c96.recoverField_e, new RecoverType9b4c5aea() {
                  @Override
                  public void recoverMethod_a_9b75() {
                     RecoverTypea0793a19.Member67131c96.recoverMethod_c_f0554df3(RecoverTypea0793a19.Member67131c96.recoverField_e);
                  }
               });
            }
         }
      }
   }
}
