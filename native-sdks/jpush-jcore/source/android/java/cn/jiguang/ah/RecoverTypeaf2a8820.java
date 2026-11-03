package cn.jiguang.ah;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.text.TextUtils;
import cn.jiguang.ai.RecoverTypeaf2a8be2;
import cn.jiguang.ak.RecoverTypeaf2a9364;
import cn.jiguang.ak.RecoverTypeaf2a9365;
import cn.jiguang.ak.RecoverTypeaf2a9367;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bk.RecoverTypeaf2b07c9;
import cn.jiguang.i.RecoverType5a69fd6;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class RecoverTypeaf2a8820 {
   public static void recoverMethod_a_3c4029ab(Context var0, JSONObject var1) {
      try {
         if (var1 == null) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeCmd", "executeWakeAction: [JWakeCmd] from heartBeat");
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeCmd", "executeWakeAction: [JWakeCmd] from cmd");
         }

         boolean var2 = (Boolean)RecoverTypeaf2a881f.recoverMethod_c_f23f0d9f(var0);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeCmd", "JWakeCmd isActionUserEnable:" + var2);
         if (var2) {
            RecoverTypeaf2aa628.recoverMethod_b_a4fc3973(new RecoverTypeaf2a8820.Member8ea904dd(var0, var1));
         }
      } catch (Throwable var3) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JWakeCmd", "[executeWakeAction] failed:" + var3.getMessage());
      }
   }

   private static void recoverMethod_c_3c4029ab(Context var0, JSONObject var1) {
      try {
         RecoverType5a69fd6.recoverMethod_d_5ffc00fd(var0, "JWakeCmdcmd");
         String var2 = RecoverTypeaf2b07c9.recoverMethod_e_e96ea081("bwct.catch.v2");
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeCmd", "read cmd wakeTarget:" + var2);
         List var3 = null;
         if (var1 != null) {
            JSONObject var4;
            try {
               var4 = var1.getJSONObject("content");
            } catch (Throwable var12) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakeCmd", "stop wake,the json form cmd is illegal:" + var12.getMessage());
               return;
            }

            int var5 = var4.optInt("type", 1);
            String var6 = var4.optString("pkgName", "");
            String var7 = var4.optString("serviceName", "");
            int var8 = var4.optInt("delay_time");
            if (TextUtils.isEmpty(var7) || TextUtils.isEmpty(var6)) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakeCmd", "Incomplete data:" + var1);
               return;
            }

            if (var5 == 1) {
               ApplicationInfo var9 = null;

               try {
                  var9 = var0.getPackageManager().getApplicationInfo(var6, 128);
               } catch (Throwable var11) {
                  RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("JWakeCmd", "not found application:" + var6);
               }

               if (var9 != null) {
                  var3 = new ArrayList();
                  RecoverTypeaf2a8be2 var10 = new RecoverTypeaf2a8be2();
                  var10.recoverField_a = var6;
                  var10.recoverField_b = var7;
                  var10.recoverField_c = var9.targetSdkVersion;
                  var10.recoverField_j = 1;
                  var10.recoverField_k = var8;
                  var10.recoverField_g = 4;
                  var3.add(var10);
               }
            }

            List var15 = RecoverTypeaf2a9365.recoverMethod_a_48092677(var2, var6, var7, var5);
            String var16 = RecoverTypeaf2a9364.recoverMethod_a_184e2efa(var15);
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JWakeCmd", "write cmd wakeTarget:" + var16);
            RecoverTypeaf2b07c9.recoverMethod_a_e925fa0d("bwct.catch.v2", var16);
         } else {
            if (var2 == null || TextUtils.isEmpty(var2)) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakeCmd", "there are no cache cmd wakeTarget");
               return;
            }

            var3 = RecoverTypeaf2a9364.recoverMethod_a_c0060268(var2);
            var3 = RecoverTypeaf2a9364.recoverMethod_a_c98d58e3(var0, var3);
         }

         if (var3 != null) {
            RecoverTypeaf2a9367.recoverMethod_a_c6923f1f().recoverMethod_a_fa4794e4(var0, var3);
         }
      } catch (Throwable var13) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakeCmd", "parse throwable:" + var13.getMessage());
      }
   }

   public static class Member8ea904dd extends RecoverTypeaf2b0401 {
      private Context recoverField_a;
      private JSONObject recoverField_b;

      Member8ea904dd(Context var1, JSONObject var2) {
         this.recoverField_a = var1;
         this.recoverField_b = var2;
         this.recoverField_h = "JWakeCmd#WakeAction";
      }

      @Override
      public void recoverMethod_a_9b75() {
         try {
            long var1 = RecoverType5a69fd6.recoverMethod_e_5ffc00f1(this.recoverField_a, "JWakeCmdcmd");
            long var3 = System.currentTimeMillis();
            if (this.recoverField_b == null && var3 - var1 < 3600000L) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakeCmd", "is not cmd wake time");
            } else {
               RecoverTypeaf2a8820.recoverMethod_c_3c4029ab(this.recoverField_a, this.recoverField_b);
            }
         } catch (Throwable var5) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JWakeCmd", "WakeAction failed:" + var5.getMessage());
         }
      }
   }
}
