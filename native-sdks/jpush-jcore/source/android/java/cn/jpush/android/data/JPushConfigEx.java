package cn.jpush.android.data;

public class JPushConfigEx extends JPushConfig {
   private String recoverField_a;
   private String recoverField_b;
   private String recoverField_c;
   private String recoverField_d;

   public JPushConfigEx() {
   }

   public JPushConfigEx(JPushConfig var1) {
      this.setjAppKey(var1.getjAppKey());
      this.setMzAppId(var1.getMzAppId());
      this.setMzAppKey(var1.getMzAppKey());
      this.setOppoAppId(var1.getOppoAppId());
      this.setOppoAppKey(var1.getOppoAppKey());
      this.setOppoAppSecret(var1.getOppoAppSecret());
      this.setXmAppId(var1.getXmAppId());
      this.setXmAppKey(var1.getXmAppKey());
   }

   public String getHwAppId() {
      return this.recoverField_a;
   }

   public void setHwAppId(String var1) {
      this.recoverField_a = var1;
   }

   public String getHonorAppId() {
      return this.recoverField_b;
   }

   public void setHonorAppId(String var1) {
      this.recoverField_b = var1;
   }

   public String getVivoAppKey() {
      return this.recoverField_c;
   }

   public void setVivoAppKey(String var1) {
      this.recoverField_c = var1;
   }

   public String getVivoAppId() {
      return this.recoverField_d;
   }

   public void setVivoAppId(String var1) {
      this.recoverField_d = var1;
   }
}
