package cn.jiguang.s;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.d.RecoverType5a68d10;

public class RecoverType5a6c55f {
   private Context recoverField_a;
   private String recoverField_b = "";

   public RecoverType5a6c55f(Context var1) {
      this.recoverField_a = var1;
      this.recoverField_b = RecoverType5a68d10.recoverMethod_c_e96ea081("SyfFpc71r1BITMlIo0m1Vt2cR3sdiPGnMd0WMSsF4yU8+J95KN/jHVtZShu2ONYO");
   }

   public String recoverMethod_a_e96ea081(String var1) {
      if (TextUtils.isEmpty(var1)) {
         return "";
      }

      String var2 = null;

      try {
         Uri var3 = Uri.parse(this.recoverField_b);
         Cursor var4 = this.recoverField_a.getContentResolver().query(var3, null, null, new String[]{var1}, null);
         if (var4 != null) {
            if (var4.moveToNext()) {
               var2 = var4.getString(var4.getColumnIndex("value"));
            }

            var4.close();
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("MeizuDB", "return cursor is null,return");
         }
      } catch (Exception var5) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("MeizuDB", "getId error: " + var5.getMessage());
      }

      return var2;
   }
}
