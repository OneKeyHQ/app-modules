package cn.jiguang.av;

import java.nio.ByteBuffer;

public class RecoverTypeaf2abcaf {
   public int recoverField_a;
   public int recoverField_b;
   public int recoverField_c;
   public byte recoverField_d;
   public long recoverField_e;
   public int recoverField_f;
   public long recoverField_g;
   private boolean recoverField_h = false;

   public RecoverTypeaf2abcaf(boolean var1, byte[] var2) {
      try {
         this.recoverField_h = var1;
         ByteBuffer var3 = ByteBuffer.wrap(var2);
         this.recoverField_a = var3.getShort();
         this.recoverField_a &= 32767;
         this.recoverField_b = var3.get();
         this.recoverField_c = var3.get();
         this.recoverField_d = var3.get();
         var3.get();
         var3.getInt();
         this.recoverField_e = var3.getShort();
         if (var1) {
            this.recoverField_f = var3.getInt();
         }

         this.recoverField_g = var3.getLong();
      } catch (Throwable var4) {
      }
   }

   @Override
   public String toString() {
      return "[JHead] - len:"
         + this.recoverField_a
         + ", version:"
         + this.recoverField_b
         + ", command:"
         + this.recoverField_c
         + ", rid:"
         + this.recoverField_e
         + (this.recoverField_h ? ", sid:" + this.recoverField_f : "")
         + ", juid:"
         + this.recoverField_g;
   }
}
