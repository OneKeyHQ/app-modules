package cn.jiguang.common.app.helper;

import android.annotation.SuppressLint;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

@SuppressLint("PackageManagerGetSignatures")
public class RecoverType82ecf7ac {
   private static final byte[] recoverField_d = new byte[]{102, 124, 16, 76, 112, 123, 98, 49, 64, 65, 122, 99, 119, 118, 85};
   private static final byte[] recoverField_e = new byte[]{111, 115, 101, 121, 78, 106, 124, 119, 125, 92, 69, 125, 88, 119, 114, 91, 65, 126, 109, 101};
   private static final HashMap recoverField_f = new HashMap();
   private static final HashMap recoverField_g = new HashMap();
   private static String recoverField_h = "JAppHelper";
   private static List recoverField_i = new ArrayList();
   public static int recoverField_a = 1;
   public static int recoverField_b = 2;
   public static int recoverField_c = recoverField_b;

   public static ArrayList recoverMethod_a_48eba5e9(JSONArray var0) {
      if (var0 != null && var0.length() != 0) {
         try {
            int var1 = 0;
            JSONArray var2 = new JSONArray();
            ArrayList var3 = new ArrayList();

            for (int var4 = 0; var4 < var0.length(); var4++) {
               JSONObject var5 = var0.optJSONObject(var4);
               if (var5 != null && var5.length() != 0) {
                  int var6 = var5.toString().getBytes("UTF-8").length;
                  if (var1 + var6 > 102400) {
                     if (var2.length() > 0) {
                        var3.add(var2);
                     }

                     var2 = new JSONArray();
                     var2.put(var5);
                     var1 = var6;
                  } else {
                     var2.put(var5);
                     var1 += var6;
                  }
               }
            }

            if (var2.length() > 0) {
               var3.add(var2);
            }

            return var3;
         } catch (UnsupportedEncodingException var7) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09(recoverField_h, "partition exception:" + var7.getMessage());
         } catch (Throwable var8) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09(recoverField_h, "partition throwable:" + var8.getMessage());
         }

         return null;
      } else {
         return null;
      }
   }
}
