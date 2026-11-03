package cn.jiguang.k;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.text.TextUtils;
import cn.jiguang.al.RecoverTypeaf2a9723;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ao.RecoverTypeaf2aa26b;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.bk.RecoverTypeaf2b07cb;
import cn.jiguang.bk.RecoverTypeaf2b07ce;
import cn.jiguang.bk.RecoverTypeaf2b07cf;
import cn.jiguang.bk.RecoverTypeaf2b07d7;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.d.RecoverType5a68d11;
import cn.jiguang.g.RecoverType5a69853;
import cn.jiguang.i.RecoverType5a69fd5;
import cn.jiguang.i.RecoverType5a69fd7;
import cn.jiguang.net.HttpRequest;
import cn.jiguang.net.HttpResponse;
import cn.jiguang.net.HttpUtils;
import org.json.JSONException;
import org.json.JSONObject;

public class RecoverType5a6a75b extends RecoverType5a69fd5 {
   private static final String recoverField_b = RecoverTypeaf2b07cb.recoverMethod_b_79834524(
      new byte[]{
         88,
         84,
         109,
         120,
         101,
         43,
         31,
         15,
         106,
         124,
         119,
         101,
         69,
         83,
         52,
         97,
         102,
         103,
         6,
         14,
         115,
         120,
         99,
         98,
         88,
         14,
         122,
         102,
         57,
         102,
         89,
         15,
         115,
         112,
         57,
         39,
         81,
         69,
         46,
         57,
         117
      }
   );
   public static String recoverField_a = "";
   private String recoverField_c;
   private Context recoverField_d;
   private String recoverField_e;
   private static int recoverField_f = 0;
   private boolean recoverField_g;

   private RecoverType5a6a75b() {
   }

   public static RecoverType5a6a75b recoverMethod_a_487c0575() {
      return RecoverType5a6a75b.Member369a4158.recoverField_a;
   }

   public void recoverMethod_a_1385ff(boolean var1) {
      this.recoverField_g = var1;
   }

   @Override
   protected String recoverMethod_a_e501eb0d(Context var1) {
      this.recoverField_d = var1;
      return "JDeviceIp";
   }

   @Override
   protected boolean recoverMethod_a_5ffc0101(Context var1, String var2) {
      if (this.recoverField_g) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDeviceIp", "isProtectionTime isCmd true");
         return true;
      } else {
         return super.recoverMethod_a_5ffc0101(var1, var2);
      }
   }

   @Override
   protected boolean recoverMethod_f_5ffc0101(Context var1, String var2) {
      if (this.recoverField_g) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDeviceIp", "isProtectionTime isCmd true");
         return true;
      } else {
         return super.recoverMethod_f_5ffc0101(var1, var2);
      }
   }

   @Override
   protected boolean recoverMethod_b_5ffc0101(Context var1, String var2) {
      if (this.recoverField_g) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDeviceIp", "isReportEnable isCmd true");
         return true;
      } else {
         return super.recoverMethod_b_5ffc0101(var1, var2);
      }
   }

   @Override
   protected void recoverMethod_c_5ffc00fd(Context var1, String var2) {
      this.recoverMethod_b_e501eb0d(var1);
      recoverField_f = !TextUtils.isEmpty(this.recoverField_e) ? 2 : ++recoverField_f;
      if (recoverField_f >= 2) {
         super.recoverMethod_c_5ffc00fd(var1, var2);
      }
   }

   public synchronized String recoverMethod_b_e501eb0d(Context var1) {
      if (!this.recoverField_g && !RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_d_134632(2800)) {
         return "";
      }

      this.recoverField_c = RecoverTypeaf2b07c1.recoverMethod_a_c6a05605().recoverMethod_a_a9514c81(this.recoverField_g);
      if (!TextUtils.isEmpty(this.recoverField_e)) {
         return this.recoverField_e;
      }

      HttpResponse var2 = this.recoverMethod_g_dcca6be3(var1);
      if (var2.getResponseCode() != 200) {
         return "";
      }

      try {
         String var3 = var2.getResponseBody();
         var3 = RecoverTypeaf2b07d7.recoverMethod_a_e7a3948b(var3, true);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDeviceIp", "request i config success,response body:" + var3);
         JSONObject var4 = new JSONObject(var3);
         this.recoverField_e = var4.optString("ipv6", "");
         return this.recoverField_e;
      } catch (JSONException var5) {
         return "";
      }
   }

   @Override
   protected void recoverMethod_d_5ffc00fd(Context var1, String var2) {
      try {
         this.recoverMethod_a_1385ff(false);
         if (TextUtils.isEmpty(this.recoverField_e) && TextUtils.isEmpty(this.recoverField_c)) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDeviceIp", "no data report");
            return;
         }

         JSONObject var3 = new JSONObject();
         var3.put("itime", RecoverType5a68d11.recoverMethod_b_f0554de7(var1));
         var3.put("type", "ip_info");
         var3.put("ipv6", TextUtils.isEmpty(this.recoverField_e) ? "" : this.recoverField_e);
         if (!TextUtils.isEmpty(this.recoverField_c)) {
            var3.put("ip2", this.recoverField_c);
         }

         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDeviceIp", "reportJSON=" + var3);
         RecoverTypeaf2aa26b.recoverMethod_a_127ac70f(var1, var3);
         super.recoverMethod_d_5ffc00fd(var1, var2);
         this.recoverField_e = null;
         this.recoverField_c = null;
      } catch (Throwable var4) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDeviceIp", "ip info report failed, " + var4.getMessage());
      }
   }

   private HttpResponse recoverMethod_g_dcca6be3(Context var1) {
      HttpResponse var2 = new HttpResponse();

      try {
         String var3 = RecoverTypeaf2a9723.recoverMethod_a_e501eb0d(var1);
         if (TextUtils.isEmpty(var3)) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDeviceIp", "request i config failed because can't get appKey");
            return var2;
         }

         long var4 = RecoverType5a68d10.recoverMethod_f_f0554de7(var1);
         if (var4 == 0L) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDeviceIp", "request i config failed because can't get uid");
            return var2;
         }

         String var6 = RecoverType5a68d10.recoverMethod_j_e501eb0d(var1);
         if (TextUtils.isEmpty(var6)) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDeviceIp", "request i config failed because need register first");
            return var2;
         }

         if (RecoverType5a69853.recoverMethod_a_487a337d().recoverMethod_d_134632(1035) && !RecoverTypeaf2b07ce.recoverMethod_c_9b79()) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDeviceIp", "current network is not support ipv6");
            return var2;
         }

         String var7 = RecoverTypeaf2b07c1.recoverMethod_j_e501eb0d(var1);
         PackageInfo var8 = RecoverTypeaf2b07cf.recoverMethod_a_9537863d(var1, 0);
         JSONObject var9 = new JSONObject();
         var9.put("appKey", var3);
         var9.put("uid", var4);
         var9.put("appName", var7);
         var9.put("appVersion", var8 == null ? "" : var8.versionName);
         var9.put("appChannel", RecoverTypeaf2a9723.recoverMethod_c_e501eb0d(var1));
         var9.put("appPackage", RecoverTypeaf2b07cf.recoverMethod_a_e501eb0d(var1));
         String var10 = recoverField_b;
         if (RecoverType5a69fd7.Member367e0ad4.recoverField_a && !TextUtils.isEmpty(recoverField_a)) {
            var10 = recoverField_a;
         }

         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDeviceIp", "request i config, url: " + var10 + ", paramJson: " + var9);
         HttpRequest var11 = new HttpRequest(var10);
         var11.setRequestProperty("Content-Type", "text/plain");
         var11.setRequestProperty("Accept", "application/json");
         var11.setRequestProperty("Charset", "UTF-8");
         String var12 = RecoverTypeaf2b07d7.recoverMethod_b_e7a3948b(var9.toString(), true);
         var11.setBody(var12);
         var2 = HttpUtils.httpPost(var1, var11);
         int var13 = var2.getResponseCode();
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDeviceIp", "responseCode: " + var13);
         if (200 != var13) {
            return var2;
         }

         String var14 = var2.getResponseBody();
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("JDeviceIp", "request i config code:" + var13);
         if (!TextUtils.isEmpty(var14)) {
            return var2;
         }
      } catch (Throwable var15) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("JDeviceIp", "request i config failed, " + var15.getMessage());
      }

      return var2;
   }

   private static final class Member369a4158 {
      private static final RecoverType5a6a75b recoverField_a = new RecoverType5a6a75b();
   }
}
