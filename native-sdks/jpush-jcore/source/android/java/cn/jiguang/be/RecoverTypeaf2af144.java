package cn.jiguang.be;

import android.content.Context;
import android.text.TextUtils;
import cn.jiguang.net.HttpRequest;
import cn.jiguang.net.HttpResponse;
import org.json.JSONObject;

public class RecoverTypeaf2af144 extends RecoverTypeaf2af145 {
   private String recoverField_a;
   private String recoverField_b;
   private int recoverField_c;
   private int recoverField_d;

   public RecoverTypeaf2af144(Context var1) {
      super(var1, "NET_HTTP");
   }

   @Override
   JSONObject recoverMethod_d_a7dbfe3() throws org.json.JSONException {
      JSONObject var1 = new JSONObject();
      var1.put("method", this.recoverField_a);
      var1.put("url", this.recoverField_b);
      var1.put("request_byte", this.recoverField_c);
      var1.put("response_byte", this.recoverField_d);
      return var1;
   }

   public void recoverMethod_a_ad38039f(HttpRequest var1, boolean var2) {
      this.recoverField_a = var2 ? "POST" : "GET";
      if (var2) {
         this.recoverField_b = var1.getUrl();
         this.recoverField_c = var1.getParas().length;
      } else {
         String var3 = var1.getUrl();
         String[] var4 = var3.split(" \\?");
         this.recoverField_b = var4[0];
         if (var4.length > 1) {
            this.recoverField_c = var4[1].getBytes().length;
         }
      }

      this.recoverMethod_e_9b75();
   }

   public void recoverMethod_a_d6d5a26b(HttpResponse var1) {
      this.recoverMethod_f_9b75();
      this.recoverMethod_d_13462e(var1.getStatusCode());
      if (var1.getStatusCode() == -1) {
         this.recoverMethod_c_13462e(var1.getResponseCode());
      }

      if (!TextUtils.isEmpty(var1.getResponseBody())) {
         this.recoverField_d = var1.getResponseBody().getBytes().length;
      }
   }
}
