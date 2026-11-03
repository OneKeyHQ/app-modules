package cn.jiguang.ao;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.api.JCoreManager;
import cn.jiguang.bk.RecoverTypeaf2b07d4;
import cn.jiguang.bk.RecoverTypeaf2b07d5;
import cn.jiguang.bk.RecoverTypeaf2b07d7;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.net.HttpRequest;
import cn.jiguang.net.HttpResponse;
import cn.jiguang.net.HttpUtils;
import cn.jiguang.net.SSLTrustManager;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Set;

public class RecoverTypeaf2aa267 {
   public static SSLTrustManager recoverField_a;

   private static void recoverMethod_a_8047a97a(HttpRequest var0, Context var1, byte[] var2, int var3, int var4) {
      var0.setConnectTimeout(30000);
      var0.setReadTimeout(30000);
      var0.setDoOutPut(true);
      var0.setDoInPut(true);
      var0.setUseCaches(false);
      var0.setBody(var2);
      var0.setRequestProperty("Content-Length", String.valueOf(var2.length));
      var0.setNeedRetryIfHttpsFailed(true);
      if (recoverField_a == null) {
         String var5 = "";

         try {
            var5 = "";
            if (!TextUtils.isEmpty(var5)) {
               recoverField_a = new SSLTrustManager(var5);
            }
         } catch (Throwable var11) {
         }
      }

      if (recoverField_a != null) {
         var0.setSslTrustManager(recoverField_a);
      }

      var0.setRequestProperty("Accept", "application/jason");
      var0.setRequestProperty("Accept-Encoding", "gzip");
      var0.setRequestProperty("X-App-Key", RecoverType5a68d10.recoverMethod_e_e501eb0d(var1));
      var0.setRequestProperty("Charset", "UTF-8");
      String var13 = var0.getUrl();
      boolean var6 = var13.contains("ipv6");
      String var7 = var6 ? RecoverTypeaf2aa26b.recoverField_f : RecoverTypeaf2aa26b.recoverField_e;
      if (JCoreManager.isTestEnv()) {
         Uri var8 = Uri.parse(var13);
         var7 = var8.getHost();
      }

      var0.setRequestProperty("Host", var7);
      String var14 = RecoverTypeaf2b07d4.recoverMethod_a_2f33e691(var3);
      if (var4 == 2) {
         var0.setHaveRspData(true);
         var0.setRspDatazip(true);
         var0.setNeedErrorInput(true);
         var0.setRequestProperty("FillType", "1");
         String var9 = RecoverTypeaf2aa26b.recoverMethod_b_e96ea081(var14);
         var0.setRequestProperty("Authorization", var9);
      } else {
         var0.setHaveRspData(false);
         var0.setRspDatazip(false);
         String var15 = RecoverTypeaf2b07d5.recoverMethod_b_79834524(var2);
         String var10 = RecoverTypeaf2aa26b.recoverMethod_a_22670df9(var1, var15, var14);
         var0.setRequestProperty("Authorization", "Basic " + var10);
      }
   }

   private static void recoverMethod_a_a94c9ea9(HttpRequest var0, Context var1, byte[] var2, int var3) throws java.io.UnsupportedEncodingException , java.security.InvalidKeyException , java.security.NoSuchAlgorithmException , javax.crypto.IllegalBlockSizeException , java.security.InvalidAlgorithmParameterException , javax.crypto.BadPaddingException , javax.crypto.NoSuchPaddingException {
      var0.setConnectTimeout(30000);
      var0.setReadTimeout(30000);
      var0.setDoOutPut(true);
      var0.setDoInPut(true);
      var0.setUseCaches(false);
      String var4 = RecoverTypeaf2b07d4.recoverMethod_a_2f33e691(RecoverTypeaf2b07d4.recoverMethod_a_9b68());
      String var5;
      if (var3 != 2 && var3 != 3) {
         var5 = RecoverTypeaf2aa26b.recoverField_b;
      } else {
         var5 = RecoverTypeaf2aa26b.recoverField_a;
      }

      byte[] var6 = RecoverTypeaf2b07d4.recoverMethod_a_f31cd299(var2, var4, var5, true);
      var0.setBody(var6);
      var0.setRequestProperty("Content-Length", String.valueOf(var6.length));
      var0.setNeedRetryIfHttpsFailed(true);
      if (recoverField_a == null) {
         String var7 = "";

         try {
            var7 = "";
            if (!TextUtils.isEmpty(var7)) {
               recoverField_a = new SSLTrustManager(var7);
            }
         } catch (Throwable var10) {
         }
      }

      if (recoverField_a != null) {
         var0.setSslTrustManager(recoverField_a);
      }

      var0.setRequestProperty("Accept", "application/jason");
      var0.setRequestProperty("Accept-Encoding", "gzip");
      var0.setRequestProperty("X-App-Key", RecoverType5a68d10.recoverMethod_e_e501eb0d(var1));
      if (var3 != 2 && var3 != 3) {
         boolean var14 = var3 == 4;
         var0.setHaveRspData(var14);
         var0.setRspDatazip(var14);
         String var8 = RecoverTypeaf2b07d5.recoverMethod_b_79834524(var6);
         String var9 = RecoverTypeaf2aa26b.recoverMethod_a_cdb8d613(var1, var8, var4, var14);
         var0.setRequestProperty("Authorization", "Basic " + var9);
      } else {
         var0.setHaveRspData(true);
         var0.setRspDatazip(true);
         var0.setNeedErrorInput(true);
         var0.setRequestProperty("FillType", "1");
         String var12 = "";
         if (var3 == 3) {
            var0.setRequestProperty("X-Real-IP", "jcore");
            var12 = RecoverTypeaf2aa26b.recoverMethod_c_e96ea081(var4);
         } else {
            var12 = RecoverTypeaf2aa26b.recoverMethod_b_e96ea081(var4);
         }

         var0.setRequestProperty("Authorization", var12);
      }

      var0.setRequestProperty("Charset", "UTF-8");
   }

   public static RecoverTypeaf2aa26c recoverMethod_a_7e961685(Context var0, String var1, byte[] var2, int var3, int var4, int var5, Set var6) {
      try {
         HttpRequest var7 = new HttpRequest(var1);
         var7.setType(var6.toString());
         recoverMethod_a_8047a97a(var7, var0, var2, var3, var5);

         while (var4 > 0) {
            var4--;
            HttpResponse var8 = HttpUtils.httpPost(var0, var7);
            int var9 = var8.getResponseCode();
            RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("HttpHelper", "status code:" + var9 + " retry left:" + var4);
            switch (var9) {
               case 200:
                  return new RecoverTypeaf2aa26c(0, var8.getResponseBody());
               case 401:
                  return new RecoverTypeaf2aa26c(-3, var8.getResponseBody());
               case 404:
               case 410:
               case 429:
                  return new RecoverTypeaf2aa26c(-1, var8.getResponseBody());
               case 503:
                  return new RecoverTypeaf2aa26c(-2, var8.getResponseBody());
               case 3005:
                  break;
               default:
                  if (var9 >= 500) {
                     return new RecoverTypeaf2aa26c(-1, var8.getResponseBody());
                  }

                  return new RecoverTypeaf2aa26c(-2, var8.getResponseBody());
            }
         }
      } catch (AssertionError var10) {
         return new RecoverTypeaf2aa26c(-2, "Catch AssertionError to avoid http close crash - " + var10.getMessage());
      } catch (Exception var11) {
         return new RecoverTypeaf2aa26c(-2, "Exception - " + var11.getMessage());
      } catch (Throwable var12) {
         return new RecoverTypeaf2aa26c(-2, "Exception - " + var12.getMessage());
      }

      return new RecoverTypeaf2aa26c(-2, "Failed - retry enough");
   }

   public static RecoverTypeaf2aa26c recoverMethod_a_ab3ea53a(String var0, String var1, Context var2, boolean var3, int var4, int var5) {
      try {
         byte[] var6;
         try {
            var6 = var1.getBytes("UTF-8");
         } catch (UnsupportedEncodingException var11) {
            return new RecoverTypeaf2aa26c(-2, "Exception - " + var11.getMessage());
         }

         if (var3) {
            try {
               var6 = RecoverTypeaf2b07d7.recoverMethod_a_4962d961(var6);
            } catch (IOException var10) {
               return new RecoverTypeaf2aa26c(-2, "zip err:" + var10.getMessage());
            }
         }

         HttpRequest var7 = new HttpRequest(var0);
         recoverMethod_a_a94c9ea9(var7, var2, var6, var5);

         while (var4 > 0) {
            var4--;
            HttpResponse var8 = HttpUtils.httpPost(var2, var7);
            int var9 = var8.getResponseCode();
            RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("HttpHelper", "status code:" + var9 + " retry left:" + var4);
            switch (var9) {
               case 200:
                  return new RecoverTypeaf2aa26c(0, var8.getResponseBody());
               case 401:
                  return new RecoverTypeaf2aa26c(-3, var8.getResponseBody());
               case 404:
               case 410:
               case 429:
                  return new RecoverTypeaf2aa26c(-1, var8.getResponseBody());
               case 503:
                  return new RecoverTypeaf2aa26c(-2, var8.getResponseBody());
               case 3005:
                  break;
               case 3006:
                  return new RecoverTypeaf2aa26c(3006, var8.getResponseBody());
               default:
                  if (var9 >= 500) {
                     return new RecoverTypeaf2aa26c(-1, var8.getResponseBody());
                  }

                  return new RecoverTypeaf2aa26c(-2, var8.getResponseBody());
            }
         }
      } catch (AssertionError var12) {
         return new RecoverTypeaf2aa26c(-2, "Catch AssertionError to avoid http close crash - " + var12.getMessage());
      } catch (Exception var13) {
         return new RecoverTypeaf2aa26c(-2, "Exception - " + var13.getMessage());
      } catch (Throwable var14) {
         return new RecoverTypeaf2aa26c(-2, "Exception - " + var14.getMessage());
      }

      return new RecoverTypeaf2aa26c(-2, "Failed - retry enough");
   }
}
