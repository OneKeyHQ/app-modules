package cn.jiguang.bg;

import android.content.Context;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bk.RecoverTypeaf2b07c9;
import cn.jiguang.bk.RecoverTypeaf2b07d5;
import cn.jiguang.bk.RecoverTypeaf2b07d7;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

public final class RecoverTypeaf2af8be {
   private final Context recoverField_a;

   public RecoverTypeaf2af8be(Context var1) {
      this.recoverField_a = var1;
   }

   public RecoverTypeaf2af8bf.Member904fc9bc recoverMethod_a_d95a863e(String var1, String var2) {
      this.recoverMethod_b_e925fa09(var1, var2);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSM", "s d: " + var1);

      try {
         return (RecoverTypeaf2af8bf.Member904fc9bc)new RecoverTypeaf2af8c0.Member904fcd7f<RecoverTypeaf2af8bf.Member904fc9bc>()
            .recoverMethod_a_3dad67c0(5)
            .recoverMethod_a_27e3bd3b(1000L, TimeUnit.MILLISECONDS)
            .recoverMethod_a_8e9912bc(() -> this.recoverMethod_c_d95a863e(var1, var2), "Download with version " + var2);
      } catch (RuntimeException var4) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JSM", "d f: " + var4.getMessage());
         return null;
      }
   }

   private void recoverMethod_b_e925fa09(String var1, String var2) {
      if (TextUtils.isEmpty(var1)) {
         throw new IllegalArgumentException("Download URL cannot be null or empty");
      }

      if (TextUtils.isEmpty(var2)) {
         throw new IllegalArgumentException("File name cannot be null or empty");
      }
   }

   private RecoverTypeaf2af8bf.Member904fc9bc recoverMethod_c_d95a863e(String var1, String var2) {
      HttpURLConnection var3 = null;

      try {
         Map var4 = RecoverTypeaf2af8bd.recoverMethod_a_604efcea();
         RecoverTypeaf2af8be.Member904fc5fc var5 = this.recoverMethod_a_4701d65b(var1, var4);
         var3 = var5.recoverField_a;
         this.recoverMethod_a_8823f89e(var3);
         RecoverTypeaf2af8be.Member904fc5fb var6 = this.recoverMethod_a_67bbb4fe(var3, var5.recoverField_b);
         this.recoverMethod_a_44c5a306(var2, var6.recoverField_a);
         this.recoverMethod_c_25e06fc(var6.recoverField_a);
         if (var5.recoverField_b != null) {
            RecoverTypeaf2af8c0.recoverMethod_b_aad3b1ff(var5.recoverField_b);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSM", "c m: " + var5.recoverField_b);
         }

         return new RecoverTypeaf2af8bf.Member904fc9bc(true, var6.recoverField_b);
      } catch (Exception var11) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JSM", "d f: " + var11.getMessage());
         return new RecoverTypeaf2af8bf.Member904fc9bc(false, null);
      } finally {
         if (var3 != null) {
            var3.disconnect();
         }
      }
   }

   private void recoverMethod_a_8823f89e(HttpURLConnection var1) throws java.io.IOException {
      if (var1.getResponseCode() != 200) {
         throw new IOException("Download failed with response code: " + var1.getResponseCode());
      }
   }

   private RecoverTypeaf2af8be.Member904fc5fb recoverMethod_a_67bbb4fe(HttpURLConnection var1, String var2) throws java.io.IOException {
      byte[] var3 = this.recoverMethod_a_b202b0f8(var1.getInputStream(), var1.getContentLength());
      String var4 = RecoverTypeaf2b07d5.recoverMethod_c_79834524(var3);
      if (!TextUtils.equals(var4, var2)) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JSM", "m f: " + var2 + " vs " + var4);
         throw new RuntimeException("MD5 validation failed");
      }

      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSM", "m ok");
      byte[] var5 = RecoverTypeaf2b07d7.recoverMethod_b_4962d961(var3);
      String var6 = RecoverTypeaf2af8c2.recoverMethod_a_79834524(var5);
      if (var6 != null) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSM", "v info: " + var6);
      }

      return new RecoverTypeaf2af8be.Member904fc5fb(var5, var6);
   }

   private void recoverMethod_a_44c5a306(String var1, byte[] var2) throws java.io.IOException {
      if (var2 == null || var2.length == 0) {
         throw new IOException("Downloaded data is null or empty");
      }

      if (!this.recoverMethod_a_25e0700(var2)) {
         throw new IOException("Downloaded data validation failed");
      }

      String var3 = RecoverTypeaf2af8bd.recoverMethod_a_a4ea6d43(this.recoverField_a, var1);
      File var4 = new File(var3);
      if (!RecoverTypeaf2b07c9.recoverMethod_a_de3c0df7(var4, var2)) {
         throw new IOException("Failed to save file atomically");
      }

      if (!this.recoverMethod_b_44c5a30a(var1, var2)) {
         throw new IOException("Saved file integrity verification failed");
      }

      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSM", "s ok:  " + var2.length);
   }

   private boolean recoverMethod_a_25e0700(byte[] var1) {
      if (var1 == null || var1.length == 0) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JSM", "d null");
         return false;
      } else if (!this.recoverMethod_b_25e0700(var1)) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JSM", "d invalid");
         return false;
      } else {
         return true;
      }
   }

   private boolean recoverMethod_b_44c5a30a(String var1, byte[] var2) {
      try {
         String var3 = RecoverTypeaf2af8bd.recoverMethod_a_a4ea6d43(this.recoverField_a, var1);
         File var4 = new File(var3);
         if (var4.exists() && var4.length() == var2.length) {
            String var5 = RecoverTypeaf2b07c9.recoverMethod_g_e96ea081(var3);
            String var6 = RecoverTypeaf2b07d5.recoverMethod_c_79834524(var2);
            if (!TextUtils.equals(var5, var6)) {
               RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JSM", "s verify f");
               return false;
            } else {
               return true;
            }
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JSM", "s mismatch");
            return false;
         }
      } catch (Exception var7) {
         return false;
      }
   }

   private boolean recoverMethod_b_25e0700(byte[] var1) {
      return var1 != null && var1.length >= 4 ? var1[0] == 127 && var1[1] == 69 && var1[2] == 76 && var1[3] == 70 : false;
   }

   private RecoverTypeaf2af8be.Member904fc5fc recoverMethod_a_4701d65b(String var1, Map var2) throws java.io.IOException {
      String var3 = var1;
      String var4 = null;

      for (int var5 = 0; var5 < 5; var5++) {
         HttpURLConnection var6 = this.recoverMethod_b_79904219(var3, var2);

         try {
            int var7 = var6.getResponseCode();
            var4 = this.recoverMethod_b_ac062ab8(var6, var4);
            if (!this.recoverMethod_a_134632(var7)) {
               return new RecoverTypeaf2af8be.Member904fc5fc(var6, var4);
            }

            var3 = this.recoverMethod_a_163e14e5(var6, var3, var5);
         } catch (Exception var8) {
            var6.disconnect();
            throw var8;
         }
      }

      throw new IOException(String.format("Too many redirects, max %d exceeded", 5));
   }

   private HttpURLConnection recoverMethod_b_79904219(String var1, Map var2) throws java.io.IOException, java.net.MalformedURLException, java.net.ProtocolException {
      HttpURLConnection var3 = (HttpURLConnection)new URL(var1).openConnection();
      var3.setRequestMethod("GET");
      var3.setConnectTimeout(10000);
      var3.setReadTimeout(10000);
      var3.setInstanceFollowRedirects(false);
      if (var2 != null) {
         for (Object recoveryElement_193 : var2.entrySet()) {
            Entry var5 = (Entry)recoveryElement_193;
            var3.setRequestProperty((String)var5.getKey(), (String)var5.getValue());
         }
      }

      return var3;
   }

   private String recoverMethod_b_ac062ab8(HttpURLConnection var1, String var2) {
      if (var2 == null) {
         String var3 = var1.getHeaderField(RecoverTypeaf2af8bd.recoverField_e);
         if (TextUtils.isEmpty(var3)) {
            var3 = var1.getHeaderField(RecoverTypeaf2af8bd.recoverField_f);
         }

         if (!TextUtils.isEmpty(var3)) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSM", "m: " + var3);
            return var3;
         }
      }

      return var2;
   }

   private String recoverMethod_a_163e14e5(HttpURLConnection var1, String var2, int var3) throws java.io.IOException {
      String var4 = var1.getHeaderField("Location");
      if (var4 != null && !var4.isEmpty()) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSM", String.format("r %d: %s -> %s", var3 + 1, var2, var4));
         var1.disconnect();
         return var4;
      } else {
         var1.disconnect();
         throw new IOException("Redirect response without Location header");
      }
   }

   private boolean recoverMethod_a_134632(int var1) {
      return var1 == 301 || var1 == 302 || var1 == 303 || var1 == 307 || var1 == 308;
   }

   private byte[] recoverMethod_a_b202b0f8(InputStream var1, int var2) throws java.io.IOException {
      if (var1 == null) {
         return new byte[0];
      }

      int var3 = this.recoverMethod_b_134621(var2);
      ByteArrayOutputStream var4 = new ByteArrayOutputStream(Math.min(1048576, var3 * 8));

      byte[] var9;
      try {
         byte[] var5 = new byte[var3];
         long var7 = 0L;

         int var6;
         while ((var6 = var1.read(var5)) != -1) {
            if (var7 + var6 > 10485760L) {
               throw new IOException("File size exceeds maximum allowed size");
            }

            var4.write(var5, 0, var6);
            var7 += var6;
         }

         var9 = var4.toByteArray();
      } catch (Throwable var11) {
         try {
            var4.close();
         } catch (Throwable var10) {
            var11.addSuppressed(var10);
         }

         throw var11;
      }

      var4.close();
      return var9;
   }

   private int recoverMethod_b_134621(int var1) {
      return var1 > 0 ? Math.min(65536, Math.max(8192, var1)) : 8192;
   }

   private void recoverMethod_c_25e06fc(byte[] var1) {
      try {
         String var2 = RecoverTypeaf2b07d5.recoverMethod_c_79834524(var1);
         if (var2 != null) {
            RecoverTypeaf2af8c0.recoverMethod_c_aad3b1ff(var2);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JSM", "c m: " + var2);
         }
      } catch (Exception var3) {
      }
   }

   private static class Member904fc5fb {
      final byte[] recoverField_a;
      final String recoverField_b;

      Member904fc5fb(byte[] var1, String var2) {
         this.recoverField_a = var1;
         this.recoverField_b = var2;
      }
   }

   private static class Member904fc5fc {
      final HttpURLConnection recoverField_a;
      final String recoverField_b;

      Member904fc5fc(HttpURLConnection var1, String var2) {
         this.recoverField_a = var1;
         this.recoverField_b = var2;
      }
   }
}
