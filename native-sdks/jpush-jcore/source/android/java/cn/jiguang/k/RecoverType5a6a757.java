package cn.jiguang.k;

import android.content.Context;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.api.ReportCallBack;
import cn.jiguang.i.RecoverType5a69fd6;

public class RecoverType5a6a757 implements ReportCallBack {
   private Context recoverField_a;
   private String recoverField_b;
   private String recoverField_c;

   public RecoverType5a6a757(Context var1, String var2, String var3) {
      this.recoverField_a = var1;
      this.recoverField_b = var2;
      this.recoverField_c = var3;
   }

   @Override
   public void onFinish(int var1) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("DeviceReport", "report finish code:" + var1);
      if (var1 == 0) {
         RecoverType5a69fd6.recoverMethod_f_5ffc00fd(this.recoverField_a, this.recoverField_c);
         if (!TextUtils.isEmpty(this.recoverField_b)) {
            RecoverType5a69fd6.recoverMethod_r_5ffc00fd(this.recoverField_a, this.recoverField_b);
         }
      }
   }
}
