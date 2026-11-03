package cn.jiguang.an;

import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bk.RecoverTypeaf2b07cb;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class RecoverTypeaf2a9ea5 {
   public static final String recoverField_a = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{87, 84, 99});
   public static final String recoverField_b = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{73, 83, 66, 31, 99, 91, 75, 57, 65, 123, 99, 74, 61, 70, 112, 84, 68, 112, 102, 113}
   );
   private static final String recoverField_c = recoverField_a;

   public static void recoverMethod_a_9f49842d(String var0, File var1, File var2) {
      recoverMethod_a_52885326(1, var0, var1, var2);
   }

   private static void recoverMethod_a_52885326(int var0, String var1, File var2, File var3) {
      String var4 = var1.substring(0, 16);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
         "CryptoUtils",
         "doCrypto cipherMode is "
            + var0
            + ", key is "
            + var1
            + ", IvParameterSpec is "
            + var4
            + ", inputFile is "
            + var2.getAbsolutePath()
            + ", outputFile is "
            + var3.getAbsolutePath()
      );

      try {
         SecretKeySpec var5 = new SecretKeySpec(var1.getBytes(StandardCharsets.UTF_8), recoverField_c);
         IvParameterSpec var6 = new IvParameterSpec(var4.getBytes(StandardCharsets.UTF_8));
         Cipher var7 = Cipher.getInstance(recoverField_b);
         var7.init(var0, var5, var6);
         FileInputStream var8 = new FileInputStream(var2);

         try {
            ByteArrayOutputStream var9 = new ByteArrayOutputStream();

            try {
               byte[] var10 = new byte[1024];

               int var11;
               while ((var11 = var8.read(var10)) != -1) {
                  byte[] var12 = var7.update(var10, 0, var11);
                  if (var12 != null) {
                     var9.write(var12);
                  }
               }

               byte[] var23 = var7.doFinal();
               if (var23 != null) {
                  var9.write(var23);
               }

               FileOutputStream var13 = new FileOutputStream(var3);

               try {
                  var9.writeTo(var13);
               } catch (Throwable var19) {
                  try {
                     var13.close();
                  } catch (Throwable var18) {
                     var19.addSuppressed(var18);
                  }

                  throw var19;
               }

               var13.close();
            } catch (Throwable var20) {
               try {
                  var9.close();
               } catch (Throwable var17) {
                  var20.addSuppressed(var17);
               }

               throw var20;
            }

            var9.close();
         } catch (Throwable var21) {
            try {
               var8.close();
            } catch (Throwable var16) {
               var21.addSuppressed(var16);
            }

            throw var21;
         }

         var8.close();
      } catch (Exception var22) {
         RecoverTypeaf2a9ae9.recoverMethod_b_1765c98c("CryptoUtils", "Error encrypting/decrypting file, error message is " + var22.getMessage(), var22);
      }
   }
}
