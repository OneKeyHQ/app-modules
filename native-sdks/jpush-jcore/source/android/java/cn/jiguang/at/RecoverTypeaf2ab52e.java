package cn.jiguang.at;

public class RecoverTypeaf2ab52e {
   int recoverField_a = 0;
   public long recoverField_b;
   public String recoverField_c;
   int recoverField_d;
   int recoverField_e;
   long recoverField_f;
   byte[] recoverField_g;
   long recoverField_h;
   long recoverField_i;
   boolean recoverField_j;

   public RecoverTypeaf2ab52e(long var1, String var3, int var4, int var5, long var6, long var8, byte[] var10) {
      this.recoverField_b = var1;
      this.recoverField_c = var3;
      this.recoverField_d = var4;
      this.recoverField_e = var5;
      this.recoverField_f = var6;
      this.recoverField_i = var8;
      this.recoverField_g = var10;
      if (var8 > 0L) {
         this.recoverField_j = true;
      }
   }

   public void recoverMethod_a_9b75() {
      this.recoverField_a++;
   }

   @Override
   public String toString() {
      return "InnerRequest{times="
         + this.recoverField_a
         + ", requestId="
         + this.recoverField_b
         + ", sdkType='"
         + this.recoverField_c
         + '\''
         + ", command="
         + this.recoverField_d
         + ", ver="
         + this.recoverField_e
         + ", rid="
         + this.recoverField_f
         + ", reqeustTime="
         + this.recoverField_h
         + ", timeout="
         + this.recoverField_i
         + '}';
   }
}
