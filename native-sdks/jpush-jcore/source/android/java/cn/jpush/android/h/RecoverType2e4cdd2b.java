package cn.jpush.android.h;

import android.content.Context;
import android.text.TextUtils;
import cn.jpush.android.a.RecoverType2e4cc2e5;
import cn.jpush.android.e.RecoverType2e4cd1e9;
import cn.jpush.android.e.RecoverType2e4cd1ea;
import cn.jpush.android.f.RecoverType2e4cd5a9;
import cn.jpush.android.f.RecoverType2e4cd5aa;
import cn.jpush.android.f.RecoverType2e4cd5ab;
import cn.jpush.android.f.RecoverType2e4cd5ae;
import cn.jpush.android.i.RecoverType2e4ce0ed;
import org.json.JSONObject;

public final class RecoverType2e4cdd2b {
   public static void recoverMethod_a_d32e5282(Context var0, String var1, RecoverType2e4cd1ea var2) {
      if (TextUtils.isEmpty(var1)) {
         RecoverType2e4ce0ed.recoverMethod_g_e925fa09("PushPlatformManager", "appId is empty");
         if (var2 != null) {
            var2.recoverMethod_a_5d527811(new RecoverType2e4cd1e9(RecoverType2e4cd5ae.recoverField_b, "", "appId is empty"));
         }
      } else if (var1.length() != 24) {
         RecoverType2e4ce0ed.recoverMethod_g_e925fa09("PushPlatformManager", "Invalid appId : " + var1 + ", Please check your appId again!");
         if (var2 != null) {
            var2.recoverMethod_a_5d527811(new RecoverType2e4cd1e9(RecoverType2e4cd5ae.recoverField_b, "", "Invalid appId"));
         }
      } else {
         try {
            RecoverType2e4cd5ab.recoverField_b = var1;
            RecoverType2e4ce0ed.recoverMethod_b_e925fa09("PushPlatformManager", "register: appId - " + var1);
            JSONObject var3;
            (var3 = new JSONObject()).put("JPUSH_APPKEY", var1);
            boolean var8 = RecoverType2e4cc2e5.recoverMethod_c_f0554df7(var0);
            var3.put("FIRST_REQUESTA", var8);
            RecoverType2e4cc2e5.recoverMethod_d_f0554df3(var0);
            long var5 = RecoverType2e4cd5ab.recoverMethod_a_9b69();
            RecoverType2e4cd5a9.recoverMethod_a_fb7a1441(new RecoverType2e4cd5aa("ups.register", var5), var2);
            RecoverType2e4cd5ab.recoverMethod_a_3bcbd2b7(var0, "ups.register", var5, var3);
         } catch (Throwable var7) {
            var7.printStackTrace();
         }
      }
   }
}
