package cn.jiguang.push.asus;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import cn.jpush.android.a.RecoverType2e4cc2e5;
import cn.jpush.android.c.RecoverType2e4cca66;
import cn.jpush.android.e.RecoverType2e4cd1e8;
import cn.jpush.android.e.RecoverType2e4cd1e9;
import cn.jpush.android.f.RecoverType2e4cd5a9;
import cn.jpush.android.f.RecoverType2e4cd5aa;
import cn.jpush.android.f.RecoverType2e4cd5ab;
import cn.jpush.android.f.RecoverType2e4cd5ae;
import cn.jpush.android.h.RecoverType2e4cdd2b;
import cn.jpush.android.i.RecoverType2e4ce0ed;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class PushMessageReceiver extends BroadcastReceiver {
   private static ExecutorService recoverField_a;

   @Override
   public void onReceive(Context var1, Intent var2) {
      if (recoverField_a == null) {
         recoverField_a = Executors.newSingleThreadExecutor();
      }

      recoverField_a.execute(new RecoverTypebe7e3e91(this, var1, var2));
   }

   protected final void recoverMethod_a_c4d2ecb0(Context var1, Intent var2) {
      String var3 = var2.getAction();
      if ("com.ups.push.PUSH_RESPONSE".equals(var3)) {
         var3 = var2.getStringExtra("message_cmd");
         long var5 = var2.getLongExtra("message_rid", -1L);
         String var7 = var2.getStringExtra("message_content");
         this.recoverMethod_a_3b017c5(var1, var5, var3, var7);
      }
   }

   public void recoverMethod_a_3b017c5(Context var1, long var2, String var4, String var5) {
      RecoverType2e4ce0ed.recoverMethod_a_e925fa09("PushMessageReceiver", "receive cmd:" + var4 + ",rid:" + var2 + ",content:" + var5);
      JSONObject var6 = null;
      if (!TextUtils.isEmpty(var5)) {
         try {
            var6 = new JSONObject(var5);
         } catch (JSONException var8) {
            var8.printStackTrace();
         }
      }

      if (var6 == null) {
         RecoverType2e4ce0ed.recoverMethod_g_e925fa09("PushMessageReceiver", "handle json null");
      } else {
         try {
            this.recoverMethod_a_3bcbd2b7(var1, var4, var2, var6);
         } catch (Throwable var7) {
            var7.printStackTrace();
         }
      }
   }

   private void recoverMethod_a_3bcbd2b7(Context var1, String var2, long var3, JSONObject var5) throws org.json.JSONException {
      int var6 = 3;
      RecoverType2e4cca66 var7 = null;
      if (var5.has("cmd_msg_result")) {
         if ((var6 = var5.optInt("cmd_msg_result")) != 0) {
            RecoverType2e4ce0ed.recoverMethod_c_e925fa09("PushMessageReceiver", "errorCode:" + var6);
            if (var6 == 2) {
               var6 = RecoverType2e4cd5ae.recoverField_d;
            } else if (var6 == 5) {
               var6 = RecoverType2e4cd5ae.recoverField_c;
            } else if (var6 == 1) {
               var6 = RecoverType2e4cd5ae.recoverField_g;
            }
         }

         var7 = new RecoverType2e4cca66(var2, var6);
      }

      if (var5.has("cmd_msg_result_content")) {
         String var8 = var5.getString("cmd_msg_result_content");
         JSONObject var9 = null;

         try {
            var9 = new JSONObject(var8);
         } catch (Throwable var10) {
         }

         if (var9 != null) {
            var5 = var9;
         }
      }

      if (TextUtils.isEmpty(var2)) {
         this.recoverMethod_a_ac8548fc(var1, var7);
      } else {
         switch (var2) {
            case "ups.register":
               String var17 = var5.optString("registrationId", "");
               RecoverType2e4cd5aa var14;
               RecoverType2e4cd1e8 var16;
               if ((var16 = RecoverType2e4cd5a9.recoverMethod_a_66272223(var14 = new RecoverType2e4cd5aa(var2, var3))) != null) {
                  RecoverType2e4cd5a9.recoverMethod_b_7757a73a(var14);
               }

               if (!TextUtils.isEmpty(var17)) {
                  RecoverType2e4ce0ed.recoverMethod_b_e925fa09("PushMessageReceiver", "register succ.");
                  if (var16 != null) {
                     var16.recoverMethod_a_5d527811(new RecoverType2e4cd1e9(0, var17, "successs"));
                  }

                  this.recoverMethod_a_5ffc00fd(var1, var17);
                  RecoverType2e4cc2e5.recoverMethod_a_aad3b1ff(var17);
                  return;
               }

               if (var6 != 1) {
                  this.recoverMethod_a_ac8548fc(var1, var7);
                  if (var16 != null) {
                     var16.recoverMethod_a_5d527811(new RecoverType2e4cd1e9(var7.recoverMethod_a_9b68(), "", "failed"));
                     return;
                  }
               }
               break;
            case "ups.unregister":
               if (var6 == 0) {
                  RecoverType2e4cc2e5.recoverMethod_a_aad3b1ff("");
               }

               RecoverType2e4cd5aa var11;
               RecoverType2e4cd1e8 var13;
               if ((var13 = RecoverType2e4cd5a9.recoverMethod_a_66272223(var11 = new RecoverType2e4cd5aa(var2, var3))) != null) {
                  RecoverType2e4cd5a9.recoverMethod_b_7757a73a(var11);
                  var13.recoverMethod_a_5d527811(new RecoverType2e4cd1e9(var6, "", var6 == 0 ? "success" : "failed"));
                  return;
               }
               break;
            case "ups.login":
               RecoverType2e4ce0ed.recoverMethod_b_e925fa09("PushMessageReceiver", "logined");
               if (!TextUtils.isEmpty(RecoverType2e4cd5ab.recoverField_b)) {
                  if (TextUtils.isEmpty(RecoverType2e4cc2e5.recoverMethod_b_e501eb0d(var1))) {
                     RecoverType2e4cdd2b.recoverMethod_a_d32e5282(var1, RecoverType2e4cd5ab.recoverField_b, null);
                  }

                  return;
               }
               break;
            case "ups.pushStatus":
               Bundle var12 = new Bundle();
               boolean var15 = var5.optBoolean("isPushStopped", false);
               var12.putBoolean("isPushStopped", var15);
               if (var7 != null) {
                  var7.recoverMethod_a_79e5e33f(var12);
               }
            case "ups.turnOff":
            case "ups.turnOn":
               this.recoverMethod_a_ac8548fc(var1, var7);
               return;
            default:
               RecoverType2e4ce0ed.recoverMethod_d_e925fa09("PushMessageReceiver", "unkown cmd:" + var2);
         }
      }
   }

   public abstract void recoverMethod_a_5ffc00fd(Context var1, String var2);

   public abstract void recoverMethod_a_ac8548fc(Context var1, RecoverType2e4cca66 var2);
}
