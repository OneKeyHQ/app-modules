package cn.jiguang.ai;

import android.content.Intent;

public class RecoverTypeaf2a8be2 {
   public String recoverField_a;
   public String recoverField_b;
   public int recoverField_c;
   public String recoverField_d;
   public Intent recoverField_e;
   public Intent recoverField_f;
   public int recoverField_g = 0;
   public String recoverField_h;
   public String recoverField_i;
   public int recoverField_j;
   public int recoverField_k = 0;
   public String recoverField_l;

   public RecoverTypeaf2a8be2() {
   }

   public RecoverTypeaf2a8be2(String var1, String var2, int var3) {
      this.recoverField_a = var1;
      this.recoverField_b = var2;
      this.recoverField_c = var3;
   }

   @Override
   public boolean equals(Object var1) {
      if (this == var1) {
         return true;
      } else if (var1 != null && this.getClass() == var1.getClass()) {
         RecoverTypeaf2a8be2 var2 = (RecoverTypeaf2a8be2)var1;
         return this.recoverField_a != null ? this.recoverField_a.equals(var2.recoverField_a) : var2.recoverField_a == null;
      } else {
         return false;
      }
   }

   @Override
   public String toString() {
      return "JWakeTargetInfo{packageName='"
         + this.recoverField_a
         + '\''
         + ", serviceName='"
         + this.recoverField_b
         + '\''
         + ", targetVersion="
         + this.recoverField_c
         + ", providerAuthority='"
         + this.recoverField_d
         + '\''
         + ", activityIntent="
         + this.recoverField_e
         + ", activityIntentBackup="
         + this.recoverField_f
         + ", wakeType="
         + this.recoverField_g
         + ", authenType="
         + this.recoverField_h
         + ", instrumentationName="
         + this.recoverField_i
         + ", cmd="
         + this.recoverField_j
         + ", delaySecTime="
         + this.recoverField_k
         + ", uExtra="
         + this.recoverField_l
         + '}';
   }
}
