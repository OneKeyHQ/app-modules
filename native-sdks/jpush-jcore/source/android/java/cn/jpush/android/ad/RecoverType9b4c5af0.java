package cn.jpush.android.ad;

import android.util.Base64;
import android.util.Log;
import cn.jpush.android.helper.Logger;
import java.lang.reflect.Constructor;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Locale;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class RecoverType9b4c5af0 {
   private static final String recoverField_f = RecoverType9b4c5ae8.recoverMethod_b_79834524(new byte[]{87, 84, 99});
   private static final String recoverField_g = RecoverType9b4c5ae8.recoverMethod_b_79834524(
      new byte[]{73, 83, 66, 31, 99, 91, 75, 57, 65, 123, 99, 74, 61, 70, 112, 84, 68, 112, 102, 113}
   );
   public static final String recoverField_a = RecoverType9b4c5ae8.recoverMethod_b_79834524(new byte[]{91, 85, 5});
   public static final String recoverField_b = RecoverType9b4c5ae8.recoverMethod_b_79834524(new byte[]{99, 104, 88, 37, 39});
   public static final String recoverField_c = RecoverType9b4c5ae8.recoverMethod_b_79834524(new byte[]{74, 64, 87, 60, 2, 21, 47});
   public static final String recoverField_d = RecoverType9b4c5ae8.recoverMethod_b_79834524(new byte[]{87, 69, 109, 65, 120, 98, 68, 65, 119, 107, 115});
   public static final String recoverField_e = RecoverType9b4c5ae8.recoverMethod_b_79834524(
      new byte[]{124, 112, 70, 65, 55, 123, 115, 114, 69, 82, 112, 124, 111, 63, 125, 69, 106, 123, 119, 118, 85, 100, 112, 111, 115, 98, 68}
   );
   private static HashMap recoverField_h = new HashMap();

   public static synchronized byte[] recoverMethod_a_f31cd299(byte[] var0, String var1, String var2, boolean var3) throws java.io.UnsupportedEncodingException, java.security.InvalidKeyException, java.security.NoSuchAlgorithmException, javax.crypto.IllegalBlockSizeException , java.security.InvalidAlgorithmParameterException, javax.crypto.BadPaddingException, javax.crypto.NoSuchPaddingException {
      if (var1 == null) {
         return null;
      }

      byte[] var4 = recoverMethod_c_3b9947f4(var1, "UTF-8");
      SecretKeySpec var5 = new SecretKeySpec(var4, recoverField_f);
      IvParameterSpec var6 = recoverMethod_a_f5fbd0b5(var2.getBytes("UTF-8"));
      Cipher var7 = Cipher.getInstance(recoverField_g);
      var7.init(var3 ? 1 : 2, var5, var6);
      return var7.doFinal(var0);
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
         Logger.recoverMethod_w_e925fa09("", "Unexpected - failed to AES encrypt.");
         return "";
      }
   }

   public static String recoverMethod_b_8ea82db7(String var0, String var1) {
      try {
         return new String(recoverMethod_a_78a45faa(var0, var1, var1));
      } catch (Exception var3) {
         Logger.recoverMethod_w_e925fa09("", "Unexpected - failed to AES decrypt.");
         return "";
      }
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

   public static String recoverMethod_b_f169196d(String var0, String var1, String var2) throws java.io.UnsupportedEncodingException , java.security.InvalidKeyException , java.security.NoSuchAlgorithmException , javax.crypto.IllegalBlockSizeException , javax.crypto.BadPaddingException , javax.crypto.NoSuchPaddingException {
      byte[] var3 = recoverMethod_a_afdcdd3a(var0, recoverMethod_b_d1778c87(var1), var2);
      return new String(Base64.encode(var3, 2), "UTF-8");
   }

   private static synchronized byte[] recoverMethod_a_afdcdd3a(String var0, RSAPublicKey var1, String var2) throws java.security.InvalidKeyException, java.security.NoSuchAlgorithmException, javax.crypto.IllegalBlockSizeException , javax.crypto.BadPaddingException, javax.crypto.NoSuchPaddingException {
      byte[] var3 = Base64.decode(var0, 2);
      Cipher var4 = Cipher.getInstance(var2);
      var4.init(2, var1);
      byte[] var5 = var4.doFinal(var3);
      return Base64.decode(var5, 2);
   }

   private static RSAPublicKey recoverMethod_b_d1778c87(String var0) {
      try {
         byte[] var1 = Base64.decode(var0, 2);
         KeyFactory var2 = KeyFactory.getInstance("RSA");
         X509EncodedKeySpec var3 = new X509EncodedKeySpec(var1);
         PublicKey var4 = var2.generatePublic(var3);
         return (RSAPublicKey)var4;
      } catch (Throwable var5) {
         Log.d("JumpManager", "rsa loadRSAKey e=" + var5);
         return null;
      }
   }

   public static String recoverMethod_a_e96ea081(String var0) {
      try {
         MessageDigest var1 = MessageDigest.getInstance(recoverField_c);
         byte[] var2 = var1.digest(var0.getBytes());
         StringBuilder var3 = new StringBuilder();

         for (int var4 = 0; var4 < var2.length; var4++) {
            String var5 = Integer.toHexString(255 & var2[var4]).toUpperCase(Locale.US);
            if (var5.length() == 1) {
               var3.append("0");
            }

            var3.append(var5);
            if (var4 < var2.length - 1) {
               var3.append(":");
            }
         }

         return var3.toString();
      } catch (Throwable var9) {
         Logger.recoverMethod_w_e925fa09("Secure", " fail, error is " + var9.getMessage());
         return "";
      } finally {
         ;
      }
   }
}
