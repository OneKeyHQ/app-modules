package cn.jpush.android.n;

import android.text.TextUtils;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.local.JPushResponse;
import java.nio.ByteBuffer;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverType2e4cf3b4 extends JPushResponse {
   private int recoverField_a = -1;
   private JSONObject recoverField_b;
   private JSONArray recoverField_c;

   public RecoverType2e4cf3b4(int var1, long var2, long var4, ByteBuffer var6) {
      super(var1, var2, var4, var6);
      this.parseBody();
   }

   public RecoverType2e4cf3b4(JPushResponse var1) {
      this(var1.getCmd(), var1.getRid(), var1.rquestId, var1.getBody());
   }

   @Override
   protected void parseBody() {
      try {
         if (null == this.body) {
            Logger.recoverMethod_ww_e925fa09("GeoPullResponse", "geo pull response empty");
            return;
         }

         this.recoverField_a = this.body.getShort();
         if (this.recoverField_a != 0) {
            Logger.recoverMethod_ww_e925fa09("GeoPullResponse", "geo pull response error code :" + this.recoverField_a);
            return;
         }

         short var1 = this.body.getShort();
         byte[] var2 = new byte[var1];
         this.body.get(var2);
         String var3 = new String(var2, "UTF-8");
         Logger.recoverMethod_d_e925fa09("GeoPullResponse", "receive content：" + var3);
         if (!TextUtils.isEmpty(var3)) {
            JSONObject var4 = new JSONObject(var3);
            this.recoverField_c = var4.getJSONArray("geofence");
            var4.remove("geofence");
            this.recoverField_b = var4;
         }
      } catch (Throwable var5) {
         Logger.recoverMethod_ww_e925fa09("GeoPullResponse", "parse geo pull response failed");
      }
   }

   public JSONObject recoverMethod_a_a7dbfe3() {
      return this.recoverField_b;
   }

   public JSONArray recoverMethod_b_6d78e11b() {
      return this.recoverField_c;
   }

   @Override
   public String toString() {
      return "[GeoPullResponse] - limit:" + this.recoverField_b + " - geoArray:" + this.recoverField_c + " - " + super.toString();
   }
}
