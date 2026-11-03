package cn.jiguang.al;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.at.RecoverTypeaf2ab531;
import cn.jiguang.bh.RecoverTypeaf2afc7f;
import cn.jiguang.bj.RecoverTypeaf2b0403;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07c4;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import cn.jiguang.m.RecoverType5a6aed9;
import java.util.concurrent.ConcurrentHashMap;
import org.json.JSONObject;

public class RecoverTypeaf2a9729 {
   private Context recoverField_a;
   private String recoverField_b = "JProcessInternalManager";
   private ConcurrentHashMap recoverField_c = new ConcurrentHashMap();
   private boolean recoverField_d;

   private RecoverTypeaf2a9729() {
      this.recoverField_a = RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d();
   }

   public static RecoverTypeaf2a9729 recoverMethod_a_c692b39d() {
      return RecoverTypeaf2a9729.Member8ee175a6.recoverField_a;
   }

   public void recoverMethod_a_5dc9c75(String var1, Bundle var2) {
      String var3 = this.recoverField_a.getPackageName();
      this.recoverMethod_a_1c1172ff(var1, var2, var3);
   }

   private Bundle recoverMethod_a_c7232d03(String var1) {
      String var2 = RecoverTypeaf2a9726.recoverMethod_a_e501eb0d(this.recoverField_a);
      return this.recoverMethod_a_c91b468d(var1, var2);
   }

   private void recoverMethod_a_1c1172ff(final String var1, final Bundle var2, final String var3) {
      try {
         RecoverTypeaf2b0403.recoverMethod_a_97dca6fd(
            "AIDL_TASK",
            new Runnable() {
               @Override
               public void run() {
                  RecoverTypeaf2a9727.recoverMethod_a_c692b35f()
                     .recoverMethod_a_1785417b(RecoverTypeaf2a9729.this.recoverField_a, "INTERNAL_API", var1, var2, var3);
               }
            }
         );
      } catch (Throwable var5) {
         RecoverTypeaf2a9ae9.recoverMethod_d_1765c98c(this.recoverField_b, "Failed to send data to process: " + var3, var5);
      }
   }

   private Bundle recoverMethod_a_c91b468d(String var1, String var2) {
      try {
         Bundle var3 = RecoverTypeaf2a9727.recoverMethod_a_c692b35f().recoverMethod_a_1785417b(this.recoverField_a, "INTERNAL_API", var1, null, var2);
         return var3 != null ? var3 : new Bundle();
      } catch (Throwable var4) {
         RecoverTypeaf2a9ae9.recoverMethod_d_1765c98c(this.recoverField_b, "Failed to get data from process: " + var2, var4);
         return new Bundle();
      }
   }

   public Object recoverMethod_a_54cf32c4(int var1) {
      switch (var1) {
         case 67:
            return this.recoverMethod_b_aad3b203("isTcpLoggedIn");
         case 68:
         case 94:
            return this.recoverMethod_d_f6abc313("deviceinfo");
         case 91:
            return this.recoverMethod_e_f6abc313("get_loc_info");
         default:
            return null;
      }
   }

   private boolean recoverMethod_b_aad3b203(final String var1) {
      try {
         Object var2 = this.recoverField_c.get(var1);
         if (var2 instanceof Boolean) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(this.recoverField_b, "return state memory: " + var2);
            return (Boolean)var2;
         } else {
            RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("AIDL_TASK", new Runnable() {
               @Override
               public void run() {
                  RecoverTypeaf2a9729.this.recoverMethod_c_aad3b1ff(var1);
               }
            });
            Boolean var3 = (Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_a, RecoverType5a690d1.recoverMethod_z_48794abf());
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(this.recoverField_b, "return state sp: " + var3);
            return var3;
         }
      } catch (Throwable var4) {
         RecoverTypeaf2a9ae9.recoverMethod_d_1765c98c(this.recoverField_b, "Error in getConnectionState", var4);
         return false;
      }
   }

   private void recoverMethod_c_aad3b1ff(String var1) {
      try {
         Bundle var2 = this.recoverMethod_a_c7232d03(var1);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(this.recoverField_b, "Updated connection state bundle: " + var2);
         if (var2 != null) {
            boolean var3 = var2.getBoolean("state", false);
            this.recoverField_c.put(var1, var3);
            RecoverType5a690d2.recoverMethod_a_abcb8d40(this.recoverField_a, RecoverType5a690d1.recoverMethod_z_48794abf().recoverMethod_a_4e9722a3(var3));
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(this.recoverField_b, "Updated connection state cache: " + var3);
         }
      } catch (Throwable var4) {
         RecoverTypeaf2a9ae9.recoverMethod_d_1765c98c(this.recoverField_b, "Error updating connection state cache", var4);
      }
   }

   private Object recoverMethod_d_f6abc313(String var1) {
      return this.recoverMethod_a_576f3a95(var1, RecoverType5a690d1.recoverMethod_aL_48794abf());
   }

   private Object recoverMethod_e_f6abc313(String var1) {
      return this.recoverMethod_a_576f3a95(var1, RecoverType5a690d1.recoverMethod_aM_48794abf());
   }

   private Object recoverMethod_a_576f3a95(final String var1, final RecoverType5a690d1 var2) {
      try {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09(this.recoverField_b, "get action=" + var1);
         Object var3 = this.recoverField_c.get(var1);
         if (var3 instanceof JSONObject) {
            RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09(this.recoverField_b, "get from memory cachedData=" + var3);
            return var3;
         }

         RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("AIDL_TASK", new Runnable() {
            @Override
            public void run() {
               RecoverTypeaf2a9729.this.recoverMethod_b_c9d67e3d(var1, var2);
            }
         });
         String var4 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(this.recoverField_a, var2);
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09(this.recoverField_b, "get from sp cachedData=" + var4);
         if (!TextUtils.isEmpty(var4)) {
            return new JSONObject(var4);
         }
      } catch (Throwable var5) {
         RecoverTypeaf2a9ae9.recoverMethod_d_1765c98c(this.recoverField_b, "Error in getGenericData", var5);
      }

      return null;
   }

   private void recoverMethod_b_c9d67e3d(String var1, RecoverType5a690d1 var2) {
      try {
         Bundle var3 = this.recoverMethod_a_c7232d03(var1);
         this.recoverMethod_a_8817bcb3(var1, var3, var2);
      } catch (Throwable var4) {
         RecoverTypeaf2a9ae9.recoverMethod_d_1765c98c(this.recoverField_b, "Error updating generic data cache", var4);
      }
   }

   private void recoverMethod_a_8817bcb3(String var1, Bundle var2, RecoverType5a690d1 var3) {
      try {
         if (var2 != null) {
            JSONObject var4 = new JSONObject();

            for (String var6 : var2.keySet()) {
               var4.put(var6, var2.get(var6));
            }

            this.recoverField_c.put(var1, var4);
            RecoverType5a690d2.recoverMethod_a_abcb8d40(this.recoverField_a, var3.recoverMethod_a_4e9722a3(var4.toString()));
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(this.recoverField_b, "Updated generic data cache: " + var4.toString());
         }
      } catch (Throwable var7) {
         RecoverTypeaf2a9ae9.recoverMethod_d_1765c98c(this.recoverField_b, "Error updating generic data cache", var7);
      }
   }

   public Bundle recoverMethod_b_fa0ea5f9(String var1, Bundle var2) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(this.recoverField_b, "action=" + var1);
      String var3 = RecoverTypeaf2b07c1.recoverMethod_a_e501eb0d(this.recoverField_a);
      String var4 = RecoverTypeaf2a9726.recoverMethod_a_e501eb0d(this.recoverField_a);
      if (this.recoverField_a.getPackageName().equals(var3)) {
         return this.recoverMethod_d_fa0ea5f9(var1, var2);
      } else {
         return !TextUtils.isEmpty(var3) && var3.equals(var4) ? this.recoverMethod_c_fa0ea5f9(var1, var2) : null;
      }
   }

   private Bundle recoverMethod_c_fa0ea5f9(String var1, Bundle var2) {
      if (var2 == null) {
         var2 = new Bundle();
      }

      switch (var1) {
         case "isTcpLoggedIn":
            boolean var5 = RecoverTypeaf2ab531.recoverMethod_a_c6965695().recoverMethod_d_9b79();
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(this.recoverField_b, "from jcore isLoggedIn=" + var5);
            var2.putBoolean("state", var5);
            break;
         case "get_loc_info":
            Object var7 = RecoverType5a6aed9.recoverMethod_a_b00c10b9(this.recoverField_a, "get_loc_info", null);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(this.recoverField_b, "from  jcore LOC_INFO bundle=" + var7);
            if (var7 instanceof Bundle) {
               var2.putAll((Bundle)var7);
            }
            break;
         case "deviceinfo":
            Object var6 = RecoverType5a6aed9.recoverMethod_a_b00c10b9(this.recoverField_a, "deviceinfo", null);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(this.recoverField_b, "from jcore DEVICE_INFO bundle=" + var6);
            if (var6 instanceof JSONObject) {
               var2.putString("de_inf", var6.toString());
            }
      }

      return var2;
   }

   private Bundle recoverMethod_d_fa0ea5f9(String var1, Bundle var2) {
      switch (var1) {
         case "isTcpLoggedIn":
            if (var2 != null) {
               boolean var5 = var2.getBoolean("state", false);
               if (var5 && !this.recoverField_d) {
                  this.recoverField_d = true;
                  RecoverTypeaf2afc7f.recoverMethod_a_c69ef907().recoverMethod_b_9b75();
               }

               this.recoverField_c.put(var1, var5);
               RecoverType5a690d2.recoverMethod_a_abcb8d40(this.recoverField_a, RecoverType5a690d1.recoverMethod_z_48794abf().recoverMethod_a_4e9722a3(var5));
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(this.recoverField_b, "Updated connection state cache from callback: " + var5);
            }
            break;
         case "deviceinfo":
            this.recoverMethod_a_8817bcb3(var1, var2, RecoverType5a690d1.recoverMethod_aL_48794abf());
            break;
         case "get_loc_info":
            this.recoverMethod_a_8817bcb3(var1, var2, RecoverType5a690d1.recoverMethod_aM_48794abf());
      }

      return null;
   }

   private static final class Member8ee175a6 {
      private static final RecoverTypeaf2a9729 recoverField_a = new RecoverTypeaf2a9729();
   }
}
