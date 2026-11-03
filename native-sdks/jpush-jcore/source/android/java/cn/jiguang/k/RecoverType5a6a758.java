package cn.jiguang.k;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.text.TextUtils;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.al.RecoverTypeaf2a9723;
import cn.jiguang.al.RecoverTypeaf2a9729;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ao.RecoverTypeaf2aa26b;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07cf;
import cn.jiguang.bk.RecoverTypeaf2b07d5;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.g.RecoverType5a69853;
import cn.jiguang.i.RecoverType5a69fd5;
import cn.jiguang.i.RecoverType5a69fd6;
import cn.jiguang.l.RecoverType5a6ab18;
import cn.jiguang.l.RecoverType5a6ab19;
import cn.jiguang.o.RecoverType5a6b65b;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

public class RecoverType5a6a758 extends RecoverType5a69fd5 {
   private String recoverField_a;
   private Context recoverField_b;
   private String recoverField_c;

   private RecoverType5a6a758() {
   }

   public static RecoverType5a6a758 recoverMethod_a_487c0518() {
      return RecoverType5a6a758.Member369a3616.recoverField_a;
   }

   @Override
   protected String recoverMethod_a_e501eb0d(Context var1) {
      this.recoverField_b = var1;
      return "JDevice";
   }

   @Override
   protected void recoverMethod_c_5ffc00fd(Context var1, String var2) {
      if (!RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_a_134632(1000)) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDevice", "will not report");
      } else {
         String var3 = this.recoverMethod_b_e501eb0d(var1);
         this.recoverField_a = var3;
         if (var3 != null) {
            Bundle var4 = new Bundle();
            var4.putString("de_inf", var3);
            RecoverTypeaf2a9729.recoverMethod_a_c692b39d().recoverMethod_a_5dc9c75("get_loc_info", var4);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDevice", "collect success:" + var3);
            super.recoverMethod_c_5ffc00fd(var1, var2);
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDevice", "collect failed");
         }
      }
   }

   @Override
   protected boolean recoverMethod_b_5ffc0101(Context var1, String var2) {
      boolean var3 = RecoverType5a69fd6.recoverMethod_b_5ffc0101(var1, var2);
      if (!var3) {
         return false;
      } else if (this.recoverField_a == null) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDevice", "there are no data to report");
         return false;
      } else {
         String var4 = this.recoverField_a;
         if (TextUtils.isEmpty(var4)) {
            return false;
         } else {
            var4 = var4 + recoverMethod_g_e501eb0d(var1);
            this.recoverField_c = RecoverTypeaf2b07d5.recoverMethod_j_e96ea081(var4);
            String var5 = RecoverType5a69fd6.recoverMethod_h_e501eb0d(var1);
            if (!TextUtils.isEmpty(this.recoverField_c) && !TextUtils.equals(this.recoverField_c, var5)) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDevice", "device detail is change");
               return super.recoverMethod_b_5ffc0101(var1, var2);
            } else {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDevice", "device detail is not change");
               return false;
            }
         }
      }
   }

   @Override
   protected void recoverMethod_d_5ffc00fd(Context var1, String var2) {
      String var3 = this.recoverField_a;
      if (var3 == null) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDevice", "there are no data to report");
      } else {
         try {
            JSONObject var4 = new JSONObject(var3);
            RecoverType5a68d10.recoverMethod_a_1b76323(var1, var4, "device_info");
            RecoverTypeaf2aa26b.recoverMethod_a_6892eae2(var1, var4, new RecoverType5a6a757(var1, this.recoverField_c, var2));
         } catch (Throwable var5) {
         }

         this.recoverField_a = null;
      }
   }

   @Override
   public Object recoverMethod_f_f23f0d9f(Context var1) {
      String var2 = this.recoverMethod_b_e501eb0d(var1);
      if (null != var2) {
         try {
            return new JSONObject(var2);
         } catch (Throwable var4) {
            return null;
         }
      } else {
         return null;
      }
   }

   private String recoverMethod_b_e501eb0d(Context var1) {
      if (var1 == null) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDevice", "when getDeviceInfo, context can't be null");
         return null;
      }

      try {
         JSONObject var2 = new JSONObject();
         String var3 = RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_k_e501eb0d(var1);
         var2.put("resolution", TextUtils.isEmpty(var3) ? "" : var3);
         var2.put("screensize", RecoverType5a6ab18.recoverMethod_a_e501eb0d(var1));
         var2.put("os_version", RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_z_e501eb0d(var1));
         var2.put("model", RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_u_e501eb0d(var1));
         var2.put("brand", RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_v_e501eb0d(var1));
         var2.put("product", RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_p_e501eb0d(var1));
         var2.put("font", RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_q_e501eb0d(var1));
         var3 = String.format(Locale.ENGLISH, Build.FINGERPRINT);
         var2.put("fingerprint", RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_r_e501eb0d(var1));
         var2.put("language", RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_w_e501eb0d(var1));
         var2.put("manufacturer", RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_x_e501eb0d(var1));
         var2.put("timezone", RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_y_e501eb0d(var1));
         var3 = RecoverType5a6ab19.recoverMethod_a_e501eb0d(var1);
         var2.put("romversion", TextUtils.isEmpty(var3) ? "" : var3);
         JSONObject var8 = RecoverType5a6b65b.recoverMethod_a_16ce4761(var1);
         if (var8 != null) {
            var2.put("ids", var8);
         }

         JSONObject var9 = RecoverType5a6ab18.recoverMethod_e_16ce4761(var1);
         if (var1 != null) {
            var2.put("ncid_box", var9);
         }

         var2.put("android_ver", VERSION.SDK_INT);
         var2.put("android_target_ver", var1.getApplicationInfo().targetSdkVersion);
         return var2.toString();
      } catch (JSONException var4) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDevice", "package json exception: " + var4.getMessage());
      } catch (Throwable var5) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDevice", "getDeviceInfo exception: " + var5.getMessage());
      }

      return null;
   }

   private static String recoverMethod_g_e501eb0d(Context var0) {
      try {
         String var1 = RecoverTypeaf2a9723.recoverMethod_c_e501eb0d(var0);
         String var2 = RecoverTypeaf2a9723.recoverMethod_a_e501eb0d(var0);
         String var3 = "";
         PackageInfo var4 = RecoverTypeaf2b07cf.recoverMethod_a_9537863d(var0, 0);
         String var5 = var4 == null ? "" : var4.versionName;
         String var6 = var4 == null ? "" : String.valueOf(var4.versionCode);
         String var7 = RecoverType5a681cd.recoverField_b;
         String var8 = String.valueOf(RecoverType5a681cd.recoverField_c);
         StringBuilder var9 = new StringBuilder();
         var9.append(var1);
         var9.append(",");
         var9.append(TextUtils.isEmpty(var2) ? "" : var2);
         var9.append(",");
         var9.append(TextUtils.isEmpty(var5) ? "" : var5);
         var9.append(",");
         var9.append(TextUtils.isEmpty(var6) ? "" : var6);
         var9.append(",");
         var9.append(TextUtils.isEmpty(var7) ? "" : var7);
         var9.append(",");
         var9.append(TextUtils.isEmpty(var8) ? "" : var8);
         var9.append(",");
         var9.append(TextUtils.isEmpty(var3) ? "" : var3);
         return var9.toString();
      } catch (Throwable var10) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDevice", "getCurrentCondition throwable: " + var10.getMessage());
         return null;
      }
   }

   public void recoverMethod_b_1a54e370(Context var1, int var2) {
      RecoverTypeaf2aa628.recoverMethod_a_fa8b67f0(new RecoverType5a6a758.Member369a3615(var1), var2);
   }

   public static class Member369a3615 extends RecoverTypeaf2b0401 {
      private Context recoverField_a;

      private Member369a3615(Context var1) {
         this.recoverField_a = var1;
         this.recoverField_h = "JDevice#RegisterAction";
      }

      @Override
      public void recoverMethod_a_9b75() {
         try {
            RecoverType5a6ab18.recoverMethod_a_5ffc00fd(this.recoverField_a, RecoverType5a6ab18.recoverMethod_c_e501eb0d(this.recoverField_a));
         } catch (Throwable var2) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDevice", "RegisterAction failed:" + var2.getMessage());
         }
      }
   }

   private static final class Member369a3616 {
      private static final RecoverType5a6a758 recoverField_a = new RecoverType5a6a758();
   }
}
