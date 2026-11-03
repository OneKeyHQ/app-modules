package cn.jiguang.at;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.af.RecoverTypeaf2a809d;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.api.JDispatchAction;
import cn.jiguang.av.RecoverTypeaf2abcaf;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.c.RecoverType5a6894f;
import cn.jiguang.internal.ActionManager;
import cn.jiguang.internal.JConstants;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONArray;

public class RecoverTypeaf2ab52c {
   public static HashMap recoverField_a = null;

   private RecoverTypeaf2ab52c() {
      recoverField_a = ActionManager.getDispatchActionMap();
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("DispatchActionManager", "actiom map :" + recoverField_a);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("DispatchActionManager", "actiom map size :" + recoverField_a.size());
      this.recoverMethod_a_e925fa09("JCore", RecoverType5a6894f.class.getCanonicalName());
   }

   public static RecoverTypeaf2ab52c recoverMethod_a_c69655fa() {
      return RecoverTypeaf2ab52c.Member8f521ee9.recoverField_a;
   }

   public void recoverMethod_a_e925fa09(String var1, String var2) {
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("DispatchActionManager", "addAction type:" + var1 + ",action:" + var2);
      if (!TextUtils.isEmpty(var2)) {
         if (!recoverField_a.containsKey(var1)) {
            try {
               Object var3 = Class.forName(var2).newInstance();
               if (var3 instanceof JDispatchAction) {
                  recoverField_a.put(var1, (JDispatchAction)var3);
                  RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("DispatchActionManager", "action init:" + var3.getClass().getName());
               } else {
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09(
                     "DispatchActionManager", "this action is not a JDispatchAction,please check and extends JDispatchAction"
                  );
               }
            } catch (Throwable var4) {
               RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("DispatchActionManager", "#unexcepted - instance " + var2 + " class failed:" + var4);
            }
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("DispatchActionManager", "has same type action");
         }
      }
   }

   public short recoverMethod_b_9b72() {
      short var1 = 0;

      for (Object recoveryElement_62 : recoverField_a.entrySet()) {
         Entry var3 = (Entry)recoveryElement_62;
         JDispatchAction var4 = (JDispatchAction)var3.getValue();
         if (var4 != null) {
            short var5 = var4.getRegFlag((String)var3.getKey());
            if (var5 != 0) {
               var1 |= var5;
            }
         }
      }

      return var1;
   }

   public short recoverMethod_c_9b72() {
      short var1 = 0;

      for (Object recoveryElement_78 : recoverField_a.entrySet()) {
         Entry var3 = (Entry)recoveryElement_78;
         JDispatchAction var4 = (JDispatchAction)var3.getValue();
         if (var4 != null) {
            short var5 = var4.getLoginFlag((String)var3.getKey());
            if (var5 != 0) {
               var1 |= var5;
            }
         }
      }

      return var1;
   }

   public boolean recoverMethod_a_134632(int var1) {
      for (Object recoveryElement_92 : recoverField_a.entrySet()) {
         Entry var3 = (Entry)recoveryElement_92;
         JDispatchAction var4 = (JDispatchAction)var3.getValue();
         if (var4 != null) {
            try {
               RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09(
                  "DispatchActionManager",
                  "isAllowAction actionType:" + var1 + ",sdktype:" + (String)var3.getKey() + ",action:" + var4.checkAction((String)var3.getKey(), var1)
               );
               if (!var4.checkAction((String)var3.getKey(), var1)) {
                  return false;
               }
            } catch (Throwable var6) {
               RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("DispatchActionManager", "isAllowAction error:" + var6.getMessage());
            }
         }
      }

      return true;
   }

   public void recoverMethod_a_4abe089d(Context var1, int var2, int var3, String var4) {
      for (Object recoveryElement_113 : recoverField_a.entrySet()) {
         Entry var6 = (Entry)recoveryElement_113;
         JDispatchAction var7 = (JDispatchAction)var6.getValue();
         if (var7 != null) {
            var7.onEvent(var1, (String)var6.getKey(), var2, var3, var4);
         }
      }

      this.recoverMethod_b_4abe089d(var1, var2, var3, var4);
   }

   private void recoverMethod_b_4abe089d(Context var1, int var2, int var3, String var4) {
      try {
         String var5 = "cn.jpush.android.intent.CONNECTION";
         String var6 = "cn.jpush.android.intent.REGISTRATION";
         Intent var7 = null;
         if (var2 == 0 && var3 == 0) {
            var7 = new Intent(var6);
            var7.putExtra("cn.jpush.android.REGISTRATION_ID", var4);
         } else if (var2 == -1 || var2 == 1) {
            var7 = new Intent(var5);
            if (var2 == -1) {
               var7.putExtra("cn.jpush.android.CONNECTION_CHANGE", false);
            } else {
               var7.putExtra("cn.jpush.android.CONNECTION_CHANGE", true);
            }
         }

         if (var7 != null) {
            String var8 = var1.getPackageName();
            var7.addCategory(var8);
            var7.setPackage(var8);
            RecoverTypeaf2b07c1.recoverMethod_a_c4d2ecb0(var1, var7);
         }
      } catch (Throwable var9) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("DispatchActionManager", "sendToOldPushUser failed:" + var9.getMessage());
      }
   }

   public Object recoverMethod_a_471f464c(Context var1, String var2, int var3) {
      if (JConstants.SDK_VERSION_INT <= 284) {
         return null;
      }

      JDispatchAction var4 = (JDispatchAction)recoverField_a.get(var2);
      if (var4 != null) {
         Object var5 = var4.getDataByCmd(var1, var3);
         if (null != var5) {
            return var5;
         }

         RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("DispatchActionManager", var2 + " sdk action data:" + var5 + ", actionType: " + var3);
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("DispatchActionManager", var2 + " sdk action is null");
      }

      return null;
   }

   public boolean recoverMethod_a_aad3b203(String var1) {
      return recoverField_a.get(var1) != null;
   }

   public JSONArray recoverMethod_a_5d5a71dd(Context var1) {
      JSONArray var2 = new JSONArray();
      var2.put(
         RecoverTypeaf2a809d.Member8e8cd21a.recoverMethod_a_4868d301(1, RecoverTypeaf2a809d.recoverMethod_a_9b68(), RecoverTypeaf2a809d.recoverMethod_b_9b68())
      );
      if (JConstants.SDK_VERSION_INT <= 284) {
         return var2;
      }

      for (Object recoveryElement_184 : recoverField_a.entrySet()) {
         Entry var4 = (Entry)recoveryElement_184;
         try {
            JDispatchAction var5 = (JDispatchAction)var4.getValue();
            Object var6 = var5.getDataByCmd(var1, 30003);
            if (var6 instanceof Integer) {
               Object var7 = var5.getDataByCmd(var1, 30004);
               Object var8 = var5.getDataByCmd(var1, 30005);
               if (var7 instanceof Integer && var8 instanceof Integer) {
                  int var9 = RecoverTypeaf2a809d.Member8e8cd21a.recoverMethod_a_4868d301((Integer)var6, (Integer)var7, (Integer)var8);
                  var2.put(var9);
               }
            }
         } catch (Throwable var10) {
         }
      }

      return var2;
   }

   public String recoverMethod_b_8ea82db7(String var1, String var2) {
      JDispatchAction var3 = (JDispatchAction)recoverField_a.get(var1);
      if (var3 != null) {
         String var4 = var3.getSdkVersion(var1);
         if (!TextUtils.isEmpty(var4)) {
            return var4;
         }

         RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("DispatchActionManager", var1 + " sdk action sdkversion:" + var4);
      } else {
         RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("DispatchActionManager", var1 + " sdk action is null");
      }

      return var2;
   }

   public String recoverMethod_b_47921032(int var1) {
      for (Object recoveryElement_220 : recoverField_a.entrySet()) {
         Entry var3 = (Entry)recoveryElement_220;
         JDispatchAction var4 = (JDispatchAction)var3.getValue();
         if (var4 != null && var4.getRegPriority((String)var3.getKey()) == var1) {
            return var4.getSdkVersion((String)var3.getKey());
         }
      }

      return "";
   }

   public void recoverMethod_a_510e8a7c(Context var1, String var2, long var3, int var5) {
      if (!TextUtils.isEmpty(var2) && var2.equals("JCore")) {
         switch (var5) {
            case 26:
               RecoverTypeaf2ab534.recoverMethod_a_c69656f2().recoverMethod_b_1a54e731(var1, var3);
            case 30:
            case 32:
            default:
               break;
            case 41:
               RecoverTypeaf2a9ae9.recoverMethod_d_e925fa09("DispatchActionManager", "setCountry time out");
               RecoverTypeaf2ab531.recoverMethod_a_c6965695().recoverMethod_a_d19840b7(var1, "tcp_a25", new Bundle());
         }
      } else {
         JDispatchAction var6 = (JDispatchAction)recoverField_a.get(var2);
         if (var6 != null) {
            var6.dispatchTimeOutMessage(var1, var2, var3, var5);
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("DispatchActionManager", "not found dispatch action by sdktype:" + var2);
         }
      }
   }

   public void recoverMethod_a_15da8d92(Context var1, RecoverTypeaf2abcaf var2, ByteBuffer var3) {
      if (var2 == null) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("DispatchActionManager", "Action - dispatchMessage unexcepted - head was null");
      } else {
         RecoverTypeaf2ab52e var4 = RecoverTypeaf2ab533.recoverMethod_a_c69656d3().recoverMethod_a_4880d0b2(var2.recoverField_e);
         if (var4 != null) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("DispatchActionManager", "dispacth msg with reuqest :" + var4);
            JDispatchAction var8 = (JDispatchAction)recoverField_a.get(var4.recoverField_c);
            if (var8 != null) {
               var8.dispatchMessage(var1, var4.recoverField_c, var2.recoverField_c, var2.recoverField_b, var2.recoverField_e, var4.recoverField_b, var3);
            }

            Bundle var9 = new Bundle();
            var9.putLong("rid", var2.recoverField_e);
            RecoverTypeaf2ab531.recoverMethod_a_c6965695().recoverMethod_a_d19840b7(var1, "tcp_a7", var9);
         } else {
            for (Object recoveryElement_269 : recoverField_a.entrySet()) {
               Entry var6 = (Entry)recoveryElement_269;
               JDispatchAction var7 = (JDispatchAction)var6.getValue();
               if (var7 != null && var7.isSupportedCMD((String)var6.getKey(), var2.recoverField_c)) {
                  var7.dispatchMessage(var1, (String)var6.getKey(), var2.recoverField_c, var2.recoverField_b, var2.recoverField_e, -1L, var3);
               }
            }
         }
      }
   }

   public void recoverMethod_a_d19840b7(Context var1, String var2, Bundle var3) {
      if (var3 == null) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("DispatchActionManager", "run action bundle is null");
      } else if (TextUtils.isEmpty(var2)) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("DispatchActionManager", "run action sdktype is empty");
      } else {
         if ("JCore".contains(var2)) {
            var2 = "JCore";
         }

         JDispatchAction var4 = (JDispatchAction)recoverField_a.get(var2);
         if (var4 == null) {
            RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("DispatchActionManager", "dispacth action is null by sdktype:" + var2);
         } else {
            String var5 = var3.getString("internal_action");
            var4.onActionRun(var1, var2, var5, var3);
         }
      }
   }

   public Map recoverMethod_d_604efcea() {
      HashMap var1 = new HashMap();

      for (Object recoveryElement_302 : recoverField_a.entrySet()) {
         Entry var3 = (Entry)recoveryElement_302;
         JDispatchAction var4 = (JDispatchAction)var3.getValue();
         Bundle var5 = var4.getPInfo((String)var3.getKey());
         if (var5 != null) {
            int var6 = var5.getInt("pid", 0);
            if (var6 > 0) {
               var1.put(var6, var5);
            }
         }
      }

      return var1;
   }

   public String recoverMethod_e_73cf11cb() {
      StringBuilder var1 = new StringBuilder();
      short var2 = 3;

      for (Object recoveryElement_320 : recoverField_a.entrySet()) {
         Entry var4 = (Entry)recoveryElement_320;
         JDispatchAction var5 = (JDispatchAction)var4.getValue();
         short var6 = var5.getRegPriority((String)var4.getKey());
         if (var2 < var6) {
            var2 = var6;
         }
      }

      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("DispatchActionManager", "max reg priority: " + var2);

      for (int var8 = 0; var8 <= var2; var8++) {
         if (var8 == 3) {
            var1.append(RecoverType5a681cd.recoverField_b).append("|");
         } else {
            for (Object recoveryElement_334 : recoverField_a.entrySet()) {
               Entry var10 = (Entry)recoveryElement_334;
               JDispatchAction var11 = (JDispatchAction)var10.getValue();
               short var7 = var11.getRegPriority((String)var10.getKey());
               if (var7 == var8) {
                  var1.append(var11.getSdkVersion((String)var10.getKey())).append("|");
                  break;
               }
            }
         }
      }

      if (var1.length() > 0) {
         var1.setLength(var1.length() - 1);
      }

      return var1.toString();
   }

   public String recoverMethod_f_73cf11cb() {
      StringBuilder var1 = new StringBuilder();
      var1.append(recoverMethod_b_aad3b1f2(RecoverType5a681cd.recoverField_b)).append("|");
      short var2 = 0;

      for (Object recoveryElement_357 : recoverField_a.entrySet()) {
         Entry var4 = (Entry)recoveryElement_357;
         JDispatchAction var5 = (JDispatchAction)var4.getValue();
         short var6 = var5.getLogPriority((String)var4.getKey());
         var2 = (short)Math.max(var2, var6);
      }

      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("DispatchActionManager", "max login priority: " + var2);

      for (int var8 = 1; var8 <= var2; var8++) {
         for (Object recoveryElement_366 : recoverField_a.entrySet()) {
            Entry var10 = (Entry)recoveryElement_366;
            JDispatchAction var11 = (JDispatchAction)var10.getValue();
            short var7 = var11.getLogPriority((String)var10.getKey());
            if (var7 == var8) {
               var1.append(recoverMethod_b_aad3b1f2(var11.getSdkVersion((String)var10.getKey()))).append("|");
               break;
            }
         }
      }

      if (var1.length() > 0) {
         var1.setLength(var1.length() - 1);
      }

      return var1.toString();
   }

   public byte recoverMethod_b_f0554ddf(Context var1) {
      for (Object recoveryElement_384 : recoverField_a.entrySet()) {
         Entry var3 = (Entry)recoveryElement_384;
         JDispatchAction var4 = (JDispatchAction)var3.getValue();
         if (var4 != null) {
            Object var5 = var4.beforLogin(var1, (String)var3.getKey(), 27, "platformtype");
            if (var5 instanceof Byte) {
               return (Byte)var5;
            }
         }
      }

      return 0;
   }

   public String recoverMethod_c_e501eb0d(Context var1) {
      for (Object recoveryElement_398 : recoverField_a.entrySet()) {
         Entry var3 = (Entry)recoveryElement_398;
         JDispatchAction var4 = (JDispatchAction)var3.getValue();
         if (var4 != null) {
            Object var5 = var4.beforLogin(var1, (String)var3.getKey(), 27, "platformregid");
            if (var5 instanceof String) {
               return (String)var5;
            }
         }
      }

      return "";
   }

   private static int recoverMethod_b_aad3b1f2(String var0) {
      if (TextUtils.isEmpty(var0)) {
         return 0;
      }

      String[] var1 = var0.split("\\.");
      return (Integer.parseInt(var1[0]) << 16) + (Integer.parseInt(var1[1]) << 8) + Integer.parseInt(var1[2]);
   }

   private static final class Member8f521ee9 {
      private static final RecoverTypeaf2ab52c recoverField_a = new RecoverTypeaf2ab52c();
   }
}
