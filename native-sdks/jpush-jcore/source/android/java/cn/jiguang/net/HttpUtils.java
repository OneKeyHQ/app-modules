package cn.jiguang.net;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bb.RecoverTypeaf2ae5f9;
import cn.jiguang.bb.RecoverTypeaf2ae5fa;
import cn.jiguang.bk.RecoverTypeaf2b07c2;
import cn.jiguang.bk.RecoverTypeaf2b07c6;
import cn.jiguang.bk.RecoverTypeaf2b07cb;
import cn.jiguang.bk.RecoverTypeaf2b07d7;
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

public class HttpUtils {
   public static final String recoverField_a = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{32, 0, 14, 41, 38, 38, 63, 1, 23, 43});

   public static HttpResponse httpGet(Context var0, HttpRequest var1) {
      return httpResponse(var0, var1, false);
   }

   public static HttpResponse httpGet(Context var0, String var1) {
      return httpGet(var0, new HttpRequest(var1));
   }

   public static String httpGetString(Context var0, HttpRequest var1) {
      HttpResponse var2 = httpGet(var0, var1);
      return var2 == null ? null : var2.getResponseBody();
   }

   public static String httpGetString(Context var0, String var1) {
      HttpResponse var2 = httpGet(var0, new HttpRequest(var1));
      return var2 == null ? null : var2.getResponseBody();
   }

   public static void httpGet(Context var0, String var1, HttpUtils.Member22d8192f var2) {
      new HttpUtils.Membera39ab68f(var2, var0).execute(var1);
   }

   public static void httpGet(Context var0, HttpRequest var1, HttpUtils.Member22d8192f var2) {
      new HttpUtils.Membera39ab68e(var0, var2).execute(var1);
   }

   public static HttpResponse httpPost(Context var0, HttpRequest var1) {
      return httpResponse(var0, var1, true, false);
   }

   public static HttpResponse httpResponse(Context var0, HttpRequest var1, boolean var2) {
      return httpResponse(var0, var1, var2, false);
   }

   public static HttpResponse httpResponse(Context var0, HttpRequest var1, boolean var2, boolean var3) {
      return httpResponse(var0, getHttpURLConnectionWithProxy(var1.getUrl()), var1, var2, var3);
   }

   public static HttpResponse httpResponse(Context var0, HttpURLConnection var1, HttpRequest var2, boolean var3, boolean var4) {
      if (var2 == null) {
         return null;
      }

      HttpResponse var5 = new HttpResponse(var2.getUrl());
      if (!RecoverTypeaf2b07c2.recoverMethod_a_9b79()) {
         var5.setResponseCode(3006);
         var5.setResponseBody("user not auth!!!");
         return var5;
      }

      if (!var4 && !RecoverTypeaf2ae5f9.recoverMethod_a_c69c3ecd().recoverMethod_a_f0554df7(var0)) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("HttpUtils", " https sdk limit");
         var5.setResponseCode(3006);
         var5.setResponseBody("sdk limit");
         return var5;
      }

      String var6 = RecoverTypeaf2ae5fa.recoverMethod_a_c69c3eec().recoverMethod_a_bd4fc8df(var0, var2, var3);
      RecoverTypeaf2ae5f9.recoverMethod_a_c69c3ecd().recoverMethod_a_ad38039f(var2, var3);
      InputStream var7 = null;
      InputStream var8 = null;
      OutputStream var9 = null;
      int var10 = -1;

      try {
         if (var1 == null) {
            var1 = getHttpURLConnectionWithProxy(var2.getUrl());
         }

         URL var11 = var1.getURL();
         var10 = var11.getPort();
         RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("HttpUtils", "http url:" + var2.getUrl());
         RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("HttpUtils", "host:" + var11.getHost() + ",port:" + var10);
         recoverMethod_a_57bdf6fe(var2, var1);
         if (var1 instanceof HttpsURLConnection) {
            try {
               if (var2.getSslTrustManager() != null) {
                  SSLContext var12 = SSLContext.getInstance("TLS");
                  TrustManager[] var13 = new TrustManager[]{var2.getSslTrustManager()};
                  var12.init(null, var13, new SecureRandom());
                  if (var12 != null) {
                     ((HttpsURLConnection)var1).setSSLSocketFactory(var12.getSocketFactory());
                  }
               }

               if (var2.getHostnameVerifier() != null) {
                  ((HttpsURLConnection)var1).setHostnameVerifier(var2.getHostnameVerifier());
               } else {
                  ((HttpsURLConnection)var1).setHostnameVerifier(new DefaultHostVerifier(var1.getURL().getHost()));
               }
            } catch (Throwable var27) {
               RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("HttpUtils", "set ssl config error:" + var27.getMessage());
            }
         }

         if (var3 || var4) {
            var1.setRequestMethod(var4 ? "PUT" : "POST");
            var1.setDoOutput(true);
            var1.setDoInput(true);
            var1.setUseCaches(var2.isUseCaches());
            byte[] var34 = var2.getParas();
            if (var34 != null) {
               var9 = var1.getOutputStream();
               var9.write(var34);
            }
         }

         if (!var4 && !RecoverTypeaf2ae5f9.recoverMethod_a_c69c3ecd().recoverMethod_a_f0554df7(var0)) {
            var5.setResponseCode(3006);
            var5.setResponseBody("sdk limit");
            return var5;
         }

         int var35 = var1.getResponseCode();

         try {
            for (String var14 : var1.getHeaderFields().keySet()) {
               String var15 = var1.getHeaderField(var14);
               var5.setResponseHeader(var14, var15);
            }
         } catch (Throwable var28) {
         }

         var5.setResponseCode(var35);
         byte[] var37 = null;

         try {
            var7 = var1.getInputStream();
         } catch (Throwable var26) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("HttpUtils", "get input stream error:" + var26.getMessage());
         }

         if (var2.isHaveRspData() && var7 != null) {
            try {
               var37 = readInputStream(var7);
               if (var37 != null && var2.isRspDatazip()) {
                  var37 = RecoverTypeaf2b07d7.recoverMethod_b_4962d961(var37);
               }
            } catch (Throwable var25) {
            }
         }

         if (var37 == null && var35 != 200 && var2.isNeedErrorInput()) {
            var8 = var1.getErrorStream();
            var37 = readInputStream(var8);
         }

         if (var37 != null) {
            String var38 = new String(var37, "UTF-8");
            var5.setResponseBody(var38);
         }

         recoverMethod_a_9b92e1e2(var1, var5);
      } catch (MalformedURLException var29) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("HttpUtils", "http post  error:" + var29.getMessage());
         var5.setResponseCode(3004);
         var5.setResponseBody("MalformedURLException");
      } catch (IOException var30) {
         var5.setResponseCode(2998);
         var5.setResponseBody("网络错误");
         if (var30 instanceof SocketTimeoutException) {
            var5.setResponseCode(3001);
            var5.setResponseBody("请求超时");
         } else if (var30 instanceof UnknownHostException) {
            var5.setResponseCode(3003);
            var5.setResponseBody("域名无效");
         } else if (var30 instanceof SSLHandshakeException) {
            var5.setResponseCode(3005);
            var5.setResponseBody("SSL失败");
         }

         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("HttpUtils", "http post IOException error:" + var30.getMessage());
      } catch (Throwable var31) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("HttpUtils", "http post Exception error:" + var31);
         var5.setResponseCode(3006);
         var5.setResponseBody("UNKnow execption" + var31.getMessage());
      } finally {
         RecoverTypeaf2b07d7.recoverMethod_a_d17885d6(var7);
         RecoverTypeaf2b07d7.recoverMethod_a_d17885d6(var8);
         RecoverTypeaf2b07d7.recoverMethod_a_d17885d6(var9);
         if (var1 != null) {
            var1.disconnect();
         }
      }

      RecoverTypeaf2ae5fa.recoverMethod_a_c69c3eec().recoverMethod_a_982b89e3(var0, var6, var5);
      RecoverTypeaf2ae5f9.recoverMethod_a_c69c3ecd().recoverMethod_a_6b0b6281(var2, var3, var5);
      return var5;
   }

   public static HttpResponse httpPost(Context var0, String var1) {
      return httpPost(var0, new HttpRequest(var1));
   }

   public static String httpPostString(Context var0, String var1) {
      HttpResponse var2 = httpPost(var0, new HttpRequest(var1));
      return var2 == null ? null : var2.getResponseBody();
   }

   public static String httpPostString(Context var0, String var1, Map var2) {
      HttpResponse var3 = httpPost(var0, new HttpRequest(var1, var2));
      return var3 == null ? null : var3.getResponseBody();
   }

   public static String getUrlWithParas(String var0, Map var1) {
      StringBuilder var2 = new StringBuilder(TextUtils.isEmpty(var0) ? "" : var0);
      String var3 = joinParas(var1);
      if (!TextUtils.isEmpty(var3)) {
         var2.append("?").append(var3);
      }

      return var2.toString();
   }

   public static String getUrlWithValueEncodeParas(String var0, Map var1) {
      StringBuilder var2 = new StringBuilder(TextUtils.isEmpty(var0) ? "" : var0);
      String var3 = joinParasWithEncodedValue(var1);
      if (!TextUtils.isEmpty(var3)) {
         var2.append("?").append(var3);
      }

      return var2.toString();
   }

   public static String joinParas(Map var0) {
      if (var0 != null && var0.size() != 0) {
         StringBuilder var1 = new StringBuilder();
         Iterator var2 = var0.entrySet().iterator();

         while (var2.hasNext()) {
            Entry var3 = (Entry)var2.next();
            var1.append((String)var3.getKey()).append("=").append((String)var3.getValue());
            if (var2.hasNext()) {
               var1.append("&");
            }
         }

         return var1.toString();
      } else {
         return null;
      }
   }

   public static String joinParasWithEncodedValue(Map var0) {
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

   public static String appendParaToUrl(String var0, String var1, String var2) {
      if (TextUtils.isEmpty(var0)) {
         return var0;
      }

      StringBuilder var3 = new StringBuilder(var0);
      if (!var0.contains("?")) {
         var3.append("?");
      } else {
         var3.append("&");
      }

      return var3.append(var1).append("=").append(var2).toString();
   }

   public static long parseGmtTime(String var0) {
      try {
         return RecoverTypeaf2b07c6.recoverMethod_a_5318097a("EEE, d MMM yyyy HH:mm:ss z").parse(var0).getTime();
      } catch (Exception var2) {
         return -1L;
      }
   }

   private static void recoverMethod_a_57bdf6fe(HttpRequest var0, HttpURLConnection var1) {
      if (var0 != null && var1 != null) {
         setURLConnection(var0.getRequestProperties(), var1);
         if (var0.getConnectTimeout() >= 0) {
            var1.setConnectTimeout(var0.getConnectTimeout());
         }

         if (var0.getReadTimeout() >= 0) {
            var1.setReadTimeout(var0.getReadTimeout());
         }
      }
   }

   public static void setURLConnection(Map var0, HttpURLConnection var1) {
      if (var0 != null && var0.size() != 0 && var1 != null) {
         for (Object recoveryElement_335 : var0.entrySet()) {
            Entry var3 = (Entry)recoveryElement_335;
            if (!TextUtils.isEmpty((CharSequence)var3.getKey())) {
               var1.setRequestProperty((String)var3.getKey(), (String)var3.getValue());
            }
         }
      }
   }

   private static void recoverMethod_a_9b92e1e2(HttpURLConnection var0, HttpResponse var1) throws java.io.IOException {
      if (var1 != null && var0 != null) {
         var1.setResponseCode(var0.getResponseCode());
         var1.setStatusCode(var0.getResponseCode());
         var1.setResponseHeader("expires", var0.getHeaderField("Expires"));
         var1.setResponseHeader("cache-control", var0.getHeaderField("Cache-Control"));
      }
   }

   public static HttpURLConnection getHttpURLConnectionWithProxy(String var0) {
      try {
         URL var1 = new URL(var0);
         return (HttpURLConnection)var1.openConnection();
      } catch (Throwable var2) {
         return null;
      }
   }

   public static byte[] readInputStream(InputStream var0) throws java.io.IOException {
      return RecoverTypeaf2b07d7.recoverMethod_a_b328ee5f(var0);
   }

   public abstract static class Member22d8192f {
      protected void recoverMethod_a_9b75() {
      }

      protected void recoverMethod_a_d6d5a26b(HttpResponse var1) {
      }
   }

   private static class Membera39ab68e extends AsyncTask {
      private HttpUtils.Member22d8192f recoverField_a;
      private Context recoverField_b;

      public Membera39ab68e(Context var1, HttpUtils.Member22d8192f var2) {
         this.recoverField_a = var2;
         this.recoverField_b = var1;
      }

      protected HttpResponse recoverMethod_a_24abd210(HttpRequest... var1) {
         return var1 != null && var1.length != 0 ? HttpUtils.httpGet(this.recoverField_b, var1[0]) : null;
      }

      @Override
      protected void onPreExecute() {
         if (this.recoverField_a != null) {
            this.recoverField_a.recoverMethod_a_9b75();
         }
      }

      protected void recoverMethod_a_d6d5a26b(HttpResponse var1) {
         if (this.recoverField_a != null) {
            this.recoverField_a.recoverMethod_a_d6d5a26b(var1);
         }
      }

      // $VF: synthetic method
      @Override
      protected void onPostExecute(Object var1) {
         this.recoverMethod_a_d6d5a26b((HttpResponse)var1);
      }

      // $VF: synthetic method
      @Override
      protected Object doInBackground(Object[] var1) {
         return this.recoverMethod_a_24abd210((HttpRequest[])var1);
      }
   }

   private static class Membera39ab68f extends AsyncTask {
      private HttpUtils.Member22d8192f recoverField_a;
      private Context recoverField_b;

      public Membera39ab68f(HttpUtils.Member22d8192f var1, Context var2) {
         this.recoverField_a = var1;
         this.recoverField_b = var2;
      }

      protected HttpResponse recoverMethod_a_de3528fa(String... var1) {
         return var1 != null && var1.length != 0 ? HttpUtils.httpGet(this.recoverField_b, var1[0]) : null;
      }

      @Override
      protected void onPreExecute() {
         if (this.recoverField_a != null) {
            this.recoverField_a.recoverMethod_a_9b75();
         }
      }

      protected void recoverMethod_a_d6d5a26b(HttpResponse var1) {
         if (this.recoverField_a != null) {
            this.recoverField_a.recoverMethod_a_d6d5a26b(var1);
         }
      }

      // $VF: synthetic method
      @Override
      protected void onPostExecute(Object var1) {
         this.recoverMethod_a_d6d5a26b((HttpResponse)var1);
      }

      // $VF: synthetic method
      @Override
      protected Object doInBackground(Object[] var1) {
         return this.recoverMethod_a_de3528fa((String[])var1);
      }
   }
}
