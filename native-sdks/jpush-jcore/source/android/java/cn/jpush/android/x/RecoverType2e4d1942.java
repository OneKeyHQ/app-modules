package cn.jpush.android.x;

import android.content.Context;
import android.text.TextUtils;
import cn.jpush.android.ad.RecoverType9b4c5ae5;
import cn.jpush.android.ad.RecoverType9b4c5ae7;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.helper.RecoverType8b455831;
import cn.jpush.android.w.RecoverType2e4d157b;
import java.util.ArrayList;

public class RecoverType2e4d1942 {
   public static boolean recoverMethod_a_26692411(ArrayList var0, Context var1, String var2, String var3, boolean var4) {
      boolean var5 = true;
      Logger.recoverMethod_v_e925fa09("RichHelper", "action:loadHtmlImageResources - urlPrefix:" + var2);
      if (RecoverType9b4c5ae5.recoverMethod_c_aad3b203(var2) && var1 != null && var0.size() > 0 && !TextUtils.isEmpty(var3)) {
         for (Object recoveryElement_17 : var0) {
            String var7 = (String)recoveryElement_17;
            String var8 = var7;
            if (null != var8 && !var8.startsWith("http://") && !var8.startsWith("https://")) {
               var8 = var2 + var7;
            }

            if (RecoverType9b4c5ae5.recoverMethod_a_afa340b9(var8, false)) {
               byte[] var9 = RecoverType2e4d157b.recoverMethod_b_878599f5(var1, var8, "notify_h_img", 5, 5000L);
               if (var9 != null) {
                  try {
                     String var10 = var7;
                     String var11 = "";
                     if (var10.startsWith("http://") || var10.startsWith("https://")) {
                        var11 = var10.substring(0, var10.lastIndexOf("/") + 1);
                        var10 = RecoverType9b4c5ae7.recoverMethod_a_e7a3948b(var10, false);
                     }

                     String var12 = !var4 ? RecoverType9b4c5ae7.recoverMethod_b_a4ea6d43(var1, var3) : RecoverType9b4c5ae7.recoverMethod_c_a4ea6d43(var1, var3);
                     if (!TextUtils.isEmpty(var11)) {
                        var12 = var11.replaceAll(var2, var12);
                     }

                     var12 = var12 + var10;
                     RecoverType9b4c5ae7.recoverMethod_a_44c5a30a(var12, var9);
                     Logger.recoverMethod_v_e925fa09("RichHelper", "Succeed to load image - " + var12);
                  } catch (Exception var13) {
                     Logger.recoverMethod_ww_1765c98c("RichHelper", "Write storage error,  create img file fail.", var13);
                     var5 = false;
                  }
               } else {
                  RecoverType8b455831.recoverMethod_a_d7d77f79(var3, 1020L, null, var1);
                  var5 = false;
               }
            } else {
               var5 = false;
            }
         }
      }

      return var5;
   }
}
