package cn.jpush.android.ad;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory.Options;
import cn.jpush.android.helper.Logger;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class RecoverType9b4c5ae9 {
   public static void recoverMethod_a_e925fa09(String var0, String var1) {
      if (var0 != null && var1 != null) {
         Options var2 = new Options();
         var2.inJustDecodeBounds = true;
         var2.inSampleSize = 1;
         BitmapFactory.decodeFile(var0, var2);
         int var3 = var2.outWidth;
         int var4 = var2.outHeight;
         int var5 = recoverMethod_a_255f288(var3, var4);
         Logger.recoverMethod_d_e925fa09("ImageUtils", "compressImage computeSize=" + var5);
         Options var6 = new Options();
         var6.inSampleSize = var5;
         var6.inJustDecodeBounds = false;
         Bitmap var7 = BitmapFactory.decodeFile(var0, var6);
         if (var7 == null) {
            Logger.recoverMethod_d_e925fa09("ImageUtils", "compressImage tagBitmap is null");
         } else {
            String var8 = RecoverType9b4c5ae5.recoverMethod_a_e96ea081(var1);
            Logger.recoverMethod_d_e925fa09("ImageUtils", "compressImage file type=" + var8);

            try {
               ByteArrayOutputStream var9 = new ByteArrayOutputStream();

               label140: {
                  try {
                     boolean var10 = var7.compress(recoverMethod_a_1362c0bc(var8), 60, var9);
                     Logger.recoverMethod_d_e925fa09("ImageUtils", "compressImage compress=" + var10);
                     if (!var10) {
                        break label140;
                     }

                     File var11 = new File(var0);
                     if (var11.exists() && !var11.delete()) {
                        Logger.recoverMethod_d_e925fa09("ImageUtils", "Failed to delete original file: " + var0);
                     }

                     FileOutputStream var12 = new FileOutputStream(var0);

                     try {
                        var12.write(var9.toByteArray());
                     } catch (Throwable var24) {
                        try {
                           var12.close();
                        } catch (Throwable var23) {
                           var24.addSuppressed(var23);
                        }

                        throw var24;
                     }

                     var12.close();
                  } catch (Throwable var25) {
                     try {
                        var9.close();
                     } catch (Throwable var22) {
                        var25.addSuppressed(var22);
                     }

                     throw var25;
                  }

                  var9.close();
                  return;
               }

               var9.close();
            } catch (IOException var26) {
               Logger.recoverMethod_d_e925fa09("ImageUtils", "compressImage IOException=" + var26.getMessage());
               return;
            } finally {
               var7.recycle();
            }
         }
      } else {
         Logger.recoverMethod_d_e925fa09("ImageUtils", "File path or URL is null.");
      }
   }

   private static CompressFormat recoverMethod_a_1362c0bc(String var0) {
      switch (var0) {
         case "image/png":
            return CompressFormat.PNG;
         default:
            return CompressFormat.JPEG;
      }
   }

   private static int recoverMethod_a_255f288(int var0, int var1) {
      var0 = var0 % 2 == 1 ? var0 + 1 : var0;
      var1 = var1 % 2 == 1 ? var1 + 1 : var1;
      int var2 = Math.max(var0, var1);
      int var3 = Math.min(var0, var1);
      float var4 = (float)var3 / var2;
      if (var4 <= 1.0F && var4 > 0.5625) {
         if (var2 < 1664) {
            return 1;
         } else if (var2 < 4990) {
            return 2;
         } else if (var2 > 4990 && var2 < 10240) {
            return 4;
         } else {
            return var2 / 1280 == 0 ? 1 : var2 / 1280;
         }
      } else if (var4 <= 0.5625 && var4 > 0.5) {
         return var2 / 1280 == 0 ? 1 : var2 / 1280;
      } else {
         return (int)Math.ceil(var2 / (1280.0 / var4));
      }
   }

   public static Bitmap recoverMethod_a_295fb07d(String var0, int var1, int var2) {
      Options var3 = new Options();
      return BitmapFactory.decodeFile(var0, var3);
   }
}
