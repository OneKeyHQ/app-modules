package cn.jiguang.al;

import android.content.Context;
import android.text.TextUtils;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ao.RecoverTypeaf2aa267;
import cn.jiguang.ao.RecoverTypeaf2aa26c;
import cn.jiguang.ar.RecoverTypeaf2aadab;
import cn.jiguang.bh.RecoverTypeaf2afc7e;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bj.RecoverTypeaf2b0403;
import cn.jiguang.bk.RecoverTypeaf2b07c4;
import cn.jiguang.bk.RecoverTypeaf2b07c9;
import cn.jiguang.bk.RecoverTypeaf2b07cb;
import cn.jiguang.bk.RecoverTypeaf2b07d4;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import java.util.LinkedList;
import org.json.JSONObject;

public class RecoverTypeaf2a972c {
   private static final String recoverField_a = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{72, 109, 124, 102, 98, 10, 15, 54, 123, 101, 125, 83, 69, 107, 124, 56, 123, 64, 85, 106, 96, 56, 114, 94}
   );
   private static final String recoverField_b = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{88, 84, 109, 120, 101, 43, 31, 15, 106, 123, 122, 114, 85, 82, 109, 37, 127, 97, 70, 22, 55, 98, 102, 100, 67, 72, 55, 107, 120}
   );
   private static String recoverField_c;
   private static Integer recoverField_d;
   private static final LinkedList recoverField_e = new LinkedList();

   private static boolean recoverMethod_b_1a54e374(Context var0, int var1) {
      boolean var2 = (Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_j_48794abf());
      if (!var2) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSslCertUpdateManager", "not use tcp ssl, so need not update cert.");
         return false;
      }

      int var3 = recoverMethod_b_9b68();
      if (var3 >= var1) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSslCertUpdateManager", "it need not update cert, l_cert_ver: " + var3 + ", new_cert_ver: " + var1);
         return false;
      }

      RecoverTypeaf2aadab.recoverMethod_a_1385ff(false);
      synchronized (RecoverTypeaf2a972c.class) {
         recoverField_c = null;
         RecoverTypeaf2b07c9.recoverMethod_d_aad3b1ff("ccp.catch.");
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSslCertUpdateManager", "should update, clear cert");
         return true;
      }
   }

   public static void recoverMethod_a_1a54e370(final Context var0, int var1) {
      if (recoverMethod_b_1a54e374(var0, var1)) {
         RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("FUTURE_TASK", new RecoverTypeaf2b0401("JSslCertUpdateManager") {
            @Override
            public void recoverMethod_a_9b75() {
               try {
                  String var1x = RecoverTypeaf2a972c.recoverMethod_b_e501eb0d(var0);
                  if (TextUtils.isEmpty(var1x)) {
                     RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_13462e(15);
                     return;
                  }

                  synchronized (RecoverTypeaf2a972c.class) {
                     JSONObject var3 = new JSONObject(var1x);
                     int var4 = var3.optInt("ver", 0);
                     String var5 = var3.optString("cert", "");
                     if (TextUtils.isEmpty(var5)) {
                        RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_13462e(19);
                        return;
                     }

                     String var6 = RecoverTypeaf2b07d4.recoverMethod_b_8ea82db7(var5, RecoverType5a681cd.recoverField_i);
                     if (!TextUtils.isEmpty(var6) && var4 > 0) {
                        RecoverTypeaf2a972c.recoverField_c = var6;
                        RecoverTypeaf2a972c.recoverField_d = var4;
                        boolean var7 = RecoverTypeaf2b07c9.recoverMethod_a_27ad0523("ccp.catch.", var3);
                        RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_13462e(var7 ? 18 : 17);
                        RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSslCertUpdateManager", "update cert status: " + (var7 ? "succeed" : "failed"));
                     } else {
                        RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_13462e(17);
                     }
                  }
               } catch (Throwable var10) {
                  RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_13462e(16);
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JSslCertUpdateManager", "checkCrtUpdate failed, " + var10.getMessage());
               }
            }
         });
      }
   }

   public static String recoverMethod_a_73cf11cb() {
      recoverMethod_c_9b75();
      return !TextUtils.isEmpty(recoverField_c) ? recoverField_c : "";
   }

   public static int recoverMethod_b_9b68() {
      recoverMethod_c_9b75();
      return recoverField_d != null ? recoverField_d : 0;
   }

   private static void recoverMethod_c_9b75() {
      if (recoverField_d == null && TextUtils.isEmpty(recoverField_c)) {
         JSONObject var0;
         synchronized (RecoverTypeaf2a972c.class) {
            Context var2 = RecoverTypeaf2b07c4.recoverMethod_a_6263c3eb(null);
            var0 = RecoverTypeaf2b07c9.recoverMethod_a_fd5e2d6d("ccp.catch.");
            if (null == var0 || "{}".equals(var0.toString())) {
               return;
            }
         }

         String var5 = var0.optString("cert", "");
         if (!TextUtils.isEmpty(var5)) {
            recoverField_c = RecoverTypeaf2b07d4.recoverMethod_b_8ea82db7(var5, RecoverType5a681cd.recoverField_i);
            recoverField_d = var0.optInt("ver", 0);
         }
      }
   }

   private static String recoverMethod_b_e501eb0d(Context var0) {
      try {
         String var1 = RecoverTypeaf2a9723.recoverMethod_a_e501eb0d(var0);
         if (TextUtils.isEmpty(var1)) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JSslCertUpdateManager", "appKey is empty, return");
            return null;
         }

         for (Object recoveryElement_133 : recoverField_e) {
            String var4 = (String)recoveryElement_133;
            if (TextUtils.isEmpty(var4)) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JSslCertUpdateManager", "url is empty, return it");
            } else {
               JSONObject var5 = new JSONObject();
               var5.put("appkey", var1);
               var5.put("sdkver", RecoverType5a681cd.recoverField_b);
               var5.put("platform", 0);
               RecoverTypeaf2aa26c var6 = RecoverTypeaf2aa267.recoverMethod_a_ab3ea53a(var4, var5.toString(), var0, true, 3, 2);
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
                  "JSslCertUpdateManager",
                  "update cert code[" + var6.recoverMethod_a_9b68() + "] from url=" + var4 + "\n body=" + var6.recoverMethod_b_73cf11cb()
               );
               if (var6.recoverMethod_a_9b68() == 0) {
                  String var7 = var6.recoverMethod_b_73cf11cb();
                  if (!TextUtils.isEmpty(var7)) {
                     JSONObject var8 = new JSONObject(var7);
                     if (var8.optInt("code") != 200) {
                        return null;
                     }

                     return var7;
                  }
               }
            }
         }
      } catch (Throwable var9) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JSslCertUpdateManager", "request crt failed, " + var9.getMessage());
      }

      return null;
   }

   static {
      recoverField_e.add(recoverField_a);
      recoverField_e.add(recoverField_b);
   }
}
