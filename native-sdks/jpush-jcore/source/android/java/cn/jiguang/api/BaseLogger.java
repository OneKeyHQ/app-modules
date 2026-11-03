package cn.jiguang.api;

public abstract class BaseLogger {
   public abstract String getCommonTag();

   private void log(int var1, boolean var2, String var3, String var4, Throwable var5) {
      JCoreManager.onEvent(null, this.getCommonTag(), 18, var3, null, var1, var2, var4, var5);
   }

   public void recoverMethod_v_e925fa09(String var1, String var2) {
      this.log(2, true, var1, var2, null);
   }

   public void recoverMethod_vv_e925fa09(String var1, String var2) {
      this.log(2, false, var1, var2, null);
   }

   public void recoverMethod_d_e925fa09(String var1, String var2) {
      this.log(3, true, var1, var2, null);
   }

   public void recoverMethod_dd_e925fa09(String var1, String var2) {
      this.log(3, false, var1, var2, null);
   }

   public void recoverMethod_i_e925fa09(String var1, String var2) {
      this.log(4, true, var1, var2, null);
   }

   public void recoverMethod_ii_e925fa09(String var1, String var2) {
      this.log(4, false, var1, var2, null);
   }

   public void recoverMethod_w_e925fa09(String var1, String var2) {
      this.log(5, true, var1, var2, null);
   }

   public void recoverMethod_ww_e925fa09(String var1, String var2) {
      this.log(5, false, var1, var2, null);
   }

   public void recoverMethod_e_e925fa09(String var1, String var2) {
      this.log(6, true, var1, var2, null);
   }

   public void recoverMethod_ee_e925fa09(String var1, String var2) {
      this.log(6, false, var1, var2, null);
   }

   public void recoverMethod_v_1765c98c(String var1, String var2, Throwable var3) {
      this.log(2, true, var1, var2, var3);
   }

   public void recoverMethod_vv_1765c98c(String var1, String var2, Throwable var3) {
      this.log(2, false, var1, var2, var3);
   }

   public void recoverMethod_d_1765c98c(String var1, String var2, Throwable var3) {
      this.log(3, true, var1, var2, var3);
   }

   public void recoverMethod_dd_1765c98c(String var1, String var2, Throwable var3) {
      this.log(3, false, var1, var2, var3);
   }

   public void recoverMethod_i_1765c98c(String var1, String var2, Throwable var3) {
      this.log(4, true, var1, var2, var3);
   }

   public void recoverMethod_ii_1765c98c(String var1, String var2, Throwable var3) {
      this.log(4, false, var1, var2, var3);
   }

   public void recoverMethod_w_1765c98c(String var1, String var2, Throwable var3) {
      this.log(5, true, var1, var2, var3);
   }

   public void recoverMethod_ww_1765c98c(String var1, String var2, Throwable var3) {
      this.log(5, false, var1, var2, var3);
   }

   public void recoverMethod_e_1765c98c(String var1, String var2, Throwable var3) {
      this.log(6, true, var1, var2, var3);
   }

   public void recoverMethod_ee_1765c98c(String var1, String var2, Throwable var3) {
      this.log(6, false, var1, var2, var3);
   }

   public void recoverMethod__d_adb25a88(String var1, String var2, Object... var3) {
   }

   public static void flushCached2File() {
   }
}
