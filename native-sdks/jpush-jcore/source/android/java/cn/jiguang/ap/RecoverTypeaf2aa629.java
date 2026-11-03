package cn.jiguang.ap;

import android.content.Context;
import android.os.Bundle;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.bh.RecoverTypeaf2afc7e;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class RecoverTypeaf2aa629 {
   private static String recoverField_a = "JDataControlManager";
   private ConcurrentHashMap recoverField_b = new ConcurrentHashMap();
   private static String recoverField_c = "DISABLE_DATA";
   private static String recoverField_d = "ENABLE_DATA";
   private Context recoverField_e;

   private RecoverTypeaf2aa629() {
   }

   public static RecoverTypeaf2aa629 recoverMethod_a_c694849d() {
      return RecoverTypeaf2aa629.Member8f19c4a6.recoverField_a;
   }

   public synchronized Bundle recoverMethod_a_99967dbb(Context var1, String var2, Bundle var3) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(recoverField_a, "setDataControl control=" + var3 + " sdkType=" + var2);
      this.recoverField_e = var1;
      ConcurrentHashMap var4 = (ConcurrentHashMap)this.recoverField_b.get(var2);
      if (null == var4) {
         var4 = new ConcurrentHashMap();
      }

      Set var5 = (Set)var4.get("enable_c_c");
      var5 = var5 == null ? new HashSet() : var5;
      ArrayList var6 = var3.getIntegerArrayList("enable_c_c");
      Set var7 = (Set)var4.get("disable_c_c");
      var7 = var7 == null ? new HashSet() : var7;
      ArrayList var8 = var3.getIntegerArrayList("disable_c_c");
      if (var8 != null) {
         var7.addAll(var8);
      }

      if (var6 != null) {
         for (Object recoveryElement_46 : var6) {
            int var10 = (int)recoveryElement_46;
            var7.remove(var10);
         }
      }

      if (var8 != null) {
         for (Object recoveryElement_52 : var8) {
            int var14 = (int)recoveryElement_52;
            var5.remove(var14);
         }
      }

      if (var6 != null) {
         var5.addAll(var6);
      }

      var4.put("enable_c_c", var5);
      var4.put("disable_c_c", var7);
      this.recoverField_b.put(var2, var4);
      return this.recoverMethod_b_88d0e4f9();
   }

   public Bundle recoverMethod_b_88d0e4f9() {
      try {
         ArrayList var1 = new ArrayList();
         ArrayList var2 = new ArrayList();
         Bundle var3 = new Bundle();
         ConcurrentHashMap var4;
         synchronized (this) {
            var4 = new ConcurrentHashMap(this.recoverField_b);
         }

         if (!var4.isEmpty()) {
            for (Object recoveryElement_78 : var4.entrySet()) {
               Entry var6 = (Entry)recoveryElement_78;
               ConcurrentHashMap var7 = (ConcurrentHashMap)var6.getValue();
               Set var8 = (Set)var7.get("enable_c_c");
               if (var8 != null) {
                  var1.addAll(new ArrayList(var8));
               }

               Set var9 = (Set)var7.get("disable_c_c");
               if (var9 != null) {
                  var2.addAll(new ArrayList(var9));
               }
            }

            var3.putIntegerArrayList(recoverField_c, var2);
            var3.putIntegerArrayList(recoverField_d, var1);
         }

         if (!var2.isEmpty()) {
            for (Object recoveryElement_96 : var2) {
               int var13 = (int)recoveryElement_96;
               RecoverTypeaf2afc7e.recoverMethod_a_c69ef8e8().recoverMethod_b_13462e(var13);
            }
         }

         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(recoverField_a, "sendDataToRemoteProcess bundle=" + var3);
         return var3;
      } catch (Throwable var11) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(recoverField_a, "sendDataToRemoteProcess throwable=" + var11);
         return null;
      }
   }

   private static final class Member8f19c4a6 {
      private static final RecoverTypeaf2aa629 recoverField_a = new RecoverTypeaf2aa629();
   }
}
