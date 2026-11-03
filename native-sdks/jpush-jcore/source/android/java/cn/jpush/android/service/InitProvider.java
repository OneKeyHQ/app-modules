package cn.jpush.android.service;

import android.app.Application;
import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import cn.jiguang.d.RecoverType5a68d10;
import cn.jiguang.internal.JConstants;
import java.lang.reflect.Method;

public class InitProvider extends ContentProvider {
   private static final String TAG = "InitProvider";
   private Class actionHelperClass;
   private Object actionHelperInstance;
   private Method onJPushMessageReceiveMethod;

   @Override
   public boolean onCreate() {
      Log.d("InitProvider", "InitProvider onCreate");

      try {
         Application var1 = (Application)this.getContext();
         String var2 = this.getProviderProcessName(var1);
         JConstants.getAppContext(var1);
         Log.d("InitProvider", "InitProvider process: " + var2);
         if (null != var1 && TextUtils.equals(var2, var1.getPackageName())) {
            RecoverType5a68d10.recoverMethod_a_1a552341(var1, false);
         } else {
            Log.w("InitProvider", "InitProvider is not in main process. processName: " + var2);
         }
      } catch (Throwable var3) {
         Log.e("InitProvider", "InitProvider onCreate error", var3);
      }

      return false;
   }

   private String getProviderProcessName(Context var1) {
      try {
         PackageManager var2 = var1.getPackageManager();
         ProviderInfo var3 = var2.getProviderInfo(new ComponentName(var1.getPackageName(), InitProvider.class.getName()), 0);
         return var3.processName;
      } catch (NameNotFoundException var4) {
         Log.e("InitProvider", "Provider not found", var4);
         return null;
      } catch (Throwable var5) {
         Log.e("InitProvider", "getProviderProcessName error", var5);
         return null;
      }
   }

   @Override
   public Cursor query(Uri var1, String[] var2, String var3, String[] var4, String var5) {
      return null;
   }

   @Override
   public String getType(Uri var1) {
      return null;
   }

   @Override
   public Uri insert(Uri var1, ContentValues var2) {
      return null;
   }

   @Override
   public int delete(Uri var1, String var2, String[] var3) {
      return 0;
   }

   @Override
   public int update(Uri var1, ContentValues var2, String var3, String[] var4) {
      return 0;
   }

   @Override
   public Bundle call(String var1, String var2, Bundle var3) {
      try {
         Context var4 = this.getContext();
         if (JConstants.isCallInit.get() && var4 != null && var4.checkCallingPermission(var4.getPackageName() + ".permission.JPUSH_MESSAGE") == 0) {
            if (this.actionHelperClass == null) {
               this.actionHelperClass = Class.forName(new String(Base64.decode("Y24uanB1c2guYW5kcm9pZC5sb2NhbC5BY3Rpb25IZWxwZXI=", 2)));
            }

            if (this.actionHelperInstance == null) {
               Method var5 = this.actionHelperClass.getMethod(new String(Base64.decode("Z2V0SW5zdGFuY2U=", 2)));
               this.actionHelperInstance = var5.invoke(null);
            }

            if (this.onJPushMessageReceiveMethod == null) {
               this.onJPushMessageReceiveMethod = this.actionHelperClass
                  .getMethod(new String(Base64.decode("b25KUHVzaE1lc3NhZ2VSZWNlaXZl", 2)), Context.class, String.class, String.class, Bundle.class);
            }

            this.onJPushMessageReceiveMethod.invoke(this.actionHelperInstance, var4, var1, var2, var3);
         }
      } catch (Throwable var6) {
      }

      return null;
   }
}
