package cn.jpush.android.x;

import android.content.Context;
import android.text.TextUtils;
import cn.jpush.android.cache.Key;
import cn.jpush.android.cache.RecoverTypea0793a19;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.helper.RecoverType8b455831;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverType2e4d193e {
   private static final Object recoverField_a = new Object();
   private static volatile Queue recoverField_b;

   public static boolean recoverMethod_a_7555e60(Context var0, String var1, String var2, int var3) {
      try {
         JSONArray var4 = recoverMethod_a_5d5a71dd(var0);
         if (var4 != null && var4.length() != 0) {
            JSONArray var5 = new JSONArray();
            long var6 = System.currentTimeMillis();
            boolean var8 = false;

            for (int var9 = 0; var9 < var4.length(); var9++) {
               JSONObject var10 = var4.getJSONObject(var9);
               long var11 = var10.getLong("time");
               int var13 = var10.optInt("type", -1);
               if (var6 < var11 + 86400000L) {
                  var5.put(var10);
                  if (!var8) {
                     String var14 = var10.getString("id");
                     if (TextUtils.isEmpty(var1) || !var1.equals(var14) || var13 != -1 && var3 != var13) {
                        if (!TextUtils.isEmpty(var2) && var2.equals(var14) && (var13 == -1 || var3 == var13)) {
                           var8 = true;
                           RecoverType8b455831.recoverMethod_a_36ca9ac3(var1, 1033L, var0);
                        }
                     } else {
                        var8 = true;
                        RecoverType8b455831.recoverMethod_a_36ca9ac3(var1, 1032L, var0);
                     }
                  }
               }
            }

            if (var5.length() != var4.length()) {
               String var16 = var5.length() > 0 ? var5.toString() : null;
               Logger.recoverMethod_d_e925fa09("NotificationRecords", "saveString when check=" + var16);
               RecoverTypea0793a19.set(var0, Key.NotiCancel().set(var16));
            }

            return var8;
         } else {
            return false;
         }
      } catch (Throwable var15) {
         Logger.recoverMethod_w_e925fa09("NotificationRecords", "isAlreadyCancel e:" + var15);
         return false;
      }
   }

   private static JSONArray recoverMethod_a_5d5a71dd(Context var0) {
      String var1 = (String)RecoverTypea0793a19.get(var0, Key.NotiCancel());
      JSONArray var2 = null;
      if (!TextUtils.isEmpty(var1)) {
         try {
            Logger.recoverMethod_d_e925fa09("NotificationRecords", "read=" + var1);
            var2 = new JSONArray(var1);
         } catch (Throwable var4) {
            Logger.recoverMethod_w_e925fa09("NotificationRecords", "read NotiCancel e:" + var4);
         }
      }

      return var2 != null ? var2 : new JSONArray();
   }

   public static void recoverMethod_a_4eab7a9f(Context var0, List var1, int var2) {
      try {
         JSONArray var3 = recoverMethod_a_5d5a71dd(var0);
         JSONArray var4 = new JSONArray();
         long var5 = System.currentTimeMillis();

         for (int var7 = 0; var7 < var3.length(); var7++) {
            JSONObject var8 = var3.getJSONObject(var7);
            String var9 = var8.getString("id");
            if (var1 != null && var1.remove(var9)) {
               var8.put("time", var5);
            }

            long var10 = var8.getLong("time");
            if (var5 < var10 + 86400000L) {
               var4.put(var8);
            }
         }

         if (var1 != null) {
            for (Object recoveryElement_99 : var1) {
               String var15 = (String)recoveryElement_99;
               JSONObject var16 = new JSONObject();
               var16.put("time", var5);
               var16.put("id", var15);
               var16.put("type", var2);
               var4.put(var16);
            }
         }

         String var14 = var4.length() > 0 ? var4.toString() : null;
         Logger.recoverMethod_d_e925fa09("NotificationRecords", "saveString=" + var14);
         RecoverTypea0793a19.set(var0, Key.NotiCancel().set(var14));
      } catch (Throwable var12) {
         Logger.recoverMethod_w_e925fa09("NotificationRecords", "save NotiCancel e:" + var12);
      }
   }

   private static void recoverMethod_b_f0554df3(Context var0) {
      if (recoverField_b == null) {
         synchronized (RecoverType2e4d193e.class) {
            if (recoverField_b == null) {
               recoverField_b = new ConcurrentLinkedQueue();
               String var2 = (String)RecoverTypea0793a19.get(var0, Key.NotiShow());
               if (!TextUtils.isEmpty(var2)) {
                  String[] var3 = var2.split(",");

                  for (String var7 : var3) {
                     if (!TextUtils.isEmpty(var7)) {
                        recoverField_b.offer(var7);
                     }
                  }
               }
            }
         }
      }
   }

   public static boolean recoverMethod_a_5ffc0101(Context var0, String var1) {
      recoverMethod_b_f0554df3(var0);
      return recoverField_b.contains(var1);
   }

   public static void recoverMethod_b_5ffc00fd(Context var0, String var1) {
      try {
         recoverMethod_b_f0554df3(var0);
         if (TextUtils.isEmpty(var1)) {
            return;
         }

         synchronized (recoverField_a) {
            if (recoverField_b.size() >= 70) {
               recoverField_b.poll();
            }

            recoverField_b.offer(var1);
            StringBuilder var3 = new StringBuilder();

            for (Object recoveryElement_156 : recoverField_b) {
               String var5 = (String)recoveryElement_156;
               var3.append(var5 + ",");
            }

            RecoverTypea0793a19.set(var0, Key.NotiShow().set(var3.toString()));
         }
      } catch (Throwable var8) {
         Logger.recoverMethod_e_e925fa09("NotificationRecords", "saveNotiHistory failed,messageId = " + var1);
      }
   }
}
