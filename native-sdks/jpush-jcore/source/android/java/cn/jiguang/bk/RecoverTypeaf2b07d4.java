package cn.jiguang.bk;

import android.util.Base64;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import java.lang.reflect.Constructor;
import java.security.KeyFactory;
import java.security.SecureRandom;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class RecoverTypeaf2b07d4 {
   public static final String recoverField_a = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{91, 85, 5});
   public static final String recoverField_b = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{99, 104, 88, 37, 39});
   public static final String recoverField_c = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{74, 64, 87, 60, 2, 21, 47});
   private static final String recoverField_d = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{87, 84, 99});
   private static final String recoverField_e = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{73, 83, 66, 31, 99, 91, 75, 57, 65, 123, 99, 74, 61, 70, 112, 84, 68, 112, 102, 113}
   );

   public static synchronized byte[] recoverMethod_a_f31cd299(byte[] var0, String var1, String var2, boolean var3) throws java.io.UnsupportedEncodingException, java.security.InvalidKeyException, java.security.NoSuchAlgorithmException, javax.crypto.IllegalBlockSizeException , java.security.InvalidAlgorithmParameterException, javax.crypto.BadPaddingException, javax.crypto.NoSuchPaddingException {
      if (var1 == null) {
         return null;
      }

      byte[] var4 = recoverMethod_c_3b9947f4(var1, "UTF-8");
      SecretKeySpec var5 = new SecretKeySpec(var4, recoverField_d);
      IvParameterSpec var6 = recoverMethod_a_f5fbd0b5(var2.getBytes("UTF-8"));
      Cipher var7 = Cipher.getInstance(recoverField_e);
      var7.init(var3 ? 1 : 2, var5, var6);
      return var7.doFinal(var0);
   }

   public static int recoverMethod_a_9b68() {
      return Math.abs(new SecureRandom().nextInt()) & 16777215;
   }

   public static byte[] recoverMethod_a_78a45faa(String var0, String var1, String var2) throws java.io.UnsupportedEncodingException , java.security.InvalidKeyException , java.security.NoSuchAlgorithmException , javax.crypto.IllegalBlockSizeException , java.security.InvalidAlgorithmParameterException , javax.crypto.BadPaddingException , javax.crypto.NoSuchPaddingException {
      byte[] var3 = Base64.decode(var0, 2);
      return recoverMethod_a_f31cd299(var3, var1, var2, false);
   }

   public static String recoverMethod_a_ed76890(byte[] var0, String var1, String var2) throws java.io.UnsupportedEncodingException , java.security.InvalidKeyException , java.security.NoSuchAlgorithmException , javax.crypto.IllegalBlockSizeException , java.security.InvalidAlgorithmParameterException , javax.crypto.BadPaddingException , javax.crypto.NoSuchPaddingException {
      return Base64.encodeToString(recoverMethod_a_f31cd299(var0, var1, var2, true), 2);
   }

   public static String recoverMethod_a_8ea82db7(String var0, String var1) {
      try {
         return recoverMethod_a_ed76890(var0.getBytes(), var1, var1);
      } catch (Exception var3) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("", "Unexpected - failed to AES encrypt.");
         return "";
      }
   }

   public static String recoverMethod_b_8ea82db7(String var0, String var1) {
      try {
         return new String(recoverMethod_a_78a45faa(var0, var1, var1));
      } catch (Exception var3) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("", "Unexpected - failed to AES decrypt.");
         return "";
      }
   }

   public static String recoverMethod_b_f169196d(String var0, String var1, String var2) throws java.io.UnsupportedEncodingException , java.security.InvalidKeyException , java.security.NoSuchAlgorithmException , javax.crypto.IllegalBlockSizeException , javax.crypto.BadPaddingException , javax.crypto.NoSuchPaddingException {
      byte[] var3 = recoverMethod_a_afdcdd3a(var0, recoverMethod_a_d1778c87(var1), var2);
      return new String(Base64.encode(var3, 2), "UTF-8");
   }

   public static String recoverMethod_a_2f33e691(long var0) {
      long var2;
      switch ((int)(var0 % 10L)) {
         case 1:
            var2 = var0 * 5L + var0 % 88L;
            break;
         case 2:
            var2 = var0 * 23L + var0 % 15L;
            break;
         case 3:
            var2 = var0 * 3L + var0 % 73L;
            break;
         case 4:
            var2 = var0 * 13L + var0 % 96L;
            break;
         case 5:
            var2 = var0 * 17L + var0 % 49L;
            break;
         case 6:
            var2 = var0 * 7L + var0 % 68L;
            break;
         case 7:
            var2 = var0 * 31L + var0 % 39L;
            break;
         case 8:
            var2 = var0 * 29L + var0 % 41L;
            break;
         case 9:
            var2 = var0 * 37L + var0 % 91L;
            break;
         default:
            var2 = var0 * 8L + var0 % 74L;
      }

      return RecoverTypeaf2b07d5.recoverMethod_d_e96ea081("JCKP" + var2);
   }

   private static Object recoverMethod_a_6fe4881(Class var0, Object[] var1, Class[] var2) throws java.lang.InstantiationException, java.lang.NoSuchMethodException , java.lang.IllegalAccessException , java.lang.reflect.InvocationTargetException {
      Constructor var3 = var0.getConstructor(var2);
      return var3.newInstance(var1);
   }

   private static IvParameterSpec recoverMethod_a_f5fbd0b5(byte[] var0) {
      try {
         return (IvParameterSpec)recoverMethod_a_6fe4881(IvParameterSpec.class, new Object[]{var0}, new Class[]{byte[].class});
      } catch (Exception var2) {
         return null;
      }
   }

   private static byte[] recoverMethod_c_3b9947f4(String var0, String var1) throws java.io.UnsupportedEncodingException {
      byte[] var2 = new byte[var0.length()];
      byte[] var3 = var0.substring(0, var0.length() / 2).getBytes(var1);
      byte[] var4 = var0.substring(var0.length() / 2).getBytes(var1);
      System.arraycopy(var3, 0, var2, 0, var3.length);
      System.arraycopy(var4, 0, var2, var3.length, var4.length);
      return var2;
   }

   private static synchronized byte[] recoverMethod_a_afdcdd3a(String var0, RSAPublicKey var1, String var2) throws java.security.InvalidKeyException, java.security.NoSuchAlgorithmException, javax.crypto.IllegalBlockSizeException , javax.crypto.BadPaddingException, javax.crypto.NoSuchPaddingException {
      Cipher var3 = Cipher.getInstance(var2);
      var3.init(1, var1);
      byte[] var4 = var0.getBytes();
      return var3.doFinal(var4);
   }

   private static RSAPublicKey recoverMethod_a_d1778c87(String var0) {
      try {
         byte[] var1 = Base64.decode(var0, 2);
         KeyFactory var2 = KeyFactory.getInstance("RSA");
         X509EncodedKeySpec var3 = new X509EncodedKeySpec(var1);
         return (RSAPublicKey)var2.generatePublic(var3);
      } catch (Throwable var4) {
         return null;
      }
   }
}
