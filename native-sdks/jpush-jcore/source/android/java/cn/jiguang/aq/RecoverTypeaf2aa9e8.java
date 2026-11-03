package cn.jiguang.aq;

import android.content.Context;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.api.JCoreManager;
import cn.jiguang.bk.RecoverTypeaf2b07cb;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import java.util.LinkedHashMap;
import org.json.JSONObject;

public class RecoverTypeaf2aa9e8 {
   private static final LinkedHashMap recoverField_p = new LinkedHashMap();
   public static final String recoverField_a = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{98, 30, 74, 105, 125, 101, 121, 30, 67, 119});
   public static final String recoverField_b = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{83, 112, 123, 56, 123, 64, 85, 106, 96, 56, 120, 95});
   public static final String recoverField_c = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{85, 65, 106, 113, 98, 126, 93, 69, 106, 123, 119, 118, 85, 14, 122, 103, 123}
   );
   public static final String recoverField_d = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{67, 73, 106, 37, 127, 97, 70, 22, 55, 98, 102, 100, 67, 72, 55, 107, 120}
   );
   public static final String recoverField_e = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{39, 32, 5, 14, 40, 58, 38, 63, 1, 25, 42, 38, 39, 38, 9});
   public static final String recoverField_f = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{40, 57, 37, 63, 4, 21, 55, 60, 38, 63, 2, 18, 47});
   public static final String recoverField_g = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{40, 58, 38, 63, 4, 22, 55, 57, 32, 63, 2, 20, 43});
   public static final String recoverField_h = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{40, 58, 38, 63, 4, 22, 55, 48, 34, 63, 1, 16, 33});
   private static final LinkedHashMap recoverField_q = new LinkedHashMap();
   public static final String recoverField_i;
   public static final String recoverField_j;
   public static final String recoverField_k;
   private static final LinkedHashMap recoverField_r;
   private static final LinkedHashMap recoverField_s;
   private static String recoverField_t;
   private static String recoverField_u;
   private static String recoverField_v;
   private static String recoverField_w;
   public static String recoverField_l;
   public static int recoverField_m;
   public static String recoverField_n;
   public static int recoverField_o;

   public static LinkedHashMap recoverMethod_a_d5e96a91() {
      return JCoreManager.isTestEnv() && !recoverField_r.isEmpty() ? recoverField_r : recoverField_p;
   }

   public static LinkedHashMap recoverMethod_b_d5e96a91() {
      return JCoreManager.isTestEnv() && !recoverField_s.isEmpty() ? recoverField_s : recoverField_q;
   }

   public static String recoverMethod_a_e501eb0d(Context var0) {
      if (JCoreManager.isTestEnv() && !TextUtils.isEmpty(recoverField_t)) {
         return recoverField_t;
      }

      String var1 = (String)RecoverType5a690d2.recoverMethod_b_413ed21(var0, RecoverType5a690d1.recoverMethod_aw_48794abf());
      return !TextUtils.isEmpty(var1) ? var1 : recoverField_i;
   }

   public static String recoverMethod_b_e501eb0d(Context var0) {
      if (JCoreManager.isTestEnv() && !TextUtils.isEmpty(recoverField_u)) {
         return recoverField_u;
      }

      String var1 = (String)RecoverType5a690d2.recoverMethod_b_413ed21(var0, RecoverType5a690d1.recoverMethod_ax_48794abf());
      return !TextUtils.isEmpty(var1) ? var1 : recoverField_j;
   }

   public static String recoverMethod_c_73cf11cb() {
      return JCoreManager.isTestEnv() && !TextUtils.isEmpty(recoverField_v) ? recoverField_v : recoverField_k;
   }

   public static String recoverMethod_d_73cf11cb() {
      return JCoreManager.isTestEnv() && !TextUtils.isEmpty(recoverField_w) ? recoverField_w : "";
   }

   public static void recoverMethod_a_5ffc00fd(Context var0, String var1) {
      if (TextUtils.isEmpty(var1)) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("HostConfig", "conn info was empty");
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("HostConfig", "get conn info=" + var1);

         try {
            JSONObject var2 = new JSONObject(var1);
            String var3 = var2.optString("srv");
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("HostConfig", "save srvHost:" + var3);
            if (!TextUtils.isEmpty(var3)) {
               RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_ax_48794abf().recoverMethod_a_4e9722a3(var3));
            }

            String var4 = var2.optString("conn");
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("HostConfig", "save connHost:" + var4);
            if (!TextUtils.isEmpty(var4)) {
               RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_aw_48794abf().recoverMethod_a_4e9722a3(var4));
            }
         } catch (Throwable var5) {
         }
      }
   }

   static {
      recoverField_p.put(recoverField_a, 19000);
      recoverField_p.put(recoverField_b, 19000);
      recoverField_p.put(recoverField_c, 19000);
      recoverField_p.put(recoverField_d, 19000);

      try {
         recoverField_q.put(recoverField_e, 19000);
         recoverField_q.put(recoverField_f, 19000);
         recoverField_q.put(recoverField_g, 19000);
         recoverField_q.put(recoverField_h, 19000);
      } catch (Throwable var1) {
      }

      recoverField_i = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{112, 101, 32, 37, 30, 74, 105, 125, 101, 121, 30, 67, 119});
      recoverField_j = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
         new byte[]{70, 97, 123, 39, 4, 14, 70, 124, 117, 97, 30, 74, 105, 125, 101, 121, 30, 67, 119}
      );
      recoverField_k = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
         new byte[]{70, 120, 101, 120, 67, 14, 70, 125, 114, 97, 30, 74, 105, 125, 101, 121, 30, 67, 119}
      );
      recoverField_r = new LinkedHashMap();
      recoverField_s = new LinkedHashMap();
      recoverField_t = "";
      recoverField_u = "";
      recoverField_v = "";
      recoverField_w = "";
   }
}
