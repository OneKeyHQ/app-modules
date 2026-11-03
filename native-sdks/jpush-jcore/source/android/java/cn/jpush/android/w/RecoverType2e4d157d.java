package cn.jpush.android.w;

import java.util.HashMap;
import java.util.Map;

public class RecoverType2e4d157d {
   private String recoverField_a;
   private String recoverField_b;
   private Map recoverField_c;
   private int recoverField_d;
   private boolean recoverField_e;
   private boolean recoverField_f;
   private int recoverField_g = -1;
   private int recoverField_h = -1;

   public RecoverType2e4d157d(String var1) {
      this.recoverField_a = var1;
      this.recoverField_d = 0;
      this.recoverField_e = false;
      this.recoverField_f = false;
      this.recoverField_c = new HashMap();
   }

   public RecoverType2e4d157d() {
      this.recoverField_c = new HashMap();
   }

   public String recoverMethod_a_73cf11cb() {
      return this.recoverField_b;
   }

   public void recoverMethod_a_aad3b1ff(String var1) {
      this.recoverField_b = var1;
   }

   public int recoverMethod_b_9b68() {
      return this.recoverField_g;
   }

   public void recoverMethod_a_13462e(int var1) {
      this.recoverField_g = var1;
   }

   public void recoverMethod_b_13462e(int var1) {
      this.recoverField_h = var1;
   }

   public void recoverMethod_a_e925fa09(String var1, String var2) {
      if (this.recoverField_c != null) {
         this.recoverField_c.put(var1, var2);
      }
   }

   @Override
   public String toString() {
      return "HttpResponse{responseBody='" + this.recoverField_b + '\'' + ", responseCode=" + this.recoverField_g + '}';
   }
}
