package cn.jiguang.ao;

import android.text.TextUtils;
import cn.jiguang.bk.RecoverTypeaf2b07c9;
import cn.jiguang.bk.RecoverTypeaf2b07cd;
import java.io.File;
import java.util.Set;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecoverTypeaf2aa268 {
   private File recoverField_c;
   private JSONObject recoverField_d;
   public long recoverField_a;
   public JSONObject recoverField_b;
   private boolean recoverField_e;
   private boolean recoverField_f;

   private RecoverTypeaf2aa268(File var1, JSONObject var2, JSONObject var3, boolean var4) {
      this.recoverField_c = var1;
      this.recoverField_d = var2;
      this.recoverField_b = var3;
      this.recoverField_a = var1.length();
      this.recoverField_f = var4;
      this.recoverField_e = var4;
   }

   public static RecoverTypeaf2aa268 recoverMethod_a_e1c46bdb(File var0, JSONObject var1) {
      JSONObject var2 = recoverMethod_a_588a971a(var0);
      if (var2 != null) {
         return new RecoverTypeaf2aa268(var0, var2, var1, true);
      }

      RecoverTypeaf2b07c9.recoverMethod_b_4ad1f430(var0);
      return null;
   }

   public static RecoverTypeaf2aa268 recoverMethod_a_b74b45dc(File var0, Set var1) {
      JSONObject var2 = recoverMethod_a_588a971a(var0);
      if (var2 != null) {
         return new RecoverTypeaf2aa268(var0, var2, RecoverTypeaf2b07cd.recoverMethod_a_d3ac7824(var2, var1), false);
      }

      RecoverTypeaf2b07c9.recoverMethod_b_4ad1f430(var0);
      return null;
   }

   public static JSONObject recoverMethod_a_588a971a(File var0) {
      try {
         String var1 = RecoverTypeaf2b07c9.recoverMethod_c_af215df4(var0);
         if (!TextUtils.isEmpty(var1)) {
            JSONObject var2 = new JSONObject(var1);
            JSONArray var3 = var2.optJSONArray("content");
            if (var3 != null && var3.length() > 0) {
               return var2;
            }
         }
      } catch (Throwable var4) {
      }

      return null;
   }

   public boolean recoverMethod_a_a6e15dff(RecoverTypeaf2aa268 var1, File var2) {
      try {
         if (var1 != null && this.recoverField_a + var1.recoverField_a <= 40960L) {
            JSONArray var7 = this.recoverField_d.getJSONArray("content");
            JSONArray var8 = var1.recoverField_d.getJSONArray("content");

            for (int var5 = 0; var5 < var8.length(); var5++) {
               var7.put(var8.getJSONObject(var5));
            }

            this.recoverField_a = this.recoverField_a + var1.recoverField_a;
            this.recoverField_e = true;
            RecoverTypeaf2b07c9.recoverMethod_b_4ad1f430(var1.recoverField_c);
            return true;
         }

         if (!var2.exists()) {
            var2.mkdirs();
         }

         File var3 = new File(var2, this.recoverField_c.getName());
         if (this.recoverField_f) {
            RecoverTypeaf2b07cd.recoverMethod_a_e0cf9bf5(this.recoverField_d, this.recoverField_b);
         }

         boolean var4 = !this.recoverField_c.equals(var3);
         if (this.recoverField_e || var4) {
            RecoverTypeaf2b07c9.recoverMethod_b_d9caeafa(var3, this.recoverField_d.toString());
         }

         if (var4) {
            RecoverTypeaf2b07c9.recoverMethod_b_4ad1f430(this.recoverField_c);
         }
      } catch (Throwable var6) {
      }

      return false;
   }
}
