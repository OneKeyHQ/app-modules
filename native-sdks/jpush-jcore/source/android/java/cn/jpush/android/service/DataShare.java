package cn.jpush.android.service;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.android.IDataShare;
import cn.jiguang.bk.RecoverTypeaf2b07c1;
import cn.jiguang.internal.JConstants;
import cn.jiguang.internal.JCoreInternalHelper;
import java.util.HashMap;
import java.util.Map;

public class DataShare extends IDataShare.Membereeaed960 {
   private static final String TAG = "DataShare";
   private static boolean isBinding = false;
   private static final Map aidlMap = new HashMap();

   public static IDataShare getInstance(String var0) {
      return (IDataShare)aidlMap.get(var0);
   }

   public static void init(IDataShare var0, String var1) {
      if (var0 != getInstance(var1)) {
         aidlMap.put(var1, var0);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("DataShare", var1 + "'s aidl created");

         try {
            Context var2 = JConstants.getAppContext(null);
            if (var2 != null) {
               String var3 = RecoverTypeaf2b07c1.recoverMethod_a_e501eb0d(var2);
               if (var2.getPackageName().equals(var3)) {
                  var0.bind(new DataShare(), var3);
               }
            }
         } catch (RemoteException var4) {
            RecoverTypeaf2a9ae9.recoverMethod_l_e925fa09("DataShare", "bind failed=" + var4);
         }
      }

      isBinding = false;
   }

   @Override
   public String bind(IDataShare var1, String var2) {
      aidlMap.put(var2, var1);
      RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("DataShare", var2 + "'s aidl bound");
      return RecoverTypeaf2b07c1.recoverMethod_a_e501eb0d(null);
   }

   public static boolean isBinding() {
      return isBinding;
   }

   public static void setBinding() {
      isBinding = true;
   }

   @Override
   public IBinder getBinderByType(String var1, String var2) {
      return null;
   }

   @Override
   public void onAction(String var1, String var2, Bundle var3) {
      try {
         JCoreInternalHelper.getInstance().directHandle(JConstants.getAppContext(null), var1, var2, var3);
      } catch (Throwable var5) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("DataShare", "onAction error:" + var5);
      }
   }

   @Override
   public Bundle execute(String var1, String var2, Bundle var3) {
      try {
         return JCoreInternalHelper.getInstance().directHandle(JConstants.getAppContext(null), var1, var2, var3);
      } catch (Throwable var5) {
         RecoverTypeaf2a9ae9.recoverMethod_j_e925fa09("DataShare", "onAction error:" + var5);
         return null;
      }
   }
}
