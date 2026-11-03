package cn.jpush.android.api;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class JPushMessage implements Serializable {
   public int recoverField_a = 0;
   public int recoverField_b = 0;
   private static final long serialVersionUID = 1L;
   private String recoverField_c;
   private Set recoverField_d;
   private Map recoverField_e;
   private String recoverField_f;
   private int recoverField_g;
   private boolean recoverField_h;
   private boolean recoverField_i;
   private int recoverField_j;
   private String recoverField_k;

   public String getAlias() {
      return this.recoverField_c;
   }

   public void setAlias(String var1) {
      this.recoverField_c = var1;
   }

   public Set getTags() {
      return this.recoverField_d;
   }

   public void setTags(Set var1) {
      this.recoverField_d = var1;
   }

   public Map getPros() {
      return this.recoverField_e;
   }

   public void setPros(Map var1) {
      this.recoverField_e = var1;
   }

   public int getErrorCode() {
      return this.recoverField_g;
   }

   public void setErrorCode(int var1) {
      this.recoverField_g = var1;
   }

   public int getSequence() {
      return this.recoverField_j;
   }

   public void setSequence(int var1) {
      this.recoverField_j = var1;
   }

   public String getCheckTag() {
      return this.recoverField_f;
   }

   public void setCheckTag(String var1) {
      this.recoverField_f = var1;
   }

   public boolean getTagCheckStateResult() {
      return this.recoverField_h;
   }

   public void setTagCheckStateResult(boolean var1) {
      this.recoverField_h = var1;
   }

   public boolean isTagCheckOperator() {
      return this.recoverField_i;
   }

   public void setTagCheckOperator(boolean var1) {
      this.recoverField_i = var1;
   }

   public String getMobileNumber() {
      return this.recoverField_k;
   }

   public void setMobileNumber(String var1) {
      this.recoverField_k = var1;
   }

   public int getProtoType() {
      return this.recoverField_a;
   }

   public void setProtoType(int var1) {
      this.recoverField_a = var1;
   }

   public int getAction() {
      return this.recoverField_b;
   }

   public void setAction(int var1) {
      this.recoverField_b = var1;
   }

   @Override
   public String toString() {
      return "JPushMessage{alias='"
         + this.recoverField_c
         + '\''
         + ", tags="
         + this.recoverField_d
         + ", pros="
         + this.recoverField_e
         + ", checkTag='"
         + this.recoverField_f
         + '\''
         + ", errorCode="
         + this.recoverField_g
         + ", tagCheckStateResult="
         + this.recoverField_h
         + ", isTagCheckOperator="
         + this.recoverField_i
         + ", sequence="
         + this.recoverField_j
         + ", mobileNumber="
         + this.recoverField_k
         + '}';
   }
}
