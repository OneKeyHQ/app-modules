package cn.jpush.android.ups;

public class TokenResult {
   private String token;
   private int returnCode;
   private String actionType;

   public TokenResult(String var1, int var2) {
      this.token = var1;
      this.returnCode = var2;
   }

   public TokenResult(String var1, int var2, String var3) {
      this.token = var1;
      this.returnCode = var2;
      this.actionType = var3;
   }

   public String getToken() {
      return this.token;
   }

   public void setToken(String var1) {
      this.token = var1;
   }

   public int getReturnCode() {
      return this.returnCode;
   }

   public void setReturnCode(int var1) {
      this.returnCode = var1;
   }

   public String getActionType() {
      return this.actionType;
   }

   public void setActionType(String var1) {
      this.actionType = var1;
   }

   @Override
   public String toString() {
      return "TokenResult{token='" + this.token + '\'' + ", returnCode=" + this.returnCode + ", actionType='" + this.actionType + '\'' + '}';
   }
}
