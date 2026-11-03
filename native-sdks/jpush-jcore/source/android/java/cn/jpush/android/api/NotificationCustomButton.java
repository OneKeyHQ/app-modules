package cn.jpush.android.api;

public class NotificationCustomButton {
   public String recoverField_a;
   public int recoverField_b;
   public String recoverField_c;
   public int recoverField_d;
   public String recoverField_e;
   public String recoverField_f;

   public NotificationCustomButton() {
   }

   public NotificationCustomButton(String var1, int var2, String var3, int var4, String var5, String var6) {
      this.recoverField_a = var1;
      this.recoverField_b = var2;
      this.recoverField_c = var3;
      this.recoverField_d = var4;
      this.recoverField_e = var5;
      this.recoverField_f = var6;
   }

   @Override
   public String toString() {
      return "NotificationCustomButton{msgId='"
         + this.recoverField_a
         + '\''
         + ", platform="
         + this.recoverField_b
         + ", name='"
         + this.recoverField_c
         + '\''
         + ", actionType="
         + this.recoverField_d
         + ", action='"
         + this.recoverField_e
         + '\''
         + ", data="
         + this.recoverField_f
         + '}';
   }
}
