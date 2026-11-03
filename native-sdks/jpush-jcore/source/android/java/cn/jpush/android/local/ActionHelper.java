package cn.jpush.android.local;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import cn.jiguang.api.JCoreManager;
import cn.jpush.android.ad.RecoverType9b4c5aea;
import cn.jpush.android.api.CallBackParams;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.api.TagAliasCallback;
import cn.jpush.android.helper.JCoreHelper;
import cn.jpush.android.helper.Logger;
import cn.jpush.android.l.RecoverType2e4cec2f;
import cn.jpush.android.service.JPushMessageReceiver;
import cn.jpush.android.y.RecoverType2e4d1cfc;
import java.util.Map;
import java.util.Set;

public class ActionHelper {
   private static final String TAG = "ActionHelper";
   private static final Object locker = new Object();
   private static volatile ActionHelper actionHelper;
   private JPushAction jPushAction;
   private RecoverType2e4d1cfc loadedResource;
   int sequence = 10;

   private ActionHelper() {
   }

   public void onEvent(Thread var1, Throwable var2) {
      JCoreManager.onEvent(null, "JPUSH", 93, null, null, var1, var2);
   }

   public static ActionHelper getInstance() {
      if (actionHelper == null) {
         synchronized (locker) {
            if (actionHelper == null) {
               actionHelper = new ActionHelper();
            }
         }
      }

      return actionHelper;
   }

   private synchronized void initLoad(Context var1) {
      if (this.jPushAction == null) {
         if (this.jPushAction == null) {
            Logger.recoverMethod_d_e925fa09("ActionHelper", "load from local");
            this.jPushAction = new RecoverType2e4cec2f();
         }
      }
   }

   public int getResourceID(Context var1, String var2, String var3, String var4) {
      return 0;
   }

   public Class getClassInPlugin(String var1) {
      return null;
   }

   public void doAction(Context var1, String var2, Object var3) {
      Logger.recoverMethod_dd_e925fa09("ActionHelper", "doAction:" + var2);
      JCoreHelper.majorExecutor(var1, "ActionHelper", new ActionHelper.Memberdb37ff4(var1, var2, var3));
   }

   public void doSingleAction(Context var1, String var2, Object var3) {
      Logger.recoverMethod_dd_e925fa09("ActionHelper", "doSingleAction: " + var2);
      JCoreHelper.futureExecutor(var1, "ActionHelper", new ActionHelper.Memberdb37ff4(var1, var2, var3));
   }

   public String getSdkVersion(String var1) {
      return this.jPushAction != null ? this.jPushAction.getSdkVersion(var1) : JPushConstants.SDK_VERSION_NAME;
   }

   public Object beforLogin(Context var1, String var2, int var3, String var4) {
      this.initLoad(var1);
      return null != this.jPushAction ? this.jPushAction.beforLogin(var1, var2, var3, var4) : null;
   }

   public boolean isSupportedCMD(String var1, int var2) {
      return this.jPushAction != null
         ? this.jPushAction.isSupportedCMD(var1, var2)
         : var2 == 3 || var2 == 29 || var2 == 28 || var2 == 27 || var2 == 10 || var2 == 26 || var2 == 25 || var2 == 34 || var2 == 36 || var2 == 37;
   }

   public void onJPushMessageReceive(Context var1, JPushMessageReceiver var2, Intent var3) {
      this.initLoad(var1);
      if (this.jPushAction != null) {
         this.jPushAction.onJPushMessageReceive(var1, var2, var3);
      }
   }

   public void onJPushMessageReceive(Context var1, String var2, String var3, Bundle var4) {
      this.initLoad(var1);
      if (this.jPushAction != null) {
         this.jPushAction.onJPushMessageReceive(var1, var2, var3, var4);
      }
   }

   public void onMessage(Context var1, CustomMessage var2) {
      this.initLoad(var1);
      if (this.jPushAction != null) {
         this.jPushAction.onMessage(var1, var2);
      }
   }

   public void onNotifyMessageOpened(Context var1, NotificationMessage var2) {
      this.initLoad(var1);
      if (this.jPushAction != null) {
         this.jPushAction.onNotifyMessageOpened(var1, var2);
      }
   }

   public void onNotifyMessageArrived(Context var1, NotificationMessage var2) {
      this.initLoad(var1);
      if (this.jPushAction != null) {
         this.jPushAction.onNotifyMessageArrived(var1, var2);
      }
   }

   public void handleNotificationIntent(Context var1, Intent var2) {
      this.initLoad(var1);
      if (this.jPushAction != null) {
         this.jPushAction.handleNotificationIntent(var1, var2);
      }
   }

   public void onMultiAction(Context var1, Intent var2) {
      this.initLoad(var1);
      if (this.jPushAction != null) {
         this.jPushAction.onMultiAction(var1, var2);
      }
   }

   public void onTagAliasResponse(Context var1, long var2, int var4, Intent var5) {
      this.initLoad(var1);
      if (this.jPushAction != null) {
         this.jPushAction.onTagAliasResponse(var1, var2, var4, var5);
      }
   }

   public void onActivityLifeCallback(Activity var1, String var2) {
      if (null != var1 && this.jPushAction != null) {
         this.jPushAction.onActivityLifeCallback(var1, var2);
      }
   }

   public ProxyActivityAction getPopWinActivity(Context var1) {
      this.initLoad(var1);
      return this.jPushAction != null ? this.jPushAction.getPopWinActivity(var1) : null;
   }

   public ProxyActivityAction getPushActivity(Context var1) {
      this.initLoad(var1);
      return this.jPushAction != null ? this.jPushAction.getPushActivity(var1) : null;
   }

   public void setAliasAndTagsWithCallBack(Context var1, String var2, Set var3, TagAliasCallback var4, int var5, int var6) {
      CallBackParams var7 = new CallBackParams(var2, var3, var4, System.currentTimeMillis(), var5, var6);
      JCoreHelper.majorExecutor(var1, "ActionHelper", new ActionHelper.Memberdb37ff5(var1, var2, var3, 0, var7));
   }

   public void setAliasWithCallBack(Context var1, int var2, String var3, int var4, int var5) {
      CallBackParams var6 = new CallBackParams(var2, var3, System.currentTimeMillis(), var4, var5);
      JCoreHelper.majorExecutor(var1, "ActionHelper", new ActionHelper.Memberdb37ff5(var1, var3, null, var2, var6));
   }

   public void setTagsWithCallBack(Context var1, int var2, Set var3, int var4, int var5) {
      CallBackParams var6 = new CallBackParams(var2, var3, System.currentTimeMillis(), var4, var5);
      JCoreHelper.majorExecutor(var1, "ActionHelper", new ActionHelper.Memberdb37ff5(var1, null, var3, var2, var6));
   }

   public void setPropertyWithCallBack(Context var1, int var2, Map var3, int var4, int var5) {
      CallBackParams var6 = new CallBackParams(var2, var3, System.currentTimeMillis(), var4, var5);
      JCoreHelper.majorExecutor(var1, "ActionHelper", new ActionHelper.Memberdb37ff5(var1, null, null, var2, var6));
   }

   public void setPushStatusWithCallBack(Context var1, int var2, int var3) {
      this.sequence++;
      CallBackParams var4 = new CallBackParams(this.sequence, System.currentTimeMillis(), var2, var3);
      JCoreHelper.majorExecutor(var1, "ActionHelper", new ActionHelper.Memberdb37ff5(var1, null, null, this.sequence, var4));
   }

   public void onInAppMessageShow(Context var1, NotificationMessage var2) {
      this.initLoad(var1);
      if (this.jPushAction != null) {
         this.jPushAction.onInAppMessageShow(var1, var2);
      }
   }

   public void onInAppMessageClick(Context var1, NotificationMessage var2) {
      this.initLoad(var1);
      if (this.jPushAction != null) {
         this.jPushAction.onInAppMessageClick(var1, var2);
      }
   }

   public Object getDataByCmd(Context var1, int var2) {
      this.initLoad(var1);
      return this.jPushAction != null ? this.jPushAction.getDataByCmd(var1, var2) : null;
   }

   class Memberdb37ff4 extends RecoverType9b4c5aea {
      private Context recoverField_b;
      private String recoverField_c;
      private Object recoverField_d;

      public Memberdb37ff4(Context var2, String var3, Object var4) {
         this.recoverField_b = var2;
         this.recoverField_c = var3;
         this.recoverField_d = var4;
         this.recoverField_e = "ActionHelper#Action";
      }

      @Override
      public void recoverMethod_a_9b75() {
         try {
            ActionHelper.this.initLoad(this.recoverField_b);
            ActionHelper.this.jPushAction.handleAction(this.recoverField_b, this.recoverField_c, this.recoverField_d);
         } catch (Throwable var2) {
         }
      }
   }

   class Memberdb37ff5 extends RecoverType9b4c5aea {
      private Context recoverField_b;
      private String recoverField_c;
      private int recoverField_d;
      private Set recoverField_f;
      private CallBackParams recoverField_g;

      public Memberdb37ff5(Context var2, String var3, Set var4, int var5, CallBackParams var6) {
         this.recoverField_g = var6;
         this.recoverField_c = var3;
         this.recoverField_d = var5;
         this.recoverField_b = var2;
         this.recoverField_f = var4;
         this.recoverField_e = "ActionHelper#TagAliasAction";
      }

      @Override
      public void recoverMethod_a_9b75() {
         try {
            ActionHelper.this.initLoad(this.recoverField_b);
            ActionHelper.this.jPushAction.setAliasAndTags(this.recoverField_b, this.recoverField_c, this.recoverField_f, this.recoverField_g);
         } catch (Throwable var2) {
         }
      }
   }
}
