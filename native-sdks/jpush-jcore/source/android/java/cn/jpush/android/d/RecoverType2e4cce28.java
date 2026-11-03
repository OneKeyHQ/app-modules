package cn.jpush.android.d;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import cn.jpush.android.ab.RecoverType9b4c5363;
import cn.jpush.android.ad.RecoverType9b4c5ae5;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.b.RecoverType2e4cc6a6;
import cn.jpush.android.b.RecoverType2e4cc6a8;
import cn.jpush.android.cache.RecoverTypea2146d45;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.helper.RecoverType8b45582f;
import cn.jpush.android.helper.RecoverType8b455831;
import cn.jpush.android.local.JPushConstants;
import cn.jpush.android.q.RecoverType2e4cfef4;
import cn.jpush.android.x.RecoverType2e4d193c;
import cn.jpush.android.x.RecoverType2e4d193e;
import cn.jpush.android.z.RecoverType2e4d20be;
import cn.jpush.android.z.RecoverType2e4d20bf;
import java.io.LineNumberReader;
import java.io.StringReader;
import java.util.Locale;
import org.json.JSONObject;

public class RecoverType2e4cce28 {
   public static void recoverMethod_a_d3c5a803(Context var0, RecoverType2e4d20bf var1) {
      String var2 = RecoverType2e4cce2a.recoverMethod_d_e96ea081(var1.recoverMethod_c_73cf11cb());
      recoverMethod_a_3fefadba(var0, var1.recoverMethod_a_9b68(), var1.recoverMethod_b_9b69(), var1.getRid(), var2);
      long var3 = var1.recoverMethod_b_9b69();
      int var5 = var1.recoverMethod_a_9b68();
      String var6 = var1.recoverMethod_c_73cf11cb();
      Logger.recoverMethod_d_e925fa09("MessageHelper", "msgType = " + var5 + ", msgId = " + var3 + ", jdata = " + var2);
      Logger.recoverMethod_v_e925fa09("MessageHelper", "msgContent: \n" + var6);
      Logger.recoverMethod_d_e925fa09("MessageHelper", "msgContent size:" + var6.getBytes().length);
      String var7 = null;
      String var8 = null;
      LineNumberReader var9 = new LineNumberReader(new StringReader(var6));

      try {
         var8 = var9.readLine();
         var7 = var9.readLine();
         if (var8 == null) {
            Logger.recoverMethod_e_e925fa09("MessageHelper", "NO appId");
            return;
         }

         if (var7 == null) {
            Logger.recoverMethod_e_e925fa09("MessageHelper", "NO senderId");
            return;
         }

         int var10 = var8.length() + var7.length() + 2;
         if (var6.length() <= var10 + 1) {
            Logger.recoverMethod_d_e925fa09("MessageHelper", "No msgContent");
            return;
         }

         var6 = var6.substring(var10);
         if (!var7.equals(JCoreHelper.getAppKey(var0))) {
            Logger.recoverMethod_e_e925fa09("MessageHelper", "msg senderId is not mine appkey,senderId:" + var7 + ",appkey:" + JCoreHelper.getAppKey(var0));
            return;
         }

         Logger.recoverMethod_v_e925fa09(
            "MessageHelper", "Message Fields - appId:" + var8 + ", senderId:" + var7 + ", msgContent:" + var6 + ", contentLen: " + var6.length()
         );
         switch (var5) {
            case 0:
            case 2:
            case 100:
            case 101:
            case 103:
            case 104:
            case 115:
               recoverMethod_a_90ee5046(var0, var5, var6, var8, var7, var3, (byte)0);
               break;
            case 20:
               RecoverType9b4c5363.recoverMethod_a_d0b594d9(var0, var6, 0, -1L, -1);
               break;
            default:
               Logger.recoverMethod_d_e925fa09("MessageHelper", "unknown msg type, type: " + var5);
         }
      } catch (Throwable var11) {
         Logger.recoverMethod_e_1765c98c("MessageHelper", "Parse msgContent failed", var11);
      }
   }

   public static void recoverMethod_a_90ee5046(Context var0, int var1, String var2, String var3, String var4, long var5, byte var7) {
      if (!var3.equals(var0.getPackageName())) {
         Logger.recoverMethod_e_e925fa09("MessageHelper", "msg appId is not mine pkgname,appid:" + var3 + ",pkgname:" + var0.getPackageName());
      } else {
         Logger.recoverMethod_d_e925fa09("MessageHelper", "action:receivedPushMessage msgId = " + var5);
         RecoverType2e4cce2a var8 = RecoverType2e4cce2a.recoverMethod_a_e5f4ac2a(var2, var3, var4, var5);
         if (null != var8) {
            var8.recoverField_a = var1;
            if (recoverMethod_a_c81dc8b7(var1, var8)) {
               Logger.recoverMethod_d_e925fa09("MessageHelper", "ssp notification, msgType: " + var1);
            } else if (!RecoverType2e4cce29.recoverMethod_a_ae3a219f(var0, var8.recoverMethod_a_cd303c69())) {
               if (!RecoverType2e4d193e.recoverMethod_a_7555e60(var0, var8.recoverField_d, var8.recoverField_h, var1)) {
                  if (var7 != 0) {
                     var8.recoverField_ai = var7;
                  }

                  var8.recoverField_a = var1;
                  RecoverType2e4cc6a6 var9 = recoverMethod_a_fbe43da5(var8);
                  if (var9 != null && !TextUtils.isEmpty(var9.recoverField_a)) {
                     RecoverType2e4cc6a8.recoverMethod_a_cd2f5408().recoverMethod_a_708444be(var9);
                  } else {
                     if (var1 == 115) {
                        RecoverType2e4cfef4.recoverMethod_a_cd36253c()
                           .recoverMethod_a_e6d36a82(var0, RecoverType2e4cce2a.recoverMethod_a_fbe526a1(var8), var8.recoverField_d);
                     } else {
                        if (recoverMethod_a_134632(var1) && recoverMethod_a_6fc2f55e(var1, var0)) {
                           recoverMethod_a_1934cb4b(var0, var8, var1);
                           return;
                        }

                        recoverMethod_a_ae3b7eb8(var0, var8);
                     }
                  }
               }
            }
         }
      }
   }

   private static boolean recoverMethod_a_134632(int var0) {
      return var0 == 104 || var0 == 103;
   }

   private static boolean recoverMethod_a_6fc2f55e(int var0, Context var1) {
      return var0 == 104 ? 1 != RecoverType2e4d193c.recoverMethod_d_f0554de6(var1) : var0 == 103;
   }

   private static void recoverMethod_a_1934cb4b(Context var0, RecoverType2e4cce2a var1, int var2) {
      RecoverType2e4cce2a var3 = RecoverType2e4cce2a.recoverMethod_a_f0b11163(var0, var1);
      if (var3 != null) {
         Logger.recoverMethod_dd_e925fa09("MessageHelper", "msgType:" + var2 + " inapp message received");
         var3.recoverField_a = var2;
         var3.recoverField_az = var2;
         RecoverType2e4cfef4.recoverMethod_a_cd36253c().recoverMethod_a_e6d36a82(var0, var1, var1.recoverField_d);
      } else {
         Logger.recoverMethod_ee_e925fa09("MessageHelper", "msgType:" + var2 + " inapp message pushEntity: " + var1);
      }
   }

   public static boolean recoverMethod_a_c81dc8b7(int var0, RecoverType2e4cce2a var1) {
      if (null == var1) {
         return false;
      }

      if (var0 != 0) {
         return false;
      }

      String var2 = "";

      try {
         JSONObject var3 = new JSONObject(var1.recoverField_i);
         JSONObject var4 = var3.optJSONObject("m_content");
         if (var4 != null) {
            var2 = var4.optString("n_source", "");
         }
      } catch (Throwable var5) {
      }

      return TextUtils.equals(var2, "ssp");
   }

   private static RecoverType2e4cc6a6 recoverMethod_a_fbe43da5(RecoverType2e4cce2a var0) {
      try {
         JSONObject var1 = new JSONObject(var0.recoverField_i);
         JSONObject var2 = var1.optJSONObject("geofence");
         RecoverType2e4cc6a6 var3 = RecoverType2e4cc6a6.recoverMethod_a_ef7f2e56(var2);
         if (var3 != null) {
            var3.recoverField_t = var0;
            return var3;
         }
      } catch (Throwable var4) {
      }

      return null;
   }

   public static void recoverMethod_a_ae3b7eb8(Context var0, RecoverType2e4cce2a var1) {
      Logger.recoverMethod_d_e925fa09("MessageHelper", "processBasicEntity type:" + var1.recoverField_f);
      byte var2 = 0;
      if (var1.recoverField_k) {
         var2 = 1;
         if (var1.recoverField_j == 4) {
            var2 = 3;
         }
      } else {
         var2 = 2;
      }

      var1.recoverField_f = var2;
      if ((2 & var1.recoverField_f) != 0) {
         Logger.recoverMethod_d_e925fa09("MessageHelper", "processBasicEntity user-defined message.");
         if (TextUtils.isEmpty(var1.recoverField_n) && TextUtils.isEmpty(var1.recoverField_q)) {
            Logger.recoverMethod_ww_e925fa09("MessageHelper", "no message or extra send to user");
         } else {
            recoverMethod_b_ae3b7eb8(var0, var1);
         }
      }

      if ((1 & var1.recoverField_f) != 0) {
         Logger.recoverMethod_d_e925fa09("MessageHelper", "processBasicEntity notification");
         if (RecoverTypea2146d45.recoverMethod_e_f0554df7(var0)) {
            Logger.recoverMethod_i_e925fa09("MessageHelper", "Service is stoped, give up all the message");
            return;
         }

         var1.recoverMethod_a_f0554df3(var0);
         if (!RecoverType2e4d193c.recoverMethod_b_f0554df7(var0)) {
            RecoverType8b455831.recoverMethod_a_36ca9ac3(var1.recoverField_d, 986L, var0);
            Logger.recoverMethod_ii_e925fa09("MessageHelper", "push is invalidPushTime，Intercept the message");
            return;
         }

         RecoverType2e4d193e.recoverMethod_b_5ffc00fd(var0, var1.recoverField_d);
         RecoverType2e4d193c.recoverMethod_a_ae3b7eb8(var0, var1);
      }
   }

   private static void recoverMethod_a_3fefadba(Context var0, int var1, long var2, long var4, String var6) {
      byte[] var7 = RecoverType2e4d20be.recoverMethod_a_322ca05d(0, (byte)var1, var2, var6);
      JCoreHelper.sendData(var0, "JPUSH", 4, 3, var4, var7);

      try {
         JSONObject var8 = new JSONObject(var6);
         int var9 = var8.optInt("data_msgtype");
         Logger.recoverMethod_d_e925fa09("MessageHelper", " data_msgtype=" + var9);
         if (1 == var9 || 3 == var9 || 5 == var9) {
            RecoverType8b455831.recoverMethod_a_36ca9ac3(var2 + "", 1108L, var0);
         } else if (32 == var9) {
            RecoverType8b455831.recoverMethod_a_510e8a7c(var0, var2 + "", 1000L, 4);
         }
      } catch (Throwable var10) {
      }
   }

   public static void recoverMethod_b_ae3b7eb8(Context var0, RecoverType2e4cce2a var1) {
      try {
         String var2 = JPushConstants.getMessageReceiverClass(var0);
         if (!TextUtils.isEmpty(var2)) {
            Intent var3 = new Intent(var0, Class.forName(var2));
            var3.setAction(RecoverType9b4c5ae5.recoverMethod_e_a4ea6d43(var0, "custom_msg"));
            var3.putExtra("data", var1.recoverField_i);
            var3.putExtra("msgid", var1.recoverField_d);
            var3.putExtra("appId", var1.recoverField_b);
            var3.putExtra("senderId", var1.recoverField_c);
            var3.putExtra("platform", var1.recoverField_ai);
            var3.putExtra("_j_data_", var1.recoverField_g);
            if (JPushConstants.getPushMessageReceiver(var0) != null) {
               JPushConstants.getPushMessageReceiver(var0).onMessage(var0, recoverMethod_a_a6521e61(var3));
            } else {
               RecoverType8b45582f.recoverMethod_a_c4d2ecb0(var0, var3);
            }
         } else {
            Logger.recoverMethod_dd_e925fa09("MessageHelper", "not found user push message,use old action to user");
            Intent var5 = new Intent("cn.jpush.android.intent.MESSAGE_RECEIVED");
            var5.putExtra("cn.jpush.android.APPKEY", var1.recoverField_c);
            var5.putExtra("cn.jpush.android.MESSAGE", var1.recoverField_n);
            var5.putExtra("cn.jpush.android.CONTENT_TYPE", var1.recoverField_o);
            var5.putExtra("cn.jpush.android.TITLE", var1.recoverField_p);
            var5.putExtra("cn.jpush.android.EXTRA", var1.recoverField_q);
            var5.putExtra("cn.jpush.android.MSG_ID", var1.recoverField_d);
            if (JPushConstants.SDK_VERSION_CODE >= 387) {
               var5.putExtra("cn.jpush.android.TYPE_PLATFORM", var1.recoverField_ai);
            }

            var5.addCategory(var1.recoverField_b);
            var5.setPackage(var0.getPackageName());
            var0.sendBroadcast(var5, String.format(Locale.ENGLISH, "%s.permission.JPUSH_MESSAGE", var1.recoverField_b));
            Logger.recoverMethod_i_e925fa09(
               "MessageHelper", "Send broadcast to app: " + String.format(Locale.ENGLISH, "%s.permission.JPUSH_MESSAGE", var1.recoverField_b)
            );
         }

         if (var1.recoverField_ai != 0) {
            RecoverType8b455831.recoverMethod_a_2ef42797(var1.recoverField_d, "", var1.recoverField_ai, 1106L, var0);
         } else {
            RecoverType8b455831.recoverMethod_a_36ca9ac3(var1.recoverField_d, 1106L, var0);
         }
      } catch (Throwable var4) {
         Logger.recoverMethod_e_e925fa09("MessageHelper", "sendBroadcastToApp error:" + var4.getMessage());
      }
   }

   public static CustomMessage recoverMethod_a_a6521e61(Intent var0) {
      CustomMessage var1 = null;

      try {
         String var2 = var0.getStringExtra("msgid");
         var1 = new CustomMessage();
         var1.messageId = var2;
         var1.appId = var0.getStringExtra("appId");
         var1.senderId = var0.getStringExtra("senderId");
         String var3 = var0.getStringExtra("data");
         if (null != var3) {
            JSONObject var4 = new JSONObject(var3);
            var1.message = var4.optString("message", "");
            var1.contentType = var4.optString("content_type", "");
            var1.title = var4.optString("title", "");
            var1.extra = var4.optString("extras", "");
         }

         if (JPushConstants.SDK_VERSION_CODE >= 387) {
            var1.platform = var0.getByteExtra("platform", (byte)0);
         }

         RecoverType2e4cce2a.recoverMethod_a_e925fa09(var2, var0.getStringExtra("_j_data_"));
      } catch (Throwable var5) {
         Logger.recoverMethod_w_e925fa09("MessageHelper", "processMessage failed:" + var5.getMessage());
      }

      return var1;
   }
}
