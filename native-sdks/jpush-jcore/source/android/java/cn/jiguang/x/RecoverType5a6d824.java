package cn.jiguang.x;

import android.content.Context;
import android.os.Build.VERSION;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.d.RecoverType5a68d10;
import java.lang.reflect.Method;
import org.json.JSONObject;

public class RecoverType5a6d824 {
   private static String recoverField_a = null;
   private static String recoverField_b = null;
   private static String recoverField_c = null;

   public static String recoverMethod_a_e501eb0d(Context var0) {
      try {
         if (null == recoverField_b) {
            recoverField_c = "";
            recoverField_a = "";
            recoverField_b = "";
            String var1 = RecoverType5a68d10.recoverMethod_c_e96ea081("KIHhQylffktk79IAY9r8iabG5YlYSM/WswW6fWtc2r2WbTdKR65l5atPPp2u96wd");
            Class var2 = recoverMethod_a_e149f7aa(var0, var1);
            Object var3 = var2.newInstance();
            String var4 = RecoverType5a68d10.recoverMethod_c_e96ea081("Oi3ZFT8+schQHSyFZbsdUg==");
            Method var5 = var2.getMethod(var4, Context.class);
            Object var6 = var5.invoke(var3, var0);
            if (var6 instanceof String) {
               recoverField_b = (String)var6;
            }
         }

         JSONObject var8 = new JSONObject();
         var8.put("joad", recoverField_b);
         return var8.toString();
      } catch (Throwable var7) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("IdProvider", "getIds failed:" + var7.getMessage());
         return "";
      }
   }

   private static Class recoverMethod_a_e149f7aa(Context var0, String var1) throws java.lang.ClassNotFoundException {
      if (var1 != null && var1.trim().length() != 0) {
         boolean var2 = var0 != null;
         if (var2 && VERSION.SDK_INT >= 29) {
            try {
               return var0.getClassLoader().loadClass(var1);
            } catch (ClassNotFoundException var5) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("IdProvider", "loadClass error, " + var5.getMessage());
            }
         }

         try {
            return Class.forName(var1);
         } catch (ClassNotFoundException var4) {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09(
               "IdProvider", String.format("loadClass fail hasContext= %s, errMsg = %s", var2, var4.getLocalizedMessage())
            );
            throw new ClassNotFoundException("loadClass fail ", var4);
         }
      } else {
         throw new ClassNotFoundException("class is empty");
      }
   }
}
