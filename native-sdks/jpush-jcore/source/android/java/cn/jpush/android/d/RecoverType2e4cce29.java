package cn.jpush.android.d;

import android.content.Context;
import android.text.TextUtils;
import cn.jpush.android.ad.RecoverType9b4c5ae7;
import cn.jpush.android.ad.RecoverType9b4c5af2;
import cn.jpush.android.helper.Logger;
import java.io.Closeable;
import java.io.File;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverType2e4cce29 {
   private static Queue recoverField_a = null;

   private static synchronized void recoverMethod_a_a03a08cd(Context var0, String var1, ArrayList var2) {
      Logger.recoverMethod_v_e925fa09("MsgQueueUtils", "Action - saveObjects");
      if (var0 == null) {
         Logger.recoverMethod_w_e925fa09("MsgQueueUtils", "unexcepted , context is null");
      } else if (var2 == null) {
         Logger.recoverMethod_w_e925fa09("MsgQueueUtils", "mObjectList is null");
      } else {
         try {
            File var3 = new File(var0.getFilesDir(), var1);
            JSONArray var4 = new JSONArray();

            for (int var5 = 0; var5 < var2.size(); var5++) {
               RecoverType2e4cce27 var6 = (RecoverType2e4cce27)var2.get(var5);
               var4.put(var6.recoverMethod_a_a7dbfe3());
            }

            RecoverType9b4c5ae7.recoverMethod_a_e925fa0d(var3.getAbsolutePath(), var4.toString());
         } catch (Throwable var7) {
            Logger.recoverMethod_ww_e925fa09("MsgQueueUtils", "save Objects  error:" + var7.getMessage());
         }
      }
   }

   public static synchronized void recoverMethod_a_5ffc00fd(Context var0, String var1) {
      if (var0 == null) {
         Logger.recoverMethod_w_e925fa09("MsgQueueUtils", "unexcepted , context is null");
      } else {
         File var2 = var0.getFilesDir();
         if (var2 == null) {
            Logger.recoverMethod_w_e925fa09("MsgQueueUtils", "can't get files dir");
         } else {
            File var3 = new File(var2, var1);
            if (var3.exists()) {
               var3.delete();
            }
         }
      }
   }

   private static synchronized ArrayList recoverMethod_b_eb118457(Context var0, String var1) {
      if (var0 == null) {
         Logger.recoverMethod_w_e925fa09("MsgQueueUtils", "unexcepted , context is null");
         return null;
      }

      ArrayList var2 = new ArrayList();
      Object var3 = null;

      try {
         File var4 = new File(var0.getFilesDir(), var1);
         if (var4.exists()) {
            String var5 = RecoverType9b4c5ae7.recoverMethod_c_af215df4(var4);
            if (!TextUtils.isEmpty(var5)) {
               JSONArray var6 = new JSONArray(var5);
               if (var6 != null) {
                  for (int var7 = 0; var7 < var6.length(); var7++) {
                     JSONObject var8 = var6.getJSONObject(var7);
                     RecoverType2e4cce27 var9 = RecoverType2e4cce27.recoverMethod_a_ef8016f5(var8);
                     var2.add(var9);
                  }
               }
            }
         }
      } catch (Throwable var13) {
         Logger.recoverMethod_w_e925fa09("MsgQueueUtils", "load objects error:" + var13.getMessage());
         recoverMethod_a_5ffc00fd(var0, var1);
      } finally {
         RecoverType9b4c5af2.recoverMethod_a_d17885d6((Closeable)var3);
      }

      return var2;
   }

   public static boolean recoverMethod_a_ae3a219f(Context var0, RecoverType2e4cce27 var1) {
      if (recoverField_a == null) {
         recoverField_a = new ConcurrentLinkedQueue();

         try {
            ArrayList var2 = recoverMethod_b_eb118457(var0, "msg_queue_v350");
            if (var2 != null && !var2.isEmpty()) {
               for (Object recoveryElement_99 : var2) {
                  RecoverType2e4cce27 var4 = (RecoverType2e4cce27)recoveryElement_99;
                  recoverField_a.offer(var4);
               }
            }
         } catch (Exception var6) {
            Logger.recoverMethod_ww_e925fa09("MsgQueueUtils", "init lastMsgQueue failed:" + var6.getMessage());
         }
      }

      if (var0 == null) {
         Logger.recoverMethod_ww_e925fa09("MsgQueueUtils", "#unexcepted - context was null");
         return false;
      }

      if (var1 == null) {
         Logger.recoverMethod_ww_e925fa09("MsgQueueUtils", "#unexcepted - entityKey was null");
      }

      if (recoverField_a.contains(var1)) {
         Logger.recoverMethod_e_e925fa09("MsgQueueUtils", "Duplicated msg. Give up processing - " + var1);
         return true;
      }

      if (recoverField_a.size() >= 200) {
         recoverField_a.poll();
      }

      recoverField_a.offer(var1);

      try {
         ArrayList var7 = recoverMethod_b_eb118457(var0, "msg_queue_v350");
         if (var7 == null) {
            var7 = new ArrayList();
         }

         if (var7.size() >= 50) {
            var7.remove(0);
         }

         var7.add(var1);
         recoverMethod_a_a03a08cd(var0, "msg_queue_v350", var7);
      } catch (Exception var5) {
         Logger.recoverMethod_ww_e925fa09("MsgQueueUtils", "msg save in sp failed:" + var5.getMessage());
      }

      return false;
   }
}
