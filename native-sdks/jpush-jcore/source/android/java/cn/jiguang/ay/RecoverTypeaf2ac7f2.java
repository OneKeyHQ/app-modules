package cn.jiguang.ay;

import android.content.Context;
import android.text.TextUtils;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bk.RecoverTypeaf2b07c8;
import cn.jiguang.bk.RecoverTypeaf2b07c9;
import cn.jiguang.bk.RecoverTypeaf2b07d4;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import org.json.JSONObject;

public class RecoverTypeaf2ac7f2 {
   private String recoverField_a;
   private int recoverField_b;
   private String recoverField_c;
   private String recoverField_d;
   private String recoverField_e;
   private long recoverField_f;
   private String recoverField_g;

   public RecoverTypeaf2ac7f2 recoverMethod_a_80115e7e(String var1) {
      this.recoverField_a = var1;
      return this;
   }

   public RecoverTypeaf2ac7f2 recoverMethod_a_7ad8b4ad(int var1) {
      this.recoverField_b = var1;
      return this;
   }

   public RecoverTypeaf2ac7f2 recoverMethod_b_80115e7e(String var1) {
      this.recoverField_c = var1;
      return this;
   }

   public RecoverTypeaf2ac7f2 recoverMethod_c_80115e7e(String var1) {
      this.recoverField_d = var1;
      return this;
   }

   public RecoverTypeaf2ac7f2 recoverMethod_d_80115e7e(String var1) {
      this.recoverField_e = var1;
      return this;
   }

   public RecoverTypeaf2ac7f2 recoverMethod_a_4883166e(long var1) {
      this.recoverField_f = var1;
      return this;
   }

   public RecoverTypeaf2ac7f2 recoverMethod_e_80115e7e(String var1) {
      this.recoverField_g = var1;
      return this;
   }

   public void recoverMethod_a_f0554df3(Context var1) {
      try {
         JSONObject var2 = new JSONObject();
         var2.put("deviceId", this.recoverField_a);
         var2.put("imei", this.recoverField_c);
         var2.put("androidId", this.recoverField_d);
         var2.put("uuid", this.recoverField_e);
         var2.put("genIdTimeStamp", this.recoverField_f);
         String var3 = RecoverTypeaf2b07d4.recoverMethod_a_8ea82db7(var2.toString(), RecoverType5a681cd.recoverField_i);
         RecoverType5a690d2.recoverMethod_a_abcb8d40(var1, RecoverType5a690d1.recoverMethod_c_48794abf().recoverMethod_a_4e9722a3(var3));
      } catch (Throwable var4) {
      }
   }

   public void recoverMethod_b_f0554df3(Context var1) {
      try {
         String var2 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var1, RecoverType5a690d1.recoverMethod_c_48794abf());
         if (TextUtils.isEmpty(var2)) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("RegKeyInfo", "no sp cid info");
            return;
         }

         String var3 = RecoverTypeaf2b07d4.recoverMethod_b_8ea82db7(var2, RecoverType5a681cd.recoverField_i);
         this.recoverMethod_a_7ad8b4ad(RecoverTypeaf2b07c8.recoverField_a);
         JSONObject var4 = new JSONObject(var3);
         var4.put("deviceIdFromType", this.recoverField_b);
         var4.put("newImei", this.recoverField_g);
         String var5 = RecoverTypeaf2b07d4.recoverMethod_a_8ea82db7(var4.toString(), RecoverType5a681cd.recoverField_i);
         if (!TextUtils.isEmpty(var5)) {
            RecoverTypeaf2b07c9.recoverMethod_a_e925fa0d("r_key_info", var5);
         }

         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("RegKeyInfo", "save reg key info: " + var4);
      } catch (Throwable var6) {
      }
   }

   public String recoverMethod_c_e501eb0d(Context var1) {
      String var2 = RecoverTypeaf2b07c9.recoverMethod_e_e96ea081("r_key_info");
      if (TextUtils.isEmpty(var2)) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("RegKeyInfo", "no reg info in cache");
         return "";
      } else {
         String var3 = RecoverTypeaf2b07d4.recoverMethod_b_8ea82db7(var2, RecoverType5a681cd.recoverField_i);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("RegKeyInfo", "get reg key info: " + var3);
         return !TextUtils.isEmpty(var3) ? var3 : "";
      }
   }

   @Override
   public String toString() {
      return "RegKeyInfo{deviceId='"
         + this.recoverField_a
         + '\''
         + ", deviceIdFromType="
         + this.recoverField_b
         + ", imei='"
         + this.recoverField_c
         + '\''
         + ", androidId='"
         + this.recoverField_d
         + '\''
         + ", uuid='"
         + this.recoverField_e
         + '\''
         + ", genIdTimeStamp="
         + this.recoverField_f
         + ", newImei='"
         + this.recoverField_g
         + '\''
         + '}';
   }
}
