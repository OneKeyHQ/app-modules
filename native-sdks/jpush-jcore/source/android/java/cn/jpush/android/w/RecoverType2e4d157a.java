package cn.jpush.android.w;

import android.text.TextUtils;
import cn.jpush.android.helper.Logger;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class RecoverType2e4d157a implements HostnameVerifier {
   public String recoverField_a = null;

   public RecoverType2e4d157a(String var1) {
      this.recoverField_a = var1;
   }

   @Override
   public boolean verify(String var1, SSLSession var2) {
      Logger.recoverMethod_d_e925fa09("DefaultHostVerifier", "host:" + var1 + ",checkHost:" + this.recoverField_a);
      return TextUtils.isEmpty(var1) ? false : TextUtils.equals(this.recoverField_a, var1);
   }
}
