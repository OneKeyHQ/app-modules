package cn.jpush.android.ad;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import cn.jpush.android.api.JThirdPlatFormInterface;
import cn.jpush.android.cache.RecoverTypea2146d45;
import cn.jpush.android.d.RecoverType2e4cce2a;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.helper.RecoverType8b455831;
import cn.jpush.android.k.RecoverType2e4ce86e;
import cn.jpush.android.x.RecoverType2e4d193c;
import java.util.Arrays;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverType9b4c5aeb {
   private Context recoverField_a;
   private final String recoverField_b = "JPUSH_JUMP";
   private static String recoverField_c;
   private static final byte[] recoverField_d = new byte[]{
      84,
      85,
      108,
      72,
      90,
      107,
      49,
      66,
      77,
      69,
      100,
      68,
      85,
      51,
      70,
      72,
      85,
      48,
      108,
      105,
      77,
      48,
      82,
      82,
      82,
      85,
      74,
      66,
      85,
      86,
      86,
      66,
      81,
      84,
      82,
      72,
      84,
      107,
      70,
      69,
      81,
      48,
      74,
      112,
      85,
      85,
      116,
      67,
      90,
      49,
      70,
      68,
      99,
      108,
      108,
      108,
      81,
      122,
      78,
      81,
      90,
      69,
      120,
      122,
      89,
      88,
      90,
      77,
      90,
      106,
      104,
      69,
      90,
      84,
      108,
      71,
      89,
      122,
      81,
      51,
      78,
      50,
      104,
      78,
      83,
      119,
      112,
      110,
      100,
      108,
      90,
      68,
      78,
      72,
      104,
      85,
      99,
      50,
      53,
      89,
      87,
      71,
      86,
      78,
      99,
      85,
      104,
      111,
      87,
      85,
      120,
      68,
      98,
      88,
      108,
      77,
      82,
      72,
      85,
      51,
      81,
      122,
      90,
      82,
      78,
      70,
      107,
      120,
      77,
      71,
      82,
      49,
      100,
      106,
      100,
      76,
      98,
      108,
      112,
      105,
      99,
      105,
      116,
      110,
      98,
      51,
      100,
      90,
      82,
      87,
      81,
      53,
      85,
      122,
      82,
      78,
      77,
      48,
      108,
      72,
      98,
      122,
      100,
      110,
      90,
      106,
      66,
      118,
      99,
      85,
      112,
      117,
      67,
      107,
      120,
      77,
      100,
      107,
      90,
      54,
      83,
      69,
      116,
      54,
      90,
      70,
      74,
      106,
      99,
      87,
      115,
      49,
      99,
      88,
      112,
      117,
      89,
      87,
      57,
      118,
      90,
      50,
      120,
      68,
      81,
      122,
      65,
      48,
      82,
      109,
      104,
      74,
      97,
      108,
      74,
      80,
      87,
      72,
      104,
      50,
      81,
      108,
      90,
      54,
      79,
      71,
      100,
      66,
      98,
      51,
      90,
      104,
      90,
      107,
      53,
      90,
      99,
      109,
      120,
      76,
      78,
      109,
      104,
      105,
      90,
      85,
      82,
      79,
      84,
      107,
      49,
      78,
      84,
      50,
      57,
      73,
      83,
      108,
      65,
      75,
      82,
      50,
      100,
      112,
      85,
      71,
      82,
      87,
      97,
      50,
      70,
      109,
      89,
      108,
      82,
      107,
      81,
      108,
      78,
      89,
      97,
      110,
      112,
      51,
      83,
      85,
      82,
      66,
      85,
      85,
      70,
      67
   };
   private static final String recoverField_e = RecoverType9b4c5ae8.recoverMethod_b_79834524(
      new byte[]{68, 66, 113, 15, 87, 103, 120, 116, 31, 112, 82, 75, 69, 32, 96, 65, 125, 108, 127, 127, 87}
   );
   private final String recoverField_f = "msg_id";
   private final String recoverField_g = "rom_type";
   private final String recoverField_h = "n_title";
   private final String recoverField_i = "n_content";
   private final String recoverField_j = "m_content";
   private final String recoverField_k = "n_extras";
   private final String recoverField_l = "n_intent";
   private final String recoverField_m = "n_url";
   private final String recoverField_n = "n_ssl";
   private final String recoverField_o = "n_fail_handle_type";
   private final String recoverField_p = "n_fail_handle_url";
   private final String recoverField_q = "n_package_name";
   private final String recoverField_r = "notification_id";
   private final String recoverField_s = "_j_data_";
   private final String recoverField_t = "n_type";
   private final int recoverField_u = 1;
   private final int recoverField_v = 2;
   private final int recoverField_w = 3;
   private final int recoverField_x = 4;
   private final int recoverField_y = 1;
   private final int recoverField_z = 2;
   private final int recoverField_A = 3;
   private final int recoverField_B = 4;
   private int recoverField_C = 0;

   private RecoverType9b4c5aeb() {
   }

   public static RecoverType9b4c5aeb recoverMethod_a_d8c46bdb() {
      return RecoverType9b4c5aeb.Memberf9a150e8.recoverField_a;
   }

   public void recoverMethod_a_f0554df3(Context var1) {
      Logger.recoverMethod_d_e925fa09("JumpManager", " init config");
      this.recoverField_a = var1;
      this.recoverMethod_c_f0554df3(var1);
   }

   public void recoverMethod_a_78545e75(Context var1, RecoverType2e4cce2a var2, Intent var3) {
      this.recoverMethod_b_f0554df3(var1);

      try {
         Logger.recoverMethod_d_e925fa09("JumpManager", "JG user click notification");
         if (var2.recoverField_m != 1) {
            if (var2.recoverField_ai == 0) {
               RecoverType8b455831.recoverMethod_a_36ca9ac3(var2.recoverField_d, 1000L, var1);
            } else {
               RecoverType8b455831.recoverMethod_a_4ef42769(var1, var2.recoverField_d, var2.recoverField_ai, null);
            }
         }

         RecoverType2e4ce86e.recoverMethod_a_ae3b7eb8(var1, var2);
         this.recoverMethod_a_ec36941(var2, var3, false);
      } catch (Throwable var5) {
         Logger.recoverMethod_w_e925fa09("JumpManager", "handleNotificationClick  error : " + var5.getMessage());
      }
   }

   public boolean recoverMethod_a_c4d2ecb4(Context var1, Intent var2) {
      if ("cn.jpush.android.intent.NOTIFICATION_OPENED_CUSTOM_BUTTON".equals(var2.getAction())) {
         Logger.recoverMethod_d_e925fa09("JumpManager", "handleNotificationIntent: parsing custom button data");
         RecoverType2e4cce2a.Memberce51f067 var3 = RecoverType2e4d193c.recoverMethod_c_c392554e(var1, var2);
         Logger.recoverMethod_d_e925fa09("JumpManager", "handleNotificationIntent: customButton = " + (var3 != null ? var3.toString() : "null"));
         if (null != var3) {
            recoverMethod_a_d8c46bdb().recoverMethod_a_5810f98(var1, var3, var2);
         }

         return true;
      } else {
         return this.recoverMethod_b_c4d2ecb4(var1, var2);
      }
   }

   public boolean recoverMethod_b_c4d2ecb4(Context var1, Intent var2) {
      this.recoverMethod_b_f0554df3(var1);
      Logger.recoverMethod_d_e925fa09("JumpManager", "third user Custom Button click notification");

      try {
         String var3 = this.recoverMethod_b_a27c4aae(var2);
         Logger.recoverMethod_d_e925fa09("JumpManager", "handleThirdNotificationCustomButtonClick data is= " + var3);
         if (TextUtils.isEmpty(var3)) {
            return false;
         }

         RecoverType2e4cce2a.Memberce51f067 var4 = RecoverType2e4cce2a.Memberce51f067.recoverMethod_a_14ff071f(var3);
         Logger.recoverMethod_d_e925fa09("JumpManager", "handleThirdNotificationCustomButtonClick: customButton = " + (var4 != null ? var4.toString() : "null"));
         if (null != var4) {
            recoverMethod_a_d8c46bdb().recoverMethod_a_5810f98(var1, var4, var2);
         }

         return true;
      } catch (Throwable var5) {
         Logger.recoverMethod_w_e925fa09("JumpManager", "handleThirdNotificationCustomButtonClick  error : " + var5.getMessage());
         return false;
      }
   }

   private void recoverMethod_a_5810f98(Context var1, RecoverType2e4cce2a.Memberce51f067 var2, Intent var3) {
      this.recoverMethod_b_f0554df3(var1);

      try {
         Logger.recoverMethod_d_e925fa09("JumpManager", "Handle custom button click");
         if (var2 == null) {
            Logger.recoverMethod_w_e925fa09("JumpManager", "Parse custom button failed");
            return;
         }

         if (var2.recoverField_b == 0) {
            RecoverType8b455831.recoverMethod_a_36ca9ac3(var2.recoverField_a, 1000L, var1);
         } else {
            RecoverType8b455831.recoverMethod_a_4ef42769(var1, var2.recoverField_a, var2.recoverField_b, null);
         }

         Logger.recoverMethod_d_e925fa09("JumpManager", "Custom button clicked: " + var2.toString());
         if (var2.recoverField_b == 0) {
            RecoverType2e4d193c.recoverMethod_a_74ff4fff(this.recoverField_a, "cn.jpush.android.intent.NOTIFICATION_OPENED_CUSTOM_BUTTON", null, var3);
         } else {
            this.recoverMethod_b_4bc56471(var1, var2.recoverMethod_a_a7dbfe3(), var2.recoverField_b);
         }

         if (!this.recoverMethod_a_afa300e8(var2.recoverField_e, var2.recoverField_d)) {
            Logger.recoverMethod_e_e925fa09("JumpManager", "illegal jump, so return");
            RecoverType8b455831.recoverMethod_a_36ca9ac3(var2.recoverField_a, 1107L, var1);
            return;
         }

         boolean var4 = this.recoverMethod_a_8f36d2b7(var2.recoverField_d, 4, var2.recoverField_e, var2.recoverMethod_a_a7dbfe3().toString(), "");
         this.recoverMethod_a_c2ba8db(var1, var2);
      } catch (Exception var5) {
         Logger.recoverMethod_e_e925fa09("JumpManager", "Handle custom button click error: " + var5.getMessage());
      }
   }

   public void recoverMethod_a_ae3b7eb8(Context var1, RecoverType2e4cce2a var2) {
      this.recoverMethod_b_f0554df3(var1);

      try {
         Logger.recoverMethod_d_e925fa09("JumpManager", "Notification to InApp click notification");
         RecoverType8b455831.recoverMethod_a_36ca9ac3(var2.recoverField_d, 1601L, var1);
         RecoverType2e4ce86e.recoverMethod_a_ae3b7eb8(var1, var2);
         RecoverType2e4d193c.recoverMethod_a_c6e61809(this.recoverField_a, "cn.jpush.android.intent.IN_APP_MSG_CLICK", var2, null, null);
         boolean var3 = this.recoverMethod_a_ec36941(var2, null, true);
         long var4 = var3 ? 1605L : 1606L;
         RecoverType8b455831.recoverMethod_a_36ca9ac3(var2.recoverField_d, var4, var1);
      } catch (Throwable var6) {
         Logger.recoverMethod_w_e925fa09("JumpManager", "handleNotificationToInAppClick  error : " + var6.getMessage());
      }
   }

   private void recoverMethod_a_c2ba8db(Context var1, RecoverType2e4cce2a.Memberce51f067 var2) {
      int var3 = RecoverType2e4d193c.recoverMethod_a_afa300d7(var2.recoverField_a, var2.recoverField_b);
      RecoverType2e4d193c.recoverMethod_d_1a54e370(var1, var3);
   }

   public void recoverMethod_c_c4d2ecb0(Context var1, Intent var2) {
      this.recoverMethod_b_f0554df3(var1);
      Logger.recoverMethod_d_e925fa09("JumpManager", "third user click notification");

      try {
         String var3 = this.recoverMethod_a_a27c4aae(var2);
         Logger.recoverMethod_d_e925fa09("JumpManager", "handleThirdNotificationClick msg content is= " + var3);
         if (TextUtils.isEmpty(var3)) {
            RecoverType2e4d193c.recoverMethod_a_74ff4fff(var1, var2.getAction(), null, var2);
            return;
         }

         JSONObject var4 = new JSONObject(var3);
         if (var4.has("o-l-l")) {
            Bundle var18 = new Bundle();
            var18.putString("aes", var4.optString("aes"));
            JCoreHelper.openLog(var1, var18);
            return;
         }

         String var5 = var4.optString("msg_id");
         byte var6 = (byte)var4.optInt("rom_type");
         Logger.recoverMethod_d_e925fa09("JumpManager", "通道：" + this.recoverMethod_a_f2253399(var6));
         boolean var7 = var4.has("msg_id");
         boolean var8 = var4.has("rom_type");
         boolean var9 = var4.has("n_title");
         boolean var10 = var4.has("n_content");
         Logger.recoverMethod_d_e925fa09("JumpManager", "hasMsgid:" + var7);
         Logger.recoverMethod_d_e925fa09("JumpManager", "hasWhich:" + var8);
         Logger.recoverMethod_d_e925fa09("JumpManager", "hasTitle:" + var9);
         Logger.recoverMethod_d_e925fa09("JumpManager", "hasContent:" + var10);
         if (!var7 || !var8 || !var9 || !var10) {
            Logger.recoverMethod_d_e925fa09("JumpManager", "参数缺失");
            return;
         }

         this.recoverMethod_a_3c4029ab(var1, var4);
         RecoverType8b455831.recoverMethod_a_4ef42769(var1, var5, var6, var3);
         JSONObject var11 = var4.optJSONObject("n_intent");
         var4.remove("n_intent");
         if (null == var11) {
            Logger.recoverMethod_d_e925fa09("JumpManager", "handleThirdNotificationClick n_intent is null,sendToUserReceiver");
            this.recoverMethod_a_4bc56471(var1, var4, var6);
            return;
         }

         String var12 = var11.optString("n_url", "");
         String var13 = var11.optString("n_ssl", "");
         int var14 = var11.optInt("n_type", 1);
         Logger.recoverMethod_d_e925fa09("JumpManager", "n_url:" + var12);
         Logger.recoverMethod_d_e925fa09("JumpManager", "n_ssl:" + var13);
         Logger.recoverMethod_d_e925fa09("JumpManager", "n_type:" + var14);
         if (var14 != 3 && TextUtils.isEmpty(var12)) {
            Logger.recoverMethod_d_e925fa09("JumpManager", "handleThirdNotificationClick url is null,sendToUserReceiver");
            this.recoverMethod_a_4bc56471(var1, var4, var6);
            return;
         }

         if (RecoverTypea2146d45.recoverMethod_r_f0554df7(this.recoverField_a)) {
            Logger.recoverMethod_d_e925fa09("JumpManager", "notification callback enable, sendToUserReceiver");
            this.recoverMethod_a_4bc56471(var1, var4, var6);
         }

         String var15 = new String(Base64.decode(var12, 0));
         if (!TextUtils.isEmpty(var12) && !this.recoverMethod_a_7555e60(var1, var12, var13, var14)) {
            Logger.recoverMethod_e_e925fa09("JumpManager", "illegal jump,so return url_intent=" + var15);
            RecoverType8b455831.recoverMethod_a_3e8d9d2b(var5, "", var6, 1107L, var1, var3, null);
            return;
         }

         Logger.recoverMethod_d_e925fa09("JumpManager", "url_intent:" + var15);
         String var16 = var4.toString();
         Logger.recoverMethod_d_e925fa09("JumpManager", "extraData:" + var16);
         this.recoverMethod_a_8f36d2b7(var14, this.recoverField_C, var15, var16, "");
      } catch (Throwable var17) {
         Logger.recoverMethod_w_e925fa09("JumpManager", "handleThirdNotificationClick  error : " + var17.getMessage());
      }
   }

   private boolean recoverMethod_a_ec36941(RecoverType2e4cce2a var1, Intent var2, boolean var3) {
      try {
         String var4 = var1.recoverField_i;
         Logger.recoverMethod_d_e925fa09("JumpManager", "handleOriginalMessage msg content is= " + var4);
         if (TextUtils.isEmpty(var4)) {
            return false;
         }

         JSONObject var5 = new JSONObject(var4);
         JSONObject var6 = var5.optJSONObject("m_content");
         JSONObject var7 = var6.optJSONObject("n_intent");
         Logger.recoverMethod_d_e925fa09("JumpManager", "handleOriginalMessage n_intent=" + var7);
         if (null == var7 && !var3) {
            RecoverType2e4d193c.recoverMethod_a_74ff4fff(this.recoverField_a, "cn.jpush.android.intent.NOTIFICATION_OPENED", var1, var2);
            Logger.recoverMethod_d_e925fa09("JumpManager", "handleOriginalMessage n_intent is null, sendToUserReceiver");
            return false;
         }

         int var8 = var7.optInt("n_type");
         String var9 = var7.optString("n_url");
         Logger.recoverMethod_d_e925fa09("JumpManager", "handleOriginalMessage url=" + var9);
         Logger.recoverMethod_d_e925fa09("JumpManager", "handleOriginalMessage jumpType=" + var8);
         if (!this.recoverMethod_a_afa300e8(var9, var8)) {
            Logger.recoverMethod_e_e925fa09("JumpManager", "illegal jump,so return");
            RecoverType8b455831.recoverMethod_a_36ca9ac3(var1.recoverField_d, 1107L, this.recoverField_a);
            return false;
         }

         String var10 = var7.optString("n_package_name", "");
         if (var8 == 0 && !TextUtils.isEmpty(var9)) {
            if (var9.startsWith("intent")) {
               var8 = 1;
            } else {
               var8 = 2;
            }
         }

         if (var8 != 3 && TextUtils.isEmpty(var9) && !var3) {
            RecoverType2e4d193c.recoverMethod_a_74ff4fff(this.recoverField_a, "cn.jpush.android.intent.NOTIFICATION_OPENED", var1, var2);
            Logger.recoverMethod_d_e925fa09("JumpManager", "handleOriginalMessage url is null, sendToUserReceiver");
            return false;
         }

         if (RecoverTypea2146d45.recoverMethod_r_f0554df7(this.recoverField_a) && !var3) {
            RecoverType2e4d193c.recoverMethod_a_74ff4fff(this.recoverField_a, "cn.jpush.android.intent.NOTIFICATION_OPENED", var1, var2);
            Logger.recoverMethod_d_e925fa09("JumpManager", "notification callback enable, sendToUserReceiver");
         }

         Logger.recoverMethod_d_e925fa09("JumpManager", "handleOriginalMessage targetPkgName=" + var10);
         var6.put("msg_id", var1.recoverField_d);
         var6.put("rom_type", "0");
         var6.put("notification_id", RecoverType2e4d193c.recoverMethod_a_73eedead(var1));
         var6.put("_j_data_", var5.optString("_j_data_"));
         var6.remove("n_intent");
         String var11 = var6.toString();
         Logger.recoverMethod_d_e925fa09("JumpManager", "handleOriginalMessage extraData=" + var11);
         boolean var12 = this.recoverMethod_a_8f36d2b7(var8, 2, var9, var11, var10);
         if (!var12 && var3) {
            int var13 = var7.optInt("n_fail_handle_type", 3);
            String var14 = var7.optString("n_fail_handle_url", "");
            if (var13 == 0) {
               var13 = 3;
            } else if (var13 == 1 || var13 == 2) {
               var13 = 2;
            }

            Logger.recoverMethod_d_e925fa09("JumpManager", "handleOriginalMessage inapp jump  failed use failedJumpUrl= " + var14 + " failedJumpType=" + var13);
            return this.recoverMethod_a_8f36d2b7(var13, 2, var14, var11, var10);
         } else {
            Logger.recoverMethod_d_e925fa09("JumpManager", "handleOriginalMessage jump " + (var12 ? "success" : "fail"));
            return var12;
         }
      } catch (Throwable var15) {
         Logger.recoverMethod_d_e925fa09("JumpManager", "handleOriginalMessage throwable " + var15);
         return false;
      }
   }

   private boolean recoverMethod_a_8f36d2b7(int var1, int var2, String var3, String var4, String var5) {
      switch (var1) {
         case 1:
            if (var3.contains("android.intent.action.MAIN")) {
               return this.recoverMethod_a_3f52113e(this.recoverField_a, var2, var4);
            }

            return this.recoverMethod_a_3aaf2088(this.recoverField_a, var2, var3, var4);
         case 2:
            return this.recoverMethod_a_67c6b152(this.recoverField_a, var2, var3, var5, var4);
         case 3:
            return this.recoverMethod_a_3f52113e(this.recoverField_a, var2, var4);
         default:
            return false;
      }
   }

   private boolean recoverMethod_a_3aaf2088(Context var1, int var2, String var3, String var4) {
      try {
         Intent var5 = this.recoverMethod_a_44df3c34(var3);
         var5.setFlags(268435456);
         var5.setPackage(var1.getPackageName());
         this.recoverMethod_a_5f102035(var2, var4, var5);
         var1.startActivity(var5);
         return true;
      } catch (Throwable var6) {
         Logger.recoverMethod_ww_e925fa09("JumpManager", "jumpIntent Throwable:" + var6.getMessage());
         return false;
      }
   }

   private boolean recoverMethod_a_3f52113e(Context var1, int var2, String var3) {
      Intent var4 = RecoverType9b4c5ae5.recoverMethod_g_813b6dc0(var1);
      if (var4 != null) {
         try {
            this.recoverMethod_a_5f102035(var2, var3, var4);
            var1.startActivity(var4);
            return true;
         } catch (Throwable var6) {
            Logger.recoverMethod_ww_e925fa09("JumpManager", "start main intent error:" + var6.getMessage());
         }
      }

      return false;
   }

   public boolean recoverMethod_a_67c6b152(Context var1, int var2, String var3, String var4, String var5) {
      if (TextUtils.isEmpty(var3)) {
         Logger.recoverMethod_d_e925fa09("JumpManager", "jumpDeepLink link uri is empty");
         return false;
      }

      boolean var6 = true;

      try {
         Logger.recoverMethod_d_e925fa09("JumpManager", "jumpDeepLink url:" + var3);
         if (var3.startsWith("http")) {
            var6 = this.recoverMethod_b_3aaf2088(var1, var2, var3, var5);
         } else {
            Logger.recoverMethod_d_e925fa09("JumpManager", "jumpDeepLink intent:" + var3);
            var6 = this.recoverMethod_b_67c6b152(var1, var2, var3, var4, var5);
         }
      } catch (Throwable var8) {
         Logger.recoverMethod_d_e925fa09("JumpManager", "jumpDeepLink start fail uri error:" + var8.getMessage());
         var6 = false;
      }

      return var6;
   }

   private boolean recoverMethod_b_3aaf2088(Context var1, int var2, String var3, String var4) {
      if (TextUtils.isEmpty(var3)) {
         Logger.recoverMethod_d_e925fa09("JumpManager", "jumpWebUrl link uri is empty");
         return false;
      }

      Logger.recoverMethod_d_e925fa09("JumpManager", "jumpWebUrl:" + var3);

      try {
         Intent var5 = null;
         if (RecoverType9b4c5ae5.recoverMethod_c_5ffc0101(var1, "com.android.browser")) {
            Logger.recoverMethod_d_e925fa09("JumpManager", "start android default browser ");
            var5 = new Intent();
            var5.setAction("android.intent.action.VIEW");
            Uri var11 = Uri.parse(var3);
            var5.setData(var11);
            var5.setClassName("com.android.browser", "com.android.browser.BrowserActivity");
         } else {
            Logger.recoverMethod_d_e925fa09("JumpManager", "start browser by android ");
            var5 = RecoverType9b4c5ae5.recoverMethod_a_2e1cfbaf(var3, 0);
         }

         this.recoverMethod_a_7c7ef75d(var1, var5, var2, var4);
         return true;
      } catch (ActivityNotFoundException var8) {
         try {
            Intent var6 = RecoverType9b4c5ae5.recoverMethod_a_2e1cfbaf(var3, 0);
            this.recoverMethod_a_7c7ef75d(var1, var6, var2, var4);
            return true;
         } catch (Throwable var7) {
            Logger.recoverMethod_d_e925fa09("JumpManager", "start ActivityNotFoundException  fail=" + var8);
         }
      } catch (Throwable var9) {
         Logger.recoverMethod_d_e925fa09("JumpManager", "start uri fail=" + var9);
      }

      return false;
   }

   private void recoverMethod_a_7c7ef75d(Context var1, Intent var2, int var3, String var4) {
      var2.setFlags(268435456);
      if (var3 == 1) {
         var3 = 2;
      }

      if (var3 != -1 && !TextUtils.isEmpty(var4)) {
         this.recoverMethod_a_5f102035(var3, var4, var2);
      }

      this.recoverMethod_d_c4d2ecb0(var1, var2);
      var1.startActivity(var2);
   }

   private boolean recoverMethod_b_67c6b152(Context var1, int var2, String var3, String var4, String var5) {
      try {
         Intent var6 = this.recoverMethod_a_44df3c34(var3);
         var6.setFlags(268435456);
         if (!TextUtils.isEmpty(var4)) {
            var6.setPackage(var4);
         }

         ResolveInfo var7 = var1.getPackageManager().resolveActivity(var6, 0);
         if (var7 == null) {
            Logger.recoverMethod_d_e925fa09("JumpManager", "deeplink is wrong, " + var3);
            return false;
         }

         if (var2 == 1) {
            var2 = 2;
         }

         if (var2 != -1 && !TextUtils.isEmpty(var5)) {
            this.recoverMethod_a_5f102035(var2, var5, var6);
         }

         this.recoverMethod_d_c4d2ecb0(var1, var6);
         var1.startActivity(var6);
         return true;
      } catch (Throwable var8) {
         return false;
      }
   }

   private void recoverMethod_d_c4d2ecb0(Context var1, Intent var2) {
      if ("com.mt.mtxx.mtxx".equals(var1.getPackageName())) {
         var2.putExtra("infoProvider", "JPush");
      }
   }

   private void recoverMethod_a_3c4029ab(Context var1, JSONObject var2) {
      try {
         String var3 = var2.optString("msg_id");
         String var4 = var2.optString("n_title");
         String var5 = var2.optString("n_content");
         String var6 = var2.optString("n_extras");
         JSONArray var7 = var2.optJSONArray("n_target_event");
         RecoverType2e4cce2a var8 = new RecoverType2e4cce2a();
         var8.recoverField_v = var4;
         var8.recoverField_d = var3;
         var8.recoverField_y = var5;
         var8.recoverField_G = var6;
         var8.recoverField_w = var7;
         RecoverType2e4ce86e.recoverMethod_a_ae3b7eb8(var1, var8);
      } catch (Throwable var9) {
      }
   }

   private void recoverMethod_b_f0554df3(Context var1) {
      if (this.recoverField_a == null) {
         this.recoverField_a = var1;
      }
   }

   private boolean recoverMethod_a_7555e60(Context var1, String var2, String var3, int var4) {
      this.recoverMethod_b_f0554df3(var1);
      Logger.recoverMethod_d_e925fa09("JumpManager", "checkConfig url=" + var2 + " ssl=" + var3);
      Logger.recoverMethod_d_e925fa09("JumpManager", "checkConfig user jump Config=" + recoverField_c);

      try {
         String var5 = RecoverType9b4c5ae5.recoverMethod_d_e96ea081(var2);
         Logger.recoverMethod_d_e925fa09("JumpManager", "checkConfig url hash=" + var5);
         String var6 = this.recoverMethod_c_e96ea081(var3);
         Logger.recoverMethod_d_e925fa09("JumpManager", "checkConfig decodeData=" + var6);
         if (!TextUtils.isEmpty(var6) && !TextUtils.isEmpty(var5) && var6.equalsIgnoreCase(var5)) {
            return this.recoverMethod_a_afa300e8(new String(Base64.decode(var2.getBytes(), 2)), var4);
         }

         Logger.recoverMethod_dd_e925fa09("JumpManager", "checkConfig fail, so return");
      } catch (Throwable var7) {
         Logger.recoverMethod_dd_e925fa09("JumpManager", "checkConfig Throwable=" + var7);
      }

      return false;
   }

   private boolean recoverMethod_a_afa300e8(String var1, int var2) {
      if (var2 == 2) {
         return true;
      }

      if (null == recoverField_c) {
         Logger.recoverMethod_dd_e925fa09("JumpManager", "jumpConfig is null goto get config");
         this.recoverMethod_c_f0554df3(this.recoverField_a);
      }

      if (null == recoverField_c) {
         Logger.recoverMethod_dd_e925fa09("JumpManager", "jumpConfig is null,return true");
         return true;
      }

      try {
         String[] var3 = recoverField_c.split("/");
         Logger.recoverMethod_d_e925fa09("JumpManager", "jumpConfig =" + Arrays.toString(var3));
         String var4 = this.recoverMethod_b_e96ea081(var1);
         Logger.recoverMethod_d_e925fa09("JumpManager", " origin className=" + var4);

         for (String var8 : var3) {
            if (!TextUtils.isEmpty(var4) && var8.equals(var4)) {
               Logger.recoverMethod_d_e925fa09("JumpManager", " match user config className");
               return true;
            }
         }
      } catch (Throwable var9) {
         Logger.recoverMethod_dd_e925fa09("JumpManager", "jumpConfig Throwable=" + var9);
      }

      return false;
   }

   private Intent recoverMethod_a_44df3c34(String var1) throws java.net.URISyntaxException {
      Intent var2;
      if (VERSION.SDK_INT >= 22) {
         var2 = RecoverType9b4c5ae5.recoverMethod_a_2e1cfbaf(var1, 4);
      } else {
         var2 = RecoverType9b4c5ae5.recoverMethod_a_2e1cfbaf(var1, 0);
      }

      return var2;
   }

   private String recoverMethod_b_e96ea081(String var1) {
      try {
         Intent var2 = this.recoverMethod_a_44df3c34(var1);
         var2.setPackage(this.recoverField_a.getPackageName());
         ResolveInfo var3 = this.recoverField_a.getPackageManager().resolveActivity(var2, 0);
         return var3.activityInfo.name;
      } catch (Throwable var4) {
         return "";
      }
   }

   private String recoverMethod_c_e96ea081(String var1) {
      if (TextUtils.isEmpty(var1)) {
         return "";
      }

      String var2 = "";

      try {
         return RecoverType9b4c5af0.recoverMethod_b_f169196d(var1, this.recoverMethod_b_73cf11cb(), recoverField_e);
      } catch (Throwable var4) {
         Logger.recoverMethod_dd_e925fa09("JumpManager", "checkJumpConfig rsa=" + var4);
         return "";
      }
   }

   private String recoverMethod_b_73cf11cb() {
      return new String(Base64.decode(recoverField_d, 2));
   }

   private void recoverMethod_c_f0554df3(Context var1) {
      Bundle var2 = null;

      try {
         ApplicationInfo var3 = var1.getPackageManager().getApplicationInfo(var1.getPackageName(), 128);
         if (null != var3) {
            var2 = var3.metaData;
         }

         if (null != var2) {
            recoverField_c = var2.getString("JPUSH_JUMP");
            Logger.recoverMethod_d_e925fa09("JumpManager", "getJumpConfig config =" + recoverField_c);
         }
      } catch (Throwable var4) {
         Logger.recoverMethod_d_e925fa09("JumpManager", "getJumpConfig =" + var4.getMessage());
      }
   }

   private void recoverMethod_a_4bc56471(Context var1, JSONObject var2, byte var3) {
      Log.d("JumpManager", "whichPushSDK=" + var3 + " jsonObject=" + var2);
      Bundle var4 = new Bundle();
      var4.putByte("platform", var3);
      var4.putString("data", var2.toString());
      JThirdPlatFormInterface.doAction(var1, "action_notification_clicked", var4);
   }

   private void recoverMethod_b_4bc56471(Context var1, JSONObject var2, byte var3) {
      Log.d("JumpManager", "whichPushSDK=" + var3 + " jsonObject=" + var2);
      Bundle var4 = new Bundle();
      var4.putByte("platform", var3);
      var4.putString("data", var2.toString());
      JThirdPlatFormInterface.doAction(var1, "action_notification_custom_button_clcked", var4);
   }

   private void recoverMethod_a_5f102035(int var1, String var2, Intent var3) {
      switch (var1) {
         case 1:
            var3.setData(Uri.parse(var2));
            var3.putExtra("JMessageExtra", var2);
            break;
         case 2:
            var3.putExtra("JMessageExtra", var2);
            break;
         case 3:
            var3.putExtra("asus_data", var2);
            var3.putExtra("JMessageExtra", var2);
            break;
         case 4:
            var3.putExtra("JMessageButtonExtra", var2);
      }
   }

   private String recoverMethod_a_f2253399(byte var1) {
      String var2;
      switch (var1) {
         case 0:
         case 9:
         case 10:
         case 11:
         case 12:
         case 13:
         case 14:
         case 15:
         default:
            var2 = "jpush";
            break;
         case 1:
            var2 = "xiaomi";
            break;
         case 2:
            var2 = "huawei";
            break;
         case 3:
            var2 = "meizu";
            break;
         case 4:
            var2 = "oppo";
            break;
         case 5:
            var2 = "vivo";
            break;
         case 6:
            var2 = "asus";
            break;
         case 7:
            var2 = "HONOR";
            break;
         case 8:
            var2 = "fcm";
            break;
         case 16:
            var2 = "nio";
      }

      return var2;
   }

   private String recoverMethod_a_a27c4aae(Intent var1) {
      String var2 = null;
      if (var1.getData() != null) {
         var2 = var1.getData().toString();
         Logger.recoverMethod_d_e925fa09("JumpManager", "华为：" + var2);
         this.recoverField_C = 1;
      }

      Bundle var3 = var1.getExtras();
      if (var3 != null) {
         if (TextUtils.isEmpty(var2)) {
            var2 = var3.getString("JMessageExtra");
            Logger.recoverMethod_d_e925fa09("JumpManager", "fcm、oppo、vivo、华硕、小米、HONOR：" + var2);
            this.recoverField_C = 2;
         }

         if (TextUtils.isEmpty(var2)) {
            var2 = var3.getString("key_app_data");
            Logger.recoverMethod_d_e925fa09("JumpManager", "nio_data：" + var2);
            this.recoverField_C = 2;
         }

         if (TextUtils.isEmpty(var2)) {
            var2 = var3.getString("asus_data");
            Logger.recoverMethod_d_e925fa09("JumpManager", "asus_data：" + var2);
            this.recoverField_C = 3;
         }
      }

      var2 = recoverMethod_d_e96ea081(var2);
      Logger.recoverMethod_d_e925fa09("JumpManager", "data：" + var2);
      return var2;
   }

   private static String recoverMethod_d_e96ea081(String var0) {
      if (!TextUtils.isEmpty(var0)) {
         try {
            JSONObject var1 = new JSONObject(var0);
            String var2 = "jg_c";
            if (var1.has(var2)) {
               String var3 = var1.optString(var2);
               byte[] var4 = Base64.decode(var3, 2);
               byte[] var5 = RecoverType9b4c5af2.recoverMethod_a_4962d961(var4);
               var0 = new String(var5);
            }
         } catch (Throwable var6) {
         }
      }

      return var0;
   }

   private String recoverMethod_b_a27c4aae(Intent var1) {
      Bundle var2 = var1.getExtras();
      return var2 == null ? null : var2.getString("jg_button");
   }

   private static final class Memberf9a150e8 {
      private static final RecoverType9b4c5aeb recoverField_a = new RecoverType9b4c5aeb();
   }
}
