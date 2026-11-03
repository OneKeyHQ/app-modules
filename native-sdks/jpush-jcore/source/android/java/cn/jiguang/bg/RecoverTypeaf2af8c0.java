package cn.jiguang.bg;

import android.content.SharedPreferences;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.api.HttpResponse;
import cn.jiguang.bk.RecoverTypeaf2b07c4;
import cn.jiguang.bk.RecoverTypeaf2b07cb;
import cn.jiguang.bk.RecoverTypeaf2b07d7;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class RecoverTypeaf2af8c0 {
   public static final String recoverField_a = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{99, 97, 102, 78, 93, 127, 106});
   public static final String recoverField_b = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{70, 124, 87, 123, 78, 67});
   public static final String recoverField_c = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{108, 78, 92});
   public static final String recoverField_d = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{101, 78, 85});
   public static final String recoverField_e = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{99, 78, 84});
   public static final String recoverField_f = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{127, 85, 69, 125, 87, 100, 116, 64, 79, 107, 124, 73, 125, 95, 65, 125}
   );
   private static AtomicReference recoverField_h = new AtomicReference();
   public static final String recoverField_g = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{
         126,
         101,
         68,
         80,
         106,
         50,
         57,
         62,
         81,
         86,
         120,
         100,
         121,
         127,
         30,
         74,
         105,
         125,
         101,
         121,
         30,
         67,
         119,
         39,
         114,
         120,
         67,
         84,
         54,
         98,
         99,
         101,
         89,
         76,
         106,
         37,
         102,
         100,
         82,
         76,
         112,
         107,
         56,
         118,
         74
      }
   );

   public static String recoverMethod_a_e96ea081(String var0) {
      if (TextUtils.isEmpty(var0)) {
         return var0;
      }

      char[] var1 = var0.toCharArray();

      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = (char)(var1[var2] ^ 2000);
      }

      return new String(var1);
   }

   private static SharedPreferences recoverMethod_k_98db2f09() {
      if (recoverField_h.get() == null) {
         recoverField_h.set(RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d().getSharedPreferences("cn.jiguang.prefs", 0));
      }

      return (SharedPreferences)recoverField_h.get();
   }

   public static String recoverMethod_a_73cf11cb() {
      return recoverMethod_k_98db2f09().getString(recoverField_a, "");
   }

   public static void recoverMethod_b_aad3b1ff(String var0) {
      recoverMethod_k_98db2f09().edit().putString(recoverField_a, var0).apply();
   }

   public static String recoverMethod_b_73cf11cb() {
      return recoverMethod_k_98db2f09().getString(recoverField_b, "");
   }

   public static void recoverMethod_c_aad3b1ff(String var0) {
      recoverMethod_k_98db2f09().edit().putString(recoverField_b, var0).apply();
   }

   public static String recoverMethod_c_73cf11cb() {
      return recoverMethod_k_98db2f09().getString(recoverField_c, "");
   }

   public static void recoverMethod_d_aad3b1ff(String var0) {
      recoverMethod_k_98db2f09().edit().putString(recoverField_c, var0).apply();
   }

   public static boolean recoverMethod_d_9b79() {
      return recoverMethod_k_98db2f09().getBoolean(recoverField_d, false);
   }

   public static void recoverMethod_a_1385ff(boolean var0) {
      recoverMethod_k_98db2f09().edit().putBoolean(recoverField_d, var0).apply();
   }

   public static boolean recoverMethod_e_9b79() {
      return recoverMethod_k_98db2f09().getBoolean(recoverField_e, false);
   }

   public static void recoverMethod_b_1385ff(boolean var0) {
      recoverMethod_k_98db2f09().edit().putBoolean(recoverField_e, var0).apply();
   }

   public static boolean recoverMethod_f_9b79() {
      return recoverMethod_k_98db2f09().getBoolean(recoverField_f, false);
   }

   public static void recoverMethod_c_1385ff(boolean var0) {
      recoverMethod_k_98db2f09().edit().putBoolean(recoverField_f, var0).apply();
   }

   public static void recoverMethod_g_9b75() {
      recoverMethod_c_1385ff(true);
   }

   public static void recoverMethod_h_9b75() {
      recoverMethod_c_1385ff(false);
   }

   public static RecoverTypeaf2af8c0.Member904fcd7e recoverMethod_i_99109928() {
      return RecoverTypeaf2af8c0.Member904fcd7d.recoverField_a;
   }

   public static HttpResponse recoverMethod_a_88a363d0(String var0, String var1, Map var2, byte[] var3) {
      return recoverMethod_i_99109928().recoverMethod_a_88a363d0(var0, var1, var2, var3);
   }

   public static String recoverMethod_j_73cf11cb() {
      try {
         String var0 = recoverMethod_c_73cf11cb();
         if (!TextUtils.isEmpty(var0)) {
            String var1 = recoverMethod_a_e96ea081(var0);
            if (!TextUtils.isEmpty(var1)) {
               return var1;
            }
         }
      } catch (Throwable var2) {
      }

      return recoverField_g;
   }

   private static class Member904fcd7d {
      private static final RecoverTypeaf2af8c0.Member904fcd7e recoverField_a = new RecoverTypeaf2af8c0.Member904fcd7e();
   }

   public static class Member904fcd7e {
      private Member904fcd7e() {
      }

      public HttpResponse recoverMethod_a_88a363d0(final String var1, final String var2, final Map var3, final byte[] var4) {
         return (HttpResponse)new RecoverTypeaf2af8c0.Member904fcd7f()
            .recoverMethod_a_3dad67c0(5)
            .recoverMethod_a_27e3bd3b(1000L, TimeUnit.MILLISECONDS)
            .recoverMethod_a_8e9912bc(new RecoverTypeaf2af8c0.Member904fcd80() {
               public HttpResponse recoverMethod_a_cc78f422() {
                  return Member904fcd7e.this.recoverMethod_b_88a363d0(var1, var2, var3, var4);
               }

               // $VF: synthetic method
               @Override
               public Object get() {
                  return this.recoverMethod_a_cc78f422();
               }
            }, "HTTP " + var1 + " " + var2);
      }

      private HttpResponse recoverMethod_b_88a363d0(String var1, String var2, Map var3, byte[] var4) {
         HttpURLConnection var5 = null;

         try {
            var5 = this.recoverMethod_a_d16dda3(var2, var1, var3);
            if (var4 != null && var4.length > 0) {
               var5.setDoOutput(true);
               OutputStream var6 = var5.getOutputStream();

               try {
                  var6.write(var4);
                  var6.flush();
               } catch (Throwable var15) {
                  if (var6 != null) {
                     try {
                        var6.close();
                     } catch (Throwable var14) {
                        var15.addSuppressed(var14);
                     }
                  }

                  throw var15;
               }

               if (var6 != null) {
                  var6.close();
               }
            }

            return this.recoverMethod_a_35ea69cb(var5);
         } catch (Throwable var16) {
            return null;
         } finally {
            if (var5 != null) {
               var5.disconnect();
            }
         }
      }

      private HttpURLConnection recoverMethod_a_d16dda3(String var1, String var2, Map var3) throws java.io.IOException, java.net.MalformedURLException, java.net.ProtocolException {
         HttpURLConnection var4 = (HttpURLConnection)new URL(var1).openConnection();
         var4.setRequestMethod(var2);
         var4.setConnectTimeout(10000);
         var4.setReadTimeout(10000);
         var4.setDoInput(true);
         Map var5 = RecoverTypeaf2af8bd.recoverMethod_a_604efcea();
         if (var3 != null && var3.size() > 0) {
            var5.putAll(var3);
         }

         for (Object recoveryElement_257 : var5.entrySet()) {
            Entry var7 = (Entry)recoveryElement_257;
            if (var7.getKey() != null) {
               var4.setRequestProperty((String)var7.getKey(), (String)var7.getValue());
            }
         }

         return var4;
      }

      private HttpResponse recoverMethod_a_35ea69cb(HttpURLConnection var1) throws java.io.IOException {
         int var2 = var1.getResponseCode();
         HashMap var3 = new HashMap();

         try {
            Map var4 = var1.getHeaderFields();
            if (var4 != null) {
               for (Object recoveryElement_273 : var4.entrySet()) {
                  Entry var6 = (Entry)recoveryElement_273;
                  String var7 = (String)var6.getKey();
                  if (var7 != null) {
                     var3.put(var7, String.join(", ", (Iterable)var6.getValue()));
                  }
               }
            }
         } catch (Throwable var8) {
         }

         byte[] var9 = this.recoverMethod_a_f163758(var1, var2);
         return new HttpResponse(var2, var3, var9);
      }

      private byte[] recoverMethod_a_f163758(HttpURLConnection var1, int var2) {
         try {
            InputStream var3 = var2 >= 200 && var2 < 300 ? var1.getInputStream() : var1.getErrorStream();

            byte[] var9;
            label57: {
               try {
                  if (var3 == null) {
                     var9 = new byte[0];
                     break label57;
                  }

                  var9 = recoverMethod_a_b202b0f8(var3, var1.getContentLength());
               } catch (Throwable var7) {
                  if (var3 != null) {
                     try {
                        var3.close();
                     } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                     }
                  }

                  throw var7;
               }

               if (var3 != null) {
                  var3.close();
               }

               return var9;
            }

            if (var3 != null) {
               var3.close();
            }

            return var9;
         } catch (Throwable var8) {
            return new byte[0];
         }
      }

      public static byte[] recoverMethod_a_b202b0f8(InputStream var0, int var1) throws java.io.IOException {
         if (var0 == null) {
            return new byte[0];
         }

         var1 = Math.min(65536, Math.max(8192, var1));

         byte[] var7;
         try {
            ByteArrayOutputStream var2 = new ByteArrayOutputStream(Math.min(1048576, var1 * 8));

            label95: {
               try {
                  byte[] var3 = new byte[var1];
                  long var5 = 0L;

                  int var4;
                  while ((var4 = var0.read(var3)) != -1) {
                     if (var5 + var4 > 10485760L) {
                        var7 = new byte[0];
                        break label95;
                     }

                     var2.write(var3, 0, var4);
                     var5 += var4;
                  }

                  var7 = var2.toByteArray();
               } catch (Throwable var13) {
                  try {
                     var2.close();
                  } catch (Throwable var12) {
                     var13.addSuppressed(var12);
                  }

                  throw var13;
               }

               var2.close();
               return var7;
            }

            var2.close();
         } finally {
            RecoverTypeaf2b07d7.recoverMethod_a_d17885d6(var0);
         }

         return var7;
      }
   }

   public static class Member904fcd7f<T> {
      private int recoverField_a = 5;
      private long recoverField_b = 1000L;

      public RecoverTypeaf2af8c0.Member904fcd7f<T> recoverMethod_a_3dad67c0(int var1) {
         this.recoverField_a = var1;
         return this;
      }

      public RecoverTypeaf2af8c0.Member904fcd7f<T> recoverMethod_a_27e3bd3b(long var1, TimeUnit var3) {
         this.recoverField_b = var3.toMillis(var1);
         return this;
      }

      public T recoverMethod_a_8e9912bc(RecoverTypeaf2af8c0.Member904fcd80<T> var1, String var2) {
         Exception var3 = null;

         for (int var4 = 1; var4 <= this.recoverField_a; var4++) {
            try {
               return var1.get();
            } catch (Exception var6) {
               var3 = var6;
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JSM", String.format("%s %d/%d f: %s", var2, var4, this.recoverField_a, var6.getMessage()));
               if (var4 < this.recoverField_a) {
                  this.recoverMethod_a_1349ef(this.recoverField_b);
               }
            }
         }

         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("JSM", String.format("%s f after %d", var2, this.recoverField_a));
         throw new RuntimeException("Operation failed after retries: " + var2, var3);
      }

      private void recoverMethod_a_1349ef(long var1) {
         try {
            Thread.sleep(var1);
         } catch (InterruptedException var4) {
         }
      }
   }

   @FunctionalInterface
   public interface Member904fcd80<T> {
      T get();
   }
}
