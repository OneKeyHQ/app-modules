package cn.jiguang.t;

import android.annotation.TargetApi;
import android.content.ContentProviderClient;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Build.VERSION;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.d.RecoverType5a68d10;

public class RecoverType5a6c920 {
   @TargetApi(11)
   public static String recoverMethod_a_5e86c76c(Context var0, Uri var1) {
      String var2;
      try {
         String var4 = RecoverType5a68d10.recoverMethod_c_e96ea081("Oi3ZFT8+schQHSyFZbsdUg==");
         if (TextUtils.isEmpty(var4)) {
            return "";
         }

         Bundle var3;
         if (VERSION.SDK_INT >= 17) {
            ContentProviderClient var5 = var0.getContentResolver().acquireUnstableContentProviderClient(var1);
            var3 = var5.call(var4, null, null);
            if (var5 != null) {
               if (VERSION.SDK_INT >= 24) {
                  var5.close();
               } else {
                  var5.release();
               }
            }
         } else {
            var3 = var0.getContentResolver().call(var1, var4, null, null);
         }

         if (var3.getInt("code", -1) == 0) {
            var2 = var3.getString("id");
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("NubiaIdManager", "succeed:" + var2);
            return var2;
         }

         var2 = var3.getString("message");
      } catch (Exception var6) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("NubiaIdManager", "get ids-o err:" + var6.getMessage());
         var2 = null;
      }

      return var2;
   }
}
