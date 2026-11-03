package cn.jiguang.bi;

import android.text.TextUtils;
import cn.jiguang.api.JAction;
import cn.jiguang.api.JActionExtra;
import cn.jiguang.api.JCoreManager;
import java.util.HashMap;

public class RecoverTypeaf2b003f {
   public static HashMap recoverField_a = new HashMap();
   public static HashMap recoverField_b = new HashMap();
   public static HashMap recoverField_c = new HashMap();
   private static final Object recoverField_d = new Object();

   private RecoverTypeaf2b003f() {
   }

   public static RecoverTypeaf2b003f recoverMethod_a_c69f6d47() {
      return RecoverTypeaf2b003f.Member906bf13c.recoverField_a;
   }

   public void recoverMethod_a_e925fa09(String var1, String var2) {
      RecoverTypeaf2b0042.recoverMethod_a_e925fa09("DispacthManager", "addAction type:" + var1 + ",action:" + var2);
      if (!TextUtils.isEmpty(var2)) {
         if (!recoverField_a.containsKey(var1)) {
            Object var3 = null;
            var3 = new RecoverTypeaf2b0045();
            JCoreManager.addDispatchAction(var1, RecoverTypeaf2b0045.class.getCanonicalName());
            recoverField_a.put(var1, var3);
         }

         if (!recoverField_b.containsKey(var1)) {
            try {
               Object var6 = Class.forName(var2).newInstance();
               if (var6 instanceof JAction) {
                  recoverField_b.put(var1, (JAction)var6);
               }
            } catch (Throwable var4) {
               RecoverTypeaf2b0042.recoverMethod_d_e925fa09("DispacthManager", "#unexcepted - instance " + var2 + " class failed:" + var4);
            }
         }
      }
   }

   public void recoverMethod_b_e925fa09(String var1, String var2) {
      RecoverTypeaf2b0042.recoverMethod_a_e925fa09("DispacthManager", "addActionExtra type:" + var1 + ",action:" + var2);
      if (!TextUtils.isEmpty(var2)) {
         if (!recoverField_a.containsKey(var1)) {
            RecoverTypeaf2b0045 var3 = new RecoverTypeaf2b0045();
            JCoreManager.addDispatchAction(var1, RecoverTypeaf2b0045.class.getCanonicalName());
            recoverField_a.put(var1, var3);
         }

         if (!recoverField_c.containsKey(var1)) {
            try {
               Object var5 = Class.forName(var2).newInstance();
               if (var5 instanceof JActionExtra) {
                  recoverField_c.put(var1, (JActionExtra)var5);
               }
            } catch (Throwable var4) {
               RecoverTypeaf2b0042.recoverMethod_d_e925fa09("DispacthManager", "#unexcepted - instance " + var2 + " class failed:" + var4);
            }
         }
      }
   }

   public JAction recoverMethod_a_7cfa4581(String var1) {
      return recoverField_b.containsKey(var1) ? (JAction)recoverField_b.get(var1) : null;
   }

   public JActionExtra recoverMethod_b_41abf0c5(String var1) {
      return recoverField_c.containsKey(var1) ? (JActionExtra)recoverField_c.get(var1) : null;
   }

   private static final class Member906bf13c {
      private static final RecoverTypeaf2b003f recoverField_a = new RecoverTypeaf2b003f();
   }
}
