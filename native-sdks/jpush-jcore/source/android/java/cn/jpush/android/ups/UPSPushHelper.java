package cn.jpush.android.ups;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import cn.jpush.android.ad.RecoverType9b4c5aee;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.helper.Logger;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UPSPushHelper {
   private static final String TAG = "UPSPushHelper";
   public static final String ACTION_UPS = ".ups.receiver";
   public static final String KEY_RID = "ups.rid";
   public static final String KEY_CODE = "ups.code";
   public static final String KEY_TOKEN = "ups.token";
   public static final String KEY_ACTION = "ups.action";
   private static Map cacheMap = new ConcurrentHashMap();
   private static AtomicInteger rid = new AtomicInteger(0);
   private static UPSPushHelper.Memberb92dd337 upsReceiver;

   public static void init(Context var0) {
      if (upsReceiver == null) {
         upsReceiver = new UPSPushHelper.Memberb92dd337();
         IntentFilter var1 = new IntentFilter();
         var1.addAction(var0.getPackageName() + ".ups.receiver");
         RecoverType9b4c5aee.recoverMethod_a_b0740a13(var0, upsReceiver, var1, getReceiverPermission(var0), null);
      }
   }

   public static String getReceiverPermission(Context var0) {
      return var0.getPackageName() + ".permission.JPUSH_MESSAGE";
   }

   public static void callResult(Context var0, String var1, Bundle var2, ICallbackResult var3) {
      int var4 = getNextRid();
      createMapping(var0, var4, var3);
      if (var2 == null) {
         var2 = new Bundle();
      }

      var2.putInt("ups.rid", var4);
      JCoreHelper.runActionWithService(var0, "JPUSH", var1, var2);
   }

   public static void createMapping(Context var0, long var1, ICallbackResult var3) {
      init(var0);
      if (var3 == null) {
         Logger.recoverMethod_e_e925fa09("UPSPushHelper", "requestParams is null");
      } else if (cacheMap.containsKey(var1)) {
         Logger.recoverMethod_v_e925fa09("UPSPushHelper", "rid " + var1 + " has exist.");
      } else {
         if (var3 instanceof Activity) {
            WeakReference var4 = new WeakReference(var3);
            cacheMap.put(var1, var4);
         } else {
            cacheMap.put(var1, var3);
         }
      }
   }

   public static ICallbackResult removeMapping(long var0) {
      return objectToCallBack(cacheMap.remove(var0));
   }

   private static ICallbackResult objectToCallBack(Object var0) {
      if (var0 != null) {
         if (var0 instanceof WeakReference) {
            return (ICallbackResult)((WeakReference)var0).get();
         }

         if (var0 instanceof ICallbackResult) {
            return (ICallbackResult)var0;
         }
      }

      return null;
   }

   public static int getNextRid() {
      int var0 = rid.incrementAndGet();
      if (var0 == Integer.MAX_VALUE) {
         rid.set(0);
      }

      return var0;
   }

   public static void upsCallBack(Context var0, int var1, String var2, int var3, String var4) {
      try {
         Intent var5 = new Intent();
         var5.setAction(var0.getPackageName() + ".ups.receiver");
         var5.setPackage(var0.getPackageName());
         var5.putExtra("ups.rid", var1);
         var5.putExtra("ups.code", var3);
         var5.putExtra("ups.token", var4);
         var5.putExtra("ups.action", var2);
         var0.sendBroadcast(var5, getReceiverPermission(var0));
      } catch (Throwable var6) {
         Logger.recoverMethod_ee_e925fa09("UPSPushHelper", "[upsCallBack] failed:" + var6.getMessage());
      }
   }

   static class Memberb92dd337 extends BroadcastReceiver {
      @Override
      public void onReceive(Context var1, Intent var2) {
         try {
            String var3 = var2.getAction();
            if (var3.equals(var1.getPackageName() + ".ups.receiver")) {
               int var4 = var2.getIntExtra("ups.rid", 0);
               int var5 = var2.getIntExtra("ups.code", -1);
               String var6 = var2.getStringExtra("ups.token");
               String var7 = var2.getStringExtra("ups.action");
               Logger.recoverMethod_dd_e925fa09("UPSPushHelper", "intenalAction:" + var7 + ",rid:" + var4 + ",code:" + var5 + ",token:" + var6);
               if (var4 > 0) {
                  ICallbackResult var8 = UPSPushHelper.removeMapping(var4);
                  if (var8 != null) {
                     var8.onResult(new TokenResult(var6, var5, var7));
                  }
               } else if (!TextUtils.isEmpty(var7) && (var7.equals("ups.register") || var7.equals("ups.unregister"))) {
                  Iterator var12 = UPSPushHelper.cacheMap.values().iterator();

                  while (var12.hasNext()) {
                     Object var9 = var12.next();
                     ICallbackResult var10 = UPSPushHelper.objectToCallBack(var9);
                     if (var10 != null) {
                        if (var9 instanceof UPSRegisterCallBack && var7.equals("ups.register")) {
                           var12.remove();
                           var10.onResult(new TokenResult(var6, var5, var7));
                        } else if (var9 instanceof UPSUnRegisterCallBack && var7.equals("ups.unregister")) {
                           var12.remove();
                           var10.onResult(new TokenResult(var6, var5, var7));
                        }
                     }
                  }
               }
            }
         } catch (Throwable var11) {
         }
      }
   }
}
