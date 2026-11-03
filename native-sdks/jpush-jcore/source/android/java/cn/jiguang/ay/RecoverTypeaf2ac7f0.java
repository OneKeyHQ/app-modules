package cn.jiguang.ay;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Build.VERSION;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07cf;
import cn.jiguang.bk.RecoverTypeaf2b07d5;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.g.RecoverType5a69853;
import java.util.concurrent.atomic.AtomicBoolean;

public class RecoverTypeaf2ac7f0 {
   public String recoverField_a;
   public String recoverField_b;
   public String recoverField_c;
   public String recoverField_d;
   public String recoverField_e;
   public String recoverField_f;
   public int recoverField_g;
   public String recoverField_h;
   public String recoverField_i;
   public String recoverField_j;
   public String recoverField_k;
   public String recoverField_l;
   public String recoverField_m;
   public String recoverField_n;
   public String recoverField_o;
   public String recoverField_p;
   public String recoverField_q;
   public String recoverField_r;
   private transient AtomicBoolean recoverField_s = new AtomicBoolean(false);
   private static volatile RecoverTypeaf2ac7f0 recoverField_t;
   private static final Object recoverField_u = new Object();
   private static String recoverField_v;

   private RecoverTypeaf2ac7f0(Context var1) {
      this.recoverMethod_c_f0554df3(var1);
   }

   public static RecoverTypeaf2ac7f0 recoverMethod_a_5ee49234(Context var0) {
      if (recoverField_t == null) {
         synchronized (recoverField_u) {
            if (recoverField_t == null) {
               recoverField_t = new RecoverTypeaf2ac7f0(var0);
            }
         }
      }

      return recoverField_t;
   }

   public static void recoverMethod_b_f0554df3(Context var0) {
      recoverField_t = null;
      recoverMethod_a_5ee49234(var0);
   }

   private void recoverMethod_c_f0554df3(Context var1) {
      if (!this.recoverField_s.get() && var1 != null) {
         this.recoverField_b = this.recoverMethod_a_e96ea081(RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_z_e501eb0d(var1))
            + ","
            + VERSION.SDK_INT;
         this.recoverField_c = RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_u_e501eb0d(var1);
         if (RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_d_134632(2001)) {
            this.recoverField_d = RecoverTypeaf2b07d5.recoverMethod_a_8ea82db7("gsm.version.baseband", "baseband");
         }

         this.recoverField_l = RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_x_e501eb0d(var1);
         this.recoverField_n = RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_v_e501eb0d(var1);
         this.recoverField_h = RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_k_e501eb0d(var1);
         this.recoverField_i = RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_n_e501eb0d(var1);
         this.recoverField_j = " ";
         this.recoverField_e = this.recoverMethod_a_e96ea081(Build.DEVICE);
         this.recoverField_k = this.recoverMethod_a_e96ea081(RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_p_e501eb0d(var1));
         this.recoverField_m = this.recoverMethod_a_e96ea081(RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_r_e501eb0d(var1));
         this.recoverField_a = recoverMethod_d_e501eb0d(var1);
         this.recoverField_f = RecoverType5a68d10.recoverMethod_h_e501eb0d(var1);
         this.recoverField_g = RecoverTypeaf2b07c1.recoverMethod_d_f0554df7(var1) ? 1 : 0;
         this.recoverField_o = RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_d_a4ea6d43(var1, "");
         this.recoverField_p = RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_c_a4ea6d43(var1, "");
         this.recoverField_q = VERSION.SDK_INT + "";
         this.recoverField_r = var1.getApplicationInfo().targetSdkVersion + "";
         this.recoverField_s.set(true);
      }
   }

   private String recoverMethod_a_e96ea081(String var1) {
      return var1 != null ? var1.trim() : null;
   }

   private static String recoverMethod_d_e501eb0d(Context var0) {
      if (recoverField_v == null) {
         try {
            PackageInfo var1 = RecoverTypeaf2b07cf.recoverMethod_a_9537863d(var0, 0);
            if (var1 != null) {
               String var2 = var1.versionName;
               byte[] var3 = var2.getBytes("UTF-8");
               if (var3.length > 30) {
                  var2 = new String(var3, 0, 30);
               }

               recoverField_v = var2;
            } else {
               RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("DeviceInfo", "NO versionName defined in manifest.");
            }
         } catch (Throwable var4) {
            RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("DeviceInfo", "NO versionName defined in manifest.");
         }
      }

      return recoverField_v == null ? "" : recoverField_v;
   }
}
