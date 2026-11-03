package cn.jpush.android.w;

import android.content.Context;
import android.text.TextUtils;
import cn.jpush.android.ad.RecoverType9b4c5ae8;
import cn.jpush.android.ad.RecoverType9b4c5af2;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.helper.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManager;

public class RecoverType2e4d157e {
   public static final String recoverField_a = RecoverType9b4c5ae8.recoverMethod_b_79834524(new byte[]{32, 0, 14, 41, 38, 38, 63, 1, 23, 43});

   public static RecoverType2e4d157d recoverMethod_a_ad8da5c2(Context var0, RecoverType2e4d157c var1) {
      return recoverMethod_a_e6b1dcc2(var0, var1, true, false);
   }

   public static RecoverType2e4d157d recoverMethod_a_e6b1dcc2(Context var0, RecoverType2e4d157c var1, boolean var2, boolean var3) {
      if (var1 == null) {
         return null;
      }

      RecoverType2e4d157d var4 = new RecoverType2e4d157d(var1.recoverMethod_a_73cf11cb());
      if (!JCoreHelper.isAuth(var0)) {
         var4.recoverMethod_a_13462e(3006);
         var4.recoverMethod_a_aad3b1ff("user not auth!!!");
         return var4;
      }

      boolean var5 = JCoreHelper.isContinueBusiness(var0);
      Logger.recoverMethod_w_e925fa09("HttpUtils", " https sdk continueBusiness=" + var5);
      if (!var5) {
         Logger.recoverMethod_w_e925fa09("HttpUtils", " https sdk limit");
         var4.recoverMethod_a_13462e(3006);
         var4.recoverMethod_a_aad3b1ff("https sdk limit");
         return var4;
      }

      HttpURLConnection var6 = null;
      InputStream var7 = null;
      InputStream var8 = null;
      OutputStream var9 = null;
      int var10 = -1;

      try {
         var6 = recoverMethod_a_e2deae62(var0, var1.recoverMethod_a_73cf11cb());
         URL var11 = var6.getURL();
         var10 = var11.getPort();
         Logger.recoverMethod_v_e925fa09("HttpUtils", "host:" + var11.getHost() + ",port:" + var10);
         recoverMethod_a_f793a0d1(var1, var6);
         if (var6 instanceof HttpsURLConnection) {
            try {
               if (var1.recoverMethod_f_ebfc5567() != null) {
                  SSLContext var12 = SSLContext.getInstance("TLS");
                  TrustManager[] var13 = new TrustManager[]{var1.recoverMethod_f_ebfc5567()};
                  var12.init(null, var13, new SecureRandom());
                  if (var12 != null) {
                     ((HttpsURLConnection)var6).setSSLSocketFactory(var12.getSocketFactory());
                  }
               }

               if (var1.recoverMethod_g_1a6e2dd9() != null) {
                  ((HttpsURLConnection)var6).setHostnameVerifier(var1.recoverMethod_g_1a6e2dd9());
               } else {
                  ((HttpsURLConnection)var6).setHostnameVerifier(new RecoverType2e4d157a(var6.getURL().getHost()));
               }
            } catch (Throwable var27) {
               Logger.recoverMethod_ww_e925fa09("HttpUtils", "set ssl config error:" + var27.getMessage());
            }
         }

         int var33 = 0;
         if (var2 || var3) {
            var6.setRequestMethod(var3 ? "PUT" : "POST");
            var6.setDoOutput(true);
            var6.setDoInput(true);
            var6.setUseCaches(var1.recoverMethod_d_9b79());
            byte[] var34 = var1.recoverMethod_h_12d408();
            if (var34 != null) {
               var33 = var34.length;
               var9 = var6.getOutputStream();
               var9.write(var34);
            }
         }

         int var35 = var6.getResponseCode();
         var4.recoverMethod_a_13462e(var35);
         byte[] var14 = null;

         try {
            var7 = var6.getInputStream();
         } catch (Throwable var26) {
            Logger.recoverMethod_d_e925fa09("HttpUtils", "get input stream error:" + var26.getMessage());
         }

         if (var1.recoverMethod_j_9b79() && var7 != null) {
            try {
               var14 = recoverMethod_a_b328ee5f(var7);
               if (var14 != null && var1.recoverMethod_k_9b79()) {
                  var14 = RecoverType9b4c5af2.recoverMethod_a_4962d961(var14);
               }
            } catch (Throwable var25) {
            }
         }

         if (var14 == null && var35 != 200 && var1.recoverMethod_e_9b79()) {
            var8 = var6.getErrorStream();
            var14 = recoverMethod_a_b328ee5f(var8);
         }

         int var15 = 0;
         if (var14 != null) {
            var15 = var14.length;
            String var16 = new String(var14, "UTF-8");
            var4.recoverMethod_a_aad3b1ff(var16);
         }

         JCoreHelper.netMonitor(var0, var1.recoverMethod_a_73cf11cb(), var1.recoverMethod_l_73cf11cb(), var33, var15);
         recoverMethod_a_f6dc1af0(var6, var4);
      } catch (MalformedURLException var28) {
         Logger.recoverMethod_w_e925fa09("HttpUtils", "http post  error:" + var28.getMessage());
         var4.recoverMethod_a_13462e(3004);
         var4.recoverMethod_a_aad3b1ff("MalformedURLException");
      } catch (IOException var29) {
         var4.recoverMethod_a_13462e(2998);
         var4.recoverMethod_a_aad3b1ff("网络错误");
         if (var29 instanceof SocketTimeoutException) {
            var4.recoverMethod_a_13462e(3001);
            var4.recoverMethod_a_aad3b1ff("请求超时");
         } else if (var29 instanceof UnknownHostException) {
            var4.recoverMethod_a_13462e(3003);
            var4.recoverMethod_a_aad3b1ff("域名无效");
         } else if (var29 instanceof SSLHandshakeException) {
            var4.recoverMethod_a_13462e(3005);
            var4.recoverMethod_a_aad3b1ff("SSL失败");
         }

         Logger.recoverMethod_w_e925fa09("HttpUtils", "http post IOException error:" + var29.getMessage());
      } catch (Throwable var30) {
         Logger.recoverMethod_w_e925fa09("HttpUtils", "http post Exception error:" + var30);
         var4.recoverMethod_a_13462e(3006);
         var4.recoverMethod_a_aad3b1ff("UNKnow execption" + var30.getMessage());
      } finally {
         RecoverType9b4c5af2.recoverMethod_a_d17885d6(var7);
         RecoverType9b4c5af2.recoverMethod_a_d17885d6(var8);
         RecoverType9b4c5af2.recoverMethod_a_d17885d6(var9);
         if (var6 != null) {
            var6.disconnect();
         }
      }

      return var4;
   }

   public static String recoverMethod_a_abb17b30(Map var0) {
      StringBuilder var1 = new StringBuilder("");
      if (var0 != null && var0.size() > 0) {
         Iterator var2 = var0.entrySet().iterator();

         try {
            while (var2.hasNext()) {
               Entry var3 = (Entry)var2.next();
               var1.append((String)var3.getKey()).append("=").append(URLEncoder.encode((String)var3.getValue(), "UTF-8"));
               if (var2.hasNext()) {
                  var1.append("&");
               }
            }
         } catch (Exception var4) {
         }
      }

      return var1.toString();
   }

   private static void recoverMethod_a_f793a0d1(RecoverType2e4d157c var0, HttpURLConnection var1) {
      if (var0 != null && var1 != null) {
         recoverMethod_a_d4ec2e19(var0.recoverMethod_i_604efcea(), var1);
         if (var0.recoverMethod_b_9b68() >= 0) {
            var1.setConnectTimeout(var0.recoverMethod_b_9b68());
         }

         if (var0.recoverMethod_c_9b68() >= 0) {
            var1.setReadTimeout(var0.recoverMethod_c_9b68());
         }
      }
   }

   public static void recoverMethod_a_d4ec2e19(Map var0, HttpURLConnection var1) {
      if (var0 != null && var0.size() != 0 && var1 != null) {
         for (Object recoveryElement_206 : var0.entrySet()) {
            Entry var3 = (Entry)recoveryElement_206;
            if (!TextUtils.isEmpty((CharSequence)var3.getKey())) {
               var1.setRequestProperty((String)var3.getKey(), (String)var3.getValue());
            }
         }
      }
   }

   private static void recoverMethod_a_f6dc1af0(HttpURLConnection var0, RecoverType2e4d157d var1) throws java.io.IOException {
      if (var1 != null && var0 != null) {
         var1.recoverMethod_a_13462e(var0.getResponseCode());
         var1.recoverMethod_b_13462e(var0.getResponseCode());
         var1.recoverMethod_a_e925fa09("expires", var0.getHeaderField("Expires"));
         var1.recoverMethod_a_e925fa09("cache-control", var0.getHeaderField("Cache-Control"));
      }
   }

   public static HttpURLConnection recoverMethod_a_e2deae62(Context var0, String var1) throws java.io.IOException, java.net.MalformedURLException {
      URL var2 = new URL(var1);
      return (HttpURLConnection)var2.openConnection();
   }

   public static byte[] recoverMethod_a_b328ee5f(InputStream var0) throws java.io.IOException {
      return RecoverType9b4c5af2.recoverMethod_b_b328ee5f(var0);
   }
}
