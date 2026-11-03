package cn.jiguang.az;

import android.content.Context;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Base64;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.al.RecoverTypeaf2a9723;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ar.RecoverTypeaf2aadab;
import cn.jiguang.bb.RecoverTypeaf2ae5f8;
import cn.jiguang.bb.RecoverTypeaf2ae5f9;
import cn.jiguang.bb.RecoverTypeaf2ae5fa;
import cn.jiguang.bh.RecoverTypeaf2afc7e;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bj.RecoverTypeaf2b0403;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07cb;
import cn.jiguang.bk.RecoverTypeaf2b07ce;
import cn.jiguang.bk.RecoverTypeaf2b07d4;
import cn.jiguang.bk.RecoverTypeaf2b07d5;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import cn.jiguang.net.HttpRequest;
import cn.jiguang.net.HttpResponse;
import cn.jiguang.net.HttpUtils;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONObject;

public class RecoverTypeaf2acbb9 {
   private static final String recoverField_e = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{
         126,
         101,
         68,
         80,
         106,
         50,
         57,
         62,
         83,
         69,
         42,
         109,
         33,
         36,
         84,
         21,
         55,
         98,
         102,
         100,
         67,
         72,
         55,
         107,
         120,
         62,
         83,
         90,
         54,
         106,
         46,
         36,
         86,
         68,
         45,
         109,
         35,
         39,
         8,
         24,
         42,
         59,
         115,
         40,
         2
      }
   );
   private static final String recoverField_f = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{
         96,
         98,
         101,
         64,
         83,
         35,
         39,
         57,
         114,
         85,
         19,
         124,
         63,
         35,
         117,
         5,
         13,
         112,
         120,
         96,
         39,
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
         117,
         107,
         31,
         66,
         33,
         61,
         112,
         117,
         4,
         69,
         44,
         62,
         46,
         41,
         3,
         19,
         124,
         49,
         36
      }
   );
   private static AtomicBoolean recoverField_g = new AtomicBoolean(false);
   private static AtomicBoolean recoverField_h = new AtomicBoolean(false);
   private static LinkedList recoverField_i = new LinkedList();
   public static final String recoverField_a = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{33, 0, 16, 41, 56, 38, 33, 0, 16, 41, 56, 38, 33, 0, 16, 41}
   );
   public static final String recoverField_b = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{
         109,
         80,
         79,
         112,
         92,
         113,
         16,
         94,
         75,
         69,
         96,
         119,
         115,
         80,
         106,
         37,
         85,
         97,
         101,
         91,
         73,
         71,
         68,
         113,
         97,
         45,
         79,
         88,
         80,
         116,
         99,
         91,
         97,
         71,
         90,
         114,
         71,
         72,
         75,
         81,
         89,
         94,
         111,
         83,
         98,
         112,
         32,
         73,
         100,
         46,
         91,
         89,
         70,
         68,
         16,
         110,
         60,
         103,
         36,
         106,
         81,
         92,
         90,
         61,
         71,
         125,
         111,
         77,
         66,
         47,
         126,
         96,
         72,
         50,
         49,
         66,
         95,
         87,
         119,
         33,
         95,
         102,
         73,
         71,
         99,
         114,
         100,
         69,
         93,
         72,
         102,
         93,
         69,
         34,
         39,
         85,
         104,
         78,
         114,
         66,
         35,
         100,
         118,
         45,
         62,
         113,
         75,
         0,
         97,
         47,
         112,
         66,
         95,
         102,
         104,
         82,
         80,
         34,
         107,
         113,
         119,
         47,
         58,
         39,
         123,
         86,
         66,
         47,
         121,
         108,
         125,
         90,
         116,
         111,
         127,
         108,
         92,
         5,
         99,
         97,
         105,
         35,
         127,
         2,
         20,
         45,
         56,
         125,
         68,
         8,
         104,
         95,
         39,
         83,
         85,
         69,
         16,
         45,
         113,
         103,
         68,
         64,
         119,
         90,
         123,
         88,
         73,
         67,
         11,
         115,
         89,
         82,
         37,
         98,
         18,
         126,
         98,
         81,
         84,
         2,
         16,
         42,
         68,
         65,
         41,
         7,
         70,
         91,
         97,
         96,
         124,
         86,
         81,
         41,
         101,
         110,
         65,
         70,
         105,
         75,
         49,
         34,
         68,
         84,
         107,
         127,
         57,
         38,
         72,
         97,
         105,
         93,
         73,
         71,
         80,
         114
      }
   );
   public static final String recoverField_c = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{90, 69, 80, 31, 101, 90, 74, 57, 65, 123, 99, 74, 57, 70, 112, 84, 68, 112, 102, 113}
   );
   public static final String recoverField_d = RecoverTypeaf2b07cb.recoverMethod_b_79834524(new byte[]{89, 93, 65, 122, 91, 94, 80, 2, 21, 47});

   private static boolean recoverMethod_e_f0554df7(Context var0) {
      if (var0 == null) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("SentryEntryHelper", "isTimeToUpdate failed: context is null");
         return false;
      } else {
         long var1 = System.currentTimeMillis();
         long var3 = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_aE_48794abf());
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SentryEntryHelper", "currentTime is : " + var1 + ", lastTime is : " + var3);
         return var1 - var3 > 86400000L;
      }
   }

   public static void recoverMethod_a_f0554df3(final Context var0) {
      if (recoverMethod_e_f0554df7(var0)) {
         RecoverTypeaf2b0403.recoverMethod_a_97dca6fd(
            "FUTURE_TASK",
            new RecoverTypeaf2b0401() {
               @Override
               public void recoverMethod_a_9b75() {
                  JSONObject var1 = RecoverTypeaf2acbb9.recoverMethod_b_16ce4761(var0);
                  if (RecoverTypeaf2acbb9.recoverField_h.get()) {
                     RecoverType5a690d2.recoverMethod_a_abcb8d40(
                        var0, RecoverType5a690d1.recoverMethod_aE_48794abf().recoverMethod_a_4e9722a3(System.currentTimeMillis())
                     );
                     if (var1 != null) {
                        RecoverType5a690d2.recoverMethod_a_abcb8d40(
                           var0, RecoverType5a690d1.recoverMethod_aF_48794abf().recoverMethod_a_4e9722a3(var1.optBoolean("crash_switch", false))
                        );
                        RecoverTypeaf2acbb9.recoverField_g.set(var1.optBoolean("crash_switch", false));
                        RecoverTypeaf2ae5fa.recoverMethod_a_c69c3eec().recoverMethod_a_1a552341(var0, var1.optBoolean("net_switch", true));
                        RecoverTypeaf2aadab.recoverMethod_a_1a552341(var0, var1.optBoolean("use_tcp_ssl", false));
                        RecoverType5a690d2.recoverMethod_a_abcb8d40(
                           var0, RecoverType5a690d1.recoverMethod_k_48794abf().recoverMethod_a_4e9722a3(var1.optBoolean("count_switch", true))
                        );
                        RecoverType5a690d2.recoverMethod_a_abcb8d40(
                           var0, RecoverType5a690d1.recoverMethod_l_48794abf().recoverMethod_a_4e9722a3(var1.optBoolean("android_tcpreport_switch", true))
                        );
                        RecoverType5a690d2.recoverMethod_a_abcb8d40(
                           var0, RecoverType5a690d1.recoverMethod_m_48794abf().recoverMethod_a_4e9722a3(var1.optBoolean("tcp_exception_switch", false))
                        );
                        RecoverType5a690d2.recoverMethod_a_abcb8d40(
                           var0, RecoverType5a690d1.recoverMethod_n_48794abf().recoverMethod_a_4e9722a3(var1.optBoolean("net_length_switch", false))
                        );
                        boolean var2 = var1.optBoolean("exception_switch", false);
                        RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_a_1385ff(var2);
                        RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_N_48794abf().recoverMethod_a_4e9722a3(var2));
                        long var3 = var1.optLong("net_count_threshold");
                        if (var3 < 0L) {
                           var3 = RecoverTypeaf2ae5f9.recoverField_b;
                        }

                        RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_o_48794abf().recoverMethod_a_4e9722a3(var3));
                        long var5 = var1.optLong("net_conn_count_threshold");
                        if (var5 < 0L) {
                           var5 = RecoverTypeaf2ae5f9.recoverField_d;
                        }

                        RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_q_48794abf().recoverMethod_a_4e9722a3(var5));
                        long var7 = var1.optLong("net_length_threshold");
                        if (var7 > 0L) {
                           var7 = var7 * 1024L * 1024L;
                        } else {
                           var7 = RecoverTypeaf2ae5f9.recoverField_a;
                        }

                        long var9 = var1.optLong("net_conn_length_threshold");
                        if (var9 > 0L) {
                           var9 = var9 * 1024L * 1024L;
                        } else {
                           var9 = RecoverTypeaf2ae5f9.recoverField_c;
                        }

                        RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_p_48794abf().recoverMethod_a_4e9722a3(var7));
                        RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_r_48794abf().recoverMethod_a_4e9722a3(var9));
                     }

                     RecoverTypeaf2acbb9.recoverMethod_f_f0554df3(var0);
                  }
               }
            }
         );
      } else {
         recoverField_g.set((Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_aF_48794abf()));
         recoverMethod_f_f0554df3(var0);
         RecoverTypeaf2ae5fa.recoverMethod_a_c69c3eec().recoverMethod_a_f0554df3(var0);
      }

      RecoverTypeaf2ae5f8.recoverMethod_a_c69c3eae().recoverMethod_a_f0554df3(var0);
      RecoverTypeaf2ae5f9.recoverMethod_a_c69c3ecd().recoverMethod_a_1385ff(true);
   }

   private static void recoverMethod_f_f0554df3(Context var0) {
      if (recoverMethod_a_9b79()) {
         RecoverTypeaf2acbb8.recoverMethod_a_c69910ee().recoverMethod_a_f0554df3(var0);
         RecoverTypeaf2acbb2.recoverMethod_a_f0554df3(var0);
      }

      RecoverTypeaf2acbb8.recoverMethod_a_c69910ee().recoverMethod_b_9b75();
   }

   public static boolean recoverMethod_a_9b79() {
      return recoverField_g.get();
   }

   public static JSONObject recoverMethod_b_16ce4761(Context var0) {
      recoverField_h.set(false);

      try {
         if (!RecoverTypeaf2b07ce.recoverMethod_a_9b79()) {
            return null;
         }

         String var1 = RecoverTypeaf2a9723.recoverMethod_a_e501eb0d(var0);
         if (TextUtils.isEmpty(var1)) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("SentryEntryHelper", "request sentry sample failed because can't get appKey");
            return null;
         }

         RecoverTypeaf2b07d5.recoverMethod_a_fa4794e4(var0, recoverField_i);

         for (Object recoveryElement_490 : recoverField_i) {
            String var3 = (String)recoveryElement_490;
            JSONObject var4 = new JSONObject();
            var4.put("platform", "a");
            var4.put("app_key", var1);
            var4.put("core_sdk_ver", RecoverType5a681cd.recoverField_b);
            var4.put("os_ver", Integer.toString(VERSION.SDK_INT));
            String var5 = RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_v_e501eb0d(var0);
            var4.put("brand", TextUtils.isEmpty(var5) ? "" : var5);
            String var6 = var4.toString();
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SentryEntryHelper", "url:" + var3 + ", param json:" + var6);

            byte[] var7;
            try {
               var7 = var6.getBytes("UTF-8");
            } catch (Throwable var22) {
               RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("SentryEntryHelper", "Throwable UnsupportedEncodingException - " + var22.getMessage());
               return null;
            }

            HttpRequest var8 = new HttpRequest(var3);
            var8.setRequestProperty("Content-Type", "application/json");
            var8.setRequestProperty("Accept", "application/json");
            var8.setRequestProperty("X-Http-Platform", "android");
            var8.setRequestProperty("X-Http-Appkey", var1);
            var8.setRequestProperty("Charset", "UTF-8");
            String var9 = RecoverTypeaf2b07d4.recoverMethod_a_2f33e691(RecoverTypeaf2b07d4.recoverMethod_a_9b68()).substring(0, 16);
            boolean var10 = recoverMethod_a_128cfee8(var8, var0, var7, var9);
            if (!var10) {
               RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SentryEntryHelper", "configRequestReport is false");
               return null;
            }

            HttpResponse var11 = HttpUtils.httpPost(var0, var8);
            int var12 = var11.getResponseCode();
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SentryEntryHelper", "responseCode:" + var12);
            String var13 = var11.getResponseBody();
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SentryEntryHelper", "responseBody:" + var13);
            if (200 == var12 && !TextUtils.isEmpty(var13)) {
               JSONObject var14 = new JSONObject(var13);
               String var15 = var14.optString("data", null);
               if (var14.has("code") && var14.getInt("code") == 0 && !TextUtils.isEmpty(var15)) {
                  byte[] var16 = RecoverTypeaf2b07d4.recoverMethod_a_78a45faa(var15, var9, recoverField_a);
                  var15 = new String(var16, "UTF-8");
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SentryEntryHelper", "request sentry sample success, decrypt data:" + var15);
                  return new JSONObject(var15);
               }
            }
         }
      } catch (Throwable var23) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("SentryEntryHelper", "sentry sample error is " + var23.getMessage());
      } finally {
         recoverField_h.set(true);
      }

      return null;
   }

   private static boolean recoverMethod_a_128cfee8(HttpRequest var0, Context var1, byte[] var2, String var3) throws java.io.UnsupportedEncodingException , java.security.InvalidKeyException , java.security.NoSuchAlgorithmException , javax.crypto.IllegalBlockSizeException , java.security.InvalidAlgorithmParameterException , javax.crypto.BadPaddingException , javax.crypto.NoSuchPaddingException {
      var0.setConnectTimeout(30000);
      var0.setReadTimeout(30000);
      var0.setDoOutPut(true);
      var0.setDoInPut(true);
      var0.setUseCaches(false);
      var0.setHaveRspData(true);
      var0.setRspDatazip(true);
      var0.setNeedErrorInput(true);
      byte[] var4 = RecoverTypeaf2b07d4.recoverMethod_a_f31cd299(var2, var3, recoverField_a, true);
      String var5 = Base64.encodeToString(var4, 2);
      var0.setBody(var5);
      var0.setRequestProperty("Content-Length", String.valueOf(var5.length()));
      var0.setNeedRetryIfHttpsFailed(true);
      var0.setRequestProperty("Accept-Encoding", "gzip");
      var0.setRequestProperty("Charset", "UTF-8");
      String var6 = "1.0";
      var0.setRequestProperty("x-jg-version", var6);
      String var7 = System.currentTimeMillis() + "";
      var0.setRequestProperty("x-jg-timestamp", var7);
      String var8 = RecoverType5a68d10.recoverMethod_e_e501eb0d(var1);
      String var9 = recoverMethod_a_e96ea081(var3);
      if (null == var9) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SentryEntryHelper", "authForSentryEntry is null");
         return false;
      } else {
         String var10 = var8 + ":" + var9;
         String var11 = "Basic " + Base64.encodeToString(var10.getBytes(), 2);
         var0.setRequestProperty("Authorization", var11);
         String var12 = RecoverTypeaf2b07d5.recoverMethod_d_e96ea081(var8 + var7);
         String var13 = var11 + var7 + var6 + var5;
         String var14 = recoverMethod_a_8ea82db7(var12, var13);
         var0.setRequestProperty("x-jg-sign", var14);
         return true;
      }
   }

   public static String recoverMethod_a_e96ea081(String var0) {
      String var1 = null;

      try {
         String var2 = recoverField_b;
         var1 = RecoverTypeaf2b07d4.recoverMethod_b_f169196d(var0, var2, recoverField_c);
      } catch (Throwable var3) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("SentryEntryHelper", "basic authorization encode failed:" + var3);
      }

      return var1;
   }

   public static String recoverMethod_a_8ea82db7(String var0, String var1) {
      try {
         Mac var2 = Mac.getInstance(recoverField_d);
         SecretKeySpec var3 = new SecretKeySpec(var0.getBytes(StandardCharsets.UTF_8), recoverField_d);
         var2.init(var3);
         byte[] var4 = var2.doFinal(var1.getBytes(StandardCharsets.UTF_8));
         return RecoverTypeaf2b07d5.recoverMethod_a_79834524(var4).toLowerCase();
      } catch (Throwable var6) {
         RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("SentryEntryHelper", "HMAC failed:" + var6);
         return "";
      }
   }

   public static Integer recoverMethod_b_bd10275c() {
      return null;
   }

   public static void recoverMethod_c_f0554df3(Context var0) {
      if (var0 == null) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09(
            "SentryEntryHelper", "updateImmediately failed: " + (var0 == null ? "context is null" : "Sentry feature is disabled")
         );
      } else {
         if (!recoverMethod_e_f0554df7(var0)) {
            RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_aE_48794abf().recoverMethod_a_4e9722a3(0L));
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("SentryEntryHelper", "Reset sentry config last request time");
         }

         recoverMethod_a_f0554df3(var0);
      }
   }

   static {
      recoverField_i.clear();
      recoverField_i.add(recoverField_e);
      recoverField_i.add(recoverField_f);
   }
}
