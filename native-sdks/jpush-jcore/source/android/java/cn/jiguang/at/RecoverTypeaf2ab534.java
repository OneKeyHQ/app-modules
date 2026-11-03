package cn.jiguang.at;

import android.content.Context;
import android.text.TextUtils;
import cn.jiguang.al.RecoverTypeaf2a9725;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.api.JDispatchAction;
import cn.jiguang.av.RecoverTypeaf2abcae;
import cn.jiguang.e.RecoverType5a690d1;
import cn.jiguang.e.RecoverType5a690d2;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class RecoverTypeaf2ab534 {
   private Map recoverField_a = new HashMap();

   private RecoverTypeaf2ab534() {
   }

   public static RecoverTypeaf2ab534 recoverMethod_a_c69656f2() {
      return RecoverTypeaf2ab534.Member8f523cf1.recoverField_a;
   }

   public void recoverMethod_a_f0554df3(Context var1) {
      if (var1 == null) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("UserCtrlHelper", "handleUserCtrl failed,context is null");
      } else {
         HashMap var2 = RecoverTypeaf2ab52c.recoverField_a;
         if (var2 != null && !var2.isEmpty()) {
            for (Object recoveryElement_31 : var2.entrySet()) {
               Entry var4 = (Entry)recoveryElement_31;
               JDispatchAction var5 = (JDispatchAction)var4.getValue();
               if (var5 != null) {
                  String var6 = this.recoverMethod_a_22670df9(var1, (String)var4.getKey(), var5.getSdkVersion((String)var4.getKey()));
                  if (!TextUtils.isEmpty(var6)) {
                     this.recoverMethod_a_14b8c90e(var1, var5.getUserCtrlProperty((String)var4.getKey()), var6, (String)var4.getKey());
                  }
               }
            }
         }
      }
   }

   private void recoverMethod_a_14b8c90e(Context var1, short var2, String var3, String var4) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("UserCtrlHelper", "sendUserCtrlInfo sdkType:" + var4 + ",property:" + var2 + ",verInfo:" + var3);
      long var5 = RecoverTypeaf2ab533.recoverMethod_b_9b69();
      byte var7 = 1;
      byte[] var8 = RecoverTypeaf2abcae.recoverMethod_a_b66bea5e(var2, var7, var3);
      RecoverTypeaf2ab533.recoverMethod_a_c69656d3().recoverMethod_a_9e326362(var1, var5, 26, 0, var8, var4, 10000L);
      this.recoverField_a.put(var5, var4);
   }

   public void recoverMethod_a_304871f2(Context var1, long var2, int var4) {
      String var5 = (String)this.recoverField_a.remove(var2);
      if (!TextUtils.isEmpty(var5)) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("UserCtrlHelper", "onUserCtrlFailed rid:" + var2 + ",sdkType:" + var5 + ",errorCode:" + var4);
         String var6 = RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_b_8ea82db7(var5, "");
         if (TextUtils.isEmpty(var6)) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("UserCtrlHelper", "onUserCtrlFailed but not found sdkversion by sdkType:" + var5);
         }
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("UserCtrlHelper", "onUserCtrlFailed but not found rid:" + var2);
      }
   }

   public void recoverMethod_a_1a54e731(Context var1, long var2) {
      String var4 = (String)this.recoverField_a.remove(var2);
      if (!TextUtils.isEmpty(var4)) {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("UserCtrlHelper", "userCtrlSuccess rid:" + var2 + ",sdkType:" + var4);
         String var5 = RecoverTypeaf2ab52c.recoverMethod_a_c69655fa().recoverMethod_b_8ea82db7(var4, "");
         if (!TextUtils.isEmpty(var5)) {
            RecoverType5a690d2.recoverMethod_a_abcb8d40(var1, RecoverType5a690d1.recoverMethod_a_5ef90ef5(var4).recoverMethod_a_4e9722a3(var5));
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("UserCtrlHelper", "userCtrlSuccess but not found sdkversion by sdkType:" + var4);
         }
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("UserCtrlHelper", "userCtrlSuccess but not found rid:" + var2);
      }

      if (this.recoverField_a != null && this.recoverField_a.isEmpty() && !RecoverTypeaf2a9725.recoverMethod_a_c692b321().recoverMethod_a_f0554df7(var1)) {
         RecoverTypeaf2ab531.recoverMethod_a_c6965695().recoverMethod_a_d19840b7(var1, "tcp_a21", null);
      }
   }

   public void recoverMethod_b_1a54e731(Context var1, long var2) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("UserCtrlHelper", "onUserCtrlTimeout rid:" + var2);
      this.recoverField_a.remove(var2);
   }

   private String recoverMethod_a_22670df9(Context var1, String var2, String var3) {
      String var4 = (String)RecoverType5a690d2.recoverMethod_a_413ed21(var1, RecoverType5a690d1.recoverMethod_a_5ef90ef5(var2));
      if (!TextUtils.isEmpty(var3) && !var3.equals(var4)) {
         return var3;
      }

      RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09(
         "UserCtrlHelper", "need not " + var2 + " userctrl,newest version:" + (TextUtils.isEmpty(var3) ? "null" : var3)
      );
      return "";
   }

   public boolean recoverMethod_b_f0554df7(Context var1) {
      if (var1 == null) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("UserCtrlHelper", "get isNeedUserCtrl failed,context is null");
         return false;
      }

      HashMap var2 = RecoverTypeaf2ab52c.recoverField_a;
      if (var2 != null && !var2.isEmpty()) {
         for (Object recoveryElement_110 : var2.entrySet()) {
            Entry var4 = (Entry)recoveryElement_110;
            JDispatchAction var5 = (JDispatchAction)var4.getValue();
            if (var5 != null) {
               String var6 = this.recoverMethod_a_22670df9(var1, (String)var4.getKey(), var5.getSdkVersion((String)var4.getKey()));
               if (!TextUtils.isEmpty(var6)) {
                  return true;
               }
            }
         }
      }

      return false;
   }

   private static final class Member8f523cf1 {
      private static final RecoverTypeaf2ab534 recoverField_a = new RecoverTypeaf2ab534();
   }
}
