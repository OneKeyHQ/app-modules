package cn.jiguang.av;

import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.api.JCoreManager;
import cn.jiguang.aq.RecoverTypeaf2aa9e8;
import java.nio.ByteBuffer;

public class RecoverTypeaf2abcb0 {
   private final RecoverTypeaf2abcaf recoverField_e;
   private ByteBuffer recoverField_f;
   public int recoverField_a;
   public int recoverField_b;
   private int recoverField_g;
   private String recoverField_h;
   public int recoverField_c;
   public int recoverField_d;
   private String recoverField_i;

   public RecoverTypeaf2abcb0(RecoverTypeaf2abcaf var1, ByteBuffer var2) {
      this.recoverField_e = var1;
      if (null != var2) {
         this.recoverField_f = var2;
         this.recoverMethod_a_9b75();
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("LoginResponse", "No body to parse.");
      }
   }

   private void recoverMethod_a_9b75() {
      try {
         this.recoverField_a = this.recoverField_f.getShort();
      } catch (Throwable var6) {
         this.recoverField_a = 10000;
      }

      if (this.recoverField_a > 0) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("LoginResponse", "Response error - code:" + this.recoverField_a);
      }

      ByteBuffer var1 = this.recoverField_f;
      this.recoverField_d = -1;
      if (this.recoverField_a == 0) {
         try {
            this.recoverField_b = var1.getInt();
            this.recoverField_g = var1.getShort();
            this.recoverField_h = RecoverTypeaf2abcae.recoverMethod_a_51ac9724(var1);
            this.recoverField_c = var1.getInt();
         } catch (Throwable var5) {
            this.recoverField_a = 10000;
         }

         try {
            this.recoverField_d = var1.get();
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("LoginResponse", "idc parse success, value:" + this.recoverField_d);
         } catch (Throwable var4) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("LoginResponse", "parse idc failed, error:" + var4);
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
      return "[LoginResponse] - code:"
         + this.recoverField_a
         + ",sid:"
         + this.recoverField_b
         + ", serverVersion:"
         + this.recoverField_g
         + ", sessionKey:"
         + this.recoverField_h
         + ", serverTime:"
         + this.recoverField_c
         + ", idc:"
         + this.recoverField_d
         + ", connectInfo:"
         + this.recoverField_i;
   }
}
