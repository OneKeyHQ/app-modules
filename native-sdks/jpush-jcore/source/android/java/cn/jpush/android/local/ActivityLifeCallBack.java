package cn.jpush.android.local;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.os.Bundle;
import android.text.TextUtils;
import cn.jpush.android.helper.Logger;

@TargetApi(14)
public class ActivityLifeCallBack implements ActivityLifecycleCallbacks {
   private static final String TAG = "ActivityLifeCallBack";
   public static int activityTaskCount = 0;
   private String lastActivity = "";

   @Override
   public void onActivityCreated(Activity var1, Bundle var2) {
   }

   @Override
   public void onActivityStarted(Activity var1) {
      ActionHelper.getInstance().onActivityLifeCallback(var1, "started");
      if (activityTaskCount == 0) {
         Logger.recoverMethod_d_e925fa09("ActivityLifeCallBack", "is Foreground");
         if (var1 != null) {
            JPushConstants.changeForegroudStat(var1.getApplicationContext(), true);
         }
      }

      activityTaskCount++;
      this.lastActivity = var1.getClass().getCanonicalName();
   }

   @Override
   public void onActivityResumed(Activity var1) {
      ActionHelper.getInstance().onActivityLifeCallback(var1, "resumed");
   }

   @Override
   public void onActivityPaused(Activity var1) {
      ActionHelper.getInstance().onActivityLifeCallback(var1, "paused");
   }

   @Override
   public void onActivityStopped(Activity var1) {
      ActionHelper.getInstance().onActivityLifeCallback(var1, "stopped");
      if (activityTaskCount > 0) {
         activityTaskCount--;
      }

      if (activityTaskCount == 0 && var1 != null) {
         if (TextUtils.equals(this.lastActivity, var1.getClass().getCanonicalName())) {
            Logger.recoverMethod_d_e925fa09("ActivityLifeCallBack", "is not Foreground");
            JPushConstants.changeForegroudStat(var1.getApplicationContext(), false);
         } else {
            activityTaskCount++;
         }
      }
   }

   @Override
   public void onActivitySaveInstanceState(Activity var1, Bundle var2) {
   }

   @Override
   public void onActivityDestroyed(Activity var1) {
      ActionHelper.getInstance().onActivityLifeCallback(var1, "destroyed");
   }

   public static ActivityLifeCallBack getInstance() {
      return ActivityLifeCallBack.Member6bd0bca0.recoverField_a;
   }

   private ActivityLifeCallBack() {
   }

   private static class Member6bd0bca0 {
      private static final ActivityLifeCallBack recoverField_a = new ActivityLifeCallBack();
   }
}
