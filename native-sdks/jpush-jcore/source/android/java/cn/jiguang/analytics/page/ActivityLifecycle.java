package cn.jiguang.analytics.page;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.content.res.TypedArray;
import android.content.res.Resources.Theme;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import cn.jiguang.am.RecoverTypeaf2a9ae9;
import cn.jiguang.ap.RecoverTypeaf2aa628;
import cn.jiguang.api.JCoreManager;
import cn.jiguang.bj.RecoverTypeaf2b0401;
import cn.jiguang.bj.RecoverTypeaf2b0403;
import cn.jiguang.internal.JConstants;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.json.JSONObject;

@TargetApi(14)
public final class ActivityLifecycle implements ActivityLifecycleCallbacks {
   private static final String TAG = "ActivityLifecycle";
   private static final AtomicInteger activityTaskCount = new AtomicInteger(0);
   public static long lastWakeTime = 0L;
   public static final long WAKE_VIOLATION_DURATION = 50L;
   public static final long WAKE_VIOLATION_COUNT = 5L;
   private List mActivityCallbacks = new ArrayList();
   private boolean isFromSync = false;
   private boolean aidlBuildSuccess = false;
   private static final String SAVE_KEY = "save_key";
   private WeakReference mWeakReference;

   @Override
   public void onActivityCreated(Activity var1, Bundle var2) {
      try {
         if (JConstants.jAnalyticsAction != null && JConstants.isCallInit.get()) {
            JConstants.jAnalyticsAction.dispatchStatus(var1, "onCreate");
         }

         if (this.aidlBuildSuccess) {
            for (Object recoveryElement_45 : this.mActivityCallbacks) {
               ActivityLifecycleCallbacks var4 = (ActivityLifecycleCallbacks)recoveryElement_45;
               var4.onActivityCreated(var1, var2);
            }
         }
      } catch (Throwable var5) {
      }
   }

   @Override
   public void onActivityStarted(Activity var1) {
      try {
         if (JConstants.jAnalyticsAction != null && JConstants.isCallInit.get()) {
            JConstants.jAnalyticsAction.dispatchStatus(var1, "onStart");
         }

         if (this.aidlBuildSuccess) {
            for (Object recoveryElement_61 : this.mActivityCallbacks) {
               ActivityLifecycleCallbacks var3 = (ActivityLifecycleCallbacks)recoveryElement_61;
               var3.onActivityStarted(var1);
            }
         }

         this.cacheBeforeInit(var1);
         if (activityTaskCount.get() == 0) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ActivityLifecycle", "isForeground");
            if (var1 != null && JConstants.isCallInit.get()) {
               byte var5 = 1;
               JCoreManager.onEvent(var1.getApplicationContext(), "JCore", 66, null, null, Integer.valueOf(var5));
            }
         }

         if (!this.isFromSync) {
            activityTaskCount.set(activityTaskCount.get() + 1);
         }
      } catch (Throwable var4) {
      }
   }

   private void reportWakeViolation(final Context var1) {
      RecoverTypeaf2b0403.recoverMethod_a_97dca6fd("FUTURE_TASK", new RecoverTypeaf2b0401() {
         @Override
         public void recoverMethod_a_9b75() {
            try {
               JSONObject var1x = new JSONObject();
               RecoverTypeaf2aa628.recoverMethod_a_d97c5eb5(var1, var1x, "wake_violation");
            } catch (Throwable var2) {
            }
         }
      });
   }

   @Override
   public void onActivityResumed(Activity var1) {
      try {
         if (JConstants.jAnalyticsAction != null && JConstants.isCallInit.get()) {
            JConstants.jAnalyticsAction.dispatchResume(var1);
         }

         if (this.aidlBuildSuccess) {
            for (Object recoveryElement_103 : this.mActivityCallbacks) {
               ActivityLifecycleCallbacks var3 = (ActivityLifecycleCallbacks)recoveryElement_103;
               var3.onActivityResumed(var1);
            }
         }

         if (!JConstants.isInstrumentationHookFailed && JConstants.isCallInit.get()) {
            PushSA.getInstance().onResume(var1);
         }
      } catch (Throwable var4) {
      }
   }

   @Override
   public void onActivityPaused(Activity var1) {
      try {
         if (JConstants.jAnalyticsAction != null && JConstants.isCallInit.get()) {
            JConstants.jAnalyticsAction.dispatchPause(var1);
         }

         if (this.aidlBuildSuccess) {
            for (Object recoveryElement_123 : this.mActivityCallbacks) {
               ActivityLifecycleCallbacks var3 = (ActivityLifecycleCallbacks)recoveryElement_123;
               var3.onActivityPaused(var1);
            }
         }

         if (!JConstants.isInstrumentationHookFailed && JConstants.isCallInit.get()) {
            PushSA.getInstance().onPause(var1);
         }
      } catch (Throwable var4) {
      }
   }

   @Override
   public void onActivityStopped(Activity var1) {
      try {
         if (this.aidlBuildSuccess) {
            for (Object recoveryElement_139 : this.mActivityCallbacks) {
               ActivityLifecycleCallbacks var3 = (ActivityLifecycleCallbacks)recoveryElement_139;
               var3.onActivityStopped(var1);
            }
         }

         if (activityTaskCount.get() > 0) {
            activityTaskCount.set(activityTaskCount.get() - 1);
         }

         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ActivityLifecycle", "onActivityStopped activityTaskCount:" + activityTaskCount);
         if (activityTaskCount.get() == 0 && JConstants.isCallInit.get()) {
            RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09("ActivityLifecycle", "is not Foreground");
            byte var5 = 0;
            Context var6 = var1.getApplicationContext();
            JCoreManager.onEvent(var6, "JCore", 66, null, null, Integer.valueOf(var5));
         }
      } catch (Throwable var4) {
      }
   }

   @Override
   public void onActivitySaveInstanceState(Activity var1, Bundle var2) {
      try {
         if (this.aidlBuildSuccess) {
            for (Object recoveryElement_163 : this.mActivityCallbacks) {
               ActivityLifecycleCallbacks var4 = (ActivityLifecycleCallbacks)recoveryElement_163;
               var4.onActivitySaveInstanceState(var1, var2);
            }
         }
      } catch (Throwable var5) {
      }
   }

   @Override
   public void onActivityDestroyed(Activity var1) {
      try {
         if (this.aidlBuildSuccess) {
            for (Object recoveryElement_175 : this.mActivityCallbacks) {
               ActivityLifecycleCallbacks var3 = (ActivityLifecycleCallbacks)recoveryElement_175;
               var3.onActivityDestroyed(var1);
            }
         }

         if (activityTaskCount.get() == 0 && var1 != null && JConstants.isCallInit.get()) {
            JCoreManager.onEvent(var1.getApplicationContext(), "JCore", 110, null, null);
         }
      } catch (Throwable var4) {
      }
   }

   private boolean isTranslucentNotitleTheme(Activity var1) {
      try {
         Theme var2 = var1.getTheme();
         TypedArray var3 = var2.obtainStyledAttributes(new int[]{16842838, 16842841, 16842836, 16842840, 16843435});
         boolean var4 = var3.getBoolean(0, false);
         Drawable var5 = var3.getDrawable(1);
         int var6 = var3.getColor(2, -1);
         boolean var7 = var3.getBoolean(3, false);
         Drawable var8 = var3.getDrawable(4);
         RecoverTypeaf2a9ae9.recoverMethod_c_e925fa09(
            "ActivityLifecycle", "notitle:" + var4 + ", overlay:" + var5 + ", color:" + var6 + ", translucent:" + var7 + "background:" + var8
         );
         var3.recycle();
         if (var4 && var7 && var5 == null && var8 == null && var6 == var1.getResources().getColor(17170445)) {
            return true;
         }
      } catch (Throwable var9) {
         RecoverTypeaf2a9ae9.recoverMethod_i_e925fa09("ActivityLifecycle", "check TranslucentNotitleTheme failed :" + var9);
      }

      return false;
   }

   private boolean isWakeActivity(Activity var1) {
      boolean var2 = false;

      try {
         if (var1 != null
            && var1.getIntent() != null
            && !TextUtils.isEmpty(var1.getIntent().getAction())
            && "cn.jpush.android.intent.DActivity".equals(var1.getIntent().getAction())) {
            var2 = true;
         }
      } catch (Throwable var4) {
      }

      return var2;
   }

   public void sendCurrentState() {
      try {
         this.aidlBuildSuccess = true;
         JCoreManager.onEvent(JConstants.getAppContext(), "JCore", 66, null, null, activityTaskCount.get() == 0 ? 0 : 1);
         if (activityTaskCount.get() <= 0) {
            return;
         }

         if (this.mWeakReference != null) {
            Activity var1 = (Activity)this.mWeakReference.get();
            if (var1 != null && !var1.isFinishing()) {
               this.isFromSync = true;
               this.onActivityCreated(var1, null);
               this.onActivityStarted(var1);
               this.onActivityResumed(var1);
               this.isFromSync = false;
            }
         }

         this.clearLocal();
      } catch (Throwable var2) {
      }
   }

   private void cacheBeforeInit(Activity var1) {
      try {
         if (!this.aidlBuildSuccess) {
            this.clearLocal();
            this.mWeakReference = new WeakReference(var1);
         } else {
            this.clearLocal();
         }
      } catch (Throwable var3) {
      }
   }

   private void clearLocal() {
      if (this.mWeakReference != null) {
         this.mWeakReference.clear();
         this.mWeakReference = null;
      }
   }

   public void addActivityLifecycleCallbacks(ActivityLifecycleCallbacks var1) {
      try {
         if (var1 != null) {
            if (this.mActivityCallbacks == null) {
               this.mActivityCallbacks = new ArrayList();
            }

            this.mActivityCallbacks.add(var1);
         }
      } catch (Throwable var3) {
      }
   }

   public void removeActivityLifecycleCallbacks(ActivityLifecycleCallbacks var1) {
      try {
         if (var1 != null) {
            this.mActivityCallbacks.remove(var1);
         }
      } catch (Throwable var3) {
      }
   }
}
