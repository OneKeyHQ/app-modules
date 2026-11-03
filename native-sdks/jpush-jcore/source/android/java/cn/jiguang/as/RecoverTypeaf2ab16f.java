package cn.jiguang.as;

import java.util.HashMap;

class RecoverTypeaf2ab16f {
   private static Integer[] recoverField_a = new Integer[64];
   private HashMap recoverField_b;
   private HashMap recoverField_c;
   private String recoverField_d;
   private int recoverField_e;
   private String recoverField_f;
   private int recoverField_g;
   private boolean recoverField_h;

   public RecoverTypeaf2ab16f(String var1, int var2) {
      this.recoverField_d = var1;
      this.recoverField_e = var2;
      this.recoverField_b = new HashMap();
      this.recoverField_c = new HashMap();
      this.recoverField_g = Integer.MAX_VALUE;
   }

   public void recoverMethod_a_13462e(int var1) {
      this.recoverField_g = var1;
   }

   public void recoverMethod_a_1385ff(boolean var1) {
      this.recoverField_h = var1;
   }

   public static Integer recoverMethod_b_61acf5d5(int var0) {
      return var0 >= 0 && var0 < recoverField_a.length ? recoverField_a[var0] : var0;
   }

   public void recoverMethod_c_13462e(int var1) {
      if (var1 < 0 || var1 > this.recoverField_g) {
         throw new IllegalArgumentException(this.recoverField_d + " " + var1 + "is out of range");
      }
   }

   private String recoverMethod_a_e96ea081(String var1) {
      if (this.recoverField_e == 2) {
         return var1.toUpperCase();
      } else {
         return this.recoverField_e == 3 ? var1.toLowerCase() : var1;
      }
   }

   public void recoverMethod_a_4f708078(int var1, String var2) {
      this.recoverMethod_c_13462e(var1);
      Integer var3 = recoverMethod_b_61acf5d5(var1);
      var2 = this.recoverMethod_a_e96ea081(var2);
      this.recoverField_b.put(var2, var3);
      this.recoverField_c.put(var3, var2);
   }

   public String recoverMethod_d_47921032(int var1) {
      this.recoverMethod_c_13462e(var1);
      String var2 = (String)this.recoverField_c.get(recoverMethod_b_61acf5d5(var1));
      if (var2 != null) {
         return var2;
      }

      var2 = Integer.toString(var1);
      return this.recoverField_f != null ? this.recoverField_f + var2 : var2;
   }

   static {
      for (int var0 = 0; var0 < recoverField_a.length; var0++) {
         recoverField_a[var0] = var0;
      }
   }
}
