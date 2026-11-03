package cn.jiguang.internal;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import cn.jiguang.a.RecoverType5a681cd;
import cn.jiguang.al.RecoverTypeaf2a9725;
import cn.jiguang.al.RecoverTypeaf2a972b;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.api.JDispatchAction;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.json.JSONException;
import org.json.JSONObject;

public class ActionManager {
   private static final String TAG = "ActionManager";
   private static HashMap actionMap = new HashMap();
   private static HashMap actionStringMap = new HashMap();
   private static final String SDK_TYPE = "sdk_type";
   private static final String SDK_VERSION = "version";

   public static ActionManager getInstance() {
      return ActionManager.Member965d0c8.recoverField_a;
   }

   public ActionManager() {
      RecoverTypeaf2a9725.recoverMethod_a_c692b321();
   }

   public static JDispatchAction getJDispatchAction(String var0) {
      return (JDispatchAction)actionMap.get(var0);
   }

   public static HashMap getActionMap() {
      return actionStringMap;
   }

   public static HashMap getDispatchActionMap() {
      return actionMap;
   }

   public static void addAction(String var0, String var1) {
      Log.d("ActionManager", "addAction type:" + var0 + ",action:" + var1);
      if (!TextUtils.isEmpty(var1)) {
         if (!actionMap.containsKey(var0)) {
            try {
               Object var2 = Class.forName(var1).newInstance();
               if (var2 instanceof JDispatchAction) {
                  actionStringMap.put(var0, var1);
                  actionMap.put(var0, (JDispatchAction)var2);
               } else {
                  RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ActionManager", "this action is not a JDispatchAction,please check and extends JDispatchAction");
               }
            } catch (Throwable var3) {
               RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("ActionManager", "#unexcepted - instance " + var1 + " class failed:" + var3);
            }
         } else {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ActionManager", "has same type action");
         }
      }
   }

   public void handleMessage(Context var1, String var2, Object var3) {
      RecoverTypeaf2a9ae9.recoverMethod_a_e925fa09("ActionManager", "onSended type:" + var2 + ",actionMap size:" + actionMap.size());
      if (TextUtils.isEmpty(var2)) {
         for (Object recoveryElement_69 : actionMap.entrySet()) {
            Entry var5 = (Entry)recoveryElement_69;
            JDispatchAction var6 = (JDispatchAction)var5.getValue();
            var6.handleMessage(var1, (String)var5.getKey(), var3);
         }
      } else {
         JDispatchAction var7 = (JDispatchAction)actionMap.get(var2);
         if (var7 != null) {
            var7.handleMessage(var1, var2, var3);
         }
      }
   }

   public boolean wrapSdkVersionInfo(JSONObject var1) {
      if (var1 == null) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ActionManager", "wrapSdkVersionInfo failed ,container is null");
         return false;
      }

      try {
         var1.put("core_sdk_ver", RecoverType5a681cd.recoverField_b);

         for (Object recoveryElement_90 : actionMap.entrySet()) {
            Entry var3 = (Entry)recoveryElement_90;
            JDispatchAction var4 = (JDispatchAction)var3.getValue();
            var1.put(var4.getReportVersionKey((String)var3.getKey()), var4.getSdkVersion((String)var3.getKey()));
            Object var5 = var4.getDataByCmd(null, 30001);
            if (null != var5 && var5 instanceof String) {
               Object var6 = var4.getDataByCmd(null, 30002);
               if (null != var6 && var6 instanceof String) {
                  var1.put((String)var5, (String)var6);
               }
            }
         }
      } catch (JSONException var7) {
      }

      return true;
   }

   public Map loadPInfo() {
      HashMap var1 = new HashMap();

      for (Object recoveryElement_110 : actionMap.entrySet()) {
         Entry var3 = (Entry)recoveryElement_110;
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

   public static boolean wrapSdkTypeVersionInfo(JSONObject var0) {
      boolean var1 = false;
      if (var0 == null) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ActionManager", "wrapSdkTypeVersionInfo failed ,container is null");
         return var1;
      }

      try {
         if (RecoverTypeaf2a972b.recoverMethod_a_134632(RecoverTypeaf2a972b.recoverMethod_b_9b68())) {
            JSONObject var2 = new JSONObject();
            var2.put("version", RecoverType5a681cd.recoverField_b);
            var2.put("sdk_type", RecoverTypeaf2a972b.recoverMethod_b_9b68());
            if (var2 != null) {
               var1 = true;
               var0.put("core_sdk_ver", var2);
            }
         }

         for (Object recoveryElement_142 : actionMap.entrySet()) {
            Entry var3 = (Entry)recoveryElement_142;
            JDispatchAction var4 = (JDispatchAction)var3.getValue();
            if (RecoverTypeaf2a972b.recoverMethod_a_134632(var4.getSdkType())) {
               JSONObject var5 = new JSONObject();
               var5.put("version", var4.getSdkVersion((String)var3.getKey()));
               var5.put("sdk_type", var4.getSdkType());
               if (var5 != null) {
                  var1 = true;
                  var0.put(var4.getReportVersionKey((String)var3.getKey()), var5);
               }
            }
         }
      } catch (JSONException var6) {
      }

      return var1;
   }

   private static final class Member965d0c8 {
      private static final ActionManager recoverField_a = new ActionManager();
   }
}
