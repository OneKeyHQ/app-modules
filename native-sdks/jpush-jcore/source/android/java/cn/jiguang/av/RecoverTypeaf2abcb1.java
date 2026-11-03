package cn.jiguang.av;

import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.api.JCoreManager;
import cn.jiguang.aq.RecoverTypeaf2aa9e8;
import java.nio.ByteBuffer;

public class RecoverTypeaf2abcb1 {
   private final RecoverTypeaf2abcaf recoverField_f;
   private ByteBuffer recoverField_g;
   public int recoverField_a;
   public long recoverField_b;
   public String recoverField_c;
   public String recoverField_d;
   public String recoverField_e;
   private String recoverField_h;
   private String recoverField_i;

   public RecoverTypeaf2abcb1(RecoverTypeaf2abcaf var1, ByteBuffer var2) {
      this.recoverField_f = var1;
      if (null != var2) {
         this.recoverField_g = var2;
         this.recoverMethod_a_9b75();
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("RegisterResponse", "No body to parse.");
      }
   }

   private void recoverMethod_a_9b75() {
      try {
         this.recoverField_a = this.recoverField_g.getShort();
      } catch (Throwable var6) {
         this.recoverField_a = 10000;
      }

      if (this.recoverField_a > 0) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("RegisterResponse", "Response error - code:" + this.recoverField_a);
      }

      ByteBuffer var1 = this.recoverField_g;
      if (this.recoverField_a == 0) {
         try {
            this.recoverField_b = var1.getLong();
            this.recoverField_c = RecoverTypeaf2abcae.recoverMethod_a_51ac9724(var1);
            this.recoverField_d = RecoverTypeaf2abcae.recoverMethod_a_51ac9724(var1);
         } catch (Throwable var5) {
            this.recoverField_a = 10000;
         }
      } else if (this.recoverField_a == 1007) {
         try {
            this.recoverField_h = RecoverTypeaf2abcae.recoverMethod_a_51ac9724(var1);
         } catch (Throwable var4) {
            this.recoverField_a = 10000;
         }
      } else if (this.recoverField_a == 1012) {
         try {
            this.recoverField_i = RecoverTypeaf2abcae.recoverMethod_a_51ac9724(var1);
         } catch (Throwable var3) {
            this.recoverField_a = 10000;
         }

         RecoverTypeaf2aa9e8.recoverMethod_a_5ffc00fd(JCoreManager.getAppContext(null), this.recoverField_i);
      }
   }

   @Override
   public String toString() {
      return "[RegisterResponse] - code:"
         + this.recoverField_a
         + ", juid:"
         + this.recoverField_b
         + ", password:"
         + this.recoverField_c
         + ", regId:"
         + this.recoverField_d
         + ", deviceId:"
         + this.recoverField_e
         + ", connectInfo:"
         + this.recoverField_i;
   }
}
