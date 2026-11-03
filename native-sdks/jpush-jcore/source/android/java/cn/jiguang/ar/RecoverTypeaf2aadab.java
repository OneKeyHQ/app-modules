package cn.jiguang.ar;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import cn.jiguang.al.RecoverTypeaf2a972c;
import cn.jiguang.al.RecoverTypeaf2a972d;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.at.RecoverTypeaf2ab52c;
import cn.jiguang.at.RecoverTypeaf2ab52f;
import cn.jiguang.at.RecoverTypeaf2ab531;
import cn.jiguang.av.RecoverTypeaf2abcad;
import cn.jiguang.av.RecoverTypeaf2abcae;
import cn.jiguang.av.RecoverTypeaf2abcaf;
import cn.jiguang.av.RecoverTypeaf2abcb0;
import cn.jiguang.av.RecoverTypeaf2abcb1;
import cn.jiguang.aw.RecoverTypeaf2ac06e;
import cn.jiguang.ay.RecoverTypeaf2ac7f0;
import cn.jiguang.ay.RecoverTypeaf2ac7f1;
import cn.jiguang.ay.RecoverTypeaf2ac7f2;
import cn.jiguang.ay.RecoverTypeaf2ac7f3;
import cn.jiguang.ay.RecoverTypeaf2ac7f4;
import cn.jiguang.bb.RecoverTypeaf2ae5f9;
import cn.jiguang.bg.RecoverTypeaf2af8c1;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07c4;
import cn.jiguang.bk.RecoverTypeaf2b07c8;
import cn.jiguang.bk.RecoverTypeaf2b07ce;
import cn.jiguang.bk.RecoverTypeaf2b07d4;
import cn.jiguang.bk.RecoverTypeaf2b07d5;
import cn.jiguang.bk.RecoverTypeaf2b07d7;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import cn.jiguang.internal.JConstants;
import cn.jiguang.l.RecoverType5a6ab18;
import cn.jiguang.m.RecoverType5a6aed9;
import cn.jiguang.o.RecoverType5a6b65b;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverTypeaf2aadab {
   private static String recoverField_a;
   private static AtomicBoolean recoverField_b = null;

   private static void recoverMethod_a_1a54e731(Context var0, long var1) {
   }

   public static int recoverMethod_a_112180f3(Context var0, RecoverTypeaf2ac06e var1) {
      long var2 = RecoverType5a68d10.recoverMethod_f_f0554de7(var0);
      String var4 = RecoverTypeaf2b07d5.recoverMethod_j_e96ea081(RecoverType5a68d10.recoverMethod_j_e501eb0d(var0));
      if (var4 == null) {
         var4 = "";
      }

      String var5 = RecoverType5a68d10.recoverMethod_e_e501eb0d(var0);
      RecoverTypeaf2ab52c var6 = RecoverTypeaf2ab52c.recoverMethod_a_c69655fa();
      byte var7 = var6.recoverMethod_b_f0554ddf(var0);
      String var8 = var6.recoverMethod_f_73cf11cb();
      RecoverTypeaf2a9ae9.recoverMethod_h_e925fa09(
         "ConnectingHelper", "Login with - juid:" + var2 + ", appKey:" + var5 + ", sdkVersion:" + var8 + ", pluginPlatformType:" + var7
      );
      short var9 = var6.recoverMethod_c_9b72();
      int var10 = RecoverTypeaf2b07ce.recoverMethod_b_f0554de6(var0);
      String var11 = RecoverTypeaf2b07c8.recoverMethod_a_e501eb0d(var0);
      Object var12 = RecoverType5a6aed9.recoverMethod_a_b00c10b9(var0, "get_loc_info", null);
      JSONObject var13 = new JSONObject();
      if (var12 instanceof Bundle) {
         try {
            Bundle var14 = (Bundle)var12;
            var13.put("lat", var14.getDouble("lat", 200.0));
            var13.put("lng", var14.getDouble("lot", 200.0));
            var13.put("time", var14.getLong("time"));
         } catch (Throwable var31) {
         }
      }

      String var32 = var13.length() > 0 ? var13.toString() : "";
      String var15 = recoverMethod_h_e501eb0d(var0);
      String var16 = RecoverTypeaf2b07d5.recoverMethod_b_e96ea081(String.format(Locale.ENGLISH, var15));
      var16 = var16 == null ? "" : var16.toUpperCase();
      String var17 = RecoverType5a68d10.recoverMethod_d_e501eb0d(var0);
      int var18 = RecoverTypeaf2aa628.recoverField_a;
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
         "ConnectingHelper",
         "login \n juid:"
            + var2
            + "\n flag:"
            + var9
            + "\n netType:"
            + var10
            + "\n deviceId:"
            + var11
            + "\n locInfo:"
            + var32
            + "\n countryCode:"
            + var16
            + "\n accountId:"
            + var17
            + "\n sdkver:"
            + var8
            + "\n userType :"
            + var18
      );
      RecoverTypeaf2a9ae9.recoverMethod_e_e925fa09("ConnectingHelper", "login with cC:" + var16);
      int var19 = RecoverTypeaf2ab531.recoverField_e ? 1 : 0;
      RecoverTypeaf2a9ae9.recoverMethod_e_e925fa09("ConnectingHelper", "login with initPushSdk:" + var19);
      String var20 = recoverMethod_a_8d6404f1(var6.recoverMethod_a_5d5a71dd(var0));
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ConnectingHelper", "login gSdkTypeInfo:" + var20);
      byte[] var21 = RecoverTypeaf2abcae.recoverMethod_a_980fbf3(
         recoverMethod_c_f0554de7(var0), var2, var4, var5, var8, var9, var7, var10, var11, var32, var16, var17, var18, var19, var20
      );
      RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("ConnectingHelper", "pluginPlatformType:0b" + Integer.toBinaryString(var7 & 255));
      byte[] var22 = RecoverTypeaf2abcae.recoverMethod_a_d9b22e23(var0, var21);
      if (var22 != null && var22.length >= 1) {
         int var23 = var1.recoverMethod_a_25e06ef(var22);
         if (var23 != 0) {
            RecoverTypeaf2aadab.Member8f35f3a8.recoverMethod_a_3f52113a(var0, 1101, "Login send failed");
            return -2;
         }

         RecoverTypeaf2ae5f9.recoverMethod_a_c69c3ecd().recoverMethod_b_3b99bddb(var1.recoverField_h + ":" + var1.recoverField_i, "login", var22.length);

         ByteBuffer var24;
         try {
            var24 = var1.recoverMethod_a_c72d506f(20000);
         } catch (RecoverTypeaf2ab52f var30) {
            RecoverTypeaf2a9ae9.recoverMethod_k_e925fa09("ConnectingHelper", "Login failed - recv msg failed wit error:" + var30);
            RecoverTypeaf2aadab.Member8f35f3a8.recoverMethod_a_3f52113a(var0, 1102, "Login recv failed");
            return -3;
         }

         if (null == var24) {
            RecoverTypeaf2a9ae9.recoverMethod_k_e925fa09("ConnectingHelper", "Login failed - recv buffer null");
            RecoverTypeaf2aadab.Member8f35f3a8.recoverMethod_a_3f52113a(var0, 1105, "Login failed - recv buffer null");
            return -6;
         }

         Pair var25 = RecoverTypeaf2abcad.recoverMethod_a_d94c1a8e(var0, var24.array(), "");
         if (var25 != null && var25.first != null && var25.second != null && ((RecoverTypeaf2abcaf)var25.first).recoverField_c == 1) {
            RecoverTypeaf2ae5f9.recoverMethod_a_c69c3ecd()
               .recoverMethod_c_3b99bddb(var1.recoverField_h + ":" + var1.recoverField_i, "login", var24.array().length);
            RecoverTypeaf2abcb0 var26 = new RecoverTypeaf2abcb0((RecoverTypeaf2abcaf)var25.first, (ByteBuffer)var25.second);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ConnectingHelper", var26.toString());
            int var27 = var26.recoverField_a;
            RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_G_48794abf().recoverMethod_a_4e9722a3(var26.recoverField_d));
            if (var27 == 0) {
               JConstants.tcpSessionId = var26.recoverField_b;
               RecoverTypeaf2af8c1.recoverMethod_b_9b79();
               long var28 = var26.recoverField_c * 1000L;
               RecoverTypeaf2aa628.recoverMethod_a_1a54e731(var0, var28);
               RecoverTypeaf2aa628.recoverMethod_a_1a54e370(var0, var26.recoverField_d);
               RecoverTypeaf2a9ae9.recoverMethod_g_e925fa09("ConnectingHelper", "Login succeed - sid:" + JConstants.tcpSessionId + ", serverTime;" + var28);
               RecoverTypeaf2a9ae9.recoverMethod_e_e925fa09("ConnectingHelper", "Login  success regID:" + RecoverType5a68d10.recoverMethod_b_e501eb0d(var0));
               recoverMethod_a_1a54e731(var0, var2);
            } else {
               RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09(
                  "ConnectingHelper", "Login failed with server error - code:" + RecoverTypeaf2ac7f3.recoverMethod_a_47921032(var27)
               );
               if (10000 == var27) {
                  RecoverTypeaf2aadab.Member8f35f3a8.recoverMethod_a_3f52113a(var0, 1104, "Login err code bytebuffer parse");
               }
            }

            return var27;
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_k_e925fa09("ConnectingHelper", "Login failed - can't parse a Login Response");
            RecoverTypeaf2aadab.Member8f35f3a8.recoverMethod_a_3f52113a(var0, 1103, "Login failed - can't parse a Login Response");
            RecoverTypeaf2ae5f9.recoverMethod_a_c69c3ecd()
               .recoverMethod_c_3b99bddb(var1.recoverField_h + ":" + var1.recoverField_i, "unknown_login", var24.array().length);
            return -4;
         }
      } else {
         RecoverTypeaf2aadab.Member8f35f3a8.recoverMethod_a_3f52113a(var0, 1100, "Login failed - encrytor reg info failed");
         return -1;
      }
   }

   public static int recoverMethod_b_112180f3(Context var0, RecoverTypeaf2ac06e var1) {
      String var2 = recoverMethod_e_e501eb0d(var0);
      String var3 = RecoverTypeaf2ac7f0.recoverMethod_a_5ee49234(var0).recoverField_a;
      String var4 = recoverMethod_f_e501eb0d(var0);
      String var5 = recoverMethod_g_e501eb0d(var0);
      long var6 = RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_b_9b72();
      String var8 = RecoverType5a68d10.recoverMethod_d_e501eb0d(var0);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
         "ConnectingHelper",
         "Register with: \n key:"
            + var2
            + "\n apkVersion:"
            + var3
            + "\n clientInfo:"
            + var4
            + "\n extKey:"
            + var5
            + "\n reg business:"
            + var6
            + "\n accountId:"
            + var8
      );
      byte[] var9 = RecoverTypeaf2abcae.recoverMethod_a_3501eca6(recoverMethod_c_f0554de7(var0), var2, var3, var4, var5, var6, var8);
      byte[] var10 = RecoverTypeaf2abcae.recoverMethod_a_d9b22e23(var0, var9);
      RecoverTypeaf2ae5f9.recoverMethod_a_c69c3ecd().recoverMethod_b_3b99bddb(var1.recoverField_h + ":" + var1.recoverField_i, "register", var10.length);
      if (var10 == null) {
         RecoverTypeaf2a9ae9.recoverMethod_k_e925fa09("ConnectingHelper", "Register failed - encrytor reg info failed");
         RecoverTypeaf2aadab.Member8f35f3a8.recoverMethod_a_3f52113a(var0, 1000, "Register failed - encrytor reg info failed");
         return -1;
      }

      if (var1.recoverMethod_a_25e06ef(var10) != 0) {
         RecoverTypeaf2a9ae9.recoverMethod_k_e925fa09("ConnectingHelper", "Register failed - send reg info failed");
         RecoverTypeaf2aadab.Member8f35f3a8.recoverMethod_a_3f52113a(var0, 1001, "Register failed - send reg info failed");
         return -2;
      }

      ByteBuffer var11;
      try {
         var11 = var1.recoverMethod_a_c72d506f(20000);
      } catch (RecoverTypeaf2ab52f var20) {
         RecoverTypeaf2a9ae9.recoverMethod_k_e925fa09("ConnectingHelper", "Register failed - recv msg failed with error:" + var20);
         RecoverTypeaf2aadab.Member8f35f3a8.recoverMethod_a_3f52113a(var0, 1002, "Register failed - recv msg failed with error:" + var20);
         return -3;
      }

      if (null == var11) {
         RecoverTypeaf2a9ae9.recoverMethod_k_e925fa09("ConnectingHelper", "Register failed - recv buffer null");
         RecoverTypeaf2aadab.Member8f35f3a8.recoverMethod_a_3f52113a(var0, 1005, "Register failed - recv buffer null");
         return -6;
      }

      Pair var12 = RecoverTypeaf2abcad.recoverMethod_a_d94c1a8e(var0, var11.array(), "");
      if (var12 != null && var12.first != null && var12.second != null && ((RecoverTypeaf2abcaf)var12.first).recoverField_c == 0) {
         RecoverTypeaf2ae5f9.recoverMethod_a_c69c3ecd()
            .recoverMethod_c_3b99bddb(var1.recoverField_h + ":" + var1.recoverField_i, "register", var11.array().length);
         RecoverTypeaf2abcb1 var13 = new RecoverTypeaf2abcb1((RecoverTypeaf2abcaf)var12.first, (ByteBuffer)var12.second);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ConnectingHelper", "register response:" + var13);
         int var14 = var13.recoverField_a;
         RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_ag_48794abf().recoverMethod_a_4e9722a3(var14));
         if (var14 == 0) {
            long var15 = var13.recoverField_b;
            String var17 = var13.recoverField_c;
            String var18 = var13.recoverField_d;
            String var19 = var13.recoverField_e;
            RecoverTypeaf2a9ae9.recoverMethod_h_e925fa09(
               "ConnectingHelper", "Register succeed - juid:" + var15 + ", registrationId:" + var18 + ", deviceId:" + var19
            );
            if (RecoverTypeaf2b07d5.recoverMethod_a_aad3b203(var18) || 0L == var15) {
               RecoverTypeaf2a9ae9.recoverMethod_n_e925fa09("ConnectingHelper", "Unexpected: registrationId/juid should not be empty. ");
               return -5;
            }

            RecoverTypeaf2aa628.recoverMethod_a_5ffc00fd(var0, var19);
            RecoverTypeaf2aa628.recoverMethod_a_3b017c5(var0, var15, var17, var18);
            RecoverTypeaf2a972d.recoverMethod_a_148762fb(var0, var19, var15, var17, var18);
            new RecoverTypeaf2ac7f2().recoverMethod_e_80115e7e(recoverField_a).recoverMethod_b_f0554df3(var0);
         } else if (10000 == var14) {
            RecoverTypeaf2aadab.Member8f35f3a8.recoverMethod_a_3f52113a(var0, 1004, "respCode == 10000, err code bytebuffer parse");
         }

         return var14;
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("ConnectingHelper", "Register failed - can't parse a Register Response");
         RecoverTypeaf2aadab.Member8f35f3a8.recoverMethod_a_3f52113a(var0, 1003, "Register failed - can't parse a Register Response");
         RecoverTypeaf2ae5f9.recoverMethod_a_c69c3ecd()
            .recoverMethod_c_3b99bddb(var1.recoverField_h + ":" + var1.recoverField_i, "unknown_register", var11.array().length);
         return -4;
      }
   }

   public static void recoverMethod_a_1a54e370(Context var0, int var1) {
      recoverMethod_a_30483d64(var0, var1, true);
   }

   public static void recoverMethod_a_30483d64(Context var0, int var1, boolean var2) {
      if (var2) {
         String var3 = RecoverTypeaf2ac7f3.recoverMethod_a_47921032(var1);
         RecoverTypeaf2a9ae9.recoverMethod_n_e925fa09("ConnectingHelper", "Register Failed with server error - code:" + var1);
         if (!TextUtils.isEmpty(var3)) {
            RecoverTypeaf2a9ae9.recoverMethod_k_e925fa09("ConnectingHelper", "Local error description: " + var3);
         }

         RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_a_4abe089d(var0, 0, var1, var3);
      }

      String var5 = RecoverType5a68d10.recoverMethod_e_e501eb0d(var0);
      String var4 = "";
      switch (var1) {
         case 11:
            break;
         case 1005:
            var4 = "包名: " + var0.getPackageName() + " 与 AppKey:" + var5 + "不匹配";
            RecoverTypeaf2b07c1.recoverMethod_a_9f8491a6(var0, var4, -1);
            break;
         case 1006:
            var4 = "包名: " + var0.getPackageName() + " 不存在";
            RecoverTypeaf2b07c1.recoverMethod_a_9f8491a6(var0, var4, -1);
            break;
         case 1007:
            RecoverTypeaf2a9ae9.recoverMethod_f_e925fa09("ConnectingHelper", "IMEI is duplicated reported by server. Give up now. ");
            break;
         case 1008:
            var4 = " AppKey:" + var5 + " 是无效的AppKey,请确认与JIGUANG web端的AppKey一致";
            RecoverTypeaf2b07c1.recoverMethod_a_9f8491a6(var0, var4, -1);
            break;
         case 1009:
            var4 = " AppKey:" + var5 + " 非android AppKey";
            RecoverTypeaf2b07c1.recoverMethod_a_9f8491a6(var0, var4, -1);
            break;
         case 1012:
            recoverMethod_a_f0554df3(var0);
            break;
         case 10001:
            var4 = " 未在manifest中配置AppKey";
            RecoverTypeaf2b07c1.recoverMethod_a_9f8491a6(var0, var4, -1);
            break;
         default:
            RecoverTypeaf2a9ae9.recoverMethod_h_e925fa09("ConnectingHelper", "Unhandled server response error code - " + var1);
      }
   }

   public static void recoverMethod_a_f0554df3(Context var0) {
      RecoverType5a690d2.recoverMethod_a_abcb8d40(
         var0,
         RecoverType5a690d1.recoverMethod_aq_48794abf().recoverMethod_a_4e9722a3(null),
         RecoverType5a690d1.recoverMethod_ar_48794abf().recoverMethod_a_4e9722a3(null),
         RecoverType5a690d1.recoverMethod_d_cf96baf5(false).recoverMethod_a_4e9722a3(null),
         RecoverType5a690d1.recoverMethod_d_cf96baf5(true).recoverMethod_a_4e9722a3(null),
         RecoverType5a690d1.recoverMethod_av_48794abf().recoverMethod_a_4e9722a3(null),
         RecoverType5a690d1.recoverMethod_as_48794abf().recoverMethod_a_4e9722a3(null),
         RecoverType5a690d1.recoverMethod_at_48794abf().recoverMethod_a_4e9722a3(null),
         RecoverType5a690d1.recoverMethod_b_cf96baf5(true).recoverMethod_a_4e9722a3(null),
         RecoverType5a690d1.recoverMethod_b_cf96baf5(false).recoverMethod_a_4e9722a3(null),
         RecoverType5a690d1.recoverMethod_c_cf96baf5(true).recoverMethod_a_4e9722a3(null),
         RecoverType5a690d1.recoverMethod_c_cf96baf5(false).recoverMethod_a_4e9722a3(null)
      );
      RecoverType5a690d2.recoverMethod_a_abcb8d40(
         var0,
         RecoverType5a690d1.recoverMethod_B_48794abf().recoverMethod_a_4e9722a3(null),
         RecoverType5a690d1.recoverMethod_C_48794abf().recoverMethod_a_4e9722a3(null),
         RecoverType5a690d1.recoverMethod_E_48794abf().recoverMethod_a_4e9722a3(null)
      );
   }

   public static void recoverMethod_b_f0554df3(Context var0) {
      RecoverType5a690d2.recoverMethod_a_abcb8d40(
         var0,
         RecoverType5a690d1.recoverMethod_c_cf96baf5(true).recoverMethod_a_4e9722a3(null),
         RecoverType5a690d1.recoverMethod_c_cf96baf5(false).recoverMethod_a_4e9722a3(null)
      );
   }

   private static String recoverMethod_e_e501eb0d(Context var0) {
      String var1 = RecoverTypeaf2ac7f4.recoverMethod_a_e501eb0d(var0);
      recoverField_a = var1;
      String var2 = RecoverTypeaf2ac7f0.recoverMethod_a_5ee49234(var0).recoverField_o;
      return recoverMethod_b_8ea82db7(var1, " ")
         + "$$"
         + recoverMethod_b_8ea82db7(var2, " ")
         + "$$"
         + var0.getPackageName()
         + "$$"
         + RecoverType5a68d10.recoverMethod_e_e501eb0d(var0);
   }

   private static String recoverMethod_f_e501eb0d(Context var0) {
      String var1 = RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_e_73cf11cb();
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ConnectingHelper", "regVersion:" + var1);
      RecoverTypeaf2ac7f0 var2 = RecoverTypeaf2ac7f0.recoverMethod_a_5ee49234(var0);
      return recoverMethod_b_8ea82db7(var2.recoverField_b, " ")
         + "$$"
         + recoverMethod_b_8ea82db7(var2.recoverField_c, " ")
         + "$$"
         + recoverMethod_b_8ea82db7(var2.recoverField_d, " ")
         + "$$"
         + recoverMethod_b_8ea82db7(var2.recoverField_e, " ")
         + "$$"
         + recoverMethod_b_8ea82db7(RecoverType5a68d10.recoverMethod_h_e501eb0d(var0), " ")
         + "$$"
         + var1
         + "$$"
         + var2.recoverField_g
         + "$$"
         + var2.recoverField_h
         + "$$"
         + var2.recoverField_l
         + "$$"
         + var2.recoverField_q
         + "&"
         + var2.recoverField_r
         + "$$"
         + RecoverTypeaf2a972d.recoverMethod_a_f0554de6(var0);
   }

   private static String recoverMethod_g_e501eb0d(Context var0) {
      String var1 = "";

      try {
         String var3 = RecoverTypeaf2ac7f0.recoverMethod_a_5ee49234(var0).recoverField_j;
         String var2;
         if ("unknown".equalsIgnoreCase(var3)) {
            var2 = " ";
         } else {
            var2 = var3;
         }

         String var4 = RecoverTypeaf2b07c8.recoverMethod_a_e501eb0d(var0);
         int var5 = RecoverTypeaf2b07c8.recoverField_a;
         String var6 = RecoverTypeaf2ac7f0.recoverMethod_a_5ee49234(var0).recoverField_p;
         String var7 = RecoverTypeaf2ac7f0.recoverMethod_a_5ee49234(var0).recoverField_i;
         String var8 = "";
         var8 = RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_b_9ba16009(var0, "", false);
         String var9 = "";
         String var10 = "";
         String var11 = "";
         String var12 = "";
         String var13 = "";
         JSONObject var14 = RecoverType5a6b65b.recoverMethod_a_16ce4761(var0);
         if (var14 != null) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ConnectingHelper", "parse ids:" + var14.toString());
            var9 = var14.optString("udid", "");
            var10 = var14.optString("joad", "");
            var11 = var14.optString("jvad", "");
            var12 = var14.optString("jaad", "");
            var13 = var14.optString("jgad", "");
         }

         String var19 = "";

         try {
            JSONObject var15 = RecoverType5a6ab18.recoverMethod_e_16ce4761(var0);
            if (var15 != null) {
               var19 = var15.optString("new_cid_all");
            }
         } catch (Throwable var16) {
         }

         var1 = var5
            + "$$"
            + recoverMethod_b_8ea82db7(var4, " ")
            + "$$"
            + recoverMethod_b_8ea82db7(var6, " ")
            + "$$"
            + recoverMethod_b_8ea82db7(var7, " ")
            + "$$"
            + recoverMethod_b_8ea82db7(var8, " ")
            + "$$"
            + recoverMethod_b_8ea82db7(var2, " ")
            + "$$"
            + recoverMethod_b_8ea82db7(var9, " ")
            + "$$"
            + recoverMethod_b_8ea82db7(var10, " ")
            + "$$"
            + recoverMethod_b_8ea82db7(var11, " ")
            + "$$"
            + recoverMethod_b_8ea82db7(var12, " ")
            + "$$"
            + recoverMethod_b_8ea82db7(var13, " ")
            + "$$"
            + recoverMethod_b_8ea82db7(var19, " ");
      } catch (Exception var17) {
      }

      return var1;
   }

   private static String recoverMethod_b_8ea82db7(String var0, String var1) {
      return !RecoverTypeaf2b07d5.recoverMethod_a_aad3b203(var0) ? var0 : var1;
   }

   private static synchronized byte[] recoverMethod_a_5b3aab83(String var0, int var1, byte[] var2, boolean var3, int var4) {
      if (!TextUtils.isEmpty(var0) && var0.length() == 2 && var2 != null && var2.length != 0) {
         RecoverTypeaf2ac7f1 var5 = new RecoverTypeaf2ac7f1(300);
         var5.recoverMethod_b_13462e(0);
         var5.recoverMethod_a_25e06fc(var0.getBytes());
         var5.recoverMethod_a_1349ef(var1);
         var5.recoverMethod_b_13462e(var4);
         var5.recoverMethod_a_25e06fc(var2);
         var5.recoverMethod_b_255f295(var5.recoverMethod_a_9b68(), 0);
         byte var6 = 0;
         if (var3) {
            var6 = (byte)(var6 | 1);
         }

         var6 = (byte)(var6 | 16);
         var5.recoverMethod_a_255f295(var6, 4);
         return var5.recoverMethod_b_12d408();
      } else {
         throw new IllegalArgumentException("flag or body length error");
      }
   }

   public static byte[] recoverMethod_a_6607361d(DatagramSocket var0, DatagramPacket var1) throws java.io.IOException, java.net.SocketException {
      var0.setSoTimeout(6000);
      var0.send(var1);
      byte[] var2 = new byte[1024];
      DatagramPacket var3 = new DatagramPacket(var2, var2.length);
      RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("ConnectingHelper", "udp Receiving...");
      var0.receive(var3);
      byte[] var4 = new byte[var3.getLength()];
      System.arraycopy(var3.getData(), 0, var4, 0, var4.length);
      return var4;
   }

   public static byte[] recoverMethod_a_3b9947f4(String var0, String var1) throws java.io.UnsupportedEncodingException , java.security.InvalidKeyException , java.security.NoSuchAlgorithmException , javax.crypto.IllegalBlockSizeException , java.security.InvalidAlgorithmParameterException , javax.crypto.BadPaddingException , javax.crypto.NoSuchPaddingException {
      byte[] var2 = RecoverTypeaf2b07d5.recoverMethod_i_afa28ebe(var1);
      boolean var3 = true;
      if (var3) {
         try {
            byte[] var4 = RecoverTypeaf2b07d7.recoverMethod_a_4962d961(var2);
            if (var4.length < var2.length) {
               var2 = var4;
            } else {
               var3 = false;
            }
         } catch (IOException var7) {
            var3 = false;
         }
      }

      int var9 = var2.length;
      int var5 = RecoverTypeaf2b07d4.recoverMethod_a_9b68();
      String var6 = RecoverTypeaf2b07d4.recoverMethod_a_2f33e691(var5);
      var2 = RecoverTypeaf2b07d4.recoverMethod_a_f31cd299(var2, var6, var6.substring(0, 16), true);
      return recoverMethod_a_5b3aab83(var0, var5, var2, var3, var9);
   }

   public static byte[] recoverMethod_a_4962d961(byte[] var0) throws RecoverTypeaf2ab52f {
      if (var0 != null && var0.length != 0) {
         int var1;
         long var2;
         byte[] var4;
         try {
            ByteBuffer var5 = ByteBuffer.wrap(var0);
            var5.getShort();
            var5.getShort();
            int var6 = var5.getInt();
            var1 = var6 >>> 24;
            var2 = var6 & 16777215;
            var5.getShort();
            int var7 = var5.remaining();
            var4 = new byte[var7];
            var5.get(var4, 0, var7);
         } catch (Throwable var10) {
            throw new RecoverTypeaf2ab52f(4, "parse head error:" + var10.getMessage());
         }

         if (var2 != 0L) {
            String var11 = RecoverTypeaf2b07d4.recoverMethod_a_2f33e691(var2);

            try {
               var4 = RecoverTypeaf2b07d4.recoverMethod_a_f31cd299(var4, var11, var11.substring(0, 16), false);
               if (var4 == null) {
                  throw new RecoverTypeaf2ab52f(5, "decrypt response error");
               }
            } catch (Exception var9) {
               throw new RecoverTypeaf2ab52f(5, "decrypt response error");
            }
         }

         if ((var1 & 1) == 1) {
            try {
               var4 = RecoverTypeaf2b07d7.recoverMethod_b_4962d961(var4);
            } catch (IOException var8) {
            }
         }

         return var4;
      } else {
         throw new RecoverTypeaf2ab52f(4, "response is empty!");
      }
   }

   public static synchronized long recoverMethod_c_f0554de7(Context var0) {
      short var1 = 10000;
      long var2 = (Long)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_ao_48794abf());
      if (var2 == -1L) {
         var2 = Math.abs(new SecureRandom().nextInt(var1));
      }

      var2 = (var2 % 2L == 0L ? var2 + 1L : var2 + 2L) % var1;
      RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_ao_48794abf().recoverMethod_a_4e9722a3(var2));
      return var2;
   }

   private static String recoverMethod_h_e501eb0d(Context var0) {
      String var1 = JConstants.testCountry;
      if (!TextUtils.isEmpty(var1)) {
         return var1;
      }

      String var2 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_K_48794abf());
      return !TextUtils.isEmpty(var2) ? var2 : RecoverTypeaf2b07c1.recoverMethod_i_e501eb0d(var0);
   }

   public static void recoverMethod_a_1a552341(Context var0, boolean var1) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ConnectingHelper", "setUseTcpSsl state: " + var1);
      RecoverType5a690d2.recoverMethod_a_abcb8d40(var0, RecoverType5a690d1.recoverMethod_j_48794abf().recoverMethod_a_4e9722a3(var1));
   }

   public static void recoverMethod_a_1385ff(boolean var0) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ConnectingHelper", "changeLocalTcpSslState, state: " + var0);
      if (recoverField_b == null) {
         recoverField_b = new AtomicBoolean(var0);
      } else {
         recoverField_b.set(var0);
      }
   }

   public static synchronized boolean recoverMethod_d_f0554df7(Context var0) {
      try {
         if (recoverField_b == null) {
            var0 = null == var0 ? RecoverTypeaf2b07c4.recoverMethod_a_6263c3eb(null) : var0;
            boolean var1 = (Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_j_48794abf());
            if (var1) {
               String var2 = RecoverTypeaf2a972c.recoverMethod_a_73cf11cb();
               var1 = !TextUtils.isEmpty(var2);
            }

            recoverField_b = new AtomicBoolean(var1);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ConnectingHelper", "use tcp ssl state: " + recoverField_b.get());
         }
      } catch (Throwable var3) {
         recoverField_b = new AtomicBoolean(false);
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ConnectingHelper", "use tcp ssl default state: false");
      }

      return recoverField_b.get();
   }

   private static String recoverMethod_a_8d6404f1(JSONArray var0) {
      if (var0 != null && var0.length() != 0) {
         StringBuilder var1 = new StringBuilder();

         for (int var2 = 0; var2 < var0.length(); var2++) {
            if (var2 > 0) {
               var1.append("|");
            }

            var1.append(var0.optInt(var2));
         }

         return var1.toString();
      } else {
         return "";
      }
   }

   public static class Member8f35f3a8 {
      public static void recoverMethod_a_3f52113a(Context var0, int var1, String var2) {
         if (var0 == null) {
            var0 = RecoverTypeaf2b07c4.recoverMethod_a_88bf2b6d();
         }

         if (var0 == null) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ConnectingHelper", "report failed: context is null");
         } else {
            Boolean var3 = (Boolean)RecoverType5a690d2.recoverMethod_a_413ed21(var0, RecoverType5a690d1.recoverMethod_m_48794abf());
            if (var3) {
               try {
                  JSONObject var4 = recoverMethod_a_45d40566(var1, var2);
                  RecoverTypeaf2aa628.recoverMethod_a_d97c5eb5(var0, var4, "tcp_exception");
               } catch (Throwable var5) {
                  RecoverTypeaf2a9ae9.recoverMethod_d_1765c98c("ConnectingHelper", "Failed to report TCP exception", var5);
               }
            }
         }
      }

      private static JSONObject recoverMethod_a_45d40566(int var0, String var1) throws org.json.JSONException {
         JSONObject var2 = new JSONObject();
         var2.put("e_code", var0);
         var2.put("e_msg", var1 != null ? var1 : "");
         return var2;
      }
   }
}
