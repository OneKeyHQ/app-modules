package cn.jiguang.m;

import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import android.text.TextUtils;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import cn.jiguang.g.RecoverType5a69853;
import cn.jiguang.l.RecoverType5a6ab19;
import java.util.Locale;

public class RecoverType5a6aedb {
   private String recoverField_a;
   private String recoverField_b;
   private String recoverField_c;
   private String recoverField_d;
   private static RecoverType5a6aedb recoverField_e = new RecoverType5a6aedb();

   private RecoverType5a6aedb() {
   }

   public static RecoverType5a6aedb recoverMethod_a_487cedf5() {
      return RecoverType5a6aedb.Member36b668d8.recoverField_a;
   }

   public String recoverMethod_a_e501eb0d(Context var1) {
      if (!TextUtils.isEmpty(this.recoverField_a)) {
         return this.recoverField_a;
      } else {
         this.recoverField_a = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var1, RecoverType5a690d1.recoverMethod_aH_48794abf());
         if (!TextUtils.isEmpty(this.recoverField_a)) {
            return this.recoverField_a;
         } else if (RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_d_134632(2009)) {
            this.recoverField_a = String.format(Locale.ENGLISH, Build.MODEL);
            RecoverType5a690d2.recoverMethod_a_abcb8d40(var1, RecoverType5a690d1.recoverMethod_aH_48794abf().recoverMethod_a_4e9722a3(this.recoverField_a));
            return this.recoverField_a;
         } else {
            return "";
         }
      }
   }

   public String recoverMethod_b_e501eb0d(Context var1) {
      if (!TextUtils.isEmpty(this.recoverField_c)) {
         return this.recoverField_c;
      } else {
         this.recoverField_c = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var1, RecoverType5a690d1.recoverMethod_aI_48794abf());
         if (!TextUtils.isEmpty(this.recoverField_c)) {
            return this.recoverField_c;
         } else if (RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_d_134632(2008)) {
            this.recoverField_c = String.format(Locale.ENGLISH, Build.MANUFACTURER);
            RecoverType5a690d2.recoverMethod_a_abcb8d40(var1, RecoverType5a690d1.recoverMethod_aI_48794abf().recoverMethod_a_4e9722a3(this.recoverField_c));
            return this.recoverField_c;
         } else {
            return "";
         }
      }
   }

   public String recoverMethod_c_e501eb0d(Context var1) {
      if (!TextUtils.isEmpty(this.recoverField_b)) {
         return this.recoverField_b;
      } else {
         this.recoverField_b = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var1, RecoverType5a690d1.recoverMethod_aK_48794abf());
         if (!TextUtils.isEmpty(this.recoverField_b)) {
            return this.recoverField_b;
         } else if (RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_d_134632(2011)) {
            this.recoverField_b = Integer.toString(VERSION.SDK_INT);
            RecoverType5a690d2.recoverMethod_a_abcb8d40(var1, RecoverType5a690d1.recoverMethod_aK_48794abf().recoverMethod_a_4e9722a3(this.recoverField_b));
            return this.recoverField_b;
         } else {
            return "";
         }
      }
   }

   public void recoverMethod_a_5ffc00fd(Context var1, String var2) {
      this.recoverField_b = var2;
      RecoverType5a690d2.recoverMethod_a_abcb8d40(var1, RecoverType5a690d1.recoverMethod_aK_48794abf().recoverMethod_a_4e9722a3(var2));
   }

   public String recoverMethod_d_e501eb0d(Context var1) {
      if (!TextUtils.isEmpty(this.recoverField_d)) {
         return this.recoverField_d;
      } else {
         this.recoverField_d = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var1, RecoverType5a690d1.recoverMethod_aJ_48794abf());
         if (!TextUtils.isEmpty(this.recoverField_d)) {
            return this.recoverField_d;
         } else if (RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_d_134632(1019)) {
            this.recoverField_d = RecoverType5a6ab19.recoverMethod_a_e501eb0d(var1);
            RecoverType5a690d2.recoverMethod_a_abcb8d40(var1, RecoverType5a690d1.recoverMethod_aJ_48794abf().recoverMethod_a_4e9722a3(this.recoverField_d));
            return this.recoverField_d;
         } else {
            return "";
         }
      }
   }

   private static final class Member36b668d8 {
      private static final RecoverType5a6aedb recoverField_a = new RecoverType5a6aedb();
   }
}
