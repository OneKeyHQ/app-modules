package cn.jiguang.l;

import android.content.Context;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07d5;
import cn.jiguang.j.RecoverType5a6a399;
import java.lang.reflect.Method;

public class RecoverType5a6ab19 {
   private static String recoverField_a;
   private static String recoverField_b = "";
   private static String recoverField_c = "";
   private static String recoverField_d = "";
   private static String recoverField_e = "";
   private static String recoverField_f = "";
   private static String recoverField_g = "";
   private static String recoverField_h = "";

   public static String recoverMethod_a_e501eb0d(final Context var0) {
      try {
         Object var1 = RecoverType5a6a399.recoverMethod_b_8f0f26ca(var0, 1019, 1800000L, 259200000L, new RecoverType5a6a399.Member368c2616() {
            @Override
            public Object recoverMethod_c_810c345d() {
               return RecoverType5a6ab19.recoverField_a;
            }

            @Override
            public void recoverMethod_b_5d527811(Object var1) {
               if (var1 instanceof String) {
                  RecoverType5a6ab19.recoverField_a = (String)var1;
               }
            }

            @Override
            public Object recoverMethod_b_810c345d() {
               String var1x = RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_x_e501eb0d(var0);
               if (TextUtils.isEmpty(var1x)) {
                  return "";
               } else {
                  var1x = var1x.toLowerCase();
                  if (var1x.contains("huawei")) {
                     return RecoverType5a6ab19.recoverMethod_k_73cf11cb();
                  } else if (var1x.contains("honor")) {
                     return RecoverType5a6ab19.recoverMethod_i_73cf11cb();
                  } else if (var1x.contains("xiaomi") || var1x.contains("blackshark")) {
                     return RecoverType5a6ab19.recoverMethod_o_73cf11cb();
                  } else if (var1x.contains("meizu")) {
                     return RecoverType5a6ab19.recoverMethod_n_73cf11cb();
                  } else if (var1x.contains("oppo") || var1x.contains("realme")) {
                     RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JRomVersionHelper", " getMethodValue getOppoVersion  ");
                     return RecoverType5a6ab19.recoverMethod_m_73cf11cb();
                  } else if (var1x.contains("vivo")) {
                     return RecoverType5a6ab19.recoverMethod_l_73cf11cb();
                  } else {
                     return var1x.contains("oneplus") ? RecoverType5a6ab19.recoverMethod_p_73cf11cb() : null;
                  }
               }
            }
         });
         if (var1 instanceof String) {
            recoverField_a = (String)var1;
         }
      } catch (Throwable var2) {
      }

      return recoverField_a;
   }

   private static String recoverMethod_i_73cf11cb() {
      if (!TextUtils.isEmpty(recoverField_h)) {
         return recoverField_h;
      }

      recoverField_h = recoverMethod_b_e96ea081("ro.build.display.id");
      return recoverField_h;
   }

   private static boolean recoverMethod_j_9b79() {
      try {
         Class var0 = Class.forName("com.huawei.system.BuildEx");
         Method var1 = var0.getMethod("getOsBrand");
         return "harmony".equalsIgnoreCase((String)var1.invoke(var0));
      } catch (Throwable var2) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JRomVersionHelper", "isHarmonyOS throwable=" + var2);
         return false;
      }
   }

   private static String recoverMethod_k_73cf11cb() {
      if (!TextUtils.isEmpty(recoverField_b)) {
         return recoverField_b;
      }

      boolean var0 = recoverMethod_j_9b79();
      if (var0) {
         recoverField_b = recoverMethod_b_e96ea081("hw_sc.build.platform.version");
      }

      if (TextUtils.isEmpty(recoverField_b)) {
         recoverField_b = recoverMethod_b_e96ea081("ro.build.version.emui");
      } else {
         recoverField_b = "harmony_os_" + recoverField_b;
      }

      return recoverField_b;
   }

   private static String recoverMethod_l_73cf11cb() {
      if (!TextUtils.isEmpty(recoverField_d)) {
         return recoverField_d;
      }

      recoverField_d = recoverMethod_b_e96ea081("ro.vivo.os.build.display.id");
      return recoverField_d;
   }

   private static String recoverMethod_m_73cf11cb() {
      if (!TextUtils.isEmpty(recoverField_c)) {
         return recoverField_c;
      }

      recoverField_c = recoverMethod_b_e96ea081("ro.build.version.oplusrom");
      if (TextUtils.isEmpty(recoverField_c)) {
         recoverField_c = recoverMethod_b_e96ea081("ro.build.version.opporom");
      }

      return recoverField_c;
   }

   private static String recoverMethod_n_73cf11cb() {
      if (!TextUtils.isEmpty(recoverField_g)) {
         return recoverField_g;
      }

      recoverField_g = recoverMethod_b_e96ea081("ro.build.display.id");
      return recoverField_g;
   }

   private static String recoverMethod_o_73cf11cb() {
      if (!TextUtils.isEmpty(recoverField_f)) {
         return recoverField_f;
      }

      recoverField_f = recoverMethod_b_e96ea081("ro.miui.ui.version.name");
      return recoverField_f;
   }

   private static String recoverMethod_p_73cf11cb() {
      if (!TextUtils.isEmpty(recoverField_e)) {
         return recoverField_e;
      }

      recoverField_e = recoverMethod_b_e96ea081("ro.rom.version");
      return recoverField_e;
   }

   private static String recoverMethod_b_e96ea081(String var0) {
      return RecoverTypeaf2b07d5.recoverMethod_l_e96ea081(var0);
   }
}
