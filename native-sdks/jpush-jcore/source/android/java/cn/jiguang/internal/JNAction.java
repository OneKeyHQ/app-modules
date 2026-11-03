package cn.jiguang.internal;

public class JNAction {
   public static boolean isNativeLibraryLoaded = false;

   public static void load() {
      try {
         System.loadLibrary("jutils");
         isNativeLibraryLoaded = true;
      } catch (Throwable var1) {
         isNativeLibraryLoaded = false;
      }
   }

   public static void loadPath(String var0) {
      try {
         System.load(var0);
         isNativeLibraryLoaded = true;
      } catch (Throwable var2) {
         isNativeLibraryLoaded = false;
      }
   }

   public static native void recoverMethod_s_b7d3a948(String var0, long var1, int var3, String var4);

   public static native byte[] recoverMethod_r_afa28ebe(String var0);

   public static native boolean recoverMethod_w_e925fa0d(String var0, String var1);

   public static native boolean recoverMethod_a_e925fa0d(String var0, String var1);

   public static native boolean recoverMethod_d_aad3b203(String var0);

   public static native void recoverMethod_i_44be02fe(String var0, long var1, int var3);

   public static native String recoverMethod_v_73cf11cb();
}
