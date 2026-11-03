package cn.jpush.android.ad;

import android.app.Activity;
import java.lang.ref.WeakReference;

public class RecoverType9b4c5ae4 {
   private static WeakReference recoverField_a;

   public static void recoverMethod_a_f9c5dd07(Activity var0) {
   }

   public static void recoverMethod_b_f9c5dd07(Activity var0) {
      if (var0 != null) {
         recoverField_a = new WeakReference(var0);
      }
   }

   public static void recoverMethod_c_f9c5dd07(Activity var0) {
      if (var0 != null) {
         if (recoverField_a != null && recoverField_a.get() == var0) {
            recoverField_a.clear();
         }
      }
   }

   public static void recoverMethod_d_f9c5dd07(Activity var0) {
   }

   public static void recoverMethod_e_f9c5dd07(Activity var0) {
   }

   public static Activity recoverMethod_a_ad5c6a53() {
      return recoverField_a != null ? (Activity)recoverField_a.get() : null;
   }
}
