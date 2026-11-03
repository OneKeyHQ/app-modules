package cn.jpush.android.data;

import java.io.Serializable;

public class JPushConfig implements Serializable {
   private static final long serialVersionUID = -3135447319267244288L;
   private String recoverField_a;
   private String recoverField_b;
   private String recoverField_c;
   private String recoverField_d;
   private String recoverField_e;
   private String recoverField_f;
   private String recoverField_g;
   private String recoverField_h;

   public String getOppoAppKey() {
      return this.recoverField_b;
   }

   public void setOppoAppKey(String var1) {
      this.recoverField_b = var1;
   }

   public String getOppoAppSecret() {
      return this.recoverField_d;
   }

   public void setOppoAppSecret(String var1) {
      this.recoverField_d = var1;
   }

   public String getMzAppId() {
      return this.recoverField_e;
   }

   public void setMzAppId(String var1) {
      this.recoverField_e = var1;
   }

   public String getMzAppKey() {
      return this.recoverField_f;
   }

   public void setMzAppKey(String var1) {
      this.recoverField_f = var1;
   }

   public String getXmAppId() {
      return this.recoverField_g;
   }

   public void setXmAppId(String var1) {
      this.recoverField_g = var1;
   }

   public String getXmAppKey() {
      return this.recoverField_h;
   }

   public void setXmAppKey(String var1) {
      this.recoverField_h = var1;
   }

   public String getOppoAppId() {
      return this.recoverField_c;
   }

   public void setOppoAppId(String var1) {
      this.recoverField_c = var1;
   }

   public String getjAppKey() {
      return this.recoverField_a;
   }

   public void setjAppKey(String var1) {
      this.recoverField_a = var1;
   }
}
