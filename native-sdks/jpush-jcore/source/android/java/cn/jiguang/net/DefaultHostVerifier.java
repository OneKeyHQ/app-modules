package cn.jiguang.net;

import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

public class DefaultHostVerifier implements HostnameVerifier {
   public String recoverField_a = null;

   public DefaultHostVerifier(String var1) {
      this.recoverField_a = var1;
   }

   @Override
   public boolean verify(String var1, SSLSession var2) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("DefaultHostVerifier", "host:" + var1 + ",checkHost:" + this.recoverField_a);
      return TextUtils.isEmpty(var1) ? false : TextUtils.equals(this.recoverField_a, var1);
   }
}
